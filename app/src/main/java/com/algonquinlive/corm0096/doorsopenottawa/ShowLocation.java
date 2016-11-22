package com.algonquinlive.corm0096.doorsopenottawa;
/*
Show Location activity inflates a secondary view to display particulars on a building,
using Google Maps API to locate that building on the map.
Code by Daniel Cormier (corm0096)
*/
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.algonquinlive.corm0096.doorsopenottawa.model.Building;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Locale;

public class ShowLocation extends FragmentActivity implements OnMapReadyCallback
{

    private GoogleMap mMap;
    private String addr;
    private android.location.Geocoder mGeocoder;
    private TextView mTitle, mDates, mDesc;
    private Button mBtn;

    //DC: Useful if sending/receiving byte arrays can work.
    //private ImageView mImg;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_details);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mGeocoder = new Geocoder( this, Locale.CANADA );
        mDates=(TextView)findViewById(R.id.detailHours);
        mTitle=(TextView)findViewById(R.id.detailTitle);
        mDesc=(TextView)findViewById(R.id.detailDescription);
        mBtn=(Button)findViewById(R.id.returnBtn);

//        mImg=(ImageView)findViewById(R.id.detailImg);
//        DC: Image removed from XML.  May return later.

        Bundle bundle = getIntent().getExtras();
        if ( bundle != null )
        {
            Log.d("DATES",bundle.getString("dates"));
            mDates.setText("\n"+bundle.getString("dates"));
            mTitle.setText(bundle.getString("title"));
            mDesc.setText(bundle.getString("details"));
            addr= bundle.getString("addr");


// DC: Code intended to convert byte array to bmp.
//            byte[] byteArray = getIntent().getByteArrayExtra("bmp");
//            mImg.setImageBitmap(BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length));

            mBtn.setOnClickListener(
                    new View.OnClickListener()
                    {
                        public void onClick(View v)
                        {
                            finish();
                        }

                    }
            );
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        pin(addr);

    }

        //pin functionality by Gerry Hurdleg.

    private void pin( String locationName )
    {
        Log.e("ADDRESS",locationName);
        try {
            android.location.Address address = mGeocoder.getFromLocationName(locationName, 1).get(0);
            LatLng ll = new LatLng( address.getLatitude(), address.getLongitude() );

            mMap.addMarker( new MarkerOptions().position(ll).title(locationName) );
            mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(ll,14f) );
            Toast.makeText(this, "Pinned: " + locationName, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Not found: " + locationName, Toast.LENGTH_LONG).show();
        }
    }
}
