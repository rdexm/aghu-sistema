package br.gov.mec.aghu.blococirurgico.portalplanejamento.vo;

import java.io.Serializable;
import java.util.List;

public class EscalaSalasEquipeVO implements Serializable {

	private static final long				serialVersionUID	= -1625771196500664440L;

	private List<EscalaSalasProfisionaisVO>	profissionais;
	private boolean							urgencia;
	private boolean							particular;
//	private String diaSemana;

	public List<EscalaSalasProfisionaisVO> getProfissionais() {
		return profissionais;
	}

	public void setProfissionais(List<EscalaSalasProfisionaisVO> profissionais) {
		this.profissionais = profissionais;
	}

	public boolean isUrgencia() {
		return urgencia;
	}

	public void setUrgencia(boolean urgencia) {
		this.urgencia = urgencia;
	}

	public boolean isParticular() {
		return particular;
	}

	public void setParticular(boolean particular) {
		this.particular = particular;
	}

//	public void setDiaSemana(String diaSemana) {
//		this.diaSemana = diaSemana;
//	}
//
//	public String getDiaSemana() {
//		return diaSemana;
//	}

}
