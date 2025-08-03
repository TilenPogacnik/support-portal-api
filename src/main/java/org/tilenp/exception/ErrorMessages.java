package org.tilenp.exception;

public final class ErrorMessages {
    // Conversation related errors
    public static final String CONVERSATION_NOT_FOUND = "Conversation not found with id: %s";
    public static final String CONVERSATION_ALREADY_COMPLETED = "Conversation is already completed";
    public static final String CANNOT_SEND_TO_COMPLETED_CONVERSATION = "Cannot send messages to a completed conversation";
    public static final String ONLY_WAITING_CONVERSATIONS_CAN_BE_ACCEPTED = "Only waiting conversations can be accepted. Current status: %s";
    
    // Authorization errors
    public static final String AUTHENTICATION_REQUIRED = "Authentication required";
    public static final String USER_NOT_FOUND = "User not found: %s";
    public static final String UNAUTHORIZED_VIEW_CONVERSATION = "User is not authorized to view this conversation";
    public static final String UNAUTHORIZED_SEND_MESSAGE = "User is not authorized to send messages to this conversation";
    public static final String UNAUTHORIZED_CLOSE_CONVERSATION = "User is not authorized to close this conversation";
    
    // Validation errors
    public static final String TOPIC_REQUIRED = "Topic is required!";
    public static final String INITIAL_MESSAGE_REQUIRED = "Initial message is required!";
    public static final String MESSAGE_TEXT_REQUIRED = "Message text is required!";
    
    private ErrorMessages() {
        // Private constructor to prevent instantiation
    }
}
