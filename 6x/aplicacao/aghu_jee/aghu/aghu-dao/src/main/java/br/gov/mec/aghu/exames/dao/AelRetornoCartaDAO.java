package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AbsMotivoRetornoCartas;
import br.gov.mec.aghu.model.AelRetornoCarta;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class AelRetornoCartaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelRetornoCarta> {

	private static final long serialVersionUID = -8535753758188801081L;

	public List<AbsMotivoRetornoCartas> listarAelRetornoCartaAtivas(Object parametro) {
	    final String srtPesquisa = (String) parametro;
	    DetachedCriteria criteria = DetachedCriteria.forClass(AelRetornoCarta.class);
	    criteria.add(Restrictions.eq(AelRetornoCarta.Fields.IND_SITUACAO.toString(), DominioSituacao.A));

	    if (CoreUtil.isNumeroInteger(srtPesquisa)) {
			criteria.add(Restrictions.eq(AelRetornoCarta.Fields.SEQ.toString(), Integer.valueOf(srtPesquisa)));
		} else if (StringUtils.isNotBlank(srtPesquisa)) {
			criteria.add(Restrictions.ilike(AelRetornoCarta.Fields.DESCRICAO.toString(), srtPesquisa , MatchMode.ANYWHERE ));
		}
	    criteria.addOrder(Order.asc(AelRetornoCarta.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}
	
	public List<AelRetornoCarta> pesquisarRetornoCarta(Integer filtroSeq, String filtroDescricaoRetorno, DominioSituacao filtroIndSituacao) {
	    DetachedCriteria criteria = DetachedCriteria.forClass(AelRetornoCarta.class);
	    
	    if (filtroSeq != null) {
	    	criteria.add(Restrictions.eq(AelRetornoCarta.Fields.SEQ.toString(), filtroSeq));
	    }
	    
	    if (StringUtils.isNotBlank(filtroDescricaoRetorno)) {
	    	criteria.add(Restrictions.ilike(AelRetornoCarta.Fields.DESCRICAO.toString(), filtroDescricaoRetorno, MatchMode.ANYWHERE));
	    }
	    
	    if (filtroIndSituacao != null) {
	    	criteria.add(Restrictions.eq(AelRetornoCarta.Fields.IND_SITUACAO.toString(), filtroIndSituacao));	
	    }

	    criteria.addOrder(Order.asc(AelRetornoCarta.Fields.DESCRICAO.toString()));
	    
		return executeCriteria(criteria);
	}

}
