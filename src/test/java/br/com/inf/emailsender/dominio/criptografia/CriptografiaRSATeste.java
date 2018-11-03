package br.com.inf.emailsender.dominio.criptografia;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;


import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.security.PrivateKey;
import java.security.PublicKey;

public class CriptografiaRSATeste {

    @Test
    public void gerarChavesTeste() throws Exception {
        String caminhoChavePublica =
                new java.io.File(".").getCanonicalPath() + "/src/test/resources/chaves_rsa/chave_publica.key";
        String caminhoChavePrivada =
                new java.io.File(".").getCanonicalPath() + "/src/test/resources/chaves_rsa/chave_privada.key";

        CriptografiaRSA.geradorChaves(caminhoChavePublica, caminhoChavePrivada, 1024);
        assert (new File(caminhoChavePublica).exists() && new File(caminhoChavePrivada).exists());
    }

    @Test
    public void criptografiaDescriptografiaTeste() throws Exception {
        this.gerarChavesTeste();
        String caminhoChavePublica =
                new java.io.File(".").getCanonicalPath() + "/src/test/resources/chaves_rsa/chave_publica.key";
        String caminhoChavePrivada =
                new java.io.File(".").getCanonicalPath() + "/src/test/resources/chaves_rsa/chave_privada.key";

        String conteudo = "CONTEUDO";
        ObjectInputStream inputStream = null;

        inputStream = new ObjectInputStream(new FileInputStream(caminhoChavePrivada));
        final PrivateKey chavePrivada = (PrivateKey) inputStream.readObject();
        final String textoCriptografado = CriptografiaRSA.criptografar(chavePrivada, conteudo);

        inputStream = new ObjectInputStream(new FileInputStream(caminhoChavePublica));
        final PublicKey chavePublica = (PublicKey) inputStream.readObject();
        final String textoDecriptografado = CriptografiaRSA.descriptografar(chavePublica, textoCriptografado);

        assertEquals(conteudo, textoDecriptografado);
    }
}
