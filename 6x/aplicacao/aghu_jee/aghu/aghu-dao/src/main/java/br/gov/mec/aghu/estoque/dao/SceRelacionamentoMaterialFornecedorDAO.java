/**
 * 
 */
package br.gov.mec.aghu.estoque.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BooleanType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.dominio.DominioOrigemMaterialFornecedor;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.estoque.vo.FiltroMaterialFornecedorVO;
import br.gov.mec.aghu.estoque.vo.SceRelacionamentoMaterialFornecedorVO;
import br.gov.mec.aghu.estoque.vo.SceSuggestionBoxMaterialFornecedorVO;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceRelacionamentoMaterialFornecedor;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedor;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

/**
 * 
 * @author julianosena
 *
 */
@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse"})
public class SceRelacionamentoMaterialFornecedorDAO extends BaseDao<SceRelacionamentoMaterialFornecedor> {

	private static final long serialVersionUID = 8498570131302960550L;

	/**
	 * Pesquisa material por código e nome
	 * @return
	 */
	public List<ScoMaterial> pesquisarMaterial(Object value){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMaterial.class);

		if( value instanceof String ){
			String param = (String) value;

			if(!param.isEmpty() && StringUtils.isNumeric(param)){
				Integer codigo = null;
				try {
					codigo = Integer.parseInt(param);
				} catch ( NumberFormatException e ) {
					codigo = 0;
				}
				Criterion criterion = Restrictions.eq(ScoMaterial.Fields.CODIGO.toString(), codigo);
				criteria.add(criterion);

			} else if (!param.isEmpty()) {
				Criterion criterion = Restrictions.like(ScoMaterial.Fields.NOME.toString(), param, MatchMode.ANYWHERE);
				criteria.add(criterion);				
			}
		}

		return this.executeCriteria(criteria, 0, 100, null, true);
	}

	/**
	 * Pesquisa material de acordo com a consulta gerada
	 * @return
	 */
	public Long pesquisarMaterialCount(Object value){
		Long count = (long) this.pesquisarMaterial(value).size();
		return count;
	}

	/**
	 * Recupera a listagem de VO de SceRelacionamentoMaterialFornecedorVO
	 * para listagem da tela de pesquisa
	 * 
	 * @author julianosena
	 * @param sceRelacionamentoMaterialFornecedor
	 * @return List<SceRelacionamentoMaterialFornecedorVO>
	 */
	public List<SceRelacionamentoMaterialFornecedorVO> pesquisarListagemPaginadaMaterialFornecedor(FiltroMaterialFornecedorVO filtroMaterialFornecedorVO){
		DetachedCriteria criteria = getCriteriaListagemPaginadaMaterialFornecedor(filtroMaterialFornecedorVO);
		
		return this.executeCriteria(
				criteria,
				filtroMaterialFornecedorVO.getPaginacao().getFirstResult(),
				filtroMaterialFornecedorVO.getPaginacao().getMaxResult(),
				filtroMaterialFornecedorVO.getPaginacao().getOrderProperty(),
				filtroMaterialFornecedorVO.getPaginacao().isAsc()
		);
	}
	
	/**
	 * Recupera o criteria da busca da listagem de pesquisa paginada
	 * 
	 * @param filtroMaterialFornecedorVO
	 * @return
	 */
	private DetachedCriteria getCriteriaListagemPaginadaMaterialFornecedor(FiltroMaterialFornecedorVO filtroMaterialFornecedorVO){
		DetachedCriteria criteria = null;

		if( filtroMaterialFornecedorVO != null ){
			criteria = DetachedCriteria.forClass(this.getClazz(), "SRMF");
			//Instanciando a lista de projections
			ProjectionList projectionList = Projections.projectionList();
				//Select
				projectionList.add(Projections.property("SRMF." + SceRelacionamentoMaterialFornecedor.Fields.SEQ), SceRelacionamentoMaterialFornecedorVO.Fields.SEQ.getValue());
				projectionList.add(Projections.property("MAT."	+ ScoMaterial.Fields.CODIGO), SceRelacionamentoMaterialFornecedorVO.Fields.CODIGO_MATERIAL.getValue());
				projectionList.add(Projections.property("MAT."	+ ScoMaterial.Fields.NOME), SceRelacionamentoMaterialFornecedorVO.Fields.NOME_MATERIAL.getValue());
				projectionList.add(Projections.property("SRMF." + SceRelacionamentoMaterialFornecedor.Fields.CODIGO_MATERIAL_FORNECEDOR), SceRelacionamentoMaterialFornecedorVO.Fields.CODIGO_MATERIAL_FORNECEDOR.getValue());
				projectionList.add(Projections.property("SRMF." + SceRelacionamentoMaterialFornecedor.Fields.DESCRICAO_MATERIAL_FORNECEDOR), SceRelacionamentoMaterialFornecedorVO.Fields.DESCRICAO_MATERIAL_FORNECEDOR.getValue());
				projectionList.add(Projections.property("FRN." 	+ ScoFornecedor.Fields.NUMERO), SceRelacionamentoMaterialFornecedorVO.Fields.NUMERO_FORNECEDOR.getValue());
				projectionList.add(Projections.property("FRN." 	+ ScoFornecedor.Fields.RAZAO_SOCIAL), SceRelacionamentoMaterialFornecedorVO.Fields.RAZAO_SOCIAL_FORNECEDOR.getValue());
				projectionList.add(Projections.property("FRN." 	+ ScoFornecedor.Fields.CGC), SceRelacionamentoMaterialFornecedorVO.Fields.CGC_FORNECEDOR.getValue());
				projectionList.add(Projections.property("FRN." 	+ ScoFornecedor.Fields.CPF), SceRelacionamentoMaterialFornecedorVO.Fields.CPF_FORNECEDOR.getValue());
				projectionList.add(Projections.property("SRMF." + SceRelacionamentoMaterialFornecedor.Fields.SITUACAO), SceRelacionamentoMaterialFornecedorVO.Fields.SITUACAO.getValue());
				projectionList.add(Projections.property("SRMF." + SceRelacionamentoMaterialFornecedor.Fields.DATA_CRIACAO), SceRelacionamentoMaterialFornecedorVO.Fields.DATA_CRIACAO.getValue());
				projectionList.add(Projections.property("PES." 	+  RapPessoasFisicas.Fields.NOME), SceRelacionamentoMaterialFornecedorVO.Fields.CRIADO_POR.getValue());
				projectionList.add(Projections.property("SRMF." + SceRelacionamentoMaterialFornecedor.Fields.ORIGEM), SceRelacionamentoMaterialFornecedorVO.Fields.ORIGEM.getValue());
				projectionList.add(Projections.property("SRMF." + SceRelacionamentoMaterialFornecedor.Fields.DATA_ALTERACAO), SceRelacionamentoMaterialFornecedorVO.Fields.DATA_ALTERACAO.getValue());
				projectionList.add(Projections.property("PES2." +  RapPessoasFisicas.Fields.NOME), SceRelacionamentoMaterialFornecedorVO.Fields.ALTERADO_POR.getValue());
				//Joins
				criteria.createAlias("SRMF." + SceRelacionamentoMaterialFornecedor.Fields.MATERIAL, "MAT", JoinType.INNER_JOIN);
				criteria.createAlias("SRMF." + SceRelacionamentoMaterialFornecedor.Fields.FORNECEDOR, "FRN", JoinType.INNER_JOIN);
				criteria.createAlias("SRMF." + SceRelacionamentoMaterialFornecedor.Fields.RAP_SERVIDORES, "SER", JoinType.LEFT_OUTER_JOIN);
				criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA, "PES", JoinType.LEFT_OUTER_JOIN);
				criteria.createAlias("SRMF." + SceRelacionamentoMaterialFornecedor.Fields.RAP_SERVIDORES_ALTERACAO, "SER2", JoinType.LEFT_OUTER_JOIN);
				criteria.createAlias("SER2." + RapServidores.Fields.PESSOA_FISICA, "PES2", JoinType.LEFT_OUTER_JOIN);
	
				if( filtroMaterialFornecedorVO.getMaterial() != null ){
					int codigoMaterial = filtroMaterialFornecedorVO.getMaterial().getCodigo();
					Criterion criterion = Restrictions.eq("MAT." + ScoMaterial.Fields.CODIGO , codigoMaterial);
					criteria.add(criterion);
				}
				
				if( filtroMaterialFornecedorVO.getFornecedor() != null ){
					int numeroFornecedor = filtroMaterialFornecedorVO.getFornecedor().getNumero();
					Criterion criterion = Restrictions.eq("FRN." + ScoFornecedor.Fields.NUMERO , numeroFornecedor);
					criteria.add(criterion);
				}
	
				if( filtroMaterialFornecedorVO.getDescricaoMaterialFornecedor() != null ){
					String descricaoMaterialFornecedor = filtroMaterialFornecedorVO.getDescricaoMaterialFornecedor();
					Criterion criterion = Restrictions.like("SRMF." + SceRelacionamentoMaterialFornecedor.Fields.DESCRICAO_MATERIAL_FORNECEDOR ,
											descricaoMaterialFornecedor.toUpperCase(),
											MatchMode.ANYWHERE);
					criteria.add(criterion);
				}
	
				if( filtroMaterialFornecedorVO.getSituacao() != null ){
					DominioSituacao dominio = filtroMaterialFornecedorVO.getSituacao();
					Criterion criterion = Restrictions.eq("SRMF." + SceRelacionamentoMaterialFornecedor.Fields.SITUACAO, dominio);
					criteria.add(criterion);
				}
	
				if(filtroMaterialFornecedorVO.getOrigem() != null ){
					DominioOrigemMaterialFornecedor dominio = filtroMaterialFornecedorVO.getOrigem();
					Criterion criterion = Restrictions.eq("SRMF." + SceRelacionamentoMaterialFornecedor.Fields.ORIGEM, dominio);
					criteria.add(criterion);
				}
	
				criteria.setProjection(projectionList);
	
				Order order = Order.desc("MAT."	+ ScoMaterial.Fields.CODIGO);
				criteria.addOrder(order);
	
				criteria.setResultTransformer(Transformers.aliasToBean(SceRelacionamentoMaterialFornecedorVO.class));
			
		}
		return criteria;
	}

	/**
	 * Recupera o total de registros na tabela da entidade SceRelacionamentoMaterialFornecedor
	 * 
	 * @author julianosena
	 * @return Long count
	 */
	public Long pesquisarListagemPaginadaMaterialFornecedorCount(FiltroMaterialFornecedorVO filtroMaterialFornecedorVO){
		DetachedCriteria criteria = getCriteriaListagemPaginadaMaterialFornecedor(filtroMaterialFornecedorVO);
		return ((long) this.executeCriteria(criteria).size());
	}

	/**
	 * Pesquisa por material através do id do fornecedor e os parâmetros passados no campo do suggestionBox
	 * 
	 * @param numeroFornecedor
	 * @param param
	 * @return
	 */
	public List<SceSuggestionBoxMaterialFornecedorVO> pesquisarMaterialPorFornecedor(Integer numeroFornecedor, Object param){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMaterial.class, "MAT");

		//Select
		ProjectionList projectionList = Projections.projectionList();
		projectionList.add(Projections.property("SLC." + ScoSolicitacaoDeCompra.Fields.MAT_CODIGO), SceSuggestionBoxMaterialFornecedorVO.Fields.CODIGO_MATERIAL.getValue());
		projectionList.add(Projections.property("MAT." + ScoMaterial.Fields.NOME), SceSuggestionBoxMaterialFornecedorVO.Fields.NOME_MATERIAL.getValue());
		projectionList.add(Projections.property("FSC." + ScoFaseSolicitacao.Fields.LCT_NUMERO), SceSuggestionBoxMaterialFornecedorVO.Fields.NUMERO_PAC.getValue());

		String subSelectVencedor ="(select sipf.ind_escolhido " +
								      "from agh.sco_fornecedores frn " +
								"inner join agh.sco_propostas_fornecedores spf " +
									"on frn.numero = spf.frn_numero " +
								"inner join agh.sco_item_propostas_fornecedor sipf " +
									"on spf.lct_numero = sipf.pfr_lct_numero " +
								       "and spf.frn_numero = sipf.pfr_frn_numero " +
								     "where frn.numero = " + numeroFornecedor + " " +
								       "and sipf.itl_lct_numero = fsc2_.itl_lct_numero " +
								       "and sipf.itl_numero = fsc2_.itl_numero) Vencedor";
		projectionList.add(Projections.sqlProjection(subSelectVencedor, new String[]{"Vencedor"}, new Type[]{BooleanType.INSTANCE}), SceSuggestionBoxMaterialFornecedorVO.Fields.VENCEDOR.getValue());

		//Subselect da cláusula select da Query principal
		String subSelect = "(SELECT 'Sim' "
								  + "FROM AGH.SCE_REL_MAT_FORN S "
								  + "WHERE S.MAT_CODIGO = this_.CODIGO "
								  	+ "AND S.FRN_NUMERO = " + numeroFornecedor + " "
								  	+ "LIMIT 1) Vinculado" ;
		projectionList.add(Projections.sqlProjection(subSelect, new String[]{"Vinculado"}, new Type[]{StringType.INSTANCE}), SceSuggestionBoxMaterialFornecedorVO.Fields.VINCULADO.getValue());
		criteria.setProjection(projectionList);

		criteria.createAlias("MAT." + ScoMaterial.Fields.SOLICITACAO_COMPRA, "SLC", JoinType.INNER_JOIN);
		criteria.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.FASES_SOLICITACAO, "FSC", JoinType.INNER_JOIN);
		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.ITEM_LICITACAO, "ITL", JoinType.INNER_JOIN);

		//Subcriteria
		DetachedCriteria subCriteriaItemLicitacaoLctNumero = DetachedCriteria.forClass(ScoFornecedor.class, "FRN");
		ProjectionList subCriteriaItemLicitacaoLctNumeroProjectionList = Projections.projectionList();
		subCriteriaItemLicitacaoLctNumeroProjectionList.add(Projections.property("IPF." + ScoItemPropostaFornecedor.Fields.ITEM_LICITACAO_ID_LCT_NUMERO));
		subCriteriaItemLicitacaoLctNumero.createAlias("FRN." +  ScoFornecedor.Fields.PROPOSTAS, "PFRN", JoinType.INNER_JOIN);
		subCriteriaItemLicitacaoLctNumero.createAlias("PFRN." + ScoPropostaFornecedor.Fields.ITEM, "IPF", JoinType.INNER_JOIN);
		subCriteriaItemLicitacaoLctNumero.add(Restrictions.eq(ScoFornecedor.Fields.NUMERO.toString(), numeroFornecedor));
		subCriteriaItemLicitacaoLctNumero.setProjection(subCriteriaItemLicitacaoLctNumeroProjectionList);

		DetachedCriteria subCriteriaItemLicitacaoNumero = DetachedCriteria.forClass(ScoFornecedor.class, "FRN");
		ProjectionList subCriteriaItemLicitacaoNumeroProjectionList = Projections.projectionList();
		subCriteriaItemLicitacaoNumeroProjectionList.add(Projections.property("IPF." + ScoItemPropostaFornecedor.Fields.ITEM_LICITACAO + "." + ScoItemLicitacao.Fields.NUMERO));
		subCriteriaItemLicitacaoNumero.createAlias("FRN." +  ScoFornecedor.Fields.PROPOSTAS, "PFRN", JoinType.INNER_JOIN);
		subCriteriaItemLicitacaoNumero.createAlias("PFRN." + ScoPropostaFornecedor.Fields.ITEM, "IPF", JoinType.INNER_JOIN);
		subCriteriaItemLicitacaoNumero.add(Restrictions.eq(ScoFornecedor.Fields.NUMERO.toString(), numeroFornecedor));
		subCriteriaItemLicitacaoNumero.setProjection(subCriteriaItemLicitacaoNumeroProjectionList);

		//Where
		Criterion solicitacaoCompraIndEfetivadaCriterion = Restrictions.eq("SLC." + ScoSolicitacaoDeCompra.Fields.IND_EFETIVADA, false);
		Criterion solicitacaoCompraIndExclusaoCriterion = Restrictions.eq("SLC." + ScoSolicitacaoDeCompra.Fields.IND_EXCLUSAO, false);
		Criterion faseSolicitacaoIndExclusaoCriterion = Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO, false);
		Criterion subCriteriaCriterion1 = Subqueries.propertyIn("FSC." + ScoFaseSolicitacao.Fields.LCT_NUMERO, subCriteriaItemLicitacaoLctNumero);
		Criterion subCriteriaCriterion2 = Subqueries.propertyIn("FSC." + ScoFaseSolicitacao.Fields.ITL_NUMERO, subCriteriaItemLicitacaoNumero);

		//Filtro
		if( param instanceof String ){
			String value = (String) param;
			Criterion criterion = null;

			if( !value.isEmpty() && StringUtils.isNumeric(value) ){
				//Se não der para converter
				Integer i = null;
				try {
					i = Integer.parseInt(value);
				} catch ( NumberFormatException e ) {
					i = 0;
				}
				criterion = Restrictions.or(
						Restrictions.eq("SLC." + ScoSolicitacaoDeCompra.Fields.MAT_CODIGO, i),
						Restrictions.eq("FSC." + ScoItemPropostaFornecedor.Fields.ITL_LCT_NUMERO, i)
				);
			} else if( !value.isEmpty() ) {
				criterion = Restrictions.like("MAT." + ScoMaterial.Fields.NOME, value, MatchMode.ANYWHERE);
			}

			if(criterion != null){
				criteria.add(criterion);
			}
		}

		//Order
		Order order = Order.asc("MAT." + ScoMaterial.Fields.NOME);
		
		//Configurando criteria
		criteria.add(solicitacaoCompraIndEfetivadaCriterion);
		criteria.add(solicitacaoCompraIndExclusaoCriterion);
		criteria.add(faseSolicitacaoIndExclusaoCriterion);
		criteria.add(subCriteriaCriterion1);
		criteria.add(subCriteriaCriterion2);
		criteria.addOrder(order);

		criteria.setResultTransformer(Transformers.aliasToBean(SceSuggestionBoxMaterialFornecedorVO.class));

		return executeCriteria(criteria);
	}

	/**
	 * Recupera a lista de materiais por número do fornecedor
	 * 
	 * @author alejandro
	 * @param numeroFornecedor
	 * @return
	 */
	public List<SceRelacionamentoMaterialFornecedorVO> pesquisarMaterialPorFornecedor(Integer numeroFornecedor){
		DetachedCriteria criteria = DetachedCriteria.forClass(this.getClazz(), "RMF");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("RMF." + SceRelacionamentoMaterialFornecedor.Fields.SEQ), SceRelacionamentoMaterialFornecedorVO.Fields.SEQ.getValue())
				.add(Projections.property("FRN." + ScoFornecedor.Fields.NUMERO), SceRelacionamentoMaterialFornecedorVO.Fields.NUMERO_FORNECEDOR.getValue())
				.add(Projections.property("FRN." + ScoFornecedor.Fields.RAZAO_SOCIAL), SceRelacionamentoMaterialFornecedorVO.Fields.RAZAO_SOCIAL_FORNECEDOR.getValue())
				.add(Projections.property("FRN." + ScoFornecedor.Fields.CGC), SceRelacionamentoMaterialFornecedorVO.Fields.CGC_FORNECEDOR.getValue())
				.add(Projections.property("FRN." + ScoFornecedor.Fields.CPF), SceRelacionamentoMaterialFornecedorVO.Fields.CPF_FORNECEDOR.getValue())
				.add(Projections.property("RMF." + SceRelacionamentoMaterialFornecedor.Fields.MATERIAL + "." + ScoMaterial.Fields.CODIGO), SceRelacionamentoMaterialFornecedorVO.Fields.CODIGO_MATERIAL.getValue())
				.add(Projections.property("MAT." + ScoMaterial.Fields.NOME), SceRelacionamentoMaterialFornecedorVO.Fields.NOME_MATERIAL.getValue())
				.add(Projections.property("RMF." + SceRelacionamentoMaterialFornecedor.Fields.CODIGO_MATERIAL_FORNECEDOR), SceRelacionamentoMaterialFornecedorVO.Fields.CODIGO_MATERIAL_FORNECEDOR.getValue())
				.add(Projections.property("RMF." + SceRelacionamentoMaterialFornecedor.Fields.DESCRICAO_MATERIAL_FORNECEDOR), SceRelacionamentoMaterialFornecedorVO.Fields.DESCRICAO_MATERIAL_FORNECEDOR.getValue())
				.add(Projections.property("RMF." + SceRelacionamentoMaterialFornecedor.Fields.SITUACAO), SceRelacionamentoMaterialFornecedorVO.Fields.SITUACAO.getValue())
				.add(Projections.property("RMF." + SceRelacionamentoMaterialFornecedor.Fields.DATA_CRIACAO), SceRelacionamentoMaterialFornecedorVO.Fields.DATA_CRIACAO.getValue())
				.add(Projections.property("PES." + RapPessoasFisicas.Fields.NOME), SceRelacionamentoMaterialFornecedorVO.Fields.CRIADO_POR.getValue())
				.add(Projections.property("RMF." + SceRelacionamentoMaterialFornecedor.Fields.ORIGEM), SceRelacionamentoMaterialFornecedorVO.Fields.ORIGEM.getValue())
				.add(Projections.property("RMF." + SceRelacionamentoMaterialFornecedor.Fields.DATA_ALTERACAO), SceRelacionamentoMaterialFornecedorVO.Fields.DATA_ALTERACAO.getValue())
				.add(Projections.property("PES2." + RapPessoasFisicas.Fields.NOME), SceRelacionamentoMaterialFornecedorVO.Fields.ALTERADO_POR.getValue())
		);
		
		criteria.createAlias("RMF." + SceRelacionamentoMaterialFornecedor.Fields.FORNECEDOR, "FRN");
		criteria.createAlias("RMF." + SceRelacionamentoMaterialFornecedor.Fields.MATERIAL, "MAT");
		criteria.createAlias("RMF." + SceRelacionamentoMaterialFornecedor.Fields.RAP_SERVIDORES, "SER", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("RMF." + SceRelacionamentoMaterialFornecedor.Fields.RAP_SERVIDORES_ALTERACAO, "SER2", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA, "PES", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SER2." + RapServidores.Fields.PESSOA_FISICA, "PES2", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq("FRN." + ScoFornecedor.Fields.NUMERO, numeroFornecedor));
		
		criteria.setResultTransformer(Transformers.aliasToBean(SceRelacionamentoMaterialFornecedorVO.class));
		
		criteria.addOrder(Order.desc("MAT." + ScoMaterial.Fields.NOME));
		
		return executeCriteria(criteria);
	}

	/**
	 * Verifico se existe um material de acordo
	 * com o fornecedor e código do material ou descrição 
	 * 
	 * @param sceRelacionamentoMaterialFornecedor
	 * @return
	 */
	public boolean materialFornecedorExists( SceRelacionamentoMaterialFornecedor sceRelacionamentoMaterialFornecedor ){
		DetachedCriteria criteria = DetachedCriteria.forClass(this.getClazz());

		if( sceRelacionamentoMaterialFornecedor != null ){
			//Se foi informado o código do material e não a descrição
			Criterion fornecedorCriterion = Restrictions.eq(
					SceRelacionamentoMaterialFornecedor.Fields.FORNECEDOR + ".numero",
					sceRelacionamentoMaterialFornecedor.getScoFornecedor().getNumero()
			);
			//Crio o criterion para adicionar a criteria se o código do material foi informado
			String codigoMaterialFornecedor = sceRelacionamentoMaterialFornecedor.getCodigoMaterialFornecedor();
			Criterion codigoMaterialFornecedorCriterion = Restrictions.eq(SceRelacionamentoMaterialFornecedor.Fields.CODIGO_MATERIAL_FORNECEDOR.getValue(), codigoMaterialFornecedor);

			//Crio o criterion para adicionar ao objeto criteria se a descrição foi informada
			String descricaoMaterialFornecedor = sceRelacionamentoMaterialFornecedor.getDescricaoMaterialFornecedor();
			Criterion descricaoMateriaFornecedorCriterion = Restrictions.eq(SceRelacionamentoMaterialFornecedor.Fields.DESCRICAO_MATERIAL_FORNECEDOR.getValue(), descricaoMaterialFornecedor);
			
			//Se o código do material foi informado mas a descrição não
			if(codigoMaterialFornecedor != null && !codigoMaterialFornecedor.isEmpty() &&
			  (descricaoMaterialFornecedor == null || descricaoMaterialFornecedor.isEmpty())){
				criteria.add(codigoMaterialFornecedorCriterion);

			//Se só a descrição foi informada
			} else if (descricaoMaterialFornecedor != null && !descricaoMaterialFornecedor.isEmpty() &&
					  (codigoMaterialFornecedor == null ||  codigoMaterialFornecedor.isEmpty())) {
				criteria.add(descricaoMateriaFornecedorCriterion);

			//Se tanto o código ou descrição foi informado
			} else if((descricaoMaterialFornecedor != null && !descricaoMaterialFornecedor.isEmpty()) &&
					  (codigoMaterialFornecedor != null && !codigoMaterialFornecedor.isEmpty())) {
				criteria.add(Restrictions.or(codigoMaterialFornecedorCriterion, descricaoMateriaFornecedorCriterion));
			}

			criteria.add(fornecedorCriterion);
		}

		return this.executeCriteriaExists(criteria);
	}

	/**
	 * Consulta para obter o(s) Material(is) do Fornecedor associados a um Material x Fornecedor do Hospital.
	 * @param SceRelacionamentoMaterialFornecedor
	 * @return 
	 */
	public List<SceRelacionamentoMaterialFornecedor> pesquisarListaMateriaHospitalFornecedor(SceRelacionamentoMaterialFornecedor sceRelacionamentoMaterialFornecedor) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SceRelacionamentoMaterialFornecedor.class, "SRMF");
		
		//RMF.MAT_CODIGO = <Código do Material – campo: Nome do Item>
		criteria.add(Restrictions.eq(SceRelacionamentoMaterialFornecedor.Fields.MATERIAL_CODIGO.toString(), sceRelacionamentoMaterialFornecedor.getScoMaterial().getCodigo()));
		
		//and RMF.FRN_NUMERO = <Fornecedor>
		criteria.add(Restrictions.eq(SceRelacionamentoMaterialFornecedor.Fields.FORNECEDOR_NUMERO.toString(), sceRelacionamentoMaterialFornecedor.getScoFornecedor().getNumero()));
		
		//order by DT_ALTERACAO desc 
		criteria.addOrder(Order.desc(SceRelacionamentoMaterialFornecedor.Fields.DATA_ALTERACAO.toString()));
		
		return executeCriteria(criteria);
		
	}
	
	/**
	 * Pesquisar se já existe um Material do Hospital relacionado ao Material do Fornecedor.
	 * @param SceRelacionamentoMaterialFornecedor
	 * @return 
	 */
	public List<SceRelacionamentoMaterialFornecedor> verificarMateriaHospitalFornecedor(SceRelacionamentoMaterialFornecedor sceRelacionamentoMaterialFornecedor) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SceRelacionamentoMaterialFornecedor.class);
		
		//and RMF.FRN_NUMERO = <Fornecedor>
		criteria.add(Restrictions.eq(SceRelacionamentoMaterialFornecedor.Fields.FORNECEDOR_NUMERO.toString(), sceRelacionamentoMaterialFornecedor.getScoFornecedor().getNumero()));
		
		//and RMF.COD_MAT_FORN = <Código Material Fornecedor>
		criteria.add(Restrictions.eq(SceRelacionamentoMaterialFornecedor.Fields.CODIGO_MATERIAL_FORNECEDOR.toString(), sceRelacionamentoMaterialFornecedor.getCodigoMaterialFornecedor()));
		
		//and RMF.MAT_CODIGO <> <Código do Material – campo: Nome do Item>
		criteria.add(Restrictions.eq(SceRelacionamentoMaterialFornecedor.Fields.MATERIAL_CODIGO.toString(), sceRelacionamentoMaterialFornecedor.getScoMaterial().getCodigo()));
				
		return executeCriteria(criteria);
	}
	
	
	/** 
	 * Ativação do relacionamento do código do material do hospital e do fornecedor
	 * @param sceRelacionamentoMaterialFornecedor
	 */
	public void alterarMateriaHospitalFornecedor(SceRelacionamentoMaterialFornecedor sceRelacionamentoMaterialFornecedor) {
		 merge(sceRelacionamentoMaterialFornecedor);
	}
	
	
	/**
	 * Inclusão do relacionamento do código do material do hospital e do fornecedor
	 * @param sceRelacionamentoMaterialFornecedor
	 */
	public void incluirMateriaHospitalFornecedor(SceRelacionamentoMaterialFornecedor sceRelacionamentoMaterialFornecedor) {
		persistir(sceRelacionamentoMaterialFornecedor);
	}
}