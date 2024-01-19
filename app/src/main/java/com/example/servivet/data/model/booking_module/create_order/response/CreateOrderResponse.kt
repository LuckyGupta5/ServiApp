package com.example.servivet.data.model.booking_module.create_order.response

data class CreateOrderResponse(
    val code: Int,
    val message: String,
    val result: CreateOrderResult
)