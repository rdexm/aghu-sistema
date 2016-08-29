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


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Table(name = "AEL_PROJETO_INTERC_PROCS", schema = "AGH")
public class AelProjetoIntercProc extends BaseEntityId<AelProjetoIntercProcId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8027968310889544431L;
	private AelProjetoIntercProcId id;
	private Integer version;
	private AelProjetoPacientes projetoPaciente;
	private RapServidores servidor;
	private MbcProcedimentoCirurgicos procedimentoCirurgico;
	private String justificativa;
	private Short qtde;
	private Boolean efetivado;
	private Date criadoEm;

	public AelProjetoIntercProc() {
	}

	public AelProjetoIntercProc(AelProjetoIntercProcId id, AelProjetoPacientes projetoPaciente, RapServidores servidor,
			MbcProcedimentoCirurgicos procedimentoCirurgico, String justificativa, Short qtde, Date criadoEm) {
		this.id = id;
		this.projetoPaciente = projetoPaciente;
		this.servidor = servidor;
		this.procedimentoCirurgico = procedimentoCirurgico;
		this.justificativa = justificativa;
		this.qtde = qtde;
		this.criadoEm = criadoEm;
	}

	public AelProjetoIntercProc(AelProjetoIntercProcId id, AelProjetoPacientes projetoPaciente, RapServidores servidor,
			MbcProcedimentoCirurgicos procedimentoCirurgico, String justificativa, Short qtde, Boolean efetivado, Date criadoEm) {
		this.id = id;
		this.projetoPaciente = projetoPaciente;
		this.servidor = servidor;
		this.procedimentoCirurgico = procedimentoCirurgico;
		this.justificativa = justificativa;
		this.qtde = qtde;
		this.efetivado = efetivado;
		this.criadoEm = criadoEm;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "ppjPjqSeq", column = @Column(name = "PPJ_PJQ_SEQ", nullable = false)),
			@AttributeOverride(name = "ppjPacCodigo", column = @Column(name = "PPJ_PAC_CODIGO", nullable = false)),
			@AttributeOverride(name = "pciSeq", column = @Column(name = "PCI_SEQ", nullable = false)),
			@AttributeOverride(name = "seqp", column = @Column(name = "SEQP", nullable = false)) })
	public AelProjetoIntercProcId getId() {
		return this.id;
	}

	public void setId(AelProjetoIntercProcId id) {
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
	public AelProjetoPacientes getProjetoPaciente() {
		return projetoPaciente;
	}
	
	public void setProjetoPaciente(AelProjetoPacientes projetoPaciente) {
		this.projetoPaciente = projetoPaciente;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return servidor;
	}
	
	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PCI_SEQ", nullable = false, insertable = false, updatable = false)
	public MbcProcedimentoCirurgicos getProcedimentoCirurgico() {
		return procedimentoCirurgico;
	}
	
	public void setProcedimentoCirurgico(MbcProcedimentoCirurgicos procedimentoCirurgico) {
		this.procedimentoCirurgico = procedimentoCirurgico;
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
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getEfetivado() {
		return efetivado;
	}
	
	public void setEfetivado(Boolean efetivado) {
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
		AEL_PROJETO_PACIENTES("projetoPaciente"),
		RAP_SERVIDORES("servidor"),
		MBC_PROCEDIMENTO_CIRURGICOS("procedimentoCirurgico"),
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
		if (!(obj instanceof AelProjetoIntercProc)) {
			return false;
		}
		AelProjetoIntercProc other = (AelProjetoIntercProc) obj;
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
