package br.gov.mec.aghu.emergencia.vo;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.commons.BaseBean;

public class DiagnosticoVO implements BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3719423092534467385L;

	private Integer seq;
	private String descricao;
	private DominioSituacao indSituacao;
	private Boolean indPlacar;
	private Integer cidSeq;
	private String cidCodigo;


	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public Boolean getIndPlacar() {
		return indPlacar;
	}

	public void setIndPlacar(Boolean indPlacar) {
		this.indPlacar = indPlacar;
	}

	public Integer getCidSeq() {
		return cidSeq;
	}

	public void setCidSeq(Integer cidSeq) {
		this.cidSeq = cidSeq;
	}

	public String getCidCodigo() {
		return cidCodigo;
	}

	public void setCidCodigo(String cidCodigo) {
		this.cidCodigo = cidCodigo;
	}
	
	public enum Fields {

		SEQ("seq"),
		DESCRICAO("descricao"),
		IND_SITUACAO("indSituacao"),
		IND_PLACAR("indPlacar");

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
