package com.corporacioncmyfv.comandassiges;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MyFragment extends Fragment {
    private static final String ARG_POSITION = "position";
    private int position;

    public static MyFragment newInstance(int position) {
        Log.e("FRAGMENT","ENTRO AL FRAGMENT W");
        MyFragment fragment = new MyFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(ARG_POSITION);
            Log.e("FRAGMENT","ENTRO AL FRAGMENT A");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Infla el diseño del fragmento según la posición de la pestaña.
        Log.e("FRAGMENT","ENTRO AL FRAGMENT X");
        View rootView;
        switch (position) {
            case 0:
                rootView = inflater.inflate(R.layout.fragment_tab1, container, false);
                Log.e("FRAGMENT","ENTRO AL FRAGMENT 0");
                break;
            case 1:
                rootView = inflater.inflate(R.layout.fragment_tab2, container, false);
                Log.e("FRAGMENT","ENTRO AL FRAGMENT 1");
                break;
            case 2:
                rootView = inflater.inflate(R.layout.fragment_tab3, container, false);
                Log.e("FRAGMENT","ENTRO AL FRAGMENT 2");
                break;
            default:
                rootView = inflater.inflate(R.layout.fragment_default, container, false);
                Log.e("FRAGMENT","ENTRO AL FRAGMENT 3");
                break;
        }
        return rootView;
    }
}