package donate.cinek.wit.ie.ridetogether;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kuba Pieczonka on 19/02/2016.
 */
public class addFriendFragment extends android.support.v4.app.Fragment {

    View fragmentView;
    Button friendSearch;
    private ArrayAdapter<String> namesArrayAdapter;
    private ListView usersListView;
    ProgressDialog progressDialog;
    TextView time, date,FoundRequestedUsers;
    TextInputLayout Search;
    private ArrayList<String> listOfUsernames;
    String name;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ArrayList<String> tempUsernName;


    public addFriendFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }
    @Override
    public void onResume() {
        super.onResume();


    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.add_friend_fragment, container, false);
        Search = (TextInputLayout) fragmentView.findViewById(R.id.SearchUser);
        Search.setHint("Username");


        friendSearch = (Button) fragmentView.findViewById(R.id.friendSearch);


        getListOfUsers();


        return fragmentView;
    }
    @Override
    public void onStart() {
        super.onStart();
        friendSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                name = Search.getEditText().getText().toString();
                if (name.isEmpty()) {
                    Toast.makeText(getActivity(), "Please enter the username before searching", Toast.LENGTH_SHORT).show();

                } else {
                    for (int i = 0; i < listOfUsernames.size(); i++) {
                        tempUsernName.add(listOfUsernames.get(i));

                        if (tempUsernName.get(i).equals(name)) {
                            usersListView = (ListView) fragmentView.findViewById(R.id.foundUsers);


                            namesArrayAdapter =
                                    new ArrayAdapter<String>(getActivity().getApplicationContext(),
                                            R.layout.user_list_item, tempUsernName);
                            usersListView.setAdapter(namesArrayAdapter);
                        } else {
                            Toast.makeText(getActivity(), "Wrong Username entered", Toast.LENGTH_SHORT).show();
                        }
                    }
                    if (!tempUsernName.isEmpty()) {

                        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                                final String currentUser = ParseUser.getCurrentUser().getUsername();
                                ParseQuery<ParseObject> query = ParseQuery.getQuery("FriendRequest");
                                query.whereEqualTo("CurrentUser", currentUser);
                                query.whereEqualTo("RequestedUser", listOfUsernames.get(0));


                                query.findInBackground(new FindCallback<ParseObject>() {
                                    public void done(List<ParseObject> users, ParseException e) {
                                        if (users.isEmpty()) {

                                            ParseObject requestedFriend = new ParseObject("FriendRequest");
                                            requestedFriend.put("CurrentUser", currentUser);
                                            String tempRequestUsername = listOfUsernames.get(0);
                                            requestedFriend.put("RequestedUser", tempRequestUsername);//Set to one as all usernames are unique therefore the list will only contain one entry

//                                                requestedFriend.saveInBackground();
                                            try {
                                                requestedFriend.save();
                                            } catch (ParseException e1) {
                                                e1.printStackTrace();
                                            }

                                            Toast.makeText(getActivity(), "Friend Request has been sent ", Toast.LENGTH_SHORT).show();
                                            getActivity().finish();


                                        } else {
                                            Toast.makeText(getActivity(), "You have already sent a friend request to this user ", Toast.LENGTH_SHORT).show();
                                            getActivity().finish();
                                        }
                                        if (e != null) {
                                            Toast.makeText(getActivity(), "There was an error fetching the list of users", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });

                            }
                        });

                    } else {
                        Toast.makeText(getActivity(), "Username Not found", Toast.LENGTH_SHORT).show();
                    }


                }


            }


        });


    }
    private void getListOfUsers() {
        progressDialog = new ProgressDialog(getActivity());
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
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                } else {
                    Toast.makeText(getActivity(), "Could not load the list of users", Toast.LENGTH_SHORT).show();
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }
            }
        });
    }


}

