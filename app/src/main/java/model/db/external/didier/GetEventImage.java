package model.db.external.didier;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.greenwav.greenwav.R;

import org.w3c.dom.Text;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import model.Event;

/**
 * Created by sauray on 16/03/15.
 */
public class GetEventImage extends AsyncTask<Void, Void, Bitmap>{

    private Event e;
    private View card;
    private DisplayMetrics metrics;

    private final String previewUrl = "http://sauray.me/greenwav/preview/";

    public GetEventImage(Activity a, Event e, View card){
        this.e = e;
        this.card = card;
        metrics = new DisplayMetrics();
        a.getWindowManager().getDefaultDisplay().getMetrics(metrics);
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        URL url = null;
        Bitmap ret = null;
        try {
            url = new URL(previewUrl+ (-e.getId()) + "_" + metrics.densityDpi + ".png");
            ret = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        }
        catch (MalformedURLException e1) {
            e1.printStackTrace();
        }catch (IOException e2) {
            e2.printStackTrace();
        }

        return ret;
    }

    @Override
    protected void onPostExecute(Bitmap result){
        if(result != null){
            ((ImageView) card.findViewById(R.id.eventImage)).setImageBitmap(result);
        }
        else{
            ((TextView) card.findViewById(R.id.eventText)).setText(e.getNom());
        }
    }
}
