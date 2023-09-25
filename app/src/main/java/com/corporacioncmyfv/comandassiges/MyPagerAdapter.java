package com.corporacioncmyfv.comandassiges;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MyPagerAdapter extends FragmentStateAdapter {

    public MyPagerAdapter(@NonNull FragmentActivity fragmentActivity) {

        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Log.e("FRAGMENT","ENTRO AL FRAGMENT Y");
        // Devuelve el fragmento correspondiente a la posición de la pestaña.
        // Puedes personalizar esto para tu caso específico.
        return MyFragment.newInstance(position);
    }

    @Override
    public int getItemCount() {
        Log.e("FRAGMENT","ENTRO AL FRAGMENT E");
        // Devuelve el número total de pestañas.
        return 3; // Por ejemplo, 3 pestañas en este caso.
    }
}
