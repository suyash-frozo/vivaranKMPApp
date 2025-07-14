package com.vivaran.app.presentation.upload

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vivaran.app.presentation.auth.AuthViewModel
import com.vivaran.shared.domain.model.FileType
import com.vivaran.app.di.getUploadViewModel
import com.vivaran.app.di.getAuthViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadScreen(
    selectedAgentId: String?,
    onUploadSuccess: () -> Unit,
    onBack: () -> Unit,
    uploadViewModel: UploadViewModel = getUploadViewModel(),
    authViewModel: AuthViewModel = getAuthViewModel()
) {
    val uploadState by uploadViewModel.uploadState.collectAsState()
    val authState by authViewModel.authState.collectAsState()
    
    LaunchedEffect(uploadState.uploadSuccess) {
        if (uploadState.uploadSuccess) {
            onUploadSuccess()
        }
    }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    text = "Upload Document",
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        )
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Selected Agent Info
                selectedAgentId?.let {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(end = 12.dp)
                            )
                            Text(
                                text = "Using AI Agent: ${getAgentName(selectedAgentId)}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }
            }
            
            item {
                // Upload Progress
                if (uploadState.isUploading) {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                progress = { uploadState.uploadProgress },
                                modifier = Modifier.size(64.dp),
                                strokeWidth = 6.dp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Uploading document...",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "${(uploadState.uploadProgress * 100).toInt()}%",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
            
            item {
                // Error Message
                uploadState.error?.let { error ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
            
            if (!uploadState.isUploading) {
                item {
                    Text(
                        text = "Choose Upload Method",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                item {
                    // Camera Upload
                    Card(
                        onClick = {
                            // TODO: Implement camera capture
                            // For demo purposes, simulate upload with sample data
                            authState.user?.let { user ->
                                uploadViewModel.uploadAndAnalyzeDocument(
                                    userId = user.id,
                                    agentId = selectedAgentId,
                                    fileName = "camera_capture.jpg",
                                    fileData = ByteArray(1024) { 0 }, // Sample data
                                    fileType = com.vivaran.shared.domain.model.FileType.JPEG
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .size(48.dp)
                                    .padding(end = 16.dp)
                            )
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "Take Photo",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Capture document with camera",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Icon(
                                Icons.Default.KeyboardArrowRight,
                                contentDescription = null
                            )
                        }
                    }
                }
                
                item {
                    // Gallery Upload
                    Card(
                        onClick = {
                            // TODO: Implement gallery picker
                            // For demo purposes, simulate upload with sample data
                            authState.user?.let { user ->
                                uploadViewModel.uploadAndAnalyzeDocument(
                                    userId = user.id,
                                    agentId = selectedAgentId,
                                    fileName = "gallery_image.png",
                                    fileData = ByteArray(2048) { 1 }, // Sample data
                                    fileType = com.vivaran.shared.domain.model.FileType.PNG
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .size(48.dp)
                                    .padding(end = 16.dp)
                            )
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "Choose from Gallery",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Select image from photo library",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Icon(
                                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null
                            )
                        }
                    }
                }
                
                item {
                    // File Upload
                    Card(
                        onClick = {
                            // TODO: Implement file picker
                            // For demo purposes, simulate upload with sample data
                            authState.user?.let { user ->
                                uploadViewModel.uploadAndAnalyzeDocument(
                                    userId = user.id,
                                    agentId = selectedAgentId,
                                    fileName = "document.pdf",
                                    fileData = ByteArray(4096) { 2 }, // Sample data
                                    fileType = com.vivaran.shared.domain.model.FileType.PDF
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Settings,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .size(48.dp)
                                    .padding(end = 16.dp)
                            )
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "Browse Files",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Select PDF, JPEG, or PNG files",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Icon(
                                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null
                            )
                        }
                    }
                }
                
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Supported formats: PDF, JPEG, PNG",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

private fun getAgentName(agentId: String): String {
    return when (agentId) {
        "medical_analysis" -> "Medical Report Analysis"
        "insurance_optimization" -> "Insurance Claim Optimization"
        "farmer_schemes" -> "Farmer Scheme Eligibility"
        "general_document" -> "General Document Summarizer"
        else -> "Unknown Agent"
    }
}