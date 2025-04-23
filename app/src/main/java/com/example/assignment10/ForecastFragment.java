package com.example.assignment10;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.assignment10.databinding.FragmentForecastBinding;
import com.example.assignment10.databinding.RowItemForecastBinding;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ForecastFragment extends Fragment {
    private final OkHttpClient client = new OkHttpClient();
    final String TAG = "demo";
    private static final String ARG_PARAM_CITY = "ARG_PARAM_CITY";
    private City mCity;
    ForecastAdapter adapter;
    List<Forecast> mForecasts = new java.util.ArrayList<>();
    public ForecastFragment() {
        // Required empty public constructor
    }

    public static ForecastFragment newInstance(City city) {
        ForecastFragment fragment = new ForecastFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM_CITY, city);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCity = (City) getArguments().getSerializable(ARG_PARAM_CITY);
        }
    }

    FragmentForecastBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentForecastBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Forecast");

        adapter = new ForecastAdapter();
        binding.recyclerViewForecast.setAdapter(adapter);
        binding.recyclerViewForecast.setLayoutManager(new LinearLayoutManager(getContext()));

        binding.textViewCityForecast.setText(mCity.toString());
        getForecastUrl();
    }

    class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder> {

        @NonNull
        @Override
        public ForecastAdapter.ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            RowItemForecastBinding forecastBinding = RowItemForecastBinding.inflate(getLayoutInflater(), parent, false);
            return new ForecastViewHolder(forecastBinding);
        }


        @Override
        public void onBindViewHolder(@NonNull ForecastViewHolder holder, int position) {
            Forecast forecast = mForecasts.get(position);
            holder.bind(forecast);
        }


        @Override
        public int getItemCount() {
            return mForecasts.size();
        }

        class ForecastViewHolder extends RecyclerView.ViewHolder {
            RowItemForecastBinding forecastBinding;
            Forecast mForecast;
            public ForecastViewHolder(RowItemForecastBinding forecastBinding) {
                super(forecastBinding.getRoot());
                this.forecastBinding = forecastBinding;
            }

            public void bind(Forecast forecast) {
                this.mForecast = forecast;
                forecastBinding.textViewStartTime.setText(forecast.getStartTime());
                forecastBinding.textViewShortForecast.setText(forecast.getShortForecast());
                String temp = forecast.getTemperature() + ".0 °F";
                forecastBinding.textViewTemperature.setText(temp);
                String windSpeed = "Wind Speed: " + forecast.getWindSpeed();
                forecastBinding.textViewWindSpeed.setText(windSpeed);
                String precipitation;
                if (forecast.getPrecipitation() != null) {
                    precipitation = "Precipitation: " + forecast.getPrecipitation() + "%";
                } else {
                    precipitation = "Precipitation: 0%";
                }
                forecastBinding.textViewPrecipitation.setText(precipitation);
                Picasso.get().load(forecast.getIcon()).into(forecastBinding.imageView);


            }
        }
    }



    void getForecastUrl() {

        String urlString = String.format("https://api.weather.gov/points/%s,%s", mCity.getLat().toString(), mCity.getLng().toString());
        HttpUrl url = HttpUrl.parse(urlString)
                .newBuilder()
                .build();


        Request request = new Request.Builder()
                .url(url)
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

                    ResponseBody responseBody = response.body();
                    String jsonResponse = responseBody.string();

                    JsonObject jsonObject = gson.fromJson(jsonResponse, JsonObject.class);
                    Log.d(TAG, "onResponse: " + jsonObject);

                    JsonObject properties = jsonObject.getAsJsonObject("properties");
                    Log.d(TAG, "onResponse: Properties: " + properties);

                    if (properties.has("forecast")) {
                        Log.d(TAG, "onResponse: Forecast key found.");
                        JsonElement forecastElement = properties.get("forecast");
                        Log.d(TAG, "onResponse: Forecast element: " + forecastElement);
                        String forecastUrl = forecastElement.getAsString();
                        Log.d(TAG, "onResponse: Forecast URL: " + forecastUrl);
                        getForecast(forecastUrl);
                    }


                } else {
                    ResponseBody responseBody = response.body();
                    String body= responseBody.string();
                    Log.d(TAG, "onResponse: " + body);
                }
            }
        });
    }

    void getForecast(String forecastUrl) {

        Request request = new Request.Builder()
                .url(forecastUrl)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()) {
                    ResponseBody responseBody = response.body();
                    String jsonResponse = responseBody.string();
                    List<Forecast> forecasts = Forecast.parseWeatherJson(jsonResponse);
                    Log.d(TAG, "onResponse: Forecasts: " + forecasts);
                    requireActivity().runOnUiThread(() -> {
                        Log.d(TAG, "mForecasts: " + mForecasts);
                        mForecasts.clear();
                        mForecasts.addAll(forecasts);
                        Log.d(TAG, "mForecasts after clear and adding Forecasts: " + mForecasts);
                        adapter.notifyDataSetChanged();
                    });


                } else {
                    ResponseBody responseBody = response.body();
                    String body= responseBody.string();
                    Log.d(TAG, "onResponse: " + body);
                }
            }
        });
    }
}