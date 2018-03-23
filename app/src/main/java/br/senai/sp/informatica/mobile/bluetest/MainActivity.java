package br.senai.sp.informatica.mobile.bluetest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void ligar(View view){
        startActivity(new Intent(this, BlueActivity.class));
    }

    public void listar(View view){
        startActivity(new Intent(this, ListaPareados.class));
    }

    public void buscar(View view){
        startActivity(new Intent(this, BuscarDevices.class));
    }
}
