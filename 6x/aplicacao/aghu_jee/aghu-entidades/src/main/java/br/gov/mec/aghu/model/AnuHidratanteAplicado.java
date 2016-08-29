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
@Table(name = "ANU_HIDRATANTE_APLICADOS", schema = "AGH")
public class AnuHidratanteAplicado extends BaseEntityId<AnuHidratanteAplicadoId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6459656326610130401L;
	private AnuHidratanteAplicadoId id;
	private Integer version;
	private AnuHidratante anuHidratante;
	private RapServidores rapServidoresByAnuHipSerFk1;
	private AnuDietaNutricao anuDietaNutricao;
	private RapServidores rapServidoresByAnuHipSerFk3;
	private RapServidores rapServidoresByAnuHipSerFk2;
	private Date criadoEm;
	private Date dthrInicio;
	private Date dthrFim;
	private Integer volume;
	private String recomendacoes;
	private Date alteradoEm;
	private String indSuspenso;
	private Set<AnuHorarioHidratante> anuHorarioHidratantees = new HashSet<AnuHorarioHidratante>(0);

	public AnuHidratanteAplicado() {
	}

	public AnuHidratanteAplicado(AnuHidratanteAplicadoId id, AnuHidratante anuHidratante,
			RapServidores rapServidoresByAnuHipSerFk1, AnuDietaNutricao anuDietaNutricao, Date criadoEm, Date dthrInicio) {
		this.id = id;
		this.anuHidratante = anuHidratante;
		this.rapServidoresByAnuHipSerFk1 = rapServidoresByAnuHipSerFk1;
		this.anuDietaNutricao = anuDietaNutricao;
		this.criadoEm = criadoEm;
		this.dthrInicio = dthrInicio;
	}

	public AnuHidratanteAplicado(AnuHidratanteAplicadoId id, AnuHidratante anuHidratante,
			RapServidores rapServidoresByAnuHipSerFk1, AnuDietaNutricao anuDietaNutricao, RapServidores rapServidoresByAnuHipSerFk3,
			RapServidores rapServidoresByAnuHipSerFk2, Date criadoEm, Date dthrInicio, Date dthrFim, Integer volume,
			String recomendacoes, Date alteradoEm, String indSuspenso, Set<AnuHorarioHidratante> anuHorarioHidratantees) {
		this.id = id;
		this.anuHidratante = anuHidratante;
		this.rapServidoresByAnuHipSerFk1 = rapServidoresByAnuHipSerFk1;
		this.anuDietaNutricao = anuDietaNutricao;
		this.rapServidoresByAnuHipSerFk3 = rapServidoresByAnuHipSerFk3;
		this.rapServidoresByAnuHipSerFk2 = rapServidoresByAnuHipSerFk2;
		this.criadoEm = criadoEm;
		this.dthrInicio = dthrInicio;
		this.dthrFim = dthrFim;
		this.volume = volume;
		this.recomendacoes = recomendacoes;
		this.alteradoEm = alteradoEm;
		this.indSuspenso = indSuspenso;
		this.anuHorarioHidratantees = anuHorarioHidratantees;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "hidSeq", column = @Column(name = "HID_SEQ", nullable = false)),
			@AttributeOverride(name = "dnuAtdSeq", column = @Column(name = "DNU_ATD_SEQ", nullable = false)),
			@AttributeOverride(name = "dnuSeq", column = @Column(name = "DNU_SEQ", nullable = false)),
			@AttributeOverride(name = "seq", column = @Column(name = "SEQ", nullable = false)) })
	public AnuHidratanteAplicadoId getId() {
		return this.id;
	}

	public void setId(AnuHidratanteAplicadoId id) {
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
	@JoinColumn(name = "HID_SEQ", nullable = false, insertable = false, updatable = false)
	public AnuHidratante getAnuHidratante() {
		return this.anuHidratante;
	}

	public void setAnuHidratante(AnuHidratante anuHidratante) {
		this.anuHidratante = anuHidratante;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getRapServidoresByAnuHipSerFk1() {
		return this.rapServidoresByAnuHipSerFk1;
	}

	public void setRapServidoresByAnuHipSerFk1(RapServidores rapServidoresByAnuHipSerFk1) {
		this.rapServidoresByAnuHipSerFk1 = rapServidoresByAnuHipSerFk1;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "DNU_ATD_SEQ", referencedColumnName = "ATD_SEQ", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "DNU_SEQ", referencedColumnName = "SEQ", nullable = false, insertable = false, updatable = false) })
	public AnuDietaNutricao getAnuDietaNutricao() {
		return this.anuDietaNutricao;
	}

	public void setAnuDietaNutricao(AnuDietaNutricao anuDietaNutricao) {
		this.anuDietaNutricao = anuDietaNutricao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA_SUSPENSO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_SUSPENSO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getRapServidoresByAnuHipSerFk3() {
		return this.rapServidoresByAnuHipSerFk3;
	}

	public void setRapServidoresByAnuHipSerFk3(RapServidores rapServidoresByAnuHipSerFk3) {
		this.rapServidoresByAnuHipSerFk3 = rapServidoresByAnuHipSerFk3;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA_MOVIMENTADO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_MOVIMENTADO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getRapServidoresByAnuHipSerFk2() {
		return this.rapServidoresByAnuHipSerFk2;
	}

	public void setRapServidoresByAnuHipSerFk2(RapServidores rapServidoresByAnuHipSerFk2) {
		this.rapServidoresByAnuHipSerFk2 = rapServidoresByAnuHipSerFk2;
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

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_FIM", length = 29)
	public Date getDthrFim() {
		return this.dthrFim;
	}

	public void setDthrFim(Date dthrFim) {
		this.dthrFim = dthrFim;
	}

	@Column(name = "VOLUME")
	public Integer getVolume() {
		return this.volume;
	}

	public void setVolume(Integer volume) {
		this.volume = volume;
	}

	@Column(name = "RECOMENDACOES", length = 240)
	@Length(max = 240)
	public String getRecomendacoes() {
		return this.recomendacoes;
	}

	public void setRecomendacoes(String recomendacoes) {
		this.recomendacoes = recomendacoes;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ALTERADO_EM", length = 29)
	public Date getAlteradoEm() {
		return this.alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}

	@Column(name = "IND_SUSPENSO", length = 1)
	@Length(max = 1)
	public String getIndSuspenso() {
		return this.indSuspenso;
	}

	public void setIndSuspenso(String indSuspenso) {
		this.indSuspenso = indSuspenso;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "anuHidratanteAplicado")
	public Set<AnuHorarioHidratante> getAnuHorarioHidratantees() {
		return this.anuHorarioHidratantees;
	}

	public void setAnuHorarioHidratantees(Set<AnuHorarioHidratante> anuHorarioHidratantees) {
		this.anuHorarioHidratantees = anuHorarioHidratantees;
	}

	public enum Fields {

		ID("id"),
		VERSION("version"),
		ANU_HIDRATANTES("anuHidratante"),
		RAP_SERVIDORES_BY_ANU_HIP_SER_FK1("rapServidoresByAnuHipSerFk1"),
		ANU_DIETA_NUTRICOES("anuDietaNutricao"),
		RAP_SERVIDORES_BY_ANU_HIP_SER_FK3("rapServidoresByAnuHipSerFk3"),
		RAP_SERVIDORES_BY_ANU_HIP_SER_FK2("rapServidoresByAnuHipSerFk2"),
		CRIADO_EM("criadoEm"),
		DTHR_INICIO("dthrInicio"),
		DTHR_FIM("dthrFim"),
		VOLUME("volume"),
		RECOMENDACOES("recomendacoes"),
		ALTERADO_EM("alteradoEm"),
		IND_SUSPENSO("indSuspenso"),
		ANU_HORARIO_HIDRATANTEES("anuHorarioHidratantees");

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
		if (!(obj instanceof AnuHidratanteAplicado)) {
			return false;
		}
		AnuHidratanteAplicado other = (AnuHidratanteAplicado) obj;
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
