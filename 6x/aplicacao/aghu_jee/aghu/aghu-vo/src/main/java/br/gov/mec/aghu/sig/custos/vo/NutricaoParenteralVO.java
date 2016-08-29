package br.gov.mec.aghu.sig.custos.vo;

import java.util.Date;

public class NutricaoParenteralVO implements java.io.Serializable {

	private static final long serialVersionUID = 7074868204890810395L;
	
	private Integer pmdSeq;   // prescricao medica
    private Integer nptSeq;   // prescricao parenteral
	private Integer atdSeq;   // atendimento
	private Integer intSeq;   // internacao
	private Short pedSeq;   // procedimento
	private Date pmdDataInicio;
	private Date pmdDataFim;
	private Date nptDataInicio;
	private Date nptDataFim;
	private Long qtdeNpt;
	
	public NutricaoParenteralVO(){}

	public Integer getPmdSeq() {
		return pmdSeq;
	}

	public void setPmdSeq(Integer pmdSeq) {
		this.pmdSeq = pmdSeq;
	}
	
	public Integer getNptSeq() {
		return nptSeq;
	}


	public void setNptSeq(Integer nptSeq) {
		this.nptSeq = nptSeq;
	}


	public Integer getAtdSeq() {
		return atdSeq;
	}


	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}


	public Integer getIntSeq() {
		return intSeq;
	}


	public void setIntSeq(Integer intSeq) {
		this.intSeq = intSeq;
	}


	public Short getPedSeq() {
		return pedSeq;
	}


	public void setPedSeq(Short pedSeq) {
		this.pedSeq = pedSeq;
	}


	public Date getPmdDataInicio() {
		return pmdDataInicio;
	}


	public void setPmdDataInicio(Date pmdDataInicio) {
		this.pmdDataInicio = pmdDataInicio;
	}


	public Date getPmdDataFim() {
		return pmdDataFim;
	}


	public void setPmdDataFim(Date pmdDataFim) {
		this.pmdDataFim = pmdDataFim;
	}


	public Date getNptDataInicio() {
		return nptDataInicio;
	}


	public void setNptDataInicio(Date nptDataInicio) {
		this.nptDataInicio = nptDataInicio;
	}


	public Date getNptDataFim() {
		return nptDataFim;
	}


	public void setNptDataFim(Date nptDataFim) {
		this.nptDataFim = nptDataFim;
	}


	public Long getQtdeNpt() {
		return qtdeNpt;
	}


	public void setQtdeNpt(Long qtdeNpt) {
		this.qtdeNpt = qtdeNpt;
	}
	
	public enum Fields{
		PMD_SEQ("pmdSeq"),
		SEQ_NPT("nptSeq"),
		ATD_SEQ("atdSeq"),
		INT_SEQ("intSeq"),
		PED_SEQ("pedSeq"),		
		PRESCRICAO_MEDICA_DATA_INICIO("pmdDataInicio"),
		PRESCRICAO_MEDICA_DATA_FIM("pmdDataFim"),
		PRESCRICAO_PARENTERAL_DATA_INICIO("nptDataInicio"),
		PRESCRICAO_PARENTERAL_DATA_FIM("nptDataFim"),
		QUANTIDADE("qtdeNpt");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

}
