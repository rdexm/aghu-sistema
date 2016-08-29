package br.gov.mec.aghu.ambulatorio.dao;

import java.util.ArrayList;
import java.util.List;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import br.gov.mec.aghu.model.MamFiguraEvolucao;

/**
 * #42360
 * Classe de acesso a base de dados responsáveis pelas operações relativas a
 * tabela MAM_FIGURA_EVOLUCAO.
 * 
 */
public class MamFiguraEvolucaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamFiguraEvolucao>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 6125040514082538200L;


	public Long obterQuestaoPorEvolucaoCount(Integer numero){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamFiguraEvolucao.class);
		criteria.add(Restrictions.eq(MamFiguraEvolucao.Fields.CON_NUMERO.toString(), numero));
		return this.executeCriteriaCount(criteria);
	}
	
	public List<MamFiguraEvolucao> obterFiguraEvolucaoPorConNumero(Integer numero){
		List<String> pend = new ArrayList<String>();
		pend.add("R");
		pend.add("P");
		DetachedCriteria criteria = DetachedCriteria.forClass(MamFiguraEvolucao.class);
		criteria.add(Restrictions.eq(MamFiguraEvolucao.Fields.CON_NUMERO.toString(), numero));
		criteria.add(Restrictions.in(MamFiguraEvolucao.Fields.IND_PENDENTE.toString(), pend));
		return  this.executeCriteria(criteria);
	}

	/**
	 * #42360
	 * @param numeroConsulta
	 * @return
	 */
	public MamFiguraEvolucao buscarFiguraEvolucaoPorNumeroConsulta(Integer numeroConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamFiguraEvolucao.class);

		criteria.add(Restrictions.eq(MamFiguraEvolucao.Fields.CON_NUMERO.toString(), numeroConsulta));
		List<MamFiguraEvolucao> list = executeCriteria(criteria);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
}
