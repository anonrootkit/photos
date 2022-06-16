package com.fiore.photos.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.fiore.photos.UserProfile
import com.fiore.photos.domain.model.UserProfileBody

fun NavController.safeNavigate(direction: NavDirections) {
    currentDestination?.getAction(direction.actionId)?.run { navigate(direction) }
}

fun Fragment.safeNavigate(direction: NavDirections) {
    findNavController().safeNavigate(direction)
}

fun Activity.doAfter(timeInMillis: Long, workToDo: () -> Unit) {
    Handler(Looper.getMainLooper()).postDelayed({
        workToDo()
    }, timeInMillis)
}

fun <T> AppCompatActivity.navigateToActivity(destination: Class<T>, finish: Boolean = true) {
    startActivity(Intent(this, destination)).also {
        if (finish) this.finish()
    }
}


fun <T> Fragment.navigateToActivity(destination: Class<T>, finish: Boolean = true) {
    startActivity(Intent(requireActivity(), destination)).also {
        if (finish) requireActivity().finish()
    }
}

fun Activity.hideStatusBar() {
    val windowInsetsController =
        ViewCompat.getWindowInsetsController(window.decorView) ?: return

    windowInsetsController.systemBarsBehavior =
        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

    windowInsetsController.hide(WindowInsetsCompat.Type.statusBars())
}

fun Activity.showStatusBar() {
    val windowInsetsController =
        ViewCompat.getWindowInsetsController(window.decorView) ?: return

    windowInsetsController.systemBarsBehavior =
        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

    windowInsetsController.show(WindowInsetsCompat.Type.statusBars())
}

fun Fragment.showToast(@StringRes msgId : Int, long : Boolean = false) {
    Toast.makeText(context, msgId, if (long) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()
}

fun Fragment.showToast(msg : String, long : Boolean = false) {
    Toast.makeText(context, msg, if (long) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()
}

fun String.showToast(context: Context, long: Boolean = false) {
    Toast.makeText(context, this, if (long) Toast.LENGTH_LONG else Toast.LENGTH_SHORT).show()
}

fun UserProfile.toUserProfileBody() : UserProfileBody {
    return UserProfileBody(
        uniqueId = uniqueId,
        name = name,
        followingCount = followingCount,
        followersCount = followersCount,
        isPublicAccount = isPublicAccount
    )
}