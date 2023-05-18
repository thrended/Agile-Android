package com.example.agileandroidalpha.feature_board.domain.util

sealed class TaskOrder (val type: OrderType) {
    class Priority(orderType: OrderType): TaskOrder(orderType)
    class Points(orderType: OrderType): TaskOrder(orderType)
    class Title(orderType: OrderType): TaskOrder(orderType)
    class Content(orderType: OrderType): TaskOrder(orderType)
    class Assignee(orderType: OrderType): TaskOrder(orderType)
    class Reporter(orderType: OrderType): TaskOrder(orderType)
    class Creator(orderType: OrderType): TaskOrder(orderType)
    class Created(orderType: OrderType): TaskOrder(orderType)
    class Date(orderType: OrderType): TaskOrder(orderType)
    class Accessed(orderType: OrderType): TaskOrder(orderType)
    class Color(orderType: OrderType): TaskOrder(orderType)
    class ID(orderType: OrderType): TaskOrder(orderType)
    class Size(orderType: OrderType): TaskOrder(orderType)

    fun copy(orderType: OrderType): TaskOrder {
        return when(this) {
            is Priority -> Priority(orderType)
            is Points -> Points(orderType)
            is Title -> Title(orderType)
            is Content -> Content(orderType)
            is Assignee -> Assignee(orderType)
            is Reporter -> Reporter(orderType)
            is Creator -> Creator(orderType)
            is Created -> Created(orderType)
            is Date -> Date(orderType)
            is Accessed -> Date(orderType)
            is Color -> Color(orderType)
            is ID -> ID(orderType)
            is Size -> Size(orderType)
        }
    }

}
