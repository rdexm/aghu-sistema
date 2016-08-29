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
@Table(name = "ABS_APRAZAMENTOS_ITENS", schema = "AGH")
public class AbsAprazamentoItem extends BaseEntityId<AbsAprazamentoItemId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2805014404926194461L;
	private AbsAprazamentoItemId id;
	private Integer version;
	private RapServidores rapServidores;
	private AbsAprazamentoItem absAprazamentoItem;
	private AbsItensSolHemoterapicas absItensSolHemoterapicas;
	private String sapCodigo;
	private Date dthrAdministracao;
	private String observacoes;
	private Date alteradoEm;
	private Short qtdeUnidades;
	private Short qtdeMl;
	private Set<AbsAprazamentoItem> absAprazamentoItemes = new HashSet<AbsAprazamentoItem>(0);

	public AbsAprazamentoItem() {
	}

	public AbsAprazamentoItem(AbsAprazamentoItemId id, AbsItensSolHemoterapicas absItensSolHemoterapicas, String sapCodigo) {
		this.id = id;
		this.absItensSolHemoterapicas = absItensSolHemoterapicas;
		this.sapCodigo = sapCodigo;
	}

	public AbsAprazamentoItem(AbsAprazamentoItemId id, RapServidores rapServidores, AbsAprazamentoItem absAprazamentoItem,
			AbsItensSolHemoterapicas absItensSolHemoterapicas, String sapCodigo, Date dthrAdministracao, String observacoes,
			Date alteradoEm, Short qtdeUnidades, Short qtdeMl, Set<AbsAprazamentoItem> absAprazamentoItemes) {
		this.id = id;
		this.rapServidores = rapServidores;
		this.absAprazamentoItem = absAprazamentoItem;
		this.absItensSolHemoterapicas = absItensSolHemoterapicas;
		this.sapCodigo = sapCodigo;
		this.dthrAdministracao = dthrAdministracao;
		this.observacoes = observacoes;
		this.alteradoEm = alteradoEm;
		this.qtdeUnidades = qtdeUnidades;
		this.qtdeMl = qtdeMl;
		this.absAprazamentoItemes = absAprazamentoItemes;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "ishSheSeq", column = @Column(name = "ISH_SHE_SEQ", nullable = false)),
			@AttributeOverride(name = "ishSequencia", column = @Column(name = "ISH_SEQUENCIA", nullable = false)),
			@AttributeOverride(name = "sequencia", column = @Column(name = "SEQUENCIA", nullable = false)),
			@AttributeOverride(name = "ishSheAtdSeq", column = @Column(name = "ISH_SHE_ATD_SEQ", nullable = false)) })
	public AbsAprazamentoItemId getId() {
		return this.id;
	}

	public void setId(AbsAprazamentoItemId id) {
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
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getRapServidores() {
		return this.rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "AIT_ISH_SHE_SEQ", referencedColumnName = "ISH_SHE_SEQ"),
			@JoinColumn(name = "AIT_ISH_SEQUENCIA", referencedColumnName = "ISH_SEQUENCIA"),
			@JoinColumn(name = "AIT_SEQUENCIA", referencedColumnName = "SEQUENCIA"),
			@JoinColumn(name = "AIT_ISH_SHE_ATD_SEQ", referencedColumnName = "ISH_SHE_ATD_SEQ") })
	public AbsAprazamentoItem getAbsAprazamentoItem() {
		return this.absAprazamentoItem;
	}

	public void setAbsAprazamentoItem(AbsAprazamentoItem absAprazamentoItem) {
		this.absAprazamentoItem = absAprazamentoItem;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "ISH_SHE_ATD_SEQ", referencedColumnName = "SHE_ATD_SEQ", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "ISH_SHE_SEQ", referencedColumnName = "SHE_SEQ", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "ISH_SEQUENCIA", referencedColumnName = "SEQUENCIA", nullable = false, insertable = false, updatable = false) })
	public AbsItensSolHemoterapicas getAbsItensSolHemoterapicas() {
		return this.absItensSolHemoterapicas;
	}

	public void setAbsItensSolHemoterapicas(AbsItensSolHemoterapicas absItensSolHemoterapicas) {
		this.absItensSolHemoterapicas = absItensSolHemoterapicas;
	}

	@Column(name = "SAP_CODIGO", nullable = false, length = 2)
	@Length(max = 2)
	public String getSapCodigo() {
		return this.sapCodigo;
	}

	public void setSapCodigo(String sapCodigo) {
		this.sapCodigo = sapCodigo;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_ADMINISTRACAO", length = 29)
	public Date getDthrAdministracao() {
		return this.dthrAdministracao;
	}

	public void setDthrAdministracao(Date dthrAdministracao) {
		this.dthrAdministracao = dthrAdministracao;
	}

	@Column(name = "OBSERVACOES", length = 60)
	@Length(max = 60)
	public String getObservacoes() {
		return this.observacoes;
	}

	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ALTERADO_EM", length = 29)
	public Date getAlteradoEm() {
		return this.alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}

	@Column(name = "QTDE_UNIDADES")
	public Short getQtdeUnidades() {
		return this.qtdeUnidades;
	}

	public void setQtdeUnidades(Short qtdeUnidades) {
		this.qtdeUnidades = qtdeUnidades;
	}

	@Column(name = "QTDE_ML")
	public Short getQtdeMl() {
		return this.qtdeMl;
	}

	public void setQtdeMl(Short qtdeMl) {
		this.qtdeMl = qtdeMl;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "absAprazamentoItem")
	public Set<AbsAprazamentoItem> getAbsAprazamentoItemes() {
		return this.absAprazamentoItemes;
	}

	public void setAbsAprazamentoItemes(Set<AbsAprazamentoItem> absAprazamentoItemes) {
		this.absAprazamentoItemes = absAprazamentoItemes;
	}

	public enum Fields {

		ID("id"),
		VERSION("version"),
		RAP_SERVIDORES("rapServidores"),
		ABS_APRAZAMENTOS_ITENS("absAprazamentoItem"),
		ABS_ITENS_SOL_HEMOTERAPICAS("absItensSolHemoterapicas"),
		SAP_CODIGO("sapCodigo"),
		DTHR_ADMINISTRACAO("dthrAdministracao"),
		OBSERVACOES("observacoes"),
		ALTERADO_EM("alteradoEm"),
		QTDE_UNIDADES("qtdeUnidades"),
		QTDE_ML("qtdeMl"),
		ABS_APRAZAMENTO_ITEMES("absAprazamentoItemes");

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
		if (!(obj instanceof AbsAprazamentoItem)) {
			return false;
		}
		AbsAprazamentoItem other = (AbsAprazamentoItem) obj;
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
