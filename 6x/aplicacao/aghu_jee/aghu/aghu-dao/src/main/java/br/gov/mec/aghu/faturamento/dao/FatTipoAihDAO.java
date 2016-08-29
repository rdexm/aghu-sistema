/**
 * 
 */
package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FatTipoAih;
import br.gov.mec.aghu.core.commons.CoreUtil;

/**
 * @author rafael
 * 
 */
public class FatTipoAihDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatTipoAih> {
	
	private static final long serialVersionUID = -8539667451277970260L;

	public FatTipoAih obterItemAihPorCodigoSus(Short codigoSus) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatTipoAih.class);

		criteria.add(Restrictions.eq(FatTipoAih.Fields.CODIGO_SUS.toString(),
				codigoSus));
		criteria.add(Restrictions.eq(
				FatTipoAih.Fields.SITUACAO_REGISTRO.toString(),
				DominioSituacao.A));

		return (FatTipoAih) executeCriteriaUniqueResult(criteria);
	}
	
	public List<FatTipoAih> pesquisarTipoAihPorSituacaoCodSus(DominioSituacao situacao, Short codSus) {
		DetachedCriteria cri = DetachedCriteria.forClass(FatTipoAih.class);
		cri.add(Restrictions.eq(FatTipoAih.Fields.CODIGO_SUS.toString(), codSus));
		cri.add(Restrictions.eq(FatTipoAih.Fields.SITUACAO_REGISTRO.toString(), situacao));
		return this.executeCriteria(cri);
	}
	
	
	public List<FatTipoAih> pesquisarTipoAih(Object parametro) {
		return this.executeCriteria(obterCriteriaPorTipoAih(parametro, true), 0, 100, null);
	}

	public Long pesquisarTipoAihCount(Object parametro) {
		return this.executeCriteriaCount(obterCriteriaPorTipoAih(parametro, false));
	}
	
	
	private DetachedCriteria obterCriteriaPorTipoAih(Object parametro, boolean ordenar) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatTipoAih.class);
		
		String strParametro = (String) parametro;
		if (StringUtils.isNotBlank(strParametro) && StringUtils.isNumeric(strParametro) && CoreUtil.isNumeroByte(strParametro)) {
			criteria.add(Restrictions.or(Restrictions.or(Restrictions.eq(
					FatTipoAih.Fields.SEQ.toString(),
					Byte.valueOf(strParametro)), Restrictions.eq(
					FatTipoAih.Fields.CODIGO_SUS.toString(),
					Short.valueOf(strParametro))), Restrictions.ilike(
					FatTipoAih.Fields.DESCRICAO.toString(),
					parametro.toString(), MatchMode.ANYWHERE)));
			
		} else if (StringUtils.isNotBlank(strParametro)) {
			criteria.add(Restrictions.ilike(FatTipoAih.Fields.DESCRICAO.toString(), parametro.toString(), MatchMode.ANYWHERE));
		}
		
		if (ordenar){
			criteria.addOrder(Order.asc(FatTipoAih.Fields.SEQ.toString()));			
		}

		return criteria;
	}
	
}
