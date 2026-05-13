package com.example.ac2web;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText edtNome, edtTempo, edtIngredientes;

    Spinner spCategoria, spDificuldade, spFiltroCategoria;

    CheckBox checkFavorita;

    Button btnSalvar;

    RecyclerView recyclerView;

    ArrayList<Receita> lista;

    ReceitaAdapter adapter;

    FirebaseFirestore db;

    String idSelecionado = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        FirebaseApp.initializeApp(this);

        setContentView(R.layout.activity_main);

        edtNome = findViewById(R.id.edtNome);
        edtTempo = findViewById(R.id.edtTempo);
        edtIngredientes = findViewById(R.id.edtIngredientes);

        spCategoria = findViewById(R.id.spCategoria);
        spDificuldade = findViewById(R.id.spDificuldade);
        spFiltroCategoria = findViewById(R.id.spFiltroCategoria);

        checkFavorita = findViewById(R.id.checkFavorita);

        btnSalvar = findViewById(R.id.btnSalvar);

        recyclerView = findViewById(R.id.recyclerReceitas);

        db = FirebaseFirestore.getInstance();

        lista = new ArrayList<>();

        adapter = new ReceitaAdapter(lista, new ReceitaAdapter.OnReceitaClick() {

            @Override
            public void onClick(Receita receita) {

                idSelecionado = receita.getId();

                edtNome.setText(receita.getNome());
                edtTempo.setText(receita.getTempo());
                edtIngredientes.setText(receita.getIngredientes());

                checkFavorita.setChecked(receita.isFavorita());

                Toast.makeText(MainActivity.this,
                        "Modo edição",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(Receita receita) {

                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Excluir")
                        .setMessage("Deseja excluir?")
                        .setPositiveButton("Sim", (dialog, which) -> {

                            db.collection("receitas")
                                    .document(receita.getId())
                                    .delete();

                            listarReceitas();

                            Toast.makeText(MainActivity.this,
                                    "Excluída",
                                    Toast.LENGTH_SHORT).show();

                        })

                        .setNegativeButton("Não", null)
                        .show();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(adapter);

        String[] categorias = {
                "Doce",
                "Salgada",
                "Bebida",
                "Massa",
                "Sobremesa"
        };

        spCategoria.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                categorias
        ));

        String[] dificuldades = {
                "Fácil",
                "Médio",
                "Difícil"
        };

        spDificuldade.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                dificuldades
        ));

        String[] filtroCategorias = {
                "Todas",
                "Doce",
                "Salgada",
                "Bebida",
                "Massa",
                "Sobremesa"
        };

        spFiltroCategoria.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                filtroCategorias
        ));

        spFiltroCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent,
                                       View view,
                                       int position,
                                       long id) {

                listarReceitas();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnSalvar.setOnClickListener(v -> salvarReceita());

        listarReceitas();
    }

    private void salvarReceita() {

        String nome = edtNome.getText().toString().trim();

        String tempo = edtTempo.getText().toString().trim();

        String ingredientes = edtIngredientes.getText().toString().trim();

        if (nome.isEmpty()) {

            edtNome.setError("Digite o nome");

            return;
        }

        if (tempo.isEmpty()) {

            edtTempo.setError("Digite o tempo");

            return;
        }

        if (ingredientes.isEmpty()) {

            edtIngredientes.setError("Digite ingredientes");

            return;
        }

        Map<String, Object> receita = new HashMap<>();

        receita.put("nome", nome);

        receita.put("categoria",
                spCategoria.getSelectedItem().toString());

        receita.put("tempo", tempo);

        receita.put("ingredientes", ingredientes);

        receita.put("dificuldade",
                spDificuldade.getSelectedItem().toString());

        receita.put("favorita",
                checkFavorita.isChecked());

        if (!idSelecionado.isEmpty()) {

            db.collection("receitas")
                    .document(idSelecionado)
                    .update(receita)

                    .addOnSuccessListener(unused -> {

                        Toast.makeText(this,
                                "Atualizada",
                                Toast.LENGTH_SHORT).show();

                        limparCampos();

                        listarReceitas();

                        idSelecionado = "";
                    });

        } else {

            db.collection("receitas")
                    .add(receita)

                    .addOnSuccessListener(documentReference -> {

                        Toast.makeText(this,
                                "SALVOU NO FIREBASE",
                                Toast.LENGTH_LONG).show();

                        limparCampos();

                        listarReceitas();
                    })

                    .addOnFailureListener(e -> {

                        Toast.makeText(this,
                                "ERRO: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    });
        }
    }

    private void listarReceitas() {

        String filtro =
                spFiltroCategoria.getSelectedItem().toString();

        if (filtro.equals("Todas")) {

            db.collection("receitas")
                    .get()

                    .addOnSuccessListener(queryDocumentSnapshots -> {

                        lista.clear();

                        for (var doc : queryDocumentSnapshots) {

                            Receita r = new Receita();

                            r.setId(doc.getId());

                            r.setNome(doc.getString("nome"));

                            r.setCategoria(doc.getString("categoria"));

                            r.setTempo(doc.getString("tempo"));

                            r.setIngredientes(doc.getString("ingredientes"));

                            r.setDificuldade(doc.getString("dificuldade"));

                            Boolean fav =
                                    doc.getBoolean("favorita");

                            r.setFavorita(fav != null && fav);

                            lista.add(r);
                        }

                        adapter.notifyDataSetChanged();
                    });

        } else {

            db.collection("receitas")
                    .whereEqualTo("categoria", filtro)
                    .get()

                    .addOnSuccessListener(queryDocumentSnapshots -> {

                        lista.clear();

                        for (var doc : queryDocumentSnapshots) {

                            Receita r = new Receita();

                            r.setId(doc.getId());

                            r.setNome(doc.getString("nome"));

                            r.setCategoria(doc.getString("categoria"));

                            r.setTempo(doc.getString("tempo"));

                            r.setIngredientes(doc.getString("ingredientes"));

                            r.setDificuldade(doc.getString("dificuldade"));

                            Boolean fav =
                                    doc.getBoolean("favorita");

                            r.setFavorita(fav != null && fav);

                            lista.add(r);
                        }

                        adapter.notifyDataSetChanged();
                    });
        }
    }

    private void limparCampos() {

        edtNome.setText("");

        edtTempo.setText("");

        edtIngredientes.setText("");

        checkFavorita.setChecked(false);

        spCategoria.setSelection(0);

        spDificuldade.setSelection(0);
    }
}
