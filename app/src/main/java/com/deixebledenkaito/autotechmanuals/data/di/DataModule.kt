package com.deixebledenkaito.autotechmanuals.data.di

import android.content.Context
import com.deixebledenkaito.autotechmanuals.data.network.firebstore.SharedPreferencesHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Singleton
    @Provides
    fun provideFirestore() = Firebase.firestore

    @Singleton
    @Provides
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()


    @Singleton
    @Provides
    fun providerFirebaseStorage(): FirebaseStorage = Firebase.storage

    @Provides
    @Singleton
    fun provideSharedPreferencesHelper(
        @ApplicationContext context: Context
    ): SharedPreferencesHelper {
        return SharedPreferencesHelper(context)
    }

}