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

package me.bramhaag.owouploader.adapter.viewholder;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import me.bramhaag.owouploader.adapter.viewholder.item.ViewHolderItem;

/**
 * {@link RecyclerView.ViewHolder} for {@link ViewHolderItem}s.
 *
 * @param <T> The concrete type of the item
 */
public abstract class HistoryViewHolder<T extends ViewHolderItem> extends RecyclerView.ViewHolder {

    public HistoryViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void initializeView(@NonNull T item);
}
