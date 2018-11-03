package br.com.inf.emailsender.dominio.criptografia;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * @author gabriel
 * @author joao henrique
 * @version 1.0.0
 */
public final class CriptograficaAES {

    private CriptograficaAES() { }

    /**Nome do algoritmo de criptografia que esta sendo usado.
     * Neste caso usamos o default da própia JVM que corresponde
     * a um AES-128 que permite vetores de inicialização e chaves
     * de 16bytes / 128 bits*/
    private static final String ALGORITMO   = "AES";

    /**
     *
     * @param qtdBytes - Quantidade de bytes que o vetor deverá conter.
     * @return Vetor com os gerados aletóriamente de maneira segura.
     * @throws Exception
     */
    public static byte[] gerarVetorBytesAleatorios(int qtdBytes) throws Exception {
        SecureRandom random = new SecureRandom();
        byte[] vetorDeInicializacao = new byte[qtdBytes];
        random.nextBytes(vetorDeInicializacao);
        return vetorDeInicializacao;
    }

    /**
     *
     * @param chave - Chave AES.
     * @param vetorDeInicializacao - Vetor de inicialização do algoritmo.
     * @param conteudo - Conteúdo a ser criptografado pelo algoritmo.
     * @throws Exception
     * @return Conteúdo criptografado pelo algoritmo em Base64.
     */
    public static String criptografar(byte[] chave, byte[] vetorDeInicializacao, String conteudo) throws Exception {
        IvParameterSpec iv = new IvParameterSpec(vetorDeInicializacao);
        SecretKeySpec skeySpec = new SecretKeySpec(chave, ALGORITMO);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(conteudo.getBytes("UTF-8"));

        return Base64.getEncoder().encodeToString(encrypted);
    }

    /**
     *
     * @param chave - Chave AES.
     * @param vetorDeInicializacao - Vetor de inicialização do algoritmo.
     * @param conteudoCriptografado - String do conteúdo criptografado em Base64.
     * @throws Exception
     * @return Conteúdo descriptografado.
     */
    public static String descriptografar(byte[] chave, byte[] vetorDeInicializacao, String conteudoCriptografado) throws Exception {
        IvParameterSpec iv = new IvParameterSpec(vetorDeInicializacao);
        SecretKeySpec skeySpec = new SecretKeySpec(chave, ALGORITMO);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

        byte[] original = cipher.doFinal(Base64.getDecoder().decode(conteudoCriptografado));

        return new String(original);
    }
}
