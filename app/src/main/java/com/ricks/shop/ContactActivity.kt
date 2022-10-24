package com.ricks.shop

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class ContactActivity : AppCompatActivity() {
    private val RC_CONTACTS = 100
    private val TAG = ContactActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)


        val permission =   //確認是否有讀取的權限
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
        if (permission != PackageManager.PERMISSION_GRANTED){  // PERMISSION_GRANTED 代表已經取得權限
            // 跳出確認權限視窗
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_CONTACTS),   // 只要求聯絡人資訊，所以array中只會有一筆資料
                RC_CONTACTS)
        }else{
            readContact()
        }
    }

    // 判斷是否權限視窗選擇是或否
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == RC_CONTACTS){
            // 只要求聯絡人資訊，所以array中只會有一筆資料
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){   // 是否有取得授權
                readContact()
            }
        }
    }

    // 讀取聯絡人的資訊
    private fun readContact() {
        val cursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,  // 取得聯絡人的URI
            null, null, null, null   // 設定null表示資料不篩選全都要
        )
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val name =
                    // 透過欄位名稱 找到索引直
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                Log.d(TAG, "readContact: $name")
            }
        }
    }
}