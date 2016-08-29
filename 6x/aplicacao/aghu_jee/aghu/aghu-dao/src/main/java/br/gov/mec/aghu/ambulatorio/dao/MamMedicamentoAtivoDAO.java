package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MamMedicamentoAtivo;

/**
 * #42360
 * Classe de acesso a base de dados responsáveis pelas operações relativas a
 * tabela MAM_MEDICAMENTO_ATIVO.
 * 
 */
public class MamMedicamentoAtivoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamMedicamentoAtivo> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6400956764734270678L;

	/**
	 * #42360
	 * @param numeroConsulta
	 * @return
	 */
	
	public MamMedicamentoAtivo buscarMedicamentoAtivoPorNumeroConsulta(Integer numeroConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamMedicamentoAtivo.class);

		criteria.add(Restrictions.eq(MamMedicamentoAtivo.Fields.CON_NUMERO.toString(), numeroConsulta));
		List<MamMedicamentoAtivo> list = executeCriteria(criteria);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
	
}
