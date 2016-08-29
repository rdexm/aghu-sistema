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
import javax.persistence.UniqueConstraint;
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
@Table(name = "MPA_CAD_ATIVIDADE_FISICAS", schema = "AGH", uniqueConstraints = @UniqueConstraint(columnNames = {
		"caf_cit_vpa_pta_seq", "caf_cit_vpa_seqp", "caf_cit_seqp", "caf_seqp" }))
public class MpaCadAtividadeFisica extends BaseEntityId<MpaCadAtividadeFisicaId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5692693619701603974L;
	private MpaCadAtividadeFisicaId id;
	private Integer version;
	private RapServidores rapServidores;
	private MpaCadIntervaloTempo mpaCadIntervaloTempo;
	private MpaCadAtividadeFisica mpaCadAtividadeFisica;
	private String descricao;
	private String indObrigatorio;
	private String indOrdemMedica;
	private Date criadoEm;
	private String indCoexistente;
	private Short ordemVisualizacao;
	private String indSituacao;
	private Set<MpaUsoAtividade> mpaUsoAtividadees = new HashSet<MpaUsoAtividade>(0);
	private Set<MpaCadAtividadeFisica> mpaCadAtividadeFisicaes = new HashSet<MpaCadAtividadeFisica>(0);
	
	// FIXME Implementar este relacionamento
//	private Set<MpaCadOrdCuidado> mpaCadOrdCuidadoes = new HashSet<MpaCadOrdCuidado>(0);

	public MpaCadAtividadeFisica() {
	}

	public MpaCadAtividadeFisica(MpaCadAtividadeFisicaId id, RapServidores rapServidores,
			MpaCadIntervaloTempo mpaCadIntervaloTempo, String descricao, String indObrigatorio, String indOrdemMedica,
			Date criadoEm, String indCoexistente, Short ordemVisualizacao, String indSituacao) {
		this.id = id;
		this.rapServidores = rapServidores;
		this.mpaCadIntervaloTempo = mpaCadIntervaloTempo;
		this.descricao = descricao;
		this.indObrigatorio = indObrigatorio;
		this.indOrdemMedica = indOrdemMedica;
		this.criadoEm = criadoEm;
		this.indCoexistente = indCoexistente;
		this.ordemVisualizacao = ordemVisualizacao;
		this.indSituacao = indSituacao;
	}

	public MpaCadAtividadeFisica(MpaCadAtividadeFisicaId id, RapServidores rapServidores,
			MpaCadIntervaloTempo mpaCadIntervaloTempo, MpaCadAtividadeFisica mpaCadAtividadeFisica, String descricao,
			String indObrigatorio, String indOrdemMedica, Date criadoEm, String indCoexistente, Short ordemVisualizacao,
			String indSituacao, Set<MpaUsoAtividade> mpaUsoAtividadees, 
//			Set<MpaCadOrdCuidado> mpaCadOrdCuidadoes,
			Set<MpaCadAtividadeFisica> mpaCadAtividadeFisicaes) {
		this.id = id;
		this.rapServidores = rapServidores;
		this.mpaCadIntervaloTempo = mpaCadIntervaloTempo;
		this.mpaCadAtividadeFisica = mpaCadAtividadeFisica;
		this.descricao = descricao;
		this.indObrigatorio = indObrigatorio;
		this.indOrdemMedica = indOrdemMedica;
		this.criadoEm = criadoEm;
		this.indCoexistente = indCoexistente;
		this.ordemVisualizacao = ordemVisualizacao;
		this.indSituacao = indSituacao;
		this.mpaUsoAtividadees = mpaUsoAtividadees;
//		this.mpaCadOrdCuidadoes = mpaCadOrdCuidadoes;
		this.mpaCadAtividadeFisicaes = mpaCadAtividadeFisicaes;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "citVpaPtaSeq", column = @Column(name = "CIT_VPA_PTA_SEQ", nullable = false)),
			@AttributeOverride(name = "citVpaSeqp", column = @Column(name = "CIT_VPA_SEQP", nullable = false)),
			@AttributeOverride(name = "citSeqp", column = @Column(name = "CIT_SEQP", nullable = false)),
			@AttributeOverride(name = "seqp", column = @Column(name = "SEQP", nullable = false)) })
	public MpaCadAtividadeFisicaId getId() {
		return this.id;
	}

	public void setId(MpaCadAtividadeFisicaId id) {
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
	@JoinColumns({
			@JoinColumn(name = "CIT_VPA_SEQP", referencedColumnName = "VPA_SEQP", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "CIT_VPA_PTA_SEQ", referencedColumnName = "VPA_PTA_SEQ", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "CIT_SEQP", referencedColumnName = "SEQP", nullable = false, insertable = false, updatable = false) })
	public MpaCadIntervaloTempo getMpaCadIntervaloTempo() {
		return this.mpaCadIntervaloTempo;
	}

	public void setMpaCadIntervaloTempo(MpaCadIntervaloTempo mpaCadIntervaloTempo) {
		this.mpaCadIntervaloTempo = mpaCadIntervaloTempo;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "CAF_CIT_VPA_PTA_SEQ", referencedColumnName = "CIT_VPA_PTA_SEQ"),
			@JoinColumn(name = "CAF_CIT_VPA_SEQP", referencedColumnName = "CIT_VPA_SEQP"),
			@JoinColumn(name = "CAF_CIT_SEQP", referencedColumnName = "CIT_SEQP"),
			@JoinColumn(name = "CAF_SEQP", referencedColumnName = "SEQP") })
	public MpaCadAtividadeFisica getMpaCadAtividadeFisica() {
		return this.mpaCadAtividadeFisica;
	}

	public void setMpaCadAtividadeFisica(MpaCadAtividadeFisica mpaCadAtividadeFisica) {
		this.mpaCadAtividadeFisica = mpaCadAtividadeFisica;
	}

	@Column(name = "DESCRICAO", nullable = false, length = 240)
	@Length(max = 240)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "IND_OBRIGATORIO", nullable = false, length = 1)
	@Length(max = 1)
	public String getIndObrigatorio() {
		return this.indObrigatorio;
	}

	public void setIndObrigatorio(String indObrigatorio) {
		this.indObrigatorio = indObrigatorio;
	}

	@Column(name = "IND_ORDEM_MEDICA", nullable = false, length = 1)
	@Length(max = 1)
	public String getIndOrdemMedica() {
		return this.indOrdemMedica;
	}

	public void setIndOrdemMedica(String indOrdemMedica) {
		this.indOrdemMedica = indOrdemMedica;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "IND_COEXISTENTE", nullable = false, length = 1)
	@Length(max = 1)
	public String getIndCoexistente() {
		return this.indCoexistente;
	}

	public void setIndCoexistente(String indCoexistente) {
		this.indCoexistente = indCoexistente;
	}

	@Column(name = "ORDEM_VISUALIZACAO", nullable = false)
	public Short getOrdemVisualizacao() {
		return this.ordemVisualizacao;
	}

	public void setOrdemVisualizacao(Short ordemVisualizacao) {
		this.ordemVisualizacao = ordemVisualizacao;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Length(max = 1)
	public String getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(String indSituacao) {
		this.indSituacao = indSituacao;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mpaCadAtividadeFisica")
	public Set<MpaUsoAtividade> getMpaUsoAtividadees() {
		return this.mpaUsoAtividadees;
	}

	public void setMpaUsoAtividadees(Set<MpaUsoAtividade> mpaUsoAtividadees) {
		this.mpaUsoAtividadees = mpaUsoAtividadees;
	}

//	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mpaCadAtividadeFisica")
//	public Set<MpaCadOrdCuidado> getMpaCadOrdCuidadoes() {
//		return this.mpaCadOrdCuidadoes;
//	}
//
//	public void setMpaCadOrdCuidadoes(Set<MpaCadOrdCuidado> mpaCadOrdCuidadoes) {
//		this.mpaCadOrdCuidadoes = mpaCadOrdCuidadoes;
//	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mpaCadAtividadeFisica")
	public Set<MpaCadAtividadeFisica> getMpaCadAtividadeFisicaes() {
		return this.mpaCadAtividadeFisicaes;
	}

	public void setMpaCadAtividadeFisicaes(Set<MpaCadAtividadeFisica> mpaCadAtividadeFisicaes) {
		this.mpaCadAtividadeFisicaes = mpaCadAtividadeFisicaes;
	}

	public enum Fields {

		ID("id"),
		VERSION("version"),
		RAP_SERVIDORES("rapServidores"),
		MPA_CAD_INTERVALO_TEMPOS("mpaCadIntervaloTempo"),
		MPA_CAD_ATIVIDADE_FISICAS("mpaCadAtividadeFisica"),
		DESCRICAO("descricao"),
		IND_OBRIGATORIO("indObrigatorio"),
		IND_ORDEM_MEDICA("indOrdemMedica"),
		CRIADO_EM("criadoEm"),
		IND_COEXISTENTE("indCoexistente"),
		ORDEM_VISUALIZACAO("ordemVisualizacao"),
		IND_SITUACAO("indSituacao"),
		MPA_USO_ATIVIDADEES("mpaUsoAtividadees"),
//		MPA_CAD_ORD_CUIDADOES("mpaCadOrdCuidadoes"),
		MPA_CAD_ATIVIDADE_FISICAES("mpaCadAtividadeFisicaes");

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
		if (!(obj instanceof MpaCadAtividadeFisica)) {
			return false;
		}
		MpaCadAtividadeFisica other = (MpaCadAtividadeFisica) obj;
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
