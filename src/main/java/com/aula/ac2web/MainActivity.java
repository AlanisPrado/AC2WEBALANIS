package com.aula.ac2web;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText edtNome, edtTempo, edtIngredientes;
    Spinner spCategoria, spDificuldade;
    CheckBox checkFavorita;
    Button btnSalvar;

    RecyclerView recyclerView;

    ArrayList<Receita> lista;
    ReceitaAdapter adapter;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtNome = findViewById(R.id.edtNome);
        edtTempo = findViewById(R.id.edtTempo);
        edtIngredientes = findViewById(R.id.edtIngredientes);

        spCategoria = findViewById(R.id.spCategoria);
        spDificuldade = findViewById(R.id.spDificuldade);

        checkFavorita = findViewById(R.id.checkFavorita);

        btnSalvar = findViewById(R.id.btnSalvar);

        recyclerView = findViewById(R.id.recyclerReceitas);

        db = FirebaseFirestore.getInstance();

        lista = new ArrayList<>();

        adapter = new ReceitaAdapter(lista);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        String[] categorias = {"Doce", "Salgada", "Bebida"};

        ArrayAdapter<String> adapterCategoria =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_dropdown_item,
                        categorias);

        spCategoria.setAdapter(adapterCategoria);

        String[] dificuldades = {"Fácil", "Médio", "Difícil"};

        ArrayAdapter<String> adapterDificuldade =
                new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_dropdown_item,
                        dificuldades);

        spDificuldade.setAdapter(adapterDificuldade);

        btnSalvar.setOnClickListener(v -> salvarReceita());

        listarReceitas();
    }

    private void salvarReceita() {

        String nome = edtNome.getText().toString();
        String tempo = edtTempo.getText().toString();
        String ingredientes = edtIngredientes.getText().toString();

        if(nome.isEmpty()){
            edtNome.setError("Digite o nome");
            return;
        }

        Map<String, Object> receita = new HashMap<>();

        receita.put("nome", nome);
        receita.put("categoria", spCategoria.getSelectedItem().toString());
        receita.put("tempo", tempo);
        receita.put("ingredientes", ingredientes);
        receita.put("dificuldade", spDificuldade.getSelectedItem().toString());
        receita.put("favorita", checkFavorita.isChecked());

        db.collection("receitas")
                .add(receita)
                .addOnSuccessListener(documentReference -> {

                    Toast.makeText(this,
                            "Receita salva",
                            Toast.LENGTH_SHORT).show();

                    listarReceitas();
                });
    }

    private void listarReceitas(){

        db.collection("receitas")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    lista.clear();

                    for(var doc : queryDocumentSnapshots){

                        Receita r = new Receita();

                        r.setId(doc.getId());
                        r.setNome(doc.getString("nome"));
                        r.setCategoria(doc.getString("categoria"));
                        r.setTempo(doc.getString("tempo"));

                        lista.add(r);
                    }

                    adapter.notifyDataSetChanged();
                });
    }
}