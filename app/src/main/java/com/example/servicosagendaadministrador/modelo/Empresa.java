package com.example.servicosagendaadministrador.modelo;

public class Empresa {

    private String imagemUrl, informacao, latitude, longitude, numeroContato, valorServico;

    public Empresa(){

    }

    public Empresa(String imagemUrl, String informacao, String latitude, String longitude, String numeroContato, String valorServico) {
        this.imagemUrl = imagemUrl;
        this.informacao = informacao;
        this.latitude = latitude;
        this.longitude = longitude;
        this.numeroContato = numeroContato;
        this.valorServico = valorServico;
    }

    public String getImagemUrl() {
        return imagemUrl;
    }

    public void setImagemUrl(String imagemUrl) {
        this.imagemUrl = imagemUrl;
    }

    public String getInformacao() {
        return informacao;
    }

    public void setInformacao(String informacao) {
        this.informacao = informacao;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getNumeroContato() {
        return numeroContato;
    }

    public void setNumeroContato(String numeroContato) {
        this.numeroContato = numeroContato;
    }

    public String getValorServico() {
        return valorServico;
    }

    public void setValorServico(String valorServico) {
        this.valorServico = valorServico;
    }
}