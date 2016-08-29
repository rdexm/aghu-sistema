package br.gov.mec.aghu.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;

import br.gov.mec.aghu.core.persistence.BaseEntity;

@Entity
@Table(name = "AIP_PACIENTE_PRONTUARIO", schema = "AGH")
public class AipPacienteProntuario implements BaseEntity{
	
	private static final long serialVersionUID = -7927681671447937530L;
	
	private Integer prontuario;
	private AghSamis samis;
	private Date criadoEm;
	private RapServidores servidor;
	private Integer version;
	
	@Id
	@Column(name = "PAC_PRONTUARIO", updatable = false, nullable = false, precision = 8, scale = 0)
	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SEQ_SAMIS", referencedColumnName = "SEQ", nullable = true)
	public AghSamis getSamis() {
		return samis;
	}

	public void setSamis(AghSamis samis) {
		this.samis = samis;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM")
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	@NotNull
	public RapServidores getServidor() {
		return servidor;
	}
	
	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}
	
	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	public enum Fields {
		PRONTUARIO("prontuario"),
		SAMIS("samis"),
		SERVIDOR_MATRICULA("servidor.id.matricula"),
		SERVIDOR_VINCULO("servidor.id.vinCodigo");
		private String fields;
		private Fields(String fields) {
			this.fields = fields;
		}
		@Override
		public String toString() {
			return fields;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((prontuario == null) ? 0 : prontuario.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof AipPacienteProntuario)) {
			return false;
		}
		AipPacienteProntuario other = (AipPacienteProntuario) obj;
		if (prontuario == null) {
			if (other.prontuario != null) {
				return false;
			}
		} else if (!prontuario.equals(other.prontuario)) {
			return false;
		}
		return true;
	}

		

}
