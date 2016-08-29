package br.gov.mec.aghu.faturamento.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FatTiposVinculo;
import br.gov.mec.aghu.core.commons.CoreUtil;


public class FatTiposVinculoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatTiposVinculo> {
	
	private static final long serialVersionUID = -2827575748723828113L;

	public List<FatTiposVinculo> pesquisarTipoVinculo(Object objPesq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(FatTiposVinculo.class);
		
		if(CoreUtil.isNumeroInteger(objPesq)) {
			criteria.add(Restrictions.eq(FatTiposVinculo.Fields.SEQ.toString(), Integer.valueOf((String)objPesq)));
		}
		else if(StringUtils.isNotEmpty((String)objPesq)){
			criteria.add(Restrictions.ilike(FatTiposVinculo.Fields.DESCCRICAO.toString(), (String)objPesq, MatchMode.ANYWHERE));
		}
		
		criteria.add(Restrictions.eq(FatTiposVinculo.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		List<FatTiposVinculo> lista = executeCriteria(criteria);
		if(lista == null || lista.isEmpty()) {
			lista = new  ArrayList<FatTiposVinculo>();
		}
		 
		return lista;
	}
	
	public Long pesquisarTipoVinculoCount(Object objPesq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(FatTiposVinculo.class);
		
		if(CoreUtil.isNumeroInteger(objPesq)) {
			criteria.add(Restrictions.eq(FatTiposVinculo.Fields.SEQ.toString(), Integer.valueOf((String)objPesq)));
		}
		else if(StringUtils.isNotEmpty((String)objPesq)){
			criteria.add(Restrictions.ilike(FatTiposVinculo.Fields.DESCCRICAO.toString(), (String)objPesq, MatchMode.ANYWHERE));
		}
		
		criteria.add(Restrictions.eq(FatTiposVinculo.Fields.SITUACAO.toString(), DominioSituacao.A));

		return executeCriteriaCount(criteria);
	}
	
	public FatTiposVinculo getTiposVinculoPeloCodigoSus(Integer codSus) {		
		DetachedCriteria criteria = DetachedCriteria.forClass(FatTiposVinculo.class);
		criteria.add(Restrictions.eq(FatTiposVinculo.Fields.CODIGO.toString(), codSus));
		//criteria.getExecutableCriteria(getSession()).setCacheable(Boolean.TRUE);
		return (FatTiposVinculo)executeCriteriaUniqueResult(criteria, true);
	}

	public Integer obterCodigoSusPorFatTiposVinculoESituacao(final Integer seq, final DominioSituacao situacao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatTiposVinculo.class);
		criteria.add(Restrictions.eq(FatTiposVinculo.Fields.SEQ.toString(), seq));
		criteria.add(Restrictions.eq(FatTiposVinculo.Fields.SITUACAO.toString(), situacao));
		
		criteria.setProjection(Projections.property(FatTiposVinculo.Fields.CODIGO.toString()));
		
		return (Integer) executeCriteriaUniqueResult(criteria);
	}
}
