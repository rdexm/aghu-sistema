package br.gov.mec.aghu.ambulatorio.vo;

import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.core.commons.BaseBean;

public class HorarioConsultaVO implements BaseBean {

	private static final long serialVersionUID = 6367988824851660199L;

	private AacConsultas consulta;

	private Boolean selecionado;

	private boolean habilitaCheck;

	public HorarioConsultaVO(AacConsultas consulta) {
		this.consulta = consulta;
		selecionado = false;
	}

	/**
	 * @return the consulta
	 */
	public AacConsultas getConsulta() {
		return consulta;
	}

	/**
	 * @param consulta
	 *            the consulta to set
	 */
	public void setConsulta(AacConsultas consulta) {
		this.consulta = consulta;
	}

	/**
	 * @return the selecionado
	 */
	public Boolean getSelecionado() {
		return selecionado;
	}

	/**
	 * @param selecionado
	 *            the selecionado to set
	 */
	public void setSelecionado(Boolean selecionado) {
		this.selecionado = selecionado;
	}

	public boolean isHabilitaCheck() {
		return habilitaCheck;
	}

	public void setHabilitaCheck(boolean habilitaCheck) {
		this.habilitaCheck = habilitaCheck;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((consulta == null) ? 0 : consulta.hashCode());
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
		if (!(obj instanceof HorarioConsultaVO)) {
			return false;
		}
		HorarioConsultaVO other = (HorarioConsultaVO) obj;
		if (consulta == null) {
			if (other.consulta != null) {
				return false;
			}
		} else if (!consulta.equals(other.consulta)) {
			return false;
		}
		return true;
	}

}
