package br.gov.mec.aghu.sig.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.SigCalculoObjetoCusto;
import br.gov.mec.aghu.model.SigDirecionadores;
import br.gov.mec.aghu.model.SigObjetoCustoDirRateios;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.sig.custos.vo.CalculoDirecionadoresVO;

public class SigObjetoCustoDirRateiosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SigObjetoCustoDirRateios> {

	private static final long serialVersionUID = -9109614555360477823L;

	public List<SigObjetoCustoDirRateios> pesquisarObjetoCustoDirRateios(SigObjetoCustoVersoes objetoCustoVersao) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SigObjetoCustoDirRateios.class);

		criteria.add(Restrictions.eq(SigObjetoCustoDirRateios.Fields.OBJETO_CUSTO_VERSAO_SEQ.toString(), objetoCustoVersao.getSeq()));

		return this.executeCriteria(criteria);
	}

	/**
	 * Busca direcionadores de rateio do objeto de custo.
	 * Busca todos os direcionadores associados aos objetos de custos de apoio, ativos no mês de competência do processamento. 
	 * 
	 * @author rmalvezzi
	 * @param seqProcessamentoCusto 	Seq do processamento atual.
	 * @return							todos os objetos de custo calculados e os seus direcionadores.
	 */
	public List<CalculoDirecionadoresVO> buscarDirecionadoresRateioObjetoCusto(Integer seqProcessamentoCusto) {
		StringBuilder sql = new StringBuilder(300);

		sql.append(" SELECT ")
		.append("  cbj." ).append( SigCalculoObjetoCusto.Fields.SEQ.toString())
		.append(", odr." ).append( SigObjetoCustoDirRateios.Fields.PERCENTUAL.toString())
		.append(", odr." ).append( SigObjetoCustoDirRateios.Fields.DIRECIONADORES.toString() ).append('.' ).append( SigDirecionadores.Fields.SEQ.toString())
		.append(", cbj." ).append( SigCalculoObjetoCusto.Fields.VALOR_ATV_INSUMOS.toString()).append(" + cbj." ).append( SigCalculoObjetoCusto.Fields.VALOR_RATEIO_INSUMOS.toString())
		.append(", cbj." ).append( SigCalculoObjetoCusto.Fields.VALOR_ATV_PESSOAL.toString()).append(" + cbj." ).append( SigCalculoObjetoCusto.Fields.VALOR_RATEIO_PESSOAS.toString())
		.append(", cbj." ).append( SigCalculoObjetoCusto.Fields.VALOR_ATV_EQUIPAMENTO.toString()).append(" + cbj." ).append( SigCalculoObjetoCusto.Fields.VALOR_RATEIO_EQUIPAMENTOS.toString())
		.append(", cbj." ).append( SigCalculoObjetoCusto.Fields.VALOR_ATV_SERVICO.toString()).append(" + cbj." ).append( SigCalculoObjetoCusto.Fields.VALOR_RATEIO_SERVICO.toString())

		.append(" FROM ")
		.append(SigCalculoObjetoCusto.class.getSimpleName() ).append( " cbj, ")
		.append(SigObjetoCustoDirRateios.class.getSimpleName() ).append( " odr ")

		.append(" WHERE ")
		.append(" cbj." ).append( SigCalculoObjetoCusto.Fields.OBJETO_CUSTO_VERSAO.toString() ).append('.' ).append( SigObjetoCustoVersoes.Fields.SEQ.toString())
		.append(" = odr." ).append( SigObjetoCustoDirRateios.Fields.OBJETO_CUSTO_VERSAO_SEQ.toString())
		.append(" AND cbj." ).append( SigCalculoObjetoCusto.Fields.PROCESSAMENTO_CUSTOS_SEQ.toString() ).append( " = :seqProcessamentoCusto")
		.append(" AND odr." ).append( SigObjetoCustoDirRateios.Fields.SITUACAO.toString() ).append( " = :dominioSituacao ")

		.append(" ORDER BY cbj." ).append( SigCalculoObjetoCusto.Fields.SEQ.toString());

		org.hibernate.Query createQuery = this.createHibernateQuery(sql.toString());
		createQuery.setInteger("seqProcessamentoCusto", seqProcessamentoCusto);
		createQuery.setParameter("dominioSituacao", DominioSituacao.A);
		
		
		List<Object[]> resultado = createQuery.list();
		List<CalculoDirecionadoresVO> retorno = new ArrayList<CalculoDirecionadoresVO>(resultado.size());
		for (Object[] objects : resultado) {
			retorno.add(CalculoDirecionadoresVO.create(objects));
		}

		return retorno;
	}

	public List<SigObjetoCustoDirRateios> pesquisarDirecionadoresPorObjetoCustoVersao(Integer seqObjetoCustoVersao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigObjetoCustoDirRateios.class);
		criteria.setFetchMode(SigObjetoCustoDirRateios.Fields.DIRECIONADORES.toString(), FetchMode.JOIN);
		criteria.add(Restrictions.eq(SigObjetoCustoDirRateios.Fields.OBJETO_CUSTO_VERSAO_SEQ.toString(), seqObjetoCustoVersao));
		return this.executeCriteria(criteria);
	}
}
