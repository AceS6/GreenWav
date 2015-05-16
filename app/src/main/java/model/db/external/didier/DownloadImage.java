package model.db.external.didier;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.InputStream;

public class DownloadImage extends AsyncTask<String, Void, Bitmap> {

    private ImageView bmImage;
    private Context c;
    private ProgressBar dl;

    public DownloadImage(Context c, ImageView bmImage, ProgressBar dl) {
        this.bmImage = bmImage;
        this.c = c;
        this.dl = dl;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];

        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        dl.setVisibility(View.INVISIBLE);
        if (result != null) {
            bmImage.setImageBitmap(result);
            bmImage.setAlpha(1f);
        }
    }
}
