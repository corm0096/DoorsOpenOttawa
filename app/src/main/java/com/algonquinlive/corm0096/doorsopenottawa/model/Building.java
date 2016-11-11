package com.algonquinlive.corm0096.doorsopenottawa.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by DC on 2016-11-08.
 */

public class Building
{
    private int buildingId;
    private String name, address, image;
    private JSONArray calendar=new JSONArray();

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

    public void setCalendar(JSONArray calendar) {this.calendar=calendar;}

    public JSONArray getCalendar() {return calendar;}
}
