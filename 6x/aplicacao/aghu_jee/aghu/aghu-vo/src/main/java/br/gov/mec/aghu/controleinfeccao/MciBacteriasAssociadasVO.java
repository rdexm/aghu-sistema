package br.gov.mec.aghu.controleinfeccao;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MciBacteriaMultir;
import br.gov.mec.aghu.core.commons.BaseBean;


public class MciBacteriasAssociadasVO implements BaseBean {

	private static final long serialVersionUID = -2755258515971423321L;
	/*
	 *  
	 */
	private Integer seq;
	private String descricao;
	private DominioSituacao indSituacao;
	private Boolean situacao;
	private Date criadoAlterado;
	private Date criadoEm;
	private MciBacteriaMultir mciBacteriaMultir;

	public enum Fields {

		SEQ("seq"),
		DESCRICAO("descricao"),
		IND_SITUACAO("indSituacao");

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
	
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public Boolean getSituacao() {
		return situacao;
	}

	public void setSituacao(Boolean situacao) {
		this.situacao = situacao;
	}	
	
	public MciBacteriaMultir getMciBacteriaMultir() {
		return mciBacteriaMultir;
	}

	public void setMciBacteriaMultir(MciBacteriaMultir mciBacteriaMultir) {
		this.mciBacteriaMultir = mciBacteriaMultir;
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
		MciBacteriasAssociadasVO other = (MciBacteriasAssociadasVO) obj;
		if (seq == null) {
			if (other.seq != null) {
				return false;
			}
		} else if (!seq.equals(other.seq)) {
			return false;
		}
		return true;
	}
}
