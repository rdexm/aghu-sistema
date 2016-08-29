package br.gov.mec.aghu.blococirurgico.vo;

import java.util.Date;

public class SolicitarReceberOrcMatNaoLicitadoVO {

	private Date criadoEm;
	private String justificativaRequisicaoOpme;
	private String observacaoOpme;
	private String nomeSolicitante;
	private String descricaoEtapaAtual;
	
	public enum Fields {
		CRIADO_EM("criadoEm"),
		JUSTIFICATIVA("justificativaRequisicaoOpme"),
		OBSERVACAO("observacaoOpme"),
		SOLICITANTE("nomeSolicitante"),
		DESCRICAO_ETAPA("descricaoEtapaAtual");
		
		private String fields;
		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public String getJustificativaRequisicaoOpme() {
		return justificativaRequisicaoOpme;
	}

	public void setJustificativaRequisicaoOpme(String justificativaRequisicaoOpme) {
		this.justificativaRequisicaoOpme = justificativaRequisicaoOpme;
	}

	public String getObservacaoOpme() {
		return observacaoOpme;
	}

	public void setObservacaoOpme(String observacaoOpme) {
		this.observacaoOpme = observacaoOpme;
	}

	public String getNomeSolicitante() {
		return nomeSolicitante;
	}

	public void setNomeSolicitante(String nomeSolicitante) {
		this.nomeSolicitante = nomeSolicitante;
	}

	public String getDescricaoEtapaAtual() {
		return descricaoEtapaAtual;
	}

	public void setDescricaoEtapaAtual(String descricaoEtapaAtual) {
		this.descricaoEtapaAtual = descricaoEtapaAtual;
	}

}
