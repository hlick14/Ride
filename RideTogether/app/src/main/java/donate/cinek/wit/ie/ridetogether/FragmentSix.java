package donate.cinek.wit.ie.ridetogether;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.List;


/**
 * Created by Kuba Pieczonka on 05/11/2015.
 */
public class FragmentSix extends android.support.v4.app.Fragment
{

   TextView tv1,tv2,tv3,tv4;
   String start,end,date;
    String name;
    FileInputStream fis;
    String username ;
    String Motorbike ;
    String UserSince ;
    Date UserSince2;
    Bitmap bitmap ;
    private boolean check1 = true, check2 = true;
    String uSince;
    String Model,Engine;


    String m, m2;
    protected byte[] bitmapData;
    public FragmentSix() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }




    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        RelativeLayout ll = (RelativeLayout)inflater.inflate(R.layout.fragment_six, container, false);


//        StringBuilder sb = new StringBuilder();
//        Reader r = null;  //or whatever encoding
//        try {
//            r = new InputStreamReader(fis, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        int ch = 0;
//        try {
//            ch = r.read();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        while(ch >= 0) {
//            sb.append(ch);
//            try {
//                ch = r.read();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        sb.toString();
//
        Bundle extras = getActivity().getIntent().getExtras();


        if (extras != null) {


            start = extras.getString("mapDetails");
            end = extras.getString("mapDetails2");


        }
        SharedPreferences bb = getActivity().getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
         m = bb.getString("Date", "");

        SharedPreferences bb2 = getActivity().getSharedPreferences("my_prefs", this.getId());
         m2 = bb2.getString("Time", "");

//        if(FragmentSix.this.isVisible()) {
//            Bundle bundle = getArguments();
//            if (bundle != null) {

//                String date = bundle.getString("Date");
//                String time = bundle.getString("Time");
        String _data = DataHolderClass.getInstance().getDistributor_id();


                tv1 = (TextView) ll.findViewById(R.id.start);
                tv1.setText("Starting Point: " + start);
                tv2 = (TextView) ll.findViewById(R.id.end);
                tv2.setText("End Point :" + end);
                tv3 = (TextView) ll.findViewById(R.id.SelectedTime);
                tv3.setText("Date :" + _data);
                tv4 = (TextView) ll.findViewById(R.id.SelctedDate);
                tv4.setText("Time :" + m2);
//            }
//        }

        FloatingActionButton fab = (FloatingActionButton) ll.findViewById(R.id.fab);

/////////////////////////////////////////////////
        //adding the trip
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ParseUser currentUser = ParseUser.getCurrentUser();
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Trip");
                query.whereEqualTo("CreatedbyUser", currentUser);
                query.findInBackground(new FindCallback<ParseObject>() {
                    public void done(List<ParseObject> image, ParseException e) {
                        if (e == null) {
                            for (int i = 0; i < image.size(); i++) {
                                ParseObject object = image.get(i);
                                String date = (String) object.get("TripDate");

                            }
//                            if(triper==true)
//                            {
//                                Log.v("tripper", " szczenaaa");
//
//                                Toast.makeText(Trip.this, "You have already signed up for a trip on this day", Toast.LENGTH_LONG).show();
//                                triper=false;
//                            }
//                            else
                            {
                                saveTrips();
                            }


                        } else {

                        }
                    }
                });

            }
        });



        return ll;
    }
    public void saveTrips() {
        ParseUser currentUser = ParseUser.getCurrentUser();
//        String startCity = StartCity.getText().toString().trim();
//        String endCity = DestinationCity.getText().toString().trim();
//        String tripName = TripName.getText().toString().trim();
//        String date = btnSelectDate.getText().toString().trim();
//        String time = btnSelectTime.getText().toString().trim();
        String newstr = "";
        String newstr2 = "";
        //Getting the date and time value only

        if (null != date && date.length() > 0) {

            if (date.length() > 22) {
                if (date.length() == 25) {

                    newstr = date.substring(16, date.length()); // not forgot to put check if(endIndex != -1)
                } else {
                    newstr = date.substring(15, date.length()); // not forgot to put check if(endIndex != -1)
                }

            }
        }
        if (null != m && m.length() > 0) {

            if (m.length() > 12) {
//                newstr2 = time.substring(15, time.length()); // not forgot to put check if(endIndex != -1)
            } else {

                check2 = false;
            }
        }

        if (check1 && check2) {

            ParseObject Trip = new ParseObject("Trip");
//            Trip.put("TripName", tripName);
            if (start == null) {
                start = "Unknown";
            }
            if (end == null) {
                end = "Unknown";
            }
            Trip.put("StartCity", start);
            Trip.put("EndCity", end);
            Trip.put("TripDate", newstr);
            Trip.put("TripTime", newstr2);
//            Trip.put("TripDistance", distance);
//            Trip.put("TripDuration", duration);

            if (currentUser != null) {
                Trip.put("CreatedbyUser", currentUser);
            } else {

                Intent backToMain = new Intent(getActivity(), MainActivity.class);
                startActivity(backToMain);
            }
//            ParseGeoPoint point = new ParseGeoPoint(startLat, startLong);
//            ParseGeoPoint point2 = new ParseGeoPoint(finishLat, finishLong);


//                Trip.saveInBackground();
            try {
                Trip.save();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(getActivity().getApplicationContext()
                        .openFileInput("myImage"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Log.v("Bitmap in method:", "" + bitmap.getByteCount());
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            bitmapData = stream.toByteArray();


//            ParseObject placeObject = new ParseObject("Geo");
//            placeObject.put("tripID", Trip.getObjectId());
//            placeObject.put("locationStart", point);
//            placeObject.put("locationEnd", point2);


            ParseFile file1 = new ParseFile(Trip.getObjectId() + ".png", bitmapData);
            file1.saveInBackground();
            ParseObject imgupload1 = new ParseObject("TripImage");

            // Create a column named "ImageName" and set the string
            imgupload1.put("ImageName", "TripMap Picture");

            // Create a column named "ImageFile" and insert the image

            imgupload1.put("ImageFile", file1);

            imgupload1.put("TripID", Trip.getObjectId());

            ParseUser currentUser1 = ParseUser.getCurrentUser();
            if (currentUser != null) {
                imgupload1.put("CreatedbyUser", currentUser1);
            }

            // Create the class and the columns
            imgupload1.saveInBackground();
//            placeObject.saveInBackground();
            Intent options = new Intent(getActivity(), OneFragment.class);
            startActivity(options);


        }
        else {
//            Toast.makeText(Trip.this,"Please select date & time of your trip",Toast.LENGTH_LONG).show();

            check1 = true;
            check2 = true;
        }
    }






}


