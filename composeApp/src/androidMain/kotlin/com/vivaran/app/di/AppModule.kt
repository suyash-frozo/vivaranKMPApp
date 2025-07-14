package com.vivaran.app

import com.vivaran.app.presentation.auth.AuthViewModel
import com.vivaran.app.presentation.upload.UploadViewModel
import com.vivaran.app.presentation.result.ResultViewModel
import com.vivaran.app.presentation.history.HistoryViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { AuthViewModel(get()) }
    viewModel { UploadViewModel(get(), get()) }
    viewModel { ResultViewModel(get()) }
    viewModel { HistoryViewModel(get()) }
}