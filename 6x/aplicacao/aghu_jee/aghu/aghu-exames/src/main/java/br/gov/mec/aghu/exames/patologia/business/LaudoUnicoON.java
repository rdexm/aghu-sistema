package br.gov.mec.aghu.exames.patologia.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.Minutes;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioFuncaoPatologista;
import br.gov.mec.aghu.dominio.DominioSecaoConfiguravel;
import br.gov.mec.aghu.dominio.DominioSismamaHistoCadCodigo;
import br.gov.mec.aghu.dominio.DominioSituacaoExamePatologia;
import br.gov.mec.aghu.dominio.DominioUnidadeTempoLiberacao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.dao.AelApXPatologistaDAO;
import br.gov.mec.aghu.exames.dao.AelConfigExLaudoUnicoDAO;
import br.gov.mec.aghu.exames.dao.AelDescMaterialApsDAO;
import br.gov.mec.aghu.exames.dao.AelDiagnosticoApsDAO;
import br.gov.mec.aghu.exames.dao.AelDiagnosticoLaudosDAO;
import br.gov.mec.aghu.exames.dao.AelExameApDAO;
import br.gov.mec.aghu.exames.dao.AelExameApItemSolicDAO;
import br.gov.mec.aghu.exames.dao.AelExamesDAO;
import br.gov.mec.aghu.exames.dao.AelExamesDependentesDAO;
import br.gov.mec.aghu.exames.dao.AelExtratoExameApDAO;
import br.gov.mec.aghu.exames.dao.AelExtratoItemSolicitacaoDAO;
import br.gov.mec.aghu.exames.dao.AelInformacaoClinicaApDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelLaminaApsDAO;
import br.gov.mec.aghu.exames.dao.AelMacroscopiaApsDAO;
import br.gov.mec.aghu.exames.dao.AelMaterialApDAO;
import br.gov.mec.aghu.exames.dao.AelMicroscopiaApsDAO;
import br.gov.mec.aghu.exames.dao.AelMotivoCancelaExamesDAO;
import br.gov.mec.aghu.exames.dao.AelNotaAdicionalApDAO;
import br.gov.mec.aghu.exames.dao.AelNotaAdicionalDAO;
import br.gov.mec.aghu.exames.dao.AelParametroCamposLaudoDAO;
import br.gov.mec.aghu.exames.dao.AelPatologistaApDAO;
import br.gov.mec.aghu.exames.dao.AelPatologistaDAO;
import br.gov.mec.aghu.exames.dao.AelResultadoExameDAO;
import br.gov.mec.aghu.exames.dao.AelSecaoConfExamesDAO;
import br.gov.mec.aghu.exames.dao.AelSitItemSolicitacoesDAO;
import br.gov.mec.aghu.exames.dao.AelTopografiaLaudosDAO;
import br.gov.mec.aghu.exames.patologia.vo.AelItemSolicitacaoExameLaudoUnicoVO;
import br.gov.mec.aghu.exames.patologia.vo.AelKitMatPatologiaVO;
import br.gov.mec.aghu.exames.patologia.vo.ConsultaItensPatologiaVO;
import br.gov.mec.aghu.exames.patologia.vo.ConsultaMaterialApVO;
import br.gov.mec.aghu.exames.patologia.vo.TelaLaudoUnicoVO;
import br.gov.mec.aghu.exames.sismama.business.ISismamaFacade;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.model.AelApXPatologista;
import br.gov.mec.aghu.model.AelConfigExLaudoUnico;
import br.gov.mec.aghu.model.AelDescMaterialAps;
import br.gov.mec.aghu.model.AelDiagnosticoAps;
import br.gov.mec.aghu.model.AelDiagnosticoLaudos;
import br.gov.mec.aghu.model.AelExameAp;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelExamesDependentes;
import br.gov.mec.aghu.model.AelInformacaoClinicaAP;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExamesId;
import br.gov.mec.aghu.model.AelLaminaAps;
import br.gov.mec.aghu.model.AelMacroscopiaAps;
import br.gov.mec.aghu.model.AelMaterialAp;
import br.gov.mec.aghu.model.AelParametroCampoLaudoId;
import br.gov.mec.aghu.model.AelParametroCamposLaudo;
import br.gov.mec.aghu.model.AelPatologista;
import br.gov.mec.aghu.model.AelPatologistaAps;
import br.gov.mec.aghu.model.AelResultadoExame;
import br.gov.mec.aghu.model.AelResultadoExameId;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelTopografiaLaudos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.commons.NumberUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;

@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength", "PMD.CouplingBetweenObjects"})
@Stateless
public class LaudoUnicoON extends BaseBusiness {

	private static final String TD_NBSP_NBSP = " <td>&nbsp;&nbsp;";

	@EJB
	private AelExameApRN aelExameApRN;

	@EJB
	private AelInformacaoClinicaAPRN aelInformacaoClinicaAPRN;

	@EJB
	private AelMaterialApRN aelMaterialApRN;

	@EJB
	private AelPatologistaApRN aelPatologistaApRN;

	private static final Log LOG = LogFactory.getLog(LaudoUnicoON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;

	@Inject
	private AelExtratoExameApDAO aelExtratoExameApDAO;

	@Inject
	private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;

	@Inject
	private AelMaterialApDAO aelMaterialApDAO;

	@Inject
	private AelExameApItemSolicDAO aelExameApItemSolicDAO;

	@Inject
	private AelResultadoExameDAO aelResultadoExameDAO;

	@Inject
	private AelPatologistaDAO aelPatologistaDAO;

	@Inject
	private AelMotivoCancelaExamesDAO aelMotivoCancelaExamesDAO;

	@EJB
	private ISolicitacaoExameFacade solicitacaoExameFacade;

	@EJB
	private IExamesFacade examesFacade;

	@Inject
	private AelPatologistaApDAO aelPatologistaApDAO;

	@Inject
	private AelApXPatologistaDAO aelApXPatologistaDAO;

	@Inject
	private AelConfigExLaudoUnicoDAO aelConfigExLaudoUnicoDAO;

	@Inject
	private AelExtratoItemSolicitacaoDAO aelExtratoItemSolicitacaoDAO;

	@Inject
	private AelSitItemSolicitacoesDAO aelSitItemSolicitacoesDAO;

	@Inject
	private AelExameApDAO aelExameApDAO;

	@Inject
	private AelNotaAdicionalDAO aelNotaAdicionalDAO;

	@Inject
	private AelExamesDependentesDAO aelExamesDependentesDAO;

	@Inject
	private AelMacroscopiaApsDAO aelMacroscopiaApsDAO;

	@Inject
	private AelMicroscopiaApsDAO aelMicroscopiaApsDAO;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@Inject
	private AelExamesDAO aelExamesDAO;

	@EJB
	private IParametroFacade parametroFacade;

	@Inject
	private AelInformacaoClinicaApDAO aelInformacaoClinicaApDAO;

	@Inject
	private AelDiagnosticoApsDAO aelDiagnosticoApsDAO;

	@Inject
	private AelNotaAdicionalApDAO aelNotaAdicionalApDAO;

	@Inject
	private AelParametroCamposLaudoDAO aelParametroCamposLaudoDAO;

	@Inject
	private AelSecaoConfExamesDAO aelSecaoConfExamesDAO;

	@Inject
	private AelDescMaterialApsDAO aelDescMaterialApsDAO;

	@Inject
	private AelLaminaApsDAO aelLaminaApsDAO;

	@Inject
	private AelTopografiaLaudosDAO aelTopografiaLaudosDAO;

	@Inject
	private AelDiagnosticoLaudosDAO aelDiagnosticoLaudosDAO;

	@EJB
	private ISismamaFacade sismamaFacade;
	
	@EJB
	private LaudoUnicoApoioON laudoUnicoApoioON;

	private static final long serialVersionUID = 7189790469844518795L;

	private static final String sitCodigo = "CA";
	private static final String QUEBRA_LINHA = "\n";
	private static final String ESPAÇO = "&nbsp;";

	// private static final NumeroApConverter apConverter = new
	// NumeroApConverter();

	private enum LaudoUnicoONExceptionCode implements BusinessExceptionCode {
		AEL_03397, AEL_02712, AEL_02734, AEL_02697, AEL_03403, AEL_02597, AEL_03398, AEL_03405, AEL_02600, AEL_02852, AEL_02853, AEL_02854, AEL_02699, AEL_02643, AEL_02670, AEL_02641, AEL_02642, AEL_02644, AEL_03248, AEL_03402, AEL_03400, AEL_03404, AEL_01987, AEL_ERRO_CANCEL_EXAME, AEL_ERRO_ESTORNAR_EXAME, MSG_SERVIDOR_PATOLOGISTA_CONTRATADO_PROFESSOR, AEL_ERRO_ANULACAO_LAUDO, MSG_PACIENTE_HISTORICO_LAUDO_UNICO;
	}

	/**
	 * ORADB: WHEN-BUTTON-PRESSED - BUT_PESQ_SOLIC && BUT_CANCELA
	 * 
	 * Cursores c_itens iguais, exoto pelo cursor @hist, não utilizado em
	 * But_cancela
	 * 
	 * @param aelExameAp
	 * @return
	 */
	public AelItemSolicitacaoExameLaudoUnicoVO obterAelItemSolicitacaoExameLaudoUnicoVO(final AelExameAp aelExameAp,
			final boolean isPesqSolic) {
		List<AelItemSolicitacaoExameLaudoUnicoVO> itens = getAelExameApItemSolicDAO().obterAelItemSolicitacaoExamesPorLuxSeq(
				aelExameAp.getSeq());

		if (itens != null && !itens.isEmpty()) {
			return itens.get(0);

		} else if (isPesqSolic) {
			itens = getAelExameApItemSolicDAO().obterAelItemSolicitacaoExamesHistPorLuxSeq(aelExameAp.getSeq());

			if (itens != null && !itens.isEmpty()) {
				return itens.get(0);
			}

		}

		return null;
	}

	/**
	 * ORADB: POST-QUERY - LUX
	 */
	public void postQueryLux(final TelaLaudoUnicoVO tela, final AelExameAp aelExameAp) {
		tela.setExame(aelExameAp.getConfigExLaudoUnico().getNome());
		tela.setEtapasLaudo(aelExameAp.getEtapasLaudo().getDescricao());

		if (aelExameAp.getIndRevisaoLaudo()) {
			tela.setSituacao("REVISAR LAUDO");
		} else {
			tela.setSituacao(null);
		}

		//tela.setAelAnatomoPatologicoOrigem(aelExameAp.getAelAnatomoPatologicoOrigem());

		switch (aelExameAp.getSituacao()) {
		case A:
			tela.setSituacao("LAUDO ANULADO");
			tela.onOff(false);
			break;

		case C:
			tela.setSituacao("LAUDO CANCELADO");
			tela.onOff(false);
			break;

		case I:
			if (aelExameAp.getIndRevisaoLaudo()) {
				tela.setSituacao("REVISAR LAUDO");

			} else {
				tela.setSituacao(null);
			}

			tela.onOff(true);
			break;

		case R:
			if (aelExameAp.getIndRevisaoLaudo()) {
				tela.setSituacao("REVISAR LAUDO");

			} else {
				tela.setSituacao(null);
			}

			tela.onOff(true);
			break;
		}
	}

	/**
	 * ORADB: AELP_ATUALIZA_TELA
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public void aelpAtualizaTela(final TelaLaudoUnicoVO tela) throws BaseException {

		final AelExameAp aelExameAp;
		if (tela.getAelExameAp() != null) {
			aelExameAp = getAelExameApDAO().merge(tela.getAelExameAp());

		} else {
			aelExameAp = getAelExameApDAO().obterAelExameApPorAelAnatomoPatologicos(tela.getAelAnatomoPatologico());

		}
		tela.setAelExameAp(aelExameAp);
		if (tela.getNumeroAp() == null) {
			tela.setNumeroAp(tela.getAelAnatomoPatologico().getNumeroAp());
		}

		final List<AelItemSolicitacaoExames> itemSolics = getAelItemSolicitacaoExameDAO().obterAelItemSolicitacaoExamesPorNumeroAP(
				tela.getNumeroAp(), tela.getConfigExame().getSeq());

		if (itemSolics != null && !itemSolics.isEmpty()) {
			final AelItemSolicitacaoExames aelItemSolicitacaoExame = itemSolics.get(0);

			final AelSolicitacaoExames aelSolEx = aelItemSolicitacaoExame.getSolicitacaoExame();

			tela.setNomePaciente(getExamesFacade().buscarLaudoNomePaciente(aelSolEx));
			tela.setProntuario(getExamesFacade().buscarLaudoProntuarioPaciente(aelSolEx));
			tela.setConvenioPlano(getExamesFacade().buscarConvenioPlano(aelSolEx.getConvenioSaudePlano()));

			tela.setRenderLaudo(true);

		} else {
			throw new ApplicationBusinessException(LaudoUnicoONExceptionCode.MSG_PACIENTE_HISTORICO_LAUDO_UNICO);
		}

		postQueryLux(tela, aelExameAp);

		final AelConfigExLaudoUnico config = aelExameAp.getConfigExLaudoUnico();
		if (DominioSituacaoExamePatologia.RE.equals(aelExameAp.getEtapasLaudo())) {
			tela.setConcluirMacro(true);
			tela.setStConcluirMacro(true);

			tela.setConcluirDiagnostico(true);
			tela.setStConcluirDiagnostico(true);
			tela.setAssinarLaudo(true);

			if (config.getMacro()) {

				tela.setStMacroscopia(true);

				tela.setStDiagnostico(true);
				tela.setStModalDiagnostico(true);
				tela.setStConcluirDiagnostico(false);

				tela.setStTopografia(true);
				tela.setStNomenclatura(true);

				tela.setStNeoplasiaMaligna(true);
				tela.setStMargemComprometida(true);

				tela.setStAssinaLaudo(false);

				tela.setStCancelaDept(false);
				tela.setStCancelaEst(false);

			} else {
				tela.setStMacroscopia(false);
				tela.setStConcluirMacro(false);

				tela.setStDiagnostico(true);
				tela.setConcluirDiagnostico(true);
				tela.setStConcluirDiagnostico(true);
				tela.setStModalDiagnostico(true);

				tela.setStNeoplasiaMaligna(true);
				tela.setStMargemComprometida(true);

				tela.setStTopografia(true);
				tela.setStNomenclatura(true);

				tela.setStAssinaLaudo(false);

				tela.setStCancelaDept(false);
				tela.setStCancelaEst(false);
			}

		} else if (DominioSituacaoExamePatologia.MC.equals(aelExameAp.getEtapasLaudo())) {

			tela.setStMacroscopia(false);
			tela.setConcluirMacro(false);
			tela.setStReabrirMacro(true);

			tela.setAssinarLaudo(true);
			tela.setStAssinaLaudo(false);

			tela.setStDiagnostico(true);
			tela.setConcluirDiagnostico(true);
			tela.setStConcluirDiagnostico(true);
			tela.setStModalDiagnostico(true);

			tela.setStNeoplasiaMaligna(true);
			tela.setStMargemComprometida(true);

			tela.setStTopografia(true);
			tela.setStNomenclatura(true);

			tela.setStCancelaDept(false);
			tela.setStCancelaEst(false);

		} else if (DominioSituacaoExamePatologia.DC.equals(aelExameAp.getEtapasLaudo())) {
			tela.setStMacroscopia(false);
			tela.setConcluirMacro(false);
			tela.setStReabrirMacro(false);

			tela.setAssinarLaudo(true);
			tela.setStAssinaLaudo(true);

			tela.setStDiagnostico(false);
			tela.setStModalDiagnostico(false);
			tela.setConcluirDiagnostico(false);
			tela.setStReabrirDiagnostico(true);

			tela.setStNeoplasiaMaligna(false);
			tela.setStMargemComprometida(false);

			tela.setStTopografia(false);
			tela.setStNomenclatura(false);

			tela.setStCancelaDept(false);
			tela.setStCancelaEst(false);

		} else if (DominioSituacaoExamePatologia.LA.equals(aelExameAp.getEtapasLaudo())) {

			tela.onOff(false);
			tela.onOffNotaAdicional(true);
			tela.setStAssinaLaudo(true);
			tela.setStCancelaDept(true);

			if (aelcHabilitaPatologista(config.getServidor())) {
				tela.setStCancelaEst(true);
			} else {
				tela.setStCancelaEst(false);
			}

		} else {
			tela.setConcluirMacro(false);
			tela.setStReabrirMacro(false);

			tela.setConcluirDiagnostico(false);
			tela.setStReabrirDiagnostico(false);

			tela.setAssinarLaudo(false);
		}

		tela.setRenderLamina(config.getLamina());

		// Seta flag no caso da etapa ser LAUDO ASSINADO
		tela.setLaudoAssinado(DominioSituacaoExamePatologia.LA.equals(aelExameAp.getEtapasLaudo()));

	}

	/**
	 * ORADB: aelc_habilita_patologista
	 */
	public boolean aelcHabilitaPatologista(final RapServidores servidor) {
		final AelPatologista patologista = getAelPatologistaDAO().obterAelPatologistaAtivoPorServidorEFuncao(servidor,
				DominioFuncaoPatologista.C, DominioFuncaoPatologista.P);
		return (patologista != null);
	}

	/***
	 * Lista os materiais de um laudo unico e retorna em um VO
	 * 
	 * @param luxSeq
	 * @param etapaLaudo
	 * @return
	 */
	public List<AelKitMatPatologiaVO> listaMateriais(Long luxSeq, DominioSituacaoExamePatologia etapaLaudo) {
		List<AelKitMatPatologiaVO> listaVO = new ArrayList<AelKitMatPatologiaVO>();
		List<AelMaterialAp> lista = getAelMaterialApDAO().obterAelMaterialApPorAelExameAps(luxSeq);
		for (AelMaterialAp materialAp : lista) {

			if (DominioSituacaoExamePatologia.LA.equals(etapaLaudo)) {
				listaVO.add(new AelKitMatPatologiaVO(materialAp, aelcSituacaoExame(materialAp.getSeq()), etapaLaudo));
			} else {
				listaVO.add(new AelKitMatPatologiaVO(materialAp, aelcCancelaExame(materialAp.getSeq()), etapaLaudo));
			}
		}

		return listaVO;
	}

	public void atualizaMateriaisVO(List<AelKitMatPatologiaVO> listaVO) {
		for (AelKitMatPatologiaVO vo : listaVO) {
			vo.setMaterialAp(getAelMaterialApDAO().obterPorChavePrimaria(vo.getMaterialAp().getSeq()));
		}

		Collections.sort(listaVO, new Comparator<AelKitMatPatologiaVO>() {
			public int compare(AelKitMatPatologiaVO vo1, AelKitMatPatologiaVO vo2) {
				return vo1.getMaterialAp().getOrdem().compareTo(vo2.getMaterialAp().getOrdem());
			}
		});
	}

	/**
	 * Referente a função que está no forms: AELC_CANCELA_EXAME
	 * 
	 * Essa função foi migrada diferente do AGH, pois o retorno null representa
	 * a ausencia do botão para ativar/inativar a imunohistoquimica
	 */
	public Boolean aelcCancelaExame(Long lurSeq) {

		Boolean retorno = null;

		List<ConsultaItensPatologiaVO> listaConsultaItensPatologiaVO = getAelExamesDependentesDAO().listaExamesDependentes(lurSeq);
		if (listaConsultaItensPatologiaVO != null && !listaConsultaItensPatologiaVO.isEmpty()) {
			ConsultaItensPatologiaVO vo = listaConsultaItensPatologiaVO.get(0);

			if (vo.getIseSoeSeq() != null) {
				String exaSiglaPai = null;
				Integer manSeqPai = null;
				AelItemSolicitacaoExames itemSolicitacaoExame = getAelItemSolicitacaoExameDAO().obterPorChavePrimaria(
						new AelItemSolicitacaoExamesId(vo.getIseSoeSeq(), vo.getIseSeqp()));

				if (itemSolicitacaoExame != null) {
					exaSiglaPai = itemSolicitacaoExame.getExame().getSigla();
					manSeqPai = itemSolicitacaoExame.getMaterialAnalise().getSeq();
				}

				if (exaSiglaPai != null) {
					List<AelExamesDependentes> listaExames = getAelExamesDependentesDAO().listaExamesDependentesPorExaSiglaEManSeq(
							exaSiglaPai, manSeqPai);
					if (!listaExames.isEmpty()) {
						retorno = Boolean.TRUE;
					} else {
						retorno = Boolean.FALSE;
					}
				}
			}
		}

		return retorno;
	}

	/**
	 * Referente a função que está no forms: AELC_SITUACAO_EXAME
	 * 
	 * Essa função foi migrada diferente do AGH, pois o retorno null representa
	 * a ausencia do botão para ativar/inativar a imunohistoquimica
	 */
	public Boolean aelcSituacaoExame(Long lurSeq) {

		Boolean retorno = null;

		List<ConsultaItensPatologiaVO> listaConsultaItensPatologiaVO = getAelExamesDependentesDAO().listaExamesDependentes(lurSeq);
		if (listaConsultaItensPatologiaVO != null && !listaConsultaItensPatologiaVO.isEmpty()) {
			ConsultaItensPatologiaVO vo = listaConsultaItensPatologiaVO.get(0);
			if (sitCodigo.equals(vo.getSitCodigo())) {
				retorno = Boolean.TRUE;
			} else {
				retorno = Boolean.FALSE;
			}
		}

		return retorno;
	}

	/**
	 * ORADB Procedure que está no forms: AELP_CARREGA_PATOLOGISTAS
	 * 
	 * @param aelAnatomoPatologico
	 * @throws BaseException
	 */
	public void carregaPatologistas(TelaLaudoUnicoVO telaVo) throws BaseException {
		List<AelPatologistaAps> listaPatologistaAp = getAelPatologistaApDAO().listarPatologistaApPorLuxSeq(telaVo.getAelExameAp().getSeq());

		if (listaPatologistaAp.isEmpty()) { // se a lista ainda não foi
											// carregada persiste
			List<AelApXPatologista> listaPatologistas = getAelApXPatologistaDAO().listarAelApXPatologistaAtivos(
					telaVo.getAelAnatomoPatologico().getSeq(), null);

			Collections.sort(listaPatologistas, new Comparator<AelApXPatologista>() {
				public int compare(AelApXPatologista vo1, AelApXPatologista vo2) {
					if (DominioFuncaoPatologista.P.equals(vo1.getAelPatologista().getFuncao())) {
						return -1;
					}
					if (DominioFuncaoPatologista.C.equals(vo1.getAelPatologista().getFuncao())) {
						return 0;
					}
					return 1;
				}
			});

			short cont = 0;
			for (AelApXPatologista apXPatologista : listaPatologistas) {
				AelPatologistaAps patologistaAp = new AelPatologistaAps();
				patologistaAp.setAelExameAps(telaVo.getAelExameAp());
				patologistaAp.setOrdemMedicoLaudo(++cont);

				patologistaAp.setServidor(apXPatologista.getAelPatologista().getServidor());

				getAelPatologistaApRN().persistirAelPatologistaAps(patologistaAp);
			}
		}
	}

	public void atualizaMateriais(final AelExameAp aelExameAp, final AelItemSolicitacaoExamesId itemSolicitacaoId) throws BaseException {
		if (aelExameAp != null && itemSolicitacaoId != null) {
			final AelItemSolicitacaoExames itemSolicitacao = this.getAelItemSolicitacaoExameDAO().obterPorChavePrimaria(itemSolicitacaoId);
			List<AelMaterialAp> listaMaterialAp = getAelMaterialApDAO().obterAelMaterialApPorAelExameAps(aelExameAp.getSeq());
			AghParametros param = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_COD_NAO_COLETAVEL);
			Integer codNaoColetavel = param.getVlrNumerico().intValue();

			int cont = 0;
			if (!listaMaterialAp.isEmpty()) {
				cont = listaMaterialAp.size();
			}

			String regiaoAnatomica = null;
			if (itemSolicitacao.getDescRegiaoAnatomica() == null) {
				if (itemSolicitacao.getRegiaoAnatomica() != null) {
					regiaoAnatomica = itemSolicitacao.getRegiaoAnatomica().getDescricao();
				}
			} else {
				regiaoAnatomica = itemSolicitacao.getDescRegiaoAnatomica();
			}

			StringBuffer material = new StringBuffer();

			if (itemSolicitacao.getDescMaterialAnalise() == null) {
				if (itemSolicitacao.getMaterialAnalise() != null
						&& !CoreUtil.igual(itemSolicitacao.getMaterialAnalise().getSeq(), codNaoColetavel)) {
					material.append(itemSolicitacao.getMaterialAnalise().getDescricao());
				}
			} else {
				material.append(itemSolicitacao.getDescMaterialAnalise());
			}

			if (regiaoAnatomica != null) {
				String aux = material.toString();
				material = new StringBuffer();
				material.append(regiaoAnatomica).append(':').append(aux);
			}
			// insere
			AelMaterialAp materialAp = new AelMaterialAp();

			materialAp.setAelExameAp(aelExameAp);
			materialAp.setMaterial(material.toString());

			materialAp.setOrdem((short) (++cont));
			materialAp.setItemSolicitacaoExame(itemSolicitacao);

			getAelMaterialApRN().persistirAelMaterialAp(materialAp);
		}
	}

	/**
	 * ORADB Procedure que está no forms: AELP_ATUALIZA_MATERIAIS
	 * 
	 * @param aelAnatomoPatologico
	 * @throws BaseException
	 */
	public void atualizaMateriais(AelExameAp aelExameAp) throws BaseException {
		List<AelMaterialAp> listaMaterialAp = getAelMaterialApDAO().obterAelMaterialApPorAelExameAps(aelExameAp.getSeq());

		AghParametros param = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_COD_NAO_COLETAVEL);
		Integer codNaoColetavel = param.getVlrNumerico().intValue();

		List<ConsultaMaterialApVO> listaMateriaisExameAp = getAelItemSolicitacaoExameDAO().listaMateriaisExameAp(aelExameAp.getSeq());
		short cont = 0;
		if (!listaMaterialAp.isEmpty()) {
			cont = (short) listaMaterialAp.size();
		}
		for (ConsultaMaterialApVO vo : listaMateriaisExameAp) {
			String regiaoAnatomica = null;

			if (vo.getDescRegiaoAnatomica() == null) {
				regiaoAnatomica = vo.getDescricaoRegiaoAnat();
			} else {
				regiaoAnatomica = vo.getDescRegiaoAnatomica();
			}

			StringBuffer material = new StringBuffer();
			if (vo.getDescMaterialAnalise() == null) {
				material.append(vo.getDescricaoMaterial());
			} else {
				// --Daniel Castro - 25/05/2010 - AEL010 - QP5
				// --Suprimir a descricao do material de analise quando o cadigo
				// do material da analise
				// --for igual ao parametro 'P_COD_NAO_COLETAVEL'
				if (!CoreUtil.igual(vo.getCodMaterialAnalise(), codNaoColetavel)) {
					material.append(vo.getDescMaterialAnalise());
				}
			}

			if (regiaoAnatomica != null) {
				String aux = material.toString();
				material = new StringBuffer();
				material.append(regiaoAnatomica).append(':').append(aux);
			}

			// insere
			AelMaterialAp materialAp = new AelMaterialAp();

			materialAp.setAelExameAp(aelExameAp);
			materialAp.setMaterial(material.toString());

			materialAp.setOrdem(++cont);
			materialAp.setItemSolicitacaoExame(this.getAelItemSolicitacaoExameDAO().obterItemSolicitacaoExamePorId(
					new AelItemSolicitacaoExamesId(vo.getIseSoeSeq(), vo.getIseSeqp())));

			getAelMaterialApRN().persistirAelMaterialAp(materialAp);
		}
		// }

	}

	/**
	 * @param aelAnatomoPatologico
	 * @throws BaseException
	 */
	public void atualizaInformacoesClinicas(TelaLaudoUnicoVO telaVo) throws BaseException {
		AelInformacaoClinicaAP info = getAelInformacaoClinicaApDAO().obterAelInformacaoClinicaApPorAelExameAps(
				telaVo.getAelExameAp().getSeq());

		// Caso Informação Clinica não existe, busca a mesma da Solicitação do
		// Exame e grava
		if (info == null || info.getInformacaoClinica() == null) {
			final List<AelItemSolicitacaoExames> itemSolics = getAelItemSolicitacaoExameDAO().obterAelItemSolicitacaoExamesPorNumeroAP(
					telaVo.getAelAnatomoPatologico().getNumeroAp(), telaVo.getConfigExame().getSeq());

			if (itemSolics != null && !itemSolics.isEmpty()) {
				// No cursor (c_ap) do form, esta limitando a uma linha (AND
				// ROWNUM = 1;)
				final AelItemSolicitacaoExames aelItemSolicitacaoExame = itemSolics.get(0);

				final AelSolicitacaoExames aelSolEx = aelItemSolicitacaoExame.getSolicitacaoExame();
				if (!StringUtils.isEmpty(aelSolEx.getInformacoesClinicas())) {
					info = new AelInformacaoClinicaAP();
					info.setAelExameAp(telaVo.getAelExameAp());
					info.setInformacaoClinica(aelSolEx.getInformacoesClinicas());

					getAelInformacaoClinicaAPRN().persistir(info);
				}
			}
		}
	}

	public Boolean assinarLaudo(Long numeroAp, Long luxSeq, Integer lu2Seq, AelDiagnosticoAps diagnostico,
			List<AelKitMatPatologiaVO> listaMateriais, String nomeMicrocomputador) throws BaseException {
		this.verificarSismama(numeroAp, lu2Seq);
		// CÓDIGO EQUIVALENTE A AELP_VERIFICA_PATOLOGISTAS
		if (!getAelPatologistaApDAO().verificaPatologitaPeloExameSeq(luxSeq)) {
			throw new ApplicationBusinessException(LaudoUnicoONExceptionCode.AEL_02734);
		}

		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		aelpAtualizaPatologistas(luxSeq);
		AelExameAp exame = getAelExameApDAO().obterPorChavePrimaria(luxSeq);
		exame.setEtapasLaudo(DominioSituacaoExamePatologia.LA);
		exame.setIndRevisaoLaudo(false);
		exame.setServidorRespLaudo(servidorLogado);

		try {
			getAelExameApRN().atualizarAelExameApRN(exame);
		} catch (BaseException e) {
			throw new ApplicationBusinessException(LaudoUnicoONExceptionCode.AEL_02597, e.getMessage());
		}

		try {
			this.atualizarSolicitacao(luxSeq, nomeMicrocomputador);
		} catch (BaseException e) {
			throw new ApplicationBusinessException(LaudoUnicoONExceptionCode.AEL_02597, e.getMessage());
		}

		try {
			// Realizar o cancelamento dos exames dependentes ao assinar o laudo
			// único
			this.aelpCancExaLaudoUnico(listaMateriais, nomeMicrocomputador);
		} catch (BaseException e) {
			throw new ApplicationBusinessException(LaudoUnicoONExceptionCode.AEL_03405, e.getMessage());
		}
		// LAUDO ASSINADO
		return true;
	}

	private void atualizarSolicitacao(Long luxSeq, String nomeMicrocomputador) throws BaseException {
		Integer vSoeSeq = null;
		IParametroFacade parametroFacade = this.getParametroFacade();
		AghParametros pCalc = parametroFacade.obterAghParametro(AghuParametrosEnum.P_CAL_SEQ_LAUDO_UNICO);
		AghParametros pSitLiberada = parametroFacade.obterAghParametro(AghuParametrosEnum.P_SITUACAO_LIBERADO);
		AghParametros pSitAreaExec = parametroFacade.obterAghParametro(AghuParametrosEnum.P_SITUACAO_NA_AREA_EXECUTORA);
		AghParametros pSitExec = parametroFacade.obterAghParametro(AghuParametrosEnum.P_SITUACAO_EXECUTANDO);

		// ITENS
		List<ConsultaItensPatologiaVO> itensExame = getAelExameApItemSolicDAO().listaExamesComVersaoLaudo(luxSeq,
				pCalc.getVlrNumerico().intValue(),
				new String[] { pSitLiberada.getVlrTexto(), pSitAreaExec.getVlrTexto(), pSitExec.getVlrTexto() });
		for (ConsultaItensPatologiaVO item : itensExame) {
			AelItemSolicitacaoExames solicitacao = getAelItemSolicitacaoExameDAO().obterPorChavePrimaria(
					new AelItemSolicitacaoExamesId(item.getIseSoeSeq(), item.getIseSeqp()));

			solicitacao.setSituacaoItemSolicitacao(getAelSitItemSolicitacoesDAO().obterPorChavePrimaria(pSitLiberada.getVlrTexto()));
			try {
				getSolicitacaoExameFacade().atualizar(solicitacao, nomeMicrocomputador);
			} catch (BaseException e) {
				throw new ApplicationBusinessException(LaudoUnicoONExceptionCode.AEL_02642, e.getLocalizedMessage());
			}

			if (vSoeSeq == null) {
				vSoeSeq = item.getIseSoeSeq();
			}
		}
		if (getAelItemSolicitacaoExameDAO().quantidadeExamesPendentesLaudoAssiando(vSoeSeq) > 0) {
			throw new ApplicationBusinessException(LaudoUnicoONExceptionCode.AEL_03397);
		}
	}

	private void anularLaudo(Long luxSeq, String nomeMicrocomputador) throws BaseException {
		AghParametros pCalc = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_CAL_SEQ_LAUDO_UNICO);
		AghParametros pSitLiberada = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_SITUACAO_LIBERADO);
		AghParametros pSitAreaExec = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_SITUACAO_NA_AREA_EXECUTORA);
		AghParametros pSitExec = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_SITUACAO_EXECUTANDO);

		List<ConsultaItensPatologiaVO> itensExame = getAelExameApItemSolicDAO().listaExamesComVersaoLaudo(luxSeq,
				pCalc.getVlrNumerico().intValue(),
				new String[] { pSitLiberada.getVlrTexto(), pSitAreaExec.getVlrTexto(), pSitExec.getVlrTexto() });
		Long quantidade = 0L;
		for (ConsultaItensPatologiaVO item : itensExame) {
			quantidade = getAelResultadoExameDAO().quantidadeResultadosExame(item.getIseSoeSeq(), item.getIseSeqp(),
					item.getUfeEmaExaSigla(), item.getUfeEmaManSeq(), item.getVelSeqp(), item.getCalSeq(), item.getSeqp());
			if (quantidade > 0) {
				List<AelResultadoExame> exames = getAelResultadoExameDAO().listarResultadosExameAnulados(item.getIseSoeSeq(),
						item.getIseSeqp(), item.getUfeEmaExaSigla(), item.getUfeEmaManSeq());
				for (AelResultadoExame exame : exames) {
					exame.setAnulacaoLaudo(true);
					try {
						getExamesFacade().atualizarAelResultadoExame(exame, nomeMicrocomputador);
					} catch (ApplicationBusinessException e){
						throw new ApplicationBusinessException(LaudoUnicoONExceptionCode.AEL_01987, e.getLocalizedMessage(), Severity.INFO);
					}catch (BaseException e) {
						throw new ApplicationBusinessException(LaudoUnicoONExceptionCode.AEL_02699, e.getLocalizedMessage());
					}
				}
			}
		}
	}

	public Boolean reabrirLaudo(Long numeroAp, Long luxSeq, Integer lu2Seq, DominioSituacaoExamePatologia etapasLaudo,
			AelDiagnosticoAps diagnostico, List<AelKitMatPatologiaVO> listaMateriais, String nomeMicrocomputador) throws BaseException {
		this.verificarSismama(numeroAp, lu2Seq);

		Short tempoAposLib = null;
		DominioUnidadeTempoLiberacao unidTempoLib = null;
		AelConfigExLaudoUnico configExLaudoUnico = getAelConfigExLaudoUnicoDAO().obterPorChavePrimaria(lu2Seq);
		tempoAposLib = configExLaudoUnico.getTempoAposLib();
		unidTempoLib = configExLaudoUnico.getUnidTempoLib();

		if (tempoAposLib == null || unidTempoLib == null) {
			tempoAposLib = 0;
			unidTempoLib = DominioUnidadeTempoLiberacao.H;
		}

		if (DominioUnidadeTempoLiberacao.H.equals(unidTempoLib)) {
			Date d = getAelExtratoExameApDAO().obterMaxCriadoEmPorLuxSeqEEtapasLaudo(luxSeq, DominioSituacaoExamePatologia.LA);

			DateTime dtInicial = new DateTime(d);
			DateTime dtFinal = new DateTime(new Date());
			Minutes minutes = Minutes.minutesBetween(dtInicial, dtFinal);

			if (minutes.getMinutes() >= (tempoAposLib * 60)) {
				throw new ApplicationBusinessException(LaudoUnicoONExceptionCode.AEL_02697);
			}
		} else {
			Date d = getAelExtratoExameApDAO().obterMaxCriadoEmPorLuxSeqEEtapasLaudo(luxSeq, DominioSituacaoExamePatologia.LA);
			if ((DateUtil.obterQtdDiasEntreDuasDatasTruncadas(DateUtil.truncaData(d), new Date())) >= tempoAposLib) {
				throw new ApplicationBusinessException(LaudoUnicoONExceptionCode.AEL_02697);
			}
		}

		AelExameAp exame = getAelExameApDAO().obterPorChavePrimaria(luxSeq);
		exame.setEtapasLaudo(etapasLaudo);
		exame.setServidorRespLaudo(null);
		try {
			getAelExameApRN().atualizarAelExameApRN(exame);
		} catch (BaseException e) {
			throw new ApplicationBusinessException(LaudoUnicoONExceptionCode.AEL_02600, e.getMessage());
		}

		try {
			this.aelpEstornaExaLaudo(listaMateriais, nomeMicrocomputador);
		} catch (BaseException e) {
			throw new ApplicationBusinessException(LaudoUnicoONExceptionCode.AEL_03405, e.getMessage());
		}

		try {
			this.anularLaudo(luxSeq, nomeMicrocomputador);
		} catch (BaseException e) {
			throw new ApplicationBusinessException(LaudoUnicoONExceptionCode.AEL_ERRO_ANULACAO_LAUDO, e.getMessage());
		}
		// LAUDO REABERTO PARA ASSINATURA
		return false;
	}

	private void verificarSismama(Long numeroAp, Integer lu2Seq) throws BaseException {
		ISismamaFacade sismamaFacade = getSismamaFacade();
		if (sismamaFacade.verificarExamesSismamaPorNumeroAp(numeroAp, lu2Seq)) {
			if ((sismamaFacade.obterRespostaSismamaHistoPorNumeroApECodigoCampo(numeroAp, DominioSismamaHistoCadCodigo.C_RES_PROCIR.name(),
					lu2Seq)).isEmpty()) {
				throw new ApplicationBusinessException(LaudoUnicoONExceptionCode.AEL_03248);
			}
		}
	}

	/**
	 * Referente a função que está no forms: BUT_ASSINA_LAUDO : TRIGGER - WHEN
	 * BUTTON PRESSED
	 */
	public Boolean assinarReabrirLaudo(Long numeroAp, Long luxSeq, Integer lu2Seq, DominioSituacaoExamePatologia etapasLaudo,
			AelDiagnosticoAps diagnostico, List<AelKitMatPatologiaVO> listaMateriais, String nomeMicrocomputador) throws BaseException {
		ISismamaFacade sismamaFacade = getSismamaFacade();
		if (sismamaFacade.verificarExamesSismamaPorNumeroAp(numeroAp, lu2Seq)) {
			if ((sismamaFacade.obterRespostaSismamaHistoPorNumeroApECodigoCampo(numeroAp, DominioSismamaHistoCadCodigo.C_RES_PROCIR.name(),
					lu2Seq)).isEmpty()) {
				throw new ApplicationBusinessException(LaudoUnicoONExceptionCode.AEL_03248);
			}
		}

		if (DominioSituacaoExamePatologia.DC.equals(etapasLaudo) && diagnostico == null) {
			throw new ApplicationBusinessException(LaudoUnicoONExceptionCode.AEL_02712);
		}

		if (DominioSituacaoExamePatologia.DC.equals(etapasLaudo)) {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

			// CÓDIGO EQUIVALENTE A AELP_VERIFICA_PATOLOGISTAS
			if (!getAelPatologistaApDAO().verificaPatologitaPeloExameSeq(luxSeq)) {
				throw new ApplicationBusinessException(LaudoUnicoONExceptionCode.AEL_02734);
			}

			aelpAtualizaPatologistas(luxSeq);
			AelExameAp exame = getAelExameApDAO().obterPorChavePrimaria(luxSeq);
			exame.setEtapasLaudo(DominioSituacaoExamePatologia.LA);
			exame.setIndRevisaoLaudo(false);
			exame.setServidorRespLaudo(servidorLogado);
			try {
				getAelExameApRN().atualizarAelExameApRN(exame);
			} catch (BaseException e) {
				throw new ApplicationBusinessException(LaudoUnicoONExceptionCode.AEL_02597, e.getMessage());
			}

			try {
				this.aelpAtualizaAel(lu2Seq, luxSeq, nomeMicrocomputador);
			} catch (BaseException e) {
				throw new ApplicationBusinessException(LaudoUnicoONExceptionCode.AEL_03398, e.getMessage());
			}

			try {
				// Realizar o cancelamento dos exames dependentes ao assinar o
				// laudo único
				this.aelpCancExaLaudoUnico(listaMateriais, nomeMicrocomputador);
			} catch (BaseException e) {
				throw new ApplicationBusinessException(LaudoUnicoONExceptionCode.AEL_03405, e.getMessage());
			}

			// LAUDO ASSINADO
			return true;
		} else if (DominioSituacaoExamePatologia.LA.equals(etapasLaudo)) {
			Short tempoAposLib = null;
			DominioUnidadeTempoLiberacao unidTempoLib = null;
			AelConfigExLaudoUnico configExLaudoUnico = getAelConfigExLaudoUnicoDAO().obterPorChavePrimaria(lu2Seq);
			tempoAposLib = configExLaudoUnico.getTempoAposLib();
			unidTempoLib = configExLaudoUnico.getUnidTempoLib();

			if (tempoAposLib == null || unidTempoLib == null) {
				tempoAposLib = 0;
				unidTempoLib = DominioUnidadeTempoLiberacao.H;
			}

			if (DominioUnidadeTempoLiberacao.H.equals(unidTempoLib)) {
				Date d = getAelExtratoExameApDAO().obterMaxCriadoEmPorLuxSeqEEtapasLaudo(luxSeq, etapasLaudo);

				DateTime dtInicial = new DateTime(d);
				DateTime dtFinal = new DateTime(new Date());
				Minutes minutes = Minutes.minutesBetween(dtInicial, dtFinal);

				if (minutes.getMinutes() > (tempoAposLib * 60)) {
					throw new ApplicationBusinessException(LaudoUnicoONExceptionCode.AEL_02697);
				}
			} else {
				Date d = getAelExtratoExameApDAO().obterMaxCriadoEmPorLuxSeqEEtapasLaudo(luxSeq, etapasLaudo);
				if ((DateUtil.obterQtdDiasEntreDuasDatasTruncadas(DateUtil.truncaData(d), new Date())) > tempoAposLib) {
					throw new ApplicationBusinessException(LaudoUnicoONExceptionCode.AEL_02697);
				}
			}

			AelExameAp exame = getAelExameApDAO().obterPorChavePrimaria(luxSeq);
			exame.setEtapasLaudo(DominioSituacaoExamePatologia.DC);
			exame.setServidorRespLaudo(null);
			try {
				getAelExameApRN().atualizarAelExameApRN(exame);
			} catch (BaseException e) {
				throw new ApplicationBusinessException(LaudoUnicoONExceptionCode.AEL_02600, e.getMessage());
			}

			try {
				this.aelpEstornaExaLaudo(listaMateriais, nomeMicrocomputador);
			} catch (BaseException e) {
				throw new ApplicationBusinessException(LaudoUnicoONExceptionCode.AEL_03405, e.getMessage());
			}
			// LAUDO REABERTO PARA ASSINATURA
			return false;
		}
		return null;
	}

	/**
	 * Referente a função que está no forms: AELP_ESTORNA_EXA_LAUDO
	 */
	public void aelpEstornaExaLaudo(List<AelKitMatPatologiaVO> listaMateriais, String nomeMicrocomputador) throws BaseException {
		if (listaMateriais != null) {
			for (AelKitMatPatologiaVO material : listaMateriais) {
				// Somente realiza o cancelamento para os materiais que
				// estiverem marcados
				// e que estejam habilitados para o cancelamento.
				if (Boolean.TRUE.equals(material.getImunoHist()) && aelcSituacaoExame(material.getMaterialAp().getSeq())) {
					List<ConsultaItensPatologiaVO> exames = getAelExamesDependentesDAO().listaExamesDependentesSoeSeqESeqp(
							material.getMaterialAp().getSeq());
					if (exames == null || exames.isEmpty()) {
						throw new ApplicationBusinessException(LaudoUnicoONExceptionCode.AEL_03403);
					}

					AghParametros pSitCancelado = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_SITUACAO_CANCELADO);
					AghParametros pSitAreaExec = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_SITUACAO_NA_AREA_EXECUTORA);

					// Apresentar mensagem caso o exame não esteja na situação
					// 'Área Executora'
					if (exames.get(0).getIseSoeSeq() != null && pSitCancelado.getVlrTexto().equals(exames.get(0).getSitCodigo())) {
						AelItemSolicitacaoExames solicitacaoExame = getAelItemSolicitacaoExameDAO().obterPorChavePrimaria(
								new AelItemSolicitacaoExamesId(exames.get(0).getIseSoeSeq(), exames.get(0).getIseSeqp()));
						solicitacaoExame.setAelMotivoCancelaExames(null);
						solicitacaoExame.setServidorResponsabilidade(null);
						solicitacaoExame.setSituacaoItemSolicitacao(getAelSitItemSolicitacoesDAO().obterPorChavePrimaria(
								pSitAreaExec.getVlrTexto()));
						try {
							getSolicitacaoExameFacade().atualizar(solicitacaoExame, nomeMicrocomputador);
						} catch (BaseException e) {
							throw new ApplicationBusinessException(LaudoUnicoONExceptionCode.AEL_ERRO_ESTORNAR_EXAME, e.getMessage());
						}
					}
				}
			}
		}
	}

	/**
	 * Referente a função que está no forms: AELP_PROCESSA_EXAMES
	 * 
	 * @param listaMateriais
	 * @throws BaseException
	 */
	public void ativarInativarImunoHistoquimica(List<AelKitMatPatologiaVO> listaMateriais, String nomeMicrocomputador) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		AelPatologista patologista = getAelPatologistaDAO().obterAelPatologistaAtivoPorServidorEFuncao(servidorLogado,
				DominioFuncaoPatologista.C, DominioFuncaoPatologista.P);
		if (patologista == null) {
			throw new ApplicationBusinessException(LaudoUnicoONExceptionCode.MSG_SERVIDOR_PATOLOGISTA_CONTRATADO_PROFESSOR);
		}

		AelKitMatPatologiaVO itemVO = listaMateriais.get(0);
		if (itemVO.getMaterialAp().getSeq() != null) {
			Boolean exameCancelado = aelcSituacaoExame(itemVO.getMaterialAp().getSeq());

			if (Boolean.TRUE.equals(itemVO.getImunoHist() && Boolean.FALSE.equals(exameCancelado))) {
				// --realizar o cancelamento do exame
				aelpEfetCancelExame(itemVO.getMaterialAp().getSeq(), nomeMicrocomputador);
			} else if (Boolean.FALSE.equals(itemVO.getImunoHist() && Boolean.TRUE.equals(exameCancelado))) {
				// --estornar o cancelamento do exame
				aelpEstCancelExame(itemVO.getMaterialAp().getSeq(), nomeMicrocomputador);
			}

		}
	}

	/**
	 * Referente a função que está no forms: AELP_EST_CANCEL_EXAME
	 * 
	 * @param lurSeq
	 */
	protected void aelpEstCancelExame(Long lurSeq, String nomeMicrocomputador) throws BaseException {

		// Buscar o exame dependente que se deseja estornar
		List<ConsultaItensPatologiaVO> examesVO = getAelExamesDependentesDAO().listaExamesDependentesSoeSeqESeqp(lurSeq);
		if (!examesVO.isEmpty() && examesVO.get(0).getIseSoeSeq() != null) {

			AghParametros pSitCancelado = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_SITUACAO_CANCELADO);
			AghParametros pSitAreaExec = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_SITUACAO_NA_AREA_EXECUTORA);

			// Apresentar mensagem caso o exame nao esteja na situacao de
			// cancelado
			if (!pSitCancelado.getVlrTexto().equals(examesVO.get(0).getSitCodigo())) {
				AelExames exame = getAelExamesDAO().obterPorChavePrimaria(examesVO.get(0).getUfeEmaExaSigla());

				throw new ApplicationBusinessException(LaudoUnicoONExceptionCode.AEL_03400, exame.getDescricao());
			} else {
				// -- Verificar o tempo em horas em que o exame foi cancelado
				Date dataHoraEvento = getAelExtratoItemSolicitacaoDAO().buscaDataEvento(examesVO.get(0).getIseSoeSeq(),
						examesVO.get(0).getIseSeqp(), examesVO.get(0).getSitCodigo());
				AghParametros pTempoEstorno = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_TEMPO_ESTORNO);
				Integer tempoAposLib = pTempoEstorno.getVlrNumerico().intValue();

				if (dataHoraEvento == null || (DateUtil.obterQtdHorasEntreDuasDatas(dataHoraEvento, new Date())) > tempoAposLib) {
					throw new ApplicationBusinessException(LaudoUnicoONExceptionCode.AEL_03404, tempoAposLib);
				}

				AelItemSolicitacaoExames solicitacaoExame = getAelItemSolicitacaoExameDAO().obterPorChavePrimaria(
						new AelItemSolicitacaoExamesId(examesVO.get(0).getIseSoeSeq(), examesVO.get(0).getIseSeqp()));
				solicitacaoExame.setAelMotivoCancelaExames(null);
				solicitacaoExame.setServidorResponsabilidade(null);
				solicitacaoExame.setSituacaoItemSolicitacao(getAelSitItemSolicitacoesDAO()
						.obterPorChavePrimaria(pSitAreaExec.getVlrTexto()));
				try {
					getSolicitacaoExameFacade().atualizar(solicitacaoExame, nomeMicrocomputador);
				} catch (BaseException e) {
					throw new ApplicationBusinessException(LaudoUnicoONExceptionCode.AEL_ERRO_ESTORNAR_EXAME, e.getMessage());
				}
			}
		}

	}

	/**
	 * Referente a função que está no forms: AELP_EFET_CANCEL_EXAME
	 * 
	 * @param lurSeq
	 * @throws BaseException
	 */
	protected void aelpEfetCancelExame(Long lurSeq, String nomeMicrocomputador) throws BaseException {

		// Buscar o exame dependente que se deseja estornar
		List<ConsultaItensPatologiaVO> examesVO = getAelExamesDependentesDAO().listaExamesDependentesSoeSeqESeqp(lurSeq);
		if (!examesVO.isEmpty() && examesVO.get(0).getIseSoeSeq() != null) {

			AghParametros pMocCancDept = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_MOC_CANCELA_DEPT);
			AghParametros pSitCancelado = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_SITUACAO_CANCELADO);
			AghParametros pSitAreaExec = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_SITUACAO_NA_AREA_EXECUTORA);

			// Apresentar mensagem caso o exame não esteja na situação 'Área
			// Executora'
			if (!pSitAreaExec.getVlrTexto().equals(examesVO.get(0).getSitCodigo())) {
				AelExames exame = getAelExamesDAO().obterPorChavePrimaria(examesVO.get(0).getUfeEmaExaSigla());

				throw new ApplicationBusinessException(LaudoUnicoONExceptionCode.AEL_03402, exame.getDescricao());
			} else {
				RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

				AelItemSolicitacaoExames solicitacaoExame = getAelItemSolicitacaoExameDAO().obterPorChavePrimaria(
						new AelItemSolicitacaoExamesId(examesVO.get(0).getIseSoeSeq(), examesVO.get(0).getIseSeqp()));
				solicitacaoExame.setAelMotivoCancelaExames(getAelMotivoCancelaExamesDAO().obterPorChavePrimaria(
						pMocCancDept.getVlrNumerico().shortValue()));
				solicitacaoExame.setServidorResponsabilidade(servidorLogado);
				solicitacaoExame.setSituacaoItemSolicitacao(getAelSitItemSolicitacoesDAO().obterPorChavePrimaria(
						pSitCancelado.getVlrTexto()));
				try {
					getSolicitacaoExameFacade().atualizar(solicitacaoExame, nomeMicrocomputador);
				} catch (BaseException e) {
					throw new ApplicationBusinessException(LaudoUnicoONExceptionCode.AEL_ERRO_CANCEL_EXAME, e.getMessage());
				}
			}
		}

	}

	/**
	 * Referente a função que está no forms: AELP_CANC_EXA_LAUDO_UNICO
	 */
	public void aelpCancExaLaudoUnico(List<AelKitMatPatologiaVO> listaMateriais, String nomeMicrocomputador) throws BaseException {
		if (listaMateriais != null) {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

			for (AelKitMatPatologiaVO material : listaMateriais) {
				// Somente realiza o cancelamento para os materiais que
				// estiverem marcados
				// e que estejam habilitados para o cancelamento.
				if (Boolean.TRUE.equals(material.getImunoHist()) && aelcCancelaExame(material.getMaterialAp().getSeq())) {
					// Buscar o exame dependente que se deseja estornar
					List<ConsultaItensPatologiaVO> exames = getAelExamesDependentesDAO().listaExamesDependentesSoeSeqESeqp(
							material.getMaterialAp().getSeq());
					if (exames == null || exames.isEmpty()) {
						throw new ApplicationBusinessException(LaudoUnicoONExceptionCode.AEL_03403);
					}

					AghParametros pMocCancDept = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_MOC_CANCELA_DEPT);
					AghParametros pSitCancelado = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_SITUACAO_CANCELADO);
					AghParametros pSitAreaExec = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_SITUACAO_NA_AREA_EXECUTORA);

					// Apresentar mensagem caso o exame não esteja na situação
					// 'Área Executora'
					if (exames.get(0).getIseSoeSeq() != null && pSitAreaExec.getVlrTexto().equals(exames.get(0).getSitCodigo())) {

						AelItemSolicitacaoExames solicitacaoExame = getAelItemSolicitacaoExameDAO().obterPorChavePrimaria(
								new AelItemSolicitacaoExamesId(exames.get(0).getIseSoeSeq(), exames.get(0).getIseSeqp()));
						solicitacaoExame.setAelMotivoCancelaExames(getAelMotivoCancelaExamesDAO().obterPorChavePrimaria(
								pMocCancDept.getVlrNumerico().shortValue()));
						solicitacaoExame.setServidorResponsabilidade(servidorLogado);
						solicitacaoExame.setSituacaoItemSolicitacao(getAelSitItemSolicitacoesDAO().obterPorChavePrimaria(
								pSitCancelado.getVlrTexto()));
						try {
							getSolicitacaoExameFacade().atualizar(solicitacaoExame, nomeMicrocomputador);
						} catch (BaseException e) {
							throw new ApplicationBusinessException(LaudoUnicoONExceptionCode.AEL_ERRO_CANCEL_EXAME, e.getMessage());
						}
					}
				}
			}
		}
	}

	/**
	 * Referente a função que está no forms: AELP_ATUALIZA_PATOLOGISTAS
	 */
	public void aelpAtualizaPatologistas(Long luxSeq) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		final AelPatologistaAps patologista = getAelPatologistaApDAO().obterPatologistaPeloExameSeqEServidor(luxSeq, servidorLogado);
		if (patologista != null) {
			patologista.setOrdemMedicoLaudo(Short.valueOf("1"));
			try {
				getAelPatologistaApRN().atulizarAelPatologistaAps(patologista);
				// patologistas.add(patologista);
			} catch (BaseException e) {
				throw new ApplicationBusinessException(LaudoUnicoONExceptionCode.AEL_02852, e.getLocalizedMessage());
			}
			final List<AelPatologistaAps> patologistas = getAelPatologistaApDAO()
					.obterPatologistasPeloExameSeqOrdenadoPelaOrdemMedicoLaudo(luxSeq);
			Integer ordem = 1;
			for (AelPatologistaAps pat : patologistas) {
				if (!patologista.getSeq().equals(pat.getSeq())) {
					ordem++;
					pat.setOrdemMedicoLaudo(ordem.shortValue());
					try {
						getAelPatologistaApRN().atulizarAelPatologistaAps(pat);
						getAelPatologistaApRN().persistirAelPatologistaAps(pat);
					} catch (BaseException e) {
						throw new ApplicationBusinessException(LaudoUnicoONExceptionCode.AEL_02854, e.getLocalizedMessage());
					}
				}
			}
		} else {
			AelPatologistaAps novoPatologista = new AelPatologistaAps();
			novoPatologista.setAelExameAps(getAelExameApDAO().obterPorChavePrimaria(luxSeq));
			novoPatologista.setOrdemMedicoLaudo(Short.valueOf("1"));
			novoPatologista.setServidor(servidorLogado);
			try {
				getAelPatologistaApRN().inserirAelPatologistaAps(novoPatologista);
				// patologistas.add(novoPatologista);
			} catch (BaseException e) {
				throw new ApplicationBusinessException(LaudoUnicoONExceptionCode.AEL_02853, e.getLocalizedMessage());
			}
			this.flush(); // precisa do flush pq busca da base de dados
			// Essa alteração foi feita pq foi implementado na tela com setas
			// para definir a ordem dos patologistas,
			// que obrigatoriamente deve ser a partir de 1 e não de 0.
			final List<AelPatologistaAps> patologistas = getAelPatologistaApDAO()
					.obterPatologistasPeloExameSeqOrdenadoPelaOrdemMedicoLaudo(luxSeq);
			Integer ordem = 1;
			for (AelPatologistaAps pat : patologistas) {
				ordem++;
				pat.setOrdemMedicoLaudo(ordem.shortValue());
				try {
					getAelPatologistaApRN().atulizarAelPatologistaAps(pat);
				} catch (BaseException e) {
					throw new ApplicationBusinessException(LaudoUnicoONExceptionCode.AEL_02854, e.getLocalizedMessage());
				}
			}
		}
	}

	/**
	 * Referente a função que está no forms: AELP_ATUALIZA_AEL
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public void aelpAtualizaAel(Integer lu2Seq, Long luxSeq, String nomeMicrocomputador) throws BaseException {
		StringBuffer vLaudo = new StringBuffer(132);
		
		// DADOS EXAME		
		AelExameAp exameAp = getAelExameApDAO().obterPorChavePrimaria(luxSeq);
		vLaudo.append(exameAp.getAelAnatomoPatologicos().getConfigExame().getSigla()).append(": ")
				.append(NumberUtil.formatarNumeroAP(exameAp.getAelAnatomoPatologicos().getNumeroAp().toString()))
				.append(QUEBRA_LINHA).append(QUEBRA_LINHA);

		if (exameAp.getAelAnatomoPatologicoOrigem() != null) {
			vLaudo.append("NÚMERO EXAME ORIGEM (").append(exameAp.getAelAnatomoPatologicoOrigem().getConfigExame().getSigla()).append("): ")
				.append(NumberUtil.formatarNumeroAP(exameAp.getAelAnatomoPatologicoOrigem().getNumeroAp().toString())).append(QUEBRA_LINHA).append(QUEBRA_LINHA);
		}
		
		// INFORMAÇÕES CLINICAS
		AelInformacaoClinicaAP info = getAelInformacaoClinicaApDAO().obterAelInformacaoClinicaApPorAelExameAps(luxSeq);
		Boolean exibeInfClinicas = getAelSecaoConfExamesDAO().buscarPorDescVersaoConfLu2Seq(DominioSecaoConfiguravel.LIC, exameAp.getAelAnatomoPatologicos().getLu2VersaoConf(), lu2Seq);

		if(info != null && !StringUtils.isBlank(info.getInformacaoClinica()) && exibeInfClinicas) {

			vLaudo.append("INFORMAÇÕES CLÍNICAS: ");
			vLaudo.append(QUEBRA_LINHA).append(info.getInformacaoClinica()).append(QUEBRA_LINHA).append(QUEBRA_LINHA);
		}

		vLaudo.append("MATERIAL: ").append(QUEBRA_LINHA);

		
		// MATERIAIS
		List<AelMaterialAp> materiais = getAelMaterialApDAO().obterAelMaterialApPorAelExameAps(luxSeq);
		for(AelMaterialAp material : materiais) {
			vLaudo.append(material.getOrdem()).append(". ").append(material.getMaterial()).append(QUEBRA_LINHA);	
		}
		
		/**
		 * #21655 
		 */
		// MACROSCOPIA
		AelMacroscopiaAps macro = getAelMacroscopiaApsDAO().obterAelMacroscopiaApsPorAelExameAps(luxSeq);
		
		Boolean exibeMacroscopia = getAelSecaoConfExamesDAO().buscarPorDescVersaoConfLu2Seq(DominioSecaoConfiguravel.LMA, exameAp.getAelAnatomoPatologicos().getLu2VersaoConf(), lu2Seq);
		if(macro != null && !StringUtils.isBlank(macro.getMacroscopia()) && exibeMacroscopia) {

			vLaudo.append(QUEBRA_LINHA).append( "MACROSCOPIA: ");

			vLaudo.append(QUEBRA_LINHA).append(macro.getMacroscopia()).append(QUEBRA_LINHA);
		}
		/**
		 * Adicionado conforme #21655
		 */
		AelDescMaterialAps descricaoMaterial = getAelDescMaterialApsDAO().obterAelDescMaterialApsPorAelExameAps(luxSeq);
		Boolean exibeDM = getAelSecaoConfExamesDAO().buscarPorDescVersaoConfLu2Seq(DominioSecaoConfiguravel.LDM, exameAp.getAelAnatomoPatologicos().getLu2VersaoConf(), lu2Seq);
		if(descricaoMaterial != null && !StringUtils.isBlank(descricaoMaterial.getDescrMaterial()) && exibeDM) {

			vLaudo.append(QUEBRA_LINHA).append( "DESCRIÇÃO DO MATERIAL: ");

			vLaudo.append(QUEBRA_LINHA).append(descricaoMaterial.getDescrMaterial()).append(QUEBRA_LINHA);
		}		
		/**
		 * Índice de Blocos  - Adicionado #21655
		 * Toni 11/08/2014 Não irá imprimir indice de blocos, pelo menos na fase 1 da patologia (impedido no cadastro)
		 */
		Boolean exibeIBL = getAelSecaoConfExamesDAO().buscarPorDescVersaoConfLu2Seq(DominioSecaoConfiguravel.IBL, exameAp.getAelAnatomoPatologicos().getLu2VersaoConf(), lu2Seq);
		List<AelLaminaAps> listLamina = this.getAelLaminaApsDAO().listarAelLaminaApsPorLuxSeq(luxSeq); 

		if(listLamina!=null && !listLamina.isEmpty() && exibeIBL){
			vLaudo.append(QUEBRA_LINHA).append( "<b>ÍNDICE DE BLOCOS  :</b> ").append(QUEBRA_LINHA)
			.append("<table> <tr> <th> DATA </th>   <th>&nbsp; MATERIAL </th>  <th>&nbsp;  Nº CÁPSULAS </th> <th>&nbsp;  Nº FRAGMENTOS </th>  <th>&nbsp; COLORAÇÃO </th> </tr>");

			for(AelLaminaAps lamina: listLamina){
				vLaudo.append("<tr> <td>").append(DateUtil.obterDataFormatada(lamina.getDthrLamina(), "dd/MM/yyyy")).append("</td>")
				.append(TD_NBSP_NBSP).append(lamina.getDescricao()!=null?lamina.getDescricao():"").append(" </td>")
				.append(TD_NBSP_NBSP).append(lamina.getNumeroCapsula()).append("</td> ")
				.append(TD_NBSP_NBSP).append(lamina.getNumeroFragmentos()).append("</td> ")
				.append(TD_NBSP_NBSP).append(lamina.getColoracao()!=null?lamina.getColoracao():"").append(" </td> </tr> ");
			
			}
			vLaudo.append("</table><br>").append(QUEBRA_LINHA);

		}

		/**
		 * Topografias- Adicionado #21655
		 */
		Boolean exibeCTO = getAelSecaoConfExamesDAO().buscarPorDescVersaoConfLu2Seq(DominioSecaoConfiguravel.CTO, exameAp.getAelAnatomoPatologicos().getLu2VersaoConf(), lu2Seq);
		List<AelTopografiaLaudos> listTopografias = this.getAelTopografiaLaudosDAO().listarTopografiaLaudosPorSeqExame(luxSeq);
		if(listTopografias!=null && !listTopografias.isEmpty() && listTopografias.get(0).getTopografiaCidOs() != null && exibeCTO){

			vLaudo.append(QUEBRA_LINHA).append( "TOPOGRAFIA: ").append(QUEBRA_LINHA);

			for(AelTopografiaLaudos topografia: listTopografias){
				vLaudo.append(topografia.getTopografiaCidOs().getCodigo()).append(" - ").append(topografia.getTopografiaCidOs().getDescricao()).append(QUEBRA_LINHA);
			}
		}

		/**
		 * Avaliação da Margem Cirúrgica -Adicionado #21655
		 */
		Boolean exibeLDI = getAelSecaoConfExamesDAO().buscarPorDescVersaoConfLu2Seq(DominioSecaoConfiguravel.LDI, exameAp.getAelAnatomoPatologicos().getLu2VersaoConf(), lu2Seq);
		AelDiagnosticoAps diagnosticoAps = getAelDiagnosticoApsDAO().obterAelDiagnosticoApsPorAelExameAps(luxSeq);
		if(diagnosticoAps != null && (diagnosticoAps.getNeoplasiaMaligna() != null || diagnosticoAps.getBiopsia() != null || diagnosticoAps.getMargemComprometida() != null) && exibeLDI){

			vLaudo.append(QUEBRA_LINHA).append( "AVALIAÇÃO DA MARGEM CIRÚRGICA: ").append(QUEBRA_LINHA);
			vLaudo.append("Neoplasia Maligna: ").append(diagnosticoAps.getNeoplasiaMaligna()!=null?diagnosticoAps.getNeoplasiaMaligna().getDescricao():"").append(ESPAÇO);
			vLaudo.append("Biópsia: ").append(diagnosticoAps.getBiopsia()!=null?diagnosticoAps.getBiopsia().getDescricao():"").append(ESPAÇO);
			vLaudo.append("Margem Comprometida: ").append(diagnosticoAps.getMargemComprometida()!=null?diagnosticoAps.getMargemComprometida().getDescricao():"").append(QUEBRA_LINHA);
		}

		// DIAGNÓSTICO
		Boolean exibeLDE = getAelSecaoConfExamesDAO().buscarPorDescVersaoConfLu2Seq(DominioSecaoConfiguravel.LDE, exameAp.getAelAnatomoPatologicos().getLu2VersaoConf(), lu2Seq);
		List<AelDiagnosticoAps>  listDiagnosticos = getAelDiagnosticoApsDAO().listAelDiagnosticoApsPorAelExameAps(luxSeq);
		if(listDiagnosticos!=null && !listDiagnosticos.isEmpty() && listDiagnosticos.get(0).getDiagnostico() != null && exibeLDE){

			vLaudo.append(QUEBRA_LINHA).append( "DIAGNÓSTICO: ").append(QUEBRA_LINHA);

			for(AelDiagnosticoAps diagnostico: listDiagnosticos){
				vLaudo.append(diagnostico.getDiagnostico()).append(QUEBRA_LINHA);
			}
		}
		vLaudo.append(QUEBRA_LINHA);
		
		//DIAGNOSTICO CID-O e CID-10
		Boolean exibeCDI = getAelSecaoConfExamesDAO().buscarPorDescVersaoConfLu2Seq(DominioSecaoConfiguravel.CDI, exameAp.getAelAnatomoPatologicos().getLu2VersaoConf(), lu2Seq);
		List<AelDiagnosticoLaudos> diagnosticosLaudo = this.getAelDiagnosticoLaudosDAO().obterAelDiagnosticoLaudosPorLuxSeq(luxSeq);
		if(diagnosticosLaudo != null&& !diagnosticosLaudo.isEmpty() && (diagnosticosLaudo.get(0).getAghCid() != null || diagnosticosLaudo.get(0).getAelCidos() != null) && exibeCDI){

			vLaudo.append( "DIAGNÓSTICO CID-O E/OU CID-10: ").append(QUEBRA_LINHA);

			for(AelDiagnosticoLaudos diagnosticoLaudo: diagnosticosLaudo){
				if(diagnosticoLaudo.getAghCid()!=null){
					vLaudo.append(diagnosticoLaudo.getAghCid().getCodigo()).append(" - ").append(diagnosticoLaudo.getAghCid().getDescricao()).append(QUEBRA_LINHA);
				}
				if(diagnosticoLaudo.getAelCidos()!=null){
					vLaudo.append(diagnosticoLaudo.getAelCidos().getCodigo()).append(" - ").append(diagnosticoLaudo.getAelCidos().getDescricao()).append(QUEBRA_LINHA);
	
				}
			}
		}		
		vLaudo.append(QUEBRA_LINHA).append(QUEBRA_LINHA);
		
		//PATOLOGISTA
		laudoUnicoApoioON.setPatologistasLaudo(vLaudo, QUEBRA_LINHA, luxSeq);

		AghParametros pCalc = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_CAL_SEQ_LAUDO_UNICO);
		AghParametros pSitLiberada = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_SITUACAO_LIBERADO);
		AghParametros pSitAreaExec = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_SITUACAO_NA_AREA_EXECUTORA);
		AghParametros pSitExec = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_SITUACAO_EXECUTANDO);

		// ITENS
		List<ConsultaItensPatologiaVO> itensExame = getAelExameApItemSolicDAO().listaExamesComVersaoLaudo(luxSeq, pCalc.getVlrNumerico().intValue(), new String[]{pSitLiberada.getVlrTexto(), pSitAreaExec.getVlrTexto(), pSitExec.getVlrTexto()});
		Long quantidade = 0l;
		for(ConsultaItensPatologiaVO item : itensExame) {
			
			quantidade = getAelResultadoExameDAO().quantidadeResultadosExame(item.getIseSoeSeq(), item.getIseSeqp(), 
								item.getUfeEmaExaSigla(), item.getUfeEmaManSeq(), item.getVelSeqp(), item.getCalSeq(), item.getSeqp());
			
			if(quantidade > 0) {
				List<AelResultadoExame> exames = getAelResultadoExameDAO().listarResultadosExameAnulados(item.getIseSoeSeq(), item.getIseSeqp(), item.getUfeEmaExaSigla(), item.getUfeEmaManSeq());
				for(AelResultadoExame exame : exames) {
					exame.setAnulacaoLaudo(true);
					try {
						getExamesFacade().atualizarAelResultadoExame(exame, nomeMicrocomputador);
					} catch (ApplicationBusinessException e){
						throw new ApplicationBusinessException(LaudoUnicoONExceptionCode.AEL_01987, e.getLocalizedMessage(), Severity.INFO);
					}catch(BaseException e) {
						throw new ApplicationBusinessException(LaudoUnicoONExceptionCode.AEL_02699, e.getLocalizedMessage());
					}
				}
			}
			quantidade ++;
			AelResultadoExame resultado = new AelResultadoExame();
			resultado.setId(new AelResultadoExameId(item.getIseSoeSeq(), item.getIseSeqp(), item.getUfeEmaExaSigla(), item
					.getUfeEmaManSeq(), item.getVelSeqp(), item.getCalSeq(), item.getSeqp(), quantidade.intValue()));
			resultado.setAnulacaoLaudo(false);
			//*/ SOLUÇÃO PALIATIVA EM FUNÇÃO DE ERRO DE IMPLEMENTAÇÃO NA PERSISTÊNCIA DE AEL_RESULTADOS_EXAMES //*/
			AelParametroCamposLaudo parCampoLaudo = getAelParametroCamposLaudoDAO().obterPorChavePrimaria(new AelParametroCampoLaudoId(item.getUfeEmaExaSigla(), item.getUfeEmaManSeq(), item.getVelSeqp(), item.getCalSeq(), item.getSeqp()));
			resultado.setParametroCampoLaudo(parCampoLaudo);
			
			resultado.setDescricao(vLaudo.toString());
			//*///*/
			try {
				getExamesFacade().inserirAelResultadoExame(resultado);
			} catch(BaseException e) {
				throw new ApplicationBusinessException(LaudoUnicoONExceptionCode.AEL_02643, e.getLocalizedMessage());
			}

			AelItemSolicitacaoExames solicitacao = getAelItemSolicitacaoExameDAO().obterPorChavePrimaria(new AelItemSolicitacaoExamesId(item.getIseSoeSeq(), item.getIseSeqp()));
			solicitacao.setSituacaoItemSolicitacao(getAelSitItemSolicitacoesDAO().obterPorChavePrimaria("EX"));
			try {
				getSolicitacaoExameFacade().atualizar(solicitacao, null, nomeMicrocomputador);
			} catch(BaseException e) {
				throw new ApplicationBusinessException(LaudoUnicoONExceptionCode.AEL_02641, e.getLocalizedMessage());
			}
		}
	}	

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IExamesFacade getExamesFacade() {
		return examesFacade;
	}

	protected IExamesPatologiaFacade getExamesPatologiaFacade() {
		return examesPatologiaFacade;
	}

	protected ISolicitacaoExameFacade getSolicitacaoExameFacade() {
		return solicitacaoExameFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected ISismamaFacade getSismamaFacade() {
		return this.sismamaFacade;
	}

	protected AelPatologistaApRN getAelPatologistaApRN() {
		return aelPatologistaApRN;
	}

	protected AelInformacaoClinicaAPRN getAelInformacaoClinicaAPRN() {
		return aelInformacaoClinicaAPRN;
	}

	protected AelMaterialApRN getAelMaterialApRN() {
		return aelMaterialApRN;
	}

	protected AelExameApRN getAelExameApRN() {
		return aelExameApRN;
	}

	protected AelApXPatologistaDAO getAelApXPatologistaDAO() {
		return aelApXPatologistaDAO;
	}

	protected AelExamesDependentesDAO getAelExamesDependentesDAO() {
		return aelExamesDependentesDAO;
	}

	protected AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO() {
		return aelItemSolicitacaoExameDAO;
	}

	protected AelMaterialApDAO getAelMaterialApDAO() {
		return aelMaterialApDAO;
	}

	protected AelExameApDAO getAelExameApDAO() {
		return aelExameApDAO;
	}

	protected AelExamesDAO getAelExamesDAO() {
		return aelExamesDAO;
	}

	protected AelMacroscopiaApsDAO getAelMacroscopiaApsDAO() {
		return aelMacroscopiaApsDAO;
	}

	protected AelMicroscopiaApsDAO getAelMicroscopiaApsDAO() {
		return aelMicroscopiaApsDAO;
	}

	protected AelDiagnosticoApsDAO getAelDiagnosticoApsDAO() {
		return aelDiagnosticoApsDAO;
	}

	protected AelPatologistaApDAO getAelPatologistaApDAO() {
		return aelPatologistaApDAO;
	}

	protected AelInformacaoClinicaApDAO getAelInformacaoClinicaApDAO() {
		return aelInformacaoClinicaApDAO;
	}

	protected AelNotaAdicionalApDAO getAelNotaAdicionalApDAO() {
		return aelNotaAdicionalApDAO;
	}

	protected AelPatologistaDAO getAelPatologistaDAO() {
		return aelPatologistaDAO;
	}

	protected AelExameApItemSolicDAO getAelExameApItemSolicDAO() {
		return aelExameApItemSolicDAO;
	}

	protected AelResultadoExameDAO getAelResultadoExameDAO() {
		return aelResultadoExameDAO;
	}

	protected AelNotaAdicionalDAO getAelNotaAdicionalDAO() {
		return aelNotaAdicionalDAO;
	}

	protected AelSitItemSolicitacoesDAO getAelSitItemSolicitacoesDAO() {
		return aelSitItemSolicitacoesDAO;
	}

	protected AelParametroCamposLaudoDAO getAelParametroCamposLaudoDAO() {
		return aelParametroCamposLaudoDAO;
	}

	protected AelMotivoCancelaExamesDAO getAelMotivoCancelaExamesDAO() {
		return aelMotivoCancelaExamesDAO;
	}

	protected AelConfigExLaudoUnicoDAO getAelConfigExLaudoUnicoDAO() {
		return aelConfigExLaudoUnicoDAO;
	}

	protected AelExtratoExameApDAO getAelExtratoExameApDAO() {
		return aelExtratoExameApDAO;
	}

	protected AelExtratoItemSolicitacaoDAO getAelExtratoItemSolicitacaoDAO() {
		return aelExtratoItemSolicitacaoDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}

	protected AelSecaoConfExamesDAO getAelSecaoConfExamesDAO() {
		return aelSecaoConfExamesDAO;
	}

	protected AelDescMaterialApsDAO getAelDescMaterialApsDAO() {
		return aelDescMaterialApsDAO;
	}

	protected AelLaminaApsDAO getAelLaminaApsDAO() {
		return aelLaminaApsDAO;
	}

	protected AelTopografiaLaudosDAO getAelTopografiaLaudosDAO() {
		return aelTopografiaLaudosDAO;
	}

	protected AelDiagnosticoLaudosDAO getAelDiagnosticoLaudosDAO() {
		return aelDiagnosticoLaudosDAO;
	}

}
