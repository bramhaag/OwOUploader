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
 * Entity for shortening history.
 */
@Entity
public class ShortenItem implements ViewHolderItem {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public URI originalUrl;

    public URI resultUrl;

    public Instant createdAt;

    /**
     * Create a new ShortenItem.
     *
     * @param originalUrl the original url
     * @param resultUrl   the resulting url
     * @return a new ShortenItem
     */
    public static ShortenItem create(URI originalUrl, URI resultUrl) {
        var item = new ShortenItem();
        item.originalUrl = originalUrl;
        item.resultUrl = resultUrl;
        item.createdAt = Instant.now();

        return item;
    }
}
