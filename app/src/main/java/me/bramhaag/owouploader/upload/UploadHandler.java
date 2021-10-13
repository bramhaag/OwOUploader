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
import dagger.hilt.android.qualifiers.ApplicationContext;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.net.URI;
import javax.inject.Inject;
import me.bramhaag.owouploader.adapter.HistoryAdapter;
import me.bramhaag.owouploader.adapter.item.ProgressItem;
import me.bramhaag.owouploader.api.OwOAPI;
import me.bramhaag.owouploader.api.callback.ProgressResultCallback;
import me.bramhaag.owouploader.api.model.UploadModel;
import me.bramhaag.owouploader.db.HistoryDatabase;
import me.bramhaag.owouploader.db.entity.UploadItem;
import me.bramhaag.owouploader.file.ContentProvider;
import me.bramhaag.owouploader.util.ApiUtil;
import me.bramhaag.owouploader.util.Runnables;

/**
 * Class that handles uploading.
 */
public class UploadHandler {

    private final OwOAPI api;
    private final Context context;
    private final Handler mainHandler;
    private final HistoryDatabase database;

    private HistoryAdapter adapter;
    private RecyclerView recyclerView;
    private Tab tab;

    /**
     * Create a new {@link UploadHandler}.
     *
     * @param api      the api
     * @param context  the context
     * @param database the database
     */
    @Inject
    public UploadHandler(OwOAPI api, @ApplicationContext Context context, HistoryDatabase database) {
        this.api = api;
        this.context = context;
        this.mainHandler = new Handler(context.getMainLooper());
        this.database = database;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        this.adapter = ((HistoryAdapter) recyclerView.getAdapter());
    }

    public void setTab(TabLayout.Tab tab) {
        this.tab = tab;
    }

    /**
     * Upload content.
     *
     * @param content the content
     */
    public void upload(ContentProvider content) {
        upload(content, Runnables.EMPTY);
    }

    /**
     * Upload content.
     *
     * @param content  the content
     * @param onFinish on finish callback
     */
    public void upload(ContentProvider content, Runnable onFinish) {
        var item = new ProgressItem();
        var call = api.uploadFile(content, new ProgressResultCallback<>() {

            @Override
            public void onStart() {
                tab.select();

                item.setName(content.getName());
                item.setSize(content.getSize());
                adapter.addItemFirst(item);

                recyclerView.smoothScrollToPosition(0);
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

                runOnUiThread(() -> {
                    adapter.removeItem(item);
                    Toast.makeText(context, "Upload error: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                });

                onFinish.run();
            }

            @Override
            public void onComplete(@NonNull UploadModel result) {
                var newItem = new UploadItem(ApiUtil.normalizeKey(result.getUrl()), content.getName(), URI.create("https://owo.whats-th.is/" + result.getUrl()));

                runOnUiThread(() -> {
                    if (!item.isCanceled()) {
                        adapter.replaceItem(item, newItem);
                    } else {
                        // If the upload is cancelled but still succeeded, the progress item is already removed
                        adapter.addItemFirst(newItem);
                    }

                    Toast.makeText(context, "Upload completed", Toast.LENGTH_SHORT).show();
                });

                database.uploadItemDao().insert(newItem)
                        .subscribeOn(Schedulers.io())
                        .subscribe();

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
