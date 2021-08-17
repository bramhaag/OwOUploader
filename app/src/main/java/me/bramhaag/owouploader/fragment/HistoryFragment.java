///*
// * OwO Uploader
// * Copyright (C) 2021
// *
// * This program is free software: you can redistribute it and/or modify
// * it under the terms of the GNU Affero General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU Affero General Public License for more details.
// *
// * You should have received a copy of the GNU Affero General Public License
// * along with this program.  If not, see <http://www.gnu.org/licenses/>.
// */
//
//package me.bramhaag.owouploader.fragment;
//
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
//import androidx.viewbinding.ViewBinding;
//import dagger.hilt.android.AndroidEntryPoint;
//import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
//import io.reactivex.rxjava3.core.Single;
//import io.reactivex.rxjava3.disposables.CompositeDisposable;
//import io.reactivex.rxjava3.functions.Consumer;
//import io.reactivex.rxjava3.schedulers.Schedulers;
//import java.net.URI;
//import java.time.Instant;
//import java.util.ArrayDeque;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.Deque;
//import java.util.HashMap;
//import java.util.List;
//import java.util.stream.Collectors;
//import javax.inject.Inject;
//import me.bramhaag.owouploader.adapter.EndlessRecyclerViewScrollListener;
//import me.bramhaag.owouploader.adapter.HistoryAdapter;
//import me.bramhaag.owouploader.api.OwOAPI;
//import me.bramhaag.owouploader.api.callback.ResultCallback;
//import me.bramhaag.owouploader.api.model.ObjectModel;
//import me.bramhaag.owouploader.db.HistoryDatabase;
//import me.bramhaag.owouploader.db.entity.UploadItem;
//
///**
// * UploadHistoryFragment.
// */
//@AndroidEntryPoint
//public abstract class HistoryFragment<T extends Comparable<? super T>, B extends ViewBinding, R> extends Fragment {
//
//    @Inject
//    HistoryDatabase database;
//
//    @Inject
//    OwOAPI api;
//
//    private Deque<? super T> localHistory;
//
//    private final CompositeDisposable disposables = new CompositeDisposable();
//
//    public HistoryFragment() {
//        // Required empty public constructor
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
//            @Nullable Bundle savedInstanceState) {
//        final var binding = getBinding(inflater);
//
//        final var adapter = new HistoryAdapter();
//        final var layoutManager = new LinearLayoutManager(getContext());
//        final var recyclerView = getRecyclerView(binding);
//        final var refreshLayout = getRefreshLayout(binding);
//
//        recyclerView.setAdapter(adapter);
//        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setItemAnimator(null);
//
//        //         ((MainActivity) requireActivity()).getUploadHandler().setRecyclerView(binding.recyclerView); todo
//
//        var scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
//            @Override
//            public void onLoadMore(EndlessRecyclerViewScrollListener listener, int offset, RecyclerView view) {
//                loadNext(listener, offset, uploadHistoryAdapter, binding.recyclerView);
//            }
//        };
//
//        recyclerView.addOnScrollListener(scrollListener);
//        refreshLayout.setOnRefreshListener(() -> onRefresh(refreshLayout, recyclerView, scrollListener, adapter));
//
//        onRefresh(refreshLayout, recyclerView, scrollListener, adapter);
//        return binding.getRoot();
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//
//        disposables.clear();
//    }
//
//    public void withLocalHistory(@NonNull Consumer<List<? super T>> onSuccess) {
//        var result = provideLocalHistory()
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(onSuccess);
//
//        disposables.add(result);
//    }
//
//    public void onRefresh(SwipeRefreshLayout refreshLayout, RecyclerView recyclerView,
//            EndlessRecyclerViewScrollListener scrollListener, HistoryAdapter adapter) {
//        refreshLayout.setRefreshing(false);
//
//        scrollListener.resetState();
//        adapter.clearHistory();
//
//        withLocalHistory(history -> {
//            localHistory = history.stream()
//                    .sorted(Collections.reverseOrder(Comparator.comparing(a -> ((T) a))))
//                    .collect(Collectors.toCollection(ArrayDeque::new));
//
//            scrollListener.onScrolled(recyclerView, 0, 0);
//        });
//    }
//
//    public void loadNext(EndlessRecyclerViewScrollListener listener, int offset, HistoryAdapter adapter,
//            RecyclerView recyclerView) {
//        var callback = new ResultCallback<R[]>() {
//
//            @Override
//            public void onStart() {
//                listener.setLoading(true);
//                recyclerView.post(() -> adapter.setLoading(true));
//            }
//
//            @Override
//            public void onError(@NonNull Throwable throwable) {
//                recyclerView.post(() -> adapter.setLoading(false));
//            }
//
//            @Override
//            public void onComplete(@NonNull R[] result) {
//                if (result.length == 0) {
//                    if (!localHistory.isEmpty()) {
//                        recyclerView.post(() -> adapter.addItems(new ArrayList<>(localHistory)));
//                    }
//
//                    adapter.setLoading(false);
//                }
//
//                var items = new HashMap<String, UploadItem>();
//                var oldest = Instant.MAX;
//
//                for (var r : result) {
//                    var key = r.getKey().split("\\.")[0].replace("/", "");
//                    var url = URI.create("https://owo.whats-th.is" + r.getKey());
//                    var item = new UploadItem(key, key, url, r.getCreatedAt().toInstant());
//                    items.put(key, item);
//
//                    if (oldest.isAfter(item.createdAt)) {
//                        oldest = item.createdAt;
//                    }
//                }
//
//                while (!localHistory.isEmpty() && localHistory.element().createdAt.isAfter(oldest)) {
//                    var item = localHistory.pop();
//                    items.put(item.key, item);
//                }
//
//                var sortedItems = new ArrayList<>(items.values());
//                Collections.sort(sortedItems, Collections.reverseOrder(Comparator.comparing(a -> a)));
//
//                listener.incrementCurrentPage(result.length);
//                listener.setLoading(false);
//
//                recyclerView.post(() -> adapter.addItems(sortedItems));
//            }
//        };
//
//        getHistory(callback);
//    }
//
//
//    protected abstract Single<List<T>> provideLocalHistory();
//
//    protected abstract B getBinding(@NonNull LayoutInflater inflater);
//
//    protected abstract RecyclerView getRecyclerView(B binding);
//
//    protected abstract SwipeRefreshLayout getRefreshLayout(B binding);
//
//    protected abstract void getHistory(ResultCallback<R[]> callback);
//
////    @Override
////    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
////            Bundle savedInstanceState) {
////        binding = FragmentHistoryBinding.inflate(inflater);
////
////        var uploadHistoryAdapter = new HistoryAdapter(database.uploadItemDao().getAll());
////
////        var layoutManager = new LinearLayoutManager(getContext());
//////        layoutManager.setReverseLayout(true);
//////        layoutManager.setStackFromEnd(true);
////
////        binding.recyclerView.setAdapter(uploadHistoryAdapter);
////        binding.recyclerView.setLayoutManager(layoutManager);
////        binding.recyclerView.setItemAnimator(null);
////
////        ((MainActivity) requireActivity()).getUploadHandler().setRecyclerView(binding.recyclerView);
////
////        var scrollListener = new EndlessRecyclerViewScrollListener(layoutManager) {
////            @Override
////            public void onLoadMore(EndlessRecyclerViewScrollListener listener, int offset, RecyclerView view) {
////                loadNext(listener, offset, uploadHistoryAdapter, binding.recyclerView);
////            }
////        };
////
////        binding.recyclerView.addOnScrollListener(scrollListener);
////
////        database.uploadItemDao().getAll().subscribeOn(Schedulers.io())
////                .observeOn(AndroidSchedulers.mainThread())
////                .subscribe(l -> {
////                    localHistory = l.stream()
////                            .sorted(Collections.reverseOrder(UploadItem::compareTo))
////                            .collect(Collectors.toCollection(ArrayDeque::new));
////
////                    scrollListener.onScrolled(binding.recyclerView, 0, 0);
////                });
////
////        binding.refreshLayout.setOnRefreshListener(() -> {
////            binding.refreshLayout.setRefreshing(false);
////
////            scrollListener.resetState();
////            uploadHistoryAdapter.clearHistory();
////
////            database.uploadItemDao().getAll().subscribeOn(Schedulers.io())
////                    .observeOn(AndroidSchedulers.mainThread())
////                    .subscribe(l -> {
////                        localHistory = l.stream()
////                                .sorted(Collections.reverseOrder(UploadItem::compareTo))
////                                .collect(Collectors.toCollection(ArrayDeque::new));
////
////                        scrollListener.onScrolled(binding.recyclerView, 0, 0);
////                    });
////        });
////
////        return binding.getRoot();
////    }
////
////    public void loadNext(EndlessRecyclerViewScrollListener listener, int offset, HistoryAdapter adapter,
////            RecyclerView recyclerView) {
////        var callback = new ResultCallback<ObjectModel[]>() {
////
////            @Override
////            public void onStart() {
////                listener.setLoading(true);
////                recyclerView.post(() -> adapter.setLoading(true));
////            }
////
////            @Override
////            public void onError(@NonNull Throwable throwable) {
////                recyclerView.post(() -> adapter.setLoading(false));
////            }
////
////            @Override
////            public void onComplete(@NonNull ObjectModel[] result) {
////                if (result.length == 0) {
////                    if (!localHistory.isEmpty()) {
////                        recyclerView.post(() -> adapter.addItems(new ArrayList<>(localHistory)));
////                    }
////
////                    adapter.setLoading(false);
////                }
////
////                var items = new HashMap<String, UploadItem>();
////                var oldest = Instant.MAX;
////
////                for (var r : result) {
////                    var key = r.getKey().split("\\.")[0].replace("/", "");
////                    var url = URI.create("https://owo.whats-th.is" + r.getKey());
////                    var item = new UploadItem(key, key, url, r.getCreatedAt().toInstant());
////                    items.put(key, item);
////
////                    if (oldest.isAfter(item.createdAt)) {
////                        oldest = item.createdAt;
////                    }
////                }
////
////                while (!localHistory.isEmpty() && localHistory.element().createdAt.isAfter(oldest)) {
////                    var item = localHistory.pop();
////                    items.put(item.key, item);
////                }
////
////                var sortedItems = new ArrayList<>(items.values());
////                Collections.sort(sortedItems, Collections.reverseOrder(Comparator.comparing(a -> a)));
////
////                listener.incrementCurrentPage(result.length);
////                listener.setLoading(false);
////
////                recyclerView.post(() -> adapter.addItems(sortedItems));
////            }
////        };
////
////        api.getObjects(3, offset, "file", null, callback);
////
//////        callback.onStart();
//////
//////        database.uploadItemDao().getAll().subscribeOn(Schedulers.io())
//////                .delay(3, TimeUnit.SECONDS)
//////                .observeOn(AndroidSchedulers.mainThread())
//////                .subscribe(callback::onComplete);
////    }
//}