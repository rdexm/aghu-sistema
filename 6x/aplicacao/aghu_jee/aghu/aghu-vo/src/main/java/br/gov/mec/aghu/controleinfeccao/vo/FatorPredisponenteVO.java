package br.gov.mec.aghu.controleinfeccao.vo;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.commons.BaseBean;


public class FatorPredisponenteVO implements BaseBean {
	
	private static final long serialVersionUID = -962601348264968243L;

	private Short seq;
	private String descricao;
	private Byte grauRisco;
	private Double pesoInicial;
	private Double pesoFinal;
	private DominioSituacao situacao;
	
	
	public FatorPredisponenteVO() {
		super();
	}
	
	public enum Fields {

		SEQ("seq"),
		DESCRICAO("descricao"),
		GRAU_RISCO("grauRisco"),
		SITUACAO("situacao"),
		PESO_INICIAL("pesoInicial"),
		PESO_FINAL("pesoFinal");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

	public Short getSeq() {
		return seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Byte getGrauRisco() {
		return grauRisco;
	}

	public void setGrauRisco(Byte grauRisco) {
		this.grauRisco = grauRisco;
	}

	public Double getPesoInicial() {
		return pesoInicial;
	}

	public void setPesoInicial(Double pesoInicial) {
		this.pesoInicial = pesoInicial;
	}

	public Double getPesoFinal() {
		return pesoFinal;
	}

	public void setPesoFinal(Double pesoFinal) {
		this.pesoFinal = pesoFinal;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}
}