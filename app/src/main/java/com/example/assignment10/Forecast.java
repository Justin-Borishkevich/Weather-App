package com.example.assignment10;

import com.google.gson.Gson;

import java.util.List;
import java.util.stream.Collectors;

public class Forecast {
    private String startTime;
    private int temperature;
    private String windSpeed;
    private String shortForecast;
    private Integer precipitation;
    private String icon;

    public Forecast() {
    }

    public Forecast(String startTime, int temperature, String windSpeed, String shortForecast, Integer precipitation, String icon) {
        this.startTime = startTime;
        this.temperature = temperature;
        this.windSpeed = windSpeed;
        this.shortForecast = shortForecast;
        this.precipitation = precipitation;
        this.icon = icon;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public int getTemperature() {
        return temperature;
    }

    public void setTemperature(int temperature) {
        this.temperature = temperature;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getShortForecast() {
        return shortForecast;
    }

    public void setShortForecast(String shortForecast) {
        this.shortForecast = shortForecast;
    }

    public Integer getPrecipitation() {
        return precipitation;
    }

    public void setPrecipitation(Integer precipitation) {
        this.precipitation = precipitation;
    }

    @Override
    public String toString() {
        return "Forecast{" +
                "startTime='" + startTime + '\'' +
                ", temperature=" + temperature +
                ", windSpeed='" + windSpeed + '\'' +
                ", shortForecast='" + shortForecast + '\'' +
                ", precipitation=" + precipitation +
                ", icon='" + icon + '\'' +
                '}';
    }

    public static List<Forecast> parseWeatherJson(String jsonString) {
        Gson gson = new Gson();
        WeatherResponse weatherResponse = gson.fromJson(jsonString, WeatherResponse.class);

        return weatherResponse.getProperties().getPeriods().stream()
                .map(period -> {
                    Forecast forecast = new Forecast();
                    forecast.setStartTime(period.getStartTime());
                    forecast.setTemperature(period.getTemperature());
                    forecast.setWindSpeed(period.getWindSpeed());
                    forecast.setIcon(period.getIcon());
                    forecast.setShortForecast(period.getShortForecast());
                    forecast.setPrecipitation(period.getProbabilityOfPrecipitation() != null
                        ? period.getProbabilityOfPrecipitation().getValue()
                        : null);
                    return forecast;
                })
                .collect(Collectors.toList());
    }

}

class ProbabilityOfPrecipitation {
    private Integer value;
    public Integer getValue() {return value;}
    public void setValue(Integer value) {this.value = value;}
}

class Period {
    private String startTime;
    private int temperature;
    private String windSpeed;
    private String shortForecast;
    private String icon;
    private ProbabilityOfPrecipitation probabilityOfPrecipitation;
    public String getStartTime() {return startTime;}
    public int getTemperature() {return temperature;}
    public String getWindSpeed() {return windSpeed;}
    public String getShortForecast() {return shortForecast;}
    public String getIcon() {return icon;}
    public ProbabilityOfPrecipitation getProbabilityOfPrecipitation() {return probabilityOfPrecipitation;}
}

class Properties {
    private List<Period> periods;
    public List<Period> getPeriods() {return periods;}
}

class WeatherResponse {
    private Properties properties;
    public Properties getProperties() {return properties;}
}
