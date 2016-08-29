package br.gov.mec.aghu.exames.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelProjetoProcedimento;

public class AelProjetoProcedimentoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelProjetoProcedimento> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3394848483377070204L;
	
	/**
	 * Obtem projeto procedimento ativo por id
	 * @param pjqSeq
	 * @param pciSeq
	 * @return
	 */
	public AelProjetoProcedimento obterProjetoProcedimentoAtivoPorId(Integer pjqSeq, Integer pciSeq) { 
		
		if (pjqSeq == null || pciSeq == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório não informado");
		}
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelProjetoProcedimento.class, "ppr");
		
		criteria.createAlias("ppr.".concat(AelProjetoProcedimento.Fields.AEL_PROJETO_PESQUISAS.toString()), "pjq", criteria.INNER_JOIN);
		
		criteria.add(Restrictions.eq("ppr.".concat(AelProjetoProcedimento.Fields.PJQ_SEQ.toString()), pjqSeq));
		criteria.add(Restrictions.eq("ppr.".concat(AelProjetoProcedimento.Fields.PCI_SEQ.toString()), pciSeq));
		
		criteria.add(Restrictions.eq("ppr.".concat(AelProjetoProcedimento.Fields.SITUACAO.toString()), DominioSituacao.A));
		
		return (AelProjetoProcedimento) executeCriteriaUniqueResult(criteria);
		
	}

}
