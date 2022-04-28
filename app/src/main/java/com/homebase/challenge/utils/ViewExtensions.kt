package com.homebase.challenge.utils

import android.view.LayoutInflater
import android.view.View

val View.layoutInflater: LayoutInflater get() = LayoutInflater.from(context)

inline var View.isVisible: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.INVISIBLE
    }
