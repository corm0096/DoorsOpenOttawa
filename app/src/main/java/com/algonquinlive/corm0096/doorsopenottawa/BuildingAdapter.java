package com.algonquinlive.corm0096.doorsopenottawa.model;

/**
 * Created by DC on 2016-11-08.
 */

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.algonquinlive.corm0096.doorsopenottawa.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
     * Purpose: customize the Planet cell for each planet displayed in the ListActivity (i.e. MainActivity).
     * Usage:
     *   1) extend from class ArrayAdapter<YourModelClass>
     *   2) @override getView( ) :: decorate the list cell
     *
     * Based on the Adapter OO Design Pattern.
     *
     * @author Gerald.Hurdle@AlgonquinCollege.com
     *
     * Reference: based on DisplayList in "Connecting Android Apps to RESTful Web Services" with David Gassner
     */
public class BuildingAdapter extends ArrayAdapter<Building>
{


    private Context context;
    private List<Building> BuildingList;

    public BuildingAdapter(Context context, int resource, List<Building> objects) {
        super(context, resource, objects);
        this.context = context;
        this.BuildingList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        //Extra variables for dates.
        String textData="";
        JSONArray calendar;
        //

        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_building, parent, false);

        //Display Building name in the TextView widget
        Building building = BuildingList.get(position);
        TextView tv = (TextView) view.findViewById(R.id.textView1);

        //Extra code for dates.
        calendar=building.getCalendar();
        for (int i=0;i<calendar.length();i++)
        {
            try
            {
                textData += "\n" + calendar.getJSONObject(i).getString("date");
            }
            catch (JSONException e)
            {
                // Nothing should be breaking at this point.
            }
        }
        //

        tv.setText(building.getName()+textData);
        Log.i("textdata",building.getName()+textData);
        return view;
    }
}
