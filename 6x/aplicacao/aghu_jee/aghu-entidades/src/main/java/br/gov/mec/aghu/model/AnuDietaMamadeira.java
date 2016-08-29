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
@Table(name = "ANU_DIETA_MAMADEIRAS", schema = "AGH")
public class AnuDietaMamadeira extends BaseEntityId<AnuDietaMamadeiraId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4324516820869362140L;
	private AnuDietaMamadeiraId id;
	private Integer version;
	private RapServidores rapServidoresByAnuDmaSerFk1;
	private RapServidores rapServidoresByAnuDmaSerFk2;
	private RapServidores rapServidoresByAnuDmaSerFk3;
	private AnuFormaDiluicao anuFormaDiluicao;
	private AghAtendimentos aghAtendimentos;
	private AfaViaAdministracao afaViaAdministracao;
	private Date criadoEm;
	private Date dthrInicio;
	private Date dthrFim;
	private String diluicao;
	private Short volume;
	private Short volumeAdicional;
	private String observacao;
	private String indSuspenso;
	private Date alteradoEm;
	private Set<AnuAcucarMamadeira> anuAcucarMamadeiraes = new HashSet<AnuAcucarMamadeira>(0);
	private Set<AnuFarinhaMamadeira> anuFarinhaMamadeiraes = new HashSet<AnuFarinhaMamadeira>(0);
	private Set<AnuOleoMamadeira> anuOleoMamadeiraes = new HashSet<AnuOleoMamadeira>(0);
	private Set<AnuHorarioMamadeira> anuHorarioMamadeiraes = new HashSet<AnuHorarioMamadeira>(0);
	private Set<AnuLeiteMamadeira> anuLeiteMamadeiraes = new HashSet<AnuLeiteMamadeira>(0);

	public AnuDietaMamadeira() {
	}

	public AnuDietaMamadeira(AnuDietaMamadeiraId id, RapServidores rapServidoresByAnuDmaSerFk1, AghAtendimentos aghAtendimentos,
			AfaViaAdministracao afaViaAdministracao, Date criadoEm, Date dthrInicio, String indSuspenso) {
		this.id = id;
		this.rapServidoresByAnuDmaSerFk1 = rapServidoresByAnuDmaSerFk1;
		this.aghAtendimentos = aghAtendimentos;
		this.afaViaAdministracao = afaViaAdministracao;
		this.criadoEm = criadoEm;
		this.dthrInicio = dthrInicio;
		this.indSuspenso = indSuspenso;
	}

	public AnuDietaMamadeira(AnuDietaMamadeiraId id, RapServidores rapServidoresByAnuDmaSerFk1,
			RapServidores rapServidoresByAnuDmaSerFk2, RapServidores rapServidoresByAnuDmaSerFk3, AnuFormaDiluicao anuFormaDiluicao,
			AghAtendimentos aghAtendimentos, AfaViaAdministracao afaViaAdministracao, Date criadoEm, Date dthrInicio, Date dthrFim,
			String diluicao, Short volume, Short volumeAdicional, String observacao, String indSuspenso, Date alteradoEm,
			Set<AnuAcucarMamadeira> anuAcucarMamadeiraes, Set<AnuFarinhaMamadeira> anuFarinhaMamadeiraes,
			Set<AnuOleoMamadeira> anuOleoMamadeiraes, Set<AnuHorarioMamadeira> anuHorarioMamadeiraes,
			Set<AnuLeiteMamadeira> anuLeiteMamadeiraes) {
		this.id = id;
		this.rapServidoresByAnuDmaSerFk1 = rapServidoresByAnuDmaSerFk1;
		this.rapServidoresByAnuDmaSerFk2 = rapServidoresByAnuDmaSerFk2;
		this.rapServidoresByAnuDmaSerFk3 = rapServidoresByAnuDmaSerFk3;
		this.anuFormaDiluicao = anuFormaDiluicao;
		this.aghAtendimentos = aghAtendimentos;
		this.afaViaAdministracao = afaViaAdministracao;
		this.criadoEm = criadoEm;
		this.dthrInicio = dthrInicio;
		this.dthrFim = dthrFim;
		this.diluicao = diluicao;
		this.volume = volume;
		this.volumeAdicional = volumeAdicional;
		this.observacao = observacao;
		this.indSuspenso = indSuspenso;
		this.alteradoEm = alteradoEm;
		this.anuAcucarMamadeiraes = anuAcucarMamadeiraes;
		this.anuFarinhaMamadeiraes = anuFarinhaMamadeiraes;
		this.anuOleoMamadeiraes = anuOleoMamadeiraes;
		this.anuHorarioMamadeiraes = anuHorarioMamadeiraes;
		this.anuLeiteMamadeiraes = anuLeiteMamadeiraes;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "atdSeq", column = @Column(name = "ATD_SEQ", nullable = false)),
			@AttributeOverride(name = "seq", column = @Column(name = "SEQ", nullable = false)) })
	public AnuDietaMamadeiraId getId() {
		return this.id;
	}

	public void setId(AnuDietaMamadeiraId id) {
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
	public RapServidores getRapServidoresByAnuDmaSerFk1() {
		return this.rapServidoresByAnuDmaSerFk1;
	}

	public void setRapServidoresByAnuDmaSerFk1(RapServidores rapServidoresByAnuDmaSerFk1) {
		this.rapServidoresByAnuDmaSerFk1 = rapServidoresByAnuDmaSerFk1;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA_MOVIMENTADA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_MOVIMENTADA", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getRapServidoresByAnuDmaSerFk2() {
		return this.rapServidoresByAnuDmaSerFk2;
	}

	public void setRapServidoresByAnuDmaSerFk2(RapServidores rapServidoresByAnuDmaSerFk2) {
		this.rapServidoresByAnuDmaSerFk2 = rapServidoresByAnuDmaSerFk2;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA_SUSPENSA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_SUSPENSA", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getRapServidoresByAnuDmaSerFk3() {
		return this.rapServidoresByAnuDmaSerFk3;
	}

	public void setRapServidoresByAnuDmaSerFk3(RapServidores rapServidoresByAnuDmaSerFk3) {
		this.rapServidoresByAnuDmaSerFk3 = rapServidoresByAnuDmaSerFk3;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FDI_SEQ")
	public AnuFormaDiluicao getAnuFormaDiluicao() {
		return this.anuFormaDiluicao;
	}

	public void setAnuFormaDiluicao(AnuFormaDiluicao anuFormaDiluicao) {
		this.anuFormaDiluicao = anuFormaDiluicao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ATD_SEQ", nullable = false, insertable = false, updatable = false)
	public AghAtendimentos getAghAtendimentos() {
		return this.aghAtendimentos;
	}

	public void setAghAtendimentos(AghAtendimentos aghAtendimentos) {
		this.aghAtendimentos = aghAtendimentos;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "VAD_SIGLA", nullable = false)
	public AfaViaAdministracao getAfaViaAdministracao() {
		return this.afaViaAdministracao;
	}

	public void setAfaViaAdministracao(AfaViaAdministracao afaViaAdministracao) {
		this.afaViaAdministracao = afaViaAdministracao;
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

	@Column(name = "DILUICAO", length = 240)
	@Length(max = 240)
	public String getDiluicao() {
		return this.diluicao;
	}

	public void setDiluicao(String diluicao) {
		this.diluicao = diluicao;
	}

	@Column(name = "VOLUME")
	public Short getVolume() {
		return this.volume;
	}

	public void setVolume(Short volume) {
		this.volume = volume;
	}

	@Column(name = "VOLUME_ADICIONAL")
	public Short getVolumeAdicional() {
		return this.volumeAdicional;
	}

	public void setVolumeAdicional(Short volumeAdicional) {
		this.volumeAdicional = volumeAdicional;
	}

	@Column(name = "OBSERVACAO", length = 500)
	@Length(max = 500)
	public String getObservacao() {
		return this.observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
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

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "anuDietaMamadeira")
	public Set<AnuAcucarMamadeira> getAnuAcucarMamadeiraes() {
		return this.anuAcucarMamadeiraes;
	}

	public void setAnuAcucarMamadeiraes(Set<AnuAcucarMamadeira> anuAcucarMamadeiraes) {
		this.anuAcucarMamadeiraes = anuAcucarMamadeiraes;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "anuDietaMamadeira")
	public Set<AnuFarinhaMamadeira> getAnuFarinhaMamadeiraes() {
		return this.anuFarinhaMamadeiraes;
	}

	public void setAnuFarinhaMamadeiraes(Set<AnuFarinhaMamadeira> anuFarinhaMamadeiraes) {
		this.anuFarinhaMamadeiraes = anuFarinhaMamadeiraes;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "anuDietaMamadeira")
	public Set<AnuOleoMamadeira> getAnuOleoMamadeiraes() {
		return this.anuOleoMamadeiraes;
	}

	public void setAnuOleoMamadeiraes(Set<AnuOleoMamadeira> anuOleoMamadeiraes) {
		this.anuOleoMamadeiraes = anuOleoMamadeiraes;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "anuDietaMamadeira")
	public Set<AnuHorarioMamadeira> getAnuHorarioMamadeiraes() {
		return this.anuHorarioMamadeiraes;
	}

	public void setAnuHorarioMamadeiraes(Set<AnuHorarioMamadeira> anuHorarioMamadeiraes) {
		this.anuHorarioMamadeiraes = anuHorarioMamadeiraes;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "anuDietaMamadeira")
	public Set<AnuLeiteMamadeira> getAnuLeiteMamadeiraes() {
		return this.anuLeiteMamadeiraes;
	}

	public void setAnuLeiteMamadeiraes(Set<AnuLeiteMamadeira> anuLeiteMamadeiraes) {
		this.anuLeiteMamadeiraes = anuLeiteMamadeiraes;
	}

	public enum Fields {

		ID("id"),
		VERSION("version"),
		RAP_SERVIDORES_BY_ANU_DMA_SER_FK1("rapServidoresByAnuDmaSerFk1"),
		RAP_SERVIDORES_BY_ANU_DMA_SER_FK2("rapServidoresByAnuDmaSerFk2"),
		RAP_SERVIDORES_BY_ANU_DMA_SER_FK3("rapServidoresByAnuDmaSerFk3"),
		ANU_FORMA_DILUICOES("anuFormaDiluicao"),
		AGH_ATENDIMENTOS("aghAtendimentos"),
		AFA_VIA_ADMINISTRACAO("afaViaAdministracao"),
		CRIADO_EM("criadoEm"),
		DTHR_INICIO("dthrInicio"),
		DTHR_FIM("dthrFim"),
		DILUICAO("diluicao"),
		VOLUME("volume"),
		VOLUME_ADICIONAL("volumeAdicional"),
		OBSERVACAO("observacao"),
		IND_SUSPENSO("indSuspenso"),
		ALTERADO_EM("alteradoEm"),
		ANU_ACUCAR_MAMADEIRAES("anuAcucarMamadeiraes"),
		ANU_FARINHA_MAMADEIRAES("anuFarinhaMamadeiraes"),
		ANU_OLEO_MAMADEIRAES("anuOleoMamadeiraes"),
		ANU_HORARIO_MAMADEIRAES("anuHorarioMamadeiraes"),
		ANU_LEITE_MAMADEIRAES("anuLeiteMamadeiraes");

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
		if (!(obj instanceof AnuDietaMamadeira)) {
			return false;
		}
		AnuDietaMamadeira other = (AnuDietaMamadeira) obj;
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
