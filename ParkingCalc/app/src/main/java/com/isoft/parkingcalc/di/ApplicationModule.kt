package com.isoft.parkingcalc.di

import android.app.Application
import android.content.Context
import com.isoft.parkingcalc.extenstions.ApplicationContext
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class ApplicationModule(app: Application) {
    private val mApplication: Application


    @Singleton
    @Provides
    fun provideContext(): Context {
        return mApplication
    }

    @Singleton
    @Provides
    fun provideApplication(): Application {
        return mApplication
    }

    init {
        mApplication = app
    }
}