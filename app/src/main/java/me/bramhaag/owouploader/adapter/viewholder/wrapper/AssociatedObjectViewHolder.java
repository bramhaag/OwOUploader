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

package me.bramhaag.owouploader.adapter.viewholder.wrapper;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import me.bramhaag.owouploader.R;
import me.bramhaag.owouploader.adapter.HistoryAdapter;
import me.bramhaag.owouploader.adapter.viewholder.BaseViewHolder;
import me.bramhaag.owouploader.api.OwOAPI;
import me.bramhaag.owouploader.api.callback.ResultCallback;
import me.bramhaag.owouploader.api.model.ObjectModel;
import me.bramhaag.owouploader.db.entity.HistoryItem;
import me.bramhaag.owouploader.util.ActionUtil;
import me.bramhaag.owouploader.util.MenuItemClickListenerWrapper;

/**
 * Wrapper ViewHolder for associated history.
 *
 * @param <T> history item type
 */
public class AssociatedObjectViewHolder<T extends HistoryItem> extends ParentViewHolder<T> {

    private final OwOAPI api;
    private final HistoryAdapter adapter;

    public AssociatedObjectViewHolder(BaseViewHolder<T> innerViewHolder, OwOAPI api, HistoryAdapter adapter) {
        super(innerViewHolder);

        this.api = api;
        this.adapter = adapter;
    }

    @Override
    @SuppressLint("RestrictedApi")
    public void initializeView(@NonNull T item) {
        super.initializeView(item);

        var menuButton = itemView.findViewById(R.id.upload_item_menu);
        var menu = new PopupMenu(getContext(), menuButton);
        menu.getMenuInflater().inflate(R.menu.history_menu, menu.getMenu());

        MenuPopupHelper menuHelper = new MenuPopupHelper(getContext(), (MenuBuilder) menu.getMenu(), menuButton);
        menuHelper.setForceShowIcon(true);

        menu.setOnMenuItemClickListener(new MenuItemClickListenerWrapper()
                .on(R.id.action_item_copy, () -> {
                    ActionUtil.copy(getContext(), item.url());
                    Toast.makeText(getContext(), getContext().getString(R.string.toast_copied), Toast.LENGTH_LONG)
                            .show();
                })
                .on(R.id.action_item_share, () -> ActionUtil.share(getContext(), item.url()))
                .on(R.id.action_item_delete, () -> {
                    var callback = new ResultCallback<ObjectModel>() {
                        @Override
                        public void onStart() {
                            // Do nothing
                        }

                        @Override
                        public void onError(@NonNull Throwable throwable) {
                            Toast.makeText(getContext(), "Unable to remove item: " + throwable.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onComplete(@NonNull ObjectModel result) {
                            adapter.removeItem(getItem());
                            Toast.makeText(getContext(), getContext().getString(R.string.toast_removed),
                                    Toast.LENGTH_SHORT).show();
                        }
                    };

                    new Builder(getContext())
                            .setTitle(R.string.dialog_delete_associated_title)
                            .setMessage(R.string.dialog_delete_associated_description)
                            .setPositiveButton(android.R.string.ok, (dialog, which) ->
                                    api.removeObject(item.key(), callback))
                            .setNegativeButton(android.R.string.cancel, ((dialog, which) -> {
                                // Do nothing
                            }))
                            .show();
                }));

        menuButton.setOnClickListener(v -> menuHelper.show());
    }
}
