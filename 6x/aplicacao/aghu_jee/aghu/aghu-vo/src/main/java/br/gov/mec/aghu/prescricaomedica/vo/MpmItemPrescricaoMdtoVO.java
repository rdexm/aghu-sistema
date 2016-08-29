package br.gov.mec.aghu.prescricaomedica.vo;

public class MpmItemPrescricaoMdtoVO {

	private Integer pmdAtdSeq;
	private Long pmdSeq;
	private Short seqp;
	private Integer medMatCodigo;
	private Boolean indOrigemJustif;
	private Short duracaoTratSolicitado;
	private Integer jumSeq;
	
	public MpmItemPrescricaoMdtoVO() {
		
	}
	
	public MpmItemPrescricaoMdtoVO(Integer pmdAtdSeq, Long pmdSeq, Short seqp,
			Integer medMatCodigo, Boolean indOrigemJustif,
			Short duracaoTratSolicitado, Integer jumSeq) {
		super();
		this.pmdAtdSeq = pmdAtdSeq;
		this.pmdSeq = pmdSeq;
		this.seqp = seqp;
		this.medMatCodigo = medMatCodigo;
		this.indOrigemJustif = indOrigemJustif;
		this.duracaoTratSolicitado = duracaoTratSolicitado;
		this.jumSeq = jumSeq;
	}

	public Integer getPmdAtdSeq() {
		return pmdAtdSeq;
	}
	
	public void setPmdAtdSeq(Integer pmdAtdSeq) {
		this.pmdAtdSeq = pmdAtdSeq;
	}
	
	public Long getPmdSeq() {
		return pmdSeq;
	}
	
	public void setPmdSeq(Long pmdSeq) {
		this.pmdSeq = pmdSeq;
	}
	
	public Short getSeqp() {
		return seqp;
	}
	
	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}
	
	public Integer getMedMatCodigo() {
		return medMatCodigo;
	}

	public void setMedMatCodigo(Integer medMatCodigo) {
		this.medMatCodigo = medMatCodigo;
	}

	public Boolean getIndOrigemJustif() {
		return indOrigemJustif;
	}

	public void setIndOrigemJustif(Boolean indOrigemJustif) {
		this.indOrigemJustif = indOrigemJustif;
	}

	public Short getDuracaoTratSolicitado() {
		return duracaoTratSolicitado;
	}

	public void setDuracaoTratSolicitado(Short duracaoTratSolicitado) {
		this.duracaoTratSolicitado = duracaoTratSolicitado;
	}

	public Integer getJumSeq() {
		return jumSeq;
	}

	public void setJumSeq(Integer jumSeq) {
		this.jumSeq = jumSeq;
	}

	public enum Fields {
		PMD_ATD_SEQ("pmdAtdSeq"), 
		PMD_SEQ("pmdSeq"), 
		SEQP("seqp"), 
		MED_MAT_CODIGO("medMatCodigo"), 
		JUM_SEQ("jumSeq"), 
		DURACAO_TRAT_SOLICITADO("duracaoTratSolicitado"), 
		IND_ORIGEM_JUSTIF("indOrigemJustif");

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
