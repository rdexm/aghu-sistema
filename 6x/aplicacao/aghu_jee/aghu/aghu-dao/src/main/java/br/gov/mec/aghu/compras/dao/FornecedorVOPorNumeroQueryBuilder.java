package br.gov.mec.aghu.compras.dao;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.compras.contaspagar.vo.FornecedorVO;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;

/**
 * Responsavel por motar a busca de sistemas por nome.<br>
 * Usando o valor de busca para filtar nos campos:<br>
 * <ol>
 * 	<li>SIGLA: MatchMode.EXACT</li>
 * 	<li>ou NOME: MatchMode.ANYWHERE</li>
 * </ol><br>
 * 
 * Classe concretas de build devem sempre ter modificador de acesso Default.<br>
 * 
 * <p>Exemplo de uso do QueryBuilder para org.hibernate.criterion.DetachedCriteria.
 * Com passagem dos filtros no proprio metodo build.</p>
 * 
 */
class FornecedorVOPorNumeroQueryBuilder extends QueryBuilder<DetachedCriteria> {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 66591985430002768L;
	private String pesquisa;
	
	@Override
	protected DetachedCriteria createProduct() {
		return DetachedCriteria.forClass(ScoFornecedor.class);
	}

	@Override
	protected void doBuild(DetachedCriteria criteria) {
		if(this.pesquisa != null && StringUtils.isNotEmpty(this.pesquisa) && CoreUtil.isNumeroInteger(this.pesquisa)){
			criteria.add(Restrictions.eq(ScoFornecedor.Fields.NUMERO.toString(), Integer.parseInt(this.pesquisa)));
		}
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(ScoFornecedor.Fields.NUMERO.toString()), FornecedorVO.Fields.NUMERO.toString())
				.add(Projections.property(ScoFornecedor.Fields.CGC.toString()), FornecedorVO.Fields.CGC.toString())
				.add(Projections.property(ScoFornecedor.Fields.CPF.toString()), FornecedorVO.Fields.CPF.toString())
				.add(Projections.property(ScoFornecedor.Fields.RAZAO_SOCIAL.toString()), FornecedorVO.Fields.RAZAO_SOCIAL.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(FornecedorVO.class));
	}
	
	public DetachedCriteria build(String pesquisa) {
		
		this.pesquisa = pesquisa;
		
		return super.build();
	}

}
