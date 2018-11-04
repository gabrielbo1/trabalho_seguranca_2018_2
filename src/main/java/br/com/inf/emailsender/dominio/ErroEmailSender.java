package br.com.inf.emailsender.dominio;

/**
 * @author gabriel
 * @author joao henrique
 * @version 1.0.0
 *
 * Exceções relaciodas a erros da aplicação
 * mensagens que não são erros de excecução.
 */
public class ErroEmailSender extends Exception {
    public ErroEmailSender(String message) {
        super(message);
    }
}
