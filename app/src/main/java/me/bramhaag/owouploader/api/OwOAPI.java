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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.gson.GsonBuilder;
import java.io.File;
import me.bramhaag.owouploader.BuildConfig;
import me.bramhaag.owouploader.api.ProgressRequestBody.ProgressResult;
import me.bramhaag.owouploader.api.deserializer.UploadModelDeserializer;
import me.bramhaag.owouploader.api.exception.ResponseStatusException;
import me.bramhaag.owouploader.api.model.UploadModel;
import me.bramhaag.owouploader.api.service.OwOService;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
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
    public void uploadFile(@NonNull File file, @NonNull ProgressResult<UploadModel> progressResult,
            boolean associated) {
        var filePart = new ProgressRequestBody(file, progressResult);
        var requestBody = MultipartBody.Part.createFormData("files[]", file.getName(), filePart);

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
            @NonNull ProgressResult<String> progressResult, boolean associated) {
        var call = associated ? service.shortenAssociated(url, resultUrl) : service.shorten(url, resultUrl);
        enqueueCall(call, progressResult);
    }

    /**
     * Enqueue a call with callbacks.
     *
     * @param call           the call
     * @param progressResult the callbacks
     * @param <T>            the type of the result
     */
    public <T> void enqueueCall(@NonNull Call<T> call, @NonNull ProgressResult<T> progressResult) {
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    progressResult.onError(new ResponseStatusException(response.code(), response.message()));
                    return;
                }

                progressResult.onProgress(1.0);
                progressResult.onComplete(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
                progressResult.onError(t);
            }
        });
    }

    @NonNull
    private static OwOService createService(@NonNull final String key) {
        var client = new OkHttpClient.Builder().addInterceptor(chain -> {
            var request = chain.request();
            var url = request.url().newBuilder().addQueryParameter("key", key).build();
            return chain.proceed(request.newBuilder().header("User-Agent", USER_AGENT).url(url).build());
        }).build();

        var scalarsConverter = ScalarsConverterFactory.create();
        var gsonConverter = GsonConverterFactory.create(new GsonBuilder()
                .registerTypeAdapter(UploadModel.class, new UploadModelDeserializer())
                .create());
        var rxJavaAdapter = RxJava2CallAdapterFactory.createAsync();

        var retrofit = new Retrofit.Builder()
                .baseUrl(DEFAULT_ENDPOINT)
                .client(client)
                .addConverterFactory(scalarsConverter)
                .addConverterFactory(gsonConverter)
                .addCallAdapterFactory(rxJavaAdapter)
                .build();

        return retrofit.create(OwOService.class);
    }
}
