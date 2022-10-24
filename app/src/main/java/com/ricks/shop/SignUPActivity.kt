package com.ricks.shop

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_nickname.*
import kotlinx.android.synthetic.main.activity_sign_upactivity.*


class SignUPActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_upactivity)
        signup_btn.setOnClickListener {
            val sEmail = email.text.toString()
            val sPassword = password.text.toString()
            // 使用Firebase 的方式取得註冊資訊
            FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(sEmail, sPassword)  // 取得帳號跟密碼字串
                .addOnCompleteListener {
                    if (it.isSuccessful){
                        AlertDialog.Builder(this)
                            .setTitle("Sing up")
                            .setMessage("Account create")
                            .setPositiveButton("OK"){dialog, which ->
                                var intent = Intent()
                                intent.putExtra("EMAIL", email.text.toString())  // 要回傳的值
                                setResult(RESULT_OK, intent)
                                finish()    // 結束回到上一個畫面
                            }.show()
                    }else{
                        AlertDialog.Builder(this)
                            .setTitle("Sign up")
                            .setMessage(it.exception?.message)  // 用 ? 去判斷值是否為null
                            .setPositiveButton("OK",null)
                            .show()
                    }
                }
        }
    }
}