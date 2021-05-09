///*
// * OwO Uploader
// * Copyright (C) 2021
// *
// * This program is free software: you can redistribute it and/or modify
// * it under the terms of the GNU Affero General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// *
// * This program is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU Affero General Public License for more details.
// *
// * You should have received a copy of the GNU Affero General Public License
// * along with this program.  If not, see <http://www.gnu.org/licenses/>.
// */
//
//package me.bramhaag.owouploader.api;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.lenient;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.mockStatic;
//import static org.mockito.Mockito.spy;
//import static org.mockito.Mockito.when;
//
//import android.webkit.MimeTypeMap;
//import java.io.File;
//import me.bramhaag.owouploader.api.callback.ProgressResultCallback;
//import okhttp3.MediaType;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//@ExtendWith(MockitoExtension.class)
//class ProgressRequestBodyTest {
//
//    @Mock
//    ProgressResultCallback<Void> progressResult;
//
//    @Mock
//    MimeTypeMap mimeTypeMap;
//
//    @BeforeEach
//    public void setUp() {
//        lenient().when(mimeTypeMap.getMimeTypeFromExtension("")).thenReturn(null);
//        lenient().when(mimeTypeMap.getMimeTypeFromExtension("jpg")).thenReturn("image/jpeg");
//        lenient().when(mimeTypeMap.getMimeTypeFromExtension("mp4")).thenReturn("video/mp4");
//        lenient().when(mimeTypeMap.getMimeTypeFromExtension("txt")).thenReturn("text/plain");
//        lenient().when(mimeTypeMap.getMimeTypeFromExtension("fake-ext")).thenReturn(null);
//    }
//
//
//    @Test
//    void contentType_default() {
//        try (var mocked = mockStatic(MimeTypeMap.class)) {
//            mocked.when(() -> MimeTypeMap.getFileExtensionFromUrl(anyString())).thenReturn("");
//            var file = new File("file");
//            var prb = new ProgressRequestBody(file, progressResult);
//            assertEquals(MediaType.get("application/octet-stream"), prb.contentType());
//        }
//    }
//
//    @Test
//    void contentType_unknown() {
//        try (var mocked = mockStatic(MimeTypeMap.class)) {
//            mocked.when(() -> MimeTypeMap.getFileExtensionFromUrl(anyString())).thenReturn("fake-ext");
//            mocked.when(MimeTypeMap::getSingleton).thenReturn(mimeTypeMap);
//
//            var file = new File("file.fake-ext");
//            var prb = new ProgressRequestBody(file, progressResult);
//            assertEquals(MediaType.get("application/octet-stream"), prb.contentType());
//        }
//    }
//
//    @Test
//    void contentType_automatic() {
//        try (var mocked = mockStatic(MimeTypeMap.class)) {
//            mocked.when(MimeTypeMap::getSingleton).thenReturn(mimeTypeMap);
//            mocked.when(() -> MimeTypeMap.getFileExtensionFromUrl("file.txt")).thenReturn("txt");
//            mocked.when(() -> MimeTypeMap.getFileExtensionFromUrl("file.jpg")).thenReturn("jpg");
//            mocked.when(() -> MimeTypeMap.getFileExtensionFromUrl("file.mp4")).thenReturn("mp4");
//
//            var file = new File("file.txt");
//            var prb = new ProgressRequestBody(file, progressResult);
//            assertEquals(MediaType.get("text/plain"), prb.contentType());
//
//            file = new File("file.jpg");
//            prb = new ProgressRequestBody(file, progressResult);
//            assertEquals(MediaType.get("image/jpeg"), prb.contentType());
//
//            file = new File("file.mp4");
//            prb = new ProgressRequestBody(file, progressResult);
//            assertEquals(MediaType.get("video/mp4"), prb.contentType());
//        }
//    }
//
//    @Test
//    void contentType_manual() {
//        var file = new File("file");
//        var prb = new ProgressRequestBody(file, "text/plain", progressResult);
//        assertEquals(MediaType.get("text/plain"), prb.contentType());
//    }
//
//    @Test
//    void contentLength() {
//        long size = 10000;
//
//        var file = spy(new File("file.txt"));
//        when(file.length()).thenReturn(size);
//
//        var prb = new ProgressRequestBody(file, "text/plain", progressResult);
//
//        assertEquals(size, prb.contentLength());
//    }
//}