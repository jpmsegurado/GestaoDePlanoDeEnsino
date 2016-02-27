/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gestaodeplanodeensino;

import java.util.ArrayList;

/**
 *
 * @author JoãoPedro
 */
public class Disciplina {
    
    private String id,nome,descricao;
    private int cargaHoraria;
    private ArrayList<ItemDeEmenta> itens;
    private ArrayList<LivroDeReferencia> bibliografia;

    public Disciplina(String id, String nome, String descricao, int cargaHoraria) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.cargaHoraria = cargaHoraria;
        
    }


    
    public ArrayList<ItemDeEmenta> getItens() {
        return itens;
    }

    public void setItens(ArrayList<ItemDeEmenta> itens) {
        this.itens = itens;
    }

    public ArrayList<LivroDeReferencia> getBibliografia() {
        return bibliografia;
    }

    public void setBibliografia(ArrayList<LivroDeReferencia> bibliografia) {
        this.bibliografia = bibliografia;
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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getCargaHoraria() {
        return cargaHoraria;
    }

    public void setCargaHoraria(int cargaHoraria) {
        this.cargaHoraria = cargaHoraria;
    }

    
    
    
    
}

