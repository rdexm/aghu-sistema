package br.gov.mec.aghu.controleinfeccao.vo;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.commons.BaseBean;


public class GrupoReportRotinaCciVO implements BaseBean {
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 2274588419490569684L;
	private Short seq;
	private String descricao;
	private Boolean indMensal;
	private Boolean indSemanal;
	private DominioSituacao situacao;
	
	
	
	public GrupoReportRotinaCciVO() {
		super();
	}
	
	public enum Fields {

		SEQ("seq"),
		DESCRICAO("descricao"),
		MENSAL("indMensal"),
		SITUACAO("situacao"),
		SEMANAL("indSemanal");

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

	public Boolean getIndMensal() {
		return indMensal;
	}

	public void setIndMensal(Boolean indMensal) {
		this.indMensal = indMensal;
	}

	public Boolean getIndSemanal() {
		return indSemanal;
	}

	public void setIndSemanal(Boolean indSemanal) {
		this.indSemanal = indSemanal;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}
}