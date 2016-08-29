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
@Table(name = "ANU_OLEO_MAMADEIRAS", schema = "AGH")
public class AnuOleoMamadeira extends BaseEntityId<AnuOleoMamadeiraId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -37929505830630309L;
	private AnuOleoMamadeiraId id;
	private Integer version;
	private AnuDietaMamadeira anuDietaMamadeira;
	private RapServidores rapServidoresByAnuOmaSerFk2;
	private RapServidores rapServidoresByAnuOmaSerFk3;
	private AnuTipoOleo anuTipoOleo;
	private RapServidores rapServidoresByAnuOmaSerFk1;
	private Date criadoEm;
	private Date dthrInicio;
	private Short volume;
	private Float percentual;
	private Date dthrFim;
	private Date alteradoEm;
	private String indSuspenso;

	public AnuOleoMamadeira() {
	}

	public AnuOleoMamadeira(AnuOleoMamadeiraId id, AnuDietaMamadeira anuDietaMamadeira, RapServidores rapServidoresByAnuOmaSerFk2,
			AnuTipoOleo anuTipoOleo, Date criadoEm, Date dthrInicio, String indSuspenso) {
		this.id = id;
		this.anuDietaMamadeira = anuDietaMamadeira;
		this.rapServidoresByAnuOmaSerFk2 = rapServidoresByAnuOmaSerFk2;
		this.anuTipoOleo = anuTipoOleo;
		this.criadoEm = criadoEm;
		this.dthrInicio = dthrInicio;
		this.indSuspenso = indSuspenso;
	}

	public AnuOleoMamadeira(AnuOleoMamadeiraId id, AnuDietaMamadeira anuDietaMamadeira, RapServidores rapServidoresByAnuOmaSerFk2,
			RapServidores rapServidoresByAnuOmaSerFk3, AnuTipoOleo anuTipoOleo, RapServidores rapServidoresByAnuOmaSerFk1,
			Date criadoEm, Date dthrInicio, Short volume, Float percentual, Date dthrFim, Date alteradoEm, String indSuspenso) {
		this.id = id;
		this.anuDietaMamadeira = anuDietaMamadeira;
		this.rapServidoresByAnuOmaSerFk2 = rapServidoresByAnuOmaSerFk2;
		this.rapServidoresByAnuOmaSerFk3 = rapServidoresByAnuOmaSerFk3;
		this.anuTipoOleo = anuTipoOleo;
		this.rapServidoresByAnuOmaSerFk1 = rapServidoresByAnuOmaSerFk1;
		this.criadoEm = criadoEm;
		this.dthrInicio = dthrInicio;
		this.volume = volume;
		this.percentual = percentual;
		this.dthrFim = dthrFim;
		this.alteradoEm = alteradoEm;
		this.indSuspenso = indSuspenso;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "dmaAtdSeq", column = @Column(name = "DMA_ATD_SEQ", nullable = false)),
			@AttributeOverride(name = "dmaSeq", column = @Column(name = "DMA_SEQ", nullable = false)),
			@AttributeOverride(name = "tolSeq", column = @Column(name = "TOL_SEQ", nullable = false)),
			@AttributeOverride(name = "seq", column = @Column(name = "SEQ", nullable = false)) })
	public AnuOleoMamadeiraId getId() {
		return this.id;
	}

	public void setId(AnuOleoMamadeiraId id) {
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
			@JoinColumn(name = "DMA_ATD_SEQ", referencedColumnName = "ATD_SEQ", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "DMA_SEQ", referencedColumnName = "SEQ", nullable = false, insertable = false, updatable = false) })
	public AnuDietaMamadeira getAnuDietaMamadeira() {
		return this.anuDietaMamadeira;
	}

	public void setAnuDietaMamadeira(AnuDietaMamadeira anuDietaMamadeira) {
		this.anuDietaMamadeira = anuDietaMamadeira;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getRapServidoresByAnuOmaSerFk2() {
		return this.rapServidoresByAnuOmaSerFk2;
	}

	public void setRapServidoresByAnuOmaSerFk2(RapServidores rapServidoresByAnuOmaSerFk2) {
		this.rapServidoresByAnuOmaSerFk2 = rapServidoresByAnuOmaSerFk2;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA_SUSPENSO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_SUSPENSO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getRapServidoresByAnuOmaSerFk3() {
		return this.rapServidoresByAnuOmaSerFk3;
	}

	public void setRapServidoresByAnuOmaSerFk3(RapServidores rapServidoresByAnuOmaSerFk3) {
		this.rapServidoresByAnuOmaSerFk3 = rapServidoresByAnuOmaSerFk3;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TOL_SEQ", nullable = false, insertable = false, updatable = false)
	public AnuTipoOleo getAnuTipoOleo() {
		return this.anuTipoOleo;
	}

	public void setAnuTipoOleo(AnuTipoOleo anuTipoOleo) {
		this.anuTipoOleo = anuTipoOleo;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA_MOVIMENTADO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_MOVIMENTADO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getRapServidoresByAnuOmaSerFk1() {
		return this.rapServidoresByAnuOmaSerFk1;
	}

	public void setRapServidoresByAnuOmaSerFk1(RapServidores rapServidoresByAnuOmaSerFk1) {
		this.rapServidoresByAnuOmaSerFk1 = rapServidoresByAnuOmaSerFk1;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_INICIO", nullable = false, length = 29)
	public Date getDthrInicio() {
		return this.dthrInicio;
	}

	public void setDthrInicio(Date dthrInicio) {
		this.dthrInicio = dthrInicio;
	}

	@Column(name = "VOLUME")
	public Short getVolume() {
		return this.volume;
	}

	public void setVolume(Short volume) {
		this.volume = volume;
	}

	@Column(name = "PERCENTUAL", precision = 8, scale = 8)
	public Float getPercentual() {
		return this.percentual;
	}

	public void setPercentual(Float percentual) {
		this.percentual = percentual;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_FIM", length = 29)
	public Date getDthrFim() {
		return this.dthrFim;
	}

	public void setDthrFim(Date dthrFim) {
		this.dthrFim = dthrFim;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ALTERADO_EM", length = 29)
	public Date getAlteradoEm() {
		return this.alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}

	@Column(name = "IND_SUSPENSO", nullable = false, length = 1)
	@Length(max = 1)
	public String getIndSuspenso() {
		return this.indSuspenso;
	}

	public void setIndSuspenso(String indSuspenso) {
		this.indSuspenso = indSuspenso;
	}

	public enum Fields {

		ID("id"),
		VERSION("version"),
		ANU_DIETA_MAMADEIRAS("anuDietaMamadeira"),
		RAP_SERVIDORES_BY_ANU_OMA_SER_FK2("rapServidoresByAnuOmaSerFk2"),
		RAP_SERVIDORES_BY_ANU_OMA_SER_FK3("rapServidoresByAnuOmaSerFk3"),
		ANU_TIPO_OLEOS("anuTipoOleo"),
		RAP_SERVIDORES_BY_ANU_OMA_SER_FK1("rapServidoresByAnuOmaSerFk1"),
		CRIADO_EM("criadoEm"),
		DTHR_INICIO("dthrInicio"),
		VOLUME("volume"),
		PERCENTUAL("percentual"),
		DTHR_FIM("dthrFim"),
		ALTERADO_EM("alteradoEm"),
		IND_SUSPENSO("indSuspenso");

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
		if (!(obj instanceof AnuOleoMamadeira)) {
			return false;
		}
		AnuOleoMamadeira other = (AnuOleoMamadeira) obj;
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
