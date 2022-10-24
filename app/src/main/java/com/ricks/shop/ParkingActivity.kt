package com.ricks.shop

import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_parking.*
import kotlinx.android.synthetic.main.activity_sign_upactivity.*
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.URL

class ParkingActivity : AppCompatActivity() {
    private val TAG = ParkingActivity::class.java.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_parking)

        // 程式開始
        val url = "http://data.tycg.gov.tw/opendata/datalist/datasetMeta/download?id=f4cc0b12-86ac-40f9-8745-885bddc18f79&rid=0daad6e6-0632-44f5-bd25-5e1de1e9146f"

        // =================== okhttp連線設定 ===================
        val okHttpClient = OkHttpClient()   // 實例化一個 Client
        // 建立 Request GET 方法 不需要傳送資料，直接透過請求的 API 網址取得需要的資料
        val request = Request.Builder()
            .url(url)
            .build()
        // 取得 Client 中的 Call 對象並帶入建立好的 Request
        val call = okHttpClient.newCall(request)

        // 異步請求
        call.enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {
                Log.d(TAG, "onFailure: $e")   // 請求失敗顯示的訊息
            }

            // 請求成功後處理回來的 Response
            override fun onResponse(call: Call, response: Response) {
                val responseStr = response.body?.string()
//                val itemList = JSONObject(responseStr)
                Log.d(TAG, "onResponse: $responseStr")
                // 需變更 UI 介面就需要用 runOnUiThread 方法由主執行緒處理
                Thread{
                    runOnUiThread {
                        parking_info.text = responseStr
                        AlertDialog.Builder(this@ParkingActivity)
                            .setTitle("Message")
                            .setMessage("Data load")
                            .setPositiveButton("OK"){dialog, which ->
                                if (responseStr != null) {
                                    parseGson(responseStr)  // 將json格式傳入
                                }
                            }.show()
                    }
                }.start()
            }
        })
        // =================== okhttp連線設定 ===================
    }

    // =================== Gson設定 ===================
    private fun parseGson(responseStr: String) {
        val parking = Gson().fromJson<Parking>(responseStr, Parking::class.java)
        Log.d(TAG, "total: ${parking.parkingLots.size}")
        parking.parkingLots.forEach {
            Log.d(TAG, "${it.areaId} ${it.areaName} ${it.parkName} ${it.totalSpace}")
        }
    }
    // =================== Gson設定 ===================

}
/*
{
"parkingLots" : [ {
    "areaId" : "1",
    "areaName" : "桃園區",
    "parkName" : "桃園縣公有府前地下停車場",
    "totalSpace" : 334,
    "surplusSpace" : "17",
    "payGuide" : "停車費率:30 元/小時。停車時數未滿一小時者，以一小時計算。逾一小時者，其超過之不滿一小時部分，如不逾三十分鐘者，以半小時計算；如逾三十分鐘者，仍以一小時計算收費。",
    "introduction" : "桃園市政府管轄之停車場",
    "address" : "桃園區縣府路1號(出入口位於桃園市政府警察局前)",
    "wgsX" : 121.3011,
    "wgsY" : 24.9934,
    "parkId" : "P-TY-001"
    }
  ]
}
*/
//class Parking(val parkingLots: List<ParkingLot>)
//
//class ParkingLot(
//    val areaId : String,
//    val areaName : String,
//    val parkName : String,
//    val totalSpace : Int
//)

// 透過外掛解析JSON Code -> Generate
data class Parking(
    val parkingLots: List<ParkingLot>
)

data class ParkingLot(
    val address: String,
    val areaId: String,
    val areaName: String,
    val introduction: String,
    val parkId: String,
    val parkName: String,
    val payGuide: String,
    val surplusSpace: String,
    val totalSpace: Int,
    val wgsX: Double,
    val wgsY: Double
)
