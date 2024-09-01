package br.ufal.ic.p2.jackut.modelos;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class UsuarioDono extends Usuario {
    private String cpf;
    public UsuarioDono(int id,String name,String email, String senha, String address, String cpf){
        super(id,name, email, senha,address);

        this.cpf = cpf;
    }
    public String getCpf(){
        return this.cpf;
    }
}

