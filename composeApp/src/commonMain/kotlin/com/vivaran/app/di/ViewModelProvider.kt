package com.vivaran.app.di

import androidx.compose.runtime.Composable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import com.vivaran.app.presentation.auth.AuthViewModel
import com.vivaran.app.presentation.history.HistoryViewModel
import com.vivaran.app.presentation.upload.UploadViewModel
import com.vivaran.app.presentation.result.ResultViewModel

object ViewModelProvider : KoinComponent {
    val authViewModel: AuthViewModel by inject()
    val historyViewModel: HistoryViewModel by inject()
    val uploadViewModel: UploadViewModel by inject()
    val resultViewModel: ResultViewModel by inject()
}

@Composable
fun getAuthViewModel(): AuthViewModel = ViewModelProvider.authViewModel


@Composable
fun getHistoryViewModel(): HistoryViewModel = ViewModelProvider.historyViewModel

@Composable
fun getUploadViewModel(): UploadViewModel = ViewModelProvider.uploadViewModel

@Composable
fun getResultViewModel(): ResultViewModel = ViewModelProvider.resultViewModel