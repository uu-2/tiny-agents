package com.uu2.tinyagents.llm.spark;


import com.uu2.tinyagents.core.tools.function.annotation.FunctionDef;
import com.uu2.tinyagents.core.tools.function.annotation.FunctionParam;

public class WeatherFunctions {

    @FunctionDef(name = "get_the_weather_info", description = "根据传入的城市和日期，返回该地区对应日期的的天气信息")
    public static String getWeatherInfo(
            @FunctionParam(name = "city", description = "the city name", required = true) String name,
            @FunctionParam(name = "date", description = "the date，required format YYYY-MM-DD HH:MM:SS", required = false) String date
    ) {
        System.out.println(">>> tool execute => getWeatherInfo: " + name);
        return name + "的" + date + "天气是：晴天，温度是：35度，风向是：西北风。";
    }
}
