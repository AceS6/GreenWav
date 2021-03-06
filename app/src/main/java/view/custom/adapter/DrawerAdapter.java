package view.custom.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.greenwav.greenwav.R;

import model.Mode;

/**
 * Created by asauray on 2/18/15.
 */
public class DrawerAdapter extends ArrayAdapter<Mode>{

    private Context context;
    private Mode[] items;

    public DrawerAdapter(Context context, int resource, Mode[] items) {
        super(context, resource);
        this.context = context;
        this.items = items;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View view, ViewGroup parent) {

        if (view == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = mInflater.inflate(R.layout.item_drawer, null);
        }
        Mode m = (Mode) (items[position]);

        TextView tv = (TextView) view.findViewById(R.id.mode);
        tv.setText(m.getTitle());
        if(m.getVersion() == 0){
            tv.setText("Indisponible");
            tv.setTextColor(context.getResources().getColor(R.color.light_text));
        }
        ImageView icon = (ImageView) view.findViewById(R.id.icon);

        if(m.isChecked()){

            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
                Drawable d = context.getResources().getDrawable(m.getDarkIcon(), null);
                d.setTint(context.getResources().getColor(R.color.accent));
            }
            else{
                icon.setImageDrawable(context.getResources().getDrawable(m.getDarkIcon()));
            }

            tv.setTextColor(context.getResources().getColor(R.color.accent));
        }
        else{
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {
                Drawable d = context.getResources().getDrawable(m.getDarkIcon(), null);
            }
            else{
                icon.setImageDrawable(context.getResources().getDrawable(m.getDarkIcon()));
            }
            tv.setTextColor(context.getResources().getColor(R.color.text));
        }
        return view;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return items.length;
    }

    @Override
    public Mode getItem(int position) {
        // TODO Auto-generated method stub
        return items[position];
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }
}
