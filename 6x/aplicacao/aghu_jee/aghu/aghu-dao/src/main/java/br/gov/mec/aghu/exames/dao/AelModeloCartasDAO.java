package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelModeloCartas;
import br.gov.mec.aghu.core.commons.CoreUtil;


public class AelModeloCartasDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelModeloCartas> {

	
	private static final long serialVersionUID = 3819482018136644830L;

	private DetachedCriteria obterCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelModeloCartas.class);
		return criteria;
    }
	
	private DetachedCriteria criarCriteria(AelModeloCartas elemento) {
    	DetachedCriteria criteria = obterCriteria();
    	
    	//Popula criteria com dados do elemento
    	if(elemento != null) {
    		
    		//Código
    		if(elemento.getSeq() != null) {
    			criteria.add(Restrictions.eq(AelModeloCartas.Fields.SEQ.toString(), elemento.getSeq()));
    		}
    		
    		//Version
    		if(elemento.getVersion() != null) {
    			criteria.add(Restrictions.eq(AelModeloCartas.Fields.VERSION.toString(), elemento.getVersion()));
    		}
    		
    		//Nome
			if(StringUtils.isNotBlank(elemento.getNome())) {
				criteria.add(Restrictions.ilike(AelModeloCartas.Fields.NOME.toString(), elemento.getNome(), MatchMode.ANYWHERE));
			}
			
    		//Texto
			if(StringUtils.isNotBlank(elemento.getTexto())) {
				criteria.add(Restrictions.ilike(AelModeloCartas.Fields.TEXTO.toString(), elemento.getTexto(), MatchMode.ANYWHERE));
			}
			
    		//Situação
			if(elemento.getIndSituacao() != null) {
				criteria.add(Restrictions.eq(AelModeloCartas.Fields.SITUACAO.toString(), elemento.getIndSituacao()));
			}
			
    		//Texto
			if(elemento.getCriadoEm() != null) {
				criteria.add(Restrictions.eq(AelModeloCartas.Fields.CRIADO_EM.toString(), elemento.getCriadoEm()));
			}
			
	   		//Servidor
			if(elemento.getServidor() != null) {
				criteria.add(Restrictions.eq(AelModeloCartas.Fields.SERVIDOR.toString(), elemento.getServidor()));
			}

    		
    	}
    	
    	return criteria;
    }
	
	public List<AelModeloCartas> pesquisar(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, AelModeloCartas elemento) {
		DetachedCriteria criteria = criarCriteria(elemento);
		if (orderProperty != null && asc) {
			criteria.addOrder(Order.asc(orderProperty));	
		}
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	public Long pesquisarCount(AelModeloCartas elemento) {
		DetachedCriteria criteria = criarCriteria(elemento);
		return executeCriteriaCount(criteria);
	}

	public List<AelModeloCartas> listarAelModeloCartasAtivas(Object parametro) {
	    final String srtPesquisa = (String) parametro;
	    DetachedCriteria criteria = DetachedCriteria.forClass(AelModeloCartas.class);
	    criteria.add(Restrictions.eq(AelModeloCartas.Fields.SITUACAO.toString(), DominioSituacao.A));

	    if (CoreUtil.isNumeroShort(srtPesquisa)) {
			criteria.add(Restrictions.eq(AelModeloCartas.Fields.SEQ.toString(), Short.valueOf(srtPesquisa)));
		} else if (StringUtils.isNotBlank(srtPesquisa)) {
			criteria.add(Restrictions.ilike(AelModeloCartas.Fields.NOME.toString(), srtPesquisa , MatchMode.ANYWHERE ));
		}
	    criteria.addOrder(Order.asc(AelModeloCartas.Fields.NOME.toString()));
		return executeCriteria(criteria);
	}
	
	public List<AelModeloCartas> listarAelModeloCartas(Object parametro) {
	    final String srtPesquisa = (String) parametro;
	    DetachedCriteria criteria = DetachedCriteria.forClass(AelModeloCartas.class);
	    if (CoreUtil.isNumeroShort(srtPesquisa)) {
			criteria.add(Restrictions.eq(AelModeloCartas.Fields.SEQ.toString(), Short.valueOf(srtPesquisa)));
		} else if (StringUtils.isNotBlank(srtPesquisa)) {
			criteria.add(Restrictions.ilike(AelModeloCartas.Fields.NOME.toString(), srtPesquisa , MatchMode.ANYWHERE ));
		}
	    criteria.addOrder(Order.asc(AelModeloCartas.Fields.SEQ.toString()));
		return executeCriteria(criteria);
	}
}
