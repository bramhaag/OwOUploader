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

package me.bramhaag.owouploader.service;

import android.app.Service;
import android.content.Intent;
import android.hardware.display.VirtualDisplay;
import android.hardware.display.VirtualDisplay.Callback;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OutputFormat;
import android.media.MediaRecorder.VideoEncoder;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Binder;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.Surface;
import android.view.View;
import androidx.annotation.NonNull;
import com.getbase.floatingactionbutton.FloatingActionButton;
import java.io.File;
import java.io.IOException;
import java.util.Objects;
import me.bramhaag.owouploader.file.FileContentProvider;
import me.bramhaag.owouploader.upload.UploadHandler;
import me.bramhaag.owouploader.util.NotificationsUtil;

/**
 * Background service for screen recording.
 */
public class ScreenCaptureService extends Service {

    private static final String TAG = "ScreenCaptureService";

    private final IBinder binder = new ScreenRecordBinder();

    private MediaProjection mediaProjection;
    private VirtualDisplay virtualDisplay;
    private MediaRecorder mediaRecorder;
    private File outputFile;

    private UploadHandler uploadHandler;

    private FloatingActionButton startButton;
    private FloatingActionButton endButton;

    @NonNull
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    /**
     * Start screen capture.
     *
     * @param resultCode result code for requesting permission
     * @param data       the data
     */
    public void start(int resultCode, Intent data) {
        var notification = NotificationsUtil.getNotification(this);
        startForeground(Objects.requireNonNull(notification.first), notification.second);

        var mediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data);

        if (mediaProjection == null) {
            throw new RuntimeException("Cannot instantiate MediaProjection");
        }

        var metrics = getResources().getDisplayMetrics();

        String fileName = String.format("record_%s.mp4", System.currentTimeMillis());
        outputFile = new File(getApplicationContext().getCacheDir(), fileName);

        mediaRecorder = createMediaRecorder(metrics, outputFile);
        virtualDisplay = createVirtualDisplay(mediaProjection, metrics, mediaRecorder.getSurface(),
                () -> setButtons(false));

        setButtons(true);

        mediaRecorder.start();
    }

    /**
     * Stop screen capture.
     */
    public void stop() {
        mediaRecorder.stop();
        mediaRecorder.reset();
        mediaRecorder.release();
        virtualDisplay.release();
        mediaProjection.stop();

        // We don't really care about if the file is deleted successfully or not.
        //noinspection ResultOfMethodCallIgnored
        uploadHandler.upload(new FileContentProvider(outputFile), () -> outputFile.delete());

        stopForeground(true);
    }

    private static MediaRecorder createMediaRecorder(DisplayMetrics metrics, File outputPath) {
        var mediaRecorder = new MediaRecorder();
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);

        mediaRecorder.setOutputFormat(OutputFormat.MPEG_4);
        mediaRecorder.setOutputFile(outputPath.getPath());

        mediaRecorder.setVideoSize(metrics.widthPixels, metrics.heightPixels);
        mediaRecorder.setVideoEncoder(VideoEncoder.H264);
        mediaRecorder.setVideoEncodingBitRate(512 * 1000);
        mediaRecorder.setVideoFrameRate(30);

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            // Rethrow because we can't recover from this
            throw new RuntimeException(e);
        }

        return mediaRecorder;
    }

    private static VirtualDisplay createVirtualDisplay(MediaProjection mediaProjection, DisplayMetrics metrics,
            Surface surface, Runnable onStop) {
        var callback = new Callback() {
            @Override
            public void onStopped() {
                onStop.run();
            }
        };

        return mediaProjection.createVirtualDisplay(TAG, metrics.widthPixels, metrics.heightPixels, metrics.densityDpi,
                0, surface, callback, null);
    }

    public void setUploadHandler(UploadHandler uploadHandler) {
        this.uploadHandler = uploadHandler;
    }

    public void setStartButton(FloatingActionButton startButton) {
        this.startButton = startButton;
    }

    public void setEndButton(FloatingActionButton endButton) {
        this.endButton = endButton;
    }

    private void setButtons(boolean started) {
        if (started) {
            startButton.setVisibility(View.GONE);
            endButton.setVisibility(View.VISIBLE);
        } else {
            startButton.setVisibility(View.VISIBLE);
            endButton.setVisibility(View.GONE);
        }
    }

    /**
     * {@link Binder} for {@link ScreenCaptureService} to set several instance variables.
     */
    public class ScreenRecordBinder extends Binder {

        public ScreenCaptureService getService() {
            return ScreenCaptureService.this;
        }
    }
}
