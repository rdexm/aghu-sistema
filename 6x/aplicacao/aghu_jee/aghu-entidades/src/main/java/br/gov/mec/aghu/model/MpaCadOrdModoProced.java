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
@Table(name = "MPA_CAD_ORD_MODO_PROCEDS", schema = "AGH")
public class MpaCadOrdModoProced extends BaseEntityId<MpaCadOrdModoProcedId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 297632254325258523L;
	private MpaCadOrdModoProcedId id;
	private Integer version;
	private RapServidores rapServidores;
	private MpmTipoModoUsoProcedimento mpmTipoModoUsoProcedimento;
	private MpaCadOrdProcedimentos mpaCadOrdProcedimentos;
	private Date criadoEm;
	private Short quantidade;
	
	// FIXME Implementar este relacionamento
//	private Set<MpaUsoOrdModoProced> mpaUsoOrdModoProcedes = new HashSet<MpaUsoOrdModoProced>(0);

	public MpaCadOrdModoProced() {
	}

	public MpaCadOrdModoProced(MpaCadOrdModoProcedId id, RapServidores rapServidores,
			MpmTipoModoUsoProcedimento mpmTipoModoUsoProcedimento, MpaCadOrdProcedimentos mpaCadOrdProcedimentos, Date criadoEm) {
		this.id = id;
		this.rapServidores = rapServidores;
		this.mpmTipoModoUsoProcedimento = mpmTipoModoUsoProcedimento;
		this.mpaCadOrdProcedimentos = mpaCadOrdProcedimentos;
		this.criadoEm = criadoEm;
	}

	public MpaCadOrdModoProced(MpaCadOrdModoProcedId id, RapServidores rapServidores,
			MpmTipoModoUsoProcedimento mpmTipoModoUsoProcedimento, MpaCadOrdProcedimentos mpaCadOrdProcedimentos, Date criadoEm,
			Short quantidade
//			, Set<MpaUsoOrdModoProced> mpaUsoOrdModoProcedes
			) {
		this.id = id;
		this.rapServidores = rapServidores;
		this.mpmTipoModoUsoProcedimento = mpmTipoModoUsoProcedimento;
		this.mpaCadOrdProcedimentos = mpaCadOrdProcedimentos;
		this.criadoEm = criadoEm;
		this.quantidade = quantidade;
//		this.mpaUsoOrdModoProcedes = mpaUsoOrdModoProcedes;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "copSeq", column = @Column(name = "COP_SEQ", nullable = false)),
			@AttributeOverride(name = "seqp", column = @Column(name = "SEQP", nullable = false)) })
	public MpaCadOrdModoProcedId getId() {
		return this.id;
	}

	public void setId(MpaCadOrdModoProcedId id) {
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
	@JoinColumns({ @JoinColumn(name = "TUP_PED_SEQ", referencedColumnName = "PED_SEQ", nullable = false),
			@JoinColumn(name = "TUP_SEQP", referencedColumnName = "SEQP", nullable = false) })
	public MpmTipoModoUsoProcedimento getMpmTipoModoUsoProcedimento() {
		return this.mpmTipoModoUsoProcedimento;
	}

	public void setMpmTipoModoUsoProcedimento(MpmTipoModoUsoProcedimento mpmTipoModoUsoProcedimento) {
		this.mpmTipoModoUsoProcedimento = mpmTipoModoUsoProcedimento;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COP_SEQ", nullable = false, insertable = false, updatable = false)
	public MpaCadOrdProcedimentos getMpaCadOrdProcedimentos() {
		return this.mpaCadOrdProcedimentos;
	}

	public void setMpaCadOrdProcedimentos(MpaCadOrdProcedimentos mpaCadOrdProcedimentos) {
		this.mpaCadOrdProcedimentos = mpaCadOrdProcedimentos;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "QUANTIDADE")
	public Short getQuantidade() {
		return this.quantidade;
	}

	public void setQuantidade(Short quantidade) {
		this.quantidade = quantidade;
	}

//	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mpaCadOrdModoProced")
//	public Set<MpaUsoOrdModoProced> getMpaUsoOrdModoProcedes() {
//		return this.mpaUsoOrdModoProcedes;
//	}
//
//	public void setMpaUsoOrdModoProcedes(Set<MpaUsoOrdModoProced> mpaUsoOrdModoProcedes) {
//		this.mpaUsoOrdModoProcedes = mpaUsoOrdModoProcedes;
//	}

	public enum Fields {

		ID("id"),
		VERSION("version"),
		RAP_SERVIDORES("rapServidores"),
		MPM_TIPO_MODO_USO_PROCEDIMENTO("mpmTipoModoUsoProcedimento"),
		MPA_CAD_ORD_PROCEDIMENTOS("mpaCadOrdProcedimentos"),
		CRIADO_EM("criadoEm"),
		QUANTIDADE("quantidade"),
//		MPA_USO_ORD_MODO_PROCEDES("mpaUsoOrdModoProcedes")
		;

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
		if (!(obj instanceof MpaCadOrdModoProced)) {
			return false;
		}
		MpaCadOrdModoProced other = (MpaCadOrdModoProced) obj;
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
