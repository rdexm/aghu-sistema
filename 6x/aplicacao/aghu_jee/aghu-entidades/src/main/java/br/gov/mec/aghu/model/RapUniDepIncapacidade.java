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
@Table(name = "RAP_UNI_DEP_INCAPACIDADES", schema = "AGH")
public class RapUniDepIncapacidade extends BaseEntityId<RapUniDepIncapacidadeId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2694260298363348819L;
	private RapUniDepIncapacidadeId id;
	private Integer version;
	private RapUniMotivoIncapacidade rapUniMotivoIncapacidade;
	private RapServidores rapServidoresByRapDinSerFk2;
	private RapServidores rapServidoresByRapDinSerFk1;
	private RapUniServDependente rapUniServDependente;
	private Date dtFim;
	private Date criadoEm;
	private Date alteradoEm;

	public RapUniDepIncapacidade() {
	}

	public RapUniDepIncapacidade(RapUniDepIncapacidadeId id, RapUniMotivoIncapacidade rapUniMotivoIncapacidade,
			RapServidores rapServidoresByRapDinSerFk1, RapUniServDependente rapUniServDependente, Date criadoEm) {
		this.id = id;
		this.rapUniMotivoIncapacidade = rapUniMotivoIncapacidade;
		this.rapServidoresByRapDinSerFk1 = rapServidoresByRapDinSerFk1;
		this.rapUniServDependente = rapUniServDependente;
		this.criadoEm = criadoEm;
	}

	public RapUniDepIncapacidade(RapUniDepIncapacidadeId id, RapUniMotivoIncapacidade rapUniMotivoIncapacidade,
			RapServidores rapServidoresByRapDinSerFk2, RapServidores rapServidoresByRapDinSerFk1,
			RapUniServDependente rapUniServDependente, Date dtFim, Date criadoEm, Date alteradoEm) {
		this.id = id;
		this.rapUniMotivoIncapacidade = rapUniMotivoIncapacidade;
		this.rapServidoresByRapDinSerFk2 = rapServidoresByRapDinSerFk2;
		this.rapServidoresByRapDinSerFk1 = rapServidoresByRapDinSerFk1;
		this.rapUniServDependente = rapUniServDependente;
		this.dtFim = dtFim;
		this.criadoEm = criadoEm;
		this.alteradoEm = alteradoEm;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "sdeSadSerMatricula", column = @Column(name = "SDE_SAD_SER_MATRICULA", nullable = false)),
			@AttributeOverride(name = "sdeSadSerVinCodigo", column = @Column(name = "SDE_SAD_SER_VIN_CODIGO", nullable = false)),
			@AttributeOverride(name = "sdeSadSeqp", column = @Column(name = "SDE_SAD_SEQP", nullable = false)),
			@AttributeOverride(name = "sdeDepPesCodigo", column = @Column(name = "SDE_DEP_PES_CODIGO", nullable = false)),
			@AttributeOverride(name = "sdeDepCodigo", column = @Column(name = "SDE_DEP_CODIGO", nullable = false)),
			@AttributeOverride(name = "sdeDtInicio", column = @Column(name = "SDE_DT_INICIO", nullable = false, length = 29)),
			@AttributeOverride(name = "dtInicio", column = @Column(name = "DT_INICIO", nullable = false, length = 29)),
			@AttributeOverride(name = "minSeq", column = @Column(name = "MIN_SEQ", nullable = false)) })
	public RapUniDepIncapacidadeId getId() {
		return this.id;
	}

	public void setId(RapUniDepIncapacidadeId id) {
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
	@JoinColumn(name = "MIN_SEQ", nullable = false, insertable = false, updatable = false)
	public RapUniMotivoIncapacidade getRapUniMotivoIncapacidade() {
		return this.rapUniMotivoIncapacidade;
	}

	public void setRapUniMotivoIncapacidade(RapUniMotivoIncapacidade rapUniMotivoIncapacidade) {
		this.rapUniMotivoIncapacidade = rapUniMotivoIncapacidade;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA_ALTERADO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_ALTERADO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getRapServidoresByRapDinSerFk2() {
		return this.rapServidoresByRapDinSerFk2;
	}

	public void setRapServidoresByRapDinSerFk2(RapServidores rapServidoresByRapDinSerFk2) {
		this.rapServidoresByRapDinSerFk2 = rapServidoresByRapDinSerFk2;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA_CRIADO", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO_CRIADO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getRapServidoresByRapDinSerFk1() {
		return this.rapServidoresByRapDinSerFk1;
	}

	public void setRapServidoresByRapDinSerFk1(RapServidores rapServidoresByRapDinSerFk1) {
		this.rapServidoresByRapDinSerFk1 = rapServidoresByRapDinSerFk1;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SDE_SAD_SER_MATRICULA", referencedColumnName = "SAD_SER_MATRICULA", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "SDE_SAD_SER_VIN_CODIGO", referencedColumnName = "SAD_SER_VIN_CODIGO", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "SDE_SAD_SEQP", referencedColumnName = "SAD_SEQP", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "SDE_DEP_PES_CODIGO", referencedColumnName = "DEP_PES_CODIGO", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "SDE_DEP_CODIGO", referencedColumnName = "DEP_CODIGO", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "SDE_DT_INICIO", referencedColumnName = "DT_INICIO", nullable = false, insertable = false, updatable = false) })
	public RapUniServDependente getRapUniServDependente() {
		return this.rapUniServDependente;
	}

	public void setRapUniServDependente(RapUniServDependente rapUniServDependente) {
		this.rapUniServDependente = rapUniServDependente;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_FIM", length = 29)
	public Date getDtFim() {
		return this.dtFim;
	}

	public void setDtFim(Date dtFim) {
		this.dtFim = dtFim;
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
		RAP_UNI_MOTIVO_INCAPACIDADES("rapUniMotivoIncapacidade"),
		RAP_SERVIDORES_BY_RAP_DIN_SER_FK2("rapServidoresByRapDinSerFk2"),
		RAP_SERVIDORES_BY_RAP_DIN_SER_FK1("rapServidoresByRapDinSerFk1"),
		RAP_UNI_SERV_DEPENDENTES("rapUniServDependente"),
		DT_FIM("dtFim"),
		CRIADO_EM("criadoEm"),
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
		if (!(obj instanceof RapUniDepIncapacidade)) {
			return false;
		}
		RapUniDepIncapacidade other = (RapUniDepIncapacidade) obj;
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
