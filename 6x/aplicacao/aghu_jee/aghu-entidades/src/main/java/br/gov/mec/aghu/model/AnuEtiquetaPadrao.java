package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * AnuEtiquetaPadrao generated by hbm2java
 */
@Entity
@Table(name = "ANU_ETIQUETA_PADROES", schema = "AGH")
public class AnuEtiquetaPadrao extends BaseEntityId<AnuEtiquetaPadraoId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7317389060620191396L;
	private AnuEtiquetaPadraoId id;
	private Integer version;
	private RapServidores rapServidores;
	private AnuRefeicao anuRefeicao;
	private AnuHabitoAlimUsual anuHabitoAlimUsual;
	private String descricao;
	private Date criadoEm;

	public AnuEtiquetaPadrao() {
	}

	public AnuEtiquetaPadrao(AnuEtiquetaPadraoId id, RapServidores rapServidores, AnuRefeicao anuRefeicao,
			AnuHabitoAlimUsual anuHabitoAlimUsual, String descricao, Date criadoEm) {
		this.id = id;
		this.rapServidores = rapServidores;
		this.anuRefeicao = anuRefeicao;
		this.anuHabitoAlimUsual = anuHabitoAlimUsual;
		this.descricao = descricao;
		this.criadoEm = criadoEm;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "hauSeq", column = @Column(name = "HAU_SEQ", nullable = false)),
			@AttributeOverride(name = "refSeq", column = @Column(name = "REF_SEQ", nullable = false)) })
	public AnuEtiquetaPadraoId getId() {
		return this.id;
	}

	public void setId(AnuEtiquetaPadraoId id) {
		this.id = id;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getRapServidores() {
		return this.rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "REF_SEQ", nullable = false, insertable = false, updatable = false)
	public AnuRefeicao getAnuRefeicao() {
		return this.anuRefeicao;
	}

	public void setAnuRefeicao(AnuRefeicao anuRefeicao) {
		this.anuRefeicao = anuRefeicao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "HAU_SEQ", nullable = false, insertable = false, updatable = false)
	public AnuHabitoAlimUsual getAnuHabitoAlimUsual() {
		return this.anuHabitoAlimUsual;
	}

	public void setAnuHabitoAlimUsual(AnuHabitoAlimUsual anuHabitoAlimUsual) {
		this.anuHabitoAlimUsual = anuHabitoAlimUsual;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 400)
	@Length(max = 400)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public enum Fields {

		ID("id"),
		VERSION("version"),
		RAP_SERVIDORES("rapServidores"),
		ANU_REFEICOES("anuRefeicao"),
		ANU_HABITO_ALIM_USUAIS("anuHabitoAlimUsual"),
		DESCRICAO("descricao"),
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


	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
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
		if (!(obj instanceof AnuEtiquetaPadrao)) {
			return false;
		}
		AnuEtiquetaPadrao other = (AnuEtiquetaPadrao) obj;
		if (getId() == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!getId().equals(other.getId())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}
