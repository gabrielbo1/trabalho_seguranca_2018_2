package br.com.inf.emailsender.dominio.criptografia;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author gabriel
 * @author joao henrique
 * @version 1.0.0
 */
public class CriptograficaAESTeste {

    @Test
    public void criptografiaDescriptografiaTest() throws Exception {
        String conteudo = "CONTEUDO";
        String conteudoOriginal = conteudo;

        byte[] chave = CriptograficaAES.gerarVetorBytesAleatorios(16);
        byte[] vetorDeInicializacao = CriptograficaAES.gerarVetorBytesAleatorios(16);


        conteudo = CriptograficaAES.criptografar(chave, vetorDeInicializacao, conteudoOriginal);
        assertEquals(conteudoOriginal, CriptograficaAES.descriptografar(chave, vetorDeInicializacao, conteudo));
    }
}
