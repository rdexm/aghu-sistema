package br.gov.mec.aghu.sig.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigAtividades;
import br.gov.mec.aghu.model.SigObjetoCustoHistoricos;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;


public class SigObjetoCustoHistoricosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SigObjetoCustoHistoricos> {

	private static final long serialVersionUID = -9109613555360476823L;

	public List<SigObjetoCustoHistoricos> pesquisarObjetoCustoHistoricos(SigObjetoCustoVersoes objetoCustoVersao) {
		return this.executeCriteria(this.criarPesquisaHistoricoObjetoCustoCriteria(objetoCustoVersao, true));
	}
	
	public List<SigObjetoCustoHistoricos> pesquisarHistoricoObjetoCusto(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, SigObjetoCustoVersoes objetoCustoVersao){
		return this.executeCriteria(this.criarPesquisaHistoricoObjetoCustoCriteria(objetoCustoVersao,true), firstResult, maxResult, orderProperty, true);
	}

	public Long pesquisarHistoricoObjetoCustoCount(
			SigObjetoCustoVersoes objetoCustoVersao){
		return this.executeCriteriaCount(this.criarPesquisaHistoricoObjetoCustoCriteria(objetoCustoVersao,false));
	}
	
	public List<SigObjetoCustoHistoricos> pesquisarHistoricoAtividades(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			SigAtividades atividade) {
		return this.executeCriteria(this.criarPesquisaHistoricoAtividadeCriteria(atividade,true), firstResult, maxResult, orderProperty, true);
	}

	public Long pesquisarHistoricoAtividadeCount(SigAtividades atividade) {
		return this.executeCriteriaCount(this.criarPesquisaHistoricoAtividadeCriteria(atividade,false));
	}
	
	protected DetachedCriteria criarPesquisaHistoricoAtividadeCriteria (SigAtividades atividade, boolean utilizarOrder){
		DetachedCriteria criteria = DetachedCriteria.forClass(SigObjetoCustoHistoricos.class);
		
		criteria.createAlias(SigObjetoCustoHistoricos.Fields.RAP_SERVIDORES.toString(), "serv", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("serv."+RapServidores.Fields.PESSOA_FISICA.toString(), "pes", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(SigObjetoCustoHistoricos.Fields.SIG_ATIVIDADES.toString(), atividade));
		if(utilizarOrder){
			criteria.addOrder(Order.desc(SigObjetoCustoHistoricos.Fields.CRIADO_EM.toString()));
		}
		return criteria;
	}
	
	protected DetachedCriteria criarPesquisaHistoricoObjetoCustoCriteria (SigObjetoCustoVersoes objetoCustoVersao, boolean utilizarOrder){
		DetachedCriteria criteria = DetachedCriteria.forClass(SigObjetoCustoHistoricos.class);
		
		criteria.createAlias(SigObjetoCustoHistoricos.Fields.RAP_SERVIDORES.toString(), "serv", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("serv."+RapServidores.Fields.PESSOA_FISICA.toString(), "pes", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(SigObjetoCustoHistoricos.Fields.SIG_OBJETO_CUSTO_VERSOES.toString(), objetoCustoVersao));
		if(utilizarOrder){
			criteria.addOrder(Order.desc(SigObjetoCustoHistoricos.Fields.CRIADO_EM.toString()));
		}
		return criteria;
	}
}
