package br.gov.mec.aghu.model;

import java.io.Serializable;
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
@SequenceGenerator(name="aipPbuSq1", sequenceName="AGH.AIP_PBU_SQ1", allocationSize = 1)
@Table(name = "AIP_PACIENTES_BLOQUEIO_UBS", schema = "AGH")
public class AipPacientesBloqueioUbs extends BaseEntitySeq<Integer> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1750292334755215146L;
	
	private Integer seq;
	private AipPacientes paciente;
	private Boolean indPacienteBloqueado;
	private Date criadoEm;
	private RapServidores servidor;
	private String motivoBloqueio;
	private Integer version;
		
	
	public AipPacientesBloqueioUbs() {
	}
	
	public AipPacientesBloqueioUbs(Integer seq, AipPacientes paciente,
			Boolean indPacienteBloqueado, Date criadoEm, RapServidores servidor) {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aipPbuSq1")
	@Column(name = "SEQ", nullable = false, precision = 8, scale = 0)
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PAC_CODIGO", referencedColumnName="CODIGO", nullable = false)
	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	@Column(name = "IND_PACIENTE_BLOQUEADO", nullable = false, length=1)
	@org.hibernate.annotations.Type( type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")	
	public Boolean getIndPacienteBloqueado() {
		return indPacienteBloqueado;
	}

	public void setIndPacienteBloqueado(Boolean indPacienteBloqueado) {
		this.indPacienteBloqueado = indPacienteBloqueado;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM")
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}
	
	
	@Column(name = "MOTIVO_BLOQUEIO", nullable = false, length = 50)
	public String getMotivoBloqueio() {
		return motivoBloqueio;
	}

	public void setMotivoBloqueio(String motivoBloqueio) {
		this.motivoBloqueio = motivoBloqueio;
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

		SEQ("seq"), PACIENTE("paciente"), PAC_CODIGO("paciente.codigo"), IND_PACIENTE_BLOQUEADO("indPacienteBloqueado"), CRIADO_EM("criadoEm");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		AipPacientesBloqueioUbs other = (AipPacientesBloqueioUbs) obj;
		if (seq == null) {
			if (other.seq != null) {
				return false;
			}
		} else if (!seq.equals(other.seq)) {
			return false;
		}
		return true;
	}
	
	

}
