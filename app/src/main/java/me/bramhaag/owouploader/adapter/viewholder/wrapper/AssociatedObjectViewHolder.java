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
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
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

        var copyButton = itemView.findViewById(R.id.upload_item_copy);
        var menu = new PopupMenu(getContext(), copyButton);
        menu.getMenuInflater().inflate(R.menu.associated_history_menu, menu.getMenu());

        MenuPopupHelper menuHelper = new MenuPopupHelper(getContext(), (MenuBuilder) menu.getMenu(), copyButton);
        menuHelper.setForceShowIcon(true);

        menu.setOnMenuItemClickListener(i -> {
            int itemId = i.getItemId();
            if (itemId == R.id.action_item_copy) {
                var clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                var clip = ClipData.newPlainText(item.url(), item.url());
                clipboard.setPrimaryClip(clip);

                Toast.makeText(getContext(), "URL copied to clipboard", Toast.LENGTH_LONG).show();

                return true;
            } else if (itemId == R.id.action_item_share) {
                var sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, item.url());
                sendIntent.setType("text/plain");

                var shareIntent = Intent.createChooser(sendIntent, null);
                getContext().startActivity(shareIntent);

                return true;
            } else if (itemId == R.id.action_item_delete) {
                api.removeObject(item.key(), new ResultCallback<>() {
                    @Override
                    public void onStart() {
                        // Ignore
                    }

                    @Override
                    public void onError(@NonNull Throwable throwable) {
                        Toast.makeText(getContext(), "Unable to remove item: " + throwable.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onComplete(@NonNull ObjectModel result) {
                        adapter.removeItem(getItem());
                        Toast.makeText(getContext(), "Item removed", Toast.LENGTH_SHORT).show();
                    }
                });
                return true;
            }

            //TODO sentry log here
            return false;
        });

        copyButton.setOnClickListener(v -> menuHelper.show());
    }
}
