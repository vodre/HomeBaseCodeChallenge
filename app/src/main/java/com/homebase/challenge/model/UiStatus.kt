package com.homebase.challenge.model

sealed interface UiStatus
object Idle : UiStatus
object Loading : UiStatus
object Success : UiStatus
class Failure : UiStatus {
    val error: String

    constructor(e: String) {
        this.error = e
    }
}
