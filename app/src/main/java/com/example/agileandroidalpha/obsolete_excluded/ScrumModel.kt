package com.example.agileandroidalpha.obsolete_excluded

import androidx.compose.runtime.Immutable

@Immutable data class ScrumModel(
    val name: String,
    val board: ScrumScreen? = null,
    val page: FuncScreen? = null,
    val tab: TabScreen? = null,
    val availActions: List<ScrumAction>? = null,

    ) {
    val showBoard = "$name, $board"
    val showPage = "$name, $page"
    val showTab = "$name, $tab"
}

@Immutable data class ScrumAction(
    val name: String,
    val desc: String,
){

}