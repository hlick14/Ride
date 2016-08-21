package donate.cinek.wit.ie.ridetogether;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;


public class LoginIn extends AppCompatActivity {

    public int numOfReuests=0;
    protected EditText username;
    protected EditText password;
    protected Button button;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_in);
//        getSupportActionBar().hide();
        final TextInputLayout usernameWrapper = (TextInputLayout) findViewById(R.id.usernameWrapper);
        final TextInputLayout passwordWrapper = (TextInputLayout) findViewById(R.id.passwordWrapper);
        final Intent intent = new Intent(getApplicationContext(), Options.class);
        final Intent serviceIntent = new Intent(getApplicationContext(), MessageService.class);


        usernameWrapper.setHint("Username");
        passwordWrapper.setHint("Password");
//        final TextView txtView = (TextView) findViewById(R.id.WelcomeText);
//        Typeface roboto = Typeface.createFromAsset(this.getAssets(),
//                "font/Roboto-Bold.ttf"); //use this.getAssets if you are calling from an Activity
//        txtView.setTypeface(roboto);
//        txtView.setTextColor(getResources().getColor(R.color.text));







//        username=(EditText)findViewById(R.id.username);
//        password=(EditText)findViewById(R.id.password);
        button=(Button)findViewById(R.id.Login);

        button.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                dialog = new ProgressDialog(LoginIn.this, 1);
                dialog.setMessage("Logging You In");
                dialog.setCancelable(false);
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.show();



                hideKeyboard();
                final String susername = usernameWrapper.getEditText().getText().toString().trim();
                final String spassword = passwordWrapper.getEditText().getText().toString().trim();
                ParseUser.logInInBackground(susername, spassword, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, com.parse.ParseException e) {
                        if (user != null) {
                            //temporarly disabled to check sinch messaging


                            final String currentUser = ParseUser.getCurrentUser().getUsername();
                            ParseQuery<ParseObject> query = ParseQuery.getQuery("FriendRequest");
                            query.whereEqualTo("RequestedUser", currentUser);
                            query.findInBackground(new FindCallback<ParseObject>() {
                                public void done(List<ParseObject> users, ParseException e) {
                                    if (e == null) {
                                        numOfReuests = users.size();
                                        Intent home = new Intent(LoginIn.this, Options.class);
                                        home.putExtra("requests", numOfReuests);
                                        if(dialog.isShowing()) {
                                            dialog.dismiss();
                                        }
                                        startService(serviceIntent);
                                        startActivity(home);

//                                final Intent intent = new Intent(getApplicationContext(), ListUsersActivity.class);
//                                        final Intent serviceIntent = new Intent(getApplicationContext(), MessageService.class);
//                                startActivity(intent);
//                                startService(serviceIntent);
                                    } else {
                                        numOfReuests = 0;
                                        Toast.makeText(LoginIn.this, "There was an error retrieving your friend requests from our servers", Toast.LENGTH_SHORT).show();
                                    }


                                }
                            });







                        } else {
                            usernameWrapper.setErrorEnabled(true);
//                                usernameWrapper.setError("Invalid Username or Password");
                            usernameWrapper.setError(Html.fromHtml("<font color='#ffffff'>Invalid Username or Password</font>"));
                            if(dialog.isShowing()) {
                                dialog.dismiss();
                            }


                        }

                    }
                });


//                                    Toast.makeText(LoginIn.this, "Log in Failed, Please Try again", Toast.LENGTH_SHORT).show();
                if(spassword.isEmpty())
                {
                    passwordWrapper.setError(Html.fromHtml("<font color='#ffffff'>Please enter the password</font>"));
                    if(dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            }

        });

    }




    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).
                    hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login_in, menu);
        return true;
    }
    @Override
    public void onDestroy() {
        stopService(new Intent(this, MessageService.class));
        super.onDestroy();
    }


}