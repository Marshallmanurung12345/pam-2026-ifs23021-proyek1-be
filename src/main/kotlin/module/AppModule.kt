package org.delcom.module

import io.ktor.server.application.*
import org.delcom.repositories.*
import org.delcom.services.AuthService
import org.delcom.services.LaundryOrderService
import org.delcom.services.LaundryServiceService
import org.delcom.services.UserService
import org.koin.dsl.module

fun appModule(application: Application) = module {
    val baseUrl = application.environment.config
        .property("ktor.app.baseUrl")
        .getString()
        .trimEnd('/')

    val jwtSecret = application.environment.config
        .property("ktor.jwt.secret")
        .getString()

    // Repositories
    single<IUserRepository> { UserRepository(baseUrl) }
    single<IRefreshTokenRepository> { RefreshTokenRepository() }
    single<ILaundryServiceRepository> { LaundryServiceRepository(baseUrl) }
    single<ILaundryOrderRepository> { LaundryOrderRepository() }

    // Services
    single { UserService(get(), get()) }
    single { AuthService(jwtSecret, get(), get()) }
    single { LaundryServiceService(get(), get()) }
    single { LaundryOrderService(get(), get(), get()) }
}
