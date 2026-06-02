package com.storekeeperservice.configuration.retrofit;

import com.mjengoshareddtos.UserDto;
import com.storekeeperservice.exceptions.MissingFieldException;
import com.storekeeperservice.exceptions.UserNotFoundException;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.List;


@Service
public class RetrofitService {
    private static final Logger logger = LoggerFactory.getLogger(RetrofitService.class);
    private final AdminApiClient apiClient;

    public RetrofitService(RetrofitClient retrofitClient) {
        this.apiClient = retrofitClient.getClient().create(AdminApiClient.class);
    }


    public List<UserDto> getStorekeepers() {
        logger.info("Fetching list of storekeepers from auth service");
        Call<List<UserDto>> call = apiClient.getStorekeepers();

        try {
            Response<List<UserDto>> response = call.execute();

            if (response.isSuccessful() && response.body() != null) {
                logger.info("Successfully fetched storekeepers. Response code: {}", response.code());
                return response.body();
            } else {
                logger.error("Failed to fetch storekeepers. Response code: {}, Error: {}",
                        response.code(), response.errorBody());

                throw new MissingFieldException("Failed to fetch storekeepers. Response code: " + response.code());
            }
        } catch (IOException e) {
            logger.error("API call failed due to network error: {}", e.getMessage());
            throw new RuntimeException("API call failed due to network error", e);
        }
    }
}