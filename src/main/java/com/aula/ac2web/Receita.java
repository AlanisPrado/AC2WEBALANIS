package com.aula.ac2web;

public class Receita {

    private String id;
    private String nome;
    private String categoria;
    private String tempo;
    private String ingredientes;
    private String dificuldade;
    private boolean favorita;

    public Receita() {
    }

    public Receita(String id, String nome, String categoria,
                   String tempo, String ingredientes,
                   String dificuldade, boolean favorita) {

        this.id = id;
        this.nome = nome;
        this.categoria = categoria;
        this.tempo = tempo;
        this.ingredientes = ingredientes;
        this.dificuldade = dificuldade;
        this.favorita = favorita;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getTempo() {
        return tempo;
    }

    public void setTempo(String tempo) {
        this.tempo = tempo;
    }

    public String getIngredientes() {
        return ingredientes;
    }

    public void setIngredientes(String ingredientes) {
        this.ingredientes = ingredientes;
    }

    public String getDificuldade() {
        return dificuldade;
    }

    public void setDificuldade(String dificuldade) {
        this.dificuldade = dificuldade;
    }

    public boolean isFavorita() {
        return favorita;
    }

    public void setFavorita(boolean favorita) {
        this.favorita = favorita;
    }
}

