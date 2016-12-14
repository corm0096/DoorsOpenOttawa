package com.algonquinlive.corm0096.doorsopenottawa.subtasks;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.algonquinlive.corm0096.doorsopenottawa.R;

/**
 * Created by DC on 2016-12-12.
 * This Activity permits the user to create a new building.  It returns the
 * data back to MainActivity with an Intent.
 */

public class NewBuildingActivity extends AppCompatActivity
{
    private EditText title, address, image, description;
    private Button cancel, save;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setContentView(R.layout.new_building);
        //Hook up the plumbing.
        title=(EditText)findViewById(R.id.new_title_input);
        address=(EditText)findViewById(R.id.new_address_input);
        image=(EditText)findViewById(R.id.new_image_input);
        description=(EditText)findViewById(R.id.new_description_input);
        save=(Button)findViewById(R.id.new_save);
        cancel=(Button)findViewById(R.id.new_cancel);



        //Need click handling:
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

        save.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Check the data:  Nothing should be empty.
                String titleText=title.getText().toString();
                String addressText=address.getText().toString();
                String imageText=address.getText().toString();
                String descText=description.getText().toString();

                Intent intent=new Intent();


                if (titleText.isEmpty() ||  addressText.isEmpty() || imageText.isEmpty() || descText.isEmpty())
                {
                    Toast.makeText( getApplicationContext(),
                            "All fields must have data." , Toast.LENGTH_LONG ).show();
                }
                else
                {
                    intent.putExtra( "addr", addressText );
                    intent.putExtra( "image", imageText );
                    intent.putExtra( "desc", descText );
                    intent.putExtra( "title", titleText );
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }
        });

    }
}
