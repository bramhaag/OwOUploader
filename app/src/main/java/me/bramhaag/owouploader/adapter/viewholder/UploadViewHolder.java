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

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import com.bumptech.glide.Glide;
import java.net.URI;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import me.bramhaag.owouploader.R;
import me.bramhaag.owouploader.db.entity.UploadItem;

/**
 * {@link HistoryViewHolder} for {@link UploadItem}.
 */
public class UploadViewHolder extends HistoryViewHolder<UploadItem> {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter
            .ofPattern("dd-MM-yyyy HH:mm")
            .withZone(ZoneId.systemDefault());

    private final TextView title;
    private final TextView description;
    private final ImageView image;
    private URI url;

    /**
     * Instantiate a new ViewHolder.
     *
     * @param view the view
     */
    public UploadViewHolder(View view) {
        super(view);
        title = view.findViewById(R.id.upload_item_title);
        description = view.findViewById(R.id.upload_item_description);
        image = view.findViewById(R.id.upload_item_image);
    }

    @Override
    public void initializeView(@NonNull UploadItem item) {
        setTitle(item.name);
        setDescription(item.url.getHost(), item.createdAt);
        setUrl(item.url);
        loadImage(item.url);

        itemView.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url.toString()));
            itemView.getContext().startActivity(browserIntent);
        });

        var clipboard = (ClipboardManager) itemView.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        var clip = ClipData.newPlainText(item.url.toString(), item.url.toString());

        itemView.findViewById(R.id.upload_item_copy).setOnClickListener(v -> {
            clipboard.setPrimaryClip(clip);
            Toast.makeText(v.getContext(), "URL copied to clipboard", Toast.LENGTH_LONG).show();
        });
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public void setDescription(String host, Instant date) {
        this.description.setText(String.format("%s - %s", host, DATE_FORMAT.format(date)));
    }

    /**
     * Load an image from a url.
     *
     * @param url the url
     */
    public void loadImage(URI url) {
        // FIXME
        var placeholder = new CircularProgressDrawable(itemView.getContext());
        placeholder.setCenterRadius(32);
        placeholder.setStrokeWidth(8);
        placeholder.setColorSchemeColors(itemView.getContext().getResources().getColor(R.color.teal_200));
        placeholder.start();

        Glide.with(itemView)
                .load(Uri.parse(url.toString()))
                .placeholder(placeholder)
                .error(R.drawable.outline_photo_24)
                .centerCrop()
                .into(image);
    }

    public void setUrl(URI url) {
        this.url = url;
    }

    public URI getUrl() {
        return url;
    }
}
