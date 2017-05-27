package mo.ipm.mengl.banphone;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

/**
 * Created by laicm on 25/5/2017.
 */

public class StartUpFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_page1, container, false);
        final SeekBar seekBar = (SeekBar)rootView.findViewById(R.id.seekBar);
        final Switch switch1 = (Switch)rootView.findViewById(R.id.switch1);
        if(switch1.isChecked()){
            seekBar.setEnabled(false);
        }
        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(switch1.isChecked()){
                    Intent bleService = new Intent(getActivity(),BLEService.class);
                    bleService.putExtra("RSSI",seekBar.getProgress());
                    getActivity().startService(bleService);
                    seekBar.setEnabled(false);

                }else{
                    Intent bleService = new Intent(getActivity(),BLEService.class);
                    getActivity().stopService(bleService);
                    seekBar.setEnabled(true);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}
