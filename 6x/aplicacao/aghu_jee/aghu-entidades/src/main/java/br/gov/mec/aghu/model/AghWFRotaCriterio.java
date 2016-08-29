package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;


import br.gov.mec.aghu.core.persistence.BaseEntitySeq;



@Entity
@Table(name = "AGH_WF_ROTA_CRITERIOS", schema = "AGH")
@SequenceGenerator(name = "AghWFRotaCriterioSequence", sequenceName ="AGH.AGH_WRC_SEQ")
public class AghWFRotaCriterio extends BaseEntitySeq<Integer> implements java.io.Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2200048616985673633L;
	private Integer seq;
	private String codigo;
	private String descricao;
	private Integer version;


	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator ="AghWFRotaCriterioSequence")
	@Column(name = "SEQ", nullable = false)
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Version
	@Column(name = "VERSION")
	public Integer getVersion() {
		return version;
	}
	
	public void setVersion(Integer version) {
		this.version = version;
	}
	
	@Column(name = "CODIGO", nullable = false, length = 10)
	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	
	@Column(name = "DESCRICAO", length = 100)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}	
	
	public enum Fields {
		SEQ("seq"),
		CODIGO("codigo"),
		DESCRICAO("descricao"),
		VERSION("version");		
		
		private String fields;
		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
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
		AghWFRotaCriterio other = (AghWFRotaCriterio) obj;
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
