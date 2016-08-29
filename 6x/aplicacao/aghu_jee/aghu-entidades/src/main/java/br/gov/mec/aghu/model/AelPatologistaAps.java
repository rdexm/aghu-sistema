package br.gov.mec.aghu.model;


import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name="aelLupSq1", sequenceName="AGH.AEL_LUP_SQ1", allocationSize = 1)
@Table(name = "AEL_PATOLOGISTA_APS")
public class AelPatologistaAps extends BaseEntitySeq<Integer> implements java.io.Serializable {

	private static final long serialVersionUID = 7151261688391916499L;
	
	private Integer seq;
	private Short ordemMedicoLaudo;
	private Date criadoEm;
	private AelExameAp aelExameAps;
	private RapServidores servidor;
	private Integer version;
	
	public AelPatologistaAps() {
		super();
	}

	public AelPatologistaAps(Integer seq, Short ordemMedicoLaudo, Date criadoEm,
			AelExameAp aelExameAps, RapServidores servidor) {
		super();
		this.seq = seq;
		this.ordemMedicoLaudo = ordemMedicoLaudo;
		this.criadoEm = criadoEm;
		this.aelExameAps = aelExameAps;
		this.servidor = servidor;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aelLupSq1")
	@Column(name = "SEQ", unique = true, nullable = false, precision = 6, scale = 0)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable=false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable=false) })
	public RapServidores getServidor() {
		return this.servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "LUX_SEQ", nullable = false)
	public AelExameAp getAelExameAps() {
		return this.aelExameAps;
	}

	public void setAelExameAps(AelExameAp aelExameAps) {
		this.aelExameAps = aelExameAps;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "ORDEM_MEDICO_LAUDO", precision = 2, scale = 0)
	public Short getOrdemMedicoLaudo() {
		return ordemMedicoLaudo;
	}

	public void setOrdemMedicoLaudo(Short ordemMedicoLaudo) {
		this.ordemMedicoLaudo = ordemMedicoLaudo;
	}
	
	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	public enum Fields {
		SEQ("seq"),
		ORDEM_MEDICO_LAUDO("ordemMedicoLaudo"),
		AEL_EXAME_APS("aelExameAps"),
		AEL_EXAME_APS_SEQ("aelExameAps.seq"),
		SERVIDOR("servidor"),
		CRIADO_EM("criadoEm"),
		VERSION("version");

		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
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
		if (!(obj instanceof AelPatologistaAps)) {
			return false;
		}
		AelPatologistaAps other = (AelPatologistaAps) obj;
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