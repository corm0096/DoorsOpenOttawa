package com.algonquinlive.corm0096.doorsopenottawa.parsers;

        import android.util.Log;

        import com.algonquinlive.corm0096.doorsopenottawa.model.Building;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.util.ArrayList;
        import java.util.List;

/**
 * Parse a JSON object for a Planet.
 *
 * //TODO: compare this parser to JSON array: https://planets-hurdleg.mybluemix.net/planets
 *
 * @author Gerald.Hurdle@AlgonquinCollege.com
 *
 * Reference: FlowerJSONParser in "Connecting Android Apps to RESTful Web Services" with David Gassner
 */
public class BuildingJSONParser {

    public static List<Building> parseFeed(String content) {

        try {
            JSONObject jsonResponse = new JSONObject(content);
            JSONArray buildingArray = jsonResponse.getJSONArray("buildings");
            List<Building> buildingList = new ArrayList<>();

            for (int i = 0; i < buildingArray.length(); i++) {

                JSONObject obj = buildingArray.getJSONObject(i);
                Building building = new Building();

                building.setBuildingId(obj.getInt("buildingId"));
                building.setName(obj.getString("name"));
                building.setImage(obj.getString("image"));
                building.setAddress(obj.getString("address"));
                building.setCalendar(obj.getJSONArray("calendar"));

                buildingList.add(building);
            }

            return buildingList;
        }
        catch (JSONException e)
        {
            Log.e("TAG","JsonParser",e);

            return null;
        }
    }
}
