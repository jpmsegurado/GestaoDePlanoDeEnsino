package gestaodeplanodeensino;

/**
 *
 * @author JoãoPedro
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import jxl.*;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author João Pedro
 */
public class Main {

    private static JSONArray listaDisciplinas = new JSONArray();// Array utilizado para apresentar as disciplinas

    public static void main(String[] args) {
        System.out.println("Seja bem vindo ao gerenciador de plano de ensino.\n");
        iniciar();
    }

    public static void iniciar() { // Método que se repete várias vezes durante a execução do programa, sendo utilizado como uma interface principal por linha de comando"

        listaDisciplinas = getAllDisciplinas(); //Pega todas as disciplinas que já estavam salvas anteriormente, fazendo uso de um array JSON.
        System.out.println("\nPor favor selecione uma opção:\n"
                + "1 - Cadastrar disciplina\n"
                + "2 - Listar disciplinas\n"
                + "3 - Remover disciplina\n"
                + "4 - Gerar plano de ensino\n"
                + "5 - SAIR\n");

        Scanner scanner = new Scanner(System.in);
        String comando;
        int numeroComando = -1;

        try { // Tenta transformar um inteiro numa string, caso não conseguir gera uma exceção!
            comando = scanner.nextLine();
            numeroComando = Integer.parseInt(comando);
        } catch (NumberFormatException e) {
            System.out.println("\n\nDigite um número válido!\n\n");
        }

        switch (numeroComando) {
            case 1:
                cadastrarDisciplina();
                break;
            case 2:
                listarDisciplinas(false);
                break;

            case 3:
                listarDisciplinas(true);
                break;
            case 4:
                gerarPlano();
                break;

            case 5:
                System.out.println("\n\nSaindo...\n\n");
                return;
            default:
                System.out.println("\n\nERRO: Por favor digite um comando válido...\n\n");
                iniciar();
                break;
        }

    }

    public static void cadastrarDisciplina() { // Cadastra uma disciplina com seus itens de ementa, fazendo a validação da carga horária dos itens com a carga horária total da disciplina

        
        System.out.println("\nCADASTRANDO DISCIPLINA: \n");
        
        System.out.println("\nID:\n");
        Scanner scanner = new Scanner(System.in);
        String id = "";

        do {

            //fazer uma verificação para ver se há ids iguais - URGENTE
            
            id = scanner.nextLine().toUpperCase();

            if (id.isEmpty()) {
                System.out.println("\n\nERRO: Por favor, digite uma ID válida!\n\n");
            }
        } while (id.isEmpty());

        System.out.println("\nNome da disciplina:\n");
        String nome = "";

        do {
            nome = scanner.nextLine();
            if (nome.isEmpty()) {
                System.out.println("\n\nERRO: Por favor, digite um nome válido!\n\n");
            }
        } while (nome.isEmpty());

        System.out.println("\nDescrição da disciplina:\n");
        String descricao = "";

        do {
            descricao = scanner.nextLine();
            if (descricao.isEmpty()) {
                System.out.println("\n\nERRO: Por favor, digite uma descrição válida!\n\n");
            }
        } while (descricao.isEmpty());

        System.out.println("\nCarga horária total da disciplina:\n");
        int cargaHoraria = -1;
        do {
            String carga = scanner.nextLine();
            try {
                cargaHoraria = Integer.parseInt(carga);
            } catch (NumberFormatException e) {
                System.out.println("\n\nERRO: Por favor, digite uma carga horária válida!\n\n");
            }
        } while (cargaHoraria <= 0);

        Disciplina disciplina = new Disciplina(id, nome, descricao, cargaHoraria); //Cria disciplina
        salvarDisciplina(disciplina);// chama o método  salva a disciplina, mesmo após o término da execução do programa.
        iniciar();

    }

    public static void adicionarItensDeEmenta(String idDisciplina) {
        int ordem = 0;
        
                    
            for (int i = 0; i < listaDisciplinas.length() + 1; i++) {

                String idDisciplina1 = (String) listaDisciplinas.getJSONObject(i).get(Contract.ID);

                if ((idDisciplina.equals(idDisciplina1))) {
                    ordem = i;
                    break;
                }
            }

       
        
        JSONObject disciplina = listaDisciplinas.getJSONObject(ordem);
        int cargaHoraria = disciplina.getInt(Contract.CARGA_HORARIA);
        int cargaTotal = 0;
        ArrayList<ItemDeEmenta> ementa; //Array que contém os itens de ementa de uma disciplina
        cargaTotal = 0;
        ementa = retornaItensDeEmenta(cargaHoraria);
        JSONArray arrayEmenta = new JSONArray();
        if (disciplina.optJSONArray(Contract.ITENS_DE_EMENTA) != null) {
            arrayEmenta = disciplina.getJSONArray(Contract.ITENS_DE_EMENTA);
        }
        for (int i = 0; i < ementa.size(); i++) {
            JSONObject newItem = new JSONObject();
            newItem.put(Contract.CARGA_HORARIA, ementa.get(i).getCargaHoraria());
            newItem.put(Contract.NOME, ementa.get(i).getNome());
            arrayEmenta.put(newItem);
        }
        disciplina.put(Contract.ITENS_DE_EMENTA, arrayEmenta);
        regravrarDisciplinas();
        imprimirDisciplina(idDisciplina);
    }

    public static void cadastrarBibliografia(String idDisciplina) {
 
        int ordem = 0;
        
     
            for (int i = 0; i < listaDisciplinas.length() + 1; i++) {

                String idDisciplina1 = (String) listaDisciplinas.getJSONObject(i).get(Contract.ID);

                if ((idDisciplina.equals(idDisciplina1))) {
                    ordem = i;
                    break;
                }
            }

       

        JSONObject disciplina = listaDisciplinas.getJSONObject(ordem);
        ArrayList<LivroDeReferencia> bibliografia = retornaBibliografia();
        JSONArray arrayLivros;

        if (disciplina.optJSONArray(Contract.BIBLIOGRAFIA) != null) {
            if (disciplina.getJSONArray(Contract.BIBLIOGRAFIA).length() > 0) {
                arrayLivros = disciplina.getJSONArray(Contract.BIBLIOGRAFIA);
            } else {
                arrayLivros = new JSONArray();
            }
        } else {
            arrayLivros = new JSONArray();
        }

        for (int i = 0; i < bibliografia.size(); i++) {
            JSONObject newObj = new JSONObject();
            newObj.put(Contract.NOME_LIVRO, bibliografia.get(i).getNome());
            newObj.put(Contract.NOME_AUTOR, bibliografia.get(i).getAutor());
            newObj.put(Contract.NOME_EDITORA, bibliografia.get(i).getEditora());
            newObj.put(Contract.EDICAO, bibliografia.get(i).getEdicao());
            arrayLivros.put(newObj);
        }

        disciplina.put(Contract.BIBLIOGRAFIA, arrayLivros);
        listaDisciplinas.put(ordem, disciplina);
        regravrarDisciplinas();
        imprimirDisciplina(idDisciplina);
    }

    public static void removerLivro(String idDisciplina) {

        int ordem =0;
        
       
            for (int i = 0; i < listaDisciplinas.length() + 1; i++) {

                String idDisciplina1 = (String) listaDisciplinas.getJSONObject(i).get(Contract.ID);

                if ((idDisciplina.equals(idDisciplina1))) {
                    ordem = i;
                    break;
                }
            }

        

        System.out.println("\nDigite o índice do livro:");
        Scanner scanner = new Scanner(System.in);
        int indiceLivro = -1;
        do {
            try {
                indiceLivro = Integer.valueOf(scanner.nextLine());
                if (indiceLivro <= 0) {
                    System.out.println("\nERRO: Por favor digite um valor válido\n");
                }
            } catch (NumberFormatException e) {
            }
        } while (indiceLivro <= 0);
        JSONObject disciplina = listaDisciplinas.getJSONObject(ordem);
        disciplina.getJSONArray(Contract.BIBLIOGRAFIA).remove(indiceLivro - 1);
        listaDisciplinas.put(ordem, disciplina);

        System.out.println("\nRemovido com sucesso.\n");
        regravrarDisciplinas();
        imprimirDisciplina(idDisciplina);
    }

    public static void listarDisciplinas(boolean canDelete) {
        if (listaDisciplinas.length() > 0) {
            StringBuilder text = new StringBuilder();
            text.append("\n---------------------------------------------------\n");
            for (int i = 0; i < listaDisciplinas.length(); i++) {
                text.append(i + 1).append("ª disciplina");
                text.append("\nID: ").append(listaDisciplinas.getJSONObject(i).get(Contract.ID));
                text.append("\nNome: ").append(listaDisciplinas.getJSONObject(i).get(Contract.NOME));
                text.append("\nCarga horária: ")
                        .append(listaDisciplinas.getJSONObject(i).get(Contract.CARGA_HORARIA))
                        .append("h");

                text.append("\nDescrição: ").append(listaDisciplinas.getJSONObject(i).get(Contract.DESCRICAO));

                text.append("\n---------------------------------------------------\n");
            }

            System.out.println(text.toString());
            if (!canDelete) {
                selecionarDisciplina();
            } else {
                deletarDisciplina();
            }
        } else {
            System.out.println("\n\nERRO: Nenhuma foi disciplina cadastrada! \n\n");
            iniciar();
        }

    }

    public static void deletarDisciplina() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Qual disciplina deseja deletar(digite o ID):");
        String idDisciplina = "";
        int ordem = 0;

        try {
            idDisciplina = scanner.nextLine().toUpperCase();
            for (int i = 0; i < listaDisciplinas.length() + 1; i++) {

                String idDisciplina1 = (String) listaDisciplinas.getJSONObject(i).get(Contract.ID);

                if ((idDisciplina.equals(idDisciplina1))) {
                    ordem = i;
                    break;
                }
            }

        } catch (JSONException e) {
            System.out.println("Tente novamente, ID não encontrado!");
        }
        listaDisciplinas.remove(ordem);
        System.out.println("\nDisciplina deletada com sucesso");
        regravrarDisciplinas();
        iniciar();
    }

    ;
    
    public static void selecionarDisciplina() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Digite o ID da disciplina que deseja selecionar:");
        String idDisciplina = "";

        try {
            idDisciplina = scanner.nextLine().toUpperCase();
            int ordem = 0;
            for (int i = 0; i < listaDisciplinas.length() + 1; i++) {

                String idDisciplina1 = (String) listaDisciplinas.getJSONObject(i).get(Contract.ID);

                if ((idDisciplina.equals(idDisciplina1))) {
                    ordem = i;
                    imprimirDisciplina(idDisciplina);
                    break;
                }
            }

        } catch (JSONException e) {
            System.out.println("\nTente novamente, ID não encontrado!\n");
            selecionarDisciplina();
        }

    }

    public static void imprimirDisciplina(String idDisciplina) {

       int ordem =0;
        
            for (int i = 0; i < listaDisciplinas.length() + 1; i++) {

                String idDisciplina1 = (String) listaDisciplinas.getJSONObject(i).get(Contract.ID);

                if ((idDisciplina.equals(idDisciplina1))) {
                    ordem = i;
                    break;
                }
            }

        
        JSONObject disciplina = listaDisciplinas.optJSONObject(ordem);
        JSONArray itensDeEmenta = disciplina.optJSONArray(Contract.ITENS_DE_EMENTA);
        JSONArray bibliografia = disciplina.optJSONArray(Contract.BIBLIOGRAFIA);

        StringBuilder text = new StringBuilder();

        text.append("\n\nID da disciplina: ").append(disciplina.get(Contract.ID));
        text.append("\n\nNome da disciplina: ").append(disciplina.get(Contract.NOME));
        text.append("\nCarga horária da disciplina: ")
                .append(disciplina.get(Contract.CARGA_HORARIA))
                .append("h\n");
        if (itensDeEmenta != null) {
            if (itensDeEmenta.length() > 0) {
                text.append("\n\nItens de ementa:");
                for (int i = 0; i < itensDeEmenta.length(); i++) {
                    text.append("\n---------------------------------------------------");
                    text.append("\nItem de ementa ").append(i + 1);
                    text.append("\nNome do item: ").append(itensDeEmenta.getJSONObject(i).get(Contract.NOME));
                    text.append("\nCarga horária: ")
                            .append(itensDeEmenta.getJSONObject(i).get(Contract.CARGA_HORARIA))
                            .append("h");
                    if (i == itensDeEmenta.length() - 1) {
                        text.append("\n---------------------------------------------------\n");
                    }
                }
            }
        }

        if (bibliografia != null) {
            if (bibliografia.length() > 0) {
                text.append("\n\nBibliografia:");
                for (int i = 0; i < bibliografia.length(); i++) {
                    text.append("\n---------------------------------------------------");
                    text.append("\nLivro ").append(i + 1);
                    text.append("\nNome do livro: ").append(bibliografia.getJSONObject(i).get(Contract.NOME_LIVRO));
                    text.append("\nNome do autor: ").append(bibliografia.getJSONObject(i).get(Contract.NOME_AUTOR));
                    text.append("\nNome da editora: ").append(bibliografia.getJSONObject(i).get(Contract.NOME_EDITORA));
                    text.append("\nEdição: ").append(bibliografia.getJSONObject(i).get(Contract.EDICAO)).append("ª edição");

                    if (i == bibliografia.length() - 1) {
                        text.append("\n---------------------------------------------------\n");
                    }
                }
            }
        }

        System.out.println(text.toString());

        Scanner scanner = new Scanner(System.in);
        int comando = -1;
        System.out.println("Selecione uma opção:\n"
                + "1 - editar item de ementa\n"
                + "2 - deletar item de ementa\n"
                + "3 - adicionar itens de ementa\n"
                + "4 - editar informações desta disciplina\n"
                + "5 - adicionar livro de bibliografia\n"
                + "6 - remover livro de bibliografia\n"
                + "7 - voltar pro inicio\n");
        do {
            try {
                comando = Integer.valueOf(scanner.nextLine());
            } catch (NumberFormatException e) {
            }
            if (comando <= 0 || comando > 7) {
                System.out.println("ERRO: Por favor digite um comando válido.");
            }
        } while (comando <= 0 || comando > 7);

        switch (comando) {
            case 1:
                selecionarItemEmentaEditado(idDisciplina);
                break;
            case 2:
                selecionarItemADeletar(idDisciplina);
                break;
            case 3:
                adicionarItensDeEmenta(idDisciplina);
                break;
            case 4:
                editarDisciplina(idDisciplina);
                break;
            case 5:
                cadastrarBibliografia(idDisciplina);
                break;
            case 6:
                removerLivro(idDisciplina);
                break;
            case 7:
                iniciar();
                break;
            default:
                iniciar();
                break;
        }
    }

    ;
    
    public static void selecionarItemADeletar(String idDisciplina) {

      int ordem =0;
        
      
            for (int i = 0; i < listaDisciplinas.length() + 1; i++) {

                String idDisciplina1 = (String) listaDisciplinas.getJSONObject(i).get(Contract.ID);

                if ((idDisciplina.equals(idDisciplina1))) {
                    ordem = i;
                    break;
                }
            }

        

        System.out.println("\nDigite a ordem do item que deseja deletar:\n");

        Scanner scanner = new Scanner(System.in);
        do {
            try {
                ordem = Integer.valueOf(scanner.nextLine());
            } catch (NumberFormatException e) {
            };
            if (ordem <= 0) {
                System.out.println("\nERRO: Por favor, digite um valor válido!\n");
            }
        } while (ordem <= 0);
        JSONObject novaDisciplina = listaDisciplinas.getJSONObject(ordem);
        novaDisciplina.getJSONArray(Contract.ITENS_DE_EMENTA).remove(ordem - 1);
        listaDisciplinas.put(ordem, novaDisciplina);
        regravrarDisciplinas();
        System.out.println("\nItem removido com sucesso.\n");
        iniciar();
    }

    public static void editarDisciplina(String idDisciplina) {
       
       int ordem =0;
        
     
            for (int i = 0; i < listaDisciplinas.length() + 1; i++) {

                String idDisciplina1 = (String) listaDisciplinas.getJSONObject(i).get(Contract.ID);

                if ((idDisciplina.equals(idDisciplina1))) {
                    ordem = i;
                    break;
                }
            }

       

        Scanner scanner = new Scanner(System.in);
        JSONObject disciplina = listaDisciplinas.getJSONObject(ordem);

        System.out.println("\nDigite o novo ID da disciplina: \n");
        String id = null;

        do {
            id = scanner.nextLine().toUpperCase();
            if (id.isEmpty() || id.matches(idDisciplina)) {
                System.out.println("\nERRO: Por favor, digite uma ID válida!\n");
            }
        } while (id.isEmpty());

        System.out.println("\nDigite o novo nome da disciplina:\n");
        String nome = null;
        do {
            nome = scanner.nextLine();
            if (nome.isEmpty()) {
                System.out.println("\nERRO: Por favor, digite um nome válido!\n");
            }
        } while (nome.isEmpty());

        System.out.println("\nDigite a nova descrição:\n");
        String descricao = null;
        do {
            descricao = scanner.nextLine();
            if (descricao.isEmpty()) {
                System.out.println("\nERRO: Por favor, digite uma descrição válida!\n");
            }
        } while (descricao.isEmpty());

        System.out.println("\nDigite a nova carga horária:\n");
        int carga = -1;

        do {
            try {
                carga = Integer.valueOf(scanner.nextLine());
            } catch (NumberFormatException e) {
            };
            if (carga <= 0) {
                System.out.println("\nERRO: Por favor, digite um valor válido!\n");
            }
        } while (carga <= 0);

        disciplina.put(Contract.ID, id);
        disciplina.put(Contract.NOME, nome);
        disciplina.put(Contract.DESCRICAO, descricao);
        disciplina.put(Contract.CARGA_HORARIA, carga);

        listaDisciplinas.put(ordem, disciplina);
        System.out.println("\nAtualizado com sucesso\n");
        regravrarDisciplinas();
        iniciar();
    }

    public static void selecionarItemEmentaEditado(String idDisciplina) {

        int ordem =0;
        
            for (int i = 0; i < listaDisciplinas.length() + 1; i++) {

                String idDisciplina1 = (String) listaDisciplinas.getJSONObject(i).get(Contract.ID);

                if ((idDisciplina.equals(idDisciplina1))) {
                    ordem = i;
                    break;
                }
            }

       

        JSONObject disciplina = listaDisciplinas.getJSONObject(ordem);
        JSONArray itensDeEmenta = disciplina.getJSONArray(Contract.ITENS_DE_EMENTA);
        
        StringBuilder text = new StringBuilder();

        System.out.println(text.toString());
        int comando = -1;
        System.out.println("\nPor favor, digite o número de qual item de ementa deseja editar:");
        do {
            Scanner scanner = new Scanner(System.in);
            try {
                comando = Integer.valueOf(scanner.nextLine());
            } catch (NumberFormatException e) {
            };

            if (comando <= 0 || comando > disciplina.getJSONArray(Contract.ITENS_DE_EMENTA).length()) {
                System.out.println("\nERRO: Por favor, digite um valor válido!\n");
            }

        } while (comando <= 0 || comando > disciplina.getJSONArray(Contract.ITENS_DE_EMENTA).length());

        editarItemDeEmenta(idDisciplina, comando - 1);
    }

    public static void editarItemDeEmenta(String idDisciplina, int indiceItemEmenta) {

       int ordem =0;
      
            for (int i = 0; i < listaDisciplinas.length() + 1; i++) {

                String idDisciplina1 = (String) listaDisciplinas.getJSONObject(i).get(Contract.ID);

                if ((idDisciplina.equals(idDisciplina1))) {
                    ordem = i;
                    break;
                }
            }

       
       
        JSONObject disciplina = listaDisciplinas.getJSONObject(ordem);
        JSONObject itemEmenta = disciplina.getJSONArray(Contract.ITENS_DE_EMENTA).getJSONObject(indiceItemEmenta);

        System.out.println("\nDigite o novo nome do item de ementa:\n");
        Scanner scanner = new Scanner(System.in);
        String nome = null;
        do {
            nome = scanner.nextLine();
            if (nome.isEmpty()) {
                System.out.println("\nERRO: Por favor, digite um nome válido");
            }
        } while (nome.isEmpty());

        int carga = -1;
        System.out.println("\nDigite a nova carga horária do item de ementa:\n");
        do {
            carga = Integer.valueOf(scanner.nextLine());
            System.out.println("\nERRO: Por favor, digite um valor válido.");
        } while (carga <= 0);

        itemEmenta.put(Contract.CARGA_HORARIA, carga);
        itemEmenta.put(Contract.NOME, nome);

        disciplina.getJSONArray(Contract.ITENS_DE_EMENTA).put(indiceItemEmenta, itemEmenta);
        listaDisciplinas.put(ordem, disciplina);
        regravrarDisciplinas();
        System.out.println("\nAlterado com sucesso\n");
        iniciar();
    }

    public static void regravrarDisciplinas() {
        try {
            PrintWriter out = new PrintWriter("filename.txt");
            out.println(listaDisciplinas.toString());
            out.close();
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static JSONArray getAllDisciplinas() {
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

            if (json.length() <= 10) {
                return new JSONArray();
            } else {
                return new JSONArray(json);
            }

        } catch (FileNotFoundException ex) {
            iniciarArquivo();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        return new JSONArray();
    }

    public static void salvarDisciplina(Disciplina d) {
        JSONObject obj = new JSONObject();

        //System.out.println(d.getId());
        obj.put(Contract.ID, d.getId());
        obj.put(Contract.CARGA_HORARIA, d.getCargaHoraria());
        obj.put(Contract.DESCRICAO, d.getDescricao());
        obj.put(Contract.NOME, d.getNome());

        //JSONArray itens = new JSONArray();
        /*if (d.getItens().size() > 0) {
         for (int i = 0; i < d.getItens().size(); i++) {
         JSONObject newObj = new JSONObject();
         ItemDeEmenta item = d.getItens().get(i);
         newObj.put(Contract.NOME, item.getNome());
         newObj.put(Contract.CARGA_HORARIA, item.getCargaHoraria());
         itens.put(newObj);
         }
         }

         JSONArray livros = new JSONArray();
        
         if (d.getBibliografia().size() > 0) {
            
         for(int i = 0; i<d.getBibliografia().size(); i++){
         JSONObject newObj = new JSONObject();
         LivroDeReferencia livro = d.getBibliografia().get(i);
         newObj.put(Contract.NOME_LIVRO, livro.getNome() );
         newObj.put (Contract.NOME_AUTOR, livro.getAutor());
         newObj.put(Contract.NOME_EDITORA, livro.getEditora());
         newObj.put(Contract.EDICAO, livro.getEdicao());
         livros.put(newObj);
         }
                    
         }
         obj.put(Contract.ITENS_DE_EMENTA, itens);
         obj.put(Contract.BIBLIOGRAFIA, livros);
         */
        listaDisciplinas.put(obj);

        try {
            PrintWriter out = new PrintWriter("filename.txt");
            out.println(listaDisciplinas.toString());
            out.close();
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        }

    }

    public static void iniciarArquivo() {
        try {
            PrintWriter out = new PrintWriter("filename.txt");
            out.close();
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static ArrayList<ItemDeEmenta> retornaItensDeEmenta(int cargaHoraria) {
        System.out.println("\nCADASTRANDO ITENS DE EMENTA: \n");
        ArrayList<ItemDeEmenta> ementa = new ArrayList<>();
        int comando = -1;
        Scanner scanner = new Scanner(System.in);
        do {
            ItemDeEmenta itemNovo = cadastrarItemDeEmenta();
            ementa.add(itemNovo);

            System.out.println("\nDeseja cadastrar mais itens?\n"
                    + "1 - sim\n"
                    + "2 - não\n");
            comando = scanner.nextInt();

        } while (comando == 1);

        return ementa;
    }

    public static ArrayList<LivroDeReferencia> retornaBibliografia() {
        System.out.println("\nCADASTRANDO A BIBLIOGRAFIA:\n");
        ArrayList<LivroDeReferencia> biblbiografia = new ArrayList<>();
        int comando = -1;
        Scanner scanner = new Scanner(System.in);
        do {
            LivroDeReferencia itemNovo = cadastrarLivro();
            biblbiografia.add(itemNovo);

            System.out.println("\nDeseja cadastrar mais itens?\n"
                    + "1 - sim\n"
                    + "2 - não\n");
            comando = scanner.nextInt();

        } while (comando == 1);

        return biblbiografia;
    }

    public static void gerarPlano() {

        int comando = -1;
        Scanner scanner = new Scanner(System.in);
        ArrayList<Integer> dias = new ArrayList<>();
        ArrayList<Integer> carga = new ArrayList<>();
        do {
            dias.add(retornaDiaDisciplina(dias));
            carga.add(retornaHorasAula());
            System.out.println("\nDeseja adicionar mais dias?\n1 - sim\n2 - não");
            try {
                comando = Integer.valueOf(scanner.nextLine());
            } catch (NumberFormatException e) {
            };
        } while (comando == 1);

        ArrayList<String> listaDias = new ArrayList<>();

        for (int i = 0; i < dias.size(); i++) {
            int cargaDoDia = carga.get(i);

            for (int j = 1; j <= cargaDoDia; j++) {
                switch (dias.get(i)) {
                    case 1:
                        listaDias.add("segunda " + j + "ª hora");
                        break;
                    case 2:
                        listaDias.add("terça " + j + "ª hora");
                        break;
                    case 3:
                        listaDias.add("quarta " + j + "ª hora");
                        break;
                    case 4:
                        listaDias.add("quinta " + j + "ª hora");
                        break;
                    case 5:
                        listaDias.add("sexta " + j + "ª hora");
                        break;
                    case 6:
                        listaDias.add("sábado " + j + "ª hora");
                        break;
                    case 7:
                        listaDias.add("domingo " + j + "ª hora");
                        break;
                    default:
                        break;
                }

            }
        }

        JSONObject disciplina = listaDisciplinas.getJSONObject(0);

        ArrayList<String> planilhaHeader = new ArrayList<>();
        ArrayList<String> planilhaContent = new ArrayList<>();

        for (int j = 0; j < disciplina.getInt(Contract.CARGA_HORARIA); j++) {
            planilhaHeader.add(listaDias.get(j % listaDias.size()));
        }
        for (int x = 0; x < disciplina.getJSONArray(Contract.ITENS_DE_EMENTA).length(); x++) {
            JSONObject item = disciplina.getJSONArray(Contract.ITENS_DE_EMENTA).getJSONObject(x);
            for (int k = 0; k < item.getInt(Contract.CARGA_HORARIA); k++) {
                planilhaContent.add(item.getString(Contract.NOME));
            }
        }

        try {
            WritableWorkbook workbook;
            workbook = Workbook.createWorkbook(new File("Plano_de_ensino.xls"));
            WritableSheet sheet = workbook.createSheet("First Sheet", 0);
            int cont;
            Label title = new Label(0, 0, "Bibliografia");
            sheet.addCell(title);
            for (cont = 0; cont < disciplina.getJSONArray(Contract.BIBLIOGRAFIA).length(); cont++) {
                JSONObject livro = disciplina.getJSONArray(Contract.BIBLIOGRAFIA).getJSONObject(cont);
                Label label = new Label(0, cont + 1, "Livro " + cont + 1);
                Label label2 = new Label(2, cont + 1, livro.getString(Contract.NOME_LIVRO) + "/" + livro.getString(Contract.NOME_AUTOR));
                sheet.addCell(label);
                sheet.addCell(label2);
            }
            for (int i = 0; i < planilhaHeader.size(); i++) {
                if (i == 0) {
                    Label title2 = new Label(0, i + cont + 3, "Itens de ementa");
                    sheet.addCell(title2);
                }
                Label label = new Label(0, i + cont + 4, planilhaHeader.get(i));
                Label label2 = new Label(2, i + cont + 4, planilhaContent.get(i));
                sheet.addCell(label);
                sheet.addCell(label2);
            }

            workbook.write();
            workbook.close();
        } catch (IOException | WriteException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

        iniciar();

    }

    public static int retornaDiaDisciplina(ArrayList<Integer> dias) {
        System.out.println("\nSelecione um dia para adicionar:");
        for (int i = 1; i < Contract.DIAS_SEMANA.length + 1; i++) {
            if (!dias.contains(i)) {
                System.out.println(i + " - " + Contract.DIAS_SEMANA[i - 1]);
            }
        }
        Scanner scanner = new Scanner(System.in);
        int dia = -1;
        do {
            try {
                dia = Integer.valueOf(scanner.nextLine());
                if (dias.contains(dia)) {
                    System.out.println("\nDia já cadastrado\n");
                } else if (dia <= 0) {
                    System.out.println("\nPor favor digite um valor válido.\n");
                }
            } catch (NumberFormatException e) {
            }
        } while (dia <= 0 || dias.contains(dia));

        return dia;
    }

    ;
    
    public static int retornaHorasAula() {
        System.out.println("\nDigite a quantidade de horas-aula desse dia:");
        Scanner scanner = new Scanner(System.in);
        int horas = -1;
        do {
            try {
                horas = Integer.valueOf(scanner.nextLine());
                if (horas <= 0) {
                    System.out.println("\nPor favor digite um valor válido.\n");
                }
            } catch (NumberFormatException e) {
            }
        } while (horas <= 0);

        return horas;
    }

    public static LivroDeReferencia cadastrarLivro() {
        System.out.println("\nDigite o nome do livro\n");
        Scanner scanner = new Scanner(System.in);
        String nome = null;

        do {
            nome = scanner.nextLine();
            if (nome.isEmpty()) {
                System.out.println("\n\n ERRO: Por favor, digite um nome válido: \n\n");
            }
        } while (nome.isEmpty());

        String autor;
        System.out.println("\nDigite o nome do autor do livro:\n");
        do {
            autor = scanner.nextLine();
            if (autor.isEmpty()) {
                System.out.println("\n\nERRO: Por favor, digite um nome válido.\n\n");
            }
        } while (autor.isEmpty());

        String editora;

        System.out.println("\nDigite o nome da editora do livro:\n");
        do {
            editora = scanner.nextLine();
            if (editora.isEmpty()) {
                System.out.println("\n\nERRO: Por favor, digite um nome válido.\n\n");
            }
        } while (editora.isEmpty());

        String edicao;
        int numeroEdicao = -1;

        System.out.println("\nDigite a edicao do livro:\n");
        do {
            edicao = scanner.nextLine();
            try {
                numeroEdicao = Integer.parseInt(edicao);
            } catch (NumberFormatException e) {
                System.out.println("\n\nERRO: Por favor, digite uma carga horária válida...\n\n");
            }
        } while (numeroEdicao <= 0);

        LivroDeReferencia livro = new LivroDeReferencia(nome, editora, editora, numeroEdicao);
        return livro;

    }

    public static ItemDeEmenta cadastrarItemDeEmenta() {
        System.out.println("\nDigite o nome do item de ementa:\n");
        Scanner scanner = new Scanner(System.in);
        String nome = null;

        do {
            nome = scanner.nextLine();
            if (nome.isEmpty()) {
                System.out.println("\n\n ERRO: Por favor, digite um nome válido...\n\n");
            }
        } while (nome.isEmpty());

        String carga;
        int cargaHorari = -1;

        System.out.println("\nDigite a carga horária do item de ementa:\n");
        do {
            carga = scanner.nextLine();
            try {
                cargaHorari = Integer.parseInt(carga);
            } catch (NumberFormatException e) {
                System.out.println("\n\nERRO: Por favor, digite uma carga horária válida...\n\n");
            }
        } while (cargaHorari <= 0);

        ItemDeEmenta item = new ItemDeEmenta(nome, cargaHorari);
        return item;

    }

}
