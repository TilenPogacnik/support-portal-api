package org.tilenp;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.tilenp.dto.ConversationDTO;
import org.tilenp.dto.CreateConversationDTO;
import org.tilenp.entities.Conversation;

import java.util.List;
import java.util.stream.Collectors;

@Path("/conversations")
public class ConversationResource {

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public CreateConversationDTO createConversation(CreateConversationDTO createConversationDTO){
        return createConversationDTO;
    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path("test")
    public Conversation createConversation(){
        return new Conversation();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<ConversationDTO> getAllConversations(){
        List<Conversation> conversations = Conversation.listAll();
        return conversations.stream()
                .map(conv -> new ConversationDTO(
                        conv.id,
                        conv.customer != null ? conv.customer.id : null,
                        conv.customer != null ? conv.customer.name : null,
                        conv.operator != null ? conv.operator.id : null,
                        conv.operator != null ? conv.operator.name : null,
                        conv.topic,
                        conv.status
                ))
                .collect(Collectors.toList());
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Conversation getConversation(Long id){ //TODO: use uuids instead of numeric ids?
        return Conversation.findById(id);
    }
}

