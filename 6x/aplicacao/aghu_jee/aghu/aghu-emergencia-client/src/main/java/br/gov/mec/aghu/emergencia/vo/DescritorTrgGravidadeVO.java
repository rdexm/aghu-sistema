package br.gov.mec.aghu.emergencia.vo;

import java.io.Serializable;

import br.gov.mec.aghu.model.MamDescritor;

public class DescritorTrgGravidadeVO implements Serializable {
	private static final long serialVersionUID = 2238819937286756049L;

	private MamDescritor descritor;
	private Boolean trgGravidade;
	private boolean habilitado;

	public DescritorTrgGravidadeVO() {
	}

	public DescritorTrgGravidadeVO(MamDescritor descritor, Boolean trgGravidade, boolean habilitado) {
		this.descritor = descritor;
		this.trgGravidade = trgGravidade;
		this.habilitado = habilitado;
	}

	public MamDescritor getDescritor() {
		return descritor;
	}

	public void setDescritor(MamDescritor descritor) {
		this.descritor = descritor;
	}

	public Boolean getTrgGravidade() {
		return trgGravidade;
	}

	public void setTrgGravidade(Boolean trgGravidade) {
		this.trgGravidade = trgGravidade;
	}

	public boolean isHabilitado() {
		return habilitado;
	}

	public void setHabilitado(boolean habilitado) {
		this.habilitado = habilitado;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((descritor == null) ? 0 : descritor.hashCode());
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
		DescritorTrgGravidadeVO other = (DescritorTrgGravidadeVO) obj;
		if (descritor == null) {
			if (other.descritor != null) {
				return false;
			}
		} else if (!descritor.equals(other.descritor)) {
			return false;
		}
		return true;
	}

}
