	package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MbcCaractSalaEsp;
import br.gov.mec.aghu.model.MbcMvtoSalaEspEquipe;

public class MbcMvtoSalaEspEquipeDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcMvtoSalaEspEquipe> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7034795087362975201L;

	public List<MbcMvtoSalaEspEquipe> pesquisarHistoricoAlteracoesAlocacaoSalas(
			MbcCaractSalaEsp caractSalaEspSelecionada) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcMvtoSalaEspEquipe.class,"mse");
//		criteria.createAlias("mse."+MbcMvtoSalaEspEquipe.Fields.MBC_CARACT_SALA_ESPS.toString(), "cse");
//		criteria.createAlias("cse."+MbcCaractSalaEsp.Fields.AGH_ESPECIALIDADES.toString(), "cse");
		criteria.add(Restrictions.eq(MbcMvtoSalaEspEquipe.Fields.MBC_CARACT_SALA_ESPS.toString(), caractSalaEspSelecionada));

		criteria.addOrder(Order.desc(MbcMvtoSalaEspEquipe.Fields.DT_INICIO_MVTO.toString()));
		criteria.addOrder(Order.desc(MbcMvtoSalaEspEquipe.Fields.CRIADO_EM.toString()));
		return executeCriteria(criteria);
	}

	public MbcMvtoSalaEspEquipe obterUltimoMovimentoPorCaractSalaEsp(MbcCaractSalaEsp mbcCaractSalaEsp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcMvtoSalaEspEquipe.class);
		criteria.add(Restrictions.eq(MbcMvtoSalaEspEquipe.Fields.MBC_CARACT_SALA_ESPS.toString(), mbcCaractSalaEsp));
		criteria.add(Restrictions.isNull(MbcMvtoSalaEspEquipe.Fields.DT_FIM_MVTO.toString()));
		
		return (MbcMvtoSalaEspEquipe) executeCriteriaUniqueResult(criteria);
	}
}
