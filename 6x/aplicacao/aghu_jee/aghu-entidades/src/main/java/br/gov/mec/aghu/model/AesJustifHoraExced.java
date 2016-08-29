package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

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
import javax.persistence.Version;


import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * ================================================================================
 *   ####   #####    ####   ######  #####   ##  ##   ####    ####    ####    #### 
 *  ##  ##  ##  ##  ##      ##      ##  ##  ##  ##  ##  ##  ##  ##  ##  ##  ##  ##
 *  ##  ##  #####    ####   ####    #####   ##  ##  ######  ##      ######  ##  ##
 *  ##  ##  ##  ##      ##  ##      ##  ##   ####   ##  ##  ##  ##  ##  ##  ##  ##
 *   ####   #####    ####   ######  ##  ##    ##    ##  ##   ####   ##  ##   #### 
 * ================================================================================
 *
 * A partir de uma análise originada pela tarefa #19993
 * esta model foi escolhida para ser apenas de leitura
 * no AGHU e por isso foi anotada como Immutable.
 *
 * Entretanto, caso esta entidade seja necessária na construção
 * de uma estória que necessite escrever dados no banco, este
 * comentário e esta anotação pode ser retirada desta model.
 */
@Immutable

@Entity
@Table(name = "AES_JUSTIF_HORAS_EXCED", schema = "AGH")
public class AesJustifHoraExced extends BaseEntityId<AesJustifHoraExcedId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7616006995458281170L;
	private AesJustifHoraExcedId id;
	private Integer version;
	private AesJustifHora aesJustifHora;
	private RapServidores rapServidores;
	private AesExtratoHoraExcedente aesExtratoHoraExcedente;

	public AesJustifHoraExced() {
	}

	public AesJustifHoraExced(AesJustifHoraExcedId id, AesJustifHora aesJustifHora,
			AesExtratoHoraExcedente aesExtratoHoraExcedente) {
		this.id = id;
		this.aesJustifHora = aesJustifHora;
		this.aesExtratoHoraExcedente = aesExtratoHoraExcedente;
	}

	public AesJustifHoraExced(AesJustifHoraExcedId id, AesJustifHora aesJustifHora, RapServidores rapServidores,
			AesExtratoHoraExcedente aesExtratoHoraExcedente) {
		this.id = id;
		this.aesJustifHora = aesJustifHora;
		this.rapServidores = rapServidores;
		this.aesExtratoHoraExcedente = aesExtratoHoraExcedente;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "eheSerMatricula", column = @Column(name = "EHE_SER_MATRICULA", nullable = false)),
			@AttributeOverride(name = "eheSerVinCodigo", column = @Column(name = "EHE_SER_VIN_CODIGO", nullable = false)),
			@AttributeOverride(name = "eheSequencia", column = @Column(name = "EHE_SEQUENCIA", nullable = false)),
			@AttributeOverride(name = "jhoCodigo", column = @Column(name = "JHO_CODIGO", nullable = false)) })
	public AesJustifHoraExcedId getId() {
		return this.id;
	}

	public void setId(AesJustifHoraExcedId id) {
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
	@JoinColumn(name = "JHO_CODIGO", nullable = false, insertable = false, updatable = false)
	public AesJustifHora getAesJustifHora() {
		return this.aesJustifHora;
	}

	public void setAesJustifHora(AesJustifHora aesJustifHora) {
		this.aesJustifHora = aesJustifHora;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getRapServidores() {
		return this.rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "EHE_SER_MATRICULA", referencedColumnName = "SER_MATRICULA", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "EHE_SER_VIN_CODIGO", referencedColumnName = "SER_VIN_CODIGO", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "EHE_SEQUENCIA", referencedColumnName = "SEQUENCIA", nullable = false, insertable = false, updatable = false) })
	public AesExtratoHoraExcedente getAesExtratoHoraExcedente() {
		return this.aesExtratoHoraExcedente;
	}

	public void setAesExtratoHoraExcedente(AesExtratoHoraExcedente aesExtratoHoraExcedente) {
		this.aesExtratoHoraExcedente = aesExtratoHoraExcedente;
	}

	public enum Fields {

		ID("id"),
		VERSION("version"),
		AES_JUSTIF_HORAS("aesJustifHora"),
		RAP_SERVIDORES("rapServidores"),
		AES_EXTRATOS_HORAS_EXCEDENT("aesExtratoHoraExcedente");

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
		if (!(obj instanceof AesJustifHoraExced)) {
			return false;
		}
		AesJustifHoraExced other = (AesJustifHoraExced) obj;
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
