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

import android.annotation.SuppressLint;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import me.bramhaag.owouploader.R;
import me.bramhaag.owouploader.adapter.viewholder.item.ViewHolderItem;

public class ObjectViewHolder<T extends ViewHolderItem> extends ParentViewHolder<T> {

    public ObjectViewHolder(BaseViewHolder<T> innerViewHolder) {
        super(innerViewHolder);
    }

    @Override
    @SuppressLint("RestrictedApi")
    public void initializeView(@NonNull T item) {
        super.initializeView(item);

        ((TextView) itemView.findViewById(R.id.upload_item_title)).setCompoundDrawables(null, null, null, null);

        var copyButton = itemView.findViewById(R.id.upload_item_copy);
        var menu = new PopupMenu(getContext(), copyButton);
        menu.getMenuInflater().inflate(R.menu.history_menu, menu.getMenu());

        MenuPopupHelper menuHelper = new MenuPopupHelper(getContext(), (MenuBuilder) menu.getMenu(), copyButton);
        menuHelper.setForceShowIcon(true);

        copyButton.setOnClickListener(v -> menuHelper.show());
    }
}
