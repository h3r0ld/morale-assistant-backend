package hu.herolds.projects.morale.exception

class SynthesizeException: Exception {
    constructor(cause: Throwable?) : super(cause)
    constructor(message: String?) : super(message)
}