package br.gov.mec.aghu.estoque.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import br.gov.mec.aghu.compras.vo.GeraSolicCompraEstoqueVO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;

/**
 * Builder responsável pela consulta de materiais para geração de SC's de
 * estoque e almoxarifado.
 * 
 * @author matheus
 */
@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse"})
public class MateriaisGeracaoScQueryBuilder extends QueryBuilder<Query> {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 255458723917668904L;

	/** ID do Almoxarifado */
	private Short almoxarifadoId;
	
	/** ID do Almoxarifado Central */
	private Short almoxCentralId;
	
	/** ID do Fornecedor Padrão */
	private Integer fornecPadraoId;
	
	/** Resultado Concreto */
	private List<GeraSolicCompraEstoqueVO> result;

	

	/** Provê query responsável pelo resultado bruto. */
	@Override
	protected Query createProduct() {
		// Aliases e Parâmetros
		final String EAL = "eal", ALM = "ALM", CCT = "CCT", MAT = "MAT", UMD = "UMD", 
				GMT = "GMT", SLC = "SLC", SITUACAO = "situacao", ALMOX_ID = "almxId", ESTOCAVEL = "estocavel", 
				FORNEC_PADRAO = "frn";
		
		// HQL
		StringBuilder hql = new StringBuilder(300);
		
		hql.append("select ").append(EAL)
				.append(" from ").append(SceEstoqueAlmoxarifado.class.getName()).append(' ').append(EAL)
				.append(" inner join fetch ").append(EAL).append('.').append(SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO).append(' ').append(ALM)
				.append(" inner join fetch ").append(ALM).append('.').append(SceAlmoxarifado.Fields.CCT_CODIGO).append(' ').append(CCT)
				.append(" inner join fetch ").append(EAL).append('.').append(SceEstoqueAlmoxarifado.Fields.MATERIAL).append(' ').append(MAT)
				.append(" inner join fetch ").append(EAL).append('.').append(SceEstoqueAlmoxarifado.Fields.UNIDADE_MEDIDA).append(' ').append(UMD)
				.append(" inner join fetch ").append(MAT).append('.').append(ScoMaterial.Fields.GRUPO_MATERIAL).append(' ').append(GMT)
				.append(" left join fetch ").append(EAL).append('.').append(SceEstoqueAlmoxarifado.Fields.SOLICITACAO_COMPRA).append(' ').append(SLC)
				.append(" where ").append(MAT).append('.').append(ScoMaterial.Fields.SITUACAO).append(" = :").append(SITUACAO);
		
		if (almoxarifadoId != null) {			
			hql.append(" and ").append(ALM).append('.')
					.append(SceAlmoxarifado.Fields.SEQ)
					.append(" = :").append(ALMOX_ID);
		}
		
		hql.append(" and ").append(MAT).append('.').append(ScoMaterial.Fields.IND_ESTOCAVEL).append(" = :").append(ESTOCAVEL)
				.append(" and ").append(MAT).append('.').append(ScoMaterial.Fields.PRODUCAO_INTERNA).append(" is null")
				.append(" and ").append(EAL).append('.').append(SceEstoqueAlmoxarifado.Fields.NUMERO_FORNECEDOR).append(" = :").append(FORNEC_PADRAO)
				.append(" and ").append(EAL).append('.').append(SceEstoqueAlmoxarifado.Fields.IND_SITUACAO).append(" = :").append(SITUACAO)
				.append(" and ").append(EAL).append('.').append(SceEstoqueAlmoxarifado.Fields.IND_ESTOCAVEL).append(" = :").append(ESTOCAVEL)
				.append(" and coalesce(").append(EAL).append('.').append(SceEstoqueAlmoxarifado.Fields.QTDE_PONTO_PEDIDO).append(",0) > 0")
				.append(" and coalesce(").append(EAL).append('.').append(SceEstoqueAlmoxarifado.Fields.QTDE_DISPONIVEL).append(",0) +")
				.append(" coalesce(").append(EAL).append('.').append(SceEstoqueAlmoxarifado.Fields.QTDE_BLOQUEADA).append(",0)")
				.append(" <= coalesce(").append(EAL).append('.').append(SceEstoqueAlmoxarifado.Fields.QTDE_PONTO_PEDIDO).append(",0)")
				.append(" and ").append(MAT).append('.').append(ScoMaterial.Fields.ALMOXARIFADO).append('=')
				.append(EAL).append('.').append(SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO)
				.append(" order by ").append(ALM).append('.').append(SceAlmoxarifado.Fields.SEQ).append(',')
				.append(GMT).append('.').append(ScoGrupoMaterial.Fields.CODIGO).append(',')
				.append(MAT).append('.').append(ScoMaterial.Fields.CODIGO);
		
		// Query Parametrizada
		Query query = createQuery(hql.toString());
		query.setParameter(FORNEC_PADRAO, fornecPadraoId);
		query.setParameter(ESTOCAVEL, true);
		query.setParameter(SITUACAO, DominioSituacao.A);
		
		if (almoxarifadoId != null) {			
			query.setParameter(ALMOX_ID, almoxarifadoId);
		}
		
		return query;
	}

	/** Monta resultado concreto a partir do resultado bruto. */
	@Override
	protected void doBuild(Query hql) {
		// Destino
		final String PLAN = "PLAN", ALMOX = "ALMOX";
		
		// Resultado Bruto
		@SuppressWarnings("unchecked")
		List<SceEstoqueAlmoxarifado> rawResult = hql.getResultList();
		result = new ArrayList<GeraSolicCompraEstoqueVO>();
		
		// Resultado Concreto
		for (SceEstoqueAlmoxarifado rawItem : rawResult) {
			// Invocado refresh para atender item 1 do issue #30015.
			refresh(rawItem);
			GeraSolicCompraEstoqueVO item = new GeraSolicCompraEstoqueVO();
			
			if (almoxCentralId.equals(rawItem.getAlmoxarifado().getSeq())) {
				item.setDestino(PLAN);
			} else {
				item.setDestino(ALMOX);
			}
			
			item.setGmtCodigo(rawItem.getMaterial().getGrupoMaterial());
			item.setAlmoxarifado(rawItem.getAlmoxarifado());
			item.setMatCodigo(rawItem.getMaterial());
			item.setSeq(rawItem.getSeq());
			item.setQtdeDisponivel(rawItem.getQtdeDisponivel());
			item.setQtdeBloqueada(rawItem.getQtdeBloqueada());
			item.setQtdePontoPedido((Integer) CoreUtil.nvl(rawItem.getQtdePontoPedido(), 0));
			item.setSlcNumero(rawItem.getSolicitacaoCompra());
			item.setUmdCodigo(rawItem.getUnidadeMedida());
			item.setCctCodigoAplic(rawItem.getAlmoxarifado().getCentroCusto());
			item.setIndCentral(rawItem.getAlmoxarifado().getIndCentral());
			result.add(item);
		}
	}
	
	// Getters/Setters

	public void setAlmoxarifadoId(Short almoxarifadoId) {
		this.almoxarifadoId = almoxarifadoId;
	}

	public void setAlmoxCentralId(Short almoxCentralId) {
		this.almoxCentralId = almoxCentralId;
	}

	public void setFornecPadraoId(Integer fornecPadraoId) {
		this.fornecPadraoId = fornecPadraoId;
	}

	public List<GeraSolicCompraEstoqueVO> getResultList() {
		return result;
	}
}