package com.example.agileandroidalpha.feature_board.domain.util

sealed class OrderType {
    object Ascending: OrderType()
    object Descending: OrderType()
}
