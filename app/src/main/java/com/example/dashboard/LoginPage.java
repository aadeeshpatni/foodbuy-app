

package com.example.dashboard;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dashboard.data.Preferences;
import com.example.dashboard.model.UserResponseObject;
import com.example.dashboard.network.DataService;
import com.example.dashboard.network.RetrofitClientInstance;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.BuildConfig;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginPage extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;
    public Button login_btn, custom_fb_btn;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private CallbackManager mCallbackManager;
    static int signInOption = 2;
    private static final String TAG = "FacebookAuthentication";
    private AccessTokenTracker accessTokenTracker;
    private static final int RC_SIGN_IN=123;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        Preferences preferences = Preferences.getPreferences(getApplicationContext());
        if(preferences.getCurrentUser() != null) {
            Toast.makeText(LoginPage.this, "user found", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(LoginPage.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        FacebookSdk.sdkInitialize(getApplicationContext());
        if(BuildConfig.DEBUG){
            FacebookSdk.setIsDebugEnabled(true);
            FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        }
        AppEventsLogger.activateApp(this);

//        progressBar = findViewById(R.id.progress_bar);

        mAuth = FirebaseAuth.getInstance();
        custom_fb_btn = findViewById(R.id.custom_fb_btn);
        custom_fb_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                progressBar.setVisibility(View.VISIBLE);

                LoginManager.getInstance().logInWithReadPermissions(LoginPage.this, Arrays.asList("email", "public_profile"));
                LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d(TAG, "onSuccess"+loginResult);
                        handleFacebookToken(loginResult.getAccessToken());
                    }
                    @Override
                    public void onCancel() {
                        Log.d(TAG, "onCancel");
                    }
                    @Override
                    public void onError(FacebookException error) {
                        Log.d(TAG, "onError: "+error);
                    }
                });
            }
        });
        mCallbackManager = CallbackManager.Factory.create();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user!=null){
                    updateUI(user);
                }else{
                    updateUI(null);
                }
            }
        };
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if(currentAccessToken == null){
                    mAuth.signOut();
                }
            }
        };


        createRequest();
        login_btn = findViewById(R.id.login_btn);
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                progressBar.setVisibility(View.VISIBLE);
                signIn();
                signInOption = 1;
            }
        });
        TextView click = findViewById(R.id.contact_us_link);
        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(LoginPage.this, "Opening default Email client", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(Intent.ACTION_SENDTO);
                String uriText = "mailto:" + Uri.encode("f20180605@hyderabad.bits-pilani.ac.in") +
                        "?subject=" + Uri.encode("Trouble logging in") +
                        "&body=" + Uri.encode("");
                Uri uri = Uri.parse(uriText);

                intent.setData(uri);
                try {
                    startActivity(Intent.createChooser(intent, "Send Email"));
                }catch(android.content.ActivityNotFoundException e) {
                    Toast.makeText(LoginPage.this,
                            "There are no email clients installed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void handleFacebookToken(AccessToken accessToken) {
        Log.d(TAG, "handleFacebookToken" + accessToken);
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "sign in with credential: successful");
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                }
                else{
                    Log.d(TAG, "sign in with credential: failure", task.getException());
                    Toast.makeText(LoginPage.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
            }
        });
    }

    private void updateUI(FirebaseUser user) {
        if(user!=null) {
            getUserWithEmail(user.getEmail(), user);
        }
        else{
            Toast.makeText(this, "User = null", Toast.LENGTH_SHORT).show();
        }
    }

    //  Google methods begin    //
    private void createRequest() {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(LoginPage.this, "api exception "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        else{
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this,
                        new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            getUserWithEmail(user.getEmail(), user);
//                            Toast.makeText(LoginPage.this, "Login Success", Toast.LENGTH_SHORT).show();
//                            Intent intent = new Intent(LoginPage.this, Registration.class);
////                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent);
                        } else {
                            Toast.makeText(LoginPage.this, "Task unsuccessful", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(authStateListener!=null){
            mAuth.removeAuthStateListener(authStateListener);
        }
    }


    public void getUserWithEmail(String email, FirebaseUser user) {
        DataService service = RetrofitClientInstance.getRetrofitInstance()
                .create(DataService.class);
        Call<UserResponseObject> call = service.getUserWithEmail(email);

        call.enqueue(new Callback<UserResponseObject>() {
            @Override
            public void onResponse(Call<UserResponseObject> call, Response<UserResponseObject> response) {
                if(response.body().error) {
                    Log.d(TAG, "onResponse: " + response.body().message);
                }
                else{
                    if(response.body().user != null) {
                        Preferences preferences = Preferences.getPreferences(getApplicationContext());
                        preferences.saveUser(response.body().user);

                        Intent intent = new Intent(LoginPage.this, OtpActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                    else {
                        Intent intent = new Intent(LoginPage.this, Registration.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("name", user.getDisplayName());
                        bundle.putString("mail", user.getEmail());
                        intent.putExtras(bundle);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onFailure(Call<UserResponseObject> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
                Toast.makeText(LoginPage.this, "Some error occurred, please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
