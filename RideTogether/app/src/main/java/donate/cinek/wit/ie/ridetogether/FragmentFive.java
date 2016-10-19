package donate.cinek.wit.ie.ridetogether;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Created by Kuba Pieczonka on 05/11/2015.
 */
////////////////Usage of Time picker dialog was implemented by following a tutorial avialble at : http://wptrafficanalyzer.in/blog/displaying-timepickerdialog-using-dialogfragment-in-android-with-backward-compatibilty-support-library/
public class FragmentFive extends android.support.v4.app.Fragment
       {

           com.rey.material.widget.Button dateButton;
           com.rey.material.widget.Button timeButton;
           static final int DATE_DIALOG_ID = 0;
           static final int TIME_DIALOG_ID = 1;
           TextView tvDate,tvTime;
           Calendar c = Calendar.getInstance();
           int minutes = c.get(Calendar.MINUTE);
           int hours = c.get(Calendar.HOUR);
           int mHour = hours;
           int mMinute = minutes;
           Bundle aBundle;
           String timeToSend;
           String toSend;
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
           ArrayList<String> usersJoined;
           Intent options;
           ProgressDialog dialog;



           String m, m2;
           protected byte[] bitmapData;
            String tName="";
           Bundle bundle = new Bundle();



    public FragmentFive() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Fragment fragment = new Fragment();
        usersJoined =  new ArrayList<>();


    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        RelativeLayout ll = (RelativeLayout)inflater.inflate(R.layout.fragment_five, container, false);

        final TextInputLayout tripNameWrapper = (android.support.design.widget.TextInputLayout) ll.findViewById(R.id.tripNameWrapper);

        tripNameWrapper.setHint("Trip Name");

        tvDate= (TextView)ll.findViewById(R.id.tempTextView);
        tvTime= (TextView)ll.findViewById(R.id.tempTextView2);
        dateButton=(com.rey.material.widget.Button)ll.findViewById(R.id.buttonSelectDate);
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDatePicker();


            }
        });

        /////////////TIme
        Handler mHandler = new Handler(){
            @Override
            public void handleMessage(Message m){
                /** Creating a bundle object to pass currently set Time to the fragment */
                Bundle b = m.getData();

                /** Getting the Hour of day from bundle */
                mHour = b.getInt("set_hour");

                /** Getting the Minute of the hour from bundle */
                mMinute = b.getInt("set_minute");

                /** Displaying a short time message containing time set by Time picker dialog fragment */
//                   Toast.makeText(getBaseContext(), b.getString("set_time"), Toast.LENGTH_SHORT).show();
            }
        };
        timeButton=(com.rey.material.widget.Button)ll.findViewById(R.id.buttonSelectTime);
        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(getActivity(), "Something New", Toast.LENGTH_SHORT).show();

                /** Creating a bundle object to pass currently set time to the fragment */
                Bundle b = new Bundle();

                /** Adding currently set hour to bundle object */
                b.putInt("set_hour", mHour);

                /** Adding currently set minute to bundle object */
                b.putInt("set_minute", mMinute);

                /** Instantiating TimePickerDialogFragment */
                TimePickerDialogFragment timePicker = new TimePickerDialogFragment();

                /** Setting the bundle object on timepicker fragment */
                timePicker.setArguments(b);

                /** Getting fragment manger for this activity */
                FragmentManager fm = getChildFragmentManager();

                /** Starting a fragment transaction */
                FragmentTransaction ft = fm.beginTransaction();

                /** Adding the fragment object to the fragment transaction */
                ft.add(timePicker, "time_picker");

                /** Opening the TimePicker fragment */
                ft.commit();
                //        tvTime.setText("Trip Time: "+ mHandler.getMessageName(Message.obtain()));

            }
        });




        /** Getting an instance of Set button */


        /** Setting click event listener for the button */
//        tvTime.setText("Trip Time: "+ mHandler.getMessageName(Message.obtain()));
        timeToSend = mHour+ "" + mMinute + "";


        FloatingActionButton fab = (FloatingActionButton) ll.findViewById(R.id.fab);

/////////////////////////////////////////////////
        //adding the trip
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tName = tripNameWrapper.getEditText().getText().toString().trim();

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


                                if(toSend == null)
                                {

                                    Toast.makeText(getActivity(), "Please select a date of your trip!", Toast.LENGTH_SHORT).show();

                                }
                                if (timeToSend == null){
                                    Toast.makeText(getActivity(), "Please select the time of your trip!", Toast.LENGTH_SHORT).show();

                            }
                                else{

                                saveTrips();
                            }
                            }


                        } else {

                        }
                    }
                });

            }
        });






        return ll;
    }
           private void showDatePicker() {
               DatePickerFragment date = new DatePickerFragment();
               /**
                * Set Up Current Date Into dialog
                */
               Calendar calender = Calendar.getInstance();
               Bundle args = new Bundle();
               args.putInt("year", calender.get(Calendar.YEAR));
               args.putInt("month", calender.get(Calendar.MONTH));
               args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
               date.setArguments(args);
               /**
                * Set Call back to capture selected date
                */
               date.setCallBack(ondate);
               date.show(getActivity().getSupportFragmentManager(), "Date Picker");

           }

           DatePickerDialog.OnDateSetListener ondate = new DatePickerDialog.OnDateSetListener() {
               @Override
               public void onDateSet(DatePicker view, int year, int monthOfYear,
                                     int dayOfMonth) {

                   tvDate.setText("Trip Date: " + String.valueOf(dayOfMonth) + "-" + String.valueOf(monthOfYear) + "-" + String.valueOf(year));
                    toSend= ( String.valueOf(dayOfMonth) + "-" + String.valueOf(monthOfYear) + "-" + String.valueOf(year));



               }
           };



           public void saveTrips() {
               ParseUser currentUser = ParseUser.getCurrentUser();

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

                   ParseObject TripParse = new ParseObject("Trip");
                   Bundle extras = getActivity().getIntent().getExtras();
                   String start = extras.getString("mapDetails");
                   String end = extras.getString("mapDetails2");
//            Trip.put("TripName", tripName);
                   if (start == null) {
                       start = "Unknown";
                   }
                   if (end == null) {
                       end = "Unknown";
                   }
                   TripParse.put("TripName", tName);
                   TripParse.put("StartCity", start);
                   TripParse.put("EndCity", end);
                   TripParse.put("TripDate", toSend);
                   TripParse.put("TripTime", timeToSend);
                   Bundle bundle = this.getArguments();
                   String temp;

                   temp=bundle.getString("TripType");
                   //temp to show!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    temp="s";
                   Log.d("HERE", "saveTrips: "+ temp);
                   TripParse.put("TripType",(temp));




                   TripParse.put("CreatedbyUser", currentUser);
                   usersJoined.add(currentUser.getUsername());
                   TripParse.add("JoinedUsers", usersJoined);
                   TripParse.put("JoinedUsers",usersJoined);

                   try {
                       TripParse.save();
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
//                   Log.v("Bitmap in method:", "" + bitmap.getByteCount());
//                   bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                   bitmapData = stream.toByteArray();




                   ParseFile file1 = new ParseFile(TripParse.getObjectId() + ".png", bitmapData);
                   file1.saveInBackground();
                   ParseObject imgupload1 = new ParseObject("TripImage");

                   // Create a column named "ImageName" and set the string
                   imgupload1.put("ImageName", "TripMap Picture");

                   // Create a column named "ImageFile" and insert the image

                   imgupload1.put("ImageFile", file1);

                   imgupload1.put("TripID", TripParse.getObjectId());
                   imgupload1.put("TripType",(temp));

                   ParseUser currentUser1 = ParseUser.getCurrentUser();
                   if (currentUser != null) {
                       imgupload1.put("CreatedbyUser", currentUser1);
                   }

                   // Create the class and the columns
                   imgupload1.saveInBackground();
//            placeObject.saveInBackground();
                   options = new Intent(getActivity(), Options.class);
                   options.putExtra("AddedNewTrip", true);
                   final Handler handler = new Handler();
                   dialog = new ProgressDialog(this.getActivity(), 1);
                   dialog.setMessage("Saving Your Trip");
                   dialog.setCancelable(false);
                   dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                   dialog.show();
                   handler.postDelayed(new Runnable() {
                       @Override
                       public void run() {
                           if (dialog.isShowing()) {
                               dialog.dismiss();
                           }
                           startActivity(options);
                       }
                   }, 2500);



               }
               else {

                   check1 = true;
                   check2 = true;
               }
           }






       }


