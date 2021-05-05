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

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import java.io.File;
import java.io.FileInputStream;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

/**
 * {@link RequestBody} implementation that publishes progress using a {@link Observable}.
 */
public class ProgressRequestBody extends RequestBody {

    private static final int DEFAULT_BUFFER_SIZE = 2048;

    private final File file;
    private final PublishSubject<Double> progress;

    public ProgressRequestBody(File file) {
        this.file = file;
        this.progress = PublishSubject.create();
    }

    public Observable<Double> getProgress() {
        return progress;
    }

    @Override
    public MediaType contentType() {
        //FIXME
        return MediaType.get("application/octet-stream");
    }

    @Override
    public long contentLength() {
        return file.length();
    }

    @Override
    public void writeTo(BufferedSink sink) {
        double uploadedCount = 0.0;
        double uploadedPercentage = 0.0;

        var buffer = new byte[DEFAULT_BUFFER_SIZE];

        try (var in = new FileInputStream(file)) {
            int read;
            while ((read = in.read(buffer)) != -1) {
                uploadedCount += read;
                sink.write(buffer, 0, read);

                double currentProgress = uploadedCount / contentLength();
                if (currentProgress - uploadedPercentage > 0.01 || currentProgress == 1.0) {
                    uploadedPercentage = currentProgress;
                    this.progress.onNext(uploadedPercentage);
                }
            }
        } catch (Exception ex) {
            this.progress.onError(ex);
        }

        this.progress.onComplete();
    }
}
