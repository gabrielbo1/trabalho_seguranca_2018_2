package br.com.inf.emailsender.dominio.mensagem;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.stream.Collectors;

/**
 * @author gabriel
 * @version joao henrique
 * @version 1.0.0
 *
 * Classe que especifíca como serão os arquivos
 * dentro da aplicação.
 */
public class ArquivoBase64 implements Serializable {

    private String base64;

    private String extensao;

    public ArquivoBase64(String caminhoArquivo) throws IOException {
        base64 = Base64.getEncoder()
                       .encodeToString(Files.lines(Paths.get(caminhoArquivo))
                                            .collect(Collectors.joining())
                                            .getBytes());
        String nomeArquivo = new File(caminhoArquivo).getName();
        int dotIndex = nomeArquivo.lastIndexOf('.');
        extensao =  (dotIndex == -1) ? "" : nomeArquivo.substring(dotIndex + 1);
    }

    public String getBase64() {
        return base64;
    }

    public String getExtensao() {
        return extensao;
    }
}
