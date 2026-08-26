package com.example.shopping.domain.di

import com.example.shopping.data.repo.RepoImplementation
import com.example.shopping.domain.repo.Repo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {

    @Singleton
    @Provides
    fun provideRepo(
        firebaseAuth: FirebaseAuth,
        firebaseFirestore: FirebaseFirestore,
        @dagger.hilt.android.qualifiers.ApplicationContext context: android.content.Context
    ): Repo {
        return RepoImplementation(firebaseAuth, firebaseFirestore, context)
    }
}