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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import me.bramhaag.owouploader.R;
import me.bramhaag.owouploader.adapter.viewholder.HistoryViewHolder;
import me.bramhaag.owouploader.adapter.viewholder.ProgressViewHolder;
import me.bramhaag.owouploader.adapter.viewholder.ShortenViewHolder;
import me.bramhaag.owouploader.adapter.viewholder.UploadViewHolder;
import me.bramhaag.owouploader.components.HistoryItem;
import me.bramhaag.owouploader.components.ProgressItem;
import me.bramhaag.owouploader.components.ShortenHistoryItem;
import me.bramhaag.owouploader.components.UploadHistoryItem;


/**
 * {@link RecyclerView.Adapter} for shorten history.
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryViewHolder<? extends HistoryItem>> {

    public final List<HistoryItem> items;
    private final Map<HistoryItem, Integer> itemsIndex;

    public HistoryAdapter(List<HistoryItem> items) {
        this.items = new ArrayList<>(items);
        this.itemsIndex = IntStream.range(0, items.size()).boxed().collect(Collectors.toMap(items::get, i -> i));
    }

    @Override
    public int getItemViewType(int position) {
        var item = items.get(position);
        if (item instanceof UploadHistoryItem) {
            return 0;
        } else if (item instanceof ShortenHistoryItem) {
            return 1;
        } else if (item instanceof ProgressItem) {
            return 2;
        }

        throw new IllegalArgumentException("No type found for item " + item);
    }

    @NonNull
    @Override
    public HistoryViewHolder<? extends HistoryItem> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
            default:
                throw new IllegalArgumentException("viewType " + viewType + " not found");
        }
    }

    // Unchecked cast from HistoryViewHolder<T extends HistoryItem> to HistoryViewHolder<Concrete Type>. Cast is safe
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
    public void addItem(@NonNull HistoryItem item) {
        var index = items.size();

        items.add(item);
        itemsIndex.put(item, index);

        notifyItemInserted(index);
    }

    /**
     * Modify an item in the view.
     *
     * @param item the item
     */
    public void modifyItem(@NonNull HistoryItem item) {
        var index = indexOf(item);
        notifyItemChanged(index);
    }

    /**
     * Replace an item in the view.
     *
     * @param originalItem the item to replace
     * @param newItem      the new item
     */
    public void replaceItem(HistoryItem originalItem, HistoryItem newItem) {
        var index = indexOf(originalItem);
        items.set(index, newItem);
        itemsIndex.remove(originalItem);
        itemsIndex.put(newItem, index);

        notifyItemChanged(index);
    }

    /**
     * Remove an item from the view.
     *
     * @param item the item
     */
    public void removeItem(HistoryItem item) {
        var index = indexOf(item);

        items.remove(index);
        itemsIndex.remove(item);

        notifyItemRemoved(index);

        for (int i = index; i < items.size(); i++) {
            var key = items.get(i);
            var value = indexOf(key);

            itemsIndex.put(key, value - 1);
        }
    }

    private int indexOf(HistoryItem item) {
        Integer index = itemsIndex.get(item);
        assert index != null;
        
        return index;
    }
}
