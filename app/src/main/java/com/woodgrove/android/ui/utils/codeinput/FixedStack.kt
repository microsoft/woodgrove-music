package com.woodgrove.android.ui.utils.codeinput

import java.util.Stack

class FixedStack<T> : Stack<T>() {

    var maxSize = 0

    override fun push(item: T): T {
        return if (maxSize > size) {
            super.push(item)
        } else item
    }
}
