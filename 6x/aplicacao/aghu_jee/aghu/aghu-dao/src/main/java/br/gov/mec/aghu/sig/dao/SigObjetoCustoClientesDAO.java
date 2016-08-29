package br.gov.mec.aghu.sig.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SigCalculoObjetoCusto;
import br.gov.mec.aghu.model.SigCentroProducao;
import br.gov.mec.aghu.model.SigDirecionadores;
import br.gov.mec.aghu.model.SigObjetoCustoClientes;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.sig.custos.vo.ClienteObjetoCustoVO;

public class SigObjetoCustoClientesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SigObjetoCustoClientes> {

	private static final long serialVersionUID = -9109614555360476823L;

	public List<SigObjetoCustoClientes> pesquisarObjetoCustoClientes(SigObjetoCustoVersoes objetoCustoVersao) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SigObjetoCustoClientes.class);
		
		criteria.setFetchMode(SigObjetoCustoClientes.Fields.DIRECIONADORES.toString(), FetchMode.JOIN);
		criteria.setFetchMode(SigObjetoCustoClientes.Fields.CENTRO_CUSTO.toString(), FetchMode.JOIN);
		criteria.setFetchMode(SigObjetoCustoClientes.Fields.CENTRO_PRODUCAO.toString(), FetchMode.JOIN);
		criteria.setFetchMode(SigObjetoCustoClientes.Fields.OBJETO_CUSTO_VERSAO.toString(), FetchMode.JOIN);
		
		criteria.add(Restrictions.eq(SigObjetoCustoClientes.Fields.OBJETO_CUSTO_VERSAO_SEQ.toString(), objetoCustoVersao.getSeq()));

		return this.executeCriteria(criteria);
	}

	/**
	 * SQL Busca clientes para rateio do objeto de custo
	 * Busca os clientes definidos para os objetos de custos de apoio, ativos no mês de competência do processamento. 
	 * 
	 * @author rmalvezzi
	 * @param seqProcessamentoCusto		Processamento atual.
	 * @return							todos os objetos de custo calculados e suas definições de clientes.
	 */
	public List<ClienteObjetoCustoVO> buscarClientesRateioObjetoCusto(Integer seqProcessamentoCusto) {

		StringBuilder sql = new StringBuilder(200);

		sql.append(" SELECT ");
		sql.append("  cbj." ).append( SigCalculoObjetoCusto.Fields.SEQ.toString());
		sql.append(", occ." ).append( SigObjetoCustoClientes.Fields.OBJETO_CUSTO_VERSAO_SEQ.toString());
		sql.append(", occ." ).append( SigObjetoCustoClientes.Fields.DIRECIONADORES.toString() ).append( '.' ).append( SigDirecionadores.Fields.SEQ.toString());
		sql.append(", occ." ).append( SigObjetoCustoClientes.Fields.IND_TODOS_CCT.toString());
		sql.append(", occ." ).append( SigObjetoCustoClientes.Fields.CENTRO_PRODUCAO.toString() ).append( '.' ).append( SigCentroProducao.Fields.SEQ.toString());
		sql.append(", occ." ).append( SigObjetoCustoClientes.Fields.CENTRO_CUSTO.toString() ).append( '.' ).append( FccCentroCustos.Fields.CODIGO.toString());
		sql.append(", occ." ).append( SigObjetoCustoClientes.Fields.VALOR.toString());

		sql.append(" FROM ");
		sql.append(SigCalculoObjetoCusto.class.getSimpleName() ).append( " cbj, ");
		sql.append(SigObjetoCustoClientes.class.getSimpleName() ).append( " occ ");

		sql.append(" WHERE ");
		sql.append(" cbj." ).append( SigCalculoObjetoCusto.Fields.OBJETO_CUSTO_VERSAO.toString() ).append('.' ).append( SigObjetoCustoVersoes.Fields.SEQ.toString());
		sql.append(" = occ." ).append( SigObjetoCustoClientes.Fields.OBJETO_CUSTO_VERSAO_SEQ.toString());
		sql.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.PROCESSAMENTO_CUSTOS_SEQ.toString() ).append( " = :seqProcessamentoCusto ");
		sql.append(" AND occ." ).append( SigObjetoCustoClientes.Fields.SITUACAO.toString() ).append( " = :dominioSituacao ");
		sql.append(" AND occ." ).append( SigObjetoCustoClientes.Fields.VALOR.toString() ).append( " is not null ");
		
		sql.append(" ORDER BY cbj." ).append( SigCalculoObjetoCusto.Fields.SEQ.toString());

		org.hibernate.Query createQuery = this.createHibernateQuery(sql.toString());
		createQuery.setInteger("seqProcessamentoCusto", seqProcessamentoCusto);
		createQuery.setParameter("dominioSituacao", DominioSituacao.A);

		List<Object[]> resultado = createQuery.list();
		List<ClienteObjetoCustoVO> retorno = new ArrayList<ClienteObjetoCustoVO>(resultado.size());
		for (Object[] objects : resultado) {
			retorno.add(ClienteObjetoCustoVO.create(objects));
		}
		return retorno;
	}

	public List<SigObjetoCustoClientes> listarClientesObjetoCustoVersao(SigObjetoCustoVersoes sigObjetoCustoVersoes, SigDirecionadores sigDirecionadores,
			Boolean semValor) {

		DetachedCriteria criteria = this.criarCriteriaAtivoObjetoVersaoDirecionador(sigObjetoCustoVersoes, sigDirecionadores, semValor);

		criteria.createAlias("occ." + SigObjetoCustoClientes.Fields.CENTRO_CUSTO.toString(), "fcc", JoinType.LEFT_OUTER_JOIN);
		criteria.addOrder(Order.desc("fcc." + FccCentroCustos.Fields.CODIGO));

		return this.executeCriteria(criteria);
	}

	public List<SigObjetoCustoClientes> buscaObjetoClienteVersaoAtivo(SigObjetoCustoVersoes sigObjetoCustoVersoes, SigDirecionadores sigDirecionadores,
			Boolean semValor) {
		return this.executeCriteria(this.criarCriteriaAtivoObjetoVersaoDirecionador(sigObjetoCustoVersoes, sigDirecionadores, semValor));
	}

	private DetachedCriteria criarCriteriaAtivoObjetoVersaoDirecionador(SigObjetoCustoVersoes sigObjetoCustoVersoes, SigDirecionadores sigDirecionadores,
			Boolean semValor) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigObjetoCustoClientes.class, "occ");
		
		criteria.setFetchMode(SigObjetoCustoClientes.Fields.CENTRO_CUSTO.toString(), FetchMode.JOIN);
		criteria.setFetchMode(SigObjetoCustoClientes.Fields.CENTRO_PRODUCAO.toString(), FetchMode.JOIN);

		criteria.add(Restrictions.eq(SigObjetoCustoClientes.Fields.OBJETO_CUSTO_VERSAO.toString(), sigObjetoCustoVersoes));
		criteria.add(Restrictions.eq(SigObjetoCustoClientes.Fields.DIRECIONADORES.toString(), sigDirecionadores));
		criteria.add(Restrictions.eq(SigObjetoCustoClientes.Fields.SITUACAO.toString(), DominioSituacao.A));
		if (semValor != null && semValor.booleanValue()) {
			criteria.add(Restrictions.isNull(SigObjetoCustoClientes.Fields.VALOR.toString()));
		}

		return criteria;
	}

	public SigObjetoCustoClientes validaIndicacaoTodosCC(Integer ocvSeq, Integer dirSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigObjetoCustoClientes.class, "occ");
		criteria.createAlias("occ."+SigObjetoCustoClientes.Fields.DIRECIONADORES.toString(), "dir", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("occ."+SigObjetoCustoClientes.Fields.OBJETO_CUSTO_VERSAO_SEQ.toString(), ocvSeq));
		criteria.add(Restrictions.eq("dir."+SigDirecionadores.Fields.SEQ.toString(), dirSeq));
		criteria.add(Restrictions.eq(SigObjetoCustoClientes.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(SigObjetoCustoClientes.Fields.IND_TODOS_CCT.toString(), Boolean.TRUE));
		return (SigObjetoCustoClientes)this.executeCriteriaUniqueResult(criteria);

	}

	/**
	 * Busca os clientes do objeto de custo por um direcionador
	 * 
	 * @param direcionador 
	 * @param servidor
	 * @return List<{@link SigObjetoCustoClientes}> -  lista de clientes 
	 * @author jgugel
	 */
	public List<SigObjetoCustoClientes> buscarClientesAtivosPorDirecionador(SigDirecionadores direcionador) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigObjetoCustoClientes.class, "occ");
		criteria.setFetchMode(SigObjetoCustoClientes.Fields.CENTRO_CUSTO.toString(), FetchMode.JOIN);
		criteria.setFetchMode(SigObjetoCustoClientes.Fields.CENTRO_PRODUCAO.toString(), FetchMode.JOIN);
		criteria.add(Restrictions.or(Restrictions.isNull(SigObjetoCustoClientes.Fields.IND_TODOS_CCT.toString()),
				Restrictions.eq(SigObjetoCustoClientes.Fields.IND_TODOS_CCT.toString(), Boolean.FALSE)));
		criteria.add(Restrictions.eq(SigObjetoCustoClientes.Fields.DIRECIONADORES.toString(), direcionador));
		criteria.add(Restrictions.eq(SigObjetoCustoClientes.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.addOrder(Order.desc(SigObjetoCustoClientes.Fields.CENTRO_CUSTO.toString()));

		return this.executeCriteria(criteria);
	}
	
	/**
	 * Busca todos os clientes ativos dos direcionadores informados, que possuem indicadores de todos os centros de custo como cliente
	 * 
	 * @author rogeriovieira
	 * @param direcionadores
	 * @return
	 */
	public List<SigObjetoCustoClientes> buscarClientesAtivosComIndicadorTodosCentrosCustoPorDirecionadores(SigDirecionadores...direcionadores) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigObjetoCustoClientes.class, "occ");

		criteria.add(Restrictions.eq(SigObjetoCustoClientes.Fields.IND_TODOS_CCT.toString(), Boolean.TRUE));
		
		criteria.add(Restrictions.in(SigObjetoCustoClientes.Fields.DIRECIONADORES.toString(), direcionadores));
		criteria.add(Restrictions.eq(SigObjetoCustoClientes.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.addOrder(Order.asc(SigObjetoCustoClientes.Fields.OBJETO_CUSTO_VERSAO.toString()));
		criteria.addOrder(Order.asc(SigObjetoCustoClientes.Fields.CENTRO_CUSTO.toString()));
		criteria.addOrder(Order.asc(SigObjetoCustoClientes.Fields.CENTRO_PRODUCAO.toString()));

		return this.executeCriteria(criteria);
	}
}
