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

package me.bramhaag.owouploader.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.rxjava3.core.Single;
import java.util.LinkedList;
import java.util.List;
import me.bramhaag.owouploader.R;
import me.bramhaag.owouploader.adapter.viewholder.HistoryViewHolder;
import me.bramhaag.owouploader.adapter.viewholder.LoadingViewHolder;
import me.bramhaag.owouploader.adapter.viewholder.ProgressViewHolder;
import me.bramhaag.owouploader.adapter.viewholder.ShortenViewHolder;
import me.bramhaag.owouploader.adapter.viewholder.UploadViewHolder;
import me.bramhaag.owouploader.adapter.viewholder.item.LoadingItem;
import me.bramhaag.owouploader.adapter.viewholder.item.ProgressItem;
import me.bramhaag.owouploader.adapter.viewholder.item.ViewHolderItem;
import me.bramhaag.owouploader.db.entity.ShortenItem;
import me.bramhaag.owouploader.db.entity.UploadItem;


/**
 * {@link RecyclerView.Adapter} for shorten history.
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryViewHolder<? extends ViewHolderItem>> {

    private final LinkedList<ViewHolderItem> items;

    private boolean loading = false;

    /**
     * Create a new HistoryAdapter from a preexisting source.
     *
     * @param source the source
     */
    public HistoryAdapter() {
        this.items = new LinkedList<>();
    }

    @Override
    public int getItemViewType(int position) {
        var item = items.get(position);
        if (item instanceof UploadItem) {
            return 0;
        } else if (item instanceof ShortenItem) {
            return 1;
        } else if (item instanceof ProgressItem) {
            return 2;
        } else if (item instanceof LoadingItem) {
            return 3;
        }

        throw new IllegalArgumentException("No type found for item " + item);
    }

    @NonNull
    @Override
    public HistoryViewHolder<? extends ViewHolderItem> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        var inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case 0: {
                var view = inflater.inflate(R.layout.upload_history_item, parent, false);
                return new UploadViewHolder(view);
            }
            case 1: {
                var view = inflater.inflate(R.layout.shorten_history_item, parent, false);
                return new ShortenViewHolder(view);
            }
            case 2: {
                var view = inflater.inflate(R.layout.progress_item, parent, false);
                return new ProgressViewHolder(view);
            }
            case 3: {
                var view = inflater.inflate(R.layout.loading_history_item, parent, false);
                return new LoadingViewHolder(view);
            }
            default:
                throw new IllegalArgumentException("viewType " + viewType + " not found");
        }
    }

    // Unchecked cast from HistoryViewHolder<T extends ViewHolderItem> to HistoryViewHolder<Concrete Type>. Cast is safe
    // because type checking is done in the getItemViewType(int) method.
    @SuppressWarnings("unchecked")
    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        holder.initializeView(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * Add a new item to the view.
     *
     * @param item the item.
     */
    public synchronized void addItemFirst(@NonNull ViewHolderItem item) {
        items.addFirst(item);

        notifyItemInserted(0);
    }

    public synchronized void addItemLast(@NonNull ViewHolderItem item) {
        if (this.loading) {
            items.removeLast();
            items.addLast(item);
            this.loading = false;

            notifyItemChanged(items.size() - 1);
            return;
        }

        items.addLast(item);
        notifyItemInserted(items.size() - 1);
    }

    /**
     * Add multiple items to the view.
     *
     * @param items the items
     */
    public synchronized void addItems(@NonNull List<? extends ViewHolderItem> items) {

        if (this.loading) {
            this.items.removeLast();
            this.loading = false;

            var index = items.size();
            notifyItemChanged(index);

            this.items.addAll(items);
            notifyItemRangeChanged(index + 1, items.size());

            return;
        }

        var index = items.size();
        this.items.addAll(items);
        notifyItemRangeInserted(index, items.size());
    }

    /**
     * Modify an item in the view.
     *
     * @param item the item
     */
    public synchronized void modifyItem(@NonNull ViewHolderItem item) {
        var index = indexOf(item);
        notifyItemChanged(index);
    }

    /**
     * Replace an item in the view.
     *
     * @param originalItem the item to replace
     * @param newItem      the new item
     */
    public synchronized void replaceItem(ViewHolderItem originalItem, ViewHolderItem newItem) {
        var index = indexOf(originalItem);
        items.set(index, newItem);
//        itemsIndex.remove(originalItem);
//        itemsIndex.put(newItem, index);

        notifyItemChanged(index);
    }

    /**
     * Remove an item from the view.
     *
     * @param item the item
     */
    public synchronized void removeItem(ViewHolderItem item) {
        var index = indexOf(item);

        items.remove(index);
//        itemsIndex.remove(item);

        notifyItemRemoved(index);

//        for (int i = index; i < items.size(); i++) {
//            var key = items.get(i);
//            var value = indexOf(key);
//
//            itemsIndex.put(key, value - 1);
//        }
    }

    public synchronized void clearHistory() {
        int start = items.size() - 1;
        int end = start;

        while (!items.isEmpty() && !(items.getLast() instanceof ProgressItem)) {
            items.removeLast();
            end--;
        }

        loading = false;

        notifyItemRangeRemoved(end, start - end);
    }

    /**
     * Add a loading indicator.
     */
    public void setLoading(boolean loading) {
        if (loading && !this.loading) {
            addItemLast(new LoadingItem());
        } else if (!loading && this.loading) {
            items.removeLast();
            notifyItemRemoved(items.size());
        }

        this.loading = loading;
    }

    private int indexOf(ViewHolderItem item) {
        return items.indexOf(item);
    }
}
