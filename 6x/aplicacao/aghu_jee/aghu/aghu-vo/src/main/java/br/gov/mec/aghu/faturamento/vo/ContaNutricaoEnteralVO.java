package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;
import java.util.Date;

public class ContaNutricaoEnteralVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3805116582799980576L;
	private Integer seq;
	private String indAutorizadoSms;
	private Integer prontuario;
	private String nome;
	private Date dtAltaAdministrativa;
	private Date dtIntAdministrativa;
	private Long quantidadesRealizadas;

	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	public String getIndAutorizadoSms() {
		return indAutorizadoSms;
	}
	public void setIndAutorizadoSms(String indAutorizadoSms) {
		this.indAutorizadoSms = indAutorizadoSms;
	}
	public Integer getProntuario() {
		return prontuario;
	}
	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public Date getDtAltaAdministrativa() {
		return dtAltaAdministrativa;	
	}
	public void setDtAltaAdministrativa(Date dtAltaAdministrativa) {
		this.dtAltaAdministrativa = dtAltaAdministrativa;
	}
	public Date getDtIntAdministrativa() {
		return dtIntAdministrativa;
	}
	public void setDtIntAdministrativa(Date dtIntAdministrativa) {
		this.dtIntAdministrativa = dtIntAdministrativa;
	}

	public Long getQuantidadesRealizadas() {
		return quantidadesRealizadas;
	}
	public void setQuantidadesRealizadas(Long quantidadesRealizadas) {
		this.quantidadesRealizadas = quantidadesRealizadas;
	}

	public enum Fields {
		SEQ("seq"),
		IND_AUTORIZADO_SMS("indAutorizadoSms"),
		PRONTUARIO("prontuario"),
		NOME("nome"),
		DT_ALTA_ADMINISTRATIVA("dtAltaAdministrativa"),
		DT_INT_ADMINISTRATIVA("dtIntAdministrativa"),
		QUANTIDADES_REALIZADAS("quantidadesRealizadas");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
	

}
