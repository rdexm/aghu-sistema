/**
 * 
 */
package br.gov.mec.aghu.estoque.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceRelacionamentoMaterialFornecedorJn;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

/**
 * 
 * @author julianosena
 *
 */
@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse"})
public class SceRelacionamentoMaterialFornecedorJnDAO extends BaseDao<SceRelacionamentoMaterialFornecedorJn> {

	private static final long serialVersionUID = -8398570131302960551L;

	/**
	 * Recupera a listagem de registros no histórico de inserção de material x fornecedor
	 * 
	 * @param codigoRelacaoMaterialFornecedor
	 * @return
	 */
	public List<SceRelacionamentoMaterialFornecedorJn> pesquisarHistoricoMaterialFornecedor(Long codigoRelacaoMaterialFornecedor) {
		DetachedCriteria criteria = DetachedCriteria.forClass(this.getClazz(), "RMFJ");

		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("RMFJ." + SceRelacionamentoMaterialFornecedorJn.Fields.MATERIAL), "scoMaterial")
				.add(Projections.property("RMFJ." + SceRelacionamentoMaterialFornecedorJn.Fields.FORNECEDOR), "scoFornecedor")
				.add(Projections.property("RMFJ." + SceRelacionamentoMaterialFornecedorJn.Fields.CODIGO_MATERIAL_FORNECEDOR), "codigoMaterialFornecedor")
				.add(Projections.property("RMFJ." + SceRelacionamentoMaterialFornecedorJn.Fields.DESCRICAO_MATERIAL_FORNECEDOR), "descricaoMaterialFornecedor")
				.add(Projections.property("RMFJ." + SceRelacionamentoMaterialFornecedorJn.Fields.SITUACAO), "situacao")
				.add(Projections.property("RMFJ." + SceRelacionamentoMaterialFornecedorJn.Fields.ORIGEM), "origem")
				.add(Projections.property("SER." + RapServidores.Fields.USUARIO), "usuarioAlteracao")
				.add(Projections.property("SER." + RapServidores.Fields.MATRICULA), "serMatriculaAlteracao")
				.add(Projections.property("SER." + RapServidores.Fields.VIN_CODIGO), "serVinCodigoAlteracao")
				.add(Projections.property("RMFJ." + SceRelacionamentoMaterialFornecedorJn.Fields.DATA_ALTERACAO), "dtAlteracao")
		);
		
		criteria.createAlias("RMFJ." + SceRelacionamentoMaterialFornecedorJn.Fields.MATERIAL, "MAT", JoinType.INNER_JOIN);
		criteria.createAlias("RMFJ." + SceRelacionamentoMaterialFornecedorJn.Fields.FORNECEDOR, "FRN", JoinType.INNER_JOIN);
		criteria.createAlias("RMFJ." + SceRelacionamentoMaterialFornecedorJn.Fields.RAP_SERVIDORES, "SER", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq("RMFJ." + SceRelacionamentoMaterialFornecedorJn.Fields.SEQ, codigoRelacaoMaterialFornecedor));
		
		criteria.addOrder(Order.desc("RMFJ." + SceRelacionamentoMaterialFornecedorJn.Fields.DATA_ALTERACAO));
		
		criteria.setResultTransformer(Transformers.aliasToBean(SceRelacionamentoMaterialFornecedorJn.class));

		return executeCriteria(criteria);
	}
}
