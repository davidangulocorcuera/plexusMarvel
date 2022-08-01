package com.plexus.marvel.base


import android.content.Context


interface GlobalAction {
    fun back(context: Context) {
        if (context is BaseActivity<*, *>) {
            context.apply {
                navigator.goToLastFragment()
            }
        }
    }
}