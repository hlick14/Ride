package donate.cinek.wit.ie.ridetogether;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.ParseUser;


public class LoginIn extends AppCompatActivity {


    protected EditText username;
    protected EditText password;
    protected Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_in);
//        getSupportActionBar().hide();
        final TextInputLayout usernameWrapper = (TextInputLayout) findViewById(R.id.usernameWrapper);
        final TextInputLayout passwordWrapper = (TextInputLayout) findViewById(R.id.passwordWrapper);
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



                    hideKeyboard();
                    final String susername = usernameWrapper.getEditText().getText().toString().trim();
                    final String spassword = passwordWrapper.getEditText().getText().toString().trim();
                    ParseUser.logInInBackground(susername, spassword, new LogInCallback() {
                        @Override
                        public void done(ParseUser user, com.parse.ParseException e) {
                            if (user != null) {
                                    //temporarly disabled to check sinch messaging
                                Intent home = new Intent(LoginIn.this, Options.class);
                                startActivity(home);
//                                final Intent intent = new Intent(getApplicationContext(), ListUsersActivity.class);
                                final Intent serviceIntent = new Intent(getApplicationContext(), MessageService.class);
//                                startActivity(intent);
//                                startService(serviceIntent);

                            } else {


                            }

                        }
                    });
                    if (susername.equals("")) {
                        usernameWrapper.setErrorEnabled(true);
                        usernameWrapper.setError("Not a valid Username!");

//                                    Toast.makeText(LoginIn.this, "Log in Failed, Please Try again", Toast.LENGTH_SHORT).show();

                    } else if (spassword == "") {

                        passwordWrapper.setError("Please enter a  password");
                        ////// For security purposes , if invalid password verification is no displayed
//                                } else {
//
//                                    Toast.makeText(LoginIn.this, "Log in Failed, Please Try again", Toast.LENGTH_SHORT).show();
//                                }
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
