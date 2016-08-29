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
@Table(name = "AEL_PROJETO_INTERC_COMPS", schema = "AGH")
public class AelProjetoIntercComp extends BaseEntityId<AelProjetoIntercCompId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -738122840867084216L;
	private AelProjetoIntercCompId id;
	private Integer version;
	private AelProjetoPacientes aelProjetoPacientes;
	private RapServidores rapServidores;
	private AbsComponenteSanguineo absComponenteSanguineo;
	private String justificativa;
	private Short qtde;
	private String efetivado;
	private Date criadoEm;

	public AelProjetoIntercComp() {
	}

	public AelProjetoIntercComp(AelProjetoIntercCompId id, AelProjetoPacientes aelProjetoPacientes, RapServidores rapServidores,
			AbsComponenteSanguineo absComponenteSanguineo, String justificativa, Short qtde, Date criadoEm) {
		this.id = id;
		this.aelProjetoPacientes = aelProjetoPacientes;
		this.rapServidores = rapServidores;
		this.absComponenteSanguineo = absComponenteSanguineo;
		this.justificativa = justificativa;
		this.qtde = qtde;
		this.criadoEm = criadoEm;
	}

	public AelProjetoIntercComp(AelProjetoIntercCompId id, AelProjetoPacientes aelProjetoPacientes, RapServidores rapServidores,
			AbsComponenteSanguineo absComponenteSanguineo, String justificativa, Short qtde, String efetivado, Date criadoEm) {
		this.id = id;
		this.aelProjetoPacientes = aelProjetoPacientes;
		this.rapServidores = rapServidores;
		this.absComponenteSanguineo = absComponenteSanguineo;
		this.justificativa = justificativa;
		this.qtde = qtde;
		this.efetivado = efetivado;
		this.criadoEm = criadoEm;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "ppjPjqSeq", column = @Column(name = "PPJ_PJQ_SEQ", nullable = false)),
			@AttributeOverride(name = "ppjPacCodigo", column = @Column(name = "PPJ_PAC_CODIGO", nullable = false)),
			@AttributeOverride(name = "csaCodigo", column = @Column(name = "CSA_CODIGO", nullable = false, length = 2)),
			@AttributeOverride(name = "seqp", column = @Column(name = "SEQP", nullable = false)) })
	public AelProjetoIntercCompId getId() {
		return this.id;
	}

	public void setId(AelProjetoIntercCompId id) {
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
	@JoinColumn(name = "CSA_CODIGO", nullable = false, insertable = false, updatable = false)
	public AbsComponenteSanguineo getAbsComponenteSanguineo() {
		return this.absComponenteSanguineo;
	}

	public void setAbsComponenteSanguineo(AbsComponenteSanguineo absComponenteSanguineo) {
		this.absComponenteSanguineo = absComponenteSanguineo;
	}

	@Column(name = "JUSTIFICATIVA", nullable = false, length = 4000)
	@Length(max = 4000)
	public String getJustificativa() {
		return this.justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	@Column(name = "QTDE", nullable = false)
	public Short getQtde() {
		return this.qtde;
	}

	public void setQtde(Short qtde) {
		this.qtde = qtde;
	}

	@Column(name = "EFETIVADO", length = 1)
	@Length(max = 1)
	public String getEfetivado() {
		return this.efetivado;
	}

	public void setEfetivado(String efetivado) {
		this.efetivado = efetivado;
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
		AEL_PROJETO_PACIENTES("aelProjetoPacientes"),
		RAP_SERVIDORES("rapServidores"),
		ABS_COMPONENTE_SANGUINEO("absComponenteSanguineo"),
		JUSTIFICATIVA("justificativa"),
		QTDE("qtde"),
		EFETIVADO("efetivado"),
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
		if (!(obj instanceof AelProjetoIntercComp)) {
			return false;
		}
		AelProjetoIntercComp other = (AelProjetoIntercComp) obj;
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
