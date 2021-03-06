package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

/**
 * RarProjetoPrograma generated by hbm2java
 */
@Entity
@Table(name = "RAR_PROJETO_PROGRAMAS", schema = "AGH")
public class RarProjetoPrograma extends BaseEntitySeq<Short> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2674114026630719073L;
	private Short seq;
	private Integer version;
	private String descricao;
	private Set<RarPrograma> rarProgramaes = new HashSet<RarPrograma>(0);

	public RarProjetoPrograma() {
	}

	public RarProjetoPrograma(Short seq, String descricao) {
		this.seq = seq;
		this.descricao = descricao;
	}

	public RarProjetoPrograma(Short seq, String descricao, Set<RarPrograma> rarProgramaes) {
		this.seq = seq;
		this.descricao = descricao;
		this.rarProgramaes = rarProgramaes;
	}

	@Id
	@Column(name = "SEQ", unique = true, nullable = false)
	public Short getSeq() {
		return this.seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 60)
	@Length(max = 60)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rarProjetoPrograma")
	public Set<RarPrograma> getRarProgramaes() {
		return this.rarProgramaes;
	}

	public void setRarProgramaes(Set<RarPrograma> rarProgramaes) {
		this.rarProgramaes = rarProgramaes;
	}

	public enum Fields {

		SEQ("seq"),
		VERSION("version"),
		DESCRICAO("descricao"),
		RAR_PROGRAMAES("rarProgramaes");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}


	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getSeq() == null) ? 0 : getSeq().hashCode());
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
		if (!(obj instanceof RarProjetoPrograma)) {
			return false;
		}
		RarProjetoPrograma other = (RarProjetoPrograma) obj;
		if (getSeq() == null) {
			if (other.getSeq() != null) {
				return false;
			}
		} else if (!getSeq().equals(other.getSeq())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}
