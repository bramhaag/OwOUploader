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

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import com.bumptech.glide.Glide;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import me.bramhaag.owouploader.R;
import me.bramhaag.owouploader.components.UploadHistoryItem;

public class UploadViewHolder extends HistoryViewHolder<UploadHistoryItem> {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.US);

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
        view.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url.toString()));
            view.getContext().startActivity(browserIntent);
        });
    }

    @Override
    void initializeView() {
        setTitle(getItem().getName());
        setDescription(getItem().getUrl().getHost(), getItem().getDate());
        setUrl(getItem().getUrl());
        loadImage(getItem().getUrl());
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public void setDescription(String host, Date date) {
        this.description.setText(String.format("%s - %s", host, DATE_FORMAT.format(date)));
    }

    public void loadImage(URI url) {
        var placeholder = new CircularProgressDrawable(itemView.getContext());
        placeholder.setCenterRadius(32);
        placeholder.setStrokeWidth(8);
        placeholder.setColorSchemeColors(itemView.getContext().getResources().getColor(R.color.teal_200));
        placeholder.start();

        Glide.with(itemView)
                .load(Uri.parse(url.toString()))
                .placeholder(placeholder)
                .into(image);
    }

    public void setUrl(URI url) {
        this.url = url;
    }

    public URI getUrl() {
        return url;
    }
}
