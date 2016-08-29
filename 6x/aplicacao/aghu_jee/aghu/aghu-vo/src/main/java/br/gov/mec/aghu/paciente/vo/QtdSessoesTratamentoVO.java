package br.gov.mec.aghu.paciente.vo;

import java.io.Serializable;

public class QtdSessoesTratamentoVO implements Serializable {
	
	private static final long serialVersionUID = -487782972402545339L;
	
	private Integer seqPrescricaoPaciente;
	private Integer atdSeqPrescricaoPaciente;
	private String observacaoPrescricaoPaciente;
	private Boolean indSessoesContinuas;
	private Byte qtdSessoes;
	private String descricaoProcedHospInterno;
	private Integer seqProcedHospInterno;
	private String tratamentoFormatado;
	private String qtdSessoesTratamentoFormatada;
	
	public enum Fields {
		SEQ_PRESCRICAO_PACIENTE("seqPrescricaoPaciente"),
		ATD_SEQ_PRESCRICAO_PACIENTE("atdSeqPrescricaoPaciente"),
		OBSERVACAO_PRESCRICAO_PACIENTE("observacaoPrescricaoPaciente"),
		IND_SESSOES_CONTINUAS("indSessoesContinuas"),
		QTD_SESSOES("qtdSessoes"),
		DESCRICAO_PROCED_HOSP_INTERNO("descricaoProcedHospInterno"),
		SEQ_PROCED_HOSP_INTERNO("seqProcedHospInterno");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	
	public Integer getSeqPrescricaoPaciente() {
		return seqPrescricaoPaciente;
	}
	public void setSeqPrescricaoPaciente(Integer seqPrescricaoPaciente) {
		this.seqPrescricaoPaciente = seqPrescricaoPaciente;
	}
	public Integer getAtdSeqPrescricaoPaciente() {
		return atdSeqPrescricaoPaciente;
	}
	public void setAtdSeqPrescricaoPaciente(Integer atdSeqPrescricaoPaciente) {
		this.atdSeqPrescricaoPaciente = atdSeqPrescricaoPaciente;
	}
	public String getObservacaoPrescricaoPaciente() {
		return observacaoPrescricaoPaciente;
	}
	public void setObservacaoPrescricaoPaciente(String observacaoPrescricaoPaciente) {
		this.observacaoPrescricaoPaciente = observacaoPrescricaoPaciente;
	}
	public Boolean getIndSessoesContinuas() {
		return indSessoesContinuas;
	}
	public void setIndSessoesContinuas(Boolean indSessoesContinuas) {
		this.indSessoesContinuas = indSessoesContinuas;
	}
	public Byte getQtdSessoes() {
		return qtdSessoes;
	}
	public void setQtdSessoes(Byte qtdSessoes) {
		this.qtdSessoes = qtdSessoes;
	}
	public String getDescricaoProcedHospInterno() {
		return descricaoProcedHospInterno;
	}
	public void setDescricaoProcedHospInterno(String descricaoProcedHospInterno) {
		this.descricaoProcedHospInterno = descricaoProcedHospInterno;
	}
	public Integer getSeqProcedHospInterno() {
		return seqProcedHospInterno;
	}
	public void setSeqProcedHospInterno(Integer seqProcedHospInterno) {
		this.seqProcedHospInterno = seqProcedHospInterno;
	}
	public String getTratamentoFormatado() {
		return tratamentoFormatado;
	}
	public void setTratamentoFormatado(String tratamentoFormatado) {
		this.tratamentoFormatado = tratamentoFormatado;
	}
	public String getQtdSessoesTratamentoFormatada() {
		return qtdSessoesTratamentoFormatada;
	}
	public void setQtdSessoesTratamentoFormatada(
			String qtdSessoesTratamentoFormatada) {
		this.qtdSessoesTratamentoFormatada = qtdSessoesTratamentoFormatada;
	}	

}
