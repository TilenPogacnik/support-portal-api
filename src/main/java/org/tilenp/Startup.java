package org.tilenp;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import org.tilenp.entities.Conversation;
import org.tilenp.entities.Message;
import org.tilenp.entities.User;
import org.tilenp.enums.ConversationStatus;
import org.tilenp.enums.ConversationTopic;
import org.tilenp.enums.UserRole;

import io.quarkus.elytron.security.common.BcryptUtil;
import io.quarkus.runtime.StartupEvent;


@Singleton
public class Startup {

    /**
     * Initialize the database with sample data.
     * Alternative to using import.sql, to avoid issues with IDs.
     */
    @Transactional
    public void initDb(@Observes StartupEvent evt) {
        User.deleteAll();
        Conversation.deleteAll();
        Message.deleteAll();

        String userPassword = BcryptUtil.bcryptHash("password123");
        String operatorPassword = BcryptUtil.bcryptHash("operator123");
        
        // Create Users - Customers
        User user_johnSmith = new User();
        user_johnSmith.name = "John Smith";
        user_johnSmith.username = "john";
        user_johnSmith.password = userPassword;
        user_johnSmith.userRole = UserRole.USER;
        user_johnSmith.persist();

        User user_sarahJohnson = new User();
        user_sarahJohnson.name = "Sarah Johnson";
        user_sarahJohnson.username = "sarah";
        user_sarahJohnson.password = userPassword;
        user_sarahJohnson.userRole = UserRole.USER;
        user_sarahJohnson.persist();

        User user_mikeWilson = new User();
        user_mikeWilson.name = "Mike Wilson";
        user_mikeWilson.username = "mike";
        user_mikeWilson.password = userPassword;
        user_mikeWilson.userRole = UserRole.USER;
        user_mikeWilson.persist();

        User user_emmaDavis = new User();
        user_emmaDavis.name = "Emma Davis";
        user_emmaDavis.username = "emma";
        user_emmaDavis.password = userPassword;
        user_emmaDavis.userRole = UserRole.USER;
        user_emmaDavis.persist();

        // Create Users - Operators
        User operator_alexThompson = new User();
        operator_alexThompson.name = "Alex Thompson";
        operator_alexThompson.username = "alex";
        operator_alexThompson.password = operatorPassword;
        operator_alexThompson.userRole = UserRole.OPERATOR;
        operator_alexThompson.persist();

        User operator_lisaRodriguez = new User();
        operator_lisaRodriguez.name = "Lisa Rodriguez";
        operator_lisaRodriguez.username = "lisa";
        operator_lisaRodriguez.password = operatorPassword;
        operator_lisaRodriguez.userRole = UserRole.OPERATOR;
        operator_lisaRodriguez.persist();

        User operator_davidChen = new User();
        operator_davidChen.name = "David Chen";
        operator_davidChen.username = "david";
        operator_davidChen.password = operatorPassword;
        operator_davidChen.userRole = UserRole.OPERATOR;
        operator_davidChen.persist();

        // Create Conversations
        // Active conversation with operator assigned
        Conversation conv1 = new Conversation();
        conv1.customer = user_johnSmith;
        conv1.operator = operator_alexThompson;
        conv1.closedBy = null;
        conv1.topic = ConversationTopic.TECHNICAL;
        conv1.status = ConversationStatus.ACTIVE;
        conv1.persist();

        // Pending conversations waiting for operator
        Conversation conv2 = new Conversation();
        conv2.customer = user_sarahJohnson;
        conv2.operator = null;
        conv2.closedBy = null;
        conv2.topic = ConversationTopic.SERVICES;
        conv2.status = ConversationStatus.WAITING;
        conv2.persist();

        Conversation conv3 = new Conversation();
        conv3.customer = user_mikeWilson;
        conv3.operator = null;
        conv3.closedBy = null;
        conv3.topic = ConversationTopic.CHAT;
        conv3.status = ConversationStatus.WAITING;
        conv3.persist();

        // Completed conversation (closed by Lisa Rodriguez)
        Conversation conv4 = new Conversation();
        conv4.customer = user_emmaDavis;
        conv4.operator = operator_lisaRodriguez;
        conv4.closedBy = operator_lisaRodriguez;
        conv4.topic = ConversationTopic.TECHNICAL;
        conv4.status = ConversationStatus.CLOSED;
        conv4.persist();

        // Another active conversation
        Conversation conv5 = new Conversation();
        conv5.customer = user_sarahJohnson;
        conv5.operator = operator_davidChen;
        conv5.closedBy = null;
        conv5.topic = ConversationTopic.SERVICES;
        conv5.status = ConversationStatus.ACTIVE;
        conv5.persist();

        // Create Messages
        // Messages for conversation 1 (John Smith with Alex Thompson - TECHNICAL)
        Message msg1 = new Message();
        msg1.conversation = conv1;
        msg1.author = user_johnSmith;
        msg1.text = "Hi, I'm having trouble connecting to the VPN. It keeps timing out.";
        msg1.persist();

        Message msg2 = new Message();
        msg2.conversation = conv1;
        msg2.author = operator_alexThompson;
        msg2.text = "Hello John! I'll help you with the VPN issue. Can you tell me which operating system you're using?";
        msg2.persist();

        Message msg3 = new Message();
        msg3.conversation = conv1;
        msg3.author = user_johnSmith;
        msg3.text = "I'm using Windows 11. The error message says 'Connection timeout after 30 seconds'.";
        msg3.persist();

        Message msg4 = new Message();
        msg4.conversation = conv1;
        msg4.author = operator_alexThompson;
        msg4.text = "Thanks for the details. Let's try updating your VPN client first. Can you download the latest version from our portal?";
        msg4.persist();

        // Messages for conversation 2 (Sarah Johnson - SERVICES, waiting)
        Message msg5 = new Message();
        msg5.conversation = conv2;
        msg5.author = user_sarahJohnson;
        msg5.text = "Hello, I need help upgrading my service plan. I'm currently on the basic plan but need more features.";
        msg5.persist();

        // Messages for conversation 3 (Mike Wilson - CHAT, waiting)
        Message msg6 = new Message();
        msg6.conversation = conv3;
        msg6.author = user_mikeWilson;
        msg6.text = "Hi there! I just wanted to say thanks for the great service. Everything has been working perfectly!";
        msg6.persist();

        // Messages for conversation 4 (Emma Davis with Lisa Rodriguez - TECHNICAL, completed)
        Message msg7 = new Message();
        msg7.conversation = conv4;
        msg7.author = user_emmaDavis;
        msg7.text = "My email client isn't syncing properly with the server. I'm not receiving new emails.";
        msg7.persist();

        Message msg8 = new Message();
        msg8.conversation = conv4;
        msg8.author = operator_lisaRodriguez;
        msg8.text = "Hi Emma! I can help you with the email sync issue. Let's start by checking your server settings.";
        msg8.persist();

        Message msg9 = new Message();
        msg9.conversation = conv4;
        msg9.author = user_emmaDavis;
        msg9.text = "The incoming server is set to mail.company.com and port 993. Is that correct?";
        msg9.persist();

        Message msg10 = new Message();
        msg10.conversation = conv4;
        msg10.author = operator_lisaRodriguez;
        msg10.text = "Yes, that's correct. Now let's check if SSL is enabled. It should be turned on for secure connection.";
        msg10.persist();

        Message msg11 = new Message();
        msg11.conversation = conv4;
        msg11.author = user_emmaDavis;
        msg11.text = "Found it! SSL was disabled. I've enabled it now and the emails are starting to sync. Thank you so much!";
        msg11.persist();

        Message msg12 = new Message();
        msg12.conversation = conv4;
        msg12.author = operator_lisaRodriguez;
        msg12.text = "Perfect! Glad we could resolve that quickly. Your email should be fully synced now. Is there anything else I can help you with?";
        msg12.persist();

        Message msg13 = new Message();
        msg13.conversation = conv4;
        msg13.author = user_emmaDavis;
        msg13.text = "No, that's all. Everything is working great now. Thanks again for your help!";
        msg13.persist();

        // Messages for conversation 5 (Sarah Johnson with David Chen - SERVICES)
        Message msg14 = new Message();
        msg14.conversation = conv5;
        msg14.author = user_sarahJohnson;
        msg14.text = "Hi, I need to increase my storage quota. I'm running out of space on my current plan.";
        msg14.persist();

        Message msg15 = new Message();
        msg15.conversation = conv5;
        msg15.author = operator_davidChen;
        msg15.text = "Hi Sarah! I can help you with that. How much additional storage do you need?";
        msg15.persist();

        Message msg16 = new Message();
        msg16.conversation = conv5;
        msg16.author = user_sarahJohnson;
        msg16.text = "I think 500GB more would be sufficient for now.";
        msg16.persist();
    }
}
