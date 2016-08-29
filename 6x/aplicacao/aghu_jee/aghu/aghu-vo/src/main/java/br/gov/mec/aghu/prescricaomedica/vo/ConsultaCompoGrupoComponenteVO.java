package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSituacao;

public class ConsultaCompoGrupoComponenteVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7149315618819981766L;
	
	private Short seq;
	private Short ticSeq;
	private String descricao;
	private DominioSituacao indSituacao;
	private Date criadoEm;
	private String criadoPor;

	public ConsultaCompoGrupoComponenteVO() {

	}

	/**
	 * FIELDS
	 */
	public enum Fields {
		SEQ("seq"),
		DESCRICAO("descricao"),
		IND_SITUACAO("indSituacao"),
		NOME("criadoPor"),
		TIC_SEQ("ticSeq"),
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

	public Short getTicSeq() {
		return ticSeq;
	}

	public void setTicSeq(Short ticSeq) {
		this.ticSeq = ticSeq;
	}

}
