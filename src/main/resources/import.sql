INSERT INTO Greeting(id, name)
VALUES (nextval('Greeting_SEQ'), 'Alice');
INSERT INTO Greeting(id, name)
VALUES (nextval('Greeting_SEQ'), 'Bob');

-- Sample Users
-- Customers
INSERT INTO users(id, name, userRole, password)
VALUES (1, 'John Smith', 'USER', 'password123');
INSERT INTO users(id, name, userRole, password)
VALUES (2, 'Sarah Johnson', 'USER', 'password123');
INSERT INTO users(id, name, userRole, password)
VALUES (3, 'Mike Wilson', 'USER', 'password123');
INSERT INTO users(id, name, userRole, password)
VALUES (4, 'Emma Davis', 'USER', 'password123');

-- Operators
INSERT INTO users(id, name, userRole, password)
VALUES (5, 'Alex Thompson', 'OPERATOR', 'operator123');
INSERT INTO users(id, name, userRole, password)
VALUES (6, 'Lisa Rodriguez', 'OPERATOR', 'operator123');
INSERT INTO users(id, name, userRole, password)
VALUES (7, 'David Chen', 'OPERATOR', 'operator123');

-- Sample Conversations
-- Active conversation with operator assigned
INSERT INTO conversations(id, customer_id, operator_id, topic, status)
VALUES (1, 1, 5, 'TECHNICAL', 'TAKEN');

-- Pending conversations waiting for operator
INSERT INTO conversations(id, customer_id, operator_id, topic, status)
VALUES (2, 2, NULL, 'SERVICES', 'WAITING');
INSERT INTO conversations(id, customer_id, operator_id, topic, status)
VALUES (3, 3, NULL, 'CHAT', 'WAITING');

-- Completed conversation
INSERT INTO conversations(id, customer_id, operator_id, topic, status)
VALUES (4, 4, 6, 'TECHNICAL', 'COMPLETED');

-- Another active conversation
INSERT INTO conversations(id, customer_id, operator_id, topic, status)
VALUES (5, 2, 7, 'SERVICES', 'TAKEN');

-- Sample Messages
-- Messages for conversation 1 (John Smith with Alex Thompson - TECHNICAL)
INSERT INTO messages(id, conversation_id, author_id, text)
VALUES (1, 1, 1, 'Hi, I''m having trouble connecting to the VPN. It keeps timing out.');
INSERT INTO messages(id, conversation_id, author_id, text)
VALUES (2, 1, 5, 'Hello John! I''ll help you with the VPN issue. Can you tell me which operating system you''re using?');
INSERT INTO messages(id, conversation_id, author_id, text)
VALUES (3, 1, 1, 'I''m using Windows 11, and this started happening yesterday.');
INSERT INTO messages(id, conversation_id, author_id, text)
VALUES (4, 1, 5, 'Thanks for the info. Let''s try restarting the VPN service. Can you open Command Prompt as administrator?');

-- Messages for conversation 2 (Sarah Johnson - SERVICES, waiting)
INSERT INTO messages(id, conversation_id, author_id, text)
VALUES (5, 2, 2, 'Hello, I need help with upgrading my service plan. What options are available?');

-- Messages for conversation 3 (Mike Wilson - CHAT, waiting)
INSERT INTO messages(id, conversation_id, author_id, text)
VALUES (6, 3, 3, 'Hi there! I just wanted to say thank you for the excellent service last week.');

-- Messages for conversation 4 (Emma Davis with Lisa Rodriguez - TECHNICAL, completed)
INSERT INTO messages(id, conversation_id, author_id, text)
VALUES (7, 4, 4, 'My email client won''t sync with the server. Can you help?');
INSERT INTO messages(id, conversation_id, author_id, text)
VALUES (8, 4, 6, 'Hi Emma! I''d be happy to help. Let''s check your email settings first.');
INSERT INTO messages(id, conversation_id, author_id, text)
VALUES (9, 4, 4, 'Sure, what should I check?');
INSERT INTO messages(id, conversation_id, author_id, text)
VALUES (10, 4, 6, 'Please verify your incoming server settings: IMAP server should be mail.company.com, port 993, SSL enabled.');
INSERT INTO messages(id, conversation_id, author_id, text)
VALUES (11, 4, 4, 'Perfect! That fixed it. Thank you so much!');
INSERT INTO messages(id, conversation_id, author_id, text)
VALUES (12, 4, 6, 'You''re welcome! Is there anything else I can help you with today?');
INSERT INTO messages(id, conversation_id, author_id, text)
VALUES (13, 4, 4, 'No, that''s all. Thanks again!');

-- Messages for conversation 5 (Sarah Johnson with David Chen - SERVICES)
INSERT INTO messages(id, conversation_id, author_id, text)
VALUES (14, 5, 2, 'I''d like to add more storage to my current plan.');
INSERT INTO messages(id, conversation_id, author_id, text)
VALUES (15, 5, 7, 'Hi Sarah! I can help you with that. How much additional storage do you need?');
INSERT INTO messages(id, conversation_id, author_id, text)
VALUES (16, 5, 2, 'I think 500GB more would be sufficient for now.');

-- Note: Sequence values are automatically managed by Hibernate (we are using H2 database for dev purposes)