package com.infinitysolutions.lnmiitsync.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.infinitysolutions.lnmiitsync.Event;
import com.infinitysolutions.lnmiitsync.RetrofitResponses.EventResponse;
import com.infinitysolutions.lnmiitsync.Adapters.EventsRecyclerViewAdapter;
import com.infinitysolutions.lnmiitsync.R;
import com.infinitysolutions.lnmiitsync.RetroFitInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.infinitysolutions.lnmiitsync.Contract.ValuesContract.BASE_URL;

public class EventsFragment extends Fragment {

    private RecyclerView mEventsRecyclerView;
    private TextView loadingView;
    private HashMap<Long,ArrayList<Event>> mEvents;
    private Context mContext;
    private String TAG = "EventsFragment";

    public EventsFragment() {
        //Required empty public constructor
    }

    public static EventsFragment newInstance() {
        return new EventsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_events_view, container, false);
        mEventsRecyclerView = (RecyclerView) rootView.findViewById(R.id.events_recycler_view);
        loadingView = (TextView)rootView.findViewById(R.id.loading_view);
        loadingView.setVisibility(View.INVISIBLE);
        mEventsRecyclerView.setVisibility(View.VISIBLE);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext,RecyclerView.VERTICAL,false);
        mEventsRecyclerView.setLayoutManager(layoutManager);

        mEvents = new HashMap<Long,ArrayList<Event>>();
        loadEvents();
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    private void loadEvents() {

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

        Retrofit retrofit = new Retrofit.Builder()
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build();

        RetroFitInterface service = retrofit.create(RetroFitInterface.class);

        loadingView.setVisibility(View.VISIBLE);
        mEventsRecyclerView.setVisibility(View.INVISIBLE);
        Log.d(TAG,"Sending GET request...");
        service.getEvents().enqueue(new Callback<List<EventResponse>>() {
            @Override
            public void onResponse(Call<List<EventResponse>> call, Response<List<EventResponse>> response) {
                List<EventResponse> events = response.body();
                Log.d(TAG,"Received response now calling loadIntoRecyclerView()");
                loadIntoRecyclerView(events);
            }

            @Override
            public void onFailure(Call<List<EventResponse>> call, Throwable t) {
                Log.d(TAG,"Error in response: " + t.getCause());
            }
        });
    }

    private void loadIntoRecyclerView(final List<EventResponse> list) {

        final Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if(msg.what == 0){
                    EventsRecyclerViewAdapter adapter = new EventsRecyclerViewAdapter(mContext,mEvents);
                    loadingView.setVisibility(View.INVISIBLE);
                    mEventsRecyclerView.setVisibility(View.VISIBLE);
                    mEventsRecyclerView.setAdapter(adapter);
                }
                return true;
            }
        });

        Thread thread = new Thread() {
            @Override
            public void run() {
                long endTime;
                for(EventResponse event : list){
                    if(event.getDuration() == 0) {
                        endTime = 0;
                    }else{
                        endTime = event.getDate() + (event.getDuration() * 60 * 60 * 1000);
                    }
                    if(mEvents.containsKey(event.getDate())){
                        mEvents.get(event.getDate()).add(new Event(event.getName(),event.getDescription(),event.getVenue(),endTime));
                    }else{
                        ArrayList<Event> eventItem = new ArrayList<Event>();
                        eventItem.add(new Event(event.getName(),event.getDescription(),event.getVenue(),endTime));
                        mEvents.put(event.getDate(),eventItem);
                    }
                }
                handler.sendEmptyMessage(0);
            }
        };

        thread.start();
    }
}
