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

package me.bramhaag.owouploader.result;

import android.content.Context;
import android.net.Uri;
import androidx.activity.result.ActivityResultCallback;
import me.bramhaag.owouploader.file.UriContentProvider;
import me.bramhaag.owouploader.upload.UploadHandler;

/**
 * Callback for document picker.
 */
public class UploadResultCallback implements ActivityResultCallback<Uri> {

    private final Context context;
    private final UploadHandler uploadHandler;

    /**
     * Create a new callback.
     *
     * @param uploadHandler the upload handler
     */
    public UploadResultCallback(Context context, UploadHandler uploadHandler) {
        this.uploadHandler = uploadHandler;
        this.context = context;
    }

    @Override
    public void onActivityResult(Uri result) {
        if (result == null) {
            return;
        }

        uploadHandler.upload(new UriContentProvider(context, result));
    }
}
