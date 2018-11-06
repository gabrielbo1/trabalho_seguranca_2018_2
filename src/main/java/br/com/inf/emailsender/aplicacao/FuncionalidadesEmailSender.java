package br.com.inf.emailsender.aplicacao;

import br.com.inf.emailsender.dominio.ErroEmailSender;
import br.com.inf.emailsender.dominio.Protocolo;
import br.com.inf.emailsender.dominio.criptografia.CriptografiaRSA;
import br.com.inf.emailsender.dominio.mensagem.ArquivoBase64;
import br.com.inf.emailsender.dominio.mensagem.Mensagem;

import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;

/**
 * @author gabriel
 * @author joao henrique
 * @version 1.0.0
 */
public class FuncionalidadesEmailSender {

    private static final String NOME_CHAVE_PUBLICA = "chave_publica.key";
    private static final String NOME_CHAVE_PRIVADA = "chave_privada.key";


    private static void print(String s) {
        System.out.print(s);
    }

    private static void println(String s) {
        System.out.println(s);
    }

    private static String entreComCaminhoPastas() throws Exception {
        String caminho = new Scanner(System.in).nextLine();
        if (caminho.isEmpty()) {
            throw new ErroEmailSender("Por favor entre com o caminho de pastas para gerar chaves.");
        }
        return caminho;
    }

    private static String entreComCaminhoArquivo() throws Exception {
        String caminho = new Scanner(System.in).nextLine();
        if (caminho.isEmpty()) {
            throw new ErroEmailSender("Por favor entre com o caminho do arquivo.");
        }
        if (!new File(caminho).exists()) {
            throw new ErroEmailSender("Arquivo não encontrado, entre com um caminho válido");
        }
        return caminho;
    }

    private static boolean criarChavesRSA(int numeroMenu) throws Exception {
        println("\t\t CRIAR CHAVES RSA - " + numeroMenu);
        print("\tEntre com o caminho da pasta:");
        String caminho = entreComCaminhoPastas();
        String caminhoChavePublica = caminho + File.separator + NOME_CHAVE_PUBLICA;
        String caminhoChavePrivada = caminho + File.separator + NOME_CHAVE_PRIVADA;

        CriptografiaRSA.geradorChaves(caminhoChavePublica, caminhoChavePrivada, 1024);
        return true;
    }

    private static boolean enviarMensagem(int numeroMenu) throws Exception {
        println("\t\t ENVIAR MENSAGEM - " + numeroMenu);

        print("\tEntre com o caminho da pasta: ");
        String caminhoPastaArquivosCriptografados = entreComCaminhoPastas();

        print("\tEntre com o caminho da sua Chave Privada: ");
        String caminhoChavePrivadaUsuarioCorrente = entreComCaminhoArquivo();

        print("\tEntre com o caminho da Chave Pública destinatário: ");
        String caminhoChavePublicaDestinatario = entreComCaminhoArquivo();

        print("\tEntre com a mensagem: ");
        Mensagem mensagem = new Mensagem(new Scanner(System.in).nextLine());

        List<ArquivoBase64> anexos = new ArrayList<>();

        int anexarArquivo = -1;
        while (anexarArquivo == -1) {
            print("\t Caso deseje anexar arquivo digite 1 senão 0");
            String opcao = new Scanner(System.in).nextLine();
            if (!opcao.equals("1")) {
                anexarArquivo = 0;
            }

            try {
                print("\t Digite o caminho do arquivo do anexo: ");
                String caminhoArquivo = entreComCaminhoArquivo();
                ArquivoBase64 arquivoBase64 = new ArquivoBase64(caminhoArquivo);
                anexos.add(arquivoBase64);
            } catch (ErroEmailSender e) {
                println(e.getMessage());
            }
        }

        if (!anexos.isEmpty()) {
            mensagem = new Mensagem(mensagem.getMensagem(), anexos);
        }

        Protocolo.gerarArquivosEnvioCriptografados(caminhoPastaArquivosCriptografados,
                mensagem, caminhoChavePrivadaUsuarioCorrente, caminhoChavePublicaDestinatario);
        return true;
    }

    private static boolean decodificarMensagem(int numeroMenu) throws Exception {
        println("\t\t DECODIFICAR MENSAGEM - " + numeroMenu);

        print("\t Entre com o caminho de arquivo do hash: ");
        String caminhoHash = entreComCaminhoArquivo();

        print("\t Entre com o caminho de arquivo da chave: ");
        String caminhoChave = entreComCaminhoArquivo();

        print("\t Entre com o caminho de arquivo da mensagem: ");
        String caminhoMensagem = entreComCaminhoArquivo();

        print("\t Entre com o caminho de pasta: ");
        String caminhoPasta = entreComCaminhoPastas();

        print("\t Entre com o caminho da Chave Privada: ");
        String caminhoChavePrivadaDestinatario = entreComCaminhoArquivo();

        print("\t Entre com o caminho da Chave Pública do Remetente: ");
        String caminhoChavePublicaRemetente = entreComCaminhoArquivo();

        Mensagem mensagem = Protocolo.lerArquivosRecebidosDescriptografar(caminhoHash, caminhoChave, caminhoMensagem,
                caminhoChavePrivadaDestinatario, caminhoChavePublicaRemetente);

        BufferedWriter buffWrite = new BufferedWriter(new FileWriter(caminhoPasta + File.separator + "mensagem.txt"));
        Scanner in = new Scanner(System.in);
        buffWrite.append(mensagem.getMensagem());
        buffWrite.close();

        if (mensagem.getAnexos() != null && !mensagem.getAnexos().isEmpty()) {
            String nomeAnexo = "arquivo";
            for (int i = 0; i < mensagem.getAnexos().size(); i++) {
                ArquivoBase64 arquivoBase64 = mensagem.getAnexos().get(i);
                String arquivo = caminhoPasta + File.separator + nomeAnexo + i + "." + arquivoBase64.getExtensao();
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(arquivo));
                bos.write(Base64.getDecoder().decode(arquivoBase64.getBase64()));
                bos.flush();
                bos.close();
            }
        }

        return true;
    }

    private final static void clearConsole() {
        try {
            final String os = System.getProperty("os.name");

            if (os.contains("Windows")) {
                Runtime.getRuntime().exec("cls");
            } else {
                Runtime.getRuntime().exec("clear");
            }
        } catch (final Exception e) {
            //  Handle any exceptions.
        }
    }

    public static void emailSenderMenu() {
        boolean aplicacao = true;
        boolean menu = true;
        String opcao = "";

        try {
            while (aplicacao) {
                if (menu) {
                    println("\t\t\t ---------------- MENU ----------------------");
                    println("\t CRIAR CHAVE RSA - 1");
                    println("\t ENVIAR MENSAGEM - 2");
                    println("\t DECODIFICAR MENSAGEM - 3");
                    println("\t SAIR - 4");
                    println("\t OPÇÃO: ");
                    opcao = new Scanner(System.in).nextLine();
                }

                switch (opcao) {
                    case "1":
                        criarChavesRSA(1);
                        break;
                    case "2":
                        enviarMensagem(2);
                        break;
                    case "3":
                        decodificarMensagem(3);
                        break;
                    case "4":
                        aplicacao = !aplicacao;
                        break;
                    default:
                        println("Entre com uma opção válida por favor");
                        break;
                }
                System.out.flush();
                clearConsole();
                menu = true;
            }
        } catch (ErroEmailSender err) {
          println(err.getMessage());
          menu = false;
        } catch (Exception exc) {
           println("ERRO JAVA");
           println(exc.getMessage());
           menu = true;
        }
    }
}
