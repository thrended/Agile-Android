package com.example.agileandroidalpha.feature_board.domain.util

sealed class SprintOrder (val type: OrderType) {
    class Default(type: OrderType): SprintOrder(type)
    class ID(type: OrderType): SprintOrder(type)
    class Points(type: OrderType): SprintOrder(type)
    class Length(type: OrderType): SprintOrder(type)
    class Countdown(type: OrderType): SprintOrder(type)
    class Epic(type: OrderType): SprintOrder(type)
    class Story(type: OrderType): SprintOrder(type)
    class Project(type: OrderType): SprintOrder(type)
    class Board(type: OrderType): SprintOrder(type)
    class Owner(type: OrderType): SprintOrder(type)
    class Manager(type: OrderType): SprintOrder(type)


    fun copy(type: OrderType): SprintOrder {
        return when(this) {
            is Default -> Default(type)
            is ID -> ID(type)
            is Points -> Points(type)
            is Length -> Length(type)
            is Countdown -> Countdown(type)
            is Epic -> Epic(type)
            is Story -> Story(type)
            is Project -> Project(type)
            is Board -> Board(type)
            is Owner -> Owner(type)
            is Manager -> Manager(type)
        }
    }
}