package com.meta_force.meta_force.di

import com.meta_force.meta_force.data.network.AuthApi
import com.meta_force.meta_force.data.network.AuthInterceptor
import com.meta_force.meta_force.data.network.WorkoutApi
import com.meta_force.meta_force.data.network.DietApi
import com.meta_force.meta_force.data.network.AiApi
import com.meta_force.meta_force.data.network.CenterApi
import com.meta_force.meta_force.data.network.MachineApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import okhttp3.ConnectionPool
import okhttp3.logging.HttpLoggingInterceptor
import com.meta_force.meta_force.data.network.TokenAuthenticator

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // We log everything
        }

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .authenticator(tokenAuthenticator)
            .connectionPool(ConnectionPool(5, 5, TimeUnit.MINUTES))
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://meta-force-back.vercel.app/api/") // Production Server
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
    fun provideGymClassApi(retrofit: Retrofit): com.meta_force.meta_force.data.network.GymClassApi {
        return retrofit.create(com.meta_force.meta_force.data.network.GymClassApi::class.java)
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

    @Provides
    @Singleton
    fun provideAiApi(retrofit: Retrofit): AiApi {
        return retrofit.create(AiApi::class.java)
    }

    @Provides
    @Singleton
    fun provideCenterApi(retrofit: Retrofit): CenterApi {
        return retrofit.create(CenterApi::class.java)
    }

    @Provides
    @Singleton
    fun provideMachineApi(retrofit: Retrofit): MachineApi {
        return retrofit.create(MachineApi::class.java)
    }

    @Provides
    @Singleton
    fun provideProgressApi(retrofit: Retrofit): com.meta_force.meta_force.data.network.ProgressApi {
        return retrofit.create(com.meta_force.meta_force.data.network.ProgressApi::class.java)
    }
}