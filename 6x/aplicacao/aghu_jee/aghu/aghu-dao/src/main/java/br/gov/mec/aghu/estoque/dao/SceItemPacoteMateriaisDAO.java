package br.gov.mec.aghu.estoque.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.estoque.vo.ItemPacoteMateriaisVO;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceItemPacoteMateriais;
import br.gov.mec.aghu.model.SceItemPacoteMateriaisId;
import br.gov.mec.aghu.model.ScePacoteMateriais;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoUnidadeMedida;

/**
 * Classe de acesso à entidade <code>br.gov.mec.aghu.model.SceItemPacoteMateriais</code>
 * 
 * @author guilherme.finotti
 *
 */
public class SceItemPacoteMateriaisDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SceItemPacoteMateriais> {
	
	private static final long serialVersionUID = -7865024502497985913L;

	/**
	 * Pesquisa ítens de pacote de materiais
	 * @param codigoCentroCustoProprietario
	 * @param codigoCentroCustoAplicacao
	 * @param numero
	 * @return
	 */
	public List<SceItemPacoteMateriais> pesquisarItensPacoteMateriais(Integer codigoCentroCustoProprietario, Integer codigoCentroCustoAplicacao, Integer numeroPacoteMateriais){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemPacoteMateriais.class, "IPM");
		
		if (codigoCentroCustoProprietario != null) {
			criteria.add(Restrictions.eq("IPM." + SceItemPacoteMateriais.Fields.CODIGO_CENTRO_CUSTO_PROPRIETARIO_PACOTE.toString(),
					codigoCentroCustoProprietario));
		}
		
		if (codigoCentroCustoAplicacao != null) {
			criteria.add(Restrictions.eq("IPM." + SceItemPacoteMateriais.Fields.CODIGO_CENTRO_CUSTO_APLICACAO_PACOTE.toString(),
					codigoCentroCustoAplicacao));
		}
		
		if (numeroPacoteMateriais != null) {
			criteria.add(Restrictions.eq("IPM." + SceItemPacoteMateriais.Fields.NUMERO_PACOTE.toString(),
					numeroPacoteMateriais));
		}
		
		return executeCriteria(criteria);
	}
	
	/**
	 * Retorna ítens de pacote de materiais como VO's
	 * @param codigoCentroCustoProprietario
	 * @param codigoCentroCustoAplicacao
	 * @param numeroPacote
	 * @return
	 */
	public List<ItemPacoteMateriaisVO> pesquisarItensPacoteMateriaisVO(Integer codigoCentroCustoProprietario, Integer codigoCentroCustoAplicacao, Integer numeroPacoteMateriais){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemPacoteMateriais.class, "IPM");
		
		criteria.createAlias("IPM." + SceItemPacoteMateriais.Fields.ESTOQUE_ALMOXARIFADO.toString(), "EAL", JoinType.INNER_JOIN);
		criteria.createAlias("EAL." + SceEstoqueAlmoxarifado.Fields.FORNECEDOR.toString(), "FRN", JoinType.INNER_JOIN);
		criteria.createAlias("EAL." + SceEstoqueAlmoxarifado.Fields.UNIDADE_MEDIDA.toString(), "UND", JoinType.INNER_JOIN);
		criteria.createAlias("EAL." + SceEstoqueAlmoxarifado.Fields.MATERIAL.toString(), "MAT", JoinType.INNER_JOIN);
		criteria.createAlias("MAT." + ScoMaterial.Fields.GRUPO_MATERIAL.toString(), "GMT", JoinType.INNER_JOIN);
		
		ProjectionList pList = Projections.projectionList();

		pList.add(Projections.property("IPM." + SceItemPacoteMateriais.Fields.QUANTIDADE.toString()), ItemPacoteMateriaisVO.Fields.QUANTIDADE.toString());
		pList.add(Projections.property("IPM." + SceItemPacoteMateriais.Fields.CODIGO_CENTRO_CUSTO_APLICACAO_PACOTE.toString()), ItemPacoteMateriaisVO.Fields.CODIGO_CENTRO_CUSTO_APLICACAO_PACOTE.toString());
		pList.add(Projections.property("IPM." + SceItemPacoteMateriais.Fields.CODIGO_CENTRO_CUSTO_PROPRIETARIO_PACOTE.toString()), ItemPacoteMateriaisVO.Fields.CODIGO_CENTRO_CUSTO_PROPRIETARIO_PACOTE.toString());
		pList.add(Projections.property("IPM." + SceItemPacoteMateriais.Fields.CODIGO_ESTOQUE.toString()), ItemPacoteMateriaisVO.Fields.SEQ_ESTOQUE.toString());
		pList.add(Projections.property("IPM." + SceItemPacoteMateriais.Fields.NUMERO_PACOTE.toString()), ItemPacoteMateriaisVO.Fields.NUMERO_PACOTE.toString());
		pList.add(Projections.property("UND." + ScoUnidadeMedida.Fields.CODIGO.toString()), ItemPacoteMateriaisVO.Fields.CODIGO_UNIDADE_MEDIDA.toString());
		pList.add(Projections.property("UND." + ScoUnidadeMedida.Fields.DESCRICAO.toString()), ItemPacoteMateriaisVO.Fields.DESCRICAO_UNIDADE_MEDIDA.toString());
		pList.add(Projections.property("FRN." + ScoFornecedor.Fields.NUMERO.toString()), ItemPacoteMateriaisVO.Fields.NUMERO_FORNECEDOR.toString());
		pList.add(Projections.property("FRN." + ScoFornecedor.Fields.NOME_FANTASIA.toString()), ItemPacoteMateriaisVO.Fields.NOME_FANTASIA_FORNECEDOR.toString());
		pList.add(Projections.property("MAT." + ScoMaterial.Fields.CODIGO.toString()), ItemPacoteMateriaisVO.Fields.CODIGO_MATERIAL.toString());
		pList.add(Projections.property("MAT." + ScoMaterial.Fields.NOME.toString()), ItemPacoteMateriaisVO.Fields.NOME_MATERIAL.toString());
		pList.add(Projections.property("GMT." + ScoGrupoMaterial.Fields.CODIGO.toString()), ItemPacoteMateriaisVO.Fields.CODIGO_GRUPO_MATERIAL.toString());
		pList.add(Projections.property("GMT." + ScoGrupoMaterial.Fields.DESCRICAO.toString()), ItemPacoteMateriaisVO.Fields.NOME_GRUPO_MTAERIAL.toString());
		
		criteria.setProjection(pList);
		
		
		if (codigoCentroCustoProprietario != null) {
			criteria.add(Restrictions.eq("IPM." + SceItemPacoteMateriais.Fields.CODIGO_CENTRO_CUSTO_PROPRIETARIO_PACOTE.toString(),
					codigoCentroCustoProprietario));
		}
		
		if (codigoCentroCustoAplicacao != null) {
			criteria.add(Restrictions.eq("IPM." + SceItemPacoteMateriais.Fields.CODIGO_CENTRO_CUSTO_APLICACAO_PACOTE.toString(),
					codigoCentroCustoAplicacao));
		}
		
		if (numeroPacoteMateriais != null) {
			criteria.add(Restrictions.eq("IPM." + SceItemPacoteMateriais.Fields.NUMERO_PACOTE.toString(),
					numeroPacoteMateriais));
		}
		
		criteria.setResultTransformer(Transformers.aliasToBean(ItemPacoteMateriaisVO.class));

		criteria.addOrder(Order.asc("MAT." + ScoMaterial.Fields.NOME.toString()));
		
		return executeCriteria(criteria);
	}
	
	
	/**
	 * Retorna ítens de pacote de materiais como VO's
	 * @param codigoCentroCustoProprietario
	 * @param codigoCentroCustoAplicacao
	 * @param numeroPacote
	 * @return
	 */
	public List<ItemPacoteMateriaisVO> pesquisarItensTrsEventualPacoteMateriaisVO(ScePacoteMateriais pacote, Short almSeq){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemPacoteMateriais.class, "IPM");
		
		criteria.createAlias("IPM." + SceItemPacoteMateriais.Fields.ESTOQUE_ALMOXARIFADO.toString(), "EAL", JoinType.INNER_JOIN);
		criteria.createAlias("EAL." + SceEstoqueAlmoxarifado.Fields.FORNECEDOR.toString(), "FRN", JoinType.INNER_JOIN);
		criteria.createAlias("EAL." + SceEstoqueAlmoxarifado.Fields.MATERIAL.toString(), "MAT", JoinType.INNER_JOIN);
		
		ProjectionList pList = Projections.projectionList();

		pList.add(Projections.property("IPM." + SceItemPacoteMateriais.Fields.CODIGO_ESTOQUE.toString()), ItemPacoteMateriaisVO.Fields.SEQ_ESTOQUE.toString());
		pList.add(Projections.property("IPM." + SceItemPacoteMateriais.Fields.QUANTIDADE.toString()), ItemPacoteMateriaisVO.Fields.QUANTIDADE.toString());
		pList.add(Projections.property("MAT." + ScoMaterial.Fields.CODIGO.toString()), ItemPacoteMateriaisVO.Fields.CODIGO_MATERIAL.toString());
		pList.add(Projections.property("EAL." + SceEstoqueAlmoxarifado.Fields.NUMERO_FORNECEDOR.toString()), ItemPacoteMateriaisVO.Fields.NUMERO_FORNECEDOR.toString());
		pList.add(Projections.property("EAL." + SceEstoqueAlmoxarifado.Fields.UMD_CODIGO.toString()), ItemPacoteMateriaisVO.Fields.CODIGO_UNIDADE_MEDIDA.toString());
		pList.add(Projections.property("EAL." + SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO_SEQ.toString()), ItemPacoteMateriaisVO.Fields.SEQ_ALMOXARIFADO.toString());
		pList.add(Projections.property("EAL." + SceEstoqueAlmoxarifado.Fields.IND_ESTOCAVEL.toString()), ItemPacoteMateriaisVO.Fields.ESTOCAVEL.toString());
		pList.add(Projections.property("MAT." + ScoMaterial.Fields.NOME.toString()), ItemPacoteMateriaisVO.Fields.NOME_MATERIAL.toString());
		pList.add(Projections.property("FRN." + ScoFornecedor.Fields.NOME_FANTASIA.toString()), ItemPacoteMateriaisVO.Fields.NOME_FANTASIA_FORNECEDOR.toString());
		
		criteria.setProjection(pList);
		
		
		if (pacote != null) {
			
			criteria.add(Restrictions.eq("IPM." + SceItemPacoteMateriais.Fields.CODIGO_CENTRO_CUSTO_PROPRIETARIO_PACOTE.toString(),
					pacote.getId().getCodigoCentroCustoProprietario()));
		
			criteria.add(Restrictions.eq("IPM." + SceItemPacoteMateriais.Fields.CODIGO_CENTRO_CUSTO_APLICACAO_PACOTE.toString(),
					pacote.getId().getCodigoCentroCustoAplicacao()));
		
			criteria.add(Restrictions.eq("IPM." + SceItemPacoteMateriais.Fields.NUMERO_PACOTE.toString(),
					pacote.getId().getNumero()));
		}
		
		if (pacote.getAlmoxarifado() != null) {
			
			criteria.add(Restrictions.eq("EAL." + SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO_SEQ.toString(), almSeq));
	
		}
		
		criteria.setResultTransformer(Transformers.aliasToBean(ItemPacoteMateriaisVO.class));

		List<ItemPacoteMateriaisVO> listItemPacote =  executeCriteria(criteria);
		
		return listItemPacote;
		
	}

	
	/**
	 * Obtém o ítem de pacote de material, a ser armazenado no grid, com campos necessários
	 * @param seqEstoqueAlmoxarifado
	 * @param codigoMaterial
	 * @return
	 */
	public ItemPacoteMateriaisVO obterItemPacoteMateriaisVO(
			Integer seqEstoqueAlmoxarifado) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class, "EAL");
		
		criteria.createAlias("EAL." + SceEstoqueAlmoxarifado.Fields.FORNECEDOR.toString(), "FRN", JoinType.INNER_JOIN);
		criteria.createAlias("EAL." + SceEstoqueAlmoxarifado.Fields.UNIDADE_MEDIDA.toString(), "UND", JoinType.INNER_JOIN);
		criteria.createAlias("EAL." + SceEstoqueAlmoxarifado.Fields.MATERIAL.toString(), "MAT", JoinType.INNER_JOIN);
		criteria.createAlias("MAT." + ScoMaterial.Fields.GRUPO_MATERIAL.toString(), "GMT", JoinType.INNER_JOIN);
		
		ProjectionList pList = Projections.projectionList();

		pList.add(Projections.property("UND." + ScoUnidadeMedida.Fields.CODIGO.toString()), ItemPacoteMateriaisVO.Fields.CODIGO_UNIDADE_MEDIDA.toString());
		pList.add(Projections.property("UND." + ScoUnidadeMedida.Fields.DESCRICAO.toString()), ItemPacoteMateriaisVO.Fields.DESCRICAO_UNIDADE_MEDIDA.toString());
		pList.add(Projections.property("FRN." + ScoFornecedor.Fields.NUMERO.toString()), ItemPacoteMateriaisVO.Fields.NUMERO_FORNECEDOR.toString());
		pList.add(Projections.property("FRN." + ScoFornecedor.Fields.NOME_FANTASIA.toString()), ItemPacoteMateriaisVO.Fields.NOME_FANTASIA_FORNECEDOR.toString());
		pList.add(Projections.property("MAT." + ScoMaterial.Fields.CODIGO.toString()), ItemPacoteMateriaisVO.Fields.CODIGO_MATERIAL.toString());
		pList.add(Projections.property("MAT." + ScoMaterial.Fields.NOME.toString()), ItemPacoteMateriaisVO.Fields.NOME_MATERIAL.toString());
		pList.add(Projections.property("GMT." + ScoGrupoMaterial.Fields.CODIGO.toString()), ItemPacoteMateriaisVO.Fields.CODIGO_GRUPO_MATERIAL.toString());
		pList.add(Projections.property("GMT." + ScoGrupoMaterial.Fields.DESCRICAO.toString()), ItemPacoteMateriaisVO.Fields.NOME_GRUPO_MTAERIAL.toString());
		
		criteria.setProjection(pList);
		
		
		criteria.add(Restrictions.eq("EAL." + SceEstoqueAlmoxarifado.Fields.SEQ.toString(),
				seqEstoqueAlmoxarifado));
		
		criteria.setResultTransformer(Transformers.aliasToBean(ItemPacoteMateriaisVO.class));
		
		return (ItemPacoteMateriaisVO) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * Verifica a existência de ítem de pacote de materiais
	 * @param idItemPacoteMateriais
	 * @return
	 */
	public Long obterQuantidadeItensPacoteMateriaisPorId(SceItemPacoteMateriaisId idItemPacoteMateriais){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemPacoteMateriais.class, "IPM");
		
		criteria.add(Restrictions.eq("IPM." + SceItemPacoteMateriais.Fields.CODIGO_CENTRO_CUSTO_PROPRIETARIO_PACOTE.toString(),
				idItemPacoteMateriais.getCodigoCentroCustoProprietarioPacoteMateriais()));
		
		criteria.add(Restrictions.eq("IPM." + SceItemPacoteMateriais.Fields.CODIGO_CENTRO_CUSTO_APLICACAO_PACOTE.toString(),
				idItemPacoteMateriais.getCodigoCentroCustoAplicacaoPacoteMateriais()));
		
		criteria.add(Restrictions.eq("IPM." + SceItemPacoteMateriais.Fields.NUMERO_PACOTE.toString(),
				idItemPacoteMateriais.getNumeroPacoteMateriais()));
		
		criteria.add(Restrictions.eq("IPM." + SceItemPacoteMateriais.Fields.CODIGO_ESTOQUE.toString(),
				idItemPacoteMateriais.getSeqEstoque()));
		
		return executeCriteriaCount(criteria);
		
	}

}