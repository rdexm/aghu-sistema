package br.gov.mec.aghu.internacao.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AinCaracteristicaLeito;
import br.gov.mec.aghu.model.AinLeitos;

public class AinCaracteristicaLeitoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AinCaracteristicaLeito> {

	private static final long serialVersionUID = -7228395931087845228L;

	/**
	 * ORADB AINC_RET_CARACT_LTO
	 */
	public String obterCaracteristicaLeito(String pLtoiD, String pTipo) {
		DetachedCriteria cri = DetachedCriteria.forClass(AinCaracteristicaLeito.class);
		cri.createAlias(AinCaracteristicaLeito.Fields.TIPO_CARACTERISTICA.toString(),
				AinCaracteristicaLeito.Fields.TIPO_CARACTERISTICA.toString());

		cri.add(Restrictions.eq("id.ltoLtoId", pLtoiD));
		cri.add(Restrictions.eq(AinCaracteristicaLeito.Fields.TIPO_CARACTERISTICA.toString()
				+ ".descricao", pTipo));

		List<AinLeitos> lista = executeCriteria(cri);
		String retorno = "N";
		if (!lista.isEmpty()) {
			retorno = "S";
		}
		return retorno;
	}
	
	public List<AinCaracteristicaLeito> obterCaracteristicasDoLeito(String ltoId){
		final DetachedCriteria criteria = DetachedCriteria.forClass(AinCaracteristicaLeito.class);
	
		criteria.add(Restrictions.eq(AinCaracteristicaLeito.Fields.LTO_LTO_ID.toString(), ltoId));
		
		return executeCriteria(criteria);
	}

}