package br.gov.mec.aghu.controleinfeccao.vo;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.commons.BaseBean;


public class BacteriaMultirresistenteVO implements BaseBean {
	
	private static final long serialVersionUID = -962601348264968243L;

	private Integer codigoBacteria;
	private String descricaoBacteria;
	private DominioSituacao situacaoBacteria;
	
	public BacteriaMultirresistenteVO() {
		super();
	}
	
	public enum Fields {

		CODIGO("codigoBacteria"),
		DESCRICAO("descricaoBacteria"),
		SITUACAO("situacaoBacteria");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}
	
	public Integer getCodigoBacteria() {
		return codigoBacteria;
	}

	public void setCodigoBacteria(Integer codigoBacteria) {
		this.codigoBacteria = codigoBacteria;
	}

	public String getDescricaoBacteria() {
		return descricaoBacteria;
	}

	public void setDescricaoBacteria(String descricaoBacteria) {
		this.descricaoBacteria = descricaoBacteria;
	}

	public DominioSituacao getSituacaoBacteria() {
		return situacaoBacteria;
	}

	public void setSituacaoBacteria(DominioSituacao situacaoBacteria) {
		this.situacaoBacteria = situacaoBacteria;
	}
}