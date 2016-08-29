package br.gov.mec.aghu.compras.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.BigDecimalType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.compras.constantes.EntregasGlobaisAcesso;
import br.gov.mec.aghu.compras.vo.ProgramacaEntregaGlobalSubQueriesVO;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;
import br.gov.mec.aghu.core.utils.DateUtil;

@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse"})
public class ProgramacaoEntregaGlobalFornecedorSubQueryBuilder extends QueryBuilder<DetachedCriteria> {

	private Integer codigoGrupoMaterial;
	private Date dataInicial;
	private Date dataFinal;
	private Integer numeroFornecedor;
	private ProgramacaEntregaGlobalSubQueriesVO subQueries;
	private EntregasGlobaisAcesso tipoValorEnum;
	private Date dataAtual = DateUtil.truncaData(new Date());
	
	@Override
	protected DetachedCriteria createProduct() {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemAutorizacaoForn.class, "IAF");
		return criteria;
	}

	@Override
	protected void doBuild(DetachedCriteria criteria) {
		String alias = determinarAlias(tipoValorEnum);
		
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.PROG_ENTREGAS.toString(), "PEA");
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), "AFN");
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString(), "FSC");
		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC");		
		criteria.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT");
		
		criteria.add(Restrictions.gt("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString(), 0));
		criteria.add(Restrictions.between("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString(), 
				dataInicial, dataFinal));
		
		
		if(tipoValorEnum == EntregasGlobaisAcesso.SALDO_PROGRAMADO) {
			criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA.toString(), Boolean.FALSE));
			criteria.add(Restrictions.gt("AFN." + ScoAutorizacaoForn.Fields.DT_VENCTO_CONTRATO.toString(), dataAtual));
				
		} else if(tipoValorEnum == EntregasGlobaisAcesso.VALOR_LIBERAR) {
			criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ASSINATURA.toString(), Boolean.FALSE));
			criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA.toString(), Boolean.FALSE));
			criteria.add(Restrictions.gt("AFN." + ScoAutorizacaoForn.Fields.DT_VENCTO_CONTRATO.toString(), dataAtual));

		} else if(tipoValorEnum == EntregasGlobaisAcesso.VALOR_LIBERADO) {
			criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_PLANEJAMENTO.toString(), Boolean.TRUE));
			criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ASSINATURA.toString(), Boolean.TRUE));
			criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA.toString(), Boolean.FALSE));
		} else if(tipoValorEnum == EntregasGlobaisAcesso.VALOR_ATRASO) {
			criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_PLANEJAMENTO.toString(), Boolean.TRUE));
			criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ASSINATURA.toString(), Boolean.TRUE));
			criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA.toString(), Boolean.FALSE));
	
			criteria.add(Restrictions.le("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString(), dataAtual));
		}
		
		criteria.add(Restrictions.sqlRestriction(" coalesce(pea1_.qtde_entregue,0) < pea1_.qtde "));
		
//		criteria.add(Restrictions.eqProperty("FSC." + ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString(), 
//				"PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString()));
//		criteria.add(Restrictions.eqProperty("FSC." + ScoFaseSolicitacao.Fields.IAF_NUMERO.toString(), 
//				"PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_NUMERO.toString()));
//		criteria.add(Restrictions.eqProperty("SLC." + ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), 
//				"FSC." + ScoFaseSolicitacao.Fields.SLC_NUMERO.toString()));
//		criteria.add(Restrictions.eqProperty("MAT." + ScoMaterial.Fields.CODIGO.toString(), 
//				"SLC." + ScoSolicitacaoDeCompra.Fields.MAT_CODIGO.toString()));
		criteria.add(Restrictions.eq("MAT." + ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString(), codigoGrupoMaterial));
//		criteria.add(Restrictions.eqProperty("IAF." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN_NUMERO.toString(), 
//				"FSC." + ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()));
//		criteria.add(Restrictions.eqProperty("IAF." + ScoItemAutorizacaoForn.Fields.NUMERO.toString(), 
//				"FSC." + ScoFaseSolicitacao.Fields.IAF_NUMERO.toString()));
//		criteria.add(Restrictions.eqProperty("AFN." + ScoAutorizacaoForn.Fields.NUMERO.toString(), 
//				"IAF." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN_NUMERO.toString()));
		criteria.add(Restrictions.eq("AFN." + ScoAutorizacaoForn.Fields.PFR_FRN_NUMERO.toString(), numeroFornecedor));
		
		List<DominioSituacaoAutorizacaoFornecimento> situacoes = new ArrayList<DominioSituacaoAutorizacaoFornecimento>();
		situacoes.add(DominioSituacaoAutorizacaoFornecimento.AE);
		situacoes.add(DominioSituacaoAutorizacaoFornecimento.PA);
		criteria.add(Restrictions.in("AFN." + ScoAutorizacaoForn.Fields.SITUACAO.toString(), situacoes));
		
		ProjectionList projectionList = Projections.projectionList();
		projectionList.add(Projections.sqlProjection(" coalesce(sum(pea1_.valor_total - coalesce(pea1_.valor_efetivado,0)),0) " + alias, new String[]{alias}, new Type[]{BigDecimalType.INSTANCE}));
		criteria.setProjection(projectionList);
	}

	
	
	private String determinarAlias(EntregasGlobaisAcesso tipoValorEnum) {
		String alias = "a";
		
		if(tipoValorEnum == EntregasGlobaisAcesso.SALDO_PROGRAMADO) {
			alias = "saldoProgramado";
		} else if (tipoValorEnum == EntregasGlobaisAcesso.VALOR_LIBERAR){
			alias = "valorALiberar";
		} else if (tipoValorEnum == EntregasGlobaisAcesso.VALOR_LIBERADO){
			alias = "valorLiberado";
		} else if (tipoValorEnum == EntregasGlobaisAcesso.VALOR_ATRASO){
			alias = "valorEmAtraso";
		}
		
		return alias;
	}
	
	
	public Integer getCodigoGrupoMaterial() {
		return codigoGrupoMaterial;
	}

	public void setCodigoGrupoMaterial(Integer codigoGrupoMaterial) {
		this.codigoGrupoMaterial = codigoGrupoMaterial;
	}

	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

	public Integer getNumeroFornecedor() {
		return numeroFornecedor;
	}

	public void setNumeroFornecedor(Integer numeroFornecedor) {
		this.numeroFornecedor = numeroFornecedor;
	}

	public ProgramacaEntregaGlobalSubQueriesVO getSubQueries() {
		return subQueries;
	}

	public void setSubQueries(ProgramacaEntregaGlobalSubQueriesVO subQueries) {
		this.subQueries = subQueries;
	}

	public EntregasGlobaisAcesso getTipoValorEnum() {
		return tipoValorEnum;
	}

	public void setTipoValorEnum(EntregasGlobaisAcesso tipoValorEnum) {
		this.tipoValorEnum = tipoValorEnum;
	}


}
