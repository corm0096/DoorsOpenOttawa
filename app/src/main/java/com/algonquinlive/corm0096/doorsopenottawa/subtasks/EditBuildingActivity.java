package com.algonquinlive.corm0096.doorsopenottawa.subtasks;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.algonquinlive.corm0096.doorsopenottawa.R;

/**
 * Created by DC on 2016-12-12.
 * This Activity presents a Building to be edited by the user, returning the data
 * to MainActivity in an Intent. It also permits the deletion of a building.
 * Almost all of the code is plumbing.
 */

public class EditBuildingActivity extends AppCompatActivity
{
    private EditText address, description;
    private TextView title;
    private Button cancel, save;
    private CheckBox checkdel;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setContentView(R.layout.edit_building);
        //Hook up the plumbing.
        title = (TextView) findViewById(R.id.edit_title_input);
        address = (EditText) findViewById(R.id.edit_address_input);
        description = (EditText) findViewById(R.id.edit_description_input);
        save = (Button) findViewById(R.id.edit_save);
        cancel = (Button) findViewById(R.id.edit_cancel);
        checkdel = (CheckBox) findViewById(R.id.checkDelete);

        final Bundle bundle = getIntent().getExtras();
        title.setText(bundle.getString("title"));
        address.setText(bundle.getString("addr"));
        description.setText(bundle.getString("desc"));

        checkdel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(checkdel.isChecked())
                {
                    save.setText(getResources().getString(R.string.delete));
                }
                else
                {
                    save.setText(getResources().getString(R.string.save));
                }
            }
        });

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
                String addressText = address.getText().toString();
                String descText = description.getText().toString();

                Intent intent = new Intent();


                if (addressText.isEmpty() || descText.isEmpty())
                {
                    Toast.makeText(getApplicationContext(),
                            "All fields must have data.", Toast.LENGTH_LONG).show();
                }
                else
                {
                    intent.putExtra("addr", addressText);
                    intent.putExtra("desc", descText);
                    intent.putExtra("delete",checkdel.isChecked());
                    intent.putExtra("id",bundle.getInt("id"));
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }
        });
    }
}
