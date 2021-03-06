package br.gov.mec.aghu.model;

// Generated 19/03/2010 17:20:15 by Hibernate Tools 3.2.5.Beta

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;
import br.gov.mec.aghu.dominio.DominioIndAbsenteismo;
import br.gov.mec.aghu.dominio.DominioSituacao;

/**
 * AacRetornos generated by hbm2java
 */
@Entity

@Table(name = "AAC_RETORNOS", schema = "AGH")
public class AacRetornos extends BaseEntitySeq<Integer> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 730328750276159226L;
	private Integer seq;
	private String descricao;
	private DominioSituacao situacao;
	private DominioIndAbsenteismo absenteismo;
	private Boolean faturaSus;
	private Boolean ausenteAmbu;
	private Set<AacConsultas> consultas = new HashSet<AacConsultas>(0);

	public AacRetornos() {
	}

	public AacRetornos(Integer seq, String descricao, DominioSituacao situacao,
			DominioIndAbsenteismo absenteismo, Boolean faturaSus, Boolean ausenteAmbu) {
		this.seq = seq;
		this.descricao = descricao;
		this.situacao = situacao;
		this.absenteismo = absenteismo;
		this.faturaSus = faturaSus;
		this.ausenteAmbu = ausenteAmbu;
	}

	@Id
	@Column(name = "SEQ", nullable = false, precision = 3, scale = 0)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 25)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacao() {
		return this.situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	@Column(name = "IND_ABSENTEISMO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioIndAbsenteismo getAbsenteismo() {
		return this.absenteismo;
	}

	public void setAbsenteismo(DominioIndAbsenteismo absenteismo) {
		this.absenteismo = absenteismo;
	}

	@Column(name = "IND_FATURA_SUS", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getFaturaSus() {
		return this.faturaSus;
	}

	public void setFaturaSus(Boolean faturaSus) {
		this.faturaSus = faturaSus;
	}

	@Column(name = "IND_AUSENTE_AMBU", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getAusenteAmbu() {
		return this.ausenteAmbu;
	}

	public void setAusenteAmbu(Boolean ausenteAmbu) {
		this.ausenteAmbu = ausenteAmbu;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "retorno")
	public Set<AacConsultas> getConsultas() {
		return this.consultas;
	}

	public void setConsultas(Set<AacConsultas> consultas) {
		this.consultas = consultas;
	}
	
	public enum Fields {
		SEQ("seq"),DESCRICAO("descricao"), IND_SITUACAO("situacao"),
		IND_ABSENTEISMO("absenteismo"), IND_FATURA_SUS("faturaSus"),IND_AUSENTE_AMBU("ausenteAmbu");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
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
		if (!(obj instanceof AacRetornos)) {
			return false;
		}
		AacRetornos other = (AacRetornos) obj;
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
