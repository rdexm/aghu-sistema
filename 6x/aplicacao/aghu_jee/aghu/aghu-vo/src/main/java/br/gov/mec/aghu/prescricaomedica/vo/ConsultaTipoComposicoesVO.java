package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSituacao;

public class ConsultaTipoComposicoesVO implements Serializable {

	private static final long serialVersionUID = 8739851016850665687L;
	
	private Short seq;
	private String descricao;
	private Boolean indProducao = Boolean.FALSE;
	private DominioSituacao indSituacao;
	private Short ordem;
	private Date criadoEm;
	private String criadoPor;

	public ConsultaTipoComposicoesVO() {

	}

	/**
	 * FIELDS
	 */
	public enum Fields {
		SEQ("seq"),
		DESCRICAO("descricao"),
		IND_PRODUCAO("indProducao"),
		IND_SITUACAO("indSituacao"),
		NOME("criadoPor"),
		ORDEM("ordem"),
		CRIADO_EM("criadoEm");

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

	public Boolean getIndProducao() {
		return indProducao;
	}

	public void setIndProducao(Boolean indProducao) {
		this.indProducao = indProducao;
	}

	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public String getCriadoPor() {
		return criadoPor;
	}

	public void setCriadoPor(String criadoPor) {
		this.criadoPor = criadoPor;
	}

	public Short getOrdem() {
		return ordem;
	}

	public void setOrdem(Short ordem) {
		this.ordem = ordem;
	}
}
