package br.gov.mec.aghu.model;

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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Table(name = "MCI_CRITERIOS_GMR", schema = "AGH")
public class MciCriterioGmr extends BaseEntityId<MciCriterioGmrId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1483129515056826309L;
	private MciCriterioGmrId id;
	private DominioSituacao situacao;
	private Date criadoEm;
	private Date alteradoEm;
	private RapServidores servidor;
	private RapServidores servidorMovimentado;
	private Integer version;
	private MciAntimicrobianos mciAntiMicrobianos;

	public MciCriterioGmr() {
	}

	public MciCriterioGmr(MciCriterioGmrId id, DominioSituacao situacao, 
			Date criadoEm, RapServidores servidor) {
		this.id = id;
		this.situacao = situacao;
		this.criadoEm = criadoEm;
		this.servidor = servidor;
	}
	
	public MciCriterioGmr(MciCriterioGmrId id, DominioSituacao situacao,
			Date criadoEm, Date alteradoEm, RapServidores servidor,
			RapServidores servidorMovimentado) {
		super();
		this.id = id;
		this.situacao = situacao;
		this.criadoEm = criadoEm;
		this.alteradoEm = alteradoEm;
		this.servidor = servidor;
		this.servidorMovimentado = servidorMovimentado;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "bmrSeq", column = @Column(name = "BMR_SEQ", nullable = false)),
			@AttributeOverride(name = "ambSeq", column = @Column(name = "AMB_SEQ", nullable = false)) })
	public MciCriterioGmrId getId() {
		return id;
	}

	public void setId(MciCriterioGmrId id) {
		this.id = id;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ALTERADO_EM")
	public Date getAlteradoEm() {
		return this.alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
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
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA_MOVIMENTADO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_MOVIMENTADO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorMovimentado() {
		return this.servidorMovimentado;
	}

	public void setServidorMovimentado(
			RapServidores servidorMovimentado) {
		this.servidorMovimentado = servidorMovimentado;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@OneToOne
	@JoinColumn(name = "AMB_SEQ", referencedColumnName = "SEQ", insertable = false, updatable = false) 
	public MciAntimicrobianos getMciAntiMicrobianos() {
		return mciAntiMicrobianos;
	}

	public void setMciAntiMicrobianos(MciAntimicrobianos mciAntiMicrobianos) {
		this.mciAntiMicrobianos = mciAntiMicrobianos;
	}	
	
	public enum Fields {
		
		ID("id"),
		AMB_SEQ("id.ambSeq"),
		BMR_SEQ("id.bmrSeq"),
		ANTIMICROBIANOS("mciAntiMicrobianos"),
		SITUACAO("situacao"),
		CRIADO_EM("criadoEm"),
		ALTERADO_EM("alteradoEm"),
		SERVIDOR("servidor"),
		SERVIDOR_MOVIMENTADO("servidorMovimentado"),
		VERSION("version");
		
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
			if (!(obj instanceof MciCriterioGmr)) {
				return false;
			}
			MciCriterioGmr other = (MciCriterioGmr) obj;
			if (getId() == null) {
				if (other.getId() != null) {
					return false;
				}
			} else if (!getId().equals(other.getId())) {
				return false;
			}
			return true;
		}
}
