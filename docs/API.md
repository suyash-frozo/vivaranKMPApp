# Vivaran API Documentation

## Overview

The Vivaran API provides endpoints for document analysis, user authentication, and session management. The API is RESTful and uses JSON for data exchange.

## Base URL
```
https://api.vivaran.com/v1
```

## Authentication

All API requests require authentication using OAuth 2.0 or JWT tokens.

```http
Authorization: Bearer <your-token>
Content-Type: application/json
```

## Endpoints

### Authentication

#### POST /auth/login
Login with OAuth credentials.

**Request:**
```json
{
  "provider": "google",
  "token": "oauth_token_here"
}
```

**Response:**
```json
{
  "user": {
    "id": "user_id",
    "email": "user@example.com",
    "name": "User Name"
  },
  "accessToken": "jwt_token",
  "refreshToken": "refresh_token"
}
```

#### POST /auth/logout
Logout and invalidate session.

### Documents

#### POST /documents/upload
Upload a document for analysis.

**Request:**
```http
Content-Type: multipart/form-data

file: <binary_data>
agentId: medical_analysis
sessionId: session_uuid
```

**Response:**
```json
{
  "documentId": "doc_uuid",
  "status": "UPLOADING",
  "uploadUrl": "signed_url_for_upload"
}
```

#### GET /documents/{documentId}/status
Get document processing status.

**Response:**
```json
{
  "documentId": "doc_uuid",
  "status": "PROCESSING",
  "progress": 45,
  "estimatedTime": 120
}
```

#### GET /documents/{documentId}/results
Get analysis results.

**Response:**
```json
{
  "documentId": "doc_uuid",
  "analysis": {
    "summary": "Document summary",
    "insights": ["insight1", "insight2"],
    "recommendations": ["rec1", "rec2"]
  },
  "metadata": {
    "fileType": "PDF",
    "pages": 3,
    "language": "en"
  }
}
```

### Agents

#### GET /agents
Get available AI agents.

**Response:**
```json
{
  "agents": [
    {
      "id": "medical_analysis",
      "name": "Medical Report Analysis",
      "description": "Analyze medical documents",
      "category": "MEDICAL_ANALYSIS"
    }
  ]
}
```

### Sessions

#### GET /sessions
Get user's analysis sessions.

**Response:**
```json
{
  "sessions": [
    {
      "id": "session_uuid",
      "createdAt": "2024-01-01T00:00:00Z",
      "status": "COMPLETED",
      "documentCount": 3,
      "agentId": "medical_analysis"
    }
  ]
}
```

## Error Handling

The API uses standard HTTP status codes:

- `200` - Success
- `400` - Bad Request
- `401` - Unauthorized
- `403` - Forbidden
- `404` - Not Found
- `429` - Rate Limited
- `500` - Internal Server Error

**Error Response Format:**
```json
{
  "error": {
    "code": "INVALID_DOCUMENT",
    "message": "Document format not supported",
    "details": "Only PDF, JPEG, and PNG files are allowed"
  }
}
```

## Rate Limiting

API requests are limited to:
- 100 requests per minute per user
- 10 document uploads per hour per user
- 1GB total upload per day per user

## SDKs

### Kotlin/Android
```kotlin
val api = VivaranApiClient(
    baseUrl = "https://api.vivaran.com/v1",
    apiKey = "your_api_key"
)

val result = api.uploadDocument(file, agentId)
```

### Swift/iOS
```swift
let api = VivaranAPIClient(
    baseURL: "https://api.vivaran.com/v1",
    apiKey: "your_api_key"
)

api.uploadDocument(file, agentId: agentId) { result in
    // Handle result
}
```