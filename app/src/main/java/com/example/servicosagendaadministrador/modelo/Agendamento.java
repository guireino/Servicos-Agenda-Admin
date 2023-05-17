package com.example.servicosagendaadministrador.modelo;

public class Agendamento {

    private String nome, contato, email;
    private boolean whatsApp, barba, cabelo;

    public Agendamento(){

    }

    public Agendamento(String nome, String contato, String email, boolean whatsApp, boolean barba, boolean cabelo) {
        this.nome = nome;
        this.contato = contato;
        this.email = email;
        this.whatsApp = whatsApp;
        this.barba = barba;
        this.cabelo = cabelo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getContato() {
        return contato;
    }

    public void setContato(String contato) {
        this.contato = contato;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isWhatsApp() {
        return whatsApp;
    }

    public void setWhatsApp(boolean whatsApp) {
        this.whatsApp = whatsApp;
    }

    public boolean isBarba() {
        return barba;
    }

    public void setBarba(boolean barba) {
        this.barba = barba;
    }

    public boolean isCabelo() {
        return cabelo;
    }

    public void setCabelo(boolean cabelo) {
        this.cabelo = cabelo;
    }
}
