package br.gov.mec.aghu.compras.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.compras.pac.vo.RelatorioApVO;
import br.gov.mec.aghu.compras.vo.ConsultarAndamentoProcessoCompraDataVO;
import br.gov.mec.aghu.compras.vo.ConsultarAndamentoProcessoCompraVO;
import br.gov.mec.aghu.model.ScoAndamentoProcessoCompra;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;
@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse"})
public class ConsultarAndamentoProcessoCompraQueryBuilder extends
		QueryBuilder<DetachedCriteria> {

	private ConsultarAndamentoProcessoCompraVO filtro;

	@Override
	protected DetachedCriteria createProduct() {
		DetachedCriteria criteria = DetachedCriteria.forClass(
				ScoAndamentoProcessoCompra.class, "APC");

		criteria.setProjection(Projections
				.projectionList()
				.add(Projections.property("APC."
						+ ScoAndamentoProcessoCompra.Fields.NUMERO_LICITACAO
								.toString()),
						ConsultarAndamentoProcessoCompraDataVO.Fields.SEQ_PAC
								.toString())
				.add(Projections.property("APC."
						+ ScoAndamentoProcessoCompra.Fields.DATA_ENTRADA
								.toString()),
						RelatorioApVO.Fields.DT_ENTRADA.toString())
				.add(Projections.property("APC."
						+ ScoAndamentoProcessoCompra.Fields.DATA_SAIDA
								.toString()),
						RelatorioApVO.Fields.DT_SAIDA.toString()));

		criteria.setResultTransformer(Transformers
				.aliasToBean(ConsultarAndamentoProcessoCompraDataVO.class));
		return criteria;
	}

	@Override
	protected void doBuild(DetachedCriteria criteria) {
		if (filtro.getNumeroPac() != null) {
			criteria.add(Restrictions.eq(
					"APC."
							+ ScoAndamentoProcessoCompra.Fields.NUMERO_LICITACAO
									.toString(), filtro.getNumeroPac()));
		}
	}

	public DetachedCriteria build(ConsultarAndamentoProcessoCompraVO filtro) {
		this.filtro = filtro;
		return super.build();
	}

}