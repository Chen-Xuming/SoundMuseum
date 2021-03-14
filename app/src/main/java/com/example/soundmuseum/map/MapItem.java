package com.example.soundmuseum.map;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapsdkexample.util.clusterutil.clustering.ClusterItem;
import com.example.soundmuseum.R;

public class MapItem implements ClusterItem {
    LatLng mPosition;
    public MapItem(LatLng position) {
        mPosition = position;
    }
    @Override
    public LatLng getPosition() {
        return mPosition;
    }
    @Override
    public BitmapDescriptor getBitmapDescriptor() {
        return BitmapDescriptorFactory
                .fromResource(R.drawable.baidumap_marker);
    }
}
