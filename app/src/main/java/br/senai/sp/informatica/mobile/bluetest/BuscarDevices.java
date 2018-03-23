package br.senai.sp.informatica.mobile.bluetest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.Toast;

public class BuscarDevices extends Activity{
    BlueActivity blueActivity = new BlueActivity();
    ListaPareados listaPareados = new ListaPareados();

    private ProgressDialog dialog;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        this.registerReceiver(mReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        this.registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));

        buscar();
    }

    public void buscar(){
        if(blueActivity.bluetoothAdapter.isDiscovering()){
            blueActivity.bluetoothAdapter.cancelDiscovery();
        }

        blueActivity.bluetoothAdapter.startDiscovery();
        dialog = ProgressDialog.show(this, "BlueEx", "Buscando devices...", false, true);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        private int count;
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device.getBondState() != BluetoothDevice.BOND_BONDED){
                    listaPareados.lista.add(device);
                    Toast.makeText(context, "Encontrou: " + device.getName() + ":" + device.getAddress(), Toast.LENGTH_SHORT).show();
                }
            }else if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
                count = 0;
                Toast.makeText(context, "Busca iniciada", Toast.LENGTH_SHORT).show();
            }else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                Toast.makeText(context, "Busca finalizada. " + count + "devices encontrados", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                listaPareados.updateLista();
            }
        }
    };

    protected void onDestroy(){
        super.onDestroy();
        if(blueActivity.bluetoothAdapter != null){
            blueActivity.bluetoothAdapter.cancelDiscovery();
        }
        this.unregisterReceiver(mReceiver);
    }

}
