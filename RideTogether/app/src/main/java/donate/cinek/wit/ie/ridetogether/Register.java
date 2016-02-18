package donate.cinek.wit.ie.ridetogether;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.rey.material.widget.Spinner;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

import de.hdodenhof.circleimageview.CircleImageView;


public class Register extends AppCompatActivity {
    protected EditText uName;
    protected EditText email;
    protected EditText username;
    protected EditText password;
    protected EditText motorbike;
    protected EditText engine;
    protected Button button,button2;
    protected CircleImageView img;
    protected byte[] scaledData;
    protected Bitmap snypImageScaled;
    com.rey.material.widget.Spinner spinner1, spinner2;

    private Switch sw;
    private Button btnSubmit;
    private int checkint=0;
    String spinValue,spinValue2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
//        getSupportActionBar().hide();
        //GET ALL EDIT TEXTS AND SET HINTS
        final TextInputLayout nameWrapper = (TextInputLayout) findViewById(R.id.nameWrapper);
        final TextInputLayout emailWrapper = (TextInputLayout) findViewById(R.id.REmailWrapper);
        final TextInputLayout usernameWrapper = (TextInputLayout) findViewById(R.id.RUsernameWrapper);
        final TextInputLayout passwordWrapper = (TextInputLayout) findViewById(R.id.RPasswordWrapper);
        final TextInputLayout motorbikeWrapper = (TextInputLayout) findViewById(R.id.ModelWrapper);

        nameWrapper.setHint("Name");
        emailWrapper.setHint("Email");
        usernameWrapper.setHint("Username");
        passwordWrapper.setHint("Password");
        motorbikeWrapper.setHint("Model");


        button=(Button)findViewById(R.id.Register);


        img = (CircleImageView)findViewById(R.id.imageView);
        ////change this to empty user vector
        //img.setImageResource(R.drawable.userplains);

        ///////////////////////Spinner adapter
        final com.rey.material.widget.Spinner spin = (com.rey.material.widget.Spinner) findViewById(R.id.spinner1);




        // Declaring an Adapter and initializing it to the data pump
        String[] objects = { "Select Manufacturer","Honda", "Yamaha", "Suzuki", "Aprilia", "Ducati","Ktm", "Triumph", "Harley Davidson"
                 };


        ArrayAdapter adapter = new ArrayAdapter(
                getApplicationContext(),android.R.layout.simple_list_item_1 ,objects);

// Setting Adapter to the Spinner
        spin.setAdapter(adapter);
        spin.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(Spinner parent, View view, int position, long id) {
                spin.removeView(view);
                spin.setSelection(position);
                spinValue=(String)spin.getSelectedItem();


            }
        });



///////////////////////////////////////////////
            /////second spinner
        final com.rey.material.widget.Spinner spin2 = (com.rey.material.widget.Spinner) findViewById(R.id.spinner2);
        String[] engines = { "Select Engine Size","125", "250", "300", "500", "600","650", "750", "800","1000","1200"
        };

        ArrayAdapter adapter2 = new ArrayAdapter(
                getApplicationContext(),android.R.layout.simple_list_item_1 ,engines);

        // Setting Adapter to the Spinner
        spin2.setAdapter(adapter2);
        spin2.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(Spinner parent, View view, int position, long id) {
                spin2.removeView(view);
                spin2.setSelection(position);
                spinValue2=(String)spin2.getSelectedItem();


            }
        });





        ///////////////////////////////////////

        img.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 0);
            }
        });
        Bitmap bitmap8 = BitmapFactory.decodeResource(getResources(),
                R.drawable.userplain);
// Convert it to byte
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
// Compress image to lower quality scale 1 - 100
        bitmap8.compress(Bitmap.CompressFormat.PNG, 100, stream);
         scaledData = stream.toByteArray();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkint =0;
                if (nameWrapper.getEditText().getText().toString().equalsIgnoreCase("")){
                    nameWrapper.setError("Please enter your name");

                    checkint=1;
                }
                if (emailWrapper.getEditText().getText().toString().equalsIgnoreCase("")){

                    emailWrapper.setError("Invalid Email");
                    checkint=1;
                }
                if(usernameWrapper.getEditText().getText().toString().equalsIgnoreCase("") )
                {
                    usernameWrapper.setError("Invalid Username");
                    checkint=1;
                }
                 if (passwordWrapper.getEditText().getText().toString().equalsIgnoreCase("")){
                     passwordWrapper.setError("Invalid Password");
                     checkint=1;
                }
                 if (motorbikeWrapper.getEditText().getText().toString().equalsIgnoreCase("")){
                     motorbikeWrapper.setError("Please enter the model of your motorbike");
                     checkint=1;
                }
                if(checkint==0){

                String sName = nameWrapper.getEditText().getText().toString().trim();
                    String sEmail = emailWrapper.getEditText().getText().toString().trim();
                    String sUsername = usernameWrapper.getEditText().getText().toString().trim();
                    String sPassword = passwordWrapper.getEditText().getText().toString().trim();
                    String sMotorbike = motorbikeWrapper.getEditText().getText().toString().trim();
//                String sEngine = engine.getText().toString().trim();
                ParseUser user = new ParseUser();
                    user.put("Name", sName);
                user.setUsername(sUsername);
                user.setEmail(sEmail);
                user.setPassword(sPassword);
//                String model=addListenerOnSpinnerItemSelection().toString();
//                String size = addListenerOnSpinnerItemSelection2();

//                Toast.makeText(Register.this, .toString(), Toast.LENGTH_SHORT).show();
                user.put("Motorbike", spinValue);
                user.put("Model" , sMotorbike);
                user.put("Engine", spinValue2);

                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            photoUpload();
                            Intent newTrip = new Intent(Register.this, Options.class);
                            startActivity(newTrip);
                        } else {
                            Toast.makeText(Register.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }


                });


            }}
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            Uri targetUri = data.getData();

            Bitmap bitmap;
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
                 snypImageScaled = Bitmap.createScaledBitmap(bitmap, 200, 200
                        * bitmap.getHeight() / bitmap.getWidth(), false);
                // Convert it to byte
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                // Compress image to lower quality scale 1 - 100
                snypImageScaled.compress(Bitmap.CompressFormat.JPEG, 100, bos);

                scaledData = bos.toByteArray();



                 img.setImageBitmap(snypImageScaled);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }
    public void photoUpload() {
                    ParseUser currentUser = ParseUser.getCurrentUser();

            if (currentUser != null) {

            // Create the ParseFile
            ParseFile file = new ParseFile("androidbegin.png", scaledData);

            // Upload the image into Parse Cloud
            file.saveInBackground();
            // Create a New Class called "ImageUpload" in Parse
            ParseObject imgupload = new ParseObject("ImageUpload");

            // Create a column named "ImageName" and set the string
            imgupload.put("ImageName", "Profile Picture");

            // Create a column named "ImageFile" and insert the image

            imgupload.put("ImageFile", file);
            imgupload.put("CreatedbyUser", currentUser);

            // Create the class and the columns
            imgupload.saveInBackground();

            // Show a simple toast message
            Toast.makeText(Register.this, "Image Uploaded",
                    Toast.LENGTH_SHORT).show();
        }
    }


//    public String addListenerOnSpinnerItemSelection() {
//        ///error becouse returning item but the spinner does not update its ui
//        spinner1 = (com.rey.material.widget.Spinner) findViewById(R.id.spinner1);
//       return String.valueOf(spinner1.getSelectedItem());
//
//
//    }
//    public String addListenerOnSpinnerItemSelection2()
//    {
//        spinner2= (com.rey.material.widget.Spinner)findViewById(R.id.spinner2);
//        return String.valueOf(spinner2.getSelectedItem());
//    }




}

