package br.gov.mec.aghu.sig.dao;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Query;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SigCalculoCliente;
import br.gov.mec.aghu.model.SigCalculoObjetoCusto;
import br.gov.mec.aghu.model.SigDirecionadores;

public class SigCalculoClienteDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SigCalculoCliente> {

	private static final String CCL = "ccl.";
	private static final long serialVersionUID = 7903453458349087L;

	/**
	 * Busca soma dos pesos dos clientes do objeto de custo
	 * 
	 * @author rmalvezzi
	 * @param sigCalculoObjetoCusto		Filtro de Calculo Objeto Custo.
	 * @param direcionador				Filtro de Direcionador.
	 * @return							Soma dos pesos de todos os clientes.
	 */
	public BigDecimal buscarSomaPesosClienesObjetoCusto(SigCalculoObjetoCusto sigCalculoObjetoCusto, SigDirecionadores direcionador) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoCliente.class);

		criteria.setProjection(Projections.sum(SigCalculoCliente.Fields.VALOR.toString()));

		criteria.add(Restrictions.eq(SigCalculoCliente.Fields.CALCULO_OBJETO_CUSTO.toString(), sigCalculoObjetoCusto));
		criteria.add(Restrictions.eq(SigCalculoCliente.Fields.DIRECIONADOR.toString(), direcionador));

		return (BigDecimal) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * SQL Busca os clientes do objeto de custo de apoio a ratear
	 * Busca os clientes para os quais o custo direto daquele objeto de custo de apoio calculado deve ser rateado.
	 * 
	 * @author rmalvezzi
	 * @param sigCalculoObjetoCusto		Filtro de Calculo Objeto Custo.
	 * @param direcionador				Filtro de Direcionador.
	 * @return							Retorna uma lista com os clientes de cada direcionador de rateio do objeto de custo (filtros).
	 */
	public List<SigCalculoCliente> buscarClientesObjetoCustoApoioRatear(SigCalculoObjetoCusto sigCalculoObjetoCusto, SigDirecionadores direcionador) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoCliente.class);

		criteria.add(Restrictions.eq(SigCalculoCliente.Fields.CALCULO_OBJETO_CUSTO+"."+SigCalculoObjetoCusto.Fields.SEQ, sigCalculoObjetoCusto.getSeq()));
		criteria.add(Restrictions.eq(SigCalculoCliente.Fields.DIRECIONADOR+"."+SigDirecionadores.Fields.SEQ, direcionador.getSeq()));

		criteria.addOrder(Order.asc(SigCalculoCliente.Fields.CALCULO_OBJETO_CUSTO+"."+SigCalculoObjetoCusto.Fields.SEQ));

		return executeCriteria(criteria);
	}

	/**
	 * Busca objetos de custo de apoio e pesos para rateio.
	 * Busca os objetos de custo de apoio para os quais o custo indireto, recebido de outras áreas, deve ser rateado.
	 * 
	 * @author rmalvezzi
	 * @param calculoObjetoCusto	Filtro pelo Calculo Objeto Custo.
	 * @param direcionador			Filtro pelo Direcionador.
	 * @return						Retorna objetos de custo de apoio a receber os custos indiretos. 
	 */
	public List<SigCalculoCliente> buscarClientesIntermediariosFinalisticosObjetoCustoApoio(SigCalculoObjetoCusto calculoObjetoCusto,
			SigDirecionadores direcionador) {
		DetachedCriteria criteria = this.criarCriteriaClientesIntermediariosFinalisticosObjetoCustoApoio(calculoObjetoCusto, direcionador);
		criteria.addOrder(Order.asc(CCL + SigCalculoCliente.Fields.CALCULO_OBJETO_CUSTO));
		return executeCriteria(criteria, true);
	}

	/**
	 * Cria o Criteria generico para os clientes intermediarios finalisticos de OC
	 * . 
	 * @author rmalvezzi
	 * @param calculoObjetoCusto	Filtro pelo Calculo Objeto Custo.
	 * @param direcionador			Filtro pelo Direcionador.
	 * @return						Criteria default.
	 */
	private DetachedCriteria criarCriteriaClientesIntermediariosFinalisticosObjetoCustoApoio(SigCalculoObjetoCusto calculoObjetoCusto,
			SigDirecionadores direcionador) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoCliente.class, "ccl");

		criteria.createAlias(CCL + SigCalculoCliente.Fields.CENTRO_CUSTO, "cct", JoinType.INNER_JOIN);
		criteria.createAlias("cct." + FccCentroCustos.Fields.CENTRO_PRODUCAO, "cto", JoinType.INNER_JOIN);

		criteria.add(Restrictions.eq(CCL + SigCalculoCliente.Fields.CALCULO_OBJETO_CUSTO, calculoObjetoCusto));
		criteria.add(Restrictions.eq(CCL + SigCalculoCliente.Fields.DIRECIONADOR, direcionador));

		//TODO Ainda não está bem certo se deve repassar somente para os os intermediários e finalísticos 
		//criteria.add(Restrictions.in("cto." + SigCentroProducao.Fields.IND_TIPO, new Object[] { DominioTipoCentroProducaoCustos.I, DominioTipoCentroProducaoCustos.F }));

		return criteria;
	}

	/**
	 * SQL Busca soma dos pesos dos objetos de custo do centro de custo.
	 * 
	 * @author rmalvezzi
	 * @param calculoObjetoCusto	Filtro pelo Calculo Objeto Custo.
	 * @param direcionador			Filtro pelo Direcionador.
	 * @return						Retorna a soma dos pesos de todos os objetos de custo de apoio.
	 */
	public BigDecimal buscarSomaPesosClientesIntermediariosFinalisticosObjetoCustoApoio(SigCalculoObjetoCusto calculoObjetoCusto, SigDirecionadores direcionador) {
		DetachedCriteria criteria = this.criarCriteriaClientesIntermediariosFinalisticosObjetoCustoApoio(calculoObjetoCusto, direcionador);
		criteria.setProjection(Projections.sum(CCL + SigCalculoCliente.Fields.VALOR.toString()));
		return (BigDecimal) executeCriteriaUniqueResult(criteria, true);
	}

	/**
	 * Remove todos os Calculos Clientes ligados a Calculos Objetos de Custos que são de um determinado processamento.
	 * 
	 * @author rmalvezzi
	 * @param idProcessamentoCusto		Processamento Atual.
	 */
	public void removerPorProcessamento(Integer idProcessamentoCusto) {
		StringBuilder sql = new StringBuilder(72);
		sql.append(" DELETE ").append(SigCalculoCliente.class.getSimpleName().toString())
		.append(" ccl WHERE ccl.").append(SigCalculoCliente.Fields.CALCULO_OBJETO_CUSTO.toString()).append('.').append(SigCalculoObjetoCusto.Fields.SEQ.toString())
		.append(" in ( select cbj.").append(SigCalculoObjetoCusto.Fields.SEQ.toString()).append(" from ").append(SigCalculoObjetoCusto.class.getSimpleName().toString())
		.append(" cbj WHERE cbj.").append(SigCalculoObjetoCusto.Fields.PROCESSAMENTO_CUSTOS_SEQ ).append(" = :pSeq)");
		Query query = this.createQuery(sql.toString());
		query.setParameter("pSeq", idProcessamentoCusto);
		query.executeUpdate();
	}
}