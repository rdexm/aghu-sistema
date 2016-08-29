package br.gov.mec.aghu.compras.vo;

import java.io.Serializable;

public class AlteracaoEntregaProgramadaVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7413170826292279684L;
	private boolean alteracaoPermitida;
	private String messagem;
	private boolean mensagemConfirmacao;
	
	public AlteracaoEntregaProgramadaVO (){}

	public AlteracaoEntregaProgramadaVO(boolean alteracaoPermitida,
			String messagem) {
		this.setAlteracaoPermitida(alteracaoPermitida);
		this.messagem = messagem;
	}

	public AlteracaoEntregaProgramadaVO(boolean alteracaoPermitida) {
		this.setAlteracaoPermitida(alteracaoPermitida);
	}

	public AlteracaoEntregaProgramadaVO(boolean alteracaoPermitida,
			boolean isMensagemConfirmacao, String messagem) {
		this.messagem = messagem;
		this.mensagemConfirmacao = isMensagemConfirmacao;
		this.setAlteracaoPermitida(alteracaoPermitida);
	}

	public void setMessagem(String messagem) {
		this.messagem = messagem;
	}

	public String getMessagem() {
		return messagem;
	}

	public void setMensagemConfirmacao(boolean mensagemConfirmacao) {
		this.mensagemConfirmacao = mensagemConfirmacao;
	}

	public boolean isMensagemConfirmacao() {
		return mensagemConfirmacao;
	}

	public void setAlteracaoPermitida(boolean alteracaoPermitida) {
		this.alteracaoPermitida = alteracaoPermitida;
	}

	public boolean isAlteracaoPermitida() {
		return alteracaoPermitida;
	}
}
