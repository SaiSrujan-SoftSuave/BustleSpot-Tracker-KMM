package org.company.app.auth.signup.data

import org.company.app.auth.utils.Result
import kotlinx.coroutines.flow.Flow

fun interface SignUpRepository {
    suspend fun signUp(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): Flow<Result<SignUpResponse>>
}