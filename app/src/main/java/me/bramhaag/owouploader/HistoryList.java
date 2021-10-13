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

package me.bramhaag.owouploader;

import android.annotation.SuppressLint;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import me.bramhaag.owouploader.adapter.filter.Filter;

public class HistoryList<T, U> extends ArrayList<T> {

    private final ArrayList<T> items;
    private final Filter<T, U> filter;

    private final RecyclerView.Adapter<?> adapter;
    private U constraint;

    public HistoryList(Filter<T, U> filter, RecyclerView.Adapter<?> adapter) {
        this.filter = filter;
        this.adapter = adapter;

        items = new ArrayList<>();
    }

    // The entire dataset has changed
    @SuppressLint("NotifyDataSetChanged")
    public void setConstraint(U constraint) {
        this.constraint = constraint;

        super.clear();

        var filteredItems = items.stream()
                .filter(this::accept)
                .collect(Collectors.toList());

        System.out.println(filteredItems.size());

        super.addAll(filteredItems);

        adapter.notifyDataSetChanged();
    }


    public void addFirst(T t) {
        items.add(0, t);

        if (accept(t)) {
            super.add(0, t);
            adapter.notifyItemInserted(0);
        }
    }

    public void addLast(T t) {
        items.add(t);

        if (accept(t)) {
            super.add(t);
            adapter.notifyItemInserted(super.size() - 1);
        }
    }

    public void removeLast() {
        var item = items.remove(items.size() - 1);

        if (super.remove(item)) {
            adapter.notifyItemRemoved(super.size());
        }
    }

    public boolean addAll(Collection<? extends T> ts) {
        items.addAll(ts);

        var filteredItems = ts.stream()
                .filter(this::accept)
                .collect(Collectors.toList());

        if (filteredItems.size() == 0) {
            return true;
        }

        var size = super.size();
        super.addAll(filteredItems);
        adapter.notifyItemRangeInserted(size - 1, filteredItems.size());

        return true;
    }

    public T set(int index, T t) {
        T item;
        if (accept(t)) {
            item = super.set(index, t);
            adapter.notifyItemChanged(index);
        } else {
            item = super.remove(index);
            adapter.notifyItemRemoved(index);
        }

        return items.set(items.indexOf(item), t);
    }

    public T remove(int index) {
        var item = super.remove(index);
        adapter.notifyItemRemoved(index);

        items.remove(item);
        return item;
    }

    public T getLast() {
        return super.get(super.size() - 1);
    }

    private boolean accept(T t) {
        return filter.accept(t, constraint);
    }
}
