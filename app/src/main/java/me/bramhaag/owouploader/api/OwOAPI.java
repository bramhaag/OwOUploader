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

import com.google.gson.GsonBuilder;
import me.bramhaag.owouploader.BuildConfig;
import me.bramhaag.owouploader.api.deserializer.UploadModelDeserializer;
import me.bramhaag.owouploader.api.model.UploadModel;
import me.bramhaag.owouploader.api.service.OwOService;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Wrapper class for the OwO API.
 */
public class OwOAPI {

    private OwOService service;

    private static final String USER_AGENT = String.format("WhatsThisClient (%s, %s)",
            "https://github.com/bramhaag/OwOUploader", BuildConfig.VERSION_CODE);

    private static final String DEFAULT_ENDPOINT = "https://api.awau.moe/";

    public OwOAPI(final String key) {
        this.service = createService(key);
    }

    private static OwOService createService(final String key) {
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
