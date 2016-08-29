package br.gov.mec.aghu.sig.custos.vo;

import java.util.ArrayList;
import java.util.List;

import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.core.commons.BaseBean;


public class VisualizarCustoPacienteVO implements BaseBean {
	
	private static final long serialVersionUID = 6085958557329325274L;

	private Integer prontuarioPaciente;
	
	private Integer codigoPaciente;
	
	private AipPacientes aipPaciente;
	
	private String nomePacienteCirurgico;
	
	private List<CompetenciaVO> listaCompetencia;
	
	private Boolean pacienteComAlta;
	
	private AghCid aghCid;
	
	private FccCentroCustos centroCusto;
	
	private AghEspecialidades especialidades;
	
	private AghEquipes equipes;
	
	public Integer getProntuarioPaciente() {
		return prontuarioPaciente;
	}

	public void setProntuarioPaciente(Integer prontuarioPaciente) {
		this.prontuarioPaciente = prontuarioPaciente;
	}

	public Integer getCodigoPaciente() {
		return codigoPaciente;
	}

	public void setCodigoPaciente(Integer codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
	}

	public AipPacientes getAipPaciente() {
		if(this.aipPaciente== null){
			this.aipPaciente = new AipPacientes();
		}
		return aipPaciente;
	}

	public void setAipPaciente(AipPacientes aipPaciente) {
		this.aipPaciente = aipPaciente;
	}

	public String getNomePacienteCirurgico() {
		return nomePacienteCirurgico;
	}

	public void setNomePacienteCirurgico(String nomePacienteCirurgico) {
		this.nomePacienteCirurgico = nomePacienteCirurgico;
	}

	public List<CompetenciaVO> getListaCompetencia() {
		if(listaCompetencia == null){
			this.listaCompetencia = new ArrayList<CompetenciaVO>();
		}
		return listaCompetencia;
	}

	public void setListaCompetencia(List<CompetenciaVO> listaCompetencia) {
		this.listaCompetencia = listaCompetencia;
	}

	public void setPacienteComAlta(Boolean pacienteComAlta) {
		this.pacienteComAlta = pacienteComAlta;
	}

	public Boolean getPacienteComAlta() {
		return pacienteComAlta;
	}

	public void setAghCid(AghCid aghCid) {
		this.aghCid = aghCid;
	}

	public AghCid getAghCid() {
		return aghCid;
	}

	public void setCentroCusto(FccCentroCustos centroCusto) {
		this.centroCusto = centroCusto;
	}

	public FccCentroCustos getCentroCusto() {
		return centroCusto;
	}

	public void setEspecialidades(AghEspecialidades especialidades) {
		this.especialidades = especialidades;
	}

	public AghEspecialidades getEspecialidades() {
		return especialidades;
	}

	public void setEquipes(AghEquipes equipes) {
		this.equipes = equipes;
	}

	public AghEquipes getEquipes() {
		return equipes;
	}

		
}
