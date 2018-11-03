package br.com.inf.emailsender.dominio;

import br.com.inf.emailsender.dominio.criptografia.CriptografiaRSA;
import br.com.inf.emailsender.dominio.mensagem.Mensagem;
import org.junit.jupiter.api.Test;

import java.io.File;

public class ProtocoloTeste {

    private String caminhoChavePrivadaUsuarioCorrente;

    private String caminhoChavePublicaUsuarioCorrente;

    private String caminhoChavePrivadaDestinatario;

    private String caminhoChavePublicaDestinatario;

    private static final String RESOURCES_TESTE = "src" + File.separator + "test" + File.separator + "resources";

    public void criarChaves() throws Exception {
        String root = new java.io.File(".").getCanonicalPath() + File.separator + RESOURCES_TESTE + File.separator;
        caminhoChavePrivadaUsuarioCorrente = root + "chaves_rsa_usuario_corrente" + File.separator + "chave_privada.key";
        caminhoChavePublicaUsuarioCorrente = root + "chaves_rsa_usuario_corrente" + File.separator + "chave_publica.key";
        caminhoChavePrivadaDestinatario = root + "chaves_rsa_destinatario" + File.separator + "chave_privada.key";
        caminhoChavePublicaDestinatario = root + "chaves_rsa_destinatario" + File.separator + "chave_publica.key";

        CriptografiaRSA.geradorChaves(caminhoChavePublicaUsuarioCorrente, caminhoChavePrivadaUsuarioCorrente, 1024);
        CriptografiaRSA.geradorChaves(caminhoChavePublicaDestinatario, caminhoChavePrivadaDestinatario, 1024);
    }

    @Test
    public void mensagemSemAnexosTeste() throws Exception {
        criarChaves();
        Mensagem mensagem = new Mensagem("Mensagem sem nenhum anexo");

        String caminhoPastaArquivosCriptografados = new java.io.File(".").getCanonicalPath()
                + File.separator + RESOURCES_TESTE + File.separator + "mensagem_sem_anexos";
        Protocolo.gerarArquivosEnvioCriptografados(caminhoPastaArquivosCriptografados, mensagem, caminhoChavePrivadaUsuarioCorrente, caminhoChavePublicaDestinatario);

        String caminhoArquivoMensagem = caminhoPastaArquivosCriptografados + File.separator + "msg.arq";
        String caminhoArquivoHash     = caminhoPastaArquivosCriptografados + File.separator + "hash.arq";
        String caminhoArquivoChave    = caminhoPastaArquivosCriptografados + File.separator + "key.arq";

        Mensagem mensagemDecriptografada =
                Protocolo.lerArquivosRecebidosDescriptografar(caminhoArquivoHash, caminhoArquivoChave, caminhoArquivoMensagem, caminhoChavePrivadaDestinatario, caminhoChavePublicaUsuarioCorrente);

        assert (mensagem.getMensagem().equals(mensagemDecriptografada.getMensagem()));
    }
}
