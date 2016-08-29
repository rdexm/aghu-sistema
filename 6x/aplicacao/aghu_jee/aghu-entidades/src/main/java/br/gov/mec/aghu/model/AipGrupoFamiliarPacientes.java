package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;
@Entity
@Table(name = "AIP_GRUPO_FAMILIAR_PACIENTES", schema = "AGH")
public class AipGrupoFamiliarPacientes extends BaseEntitySeq<Integer>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2139379574772250742L;
	
	private Integer seq;
	private Integer agfSeq;
	private AipGrupoFamiliar grupoFamiliar;
	private AipPacientes paciente;
	

	public AipGrupoFamiliarPacientes(){
		
	}
	
	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getSeq());
		return umHashCodeBuilder.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof AipGrupoFamiliarPacientes)) {
			return false;
		}
		AipGrupoFamiliarPacientes other = (AipGrupoFamiliarPacientes) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getSeq(), other.getSeq());	
		return umEqualsBuilder.isEquals();
	}

	
	
	public enum Fields {
		GRUPO_FAMILIAR("grupoFamiliar"),
		PACIENTE("paciente"),
		PAC_CODIGO("seq"),
		AGF_SEQ("agfSeq");
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AGF_SEQ",  insertable=false,updatable=false)
	public AipGrupoFamiliar getGrupoFamiliar() {
		return grupoFamiliar;
	}

	public void setGrupoFamiliar(AipGrupoFamiliar grupoFamiliar) {
		this.grupoFamiliar = grupoFamiliar;
	}
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PAC_CODIGO",insertable=false, updatable=false)
	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}
	
	@Column(name = "AGF_SEQ")
	public Integer getAgfSeq() {
		return agfSeq;
	}

	public void setAgfSeq(Integer agfSeq) {
		this.agfSeq = agfSeq;
	}

	@Override
	@Id
	@Column(name = "PAC_CODIGO")
	public Integer getSeq() {
		return seq;
	}

	@Override
	public void setSeq(Integer seq) {
		this.seq = seq;
	}

}
