package org.tilenp;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.tilenp.entities.Greeting;

import java.util.List;
import java.util.stream.Collectors;

@Path("/hello")
public class GreetingResource { //TODO delete class

    @Inject
    GreetingService service;

    @GET
    @Transactional
    @Produces(MediaType.TEXT_PLAIN)
    public String hello(@QueryParam("name") String name) {
        Greeting greeting = new Greeting();
        greeting.name = name;
        greeting.persist();
        return "Hello persisted " + name;
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/greeting/{name}")
    public String greeting(String name) {
        return service.greeting(name);
    }

    @GET
    @Path("names")
    @Produces(MediaType.TEXT_PLAIN)
    public String names() {
        List<Greeting> greetings = Greeting.listAll();
        String names = greetings.stream().map(g-> g.name)
                .collect(Collectors.joining (", "));
        return "I've said hello to " + names;
    }
}
