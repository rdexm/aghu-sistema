package br.gov.mec.aghu.compras.dao;

import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.contaspagar.vo.FiltroSolicitacaoTituloVO;
import br.gov.mec.aghu.compras.contaspagar.vo.SolicitacaoTituloVO;
import br.gov.mec.aghu.compras.pac.vo.PreItemPacVO;
import br.gov.mec.aghu.compras.vo.FiltroConsSCSSVO;
import br.gov.mec.aghu.compras.vo.ItensSCSSVO;
import br.gov.mec.aghu.compras.vo.LoteSolicitacaoServicoVO;
import br.gov.mec.aghu.compras.vo.SolServicoVO;
import br.gov.mec.aghu.compras.vo.SolicitacaoServicoVO;
import br.gov.mec.aghu.dominio.DominioOrigemSolicitacaoSuprimento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;
import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.dominio.DominioTipoPontoParada;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.FcpTituloSolicitacoes;
import br.gov.mec.aghu.model.FsoGrupoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesaId;
import br.gov.mec.aghu.model.FsoVerbaGestao;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoArquivoAnexo;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoGrupoServico;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.model.ScoPontoServidor;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.ScoSolicitacaoCompraServico;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * @modulo compras
 */
@SuppressWarnings({"PMD.CyclomaticComplexity","PMD.ExcessiveClassLength"})
public class ScoSolicitacaoServicoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoSolicitacaoServico> {
	
	private static final Log LOG = LogFactory.getLog(ScoSolicitacaoServicoDAO.class);
	
    @Inject
    private IParametroFacade aIParametroFacade;
    @Inject
    private ICentroCustoFacade aICentroCustoFacade;
    @Inject
    private ScoFaseSolicitacaoDAO aScoFaseSolicitacaoDAO;
    @Inject
    private ScoGrupoServicoDAO aScoGrupoServicoDAO;
    @Inject
    private ScoPontoParadaSolicitacaoDAO aScoPontoParadaSolicitacaoDAO;
    @Inject
    private ScoCaracteristicaUsuarioCentroCustoDAO scoCaracteristicaUsuarioCentroCustoDAO;
    
	private static final long serialVersionUID = 7978548177040794487L;

	public Boolean verificarSSNaturezaDespEVerbaGestaoNulo(Integer numero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoSolicitacaoServico.class);
		criteria.add(Restrictions.eq(ScoSolicitacaoServico.Fields.NUMERO.toString(), numero));
		criteria.add(Restrictions.or(Restrictions.isNull(ScoSolicitacaoServico.Fields.NATUREZA_DESPESA.toString()), 
				Restrictions.isNull(ScoSolicitacaoServico.Fields.VERBA_GESTAO.toString())));
		
		return executeCriteriaCount(criteria) > 0;
	}
	public ScoSolicitacaoServico obterDescricaoSolicitacaoServicoPeloId(Integer numero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoSolicitacaoServico.class);
		criteria.add(Restrictions.eq(ScoSolicitacaoServico.Fields.NUMERO.toString(), numero));
		return (ScoSolicitacaoServico)executeCriteriaUniqueResult(criteria);
	}
	
	public ScoSolicitacaoServico obterScoSolicitacaoServicoPeloId(Integer numero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoSolicitacaoServico.class);
		
		criteria.createAlias(ScoSolicitacaoServico.Fields.CONVENIO_FINANCEIRO.toString(), "conv_fin", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoSolicitacaoServico.Fields.CENTRO_CUSTO.toString(), "centro_custo", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoSolicitacaoServico.Fields.CC_APLICADA.toString(), "centro_custo_aplicacao", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoSolicitacaoServico.Fields.CENTRO_CUSTO_AUT_TEC.toString(), "centro_custo_aut_tec", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoSolicitacaoServico.Fields.NATUREZA_DESPESA.toString(), "nat_desp", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("nat_desp." + FsoNaturezaDespesa.Fields.GRUPO_NATUREZA.toString(), "nat_desp_grp", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoSolicitacaoServico.Fields.PONTO_PARADA.toString(), "pp", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoSolicitacaoServico.Fields.PONTO_PARADA_ATUAL.toString(), "ppa", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoSolicitacaoServico.Fields.MODALIDADE_LICITACAO.toString(), "mod_lic", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoSolicitacaoServico.Fields.SERVICO.toString(), "serv", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoSolicitacaoServico.Fields.SERVIDOR_EXCLUIDOR.toString(), "ser", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoSolicitacaoServico.Fields.VERBA_GESTAO.toString(), "Verba", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ser."+RapServidores.Fields.PESSOA_FISICA.toString(), "pes", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoSolicitacaoServico.Fields.SERVIDOR.toString(), "servidor", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoSolicitacaoServico.Fields.SERVIDOR_COMPRADOR.toString(), "servidor_comprador", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoSolicitacaoServico.Fields.SERVIDOR_AUTORIZADOR.toString(), "servidor_aut", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(ScoSolicitacaoServico.Fields.NUMERO.toString(), numero));
		
		return (ScoSolicitacaoServico)executeCriteriaUniqueResult(criteria);
		
		
	}

	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public List<SolicitacaoServicoVO> obterRelatorioSolicitacaoServico(List<Integer> listaCodSS){
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoSolicitacaoServico.class, "SSL");
		criteria.createAlias(ScoSolicitacaoServico.Fields.CENTRO_CUSTO.toString(), "centro_custo", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoSolicitacaoServico.Fields.CC_APLICADA.toString(), "centro_custo_aplicacao", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoSolicitacaoServico.Fields.SERVICO.toString(), "serv", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoSolicitacaoServico.Fields.VERBA_GESTAO.toString(), "Verba", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoSolicitacaoServico.Fields.SERVIDOR.toString(), "servidor", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("servidor."+RapServidores.Fields.PESSOA_FISICA.toString(), "pes", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoSolicitacaoServico.Fields.SERVIDOR_AUTORIZADOR.toString(), "servidor_aut", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("servidor_aut."+RapServidores.Fields.PESSOA_FISICA.toString(), "pes_aut", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.in(ScoSolicitacaoServico.Fields.NUMERO.toString(), listaCodSS));		
		criteria.addOrder(Order.asc(ScoSolicitacaoServico.Fields.NUMERO.toString()));		
		List<ScoSolicitacaoServico> listaSolicitacoesServico = this.executeCriteria(criteria);
		List<SolicitacaoServicoVO> listaSolicitacaoServicoVO = new ArrayList<SolicitacaoServicoVO>();		
		for (ScoSolicitacaoServico solicitacaoServico : listaSolicitacoesServico){			 		 
			 final DetachedCriteria criteriaArquivosAnexos = DetachedCriteria.forClass(ScoArquivoAnexo.class, "SSARQ");
			 criteriaArquivosAnexos.add(Restrictions.eq(ScoArquivoAnexo.Fields.TPORIGEM.toString(), DominioOrigemSolicitacaoSuprimento.SS));
			 criteriaArquivosAnexos.add(Restrictions.eq(ScoArquivoAnexo.Fields.NUMERO.toString(), BigInteger.valueOf(solicitacaoServico.getNumero())));
			 Long existeAnexo = this.executeCriteriaCount(criteriaArquivosAnexos);			 
			 String mensagemAnexo = (existeAnexo.intValue() == 0 ? "" : "Anexos: Esta solicitação possui arquivos em anexo.");			 
			 SolicitacaoServicoVO solicitacaoServicoVO = new SolicitacaoServicoVO();	
			 solicitacaoServicoVO.setSolicitacaoServico(solicitacaoServico);
			 solicitacaoServicoVO.setMensagemAnexos(mensagemAnexo);			 
		     listaSolicitacaoServicoVO.add(solicitacaoServicoVO);
		}		
		return listaSolicitacaoServicoVO;		
	}
	public Long pesquisarSolicitacaoServicoPorPpsCodigoCount(Short codigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoSolicitacaoServico.class);
		criteria.add(Restrictions.eq(ScoSolicitacaoServico.Fields.PPS_CODIGO.toString(), codigo));
		return this.executeCriteriaCount(criteria);
	}
	public Long pesquisarSolicitacaoServicoPorPpsCodigoLocAtualCount(Short codigo) {

		DetachedCriteria criteria = DetachedCriteria.forClass(ScoSolicitacaoServico.class);
		criteria.add(Restrictions.eq(ScoSolicitacaoServico.Fields.PPS_CODIGO_LOC_ATUAL.toString(), codigo));
		return this.executeCriteriaCount(criteria);
	}
	public List<ScoSolicitacaoServico> pesquisarSolicitacaoServicoPorNumeroOuDescricao(String filtro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoSolicitacaoServico.class);
		SimpleExpression restrictionCodigo = null,
						 restrictionDescricao = null;
		restrictionDescricao = Restrictions.like(ScoSolicitacaoServico.Fields
				.DESCRICAO.toString(), filtro, MatchMode.ANYWHERE).ignoreCase();
		if(Pattern.matches("\\d+", filtro))
		{
			restrictionCodigo = Restrictions.eq(ScoSolicitacaoServico.Fields
					.NUMERO.toString(), Integer.valueOf(filtro));
			criteria.add(Restrictions.or(restrictionCodigo, restrictionDescricao));
		}
		else{
			criteria.add(restrictionDescricao);
		}
		criteria.add(Restrictions.eq(ScoSolicitacaoServico.Fields.IND_EXCLUSAO.toString(), false));
		criteria.add(Restrictions.eq(ScoSolicitacaoServico.Fields.IND_EFETIVADA.toString(), false));
		DetachedCriteria subQuery = DetachedCriteria.forClass(ScoSolicitacaoCompraServico.class);
		subQuery.setProjection(Projections.projectionList()
				.add(Projections.property(ScoSolicitacaoCompraServico.Fields.SLS_NUMERO.toString())));		
		criteria.add(Subqueries.propertyNotIn(ScoSolicitacaoServico.Fields.NUMERO.toString(), subQuery));		
		criteria.addOrder(Order.asc(ScoSolicitacaoServico.Fields
				.NUMERO.toString()));
		return executeCriteria(criteria, 0, 100, null, true);
	}
	
	public List<ScoSolicitacaoCompraServico> pesquisarSolServicosPorSolCompra(ScoSolicitacaoDeCompra solCompra){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoSolicitacaoCompraServico.class);
		criteria.createAlias(ScoSolicitacaoCompraServico.Fields.SCO_SOLICITACAO_SERVICO.toString(), "SOL", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SOL."+ScoSolicitacaoServico.Fields.SERVICO.toString(), "SER", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(ScoSolicitacaoCompraServico.Fields.SCO_SOLICITACAO_DE_COMPRA.toString(), solCompra));
		return executeCriteria(criteria);			
	}
		
	public void excluirScoSolCompraServico(ScoSolicitacaoDeCompra solCompra) throws ApplicationBusinessException{
		String hql = "delete from "+ScoSolicitacaoCompraServico.class.getSimpleName() +" where SLC_NUMERO= :solCompraNumero";
		Query query = createHibernateQuery(hql);
		query.setParameter("solCompraNumero", solCompra.getNumero());
		query.executeUpdate();
	}
	
	/**
	 * Retorna quantidade de solicitações de serviço conforme regras da tela de autorização
	 * @param filtroPesquisa
	 * @param servidorLogado
	 * @return Integer
	 */
	public Long countSolicitacaoServicoAutorizarSs(LoteSolicitacaoServicoVO filtroPesquisa, RapServidores servidorLogado) {
		return executeCriteriaCount(this.obterCriteriaLoteSolicitacaoServico(filtroPesquisa, servidorLogado, true, false, false, false, true, true, true));
	}
	
	public Long countSolicitacaoServicoEncaminharSs(LoteSolicitacaoServicoVO filtroPesquisa, RapServidores servidorLogado) {
		return executeCriteriaCount(this.obterCriteriaLoteSolicitacaoServico(filtroPesquisa, servidorLogado, false, false, true, false, true, true, false));
	}

	/**
	 * Retorna um DetachedCriteria com filtros para diversas telas da solicitação de serviço
	 * @param filtroPesquisa - VO para transporte dos filtros de pesquisa
	 * @param servidorLogado indica o servidor que está usando a tela
	 * @param verificaCentroCustoAutorizacao indica se deve checar o CC e a característica para autorização
	 * @param filtraTipoPontoParada filtra determinados tipos de ponto de parada, utilizado na liberação
	 * @param verificaCentroCustoGeracao indica se deve checar o CC e a característica para geração de ss
	 * @param incluiGppg indica se deve incluir o centro de custo do FIPE e o PPS do GPPG
	 * @param verificaFases indica se deve verificar as fases
	 * @param verificaPontoServidor indica se deve verificar as permissoes de ponto de parada do servidor logado
	 * @param verificaGrupoEngenharia indica se deve verificar o grupo de engenharia
	 * @return DetachedCriteria
	 */
	@SuppressWarnings({"PMD.NPathComplexity", "PMD.ExcessiveMethodLength"})
	private DetachedCriteria obterCriteriaLoteSolicitacaoServico(LoteSolicitacaoServicoVO filtroPesquisa, RapServidores servidorLogado, 
			Boolean verificaCentroCustoAutorizacao, Boolean filtraTipoPontoParada,
			Boolean verificaCentroCustoGeracao, Boolean incluiGppg, Boolean verificaFases, Boolean verificaPontoServidor,
			Boolean verificaGrupoEngenharia) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoSolicitacaoServico.class, "SS");
		
		criteria.createAlias("SS."+ScoSolicitacaoServico.Fields.SERVICO.toString(),	"SRV");		
		criteria.createAlias("SRV."+ScoServico.Fields.SERVIDOR.toString(),	"SED");		
		criteria.createAlias("SS."+ScoSolicitacaoServico.Fields.CONVENIO_FINANCEIRO.toString(),	"CVF", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SS."+ScoSolicitacaoServico.Fields.MODALIDADE_LICITACAO.toString(), "MLC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SS."+ScoSolicitacaoServico.Fields.NATUREZA_DESPESA.toString(), "NTD", JoinType.LEFT_OUTER_JOIN);
//		criteria.createAlias("NTD."+FsoNaturezaDespesa.Fields.GRUPO_NATUREZA.toString(), "GND");
//		criteria.createAlias("NTD."+FsoNaturezaDespesa.Fields.RELACIONA_NATUREZAS.toString(), "RLN", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SS."+ScoSolicitacaoServico.Fields.VERBA_GESTAO.toString(), "VBG", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias("SS."+ScoSolicitacaoServico.Fields.PONTO_PARADA.toString(), "PTP");
		criteria.createAlias("SS."+ScoSolicitacaoServico.Fields.PONTO_PARADA_ATUAL.toString(), "PPA");
		criteria.createAlias("SS."+ScoSolicitacaoServico.Fields.CENTRO_CUSTO.toString(), "CCT");
		criteria.createAlias("SS."+ScoSolicitacaoServico.Fields.CC_APLICADA.toString(), "CCA");
		criteria.createAlias("SS."+ScoSolicitacaoServico.Fields.SERVIDOR.toString(), "SVD");
		criteria.createAlias("SVD."+RapServidores.Fields.PESSOA_FISICA.toString(), "PSF");
		
		FccCentroCustos ccFipe = this.obterCcFipe();
		Boolean permiteAutorizarGppg = this.getScoCaracteristicaUsuarioCentroCustoDAO().verificarAutorizacaoGppg(servidorLogado);
		if (filtroPesquisa.getNumeroSolicitacaoServico() != null) {
			criteria.add(Restrictions.eq("SS."+ScoSolicitacaoServico.Fields.NUMERO.toString(), filtroPesquisa.getNumeroSolicitacaoServico()));
		} else {
			if (filtroPesquisa.isExibirScSolicitante()) {
				criteria.add(Restrictions.ne("SS."+ScoSolicitacaoServico.Fields.PONTO_PARADA.toString(), this.getScoPontoParadaSolicitacaoDAO().obterPontoParadaPorTipo(DominioTipoPontoParada.CH)));
				criteria.add(Restrictions.isNull("SS."+ScoSolicitacaoServico.Fields.DT_AUTORIZACAO.toString()));
				criteria.add(Restrictions.isNull("SS."+ScoSolicitacaoServico.Fields.SER_MATRICULA_AUTORIZADA.toString()));
			} else {
				if (filtroPesquisa.getSolicitacaoAutorizada() == null || !filtroPesquisa.getSolicitacaoAutorizada().isSim()) {
					if (filtroPesquisa.getPontoParada() != null) {
						if (!incluiGppg) {	
							criteria.add(Restrictions.eq("SS."+ScoSolicitacaoServico.Fields.PONTO_PARADA.toString(), filtroPesquisa.getPontoParada()));	
						} else {
							if (permiteAutorizarGppg) {
								criteria.add(Restrictions.or(
										Restrictions.and(Restrictions.eq("SS."+ ScoSolicitacaoServico.Fields.PONTO_PARADA.toString(),
														this.getScoPontoParadaSolicitacaoDAO().obterPontoParadaPorTipo(DominioTipoPontoParada.GP)),
														Restrictions.eq("SS."+ScoSolicitacaoServico.Fields.CC_APLICADA.toString(), ccFipe)), 
										Restrictions.eq("SS."+ScoSolicitacaoServico.Fields.PONTO_PARADA.toString(), filtroPesquisa.getPontoParada())));						
							} else {
								criteria.add(Restrictions.eq("SS."+ScoSolicitacaoServico.Fields.PONTO_PARADA.toString(), filtroPesquisa.getPontoParada()));
							}
						}
					} else {
						if (incluiGppg) {
							if (permiteAutorizarGppg) {
								Restrictions.and(
										Restrictions.eq("SS."+ ScoSolicitacaoServico.Fields.PONTO_PARADA.toString(), this.getScoPontoParadaSolicitacaoDAO().obterPontoParadaPorTipo(DominioTipoPontoParada.GP)),
										Restrictions.eq("SS."+ScoSolicitacaoServico.Fields.CC_APLICADA.toString(), ccFipe));
							}
						}				
					}
				}
			}
		}
		if (filtroPesquisa.getDataSolicitacaoServico() != null) {
			SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy");
			final String sqlRestrictionToChar = "TO_CHAR(this_.DT_SOLICITACAO,'dd/MM/yyyy') = ?";
			criteria.add(Restrictions.sqlRestriction(sqlRestrictionToChar,
					formatador.format(filtroPesquisa.getDataSolicitacaoServico()),StringType.INSTANCE));
		}	
		if (filtroPesquisa.getNumeroSolicitacaoServico() == null && filtroPesquisa.getSolicitacaoAutorizada() != null && filtroPesquisa.getSolicitacaoAutorizada().isSim()) {
			criteria.add(Restrictions.isNotNull("SS."+ScoSolicitacaoServico.Fields.DT_AUTORIZACAO.toString()));
			criteria.add(Restrictions.isNotNull("SS."+ScoSolicitacaoServico.Fields.SER_MATRICULA_AUTORIZADA.toString()));
		}
		if (filtroPesquisa.getDataInicioAutorizacaoSolicitacaoServico() != null) {
			criteria.add(Restrictions.ge("SS."+ScoSolicitacaoServico.Fields.DT_AUTORIZACAO.toString(), filtroPesquisa.getDataInicioAutorizacaoSolicitacaoServico()));
		}
		if (filtroPesquisa.getDataFimAutorizacaoSolicitacaoServico() != null) {
			SimpleDateFormat formatadorTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			SimpleDateFormat formatadorDia = new SimpleDateFormat("dd/MM/yyyy");
			try {
				criteria.add(Restrictions.le("SS."+ScoSolicitacaoServico.Fields.DT_AUTORIZACAO.toString(), 
						formatadorTime.parse(formatadorDia.format(filtroPesquisa.getDataFimAutorizacaoSolicitacaoServico())+" 23:59:59")));
			} catch (ParseException e) {
				LOG.error("Erro ao executar chamada"+ e.getMessage());
			}
		}
		if (filtroPesquisa.getCentroCustoSolicitante() != null) {
			criteria.add(Restrictions.eq("SS."+ScoSolicitacaoServico.Fields.CENTRO_CUSTO.toString(), filtroPesquisa.getCentroCustoSolicitante()));
		}
		if (filtroPesquisa.getCentroCustoAplicacao() != null) {
			criteria.add(Restrictions.eq("SS."+ScoSolicitacaoServico.Fields.CC_APLICADA.toString(), filtroPesquisa.getCentroCustoAplicacao()));
		}
		if (filtroPesquisa.getServico() != null) {
			criteria.add(Restrictions.eq("SRV."+ScoServico.Fields.CODIGO.toString(), filtroPesquisa.getServico().getCodigo()));
		}
		if (filtroPesquisa.getSolicitacaoPrioritaria() != null) {
			criteria.add(Restrictions.eq("SS."+ScoSolicitacaoServico.Fields.IND_PRIORIDADE.toString(),	(filtroPesquisa.getSolicitacaoPrioritaria() == DominioSimNao.S) ? Boolean.TRUE : Boolean.FALSE));
		}
		if (filtroPesquisa.getSolicitacaoUrgente() != null) {
			criteria.add(Restrictions.eq("SS."+ScoSolicitacaoServico.Fields.IND_URGENTE.toString(),	(filtroPesquisa.getSolicitacaoUrgente() == DominioSimNao.S ? true : false)));
		}
		if (filtroPesquisa.getNumeroSolicitacaoServico() == null) {
			criteria.add(Restrictions.eq("SS."	+ ScoSolicitacaoServico.Fields.IND_EXCLUSAO.toString(), false));
		}
		
		// Default da consulta é buscar apenas solicitações que não foram devolvidas
		// Telas que necessitam de solitações devolvidas e não devolvidas devem setar um valor neste campo do filtro
		if (filtroPesquisa.getDevolvidasENaoDevolvidas() == null){
			criteria.add(Restrictions.eq("SS."	+ ScoSolicitacaoServico.Fields.IND_DEVOLUCAO.toString(), false));
		}
		if (filtroPesquisa.getGrupoServico() != null) {
			criteria.add(Restrictions.eq("SRV."+ ScoServico.Fields.GRUPO_SERVICO.toString(),filtroPesquisa.getGrupoServico()));
		}				
		if (verificaCentroCustoAutorizacao) {
			if (filtroPesquisa.getNumeroSolicitacaoServico() == null && 
					(filtroPesquisa.getSolicitacaoAutorizada() == null || !filtroPesquisa.getSolicitacaoAutorizada().isSim())) {
					criteria.add(Restrictions.isNull("SS."+ScoSolicitacaoServico.Fields.DT_AUTORIZACAO.toString()));
					criteria.add(Restrictions.isNull("SS."+ScoSolicitacaoServico.Fields.SER_MATRICULA_AUTORIZADA.toString()));
			}
			List<FccCentroCustos> listaCCRetorno = getCentroCustoFacade().pesquisarCentroCustoUsuarioAutorizacaoSs();
			if (listaCCRetorno.size() > 0) {
				if (!incluiGppg) { 
					criteria.add(Restrictions.in("SS."+ScoSolicitacaoServico.Fields.CENTRO_CUSTO.toString(), listaCCRetorno));	
				} else {
					if (permiteAutorizarGppg) {
						criteria.add(Restrictions.or(
								Restrictions.in("SS."+ScoSolicitacaoServico.Fields.CENTRO_CUSTO.toString(), listaCCRetorno), 
								Restrictions.and(Restrictions.eq("SS."+ScoSolicitacaoServico.Fields.CC_APLICADA.toString(), ccFipe),
										Restrictions.eq("SS."+ ScoSolicitacaoServico.Fields.PONTO_PARADA_ATUAL.toString(),
												this.getScoPontoParadaSolicitacaoDAO().obterPontoParadaPorTipo(DominioTipoPontoParada.GP)))));
					} else {
						criteria.add(Restrictions.in("SS."+ScoSolicitacaoServico.Fields.CENTRO_CUSTO.toString(), listaCCRetorno));
					}
				}
			} else {
				if (!incluiGppg) {
					// se não existir nenhum centro de custo que o servidor possui caracteristica de autorizacao
					// deve fazer uma condicao para nao retornar nada na query
					criteria.add(Restrictions.eq("SS."+ScoSolicitacaoServico.Fields.NUMERO.toString(), -1));
				} else {
					if (permiteAutorizarGppg) {
						criteria.add(Restrictions.and(Restrictions.eq("SS."+ScoSolicitacaoServico.Fields.CC_APLICADA.toString(), ccFipe),
								Restrictions.eq("SS."+ ScoSolicitacaoServico.Fields.PONTO_PARADA_ATUAL.toString(),
										this.getScoPontoParadaSolicitacaoDAO().obterPontoParadaPorTipo(DominioTipoPontoParada.GP))));
					} else {
						criteria.add(Restrictions.eq("SS."+ScoSolicitacaoServico.Fields.NUMERO.toString(), -1));
					}
				}
			}
		}		
		if (verificaCentroCustoGeracao) {
			List<FccCentroCustos> listaCCRetorno = getCentroCustoFacade().pesquisarCentroCustoUsuarioGerarSs();
			if (listaCCRetorno.size() > 0) {
				criteria.add(Restrictions.in("SS."+ScoSolicitacaoServico.Fields.CENTRO_CUSTO.toString(), listaCCRetorno));	
			} else {
				// se não existir nenhum centro de custo que o servidor possui caracteristica de geração
				// deve fazer uma condicao para nao retornar nada na query
				criteria.add(Restrictions.eq("SS."+ScoSolicitacaoServico.Fields.NUMERO.toString(), -1));
			}
		}		
		// pega os pontos de parada que o servidor tem direito
		DetachedCriteria subQueryPontoServidor = DetachedCriteria.forClass(ScoPontoServidor.class);
		ProjectionList projectionListSubQueryPontoServidor = Projections.projectionList().add(Projections.property(ScoPontoServidor.Fields.PONTO_PARADA_SOLICITACAO.toString()));
		subQueryPontoServidor.setProjection(projectionListSubQueryPontoServidor);
		subQueryPontoServidor.add(Restrictions.eq(ScoPontoServidor.Fields.SERVIDOR.toString(), servidorLogado));
		if (filtraTipoPontoParada) {
			subQueryPontoServidor.createAlias(ScoPontoServidor.Fields.PONTO_PARADA_SOLICITACAO.toString(), "PPS", JoinType.INNER_JOIN);
			String filtraTipos = "";
			try {
				filtraTipos = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_PPS_FILTRO_LIBERACAO).getVlrTexto();
			} catch (ApplicationBusinessException e) {
				LOG.error(e.getMessage());
			}
			String[] arrayStringTipos = StringUtils.split(filtraTipos, ',');			
			List<DominioTipoPontoParada> listDominioTipos = new ArrayList<DominioTipoPontoParada>();						
			for (String item : arrayStringTipos) {				
				listDominioTipos.add((DominioTipoPontoParada.valueOf(item)));
			}
			subQueryPontoServidor.add(Restrictions.or(
							Restrictions.and(Restrictions.isNotNull("PPS." + ScoPontoParadaSolicitacao.Fields.TIPO.toString()), 
									Restrictions.not(
											Restrictions.in("PPS." + ScoPontoParadaSolicitacao.Fields.TIPO.toString(), listDominioTipos))), 
							Restrictions.isNull("PPS." + ScoPontoParadaSolicitacao.Fields.TIPO.toString())));
		}
		if (verificaFases) {
			DetachedCriteria subQueryFasesSolicitacoes = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "SLS");
			ProjectionList projectionListSubQueryFasesSolicitacoes = Projections.projectionList().add(Projections.property("SLS."+ScoFaseSolicitacao.Fields.SLS_NUMERO.toString()));
			subQueryFasesSolicitacoes.setProjection(projectionListSubQueryFasesSolicitacoes);
			subQueryFasesSolicitacoes.add(Restrictions.eqProperty("SLS."+ScoFaseSolicitacao.Fields.SLS_NUMERO.toString(),"SS."+ScoSolicitacaoServico.Fields.NUMERO.toString()));
			subQueryFasesSolicitacoes.add(Restrictions.eq("SLS."+ScoFaseSolicitacao.Fields.TIPO.toString(), DominioTipoFaseSolicitacao.S));
			subQueryFasesSolicitacoes.add(Restrictions.eq("SLS."+ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
			subQueryFasesSolicitacoes.add(Restrictions.isNull("SLS."+ScoFaseSolicitacao.Fields.LCT_NUMERO.toString()));					
			criteria.add(Subqueries.propertyNotIn("SS."+ScoSolicitacaoServico.Fields.NUMERO.toString(), subQueryFasesSolicitacoes));
		}
		if (verificaPontoServidor) {
			criteria.add(Subqueries.propertyIn("SS." + ScoSolicitacaoServico.Fields.PPS_CODIGO.toString(), subQueryPontoServidor));
		}
		if (verificaGrupoEngenharia) {
			Integer paramGrupoServicoEngenharia = null;
			try {
				paramGrupoServicoEngenharia = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_GRPO_SERV_ENG).getVlrNumerico().intValue();
			} catch (ApplicationBusinessException e) {
				LOG.error(e.getMessage());
			}
			if (paramGrupoServicoEngenharia != null) {
				ScoGrupoServico grupoServicoEngenharia = null;
				grupoServicoEngenharia = this.getScoGrupoServicoDAO().obterPorChavePrimaria(paramGrupoServicoEngenharia);	
				if (grupoServicoEngenharia != null) {
					DetachedCriteria subQueryGrupoEngenharia = DetachedCriteria.forClass(ScoServico.class, "SRV2");
					ProjectionList projectionListSubQueryGrupoEngenharia = Projections.projectionList()
					.add(Projections.property("SRV2."+ScoServico.Fields.CODIGO.toString()));
					subQueryGrupoEngenharia.setProjection(projectionListSubQueryGrupoEngenharia);
					subQueryGrupoEngenharia.add(Restrictions.eq("SRV2."+ScoServico.Fields.GRUPO_SERVICO.toString(), grupoServicoEngenharia));
					criteria.add(Subqueries.propertyNotIn("SRV."+ScoServico.Fields.CODIGO.toString(), subQueryGrupoEngenharia));
				}
			}
		}		
		return criteria;
	}
	
	/**
	 * Retorna lista paginada de solicitações de serviço conforme regras da tela de autorização 
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param filtroPesquisa
	 * @param servidorLogado
	 * @return List
	 */
	public List<ScoSolicitacaoServico> pesquisarSolicitacaoServicoAutorizarSs(Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, LoteSolicitacaoServicoVO filtroPesquisa, RapServidores servidorLogado) {
		return this.executeCriteria((this.obterCriteriaLoteSolicitacaoServico(filtroPesquisa, servidorLogado, true, false, false, false, true, true, true)), 
				firstResult, maxResults, orderProperty,  asc);
	}

	/**
	 * Retorna lista paginada de solicitações de serviço conforme regras da tela de encaminhamento
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param filtroPesquisa
	 * @param servidorLogado
	 * @return List
	 */
	public List<ScoSolicitacaoServico> pesquisarSolicitacaoServicoEncaminharSs(Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, LoteSolicitacaoServicoVO filtroPesquisa, RapServidores servidorLogado) {
		return this.executeCriteria((this.obterCriteriaLoteSolicitacaoServico(filtroPesquisa, servidorLogado, false, false, true, false, true, true, false)),firstResult, maxResults, orderProperty,  asc);
	}
	
	public List<SolServicoVO> listarSolicitacoesDeServicos(Integer firstResult, Integer maxResult, String order, Boolean asc, 
			List<FccCentroCustos> listaCentroCustos, FccCentroCustos centroCusto, 
			FccCentroCustos centroCustoAplicada, ScoServico servico, Date dtSolInicial, Date dtSolFinal, 
			DominioSimNao pendente, ScoPontoParadaSolicitacao ppsSolicitante, ScoPontoParadaSolicitacao ppsAutorizacao, ScoSolicitacaoServico solicitacaoServico, RapServidores servidorLogado,	ScoPontoParadaSolicitacao pontoParada) {
		DetachedCriteria criteria = this.obterCriteriaSolicitacoesServico(listaCentroCustos, centroCusto, centroCustoAplicada, servico, 
											dtSolInicial, dtSolFinal, pendente, ppsSolicitante, ppsAutorizacao, solicitacaoServico, servidorLogado, pontoParada);
		criteria.setResultTransformer(Transformers.aliasToBean(SolServicoVO.class));
		criteria.addOrder(Order.desc(ScoSolicitacaoServico.Fields.IND_URGENTE.toString()));
		criteria.addOrder(Order.desc(ScoSolicitacaoServico.Fields.DT_DIGITACAO.toString()));
		criteria.addOrder(Order.asc(ScoSolicitacaoServico.Fields.NUMERO.toString()));
		return this.executeCriteria(criteria, firstResult, maxResult, order,  asc);
	}

	public Long listarSolicitacoesDeServicosCount(List<FccCentroCustos> listaCentroCustos, FccCentroCustos centroCusto, 
			FccCentroCustos centroCustoAplicada, ScoServico servico, Date dtSolInicial , 
			Date dtSolFinal, DominioSimNao pendente , ScoPontoParadaSolicitacao ppsSolicitante, ScoPontoParadaSolicitacao ppsAutorizacao, 
			ScoSolicitacaoServico solicitacaoServico, RapServidores servidorLogado, ScoPontoParadaSolicitacao pontoParada) {
		DetachedCriteria criteria = this.obterCriteriaSolicitacoesServico(listaCentroCustos, centroCusto, centroCustoAplicada, servico, 
											dtSolInicial, dtSolFinal, pendente, ppsSolicitante, ppsAutorizacao,  solicitacaoServico, servidorLogado, pontoParada);
		return this.executeCriteriaCount(criteria);
	}
	
	@SuppressWarnings({"PMD.ExcessiveMethodLength","PMD.NPathComplexity"})
	private DetachedCriteria obterCriteriaSolicitacoesServico(List<FccCentroCustos> listaCentroCustos, FccCentroCustos centroCusto, FccCentroCustos centroCustoAplicada, 
						ScoServico servico, Date dtSolInicial , Date dtSolFinal, DominioSimNao pendente , 
						ScoPontoParadaSolicitacao ppsSolicitante, ScoPontoParadaSolicitacao ppsAutorizacao, 
						ScoSolicitacaoServico solicitacaoServico, RapServidores servidorLogado, ScoPontoParadaSolicitacao pontoParada) {
		FccCentroCustos ccFipe = this.getScoCaracteristicaUsuarioCentroCustoDAO().obterCcAplicacaoGeracaoGppg(servidorLogado);
		Boolean possuiPermissaoGerarGppg = false;
		Set<Integer> listaHierarquicaGppg = null;	
		if (ccFipe != null) {
			listaHierarquicaGppg = getCentroCustoFacade().pesquisarCentroCustoComHierarquia(ccFipe.getCodigo());
			possuiPermissaoGerarGppg = (listaHierarquicaGppg != null && !listaHierarquicaGppg.isEmpty());
		}
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoSolicitacaoServico.class,"SCO");
		criteria.setProjection(getProjectionListSc(criteria));
		SimpleDateFormat formatadorTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		SimpleDateFormat formatadorDia = new SimpleDateFormat("dd/MM/yyyy");
		
		if (solicitacaoServico!=null && solicitacaoServico.getNumero()!=null){
				criteria.add(Restrictions.eq(ScoSolicitacaoServico.Fields.NUMERO.toString(), solicitacaoServico.getNumero()));
				if (centroCusto==null){
					criteria.add(Restrictions.in(ScoSolicitacaoServico.Fields.CENTRO_CUSTO.toString(), listaCentroCustos));	
				} 
			return criteria;
		}
		
		if(servico != null) {
			criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoServico.Fields.SERVICO.toString(), servico));
		}
		if(centroCusto != null) {
			criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoServico.Fields.CENTRO_CUSTO.toString(), centroCusto));
		}
		else {
			if (!possuiPermissaoGerarGppg || ccFipe == null) {
				criteria.add(Restrictions.in("SCO."+ScoSolicitacaoServico.Fields.CENTRO_CUSTO.toString(), listaCentroCustos));	
			} else {
				if (centroCustoAplicada == null) {
					criteria.add(Restrictions.or(Restrictions.in("SCO."+ScoSolicitacaoServico.Fields.CENTRO_CUSTO.toString(), listaCentroCustos), 
											Restrictions.in("SCO."+ScoSolicitacaoServico.Fields.CCT_CODIGO_APLICADA.toString(), listaHierarquicaGppg)));
				}
			}
		}
		if(centroCustoAplicada != null) {
			criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoServico.Fields.CC_APLICADA.toString(), centroCustoAplicada));
		}
		if (dtSolInicial!=null){
			if (dtSolFinal!=null){
				criteria.add(Restrictions.ge("SCO."+ScoSolicitacaoServico.Fields.DT_SOLICITACAO.toString(), dtSolInicial));
				try {
					criteria.add(Restrictions.le("SCO."+ScoSolicitacaoServico.Fields.DT_SOLICITACAO.toString(), formatadorTime.parse(formatadorDia.format(dtSolFinal)+" 23:59:59")));
				} catch (ParseException e) {
					LOG.error("Erro ao executar chamada"+ e.getMessage());
				}
			} else {
				criteria.add(Restrictions.ge("SCO."+ScoSolicitacaoServico.Fields.DT_SOLICITACAO.toString(), dtSolInicial));
			}
		}else if (dtSolFinal!=null){
				try {
					criteria.add(Restrictions.le("SCO."+ScoSolicitacaoServico.Fields.DT_SOLICITACAO.toString(), formatadorTime.parse(formatadorDia.format(dtSolFinal)+" 23:59:59")));
				} catch (ParseException e) {
					LOG.error("Erro ao executar chamada"+ e.getMessage());
				}
		}		
		if (pontoParada == null) {
			if (pendente ==DominioSimNao.S){
				criteria.add(Restrictions.disjunction()
						.add(Restrictions.eq("SCO."+ScoSolicitacaoServico.Fields.PPS_CODIGO.toString(), ppsSolicitante.getCodigo().shortValue()))
						.add(Restrictions.eq("SCO."+ScoSolicitacaoServico.Fields.PPS_CODIGO.toString(), ppsAutorizacao.getCodigo().shortValue())));
			}		
		} else {
			criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoServico.Fields.PPS_CODIGO.toString(), pontoParada.getCodigo()));
		}
		if (solicitacaoServico!=null){
			if (solicitacaoServico.getNumero()!=null){
				criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoServico.Fields.NUMERO.toString(), solicitacaoServico.getNumero()));
			}
			if (solicitacaoServico.getDtAutorizacao()!=null){
				try {
					criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoServico.Fields.DT_AUTORIZACAO.toString(),formatadorDia.parse(formatadorDia.format(solicitacaoServico.getDtAutorizacao()))));
				} catch (ParseException e) {
					LOG.error("Erro ao executar chamada"+ e.getMessage());
				}
			}
			if (solicitacaoServico.getIndExclusao()!=null){
				criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoServico.Fields.IND_EXCLUSAO.toString(), solicitacaoServico.getIndExclusao()));
			}
			if (solicitacaoServico.getIndDevolucao()!=null){
				criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoServico.Fields.IND_DEVOLUCAO.toString(), solicitacaoServico.getIndDevolucao()));
			}
			if (solicitacaoServico.getIndPrioridade()!=null){
				criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoServico.Fields.IND_PRIORIDADE.toString(), solicitacaoServico.getIndPrioridade()));
			}
			if (solicitacaoServico.getIndUrgente()!=null){
				criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoServico.Fields.IND_URGENTE.toString(), solicitacaoServico.getIndUrgente()));
			}
			if (solicitacaoServico.getIndExclusivo()!=null){
				criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoServico.Fields.IND_EXCLUSIVO.toString(), solicitacaoServico.getIndExclusivo()));
			}
			if (solicitacaoServico.getIndEfetivada()!=null){
				criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoServico.Fields.IND_EFETIVADA.toString(), solicitacaoServico.getIndEfetivada()));
			}
		}		
		return criteria;
	}
	
	public FccCentroCustos obterCcFipe() {
		FccCentroCustos ccFipe = null;
		try {
			ccFipe = this.getCentroCustoFacade().obterCentroCustoPorChavePrimaria(this.getParametroFacade().
							buscarAghParametro(AghuParametrosEnum.P_CCUSTO_PROJ_FIPE).getVlrNumerico().intValue());
		} catch (ApplicationBusinessException e) {
			LOG.error(e.getMessage());
		}
		return ccFipe;
	}
	
	public List<SolServicoVO> listarSolicitacoesDeServicosSemParametros(Integer firstResult, Integer maxResult, String order, Boolean asc,
			List<FccCentroCustos> listaCentroCustos, ScoPontoParadaSolicitacao ppsSolicitante, 
			ScoPontoParadaSolicitacao ppsAutorizacao, List<ScoServico> pGrpoServEng, RapServidores servidorLogado){
		
		DetachedCriteria criteria = this.obterCriterioSemParamtro(listaCentroCustos, ppsSolicitante, ppsAutorizacao , pGrpoServEng, servidorLogado);
		criteria.setResultTransformer(Transformers.aliasToBean(SolServicoVO.class));
		criteria.addOrder(Order.desc("SCO."+ScoSolicitacaoServico.Fields.IND_URGENTE.toString()));
		criteria.addOrder(Order.asc("SCO."+ScoSolicitacaoServico.Fields.DT_SOLICITACAO.toString()));
		criteria.addOrder(Order.asc("SCO."+ScoSolicitacaoServico.Fields.NUMERO.toString()));		
		return this.executeCriteria(criteria, firstResult, maxResult, order,  asc);	
	}
	
	public Long listarSolicitacoesDeServicosCountSemParametros(List<FccCentroCustos> listaCentroCustos, 
			ScoPontoParadaSolicitacao ppsSolicitante, ScoPontoParadaSolicitacao ppsAutorizacao , 
				List<ScoServico> pGrpoServEng, RapServidores servidorLogado){
		return this.executeCriteriaCount(this.obterCriterioSemParamtro(listaCentroCustos, ppsSolicitante, ppsAutorizacao, pGrpoServEng, servidorLogado));
	}
	
	public DetachedCriteria obterCriterioSemParamtro(List<FccCentroCustos> listaCentroCustos, ScoPontoParadaSolicitacao ppsSolicitante, 
						ScoPontoParadaSolicitacao ppsAutorizacao , List<ScoServico> pGrpoServEng, RapServidores servidorLogado){
			
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoSolicitacaoServico.class,"SCO");
		criteria.setProjection(getProjectionListSc(criteria));
		criteria.add(Restrictions.disjunction()
				.add(Restrictions.eq("SCO."+ScoSolicitacaoServico.Fields.PPS_CODIGO.toString(), ppsSolicitante.getCodigo().shortValue()))
				.add(Restrictions.eq("SCO."+ScoSolicitacaoServico.Fields.PPS_CODIGO.toString(), ppsAutorizacao.getCodigo().shortValue())));		
		criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoServico.Fields.IND_EXCLUSAO.toString(), false));		
		
		FccCentroCustos ccFipe = this.getScoCaracteristicaUsuarioCentroCustoDAO().obterCcAplicacaoGeracaoGppg(servidorLogado);
		Boolean possuiPermissaoGerarGppg = false;
		Set<Integer> listaHierarquicaGppg = null;
		
		if (ccFipe != null) {
			listaHierarquicaGppg = getCentroCustoFacade().pesquisarCentroCustoComHierarquia(ccFipe.getCodigo());
			possuiPermissaoGerarGppg = (listaHierarquicaGppg != null && !listaHierarquicaGppg.isEmpty());
		}
		if (!possuiPermissaoGerarGppg || ccFipe == null) {
			criteria.add(Restrictions.in("SCO."+ScoSolicitacaoServico.Fields.CENTRO_CUSTO.toString(), listaCentroCustos));
		} else {
			criteria.add(Restrictions.or(
							Restrictions.in("SCO."+ScoSolicitacaoServico.Fields.CENTRO_CUSTO.toString(), listaCentroCustos), 
							Restrictions.in("SCO."+ScoSolicitacaoServico.Fields.CCT_CODIGO_APLICADA.toString(), listaHierarquicaGppg)));
		}
		criteria.add(Restrictions.not(Restrictions.in("SCO."+ScoSolicitacaoServico.Fields.SERVICO.toString(), pGrpoServEng)));
		return criteria;
	}
	
	private ProjectionList getProjectionListSc(DetachedCriteria criteria) {
		ProjectionList p = Projections.projectionList();
		p.add(Projections.property("SCO." + ScoSolicitacaoServico.Fields.NUMERO.toString()), SolServicoVO.Fields.NUMERO.toString());
		p.add(Projections.property("PPA." + ScoPontoParadaSolicitacao.Fields.CODIGO.toString()), SolServicoVO.Fields.PONTO_PARADA_ATUAL.toString());
		p.add(Projections.property("SCO." + ScoSolicitacaoServico.Fields.PPS_CODIGO.toString()), SolServicoVO.Fields.PONTO_PARADA.toString());
		p.add(Projections.property("SCO." + ScoSolicitacaoServico.Fields.IND_URGENTE.toString()), SolServicoVO.Fields.URGENTE.toString());
		p.add(Projections.property("SCO." + ScoSolicitacaoServico.Fields.IND_PRIORIDADE.toString()), SolServicoVO.Fields.PRIORIDADE.toString());
		p.add(Projections.property("SCO." + ScoSolicitacaoServico.Fields.IND_EXCLUSAO.toString()), SolServicoVO.Fields.EXCLUSAO.toString());
		p.add(Projections.property("SCO." + ScoSolicitacaoServico.Fields.IND_DEVOLUCAO.toString()), SolServicoVO.Fields.DEVOLUCAO.toString());
		p.add(Projections.property("SCO." + ScoSolicitacaoServico.Fields.DT_SOLICITACAO.toString()), SolServicoVO.Fields.DT_SOLICITACAO.toString());
		p.add(Projections.property("SCO." + ScoSolicitacaoServico.Fields.DESCRICAO.toString()), SolServicoVO.Fields.DESCRICAO_SS.toString());
		p.add(Projections.property("SRV." + ScoServico.Fields.CODIGO.toString()), SolServicoVO.Fields.SRV_CODIGO.toString());
		p.add(Projections.property("SRV." + ScoServico.Fields.NOME.toString()), SolServicoVO.Fields.NOME_SERVICO.toString());
		p.add(Projections.property("SRV." + ScoServico.Fields.DESCRICAO.toString()), SolServicoVO.Fields.DESCRICAO_SERVICO.toString());
		p.add(Projections.property("SCO." + ScoSolicitacaoServico.Fields.QUANTIDADE_SOLICITADA.toString()), SolServicoVO.Fields.QTD_SOLICITADA.toString());
		p.add(Projections.property("PPA." + ScoPontoParadaSolicitacao.Fields.DESCRICAO.toString()), SolServicoVO.Fields.DESCRICAO_PONTO_PARADA.toString());
		p.add(Projections.property("CC." + FccCentroCustos.Fields.CODIGO.toString()), SolServicoVO.Fields.CODIGO_CC.toString());
		p.add(Projections.property("CC." + FccCentroCustos.Fields.DESCRICAO.toString()), SolServicoVO.Fields.DESCRICAO_CC.toString());
		p.add(Projections.property("CCA." + FccCentroCustos.Fields.CODIGO.toString()), SolServicoVO.Fields.CODIGO_CC_APLICACAO.toString());
		p.add(Projections.property("CCA." + FccCentroCustos.Fields.DESCRICAO.toString()), SolServicoVO.Fields.DESCRICAO_CC_APLICACAO.toString());
		p.add(Projections.property("SCP." + RapServidores.Fields.VIN_CODIGO.toString()), SolServicoVO.Fields.VINCULO_COMPRADOR.toString());
		p.add(Projections.property("SCP." + RapServidores.Fields.MATRICULA.toString()), SolServicoVO.Fields.MATRICULA_COMPRADOR.toString());
		p.add(Projections.property("PES." + RapPessoasFisicas.Fields.NOME.toString()), SolServicoVO.Fields.NOME_COMPRADOR.toString());
		p.add(Projections.property("PES." + RapPessoasFisicas.Fields.NOME_USUAL.toString()), SolServicoVO.Fields.NOME_USUAL_COMPRADOR.toString());
		p.add(Projections.sqlProjection("RAP5_.RAM_NRO_RAMAL as "+SolServicoVO.Fields.RAMAL.toString(), new String[]{SolServicoVO.Fields.RAMAL.toString()}, new Type[]{StringType.INSTANCE}));
		p.add(Projections.sqlProjection("( SELECT count(*) FROM AGH.SCO_ARQUIVOS_ANEXOS AN WHERE AN.NUMERO = {alias}.NUMERO AND AN.TP_ORIGEM = 'SS') as "+SolServicoVO.Fields.QTDE_ANEXO.toString(), new String[]{SolServicoVO.Fields.QTDE_ANEXO.toString()}, new Type[]{IntegerType.INSTANCE}));  
		p.add(Projections.sqlProjection("( SELECT COUNT(numero) FROM agh.sco_ss_jn jn WHERE jn.numero = {alias}.NUMERO AND pps_codigo = 2) as "+SolServicoVO.Fields.DEVOLVIDA.toString(), new String[]{SolServicoVO.Fields.DEVOLVIDA.toString()}, new Type[]{IntegerType.INSTANCE}));
		p.add(Projections.property("FIS." + RapPessoasFisicas.Fields.NOME.toString()), SolServicoVO.Fields.NOME_SOLICITANTE.toString());
		criteria.createAlias("SCO."+ScoSolicitacaoServico.Fields.PONTO_PARADA.toString(), "PPA");
		criteria.createAlias("SCO."+ScoSolicitacaoServico.Fields.SERVICO.toString(), "SRV");
		criteria.createAlias("SCO."+ScoSolicitacaoServico.Fields.CENTRO_CUSTO.toString(), "CC");
		criteria.createAlias("SCO."+ScoSolicitacaoServico.Fields.CC_APLICADA.toString(), "CCA");				
		criteria.createAlias("SCO."+ScoSolicitacaoServico.Fields.SERVIDOR.toString(), "RAP");
		criteria.createAlias("RAP."+RapServidores.Fields.PESSOA_FISICA.toString(), "FIS");
		criteria.createAlias("SCO."+ScoSolicitacaoServico.Fields.SERVIDOR_COMPRADOR.toString(), "SCP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SCP."+RapServidores.Fields.PESSOA_FISICA.toString(), "PES", JoinType.LEFT_OUTER_JOIN);
		return p;
	}
	
	public List<SolServicoVO> listarSolicitacoesDeServicosCompradoreEngenharia(Integer firstResult, Integer maxResult, String order, Boolean asc, 
			List<FccCentroCustos> listaCentroCustos, FccCentroCustos centroCusto, FccCentroCustos centroCustoAplicada, ScoServico servico, Date dtSolInicial, Date dtSolFinal, 
			ScoPontoParadaSolicitacao ppsSolicitante, ScoPontoParadaSolicitacao ppsAutorizacao, 
			ScoSolicitacaoServico solicitacaoServico, ScoPontoParadaSolicitacao pontoParadaAtual, RapServidores servidorCompra,
			ScoModalidadeLicitacao modalidadeLicitacao) {
		//PERFIL COMPRADOR
		DetachedCriteria criteria = this.obterCriteriaSolicitacoesServicoCompradoreEngenharia(listaCentroCustos, centroCusto, centroCustoAplicada, servico, 
											dtSolInicial, dtSolFinal, ppsSolicitante, ppsAutorizacao, solicitacaoServico,
											pontoParadaAtual, servidorCompra, modalidadeLicitacao );
		criteria.setResultTransformer(Transformers.aliasToBean(SolServicoVO.class));
		criteria.addOrder(Order.desc("SCO."+ScoSolicitacaoServico.Fields.IND_URGENTE.toString()));
		criteria.addOrder(Order.desc("SCO."+ScoSolicitacaoServico.Fields.DT_SOLICITACAO.toString()));
		criteria.addOrder(Order.asc("SCO."+ScoSolicitacaoServico.Fields.NUMERO.toString()));

		return this.executeCriteria(criteria, firstResult, maxResult, order,  asc);
	}

	public Long listarSolicitacoesDeServicosCompradoreEngenhariaCount(List<FccCentroCustos> listaCentroCustos, FccCentroCustos centroCusto, 
			FccCentroCustos centroCustoAplicada, ScoServico servico, Date dtSolInicial , 
			Date dtSolFinal, ScoPontoParadaSolicitacao ppsSolicitante, ScoPontoParadaSolicitacao ppsAutorizacao, 
			ScoSolicitacaoServico solicitacaoServico, ScoPontoParadaSolicitacao pontoParadaAtual, RapServidores servidorCompra,
			ScoModalidadeLicitacao modalidadeLicitacao) {
		//PERFIL COMPRADOR
		DetachedCriteria criteria = this.obterCriteriaSolicitacoesServicoCompradoreEngenharia(listaCentroCustos, centroCusto, centroCustoAplicada, servico, 
											dtSolInicial, dtSolFinal, ppsSolicitante, ppsAutorizacao,  solicitacaoServico,
											pontoParadaAtual, servidorCompra ,modalidadeLicitacao);
		return this.executeCriteriaCount(criteria);
	}
	
	@SuppressWarnings({"PMD.ExcessiveMethodLength","PMD.NPathComplexity"})
	private DetachedCriteria obterCriteriaSolicitacoesServicoCompradoreEngenharia(List<FccCentroCustos> listaCentroCustos, FccCentroCustos centroCusto, 
			FccCentroCustos centroCustoAplicada, ScoServico servico, 
			Date dtSolInicial, Date dtSolFinal, ScoPontoParadaSolicitacao ppsSolicitante, 
			ScoPontoParadaSolicitacao ppsAutorizacao, ScoSolicitacaoServico solicitacaoServico, 
			ScoPontoParadaSolicitacao pontoParada, RapServidores servidorCompra,
			ScoModalidadeLicitacao modalidadeLicitacao) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoSolicitacaoServico.class, "SCO");
		criteria.setProjection(getProjectionListSc(criteria));
		
		if (listaCentroCustos!=null){
			// perfil engenharia
			criteria.createAlias("SRV."+ScoServico.Fields.GRUPO_SERVICO.toString(), "GSV", JoinType.INNER_JOIN);
			criteria.add(Restrictions.eq("GSV."+ScoGrupoServico.Fields.IND_ENGENHARIA.toString(), Boolean.TRUE));
		}
		SimpleDateFormat formatadorTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		SimpleDateFormat formatadorDia = new SimpleDateFormat("dd/MM/yyyy");										
		if(servico != null) {
			criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoServico.Fields.SERVICO.toString(), servico));
		}
		if(centroCusto != null) {
			criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoServico.Fields.CENTRO_CUSTO.toString(), centroCusto));
		}else if (listaCentroCustos!=null) {
			// perfil engenharia
			criteria.add(Restrictions.in(ScoSolicitacaoServico.Fields.CENTRO_CUSTO.toString(), listaCentroCustos));
		}
		if(centroCustoAplicada != null) {
			criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoServico.Fields.CC_APLICADA.toString(), centroCustoAplicada));
		}
		if (dtSolInicial!=null){
			if (dtSolFinal!=null){
				criteria.add(Restrictions.ge("SCO."+ScoSolicitacaoServico.Fields.DT_SOLICITACAO.toString(), dtSolInicial));
				try {
					criteria.add(Restrictions.le("SCO."+ScoSolicitacaoServico.Fields.DT_SOLICITACAO.toString(), formatadorTime.parse(formatadorDia.format(dtSolFinal)+" 23:59:59")));
				} catch (ParseException e) {
					LOG.error("Erro ao executar chamada"+ e.getMessage());
				}
			} else {
				criteria.add(Restrictions.ge("SCO."+ScoSolicitacaoServico.Fields.DT_SOLICITACAO.toString(), dtSolInicial));
			}
		}else if (dtSolFinal!=null){
				try {
					criteria.add(Restrictions.le("SCO."+ScoSolicitacaoServico.Fields.DT_SOLICITACAO.toString(), formatadorTime.parse(formatadorDia.format(dtSolFinal)+" 23:59:59")));
				} catch (ParseException e) {
					LOG.error("Erro ao executar chamada"+ e.getMessage());
				}
		}
		if (solicitacaoServico!=null){
			if (solicitacaoServico.getNumero()!=null){
				criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoServico.Fields.NUMERO.toString(), solicitacaoServico.getNumero()));
			}
			
			if (solicitacaoServico.getDtAutorizacao()!=null){
				try {
					criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoServico.Fields.DT_AUTORIZACAO.toString(),formatadorDia.parse(formatadorDia.format(solicitacaoServico.getDtAutorizacao()))));
				} catch (ParseException e) {
					LOG.error("Erro ao executar chamada"+ e.getMessage());
				}
			}
			if (solicitacaoServico.getIndExclusao()!=null){
				criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoServico.Fields.IND_EXCLUSAO.toString(), solicitacaoServico.getIndExclusao()));
			}
			if (solicitacaoServico.getIndDevolucao()!=null){
				criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoServico.Fields.IND_DEVOLUCAO.toString(), solicitacaoServico.getIndDevolucao()));
			}
			if (solicitacaoServico.getIndPrioridade()!=null){
				criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoServico.Fields.IND_PRIORIDADE.toString(), solicitacaoServico.getIndPrioridade()));
			}
			if (solicitacaoServico.getIndUrgente()!=null){
				criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoServico.Fields.IND_URGENTE.toString(), solicitacaoServico.getIndUrgente()));
			}
			if (solicitacaoServico.getIndExclusivo()!=null){
				criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoServico.Fields.IND_EXCLUSIVO.toString(), solicitacaoServico.getIndExclusivo()));
			}
			if (solicitacaoServico.getIndEfetivada()!=null){
				criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoServico.Fields.IND_EFETIVADA.toString(), solicitacaoServico.getIndEfetivada()));
			}
		}		
		if (modalidadeLicitacao != null) {
			criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoServico.Fields.MOD_LICITACAO.toString(), modalidadeLicitacao.getCodigo()));
		}		
		if (servidorCompra != null) {
			criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoServico.Fields.SERVIDOR_COMPRADOR.toString(), servidorCompra));
		}		
		if (pontoParada!= null) {
			criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoServico.Fields.PPS_CODIGO.toString(), pontoParada.getCodigo()));
		}
		else {
			if (listaCentroCustos==null) {
				// perfil comprador
			criteria.add(Restrictions.in("SCO."+ScoSolicitacaoServico.Fields.PONTO_PARADA.toString(), this.getScoPontoParadaSolicitacaoDAO().listarPontoParadaPorTipo(DominioTipoPontoParada.CP)));
			DetachedCriteria subQueryFasesSolicitacoes = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "SLC");
			ProjectionList projectionListSubQueryFasesSolicitacoes = Projections.projectionList()
			.add(Projections.property("SLC."+ScoFaseSolicitacao.Fields.SLS_NUMERO.toString()));
			subQueryFasesSolicitacoes.setProjection(projectionListSubQueryFasesSolicitacoes);
			subQueryFasesSolicitacoes.add(Restrictions.eqProperty("SLC."+ScoFaseSolicitacao.Fields.SLS_NUMERO.toString(), "SCO."+ScoSolicitacaoServico.Fields.NUMERO.toString()));
			subQueryFasesSolicitacoes.add(Restrictions.eq("SLC."+ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));					
			criteria.add(Subqueries.propertyNotIn("SCO."+ScoSolicitacaoServico.Fields.NUMERO.toString(), subQueryFasesSolicitacoes));
		 }
			else {
				 // perfil engenharia
				 criteria.add(Restrictions.disjunction()
					.add(Restrictions.eq(ScoSolicitacaoServico.Fields.PPS_CODIGO.toString(), ppsSolicitante.getCodigo()))
					.add(Restrictions.eq(ScoSolicitacaoServico.Fields.PPS_CODIGO.toString(), ppsAutorizacao.getCodigo())));		
				}
		}			
		return criteria;
	}
	
	public Long listarSSItensSCSSVOCount(FiltroConsSCSSVO filtroConsultaSS) {
		return executeCriteriaCount(this.obterCriteriaConsultaItensSCSSVO(filtroConsultaSS));			
	}
	
	public List<ItensSCSSVO> listarSSItensSCSSVO(Integer firstResult, Integer maxResults, String orderProperty,	boolean asc, FiltroConsSCSSVO filtroConsultaSS) {
		return executeCriteria(this.obterCriteriaConsultaItensSCSSVO(filtroConsultaSS), firstResult, maxResults, orderProperty, asc);			
	}	
	
	public List<ScoSolicitacaoServico> listarSSItensSCSSVO(FiltroConsSCSSVO filtroConsultaSS) throws IllegalAccessException,NoSuchMethodException, InvocationTargetException{
	  	DetachedCriteria criteria = this.obterCriteriaConsultaItensSCSSVO(filtroConsultaSS);		
		criteria.addOrder(Order.asc(ScoSolicitacaoServico.Fields.NUMERO.toString()));		
		return executeCriteria(criteria);			
	}
	
	private void obterCriteriaConsultaItensSCSSVOFlags(FiltroConsSCSSVO filtroConsultaSS, DetachedCriteria criteria)  {		
		if (filtroConsultaSS.getIndEfetivada() != null) {
			criteria.add(Restrictions.eq("SS." + ScoSolicitacaoServico.Fields.IND_EFETIVADA.toString(), filtroConsultaSS.getIndEfetivada()));
		}
		if (filtroConsultaSS.getIndExclusao() != null) {
			criteria.add(Restrictions.eq("SS." + ScoSolicitacaoServico.Fields.IND_EXCLUSAO.toString(),filtroConsultaSS.getIndExclusao()));
		}
		if (filtroConsultaSS.getIndDevolucao() != null) {
			criteria.add(Restrictions.eq("SS." + ScoSolicitacaoServico.Fields.IND_DEVOLUCAO.toString(),filtroConsultaSS.getIndDevolucao()));
		}
		if (filtroConsultaSS.getIndUrgente() != null) {
			criteria.add(Restrictions.eq("SS." + ScoSolicitacaoServico.Fields.IND_URGENTE.toString(),filtroConsultaSS.getIndUrgente()));
		}
		if (filtroConsultaSS.getIndPrioridade() != null) {
			criteria.add(Restrictions.eq("SS." + ScoSolicitacaoServico.Fields.IND_PRIORIDADE.toString(),filtroConsultaSS.getIndPrioridade()));
		}
		if (filtroConsultaSS.getIndExclusivo() != null) {
			criteria.add(Restrictions.eq("SS." + ScoSolicitacaoServico.Fields.IND_EXCLUSIVO.toString(),filtroConsultaSS.getIndExclusivo()));
		}
	}
	
	private void obterCriteriaConsultaItensSCSSVODatas(FiltroConsSCSSVO filtroConsultaSS, DetachedCriteria criteria) {		
		if (filtroConsultaSS.getDataInicioSolicitacao() != null) {
			criteria.add(Restrictions.ge("SS." + ScoSolicitacaoServico.Fields.DT_SOLICITACAO.toString(), filtroConsultaSS.getDataInicioSolicitacao()));
		}
	 	if (filtroConsultaSS.getDataFimSolicitacao() != null) {
			criteria.add(Restrictions.le("SS." + ScoSolicitacaoServico.Fields.DT_SOLICITACAO.toString(), filtroConsultaSS.getDataFimSolicitacao()));
		}
		if(filtroConsultaSS.getDataInicioAutorizacao() !=null){
			criteria.add(Restrictions.ge("SS." + ScoSolicitacaoServico.Fields.DT_AUTORIZACAO.toString(),filtroConsultaSS.getDataInicioAutorizacao()));			
		}
		if(filtroConsultaSS.getDataFimAutorizacao() !=null){
			criteria.add(Restrictions.le("SS." + ScoSolicitacaoServico.Fields.DT_AUTORIZACAO.toString(),filtroConsultaSS.getDataFimAutorizacao()));			
		}
		if(filtroConsultaSS.getDataInicioAnalise() !=null){
			criteria.add(Restrictions.ge("SS." + ScoSolicitacaoServico.Fields.DT_ANALISE.toString(),filtroConsultaSS.getDataInicioAnalise()));			
		}
		if(filtroConsultaSS.getDataFimAnalise() !=null){
			criteria.add(Restrictions.le("SS." + ScoSolicitacaoServico.Fields.DT_ANALISE.toString(),filtroConsultaSS.getDataFimAnalise()));			
		}
	}
	
    
	private void obterCriteriaConsultaItensSCSSVOPontoParada(FiltroConsSCSSVO filtroConsultaSS, DetachedCriteria criteria) {
		if (filtroConsultaSS.getPontoParadaAtual() != null){
			criteria.add(Restrictions.eq("PP_ATU." + ScoPontoParadaSolicitacao.Fields.CODIGO.toString(), filtroConsultaSS.getPontoParadaAtual().getCodigo()));
		}
		if (filtroConsultaSS.getPontoParadaAnterior() != null){
			criteria.add(Restrictions.eq("PP_ANT." + ScoPontoParadaSolicitacao.Fields.CODIGO.toString(), filtroConsultaSS.getPontoParadaAnterior().getCodigo()));
		}		
    }
	
	private void obterCriteriaConsultaItensSCSSVOSS(FiltroConsSCSSVO filtroConsultaSS, DetachedCriteria criteria) {	
		if (filtroConsultaSS.getNumero() != null && filtroConsultaSS.getNumeroFinal() != null) {
			criteria.add(Restrictions.ge("SS." + ScoSolicitacaoServico.Fields.NUMERO.toString(), filtroConsultaSS.getNumero()));
			criteria.add(Restrictions.le("SS." + ScoSolicitacaoServico.Fields.NUMERO.toString(), filtroConsultaSS.getNumeroFinal()));
		} else if (filtroConsultaSS.getNumero() != null && filtroConsultaSS.getNumeroFinal() == null) {
			criteria.add(Restrictions.eq("SS." + ScoSolicitacaoServico.Fields.NUMERO.toString(), filtroConsultaSS.getNumero()));			
		} else if (filtroConsultaSS.getNumero() == null && filtroConsultaSS.getNumeroFinal() != null) {
			criteria.add(Restrictions.eq("SS." + ScoSolicitacaoServico.Fields.NUMERO.toString(), filtroConsultaSS.getNumeroFinal()));
		}
		
		if (filtroConsultaSS.getCentroCustoSolicitante() != null) {
			criteria.add(Restrictions.eq("CC_REQ." + FccCentroCustos.Fields.CODIGO.toString(),	filtroConsultaSS.getCentroCustoSolicitante().getCodigo()));
		}
		if (filtroConsultaSS.getCentroCustoAplicacao() != null) {
			criteria.add(Restrictions.eq("CC_APP."	+ FccCentroCustos.Fields.CODIGO.toString(), filtroConsultaSS.getCentroCustoAplicacao().getCodigo()));
		}		
		if (StringUtils.isNotBlank(filtroConsultaSS.getDescricao())) {
			criteria.add(Restrictions.ilike("SS." + ScoSolicitacaoServico.Fields.DESCRICAO.toString(), filtroConsultaSS.getDescricao(), MatchMode.ANYWHERE));
		}		
		if (filtroConsultaSS.getNroInvestimento() != null) {
			criteria.add(Restrictions.eq("SS." + ScoSolicitacaoServico.Fields.NRO_INVESTIMENTO.toString(),filtroConsultaSS.getNroInvestimento()));
		}
		if (filtroConsultaSS.getNaturezaDespesa() != null) {
			criteria.add(Restrictions.eq("SS." + ScoSolicitacaoServico.Fields.NATUREZA_DESPESA.toString(),filtroConsultaSS.getNaturezaDespesa()));
		}
		if (filtroConsultaSS.getVerbaGestao() != null) {
			criteria.add(Restrictions.eq("SS." + ScoSolicitacaoServico.Fields.VERBA_GESTAO.toString(),filtroConsultaSS.getVerbaGestao()));
		}
		if (filtroConsultaSS.getNroProjeto() != null) {
			criteria.add(Restrictions.eq("SS." + ScoSolicitacaoServico.Fields.NRO_PROJETO.toString(),filtroConsultaSS.getNroProjeto()));
		}
	 }  

	private void obterCriteriaConsultaItensSCSSVOPACAF(FiltroConsSCSSVO filtroConsultaSS, DetachedCriteria criteria) {
		if (filtroConsultaSS != null && filtroConsultaSS.getPesquisarPorPAC() != null && filtroConsultaSS.getPesquisarPorPAC()){
			DetachedCriteria subQueryPAC = this.getScoFaseSolicitacaoDAO().obterCriteriaFaseSolicitacaoLicitacao(filtroConsultaSS);
			subQueryPAC.add(Property.forName("SS." + ScoSolicitacaoServico.Fields.NUMERO.toString()).eqProperty("FASES." + ScoFaseSolicitacao.Fields.SLS_NUMERO.toString()));
			criteria.add(Subqueries.exists(subQueryPAC));			
		}		
		if (filtroConsultaSS != null && filtroConsultaSS.getPesquisarPorAF() != null && filtroConsultaSS.getPesquisarPorAF()){
			DetachedCriteria subQueryAF = this.getScoFaseSolicitacaoDAO().obterCriteriaFaseSolicitacaoAF(filtroConsultaSS);
			subQueryAF.add(Property.forName("SS." + ScoSolicitacaoServico.Fields.NUMERO.toString()).eqProperty("FASES." + ScoFaseSolicitacao.Fields.SLS_NUMERO.toString()));
			criteria.add(Subqueries.exists(subQueryAF));			
		}
	}
	
	private void obterCriteriaConsultaServico(FiltroConsSCSSVO filtroConsultaSS, DetachedCriteria criteria) {
		if (filtroConsultaSS.getServico() != null) {
			criteria.add(Restrictions.eq("SRV." + ScoServico.Fields.CODIGO.toString(),	filtroConsultaSS.getServico().getCodigo()));
		}
		if (StringUtils.isNotBlank(filtroConsultaSS.getDescricao())) {
			criteria.add(Restrictions.or(Restrictions.ilike("SRV." + ScoServico.Fields.DESCRICAO.toString(), filtroConsultaSS.getDescricao(), MatchMode.ANYWHERE), Restrictions.ilike("SRV." + ScoServico.Fields.NOME.toString(), filtroConsultaSS.getDescricao(), MatchMode.ANYWHERE)));
		}
		if (filtroConsultaSS.getGrupoServico() != null) {
			criteria.createAlias("SRV."+ScoServico.Fields.GRUPO_SERVICO.toString(), "GRS");
			criteria.add(Restrictions.eq("GRS." + ScoGrupoServico.Fields.CODIGO.toString(), filtroConsultaSS.getGrupoServico().getCodigo()));
		}			
	}
	public DetachedCriteria obterCriteriaConsultaItensSCSSVO(FiltroConsSCSSVO filtroConsultaSS) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoSolicitacaoServico.class, "SS");
		ProjectionList p = Projections.projectionList();
		p.add(Projections.property("SS." + ScoSolicitacaoServico.Fields.NUMERO.toString()), ItensSCSSVO.Fields.NUMERO.toString());
		p.add(Projections.property("SS." + ScoSolicitacaoServico.Fields.DESCRICAO.toString()), ItensSCSSVO.Fields.DESCRICAO.toString());
		p.add(Projections.property("SS." + ScoSolicitacaoServico.Fields.DT_SOLICITACAO.toString()), ItensSCSSVO.Fields.DT_SOLICITACAO.toString());
		p.add(Projections.property("CC_REQ." + FccCentroCustos.Fields.CODIGO.toString()), ItensSCSSVO.Fields.SEQ_CC_REQ.toString());
		p.add(Projections.property("CC_REQ." + FccCentroCustos.Fields.DESCRICAO.toString()), ItensSCSSVO.Fields.NOME_CC_REQ.toString());
		p.add(Projections.property("CC_APP." + FccCentroCustos.Fields.CODIGO.toString()), ItensSCSSVO.Fields.SEQ_CC_APLIC.toString());
		p.add(Projections.property("CC_APP." + FccCentroCustos.Fields.DESCRICAO.toString()), ItensSCSSVO.Fields.NOME_CC_APLIC.toString());
		p.add(Projections.property("PP_ATU." + ScoPontoParadaSolicitacao.Fields.CODIGO.toString()), ItensSCSSVO.Fields.SEQ_PONTO_PARADA_ATUAL.toString());
		p.add(Projections.property("PP_ATU." + ScoPontoParadaSolicitacao.Fields.DESCRICAO.toString()), ItensSCSSVO.Fields.NOME_PONTO_PARADA_ATUAL.toString());
		p.add(Projections.property("PP_ANT." + ScoPontoParadaSolicitacao.Fields.CODIGO.toString()), ItensSCSSVO.Fields.SEQ_PONTO_PARADA_ANTERIOR.toString());
		p.add(Projections.property("PP_ANT." + ScoPontoParadaSolicitacao.Fields.DESCRICAO.toString()), ItensSCSSVO.Fields.NOME_PONTO_PARADA_ANTERIOR.toString());
		p.add(Projections.property("SRV." + ScoServico.Fields.CODIGO.toString()), ItensSCSSVO.Fields.CODIGO_MATERIAL.toString());
		p.add(Projections.property("SRV." + ScoServico.Fields.NOME.toString()), ItensSCSSVO.Fields.NOME_MATERIAL.toString());
		p.add(Projections.property("SRV." + ScoServico.Fields.DESCRICAO.toString()), ItensSCSSVO.Fields.DESCRICAO_MATERIAL.toString());
		p.add(Projections.property("SS." + ScoSolicitacaoServico.Fields.DESCRICAO.toString()), ItensSCSSVO.Fields.DESCRICAO_SC.toString());
		p.add(Projections.property("SS." + ScoSolicitacaoServico.Fields.IND_EFETIVADA.toString()), ItensSCSSVO.Fields.EFETIVADA.toString());
		p.add(Projections.property("SS." + ScoSolicitacaoServico.Fields.IND_EXCLUSAO.toString()), ItensSCSSVO.Fields.EXCLUSAO.toString());
		p.add(Projections.property("SS." + ScoSolicitacaoServico.Fields.DT_AUTORIZACAO.toString()), ItensSCSSVO.Fields.DT_AUTORIZACAO.toString());
		p.add(Projections.property("SS." + ScoSolicitacaoServico.Fields.DT_EXCLUSAO.toString()), ItensSCSSVO.Fields.DT_EXCLUSAO.toString());
		p.add(Projections.property("FIS_SOL." + RapPessoasFisicas.Fields.NOME.toString()), ItensSCSSVO.Fields.NOME_SERVIDOR_GERACAO.toString());
		p.add(Projections.property("FIS_AUT." + RapPessoasFisicas.Fields.NOME.toString()), ItensSCSSVO.Fields.NOME_SERVIDOR_AUTORIZADOR.toString());
		p.add(Projections.property("FIS_EXC." + RapPessoasFisicas.Fields.NOME.toString()), ItensSCSSVO.Fields.NOME_SERVIDOR_EXCLUSAO.toString());
		p.add(Projections.sqlProjection("(SELECT COUNT(ass.slc_numero) FROM AGH.SCO_SOLICITACAO_COMPRA_SERVICO ass WHERE ass.sls_numero = {alias}.numero) AS "+ItensSCSSVO.Fields.QTD_SOLICITACOES_VINCULADAS.toString(), new String[]{ItensSCSSVO.Fields.QTD_SOLICITACOES_VINCULADAS.toString()}, new Type[]{IntegerType.INSTANCE}));
		p.add(Projections.sqlProjection("(SELECT MAX(fs_lic.itl_lct_numero) FROM AGH.SCO_FASES_SOLICITACOES fs_lic WHERE FS_LIC.TIPO = 'S' AND FS_LIC.ITL_LCT_NUMERO IS NOT NULL AND FS_LIC.IND_EXCLUSAO = 'N' AND FS_LIC.SLS_NUMERO = {alias}.NUMERO) AS "+ItensSCSSVO.Fields.LCT_NUMERO.toString(), new String[]{ItensSCSSVO.Fields.LCT_NUMERO.toString()}, new Type[]{IntegerType.INSTANCE}));
		p.add(Projections.sqlProjection("(SELECT MAX(fs_itl.itl_numero) FROM AGH.SCO_FASES_SOLICITACOES fs_itl WHERE FS_ITL.TIPO = 'S' AND FS_ITL.ITL_LCT_NUMERO IS NOT NULL AND FS_ITL.IND_EXCLUSAO = 'N' AND FS_ITL.ITL_LCT_NUMERO = (SELECT MAX(fs_lic2.itl_lct_numero) FROM AGH.SCO_FASES_SOLICITACOES fs_lic2 WHERE FS_LIC2.TIPO = 'S' AND FS_LIC2.ITL_LCT_NUMERO IS NOT NULL AND FS_LIC2.IND_EXCLUSAO = 'N' AND FS_LIC2.SLS_NUMERO = {alias}.NUMERO) AND FS_ITL.SLS_NUMERO = {alias}.NUMERO) AS "+ItensSCSSVO.Fields.ITL_NUMERO.toString(), new String[]{ItensSCSSVO.Fields.ITL_NUMERO.toString()}, new Type[]{IntegerType.INSTANCE}));
		p.add(Projections.sqlProjection("(SELECT MAX(mlc.descricao) FROM AGH.sco_licitacoes lic, AGH.sco_modalidade_licitacoes mlc WHERE lic.numero = (SELECT MAX(fs_lic3.itl_lct_numero) FROM AGH.SCO_FASES_SOLICITACOES fs_lic3 WHERE FS_LIC3.TIPO = 'S' AND FS_LIC3.ITL_LCT_NUMERO IS NOT NULL AND FS_LIC3.IND_EXCLUSAO = 'N' AND FS_LIC3.SLS_NUMERO = {alias}.NUMERO) AND lic.mlc_codigo = mlc.codigo) AS "+ItensSCSSVO.Fields.DESCRICAO_MODALIDADE_LICITACAO.toString(), new String[]{ItensSCSSVO.Fields.DESCRICAO_MODALIDADE_LICITACAO.toString()}, new Type[]{StringType.INSTANCE}));
		p.add(Projections.sqlProjection("(SELECT (CASE WHEN LIC2.IND_TIPO_PREGAO = 'P' THEN 'Presencial' WHEN LIC2.IND_TIPO_PREGAO = 'E' THEN 'Eletrônico' END) FROM AGH.sco_licitacoes lic2 WHERE lic2.numero = (SELECT MAX(fs_lic4.itl_lct_numero) FROM AGH.SCO_FASES_SOLICITACOES fs_lic4 WHERE FS_LIC4.TIPO = 'S' AND FS_LIC4.ITL_LCT_NUMERO IS NOT NULL AND FS_LIC4.IND_EXCLUSAO = 'N' AND FS_LIC4.SLS_NUMERO = {alias}.NUMERO)) AS "+ItensSCSSVO.Fields.DESCRICAO_MODALIDADE_PREGAO.toString(), new String[]{ItensSCSSVO.Fields.DESCRICAO_MODALIDADE_PREGAO.toString()}, new Type[]{StringType.INSTANCE}));
		p.add(Projections.sqlProjection("(SELECT MAX(af.pfr_lct_numero) FROM AGH.sco_fases_solicitacoes fs, AGH.sco_autorizacoes_forn af WHERE fs.tipo = 'S' AND fs.iaf_afn_numero = af.numero AND fs.ind_exclusao = 'N' AND fs.sls_numero = {alias}.NUMERO) AS "+ItensSCSSVO.Fields.NUMERO_AF.toString(), new String[]{ItensSCSSVO.Fields.NUMERO_AF.toString()}, new Type[]{IntegerType.INSTANCE}));
		p.add(Projections.sqlProjection("(SELECT MAX(afn.nro_complemento) FROM AGH.sco_autorizacoes_forn afn WHERE afn.numero = (SELECT MAX(af.numero) AS "+ItensSCSSVO.Fields.NUMERO_AF.toString()+" FROM AGH.sco_fases_solicitacoes fs, AGH.sco_autorizacoes_forn af WHERE fs.tipo = 'S' AND fs.iaf_afn_numero = af.numero AND fs.ind_exclusao = 'N' AND fs.sls_numero = {alias}.NUMERO)) AS "+ItensSCSSVO.Fields.COMPLEMENTO_AF.toString(), new String[]{ItensSCSSVO.Fields.COMPLEMENTO_AF.toString()}, new Type[]{IntegerType.INSTANCE}));
		p.add(Projections.sqlProjection("(SELECT frn.nome_fantasia FROM AGH.sco_autorizacoes_forn af, AGH.sco_fornecedores frn WHERE af.pfr_frn_numero = frn.numero AND af.numero = (SELECT MAX(af.numero) AS "+ItensSCSSVO.Fields.NUMERO_AF.toString()+" FROM AGH.sco_fases_solicitacoes fs, AGH.sco_autorizacoes_forn af WHERE fs.tipo = 'S' AND fs.iaf_afn_numero = af.numero AND fs.ind_exclusao = 'N' AND fs.sls_numero = {alias}.NUMERO)) AS "+ItensSCSSVO.Fields.NOME_FANTASIA_FORN_AF.toString(), new String[]{ItensSCSSVO.Fields.NOME_FANTASIA_FORN_AF.toString()}, new Type[]{StringType.INSTANCE}));
		p.add(Projections.sqlProjection("(SELECT count(*) FROM AGH.SCO_ARQUIVOS_ANEXOS AN WHERE AN.NUMERO = {alias}.NUMERO AND AN.TP_ORIGEM = 'SS') AS "+ItensSCSSVO.Fields.QTD_ANEXO.toString(), new String[]{ItensSCSSVO.Fields.QTD_ANEXO.toString()}, new Type[]{IntegerType.INSTANCE}));
		criteria.setProjection(p);
		criteria.createAlias("SS."+ScoSolicitacaoServico.Fields.CENTRO_CUSTO.toString(), "CC_REQ");
		criteria.createAlias("SS."+ScoSolicitacaoServico.Fields.CC_APLICADA.toString(), "CC_APP");
		criteria.createAlias("SS."+ScoSolicitacaoServico.Fields.PONTO_PARADA.toString(), "PP_ATU");
		criteria.createAlias("SS."+ScoSolicitacaoServico.Fields.PONTO_PARADA_ATUAL.toString(), "PP_ANT");
		criteria.createAlias("SS."+ScoSolicitacaoServico.Fields.SERVICO.toString(), "SRV");
		criteria.createAlias("SS."+ScoSolicitacaoServico.Fields.SERVIDOR.toString(), "RAP_SOL");
		criteria.createAlias("RAP_SOL."+RapServidores.Fields.PESSOA_FISICA.toString(), "FIS_SOL");
		criteria.createAlias("SS."+ScoSolicitacaoServico.Fields.SERVIDOR_AUTORIZADOR.toString(), "RAP_AUT", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("RAP_AUT."+RapServidores.Fields.PESSOA_FISICA.toString(), "FIS_AUT", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SS."+ScoSolicitacaoServico.Fields.SERVIDOR_EXCLUIDOR.toString(), "RAP_EXC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("RAP_EXC."+RapServidores.Fields.PESSOA_FISICA.toString(), "FIS_EXC", JoinType.LEFT_OUTER_JOIN);
		criteria.setResultTransformer(Transformers.aliasToBean(ItensSCSSVO.class));
		this.obterCriteriaConsultaItensSCSSVOSS(filtroConsultaSS, criteria);
		this.obterCriteriaConsultaItensSCSSVOFlags(filtroConsultaSS, criteria);
		this.obterCriteriaConsultaItensSCSSVODatas(filtroConsultaSS, criteria);
		this.obterCriteriaConsultaItensSCSSVOPontoParada(filtroConsultaSS, criteria);
		this.obterCriteriaConsultaItensSCSSVOPACAF(filtroConsultaSS, criteria);
		this.obterCriteriaConsultaServico(filtroConsultaSS, criteria);
		return criteria;
	}
	
	public List<PreItemPacVO> listaIdentificacaoItensPac(ScoPontoParadaSolicitacao caixa, RapServidores comprador, ScoServico servico, 
			boolean verificaFases, boolean excluido, boolean autorizada, boolean verficaPP, Integer numeroSSIni, Integer numeroSSFim){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoSolicitacaoServico.class, "SC");
		criteria.createAlias(ScoSolicitacaoServico.Fields.SERVICO.toString(), "SERV"); 
		criteria.createAlias(ScoSolicitacaoServico.Fields.PONTO_PARADA.toString(), "PP"); 
		criteria.createAlias(ScoSolicitacaoServico.Fields.CC_APLICADA.toString(), "CC_AP");
		criteria.createAlias(ScoSolicitacaoServico.Fields.CENTRO_CUSTO.toString(), "CC_SOL");
		criteria.createAlias(ScoSolicitacaoServico.Fields.SERVIDOR_COMPRADOR.toString(), "CC_RAP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SERV."+ScoServico.Fields.GRUPO_SERVICO, "GSV");
		ProjectionList projection = Projections.projectionList()
				.add(Projections.property("SC."+ScoSolicitacaoServico.Fields.NUMERO.toString()), PreItemPacVO.Fields.NUMERO.toString())
				.add(Projections.property("SERV."+ScoServico.Fields.CODIGO.toString()), PreItemPacVO.Fields.COD_MAT_SERV.toString())
				.add(Projections.property("SERV."+ScoServico.Fields.NOME.toString()), PreItemPacVO.Fields.NOME_MAT_SERV.toString())
				.add(Projections.property("SC."+ScoSolicitacaoServico.Fields.IND_URGENTE.toString()), PreItemPacVO.Fields.IND_URGENTE.toString())
				.add(Projections.property("SC."+ScoSolicitacaoServico.Fields.QTDE_SOLICITADA.toString()), PreItemPacVO.Fields.QTD_SS.toString())
				.add(Projections.property("SC."+ScoSolicitacaoServico.Fields.VALOR_UNIT_PREVISTO.toString()), PreItemPacVO.Fields.VALOR_UNIT_PREVISTO.toString())
				.add(Projections.property("PP."+ScoPontoParadaSolicitacao.Fields.CODIGO.toString()), PreItemPacVO.Fields.CAIXA_COD.toString())
				.add(Projections.property("PP."+ScoPontoParadaSolicitacao.Fields.DESCRICAO.toString()), PreItemPacVO.Fields.CAIXA_DESC.toString())
				.add(Projections.property("CC_AP."+FccCentroCustos.Fields.CODIGO.toString()), PreItemPacVO.Fields.CC_APLICACAO_COD.toString())
				.add(Projections.property("CC_AP."+FccCentroCustos.Fields.DESCRICAO.toString()), PreItemPacVO.Fields.CC_APLICACAO_DESC.toString())
				.add(Projections.property("CC_SOL."+FccCentroCustos.Fields.CODIGO.toString()), PreItemPacVO.Fields.CC_SOLICITANTE_COD.toString())
				.add(Projections.property("CC_SOL."+FccCentroCustos.Fields.DESCRICAO.toString()), PreItemPacVO.Fields.CC_SOLICITANTE_DESC.toString());
				criteria.setProjection(projection);	
				
		criteria.add(Restrictions.sqlRestriction("(( select 1 from AGH.FCP_TTL_X_SOLICITACOES TXS where TXS.SLS_NUMERO = this_.NUMERO ) IS NULL)"));
		criteria.add(Restrictions.eq("GSV."+ScoGrupoServico.Fields.IND_GERA_TITULO_AVULSO, Boolean.FALSE));
		
		if(servico != null) {
			criteria.add(Restrictions.eq("SC."+ScoSolicitacaoServico.Fields.SERVICO.toString(), servico));
		}
		if (caixa != null) {
			criteria.add(Restrictions.eq("SC."+ScoSolicitacaoServico.Fields.PPS_CODIGO.toString(), caixa.getCodigo().shortValue()));
		}
		else if (servico == null && verficaPP){
			
			criteria.addOrder(Order.desc("SC."+(ScoSolicitacaoServico.Fields.PPS_CODIGO.toString())));
		}
		if (comprador != null) {
			criteria.add(Restrictions.eq("CC_RAP."+RapServidores.Fields.ID.toString(), comprador.getId()));
		}
		
		if (excluido){
			criteria.add(Restrictions.eq("SC."+ScoSolicitacaoServico.Fields.IND_EXCLUSAO.toString(), false));
		}
		if (autorizada){
			criteria.add(Restrictions.isNotNull("SC."+ScoSolicitacaoServico.Fields.DT_AUTORIZACAO.toString()));
		}
		if (verificaFases){
			DetachedCriteria subQueryFasesSolicitacoes = DetachedCriteria.forClass(ScoFaseSolicitacao.class , "FS");
			ProjectionList projectionListSubQueryFasesSolicitacoes = Projections.projectionList()
			.add(Projections.property("FS."+ScoFaseSolicitacao.Fields.SLS_NUMERO.toString()));
			subQueryFasesSolicitacoes.setProjection(projectionListSubQueryFasesSolicitacoes);
			subQueryFasesSolicitacoes.add(Restrictions.eqProperty("FS."+ScoFaseSolicitacao.Fields.SLS_NUMERO.toString(), "SC."+ScoSolicitacaoServico.Fields.NUMERO.toString()));
			subQueryFasesSolicitacoes.add(Restrictions.eq("FS."+ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
					
			criteria.add(Subqueries.propertyNotIn("SC."+ScoSolicitacaoServico.Fields.NUMERO.toString(), subQueryFasesSolicitacoes));
		}
		
		this.adicionarCriteriaSolicitacao(criteria, numeroSSIni, numeroSSFim);
		criteria.addOrder(Order.asc("SC."+ScoSolicitacaoServico.Fields.NUMERO.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(PreItemPacVO.class));
		return executeCriteria(criteria);
	}
	
	public void adicionarCriteriaSolicitacao(DetachedCriteria criteria, Integer numeroSSIni, Integer numeroSSFim){
		
		if (numeroSSIni!=null && numeroSSFim!=null ){			
			criteria.add(Restrictions.between("SC."+ScoSolicitacaoServico.Fields.NUMERO.toString(), numeroSSIni, numeroSSFim));
		}
		else if (numeroSSIni!=null && numeroSSFim==null ) {
			criteria.add(Restrictions.eq("SC."+ScoSolicitacaoServico.Fields.NUMERO.toString(), numeroSSIni));
			
		} else  if (numeroSSIni==null && numeroSSFim!=null ) {
			
			criteria.add(Restrictions.eq("SC."+ScoSolicitacaoServico.Fields.NUMERO.toString(), numeroSSFim));
		}
		else {
			
			criteria.add(Restrictions.eq("PP."+ScoPontoParadaSolicitacao.Fields.TIPO.toString(), DominioTipoPontoParada.CP));
		}
	} 
	/**
	 * Retorna uma lista de solicitacoes de serviço de determinado serviço, natureza de despesa e verba de gestão pesquisando
	 * por codigo e descrição da solicitação de serviço
	 * @param filter
	 * @param servico
	 * @param naturezaDespesa
	 * @param verbaGestao
	 * @return
	 */
	public List<ScoSolicitacaoServico> pesquisarSolicServicoCodigoDescricao(
			Object filter, ScoServico servico, FsoNaturezaDespesa naturezaDespesa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoSolicitacaoServico.class, "SLS");
		criteria.createAlias("SLS."+ScoSolicitacaoServico.Fields.SERVICO.toString(), "SRV", JoinType.INNER_JOIN);
		criteria.createAlias("SLS."+ScoSolicitacaoServico.Fields.PONTO_PARADA.toString(), "PPS", JoinType.INNER_JOIN);		
		String filterStr = (String) filter;
		if (StringUtils.isNotBlank(filterStr)) {
			Criterion restriction = Restrictions.ilike("SLS."+ScoSolicitacaoServico.Fields.DESCRICAO.toString(), filterStr,	MatchMode.ANYWHERE);
			if (CoreUtil.isNumeroInteger(filter)) {
				restriction = Restrictions.or(restriction, Restrictions.eq("SLS."+ScoSolicitacaoServico.Fields.NUMERO.toString(), Integer.valueOf(filterStr)));
			}
			criteria.add(restriction);
		}
		DetachedCriteria subQueryFasesSolicitacoes = DetachedCriteria.forClass(ScoFaseSolicitacao.class , "FS");
		ProjectionList projectionListSubQueryFasesSolicitacoes = Projections.projectionList()
		.add(Projections.property("FS."+ScoFaseSolicitacao.Fields.SLS_NUMERO.toString()));
		subQueryFasesSolicitacoes.setProjection(projectionListSubQueryFasesSolicitacoes);
		subQueryFasesSolicitacoes.add(Restrictions.isNotNull("FS."+ScoFaseSolicitacao.Fields.SLS_NUMERO.toString()));
		subQueryFasesSolicitacoes.add(Restrictions.eq("FS."+ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));				
		criteria.add(Subqueries.propertyNotIn("SLS."+ScoSolicitacaoServico.Fields.NUMERO.toString(), subQueryFasesSolicitacoes));
		criteria.add(Restrictions.eq("SLS."+ScoSolicitacaoServico.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		criteria.add(Restrictions.isNotNull("SLS."+ScoSolicitacaoServico.Fields.DT_AUTORIZACAO.toString()));
		criteria.add(Restrictions.eq("PPS."+ScoPontoParadaSolicitacao.Fields.TIPO.toString(), DominioTipoPontoParada.CP));
		if (servico != null) {
			criteria.add(Restrictions.eq("SLS."+ScoSolicitacaoServico.Fields.SERVICO.toString(), servico));				
		}
		if (naturezaDespesa != null) {
			criteria.add(Restrictions.eq("SLS."+ScoSolicitacaoServico.Fields.NATUREZA_DESPESA.toString(), naturezaDespesa));
		}
		return this.executeCriteria(criteria);
	}
	
	/**
	 * Retorna uma lista de 10 solicitacoes de serviço que estao associadas a determinada natureza de despesa
	 * @param id
	 * @param isExclusao
	 * @return List
	 */
	
	public List<ScoSolicitacaoServico> buscarSolicitacaoServicoAssociadaNaturezaDespesa(FsoNaturezaDespesaId id, Boolean isExclusao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoSolicitacaoServico.class);
		criteria.createCriteria(ScoSolicitacaoServico.Fields.NATUREZA_DESPESA.toString(), "NAT", JoinType.INNER_JOIN);

		if (id.getGndCodigo() != null) {
			criteria.add(Restrictions.eq("NAT."+FsoNaturezaDespesa.Fields.GND_CODIGO.toString(), id.getGndCodigo()));
		}
		if (id.getCodigo() != null) {
			criteria.add(Restrictions.eq("NAT."+FsoNaturezaDespesa.Fields.CODIGO.toString(), id.getCodigo()));
		}
		if (isExclusao) {
			criteria.add(Restrictions.eq(ScoSolicitacaoServico.Fields.IND_EXCLUSAO.toString(),	Boolean.FALSE));
		}
		return executeCriteria(criteria, 0, 10, ScoSolicitacaoServico.Fields.NUMERO.toString(), true);
	}

	
	/**
	 * Retorna uma lista de 10 solicitacoes de serviço ativas em AF que estao associadas a natureza de despesa
	 * @param id
	 * @return List
	 */
	public List<ScoSolicitacaoServico> buscarSolicitacaoServicoAssociadaAutorizacaoFornEfetivada(FsoNaturezaDespesaId id) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoSolicitacaoServico.class, "SLS");
	//	criteria.getExecutableCriteria(getSession()).setMaxResults(10);
		criteria.createCriteria("SLS."+ScoSolicitacaoServico.Fields.NATUREZA_DESPESA.toString(), "NAT", JoinType.INNER_JOIN);
		criteria.addOrder(Order.asc("SLS."+ScoSolicitacaoServico.Fields.NUMERO.toString()));
		if (id.getGndCodigo() != null) {
			criteria.add(Restrictions.eq("NAT."+FsoNaturezaDespesa.Fields.GND_CODIGO.toString(), id.getGndCodigo()));
		}
		if (id.getCodigo() != null) {
			criteria.add(Restrictions.eq("NAT."+FsoNaturezaDespesa.Fields.CODIGO.toString(), id.getCodigo()));
		}
		DetachedCriteria subQueryFasesSolicitacoes = DetachedCriteria.forClass(ScoFaseSolicitacao.class , "FS");
		subQueryFasesSolicitacoes.createCriteria("FS."+ScoFaseSolicitacao.Fields.SCO_ITENS_AUTORIZACAO_FORN.toString(), "IAF", JoinType.INNER_JOIN);
		subQueryFasesSolicitacoes.createCriteria("IAF."+ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), "AF", JoinType.INNER_JOIN);
		ProjectionList projectionListSubQueryFasesSolicitacoes = Projections.projectionList()
		.add(Projections.property("FS."+ScoFaseSolicitacao.Fields.SLS_NUMERO.toString()));
		subQueryFasesSolicitacoes.setProjection(projectionListSubQueryFasesSolicitacoes);
		subQueryFasesSolicitacoes.add(Restrictions.eq("FS."+ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));		
		subQueryFasesSolicitacoes.add(Restrictions.eqProperty("FS."+ScoFaseSolicitacao.Fields.SLS_NUMERO.toString(),
				"SLS."+ScoSolicitacaoServico.Fields.NUMERO.toString()));		
		subQueryFasesSolicitacoes.add(Restrictions.eq("AF."+ScoAutorizacaoForn.Fields.SITUACAO.toString(), DominioSituacaoAutorizacaoFornecimento.EF));
		criteria.add(Subqueries.propertyIn("SLS."+ScoSolicitacaoServico.Fields.NUMERO.toString(), subQueryFasesSolicitacoes));
		criteria.add(Restrictions.eq("SLS."+ScoSolicitacaoServico.Fields.IND_EXCLUSAO.toString(),	Boolean.FALSE));
		return executeCriteria(criteria, 0, 10, null);
	}

	/**
	 * Retorna uma lista de até 10 solicitações de serviço associadas à verba de gestão
	 * @param verbaGestao
	 * @return List
	 */
	public List<ScoSolicitacaoServico> buscarSolicitacaoServicoAssociadaVerbaGestao(FsoVerbaGestao verbaGestao, Boolean filtraEfetivada) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoSolicitacaoServico.class, "SLS");
		//criteria.getExecutableCriteria(getSession()).setMaxResults(10);
		criteria.addOrder(Order.asc(ScoSolicitacaoServico.Fields.NUMERO.toString()));
		
		if (verbaGestao != null) {
			criteria.add(Restrictions.eq(ScoSolicitacaoServico.Fields.VERBA_GESTAO.toString(), verbaGestao));
		}

		if (filtraEfetivada) {
			criteria.add(Restrictions.eq(ScoSolicitacaoServico.Fields.IND_EFETIVADA.toString(), Boolean.FALSE));
		}
		
		return executeCriteria(criteria, 0 , 10, null);
	}
	
	/**
	 * #46298 - C1 - Buscar Solicitações de Serviço
	 * 
	 * @param filtro
	 * @param pontoParada
	 * @return
	 */
	public List<SolicitacaoTituloVO> obterListaSolicitacaoServico(FiltroSolicitacaoTituloVO filtro, Short pontoParada) {
		DetachedCriteria criteria = obterCriteriaListaSolicitacaoServico(filtro);
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("SLS." + ScoSolicitacaoServico.Fields.NUMERO.toString()).as(SolicitacaoTituloVO.Fields.SOLICITACAO.toString()))
				.add(Projections.property("SLS." + ScoSolicitacaoServico.Fields.DESCRICAO.toString()).as(SolicitacaoTituloVO.Fields.DESCRICAO.toString()))
				.add(Projections.property("SLS." + ScoSolicitacaoServico.Fields.APLICACAO.toString()).as(SolicitacaoTituloVO.Fields.APLICACAO.toString()))
				.add(Projections.property("VBG." + FsoVerbaGestao.Fields.SEQ.toString()).as(SolicitacaoTituloVO.Fields.VBG_SEQ.toString()))
				.add(Projections.property("VBG." + FsoVerbaGestao.Fields.DESCRICAO.toString()).as(SolicitacaoTituloVO.Fields.VBG_DESCRICAO.toString()))
				.add(Projections.property("SRV." + ScoServico.Fields.CODIGO.toString()).as(SolicitacaoTituloVO.Fields.CODIGO.toString()))
				.add(Projections.property("SRV." + ScoServico.Fields.NOME.toString()).as(SolicitacaoTituloVO.Fields.NOME.toString()))
				.add(Projections.property("GND." + FsoGrupoNaturezaDespesa.Fields.CODIGO.toString()).as(SolicitacaoTituloVO.Fields.GRUPO_NATUREZA_DESPESA.toString()))
				.add(Projections.property("GND." + FsoGrupoNaturezaDespesa.Fields.DESCRICAO.toString()).as(SolicitacaoTituloVO.Fields.DESCRICAO_GRUPO_NATUREZA_DESPESA.toString()))
				.add(Projections.property("NTD." + FsoNaturezaDespesa.Fields.CODIGO.toString()).as(SolicitacaoTituloVO.Fields.NTD_CODIGO.toString()))
				.add(Projections.property("NTD." + FsoNaturezaDespesa.Fields.DESCRICAO.toString()).as(SolicitacaoTituloVO.Fields.NTD_DESCRICAO.toString()))
				.add(Projections.property("SLS." + ScoSolicitacaoServico.Fields.DT_DIGITACAO.toString()).as(SolicitacaoTituloVO.Fields.DATA_GERACAO.toString()))
				.add(Projections.property("SLS." + ScoSolicitacaoServico.Fields.QTDE_SOLICITADA.toString()).as(SolicitacaoTituloVO.Fields.QTDE_SOLICITADA.toString()))
				.add(Projections.property("SLS." + ScoSolicitacaoServico.Fields.VALOR_UNIT_PREVISTO.toString()).as(SolicitacaoTituloVO.Fields.VALOR_UNIT_PREVISTO.toString()))
		);
		criteria.add(Restrictions.eq("SLS." + ScoSolicitacaoServico.Fields.IND_EXCLUSAO.toString(), DominioSimNao.N.isSim()));
		criteria.add(Restrictions.eq("SLS." + ScoSolicitacaoServico.Fields.PPS_CODIGO_LOC_ATUAL.toString(), pontoParada));
		criteria.add(Subqueries.notExists(obterSubCriteriaTituloSolicitacao()));
		criteria.add(Subqueries.notExists(obterSubCriteriaFaseSolicitacao()));
		obterRestrictionListaSolicitacaoServico(criteria, filtro);
		obterRestrictionDatasListaSolicitacaoServico(criteria, filtro);
		criteria.setResultTransformer(Transformers.aliasToBean(SolicitacaoTituloVO.class));
		return executeCriteria(criteria);
	}
	
	private DetachedCriteria obterCriteriaListaSolicitacaoServico(FiltroSolicitacaoTituloVO filtro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoSolicitacaoServico.class, "SLS");
		criteria.createAlias("SLS." + ScoSolicitacaoServico.Fields.SERVICO.toString(), "SRV", JoinType.INNER_JOIN);
		if (filtro.getVerbaGestao() != null) {
			criteria.createAlias("SLS." + ScoSolicitacaoServico.Fields.VERBA_GESTAO.toString(), "VBG", JoinType.INNER_JOIN);
		} else {
			criteria.createAlias("SLS." + ScoSolicitacaoServico.Fields.VERBA_GESTAO.toString(), "VBG", JoinType.LEFT_OUTER_JOIN);
		}
		if (filtro.getGrupoNaturezaDespesa() != null || filtro.getNaturezaDespesa() != null) {
			criteria.createAlias("SLS." + ScoSolicitacaoServico.Fields.NATUREZA_DESPESA.toString(), "NTD", JoinType.INNER_JOIN);
			criteria.createAlias("NTD." + FsoNaturezaDespesa.Fields.GRUPO_NATUREZA.toString(), "GND", JoinType.INNER_JOIN);
		} else {
			criteria.createAlias("SLS." + ScoSolicitacaoServico.Fields.NATUREZA_DESPESA.toString(), "NTD", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("NTD." + FsoNaturezaDespesa.Fields.GRUPO_NATUREZA.toString(), "GND", JoinType.LEFT_OUTER_JOIN);
		}
		return criteria;
	}
	
	private void obterRestrictionListaSolicitacaoServico(DetachedCriteria criteria, FiltroSolicitacaoTituloVO filtro) {
		if (filtro.getGrupoNaturezaDespesa() != null && filtro.getGrupoNaturezaDespesa().getCodigo() != null) {
			criteria.add(Restrictions.eq("SLS." + ScoSolicitacaoServico.Fields.NTD_GND_CODIGO.toString(), filtro.getGrupoNaturezaDespesa().getCodigo()));
		}
		if (filtro.getNaturezaDespesa() != null && filtro.getNaturezaDespesa().getId() != null && filtro.getNaturezaDespesa().getId().getCodigo() != null) {
			criteria.add(Restrictions.eq("SLS." + ScoSolicitacaoServico.Fields.NTD_CODIGO.toString(), filtro.getNaturezaDespesa().getId().getCodigo()));
		}
		if (filtro.getVerbaGestao() != null && filtro.getVerbaGestao().getSeq() != null) {
			criteria.add(Restrictions.eq("SLS." + ScoSolicitacaoServico.Fields.VBG_SEQ.toString(), filtro.getVerbaGestao().getSeq()));
		}
		if (filtro.getSolicitacao() != null) {
			criteria.add(Restrictions.eq("SLS." + ScoSolicitacaoServico.Fields.NUMERO.toString(), filtro.getSolicitacao()));
		}
		if (filtro.getServico() != null && filtro.getServico().getCodigo() != null) {
			criteria.add(Restrictions.eq("SLS." + ScoSolicitacaoServico.Fields.SERVICO_CODIGO.toString(), filtro.getServico().getCodigo()));
		}
		if (filtro.getGrupoServico() != null && filtro.getGrupoServico().getCodigo() != null) {
			criteria.add(Restrictions.eq("SRV." + ScoServico.Fields.GRUPO_SERVICO_CODIGO.toString(), filtro.getGrupoServico().getCodigo() ));
		}
	}
	
	private void obterRestrictionDatasListaSolicitacaoServico(DetachedCriteria criteria, FiltroSolicitacaoTituloVO filtro) {
		if (filtro.getDataGeracaoInicial() != null) {
			if (filtro.getDataGeracaoFinal() != null) {
				criteria.add(Restrictions.between("SLS." + ScoSolicitacaoServico.Fields.DT_DIGITACAO.toString(), 
						DateUtil.truncaData(filtro.getDataGeracaoInicial()), DateUtil.truncaDataFim(filtro.getDataGeracaoFinal())));
			} else {
				criteria.add(Restrictions.ge("SLS." + ScoSolicitacaoServico.Fields.DT_DIGITACAO.toString(), DateUtil.truncaData(filtro.getDataGeracaoInicial())));
			}
		} else {
			if (filtro.getDataGeracaoFinal() != null) {
				criteria.add(Restrictions.le("SLS." + ScoSolicitacaoServico.Fields.DT_DIGITACAO.toString(), DateUtil.truncaDataFim(filtro.getDataGeracaoFinal())));
			}
		}
	}
	
	private DetachedCriteria obterSubCriteriaTituloSolicitacao() {
		DetachedCriteria subCriteria = DetachedCriteria.forClass(FcpTituloSolicitacoes.class , "TXS");
		subCriteria.setProjection(Projections.property("TXS." + FcpTituloSolicitacoes.Fields.SEQ.toString()));
		subCriteria.add(Restrictions.eqProperty("TXS." + FcpTituloSolicitacoes.Fields.SLS_NUMERO.toString(), "SLS." + ScoSolicitacaoServico.Fields.NUMERO.toString()));
		return subCriteria;
	}
	
	private DetachedCriteria obterSubCriteriaFaseSolicitacao() {
		DetachedCriteria subCriteria = DetachedCriteria.forClass(ScoFaseSolicitacao.class , "FSC");
		subCriteria.setProjection(Projections.property("FSC." + ScoFaseSolicitacao.Fields.NUMERO.toString()));
		subCriteria.add(Restrictions.eqProperty("FSC." + ScoFaseSolicitacao.Fields.SLS_NUMERO.toString(), "SLS." + ScoSolicitacaoServico.Fields.NUMERO.toString()));
		subCriteria.add(Restrictions.eq("FSC." + ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), DominioSimNao.N.isSim()));
		return subCriteria;
	}
	
	/**
	 * 
	 * GETs and SETs
	 * 
	 */
	private ICentroCustoFacade getCentroCustoFacade() {
		return aICentroCustoFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return aIParametroFacade;
	}
	
	private ScoPontoParadaSolicitacaoDAO getScoPontoParadaSolicitacaoDAO() {
		return aScoPontoParadaSolicitacaoDAO;
	}
	
	protected ScoGrupoServicoDAO getScoGrupoServicoDAO() {
		return aScoGrupoServicoDAO;
	}
	
	private ScoFaseSolicitacaoDAO getScoFaseSolicitacaoDAO() {
		return aScoFaseSolicitacaoDAO;
	}
	
	public ScoCaracteristicaUsuarioCentroCustoDAO getScoCaracteristicaUsuarioCentroCustoDAO() {
		return scoCaracteristicaUsuarioCentroCustoDAO;
	}
	
	public void setScoCaracteristicaUsuarioCentroCustoDAO(ScoCaracteristicaUsuarioCentroCustoDAO scoCaracteristicaUsuarioCentroCustoDAO) {
		this.scoCaracteristicaUsuarioCentroCustoDAO = scoCaracteristicaUsuarioCentroCustoDAO;
	}

	
	
}