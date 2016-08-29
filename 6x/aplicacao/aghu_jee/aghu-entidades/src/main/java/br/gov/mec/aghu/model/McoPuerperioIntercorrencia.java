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
@Table(name = "MCO_PUERPERIO_INTERCORRENCIAS", schema = "AGH")
public class McoPuerperioIntercorrencia extends BaseEntityId<McoPuerperioIntercorrenciaId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7564578690517016866L;
	private McoPuerperioIntercorrenciaId id;
	private Integer version;
	private RapServidores rapServidores;
	private McoIntercorrencia mcoIntercorrencia;
	private McoPuerperio mcoPuerperio;
	private McoProcedimentoObstetrico mcoProcedimentoObstetrico;
	private Date dthrIntercorrencia;
	private Date criadoEm;

	public McoPuerperioIntercorrencia() {
	}

	public McoPuerperioIntercorrencia(McoPuerperioIntercorrenciaId id, RapServidores rapServidores,
			McoIntercorrencia mcoIntercorrencia, McoPuerperio mcoPuerperio, Date dthrIntercorrencia, Date criadoEm) {
		this.id = id;
		this.rapServidores = rapServidores;
		this.mcoIntercorrencia = mcoIntercorrencia;
		this.mcoPuerperio = mcoPuerperio;
		this.dthrIntercorrencia = dthrIntercorrencia;
		this.criadoEm = criadoEm;
	}

	public McoPuerperioIntercorrencia(McoPuerperioIntercorrenciaId id, RapServidores rapServidores,
			McoIntercorrencia mcoIntercorrencia, McoPuerperio mcoPuerperio, McoProcedimentoObstetrico mcoProcedimentoObstetrico,
			Date dthrIntercorrencia, Date criadoEm) {
		this.id = id;
		this.rapServidores = rapServidores;
		this.mcoIntercorrencia = mcoIntercorrencia;
		this.mcoPuerperio = mcoPuerperio;
		this.mcoProcedimentoObstetrico = mcoProcedimentoObstetrico;
		this.dthrIntercorrencia = dthrIntercorrencia;
		this.criadoEm = criadoEm;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "pueGsoPacCodigo", column = @Column(name = "PUE_GSO_PAC_CODIGO", nullable = false)),
			@AttributeOverride(name = "pueGsoSeqp", column = @Column(name = "PUE_GSO_SEQP", nullable = false)),
			@AttributeOverride(name = "seqp", column = @Column(name = "SEQP", nullable = false)) })
	public McoPuerperioIntercorrenciaId getId() {
		return this.id;
	}

	public void setId(McoPuerperioIntercorrenciaId id) {
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
	@JoinColumn(name = "ICR_SEQ", nullable = false)
	public McoIntercorrencia getMcoIntercorrencia() {
		return this.mcoIntercorrencia;
	}

	public void setMcoIntercorrencia(McoIntercorrencia mcoIntercorrencia) {
		this.mcoIntercorrencia = mcoIntercorrencia;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "PUE_GSO_PAC_CODIGO", referencedColumnName = "GSO_PAC_CODIGO", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "PUE_GSO_SEQP", referencedColumnName = "GSO_SEQP", nullable = false, insertable = false, updatable = false) })
	public McoPuerperio getMcoPuerperio() {
		return this.mcoPuerperio;
	}

	public void setMcoPuerperio(McoPuerperio mcoPuerperio) {
		this.mcoPuerperio = mcoPuerperio;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OBS_SEQ")
	public McoProcedimentoObstetrico getMcoProcedimentoObstetrico() {
		return this.mcoProcedimentoObstetrico;
	}

	public void setMcoProcedimentoObstetrico(McoProcedimentoObstetrico mcoProcedimentoObstetrico) {
		this.mcoProcedimentoObstetrico = mcoProcedimentoObstetrico;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_INTERCORRENCIA", nullable = false, length = 29)
	public Date getDthrIntercorrencia() {
		return this.dthrIntercorrencia;
	}

	public void setDthrIntercorrencia(Date dthrIntercorrencia) {
		this.dthrIntercorrencia = dthrIntercorrencia;
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
		MCO_INTERCORRENCIAS("mcoIntercorrencia"),
		MCO_PUERPERIOS("mcoPuerperio"),
		MCO_PROCEDIMENTO_OBSTETRICOS("mcoProcedimentoObstetrico"),
		DTHR_INTERCORRENCIA("dthrIntercorrencia"),
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
		if (!(obj instanceof McoPuerperioIntercorrencia)) {
			return false;
		}
		McoPuerperioIntercorrencia other = (McoPuerperioIntercorrencia) obj;
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
