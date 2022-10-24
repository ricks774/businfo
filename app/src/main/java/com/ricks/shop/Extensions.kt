package com.ricks.shop

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity

//在 Activity 底下建立方法
// 儲存暱稱
fun Activity.setNickname(nickname : String){
    getSharedPreferences("nickname", AppCompatActivity.MODE_PRIVATE)
        .edit()
        .putString("NICKNAME",nickname)
        .apply()
}

// 讀取暱稱
fun Activity.getNickname(): String? {
    return getSharedPreferences("nickname", AppCompatActivity.MODE_PRIVATE)
        .getString("NICKNAME", "")

}