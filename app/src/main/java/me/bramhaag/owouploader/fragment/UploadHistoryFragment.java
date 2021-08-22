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

package me.bramhaag.owouploader.fragment;

import androidx.recyclerview.widget.RecyclerView;
import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.core.Single;
import java.net.URI;
import java.util.List;
import javax.inject.Inject;
import me.bramhaag.owouploader.activity.MainActivity;
import me.bramhaag.owouploader.api.OwOAPI;
import me.bramhaag.owouploader.api.callback.ResultCallback;
import me.bramhaag.owouploader.api.model.ObjectModel;
import me.bramhaag.owouploader.db.HistoryDatabase;
import me.bramhaag.owouploader.db.entity.UploadItem;
import me.bramhaag.owouploader.util.ApiUtil;

/**
 * UploadHistoryFragment.
 */
@AndroidEntryPoint
public class UploadHistoryFragment extends HistoryFragment<UploadItem> {

    @Inject
    HistoryDatabase database;

    @Inject
    OwOAPI api;

    @Override
    protected Single<List<UploadItem>> provideLocalHistory() {
        return database.uploadItemDao().getAll();
    }

    @Override
    protected void getHistory(int offset, ResultCallback<ObjectModel[]> callback) {
        api.getObjects(null, offset, "file", null, callback);
    }

    @Override
    protected UploadItem modelToItem(ObjectModel model) {
        var key = ApiUtil.normalizeKey(model.getKey());
        // TODO insert other url here
        var url = URI.create("https://owo.whats-th.is/" + model.getKey());
        return new UploadItem(key, key, url, model.getCreatedAt().toInstant());
    }

    @Override
    protected void setRecyclerView(RecyclerView recyclerView) {
        ((MainActivity) requireActivity()).getUploadHandler().setRecyclerView(recyclerView);
    }
}