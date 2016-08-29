package br.gov.mec.aghu.casca.vo;

import org.apache.commons.lang3.ArrayUtils;

import br.gov.mec.aghu.casca.model.Modulo;
import br.gov.mec.aghu.core.commons.BaseBean;

public class ModuloVO implements BaseBean {
	private static final long serialVersionUID = -5353225127531705859L;
	
	private static final String[] MODULOS_READ_ONLY = {"configuracao", "centrocusto", "casca", "pacientes", "registrocolaborador"};
	
	private Modulo modulo;	
	private Boolean atualizado;
	
	public Boolean getReadOnly() {
		return (this.modulo != null && ArrayUtils.contains(MODULOS_READ_ONLY, this.modulo.getNome()));		
	}
	
	public Modulo getModulo() {
		return modulo;
	}
	public void setModulo(Modulo modulo) {
		this.modulo = modulo;
	}
	public Boolean getAtualizado() {
		return atualizado;
	}
	public void setAtualizado(Boolean atualizado) {
		this.atualizado = atualizado;
	}
	
	/**
	 * @return Apenas os módulos que sempre devem estar ligados e
	 * não podem ser alterados, por isso são somente de leitura
	 */
	public static String[] getNomeModulosReadOnly() {
		return MODULOS_READ_ONLY;
	}	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((modulo == null) ? 0 : modulo.hashCode());
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
		ModuloVO other = (ModuloVO) obj;
		if (modulo == null) {
			if (other.modulo != null) {
				return false;
			}
		} else if (!modulo.equals(other.modulo)) {
			return false;
		}
		return true;
	}
}
