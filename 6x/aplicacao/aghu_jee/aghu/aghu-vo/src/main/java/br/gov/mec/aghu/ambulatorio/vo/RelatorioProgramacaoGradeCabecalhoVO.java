package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;
import java.util.List;

public class RelatorioProgramacaoGradeCabecalhoVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5313189637787646901L;

	List<RelatorioProgramacaoGradeVO> grades;
	
	public List<RelatorioProgramacaoGradeVO> getGrades() {
		return grades;
	}

	public void setGrades(List<RelatorioProgramacaoGradeVO> grades) {
		this.grades = grades;
	}
	
}
