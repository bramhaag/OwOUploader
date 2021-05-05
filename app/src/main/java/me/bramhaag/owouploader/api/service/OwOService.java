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

package me.bramhaag.owouploader.api.service;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.List;
import me.bramhaag.owouploader.api.model.ObjectModel;
import me.bramhaag.owouploader.api.model.UploadModel;
import me.bramhaag.owouploader.api.model.UserModel;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Service that implement the OwO API.
 */
public interface OwOService {

    /**
     * Upload a file to the upload endpoint.
     *
     * @param file file to upload
     * @return {@link Call} of type {@link UploadModel}
     */
    @POST("upload/pomf")
    @Multipart
    @NonNull
    Call<UploadModel> upload(@NonNull @Part MultipartBody.Part file);

    /**
     * Shorten link using a specified endpoint.
     *
     * @param url       URL to shorten
     * @param resultUrl shorten url used
     * @return {@link Call} of type {@link String}
     */
    @GET("shorten/polr?action=shorten")
    @NonNull
    Call<String> shorten(@NonNull @Query("url") String url, @Nullable @Query("resultUrl") String resultUrl);

    /**
     * Upload a file to the associated upload endpoint.
     *
     * @param file file to upload
     * @return {@link Call} of type {@link UploadModel}
     */
    @Multipart
    @POST("upload/pomf/associated")
    @NonNull
    Call<UploadModel> uploadAssociated(@NonNull @Part MultipartBody.Part file);

    /**
     * Shorten link using the associated shorten endpoint.
     *
     * @param url       URL to shorten
     * @param resultUrl shorten url used
     * @return {@link Call} of type {@link String}
     */
    @GET("shorten/polr/associated?action=shorten")
    @NonNull
    Call<String> shortenAssociated(@NonNull @Query("url") String url, @Nullable @Query("resultUrl") String resultUrl);

    /**
     * Get {@code limit} objects.
     *
     * @param limit  the amount of objects to fetch
     * @param offset the amount of objects to skip
     * @return {@link Call} with a list of {@link ObjectModel}s
     */
    @GET("objects")
    @NonNull
    Call<List<ObjectModel>> getObjects(@Query("limit") int limit, @Query("offset") int offset);

    /**
     * Get an object.
     *
     * @param key the object's key
     * @return {@link Call} of type {@link ObjectModel}
     */
    @GET("objects/{key}")
    @NonNull
    Call<ObjectModel> getObject(@NonNull @Path("key") String key);

    /**
     * Get and delete an object.
     *
     * @param key the object's key
     * @return {@link Call} of type {@link ObjectModel}
     */
    @DELETE("objects/{key}")
    @NonNull
    Call<ObjectModel> deleteObject(@NonNull @Path("key") String key);

    /**
     * Get the current user's details.
     *
     * @return {@link Call} of type {@link UserModel}
     */
    @GET("user/me")
    @NonNull
    Call<UserModel> getUser();
}
