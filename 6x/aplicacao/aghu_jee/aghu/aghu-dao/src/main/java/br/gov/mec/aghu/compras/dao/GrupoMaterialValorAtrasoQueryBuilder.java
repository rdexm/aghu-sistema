package br.gov.mec.aghu.compras.dao;

import java.util.Date;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.core.utils.DateUtil;
 

public class GrupoMaterialValorAtrasoQueryBuilder extends GrupoMaterialValorLiberadoQueryBuilder {


	@Override
	protected void doBuild(DetachedCriteria criteria) {
		super.doBuild(criteria);

		Date dtFim = DateUtil.obterDataComHoraFinal(new Date());
		criteria.add(Restrictions.le("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString(), dtFim));
	}

}
