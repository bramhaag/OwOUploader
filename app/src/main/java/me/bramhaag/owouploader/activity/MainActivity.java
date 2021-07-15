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

package me.bramhaag.owouploader.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.Intent;
import android.graphics.Rect;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Pair;
import android.os.IBinder;
import android.view.Menu;
import android.view.MotionEvent;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.google.android.material.tabs.TabLayoutMediator;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.List;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import me.bramhaag.owouploader.R;
import me.bramhaag.owouploader.api.OwOAPI;
import me.bramhaag.owouploader.databinding.ActivityMainBinding;
import me.bramhaag.owouploader.fragment.ShortenDialogFragment;
import me.bramhaag.owouploader.fragment.ShortenHistoryFragment;
import me.bramhaag.owouploader.fragment.UploadHistoryFragment;
import me.bramhaag.owouploader.result.UploadResultCallback;
import me.bramhaag.owouploader.service.ScreenCaptureService;
import me.bramhaag.owouploader.service.ScreenCaptureService.ScreenRecordBinder;
import me.bramhaag.owouploader.upload.UploadHandler;
import me.bramhaag.owouploader.util.CryptographyHelper;

/**
 * Main activity.
 */
public class MainActivity extends AppCompatActivity {

    public ActivityMainBinding binding;

    private UploadHandler uploadHandler;
    private ShortenDialogFragment shortenDialog;

    private ScreenCaptureService screenCaptureService;

    // This should probably be moved in the future lol
    @Nullable
    private String apiKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        var extras = getIntent().getExtras();
        var token = extras.getString("TOKEN");
        var api = new OwOAPI(token);

        var tabLayoutPageAdapter = new TabLayoutPageAdapter(this);
        binding.viewPager.setAdapter(tabLayoutPageAdapter);
        binding.tabLayout.setupWithViewPager(binding.viewPager);

        var encryptedApiKey = PreferenceManager.getDefaultSharedPreferences(this)
                .getString("owo_api_key", null);
        // Probably move it up before the content is rendered (?)
        if (encryptedApiKey != null) {
            try {
                this.apiKey = CryptographyHelper.getInstance().decrypt(encryptedApiKey);
            } catch (InvalidKeyException | InvalidAlgorithmParameterException | BadPaddingException | IllegalBlockSizeException e) {
                e.printStackTrace();
            }
            return;
        }

        startActivity(new Intent(this, LoginActivity.class));

        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, position) ->
                        tab.setText(tabLayoutPageAdapter.getTitle(position))).attach();

        uploadHandler = new UploadHandler(api, this, binding.tabLayout.getTabAt(0));

        var documentActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(), new UploadResultCallback(this, uploadHandler));

        binding.actionUpload.setOnClickListener(view -> {
            documentActivityLauncher.launch(new String[]{"*/*"});
            binding.fabMenu.collapse();
        });

        shortenDialog = new ShortenDialogFragment(api, binding.tabLayout.getTabAt(1));
        binding.actionShorten.setOnClickListener(view -> {
            shortenDialog.show(getSupportFragmentManager(), "shorten_dialog");
            binding.fabMenu.collapse();
        });

        var mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        var screenRecordLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> screenCaptureService.start(result.getResultCode(), result.getData()));

        binding.actionScreenRecord.setOnClickListener(view -> {
            var intent = mediaProjectionManager.createScreenCaptureIntent();
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            screenRecordLauncher.launch(intent);
        });

        binding.actionEndScreenRecord.setOnClickListener(view -> {
            screenCaptureService.stop();
            binding.fabMenu.collapse();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(this, ScreenCaptureService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();

        unbindService(connection);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        var fabMenu = binding.fabMenu;

        if (event.getAction() != MotionEvent.ACTION_DOWN || !fabMenu.isExpanded()) {
            return super.dispatchTouchEvent(event);
        }

        var outRect = new Rect();
        fabMenu.getGlobalVisibleRect(outRect);

        if (outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
            return super.dispatchTouchEvent(event);
        }

        fabMenu.collapse();
        return true;
    }

    public UploadHandler getUploadHandler() {
        return uploadHandler;
    }

    public ShortenDialogFragment getShortenDialog() {
        return shortenDialog;
    }

    private final ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            var binder = (ScreenRecordBinder) service;
            screenCaptureService = binder.getService();
            screenCaptureService.setUploadHandler(uploadHandler);
            screenCaptureService.setStartButton(binding.actionScreenRecord);
            screenCaptureService.setEndButton(binding.actionEndScreenRecord);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            screenCaptureService = null;
        }
    };

    private static class TabLayoutPageAdapter extends FragmentStateAdapter {

        private final String[] titles;

        public TabLayoutPageAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
            titles = new String[]{"upload", "shorten"};
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new UploadHistoryFragment();
                case 1:
                    return new ShortenHistoryFragment();
                default:
                    throw new IllegalArgumentException();
            }
        }

        @Override
        public int getItemCount() {
            return 2;
        }

        public String getTitle(int position) {
            return titles[position];
        }
    }
}