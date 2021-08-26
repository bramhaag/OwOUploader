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

import androidx.annotation.NonNull;
import me.bramhaag.owouploader.adapter.viewholder.item.ViewHolderItem;

public abstract class ParentViewHolder<T extends ViewHolderItem> extends BaseViewHolder<T> {

    private final BaseViewHolder<T> childViewHolder;

    public BaseViewHolder<T> getChildViewHolder() {
        return childViewHolder;
    }

    public ParentViewHolder(BaseViewHolder<T> innerViewHolder) {
        super(innerViewHolder.itemView);
        this.childViewHolder = innerViewHolder;
    }

    @Override
    public void initializeView(@NonNull T item) {
        this.childViewHolder.initializeView(item);
    }
}
