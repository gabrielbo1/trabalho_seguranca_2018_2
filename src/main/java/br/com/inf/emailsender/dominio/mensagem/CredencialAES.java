package br.com.inf.emailsender.dominio.mensagem;

import java.io.Serializable;

/**
 * @author gabriel
 * @author joao henrique
 * @version 1.0.0
 *
 * Classe que representa a chave que será posteriormente
 * serializada no arquivo key.arq
 */
public class CredencialAES implements Serializable {

    private byte[] chave;

    private byte[] vetorDeInicializacao;

    public CredencialAES(byte[] chave, byte[] vetorDeInicializacao) {
        this.chave = chave;
        this.vetorDeInicializacao = vetorDeInicializacao;
    }

    public byte[] getChave() {
        return chave;
    }

    public byte[] getVetorDeInicializacao() {
        return vetorDeInicializacao;
    }
}
