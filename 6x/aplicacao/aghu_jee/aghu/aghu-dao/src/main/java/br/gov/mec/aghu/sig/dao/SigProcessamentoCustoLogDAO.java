package br.gov.mec.aghu.sig.dao;

import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioEtapaProcessamento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigPassos;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.model.SigProcessamentoCustoLog;
import br.gov.mec.aghu.model.SigProcessamentoPassos;
import br.gov.mec.aghu.core.utils.DateUtil;

public class SigProcessamentoCustoLogDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SigProcessamentoCustoLog> {

	private static final long serialVersionUID = 4214418832743124789L;

	public void removerPorProcessamento(Integer idProcessamentoCusto){
		StringBuilder sql = new StringBuilder(50);
		sql.append(" DELETE ").append(SigProcessamentoCustoLog.class.getSimpleName().toString()).append(" ca ");
		sql.append(" WHERE ca.").append(SigProcessamentoCustoLog.Fields.PROCESSAMENTO_CUSTO.toString()).append('.').append(SigProcessamentoCusto.Fields.SEQ.toString()).append(" = :pSeq");
		Query query = this.createQuery(sql.toString());
		query.setParameter("pSeq", idProcessamentoCusto);
		query.executeUpdate();
	}
	
	public void removerSigProcessamentoCustoLogByIdPasso(Integer idPasso){
		StringBuilder sql = new StringBuilder(50);
		sql.append(" DELETE ").append(SigProcessamentoCustoLog.class.getSimpleName().toString()).append(" ca ");
		sql.append(" WHERE ca.").append(SigProcessamentoCustoLog.Fields.PASSO_PROCESSAMENTO.toString()).append('.').append(SigProcessamentoPassos.Fields.SEQ.toString()).append(" = :pSeq");
		Query query = this.createQuery(sql.toString());
		query.setParameter("pSeq", idPasso);
		query.executeUpdate();
	}
	
	public List<SigProcessamentoCustoLog> pesquisarHistoricoProcessamentoCusto(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			SigProcessamentoCusto processamentoCusto, DominioEtapaProcessamento etapa, SigPassos passo){
		DetachedCriteria criteria = this.criarCriteriaPesquisaHistoricoProcessamentoCusto(processamentoCusto, etapa, passo, true);
		return this.executeCriteria(criteria, firstResult, maxResult, orderProperty, true);
	}
	
	public Long pesquisarHistoricoProcessamentoCustoCount(SigProcessamentoCusto processamentoCusto, DominioEtapaProcessamento etapa, SigPassos passo){
		DetachedCriteria criteria = this.criarCriteriaPesquisaHistoricoProcessamentoCusto(processamentoCusto, etapa, passo, false);
		return this.executeCriteriaCount(criteria);
	}
	
	protected DetachedCriteria criarCriteriaPesquisaHistoricoProcessamentoCusto (SigProcessamentoCusto processamentoCusto, DominioEtapaProcessamento etapa, SigPassos passo, boolean utilizarOrder){
		DetachedCriteria criteria = DetachedCriteria.forClass( SigProcessamentoCustoLog.class);
		
		criteria.createAlias(SigProcessamentoCustoLog.Fields.SERVIDOR_RESPONSAVEL.toString(), "serv", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("serv."+RapServidores.Fields.PESSOA_FISICA.toString(), "pes", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq( SigProcessamentoCustoLog.Fields.PROCESSAMENTO_CUSTO.toString(), processamentoCusto));
		
		if(etapa != null){
			criteria.add(Restrictions.eq( SigProcessamentoCustoLog.Fields.ETAPA_PROCESSAMENTO.toString(), etapa));
		}
		
		if(passo != null){
			criteria.
				createCriteria(SigProcessamentoCustoLog.Fields.PASSO_PROCESSAMENTO.toString(), "pc", JoinType.INNER_JOIN).
				add(Restrictions.eq("pc."+SigProcessamentoPassos.Fields.PASSOS.toString(), passo));
		}
		
		if(utilizarOrder){
			criteria.addOrder(Order.desc(SigProcessamentoCustoLog.Fields.CRIADO_EM.toString()));
			criteria.addOrder(Order.desc(SigProcessamentoCustoLog.Fields.SEQ.toString()));
		}
		
		return criteria;
	}
	
	public String calcularTempoExecucao(SigProcessamentoCusto processamento, SigProcessamentoPassos passo){
		DetachedCriteria criteria = DetachedCriteria.forClass( SigProcessamentoCustoLog.class);
		
		criteria.setProjection(
			Projections.projectionList()
				.add(Projections.min(SigProcessamentoCustoLog.Fields.CRIADO_EM.toString()))
				.add(Projections.max(SigProcessamentoCustoLog.Fields.CRIADO_EM.toString()))
		);
		
		criteria.add(Restrictions.eq( SigProcessamentoCustoLog.Fields.PROCESSAMENTO_CUSTO.toString(), processamento));
		if(passo != null){
			criteria.add(Restrictions.eq( SigProcessamentoCustoLog.Fields.PASSO_PROCESSAMENTO .toString(), passo));
		}
		
		Object[] retorno  = (Object[])this.executeCriteriaUniqueResult(criteria);
		
		if(retorno != null && retorno[0] != null && retorno[1] != null){
			return DateUtil.calculaDiferencaTempo((Date)retorno[0], (Date)retorno[1]);
		}
		return null;
	}
}
