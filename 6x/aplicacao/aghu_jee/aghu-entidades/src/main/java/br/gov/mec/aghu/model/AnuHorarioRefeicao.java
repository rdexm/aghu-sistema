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
@Table(name = "ANU_HORARIO_REFEICOES", schema = "AGH")
public class AnuHorarioRefeicao extends BaseEntityId<AnuHorarioRefeicaoId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5163826844046825026L;
	private AnuHorarioRefeicaoId id;
	private Integer version;
	private RapServidores rapServidoresByAnuHreSerFk2;
	private RapServidores rapServidoresByAnuHreSerFk3;
	private AnuAlimentacaoComplementar anuAlimentacaoComplementar;
	private RapServidores rapServidoresByAnuHreSerFk1;
	private Date criadoEm;
	private Date horario;
	private Date dthrInicio;
	private String indSuspenso;
	private Date dthrFim;
	private Date alteradoEm;

	public AnuHorarioRefeicao() {
	}

	public AnuHorarioRefeicao(AnuHorarioRefeicaoId id, RapServidores rapServidoresByAnuHreSerFk2,
			AnuAlimentacaoComplementar anuAlimentacaoComplementar, Date criadoEm, Date horario, Date dthrInicio) {
		this.id = id;
		this.rapServidoresByAnuHreSerFk2 = rapServidoresByAnuHreSerFk2;
		this.anuAlimentacaoComplementar = anuAlimentacaoComplementar;
		this.criadoEm = criadoEm;
		this.horario = horario;
		this.dthrInicio = dthrInicio;
	}

	public AnuHorarioRefeicao(AnuHorarioRefeicaoId id, RapServidores rapServidoresByAnuHreSerFk2,
			RapServidores rapServidoresByAnuHreSerFk3, AnuAlimentacaoComplementar anuAlimentacaoComplementar,
			RapServidores rapServidoresByAnuHreSerFk1, Date criadoEm, Date horario, Date dthrInicio, String indSuspenso, Date dthrFim,
			Date alteradoEm) {
		this.id = id;
		this.rapServidoresByAnuHreSerFk2 = rapServidoresByAnuHreSerFk2;
		this.rapServidoresByAnuHreSerFk3 = rapServidoresByAnuHreSerFk3;
		this.anuAlimentacaoComplementar = anuAlimentacaoComplementar;
		this.rapServidoresByAnuHreSerFk1 = rapServidoresByAnuHreSerFk1;
		this.criadoEm = criadoEm;
		this.horario = horario;
		this.dthrInicio = dthrInicio;
		this.indSuspenso = indSuspenso;
		this.dthrFim = dthrFim;
		this.alteradoEm = alteradoEm;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "amiSeq", column = @Column(name = "AMI_SEQ", nullable = false)),
			@AttributeOverride(name = "amiDnuAtdSeq", column = @Column(name = "AMI_DNU_ATD_SEQ", nullable = false)),
			@AttributeOverride(name = "amiDnuSeq", column = @Column(name = "AMI_DNU_SEQ", nullable = false)),
			@AttributeOverride(name = "seq", column = @Column(name = "SEQ", nullable = false)) })
	public AnuHorarioRefeicaoId getId() {
		return this.id;
	}

	public void setId(AnuHorarioRefeicaoId id) {
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
	public RapServidores getRapServidoresByAnuHreSerFk2() {
		return this.rapServidoresByAnuHreSerFk2;
	}

	public void setRapServidoresByAnuHreSerFk2(RapServidores rapServidoresByAnuHreSerFk2) {
		this.rapServidoresByAnuHreSerFk2 = rapServidoresByAnuHreSerFk2;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA_SUSPENSO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_SUSPENSO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getRapServidoresByAnuHreSerFk3() {
		return this.rapServidoresByAnuHreSerFk3;
	}

	public void setRapServidoresByAnuHreSerFk3(RapServidores rapServidoresByAnuHreSerFk3) {
		this.rapServidoresByAnuHreSerFk3 = rapServidoresByAnuHreSerFk3;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "AMI_DNU_ATD_SEQ", referencedColumnName = "DNU_ATD_SEQ", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "AMI_DNU_SEQ", referencedColumnName = "DNU_SEQ", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "AMI_SEQ", referencedColumnName = "SEQ", nullable = false, insertable = false, updatable = false) })
	public AnuAlimentacaoComplementar getAnuAlimentacaoComplementar() {
		return this.anuAlimentacaoComplementar;
	}

	public void setAnuAlimentacaoComplementar(AnuAlimentacaoComplementar anuAlimentacaoComplementar) {
		this.anuAlimentacaoComplementar = anuAlimentacaoComplementar;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA_MOVIMENTADO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_MOVIMENTADO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getRapServidoresByAnuHreSerFk1() {
		return this.rapServidoresByAnuHreSerFk1;
	}

	public void setRapServidoresByAnuHreSerFk1(RapServidores rapServidoresByAnuHreSerFk1) {
		this.rapServidoresByAnuHreSerFk1 = rapServidoresByAnuHreSerFk1;
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

	@Column(name = "IND_SUSPENSO", length = 1)
	@Length(max = 1)
	public String getIndSuspenso() {
		return this.indSuspenso;
	}

	public void setIndSuspenso(String indSuspenso) {
		this.indSuspenso = indSuspenso;
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

	public enum Fields {

		ID("id"),
		VERSION("version"),
		RAP_SERVIDORES_BY_ANU_HRE_SER_FK2("rapServidoresByAnuHreSerFk2"),
		RAP_SERVIDORES_BY_ANU_HRE_SER_FK3("rapServidoresByAnuHreSerFk3"),
		ANU_ALIMENTACAO_COMPLEMENTARES("anuAlimentacaoComplementar"),
		RAP_SERVIDORES_BY_ANU_HRE_SER_FK1("rapServidoresByAnuHreSerFk1"),
		CRIADO_EM("criadoEm"),
		HORARIO("horario"),
		DTHR_INICIO("dthrInicio"),
		IND_SUSPENSO("indSuspenso"),
		DTHR_FIM("dthrFim"),
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
		if (!(obj instanceof AnuHorarioRefeicao)) {
			return false;
		}
		AnuHorarioRefeicao other = (AnuHorarioRefeicao) obj;
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
