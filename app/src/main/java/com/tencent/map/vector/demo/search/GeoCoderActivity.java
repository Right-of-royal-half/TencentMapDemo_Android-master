package com.tencent.map.vector.demo.search;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.map.vector.demo.R;
import com.tencent.map.vector.demo.basic.SupportMapFragmentActivity;
import com.tencent.lbssearch.TencentSearch;
import com.tencent.lbssearch.httpresponse.BaseObject;
import com.tencent.lbssearch.httpresponse.HttpResponseListener;
import com.tencent.lbssearch.httpresponse.Poi;
import com.tencent.lbssearch.object.param.Address2GeoParam;
import com.tencent.lbssearch.object.param.Geo2AddressParam;
import com.tencent.lbssearch.object.result.Address2GeoResultObject;
import com.tencent.lbssearch.object.result.Geo2AddressResultObject;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory;
import com.tencent.tencentmap.mapsdk.maps.model.CameraPosition;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;
import com.tencent.tencentmap.mapsdk.maps.model.MarkerOptions;

public class GeoCoderActivity extends SupportMapFragmentActivity {

    private EditText etGeocoder;
    private Button btnGeocoder;
    private EditText etRegeocoder;
    private Button btnRegeocoder;
    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }


    private void initView(){
        etGeocoder = (EditText) findViewById(R.id.et_geocoder);
        btnGeocoder = (Button) findViewById(R.id.btn_geocoder);
        etRegeocoder = (EditText) findViewById(R.id.et_regeocoder);
        btnRegeocoder = (Button) findViewById(R.id.btn_regeocoder);
        etGeocoder.setVisibility(View.VISIBLE);
        btnGeocoder.setVisibility(View.VISIBLE);
        etRegeocoder.setVisibility(View.VISIBLE);
        btnRegeocoder.setVisibility(View.VISIBLE);
        tvResult = (TextView) findViewById(R.id.tv_result);
        tvResult.setVisibility(View.VISIBLE);
        btnGeocoder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                geocoder();
            }
        });

        btnRegeocoder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reGeocoder();
            }
        });
    }
    /**
     *????????????
     */
    protected void geocoder() {
        TencentSearch tencentSearch = new TencentSearch(this);
        String address = etGeocoder.getText().toString();
        Address2GeoParam address2GeoParam =
                new Address2GeoParam(address).region("??????");
        tencentSearch.address2geo(address2GeoParam, new HttpResponseListener<BaseObject>() {

            @Override
            public void onSuccess(int arg0, BaseObject arg1) {
                // TODO Auto-generated method stub
                if (arg1 == null) {
                    return;
                }
                Address2GeoResultObject obj = (Address2GeoResultObject)arg1;
                StringBuilder sb = new StringBuilder();
                sb.append("????????????");
                if (obj.result.latLng != null) {
                    sb.append("\n?????????" + obj.result.latLng.toString());
                } else {
                    sb.append("\n?????????");
                }
                printResult(sb.toString());
                tencentMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(obj.result.latLng,15f, 0, 0)));
                tencentMap.addMarker(new MarkerOptions()
                        .position(obj.result.latLng));
            }

            @Override
            public void onFailure(int arg0, String arg1, Throwable arg2) {
                // TODO Auto-generated method stub
                printResult(arg1);
            }
        });
    }

    /**
     * ???????????????
     */
    protected void reGeocoder() {
        String str = etRegeocoder.getText().toString().trim();
        LatLng latLng = str2Coordinate(this, str);
        if (latLng == null) {
            return;
        }
        TencentSearch tencentSearch = new TencentSearch(this);
        //?????????????????????????????????????????????????????????coord_type()??????????????????
        //????????????????????????poi?????????????????????????????????????????????????????????????????????poi?????????
        Geo2AddressParam geo2AddressParam = new Geo2AddressParam(latLng).getPoi(true)
                .setPoiOptions(new Geo2AddressParam.PoiOptions()
                        .setRadius(1000).setCategorys("??????")
                        .setPolicy(Geo2AddressParam.PoiOptions.POLICY_O2O));
        tencentSearch.geo2address(geo2AddressParam, new HttpResponseListener<BaseObject>() {

            @Override
            public void onSuccess(int arg0, BaseObject arg1) {
                // TODO Auto-generated method stub
                if (arg1 == null) {
                    return;
                }
                Geo2AddressResultObject obj = (Geo2AddressResultObject)arg1;
                StringBuilder sb = new StringBuilder();
                sb.append("???????????????");
                sb.append("\n?????????" + obj.result.address);
                sb.append("\npois:");
                for (Poi poi : obj.result.pois) {
                    sb.append("\n\t" + poi.title);
                    tencentMap.addMarker(new MarkerOptions()
                            .position(poi.latLng)  //???????????????
                            .title(poi.title)     //?????????InfoWindow?????????
                            .snippet(poi.address) //?????????InfoWindow?????????
                    );
                }
                //printResult(sb.toString());
            }

            @Override
            public void onFailure(int arg0, String arg1, Throwable arg2) {
                // TODO Auto-generated method stub
                printResult(arg1);
            }
        });
    }


    /**
     * ????????????????????????
     * @param context
     * @param str
     * @return
     */
    public static LatLng str2Coordinate(Context context, String str) {
        if (!str.contains(",")) {
            Toast.makeText(context, "????????????\",\"??????", Toast.LENGTH_SHORT).show();
            return null;
        }
        String[] strs = str.split(",");
        double lat = 0;
        double lng = 0;
        try {
            lat = Double.parseDouble(strs[0]);
            lng = Double.parseDouble(strs[1]);
        } catch (NumberFormatException e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            return null;
        }
        return new LatLng(lat, lng);
    }
    protected void printResult(final String result) {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                tvResult.setText(result);
            }
        });
    }
}
