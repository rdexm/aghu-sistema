package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
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
@Table(name = "ANU_HORARIO_MAMADEIRAS", schema = "AGH")
public class AnuHorarioMamadeira extends BaseEntityId<AnuHorarioMamadeiraId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3453253126503952665L;
	private AnuHorarioMamadeiraId id;
	private Integer version;
	private AnuDietaMamadeira anuDietaMamadeira;
	private RapServidores rapServidoresByAnuHmaSerFk1;
	private RapServidores rapServidoresByAnuHmaSerFk3;
	private RapServidores rapServidoresByAnuHmaSerFk2;
	private Date criadoEm;
	private Date horario;
	private Date dthrInicio;
	private Date dthrFim;
	private String indSuspenso;
	private Date alteradoEm;
	private Set<AnuConferenciaMamadeira> anuConferenciaMamadeiraes = new HashSet<AnuConferenciaMamadeira>(0);

	public AnuHorarioMamadeira() {
	}

	public AnuHorarioMamadeira(AnuHorarioMamadeiraId id, AnuDietaMamadeira anuDietaMamadeira,
			RapServidores rapServidoresByAnuHmaSerFk1, Date criadoEm, Date horario, Date dthrInicio, String indSuspenso) {
		this.id = id;
		this.anuDietaMamadeira = anuDietaMamadeira;
		this.rapServidoresByAnuHmaSerFk1 = rapServidoresByAnuHmaSerFk1;
		this.criadoEm = criadoEm;
		this.horario = horario;
		this.dthrInicio = dthrInicio;
		this.indSuspenso = indSuspenso;
	}

	public AnuHorarioMamadeira(AnuHorarioMamadeiraId id, AnuDietaMamadeira anuDietaMamadeira,
			RapServidores rapServidoresByAnuHmaSerFk1, RapServidores rapServidoresByAnuHmaSerFk3,
			RapServidores rapServidoresByAnuHmaSerFk2, Date criadoEm, Date horario, Date dthrInicio, Date dthrFim, String indSuspenso,
			Date alteradoEm, Set<AnuConferenciaMamadeira> anuConferenciaMamadeiraes) {
		this.id = id;
		this.anuDietaMamadeira = anuDietaMamadeira;
		this.rapServidoresByAnuHmaSerFk1 = rapServidoresByAnuHmaSerFk1;
		this.rapServidoresByAnuHmaSerFk3 = rapServidoresByAnuHmaSerFk3;
		this.rapServidoresByAnuHmaSerFk2 = rapServidoresByAnuHmaSerFk2;
		this.criadoEm = criadoEm;
		this.horario = horario;
		this.dthrInicio = dthrInicio;
		this.dthrFim = dthrFim;
		this.indSuspenso = indSuspenso;
		this.alteradoEm = alteradoEm;
		this.anuConferenciaMamadeiraes = anuConferenciaMamadeiraes;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "dmaAtdSeq", column = @Column(name = "DMA_ATD_SEQ", nullable = false)),
			@AttributeOverride(name = "dmaSeq", column = @Column(name = "DMA_SEQ", nullable = false)),
			@AttributeOverride(name = "seq", column = @Column(name = "SEQ", nullable = false)) })
	public AnuHorarioMamadeiraId getId() {
		return this.id;
	}

	public void setId(AnuHorarioMamadeiraId id) {
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
	public RapServidores getRapServidoresByAnuHmaSerFk1() {
		return this.rapServidoresByAnuHmaSerFk1;
	}

	public void setRapServidoresByAnuHmaSerFk1(RapServidores rapServidoresByAnuHmaSerFk1) {
		this.rapServidoresByAnuHmaSerFk1 = rapServidoresByAnuHmaSerFk1;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA_SUSPENSO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_SUSPENSO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getRapServidoresByAnuHmaSerFk3() {
		return this.rapServidoresByAnuHmaSerFk3;
	}

	public void setRapServidoresByAnuHmaSerFk3(RapServidores rapServidoresByAnuHmaSerFk3) {
		this.rapServidoresByAnuHmaSerFk3 = rapServidoresByAnuHmaSerFk3;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA_MOVIMENTADO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_MOVIMENTADO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getRapServidoresByAnuHmaSerFk2() {
		return this.rapServidoresByAnuHmaSerFk2;
	}

	public void setRapServidoresByAnuHmaSerFk2(RapServidores rapServidoresByAnuHmaSerFk2) {
		this.rapServidoresByAnuHmaSerFk2 = rapServidoresByAnuHmaSerFk2;
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
	@Column(name = "HORARIO", nullable = false, length = 29)
	public Date getHorario() {
		return this.horario;
	}

	public void setHorario(Date horario) {
		this.horario = horario;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_INICIO", nullable = false, length = 29)
	public Date getDthrInicio() {
		return this.dthrInicio;
	}

	public void setDthrInicio(Date dthrInicio) {
		this.dthrInicio = dthrInicio;
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

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "anuHorarioMamadeira")
	public Set<AnuConferenciaMamadeira> getAnuConferenciaMamadeiraes() {
		return this.anuConferenciaMamadeiraes;
	}

	public void setAnuConferenciaMamadeiraes(Set<AnuConferenciaMamadeira> anuConferenciaMamadeiraes) {
		this.anuConferenciaMamadeiraes = anuConferenciaMamadeiraes;
	}

	public enum Fields {

		ID("id"),
		VERSION("version"),
		ANU_DIETA_MAMADEIRAS("anuDietaMamadeira"),
		RAP_SERVIDORES_BY_ANU_HMA_SER_FK1("rapServidoresByAnuHmaSerFk1"),
		RAP_SERVIDORES_BY_ANU_HMA_SER_FK3("rapServidoresByAnuHmaSerFk3"),
		RAP_SERVIDORES_BY_ANU_HMA_SER_FK2("rapServidoresByAnuHmaSerFk2"),
		CRIADO_EM("criadoEm"),
		HORARIO("horario"),
		DTHR_INICIO("dthrInicio"),
		DTHR_FIM("dthrFim"),
		IND_SUSPENSO("indSuspenso"),
		ALTERADO_EM("alteradoEm"),
		ANU_CONFERENCIA_MAMADEIRAES("anuConferenciaMamadeiraes");

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
		if (!(obj instanceof AnuHorarioMamadeira)) {
			return false;
		}
		AnuHorarioMamadeira other = (AnuHorarioMamadeira) obj;
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
