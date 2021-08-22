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

package me.bramhaag.owouploader.db.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import java.net.URI;
import java.time.Instant;
import me.bramhaag.owouploader.adapter.viewholder.item.ViewHolderItem;

/**
 * Entity for upload history.
 */
@Entity
public class UploadItem implements ViewHolderItem, HistoryItem {

    @PrimaryKey
    @NonNull
    public String key;

    public String name;

    public URI url;

    public Instant createdAt;

    @Ignore
    public UploadItem(@NonNull String key, String name, URI url) {
        this(key, name, url, Instant.now());
    }

    public UploadItem(@NonNull String key, String name, URI url, Instant createdAt) {
        this.key = key;
        this.name = name;
        this.url = url;
        this.createdAt = createdAt;
    }

    @Override
    public String key() {
        return key;
    }

    @Override
    public Instant createdAt() {
        return createdAt;
    }
}
