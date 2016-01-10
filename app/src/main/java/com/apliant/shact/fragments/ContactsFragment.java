package com.apliant.shact.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.apliant.shact.R;
import com.apliant.shact.models.Network;
import com.apliant.shact.models.Profile;
import com.apliant.shact.views.adapters.NetworksAdapter;
import com.cocosw.bottomsheet.BottomSheet;
import com.yalantis.flipviewpager.utils.FlipSettings;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rafa93br on 07/01/2016.
 */
public class ContactsFragment extends Fragment {
    private static final String TAG = "TAG";
    NetworksAdapter mAdapter;
    ListView socialProfiles;
    List<Network> dummy = new ArrayList<>();

    /**
     * Create a new instance of DetailsFragment, initialized to
     * show the text at 'index'.
     */
    public static ContactsFragment newInstance(int index) {
        ContactsFragment f = new ContactsFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);

        return f;
    }

    public int getShownIndex() {
        return getArguments().getInt("index", 0);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_networks, container, false);



        return v;
    }


}