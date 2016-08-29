package br.gov.mec.aghu.compras.contaspagar.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.compras.contaspagar.vo.SolicitacaoTituloVO;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.FcpTituloSolicitacoes;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoVerbaGestao;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;

/**
 * Classe de acesso a dados pertencente ao modelo de Título.
 */
public class FcpTituloSolicitacoesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FcpTituloSolicitacoes> {


/**
	 * 
	 */
	private static final long serialVersionUID = 5043850946802575723L;
	//	private static final String TIULO_SOLICITACOES= "TLS";
	private static final String TIULO_SOLICITACOES_PONTO = "TLS.";
	public List<FcpTituloSolicitacoes> listarTitulosPorTtlSeq(Integer seq){
		DetachedCriteria criteria = DetachedCriteria.forClass(FcpTituloSolicitacoes.class, "TLS");
		criteria.add(Restrictions.eq(TIULO_SOLICITACOES_PONTO+FcpTituloSolicitacoes.Fields.TTL_SEQ.toString(), seq));		
		
		return executeCriteria(criteria);
	}
	
	public List<SolicitacaoTituloVO> listarTitulosSolicitacaoCompra(Integer ttlSeq) {
		DetachedCriteria criteria = obterCriteriaTitulosSolicitacaoCompra();
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("SLC." + ScoSolicitacaoDeCompra.Fields.NUMERO.toString()).as(SolicitacaoTituloVO.Fields.SOLICITACAO.toString()))
				.add(Projections.property("SLC." + ScoSolicitacaoDeCompra.Fields.DESCRICAO.toString()).as(SolicitacaoTituloVO.Fields.DESCRICAO.toString()))
				.add(Projections.property("SLC." + ScoSolicitacaoDeCompra.Fields.APLICACAO.toString()).as(SolicitacaoTituloVO.Fields.APLICACAO.toString()))
				.add(Projections.property("VBG." + FsoVerbaGestao.Fields.SEQ.toString()).as(SolicitacaoTituloVO.Fields.VBG_SEQ.toString()))
				.add(Projections.property("VBG." + FsoVerbaGestao.Fields.DESCRICAO.toString()).as(SolicitacaoTituloVO.Fields.VBG_DESCRICAO.toString()))
				.add(Projections.property("MAT." + ScoMaterial.Fields.CODIGO.toString()).as(SolicitacaoTituloVO.Fields.CODIGO.toString()))
				.add(Projections.property("MAT." + ScoMaterial.Fields.NOME.toString()).as(SolicitacaoTituloVO.Fields.NOME.toString()))
				.add(Projections.property("TXS." + FcpTituloSolicitacoes.Fields.VALOR.toString()).as(SolicitacaoTituloVO.Fields.VALOR_TITULO_SOLICITACAO.toString()))
				.add(Projections.property("TXS." + FcpTituloSolicitacoes.Fields.SEQ.toString()).as(SolicitacaoTituloVO.Fields.SEQ_TITULO_SOLICITACAO.toString()))
		);
		criteria.add(Restrictions.eq("SLC." + ScoSolicitacaoDeCompra.Fields.IND_EXCLUSAO.toString(), DominioSimNao.N.isSim()));
		criteria.add(Restrictions.eq("TXS." + FcpTituloSolicitacoes.Fields.TTL_SEQ.toString(), ttlSeq));
		criteria.setResultTransformer(Transformers.aliasToBean(SolicitacaoTituloVO.class));
		criteria.addOrder(Order.desc("TXS."+FcpTituloSolicitacoes.Fields.SEQ.toString()));
		return executeCriteria(criteria);
	}
	
	private DetachedCriteria obterCriteriaTitulosSolicitacaoCompra() {
		DetachedCriteria criteria = DetachedCriteria.forClass(FcpTituloSolicitacoes.class, "TXS");
		criteria.createAlias("TXS." + FcpTituloSolicitacoes.Fields.SOLICITACAO_COMPRA.toString(), "SLC", JoinType.INNER_JOIN);
		criteria.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT", JoinType.INNER_JOIN);
		
			criteria.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.VERBA_GESTAO.toString(), "VBG", JoinType.LEFT_OUTER_JOIN);
		
			criteria.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.NATUREZA_DESPESA.toString(), "NTD", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("NTD." + FsoNaturezaDespesa.Fields.GRUPO_NATUREZA.toString(), "GND", JoinType.LEFT_OUTER_JOIN);
		
		return criteria;
	}
	
	
	public List<SolicitacaoTituloVO> listarTitulosSolicitacaoServico(Integer ttlSeq) {
		DetachedCriteria criteria = obterTitulosSolicitacaoServico();
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("SLS." + ScoSolicitacaoServico.Fields.NUMERO.toString()).as(SolicitacaoTituloVO.Fields.SOLICITACAO.toString()))
				.add(Projections.property("SLS." + ScoSolicitacaoServico.Fields.DESCRICAO.toString()).as(SolicitacaoTituloVO.Fields.DESCRICAO.toString()))
				.add(Projections.property("SLS." + ScoSolicitacaoServico.Fields.APLICACAO.toString()).as(SolicitacaoTituloVO.Fields.APLICACAO.toString()))
				.add(Projections.property("VBG." + FsoVerbaGestao.Fields.SEQ.toString()).as(SolicitacaoTituloVO.Fields.VBG_SEQ.toString()))
				.add(Projections.property("VBG." + FsoVerbaGestao.Fields.DESCRICAO.toString()).as(SolicitacaoTituloVO.Fields.VBG_DESCRICAO.toString()))
				.add(Projections.property("SERV." + ScoServico.Fields.CODIGO.toString()).as(SolicitacaoTituloVO.Fields.CODIGO.toString()))
				.add(Projections.property("SERV." + ScoServico.Fields.NOME.toString()).as(SolicitacaoTituloVO.Fields.NOME.toString()))
				.add(Projections.property("TXS." + FcpTituloSolicitacoes.Fields.VALOR.toString()).as(SolicitacaoTituloVO.Fields.VALOR_TITULO_SOLICITACAO.toString()))
				.add(Projections.property("TXS." + FcpTituloSolicitacoes.Fields.SEQ.toString()).as(SolicitacaoTituloVO.Fields.SEQ_TITULO_SOLICITACAO.toString()))
		);
		criteria.add(Restrictions.eq("SLS." + ScoSolicitacaoServico.Fields.IND_EXCLUSAO.toString(), DominioSimNao.N.isSim()));
		criteria.add(Restrictions.eq("TXS." + FcpTituloSolicitacoes.Fields.TTL_SEQ.toString(), ttlSeq));
		criteria.setResultTransformer(Transformers.aliasToBean(SolicitacaoTituloVO.class));
		criteria.addOrder(Order.desc("TXS."+FcpTituloSolicitacoes.Fields.SEQ.toString()));
		return executeCriteria(criteria);
	}
	
	private DetachedCriteria obterTitulosSolicitacaoServico() {
		DetachedCriteria criteria = DetachedCriteria.forClass(FcpTituloSolicitacoes.class, "TXS");
		criteria.createAlias("TXS." + FcpTituloSolicitacoes.Fields.SOLICITACAO_SERVICO.toString(), "SLS", JoinType.INNER_JOIN);
		criteria.createAlias("SLS." + ScoSolicitacaoServico.Fields.SERVICO.toString(), "SERV", JoinType.INNER_JOIN);
		
			criteria.createAlias("SLS." + ScoSolicitacaoServico.Fields.VERBA_GESTAO.toString(), "VBG", JoinType.LEFT_OUTER_JOIN);
		
			criteria.createAlias("SLS." + ScoSolicitacaoServico.Fields.NATUREZA_DESPESA.toString(), "NTD", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("NTD." + FsoNaturezaDespesa.Fields.GRUPO_NATUREZA.toString(), "GND", JoinType.LEFT_OUTER_JOIN);
		
		return criteria;
	}


	
}

