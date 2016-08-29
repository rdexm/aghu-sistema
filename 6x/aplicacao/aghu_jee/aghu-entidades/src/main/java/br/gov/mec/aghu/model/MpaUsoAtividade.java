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
@Table(name = "MPA_USO_ATIVIDADES", schema = "AGH")
public class MpaUsoAtividade extends BaseEntityId<MpaUsoAtividadeId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6046708135517400535L;
	private MpaUsoAtividadeId id;
	private Integer version;
	private MpaUsoProtocolo mpaUsoProtocolo;
	private RapServidores rapServidores;
	private MpaCadAtividadeFisica mpaCadAtividadeFisica;
	private Date criadoEm;
	private String indMarcada;
	
	// FIXME Implementar este relacionamento
//	private Set<MpaUsoOrdCuidado> mpaUsoOrdCuidadoes = new HashSet<MpaUsoOrdCuidado>(0);

	public MpaUsoAtividade() {
	}

	public MpaUsoAtividade(MpaUsoAtividadeId id, MpaUsoProtocolo mpaUsoProtocolo, RapServidores rapServidores,
			MpaCadAtividadeFisica mpaCadAtividadeFisica, Date criadoEm, String indMarcada) {
		this.id = id;
		this.mpaUsoProtocolo = mpaUsoProtocolo;
		this.rapServidores = rapServidores;
		this.mpaCadAtividadeFisica = mpaCadAtividadeFisica;
		this.criadoEm = criadoEm;
		this.indMarcada = indMarcada;
	}

//	public MpaUsoAtividade(MpaUsoAtividadeId id, MpaUsoProtocolo mpaUsoProtocolo, RapServidores rapServidores,
//			MpaCadAtividadeFisica mpaCadAtividadeFisica, Date criadoEm, String indMarcada, Set<MpaUsoOrdCuidado> mpaUsoOrdCuidadoes) {
//		this.id = id;
//		this.mpaUsoProtocolo = mpaUsoProtocolo;
//		this.rapServidores = rapServidores;
//		this.mpaCadAtividadeFisica = mpaCadAtividadeFisica;
//		this.criadoEm = criadoEm;
//		this.indMarcada = indMarcada;
//		this.mpaUsoOrdCuidadoes = mpaUsoOrdCuidadoes;
//	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "uspApaAtdSeq", column = @Column(name = "USP_APA_ATD_SEQ", nullable = false)),
			@AttributeOverride(name = "uspApaSeq", column = @Column(name = "USP_APA_SEQ", nullable = false)),
			@AttributeOverride(name = "uspVpaPtaSeq", column = @Column(name = "USP_VPA_PTA_SEQ", nullable = false)),
			@AttributeOverride(name = "uspVpaSeqp", column = @Column(name = "USP_VPA_SEQP", nullable = false)),
			@AttributeOverride(name = "uspSeq", column = @Column(name = "USP_SEQ", nullable = false)),
			@AttributeOverride(name = "cafCitVpaPtaSeq", column = @Column(name = "CAF_CIT_VPA_PTA_SEQ", nullable = false)),
			@AttributeOverride(name = "cafCitVpaSeqp", column = @Column(name = "CAF_CIT_VPA_SEQP", nullable = false)),
			@AttributeOverride(name = "cafCitSeqp", column = @Column(name = "CAF_CIT_SEQP", nullable = false)),
			@AttributeOverride(name = "cafSeqp", column = @Column(name = "CAF_SEQP", nullable = false)) })
	public MpaUsoAtividadeId getId() {
		return this.id;
	}

	public void setId(MpaUsoAtividadeId id) {
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
			@JoinColumn(name = "USP_APA_ATD_SEQ", referencedColumnName = "APA_ATD_SEQ", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "USP_APA_SEQ", referencedColumnName = "APA_SEQ", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "USP_VPA_PTA_SEQ", referencedColumnName = "VPA_PTA_SEQ", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "USP_VPA_SEQP", referencedColumnName = "VPA_SEQP", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "USP_SEQ", referencedColumnName = "SEQ", nullable = false, insertable = false, updatable = false) })
	public MpaUsoProtocolo getMpaUsoProtocolo() {
		return this.mpaUsoProtocolo;
	}

	public void setMpaUsoProtocolo(MpaUsoProtocolo mpaUsoProtocolo) {
		this.mpaUsoProtocolo = mpaUsoProtocolo;
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
	@JoinColumns({
			@JoinColumn(name = "CAF_CIT_VPA_PTA_SEQ", referencedColumnName = "CIT_VPA_PTA_SEQ", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "CAF_CIT_VPA_SEQP", referencedColumnName = "CIT_VPA_SEQP", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "CAF_CIT_SEQP", referencedColumnName = "CIT_SEQP", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "CAF_SEQP", referencedColumnName = "SEQP", nullable = false, insertable = false, updatable = false) })
	public MpaCadAtividadeFisica getMpaCadAtividadeFisica() {
		return this.mpaCadAtividadeFisica;
	}

	public void setMpaCadAtividadeFisica(MpaCadAtividadeFisica mpaCadAtividadeFisica) {
		this.mpaCadAtividadeFisica = mpaCadAtividadeFisica;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "IND_MARCADA", nullable = false, length = 1)
	@Length(max = 1)
	public String getIndMarcada() {
		return this.indMarcada;
	}

	public void setIndMarcada(String indMarcada) {
		this.indMarcada = indMarcada;
	}

//	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mpaUsoAtividade")
//	public Set<MpaUsoOrdCuidado> getMpaUsoOrdCuidadoes() {
//		return this.mpaUsoOrdCuidadoes;
//	}
//
//	public void setMpaUsoOrdCuidadoes(Set<MpaUsoOrdCuidado> mpaUsoOrdCuidadoes) {
//		this.mpaUsoOrdCuidadoes = mpaUsoOrdCuidadoes;
//	}

	public enum Fields {

		ID("id"),
		VERSION("version"),
		MPA_USO_PROTOCOLOS("mpaUsoProtocolo"),
		RAP_SERVIDORES("rapServidores"),
		MPA_CAD_ATIVIDADE_FISICAS("mpaCadAtividadeFisica"),
		CRIADO_EM("criadoEm"),
		IND_MARCADA("indMarcada"),
//		MPA_USO_ORD_CUIDADOES("mpaUsoOrdCuidadoes")
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
		if (!(obj instanceof MpaUsoAtividade)) {
			return false;
		}
		MpaUsoAtividade other = (MpaUsoAtividade) obj;
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
