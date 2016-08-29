package br.gov.mec.aghu.blococirurgico.vo;

import br.gov.mec.aghu.dominio.DominioIndContaminacao;

public class CursorCProcDescricaoVO {

	private Integer dcgCrgSeq;
	private Integer pciSeq;
	private String contaminacao;

	public enum Fields {

		DCG_CRG_SEQ("dcgCrgSeq"), 
		PCI_SEQ("pciSeq"),
		CONTAMINACAO("contaminacao");

		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public Integer getDcgCrgSeq() {
		return dcgCrgSeq;
	}

	public void setDcgCrgSeq(Integer dcgCrgSeq) {
		this.dcgCrgSeq = dcgCrgSeq;
	}

	public Integer getPciSeq() {
		return pciSeq;
	}

	public void setPciSeq(Integer pciSeq) {
		this.pciSeq = pciSeq;
	}

	public DominioIndContaminacao getIndContaminacao() {
		return contaminacao!=null ? DominioIndContaminacao.valueOf(contaminacao): null;
	}

	public String getContaminacao() {
		return contaminacao;
	}

	public void setContaminacao(String contaminacao) {
		this.contaminacao = contaminacao;
	}
}