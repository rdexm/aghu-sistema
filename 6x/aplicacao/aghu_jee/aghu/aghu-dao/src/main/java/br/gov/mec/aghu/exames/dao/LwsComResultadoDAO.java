package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.LwsComResultado;
import br.gov.mec.aghu.model.LwsComSolicitacaoExame;
import br.gov.mec.aghu.model.LwsComunicacao;

public class LwsComResultadoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<LwsComResultado>{
	
	private static final long serialVersionUID = -5306214680160693647L;

	/**
	 * Pesquisa LwsComResultado 
	 * @param idComunicacao
	 * @return
	 */
	public List<LwsComResultado> pesquisarLwsComResultadoPorIdComunicIdExame(Integer idComunicacao, Integer idExame) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(LwsComResultado.class);
		
		
		criteria.createAlias(LwsComResultado.Fields.LWS_COMUNICACAO.toString(), "COM", DetachedCriteria.INNER_JOIN);
		criteria.createAlias(LwsComResultado.Fields.LWS_COM_SOLICITACAO_EXAMES.toString(), "CSE", DetachedCriteria.INNER_JOIN);
		
		criteria.add(Restrictions.eq("COM." + LwsComunicacao.Fields.ID_COMUNICACAO.toString(), idComunicacao));
		criteria.add(Restrictions.eq("CSE." + LwsComSolicitacaoExame.Fields.ID.toString(), idExame));
			
		return executeCriteria(criteria);
	}


}
