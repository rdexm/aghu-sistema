package br.gov.mec.aghu.ambulatorio.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MamFiguraAnamnese;

/**
 * #42360
 * Classe de acesso a base de dados responsáveis pelas operações relativas a
 * tabela MAM_FIGURA_ANAMNESES.
 * 
 */
public class MamFiguraAnamnesesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamFiguraAnamnese> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3857798403649220154L;
	
	/**
	 * #42360
	 * @param numeroConsulta
	 * @return
	 */
	public MamFiguraAnamnese buscarFiguraAnamnesesPorNumeroConsulta(Integer numeroConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamFiguraAnamnese.class);

		criteria.add(Restrictions.eq(MamFiguraAnamnese.Fields.CON_NUMERO.toString(), numeroConsulta));
		List<MamFiguraAnamnese> list = executeCriteria(criteria);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	// #50745 - cursor das figuras da anamnese
	public Long obterCountFiguraAnamnesePorConNumero(Integer conNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamFiguraAnamnese.class);
		criteria.add(Restrictions.eq(MamFiguraAnamnese.Fields.CON_NUMERO.toString(), conNumero));
		return executeCriteriaCount(criteria);
	}

	// #50745
	public List<MamFiguraAnamnese> obterListaFiguraAnamnesePorConNumero(Integer conNumero) {
		List<String> filtroPendente = new ArrayList<String>();
		filtroPendente.add("R");
		filtroPendente.add("P");
		DetachedCriteria criteria = DetachedCriteria.forClass(MamFiguraAnamnese.class);
		criteria.add(Restrictions.eq(MamFiguraAnamnese.Fields.CON_NUMERO.toString(), conNumero));
		criteria.add(Restrictions.in(MamFiguraAnamnese.Fields.IND_PENDENTE.toString(), filtroPendente));
		return executeCriteria(criteria);
	}
}
