package com.meta_force.meta_force.di

import com.meta_force.meta_force.data.network.AuthApi
import com.meta_force.meta_force.data.network.AuthInterceptor
import com.meta_force.meta_force.data.network.WorkoutApi
import com.meta_force.meta_force.data.network.DietApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:3000/api/") // Android Emulator localhost loopback
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideClassApi(retrofit: Retrofit): com.meta_force.meta_force.data.network.ClassApi {
        return retrofit.create(com.meta_force.meta_force.data.network.ClassApi::class.java)
    }

    @Provides
    @Singleton
    fun provideWorkoutApi(retrofit: Retrofit): WorkoutApi {
        return retrofit.create(WorkoutApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDietApi(retrofit: Retrofit): DietApi {
        return retrofit.create(DietApi::class.java)
    }
}