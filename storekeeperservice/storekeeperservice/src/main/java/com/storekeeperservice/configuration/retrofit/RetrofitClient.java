package com.storekeeperservice.configuration.retrofit;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import lombok.Data;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.time.Instant;

@Component
public class RetrofitClient {

    private final String baseUrl;
    private final String clientId;
    private final String clientSecret;

    private String cachedSystemToken;
    private Instant tokenExpiry;         // ← track expiry

    private Retrofit retrofit;

    public RetrofitClient(
            @Value("${base.url}") String baseUrl,
            @Value("${mjengo.service.id}") String clientId,
            @Value("${mjengo.service.secret}") String clientSecret) {
        this.baseUrl = baseUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public Retrofit getClient() {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(chain -> {
                        String finalToken;
                        String humanToken = getHumanTokenFromContext();

                        if (humanToken != null) {
                            finalToken = humanToken;
                        } else {
                            finalToken = getSystemToken();
                        }

                        Request authenticatedRequest = chain.request().newBuilder()
                                .header("Authorization", "Bearer " + finalToken)
                                .build();

                        return chain.proceed(authenticatedRequest);
                    })
                    .authenticator((route, response) -> {
                        // On 401, force refresh system token and retry ONCE
                        if (response.request().header("Authorization") != null) {
                            invalidateSystemToken();
                            try {
                                String freshToken = getSystemToken();
                                return response.request().newBuilder()
                                        .header("Authorization", "Bearer " + freshToken)
                                        .build();
                            } catch (IOException e) {
                                return null; // give up
                            }
                        }
                        return null;
                    })
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    private String getHumanTokenFromContext() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getCredentials() != null) {
            String credentials = auth.getCredentials().toString();
            // Avoid returning empty or placeholder strings
            return credentials.isEmpty() ? null : credentials;
        }
        return null;
    }

    private synchronized String getSystemToken() throws IOException {
        // Refresh if null OR expired (with 30s buffer)
        if (cachedSystemToken == null
                || tokenExpiry == null
                || Instant.now().isAfter(tokenExpiry.minusSeconds(30))) {
            fetchAndCacheToken();
        }
        return cachedSystemToken;
    }

    private synchronized void invalidateSystemToken() {
        this.cachedSystemToken = null;
        this.tokenExpiry = null;
    }

    private void fetchAndCacheToken() throws IOException {
        OkHttpClient authClient = new OkHttpClient();
        String loginJson = String.format(
                "{\"email\":\"%s\", \"password\":\"%s\"}", clientId, clientSecret
        );
        RequestBody body = RequestBody.create(
                loginJson, MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(baseUrl + "auth/authenticate")
                .post(body)
                .build();

        try (Response response = authClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Auth failed: " + response.code() + " " + response.message());
            }

            String responseBody = response.body().string();
            AuthResponse authResponse = new Gson().fromJson(responseBody, AuthResponse.class);

            if (authResponse.getAccessToken() == null) {
                throw new IOException("Auth service returned null token. Response was: " + responseBody);
            }

            this.cachedSystemToken = authResponse.getAccessToken();
            // Use expiry from response, or default to 55 minutes
            long expiresIn = authResponse.getExpiresIn() > 0 ? authResponse.getExpiresIn() : 3300;
            this.tokenExpiry = Instant.now().plusSeconds(expiresIn);
        }
    }

    @Data
    private static class AuthResponse {
        @SerializedName("token")  // ← handles snake_case from auth server
        private String accessToken;

        @SerializedName("expires_in")    // ← capture expiry if your auth service returns it
        private long expiresIn;
    }
}