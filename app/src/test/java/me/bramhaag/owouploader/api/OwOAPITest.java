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
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyDouble;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.ArgumentMatchers.notNull;
//import static org.mockito.ArgumentMatchers.startsWith;
//import static org.mockito.Mockito.atLeastOnce;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.mockStatic;
//import static org.mockito.Mockito.timeout;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import android.webkit.MimeTypeMap;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.util.concurrent.TimeUnit;
//import me.bramhaag.owouploader.api.callback.ProgressResultCallback;
//import me.bramhaag.owouploader.api.callback.ResultCallback;
//import me.bramhaag.owouploader.api.model.UploadModel;
//import me.bramhaag.owouploader.api.model.UserModel;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//@ExtendWith(MockitoExtension.class)
//class OwOAPITest {
//    private static final int TIMEOUT = 60 * 1000;
//
//    static OwOAPI api;
//
//    @Mock
//    ResultCallback<String> shortenCallback;
//
//    @Mock
//    ProgressResultCallback<UploadModel> uploadCallback;
//
//    @Mock
//    ProgressResultCallback<UserModel> userCallback;
//
//    @BeforeAll
//    public static void setUp() {
//        var token = System.getenv("OWO_TEST_TOKEN");
//        if (token == null) {
//            throw new RuntimeException("Please set env var OWO_TEST_TOKEN");
//        }
//
//        api = new OwOAPI(token);
//    }
//
//    @Test
//    void uploadFile() {
//        var file = new File("file.txt");
//
//        try (var mocked = mockStatic(MimeTypeMap.class)) {
//            var mimeTypeMap = mock(MimeTypeMap.class);
//            when(mimeTypeMap.getMimeTypeFromExtension("txt")).thenReturn("text/plain");
//
//            mocked.when(() -> MimeTypeMap.getFileExtensionFromUrl(anyString())).thenReturn("txt");
//            mocked.when(MimeTypeMap::getSingleton).thenReturn(mimeTypeMap);
//
//            file.createNewFile();
//            try (var os = new FileOutputStream(file)) {
//                os.write("Hello, World!".getBytes());
//            }
//
//            api.uploadFile(file, uploadCallback, false);
//            verify(uploadCallback, timeout(5000)).onComplete(notNull());
//            verify(uploadCallback, atLeastOnce()).onProgress(anyDouble());
//            verify(uploadCallback, times(0)).onError(any());
//        } catch (IOException e) {
//            if (file.exists()) {
//                file.delete();
//            }
//        }
//    }
//
//    @Test
//    void shortenUrl() {
//        api.shortenUrl("https://google.com/", "https://example.com/", shortenCallback, false);
//        verify(shortenCallback, timeout(TIMEOUT)).onComplete(startsWith("https://"));
//        verify(shortenCallback, times(0)).onError(any());
//    }
//
//    @Test
//    void shortenUrl_result() {
//        api.shortenUrl("https://google.com/", "https://example.com/", shortenCallback, false);
//        verify(shortenCallback, timeout(TIMEOUT)).onComplete(startsWith("https://example.com/"));
//        verify(shortenCallback, times(0)).onError(any());
//    }
//
//    @Test
//    void getUser() {
//        api.getUser(userCallback);
//        verify(userCallback, timeout(TIMEOUT)).onComplete(notNull());
//        verify(userCallback, times(0)).onError(any());
//    }
//}