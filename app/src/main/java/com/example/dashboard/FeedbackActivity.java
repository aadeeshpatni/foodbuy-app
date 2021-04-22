package com.example.dashboard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.dashboard.data.Preferences;
import com.example.dashboard.model.feedback.Feedback;
import com.example.dashboard.model.feedback.FeedbackPostResponse;
import com.example.dashboard.network.DataService;
import com.example.dashboard.network.RetrofitClientInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FeedbackActivity extends AppCompatActivity {
    RatingBar mRatingBar;
    TextView mRatingScale;
    EditText mFeedback;
    Button mSendFeedback;
    ProgressDialog progressDialog;

    String productId, receiverId, submitterId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        mRatingBar = (RatingBar) findViewById(R.id.ratingBar);
        mRatingScale = (TextView) findViewById(R.id.tvRatingScale);
        mFeedback = (EditText) findViewById(R.id.etFeedback);
        mSendFeedback = (Button) findViewById(R.id.btnSubmit);
        progressDialog = new ProgressDialog(FeedbackActivity.this);
        progressDialog.setMessage("Sending feedback...");

        Intent intentFromProductActivity = getIntent();
        productId = intentFromProductActivity.getStringExtra("productId");
        receiverId = intentFromProductActivity.getStringExtra("receiverId");

        Preferences preferences = Preferences.getPreferences(getApplicationContext());
        submitterId = preferences.getCurrentUser()._id;

        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                mRatingScale.setText(String.valueOf(v));
                switch ((int) ratingBar.getRating()) {
                    case 1:
                        mRatingScale.setText("Very bad");
                        break;
                    case 2:
                        mRatingScale.setText("Need some improvement");
                        break;
                    case 3:
                        mRatingScale.setText("Good");
                        break;
                    case 4:
                        mRatingScale.setText("Great");
                        break;
                    case 5:
                        mRatingScale.setText("Awesome. I love it");
                        break;
                    default:
                        mRatingScale.setText("");
                }
            }
        });

        mSendFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFeedback.getText().toString().isEmpty()) {
                    Toast.makeText(FeedbackActivity.this, "Please fill in feedback text box", Toast.LENGTH_LONG).show();
                } else {
                    Feedback feedback = new Feedback(submitterId, receiverId, productId, (int) mRatingBar.getRating(), mFeedback.getText().toString());
                    postFeedback(feedback);
                }
            }
        });
    }

    public void postFeedback(Feedback feedback) {
        progressDialog.show();

        DataService service = RetrofitClientInstance.getRetrofitInstance().create(DataService.class);

        Call<FeedbackPostResponse> call = service.postFeedback(feedback);
        call.enqueue(new Callback<FeedbackPostResponse>() {
            @Override
            public void onResponse(Call<FeedbackPostResponse> call, Response<FeedbackPostResponse> response) {
                progressDialog.dismiss();
                if(response.body().error) {
                    Log.e("FeedbackActivity.java", "onResponse: " + response.body().message);
                    Toast.makeText(FeedbackActivity.this, "Error submitting feedback", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(FeedbackActivity.this, "Feedback submitted successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<FeedbackPostResponse> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("FeedbackActivity.java", "onFailure: " + t.getMessage());
                Toast.makeText(FeedbackActivity.this, "Error submitting feedback", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
