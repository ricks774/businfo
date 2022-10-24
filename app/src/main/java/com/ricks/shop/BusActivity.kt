package com.ricks.shop

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_bus.*
import kotlinx.android.synthetic.main.row_bus.*
import kotlinx.android.synthetic.main.row_bus.view.*
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.io.IOException

class BusActivity : AppCompatActivity() {
    var bus:BusRealtime?= null
    var datas: List<BusLot>? = null
    val retrofit = Retrofit.Builder()
        .baseUrl("https://data.tycg.gov.tw/opendata/datalist/datasetMeta/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val TAG = BusActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bus)

        //get http
        val url = ("https://data.tycg.gov.tw/opendata/datalist/datasetMeta/download?id=b3abedf0-aeae-4523-a804-6e807cbad589&rid=bf55b21a-2b7c-4ede-8048-f75420344aed")
        val okHttpClient = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()
        val call = okHttpClient.newCall(request)

        // 異步請求
        call.enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {
                Log.d(TAG, "FAIL!!")
            }

            override fun onResponse(call: Call, response: Response) {
                val busJson = response.body?.string()
                val busServer = retrofit.create(BusService::class.java)
                bus = busServer.listBus()
                    .execute()
                    .body()
                datas = bus?.datas
                Thread{
                    runOnUiThread {
                        bus_recycler.layoutManager = LinearLayoutManager(this@BusActivity)
                        bus_recycler.setHasFixedSize(true)
                        bus_recycler.adapter = BusAdapter()
                    }
                }.start()
            }

        })

    }

//    // Gson
//    private fun busGson(busJson : String){
//        bus = Gson().fromJson<List<BusRealtime>>(busJson,
//        object : TypeToken<List<BusRealtime>>(){}.type)
//        Log.d(TAG, "busGson: $bus")
////        bus?.forEach {
////            Log.d(TAG, "${it.busLots.last()} ${it.Speed} ${it.RouteID} ${it.GoBack}")
////        }
//    }


    // RecyclerView
    // RecyclerHolder
    inner class BusHolder(view: View) : RecyclerView.ViewHolder(view){
        val caridText : TextView = view.bus_carid
        val carspeedText : TextView = view.bus_speed
        val carrouteidText : TextView = view.bus_routeid
        val cargobackText : TextView = view.bus_goback
        fun bindBus(busItem : BusLot){
            caridText.text = busItem.BusID
            carspeedText.text = busItem.Speed
            carrouteidText.text = busItem.RouteID
            cargobackText.text = busItem.GoBack
        }
    }

    //RecyclerAdapter
    inner class BusAdapter() : RecyclerView.Adapter<BusHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_bus, parent, false)
            return BusHolder(view)
        }

        override fun onBindViewHolder(holder: BusHolder, position: Int) {
            val busItem = datas?.get(position)
            holder.bindBus(busItem!!)
        }

        override fun getItemCount(): Int {
            return bus?.datas?.size ?: 0
        }

    }

}

data class BusRealtime(
    val datas: List<BusLot>
)

data class BusLot(
    val Azimuth: String,
    val BusID: String,
    val BusStatus: String,
    val DataTime: String,
    val DutyStatus: String,
    val GoBack: String,
    val Latitude: String,
    val Longitude: String,
    val ProviderID: String,
    val RouteID: String,
    val Speed: String,
    val ledstate: String,
    val sections: String
)

interface BusService{
    @GET("download?id=b3abedf0-aeae-4523-a804-6e807cbad589&rid=bf55b21a-2b7c-4ede-8048-f75420344aed")
    fun listBus() : retrofit2.Call<BusRealtime>

}