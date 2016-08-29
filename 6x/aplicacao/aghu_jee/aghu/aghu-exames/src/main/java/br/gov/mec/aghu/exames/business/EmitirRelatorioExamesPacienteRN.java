/**
 * 
 */
package br.gov.mec.aghu.exames.business;

import static br.gov.mec.aghu.exames.business.DominioVariaveisEmitirRelatorioExamesPaciente.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioObjetoVisual;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSumarioExame;
import br.gov.mec.aghu.dominio.DominioTipoCampoCampoLaudo;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelNotaAdicionalDAO;
import br.gov.mec.aghu.exames.dao.AelParametroCamposLaudoDAO;
import br.gov.mec.aghu.exames.dao.AelResultadoExameDAO;
import br.gov.mec.aghu.exameselaudos.business.IExamesLaudosFacade;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelNotaAdicional;
import br.gov.mec.aghu.model.AelParametroCamposLaudo;
import br.gov.mec.aghu.model.AelPolSumExameMasc;
import br.gov.mec.aghu.model.AelPolSumExameTab;
import br.gov.mec.aghu.model.AelPolSumLegenda;
import br.gov.mec.aghu.model.AelPolSumMascCampoEdit;
import br.gov.mec.aghu.model.AelPolSumMascLinha;
import br.gov.mec.aghu.model.AelPolSumObservacao;
import br.gov.mec.aghu.model.AelResultadoExame;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class EmitirRelatorioExamesPacienteRN extends BaseBusiness {

    @EJB
    private EmitirRelatorioExamesPacientePopularListasUtilRN emitirRelatorioExamesPacientePopularListasUtilRN;

    @EJB
    private EmitirRelatorioExamesPacienteUtilRN emitirRelatorioExamesPacienteUtilRN;

    private static final Log LOG = LogFactory.getLog(EmitirRelatorioExamesPacienteRN.class);

    @Override
    @Deprecated
    protected Log getLogger() {
	return LOG;
    }

    @Inject
    private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;

    @Inject
    private AelNotaAdicionalDAO aelNotaAdicionalDAO;

    @Inject
    private AelResultadoExameDAO aelResultadoExameDAO;

    @EJB
    private IExamesLaudosFacade examesLaudosFacade;

    @EJB
    private IAghuFacade aghuFacade;

    @Inject
    private AelParametroCamposLaudoDAO aelParametroCamposLaudoDAO;

    private static final long serialVersionUID = 6126467883048043160L;

    protected EmitirRelatorioExamesPacienteUtilRN getEmitirRelatorioExamesPacienteUtilRN() {
	return emitirRelatorioExamesPacienteUtilRN;
    }

    protected IAghuFacade getAghuFacade() {
	return this.aghuFacade;
    }

    protected IExamesLaudosFacade getExamesLaudosFacade() {
	return this.examesLaudosFacade;
    }

    protected AelResultadoExameDAO getAelResultadoExameDAO() {
	return aelResultadoExameDAO;
    }

    protected AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO() {
	return aelItemSolicitacaoExameDAO;
    }

    protected AelNotaAdicionalDAO getAelNotaAdicionalDAO() {
	return aelNotaAdicionalDAO;
    }

    protected EmitirRelatorioExamesPacientePopularListasUtilRN getEmitirRelatorioExamesPacientePopularListasUtilRN() {
	return emitirRelatorioExamesPacientePopularListasUtilRN;
    }

    /**
     * 
     * @param prontuario
     * @param atdSeq
     * @param pertenceSumario
     * @param dthrEvento
     * @param recemNascido
     * @param pertenceSumarioRodape
     * @return
     * @throws BaseException
     */
    public Map<String, Object> executarProcedure(Integer prontuario, Integer atdSeq, DominioSumarioExame pertenceSumario, Date dthrEvento,
	    Boolean recemNascido, String pertenceSumarioRodape) throws BaseException {

	Map<String, Object> mapLista = inicializarMapLista();
	Map<String, Object> mapVariaveis = inicializarMapVariaveis();
	executar(mapLista, mapVariaveis, prontuario, atdSeq, getEmitirRelatorioExamesPacienteUtilRN().getValorParametroPSituacaoLiberado(),
		getEmitirRelatorioExamesPacienteUtilRN().getValorParametroPSituacaoNaAreaExecutora());
	preencherIndentificador(mapLista, prontuario, pertenceSumario, dthrEvento, recemNascido);
	preencherTextoRodape(pertenceSumarioRodape);
	return mapLista;
    }

    /**
     * @ORADB: AELK_POL_SUM_EXAMES.AELP_POL_SUM_EXAMES
     * @param mapLista
     * @param mapVariaveis
     * @param prontuario
     * @throws BaseException
     */
    @SuppressWarnings("unchecked")
    private void executar(Map<String, Object> mapLista, Map<String, Object> mapVariaveis, Integer prontuario, Integer atdSeq,
	    String vLiberado, String vNaAreaExecutora) throws BaseException {

	List<AelPolSumMascLinha> listaAelPolSumMascLinha = (List<AelPolSumMascLinha>) mapLista.get(LISTA_AEL_POL_SUM_MASC_LINHA);
	List<AelPolSumExameMasc> listaAelPolSumExameMasc = (List<AelPolSumExameMasc>) mapLista.get(LISTA_AEL_POL_SUM_EXAME_MASC);
	List<AelPolSumExameTab> listaAelPolSumExameTab = (List<AelPolSumExameTab>) mapLista.get(LISTA_AEL_POL_SUM_EXAME_TAB);
	Integer vPrntControle = null;
	Integer vPrntContrFim = null;
	Integer vNroTabPorPrnt = null;
	Integer vNroMascPorPrnt = null;

	List<AelItemSolicitacaoExames> listaRCursor = getAghuFacade().listarAelItemSolicitacaoExamesPorAtdSeqSitCodigo(atdSeq, vLiberado,
		vNaAreaExecutora);
	for (AelItemSolicitacaoExames itemSolicitacao : listaRCursor) {

	    vNroTabPorPrnt = (Integer) mapVariaveis.get(V_NRO_TAB_POR_PRNT);
	    vNroMascPorPrnt = (Integer) mapVariaveis.get(V_NRO_MASC_POR_PRNT);
	    vPrntControle = (Integer) mapVariaveis.get(V_PRNT_CONTROLE);
	    vPrntContrFim = (Integer) mapVariaveis.get(V_PRNT_CONTR_FIM);

	    Integer vOrdemRel = (Integer) mapVariaveis.get(V_ORDEM_REL);
	    Integer vEstMascLinha = (Integer) mapVariaveis.get(V_EST_MASC_LINHA);
	    // Integer vNroLinha = (Integer) mapVariaveis.get("vNroLinha");
	    Short vSeqpAnt = (Short) mapVariaveis.get(V_SEQP_ANT);
	    Integer vSoeSeqpAnt = (Integer) mapVariaveis.get(V_SOE_SEQP_ANT);
	    // String vResultadoComMascara = (String)
	    // mapVariaveis.get("vResultadoComMascara");

	    if (vPrntControle == null) {
		vPrntControle = obterVPrntControle(itemSolicitacao.getSolicitacaoExame().getAtendimento(), mapVariaveis, mapLista);
	    } else {
		if (vPrntControle != null && vPrntContrFim != null && vPrntControle.intValue() == vPrntContrFim.intValue()) {
		    if (vNroTabPorPrnt == 0 && vNroMascPorPrnt > 0) {
			listaAelPolSumExameTab.add(getEmitirRelatorioExamesPacientePopularListasUtilRN().executarProcedureAelPolSumInsSem(
				mapVariaveis, "N"));
		    }
		}
	    }
	    if (!verificarAtendimento(itemSolicitacao, mapVariaveis)) {
		atualizarMapVariaveis(itemSolicitacao, mapVariaveis);
	    }

	    // Se pertencer aos sumarios da lista, gera TAB, senão gera MASC
	    if (obterListagemSumario().contains(itemSolicitacao.getAelExameMaterialAnalise().getPertenceSumario())) {
		manipularResultadoExamesTab(itemSolicitacao, mapVariaveis, mapLista);
	    } else {
		vNroMascPorPrnt = manipularResultadoExamesMasc(mapLista, mapVariaveis, listaAelPolSumMascLinha, listaAelPolSumExameMasc,
			itemSolicitacao, vOrdemRel, vEstMascLinha, vSeqpAnt, vSoeSeqpAnt);
		vPrntContrFim = itemSolicitacao.getSolicitacaoExame().getAtendimento().getProntuario();
	    }
	}

	// se não achou TAB mas achou MASC, insere um resultado para passar na
	// validação da ON.
	if (vPrntControle != null && vPrntContrFim != null && vPrntControle.equals(vPrntContrFim)) {
	    if (vNroTabPorPrnt == 0 && vNroMascPorPrnt > 0) {
		listaAelPolSumExameTab.add(getEmitirRelatorioExamesPacientePopularListasUtilRN().executarProcedureAelPolSumInsSem(
			mapVariaveis, "N"));
	    }
	}

	mapLista.put(LISTA_AEL_POL_SUM_MASC_LINHA, listaAelPolSumMascLinha);
	mapLista.put(LISTA_AEL_POL_SUM_EXAME_MASC, listaAelPolSumExameMasc);
	mapLista.put(LISTA_AEL_POL_SUM_EXAME_TAB, listaAelPolSumExameTab);
    }

    private Integer manipularResultadoExamesMasc(Map<String, Object> mapLista, Map<String, Object> mapVariaveis,
	    List<AelPolSumMascLinha> listaAelPolSumMascLinha, List<AelPolSumExameMasc> listaAelPolSumExameMasc,
	    AelItemSolicitacaoExames itemSolicitacao, Integer vOrdemRel, Integer vEstMascLinha, Short vSeqpAnt, Integer vSoeSeqpAnt)
	    throws BaseException {
	Integer vNroMascPorPrnt;
	Integer vNroLinha;
	String vResultadoComMascara;
	vNroLinha = 0;
	mapVariaveis.put(V_NRO_LINHA, vNroLinha);
	mapVariaveis.put(V_NRO_CAMPO, 0);

	Integer vProntuarioAnt = (Integer) mapVariaveis.get(V_PRONTUARIO_ANT);
	Boolean vRecemNascidoAnt = (Boolean) mapVariaveis.get(V_RECEM_NASCIDO_ANT);
	DominioSumarioExame vPertenceSumarioAnt = (DominioSumarioExame) mapVariaveis.get(V_PERTENCE_SUMARIO_ANT);
	Date vDthrEventoLibAnt = (Date) mapVariaveis.get(V_DTHR_EVENTO_LIB_ANT);
	Short vUfeUnfSeqAnt = (Short) mapVariaveis.get(V_UFE_UNF_SEQ_ANT);
	Integer vUfeUmaManSeqAnt = (Integer) mapVariaveis.get(V_UFE_UMA_MAN_SEQ_ANT);
	Integer vOrdemAgrup = (Integer) mapVariaveis.get(V_ORDEM_AGRUP);

	if (!itemSolicitacao.getSolicitacaoExame().getAtendimento().getProntuario().equals(vProntuarioAnt)
		|| !itemSolicitacao.getSolicitacaoExame().getRecemNascido().equals(vRecemNascidoAnt)
		|| !itemSolicitacao.getAelExameMaterialAnalise().getPertenceSumario().equals(vPertenceSumarioAnt)
		|| !itemSolicitacao.getDthrLiberada().equals(vDthrEventoLibAnt)
		|| !itemSolicitacao.getUnidadeFuncional().getSeq().equals(vUfeUnfSeqAnt)
		|| !itemSolicitacao.getMaterialAnalise().getSeq().equals(vUfeUmaManSeqAnt)) {
	    mapVariaveis.put(V_PRONTUARIO_ANT, itemSolicitacao.getSolicitacaoExame().getAtendimento().getProntuario());
	    mapVariaveis.put(V_RECEM_NASCIDO_ANT, itemSolicitacao.getSolicitacaoExame().getRecemNascido());
	    mapVariaveis.put(V_PERTENCE_SUMARIO_ANT, itemSolicitacao.getAelExameMaterialAnalise().getPertenceSumario());
	    mapVariaveis.put(V_DTHR_EVENTO_LIB_ANT, itemSolicitacao.getDthrLiberada());
	    mapVariaveis.put(V_UFE_UNF_SEQ_ANT, itemSolicitacao.getUnidadeFuncional().getSeq());
	    mapVariaveis.put(V_UFE_UMA_MAN_SEQ_ANT, itemSolicitacao.getMaterialAnalise().getSeq());
	    vOrdemAgrup++;
	    mapVariaveis.put(V_ORDEM_AGRUP, vOrdemAgrup);
	}
	vOrdemRel++;
	mapVariaveis.put(V_ORDEM_REL, vOrdemRel);

	listaAelPolSumExameMasc.add(getEmitirRelatorioExamesPacientePopularListasUtilRN().executarProcedureAelPolSumInsComItem(
		itemSolicitacao, vOrdemRel, vOrdemAgrup));
	Integer vEstExameMasc = (Integer) mapVariaveis.get(V_EST_EXAME_MASC);
	vEstExameMasc = vEstExameMasc + 1;
	vNroMascPorPrnt = (Integer) mapVariaveis.get(V_NRO_MASC_POR_PRNT);
	vNroMascPorPrnt = vNroMascPorPrnt + 1;
	mapVariaveis.put(V_EST_EXAME_MASC, vEstExameMasc);
	mapVariaveis.put(V_NRO_MASC_POR_PRNT, vNroMascPorPrnt);

	// atualizarMapVariaveisAposProcedureAelPolSumInsCom(itemSolicitacao,
	// mapVariaveis);

	if (itemSolicitacao.getAelExameMaterialAnalise().getIndDependente()) {
	    manipularListaAelPolSumMascLinha(itemSolicitacao, mapVariaveis, mapLista);
	}
	if (itemSolicitacao != null && itemSolicitacao.getId() != null) {
	    manipularItemSolicitacao(itemSolicitacao, mapVariaveis);
	}
	List<AelResultadoExame> resultadoExames = getExamesLaudosFacade().listarResultadoVersaoLaudo(itemSolicitacao.getId().getSoeSeq(),
		itemSolicitacao.getId().getSeqp());
	if (resultadoExames != null) {
	    manipularResultadoExames(resultadoExames, mapVariaveis, mapLista);
	}
	mapVariaveis.put(V_POSICAO_LINHA_IMP, 0);
	vResultadoComMascara = (String) mapVariaveis.get(V_RESULTADO_COM_MASCARA);

	if (StringUtils.isNotBlank(vResultadoComMascara)) {
	    vNroLinha = vNroLinha + 1;
	    listaAelPolSumMascLinha.add(getEmitirRelatorioExamesPacientePopularListasUtilRN().obterAelPolSumMascLinha(vOrdemRel, vNroLinha,
		    vResultadoComMascara));
	    vEstMascLinha = vEstMascLinha + 1;
	    vResultadoComMascara = "";
	    mapVariaveis.put(V_NRO_LINHA, vNroLinha);
	    mapVariaveis.put(V_EST_MASC_LINHA, vEstMascLinha);
	    mapVariaveis.put(V_RESULTADO_COM_MASCARA, vResultadoComMascara);
	}
	List<AelNotaAdicional> notasAdicionais = getAelNotaAdicionalDAO().pesquisarNotaAdicionalPorSolicitacaoEItem(vSoeSeqpAnt, vSeqpAnt);
	getEmitirRelatorioExamesPacienteUtilRN().manipularNotasAdicionais(notasAdicionais, mapVariaveis, mapLista);
	return vNroMascPorPrnt;
    }

    /**
     * 
     * @param examePai
     * @param materialAnalise
     * @param listaAelPolSumMascLinha
     * @param mapVariaveis
     * @param separador
     */
    private void adicionarListaAelPolSumMascLinha(AelItemSolicitacaoExames examePai, AelExamesMaterialAnalise materialAnalise,
	    List<AelPolSumMascLinha> listaAelPolSumMascLinha, Map<String, Object> mapVariaveis, String separador) {
	Integer vNroLinha = (Integer) mapVariaveis.get(V_NRO_LINHA);
	Integer vOrdemRel = (Integer) mapVariaveis.get(V_ORDEM_REL);
	String vDescricaoPai = (String) mapVariaveis.get(V_DESCRICAO_PAI);
	AelPolSumMascLinha obj = null;
	if (examePai != null) {
	    vNroLinha = vNroLinha + 1;
	    if (StringUtils.isNotBlank(examePai.getDescMaterialAnalise())) {
		vDescricaoPai = obterDescricaoExamePai(examePai, null, separador);
		obj = getEmitirRelatorioExamesPacientePopularListasUtilRN().obterAelPolSumMascLinha(vOrdemRel, vNroLinha, vDescricaoPai);
		if (obj != null && obj.getId() != null) {
		    listaAelPolSumMascLinha.add(obj);
		}
	    } else {
		vDescricaoPai = obterDescricaoExamePai(examePai, materialAnalise, separador);
		obj = getEmitirRelatorioExamesPacientePopularListasUtilRN().obterAelPolSumMascLinha(vOrdemRel, vNroLinha, vDescricaoPai);
		if (obj != null && obj.getId() != null) {
		    listaAelPolSumMascLinha.add(obj);
		}
	    }
	} else {
	    vDescricaoPai = super.getResourceBundleValue("PAI N\u00C3O ENCONTRADO");
	    vNroLinha = vNroLinha + 1;
	    obj = getEmitirRelatorioExamesPacientePopularListasUtilRN().obterAelPolSumMascLinha(vOrdemRel, vNroLinha, vDescricaoPai);
	    if (obj != null && obj.getId() != null) {
		listaAelPolSumMascLinha.add(obj);
	    }
	}
	mapVariaveis.put(V_NRO_LINHA, vNroLinha);
	mapVariaveis.put(V_ORDEM_REL, vOrdemRel);
	mapVariaveis.put(V_DESCRICAO_PAI, vDescricaoPai);
    }

    /**
     * 
     * @param examePai
     * @param materialAnalise
     * @param vOrdemRel
     * @param vNroLinha
     * @param vDescricaoPai
     * @return
     */
    private String obterDescricaoExamePai(AelItemSolicitacaoExames examePai, AelExamesMaterialAnalise materialAnalise, String separador) {
	StringBuilder sb = new StringBuilder();
	if (materialAnalise == null) {
	    if (StringUtils.isNotBlank(separador)) {
		sb.append(examePai.getExame().getDescricao()).append(StringUtils.rightPad("", 6)).append(separador)
			.append(StringUtils.rightPad("", 9)).append(examePai.getDescMaterialAnalise());
	    } else {
		sb.append(examePai.getExame().getDescricao()).append(StringUtils.rightPad("", 15))
			.append(examePai.getDescMaterialAnalise());
	    }
	} else {
	    if (StringUtils.isNotBlank(separador)) {
		sb.append(examePai.getExame().getDescricao()).append(StringUtils.rightPad("", 6)).append(separador)
			.append(StringUtils.rightPad("", 9)).append(materialAnalise.getAelMateriaisAnalises().getDescricao());
	    } else {
		sb.append(examePai.getExame().getDescricao()).append(StringUtils.rightPad("", 15))
			.append(materialAnalise.getAelMateriaisAnalises().getDescricao());
	    }
	}
	return sb.toString();
    }

    /**
     * 
     * @param atendimento
     * @param exameMasc
     * @param mapVariaveis
     * @return
     */
    private boolean verificarAtendimento(AelItemSolicitacaoExames itemSolicitacao, Map<String, Object> mapVariaveis) {
	if (itemSolicitacao.getSolicitacaoExame() != null && itemSolicitacao.getSolicitacaoExame().getAtendimento() != null
		&& itemSolicitacao.getSolicitacaoExame().getAtendimento().getProntuario() != null
		&& itemSolicitacao.getSolicitacaoExame().getRecemNascido() != null && itemSolicitacao.getAelExameMaterialAnalise() != null
		&& itemSolicitacao.getAelExameMaterialAnalise().getPertenceSumario() != null && itemSolicitacao.getDthrLiberada() != null
		&& itemSolicitacao.getAelUnfExecutaExames() != null) {

	    if ((itemSolicitacao.getSolicitacaoExame().getAtendimento().getProntuario().equals(mapVariaveis.get(V_PRNT_CONTROLE))
		    && itemSolicitacao.getSolicitacaoExame().getRecemNascido().equals(mapVariaveis.get(V_RECEM_NASCIDO))
		    && itemSolicitacao.getAelExameMaterialAnalise().getPertenceSumario().equals(mapVariaveis.get(V_PERTENCE_SUMARIO))
		    && itemSolicitacao.getDthrLiberada().equals(mapVariaveis.get(V_DTHR_EVENTO_LIB))
		    && itemSolicitacao.getDthrLiberada().equals(mapVariaveis.get(V_DTHR_EVENTO_LIB))
		    && itemSolicitacao.getAelUnfExecutaExames().getId().getUnfSeq().equals(mapVariaveis.get(V_UFE_UNF_SEQ)) && itemSolicitacao
		    .getAelUnfExecutaExames().getId().getEmaManSeq().equals(mapVariaveis.get(V_UFE_EMA_MAN_SEQ_ANT)))) {
		return Boolean.TRUE;
	    }
	}
	return Boolean.FALSE;
    }

    /**
     * 
     * @param atendimento
     * @param mapVariaveis
     */
    private void atualizarMapVariaveis(AelItemSolicitacaoExames itemSolicitacao, Map<String, Object> mapVariaveis) {
	Integer vOrdemAgrup = (Integer) mapVariaveis.get(V_ORDEM_AGRUP);
	vOrdemAgrup = vOrdemAgrup + 1;
	mapVariaveis.put(V_PRNT_CONTROLE, itemSolicitacao.getSolicitacaoExame().getAtendimento().getProntuario());
	mapVariaveis.put(V_RECEM_NASCIDO, itemSolicitacao.getSolicitacaoExame().getRecemNascido());
	mapVariaveis.put(V_PERTENCE_SUMARIO, itemSolicitacao.getAelExameMaterialAnalise().getPertenceSumario());
	mapVariaveis.put(V_DTHR_EVENTO_LIB, itemSolicitacao.getDthrLiberada());
	mapVariaveis.put(V_UFE_UNF_SEQ, itemSolicitacao.getAelUnfExecutaExames().getId().getUnfSeq().getSeq());
	mapVariaveis.put(V_UFE_EMA_MAN_SEQ_ANT, itemSolicitacao.getAelUnfExecutaExames().getId().getEmaManSeq());
	mapVariaveis.put(V_ORDEM_AGRUP, vOrdemAgrup);
    }

    /**
     * 
     * @return
     */
    private Map<String, Object> inicializarMapVariaveis() {
	Map<String, Object> param = new HashMap<String, Object>();
	param.put(V_NRO_TAB_POR_PRNT, 0);
	param.put(V_NRO_MASC_POR_PRNT, 0);
	param.put(V_DESCRICAO_CARACTERISTICA, "");
	param.put(V_DESCRICAO_CODIFICADO, "");
	param.put(V_DESCRICAO_RESULTADO, "");
	param.put(V_DESCRICAO_NOTAS_ADICIONAIS, "");
	param.put(V_OBSERVACAO, "");
	param.put(V_LEGENDA, "");
	param.put(V_NRO_TAB_POR_PRNTV_LEGENDA, "");
	param.put(V_PRNT_CONTROLE, null);
	param.put(V_PRNT_CONTR_FIM, 0);
	param.put(V_RESULTADO_TAB, "");
	param.put(V_EST_LV_NRO_TAB_POR_PRNTEGENDA, 0);
	param.put(V_EST_LEGENDA, 0);
	param.put(V_LTO_LTO_ID, "");
	param.put(V_EST_EXAME_TAB, 0);
	param.put(V_EST_ITENS_ATU, 0);
	param.put(V_RECEM_NASCIDO, false);
	param.put(V_PERTENCE_SUMARIO, DominioSumarioExame.N);
	param.put(V_DTHR_EVENTO_LIB, Calendar.getInstance().getTime());
	param.put(V_UFE_UNF_SEQ, Short.valueOf("0"));
	param.put(V_UFE_EMA_MAN_SEQ_ANT, 0);
	param.put(V_SEQP_ANT, Short.valueOf("0"));
	param.put(V_SEQ_DEPT_ANT, Short.valueOf("0"));
	param.put(V_DESCRICAO_PAI, "");
	param.put(V_EST_MASC_LINHA, 0);
	param.put(V_EST_CAMPO_EDIT, 0);
	param.put(V_SOE_SEQ_ANT, 0);
	param.put(V_SOE_SEQ_DEPT_ANT, 0);
	param.put(V_VERSAO_EXA_SIGLA, "");
	param.put(V_VERSAO_MAN_SEQ, 0);
	param.put(V_VERSAO_SEQP, 0);
	param.put(V_RESULTADO_COM_MASCARA, "");
	param.put(V_NRO_CAMPO, 0);
	param.put(V_NRO_LINHA, 0);
	param.put(V_POSICAO_LINHA_IMP, 0);
	param.put(V_RESULTADO_AUX, "");
	param.put(V_COD_OBSERVACAO, Short.valueOf("0"));
	param.put(V_RESULTADO_TAB, "");
	param.put(V_DESCRICAO_EXAMES_MASC, "");
	param.put(V_ORDEM_REL, 0);
	param.put(V_DTHR_FIM_EXAMES_MASC, Calendar.getInstance().getTime());
	param.put(V_CAL_SEQ, 0);
	param.put(V_CAC_SEQ, 0);
	param.put(V_VALOR, 0.0);
	param.put(V_CAL_NOME, "");
	param.put(V_CAL_NOME_SUMARIO, "");
	param.put(V_RCD_GTC_SEQ, 0);
	param.put(V_RCD_SEQ, 0);
	param.put(V_DESCRICAO_EXAMES_TAB, "");
	param.put(V_DTHR_EVENTO_AREA_EXEC, Calendar.getInstance().getTime());
	param.put(V_ORDEM_EXAMES_TAB, Short.valueOf("0"));
	param.put(V_IND_IMPRIME, "");
	param.put(V_DTHR_FIM_EXAMES_TAB, Calendar.getInstance().getTime());
	param.put(V_UNF_SEQ_CONTROLE, 0);
	param.put(V_SOE_SEQP_DEPT_ANT, Short.valueOf("0"));
	param.put(V_ISE_SEQ_DEPT, Short.valueOf("0"));
	param.put(V_EST_EXAME_MASC, 0);
	param.put(V_EST_EXAME_MASC, 0);

	param.put(V_ORDEM_AGRUP, 0);
	param.put(V_PRONTUARIO_ANT, 0);
	param.put(V_RECEM_NASCIDO_ANT, false);
	param.put(V_PERTENCE_SUMARIO_ANT, DominioSumarioExame.N);
	param.put(V_DTHR_EVENTO_LIB_ANT, Calendar.getInstance().getTime());
	param.put(V_UFE_UNF_SEQ_ANT, Short.valueOf("0"));
	param.put(V_UFE_UMA_MAN_SEQ_ANT, 0);
	return param;
    }

    /**
     * 
     * @return
     */
    private Map<String, Object> inicializarMapLista() {
	Map<String, Object> param = new HashMap<String, Object>();
	param.put(LISTA_AEL_POL_SUM_LEGENDA, new ArrayList<AelPolSumLegenda>());
	param.put(LISTA_AEL_POL_SUM_EXAME_TAB, new ArrayList<AelPolSumExameTab>());
	param.put(LISTA_AEL_POL_SUM_EXAME_MASC, new ArrayList<AelPolSumExameMasc>());
	param.put(LISTA_AEL_POL_SUM_OBSERVACAO, new ArrayList<AelPolSumObservacao>());
	param.put(LISTA_AEL_POL_SUM_MASC_LINHA, new ArrayList<AelPolSumMascLinha>());
	param.put(LISTA_AEL_POL_SUM_MASC_CAMPO_EDIT, new ArrayList<AelPolSumMascCampoEdit>());
	return param;
    }

    /**
     * CF_RODAPEFormula.sql
     * 
     * @param pertenceSumarioRodape
     */
    public String preencherTextoRodape(String pertenceSumarioRodape) {
	StringBuilder textoRodape = new StringBuilder();
	if (pertenceSumarioRodape.trim().equals("B")) {
	    textoRodape.append(super.getResourceBundleValue("MENSAGEM_RODAPE_B"));
	} else if (pertenceSumarioRodape.trim().equals("H")) {
	    textoRodape.append(super.getResourceBundleValue("MENSAGEM_RODAPE_H"));
	}
	return textoRodape.toString();
    }

    /**
     * CF_IDENTIFICADORFormula.sql
     * 
     * @param pertenceIndentificador
     */
    @SuppressWarnings("unchecked")
    public String preencherIndentificador(Map<String, Object> mapLista, Integer prontuario, DominioSumarioExame petenceSumario,
	    Date dthrEvento, Boolean recemNascido) {
	StringBuilder identificador = new StringBuilder();
	List<AelPolSumObservacao> obsList = (List<AelPolSumObservacao>) mapLista.get(LISTA_AEL_POL_SUM_OBSERVACAO);
	for (AelPolSumObservacao tmpObs : obsList) {
	    if (tmpObs != null
		    && tmpObs.getId() != null
		    && (tmpObs.getId().getProntuario() != null && tmpObs.getId().getProntuario() == prontuario)
		    && (tmpObs.getId().getPertenceSumario() != null && tmpObs.getId().getPertenceSumario().equals(petenceSumario)
			    && (tmpObs.getId().getDthrEvento() != null && tmpObs.getId().getDthrEvento().equals(dthrEvento)) && (tmpObs
			    .getId().getRecemNascido() != null && tmpObs.getId().getRecemNascido().equals(recemNascido)))) {

		if (tmpObs.getId().getCodigoMensagem() != null) {
		    identificador.append('(').append(tmpObs.getId().getCodigoMensagem()).append(')');
		}
	    }
	}
	return identificador.toString();
    }

    /**
     * 
     * @return
     */
    private List<DominioSumarioExame> obterListagemSumario() {
	final List<DominioSumarioExame> listSitCodigo = new ArrayList<DominioSumarioExame>();
	listSitCodigo.add(DominioSumarioExame.B);
	listSitCodigo.add(DominioSumarioExame.E);
	listSitCodigo.add(DominioSumarioExame.G);
	listSitCodigo.add(DominioSumarioExame.H);
	return listSitCodigo;
    }

    // /**
    // * r_cursor.pertence_sumario IN ('B','E','G','H')
    // * @param lista
    // * @return
    // */
    // private boolean verificaPertenceSumario(List<AghAtendimentos> lista) {
    // for(AghAtendimentos atd : lista) {
    // for(AelSolicitacaoExames solExames : atd.getAelSolicitacaoExames()) {
    // for(AelItemSolicitacaoExames itemExames :
    // solExames.getItensSolicitacaoExame()) {
    // if(obterListagemSumario().contains(itemExames.getAelExameMaterialAnalise().getPertenceSumario()))
    // {
    // return Boolean.TRUE;
    // }
    // }
    // }
    // }
    // return Boolean.FALSE;
    // }

    /**
     * Atualiza os valores da variáveis 'globais' da procedure
     * 
     * @param mapVariaveis
     * @return
     */
    private void atualizarMapVariaveis(Map<String, Object> mapVariaveis, Integer prontuario) {
	mapVariaveis.put(V_NRO_TAB_POR_PRNT, 0);
	mapVariaveis.put(V_NRO_MASC_POR_PRNT, 0);
	mapVariaveis.put(V_PRNT_CONTROLE, prontuario);
	mapVariaveis.put("vLtoControle", "");
	mapVariaveis.put("vRnControle", DominioSimNao.N);
	mapVariaveis.put("vExaSiglaControle", "");
	mapVariaveis.put("vManSeqControle", 0);
	mapVariaveis.put(V_UNF_SEQ_CONTROLE, 0);
	mapVariaveis.put("vPertSumControle", DominioSumarioExame.N);
	mapVariaveis.put("vSoeSeqControle", 0);
	mapVariaveis.put("vIseSeqpControle", 0);
	mapVariaveis.put("vDthrControle", null);
	mapVariaveis.put(V_PRNT_CONTR_FIM, 0);
	mapVariaveis.put("vDthrFimControle", null);
    }

    /**
     * 
     * @param vLegenda
     * @param vObservacao
     * @return
     */
    private String obterResultadoTabLegenda(String vLegenda, String vObservacao) {
	String vResultadoTab = null;
	StringBuilder strResultado = new StringBuilder();
	if (StringUtils.isNotBlank(vLegenda)) {
	    if (StringUtils.isNotBlank(vObservacao)) {
		strResultado.append(vLegenda).append('\n').append(vObservacao);
		vResultadoTab = strResultado.toString();
	    } else {
		strResultado.append(vLegenda);
		vResultadoTab = strResultado.toString();
	    }
	} else {
	    if (StringUtils.isNotBlank(vObservacao)) {
		strResultado.append(vObservacao);
		vResultadoTab = strResultado.toString();
	    } else {
		vResultadoTab = null;
	    }
	}
	return vResultadoTab;
    }

    /**
     * 
     * @param mapVariaveis
     * @param listaAelPolSumExameTab
     * @param atendimentoProntuario
     */
    private void atualizarNroTabPorPrnt(Map<String, Object> mapVariaveis, Map<String, Object> mapLista,
	    List<AelPolSumExameTab> listaAelPolSumExameTab, Integer atendimentoProntuario) {

	Integer vNroTabPorPrnt = (Integer) mapVariaveis.get(V_NRO_TAB_POR_PRNT);
	Integer vNroMascPorPrnt = (Integer) mapVariaveis.get(V_NRO_MASC_POR_PRNT);
	Integer vPrntControle = (Integer) mapVariaveis.get(V_PRNT_CONTROLE);

	if (vNroTabPorPrnt == 0 && vNroMascPorPrnt > 0) {
	    AelPolSumExameTab exameTab = getEmitirRelatorioExamesPacientePopularListasUtilRN().executarProcedureAelPolSumInsSem(
		    mapVariaveis, "S");
	    if (exameTab != null) {
		exameTab.getId().setProntuario(vPrntControle);
		listaAelPolSumExameTab.add(exameTab);
	    }
	    atualizarMapVariaveis(mapVariaveis, vPrntControle);
	} else {
	    mapVariaveis.put(V_NRO_TAB_POR_PRNT, 0);
	    mapVariaveis.put(V_NRO_MASC_POR_PRNT, 0);
	    mapVariaveis.put(V_PRNT_CONTROLE, atendimentoProntuario);
	    mapLista.put(LISTA_AEL_POL_SUM_EXAME_TAB, listaAelPolSumExameTab);
	}
    }

    /**
     * 
     * @param vPrntControle
     * @param atendimento
     * @return
     */
    @SuppressWarnings("unchecked")
    private Integer obterVPrntControle(AghAtendimentos atendimento, Map<String, Object> mapVariaveis, Map<String, Object> mapLista) {
	Integer vPrntControle = (Integer) mapVariaveis.get(V_PRNT_CONTROLE);
	if (vPrntControle == null || vPrntControle.intValue() == 0) {
	    vPrntControle = atendimento.getProntuario();
	}
	if (vPrntControle != atendimento.getProntuario()) {
	    List<AelPolSumExameTab> listaAelPolSumExameTab = (List<AelPolSumExameTab>) mapLista.get(LISTA_AEL_POL_SUM_EXAME_TAB);
	    atualizarNroTabPorPrnt(mapVariaveis, mapLista, listaAelPolSumExameTab, atendimento.getProntuario());
	}
	mapVariaveis.put(V_PRNT_CONTROLE, vPrntControle);
	return vPrntControle;
    }

    @SuppressWarnings("unchecked")
    private void manipularResultadoExamesTab(AelItemSolicitacaoExames itemSolicitacao, Map<String, Object> mapVariaveis,
	    Map<String, Object> mapLista) {

	List<AelResultadoExame> resultadoExames = getAelResultadoExameDAO().listarMascaraResultadosExames(
		itemSolicitacao.getId().getSoeSeq(), itemSolicitacao.getId().getSeqp());

	Collections.sort(resultadoExames, new Comparator<AelResultadoExame>() {

	    @Override
	    public int compare(AelResultadoExame o1, AelResultadoExame o2) {
		Integer sum1 = o1.getParametroCampoLaudo().getPosicaoLinhaImpressao() + o1.getParametroCampoLaudo().getAlturaObjetoVisual();
		Integer sum2 = o2.getParametroCampoLaudo().getPosicaoLinhaImpressao() + o2.getParametroCampoLaudo().getAlturaObjetoVisual();
		int compare = sum1.compareTo(sum2);
		if (compare == 0) {
		    return o1.getParametroCampoLaudo().getPosicaoColunaImpressao()
			    .compareTo(o2.getParametroCampoLaudo().getPosicaoColunaImpressao());
		}
		return compare;
	    }
	});

	Integer vPrntControle = (Integer) mapVariaveis.get(V_PRNT_CONTROLE);
	String vObservacao = (String) mapVariaveis.get(V_OBSERVACAO);
	String vLegenda = (String) mapVariaveis.get(V_LEGENDA);
	String vResultadoTab = (String) mapVariaveis.get(V_RESULTADO_TAB);
	Short vCodObservacao = (Short) mapVariaveis.get(V_COD_OBSERVACAO);
	Boolean vRecemNascido = (Boolean) mapVariaveis.get(V_RECEM_NASCIDO);
	DominioSumarioExame vPertenceSumario = (DominioSumarioExame) mapVariaveis.get(V_PERTENCE_SUMARIO);
	String vLtoLtoId = (String) mapVariaveis.get(V_LTO_LTO_ID);
	List<AelPolSumObservacao> listaAelPolSumObservacao = (List<AelPolSumObservacao>) mapLista.get(LISTA_AEL_POL_SUM_OBSERVACAO);

	if (resultadoExames != null && resultadoExames.size() > 0) {
	    getEmitirRelatorioExamesPacienteUtilRN().manipularResultadoExames(resultadoExames, mapVariaveis, mapLista);
	    vLegenda = (String) mapVariaveis.get(V_LEGENDA);
	    vObservacao = (String) mapVariaveis.get(V_OBSERVACAO);
	}
	vResultadoTab = obterResultadoTabLegenda(vLegenda, vObservacao);
	AelPolSumObservacao obj = getEmitirRelatorioExamesPacientePopularListasUtilRN().obterAelPolSumObservacao(itemSolicitacao,
		vResultadoTab, vCodObservacao, DominioSimNao.getInstance(vRecemNascido), vPrntControle, vPertenceSumario, vLtoLtoId);
	if (obj != null && obj.getId() != null) {
	    listaAelPolSumObservacao.add(obj);
	    mapVariaveis.put(V_COD_OBSERVACAO, obj.getId().getCodigoMensagem());
	}
	// if(vObservacao != null && !"".equals(vObservacao)){
	// vCodObservacao++;
	// }
	mapVariaveis.put(V_RESULTADO_TAB, "");
	mapVariaveis.put(V_LEGENDA, "");
	mapVariaveis.put(V_OBSERVACAO, "");
	mapLista.put(LISTA_AEL_POL_SUM_OBSERVACAO, listaAelPolSumObservacao);
    }

    /**
     * 
     * @param atendimento
     * @param mapVariaveis
     */
    private void manipularItemSolicitacao(AelItemSolicitacaoExames itemSolicitacao, Map<String, Object> mapVariaveis) {

	// AelSolicitacaoExames solicitacaoExames =
	// atendimento.getAelSolicitacaoExames().iterator().next();
	// AelItemSolicitacaoExames itemSolicitacao =
	// solicitacaoExames.getItensSolicitacaoExame().iterator().next(); //
	// primeiro item da lista

	Short vSeqDeptAnt = (Short) mapVariaveis.get(V_SEQ_DEPT_ANT);
	Short vSeqpAnt = (Short) mapVariaveis.get(V_SEQP_ANT);
	Integer vSoeSeqpAnt = (Integer) mapVariaveis.get(V_SOE_SEQP_ANT);
	Integer vSoeSeqDeptAnt = (Integer) mapVariaveis.get(V_SOE_SEQ_DEPT_ANT);

	vSoeSeqpAnt = itemSolicitacao.getId().getSoeSeq();
	vSeqpAnt = itemSolicitacao.getId().getSeqp();
	if (itemSolicitacao.getItemSolicitacaoExame() != null && itemSolicitacao.getItemSolicitacaoExame().getId() != null) {
	    vSoeSeqDeptAnt = itemSolicitacao.getItemSolicitacaoExame().getId().getSoeSeq();
	    vSeqDeptAnt = itemSolicitacao.getItemSolicitacaoExame().getId().getSeqp();
	} else {
	    vSoeSeqDeptAnt = null;
	    vSeqDeptAnt = null;
	}
	mapVariaveis.put(V_SOE_SEQ_DEPT_ANT, vSoeSeqDeptAnt);
	mapVariaveis.put(V_SEQ_DEPT_ANT, vSeqDeptAnt);
	mapVariaveis.put(V_SOE_SEQP_ANT, vSoeSeqpAnt);
	mapVariaveis.put(V_SEQP_ANT, vSeqpAnt);
    }

    /**
     * 
     * @param atendimento
     * @param mapVariaveis
     * @param mapLista
     */
    @SuppressWarnings("unchecked")
    private void manipularListaAelPolSumMascLinha(AelItemSolicitacaoExames itemSolicitacao, Map<String, Object> mapVariaveis,
	    Map<String, Object> mapLista) {

	// AelSolicitacaoExames solicitacaoExames =
	// atendimento.getAelSolicitacaoExames().iterator().next();
	// AelItemSolicitacaoExames itemSolicitacao =
	// solicitacaoExames.getItensSolicitacaoExame().iterator().next();
	// AelExamesMaterialAnalise materialAnalise =
	// itemSolicitacao.getAelExameMaterialAnalise();
	AelItemSolicitacaoExames examePai = getAelItemSolicitacaoExameDAO().buscarExamePai(
		itemSolicitacao.getItemSolicitacaoExame().getId().getSoeSeq(), itemSolicitacao.getItemSolicitacaoExame().getId().getSeqp());

	Short vSeqDeptAnt = (Short) mapVariaveis.get(V_SEQ_DEPT_ANT);
	Short vSeqpAnt = (Short) mapVariaveis.get(V_SEQP_ANT);
	Integer vEstMascLinha = (Integer) mapVariaveis.get(V_EST_MASC_LINHA);
	List<AelPolSumMascLinha> listaAelPolSumMascLinha = (List<AelPolSumMascLinha>) mapLista.get(LISTA_AEL_POL_SUM_MASC_LINHA);

	if (vSeqpAnt == null) {
	    adicionarListaAelPolSumMascLinha(examePai, itemSolicitacao.getAelExameMaterialAnalise(), listaAelPolSumMascLinha, mapVariaveis,
		    null);
	    vEstMascLinha = vEstMascLinha + 1;
	} else {
	    if (vSeqDeptAnt != null) {
		adicionarListaAelPolSumMascLinha(examePai, itemSolicitacao.getAelExameMaterialAnalise(), listaAelPolSumMascLinha,
			mapVariaveis, null);
		vEstMascLinha = vEstMascLinha + 1;
	    } else {
		if (itemSolicitacao != null) {
		    Integer iseSoeSeq = itemSolicitacao.getItemSolicitacaoExame().getId().getSoeSeq();
		    Short iseSeqp = itemSolicitacao.getItemSolicitacaoExame().getId().getSeqp();
		    if ((!iseSoeSeq.equals(examePai.getId().getSoeSeq()) || !iseSeqp.equals(examePai.getId().getSeqp()))) {
			adicionarListaAelPolSumMascLinha(examePai, itemSolicitacao.getAelExameMaterialAnalise(), listaAelPolSumMascLinha,
				mapVariaveis, "-");
		    }
		}
	    }
	}
	mapVariaveis.put(V_EST_MASC_LINHA, vEstMascLinha);
    }

    /**
     * 
     * @param resultadoExames
     * @param mapVariaveis
     * @param mapLista
     * @throws BaseException
     */
    @SuppressWarnings("unchecked")
    private void manipularResultadoExames(List<AelResultadoExame> resultadoExames, Map<String, Object> mapVariaveis,
	    Map<String, Object> mapLista) throws BaseException {
	if (resultadoExames != null) {
	    for (AelResultadoExame resultadoExame : resultadoExames) {
		List<AelParametroCamposLaudo> parametrosCampoLaudo = getAelParametroCamposLaudoDAO()
			.pesquisarParametroCamposLaudoPorVersaoLaudo(resultadoExame.getParametroCampoLaudo().getId().getVelEmaExaSigla(),
				resultadoExame.getParametroCampoLaudo().getId().getVelEmaManSeq(),
				resultadoExame.getParametroCampoLaudo().getId().getVelSeqp());
		Collections.sort(parametrosCampoLaudo, new Comparator<AelParametroCamposLaudo>() {

		    @Override
		    public int compare(AelParametroCamposLaudo o1, AelParametroCamposLaudo o2) {
			Integer sum1 = zeroIfNull(o1.getPosicaoLinhaImpressao()) + zeroIfNull(o1.getAlturaObjetoVisual());
			Integer sum2 = zeroIfNull(o2.getPosicaoLinhaImpressao()) + zeroIfNull(o2.getAlturaObjetoVisual());
			int compare = sum1.compareTo(sum2);
			if (compare == 0) {
			    return o1.getPosicaoColunaImpressao().compareTo(o2.getPosicaoColunaImpressao());
			}
			return compare;
		    }
		});
		for (AelParametroCamposLaudo parametroCampoLaudo : parametrosCampoLaudo) {
		    String textoLivre = parametroCampoLaudo.getTextoLivre();
		    DominioTipoCampoCampoLaudo tipoCampo = parametroCampoLaudo.getCampoLaudo().getTipoCampo();
		    Integer campoLaudoCalSeq = parametroCampoLaudo.getId().getCalSeq();
		    // Integer campoLaudoSeq =
		    // parametroCampoLaudo.getId().getSeqp();
		    DominioObjetoVisual objetoVisual = parametroCampoLaudo.getObjetoVisual();

		    String vVersaoExaSigla = (String) mapVariaveis.get(V_VERSAO_EXA_SIGLA);
		    Integer vVersaoManSeq = (Integer) mapVariaveis.get(V_VERSAO_MAN_SEQ);
		    Integer vVersaoSeqp = (Integer) mapVariaveis.get(V_VERSAO_SEQP);

		    String vResultadoComMascara = (String) mapVariaveis.get(V_RESULTADO_COM_MASCARA);
		    Integer vNroLinha = (Integer) mapVariaveis.get(V_NRO_LINHA);
		    Integer vNroCampo = (Integer) mapVariaveis.get(V_NRO_CAMPO);
		    Integer vPosicaoLinhaImp = (Integer) mapVariaveis.get(V_POSICAO_LINHA_IMP);
		    Integer vEstMascLinha = (Integer) mapVariaveis.get(V_EST_MASC_LINHA);
		    Integer vEstCampoEdit = (Integer) mapVariaveis.get(V_EST_CAMPO_EDIT);
		    Integer vOrdemRel = (Integer) mapVariaveis.get(V_ORDEM_REL);
		    Short vSeqpAnt = (Short) mapVariaveis.get(V_SEQP_ANT);
		    Integer vSoeSeqpAnt = (Integer) mapVariaveis.get(V_SOE_SEQP_ANT);
		    List<AelPolSumMascLinha> listaAelPolSumMascLinha = (List<AelPolSumMascLinha>) mapLista
			    .get(LISTA_AEL_POL_SUM_MASC_LINHA);
		    List<AelPolSumMascCampoEdit> listaAelPolSumMascCampoEdit = (List<AelPolSumMascCampoEdit>) mapLista
			    .get(LISTA_AEL_POL_SUM_MASC_CAMPO_EDIT);

		    vVersaoExaSigla = parametroCampoLaudo.getId().getVelEmaExaSigla();
		    vVersaoManSeq = parametroCampoLaudo.getId().getVelEmaManSeq();
		    vVersaoSeqp = parametroCampoLaudo.getId().getSeqp();

		    if (DominioObjetoVisual.TEXTO_LONGO.equals(objetoVisual)) {
			if (StringUtils.isNotBlank(vResultadoComMascara)) {
			    vNroLinha = vNroLinha + 1;
			    listaAelPolSumMascLinha.add(getEmitirRelatorioExamesPacientePopularListasUtilRN().obterAelPolSumMascLinha(
				    vOrdemRel, vNroLinha, vResultadoComMascara));
			    vEstMascLinha = vEstMascLinha + 1;
			    vResultadoComMascara = "";
			    mapVariaveis.put(V_RESULTADO_COM_MASCARA, vResultadoComMascara);
			}
			vPosicaoLinhaImp = 0;
			mapVariaveis.put(V_POSICAO_LINHA_IMP, vPosicaoLinhaImp);
			if (vNroLinha == 0) {
			    vNroLinha = vNroLinha + 1;
			    listaAelPolSumMascLinha.add(getEmitirRelatorioExamesPacientePopularListasUtilRN().obterAelPolSumMascLinha(
				    vOrdemRel, vNroLinha, null));
			    vEstMascLinha = vEstMascLinha + 1;
			}
			if (StringUtils.isNotBlank(textoLivre) && !DominioTipoCampoCampoLaudo.E.equals(tipoCampo)) {
			    vNroCampo = vNroCampo + 1;
			    AelPolSumMascCampoEdit aelPolSumMascCampoEdit = getEmitirRelatorioExamesPacientePopularListasUtilRN()
				    .obterAelPolSumMascCampoEdit(vOrdemRel, vNroLinha, vNroCampo, parametroCampoLaudo.getTextoLivre());
			    if (aelPolSumMascCampoEdit != null && aelPolSumMascCampoEdit.getId() != null) {
				listaAelPolSumMascCampoEdit.add(aelPolSumMascCampoEdit);
			    }
			    vEstCampoEdit = vEstCampoEdit + 1;
			} else {
			    mapVariaveis.put(V_NRO_LINHA, vNroLinha);
			    List<AelResultadoExame> resultados = getAelResultadoExameDAO().listarResultadosExames(
				    resultadoExame.getItemSolicitacaoExame().getId().getSoeSeq(),
				    resultadoExame.getItemSolicitacaoExame().getId().getSeqp(),
				    parametroCampoLaudo.getAelVersaoLaudo().getId().getEmaExaSigla(),
				    parametroCampoLaudo.getAelVersaoLaudo().getId().getEmaManSeq(),
				    parametroCampoLaudo.getAelVersaoLaudo().getId().getSeqp(), parametroCampoLaudo.getId().getCalSeq(),
				    parametroCampoLaudo.getId().getSeqp());
			    getEmitirRelatorioExamesPacienteUtilRN().adicionarListaAelPolSumMascCampoEdit(resultados, mapVariaveis,
				    mapLista, parametroCampoLaudo);
			}
		    } else {
			Integer posicaoLinha = zeroIfNull(parametroCampoLaudo.getPosicaoLinhaImpressao())
				+ zeroIfNull(parametroCampoLaudo.getAlturaObjetoVisual());
			if (vPosicaoLinhaImp == null || vPosicaoLinhaImp.intValue() == 0) {
			    vPosicaoLinhaImp = posicaoLinha;
			}
			if (vPosicaoLinhaImp.equals(posicaoLinha)) {
			    // if(getEmitirRelatorioExamesPacienteUtilRN().verificarDominioObjetoVisualMesmaPosicao(objetoVisual,
			    // textoLivre, tipoCampo, campoLaudoCalSeq,
			    // mapVariaveis, Boolean.FALSE,resultadoExame)) {
			    if (getEmitirRelatorioExamesPacienteUtilRN().verificarDominioObjetoVisualOutraPosicao(objetoVisual, textoLivre,
				    tipoCampo, campoLaudoCalSeq, mapVariaveis, Boolean.FALSE)) {
				vResultadoComMascara = (String) mapVariaveis.get(V_RESULTADO_COM_MASCARA);
			    } else {
				List<AelResultadoExame> resultados = getAelResultadoExameDAO().listarResultadosExames(
					resultadoExame.getItemSolicitacaoExame().getId().getSoeSeq(),
					resultadoExame.getItemSolicitacaoExame().getId().getSeqp(),
					parametroCampoLaudo.getAelVersaoLaudo().getId().getEmaExaSigla(),
					parametroCampoLaudo.getAelVersaoLaudo().getId().getEmaManSeq(),
					parametroCampoLaudo.getAelVersaoLaudo().getId().getSeqp(), parametroCampoLaudo.getId().getCalSeq(),
					parametroCampoLaudo.getId().getSeqp());
				// List<AelResultadoExame> resultados =
				// getAelResultadoExameDAO().listarResultadosExames(vSoeSeqpAnt,
				// vSeqpAnt, vVersaoExaSigla, vVersaoManSeq,
				// vVersaoSeqp, campoLaudoCalSeq,
				// campoLaudoSeq);
				getEmitirRelatorioExamesPacienteUtilRN().manipularResultado(resultados, parametroCampoLaudo, mapVariaveis,
					Boolean.TRUE); // VOLTAR????
				vResultadoComMascara = (String) mapVariaveis.get(V_RESULTADO_COM_MASCARA);
			    }
			    // getEmitirRelatorioExamesPacienteUtilRN().verificarDominioObjetoVisualMesmaPosicao(objetoVisual,
			    // textoLivre, tipoCampo, campoLaudoCalSeq,
			    // mapVariaveis, Boolean.FALSE,resultadoExame);
			    // vResultadoComMascara = (String)
			    // mapVariaveis.get("vResultadoComMascara");
			} else {
			    if (StringUtils.isNotBlank(vResultadoComMascara)) {
				vNroLinha = vNroLinha + 1;
				listaAelPolSumMascLinha.add(getEmitirRelatorioExamesPacientePopularListasUtilRN().obterAelPolSumMascLinha(
					vOrdemRel, vNroLinha, vResultadoComMascara));
				vEstMascLinha = vEstMascLinha + 1;
				mapVariaveis.put(V_NRO_LINHA, vNroLinha);
				mapVariaveis.put(V_RESULTADO_COM_MASCARA, "");
				vResultadoComMascara = "";
				mapLista.put(LISTA_AEL_POL_SUM_MASC_LINHA, listaAelPolSumMascLinha);
			    }
			    vPosicaoLinhaImp = zeroIfNull(parametroCampoLaudo.getPosicaoLinhaImpressao())
				    + zeroIfNull(parametroCampoLaudo.getAlturaObjetoVisual());
			    if (getEmitirRelatorioExamesPacienteUtilRN().verificarDominioObjetoVisualOutraPosicao(objetoVisual, textoLivre,
				    tipoCampo, campoLaudoCalSeq, mapVariaveis, Boolean.TRUE)) {
				vResultadoComMascara = (String) mapVariaveis.get(V_RESULTADO_COM_MASCARA);
				mapVariaveis.put(V_RESULTADO_COM_MASCARA, vResultadoComMascara);
			    } else {
				List<AelResultadoExame> resultados = getAelResultadoExameDAO().listarResultadosExames(
					resultadoExame.getItemSolicitacaoExame().getId().getSoeSeq(),
					resultadoExame.getItemSolicitacaoExame().getId().getSeqp(),
					parametroCampoLaudo.getAelVersaoLaudo().getId().getEmaExaSigla(),
					parametroCampoLaudo.getAelVersaoLaudo().getId().getEmaManSeq(),
					parametroCampoLaudo.getAelVersaoLaudo().getId().getSeqp(), parametroCampoLaudo.getId().getCalSeq(),
					parametroCampoLaudo.getId().getSeqp());
				getEmitirRelatorioExamesPacienteUtilRN().manipularResultado(resultados, parametroCampoLaudo, mapVariaveis,
					Boolean.TRUE);
				vResultadoComMascara = (String) mapVariaveis.get(V_RESULTADO_COM_MASCARA);
			    }
			}
		    }
		    mapVariaveis.put(V_EST_MASC_LINHA, vEstMascLinha);
		    mapVariaveis.put(V_EST_CAMPO_EDIT, vEstCampoEdit);
		    mapVariaveis.put(V_NRO_LINHA, vNroLinha);
		    mapVariaveis.put(V_NRO_CAMPO, vNroCampo);
		    mapVariaveis.put(V_VERSAO_EXA_SIGLA, vVersaoExaSigla);
		    mapVariaveis.put(V_VERSAO_MAN_SEQ, vVersaoManSeq);
		    mapVariaveis.put(V_VERSAO_SEQP, vVersaoSeqp);
		    mapVariaveis.put(V_RESULTADO_COM_MASCARA, vResultadoComMascara);
		    mapVariaveis.put(V_POSICAO_LINHA_IMP, vPosicaoLinhaImp);
		    mapVariaveis.put(V_ORDEM_REL, vOrdemRel);
		    mapVariaveis.put(V_SEQP_ANT, vSeqpAnt);
		    mapVariaveis.put(V_SOE_SEQP_ANT, vSoeSeqpAnt);
		    mapLista.put(LISTA_AEL_POL_SUM_MASC_LINHA, listaAelPolSumMascLinha);
		    mapLista.put(LISTA_AEL_POL_SUM_MASC_CAMPO_EDIT, listaAelPolSumMascCampoEdit);
		}
		break;
	    }
	}
    }

    private Integer zeroIfNull(Object valor) {
	if (valor != null) {
	    return Integer.valueOf(valor.toString());
	}
	return 0;
    }

    public AelParametroCamposLaudoDAO getAelParametroCamposLaudoDAO() {
	return aelParametroCamposLaudoDAO;
    }

}
