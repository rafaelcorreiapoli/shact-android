package com.apliant.shact.views.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.apliant.shact.R;
import com.apliant.shact.models.Network;
import com.yalantis.flipviewpager.adapter.BaseFlipAdapter;
import com.yalantis.flipviewpager.utils.FlipSettings;

import java.util.List;

/**
 * Created by rafa93br on 08/01/2016.
 */
public class NetworksAdapter extends BaseFlipAdapter<Network> {
    private Context context;
    private static final Integer PAGE_COUNT = 3;
    public NetworksAdapter(Context context, List<Network> items, FlipSettings settings) {
        super(context, items, settings);
        this.context = context;
    }

    private void paintImageView(Network network, ImageView imageView) {
        int iconColor;
        int backgroundColor;

        if (network.getProfile() != null) {
            iconColor = Color.WHITE;
            backgroundColor = network.getColor();
        } else {
            iconColor = Color.WHITE;
            backgroundColor = ContextCompat.getColor(context, R.color.primaryGray);
        }
        imageView.setBackgroundColor(backgroundColor);

        imageView.setImageDrawable(network.getDrawable(context, iconColor));
    }

    @Override
    public View getPage(int position, View convertView, ViewGroup parent, Network network1, Network network2) {
        final SocialProfileHolder holder;

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        if (convertView == null) {
            holder = new SocialProfileHolder();
            convertView = layoutInflater.inflate(R.layout.network_item, parent, false);
            holder.leftAvatar = (ImageView) convertView.findViewById(R.id.first);
            holder.rightAvatar = (ImageView) convertView.findViewById(R.id.second);
            holder.infoPage = layoutInflater.inflate(R.layout.network_item_detail, parent, false);
            holder.name = (TextView) holder.infoPage.findViewById(R.id.name);
            holder.profile = (TextView) holder.infoPage.findViewById(R.id.profile);
            convertView.setTag(holder);
        } else {
            holder = (SocialProfileHolder) convertView.getTag();
        }


        switch (position) {
            case 1:
                paintImageView(network1, holder.leftAvatar);
                if (network2 != null) {
                    paintImageView(network2, holder.rightAvatar);
                }
                break;
            default:
                fillHolder(holder, position == 0 ? network1 : network2);
                holder.infoPage.setTag(holder);
                return holder.infoPage;
        }
        return convertView;
    }

    @Override
    public int getPagesCount() {
        return PAGE_COUNT;
    }

    private void fillHolder(SocialProfileHolder holder, Network network) {
        if (network == null)
            return;
        holder.infoPage.setBackgroundColor(network.getColor());
        holder.name.setText(network.getName());
        if (network.getProfile() != null) {
            // Tenho Profile
            holder.profile.setText(network.getProfile().getName());
        } else {
            holder.profile.setText(context.getResources().getString(R.string.not_registered_profile));
        }
    }

    class SocialProfileHolder {
        ImageView leftAvatar;
        ImageView rightAvatar;
        View infoPage;
        TextView name;
        TextView profile;
    }
}