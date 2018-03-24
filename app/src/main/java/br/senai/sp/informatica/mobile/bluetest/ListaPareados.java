package br.senai.sp.informatica.mobile.bluetest;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class ListaPareados extends BlueActivity implements AdapterView.OnItemClickListener {

    public List<BluetoothDevice> lista;
    private ListView listView;

    @Override
    public void onCreate(Bundle bundle){
        super.onCreate(bundle);
        setContentView(R.layout.pesquisar_devices);
        listView = (ListView) findViewById(R.id.blueList);
    }

    @Override
    protected void onResume(){
        super.onResume();
        if(bluetoothAdapter != null){
            lista = new ArrayList<BluetoothDevice>(bluetoothAdapter.getBondedDevices());
            updateLista();
        }
    }

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
}
