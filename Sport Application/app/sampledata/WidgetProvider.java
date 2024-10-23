package com.example.caglarkc;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

public class WidgetProvider extends AppWidgetProvider {
//NASIL WİDGET YAPILIR ÖRNEĞİ
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        CharSequence widgetText = "EXAMPLE";

        // Widget layout'unu yükle
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        views.setTextViewText(R.id.widget_text, widgetText);

        // Butona tıklama olayı tanımla (LoginActivity'yi açacak)
        Intent intent = new Intent(context, LoginActivity.class); // LoginActivity'yi açacak intent
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widget_button, pendingIntent); // Butona pending intent'i ekle

        // Widget'ı güncelle
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }
}

/*
manifest application içine
<receiver android:name=".WidgetProvider" android:exported="true">
            <intent-filter>
                <action android:name="android.appwidget.action.APPWIDGET_UPDATE" />
            </intent-filter>

            <meta-data
                android:name="android.appwidget.provider"
                android:resource="@xml/widget_info" />
        </receiver>
 */

/*
widget_info.xml res/xml kısmına
widget_layout.xml res_layout kısmına
WidgetProvider.java classların oraya eklenicek
 */