package org.tilenp;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.tilenp.dto.CreateConversationDTO;
import org.tilenp.entities.Conversation;

import java.util.List;

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
    public List<Conversation> getAllConversations(){
        return Conversation.listAll();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Conversation getConversation(Long id){ //TODO: use uuids instead of numeric ids?
        return Conversation.findById(id);
    }
}

