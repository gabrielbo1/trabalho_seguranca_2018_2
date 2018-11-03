package br.com.inf.emailsender.dominio.criptografia;

import javax.crypto.Cipher;
import java.io.*;
import java.security.*;
import java.util.Base64;

/**
 * @author gabriel
 * @author joao henrique
 * @version 1.0.0
 *
 */
public final class CriptografiaRSA {

    private CriptografiaRSA() { }

    /**Nome do algoritmo de criptografia que esta sendo usado.*/
    private static final String ALGORITMO   = "RSA";

    /**Numero maximo de caracteres que podem ser criptografados
     * de uma so vez com o algoritmo.*/
    private static final int MAX_CARACTERES_BYTE = 117;

    /**Tamanho do array gerado pelo algoritmo de criptografia.*/
    private static final int TAM_ARRAY_BYTE = 128;

    /**
     *
     * @param caminhoChavePublica - Caminho de arquivo para a geração da chave pública.
     * @param caminhoChavePrivada - Caminho de arquivo para a geração da chave privada.
     * @param tamanhoChave - Tamanho em bytes das chaves a serem geradas.
     * @throws Exception
     */
    public static void geradorChaves(String caminhoChavePublica, String caminhoChavePrivada, int tamanhoChave) throws Exception {
        final KeyPairGenerator geradorChave = KeyPairGenerator.getInstance(ALGORITMO);
        geradorChave.initialize(tamanhoChave);
        final KeyPair parDeChaves = geradorChave.generateKeyPair();

        File arquivoChavePublica = new File(caminhoChavePublica);
        File arquivoChavePrivada = new File(caminhoChavePrivada);

        if (arquivoChavePrivada.getParentFile() != null) {
            arquivoChavePrivada.getParentFile().mkdirs();
        }

        arquivoChavePrivada.createNewFile();

        if (arquivoChavePublica.getParentFile() != null) {
            arquivoChavePublica.getParentFile().mkdirs();
        }
        arquivoChavePublica.createNewFile();

        ObjectOutputStream  chavePublicaObjectOutputStream =
                new ObjectOutputStream(new FileOutputStream(arquivoChavePublica));
        chavePublicaObjectOutputStream.writeObject(parDeChaves.getPublic());
        chavePublicaObjectOutputStream.close();

        ObjectOutputStream chavePrivadaObjectOutputStream =
                new ObjectOutputStream(new FileOutputStream(arquivoChavePrivada));
        chavePrivadaObjectOutputStream.writeObject(parDeChaves.getPrivate());
        chavePrivadaObjectOutputStream.close();
    }

    /**
     *
     * @param chave - Chave RSA para criptografia.
     * @param conteudo - Conteúdo a ser criptografado.
     * @return Bytes criptografados em Base64.
     * @throws Exception
     */
    public static String criptografar(Key chave, String conteudo) throws Exception {
        byte[] bytesTextoCifrado = null;
        final Cipher cifrador = Cipher.getInstance(ALGORITMO);
        cifrador.init(Cipher.ENCRYPT_MODE, chave);
        bytesTextoCifrado = criptografaString(cifrador, conteudo);
        return Base64.getEncoder().encodeToString(bytesTextoCifrado);
    }

    /**
     *
     * @param chave - Chave RSA para descriptografia.
     * @param conteudoCriptografado - Conteúdo em Base64 a ser descriptografado.
     * @return Conteúdo em Base64 a ser descriptografado.
     * @throws Exception
     */
    public static String descriptografar(Key chave, String conteudoCriptografado) throws Exception {
        byte[] bytesDescriptografados = null;
        final Cipher cifrador = Cipher.getInstance(ALGORITMO);
        cifrador.init(Cipher.DECRYPT_MODE, chave);
        return decriptografaString(cifrador, Base64.getDecoder().decode(conteudoCriptografado));
    }

    /**
     * @param texto Texto a ser criptografado.
     * Criptografa o texto puro usando chave pública.
     * @return Array de bytes criptografados.
     * O algoritmo RSA com a chave de 1024 bits usada por
     * nossa aplicacao por motivos de performace so consegue
     * criptografar 117 caracteres por vez, sendo assim o algoritmo
     * abaixo separ o texto que supera 117 caracteres em bloco
     * criptografa cada bloco de texto e em seguida concatena os array(s)
     * de bytes criptografados.
     */
    public static byte[] criptografaString(final Cipher cifrador, final String texto) throws Exception {
        byte[] textoCriptografado = null;

        if (texto.length() <= MAX_CARACTERES_BYTE) {
            textoCriptografado = cifrador.doFinal(texto.getBytes("UTF-8"));
        } else {

            int qtdBlocos = 0;

            if (texto.length() % MAX_CARACTERES_BYTE == 0) {
                qtdBlocos = texto.length() / MAX_CARACTERES_BYTE;
            } else {
                qtdBlocos = (texto.length() / MAX_CARACTERES_BYTE) + 1;
            }
            /**Comeco e termino do primeiro bloco de caracteres
             * a ser criptografado.*/
            int inicio = 0, fim = MAX_CARACTERES_BYTE;

            textoCriptografado = cifrador.doFinal(texto.substring(inicio, fim).getBytes("UTF-8"));

            /**PRIMEIRO BLOCO JA FOI CRIPTOGRAFADO*/
            qtdBlocos--;
            for (int i = 0; i < qtdBlocos; i++) {

                /**INICIO <- FIM
                 * FIM += QTD DE CARACTERES DE UM BLOCO.*/
                inicio =  fim;
                fim    += MAX_CARACTERES_BYTE;
                byte[] bloco = null;

                /**Ultimo bloco incompleto de caracteres.*/
                if (fim > texto.length()) {
                    bloco = cifrador.doFinal(texto.substring(inicio,
                            texto.length()).getBytes());
                } else {
                    bloco = cifrador.doFinal(texto.substring(inicio,
                            fim).getBytes());
                }

                /**Concatena o que ja fora criptografado
                 * antes pelo algoritmo com o bloco atual.*/
                textoCriptografado = concatenaArrayByte(textoCriptografado, bloco);
            } //FIM DO FOR.
        } //FIM DO ELSE.
        return textoCriptografado;
    }

    /**
     * @param texto - Array criptografado com o algoritmo
     * acima de criptografia.
     * Caso o array de bytes supere os 128 byte TAM_ARRAY_BYTE
     * o algoritmo divide o array em blocos de 128 e decriptografa
     * bloco a bloco e concatena com os anteriores assim monta
     * o array de bytes decriptografado.
     * Decriptografa o texto puro usando chave privada.
     * BadPaddingException - Preechimento da String e
     * invalido para o funcionamento do algoritmo.
     * IllegalBlockSizeException - A quantidade de caracteres
     * passadas ultrapassa a quantidade maxima de bytes suportados.
     * @return Texto decriptografado.
     */
    public static String decriptografaString(final Cipher cifrador, final byte[] texto) throws Exception {
        byte[] textoDecriptografado = null;
        if (texto.length == TAM_ARRAY_BYTE) {
            textoDecriptografado  = cifrador.doFinal(texto);
        } else {
            /**Bloco de caracteres que guarda
             * o segumento do array texto decriptografado.*/
            byte[] bloco = new byte[TAM_ARRAY_BYTE];

            /**Divide o texto em blocos de 128 bytes
             * copia conteudo criptografado em bloco
             * decriptografa o bloco e em seguida concatena com
             * o que antes fora criptografado.*/
            for (int indice = 0; indice < texto.length;
                 indice += TAM_ARRAY_BYTE) {
                for (int i = 0; i < TAM_ARRAY_BYTE; i++) {
                    bloco[i] = texto[indice + i];
                }
                bloco = cifrador.doFinal(bloco);
                textoDecriptografado = concatenaArrayByte(textoDecriptografado, bloco);
                bloco = new byte[TAM_ARRAY_BYTE];
            }
        }
        return new String(textoDecriptografado);
    }


    /**@param primeiroBloco - Primeiro bloco escrito do buffer.
     * @param segundoBloco  - Segundo bloco que escrito logo apos
     * o termino do primeiro bloco.
     * Caso o primeiro blobo seja nulo retorna-se o
     * segundo bloco, caso ambos nulos retorna-se null.
     * @return Array de byte[] com a concatenacao.*/
    public static byte[] concatenaArrayByte(final byte[] primeiroBloco,
                                            final byte[] segundoBloco) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            if (primeiroBloco != null && primeiroBloco.length != 0) {
                outputStream.write(primeiroBloco);
            }
            if (segundoBloco != null && segundoBloco.length != 0) {
                outputStream.write(segundoBloco);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputStream.toByteArray();
    }
}
