package com.uu2.tinyagents.llm.ollama;


import com.uu2.tinyagents.core.tools.function.annotation.FunctionDef;
import com.uu2.tinyagents.core.tools.function.annotation.FunctionParam;

public class WeatherFunctions {

    @FunctionDef(name = "get_the_weather_info", description = "更具传入的城市，返回最新的天气信息")
    public static String getWeatherInfo(
        @FunctionParam(name = "city", description = "the city name", required = true) String name
    ) {
        System.out.println(">>> tool execute => getWeatherInfo: " + name);
        return  name + "的今天天气是：晴天，温度是：35度，风向是：西北风。";
    }
}
