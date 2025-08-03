package org.tilenp;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.tilenp.dto.CloseConversationDTO;
import org.tilenp.dto.ConversationDTO;
import org.tilenp.dto.CreateConversationDTO;
import org.tilenp.dto.CreateMessageDTO;
import org.tilenp.dto.MessageDTO;
import org.tilenp.dto.TakeoverConversationDTO;
import org.tilenp.entities.Conversation;
import org.tilenp.entities.Message;
import org.tilenp.entities.User;
import org.tilenp.enums.ConversationStatus;
import org.tilenp.enums.UserRole;

import java.util.List;
import java.util.stream.Collectors;

@Path("/conversations")
public class ConversationResource {

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ConversationDTO createConversation(CreateConversationDTO createConversationDTO){
        // Validate DTO
        validateCreateConversationDTO(createConversationDTO);
        
        // Find the customer //TODO: we probably dont need customer to already exist. this is a stretch goal
        User customer = User.findById(createConversationDTO.customerId);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found with id: " + createConversationDTO.customerId);
        }

        // Create new conversation
        Conversation conversation = new Conversation();
        conversation.customer = customer;
        conversation.operator = null; // No operator assigned initially
        conversation.topic = createConversationDTO.topic;
        conversation.status = ConversationStatus.WAITING; // New conversations are waiting for operator
        conversation.persist();

        // Create initial message (required)
        Message initialMessage = new Message();
        initialMessage.conversation = conversation;
        initialMessage.author = customer;
        initialMessage.text = createConversationDTO.initialMessage;
        initialMessage.persist();

        return toConversationDTO(conversation);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<ConversationDTO> getAllConversations(){
        List<Conversation> conversations = Conversation.listAll();
        return conversations.stream()
                .map(this::toConversationDTO)
                .collect(Collectors.toList());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public ConversationDTO getConversation(@PathParam("id") Long id){ //TODO: use uuids instead of numeric ids?
        Conversation conv = Conversation.findById(id);
        if (conv == null) {
            return null; // TODO: Consider throwing a proper 404 exception
        }
        return toConversationDTO(conv);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}/messages")
    public List<MessageDTO> getConversationMessages(@PathParam("id") Long id){
        Conversation conv = Conversation.findById(id);
        if (conv == null) {
            return null; // TODO: Consider throwing a proper 404 exception
        }
        
        List<Message> messages = Message.findByConversationId(id);
        return messages.stream()
                .map(this::toMessageDTO)
                .collect(Collectors.toList());
    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}/messages")
    public MessageDTO addMessageToConversation(@PathParam("id") Long conversationId, CreateMessageDTO createMessageDTO){
        // Validate DTO
        validateCreateMessageDTO(createMessageDTO);
        
        // Find the conversation
        Conversation conversation = Conversation.findById(conversationId);
        if (conversation == null) {
            throw new IllegalArgumentException("Conversation not found with id: " + conversationId);
        }
        
        // Check if conversation is closed
        if (conversation.status == ConversationStatus.COMPLETED) {
            throw new IllegalArgumentException("Cannot send messages to a completed conversation");
        }
        
        // Find the author
        User author = User.findById(createMessageDTO.authorId);
        if (author == null) {
            throw new IllegalArgumentException("User not found with id: " + createMessageDTO.authorId);
        }
        
        // Create and persist the message
        Message message = new Message();
        message.conversation = conversation;
        message.author = author;
        message.text = createMessageDTO.text;
        message.persist();
        
        return toMessageDTO(message);
    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}/takeover")
    public ConversationDTO takeoverConversation(@PathParam("id") Long conversationId, TakeoverConversationDTO takeoverDTO){
        // Validate DTO
        validateTakeoverConversationDTO(takeoverDTO);
        
        // Find the conversation
        Conversation conversation = Conversation.findById(conversationId);
        if (conversation == null) {
            throw new IllegalArgumentException("Conversation not found with id: " + conversationId);
        }
        
        // Check if conversation is in WAITING status
        if (conversation.status != ConversationStatus.WAITING) {
            throw new IllegalArgumentException("Only waiting conversations can be taken over. Current status: " + conversation.status);
        }
        
        // Find the operator
        User operator = User.findById(takeoverDTO.operatorId);
        if (operator == null) {
            throw new IllegalArgumentException("Operator not found with id: " + takeoverDTO.operatorId);
        }
        
        // Verify user is an operator
        if (operator.userRole != UserRole.OPERATOR) {
            throw new IllegalArgumentException("User is not an operator");
        }
        
        // Assign operator and change status to TAKEN
        conversation.operator = operator;
        conversation.status = ConversationStatus.TAKEN;
        conversation.persist();
        
        return toConversationDTO(conversation);
    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}/close")
    @Produces(MediaType.APPLICATION_JSON)
    public ConversationDTO closeConversation(@PathParam("id") Long conversationId, CloseConversationDTO closeDTO){
        // Validate DTO
        validateCloseConversationDTO(closeDTO);
        
        // Find the conversation
        Conversation conversation = Conversation.findById(conversationId);
        if (conversation == null) {
            throw new IllegalArgumentException("Conversation not found with id: " + conversationId);
        }
        
        // Check if conversation is already completed
        if (conversation.status == ConversationStatus.COMPLETED) {
            throw new IllegalArgumentException("Conversation is already completed");
        }
        
        // Find the user who is closing the conversation
        User closingUser = User.findById(closeDTO.userId);
        if (closingUser == null) {
            throw new IllegalArgumentException("User not found with id: " + closeDTO.userId);
        }
        
        // Close the conversation and record who closed it
        conversation.status = ConversationStatus.COMPLETED;
        conversation.closedBy = closingUser;
        conversation.persist();
        
        return toConversationDTO(conversation);
    }

    private void validateCreateConversationDTO(CreateConversationDTO dto) {
        if (dto.customerId == null) {
            throw new IllegalArgumentException("Customer ID is required");
        }
        if (dto.topic == null) {
            throw new IllegalArgumentException("Topic is required");
        }
        if (dto.initialMessage == null || dto.initialMessage.trim().isEmpty()) {
            throw new IllegalArgumentException("Initial message is required and cannot be empty");
        }
    }

    private void validateCreateMessageDTO(CreateMessageDTO dto) {
        if (dto.authorId == null) {
            throw new IllegalArgumentException("Author ID is required");
        }
        if (dto.text == null || dto.text.trim().isEmpty()) {
            throw new IllegalArgumentException("Message text is required and cannot be empty");
        }
    }

    private void validateTakeoverConversationDTO(TakeoverConversationDTO dto) {
        if (dto.operatorId == null) {
            throw new IllegalArgumentException("Operator ID is required");
        }
    }

    private void validateCloseConversationDTO(CloseConversationDTO dto) {
        if (dto.userId == null) {
            throw new IllegalArgumentException("User ID is required");
        }
    }

    private MessageDTO toMessageDTO(Message message) {
        return new MessageDTO(
                message.id,
                message.conversation.id,
                message.author.id,
                message.author.name,
                message.text
        );
    }

    private ConversationDTO toConversationDTO(Conversation conv) {
        return new ConversationDTO(
                conv.id,
                conv.customer != null ? conv.customer.id : null,
                conv.customer != null ? conv.customer.name : null,
                conv.operator != null ? conv.operator.id : null,
                conv.operator != null ? conv.operator.name : null,
                conv.topic,
                conv.status
        );
    }
}

