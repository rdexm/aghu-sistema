package br.gov.mec.aghu.sig.custos.vo;

import java.math.BigDecimal;


public class SigConsumosInternacoesVO implements java.io.Serializable {
	
	
	private static final long serialVersionUID = 8060212906156671351L;
	
	private Integer intSeq;
	private Integer phiSeq;
	private BigDecimal qtde;
	private Integer cppSeq;
	private Integer ctcSeq;
	private Integer cctCodigo;
    private Integer ccsCctCodigo;
	private Integer ocvSeq;
	private Integer matCodigo;
	private Integer pmuSeq;
	private Integer cacSeq;
	private Boolean indFatPendente;
	
	public SigConsumosInternacoesVO() {
		super();
	}

	public Integer getIntSeq() {
		return intSeq;
	}
	public void setIntSeq(Integer intSeq) {
		this.intSeq = intSeq;
	}
	public Integer getPhiSeq() {
		return phiSeq;
	}
	public void setPhiSeq(Integer phiSeq) {
		this.phiSeq = phiSeq;
	}
	public BigDecimal getQtde() {
		return qtde;
	}
	public void setQtde(BigDecimal qtde) {
		this.qtde = qtde;
	}
	public Integer getCppSeq() {
		return cppSeq;
	}
	public void setCppSeq(Integer cppSeq) {
		this.cppSeq = cppSeq;
	}
	public Integer getCtcSeq() {
		return ctcSeq;
	}
	public void setCtcSeq(Integer ctcSeq) {
		this.ctcSeq = ctcSeq;
	}
	public Integer getCctCodigo() {
		return cctCodigo;
	}
	public void setCctCodigo(Integer cctCodigo) {
		this.cctCodigo = cctCodigo;
	}
	public Integer getOcvSeq() {
		return ocvSeq;
	}
	public void setOcvSeq(Integer ocvSeq) {
		this.ocvSeq = ocvSeq;
	}
	public Integer getMatCodigo() {
		return matCodigo;
	}
	public void setMatCodigo(Integer matCodigo) {
		this.matCodigo = matCodigo;
	}
    public Integer getCcsCctCodigo() {
        return ccsCctCodigo;
    }
    public void setCcsCctCodigo(Integer ccsCctCodigo) {
        this.ccsCctCodigo = ccsCctCodigo;
    }
	
	public enum Fields {
		INT_SEQ("intSeq"),
		PHI_SEQ("phiSeq"),
		QTDE("qtde"),
		CPP_SEQ("cppSeq"),
		CTC_SEQ("ctcSeq"),
		CCT_CODIGO("cctCodigo"),
        CCS_CCT_CODIGO("ccsCctCodigo"),
		OCV_SEQ("ocvSeq"),
		MAT_CODIGO("matCodigo"),
		PMU_SEQ("pmuSeq"),
		CAC_SEQ("cacSeq"),
		IND_FAT_PENDENTE("indFatPendente");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	public Integer getPmuSeq() {
		return pmuSeq;
	}

	public void setPmuSeq(Integer pmuSeq) {
		this.pmuSeq = pmuSeq;
	}

	public Integer getCacSeq() {
		return cacSeq;
	}

	public void setCacSeq(Integer cacSeq) {
		this.cacSeq = cacSeq;
	}

	public Boolean getIndFatPendente() {
		return indFatPendente;
	}

	public void setIndFatPendente(Boolean indFatPendente) {
		this.indFatPendente = indFatPendente;
	}

}
