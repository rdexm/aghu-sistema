package br.gov.mec.aghu.internacao.vo;

import br.gov.mec.aghu.model.AinSolicTransfPacientes;
import br.gov.mec.aghu.core.commons.BaseBean;

public class SolicitacaoTransferenciaPacienteVO implements BaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8571440594651997434L;
	private AinSolicTransfPacientes solicitacao;
	private String estilo;
	private String descricaoUnidadeFuncional;

	public AinSolicTransfPacientes getSolicitacao() {
		return solicitacao;
	}

	public void setSolicitacao(AinSolicTransfPacientes solicitacao) {
		this.solicitacao = solicitacao;
	}

	public String getEstilo() {
		return estilo;
	}

	public void setEstilo(String estilo) {
		this.estilo = estilo;
	}

	public String getDescricaoUnidadeFuncional() {
		return descricaoUnidadeFuncional;
	}

	public void setDescricaoUnidadeFuncional(String descricaoUnidadeFuncional) {
		this.descricaoUnidadeFuncional = descricaoUnidadeFuncional;
	}
	
}
