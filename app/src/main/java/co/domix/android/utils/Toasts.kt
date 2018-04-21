package co.domix.android.utils

import android.app.Activity
import android.widget.Toast

/**
 * Created by unicorn on 4/13/2018.
 */
fun Activity.toastShort(message: String){
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Activity.toastLong(message: String){
    Toast.makeText(this, message, Toast.LENGTH_LONG).show()
}