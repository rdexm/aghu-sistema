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
import br.gov.mec.aghu.model.AelAnticoagulante;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.commons.CoreUtil;


public class AelAnticoagulanteDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelAnticoagulante> {

	private static final long serialVersionUID = 6593280527983280412L;

	private DetachedCriteria obterCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelAnticoagulante.class);
		return criteria;
    }
	
	private DetachedCriteria criarCriteria(AelAnticoagulante elemento) {
    	DetachedCriteria criteria = obterCriteria();
    	//Popula criteria com dados do elemento
    	if(elemento != null) {
    		//Código
    		if(elemento.getSeq() != null) {
    			criteria.add(Restrictions.eq(AelMateriaisAnalises.Fields.SEQ.toString(), elemento.getSeq()));
    		}
    		//Descrição
			if(elemento.getDescricao() != null && !elemento.getDescricao().trim().isEmpty()) {
				criteria.add(Restrictions.ilike(AelAnticoagulante.Fields.DESCRICAO.toString(), elemento.getDescricao(), MatchMode.ANYWHERE));
			}
			//Situação
			if(elemento.getIndSituacao() != null) {
				criteria.add(Restrictions.eq(AelAnticoagulante.Fields.SITUACAO.toString(), elemento.getIndSituacao()));
			}
    	}
    	return criteria;
    }
	
	public AelAnticoagulante obterPeloId(Integer codigo) {
		AelAnticoagulante elemento = new AelAnticoagulante();
		elemento.setSeq(codigo);
		DetachedCriteria criteria = criarCriteria(elemento);
		
		return (AelAnticoagulante) executeCriteriaUniqueResult(criteria);
	}
	
	public AelAnticoagulante obterAntigoPeloId(Integer entity) {
		if (entity == null) {
			throw new IllegalArgumentException("Parametro Obrigatorio nao informado.");
		}
		
		StringBuilder hql = new StringBuilder(100);
		hql.append("select a.").append(AelAnticoagulante.Fields.CRIADOEM.toString());
		hql.append(", a.").append(AelAnticoagulante.Fields.SEQ.toString());
		hql.append(", a.").append(AelAnticoagulante.Fields.SITUACAO.toString());
		hql.append(", a.").append(AelAnticoagulante.Fields.SERVIDOR.toString());
		hql.append(", a.").append(AelAnticoagulante.Fields.DESCRICAO.toString());
		hql.append(" from ").append(AelAnticoagulante.class.getSimpleName()).append(" a ");
		hql.append(" where a.").append(AelAnticoagulante.Fields.SEQ.toString()).append(" = :entityId ");
		
		Query query = this.createQuery(hql.toString());
		query.setParameter("entityId", entity);
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = query.getResultList();
		AelAnticoagulante returnValue = null; 
		
		if (lista != null && !lista.isEmpty()) {
			// Pelo criterio de Pesquisa deve ter apenas um elemento na lista.
			returnValue = new AelAnticoagulante();
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
	
	public List<AelAnticoagulante> pesquisar(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, AelAnticoagulante elemento) {
		DetachedCriteria criteria = criarCriteria(elemento);
		criteria.createAlias(AelAnticoagulante.Fields.SERVIDOR.toString(), "SERV", JoinType.INNER_JOIN);
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	public List<AelAnticoagulante> pesquisarDescricao(AelAnticoagulante elemento) {
		DetachedCriteria criteria = obterCriteria();
    	//Popula criteria com dados do elemento
    	if(elemento != null) {
    		//Descrição
			if(elemento.getDescricao() != null && !elemento.getDescricao().trim().isEmpty()) {
				criteria.add(Restrictions.eq(AelAnticoagulante.Fields.DESCRICAO.toString(), elemento.getDescricao()));
			}
    	}
		return executeCriteria(criteria);
	}

	public Long pesquisarCount(AelAnticoagulante elemento) {
		DetachedCriteria criteria = criarCriteria(elemento);
		return executeCriteriaCount(criteria);
	}
	
	/**
	 * Método de listagem utilizado pelo componente mec:suggestionBox
	 * @param parametro
	 * @return
	 */
	public List<AelAnticoagulante> listarAelAnticoagulanteAtivo(Object parametro) {
	    final String srtPesquisa = (String) parametro;
	    DetachedCriteria criteria = DetachedCriteria.forClass(AelAnticoagulante.class);
	    if (CoreUtil.isNumeroInteger(srtPesquisa)) {
			criteria.add(Restrictions.eq(AelAnticoagulante.Fields.SEQ.toString(), Integer.valueOf(srtPesquisa)));
		} else if (StringUtils.isNotBlank(StringUtils.trim(srtPesquisa))) {
			criteria.add(Restrictions.ilike(AelAnticoagulante.Fields.DESCRICAO.toString(), srtPesquisa , MatchMode.ANYWHERE ));
		}
	    criteria.add(Restrictions.eq(AelAnticoagulante.Fields.SITUACAO.toString(), DominioSituacao.A));
	    criteria.addOrder(Order.asc(AelAnticoagulante.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}

}
