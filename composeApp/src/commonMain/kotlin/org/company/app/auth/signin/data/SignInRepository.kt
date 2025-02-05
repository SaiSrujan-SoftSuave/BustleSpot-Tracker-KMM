package org.company.app.auth.signin.data
import org.company.app.auth.utils.Result
import kotlinx.coroutines.flow.Flow

interface SignInRepository {
    suspend fun testRoot() :Flow<Result<Any>>
    fun signIn(email: String, password: String): Flow<Result<SignInResponse>>
}
