package com.algonquinlive.corm0096.doorsopenottawa.subtasks;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.algonquinlive.corm0096.doorsopenottawa.R;

/**
 * Created by DC on 2016-12-13.
 * A primitive means of gathering the information for a search (or permitting a search
 * to be cleared) for the user to filter Buildings.
 */

public class SearchActivity extends AppCompatActivity
{

    private TextView searchbar;
    private Button search, cancel;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_building);
        searchbar=(TextView)findViewById(R.id.searchEntry);
        cancel=(Button)findViewById(R.id.searchCancel);
        search=(Button)findViewById(R.id.searchButton);

        search.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (searchbar.getText().toString()==null)
                {
                    Toast.makeText(getApplicationContext(),
                            "Search must not be empty.", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Intent intent=new Intent();
                    intent.putExtra("searchterm",searchbar.getText().toString());
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Cancel out everything and return a cancel intent.
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });
    }

}
