# Support Portal API

A RESTful API for a tech support portal that enables customers to create support conversations and operators to manage them.

## Getting Started

### Running Locally

1. **Start the application in dev mode**:
   ```bash
   ./mvnw quarkus:dev
   ```
   - The API will be available at `http://localhost:8080`
   
   **Notes**:
   - A database is automatically started using Quarkus Dev Services
   - The database is recreated on each application start (in-memory for development only)
   - For production use, configure a persistent database in `application.properties`

2. **Test users** (created automatically on startup):
   - **Customer users**:
     - `john:password123`
     - `sarah:password123`
     - `mike:password123`
     - `emma:password123`
   - **Operator users**:
     - `alex:operator123`
     - `lisa:operator123`
     - `david:operator123`

## API Endpoints

### Authentication
- All endpoints require Basic Authentication
- Users can only access their own conversations
- Operators can access all conversations

### Create a Conversation
```
POST /conversations
```
**Request Body**:
```json
{
  "topic": "TECHNICAL|SERVICES|CHAT",
  "initialMessage": "Your message here"
}
```
**Roles**: USER only

---

### List Conversations
```
GET /conversations?operatorId=1,2&topic=TECHNICAL,CHAT&status=WAITING,ACTIVE
```
**Query Parameters**:
- `operatorId`: Filter by operator ID (comma-separated values are OR'ed)
- `topic`: Filter by topic (comma-separated values are OR'ed)
- `status`: Filter by status (comma-separated values are OR'ed)

**Note**: Multiple values within the same parameter are combined with OR logic. For example, `status=WAITING,ACTIVE` will return conversations that are either WAITING OR ACTIVE. Different parameters are combined with AND logic, so `status=WAITING,ACTIVE&topic=TECHNICAL` will return conversations that are (WAITING OR ACTIVE) AND have the TECHNICAL topic.

**Roles**: USER, OPERATOR

---

### Get Conversation Details
```
GET /conversations/{id}
```
**Roles**: USER (own conversations only), OPERATOR

---

### Get Conversation Messages
```
GET /conversations/{id}/messages
```
**Roles**: USER (own conversations only), OPERATOR

---

### Add Message to Conversation
```
POST /conversations/{id}/messages
```
**Request Body**:
```json
{
  "text": "Your message"
}
```
**Notes**:
- Messages can only be added to conversations with status `WAITING` or `ACTIVE`

**Roles**: USER (own conversations only), OPERATOR

---

### Accept Conversation (Operator Only)
```
POST /conversations/{id}/accept
```
**Notes**:
- Only conversations with status `WAITING` can be accepted

**Roles**: OPERATOR only

---

### Close Conversation
```
POST /conversations/{id}/close
```
**Notes**:
- Only conversations with status `WAITING` or `ACTIVE` can be closed
- Both operators and users can close their conversations. Users are limited to closing their own conversations. Operators can close any conversation.

**Roles**: USER (own conversations), OPERATOR

---

## Data Models

### Conversation
```typescript
{
  id: number;
  customer: User;
  operator: User | null;
  closedBy: User | null;
  topic: 'TECHNICAL' | 'SERVICES' | 'CHAT';
  status: 'WAITING' | 'ACTIVE' | 'CLOSED';
  createdAt: string; // ISO-8601 timestamp
  closedAt: string | null; // ISO-8601 timestamp
}
```

### Message
```typescript
{
  id: number;
  conversationId: number;
  author: User;
  text: string;
  createdAt: string; // ISO-8601 timestamp
}
```

### User
```typescript
{
  id: number;
  username: string;  
  password: string;  
  name: string;      
  role: 'USER' | 'OPERATOR';
}
```

## Using the CLI Script

The project includes an improvised script for easier API interaction:

1. Make the script executable:
   ```bash
   chmod +x support-api-cli.sh
   ```

2. Source the script to load the functions:
   ```bash
   source support-api-cli.sh
   ```

3. Use the available functions:
   ```bash
   # Example: Create a conversation
   create_conversation john password123 TECHNICAL "I need help with my account"
   
   # Example: Get all conversations
   get_conversations alice operator123
   
   # Example: Add a message to a conversation
   add_message alice operator123 1 "Hello, how can I help you?"
   ```
