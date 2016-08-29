package br.gov.mec.aghu.perinatologia.vo;

import java.io.Serializable;

/**
 * Representa todos os resultado de exames em uma data
 * 
 * @author luismoura
 * 
 */
public class ExameResultados implements Serializable {
	private static final long serialVersionUID = 7934619755299675443L;

	private UnidadeExamesSignificativoPerinatologiaVO exame;
	private ResultadoExameSignificativoPerinatologiaVO[] resultados;

	public ExameResultados() {
	}
	
	public ExameResultados(UnidadeExamesSignificativoPerinatologiaVO exame) {
		this.exame = exame;
		this.resultados = new ResultadoExameSignificativoPerinatologiaVO[0];
	}

	public UnidadeExamesSignificativoPerinatologiaVO getExame() {
		return exame;
	}

	public void setExame(UnidadeExamesSignificativoPerinatologiaVO exame) {
		this.exame = exame;
	}

	public ResultadoExameSignificativoPerinatologiaVO[] getResultados() {
		return resultados;
	}

	public void setResultados(ResultadoExameSignificativoPerinatologiaVO[] resultados) {
		this.resultados = resultados;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((exame == null) ? 0 : exame.hashCode());
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		ExameResultados other = (ExameResultados) obj;
		if (exame == null) {
			if (other.exame != null) {
				return false;
			}
		} else if (!exame.equals(other.exame)) {
			return false;
		}
		return true;
	}

}
