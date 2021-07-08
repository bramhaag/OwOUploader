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

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.net.URI;
import java.time.Instant;
import me.bramhaag.owouploader.adapter.viewholder.item.ViewHolderItem;

/**
 * Entity for upload history.
 */
@Entity
public class UploadItem implements ViewHolderItem {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;

    public URI url;

    public Instant createdAt;

    /**
     * Create a new UploadItem.
     *
     * @param name the name
     * @param url  the url
     * @return a new UploadItem
     */
    public static UploadItem create(String name, URI url) {
        var item = new UploadItem();
        item.name = name;
        item.url = url;
        item.createdAt = Instant.now();

        return item;
    }
}
