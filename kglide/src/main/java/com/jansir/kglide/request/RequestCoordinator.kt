package com.jansir.kglide.request

interface RequestCoordinator {

    enum class RequestState(val isComplete: Boolean) {
        RUNNING(false), PAUSED(false), CLEARED(false), SUCCESS(true), FAILED(true);
    }
}