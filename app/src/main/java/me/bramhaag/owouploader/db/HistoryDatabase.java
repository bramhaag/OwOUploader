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
import me.bramhaag.owouploader.db.dao.ShortenItemDao;
import me.bramhaag.owouploader.db.dao.UploadItemDao;
import me.bramhaag.owouploader.db.entity.ShortenItem;
import me.bramhaag.owouploader.db.entity.UploadItem;


@Database(entities = {ShortenItem.class, UploadItem.class}, version = 1)
public abstract class HistoryDatabase extends RoomDatabase {
    public abstract ShortenItemDao shortenItemDao();

    public abstract UploadItemDao uploadItemDao();
}
