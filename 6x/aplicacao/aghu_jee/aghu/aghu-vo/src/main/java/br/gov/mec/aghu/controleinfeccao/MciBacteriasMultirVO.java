package br.gov.mec.aghu.controleinfeccao;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.commons.BaseBean;


public class MciBacteriasMultirVO implements BaseBean {

	private static final long serialVersionUID = -2755258515971423321L;
	/*
	 *  
	 */
	private Integer seq;
	private String descricao;
	private DominioSituacao situacao;
	private Integer bmrSeq;
	private Date criadoAlterado;
	private Date criadoEm;
	
	public enum Fields {

		SEQ("seq"),
		DESCRICAO("descricao"),
		BMR_SEQ("bmrSeq"),
		SITUACAO("situacao");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}
	
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

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public Date getCriadoAlterado() {
		return criadoAlterado;
	}

	public void setCriadoAlterado(Date criadoAlterado) {
		this.criadoAlterado = criadoAlterado;
	}

	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		MciBacteriasMultirVO other = (MciBacteriasMultirVO) obj;
		if (seq == null) {
			if (other.seq != null) {
				return false;
			}
		} else if (!seq.equals(other.seq)) {
			return false;
		}
		return true;
	}

	public Integer getBmrSeq() {
		return bmrSeq;
	}

	public void setBmrSeq(Integer bmrSeq) {
		this.bmrSeq = bmrSeq;
	}
}
