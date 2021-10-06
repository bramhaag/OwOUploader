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

import androidx.annotation.NonNull;
import io.sentry.Sentry;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * {@link Interceptor} that retries requests if they fail due to rate limit errors.
 */
public class RateLimitInterceptor implements Interceptor {

    private static final int HTTP_TOO_MANY_REQUESTS = 429;

    private static final String HEADER_RATE_LIMIT_EXPIRY = "X-Ratelimiter-Expiry";

    @Override
    @NonNull
    public Response intercept(Chain chain) throws IOException {
        var request = chain.request();
        var response = chain.proceed(request);

        if (response.code() != HTTP_TOO_MANY_REQUESTS) {
            return response;
        }

        String expiryHeaderValue = response.header(HEADER_RATE_LIMIT_EXPIRY);
        if (expiryHeaderValue == null) {
            return response;
        }

        long expiration = Long.parseLong(expiryHeaderValue) * 1000;
        long currentTime = System.currentTimeMillis();
        long wait = expiration - currentTime;

        if (wait > 0) {
            try {
                Thread.sleep(wait);
            } catch (InterruptedException e) {
                Sentry.captureException(e);
                return response;
            }
        }

        response.close();

        return chain.proceed(request);
    }
}
