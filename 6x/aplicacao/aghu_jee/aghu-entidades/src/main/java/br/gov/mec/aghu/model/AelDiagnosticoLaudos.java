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
import javax.validation.constraints.NotNull;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


@Entity
@SequenceGenerator(name="aelDluSq1", sequenceName="AGH.AEL_DLU_SQ1", allocationSize = 1)
@Table(name = "AEL_DIAGNOSTICO_LAUDOS") 
public class AelDiagnosticoLaudos extends BaseEntitySeq<Long> {

	private static final long serialVersionUID = 943837523859993708L;
	
	private Long seq;
	private AelExameAp exameAp;
	private AghCid aghCid;
	private AelCidos aelCidos;
	private Date criadoEm;
	private RapServidores servidor;
	private Integer version;
	
	public AelDiagnosticoLaudos() {
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aelDluSq1")
	@Column(name = "SEQ", unique = true, nullable = false, precision = 14, scale = 0)
	public Long getSeq() {
		return this.seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	@NotNull
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
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
	
	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "LUX_SEQ", nullable=false)
	@NotNull
	public AelExameAp getExameAp() {
		return exameAp;
	}

	public void setExameAp(AelExameAp exameAp) {
		this.exameAp = exameAp;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CID_SEQ")
	public AghCid getAghCid() {
		return aghCid;
	}

	public void setAghCid(AghCid aghCid) {
		this.aghCid = aghCid;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CIO_SEQ")
	public AelCidos getAelCidos() {
		return aelCidos;
	}

	public void setAelCidos(AelCidos aelCidos) {
		this.aelCidos = aelCidos;
	}

	public enum Fields {		
		SEQ("seq"), 
		SEQ_EXAME("exameAp.seq"),
		SEQ_CID("aghCid.seq"),
		SEQ_CIDO("aelCidos.seq"),
		EXAME("exameAp"),
		CID("aghCid"),
		CIDOS("aelCidos"),
		CRIADO_EM("criadoEm"),
		SERVIDOR("servidor"),
		VERSION("version");
		
		private String fields;

		private Fields(String fields) {
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
		if (!(obj instanceof AelDiagnosticoLaudos)) {
			return false;
		}
		AelDiagnosticoLaudos other = (AelDiagnosticoLaudos) obj;
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
