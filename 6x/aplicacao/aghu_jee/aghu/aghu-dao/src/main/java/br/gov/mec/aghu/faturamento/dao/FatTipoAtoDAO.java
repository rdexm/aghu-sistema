package br.gov.mec.aghu.faturamento.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FatTipoAto;
import br.gov.mec.aghu.core.commons.CoreUtil;


public class FatTipoAtoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatTipoAto>{

	private static final long serialVersionUID = 5871249570814174570L;

	public List<FatTipoAto> pesquisarTipoAto(Object objPesq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(FatTipoAto.class);
		
		if(CoreUtil.isNumeroByte(objPesq)) {
			criteria.add(Restrictions.eq(FatTipoAto.Fields.SEQ.toString(), Byte.valueOf((String)objPesq)));
		}
		else if(StringUtils.isNotEmpty((String)objPesq)){
			criteria.add(Restrictions.ilike(FatTipoAto.Fields.DESCCRICAO.toString(), (String)objPesq, MatchMode.ANYWHERE));
		}
		
		criteria.add(Restrictions.eq(FatTipoAto.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		List<FatTipoAto> lista = executeCriteria(criteria);
		if(lista == null || lista.isEmpty()) {
			lista = new  ArrayList<FatTipoAto>();
		}
		 
		return lista;
	}
	
	public Long pesquisarTipoAtoCount(Object objPesq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(FatTipoAto.class);
		
		if(CoreUtil.isNumeroByte(objPesq)) {
			criteria.add(Restrictions.eq(FatTipoAto.Fields.SEQ.toString(), Byte.valueOf((String)objPesq)));
		}
		else if(StringUtils.isNotEmpty((String)objPesq)){
			criteria.add(Restrictions.ilike(FatTipoAto.Fields.DESCCRICAO.toString(), (String)objPesq, MatchMode.ANYWHERE));
		}
		
		criteria.add(Restrictions.eq(FatTipoAto.Fields.SITUACAO.toString(), DominioSituacao.A));

		return executeCriteriaCount(criteria);
	}
	
	public FatTipoAto getTipoAtoPeloCodigoSus(Byte codSus) {		
		DetachedCriteria criteria = DetachedCriteria.forClass(FatTipoAto.class);
		criteria.add(Restrictions.eq(FatTipoAto.Fields.CODIGO.toString(), codSus));
		return (FatTipoAto)executeCriteriaUniqueResult(criteria);
	}
	
	public Byte obterCodigoSusPorFatTipoAtoESituacao(final Byte seq, final DominioSituacao situacao){
		final DetachedCriteria criteria = DetachedCriteria.forClass(FatTipoAto.class);
		criteria.add(Restrictions.eq(FatTipoAto.Fields.SEQ.toString(), seq));
		criteria.add(Restrictions.eq(FatTipoAto.Fields.SITUACAO.toString(), situacao));
		
		criteria.setProjection(Projections.property(FatTipoAto.Fields.CODIGO.toString()));
		
		return (Byte) executeCriteriaUniqueResult(criteria);
	}
}

