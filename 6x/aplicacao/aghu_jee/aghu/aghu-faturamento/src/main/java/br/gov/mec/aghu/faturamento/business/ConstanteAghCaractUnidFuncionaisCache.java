package br.gov.mec.aghu.faturamento.business;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.utils.CacheMap;



@Stateless
public class ConstanteAghCaractUnidFuncionaisCache implements Serializable {
	
    @Inject
	private IAghuFacade aghuFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7971946557242842327L;
	private Map<ConstanteAghCaractUnidFuncionaisCacheInner, ConstanteAghCaractUnidFuncionais> vos = new CacheMap<ConstanteAghCaractUnidFuncionaisCacheInner, ConstanteAghCaractUnidFuncionais>(
			5000);

	public ConstanteAghCaractUnidFuncionais buscaPrimeiraCaractUnidFuncionais(Short unfSeq,
			ConstanteAghCaractUnidFuncionais[] caracteristicas) {
		ConstanteAghCaractUnidFuncionaisCacheInner innerObject = new ConstanteAghCaractUnidFuncionaisCacheInner(unfSeq,
				caracteristicas);

		ConstanteAghCaractUnidFuncionais result = this.vos.get(innerObject);

		if (result == null) {
			List<ConstanteAghCaractUnidFuncionais> lista = aghuFacade.listarCaractUnidFuncionais(unfSeq, caracteristicas, 0,
					1);

			result = lista != null && !lista.isEmpty() ? lista.get(0) : null;

			this.vos.put(innerObject, result);
		}

		return result;
	}

	private class ConstanteAghCaractUnidFuncionaisCacheInner {

		private Short unfSeq;

		private ConstanteAghCaractUnidFuncionais[] caracteristicas;

		public ConstanteAghCaractUnidFuncionaisCacheInner(Short unfSeq, ConstanteAghCaractUnidFuncionais[] caracteristicas) {
			this.unfSeq = unfSeq;
			this.caracteristicas = caracteristicas;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + Arrays.hashCode(caracteristicas);
			result = prime * result + ((unfSeq == null) ? 0 : unfSeq.hashCode());
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
			ConstanteAghCaractUnidFuncionaisCacheInner other = (ConstanteAghCaractUnidFuncionaisCacheInner) obj;
			if (!getOuterType().equals(other.getOuterType())) {
				return false;
			}
			if (!Arrays.equals(caracteristicas, other.caracteristicas)) {
				return false;
			}
			if (unfSeq == null) {
				if (other.unfSeq != null) {
					return false;
				}
			} else if (!unfSeq.equals(other.unfSeq)) {
				return false;
			}
			return true;
		}

		private ConstanteAghCaractUnidFuncionaisCache getOuterType() {
			return ConstanteAghCaractUnidFuncionaisCache.this;
		}

	}

}
