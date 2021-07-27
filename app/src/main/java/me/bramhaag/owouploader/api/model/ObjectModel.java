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

package me.bramhaag.owouploader.api.model;

import androidx.annotation.NonNull;
import com.google.gson.annotations.SerializedName;
import java.util.Date;

/**
 * Stores result of a call to the {@code objects} endpoint.
 */
public class ObjectModel {

    private String bucket;
    private String key;
    private String dir;
    private ObjectType type;
    private String destUrl;
    private String contentType;
    private long contentLength;
    private Date createdAt;
    private Date deletedAt;
    private String deleteReason;
    private String md5Hash;
    private String sha256Hash;
    private boolean associatedWithCurrentUser;

    public String getBucket() {
        return bucket;
    }

    public String getKey() {
        return key;
    }

    public String getDir() {
        return dir;
    }

    public ObjectType getType() {
        return type;
    }

    public String getDestUrl() {
        return destUrl;
    }

    public String getContentType() {
        return contentType;
    }

    public long getContentLength() {
        return contentLength;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public String getDeleteReason() {
        return deleteReason;
    }

    public String getMd5Hash() {
        return md5Hash;
    }

    public String getSha256Hash() {
        return sha256Hash;
    }

    public boolean isAssociatedWithCurrentUser() {
        return associatedWithCurrentUser;
    }

    @Override
    @NonNull
    public String toString() {
        return "ObjectModel{"
                + "bucket='" + bucket + '\''
                + ", key='" + key + '\''
                + ", dir='" + dir + '\''
                + ", type=" + type
                + ", destUrl='" + destUrl + '\''
                + ", contentType='" + contentType + '\''
                + ", contentLength=" + contentLength
                + ", createdAt=" + createdAt
                + ", deletedAt=" + deletedAt
                + ", deleteReason='" + deleteReason + '\''
                + ", md5Hash='" + md5Hash + '\''
                + ", sha256Hash='" + sha256Hash + '\''
                + ", associatedWithCurrentUser=" + associatedWithCurrentUser
                + '}';
    }

    /**
     * Enum that represents object types.
     */
    public enum ObjectType {
        @SerializedName("0")
        FILE,
        @SerializedName("1")
        REDIRECT,
        @SerializedName("2")
        TOMBSTONE
    }
}
