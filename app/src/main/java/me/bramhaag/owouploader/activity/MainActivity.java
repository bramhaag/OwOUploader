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

import android.graphics.Rect;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.LinearLayout;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import java.util.Arrays;
import java.util.List;
import me.bramhaag.owouploader.R;
import me.bramhaag.owouploader.api.OwOAPI;
import me.bramhaag.owouploader.databinding.ActivityMainBinding;
import me.bramhaag.owouploader.fragment.ShortenDialogFragment;
import me.bramhaag.owouploader.fragment.ShortenHistoryFragment;
import me.bramhaag.owouploader.fragment.UploadHistoryFragment;
import me.bramhaag.owouploader.result.UploadResultCallback;

/**
 * Main activity.
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private UploadResultCallback uploadCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        var extras = getIntent().getExtras();
        var token = extras.getString("TOKEN");
        var api = new OwOAPI(token);

        uploadCallback = new UploadResultCallback(api, getApplicationContext());
        var documentActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                uploadCallback
        );

        var tabLayoutPageAdapter = new TabLayoutPageAdapter(this.getSupportFragmentManager());
        binding.viewPager.setAdapter(tabLayoutPageAdapter);
        binding.tabLayout.setupWithViewPager(binding.viewPager);

        binding.actionUpload.setOnClickListener(view -> {
            documentActivityLauncher.launch(new String[]{"*/*"});
            binding.fabMenu.collapse();
        });

        binding.actionShorten.setOnClickListener(view -> {
            new ShortenDialogFragment(api, getApplicationContext(),
                    (ShortenHistoryFragment) tabLayoutPageAdapter.tabs.get(1).second).show(getSupportFragmentManager(), "tag");
            binding.fabMenu.collapse();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
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

    public UploadResultCallback getUploadCallback() {
        return uploadCallback;
    }

    private static class TabLayoutPageAdapter extends FragmentPagerAdapter {

        private final List<Pair<String, Fragment>> tabs;

        public TabLayoutPageAdapter(FragmentManager fm) {
            super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

            tabs = Arrays.asList(new Pair<>("upload", new UploadHistoryFragment()),
                    new Pair<>("shorten", new ShortenHistoryFragment()));
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return tabs.get(position).first;
        }

        @Override
        @NonNull
        public Fragment getItem(int position) {
            return tabs.get(position).second;
        }

        @Override
        public int getCount() {
            return tabs.size();
        }

        public List<Pair<String, Fragment>> getTabs() {
            return tabs;
        }
    }
}