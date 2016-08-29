package br.gov.mec.aghu.compras.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.compras.pac.vo.ItemPropostaAFVO;
import br.gov.mec.aghu.dominio.DominioModalidadeEmpenho;
import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.model.FcpMoeda;
import br.gov.mec.aghu.model.ScoCondicaoPagamentoPropos;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoItemLicitacaoId;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedor;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedorId;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.model.ScoMarcaModelo;
import br.gov.mec.aghu.model.ScoMarcaModeloId;
import br.gov.mec.aghu.model.ScoNomeComercial;
import br.gov.mec.aghu.model.ScoNomeComercialId;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.model.ScoPropostaFornecedorId;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class ScoPropostaFornecedorDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoPropostaFornecedor>{
	
	private static final String ITL2 = " = ITL.";
	private static final String AND_FSE1 = " AND FSE1.";
	private static final String AND_FSE = " AND FSE.";
	private static final String AND_FSE2 = " AND FSE2.";
	private static final String NUMERO_PAC2 = " = :numeroPac ";
	private static final String IPF3 = " IPF, ";
	private static final String IS_NULL = " IS NULL ";
	private static final String AND_SLC = " AND SLC.";
	private static final String FSE = " FSE ";
	private static final String ITL = " ITL.";
	private static final String FROM = " FROM ";
	private static final String AND_SLS = " AND SLS.";
	private static final String IPF2 = " IPF.";
	private static final String SLS2 = " SLS ";
	private static final String SLC2 = " SLC ";
	private static final String LEFT_JOIN_FSE = " LEFT JOIN FSE.";
	private static final String IS_NOT_NULL = " IS NOT NULL ";
	private static final String NOT_IN = " NOT IN ( ";
	private static final String SLC = ", SLC.";
	private static final String EXCLUSAO2 = "exclusao";
	private static final String SLS = ", SLS.";
	private static final String AND_PRF = " AND PRF.";
	private static final String AND_ITL = " AND ITL.";
	private static final String AND_FSE3 = " AND FSE3.";
	private static final String AND_IPF = " AND IPF.";
	private static final String STR = " = ";
	private static final String EXCLUSAO = " = :exclusao ";
	private static final String SELECT = " SELECT ";
	private static final String IPF = ", IPF.";
	private static final String NUMERO_PAC = "numeroPac";
	private static final long serialVersionUID = 6793408119941187809L;

	@Override
	protected void obterValorSequencialId(ScoPropostaFornecedor elemento) {

		if (elemento.getLicitacao() == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório ScoLicitacao não informado!");
		}
		
		if (elemento.getFornecedor() == null) {
			throw new IllegalArgumentException("Parâmetro obrigatoório ScoFornecedor não informado!");
		}
		
		final ScoPropostaFornecedorId id = new ScoPropostaFornecedorId();
		
		id.setLctNumero(elemento.getLicitacao().getNumero());
		id.setFrnNumero(elemento.getFornecedor().getNumero());
		
		elemento.setId(id);
	}

	/**
	 * Método que pesquisa propostas de fornecedores pelo número da licitação
	 * @author clayton.bras
	 * @param licitacao
	 * @param fornecedor 
	 * @return
	 */
	public List<ScoPropostaFornecedor> pesquisarPropostas(
			ScoLicitacao licitacao, String fornecedor,
			Integer first, Integer max, String order, boolean asc) {
		DetachedCriteria criteria = getPropostaFornecedorByLicitacaoCriteria(
				licitacao.getNumero(), fornecedor);
		
		criteria.addOrder(Order.asc(path('F', ScoFornecedor.Fields.RAZAO_SOCIAL)));
		
		return executeCriteria(criteria, first, max, order, asc);		
	}

	/**
	 * #5262 
	 * @author dzboeira
	 * @param numero
	 * @return ScoPropostaFornecedor
	 */
	public List<ScoPropostaFornecedor> listarDataDigitacaoPropostaForn(Integer numero) {
		DetachedCriteria criteria = getPropostaFornecedorByLicitacaoCriteria(numero, null);

		criteria.addOrder(Order.asc(ScoPropostaFornecedor.Fields.DT_DIGITACAO
				.toString()));

		return executeCriteria(criteria);
	}

	/**
	 * Verifica quantas propostas existem para o PAC.
	 * 
	 * @param licitacao Licitação
	 * @param fornecedor 
	 * @return Propostas
	 */
	public Long contarPropostas(ScoLicitacao licitacao, String fornecedor) {
		DetachedCriteria criteria = getPropostaFornecedorByLicitacaoCriteria(
				licitacao.getNumero(), fornecedor);
		
		return executeCriteriaCount(criteria);
	}
	
	public Boolean verificarFornecedorPropostaAtivaPorLicitacao(Integer numeroPac, Integer numeroFornecedor) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoPropostaFornecedor.class);
		
		criteria.add(Restrictions.eq(ScoPropostaFornecedor.Fields.LICITACAO_ID.toString(), numeroPac));
		criteria.add(Restrictions.eq(ScoPropostaFornecedor.Fields.FORNECEDOR_ID.toString(), numeroFornecedor));
		criteria.add(Restrictions.eq(ScoPropostaFornecedor.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		
		return executeCriteriaCount(criteria) > 0;
	}
	
	/**
	 * Obtem criteria para consulta a propostas por licitação.
	 * 
	 * @param licitacaoId ID da Licitação
	 * @param fornecedor Fornecedor (nome, CPF ou CNPJ)
	 */
	private DetachedCriteria getPropostaFornecedorByLicitacaoCriteria(
			Integer licitacaoId, String fornecedor) {
		final String PF = "PF", F = "F";
		
		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoPropostaFornecedor.class, PF);
		
		criteria.createAlias(path(PF, ScoPropostaFornecedor.Fields.FORNECEDOR), F);

		criteria.add(Restrictions.eq(
				path(PF, ScoPropostaFornecedor.Fields.LICITACAO_ID),
				licitacaoId));
		
		if (!StringUtils.isEmpty(fornecedor)) {
			if (CoreUtil.isNumeroLong(fornecedor)) {
				Long numero = Long.valueOf(fornecedor);
				
				Criterion cpfMatch = Restrictions.and(
						Restrictions.isNotNull(path(F, ScoFornecedor.Fields.CPF)),
						Restrictions.eq(path(F, ScoFornecedor.Fields.CPF), numero));
				
				Criterion cnpjMatch = Restrictions.and(
						Restrictions.isNotNull(path(F, ScoFornecedor.Fields.CGC)),
						Restrictions.eq(path(F, ScoFornecedor.Fields.CGC), numero));
				
				criteria.add(Restrictions.or(cpfMatch, cnpjMatch));
			} else {
				criteria.add(Restrictions.ilike(
						path(F, ScoFornecedor.Fields.RAZAO_SOCIAL), 
						fornecedor, MatchMode.ANYWHERE));
			}
		}

		return criteria;
	}

	/**
	 * Obtem path para campos e relacionamentos dos POJO's
	 * 
	 * @param parts Partes do Path
	 * @return Path
	 */
	private String path(Object... parts) {
		StringBuffer path = new StringBuffer();
		
		for (int i = 0; i < parts.length; i ++) {
			path.append(parts[i]);
			
			if (i < parts.length - 1) {
				path.append('.');
			}
		}
		
		return path.toString();
	}
	
	public Long listarItemPropostaFornecedorPorLicitacaoCount(Integer numPac){
		StringBuilder hql = new StringBuilder(266);
		hql.append("SELECT COUNT(*)")
		.append(FROM).append(ScoPropostaFornecedor.class.getSimpleName()).append(" PRF, ")
		.append(ScoItemPropostaFornecedor.class.getSimpleName()).append(IPF3)
		.append(ScoFaseSolicitacao.class.getSimpleName()).append(FSE)
		.append(LEFT_JOIN_FSE).append(ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString()).append(SLC2)
		.append(LEFT_JOIN_FSE).append(ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO.toString()).append(SLS2)
		.append(" WHERE PRF.").append(ScoPropostaFornecedor.Fields.LICITACAO_ID).append(STR)
		.append(IPF2).append(ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR_LICITACAO_ID)
		.append(AND_PRF).append(ScoPropostaFornecedor.Fields.FORNECEDOR_ID).append(STR)
		.append(IPF2).append(ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR_FORNECEDOR_ID)
		.append(AND_FSE).append(ScoFaseSolicitacao.Fields.LCT_NUMERO).append(STR)
		.append(IPF2).append(ScoItemPropostaFornecedor.Fields.ITL_LCT_NUMERO)
		.append(AND_FSE).append(ScoFaseSolicitacao.Fields.ITL_NUMERO).append(STR)
		.append(IPF2).append(ScoItemPropostaFornecedor.Fields.ITL_NUMERO)
		.append(AND_PRF).append(ScoPropostaFornecedor.Fields.LICITACAO_ID.toString()).append(NUMERO_PAC2)
		.append(AND_IPF).append(ScoItemPropostaFornecedor.Fields.IND_EXCLUSAO.toString()).append(" = :naoExclusao ")
		.append(AND_FSE).append(ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString())
		.append(" = :naoExclusao AND (IPF.").append(ScoItemPropostaFornecedor.Fields.IND_ESCOLHIDO.toString())
		.append(" = :escolhido OR IPF.").append(ScoItemPropostaFornecedor.Fields.VALOR_UNITARIO.toString()).append(IS_NULL)
		.append(" OR IPF.").append(ScoItemPropostaFornecedor.Fields.NUMERO_CONDICAO_PAGAMENTO_PROPOS.toString())
		.append(" IS NULL OR (FSE.").append(ScoFaseSolicitacao.Fields.TIPO.toString())
		.append(" = :tipoFaseSolicitacaoCompra AND (SLC.").append(ScoSolicitacaoDeCompra.Fields.IND_EXCLUSAO.toString()).append(EXCLUSAO)
		.append(" OR SLC.").append(ScoSolicitacaoDeCompra.Fields.VERBA_GESTAO_SEQ.toString()).append(IS_NULL)
		.append(" OR SLC.").append(ScoSolicitacaoDeCompra.Fields.NTD_GND_CODIGO.toString())
		.append(" IS NULL)) OR (FSE.").append(ScoFaseSolicitacao.Fields.TIPO.toString())
		.append(" = :tipoFaseSolicitacaoServico AND (SLS.").append(ScoSolicitacaoServico.Fields.IND_EXCLUSAO.toString()).append(EXCLUSAO)
		.append(" OR SLS.").append(ScoSolicitacaoServico.Fields.VBG_SEQ.toString()).append(IS_NULL)
		.append(" OR SLS.").append(ScoSolicitacaoServico.Fields.NTD_GND_CODIGO.toString()).append(" IS NULL))) ");
		
		javax.persistence.Query query = this.createQuery(hql.toString());
			query.setParameter(NUMERO_PAC, numPac);
			query.setParameter(EXCLUSAO2, Boolean.TRUE);
			query.setParameter("naoExclusao", Boolean.FALSE);
			query.setParameter("escolhido", Boolean.FALSE);
			query.setParameter("tipoFaseSolicitacaoCompra", DominioTipoFaseSolicitacao.C);
			query.setParameter("tipoFaseSolicitacaoServico", DominioTipoFaseSolicitacao.S);
		
				Object counter = (Object) query.getSingleResult();
		
				return (Long)counter;
		
	}
	
	public List<ScoPropostaFornecedor> listarPropostaFornPorLicitacao(ScoLicitacao scoLicitacao) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoPropostaFornecedor.class);
		
		criteria.add(Restrictions.eq(ScoPropostaFornecedor.Fields.LICITACAO.toString(), scoLicitacao));		
		
		return executeCriteria(criteria);
	}
	
	
	public List<Object[]> pesquisarPropostaPorLicitacaoEItem(Integer numPac, Integer numItem){
		StringBuffer hql = new StringBuffer(1300);
		
		hql.append(" SELECT FRN.").append(ScoFornecedor.Fields.RAZAO_SOCIAL.toString())
		.append(", CASE WHEN FRN.").append(ScoFornecedor.Fields.CGC.toString())
		.append(" IS NULL THEN  FRN.").append(ScoFornecedor.Fields.CPF.toString())
		.append(" ELSE FRN.").append(ScoFornecedor.Fields.CGC.toString()).append(" END")
		.append(IPF).append(ScoItemPropostaFornecedor.Fields.VALOR_UNITARIO.toString())
		.append(IPF).append(ScoItemPropostaFornecedor.Fields.UMD_CODIGO.toString())
		.append(IPF).append(ScoItemPropostaFornecedor.Fields.QUANTIDADE.toString())
		.append(IPF).append(ScoItemPropostaFornecedor.Fields.FATOR_CONVERSAO.toString())
		.append(IPF).append(ScoItemPropostaFornecedor.Fields.QUANTIDADE.toString())
		.append(", CASE WHEN IPF.").append(ScoItemPropostaFornecedor.Fields.IND_NACIONAL.toString())
		.append(" = :indNacional THEN 'Nacional' ELSE 'Importado' END, MOE.").append(FcpMoeda.Fields.REPRESENTACAO.toString())
		.append(IPF).append(ScoItemPropostaFornecedor.Fields.MOM_SEQP.toString())
		.append(IPF).append(ScoItemPropostaFornecedor.Fields.MOM_MCM_CODIGO.toString())
		.append(IPF).append(ScoItemPropostaFornecedor.Fields.MCM_CODIGO.toString())
		.append(IPF).append(ScoItemPropostaFornecedor.Fields.APRESENTACAO.toString())
		.append(", FRN.").append(ScoFornecedor.Fields.NUMERO.toString())
		.append(IPF).append(ScoItemPropostaFornecedor.Fields.NUMERO.toString())		
		.append(IPF).append(ScoItemPropostaFornecedor.Fields.MOT_DESCLASSIF.toString())  		
		.append(", CASE WHEN IPF.").append(ScoItemPropostaFornecedor.Fields.JUSTIF_AUTORIZ_USR.toString())
		.append(" IS NULL THEN '' ELSE ")
		.append(IPF2).append(ScoItemPropostaFornecedor.Fields.JUSTIF_AUTORIZ_USR.toString()).append(" END ")  
		
		.append(FROM).append(ScoPropostaFornecedor.class.getSimpleName()).append(" PFR, ")
		.append(ScoItemPropostaFornecedor.class.getSimpleName()).append(IPF3)
		.append(ScoFornecedor.class.getSimpleName()).append(" FRN, ")
		.append(FcpMoeda.class.getSimpleName()).append(" MOE WHERE PFR.").append(ScoPropostaFornecedor.Fields.FORNECEDOR_ID.toString())
		.append(" =  IPF.").append(ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR_FORNECEDOR_ID.toString())
		.append(" AND PFR.").append(ScoPropostaFornecedor.Fields.LICITACAO_ID.toString())
		.append(" = IPF.").append(ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR_LICITACAO_ID.toString())		
		.append(" AND FRN.").append(ScoFornecedor.Fields.NUMERO)
		.append(" = PFR.").append(ScoPropostaFornecedor.Fields.FORNECEDOR_ID.toString())
		.append(" AND  MOE.").append(FcpMoeda.Fields.CODIGO.toString())
		.append(" = IPF.").append(ScoItemPropostaFornecedor.Fields.MDA_CODIGO.toString())		
		.append(AND_IPF).append(ScoItemPropostaFornecedor.Fields.IND_EXCLUSAO)
		.append(" = :indExclusao AND PFR.").append(ScoPropostaFornecedor.Fields.LICITACAO_ID).append(" = :numPac ")
		.append(AND_IPF).append(ScoItemPropostaFornecedor.Fields.ITL_NUMERO)
		.append(" = :numItem ORDER BY IPF.").append(ScoItemPropostaFornecedor.Fields.ITL_LCT_NUMERO.toString()).append(",IPF.")
		.append(ScoItemPropostaFornecedor.Fields.ITL_NUMERO.toString())
		.append(",  IPF.").append(ScoItemPropostaFornecedor.Fields.VALOR_UNITARIO).append(" / IPF.").append(ScoItemPropostaFornecedor.Fields.FATOR_CONVERSAO);
		
		Query query = createHibernateQuery(hql.toString());
		query.setParameter("numPac", numPac);
		query.setParameter("numItem", numItem.shortValue());
		query.setParameter("indExclusao", Boolean.FALSE);
		query.setParameter("indNacional", Boolean.TRUE);
		
		List<Object[]> resultList = query.list();
		
		return resultList;		
	}
	
	
	public List<ItemPropostaAFVO> pesquisarItensComProposta(Integer numPac){
		StringBuilder hql = new StringBuilder(357);
		hql.append("SELECT PRF.").append(ScoPropostaFornecedor.Fields.PRAZO_ENTREGA.toString())
		.append(", PRF.").append(ScoPropostaFornecedor.Fields.VALOR_TOTAL_FRETE.toString())
		.append(IPF).append(ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR_LICITACAO_ID.toString())
		.append(IPF).append(ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR_FORNECEDOR_ID.toString())
		.append(IPF).append(ScoItemPropostaFornecedor.Fields.NUMERO.toString())
		.append(IPF).append(ScoItemPropostaFornecedor.Fields.ITL_NUMERO.toString())
		.append(IPF).append(ScoItemPropostaFornecedor.Fields.UMD_CODIGO.toString())
		.append(IPF).append(ScoItemPropostaFornecedor.Fields.MDA_CODIGO.toString())
		.append(IPF).append(ScoItemPropostaFornecedor.Fields.QUANTIDADE.toString())
		.append(IPF).append(ScoItemPropostaFornecedor.Fields.IND_COM_DESCONTO.toString())
		.append(IPF).append(ScoItemPropostaFornecedor.Fields.FATOR_CONVERSAO.toString())
		.append(IPF).append(ScoItemPropostaFornecedor.Fields.PERC_ACRESCIMO.toString())
		.append(IPF).append(ScoItemPropostaFornecedor.Fields.PERC_IPI.toString())
		.append(IPF).append(ScoItemPropostaFornecedor.Fields.PERC_DESCONTO.toString())
		.append(IPF).append(ScoItemPropostaFornecedor.Fields.VALOR_UNITARIO.toString())
		.append(IPF).append(ScoItemPropostaFornecedor.Fields.MCM_CODIGO.toString())
		.append(IPF).append(ScoItemPropostaFornecedor.Fields.NC_MCM_CODIGO.toString())
		.append(IPF).append(ScoItemPropostaFornecedor.Fields.NC_NUMERO.toString())
		.append(IPF).append(ScoItemPropostaFornecedor.Fields.IND_ESCOLHIDO.toString())
		.append(", CDP.").append(ScoCondicaoPagamentoPropos.Fields.NUMERO.toString())
		.append(", FSE.").append(ScoFaseSolicitacao.Fields.TIPO.toString())
		.append(", FSE.").append(ScoFaseSolicitacao.Fields.SLC_NUMERO.toString())
		.append(", FSE.").append(ScoFaseSolicitacao.Fields.SLS_NUMERO.toString())
		.append(SLC).append(ScoSolicitacaoDeCompra.Fields.VERBA_GESTAO_SEQ.toString())
		.append(SLC).append(ScoSolicitacaoDeCompra.Fields.NTD_GND_CODIGO.toString())
		.append(SLC).append(ScoSolicitacaoDeCompra.Fields.NTD_CODIGO.toString())
		.append(SLS).append(ScoSolicitacaoServico.Fields.VBG_SEQ.toString())
		.append(SLS).append(ScoSolicitacaoServico.Fields.NTD_GND_CODIGO.toString())
		.append(SLS).append(ScoSolicitacaoServico.Fields.NTD_CODIGO.toString())
		.append(SLC).append(ScoSolicitacaoDeCompra.Fields.IND_EXCLUSAO.toString())
		.append(SLS).append(ScoSolicitacaoServico.Fields.IND_EXCLUSAO.toString())
		.append(SLC).append(ScoSolicitacaoDeCompra.Fields.VALOR_UNIT_PREVISTO.toString())
		.append(SLC).append(ScoSolicitacaoDeCompra.Fields.MAT_CODIGO.toString())
		.append(", LCT.").append(ScoLicitacao.Fields.MODALIDADE_EMPENHO.toString())
		.append(", ITL.").append(ScoItemLicitacao.Fields.FREQUENCIA_ENTREGA.toString())
		.append(IPF).append(ScoItemPropostaFornecedor.Fields.MOM_MCM_CODIGO.toString())
		.append(IPF).append(ScoItemPropostaFornecedor.Fields.MOM_SEQP.toString())
		.append(FROM).append(ScoPropostaFornecedor.class.getSimpleName())
		.append(" PRF ,").append(ScoItemPropostaFornecedor.class.getSimpleName())
		.append(" IPF ,").append(ScoCondicaoPagamentoPropos.class.getSimpleName())
		.append(" CDP ,").append(ScoLicitacao.class.getSimpleName())
		.append(" LCT ,").append(ScoItemLicitacao.class.getSimpleName())
		.append(" ITL ,").append(ScoFaseSolicitacao.class.getSimpleName()).append(FSE)
		.append(LEFT_JOIN_FSE).append(ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString()).append(SLC2)
		.append(LEFT_JOIN_FSE).append(ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO.toString()).append(SLS2)
		.append(" WHERE ITL.").append(ScoItemLicitacao.Fields.NUMERO_LICITACAO.toString()).append(NUMERO_PAC2)
		.append(AND_ITL).append(ScoItemLicitacao.Fields.IND_PROPOSTA_ESCOLHIDA.toString()).append(" = :propostaEscolhida ")
//		.append(" AND ITL.").append(ScoItemLicitacao.Fields.IND_EM_AF.toString()).append(" = :emAf ")
		.append(AND_ITL).append(ScoItemLicitacao.Fields.IND_EXCLUSAO.toString()).append(EXCLUSAO)
		.append(" AND LCT.").append(ScoLicitacao.Fields.NUMERO.toString()).append(STR).append(ITL).append(ScoItemLicitacao.Fields.NUMERO_LICITACAO.toString())
		.append(AND_FSE).append(ScoFaseSolicitacao.Fields.LCT_NUMERO.toString()).append(STR).append(ITL).append(ScoItemLicitacao.Fields.NUMERO_LICITACAO.toString())
		.append(AND_FSE).append(ScoFaseSolicitacao.Fields.ITL_NUMERO.toString()).append(STR).append(ITL).append(ScoItemLicitacao.Fields.NUMERO.toString())
		.append(AND_FSE).append(ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString()).append(EXCLUSAO)
		.append(AND_IPF).append(ScoItemPropostaFornecedor.Fields.ITL_LCT_NUMERO.toString()).append(STR).append(ITL).append(ScoItemLicitacao.Fields.NUMERO_LICITACAO.toString())
		.append(AND_IPF).append(ScoItemPropostaFornecedor.Fields.ITL_NUMERO.toString()).append(STR).append(ITL).append(ScoItemLicitacao.Fields.NUMERO.toString())
		.append(AND_IPF).append(ScoItemPropostaFornecedor.Fields.IND_ESCOLHIDO.toString()).append(" = :escolhido ")
		.append(AND_IPF).append(ScoItemPropostaFornecedor.Fields.IND_EXCLUSAO.toString()).append(EXCLUSAO)
		.append(AND_PRF).append(ScoPropostaFornecedor.Fields.LICITACAO_ID.toString()).append(STR).append(IPF2).append(ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR_LICITACAO_ID.toString())
		.append(AND_PRF).append(ScoPropostaFornecedor.Fields.FORNECEDOR_ID.toString()).append(STR).append(IPF2).append(ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR_FORNECEDOR_ID.toString())
		.append(" AND CDP.").append(ScoCondicaoPagamentoPropos.Fields.NUMERO.toString()).append(STR).append(IPF2).append(ScoItemPropostaFornecedor.Fields.NUMERO_CONDICAO_PAGAMENTO_PROPOS.toString())
//		.append(" AND CDP.").append(ScoCondicaoPagamentoPropos.Fields.PFR_FRN_NUMERO.toString()).append(" = ").append(" IPF.").append(ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR_FORNECEDOR_ID.toString())
//		hql.append(" AND CDP.").append(ScoCondicaoPagamentoPropos.Fields.PFR_LCT_NUMERO.toString()).append(" = ").append(" IPF.").append(ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR_LICITACAO_ID.toString());
		
		.append(" AND COALESCE(FSE.").append(ScoFaseSolicitacao.Fields.SLC_NUMERO.toString())
		.append(",0) NOT IN (SELECT FSE.").append(ScoFaseSolicitacao.Fields.SLC_NUMERO.toString())
		.append(FROM).append(ScoFaseSolicitacao.class.getSimpleName())
		.append(" FSE1 WHERE FSE1.").append(ScoFaseSolicitacao.Fields.SLC_NUMERO.toString()).append(STR).append("FSE.").append(ScoFaseSolicitacao.Fields.SLC_NUMERO.toString())
		.append(AND_FSE1).append(ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()).append(IS_NOT_NULL)
		.append(AND_FSE1).append(ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString())
		.append(" = :exclusao) AND COALESCE(FSE.").append(ScoFaseSolicitacao.Fields.SLS_NUMERO.toString())
		.append(",0) NOT IN (SELECT FSE.").append(ScoFaseSolicitacao.Fields.SLS_NUMERO.toString())
		.append(FROM).append(ScoFaseSolicitacao.class.getSimpleName())
		.append(" FSE1 WHERE FSE1.").append(ScoFaseSolicitacao.Fields.SLS_NUMERO.toString()).append(STR).append("FSE.").append(ScoFaseSolicitacao.Fields.SLS_NUMERO.toString())
		.append(AND_FSE1).append(ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()).append(IS_NOT_NULL)
		.append(AND_FSE1).append(ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString())
		.append(" = :exclusao) ORDER BY PRF.").append(ScoPropostaFornecedor.Fields.FORNECEDOR_ID.toString())
		.append(" ,SLC.").append(ScoSolicitacaoDeCompra.Fields.VERBA_GESTAO_SEQ.toString())
		.append(" ,SLC.").append(ScoSolicitacaoDeCompra.Fields.NTD_GND_CODIGO.toString())
		.append(" ,SLC.").append(ScoSolicitacaoDeCompra.Fields.NTD_CODIGO.toString())
		.append(" ,CDP.").append(ScoCondicaoPagamentoPropos.Fields.NUMERO.toString())
		.append(" ,FSE.").append(ScoFaseSolicitacao.Fields.TIPO.toString())
		.append(" ,IPF.").append(ScoItemPropostaFornecedor.Fields.ITL_NUMERO.toString());
	//	hql.append(" ,FSE.").append(ScoFaseSolicitacao.Fields.SLC_NUMERO.toString());
	//	hql.append(" ,FSE.").append(ScoFaseSolicitacao.Fields.SLS_NUMERO.toString());
		
		Query query = this.createHibernateQuery(hql.toString());
		query.setParameter(NUMERO_PAC, numPac);
		query.setParameter("propostaEscolhida", Boolean.TRUE);
//		query.setParameter("emAf", Boolean.FALSE);
		query.setParameter(EXCLUSAO2, Boolean.FALSE);
		query.setParameter("escolhido", Boolean.TRUE);
		
		List<ItemPropostaAFVO> listaVo = new ArrayList<ItemPropostaAFVO>();
		List<Object[]> camposList = (List<Object[]>) query.list();
		
		if(camposList != null && camposList.size()>0) {
			for(Object[] campo: camposList){
				ItemPropostaAFVO vo = new ItemPropostaAFVO();
				ScoItemPropostaFornecedor itemPropostaFornecedor = new ScoItemPropostaFornecedor();
				ScoPropostaFornecedor propostaFornecedor = new ScoPropostaFornecedor();
				propostaFornecedor.setPrazoEntrega((Short)campo[0]);
				propostaFornecedor.setValorTotalFrete((BigDecimal)campo[1]);
				itemPropostaFornecedor.setPropostaFornecedor(propostaFornecedor);
				ScoItemPropostaFornecedorId itemPropostaFornecedorId = new ScoItemPropostaFornecedorId();
				itemPropostaFornecedorId.setPfrLctNumero((Integer)campo[2]);
				itemPropostaFornecedorId.setPfrFrnNumero((Integer)campo[3]);
				itemPropostaFornecedorId.setNumero((Short)campo[4]);
				itemPropostaFornecedor.setId(itemPropostaFornecedorId);
				ScoItemLicitacao itemLicitacao = new ScoItemLicitacao();
				ScoItemLicitacaoId itemLicitacaoId = new ScoItemLicitacaoId();
				itemLicitacaoId.setNumero((Short)campo[5]);
				itemLicitacao.setId(itemLicitacaoId);
				itemPropostaFornecedor.setItemLicitacao(itemLicitacao);
				ScoUnidadeMedida unidadeMedida = new ScoUnidadeMedida();
				unidadeMedida.setCodigo((String)campo[6]);
				itemPropostaFornecedor.setUnidadeMedida(unidadeMedida);
				FcpMoeda moeda = new FcpMoeda();
				moeda.setCodigo((Short)campo[7]);
				itemPropostaFornecedor.setMoeda(moeda);
				itemPropostaFornecedor.setQuantidade((Long)campo[8]);
				itemPropostaFornecedor.setIndComDesconto((Boolean)campo[9]);
				itemPropostaFornecedor.setFatorConversao((Integer)campo[10]);
				itemPropostaFornecedor.setPercAcrescimo((BigDecimal) campo[11]);
				itemPropostaFornecedor.setPercIpi((BigDecimal) campo[12]);
				itemPropostaFornecedor.setPercDesconto((BigDecimal) campo[13]);
				itemPropostaFornecedor.setValorUnitario((BigDecimal) campo[14]);
				ScoMarcaComercial marcaComercial = new ScoMarcaComercial();
				marcaComercial.setCodigo((Integer) campo[15]);
				itemPropostaFornecedor.setMarcaComercial(marcaComercial);
				
				ScoMarcaModelo modeloComercial = new ScoMarcaModelo();
				ScoMarcaModeloId scoMarcaModeloId = new  ScoMarcaModeloId();
				scoMarcaModeloId.setMcmCodigo((Integer) campo[35]);
				scoMarcaModeloId.setSeqp((Integer) campo[36]);
				modeloComercial.setId(scoMarcaModeloId);
				
				itemPropostaFornecedor.setModeloComercial(modeloComercial);
				
				ScoNomeComercial nomeComercial = new ScoNomeComercial();
				ScoNomeComercialId nomeComercialId = new ScoNomeComercialId();
				nomeComercialId.setMcmCodigo((Integer) campo[16]);
				nomeComercialId.setNumero((Integer) campo[17]);
				nomeComercial.setId(nomeComercialId);
				itemPropostaFornecedor.setNomeComercial(nomeComercial);
				itemPropostaFornecedor.setIndEscolhido((Boolean) campo[18]);
				ScoCondicaoPagamentoPropos condicaoPagamentoPropos = new ScoCondicaoPagamentoPropos();
				condicaoPagamentoPropos.setNumero((Integer) campo[19]);
				itemPropostaFornecedor.setCondicaoPagamentoPropos(condicaoPagamentoPropos);
				vo.setItemPropostaFornecedor(itemPropostaFornecedor);
				vo.setTipo((DominioTipoFaseSolicitacao) campo[20]);
				vo.setSlcNumero((Integer) campo[21]);
				vo.setSlsNumero((Integer) campo[22]);
				vo.setSlcVbgSeq((Integer) campo[23]);				
				vo.setSlcNtdGndCodigo((Integer) campo[24]);
				vo.setSlcNtdCodigo((Byte) campo[25]);
				vo.setSlsVbgSeq((Integer) campo[26]);
				vo.setSlsNtdGndCodigo((Integer) campo[27]);
				vo.setSlsNtdCodigo((Byte) campo[28]);
				vo.setSlcIndExclusao((Boolean) campo[29]);
				vo.setSlsIndExclusao((Boolean) campo[30]);
				vo.setValorUnitarioPrevisto((BigDecimal) campo[31]);
				vo.setMatCodigo((Integer) campo[32]);
				vo.setModalidadeEmpenho((DominioModalidadeEmpenho) campo[33]);
				vo.setFrequenciaEntrega((Integer) campo[34]);
				listaVo.add(vo);
			}
		}
		
		return listaVo;
}
	
	
	public Long pesquisarItensParaGeracao(Integer numPac){
			StringBuilder hql = new StringBuilder(148);
			hql.append("SELECT COUNT(*)")
			.append(FROM).append(ScoPropostaFornecedor.class.getSimpleName()).append(" PRF, ")
			.append(ScoItemPropostaFornecedor.class.getSimpleName()).append(IPF3)
			.append(ScoItemLicitacao.class.getSimpleName()).append(" ITL, ")
			.append(ScoFaseSolicitacao.class.getSimpleName()).append(FSE)
			.append(LEFT_JOIN_FSE).append(ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString()).append(SLC2)
			.append(LEFT_JOIN_FSE).append(ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO.toString()).append(SLS2)
			.append(" WHERE PRF.").append(ScoPropostaFornecedor.Fields.LICITACAO_ID).append(STR)
			.append(IPF2).append(ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR_LICITACAO_ID)
			.append(AND_PRF).append(ScoPropostaFornecedor.Fields.FORNECEDOR_ID).append(STR)
			.append(IPF2).append(ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR_FORNECEDOR_ID)
			.append(AND_FSE).append(ScoFaseSolicitacao.Fields.LCT_NUMERO).append(STR)
			.append(IPF2).append(ScoItemPropostaFornecedor.Fields.ITL_LCT_NUMERO)
			.append(AND_FSE).append(ScoFaseSolicitacao.Fields.ITL_NUMERO).append(STR)
			.append(IPF2).append(ScoItemPropostaFornecedor.Fields.ITL_NUMERO)
			.append(" AND ((PRF.").append(ScoPropostaFornecedor.Fields.LICITACAO_ID)
			.append(ITL2).append(ScoPropostaFornecedor.Fields.LICITACAO_ID)
			.append(AND_IPF).append(ScoItemPropostaFornecedor.Fields.ITL_NUMERO)
			.append(ITL2).append(ScoItemLicitacao.Fields.NUMERO)
			.append(") OR (IPF.").append(ScoItemPropostaFornecedor.Fields.ITL_NUMERO)
			.append(" IS NULL))")
			.append(AND_FSE).append(ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString()).append(EXCLUSAO)
			.append(AND_IPF).append(ScoItemPropostaFornecedor.Fields.IND_EXCLUSAO.toString()).append(EXCLUSAO)
			.append(AND_PRF).append(ScoPropostaFornecedor.Fields.LICITACAO_ID.toString()).append(NUMERO_PAC2)
			.append(AND_SLC).append(ScoSolicitacaoDeCompra.Fields.NUMERO.toString()).append(NOT_IN)
			.append(SELECT).append(ScoFaseSolicitacao.Fields.SLC_NUMERO.toString()).append(FROM)
			.append(ScoFaseSolicitacao.class.getSimpleName()).append(" FSE2  WHERE FSE2.")
			.append(ScoFaseSolicitacao.Fields.SLC_NUMERO.toString()).append(STR)
			.append(" SLC.").append(ScoSolicitacaoDeCompra.Fields.NUMERO.toString())
			.append(AND_FSE2).append(ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()).append(IS_NOT_NULL)
			.append(AND_FSE2).append(ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString()).append(" = :exclusao )))")
			.append(AND_SLS).append(ScoSolicitacaoServico.Fields.NUMERO.toString()).append(NOT_IN)
			.append(SELECT).append(ScoFaseSolicitacao.Fields.SLS_NUMERO.toString()).append(FROM)
			.append(ScoFaseSolicitacao.class.getSimpleName()).append(" FSE3  WHERE FSE3.")
			.append(ScoFaseSolicitacao.Fields.SLS_NUMERO.toString()).append(STR)
			.append(" SLS.").append(ScoSolicitacaoServico.Fields.NUMERO.toString())
			.append(AND_FSE3).append(ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()).append(IS_NOT_NULL)
			.append(AND_FSE3).append(ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString()).append(" = :exclusao ))))");
			javax.persistence.Query query = this.createQuery(hql.toString());
			query.setParameter(NUMERO_PAC, numPac);
			query.setParameter(EXCLUSAO2, Boolean.FALSE);
			Object counter = (Object) query.getSingleResult();
			return (Long)counter;
		}
	
	
	  public Long pesquisarItensParaGeracaoCount(Integer numPac){
             StringBuilder hql = new StringBuilder(307);
             hql.append("SELECT COUNT(*)")
		     .append(FROM).append(ScoPropostaFornecedor.class.getSimpleName()).append(" PRF, ")
		     .append(ScoItemPropostaFornecedor.class.getSimpleName()).append(IPF3)
		     .append(ScoItemLicitacao.class.getSimpleName()).append(" ITL, ")
		     .append(ScoFaseSolicitacao.class.getSimpleName()).append(FSE)
		     .append(LEFT_JOIN_FSE).append(ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString()).append(SLC2)
		     .append(LEFT_JOIN_FSE).append(ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO.toString()).append(SLS2)
		     .append(" WHERE PRF.").append(ScoPropostaFornecedor.Fields.LICITACAO_ID).append(STR)
		     .append(IPF2).append(ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR_LICITACAO_ID)
		     .append(AND_PRF).append(ScoPropostaFornecedor.Fields.FORNECEDOR_ID).append(STR)
		     .append(IPF2).append(ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR_FORNECEDOR_ID)
		     .append(AND_FSE).append(ScoFaseSolicitacao.Fields.LCT_NUMERO).append(STR)
		     .append(IPF2).append(ScoItemPropostaFornecedor.Fields.ITL_LCT_NUMERO)
		     .append(AND_FSE).append(ScoFaseSolicitacao.Fields.ITL_NUMERO).append(STR)
		     .append(IPF2).append(ScoItemPropostaFornecedor.Fields.ITL_NUMERO)
		     .append(" AND ((PRF.").append(ScoPropostaFornecedor.Fields.LICITACAO_ID)
		     .append(ITL2).append(ScoPropostaFornecedor.Fields.LICITACAO_ID)
		     .append(AND_IPF).append(ScoItemPropostaFornecedor.Fields.ITL_NUMERO)
		     .append(ITL2).append(ScoItemLicitacao.Fields.NUMERO).append(") OR (IPF.").append(ScoItemPropostaFornecedor.Fields.ITL_NUMERO)
		     .append(" IS NULL))")
		     .append(AND_PRF).append(ScoPropostaFornecedor.Fields.LICITACAO_ID.toString()).append(NUMERO_PAC2)
		     .append(AND_IPF).append(ScoItemPropostaFornecedor.Fields.IND_EXCLUSAO.toString()).append(EXCLUSAO)
		     .append(AND_FSE).append(ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString()).append(EXCLUSAO)
		     .append(AND_IPF).append(ScoItemPropostaFornecedor.Fields.IND_ESCOLHIDO.toString()).append(" = :escolhido ")
		     .append(AND_IPF).append(ScoItemPropostaFornecedor.Fields.VALOR_UNITARIO.toString()).append(IS_NOT_NULL)
		     .append(AND_IPF).append(ScoItemPropostaFornecedor.Fields.NUMERO_CONDICAO_PAGAMENTO_PROPOS.toString()).append(IS_NOT_NULL)
		     .append(AND_ITL).append(ScoItemLicitacao.Fields.IND_PROPOSTA_ESCOLHIDA.toString()).append(" = :propostaEscolhida ")
//		     hql.append(" AND ITL.").append(ScoItemLicitacao.Fields.IND_EM_AF.toString()).append(" = :emAf ");
		     .append(AND_ITL).append(ScoItemLicitacao.Fields.IND_PROPOSTA_ESCOLHIDA.toString()).append(" = :propostaEscolhida ")
		     .append(AND_ITL).append(ScoItemLicitacao.Fields.IND_EXCLUSAO.toString()).append(EXCLUSAO)
		     .append(AND_ITL).append(ScoItemLicitacao.Fields.MOTIVO_CANCEL.toString()).append(IS_NULL)
		     .append(" AND ((FSE.").append(ScoFaseSolicitacao.Fields.TIPO.toString())
		     .append(" = :tipoFaseSolicitacaoCompra AND (SLC.").append(ScoSolicitacaoDeCompra.Fields.IND_EXCLUSAO.toString()).append(EXCLUSAO)
		     .append(AND_SLC).append(ScoSolicitacaoDeCompra.Fields.VERBA_GESTAO_SEQ.toString()).append(IS_NOT_NULL)
		     .append(AND_SLC).append(ScoSolicitacaoDeCompra.Fields.NTD_CODIGO.toString()).append(IS_NOT_NULL)
		     .append(AND_SLC).append(ScoSolicitacaoDeCompra.Fields.NTD_GND_CODIGO.toString()).append(IS_NOT_NULL)
		     .append(AND_SLC).append(ScoSolicitacaoDeCompra.Fields.NUMERO.toString()).append(NOT_IN)
		     .append(SELECT).append(ScoFaseSolicitacao.Fields.SLC_NUMERO.toString()).append(FROM)
		     .append(ScoFaseSolicitacao.class.getSimpleName())
		     .append(" FSE2  WHERE FSE2.").append(ScoFaseSolicitacao.Fields.SLC_NUMERO.toString()).append(STR)
		     .append(" SLC.").append(ScoSolicitacaoDeCompra.Fields.NUMERO.toString())
		     .append(AND_FSE2).append(ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()).append(IS_NOT_NULL)
		     .append(AND_FSE2).append(ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString())
		     .append(" = :exclusao ))) OR (FSE.").append(ScoFaseSolicitacao.Fields.TIPO.toString())
		     .append(" = :tipoFaseSolicitacaoServico AND (SLS.").append(ScoSolicitacaoServico.Fields.IND_EXCLUSAO.toString()).append(EXCLUSAO)
		     .append(AND_SLS).append(ScoSolicitacaoServico.Fields.VBG_SEQ.toString()).append(IS_NOT_NULL)
		     .append(AND_SLS).append(ScoSolicitacaoServico.Fields.NTD_CODIGO.toString()).append(IS_NOT_NULL)
		     .append(AND_SLS).append(ScoSolicitacaoServico.Fields.NTD_GND_CODIGO.toString()).append(IS_NOT_NULL)
		     .append(AND_SLS).append(ScoSolicitacaoServico.Fields.NUMERO.toString()).append(NOT_IN)
		     .append(SELECT).append(ScoFaseSolicitacao.Fields.SLS_NUMERO.toString()).append(FROM)
		     .append(ScoFaseSolicitacao.class.getSimpleName())
		     .append(" FSE3  WHERE FSE3.").append(ScoFaseSolicitacao.Fields.SLS_NUMERO.toString()).append(STR)
		     .append(" SLS.").append(ScoSolicitacaoServico.Fields.NUMERO.toString())
		     .append(AND_FSE3).append(ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()).append(IS_NOT_NULL)
		     .append(AND_FSE3).append(ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString()).append(" = :exclusao ))))");
             
		     javax.persistence.Query query = createQuery(hql.toString());
		     query.setParameter(NUMERO_PAC, numPac);
		     query.setParameter(EXCLUSAO2, Boolean.FALSE);
		     query.setParameter("escolhido", Boolean.TRUE);
		     query.setParameter("propostaEscolhida", Boolean.TRUE);
	//	     query.setParameter("emAf", Boolean.FALSE);
		     query.setParameter("tipoFaseSolicitacaoCompra", DominioTipoFaseSolicitacao.C);
		     query.setParameter("tipoFaseSolicitacaoServico", DominioTipoFaseSolicitacao.S);
		     Object counter = (Object) query.getSingleResult();
		     return (Long)counter;
		  }
	
}
