package br.gov.mec.aghu.compras.dao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.FetchMode;
import org.hibernate.Query;
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
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.pac.vo.PreItemPacVO;
import br.gov.mec.aghu.compras.vo.FiltroConsSCSSVO;
import br.gov.mec.aghu.compras.vo.ItensSCSSVO;
import br.gov.mec.aghu.compras.vo.RelatorioSolicitacaoCompraEstocavelVO;
import br.gov.mec.aghu.compras.vo.SolCompraVO;
import br.gov.mec.aghu.compras.vo.SolicitacaoDeComprasVO;
import br.gov.mec.aghu.compras.vo.SubRelatorioSolicitacoesEstocaveisVO;
import br.gov.mec.aghu.dominio.DominioClassifABC;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecedor;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;
import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.dominio.DominioTipoPontoParada;
import br.gov.mec.aghu.estoque.vo.RelatorioMateriaisEstocaveisGrupoCurvaAbcVO;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesaId;
import br.gov.mec.aghu.model.FsoVerbaGestao;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceEstoqueGeral;
import br.gov.mec.aghu.model.SceItemNotaRecebimento;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItemAutorizacaoFornId;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedor;
import br.gov.mec.aghu.model.ScoLogGeracaoScMatEstocavel;
import br.gov.mec.aghu.model.ScoMateriaisClassificacoes;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;
import br.gov.mec.aghu.model.ScoPontoServidor;
import br.gov.mec.aghu.model.ScoSolicitacaoCompraServico;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoProgramacaoEntrega;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.model.VRapPessoaServidor;
import br.gov.mec.aghu.suprimentos.vo.PesqLoteSolCompVO;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;


/**
 * Classe de acesso à entidade <code>br.gov.mec.aghu.model.ScoSolicitacoesDeCompras</code> 
 * 
 * @modulo compras
 * @author guilherme.finotti
 * @since 31/05/2011
 */
@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength", "PMD.NcssTypeCount"})
public class ScoSolicitacoesDeComprasDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoSolicitacaoDeCompra> {
	
	private static final Log LOG = LogFactory.getLog(ScoSolicitacoesDeComprasDAO.class);
	
    @Inject
    private IParametroFacade aIParametroFacade;
    @Inject
    private ICentroCustoFacade aICentroCustoFacade;
    @Inject
    private ScoFaseSolicitacaoDAO aScoFaseSolicitacaoDAO;
    @Inject
    private ScoPontoParadaSolicitacaoDAO aScoPontoParadaSolicitacaoDAO;
    @Inject
    private ScoCaracteristicaUsuarioCentroCustoDAO scoCaracteristicaUsuarioCentroCustoDAO;
    
	private static final long serialVersionUID = -7131871323871344682L;

	// Aliases e Parâmetros
	private final String SC = "sc", MAT = "mat", EXCLUSAO = "excl", FUNDO_FIXO = "ff", FSL = "fsl", SPE = "spe";
	
	public List<SolCompraVO> listarSolicitacoesDeCompras(Integer firstResult, Integer maxResult, String order, Boolean asc, 
			List<FccCentroCustos> listaCentroCustos, FccCentroCustos centroCusto, 
			FccCentroCustos centroCustoAplicada, ScoMaterial material, Date dtSolInicial, Date dtSolFinal, 
			DominioSimNao pendente, ScoPontoParadaSolicitacao ppsSolicitante, ScoPontoParadaSolicitacao ppsAutorizacao, ScoSolicitacaoDeCompra solicitacaoDeCompra, ScoPontoParadaSolicitacao pontoParadaAtual, RapServidores servidorLogado) {
		DetachedCriteria criteria = this.obterCriteriaSolicitacoesCompra(listaCentroCustos, centroCusto, centroCustoAplicada, material, 
											dtSolInicial, dtSolFinal, pendente, ppsSolicitante, ppsAutorizacao, solicitacaoDeCompra, pontoParadaAtual, servidorLogado);
		
		criteria.setProjection(getProjectionListSc(criteria));
		criteria.setResultTransformer(Transformers.aliasToBean(SolCompraVO.class));
		if (order == null){
			criteria.addOrder(Order.desc("SCO."+ScoSolicitacaoDeCompra.Fields.IND_URGENTE.toString()));
			criteria.addOrder(Order.desc("SCO."+ScoSolicitacaoDeCompra.Fields.PRIORIDADE.toString()));
			criteria.addOrder(Order.desc("SCO."+ScoSolicitacaoDeCompra.Fields.DATA_DIGITACAO.toString()));
			criteria.addOrder(Order.asc("SCO."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString()));
		}

		return this.executeCriteria(criteria, firstResult, maxResult, order,  asc);
	}

	public Long listarSolicitacoesDeComprasCount(List<FccCentroCustos> listaCentroCustos, FccCentroCustos centroCusto, 
			FccCentroCustos centroCustoAplicada, ScoMaterial material, Date dtSolInicial , 
			Date dtSolFinal, DominioSimNao pendente , ScoPontoParadaSolicitacao ppsSolicitante, ScoPontoParadaSolicitacao ppsAutorizacao, 
			ScoSolicitacaoDeCompra solicitacaoDeCompra, ScoPontoParadaSolicitacao pontoParada, RapServidores servidorLogado) {
		
		DetachedCriteria criteria = this.obterCriteriaSolicitacoesCompra(listaCentroCustos, centroCusto, centroCustoAplicada, material, 
				dtSolInicial, dtSolFinal, pendente, ppsSolicitante, ppsAutorizacao,  solicitacaoDeCompra, pontoParada, servidorLogado);
		criteria.setProjection(getProjectionListSc(criteria));
		return this.executeCriteriaCount(criteria);
	}

	@SuppressWarnings({"PMD.ExcessiveMethodLength","PMD.NPathComplexity"})
	private DetachedCriteria obterCriteriaSolicitacoesCompra(List<FccCentroCustos> listaCentroCustos, FccCentroCustos centroCusto, FccCentroCustos centroCustoAplicada, 
						ScoMaterial material, Date dtSolInicial , Date dtSolFinal, DominioSimNao pendente , 
						ScoPontoParadaSolicitacao ppsSolicitante, ScoPontoParadaSolicitacao ppsAutorizacao, 
						ScoSolicitacaoDeCompra solicitacaoDeCompra, ScoPontoParadaSolicitacao pontoParada, RapServidores servidorLogado) {
	
		FccCentroCustos ccFipe = this.getScoCaracteristicaUsuarioCentroCustoDAO().obterCcAplicacaoGeracaoGppg(servidorLogado);
		Boolean possuiPermissaoGerarGppg = false;
		Set<Integer> listaHierarquicaGppg = null;
		
		if (ccFipe != null) {
			listaHierarquicaGppg = getCentroCustoFacade().pesquisarCentroCustoComHierarquia(ccFipe.getCodigo());
			possuiPermissaoGerarGppg = (listaHierarquicaGppg != null && !listaHierarquicaGppg.isEmpty());
		}
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoSolicitacaoDeCompra.class, "SCO");
		SimpleDateFormat formatadorTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		SimpleDateFormat formatadorDia = new SimpleDateFormat("dd/MM/yyyy");
		
		criteria.setFetchMode(ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), FetchMode.JOIN);
		criteria.setFetchMode(ScoSolicitacaoDeCompra.Fields.UNIDADE_MEDIDA.toString(), FetchMode.JOIN);
		criteria.setFetchMode(ScoSolicitacaoDeCompra.Fields.CENTRO_CUSTO.toString(), FetchMode.JOIN);
		criteria.setFetchMode(ScoSolicitacaoDeCompra.Fields.CENTRO_CUSTO_APLICADA.toString(), FetchMode.JOIN);
		
		if (solicitacaoDeCompra!=null && solicitacaoDeCompra.getNumero()!=null){
			criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), solicitacaoDeCompra.getNumero()));
			if (centroCusto==null){
				if (!possuiPermissaoGerarGppg || ccFipe == null) {
					criteria.add(Restrictions.in("SCO."+ScoSolicitacaoDeCompra.Fields.CENTRO_CUSTO.toString(), listaCentroCustos));
				} else {
					if (centroCustoAplicada == null) {
						criteria.add(
								Restrictions.or(Restrictions.in("SCO."+ScoSolicitacaoDeCompra.Fields.CENTRO_CUSTO.toString(), listaCentroCustos),
										Restrictions.in("SCO."+ScoSolicitacaoDeCompra.Fields.CC_APLICADA_CODIGO.toString(), listaHierarquicaGppg)));	
					}
				}
			} 
			return criteria;
		}
		
		if(material != null) {
			criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), material));
		}
		if(centroCusto != null) {
			criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoDeCompra.Fields.CENTRO_CUSTO.toString(), centroCusto));
		}
		else {
			if (!possuiPermissaoGerarGppg || ccFipe == null) {
				criteria.add(Restrictions.in("SCO."+ScoSolicitacaoDeCompra.Fields.CENTRO_CUSTO.toString(), listaCentroCustos));
			} else {
				if (centroCustoAplicada == null) {
					criteria.add(
							Restrictions.or(Restrictions.in("SCO."+ScoSolicitacaoDeCompra.Fields.CENTRO_CUSTO.toString(), listaCentroCustos),
									Restrictions.in("SCO."+ScoSolicitacaoDeCompra.Fields.CC_APLICADA_CODIGO.toString(), listaHierarquicaGppg)));
				}
			}
		}

		if(centroCustoAplicada != null) {
			criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoDeCompra.Fields.CENTRO_CUSTO_APLICADA.toString(), centroCustoAplicada));
		}
		
		if (dtSolInicial!=null){
			if (dtSolFinal!=null){
				criteria.add(Restrictions.ge("SCO."+ScoSolicitacaoDeCompra.Fields.DATA_SOLICITACAO.toString(), dtSolInicial));
				try {
					criteria.add(Restrictions.le("SCO."+ScoSolicitacaoDeCompra.Fields.DATA_SOLICITACAO.toString(), formatadorTime.parse(formatadorDia.format(dtSolFinal)+" 23:59:59")));
				} catch (ParseException e) {
					LOG.error("Erro ao executar chamada"+ e.getMessage());
				}
			} else {
				criteria.add(Restrictions.ge("SCO."+ScoSolicitacaoDeCompra.Fields.DATA_SOLICITACAO.toString(), dtSolInicial));
			}
		}else if (dtSolFinal!=null){
				try {
					criteria.add(Restrictions.le("SCO."+ScoSolicitacaoDeCompra.Fields.DATA_SOLICITACAO.toString(), formatadorTime.parse(formatadorDia.format(dtSolFinal)+" 23:59:59")));
				} catch (ParseException e) {
					LOG.error("Erro ao executar chamada"+ e.getMessage());
				}
		}
		
		
		if(pontoParada!=null){
			criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoDeCompra.Fields.CODIGO_LOCALIZACAO_PONTO_PARADA_ATUAL.toString(), pontoParada.getCodigo().shortValue()));
		} else if (pendente ==DominioSimNao.S){
			criteria.add(Restrictions.disjunction()
					.add(Restrictions.eq("SCO."+ScoSolicitacaoDeCompra.Fields.CODIGO_LOCALIZACAO_PONTO_PARADA_ATUAL.toString(), ppsSolicitante.getCodigo().shortValue()))
					.add(Restrictions.eq(ScoSolicitacaoDeCompra.Fields.CODIGO_LOCALIZACAO_PONTO_PARADA_ATUAL.toString(), ppsAutorizacao.getCodigo().shortValue())));
		} else if(pendente ==DominioSimNao.N){
			criteria.add(Restrictions.and(Restrictions.ne("SCO."+ScoSolicitacaoDeCompra.Fields.CODIGO_LOCALIZACAO_PONTO_PARADA_ATUAL.toString(), ppsSolicitante.getCodigo().shortValue()),Restrictions.ne(ScoSolicitacaoDeCompra.Fields.CODIGO_LOCALIZACAO_PONTO_PARADA_ATUAL.toString(), ppsAutorizacao.getCodigo().shortValue())));
		}
		if (solicitacaoDeCompra!=null){
						
			if (solicitacaoDeCompra.getNumero()!=null){
				criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), solicitacaoDeCompra.getNumero()));
			}
			if (solicitacaoDeCompra.getDtAutorizacao()!=null){
				try {
					criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoDeCompra.Fields.DATA_AUTORIZACAO.toString(),formatadorDia.parse(formatadorDia.format(solicitacaoDeCompra.getDtAutorizacao()))));
				} catch (ParseException e) {
					LOG.error("Erro ao executar chamada"+ e.getMessage());
				}
			}
			if (solicitacaoDeCompra.getExclusao()!=null){
				criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoDeCompra.Fields.IND_EXCLUSAO.toString(), solicitacaoDeCompra.getExclusao()));
			}
			if (solicitacaoDeCompra.getDevolucao()!=null){
				criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoDeCompra.Fields.IND_DEVOLUCAO.toString(), solicitacaoDeCompra.getDevolucao()));
			}
			if (solicitacaoDeCompra.getPrioridade()!=null){
				criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoDeCompra.Fields.PRIORIDADE.toString(), solicitacaoDeCompra.getPrioridade()));
			}
			if (solicitacaoDeCompra.getUrgente()!=null){
				criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoDeCompra.Fields.IND_URGENTE.toString(), solicitacaoDeCompra.getUrgente()));
			}
			if (solicitacaoDeCompra.getMatExclusivo()!=null){
				criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoDeCompra.Fields.IND_MAT_EXCLUSIVO.toString(), solicitacaoDeCompra.getMatExclusivo()));
			}
			if (solicitacaoDeCompra.getEfetivada()!=null){
				criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoDeCompra.Fields.IND_EFETIVADA.toString(), solicitacaoDeCompra.getEfetivada()));
			}
		}
		return criteria;
	}
	
	public List<SolCompraVO> listarSolicitacoesDeComprasSemParametros(Integer firstResult, Integer maxResult, String order, Boolean asc,
			List<FccCentroCustos> listaCentroCustos, ScoPontoParadaSolicitacao ppsSolicitante, ScoPontoParadaSolicitacao ppsAutorizacao, RapServidores servidorLogado){
		DetachedCriteria criteria = this.obterCriterioSemParamtro(listaCentroCustos, ppsSolicitante, ppsAutorizacao, servidorLogado);
		criteria.setProjection(getProjectionListSc(criteria));
		criteria.setResultTransformer(Transformers.aliasToBean(SolCompraVO.class));
		criteria.addOrder(Order.desc("SCO."+ScoSolicitacaoDeCompra.Fields.IND_URGENTE.toString()));
		criteria.addOrder(Order.desc("SCO."+ScoSolicitacaoDeCompra.Fields.PRIORIDADE.toString()));
		criteria.addOrder(Order.desc("SCO."+ScoSolicitacaoDeCompra.Fields.DATA_DIGITACAO.toString()));
		criteria.addOrder(Order.asc("SCO."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString()));
		return this.executeCriteria(criteria, firstResult, maxResult, order,  asc);
	}
	
	public Long listarSolicitacoesDeComprasCountSemParametros(List<FccCentroCustos> listaCentroCustos, 
			ScoPontoParadaSolicitacao ppsSolicitante, ScoPontoParadaSolicitacao ppsAutorizacao, RapServidores servidorLogado){
		DetachedCriteria criteria = this.obterCriterioSemParamtro(listaCentroCustos, ppsSolicitante, ppsAutorizacao, servidorLogado);
		criteria.setProjection(getProjectionListSc(criteria));
		return this.executeCriteriaCount(criteria);
	}
	
	public DetachedCriteria obterCriterioSemParamtro(List<FccCentroCustos> listaCentroCustos, ScoPontoParadaSolicitacao ppsSolicitante, ScoPontoParadaSolicitacao ppsAutorizacao, RapServidores servidorLogado){
			
				DetachedCriteria criteria = DetachedCriteria.forClass(ScoSolicitacaoDeCompra.class, "SCO");
		criteria.add(Restrictions.disjunction()
				.add(Restrictions.eq("SCO."+ScoSolicitacaoDeCompra.Fields.CODIGO_LOCALIZACAO_PONTO_PARADA_ATUAL.toString(), ppsSolicitante.getCodigo().shortValue()))
				.add(Restrictions.eq("SCO."+ScoSolicitacaoDeCompra.Fields.CODIGO_LOCALIZACAO_PONTO_PARADA_ATUAL.toString(), ppsAutorizacao.getCodigo().shortValue())));
		criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoDeCompra.Fields.IND_EXCLUSAO.toString(), false));
		
		FccCentroCustos ccFipe = this.getScoCaracteristicaUsuarioCentroCustoDAO().obterCcAplicacaoGeracaoGppg(servidorLogado);
		Boolean possuiPermissaoGerarGppg = false;
		Set<Integer> listaHierarquicaGppg = null;
		
		if (ccFipe != null) {
			listaHierarquicaGppg = getCentroCustoFacade().pesquisarCentroCustoComHierarquia(ccFipe.getCodigo());
			possuiPermissaoGerarGppg = (listaHierarquicaGppg != null && !listaHierarquicaGppg.isEmpty());
		}
		
		if (!possuiPermissaoGerarGppg || ccFipe == null) {
			criteria.add(Restrictions.in("SCO."+ScoSolicitacaoDeCompra.Fields.CENTRO_CUSTO.toString(), listaCentroCustos));
		} else {
			criteria.add(Restrictions.or(Restrictions.in("SCO."+ScoSolicitacaoDeCompra.Fields.CENTRO_CUSTO.toString(), listaCentroCustos),
											Restrictions.in("SCO."+ScoSolicitacaoDeCompra.Fields.CC_APLICADA_CODIGO.toString(), listaHierarquicaGppg)));
		}
		return criteria;
	}
	
	public List<ScoSolicitacaoDeCompra> listarSolicitacoesDeComprasPorCodigoPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoSolicitacaoDeCompra.class);
		criteria.add(Restrictions.eq(ScoSolicitacaoDeCompra.Fields.PAC_CODIGO.toString(), pacCodigo));
    	return executeCriteria(criteria);
	}
	
	public List<ScoSolicitacaoDeCompra> listarSolicitacoesDeComprasPorNumeroVinculado(Object objPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoSolicitacaoDeCompra.class);
		String strPesquisa = (String) objPesquisa;
		
		criteria.add(Restrictions.eq(ScoSolicitacaoDeCompra.Fields.IND_EFETIVADA.toString(), false));
		criteria.addOrder(Order.asc(ScoSolicitacaoDeCompra.Fields.NUMERO.toString()));
		
		if (CoreUtil.isNumeroInteger(strPesquisa)) {
			criteria.add(Restrictions.eq(ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), Integer.valueOf(strPesquisa)));			
		}
		return executeCriteria(criteria, 0, 100, ScoSolicitacaoDeCompra.Fields.DATA_SOLICITACAO.toString(), true);
		
	}
	
// # 5260 - Eduardo Ando
	
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public List<SolicitacaoDeComprasVO> listarSolicitacoesDeComprasPorNumero (List<Integer> numSolicComp) {
		
		SimpleDateFormat sdf_1 = new SimpleDateFormat("dd/MM/yyyy");
		StringBuffer hql = new StringBuffer(3000);
		Query query = null;
		List<SolicitacaoDeComprasVO> result = new ArrayList<SolicitacaoDeComprasVO>();   
	
		hql.append("SELECT  SLC.").append(ScoSolicitacaoDeCompra.Fields.NUMERO.toString()).append(",SLC.").append(ScoSolicitacaoDeCompra.Fields.DATA_SOLICITACAO.toString());
		hql.append(",SLC.").append(ScoSolicitacaoDeCompra.Fields.DATA_AUTORIZACAO.toString()).append(",SLC.").append(ScoSolicitacaoDeCompra.Fields.CCT_CODIGO.toString());
		hql.append(",CCT." ).append(FccCentroCustos.Fields.DESCRICAO.toString()).append(",SLC.").append(ScoSolicitacaoDeCompra.Fields.CC_APLICADA_CODIGO.toString());
	    hql.append(",CCT1.").append(FccCentroCustos.Fields.DESCRICAO.toString()).append(",SLC.").append(ScoSolicitacaoDeCompra.Fields.MAT_CODIGO.toString());
		hql.append(",MAT.").append(ScoMaterial.Fields.NOME.toString()).append(",SLC.").append(ScoSolicitacaoDeCompra.Fields.UMD_CODIGO.toString());
		hql.append(",SLC.").append(ScoSolicitacaoDeCompra.Fields.QUANTIDADE_SOLICITADA.toString()).append(",SLC.").append(ScoSolicitacaoDeCompra.Fields.DIAS_DURACAO.toString());
		hql.append(",SLC.").append(ScoSolicitacaoDeCompra.Fields.QUANTIDADE_APROVADA.toString()).append(",SLC.").append(ScoSolicitacaoDeCompra.Fields.VERBA_GESTAO_SEQ.toString());
		hql.append(",SLC.").append(ScoSolicitacaoDeCompra.Fields.NTD_GND_CODIGO.toString()).append(",SLC.").append(ScoSolicitacaoDeCompra.Fields.NTD_CODIGO.toString());
		hql.append(",SLC.").append(ScoSolicitacaoDeCompra.Fields.DESCRICAO.toString()).append(",MAT.").append(ScoMaterial.Fields.DESCRICAO.toString());
		hql.append(",PAC.").append(AipPacientes.Fields.NOME.toString()).append(",SLC.").append(ScoSolicitacaoDeCompra.Fields.APLICACAO.toString());
		hql.append(",PAC.").append(AipPacientes.Fields.PRONTUARIO.toString()).append(",SLC.").append(ScoSolicitacaoDeCompra.Fields.JUSTIFICATIVA_USO.toString());
		hql.append(",SLC.").append(ScoSolicitacaoDeCompra.Fields.MOTIVO_URGENCIA.toString()).append(",SLC.").append(ScoSolicitacaoDeCompra.Fields.MOTIVO_PRIORIDADE.toString());
		hql.append(",SLC.").append(ScoSolicitacaoDeCompra.Fields.JUSTIFICATIVA_EXCLUSIVIDADE.toString()).append(",PES.").append(RapPessoasFisicas.Fields.NOME.toString());
		hql.append(",PES1.").append(RapPessoasFisicas.Fields.NOME.toString()).append(",SER1.").append(RapServidores.Fields.MATRICULA.toString());
		hql.append(",SLC.").append(ScoSolicitacaoDeCompra.Fields.NRO_INVESTIMENTO.toString()).append(",SLC.").append(ScoSolicitacaoDeCompra.Fields.NRO_PROJETO.toString());
		hql.append(",NTD.").append(FsoNaturezaDespesa.Fields.DESCRICAO.toString()).append(",SLC.").append(ScoSolicitacaoDeCompra.Fields.DT_ALTERACAO.toString());
		hql.append(",SLC.").append(ScoSolicitacaoDeCompra.Fields.SER_VIN_CODIGO_ALTERADA.toString()).append(",SLC.").append(ScoSolicitacaoDeCompra.Fields.SER_MATRICULA_ALTERADA.toString());
		hql.append(",VBG.").append(FsoVerbaGestao.Fields.DESCRICAO.toString()).append(",SLC.").append(ScoSolicitacaoDeCompra.Fields.VALOR_UNIT_PREVISTO.toString());
		hql.append(",SLC.").append(ScoSolicitacaoDeCompra.Fields.DT_ORCAMENTO.toString()).append(",SLC.").append(ScoSolicitacaoDeCompra.Fields.SER_VIN_CODIGO_ORCAMENTO.toString());
		hql.append(",SLC.").append(ScoSolicitacaoDeCompra.Fields.SER_MATRICULA_ORCAMENTO.toString()).append(",SER.").append(RapServidores.Fields.NRO_RAMAL.toString());
		hql.append(",SER1.").append(RapServidores.Fields.NRO_RAMAL.toString()).append(",CCT2.").append(FccCentroCustos.Fields.DESCRICAO.toString());
		hql.append(",SER.").append(RapServidores.Fields.MATRICULA.toString()).append(",SLC.").append(ScoSolicitacaoDeCompra.Fields.DT_MAX_ATENDIMENTO.toString());
		hql.append(" FROM ").append(ScoSolicitacaoDeCompra.class.getSimpleName()).append(" SLC ");
		
		hql.append("LEFT JOIN SLC.").append(ScoSolicitacaoDeCompra.Fields.PACIENTE.toString()).append(" PAC ");
		hql.append("INNER JOIN SLC.").append(ScoSolicitacaoDeCompra.Fields.MATERIAL.toString()).append(" MAT ");
		hql.append("INNER JOIN SLC.").append(ScoSolicitacaoDeCompra.Fields.NOME_SOLICITANTE.toString()).append(" PES ");
		hql.append("LEFT JOIN SLC.").append(ScoSolicitacaoDeCompra.Fields.NOME_AUTORIZANTE.toString()).append(" PES1 ");
		hql.append("INNER JOIN SLC.").append(ScoSolicitacaoDeCompra.Fields.SOLICITANTE.toString()).append(" SER ");
		hql.append("LEFT JOIN SLC.").append(ScoSolicitacaoDeCompra.Fields.AUTORIZANTE.toString()).append(" SER1 ");
		hql.append("LEFT JOIN SLC.").append(ScoSolicitacaoDeCompra.Fields.NATUREZA_DESPESA.toString()).append(" NTD ");
		hql.append("INNER JOIN SLC.").append(ScoSolicitacaoDeCompra.Fields.CENTRO_CUSTO.toString()).append(" CCT ");
		hql.append("INNER JOIN SLC.").append(ScoSolicitacaoDeCompra.Fields.CENTRO_CUSTO_APLICADA.toString()).append(" CCT1 ");
		hql.append("LEFT JOIN SLC.").append(ScoSolicitacaoDeCompra.Fields.CCT_AUTZ_TECNICA.toString()).append(" CCT2 ");
		hql.append("LEFT JOIN SLC.").append(ScoSolicitacaoDeCompra.Fields.CONVENIO_FINANCEIRO.toString()).append(" CVF ");
		hql.append("LEFT JOIN SLC.").append(ScoSolicitacaoDeCompra.Fields.VERBA_GESTAO.toString()).append(" VBG ");
		
		if (numSolicComp!=null && numSolicComp.size()>0){
			
			hql.append("WHERE SLC.").append(ScoSolicitacaoDeCompra.Fields.NUMERO.toString()).append(" in (:numSolicComp)");
		}
		
		hql.append(" ORDER BY SLC.").append(ScoSolicitacaoDeCompra.Fields.NUMERO.toString());

		query = createHibernateQuery(hql.toString());
		query.setParameterList("numSolicComp", numSolicComp);
		
		SolicitacaoDeComprasVO aux;
	       
        List<Object[]> resultList = query.list();      
       
        for (Object[] object : resultList) {
        	
        
            aux = new SolicitacaoDeComprasVO();
            
            if(object[0]!=null){ aux.setNroSolicitacao((Integer) object[0]);   }
            if(object[1]!=null){ aux.setDataSltc((Date) object[1]);        }
            if(object[2]!=null){ aux.setDataAutz((Date) object[2]);        }
            if(object[3]!=null){ aux.setcCustoRqsc((Integer) object[3]);  }    
            if(object[4]!=null){ aux.setcCustoRqscDesc((String) object[4]);   }
            if(object[5]!=null){ aux.setcCustoAplc((Integer) object[5]);      }
            if(object[6]!=null){ aux.setcCustoAplcDesc((String) object[6]);   }
            if(object[7]!=null){ aux.setCodigo((Integer) object[7]);         }
            if(object[8]!=null){ aux.setNomeMaterial((String) object[8]);    }
            if(object[9]!=null){ aux.setUnid((String) object[9]);            }
            if(object[10]!=null){aux.setQtdSltd((Long) object[10]);         }
            if(object[11]!=null){ aux.setDiasDur((Short) object[11]);       }
            if(object[12]!=null){ aux.setQtdAprov((Long) object[12]);       }
            if(object[13]!=null){ aux.setConvenio1((Integer)object[13]);   }
            if(object[14]!=null){ aux.setNatureza1((Integer) object[14]);   }
            if(object[15]!=null){ aux.setNatureza2((Byte) object[15]);      }
            if(object[16]!=null){ aux.setDescricaoSolicitacao((String) object[16]);   }
            if(object[17]!=null){ aux.setDescricaoCatalogo((String) object[17]); }
            if(object[18]!=null){ aux.setPacNome((String) object[18]);    }
            if(object[19]!=null){ aux.setAplicacao((String) object[19]);  }
            if(object[20]!=null){ aux.setProntuario((Integer) object[20]);    }
            if(object[21]!=null){ aux.setJustificativa((String) object[21]);  }
            if(object[22]!=null){ aux.setUrgencia((String) object[22]);       }
            if(object[23]!=null){ aux.setPrioridade((String) object[23]);     }
            if(object[24]!=null){ aux.setExclusividade((String) object[24]);  }
            if(object[25]!=null){ aux.setNomeSolicitante((String) object[25]); }
            if(object[26]!=null){ aux.setNomeAutorizador((String) object[26]);  }
            if(object[27]!=null){ aux.setCartaoPontoAutorizador((Integer) object[27]);    }
            if(object[28]!=null){ aux.setNroInvestimento((Integer) object[28]);        }
            if(object[29]!=null){ aux.setNroProjeto((Integer) object[29]);    }
            if(object[30]!=null){ aux.setNatureza3((String) object[30]);      }
            if(object[31]!=null){ aux.setDataAlt((String) sdf_1.format(object[31]));      }
            if(object[32]!=null){ aux.setSerVinCodigoAlterada((Short) object[32]);        }
            if(object[33]!=null){ aux.setSerMatriculaAlterada((Integer) object[33]);     }
            if(object[34]!=null){ aux.setConvenio2((String) object[34]);   }
            if(object[35]!=null){aux.setValorUnitPrevisto((BigDecimal) object[35]);     }
            if(object[36]!=null){ aux.setDataLiber((Date) object[36]);    }
            if(object[37]!=null){ aux.setSerVinCodigoOrcamento((Short) object[37]);    }        
            if(object[38]!=null){aux.setSerMatriculaOrcamento((Integer) object[38]);  }
            if(object[39]!=null){aux.setRamalSolic((Integer) object[39]);  }
            if(object[40]!=null){aux.setRamalAut((Integer) object[40]);    }
            if(object[41]!=null){aux.setDescricaoAutTec((String) object[41]);   }
            if(object[42]!=null){aux.setCartaoPontoSolicitante((Integer) object[42]);   }
            if(object[43]!=null){aux.setDtMaxAtendimento((Date) object[43]);   }
            result.add(aux);
           
        }
       return result;
	}
	
	@SuppressWarnings("unchecked")
	public Integer ultNR (Integer numSolicComp){
		
		StringBuffer hql = new StringBuffer(3000);
		Query query = null;
		
		hql.append("SELECT MAX (INR.").append(SceItemNotaRecebimento.Fields.NOTA_RECEBIMENTO_SEQ.toString()).append(')'); 
		hql.append(" FROM ").append(SceItemNotaRecebimento.class.getSimpleName()).append(" INR, ").append(ScoFaseSolicitacao.class.getSimpleName()).append(" FSC ");
		hql.append(" WHERE FSC.").append(ScoFaseSolicitacao.Fields.SLC_NUMERO.toString()).append(" = :numSolicComp ");
		hql.append(" AND   INR.").append(SceItemNotaRecebimento.Fields.IAF_AFN_NUMERO.toString()).append(" = FSC.").append(ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()).append(' ');
		hql.append(" AND   INR.").append(SceItemNotaRecebimento.Fields.IAF_NUMERO.toString()).append(" = FSC.").append(ScoFaseSolicitacao.Fields.IAF_NUMERO.toString()).append(' ');
		query = createHibernateQuery(hql.toString());
		query.setParameter("numSolicComp", numSolicComp);
		
		List<Integer> lastn;
		Integer ultNR;
		lastn = query.list();
		ultNR = lastn.get(0);
		
		return ultNR;
		
	}

	/**
	 * Cursor c_slc_aparelho
	 * @param pacCodigo
	 * @param phi
	 * @param 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ScoSolicitacaoDeCompra> listarSolicitacoesAparelho(Integer pacCodigo, Integer phi, 
			Boolean indRecebimento, Long cn5Numero) {
		
		List<ScoSolicitacaoDeCompra> result = null;
		StringBuffer hql = new StringBuffer(3000);
		Query query = null;
			hql.append(" select slc from ").append(ScoSolicitacaoDeCompra.class.getName()).append(" as slc ");
		hql.append(" where slc.").append(ScoSolicitacaoDeCompra.Fields.PAC_CODIGO.toString()).append(" = :pacCodigo").append(" and slc.");
		hql.append(ScoSolicitacaoDeCompra.Fields.IND_RECEBIMENTO.toString()).append(" = :indRecebimento");
		hql.append(" and slc.").append(ScoSolicitacaoDeCompra.Fields.MAT_CODIGO.toString());
		hql.append(" in (").append(" select mcl.");
			hql.append(ScoMateriaisClassificacoes.Fields.MAT_CODIGO.toString());
			hql.append(" from ").append(FatProcedHospInternos.class.getName()).append(" as phi ");
			hql.append(", ").append(ScoMateriaisClassificacoes.class.getName()).append(" as mcl ");
			hql.append(" where mcl.").append(ScoMateriaisClassificacoes.Fields.CN5.toString()).append(" = :cn5Numero");
			hql.append(" and phi.").append(FatProcedHospInternos.Fields.MAT_CODIGO.toString()).append(" = mcl.");
			hql.append(ScoMateriaisClassificacoes.Fields.MAT_CODIGO.toString());
			hql.append(" and phi.").append(FatProcedHospInternos.Fields.SEQ.toString()).append(" = :phi");

			
		hql.append(')');

		query = createHibernateQuery(hql.toString());
		query.setParameter("pacCodigo", pacCodigo);
		query.setParameter("phi", phi);
		query.setParameter("indRecebimento", indRecebimento);
		query.setParameter("cn5Numero", cn5Numero);

		result = query.list();
		
		return result;	
	}
	
	public Boolean verificarSCNaturezaDespEVerbaGestaoNulo(Integer numero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoSolicitacaoDeCompra.class);
		
		criteria.add(Restrictions.eq(ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), numero));
		criteria.add(Restrictions.or(Restrictions.isNull(ScoSolicitacaoDeCompra.Fields.NATUREZA_DESPESA.toString()), 
				Restrictions.isNull(ScoSolicitacaoDeCompra.Fields.VERBA_GESTAO.toString()) ));
		
		return executeCriteriaCount(criteria) > 0;
	}
	
	public Long obterQuantidadeSolicitacoesEmLicitaca(Integer numero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoSolicitacaoDeCompra.class,"SCO");

		criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), numero));
		criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoDeCompra.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		criteria.add(Restrictions.isNull("SCO."+ScoSolicitacaoDeCompra.Fields.DATA_ANALISE.toString()));
		
		DetachedCriteria subQuery = DetachedCriteria.forClass(ScoFaseSolicitacao.class,"FSO");
		subQuery.setProjection(Projections.projectionList()
				.add(Projections.property("FSO."+ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString()+"."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString())));

		subQuery.add(Restrictions.eqProperty("FSO."+ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString()+"."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), "SCO."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString()));
		subQuery.add(Restrictions.eq("FSO."+ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE ));
		subQuery.add(Restrictions.isNotNull("FSO."+ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()));

		
		DetachedCriteria subQuery1 = DetachedCriteria.forClass(ScoFaseSolicitacao.class,"FSO1");
		subQuery1.setProjection(Projections.projectionList()
				.add(Projections.property("FSO1."+ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString()+"."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString())));

		subQuery1.add(Restrictions.eqProperty("FSO1."+ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS.toString()+"."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), "SCO."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString()));
		subQuery1.add(Restrictions.eq("FSO1."+ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE ));
		subQuery1.add(Restrictions.isNotNull("FSO1."+ScoFaseSolicitacao.Fields.ITEM_LICITACAO.toString()));
		
		criteria.add(Subqueries.propertyIn("SCO."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), subQuery1));
		criteria.add(Subqueries.propertyNotIn("SCO."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), subQuery));

		return executeCriteriaCount(criteria);
	}
	
	public Long obterQuantidadeSolicitacoesEmAfPorNumESituacao(Integer numero, DominioSituacaoAutorizacaoFornecimento[] situacoes) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoSolicitacaoDeCompra.class,"SCO");

		criteria.createAlias(ScoSolicitacaoDeCompra.Fields.FASES_SOLICITACAO.toString(), "FSO", JoinType.INNER_JOIN);
		criteria.createAlias("FSO."+ScoFaseSolicitacao.Fields.SCO_ITENS_AUTORIZACAO_FORN.toString(), "IAF", JoinType.INNER_JOIN);
		criteria.createAlias("IAF."+ScoItemAutorizacaoForn.Fields.AUTORIZACAO_FORN.toString(), "AFO", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), numero));
		criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoDeCompra.Fields.IND_EXCLUSAO.toString(),  Boolean.FALSE ));
		criteria.add(Restrictions.isNull("SCO."+ScoSolicitacaoDeCompra.Fields.DATA_ANALISE.toString()));
		criteria.add(Restrictions.in("AFO."+ScoAutorizacaoForn.Fields.SITUACAO.toString(), situacoes));
		
		return executeCriteriaCount(criteria);
	}
	
	public List<ScoSolicitacaoDeCompra> pesquisarSolicitacaoDeCompraPorDtAnalise(
			Date dtAnalise) {
		DetachedCriteria criteria = DetachedCriteria.forClass(
				ScoSolicitacaoDeCompra.class, "SCO");
		
		criteria.add(Restrictions.le("SCO."+ ScoSolicitacaoDeCompra.Fields.DATA_ANALISE.toString(),DateUtil.obterDataComHoraFinal(dtAnalise)));
		return executeCriteria(criteria);
	}

	/**
	 * Busca qtde saldo em SC que ainda não está em AF.
	 * @param mat Material
	 * @return
	 */
	public Integer buscarQtdeSaldoScNaoAf(ScoMaterial mat) {
		assert mat != null : "Material não definido";
		return buscarQtdeSaldoScNaoAf(mat.getCodigo());
	}

	/**
	 * Busca qtde saldo em SC que ainda não está em AF.
	 * @param materialId ID do Material
	 * @return
	 */
	public Integer buscarQtdeSaldoScNaoAf(Integer materialId) {
		assert materialId != null : "ID do material não definido";
		
		// HQL
		StringBuilder hql = new StringBuilder()
			.append("select sum(case when ").append(SC).append('.').append(ScoSolicitacaoDeCompra.Fields.QUANTIDADE_APROVADA)
			.append(" is null or ").append(SC).append('.').append(ScoSolicitacaoDeCompra.Fields.QUANTIDADE_APROVADA)
			.append(" = 0 then ").append(SC).append('.').append(ScoSolicitacaoDeCompra.Fields.QUANTIDADE_SOLICITADA)
			.append(" else ").append(SC).append('.').append(ScoSolicitacaoDeCompra.Fields.QUANTIDADE_APROVADA).append(" end)")
			.append(getScsNaoAfCriteria());
		
		// Query Parametrizada
		Query query = createHibernateQuery(hql.toString());
		query.setInteger(MAT, materialId);
		query.setBoolean(EXCLUSAO, false);
		query.setBoolean(FUNDO_FIXO, false);
		
		// Resultado
		Long qtde = (Long) query.uniqueResult();
		return qtde != null ? qtde.intValue() : 0;
	}

	@SuppressWarnings("PMD.ExcessiveMethodLength")
public List<RelatorioMateriaisEstocaveisGrupoCurvaAbcVO> buscarRelatorioMaterialEstocaveisCurvaAbc(Date dtCompetencia){
		
		StringBuffer hql = new StringBuffer(3000);
		hql.append(" select");
		hql.append(" EGR.").append(SceEstoqueGeral.Fields.CLASSIFICACAO_ABC.toString());
		hql.append(" ,MAT.").append(ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString());
		hql.append(" ,MAT.").append(ScoMaterial.Fields.CODIGO.toString());
		hql.append(" ,MAT.").append(ScoMaterial.Fields.NOME.toString());
		hql.append(" ,EAL.").append(SceEstoqueAlmoxarifado.Fields.TEMPO_REPOSICAO.toString());
		hql.append(" ,EAL.").append(SceEstoqueAlmoxarifado.Fields.QTDE_PONTO_PEDIDO.toString());
		hql.append(" ,EAL.").append(SceEstoqueAlmoxarifado.Fields.QTDE_DISPONIVEL.toString()).append(" ,SLC.").append(ScoSolicitacaoDeCompra.Fields.NUMERO.toString()).append(" ,SLC.").append(ScoSolicitacaoDeCompra.Fields.QUANTIDADE_SOLICITADA.toString());
		hql.append(" ,SLC.").append(ScoSolicitacaoDeCompra.Fields.DATA_SOLICITACAO.toString());
		hql.append(" , (");
		hql.append(" SELECT MAX(FSC.").append(ScoFaseSolicitacao.Fields.LICITACAO_NRO.toString());
		hql.append(") from ");
		hql.append(ScoFaseSolicitacao.class.getName()).append(" as FSC");
		hql.append(" WHERE ");
		hql.append(" SLC.").append(ScoSolicitacaoDeCompra.Fields.NUMERO.toString()).append(" = FSC.").append(ScoFaseSolicitacao.Fields.SLC_NUMERO.toString()).append(" AND ");
		hql.append(" FSC.").append(ScoFaseSolicitacao.Fields.LICITACAO_NRO.toString()).append(" is not null AND");
		hql.append(" FSC.").append(ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString()).append(" = :indExclusao )");
		hql.append(" , (");
		hql.append(" SELECT AFN.").append(ScoAutorizacaoForn.Fields.LICITACAO_NUMERO.toString()).append(" ||'/'|| AFN.").append(ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString());
		hql.append(" from ").append(ScoFaseSolicitacao.class.getName()).append(" as FSC, ").append(ScoAutorizacaoForn.class.getName()).append(" as AFN ");
		hql.append(" WHERE ").append(" SLC.").append(ScoSolicitacaoDeCompra.Fields.NUMERO.toString()).append(" = FSC.").append(ScoFaseSolicitacao.Fields.SLC_NUMERO.toString());
		hql.append(" AND FSC.").append(ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString()).append(" = :indExclusao ");
		hql.append(" AND FSC.").append(ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()).append(" is not null ");
		hql.append(" AND AFN.").append(ScoAutorizacaoForn.Fields.NUMERO.toString()).append(" = FSC.").append(ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString());
		hql.append(" AND AFN.").append(ScoAutorizacaoForn.Fields.SITUACAO.toString());
		hql.append(" IN (:situacaoautorizacao) ").append(" ) ").append(" , (");
		//hql.append(",(");
		hql.append(" SELECT AFN.").append(ScoAutorizacaoForn.Fields.SITUACAO.toString());
		hql.append(" from ").append(ScoFaseSolicitacao.class.getName()).append(" as FSC, ");
		hql.append(ScoAutorizacaoForn.class.getName()).append(" as AFN ");
		hql.append(" WHERE ").append(" SLC.").append(ScoSolicitacaoDeCompra.Fields.NUMERO.toString()).append(" = FSC.").append(ScoFaseSolicitacao.Fields.SLC_NUMERO.toString());
		hql.append(" AND FSC.").append(ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString()).append(" = :indExclusao ");
		hql.append(" AND FSC.").append(ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()).append(" is not null ");
		hql.append(" AND AFN.").append(ScoAutorizacaoForn.Fields.NUMERO.toString()).append(" = FSC.").append(ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString());
		hql.append(" AND AFN.").append(ScoAutorizacaoForn.Fields.SITUACAO.toString());
		hql.append(" IN (:situacaoautorizacao) ").append("  ) ").append(" , (");
		hql.append(" SELECT AFN.").append(ScoAutorizacaoForn.Fields.DT_PREV_ENTREGA.toString());
		hql.append(" from ").append(ScoFaseSolicitacao.class.getName()).append(" as FSC, ");
		hql.append(ScoAutorizacaoForn.class.getName()).append(" as AFN ");
		hql.append(" WHERE ").append(" SLC.").append(ScoSolicitacaoDeCompra.Fields.NUMERO.toString()).append(" = FSC.").append(ScoFaseSolicitacao.Fields.SLC_NUMERO.toString());
		hql.append(" AND FSC.").append(ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString()).append(" = :indExclusao ");
		hql.append(" AND FSC.").append(ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()).append(" is not null ");
		hql.append(" AND AFN.").append(ScoAutorizacaoForn.Fields.NUMERO.toString()).append(" = FSC.").append(ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString());
		hql.append(" AND AFN.").append(ScoAutorizacaoForn.Fields.SITUACAO.toString());
		hql.append(" IN (:situacaoautorizacao) ").append("  ) ").append(" from ").append(SceAlmoxarifado.class.getName());
		hql.append(" as ALM, ").append(SceEstoqueGeral.class.getName());
		hql.append(" as EGR, ").append(ScoSolicitacaoDeCompra.class.getName());
		hql.append(" as SLC, ").append(ScoMaterial.class.getName());
		hql.append(" as MAT, ").append(SceEstoqueAlmoxarifado.class.getName());
		hql.append(" as EAL ").append(" where EAL.").append(SceEstoqueAlmoxarifado.Fields.MATERIAL_CODIGO.toString());
		hql.append(" = MAT.").append(ScoMaterial.Fields.CODIGO.toString()).append(" and EAL.");
		hql.append(SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO_SEQ.toString());
		hql.append(" = MAT.");
		hql.append(ScoMaterial.Fields.ALMOXARIFADO_SEQ.toString()).append(" and EAL.");
		hql.append(SceEstoqueAlmoxarifado.Fields.IND_ESTOCAVEL.toString()).append(" = :indEstocavel");
		hql.append(" and EAL.").append(SceEstoqueAlmoxarifado.Fields.NUMERO_FORNECEDOR.toString());
		hql.append(" = :fornecedor");
		hql.append(" and ALM.").append(SceAlmoxarifado.Fields.SEQ.toString());
		hql.append(" = EAL.").append(SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO_SEQ.toString());
		hql.append(" and EGR.");
		hql.append(SceEstoqueGeral.Fields.MAT_CODIGO.toString());
		hql.append(" = MAT.");
		hql.append(ScoMaterial.Fields.CODIGO.toString());
		hql.append(" and EGR.");
		hql.append(SceEstoqueGeral.Fields.CLASSIFICACAO_ABC.toString());
		hql.append(" IN (:classificacaoAbc)");
		hql.append(" and EGR.");
		hql.append(SceEstoqueGeral.Fields.DATA_COMPETENCIA.toString());
		hql.append(" = :dtCompetencia ");
		hql.append(" and SLC.").append(ScoSolicitacaoDeCompra.Fields.MAT_CODIGO.toString()).append(" = MAT.");
		hql.append(ScoMaterial.Fields.CODIGO.toString());
		hql.append(" and SLC.");
		hql.append(ScoSolicitacaoDeCompra.Fields.IND_EXCLUSAO.toString());
		hql.append(" = :indExclusao");
		hql.append(" and SLC.");
		hql.append(ScoSolicitacaoDeCompra.Fields.IND_EFETIVADA.toString());
		hql.append(" = :indEfetivada");


		javax.persistence.Query query = this.createQuery(hql.toString());
		query.setParameter("indEstocavel", Boolean.TRUE);
		query.setParameter("fornecedor", 1);
		query.setParameter("classificacaoAbc", Arrays.asList(DominioClassifABC.A,DominioClassifABC.B,DominioClassifABC.C));
		query.setParameter("dtCompetencia", dtCompetencia);		
		query.setParameter("indExclusao", Boolean.FALSE);
		query.setParameter("indEfetivada", Boolean.FALSE);
		query.setParameter("situacaoautorizacao", Arrays.asList(DominioSituacaoAutorizacaoFornecimento.AE,DominioSituacaoAutorizacaoFornecimento.PA));
		
		
		
		@SuppressWarnings("unchecked")
		List<Object[]> camposLst = (List<Object[]>) query.getResultList();

		List<RelatorioMateriaisEstocaveisGrupoCurvaAbcVO> result = new ArrayList<RelatorioMateriaisEstocaveisGrupoCurvaAbcVO>();

		if (camposLst != null && !camposLst.isEmpty()) {

			SimpleDateFormat sdf_1 = new SimpleDateFormat("dd/MM/yyyy");
			
			for(Object[] obj : camposLst){
				RelatorioMateriaisEstocaveisGrupoCurvaAbcVO vo = new RelatorioMateriaisEstocaveisGrupoCurvaAbcVO();
				vo.setAbc(obj[0].toString());
				vo.setGrupo((obj[1]!=null)?Integer.parseInt(obj[1].toString()):null);
				vo.setCodMat(obj[2].toString());
				vo.setNomeMaterial(obj[3].toString());
				vo.setPrazo(obj[4].toString());
				vo.setpPddo(obj[5].toString());
				vo.setDisp(obj[6].toString());
				vo.setNroSC(obj[7].toString());
				vo.setQtdSC(obj[8].toString());
				vo.setDtSC(sdf_1.format(obj[9]));
				
				if(obj[10] != null) {vo.setCvte(obj[10].toString());	}
				if(obj[11] != null) {vo.setNroAF(obj[11].toString()); }
				if(obj[12] != null) {vo.setStAF(obj[12].toString()); }
				if(obj[13] != null) {vo.setDtPrevEntrega(sdf_1.format(obj[13]));
				
				result.add(vo);
				}
			}
		}
		return result;	
	}

	public List<RelatorioMateriaisEstocaveisGrupoCurvaAbcVO> buscarRelatorioMaterialEstocaveisSemComprasCurvaAbc(Date dtCompetencia){
		StringBuffer hql = new StringBuffer(3000);
		hql.append(" select distinct").append(" EGR.").append(SceEstoqueGeral.Fields.CLASSIFICACAO_ABC.toString());
		hql.append(" ,MAT.").append(ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString());
		hql.append(" ,MAT.").append(ScoMaterial.Fields.CODIGO.toString());
		hql.append(" ,MAT.").append(ScoMaterial.Fields.NOME.toString());
		hql.append(" ,EAL.").append(SceEstoqueAlmoxarifado.Fields.TEMPO_REPOSICAO.toString());
		hql.append(" ,EAL.").append(SceEstoqueAlmoxarifado.Fields.QTDE_PONTO_PEDIDO.toString());
		hql.append(" ,EAL.").append(SceEstoqueAlmoxarifado.Fields.QTDE_DISPONIVEL.toString());
		hql.append(" from ").append(SceAlmoxarifado.class.getName()).append(" as ALM, ").append(SceEstoqueGeral.class.getName()).append(" as EGR, ");
		hql.append(ScoMaterial.class.getName()).append(" as MAT, ").append(SceEstoqueAlmoxarifado.class.getName()).append(" as EAL ");
		hql.append(" where EAL.").append(SceEstoqueAlmoxarifado.Fields.MATERIAL_CODIGO.toString()).append(" = MAT.").append(ScoMaterial.Fields.CODIGO.toString());
		hql.append(" and EAL.").append(SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO_SEQ.toString()).append(" = MAT.").append(ScoMaterial.Fields.ALMOXARIFADO_SEQ.toString());
		hql.append(" and EAL.").append(SceEstoqueAlmoxarifado.Fields.IND_ESTOCAVEL.toString()).append(" = :indEstocavel");
		hql.append(" and EAL.").append(SceEstoqueAlmoxarifado.Fields.NUMERO_FORNECEDOR.toString()).append(" = :fornecedor");
		hql.append(" and ALM.").append(SceAlmoxarifado.Fields.SEQ.toString()).append(" = EAL.").append(SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO_SEQ.toString());
		hql.append(" and EGR.").append(SceEstoqueGeral.Fields.MAT_CODIGO.toString()).append(" = MAT.").append(ScoMaterial.Fields.CODIGO.toString());
		hql.append(" and EGR.").append(SceEstoqueGeral.Fields.CLASSIFICACAO_ABC.toString()).append(" IN (:classificacaoAbc)");
		hql.append(" and EGR.").append(SceEstoqueGeral.Fields.DATA_COMPETENCIA.toString()).append(" = :dtCompetencia ");
		hql.append("and (EAL.qtdeDisponivel + EAL.qtdeBloqueada) <= EAL.qtdePontoPedido");

		javax.persistence.Query query = this.createQuery(hql.toString());

		query.setParameter("indEstocavel", Boolean.TRUE);
		query.setParameter("fornecedor", 1);
		query.setParameter("classificacaoAbc", Arrays.asList(DominioClassifABC.A,DominioClassifABC.B,DominioClassifABC.C));
		query.setParameter("dtCompetencia", dtCompetencia);		

		@SuppressWarnings("unchecked")
		List<Object[]> camposLst = (List<Object[]>) query.getResultList();
		List<RelatorioMateriaisEstocaveisGrupoCurvaAbcVO> result = new ArrayList<RelatorioMateriaisEstocaveisGrupoCurvaAbcVO>();

		if (camposLst != null && !camposLst.isEmpty()) {
			for(Object[] obj : camposLst){
				RelatorioMateriaisEstocaveisGrupoCurvaAbcVO vo = new RelatorioMateriaisEstocaveisGrupoCurvaAbcVO();
				vo.setAbc(obj[0].toString());
				vo.setGrupo((obj[1]!=null)?Integer.parseInt(obj[1].toString()):null);
				vo.setCodMat(obj[2].toString());
				vo.setNomeMaterial(obj[3].toString());
				if(obj[4]!=null){
					vo.setPrazo(obj[4].toString());
				}
				if(obj[5]!=null){
					vo.setpPddo(obj[5].toString());
				}
				if(obj[6]!=null){
					vo.setDisp(obj[6].toString());
				}
				result.add(vo);
			}
		}
		return result;	
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
	/**
	 * obterCriteriaLoteSolicitacaoCompras
	 * 
	 * @param filtroPesquisa - VO para transporte dos filtros de pesquisa
	 * @param servidorLogado - indica o servidor que está usando a tela
	 * @param verificaCentroCustoAutorizacao - indica se deve checar o CC e a característica para autorização
	 * @param filtraTipoPontoParada - filtra determinados tipos de ponto de parada, utilizado na liberação
	 * @param verificaCentroCustoGeracao - indica se deve checar o CC e a característica para geração de sc
	 * @param incluiGppg - indica se deve incluir o centro de custo do FIPE e o PPS do GPPG
	 * @return
	 */
	@SuppressWarnings({"PMD.NPathComplexity", "PMD.ExcessiveMethodLength"})
	private DetachedCriteria obterCriteriaLoteSolicitacaoCompras(PesqLoteSolCompVO filtroPesquisa, RapServidores servidorLogado, 
			Boolean verificaCentroCustoAutorizacao, Boolean filtraTipoPontoParada,
			Boolean verificaCentroCustoGeracao, Boolean incluiGppg, Boolean verificaFases, Boolean verificaPontoServidor) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoSolicitacaoDeCompra.class, "SCO");
			
		criteria.createAlias("SCO."+ScoSolicitacaoDeCompra.Fields.MODALIDADE_LICITACAO.toString(), "MDL", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SCO."+ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MTR");
		criteria.createAlias("SCO."+ScoSolicitacaoDeCompra.Fields.UNIDADE_MEDIDA.toString(), "UND");
		criteria.createAlias("SCO."+ScoSolicitacaoDeCompra.Fields.PONTO_PARADA_SOLICITACAO.toString(), "PPS", JoinType.INNER_JOIN);
		criteria.createAlias("SCO."+ScoSolicitacaoDeCompra.Fields.CENTRO_CUSTO.toString(), "CCT");
		criteria.createAlias("SCO."+ScoSolicitacaoDeCompra.Fields.CENTRO_CUSTO_APLICADA.toString(), "CCA");
		criteria.createAlias("SCO."+ScoSolicitacaoDeCompra.Fields.SERVIDOR.toString(), "SRV");
		criteria.createAlias("SRV."+RapServidores.Fields.PESSOA_FISICA.toString(), "PSF");
	
		FccCentroCustos ccFipe = this.obterCcFipe();
		Boolean permiteAutorizarGppg = this.getScoCaracteristicaUsuarioCentroCustoDAO().verificarAutorizacaoGppg(servidorLogado);
		
		if (filtroPesquisa.getIndContrato() != null && filtroPesquisa.getIndContrato().isSim()) {
			DetachedCriteria subQueryEal = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class, "EAL");
			subQueryEal.setProjection(Projections.property("EAL."+SceEstoqueAlmoxarifado.Fields.NRO_SOLICITACAO_COMPRA.toString()));
			criteria.add(Subqueries.propertyIn("SCO."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), subQueryEal));
		}
		
		if (filtroPesquisa.getNumeroSolicitacaoCompra() == null && filtroPesquisa.isExibirScSolicitante()) {
			criteria.add(Restrictions.ne(ScoSolicitacaoDeCompra.Fields.PPS_CODIGO_LOC_PROXIMA.toString(), this.getScoPontoParadaSolicitacaoDAO().obterPontoParadaPorTipo(DominioTipoPontoParada.CH).getCodigo()));
			criteria.add(Restrictions.isNull(ScoSolicitacaoDeCompra.Fields.DATA_AUTORIZACAO.toString()));
			criteria.add(Restrictions.isNull(ScoSolicitacaoDeCompra.Fields.SER_MATRICULA_AUTORIZADA.toString()));
		} else {
			if (filtroPesquisa.getSolicitacaoAutorizada() == null || !filtroPesquisa.getSolicitacaoAutorizada().isSim()) {
				if (filtroPesquisa.getPontoParada() != null) {
					if (!incluiGppg) {
						criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoDeCompra.Fields.LOCALIZACAO_PONTO_PARADA_ATUAL.toString(), filtroPesquisa.getPontoParada()));
					} else {
						if (permiteAutorizarGppg) {
							criteria.add(Restrictions.or(
									Restrictions.and(Restrictions.eq("SCO."+ ScoSolicitacaoDeCompra.Fields.LOCALIZACAO_PONTO_PARADA_ATUAL.toString(),
													this.getScoPontoParadaSolicitacaoDAO().obterPontoParadaPorTipo(DominioTipoPontoParada.GP)),
													Restrictions.eq("CCA."+FccCentroCustos.Fields.CODIGO.toString(), ccFipe.getCodigo())), 
									Restrictions.eq("SCO."+ScoSolicitacaoDeCompra.Fields.LOCALIZACAO_PONTO_PARADA_ATUAL.toString(), 
											filtroPesquisa.getPontoParada())));
						} else {
							criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoDeCompra.Fields.LOCALIZACAO_PONTO_PARADA_ATUAL.toString(), filtroPesquisa.getPontoParada()));
						}
					}
				} else {
					if (incluiGppg) {
						if (permiteAutorizarGppg) {
							Restrictions.and(
									Restrictions.eq("SCO."+ ScoSolicitacaoDeCompra.Fields.LOCALIZACAO_PONTO_PARADA_ATUAL.toString(),
											this.getScoPontoParadaSolicitacaoDAO().obterPontoParadaPorTipo(DominioTipoPontoParada.GP)),
									Restrictions.eq("CCA."+FccCentroCustos.Fields.CODIGO.toString(), ccFipe.getCodigo()));
						}
					}				
				}
			}
		}
		
		if (filtroPesquisa.getNumeroSolicitacaoCompra() != null) {
			criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), filtroPesquisa.getNumeroSolicitacaoCompra()));
		}
	
		if (filtroPesquisa.getDataSolicitacaoCompra() != null) {
			SimpleDateFormat formatador = new SimpleDateFormat("dd/MM/yyyy");
			final String sqlRestrictionToChar = "TO_CHAR(this_.DT_SOLICITACAO,'dd/MM/yyyy') = ?";
			criteria.add(Restrictions.sqlRestriction(sqlRestrictionToChar,
					formatador.format(filtroPesquisa
							.getDataSolicitacaoCompra()),
							StringType.INSTANCE));
		} else {
			if (filtroPesquisa.getDataInicioSolicitacaoCompra() != null) {
				criteria.add(Restrictions.ge(ScoSolicitacaoDeCompra.Fields.DATA_SOLICITACAO.toString(), filtroPesquisa
						.getDataInicioSolicitacaoCompra()));
			}

			if (filtroPesquisa.getDataFimSolicitacaoCompra() != null) {
				SimpleDateFormat formatadorTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				SimpleDateFormat formatadorDia = new SimpleDateFormat("dd/MM/yyyy");

				try {
					criteria.add(Restrictions.le(ScoSolicitacaoDeCompra.Fields.DATA_SOLICITACAO.toString(), 
							formatadorTime.parse(formatadorDia.format(filtroPesquisa.getDataFimSolicitacaoCompra())+" 23:59:59")));
				} catch (ParseException e) {
					LOG.error("Erro ao executar chamada"+ e.getMessage());
				}
			}
		}

		if (filtroPesquisa.getNumeroSolicitacaoCompra() == null && 
				filtroPesquisa.getSolicitacaoAutorizada() != null && filtroPesquisa.getSolicitacaoAutorizada().isSim()) {
			criteria.add(Restrictions.isNotNull(ScoSolicitacaoDeCompra.Fields.DATA_AUTORIZACAO.toString()));
			criteria.add(Restrictions.isNotNull(ScoSolicitacaoDeCompra.Fields.SER_MATRICULA_AUTORIZADA.toString()));
		}
		
		if (filtroPesquisa.getDataInicioAutorizacaoSolicitacaoCompra() != null) {
			criteria.add(Restrictions.ge(ScoSolicitacaoDeCompra.Fields.DATA_AUTORIZACAO.toString(), filtroPesquisa
					.getDataInicioAutorizacaoSolicitacaoCompra()));
		}

		if (filtroPesquisa.getDataFimAutorizacaoSolicitacaoCompra() != null) {
			SimpleDateFormat formatadorTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			SimpleDateFormat formatadorDia = new SimpleDateFormat("dd/MM/yyyy");

			try {
				criteria.add(Restrictions.le(ScoSolicitacaoDeCompra.Fields.DATA_AUTORIZACAO.toString(), 
						formatadorTime.parse(formatadorDia.format(filtroPesquisa.getDataFimAutorizacaoSolicitacaoCompra())+" 23:59:59")));
			} catch (ParseException e) {
				LOG.error("Erro ao executar chamada"+ e.getMessage());
			}
		}

		//  #30927 - estória #5165 - Consultar solicitações de compras a autoriza
		// Data Inicial	Data Final	Critério
		// ---------------------------------
		// null	        null	    dt_analise is null or dt_analise <= data_atual
		// null	        xx/xx/xxxx	dt_analise is null or dt_analise <= data final
		// xx/xx/xxxx	null	    dt_analise >= data inicial
		// xx/xx/xxxx	xx/xx/xxxx	data analise >= data inicial && dt_analise <= data final
		
		if (filtroPesquisa.getDataInicioAnaliseSolicitacaoCompra() != null){
			SimpleDateFormat formatadorTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			SimpleDateFormat formatadorDia = new SimpleDateFormat("dd/MM/yyyy");

			criteria.add(Restrictions.ge(ScoSolicitacaoDeCompra.Fields.DATA_ANALISE.toString(), filtroPesquisa.getDataInicioAnaliseSolicitacaoCompra()));
	
			if (filtroPesquisa.getDataFimAnaliseSolicitacaoCompra() != null) {
				try {
					criteria.add(Restrictions.le(ScoSolicitacaoDeCompra.Fields.DATA_ANALISE.toString(), 
							formatadorTime.parse(formatadorDia.format(filtroPesquisa.getDataFimAnaliseSolicitacaoCompra())+" 23:59:59")));
				} catch (ParseException e) {
					LOG.error("Erro ao executar chamada"+ e.getMessage());
				}
			}
		} else {
			if (filtroPesquisa.getDataFimAnaliseSolicitacaoCompra() != null) {			

				SimpleDateFormat formatadorTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				SimpleDateFormat formatadorDia = new SimpleDateFormat("dd/MM/yyyy");
				
				try {
					criteria.add(Restrictions.or(Restrictions.le(ScoSolicitacaoDeCompra.Fields.DATA_ANALISE.toString(), 
		                                                         formatadorTime.parse(formatadorDia.format(filtroPesquisa.getDataFimAnaliseSolicitacaoCompra())+" 23:59:59")),
	                                            Restrictions.isNull(ScoSolicitacaoDeCompra.Fields.DATA_ANALISE.toString())));
				} catch (ParseException e) {
					LOG.error("Erro ao executar chamada"+ e.getMessage());
				}	
			}
		}
		
		if (filtroPesquisa.getRepAutomatica() != null) {
			criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoDeCompra.Fields.GERACAO_AUTOMATICA.toString(), 
					(filtroPesquisa.getRepAutomatica() == DominioSimNao.S) ? Boolean.TRUE : Boolean.FALSE));
		}

		if (filtroPesquisa.getCentroCustoSolicitante() != null) {
			criteria.add(Restrictions.eq("CCT."+FccCentroCustos.Fields.CODIGO.toString(), filtroPesquisa.getCentroCustoSolicitante().getCodigo()));
		}

		if (filtroPesquisa.getCentroCustoAplicacao() != null) {
			criteria.add(Restrictions.eq("CCA."+FccCentroCustos.Fields.CODIGO.toString(), filtroPesquisa.getCentroCustoAplicacao().getCodigo()));
		}

		if (filtroPesquisa.getMaterial() != null) {
			criteria.add(Restrictions.eq("MTR."+ScoMaterial.Fields.CODIGO.toString(), filtroPesquisa.getMaterial().getCodigo()));
		}
		
		if (filtroPesquisa.getSolicitacaoPrioritaria() != null) {
			criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoDeCompra.Fields.PRIORIDADE.toString(), 
					(filtroPesquisa.getSolicitacaoPrioritaria() == DominioSimNao.S) ? Boolean.TRUE : Boolean.FALSE));
		}
		
		if (filtroPesquisa.getSolicitacaoUrgente() != null) {
			criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoDeCompra.Fields.IND_URGENTE.toString(), 
					(filtroPesquisa.getSolicitacaoUrgente() == DominioSimNao.S ? true : false)));
		}
		
		if (filtroPesquisa.getSolicitacaoDevolvida() != null) {
			criteria.add(Restrictions.eq("SCO."	+ ScoSolicitacaoDeCompra.Fields.IND_DEVOLUCAO.toString(),
					(filtroPesquisa.getSolicitacaoDevolvida() == DominioSimNao.S ? true : false)));
		}
		
		if (verificaCentroCustoAutorizacao) {
			// como este filtro so existe na tela de autorizacao...
			if (filtroPesquisa.getNumeroSolicitacaoCompra() == null && 
					(filtroPesquisa.getSolicitacaoAutorizada() == null || 
					!filtroPesquisa.getSolicitacaoAutorizada().isSim())) {
					criteria.add(Restrictions.isNull("SCO."+ScoSolicitacaoDeCompra.Fields.DATA_AUTORIZACAO.toString()));
					criteria.add(Restrictions.isNull("SCO."+ScoSolicitacaoDeCompra.Fields.SER_MATRICULA_AUTORIZADA.toString()));
			}
			if (filtroPesquisa.getSolicitacaoExcluida() != null && filtroPesquisa.getNumeroSolicitacaoCompra() == null) {
				criteria.add(Restrictions.eq("SCO."	+ ScoSolicitacaoDeCompra.Fields.IND_EXCLUSAO.toString(),
						(filtroPesquisa.getSolicitacaoExcluida() == DominioSimNao.S ? true : false)));
			}
		} else {
			if (filtroPesquisa.getNumeroSolicitacaoCompra() == null){
				criteria.add(Restrictions.eq("SCO."
						+ ScoSolicitacaoDeCompra.Fields.IND_EXCLUSAO.toString(), false));
			}
		}
		
		if (filtroPesquisa.getGrupoMaterial() != null) {
			criteria.add(Restrictions.eq("MTR."+ ScoMaterial.Fields.GRUPO_MATERIAL.toString(),	filtroPesquisa.getGrupoMaterial()));
		}
		
		if (filtroPesquisa.getMatEstocavel() != null) {
			criteria.add(Restrictions.eq("MTR."+ ScoMaterial.Fields.IND_ESTOCAVEL.toString(),filtroPesquisa.getMatEstocavel().isSim()));
		}

		if (filtroPesquisa.getVerbaGestao() != null) {
			criteria.add(Restrictions.eq("SCO."+ ScoSolicitacaoDeCompra.Fields.VERBA_GESTAO.toString(),filtroPesquisa.getVerbaGestao()));
		}
		
		if (filtroPesquisa.getFiltroComprador() != null) {
			criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoDeCompra.Fields.MATRICULA_SERVIDOR_COMPRADOR.toString(), 
					filtroPesquisa.getFiltroComprador().getId().getMatricula()));
			criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoDeCompra.Fields.CODIGO_VINCULO_SERVIDOR_COMPRADOR.toString(), 
					filtroPesquisa.getFiltroComprador().getId().getVinCodigo()));
		}
		
		if (filtroPesquisa.getFiltroVazio() != null && filtroPesquisa.getFiltroVazio()) {
			criteria.add(Restrictions.or(Restrictions.le(ScoSolicitacaoDeCompra.Fields.DATA_ANALISE.toString(), new Date()), 
							Restrictions.isNull(ScoSolicitacaoDeCompra.Fields.DATA_ANALISE.toString())));
		}
		
		if (verificaCentroCustoAutorizacao) {
			List<Integer> listaCCRetorno = getCentroCustoFacade().getCodigosCentrosCusto(getCentroCustoFacade().pesquisarCentroCustoUsuarioAutorizacaoSC());
			if (listaCCRetorno != null && listaCCRetorno.size() > 0) {
				if (!incluiGppg) { 
					criteria.add(Restrictions.in("CCT."+FccCentroCustos.Fields.CODIGO.toString(), listaCCRetorno));	
				} else {
					if (permiteAutorizarGppg) {
						criteria.add(Restrictions.or(
								Restrictions.in("CCT."+FccCentroCustos.Fields.CODIGO.toString(), listaCCRetorno), 
								Restrictions.and(Restrictions.eq("CCA."+FccCentroCustos.Fields.CODIGO.toString(), ccFipe.getCodigo()),
										Restrictions.eq("SCO."+ ScoSolicitacaoDeCompra.Fields.LOCALIZACAO_PONTO_PARADA_ATUAL.toString(),
												this.getScoPontoParadaSolicitacaoDAO().obterPontoParadaPorTipo(DominioTipoPontoParada.GP)))));
					} else {
						criteria.add(Restrictions.in("CCT."+FccCentroCustos.Fields.CODIGO.toString(), listaCCRetorno)); 
					}
				}
			} else {
				if (!incluiGppg) {
					// se não existir nenhum centro de custo que o servidor possui caracteristica de autorizacao
					// deve fazer uma condicao para nao retornar nada na query
					criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), -1));
				} else {
					if (permiteAutorizarGppg) {
						criteria.add(Restrictions.and(Restrictions.eq("CCA."+FccCentroCustos.Fields.CODIGO.toString(), ccFipe.getCodigo()),
								Restrictions.eq("SCO."+ ScoSolicitacaoDeCompra.Fields.LOCALIZACAO_PONTO_PARADA_ATUAL.toString(),
										this.getScoPontoParadaSolicitacaoDAO().obterPontoParadaPorTipo(DominioTipoPontoParada.GP))));
					} else {
						criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), -1));
					}
				}
			}
		}
		
		if (verificaCentroCustoGeracao) {
			List<Integer> listaCCRetorno = getCentroCustoFacade().getCodigosCentrosCusto(getCentroCustoFacade().pesquisarCentroCustoUsuarioGerarSC());
			if (listaCCRetorno.size() > 0) {
				criteria.add(Restrictions.in("CCT."+FccCentroCustos.Fields.CODIGO.toString(), listaCCRetorno));	
			} else {
				// se não existir nenhum centro de custo que o servidor possui caracteristica de geração deve fazer uma condicao para nao retornar nada na query
				criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), -1));
			}
			
		}
		// pega os pontos de parada que o servidor tem direito
		DetachedCriteria subQueryPontoServidor = DetachedCriteria.forClass(ScoPontoServidor.class);
		ProjectionList projectionListSubQueryPontoServidor = Projections.projectionList()
		.add(Projections.property(ScoPontoServidor.Fields.PONTO_PARADA_SOLICITACAO.toString()));
		subQueryPontoServidor.setProjection(projectionListSubQueryPontoServidor);
		subQueryPontoServidor.add(Restrictions.eq(ScoPontoServidor.Fields.SERVIDOR.toString(), servidorLogado));

		if (filtraTipoPontoParada) {
			subQueryPontoServidor.createAlias(ScoPontoServidor.Fields.PONTO_PARADA_SOLICITACAO.toString(), "PPSU", JoinType.INNER_JOIN);
			
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
							Restrictions.and(Restrictions.isNotNull("PPSU." + ScoPontoParadaSolicitacao.Fields.TIPO.toString()), 
									Restrictions.not(Restrictions.in("PPSU." + ScoPontoParadaSolicitacao.Fields.TIPO.toString(), listDominioTipos))), 
							Restrictions.isNull("PPSU." + ScoPontoParadaSolicitacao.Fields.TIPO.toString())));
		}
		
		if (verificaFases) {
			DetachedCriteria subQueryFasesSolicitacoes = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "SLC");
			ProjectionList projectionListSubQueryFasesSolicitacoes = Projections.projectionList()
			.add(Projections.property("SLC."+ScoFaseSolicitacao.Fields.SLC_NUMERO.toString()));
			subQueryFasesSolicitacoes.setProjection(projectionListSubQueryFasesSolicitacoes);
			subQueryFasesSolicitacoes.add(Restrictions.eqProperty("SLC."+ScoFaseSolicitacao.Fields.SLC_NUMERO.toString(), "SCO."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString()));
			subQueryFasesSolicitacoes.add(Restrictions.eq("SLC."+ScoFaseSolicitacao.Fields.TIPO.toString(), DominioTipoFaseSolicitacao.C));
			subQueryFasesSolicitacoes.add(Restrictions.eq("SLC."+ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
			subQueryFasesSolicitacoes.add(Restrictions.isNull("SLC."+ScoFaseSolicitacao.Fields.LCT_NUMERO.toString()));
					
			criteria.add(Subqueries.propertyNotIn("SCO."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), subQueryFasesSolicitacoes));
		}
		
		if (verificaPontoServidor) {
			criteria.add(Subqueries.propertyIn("SCO." + ScoSolicitacaoDeCompra.Fields.LOCALIZACAO_PONTO_PARADA_ATUAL.toString(), subQueryPontoServidor));
		}
		
		if (filtroPesquisa.getNumeroSolicitacaoCompra() == null && DominioSimNao.S.equals(filtroPesquisa.getSolicitacaoAutorizada())) {
			final String SPPS = "SPPS";
			criteria.createAlias("SCO."+ ScoSolicitacaoDeCompra.Fields.LOCALIZACAO_PONTO_PARADA_ATUAL.toString(),
					SPPS).add(Restrictions.not(Restrictions.in(SPPS + "."+ ScoPontoParadaSolicitacao.Fields.TIPO.toString(),
							new Object[] { DominioTipoPontoParada.SL,DominioTipoPontoParada.CH })));
		} else {
			criteria.createAlias("SCO."+ScoSolicitacaoDeCompra.Fields.LOCALIZACAO_PONTO_PARADA_ATUAL.toString(), "PPP");
		}
		
		return criteria;

	}
	
	/**
	 * Retorna lista de solicitações de compras conforme preenchimento do VO filtroPesquisa.
	 * Utilizado na tela de encaminhar solicitações de compras
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param filtroPesquisa
	 * @param servidor
	 * @return List
	 */
	public List<ScoSolicitacaoDeCompra> pesquisarLoteSolicitacaoCompras(Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, PesqLoteSolCompVO filtroPesquisa, RapServidores servidorLogado) {
				return this.executeCriteria(
				this.obterCriteriaLoteSolicitacaoCompras(filtroPesquisa, servidorLogado, false, false, true, false, true, true), 
				firstResult, maxResults, orderProperty,  asc);
	}
	
	/**
	 * Retorna a quantidade de solicitações de compras conforme preenchimento do VO filtroPesquisa.
	 * Utilizado na tela de encaminhar solicitações de compras
	 * @param filtroPesquisa
	 * @param servidorLogado
	 * @return Integer
	 */
	public Long countLoteSolicitacaoCompras (PesqLoteSolCompVO filtroPesquisa, RapServidores servidorLogado) {
		return executeCriteriaCount(this.obterCriteriaLoteSolicitacaoCompras(filtroPesquisa, servidorLogado, false, false, true, false, true, true));
	}

	/**
	 * Retorna lista de solicitações de compras conforme regras para autorização da SC.
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param filtroPesquisa
	 * @param servidorLogado
	 * @return List
	 */
	public List<ScoSolicitacaoDeCompra> pesquisarSolicitacaoComprasAutorizarSc(Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, PesqLoteSolCompVO filtroPesquisa, RapServidores servidorLogado) {
		DetachedCriteria criteria = obterCriteriaLoteSolicitacaoCompras(
				filtroPesquisa, servidorLogado, true, false, false, true, true,false);

		if (orderProperty == null) {
			// Melhoria em Desenvolvimento #31712
			if (filtroPesquisa.getSolicitacaoAutorizada() != null && filtroPesquisa.getSolicitacaoAutorizada().isSim()) {
				criteria.addOrder(Order.desc(ScoSolicitacaoDeCompra.Fields.NUMERO.toString()));
			} else {
				criteria.addOrder(Order.desc(ScoSolicitacaoDeCompra.Fields.IND_URGENTE.toString()))
					.addOrder(Order.desc(ScoSolicitacaoDeCompra.Fields.PRIORIDADE.toString()))
					.addOrder(Order.desc(ScoSolicitacaoDeCompra.Fields.NUMERO.toString()));
			}
		} 
		
		List<ScoSolicitacaoDeCompra> listaSC = this.executeCriteria(criteria, firstResult, maxResults, orderProperty,  asc);
		
		return listaSC;
	}
	
	/**
	 * Retorna solicitações de compras conforme regras da tela de solicitacao de compras para liberação.
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param filtroPesquisa
	 * @param servidorLogado
	 * @return List
	 */
	public List<ScoSolicitacaoDeCompra> pesquisarSolicitacaoComprasLiberacaoSc(Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, PesqLoteSolCompVO filtroPesquisa, RapServidores servidorLogado) {
		DetachedCriteria criteria = obterCriteriaLoteSolicitacaoCompras(
				filtroPesquisa, servidorLogado, false, true, false, false, true, true)				
				.addOrder(Order.desc(ScoSolicitacaoDeCompra.Fields.IND_URGENTE.toString()))
				.addOrder(Order.desc(ScoSolicitacaoDeCompra.Fields.PRIORIDADE.toString()))
				.addOrder(Order.desc(ScoSolicitacaoDeCompra.Fields.NUMERO.toString()));
						
		return this.executeCriteria(criteria, firstResult, maxResults, orderProperty,  asc);
	}
	
	/**
	 * Retorna lista de solicitações de compras conforme regras de visualização do planejamento
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @param filtroPesquisa
	 * @param servidorLogado
	 * @return List
	 */
	public List<ScoSolicitacaoDeCompra> pesquisarSolicitacaoComprasPlanejamentoSc(Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, PesqLoteSolCompVO filtroPesquisa, RapServidores servidorLogado) {

		return this.executeCriteria(
				this.obterCriteriaLoteSolicitacaoCompras(filtroPesquisa, servidorLogado, false, false, false, false, false, false).addOrder(
						Order.desc(ScoSolicitacaoDeCompra.Fields.IND_URGENTE.toString())).addOrder(
								Order.desc(ScoSolicitacaoDeCompra.Fields.DATA_AUTORIZACAO.toString())).addOrder(
										Order.asc("MTR."+ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString())).addOrder(
												Order.asc(ScoSolicitacaoDeCompra.Fields.NUMERO.toString())), 
				firstResult, maxResults, orderProperty,  asc);
	}
	
	/**
	 * Retorna quantidade de solicitações de compras conforme regras da tela de solicitacao de compras para liberação.
	 * @param filtroPesquisa
	 * @param servidorLogado
	 * @return
	 */
	public Long countSolicitacaoComprasLiberacaoSc(PesqLoteSolCompVO filtroPesquisa, RapServidores servidorLogado) {
		return executeCriteriaCount(this.obterCriteriaLoteSolicitacaoCompras(filtroPesquisa, servidorLogado, false, true, false, false, true, true));
	}
	
	/**
	 * Retorna quantidade de solicitações de compras conforme regras para autorização da SC.
	 * @param filtroPesquisa
	 * @param servidorLogado
	 * @return
	 */
	public Long countSolicitacaoComprasAutorizarSc(PesqLoteSolCompVO filtroPesquisa, RapServidores servidorLogado) {
		return executeCriteriaCount(this.obterCriteriaLoteSolicitacaoCompras(filtroPesquisa, servidorLogado, true, false, false, true, true, false));
	}
		
	/**
	 * Retorna quantidade de solicitações de compras conforme regras da tela de planejamento
	 * @param filtroPesquisa
	 * @param servidorLogado
	 * @return
	 */
	public Long countSolicitacaoComprasPlanejamentoSc(PesqLoteSolCompVO filtroPesquisa, RapServidores servidorLogado) {
		return executeCriteriaCount(this.obterCriteriaLoteSolicitacaoCompras(filtroPesquisa, servidorLogado, false, false, false, false, false, false));
	}
		
	protected ICentroCustoFacade getCentroCustoFacade() {
		return aICentroCustoFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return aIParametroFacade;
	}
	
	private ScoPontoParadaSolicitacaoDAO getScoPontoParadaSolicitacaoDAO() {
		return aScoPontoParadaSolicitacaoDAO;
	}
	
	private ScoCaracteristicaUsuarioCentroCustoDAO getScoCaracteristicaUsuarioCentroCustoDAO(){
		return scoCaracteristicaUsuarioCentroCustoDAO;
	}
	/**
	 * Pesquisa solicitacao de compras com PPS_CODIGO igual ao informado.
	 * @param codigo
	 * @author dilceia.alves
	 * @since 31/10/2012
	 */
	public Long pesquisarSolicitacaoDeCompraPorPpsCodigoCount(Short codigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoSolicitacaoDeCompra.class);
		criteria.add(Restrictions.eq(ScoSolicitacaoDeCompra.Fields.PPS_CODIGO.toString(), codigo));
		return this.executeCriteriaCount(criteria);
	}
	
	/**
	 * Pesquisa solicitacao de compras com PPS_CODIGO_LOC_PROXIMA igual ao informado.
	 * @param codigo
	 * @author dilceia.alves
	 * @since 31/10/2012
	 */
	public Long pesquisarSolicitacaoDeCompraPorPpsCodigoLocProximaCount(Short codigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoSolicitacaoDeCompra.class);
		criteria.add(Restrictions.eq(ScoSolicitacaoDeCompra.Fields.PPS_CODIGO_LOC_PROXIMA.toString(), codigo));
		return this.executeCriteriaCount(criteria);
	}
	
	/**
	 * Verifica se a solicitacao de compra esta vinculada à alguma AF
	 * @param solicitacao
	 * @return Boolean
	 */
	public Boolean verificarAutorizacaoFornecimentoVinculada(ScoSolicitacaoDeCompra solicitacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoSolicitacaoDeCompra.class, "SLC");
		if (solicitacao == null) {
			return false;
		}
		criteria.add(Restrictions.eq("SLC."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), solicitacao.getNumero()));
		criteria.add(Restrictions.gt("SLC."+ScoSolicitacaoDeCompra.Fields.QTDE_REFORCO.toString(), Long.valueOf("0")));
		DetachedCriteria criteriaFases = DetachedCriteria.forClass(ScoFaseSolicitacao.class, "FSC");
		criteriaFases.add(Restrictions.eq("FSC."+ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		criteriaFases.add(Restrictions.isNotNull("FSC."+ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()));
		ProjectionList projectionListFases = Projections.projectionList()
				.add(Projections.property(ScoFaseSolicitacao.Fields.SLC_NUMERO.toString()));		
		criteriaFases.setProjection(projectionListFases);
		DetachedCriteria criteriaEstoque = DetachedCriteria.forClass(SceEstoqueAlmoxarifado.class, "EAL");
		ProjectionList projectionEstoque = Projections.projectionList()
				.add(Projections.property(SceEstoqueAlmoxarifado.Fields.NRO_SOLICITACAO_COMPRA.toString()));		
		criteriaEstoque.setProjection(projectionEstoque);
		criteria.add(Subqueries.propertyIn("SLC."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), criteriaFases));
		criteria.add(Subqueries.propertyIn("SLC."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), criteriaEstoque));
		return this.executeCriteriaCount(criteria).intValue() > 0;									
	}
	
	public List<SolCompraVO> listarSolicitacoesDeComprasComprador(Integer firstResult, Integer maxResult, String order, Boolean asc, 
			List<FccCentroCustos> listaCentroCustos, FccCentroCustos centroCusto, 
			FccCentroCustos centroCustoAplicada, ScoMaterial material, Date dtSolInicial, Date dtSolFinal, 
			ScoPontoParadaSolicitacao ppsSolicitante, ScoPontoParadaSolicitacao ppsAutorizacao, 
			ScoSolicitacaoDeCompra solicitacaoDeCompra, ScoPontoParadaSolicitacao pontoParadaAtual, RapServidores servidorCompra, Boolean isPerfilGeral) {
		//PERFIL COMPRADOR
		DetachedCriteria criteria = this.obterCriteriaSolicitacoesCompraComprador(listaCentroCustos, centroCusto, centroCustoAplicada, material, 
											dtSolInicial, dtSolFinal, ppsSolicitante, ppsAutorizacao, solicitacaoDeCompra,
											pontoParadaAtual, servidorCompra, isPerfilGeral);
		criteria.setResultTransformer(Transformers.aliasToBean(SolCompraVO.class));
		if (order == null){
			criteria.addOrder(Order.desc(ScoSolicitacaoDeCompra.Fields.IND_URGENTE.toString()));
			criteria.addOrder(Order.desc(ScoSolicitacaoDeCompra.Fields.PRIORIDADE.toString()));
			criteria.addOrder(Order.desc(ScoSolicitacaoDeCompra.Fields.DATA_SOLICITACAO.toString()));
			criteria.addOrder(Order.asc(ScoSolicitacaoDeCompra.Fields.NUMERO.toString()));
		}
		return this.executeCriteria(criteria, firstResult, maxResult, order,  asc);
	}
	
	public Long listarSolicitacoesDeComprasCompradorCount(List<FccCentroCustos> listaCentroCustos, FccCentroCustos centroCusto, 
			FccCentroCustos centroCustoAplicada, ScoMaterial material, Date dtSolInicial , 
			Date dtSolFinal, ScoPontoParadaSolicitacao ppsSolicitante, ScoPontoParadaSolicitacao ppsAutorizacao, 
			ScoSolicitacaoDeCompra solicitacaoDeCompra, ScoPontoParadaSolicitacao pontoParadaAtual, RapServidores servidorCompra, Boolean isPerfilGeral) {
		//PERFIL COMPRADOR
			DetachedCriteria criteria = this.obterCriteriaSolicitacoesCompraComprador(listaCentroCustos, centroCusto, centroCustoAplicada, material, 
											dtSolInicial, dtSolFinal, ppsSolicitante, ppsAutorizacao,  solicitacaoDeCompra,
											pontoParadaAtual, servidorCompra, isPerfilGeral);		
		return this.executeCriteriaCount(criteria);
	}
	
private ProjectionList getProjectionListSc(DetachedCriteria criteria) {
		ProjectionList p = Projections.projectionList();

		p.add(Projections.property("SCO." + ScoSolicitacaoDeCompra.Fields.NUMERO.toString()), SolCompraVO.Fields.NUMERO.toString());
		p.add(Projections.property("PPA." + ScoPontoParadaSolicitacao.Fields.CODIGO.toString()), SolCompraVO.Fields.PONTO_PARADA_ATUAL.toString());
		p.add(Projections.property("SCO." + ScoSolicitacaoDeCompra.Fields.PPS_CODIGO.toString()), SolCompraVO.Fields.PONTO_PARADA.toString());
		p.add(Projections.property("SCO." + ScoSolicitacaoDeCompra.Fields.IND_URGENTE.toString()), SolCompraVO.Fields.URGENTE.toString());
		p.add(Projections.property("SCO." + ScoSolicitacaoDeCompra.Fields.PRIORIDADE.toString()), SolCompraVO.Fields.PRIORIDADE.toString());
		p.add(Projections.property("SCO." + ScoSolicitacaoDeCompra.Fields.IND_EXCLUSAO.toString()), SolCompraVO.Fields.EXCLUSAO.toString());
		p.add(Projections.property("SCO." + ScoSolicitacaoDeCompra.Fields.IND_DEVOLUCAO.toString()), SolCompraVO.Fields.DEVOLUCAO.toString());
		p.add(Projections.property("SCO." + ScoSolicitacaoDeCompra.Fields.DATA_SOLICITACAO.toString()), SolCompraVO.Fields.DT_SOLICITACAO.toString());
		p.add(Projections.property("SCO." + ScoSolicitacaoDeCompra.Fields.DATA_AUTORIZACAO.toString()), SolCompraVO.Fields.DT_AUTORIZACAO.toString());
		p.add(Projections.property("MAT." + ScoMaterial.Fields.CODIGO.toString()), SolCompraVO.Fields.MAT_CODIGO.toString());
		p.add(Projections.property("MAT." + ScoMaterial.Fields.NOME.toString()), SolCompraVO.Fields.NOME_MATERIAL.toString());
		p.add(Projections.property("MAT." + ScoMaterial.Fields.DESCRICAO.toString()), SolCompraVO.Fields.DESCRICAO_MATERIAL.toString());
		p.add(Projections.property("SCO." + ScoSolicitacaoDeCompra.Fields.DESCRICAO.toString()), SolCompraVO.Fields.DESCRICAO_SC.toString());
		p.add(Projections.property("SCO." + ScoSolicitacaoDeCompra.Fields.QUANTIDADE_SOLICITADA.toString()), SolCompraVO.Fields.QTD_SOLICITADA.toString());
		p.add(Projections.property("SCO." + ScoSolicitacaoDeCompra.Fields.QUANTIDADE_APROVADA.toString()), SolCompraVO.Fields.QTD_APROVADA.toString());
		p.add(Projections.property("SCO." + ScoSolicitacaoDeCompra.Fields.UMD_CODIGO.toString()), SolCompraVO.Fields.COD_UNIDADE_MEDIDA.toString());
		p.add(Projections.property("SCO." + ScoSolicitacaoDeCompra.Fields.VALOR_UNIT_PREVISTO.toString()), SolCompraVO.Fields.VLR_UNITARIO_PREVISTO.toString());
		p.add(Projections.property("PPA." + ScoPontoParadaSolicitacao.Fields.DESCRICAO.toString()), SolCompraVO.Fields.DESCRICAO_PONTO_PARADA.toString());
		p.add(Projections.property("CC." + FccCentroCustos.Fields.CODIGO.toString()), SolCompraVO.Fields.CODIGO_CC.toString());
		p.add(Projections.property("CC." + FccCentroCustos.Fields.DESCRICAO.toString()), SolCompraVO.Fields.DESCRICAO_CC.toString());
		p.add(Projections.property("CCA." + FccCentroCustos.Fields.CODIGO.toString()), SolCompraVO.Fields.CODIGO_CC_APLICACAO.toString());
		p.add(Projections.property("CCA." + FccCentroCustos.Fields.DESCRICAO.toString()), SolCompraVO.Fields.DESCRICAO_CC_APLICACAO.toString());
		p.add(Projections.property("FIS." + RapPessoasFisicas.Fields.NOME.toString()), SolCompraVO.Fields.NOME_SOLICITANTE.toString());
		p.add(Projections.sqlProjection("SRV5_.RAM_NRO_RAMAL as "+SolCompraVO.Fields.RAMAL.toString(), new String[]{SolCompraVO.Fields.RAMAL.toString()}, new Type[]{StringType.INSTANCE}));
		p.add(Projections.sqlProjection("( SELECT count(*) FROM AGH.SCO_ARQUIVOS_ANEXOS AN WHERE AN.NUMERO = {alias}.NUMERO AND AN.TP_ORIGEM = 'SC') as "+SolCompraVO.Fields.QTDE_ANEXO.toString(), new String[]{SolCompraVO.Fields.QTDE_ANEXO.toString()}, new Type[]{IntegerType.INSTANCE}));
		p.add(Projections.sqlProjection("( SELECT COUNT(numero) FROM AGH.sco_sc_jn jn WHERE jn.numero = {alias}.NUMERO AND pps_codigo_loc_proxima = 2) as "+SolCompraVO.Fields.DEVOLVIDA.toString(), new String[]{SolCompraVO.Fields.DEVOLVIDA.toString()}, new Type[]{IntegerType.INSTANCE}));  
		
		criteria.createAlias("SCO."+ScoSolicitacaoDeCompra.Fields.LOCALIZACAO_PONTO_PARADA_ATUAL.toString(), "PPA");
		criteria.createAlias("SCO."+ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT");
		criteria.createAlias("SCO."+ScoSolicitacaoDeCompra.Fields.CENTRO_CUSTO.toString(), "CC");
		criteria.createAlias("SCO."+ScoSolicitacaoDeCompra.Fields.CENTRO_CUSTO_APLICADA.toString(), "CCA");				
		criteria.createAlias("SCO."+ScoSolicitacaoDeCompra.Fields.SERVIDOR.toString(), "SRV");
		criteria.createAlias("SRV."+RapServidores.Fields.PESSOA_FISICA.toString(),"FIS");
		criteria.createAlias("SCO."+ScoSolicitacaoDeCompra.Fields.SERVIDOR_COMPRA.toString(), "SCP", JoinType.LEFT_OUTER_JOIN);
		
		return p;
	}

	@SuppressWarnings({"PMD.ExcessiveMethodLength","PMD.NPathComplexity"})
	private DetachedCriteria obterCriteriaSolicitacoesCompraComprador(List<FccCentroCustos> listaCentroCustos, 
			FccCentroCustos centroCusto, FccCentroCustos centroCustoAplicada, ScoMaterial material, 
			Date dtSolInicial, Date dtSolFinal, ScoPontoParadaSolicitacao ppsSolicitante, 
			ScoPontoParadaSolicitacao ppsAutorizacao, ScoSolicitacaoDeCompra solicitacaoDeCompra, 
			ScoPontoParadaSolicitacao pontoParadaAtual, RapServidores servidorCompra, Boolean isPerfilGeral) {
		//PERFIL COMPRADOR
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoSolicitacaoDeCompra.class, "SCO");
        criteria.setProjection(getProjectionListSc(criteria));		

		criteria.setFetchMode(ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), FetchMode.JOIN);
		criteria.setFetchMode(ScoSolicitacaoDeCompra.Fields.UNIDADE_MEDIDA.toString(), FetchMode.JOIN);
		criteria.setFetchMode(ScoSolicitacaoDeCompra.Fields.CENTRO_CUSTO.toString(), FetchMode.JOIN);
		criteria.setFetchMode(ScoSolicitacaoDeCompra.Fields.CENTRO_CUSTO_APLICADA.toString(), FetchMode.JOIN);
		
		
		SimpleDateFormat formatadorTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		SimpleDateFormat formatadorDia = new SimpleDateFormat("dd/MM/yyyy");
		if(isPerfilGeral.equals(Boolean.FALSE)){
			criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoDeCompra.Fields.IND_FUNDO_FIXO.toString(), false));	
		}
		if(material != null) {
			criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), material));
		}
		if(centroCusto != null) {
			criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoDeCompra.Fields.CENTRO_CUSTO.toString(), centroCusto));
		}
		if(centroCustoAplicada != null) {
			criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoDeCompra.Fields.CENTRO_CUSTO_APLICADA.toString(), centroCustoAplicada));
		}
		if (dtSolInicial!=null){
			if (dtSolFinal!=null){
				
				criteria.add(Restrictions.ge("SCO."+ScoSolicitacaoDeCompra.Fields.DATA_SOLICITACAO.toString(), dtSolInicial));
				try {
					criteria.add(Restrictions.le("SCO."+ScoSolicitacaoDeCompra.Fields.DATA_SOLICITACAO.toString(), formatadorTime.parse(formatadorDia.format(dtSolFinal)+" 23:59:59")));
				} catch (ParseException e) {
					LOG.error("Erro ao executar chamada"+ e.getMessage());
				}
			
			} else {
				criteria.add(Restrictions.ge("SCO."+ScoSolicitacaoDeCompra.Fields.DATA_SOLICITACAO.toString(), dtSolInicial));
			}
		}else if (dtSolFinal!=null){
				try {
					criteria.add(Restrictions.le("SCO."+ScoSolicitacaoDeCompra.Fields.DATA_SOLICITACAO.toString(), formatadorTime.parse(formatadorDia.format(dtSolFinal)+" 23:59:59")));
				} catch (ParseException e) {
					LOG.error("Erro ao executar chamada"+ e.getMessage());
				}
		}
		if (solicitacaoDeCompra!=null){
			if (solicitacaoDeCompra.getNumero()!=null){
				criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), solicitacaoDeCompra.getNumero()));
			}
			if (solicitacaoDeCompra.getDtAutorizacao()!=null){
			try {
					criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoDeCompra.Fields.DATA_AUTORIZACAO.toString(),formatadorDia.parse(formatadorDia.format(solicitacaoDeCompra.getDtAutorizacao()))));
				} catch (ParseException e) {
					LOG.error("Erro ao executar chamada"+ e.getMessage());
				}
			}
			if (solicitacaoDeCompra.getExclusao()!=null){
				criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoDeCompra.Fields.IND_EXCLUSAO.toString(), solicitacaoDeCompra.getExclusao()));
			}
			if (solicitacaoDeCompra.getDevolucao()!=null){
				criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoDeCompra.Fields.IND_DEVOLUCAO.toString(), solicitacaoDeCompra.getDevolucao()));
			}
			if (solicitacaoDeCompra.getPrioridade()!=null){
				criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoDeCompra.Fields.PRIORIDADE.toString(), solicitacaoDeCompra.getPrioridade()));
			}
			if (solicitacaoDeCompra.getUrgente()!=null){
				criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoDeCompra.Fields.IND_URGENTE.toString(), solicitacaoDeCompra.getUrgente()));
			}
			if (solicitacaoDeCompra.getMatExclusivo()!=null){
				criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoDeCompra.Fields.IND_MAT_EXCLUSIVO.toString(), solicitacaoDeCompra.getMatExclusivo()));
			}
			if (solicitacaoDeCompra.getEfetivada()!=null){
				criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoDeCompra.Fields.IND_EFETIVADA.toString(), solicitacaoDeCompra.getEfetivada()));
			}
		}
		if (servidorCompra != null) {
			criteria.add(Restrictions.eq("SCP."+RapServidores.Fields.ID.toString(), servidorCompra.getId()));
		} 
		if (pontoParadaAtual != null) {
			criteria.add(Restrictions.eq("SCO."+ScoSolicitacaoDeCompra.Fields.CODIGO_LOCALIZACAO_PONTO_PARADA_ATUAL.toString(), pontoParadaAtual.getCodigo().shortValue()));
		}
		else {
			criteria.add(Restrictions.or(Restrictions.eq("SCO."+ScoSolicitacaoDeCompra.Fields.CODIGO_LOCALIZACAO_PONTO_PARADA_ATUAL.toString(), ppsAutorizacao.getCodigo().shortValue()), Restrictions.eq("SCO."+ScoSolicitacaoDeCompra.Fields.CODIGO_LOCALIZACAO_PONTO_PARADA_ATUAL.toString(), ppsSolicitante.getCodigo().shortValue())));
		}
		return criteria;
	}
	
	public List<ScoSolicitacaoDeCompra> pesquisarSolicitacaoCompraPorNumeroOuDescricao(Object pesquisa, Boolean filtrarAssociadas) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoSolicitacaoDeCompra.class, "SCO");
		criteria.createAlias(ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "material", JoinType.LEFT_OUTER_JOIN);
		
		String srtPesquisa = (String) pesquisa;
		if (StringUtils.isNotBlank(srtPesquisa)) {
			if (CoreUtil.isNumeroInteger(pesquisa)) {
				criteria.add(Restrictions.eq(ScoSolicitacaoDeCompra.Fields.NUMERO.toString(),
						Integer.valueOf(srtPesquisa)));
			} else {
				criteria.add(Restrictions.ilike(ScoSolicitacaoDeCompra.Fields.DESCRICAO.toString(),
						srtPesquisa, MatchMode.ANYWHERE));
			}			
		}
		criteria.add(Restrictions.eq(ScoSolicitacaoDeCompra.Fields.IND_EXCLUSAO.toString(), false));
		criteria.add(Restrictions.eq(ScoSolicitacaoDeCompra.Fields.IND_EFETIVADA.toString(), false));
		if (filtrarAssociadas) {
			DetachedCriteria subQuery = DetachedCriteria.forClass(ScoSolicitacaoCompraServico.class);
	
			subQuery.setProjection(Projections.projectionList().add(
					Projections.property(ScoSolicitacaoCompraServico.Fields.SLC_NUMERO.toString())));
			criteria.add(Subqueries.propertyNotIn(ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), subQuery));
		}

		criteria.addOrder(Order.asc(ScoSolicitacaoDeCompra.Fields.NUMERO.toString()));
		return executeCriteria(criteria, 0, 100, null, true);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<RelatorioSolicitacaoCompraEstocavelVO> pesquisarSolicitacaoMaterialEstocavel(
			Date dtInicial, Date dtFinal, Integer numSolicitacao, Date dataCompetencia) {
		Set<RelatorioSolicitacaoCompraEstocavelVO> listaRelatorioSolicitacaoCompraEstocavelVO = new HashSet<RelatorioSolicitacaoCompraEstocavelVO>();
		// Query principal do relatório Solicitação de Compra de Material Estocável
		consultaSolicitacaoMaterialEstocavelUnion1(dtInicial, dtFinal,
				numSolicitacao, dataCompetencia, listaRelatorioSolicitacaoCompraEstocavelVO);
		consultaSolicitacaoMaterialEstocavelUnion2(dtInicial, dtFinal,
				numSolicitacao, dataCompetencia, listaRelatorioSolicitacaoCompraEstocavelVO);
		// Busca dados para planejamento de solicitações de compra
		for (RelatorioSolicitacaoCompraEstocavelVO solicitacaoCompraEstocavelVO : listaRelatorioSolicitacaoCompraEstocavelVO) {
			planejamentoSolicitacaoEstocaveis(solicitacaoCompraEstocavelVO);
			historicoSolicitacaoEstocaveis(solicitacaoCompraEstocavelVO);
		}
		Object[] sorted = listaRelatorioSolicitacaoCompraEstocavelVO.toArray();
		Arrays.sort(sorted);
		return (List) Arrays.asList(sorted);
	}

	public Integer buscaFornecedorPadrao() {
		Integer fornecedorPadrao = 1;
		DetachedCriteria criteria = DetachedCriteria.forClass(AghParametros.class, "SCO").add(Restrictions.eq(AghParametros.Fields.NOME.toString(), "P_FORNECEDOR_PADRAO"));
        List<AghParametros> aghParametros = executeCriteria(criteria);
		if (aghParametros != null && !aghParametros.isEmpty()) {
			fornecedorPadrao = aghParametros.get(0).getVlrNumerico().intValue();
		}
		return fornecedorPadrao;
	}
	
	public Integer buscaTmvDoc(){
		Integer tmvDoc = 0;
		DetachedCriteria criteria = DetachedCriteria.forClass(AghParametros.class, "SCO").add(Restrictions.eq(AghParametros.Fields.NOME.toString(), "P_TMV_DOC_NR"));
		List<AghParametros> aghParametros = executeCriteria(criteria);
		if (aghParametros != null) {
			tmvDoc = aghParametros.get(0).getVlrNumerico().intValue();
		}
		return tmvDoc;
	}

	private void consultaSolicitacaoMaterialEstocavelUnion1(Date dtInicial,Date dtFinal,Integer numSolicitacao,	Date dataCompetencia, 
			Set<RelatorioSolicitacaoCompraEstocavelVO> listaRelatorioSolicitacaoCompraEstocavelVO) {
		if (dtInicial == null && numSolicitacao == null) {
			return;
		}
		Integer fornecedorPadrao = buscaFornecedorPadrao();
		Query query = null;
		StringBuilder hql = new StringBuilder(3000);
		hql.append("SELECT  MAT.").append(ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString());
		hql.append(",MAT.").append(ScoMaterial.Fields.CODIGO.toString()).append(",MAT.").append(ScoMaterial.Fields.NOME.toString());
		hql.append(",MAT.").append(ScoMaterial.Fields.UNIDADE_MEDIDA.toString()).append(",SLC.").append(ScoSolicitacaoDeCompra.Fields.NUMERO.toString());
		hql.append(",MAT.").append(ScoMaterial.Fields.DESCRICAO.toString()).append(",EAL1.").append(SceEstoqueAlmoxarifado.Fields.QTDE_PONTO_PEDIDO.toString()); 
		hql.append(",EAL1.").append(SceEstoqueAlmoxarifado.Fields.QTDE_PONTO_PEDIDO.toString()).append(",SLC.").append(ScoSolicitacaoDeCompra.Fields.QTDE_REFORCO.toString());
		hql.append(",SLC.").append(ScoSolicitacaoDeCompra.Fields.QUANTIDADE_APROVADA.toString()).append(",EGR.").append(SceEstoqueGeral.Fields.CLASSIFICACAO_ABC.toString());
		hql.append(",EGR.").append(SceEstoqueGeral.Fields.SUBCLASSIFICACAO_ABC.toString()).append(",EAL1.").append(SceEstoqueAlmoxarifado.Fields.QTDE_DISPONIVEL.toString());
		hql.append(",EAL1.").append(SceEstoqueAlmoxarifado.Fields.QTDE_BLOQUEADA.toString()).append(",ALM.").append(SceAlmoxarifado.Fields.SEQ.toString());
		hql.append(",SLC.").append(ScoSolicitacaoDeCompra.Fields.DATA_SOLICITACAO.toString()).append(",SLC.").append(ScoSolicitacaoDeCompra.Fields.GERACAO_AUTOMATICA.toString());
		hql.append(",VPS.").append(VRapPessoaServidor.Fields.NOME.toString()).append(",SLC.").append(ScoSolicitacaoDeCompra.Fields.JUSTIFICATIVA_EXCLUSIVIDADE.toString());
		hql.append(" FROM ").append(VRapPessoaServidor.class.getSimpleName()).append(" VPS, ");
		hql.append(SceEstoqueGeral.class.getSimpleName()).append(" EGR, ").append(SceAlmoxarifado.class.getSimpleName()).append(" ALM, ");
		hql.append(SceEstoqueAlmoxarifado.class.getSimpleName()).append(" EAL1, ");
		hql.append(ScoMaterial.class.getSimpleName()).append(" MAT, ").append(ScoSolicitacaoDeCompra.class.getSimpleName()).append(" SLC ");

		hql.append("WHERE SLC.").append(ScoSolicitacaoDeCompra.Fields.IND_EXCLUSAO.toString()).append(" = :indExclusao ");
		
		if (numSolicitacao != null && numSolicitacao != 0) {hql.append(" AND SLC.").append(ScoSolicitacaoDeCompra.Fields.NUMERO.toString()).append(" = :numSolicitacao ");
		} else {
			hql.append(" AND SLC.").append(ScoSolicitacaoDeCompra.Fields.DATA_SOLICITACAO.toString()).append(" BETWEEN :dtInicial AND :dtFinal ");
		}
		hql.append(" AND   MAT.").append(ScoMaterial.Fields.CODIGO.toString()).append(" = SLC.")
				.append(ScoSolicitacaoDeCompra.Fields.MAT_CODIGO.toString()).append(' ');
		hql.append(" AND   EAL1.").append(SceEstoqueAlmoxarifado.Fields.CODIGO_MATERIAL.toString()).append(" = MAT.")
				.append(ScoMaterial.Fields.CODIGO.toString()).append(' ');
		hql.append(" AND   EAL1.").append(SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO.toString())
				.append(" =  CASE WHEN  MAT.").append(ScoMaterial.Fields.ALMOXARIFADO_SEQ.toString()).append(" IS NULL THEN MAT.");
		hql.append(ScoMaterial.Fields.ALM_SEQ_LOCAL_ESTQ.toString());
		hql.append(" ELSE MAT.").append(ScoMaterial.Fields.ALMOXARIFADO_SEQ.toString()).append(" END");
		hql.append(" AND EAL1.").append(SceEstoqueAlmoxarifado.Fields.NUMERO_FORNECEDOR.toString()).append(" = :fornecedorPadrao ");
		hql.append(" AND EAL1.").append(SceEstoqueAlmoxarifado.Fields.IND_ESTOCAVEL.toString()).append(" = :indEstocavel ");
		hql.append(" AND   ALM.").append(SceAlmoxarifado.Fields.SEQ.toString())
				.append(" = EAL1.").append(SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO_SEQ.toString()).append(' ');
		hql.append(" AND   EGR.").append(SceEstoqueGeral.Fields.MAT_CODIGO.toString())
				.append(" = EAL1.").append(SceEstoqueAlmoxarifado.Fields.CODIGO_MATERIAL.toString()).append(' ');
		hql.append(" AND EGR.").append(SceEstoqueGeral.Fields.DATA_COMPETENCIA.toString()).append(" = :dtAtual ");
		hql.append(" AND EGR.").append(SceEstoqueGeral.Fields.FRN_NUMERO.toString()).append(" = :fornecedorPadrao ");
		hql.append(" AND   VPS.").append(VRapPessoaServidor.Fields.SER_MATRICULA.toString())
				.append(" = SLC.").append(ScoSolicitacaoDeCompra.Fields.MATRICULA_SERVIDOR_ATUAL.toString()).append(' ');
		hql.append(" AND   VPS.").append(VRapPessoaServidor.Fields.SER_VIN_CODIGO.toString()).append(" = SLC.")
				.append(ScoSolicitacaoDeCompra.Fields.CODIGO_VINCULO_SERVIDOR_ACESSO.toString()).append(' ');		
		hql.append(" AND SLC.").append(ScoSolicitacaoDeCompra.Fields.PPS_CODIGO_LOC_PROXIMA).append(" IN (SELECT ");
		hql.append(ScoPontoParadaSolicitacao.Fields.CODIGO).append(" FROM ").append(ScoPontoParadaSolicitacao.class.getSimpleName());
		hql.append(" WHERE ").append(ScoPontoParadaSolicitacao.Fields.TIPO).append(" = :pontoParadaPlanejamento )");
//		hql.append(" ORDER BY 1,2,3,7 ");
		query = createHibernateQuery(hql.toString());
		query.setParameter("indExclusao", Boolean.FALSE);
		query.setParameter("indEstocavel", Boolean.TRUE);
		query.setParameter("dtAtual", dataCompetencia);
		query.setParameter("fornecedorPadrao", fornecedorPadrao);
		query.setParameter("pontoParadaPlanejamento", DominioTipoPontoParada.PL);
		if (numSolicitacao != null && numSolicitacao > 0) {
			query.setParameter("numSolicitacao", numSolicitacao);
		}
			else {
			if (dtInicial != null) {
				query.setParameter("dtInicial", dtInicial);
			}
			if (dtFinal != null) {
				query.setParameter("dtFinal", dtFinal);
			}
		}
		List<Object[]> resultList = query.list();
		preencheRelatorioSolicitacaoCompraEstocavelVO(resultList,listaRelatorioSolicitacaoCompraEstocavelVO);
	}

	private void consultaSolicitacaoMaterialEstocavelUnion2(Date dtInicial,Date dtFinal,Integer numSolicitacao,	Date dataCompetencia, 
			Set<RelatorioSolicitacaoCompraEstocavelVO> listaRelatorioSolicitacaoCompraEstocavelVO) {
		if (dtInicial == null && numSolicitacao == null) {
			return;
		}
		Integer fornecedorPadrao = buscaFornecedorPadrao();
		Query query = null;
		StringBuilder hql = new StringBuilder(2000);
hql.append("SELECT  MAT.").append(ScoMaterial.Fields.GRUPO_MATERIAL_CODIGO.toString());
		hql.append(",MAT.").append(ScoMaterial.Fields.CODIGO.toString()).append(",MAT.").append(ScoMaterial.Fields.NOME.toString());
		hql.append(",MAT.").append(ScoMaterial.Fields.UNIDADE_MEDIDA.toString()).append(",SLC.").append(ScoSolicitacaoDeCompra.Fields.NUMERO.toString());
		hql.append(",MAT.").append(ScoMaterial.Fields.DESCRICAO.toString()).append(",EAL1.").append(SceEstoqueAlmoxarifado.Fields.QTDE_PONTO_PEDIDO.toString()); 
		hql.append(",EAL1.").append(SceEstoqueAlmoxarifado.Fields.QTDE_PONTO_PEDIDO.toString()).append(",SLC.").append(ScoSolicitacaoDeCompra.Fields.QTDE_REFORCO.toString());
		hql.append(",SLC.").append(ScoSolicitacaoDeCompra.Fields.QUANTIDADE_APROVADA.toString()).append(",EGR.").append(SceEstoqueGeral.Fields.CLASSIFICACAO_ABC.toString());
		hql.append(",EGR.").append(SceEstoqueGeral.Fields.SUBCLASSIFICACAO_ABC.toString()).append(",EAL1.").append(SceEstoqueAlmoxarifado.Fields.QTDE_DISPONIVEL.toString());
		hql.append(",EAL1.").append(SceEstoqueAlmoxarifado.Fields.QTDE_BLOQUEADA.toString()).append(",ALM.").append(SceAlmoxarifado.Fields.SEQ.toString());
		hql.append(",SLC.").append(ScoSolicitacaoDeCompra.Fields.DATA_SOLICITACAO.toString()).append(",SLC.").append(ScoSolicitacaoDeCompra.Fields.GERACAO_AUTOMATICA.toString());
		hql.append(",VPS.").append(VRapPessoaServidor.Fields.NOME.toString()).append(",SLC.").append(ScoSolicitacaoDeCompra.Fields.JUSTIFICATIVA_EXCLUSIVIDADE.toString());
		hql.append(" FROM ").append(ScoAutorizacaoForn.class.getSimpleName()).append(" AFN, ");
		hql.append(ScoFaseSolicitacao.class.getSimpleName()).append(" FSC, ").append(VRapPessoaServidor.class.getSimpleName()).append(" VPS, ");
		hql.append(SceEstoqueGeral.class.getSimpleName()).append(" EGR, ").append(SceAlmoxarifado.class.getSimpleName()).append(" ALM, ");
		hql.append(SceEstoqueAlmoxarifado.class.getSimpleName()).append(" EAL1, ").append(ScoMaterial.class.getSimpleName()).append(" MAT, ");
		hql.append(ScoSolicitacaoDeCompra.class.getSimpleName()).append(" SLC ");

		hql.append("WHERE SLC.").append(ScoSolicitacaoDeCompra.Fields.IND_EXCLUSAO.toString()).append(" = :indExclusao ");
		if (numSolicitacao != null && numSolicitacao != 0) {
			hql.append(" AND SLC.").append(ScoSolicitacaoDeCompra.Fields.NUMERO.toString()).append(" = :numSolicitacao ");
		} else {
			hql.append(" AND SLC.").append(ScoSolicitacaoDeCompra.Fields.DATA_ANALISE.toString()).append(" BETWEEN :dtInicial AND :dtFinal ");
		}
		hql.append(" AND   MAT.").append(ScoMaterial.Fields.CODIGO.toString()).append(" = SLC.")
				.append(ScoSolicitacaoDeCompra.Fields.MAT_CODIGO.toString()).append(' ');
		hql.append(" AND   MAT.").append(ScoMaterial.Fields.CODIGO.toString()).append(" = SLC.")
				.append(ScoSolicitacaoDeCompra.Fields.MAT_CODIGO.toString()).append(' ');
		hql.append(" AND   EAL1.").append(SceEstoqueAlmoxarifado.Fields.CODIGO_MATERIAL.toString()).append(" = MAT.")
				.append(ScoMaterial.Fields.CODIGO.toString()).append(' ');
		hql.append(" AND   EAL1.").append(SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO.toString())
				.append(" =  CASE WHEN  MAT.").append(ScoMaterial.Fields.ALMOXARIFADO_SEQ.toString()).append(" IS NULL THEN MAT.");
		hql.append(ScoMaterial.Fields.ALM_SEQ_LOCAL_ESTQ.toString());
		hql.append(" ELSE MAT.").append(ScoMaterial.Fields.ALMOXARIFADO_SEQ.toString()).append(" END");
		hql.append(" AND EAL1.").append(SceEstoqueAlmoxarifado.Fields.NUMERO_FORNECEDOR.toString()).append(" = :fornecedorPadrao ");
		hql.append(" AND EAL1.").append(SceEstoqueAlmoxarifado.Fields.IND_ESTOCAVEL.toString()).append(" = :indEstocavel ");
		hql.append(" AND   ALM.").append(SceAlmoxarifado.Fields.SEQ.toString()).append(" = EAL1.")
				.append(SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO_SEQ.toString()).append(' ');
		hql.append(" AND   EGR.").append(SceEstoqueGeral.Fields.MAT_CODIGO.toString()).append(" = EAL1.")
				.append(SceEstoqueAlmoxarifado.Fields.CODIGO_MATERIAL.toString()).append(' ');
		hql.append(" AND EGR.").append(SceEstoqueGeral.Fields.DATA_COMPETENCIA.toString()).append(" = :dtAtual ");
		hql.append(" AND EGR.").append(SceEstoqueGeral.Fields.FRN_NUMERO.toString()).append(" = :fornecedorPadrao ");
		hql.append(" AND   VPS.").append(VRapPessoaServidor.Fields.SER_MATRICULA.toString()).append(" = SLC.").append(ScoSolicitacaoDeCompra.Fields.MATRICULA_SERVIDOR_ATUAL.toString()).append(' ');
		hql.append(" AND   VPS.").append(VRapPessoaServidor.Fields.SER_VIN_CODIGO.toString()).append(" = SLC.").append(ScoSolicitacaoDeCompra.Fields.CODIGO_VINCULO_SERVIDOR_ACESSO.toString()).append(' ');
		hql.append(" AND   FSC.").append(ScoFaseSolicitacao.Fields.SLC_NUMERO.toString()).append(" = SLC.").append(ScoSolicitacaoDeCompra.Fields.NUMERO.toString()).append(' ');
		hql.append(" AND FSC.").append(ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString()).append(" = :indExclusao ");
		hql.append(" AND   AFN.").append(ScoAutorizacaoForn.Fields.NUMERO.toString()).append(" = FSC.").append(ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()).append(' ');
		hql.append(" AND SLC.").append(ScoSolicitacaoDeCompra.Fields.PPS_CODIGO_LOC_PROXIMA).append(" IN (SELECT ");
		hql.append(ScoPontoParadaSolicitacao.Fields.CODIGO).append(" FROM ").append(ScoPontoParadaSolicitacao.class.getSimpleName());
		hql.append(" WHERE ").append(ScoPontoParadaSolicitacao.Fields.TIPO).append(" = :pontoParadaPlanejamento )");

		query = createHibernateQuery(hql.toString());
		query.setParameter("indExclusao", Boolean.FALSE);
		query.setParameter("indEstocavel", Boolean.TRUE);
		query.setParameter("dtAtual", dataCompetencia);
		query.setParameter("fornecedorPadrao", fornecedorPadrao);
		query.setParameter("pontoParadaPlanejamento", DominioTipoPontoParada.PL);
		if (numSolicitacao != null && numSolicitacao > 0) {
			query.setParameter("numSolicitacao", numSolicitacao);
		}
		else {
			if (dtInicial != null) {
				query.setParameter("dtInicial", dtInicial);
			}
			if (dtFinal != null) {
				query.setParameter("dtFinal", dtFinal);
			}
		}
		List<Object[]> resultList = query.list();
	preencheRelatorioSolicitacaoCompraEstocavelVO(resultList, listaRelatorioSolicitacaoCompraEstocavelVO);
	}

	private void preencheRelatorioSolicitacaoCompraEstocavelVO(List<Object[]> resultList,Set<RelatorioSolicitacaoCompraEstocavelVO> listaRelatorioSolicitacaoCompraEstocavelVO) {
		RelatorioSolicitacaoCompraEstocavelVO relatorioSolicitacaoCompraEstocavelVO;
		for (Object[] object : resultList) {
			relatorioSolicitacaoCompraEstocavelVO = new RelatorioSolicitacaoCompraEstocavelVO();
			Integer codigoGrupoMaterial = (Integer) object[0];
			Integer codigoMaterial = (Integer) object[1];
			relatorioSolicitacaoCompraEstocavelVO.setCodigoGrupoMaterial(codigoGrupoMaterial);
			relatorioSolicitacaoCompraEstocavelVO.setCodigo(codigoMaterial);
	        relatorioSolicitacaoCompraEstocavelVO.setDescricao((String) object[2]);
		    if (object[5] != null){
    	       relatorioSolicitacaoCompraEstocavelVO.setDescricao(relatorioSolicitacaoCompraEstocavelVO.getDescricao() + (String) object[5]);
		    }
			ScoUnidadeMedida unidadeMedida = (ScoUnidadeMedida) object[3];
			relatorioSolicitacaoCompraEstocavelVO.setUnidade(unidadeMedida.getDescricao());
			relatorioSolicitacaoCompraEstocavelVO.setNumSolicitacao((Integer) object[4]);
			relatorioSolicitacaoCompraEstocavelVO.setEstoqueMaximo(((Integer) object[6]) * 2);
			relatorioSolicitacaoCompraEstocavelVO.setPontoPedido((Integer) object[7]);
			Long loteRep;
			if (object[8] == null || (Long) object[8] == 0) {
				loteRep = (Long) object[9];
			} else {
				loteRep = (Long) object[8];
			}
			relatorioSolicitacaoCompraEstocavelVO.setLoteRep(Integer.valueOf(loteRep.toString()));
			DominioClassifABC classeABC;
			if (object[10] != null) {
				classeABC = (DominioClassifABC) object[10];
				relatorioSolicitacaoCompraEstocavelVO.setClasseAbc(classeABC.getDescricao());
			}
			DominioClassifABC subClasseAbc;
			if (object[11] != null) {
				subClasseAbc = (DominioClassifABC) object[11];
				relatorioSolicitacaoCompraEstocavelVO.setClasseAbc(relatorioSolicitacaoCompraEstocavelVO
								.getClasseAbc() + subClasseAbc.getDescricao());
			}
			if (object[12] != null) {
				relatorioSolicitacaoCompraEstocavelVO.setQuantidadeDisponivel((Integer) object[12]);
			} else {
				relatorioSolicitacaoCompraEstocavelVO.setQuantidadeDisponivel(0);
			}
			if (object[13] != null) {
				relatorioSolicitacaoCompraEstocavelVO.setQuantidadeBloqueada((Integer) object[13]);
			} else {
				relatorioSolicitacaoCompraEstocavelVO.setQuantidadeBloqueada(0);
			}
			relatorioSolicitacaoCompraEstocavelVO.setSaldoTotalEstoque(relatorioSolicitacaoCompraEstocavelVO
							.getQuantidadeDisponivel()	+ relatorioSolicitacaoCompraEstocavelVO.getQuantidadeBloqueada());
			if (relatorioSolicitacaoCompraEstocavelVO.getLoteRep() != null) {
				relatorioSolicitacaoCompraEstocavelVO
						.setQuantidadeSolicitada(relatorioSolicitacaoCompraEstocavelVO.getLoteRep());
			}
			relatorioSolicitacaoCompraEstocavelVO.setDataSolicitacao((Date) object[15]);
			if (object[16] != null && (Boolean) object[16] == Boolean.FALSE) {
				relatorioSolicitacaoCompraEstocavelVO.setDigitadoPor("Geração Automática");
			} else {
				relatorioSolicitacaoCompraEstocavelVO.setDigitadoPor("Digitada por " + (String) object[17]);
			}
			listaRelatorioSolicitacaoCompraEstocavelVO.add(relatorioSolicitacaoCompraEstocavelVO);
		}
	}

	private void planejamentoSolicitacaoEstocaveis(RelatorioSolicitacaoCompraEstocavelVO relatorioVO) {
		Integer numSolicitacao;
		if (relatorioVO.getNumSolicitacao() != null) {
			numSolicitacao = relatorioVO.getNumSolicitacao();
		} else {
			return;
		}
		Integer fornecedorPadrao = buscaFornecedorPadrao();
		StringBuffer hql = new StringBuffer(3000);
		Query query = null;
			hql.append("SELECT  SLC.").append(ScoSolicitacaoDeCompra.Fields.DATA_SOLICITACAO.toString());
		hql.append(", SLC.").append(ScoSolicitacaoDeCompra.Fields.CCT_CODIGO.toString()).append(", MAT.").append(ScoMaterial.Fields.ALMOXARIFADO_SEQ.toString());
		hql.append(", MAT.").append(ScoMaterial.Fields.UNIDADE_MEDIDA_CODIGO.toString()).append(", EAL.").append(SceEstoqueAlmoxarifado.Fields.TEMPO_REPOSICAO.toString());
		hql.append(", EAL.").append(SceEstoqueAlmoxarifado.Fields.QTDE_DISPONIVEL.toString()).append(", EAL.").append(SceEstoqueAlmoxarifado.Fields.QTDE_BLOQUEADA.toString());
		hql.append(", EAL.").append(SceEstoqueAlmoxarifado.Fields.QTDE_PONTO_PEDIDO.toString()).append(", SLC.").append(ScoSolicitacaoDeCompra.Fields.QTDE_REFORCO.toString());
		hql.append(", SLC.").append(ScoSolicitacaoDeCompra.Fields.QUANTIDADE_APROVADA.toString()).append(", EAL.").append(SceEstoqueAlmoxarifado.Fields.IND_PONTO_PEDIDO_CALC.toString());
		hql.append(" FROM ").append(SceAlmoxarifado.class.getSimpleName()).append(" ALM, ").append(SceEstoqueAlmoxarifado.class.getSimpleName()).append(" EAL, ");
		hql.append(ScoMaterial.class.getSimpleName()).append(" MAT, ").append(ScoSolicitacaoDeCompra.class.getSimpleName()).append(" SLC ");
		hql.append("WHERE SLC.").append(ScoSolicitacaoDeCompra.Fields.IND_EXCLUSAO.toString()).append(" = :indExclusao");
		hql.append(" AND   SLC.").append(ScoSolicitacaoDeCompra.Fields.NUMERO.toString()).append(" = :numSolicitacao ");
		hql.append(" AND   SLC.").append(ScoSolicitacaoDeCompra.Fields.MAT_CODIGO.toString()).append(" = EAL.").append(SceEstoqueAlmoxarifado.Fields.MATERIAL_CODIGO.toString());
		hql.append(" AND   EAL.").append(SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO_SEQ.toString()).append(" =  CASE WHEN  MAT.").append(ScoMaterial.Fields.ALMOXARIFADO_SEQ.toString()).append(" IS NULL THEN MAT.").append(ScoMaterial.Fields.ALM_SEQ_LOCAL_ESTQ.toString());
		hql.append(" ELSE MAT.").append(ScoMaterial.Fields.ALMOXARIFADO_SEQ.toString()).append(" END");
		hql.append(" AND EAL.").append(SceEstoqueAlmoxarifado.Fields.NUMERO_FORNECEDOR.toString()).append(" = :fornecedorPadrao ");
		hql.append(" AND EAL.").append(SceEstoqueAlmoxarifado.Fields.IND_ESTOCAVEL.toString()).append(" = :indEstocavel ");
		hql.append(" AND   MAT.").append(ScoMaterial.Fields.CODIGO.toString()).append(" = EAL.").append(SceEstoqueAlmoxarifado.Fields.MATERIAL_CODIGO.toString());
		hql.append(" AND   ALM.").append(SceAlmoxarifado.Fields.SEQ.toString()).append(" = EAL.").append(SceEstoqueAlmoxarifado.Fields.ALMOXARIFADO_SEQ.toString());
		query = createHibernateQuery(hql.toString());
		query.setParameter("indExclusao", Boolean.FALSE);
		query.setParameter("numSolicitacao", numSolicitacao);
		query.setParameter("fornecedorPadrao", fornecedorPadrao);
		query.setParameter("indEstocavel", Boolean.TRUE);
		List<Object[]> resultList = query.list();
		preenchePlanejamentoSolicitacaoEstocaveis(resultList, relatorioVO);
	}

	private void preenchePlanejamentoSolicitacaoEstocaveis(	List<Object[]> resultList,RelatorioSolicitacaoCompraEstocavelVO relatorioVO) {
		Date datasolicitacao = null;
		Short almoxSeq = null;
		String unidade = null;
		Integer cCustoRequisitante = null , qtdSolicitada = null, qtdPontoPedidoPlanejamento= null, tempoReposicao= null;
		Long qtdReforco = null;
		Long qtdAprovada = null;
		Boolean IndPontoPedido = null;
		Double duracaoEstoque = null, qtdBloqueada= null, qtdDisponivel = null;
	
		for (Object[] object : resultList) {
			datasolicitacao = (Date) object[0];
			cCustoRequisitante = (Integer) object[1];
			almoxSeq = (Short) object[2];
			unidade = (String) object[3];
			tempoReposicao = (Integer) object[4];
			qtdDisponivel = Double.valueOf(((Integer) object[5]).toString());
			qtdBloqueada = Double.valueOf(((Integer) object[6]).toString());
			qtdPontoPedidoPlanejamento = (Integer) object[7];
			qtdReforco = (Long) object[8];
			qtdAprovada = (Long) object[9];
			IndPontoPedido = (Boolean) object[10];

			if (qtdReforco == null || qtdReforco == 0) {
				qtdSolicitada = Integer.valueOf(qtdAprovada.toString());
			} else {
				qtdSolicitada = qtdReforco.intValue();
			}

			if (qtdPontoPedidoPlanejamento == null || qtdPontoPedidoPlanejamento == 0) {
				qtdPontoPedidoPlanejamento = 1;
			}
		}
		duracaoEstoque = ((qtdDisponivel + qtdBloqueada) * tempoReposicao)/ qtdPontoPedidoPlanejamento;
		relatorioVO.setDataSolicitacao(datasolicitacao);
		relatorioVO.setCentroCustoRequisitante(cCustoRequisitante);
		relatorioVO.setAlmox(Integer.parseInt(almoxSeq.toString()));
		relatorioVO.setUnidade(unidade);
		relatorioVO.setTempoReposicao(tempoReposicao);
		relatorioVO.setSaldoAtual(qtdDisponivel.intValue() + qtdBloqueada.intValue());
		relatorioVO.setDuracaoEstoque(new BigDecimal(duracaoEstoque).setScale(2, RoundingMode.HALF_EVEN));
		relatorioVO.setPontoPedidoPlanejamento(qtdPontoPedidoPlanejamento);
		if (IndPontoPedido != null && IndPontoPedido) {
			relatorioVO.setPontoPedCalc("S"); 
		} else { 
			relatorioVO.setPontoPedCalc("N");
		}
		relatorioVO.setQuantidadeSolcitadaReferente(qtdSolicitada);
		relatorioVO.setQuantidadeAutorizada(Integer.valueOf(qtdAprovada.toString()));
	}

	private void historicoSolicitacaoEstocaveis(RelatorioSolicitacaoCompraEstocavelVO relatorioVO) {
		Integer codMaterial;
		if (relatorioVO.getCodigo() != null) {
			codMaterial = relatorioVO.getCodigo();
		} else {
			return;
		}
		StringBuffer hql = new StringBuffer(3000);
		Query query = null;
		hql.append("SELECT  SLC.").append(ScoSolicitacaoDeCompra.Fields.MATERIAL_CODIGO.toString());
		hql.append(", SLC.").append(ScoSolicitacaoDeCompra.Fields.QUANTIDADE_APROVADA.toString()).append(", AFN.").append(ScoAutorizacaoForn.Fields.PFR_LCT_NUMERO.toString());
		hql.append(", AFN.").append(ScoAutorizacaoForn.Fields.NRO_COMPLEMENTO.toString()).append(", IPF.").append(ScoItemPropostaFornecedor.Fields.ITL_NUMERO.toString());
		hql.append(", AFN.").append(ScoAutorizacaoForn.Fields.DT_GERACAO.toString()).append(", AFN.").append(ScoAutorizacaoForn.Fields.DT_PREV_ENTREGA.toString());
		hql.append(", IAF.").append(ScoItemAutorizacaoForn.Fields.QUANTIDADE_SOLICITADA.toString()).append(", IAF.").append(ScoItemAutorizacaoForn.Fields.QUANTIDADE_RECEBIDA.toString());
		hql.append(", IAF.").append(ScoItemAutorizacaoForn.Fields.IND_SITUACAO.toString()).append(", FRN.").append(ScoFornecedor.Fields.RAZAO_SOCIAL.toString());
		hql.append(", SLC.").append(ScoSolicitacaoDeCompra.Fields.NUMERO.toString()).append(", SLC.").append(ScoSolicitacaoDeCompra.Fields.DATA_SOLICITACAO.toString());
		hql.append(" FROM ").append(ScoFornecedor.class.getSimpleName()).append(" FRN, ").append(ScoItemPropostaFornecedor.class.getSimpleName()).append(" IPF, ");
		hql.append(ScoItemAutorizacaoForn.class.getSimpleName()).append(" IAF, ").append(ScoAutorizacaoForn.class.getSimpleName()).append(" AFN, ");
		hql.append(ScoFaseSolicitacao.class.getSimpleName()).append(" FSC, ").append(ScoSolicitacaoDeCompra.class.getSimpleName()).append(" SLC ");
		hql.append("WHERE SLC.").append(ScoSolicitacaoDeCompra.Fields.IND_EXCLUSAO.toString()).append(" = :indExclusao");
		hql.append(" AND   SLC.").append(ScoSolicitacaoDeCompra.Fields.MAT_CODIGO.toString()).append(" = :codMaterial ");
		hql.append(" AND   FSC.").append(ScoFaseSolicitacao.Fields.SLC_NUMERO.toString()).append(" = SLC.").append(ScoSolicitacaoDeCompra.Fields.NUMERO.toString());
		hql.append(" AND FSC.").append(ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString()).append(" = :indExclusao");
		hql.append(" AND   IAF.").append(ScoItemAutorizacaoForn.Fields.ID_AUT_FORN_NUMERO.toString()).append(" = FSC.").append(ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()).append(" AND   IAF.").append(ScoItemAutorizacaoForn.Fields.NUMERO.toString()).append(" = FSC.").append(ScoFaseSolicitacao.Fields.IAF_NUMERO.toString());
		hql.append(" AND   IAF.").append(ScoItemAutorizacaoForn.Fields.IND_SITUACAO.toString()).append(" IN ('AE','PA') ");
		hql.append(" AND   AFN.").append(ScoAutorizacaoForn.Fields.NUMERO.toString()).append(" = IAF.").append(ScoItemAutorizacaoForn.Fields.ID_AUT_FORN_NUMERO.toString());
		hql.append(" AND   AFN.").append(ScoAutorizacaoForn.Fields.SITUACAO.toString()).append(" IN ('AE','PA') ");
		hql.append(" AND   IPF.").append(ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR_LICITACAO_ID.toString()).append(" = IAF.").append(ScoItemAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR_LICITACAO_ID.toString());
		hql.append(" AND   IPF.").append(ScoItemPropostaFornecedor.Fields.PROPOSTA_FORNECEDOR_FORNECEDOR_ID.toString()).append(" = IAF.").append(ScoItemAutorizacaoForn.Fields.PROPOSTA_FORNECEDOR_FORNECEDOR_ID.toString());
		hql.append(" AND   IPF.").append(ScoItemPropostaFornecedor.Fields.NUMERO.toString()).append(" = IAF.").append(ScoItemAutorizacaoForn.Fields.NUMERO_PROPOSTA.toString());
		hql.append(" AND IPF.")	.append(ScoItemPropostaFornecedor.Fields.IND_EXCLUSAO.toString()).append(" = :indExclusao");
		hql.append(" AND   FRN.").append(ScoFornecedor.Fields.NUMERO.toString()).append(" = AFN.").append(ScoAutorizacaoForn.Fields.FORNECEDOR.toString());
		hql.append(" UNION ");		
		hql.append("SELECT  SLC.").append(ScoSolicitacaoDeCompra.Fields.MATERIAL_CODIGO.toString()).append(", SLC.").append(ScoSolicitacaoDeCompra.Fields.QUANTIDADE_APROVADA.toString());
		hql.append(", FSC.").append(ScoFaseSolicitacao.Fields.LCT_NUMERO.toString()).append(", NULL").append(", FSC.").append(ScoFaseSolicitacao.Fields.ITL_NUMERO.toString()).append(", NULL, NULL, NULL, NULL, NULL, NULL, ");
		hql.append(", SLC.").append(ScoSolicitacaoDeCompra.Fields.NUMERO.toString()).append(", SLC.").append(ScoSolicitacaoDeCompra.Fields.DATA_SOLICITACAO.toString());
		hql.append(" FROM ").append(ScoFaseSolicitacao.class.getSimpleName()).append(" FSC LEFT JOIN ").append(ScoSolicitacaoDeCompra.class.getSimpleName()).append(" SLC ");
        hql.append(" ON FSC.").append(ScoFaseSolicitacao.Fields.SLC_NUMERO.toString()).append(" = SLC.").append(ScoSolicitacaoDeCompra.Fields.NUMERO.toString());
		hql.append(" AND FSC.").append(ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString()).append(" = :indExclusao");
		hql.append(" WHERE SLC.").append(ScoSolicitacaoDeCompra.Fields.IND_EXCLUSAO.toString()).append(" ||'' = :indExclusao");
		hql.append(" AND   SLC.").append(ScoSolicitacaoDeCompra.Fields.MAT_CODIGO.toString()).append(" = :codMaterial ");
		hql.append(" AND   SLC.").append(ScoSolicitacaoDeCompra.Fields.NUMERO.toString()).append(" NOT IN (SELECT FSC.").append(ScoFaseSolicitacao.Fields.SLC_NUMERO.toString());
		hql.append(" FROM ").append(ScoFaseSolicitacao.class.getSimpleName()).append(" FSC, ").append(" WHERE FSC").append(ScoFaseSolicitacao.Fields.SLC_NUMERO.toString()).append(" = SLC.").append(ScoSolicitacaoDeCompra.Fields.NUMERO.toString());		 
		hql.append(" AND   FSC.").append(ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO.toString()).append(" IS NOT NULL ");       
		hql.append(" AND FSC.").append(ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString()).append(" ||'' = :indExclusao )");
		
        query = createHibernateQuery(hql.toString());
		query.setParameter("indExclusao", Boolean.FALSE);
		query.setParameter("codMaterial", codMaterial);
		List<Object[]> resultList = query.list();
		preencheHistoricoSolicitacaoEstocaveis(resultList, relatorioVO);
	}

	private void preencheHistoricoSolicitacaoEstocaveis(List<Object[]> resultList, RelatorioSolicitacaoCompraEstocavelVO relatorioVO) {
		List<SubRelatorioSolicitacoesEstocaveisVO> listaSubRelatorioSolicitacoesEstocaveisVO = new ArrayList<SubRelatorioSolicitacoesEstocaveisVO>();
		Date dataSolicitacao;
		String numeroAF = null;
		Date dataPrev;
		Integer item, quantidadeAF, quantidadeRecebida, lctNumero, numeroPAC, quantidadeSolicitada, numeroSolicitacao;
		DominioSituacaoAutorizacaoFornecedor st;
		String fornecedor;
		Short nroComplemento, itemShort;
		for (Object[] object : resultList) {
			if (listaSubRelatorioSolicitacoesEstocaveisVO.size() < 4){
				numeroSolicitacao = (Integer) object[11];
				dataSolicitacao = (Date) object[12];
				quantidadeSolicitada = (Integer) object[7];
				numeroPAC = (Integer) object[2];
	
				lctNumero = (Integer) object[2];
				nroComplemento = (Short) object[3];
						
				if (lctNumero != null && nroComplemento != null) {
					numeroAF = lctNumero.toString() + '/' + nroComplemento.toString();
				} else {	
					if (lctNumero != null) {
						numeroAF = lctNumero.toString();
					}
				}
	
				itemShort = (Short) object[4];
				if (itemShort != null) {
					item = itemShort.intValue();
				} else {
					item = null;
				}
	
				dataPrev = (Date) object[6];
				quantidadeAF = (Integer) object[7];
				quantidadeRecebida = (Integer) object[8];
				st = (DominioSituacaoAutorizacaoFornecedor) object[9];
				fornecedor = (String) object[10];
	
				SubRelatorioSolicitacoesEstocaveisVO subRelatorioSolicitacoesEstocaveisVO = new SubRelatorioSolicitacoesEstocaveisVO(
						numeroSolicitacao, dataSolicitacao, quantidadeSolicitada,
						numeroPAC, numeroAF, item, dataPrev, quantidadeAF,
						quantidadeRecebida, st.name(), fornecedor);
	
				listaSubRelatorioSolicitacoesEstocaveisVO.add(subRelatorioSolicitacoesEstocaveisVO);
			}
			relatorioVO.setSolicitacoesEstocaveis(listaSubRelatorioSolicitacoesEstocaveisVO);
		}
	}
	
	public Long listarSCItensSCSSVOCount(FiltroConsSCSSVO filtroConsultaSC) {
		return executeCriteriaCount(this.obterCriteriaConsultaItensSCSSVO(filtroConsultaSC));				
	}
	
	public List<ItensSCSSVO> listarSCItensSCSSVO(Integer firstResult, Integer maxResults, 
			String orderProperty,	boolean asc, FiltroConsSCSSVO filtroConsultaSC) {
		return executeCriteria(this.obterCriteriaConsultaItensSCSSVO(filtroConsultaSC), firstResult, maxResults, orderProperty, asc);				
	}
	
	public void obterCriteriaConsultaItensSCSSVOFlags(FiltroConsSCSSVO filtroConsultaSC, DetachedCriteria criteria){
		if (filtroConsultaSC.getIndEfetivada() != null) {
			criteria.add(Restrictions.eq("SC." + ScoSolicitacaoDeCompra.Fields.IND_EFETIVADA.toString(), filtroConsultaSC.getIndEfetivada()));
		}
		if (filtroConsultaSC.getIndExclusao() != null) {
			criteria.add(Restrictions.eq("SC." + ScoSolicitacaoDeCompra.Fields.IND_EXCLUSAO.toString(),filtroConsultaSC.getIndExclusao()));
		}
		if (filtroConsultaSC.getIndDevolucao() != null) {
			criteria.add(Restrictions.eq("SC." + ScoSolicitacaoDeCompra.Fields.IND_DEVOLUCAO.toString(),filtroConsultaSC.getIndDevolucao()));
		}
		if (filtroConsultaSC.getIndUrgente() != null) {
			criteria.add(Restrictions.eq("SC." + ScoSolicitacaoDeCompra.Fields.IND_URGENTE.toString(),filtroConsultaSC.getIndUrgente()));
		}
		if (filtroConsultaSC.getIndPrioridade() != null) {
			criteria.add(Restrictions.eq("SC." + ScoSolicitacaoDeCompra.Fields.PRIORIDADE.toString(),filtroConsultaSC.getIndPrioridade()));
		}
		if (filtroConsultaSC.getIndExclusivo() != null) {
			criteria.add(Restrictions.eq("SC." + ScoSolicitacaoDeCompra.Fields.IND_MAT_EXCLUSIVO.toString(),filtroConsultaSC.getIndExclusivo()));
		}
		if (filtroConsultaSC.getIndGeracaoAutomatica() != null) {
			criteria.add(Restrictions.eq("SC." + ScoSolicitacaoDeCompra.Fields.GERACAO_AUTOMATICA.toString(),filtroConsultaSC.getIndGeracaoAutomatica()));
		}
	}
	
	 public void obterCriteriaConsultaItensSCSSVODatas(FiltroConsSCSSVO filtroConsultaSC, DetachedCriteria criteria){
		 if (filtroConsultaSC.getDataInicioSolicitacao() != null) {
				criteria.add(Restrictions.ge("SC." + ScoSolicitacaoDeCompra.Fields.DATA_SOLICITACAO.toString(), filtroConsultaSC.getDataInicioSolicitacao()));
			}
		 	if (filtroConsultaSC.getDataFimSolicitacao() != null) {
				criteria.add(Restrictions.le("SC." + ScoSolicitacaoDeCompra.Fields.DATA_SOLICITACAO.toString(), filtroConsultaSC.getDataFimSolicitacao()));
			}
			if(filtroConsultaSC.getDataInicioAutorizacao() !=null){
				criteria.add(Restrictions.ge("SC." + ScoSolicitacaoDeCompra.Fields.DATA_AUTORIZACAO.toString(),filtroConsultaSC.getDataInicioAutorizacao()));			
			}
			if(filtroConsultaSC.getDataFimAutorizacao() !=null){
				criteria.add(Restrictions.le("SC." + ScoSolicitacaoDeCompra.Fields.DATA_AUTORIZACAO.toString(),filtroConsultaSC.getDataFimAutorizacao()));			
			}
			if(filtroConsultaSC.getDataInicioAnalise() !=null){
				criteria.add(Restrictions.ge("SC." + ScoSolicitacaoDeCompra.Fields.DT_ANALISE.toString(),filtroConsultaSC.getDataInicioAnalise()));			
			}
			if(filtroConsultaSC.getDataFimAnalise() !=null){
				criteria.add(Restrictions.le("SC." + ScoSolicitacaoDeCompra.Fields.DT_ANALISE.toString(),filtroConsultaSC.getDataFimAnalise()));			
			}
	 }
	
	 public void obterCriteriaConsultaItensSCSSVOPontoParada(FiltroConsSCSSVO filtroConsultaSC, DetachedCriteria criteria) {
		if (filtroConsultaSC.getPontoParadaAtual() != null){
			criteria.add(Restrictions.eq("PP_ATU." + ScoPontoParadaSolicitacao.Fields.CODIGO.toString(), filtroConsultaSC.getPontoParadaAtual().getCodigo()));
		}
		if (filtroConsultaSC.getPontoParadaAnterior() != null){
			criteria.add(Restrictions.eq("PP_ANT." + ScoPontoParadaSolicitacao.Fields.CODIGO.toString(), filtroConsultaSC.getPontoParadaAnterior().getCodigo()));
		}			
	}
	 
	public void obterCriteriaConsultaItensSCSSVOSC(FiltroConsSCSSVO filtroConsultaSC, DetachedCriteria criteria) {
		if (filtroConsultaSC.getNumero() != null && filtroConsultaSC.getNumeroFinal() != null) {
			criteria.add(Restrictions.ge("SC." + ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), filtroConsultaSC.getNumero()));
			criteria.add(Restrictions.le("SC." + ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), filtroConsultaSC.getNumeroFinal()));
		} else if (filtroConsultaSC.getNumero() != null && filtroConsultaSC.getNumeroFinal() == null) {
			criteria.add(Restrictions.eq("SC." + ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), filtroConsultaSC.getNumero()));			
		} else if (filtroConsultaSC.getNumero() == null && filtroConsultaSC.getNumeroFinal() != null) {
			criteria.add(Restrictions.eq("SC." + ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), filtroConsultaSC.getNumeroFinal()));
		}
		if (filtroConsultaSC.getCentroCustoSolicitante() != null) {
			criteria.add(Restrictions.eq("CC_REQ." + FccCentroCustos.Fields.CODIGO.toString(),	filtroConsultaSC.getCentroCustoSolicitante().getCodigo()));
		}
		if (filtroConsultaSC.getCentroCustoAplicacao() != null) {
			criteria.add(Restrictions.eq("CC_APP."	+ FccCentroCustos.Fields.CODIGO.toString(), filtroConsultaSC.getCentroCustoAplicacao().getCodigo()));
		}	
		if (StringUtils.isNotBlank(filtroConsultaSC.getDescricao())) {
			criteria.add(Restrictions.ilike("SC." + ScoSolicitacaoDeCompra.Fields.DESCRICAO.toString(), filtroConsultaSC.getDescricao(), MatchMode.ANYWHERE));
		}		
		if (filtroConsultaSC.getNroProjeto() != null) {
			criteria.add(Restrictions.eq("SC." + ScoSolicitacaoDeCompra.Fields.NRO_PROJETO.toString(),filtroConsultaSC.getNroProjeto()));
		}
		if (filtroConsultaSC.getNroInvestimento() != null) {
			criteria.add(Restrictions.eq("SC." + ScoSolicitacaoDeCompra.Fields.NRO_INVESTIMENTO.toString(),filtroConsultaSC.getNroInvestimento()));
		}
		if (filtroConsultaSC.getNaturezaDespesa() != null) {
			criteria.add(Restrictions.eq("SC." + ScoSolicitacaoDeCompra.Fields.NATUREZA_DESPESA.toString(),filtroConsultaSC.getNaturezaDespesa()));
		}
		if (filtroConsultaSC.getVerbaGestao() != null) {
			criteria.add(Restrictions.eq("SC." + ScoSolicitacaoDeCompra.Fields.VERBA_GESTAO.toString(),filtroConsultaSC.getVerbaGestao()));
		}			
		 
	 }  
	
	public void obterCriteriaConsultaItensSCSSVOPACAF(FiltroConsSCSSVO filtroConsultaSC, DetachedCriteria criteria) {
		if (filtroConsultaSC != null && filtroConsultaSC.getPesquisarPorPAC() != null && filtroConsultaSC.getPesquisarPorPAC()){
			DetachedCriteria subQueryPAC = this.getScoFaseSolicitacaoDAO().obterCriteriaFaseSolicitacaoLicitacao(filtroConsultaSC);
			subQueryPAC.add(Property.forName("SC." + ScoSolicitacaoDeCompra.Fields.NUMERO.toString()).eqProperty("FASES." + ScoFaseSolicitacao.Fields.SLC_NUMERO.toString()));
			criteria.add(Subqueries.exists(subQueryPAC));			
		}
		if (filtroConsultaSC != null && filtroConsultaSC.getPesquisarPorAF() != null && filtroConsultaSC.getPesquisarPorAF()){
			DetachedCriteria subQueryAF = this.getScoFaseSolicitacaoDAO().obterCriteriaFaseSolicitacaoAF(filtroConsultaSC);
			subQueryAF.add(Property.forName("SC." + ScoSolicitacaoDeCompra.Fields.NUMERO.toString()).eqProperty("FASES." + ScoFaseSolicitacao.Fields.SLC_NUMERO.toString()));
			criteria.add(Subqueries.exists(subQueryAF));			
		}
	}
	
	public DetachedCriteria obterCriteriaConsultaItensSCSSVO(FiltroConsSCSSVO filtroConsultaSC) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoSolicitacaoDeCompra.class, "SC");		
		criteria.setFetchMode(ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), FetchMode.JOIN);

		ProjectionList p = Projections.projectionList();

		p.add(Projections.property("SC." + ScoSolicitacaoDeCompra.Fields.NUMERO.toString()), ItensSCSSVO.Fields.NUMERO.toString());
		p.add(Projections.property("SC." + ScoSolicitacaoDeCompra.Fields.DESCRICAO.toString()), ItensSCSSVO.Fields.DESCRICAO.toString());
		p.add(Projections.property("SC." + ScoSolicitacaoDeCompra.Fields.DATA_SOLICITACAO.toString()), ItensSCSSVO.Fields.DT_SOLICITACAO.toString());
		p.add(Projections.property("CC_REQ." + FccCentroCustos.Fields.CODIGO.toString()), ItensSCSSVO.Fields.SEQ_CC_REQ.toString());
		p.add(Projections.property("CC_REQ." + FccCentroCustos.Fields.DESCRICAO.toString()), ItensSCSSVO.Fields.NOME_CC_REQ.toString());
		p.add(Projections.property("CC_APP." + FccCentroCustos.Fields.CODIGO.toString()), ItensSCSSVO.Fields.SEQ_CC_APLIC.toString());
		p.add(Projections.property("CC_APP." + FccCentroCustos.Fields.DESCRICAO.toString()), ItensSCSSVO.Fields.NOME_CC_APLIC.toString());
		p.add(Projections.property("PP_ATU." + ScoPontoParadaSolicitacao.Fields.CODIGO.toString()), ItensSCSSVO.Fields.SEQ_PONTO_PARADA_ATUAL.toString());
		p.add(Projections.property("PP_ATU." + ScoPontoParadaSolicitacao.Fields.DESCRICAO.toString()), ItensSCSSVO.Fields.NOME_PONTO_PARADA_ATUAL.toString());
		p.add(Projections.property("PP_ANT." + ScoPontoParadaSolicitacao.Fields.CODIGO.toString()), ItensSCSSVO.Fields.SEQ_PONTO_PARADA_ANTERIOR.toString());
		p.add(Projections.property("PP_ANT." + ScoPontoParadaSolicitacao.Fields.DESCRICAO.toString()), ItensSCSSVO.Fields.NOME_PONTO_PARADA_ANTERIOR.toString());
		p.add(Projections.property("MAT." + ScoMaterial.Fields.CODIGO.toString()), ItensSCSSVO.Fields.CODIGO_MATERIAL.toString());
		p.add(Projections.property("MAT." + ScoMaterial.Fields.NOME.toString()), ItensSCSSVO.Fields.NOME_MATERIAL.toString());
		p.add(Projections.property("MAT." + ScoMaterial.Fields.DESCRICAO.toString()), ItensSCSSVO.Fields.DESCRICAO_MATERIAL.toString());
		p.add(Projections.property("SC." + ScoSolicitacaoDeCompra.Fields.DESCRICAO.toString()), ItensSCSSVO.Fields.DESCRICAO_SC.toString());
		p.add(Projections.property("SC." + ScoSolicitacaoDeCompra.Fields.IND_EFETIVADA.toString()), ItensSCSSVO.Fields.EFETIVADA.toString());
		p.add(Projections.property("SC." + ScoSolicitacaoDeCompra.Fields.IND_EXCLUSAO.toString()), ItensSCSSVO.Fields.EXCLUSAO.toString());
		p.add(Projections.property("SC." + ScoSolicitacaoDeCompra.Fields.DATA_AUTORIZACAO.toString()), ItensSCSSVO.Fields.DT_AUTORIZACAO.toString());
		p.add(Projections.property("SC." + ScoSolicitacaoDeCompra.Fields.DT_EXCLUSAO.toString()), ItensSCSSVO.Fields.DT_EXCLUSAO.toString());
		p.add(Projections.property("FIS_SOL." + RapPessoasFisicas.Fields.NOME.toString()), ItensSCSSVO.Fields.NOME_SERVIDOR_GERACAO.toString());
		p.add(Projections.property("FIS_AUT." + RapPessoasFisicas.Fields.NOME.toString()), ItensSCSSVO.Fields.NOME_SERVIDOR_AUTORIZADOR.toString());
		p.add(Projections.property("FIS_EXC." + RapPessoasFisicas.Fields.NOME.toString()), ItensSCSSVO.Fields.NOME_SERVIDOR_EXCLUSAO.toString());
		p.add(Projections.sqlProjection("(SELECT COUNT(ass.sls_numero) FROM AGH.SCO_SOLICITACAO_COMPRA_SERVICO ass WHERE ass.slc_numero = {alias}.numero) AS "+ItensSCSSVO.Fields.QTD_SOLICITACOES_VINCULADAS.toString(), new String[]{ItensSCSSVO.Fields.QTD_SOLICITACOES_VINCULADAS.toString()}, new Type[]{IntegerType.INSTANCE}));
		p.add(Projections.sqlProjection("(SELECT MAX(fs_lic.itl_lct_numero) FROM AGH.SCO_FASES_SOLICITACOES fs_lic WHERE FS_LIC.TIPO = 'C' AND FS_LIC.ITL_LCT_NUMERO IS NOT NULL AND FS_LIC.IND_EXCLUSAO = 'N' AND FS_LIC.SLC_NUMERO = {alias}.NUMERO) AS "+ItensSCSSVO.Fields.LCT_NUMERO.toString(), new String[]{ItensSCSSVO.Fields.LCT_NUMERO.toString()}, new Type[]{IntegerType.INSTANCE}));
		p.add(Projections.sqlProjection("(SELECT MAX(fs_itl.itl_numero) FROM AGH.SCO_FASES_SOLICITACOES fs_itl WHERE FS_ITL.TIPO = 'C' AND FS_ITL.ITL_LCT_NUMERO IS NOT NULL AND FS_ITL.IND_EXCLUSAO = 'N' AND FS_ITL.ITL_LCT_NUMERO = (SELECT MAX(fs_lic2.itl_lct_numero) FROM AGH.SCO_FASES_SOLICITACOES fs_lic2 WHERE FS_LIC2.TIPO = 'C' AND FS_LIC2.ITL_LCT_NUMERO IS NOT NULL AND FS_LIC2.IND_EXCLUSAO = 'N' AND FS_LIC2.SLC_NUMERO = {alias}.NUMERO) AND FS_ITL.SLC_NUMERO = {alias}.NUMERO) AS "+ItensSCSSVO.Fields.ITL_NUMERO.toString(), new String[]{ItensSCSSVO.Fields.ITL_NUMERO.toString()}, new Type[]{IntegerType.INSTANCE}));
		p.add(Projections.sqlProjection("(SELECT MAX(mlc.descricao) FROM AGH.sco_licitacoes lic, AGH.sco_modalidade_licitacoes mlc WHERE lic.numero = (SELECT MAX(fs_lic3.itl_lct_numero) FROM AGH.SCO_FASES_SOLICITACOES fs_lic3 WHERE FS_LIC3.TIPO = 'C' AND FS_LIC3.ITL_LCT_NUMERO IS NOT NULL AND FS_LIC3.IND_EXCLUSAO = 'N' AND FS_LIC3.SLC_NUMERO = {alias}.NUMERO) AND lic.mlc_codigo = mlc.codigo) AS "+ItensSCSSVO.Fields.DESCRICAO_MODALIDADE_LICITACAO.toString(), new String[]{ItensSCSSVO.Fields.DESCRICAO_MODALIDADE_LICITACAO.toString()}, new Type[]{StringType.INSTANCE}));
		p.add(Projections.sqlProjection("(SELECT (CASE WHEN LIC2.IND_TIPO_PREGAO = 'P' THEN 'Presencial' WHEN LIC2.IND_TIPO_PREGAO = 'E' THEN 'Eletrônico' END) FROM AGH.sco_licitacoes lic2 WHERE lic2.numero = (SELECT MAX(fs_lic4.itl_lct_numero) FROM AGH.SCO_FASES_SOLICITACOES fs_lic4 WHERE FS_LIC4.TIPO = 'C' AND FS_LIC4.ITL_LCT_NUMERO IS NOT NULL AND FS_LIC4.IND_EXCLUSAO = 'N' AND FS_LIC4.SLC_NUMERO = {alias}.NUMERO)) AS "+ItensSCSSVO.Fields.DESCRICAO_MODALIDADE_PREGAO.toString(), new String[]{ItensSCSSVO.Fields.DESCRICAO_MODALIDADE_PREGAO.toString()}, new Type[]{StringType.INSTANCE}));
		p.add(Projections.sqlProjection("(SELECT MAX(af.pfr_lct_numero) FROM AGH.sco_fases_solicitacoes fs, AGH.sco_autorizacoes_forn af WHERE fs.tipo = 'C' AND fs.iaf_afn_numero = af.numero AND fs.ind_exclusao = 'N' AND fs.slc_numero = {alias}.NUMERO) AS "+ItensSCSSVO.Fields.NUMERO_AF.toString(), new String[]{ItensSCSSVO.Fields.NUMERO_AF.toString()}, new Type[]{IntegerType.INSTANCE}));
		p.add(Projections.sqlProjection("(SELECT MAX(afn.nro_complemento) FROM AGH.sco_autorizacoes_forn afn WHERE afn.numero = (SELECT MAX(af.numero) AS "+ItensSCSSVO.Fields.NUMERO_AF.toString()+" FROM AGH.sco_fases_solicitacoes fs, AGH.sco_autorizacoes_forn af WHERE fs.tipo = 'C' AND fs.iaf_afn_numero = af.numero AND fs.ind_exclusao = 'N' AND fs.slc_numero = {alias}.NUMERO)) AS "+ItensSCSSVO.Fields.COMPLEMENTO_AF.toString(), new String[]{ItensSCSSVO.Fields.COMPLEMENTO_AF.toString()}, new Type[]{IntegerType.INSTANCE}));
		p.add(Projections.sqlProjection("(SELECT frn.nome_fantasia FROM AGH.sco_autorizacoes_forn af, AGH.sco_fornecedores frn WHERE af.pfr_frn_numero = frn.numero AND af.numero = (SELECT MAX(af.numero) AS "+ItensSCSSVO.Fields.NUMERO_AF.toString()+" FROM AGH.sco_fases_solicitacoes fs, AGH.sco_autorizacoes_forn af WHERE fs.tipo = 'C' AND fs.iaf_afn_numero = af.numero AND fs.ind_exclusao = 'N' AND fs.slc_numero = {alias}.NUMERO)) AS "+ItensSCSSVO.Fields.NOME_FANTASIA_FORN_AF.toString(), new String[]{ItensSCSSVO.Fields.NOME_FANTASIA_FORN_AF.toString()}, new Type[]{StringType.INSTANCE}));
		p.add(Projections.sqlProjection("(SELECT count(*) FROM AGH.SCO_ARQUIVOS_ANEXOS AN WHERE AN.NUMERO = {alias}.NUMERO AND AN.TP_ORIGEM = 'SC') AS "+ItensSCSSVO.Fields.QTD_ANEXO.toString(), new String[]{ItensSCSSVO.Fields.QTD_ANEXO.toString()}, new Type[]{IntegerType.INSTANCE}));

		criteria.setProjection(p);

		criteria.createAlias("SC."+ScoSolicitacaoDeCompra.Fields.CENTRO_CUSTO.toString(), "CC_REQ");
		criteria.createAlias("SC."+ScoSolicitacaoDeCompra.Fields.CENTRO_CUSTO_APLICADA.toString(), "CC_APP");
		criteria.createAlias("SC."+ScoSolicitacaoDeCompra.Fields.LOCALIZACAO_PONTO_PARADA_ATUAL.toString(), "PP_ATU");
		criteria.createAlias("SC."+ScoSolicitacaoDeCompra.Fields.PONTO_PARADA_SOLICITACAO.toString(), "PP_ANT");
		criteria.createAlias("SC."+ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT");
		criteria.createAlias("SC."+ScoSolicitacaoDeCompra.Fields.SERVIDOR.toString(), "RAP_SOL");
		criteria.createAlias("RAP_SOL."+RapServidores.Fields.PESSOA_FISICA.toString(), "FIS_SOL");
		criteria.createAlias("SC."+ScoSolicitacaoDeCompra.Fields.AUTORIZANTE.toString(), "RAP_AUT", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("RAP_AUT."+RapServidores.Fields.PESSOA_FISICA.toString(), "FIS_AUT", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SC."+ScoSolicitacaoDeCompra.Fields.SERVIDOR_EXCLUSAO.toString(), "RAP_EXC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("RAP_EXC."+RapServidores.Fields.PESSOA_FISICA.toString(), "FIS_EXC", JoinType.LEFT_OUTER_JOIN);
		
		criteria.setResultTransformer(Transformers.aliasToBean(ItensSCSSVO.class));
		
		this.obterCriteriaConsultaItensSCSSVOFlags(filtroConsultaSC, criteria);
		this.obterCriteriaConsultaItensSCSSVODatas(filtroConsultaSC, criteria);
		this.obterCriteriaConsultaItensSCSSVOPontoParada(filtroConsultaSC, criteria);
        this.obterCriteriaConsultaItensSCSSVOSC(filtroConsultaSC,criteria); 
        this.obterCriteriaConsultaItensSCSSVOPACAF(filtroConsultaSC, criteria);			
        this.obterCriteriaConsultaMaterial(filtroConsultaSC, criteria);
		return criteria;
	}
	
	public void obterCriteriaConsultaMaterial(FiltroConsSCSSVO filtroConsultaSC, DetachedCriteria criteria) {
	if (filtroConsultaSC.getMaterial() != null) {
			criteria.add(Restrictions.eq("MAT." + ScoMaterial.Fields.CODIGO.toString(), filtroConsultaSC.getMaterial().getCodigo()));
		}
		if (filtroConsultaSC.getGrupoMaterial() != null) {
			criteria.createAlias("MAT."+ScoMaterial.Fields.GRUPO_MATERIAL.toString(), "GMT");
			criteria.add(Restrictions.eq("GMT." + ScoGrupoMaterial.Fields.CODIGO.toString(), filtroConsultaSC.getGrupoMaterial().getCodigo()));
		}
		if (filtroConsultaSC.getIndEstocavel() != null) {
			criteria.add(Restrictions.eq("MAT."	+ ScoMaterial.Fields.IND_ESTOCAVEL.toString(), filtroConsultaSC.getIndEstocavel().isSim()));
		}
		if (StringUtils.isNotBlank(filtroConsultaSC.getDescricao())) {
			criteria.add(Restrictions.or(Restrictions.ilike("MAT." + ScoMaterial.Fields.NOME.toString(), filtroConsultaSC.getDescricao(), MatchMode.ANYWHERE), Restrictions.ilike("MAT." + ScoMaterial.Fields.DESCRICAO.toString(), filtroConsultaSC.getDescricao(), MatchMode.ANYWHERE)));
		}		
     }
	
	
	public List<PreItemPacVO> listaIdentificacaoItensPac(ScoPontoParadaSolicitacao caixa, RapServidores comprador, ScoMaterial material, 
			boolean verificaFases, boolean excluido, boolean autorizada, boolean verficaPP, Integer numeroSCIni, Integer numeroSCFim){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoSolicitacaoDeCompra.class, "SC");
		criteria.createAlias(ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT"); 
		criteria.createAlias(ScoSolicitacaoDeCompra.Fields.LOCALIZACAO_PONTO_PARADA_ATUAL.toString(), "PP"); 
		criteria.createAlias(ScoSolicitacaoDeCompra.Fields.CENTRO_CUSTO_APLICADA.toString(), "CC_AP");
		criteria.createAlias(ScoSolicitacaoDeCompra.Fields.CENTRO_CUSTO.toString(), "CC_SOL");
		criteria.createAlias(ScoSolicitacaoDeCompra.Fields.SERVIDOR_COMPRA.toString(), "CC_RAP", JoinType.LEFT_OUTER_JOIN);
		ProjectionList projection = Projections.projectionList()
				.add(Projections.property("SC."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString()), PreItemPacVO.Fields.NUMERO.toString())
				.add(Projections.property("MAT."+ScoMaterial.Fields.CODIGO.toString()),PreItemPacVO.Fields.COD_MAT_SERV.toString())
				.add(Projections.property("MAT."+ScoMaterial.Fields.NOME.toString()), PreItemPacVO.Fields.NOME_MAT_SERV.toString())
				.add(Projections.property("SC."+ScoSolicitacaoDeCompra.Fields.IND_URGENTE.toString()), PreItemPacVO.Fields.IND_URGENTE.toString())
				.add(Projections.property("MAT."+ScoMaterial.Fields.IND_ESTOCAVEL.toString()), PreItemPacVO.Fields.IND_ESTOCAVEL.toString())
				.add(Projections.property("SC."+ScoSolicitacaoDeCompra.Fields.QUANTIDADE_APROVADA.toString()), PreItemPacVO.Fields.QTD_SC.toString())
				.add(Projections.property("SC."+ScoSolicitacaoDeCompra.Fields.VALOR_UNIT_PREVISTO.toString()), PreItemPacVO.Fields.VALOR_UNIT_PREVISTO.toString())
				.add(Projections.property("PP."+ScoPontoParadaSolicitacao.Fields.CODIGO.toString()), PreItemPacVO.Fields.CAIXA_COD.toString())
				.add(Projections.property("PP."+ScoPontoParadaSolicitacao.Fields.DESCRICAO.toString()), PreItemPacVO.Fields.CAIXA_DESC.toString())
				.add(Projections.property("CC_AP."+FccCentroCustos.Fields.CODIGO.toString()), PreItemPacVO.Fields.CC_APLICACAO_COD.toString())
				.add(Projections.property("CC_AP."+FccCentroCustos.Fields.DESCRICAO.toString()), PreItemPacVO.Fields.CC_APLICACAO_DESC.toString())
				.add(Projections.property("CC_SOL."+FccCentroCustos.Fields.CODIGO.toString()), PreItemPacVO.Fields.CC_SOLICITANTE_COD.toString())
				.add(Projections.property("CC_SOL."+FccCentroCustos.Fields.DESCRICAO.toString()), PreItemPacVO.Fields.CC_SOLICITANTE_DESC.toString());
				criteria.setProjection(projection);	
				
 		if(material != null) {
			criteria.add(Restrictions.eq("SC."+ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), material));
		}
		
		if (caixa != null) {
			criteria.add(Restrictions.eq("SC."+ScoSolicitacaoDeCompra.Fields.PPS_CODIGO_LOC_PROXIMA.toString(), caixa.getCodigo().shortValue()));
		}
		else if (material == null && verficaPP){
			criteria.addOrder(Order.desc("SC."+ScoSolicitacaoDeCompra.Fields.PPS_CODIGO_LOC_PROXIMA.toString()));
		}

		if (comprador != null) {
			criteria.add(Restrictions.eq("CC_RAP."+RapServidores.Fields.ID.toString(), comprador.getId()));
		}
		if (excluido){
			criteria.add(Restrictions.eq("SC."+ScoSolicitacaoDeCompra.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		}
		if (autorizada){
			criteria.add(Restrictions.isNotNull("SC."+ScoSolicitacaoDeCompra.Fields.DATA_AUTORIZACAO.toString()));
		}
		if (verificaFases){
			DetachedCriteria subQueryFasesSolicitacoes = DetachedCriteria.forClass(ScoFaseSolicitacao.class , "FS");
			ProjectionList projectionListSubQueryFasesSolicitacoes = Projections.projectionList()
			.add(Projections.property("FS."+ScoFaseSolicitacao.Fields.SLC_NUMERO.toString()));
			subQueryFasesSolicitacoes.setProjection(projectionListSubQueryFasesSolicitacoes);
			subQueryFasesSolicitacoes.add(Restrictions.eqProperty("FS."+ScoFaseSolicitacao.Fields.SLC_NUMERO.toString(), "SC."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString()));
			subQueryFasesSolicitacoes.add(Restrictions.eq("FS."+ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
			criteria.add(Subqueries.propertyNotIn("SC."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), subQueryFasesSolicitacoes));
		}	
		
		if (numeroSCIni!=null && numeroSCFim!=null ){			
			criteria.add(Restrictions.between("SC."+ScoSolicitacaoServico.Fields.NUMERO.toString(), numeroSCIni, numeroSCFim));
		}
		else if (numeroSCIni!=null && numeroSCFim==null ) {
			criteria.add(Restrictions.eq("SC."+ScoSolicitacaoServico.Fields.NUMERO.toString(), numeroSCIni));
			
		} else  if (numeroSCIni==null && numeroSCFim!=null ) {			
			criteria.add(Restrictions.eq("SC."+ScoSolicitacaoServico.Fields.NUMERO.toString(), numeroSCFim));
		}
		else {
			criteria.add(Restrictions.eq("PP."+ScoPontoParadaSolicitacao.Fields.TIPO.toString(), DominioTipoPontoParada.CP));
		}
		criteria.addOrder(Order.asc("SC."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(PreItemPacVO.class));
		return executeCriteria(criteria);
	}

	/**
	 * Retorna uma lista de solicitacoes de compras de determinado material, natureza de despesa e verba de gestão pesquisando
	 * por codigo e descrição da solicitação de compras
	 * @param filter
	 * @param material
	 * @param naturezaDespesa
	 * @param verbaGestao
	 * @return List
	 */
	public List<ScoSolicitacaoDeCompra> pesquisarSolicCompraCodigoDescricao(
			Object filter, ScoMaterial material, FsoNaturezaDespesa naturezaDespesa) {
		return this.executeCriteria(new ScPorCodigoDescricaoQueryBuilder().build(filter, material, naturezaDespesa));
	}

	/**
	 * Retorna uma lista de solicitacoes de compras que estao associadas a natureza de despesa
	 * @param id
	 * @param isExclusao
	 * @return List
	 */
	public List<ScoSolicitacaoDeCompra> buscarSolicitacaoCompraAssociadaNaturezaDespesa(FsoNaturezaDespesaId id, Boolean isExclusao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoSolicitacaoDeCompra.class, "SLC");
		criteria.createCriteria("SLC."+ScoSolicitacaoDeCompra.Fields.NATUREZA_DESPESA.toString(), "NAT", JoinType.INNER_JOIN);
		//criteria.getExecutableCriteria(getSession()).setMaxResults(10);
		criteria.addOrder(Order.asc("SLC."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString()));
		if (id.getGndCodigo() != null) {
			criteria.add(Restrictions.eq("NAT."+FsoNaturezaDespesa.Fields.GND_CODIGO.toString(), id.getGndCodigo()));
		}
		if (id.getCodigo() != null) {
			criteria.add(Restrictions.eq("NAT."+FsoNaturezaDespesa.Fields.CODIGO.toString(), id.getCodigo()));
		}
		if (isExclusao) {
			criteria.add(Restrictions.eq("SLC."+ScoSolicitacaoDeCompra.Fields.IND_EXCLUSAO.toString(),	Boolean.FALSE));
		}
		return executeCriteria(criteria,0,10,null);
	}

	public ScoSolicitacaoDeCompra obterSolicitacaoCompraPorNumero(Integer numero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoSolicitacaoDeCompra.class, "SLC");
		
		criteria.createAlias("SLC."+ScoSolicitacaoDeCompra.Fields.PONTO_PARADA_SOLICITACAO.toString(), "PP"); 
		
		if (numero != null) {
			criteria.add(Restrictions.eq(ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), numero));
		}
		List<ScoSolicitacaoDeCompra> lista = executeCriteria(criteria);
		return lista.get(0);
	}


	/**
	 * Retorna uma lista de solicitacoes de compras ativas em AF que estao associadas a natureza de despesa
	 * @param id
	 * @return List
	 */
	public List<ScoSolicitacaoDeCompra> buscarSolicitacaoCompraAssociadaAutorizacaoFornEfetivada(FsoNaturezaDespesaId id) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoSolicitacaoDeCompra.class, "SLC");
	//	criteria.getExecutableCriteria(getSession()).setMaxResults(10);
		criteria.createCriteria("SLC."+ScoSolicitacaoDeCompra.Fields.NATUREZA_DESPESA.toString(), "NAT", JoinType.INNER_JOIN);
		criteria.addOrder(Order.asc("SLC."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString()));
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
		.add(Projections.property("FS."+ScoFaseSolicitacao.Fields.SLC_NUMERO.toString()));
		subQueryFasesSolicitacoes.setProjection(projectionListSubQueryFasesSolicitacoes);
		subQueryFasesSolicitacoes.add(Restrictions.eq("FS."+ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));		
		subQueryFasesSolicitacoes.add(Restrictions.eqProperty("FS."+ScoFaseSolicitacao.Fields.SLC_NUMERO.toString(),
				"SLC."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString()));		
		subQueryFasesSolicitacoes.add(Restrictions.eq("AF."+ScoAutorizacaoForn.Fields.SITUACAO.toString(), DominioSituacaoAutorizacaoFornecimento.EF));
		
		criteria.add(Subqueries.propertyIn("SLC."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), subQueryFasesSolicitacoes));
		criteria.add(Restrictions.eq("SLC."+ScoSolicitacaoDeCompra.Fields.IND_EXCLUSAO.toString(),	Boolean.FALSE));
		
		return executeCriteria(criteria,0,10,null);
	}

	/**
	 * Busca uma lista de 10 verbas de gestões associadas à SCs
	 * @param verbaGestao
	 * @return
	 */
	public List<ScoSolicitacaoDeCompra> buscarSolicitacaoComprasAssociadaVerbaGestao(FsoVerbaGestao verbaGestao, Boolean filtraEfetivada) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoSolicitacaoDeCompra.class, "SLC");
		//criteria.getExecutableCriteria(getSession()).setMaxResults(10);
		criteria.addOrder(Order.asc(ScoSolicitacaoDeCompra.Fields.NUMERO.toString()));
		if (verbaGestao != null) {
			criteria.add(Restrictions.eq(ScoSolicitacaoDeCompra.Fields.VERBA_GESTAO.toString(), verbaGestao));
		}
		if (filtraEfetivada) {
			criteria.add(Restrictions.eq(ScoSolicitacaoDeCompra.Fields.IND_EFETIVADA.toString(), Boolean.FALSE));
		}
		return executeCriteria(criteria,0,10,null);
	}

	/**
	 * Pesquisa solicitacoes de compras ativas associadas a um item de af
	 * @param itemAfId
	 * @return List
	 */
	public List<ScoSolicitacaoDeCompra> pesquisarSolicitacaoComprasPorItemAf(ScoItemAutorizacaoFornId  itemAfId) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoSolicitacaoDeCompra.class, "SLC");
		criteria.createAlias("SLC."+ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT");
		criteria.createAlias("SLC."+ScoSolicitacaoDeCompra.Fields.UNIDADE_MEDIDA.toString(), "UN");
		DetachedCriteria subQueryFasesSolicitacoes = DetachedCriteria.forClass(ScoFaseSolicitacao.class , "FS");
		subQueryFasesSolicitacoes.createAlias("FS."+ScoFaseSolicitacao.Fields.SCO_ITENS_AUTORIZACAO_FORN.toString(), "IAF", JoinType.INNER_JOIN);
		subQueryFasesSolicitacoes.add(Restrictions.eq("IAF."+ScoItemAutorizacaoForn.Fields.ID_AUT_FORN_NUMERO.toString(), itemAfId.getAfnNumero()));
		subQueryFasesSolicitacoes.add(Restrictions.eq("IAF."+ScoItemAutorizacaoForn.Fields.NUMERO.toString(), itemAfId.getNumero()));
		ProjectionList projectionListSubQueryFasesSolicitacoes = Projections.projectionList()
		.add(Projections.property("FS."+ScoFaseSolicitacao.Fields.SLC_NUMERO.toString()));
		subQueryFasesSolicitacoes.setProjection(projectionListSubQueryFasesSolicitacoes);
		subQueryFasesSolicitacoes.add(Restrictions.eq("FS."+ScoFaseSolicitacao.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));		
		subQueryFasesSolicitacoes.add(Restrictions.eqProperty("FS."+ScoFaseSolicitacao.Fields.SLC_NUMERO.toString(),
				"SLC."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString()));		
		criteria.add(Subqueries.propertyIn("SLC."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), subQueryFasesSolicitacoes));
		criteria.add(Restrictions.eq("SLC."+ScoSolicitacaoDeCompra.Fields.IND_EXCLUSAO.toString(),	Boolean.FALSE));
		return executeCriteria(criteria);
	}
	
	private ScoFaseSolicitacaoDAO getScoFaseSolicitacaoDAO() {
		return aScoFaseSolicitacaoDAO;
	}
	
	
	private DetachedCriteria obterCriteriaPesquisaScMaterialEstocavel(Integer seqProcesso) {
		Set<Integer> listaNumeros = new HashSet<Integer>();
		
		// primeiro pega os numero das com contrato
		DetachedCriteria criteriaUnion1 = DetachedCriteria.forClass(ScoSolicitacaoDeCompra.class, "SLC");
		criteriaUnion1.add(Restrictions.eq("SLC."+ScoSolicitacaoDeCompra.Fields.IND_EXCLUSAO.toString(), false));
		
		DetachedCriteria subQueryContrato = DetachedCriteria.forClass(ScoLogGeracaoScMatEstocavel.class, "LOG");
		subQueryContrato.setProjection(Projections.property("LOG."+ScoLogGeracaoScMatEstocavel.Fields.SLC_NUMERO.toString()));
		subQueryContrato.add(Restrictions.eq("LOG."+ScoLogGeracaoScMatEstocavel.Fields.SEQ_PROCESSO.toString(), seqProcesso));
		subQueryContrato.add(Restrictions.gt("LOG."+ScoLogGeracaoScMatEstocavel.Fields.QTDE_A_COMPRAR.toString(), 0));

		criteriaUnion1.add(Subqueries.propertyIn("SLC."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), subQueryContrato));
		
		List<ScoSolicitacaoDeCompra> listaUnion1 = this.executeCriteria(criteriaUnion1);
		for(ScoSolicitacaoDeCompra slc : listaUnion1) {
			listaNumeros.add(slc.getNumero());
		}
		
		// depois pega os numeros das criadas pela geracao automatica
		DetachedCriteria criteriaUnion2 = DetachedCriteria.forClass(ScoSolicitacaoDeCompra.class, "SLC");
		criteriaUnion2.add(Restrictions.eq("SLC."+ScoSolicitacaoDeCompra.Fields.IND_EXCLUSAO.toString(), false));
		
		DetachedCriteria subQueryProcNovo = DetachedCriteria.forClass(ScoLogGeracaoScMatEstocavel.class, "LOG2");
		subQueryProcNovo.setProjection(Projections.property("LOG2."+ScoLogGeracaoScMatEstocavel.Fields.MAT_CODIGO.toString()));
		subQueryProcNovo.add(Restrictions.eq("LOG2."+ScoLogGeracaoScMatEstocavel.Fields.SEQ_PROCESSO.toString(), seqProcesso));
		subQueryProcNovo.add(Restrictions.gt("LOG2."+ScoLogGeracaoScMatEstocavel.Fields.QTDE_A_COMPRAR.toString(), 0));
		
		DetachedCriteria subQueryNovas = DetachedCriteria.forClass(ScoSolicitacaoDeCompra.class, "SLCN");
		subQueryNovas.setProjection(Projections.property("SLCN." + ScoSolicitacaoDeCompra.Fields.NUMERO.toString()));
		subQueryNovas.add(Restrictions.eq("SLCN."+ ScoSolicitacaoDeCompra.Fields.GERACAO_AUTOMATICA.toString(), true));
		subQueryNovas.add(Restrictions.eq("SLCN."+ ScoSolicitacaoDeCompra.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));
		
		if (isOracle()) {
			subQueryNovas.add(Restrictions.sqlRestriction("TRUNC(SLCN_.DT_SOLICITACAO) = TRUNC((SELECT MAX(LOG3_.DT_GERACAO) FROM AGH.SCO_LOG_GERAC_SC_MAT_ESTOCAVEL LOG3_ WHERE LOG3_.SEQ_PROCESSO = "+seqProcesso+"))"));
		} else {
			subQueryNovas.add(Restrictions.sqlRestriction("DATE(SLCN_.DT_SOLICITACAO) = DATE((SELECT MAX(LOG3_.DT_GERACAO) FROM AGH.SCO_LOG_GERAC_SC_MAT_ESTOCAVEL LOG3_ WHERE LOG3_.SEQ_PROCESSO = "+seqProcesso+"))"));
		}
		
		subQueryNovas.add(Subqueries.propertyIn("SLCN."+ ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), subQueryProcNovo));
		
		criteriaUnion2.add(Subqueries.propertyIn("SLC."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), subQueryNovas));
		
		List<ScoSolicitacaoDeCompra> listaUnion2 = this.executeCriteria(criteriaUnion2);
		for(ScoSolicitacaoDeCompra slc : listaUnion2) {
			listaNumeros.add(slc.getNumero());
		}
		
		// entao faz uma query simples. tudo isso porque nesta versao do hibernate nao tem union. eh mais rapido (na execucao) fazer assim
		// do que fazer com OR e quebrar indices no oracle.
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoSolicitacaoDeCompra.class, "SLC");
		criteria.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), "MAT", JoinType.INNER_JOIN);
		criteria.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.CENTRO_CUSTO.toString(), "CC_SOLIC",JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SLC." + ScoSolicitacaoDeCompra.Fields.CENTRO_CUSTO_APLICADA.toString(), "CC_APLIC",JoinType.LEFT_OUTER_JOIN);
		
		
		criteria.add(Restrictions.eq("SLC."+ScoSolicitacaoDeCompra.Fields.IND_EXCLUSAO.toString(), false));
		if (listaNumeros.size() > 0) {
			criteria.add(Restrictions.in("SLC."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), listaNumeros));
		} else {
			criteria.add(Restrictions.eq("SLC."+ScoSolicitacaoDeCompra.Fields.NUMERO.toString(), -1));
		}
		
		return criteria;
		
	}

	
	public List<ScoSolicitacaoDeCompra> pesquisarSCMaterialEstocavel(Integer firstResult,Integer maxResult,	String orderProperty,
			boolean asc,Integer seqProcesso) {
		return this.executeCriteria(obterCriteriaPesquisaScMaterialEstocavel(seqProcesso).
				addOrder(Order.asc("SLC." + ScoSolicitacaoDeCompra.Fields.NUMERO.toString())), firstResult, maxResult, orderProperty, asc);		
	}	
	
	public Long pesquisarSCMaterialEstocavelCount(Integer seqProcesso) {
		return this.executeCriteriaCount(obterCriteriaPesquisaScMaterialEstocavel(seqProcesso));	
	}	

	/**
	 * Obtem SC a partir do log de geração.
	 * 
	 * @param item Log
	 * @return ID da SC
	 */
	public Integer obterSolicitacaoDeCompra(ScoLogGeracaoScMatEstocavel item) {
		Integer slcNumero = null;
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoSolicitacaoDeCompra.class, "SLC");
		criteria.setProjection(Projections.property("SLC." + ScoSolicitacaoDeCompra.Fields.NUMERO.toString()));
		criteria.add(Restrictions.eq("SLC."+ ScoSolicitacaoDeCompra.Fields.MATERIAL.toString(), item.getMaterial()));
		criteria.add(Restrictions.eq("SLC."+ ScoSolicitacaoDeCompra.Fields.GERACAO_AUTOMATICA.toString(), true));
		criteria.add(Restrictions.eq("SLC."+ ScoSolicitacaoDeCompra.Fields.IND_EXCLUSAO.toString(), Boolean.FALSE));

		criteria.add(Restrictions.ge("SLC." + ScoSolicitacaoDeCompra.Fields.DATA_SOLICITACAO.toString(), 
				DateUtil.obterDataComHoraInical(item.getDtGeracao())));

		criteria.add(Restrictions.le("SLC." + ScoSolicitacaoDeCompra.Fields.DATA_SOLICITACAO.toString(), 
				DateUtil.obterDataComHoraFinal(item.getDtGeracao())));
		
		List<Integer> listaResultado = executeCriteria(criteria);
		
		if (listaResultado != null && !listaResultado.isEmpty()) {
			slcNumero = listaResultado.get(0);
		}
		return slcNumero; 
	}

	public List<ScoSolicitacaoDeCompra> pesquisarScsNaoAf(ScoMaterial material) {
		assert material != null : "Material não definido";
		return pesquisarScsNaoAf(material.getCodigo());
	}

	@SuppressWarnings("unchecked")
	public List<ScoSolicitacaoDeCompra> pesquisarScsNaoAf(Integer materialId) {
		assert materialId != null : "ID do material não definido";
		
		// Query Parametrizada
		Query query = createHibernateQuery(getScsNaoAfCriteria());
		query.setInteger(MAT, materialId);
		query.setBoolean(EXCLUSAO, false);
		query.setBoolean(FUNDO_FIXO, false);
		
		// Resultado
		return query.list();
	}
	
	private String getScsNaoAfCriteria() {
		return new StringBuilder()
			.append(" from ").append(ScoSolicitacaoDeCompra.class.getName()).append(' ').append(SC)
			.append(" where ").append(SC).append('.').append(ScoSolicitacaoDeCompra.Fields.MAT_CODIGO).append(" = :").append(MAT)
			.append(" and ").append(SC).append('.').append(ScoSolicitacaoDeCompra.Fields.IND_EXCLUSAO).append(" = :").append(EXCLUSAO)
			.append(" and ").append(SC).append('.').append(ScoSolicitacaoDeCompra.Fields.IND_FUNDO_FIXO).append(" = :").append(FUNDO_FIXO)
			.append(" and not exists(select 1 from ").append(ScoFaseSolicitacao.class.getName()).append(' ').append(FSL)
			.append(" where ").append(FSL).append('.').append(ScoFaseSolicitacao.Fields.SOLICITACAO_COMPRAS).append('=').append(SC)
			.append(" and ").append(FSL).append('.').append(ScoFaseSolicitacao.Fields.IND_EXCLUSAO)
			.append(" = :").append(EXCLUSAO).append(" and ").append(FSL).append('.').append(ScoFaseSolicitacao.Fields.IAF_AFN_NUMERO)
			.append(" is not null").append(')').append(" and not exists(select 1 from ").append(ScoSolicitacaoProgramacaoEntrega.class.getName()).append(' ').append(SPE)
			.append(" where ").append(SPE).append('.').append(ScoSolicitacaoProgramacaoEntrega.Fields.SOLICITACAO_COMPRA)
			.append('=').append(SC).append(')').toString();
	}
}