# LetMeCook – Backend (Personal Contribution Version)
This is the backend portion of LetMeCook, a team project of 5 members, building a social platform for users to share and interact with cooking recipes.
This version was uploaded and documented by Dũng, who directly developed several core backend modules such as notifications, commenting, reporting, and WebSocket integration. The code here reflects only the parts that I personally implemented.

## Project Overview
- Team size: 5 people  
- Frontend was developed by other teammates in a separate repository  
- Deployment: Not yet deployed  
- My role: Backend developer responsible for multiple core modules listed below

## Modules I Built
### Notification System (Java Spring Boot with WebSocket and JWT)
- Built a real-time notification feature using Spring WebSocket and STOMP.
- Integrated JWT authentication into the WebSocket handshake using a HandshakeInterceptor to secure user connections.
- Used SimpMessagingTemplate to send socket messages to topics like `/topic/notify/{userId}` so clients receive personalized notifications.
- All notifications are stored in a MySQL database, including read/unread status and timestamps.
- Also provided a REST API for users to fetch their notification history.

### Comment System (REST API with MySQL)
- Created a threaded comment and reply system using a parent-child structure.
- Provided REST endpoints to post new comments, reply to existing ones, and retrieve comment trees by post or comment ID.
- Comment data is stored in MySQL with timestamps and user associations.
- Although currently API-based, the system is designed to support real-time integration later.

### Report System (Content Moderation Workflow)
- Implemented a system where users can report content they believe is inappropriate, including fields like content ID, reason, report type, and user ID.
- Built a moderation flow for admins to view, approve, or reject reports using REST APIs.
- Structured the logic to support future enhancements like automatic suspensions for repeated violations.

## Technologies Used
- Java 17 with Spring Boot  
- Spring Security with JWT  
- Spring WebSocket using STOMP  
- MySQL with Spring Data JPA  
- Git for version control

## About This Repository
This repository includes only the backend modules that I developed personally in the LetMeCook team project.  
I do not claim ownership of other components like authentication, user account management, or the frontend.  
This version is shared strictly for educational and portfolio purposes.

## Acknowledgment
Thanks to my teammates who contributed to the frontend, user authentication, and other parts of the system.  
Their support made it possible for me to focus on building and refining the backend modules listed above.
