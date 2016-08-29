package br.gov.mec.aghu.compras.dao;

import java.util.Calendar;
import java.util.Date;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.compras.vo.ItemAutFornEntregaProgramadaVO;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedor;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;
@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse"})
public class ItemAutFornecimentoEntregaProgramadaQueryBuilder extends QueryBuilder<DetachedCriteria> {

	private Integer afNumero;
	private Date dataEntregaInicial;
	private Date dataEntregaInicialParametro;
	private Date dataEntregaFinal;
	private Date dataEntregaFinalParametro;
	
	@Override
	protected DetachedCriteria createProduct() {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemAutorizacaoForn.class, "IAF");
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), "AFN", JoinType.INNER_JOIN);
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString(), "FSC", JoinType.INNER_JOIN);
		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC", JoinType.INNER_JOIN);
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.PROG_ENTREGAS.toString(), "PEA", JoinType.INNER_JOIN);
		criteria.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT", JoinType.INNER_JOIN);
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.ITEM_PROPOSTA_FORNECEDOR.toString(), "IPF", JoinType.INNER_JOIN);
		criteria.createAlias("IPF." + ScoItemPropostaFornecedor.Fields.ITEM_LICITACAO.toString(), "ITL", JoinType.INNER_JOIN);
		criteria.createAlias("ITL." + ScoItemLicitacao.Fields.LICITACAO.toString(), "LCT", JoinType.INNER_JOIN);
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.UNIDADE.toString(), "UMD", JoinType.INNER_JOIN);
		return criteria;
	}

	@Override
	protected void doBuild(DetachedCriteria criteria) {
		criteria.add(Restrictions.eq("AFN." + ScoAutorizacaoForn.Fields.NUMERO.toString(), afNumero));
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA.toString(), false));

		criteria.add(Restrictions.in("AFN." + ScoAutorizacaoForn.Fields.SITUACAO.toString(), 
				new Object[] {DominioSituacaoAutorizacaoFornecimento.AE, DominioSituacaoAutorizacaoFornecimento.PA}));
		
		Date dataInicial = dataEntregaInicialParametro;
		Date dataFinal = dataEntregaFinalParametro;
		
		if (dataEntregaInicial != null) {
			dataInicial = dataEntregaInicial;
		}
		if (dataEntregaFinal != null) {
			Calendar c = Calendar.getInstance();
			c.setTime(dataEntregaFinal);
			c.add(Calendar.DATE, 1);
			dataFinal = c.getTime(); 
		}
		
		criteria.add(Restrictions.between("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString(), dataInicial, dataFinal));		
		
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("LCT." + ScoLicitacao.Fields.NUMERO.toString()), ItemAutFornEntregaProgramadaVO.Fields.LCT_NUMERO.toString())
				.add(Projections.property("AFN." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString()), ItemAutFornEntregaProgramadaVO.Fields.NRO_COMPLEMENTO.toString())
				.add(Projections.property("ITL." + ScoItemLicitacao.Fields.NUMERO.toString()), ItemAutFornEntregaProgramadaVO.Fields.ITL_NUMERO.toString())
				.add(Projections.property("MAT." + ScoMaterial.Fields.NOME.toString()), ItemAutFornEntregaProgramadaVO.Fields.NOME_MATERIAL.toString())
				.add(Projections.property("MAT." + ScoMaterial.Fields.CODIGO.toString()), ItemAutFornEntregaProgramadaVO.Fields.CODIGO_MATERIAL.toString())
				.add(Projections.property("UMD." + ScoUnidadeMedida.Fields.CODIGO.toString()), ItemAutFornEntregaProgramadaVO.Fields.CODIGO_UNIDADE_MEDIDA.toString())
				.add(Projections.property("UMD." + ScoUnidadeMedida.Fields.DESCRICAO.toString()), ItemAutFornEntregaProgramadaVO.Fields.DESCRICAO_UNIDADE_MEDIDA.toString())
				.add(Projections.property("IAF." + ScoItemAutorizacaoForn.Fields.NUMERO.toString()), ItemAutFornEntregaProgramadaVO.Fields.IAF_NUMERO.toString())
				);
		criteria.setResultTransformer(Transformers.aliasToBean(ItemAutFornEntregaProgramadaVO.class));
		
	}

	public DetachedCriteria build(Integer afNumero, 
			Date dataEntregaInicial, Date dataEntregaInicialParametro, 
			Date dataEntregaFinal, Date dataEntregaFinalParametro) {
		
		this.afNumero = afNumero;
		this.dataEntregaInicial = dataEntregaInicial;
		this.dataEntregaInicialParametro = dataEntregaInicialParametro;
		this.dataEntregaFinal = dataEntregaFinal;
		this.dataEntregaFinalParametro = dataEntregaFinalParametro;
		
		return super.build();
	}

}
