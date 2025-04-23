package com.example.assignment10;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.example.assignment10.databinding.FragmentCitiesBinding;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class CitiesFragment extends Fragment {
    private final OkHttpClient client = new OkHttpClient();
    final String TAG = "demo";

    public CitiesFragment() {
        // Required empty public constructor
    }

    FragmentCitiesBinding binding;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCitiesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    ArrayAdapter<City> adapter;
    ArrayList<City> mCities = new ArrayList<>();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Cities");

        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, mCities);

        binding.listView.setAdapter(adapter);

        binding.listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListener.onCitySelected(adapter.getItem(position));
            }
        });

        getCities();

    }
    CitiesListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof CitiesListener) {
            mListener = (CitiesListener) context;
        } else {
            throw new ClassCastException(context.toString() + " must implement CitiesListener");
        }
    }

    void getCities() {
        Request request = new Request.Builder()
                .url("https://www.theappsdr.com/api/cities")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()) {
                    Gson gson = new Gson();

                    CitiesResponse citiesResponse = gson.fromJson(response.body().charStream(), CitiesResponse.class);

                    requireActivity().runOnUiThread(() -> {
                        mCities.clear();
                        mCities.addAll(citiesResponse.cities);
                        adapter.notifyDataSetChanged();
                    });

                    Log.d(TAG, "onResponse:" + citiesResponse.cities);



                } else {
                    ResponseBody responseBody = response.body();
                    String body= responseBody.string();
                    Log.d(TAG, "onResponse: " + body);
                }
            }
        });
    }

    interface CitiesListener {
        void onCitySelected(City city);
    }

}