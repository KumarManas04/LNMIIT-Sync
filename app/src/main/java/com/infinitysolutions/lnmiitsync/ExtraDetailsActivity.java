package com.infinitysolutions.lnmiitsync;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("LogNotTimber")
public class ExtraDetailsActivity extends AppCompatActivity {

    private String TAG = "ExtraDetailsActivity";
    ClubsRecyclerViewAdapter mClubsRecyclerViewAdapter;
    List<String> mRecyclerItemsList;
    Spinner mSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extra_details);

        mSpinner = (Spinner)findViewById(R.id.spinner);
        RecyclerView clubsRecyclerView = (RecyclerView) findViewById(R.id.clubs_recycler_view);

        mRecyclerItemsList = new ArrayList<String>();
        mRecyclerItemsList.add("Astronomy");
        mRecyclerItemsList.add("Cybros");
        mRecyclerItemsList.add("E-Cell");
        mRecyclerItemsList.add("Literary committee");
        mRecyclerItemsList.add("Phoenix");
        mRecyclerItemsList.add("Quiz");
        mRecyclerItemsList.add("Photography");
        mRecyclerItemsList.add("Sankalp");
        mRecyclerItemsList.add("Fashion");

        mClubsRecyclerViewAdapter = new ClubsRecyclerViewAdapter(mRecyclerItemsList);
        clubsRecyclerView.setAdapter(mClubsRecyclerViewAdapter);
        GridLayoutManager manager = new GridLayoutManager(this,2,RecyclerView.VERTICAL,false);
        clubsRecyclerView.setLayoutManager(manager);

        List<String> spinnerItemsList = new ArrayList<String>();
        spinnerItemsList.add("A1");
        spinnerItemsList.add("A2");
        spinnerItemsList.add("B1");
        spinnerItemsList.add("B2");

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,R.layout.spinner_item,spinnerItemsList);

        mSpinner.setAdapter(spinnerAdapter);
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
        intent.putExtra("batch",mSpinner.getSelectedItem().toString());
        setResult(102,intent);
        finish();
    }
}
