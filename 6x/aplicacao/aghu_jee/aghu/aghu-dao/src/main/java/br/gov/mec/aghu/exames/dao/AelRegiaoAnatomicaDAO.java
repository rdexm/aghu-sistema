package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelRegiaoAnatomica;
import br.gov.mec.aghu.core.commons.CoreUtil;


public class AelRegiaoAnatomicaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelRegiaoAnatomica> {

	
	private static final long serialVersionUID = -7047305603589356641L;

	private DetachedCriteria obterCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelRegiaoAnatomica.class);
		return criteria;
    }
	
	private DetachedCriteria criarCriteria(AelRegiaoAnatomica elemento) {
    	DetachedCriteria criteria = obterCriteria();
    	
    	//Popula criteria com dados do elemento
    	if(elemento != null) {
    		
    		//Código
    		if(elemento.getSeq() != null) {
    			criteria.add(Restrictions.eq(AelRegiaoAnatomica.Fields.SEQ.toString(), elemento.getSeq()));
    		}
    		
    		//Descrição
			if(elemento.getDescricao() != null && !elemento.getDescricao().trim().isEmpty()) {
				criteria.add(Restrictions.ilike(AelRegiaoAnatomica.Fields.DESCRICAO.toString(), elemento.getDescricao(), MatchMode.ANYWHERE));
			}
			
			//Situação
			if(elemento.getIndSituacao() != null) {
				criteria.add(Restrictions.eq(AelRegiaoAnatomica.Fields.IND_SITUACAO.toString(), elemento.getIndSituacao()));
			}
			
			//Não busca por data de criação
    	}
    	
    	return criteria;
    }
	
	public AelRegiaoAnatomica obterPeloId(Integer codigo) {
		AelRegiaoAnatomica elemento = new AelRegiaoAnatomica();
		elemento.setSeq(codigo);
		DetachedCriteria criteria = criarCriteria(elemento);
		return (AelRegiaoAnatomica) executeCriteriaUniqueResult(criteria);
	}	
	
	public List<AelRegiaoAnatomica> pesquisarDescricao(AelRegiaoAnatomica elemento) {
		DetachedCriteria criteria = obterCriteria();
    	//Popula criteria com dados do elemento
    	if(elemento != null) {
    		//Descrição
			if(elemento.getDescricao() != null && !elemento.getDescricao().trim().isEmpty()) {
				criteria.add(Restrictions.eq(AelRegiaoAnatomica.Fields.DESCRICAO.toString(), elemento.getDescricao()));
			}
    	}
		return executeCriteria(criteria);
	}
	
	public List<AelRegiaoAnatomica> pesquisar(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, AelRegiaoAnatomica elemento) {
		DetachedCriteria criteria = criarCriteria(elemento);
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	public Long pesquisarCount(AelRegiaoAnatomica elemento) {
		DetachedCriteria criteria = criarCriteria(elemento);
		return executeCriteriaCount(criteria);
	}
	
	/**
	 * Retorna uma lista de AelRegiaoAnatomica a partir de <br>
	 * uma consulta da suggestion, por seq ou pela descricao.
	 * 
	 * @param objPesquisa
	 * @return
	 */
	public List<AelRegiaoAnatomica> listarRegiaoAnatomica(String objPesquisa, List<Integer> regioesMama) {
		DetachedCriteria criteria = obterCriteria();
		
		if(CoreUtil.isNumeroInteger(objPesquisa)) {
			criteria.add(Restrictions.eq(AelRegiaoAnatomica.Fields.SEQ.toString(), Integer.valueOf(objPesquisa)));
		} else {
			criteria.add(Restrictions.ilike(AelRegiaoAnatomica.Fields.DESCRICAO.toString(), objPesquisa, MatchMode.ANYWHERE));
		}
		
		if(regioesMama != null) {
			criteria.add(Restrictions.in(AelRegiaoAnatomica.Fields.SEQ.toString(), regioesMama));
		}
		
		criteria.addOrder(Order.asc(AelRegiaoAnatomica.Fields.DESCRICAO.toString()));
		
		return executeCriteria(criteria);
	}

	//#42108 - #25685 - C2
	public List<AelRegiaoAnatomica> pesquisarRegioesAnatomicasAtivasPorDescricaoSeq(String param) {
		DetachedCriteria criteria = obterCriteria();
		adicionarFiltrosRegioesAnatomicasAtivas(param, criteria);
		criteria.addOrder(Order.asc(AelRegiaoAnatomica.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}

	private void adicionarFiltrosRegioesAnatomicasAtivas(String param, DetachedCriteria criteria) {
		if(StringUtils.isNotBlank(param)) {
			if(CoreUtil.isNumeroInteger(param)) {
				criteria.add(Restrictions.eq(AelRegiaoAnatomica.Fields.SEQ.toString(), Integer.getInteger(param)));
			} else {
				criteria.add(Restrictions.ilike(AelRegiaoAnatomica.Fields.DESCRICAO.toString(), param, MatchMode.ANYWHERE));
			}
		}
		criteria.add(Restrictions.eq(AelRegiaoAnatomica.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
	}

	//#42109 - #25685 - C3
	public Boolean verificarRegioesPorSeqAchadoDescricao(List<Integer> seqs, String descricao) {
		DetachedCriteria criteria = obterCriteria();
		adicionarFiltrosCriteriaAchadoRegiaoAnatomica(seqs, descricao, criteria);
		return executeCriteriaExists(criteria);
	}	

	//#42899 - #25685 - C4
	public List<AelRegiaoAnatomica> buscarRegioesAnatomicas(String descricao) {
		DetachedCriteria criteria = obterCriteria();
		adicionarFiltrosCriteriaAchadoRegiaoAnatomica(null, descricao, criteria);
		return executeCriteria(criteria);
	}

	private void adicionarFiltrosCriteriaAchadoRegiaoAnatomica(List<Integer> seqs,
			String descricao, DetachedCriteria criteria) {
		if(seqs != null && !seqs.isEmpty()) {
			criteria.add(Restrictions.in(AelRegiaoAnatomica.Fields.SEQ.toString(), seqs));
		}
		if(StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(AelRegiaoAnatomica.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}
	}

	public Long pesquisarRegioesAnatomicasAtivasPorDescricaoSeqCount(String param) {
		DetachedCriteria criteria = obterCriteria();
		adicionarFiltrosRegioesAnatomicasAtivas(param, criteria);
		return executeCriteriaCount(criteria);
	}	
}
