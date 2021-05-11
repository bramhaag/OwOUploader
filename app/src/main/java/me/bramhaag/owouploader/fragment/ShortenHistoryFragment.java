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

package me.bramhaag.owouploader.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import java.net.URI;
import java.util.Arrays;
import java.util.Date;
import me.bramhaag.owouploader.adapter.HistoryAdapter;
import me.bramhaag.owouploader.components.ShortenHistoryItem;
import me.bramhaag.owouploader.databinding.FragmentHistoryBinding;

/**
 * ShortenHistoryFragment.
 */
public class ShortenHistoryFragment extends Fragment {

    private FragmentHistoryBinding binding;

    public ShortenHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(inflater);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        var shortenHistoryAdapter = new HistoryAdapter(Arrays.asList(
                new ShortenHistoryItem(
                        URI.create("https://www.youtube.com/watch?v=gOK12Ombicg"),
                        URI.create("https://youtu.be/gOK12Ombicg"),
                        new Date()),
                new ShortenHistoryItem(
                        URI.create("https://www.youtube.com/watch?v=eNxqPNxqocM"),
                        URI.create("https://youtu.be/eNxqPNxqocM"),
                        new Date())
        ));

        binding.recyclerView.setAdapter(shortenHistoryAdapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }
}