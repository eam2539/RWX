package io.github.rwx.ui.component

import de.fabmax.kool.modules.ui2.MutableStateList

internal fun <T> MutableStateList<T>.replaceAllIncrementally(next: List<T>): Boolean {
    var changed = false
    atomic {
        if (size == next.size) {
            for (index in next.indices) {
                if (this[index] != next[index]) {
                    this[index] = next[index]
                    changed = true
                }
            }
        } else {
            clear()
            addAll(next)
            changed = true
        }
    }
    return changed
}
