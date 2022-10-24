package com.ricks.shop

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_movie.*
import kotlinx.android.synthetic.main.fragment_first.*
import kotlinx.android.synthetic.main.row_movie.view.*
import okhttp3.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.io.IOException

class MovieActivity : AppCompatActivity() {
    var movies:List<Movie>?= null
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.jsonserve.com/")
        .addConverterFactory(GsonConverterFactory.create()) // 編譯的方式，使用Gson編譯器
        .build()
    private val TAG = MovieActivity::class.java.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)
        // get http
        val url = "https://api.jsonserve.com/6HzNXZ"
        val okHttpClient = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()
        val call = okHttpClient.newCall(request)

        // =============== 異步請求 ===============
        call.enqueue(object : Callback{
            override fun onFailure(call: Call, e: IOException) {
                Log.d(TAG, "Fail line!!")
            }

            override fun onResponse(call: Call, response: Response) {
                val json = response.body?.string()
//                if (json != null) {
//                    movieGson(json)
//                }
                // 使用 Retrofit
                val movieService = retrofit.create(MovieService::class.java)
                movies = movieService.listMovie()
                    .execute()
                    .body()
                Thread{
                    runOnUiThread {
                        // 將電影資訊顯示在Recycler上
                        movie_rec.layoutManager = LinearLayoutManager(this@MovieActivity)
                        movie_rec.setHasFixedSize(true)  // 是否為固定大小
                        movie_rec.adapter = MovieAdapter()  // Recycler的adapter
                    }
                }.start()
            }
        })
        // =============== 異步請求 ===============
    }


    // =============== Gson ===============
    fun movieGson(json: String){
        // Movie是 List集合 ，所以要使用 TypeToken 來解析資料
        movies = Gson().fromJson<List<Movie>>(json,
            object : TypeToken<List<Movie>>(){}.type)
        movies?.forEach {
            Log.d(TAG, "${it.Title} ${it.imdbRating}")
        }
    }
    // =============== Gson ===============


    // =============== RecyclerHolder ===============
   inner class MovieHolder(view: View) : RecyclerView.ViewHolder(view){
        val titleText: TextView = view.movie_title
        val imdbText: TextView = view.movie_imdb
        val directorText: TextView = view.movie_director
        val posterImage: ImageView = view.movie_poster
        fun bindMovie(movie: Movie) {
            titleText.text = movie.Title
            imdbText.text = movie.imdbRating
            directorText.text = movie.Director
            Glide.with(this@MovieActivity)  // 讀取照片設定
//                .load(movie.Poster)   // 網站的海報都掛掉了所以註解
                .load(movie.Images[0])  // 劇照是List 讀取劇照中的第1張
                .override(300)  // 圖片的大小限制
                .into(posterImage)  // 圖片要放到哪裡
        }
    }
    // =============== RecyclerHolder ===============


    // =============== Recycler的adapter設定 ===============
    inner class MovieAdapter() : RecyclerView.Adapter<MovieHolder>(){
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.row_movie, parent, false)
            return MovieHolder(view)
        }

        override fun onBindViewHolder(holder: MovieHolder, position: Int) {
            val movie = movies?.get(position)
            holder.bindMovie(movie!!)
        }

        override fun getItemCount(): Int {
            val size = movies?.size?:0   // 假如資料是null 就給 0
            return size
        }

    }
    // =============== Recycler的adapter設定 ===============

}

// Movie List
data class Movie(
    val Actors: String,
    val Awards: String,
    val ComingSoon: Boolean,
    val Country: String,
    val Director: String,
    val Genre: String,
    val Images: List<String>,
    val Language: String,
    val Metascore: String,
    val Plot: String,
    val Poster: String,
    val Rated: String,
    val Released: String,
    val Response: String,
    val Runtime: String,
    val Title: String,
    val Type: String,
    val Writer: String,
    val Year: String,
    val imdbID: String,
    val imdbRating: String,
    val imdbVotes: String,
    val totalSeasons: String
)

// 定義 Retrofit 的interface
interface MovieService {
    @GET("6HzNXZ")
    fun listMovie() : retrofit2.Call<List<Movie>>
}