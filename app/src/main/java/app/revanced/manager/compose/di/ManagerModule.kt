package app.revanced.manager.compose.di

import app.revanced.manager.compose.domain.manager.KeystoreManager
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val managerModule = module {
    singleOf(::KeystoreManager)
}