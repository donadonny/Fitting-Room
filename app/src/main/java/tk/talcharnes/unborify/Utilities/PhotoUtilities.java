package tk.talcharnes.unborify.Utilities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;

/**
 * Created by Tal on 8/18/2017.
 */

public class PhotoUtilities {
    public static int getCameraPhotoOrientation(Context context, Uri imageUri, String imagePath) {
        int rotate = 0;
        try {
            context.getContentResolver().notifyChange(imageUri, null);
            File imageFile = new File(imagePath);

            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }

            Log.i("RotateImage", "Exif orientation: " + orientation);
            Log.i("RotateImage", "Rotate value: " + rotate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }


    public static void scaleImage(Activity activity, ImageView imageView) {
        // Get the ImageView and its bitmap
        Drawable drawing = imageView.getDrawable();
        if (drawing == null) {
            return; // Checking for null & return, as suggested in comments
        }
        Bitmap bitmap = ((BitmapDrawable) drawing).getBitmap();

        // Get current dimensions AND the desired bounding box
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int bounding = dpToPx(250, activity);
        Log.i("Test", "original width = " + Integer.toString(width));
        Log.i("Test", "original height = " + Integer.toString(height));
        Log.i("Test", "bounding = " + Integer.toString(bounding));

        // Determine how much to scale: the dimension requiring less scaling is
        // closer to the its side. This way the image always stays inside your
        // bounding box AND either x/y axis touches it.
        float xScale = ((float) bounding) / width;
        float yScale = ((float) bounding) / height;
        float scale = (xScale <= yScale) ? xScale : yScale;
        Log.i("Test", "xScale = " + Float.toString(xScale));
        Log.i("Test", "yScale = " + Float.toString(yScale));
        Log.i("Test", "scale = " + Float.toString(scale));

        // Create a matrix for the scaling and add the scaling data
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        // Create a new bitmap and convert it to a format understood by the ImageView
        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        width = scaledBitmap.getWidth(); // re-use
        height = scaledBitmap.getHeight(); // re-use
        BitmapDrawable result = new BitmapDrawable(scaledBitmap);
        Log.i("Test", "scaled width = " + Integer.toString(width));
        Log.i("Test", "scaled height = " + Integer.toString(height));

        // Apply the scaled bitmap
        imageView.setImageDrawable(result);

        // Now change ImageView's dimensions to match the scaled image
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imageView.getLayoutParams();
        params.width = width;
        params.height = height;
        imageView.setLayoutParams(params);

        Log.i("Test", "done");
    }

    private static int dpToPx(int dp, Activity activity) {
        float density = activity.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    public static String removeWebPFromUrl(String url) {
        return url.replace(".webp", "");
    }

}
