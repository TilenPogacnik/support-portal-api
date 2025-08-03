package org.tilenp;

import jakarta.annotation.security.RolesAllowed;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.tilenp.dto.ConversationDTO;
import org.tilenp.dto.CreateConversationDTO;
import org.tilenp.dto.CreateMessageDTO;
import org.tilenp.dto.MessageDTO;
import org.tilenp.entities.Conversation;
import org.tilenp.entities.Message;
import org.tilenp.entities.User;
import org.tilenp.enums.ConversationStatus;
import org.tilenp.enums.UserRole;

import java.util.List;
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

        return ConversationDTO.fromEntity(conversation); //TODO: include messages
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed(UserRole.OPERATOR) //TODO: permit all, if user return only their own conversations
    public List<ConversationDTO> getAllConversations(){ //TODO: add filters for status, operator, topic
        List<Conversation> conversations = Conversation.listAll();
        return conversations.stream()
                .map(ConversationDTO::fromEntity)
                .collect(Collectors.toList()); //TODO dont include messages
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    @PermitAll
    public ConversationDTO getConversation(@PathParam("id") Long conversationId){
        User user = currentUser.get();

        Conversation conversation = Conversation.findById(conversationId);
        if (conversation == null) {
            return null; // TODO: Consider throwing a proper 404 exception - test with invalid id, exception is thrown earlier
        }

        //Users can only view their own conversations, operators can view all conversations
        if (UserRole.USER.equals(user.userRole) && !conversation.customer.equals(user)) {
            throw new NotAuthorizedException("User is not authorized to view this conversation");
        }

        return ConversationDTO.fromEntity(conversation); //TODO: include messages
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}/messages")
    @PermitAll
    public List<MessageDTO> getConversationMessages(@PathParam("id") Long conversationId){
        User user = currentUser.get();
        Conversation conversation = Conversation.findById(conversationId);
        if (conversation == null) {
            return null; // TODO: Consider throwing a proper 404 exception - test with invalid id, exception is thrown earlier
        }

        //Users can only view their own conversations, operators can view all conversations
        if (UserRole.USER.equals(user.userRole) && !conversation.customer.equals(user)) {
            throw new NotAuthorizedException("User is not authorized to view this conversation");
        }
        
        return conversation.messages.stream()
                .map(MessageDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}/messages") //TODO: cleanup order of annotations
    @PermitAll
    public MessageDTO addMessageToConversation(@PathParam("id") Long conversationId, CreateMessageDTO createMessageDTO){
        User user = currentUser.get();
        
        validateCreateMessageDTO(createMessageDTO);
        
        Conversation conversation = Conversation.findById(conversationId);
        if (conversation == null) {
            throw new IllegalArgumentException("Conversation not found with id: " + conversationId);
        }

        //Users can only send messages to their own conversations, operators can send messages to any conversation
        if (UserRole.USER.equals(user.userRole) && !conversation.customer.equals(user)) {
            throw new NotAuthorizedException("User is not authorized to send messages to this conversation");
        }
        
        //We do not accept messages to completed conversations
        if (conversation.status == ConversationStatus.CLOSED) {
            throw new IllegalArgumentException("Cannot send messages to a completed conversation");
        }

        Message message = new Message();
        message.conversation = conversation;
        message.text = createMessageDTO.text();
        message.persist();
        
        return MessageDTO.fromEntity(message);
    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}/accept")
    @RolesAllowed(UserRole.OPERATOR)
    public ConversationDTO acceptConversation(@PathParam("id") Long conversationId){
        User user = currentUser.get();
                
        // Find the conversation
        Conversation conversation = Conversation.findById(conversationId);
        if (conversation == null) {
            throw new IllegalArgumentException("Conversation not found with id: " + conversationId);
        }
        
        //Only waiting conversations can be accepted
        if (conversation.status != ConversationStatus.WAITING) {
            throw new IllegalArgumentException("Only waiting conversations can be accepted. Current status: " + conversation.status);
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
    @PermitAll
    public ConversationDTO closeConversation(@PathParam("id") Long conversationId){
        User user = currentUser.get();
        Conversation conversation = Conversation.findById(conversationId);
        if (conversation == null) {
            throw new IllegalArgumentException("Conversation not found with id: " + conversationId);
        }

        //Users can only close their own conversations. Operators can close any conversation.
        if (UserRole.USER.equals(user.userRole) && !conversation.customer.equals(user)) {
            throw new NotAuthorizedException("User is not authorized to close this conversation");
        }
        
        // Completed conversations cannot be closed again
        if (conversation.status == ConversationStatus.CLOSED) {
            throw new IllegalArgumentException("Conversation is already completed");
        }

        conversation.status = ConversationStatus.CLOSED; //TODO: closing timestamp
        conversation.closedBy = user;
        conversation.persist();
        
        return ConversationDTO.fromEntity(conversation);
    }

    private void validateCreateConversationDTO(CreateConversationDTO dto) {
        if (dto.topic() == null) {
            throw new IllegalArgumentException("Topic is required");
        }
        if (dto.initialMessage() == null || dto.initialMessage().trim().isEmpty()) {
            throw new IllegalArgumentException("Initial message is required and cannot be empty");
        }
    }

    private void validateCreateMessageDTO(CreateMessageDTO dto) {
        if (dto.text() == null || dto.text().trim().isEmpty()) {
            throw new IllegalArgumentException("Message text is required and cannot be empty");
        }
    }
}

