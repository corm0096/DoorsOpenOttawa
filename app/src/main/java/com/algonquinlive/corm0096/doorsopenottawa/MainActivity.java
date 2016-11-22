package com.algonquinlive.corm0096.doorsopenottawa;
/*
Main Activity by Daniel Cormier (corm0096), modified from Gerry Hurdleg's planets code.
Handles logic, sets up MVC, reads buttons in the main view.
*/

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.app.ListActivity;

import com.algonquinlive.corm0096.doorsopenottawa.model.Building;
import com.algonquinlive.corm0096.doorsopenottawa.parsers.BuildingJSONParser;
import com.algonquinlive.corm0096.doorsopenottawa.parsers.HttpManager;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ListActivity implements AdapterView.OnItemClickListener
{

    public static final String REST_URI = "https://doors-open-ottawa-hurdleg.mybluemix.net/buildings";
    public static final String IMAGES_BASE_URL = "https://doors-open-ottawa-hurdleg.mybluemix.net/";

    private ProgressBar pb;
    private List<MyTask> tasks;

    private List<Building> buildingList;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pb = (ProgressBar) findViewById(R.id.progressBar1);
        pb.setVisibility(View.INVISIBLE);

        tasks = new ArrayList<>();

        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        getListView().setOnItemClickListener(this);

        //Launch!

       // if (isOnline())  //This version hates Samsung phones, or vice versa.
        // Trying to mitigate status spam from Samsung connection logs.
        if (hasNetworkConnection())
        {
            Log.d("Status", "Connected");
            requestData( REST_URI );
        }
        else
        {
            Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
            Log.d("Status", "Not Connected");
        }

    }

    private boolean hasNetworkConnection()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnected();
        return isConnected;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    //Code to handle selecting a list item.
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Building theSelectedBuilding = buildingList.get( position );

        Intent intent = new Intent( getApplicationContext(), ShowLocation.class );
        intent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
        intent.putExtra( "addr", theSelectedBuilding.getAddress() );
        intent.putExtra( "details",theSelectedBuilding.getDescription());
        intent.putExtra( "dates",theSelectedBuilding.getDates());
        intent.putExtra( "title",theSelectedBuilding.getName());

//  Snippet from Stackoverflow to convert an image to a byte array.

//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        theSelectedBuilding.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, stream);
//        byte[] byteArray = stream.toByteArray();
// DC: Not required when I can't send this byteArray.  May reimplement later.

 //       intent.putExtra( "bmp",byteArray);
//DC: I can't seem to send this byteArray without an error. The images are too large to put on the
// heap.  Should consider processing images down.  Removed as it wasn't required in the original
// design spec anyway.

        startActivity( intent );
     }


    private void requestData(String uri)
    {
        MyTask task = new MyTask();
        Log.d("Status", "Request Data");
        task.execute(uri);
    }


    protected void updateDisplay()
    {
        //Use BuildingAdapter to display data
        BuildingAdapter adapter = new BuildingAdapter(this, R.layout.item_building, buildingList);
        setListAdapter(adapter);
    }

    protected boolean isOnline()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    private class MyTask extends AsyncTask<String, String, String>
    {

        @Override
        protected void onPreExecute() {
            if (tasks.size() == 0) {
                pb.setVisibility(View.VISIBLE);
            }
            Log.d("Status", "MyTask");
            tasks.add(this);
        }

        @Override
        protected String doInBackground(String... params) {

            Log.d("Status","Getting data");
            String content = HttpManager.getData(params[0]);
            Log.d("Status","Got data");
            buildingList = BuildingJSONParser.parseFeed(content);
            Log.d("Status","Parsed data");

            return content;
        }

        @Override
        protected void onPostExecute(String result) {

            tasks.remove(this);
            if (tasks.size() == 0) {
                pb.setVisibility(View.INVISIBLE);
            }

            if (result == null) {
                Toast.makeText(MainActivity.this, "Web service not available", Toast.LENGTH_LONG).show();
                return;
            }

            buildingList = BuildingJSONParser.parseFeed(result);
            updateDisplay();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id== R.id.action_about)
        {
            DialogFragment newFragment = new AboutDialogFragment();
            newFragment.show(getFragmentManager(), "About Dialog");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

