package com.example.myapplication.utils

sealed class Resource<T>(
    val data: T? = null,
    val error: Throwable? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Loading<T>(data: T? = null) : Resource<T>(data)
    class Initial<T>(data: T? = null) : Resource<T>(data)
    class Error<T>(throwable: Throwable? = null, data: T? = null, message: String? = null) : Resource<T>(data, throwable)
}
