package com.algonquinlive.corm0096.doorsopenottawa.model;

/**
 * Created by DC on 2016-11-08.
 */

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.algonquinlive.corm0096.doorsopenottawa.MainActivity;
import com.algonquinlive.corm0096.doorsopenottawa.R;
import com.algonquinlive.corm0096.doorsopenottawa.model.Building;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
     * Purpose: customize the Building cell for each building displayed in the ListActivity (i.e. MainActivity).
     * Usage:
     *   1) extend from class ArrayAdapter<YourModelClass>
     *   2) @override getView( ) :: decorate the list cell
     *
     * Based on the Adapter OO Design Pattern.
     *
     * @author Gerald.Hurdle@AlgonquinCollege.com
     * Modified from Planets to Building by Daniel Cormier (corm0096)
     *
     * Reference: based on DisplayList in "Connecting Android Apps to RESTful Web Services" with David Gassner
     */
public class BuildingAdapter extends ArrayAdapter<Building>
{
    private Context context;
    private List<Building> BuildingList;
    private ArrayList faves;

    public BuildingAdapter(Context context, int resource, List<Building> objects, ArrayList faves)
    {
        super(context, resource, objects);
        this.context = context;
        this.BuildingList = objects;
        this.faves=faves;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {

        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_building, parent, false);

        // Stripe the cells.
        if (position %2==1)
        {
            view.setBackgroundColor(0xFFCCCCFF);

        }
        else
        {
            view.setBackgroundColor(0xFFDDDDFF);
        }

        //Display Building name in the TextView widget
        Building building = BuildingList.get(position);
        TextView tv = (TextView) view.findViewById(R.id.textView);
        TextView stv = (TextView) view.findViewById(R.id.subTextView);
        ImageView fav = (ImageView)view.findViewById(R.id.favbutton);

        tv.setText(building.getName());
        stv.setText(building.getDates());
        if (faves.contains(building.getBuildingId()+""))
        {
            fav.setImageResource(android.R.drawable.star_big_on);
        }
        else
        {
            fav.setImageResource(android.R.drawable.star_big_off);
        }

        fav.setTag(building.getBuildingId()+"");

        if (building.getBitmap() != null)
        {
            Log.i("BUILDINGS", building.getName() + "\tbitmap in memory");
            ImageView image = (ImageView) view.findViewById(R.id.imageView1);
            image.setImageBitmap(building.getBitmap());
        }

        else
        {
            Log.i("BUILDINGS", building.getName() + "\tfetching bitmap using AsyncTask");
            BuildingAndView container = new BuildingAndView();
            container.building = building;
            container.view = view;

            ImageLoader loader = new ImageLoader();
            loader.execute(container);
        }
        return view;
    }


    private class BuildingAndView
    {
        public Building building;
        public View view;
        public Bitmap bitmap;
    }

    private class ImageLoader extends AsyncTask<BuildingAndView, Void, BuildingAndView>
    {

        @Override
        protected BuildingAndView doInBackground(BuildingAndView... params)
        {

            BuildingAndView container = params[0];
            Building building = container.building;

            try
            {
                String imageUrl = MainActivity.IMAGES_BASE_URL + building.getImage();
                InputStream in = (InputStream) new URL(imageUrl).getContent();

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize=2;
                Bitmap bitmap = BitmapFactory.decodeStream(in,null,options);
                building.setBitmap(bitmap);

                in.close();
                container.bitmap = bitmap;
                return container;
            }
            catch (Exception e)
            {
                Log.e("File not found: ",e.getMessage());
//                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(BuildingAndView result)
        {
            if (result !=null)
            {
                ImageView image = (ImageView) result.view.findViewById(R.id.imageView1);
                image.setImageBitmap(result.bitmap);
                result.building.setBitmap(result.bitmap);
            }
        }
    }
}