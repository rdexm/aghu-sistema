package br.gov.mec.aghu.compras.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BigDecimalType;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.compras.pac.vo.AtaRegistroPrecoVO;
import br.gov.mec.aghu.compras.pac.vo.LicitacoesLiberarCriteriaVO;
import br.gov.mec.aghu.compras.pac.vo.PacParaJulgamentoCriteriaVO;
import br.gov.mec.aghu.compras.pac.vo.PacParaJulgamentoVO;
import br.gov.mec.aghu.compras.pac.vo.RelatorioEpVo;
import br.gov.mec.aghu.compras.pac.vo.ScoLicitacaoVO;
import br.gov.mec.aghu.compras.vo.DadosGestorVO;
import br.gov.mec.aghu.compras.vo.RelatorioEspelhoPACVO;
import br.gov.mec.aghu.compras.vo.VerbaGrupoSolicitacaoVO;
import br.gov.mec.aghu.dominio.DominioAprovadaAutorizacaoForn;
import br.gov.mec.aghu.dominio.DominioModalidadeEmpenho;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecedor;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;
import br.gov.mec.aghu.dominio.DominioSituacaoLicitacao;
import br.gov.mec.aghu.model.FsoGrupoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoVerbaGestao;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceDevolucaoFornecedor;
import br.gov.mec.aghu.model.SceDocumentoFiscalEntrada;
import br.gov.mec.aghu.model.SceEstoqueGeral;
import br.gov.mec.aghu.model.SceItemNotaRecebimento;
import br.gov.mec.aghu.model.SceNotaRecebimento;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoAutorizacaoFornJn;
import br.gov.mec.aghu.model.ScoCondicaoPagamentoPropos;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoFormaPagamento;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItemAutorizacaoFornJn;
import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoLoteLicitacao;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.ScoNomeComercial;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.model.ScoTempoAndtPac;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.suprimentos.vo.ScoUltimasComprasMaterialVO;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.utils.DateUtil;

public class ScoLicitacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoLicitacao> {

	private static final long serialVersionUID = -6089877052414954721L;
	
	private static final Log LOG = LogFactory.getLog(ScoLicitacaoDAO.class);
	

	public List<ScoLicitacao> listarLicitacoesAtivas(Object pesquisa, DominioModalidadeEmpenho modemp) {
		String param = (String) pesquisa;
		StringBuilder str = new StringBuilder(" from ScoLicitacao lct").append(" where lct.exclusao = :LIC_IND_EX_PARAM");
		if (!StringUtils.isBlank(param) && !CoreUtil.isNumeroInteger(param)) {
			param = new StringBuffer("%").append(param).append("%").toString();
			str.append(" and lct.descricao like :DESC");
		} else if (CoreUtil.isNumeroInteger(param)) {str.append(" and lct.numero =:DESC");}
		str.append(" and (lct.modalidadeEmpenho = :LIC_MOD_EMPENHO_PARAM").append(" or exists ( from ScoAutorizacaoForn afn ")
				.append(" where afn.propostaFornecedor.id.lctNumero = lct.numero").append(" and afn.exclusao = :AFN__IND_EX_PARAM")
				.append(" and afn.modalidadeEmpenho = :AFN_MOD_EMPENHO_PARAM ))").append(" and exists ( from ScoAutorizacaoForn afn2")
				.append(" where afn2.propostaFornecedor.id.lctNumero = lct.numero").append(" and afn2.exclusao = :AFN__IND_EX_PARAM")
				.append(" and afn2.situacao <> :AFN_SIT_PARAM) order by lct.numero asc");
		Query q = createHibernateQuery(str.toString());
		q.setParameter("LIC_IND_EX_PARAM", Boolean.FALSE);
		q.setParameter("LIC_MOD_EMPENHO_PARAM", modemp);
		q.setParameter("AFN__IND_EX_PARAM", Boolean.FALSE);
		q.setParameter("AFN_MOD_EMPENHO_PARAM", modemp);
		q.setParameter("AFN_SIT_PARAM", DominioSituacaoAutorizacaoFornecimento.EF);
		if (param != null && !param.equals("") && !CoreUtil.isNumeroInteger(param)) {
			q.setParameter("DESC", param.toUpperCase());
		} else if (CoreUtil.isNumeroInteger(param)) {
			q.setParameter("DESC", Integer.parseInt(param));
		}
		return q.list();
	}
	/**
	 * Método que retorna a criteria da pesquisa de licitações
	 * 
	 * @author clayton.bras
	 * @since 30/05/2011
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public DetachedCriteria criaQueryPesquisaPac(ScoLicitacao licitacao, Date dataInicioGer, Date dataFimGer) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoLicitacao.class);
		criteria.createAlias(ScoLicitacao.Fields.MODALIDADE_LICITACAO.toString(), "mod_lic", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoLicitacao.Fields.SERVIDOR_GESTOR.toString(), "serv_gestor", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("serv_gestor." + RapServidores.Fields.PESSOA_FISICA.toString(), "serv_pf", JoinType.LEFT_OUTER_JOIN);						
		criteria.createAlias("serv_gestor." + RapServidores.Fields.VINCULO.toString(), "vinc_gestor", JoinType.LEFT_OUTER_JOIN);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"); 
	    SimpleDateFormat sdfDia = new SimpleDateFormat("dd/MM/yyyy"); 
		if (licitacao.getNumero() != null) {
			criteria.add(Restrictions.eq(ScoLicitacao.Fields.NUMERO.toString(), licitacao.getNumero()));}
		if (licitacao.getModalidadeLicitacao() != null) {
			criteria.add(Restrictions.eq(ScoLicitacao.Fields.MODALIDADE_LICITACAO.toString(), licitacao.getModalidadeLicitacao()));}
		if (licitacao.getTipoPregao() != null) {
			criteria.add(Restrictions.eq(ScoLicitacao.Fields.IND_TIPO_PREGAO.toString(), licitacao.getTipoPregao()));}
		if (!StringUtils.isBlank(licitacao.getDescricao())) {
			criteria.add(Restrictions.ilike(ScoLicitacao.Fields.DESCRICAO.toString(), licitacao.getDescricao(), MatchMode.ANYWHERE));}
		if (licitacao.getNumDocLicit() != null) {
			criteria.add(Restrictions.eq(ScoLicitacao.Fields.NUM_DOC_LICIT.toString(), licitacao.getNumDocLicit()));}
		if (licitacao.getNumEdital() != null) {
			criteria.add(Restrictions.eq(ScoLicitacao.Fields.NUM_EDITAL.toString(), licitacao.getNumEdital()));}
		if (licitacao.getAnoComplemento() != null) {
			criteria.add(Restrictions.eq(ScoLicitacao.Fields.ANO_COMPLEMENTO.toString(), licitacao.getAnoComplemento()));}
		if (licitacao.getTipo() != null) {
			criteria.add(Restrictions.eq(ScoLicitacao.Fields.TIPO.toString(), licitacao.getAnoComplemento()));}
		if (licitacao.getIdEletronico() != null) {
			criteria.add(Restrictions.eq(ScoLicitacao.Fields.ID_ELETRONICO.toString(), licitacao.getIdEletronico()));}
		if (licitacao.getSituacao() != null) {
			criteria.add(Restrictions.eq(ScoLicitacao.Fields.IND_SITUACAO.toString(), licitacao.getSituacao()));}
		if (licitacao.getIndUrgente() != null) {
			criteria.add(Restrictions.eq(ScoLicitacao.Fields.IND_URGENTE.toString(), licitacao.getIndUrgente()));}
		if (licitacao.getExclusao() != null) {
			criteria.add(Restrictions.eq(ScoLicitacao.Fields.IND_EXCLUSAO.toString(), licitacao.getExclusao()));}
		if (licitacao.getServidorGestor() != null) {
			criteria.add(Restrictions.eq(ScoLicitacao.Fields.SERVIDOR_GESTOR.toString(), licitacao.getServidorGestor()));}
		if (dataInicioGer!= null) {
			criteria.add(Restrictions.ge(ScoLicitacao.Fields.DT_DIGITACAO.toString(), dataInicioGer));}
			if (dataFimGer!= null) {
			try{
			criteria.add(Restrictions.le(ScoLicitacao.Fields.DT_DIGITACAO.toString(), sdf.parse(sdfDia.format(dataFimGer)+" 23:59:59")));
			} catch (ParseException e) {
			LOG.error("Erro ao executar chamada"+ e.getMessage());
			}
		}
		return criteria;
	}
	/**
	 * Método responsável pela pesquisa de pacs executando criteria
	 * @author silvia.gralha
	 * @param licitacao
	 * @since 01/03/2013
	 */
	public List<ScoLicitacao> listarProcessosAdmCompra(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, ScoLicitacao licitacao, Date dataInicioGer, Date dataFimGer) {
		DetachedCriteria criteria = criaQueryPesquisaPac(licitacao, dataInicioGer, dataFimGer);	
	    criteria.addOrder(Order.desc(ScoLicitacao.Fields.NUMERO.toString()));
		if(firstResult == null && maxResult == null ){
			return executeCriteria(criteria);
		}
		return executeCriteria(criteria, firstResult, maxResult, null, true);
	}
	/**
	 * Método responsável pelo retorno do número de pacs encontradas na pesquisa
	 * de licitações
	 * 
	 * @author silvia.gralha
	 * @param licitacao
	 * @since 01/03/2013
	 */
	public Long listarProcessosAdmCompraCount(ScoLicitacao licitacao, Date dataInicioGer, Date dataFimGer) {
		return executeCriteriaCount(criaQueryPesquisaPac(licitacao, dataInicioGer, dataFimGer));
	}
	public Integer obterNumeroMaximoDeDiasDePermaneciaPelaLicitacao(Integer lctNumero) {
		final String TAP = "TAP", LCT = "LCT";
		final StringBuffer hql = new StringBuffer();
		hql.append(String.format("select sum(%s)", TAP + "." + ScoTempoAndtPac.Fields.MAX_DIAS_PERMANENCIA.toString()));
		hql.append(String.format(" from %s, %s", ScoTempoAndtPac.class.getName() + " " + TAP, ScoLicitacao.class.getName() + " " + LCT));
		hql.append(String.format(" where %s = :lctNumero", LCT + "." + ScoLicitacao.Fields.NUMERO.toString()));
		hql.append(String.format(" and %s = %s", TAP + "." + ScoTempoAndtPac.Fields.MLC_CODIGO.toString(), LCT + "."+ ScoLicitacao.Fields.MODALIDADE_LICITACAO_CODIGO.toString()));
		hql.append(String.format(" and %s = :lcpCodigo", TAP + "." + ScoTempoAndtPac.Fields.LCP_CODIGO.toString()));
		Query query = createHibernateQuery(hql.toString());
		query.setInteger("lctNumero", lctNumero);
		query.setInteger("lcpCodigo", 0);
		Long sum = (Long) query.uniqueResult();
		return sum != null ? sum.intValue() : 0;
	}
	public DetachedCriteria criteriaLicitacao(Object parametro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoLicitacao.class);
		String strParametro = parametro.toString();
		Integer numero = null;
		if (CoreUtil.isNumeroInteger(strParametro)) {
			numero = Integer.valueOf(strParametro); }
		if (numero != null) {
			criteria.add(Restrictions.eq(ScoLicitacao.Fields.NUMERO.toString(), numero));
		} else {
			if (StringUtils.isNotBlank(strParametro)) {
				criteria.add(Restrictions.ilike(ScoLicitacao.Fields.DESCRICAO.toString(), strParametro, MatchMode.ANYWHERE));
			}
		}
		return criteria;
	}
	public List<ScoLicitacao> pesquisarLicitacoesPorNumeroDescricao(Object parametro) {
		DetachedCriteria criteria = criteriaLicitacao(parametro);
		return this.executeCriteria(criteria, 0, 100, ScoLicitacao.Fields.DESCRICAO.toString(), true);
	}
	/**
	 * Estoria 5488
	 * 
	 * @param numLicitacao
	 * @param codigoModalidade
	 * @param servidorDigitado
	 * @param dataDigitacao
	 * @return
	 */
	public List<ScoLicitacao> pesquisarLicitacoesLiberar(LicitacoesLiberarCriteriaVO criteria, Integer firstResult, Integer maxResult, String order, boolean asc) {
		DetachedCriteria detachedCriteria = getLicitacoesLiberarCriteria(criteria);
		return executeCriteria(detachedCriteria, firstResult, maxResult, order, asc);
	}
	/**
	 * Estoria 5488
	 * 
	 * @param numLicitacao
	 * @param codigoModalidade
	 * @param servidorDigitado
	 * @param dataDigitacao
	 * @return
	 */
	public Long pesquisarLicitacoesLiberarCount(LicitacoesLiberarCriteriaVO criteria) {
		DetachedCriteria detachedCriteria = getLicitacoesLiberarCriteria(criteria);
		return executeCriteriaCount(detachedCriteria);
	}
	private DetachedCriteria getLicitacoesLiberarCriteria(LicitacoesLiberarCriteriaVO criteria) {
		final String LCT = "LCT", VPS = "VPS", MLC = "MLC", PF = "PF";
		DetachedCriteria detached = DetachedCriteria.forClass(ScoLicitacao.class, LCT);
		detached.createAlias(path(LCT, ScoLicitacao.Fields.SERVIDOR_DIGITADO), VPS);
		detached.createAlias(path(VPS, RapServidores.Fields.PESSOA_FISICA), PF);
		detached.createAlias(path(LCT, ScoLicitacao.Fields.MODALIDADE_LICITACAO), MLC);
		detached.add(Restrictions.eq(path(LCT, ScoLicitacao.Fields.IND_EXCLUSAO), false));
		if (criteria.getNumero() != null) {
			detached.add(Restrictions.eq(path(LCT, ScoLicitacao.Fields.NUMERO), criteria.getNumero()));
		}
		if (criteria.getDescricao() != null) {
			detached.add(Restrictions.ilike(path(LCT, ScoLicitacao.Fields.DESCRICAO), criteria.getDescricao(), MatchMode.ANYWHERE));
		}
		if(criteria.getTipo() != null){
		   switch (criteria.getTipo()) {
			   case SC:
				  restrictBySc(detached, criteria);
				  break;
			   case SS:
				  restrictBySs(detached, criteria);
				  break;
			}
		}
		if (criteria.getModalidade() != null) {
			detached.add(Restrictions.eq(path(LCT, ScoLicitacao.Fields.MODALIDADE_LICITACAO), criteria.getModalidade()));
		}
		if (criteria.getGestor() != null) {
			detached.add(Restrictions.eq(path(LCT, ScoLicitacao.Fields.SERVIDOR_DIGITADO), criteria.getGestor()));
		}
		if (criteria.getGeracao() != null) {
			if (criteria.getGeracao().getInicio() != null) {
				detached.add(Restrictions.ge(path(LCT, ScoLicitacao.Fields.DT_DIGITACAO), DateUtil.obterDataComHoraInical(criteria.getGeracao().getInicio())));
			}
			if (criteria.getGeracao().getFim() != null) {
				detached.add(Restrictions.le(path(LCT, ScoLicitacao.Fields.DT_DIGITACAO), DateUtil.obterDataComHoraFinal(criteria.getGeracao().getFim())));
			}
		}
		return detached;
	}
	private void restrictBySs(DetachedCriteria detached, LicitacoesLiberarCriteriaVO criteria) {
		final String ITL = "ITL", LIT = "LIT", FSL = "FSL", SS = "SC";
		boolean hasFilter = false;
		DetachedCriteria subquery = DetachedCriteria.forClass(ScoItemLicitacao.class, ITL);
		if (criteria.getSolicitacaoServicoId() != null) {
			subquery.add(Restrictions.eq(path(SS, ScoSolicitacaoServico.Fields.NUMERO), criteria.getSolicitacaoServicoId()));
			hasFilter = true;
		}
		if (criteria.getServico() != null) {
			subquery.add(Restrictions.eq(path(SS, ScoSolicitacaoServico.Fields.SERVICO), criteria.getServico()));
			hasFilter = true;
		}
		if (hasFilter) {
			subquery.setProjection(Projections.property(path(ITL, ScoItemLicitacao.Fields.NUMERO)));
			subquery.createAlias(path(ITL, ScoItemLicitacao.Fields.LICITACAO), LIT);
			subquery.createAlias(path(ITL, ScoItemLicitacao.Fields.FASES_SOLICITACAO), FSL);
			subquery.createAlias(path(FSL, ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO), SS);
			subquery.add(Restrictions.eqProperty(path(LIT, ScoLicitacao.Fields.NUMERO), path(detached.getAlias(), ScoLicitacao.Fields.NUMERO)));
			detached.add(Subqueries.exists(subquery));
		}
	}
	private void restrictBySc(DetachedCriteria detached, LicitacoesLiberarCriteriaVO criteria) {
		final String ITL = "ITL", LIT = "LIT", FSL = "FSL", SC = "SC";
		boolean hasFilter = false;
		DetachedCriteria subquery = DetachedCriteria.forClass(ScoItemLicitacao.class, ITL);
		if (criteria.getSolicitacaoCompraId() != null) {
			subquery.add(Restrictions.eq(path(SC, ScoSolicitacaoDeCompra.Fields.NUMERO), criteria.getSolicitacaoCompraId()));
			hasFilter = true;
		}
		if (criteria.getMaterial() != null) {
			subquery.add(Restrictions.eq(path(SC, ScoSolicitacaoDeCompra.Fields.MATERIAL), criteria.getMaterial()));
			hasFilter = true;}
		if (hasFilter) {
			subquery.setProjection(Projections.property(path(ITL, ScoItemLicitacao.Fields.NUMERO)));
			subquery.createAlias(path(ITL, ScoItemLicitacao.Fields.LICITACAO), LIT);
			subquery.createAlias(path(ITL, ScoItemLicitacao.Fields.FASES_SOLICITACAO), FSL);
			subquery.createAlias(path(FSL, ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS), SC);
			subquery.add(Restrictions.eqProperty(path(LIT, ScoLicitacao.Fields.NUMERO), path(detached.getAlias(), ScoLicitacao.Fields.NUMERO)));
			detached.add(Subqueries.exists(subquery));
		}
	}
	private String path(Object... parts) {
		StringBuffer path = new StringBuffer();
		for (int i = 0; i < parts.length; i++) {
			path.append(parts[i]);
			if (i < parts.length - 1) {
				path.append('.');
			}
		}
		return path.toString();
	}
	public ScoLicitacao obterLicitacaoPorNumIndExclusao(Integer numLicitacao, Boolean indExclusao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoLicitacao.class, "lic");
		if (numLicitacao != null) {
			criteria.add(Restrictions.eq("lic." + ScoLicitacao.Fields.NUMERO.toString(), numLicitacao));
		}
		criteria.add(Restrictions.eq("lic." + ScoLicitacao.Fields.IND_EXCLUSAO.toString(), indExclusao));
		return (ScoLicitacao) executeCriteriaUniqueResult(criteria);
	}
	/**
	 * Efetua a pesquisa para obter espelho de licitação para compra de
	 * materiais
	 * @param numLicitacao
	 * @param itemInicial
	 * @param itemFinal
	 * @return Lista com os dados de solicitação de compras para a licitação
	 */
	public List<RelatorioEspelhoPACVO> pesquisarDadosRelatorioEspelhoPACParaCompras(Integer numLicitacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoLicitacao.class, "LCT");
		final int FRN_NUMERO_ESTOQUE_GERAL = 1;
		ProjectionList p = Projections.projectionList();
		p.add(Projections.sum("SLC." + ScoSolicitacaoDeCompra.Fields.QUANTIDADE_APROVADA.toString()),RelatorioEspelhoPACVO.Fields.QUANTIDADE_APROVADA.toString());
		p.add(Projections.max("SLC." + ScoSolicitacaoDeCompra.Fields.DESCRICAO.toString()), RelatorioEspelhoPACVO.Fields.DESCRICAO_SOLICITACAO.toString());
		p.add(Projections.groupProperty("SLC." + ScoSolicitacaoDeCompra.Fields.NUMERO.toString()), RelatorioEspelhoPACVO.Fields.NUMERO_SOLICITACAO.toString());
		p.add(Projections.groupProperty("LCT." + ScoLicitacao.Fields.NUMERO.toString()), RelatorioEspelhoPACVO.Fields.NUMERO_LICITACAO.toString());
		p.add(Projections.groupProperty("LCT." + ScoLicitacao.Fields.ANO_COMPLEMENTO.toString()), RelatorioEspelhoPACVO.Fields.ANO_COMPLEMENTO.toString());
		p.add(Projections.groupProperty("LCT." + ScoLicitacao.Fields.NUM_DOC_LICIT.toString()),RelatorioEspelhoPACVO.Fields.NUMERO_DOCUMENTO_LICITACAO.toString());
		p.add(Projections.groupProperty("LCT." + ScoLicitacao.Fields.NUM_EDITAL.toString()), RelatorioEspelhoPACVO.Fields.NUMERO_EDITAL.toString());
		p.add(Projections.groupProperty("MOD_LICITACAO." + ScoModalidadeLicitacao.Fields.DESCRICAO.toString()),RelatorioEspelhoPACVO.Fields.DESCRICAO_MODALIDADE_LIC.toString());
		p.add(Projections.groupProperty("ITL." + ScoItemLicitacao.Fields.NUMERO.toString()), RelatorioEspelhoPACVO.Fields.NUMERO_ITEM_LICITACAO.toString());
		p.add(Projections.groupProperty("ITL." + ScoItemLicitacao.Fields.VALOR_UNITARIO.toString()), RelatorioEspelhoPACVO.Fields.VALOR_UNITARIO.toString());
		p.add(Projections.groupProperty("LOTE." + ScoLoteLicitacao.Fields.NUMERO.toString()), RelatorioEspelhoPACVO.Fields.NUMERO_LOTE.toString());
		p.add(Projections.groupProperty("MAT." + ScoMaterial.Fields.NOME.toString()), RelatorioEspelhoPACVO.Fields.NOME_MATERIAL_SERVICO.toString());
		p.add(Projections.groupProperty("MAT." + ScoMaterial.Fields.CODIGO.toString()), RelatorioEspelhoPACVO.Fields.CODIGO_MATERIAL_SERVICO.toString());
		p.add(Projections.groupProperty("MAT." + ScoMaterial.Fields.UNIDADE_MEDIDA.toString() + "." + ScoUnidadeMedida.Fields.CODIGO.toString()),RelatorioEspelhoPACVO.Fields.CODIGO_UNIDADE.toString());
		p.add(Projections.groupProperty("MAT." + ScoMaterial.Fields.DESCRICAO.toString()), RelatorioEspelhoPACVO.Fields.DESCRICAO_MATERIAL_SERVICO.toString());
		criteria.setProjection(p);
		criteria.createAlias(ScoLicitacao.Fields.MODALIDADE_LICITACAO.toString(), "MOD_LICITACAO", JoinType.INNER_JOIN);
		criteria.createAlias(ScoLicitacao.Fields.ITENS_LICITACAO.toString(), "ITL", JoinType.INNER_JOIN);
		criteria.createAlias("ITL." + ScoItemLicitacao.Fields.LOTE_LICITACAO.toString(), "LOTE", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ITL." + ScoItemLicitacao.Fields.FASES_SOLICITACAO.toString(), "FAS", JoinType.INNER_JOIN);
		criteria.createAlias("FAS." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC", JoinType.INNER_JOIN);
		criteria.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT", JoinType.INNER_JOIN);
		criteria.createAlias("MAT." + ScoMaterial.Fields.ESTOQUE_GERAL.toString(), "EST", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("LCT." + ScoLicitacao.Fields.NUMERO.toString(), numLicitacao));
		criteria.add(Restrictions.eq("FAS." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), false));
		criteria.add(Restrictions.eq("EST." + SceEstoqueGeral.Fields.FORNECEDOR.toString() + "." + ScoFornecedor.Fields.NUMERO.toString(),FRN_NUMERO_ESTOQUE_GERAL));
		criteria.add(Subqueries.propertyEq("EST." + SceEstoqueGeral.Fields.DATA_COMPETENCIA.toString(), obterSubQueryEstoqueDadosRelatorioEspelho()));
		criteria.addOrder(Order.asc("ITL." + ScoItemLicitacao.Fields.NUMERO.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(RelatorioEspelhoPACVO.class));
		return executeCriteria(criteria);
	}
	private DetachedCriteria obterSubQueryEstoqueDadosRelatorioEspelho() {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceEstoqueGeral.class, "estoque");
		criteria.setProjection(Projections.projectionList().add(Projections.max(SceEstoqueGeral.Fields.DATA_COMPETENCIA.toString())));
		criteria.add(Restrictions.eqProperty("estoque." + SceEstoqueGeral.Fields.MATERIAL.toString(), "EST." + SceEstoqueGeral.Fields.MATERIAL.toString()));
		return criteria;
	}
	public List<RelatorioEspelhoPACVO> pesquisarDadosRelatorioEspelhoPACParaServicos(Integer numLicitacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoLicitacao.class, "LCT");
		String nomeCampoDBQuantidade = null, nomeCampoDBFrequenciaEntrega = null, sqlSumProjection = null;
		nomeCampoDBQuantidade = CoreUtil.obterNomeCampoAtributo(ScoSolicitacaoServico.class, ScoSolicitacaoServico.Fields.QUANTIDADE_SOLICITADA.toString());
		nomeCampoDBFrequenciaEntrega = CoreUtil.obterNomeCampoAtributo(ScoLicitacao.class, ScoLicitacao.Fields.FREQUENCIA_ENTREGA.toString());
		sqlSumProjection = "SUM(sls5_." + nomeCampoDBQuantidade + " * this_." + nomeCampoDBFrequenciaEntrega + " ) as "+ RelatorioEspelhoPACVO.Fields.QUANTIDADE_APROVADA.toString();
		ProjectionList p = Projections.projectionList();
		p.add(Projections.sqlProjection(sqlSumProjection, new String[] { RelatorioEspelhoPACVO.Fields.QUANTIDADE_APROVADA.toString() },
				new Type[] { LongType.INSTANCE }), RelatorioEspelhoPACVO.Fields.QUANTIDADE_APROVADA.toString());
		p.add(Projections.max("SLS." + ScoSolicitacaoServico.Fields.DESCRICAO.toString()), RelatorioEspelhoPACVO.Fields.DESCRICAO_SOLICITACAO.toString());
		p.add(Projections.groupProperty("SLS." + ScoSolicitacaoServico.Fields.NUMERO.toString()), RelatorioEspelhoPACVO.Fields.NUMERO_SOLICITACAO.toString());
		p.add(Projections.groupProperty("LCT." + ScoLicitacao.Fields.NUMERO.toString()), RelatorioEspelhoPACVO.Fields.NUMERO_LICITACAO.toString());
		p.add(Projections.groupProperty("LCT." + ScoLicitacao.Fields.ANO_COMPLEMENTO.toString()), RelatorioEspelhoPACVO.Fields.ANO_COMPLEMENTO.toString());
		p.add(Projections.groupProperty("LCT." + ScoLicitacao.Fields.NUM_DOC_LICIT.toString()),
				RelatorioEspelhoPACVO.Fields.NUMERO_DOCUMENTO_LICITACAO.toString());
		p.add(Projections.groupProperty("LCT." + ScoLicitacao.Fields.NUM_EDITAL.toString()), RelatorioEspelhoPACVO.Fields.NUMERO_EDITAL.toString());
		p.add(Projections.groupProperty("MOD_LICITACAO." + ScoModalidadeLicitacao.Fields.DESCRICAO.toString()),
				RelatorioEspelhoPACVO.Fields.DESCRICAO_MODALIDADE_LIC.toString());
		p.add(Projections.groupProperty("ITL." + ScoItemLicitacao.Fields.NUMERO.toString()), RelatorioEspelhoPACVO.Fields.NUMERO_ITEM_LICITACAO.toString());
		p.add(Projections.groupProperty("ITL." + ScoItemLicitacao.Fields.VALOR_UNITARIO.toString()), RelatorioEspelhoPACVO.Fields.VALOR_UNITARIO.toString());
		p.add(Projections.groupProperty("LOTE." + ScoLoteLicitacao.Fields.NUMERO.toString()), RelatorioEspelhoPACVO.Fields.NUMERO_LOTE.toString());
		p.add(Projections.groupProperty("SRV." + ScoServico.Fields.NOME.toString()), RelatorioEspelhoPACVO.Fields.NOME_MATERIAL_SERVICO.toString());
		p.add(Projections.groupProperty("SRV." + ScoServico.Fields.CODIGO.toString()), RelatorioEspelhoPACVO.Fields.CODIGO_MATERIAL_SERVICO.toString());
		p.add(Projections.groupProperty("SRV." + ScoServico.Fields.DESCRICAO.toString()), RelatorioEspelhoPACVO.Fields.DESCRICAO_MATERIAL_SERVICO.toString());
		criteria.setProjection(p);
		criteria.createAlias(ScoLicitacao.Fields.MODALIDADE_LICITACAO.toString(), "MOD_LICITACAO", JoinType.INNER_JOIN);
		criteria.createAlias(ScoLicitacao.Fields.ITENS_LICITACAO.toString(), "ITL", JoinType.INNER_JOIN);
		criteria.createAlias("ITL." + ScoItemLicitacao.Fields.LOTE_LICITACAO.toString(), "LOTE", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ITL." + ScoItemLicitacao.Fields.FASES_SOLICITACAO.toString(), "FAS", JoinType.INNER_JOIN);
		criteria.createAlias("FAS." + ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO.toString(), "SLS", JoinType.INNER_JOIN);
		criteria.createAlias("SLS." + ScoSolicitacaoServico.Fields.SERVICO.toString(), "SRV", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("LCT." + ScoLicitacao.Fields.NUMERO.toString(), numLicitacao));
		criteria.add(Restrictions.eq("FAS." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), false));
		criteria.addOrder(Order.asc("ITL." + ScoItemLicitacao.Fields.NUMERO.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(RelatorioEspelhoPACVO.class));
		return executeCriteria(criteria);
	}
	public Long contarPacsParaJulgamento(PacParaJulgamentoCriteriaVO criteria) {
		DetachedCriteria detached = getPacsParaJulgamentoCriteria(criteria);
		return executeCriteriaCount(detached);
	}
	public List<VerbaGrupoSolicitacaoVO> pesquisarDadosRelatorioResumoVerbasGrupoParaCompras(Integer numLicitacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoLicitacao.class, "LCT");
		ProjectionList p = Projections.projectionList();
		p.add(Projections.sqlProjection("sum(slc3_.VALOR_UNIT_PREVISTO * (slc3_.QTDE_APROVADA+COALESCE(slc3_.QTDE_REFORCO,0))) as "
				+ VerbaGrupoSolicitacaoVO.Fields.VALOR_UNITARIO.toString(), new String[] { VerbaGrupoSolicitacaoVO.Fields.VALOR_UNITARIO.toString() },
				new Type[] { BigDecimalType.INSTANCE }));
		p.add(Projections.groupProperty("VBG." + FsoVerbaGestao.Fields.SEQ.toString()), VerbaGrupoSolicitacaoVO.Fields.VBG_SEQ.toString());
		p.add(Projections.groupProperty("VBG." + FsoVerbaGestao.Fields.DESCRICAO.toString()), VerbaGrupoSolicitacaoVO.Fields.VBG_DESCRICAO.toString());
		p.add(Projections.groupProperty("GND." + FsoGrupoNaturezaDespesa.Fields.CODIGO.toString()), VerbaGrupoSolicitacaoVO.Fields.GND_CODIGO.toString());
		p.add(Projections.groupProperty("GND." + FsoGrupoNaturezaDespesa.Fields.DESCRICAO.toString()), VerbaGrupoSolicitacaoVO.Fields.GND_DESCRICAO.toString());
		criteria.setProjection(p);
		criteria.createAlias(ScoLicitacao.Fields.ITENS_LICITACAO.toString(), "ITL", JoinType.INNER_JOIN);
		criteria.createAlias("ITL." + ScoItemLicitacao.Fields.FASES_SOLICITACAO.toString(), "FAS", JoinType.INNER_JOIN);
		criteria.createAlias("FAS." + ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString(), "SLC", JoinType.INNER_JOIN);
		criteria.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.NATUREZA_DESPESA.toString(), "NTD", JoinType.INNER_JOIN);
		criteria.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.VERBA_GESTAO.toString(), "VBG", JoinType.INNER_JOIN);
		criteria.createAlias("NTD." + FsoNaturezaDespesa.Fields.GRUPO_NATUREZA.toString(), "GND", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq(ScoLicitacao.Fields.NUMERO.toString(), numLicitacao));
		criteria.addOrder(Order.asc("VBG." + FsoVerbaGestao.Fields.SEQ.toString()));
		criteria.addOrder(Order.asc("GND." + FsoGrupoNaturezaDespesa.Fields.CODIGO.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(VerbaGrupoSolicitacaoVO.class));
		return executeCriteria(criteria);
	}
	public List<VerbaGrupoSolicitacaoVO> pesquisarDadosRelatorioResumoVerbasGrupoParaServicos(Integer numLicitacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoLicitacao.class, "LCT");
		ProjectionList p = Projections.projectionList();
		p.add(Projections.sqlProjection(
				"sum(sls3_.VALOR_UNIT_PREVISTO * sls3_.QTDE_SOLICITADA) as " + VerbaGrupoSolicitacaoVO.Fields.VALOR_UNITARIO.toString(),
				new String[] { VerbaGrupoSolicitacaoVO.Fields.VALOR_UNITARIO.toString() }, new Type[] { BigDecimalType.INSTANCE }));
		p.add(Projections.groupProperty("VBG." + FsoVerbaGestao.Fields.SEQ.toString()), VerbaGrupoSolicitacaoVO.Fields.VBG_SEQ.toString());
		p.add(Projections.groupProperty("VBG." + FsoVerbaGestao.Fields.DESCRICAO.toString()), VerbaGrupoSolicitacaoVO.Fields.VBG_DESCRICAO.toString());
		p.add(Projections.groupProperty("GND." + FsoGrupoNaturezaDespesa.Fields.CODIGO.toString()), VerbaGrupoSolicitacaoVO.Fields.GND_CODIGO.toString());
		p.add(Projections.groupProperty("GND." + FsoGrupoNaturezaDespesa.Fields.DESCRICAO.toString()), VerbaGrupoSolicitacaoVO.Fields.GND_DESCRICAO.toString());
		criteria.setProjection(p);
		criteria.createAlias(ScoLicitacao.Fields.ITENS_LICITACAO.toString(), "ITL", JoinType.INNER_JOIN);
		criteria.createAlias("ITL." + ScoItemLicitacao.Fields.FASES_SOLICITACAO.toString(), "FAS", JoinType.INNER_JOIN);
		criteria.createAlias("FAS." + ScoFaseSolicitacao.Fields.SOLICITACAO_SERVICO.toString(), "SLS", JoinType.INNER_JOIN);
		criteria.createAlias("SLS." + ScoSolicitacaoServico.Fields.NATUREZA_DESPESA.toString(), "NTD", JoinType.INNER_JOIN);
		criteria.createAlias("SLS." + ScoSolicitacaoServico.Fields.VERBA_GESTAO.toString(), "VBG", JoinType.INNER_JOIN);
		criteria.createAlias("NTD." + FsoNaturezaDespesa.Fields.GRUPO_NATUREZA.toString(), "GND", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq(ScoLicitacao.Fields.NUMERO.toString(), numLicitacao));
		criteria.addOrder(Order.asc("VBG." + FsoVerbaGestao.Fields.SEQ.toString()));
		criteria.addOrder(Order.asc("GND." + FsoGrupoNaturezaDespesa.Fields.CODIGO.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(VerbaGrupoSolicitacaoVO.class));
		return executeCriteria(criteria);
	}
	public List<PacParaJulgamentoVO> pesquisarPacsParaJulgamento(PacParaJulgamentoCriteriaVO criteria, int first, int max, String order, boolean asc) {
		final String MLC = "MLC";
		DetachedCriteria detached = getPacsParaJulgamentoCriteria(criteria);
		detached.createAlias(detached.getAlias() + "." + ScoLicitacao.Fields.MODALIDADE_LICITACAO.toString(), MLC);
		detached.setProjection(Projections.projectionList()
				.add(Property.forName(detached.getAlias() + "." + ScoLicitacao.Fields.NUMERO.toString()), PacParaJulgamentoVO.Field.NUMERO.toString())
				.add(Property.forName(detached.getAlias() + "." + ScoLicitacao.Fields.DESCRICAO.toString()), PacParaJulgamentoVO.Field.DESCRICAO.toString())
				.add(Property.forName(MLC + "." + ScoModalidadeLicitacao.Fields.DESCRICAO.toString()), PacParaJulgamentoVO.Field.MODALIDADE.toString())
				.add(Property.forName(detached.getAlias() + "." + ScoLicitacao.Fields.IND_SITUACAO.toString()), PacParaJulgamentoVO.Field.SITUACAO.toString()));
		detached.setResultTransformer(Transformers.aliasToBean(PacParaJulgamentoVO.class));
		detached.addOrder(Order.asc(detached.getAlias() + "." + ScoLicitacao.Fields.DESCRICAO.toString()));
		return executeCriteria(detached, first, max, order, asc);
	}
	private DetachedCriteria getPacsParaJulgamentoCriteria(PacParaJulgamentoCriteriaVO criteria) {
		final String LCT = "LCT";
		DetachedCriteria detached = DetachedCriteria.forClass(ScoLicitacao.class, LCT);
		if (criteria.getNroPac() != null) {
			detached.add(Restrictions.eq(LCT + "." + ScoLicitacao.Fields.NUMERO.toString(), criteria.getNroPac()));
		}
		if (criteria.getDescricao() != null) {
			detached.add(Restrictions.ilike(LCT + "." + ScoLicitacao.Fields.DESCRICAO.toString(), criteria.getDescricao(), MatchMode.ANYWHERE));
		}
		if (criteria.getModalidade() != null) {
			detached.add(Restrictions.eq(LCT + "." + ScoLicitacao.Fields.MODALIDADE_LICITACAO.toString(), criteria.getModalidade()));
		}
		if (criteria.getSituacao() != null) {
			detached.add(Restrictions.eq(LCT + "." + ScoLicitacao.Fields.IND_SITUACAO.toString(), criteria.getSituacao()));
		}
		if (criteria.getPacPossuiProposta() != null && criteria.getPacPossuiProposta()) {
			DetachedCriteria subQueryProposta = DetachedCriteria.forClass(ScoPropostaFornecedor.class, "PROP");
			subQueryProposta.setProjection(Projections.property("PROP." + ScoPropostaFornecedor.Fields.LICITACAO_ID.toString()));
			subQueryProposta.add(Restrictions.eqProperty("PROP." + ScoPropostaFornecedor.Fields.LICITACAO_ID.toString(),
					"LCT." + ScoLicitacao.Fields.NUMERO.toString()));
			detached.add(Subqueries.propertyIn(LCT + "." + ScoLicitacao.Fields.NUMERO.toString(), subQueryProposta));
		}
		if (criteria.getNroPac() == null) {
			detached.add(Restrictions.ne(LCT + "." + ScoLicitacao.Fields.IND_SITUACAO.toString(), DominioSituacaoLicitacao.CL));

			detached.add(Restrictions.ne(LCT + "." + ScoLicitacao.Fields.IND_SITUACAO.toString(), DominioSituacaoLicitacao.EF));

			detached.add(Restrictions.eq(LCT + "." + ScoLicitacao.Fields.IND_EXCLUSAO.toString(), false));
		}
		return detached;
	}
	public ScoLicitacao obterLicitacaoModalidadePorNumeroPac(Integer numPac) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoLicitacao.class, "LCT");
		criteria.createAlias("LCT." + ScoLicitacao.Fields.SERVIDOR_DIGITADO.toString(), "SDG");
		criteria.createAlias("SDG." + RapServidores.Fields.PESSOA_FISICA.toString(), "SPF");
		criteria.createAlias("LCT." + ScoLicitacao.Fields.MODALIDADE_LICITACAO.toString(), "MLC");
		criteria.createAlias("LCT." + ScoLicitacao.Fields.ITENS_LICITACAO.toString(), "IT_LIC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("LCT." + ScoLicitacao.Fields.SERVIDOR_GESTOR.toString(), "SERV", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SERV." + RapServidores.Fields.PESSOA_FISICA.toString(), "SERVPF", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SERV." + RapServidores.Fields.RAMAL_TELEFONICO.toString(), "RAM", JoinType.LEFT_OUTER_JOIN);		
		criteria.add(Restrictions.eq(ScoLicitacao.Fields.NUMERO.toString(), numPac));
		return (ScoLicitacao) executeCriteriaUniqueResult(criteria);
	}
	private String montarConsultaAf(ScoLicitacao licitacao, ScoGrupoMaterial grupoMaterial, Boolean indProcNaoAptoGer, Boolean count) {
		StringBuilder hql = new StringBuilder(1500);
		if (count) {
			hql.append(" SELECT COUNT(LCT.NUMERO) AS qtd");
		} else {
			hql.append(" SELECT LCT.NUMERO AS lct_numero");
		}
		hql.append(" FROM AGH.SCO_LICITACOES LCT ");
		hql.append(" INNER JOIN AGH.SCO_MODALIDADE_LICITACOES MLC ON LCT.MLC_CODIGO = MLC.CODIGO ");
		hql.append(" WHERE LCT.IND_EXCLUSAO = 'N' ");
		hql.append(" AND EXISTS (");
		hql.append("SELECT 1");
		hql.append(" FROM AGH.SCO_ITENS_LICITACOES ITL ");
		hql.append(" LEFT JOIN AGH.SCO_FASES_SOLICITACOES FSE ON ");
		hql.append(" FSE.ITL_LCT_NUMERO = ");
		hql.append(" ITL.LCT_NUMERO");
		hql.append(" AND FSE.ITL_NUMERO = ");
		hql.append(" ITL.NUMERO");
		hql.append(" LEFT JOIN AGH.SCO_PROPOSTAS_FORNECEDORES PRF ON ");
		hql.append(" (PRF.LCT_NUMERO = ITL.LCT_NUMERO)");
		hql.append(" LEFT JOIN AGH.SCO_ITEM_PROPOSTAS_FORNECEDOR IPF ON ");
		hql.append(" (PRF.LCT_NUMERO = IPF.PFR_LCT_NUMERO AND PRF.FRN_NUMERO = IPF.PFR_FRN_NUMERO");
		hql.append(" AND IPF.ITL_NUMERO = ITL.NUMERO");
		hql.append(" AND IPF.IND_EXCLUSAO = 'N' )");
		hql.append(" LEFT JOIN AGH.SCO_SOLICITACOES_DE_COMPRAS SLC ON ");
		hql.append(" FSE.SLC_NUMERO = SLC.NUMERO");
		hql.append(" LEFT JOIN AGH.SCO_SOLICITACOES_SERVICO SLS ON ");
		hql.append(" FSE.SLS_NUMERO =  SLS.NUMERO");
		hql.append(" WHERE");
		hql.append(" ITL.LCT_NUMERO =  LCT.NUMERO");
		hql.append(" AND FSE.IND_EXCLUSAO = 'N' ");
		hql.append(" AND FSE.SLC_NUMERO NOT IN ( ");
		hql.append(" SELECT FSE1.SLC_NUMERO FROM ");
		hql.append(" AGH.SCO_FASES_SOLICITACOES FSE1 ");
		hql.append(" WHERE FSE1.SLC_NUMERO = FSE.SLC_NUMERO ");
		hql.append(" AND FSE1.IAF_AFN_NUMERO IS NOT NULL ");
		hql.append(" AND FSE1.IND_EXCLUSAO = 'N' )");
		hql.append(" AND FSE.SLS_NUMERO NOT IN ( ");
		hql.append(" SELECT FSE1.SLS_NUMERO FROM ");
		hql.append(" AGH.SCO_FASES_SOLICITACOES FSE1 ");
		hql.append(" WHERE FSE1.SLS_NUMERO  = FSE.SLS_NUMERO ");
		hql.append(" AND FSE1.IAF_AFN_NUMERO IS NOT NULL ");
		hql.append(" AND FSE1.IND_EXCLUSAO = 'N' )");
		hql.append(')');
		if (licitacao.getNumero() != null) {
			hql.append(" AND LCT.NUMERO = :lctNumero "); }
		if (licitacao.getModalidadeLicitacao() != null && licitacao.getModalidadeLicitacao().getCodigo() != null) {
			hql.append(" AND LCT.MLC_CODIGO = :modalidadeLicitacao ");	}
		if (licitacao.getServidorGestor() != null) {
			hql.append(" AND LCT.SER_VIN_CODIGO_GESTOR = :vinCodigoGestor ")
			.append(" AND LCT.SER_MATRICULA_GESTOR  = :matriculaGestor ");
		}
		if (indProcNaoAptoGer != null && indProcNaoAptoGer) {
			hql.append(" AND NOT EXISTS (SELECT 1")
			.append(" FROM AGH.SCO_ITENS_LICITACOES ITL ")
			.append(" WHERE ITL.LCT_NUMERO = LCT.NUMERO ")
			.append(" AND ITL.IND_PROPOSTA_ESCOLHIDA = 'S'")
			.append(" AND ITL.IND_EXCLUSAO = 'N'")
			.append(" AND ITL.MOTIVO_CANCEL IS NULL)");
		} else {
			hql.append(" AND EXISTS (SELECT 1")
			.append(" FROM AGH.SCO_ITENS_LICITACOES ITL ")
			.append(" WHERE ITL.LCT_NUMERO = LCT.NUMERO ")
			.append(" AND ITL.IND_PROPOSTA_ESCOLHIDA = 'S'")
			.append(" AND ITL.IND_EXCLUSAO = 'N'")
			.append(" AND ITL.MOTIVO_CANCEL IS NULL")
			.append(" )");
		}
		if (grupoMaterial != null) {
			if (grupoMaterial.getCodigo() != null) {
				hql.append(" AND EXISTS (SELECT 1")
				.append(" FROM AGH.SCO_ITENS_LICITACOES ITL ")
				.append(" INNER JOIN AGH.SCO_FASES_SOLICITACOES FSL ON ")
				.append(" (ITL.NUMERO = FSL.ITL_NUMERO AND ITL.LCT_NUMERO = FSL.ITL_LCT_NUMERO)")
				.append(" INNER JOIN AGH.SCO_SOLICITACOES_DE_COMPRAS SLC  ON ")
				.append(" (SLC.NUMERO  = FSL.SLC_NUMERO) ")
				.append(" INNER JOIN AGH.SCO_MATERIAIS MAT ON (")
				.append(" MAT.CODIGO = ")
				.append(" SLC.MAT_CODIGO) ")
				.append(" WHERE")
				.append(" ITL.LCT_NUMERO = ")
				.append(" LCT.NUMERO AND ")
				.append(" MAT.GMT_CODIGO = :grupoMaterialCodigo )");
			}
		}
		if (!count) {
			hql.append("ORDER BY LCT.NUMERO DESC");
		}
		return hql.toString();
	}
	public List<ScoLicitacao> listarPacsParaAF(Integer firstResult, Integer maxResult, ScoLicitacao licitacao, ScoGrupoMaterial grupoMaterial,
			Boolean indProcNaoAptoGer) {
		String hql = montarConsultaAf(licitacao, grupoMaterial, indProcNaoAptoGer, false);
		SQLQuery query = createSQLQuery(hql);
		query.addScalar("lct_numero", IntegerType.INSTANCE);
		if (grupoMaterial != null) {
			if (grupoMaterial.getCodigo() != null) {
				query.setParameter("grupoMaterialCodigo", grupoMaterial.getCodigo());
			}
		}
		if (licitacao.getNumero() != null) {
			query.setParameter("lctNumero", licitacao.getNumero());
		}
		if (licitacao.getModalidadeLicitacao() != null && licitacao.getModalidadeLicitacao().getCodigo() != null) {
			query.setParameter("modalidadeLicitacao", licitacao.getModalidadeLicitacao().getCodigo());
		}
		if (licitacao.getServidorGestor() != null) {
			query.setParameter("vinCodigoGestor", licitacao.getServidorGestor().getId().getVinCodigo());
			query.setParameter("matriculaGestor", licitacao.getServidorGestor().getId().getMatricula());
		}
		query.setFirstResult(firstResult);
		query.setMaxResults(maxResult);
		List<ScoLicitacao> listaRetorno = new ArrayList<ScoLicitacao>();
		List<Integer> lista = query.list();
		if (lista != null && !lista.isEmpty()) {
			for (Integer numLct : lista) {
				ScoLicitacao scoLicitacao = this.obterPorChavePrimaria(numLct,true, ScoLicitacao.Fields.MODALIDADE_LICITACAO);
				listaRetorno.add(scoLicitacao);	
			}
		}
		return listaRetorno;
	}
	public Integer listarPacsParaAFCount(ScoLicitacao licitacao, ScoGrupoMaterial grupoMaterial, Boolean indProcNaoAptoGer) {
		String hql = montarConsultaAf(licitacao, grupoMaterial, indProcNaoAptoGer, true);
		SQLQuery query = createSQLQuery(hql);
		query.addScalar("qtd", IntegerType.INSTANCE);
		if (grupoMaterial != null) {
			if (grupoMaterial.getCodigo() != null) {
				query.setParameter("grupoMaterialCodigo", grupoMaterial.getCodigo());
			}
		}
		if (licitacao.getNumero() != null) {
			query.setParameter("lctNumero", licitacao.getNumero());
		}
		if (licitacao.getModalidadeLicitacao() != null && licitacao.getModalidadeLicitacao().getCodigo() != null) {
			query.setParameter("modalidadeLicitacao", licitacao.getModalidadeLicitacao().getCodigo());
		}
		if (licitacao.getServidorGestor() != null) {
			query.setParameter("vinCodigoGestor", licitacao.getServidorGestor().getId().getVinCodigo());
			query.setParameter("matriculaGestor", licitacao.getServidorGestor().getId().getMatricula());
		}
		
		Integer total = 0 ;
		List<Integer> lista = query.list();
		if (lista != null) {
			total = lista.get(0).intValue();
		}
		
		return total;
	}
	public Long verificarValorAssinadoPorAf(Integer afnNumero) {
		final StringBuilder hql = new StringBuilder(700);
		hql.append("select count(*) ")
		.append("from ScoLicitacao lct, ScoAutorizacaoFornJn afnjn, ScoItemAutorizacaoFornJn iafjn, ScoItemAutorizacaoForn iaf, ScoFaseSolicitacao fsc ")
		.append("where lct.").append(ScoLicitacao.Fields.NUMERO.toString()).append(" = ")
				.append("afnjn." ).append( ScoAutorizacaoFornJn.Fields.PFR_LCT_NUMERO.toString())
		.append(" and afnjn." ).append( ScoAutorizacaoFornJn.Fields.NUMERO.toString()).append(" = ")
				.append(" iafjn." ).append( ScoItemAutorizacaoFornJn.Fields.AFN_NUMERO.toString())
		.append(" and iafjn." ).append( ScoItemAutorizacaoFornJn.Fields.AFN_NUMERO.toString()).append(" = ")
				.append(" iaf." ).append( ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN_NUMERO.toString())
		.append(" and iafjn." ).append( ScoAutorizacaoFornJn.Fields.NUMERO.toString()).append(" = ")
				.append(" iaf." ).append( ScoItemAutorizacaoForn.Fields.NUMERO.toString())
		.append(" and fsc." ).append( ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()).append(" = ")
				.append(" iafjn." ).append( ScoItemAutorizacaoFornJn.Fields.AFN_NUMERO.toString())
		.append(" and fsc." ).append( ScoFaseSolicitacao.Fields.IAF_NUMERO.toString()).append(" = ")
				.append(" iafjn." ).append( ScoItemAutorizacaoFornJn.Fields.NUMERO.toString())
		.append(" and fsc." ).append( ScoFaseSolicitacao.Fields.IAF_NUMERO.toString()).append(" = ")
				.append(" iafjn." ).append( ScoItemAutorizacaoFornJn.Fields.NUMERO.toString())
		.append(" and afnjn." ).append( ScoAutorizacaoFornJn.Fields.NUMERO.toString()).append(" = ").append(" :afnNumero")
		.append(" and iafjn." ).append( ScoItemAutorizacaoFornJn.Fields.IND_SITUACAO.toString()).append(" <> ").append(" :situacao")
		.append(" and afnjn." ).append( ScoAutorizacaoFornJn.Fields.SEQUENCIA_ALTERACAO.toString()).append(" in ").append(" (").append(" select max(")
				.append(ScoAutorizacaoFornJn.Fields.SEQUENCIA_ALTERACAO.toString()).append(" )")
		.append("from ScoAutorizacaoFornJn ")
		.append(" where " ).append( ScoAutorizacaoFornJn.Fields.NUMERO.toString()).append(" = afnjn." ).append( ScoAutorizacaoFornJn.Fields.NUMERO.toString())
		.append(" and " ).append( ScoAutorizacaoFornJn.Fields.IND_APROVADA.toString()).append(" in(:situacaoA,:situacaoE)")
		.append(" and " ).append( ScoAutorizacaoFornJn.Fields.MATRICULA_ASSINA_COORD.toString()).append(" is not null ")
		.append(" and " ).append( ScoAutorizacaoFornJn.Fields.DT_ASSINATURA_COORD.toString()).append(" is not null ").append(" )")
		.append(" and iafjn." ).append( ScoItemAutorizacaoFornJn.Fields.SEQUENCIA_ALTERACAO.toString()).append(" = ").append(" (").append(" select max(")
				.append(ScoItemAutorizacaoFornJn.Fields.SEQUENCIA_ALTERACAO.toString()).append(" )")
		.append("from ScoItemAutorizacaoFornJn iafj1 ")
		.append(" where iafj1." ).append( ScoItemAutorizacaoFornJn.Fields.AFN_NUMERO.toString()).append(
				" = iafjn." ).append( ScoItemAutorizacaoFornJn.Fields.AFN_NUMERO.toString())
		.append(" and iafj1." ).append( ScoItemAutorizacaoFornJn.Fields.NUMERO.toString()).append(" = iafjn." ).append( ScoItemAutorizacaoFornJn.Fields.NUMERO.toString())
		.append(" and iafj1." ).append( ScoItemAutorizacaoFornJn.Fields.SEQUENCIA_ALTERACAO.toString())
				.append(" <= afnjn." ).append( ScoAutorizacaoFornJn.Fields.SEQUENCIA_ALTERACAO.toString()).append(" )");
		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("afnNumero", afnNumero);
		query.setParameter("situacao", DominioSituacaoAutorizacaoFornecedor.EX);
		query.setParameter("situacaoA", DominioAprovadaAutorizacaoForn.A);
		query.setParameter("situacaoE", DominioAprovadaAutorizacaoForn.E);
		return (Long) query.uniqueResult();
	}
	public ScoLicitacao buscarLicitacaoPorNumero(Integer numeroPac) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoLicitacao.class);
		criteria.createAlias(ScoLicitacao.Fields.MODALIDADE_LICITACAO.toString(), ScoLicitacao.Fields.MODALIDADE_LICITACAO.toString(), JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(ScoLicitacao.Fields.NUMERO.toString(), numeroPac));
		return (ScoLicitacao) executeCriteriaUniqueResult(criteria);
	}
	@SuppressWarnings("unchecked")
	public AtaRegistroPrecoVO pesquisarInfoAtaDeRegistroDePreco(Integer pacCodigo, Integer fornecedor){
		StringBuilder sql = new StringBuilder(1000);
		sql.append(" select lct.numero as licitacao, ")
	       .append(" lct.num_doc_licit as docLicitacao, ")
	       .append(" lct.dt_limite_aceite_proposta as dtLimiteAceiteProposta, ")
	       .append(" lct.descricao as descricaoLicitacao, ")
	       .append(" lct.dt_abertura_proposta as dthrAberturaProposta, ")
	       .append(" frn.razao_social as razaoSocialForn, ")
	       .append(" frn.cgc as cgc, ")
	       .append(" frn.cpf as cnpj, ")
	       .append(" frn.logradouro as logradouroForn, ")
	       .append(" frn.nro_logradouro as nroLogradouro, ")
	       .append(" cdd.nome as cidade, ")
	       .append(" cdd.uf_sigla as ufSigla ")
	       .append(" from   agh.sco_licitacoes lct, ")
	       .append(" agh.sco_propostas_fornecedores pfr, ")
	       .append(" agh.sco_fornecedores frn, ")
	       .append(" agh.aip_cidades cdd ")
	       .append(" where  lct.numero = :pac ")
	       .append(" and    pfr.lct_numero = lct.numero ")
	       .append(" and    pfr.ind_exclusao ='N' ")
	       .append(" and    pfr.frn_numero = frn.numero ")
	       .append(" and    cdd.codigo     = frn.cdd_codigo ")
	       .append(" and    exists (select 1 ")
	       .append(" from   agh.sco_item_propostas_fornecedor ipfr ")
	       .append(" where  ipfr.pfr_frn_numero = pfr.frn_numero ")
	       .append(" and   ipfr.pfr_lct_numero = lct.numero ")
	       .append(" and   ipfr.ind_escolhido = 'S' ")
	       .append("  and   ipfr.pfr_frn_numero = :fornecedor) ");
		SQLQuery q = this.createSQLQuery(sql.toString());
		q.setInteger("pac", pacCodigo);
		q.setInteger("fornecedor", fornecedor);
		q.addScalar(AtaRegistroPrecoVO.Fields.LICITACAO.toString(), IntegerType.INSTANCE).
				addScalar(AtaRegistroPrecoVO.Fields.LICITACAO.toString(), 	IntegerType.INSTANCE).
				addScalar(AtaRegistroPrecoVO.Fields.DOC_LICITACAO.toString(), IntegerType.INSTANCE).
				addScalar(AtaRegistroPrecoVO.Fields.DT_LIMITE_ACEITE_PROPOSTA.toString(), DateType.INSTANCE).
				addScalar(AtaRegistroPrecoVO.Fields.DESCRICAO_LICITACAO.toString(), StringType.INSTANCE).
				addScalar(AtaRegistroPrecoVO.Fields.DT_HR_ABERTURA_PROPOSTA.toString(), DateType.INSTANCE).
				addScalar(AtaRegistroPrecoVO.Fields.RAZAO_SOCIAL_FORN.toString(), StringType.INSTANCE).
				addScalar(AtaRegistroPrecoVO.Fields.CNPJ.toString(), LongType.INSTANCE).
				addScalar(AtaRegistroPrecoVO.Fields.LOGRADOURO_FORN.toString(), StringType.INSTANCE).
				addScalar(AtaRegistroPrecoVO.Fields.NRO_LOGRADOURO.toString(), StringType.INSTANCE).
				addScalar(AtaRegistroPrecoVO.Fields.CIDADE.toString(), StringType.INSTANCE).
				addScalar(AtaRegistroPrecoVO.Fields.UF_SIGLA.toString(), StringType.INSTANCE).
				addScalar(AtaRegistroPrecoVO.Fields.CGC.toString(), StringType.INSTANCE).
				setResultTransformer(Transformers.aliasToBean(AtaRegistroPrecoVO.class)).uniqueResult();
		List<AtaRegistroPrecoVO> lista = q.list();
		if(lista!= null && lista.size() > 0){
			return lista.get(0);
		}
		
		return null;
	}
	public List<ScoLicitacao> buscarLicitacaoPorNumero(Integer numeroPac, String[] modalidades) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoLicitacao.class);
		criteria.add(Restrictions.eq(ScoLicitacao.Fields.NUMERO.toString(), numeroPac));
        criteria.createAlias(ScoLicitacao.Fields.MODALIDADE_LICITACAO.toString(), "MDL");
		criteria.add(Restrictions.in("MDL."+ScoModalidadeLicitacao.Fields.CODIGO.toString(), modalidades));
		return executeCriteria(criteria);
	}
	public List<RelatorioEpVo> buscaDadosRelatorioEP(Integer anoEP) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoLicitacao.class, "LCT");
		criteria.createAlias("LCT."+ScoLicitacao.Fields.MODALIDADE_LICITACAO.toString(), "MDL");
		criteria.setProjection(Projections.projectionList().
				add(Projections.property("LCT."+ScoLicitacao.Fields.NUMERO.toString()), RelatorioEpVo.Fields.PAC.toString()).
				add(Projections.property("LCT."+ScoLicitacao.Fields.DESCRICAO.toString()), RelatorioEpVo.Fields.LCT_DESCRICAO.toString()).
				add(Projections.property("MDL."+ScoModalidadeLicitacao.Fields.DESCRICAO.toString()), RelatorioEpVo.Fields.DESCRICAO.toString()).
				add(Projections.property("LCT."+ScoLicitacao.Fields.TIPO.toString()), RelatorioEpVo.Fields.TIPO.toString()).
				add(Projections.property("LCT."+ScoLicitacao.Fields.INCISO_ARTIGO_LICITACAO.toString()), RelatorioEpVo.Fields.INCISO.toString()).
				add(Projections.property("LCT."+ScoLicitacao.Fields.ARTIGO_LICITACAO.toString()), RelatorioEpVo.Fields.ARTIGO.toString()).
				add(Projections.property("LCT."+ScoLicitacao.Fields.DT_DIGITACAO.toString()), RelatorioEpVo.Fields.DT_GERACAO.toString()));
		criteria.add(Subqueries.propertyNotIn("LCT."+ScoLicitacao.Fields.NUMERO.toString(), getNotInSubquery()));
		criteria.add(Subqueries.propertyIn("LCT."+ScoLicitacao.Fields.NUMERO.toString(), getInSubquery(montaDataInicial(anoEP), montaDataFinal(anoEP))));
		criteria.addOrder(Order.asc("LCT."+ScoLicitacao.Fields.NUMERO.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(RelatorioEpVo.class));
		return executeCriteria(criteria);
	}
	private Date montaDataFinal(Integer anoEP) {
		return DateUtil.obterData((anoEP+1), 1, 1);
	}
	private Date montaDataInicial(Integer anoEP) {
		return DateUtil.obterData(anoEP, 1, 1);
	}
	private DetachedCriteria getNotInSubquery() {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAutorizacaoForn.class, "SAF");
		criteria.setProjection(Projections.projectionList().add(Projections.property("SAF."+ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString())));
		criteria.add(Restrictions.eqProperty("SAF."+ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString(), "LCT."+ScoLicitacao.Fields.NUMERO.toString()));
		criteria.add(Restrictions.in("SAF."+ScoAutorizacaoForn.Fields.SITUACAO.toString(), getDomainList()));
		return criteria;
	}	
	private List<DominioSituacaoAutorizacaoFornecimento> getDomainList(){
		List<DominioSituacaoAutorizacaoFornecimento> paramList = new ArrayList<DominioSituacaoAutorizacaoFornecimento>();
		paramList.add(DominioSituacaoAutorizacaoFornecimento.AE);
		paramList.add(DominioSituacaoAutorizacaoFornecimento.PA);
		return paramList;
	}	
	private DetachedCriteria getInSubquery(Date dataInicial, Date dataFinal) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceNotaRecebimento.class, "NRS");
		criteria.createAlias("NRS."+SceNotaRecebimento.Fields.AUTORIZACAO_FORN.toString(), "AFN");
		criteria.setProjection(Projections.projectionList().add(Projections.property("AFN."+ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString())));
		criteria.add(Restrictions.eqProperty("AFN."+ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString(), "LCT."+ScoLicitacao.Fields.NUMERO.toString()));
		criteria.add(Restrictions.between("NRS."+SceNotaRecebimento.Fields.DATA_GERACAO.toString(), dataInicial, dataFinal));
		return criteria;
	}		
	public Long pesquisarLicitacoesCount(Object parametro) {
		DetachedCriteria criteria = criteriaLicitacao(parametro);
		return executeCriteriaCount(criteria);
	}	
	public List<ScoLicitacao> pesquisarLicitacao(ScoLicitacao licitacao,Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		DetachedCriteria criteria = criteriaLicitacao(licitacao.getNumero());
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}	
	public Long pesquisarUltimasComprasMateriasCount(String modl, Date DataNrInicial, Integer matCodigo) {
		DetachedCriteria criteria = createUltimasComprasMaterias(modl, DataNrInicial, matCodigo);
		return executeCriteriaCount(criteria);
	}	
	public List<ScoUltimasComprasMaterialVO> pesquisarUltimasComprasMaterias(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,String modl, Date DataNrInicial, Integer matCodigo) {
		DetachedCriteria criteria = createUltimasComprasMaterias(modl, DataNrInicial, matCodigo);
		criteria.addOrder(Order.desc("NRS."+SceNotaRecebimento.Fields.NUMERO_NR.toString()));
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}	
	public DetachedCriteria createUltimasComprasMaterias(String modl, Date DataNrInicial, Integer matCodigo) {
		DetachedCriteria subCriteria = DetachedCriteria.forClass(ScoLicitacao.class, "LCT");
		subCriteria.createAlias("LCT." + ScoLicitacao.Fields.MODALIDADE_LICITACAO.toString(), "MLC");
		if(modl!=null){
			subCriteria.add(Restrictions.eq("LCT."+ScoLicitacao.Fields.MLC_CODIGO.toString(), modl));
		}	
		subCriteria.setProjection(Projections.property("LCT." + ScoLicitacao.Fields.NUMERO.toString()));
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoAutorizacaoForn.class, "AFN");
		subCriteria.add(Property.forName("AFN." + ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString())
				.eqProperty("LCT." + ScoLicitacao.Fields.NUMERO.toString()));
		criteria.add(Subqueries.exists(subCriteria));
		criteria.createAlias("AFN." + ScoAutorizacaoForn.Fields.ITENSAUTORIZACAOFORN.toString(), "IAF");
		criteria.createAlias("AFN." + ScoAutorizacaoForn.Fields.CONDICAO_PAGAMENTO_PROPOS.toString(), "CDP");
		criteria.createAlias("CDP." + ScoCondicaoPagamentoPropos.Fields.FORMA_PAGAMENTO.toString(), "FPG");
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.FASES_SOLICITACAO.toString(), "FSC");
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.ITEM_NOTA_RECEBIMENTO.toString(), "INR");
		criteria.createAlias("INR." + SceItemNotaRecebimento.Fields.NOTA_RECEBIMENTO.toString(), "NRS");
		criteria.createAlias("NRS." + SceNotaRecebimento.Fields.DOCUMENTO_FISCAL_ENTRADA.toString(), "DFE");
		criteria.createAlias("AFN." + ScoAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR.toString(), "PFR");
		criteria.createAlias("PFR." + ScoPropostaFornecedor.Fields.FORNECEDOR.toString(), "FRN");
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.NOME_COMERCIAL.toString(), "NC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("IAF." + ScoItemAutorizacaoForn.Fields.MARCA_COMERCIAL.toString(), "MCM",JoinType.LEFT_OUTER_JOIN);
		criteria.setProjection(Projections.projectionList().
				add(Projections.property("FSC."+ScoFaseSolicitacao.Fields.SLC_NUMERO.toString()), ScoUltimasComprasMaterialVO.Fields.SOLICITACAO.toString()).
				add(Projections.property("AFN."+ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString()), ScoUltimasComprasMaterialVO.Fields.NRO_AF.toString()).
				add(Projections.property("AFN."+ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString()), ScoUltimasComprasMaterialVO.Fields.CP.toString()).
				add(Projections.property("NRS."+SceNotaRecebimento.Fields.NUMERO_NR.toString()), ScoUltimasComprasMaterialVO.Fields.NUMERO_NR.toString()).
				add(Projections.property("NRS."+SceNotaRecebimento.Fields.DATA_GERACAO.toString()), ScoUltimasComprasMaterialVO.Fields.DATA_NR.toString()).
				add(Projections.property("DFE."+SceDocumentoFiscalEntrada.Fields.NUMERO.toString()), ScoUltimasComprasMaterialVO.Fields.NOTA_FISCAL.toString()).
				add(Projections.property("FPG."+ScoFormaPagamento.Fields.DESCRICAO.toString()), ScoUltimasComprasMaterialVO.Fields.FORMA_PGTO.toString()).
				add(Projections.property("FPG."+ScoFormaPagamento.Fields.SIGLA.toString()), ScoUltimasComprasMaterialVO.Fields.SIGLA.toString()).
				add(Projections.property("INR."+SceItemNotaRecebimento.Fields.QUANTIDADE.toString()), ScoUltimasComprasMaterialVO.Fields.QTDE.toString()).
				add(Projections.property("INR."+SceItemNotaRecebimento.Fields.VALOR.toString()), ScoUltimasComprasMaterialVO.Fields.VALOR.toString()).
				add(Projections.property("FRN."+ScoFornecedor.Fields.NUMERO.toString()), ScoUltimasComprasMaterialVO.Fields.FORNECEDOR_NRO.toString()).
				add(Projections.property("FRN."+ScoFornecedor.Fields.RAZAO_SOCIAL.toString()), ScoUltimasComprasMaterialVO.Fields.RAZAO_SOCIAL.toString()).
				add(Projections.property("FRN."+ScoFornecedor.Fields.CGC.toString()), ScoUltimasComprasMaterialVO.Fields.CNPJ.toString()).
				add(Projections.property("FRN."+ScoFornecedor.Fields.CPF.toString()), ScoUltimasComprasMaterialVO.Fields.CPF.toString()).
				add(Projections.property("FRN."+ScoFornecedor.Fields.DDI.toString()), ScoUltimasComprasMaterialVO.Fields.DDI.toString()).
				add(Projections.property("FRN."+ScoFornecedor.Fields.DDD.toString()), ScoUltimasComprasMaterialVO.Fields.DDD.toString()).
				add(Projections.property("FRN."+ScoFornecedor.Fields.FONE.toString()), ScoUltimasComprasMaterialVO.Fields.FONE.toString()).
				add(Projections.property("MCM."+ScoMarcaComercial.Fields.DESCRICAO.toString()), ScoUltimasComprasMaterialVO.Fields.MARCA_COMERCIAL.toString()).
				add(Projections.property("NC."+ScoNomeComercial.Fields.NOME.toString()), ScoUltimasComprasMaterialVO.Fields.NOME_COMERCIAL.toString()).
				add(Projections.property("AFN."+ScoAutorizacaoForn.Fields.NUMERO.toString()), ScoUltimasComprasMaterialVO.Fields.AFN_NUMERO.toString()));
		criteria.add(Subqueries.propertyIn("FSC."+ScoFaseSolicitacao.Fields.SLC_NUMERO.toString(), getFasesSolicitacaoInSubquery()));
		criteria.add(Subqueries.propertyIn("NRS." + SceNotaRecebimento.Fields.NUMERO_NR.toString(), getNotaRecebimentoInSubquery()));
		criteria.add(Subqueries.propertyNotIn("DFE."+SceDocumentoFiscalEntrada.Fields.SEQ.toString(), getFornecedoresNotInSubquery()));
		criteria.add(Restrictions.eq("FSC."+ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));		
		if(matCodigo!=null){
			criteria.add(Restrictions.eq("INR."+SceItemNotaRecebimento.Fields.MAT_CODIGO.toString(), matCodigo));
		}		
		if(DataNrInicial!=null){
			criteria.add(Restrictions.ge("NRS."+SceNotaRecebimento.Fields.DATA_GERACAO.toString(), DataNrInicial));
		}		
		criteria.setResultTransformer(Transformers.aliasToBean(ScoUltimasComprasMaterialVO.class));
		return criteria;
	}	
	private DetachedCriteria getFasesSolicitacaoInSubquery(){
		DetachedCriteria subCriteria2 = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FSC2");
		subCriteria2.add(Property.forName("IAF." +ScoItemAutorizacaoForn.Fields.ID_AUT_FORN_NUMERO.toString())
				.eqProperty("FSC2." + ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()));
		subCriteria2.add(Property.forName("IAF." + ScoItemAutorizacaoForn.Fields.NUMERO.toString())
				.eqProperty("FSC2." + ScoFaseSolicitacao.Fields.IAF_NUMERO.toString()));
		subCriteria2.add(Restrictions.eq("FSC2."+ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		subCriteria2.setProjection(Projections.projectionList().add(Projections.max("FSC2." + ScoFaseSolicitacao.Fields.SLC_NUMERO.toString())));
		return subCriteria2;
	}	
	private DetachedCriteria getNotaRecebimentoInSubquery(){
		DetachedCriteria criteria = DetachedCriteria.forClass(SceItemNotaRecebimento.class, "IN");
		criteria.createAlias("IN." + SceItemNotaRecebimento.Fields.NOTA_RECEBIMENTO.toString(), "NR");
		criteria.add(Property.forName("IN." + SceItemNotaRecebimento.Fields.MATERIAL.toString()).eqProperty("INR." + SceItemNotaRecebimento.Fields.MATERIAL.toString()));
		criteria.add(Property.forName("IN." + SceItemNotaRecebimento.Fields.ITEM_AUTORIZACAO_FORN.toString()).eqProperty("INR." + SceItemNotaRecebimento.Fields.ITEM_AUTORIZACAO_FORN.toString()));
		criteria.add(Restrictions.eq("NR."+SceNotaRecebimento.Fields.IND_ESTORNO.toString(), Boolean.FALSE));
		criteria.setProjection(Projections.projectionList().add(Projections.max("IN." + SceItemNotaRecebimento.Fields.NOTA_RECEBIMENTO_SEQ.toString())));
		return criteria;
	}	
	private DetachedCriteria getFornecedoresNotInSubquery() {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceDevolucaoFornecedor.class, "DFN");
		criteria.setProjection(Projections.projectionList().add(Projections.property("DFN."+SceDevolucaoFornecedor.Fields.DFE_SEQ.toString())));
		return criteria;
	}
	public ScoUltimasComprasMaterialVO pesquisarUltimasComprasMateriasLicitacao(Integer nroLicitacao){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoLicitacao.class, "LCT");
		criteria.createAlias("LCT." + ScoLicitacao.Fields.MODALIDADE_LICITACAO.toString(), "MLC");
		criteria.add(Restrictions.eq("LCT." + ScoLicitacao.Fields.NUMERO.toString(),nroLicitacao));
		criteria.setProjection(Projections.projectionList().
				add(Projections.property("LCT."+ScoLicitacao.Fields.NUMERO.toString()), ScoUltimasComprasMaterialVO.Fields.NRO_PAC.toString()).
				add(Projections.property("MLC."+ScoModalidadeLicitacao.Fields.DESCRICAO.toString()), ScoUltimasComprasMaterialVO.Fields.MLC_DESC.toString()).
				add(Projections.property("MLC."+ScoModalidadeLicitacao.Fields.CODIGO.toString()), ScoUltimasComprasMaterialVO.Fields.MLC_CODIGO.toString()).
				add(Projections.property("LCT."+ScoLicitacao.Fields.DT_ABERTURA_PROPOSTA.toString()), ScoUltimasComprasMaterialVO.Fields.DT_ABERTURA.toString()).
				add(Projections.property("LCT."+ScoLicitacao.Fields.INCISO_ARTIGO_LICITACAO.toString()), ScoUltimasComprasMaterialVO.Fields.INCISO_ARTIGO_LICITACAO.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(ScoUltimasComprasMaterialVO.class));
		return (ScoUltimasComprasMaterialVO)executeCriteriaUniqueResult(criteria);
	}
	public ScoLicitacao buscaLicitacoesPorNumero(Integer numeroPac) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoLicitacao.class);
		criteria.createAlias(ScoLicitacao.Fields.MODALIDADE_LICITACAO.toString(), "MLC", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(ScoLicitacao.Fields.NUMERO.toString(), numeroPac));
		return (ScoLicitacao) executeCriteriaUniqueResult(criteria);
	}	
	public DadosGestorVO obterDadosGestor(Integer numeroPac) {
		ConsultarAndamentoProcessoCompraQueryProvider builder = new ConsultarAndamentoProcessoCompraQueryProvider();
		SQLQuery query = createSQLQuery(builder.buildQueryObterDadosGestor(numeroPac));
		return  (DadosGestorVO) 	query.addScalar("nomeGestor",StringType.INSTANCE).
									addScalar("matriculaGestor",IntegerType.INSTANCE).
									addScalar("mlcCodigo",StringType.INSTANCE).
									setResultTransformer(Transformers.aliasToBean(DadosGestorVO.class)).uniqueResult();
	}
	//#5550 C1
	private DetachedCriteria criarPesquisaProcessoAdmCompra(ScoLicitacaoVO scoLicitacaoVOFiltro, Date dataInicio, Date dataFim, Date dataInicioGerArqRem, Date dataFimGerArqRem, Boolean pacPendenteEnvio, Boolean pacPendenteRetorno){
		PesquisarLicitacoesQueryBuilder builder = new PesquisarLicitacoesQueryBuilder();
		builder.build(scoLicitacaoVOFiltro, dataInicio, dataFim, dataInicioGerArqRem, dataFimGerArqRem, pacPendenteEnvio, pacPendenteRetorno);
		return builder.getResult();
	}
	//#5550 C1
	public List<ScoLicitacaoVO> pesquisarLicitacoesVO(Integer firstResult, Integer maxResults, String orderProperty, boolean asc,
			ScoLicitacaoVO scoLicitacaoVOFiltro, Date dataInicio, Date dataFim, Date dataInicioGerArqRem, Date dataFimGerArqRem, Boolean pacPendenteEnvio, Boolean pacPendenteRetorno){
		DetachedCriteria criteria = criarPesquisaProcessoAdmCompra(scoLicitacaoVOFiltro, dataInicio, dataFim, dataInicioGerArqRem, dataFimGerArqRem, pacPendenteEnvio, pacPendenteRetorno);
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("RSE."+RapServidores.Fields.VIN_CODIGO.toString()), ScoLicitacaoVO.Fields.VINCULO_GESTOR.toString())
				.add(Projections.property("RSE."+RapServidores.Fields.MATRICULA.toString()), ScoLicitacaoVO.Fields.MATRICULA_GESTOR.toString())
				.add(Projections.property("RPF."+RapPessoasFisicas.Fields.NOME.toString()), ScoLicitacaoVO.Fields.NOME_GESTOR.toString())
				.add(Projections.property("LCT."+ScoLicitacao.Fields.NUMERO.toString()), ScoLicitacaoVO.Fields.NUMERO_PAC.toString())
				.add(Projections.property("LCT."+ScoLicitacao.Fields.MLC_CODIGO.toString()), ScoLicitacaoVO.Fields.MODALIDADE_LICITACAO.toString())
				.add(Projections.property("LCT."+ScoLicitacao.Fields.DESCRICAO.toString()), ScoLicitacaoVO.Fields.DESCRICAO_PAC.toString())
				.add(Projections.property("LCT."+ScoLicitacao.Fields.IND_SITUACAO.toString()), ScoLicitacaoVO.Fields.SITUACAO.toString())
				.add(Projections.property("LCT."+ScoLicitacao.Fields.DT_DIGITACAO.toString()), ScoLicitacaoVO.Fields.DT_DIGITACAO.toString())
				.add(Projections.property("LCT."+ScoLicitacao.Fields.DT_GERACAO_ARQ_REMESSA.toString()), ScoLicitacaoVO.Fields.DT_GERACAO_ARQ_REM.toString())
				.add(Projections.property("LCT."+ScoLicitacao.Fields.NOME_ARQ_REMESSA.toString()), ScoLicitacaoVO.Fields.NOME_ARQ_REMESSA.toString())
				.add(Projections.property("LCT."+ScoLicitacao.Fields.NOME_ARQ_RETORNO.toString()), ScoLicitacaoVO.Fields.NOME_ARQ_RETORNO.toString()));
		criteria.addOrder(Order.desc("LCT."+ScoLicitacao.Fields.NUMERO.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(ScoLicitacaoVO.class));
		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}
	//#5550
	public Long pesquisarLicitacoesVOCount(ScoLicitacaoVO scoLicitacaoVOFiltro, Date dataInicio, Date dataFim, Date dataInicioGerArqRem, Date dataFimGerArqRem, Boolean pacPendenteEnvio, Boolean pacPendenteRetorno){
		DetachedCriteria criteria = criarPesquisaProcessoAdmCompra(scoLicitacaoVOFiltro, dataInicio, dataFim, dataInicioGerArqRem, dataFimGerArqRem, pacPendenteEnvio, pacPendenteRetorno);
		return executeCriteriaCount(criteria);
	}
	//#5550 C2
	public List<ScoLicitacao> pesquisarLicitacoesPorNumero(String parametro){ 
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoLicitacao.class);
		if(parametro != null && !parametro.isEmpty()){
			if(CoreUtil.isNumeroInteger(parametro)) {	
			 criteria.add(Restrictions.eq(ScoLicitacao.Fields.NUMERO.toString(), Integer.valueOf((String)parametro))); }
		}
		criteria.add(Restrictions.eq(ScoLicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		criteria.addOrder(Order.desc(ScoLicitacao.Fields.NUMERO.toString()));
		return executeCriteria(criteria, 0, 100, null, false);
	}
	//#5550 C4
	public List<ScoLicitacao> pesquisarLicitacoesPorNomeArquivoRetorno(String nome){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoLicitacao.class); 
		if (StringUtils.isNotBlank(nome) && nome != null) {	criteria.add(Restrictions.ilike(ScoLicitacao.Fields.NOME_ARQ_RETORNO.toString(), nome, MatchMode.EXACT)); }
		criteria.addOrder(Order.desc(ScoLicitacao.Fields.NUMERO.toString()));
		return executeCriteria(criteria);
	}
	public Long pesquisarLicitacoesPorNumeroCount(String parametro) {
		return Long.valueOf(pesquisarLicitacoesPorNumero(parametro).size());
	}
	/**
	 * Busca licitações - #5481 Consulta C3
	 * 
	 * @param nrosPac Números de licitações que desejam ser retornadas. 
	 * @return uma lista de licitações de acordo com a lista de numeros
	 */
	public List<ScoLicitacao> obterListaLicitacoesPorNumeros(List<Integer> nrosPac) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoLicitacao.class);
		criteria.add(Restrictions.in(ScoLicitacao.Fields.NUMERO.toString(), nrosPac));
		return executeCriteria(criteria);
	}
	
	public ScoLicitacao obterNomeArqRetorno(Integer nrosPac) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoLicitacao.class);
		criteria.add(Restrictions.eq(ScoLicitacao.Fields.NUMERO.toString(), nrosPac));
		return (ScoLicitacao) executeCriteriaUniqueResult(criteria);
	}
	
	public Long pesquisarLicitacoesPorNumeroDescricaoCount(String parametro) {
		DetachedCriteria criteria = criteriaLicitacao(parametro);
		return this.executeCriteriaCount(criteria);
	}
	
	public ScoLicitacao obterLicitacaoModalidadePorModalidadeEditalAno(ScoModalidadeLicitacao modalidade, Integer edital, Integer ano) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoLicitacao.class, "LCT");
		criteria.createAlias("LCT." + ScoLicitacao.Fields.SERVIDOR_DIGITADO.toString(), "SDG");
		criteria.createAlias("SDG." + RapServidores.Fields.PESSOA_FISICA.toString(), "SPF");
		criteria.createAlias("LCT." + ScoLicitacao.Fields.MODALIDADE_LICITACAO.toString(), "MLC");
		criteria.createAlias("LCT." + ScoLicitacao.Fields.ITENS_LICITACAO.toString(), "IT_LIC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("LCT." + ScoLicitacao.Fields.SERVIDOR_GESTOR.toString(), "SERV", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SERV." + RapServidores.Fields.RAMAL_TELEFONICO.toString(), "RAM", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(ScoLicitacao.Fields.MODALIDADE_LICITACAO.toString(), modalidade));
		criteria.add(Restrictions.eq(ScoLicitacao.Fields.NUM_EDITAL.toString(), edital));
		criteria.add(Restrictions.eq(ScoLicitacao.Fields.ANO_COMPLEMENTO.toString(), ano));
		
		List<ScoLicitacao> listScoLicitacao = executeCriteria(criteria, 0, 1, null, false);
		
		if (listScoLicitacao != null && listScoLicitacao.size() > 0) {
			return listScoLicitacao.get(0);
		} else {
			return null;
		}
	}
}