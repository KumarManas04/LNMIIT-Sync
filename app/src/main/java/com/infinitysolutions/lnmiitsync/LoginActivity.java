package com.infinitysolutions.lnmiitsync;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import java.util.List;

import static com.infinitysolutions.lnmiitsync.Contract.ValuesContract.BASE_URL;

@SuppressLint("LogNotTimber")
public class LoginActivity extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN = 101;
    private int EXTRA_DETAILS_REQUEST_CODE = 102;
    private String TAG = "LoginActivity";
    private GoogleSignInAccount mAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().setNavigationBarColor(Color.BLACK);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    public void login(View view) {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);
            handleSignInResult(task);
        } else if (requestCode == EXTRA_DETAILS_REQUEST_CODE) {
            if (intent == null) {
                Log.d(TAG, "intent was null");
            } else {
                intent.putExtra("gId", mAccount.getId());
                intent.putExtra("userName", mAccount.getDisplayName());
                intent.putExtra("emailId", mAccount.getEmail());
                try {
                    intent.putExtra("thumbnailUrl", mAccount.getPhotoUrl().toString());
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    intent.putExtra("thumbnailUrl", "NullPhoto");
                }
                setResult(105, intent);
                finish();
            }
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            mAccount = completedTask.getResult(ApiException.class);
            Log.d(TAG, "GoogleId = " + mAccount.getId());
            checkIfUserExists(mAccount.getId());
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    public void checkIfUserExists(String googleId) {
        final ProgressDialog dialog = new ProgressDialog(this); // this = YourActivity
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Contacting server...");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        //The Retrofit builder will have the client attached, in order to get connection logs
        Retrofit retrofit = new Retrofit.Builder()
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build();

        RetroFitInterface service = retrofit.create(RetroFitInterface.class);

        service.getUserDetails(googleId).enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (response.isSuccessful()) {
                    String jsonResponse = new Gson().toJson(response.body());
                    if (jsonResponse.equals("{}")) {
                        dialog.cancel();
                        Intent intent = new Intent(LoginActivity.this, ExtraDetailsActivity.class);
                        startActivityForResult(intent, EXTRA_DETAILS_REQUEST_CODE);
                    } else {
                        List<String> clubs = null;
                        try {
                            clubs = response.body().getClubs();
                        }catch (NullPointerException e){
                            e.printStackTrace();
                        }
                        String clubsArray[] = new String[clubs.size()];
                        for (int i = 0; i < clubs.size(); i++) {
                            clubsArray[i] = clubs.get(i);
                        }

                        Intent intent = new Intent();
                        intent.putExtra("gId", response.body().getGoogleId());
                        intent.putExtra("userName", response.body().getUsername());
                        intent.putExtra("emailId", response.body().getEmail());
                        intent.putExtra("thumbnailUrl", response.body().getThumbnail());
                        intent.putExtra("batch", response.body().getBatch());
                        intent.putExtra("clubs", clubsArray);
                        intent.putExtra("isRegistered",1);
                        setResult(105, intent);
                        dialog.cancel();
                        finish();
                    }
                }else{
                    dialog.cancel();
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {

            }
        });
    }
}
