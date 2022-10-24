package com.ricks.shop

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.ricks.shop.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_nickname.*
import kotlinx.android.synthetic.main.activity_sign_upactivity.*
import kotlinx.android.synthetic.main.fragment_first.*
import kotlinx.android.synthetic.main.row_function.view.*
import java.text.FieldPosition

class MainActivity : AppCompatActivity() {
    val signup = false
    val TAG = MainActivity::class.java.simpleName
    val auth = FirebaseAuth.getInstance()
    val functions = listOf<String>(  // Recycler的資料
        "Camera",
        "Invite friend",
        "Parking",
        "Movie",
        "Bus",
        "Map"
    )

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

//        if (!signup){
//            val intent = Intent(this, SignUPActivity::class.java)
//            resultLauncher.launch(intent)
//        }

        // Firebase狀態傾聽器
        auth.addAuthStateListener { auth ->
            authChange(auth)
        }

        // Spinner下拉選單
        val colors = arrayOf("Red", "Green", "Blue")
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, colors) // 設定下拉選單樣式
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)  // 設定選項的樣式
        spinner.adapter = adapter
        // 讀取spinner的值
        spinner.onItemSelectedListener = object : OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
                Log.d(TAG, "onItemSelected: ${colors[position]}")
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        // RecyclerView
        recycler.layoutManager = LinearLayoutManager(this)  // 佈局設定
        recycler.setHasFixedSize(true)  // 是否為固定大小
        recycler.adapter = FunctionAdapter()  // Recycler的adapter



        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    // =================== Recycler顯示設定 ===================
    // Recycler的ViewHolder設定
    class FunctionHolder(view: View) : RecyclerView.ViewHolder(view){
        var nameText: TextView = view.name   // name是指 row_function.xml 裡面的文字ID
    }


    // Recycler的adapter設定
    inner class FunctionAdapter() : RecyclerView.Adapter<FunctionHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FunctionHolder {
            val view = LayoutInflater.from(parent.context)   // 設定 LayoutInflater 類別
                .inflate(R.layout.row_function, parent, false)
            val holder = FunctionHolder(view)
            return holder
        }

        override fun onBindViewHolder(holder: FunctionHolder, position: Int) {
            holder.nameText.text = functions.get(position)
            holder.itemView.setOnClickListener { view ->   // 事件觸發
                functionClick(holder, position)
            }
        }

        override fun getItemCount(): Int {   // 有幾筆資料 回傳size
            return functions.size
        }
    }


    // 事件觸發器
    private fun functionClick(holder: FunctionHolder, position: Int) {
        Log.d(TAG, "functionClick: $position")
        when(position){
            1 -> startActivity(Intent(this, ContactActivity::class.java))
            2 -> startActivity(Intent(this, ParkingActivity::class.java))
            3 -> startActivity(Intent(this, MovieActivity::class.java))
            4 -> startActivity(Intent(this, BusActivity::class.java))
        }
    }
    // =================== Recycler顯示設定 ===================


    // Firebase狀態傾聽器的方法
    private fun authChange(auth: FirebaseAuth) {
        if (auth.currentUser == null){
            val intent = Intent(this, SignUPActivity::class.java)
            resultLauncher.launch(intent)
        }else{
            Log.d(TAG, "authChange: ${auth.currentUser?.uid}")
        }
    }

    // =================== Activity頁面跳轉設定 ===================
    val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            activityResult ->
        if (RESULT_OK == activityResult.resultCode){
            val intent = Intent(this, NicknameActivity::class.java)
            startActivity(intent)
        }

        val email = activityResult.data?.getStringExtra("EMAIL")
        Log.d(TAG, "email: ${email}")
        val nickname = activityResult.data?.getStringExtra("NICK")
        Log.d(TAG, "nickname:${nickname}")
    }
    // =================== Activity頁面跳轉設定 ===================


    // =================== 透過 Firebase 網路讀取資料 ===================
    override fun onResume() {
        super.onResume()
//        show_nickname.setText(getNickname())
        // 讀取即時資料庫的內容
        FirebaseDatabase.getInstance()
            .getReference("users")
            .child(auth.currentUser!!.uid)
            .child("nickname")
            .addListenerForSingleValueEvent(object :ValueEventListener{  // 連線讀取資料 只讀取一次
                override fun onDataChange(datasnapshot: DataSnapshot) {
                    show_nickname.text = (datasnapshot.value as String) // 要用 as String轉為字串
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
    }
    // =================== 透過 Firebase 網路讀取資料 ===================

//    override fun onRestart() {
//        super.onRestart()
//        Log.d(TAG, "rs_email: ${resultLauncher.launch(intent).toString()}")
//
//        val intent = Intent(this, NicknameActivity::class.java)
//            resultLauncher.launch(intent)
//    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}