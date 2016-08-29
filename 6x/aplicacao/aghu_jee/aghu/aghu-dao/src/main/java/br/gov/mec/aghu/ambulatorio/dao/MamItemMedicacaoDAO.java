package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MamItemMedicacao;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;


public class MamItemMedicacaoDAO extends BaseDao<MamItemMedicacao> {

	private static final long serialVersionUID = 28820242675314761L;

	public List<MamItemMedicacao> listarTodosItensMedicacao(String ordem) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamItemMedicacao.class);
		
		if (!StringUtils.isEmpty(ordem)){
			criteria.addOrder(Order.asc(ordem));
		}
		return executeCriteria(criteria);
	}
	
	/**
	 * Monta criteria para consulta de ativos por seq ou descrição
	 * 
	 * C5 de #32658
	 * 
	 * @param param
	 * @return
	 */
	private DetachedCriteria montarCriteriaAtivosPorSeqOuDescricao(String param) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamItemMedicacao.class);
		if (StringUtils.isNotBlank(param)) {
			if (CoreUtil.isNumeroInteger(param)) {
				criteria.add(Restrictions.eq(MamItemMedicacao.Fields.SEQ.toString(), Integer.valueOf(param)));
			} else {
				criteria.add(Restrictions.ilike(MamItemMedicacao.Fields.DESCRICAO.toString(), param, MatchMode.ANYWHERE));
			}
		}
		criteria.add(Restrictions.eq(MamItemMedicacao.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		return criteria;
	}

	/**
	 * Lista ativos por seq ou descrição
	 * 
	 * C5 de #32658
	 * 
	 * @param param
	 * @param maxResults
	 * @return
	 */
	public List<MamItemMedicacao> pesquisarAtivosPorSeqOuDescricao(String param, Integer maxResults) {
		DetachedCriteria criteria = this.montarCriteriaAtivosPorSeqOuDescricao(param);
		criteria.addOrder(Order.asc(MamItemMedicacao.Fields.DESCRICAO.toString()));
		if (maxResults != null) {
			return super.executeCriteria(criteria, 0, maxResults, null, true);
		}
		return super.executeCriteria(criteria);
	}

	/**
	 * Lista ativos por seq ou descrição
	 * 
	 * C5 de #32658
	 * 
	 * @param param
	 * @return
	 */
	public List<MamItemMedicacao> pesquisarAtivosPorSeqOuDescricao(String param) {
		return this.pesquisarAtivosPorSeqOuDescricao(param, null);
	}

	/**
	 * Conta ativos por seq ou descrição
	 * 
	 * C5 de #32658
	 * 
	 * @param param
	 * @return
	 */
	public Long pesquisarAtivosPorSeqOuDescricaoCount(String param) {
		DetachedCriteria criteria = this.montarCriteriaAtivosPorSeqOuDescricao(param);
		return super.executeCriteriaCount(criteria);
	}
}