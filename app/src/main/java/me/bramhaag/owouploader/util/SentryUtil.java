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

package me.bramhaag.owouploader.util;

import android.content.Context;
import io.sentry.android.core.SentryAndroid;
import me.bramhaag.owouploader.BuildConfig;

/**
 * Utility class for Sentry.
 */
public class SentryUtil {

    public static final String SENTRY_ENABLED_KEY = "sentry_enabled";

    private SentryUtil() {
        throw new AssertionError("Cannot instantiate utility class");
    }

    public static void enableSentry(Context context, boolean enabled) {
        SentryAndroid.init(context, options -> options.setDsn(enabled ? BuildConfig.SENTRY_DSN : ""));
    }
}
