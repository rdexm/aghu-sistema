package br.gov.mec.aghu.bancosangue.vo;

public class ItemSolicitacaoHemoterapicaJustificativaVO {

	private Integer ishSheAtdSeq;
	private Integer ishSheSeq;
	private Short ishSequencia;
	private Integer jcsSeq;
	
	public ItemSolicitacaoHemoterapicaJustificativaVO() {
	}

	public ItemSolicitacaoHemoterapicaJustificativaVO(Integer ishSheAtdSeq,
			Integer ishSheSeq, Short ishSequencia, Integer jcsSeq) {
		super();
		this.ishSheAtdSeq = ishSheAtdSeq;
		this.ishSheSeq = ishSheSeq;
		this.ishSequencia = ishSequencia;
		this.jcsSeq = jcsSeq;
	}

	public Integer getIshSheAtdSeq() {
		return ishSheAtdSeq;
	}

	public void setIshSheAtdSeq(Integer ishSheAtdSeq) {
		this.ishSheAtdSeq = ishSheAtdSeq;
	}

	public Integer getIshSheSeq() {
		return ishSheSeq;
	}

	public void setIshSheSeq(Integer ishSheSeq) {
		this.ishSheSeq = ishSheSeq;
	}

	public Short getIshSequencia() {
		return ishSequencia;
	}

	public void setIshSequencia(Short ishSequencia) {
		this.ishSequencia = ishSequencia;
	}

	public Integer getJcsSeq() {
		return jcsSeq;
	}

	public void setJcsSeq(Integer jcsSeq) {
		this.jcsSeq = jcsSeq;
	}
	
	public enum Fields {
		
		ISH_SHE_ATD_SEQ("ishSheAtdSeq"), 
		ISH_SHE_SEQ("ishSheSeq"), 
		ISH_SEQUENCIA("ishSequencia"), 
		JCS_SEQ("jcsSeq"); 

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
