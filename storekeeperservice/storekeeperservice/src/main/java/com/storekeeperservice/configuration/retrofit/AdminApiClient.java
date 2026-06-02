package com.storekeeperservice.configuration.retrofit;

import com.mjengoshareddtos.UserDto;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;


public interface AdminApiClient {


    @GET("/auth/user/get/storekeepers")
    Call<List<UserDto>> getStorekeepers();
}