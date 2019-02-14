package com.example.android.azs;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class GeoFragment extends Fragment {


    public GeoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_geo, container, false);
        final ImageView googleBottom = rootView.findViewById(R.id.google_image);
        final ImageView yandexBottom = rootView.findViewById(R.id.yandex_image);

        googleBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                        Uri intentUri = Uri.parse("google.navigation:q="+ MainActivity.LAT + "," + MainActivity.LOG);
        Intent intent = new Intent(Intent.ACTION_VIEW, intentUri);
        startActivity(intent);
            }
        });


        yandexBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ////обращаемся к яндексу через интент
        Intent intent = new Intent("ru.yandex.yandexnavi.action.BUILD_ROUTE_ON_MAP");
        intent.setPackage("ru.yandex.yandexnavi");

// Проверяем, установлен ли Яндекс.Навигатор
        if (MainActivity.infos == null || MainActivity.infos.size() == 0) {
            Toast toast = Toast.makeText(getActivity(),
                    "Яндекс навигатор не установлен", Toast.LENGTH_SHORT);
            toast.show();
        } else {

            intent.putExtra("lat_to", MainActivity.LAT);
            intent.putExtra("lon_to", MainActivity.LOG);
            startActivity(intent);
        }

            }
        });
        return rootView;
    }
    public static GeoFragment newInstance (){
        return new GeoFragment();
    }
}
