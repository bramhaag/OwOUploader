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
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import me.bramhaag.owouploader.R;
import me.bramhaag.owouploader.components.ShortenHistoryItem;


/**
 * {@link RecyclerView.Adapter} for shorten history.
 */
public class ShortenHistoryAdapter extends RecyclerView.Adapter<ShortenHistoryAdapter.ViewHolder> {

    private final List<ShortenHistoryItem> items;

    public ShortenHistoryAdapter(List<ShortenHistoryItem> items) {
        this.items = items;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.shorten_history_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        var item = items.get(position);

        viewHolder.setTitle(item.getOriginalUrl().toString());
        viewHolder.setDescription(item.getShortenedUrl(), item.getDate());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * {@link RecyclerView.ViewHolder} for shorten history.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {

        private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.US);

        private final TextView title;
        private final TextView description;

        /**
         * Instantiate a new ViewHolder.
         *
         * @param view the view
         */
        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.shorten_item_title);
            description = view.findViewById(R.id.shorten_item_description);
        }

        public void setTitle(String title) {
            this.title.setText(title);
        }

        public void setDescription(URI url, Date date) {
            this.description.setText(String.format("%s - %s", url.toString(), DATE_FORMAT.format(date)));
        }
    }
}
