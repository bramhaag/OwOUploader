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
import androidx.recyclerview.widget.RecyclerView;
import java.net.URI;
import java.util.Arrays;
import java.util.Date;
import me.bramhaag.owouploader.R;
import me.bramhaag.owouploader.adapter.UploadHistoryAdapter;
import me.bramhaag.owouploader.components.UploadHistoryItem;

/**
 * UploadHistoryFragment.
 */
public class UploadHistoryFragment extends Fragment {

    public UploadHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_upload_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView recyclerView = view.findViewById(R.id.upload_recycler_view);

        var uploadHistoryAdapter = new UploadHistoryAdapter(Arrays.asList(
                new UploadHistoryItem(
                        "File1.jpg",
                        URI.create("https://totally-not.a-sketchy.site/4f23PwD.jpg"),
                        new Date()
                ),
                new UploadHistoryItem(
                        "File2.docx",
                        URI.create("https://awau.moe/4f23PwD.jpg"),
                        new Date()
                ),
                new UploadHistoryItem(
                        "File_3_example.png",
                        URI.create("https://owo.whats-th.is/4f23PwD.jpg"),
                        new Date()
                ),
                new UploadHistoryItem(
                        "ballooncat.jpg",
                        URI.create("https://owo.whats-th.is/4f23PwD.jpg"),
                        new Date()
                )
        ), getContext());

        recyclerView.setAdapter(uploadHistoryAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

    }
}