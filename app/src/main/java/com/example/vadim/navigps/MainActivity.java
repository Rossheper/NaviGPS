package com.example.vadim.navigps;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/*public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}*/
public class MainActivity extends AppCompatActivity {
    /** Called when the activity is first created. */

    private int dev = 1;
    GeoTransforms geo;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        geo = new GeoTransforms();
        LayoutInflater inflater = LayoutInflater.from(this);
        List<View> pages = new ArrayList<View>();

        View page = inflater.inflate(R.layout.activity_main, null);
        pages.add(page);
        pages.add(inflater.inflate(R.layout.converter_coords, null));
        pages.add(inflater.inflate(R.layout.fragment, null));
        pages.add(inflater.inflate(R.layout.edits_window, null));

        PageFragment pagerAdapter = new PageFragment(pages);
        ViewPager viewPager = new ViewPager(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(0);

        setContentView(viewPager);

    }
    public void onMyButtonClick(View view)
    {
        // выводим сообщение
        final EditText gradText = (EditText) findViewById(R.id.gradEditText);
        final EditText minText = (EditText) findViewById(R.id.minEditText);
        final EditText secText = (EditText) findViewById(R.id.secEditText);
        String str = gradText.getText().toString() + " " + minText.getText().toString() + " " + secText.getText().toString();
        ((EditText) findViewById(R.id.resultEditText)).setText(String.valueOf(LLA_To_Degree(str)));
    }
    public void SaveEditsClick(View view){
        try {
            if (((RadioButton) findViewById(R.id.radio_red)).isChecked()) {
                geo.dx = Double.parseDouble(((EditText) findViewById(R.id.dx_text)).getText().toString());
                geo.dy = Double.parseDouble(((EditText) findViewById(R.id.dy_text)).getText().toString());
                geo.dz = Double.parseDouble(((EditText) findViewById(R.id.dz_text)).getText().toString());

                geo.wx = Double.parseDouble(((EditText) findViewById(R.id.wx_text)).getText().toString());
                geo.wy = Double.parseDouble(((EditText) findViewById(R.id.wy_text)).getText().toString());
                geo.wz = Double.parseDouble(((EditText) findViewById(R.id.wz_text)).getText().toString());

                geo.ms = Double.parseDouble(((EditText) findViewById(R.id.scale_text)).getText().toString()) * (1e-6);

                geo.dxdydzSk42._mass[0][0] = geo.dx;
                geo.dxdydzSk42._mass[1][0] = geo.dy;
                geo.dxdydzSk42._mass[2][0] = geo.dz;

                geo.dxdydzPz._mass[0][0] = 0;//-1.1;
                geo.dxdydzPz._mass[1][0] = 0;//-0.3;
                geo.dxdydzPz._mass[2][0] = 0;//-0.9;

                geo.toPZ90._mass[0][1] = -geo.wz;
                geo.toPZ90._mass[0][2] = geo.wy;
                geo.toPZ90._mass[1][0] = geo.wz;
                geo.toPZ90._mass[1][2] = -geo.wx;
                geo.toPZ90._mass[2][0] = -geo.wy;
                geo.toPZ90._mass[2][1] = geo.wx;
                geo.mToPZ90 = geo.ms;

                geo.toSK42._mass[0][1] = 0;
                geo.toSK42._mass[0][2] = 0;
                geo.toSK42._mass[1][0] = 0;
                geo.toSK42._mass[1][2] = 0;
                geo.toSK42._mass[2][0] = 0;
                geo.toSK42._mass[2][1] = 0;
                geo.mToSK42 = 0;
            } else {
                double wxPz, wyPz, wzPz, wxSk, wySk, wzSk;
                wxPz = geo.DegreeSecondsToRadians(0);
                wyPz = geo.DegreeSecondsToRadians(0);
                wzPz = geo.DegreeSecondsToRadians(-0.2);

                wxSk = geo.DegreeSecondsToRadians(0);
                wySk = geo.DegreeSecondsToRadians(-0.35);
                wzSk = geo.DegreeSecondsToRadians(-0.66);

                geo.dxdydzPz._mass[0][0] = -1.1;
                geo.dxdydzPz._mass[1][0] = -0.3;
                geo.dxdydzPz._mass[2][0] = -0.9;

                geo.toPZ90._mass[0][1] = -wzPz;
                geo.toPZ90._mass[0][2] = wyPz;
                geo.toPZ90._mass[1][0] = wzPz;
                geo.toPZ90._mass[1][2] = -wxPz;
                geo.toPZ90._mass[2][0] = -wyPz;
                geo.toPZ90._mass[2][1] = wxPz;
                geo.mToPZ90 = -0.12 * (1e-6);

                geo.dxdydzSk42._mass[0][0] = 25;
                geo.dxdydzSk42._mass[1][0] = -141;
                geo.dxdydzSk42._mass[2][0] = -80;

                geo.toSK42._mass[0][1] = -wzSk;
                geo.toSK42._mass[0][2] = wySk;
                geo.toSK42._mass[1][0] = wzSk;
                geo.toSK42._mass[1][2] = -wxSk;
                geo.toSK42._mass[2][0] = -wySk;
                geo.toSK42._mass[2][1] = wxSk;
                geo.mToSK42 = 0;
            }
        }
        catch(Exception er){
            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
            dlgAlert.setMessage("Ошибка в коде :(");
            dlgAlert.setTitle("Ошибка!");
            dlgAlert.setIcon(android.R.drawable.ic_dialog_alert);
            dlgAlert.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //dismiss the dialog
                            Toast.makeText(getApplicationContext(), "Что-то пошло не так...",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
            dlgAlert.setNegativeButton("No", null);
            dlgAlert.show();
        }
    }
    public void OnClick(View view){
        try {
            double LatWGS84 = Double.parseDouble(((TextView) findViewById(R.id.LatText)).getText().toString());
            double LonWGS84 = Double.parseDouble(((TextView) findViewById(R.id.LonText)).getText().toString());
            double AltWGS84 = Double.parseDouble(((TextView) findViewById(R.id.AltText)).getText().toString());

            MyMatrix SK42 = geo.WGS84_To_SK42(LatWGS84, LonWGS84, AltWGS84);
            List<Double> list = geo.ECEF2LLA_SK42(SK42._mass[0][0], SK42._mass[1][0], SK42._mass[2][0]);

            ((TextView) findViewById(R.id.Lat_SK42_TextView)).setText(list.get(0).toString());
            ((TextView) findViewById(R.id.Lon_SK42_TextView)).setText(list.get(1).toString());
            ((TextView) findViewById(R.id.Alt_SK42_TextView)).setText(list.get(2).toString());

            ((TextView) findViewById(R.id.GausseResultX)).setText(String.valueOf(geo.Gauss_Kruger_X(list.get(0), list.get(1))));
            ((TextView) findViewById(R.id.GausseResultY)).setText(String.valueOf(geo.Gauss_Kruger_Y(list.get(0), list.get(1))));
        }
        catch(Exception er){
            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
            dlgAlert.setMessage("Ошибка в коде :(");
            dlgAlert.setTitle("Ошибка!");
            dlgAlert.setIcon(android.R.drawable.ic_dialog_alert);
            dlgAlert.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //dismiss the dialog
                            Toast.makeText(getApplicationContext(), "Что-то пошло не так...",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
            dlgAlert.setNegativeButton("No", null);
            dlgAlert.show();
        }
    }
    public void wgs84_to_sk42Click(){

        try {
           /* double LatWGS84 = LLA_To_Degree(((TextView) findViewById(R.id.LonText)).getText().toString());
            double LonWGS84 = LLA_To_Degree(((TextView)findViewById(R.id.LatText)).getText().toString());
            double AltWGS84 = LLA_To_Degree(((TextView)findViewById(R.id.AltText)).getText().toString());*/

            double LatWGS84 = Double.parseDouble(((TextView) findViewById(R.id.LonText)).getText().toString());
            double LonWGS84 =  Double.parseDouble(((TextView) findViewById(R.id.LatText)).getText().toString());
            double AltWGS84 =  Double.parseDouble(((TextView) findViewById(R.id.AltText)).getText().toString());

            MyMatrix SK42 = geo.WGS84_To_SK42(LatWGS84, LonWGS84, AltWGS84);
            List<Double> list = geo.ECEF2LLA_SK42(SK42._mass[0][0], SK42._mass[1][0], SK42._mass[2][0]);

            ((TextView)findViewById(R.id.Lon_SK42_TextView)).setText(list.get(0).toString());
            ((TextView)findViewById(R.id.Lat_SK42_TextView)).setText(list.get(1).toString());
            ((TextView)findViewById(R.id.Alt_SK42_TextView)).setText(list.get(2).toString());


        }
        catch (Exception ex) {
            return;
        }
    }
    public String ConvToSec(double grad)
    {
        int curGrad = (int)grad;
        double min = (int)((grad - curGrad) * 60);
        double sec = ((((grad - curGrad) * 60) - curGrad) * 60);
        return String.valueOf(curGrad) + " " + String.valueOf(min) + " " + String.valueOf(sec);
    }
    private double LLA_To_Degree(String str){
        String[] result = str.trim().split("\\s+");
        double trans = 0;
        dev = 1;
        try
        {
            for (String value : result) {
                trans += Double.parseDouble(value) / dev;
                dev*=60;
            }
        }
        catch (Exception ex) {
            return -1;
        }
        return trans;
    }
    private void SK42_To_GausseKruger(){
        geo = new GeoTransforms();
        try
        {
            double LatWGS84 = Double.parseDouble(((TextView)findViewById(R.id.Lon_SK42_TextView)).getText().toString());
            double LonWGS84 = Double.parseDouble(((TextView)findViewById(R.id.Lat_SK42_TextView)).getText().toString());

            ((TextView)findViewById(R.id.GausseResultX)).setText(String.valueOf(geo.Gauss_Kruger_X(LatWGS84, LonWGS84)));
            ((TextView)findViewById(R.id.GausseResultY)).setText(String.valueOf(geo.Gauss_Kruger_Y(LatWGS84, LonWGS84)));
        }
        catch (Exception ex)
        {
            return;
        }
    }
}