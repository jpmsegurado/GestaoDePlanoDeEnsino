package gestaodeplanodeensino;

/**
 *
 * @author JoãoPedro
 */
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author João Pedro
 */
public class Main {
    

    private static JSONArray listaDisciplinas = new JSONArray();
    
    public static void main(String[] args) {
        iniciar();
    }
    
    public static void iniciar(){
        listaDisciplinas = getAllDisciplinas();
        System.out.println("Seja bem vindo ao Sistema de gestão de plano de ensino, por favor selecione uma opção:\n"
                + "1 - cadastrar disciplina\n"
                + "2 - listar disciplinas\n"
                + "3 - editar disciplina\n"
                + "4 - remover disciplina\n"
                + "5 - gerar plano de ensino\n"
                + "6 - sair");
        
        Scanner scanner = new Scanner(System.in);
        String comando;
        int numeroComando = -1;
        
        try {
            comando = scanner.next();
            numeroComando = Integer.parseInt(comando);
        } catch (NumberFormatException e) {
        }
        
        switch(numeroComando){
            case 1:
                cadastrarDisciplina();
                break;
            case 2:
                listarDisciplinas();
                break;
                
            case 6:
                return;
            default:
                System.out.println("\n\nPor favor digite um comando válido...\n\n");
                iniciar();
                break;
        }
        
    }
    
    public static void cadastrarDisciplina(){
        System.out.println("\nNome da disciplina:\n");
        Scanner scanner = new Scanner(System.in);
        String nome = scanner.next();
        
        while(nome.isEmpty()){
            System.out.println("\n\nPor favor digite um nome válido...\n\n");
            nome = scanner.next();
        }
        
        System.out.println("\nDescrição da disciplina:\n");
        String descricao = scanner.next();
        
        while(descricao.isEmpty()){
            System.out.println("\n\nPor favor digite uma descrição válido...\n\n");
            descricao = scanner.next();
        }
        
        System.out.println("\nCarga horária total da disciplina:\n");
        int cargaHoraria = -1;
        do{
            String carga = scanner.next();
            try {
                cargaHoraria = Integer.parseInt(carga);
            } catch (NumberFormatException e){
                System.out.println("\n\nPor favor digite uma carga horária válida...\n\n");
            }
        }while(cargaHoraria <= 0);
        
        int cargaTotal = 0;
        ArrayList<ItemDeEmenta> ementa = new ArrayList<>();
        do{
            cargaTotal = 0;
            ementa = retornaItensDeEmenta(cargaHoraria);
            
            for( int posicao = 0; posicao < ementa.size(); posicao++ ){
                cargaTotal += ementa.get(posicao).getCargaHoraria();
            }
            if(cargaTotal > cargaHoraria){
                System.out.println("\nA soma da carga horária dos itens de ementa"
                        + " é maior que a carga horária da disciplina. ");
            }else if(cargaTotal < cargaHoraria){
                System.out.println("\nA soma da carga horária dos itens de ementa"
                        + " é menor que a carga horária da disciplina. ");
            }
        }while(cargaTotal != cargaHoraria || cargaTotal == 0);
        
        ArrayList<LivroDeReferencia> bibliografia = retornaBibliografia();
        
        Disciplina disciplina = new Disciplina(nome,descricao,cargaHoraria,ementa,bibliografia,0,0);
        salvarDisciplina(disciplina);
        iniciar();
        
    }
    
    public static void listarDisciplinas(){
        if(listaDisciplinas.length() > 0){
            StringBuilder text  = new StringBuilder();
            text.append("\n---------------------------------------------------\n");
            for(int i=0; i<listaDisciplinas.length();i++){
                text.append(i+1).append("ª disciplina");
                text.append("\nNome: ").append(listaDisciplinas.getJSONObject(i).get(Contract.NOME));
                text.append("\nCarga horária: ")
                        .append(listaDisciplinas.getJSONObject(i).get(Contract.CARGA_HORARIA))
                        .append("h");
                
                text.append("\n---------------------------------------------------\n");
            }
            
            System.out.println(text.toString());
        }else{
            System.out.println("\n\nNenhuma disciplina cadastrada \n\n");
        }
        
        iniciar();
    }
    
    public static JSONArray getAllDisciplinas(){
        BufferedReader br;
        try {
             br = new BufferedReader(new FileReader("filename.txt"));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            String json = sb.toString();
            br.close();
            
            return new JSONArray(json);
            
        }catch (FileNotFoundException ex) {
            iniciarArquivo();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        
        return null;
    }
    
    public static void salvarDisciplina(Disciplina d){
        JSONObject obj = new JSONObject();
        
        obj.put("nome",d.getNome());
        obj.put(Contract.CARGA_HORARIA,d.getCargaHoraria());
        obj.put(Contract.NOME, d.getNome());
        JSONArray itens = new JSONArray();
        if(d.getItens().size() > 0){
            for(int i=0; i<d.getItens().size();i++){
                JSONObject newObj = new JSONObject();
                ItemDeEmenta item = d.getItens().get(i);
                newObj.put(Contract.NOME, item.getNome());
                newObj.put(Contract.CARGA_HORARIA, item.getCargaHoraria());
                itens.put(newObj.toString());
            }
        }
        obj.put(Contract.ITENS_DE_EMENTA, itens.toString());
        
        listaDisciplinas.put(obj);
        
        try {
            PrintWriter out = new PrintWriter("filename.txt");
            out.println(listaDisciplinas.toString());
            out.close();
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
        
    }
    
    public static void iniciarArquivo(){
        try {
            PrintWriter out = new PrintWriter("filename.txt");
            out.close();
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public static ArrayList<ItemDeEmenta> retornaItensDeEmenta(int cargaHoraria){
        System.out.println("\nCadastando os itens de ementa\n");
        ArrayList<ItemDeEmenta> ementa = new ArrayList<>();
        int comando = -1;
        Scanner scanner = new Scanner(System.in);
        do{
            ItemDeEmenta itemNovo = cadastrarItemDeEmenta();
            ementa.add(itemNovo);
            
            System.out.println("\nDeseja cadastrar mais itens?\n"
                    + "1 - sim\n"
                    + "2 - não\n");
            comando = scanner.nextInt();
            
        }while(comando == 1);
        
        return ementa;
    }
    
    public static ArrayList<LivroDeReferencia> retornaBibliografia(){
        System.out.println("\nCadastando a bibliografia\n");
        ArrayList<LivroDeReferencia> biblbiografia = new ArrayList<>();
        int comando = -1;
        Scanner scanner = new Scanner(System.in);
        do{
            LivroDeReferencia itemNovo = cadastrarLivro();
            biblbiografia.add(itemNovo);
            
            System.out.println("\nDeseja cadastrar mais itens?\n"
                    + "1 - sim\n"
                    + "2 - não\n");
            comando = scanner.nextInt();
            
        }while(comando == 1);
        
        return biblbiografia;
    }
    
    
    public static LivroDeReferencia cadastrarLivro(){
        System.out.println("\nDigite o nome do livro\n");
        Scanner scanner = new Scanner(System.in);
        String nome = null;
        
        do{
            nome = scanner.next();
            if(nome.isEmpty()){
                System.out.println("\n\n Por favor digite um nome válido...\n\n");
            }
        }while(nome.isEmpty());
        
        String autor;
        System.out.println("\nDigite o nome do autor do livro:\n");
        do{
            autor = scanner.next();
            if(autor.isEmpty()){
                System.out.println("\n\n Por favor digite um nome válido...\n\n");
            }
        }while(autor.isEmpty());
        
        String editora;
        
        System.out.println("\nDigite o nome da editora do livro:\n");
        do{
            editora = scanner.next();
            if(editora.isEmpty()){
                System.out.println("\n\n Por favor digite um nome válido...\n\n");
            }
        }while(editora.isEmpty());
        
        String edicao;
        int numeroEdicao = -1;
        
        System.out.println("\nDigite a edicao do livro:\n");
        do{
            edicao = scanner.next();
            try {
                numeroEdicao = Integer.parseInt(edicao);
            } catch (NumberFormatException e){
                System.out.println("\n\nPor favor digite uma carga horária válida...\n\n");
            }
        }while(numeroEdicao <= 0);
        
        LivroDeReferencia livro = new LivroDeReferencia(nome, editora, editora, numeroEdicao);
        return livro;
        
        
    }
    
    public static ItemDeEmenta cadastrarItemDeEmenta(){
        System.out.println("\nDigite o nome do item de ementa:\n");
        Scanner scanner = new Scanner(System.in);
        String nome = null;
        
        do{
            nome = scanner.next();
            if(nome.isEmpty()){
                System.out.println("\n\n Por favor digite um nome válido...\n\n");
            }
        }while(nome.isEmpty());
        
        String carga;
        int cargaHorari = -1;
        
        System.out.println("\nDigite a carga horária do item de ementa:\n");
        do{
            carga = scanner.next();
            try {
                cargaHorari = Integer.parseInt(carga);
            } catch (NumberFormatException e){
                System.out.println("\n\nPor favor digite uma carga horária válida...\n\n");
            }
        }while(cargaHorari <= 0);
        
        
        ItemDeEmenta item = new ItemDeEmenta(nome, cargaHorari);
        return item;
        
        
    }
    
}

