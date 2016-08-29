package br.gov.mec.aghu.exames.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioTipoTransporte;

public class RelatorioEstatisticaTipoTransporteExtratoVO {

	private Date dataHoraEvento;
	private DominioTipoTransporte tipoTransporte;

	public Date getDataHoraEvento() {
		return dataHoraEvento;
	}

	public void setDataHoraEvento(Date dataHoraEvento) {
		this.dataHoraEvento = dataHoraEvento;
	}

	public DominioTipoTransporte getTipoTransporte() {
		return tipoTransporte;
	}

	public void setTipoTransporte(DominioTipoTransporte tipoTransporte) {
		this.tipoTransporte = tipoTransporte;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dataHoraEvento == null) ? 0 : dataHoraEvento.hashCode());
		result = prime * result + ((tipoTransporte == null) ? 0 : tipoTransporte.hashCode());
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
		RelatorioEstatisticaTipoTransporteExtratoVO other = (RelatorioEstatisticaTipoTransporteExtratoVO) obj;
		if (dataHoraEvento == null) {
			if (other.dataHoraEvento != null) {
				return false;
			}
		} else if (!dataHoraEvento.equals(other.dataHoraEvento)) {
			return false;
		}
		if (tipoTransporte != other.tipoTransporte) {
			return false;
		}
		return true;
	}

}