package mo.ipm.mengl.banphone;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;

import android.content.SharedPreferences;
import android.os.Bundle;

import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by laicm on 25/5/2017.
 */

public class DeviceScannerFragment extends ListFragment {

    private BluetoothAdapter mBluetoothAdapter;
    private List<BLEDevice> mdevices = new ArrayList<BLEDevice>();
    private BLEAdapter mAdapter;
    private HashSet<String> selecetAddress = new HashSet<String>();

    @Override
    public void onDestroyView() {
        mdevices.clear();
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_page3, container, false);
        final BluetoothManager bluetoothManager = (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        startScan();
        return rootView;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SelectDevices", Context.MODE_PRIVATE);
        selecetAddress = (HashSet<String>) sharedPreferences.getStringSet("address", new HashSet<String>());
        Log.d("SIZE",""+selecetAddress.size());

        mAdapter = new BLEAdapter(mdevices);
        setListAdapter(mAdapter);



    }

    private void startScan() {
        mBluetoothAdapter.getBluetoothLeScanner().startScan(mScanCallback);
    }
    private void stopScan() {
        mBluetoothAdapter.getBluetoothLeScanner().stopScan(mScanCallback);
    }

    @Override
    public void onStop() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SelectDevices",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("Size",String.valueOf(selecetAddress.size()).trim());
        for(int i = 0;i<selecetAddress.size();i++){
            editor.putStringSet("address",selecetAddress);
        }
        editor.commit();

        stopScan();
        super.onStop();
    }

    private ScanCallback mScanCallback =
            new ScanCallback() {
                @Override
                public void onScanFailed(int errorCode) {
                    super.onScanFailed(errorCode);
                }

                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    boolean hasContain = false;
                    for (BLEDevice d: mdevices) {
                        if(d.getAddress().equals(result.getDevice().getAddress())) {
                            Log.d("Device1","XX");
                            hasContain = true;
                            break;
                        }
                    }
                    if(!hasContain){
                        BluetoothDevice d = result.getDevice();
                        boolean status = false;
                        for (String s:selecetAddress) {
                            if(s.equals(d.getAddress())){
                                status = true;
                            }
                        }
                        mdevices.add(new BLEDevice.Builder(d.getAddress()).setName(d.getName()).setStatus(status).build());
                        mAdapter.notifyDataSetChanged();
                        Log.d("Device1",""+mdevices.size());
                    }
                }

                @Override
                public void onBatchScanResults(List<ScanResult> results) {
                    super.onBatchScanResults(results);
                }
            };

    private class BLEAdapter extends ArrayAdapter<BLEDevice>{
        public BLEAdapter(List<BLEDevice> list) {
            super(getActivity(),  android.R.layout.simple_list_item_1, list);

        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null){
                convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_ble,null);
            }

            CheckBox name = (CheckBox)convertView.findViewById(R.id.checkBox);
            TextView address = (TextView)convertView.findViewById(R.id.textView);
            address.setText(mdevices.get(position).getAddress());
            name.setText(mdevices.get(position).getName());
            name.setChecked(mdevices.get(position).isStatus());
            name.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        selecetAddress.add(mdevices.get(position).getAddress());
                    }else{
                        selecetAddress.remove(mdevices.get(position).getAddress());
                    }

                }
            });

            return convertView;
        }
    }


}
