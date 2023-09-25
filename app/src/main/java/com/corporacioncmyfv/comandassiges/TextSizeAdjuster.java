package com.corporacioncmyfv.comandassiges;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.TextView;

public class TextSizeAdjuster {

    public static void adjustTextSizeBasedOnScreenSize(Context context, TextView textView) {
        // Obtener el tamaño de pantalla en pulgadas
        float screenSizeInInches = ancho(context);

        // Ajustar el tamaño del texto en función del tamaño de la pantalla
        if (screenSizeInInches < 4.0) {
            // Pantalla pequeña, ajustar el texto a un tamaño pequeño
            textView.setTextSize(12); // Tamaño en sp
        } else if (screenSizeInInches >= 4.0 && screenSizeInInches < 6.0) {
            // Pantalla mediana, ajustar el texto a un tamaño mediano
            textView.setTextSize(13); // Tamaño en sp
        } else {
            // Pantalla grande, ajustar el texto a un tamaño grande
            textView.setTextSize(17); // Tamaño en sp
        }
    }

    public static float getScreenSizeInInches(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        if (windowManager != null) {
            windowManager.getDefaultDisplay().getMetrics(metrics);
            int widthPixels = metrics.widthPixels;
            int heightPixels = metrics.heightPixels;
            float xdpi = metrics.xdpi;
            float ydpi = metrics.ydpi;

            // Calcular el tamaño de la pantalla en pulgadas
            float widthInches = widthPixels / xdpi;
            float heightInches = heightPixels / ydpi;
            return (float) Math.sqrt(Math.pow(widthInches, 2) + Math.pow(heightInches, 2));
        }
        return 0;
    }

    public static float ancho(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        if (windowManager != null) {
            windowManager.getDefaultDisplay().getMetrics(metrics);
            int widthPixels = metrics.widthPixels;
            int heightPixels = metrics.heightPixels;
            float xdpi = metrics.xdpi;
            float ydpi = metrics.ydpi;

            // Calcular el tamaño de la pantalla en pulgadas
            float widthInches = widthPixels / xdpi;

            return widthInches;
        }
        return 0;
    }

    public static float alto(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        if (windowManager != null) {
            windowManager.getDefaultDisplay().getMetrics(metrics);
            int widthPixels = metrics.widthPixels;
            int heightPixels = metrics.heightPixels;
            float xdpi = metrics.xdpi;
            float ydpi = metrics.ydpi;

            // Calcular el tamaño de la pantalla en pulgadas

            float heightInches = heightPixels / ydpi;
            return heightInches;
        }
        return 0;
    }
}
