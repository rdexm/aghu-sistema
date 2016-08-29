package br.gov.mec.aghu.blococirurgico.vo;

import br.gov.mec.aghu.dominio.DominioIndContaminacao;

public class ProcEspPorCirurgiaVO {
	
	private Integer crgSeq;
	private Integer pciSeq;
	private Integer dptSeq;
    private DominioIndContaminacao indContaminacao;
    private Boolean indPrincipal;
	
	public ProcEspPorCirurgiaVO() {
	}

	public Integer getCrgSeq() {
		return crgSeq;
	}

	public void setCrgSeq(Integer crgSeq) {
		this.crgSeq = crgSeq;
	}

	public Integer getPciSeq() {
		return pciSeq;
	}

	public void setPciSeq(Integer pciSeq) {
		this.pciSeq = pciSeq;
	}
	
	public Integer getDptSeq() {
		return dptSeq;
	}

	public void setDptSeq(Integer dptSeq) {
		this.dptSeq = dptSeq;
	}

	public DominioIndContaminacao getIndContaminacao() {
		return indContaminacao;
	}

	public void setIndContaminacao(DominioIndContaminacao indContaminacao) {
		this.indContaminacao = indContaminacao;
	}

	public Boolean getIndPrincipal() {
		return indPrincipal;
	}

	public void setIndPrincipal(Boolean indPrincipal) {
		this.indPrincipal = indPrincipal;
	}


	public enum Fields {

		CRG_SEQ("crgSeq"),
		PCI_SEQ("pciSeq"),
		DPT_SEQ("dptSeq"),
		IND_CONTAMINACAO("indContaminacao"),
		IND_PRINCIPAL("indPrincipal");

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
