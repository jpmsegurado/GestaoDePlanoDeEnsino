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
public class LivroDeReferencia {
    private String nome,autor,editora;
    private int edicao;
    
    public LivroDeReferencia(String nome,String autor,String editora,int edicao){
        this.autor = autor;
        this.nome = nome;
        this.editora = editora;
        this.edicao = edicao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getEditora() {
        return editora;
    }

    public void setEditora(String editora) {
        this.editora = editora;
    }

    public int getEdicao() {
        return edicao;
    }

    public void setEdicao(int edicao) {
        this.edicao = edicao;
    }
    
    
    
}
