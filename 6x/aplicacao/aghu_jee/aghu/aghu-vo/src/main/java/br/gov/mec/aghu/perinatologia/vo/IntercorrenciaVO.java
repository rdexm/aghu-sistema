package br.gov.mec.aghu.perinatologia.vo;

import br.gov.mec.aghu.model.McoIntercorrencia;
import br.gov.mec.aghu.core.commons.BaseBean;

/**
 * VO que representa dados de {@link McoIntercorrencia}
 * 
 * @author luismoura
 * 
 */
public class IntercorrenciaVO implements BaseBean {
	private static final long serialVersionUID = 8408755846778519304L;

	private McoIntercorrencia mcoIntercorrencia;
	private String codigoCid;

	public IntercorrenciaVO() {

	}

	public IntercorrenciaVO(String codigoCid, McoIntercorrencia mcoIntercorrencia) {
		this.codigoCid = codigoCid;
		this.mcoIntercorrencia = mcoIntercorrencia;
	}

	public String getCodigoCid() {
		return codigoCid;
	}

	public void setCodigoCid(String codigoCid) {
		this.codigoCid = codigoCid;
	}

	public McoIntercorrencia getMcoIntercorrencia() {
		return mcoIntercorrencia;
	}

	public void setMcoIntercorrencia(McoIntercorrencia mcoIntercorrencia) {
		this.mcoIntercorrencia = mcoIntercorrencia;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mcoIntercorrencia == null) ? 0 : mcoIntercorrencia.hashCode());
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
		IntercorrenciaVO other = (IntercorrenciaVO) obj;
		if (mcoIntercorrencia == null) {
			if (other.mcoIntercorrencia != null) {
				return false;
			}
		} else if (!mcoIntercorrencia.equals(other.mcoIntercorrencia)) {
			return false;
		}
		return true;
	}
}
