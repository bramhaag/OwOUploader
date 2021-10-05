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

package me.bramhaag.owouploader.module;

import android.content.Context;
import androidx.room.Room;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import javax.inject.Singleton;
import me.bramhaag.owouploader.db.HistoryDatabase;

/**
 * Module that provides {@link HistoryDatabase}.
 */
@Module
@InstallIn(SingletonComponent.class)
public class DatabaseModule {

    /**
     * Provider for {@link HistoryDatabase}. The instance will only be created once.
     *
     * @param context the context
     * @return a {@link HistoryDatabase} instance
     */
    @Singleton
    @Provides
    public static HistoryDatabase provideHistoryDatabase(@ApplicationContext Context context) {
        return Room.databaseBuilder(context, HistoryDatabase.class, HistoryDatabase.NAME)
                .allowMainThreadQueries() //TODO disable
                .build();
    }
}
