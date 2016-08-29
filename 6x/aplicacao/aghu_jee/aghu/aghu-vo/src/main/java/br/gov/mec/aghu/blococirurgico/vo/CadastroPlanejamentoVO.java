package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;

import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;

public class CadastroPlanejamentoVO implements Serializable {

	private static final long serialVersionUID = 4418729048192642723L;
	
	private AipPacientes paciente;
	private AghUnidadesFuncionais unidadeFuncional;
	private FatConvenioSaude fatConvenioSaude;
	private FatConvenioSaudePlano convenioSaudePlano;
	
	public AipPacientes getPaciente() {
		return paciente;
	}
	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}
	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}
	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}
	public FatConvenioSaude getFatConvenioSaude() {
		return fatConvenioSaude;
	}
	public void setFatConvenioSaude(FatConvenioSaude fatConvenioSaude) {
		this.fatConvenioSaude = fatConvenioSaude;
	}
	public FatConvenioSaudePlano getConvenioSaudePlano() {
		return convenioSaudePlano;
	}
	public void setConvenioSaudePlano(FatConvenioSaudePlano convenioSaudePlano) {
		this.convenioSaudePlano = convenioSaudePlano;
	}
	
}
