package com.endimuceku.groupup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class GroupFragment extends Fragment {

    private Activity activity;

    private ImageView accountSettingsIcon;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        activity = getActivity();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_group, container, false);

        accountSettingsIcon = (ImageView) view.findViewById(R.id.accountSettingsIcon);
        accountSettingsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startViewAccountActivityIntent = new Intent(activity, ViewAccountActivity.class);
                startActivity(startViewAccountActivityIntent);
            }
        });

        return view;
    }
}