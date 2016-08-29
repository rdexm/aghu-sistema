package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.compras.pac.vo.ItemLicitacaoPregaoBBVO;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoLoteLicitacao;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;

public class ScoLoteLicitacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoLoteLicitacao> {

	private static final long serialVersionUID = 5796921342842170591L;
	
	public List<ScoLoteLicitacao> listarLotes(final ScoLicitacao scoLicitacao)
	{
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoLoteLicitacao.class, "SCOJUS");
		
		if (scoLicitacao != null) {
			criteria.add(Restrictions.eq(ScoLoteLicitacao.Fields.LICITACAO.toString(), scoLicitacao));
			
		}		
		return this.executeCriteria(criteria);
	}

	public List<ScoLoteLicitacao> listarLotesPorPac(final Integer nroPac){
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoLoteLicitacao.class, "ScoLote");
		if (nroPac != null) {
			criteria.add(Restrictions.eq(ScoLoteLicitacao.Fields.NUMERO_LCT.toString(), nroPac));
		}		
		return this.executeCriteria(criteria);
	}
	
	
	public Boolean verificarExisteLote(final Integer nroPac, Short nroLote){
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoLoteLicitacao.class, "ScoLote");
		criteria.add(Restrictions.eq(ScoLoteLicitacao.Fields.NUMERO_LCT.toString(), nroPac));
		criteria.add(Restrictions.eq(ScoLoteLicitacao.Fields.NUMERO.toString(), nroLote));
		return this.executeCriteriaExists(criteria);
	}

	
	public Short obterSequenceLoteLicitacao(final Integer nroPac) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoLoteLicitacao.class);
		criteria.setProjection(Projections.max(ScoLoteLicitacao.Fields.NUMERO
				.toString()));
		criteria.add(Restrictions.eq(ScoLoteLicitacao.Fields.NUMERO_LCT.toString(), nroPac));
		return (Short) executeCriteriaUniqueResult(criteria);
	}
	
	public List<ItemLicitacaoPregaoBBVO> obterItensLicitacaoPregaoBB(Integer nroPac){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemLicitacao.class, "ITL");
		
		//SELECT
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("ITL." + ScoItemLicitacao.Fields.NUMERO_LICITACAO.toString()), ItemLicitacaoPregaoBBVO.Fields.ITL_LCT_NUMERO.toString())
				.add(Projections.property("ITL." + ScoItemLicitacao.Fields.NUMERO.toString()), ItemLicitacaoPregaoBBVO.Fields.ITL_NUMERO.toString())
				.add(Projections.property("ITL." + ScoItemLicitacao.Fields.CLASSIF_ITEM.toString()), ItemLicitacaoPregaoBBVO.Fields.ITL_CLASSIF_ITEM.toString())
				.add(Projections.property("ITL." + ScoItemLicitacao.Fields.VALOR_UNITARIO.toString()), ItemLicitacaoPregaoBBVO.Fields.ITL_VALOR_UNITARIO.toString())
				.add(Projections.property("ITL." + ScoItemLicitacao.Fields.IND_EXCLUSAO.toString()), ItemLicitacaoPregaoBBVO.Fields.ITL_EXCLUSAO.toString())
				.add(Projections.property("ITL." + ScoItemLicitacao.Fields.MOTIVO_EXCLUSAO.toString()), ItemLicitacaoPregaoBBVO.Fields.ITL_MOTIVO_EXCLUSAO.toString())
				.add(Projections.property("ITL." + ScoItemLicitacao.Fields.DT_EXCLUSAO.toString()), ItemLicitacaoPregaoBBVO.Fields.ITL_DT_EXCLUSAO.toString())
				.add(Projections.property("ITL." + ScoItemLicitacao.Fields.MOTIVO_CANCEL.toString()), ItemLicitacaoPregaoBBVO.Fields.ITL_MOTIVO_CANCEL.toString())
				.add(Projections.property("ITL." + ScoItemLicitacao.Fields.IND_PROPOSTA_ESCOLHIDA.toString()), ItemLicitacaoPregaoBBVO.Fields.ITL_PROPOSTA_ESCOLHIDA.toString())
				.add(Projections.property("ITL." + ScoItemLicitacao.Fields.IND_EM_AF.toString()), ItemLicitacaoPregaoBBVO.Fields.ITL_EM_AF.toString())
				.add(Projections.property("LLC." + ScoLoteLicitacao.Fields.NUMERO.toString()), ItemLicitacaoPregaoBBVO.Fields.LLC_NUMERO.toString())
				.add(Projections.property("LLC." + ScoLoteLicitacao.Fields.DESCRICAO.toString()), ItemLicitacaoPregaoBBVO.Fields.LLC_DESCRICAO.toString())
				.add(Projections.property("SLC." + ScoSolicitacaoDeCompra.Fields.QUANTIDADE_APROVADA.toString()), ItemLicitacaoPregaoBBVO.Fields.SLC_QTDE_APROVADA.toString())
				.add(Projections.property("SLC." + ScoSolicitacaoDeCompra.Fields.DESCRICAO.toString()), ItemLicitacaoPregaoBBVO.Fields.SLC_DESCRICAO.toString())
				.add(Projections.property("SLS." + ScoSolicitacaoServico.Fields.QUANTIDADE_SOLICITADA.toString()), ItemLicitacaoPregaoBBVO.Fields.SLS_QTD_SOLICITADA.toString())
				.add(Projections.property("SLS." + ScoSolicitacaoServico.Fields.DESCRICAO.toString()), ItemLicitacaoPregaoBBVO.Fields.SLS_DESCRICAO.toString())
				.add(Projections.property("MAT." + ScoMaterial.Fields.CODIGO.toString()), ItemLicitacaoPregaoBBVO.Fields.MAT_CODIGO.toString())
				.add(Projections.property("MAT." + ScoMaterial.Fields.NOME.toString()), ItemLicitacaoPregaoBBVO.Fields.MAT_NOME.toString())
				.add(Projections.property("MAT." + ScoMaterial.Fields.DESCRICAO.toString()), ItemLicitacaoPregaoBBVO.Fields.MAT_DESCRICAO.toString())
				.add(Projections.property("SRV." + ScoServico.Fields.CODIGO.toString()), ItemLicitacaoPregaoBBVO.Fields.SRV_CODIGO.toString())
				.add(Projections.property("FSC." + ScoFaseSolicitacao.Fields.TIPO.toString()), ItemLicitacaoPregaoBBVO.Fields.FSC_TIPO.toString())
				.add(Projections.property("GMT." + ScoGrupoMaterial.Fields.COD_MERCADORIA_BB.toString()), ItemLicitacaoPregaoBBVO.Fields.GMT_CODIGO_MERCADORIA_BB.toString())
		);
		
		
		//JOIN
		criteria.createAlias("ITL." + ScoItemLicitacao.Fields.LOTE_LICITACAO.toString(), "LLC");
		criteria.createAlias("ITL." + ScoItemLicitacao.Fields.FASES_SOLICITACAO.toString(), "FSC");
		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO.toString(), "SLS", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("MAT." + ScoMaterial.Fields.GRUPO_MATERIAL.toString(), "GMT", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SLS." + ScoSolicitacaoServico.Fields.SERVICO.toString(), "SRV", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SRV." + ScoServico.Fields.GRUPO_SERVICO.toString(), "GSV", JoinType.LEFT_OUTER_JOIN);
		
		//WHERE
		criteria.add(Restrictions.eq("ITL." + ScoItemLicitacao.Fields.NUMERO_LICITACAO.toString(), nroPac));
		
		//ORDER
		criteria.addOrder(Order.asc("LLC." + ScoLoteLicitacao.Fields.NUMERO.toString()));
		criteria.addOrder(Order.asc("ITL." + ScoItemLicitacao.Fields.NUMERO.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(ItemLicitacaoPregaoBBVO.class));
		
		return executeCriteria(criteria);
	}
	

	/**
	 * Obtém o número lote da licitação selecionada.
	 * @param nroPac
	 * @return Short
	 */
	public List<Long> obterLoteLicitacaoSelecionada(final Integer nroPac) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoLoteLicitacao.class);
		criteria.setProjection(Projections.property(ScoLoteLicitacao.Fields.NUMERO.toString()));
		criteria.add(Restrictions.eq(ScoLoteLicitacao.Fields.NUMERO_LCT.toString(), nroPac));
		return executeCriteria(criteria);
	}	
}
