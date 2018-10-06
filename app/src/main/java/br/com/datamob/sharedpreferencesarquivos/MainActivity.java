package br.com.datamob.sharedpreferencesarquivos;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

public class MainActivity extends AppCompatActivity
{
    private RadioGroup rgTipo;
    private AppCompatEditText etTexto;
    private Button btApagar;

    private final static String NOME_CONFIGURACOES = "CONFIGURACOES";
    private final static String NOME_ARQUIVO = "ARQUIVO_TEXTO.txt";
    private final static String NOME_PASTA = "PASTA DE DADOS";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inicializaComponenetes();
        carregaOpcao();
        carregaArquivo();
        inicializaEventos();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        salvaArquivo(rgTipo.getCheckedRadioButtonId());
    }

    private void inicializaComponenetes()
    {
        rgTipo = findViewById(R.id.rgTipo);
        etTexto = findViewById(R.id.etTexto);
        btApagar = findViewById(R.id.btApagar);
    }

    private void inicializaEventos()
    {
        rgTipo.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                switch (checkedId)
                {
                    case R.id.rbExterno:
                        salvaArquivo(R.id.rbInterno);
                        salvaOpcao("Externo");
                        carregaArquivo();
                        break;
                    case R.id.rbInterno:
                        salvaArquivo(R.id.rbExterno);
                        salvaOpcao("Interno");
                        carregaArquivo();
                        break;
                }
            }
        });
        //
        btApagar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                apagaArquivo();
            }
        });
    }

    private void salvaOpcao(String tipoSelecionado)
    {
        SharedPreferences sharedPreferences = getSharedPreferences(NOME_CONFIGURACOES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("TIPO", tipoSelecionado);
        editor.apply();
    }

    private void carregaOpcao()
    {
        SharedPreferences sharedPreferences = getSharedPreferences(NOME_CONFIGURACOES, Context.MODE_PRIVATE);
        String tipo = sharedPreferences.getString("TIPO", null);
        if (tipo != null)
        {
            if (tipo.equalsIgnoreCase("Externo"))
                ((RadioButton) findViewById(R.id.rbExterno)).setChecked(true);
            else if (tipo.equalsIgnoreCase("Interno"))
                ((RadioButton) findViewById(R.id.rbInterno)).setChecked(true);

        }
    }

    private void carregaArquivo()
    {
        etTexto.setText("");
        //
        File file = null;
        switch (rgTipo.getCheckedRadioButtonId())
        {
            case R.id.rbExterno:
                File externalStorageDirectory = getExternalFilesDir(null);
                File directory = new File(externalStorageDirectory, NOME_PASTA);
                directory.mkdirs();
                file = new File(directory, NOME_ARQUIVO);
                break;
            case R.id.rbInterno:
                file = new File(getDir(NOME_PASTA, Context.MODE_PRIVATE), NOME_ARQUIVO);

                break;
        }
        //
        if (file != null && file.exists())
        {
            FileReader fileReader = null;
            try
            {
                fileReader = new FileReader(file);
                char[] data = new char[(int) file.length()];
                fileReader.read(data);
                etTexto.setText(new String(data));
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void salvaArquivo(int tipo)
    {
        File file = null;
        switch (tipo)
        {
            case R.id.rbExterno:
                File externalStorageDirectory = getExternalFilesDir(null);
                File directory = new File(externalStorageDirectory, NOME_PASTA);
                directory.mkdirs();
                file = new File(directory, NOME_ARQUIVO);
                break;
            case R.id.rbInterno:
                file = new File(getDir(NOME_PASTA, Context.MODE_PRIVATE), NOME_ARQUIVO);
                break;
        }
        //
        if(file != null)
        {
            if (file.exists())
                file.delete();
            //
            try
            {
                file.createNewFile();
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(etTexto.getText().toString());
                fileWriter.flush();
                fileWriter.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void apagaArquivo()
    {
        etTexto.setText("");
        //
        File file = null;
        switch (rgTipo.getCheckedRadioButtonId())
        {
            case R.id.rbExterno:
                File externalStorageDirectory = getExternalFilesDir(null);
                File directory = new File(externalStorageDirectory, NOME_PASTA);
                directory.mkdirs();
                file = new File(directory, NOME_ARQUIVO);
                break;
            case R.id.rbInterno:
                file = new File(getDir(NOME_PASTA, Context.MODE_PRIVATE), NOME_ARQUIVO);
                break;
        }
        if(file != null)
        {
            if(file.exists())
                file.delete();
        }
    }
}
