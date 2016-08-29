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


import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

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
@Table(name = "ANU_CONFERENCIA_MAMADEIRAS", schema = "AGH")
public class AnuConferenciaMamadeira extends BaseEntityId<AnuConferenciaMamadeiraId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -326059028085922449L;
	private AnuConferenciaMamadeiraId id;
	private Integer version;
	private AnuHorarioMamadeira anuHorarioMamadeira;
	private RapServidores rapServidores;
	private Date dthr;
	private String indQualidadeOk;

	public AnuConferenciaMamadeira() {
	}

	public AnuConferenciaMamadeira(AnuConferenciaMamadeiraId id, AnuHorarioMamadeira anuHorarioMamadeira,
			RapServidores rapServidores, Date dthr, String indQualidadeOk) {
		this.id = id;
		this.anuHorarioMamadeira = anuHorarioMamadeira;
		this.rapServidores = rapServidores;
		this.dthr = dthr;
		this.indQualidadeOk = indQualidadeOk;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "hmaDmaAtdSeq", column = @Column(name = "HMA_DMA_ATD_SEQ", nullable = false)),
			@AttributeOverride(name = "hmaDmaSeq", column = @Column(name = "HMA_DMA_SEQ", nullable = false)),
			@AttributeOverride(name = "hmaSeq", column = @Column(name = "HMA_SEQ", nullable = false)),
			@AttributeOverride(name = "seq", column = @Column(name = "SEQ", nullable = false)) })
	public AnuConferenciaMamadeiraId getId() {
		return this.id;
	}

	public void setId(AnuConferenciaMamadeiraId id) {
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
	@JoinColumns({
			@JoinColumn(name = "HMA_DMA_ATD_SEQ", referencedColumnName = "DMA_ATD_SEQ", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "HMA_DMA_SEQ", referencedColumnName = "DMA_SEQ", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "HMA_SEQ", referencedColumnName = "SEQ", nullable = false, insertable = false, updatable = false) })
	public AnuHorarioMamadeira getAnuHorarioMamadeira() {
		return this.anuHorarioMamadeira;
	}

	public void setAnuHorarioMamadeira(AnuHorarioMamadeira anuHorarioMamadeira) {
		this.anuHorarioMamadeira = anuHorarioMamadeira;
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

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR", nullable = false, length = 29)
	public Date getDthr() {
		return this.dthr;
	}

	public void setDthr(Date dthr) {
		this.dthr = dthr;
	}

	@Column(name = "IND_QUALIDADE_OK", nullable = false, length = 1)
	@Length(max = 1)
	public String getIndQualidadeOk() {
		return this.indQualidadeOk;
	}

	public void setIndQualidadeOk(String indQualidadeOk) {
		this.indQualidadeOk = indQualidadeOk;
	}

	public enum Fields {

		ID("id"),
		VERSION("version"),
		ANU_HORARIO_MAMADEIRAS("anuHorarioMamadeira"),
		RAP_SERVIDORES("rapServidores"),
		DTHR("dthr"),
		IND_QUALIDADE_OK("indQualidadeOk");

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
		if (!(obj instanceof AnuConferenciaMamadeira)) {
			return false;
		}
		AnuConferenciaMamadeira other = (AnuConferenciaMamadeira) obj;
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
