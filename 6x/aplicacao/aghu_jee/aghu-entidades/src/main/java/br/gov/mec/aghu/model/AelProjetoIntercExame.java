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
@Table(name = "AEL_PROJETO_INTERC_EXAMES", schema = "AGH")
public class AelProjetoIntercExame extends BaseEntityId<AelProjetoIntercExameId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1495099422278095105L;
	private AelProjetoIntercExameId id;
	private Integer version;
	private AelProjetoPacientes aelProjetoPacientes;
	private RapServidores rapServidores;
	private AelExamesMaterialAnalise aelExamesMaterialAnalise;
	private Date criadoEm;
	private String justificativa;
	private String efetivado;
	private Short qtde;

	public AelProjetoIntercExame() {
	}

	public AelProjetoIntercExame(AelProjetoIntercExameId id, AelProjetoPacientes aelProjetoPacientes, RapServidores rapServidores,
			AelExamesMaterialAnalise aelExamesMaterialAnalise, Date criadoEm, String justificativa, String efetivado, Short qtde) {
		this.id = id;
		this.aelProjetoPacientes = aelProjetoPacientes;
		this.rapServidores = rapServidores;
		this.aelExamesMaterialAnalise = aelExamesMaterialAnalise;
		this.criadoEm = criadoEm;
		this.justificativa = justificativa;
		this.efetivado = efetivado;
		this.qtde = qtde;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "ppjPjqSeq", column = @Column(name = "PPJ_PJQ_SEQ", nullable = false)),
			@AttributeOverride(name = "ppjPacCodigo", column = @Column(name = "PPJ_PAC_CODIGO", nullable = false)),
			@AttributeOverride(name = "emaExaSigla", column = @Column(name = "EMA_EXA_SIGLA", nullable = false, length = 5)),
			@AttributeOverride(name = "emaManSeq", column = @Column(name = "EMA_MAN_SEQ", nullable = false)),
			@AttributeOverride(name = "seqp", column = @Column(name = "SEQP", nullable = false)) })
	public AelProjetoIntercExameId getId() {
		return this.id;
	}

	public void setId(AelProjetoIntercExameId id) {
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
			@JoinColumn(name = "PPJ_PJQ_SEQ", referencedColumnName = "PJQ_SEQ", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "PPJ_PAC_CODIGO", referencedColumnName = "PAC_CODIGO", nullable = false, insertable = false, updatable = false) })
	public AelProjetoPacientes getAelProjetoPacientes() {
		return this.aelProjetoPacientes;
	}

	public void setAelProjetoPacientes(AelProjetoPacientes aelProjetoPacientes) {
		this.aelProjetoPacientes = aelProjetoPacientes;
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
			@JoinColumn(name = "EMA_EXA_SIGLA", referencedColumnName = "EXA_SIGLA", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "EMA_MAN_SEQ", referencedColumnName = "MAN_SEQ", nullable = false, insertable = false, updatable = false) })
	public AelExamesMaterialAnalise getAelExamesMaterialAnalise() {
		return this.aelExamesMaterialAnalise;
	}

	public void setAelExamesMaterialAnalise(AelExamesMaterialAnalise aelExamesMaterialAnalise) {
		this.aelExamesMaterialAnalise = aelExamesMaterialAnalise;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "JUSTIFICATIVA", nullable = false, length = 4000)
	@Length(max = 4000)
	public String getJustificativa() {
		return this.justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	@Column(name = "EFETIVADO", nullable = false, length = 1)
	@Length(max = 1)
	public String getEfetivado() {
		return this.efetivado;
	}

	public void setEfetivado(String efetivado) {
		this.efetivado = efetivado;
	}

	@Column(name = "QTDE", nullable = false)
	public Short getQtde() {
		return this.qtde;
	}

	public void setQtde(Short qtde) {
		this.qtde = qtde;
	}

	public enum Fields {

		ID("id"),
		VERSION("version"),
		AEL_PROJETO_PACIENTES("aelProjetoPacientes"),
		RAP_SERVIDORES("rapServidores"),
		AEL_EXAMES_MATERIAL_ANALISE("aelExamesMaterialAnalise"),
		CRIADO_EM("criadoEm"),
		JUSTIFICATIVA("justificativa"),
		EFETIVADO("efetivado"),
		QTDE("qtde");

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
		if (!(obj instanceof AelProjetoIntercExame)) {
			return false;
		}
		AelProjetoIntercExame other = (AelProjetoIntercExame) obj;
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
