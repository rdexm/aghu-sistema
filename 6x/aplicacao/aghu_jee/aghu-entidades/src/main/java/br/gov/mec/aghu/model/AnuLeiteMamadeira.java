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
@Table(name = "ANU_LEITE_MAMADEIRAS", schema = "AGH")
public class AnuLeiteMamadeira extends BaseEntityId<AnuLeiteMamadeiraId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -766201658406473286L;
	private AnuLeiteMamadeiraId id;
	private Integer version;
	private AnuDietaMamadeira anuDietaMamadeira;
	private AnuTipoLeite anuTipoLeite;
	private RapServidores rapServidoresByAnuLmaSerFk2;
	private RapServidores rapServidoresByAnuLmaSerFk3;
	private RapServidores rapServidoresByAnuLmaSerFk1;
	private Date criadoEm;
	private Date dthrInicio;
	private Short volume;
	private Float percentual;
	private Date dthrFim;
	private String indSuspenso;
	private Date alteradoEm;

	public AnuLeiteMamadeira() {
	}

	public AnuLeiteMamadeira(AnuLeiteMamadeiraId id, AnuDietaMamadeira anuDietaMamadeira, AnuTipoLeite anuTipoLeite,
			RapServidores rapServidoresByAnuLmaSerFk2, Date criadoEm, Date dthrInicio, String indSuspenso) {
		this.id = id;
		this.anuDietaMamadeira = anuDietaMamadeira;
		this.anuTipoLeite = anuTipoLeite;
		this.rapServidoresByAnuLmaSerFk2 = rapServidoresByAnuLmaSerFk2;
		this.criadoEm = criadoEm;
		this.dthrInicio = dthrInicio;
		this.indSuspenso = indSuspenso;
	}

	public AnuLeiteMamadeira(AnuLeiteMamadeiraId id, AnuDietaMamadeira anuDietaMamadeira, AnuTipoLeite anuTipoLeite,
			RapServidores rapServidoresByAnuLmaSerFk2, RapServidores rapServidoresByAnuLmaSerFk3,
			RapServidores rapServidoresByAnuLmaSerFk1, Date criadoEm, Date dthrInicio, Short volume, Float percentual, Date dthrFim,
			String indSuspenso, Date alteradoEm) {
		this.id = id;
		this.anuDietaMamadeira = anuDietaMamadeira;
		this.anuTipoLeite = anuTipoLeite;
		this.rapServidoresByAnuLmaSerFk2 = rapServidoresByAnuLmaSerFk2;
		this.rapServidoresByAnuLmaSerFk3 = rapServidoresByAnuLmaSerFk3;
		this.rapServidoresByAnuLmaSerFk1 = rapServidoresByAnuLmaSerFk1;
		this.criadoEm = criadoEm;
		this.dthrInicio = dthrInicio;
		this.volume = volume;
		this.percentual = percentual;
		this.dthrFim = dthrFim;
		this.indSuspenso = indSuspenso;
		this.alteradoEm = alteradoEm;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "dmaAtdSeq", column = @Column(name = "DMA_ATD_SEQ", nullable = false)),
			@AttributeOverride(name = "dmaSeq", column = @Column(name = "DMA_SEQ", nullable = false)),
			@AttributeOverride(name = "tleSeq", column = @Column(name = "TLE_SEQ", nullable = false)),
			@AttributeOverride(name = "seq", column = @Column(name = "SEQ", nullable = false)) })
	public AnuLeiteMamadeiraId getId() {
		return this.id;
	}

	public void setId(AnuLeiteMamadeiraId id) {
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
	@JoinColumn(name = "TLE_SEQ", nullable = false, insertable = false, updatable = false)
	public AnuTipoLeite getAnuTipoLeite() {
		return this.anuTipoLeite;
	}

	public void setAnuTipoLeite(AnuTipoLeite anuTipoLeite) {
		this.anuTipoLeite = anuTipoLeite;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getRapServidoresByAnuLmaSerFk2() {
		return this.rapServidoresByAnuLmaSerFk2;
	}

	public void setRapServidoresByAnuLmaSerFk2(RapServidores rapServidoresByAnuLmaSerFk2) {
		this.rapServidoresByAnuLmaSerFk2 = rapServidoresByAnuLmaSerFk2;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA_SUSPENSO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_SUSPENSO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getRapServidoresByAnuLmaSerFk3() {
		return this.rapServidoresByAnuLmaSerFk3;
	}

	public void setRapServidoresByAnuLmaSerFk3(RapServidores rapServidoresByAnuLmaSerFk3) {
		this.rapServidoresByAnuLmaSerFk3 = rapServidoresByAnuLmaSerFk3;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA_MOVIMENTADO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_MOVIMENTADO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getRapServidoresByAnuLmaSerFk1() {
		return this.rapServidoresByAnuLmaSerFk1;
	}

	public void setRapServidoresByAnuLmaSerFk1(RapServidores rapServidoresByAnuLmaSerFk1) {
		this.rapServidoresByAnuLmaSerFk1 = rapServidoresByAnuLmaSerFk1;
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

	@Column(name = "IND_SUSPENSO", nullable = false, length = 1)
	@Length(max = 1)
	public String getIndSuspenso() {
		return this.indSuspenso;
	}

	public void setIndSuspenso(String indSuspenso) {
		this.indSuspenso = indSuspenso;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ALTERADO_EM", length = 29)
	public Date getAlteradoEm() {
		return this.alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}

	public enum Fields {

		ID("id"),
		VERSION("version"),
		ANU_DIETA_MAMADEIRAS("anuDietaMamadeira"),
		ANU_TIPO_LEITES("anuTipoLeite"),
		RAP_SERVIDORES_BY_ANU_LMA_SER_FK2("rapServidoresByAnuLmaSerFk2"),
		RAP_SERVIDORES_BY_ANU_LMA_SER_FK3("rapServidoresByAnuLmaSerFk3"),
		RAP_SERVIDORES_BY_ANU_LMA_SER_FK1("rapServidoresByAnuLmaSerFk1"),
		CRIADO_EM("criadoEm"),
		DTHR_INICIO("dthrInicio"),
		VOLUME("volume"),
		PERCENTUAL("percentual"),
		DTHR_FIM("dthrFim"),
		IND_SUSPENSO("indSuspenso"),
		ALTERADO_EM("alteradoEm");

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
		if (!(obj instanceof AnuLeiteMamadeira)) {
			return false;
		}
		AnuLeiteMamadeira other = (AnuLeiteMamadeira) obj;
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
