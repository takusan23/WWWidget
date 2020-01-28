package io.github.takusan23.wwwidget

import android.content.SharedPreferences
import android.graphics.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup

class MainActivity : AppCompatActivity() {

    // 保存するやつ
    lateinit var preferences: SharedPreferences
    // GitHubから取得＋HTMLパース＋画像生成　関数があるクラス
    val kusa = Kusa()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        preferences = PreferenceManager.getDefaultSharedPreferences(this)

        // ユーザーネーム保存してるときは表示
        if (preferences.getString("user_name", null) != null) {
            user_name_edittext.setText(preferences.getString("user_name", ""))
            // 時代の流れに乗ってコルーチン使ってみる
            val userName = user_name_edittext.text.toString()
            kusa(userName)
        }

        // 押したとき
        load_button.setOnClickListener {
            // ユーザー名取得
            val userName = user_name_edittext.text.toString()
            // 保存しとく
            preferences.edit().apply {
                putString("user_name", userName)
                apply()
            }
            // 時代の流れに乗ってコルーチン使ってみる
            kusa(userName)
        }

    }

    // コルーチンチン！！！
    // 使い方があってるのかは知らん。
    fun kusa(userName: String) = GlobalScope.launch(Dispatchers.Main) {
        // 時代の流れに乗ってコルーチン使ってみる
        val contribute = kusa.getGitHubContribute(userName).await()
        val colorList = kusa.parseContributeResponse(contribute)
        // println(colorList)
        val grass = kusa.createGrassCanvas(colorList)
        imageview.setImageBitmap(grass)
    }


}
