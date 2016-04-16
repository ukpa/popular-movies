package me.unnikrishnanpatel.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by unnikrishnanpatel on 15/04/16.
 */
public class MovieAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<HashMap<String,String>> mData;

    public MovieAdapter(Context c, ArrayList<HashMap<String,String>> data) {
        mContext = c;
        mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);

            //imageView.setLayoutParams(new GridView.LayoutParams(187,255));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        Picasso.with(mContext).load(mData.get(position).get("poster_path")).into(imageView);
        return imageView;
    }
}
