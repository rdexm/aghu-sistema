package br.gov.mec.aghu.compras.dao;


import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoPedItensMatExpediente;
import br.gov.mec.aghu.model.ScoPedidoMatExpediente;



public class ScoPedItensMatExpedienteDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoPedItensMatExpediente>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3990448613344477315L;

	/**
	 * Método que realiza a pesquisa dos itens do pedido.
	 * C3
	 */
	public List<ScoPedItensMatExpediente> pesquisarItensPedidoByNotaFiscal(final Integer numeroNotaFiscal) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoPedItensMatExpediente.class, "imx");
		criteria.createAlias("imx." + ScoPedItensMatExpediente.Fields.PEDIDO, "pmx");
		criteria.add(Restrictions.eq("pmx." + ScoPedidoMatExpediente.Fields.NUMERO_NOTA_FISCAL.toString(), numeroNotaFiscal));		
		return executeCriteria(criteria);
	}
	
	/**
	 * Método que realiza a pesquisa dos itens do pedido.
	 * #31842 - C3
	 */
	public List<ScoPedItensMatExpediente> pesquisarItensPedidoByNotaFiscalCodigoMaterial(final Integer numeroNotaFiscal, final Integer codigoMaterial) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoPedItensMatExpediente.class, "imx");
		criteria.createAlias("imx." + ScoPedItensMatExpediente.Fields.PEDIDO, "pmx");
		criteria.add(Restrictions.eq("pmx." + ScoPedidoMatExpediente.Fields.NUMERO_NOTA_FISCAL.toString(), numeroNotaFiscal));
		criteria.add(Restrictions.eq("imx." + ScoPedItensMatExpediente.Fields.MATERIAL_CODIGO.toString(), codigoMaterial));
		return executeCriteria(criteria);
	}

	//C2
	public List<ScoPedItensMatExpediente> pesquisarItensNotaFiscalByNumeroNotaFiscal(Integer numeroNotaFiscal) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoPedItensMatExpediente.class, "imx");
		criteria.createAlias("imx." + ScoPedItensMatExpediente.Fields.MATERIAL.toString(), "m");
		criteria.createAlias("imx." + ScoPedItensMatExpediente.Fields.PEDIDO.toString(), "pmx");
		
		criteria.add(Restrictions.eq("pmx." + ScoPedidoMatExpediente.Fields.NUMERO_NOTA_FISCAL.toString(), numeroNotaFiscal));
		
		ScoPedItensMatExpediente.Fields valorUnitario = ScoPedItensMatExpediente.Fields.VALOR_UNITARIO;
		ProjectionList projectionList = Projections.projectionList()
					.add(Projections.property(ScoPedItensMatExpediente.Fields.MATERIAL.toString()), "material")					
					.add(Projections.sqlProjection("sum({alias}." + ScoPedItensMatExpediente.Fields.QUANTIDADE.toString() + ") as quantidade", new String[]{"quantidade"}, new Type[] { IntegerType.INSTANCE }))
				    .add(Projections.groupProperty("imx." + valorUnitario.toString()), valorUnitario.toString())
					.add(Projections.groupProperty("imx." + ScoPedItensMatExpediente.Fields.MATERIAL.toString()))
					.add(Projections.groupProperty("m." + ScoMaterial.Fields.NOME.toString()))
					.add(Projections.groupProperty("m." + ScoMaterial.Fields.UNIDADE_MEDIDA_CODIGO.toString()));
		
		criteria.setProjection(projectionList);
		
		criteria.setResultTransformer(Transformers.aliasToBean(ScoPedItensMatExpediente.class));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * Método que realiza a pesquisa dos itens do pedido pelo numero do pedido
	 * 
	 */
	public List<ScoPedItensMatExpediente> pesquisarItensPedidoByNumeroPedido(final Integer numeroPedido) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoPedItensMatExpediente.class, "imx");
		criteria.createAlias("imx." + ScoPedItensMatExpediente.Fields.PEDIDO, "pmx");
		criteria.add(Restrictions.eq("pmx." + ScoPedidoMatExpediente.Fields.NUMERO_PEDIDO.toString(), numeroPedido));		
		return executeCriteria(criteria);
	}
}
