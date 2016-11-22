package com.algonquinlive.corm0096.doorsopenottawa.model;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by DC on 2016-11-08.
 * POJO for containing building information.
 */

public class Building
{
    private int buildingId;
    private String name, address, image, dates, description;
    private JSONArray calendar=new JSONArray();
    private Bitmap bitmap;

    public int getBuildingId()
    {
        return buildingId;
    }

    public void setBuildingId(int buildingId)
    {
        this.buildingId = buildingId;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(String address)
    {
        this.address = address + " Ottawa, Ontario";
    }

    public String getImage()
    {
        return image;
    }

    public void setImage(String image)
    {
        this.image = image;
    }

    public void setCalendar(JSONArray calendar)
    {
        this.calendar=calendar;
        dates="";
        for (int i = 0; i < calendar.length(); i++)
        {
            try
            {
                if (i>0){dates+="\n";} //Carriage returns for multi-line entries.

                dates += calendar.getJSONObject(i).getString("date");
            } catch (JSONException e)
            {
                // Nothing should be breaking at this point.
            }
        }
    }

    public JSONArray getCalendar() {return calendar;}
    public String getDates() {return dates;}
    public Bitmap getBitmap() { return bitmap; }
    public void setBitmap(Bitmap bitmap) { this.bitmap = bitmap; }
    public String getDescription() {return description;}
    public void setDescription(String description){this.description=description;}
}
