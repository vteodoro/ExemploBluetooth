package br.senai.sp.informatica.mobile.bluetest;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class BuscarDevices extends Activity implements AdapterView.OnItemClickListener{
    private static final int PERMISSION_LOCATION = 1;
    BlueActivity blueActivity = new BlueActivity();
    private List<BluetoothDevice> lista = new ArrayList<>();
    private ListView listView;

    private ProgressDialog dialog;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pesquisar_devices);
        listView = findViewById(R.id.blueList);


        this.registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
        this.registerReceiver(mReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        this.registerReceiver(mReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSION_LOCATION);
        } else {
            buscar();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case PERMISSION_LOCATION:
                if(grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
                    buscar();
                } else {
                    Toast.makeText(this, "Permissão negada", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
        }
    }

    public void buscar(){
        if(blueActivity.bluetoothAdapter.isDiscovering()){
            blueActivity.bluetoothAdapter.cancelDiscovery();
        }
        
        lista.clear();
        //listar também dispositivos conhecidos
        final Set<BluetoothDevice> conhecidos = blueActivity.bluetoothAdapter.getBondedDevices();
        lista.addAll(conhecidos);
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
                lista.add(device);
                count++;
                Toast.makeText(context, "Encontrou: " + device.getName() + ":" + device.getAddress(), Toast.LENGTH_SHORT).show();
            }else if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
                count = 0;
                Toast.makeText(context, "Busca iniciada", Toast.LENGTH_SHORT).show();
            }else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                Toast.makeText(context, "Busca finalizada. " + count + "devices encontrados", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                updateLista();
            }
        }
    };

    protected void updateLista(){
        List<String> nomes = new ArrayList<String>();

        for(BluetoothDevice device : lista){
            boolean pareado = device.getBondState() == BluetoothDevice.BOND_BONDED;
            nomes.add(device.getName() + " - " + device.getAddress() + (pareado ? " *pareado" : ""));

        }

        int layout = android.R.layout.simple_list_item_1;
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, layout, nomes);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((AdapterView.OnItemClickListener) this);
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BluetoothDevice device = lista.get(position);
        String msg = device.getName() + " - " + device.getAddress();
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    protected void onDestroy(){
        super.onDestroy();
        if(blueActivity.bluetoothAdapter != null){
            blueActivity.bluetoothAdapter.cancelDiscovery();
        }
        this.unregisterReceiver(mReceiver);
    }

}
