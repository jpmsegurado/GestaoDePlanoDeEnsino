/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gestaodeplanodeensino;

/**
 *
 * @author JoãoPedro
 */
public class ItemDeEmenta {
    private String nome;
    private int cargaHoraria;
    
    public ItemDeEmenta(String nome,int carga){
        this.nome = nome;
        this.cargaHoraria = carga;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getCargaHoraria() {
        return cargaHoraria;
    }

    public void setCargaHoraria(int cargaHoraria) {
        this.cargaHoraria = cargaHoraria;
    }
}

