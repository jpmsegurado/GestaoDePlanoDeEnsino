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
    
    private String nome,descricao;
    private int cargaHoraria;
    private ArrayList<ItemDeEmenta> itens;
    private ArrayList<LivroDeReferencia> bibliografia;

    public Disciplina(String nome, String descricao, int cargaHoraria, ArrayList<ItemDeEmenta> itens, ArrayList<LivroDeReferencia> bibliografia) {
        this.nome = nome;
        this.descricao = descricao;
        this.cargaHoraria = cargaHoraria;
        this.itens = itens;
        this.bibliografia = bibliografia;
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

