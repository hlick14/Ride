package donate.cinek.wit.ie.ridetogether;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class popup extends AppCompatActivity {
    Button eConfirm,eDelete;
    String sId,tName,tTime,tDate;
    TextView time,date;

    String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .8), (int) (height * .6));
        final TextInputLayout update = (TextInputLayout) findViewById(R.id.UpdateNameWrapper);

        eDelete = (Button) this.findViewById(R.id.eDelete);
        eConfirm = (Button) this.findViewById(R.id.eConfirm);

        time = (TextView)findViewById(R.id.tTime);
        date = (TextView)findViewById(R.id.tDate);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            sId = extras.getString("ID");
            tName = extras.getString("Name");
            tDate = extras.getString("Date");
            tTime = extras.getString("Time");
            update.setHint(tName);
            date.setText("Trip Date : " + tDate);
            time.setText("Trip Time : " +tTime);

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

                                cos.saveInBackground();

                            } else {
                                Log.d("score", "Error: " + e.getMessage());
                            }
                        }
                    });


                    Intent backToAdpater = new Intent(popup.this, Options.class);
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
                                    Intent backToAdpater = new Intent(popup.this,Options.class);
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
    }}
