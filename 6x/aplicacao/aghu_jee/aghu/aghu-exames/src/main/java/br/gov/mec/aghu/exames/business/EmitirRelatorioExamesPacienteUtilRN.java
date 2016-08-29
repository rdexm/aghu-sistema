/**
 * 
 */
package br.gov.mec.aghu.exames.business;

import static br.gov.mec.aghu.exames.business.DominioVariaveisEmitirRelatorioExamesPaciente.LISTA_AEL_POL_SUM_EXAME_TAB;
import static br.gov.mec.aghu.exames.business.DominioVariaveisEmitirRelatorioExamesPaciente.LISTA_AEL_POL_SUM_LEGENDA;
import static br.gov.mec.aghu.exames.business.DominioVariaveisEmitirRelatorioExamesPaciente.LISTA_AEL_POL_SUM_MASC_CAMPO_EDIT;
import static br.gov.mec.aghu.exames.business.DominioVariaveisEmitirRelatorioExamesPaciente.LISTA_AEL_POL_SUM_MASC_LINHA;
import static br.gov.mec.aghu.exames.business.DominioVariaveisEmitirRelatorioExamesPaciente.V_CAL_NOME;
import static br.gov.mec.aghu.exames.business.DominioVariaveisEmitirRelatorioExamesPaciente.V_CAL_NOME_SUMARIO;
import static br.gov.mec.aghu.exames.business.DominioVariaveisEmitirRelatorioExamesPaciente.V_DESCRICAO_CARACTERISTICA;
import static br.gov.mec.aghu.exames.business.DominioVariaveisEmitirRelatorioExamesPaciente.V_DESCRICAO_CODIFICADO;
import static br.gov.mec.aghu.exames.business.DominioVariaveisEmitirRelatorioExamesPaciente.V_DESCRICAO_NOTAS_ADICIONAIS;
import static br.gov.mec.aghu.exames.business.DominioVariaveisEmitirRelatorioExamesPaciente.V_DESCRICAO_RESULTADO;
import static br.gov.mec.aghu.exames.business.DominioVariaveisEmitirRelatorioExamesPaciente.V_DTHR_EVENTO_AREA_EXEC;
import static br.gov.mec.aghu.exames.business.DominioVariaveisEmitirRelatorioExamesPaciente.V_DTHR_FIM_EXAMES_TAB;
import static br.gov.mec.aghu.exames.business.DominioVariaveisEmitirRelatorioExamesPaciente.V_EST_CAMPO_EDIT;
import static br.gov.mec.aghu.exames.business.DominioVariaveisEmitirRelatorioExamesPaciente.V_EST_EXAME_TAB;
import static br.gov.mec.aghu.exames.business.DominioVariaveisEmitirRelatorioExamesPaciente.V_EST_ITENS_ATU;
import static br.gov.mec.aghu.exames.business.DominioVariaveisEmitirRelatorioExamesPaciente.V_EST_LEGENDA;
import static br.gov.mec.aghu.exames.business.DominioVariaveisEmitirRelatorioExamesPaciente.V_EST_LV_NRO_TAB_POR_PRNTEGENDA;
import static br.gov.mec.aghu.exames.business.DominioVariaveisEmitirRelatorioExamesPaciente.V_EST_MASC_LINHA;
import static br.gov.mec.aghu.exames.business.DominioVariaveisEmitirRelatorioExamesPaciente.V_LEGENDA;
import static br.gov.mec.aghu.exames.business.DominioVariaveisEmitirRelatorioExamesPaciente.V_LTO_LTO_ID;
import static br.gov.mec.aghu.exames.business.DominioVariaveisEmitirRelatorioExamesPaciente.V_NRO_CAMPO;
import static br.gov.mec.aghu.exames.business.DominioVariaveisEmitirRelatorioExamesPaciente.V_NRO_LINHA;
import static br.gov.mec.aghu.exames.business.DominioVariaveisEmitirRelatorioExamesPaciente.V_NRO_TAB_POR_PRNT;
import static br.gov.mec.aghu.exames.business.DominioVariaveisEmitirRelatorioExamesPaciente.V_NRO_TAB_POR_PRNTV_LEGENDA;
import static br.gov.mec.aghu.exames.business.DominioVariaveisEmitirRelatorioExamesPaciente.V_OBSERVACAO;
import static br.gov.mec.aghu.exames.business.DominioVariaveisEmitirRelatorioExamesPaciente.V_ORDEM_EXAMES_TAB;
import static br.gov.mec.aghu.exames.business.DominioVariaveisEmitirRelatorioExamesPaciente.V_ORDEM_REL;
import static br.gov.mec.aghu.exames.business.DominioVariaveisEmitirRelatorioExamesPaciente.V_REE_VALOR;
import static br.gov.mec.aghu.exames.business.DominioVariaveisEmitirRelatorioExamesPaciente.V_RESULTADO_AUX;
import static br.gov.mec.aghu.exames.business.DominioVariaveisEmitirRelatorioExamesPaciente.V_RESULTADO_COM_MASCARA;
import static br.gov.mec.aghu.exames.business.DominioVariaveisEmitirRelatorioExamesPaciente.V_SEQP_ANT;
import static br.gov.mec.aghu.exames.business.DominioVariaveisEmitirRelatorioExamesPaciente.V_SEQ_DEPT_ANT;
import static br.gov.mec.aghu.exames.business.DominioVariaveisEmitirRelatorioExamesPaciente.V_SOE_SEQP_ANT;
import static br.gov.mec.aghu.exames.business.DominioVariaveisEmitirRelatorioExamesPaciente.V_SOE_SEQP_DEPT_ANT;
import static br.gov.mec.aghu.exames.business.DominioVariaveisEmitirRelatorioExamesPaciente.V_VALOR;
import static br.gov.mec.aghu.exames.business.DominioVariaveisEmitirRelatorioExamesPaciente.V_VERSAO_EXA_SIGLA;
import static br.gov.mec.aghu.exames.business.DominioVariaveisEmitirRelatorioExamesPaciente.V_VERSAO_MAN_SEQ;
import static br.gov.mec.aghu.exames.business.DominioVariaveisEmitirRelatorioExamesPaciente.V_VERSAO_SEQP;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioObjetoVisual;
import br.gov.mec.aghu.dominio.DominioSumarioExame;
import br.gov.mec.aghu.dominio.DominioTipoCampoCampoLaudo;
import br.gov.mec.aghu.exames.dao.AelResultadoCaracteristicaDAO;
import br.gov.mec.aghu.exames.dao.AelResultadoCodificadoDAO;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.model.AelExtratoItemSolicitacao;
import br.gov.mec.aghu.model.AelNotaAdicional;
import br.gov.mec.aghu.model.AelParametroCamposLaudo;
import br.gov.mec.aghu.model.AelPolSumExameTab;
import br.gov.mec.aghu.model.AelPolSumLegenda;
import br.gov.mec.aghu.model.AelPolSumMascCampoEdit;
import br.gov.mec.aghu.model.AelPolSumMascLinha;
import br.gov.mec.aghu.model.AelResultadoCaracteristica;
import br.gov.mec.aghu.model.AelResultadoCodificado;
import br.gov.mec.aghu.model.AelResultadoExame;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * @author aghu
 * 
 *         Implementação das procedures internas chamadas pela procedure
 *         AELK_POL_SUM_EXAMES + métodos utilitários para atender a procedure
 *
 */
@Stateless
public class EmitirRelatorioExamesPacienteUtilRN extends BaseBusiness {

    @EJB
    private EmitirRelatorioExamesPacientePopularListasUtilRN emitirRelatorioExamesPacientePopularListasUtilRN;

    @EJB
    private MascaraExamesRN mascaraExamesRN;

    private static final Log LOG = LogFactory.getLog(EmitirRelatorioExamesPacienteUtilRN.class);

    @Override
    @Deprecated
    protected Log getLogger() {
	return LOG;
    }


    @EJB
    private ISolicitacaoExameFacade solicitacaoExameFacade;

    @Inject
    private AelResultadoCodificadoDAO aelResultadoCodificadoDAO;

    @EJB
    private IParametroFacade parametroFacade;

    @Inject
    private AelResultadoCaracteristicaDAO aelResultadoCaracteristicaDAO;

    private static final long serialVersionUID = -3949690073502330065L;

    protected IParametroFacade getParametroFacade() {
	return this.parametroFacade;
    }

    protected ISolicitacaoExameFacade getSolicitacaoExameFacade() {
	return this.solicitacaoExameFacade;
    }

    protected AelResultadoCodificadoDAO getAelResultadoCodificadoDAO() {
	return aelResultadoCodificadoDAO;
    }


    private MascaraExamesRN getMascaraExamesRN() {
	return mascaraExamesRN;
    }

    protected AelResultadoCaracteristicaDAO getAelResultadoCaracteristicaDAO() {
	return aelResultadoCaracteristicaDAO;
    }

    protected EmitirRelatorioExamesPacientePopularListasUtilRN getEmitirRelatorioExamesPacientePopularListasUtilRN() {
	return emitirRelatorioExamesPacientePopularListasUtilRN;
    }

    /**
     * 
     * @param resultadoExames
     * @param mapVariaveis
     * @return
     */
    @SuppressWarnings("unchecked")
    public void manipularResultadoExames(List<AelResultadoExame> resultadoExames, Map<String, Object> mapVariaveis,
	    Map<String, Object> mapLista) {
	List<AelPolSumLegenda> listaAelPolSumLegenda = (List<AelPolSumLegenda>) mapLista.get(LISTA_AEL_POL_SUM_LEGENDA);
	List<AelPolSumExameTab> listaAelPolSumExameTab = (List<AelPolSumExameTab>) mapLista.get(LISTA_AEL_POL_SUM_EXAME_TAB);
	Integer vNroTabPorPrnt = (Integer) mapVariaveis.get(V_NRO_TAB_POR_PRNT);
	String vDescricaoCaracteristica = (String) mapVariaveis.get(V_DESCRICAO_CARACTERISTICA);
	String vLegenda = (String) mapVariaveis.get(V_LEGENDA);
	String vNroTabPorPrntvLegenda = (String) mapVariaveis.get(V_NRO_TAB_POR_PRNTV_LEGENDA);
	String vDescricaoCodificado = (String) mapVariaveis.get(V_DESCRICAO_CODIFICADO);
	String vObservacao = (String) mapVariaveis.get(V_OBSERVACAO);
	Double vValor = (Double) mapVariaveis.get(V_VALOR);
	Integer vEstLvNroTabPorPrntegenda = (Integer) mapVariaveis.get(V_EST_LV_NRO_TAB_POR_PRNTEGENDA);
	Integer vEstLegenda = (Integer) mapVariaveis.get(V_EST_LEGENDA);
	String vLtoLtoId = (String) mapVariaveis.get(V_LTO_LTO_ID);
	Integer vEstExameTab = (Integer) mapVariaveis.get(V_EST_EXAME_TAB);
	Integer vEstItensAtu = (Integer) mapVariaveis.get(V_EST_ITENS_ATU);
	for (AelResultadoExame resultadoExame : resultadoExames) {
	    vLtoLtoId = getSolicitacaoExameFacade().recuperarLocalPaciente(
		    resultadoExame.getItemSolicitacaoExame().getSolicitacaoExame().getAtendimento());
	    if (StringUtils.isBlank(resultadoExame.getParametroCampoLaudo().getTextoLivre())
		    || DominioTipoCampoCampoLaudo.E.equals(resultadoExame.getParametroCampoLaudo().getCampoLaudo().getTipoCampo())) {
		if (resultadoExame.getValor() == null) {
		    if (resultadoExame.getResultadoCaracteristica() != null) {
			vDescricaoCaracteristica = resultadoExame.getResultadoCaracteristica().getDescricao();
			Boolean temLegenda = verificarExisteLegenda(resultadoExame.getItemSolicitacaoExame().getSolicitacaoExame()
				.getAtendimento().getProntuario(), resultadoExame.getItemSolicitacaoExame().getSolicitacaoExame()
				.getRecemNascido(), resultadoExame.getItemSolicitacaoExame().getAelExameMaterialAnalise()
				.getPertenceSumario(), resultadoExame.getResultadoCaracteristica().getSeq(), 0, vDescricaoCaracteristica,
				vLtoLtoId, listaAelPolSumLegenda);
			// List<AelPolSumLegenda> fLegenda =
			// getAelPolSumLegendaDAO().buscarLegendas(resultadoExame.getItemSolicitacaoExame().getSolicitacaoExame().getAtendimento().getProntuario(),
			// DominioSimNao.getInstance(resultadoExame.getItemSolicitacaoExame().getSolicitacaoExame().getRecemNascido()),
			// resultadoExame.getItemSolicitacaoExame().getAelExameMaterialAnalise().getPertenceSumario()
			// ,
			// resultadoExame.getResultadoCaracteristica().getSeq(),
			// 0, vDescricaoCaracteristica, vLtoLtoId);
			if (!temLegenda) {
			    vNroTabPorPrntvLegenda = obterNroTabPorPrntvLegenda(vLegenda, resultadoExame);
			    vLegenda = vNroTabPorPrntvLegenda;
			    AelPolSumLegenda obj = getEmitirRelatorioExamesPacientePopularListasUtilRN().obterSumarioLegenda(
				    vDescricaoCaracteristica, vLtoLtoId, resultadoExame);
			    if (obj != null && obj.getId() != null) {
				listaAelPolSumLegenda.add(obj);
			    }
			    vEstLegenda = vEstLegenda + 1;
			}
		    } else if (resultadoExame.getResultadoCodificado() != null) {
			List<AelResultadoCodificado> resultadoCodificadoList = getAelResultadoCodificadoDAO()
				.pesquisarResultadoCodificadoPorGtcSeqSeqp(resultadoExame.getResultadoCodificado().getId().getGtcSeq(),
					resultadoExame.getResultadoCodificado().getId().getSeqp());
			if (resultadoCodificadoList != null && resultadoCodificadoList.size() > 0) {
			    vDescricaoCodificado = resultadoCodificadoList.get(0).getDescricao();
			}
			Boolean recemNascido = null;
			if (resultadoExame.getItemSolicitacaoExame().getSolicitacaoExame() != null) {
			    recemNascido = resultadoExame.getItemSolicitacaoExame().getSolicitacaoExame().getRecemNascido();
			}
			DominioSumarioExame dominioSumarioExame = null;
			if (resultadoExame.getItemSolicitacaoExame() != null
				&& resultadoExame.getItemSolicitacaoExame().getAelAmostraItemExames() != null) {
			    dominioSumarioExame = resultadoExame.getItemSolicitacaoExame().getAelExameMaterialAnalise()
				    .getPertenceSumario();
			}
			// OPEN c_legenda (r_cursor.prontuario,
			// r_cursor.recem_nascido,
			// r_cursor.pertence_sumario,r_pcl_ree.rcd_seqp,
			// r_pcl_ree.rcd_gtc_seq,v_descricao_codificado,
			// r_cursor.lto_lto_id);
			Boolean temLegenda = verificarExisteLegenda(resultadoExame.getItemSolicitacaoExame().getSolicitacaoExame()
				.getAtendimento().getProntuario(), recemNascido, dominioSumarioExame, resultadoExame
				.getResultadoCodificado().getId().getSeqp(), resultadoExame.getResultadoCodificado().getId().getGtcSeq(),
				resultadoExame.getResultadoCodificado().getDescricao(), vLtoLtoId, listaAelPolSumLegenda);
			// List<AelPolSumLegenda> fLegenda =
			// getAelPolSumLegendaDAO().buscarLegendas(resultadoExame.getItemSolicitacaoExame().getSolicitacaoExame().getAtendimento().getProntuario(),
			// dominioSimNao, dominioSumarioExame,
			// resultadoExame.getResultadoCodificado().getId().getSeqp(),
			// resultadoExame.getResultadoCodificado().getId().getGtcSeq(),
			// resultadoExame.getResultadoCodificado().getDescricao(),
			// vLtoLtoId);
			if (!temLegenda) {
			    vNroTabPorPrntvLegenda = obterNroTabPorPrntvLegenda(vLegenda, resultadoExame.getResultadoCodificado());
			    vLegenda = vNroTabPorPrntvLegenda;
			    listaAelPolSumLegenda.add(getEmitirRelatorioExamesPacientePopularListasUtilRN().obterSumarioLegenda(
				    vDescricaoCodificado, vLtoLtoId, resultadoExame));
			    vEstLvNroTabPorPrntegenda = vEstLegenda + 1;
			}
		    } else {
			vObservacao = obterObservacao((resultadoExame.getDescricao() != null ? resultadoExame.getDescricao() : ""),
				vObservacao);
		    }
		} else {
		    if (resultadoExame.getValor() != null && resultadoExame.getParametroCampoLaudo() != null) {
			vValor = (resultadoExame.getValor() / Math.pow(10,
				nvl(resultadoExame.getParametroCampoLaudo().getQuantidadeCasasDecimais(), Short.valueOf("0"))));
		    }
		    if (truncate(vValor) > 99999999) {
			vValor = (vValor / 1000000);
			mapVariaveis.put(V_CAL_NOME, obterNome(vValor, resultadoExame));
			mapVariaveis.put(V_CAL_NOME_SUMARIO, obterNomeSumario(vValor, resultadoExame));
		    } else {
			if (truncate(vValor) > 9999) {
			    vValor = (vValor / 1000);
			    mapVariaveis.put(V_CAL_NOME, obterNome(vValor, resultadoExame));
			    mapVariaveis.put(V_CAL_NOME_SUMARIO, obterNomeSumario(vValor, resultadoExame));
			} else {
			    if (resultadoExame.getParametroCampoLaudo() != null
				    && resultadoExame.getParametroCampoLaudo().getCampoLaudo() != null) {
				mapVariaveis.put(V_CAL_NOME, obterNome(vValor, resultadoExame));
				mapVariaveis.put(V_CAL_NOME_SUMARIO, obterNomeSumario(vValor, resultadoExame));
			    }
			}
		    }
		    mapVariaveis.put(V_REE_VALOR, vValor);
		    Date dataHoraEvento = DateUtil.obterData(1900, 1, 1);
		    for (AelExtratoItemSolicitacao extrato : resultadoExame.getItemSolicitacaoExame().getAelExtratoItemSolicitacao()) {
			if (extrato.getAelSitItemSolicitacoes().getCodigo().equals("AE")) {
			    if (dataHoraEvento.compareTo(extrato.getDataHoraEvento()) < 0) {
				dataHoraEvento = extrato.getDataHoraEvento();
			    }
			}
		    }
		    mapVariaveis.put(V_DTHR_EVENTO_AREA_EXEC, dataHoraEvento);
		    mapVariaveis.put(V_DTHR_FIM_EXAMES_TAB, resultadoExame.getItemSolicitacaoExame().getSolicitacaoExame().getAtendimento()
			    .getDthrFim());
		    mapVariaveis.put(V_LTO_LTO_ID, vLtoLtoId);
		    mapVariaveis.put(V_ORDEM_EXAMES_TAB, resultadoExame.getParametroCampoLaudo().getCampoLaudo().getOrdem());
		    AelPolSumExameTab aelPolSumExameTab = getEmitirRelatorioExamesPacientePopularListasUtilRN()
			    .executarProcedureAelPolSumInsSem(mapVariaveis, "S");
		    if (aelPolSumExameTab != null && aelPolSumExameTab.getId() != null) {
			listaAelPolSumExameTab.add(aelPolSumExameTab);
		    }

		    vNroTabPorPrnt = vNroTabPorPrnt + 1;
		    vEstExameTab = vEstExameTab + 1;
		    vEstItensAtu = vEstItensAtu + 1;
		}
	    }
	}
	mapLista.put(LISTA_AEL_POL_SUM_LEGENDA, listaAelPolSumLegenda);
	mapLista.put(LISTA_AEL_POL_SUM_EXAME_TAB, listaAelPolSumExameTab);
	mapVariaveis.put(V_VALOR, vValor);
	mapVariaveis.put(V_NRO_TAB_POR_PRNT, vNroTabPorPrnt);
	mapVariaveis.put(V_DESCRICAO_CARACTERISTICA, vDescricaoCaracteristica);
	mapVariaveis.put(V_LEGENDA, vLegenda);
	mapVariaveis.put(V_NRO_TAB_POR_PRNTV_LEGENDA, vNroTabPorPrntvLegenda);
	mapVariaveis.put(V_DESCRICAO_CODIFICADO, vDescricaoCodificado);
	mapVariaveis.put(V_OBSERVACAO, vObservacao);
	mapVariaveis.put(V_EST_LV_NRO_TAB_POR_PRNTEGENDA, vEstLvNroTabPorPrntegenda);
	mapVariaveis.put(V_EST_LEGENDA, vEstLegenda);
	mapVariaveis.put(V_LTO_LTO_ID, vLtoLtoId);
	mapVariaveis.put(V_EST_EXAME_TAB, vEstExameTab);
	mapVariaveis.put(V_EST_ITENS_ATU, vEstItensAtu);
    }

    private boolean verificarExisteLegenda(Integer prontuario, Boolean recemNascido, DominioSumarioExame pertenceSumario,
	    Integer numeroLegenda, Integer grupoLegenda, String descricao, String ltoLtoId, List<AelPolSumLegenda> listaAelPolSumLegenda) {
	for (AelPolSumLegenda aelPolSumLegenda : listaAelPolSumLegenda) {
	    if ((aelPolSumLegenda.getId().getProntuario() != null && aelPolSumLegenda.getId().getProntuario().equals(prontuario))
		    && (aelPolSumLegenda.getId().getRecemNascido() != null && aelPolSumLegenda.getId().getRecemNascido()
			    .equals(recemNascido))
		    && (aelPolSumLegenda.getId().getPertenceSumario() != null && aelPolSumLegenda.getId().getPertenceSumario()
			    .equals(pertenceSumario))
		    && (aelPolSumLegenda.getId().getNumeroLegenda() != null && aelPolSumLegenda.getId().getNumeroLegenda()
			    .equals(numeroLegenda))
		    && (aelPolSumLegenda.getId().getGrupoLegenda() != null && aelPolSumLegenda.getId().getGrupoLegenda()
			    .equals(grupoLegenda))
		    && (aelPolSumLegenda.getId().getDescricao() != null && aelPolSumLegenda.getId().getDescricao().equals(descricao))
		    && (aelPolSumLegenda.getId().getLtoLtoId() != null && aelPolSumLegenda.getId().getLtoLtoId().equals(ltoLtoId))) {
		return true;
	    }
	}
	return false;
    }

    /**
     * 
     * @param a
     * @param b
     * @return
     */
    public Short nvl(Short a, Short b) {
	if (a != null) {
	    return a;
	} else {
	    return b;
	}
    }

    /**
     * 
     * @param value
     * @return
     */
    public double truncate(Double value) {
	return Math.round(value * 100) / 100L;
    }

    /**
     * 
     * @return
     * @throws ApplicationBusinessException
     */
    public String getValorParametroPSituacaoLiberado() throws ApplicationBusinessException {
	AghParametros paramVLiberado = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_LIBERADO);
	if (paramVLiberado != null) {
	    return paramVLiberado.getVlrTexto();
	}
	return "";
    }

    /**
     * 
     * @return
     * @throws ApplicationBusinessException
     */
    public String getValorParametroPSituacaoNaAreaExecutora() throws ApplicationBusinessException {
	AghParametros paramVNaAreaExecutora = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_NA_AREA_EXECUTORA);
	if (paramVNaAreaExecutora != null) {
	    return paramVNaAreaExecutora.getVlrTexto();
	}
	return "";
    }

    /**
     * 
     * @param vLegenda
     * @param listaLegendas
     * @param resultadoExame
     * @return
     */
    private String obterNroTabPorPrntvLegenda(String vLegenda, AelResultadoExame resultadoExame) {
	StringBuilder strLegenda = new StringBuilder();
	if (StringUtils.isNotBlank(vLegenda)) {
	    strLegenda.append(vLegenda).append("0 - ").append(resultadoExame.getResultadoCaracteristica().getSeq())
		    .append(StringUtils.rightPad("", 10));
	} else {
	    strLegenda.append("0 - ").append(resultadoExame.getResultadoCaracteristica().getSeq()).append(StringUtils.rightPad("", 10));
	}
	return strLegenda.toString();
    }

    /**
     * 
     * @param vLegenda
     * @param listaLegendas
     * @param resultadoCodificado
     * @return
     */
    private String obterNroTabPorPrntvLegenda(String vLegenda, AelResultadoCodificado resultadoCodificado) {
	StringBuilder strLegenda = new StringBuilder();
	if (StringUtils.isNotBlank(vLegenda)) {
	    strLegenda.append(vLegenda).append(resultadoCodificado.getId().getGtcSeq()).append('-')
		    .append(resultadoCodificado.getId().getSeqp()).append(StringUtils.rightPad("", 5));
	} else {
	    strLegenda.append(resultadoCodificado.getId().getGtcSeq()).append('-').append(resultadoCodificado.getId().getSeqp())
		    .append(StringUtils.rightPad("", 5));
	}
	return strLegenda.toString();
    }

    /**
     * 
     * @param descResultado
     * @param vDescricaoResultado
     * @param vObservacao
     * @return
     */
	private String obterObservacao(String vDescricaoResultado, String vObservacao) {
		StringBuilder strObservacao = new StringBuilder();
		if (StringUtils.isBlank(vObservacao)) {
			strObservacao.append(vDescricaoResultado).append('\n');
		} else {
			strObservacao.append(vObservacao).append(vDescricaoResultado).append('\n');
		}
		return strObservacao.toString();
	}

    /**
     * 
     * @param vValor
     * @param resultadoExame
     * @param vNome
     * @return
     */
    private String obterNome(Double vValor, AelResultadoExame resultadoExame) {
	StringBuilder sbNome = new StringBuilder();
	if (resultadoExame.getValor() != null && resultadoExame.getParametroCampoLaudo() != null) {
	    vValor = (resultadoExame.getValor() / Math.pow(10,
		    nvl(resultadoExame.getParametroCampoLaudo().getQuantidadeCasasDecimais(), Short.valueOf("0"))));
	}
	String nome = "";
	if (resultadoExame != null && resultadoExame.getParametroCampoLaudo() != null
		&& resultadoExame.getParametroCampoLaudo().getCampoLaudo() != null) {
	    nome = resultadoExame.getParametroCampoLaudo().getCampoLaudo().getNome();
	}
	if (truncate(vValor) > 99999999) {
	    vValor = (vValor / 1000000);
	    if (StringUtils.isNotBlank(nome)) {
		if (nome.length() >= 9) {
		    sbNome.append(nome.substring(1, 9));
		} else {
		    sbNome.append(nome);
		}
	    }
	    sbNome.append("-Mã");
	} else if (truncate(vValor) > 9999) {
	    vValor = (vValor / 1000);
	    if (StringUtils.isNotBlank(nome)) {
		if (nome.length() >= 9) {
		    sbNome.append(nome.substring(1, 9));
		} else {
		    sbNome.append(nome);
		}
	    }
	    sbNome.append("-Mi");
	} else {
	    if (StringUtils.isNotBlank(nome)) {
		if (nome.length() >= 12) {
		    sbNome.append(nome.substring(1, 12));
		} else {
		    sbNome.append(nome);
		}
	    }
	}
	return sbNome.toString();
    }

    /**
     * 
     * @param vValor
     * @param resultadoExame
     * @param vNomeSumario
     * @return
     */
    private String obterNomeSumario(Double vValor, AelResultadoExame resultadoExame) {
	if (resultadoExame.getValor() != null && resultadoExame.getParametroCampoLaudo() != null) {
	    vValor = (resultadoExame.getValor() / Math.pow(10,
		    nvl(resultadoExame.getParametroCampoLaudo().getQuantidadeCasasDecimais(), Short.valueOf("0"))));
	}
	StringBuilder sbNomeSumario = new StringBuilder();
	String nomeSumario = "";
	if (resultadoExame != null && resultadoExame.getParametroCampoLaudo() != null
		&& resultadoExame.getParametroCampoLaudo().getCampoLaudo() != null) {
	    nomeSumario = resultadoExame.getParametroCampoLaudo().getCampoLaudo().getNomeSumario();
	}
	if (truncate(vValor) > 99999999) {
	    vValor = (vValor / 1000000);
	    if (StringUtils.isNotBlank(nomeSumario)) {
		if (nomeSumario.length() > 9) {
		    sbNomeSumario.append(nomeSumario.substring(1, 9));
		} else {
		    sbNomeSumario.append(nomeSumario);
		}
	    }
	    sbNomeSumario.append("-Mã");
	} else if (truncate(vValor) > 9999) {
	    vValor = (vValor / 1000);
	    if (StringUtils.isNotBlank(nomeSumario)) {
		if (nomeSumario.length() > 9) {
		    sbNomeSumario.append(nomeSumario.substring(1, 9));
		} else {
		    sbNomeSumario.append(nomeSumario);
		}
	    }
	    sbNomeSumario.append("-Mi");
	} else {
	    if (StringUtils.isNotBlank(nomeSumario)) {
		if (nomeSumario.length() > 12) {
		    sbNomeSumario.append(nomeSumario.substring(1, 12));
		} else {
		    sbNomeSumario.append(nomeSumario);
		}
	    }
	}
	return sbNomeSumario.toString();
    }

    /**
     * 
     * @param vVersaoExaSigla
     * @param textoLivre
     * @return
     */
    public boolean verificarVersaoExaSiglaTextoLivre(String vVersaoExaSigla, String textoLivre) {
	if (StringUtils.equalsIgnoreCase("TSA", vVersaoExaSigla)
		&& (StringUtils.equalsIgnoreCase("1)", textoLivre) || (!StringUtils.equalsIgnoreCase("2)", textoLivre)
			&& !StringUtils.equalsIgnoreCase("3)", textoLivre) && !StringUtils.equalsIgnoreCase("4)", textoLivre)))) {
	    return Boolean.TRUE;
	}
	return Boolean.FALSE;
    }

    /**
     * 
     * @param objetoVisual
     * @param textoLivre
     * @param tipoCampo
     * @param parametroCampoLaudoSeq
     * @param mapVariaveis
     * @param validarTamanhoTexto
     * @return
     * @throws BaseException
     */
    public boolean verificarDominioObjetoVisualOutraPosicao(DominioObjetoVisual objetoVisual, String textoLivre,
	    DominioTipoCampoCampoLaudo tipoCampo, Integer parametroCampoLaudoSeq, Map<String, Object> mapVariaveis,
	    boolean validarTamanhoTexto) throws BaseException {

	boolean retorno = Boolean.FALSE;
	String vResultadoComMascara = (String) mapVariaveis.get(V_RESULTADO_COM_MASCARA);
	Short vSeqpAnt = (Short) mapVariaveis.get(V_SEQP_ANT);
	Integer vSoeSeqpAnt = (Integer) mapVariaveis.get(V_SOE_SEQP_ANT);
	String vVersaoExaSigla = (String) mapVariaveis.get(V_VERSAO_EXA_SIGLA);

	if (DominioObjetoVisual.EQUIPAMENTO.equals(objetoVisual)) {
	    vResultadoComMascara = getMascaraExamesRN().buscaInformacaoEquipamento(vSoeSeqpAnt, vSeqpAnt);
	    retorno = Boolean.TRUE;
	} else if (DominioObjetoVisual.METODO.equals(objetoVisual)) {
	    vResultadoComMascara = getMascaraExamesRN().buscaInformacaoMetodo(vSoeSeqpAnt, vSeqpAnt);
	    retorno = Boolean.TRUE;
	} else if (DominioObjetoVisual.RECEBIMENTO.equals(objetoVisual)) {
	    vResultadoComMascara = getMascaraExamesRN().buscaInformacaoRecebimento(vSoeSeqpAnt, vSeqpAnt);
	    retorno = Boolean.TRUE;
	} else if (DominioObjetoVisual.HISTORICO.equals(objetoVisual)) {
	    List<String> strResultado = getMascaraExamesRN().buscaInformacaoHistorico(vSoeSeqpAnt, vSeqpAnt, parametroCampoLaudoSeq);
	    if (strResultado != null && strResultado.size() > 0) {
		vResultadoComMascara = strResultado.get(0);
	    }
	    retorno = Boolean.TRUE;
	} else if (DominioObjetoVisual.VALORES_REFERENCIA.equals(objetoVisual)) {
	    vResultadoComMascara = null;
	    retorno = Boolean.TRUE;
	} else if (StringUtils.isNotBlank(textoLivre) && !DominioTipoCampoCampoLaudo.E.equals(tipoCampo)) {
	    if (validarTamanhoTexto) {
		int tamanhoTexto = 0;
		if (StringUtils.isBlank(vResultadoComMascara)) {
		    if (verificarVersaoExaSiglaTextoLivre(vVersaoExaSigla, textoLivre)) {
			tamanhoTexto = textoLivre.length();
			for (int i = 0; i <= 40; i++) {
			    if (i <= tamanhoTexto) {
				vResultadoComMascara = textoLivre;
			    } else {
				if (StringUtils.equalsIgnoreCase("1)", textoLivre)) {
				    vResultadoComMascara = (".").concat(vResultadoComMascara);
				} else {
				    vResultadoComMascara = vResultadoComMascara.concat(".");
				}
			    }
			}
			if (StringUtils.equalsIgnoreCase("1)", textoLivre)) {
			    vResultadoComMascara = "..".concat(vResultadoComMascara).concat(StringUtils.rightPad("", 10));
			}
		    } else {
			vResultadoComMascara = textoLivre.concat(StringUtils.rightPad("", 10));
		    }
		} else {
		    vResultadoComMascara = vResultadoComMascara.concat(textoLivre).concat(StringUtils.rightPad("", 10));
		}

		// if(verificarVersaoExaSiglaTextoLivre(vVersaoExaSigla,
		// textoLivre)) {
		// if(StringUtils.isBlank(vResultadoComMascara)) {
		// tamanhoTexto = textoLivre.length();
		// for(int i = 0; i <= 40; i++) {
		// if(i <= tamanhoTexto) {
		// vResultadoComMascara = textoLivre;
		// } else {
		// if(StringUtils.equalsIgnoreCase("1)", textoLivre)) {
		// vResultadoComMascara = (".").concat(vResultadoComMascara);
		// }else{
		// vResultadoComMascara = vResultadoComMascara.concat(".");
		// }
		// }
		// }
		// if(StringUtils.equalsIgnoreCase("1)", textoLivre)) {
		// vResultadoComMascara =
		// "..".concat(vResultadoComMascara).concat(StringUtils.right("",
		// 10));
		// }
		// } else {
		// vResultadoComMascara =
		// textoLivre.concat(StringUtils.right("", 10));
		// }
		// } else {
		// if(vResultadoComMascara != null){
		// vResultadoComMascara =
		// vResultadoComMascara.concat(textoLivre).concat(StringUtils.right("",
		// 10));
		// }
		// }
	    } else {
		if (StringUtils.isBlank(vResultadoComMascara)) {
		    vResultadoComMascara = textoLivre.concat(StringUtils.rightPad("", 10));
		} else {
		    vResultadoComMascara = vResultadoComMascara.concat(textoLivre).concat(StringUtils.rightPad("", 10));
		}
	    }
	    retorno = Boolean.TRUE;
	}
	mapVariaveis.put(V_RESULTADO_COM_MASCARA, vResultadoComMascara);
	return retorno;
    }

    /**
     * 
     * @param resultados
     * @param parametroCampoLaudo
     * @param mapVariaveis
     */
    public void manipularResultado(List<AelResultadoExame> resultados, AelParametroCamposLaudo parametroCampoLaudo,
	    Map<String, Object> mapVariaveis, boolean isVResultadoComMascaraVazio) {

	String vDescricaoResultado = (String) mapVariaveis.get(V_DESCRICAO_RESULTADO);
	String vDescricaoCodificado = (String) mapVariaveis.get(V_DESCRICAO_CODIFICADO);
	String vDescricaoCaracteristica = (String) mapVariaveis.get(V_DESCRICAO_CARACTERISTICA);
	String vResultadoAux = (String) mapVariaveis.get(V_RESULTADO_AUX);
	String vResultadoComMascara = (String) mapVariaveis.get(V_RESULTADO_COM_MASCARA);
	Double vValor = (Double) mapVariaveis.get(V_VALOR);

	if (resultados != null && !resultados.isEmpty()) {
	    for (AelResultadoExame resultado : resultados) {
		if (resultado.getValor() == null || resultado.getValor().intValue() == 0) {
		    if (resultado.getResultadoCaracteristica() != null && resultado.getResultadoCaracteristica().getSeq() != null) {
			AelResultadoCaracteristica resultadoCaracteristica = getAelResultadoCaracteristicaDAO().obterPorChavePrimaria(
				resultado.getResultadoCaracteristica().getSeq());
			vDescricaoCaracteristica = resultadoCaracteristica.getDescricao();
			vDescricaoResultado = vDescricaoCaracteristica;
			vResultadoAux = vDescricaoCaracteristica;
		    } else if (resultado.getResultadoCodificado() != null) {
			Integer gtcSeq = resultado.getResultadoCodificado().getId().getGtcSeq();
			Integer seqp = resultado.getResultadoCodificado().getId().getSeqp();
			List<AelResultadoCodificado> resultadoCodificadoList = getAelResultadoCodificadoDAO()
				.pesquisarResultadoCodificadoPorGtcSeqSeqp(gtcSeq, seqp);
			if (resultadoCodificadoList != null && resultadoCodificadoList.size() > 0) {
			    vDescricaoCodificado = resultadoCodificadoList.get(0).getDescricao();
			    vDescricaoResultado = vDescricaoCodificado;
			    vResultadoAux = vDescricaoCodificado;
			}
		    } else {
			    vDescricaoResultado = resultado.getDescricao();
			    vResultadoAux = vDescricaoResultado;
		    }
		} else {
		    if (resultado.getValor() != null && resultado.getParametroCampoLaudo() != null) {
			vValor = (resultado.getValor() / Math.pow(10,
				nvl(parametroCampoLaudo.getQuantidadeCasasDecimais(), Short.valueOf("0"))));
			vResultadoAux = vValor.toString();
		    }
		}
		if (isVResultadoComMascaraVazio) {
		    if (StringUtils.isBlank(vResultadoComMascara)) {
			vResultadoComMascara = vResultadoAux.concat(StringUtils.rightPad("", 10));
		    } else {
			vResultadoComMascara = vResultadoComMascara.concat(vResultadoAux).concat(StringUtils.rightPad("", 10));
		    }
		} else {
		    vResultadoComMascara = vResultadoComMascara.concat(StringUtils.rightPad("", 10));
		}
		break;
	    }
	} else {
	    if (isVResultadoComMascaraVazio) {
		if (StringUtils.isBlank(vResultadoComMascara)) {
		    vResultadoComMascara = " ";
		} else {
		    vResultadoComMascara = vResultadoComMascara.concat(StringUtils.rightPad("", 11));
		}
	    } else {
		if (StringUtils.isBlank(vResultadoComMascara)) {
		    if (vResultadoComMascara != null) {
			vResultadoComMascara = vResultadoComMascara.concat(StringUtils.rightPad("", 11));
		    }
		}
	    }
	}
	mapVariaveis.put(V_DESCRICAO_RESULTADO, vDescricaoResultado);
	mapVariaveis.put(V_DESCRICAO_CODIFICADO, vDescricaoCodificado);
	mapVariaveis.put(V_DESCRICAO_CARACTERISTICA, vDescricaoCaracteristica);
	mapVariaveis.put(V_RESULTADO_AUX, vResultadoAux);
	mapVariaveis.put(V_RESULTADO_COM_MASCARA, vResultadoComMascara);
	mapVariaveis.put(V_VALOR, vValor);
    }

    /**
     * 
     * @param notasAdicionais
     * @param mapVariaveis
     * @param mapLista
     */
    public void manipularNotasAdicionais(List<AelNotaAdicional> notasAdicionais, Map<String, Object> mapVariaveis,
	    Map<String, Object> mapLista) {

	Boolean vPrimeiraVezNA = Boolean.TRUE;
	String vResultadoComMascara = (String) mapVariaveis.get(V_RESULTADO_COM_MASCARA);
	String vDescricaoNotasAdicionais = (String) mapVariaveis.get(V_DESCRICAO_NOTAS_ADICIONAIS);
	Integer vEstMascLinha = (Integer) mapVariaveis.get(V_EST_MASC_LINHA);
	Integer vNroLinha = (Integer) mapVariaveis.get(V_NRO_LINHA);
	Integer vEstItensAtu = (Integer) mapVariaveis.get(V_EST_ITENS_ATU);
	Integer vOrdemRel = (Integer) mapVariaveis.get(V_ORDEM_REL);
	List<AelPolSumMascLinha> listaAelPolSumMascLinha = (List<AelPolSumMascLinha>) mapLista.get(LISTA_AEL_POL_SUM_MASC_LINHA);

	if (notasAdicionais != null) {
	    for (AelNotaAdicional nota : notasAdicionais) {
		AelPolSumMascLinha aelPolSumMascLinha = null;
		if (vPrimeiraVezNA) {
		    vResultadoComMascara = super.getResourceBundleValue("AEL_POL_SUM_EXAMES_NOTAS_ADICIONAIS");
		    vPrimeiraVezNA = Boolean.FALSE;
		    vNroLinha = vNroLinha + 1;
		    aelPolSumMascLinha = getEmitirRelatorioExamesPacientePopularListasUtilRN().obterAelPolSumMascLinha(vOrdemRel,
			    vNroLinha, vResultadoComMascara);
		    if (aelPolSumMascLinha != null && aelPolSumMascLinha.getId() != null) {
			listaAelPolSumMascLinha.add(aelPolSumMascLinha);
		    }
		    vEstMascLinha = vEstMascLinha + 1;
		    vResultadoComMascara = null;
		}
		vDescricaoNotasAdicionais = StringUtils.trim(nota.getNotasAdicionais());
		vResultadoComMascara = StringUtils.substring(vDescricaoNotasAdicionais, 1, 2000);
		vNroLinha = vNroLinha + 1;
		aelPolSumMascLinha = getEmitirRelatorioExamesPacientePopularListasUtilRN().obterAelPolSumMascLinha(vOrdemRel, vNroLinha,
			vResultadoComMascara);
		if (aelPolSumMascLinha != null && aelPolSumMascLinha.getId() != null) {
		    listaAelPolSumMascLinha.add(aelPolSumMascLinha);
		}
		vEstMascLinha = vEstMascLinha + 1;
		vResultadoComMascara = null;
		if (StringUtils.isNotBlank(StringUtils.substring(vDescricaoNotasAdicionais, 2000, 2001))) {
		    vResultadoComMascara = StringUtils.substring(vDescricaoNotasAdicionais, 2000, 2001);
		    vNroLinha = vNroLinha + 1;
		    aelPolSumMascLinha = getEmitirRelatorioExamesPacientePopularListasUtilRN().obterAelPolSumMascLinha(vOrdemRel,
			    vNroLinha, vResultadoComMascara);
		    if (aelPolSumMascLinha != null && aelPolSumMascLinha.getId() != null) {
			listaAelPolSumMascLinha.add(aelPolSumMascLinha);
		    }
		    vEstMascLinha = vEstMascLinha + 1;
		    vResultadoComMascara = null;
		}
		// TODO: verificar formatacao da data
		vResultadoComMascara = nota.getCriadoEm().toString().concat(StringUtils.rightPad("", 6))
			.concat(nota.getServidor().getPessoaFisica().getNome());
		vNroLinha = vNroLinha + 1;
		aelPolSumMascLinha = getEmitirRelatorioExamesPacientePopularListasUtilRN().obterAelPolSumMascLinha(vOrdemRel, vNroLinha,
			vResultadoComMascara);
		if (aelPolSumMascLinha != null && aelPolSumMascLinha.getId() != null) {
		    listaAelPolSumMascLinha.add(aelPolSumMascLinha);
		}
		vEstMascLinha = vEstMascLinha + 1;
		vResultadoComMascara = null;
	    }
	    vEstItensAtu = vEstItensAtu + 1;
	}
	mapVariaveis.put(V_RESULTADO_COM_MASCARA, vResultadoComMascara);
	mapVariaveis.put(V_DESCRICAO_NOTAS_ADICIONAIS, vDescricaoNotasAdicionais);
	mapVariaveis.put(V_EST_MASC_LINHA, vEstMascLinha);
	mapVariaveis.put(V_NRO_LINHA, vNroLinha);
	mapVariaveis.put(V_EST_ITENS_ATU, vEstItensAtu);
	mapVariaveis.put(V_ORDEM_REL, vOrdemRel);
	mapLista.put(LISTA_AEL_POL_SUM_MASC_LINHA, listaAelPolSumMascLinha);
    }

    /**
     * 
     * @param lista
     * @return
     */
    public Date obterMaxDataHoraEvento(List<AelExtratoItemSolicitacao> lista) {
	Date dataHoraEvento = DateUtil.obterData(1900, 1, 1);
	for (AelExtratoItemSolicitacao extrato : lista) {
	    if (extrato.getAelSitItemSolicitacoes().getCodigo().equals("AE")) {
		if (dataHoraEvento.compareTo(extrato.getDataHoraEvento()) < 0) {
		    dataHoraEvento = extrato.getDataHoraEvento();
		}
	    }
	}
	return dataHoraEvento;
    }

    @SuppressWarnings("unchecked")
    public void adicionarListaAelPolSumMascCampoEdit(List<AelResultadoExame> resultados, Map<String, Object> mapVariaveis,
	    Map<String, Object> mapLista, AelParametroCamposLaudo parametroCampoLaudo) {

	Short vSeqDeptAnt = (Short) mapVariaveis.get(V_SEQ_DEPT_ANT);
	Short vSeqpAnt = (Short) mapVariaveis.get(V_SEQP_ANT);
	Integer vSoeSeqpAnt = (Integer) mapVariaveis.get(V_SOE_SEQP_ANT);
	Short vSoeSeqpDeptAnt = (Short) mapVariaveis.get(V_SOE_SEQP_DEPT_ANT);
	String vVersaoExaSigla = (String) mapVariaveis.get(V_VERSAO_EXA_SIGLA);
	Integer vVersaoManSeq = (Integer) mapVariaveis.get(V_VERSAO_MAN_SEQ);
	Integer vVersaoSeqp = (Integer) mapVariaveis.get(V_VERSAO_SEQP);
	Integer vOrdemRel = (Integer) mapVariaveis.get(V_ORDEM_REL);
	Integer vNroLinha = (Integer) mapVariaveis.get(V_NRO_LINHA);
	Integer vNroCampo = (Integer) mapVariaveis.get(V_NRO_CAMPO);
	String vDescricaoCaracteristica = (String) mapVariaveis.get(V_DESCRICAO_CARACTERISTICA);
	String vDescricaoResultado = (String) mapVariaveis.get(V_DESCRICAO_RESULTADO);
	String vResultadoAux = (String) mapVariaveis.get(V_RESULTADO_AUX);
	Integer vEstCampoEdit = (Integer) mapVariaveis.get(V_EST_CAMPO_EDIT);
	String vDescricaoCodificado = (String) mapVariaveis.get(V_DESCRICAO_CODIFICADO);
	Double vValor = (Double) mapVariaveis.get(V_VALOR);
	List<AelPolSumMascCampoEdit> listaAelPolSumMascCampoEdit = (List<AelPolSumMascCampoEdit>) mapLista
		.get(LISTA_AEL_POL_SUM_MASC_CAMPO_EDIT);

	// List<AelResultadoExame> resultados =
	// getAelResultadoExameDAO().listarResultadosExames(vSoeSeqpAnt,
	// vSeqpAnt, vVersaoExaSigla, vVersaoManSeq, vVersaoSeqp,
	// parametroCampoLaudo.getId().getCalSeq(),
	// parametroCampoLaudo.getId().getSeqp());

	if (resultados != null) {
	    for (AelResultadoExame resultado : resultados) {
		if (resultado.getValor() == null) {
		    if (resultado.getResultadoCaracteristica() != null) {
			AelResultadoCaracteristica resultadoCaracteristica = getAelResultadoCaracteristicaDAO().obterPorChavePrimaria(
				resultado.getResultadoCaracteristica().getSeq());
			vDescricaoCaracteristica = resultadoCaracteristica.getDescricao();
			vDescricaoResultado = vDescricaoCaracteristica;
			vResultadoAux = vDescricaoCaracteristica;
			vNroCampo = vNroCampo + 1;
			listaAelPolSumMascCampoEdit.add(getEmitirRelatorioExamesPacientePopularListasUtilRN().obterAelPolSumMascCampoEdit(
				vOrdemRel, vNroLinha, vNroCampo, vDescricaoResultado));
			vEstCampoEdit = vEstCampoEdit + 1;
		    } else if (resultado.getResultadoCodificado() != null) {
			List<AelResultadoCodificado> resultadosCodificados = getAelResultadoCodificadoDAO()
				.pesquisarResultadoCodificadoPorGtcSeqSeqp(resultado.getResultadoCodificado().getId().getGtcSeq(),
					resultado.getResultadoCodificado().getId().getSeqp());
			if (resultadosCodificados != null) {
			    vDescricaoCodificado = resultadosCodificados.iterator().next().getDescricao();
			    vDescricaoResultado = vDescricaoCodificado;
			    vResultadoAux = vDescricaoCodificado;
			    listaAelPolSumMascCampoEdit.add(getEmitirRelatorioExamesPacientePopularListasUtilRN()
				    .obterAelPolSumMascCampoEdit(vOrdemRel, vNroLinha, vNroCampo, vDescricaoCodificado));
			    vEstCampoEdit = vEstCampoEdit + 1;
			}
		    } else {
			    vDescricaoResultado = resultado.getDescricao();
			    vResultadoAux = vDescricaoResultado;
			    listaAelPolSumMascCampoEdit.add(getEmitirRelatorioExamesPacientePopularListasUtilRN()
				    .obterAelPolSumMascCampoEdit(vOrdemRel, vNroLinha, vNroCampo, vDescricaoResultado));
			    vEstCampoEdit = vEstCampoEdit + 1;
		    }
		} else {
		    if (resultado.getValor() != null && parametroCampoLaudo != null) {
			vValor = (resultado.getValor() / Math.pow(10,
				nvl(parametroCampoLaudo.getQuantidadeCasasDecimais(), Short.valueOf("0"))));
		    }
		    listaAelPolSumMascCampoEdit.add(getEmitirRelatorioExamesPacientePopularListasUtilRN().obterAelPolSumMascCampoEdit(
			    vOrdemRel, vNroLinha, vNroCampo, vValor == null ? "" : vValor.toString()));
		    vEstCampoEdit = vEstCampoEdit + 1;
		}
		break;
	    }
	} else {
	    listaAelPolSumMascCampoEdit.add(getEmitirRelatorioExamesPacientePopularListasUtilRN().obterAelPolSumMascCampoEdit(vOrdemRel,
		    vNroLinha, vNroCampo, " "));
	    vEstCampoEdit = vEstCampoEdit + 1;
	}
	mapVariaveis.put(V_SEQ_DEPT_ANT, vSeqDeptAnt);
	mapVariaveis.put(V_SEQP_ANT, vSeqpAnt);
	mapVariaveis.put(V_SOE_SEQP_ANT, vSoeSeqpAnt);
	mapVariaveis.put(V_SOE_SEQP_DEPT_ANT, vSoeSeqpDeptAnt);
	mapVariaveis.put(V_VERSAO_EXA_SIGLA, vVersaoExaSigla);
	mapVariaveis.put(V_VERSAO_MAN_SEQ, vVersaoManSeq);
	mapVariaveis.put(V_VERSAO_SEQP, vVersaoSeqp);
	mapVariaveis.put(V_ORDEM_REL, vOrdemRel);
	mapVariaveis.put(V_NRO_LINHA, vNroLinha);
	mapVariaveis.put(V_NRO_CAMPO, vNroCampo);
	mapVariaveis.put(V_DESCRICAO_CARACTERISTICA, vDescricaoCaracteristica);
	mapVariaveis.put(V_DESCRICAO_RESULTADO, vDescricaoResultado);
	mapVariaveis.put(V_RESULTADO_AUX, vResultadoAux);
	mapVariaveis.put(V_EST_CAMPO_EDIT, vEstCampoEdit);
	mapVariaveis.put(V_DESCRICAO_CODIFICADO, vDescricaoCodificado);
	mapVariaveis.put(V_VALOR, vValor);
	mapLista.put(LISTA_AEL_POL_SUM_MASC_CAMPO_EDIT, listaAelPolSumMascCampoEdit);
    }

}
