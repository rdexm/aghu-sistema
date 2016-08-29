package br.gov.mec.aghu.exames.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelUnidMedValorNormal;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.commons.CoreUtil;


public class AelUnidMedValorNormalDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelUnidMedValorNormal> {

	private static final long serialVersionUID = 8420805812405907641L;

	private DetachedCriteria obterCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelUnidMedValorNormal.class);
		return criteria;
    }
	
	private DetachedCriteria criarCriteria(AelUnidMedValorNormal elemento) {
    	DetachedCriteria criteria = obterCriteria();
    	//Popula criteria com dados do elemento
    	if(elemento != null) {
    		//Código
    		if(elemento.getSeq() != null) {
    			criteria.add(Restrictions.eq(AelUnidMedValorNormal.Fields.SEQ.toString(), elemento.getSeq()));
    		}
    		//Descrição
			if(elemento.getDescricao() != null && !elemento.getDescricao().trim().isEmpty()) {
				criteria.add(Restrictions.ilike(AelUnidMedValorNormal.Fields.DESCRICAO.toString(), elemento.getDescricao(), MatchMode.ANYWHERE));
			}
			//Situação
			if(elemento.getIndSituacao() != null) {
				criteria.add(Restrictions.eq(AelUnidMedValorNormal.Fields.SITUACAO.toString(), elemento.getIndSituacao()));
			}
    	}
    	return criteria;
    }
	
	public AelUnidMedValorNormal obterPeloId(Integer codigo) {
		AelUnidMedValorNormal elemento = new AelUnidMedValorNormal();
		elemento.setSeq(codigo);
		DetachedCriteria criteria = criarCriteria(elemento);
		return (AelUnidMedValorNormal) executeCriteriaUniqueResult(criteria);
	}
	
	public AelUnidMedValorNormal obterAntigoPeloId(Integer entity) {
		if (entity == null) {
			throw new IllegalArgumentException("Parametro Obrigatorio nao informado.");
		}
		
		StringBuilder hql = new StringBuilder(100);
		hql.append("select a.").append(AelUnidMedValorNormal.Fields.CRIADOEM.toString());
		hql.append(", a.").append(AelUnidMedValorNormal.Fields.SEQ.toString());
		hql.append(", a.").append(AelUnidMedValorNormal.Fields.SITUACAO.toString());
		hql.append(", a.").append(AelUnidMedValorNormal.Fields.SERVIDOR.toString());
		hql.append(", a.").append(AelUnidMedValorNormal.Fields.DESCRICAO.toString());
		hql.append(" from ").append(AelUnidMedValorNormal.class.getSimpleName()).append(" a ");
		hql.append(" where a.").append(AelUnidMedValorNormal.Fields.SEQ.toString()).append(" = :entityId ");
		
		Query query = this.createQuery(hql.toString());
		query.setParameter("entityId", entity);
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = query.getResultList();
		AelUnidMedValorNormal returnValue = null; 
		
		if (lista != null && !lista.isEmpty()) {
			// Pelo criterio de Pesquisa deve ter apenas um elemento na lista.
			returnValue = new AelUnidMedValorNormal();
			for (Object[] listFileds : lista) {
				returnValue.setCriadoEm( (Date) listFileds[0]);
				returnValue.setSeq( (Integer) listFileds[1]);
				returnValue.setIndSituacao( (DominioSituacao) listFileds[2]);
				returnValue.setServidor( (RapServidores) listFileds[3]);
				returnValue.setDescricao( (String) listFileds[4]);
			}
		}		
		return returnValue;
	}
	
	public List<AelUnidMedValorNormal> pesquisar(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, AelUnidMedValorNormal elemento) {
		DetachedCriteria criteria = criarCriteria(elemento);
		criteria.createAlias(AelUnidMedValorNormal.Fields.SERVIDOR.toString(), "SERV", JoinType.INNER_JOIN);
		
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	public Long pesquisarCount(AelUnidMedValorNormal elemento) {
		DetachedCriteria criteria = criarCriteria(elemento);
		return executeCriteriaCount(criteria);
	}
	
	public List<AelUnidMedValorNormal> pesquisarSb(
			Object parametroConsulta) {

		List<AelUnidMedValorNormal> result = null;
		DetachedCriteria criteria = null;

		criteria = DetachedCriteria.forClass(AelUnidMedValorNormal.class);
		criteria.add(Restrictions.eq(AelUnidMedValorNormal.Fields.SITUACAO.toString(), DominioSituacao.A));
		
	
		if (parametroConsulta != null) {
			final String parametro = (String) parametroConsulta;

			if (CoreUtil.isNumeroInteger(parametro)) {

				Integer seq = Integer.valueOf(parametro);
				criteria.add(Restrictions.eq(AelUnidMedValorNormal.Fields.SEQ.toString(), seq));

			} else if (StringUtils.isNotEmpty(parametro)) {

				criteria.add(Restrictions.ilike(AelUnidMedValorNormal.Fields.DESCRICAO.toString(),parametro, MatchMode.ANYWHERE));
			}

		}

		criteria.addOrder(Order.asc(AelUnidMedValorNormal.Fields.DESCRICAO.toString()));
		result = this.executeCriteria(criteria, false);

		return result;
	}

	public List<AelUnidMedValorNormal> pesquisarSb() {

		List<AelUnidMedValorNormal> result = null;
		DetachedCriteria criteria = null;

		criteria = DetachedCriteria.forClass(AelUnidMedValorNormal.class);
		criteria.add(Restrictions.eq(AelUnidMedValorNormal.Fields.SITUACAO.toString(),DominioSituacao.A));
		criteria.addOrder(Order.asc(AelUnidMedValorNormal.Fields.DESCRICAO.toString()));
		result = this.executeCriteria(criteria, false);

		return result;
	}
}
