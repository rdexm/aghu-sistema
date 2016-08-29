package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;
import java.util.Date;


public class DataInicioFimVO  implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8685846638754798690L;
	private Date dataInicial;
	private Date dataFinal;
	
	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}
	
	public Date getDataInicial() {
		return dataInicial;
	}
	
	public DataInicioFimVO(Date dataInicial, Date dataFinal) {
		super();
		this.dataInicial = dataInicial;
		this.dataFinal = dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}
	
	public Date getDataFinal() {
		return dataFinal;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((dataFinal == null) ? 0 : dataFinal.hashCode());
		result = prime * result
				+ ((dataInicial == null) ? 0 : dataInicial.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		DataInicioFimVO other = (DataInicioFimVO) obj;
		if (dataFinal == null) {
			if (other.dataFinal != null){
				return false;
			}
		} else if (!dataFinal.equals(other.dataFinal)){
			return false;
		}
		if (dataInicial == null) {
			if (other.dataInicial != null){
				return false;
			}
		} else if (!dataInicial.equals(other.dataInicial)){
			return false;
		}
		return true;
	}
	
	
}
