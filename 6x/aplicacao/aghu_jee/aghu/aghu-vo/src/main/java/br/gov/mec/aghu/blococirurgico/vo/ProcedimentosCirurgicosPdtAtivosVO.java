package br.gov.mec.aghu.blococirurgico.vo;

import br.gov.mec.aghu.dominio.DominioIndContaminacao;
import br.gov.mec.aghu.core.commons.BaseBean;

public class ProcedimentosCirurgicosPdtAtivosVO implements BaseBean {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4632356688295331316L;
	private String nomeEspecialidade;
	private Integer pciSeq;
	private String descricao;
	private String strProcedimento;
	private DominioIndContaminacao contaminacao;
	private String strContaminacao;
	private Integer phiSeq;
	private String valorItemSusAmb;
	private String valorSsmSusInt;

	public String getNomeEspecialidade() {
		return nomeEspecialidade;
	}

	public void setNomeEspecialidade(String nomeEspecialidade) {
		this.nomeEspecialidade = nomeEspecialidade;
	}

	public Integer getPciSeq() {
		return pciSeq;
	}

	public void setPciSeq(Integer pciSeq) {
		this.pciSeq = pciSeq;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getStrProcedimento() {
		return strProcedimento;
	}

	public void setStrProcedimento(String strProcedimento) {
		this.strProcedimento = strProcedimento;
	}

	public DominioIndContaminacao getContaminacao() {
		return contaminacao;
	}

	public void setContaminacao(DominioIndContaminacao contaminacao) {
		this.contaminacao = contaminacao;
	}

	public String getStrContaminacao() {
		return strContaminacao;
	}

	public void setStrContaminacao(String strContaminacao) {
		this.strContaminacao = strContaminacao;
	}

	public Integer getPhiSeq() {
		return phiSeq;
	}

	public void setPhiSeq(Integer phiSeq) {
		this.phiSeq = phiSeq;
	}

	public String getValorItemSusAmb() {
		return valorItemSusAmb;
	}

	public void setValorItemSusAmb(String valorItemSusAmb) {
		this.valorItemSusAmb = valorItemSusAmb;
	}

	public String getValorSsmSusInt() {
		return valorSsmSusInt;
	}

	public void setValorSsmSusInt(String valorSsmSusInt) {
		this.valorSsmSusInt = valorSsmSusInt;
	}



	public enum Fields {
		ESPECIALIDADE("nomeEspecialidade"),
		PCI_SEQ("pciSeq"),
		DESCRICAO("descricao"),
		STR_PROCEDIMENTO("strProcedimento"),
		CONTAMINACAO("contaminacao"),
		STR_CONTAMINACAO("strContaminacao"),
		PHI_SEQ("phiSeq"),
		VALOR_ITEM("valorItemSusAmb"),
		VALOR_SSM("valorSsmSusInt");
	
		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	

}
