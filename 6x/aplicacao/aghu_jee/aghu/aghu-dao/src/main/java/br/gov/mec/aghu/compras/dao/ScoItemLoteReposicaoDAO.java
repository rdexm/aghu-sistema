package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.compras.vo.ItemReposicaoMaterialVO;
import br.gov.mec.aghu.compras.vo.SolReposicaoMaterialVO;
import br.gov.mec.aghu.dominio.DominioSituacaoLoteReposicao;
import br.gov.mec.aghu.dominio.DominioTipoMaterial;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoItemLoteReposicao;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoLoteReposicao;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;

public class ScoItemLoteReposicaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoItemLoteReposicao> {

	private static final String ITL = "ITL.";
	private static final String FSL = "FSL.";
	private static final String SLC3 = "SLC.";
	private static final String SLC = "SLC";
	private static final String ILR2 = "ILR";
	private static final String SLC2 = "SLC2.";
	private static final String LTR = "LTR.";
	private static final String ILR = "ILR.";
	private static final String MAT = "MAT.";
	private static final long serialVersionUID = -4588561818411954354L;

	public List<SolReposicaoMaterialVO> pesquisarSolicitacoesEmLoteSemPac(Integer matCodigo, FccCentroCustos ccAplic) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemLoteReposicao.class, ILR2);
		criteria.createAlias(ILR+ScoItemLoteReposicao.Fields.LOTE_REPOSICAO.toString(), "LTR");
		criteria.createAlias(ILR+ScoItemLoteReposicao.Fields.MATERIAL.toString(), "MAT");
		criteria.createAlias(ILR+ScoItemLoteReposicao.Fields.SOLICITACAO_COMPRA.toString(), SLC);

		ProjectionList p = Projections.projectionList();

		p.add(Projections.property(SLC3 + ScoSolicitacaoDeCompra.Fields.NUMERO.toString()), SolReposicaoMaterialVO.Fields.SLC_NUMERO.toString());
		p.add(Projections.property(SLC3 + ScoSolicitacaoDeCompra.Fields.QUANTIDADE_APROVADA.toString()), SolReposicaoMaterialVO.Fields.QTD_APROVADA.toString());
		p.add(Projections.property(LTR + ScoLoteReposicao.Fields.DESCRICAO.toString()), SolReposicaoMaterialVO.Fields.DESCRICAO_LOTE.toString());
		p.add(Projections.property(LTR + ScoLoteReposicao.Fields.SEQ.toString()), SolReposicaoMaterialVO.Fields.LTR_SEQ.toString());
		p.add(Projections.property(ILR + ScoItemLoteReposicao.Fields.SEQ.toString()), SolReposicaoMaterialVO.Fields.SEQ_ITEM.toString());
		
		criteria.setProjection(p);

		criteria.add(Restrictions.eq(MAT+ScoMaterial.Fields.CODIGO.toString(), matCodigo));
		criteria.add(Restrictions.eq(SLC3+ScoSolicitacaoDeCompra.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		
		if (ccAplic != null) {
			criteria.add(Restrictions.eq(LTR+ScoLoteReposicao.Fields.CC_APLICACAO.toString(), ccAplic));
		} else {
			criteria.add(Restrictions.isNull(LTR+ScoLoteReposicao.Fields.CC_APLICACAO.toString()));
		}
		
		DetachedCriteria subQuery = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FSL");
		subQuery.setProjection(Projections.property(FSL+ScoFaseSolicitacao.Fields.SLC_NUMERO.toString()));
		subQuery.createAlias(FSL+ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC2");
		subQuery.createAlias(FSL+ScoFaseSolicitacao.Fields.ITEM_LICITACAO.toString(), "ITL");
		subQuery.createAlias(ITL+ScoItemLicitacao.Fields.LICITACAO.toString(), "LCT");
		
		subQuery.add(Restrictions.eq(FSL+ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		subQuery.add(Restrictions.eq("LCT."+ScoLicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		subQuery.add(Restrictions.eq(ITL+ScoItemLicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		subQuery.add(Restrictions.ne(LTR+ScoLoteReposicao.Fields.SITUACAO.toString(), DominioSituacaoLoteReposicao.EX));
		subQuery.add(Restrictions.eq(SLC2+ScoSolicitacaoDeCompra.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		subQuery.add(Restrictions.eq(SLC2+ScoSolicitacaoDeCompra.Fields.MAT_CODIGO.toString(), matCodigo));
//		if(isOracle()){
//			subQuery.add(Restrictions.sqlRestriction(" rownum < 10"));
//	    } else {
//	    	subQuery.add(Restrictions.sqlRestriction(" limit 10"));
//	    }
		criteria.add(Subqueries.propertyNotIn(SLC3+ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), subQuery));
		criteria.setResultTransformer(Transformers.aliasToBean(SolReposicaoMaterialVO.class));
		//criteria.getExecutableCriteria(getSession()).setMaxResults(10);
		return executeCriteria(criteria, 0, 10, null, false);
	}

	public List<SolReposicaoMaterialVO> pesquisarSolicitacoesEmLoteSemPac(ScoSolicitacaoDeCompra solCompra) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemLoteReposicao.class, ILR2);
		criteria.createAlias(ILR+ScoItemLoteReposicao.Fields.LOTE_REPOSICAO.toString(), "LTR");
		criteria.createAlias(ILR+ScoItemLoteReposicao.Fields.MATERIAL.toString(), "MAT");
		criteria.createAlias(ILR+ScoItemLoteReposicao.Fields.SOLICITACAO_COMPRA.toString(), SLC);

		ProjectionList p = Projections.projectionList();

		p.add(Projections.property(SLC3 + ScoSolicitacaoDeCompra.Fields.NUMERO.toString()), SolReposicaoMaterialVO.Fields.SLC_NUMERO.toString());
		p.add(Projections.property(SLC3 + ScoSolicitacaoDeCompra.Fields.QUANTIDADE_APROVADA.toString()), SolReposicaoMaterialVO.Fields.QTD_APROVADA.toString());
		p.add(Projections.property(LTR + ScoLoteReposicao.Fields.DESCRICAO.toString()), SolReposicaoMaterialVO.Fields.DESCRICAO_LOTE.toString());
		p.add(Projections.property(LTR + ScoLoteReposicao.Fields.SEQ.toString()), SolReposicaoMaterialVO.Fields.LTR_SEQ.toString());
		p.add(Projections.property(ILR + ScoItemLoteReposicao.Fields.SEQ.toString()), SolReposicaoMaterialVO.Fields.SEQ_ITEM.toString());
		
		criteria.setProjection(p);

		criteria.add(Restrictions.eq(SLC3+ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), solCompra.getNumero()));
		criteria.add(Restrictions.eq(SLC3+ScoSolicitacaoDeCompra.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		
		DetachedCriteria subQuery = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FSL");
		subQuery.setProjection(Projections.property(FSL+ScoFaseSolicitacao.Fields.SLC_NUMERO.toString()));
		subQuery.createAlias(FSL+ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC2");
		subQuery.createAlias(FSL+ScoFaseSolicitacao.Fields.ITEM_LICITACAO.toString(), "ITL");
		subQuery.createAlias(ITL+ScoItemLicitacao.Fields.LICITACAO.toString(), "LCT");
		
		subQuery.add(Restrictions.eq(FSL+ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		subQuery.add(Restrictions.eq("LCT."+ScoLicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		subQuery.add(Restrictions.eq(ITL+ScoItemLicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		subQuery.add(Restrictions.ne(LTR+ScoLoteReposicao.Fields.SITUACAO.toString(), DominioSituacaoLoteReposicao.EX));
		subQuery.add(Restrictions.eq(SLC2+ScoSolicitacaoDeCompra.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		subQuery.add(Restrictions.eq(SLC2+ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), solCompra.getNumero()));
		if(isOracle()){
			subQuery.add(Restrictions.sqlRestriction(" rownum < 10"));
	    } else {
	    	subQuery.add(Restrictions.sqlRestriction(" limit 10"));
	    }
		criteria.add(Subqueries.propertyNotIn(SLC3+ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), subQuery));
		criteria.setResultTransformer(Transformers.aliasToBean(SolReposicaoMaterialVO.class));
		//criteria.getExecutableCriteria(getSession()).setMaxResults(10);
		return executeCriteria(criteria, 0, 10, null, false);
	}

	public DetachedCriteria obterCriteriaReposicaoMaterial(ScoLoteReposicao lote) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemLoteReposicao.class, ILR2);
		criteria.createAlias(ILR+ScoItemLoteReposicao.Fields.MATERIAL.toString(), "MAT");
		criteria.createAlias(MAT+ScoMaterial.Fields.GRUPO_MATERIAL.toString(), "GMT");
		criteria.createAlias(ILR+ScoItemLoteReposicao.Fields.SOLICITACAO_COMPRA.toString(), SLC, CriteriaSpecification.LEFT_JOIN);
		
		ProjectionList p = Projections.projectionList();

		p.add(Projections.property(MAT + ScoMaterial.Fields.CODIGO.toString()), ItemReposicaoMaterialVO.Fields.MAT_CODIGO.toString());
		p.add(Projections.property(MAT + ScoMaterial.Fields.NOME.toString()), ItemReposicaoMaterialVO.Fields.NOME_MATERIAL.toString());
		p.add(Projections.property("GMT." + ScoGrupoMaterial.Fields.CODIGO.toString()), ItemReposicaoMaterialVO.Fields.CODIGO_GRUPO.toString());
		p.add(Projections.property("GMT." + ScoGrupoMaterial.Fields.DESCRICAO.toString()), ItemReposicaoMaterialVO.Fields.NOME_GRUPO.toString());
		p.add(Projections.sqlProjection("CASE WHEN MAT1_." + ScoMaterial.Fields.IND_ESTOCAVEL.name() + " = 'N' THEN '"+DominioTipoMaterial.D.getDescricao()+"' ELSE '"+DominioTipoMaterial.E.getDescricao()+"' END as " + ItemReposicaoMaterialVO.Fields.TIPO_MATERIAL.toString(), new String[]{ItemReposicaoMaterialVO.Fields.TIPO_MATERIAL.toString()}, new Type[]{StringType.INSTANCE}));
		p.add(Projections.property(ILR + ScoItemLoteReposicao.Fields.QTD_PONTO_PEDIDO.toString()), ItemReposicaoMaterialVO.Fields.PONTO_PEDIDO.toString());
		p.add(Projections.property(ILR + ScoItemLoteReposicao.Fields.TEMPO_REPOSICAO.toString()), ItemReposicaoMaterialVO.Fields.TEMPO_REPOSICAO.toString());
		p.add(Projections.property(ILR + ScoItemLoteReposicao.Fields.CUSTO_MEDIO.toString()), ItemReposicaoMaterialVO.Fields.CUSTO_MEDIO.toString());
		p.add(Projections.property(ILR + ScoItemLoteReposicao.Fields.QTD_CONFIRMADA.toString()), ItemReposicaoMaterialVO.Fields.QTDE.toString());
		p.add(Projections.property(ILR + ScoItemLoteReposicao.Fields.SEQ.toString()), ItemReposicaoMaterialVO.Fields.SEQ_ITEM.toString());
		p.add(Projections.property(SLC3 + ScoSolicitacaoDeCompra.Fields.NUMERO.toString()), ItemReposicaoMaterialVO.Fields.SLC_GERADA.toString());
		p.add(Projections.property(ILR + ScoItemLoteReposicao.Fields.IND_INCLUSAO.toString()), ItemReposicaoMaterialVO.Fields.IND_INCLUSAO.toString());
		
		criteria.setProjection(p);

		criteria.add(Restrictions.eq(ILR+ScoItemLoteReposicao.Fields.LOTE_REPOSICAO.toString(), lote));
		
		return criteria;
	}

	public List<ScoItemLoteReposicao> pesquisarScLoteReposicao(ScoLoteReposicao lote) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemLoteReposicao.class, ILR2);
		criteria.add(Restrictions.eq(ILR+ScoItemLoteReposicao.Fields.LOTE_REPOSICAO.toString(), lote));
		criteria.add(Restrictions.isNotNull(ILR+ScoItemLoteReposicao.Fields.SOLICITACAO_COMPRA.toString()));
		return this.executeCriteria(criteria);
	}

	public Long obterQtdScGerada(ScoLoteReposicao lote) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemLoteReposicao.class);
		
		criteria.add(Restrictions.eq(ScoItemLoteReposicao.Fields.LOTE_REPOSICAO.toString(), lote));
		criteria.add(Restrictions.isNotNull(ScoItemLoteReposicao.Fields.SOLICITACAO_COMPRA.toString()));
		
		return this.executeCriteriaCount(criteria);
	}

	public Long pesquisarMaterialReposicaoCount(ScoLoteReposicao lote) {
		DetachedCriteria criteria = this.obterCriteriaReposicaoMaterial(lote);
		return this.executeCriteriaCount(criteria);
	}

	public List<ItemReposicaoMaterialVO> pesquisarMaterialReposicao(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, ScoLoteReposicao lote) {
		DetachedCriteria criteria = this.obterCriteriaReposicaoMaterial(lote);
		criteria.setResultTransformer(Transformers.aliasToBean(ItemReposicaoMaterialVO.class));
		return this.executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	public Boolean verificarMovimentacaoScLoteReposicao(ScoLoteReposicao loteReposicao, 
			ScoPontoParadaSolicitacao ppsAtual, ScoPontoParadaSolicitacao ppsAnterior,
			Integer seqItem) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemLoteReposicao.class, ILR2);
		
		criteria.add(Restrictions.eq(ILR+ScoItemLoteReposicao.Fields.LOTE_REPOSICAO.toString(), loteReposicao));
		criteria.createAlias(ILR+ScoItemLoteReposicao.Fields.SOLICITACAO_COMPRA.toString(), SLC);
		criteria.add(Restrictions.or(
				Restrictions.ne(SLC3+ScoSolicitacaoDeCompra.Fields.PPS_CODIGO_LOC_PROXIMA.toString(), ppsAtual.getCodigo()), 
				Restrictions.ne(SLC3+ScoSolicitacaoDeCompra.Fields.PPS_CODIGO.toString(), ppsAnterior.getCodigo())));
		
		if (seqItem != null) {
			criteria.add(Restrictions.eq(ILR+ScoItemLoteReposicao.Fields.SEQ.toString(), seqItem));
		}
		
		return this.executeCriteriaCount(criteria) > 0;
	}
	
	public Boolean verificarMaterialExistente(ScoLoteReposicao lote, ScoMaterial mat) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemLoteReposicao.class, ILR2);
		criteria.add(Restrictions.eq(ILR+ScoItemLoteReposicao.Fields.LOTE_REPOSICAO.toString(), lote));
		criteria.add(Restrictions.eq(ILR+ScoItemLoteReposicao.Fields.MATERIAL.toString(), mat));
		
		return this.executeCriteriaCount(criteria) > 0;
	}
}
