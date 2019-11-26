package com.isoft.parkingcalc.extenstions

import java.lang.annotation.RetentionPolicy

import javax.inject.Qualifier


@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ApplicationContext

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class DatabaseInfo