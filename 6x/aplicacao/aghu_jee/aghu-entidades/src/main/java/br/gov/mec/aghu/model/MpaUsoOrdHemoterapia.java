package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

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
@SequenceGenerator(name="mpaUohSq1", sequenceName="AGH.MPA_UOH_SQ1", allocationSize = 1)
@Table(name = "MPA_USO_ORD_HEMOTERAPIAS", schema = "AGH")
public class MpaUsoOrdHemoterapia extends BaseEntitySeq<Integer> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2399800884364033171L;
	private Integer seq;
	private Integer version;
	private MpaUsoMedicacao mpaUsoMedicacao;
	private AbsSolicitacoesHemoterapicas absSolicitacoesHemoterapicas;
	private RapServidores rapServidores;
	private MpaUsoOrdHemoterapia mpaUsoOrdHemoterapia;
	private MpaCadOrdHemoterapia mpaCadOrdHemoterapia;
	private Date criadoEm;
	private String indPacTransplantado;
	private String indTransfAnteriores;
	private String indUrgente;
	private String justificativa;
	private String observacao;
	private String indSituacao;
	private Set<MpaUsoOrdHemoterapia> mpaUsoOrdHemoterapiaes = new HashSet<MpaUsoOrdHemoterapia>(0);
	
	// FIXME Implementar este relacionamento
//	private Set<MpaUsoOrdItemHemoters> mpaUsoOrdItemHemoterses = new HashSet<MpaUsoOrdItemHemoters>(0);

	public MpaUsoOrdHemoterapia() {
	}

	public MpaUsoOrdHemoterapia(Integer seq, MpaUsoMedicacao mpaUsoMedicacao, RapServidores rapServidores, Date criadoEm,
			String indPacTransplantado, String indTransfAnteriores, String indUrgente, String indSituacao) {
		this.seq = seq;
		this.mpaUsoMedicacao = mpaUsoMedicacao;
		this.rapServidores = rapServidores;
		this.criadoEm = criadoEm;
		this.indPacTransplantado = indPacTransplantado;
		this.indTransfAnteriores = indTransfAnteriores;
		this.indUrgente = indUrgente;
		this.indSituacao = indSituacao;
	}

	public MpaUsoOrdHemoterapia(Integer seq, MpaUsoMedicacao mpaUsoMedicacao,
			AbsSolicitacoesHemoterapicas absSolicitacoesHemoterapicas, RapServidores rapServidores,
			MpaUsoOrdHemoterapia mpaUsoOrdHemoterapia, MpaCadOrdHemoterapia mpaCadOrdHemoterapia, Date criadoEm,
			String indPacTransplantado, String indTransfAnteriores, String indUrgente, String justificativa, String observacao,
			String indSituacao, 
//			Set<MpaUsoOrdItemHemoters> mpaUsoOrdItemHemoterses, 
			Set<MpaUsoOrdHemoterapia> mpaUsoOrdHemoterapiaes) {
		this.seq = seq;
		this.mpaUsoMedicacao = mpaUsoMedicacao;
		this.absSolicitacoesHemoterapicas = absSolicitacoesHemoterapicas;
		this.rapServidores = rapServidores;
		this.mpaUsoOrdHemoterapia = mpaUsoOrdHemoterapia;
		this.mpaCadOrdHemoterapia = mpaCadOrdHemoterapia;
		this.criadoEm = criadoEm;
		this.indPacTransplantado = indPacTransplantado;
		this.indTransfAnteriores = indTransfAnteriores;
		this.indUrgente = indUrgente;
		this.justificativa = justificativa;
		this.observacao = observacao;
		this.indSituacao = indSituacao;
//		this.mpaUsoOrdItemHemoterses = mpaUsoOrdItemHemoterses;
		this.mpaUsoOrdHemoterapiaes = mpaUsoOrdHemoterapiaes;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mpaUohSq1")
	@Column(name = "SEQ", unique = true, nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
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
	@JoinColumns({ @JoinColumn(name = "UME_USP_APA_ATD_SEQ", referencedColumnName = "USP_APA_ATD_SEQ", nullable = false),
			@JoinColumn(name = "UME_USP_APA_SEQ", referencedColumnName = "USP_APA_SEQ", nullable = false),
			@JoinColumn(name = "UME_USP_VPA_PTA_SEQ", referencedColumnName = "USP_VPA_PTA_SEQ", nullable = false),
			@JoinColumn(name = "UME_USP_VPA_SEQP", referencedColumnName = "USP_VPA_SEQP", nullable = false),
			@JoinColumn(name = "UME_USP_SEQ", referencedColumnName = "USP_SEQ", nullable = false),
			@JoinColumn(name = "UME_CAM_CIT_VPA_PTA_SEQ", referencedColumnName = "CAM_CIT_VPA_PTA_SEQ", nullable = false),
			@JoinColumn(name = "UME_CAM_CIT_VPA_SEQP", referencedColumnName = "CAM_CIT_VPA_SEQP", nullable = false),
			@JoinColumn(name = "UME_CAM_CIT_SEQP", referencedColumnName = "CAM_CIT_SEQP", nullable = false),
			@JoinColumn(name = "UME_CAM_SEQP", referencedColumnName = "CAM_SEQP", nullable = false) })
	public MpaUsoMedicacao getMpaUsoMedicacao() {
		return this.mpaUsoMedicacao;
	}

	public void setMpaUsoMedicacao(MpaUsoMedicacao mpaUsoMedicacao) {
		this.mpaUsoMedicacao = mpaUsoMedicacao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SHE_ATD_SEQ", referencedColumnName = "ATD_SEQ"),
			@JoinColumn(name = "SHE_SEQ", referencedColumnName = "SEQ") })
	public AbsSolicitacoesHemoterapicas getAbsSolicitacoesHemoterapicas() {
		return this.absSolicitacoesHemoterapicas;
	}

	public void setAbsSolicitacoesHemoterapicas(AbsSolicitacoesHemoterapicas absSolicitacoesHemoterapicas) {
		this.absSolicitacoesHemoterapicas = absSolicitacoesHemoterapicas;
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
	@JoinColumn(name = "UOH_SEQ")
	public MpaUsoOrdHemoterapia getMpaUsoOrdHemoterapia() {
		return this.mpaUsoOrdHemoterapia;
	}

	public void setMpaUsoOrdHemoterapia(MpaUsoOrdHemoterapia mpaUsoOrdHemoterapia) {
		this.mpaUsoOrdHemoterapia = mpaUsoOrdHemoterapia;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COH_SEQ")
	public MpaCadOrdHemoterapia getMpaCadOrdHemoterapia() {
		return this.mpaCadOrdHemoterapia;
	}

	public void setMpaCadOrdHemoterapia(MpaCadOrdHemoterapia mpaCadOrdHemoterapia) {
		this.mpaCadOrdHemoterapia = mpaCadOrdHemoterapia;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "IND_PAC_TRANSPLANTADO", nullable = false, length = 1)
	@Length(max = 1)
	public String getIndPacTransplantado() {
		return this.indPacTransplantado;
	}

	public void setIndPacTransplantado(String indPacTransplantado) {
		this.indPacTransplantado = indPacTransplantado;
	}

	@Column(name = "IND_TRANSF_ANTERIORES", nullable = false, length = 1)
	@Length(max = 1)
	public String getIndTransfAnteriores() {
		return this.indTransfAnteriores;
	}

	public void setIndTransfAnteriores(String indTransfAnteriores) {
		this.indTransfAnteriores = indTransfAnteriores;
	}

	@Column(name = "IND_URGENTE", nullable = false, length = 1)
	@Length(max = 1)
	public String getIndUrgente() {
		return this.indUrgente;
	}

	public void setIndUrgente(String indUrgente) {
		this.indUrgente = indUrgente;
	}

	@Column(name = "JUSTIFICATIVA", length = 2000)
	@Length(max = 2000)
	public String getJustificativa() {
		return this.justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	@Column(name = "OBSERVACAO", length = 60)
	@Length(max = 60)
	public String getObservacao() {
		return this.observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 2)
	@Length(max = 2)
	public String getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(String indSituacao) {
		this.indSituacao = indSituacao;
	}

//	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mpaUsoOrdHemoterapia")
//	public Set<MpaUsoOrdItemHemoters> getMpaUsoOrdItemHemoterses() {
//		return this.mpaUsoOrdItemHemoterses;
//	}
//
//	public void setMpaUsoOrdItemHemoterses(Set<MpaUsoOrdItemHemoters> mpaUsoOrdItemHemoterses) {
//		this.mpaUsoOrdItemHemoterses = mpaUsoOrdItemHemoterses;
//	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mpaUsoOrdHemoterapia")
	public Set<MpaUsoOrdHemoterapia> getMpaUsoOrdHemoterapiaes() {
		return this.mpaUsoOrdHemoterapiaes;
	}

	public void setMpaUsoOrdHemoterapiaes(Set<MpaUsoOrdHemoterapia> mpaUsoOrdHemoterapiaes) {
		this.mpaUsoOrdHemoterapiaes = mpaUsoOrdHemoterapiaes;
	}

	public enum Fields {

		SEQ("seq"),
		VERSION("version"),
		MPA_USO_MEDICACAO("mpaUsoMedicacao"),
		ABS_SOLICITACOES_HEMOTERAPICAS("absSolicitacoesHemoterapicas"),
		RAP_SERVIDORES("rapServidores"),
		MPA_USO_ORD_HEMOTERAPIAS("mpaUsoOrdHemoterapia"),
		MPA_CAD_ORD_HEMOTERAPIAS("mpaCadOrdHemoterapia"),
		CRIADO_EM("criadoEm"),
		IND_PAC_TRANSPLANTADO("indPacTransplantado"),
		IND_TRANSF_ANTERIORES("indTransfAnteriores"),
		IND_URGENTE("indUrgente"),
		JUSTIFICATIVA("justificativa"),
		OBSERVACAO("observacao"),
		IND_SITUACAO("indSituacao"),
//		MPA_USO_ORD_ITEM_HEMOTERSES("mpaUsoOrdItemHemoterses"),
		MPA_USO_ORD_HEMOTERAPIAES("mpaUsoOrdHemoterapiaes");

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
		result = prime * result + ((getSeq() == null) ? 0 : getSeq().hashCode());
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
		if (!(obj instanceof MpaUsoOrdHemoterapia)) {
			return false;
		}
		MpaUsoOrdHemoterapia other = (MpaUsoOrdHemoterapia) obj;
		if (getSeq() == null) {
			if (other.getSeq() != null) {
				return false;
			}
		} else if (!getSeq().equals(other.getSeq())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}
