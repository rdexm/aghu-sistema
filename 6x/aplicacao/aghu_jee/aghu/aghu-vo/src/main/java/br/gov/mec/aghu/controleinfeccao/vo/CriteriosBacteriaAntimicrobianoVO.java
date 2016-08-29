package br.gov.mec.aghu.controleinfeccao.vo;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.commons.BaseBean;


public class CriteriosBacteriaAntimicrobianoVO implements BaseBean {
	
	private static final long serialVersionUID = -962601348264968243L;

	private Integer bmrSeq;
	private Integer ambSeq;
	private DominioSituacao indSituacao;
	private String descricao;
	
	public CriteriosBacteriaAntimicrobianoVO() {
		super();
	}
	
	public enum Fields {

		BMR_SEQ("bmrSeq"),
		AMB_SEQ("ambSeq"),
		SITUACAO("indSituacao"),
		DESCRICAO("descricao");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

	public Integer getBmrSeq() {
		return bmrSeq;
	}

	public void setBmrSeq(Integer bmrSeq) {
		this.bmrSeq = bmrSeq;
	}

	public Integer getAmbSeq() {
		return ambSeq;
	}

	public void setAmbSeq(Integer ambSeq) {
		this.ambSeq = ambSeq;
	}

	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
}