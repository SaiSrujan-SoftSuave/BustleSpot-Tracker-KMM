package org.company.app.tracker.data

import org.company.app.network.models.response.GetAllProjects
import org.company.app.network.models.response.GetAllTasks
import kotlinx.coroutines.flow.Flow
import org.company.app.auth.utils.Result
import org.company.app.network.models.request.ActivityDto
import org.company.app.network.models.response.ActivityResponseDto
import org.company.app.network.models.response.GetAllActivities

interface TrackerRepository {
    fun getAllProjects() : Flow<Result<GetAllProjects>>

    fun getAllTask() : Flow<Result<GetAllTasks>>

    fun postUserActivity(activityDto: ActivityDto): Flow<Result<ActivityResponseDto>>

    fun getAllActivities(taskId : String) :  Flow<Result<GetAllActivities>>
}