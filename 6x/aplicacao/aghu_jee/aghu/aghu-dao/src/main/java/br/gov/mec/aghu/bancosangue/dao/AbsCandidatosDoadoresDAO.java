package br.gov.mec.aghu.bancosangue.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AbsCandidatosDoadores;
import br.gov.mec.aghu.model.AipNacionalidades;
import br.gov.mec.aghu.model.AipOcupacoes;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class AbsCandidatosDoadoresDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AbsCandidatosDoadores>{
	
	private static final long serialVersionUID = -2069854272731822084L;

	/**
	 * Retorna os candidatos doadores por nacionalidade
	 * @param nacionalidade
	 * @return
	 */
	public Long pesquisarCandidatosDoadoresPorNacionalidadeCount(
			AipNacionalidades nacionalidade) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AbsCandidatosDoadores.class);
		criteria.add(Restrictions.eq(AbsCandidatosDoadores.Fields.NACIONALIDADE.toString(), nacionalidade));
		return executeCriteriaCount(criteria);
	}
	
	
	public Long pesquisarCandidatosDoadoresPorOcupacaoCount(
			AipOcupacoes ocupacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AbsCandidatosDoadores.class);
		criteria.add(Restrictions.eq(AbsCandidatosDoadores.Fields.OCUPACAO.toString(), ocupacao));
		return executeCriteriaCount(criteria);
	}
	
	public List<AbsCandidatosDoadores> obterCandidatosDoadoresList(String parametro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AbsCandidatosDoadores.class);
		
		if (StringUtils.isNotBlank(parametro)) {
			if(CoreUtil.isNumeroInteger(parametro)) {
				criteria.add(Restrictions.eq(AbsCandidatosDoadores.Fields.SEQ.toString(), Integer.valueOf(parametro)));
			} else {
				criteria.add(Restrictions.ilike(AbsCandidatosDoadores.Fields.NOME.toString(), parametro, MatchMode.ANYWHERE));
			}
		}
		criteria.addOrder(Order.asc(AbsCandidatosDoadores.Fields.NOME.toString()));
		
		return this.executeCriteria(criteria, 0, 100, null, true);
	}

}
