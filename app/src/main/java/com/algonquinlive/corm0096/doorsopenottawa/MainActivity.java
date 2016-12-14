package com.algonquinlive.corm0096.doorsopenottawa;
/*
Main Activity by Daniel Cormier (corm0096), modified from Gerry Hurdleg's planets code.
Handles logic, sets up MVC, reads buttons in the main view, and does a lot of other work
besides.  Notes on major chunks to follow below.
*/

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;
import android.app.ListActivity;

import com.algonquinlive.corm0096.doorsopenottawa.HTTPManagement.HttpManager;
import com.algonquinlive.corm0096.doorsopenottawa.HTTPManagement.HttpMethod;
import com.algonquinlive.corm0096.doorsopenottawa.HTTPManagement.RequestPackage;
import com.algonquinlive.corm0096.doorsopenottawa.model.Building;
import com.algonquinlive.corm0096.doorsopenottawa.model.BuildingAdapter;
import com.algonquinlive.corm0096.doorsopenottawa.parsers.BuildingJSONParser;
import com.algonquinlive.corm0096.doorsopenottawa.subtasks.EditBuildingActivity;
import com.algonquinlive.corm0096.doorsopenottawa.subtasks.NewBuildingActivity;
import com.algonquinlive.corm0096.doorsopenottawa.subtasks.SearchActivity;
import com.algonquinlive.corm0096.doorsopenottawa.subtasks.ShowBuildingActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends ListActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener
        //, AdapterView.OnTouchListener
{

    public static final String REST_URI = "https://doors-open-ottawa-hurdleg.mybluemix.net/buildings";
    public static final String IMAGES_BASE_URL = "https://doors-open-ottawa-hurdleg.mybluemix.net/";
    public static final String CLOSE_URL = "https://doors-open-ottawa-hurdleg.mybluemix.net/users/logout";
    public static final int ACTIVITY_NEW = 1, ACTIVITY_EDIT=2, ACTIVITY_SHOW=3, ACTIVITY_SEARCH=4;


    private ProgressBar pb;
    private List<GetTask> tasks;
    private List<Building> buildingList, buildingListIntact;

    private SwipeRefreshLayout mySwipeRefreshLayout;
    private Building gotBuilding;
    private ArrayList favBuildingList = new ArrayList();

    private RadioButton ascItem;

    private String searchTerm="";
    private boolean customSearch=false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ascItem=(RadioButton) findViewById(R.id.action_sort_id_asc);

        buildingListIntact=new ArrayList();
        SharedPreferences settings =   PreferenceManager.getDefaultSharedPreferences(this);

        //Load up favourite buildings:
        int size = settings.getInt("Status_size", 0);
        Log.i("WRITING IN DATA",size+"");
        for(int i=0;i<size;i++)
        {
            favBuildingList.add(settings.getString("Status_" + i, null));
        }

        pb = (ProgressBar) findViewById(R.id.progressBar1);
        pb.setVisibility(View.INVISIBLE);

        tasks = new ArrayList<>();

        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        getListView().setOnItemClickListener(this);
        getListView().setOnItemLongClickListener(this);

        // Functionality to set up Swipe refreshing.

        mySwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mySwipeRefreshLayout.setOnRefreshListener(
            new SwipeRefreshLayout.OnRefreshListener()
            {
                @Override
                public void onRefresh()
                {
                    Log.i("refresh", "onRefresh called from SwipeRefreshLayout");

                    // This method performs the actual data-refresh operation.
                    // The method calls setRefreshing(false) when it's finished.
                    reloadData();
                }
            }
        );
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
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Building theSelectedBuilding = buildingList.get(position);
        Log.i("Coordinate",view.getX()+"");
        Intent intent = new Intent(getApplicationContext(), ShowBuildingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("addr", theSelectedBuilding.getAddress());
        intent.putExtra("details", theSelectedBuilding.getDescription());
        intent.putExtra("dates", theSelectedBuilding.getDates());
        intent.putExtra("title", theSelectedBuilding.getName());
        intent.putExtra("faved",favBuildingList.contains(theSelectedBuilding.getBuildingId()+""));
        intent.putExtra("id",theSelectedBuilding.getBuildingId());

        startActivityForResult(intent,ACTIVITY_SHOW);
    }

    //Code to permit editing an item through long clicks.
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
    {
        Building theSelectedBuilding = buildingList.get(position);

        Intent intent = new Intent(getApplicationContext(), EditBuildingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("addr", theSelectedBuilding.getAddress());
        intent.putExtra("desc", theSelectedBuilding.getDescription());
        intent.putExtra("title",theSelectedBuilding.getName());
        intent.putExtra("id",theSelectedBuilding.getBuildingId());

        startActivityForResult(intent,ACTIVITY_EDIT);
        return false;
    }

    private void requestData(String uri)
    {
        RequestPackage getPackage = new RequestPackage();
        getPackage.setMethod( HttpMethod.GET );
        getPackage.setUri( uri );
        GetTask getTask = new GetTask();
        getTask.execute( getPackage );
    }


    protected void updateDisplay()
    {
        //Use BuildingAdapter to display data
        Log.e("size?",buildingList.size()+"");
        BuildingAdapter adapter = new BuildingAdapter(this, R.layout.item_building, buildingList, favBuildingList);
        setListAdapter(adapter);
    }

    protected boolean isOnline()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting())
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    //Major bit of code that will handle return Intents from the various "subtask"
    //Activities.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == ACTIVITY_NEW && resultCode == RESULT_OK) //New Building
        {
            Bundle result = data.getExtras();
            RequestPackage pkg = new RequestPackage();
            pkg.setMethod( HttpMethod.POST );
            pkg.setUri( REST_URI );
            pkg.setParam("address",result.getString("addr"));
            pkg.setParam("image", result.getString("image") );
            pkg.setParam("description", result.getString("desc") );
            pkg.setParam("name", result.getString("title") );
            DoTask postTask = new DoTask();
            postTask.execute( pkg );
        }

        if (requestCode == ACTIVITY_EDIT && resultCode == RESULT_OK)
        {
            RequestPackage pkg = new RequestPackage();
            Bundle result = data.getExtras();
            pkg.setUri(REST_URI+"/"+result.getInt("id"));
            Log.i("Deleted?",result.getBoolean("delete")+"");
            if (result.getBoolean("delete"))
            {
                pkg.setMethod(HttpMethod.DELETE);
            }
            else
            {
                pkg.setMethod(HttpMethod.PUT);
                pkg.setParam("address", result.getString("addr"));
                pkg.setParam("description", result.getString("desc"));
            }
            DoTask postTask = new DoTask();
            postTask.execute(pkg);
        }
        if (requestCode == ACTIVITY_SHOW && resultCode == RESULT_OK)
        {
            Bundle result = data.getExtras();
            String favId=result.getInt("id")+"";
            boolean faved=result.getBoolean("faved");
            if (!faved)
            {
                if (favBuildingList.contains(favId))
                {
                    favBuildingList.remove(favId+"");
                    ((ArrayAdapter)getListAdapter()).notifyDataSetChanged();
                }
            }
            else
            {
                if (!favBuildingList.contains(favId))
                {
                    favBuildingList.add(favId);
                    ((ArrayAdapter)getListAdapter()).notifyDataSetChanged();
                }
            }
        }
        if (requestCode==ACTIVITY_SEARCH)
        {
            if (resultCode==RESULT_CANCELED)
            {
                Log.i("SEARCH CANCEL","SEARCH CANCELED!");
                customSearch=false;
                searchTerm="";

                buildingList.removeAll(buildingList);
                for (int i=0;i<buildingListIntact.size();i++)
                {
                    buildingList.add(buildingListIntact.get(i));
                }

                ((ArrayAdapter)getListAdapter()).notifyDataSetChanged();

            }
            else
            {

                Bundle result = data.getExtras();
                searchTerm=result.getString("searchterm");
                customSearch=true;
                buildingList.removeAll(buildingList);
                Log.i("SEARCH TERM",searchTerm);
                for (int i=0;i<buildingListIntact.size();i++)
                {
                    if(buildingListIntact.get(i).getName().contains(searchTerm))
                    {
                        buildingList.add(buildingListIntact.get(i));
                    }
                }
                Log.i("RESULT LENGTH",buildingList.size()+"");
                ((ArrayAdapter)getListAdapter()).notifyDataSetChanged();
            }
        }
    }

    //Code to handle the Action Menu items: searching, reloading, sorting.
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_get_data:
            {
                reloadData();
                break;
            }
            case R.id.action_post_data:
            {
                if (isOnline())
                {
                    createBuilding();
                }
                else
                {
                    Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
                }
                break;
            }
            case R.id.action_sort_name_asc:
            {
                if ( buildingList == null ) {break;}
                Collections.sort(buildingList, new Comparator<Building>()
                {
                    @Override
                    public int compare(Building lhs, Building rhs)
                    {
                        Log.i("BUILDINGS!!", "Sorting 'em a-z");
                        return lhs.getName().compareTo(rhs.getName());
                    }
                });
                item.setChecked(true);
                ((ArrayAdapter)getListAdapter()).notifyDataSetChanged();
                break;
            }

            case R.id.search:
            {
                Intent newIntent=new Intent( getApplicationContext(),SearchActivity.class);
                newIntent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
                startActivityForResult(newIntent,ACTIVITY_SEARCH);
                break;
            }

            case R.id.action_sort_name_dsc:
            {
                if ( buildingList == null ) {break;}
                Collections.sort(buildingList, Collections.reverseOrder(new Comparator<Building>()
                {
                    @Override
                    public int compare(Building lhs, Building rhs)
                    {
                        Log.i("BUILDINGS", "Sorting 'em (z-a)");
                        return lhs.getName().compareTo(rhs.getName());
                    }
                }));
                item.setChecked(true);
                ((ArrayAdapter)getListAdapter()).notifyDataSetChanged();
                break;
            }

            case R.id.action_sort_id_asc:
            {
                if ( buildingList == null ) {break;}
                Collections.sort(buildingList, new Comparator<Building>()
                {
                    @Override
                    public int compare(Building lhs, Building rhs)
                    {
                        Log.i("BUILDINGS", "Sorting 'em by id (asc)");
                        return (lhs.getBuildingId()<rhs.getBuildingId())?-1:1;
                    }
                });
                item.setChecked(true);
                ((ArrayAdapter)getListAdapter()).notifyDataSetChanged();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void createBuilding()
    {

        //Create intent to NewBuildingActivity, retrieve data.

        Intent newIntent=new Intent( getApplicationContext(),NewBuildingActivity.class);

        newIntent.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );
        startActivityForResult(newIntent,ACTIVITY_NEW);
    }


    protected void reloadData()
    {
        if (hasNetworkConnection())
        {
            Log.d("Status", "Connected");
            requestData(REST_URI);
            mySwipeRefreshLayout.setRefreshing(false);
            if (ascItem!=null) {ascItem.setChecked(true);}
        }
        else
        {
            Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
            Log.d("Status", "Not Connected");
        }
    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        requestData(CLOSE_URL);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor mEdit1 = sp.edit();
//        mEdit1.clear(); //We're not storing anything else, this prevents bloat.
        mEdit1.putInt("Status_size", favBuildingList.size());
        Log.i("WRITING OUT DATA",favBuildingList.size()+"");
        for(int i=0;i<favBuildingList.size();i++)
        {
            mEdit1.remove("Status_" + i);
            mEdit1.putString("Status_" + i, favBuildingList.get(i).toString());
        }
        mEdit1.commit();
    }


    private class DoTask extends AsyncTask<RequestPackage, String, String>
    {

        @Override
        protected void onPreExecute()
        {
            pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(RequestPackage... params)
        {

            String content = HttpManager.getData(params[0]);
            return content;
        }

        @Override
        protected void onPostExecute(String result)
        {

            pb.setVisibility(View.INVISIBLE);

            if (result == null)
            {
                Toast.makeText(MainActivity.this, "Web service not available", Toast.LENGTH_LONG).show();
                return;
            }
        }
    }

    private class GetTask extends AsyncTask<RequestPackage, String, String> {

        @Override
        protected void onPreExecute() {
            if (tasks.size() == 0) {
                pb.setVisibility(View.VISIBLE);
            }
            tasks.add(this);
        }

        @Override
        protected String doInBackground(RequestPackage ... params) {

            String content = HttpManager.getData(params[0]);
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
            if (buildingListIntact!=null)
            {
                buildingListIntact.clear();
            }

            for (int i=0;i<buildingList.size();i++)
            {
                buildingListIntact.add(buildingList.get(i));
            }
            updateDisplay();
        }
    }
}