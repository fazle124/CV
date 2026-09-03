package com.example.cv.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cv.ui.components.AdminPanelView
import com.example.cv.ui.components.CvPageView
import com.example.cv.ui.components.DocumentsView
import com.example.cv.util.CvPdfExporter
import com.example.cv.viewmodel.CvTab
import com.example.cv.viewmodel.CvViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CvApp(viewModel: CvViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.statusNotification) {
        state.statusNotification?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearNotification()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Resume of ${state.header.name}",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        ),
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    if (state.activeTab != CvTab.CV_VIEW) {
                        IconButton(onClick = { viewModel.setTab(CvTab.CV_VIEW) }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back to CV",
                                tint = Color.White
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = {
                        CvPdfExporter.printCvDocument(context, state.header, state.sections)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Print,
                            contentDescription = "Print CV",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = {
                        CvPdfExporter.exportAndSharePdf(context, state.header, state.sections)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share PDF",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF0B69FF)
                )
            )
        },
        floatingActionButton = {
            if (state.activeTab == CvTab.CV_VIEW) {
                FloatingActionButton(
                    onClick = {
                        CvPdfExporter.exportAndSharePdf(context, state.header, state.sections)
                    },
                    containerColor = Color(0xFF0B69FF),
                    contentColor = Color.White
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = "PDF")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Export A4 PDF", fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        containerColor = Color(0xFFE9EDF3)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Control Action Bar (like #appControls in the web application)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally)
                ) {
                    // Admin button
                    Button(
                        onClick = {
                            if (state.activeTab == CvTab.ADMIN_PANEL) {
                                viewModel.setTab(CvTab.CV_VIEW)
                            } else {
                                viewModel.setTab(CvTab.ADMIN_PANEL)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (state.activeTab == CvTab.ADMIN_PANEL) Color(0xFF1E293B) else Color(0xFF334155)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Admin Panel", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    // Download PDF button
                    Button(
                        onClick = {
                            CvPdfExporter.exportAndSharePdf(context, state.header, state.sections)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0B69FF)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("A4 PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    // Documents button
                    Button(
                        onClick = {
                            if (state.activeTab == CvTab.DOCUMENTS) {
                                viewModel.setTab(CvTab.CV_VIEW)
                            } else {
                                viewModel.setTab(CvTab.DOCUMENTS)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (state.activeTab == CvTab.DOCUMENTS) Color(0xFFD0E2FF) else Color(0xFFEEF4FF),
                            contentColor = Color(0xFF0B69FF)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Description, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Documents", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Loading indicator
            if (state.isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF0B69FF))
                    }
                }
            }

            // Main Active Tab Content
            when (state.activeTab) {
                CvTab.ADMIN_PANEL -> {
                    item {
                        AdminPanelView(
                            viewModel = viewModel,
                            header = state.header,
                            sections = state.sections,
                            isLoggedIn = state.isAdminLoggedIn,
                            isLoading = state.isLoading,
                            loginError = state.loginError
                        )
                    }
                }
                CvTab.DOCUMENTS -> {
                    item {
                        DocumentsView(
                            documents = state.documents,
                            isAdmin = state.isAdminLoggedIn,
                            onAddDocument = { viewModel.addDocument(it) }
                        )
                    }
                }
                CvTab.CV_VIEW -> {
                    item {
                        CvPageView(
                            header = state.header,
                            sections = state.sections
                        )
                    }
                }
            }

            // Bottom Spacing for FAB
            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }
}
