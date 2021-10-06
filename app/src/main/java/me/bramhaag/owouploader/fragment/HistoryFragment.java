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
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import me.bramhaag.owouploader.adapter.EndlessRecyclerViewScrollListener;
import me.bramhaag.owouploader.adapter.HistoryAdapter;
import me.bramhaag.owouploader.adapter.item.AssociatedItem;
import me.bramhaag.owouploader.adapter.item.ViewHolderItem;
import me.bramhaag.owouploader.api.OwOAPI;
import me.bramhaag.owouploader.api.callback.ResultCallback;
import me.bramhaag.owouploader.api.model.ObjectModel;
import me.bramhaag.owouploader.databinding.FragmentHistoryBinding;
import me.bramhaag.owouploader.db.entity.HistoryItem;

/**
 * HistoryFragment.
 */
public abstract class HistoryFragment<T extends HistoryItem> extends Fragment {

    @Inject
    OwOAPI api;

    @Inject
    HistoryAdapter adapter;

    private Deque<T> localHistory;

    private final CompositeDisposable disposables = new CompositeDisposable();

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        final var binding = FragmentHistoryBinding.inflate(inflater);

        final var layoutManager = new LinearLayoutManager(getContext());
        final var recyclerView = binding.recyclerView;
        final var refreshLayout = binding.refreshLayout;

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(null);

        setRecyclerView(recyclerView);

        var scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
            @Override
            public void onLoadMore(EndlessRecyclerViewScrollListener listener, int offset, RecyclerView view) {
                loadNext(listener, offset, recyclerView, adapter);
            }
        };

        recyclerView.addOnScrollListener(scrollListener);
        refreshLayout.setOnRefreshListener(() -> onRefresh(refreshLayout, recyclerView, scrollListener, adapter));

        onRefresh(refreshLayout, recyclerView, scrollListener, adapter);
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
                    return;
                }

                // Map associated objects to UploadItem, and keep track of the oldest upload fetched
                var items = new HashMap<String, ViewHolderItem>();
                var oldest = Instant.MAX;

                for (var r : result) {
                    var item = new AssociatedItem(modelToItem(r));
                    items.put(item.key(), item);

                    if (oldest.isAfter(item.createdAt())) {
                        oldest = item.createdAt();
                    }
                }

                // Add all items from the local history that were uploaded after the oldest date
                // The associated uploads are overwritten with the local history, because these contain more information
                while (!localHistory.isEmpty() && localHistory.element().createdAt().isAfter(oldest)) {
                    var item = localHistory.pop();
                    var existingItem = items.get(item.key());

                    if (existingItem != null) {
                        ((AssociatedItem) existingItem).setItem(item);
                    } else {
                        items.put(item.key(), item);
                    }
                }

                var sortedItems = new ArrayList<>(items.values());
                Collections.sort(sortedItems, Collections.reverseOrder());

                listener.incrementCurrentPage(result.length);
                listener.setLoading(false);

                recyclerView.post(() -> adapter.addItems(sortedItems));
            }
        };

        getHistory(offset, callback);
    }

    public void withLocalHistory(@NonNull Consumer<List<T>> onSuccess) {
        var result = provideLocalHistory()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onSuccess);

        disposables.add(result);
    }

    public void onRefresh(SwipeRefreshLayout refreshLayout, RecyclerView recyclerView,
            EndlessRecyclerViewScrollListener scrollListener, HistoryAdapter adapter) {
        refreshLayout.setRefreshing(false);

        scrollListener.resetState();
        adapter.clearHistory();

        withLocalHistory(history -> {
            localHistory = history.stream()
                    .sorted(Collections.reverseOrder())
                    .collect(Collectors.toCollection(ArrayDeque::new));

            scrollListener.onScrolled(recyclerView, 0, 0);
        });
    }


    protected abstract Single<List<T>> provideLocalHistory();

    protected abstract void getHistory(int offset, ResultCallback<ObjectModel[]> callback);

    protected abstract void setRecyclerView(RecyclerView recyclerView);

    protected abstract T modelToItem(ObjectModel model);
}