package donate.cinek.wit.ie.ridetogether;

/**
 * Created by Cinek on 25/02/2015.
 */

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.List;

public class Trip extends AppCompatActivity {
    protected TextView StartCity;
    protected TextView DestinationCity, distanceTV, durationTV;
    protected Button TripDate;
    protected EditText TripTime;
    protected Button button,cancel;
    protected EditText TripName;
    protected String addressFrom, addressTo, distance, duration;
    protected byte[] bitmapData;
    Boolean triper=false;
    private Bitmap bitmap12;
    private boolean check1 = true, check2 = true;


    protected Button btnSelectDate, btnSelectTime;

    static final int DATE_DIALOG_ID = 0;
    static final int TIME_DIALOG_ID = 1;
    public int year, month, day, hour, minute;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private double startLat, finishLat, startLong, finishLong;


    public Trip() {
        {
            // Assign current Date and Time Values to Variables
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_trip);
        //super.set();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            addressFrom = extras.getString("mapDetails");
            addressTo = extras.getString("mapDetails2");
            startLat = extras.getDouble("originLat");
            startLong = extras.getDouble("originLong");
            finishLat = extras.getDouble("destLat");
            finishLong = extras.getDouble("destLong");
            distance = extras.getString("distance");
            duration = extras.getString("duration");

                   }
          Intent intent = getIntent();
        bitmap12 = intent.getParcelableExtra("screenshot");


          ImageView mImageView;
          mImageView = (ImageView)findViewById(R.id.planImage);

//set resource for imageview
          mImageView.setImageBitmap(bitmap12);

          Toast.makeText(Trip.this, addressTo,Toast.LENGTH_SHORT).show();


        btnSelectDate = (Button) findViewById(R.id.buttonSelectDate);
        btnSelectTime = (Button) findViewById(R.id.buttonSelectTime);


        btnSelectDate.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // Show the DatePickerDialog
                showDialog(DATE_DIALOG_ID);
            }
        });
        btnSelectTime.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // Show the TimePickerDialog
                showDialog(TIME_DIALOG_ID);
            }
        });


        StartCity = (TextView) findViewById(R.id.StartCity);
        DestinationCity = (TextView) findViewById(R.id.DestinationCity);
        distanceTV = (TextView) findViewById(R.id.distance_newtrip);
        durationTV = (TextView) findViewById(R.id.duration_newtrip);



        StartCity.setText("Start at: " + addressFrom);
        DestinationCity.setText("End at: " + addressTo);
        distanceTV.setText("Distance: " + distance);
        durationTV.setText("Duration: " + duration);



        TripName = (EditText) findViewById(R.id.trip_name);

        button = (Button) findViewById(R.id.CreateTrip);

        button.setOnClickListener(new View.OnClickListener() {
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
                                String dateBTN="";
                                if(btnSelectDate.getText().toString().equalsIgnoreCase("Select Date"))
                                {

                                    check1 = false;


                                }
                                else {
                                    if (btnSelectDate.getText().toString().length() == 25) {
                                        dateBTN = btnSelectDate.getText().toString().trim().substring(16, 25);
                                    } else {

                                        dateBTN = btnSelectDate.getText().toString().trim().substring(15, 24);
                                    }
                                }

                                Log.v(date, " " + dateBTN + " " + date);
                                if (date.equals(dateBTN))
                                {
                                    triper= true;
                                }
                                else
                                {

                                }
                                Log.v("tripper", " " + triper + " ");

                            }
                            if(triper==true)
                            {
                                Log.v("tripper", " szczenaaa");

                                Toast.makeText(Trip.this,"You have already signed up for a trip on this day",Toast.LENGTH_LONG).show();
                                triper=false;
                            }
                            else {
                                saveTrips();
                            }


                        } else {

                        }
                    }
                });

            }
        });

        cancel = (Button) findViewById(R.id.Cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent options = new Intent(Trip.this, Options.class);
                startActivity(options);

            }

        });
    }

    public void saveTrips() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        String startCity = StartCity.getText().toString().trim();
        String endCity = DestinationCity.getText().toString().trim();
        String tripName = TripName.getText().toString().trim();
        String date = btnSelectDate.getText().toString().trim();
        String time = btnSelectTime.getText().toString().trim();
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
        if (null != time && time.length() > 0) {

            if (time.length() > 12) {
                newstr2 = time.substring(15, time.length()); // not forgot to put check if(endIndex != -1)
            } else {

                check2 = false;
            }
        }

        if (check1 && check2) {

            ParseObject Trip = new ParseObject("Trip");
            Trip.put("TripName", tripName);
            if (addressFrom == null) {
                addressFrom = "Unknown";
            }
            if (addressTo == null) {
                addressTo = "Unknown";
            }
            Trip.put("StartCity", addressFrom);
            Trip.put("EndCity", addressTo);
            Trip.put("TripDate", newstr);
            Trip.put("TripTime", newstr2);
            Trip.put("TripDistance", distance);
            Trip.put("TripDuration", duration);

            if (currentUser != null) {
                Trip.put("CreatedbyUser", currentUser);
            } else {

                Intent backToMain = new Intent(Trip.this, MainActivity.class);
                startActivity(backToMain);
            }
            ParseGeoPoint point = new ParseGeoPoint(startLat, startLong);
            ParseGeoPoint point2 = new ParseGeoPoint(finishLat, finishLong);


//                Trip.saveInBackground();
            try {
                Trip.save();
            } catch (ParseException e) {
                e.printStackTrace();
            }


            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            Log.v("Bitmap in method:", "" + bitmap12.getByteCount());
            bitmap12.compress(Bitmap.CompressFormat.PNG, 100, stream);
            bitmapData = stream.toByteArray();


            ParseObject placeObject = new ParseObject("Geo");
            placeObject.put("tripID", Trip.getObjectId());
            placeObject.put("locationStart", point);
            placeObject.put("locationEnd", point2);


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
            placeObject.saveInBackground();
            Intent options = new Intent(Trip.this, Options.class);
            startActivity(options);


        }
        else {
            Toast.makeText(Trip.this,"Please select date & time of your trip",Toast.LENGTH_LONG).show();

            check1 = true;
            check2 = true;
        }
    }



    DatePickerDialog.OnDateSetListener mDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                // the callback received when the user "sets" the Date in the DatePickerDialog
                public void onDateSet(DatePicker view, int yearSelected,
                                      int monthOfYear, int dayOfMonth) {
                    year = yearSelected;
                    month = monthOfYear;
                    day = dayOfMonth;
                    // Set the Selected Date in Select date Button
                    btnSelectDate.setText("Date selected : " + day + "-" + month + "-" + year);
                }
            };
    TimePickerDialog.OnTimeSetListener mTimeSetListener =
            new TimePickerDialog.OnTimeSetListener() {
                // the callback received when the user "sets" the TimePickerDialog in the dialog
                public void onTimeSet(TimePicker view, int hourOfDay, int min) {
                    hour = hourOfDay;
                    minute = min;
                    // Set the Selected Date in Select date Button
                    btnSelectTime.setText("Time selected :" + hour + "-" + minute);
                }
            };

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                // create a new DatePickerDialog with values you want to show
                return new DatePickerDialog(this,
                        mDateSetListener,
                        mYear, mMonth, mDay);
            // create a new TimePickerDialog with values you want to show
            case TIME_DIALOG_ID:
                return new TimePickerDialog(this,
                        mTimeSetListener, mHour, mMinute, false);

        }
        return null;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_solo_trip_dipsplayer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.optionsO) {
            Intent back = new Intent(Trip.this, Options.class);
            startActivity(back);

            return true;
        } else if (id == R.id.optionsLogout) {
            ParseUser.logOut();
            Intent backToMain = new Intent(Trip.this, MainActivity.class);
            startActivity(backToMain);
            return true;

        }

        return super.onOptionsItemSelected(item);


    }
}
