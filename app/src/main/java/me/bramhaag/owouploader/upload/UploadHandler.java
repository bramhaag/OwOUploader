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

package me.bramhaag.owouploader.upload;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayout.Tab;
import java.net.URI;
import java.util.Date;
import me.bramhaag.owouploader.adapter.HistoryAdapter;
import me.bramhaag.owouploader.api.OwOAPI;
import me.bramhaag.owouploader.api.callback.ProgressResultCallback;
import me.bramhaag.owouploader.api.model.UploadModel;
import me.bramhaag.owouploader.components.ProgressItem;
import me.bramhaag.owouploader.components.UploadHistoryItem;
import me.bramhaag.owouploader.file.ContentProvider;

public class UploadHandler {

    private final OwOAPI api;
    private final Tab tab;
    private final Context context;
    private final Handler mainHandler;

    private HistoryAdapter adapter;
    private RecyclerView recyclerView;

    public UploadHandler(OwOAPI api, Context context, TabLayout.Tab tab) {
        this.api = api;
        this.context = context;
        this.mainHandler = new Handler(context.getMainLooper());
        this.tab = tab;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        this.adapter = ((HistoryAdapter) recyclerView.getAdapter());
    }

    public void upload(ContentProvider file) {
        upload(file, () -> {});
    }

    public void upload(ContentProvider file, Runnable onFinish) {
        ProgressItem item = new ProgressItem();
        var call = api.uploadFile(file, new ProgressResultCallback<>() {

            @Override
            public void onStart() {
                tab.select();

                item.setName(file.getName());
                item.setSize(file.getSize());
                adapter.addItem(item);

                recyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
            }

            @Override
            public void onProgress(long progress) {
                if (item.isCanceled()) {
                    return;
                }

                item.setUploaded(progress);
                runOnUiThread(() -> adapter.modifyItem(item));
            }

            @Override
            public void onError(@NonNull Throwable throwable) {
                if (item.isCanceled()) {
                    return;
                }

                System.out.println("Upload error: " + throwable.getMessage());
                throwable.printStackTrace();

                runOnUiThread(() -> {
                    adapter.removeItem(item);
                    Toast.makeText(context, "Upload error: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                });

                onFinish.run();
            }

            @Override
            public void onComplete(@NonNull UploadModel result) {
                var newItem = new UploadHistoryItem(file.getName(),
                        URI.create("https://owo.whats-th.is/" + result.getUrl()), new Date());

                runOnUiThread(() -> {
                    if (item.isCanceled()) {
                        adapter.addItem(newItem);
                    } else {
                        adapter.replaceItem(item, newItem);
                    }

                    Toast.makeText(context, "Upload completed", Toast.LENGTH_SHORT).show();
                });

                onFinish.run();
            }
        }, false);

        item.setOnCancel(() -> {
            call.cancel();
            runOnUiThread(() -> adapter.removeItem(item));
        });
    }

    private void runOnUiThread(Runnable runnable) {
        mainHandler.post(runnable);
    }
}
