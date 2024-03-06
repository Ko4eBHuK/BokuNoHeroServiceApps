package com.preachooda.assets.templates.section.domain

sealed class Intent { // TODO: rename
    data object CloseError : Intent()

    data object CloseMessage : Intent()

    // TODO:  and add remaining children
}
