package br.gov.mec.aghu.compras.dao;

import java.util.Date;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.core.utils.DateUtil;


public class GrupoMaterialSaldoProgramadoQueryBuilder extends GrupoMaterialValoresProgramadosQueryBuilder {


	@Override
	protected void doBuild(DetachedCriteria criteria) {
		super.doBuild(criteria);
		Date dtFim = DateUtil.obterDataComHoraFinal(new Date());
		criteria.add(Restrictions.gt("AF." + ScoAutorizacaoForn.Fields.DT_VENCTO_CONTRATO.toString(), dtFim));
		
	}

}
