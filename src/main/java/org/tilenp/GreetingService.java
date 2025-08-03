package org.tilenp;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class GreetingService { //TODO delete class

    public String greeting(String name) {
        return "hello " + name;
    }
}
