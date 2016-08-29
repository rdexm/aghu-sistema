package br.gov.mec.aghu.business.integracao.exception;

/**
 * Representa um erro na execução do serviço requisitado.
 * 
 * @author aghu
 * 
 */
@SuppressWarnings("ucd")
public class ExecucaoServicoNegocioException extends AGHUIntegracaoException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6632030257223390586L;

	private String chaveErro;

	public ExecucaoServicoNegocioException(Throwable e) {
		super(e);
	}

	public ExecucaoServicoNegocioException(String chaveErro) {
		this.chaveErro = chaveErro;
	}

	public String getChaveErro() {
		return chaveErro;
	}

	public void setChaveErro(String chaveErro) {
		this.chaveErro = chaveErro;
	}

}
