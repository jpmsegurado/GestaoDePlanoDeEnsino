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

    private static JSONArray listaDisciplinas = new JSONArray(); // Array utilizado para apresentar as disciplinas

    public static void main(String[] args) {
        System.out.println("Seja bem vindo ao gerenciador de plano de ensino.\n");
        iniciar();
    }

    public static void iniciar() { // Método que se repete várias vezes durante a execução do programa, sendo utilizado como uma interface principal por linha de comando"

        listaDisciplinas = getAllDisciplinas(); //Pega todas as disciplinas que já estavam salvas anteriormente, fazendo uso de um array JSON.
        System.out.println("\nPor favor selecione uma opção:\n"
                + "1 - Cadastrar disciplina\n"
                + "2 - Listar disciplinas\n"
                + "3 - Editar disciplina\n"
                + "4 - Remover disciplina\n"
                + "5 - Gerar plano de ensino\n"
                + "6 - SAIR\n");

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

            case 4:
                listarDisciplinas(true);
                break;
            case 6:
                System.out.println("\n\nSaindo...\n\n");
                return;
            default:
                System.out.println("\n\nERRO: Por favor digite um comando válido...\n\n");
                iniciar();
                break;
        }

    }

    public static void cadastrarDisciplina() { // Cadastra uma disciplina com seus itens de ementa, fazendo a validação da carga horária dos itens com a carga horária total da disciplina
        System.out.println("\nNome da disciplina:\n");
        Scanner scanner = new Scanner(System.in);
        String nome = scanner.nextLine();

        while (nome.isEmpty()) {
            System.out.println("\n\nERRO: Por favor, digite um nome válido!\n\n");
            nome = scanner.nextLine();
        }

        System.out.println("\nDescrição da disciplina:\n");
        String descricao = scanner.nextLine();

        while (descricao.isEmpty()) {
            System.out.println("\n\nERRO: Por favor, digite uma descrição válida!\n\n");
            descricao = scanner.nextLine();
        }

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

        

        Disciplina disciplina = new Disciplina(nome, descricao, cargaHoraria, null, null); //Cria disciplina
        salvarDisciplina(disciplina);// chama o método que salva a disciplina, mesmo após o término da execução do programa.
        iniciar();

    }
    
    public static void adicionarItensDeEmenta(int indiceDisciplina){
        JSONObject disciplina = listaDisciplinas.getJSONObject(indiceDisciplina);
        int cargaHoraria = disciplina.getInt(Contract.CARGA_HORARIA);
        int cargaTotal = 0;
        ArrayList<ItemDeEmenta> ementa = new ArrayList<>(); //Array que contém os itens de ementa de uma disciplina

        cargaTotal = 0;
        ementa = retornaItensDeEmenta(cargaHoraria);
        JSONArray arrayEmenta = new JSONArray();
        for(int i=0; i<ementa.size();i++){
            JSONObject newItem = new JSONObject();
            newItem.put(Contract.CARGA_HORARIA, ementa.get(i).getCargaHoraria());
            newItem.put(Contract.NOME, ementa.get(i).getNome());
            arrayEmenta.put(newItem);
        }
        disciplina.put(Contract.ITENS_DE_EMENTA, arrayEmenta);
        regravrarDisciplinas();
        imprimirDisciplina(indiceDisciplina);
    }
    
    public static void cadastrarBibliografia(int indiceDisciplina){
        JSONObject disciplina = listaDisciplinas.getJSONObject(indiceDisciplina);        
        ArrayList<LivroDeReferencia> bibliografia = retornaBibliografia();
        JSONArray arrayLivros;
        
        if(disciplina.getJSONArray(Contract.BIBLIOGRAFIA) != null){
            if(disciplina.getJSONArray(Contract.BIBLIOGRAFIA).length() > 0){
                arrayLivros = disciplina.getJSONArray(Contract.BIBLIOGRAFIA);
            }else{
                arrayLivros = new JSONArray();
            }
        }else{
            arrayLivros = new JSONArray();
        }
        
        for(int i=0; i<bibliografia.size();i++){
            JSONObject newObj = new JSONObject();
            newObj.put(Contract.NOME_LIVRO,bibliografia.get(i).getNome());
            newObj.put(Contract.NOME_AUTOR,bibliografia.get(i).getAutor());
            newObj.put(Contract.NOME_EDITORA,bibliografia.get(i).getEditora());
            newObj.put(Contract.EDICAO,bibliografia.get(i).getEdicao());
            arrayLivros.put(newObj);
        }
        
        disciplina.put(Contract.BIBLIOGRAFIA, arrayLivros);
        listaDisciplinas.put(indiceDisciplina,disciplina);
        regravrarDisciplinas();
        imprimirDisciplina(indiceDisciplina);
    }

    public static void removerLivro(int indiceDisciplina){
        System.out.println("\nDigite o índice do livro:");
        Scanner scanner = new Scanner(System.in);
        int indiceLivro = -1;
        do{
            try {
                indiceLivro = Integer.valueOf(scanner.next());
                if(indiceLivro <= 0){
                    System.out.println("\nERRO: Por favor digite um valor válido\n");
                }
            } catch (NumberFormatException e) {}
        }while(indiceLivro <= 0);
        JSONObject disciplina = listaDisciplinas.getJSONObject(indiceDisciplina);
        disciplina.getJSONArray(Contract.BIBLIOGRAFIA).remove(indiceLivro - 1);
        listaDisciplinas.put(indiceDisciplina, disciplina);
        
        System.out.println("\nRemovido com sucesso.\n");
        regravrarDisciplinas();
        imprimirDisciplina(indiceDisciplina);
    }
    public static void listarDisciplinas(boolean canDelete) {
        if (listaDisciplinas.length() > 0) {
            StringBuilder text = new StringBuilder();
            text.append("\n---------------------------------------------------\n");
            for (int i = 0; i < listaDisciplinas.length(); i++) {
                text.append(i + 1).append("ª disciplina");
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
        System.out.println("Qual disciplina deseja deletar(digite o número da ordem):");
        int ordem = -1;
        do {
            try {
                ordem = Integer.valueOf(scanner.nextLine());
            } catch (NumberFormatException e) {
            }
            if (ordem <= 0 || ordem > listaDisciplinas.length()) {
                System.out.println("\nERRO: Por favor, digite um valor válido\n");
            }
        } while (ordem <= 0 || ordem > listaDisciplinas.length());
        listaDisciplinas.remove(ordem - 1);
        regravrarDisciplinas();
        iniciar();
    }

    ;
    
    public static void selecionarDisciplina() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Qual disciplina deseja selecionar(digite o número da ordem):");
        int ordem = -1;
        do {
            try {
                ordem = Integer.valueOf(scanner.nextLine());
            } catch (NumberFormatException e) {
            }
            if (ordem <= 0 || ordem > listaDisciplinas.length()) {
                System.out.println("\nERRO: Por favor, digite um valor válido\n");
            }
        } while (ordem <= 0 || ordem > listaDisciplinas.length());
        imprimirDisciplina(ordem - 1);
    }

    ;
    
    public static void imprimirDisciplina(int ordem) {
        JSONObject disciplina = listaDisciplinas.optJSONObject(ordem);
        JSONArray itensDeEmenta = disciplina.optJSONArray(Contract.ITENS_DE_EMENTA);
        JSONArray bibliografia = disciplina.optJSONArray(Contract.BIBLIOGRAFIA);
        StringBuilder text = new StringBuilder();
        text.append("\n\nNome da disciplina: ").append(disciplina.get(Contract.NOME));
        text.append("\nCarga horária da disciplina: ")
                .append(disciplina.get(Contract.CARGA_HORARIA))
                .append("h\n");
        if(itensDeEmenta != null){
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
        
        if(bibliografia != null){
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
                selecionarItemEmentaEditado(ordem);
                break;
            case 2:
                selecionarItemADeletar(ordem);
                break;
            case 3:
                adicionarItensDeEmenta(ordem);
                break;
            case 4:
                editarDisciplina(ordem);
                break;
            case 5:
                cadastrarBibliografia(ordem);
                break;
            case 6:
                removerLivro(ordem);
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
    
    public static void selecionarItemADeletar(int indiceDisciplina) {
        System.out.println("\nDigite a ordem do item que deseja deletar:\n");
        int ordem = -1;
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
        JSONObject novaDisciplina = listaDisciplinas.getJSONObject(indiceDisciplina);
        novaDisciplina.getJSONArray(Contract.ITENS_DE_EMENTA).remove(ordem - 1);
        listaDisciplinas.put(indiceDisciplina, novaDisciplina);
        regravrarDisciplinas();
        System.out.println("\nItem removido com sucesso.\n");
        iniciar();
    }

    public static void editarDisciplina(int indiceDisciplina) {
        Scanner scanner = new Scanner(System.in);
        JSONObject disciplina = listaDisciplinas.getJSONObject(indiceDisciplina);
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

        disciplina.put(Contract.NOME, nome);
        disciplina.put(Contract.DESCRICAO, descricao);
        disciplina.put(Contract.CARGA_HORARIA, carga);

        listaDisciplinas.put(indiceDisciplina, disciplina);
        System.out.println("\nAtualizado com sucesso\n");
        regravrarDisciplinas();
        iniciar();
    }

    public static void selecionarItemEmentaEditado(int indiceDisciplina) {
        JSONObject disciplina = listaDisciplinas.getJSONObject(indiceDisciplina);
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

        editarItemDeEmenta(indiceDisciplina, comando - 1);
    }

    public static void editarItemDeEmenta(int indiceDisciplina, int indiceItemEmenta) {
        JSONObject disciplina = listaDisciplinas.getJSONObject(indiceDisciplina);
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
        listaDisciplinas.put(indiceDisciplina, disciplina);
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

        obj.put("nome", d.getNome());
        obj.put(Contract.CARGA_HORARIA, d.getCargaHoraria());
        obj.put(Contract.DESCRICAO, d.getDescricao());
        obj.put(Contract.NOME, d.getNome());
        JSONArray itens = new JSONArray();
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
