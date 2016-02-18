package donate.cinek.wit.ie.ridetogether;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class addFriend extends AppCompatActivity {
    Button friendSearch;
    private ArrayAdapter<String> namesArrayAdapter;
    private ListView usersListView;
    ProgressDialog progressDialog;
    TextView time,date;
    TextInputLayout update;
    private ArrayList<String> listOfUsernames;
    String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_add_friend);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .8), (int) (height * .6));
         update = (TextInputLayout) findViewById(R.id.SearchUser);


        friendSearch = (Button) this.findViewById(R.id.friendSearch);
        getListOfUsers();


        friendSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name = update.getEditText().getText().toString();
                if (name.isEmpty()) {
                    Toast.makeText(addFriend.this, "Please enter the username before searching", Toast.LENGTH_SHORT).show();

                } else {
                    for (int i = 0; i < listOfUsernames.size(); i++) {
                        String tempUsernName = listOfUsernames.get(i);
                        if (tempUsernName.equals(name)) {
                            usersListView = (ListView) findViewById(R.id.foundUsers);
                            namesArrayAdapter =
                                    new ArrayAdapter<String>(getApplicationContext(),
                                            R.layout.user_list_item,listOfUsernames);
                            usersListView.setAdapter(namesArrayAdapter);
//                    imagesArrayAdapter = new ArrayAdapter<Bitmap>(getApplicationContext(),R.layout.user_list_item,images);
//                    usersListView.setAdapter(imagesArrayAdapter);
                            Button sendRequest= new Button(addFriend.this);
                            sendRequest.setText("Send Friend Request");
                            usersListView.addView(sendRequest);
                            sendRequest.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    Toast.makeText(addFriend.this,"button on click works",Toast.LENGTH_SHORT).show();
                                }
                            });

                            usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> a, View v, int i, long l) {

                                }
                            });
                        }

                    }
                }
            }

        });



    }

    private void getListOfUsers() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        listOfUsernames = new ArrayList<String>();


        String currentUser = ParseUser.getCurrentUser().getUsername();
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo("username", currentUser);

        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {

                        listOfUsernames.add(objects.get(i).get("username").toString());
                    }
                  if( progressDialog.isShowing())
                  {
                      progressDialog.dismiss();
                  }
                } else {
                    Toast.makeText(addFriend.this,"Could not load the list of users",Toast.LENGTH_SHORT).show();
                    if( progressDialog.isShowing())
                    {
                        progressDialog.dismiss();
                    }
                }
            }
        });
    }
    }
