package com.example.dashboard;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dashboard.data.Preferences;
import com.example.dashboard.model.User;
import com.example.dashboard.model.UserResponseObject;
import com.example.dashboard.network.DataService;

import com.example.dashboard.network.RetrofitClientInstance;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Registration extends AppCompatActivity{
    private final int REQUEST_CODE_MAP = 1024;
    private final String LOCATION_MESSAGE = "com.example.map.message";
    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    private TextView name_text, mail_text, storename_head, addressText;
    private EditText storeName, userName, street, city, state, pincode, phone, password;
    private String userType = "Customer";
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private FloatingActionButton doneButton;
    private AppCompatButton googleLocationButton;
    private ProgressDialog progressDialog;
    private ImageView status_img, visible_password;
    private String addressString;
    private Double longitude, latitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        name_text = findViewById(R.id.nametext);
        mail_text = findViewById(R.id.mailtext);
        userName = findViewById(R.id.username);
        phone = findViewById(R.id.phone);
        doneButton = findViewById(R.id.done_text_activity_registration);
        storename_head = findViewById(R.id.storeName_head);
        radioGroup = findViewById(R.id.userGroup);
        storeName = findViewById(R.id.storeName);
        googleLocationButton = findViewById(R.id.google_location_btn);
        addressText = findViewById(R.id.address_text);

        progressDialog = new ProgressDialog(Registration.this);
        progressDialog.setMessage("Registering...");

        password = findViewById(R.id.password);
        status_img = findViewById(R.id.status_img);
        visible_password = findViewById(R.id.visible_password);

        int loginOption = LoginPage.signInOption;
        if(loginOption == 2) {
            Intent intent = getIntent();
            if (intent == null) {
                Toast.makeText(this, "intent received == null", Toast.LENGTH_SHORT).show();
            }
            Bundle bundle = intent.getExtras();

            if (bundle != null) {
                String name = bundle.getString("name");
                String mail = bundle.getString("mail");

                name_text.setText(name);
                mail_text.setText(mail);
            } else {
                name_text.setText("");
                mail_text.setText("");
            }
        }else{
            GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
            if(signInAccount!=null){
                name_text.setText(signInAccount.getDisplayName());
                mail_text.setText(signInAccount.getEmail());
            } else{
                name_text.setText("2");
                mail_text.setText("2");
            }
        }

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndRegisterUser();
            }
        });

        if(isServiceOK()){
            init();
        }
    }

    private void init() {
        googleLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMapActivity();
            }
        });
    }

    private void startMapActivity() {
        Intent intent = new Intent(Registration.this, MapActivity.class);
        this.startActivityForResult(intent, REQUEST_CODE_MAP);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            if(extras != null) {
                status_img.setImageResource(R.drawable.connected);
                addressString = extras.getString(LOCATION_MESSAGE, "Can not find address");
                longitude = extras.getDouble("longitude", 0);
                latitude = extras.getDouble("latitude", 0);
                addressText.setText("Your Address:\n" + addressString);
                addressText.setVisibility(View.VISIBLE);
            }
        }
    }

    public boolean isServiceOK(){
        Log.d(TAG, "isServiceOK: Checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(Registration.this);

        if(available == ConnectionResult.SUCCESS)
        {
            Log.d(TAG, "isServiceOK: Google Play Services is working");
            return true;
        }
        else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d(TAG, "isServiceOK: an error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(Registration.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else{
            Toast.makeText(this,"You can't make a request", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    public void radioButtonClicked(View v) {
        int radioId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(radioId);
        Toast.makeText(this, radioButton.getText(), Toast.LENGTH_SHORT).show();
        if(radioButton.getText().equals("Consumer"))
        {
            storeName.setVisibility(View.GONE);
            storename_head.setVisibility(View.GONE);
        }else{
            userType = radioButton.getText().toString();
            storeName.setVisibility(View.VISIBLE);
            storename_head.setVisibility(View.VISIBLE);
        }
    }

    private void validateAndRegisterUser() {
        User user = new User(mail_text.getText().toString(), userName.getText().toString(), userType.toLowerCase(), storeName.getText().toString(), phone.getText().toString(), addressString, longitude, latitude);
        postNewUser(user);
    }

    private void postNewUser(User user) {
        progressDialog.show();

        DataService service = RetrofitClientInstance.getRetrofitInstance()
                .create(DataService.class);

        Call<UserResponseObject> call = service.postNewUser(user.userType, user);//(user.userType, user.email, user.name, user.storeName, user.phone, user.address.street, user.address.pincode, user.address.city, user.address.state);

        call.enqueue(new Callback<UserResponseObject>() {
            @Override
            public void onResponse(Call<UserResponseObject> call, Response<UserResponseObject> response) {
                progressDialog.dismiss();
                if(response.body().error){
                    Log.e("Registration.java", "onResponse: " + response.body().message);
                    Toast.makeText(Registration.this, "Error adding user", Toast.LENGTH_SHORT).show();
                }
                else {
                    Preferences preferences = Preferences.getPreferences(getApplicationContext());
                    preferences.saveUser(response.body().user);

                    Toast.makeText(Registration.this, "Successfully Registered.", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(Registration.this, OtpActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("phone", phone.getText().toString());
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<UserResponseObject> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("Registration.java", "onFailure: " + t.getMessage());
                Toast.makeText(Registration.this, "Error adding user", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showHidePass(View view){

        if(view.getId()==R.id.visible_password){
            if (password.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                //Show Password
                visible_password.setImageResource(R.drawable.ic_baseline_visibility_24);
                password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                //Hide Password
                visible_password.setImageResource(R.drawable.ic_baseline_visibility_off_24);
                password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            password.setSelection(password.getText().length());
        }
    }
}