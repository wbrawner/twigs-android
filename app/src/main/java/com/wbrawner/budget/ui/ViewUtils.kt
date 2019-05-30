package com.wbrawner.budget.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.res.Resources
import android.view.View
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.wbrawner.budget.R
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

fun Disposable.autoDispose(compositeDisposable: CompositeDisposable) {
    compositeDisposable.add(this)
}

fun RecyclerView.hideFabOnScroll(fab: FloatingActionButton) {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (dy > 0) fab.hide() else fab.show()
        }
    })
}

fun <T> Single<T>.fromBackgroundToMain(): Single<T> {
    return this.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
}

fun <T> Observable<T>.fromBackgroundToMain(): Observable<T> {
    return this.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
}

fun View.hide() {
    show(false)
}

fun View.show(show: Boolean = true) {
    val shortAnimTime = Resources.getSystem().getInteger(android.R.integer.config_shortAnimTime).toLong()
    if (show) {
        // Put the view back on the screen before animating its visibility
        alpha = 0f
        visibility = View.VISIBLE
    }
    animate()
            .setDuration(shortAnimTime)
            .alpha((if (show) 1 else 0).toFloat())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    this@show.visibility = if (show) View.VISIBLE else View.GONE
                }
            })
}

fun EditText.ensureNotEmpty(): Boolean {
    return if (this.text.isBlank()) {
        this.error = this.context.getString(R.string.error_required_field)
        false
    } else {
        this.error = null
        true
    }
}
