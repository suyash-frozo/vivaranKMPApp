package com.vivaran.shared
//
//import com.vivaran.shared.data.network.AnalysisRequest
//import com.vivaran.shared.data.network.VivaranApiServiceImpl
//import com.vivaran.shared.utils.Base64Utils
//import io.ktor.client.*
//import io.ktor.client.engine.mock.*
//import io.ktor.client.plugins.contentnegotiation.*
//import io.ktor.http.*
//import io.ktor.serialization.kotlinx.json.*
//import kotlinx.coroutines.test.runTest
//import kotlinx.serialization.json.Json
//import kotlin.test.*
//
//class ApiIntegrationTest {
//
//    private fun createMockHttpClient(responseContent: String, statusCode: HttpStatusCode = HttpStatusCode.OK): HttpClient {
//        return HttpClient(MockEngine) {
//            install(ContentNegotiation) {
//                json(Json {
//                    prettyPrint = true
//                    isLenient = true
//                    ignoreUnknownKeys = true
//                })
//            }
//            engine {
//                addHandler { request ->
//                    respond(
//                        content = responseContent,
//                        status = statusCode,
//                        headers = headersOf(HttpHeaders.ContentType, "application/json")
//                    )
//                }
//            }
//        }
//    }
//
//    @Test
//    fun testAnalysisRequest_Success() = runTest {
//        val mockResponse = """
//            {
//                "success": true,
//                "verdict": "Bill appears accurate with minor discrepancies",
//                "total_overcharge": 50.0,
//                "confidence_score": 0.92,
//                "bill_amount": 1500.0,
//                "processing_time": 2.3
//            }
//        """.trimIndent()
//
//        val mockClient = createMockHttpClient(mockResponse)
//        val apiService = VivaranApiServiceImpl(mockClient)
//
//        val samplePdfContent = ByteArray(100) { it.toByte() }
//        val base64Content = Base64Utils.encodeToString(samplePdfContent)
//
//        val request = AnalysisRequest(
//            file_content = base64Content,
//            doc_id = "test-doc-123",
//            user_id = "test-user-456",
//            language = "english",
//            state_code = "DL",
//            insurance_type = "cghs",
//            file_format = "pdf"
//        )
//
//        val response = apiService.analyzeDocument(request)
//
//        assertTrue(response.success)
//        assertEquals("Bill appears accurate with minor discrepancies", response.verdict)
//        assertEquals(50.0, response.total_overcharge)
//        assertEquals(0.92, response.confidence_score)
//        assertEquals(1500.0, response.bill_amount)
//        assertEquals(2.3, response.processing_time)
//    }
//
//    @Test
//    fun testAnalysisRequest_Error() = runTest {
//        val mockResponse = """
//            {
//                "success": false,
//                "error": "Invalid file format or corrupted document"
//            }
//        """.trimIndent()
//
//        val mockClient = createMockHttpClient(mockResponse)
//        val apiService = VivaranApiServiceImpl(mockClient)
//
//        val request = AnalysisRequest(
//            file_content = "invalid-base64",
//            doc_id = "test-doc-123",
//            user_id = "test-user-456"
//        )
//
//        val response = apiService.analyzeDocument(request)
//
//        assertFalse(response.success)
//        assertEquals("Invalid file format or corrupted document", response.error)
//        assertNull(response.verdict)
//        assertNull(response.total_overcharge)
//    }
//
//    @Test
//    fun testBase64Encoding() {
//        val originalData = "This is a test document content".encodeToByteArray()
//        val base64String = Base64Utils.encodeToString(originalData)
//        val decodedData = Base64Utils.decodeFromString(base64String)
//
//        assertContentEquals(originalData, decodedData)
//    }
//
//    @Test
//    fun testFileFormatDetection() {
//        assertEquals("pdf", Base64Utils.getFileFormat("document.pdf"))
//        assertEquals("jpeg", Base64Utils.getFileFormat("image.jpg"))
//        assertEquals("jpeg", Base64Utils.getFileFormat("photo.jpeg"))
//        assertEquals("png", Base64Utils.getFileFormat("screenshot.png"))
//        assertEquals("pdf", Base64Utils.getFileFormat("unknown.xyz")) // default
//    }
//
//    @Test
//    fun testMimeTypeDetection() {
//        assertEquals("application/pdf", Base64Utils.getMimeType("document.pdf"))
//        assertEquals("image/jpeg", Base64Utils.getMimeType("image.jpg"))
//        assertEquals("image/jpeg", Base64Utils.getMimeType("photo.JPEG"))
//        assertEquals("image/png", Base64Utils.getMimeType("screenshot.PNG"))
//        assertEquals("application/pdf", Base64Utils.getMimeType("unknown.xyz")) // default
//    }
//
//    @Test
//    fun testHealthEndpoint() = runTest {
//        val mockResponse = """
//            {
//                "status": "healthy",
//                "timestamp": "2024-01-15T10:30:00Z",
//                "uptime": 86400.0
//            }
//        """.trimIndent()
//
//        val mockClient = createMockHttpClient(mockResponse)
//        val apiService = VivaranApiServiceImpl(mockClient)
//
//        val response = apiService.getHealthStatus()
//
//        assertEquals("healthy", response.status)
//        assertEquals("2024-01-15T10:30:00Z", response.timestamp)
//        assertEquals(86400.0, response.uptime)
//    }
//}