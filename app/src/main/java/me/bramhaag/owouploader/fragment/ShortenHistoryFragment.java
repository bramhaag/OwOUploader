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
import me.bramhaag.owouploader.adapter.HistoryAdapter;
import me.bramhaag.owouploader.api.OwOAPI;
import me.bramhaag.owouploader.api.callback.ResultCallback;
import me.bramhaag.owouploader.api.model.ObjectModel;
import me.bramhaag.owouploader.databinding.FragmentHistoryBinding;
import me.bramhaag.owouploader.db.HistoryDatabase;
import me.bramhaag.owouploader.db.entity.ShortenItem;
import me.bramhaag.owouploader.util.ApiUtil;

/**
 * ShortenHistoryFragment.
 */
@AndroidEntryPoint
public class ShortenHistoryFragment extends HistoryFragment<ShortenItem> {

    @Inject
    HistoryDatabase database;

    @Inject
    OwOAPI api;

    @Override
    protected Single<List<ShortenItem>> provideLocalHistory() {
        return database.shortenItemDao().getAll();
    }

    @Override
    protected void getHistory(int offset, ResultCallback<ObjectModel[]> callback) {
        api.getObjects(null, offset, "link", null, callback);
    }

    @Override
    protected void setRecyclerView(RecyclerView recyclerView) {
        ((MainActivity) requireActivity()).getShortenDialog().setAdapter((HistoryAdapter) recyclerView.getAdapter());
    }

    @Override
    protected ShortenItem modelToItem(ObjectModel model) {
        var key = ApiUtil.normalizeKey(model.getKey());
        // TODO insert other url here
        var destUrl = URI.create(model.getDestUrl());
        var url = URI.create("https://owo.whats-th.is/" + model.getKey());
        return new ShortenItem(key, destUrl, url, model.getCreatedAt().toInstant());

    }
}