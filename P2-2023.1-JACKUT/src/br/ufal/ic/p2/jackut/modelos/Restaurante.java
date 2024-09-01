package br.ufal.ic.p2.jackut.modelos;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Restaurante {
    private int id;
    private String name;
    private String address;
    private String tipoCozinha;
    private String tipoEmpresa;
    private int idDono;
    public Restaurante(int idDono,String tipoEmpresa,int id,String name,String address,String tipoCozinha){
        this.id = id;
        this.name = name;
        this.address = address;
        this.tipoCozinha = tipoCozinha;
        this.tipoEmpresa = tipoEmpresa;
        this.idDono = idDono;
    }
    public int getIdDono(){
        return this.idDono;
    }
    public int getId(){
        return this.id;
    }
    public String getName() {
        return this.name;
    }
    public String getAddress() {
        return this.address;
    }
    public String getTipoCozinha() {
        return this.tipoCozinha;
    }
    public String getTipoEmpresa() {
        return this.tipoEmpresa;
    }
}

