package br.gov.mec.aghu.internacao.vo;

import java.io.Serializable;

/**
 * VO que representa alguns dados de uma internacao
 * 
 * @author luismoura
 * 
 */
public class DadosInternacaoUrgenciaVO implements Serializable {
	private static final long serialVersionUID = -4608991880969266036L;

	private Integer seq;
	private Integer matriculaProfessor;
	private Short vinCodigoProfessor;

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Integer getMatriculaProfessor() {
		return matriculaProfessor;
	}

	public void setMatriculaProfessor(Integer matriculaProfessor) {
		this.matriculaProfessor = matriculaProfessor;
	}

	public Short getVinCodigoProfessor() {
		return vinCodigoProfessor;
	}

	public void setVinCodigoProfessor(Short vinCodigoProfessor) {
		this.vinCodigoProfessor = vinCodigoProfessor;
	}
}
