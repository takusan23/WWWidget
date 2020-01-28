package io.github.takusan23.wwwidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.Toast
import androidx.preference.PreferenceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Implementation of App Widget functionality.
 */
class KusaWidget : AppWidgetProvider() {
    // 定期的に呼ばれる。
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            // 更新したい内容はここに書く？
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        if (context != null) {
            val componentName = ComponentName(context, KusaWidget::class.java)
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
            for (appWidgetId in appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }
        }
    }

}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    // 画像作成
    val preference = PreferenceManager.getDefaultSharedPreferences(context)
    val kusa = Kusa()
    GlobalScope.launch(Dispatchers.Main) {
        if (preference.getString("user_name", null) != null) {
            Toast.makeText(context, R.string.update, Toast.LENGTH_SHORT).show()
            val userName = preference.getString("user_name", null)!!
            // 時代の流れに乗ってコルーチン使ってみる
            // 取得＋HTMLパース＋画像生成
            val contribute = kusa.getGitHubContribute(userName).await()
            val colorList = kusa.parseContributeResponse(contribute)
            val grass = kusa.createGrassCanvas(colorList)

            // Construct the RemoteViews object
            // ウィジェットのレイアウト読み込み
            val views = RemoteViews(context.packageName, R.layout.kusa_widget)
            // Bitmapセット
            views.setImageViewBitmap(R.id.widget_imageview, grass)
            // ユーザーネームセット
            views.setTextViewText(R.id.widget_username_textview, userName)
            // 押したとき
            val intent = Intent(context, KusaWidget::class.java)
            val pendingIntent =
                PendingIntent.getBroadcast(context, 25, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            views.setOnClickPendingIntent(R.id.widget_load_button, pendingIntent)
            // ウィジェット更新
            appWidgetManager.updateAppWidget(appWidgetId, views)
        } else {
            Toast.makeText(context, R.string.user_name_not_fount, Toast.LENGTH_SHORT).show()
        }

//        // Construct the RemoteViews object
//        // ウィジェットのレイアウト読み込み
//        val views = RemoteViews(context.packageName, R.layout.kusa_widget)
//        // 押したとき
//        val intent = Intent(context, KusaWidget::class.java)
//        val pendingIntent =
//            PendingIntent.getBroadcast(context, 25, intent, PendingIntent.FLAG_UPDATE_CURRENT)
//        views.setOnClickPendingIntent(R.id.widget_load_button, pendingIntent)
//        // ウィジェット更新
//        appWidgetManager.updateAppWidget(appWidgetId, views)


    }
}

