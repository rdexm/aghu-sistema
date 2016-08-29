package br.gov.mec.aghu.compras.dao;


import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;

public class GrupoMaterialValorLiberadoQueryBuilder extends GrupoMaterialValoresProgramadosQueryBuilder {


	@Override
	protected void doBuild(DetachedCriteria criteria) {
		super.doBuild(criteria);
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ASSINATURA.toString(), true));
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_PLANEJAMENTO.toString(), true));
		
	}

}
