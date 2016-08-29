package br.gov.mec.aghu.model.temp;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntityCodigo;
import br.gov.mec.aghu.model.AipFonemaNomeSocialPacientes;

@Entity
@Table(name = "AIP_PACIENTES", schema = "AGH")
public class AipPacientesRotinaFonema extends BaseEntityCodigo<Integer> implements java.io.Serializable {
	
	private static final long serialVersionUID = 2054696635323298735L;
	
	private Integer codigo;
	private String nome;
	private String nomeMae;
	private String nomeSocial;
	private Set<AipFonemaPacientesRotinaFonema> fonemas = new HashSet<AipFonemaPacientesRotinaFonema>(0);
	private Set<AipFonemasMaePacienteRotinaFonema> fonemasMae = new HashSet<AipFonemasMaePacienteRotinaFonema>(0);	
	private Set<AipFonemaNomeSocialPacientes> fonemasNomeSocial = new HashSet<AipFonemaNomeSocialPacientes>();
	
	@Id
	@Column(name = "CODIGO", updatable = false, nullable = false, precision = 8, scale = 0)
	// @DocumentId
	public Integer getCodigo() {
		return this.codigo;
	}
	
	@Column(name = "NOME", nullable = false, length = 50)
	@Length(max = 50)
	public String getNome() {
		return this.nome;
	}

	@Column(name = "NOME_MAE", nullable = false, length = 50)
	@Length(max = 50)
	public String getNomeMae() {
		return this.nomeMae;
	}
	
	@Column(name = "NOME_SOCIAL", nullable = false, length = 50)
	@NotNull
	@Length(max = 50)
	public String getNomeSocial() {
		return nomeSocial;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "aipPaciente")
	public Set<AipFonemaNomeSocialPacientes> getFonemasNomeSocial() {
		return fonemasNomeSocial;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "aipPaciente")
	public Set<AipFonemaPacientesRotinaFonema> getFonemas() {
		return this.fonemas;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "aipPaciente")
	public Set<AipFonemasMaePacienteRotinaFonema> getFonemasMae() {
		return this.fonemasMae;
	}	

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public void setNomeSocial(String nomeSocial) {
		this.nomeSocial = nomeSocial;
	}

	public void setNomeMae(String nomeMae) {
		this.nomeMae = nomeMae;
	}

	public void setFonemas(Set<AipFonemaPacientesRotinaFonema> fonemas) {
		this.fonemas = fonemas;
	}

	public void setFonemasMae(Set<AipFonemasMaePacienteRotinaFonema> fonemasMae) {
		this.fonemasMae = fonemasMae;
	}	
	
	public void setFonemasNomeSocial(Set<AipFonemaNomeSocialPacientes> fonemasNomeSocial) {
		this.fonemasNomeSocial = fonemasNomeSocial;
	}

}
