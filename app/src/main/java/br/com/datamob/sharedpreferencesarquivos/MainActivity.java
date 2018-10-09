package br.com.datamob.sharedpreferencesarquivos;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

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
        //Abre o SharedPreferences
        SharedPreferences sharedPreferences =
                getSharedPreferences(NOME_CONFIGURACOES
                        , Context.MODE_PRIVATE);

        //Cria objeto responsável por editar o SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //Salva a informação que é composta po um nome e pelo valor
        editor.putString("TIPO", tipoSelecionado);

        editor.apply();
    }

    private void carregaOpcao()
    {
        try
        {
            //Abre o SharedPreferences
            SharedPreferences sharedPreferences
                    = getSharedPreferences(NOME_CONFIGURACOES
                    , Context.MODE_PRIVATE);

            Map<String, ?> all = sharedPreferences.getAll();
            String tipo1 = (String) all.get("TIPO");

            //Realiza leitura da informação passando o nome
            String tipo = sharedPreferences.getString("TIPO", null);
            if (tipo != null)
            {
                if (tipo.equalsIgnoreCase("Externo"))
                    ((RadioButton) findViewById(R.id.rbExterno)).setChecked(true);
                else if (tipo.equalsIgnoreCase("Interno"))
                    ((RadioButton) findViewById(R.id.rbInterno)).setChecked(true);

            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private File getArquivoExterno()
    {
        File file = null;
        //Pega o diretório externo da aplicação
        File externalStorageDirectory = getExternalFilesDir(null);
        //Pega diretório DCIM do Android
        File dcim = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        //Pega diretório raiz do sdcard
        File raiz = Environment.getExternalStorageDirectory();
        //
        //Cria uma pasta no diretório
        File directory = new File(externalStorageDirectory, NOME_PASTA);
        directory.mkdirs();
        //Cria um referêcia para o arquivo
        file = new File(raiz, NOME_ARQUIVO);
        return file;
    }

    private File getArquivoInterno()
    {
        File file = null;
        //Cria uma pasta no diretório
        File directory = getDir(NOME_PASTA, Context.MODE_PRIVATE);
        //Cria uma referência para um arquivo e uma pasta no diretório interno da aplicação
        file = new File(directory, NOME_ARQUIVO);
        return file;
    }

    private File getArquivo(int tipo)
    {
        File file = null;
        switch (tipo)
        {
            case R.id.rbExterno:
                file = getArquivoExterno();
                break;
            case R.id.rbInterno:
                file = getArquivoInterno();
                break;
        }
        return file;
    }

    private void carregaArquivo()
    {
        etTexto.setText("");
        //
        File file = getArquivo(rgTipo.getCheckedRadioButtonId());
        //
        if (file != null && file.exists())
        {
            try
            {
                FileReader fileReader = new FileReader(file);
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
        File file = getArquivo(tipo);
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
        File file = getArquivo(rgTipo.getCheckedRadioButtonId());
        //
        if(file != null)
        {
            if(file.exists())
                file.delete();
        }
    }
}
