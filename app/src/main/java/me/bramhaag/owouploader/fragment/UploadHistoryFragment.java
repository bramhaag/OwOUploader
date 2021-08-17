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

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.net.URI;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.stream.Collectors;
import javax.inject.Inject;
import me.bramhaag.owouploader.activity.MainActivity;
import me.bramhaag.owouploader.adapter.EndlessRecyclerViewScrollListener;
import me.bramhaag.owouploader.adapter.HistoryAdapter;
import me.bramhaag.owouploader.api.OwOAPI;
import me.bramhaag.owouploader.api.callback.ResultCallback;
import me.bramhaag.owouploader.api.model.ObjectModel;
import me.bramhaag.owouploader.databinding.FragmentHistoryBinding;
import me.bramhaag.owouploader.db.HistoryDatabase;
import me.bramhaag.owouploader.db.entity.UploadItem;
import me.bramhaag.owouploader.util.ApiUtil;

/**
 * UploadHistoryFragment.
 */
@AndroidEntryPoint
public class UploadHistoryFragment extends Fragment {

    public FragmentHistoryBinding binding;

    @Inject
    HistoryDatabase database;

    @Inject
    OwOAPI api;

    private Deque<UploadItem> localHistory;

    private final CompositeDisposable disposables = new CompositeDisposable();

    public UploadHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(inflater);

        var uploadHistoryAdapter = new HistoryAdapter();

        var layoutManager = new LinearLayoutManager(getContext());

        binding.recyclerView.setAdapter(uploadHistoryAdapter);
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setItemAnimator(null);

        ((MainActivity) requireActivity()).getUploadHandler().setRecyclerView(binding.recyclerView);

        var scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(EndlessRecyclerViewScrollListener listener, int offset, RecyclerView view) {
                loadNext(listener, offset, view, uploadHistoryAdapter);
            }
        };

        binding.recyclerView.addOnScrollListener(scrollListener);

        binding.refreshLayout.setOnRefreshListener(() -> onRefresh(scrollListener, uploadHistoryAdapter));

        onRefresh(scrollListener, uploadHistoryAdapter);

        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        disposables.dispose();
    }

    private void loadNext(EndlessRecyclerViewScrollListener listener, int offset, RecyclerView recyclerView,
            HistoryAdapter adapter) {
        var callback = new ResultCallback<ObjectModel[]>() {

            @Override
            public void onStart() {
                listener.setLoading(true);
                recyclerView.post(() -> adapter.setLoading(true));
            }

            @Override
            public void onError(@NonNull Throwable throwable) {
                recyclerView.post(() -> adapter.setLoading(false));
            }

            @Override
            public void onComplete(@NonNull ObjectModel[] result) {
                // If there are no associated objects (left), add the remaining local history
                if (result.length == 0) {
                    recyclerView.post(() -> adapter.addItems(new ArrayList<>(localHistory)));
                }

                // Map associated objects to UploadItem, and keep track of the oldest upload fetched
                var items = new HashMap<String, UploadItem>();
                var oldest = Instant.MAX;

                for (var r : result) {
                    var item = modelToItem(r);
                    items.put(item.key, item);

                    if (oldest.isAfter(item.createdAt)) {
                        oldest = item.createdAt;
                    }
                }

                // Add all items from the local history that were uploaded after the oldest date
                // The associated uploads are overwritten with the local history
                while (!localHistory.isEmpty() && localHistory.element().createdAt.isAfter(oldest)) {
                    var item = localHistory.pop();
                    items.put(item.key, item);
                }

                var sortedItems = new ArrayList<>(items.values());
                Collections.sort(sortedItems, Collections.reverseOrder(Comparator.comparing(a -> a)));

                listener.incrementCurrentPage(result.length);
                listener.setLoading(false);

                recyclerView.post(() -> adapter.addItems(sortedItems));
            }
        };

        api.getObjects(3, offset, "file", null, callback);
    }

    private void onRefresh(EndlessRecyclerViewScrollListener scrollListener, HistoryAdapter adapter) {
        binding.refreshLayout.setRefreshing(false);

        scrollListener.resetState();
        adapter.clearHistory();

        var disposable = database.uploadItemDao().getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(history -> {
                    localHistory = history.stream()
                            .sorted(Collections.reverseOrder(UploadItem::compareTo))
                            .collect(Collectors.toCollection(ArrayDeque::new));

                    scrollListener.onScrolled(binding.recyclerView, 0, 0);
                });

        disposables.add(disposable);
    }

    private UploadItem modelToItem(ObjectModel model) {
        var key = ApiUtil.normalizeKey(model.getKey());
        // TODO insert other url here
        var url = URI.create("https://owo.whats-th.is/" + model.getKey());
        return new UploadItem(key, key, url, model.getCreatedAt().toInstant());
    }
}