package donate.cinek.wit.ie.ridetogether;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class popup2 extends AppCompatActivity {
    Button eConfirm,eDelete,eJoin;
    String sId,tName,tTime,tDate,userId;
    TextView time,date;
    private ListView usersListView;
    private ArrayAdapter<String> namesArrayAdapter;
    List <String> JoinedUsers;

    String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup2);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .8), (int) (height * .6));
        final TextInputLayout update = (TextInputLayout) findViewById(R.id.UpdateNameWrapper);
        JoinedUsers = new ArrayList<>();

        eDelete = (Button) this.findViewById(R.id.eDelete2);
        eConfirm = (Button) this.findViewById(R.id.eConfirm2);
        eJoin = (Button) this.findViewById(R.id.eJoin2);

        time = (TextView)findViewById(R.id.tTime);
        date = (TextView)findViewById(R.id.tDate);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            sId = extras.getString("ID");
            tName = extras.getString("Name");
            tDate = extras.getString("Date");
            tTime = extras.getString("Time");
            userId = extras.getString("UserId");
            update.setHint(tName);
            date.setText("Trip Date : " + tDate);
            time.setText("Trip Time : " + tTime);

            final ParseQuery<ParseObject> query = ParseQuery.getQuery("Trip");

            query.whereEqualTo("objectId", sId);
            query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> StartingCity, ParseException e) {
                    for (int i = 0; i < StartingCity.size(); i++) {

                        ParseObject object = StartingCity.get(i);
                         JoinedUsers =  object.getList("JoinedUsers");

                    }
                    usersListView = (ListView) findViewById(R.id.popUpListView);
                    namesArrayAdapter =
                            new ArrayAdapter<String>(getApplicationContext(),
                                    R.layout.user_list_item, JoinedUsers);
                    usersListView.setAdapter(namesArrayAdapter);


                    eJoin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            JoinedUsers.add(JoinedUsers.size(),userId);
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("Trip");

                            query.getInBackground(sId, new GetCallback<ParseObject>() {


                                public void done(ParseObject cos, com.parse.ParseException e) {
                                    if (e == null) {


                                        cos.put("JoinedUsers", JoinedUsers);

                                        cos.saveInBackground();

                                    } else {
                                        Log.d("RideTogether", "Error: " + e.getMessage());
                                    }
                                }
                            });
                            Intent backToAdpater = new Intent(popup2.this, Options.class);
                            startActivity(backToAdpater);
                        }
                    });

                    eConfirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //////Parse upadte
                            name = update.getEditText().getText().toString().trim();

                            ParseQuery<ParseObject> query = ParseQuery.getQuery("Trip");

                            query.getInBackground(sId, new GetCallback<ParseObject>() {


                                public void done(ParseObject cos, com.parse.ParseException e) {
                                    if (e == null) {

                                        cos.put("TripName", name);
                                        cos.put("JoinedUsers", userId);

                                        cos.saveInBackground();

                                    } else {
                                        Log.d("RideTogether", "Error: " + e.getMessage());
                                    }
                                }
                            });


                            Intent backToAdpater = new Intent(popup2.this, Options.class);
                            startActivity(backToAdpater);
                        }
                    });
                    eDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //////Parse upadte


                            ParseQuery<ParseObject> query = ParseQuery.getQuery("Trip");

                            ParseQuery<ParseObject> qImage = ParseQuery.getQuery("TripImage");
                            qImage.whereEqualTo("TripID", sId);
                            qImage.findInBackground(new FindCallback<ParseObject>() {
                                public void done(List<ParseObject> image, com.parse.ParseException e) {
                                    if (e == null) {
                                        for (int i = 0; i < image.size(); i++) {
                                            try {
                                                image.get(i).delete();
                                            } catch (com.parse.ParseException e1) {
                                                e1.printStackTrace();
                                            }
                                        }
                                    } else {

                                    }
                                }
                            });

                            query.getInBackground(sId, new GetCallback<ParseObject>() {


                                public void done(ParseObject cos, com.parse.ParseException e) {
                                    if (e == null) {

                                        try {
                                            cos.delete();
                                            Intent backToAdpater = new Intent(popup2.this, Options.class);
                                            startActivity(backToAdpater);

                                        } catch (com.parse.ParseException e1) {
                                            e1.printStackTrace();
                                        }
                                        cos.deleteInBackground();

                                    } else {

                                    }
                                }
                            });


                        }
                    });

                }
            });
    }}



}
