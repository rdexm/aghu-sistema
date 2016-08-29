package br.gov.mec.aghu.compras.dao;

import java.util.Date;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;

public class GrupoMaterialValorLiberarQueryBuilder extends GrupoMaterialSaldoProgramadoQueryBuilder {

	@Override
	protected void doBuild(DetachedCriteria criteria) {
		super.doBuild(criteria);
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ASSINATURA.toString(), false));
	}
	
	@Override
	protected Date getDataLiberaca(Date dataEntregaFinal,
			Date dataEntregaFinalParametro) {
		return super.getDataLiberaca(dataEntregaFinal, dataEntregaFinalParametro);
	}

}
