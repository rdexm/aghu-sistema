package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.dominio.DominioTipoValidade;
import br.gov.mec.aghu.dominio.DominioUnidadeTempo;
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
@Table(name = "ABS_VALID_AMOSTRAS_PROCEDS", schema = "AGH")
public class AbsValidAmostraProced extends BaseEntityId<AbsValidAmostraProcedId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6977864550137676323L;
	private AbsValidAmostraProcedId id;
	private Integer version;
	private RapServidores rapServidores;
	private AbsProcedHemoterapico absProcedHemoterapico;
	private Short idadeMesInicial;
	private Short idadeMesFinal;
	private Short nroMaximoSolicitacoes;
	private Integer validade;
	private DominioUnidadeTempo unidValidAmostra;
	private DominioTipoValidade tipoValidade;
	private Date criadoEm;

	public AbsValidAmostraProced() {
	}

	public AbsValidAmostraProced(AbsValidAmostraProcedId id, RapServidores rapServidores,
			AbsProcedHemoterapico absProcedHemoterapico, Short idadeMesInicial, Short idadeMesFinal, Short nroMaximoSolicitacoes,
			Integer validade, DominioUnidadeTempo unidValidAmostra, DominioTipoValidade tipoValidade, Date criadoEm) {
		this.id = id;
		this.rapServidores = rapServidores;
		this.absProcedHemoterapico = absProcedHemoterapico;
		this.idadeMesInicial = idadeMesInicial;
		this.idadeMesFinal = idadeMesFinal;
		this.nroMaximoSolicitacoes = nroMaximoSolicitacoes;
		this.validade = validade;
		this.unidValidAmostra = unidValidAmostra;
		this.tipoValidade = tipoValidade;
		this.criadoEm = criadoEm;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "pheCodigo", column = @Column(name = "PHE_CODIGO", nullable = false, length = 2)),
			@AttributeOverride(name = "seqp", column = @Column(name = "SEQP", nullable = false)) })
	public AbsValidAmostraProcedId getId() {
		return this.id;
	}

	public void setId(AbsValidAmostraProcedId id) {
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
	@JoinColumn(name = "PHE_CODIGO", nullable = false, insertable = false, updatable = false)
	public AbsProcedHemoterapico getAbsProcedHemoterapico() {
		return this.absProcedHemoterapico;
	}

	public void setAbsProcedHemoterapico(AbsProcedHemoterapico absProcedHemoterapico) {
		this.absProcedHemoterapico = absProcedHemoterapico;
	}

	@Column(name = "IDADE_MES_INICIAL", nullable = false)
	public Short getIdadeMesInicial() {
		return this.idadeMesInicial;
	}

	public void setIdadeMesInicial(Short idadeMesInicial) {
		this.idadeMesInicial = idadeMesInicial;
	}

	@Column(name = "IDADE_MES_FINAL", nullable = false)
	public Short getIdadeMesFinal() {
		return this.idadeMesFinal;
	}

	public void setIdadeMesFinal(Short idadeMesFinal) {
		this.idadeMesFinal = idadeMesFinal;
	}

	@Column(name = "NRO_MAXIMO_SOLICITACOES", nullable = false)
	public Short getNroMaximoSolicitacoes() {
		return this.nroMaximoSolicitacoes;
	}

	public void setNroMaximoSolicitacoes(Short nroMaximoSolicitacoes) {
		this.nroMaximoSolicitacoes = nroMaximoSolicitacoes;
	}

	@Column(name = "VALIDADE", nullable = false)
	public Integer getValidade() {
		return this.validade;
	}

	public void setValidade(Integer validade) {
		this.validade = validade;
	}

	@Column(name="UNID_VALID_AMOSTRA", length=1)
	@Enumerated(EnumType.STRING)
	public DominioUnidadeTempo getUnidValidAmostra() {
		return unidValidAmostra;
	}

	public void setUnidValidAmostra(DominioUnidadeTempo unidValidAmostra) {
		this.unidValidAmostra = unidValidAmostra;
	}

	@Column(name="TIPO_VALIDADE", length=1)
	@Enumerated(EnumType.STRING)
	public DominioTipoValidade getTipoValidade() {
		return tipoValidade;
	}

	public void setTipoValidade(DominioTipoValidade tipoValidade) {
		this.tipoValidade = tipoValidade;
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
		PHE_CODIGO("id.pheCodigo"),
		VERSION("version"),
		RAP_SERVIDORES("rapServidores"),
		ABS_PROCED_HEMOTERAPICO("absProcedHemoterapico"),
		IDADE_MES_INICIAL("idadeMesInicial"),
		IDADE_MES_FINAL("idadeMesFinal"),
		NRO_MAXIMO_SOLICITACOES("nroMaximoSolicitacoes"),
		VALIDADE("validade"),
		UNID_VALID_AMOSTRA("unidValidAmostra"),
		TIPO_VALIDADE("tipoValidade"),
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
		if (!(obj instanceof AbsValidAmostraProced)) {
			return false;
		}
		AbsValidAmostraProced other = (AbsValidAmostraProced) obj;
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
