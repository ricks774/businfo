package com.ricks.shop

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_nickname.*
import kotlinx.android.synthetic.main.activity_sign_upactivity.*

class NicknameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nickname)

        finish_btn.setOnClickListener {
            setNickname(nickname.text.toString())  // 呼叫 Extensions 中的方法
            // 設定Firebase的即時資料庫資料
            FirebaseDatabase.getInstance()
                .getReference("users")  // 建立名稱為 users 的節點
                .child(FirebaseAuth.getInstance().currentUser!!.uid)  // 透過 user UID !!代表絕對不是null
                .child("nickname")  // 叫 nickname 的子節點
                .setValue(nickname.text.toString())  // 把nickname值寫入
            val intent = Intent()
            intent.putExtra("NICK", nickname.text.toString())  // 要回傳的值
            setResult(RESULT_OK, intent)    // 回傳
            finish()    // 結束回到上一個畫面
        }
    }
}