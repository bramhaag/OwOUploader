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

package me.bramhaag.owouploader.api.interceptor;

import android.os.SystemClock;
import androidx.annotation.NonNull;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * {@link Interceptor} that adds the Authorization and User-Agent headers.
 */
public class AuthenticationInterceptor implements Interceptor {

    private final String userAgent;
    private final String key;

    public AuthenticationInterceptor(@NonNull String userAgent, @NonNull String key) {
        this.userAgent = userAgent;
        this.key = key;
    }

    @Override
    @NonNull
    public Response intercept(Chain chain) throws IOException {
        var request = chain.request();

        System.out.println("REQ INTERCEPT: " + request.url().url().toString());

        SystemClock.sleep(1000);

        return chain.proceed(request.newBuilder()
                .header("User-Agent", userAgent)
                .header("Authorization", key)
                .build());
    }
}
