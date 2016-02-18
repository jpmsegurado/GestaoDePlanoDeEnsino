package gestaodeplanodeensino;

/**
 *
 * @author JoãoPedro
 */
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author João Pedro
 */
public class Main {
    

    private static JSONArray listaDisciplinas = new JSONArray();
    
    public static void main(String[] args) {
        System.out.println("Seja bem vindo ao Sistema de gestão de plano de ensino.\n");
        iniciar();
    }
    
    public static void iniciar(){
        listaDisciplinas = getAllDisciplinas();
        System.out.println("\n\nPor favor selecione uma opção:\n"
                + "1 - cadastrar disciplina\n"
                + "2 - listar disciplinas\n"
                + "3 - editar disciplina\n"
                + "4 - remover disciplina\n"
                + "5 - gerar plano de ensino\n"
                + "6 - sair\n");
        
        Scanner scanner = new Scanner(System.in);
        String comando;
        int numeroComando = -1;
        
        try {
            comando = scanner.nextLine();
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
        String nome = scanner.nextLine();
        
        while(nome.isEmpty()){
            System.out.println("\n\nPor favor digite um nome válido...\n\n");
            nome = scanner.nextLine();
        }
        
        System.out.println("\nDescrição da disciplina:\n");
        String descricao = scanner.nextLine();
        
        while(descricao.isEmpty()){
            System.out.println("\n\nPor favor digite uma descrição válido...\n\n");
            descricao = scanner.nextLine();
        }
        
        System.out.println("\nCarga horária total da disciplina:\n");
        int cargaHoraria = -1;
        do{
            String carga = scanner.nextLine();
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
        
        Disciplina disciplina = new Disciplina(nome,descricao,cargaHoraria,ementa,bibliografia);
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
            selecionarDisciplina();
        }else{
            System.out.println("\n\nNenhuma disciplina cadastrada \n\n");
            iniciar();
        }
        
        
    }
    
    public static void selecionarDisciplina(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Qual disciplina deseja selecionar(digite o número da ordem):");
        int ordem = -1;
        do{
            try{
                ordem = Integer.valueOf(scanner.nextLine());
            }catch(NumberFormatException e){}
            if(ordem <= 0 || ordem > listaDisciplinas.length()){
                System.out.println("\nPor favor, digite um valor válido\n");
            }
        }while(ordem <= 0 || ordem > listaDisciplinas.length());
        imprimirDisciplina(ordem - 1);
    };
    
    public static void imprimirDisciplina(int ordem){
        JSONObject disciplina = listaDisciplinas.getJSONObject(ordem);
        JSONArray itensDeEmenta = disciplina.getJSONArray(Contract.ITENS_DE_EMENTA);
        StringBuilder text = new StringBuilder();
        text.append("\n\nNome da disciplina: ").append(disciplina.get(Contract.NOME));
                text.append("\nCarga horária da disciplina: ")
                        .append(disciplina.get(Contract.CARGA_HORARIA))
                        .append("h");
        if(itensDeEmenta.length() > 0){
            text.append("\n\nItens de ementa:");
            for(int i=0; i<itensDeEmenta.length(); i++){
                text.append("\n---------------------------------------------------");
                text.append("\nItem de ementa ").append(i+1);
                text.append("\nNome do item: ").append(itensDeEmenta.getJSONObject(i).get(Contract.NOME));
                text.append("\nCarga horária: ")
                        .append(itensDeEmenta.getJSONObject(i).get(Contract.CARGA_HORARIA))
                        .append("h");
                if(i == itensDeEmenta.length() - 1){
                    text.append("\n---------------------------------------------------\n");
                }
            }
        }
        
        System.out.println(text.toString());
        
        Scanner scanner = new Scanner(System.in);
        int comando = -1;
        System.out.println("Selecione uma opção:\n"
                + "1 - editar item de ementa\n"
                + "2 - deletar item de ementa\n"
                + "3 - editar informações desta disciplina\n"
                + "4 - voltar pro inicio\n");
        do{
            try {
                comando = Integer.valueOf(scanner.nextLine());
            }catch (NumberFormatException e) {}
            if(comando <= 0 || comando > 4){
                System.out.println("Por favor digite um comando válido.");
            }
        }while(comando <= 0 || comando > 4);
        
        
        switch(comando){
            case 1:
                selecionarItemEmentaEditado(ordem);
                break;
            case 2:
                selecionarItemADeletar(ordem);
                break;
            case 3:
                break;
            case 4:
                iniciar();
                break;
        }
    };
    
    public static void selecionarItemADeletar(int indiceDisciplina){
        System.out.println("\nDigite a ordem do item que deseja deletar:\n");
        int ordem = -1;
        Scanner scanner = new Scanner(System.in);
        do{
            try{
                ordem = Integer.valueOf(scanner.nextLine());
            }catch(NumberFormatException e){};
            if(ordem <= 0){
                System.out.println("\nPor favor digite um valor válido:\n");
            }
        }while(ordem <= 0);
        JSONObject novaDisciplina = listaDisciplinas.getJSONObject(indiceDisciplina);
        novaDisciplina.getJSONArray(Contract.ITENS_DE_EMENTA).remove(ordem-1);
        listaDisciplinas.put(indiceDisciplina, novaDisciplina);
        regravrarDisciplinas();
        System.out.println("\nItem removido com sucesso.\n");
        iniciar();
    }
    
    public static void editarDisciplina(int indiceDisciplina){
        
    }
    
    public static void selecionarItemEmentaEditado(int indiceDisciplina){
        JSONObject disciplina = listaDisciplinas.getJSONObject(indiceDisciplina);
        JSONArray itensDeEmenta = disciplina.getJSONArray(Contract.ITENS_DE_EMENTA);
        StringBuilder text = new StringBuilder();
       
        
        System.out.println(text.toString());
        int comando = -1;
        System.out.println("Por favor, digite o número de qual item de ementa deseja editar:");
        do{
            Scanner scanner = new Scanner(System.in);   
            try{
                comando = Integer.valueOf(scanner.nextLine());
            }catch(NumberFormatException e){};
            
            if(comando <= 0 || comando > disciplina.getJSONArray(Contract.ITENS_DE_EMENTA).length()){
                System.out.println("\nPor favor, digite um valor válido\n");
            }
            
        }while(comando <= 0 || comando > disciplina.getJSONArray(Contract.ITENS_DE_EMENTA).length());
        
        editarItemDeEmenta(indiceDisciplina, comando - 1);
    }
    
    public static void editarItemDeEmenta(int indiceDisciplina,int indiceItemEmenta){
        JSONObject disciplina = listaDisciplinas.getJSONObject(indiceDisciplina);
        JSONObject itemEmenta = disciplina.getJSONArray(Contract.ITENS_DE_EMENTA).getJSONObject(indiceItemEmenta);
        
        System.out.println("\nDigite o novo nome do item de ementa:\n");
        Scanner scanner = new Scanner(System.in);
        String nome = null;
        do{
            nome = scanner.nextLine();
            if(nome.isEmpty()){
                System.out.println("\nPor favor digite um nome válido");
            }
        }while(nome.isEmpty());
        
        int carga = -1;
        System.out.println("Digite a nova carga horária do item de ementa:\n");
        do{
            carga = Integer.valueOf(scanner.nextLine());
            if(carga <= 0){
                System.out.println("\nPor favor digite um valor válido.");
            }else if(carga > disciplina.getInt(Contract.CARGA_HORARIA)){
                System.out.println("\nO valor da carga é maior que a carga da disciplina, tente novamnte:\n");
                carga = -1;
            }else{
                int cargaTotal = 0;
                for(int i=0; i<disciplina.getJSONArray(Contract.ITENS_DE_EMENTA).length(); i++){
                    if(i == indiceItemEmenta){
                        cargaTotal += carga;
                    }else{
                        cargaTotal += disciplina
                                .getJSONArray(Contract.ITENS_DE_EMENTA)
                                .getJSONObject(i).getInt(Contract.CARGA_HORARIA);
                    }
                }
                
                if(cargaTotal > disciplina.getInt(Contract.CARGA_HORARIA)){
                    carga = -1;
                    System.out.println("\nO A soma das cargas horárias dos itens de ementa é maior que a carga da disciplina, tente novamnte:\n");
                }
            }
        }while(carga <= 0);
        
        itemEmenta.put(Contract.CARGA_HORARIA, carga);
        itemEmenta.put(Contract.NOME, nome);
        
        disciplina.getJSONArray(Contract.ITENS_DE_EMENTA).put(indiceItemEmenta, itemEmenta);
        listaDisciplinas.put(indiceDisciplina, disciplina);
        regravrarDisciplinas();
        System.out.println("\nAlterado com sucesso\n");
        iniciar();
    }
    
    public static void regravrarDisciplinas(){
        try {
            PrintWriter out = new PrintWriter("filename.txt");
            out.println(listaDisciplinas.toString());
            out.close();
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
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
            
            if(json.length() <= 10){
                return new JSONArray();
            }else{
                return new JSONArray(json);
            }
            
        }catch (FileNotFoundException ex) {
            iniciarArquivo();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        
        return new JSONArray();
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
                itens.put(newObj);
            }
        }
        obj.put(Contract.ITENS_DE_EMENTA, itens);
        
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
            nome = scanner.nextLine();
            if(nome.isEmpty()){
                System.out.println("\n\n Por favor digite um nome válido...\n\n");
            }
        }while(nome.isEmpty());
        
        String autor;
        System.out.println("\nDigite o nome do autor do livro:\n");
        do{
            autor = scanner.nextLine();
            if(autor.isEmpty()){
                System.out.println("\n\n Por favor digite um nome válido...\n\n");
            }
        }while(autor.isEmpty());
        
        String editora;
        
        System.out.println("\nDigite o nome da editora do livro:\n");
        do{
            editora = scanner.nextLine();
            if(editora.isEmpty()){
                System.out.println("\n\n Por favor digite um nome válido...\n\n");
            }
        }while(editora.isEmpty());
        
        String edicao;
        int numeroEdicao = -1;
        
        System.out.println("\nDigite a edicao do livro:\n");
        do{
            edicao = scanner.nextLine();
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
            nome = scanner.nextLine();
            if(nome.isEmpty()){
                System.out.println("\n\n Por favor digite um nome válido...\n\n");
            }
        }while(nome.isEmpty());
        
        String carga;
        int cargaHorari = -1;
        
        System.out.println("\nDigite a carga horária do item de ementa:\n");
        do{
            carga = scanner.nextLine();
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

