package br.gov.mec.aghu.emergencia.vo;

import br.gov.mec.aghu.model.MamEmgServidor;
import br.gov.mec.aghu.registrocolaborador.vo.Servidor;
import br.gov.mec.aghu.core.commons.BaseBean;

public class ServidorEmergenciaVO implements BaseBean {
	private static final long serialVersionUID = 4542843086033323919L;

	private MamEmgServidor servidorEmergencia;
	private Servidor servidor;

	public MamEmgServidor getServidorEmergencia() {
		return servidorEmergencia;
	}

	public void setServidorEmergencia(MamEmgServidor servidorEmergencia) {
		this.servidorEmergencia = servidorEmergencia;
	}

	public Servidor getServidor() {
		return servidor;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((servidor == null) ? 0 : servidor.hashCode());
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
		ServidorEmergenciaVO other = (ServidorEmergenciaVO) obj;
		if (servidor == null) {
			if (other.servidor != null) {
				return false;
			}
		} else if (!servidor.equals(other.servidor)) {
			return false;
		}
		return true;
	}
}
