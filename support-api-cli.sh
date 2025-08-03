#!/bin/bash

# Support Portal API CLI
# Usage: source support-api-cli.sh
# Then call the functions below with required parameters

# Base URL of the API (default: http://localhost:8080)
BASE_URL=${BASE_URL:-http://localhost:8080}

# Helper function to make authenticated requests
# Usage: _make_request USERNAME PASSWORD METHOD ENDPOINT [BODY]
# Example: _make_request alice pass123 GET /conversations
_make_request() {
    local username=$1
    local password=$2
    local method=$3
    local endpoint=$4
    local body=${5:-}
    
    # Encode credentials for Basic Auth
    local credentials="${username}:${password}"
    local auth_header="Basic $(echo -n "$credentials" | base64)"
    
    local curl_cmd=(
        curl -s -w "\n%{http_code}" 
        -H "accept: application/json"
        -H "Authorization: $auth_header"
        -X "$method"
        "${BASE_URL}${endpoint}"
    )
    
    if [ -n "$body" ]; then
        curl_cmd+=(
            -H "Content-Type: application/json"
            -d "$body"
        )
    fi
    
    # Execute the command and capture both response and status code
    local response
    response=$("${curl_cmd[@]}")
    
    # Extract status code (last 3 characters of response)
    local status_code=${response: -3}
    
    # Extract response body (all except last 3 characters)
    local response_body=${response:0: -3}
    
    echo "Status: $status_code"
    if [ -n "$response_body" ]; then
        echo "Response:"
        echo "$response_body" | jq . 2>/dev/null || echo "$response_body"
    fi
    echo
}

# Create a new conversation
# Usage: create_conversation USERNAME PASSWORD TOPIC "INITIAL_MESSAGE"
create_conversation() {
    local username=$1
    local password=$2
    local topic=$3
    local message=$4
    
    local body=$(jq -n \
        --arg topic "$topic" \
        --arg message "$message" \
        '{topic: $topic, initialMessage: $message}')
    
    _make_request "$username" "$password" POST "/conversations" "$body"
}

# Get all conversations with optional filters
# Usage: get_conversations USERNAME PASSWORD [OPERATOR_IDS] [TOPICS] [STATUSES]
# Example: get_conversations alice pass123 "1,2,3" "TECHNICAL,CHAT" "WAITING,ACTIVE"
get_conversations() {
    local username=$1
    local password=$2
    local operator_ids=${3:-}
    local topics=${4:-}
    local statuses=${5:-}
    
    local endpoint="/conversations"
    local params=()
    
    # Add operator IDs if provided
    if [ -n "$operator_ids" ]; then
        params+=("operatorId=$operator_ids")
    fi
    
    # Add topics if provided
    if [ -n "$topics" ]; then
        params+=("topic=$topics")
    fi
    
    # Add statuses if provided
    if [ -n "$statuses" ]; then
        params+=("status=$statuses")
    fi
    
    # Build the full URL with parameters
    if [ ${#params[@]} -gt 0 ]; then
        endpoint+="?$(IFS='&'; echo "${params[*]}")"
    fi
    
    _make_request "$username" "$password" GET "$endpoint"
}

# Get a specific conversation
# Usage: get_conversation USERNAME PASSWORD CONVERSATION_ID
get_conversation() {
    local username=$1
    local password=$2
    local conversation_id=$3
    
    _make_request "$username" "$password" GET "/conversations/$conversation_id"
}

# Get messages in a conversation
# Usage: get_conversation_messages USERNAME PASSWORD CONVERSATION_ID
get_conversation_messages() {
    local username=$1
    local password=$2
    local conversation_id=$3
    
    _make_request "$username" "$password" GET "/conversations/$conversation_id/messages"
}

# Add a message to a conversation
# Usage: add_message USERNAME PASSWORD CONVERSATION_ID "MESSAGE_TEXT"
add_message() {
    local username=$1
    local password=$2
    local conversation_id=$3
    local message_text=$4
    
    local body=$(jq -n \
        --arg text "$message_text" \
        '{text: $text}')
    
    _make_request "$username" "$password" POST "/conversations/$conversation_id/messages" "$body"
}

# Accept a conversation (operator only)
# Usage: accept_conversation USERNAME PASSWORD CONVERSATION_ID
accept_conversation() {
    local username=$1
    local password=$2
    local conversation_id=$3
    
    _make_request "$username" "$password" POST "/conversations/$conversation_id/accept"
}

# Close a conversation
# Usage: close_conversation USERNAME PASSWORD CONVERSATION_ID
close_conversation() {
    local username=$1
    local password=$2
    local conversation_id=$3
    
    _make_request "$username" "$password" POST "/conversations/$conversation_id/close"
}

echo "Support Portal API CLI loaded. Available functions:"
echo "- create_conversation USERNAME PASSWORD TOPIC \"INITIAL_MESSAGE\""
echo "- get_conversations USERNAME PASSWORD [OPERATOR_IDS] [TOPICS] [STATUSES]"
echo "- get_conversation USERNAME PASSWORD CONVERSATION_ID"
echo "- get_conversation_messages USERNAME PASSWORD CONVERSATION_ID"
echo "- add_message USERNAME PASSWORD CONVERSATION_ID \"MESSAGE_TEXT\""
echo "- accept_conversation USERNAME PASSWORD CONVERSATION_ID"
echo "- close_conversation USERNAME PASSWORD CONVERSATION_ID"
