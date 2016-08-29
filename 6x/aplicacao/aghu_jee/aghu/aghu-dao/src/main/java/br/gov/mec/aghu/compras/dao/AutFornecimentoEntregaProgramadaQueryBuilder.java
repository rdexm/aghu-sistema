package br.gov.mec.aghu.compras.dao;

import java.util.Calendar;
import java.util.Date;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.compras.vo.AutFornEntregaProgramadaVO;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;
@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse"})
public class AutFornecimentoEntregaProgramadaQueryBuilder extends QueryBuilder<DetachedCriteria> {

	private Integer gmtCodigo;
	private Integer frnNumero;
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
		criteria.createAlias("AFN." + ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString(), "PFR", JoinType.INNER_JOIN);
		criteria.createAlias("PFR." + ScoPropostaFornecedor.Fields.FORNECEDOR.toString(), "FRN", JoinType.INNER_JOIN);
		criteria.createAlias("PFR." + ScoPropostaFornecedor.Fields.LICITACAO.toString(), "LCT", JoinType.INNER_JOIN);
		criteria.createAlias("MAT." + ScoMaterial.Fields.GRUPO_MATERIAL.toString(), "GMT", JoinType.INNER_JOIN);
		
		return criteria;
	}

	@Override
	protected void doBuild(DetachedCriteria criteria) {
		criteria.add(Restrictions.eq("MAT." + ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString(), gmtCodigo));
		criteria.add(Restrictions.eq("AFN." + ScoAutorizacaoForn.Fields.PFR_FRN_NUMERO.toString(), frnNumero));
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA.toString(), false));
		criteria.add(Restrictions.in("AFN." + ScoAutorizacaoForn.Fields.SITUACAO.toString(), 
				new Object[] {DominioSituacaoAutorizacaoFornecimento.AE, DominioSituacaoAutorizacaoFornecimento.PA}));
		//foi utilizado o alias gerado pelo hibernate pois a coluna valor_efetivado existe em outra tabela desse select
		//Vi q essa situacao é utilizada em outras classes
		criteria.add(Restrictions.sqlRestriction("pea4_.valor_total - coalesce(pea4_.valor_efetivado,0) > 0"));
		
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
		
		
		criteria.setProjection(Projections.distinct(Projections.projectionList()
				.add(Projections.property("AFN." + ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString()), AutFornEntregaProgramadaVO.Fields.LCT_NUMERO.toString())
				.add(Projections.property("AFN." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString()), AutFornEntregaProgramadaVO.Fields.NRO_COMPLEMENTO.toString())
				.add(Projections.property("AFN." + ScoAutorizacaoForn.Fields.NUMERO.toString()), AutFornEntregaProgramadaVO.Fields.AFN_NUMERO.toString())
			 )
		);
		criteria.setResultTransformer(Transformers.aliasToBean(AutFornEntregaProgramadaVO.class));
	}

	public DetachedCriteria build(Integer gmtCodigo, Integer frnNumero, 
			Date dataEntregaInicial, Date dataEntregaInicialParametro, 
			Date dataEntregaFinal, Date dataEntregaFinalParametro) {
		
		this.gmtCodigo = gmtCodigo;
		this.frnNumero = frnNumero;
		this.dataEntregaInicial = dataEntregaInicial;
		this.dataEntregaInicialParametro = dataEntregaInicialParametro;
		this.dataEntregaFinal = dataEntregaFinal;
		this.dataEntregaFinalParametro = dataEntregaFinalParametro;
		
		return super.build();
	}

}
