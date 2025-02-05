package org.company.app.tracker.data

import org.company.app.network.models.response.GetAllProjects
import org.company.app.network.models.response.GetAllTasks
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.company.app.auth.utils.Result
import org.company.app.network.APIEndpoints.GETALLPROJECTS
import org.company.app.network.APIEndpoints.GETALLTASKS
import org.company.app.network.APIEndpoints.POSTACTIVITY
import org.company.app.network.BASEURL
import org.company.app.network.models.request.ActivityDto
import org.company.app.network.models.response.ActivityResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.util.reflect.TypeInfo

class TrackerRepositoryImpl(private val client: HttpClient) : TrackerRepository {
    override fun getAllProjects(): Flow<Result<GetAllProjects>> {
        return flow {
            try {
                emit(Result.Loading)
                val response: HttpResponse = client.get("$BASEURL$GETALLPROJECTS") {
                    contentType(ContentType.Application.Json)
                }
                if (response.status == HttpStatusCode.OK){
                    val data: GetAllProjects = response.body()
                    emit(Result.Success(data))
                }else{
                    emit(Result.Error(message = "Failed to fetch Projects: ${response.status}"))
                }
            } catch (e: Exception) {
                emit(Result.Error(e.message ?: "Unknown error"))
            }
        }
    }

    override fun getAllTask(): Flow<Result<GetAllTasks>> {
        return flow {
            try {
                emit(Result.Loading)
                val response: HttpResponse = client.get("$BASEURL$GETALLTASKS") {
                    contentType(ContentType.Application.Json)
                }
                if (response.status == HttpStatusCode.OK){
                    val data: GetAllTasks = response.body()
                    emit(Result.Success(data))
                }else{
                    emit(Result.Error(message = "Failed to fetch Projects: ${response.status}"))
                }
            } catch (e: Exception) {
                emit(Result.Error(e.message ?: "Unknown error"))
            }
        }
    }

    override fun postUserActivity(activityDto: ActivityDto): Flow<Result<ActivityResponseDto>> {
        return flow {
            try {
                emit(Result.Loading)
                val response: HttpResponse = client.post("$BASEURL$POSTACTIVITY") {
                    contentType(ContentType.Application.Json)
                  setBody(activityDto,bodyType = TypeInfo(ActivityDto :: class))
                }
                if (response.status == HttpStatusCode.OK){
                    val data: ActivityResponseDto = response.body()
                    emit(Result.Success(data))
                }else{
                    emit(Result.Error(message = "Failed to fetch Projects: ${response.status}"))
                }
            } catch (e: Exception) {
                emit(Result.Error(e.message ?: "Unknown error"))
            }
        }
    }
}