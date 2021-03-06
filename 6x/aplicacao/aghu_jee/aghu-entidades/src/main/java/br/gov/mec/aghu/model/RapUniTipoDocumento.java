package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

/**
 * RapUniTipoDocumento generated by hbm2java
 */
@Entity
@SequenceGenerator(name="rapTdoSq1", sequenceName="AGH.RAP_TDO_SQ1", allocationSize = 1)
@Table(name = "RAP_UNI_TIPO_DOCUMENTOS", schema = "AGH")
public class RapUniTipoDocumento extends BaseEntitySeq<Short> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3747317542501635370L;
	private Short seq;
	private Integer version;
	private String descricao;
	private String indSituacao;
	private Set<RapUniDepDocumento> rapUniDepDocumentoes = new HashSet<RapUniDepDocumento>(0);

	public RapUniTipoDocumento() {
	}

	public RapUniTipoDocumento(Short seq, String descricao) {
		this.seq = seq;
		this.descricao = descricao;
	}

	public RapUniTipoDocumento(Short seq, String descricao, String indSituacao, Set<RapUniDepDocumento> rapUniDepDocumentoes) {
		this.seq = seq;
		this.descricao = descricao;
		this.indSituacao = indSituacao;
		this.rapUniDepDocumentoes = rapUniDepDocumentoes;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "rapTdoSq1")
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

	@Column(name = "IND_SITUACAO", length = 1)
	@Length(max = 1)
	public String getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(String indSituacao) {
		this.indSituacao = indSituacao;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "rapUniTipoDocumento")
	public Set<RapUniDepDocumento> getRapUniDepDocumentoes() {
		return this.rapUniDepDocumentoes;
	}

	public void setRapUniDepDocumentoes(Set<RapUniDepDocumento> rapUniDepDocumentoes) {
		this.rapUniDepDocumentoes = rapUniDepDocumentoes;
	}

	public enum Fields {

		SEQ("seq"),
		VERSION("version"),
		DESCRICAO("descricao"),
		IND_SITUACAO("indSituacao"),
		RAP_UNI_DEP_DOCUMENTOES("rapUniDepDocumentoes");

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
		if (!(obj instanceof RapUniTipoDocumento)) {
			return false;
		}
		RapUniTipoDocumento other = (RapUniTipoDocumento) obj;
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
