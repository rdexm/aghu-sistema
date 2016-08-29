package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;
import java.util.Date;


public class GradeAgendamentoAmbulatorioVO implements Serializable {
	private static final long serialVersionUID = 11244534532332L;
	
	private Short uslUnfSeq;
	private Short espSeq;
	private Date dtConsulta;
	private Integer numero;
	private String indSituacaoConsulta;
	private Boolean excedeProgramacao;
	private Integer pacCodigo;
	private Integer grdSeq;
	
	
	public enum Fields {

		USL_UNF_SEQ("uslUnfSeq"),
		ESP_SEQ("espSeq"),
		DT_CONSULTA("dtConsulta"),
		NUMERO("numero"),
		IND_SIT_CONSULTA("indSituacaoConsulta"),
		IND_EXCEDE_PROGRAMACAO("excedeProgramacao"),
		PAC_CODIGO("pacCodigo"),
		GRD_SEQ("grdSeq");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
	
	public Short getUslUnfSeq() {
		return uslUnfSeq;
	}
	public void setUslUnfSeq(Short uslUnfSeq) {
		this.uslUnfSeq = uslUnfSeq;
	}
	public Short getEspSeq() {
		return espSeq;
	}
	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
	}
	public Date getDtConsulta() {
		return dtConsulta;
	}
	public void setDtConsulta(Date dtConsulta) {
		this.dtConsulta = dtConsulta;
	}
	public Integer getNumero() {
		return numero;
	}
	public void setNumero(Integer numero) {
		this.numero = numero;
	}
	public String getIndSituacaoConsulta() {
		return indSituacaoConsulta;
	}
	public void setIndSituacaoConsulta(String indSituacaoConsulta) {
		this.indSituacaoConsulta = indSituacaoConsulta;
	}
	public Boolean getExcedeProgramacao() {
		return excedeProgramacao;
	}
	public void setExcedeProgramacao(Boolean excedeProgramacao) {
		this.excedeProgramacao = excedeProgramacao;
	}
	public Integer getPacCodigo() {
		return pacCodigo;
	}
	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}
	public Integer getGrdSeq() {
		return grdSeq;
	}
	public void setGrdSeq(Integer grdSeq) {
		this.grdSeq = grdSeq;
	}
}
