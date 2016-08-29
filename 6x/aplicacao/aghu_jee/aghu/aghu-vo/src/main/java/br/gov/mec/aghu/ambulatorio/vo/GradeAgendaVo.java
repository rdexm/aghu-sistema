package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;

/**
 * Filtro para busca de usuário através do web service.
 * 
 * @author Flavio Rutkowski
 * 
 */
public class GradeAgendaVo implements Serializable {

	private static final long serialVersionUID = -4195817103912553934L;

	private Integer gradeSeq;
	private Short UnidadeFuncionalSeq;
	
	public Integer getGradeSeq() {
		return gradeSeq;
	}
	public void setGradeSeq(Integer gradeSeq) {
		this.gradeSeq = gradeSeq;
	}
	public Short getUnidadeFuncionalSeq() {
		return UnidadeFuncionalSeq;
	}
	public void setUnidadeFuncionalSeq(Short unidadeFuncionalSeq) {
		UnidadeFuncionalSeq = unidadeFuncionalSeq;
	}

	
}
