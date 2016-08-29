package br.gov.mec.aghu.exames.vo;

import java.io.Serializable;
import java.util.List;

import br.gov.mec.aghu.model.AelExameAp;

public class AelAmostraRecebidaVO implements Serializable {

	private static final long serialVersionUID = 4182754562216041849L;

	private List<ImprimeEtiquetaVO> etiquetas;
	private AelExameAp aelExamep;

	public void setEtiquetas(List<ImprimeEtiquetaVO> etiquetas) {
		this.etiquetas = etiquetas;
	}

	public List<ImprimeEtiquetaVO> getEtiquetas() {
		return etiquetas;
	}

	public void setAelExamep(AelExameAp aelExamep) {
		this.aelExamep = aelExamep;
	}

	public AelExameAp getAelExamep() {
		return aelExamep;
	}

}
