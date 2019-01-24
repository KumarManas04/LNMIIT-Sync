package com.infinitysolutions.lnmiitsync;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.infinitysolutions.lnmiitsync.Adapters.ClubsRecyclerViewAdapter;
import com.infinitysolutions.lnmiitsync.RetrofitResponses.ClubResponse;

import java.util.ArrayList;
import java.util.List;

import static com.infinitysolutions.lnmiitsync.Contract.ValuesContract.BASE_URL;

@SuppressLint("LogNotTimber")
public class ExtraDetailsActivity extends AppCompatActivity {

    private String TAG = "ExtraDetailsActivity";
    private RecyclerView mClubsRecyclerView;
    private ClubsRecyclerViewAdapter mClubsRecyclerViewAdapter;
    private List<String> mRecyclerItemsList;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extra_details);

        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("Gathering clubs data...");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);

        mClubsRecyclerView = findViewById(R.id.clubs_recycler_view);
        loadAndShowClubs();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public void submit(View view){
        List<String> selectedClubs = new ArrayList<String>();
        int clubsList[] = mClubsRecyclerViewAdapter.getCheckedList();
        for(int i = 0; i < clubsList.length ; i++){
            if(clubsList[i] == 1){
                selectedClubs.add(mRecyclerItemsList.get(i));
            }
        }

        String selectedClubsArray[] = new String[selectedClubs.size()];
        for(int i = 0; i < selectedClubs.size() ; i++){
            selectedClubsArray[i] = selectedClubs.get(i);
        }

        Intent intent = new Intent();
        intent.putExtra("clubs",selectedClubsArray);
        setResult(102,intent);
        finish();
    }

    private void loadAndShowClubs(){
        mRecyclerItemsList = new ArrayList<String>();
        dialog.show();

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        Retrofit retrofit = new Retrofit.Builder()
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build();

        RetroFitInterface service = retrofit.create(RetroFitInterface.class);

        service.getClubsList().enqueue(new Callback<List<ClubResponse>>() {
            @Override
            public void onResponse(Call<List<ClubResponse>> call, Response<List<ClubResponse>> response) {
                if(dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                List<ClubResponse> clubsResponseList = response.body();
                for(ClubResponse clubResponse:clubsResponseList){
                    mRecyclerItemsList.add(clubResponse.getName());
                }
                mClubsRecyclerViewAdapter = new ClubsRecyclerViewAdapter(mRecyclerItemsList);
                mClubsRecyclerView.setAdapter(mClubsRecyclerViewAdapter);
                GridLayoutManager manager = new GridLayoutManager(ExtraDetailsActivity.this,2,RecyclerView.VERTICAL,false);
                mClubsRecyclerView.setLayoutManager(manager);
            }

            @Override
            public void onFailure(Call<List<ClubResponse>> call, Throwable t) {
                if(dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
                new AlertDialog.Builder(ExtraDetailsActivity.this)
                        .setTitle("Network error")
                        .setMessage("Couldn't gather clubs data. Please check your internet connection and press retry.")
                        .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                loadAndShowClubs();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        }).show();
            }
        });
    }
}
