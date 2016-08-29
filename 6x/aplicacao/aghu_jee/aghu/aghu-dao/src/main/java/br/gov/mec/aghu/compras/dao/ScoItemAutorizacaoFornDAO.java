package br.gov.mec.aghu.compras.dao;

import br.gov.mec.aghu.compras.constantes.EntregasGlobaisAcesso;
import br.gov.mec.aghu.compras.vo.*;
import br.gov.mec.aghu.dominio.DominioModalidadeEmpenho;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecedor;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;
import br.gov.mec.aghu.model.*;
import br.gov.mec.aghu.core.utils.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.*;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DoubleType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.Type;

import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 
 * @modulo compras
 *
 */

public class ScoItemAutorizacaoFornDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoItemAutorizacaoForn> {

	private static final long serialVersionUID = -6390416994902382284L;

	/**
	 * Obtem um item de autorização de fornecimento através do numero da autorização e número do item
	 * @param afnNumero
	 * @param numero
	 * @return
	 */
	public ScoItemAutorizacaoForn obterItemAutorizacaoFornPorId(Integer afnNumero, Integer numero){
		ScoItemAutorizacaoFornId id = new ScoItemAutorizacaoFornId();
		id.setAfnNumero(afnNumero);
		id.setNumero(numero);
		return this.obterItemAutorizacaoFornPorId(id);
	}
	public ScoItemAutorizacaoForn obterItemAutorizacaoFornPorIdRelatorio(Integer afnNumero, Integer numero){
		ScoItemAutorizacaoFornId id = new ScoItemAutorizacaoFornId();
		id.setAfnNumero(afnNumero);
		id.setNumero(numero);
		return this.obterItemAutorizacaoFornPorIdRelatorio(id);
	}

	/**
	 * Obtem um item de autorização de fornecimento através do id de ScoItemAutorizacaoForn
	 * @param id
	 * @return
	 */
	public ScoItemAutorizacaoForn obterItemAutorizacaoFornPorIdRelatorio(ScoItemAutorizacaoFornId id){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemAutorizacaoForn.class, "IAF");
		
		criteria.createAlias("IAF."+ScoItemAutorizacaoForn.Fields.SERVIDOR.toString(), "SRV", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("IAF."+ScoItemAutorizacaoForn.Fields.SERVIDOR_ESTORNO.toString(), "SRE", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("IAF."+ScoItemAutorizacaoForn.Fields.UNIDADE.toString(), "UMD", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("IAF."+ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), "AFN", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias("AFN."+ScoAutorizacaoForn.Fields.CONDICAO_PAGAMENTO_PROPOS.toString(), "AFN_CPP", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias("AFN."+ScoAutorizacaoForn.Fields.SERVIDOR_AUTORIZADO.toString(), "AFN_SA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("AFN_SA."+RapServidores.Fields.PESSOA_FISICA.toString(), "AFN_SA_PF", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias("AFN."+ScoAutorizacaoForn.Fields.SERVIDOR_ASSINA_COORD.toString(), "AFN_SAC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("AFN_SAC."+RapServidores.Fields.PESSOA_FISICA.toString(), "AFN_SAC_PF", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias("AFN."+ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString(), "AFN_PF", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("AFN_PF."+ScoPropostaFornecedor.Fields.LICITACAO.toString(), "AFN_PF_LI", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("AFN_PF_LI."+ScoLicitacao.Fields.MODALIDADE_LICITACAO.toString(), "AFN_PF_LI_ML", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias("AFN_PF."+ScoPropostaFornecedor.Fields.FORNECEDOR.toString(), "AFN_PF_FO", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("AFN_PF_FO."+ScoFornecedor.Fields.CIDADE.toString(), "AFN_PF_FO_CI", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias("AFN."+ScoAutorizacaoForn.Fields.AF_EMPENHO.toString(), "AFN_AE", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("IAF."+ScoItemAutorizacaoForn.Fields.ITEM_PROPOSTA_FORNECEDOR.toString(), "PPF", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("IAF."+ScoItemAutorizacaoForn.Fields.NOME_COMERCIAL.toString(), "NOC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("IAF."+ScoItemAutorizacaoForn.Fields.MARCA_COMERCIAL.toString(), "MAC", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(ScoItemAutorizacaoForn.Fields.ID_AUT_FORN_NUMERO.toString(), id.getAfnNumero()));
		criteria.add(Restrictions.eq(ScoItemAutorizacaoForn.Fields.NUMERO.toString(), id.getNumero()));
		
		return (ScoItemAutorizacaoForn)executeCriteriaUniqueResult(criteria);
	}
	
	public ScoItemAutorizacaoForn obterItemAutorizacaoFornPorId(ScoItemAutorizacaoFornId id){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemAutorizacaoForn.class, "IAF");
		
		criteria.createAlias("IAF."+ScoItemAutorizacaoForn.Fields.SERVIDOR.toString(), "SRV", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("IAF."+ScoItemAutorizacaoForn.Fields.SERVIDOR_ESTORNO.toString(), "SRE", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("IAF."+ScoItemAutorizacaoForn.Fields.UNIDADE.toString(), "UMD", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("IAF."+ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), "AFN", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("IAF."+ScoItemAutorizacaoForn.Fields.ITEM_PROPOSTA_FORNECEDOR.toString(), "PPF", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("IAF."+ScoItemAutorizacaoForn.Fields.NOME_COMERCIAL.toString(), "NOC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("IAF."+ScoItemAutorizacaoForn.Fields.MARCA_COMERCIAL.toString(), "MAC", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(ScoItemAutorizacaoForn.Fields.ID_AUT_FORN_NUMERO.toString(), id.getAfnNumero()));
		criteria.add(Restrictions.eq(ScoItemAutorizacaoForn.Fields.NUMERO.toString(), id.getNumero()));
		
		return (ScoItemAutorizacaoForn)executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * #5262 
	 * @author dzboeira
	 * @param objPesquisa
	 * @return ScoItem
	 */
	public ScoItemAutorizacaoForn obterDadosItensAutorizacaoFornecimento(Integer afNumero, Integer numero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemAutorizacaoForn.class, "IAF");
		criteria.createAlias("IAF."+ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), "AF", JoinType.INNER_JOIN);
		criteria.createAlias("IAF."+ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString(), "FSL", JoinType.INNER_JOIN);
		criteria.createAlias("FSL."+ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("FSL."+ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO.toString(), "SLS", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SLC."+ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SLS."+ScoSolicitacaoServico.Fields.SERVICO.toString(), "SRV", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("IAF."+ScoItemAutorizacaoForn.Fields.ITEM_PROPOSTA_FORNECEDOR.toString(), "IPR", JoinType.INNER_JOIN);
		criteria.createAlias("IPR."+ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR.toString(), "PROP", JoinType.INNER_JOIN);
		
		criteria.add(Restrictions.eq("IAF."+ScoItemAutorizacaoForn.Fields.ID_AUT_FORN_NUMERO.toString(), afNumero));
     	criteria.add(Restrictions.eq("IAF."+ScoItemAutorizacaoForn.Fields.NUMERO.toString(), numero));		
		
		return (ScoItemAutorizacaoForn) executeCriteriaUniqueResult(criteria);		
	}

	public Boolean verificarItemPropostaFornecedorEmAf(Integer numeroPac, Short numeroItem, ScoFornecedor fornecedor) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemAutorizacaoForn.class, "IAF");
		
		criteria.add(Restrictions.eq(ScoItemAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR_LICITACAO_ID.toString(), numeroPac));
		criteria.add(Restrictions.eq(ScoItemAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR_FORNECEDOR_ID.toString(), fornecedor.getNumero()));
		criteria.add(Restrictions.eq(ScoItemAutorizacaoForn.Fields.NUMERO_PROPOSTA.toString(), numeroItem));
		
		return executeCriteria(criteria).size() > 0;
	}
	
	public Boolean verificarPropostaEmItemAfExcluido(Integer numeroPac, Integer numeroFornecedor, Short numeroItemProposta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemAutorizacaoForn.class, "IAF");
		
		criteria.add(Restrictions.eq(ScoItemAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR_LICITACAO_ID.toString(), numeroPac));
		criteria.add(Restrictions.eq(ScoItemAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR_FORNECEDOR_ID.toString(), numeroFornecedor));
		criteria.add(Restrictions.eq(ScoItemAutorizacaoForn.Fields.NUMERO_PROPOSTA.toString(), numeroItemProposta));
		
		ScoItemAutorizacaoForn scoItemAutForn = (ScoItemAutorizacaoForn) executeCriteriaUniqueResult(criteria);
		
		if (scoItemAutForn != null){
			return scoItemAutForn.getIndExclusao() || scoItemAutForn.getIndEstorno(); 
		} else {
			return true;
		}		
	}
	
	
	public ScoItemAutorizacaoForn obterItemAFComFases(Integer numero, Short ipfNumero, Integer prfLctNumero, Integer prfFrnNumero){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemAutorizacaoForn.class,"IAF");
		criteria.createAlias("IAF."+ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), "AFN");
		criteria.createAlias("IAF."+ScoItemAutorizacaoForn.Fields.ITEM_PROPOSTA_FORNECEDOR.toString(), "IPF");
		criteria.createAlias("IPF."+ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR.toString(), "PFR");
		criteria.add(Restrictions.eq("AFN."+ScoAutorizacaoForn.Fields.NUMERO.toString(), numero));
		criteria.add(Restrictions.eq("IPF."+ScoItemPropostaFornecedor.Fields.NUMERO.toString(), ipfNumero));
		criteria.add(Restrictions.eq("PFR."+ScoPropostaFornecedor.Fields.LICITACAO_ID.toString(), prfLctNumero));
		criteria.add(Restrictions.eq("PFR."+ScoPropostaFornecedor.Fields.FORNECEDOR_ID.toString(), prfFrnNumero));
		final DetachedCriteria subCriteria = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FSC");
		subCriteria.setProjection(Projections.property(ScoFaseSolicitacao.Fields.NUMERO.toString()));
		subCriteria.add(Restrictions.eqProperty("FSC." + ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString(), "IAF."
				+ ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN_NUMERO.toString()));
		subCriteria.add(Restrictions.eqProperty("FSC."+ScoFaseSolicitacao.Fields.IAF_NUMERO.toString(), "IAF."+ScoItemAutorizacaoForn.Fields.NUMERO));
		criteria.add(Subqueries.exists(subCriteria));
		return (ScoItemAutorizacaoForn) executeCriteriaUniqueResult(criteria);
	}
	
	public ScoItemAutorizacaoForn obterItemAF(Integer numero, Short ipfNumero, Integer prfLctNumero, Integer prfFrnNumero){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemAutorizacaoForn.class,"IAF");
		criteria.createAlias("IAF."+ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), "AFN");
		criteria.createAlias("IAF."+ScoItemAutorizacaoForn.Fields.ITEM_PROPOSTA_FORNECEDOR.toString(), "IPF");
		criteria.createAlias("IPF."+ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR.toString(), "PFR");
		criteria.add(Restrictions.eq("AFN."+ScoAutorizacaoForn.Fields.NUMERO.toString(), numero));
		criteria.add(Restrictions.eq("IPF."+ScoItemPropostaFornecedor.Fields.NUMERO.toString(), ipfNumero));
		criteria.add(Restrictions.eq("PFR."+ScoPropostaFornecedor.Fields.LICITACAO_ID.toString(), prfLctNumero));
		criteria.add(Restrictions.eq("IPF."+ScoPropostaFornecedor.Fields.FORNECEDOR_ID.toString(), prfFrnNumero));
		return (ScoItemAutorizacaoForn) executeCriteriaUniqueResult(criteria);
	}
	
	public Integer obterMaxItemAF(Integer numAf){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemAutorizacaoForn.class,"IAF");
		criteria.createAlias("IAF."+ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), "AFN");
		criteria.add(Restrictions.eq("AFN."+ScoAutorizacaoForn.Fields.NUMERO.toString(), numAf));
		criteria.setProjection(Projections.max(ScoItemAutorizacaoForn.Fields.NUMERO.toString()));
		return (Integer) executeCriteriaUniqueResult(criteria);
	}
	
	public Integer obterMaxSequenciaAlteracaoAF(Integer numAf) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemAutorizacaoForn.class,"IAF");
		criteria.createAlias("IAF."+ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), "AFN");
		criteria.add(Restrictions.eq("AFN."+ScoAutorizacaoForn.Fields.NUMERO.toString(), numAf));
		criteria.setProjection(Projections.max(ScoItemAutorizacaoForn.Fields.SEQUENCIA_ALTERACAO.toString()));
		return (Integer) executeCriteriaUniqueResult(criteria);
	}
	
	public List<ScoItemAutorizacaoForn> pesquisarItemAfPorNumeroAf(Integer afnNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemAutorizacaoForn.class,"IAF");
		
		criteria.createAlias("IAF."+ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString(), "FASES", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("FASES."+ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SC."+ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias("FASES."+ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO.toString(), "SS", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SS."+ScoSolicitacaoServico.Fields.SERVICO.toString(), "SERV", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(ScoItemAutorizacaoForn.Fields.MARCA_COMERCIAL.toString(), "MARCA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoItemAutorizacaoForn.Fields.MARCA_MODELO.toString(), "MODELO", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoItemAutorizacaoForn.Fields.UMD_CODIGO_FORN.toString(), "UMD", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoItemAutorizacaoForn.Fields.UNIDADE.toString(), "UNIDADE", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoItemAutorizacaoForn.Fields.ITEM_PROPOSTA_FORNECEDOR.toString(), "IPF", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("IPF."+ScoItemPropostaFornecedor.Fields.ITEM_LICITACAO.toString(), "IL", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("IPF."+ScoItemPropostaFornecedor.Fields.MARCA_COMERCIAL.toString(), "MC", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(ScoItemAutorizacaoForn.Fields.ID_AUT_FORN_NUMERO.toString(), afnNumero));
		criteria.addOrder(Order.asc(ScoItemAutorizacaoForn.Fields.ID_AUT_FORN_NUMERO.toString()));
		return executeCriteria(criteria);		
	}
	
	
	public List<ScoItemAutorizacaoForn> pesquisarItemAfDetalhadoPorNumeroAf(Integer afnNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemAutorizacaoForn.class,"IAF");
		
		criteria.createAlias(ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString(), "FASES", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("FASES."+ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SOL_COMP", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias("SOL_COMP."+ScoSolicitacaoDeCompra.Fields.CENTRO_CUSTO.toString(), "SOL_COMP_CC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SOL_COMP."+ScoSolicitacaoDeCompra.Fields.ALMOXARIFADO.toString(), "SOL_COMP_ALMOX", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SOL_COMP."+ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "SOL_COMP_MAT", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias("FASES."+ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO.toString(), "SOL_SERV", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SOL_SERV."+ScoSolicitacaoServico.Fields.CENTRO_CUSTO.toString(), "SOL_SERV_CC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SOL_SERV."+ScoSolicitacaoServico.Fields.SERVICO.toString(), "SOL_SERV_SERVICO", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(ScoItemAutorizacaoForn.Fields.ID_AUT_FORN_NUMERO.toString(), afnNumero));
		
		return executeCriteria(criteria);
	}
	
	
	
	public List<ScoItemAutorizacaoForn> pesquisarItemAfAtivosPorNumeroAf(Integer afnNumero, Boolean filtraTipos) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemAutorizacaoForn.class,"IAF");
		criteria.createAlias(ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString(), "FASES", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoItemAutorizacaoForn.Fields.UNIDADE.toString(), "UNIDADE", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias("FASES." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SOL_COMPRAS", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SOL_COMPRAS." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MATERIAL", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias("FASES." + ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO.toString(), "SOL_SERV", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SOL_SERV." + ScoSolicitacaoServico.Fields.SERVICO.toString(), "SERVICO", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(ScoItemAutorizacaoForn.Fields.ITEM_PROPOSTA_FORNECEDOR.toString(), "IPF", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(ScoItemAutorizacaoForn.Fields.ID_AUT_FORN_NUMERO.toString(), afnNumero));
		
		if (filtraTipos) {
			List<DominioSituacaoAutorizacaoFornecimento> listaFiltroSituacoes = new ArrayList<DominioSituacaoAutorizacaoFornecimento>();						
			listaFiltroSituacoes.add(DominioSituacaoAutorizacaoFornecimento.AE);
			listaFiltroSituacoes.add(DominioSituacaoAutorizacaoFornecimento.PA);
			listaFiltroSituacoes.add(DominioSituacaoAutorizacaoFornecimento.EP);
			listaFiltroSituacoes.add(DominioSituacaoAutorizacaoFornecimento.EF);
			
			criteria.add(Restrictions.in(ScoItemAutorizacaoForn.Fields.IND_SITUACAO.toString(), listaFiltroSituacoes));
		}
		
		criteria.addOrder(Order.asc(ScoItemAutorizacaoForn.Fields.ID_AUT_FORN_NUMERO.toString()));
		
		return executeCriteria(criteria);		
	}
	
	public List<ScoItemAutorizacaoForn> pesquisarItemAfConversaoUnidade(Integer afnNumero, ScoFornecedor fornecedorPadrao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemAutorizacaoForn.class,"IAF");
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString(), "FSC", JoinType.INNER_JOIN);
		criteria.createAlias("FSC."+ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC");
		criteria.createAlias("SLC."+ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT");
		criteria.createAlias("MAT."+ScoMaterial.Fields.ESTOQUE_ALMOXARIFADO.toString(), "EAL");
		
		criteria.add(Restrictions.eq("IAF."+ScoItemAutorizacaoForn.Fields.ID_AUT_FORN_NUMERO.toString(), afnNumero));
		criteria.add(Restrictions.eq("EAL." +SceEstoqueAlmoxarifado.Fields.FORNECEDOR.toString(), fornecedorPadrao));
		
		return executeCriteria(criteria);		
	}
	
	public Object[] pesquisaContrato(ScoItemAutorizacaoFornId itemAutorizacaoFornId, Integer pGrupoMatOrtProt) {

		
		StringBuilder hql = new StringBuilder(600);
		
		hql.append("SELECT MAT." ).append( ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString() ).append(',').append( "EAL." ).append( SceEstoqueAlmoxarifado.Fields.MATERIAL_CODIGO.toString()
				).append(',').append( "EAL." ).append( SceEstoqueAlmoxarifado.Fields.NRO_SOLICITACAO_COMPRA.toString() ).append(',').append( "EAL."
				).append( SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO_SEQ.toString() ).append(',').append( "AFN." ).append( ScoAutorizacaoForn.Fields.LICITACAO_NUMERO.toString() ).append(',').append( "AFN."
				).append( ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString() ).append(',').append( "FSC3." ).append( ScoFaseSolicitacao.Fields.ITL_NUMERO.toString() ).append(',').append( "IAF."
				).append( ScoItemAutorizacaoForn.Fields.VALOR_UNITARIO.toString() ).append(' ').append( "FROM " ).append( ScoItemAutorizacaoForn.class.getSimpleName() ).append( " IAF,"
				).append( ScoFaseSolicitacao.class.getSimpleName() ).append( " FSC," ).append( ScoSolicitacaoDeCompra.class.getSimpleName() ).append( " SLC," ).append( ScoMaterial.class.getSimpleName()
				).append( " MAT," ).append( SceEstoqueAlmoxarifado.class.getSimpleName() ).append( " EAL," ).append( ScoFaseSolicitacao.class.getSimpleName() ).append( " FSC2,"
				).append( ScoFaseSolicitacao.class.getSimpleName() ).append( " FSC3," ).append( ScoAutorizacaoForn.class.getSimpleName() ).append( " AFN " ).append( "WHERE FSC."
				).append( ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString() ).append( "= :AFN_NUMERO" ).append( "  AND FSC." ).append( ScoFaseSolicitacao.Fields.IAF_NUMERO.toString()
				).append( "= :NUMERO" ).append( "  AND FSC." ).append( ScoFaseSolicitacao.Fields.SLC_NUMERO.toString() ).append( " = SLC." ).append( ScoSolicitacaoDeCompra.Fields.NUMERO.toString()
				).append( "  AND SLC." ).append( ScoSolicitacaoDeCompra.Fields.MAT_CODIGO.toString() ).append( " = MAT." ).append( ScoMaterial.Fields.CODIGO.toString() ).append( "  AND MAT."
				).append( ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString() ).append( " <> :GRUPO_MATERIAL" ).append( "  AND EAL."
				).append( SceEstoqueAlmoxarifado.Fields.MATERIAL_CODIGO.toString() ).append( " = MAT." ).append( ScoMaterial.Fields.CODIGO.toString() ).append( "  AND EAL."
				).append( SceEstoqueAlmoxarifado.Fields.NRO_SOLICITACAO_COMPRA.toString() ).append( " <> FSC." ).append( ScoFaseSolicitacao.Fields.SLC_NUMERO.toString() ).append( "  AND FSC2."
				).append( ScoFaseSolicitacao.Fields.SLC_NUMERO.toString() ).append( " = EAL." ).append( SceEstoqueAlmoxarifado.Fields.NRO_SOLICITACAO_COMPRA.toString() ).append( "  AND FSC2."
				).append( ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString() ).append( " = :IND_EXCLUSAO_FSC2" ).append( "  AND FSC3." ).append( ScoFaseSolicitacao.Fields.SLC_NUMERO.toString()
				).append( " = EAL." ).append( SceEstoqueAlmoxarifado.Fields.NRO_SOLICITACAO_COMPRA.toString() ).append( "  AND FSC3." ).append( ScoFaseSolicitacao.Fields.LCT_NUMERO.toString()
				).append( " IS NOT NULL " ).append( "  AND AFN." ).append( ScoAutorizacaoForn.Fields.NUMERO.toString() ).append( " = FSC2." ).append( ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()
				).append( "  AND IAF." ).append( ScoItemAutorizacaoForn.Fields.ID_AUT_FORN_NUMERO.toString() ).append( " = FSC2." ).append( ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()
				).append( "  AND IAF." ).append( ScoItemAutorizacaoForn.Fields.NUMERO.toString() ).append( " = FSC2." ).append( ScoFaseSolicitacao.Fields.IAF_NUMERO.toString() ).append( "  AND IAF."
				).append( ScoItemAutorizacaoForn.Fields.IND_EXCLUSAO.toString() ).append( " = :IND_EXCLUSAO_IAF" ).append( "  AND IAF."
				).append( ScoItemAutorizacaoForn.Fields.IND_SITUACAO.toString() ).append( " <> :IND_SITUACAO_IAF" ).append( "  AND IAF."
				).append( ScoItemAutorizacaoForn.Fields.IND_CONTRATO.toString() ).append( " = :IND_CONTRATO_IAF");			
				
		Query query = this.createQuery(hql.toString());
		query.setParameter("AFN_NUMERO", itemAutorizacaoFornId.getAfnNumero());
		query.setParameter("NUMERO", itemAutorizacaoFornId.getNumero());
		query.setParameter("GRUPO_MATERIAL", pGrupoMatOrtProt);
		query.setParameter("IND_EXCLUSAO_FSC2", Boolean.FALSE);
		query.setParameter("IND_EXCLUSAO_IAF", Boolean.FALSE);
		query.setParameter("IND_SITUACAO_IAF", DominioSituacaoAutorizacaoFornecedor.EX);
		query.setParameter("IND_CONTRATO_IAF", Boolean.TRUE);
		query.setFirstResult(0);
		query.setMaxResults(1);
		
		Object [] resultado = null;
		if (query.getResultList().size() > 0){
			resultado = (Object[]) query.getResultList().get(0);
		}
		return resultado;
		
	}
	
	public Long verificaItensSituacaoAEPA(Integer afnNumero, Integer numero){
		
        DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemAutorizacaoForn.class,"IAF");
		
		criteria.add(Restrictions.eq(ScoItemAutorizacaoForn.Fields.ID_AUT_FORN_NUMERO.toString(), afnNumero));
		criteria.add(Restrictions.ne(ScoItemAutorizacaoForn.Fields.NUMERO.toString(), numero));
		
		List<DominioSituacaoAutorizacaoFornecimento> listaFiltroSituacoes = new ArrayList<DominioSituacaoAutorizacaoFornecimento>();						
		listaFiltroSituacoes.add(DominioSituacaoAutorizacaoFornecimento.AE);
		listaFiltroSituacoes.add(DominioSituacaoAutorizacaoFornecimento.PA);		
		
		criteria.add(Restrictions.in(ScoItemAutorizacaoForn.Fields.IND_SITUACAO.toString(), listaFiltroSituacoes));
		
		return executeCriteriaCount(criteria);
	}
	
	/**
	 * Pesquisa um item de AF pela solicitacao de compra
	 * @param slcNumero
	 * @param filtraContrato
	 * @param filtraSituacao
	 * @return ScoItemAutorizacaoForn
	 */
	public ScoItemAutorizacaoForn obterItemAfPorSolicitacaoCompra(Integer slcNumero, Boolean filtraContrato, Boolean filtraSituacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemAutorizacaoForn.class,"IAF");
		criteria.createAlias("IAF."+ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString(), "FS", JoinType.INNER_JOIN);
		criteria.createAlias("IAF."+ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), "AFN", JoinType.INNER_JOIN);
		criteria.createAlias("AFN."+ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString(), "PROP", JoinType.INNER_JOIN);
		criteria.createAlias("FS."+ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC", JoinType.INNER_JOIN);
		
		criteria.add(Restrictions.eq("FS."+ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		criteria.add(Restrictions.eq("SLC."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), slcNumero));
		
		if (filtraContrato) {
			criteria.add(Restrictions.eq("IAF."+ScoItemAutorizacaoForn.Fields.IND_CONTRATO.toString(), Boolean.TRUE));
		}
		
		if (filtraSituacao) {
			List<DominioSituacaoAutorizacaoFornecimento> listaFiltroSituacoes = new ArrayList<DominioSituacaoAutorizacaoFornecimento>();						
			listaFiltroSituacoes.add(DominioSituacaoAutorizacaoFornecimento.AE);
			listaFiltroSituacoes.add(DominioSituacaoAutorizacaoFornecimento.PA);		
			
			criteria.add(Restrictions.in(ScoItemAutorizacaoForn.Fields.IND_SITUACAO.toString(), listaFiltroSituacoes));
		}
		
		return (ScoItemAutorizacaoForn) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * Pesquisa um item de AF pela solicitacao de servico
	 * @param slsNumero
	 * @param filtraContrato
	 * @param filtraSituacao
	 * @return ScoItemAutorizacaoForn
	 */

	public ScoItemAutorizacaoForn obterItemAfPorSolicitacaoServico(Integer slsNumero, Boolean filtraContrato, Boolean filtraSituacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemAutorizacaoForn.class,"IAF");
		criteria.createAlias("IAF."+ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString(), "FS", JoinType.INNER_JOIN);
		criteria.createAlias("FS."+ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO.toString(), "SLS", JoinType.INNER_JOIN);

		criteria.add(Restrictions.eq("FS."+ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		criteria.add(Restrictions.eq("SLS."+ScoSolicitacaoServico.Fields.NUMERO.toString(), slsNumero));

		if (filtraContrato) {
			criteria.add(Restrictions.eq("IAF."+ScoItemAutorizacaoForn.Fields.IND_CONTRATO.toString(), Boolean.TRUE));
		}

		if (filtraSituacao) {
			List<DominioSituacaoAutorizacaoFornecimento> listaFiltroSituacoes = new ArrayList<DominioSituacaoAutorizacaoFornecimento>();						
			listaFiltroSituacoes.add(DominioSituacaoAutorizacaoFornecimento.AE);
			listaFiltroSituacoes.add(DominioSituacaoAutorizacaoFornecimento.PA);		

			criteria.add(Restrictions.in(ScoItemAutorizacaoForn.Fields.IND_SITUACAO.toString(), listaFiltroSituacoes));
		}

		return (ScoItemAutorizacaoForn) executeCriteriaUniqueResult(criteria);
	}
	
	// ATENCAO: a tabela sce_registra_entrega_mat nao existe em bases postgres por
	// tratar-se de solucao temporaria para um problema ja resolvido pelo recebimento 
	// de materiais da versao 5.0 do AGHU porem que ainda nao esta implantado no HCPA.
	// Esta consulta deve ser removida quando o recebimento estiver implantado no HCPA. 
	// Exclusivamente por causa disto esta query foi feita assim e nao por criteria.
	// amenegotto em 02/07/2014.
	public Integer obterQtdeEntregaPendente(Integer codMat) {
		Integer ret = 0;
		
		final StringBuilder sql = new StringBuilder(100);
		
		if(isOracle()) {
			sql.append("SELECT NVL(SUM(remat.qtde_entregue),0) qtde_pend_entrega ");
			sql.append("FROM sco_itens_autorizacao_forn iaf, ");
			sql.append("  sco_fases_solicitacoes fsc, ");
			sql.append("  sco_solicitacoes_de_compras slc, ");
			sql.append("  sco_materiais mat, ");
			sql.append("  sce_registra_entrega_mat remat ");
			sql.append("WHERE fsc.iaf_afn_numero        = iaf.afn_numero ");
			sql.append("AND fsc.iaf_numero              = iaf.numero ");
			sql.append("AND fsc.ind_exclusao            = 'N' ");
			sql.append("AND slc.numero                  = fsc.slc_numero ");
			sql.append("AND mat.codigo                  = slc.mat_codigo ");
			sql.append("AND iaf.afn_numero              = remat.iaf_afn_numero ");
			sql.append("AND iaf.numero                  = remat.iaf_numero ");
			sql.append("AND NVL(remat.ind_recebido,'N') = 'N' ");
			sql.append("AND slc.mat_codigo = ").append(codMat);
			
			javax.persistence.Query query = this.createNativeQuery(sql.toString());
			if (!query.getResultList().isEmpty()) {
				List<BigDecimal> result = (List<BigDecimal>) query.getResultList();
				if (result != null && !result.isEmpty()) {
					ret = result.get(0).intValue();
				}
			}
		}
		return ret;
	}
	
	
	public Integer obterQuantidadeEslPendenteAF(Integer afnNumero, Integer numero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemAutorizacaoForn.class, "IAF");
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.ITEM_ENTR_SAID_SEM_LICITACAO.toString(), "IE");

		criteria.setProjection(Projections.sum("IE." + SceItemEntrSaidSemLicitacao.Fields.QUANTIDADE.toString()));

		criteria.add(Restrictions.eq("IE." + SceItemEntrSaidSemLicitacao.Fields.IND_ENCERRADO.toString(), Boolean.FALSE));
		criteria.add(Restrictions.eq("IAF." + ScoItemAutorizacaoForn.Fields.ID_AUT_FORN_NUMERO.toString(), afnNumero));
		criteria.add(Restrictions.eq("IAF." + ScoItemAutorizacaoForn.Fields.NUMERO.toString(), numero));

		return (Integer) executeCriteriaUniqueResult(criteria);
	}
	
	public Long obterQuantidadeItensNaoEfetivados(Integer afnNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemAutorizacaoForn.class);
		criteria.add(Restrictions.eq(ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN_NUMERO.toString(), afnNumero));
		criteria.add(Restrictions.in(ScoItemAutorizacaoForn.Fields.IND_SITUACAO.toString(), new DominioSituacaoAutorizacaoFornecedor[] {
				DominioSituacaoAutorizacaoFornecedor.AE, DominioSituacaoAutorizacaoFornecedor.PA }));

		return executeCriteriaCount(criteria);
	}

	public Long obterQuantidadeItensAutomaticos(Integer afnNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemAutorizacaoForn.class);
		criteria.add(Restrictions.eq(ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN_NUMERO.toString(), afnNumero));
		criteria.add(Restrictions.eq(ScoItemAutorizacaoForn.Fields.IND_PROGR_ENTG_AUTO.toString(), true));

		return executeCriteriaCount(criteria);
	}
	
	public Long obterQuantidadeItensParaProgramacao(Integer afnNumero, Integer fornecedorPadrao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemAutorizacaoForn.class, "IAF");
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), "AFN", JoinType.INNER_JOIN);
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString(), "FSC", JoinType.INNER_JOIN);
		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC", JoinType.INNER_JOIN);
		criteria.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT", JoinType.INNER_JOIN);
		criteria.createAlias("MAT." + ScoMaterial.Fields.ESTOQUE_ALMOXARIFADO.toString(), "EAL", JoinType.INNER_JOIN);

		criteria.add(Restrictions.eq("AFN." + ScoAutorizacaoForn.Fields.NUMERO.toString(), afnNumero));
		criteria.add(Restrictions.eq("AFN." + ScoAutorizacaoForn.Fields.IND_ENTREGA_PROGRAMADA.toString(), true));
		criteria.add(Restrictions.eq("IAF." + ScoItemAutorizacaoForn.Fields.IND_PROGR_ENTG_AUTO.toString(), true));
		criteria.add(Restrictions.in("IAF." + ScoItemAutorizacaoForn.Fields.IND_SITUACAO.toString(), new DominioSituacaoAutorizacaoFornecedor[] {
				DominioSituacaoAutorizacaoFornecedor.AE, DominioSituacaoAutorizacaoFornecedor.PA }));
		criteria.add(Restrictions.eq("EAL." + SceEstoqueAlmoxarifado.Fields.IND_ESTOCAVEL.toString(), true));
		criteria.add(Restrictions.eq("EAL." + SceEstoqueAlmoxarifado.Fields.NUMERO_FORNECEDOR.toString(), fornecedorPadrao));
		criteria.add(Restrictions.eqProperty("EAL." + SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO.toString(), "MAT." + ScoMaterial.Fields.ALMOXARIFADO.toString()));

		DetachedCriteria criteriaEstoqueGeral = DetachedCriteria.forClass(SceEstoqueGeral.class, "EGR");
		criteriaEstoqueGeral.add(Restrictions.eqProperty("MAT." + ScoMaterial.Fields.CODIGO.toString(), "EGR." + SceEstoqueGeral.Fields.MAT_CODIGO.toString()));
		criteriaEstoqueGeral.add(Restrictions.eqProperty("EGR." + SceEstoqueGeral.Fields.FRN_NUMERO.toString(), "EAL."
				+ SceEstoqueAlmoxarifado.Fields.NUMERO_FORNECEDOR.toString()));
		Calendar mesAtual = Calendar.getInstance();
		mesAtual.set(Calendar.DAY_OF_MONTH, 1);
		criteriaEstoqueGeral.add(Restrictions.eq("EGR." + SceEstoqueGeral.Fields.DATA_COMPETENCIA.toString(), DateUtil.truncaData(mesAtual.getTime())));
		criteriaEstoqueGeral.setProjection(Projections.projectionList().add(Projections.property("EGR." + SceEstoqueGeral.Fields.MAT_CODIGO.toString())));
		criteria.add(Subqueries.exists(criteriaEstoqueGeral));

		return executeCriteriaCount(criteria);
	}

	public List<Integer> obterFatorConversao(Integer afnNumero, Date previsaoEntrega) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemAutorizacaoForn.class, "IAF");
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.PROG_ENTREGAS.toString(), "PEA", JoinType.INNER_JOIN);
		String sqlCase  = " case when {alias}.FATOR_CONVERSAO_FORN IS NULL" 
						+  "                 THEN (QTDE / 1)-TRUNC(QTDE / 1)"
						+  "                 ELSE (QTDE / {alias}.FATOR_CONVERSAO_FORN)-TRUNC(QTDE / {alias}.FATOR_CONVERSAO_FORN)"
						+  " END AS fatorConversao";
		criteria.setProjection(Projections.sqlProjection(sqlCase, new String[]{"fatorConversao"}, new Type[] { IntegerType.INSTANCE }));
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IAF_AFN_NUMERO.toString(), afnNumero));
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA.toString(), false));
		criteria.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_PLANEJAMENTO.toString(), false));
		criteria.add(Restrictions.lt("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString(), DateUtil.truncaData(previsaoEntrega)));
		criteria.add(Restrictions.sqlRestriction("COALESCE(qtde_entregue,0) < qtde"));

		return executeCriteria(criteria);
	}
	
	public List<AutFornEntregaProgramadaVO> obtemAutFornecimentosEntregasProgramadas(Integer gmtCodigo, Integer frnNumero, Date dataEntregaInicial,
			Date dataEntregaInicialParametro, Date dataEntregaFinal, Date dataEntregaFinalParametro) {

		AutFornecimentoEntregaProgramadaQueryBuilder builder = new AutFornecimentoEntregaProgramadaQueryBuilder();
		List<AutFornEntregaProgramadaVO> listagem = executeCriteria(builder.build(gmtCodigo, frnNumero, dataEntregaInicial, dataEntregaInicialParametro,
				dataEntregaFinal, dataEntregaFinalParametro));

		return listagem;
	}
	
	private DetachedCriteria montaSubQueryBasica(Integer afnNumero, Integer iafNumero, Date dataEntregaInicial, Date dataEntregaInicialParametro,
			Date dataEntregaFinal, Date dataEntregaFinalParametro) {

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
	
		DetachedCriteria c = DetachedCriteria.forClass(ScoItemAutorizacaoForn.class, "IAF");

		c.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.PROG_ENTREGAS.toString(), "PEA", JoinType.INNER_JOIN);
		c.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), "AFN", JoinType.INNER_JOIN);
		c.add(Restrictions.between("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString(), dataInicial, dataFinal));

		c.add(Restrictions.sqlRestriction("coalesce(qtde_entregue,0) < qtde"));
		c.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_CANCELADA.toString(), false));
		c.add(Restrictions.eq("AFN." + ScoAutorizacaoForn.Fields.NUMERO.toString(), afnNumero));

		if (iafNumero != null) {
			c.add(Restrictions.eq("IAF." + ScoItemAutorizacaoForn.Fields.NUMERO.toString(), iafNumero));
		}

		ProjectionList p = Projections.projectionList();
		p.add(Projections.sum("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.VALOR_TOTAL.toString()));
		p.add(Projections.sum("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.VALOR_EFETIVADO.toString()));
		c.setProjection(p);
		return c;
	}
		
	private BigDecimal mostraResultado(DetachedCriteria c) {
		Object[] valores = (Object[]) executeCriteriaUniqueResult(c);
		BigDecimal valorTotal = BigDecimal.ZERO;
		BigDecimal valorEfetivado = BigDecimal.ZERO;

		if (valores[0] != null) {
			valorTotal = new BigDecimal((Double) valores[0]);
		}
		if (valores[1] != null) {
			valorEfetivado = new BigDecimal((Double) valores[1]);
		}

		return valorTotal.subtract(valorEfetivado);
	}
	
	public BigDecimal obtemValorSaldoProgramado(Integer afnNumero, Integer iafNumero, Date dataEntregaInicial, Date dataEntregaInicialParametro,
			Date dataEntregaFinal, Date dataEntregaFinalParametro) {

		DetachedCriteria c = montaSubQueryBasica(afnNumero, iafNumero, dataEntregaInicial, dataEntregaInicialParametro, dataEntregaFinal, dataEntregaFinalParametro);

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 1);
		// Condição devido ao uso do trunc
		c.add(Restrictions.gt("AFN." + ScoAutorizacaoForn.Fields.DT_VENCTO_CONTRATO.toString(), DateUtil.truncaData(calendar.getTime())));

		return mostraResultado(c);
	}
	
	public BigDecimal obtemValorLiberar(Integer afnNumero, Integer iafNumero, Date dataEntregaInicial, Date dataEntregaInicialParametro, Date dataEntregaFinal,
			Date dataEntregaFinalParametro) {
		DetachedCriteria c = montaSubQueryBasica(afnNumero, iafNumero, dataEntregaInicial, dataEntregaInicialParametro, dataEntregaFinal, dataEntregaFinalParametro);

		c.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ASSINATURA.toString(), false));
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 1);
		// Condição devido ao uso do trunc
		c.add(Restrictions.gt("AFN." + ScoAutorizacaoForn.Fields.DT_VENCTO_CONTRATO.toString(), DateUtil.truncaData(calendar.getTime())));

		return mostraResultado(c);

	}
	
	public BigDecimal obtemValorLiberado(Integer afnNumero, Integer iafNumero, Date dataEntregaInicial, Date dataEntregaInicialParametro, Date dataEntregaFinal,
			Date dataEntregaFinalParametro) {
		DetachedCriteria c = montaSubQueryBasica(afnNumero, iafNumero, dataEntregaInicial, dataEntregaInicialParametro, dataEntregaFinal, dataEntregaFinalParametro);

		c.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ASSINATURA.toString(), true));
		c.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_PLANEJAMENTO.toString(), true));

		return mostraResultado(c);

	}

	public BigDecimal obtemValorAtraso(Integer afnNumero, Integer iafNumero, Date dataEntregaInicial, Date dataEntregaInicialParametro, Date dataEntregaFinal,
			Date dataEntregaFinalParametro) {
		DetachedCriteria c = montaSubQueryBasica(afnNumero, iafNumero, dataEntregaInicial, dataEntregaInicialParametro, dataEntregaFinal, dataEntregaFinalParametro);

		c.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_ASSINATURA.toString(), true));
		c.add(Restrictions.eq("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.IND_PLANEJAMENTO.toString(), true));

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, 1);

		c.add(Restrictions.lt("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.DT_PREV_ENTREGA.toString(), DateUtil.truncaData(calendar.getTime())));

		return mostraResultado(c);
	}
	
	public List<ItemAutFornEntregaProgramadaVO> obtemItemAutFornecimentosEntregasProgramadas(Integer afNumero, Date dataInicial, Date vlrDataInicial,
			Date dataFinal, Date vlrDataFinal) {
		ItemAutFornecimentoEntregaProgramadaQueryBuilder builder = new ItemAutFornecimentoEntregaProgramadaQueryBuilder();
		List<ItemAutFornEntregaProgramadaVO> listagem = executeCriteria(builder.build(afNumero, dataInicial, vlrDataInicial, dataFinal, vlrDataFinal));

		return listagem;
	}
	
	
	/**
	 * Lista programacao entrega itens AF
	 * #5565 - C1
	 * @param numeroAF
	 * @return
	 */
	public List<ProgramacaoEntregaItemAFVO> listarProgramacaoEntregaItensAF(Integer numeroAF, Integer numeroFornecedorPadrao) {
		ProgramacaoEntregaItemAFQueryBuilder queryBuilder = new ProgramacaoEntregaItemAFQueryBuilder();
		queryBuilder.setNroAF(numeroAF);
		queryBuilder.setNumeroFornecedorPadrao(numeroFornecedorPadrao);
		DetachedCriteria criteria = queryBuilder.build();

		return executeCriteria(criteria);
	}
	
	/**
	 * Busca quantidade AFs programadas * #5565 - C2
	 * 
	 * @param codigoMaterial
	 * @param afnNumero
	 * @return
	 */
	public Integer buscarQuantidadeAFsProgramadas(Integer codigoMaterial, Integer afnNumero) {
		QuantidadeOutrasAFsProgramadasQueryBuilder queryBuilder = new QuantidadeOutrasAFsProgramadasQueryBuilder();
		queryBuilder.setAfnNumero(afnNumero);
		queryBuilder.setCodigoMaterial(codigoMaterial);
		DetachedCriteria criteria = queryBuilder.build();

		return (Integer) executeCriteriaUniqueResult(criteria);
	}
	
	// #26773 C1
	public Boolean verificarItemConsumoDiretoSaldoAFNaoProg(Integer numeroAF) {
		DetachedCriteria criteria = criarCriteriaParcelasEntregaMatDireto(numeroAF);
		criteria.add(Restrictions.sqlRestriction("(({alias}.QTDE_SOLICITADA - COALESCE({alias}.QTDE_RECEBIDA,0)) > " + criarSubQueryQtdMenosQtdEntregue()));
		return executeCriteriaCount(criteria) > 0;
	}
	
	private DetachedCriteria criarCriteriaParcelasEntregaMatDireto(Integer numeroAF) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemAutorizacaoForn.class, "IAF");
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString(), "FSC", JoinType.INNER_JOIN);
		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC", JoinType.INNER_JOIN);
		criteria.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT", JoinType.INNER_JOIN);
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.PROG_ENTREGAS.toString(), "PEA");
		criteria.add(Restrictions.eq("IAF." + ScoItemAutorizacaoForn.Fields.ID_AUT_FORN_NUMERO.toString(), numeroAF));
		DominioSituacaoAutorizacaoFornecimento[] situacoes = { DominioSituacaoAutorizacaoFornecimento.AE, DominioSituacaoAutorizacaoFornecimento.PA };
		criteria.add(Restrictions.in("IAF." + ScoItemAutorizacaoForn.Fields.IND_SITUACAO.toString(), situacoes));
		criteria.add(Restrictions.eq("MAT." + ScoMaterial.Fields.IND_ESTOCAVEL.toString(), Boolean.FALSE));
		return criteria;
	}
	
	private String criarSubQueryQtdMenosQtdEntregue() {
		StringBuilder query = new StringBuilder(300);
		query.append(" (SELECT COALESCE(SUM(QTDE - COALESCE(QTDE_ENTREGUE,0)),0) ");
		query.append(" FROM AGH.SCO_PROGR_ENTREGA_ITENS_AF ");
		query.append(" WHERE IAF_AFN_NUMERO = {alias}.AFN_NUMERO ");
		query.append(" AND IAF_NUMERO     = {alias}.NUMERO ");
		query.append(" AND IND_CANCELADA = 'N' )) ");
		return query.toString();
	}
	
	// #26773 C2
	public List<ParcelasEntregaMatDiretoVO> buscarItensAFMaterialDireto(Integer iafAfnNumero) {
		DetachedCriteria criteria = criarCriteriaParcelasEntregaMatDireto(iafAfnNumero);
		criteria.setProjection(Projections
				.projectionList()
				.add(Projections.property("IAF." + ScoItemAutorizacaoForn.Fields.ID_AUT_FORN_NUMERO.toString()), "iafAfnNumero")
				.add(Projections.property("IAF." + ScoItemAutorizacaoForn.Fields.NUMERO.toString()), "iafNumero")
				.add(Projections.sqlProjection("({alias}.QTDE_SOLICITADA - COALESCE({alias}.QTDE_RECEBIDA,0)) as qtdSaldoItem", new String[] { "qtdSaldoItem" },
						new Type[] { IntegerType.INSTANCE }), "qtdSaldoItem")
				.add(Projections.property("IAF." + ScoItemAutorizacaoForn.Fields.VALOR_UNITARIO.toString()), "iafValorUnitario")
				.add(Projections.property("MAT." + ScoMaterial.Fields.CODIGO.toString()), "matCodigo")
				.add(Projections.property("SLC." + ScoSolicitacaoDeCompra.Fields.NUMERO.toString()), "sclNumero")
				.add(Projections.property("IAF." + ScoItemAutorizacaoForn.Fields.UNIDADE_CODIGO.toString()), "iafUmdCod")
				.add(Projections.property("IAF." + ScoItemAutorizacaoForn.Fields.QUANTIDADE_SOLICITADA.toString()), "iafQtdSolicitada")
				.add(Projections.property("MAT." + ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString()), "codGrupoMaterial")
				.add(Projections.property("IAF." + ScoItemAutorizacaoForn.Fields.IND_CONTRATO.toString()), "iafIndContrato")
				.add(Projections.property("IAF." + ScoItemAutorizacaoForn.Fields.IND_CONSIGNADO.toString()), "iafIndConsignado")
				.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.PARCELA.toString()), "qtdParcelaZero")
				.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.PARCELA.toString()), "numeroParcela")
				.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.SEQ.toString()), "seq")
				.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.PARCELA.toString()), "qtdSaldo")
				.add(Projections.property("PEA." + ScoProgEntregaItemAutorizacaoFornecimento.Fields.PARCELA.toString()), "qtdParcela"));

		criteria.setResultTransformer(Transformers.aliasToBean(ParcelasEntregaMatDiretoVO.class));
		criteria.addOrder(Order.asc("IAF." + ScoItemAutorizacaoForn.Fields.NUMERO.toString()));
		return executeCriteria(criteria);
	}

	public List<EntregaProgramadaGrupoMaterialVO> obtemEntregaGlobalPorGrupoMaterial(Integer gmtCodigo, Integer frnNumero, Boolean materialEstocavel,
			Date dataEntregaInicial, Date dataEntregaInicialParametro, Date dataEntregaFinal, Date dataEntregaFinalParametro, Date dataLiberacao) {
		EntregaGlobalGrupoMaterialQueryBuilder builder = new EntregaGlobalGrupoMaterialQueryBuilder();
		List<EntregaProgramadaGrupoMaterialVO> listagem = executeCriteria(builder.build(gmtCodigo, materialEstocavel, dataEntregaInicial,
				dataEntregaInicialParametro, dataEntregaFinal, dataEntregaFinalParametro));

		for (EntregaProgramadaGrupoMaterialVO vo : listagem) {
			vo.setSaldoProgramado(criaValor(vo.getGmtCodigo(), frnNumero, materialEstocavel, dataEntregaInicial, dataEntregaInicialParametro, dataEntregaFinal,
					dataEntregaFinalParametro, new GrupoMaterialSaldoProgramadoQueryBuilder(), dataLiberacao));

			vo.setValorALiberar(criaValor(vo.getGmtCodigo(), frnNumero, materialEstocavel, dataEntregaInicial, dataEntregaInicialParametro, dataEntregaFinal,
					dataEntregaFinalParametro, new GrupoMaterialValorLiberarQueryBuilder(), dataLiberacao));

			vo.setValorLiberado(criaValor(vo.getGmtCodigo(), frnNumero, materialEstocavel, dataEntregaInicial, dataEntregaInicialParametro, dataEntregaFinal,
					dataEntregaFinalParametro, new GrupoMaterialValorLiberadoQueryBuilder(), dataLiberacao));

			vo.setValorEmAtraso(criaValor(vo.getGmtCodigo(), frnNumero, materialEstocavel, dataEntregaInicial, dataEntregaInicialParametro, dataEntregaFinal,
					dataEntregaFinalParametro, new GrupoMaterialValorAtrasoQueryBuilder(), dataLiberacao));
		}
	
		List<EntregaProgramadaGrupoMaterialVO> listagemNova = new ArrayList<EntregaProgramadaGrupoMaterialVO>();
		for (EntregaProgramadaGrupoMaterialVO entregaProgramadaGrupoMaterialVO : listagem) {
			if (entregaProgramadaGrupoMaterialVO.getSaldoProgramado().doubleValue() > 0 || entregaProgramadaGrupoMaterialVO.getValorALiberar().doubleValue() > 0
					|| entregaProgramadaGrupoMaterialVO.getValorLiberado().doubleValue() > 0
					|| entregaProgramadaGrupoMaterialVO.getValorEmAtraso().doubleValue() > 0) {
				listagemNova.add(entregaProgramadaGrupoMaterialVO);
			}
		}

		return listagemNova;
	}
		
	@SuppressWarnings("PMD.UnusedPrivateMethod")
	private BigDecimal criaValor(Integer gmtCodigo, Integer frnNumero, Boolean materialEstocavel, Date dataEntregaInicial, Date dataEntregaInicialParametro,
			Date dataEntregaFinal, Date dataEntregaFinalParametro, GrupoMaterialValoresProgramadosQueryBuilder builder, Date dataLiberacao) {
		ValoresProgramadosVO vo = (ValoresProgramadosVO) executeCriteriaUniqueResult(builder.build(gmtCodigo, materialEstocavel, frnNumero, dataEntregaInicial,
				dataEntregaInicialParametro, dataEntregaFinal, dataEntregaFinalParametro, dataLiberacao));
		if (vo != null) {
			if (vo.getValorTotal() == null) {
				vo.setValorTotal(0d);
			}
			if (vo.getValorEfetivado() == null) {
				vo.setValorEfetivado(0d);
			}
			return new BigDecimal(vo.getValorTotal()).subtract(new BigDecimal(vo.getValorEfetivado()));
		}
		return BigDecimal.ZERO;
	}
	
	public List<ExcluirProgramacaoEntregaItemAFVO> buscarProgramacaoItensAFExclusao(Integer afnNumero) {
		ProgramacaoItensAFQueryBuilder queryBuilder = new ProgramacaoItensAFQueryBuilder();
		queryBuilder.setNumeroAF(afnNumero);
		DetachedCriteria criteria = queryBuilder.build();
		return executeCriteria(criteria);
	}
	
	/**
	 * Consultar AFs associadas ao material e que possuam saldo
	 */
	public List<ScoItemAutorizacaoForn> listarAfsAssociadasComSaldo(Integer codigoMaterial, Integer codigoCCT, Integer centroCustoAplic, Integer CentroCustoSolic) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemAutorizacaoForn.class, "IAF");
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), "AFN", JoinType.INNER_JOIN);
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString(), "FSC", JoinType.INNER_JOIN);
		criteria.createAlias("FSC." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC", JoinType.INNER_JOIN);
		criteria.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT", JoinType.INNER_JOIN);
		criteria.add(Restrictions.or(Restrictions.eq("AFN." + ScoAutorizacaoForn.Fields.MODALIDADE_EMPENHO.toString(), DominioModalidadeEmpenho.ESTIMATIVA),
				Restrictions.eq("AFN." + ScoAutorizacaoForn.Fields.MODALIDADE_EMPENHO.toString(), DominioModalidadeEmpenho.CONTRATO)));
		criteria.add(Restrictions.or(
				Restrictions.and(
						Restrictions.gtProperty("IAF." + ScoItemAutorizacaoForn.Fields.QUANTIDADE_SOLICITADA.toString(), "IAF."
								+ ScoItemAutorizacaoForn.Fields.QUANTIDADE_RECEBIDA.toString()),
						Restrictions.isNotNull("IAF." + ScoItemAutorizacaoForn.Fields.QUANTIDADE_RECEBIDA.toString())),
				Restrictions.and(Restrictions.gt("IAF." + ScoItemAutorizacaoForn.Fields.QUANTIDADE_SOLICITADA.toString(), 0),
						Restrictions.isNull("IAF." + ScoItemAutorizacaoForn.Fields.QUANTIDADE_RECEBIDA.toString()))));
		criteria.add(Restrictions.or(
				Restrictions.eq("SLC." + ScoSolicitacaoDeCompra.Fields.CCT_CODIGO.toString(), codigoCCT),
				Restrictions.or(Restrictions.eq("SLC." + ScoSolicitacaoDeCompra.Fields.CCT_CODIGO.toString(), centroCustoAplic),
						Restrictions.eq("SLC." + ScoSolicitacaoDeCompra.Fields.CCT_CODIGO.toString(), CentroCustoSolic))));
		criteria.add(Restrictions.eq("MAT." + ScoMaterial.Fields.CODIGO.toString(), codigoMaterial));
		criteria.add(Restrictions.lt("AFN." + ScoAutorizacaoForn.Fields.DT_VENCTO_CONTRATO.toString(), new Date()));
		criteria.add(Restrictions.isNotNull("AFN." + ScoAutorizacaoForn.Fields.NRO_CONTRATO.toString()));
		criteria.addOrder(Order.asc("AFN." + ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString()));
		criteria.addOrder(Order.asc("AFN." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString()));
		return executeCriteria(criteria);
	}
	
	/**
	 * Consulta principal para listar programacao entrega global de fornecedores #24885 - C1
	 * @param fornecedor 
	 * 
	 * @return
	 */
	public List<ProgramacaoEntregaGlobalFornecedoresVO> listarProgramacaoEntregaGlobalFornecedores(Integer codigoGrupoMaterial, Date dataInicial, Date dataFinal,
			EntregasGlobaisAcesso tipoValorEnum, ScoFornecedor fornecedor) {
		ProgramacaoEntregaGlobalFornecedorQueryBuilder queryBuilder = new ProgramacaoEntregaGlobalFornecedorQueryBuilder();
		queryBuilder.setCodigoGrupoMaterial(codigoGrupoMaterial);
		queryBuilder.setDataInicial(dataInicial);
		queryBuilder.setDataFinal(dataFinal);
		queryBuilder.setTipoValorEnum(tipoValorEnum);
		queryBuilder.setNrFornecedor(fornecedor!=null?fornecedor.getNumero():null);
		DetachedCriteria criteria = queryBuilder.build();
		return executeCriteria(criteria);
	}
	
	/**
	 * ScoItemAutorizacaoFornDAO Gera subqueries para listar programacao entrega global de fornecedores #24885 - C1
	 * 
	 * @return
	 */
	public BigDecimal obterProgramacaoEntregaGlobalFornecedoresTipoValor(Integer codigoGrupoMaterial, Date dataInicial, Date dataFinal, Integer numeroFornecedor,
			EntregasGlobaisAcesso tipoValorEnum) {
		ProgramacaoEntregaGlobalFornecedorSubQueryBuilder queryBuilder = new ProgramacaoEntregaGlobalFornecedorSubQueryBuilder();
		queryBuilder.setCodigoGrupoMaterial(codigoGrupoMaterial);
		queryBuilder.setDataInicial(dataInicial);
		queryBuilder.setDataFinal(dataFinal);
		queryBuilder.setNumeroFornecedor(numeroFornecedor);
		queryBuilder.setTipoValorEnum(tipoValorEnum);
		DetachedCriteria criteria = queryBuilder.build();
		return (BigDecimal) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Busca ScoItemAutorizacaoForn ainda não assinada
	 * 
	 * C4 de #5554 - Programação automática de Parcelas AF
	 * 
	 * @param numero
	 * @param nroComplemento
	 * @param lctNumero
	 * @return
	 */
	public ScoItemAutorizacaoForn buscarScoItemAutorizacaoForn(Integer numero, Short nroComplemento, Integer lctNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemAutorizacaoForn.class, "IAF");

		criteria.add(Restrictions.eq("IAF." + ScoItemAutorizacaoForn.Fields.NUMERO.toString(), numero));

		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), "AFN");
		criteria.add(Restrictions.eq("AFN." + ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString(), nroComplemento));

		criteria.createAlias("AFN." + ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString(), "PFR");
		criteria.add(Restrictions.eq("PFR." + ScoPropostaFornecedor.Fields.LICITACAO_ID.toString(), lctNumero));

		List<ScoItemAutorizacaoForn> result = executeCriteria(criteria, 0, 1, null, true);

		if (result != null && !result.isEmpty()) {
			return result.get(0);
		}

		return null;
	}

	/**
	 * Lista de Itens de uma Autorização de Fornecimento
	 * 
	 * C12 de #5554 - Programação automática de Parcelas AF
	 * 
	 * @param afnNumero
	 * @return
	 */
	public List<ScoItemAutorizacaoFornId> buscarItensAutorizacao(Integer afnNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemAutorizacaoForn.class);

		criteria.add(Restrictions.eq(ScoItemAutorizacaoForn.Fields.ID_AUT_FORN_NUMERO.toString(), afnNumero));
		criteria.add(Restrictions.ne(ScoItemAutorizacaoForn.Fields.IND_EXCLUSAO.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq(ScoItemAutorizacaoForn.Fields.IND_CONTRATO.toString(), Boolean.TRUE));
		criteria.add(Restrictions.ne(ScoItemAutorizacaoForn.Fields.IND_PROGR_ENTG_AUTO.toString(), Boolean.FALSE));
		criteria.add(Restrictions.or(Restrictions.eq(ScoItemAutorizacaoForn.Fields.IND_PROGR_ENTG_BLOQ.toString(), Boolean.FALSE),
				Restrictions.isNull(ScoItemAutorizacaoForn.Fields.IND_PROGR_ENTG_BLOQ.toString())));

		criteria.add(Restrictions.sqlRestriction("COALESCE(qtde_recebida,0) < qtde_solicitada"));
//		criteria.add(Restrictions.ltProperty(Projections.sqlProjection("coalesce(qtde_entregue,0)"),
//				ScoItemAutorizacaoForn.Fields.QUANTIDADE_SOLICITADA.toString()));

		criteria.add(Restrictions.in(ScoItemAutorizacaoForn.Fields.IND_SITUACAO.toString(), new DominioSituacaoAutorizacaoFornecedor[] {
				DominioSituacaoAutorizacaoFornecedor.EP, DominioSituacaoAutorizacaoFornecedor.AE, DominioSituacaoAutorizacaoFornecedor.PA }));

		ProjectionList projections = Projections.projectionList();
		projections.add(Projections.property(ScoItemAutorizacaoForn.Fields.ID_AUT_FORN_NUMERO.toString()), ScoItemAutorizacaoFornId.Fields.AFN_NUMERO.toString());
		projections.add(Projections.property(ScoItemAutorizacaoForn.Fields.NUMERO.toString()), ScoItemAutorizacaoFornId.Fields.NUMERO.toString());
		criteria.setProjection(projections);

		criteria.setResultTransformer(Transformers.aliasToBean(ScoItemAutorizacaoFornId.class));

		return executeCriteria(criteria);
	}

	public List<AutorizacaoFornVO> listarAfComSaldoProgramar(Object objPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemAutorizacaoForn.class, "IAF");
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), "AFN");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.distinct(Projections.property("AFN." + ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString()))
						, AutorizacaoFornVO.Fields.PFR_LCT_NUMERO.toString()));
		
		final String srtPesquisa = (String) objPesquisa;
		if (StringUtils.isNotBlank(srtPesquisa)) {
			criteria.add(Restrictions.eq("AFN." + ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString(), Integer.valueOf(srtPesquisa)));
		}
		criteria.add(Restrictions.sqlRestriction("{alias}.QTDE_SOLICITADA - " + criarSubQuerySomaQtde()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(AutorizacaoFornVO.class));
		
		return executeCriteria(criteria, 0, 100, null, true);
	}
	
	public Long listarAfComSaldoProgramarCount(Object objPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemAutorizacaoForn.class, "IAF");
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), "AFN");
		
		final String srtPesquisa = (String) objPesquisa;
		if (StringUtils.isNotBlank(srtPesquisa)) {
			criteria.add(Restrictions.eq("AFN." + ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString(), Integer.valueOf(srtPesquisa)));
		}
		criteria.add(Restrictions.sqlRestriction("{alias}.QTDE_SOLICITADA - " + criarSubQuerySomaQtde()));
		
		return executeCriteriaCountDistinct(criteria, "AFN." + ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString(), true);
	}
	
	private String criarSubQuerySomaQtde() {
		StringBuilder query = new StringBuilder(200);
		query.append(" (SELECT SUM(QTDE) ");
		query.append(" FROM AGH.SCO_PROGR_ENTREGA_ITENS_AF PEA");
		query.append(" WHERE PEA.IAF_AFN_NUMERO = {alias}.AFN_NUMERO ");
		query.append(" AND PEA.IAF_NUMERO = {alias}.NUMERO ");
		query.append(" AND PEA.IND_ASSINATURA = 'S' ) > 0 ");
		return query.toString();
	}
	
	@SuppressWarnings("unchecked")
	public List<ConsultaItensAFProgramacaoManualVO> consultarItensAFProgramacaoManual(final Integer numeroItem, final Integer numeroAF,
			final Short numeroComplemento, final Integer numeroFornecedor, final Integer codigoMaterial,
			final Integer codigoGrupoMaterial, final Boolean isIndProgramado) {

		ConsultaItensAFProgramacaoManualQueryBuilder builder = new ConsultaItensAFProgramacaoManualQueryBuilder(numeroItem, numeroAF, numeroComplemento,
				numeroFornecedor, codigoMaterial, codigoGrupoMaterial, isIndProgramado);
		String hql = builder.getSql();

		final org.hibernate.Query query = createHibernateQuery(hql);
		if (numeroItem != null) {
			query.setParameter("P_NUMERO_ITEM", numeroItem);
		}
		if (numeroAF != null) {
			query.setParameter("P_NUMERO_AF", numeroAF);
		}
		if (numeroComplemento != null) {
			query.setParameter("P_NUMERO_COMPLEMENTO", numeroComplemento);
		}
		if (numeroFornecedor != null) {
			query.setParameter("P_NUMERO_FORNECEDOR", numeroFornecedor);
		}
		if (codigoMaterial != null) {
			query.setParameter("P_COD_MATERIAL", codigoMaterial);
		}
		if (codigoGrupoMaterial != null) {
			query.setParameter("P_COD_GRUPO_MATERIAL", codigoGrupoMaterial);
		}
		query.setResultTransformer(Transformers.aliasToBean(ConsultaItensAFProgramacaoManualVO.class));
		return query.list();
	}

	/**
	 * #27143
	 * @author marcelo.deus <br/>
	 * Consulta que busca o total efetivado.
	 */
	public Double totalEfetivadoRelatorioEntradasSemEmpenhoSemAssinaturaAF(Integer afnNumero) {
		Double result = null;
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoItemAutorizacaoForn.class);
		
		criteria.add(Restrictions.eq(ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN_NUMERO.toString(), afnNumero));
		criteria.setProjection(Projections.projectionList().add(Projections.sqlProjection("sum({alias}.VALOR_EFETIVADO) as VALOR_EFETIVADO", new String[] { "VALOR_EFETIVADO"}, new Type[] { DoubleType.INSTANCE })));//(ScoItemAutorizacaoForn.Fields.VALOR_EFETIVADO.toString())));
		
		result = (Double) executeCriteriaUniqueResult(criteria);
		
		return result;
	}
}
