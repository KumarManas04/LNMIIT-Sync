package com.infinitysolutions.lnmiitsync;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import com.infinitysolutions.lnmiitsync.RetrofitResponses.Result;

import java.util.List;

import static com.infinitysolutions.lnmiitsync.Contract.ValuesContract.BASE_URL;
import static com.infinitysolutions.lnmiitsync.Contract.ValuesContract.GUEST_LOGIN;
import static com.infinitysolutions.lnmiitsync.Contract.ValuesContract.SHARED_PREF_LOGIN_TYPE;
import static com.infinitysolutions.lnmiitsync.Contract.ValuesContract.SHARED_PREF_NAME;
import static com.infinitysolutions.lnmiitsync.Contract.ValuesContract.STUDENT_LOGIN;

@SuppressLint("LogNotTimber")
public class LoginActivity extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN = 101;
    private int EXTRA_DETAILS_REQUEST_CODE = 102;
    private String TAG = "LoginActivity";
    private GoogleSignInAccount mAccount;
    private boolean isGuest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mGoogleSignInClient.signOut();
    }

    public void login(View view) {
        SharedPreferences sharedPrefs = this.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPrefs.edit();

        if (view.getId() == R.id.student_login_button) {
            editor.putInt(SHARED_PREF_LOGIN_TYPE, STUDENT_LOGIN);
            isGuest = false;
        } else {
            editor.putInt(SHARED_PREF_LOGIN_TYPE, GUEST_LOGIN);
            isGuest = true;
        }
        editor.commit();

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == RC_SIGN_IN) {
            Log.d(TAG,"RC_SIGN_IN triggered");
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

    @Override
    public void onBackPressed() {
        mGoogleSignInClient.signOut();
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            mAccount = completedTask.getResult(ApiException.class);

            if (!isGuest) {
                if (mAccount.getEmail() != null) {
                    String emailIdParts[] = mAccount.getEmail().split("@");
                    if (!emailIdParts[1].equals("lnmiit.ac.in")) {
                        mGoogleSignInClient.signOut();
                        Toast.makeText(LoginActivity.this, "LNMIIT account required. Please Login again.", Toast.LENGTH_LONG).show();
                    } else {
                        checkIfUserExists(mAccount.getId());
                    }
                }
            } else {
                checkIfUserExists(mAccount.getId());
            }
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            mGoogleSignInClient.signOut();
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
            public void onResponse(@NonNull Call<Result> call, @NonNull Response<Result> response) {
                if (response.isSuccessful()) {
                    String jsonResponse = new Gson().toJson(response.body());
                    if (jsonResponse.equals("{}")) {
                        dialog.cancel();
                        if (!isGuest) {
                            Intent intent = new Intent(LoginActivity.this, ExtraDetailsActivity.class);
                            startActivityForResult(intent, EXTRA_DETAILS_REQUEST_CODE);
                        } else {
                            Intent intent = new Intent();
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

                    } else {
                        Intent intent = new Intent();

                        if (!isGuest) {
                            List<String> clubs = null;
                            try {
                                clubs = response.body().getClubs();
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                            String clubsArray[] = new String[clubs.size()];
                            for (int i = 0; i < clubs.size(); i++) {
                                clubsArray[i] = clubs.get(i);
                            }

                            intent.putExtra("clubs", clubsArray);
                            intent.putExtra("batch", response.body().getBatch());
                        }

                        intent.putExtra("gId", response.body().getGoogleId());
                        intent.putExtra("userName", response.body().getUsername());
                        intent.putExtra("emailId", response.body().getEmail());
                        intent.putExtra("thumbnailUrl", response.body().getThumbnail());
                        intent.putExtra("isRegistered", 1);
                        setResult(105, intent);
                        dialog.cancel();
                        finish();
                    }
                } else {
                    dialog.cancel();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Result> call, @NonNull Throwable t) {
                Log.d(TAG,t.getMessage());
                dialog.cancel();
                mGoogleSignInClient.signOut();
                Toast.makeText(LoginActivity.this, "Couldn't contact server", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
