package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelRecipienteColeta;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class AelRecipienteColetaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelRecipienteColeta> {


	
	private static final long serialVersionUID = -851996836883986969L;

	/**
	 * Realiza a pesquisa de registros da tabela AEL_RECIPIENTES_COLETA.
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param elemento
	 * @return List - lista com os elementos do tipo AelRecipienteColeta.
	 */
	public List<AelRecipienteColeta> pesquisaRecipienteColetaList(Integer firstResult, Integer maxResult, 
			String orderProperty, boolean asc, AelRecipienteColeta elemento) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelRecipienteColeta.class);
		
		mountCriteria(elemento, criteria);
		
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	
	public Long countRecipienteColeta(AelRecipienteColeta elemento) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelRecipienteColeta.class);
		
		mountCriteria(elemento, criteria);
		
		return executeCriteriaCount(criteria);
	}
	
	
	/*public AelRecipienteColeta obterOriginal(Integer seq) {
		
		StringBuilder hql = new StringBuilder()
			.append("select rc.")
			.append(AelRecipienteColeta.Fields.SEQ.toString())
			.append(", rc.").append(AelRecipienteColeta.Fields.DESCRICAO.toString())
			.append(", rc.").append(AelRecipienteColeta.Fields.INDANTICOAG.toString())
			.append(", rc.").append(AelRecipienteColeta.Fields.INDSITUACAO.toString())
			.append(", rc.").append(AelRecipienteColeta.Fields.CRIADOEM.toString())
			.append(", rc.").append(AelRecipienteColeta.Fields.RAPSERVIDORES.toString())
			.append(" from ")
			.append(AelRecipienteColeta.class.getSimpleName())
			.append(" rc ")
			.append(" where rc. ")
			.append(AelRecipienteColeta.Fields.SEQ.toString())
			.append(" = :paramSeq");
		
		Query query = this.createQuery(hql.toString());
		query.setParameter("paramSeq", seq);
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = query.getResultList();
		AelRecipienteColeta returnValue = null; 
		
		if (lista != null && !lista.isEmpty()) {
			// Pelo criterio de Pesquisa deve ter apenas um elemento na lista.
			returnValue = new AelRecipienteColeta();
			for (Object[] listFileds : lista) {
				returnValue.setSeq( (Integer) listFileds[0]);
				returnValue.setDescricao( (String) listFileds[1]);
				returnValue.setIndAnticoag( (DominioSimNao) listFileds[2]);
				returnValue.setIndSituacao( (DominioSituacao) listFileds[3]);
				returnValue.setCriadoEm( (Date) listFileds[4]);
				returnValue.setServidor( (RapServidores) listFileds[5]);
			}
		}
		
		return returnValue;
	}*/
	
	
	private void mountCriteria(AelRecipienteColeta elemento, DetachedCriteria criteria) {
		if(elemento != null) {
			//coluna SEQ
			if(elemento.getSeq() != null && elemento.getSeq().intValue() > 0) {
				criteria.add(Restrictions.eq(AelRecipienteColeta.Fields.SEQ.toString(), elemento.getSeq()));
			}
			//coluna DESCRICAO
			if(elemento.getDescricao() != null && !StringUtils.isBlank(elemento.getDescricao().trim())) {
				criteria.add(Restrictions.ilike(AelRecipienteColeta.Fields.DESCRICAO.toString(), 
						elemento.getDescricao(), MatchMode.ANYWHERE));
			}
			//coluna IND_ANTICOAG
			if(elemento.getIndAnticoag() != null) {
				criteria.add(Restrictions.eq(AelRecipienteColeta.Fields.INDANTICOAG.toString(), elemento.getIndAnticoag()));
			}
			//coluna IND_SITUACAO
			if(elemento.getIndSituacao() != null) {
				criteria.add(Restrictions.eq(AelRecipienteColeta.Fields.INDSITUACAO.toString(), elemento.getIndSituacao()));
			}
		}	
	}
	
	public List<AelRecipienteColeta> listarAelRecipienteAtivoColetavel(Object parametro) {
	    final String srtPesquisa = (String) parametro;
	    DetachedCriteria criteria = DetachedCriteria.forClass(AelRecipienteColeta.class);
	    if (CoreUtil.isNumeroInteger(srtPesquisa)) {
			criteria.add(Restrictions.eq(AelRecipienteColeta.Fields.SEQ.toString(), Integer.valueOf(srtPesquisa)));
		} else if (StringUtils.isNotBlank(StringUtils.trim(srtPesquisa))) {
			criteria.add(Restrictions.ilike(AelRecipienteColeta.Fields.DESCRICAO.toString(), srtPesquisa , MatchMode.ANYWHERE ));
		}
	    criteria.add(Restrictions.eq(AelRecipienteColeta.Fields.INDSITUACAO.toString(), DominioSituacao.A));
	    criteria.addOrder(Order.asc(AelRecipienteColeta.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}
	
	public List<AelRecipienteColeta> pesquisarDescricao(AelRecipienteColeta elemento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelRecipienteColeta.class);
    	//Popula criteria com dados do elemento
    	if(elemento != null) {
    		//Descrição
			if(elemento.getDescricao() != null && !elemento.getDescricao().trim().isEmpty()) {
				criteria.add(Restrictions.eq(AelRecipienteColeta.Fields.DESCRICAO.toString(), elemento.getDescricao()));
			}
    	}
		return executeCriteria(criteria);
	}
	
}
