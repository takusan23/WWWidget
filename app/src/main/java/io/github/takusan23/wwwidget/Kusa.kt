package io.github.takusan23.wwwidget

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup

class Kusa {

    /**
     * CSS var() から カラーコード対応表
     * */
    private val colorNameList = mutableMapOf(
        "var(--color-calendar-graph-day-bg)" to "#ebedf0",
        "var(--color-calendar-graph-day-L1-bg)" to "#9be9a8",
        "var(--color-calendar-graph-day-L2-bg)" to "#40c463",
        "var(--color-calendar-graph-day-L3-bg)" to "#30a14e",
        "var(--color-calendar-graph-day-L4-bg)" to "#216e39"
    )

    /**
     * GitHubの草を取得する
     * @param userName GitHubでの名前。
     * @return API叩いた結果。失敗したら空文字です
     * */
    fun getGitHubContribute(userName: String): Deferred<String> = GlobalScope.async {
        // 作成
        val request = Request.Builder()
            .url("https://github.com/users/$userName/contributions")
            .get()
            .build()
        // リクエスト
        val okHttpClient = OkHttpClient()
        val response = okHttpClient.newCall(request).execute()
        if (response.isSuccessful) {
            // 成功時
            return@async response.body?.string() ?: ""
        } else {
            return@async ""
        }
    }

    /**
     * Contributeを取得したあと色の配列にする
     * @param response Contribute叩いた結果
     * @return カラーコードの配列。
     * */
    fun parseContributeResponse(response: String): ArrayList<String> {
        val arrayList = arrayListOf<String>()
        // HTMLぱーさー
        val document = Jsoup.parse(response)
        // 草一個分
        val rectList = document.getElementsByTag("rect")
        for (i in 0 until rectList.size) {
            // カラーコードがある属性取得
            val color = rectList[i].attr("fill")
            arrayList.add(colorNameList[color] ?: "#ffffff")
        }
        return arrayList
    }

    fun createGrassCanvas(colorList: ArrayList<String>): Bitmap {
        // 辺の長さ？
        val squareSize = 30f

        // Canvas生成
        val bitmap = Bitmap.createBitmap(((squareSize + 2) * 52).toInt(), ((squareSize + 2) * 7).toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // 左上のX座標
        var left = 0f
        // 左上のY座標
        var top = 0f
        // 右下のX座標
        var right = squareSize
        // 右下のY座標
        var bottom = squareSize

        for (i in 0 until colorList.size) {

            if (true) {

                // println(colorList[i])

                // 次の列へ
                if (i % 7 == 0 && i != 0) {
                    left = right + 2
                    right += squareSize
                    // 高さ初期化
                    top = 0f
                    bottom = squareSize
                }

                // 描画
                val paint = Paint()
                paint.color = Color.parseColor(colorList[i])
                canvas.drawRoundRect(left, top, right, bottom, 0f, 0f, paint)

                // Canvasに書いたら下に移動
                // 高さ。第二引数と第四引数
                top += squareSize + 2
                bottom += squareSize + 2


            }

        }

        return bitmap
    }

}