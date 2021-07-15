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

package me.bramhaag.owouploader.api.callback;

import androidx.annotation.NonNull;

/**
 * Callbacks for a call.
 *
 * @param <T> the type of the result
 */
public interface ResultCallback<T> {

    void onStart();

    /**
     * Called when an error occurred during or after uploading.
     *
     * @param throwable the error
     */
    void onError(@NonNull Throwable throwable);

    /**
     * Called when the upload is successfully completed.
     *
     * @param result the result of the upload
     */
    void onComplete(@NonNull T result);
}
