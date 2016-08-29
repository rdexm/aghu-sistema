/**
 * 
 */
package br.gov.mec.aghu.core.exception;


/**
 * Exceção ao enviar requisição ao CUPS.
 * 
 * Ex1: Erro: o servidor de impressão não pode ser alcançado ou a impressora
 * especificada não existe.
 * 
 * Ex2: Erro: o servidor de impressão não aceitou a requisição. Trabalho
 * interrompido.
 * 
 * @author Ricardo Costa
 */

public class CupsException extends BaseRuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -214774691078388248L;

	public enum CupsIndisponivelExceptionCode implements BusinessExceptionCode {
		MENSAGEM_FALHA_ENVIAR_CUPS;
	}

	public CupsException(String ipCups, String nomeImpressora) {
		super(CupsIndisponivelExceptionCode.MENSAGEM_FALHA_ENVIAR_CUPS, ipCups, nomeImpressora);
	}

	public CupsException(BusinessExceptionCode exception, Object... params) {
		super(exception, params);
	}

	public CupsException(BusinessExceptionCode exception) {
		super(exception);
	}

}
