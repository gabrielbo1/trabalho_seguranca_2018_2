package br.com.inf.emailsender.dominio;

import br.com.inf.emailsender.dominio.criptografia.CriptografiaRSA;
import br.com.inf.emailsender.dominio.criptografia.CriptograficaAES;
import br.com.inf.emailsender.dominio.mensagem.CredencialAES;
import br.com.inf.emailsender.dominio.mensagem.Mensagem;

import java.io.*;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

/**
 * @author gabriel
 * @author joao henrique
 * @version 1.0.0
 * <p>
 * Metódos para geração de arquivos de envio
 * de mensagens e leitura de mensagens.
 */
public final class Protocolo {

    private Protocolo() {
    }

    /**
     * @param caminhoPasta        - Caminho de pasta onde o usuário deseja
     *                            gerar os arquivos para envio da mensagem.
     * @param mensagem            - Objeto que será serializado em arquivo para envio
     *                            após criptografia.
     * @param caminhoChavePrivada - Caminho da chave privada do usuário
     *                            corrente para a criptografia do hash
     *                            da mensagem.
     * @param caminhoChavePublica - Caminho da chave pública do usuário
     *                            a quem se destina mensagem para criptografia
     *                            das credenciais do algoritmo AES.
     * @throws Exception
     */
    public static void gerarArquivosEnvioCriptografados(String caminhoPasta, Mensagem mensagem,
                                          String caminhoChavePrivada, String caminhoChavePublica) throws Exception {
        String caminhoArquivoMensagem = caminhoPasta + File.separator + "msg.arq";
        String caminhoArquivoHash = caminhoPasta + File.separator + "hash.arq";
        String caminhoArquivoChave = caminhoPasta + File.separator + "key.arq";

        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        sha256.update(converteObjetoParaBytes(mensagem));
        String hashBase64 = Base64.getEncoder().encodeToString(sha256.digest());
        PrivateKey chavePrivadaUsuarioCorrente = lerObjetoArquivo(caminhoChavePrivada);
        gravarObjetoArquivo(caminhoArquivoHash, CriptografiaRSA.criptografar(chavePrivadaUsuarioCorrente, hashBase64));

        CredencialAES credencialAES = new CredencialAES(CriptograficaAES.gerarVetorBytesAleatorios(16), CriptograficaAES.gerarVetorBytesAleatorios(16));
        String credencialAESBase64 = Base64.getEncoder().encodeToString(converteObjetoParaBytes(credencialAES));
        PublicKey chavePublicaDestinatario = lerObjetoArquivo(caminhoChavePublica);
        gravarObjetoArquivo(caminhoArquivoChave, CriptografiaRSA.criptografar(chavePublicaDestinatario, credencialAESBase64));


        gravarObjetoArquivo(caminhoArquivoMensagem, CriptograficaAES.criptografar(
                credencialAES.getChave(), credencialAES.getVetorDeInicializacao(),
                Base64.getEncoder().encodeToString(converteObjetoParaBytes(mensagem))));
    }

    /**
     *
     * @param caminhoArquivoHash - Caminho do arquivo de hash criptografado
     *                           gerado no envio da mensagem. (hash.arq)
     * @param caminhoArquivoChave - Caminho da chave AES usada para criptografar a mensagem. (key.arq)
     * @param caminhoArquivoMensagem - Caminho da mensagem em si. (msg.arq)
     * @param caminhoChavePrivada - Caminho da chave privada do usuário corrente.
     * @param caminhoChavePublica - Caminho da chave pública do remente.
     * @return Intância do objeto mensagem com o conteúdo da mensagem.
     * @throws Exception
     */
    public static Mensagem lerArquivosRecebidosDescriptografar(
            String caminhoArquivoHash, String caminhoArquivoChave, String caminhoArquivoMensagem,
            String caminhoChavePrivada, String caminhoChavePublica) throws Exception {
        PrivateKey chavePrivadaUsuarioCorrente = lerObjetoArquivo(caminhoChavePrivada);
        PublicKey chavePublicaRemetente = lerObjetoArquivo(caminhoChavePublica);

        String hashBase64 = lerObjetoArquivo(caminhoArquivoHash);
        hashBase64 = CriptografiaRSA.descriptografar(chavePublicaRemetente, hashBase64);

        String credencialAESBase64 = lerObjetoArquivo(caminhoArquivoChave);
        credencialAESBase64 = CriptografiaRSA.descriptografar(chavePrivadaUsuarioCorrente, credencialAESBase64);
        CredencialAES credencialAES = converteBytesParaObjeto(Base64.getDecoder().decode(credencialAESBase64));

        String mensagemBase64 = lerObjetoArquivo(caminhoArquivoMensagem);
        mensagemBase64 = CriptograficaAES.descriptografar(credencialAES.getChave(), credencialAES.getVetorDeInicializacao(), mensagemBase64);
        Mensagem mensagem = converteBytesParaObjeto(Base64.getDecoder().decode(mensagemBase64));


        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        sha256.update(converteObjetoParaBytes(mensagem));
        String novoHashBase64 = Base64.getEncoder().encodeToString(sha256.digest());

        if (!hashBase64.equals(novoHashBase64)) {
            throw new RuntimeException("Mensagem com conteúdo alterado durante o envio da mensagem!");
        }
        return mensagem;
    }

    private static <T> byte[] converteObjetoParaBytes(T object) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutput out = new ObjectOutputStream(bos)) {
            out.writeObject(object);
            return bos.toByteArray();
        }
    }

    private static <T> T converteBytesParaObjeto(byte[] bytes) throws IOException, ClassNotFoundException  {
        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));
        return (T) objectInputStream.readObject();
    }

    private static <T> void gravarObjetoArquivo(String caminho, T object) throws IOException {
        File arquivo = new File(caminho);
        if (arquivo.getParentFile() != null) {
            arquivo.getParentFile().mkdirs();
        }
        arquivo.createNewFile();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(arquivo));
        objectOutputStream.writeObject(object);
        objectOutputStream.close();
    }

    private static <T> T lerObjetoArquivo(String caminho) throws IOException, ClassNotFoundException {
        ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(caminho));
        return (T) objectInputStream.readObject();
    }
}
