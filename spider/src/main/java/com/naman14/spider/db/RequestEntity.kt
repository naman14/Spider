package com.naman14.spider.db

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "requests")
data class  RequestEntity(@PrimaryKey(autoGenerate = false) @NonNull var uri: String = "",
                          @ColumnInfo(name = "path") var path: String? = null,
                          @ColumnInfo(name = "request_headers") var requestHeaders: String? = null,
                          @ColumnInfo(name = "request_body_map") var requestBodyMap: String? = null,
                          @ColumnInfo(name = "request_string") var requestString: String? = null,
                          @ColumnInfo(name = "request_method") var requestMethod: String? = null,
                          @ColumnInfo(name = "request_sent_at") var requestSentAt: Long? = 0,
                          @ColumnInfo(name = "response_status_code") var responseStatusCode: Int? = 0,
                          @ColumnInfo(name = "response_string") var responseString: String? = null,
                          @ColumnInfo(name = "response_headers") var responseHeaders: String? = null,
                          @ColumnInfo(name = "response_received_at") var responseReceivedAt: Long? = 0,
                          @ColumnInfo(name = "request_successful") var isSuccessful: Boolean? = false,
                          @ColumnInfo(name = "curl") var curl: String? = null)