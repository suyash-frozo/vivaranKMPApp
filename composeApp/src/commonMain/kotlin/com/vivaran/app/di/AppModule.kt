package com.vivaran.app.di

import com.vivaran.app.presentation.auth.AuthViewModel
import com.vivaran.app.presentation.upload.UploadViewModel
import com.vivaran.app.presentation.result.ResultViewModel
import com.vivaran.app.presentation.history.HistoryViewModel
import org.koin.dsl.module

val appModule = module {
    factory { AuthViewModel(get()) }
    factory { UploadViewModel(get(), get()) }
    factory { ResultViewModel(get()) }
    factory { HistoryViewModel(get()) }
}