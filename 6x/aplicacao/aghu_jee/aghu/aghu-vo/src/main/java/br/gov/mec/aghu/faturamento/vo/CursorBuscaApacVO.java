package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;
import java.util.Date;


public class CursorBuscaApacVO implements Serializable {

	private static final long serialVersionUID = 8981914689510990564L;
	
	private Byte seqp;
	private Date dtInicioValidade;
	private String capType;
	private String indAutorizadoSms;
	private Long numero;
	private Date dtInicio;
	private Integer phiSeq;
	
	public Byte getSeqp() {
		return seqp;
	}

	public void setSeqp(Byte seqp) {
		this.seqp = seqp;
	}

	public Date getDtInicioValidade() {
		return dtInicioValidade;
	}

	public void setDtInicioValidade(Date dtInicioValidade) {
		this.dtInicioValidade = dtInicioValidade;
	}

	public String getCapType() {
		return capType;
	}

	public void setCapType(String capType) {
		this.capType = capType;
	}

	public String getIndAutorizadoSms() {
		return indAutorizadoSms;
	}

	public void setIndAutorizadoSms(String indAutorizadoSms) {
		this.indAutorizadoSms = indAutorizadoSms;
	}

	public Date getDtInicio() {
		return dtInicio;
	}

	public void setDtInicio(Date dtInicio) {
		this.dtInicio = dtInicio;
	}

	public enum Fields {
		NUMERO("numero"),
		SEQP("seqp"),
		DT_INICIO_VALIDADE("dtInicioValidade"),
		CAP_TYPE("capType"),
		IND_AUTORIZADO_SMS("indAutorizadoSms"),
		DATA_INICIO("dtInicio"),
		PHI_SEQ("phiSeq")
		;
		
		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public Long getNumero() {
		return numero;
	}

	public void setNumero(Long numero) {
		this.numero = numero;
	}

	public Integer getPhiSeq() {
		return phiSeq;
	}

	public void setPhiSeq(Integer phiSeq) {
		this.phiSeq = phiSeq;
	}
}