# training-assignment
Personal Notes Backend

A secure and extensible backend service for managing personal notes. Built with Spring Boot and enhanced with JWT authentication, AES encryption, file upload/download, and profile management.

🚀 Features

CRUD APIs for personal notes

JWT Authentication & Spring Security for secure access

AES Encryption for sensitive note storage

Profile Image Upload with file validation

PDF Download for exporting notes

Centralized error handling & input validation

Configurable storage paths via properties

Extensible architecture for DB & cloud integrations

🛠️ Tech Stack

Java 17+

Spring Boot – REST APIs

Spring Security + JWT – Authentication/Authorization

Jakarta Validation – Input validation

AES Encryption – Note encryption/decryption

Maven/Gradle – Build tool


🔑 API Endpoints
Method	Endpoint	Description	Secured
POST	/api/auth/login	User login → returns JWT token	❌
POST	/api/auth/register	Register a new user	❌
POST	/api/notes	Create a note (AES encrypted)	✅
GET	/api/notes/{id}	Get note by ID (decrypt on read)	✅
GET	/api/notes	List all notes	✅
PUT	/api/notes/{id}	Update a note	✅
DELETE	/api/notes/{id}	Delete a note	✅
POST	/api/users/{id}/upload	Upload profile image	✅
GET	/api/users/{id}/profile-pic	Download profile image	✅
GET	/api/notes/{id}/export/pdf	Download note as PDF	✅
🔐 Authentication Flow (JWT + Spring Security)

User registers/login → receives JWT token.

Token is passed in Authorization Header (Bearer <token>) for all secured APIs.

Spring Security validates token before executing controller logic.

Example:

Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

🔒 AES Encryption in Notes

Notes are encrypted with AES (Advanced Encryption Standard) before saving.

Decryption happens transparently when fetching notes.

Keeps sensitive notes safe even if the DB/filesystem is compromised.

📂 File Handling
Profile Image Upload

Accepts .jpg, .jpeg, .png

Validates file size & type

Stored in configured path

PDF Export

Any note can be exported as a downloadable PDF with formatted content.

▶️ Running the Application
Gradle
./gradlew bootRun


