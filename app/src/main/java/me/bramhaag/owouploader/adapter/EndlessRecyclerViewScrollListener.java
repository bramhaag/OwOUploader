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

package me.bramhaag.owouploader.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

// Adapted from https://gist.github.com/nesquena/d09dc68ff07e845cc622
public abstract class EndlessRecyclerViewScrollListener extends RecyclerView.OnScrollListener {

    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private static final int VISIBLE_THRESHOLD = 5;

    // The current offset index of data you have loaded
    private int currentPage = 0;

    // True if we are still waiting for the last set of data to load.
    private boolean loading = false;

    private final LinearLayoutManager layoutManager;

    public EndlessRecyclerViewScrollListener(LinearLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    public int getLastVisibleItem(int[] lastVisibleItemPositions) {
        int maxSize = 0;
        for (int i = 0; i < lastVisibleItemPositions.length; i++) {
            if (i == 0 || lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i];
            }
        }
        return maxSize;
    }

    // This happens many times a second during a scroll, so be wary of the code you place here.
    // We are given a few useful parameters to help us work out if we need to load some more data,
    // but first we check if we are waiting for the previous load to finish.
    @Override
    public void onScrolled(@NonNull RecyclerView view, int dx, int dy) {
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
        int totalItemCount = layoutManager.getItemCount();

//        // If the total item count is zero and the previous isn't, assume the
//        // list is invalidated and should be reset back to initial state
//        if (totalItemCount < previousTotalItemCount) {
//            this.currentPage = this.startingPageIndex;
//            this.previousTotalItemCount = totalItemCount;
//            if (totalItemCount == 0) {
//                this.loading = true;
//            }
//        }
//        // If it’s still loading, we check to see if the dataset count has
//        // changed, if so we conclude it has finished loading and update the current page
//        // number and total item count.
//        if (loading && (totalItemCount > previousTotalItemCount)) {
//            loading = false;
//            previousTotalItemCount = totalItemCount;
//        }

        // If it isn’t currently loading, we check to see if we have breached
        // the visibleThreshold and need to reload more data.
        // If we do need to reload some more data, we execute onLoadMore to fetch the data.
        // threshold should reflect how many total columns there are too

        if (!loading && (lastVisibleItemPosition + VISIBLE_THRESHOLD) > totalItemCount) {
            onLoadMore(this, currentPage, view);
            loading = true;
        }
    }

    // Call this method whenever performing new searches
    public void resetState() {
        this.currentPage = 0;
        this.loading = false;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }

    public void incrementCurrentPage(int currentPage) {
        this.currentPage += currentPage;
    }

    // Defines the process for actually loading more data based on page
    public abstract void onLoadMore(EndlessRecyclerViewScrollListener listener, int offset, RecyclerView view);

}
