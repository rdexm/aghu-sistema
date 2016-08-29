package br.gov.mec.aghu.sig.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SigCalculoDirecionador;
import br.gov.mec.aghu.model.SigCalculoObjetoCusto;
import br.gov.mec.aghu.model.SigDirecionadores;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.sig.custos.vo.CustoIndiretoRatearObjetoCustoApoioVO;
import br.gov.mec.aghu.sig.custos.vo.ValoresIndiretosCustoDiretoVO;

public class SigCalculoDirecionadorDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SigCalculoDirecionador> {

	private static final long serialVersionUID = -3423892385679067L;

	/**
	 * Busca custos indiretos dos objetos de custo de apoio a ratear.
	 * Busca todos os objetos de custos de apoio com custos indiretos a serem rateados, por direcionador, no mês de competência do processamento. 
	 * 
	 * @author rmalvezzi
	 * @param seqProcessamento		Codigo do processamento atual.
	 * @return						Retorna todos os objetos de custo de apoio calculados com custos a ratear para clientes intermediários ou finalísticos.
	 */
	public List<CustoIndiretoRatearObjetoCustoApoioVO> buscarCustosIndiretosRatearObjetosCustoApoio(Integer seqProcessamento) {
		StringBuilder sql = new StringBuilder(200);
		sql.append("SELECT ");
		sql.append("cbj." ).append( SigCalculoObjetoCusto.Fields.SEQ ).append( ", ");
		sql.append("cbj." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO ).append( '.' ).append( FccCentroCustos.Fields.CODIGO ).append( ", ");
		sql.append("cdi." ).append( SigCalculoDirecionador.Fields.DIRECIONADOR ).append( '.' ).append( SigDirecionadores.Fields.SEQ ).append( ", ");
		sql.append("cdi." ).append( SigCalculoDirecionador.Fields.VLR_IND_INSUMOS ).append( ", ");
		sql.append("cdi." ).append( SigCalculoDirecionador.Fields.VLR_IND_PESSOAS ).append( ", ");
		sql.append("cdi." ).append( SigCalculoDirecionador.Fields.VLR_IND_EQUIPAMENTOS ).append( ", ");
		sql.append("cdi." ).append( SigCalculoDirecionador.Fields.VLR_IND_SERVICOS ).append( ' ');
		sql.append("FROM ");
		sql.append(SigCalculoObjetoCusto.class.getSimpleName() ).append( " cbj, ");
		sql.append(SigCalculoDirecionador.class.getSimpleName() ).append( " cdi ");
		sql.append("WHERE ");
		sql.append("cdi." ).append( SigCalculoDirecionador.Fields.CALCULO_OBJETO_CUSTO ).append( '.' ).append( SigCalculoObjetoCusto.Fields.SEQ ).append( " = cbj."
				).append( SigCalculoObjetoCusto.Fields.SEQ ).append( ' ');
		sql.append("and cbj." ).append( SigCalculoObjetoCusto.Fields.PROCESSAMENTO_CUSTOS_SEQ ).append( " = :seqProcessamento ");
		sql.append("and ( ");
		sql.append("(cbj." ).append( SigCalculoObjetoCusto.Fields.VALOR_IND_INSUMOS ).append( " > 0) or ");
		sql.append("(cbj." ).append( SigCalculoObjetoCusto.Fields.VALOR_IND_PESSOAS ).append( " > 0) or ");
		sql.append("(cbj." ).append( SigCalculoObjetoCusto.Fields.VALOR_IND_EQUIPAMENTOS ).append( " > 0) or ");
		sql.append("(cbj." ).append( SigCalculoObjetoCusto.Fields.VALOR_IND_SERVICOS ).append( " > 0) ");
		sql.append(" ) ");
		sql.append("ORDER BY ");
		sql.append("cbj." ).append( SigCalculoObjetoCusto.Fields.SEQ ).append( ", ");
		sql.append("cbj." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO ).append( '.' ).append( FccCentroCustos.Fields.CODIGO ).append(' ');
		org.hibernate.Query createQuery = this.createHibernateQuery(sql.toString());
		createQuery.setParameter("seqProcessamento", seqProcessamento);
		List<Object[]> resultado = createQuery.list();
		List<CustoIndiretoRatearObjetoCustoApoioVO> retorno = new ArrayList<CustoIndiretoRatearObjetoCustoApoioVO>(resultado.size());
		for (Object[] objects : resultado) {
			retorno.add(CustoIndiretoRatearObjetoCustoApoioVO.create(objects));
		}
		return retorno;
	}

	/**
	 * Busca custos diretos dos objetos de custo de apoio a ratear.
	 * Busca todos os objetos de custos de apoio com custos diretos a serem rateados, por direcionador, no mês de competência do processamento.
	 *  
	 * @author rmalvezzi
	 * @param seqProcessamento			Código do processamento atual.
	 * @return							Retorna todos os objetos de custo de apoio calculados com custos diretos a ratear.
	 */
	public List<ValoresIndiretosCustoDiretoVO> buscarCustosDiretosDosObjetosCustoApoioRatear(Integer seqProcessamento) {
		StringBuilder sql = new StringBuilder(300);
		sql.append("SELECT ");
		sql.append("cbj." ).append( SigCalculoObjetoCusto.Fields.SEQ ).append( ", ");
		sql.append("cdi." ).append( SigCalculoDirecionador.Fields.DIRECIONADOR ).append( '.' ).append( SigDirecionadores.Fields.SEQ ).append( ", ");
		sql.append("cbj." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO ).append( '.' ).append( FccCentroCustos.Fields.CODIGO ).append( ", ");
		sql.append("cdi." ).append( SigCalculoDirecionador.Fields.VLR_DIR_INSUMOS ).append( ", ");
		sql.append("cdi." ).append( SigCalculoDirecionador.Fields.VLR_DIR_PESSOAS ).append( ", ");
		sql.append("cdi." ).append( SigCalculoDirecionador.Fields.VLR_DIR_EQUIPAMENTOS ).append( ", ");
		sql.append("cdi." ).append( SigCalculoDirecionador.Fields.VLR_DIR_SERVICOS ).append( ' ');
		sql.append("FROM ");
		sql.append(SigCalculoObjetoCusto.class.getSimpleName() ).append( " cbj, ");
		sql.append(SigCalculoDirecionador.class.getSimpleName() ).append( " cdi ");
		sql.append("WHERE ");
		sql.append("cdi." ).append( SigCalculoDirecionador.Fields.CALCULO_OBJETO_CUSTO ).append( '.' ).append( SigCalculoObjetoCusto.Fields.SEQ ).append( " = cbj."
				).append( SigCalculoObjetoCusto.Fields.SEQ ).append( ' ');
		sql.append("and cbj." ).append( SigCalculoObjetoCusto.Fields.PROCESSAMENTO_CUSTOS_SEQ.toString() ).append( " = :seqProcessamento ");
		sql.append("and ( ");
		sql.append("(cbj." ).append( SigCalculoObjetoCusto.Fields.VALOR_ATV_INSUMOS ).append( " + cbj." ).append( SigCalculoObjetoCusto.Fields.VALOR_RATEIO_INSUMOS ).append( " > 0) or ");
		sql.append("(cbj." ).append( SigCalculoObjetoCusto.Fields.VALOR_ATV_PESSOAL ).append( " + cbj." ).append( SigCalculoObjetoCusto.Fields.VALOR_RATEIO_PESSOAS ).append( " > 0) or ");
		sql.append("(cbj." ).append( SigCalculoObjetoCusto.Fields.VALOR_ATV_EQUIPAMENTO ).append( " + cbj." ).append( SigCalculoObjetoCusto.Fields.VALOR_RATEIO_EQUIPAMENTOS
				).append( " > 0) or ");
		sql.append("(cbj." ).append( SigCalculoObjetoCusto.Fields.VALOR_ATV_SERVICO ).append( " + cbj." ).append( SigCalculoObjetoCusto.Fields.VALOR_RATEIO_SERVICO ).append( " > 0) ");
		sql.append(" ) ");
		sql.append("ORDER BY ");
		sql.append("cbj." ).append( SigCalculoObjetoCusto.Fields.SEQ ).append( ", ");
		sql.append("cbj." ).append( SigCalculoObjetoCusto.Fields.CENTRO_CUSTO ).append( '.' ).append( FccCentroCustos.Fields.CODIGO ).append( ' ');

		org.hibernate.Query createQuery = this.createHibernateQuery(sql.toString());
		createQuery.setParameter("seqProcessamento", seqProcessamento);

		List<Object[]> resultado = createQuery.list();
		List<ValoresIndiretosCustoDiretoVO> retorno = new ArrayList<ValoresIndiretosCustoDiretoVO>(resultado.size());
		for (Object[] objects : resultado) {
			retorno.add(ValoresIndiretosCustoDiretoVO.create(objects));
		}
		return retorno;
	}

	/**
	 * Busca valores indiretos dos objetos de custo de apoio.
	 * Busca retorna todos os objetos de custo de apoio com custos indiretos, ativos no mês de competência do processamento. 
	 * 
	 * @author rmalvezzi
	 * @param processamentoCusto		Processamento Atual.
	 * @return							Retorna todos os objetos de custo calculados e os seus direcionadores.
	 */
	public List<SigCalculoDirecionador> buscarValoresIndiretosObjetoCustoApoio(SigProcessamentoCusto processamentoCusto) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoDirecionador.class, "cdi");
		criteria.createCriteria("cdi." + SigCalculoDirecionador.Fields.CALCULO_OBJETO_CUSTO, "cbj", JoinType.INNER_JOIN);

		criteria.add(Restrictions.eq("cbj." + SigCalculoObjetoCusto.Fields.PROCESSAMENTO_CUSTOS.toString(), processamentoCusto));
		criteria.addOrder(Order.asc("cbj." + SigCalculoObjetoCusto.Fields.SEQ.toString()));

		return executeCriteria(criteria);
	}

	/**
	 * Remove os cálculos dos direcionadores ligados a um determinado processamento
	 * 
	 * @author rogeriovieira
	 * @param idProcessamentoCusto identificador do processamento.
	 */
	public void removerPorProcessamento(Integer idProcessamentoCusto) {
		Query query = this.createQuery(
				"DELETE " + SigCalculoDirecionador.class.getSimpleName() + " c where c." + SigCalculoDirecionador.Fields.CALCULO_OBJETO_CUSTO + '.'
						+ SigCalculoObjetoCusto.Fields.SEQ + " IN " + "(SELECT C.seq FROM " + SigCalculoObjetoCusto.class.getSimpleName() + " c where c."
						+ SigCalculoObjetoCusto.Fields.PROCESSAMENTO_CUSTOS + '.' + SigProcessamentoCusto.Fields.SEQ + " = :pSeq)");
		query.setParameter("pSeq", idProcessamentoCusto);
		query.executeUpdate();
	}
}