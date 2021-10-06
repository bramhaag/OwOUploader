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

package me.bramhaag.owouploader.adapter.viewholder.history;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import java.net.URI;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import me.bramhaag.owouploader.R;
import me.bramhaag.owouploader.adapter.viewholder.BaseViewHolder;
import me.bramhaag.owouploader.db.entity.ShortenItem;

/**
 * ViewHolder for shorten objects.
 */
public class ShortenObjectViewHolder extends BaseViewHolder<ShortenItem> {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter
            .ofPattern("dd-MM-yyyy HH:mm")
            .withZone(ZoneId.systemDefault());

    private final TextView title;
    private final TextView description;
    private final ImageView image;

    /**
     * Instantiate a new ViewHolder.
     *
     * @param view the view
     */
    public ShortenObjectViewHolder(View view) {
        super(view);
        title = view.findViewById(R.id.upload_item_title);
        description = view.findViewById(R.id.upload_item_description);
        image = view.findViewById(R.id.upload_item_image);
    }

    @Override
    public void initializeView(@NonNull ShortenItem item) {
        super.initializeView(item);

        this.image.setVisibility(View.GONE);

        setTitle(item.originalUrl.toString());
        setDescription(item.resultUrl, item.createdAt);

        itemView.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.resultUrl.toString()));
            getContext().startActivity(browserIntent);
        });
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public void setDescription(URI url, Instant date) {
        this.description.setText(String.format("%s - %s", url.toString(), DATE_FORMAT.format(date)));
    }
}
