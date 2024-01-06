package com.example.servivet.data.repository

import com.example.servivet.data.api.RetrofitBuilder
import com.example.servivet.data.model.booking_module.booking_model.request.RatingRequest

class M2Repository {
    private val service = RetrofitBuilder.apiService
    suspend fun ratingApi(request: RatingRequest) = service.ratingApi(request)

}