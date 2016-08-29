package br.gov.mec.aghu.compras.dao;

import java.util.Calendar;
import java.util.Date;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.compras.vo.ValoresProgramadosVO;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;
@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse"})
public class GrupoMaterialValoresProgramadosQueryBuilder extends QueryBuilder<DetachedCriteria> {

	private Integer gmtCodigo;
	private Boolean materialEstocavel;
	private Integer frnNumero;
	
	private Date dataEntregaInicial;
	private Date dataEntregaInicialParametro;
	private Date dataEntregaFinal;
	private Date dataEntregaFinalParametro;
	
	private Date dataLiberacao;
	
	@Override
	protected DetachedCriteria createProduct() {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemAutorizacaoForn.class, "IAF");
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), "AF", JoinType.INNER_JOIN);
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.PROG_ENTREGAS.toString(), "PEA", JoinType.INNER_JOIN);
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString(), "FSC", JoinType.INNER_JOIN);
		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC", JoinType.INNER_JOIN);
		criteria.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT", JoinType.INNER_JOIN);
		criteria.createAlias("AF." + ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString(), "PRF", JoinType.INNER_JOIN);
		criteria.createAlias("PRF." + ScoPropostaFornecedor.Fields.FORNECEDOR.toString(), "FRN", JoinType.INNER_JOIN);
		return criteria;
	}

	@Override
	protected void doBuild(DetachedCriteria criteria) {
		criteria.add(Restrictions.in("AF." + ScoAutorizacaoForn.Fields.SITUACAO.toString(), 
				new Object[] {DominioSituacaoAutorizacaoFornecimento.AE, DominioSituacaoAutorizacaoFornecimento.PA}));
		Date dataInicial = dataEntregaInicialParametro;
		
		if (dataEntregaInicial != null) {
			dataInicial = dataEntregaInicial;
		}
		
		criteria.add(Restrictions.between("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString(), dataInicial, this.getDataLiberaca(dataEntregaFinal, dataEntregaFinalParametro)));		
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA.toString(), false));
		criteria.add(Restrictions.sqlRestriction("COALESCE(pea2_.qtde_entregue,0) < qtde"));
		criteria.add(Restrictions.gt("AF." + ScoAutorizacaoForn.Fields.NUMERO.toString(), 0));
		
		criteria.add(Restrictions.eq("MAT." + ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString(), gmtCodigo));
		
		if (materialEstocavel != null) {
			criteria.add(Restrictions.eq("MAT." + ScoMaterial.Fields.IND_ESTOCAVEL.toString(), materialEstocavel.booleanValue()));
		}
		if (frnNumero != null) {
			criteria.add(Restrictions.eq("FRN." + ScoFornecedor.Fields.NUMERO.toString(), frnNumero));
		}
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.sum("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.VALOR_TOTAL.toString()), ValoresProgramadosVO.Fields.VALOR_TOTAL.toString())
				.add(Projections.sum("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.VALOR_EFETIVADO.toString()), ValoresProgramadosVO.Fields.VALOR_EFETIVADO.toString())
				);
		criteria.setResultTransformer(Transformers.aliasToBean(ValoresProgramadosVO.class));
		
	}

	public DetachedCriteria build(Integer gmtCodigo, Boolean materialEstocavel, Integer frnNumero,
			Date dataEntregaInicial, Date dataEntregaInicialParametro, 
			Date dataEntregaFinal, Date dataEntregaFinalParametro,
			Date dataLiberacao) {
		
		this.gmtCodigo = gmtCodigo;
		this.materialEstocavel = materialEstocavel;
		this.frnNumero = frnNumero;
		this.dataEntregaInicial = dataEntregaInicial;
		this.dataEntregaInicialParametro = dataEntregaInicialParametro;
		this.dataEntregaFinal = dataEntregaFinal;
		this.dataEntregaFinalParametro = dataEntregaFinalParametro;
		this.dataLiberacao = dataLiberacao;
		
		return super.build();
	}
	
	protected Date getDataLiberaca(Date dataEntregaFinal, Date dataEntregaFinalParametro) {
		Date dataFinal = dataEntregaFinalParametro;
		if (dataEntregaFinal != null) {
			Calendar c = Calendar.getInstance();
			c.setTime(dataEntregaFinal);
			c.add(Calendar.DATE, 1);
			dataFinal = c.getTime(); 
		}
		return dataFinal;
	}

	public Date getDataLiberacao() {
		return dataLiberacao;
	}
}
