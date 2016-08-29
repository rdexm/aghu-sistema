package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;
import java.util.Date;

public class ResponsavelAnamneseVO implements Serializable {
	
	private static final long serialVersionUID = -118454728018363L;
	
	private Integer matricula;
	private Short vinculo;
	private Integer matriculaValida;
	private Short vinculoValida;
	private Date dthrValida;
	
	public enum Fields {
		MATRICULA("matricula"),
		VINCULO("vinculo"),
		MATRICULA_VALIDA("matriculaValida"),
		VINCULO_VALIDA("vinculoValida"),
		DTHR_VALIDA("dthrValida");
		
		private String field;
		
		private Fields(String field) {
			this.field = field;
		}
		
		@Override
		public String toString() {
			return this.field;
		}
	}

	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	public Short getVinculo() {
		return vinculo;
	}

	public void setVinculo(Short vinculo) {
		this.vinculo = vinculo;
	}

	public Integer getMatriculaValida() {
		return matriculaValida;
	}

	public void setMatriculaValida(Integer matriculaValida) {
		this.matriculaValida = matriculaValida;
	}

	public Short getVinculoValida() {
		return vinculoValida;
	}

	public void setVinculoValida(Short vinculoValida) {
		this.vinculoValida = vinculoValida;
	}

	public Date getDthrValida() {
		return dthrValida;
	}

	public void setDthrValida(Date dthrValida) {
		this.dthrValida = dthrValida;
	}
}
