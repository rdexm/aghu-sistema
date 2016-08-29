package br.gov.mec.aghu.sig.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.hibernate.FetchMode;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoVersoesCustos;
import br.gov.mec.aghu.dominio.DominioTipoObjetoCusto;
import br.gov.mec.aghu.dominio.DominioTipoObjetoCustoCcts;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SigDirecionadores;
import br.gov.mec.aghu.model.SigObjetoCustoCcts;
import br.gov.mec.aghu.model.SigObjetoCustoClientes;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.model.SigObjetoCustos;
import br.gov.mec.aghu.sig.custos.processamento.business.ICustosSigProcessamentoFacade;

public class SigObjetoCustoCctsDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SigObjetoCustoCcts> {

	private static final long serialVersionUID = -2098613783842247132L;

	public List<SigObjetoCustoCcts> pesquisarObjetosCustoCentroCusto(SigObjetoCustoVersoes objetoCustoVersao) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SigObjetoCustoCcts.class);
		criteria.add(Restrictions.eq(SigObjetoCustoCcts.Fields.OBJETO_CUSTO_VERSAO.toString(), objetoCustoVersao));

		return executeCriteria(criteria);
	}

	public List<SigObjetoCustoCcts> pesquisarObjetosCustoCentroCusto(FccCentroCustos centroCustos) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SigObjetoCustoCcts.class);

		//Adiciona regras para carregar os campos já no join
		criteria.setFetchMode(SigObjetoCustoCcts.Fields.OBJETO_CUSTO_VERSAO.toString(), FetchMode.JOIN);
		criteria.setFetchMode(SigObjetoCustoCcts.Fields.OBJETO_CUSTO_VERSAO.toString() + "." + SigObjetoCustoVersoes.Fields.OBJETO_CUSTO, FetchMode.JOIN);
		criteria.setFetchMode(SigObjetoCustoCcts.Fields.OBJETO_CUSTO_VERSAO.toString() + "." + SigObjetoCustoVersoes.Fields.OBJETO_CUSTO + "."
				+ SigObjetoCustos.Fields.OBJETO_CUSTO_PESO, FetchMode.JOIN);

		//Adiciona regra para selecionar somente os objetos de custos versão que não sejam inativos
		criteria.createCriteria(SigObjetoCustoCcts.Fields.OBJETO_CUSTO_VERSAO.toString()).add(
				Restrictions.ne(SigObjetoCustoVersoes.Fields.IND_SITUACAO.toString(), DominioSituacao.I));

		//Adiciona a restrição pelo centro de custos
		criteria.add(Restrictions.eq(SigObjetoCustoCcts.Fields.CENTRO_CUSTO.toString(), centroCustos));

		return executeCriteria(criteria);
	}
	
	/**
	* SQL Busca objetos de custo de apoio com data de inicio e fim ativas, objeto de custo tipo Apoio.
	* 
	* @author rmalvezzi
	* @return					ScrollableResults contendo uma lista de SigObjetoCustoCcts.
	*/
	public ScrollableResults buscarObjetosCustoApoioCarga() {	
		DetachedCriteria criteria = this.criarDetachedCriteriaBuscaOCApoioCarga();
		criteria.addOrder(Order.asc("fcc." + FccCentroCustos.Fields.CODIGO.toString()));
		return this.createScrollableResults(criteria, ICustosSigProcessamentoFacade.SCROLLABLE_FETCH_SIZE, ScrollMode.FORWARD_ONLY);
	}
	
	/**
	*  Cria o criteria para ser executado.
	* 
	* @author rmalvezzi
	* @return					DetachedCriteria com as restrições necessárias para a etapa de carga de apoio.
	*/
	private DetachedCriteria criarDetachedCriteriaBuscaOCApoioCarga() {	
		DetachedCriteria criteria = DetachedCriteria.forClass(SigObjetoCustoCcts.class, "occ");

		criteria.createCriteria("occ." + SigObjetoCustoCcts.Fields.OBJETO_CUSTO_VERSAO.toString(), "ocv", JoinType.INNER_JOIN);
		criteria.createCriteria("ocv." + SigObjetoCustoVersoes.Fields.OBJETO_CUSTO.toString(), "obj", JoinType.INNER_JOIN);
		criteria.createCriteria("occ." + SigObjetoCustoCcts.Fields.CENTRO_CUSTO.toString(), "fcc", JoinType.INNER_JOIN);
		Date dataAtual = new Date();
		criteria.add(Restrictions.le("ocv." + SigObjetoCustoVersoes.Fields.DATA_INICIO.toString(), dataAtual));
		criteria.add(Restrictions.or(Restrictions.ge("ocv." + SigObjetoCustoVersoes.Fields.DATA_FIM.toString(), dataAtual),
				Restrictions.isNull("ocv." + SigObjetoCustoVersoes.Fields.DATA_FIM.toString())));
		criteria.add(Restrictions.in("ocv." + SigObjetoCustoVersoes.Fields.IND_SITUACAO.toString(), new DominioSituacaoVersoesCustos[] {
				DominioSituacaoVersoesCustos.A, DominioSituacaoVersoesCustos.P }));
		criteria.add(Restrictions.eq("obj." + SigObjetoCustos.Fields.IND_TIPO.toString(), DominioTipoObjetoCusto.AP));
		//criteria.add(Restrictions.eq("occ." + SigObjetoCustoCcts.Fields.IND_TIPO.toString(), DominioTipoObjetoCustoCcts.P));
		criteria.add(Restrictions.eq("occ." + SigObjetoCustoCcts.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		return criteria;
	}
	
	/**
	* Busca clientes para rateio do objeto de custo.
	*  
	* @author rmalvezzi
	* @return							Lista de SigObjetoCustoClientes.
	*/
		
	public List<SigObjetoCustoClientes> buscarClientesRateioObjetoCustoSemValor() {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigObjetoCustoClientes.class, "occ1");
		criteria.createCriteria("occ1." + SigObjetoCustoClientes.Fields.DIRECIONADORES.toString(), "dir1", JoinType.INNER_JOIN);
		criteria.createCriteria("occ1." + SigObjetoCustoClientes.Fields.CENTRO_CUSTO.toString(), "fcc", JoinType.INNER_JOIN);
		criteria.createCriteria("occ1." + SigObjetoCustoClientes.Fields.OBJETO_CUSTO_VERSAO.toString(), "ocv", JoinType.INNER_JOIN);
		criteria.createCriteria("ocv." + SigObjetoCustoVersoes.Fields.OBJETO_CUSTO.toString(), "obj", JoinType.INNER_JOIN);
		
		DetachedCriteria criteriaIn = this.criarDetachedCriteriaBuscaOCApoioCarga();
		criteriaIn.setProjection(Projections.projectionList().add(Projections.property("ocv." + SigObjetoCustoVersoes.Fields.SEQ.toString())));
		criteria.add(Subqueries.propertyIn("occ1." + SigObjetoCustoClientes.Fields.OBJETO_CUSTO_VERSAO_SEQ.toString(), criteriaIn));
		criteria.add(Restrictions.eq("occ1." + SigObjetoCustoClientes.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("dir1." + SigDirecionadores.Fields.INDICADOR_COLETA_SISTEMA.toString(), false));
		criteria.add(Restrictions.or(Restrictions.isNull("occ1." + SigObjetoCustoClientes.Fields.VALOR.toString()),
				Restrictions.eq("occ1." + SigObjetoCustoClientes.Fields.VALOR.toString(), BigDecimal.ZERO)));
		return this.executeCriteria(criteria);
	}

	public SigObjetoCustoCcts obterObjetoCustoCctsPrincipal(Integer seqObjetoCustoVersao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigObjetoCustoCcts.class);
		criteria.setFetchMode(SigObjetoCustoCcts.Fields.CENTRO_CUSTO.toString(), FetchMode.JOIN);
		criteria.add(Restrictions.eq(SigObjetoCustoCcts.Fields.OBJETO_CUSTO_VERSAO+"."+SigObjetoCustoVersoes.Fields.SEQ, seqObjetoCustoVersao));
		criteria.add(Restrictions.eq(SigObjetoCustoCcts.Fields.IND_TIPO.toString(), DominioTipoObjetoCustoCcts.P));
		return (SigObjetoCustoCcts) this.executeCriteriaUniqueResult(criteria);
	}
}
