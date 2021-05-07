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

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import me.bramhaag.owouploader.R;
import me.bramhaag.owouploader.components.UploadHistoryItem;

public class UploadHistoryAdapter extends RecyclerView.Adapter<UploadHistoryAdapter.ViewHolder> {

    private final List<UploadHistoryItem> files;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm");

        private final TextView title;
        private final TextView description;
        private final ImageView image;
        private URI url;

        public ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.upload_item_title);
            description = view.findViewById(R.id.upload_item_description);
            image = view.findViewById(R.id.upload_item_image);
        }

        public void setTitle(String title) {
            this.title.setText(title);
        }

        public void setDescription(String host, Date date) {
            this.description.setText(String.format("%s - %s", host, DATE_FORMAT.format(date)));
        }

        public void setImage(Drawable drawable) {
            this.image.setImageDrawable(drawable);
        }

        public void setUrl(URI url) {
            this.url = url;
        }

        public URI getUrl() {
            return url;
        }
    }

    private final Context context;

    public UploadHistoryAdapter(List<UploadHistoryItem> files, Context context) {
        this.files = files;
        this.context = context;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.upload_history_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        var file = files.get(position);

        viewHolder.setTitle(file.getName());
        viewHolder.setDescription(file.getUrl().getHost(), file.getDate());
        viewHolder.setUrl(file.getUrl());
        viewHolder.setImage(ContextCompat.getDrawable(context, R.drawable.ic_launcher_background));
    }

    @Override
    public int getItemCount() {
        return files.size();
    }
}
