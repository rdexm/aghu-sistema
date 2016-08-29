package br.gov.mec.aghu.compras.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Query;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoParamAutorizacaoSc;
import br.gov.mec.aghu.model.ScoParamProgEntgAf;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class ScoParamProgEntgAfDAO extends BaseDao<ScoParamProgEntgAf> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8590682753373651516L;


	public List<ScoParamAutorizacaoSc> pesquisarParamAutorizacaoSc(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, ScoParamAutorizacaoSc scoParamAutorizacaoSc) {

		DetachedCriteria criteria = montarClausulaWhere(scoParamAutorizacaoSc);

		criteria.addOrder(Order.asc(ScoParamAutorizacaoSc.Fields.CCT_CODIGO
				.toString()));

		return this.executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	public Long pesquisarParamAutorizacaoScCount(
			ScoParamAutorizacaoSc scoParamAutorizacaoSc) {

		DetachedCriteria criteria = montarClausulaWhere(scoParamAutorizacaoSc);

		return this.executeCriteriaCount(criteria);
	}
	
	public void excluirScoParamProgEntgAf(ScoParamProgEntgAf programacaoEntrega) {
		ScoParamProgEntgAf afExcluir = obterPorChavePrimaria(programacaoEntrega.getId());
		this.remover(afExcluir);
		this.flush();
	}
	
	public void persistirScoParamProgEntgAf(ScoParamProgEntgAf programacaoEntrega, Boolean inserir) {
		if(inserir){
			this.persistir(programacaoEntrega);
		}else{
			this.atualizar(programacaoEntrega);
		}
	}
	
	public ScoParamProgEntgAf obterPorSolicitacaoDeCompra(Integer numero) {
		List<ScoParamProgEntgAf> listaProgEntg = new ArrayList<ScoParamProgEntgAf>(0);
		DetachedCriteria criteria = montarCriteriaProgEntg(numero);
		listaProgEntg =  this.executeCriteria(criteria);
		if(listaProgEntg == null || listaProgEntg.isEmpty()){
			return null;
		}
		return listaProgEntg.get(0);
	}

	public Long obterPorSolicitacaoDeCompraCount(Integer numero) {

		DetachedCriteria criteria = montarCriteriaProgEntg(numero);
		return this.executeCriteriaCount(criteria);
	}
	
	
	private DetachedCriteria montarCriteriaProgEntg(Integer numero) {

		DetachedCriteria criteria = DetachedCriteria.forClass(ScoParamProgEntgAf.class);
		if (numero != null) {
			criteria.add(Restrictions.eq(ScoParamProgEntgAf.Fields.SCO_SOLICITACAO_DE_COMPRA.toString(), numero));
		}
		return criteria;
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
	
	public List<ScoParamProgEntgAf> buscaVinculosAfSolicitacaoCompras(Integer numeroAF,  Integer numeroItem) {
		DetachedCriteria criteria = obterCriteria();
		criteria.createAlias(ScoParamProgEntgAf.Fields.SCO_ITEM_AUTORIZACAO_FORN.toString(), "IAF");
		criteria.add(Restrictions.eq("IAF." + ScoItemAutorizacaoForn.Fields.ID_AUT_FORN_NUMERO.toString(), numeroAF));
		if(numeroItem != null) {
			criteria.add(Restrictions.eq("IAF." + ScoItemAutorizacaoForn.Fields.NUMERO.toString(), numeroItem));
		}
		return executeCriteria(criteria);
	}
	

	private DetachedCriteria obterCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoParamProgEntgAf.class);
		return criteria;
	}
	
	//#26773 D2
	public Integer deletarRelacaoParcelaSolicitacaoCompra(Integer slcNumero) throws ApplicationBusinessException {
		String sql = " delete from "+ScoParamProgEntgAf.class.getSimpleName() 
				   + " where slc_numero= :slcNumero ";
		Query query = createQuery(sql);
		query.setParameter("slcNumero", slcNumero);
		return query.executeUpdate();
	}
	



}