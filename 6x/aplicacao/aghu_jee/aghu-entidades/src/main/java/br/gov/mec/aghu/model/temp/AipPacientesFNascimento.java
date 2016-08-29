package br.gov.mec.aghu.model.temp;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.model.AipFonemaPacientes;
import br.gov.mec.aghu.model.AipFonemasMaePaciente;
import br.gov.mec.aghu.core.persistence.BaseEntityCodigo;

@Entity
@Table(name = "AIP_PACIENTES", schema = "FNASCIMENTO")
public class AipPacientesFNascimento extends BaseEntityCodigo<Integer> implements java.io.Serializable {

	private static final long serialVersionUID = -1667658733038187340L;

	private Integer codigo;

	private String nome;
	
	private String nomeMae;

	private String nomePai;
	
	private Integer prontuario;
	
	private Set<AipFonemaPacientes> fonemas = new HashSet<AipFonemaPacientes>(0);

	private Set<AipFonemasMaePaciente> fonemasMae = new HashSet<AipFonemasMaePaciente>(
			0);

	public AipPacientesFNascimento() {
	}

	@Id
	@Column(name = "CODIGO", updatable = false, nullable = false, precision = 8, scale = 0)
	// @DocumentId
	public Integer getCodigo() {
		return this.codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	@Column(name = "NOME", nullable = false, length = 50)
	@Length(max = 50)
	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = (nome == null ? null : nome.toUpperCase());
	}

	@Column(name = "NOME_MAE", nullable = false, length = 50)
	@Length(max = 50)
	public String getNomeMae() {
		return this.nomeMae;
	}

	public void setNomeMae(String nomeMae) {
		this.nomeMae = (nomeMae == null ? null : nomeMae.toUpperCase());
	}

	@Column(name = "NOME_PAI", length = 50)
	@Length(max = 50)
	public String getNomePai() {
		return this.nomePai;
	}

	public void setNomePai(String nomePai) {
		this.nomePai = (nomePai == null ? null : nomePai.toUpperCase());
	}

	@Column(name = "PRONTUARIO", unique = true, precision = 8, scale = 0)
	//@Prontuario
	public Integer getProntuario() {
		return this.prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "aipPaciente")
	public Set<AipFonemaPacientes> getFonemas() {
		return this.fonemas;
	}

	public void setFonemas(Set<AipFonemaPacientes> fonemas) {
		this.fonemas = fonemas;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "aipPaciente")
	public Set<AipFonemasMaePaciente> getFonemasMae() {
		return this.fonemasMae;
	}

	public void setFonemasMae(Set<AipFonemasMaePaciente> fonemasMae) {
		this.fonemasMae = fonemasMae;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((this.codigo == null) ? 0 : this.codigo.hashCode());
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
		if (!(obj instanceof AipPacientesFNascimento)) {
			return false;
		}
		AipPacientesFNascimento other = (AipPacientesFNascimento) obj;
		if (this.codigo == null) {
			if (other.codigo != null) {
				return false;
			}
		} else if (!this.codigo.equals(other.codigo)) {
			return false;
		}
		return true;
	}

}
