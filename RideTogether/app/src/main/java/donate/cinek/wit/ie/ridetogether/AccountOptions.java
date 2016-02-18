package donate.cinek.wit.ie.ridetogether;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

import java.util.List;


public class AccountOptions extends BaseActivity {
    Button passResetButton,userDeleteButton;
    EditText uName,uPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_options);
        //super.set();

        final ParseUser currentUser = ParseUser.getCurrentUser();
        String cUserName=(String)currentUser.get("Name");
        String cUserEmail=(String)currentUser.getEmail();

        uName=(EditText)findViewById(R.id.SettingsName);
        uName.setText(cUserName);
        uPassword=(EditText)findViewById(R.id.SettingsEmail);
        uPassword.setText(cUserEmail);

        final ParseQuery<ParseObject> query = ParseQuery.getQuery("User");
        query.whereEqualTo("Email", cUserEmail);
        passResetButton=(Button)findViewById(R.id.pass_btn);
        passResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                ParseUser.requestPasswordResetInBackground(currentUser.getEmail(),
                        new RequestPasswordResetCallback() {
                            public void done(ParseException e) {
                                if (e == null) {
                                    Toast.makeText(AccountOptions.this, "An email has been sent with instructions on resetting your passoword", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(AccountOptions.this, "Ooops, Something went wrong, please try again", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
        userDeleteButton=(Button)findViewById(R.id.delete_btn);
        userDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ParseQuery<ParseObject> query = ParseQuery.getQuery("Trip");
                query.whereEqualTo("CreatedbyUser", currentUser);
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> parseObjects, ParseException e) {
                        if (e == null) {

                            for (int i = 0; i < parseObjects.size(); i++) {
                                ParseObject tempTest = parseObjects.get(i);
                                ParseObject user =(ParseObject) tempTest.get("CreatedbyUser");
                                Toast.makeText(AccountOptions.this, user.toString(), Toast.LENGTH_SHORT).show();
                                while(tempTest!=null) {
                                    if (user.toString().equals(currentUser.toString())) {
                                        tempTest.deleteInBackground();
                                    }
                                }
                            }
                        } else {
                            Log.d("score", "Error: " + e.getMessage());
                        }
                    }
                });
                currentUser.deleteInBackground();
                Intent back = new Intent(AccountOptions.this,MainActivity.class);
                startActivity(back);

                    }

                });






            }





            @Override
            public boolean onCreateOptionsMenu(Menu menu) {
                // Inflate the menu; this adds items to the action bar if it is present.
//                getMenuInflater().inflate(R.menu.menu_account_options, menu);
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
                    Intent back = new Intent(AccountOptions.this,Options.class);
                    startActivity(back);

                    return true;
                }
                else if (id== R.id.optionsLogout)
                {
                    ParseUser.logOut();
                    Intent backToMain = new Intent(AccountOptions.this,MainActivity.class);
                    startActivity(backToMain);
                    return true;

                }

                return super.onOptionsItemSelected(item);
            }
        }
