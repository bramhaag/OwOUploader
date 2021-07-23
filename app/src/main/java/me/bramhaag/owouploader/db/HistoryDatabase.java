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

package me.bramhaag.owouploader.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import me.bramhaag.owouploader.db.converters.InstantConverter;
import me.bramhaag.owouploader.db.converters.URIConverter;
import me.bramhaag.owouploader.db.dao.ShortenItemDao;
import me.bramhaag.owouploader.db.dao.UploadItemDao;
import me.bramhaag.owouploader.db.entity.ShortenItem;
import me.bramhaag.owouploader.db.entity.UploadItem;


/**
 * Database for {@link ShortenItem}s and {@link UploadItem}s.
 */
@Database(entities = {ShortenItem.class, UploadItem.class}, version = 1)
@TypeConverters({InstantConverter.class, URIConverter.class})
public abstract class HistoryDatabase extends RoomDatabase {

    public static final String NAME = "history";

    public abstract ShortenItemDao shortenItemDao();

    public abstract UploadItemDao uploadItemDao();
}
