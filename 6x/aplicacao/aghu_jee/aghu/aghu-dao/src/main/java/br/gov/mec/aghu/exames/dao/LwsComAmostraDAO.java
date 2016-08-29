package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.LwsComAmostra;
import br.gov.mec.aghu.model.LwsComunicacao;



public class LwsComAmostraDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<LwsComAmostra>{
	
	private static final long serialVersionUID = 5889707781010367770L;



	/**
	 * Pesquisa LwsComAmostra através do id da comunicação
	 * @param idComunicacao
	 * @return
	 */
	public List<LwsComAmostra> pesquisarLwsComAmostraPorIdComunicacao(Integer idComunicacao) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(LwsComAmostra.class);
		criteria.createAlias(LwsComAmostra.Fields.LWS_COMUNICACAO.toString(), "COM", DetachedCriteria.INNER_JOIN);
		criteria.add(Restrictions.eq("COM." + LwsComunicacao.Fields.ID_COMUNICACAO.toString(), idComunicacao));
		
		return executeCriteria(criteria);
	}

	
	public List<LwsComAmostra> listarComAmostraPorIdComunicacao(Integer idComunicacao) {
	
		DetachedCriteria criteria = DetachedCriteria.forClass(LwsComAmostra.class);
	
		
		criteria.createAlias(LwsComAmostra.Fields.LWS_COMUNICACAO.toString(), "LCM");
		
		criteria.add(Restrictions.eq("LCM."+ LwsComunicacao.Fields.ID_COMUNICACAO.toString(), idComunicacao ));
	
		
		return executeCriteria(criteria);
	}

	
	
	/**
	 * Obtém a quantidade de LwsComAmostra através do id da comunicação
	 * @param idComunicacao
	 * @return
	 */
	public Integer pesquisarLwsComAmostraPorIdComunicacaoCount(Integer idComunicacao) {
		return this.pesquisarLwsComAmostraPorIdComunicacao(idComunicacao).size();
	}
	
	
}
