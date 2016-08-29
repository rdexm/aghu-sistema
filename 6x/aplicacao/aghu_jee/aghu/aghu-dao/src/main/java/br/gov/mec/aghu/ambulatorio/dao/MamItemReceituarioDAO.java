package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.model.MamItemReceituario;
import br.gov.mec.aghu.model.MamReceituarios;
import br.gov.mec.aghu.prescricaomedica.vo.ItemReceitaMedicaVO;

/**
 * 
 * @author rhrosa
 * 
 */

public class MamItemReceituarioDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamItemReceituario> {	

	private static final long serialVersionUID = -8758334752387436551L;

	/**
	 * Busca os itens da receita pelo seq da receita
	 * 
	 * @param rct_seq
	 * @return
	 */
	public List<MamItemReceituario> pesquisarMamItemReceituario(Long rct_seq) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MamItemReceituario.class);
		criteria.add(Restrictions.eq(
				MamItemReceituario.Fields.RCT_SEQ.toString(), rct_seq));
		criteria.addOrder(Order.asc(MamItemReceituario.Fields.ORDEM.toString()));
		return executeCriteria(criteria);
	}

	/**
	 * Busca o proximo Seqp de uma Receita (rct_seq)
	 * 
	 * @param rct_seq
	 * @return
	 */

	public Short buscarProximoSeqp(Long rct_seq) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MamItemReceituario.class);

		criteria.setProjection(Projections.max(MamItemReceituario.Fields.SEQP
				.toString()));

		criteria.add(Restrictions.eq(
				MamItemReceituario.Fields.RCT_SEQ.toString(), rct_seq));

		Short sequencia = (Short) executeCriteriaUniqueResult(criteria);
		sequencia = (sequencia == null) ? (short) 0 : sequencia;

		return sequencia;
	}


	/**
	 * Obter os grupos de impressão e validade em meses configurados nos itens
	 * de receita
	 * 
	 * @param receituarios
	 * @return
	 */
	public List<Object[]> buscarConfiguracaoImpressaoItensReceituario(
			MamReceituarios receituarios) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MamItemReceituario.class);

		criteria.setProjection(Projections.distinct(Projections
				.projectionList()
				.add(Projections
						.property(MamItemReceituario.Fields.NRO_GRUPO_IMPRESSAO
								.toString()))
				.add(Projections
						.property(MamItemReceituario.Fields.VALIDADE_MESES
								.toString()))));

		criteria.add(Restrictions.eq(
				MamItemReceituario.Fields.RECEITUARIO.toString(),
				receituarios));
		criteria.addOrder(Order
				.asc(MamItemReceituario.Fields.NRO_GRUPO_IMPRESSAO.toString()));

		return executeCriteria(criteria);
	}

	/**
	 * Carregar o VO com os itens da receita que serão impressos no relatório
	 * 
	 * @param receituario
	 * @param grupoImpressao
	 * @param validadeMeses
	 * @return
	 */
	public List<ItemReceitaMedicaVO> obterItemReceituarioPorImpressaoValidade(
			MamReceituarios receituario, Byte grupoImpressao, Byte validadeMeses) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MamItemReceituario.class);

		criteria.setProjection(Projections
				.projectionList()
				.add(Projections.property(MamItemReceituario.Fields.DESCRICAO
						.toString()),
						ItemReceitaMedicaVO.Fields.MEDICAMENTO.toString())
				.add(Projections.property(MamItemReceituario.Fields.QUANTIDADE
						.toString()),
						ItemReceitaMedicaVO.Fields.QUANTIDADE.toString())
				.add(Projections.property(MamItemReceituario.Fields.FORMA_USO
						.toString()),
						ItemReceitaMedicaVO.Fields.FORMA_USO.toString())
				.add(Projections
						.property(MamItemReceituario.Fields.IND_USO_CONTINUO
								.toString()),
						ItemReceitaMedicaVO.Fields.USO_CONTINUO.toString())
				.add(Projections.property(MamItemReceituario.Fields.IND_INTERNO
						.toString()),
						ItemReceitaMedicaVO.Fields.USO_INTERNO.toString()));

		criteria.add(Restrictions.eq(
				MamItemReceituario.Fields.RECEITUARIO.toString(),
				receituario));

		if (grupoImpressao == null) {
			criteria.add(Restrictions
					.isNull(MamItemReceituario.Fields.NRO_GRUPO_IMPRESSAO
							.toString()));
		} else {
			criteria.add(Restrictions.eq(
					MamItemReceituario.Fields.NRO_GRUPO_IMPRESSAO.toString(),
					grupoImpressao));
		}

		if (validadeMeses == null) {
			criteria.add(Restrictions
					.isNull(MamItemReceituario.Fields.VALIDADE_MESES.toString()));
		} else {
			criteria.add(Restrictions.eq(
					MamItemReceituario.Fields.VALIDADE_MESES.toString(),
					validadeMeses));
		}

		//criteria.addOrder(Order.asc(MamItemReceituario.Fields.ORDEM.toString()));
		criteria.addOrder(Order.asc(MamItemReceituario.Fields.IND_USO_CONTINUO.toString()));
		criteria.addOrder(Order.asc(MamItemReceituario.Fields.IND_INTERNO.toString()));
		criteria.addOrder(Order.asc(MamItemReceituario.Fields.SEQP.toString()));
		criteria.addOrder(Order.asc(MamItemReceituario.Fields.DESCRICAO
				.toString()));

		criteria.setResultTransformer(Transformers
				.aliasToBean(ItemReceitaMedicaVO.class));

		return executeCriteria(criteria);
	}
	
	/**
	 * Busca os itens da receita pelo seq da receita e ordena pelo seqp
	 * 
	 * @param rct_seq
	 * @return
	 */
	public List<MamItemReceituario> obterMamItemReceituarioOrdenadoSeqp(Long rct_seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamItemReceituario.class);
		criteria.add(Restrictions.eq(MamItemReceituario.Fields.RCT_SEQ.toString(), rct_seq));
		criteria.addOrder(Order.asc(MamItemReceituario.Fields.SEQP.toString()));
		return executeCriteria(criteria);
	}
	
	public Short obterValorMaxSeqP(Long rctSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamItemReceituario.class);
		criteria.setProjection(Projections.max(MamItemReceituario.Fields.SEQP.toString()));
		criteria.add(Restrictions.eq(MamItemReceituario.Fields.RCT_SEQ.toString(), rctSeq));
		return (Short) executeCriteriaUniqueResult(criteria);
	}
}
