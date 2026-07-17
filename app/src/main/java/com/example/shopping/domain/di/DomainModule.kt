package com.example.shopping.domain.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class DomainModule {

//    @Provides
//    fun provideRepo(firebaseAuth: FirebaseAuth, firebaseFirestore: FirebaseFirestore): Repo{
//        return RepoImpl(firebaseAuth, firebaseFirestore)
//    }

}