package net.maps.navigation.gps.location.sondermap.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

public class MyUtil {
    public static void MyLog(Object s){
        System.out.println("----*--------*"+s);

    }

    public static void MyToast(Context context,String s){
        Toast toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        toast.setText(s);
        toast.show();
    }


    public static Bitmap decodeBitmapFromResource(Context context, int drawableId) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            return BitmapFactory.decodeResource(context.getResources(), drawableId);
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }


}
