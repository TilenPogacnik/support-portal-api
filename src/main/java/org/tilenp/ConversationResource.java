package org.tilenp;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.tilenp.dto.ConversationDTO;
import org.tilenp.dto.CreateConversationDTO;
import org.tilenp.entities.Conversation;
import org.tilenp.entities.Message;
import org.tilenp.entities.User;
import org.tilenp.enums.ConversationStatus;

import java.util.List;
import java.util.stream.Collectors;

@Path("/conversations")
public class ConversationResource {

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ConversationDTO createConversation(CreateConversationDTO createConversationDTO){
        // Find the customer //TODO: we probably dont need customer to already exist. this is a stretch goal
        User customer = User.findById(createConversationDTO.customerId);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found with id: " + createConversationDTO.customerId);
        }

        // Validate DTO
        validateCreateConversationDTO(createConversationDTO);

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

