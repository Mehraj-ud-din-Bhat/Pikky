package com.xscoder.pikky.Home.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xscoder.pikky.R;

import androidx.fragment.app.Fragment;


public class Fallowing extends Fragment {
    public Fallowing() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_fallowing, container, false);
    }
}
