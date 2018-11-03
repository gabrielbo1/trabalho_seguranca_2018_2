package br.com.inf.emailsender.dominio.mensagem;

import java.io.Serializable;
import java.util.List;

/**
 * @author gabriel
 * @author joao henrique
 *
 * Classe que da forma para a mensagem que serão trocadas
 * através da rede, que será posteriormente gragada de maneira
 * criptograda no arquivo msg.arq
 */
public class Mensagem implements Serializable {

    /**
     * Mensagem que poderá estar ou não presente na mensagem.
     */
    private String mensagem;

    /**
     * Anexos que serão anexados.
     */
    private List<ArquivoBase64> anexos;

    public Mensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public Mensagem(List<ArquivoBase64> anexos) {
        this.anexos = anexos;
    }

    public Mensagem(String mensagem, List<ArquivoBase64> anexos) {
        this.mensagem = mensagem;
        this.anexos = anexos;
    }

    public String getMensagem() {
        return mensagem;
    }

    public List<ArquivoBase64> getAnexos() {
        return anexos;
    }
}
