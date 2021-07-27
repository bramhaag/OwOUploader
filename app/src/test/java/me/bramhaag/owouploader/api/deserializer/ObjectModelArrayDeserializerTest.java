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

package me.bramhaag.owouploader.api.deserializer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.time.Instant;
import java.util.Date;
import me.bramhaag.owouploader.api.model.ObjectModel;
import me.bramhaag.owouploader.api.model.ObjectModel.ObjectType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ObjectModelArrayDeserializerTest {

    private Gson gson;

    @BeforeEach
    void setUp() {
        gson = new GsonBuilder()
                .registerTypeAdapter(ObjectModel[].class, new ObjectModelArrayDeserializer())
                .registerTypeAdapter(ObjectModel.class, new ObjectModelDeserializer())
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
    }

    @Test
    void deserialize_valid() {
        //language=JSON
        var input = "{\n"
                + "  \"success\": true,\n"
                + "  \"data\": [\n"
                + "    {\n"
                + "      \"bucket\": \"public\",\n"
                + "      \"key\": \"/dWKk75z.jpg\",\n"
                + "      \"dir\": \"/\",\n"
                + "      \"type\": 0,\n"
                + "      \"dest_url\": null,\n"
                + "      \"content_type\": \"image/jpeg\",\n"
                + "      \"content_length\": 468048,\n"
                + "      \"created_at\": \"2021-05-06T02:20:42.488708Z\",\n"
                + "      \"deleted_at\": null,\n"
                + "      \"delete_reason\": null,\n"
                + "      \"md5_hash\": \"9f76df5b93647c39fe5ec62dde46ed6b\",\n"
                + "      \"sha256_hash\": \"223e6108c10f1d78abd19ed298da7d2cbce60713a80ca09876af5873ebb5a432\",\n"
                + "      \"associated_with_current_user\": true\n"
                + "    },\n"
                + "    {\n"
                + "      \"bucket\": \"public\",\n"
                + "      \"key\": \"/dWKk75z.jpg\",\n"
                + "      \"dir\": \"/\",\n"
                + "      \"type\": 0,\n"
                + "      \"dest_url\": null,\n"
                + "      \"content_type\": \"image/jpeg\",\n"
                + "      \"content_length\": 468048,\n"
                + "      \"created_at\": \"2021-05-06T02:20:42.488708Z\",\n"
                + "      \"deleted_at\": null,\n"
                + "      \"delete_reason\": null,\n"
                + "      \"md5_hash\": \"9f76df5b93647c39fe5ec62dde46ed6b\",\n"
                + "      \"sha256_hash\": \"223e6108c10f1d78abd19ed298da7d2cbce60713a80ca09876af5873ebb5a432\",\n"
                + "      \"associated_with_current_user\": true\n"
                + "    }\n"
                + "  ]\n"
                + "}";

        var result = gson.fromJson(input, ObjectModel[].class);
        assertEquals(2, result.length);
        for (var r : result) {
            assertEquals("public", r.getBucket());
            assertEquals("/dWKk75z.jpg", r.getKey());
            assertEquals("/", r.getDir());
            assertEquals(ObjectType.FILE, r.getType());
            assertNull(r.getDestUrl());
            assertEquals("image/jpeg", r.getContentType());
            assertEquals(468048, r.getContentLength());
            assertEquals(Date.from(Instant.parse("2021-05-06T02:20:42.488708Z")), r.getCreatedAt());
            assertNull(r.getDeletedAt());
            assertNull(r.getDeleteReason());
            assertEquals("9f76df5b93647c39fe5ec62dde46ed6b", r.getMd5Hash());
            assertEquals("223e6108c10f1d78abd19ed298da7d2cbce60713a80ca09876af5873ebb5a432", r.getSha256Hash());
            assertTrue(r.isAssociatedWithCurrentUser());
        }
    }

    @Test
    void deserialize_invalid() {
        //language=JSON
        var input = "{\n"
                + "  \"success\": false\n"
                + "}";

        var result = gson.fromJson(input, ObjectModel[].class);
        assertNull(result);
    }
}