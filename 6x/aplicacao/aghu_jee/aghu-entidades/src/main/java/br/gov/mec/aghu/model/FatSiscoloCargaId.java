package br.gov.mec.aghu.model;

// Generated 15/03/2011 13:46:58 by Hibernate Tools 3.2.5.Beta

import javax.persistence.Column;
import javax.persistence.Embeddable;


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

/**
 * FatSiscoloCargasId generated by hbm2java
 */
@Embeddable
public class FatSiscoloCargaId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4657873426765352405L;
	private String numSolic;
	private String nomePaciente;
	private String prontuario;
	private String dtLiberacao;

	public FatSiscoloCargaId() {
	}

	public FatSiscoloCargaId(String dtLiberacao) {
		this.dtLiberacao = dtLiberacao;
	}

	public FatSiscoloCargaId(String numSolic, String nomePaciente,
			String prontuario, String dtLiberacao) {
		this.numSolic = numSolic;
		this.nomePaciente = nomePaciente;
		this.prontuario = prontuario;
		this.dtLiberacao = dtLiberacao;
	}

	@Column(name = "NUM_SOLIC", length = 15)
	@Length(max = 15)
	public String getNumSolic() {
		return this.numSolic;
	}

	public void setNumSolic(String numSolic) {
		this.numSolic = numSolic;
	}

	@Column(name = "NOME_PACIENTE", length = 50)
	@Length(max = 50)
	public String getNomePaciente() {
		return this.nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	@Column(name = "PRONTUARIO", length = 10)
	@Length(max = 10)
	public String getProntuario() {
		return this.prontuario;
	}

	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}

	@Column(name = "DT_LIBERACAO", nullable = false, length = 10)
	@Length(max = 10)
	public String getDtLiberacao() {
		return this.dtLiberacao;
	}

	public void setDtLiberacao(String dtLiberacao) {
		this.dtLiberacao = dtLiberacao;
	}

	@Override
	public boolean equals(Object other) {
		if ((this == other)){
			return true;
		}
		if ((other == null)){
			return false;
		}
		if (!(other instanceof FatSiscoloCargaId)){
			return false;
		}
		FatSiscoloCargaId castOther = (FatSiscoloCargaId) other;

		return ((this.getNumSolic() == castOther.getNumSolic()) || (this
				.getNumSolic() != null
				&& castOther.getNumSolic() != null && this.getNumSolic()
				.equals(castOther.getNumSolic())))
				&& ((this.getNomePaciente() == castOther.getNomePaciente()) || (this
						.getNomePaciente() != null
						&& castOther.getNomePaciente() != null && this
						.getNomePaciente().equals(castOther.getNomePaciente())))
				&& ((this.getProntuario() == castOther.getProntuario()) || (this
						.getProntuario() != null
						&& castOther.getProntuario() != null && this
						.getProntuario().equals(castOther.getProntuario())))
				&& ((this.getDtLiberacao() == castOther.getDtLiberacao()) || (this
						.getDtLiberacao() != null
						&& castOther.getDtLiberacao() != null && this
						.getDtLiberacao().equals(castOther.getDtLiberacao())));
	}

	@Override
	public int hashCode() {
		int result = 17;

		result = 37 * result
				+ (getNumSolic() == null ? 0 : this.getNumSolic().hashCode());
		result = 37
				* result
				+ (getNomePaciente() == null ? 0 : this.getNomePaciente()
						.hashCode());
		result = 37
				* result
				+ (getProntuario() == null ? 0 : this.getProntuario()
						.hashCode());
		result = 37
				* result
				+ (getDtLiberacao() == null ? 0 : this.getDtLiberacao()
						.hashCode());
		return result;
	}

}
