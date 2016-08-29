package br.gov.mec.aghu.faturamento.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FatMotivoCobrancaApac;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class FatMotivoCobrancaApacDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatMotivoCobrancaApac> {

	private static final long serialVersionUID = 8537185967403189424L;

	public List<FatMotivoCobrancaApac> pesquisarMotivoCobrancaApac(Object objPesq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatMotivoCobrancaApac.class);
		
		if(CoreUtil.isNumeroByte(objPesq)) {
			criteria.add(Restrictions.eq(FatMotivoCobrancaApac.Fields.SEQ.toString(), Byte.valueOf((String)objPesq)));
		}
		else if(StringUtils.isNotEmpty((String)objPesq)){
			criteria.add(Restrictions.ilike(FatMotivoCobrancaApac.Fields.DESCRICAO.toString(), (String)objPesq, MatchMode.ANYWHERE));
		}
		
		criteria.add(Restrictions.eq(FatMotivoCobrancaApac.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		List<FatMotivoCobrancaApac> lista = executeCriteria(criteria);
		if(lista == null || lista.isEmpty()) {
			lista = new ArrayList<FatMotivoCobrancaApac>();
		}
		
		return lista;
	}
	
	public Long pesquisarMotivoCobrancaApacCount(Object objPesq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatMotivoCobrancaApac.class);
		
		if(CoreUtil.isNumeroByte(objPesq)) {
			criteria.add(Restrictions.eq(FatMotivoCobrancaApac.Fields.SEQ.toString(), Byte.valueOf((String)objPesq)));
		}
		else if(StringUtils.isNotEmpty((String)objPesq)){
			criteria.add(Restrictions.ilike(FatMotivoCobrancaApac.Fields.DESCRICAO.toString(), (String)objPesq, MatchMode.ANYWHERE));
		}
		
		return executeCriteriaCount(criteria);
	}
	
	public FatMotivoCobrancaApac obterMotivoCobrancaPorCodSus(Byte codigoSus){
		FatMotivoCobrancaApacsPorCodSusQueryBuilder builder = new FatMotivoCobrancaApacsPorCodSusQueryBuilder();
		return (FatMotivoCobrancaApac) executeCriteriaUniqueResult(builder.build(codigoSus));
	}
	
}
