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
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import androidx.activity.result.ActivityResultCallback;
import androidx.annotation.NonNull;
import me.bramhaag.owouploader.api.OwOAPI;
import me.bramhaag.owouploader.api.callback.ProgressResultCallback;
import me.bramhaag.owouploader.api.model.UploadModel;
import me.bramhaag.owouploader.file.UriFileProvider;

public class UploadResultCallback implements ActivityResultCallback<Uri> {

    private final OwOAPI api;
    private final Context context;
    private final Handler mainHandler;

    public UploadResultCallback(OwOAPI api, Context context) {
        this.api = api;
        this.context = context;
        this.mainHandler = new Handler(context.getMainLooper());
    }

    @Override
    public void onActivityResult(Uri result) {
        var file = new UriFileProvider(context, result);
        api.uploadFile(file, new ProgressResultCallback<>() {
            @Override
            public void onProgress(double progress) {
                System.out.println("Upload progress: " + progress);
            }

            @Override
            public void onError(@NonNull Throwable throwable) {
                System.out.println("Upload error: " + throwable.getMessage());
                throwable.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(context, "Upload error: " + throwable.getMessage(), Toast.LENGTH_LONG).show()
                );
            }

            @Override
            public void onComplete(@NonNull UploadModel result) {
                System.out.println(result);
                runOnUiThread(() ->
                        Toast.makeText(context, "Upload completed: " + result.getUrl(), Toast.LENGTH_LONG).show()
                );
            }
        }, false);
    }

    private void runOnUiThread(Runnable runnable) {
        mainHandler.post(runnable);
    }
}
