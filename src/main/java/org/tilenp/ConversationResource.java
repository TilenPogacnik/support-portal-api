package org.tilenp;

import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import org.tilenp.exception.BadRequestException;

import org.tilenp.dto.ConversationDTO;
import org.tilenp.dto.CreateConversationDTO;
import org.tilenp.dto.CreateMessageDTO;
import org.tilenp.dto.MessageDTO;
import org.tilenp.entities.Conversation;
import org.tilenp.entities.Message;
import org.tilenp.entities.User;
import org.tilenp.enums.ConversationStatus;
import org.tilenp.enums.UserRole;
import org.tilenp.exception.ErrorMessages;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Path("/conversations")
public class ConversationResource {

    @Inject
    CurrentUser currentUser;

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(UserRole.USER) //Operators are not allowed to create conversations
    public ConversationDTO createConversation(CreateConversationDTO createConversationDTO){
        validateCreateConversationDTO(createConversationDTO);
        
        User user = currentUser.get();
        
        Conversation conversation = new Conversation();
        conversation.customer = user;
        conversation.operator = null;
        conversation.topic = createConversationDTO.topic();
        conversation.status = ConversationStatus.WAITING;
        conversation.persist();

        Message initialMessage = new Message();
        initialMessage.conversation = conversation;
        initialMessage.author = user;
        initialMessage.text = createConversationDTO.initialMessage();
        initialMessage.persist();

        return ConversationDTO.fromEntity(conversation);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({UserRole.OPERATOR, UserRole.USER})
    public List<ConversationDTO> getAllConversations(
            @QueryParam("operatorId") String operatorIdsParam,
            @QueryParam("topic") String topicsParam,
            @QueryParam("status") String statusesParam) {
        
        User user = currentUser.get();
        
        List<Long> includedOperators = operatorIdsParam != null ? Arrays.stream(operatorIdsParam.split(",")).map(Long::parseLong).collect(Collectors.toList()) : null;
        List<String> includedTopics = topicsParam != null ? Arrays.stream(topicsParam.split(",")).map(String::toUpperCase).collect(Collectors.toList()) : null;
        List<String> includedStatuses = statusesParam != null ? Arrays.stream(statusesParam.split(",")).map(String::toUpperCase).collect(Collectors.toList()) : null;
        
        
        List<Conversation> conversations = Conversation.findConversations(user, includedOperators, includedTopics, includedStatuses);

        return conversations.stream()
                .map(ConversationDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    @RolesAllowed({UserRole.OPERATOR, UserRole.USER})
    public ConversationDTO getConversation(@PathParam("id") Long conversationId) {
        User user = currentUser.get();

        Conversation conversation = Conversation.findById(conversationId);
        if (conversation == null) {
            throw new NotFoundException(String.format(ErrorMessages.CONVERSATION_NOT_FOUND, conversationId));
        }

        // Users can only view their own conversations, operators can view all conversations
        if (UserRole.USER.equals(user.userRole) && !conversation.customer.equals(user)) {
            throw new NotAuthorizedException(ErrorMessages.UNAUTHORIZED_VIEW_CONVERSATION);
        }

        return ConversationDTO.fromEntity(conversation);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}/messages")
    @RolesAllowed({UserRole.OPERATOR, UserRole.USER})
    public List<MessageDTO> getConversationMessages(@PathParam("id") Long conversationId) {
        User user = currentUser.get();
        Conversation conversation = Conversation.findById(conversationId);
        if (conversation == null) {
            throw new NotFoundException(String.format(ErrorMessages.CONVERSATION_NOT_FOUND, conversationId));
        }

        // Users can only view their own conversations, operators can view all conversations
        if (UserRole.USER.equals(user.userRole) && !conversation.customer.equals(user)) {
            throw new NotAuthorizedException(ErrorMessages.UNAUTHORIZED_VIEW_CONVERSATION);
        }
        
        return conversation.messages.stream()
                .map(MessageDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}/messages")
    @RolesAllowed({UserRole.OPERATOR, UserRole.USER})
    public MessageDTO addMessageToConversation(@PathParam("id") Long conversationId, CreateMessageDTO createMessageDTO) {
        User user = currentUser.get();
        
        validateCreateMessageDTO(createMessageDTO);
        
        Conversation conversation = Conversation.findById(conversationId);
        if (conversation == null) {
            throw new NotFoundException(String.format(ErrorMessages.CONVERSATION_NOT_FOUND, conversationId));
        }

        // Users can only send messages to their own conversations, operators can send messages to any conversation
        if (UserRole.USER.equals(user.userRole) && !conversation.customer.equals(user)) {
            throw new NotAuthorizedException(ErrorMessages.UNAUTHORIZED_SEND_MESSAGE);
        }
        
        // We do not accept messages to completed conversations
        if (conversation.status == ConversationStatus.CLOSED) {
            throw new BadRequestException(ErrorMessages.CANNOT_SEND_TO_COMPLETED_CONVERSATION);
        }

        Message message = new Message();
        message.conversation = conversation;
        message.text = createMessageDTO.text();
        message.author = user;
        message.persist();
        
        return MessageDTO.fromEntity(message);
    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}/accept")
    @RolesAllowed(UserRole.OPERATOR)
    public ConversationDTO acceptConversation(@PathParam("id") Long conversationId) {
        User user = currentUser.get();
                
        // Find the conversation
        Conversation conversation = Conversation.findById(conversationId);
        if (conversation == null) {
            throw new NotFoundException(String.format(ErrorMessages.CONVERSATION_NOT_FOUND, conversationId));
        }
        
        // Only waiting conversations can be accepted
        if (conversation.status != ConversationStatus.WAITING) {
            throw new BadRequestException(
                String.format(ErrorMessages.ONLY_WAITING_CONVERSATIONS_CAN_BE_ACCEPTED, conversation.status)
            );
        }
        
        conversation.operator = user;
        conversation.status = ConversationStatus.ACTIVE;
        conversation.persist();
        
        return ConversationDTO.fromEntity(conversation);
    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}/close")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({UserRole.OPERATOR, UserRole.USER})
    public ConversationDTO closeConversation(@PathParam("id") Long conversationId) {
        User user = currentUser.get();
        Conversation conversation = Conversation.findById(conversationId);
        if (conversation == null) {
            throw new NotFoundException(String.format(ErrorMessages.CONVERSATION_NOT_FOUND, conversationId));
        }

        // Users can only close their own conversations. Operators can close any conversation.
        if (UserRole.USER.equals(user.userRole) && !conversation.customer.equals(user)) {
            throw new NotAuthorizedException(ErrorMessages.UNAUTHORIZED_CLOSE_CONVERSATION);
        }
        
        // Completed conversations cannot be closed again
        if (conversation.status == ConversationStatus.CLOSED) {
            throw new BadRequestException(ErrorMessages.CONVERSATION_ALREADY_COMPLETED);
        }

        conversation.status = ConversationStatus.CLOSED; //TODO: closing timestamp
        conversation.closedBy = user;
        conversation.persist();
        
        return ConversationDTO.fromEntity(conversation);
    }

    private void validateCreateConversationDTO(CreateConversationDTO dto) {
        if (dto.topic() == null) {
            throw new BadRequestException(ErrorMessages.TOPIC_REQUIRED);
        }
        if (dto.initialMessage() == null || dto.initialMessage().trim().isEmpty()) {
            throw new BadRequestException(ErrorMessages.INITIAL_MESSAGE_REQUIRED);
        }
    }

    private void validateCreateMessageDTO(CreateMessageDTO dto) {
        if (dto.text() == null || dto.text().trim().isEmpty()) {
            throw new BadRequestException(ErrorMessages.MESSAGE_TEXT_REQUIRED);
        }
    }
}

