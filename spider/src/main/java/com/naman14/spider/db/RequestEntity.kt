package com.naman14.spider.db

import android.net.Uri
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "requests")
data class  RequestEntity(@PrimaryKey(autoGenerate = false) @NonNull var uri: Uri? = null,
                          @ColumnInfo(name = "request_headers") var requestHeaders: String,
                          @ColumnInfo(name = "request_body") var requestBody: String,
                          @ColumnInfo(name = "request_method") var requestMethod: String,
                          @ColumnInfo(name = "request_sent_at") var requestSentAt: Long,
                          @ColumnInfo(name = "response_status_code") var responseStatusCode: Int,
                          @ColumnInfo(name = "response_string") var responseString: String,
                          @ColumnInfo(name = "response_received_at") var responseReceivedAt: Long,
                          @ColumnInfo(name = "request_successful") var isSuccessful: Boolean)