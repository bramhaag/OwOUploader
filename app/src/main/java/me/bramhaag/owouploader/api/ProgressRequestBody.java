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

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.webkit.MimeTypeMap;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import me.bramhaag.owouploader.api.callback.ProgressResultCallback;
import me.bramhaag.owouploader.file.FileProvider;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

/**
 * {@link RequestBody} implementation that publishes progress using a {@link ProgressResultCallback}.
 */
public class ProgressRequestBody extends RequestBody {

    private static final int DEFAULT_BUFFER_SIZE = 2048;

    @NonNull
    private final FileProvider file;
    @NonNull
    private final ProgressResultCallback<?> progressResult;

    /**
     * Create a new {@link ProgressRequestBody} with an automatically determined content type.
     *
     * @param file           the file
     * @param progressResult the callbacks
     */
    public ProgressRequestBody(@NonNull FileProvider file, @NonNull ProgressResultCallback<?> progressResult) {
        this.file = file;
        this.progressResult = progressResult;
    }

    @Override
    public MediaType contentType() {
        return MediaType.get(file.getContentType());
    }

    @Override
    public long contentLength() {
        return file.getSize();
    }

    @Override
    public void writeTo(@NonNull BufferedSink sink) throws IOException {
        double uploaded = 0.0;
        double progress = 0.0;

        var buffer = new byte[DEFAULT_BUFFER_SIZE];

        try (var in = file.getContent()) {
            int read;
            while ((read = in.read(buffer)) != -1) {
                uploaded += read;
                sink.write(buffer, 0, read);

                double currentProgress = uploaded / contentLength();
                if (currentProgress - progress > 0.01 || currentProgress == 1.0) {
                    progress = currentProgress;
                    this.progressResult.onProgress(progress);
                }
            }
        }
    }
}
