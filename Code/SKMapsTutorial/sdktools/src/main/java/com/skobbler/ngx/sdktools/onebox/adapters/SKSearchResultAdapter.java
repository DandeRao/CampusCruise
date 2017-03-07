package com.skobbler.ngx.sdktools.onebox.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.skobbler.ngx.R;
import com.skobbler.ngx.SKCategories;
import com.skobbler.ngx.SKCoordinate;
import com.skobbler.ngx.SKMaps;
import com.skobbler.ngx.sdktools.onebox.SKOneBoxSearchResult;
import com.skobbler.ngx.sdktools.onebox.fragments.OneBoxManager;
import com.skobbler.ngx.sdktools.onebox.listeners.OnSeeMoreResultsListener;
import com.skobbler.ngx.sdktools.onebox.utils.SKToolsUtils;
import com.skobbler.ngx.search.SKSearchResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Adapter for search results.
 */
public class SKSearchResultAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int FOOTER_VIEW = 1;
    List<SKOneBoxSearchResult> skSearchResults;
    public static boolean resultsListExpanded = false;
    private HashMap<SKCategories.SKPOIMainCategory, Integer> imageResources;
    public static OnSeeMoreResultsListener onSeeMoreResultsListener;


    public SKSearchResultAdapter(Context context) {
        imageResources = new HashMap<>();
        imageResources.put(SKCategories.SKPOIMainCategory.SKPOI_MAIN_CATEGORY_FOOD, R.drawable.onebox_cat_food_list_icon);
        imageResources.put(SKCategories.SKPOIMainCategory.SKPOI_MAIN_CATEGORY_HEALTH, R.drawable.onebox_cat_health_list_icon);
        imageResources.put(SKCategories.SKPOIMainCategory.SKPOI_MAIN_CATEGORY_LEISURE, R.drawable.onebox_cat_leisure_list_icon);
        imageResources.put(SKCategories.SKPOIMainCategory.SKPOI_MAIN_CATEGORY_NIGHTLIFE, R.drawable.onebox_cat_nightlife_list_icon);
        imageResources.put(SKCategories.SKPOIMainCategory.SKPOI_MAIN_CATEGORY_PUBLIC, R.drawable.onebox_cat_public_list_icon);
        imageResources.put(SKCategories.SKPOIMainCategory.SKPOI_MAIN_CATEGORY_SERVICES, R.drawable.onebox_cat_services_list_icon);
        imageResources.put(SKCategories.SKPOIMainCategory.SKPOI_MAIN_CATEGORY_SHOPPING, R.drawable.onebox_cat_shopping_list_icon);
        imageResources.put(SKCategories.SKPOIMainCategory.SKPOI_MAIN_CATEGORY_ACCOMODATION, R.drawable.onebox_cat_sleeping_list_icon);
        imageResources.put(SKCategories.SKPOIMainCategory.SKPOI_MAIN_CATEGORY_TRANSPORT, R.drawable.onebox_cat_transport_list_icon);

    }

    public void setOnSeeMoreResultsListener(OnSeeMoreResultsListener listener) {
        onSeeMoreResultsListener = listener;
    }


    public void setData(List<SKOneBoxSearchResult> results) {
        this.skSearchResults = new ArrayList<>();
        for (SKOneBoxSearchResult res : results) {
            if (res.isVisible()) {
                this.skSearchResults.add(res);
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v;
        if (viewType == FOOTER_VIEW) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.onebox_see_more_results, parent, false);
            ResultsFooterViewHolder vh = new ResultsFooterViewHolder(v);
            return vh;
        }

        v = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.onebox_two_line_item, parent, false);
        return new ResultsHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (holder instanceof ResultsHolder) {
            ResultsHolder resultsHolder = (ResultsHolder) holder;
            final SKOneBoxSearchResult resultObject = skSearchResults.get(position);
            final SKSearchResult result = resultObject.getSearchResult();
            if (!result.getName().isEmpty()) {
                resultsHolder.textItem.setText(result.getName());
                if (result.getMainCategory() != null) {
                    resultsHolder.imageItem.setImageResource(imageResources.get(result.getMainCategory()));
                } else {
                    resultsHolder.imageItem.setImageResource(R.drawable.onebox_osm_list_icon);
                }
                double distance = calculateDistance(result);
                if (distance > 0) {
                    resultsHolder.distanceItem.setText(SKToolsUtils.convertAndformatDistance(distance, SKMaps.SKDistanceUnitType.DISTANCE_UNIT_KILOMETER_METERS));
                }
                StringBuilder resultSubtitle = new StringBuilder();
                if (result.getAddress().getZipCode() != null) {
                    resultSubtitle.append(result.getAddress().getZipCode() + ", ");
                }
                if (result.getAddress().getStreet() != null) {
                    resultSubtitle.append(result.getAddress().getStreet() + ", ");
                }
                if (result.getAddress().getCity() != null) {
                    resultSubtitle.append(result.getAddress().getCity() + ", ");
                }
                if (result.getAddress().getCountry() != null) {
                    resultSubtitle.append(result.getAddress().getCountry());
                } else if (result.getAddress().getCountryCode() != null) {
                    resultSubtitle.append(result.getAddress().getCountryCode());
                }

                resultsHolder.subtitleItem.setText(resultSubtitle);
            }
        } else if (holder instanceof ResultsFooterViewHolder) {
            SKSearchResultAdapter.ResultsFooterViewHolder vh = (SKSearchResultAdapter.ResultsFooterViewHolder) holder;
            if (resultsListExpanded) {
                vh.textView.setVisibility(View.GONE);
            } else {
                vh.textView.setVisibility(View.VISIBLE);
            }
        }
    }

    public void updateList(List<SKOneBoxSearchResult> results) {
        skSearchResults.clear();
        for (SKOneBoxSearchResult res : results) {
            if (res.isVisible()) {
                this.skSearchResults.add(res);
            }
        }
        resultsListExpanded = true;
        notifyDataSetChanged();
    }

    private double calculateDistance(SKSearchResult result) {
        double distance = -1;
        double[] coordinate = OneBoxManager.getCurrentPosition();
        if (coordinate != null && result.getLocation() != null) {

            distance = SKToolsUtils.distanceBetween(new SKCoordinate(coordinate[0], coordinate[1]), result.getLocation());
        }
        return distance;
    }

    public void sort(final int sortType) {
        switch (sortType) {
            case OneBoxManager.SORT_NAME:
                Collections.sort(skSearchResults, new Comparator<SKOneBoxSearchResult>() {
                    @Override
                    public int compare(SKOneBoxSearchResult first, SKOneBoxSearchResult second) {
                        return first.getSearchResult().getName().compareTo(second.getSearchResult().getName());
                    }
                });
                break;
            case OneBoxManager.SORT_DISTANCE:
                Collections.sort(skSearchResults, new Comparator<SKOneBoxSearchResult>() {
                    @Override
                    public int compare(SKOneBoxSearchResult first, SKOneBoxSearchResult second) {
                        double firstDistance = calculateDistance(first.getSearchResult());
                        double secondDistance = calculateDistance(second.getSearchResult());

                        return Double.compare(firstDistance, secondDistance);
                    }
                });
                break;
            case OneBoxManager.SORT_RANK:
                Collections.sort(skSearchResults, new Comparator<SKOneBoxSearchResult>() {
                    @Override
                    public int compare(SKOneBoxSearchResult first, SKOneBoxSearchResult second) {
                        return first.getRankIndex() - second.getRankIndex();
                    }
                });
                break;
        }

        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (skSearchResults == null) {
            return 0;
        }

        if (skSearchResults.size() == 0) {
            return 1;
        }

        // Add extra view to show the footer view
        return skSearchResults.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == skSearchResults.size()) {
            //  add footer.
            return FOOTER_VIEW;
        }
        return super.getItemViewType(position);
    }

    public static class ResultsHolder extends RecyclerView.ViewHolder {
        View container;

        ImageView imageItem;

        TextView textItem;

        TextView subtitleItem;

        TextView distanceItem;

        public ResultsHolder(View v) {
            super(v);
            container = v;
            textItem = (TextView) v.findViewById(R.id.list_item_text);
            imageItem = (ImageView) v.findViewById(R.id.list_item_image);
            subtitleItem = (TextView) v.findViewById(R.id.list_item_subtitle);
            distanceItem = (TextView) v.findViewById(R.id.list_item_distance);
        }

    }

    public class ResultsFooterViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ResultsFooterViewHolder(final View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.see_more_results);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onSeeMoreResultsListener != null) {
                        onSeeMoreResultsListener.onSeeMoreResultsClick(view);
                    }
                }
            });

        }
    }
}
