package com.example.dooglemaps.fragments;

import com.example.dooglemaps.notifications.MyResponse;
import com.example.dooglemaps.notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAABPA8So8:APA91bHrC78lsDSZlSb7Bz7VW9SlHug0HWkUv83oVf1Srtg_6g-xyHJPAifR4OesV7b76Y94TbrvTXzxiDSciR5nST9sPfHHIQay02jbUdX2dUbk-vDJI0IfNQCityOdoFOzv3Xokpvp"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);

}
