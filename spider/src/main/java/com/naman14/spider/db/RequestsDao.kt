package com.naman14.spider.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RequestsDao {

    @Query("UPDATE requests SET request_successful = :success where uri = :id")
    fun setRequestStatus(id: String, success: Boolean)

    @Query("UPDATE requests SET response_status_code = :code where uri = :id")
    fun setResponseStatusCode(id: String, code: Int)

    @Query("UPDATE requests SET response_string = :response where uri = :id")
    fun setResponseString(id: String, response: String)


    @Query("SELECT * FROM requests")
    fun getAllRequests(): LiveData<List<RequestEntity>>

    @Query("SELECT * FROM requests")
    fun getAllRequestsSync(): List<RequestEntity>

    @Query("SELECT * FROM requests where id = :id")
    fun getRequestSync(id: String): List<RequestEntity>

    @Query("DELETE from requests")
    fun clearAllRequests()

    @Query("DELETE from requests")
    fun clearRequests()

    @Query("")

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRequest(request: RequestEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllRequests(requests: List<RequestEntity>)

    @Delete
    fun deleteRequest(request: RequestEntity)

}