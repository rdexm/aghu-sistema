package br.gov.mec.aghu.faturamento.vo;

import br.gov.mec.aghu.model.VFatAssociacaoProcedimento;
import br.gov.mec.aghu.model.VFatContasHospPacientes;
import br.gov.mec.aghu.core.commons.BaseBean;


public class ListarContasHospPacientesPorPacCodigoVO implements BaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6003509460399635263L;

	private VFatContasHospPacientes viewContaHospitalar;

	private VFatAssociacaoProcedimento procedimentoSolicitado;

	private VFatAssociacaoProcedimento procedimentoRealizado;

	public ListarContasHospPacientesPorPacCodigoVO() {
	}

	public ListarContasHospPacientesPorPacCodigoVO(VFatContasHospPacientes viewContaHospitalar,
			VFatAssociacaoProcedimento procedimentoSolicitado, VFatAssociacaoProcedimento procedimentoRealizado) {
		this.viewContaHospitalar = viewContaHospitalar;
		this.procedimentoSolicitado = procedimentoSolicitado;
		this.procedimentoRealizado = procedimentoRealizado;
	}

	public VFatContasHospPacientes getViewContaHospitalar() {
		return viewContaHospitalar;
	}

	public void setViewContaHospitalar(VFatContasHospPacientes viewContaHospitalar) {
		this.viewContaHospitalar = viewContaHospitalar;
	}

	public VFatAssociacaoProcedimento getProcedimentoSolicitado() {
		return procedimentoSolicitado;
	}

	public void setProcedimentoSolicitado(VFatAssociacaoProcedimento procedimentoSolicitado) {
		this.procedimentoSolicitado = procedimentoSolicitado;
	}

	public VFatAssociacaoProcedimento getProcedimentoRealizado() {
		return procedimentoRealizado;
	}

	public void setProcedimentoRealizado(VFatAssociacaoProcedimento procedimentoRealizado) {
		this.procedimentoRealizado = procedimentoRealizado;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((procedimentoRealizado == null) ? 0 : procedimentoRealizado.hashCode());
		result = prime * result + ((procedimentoSolicitado == null) ? 0 : procedimentoSolicitado.hashCode());
		result = prime * result + ((viewContaHospitalar == null) ? 0 : viewContaHospitalar.hashCode());
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
		ListarContasHospPacientesPorPacCodigoVO other = (ListarContasHospPacientesPorPacCodigoVO) obj;
		if (procedimentoRealizado == null) {
			if (other.procedimentoRealizado != null) {
				return false;
			}
		} else if (!procedimentoRealizado.equals(other.procedimentoRealizado)) {
			return false;
		}
		if (procedimentoSolicitado == null) {
			if (other.procedimentoSolicitado != null) {
				return false;
			}
		} else if (!procedimentoSolicitado.equals(other.procedimentoSolicitado)) {
			return false;
		}
		if (viewContaHospitalar == null) {
			if (other.viewContaHospitalar != null) {
				return false;
			}
		} else if (!viewContaHospitalar.equals(other.viewContaHospitalar)) {
			return false;
		}
		return true;
	}

}
