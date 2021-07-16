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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import dagger.hilt.android.AndroidEntryPoint;
import javax.inject.Inject;
import me.bramhaag.owouploader.activity.MainActivity;
import me.bramhaag.owouploader.adapter.HistoryAdapter;
import me.bramhaag.owouploader.databinding.FragmentHistoryBinding;
import me.bramhaag.owouploader.db.HistoryDatabase;

/**
 * UploadHistoryFragment.
 */
@AndroidEntryPoint
public class UploadHistoryFragment extends Fragment {

    public FragmentHistoryBinding binding;

    @Inject
    HistoryDatabase database;

    public UploadHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        binding = FragmentHistoryBinding.inflate(inflater);

        var uploadHistoryAdapter = new HistoryAdapter(database.uploadItemDao().getAll());

        var layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        binding.recyclerView.setAdapter(uploadHistoryAdapter);
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setItemAnimator(null);

        ((MainActivity) requireActivity()).getUploadHandler().setRecyclerView(binding.recyclerView);

        return binding.getRoot();
    }
}