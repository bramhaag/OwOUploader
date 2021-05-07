/*
 * OwO Uploader
 * Copyright (C) 2021
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.bramhaag.owouploader.api;

import android.content.Context;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.InputStream;
import me.bramhaag.owouploader.BuildConfig;
import me.bramhaag.owouploader.api.callback.ProgressResultCallback;
import me.bramhaag.owouploader.api.callback.ResultCallback;
import me.bramhaag.owouploader.api.deserializer.ObjectModelArrayDeserializer;
import me.bramhaag.owouploader.api.deserializer.ObjectModelDeserializer;
import me.bramhaag.owouploader.api.deserializer.UploadModelDeserializer;
import me.bramhaag.owouploader.api.deserializer.UserModelDeserializer;
import me.bramhaag.owouploader.api.exception.ResponseStatusException;
import me.bramhaag.owouploader.api.interceptor.AuthenticationInterceptor;
import me.bramhaag.owouploader.api.interceptor.RateLimitInterceptor;
import me.bramhaag.owouploader.api.model.ObjectModel;
import me.bramhaag.owouploader.api.model.UploadModel;
import me.bramhaag.owouploader.api.model.UserModel;
import me.bramhaag.owouploader.api.service.OwOService;
import me.bramhaag.owouploader.file.FileProvider;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Wrapper class for the OwO API.
 */
public class OwOAPI {

    private final OwOService service;

    private static final String USER_AGENT = String.format("WhatsThisClient (%s, %s)",
            "https://github.com/bramhaag/OwOUploader", BuildConfig.VERSION_CODE);

    private static final String DEFAULT_ENDPOINT = "https://api.awau.moe/";

    /**
     * Initialize a new instance of OwO API with an API key.
     *
     * @param key the API key
     */
    public OwOAPI(String key) {
        this.service = createService(key);
    }

    /**
     * Upload a file asynchronously.
     *
     * @param file           the file
     * @param progressResult the callbacks
     * @param associated     uses associated endpoint when set to true
     */
    public void uploadFile(@NonNull FileProvider file, @NonNull ProgressResultCallback<UploadModel> progressResult,
            boolean associated) {
        var filePart = new ProgressRequestBody(file, progressResult);
        var requestBody = MultipartBody.Part.createFormData("files[]", "file.png", filePart);

        var call = associated ? service.uploadAssociated(requestBody) : service.upload(requestBody);
        enqueueCall(call, progressResult);
    }

    /**
     * Shorten a URL asynchronously.
     *
     * @param url            the url to shorten
     * @param resultUrl      the resulting base url, or default if null
     * @param progressResult the callbacks
     * @param associated     uses associated endpoint when set to true
     */
    public void shortenUrl(@NonNull String url, @Nullable String resultUrl,
            @NonNull ResultCallback<String> progressResult, boolean associated) {
        var call = associated ? service.shortenAssociated(url, resultUrl) : service.shorten(url, resultUrl);
        enqueueCall(call, progressResult);
    }

    public void getUser(@NonNull ResultCallback<UserModel> result) {
        enqueueCall(service.getUser(), result);
    }

    /**
     * Enqueue a call with callbacks.
     *
     * @param call   the call
     * @param result the callbacks
     * @param <T>    the type of the result
     */
    public <T> void enqueueCall(@NonNull Call<T> call, @NonNull ResultCallback<T> result) {
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    result.onError(new ResponseStatusException(response.code(), response.message()));
                    return;
                }

                result.onComplete(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
                result.onError(t);
            }
        });
    }

    @NonNull
    private static OwOService createService(@NonNull final String key) {
        var client = new Builder()
                .addNetworkInterceptor(new AuthenticationInterceptor(USER_AGENT, key))
                .addInterceptor(new RateLimitInterceptor())
                .build();

        var scalarsConverter = ScalarsConverterFactory.create();
        var gsonConverter = GsonConverterFactory.create(new GsonBuilder()
                .registerTypeAdapter(UploadModel.class, new UploadModelDeserializer())
                .registerTypeAdapter(UserModel.class, new UserModelDeserializer())
                .registerTypeAdapter(ObjectModel.class, new ObjectModelDeserializer())
                .registerTypeAdapter(ObjectModel[].class, new ObjectModelArrayDeserializer())
                .create());

        var retrofit = new Retrofit.Builder()
                .baseUrl(DEFAULT_ENDPOINT)
                .client(client)
                .addConverterFactory(scalarsConverter)
                .addConverterFactory(gsonConverter)
                .build();

        return retrofit.create(OwOService.class);
    }
}
