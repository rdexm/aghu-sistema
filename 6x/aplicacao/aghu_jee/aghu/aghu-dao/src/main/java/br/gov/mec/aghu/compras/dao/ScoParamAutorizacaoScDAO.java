package br.gov.mec.aghu.compras.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoParamAutorizacaoSc;

public class ScoParamAutorizacaoScDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoParamAutorizacaoSc> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3664115492728788524L;

	public List<ScoParamAutorizacaoSc> pesquisarParamAutorizacaoSc(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, ScoParamAutorizacaoSc scoParamAutorizacaoSc) {

		DetachedCriteria criteria = montarClausulaWhere(scoParamAutorizacaoSc);

		criteria.createAlias(ScoParamAutorizacaoSc.Fields.CENTRO_CUSTO_SOLICITANTE.toString(), "CCS", JoinType.INNER_JOIN);
		criteria.createAlias(ScoParamAutorizacaoSc.Fields.CENTRO_CUSTO_APLICACAO.toString(), "CCA", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(ScoParamAutorizacaoSc.Fields.SERVIDOR.toString(), "SERV", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SERV."+RapServidores.Fields.PESSOA_FISICA.toString(), "SERV_PF", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(ScoParamAutorizacaoSc.Fields.SERVIDOR_AUTORIZA.toString(), "SERV_AUT", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SERV_AUT."+RapServidores.Fields.PESSOA_FISICA.toString(), "SERV_AUT_PF", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(ScoParamAutorizacaoSc.Fields.SERVIDOR_COMPRA.toString(), "SERV_COMP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SERV_COMP."+RapServidores.Fields.PESSOA_FISICA.toString(), "SERV_COMP_PF", JoinType.LEFT_OUTER_JOIN);

		criteria.addOrder(Order.asc(ScoParamAutorizacaoSc.Fields.CCT_CODIGO.toString()));

		return this.executeCriteria(criteria, firstResult, maxResult,
				orderProperty, asc);
	}

	public Long pesquisarParamAutorizacaoScCount(
			ScoParamAutorizacaoSc scoParamAutorizacaoSc) {

		DetachedCriteria criteria = montarClausulaWhere(scoParamAutorizacaoSc);

		return this.executeCriteriaCount(criteria);
	}

	private DetachedCriteria montarClausulaWhere(
			ScoParamAutorizacaoSc scoParamAutorizacaoSc) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoParamAutorizacaoSc.class);

		if (scoParamAutorizacaoSc.getCentroCustoSolicitante() != null) {
			criteria.add(Restrictions.eq(
					ScoParamAutorizacaoSc.Fields.CENTRO_CUSTO_SOLICITANTE
							.toString(), scoParamAutorizacaoSc
							.getCentroCustoSolicitante()));
		}

		if (scoParamAutorizacaoSc.getCentroCustoAplicacao() != null) {
			criteria.add(Restrictions.eq(
					ScoParamAutorizacaoSc.Fields.CENTRO_CUSTO_APLICACAO
							.toString(), scoParamAutorizacaoSc
							.getCentroCustoAplicacao()));
		}

		if (scoParamAutorizacaoSc.getServidor() != null) {
			criteria.add(Restrictions.eq(
					ScoParamAutorizacaoSc.Fields.SERVIDOR.toString(),
					scoParamAutorizacaoSc.getServidor()));
		}

		if (scoParamAutorizacaoSc.getIndSituacao() != null) {
			criteria.add(Restrictions.eq(
					ScoParamAutorizacaoSc.Fields.IND_SITUACAO.toString(),
					scoParamAutorizacaoSc.getIndSituacao()));
		}

		if (scoParamAutorizacaoSc.getIndHierarquiaCCusto() != null) {
			criteria.add(Restrictions.eq(
					ScoParamAutorizacaoSc.Fields.IND_HIERARQUIA_CCUSTO
							.toString(), scoParamAutorizacaoSc
							.getIndHierarquiaCCusto()));
		}
		return criteria;
	}

	public ScoParamAutorizacaoSc pesquisarParamAutorizacao(
			ScoParamAutorizacaoSc scoParamAutorizacaoSc) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoParamAutorizacaoSc.class);

		if (scoParamAutorizacaoSc.getCentroCustoSolicitante() != null) {
			criteria.add(Restrictions.eq(
					ScoParamAutorizacaoSc.Fields.CENTRO_CUSTO_SOLICITANTE
							.toString(), scoParamAutorizacaoSc
							.getCentroCustoSolicitante()));
		}

		if (scoParamAutorizacaoSc.getCentroCustoAplicacao() != null) {
			criteria.add(Restrictions.eq(
					ScoParamAutorizacaoSc.Fields.CENTRO_CUSTO_APLICACAO
							.toString(), scoParamAutorizacaoSc
							.getCentroCustoAplicacao()));
		}

		if (scoParamAutorizacaoSc.getServidor() != null) {
			criteria.add(Restrictions.eq(
					ScoParamAutorizacaoSc.Fields.SERVIDOR.toString(),
					scoParamAutorizacaoSc.getServidor()));
		}

		if (scoParamAutorizacaoSc.getIndSituacao() != null) {
			criteria.add(Restrictions.eq(
					ScoParamAutorizacaoSc.Fields.IND_SITUACAO.toString(),
					scoParamAutorizacaoSc.getIndSituacao()));
		}

		return (ScoParamAutorizacaoSc) this
				.executeCriteriaUniqueResult(criteria);
	}

	public List<ScoParamAutorizacaoSc> pesquisarParametrosAutorizacaoSC(
			FccCentroCustos centroCusto) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoParamAutorizacaoSc.class);

		criteria.add(Restrictions.eq(
				ScoParamAutorizacaoSc.Fields.CENTRO_CUSTO_SOLICITANTE
						.toString(), centroCusto));
		
		criteria.add(Restrictions.eq(
				ScoParamAutorizacaoSc.Fields.IND_SITUACAO
						.toString(), DominioSituacao.A));

		criteria.addOrder(Order.asc(ScoParamAutorizacaoSc.Fields.CCT_CODIGO
				.toString()));
		criteria.addOrder(Order
				.asc(ScoParamAutorizacaoSc.Fields.CCT_CODIGO_APLICACAO
						.toString()));
		criteria.addOrder(Order.asc(ScoParamAutorizacaoSc.Fields.SER_VIN_CODIGO
				.toString()));
		criteria.addOrder(Order.asc(ScoParamAutorizacaoSc.Fields.SER_MATRICULA
				.toString()));
		
		return this.executeCriteria(criteria);
		
	}
	
	public ScoParamAutorizacaoSc obterParametrosAutorizacaoSCPrioridade(
			FccCentroCustos centroCusto, FccCentroCustos centroCustoAplicacao,
			RapServidores servidor) {

				
		List<ScoParamAutorizacaoSc> listaCCRetorno = new ArrayList<ScoParamAutorizacaoSc>();
		
		listaCCRetorno.addAll(this.pesquisarParametrosAutorizacaoSC(centroCusto));
		boolean flagParar = false;
		
		if (listaCCRetorno == null ||
			listaCCRetorno.isEmpty()){
			return null;
		}	
		
		ScoParamAutorizacaoSc paramatroAutorizacaoSC = new ScoParamAutorizacaoSc();
		Iterator<ScoParamAutorizacaoSc> itScoParamAutorizacaoSc = listaCCRetorno.iterator(); 
		if (itScoParamAutorizacaoSc != null){
			do {
				paramatroAutorizacaoSC = itScoParamAutorizacaoSc.next();
				
				if (paramatroAutorizacaoSC.getCentroCustoAplicacao() != null
						&& paramatroAutorizacaoSC.getServidor() != null) {
					flagParar = paramatroAutorizacaoSC
							.getCentroCustoAplicacao().equals(
									centroCustoAplicacao)
							&& paramatroAutorizacaoSC.getServidor().equals(
									servidor);
				}

				if (!flagParar
						&& paramatroAutorizacaoSC.getCentroCustoAplicacao() != null) {
					flagParar = (paramatroAutorizacaoSC
							.getCentroCustoAplicacao().equals(
									centroCustoAplicacao) && paramatroAutorizacaoSC
							.getServidor() == null);
				}

				if (!flagParar && paramatroAutorizacaoSC.getServidor() != null) {
					flagParar = (paramatroAutorizacaoSC
							.getCentroCustoAplicacao() == null && paramatroAutorizacaoSC
							.getServidor().equals(servidor));
				}

				if (!flagParar) {
					flagParar = (paramatroAutorizacaoSC
							.getCentroCustoAplicacao() == null && paramatroAutorizacaoSC
							.getServidor() == null);
				}

				if (!flagParar) {
					flagParar = !(itScoParamAutorizacaoSc.hasNext());
				}

			} while (!flagParar);
		}
		return paramatroAutorizacaoSC;
	}

	public ScoParamAutorizacaoSc obterParametrosAutorizacaoSC(
			Integer seq) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoParamAutorizacaoSc.class);

		criteria.createAlias(ScoParamAutorizacaoSc.Fields.CENTRO_CUSTO_SOLICITANTE.toString(), "CCT", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoParamAutorizacaoSc.Fields.CENTRO_CUSTO_APLICACAO.toString(), "CCA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoParamAutorizacaoSc.Fields.SERVIDOR.toString(), "SRV", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SRV."+RapServidores.Fields.PESSOA_FISICA.toString(), "PRV", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoParamAutorizacaoSc.Fields.SERVIDOR_AUTORIZA.toString(), "SRVA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SRVA."+RapServidores.Fields.PESSOA_FISICA.toString(), "PRVA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoParamAutorizacaoSc.Fields.SERVIDOR_COMPRA.toString(), "SRVC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SRVC."+RapServidores.Fields.PESSOA_FISICA.toString(), "PRVC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoParamAutorizacaoSc.Fields.SERVIDOR_CRIACAO.toString(), "SRVCI", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SRVCI."+RapServidores.Fields.PESSOA_FISICA.toString(), "PRVCI", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoParamAutorizacaoSc.Fields.PONTO_PARADA.toString(), "PPS", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoParamAutorizacaoSc.Fields.PONTO_PARADA_PROXIMA.toString(), "PPP", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(
				ScoParamAutorizacaoSc.Fields.SEQ.toString(), seq));
		
		return (ScoParamAutorizacaoSc) this.executeCriteriaUniqueResult(criteria);
	}

}