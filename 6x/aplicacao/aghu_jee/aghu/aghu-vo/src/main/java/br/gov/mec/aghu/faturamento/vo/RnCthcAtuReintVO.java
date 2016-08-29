package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioSituacaoConta;


public class RnCthcAtuReintVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1201817209701309785L;

	private DominioSituacaoConta situacao;
	
	private Boolean retorno;

	public DominioSituacaoConta getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacaoConta situacao) {
		this.situacao = situacao;
	}

	public Boolean getRetorno() {
		return retorno;
	}

	public void setRetorno(Boolean retorno) {
		this.retorno = retorno;
	}
	
}
