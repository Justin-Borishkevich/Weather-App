package com.example.assignment10;

import java.util.List;

public class ForecastResponse {
    private Properties properties;

    public Properties getProperties() {
        return properties;
    }

    public static class Properties {
        private List<Period> periods;

        public List<Period> getPeriods() {
            return periods;
        }
    }

    public static class Period {
        private String startTime;
        private int temperature;
        private ProbabilityOfPrecipitation probabilityOfPrecipitation;
        private String windSpeed;
        private String shortForecast;

        public String getStartTime() {
            return startTime;
        }

        public int getTemperature() {
            return temperature;
        }

        public ProbabilityOfPrecipitation getProbabilityOfPrecipitation() {
            return probabilityOfPrecipitation;
        }

        public String getWindSpeed() {
            return windSpeed;
        }

        public String getShortForecast() {
            return shortForecast;
        }
    }

    public static class ProbabilityOfPrecipitation {
        private Integer value;

        public Integer getValue() {
            return value;
        }
    }
}
