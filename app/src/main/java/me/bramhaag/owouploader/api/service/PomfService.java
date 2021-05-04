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

import me.bramhaag.owouploader.api.model.UploadModel;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;

/**
 * Service that implement the Pomf API.
 */
public interface PomfService {

    /**
     * Upload a file to a specified endpoint.
     *
     * @param endpoint the endpoint
     * @param file file to upload
     * @return {@link Call} of type {@link UploadModel}
     */
    @POST
    @Multipart
    Call<UploadModel> upload(@Url String endpoint, @Part MultipartBody.Part file);
}
