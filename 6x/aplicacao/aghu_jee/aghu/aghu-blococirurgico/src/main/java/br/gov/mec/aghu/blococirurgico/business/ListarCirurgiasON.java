package br.gov.mec.aghu.blococirurgico.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.blococirurgico.business.ListaCirurgiasDescCirurgicaON.ListarCirurgiasDescCirurgicaONExceptionCode;
import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcControleEscalaCirurgicaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcDescricaoCirurgicaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcFichaAnestesiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcMotivoCancelamentoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcEspPorCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcedimentoCirurgicoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfAtuaUnidCirgsDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcQuestaoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcValorValidoCancDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtDescricaoDAO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.business.IBlocoCirurgicoPortalPlanejamentoFacade;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaVO;
import br.gov.mec.aghu.blococirurgico.vo.MotivoCancelamentoVO;
import br.gov.mec.aghu.blococirurgico.vo.SuggestionListaCirurgiaVO;
import br.gov.mec.aghu.blococirurgico.vo.TelaListaCirurgiaVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioOrigemPacienteCirurgia;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.dominio.DominioTipoProcedimentoCirurgico;
import br.gov.mec.aghu.exameselaudos.business.IExamesLaudosFacade;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghFeriados;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.CseCategoriaProfissional;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcControleEscalaCirurgica;
import br.gov.mec.aghu.model.MbcMotivoCancelamento;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgias;
import br.gov.mec.aghu.model.MbcQuestao;
import br.gov.mec.aghu.model.MbcValorValidoCanc;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@SuppressWarnings({ "PMD.ExcessiveClassLength" })
@Stateless
public class ListarCirurgiasON extends BaseBusiness {
private static final String RENDER = "render";

@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(ListarCirurgiasON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcValorValidoCancDAO mbcValorValidoCancDAO;

	@Inject
	private PdtDescricaoDAO pdtDescricaoDAO;

	@Inject
	private MbcProcEspPorCirurgiasDAO mbcProcEspPorCirurgiasDAO;

	@Inject
	private MbcFichaAnestesiasDAO mbcFichaAnestesiasDAO;

	@Inject
	private MbcProcedimentoCirurgicoDAO mbcProcedimentoCirurgicoDAO;

	@Inject
	private MbcCirurgiasDAO mbcCirurgiasDAO;

	@Inject
	private MbcMotivoCancelamentoDAO mbcMotivoCancelamentoDAO;

	@Inject
	private MbcQuestaoDAO mbcQuestaoDAO;

	@Inject
	private MbcDescricaoCirurgicaDAO mbcDescricaoCirurgicaDAO;

	@Inject
	private MbcControleEscalaCirurgicaDAO mbcControleEscalaCirurgicaDAO;

	@Inject
	private MbcProfAtuaUnidCirgsDAO mbcProfAtuaUnidCirgsDAO;


	@EJB
	private ICascaFacade iCascaFacade;

	@EJB
	private IFaturamentoFacade iFaturamentoFacade;

	@EJB
	private IAmbulatorioFacade iAmbulatorioFacade;
	
	@EJB
	private IPrescricaoMedicaFacade iPrescricaoMedicaFacade;

	@EJB
	private ListaCirurgiasDescCirurgicaON listaCirurgiasDescCirurgicaON;

	@EJB
	private IBlocoCirurgicoPortalPlanejamentoFacade iBlocoCirurgicoPortalPlanejamentoFacade;

	@EJB
	private EscalaCirurgiasON escalaCirurgiasON;

	@EJB
	private IPacienteFacade iPacienteFacade;

	@EJB
	private IRegistroColaboradorFacade iRegistroColaboradorFacade;

	@EJB
	private IExamesLaudosFacade iExamesLaudosFacade;

	@EJB
	private IParametroFacade iParametroFacade;

	@EJB
	private IAghuFacade iAghuFacade;

	@EJB
	private IBlocoCirurgicoFacade iBlocoCirurgicoFacade;

	@EJB
	private ICertificacaoDigitalFacade iCertificacaoDigitalFacade;

	@EJB
	private IPesquisaInternacaoFacade iPesquisaInternacaoFacade;
	
	@EJB
	private IPermissionService permissionService;

	private static final long serialVersionUID = -8874666284243240461L;
	private static final String PRESCRICAO_MEDICA = "/prescricaomedica/verificaprescricao/verificaPrescricaoMedica.xhtml";
    private static final String PRESCRICAO_ENFERMAGEM = "/prescricaoenfermagem/elaboracaoPrescricaoEnfermagem.xhtml";
	private static final String AVISO_CERIH_LISTA_CIRURGIAS = "avisoCerihListaCirurgias";
	private static final String PERMISSAO_CANCELAR_CIRURGIA_FORA_DO_PERIODO = "cancelarCirurgiaForaDoPeriodo";	
	private static final String PERMISSAO_DESMARCAR_PROCEDIMENTO = "desmarcarProcedimento";
	private static final String PERMISSAO_AGENDAR_CIRURGIA_NAO_PREVISTA = "agendarCirurgiaNaoPrevista";
	private static final String PERMISSAO_ALTERAR_CIRURGIA_APOS_ESCALA = "alterarCirurgiaAposEscala";
	private static final String PERMISSAO_ACAO_EXECUTAR = "executar";
	private static final String PERMISSAO_ACAO_AGENDAR = "agendar";
	private static final String PERMISSAO_ACAO_ALTERAR = "alterar";
	// Comentado, fora do escopo de desenvolvimento atual
	// private static final String ELABORAR_FICHA_ANESTESICA = "ELABORAR FICHA ANESTESICA";
	
	public enum ListarCirurgiasONExceptionCode implements BusinessExceptionCode {
		MBC_01346, MBC_01345, MBC_00392, MBC_00460, MBC_01107, MBC_01109, MBC_01108, ERRO_OBTER_NOME_NODO_POL_EXAME,
		ERRO_LISTA_CIRURGIA_RESPOSTA_CANCELAMENTO_NAO_INFORMADA, ERRO_LISTA_CIRURGIA_COMPLEMENTO_CANCELAMENTO_NAO_INFORMADO,
		MENSAGEM_JA_EXISTE_DESCRICAO_CIRURGICA, MENSAGEM_JA_EXISTE_DESCRICAO_CIRURGICA_PDT
	}
	
	/**
	 * ORADB: MBCP_POPULA_CANCELAMENTO (forms)
	 * 
	 * @param usuarioLogado
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private void popularListaMotivoCancelamento(final TelaListaCirurgiaVO tela, final CirurgiaVO cirurgia) throws ApplicationBusinessException {
		List<MotivoCancelamentoVO> listaMotivoCancelamentoVO = new ArrayList<MotivoCancelamentoVO>();
		String loginUsuarioLogado = tela.getLoginUsuarioLogado();
		Date dataCirurgia = cirurgia.getCrgData();
		Integer crgSeq = cirurgia.getCrgSeq();
		ICascaFacade cascaFacade = getCascaFacade();
		
		Boolean possuiPermissaoDesmarcarProcedimento = Boolean.FALSE;
		possuiPermissaoDesmarcarProcedimento = cascaFacade.usuarioTemPermissao(
				loginUsuarioLogado, PERMISSAO_DESMARCAR_PROCEDIMENTO,
				PERMISSAO_ACAO_EXECUTAR);
		
		Boolean temEscala = Boolean.FALSE;
		
		// Busca código de cancelamento de Overbooking
		AghParametros paramCancelaOverbooking = null;
		paramCancelaOverbooking = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_CANC_OVERBOOKING);
		
		Short seqCancelaOverbooking = null;
		if (paramCancelaOverbooking == null) {
			throw new ApplicationBusinessException(ListarCirurgiasONExceptionCode.MBC_01346);
		} else {
			seqCancelaOverbooking = paramCancelaOverbooking.getVlrNumerico().shortValue();
		}
		
		// Verifica se já rodou escala pra data, não pode desmarcar
		// c_controle
		List<MbcControleEscalaCirurgica> listaControleEscalaCirurgica = getMbcControleEscalaCirurgicaDAO()
				.pesquisarControleEscalaCirurgicaPorUnfSeqDataPesquisa(tela.getUnidade().getSeq(),
						cirurgia.getCrgData());
		MbcControleEscalaCirurgica controleEscalaCirurgica = null;
		
		if (!listaControleEscalaCirurgica.isEmpty()) {
			temEscala = Boolean.TRUE;
			controleEscalaCirurgica = listaControleEscalaCirurgica.get(0);
		}
		
		if (dataCirurgia.after(new Date()) && !temEscala) {
			// Médico não pode desmarcar pela lista.
			// Observação: no AGH somente o perfil MBCG_CHEFIA_ADM pode desmarcar. No AGHU a permissão
			// 		"desmarcarProcedimento" deverá estar associada ao perfil do usuário que deseja desmarcar.
			if (possuiPermissaoDesmarcarProcedimento) {
				List<MbcProcEspPorCirurgias> listaProcEspPorCirurgia = getMbcProcEspPorCirurgiasDAO()
						.pesquisarProcEspCirurgicoPrincipalAgendamentoPorCrgSeq(crgSeq, DominioSituacao.A, Boolean.TRUE);
				// Verifica o tipo do procedimento principal
				if (listaProcEspPorCirurgia.isEmpty()) {
					throw new ApplicationBusinessException(ListarCirurgiasONExceptionCode.MBC_01345);
				} else {
					MbcProcEspPorCirurgias procEspPorCirurgia = listaProcEspPorCirurgia.get(0);
					DominioTipoProcedimentoCirurgico tipoProcedimentoCirurgico = procEspPorCirurgia.getProcedimentoCirurgico().getTipo();
					if (this.isHCPA() && !cirurgia.getGridCspCnvCodigo().equals(tela.getConvenioSusPadrao()) || DominioTipoProcedimentoCirurgico.PROCEDIMENTO_DIAGNOSTICO_TERAPEUTICO.equals(tipoProcedimentoCirurgico) ||
								DominioTipoProcedimentoCirurgico.EXAME_COMPLEMENTAR.equals(tipoProcedimentoCirurgico)) {
							adicionarMotivoCancelamento(tela, listaMotivoCancelamentoVO);		
						} else if (!this.isHCPA()) {	
							adicionarMotivoCancelamento(tela, listaMotivoCancelamentoVO);							
					}
				}
			}
		} else {
			// Monta lista de motivos de cancelamento por perfil
			List<MotivoCancelamentoVO> listaInicialMotivoCancelamentoVO = getMbcMotivoCancelamentoDAO()
					.pesquisarMotivoCancelamentoVO(loginUsuarioLogado);
			listaMotivoCancelamentoVO = popularListaCancelamentoVO(tela,cirurgia,
					dataCirurgia, crgSeq, temEscala, seqCancelaOverbooking, controleEscalaCirurgica,
					listaInicialMotivoCancelamentoVO);
		}		
		cirurgia.setListaMotivoCancelamentoVO(listaMotivoCancelamentoVO);
		// Seta motivo selecionado caso ele exista
		Short mctSeq = cirurgia.getMtcSeq();
		if (mctSeq != null) {
			MbcMotivoCancelamento motivoCancelamento = getMbcMotivoCancelamentoDAO().obterPorChavePrimaria(mctSeq);
			MotivoCancelamentoVO motivoCancelamentoVO = new MotivoCancelamentoVO(motivoCancelamento.getDescricao(), motivoCancelamento.getSeq());
			cirurgia.setMotivoCancelamento(motivoCancelamentoVO);
			
			List<MotivoCancelamentoVO> listaMotivCancela = cirurgia.getListaMotivoCancelamentoVO();
			if (!listaMotivCancela.contains(motivoCancelamentoVO)) {
				listaMotivCancela.add(motivoCancelamentoVO);
			}
			cirurgia.setListaMotivoCancelamentoVO(listaMotivCancela);
		}
	}

	private void adicionarMotivoCancelamento(final TelaListaCirurgiaVO tela,
			List<MotivoCancelamentoVO> listaMotivoCancelamentoVO) {
		MbcMotivoCancelamento motivoCancelamento = getMbcMotivoCancelamentoDAO().obterPorChavePrimaria(tela.getDesmarcar());
		MotivoCancelamentoVO motivoCancelamentoVO = new MotivoCancelamentoVO(motivoCancelamento.getDescricao(), motivoCancelamento.getSeq());
		listaMotivoCancelamentoVO.add(motivoCancelamentoVO);
	}
	
	private List<MotivoCancelamentoVO> popularListaCancelamentoVO(
			TelaListaCirurgiaVO telaListaCirurgiaVO,
			CirurgiaVO cirurgia,
			Date dataCirurgia, Integer crgSeq, Boolean temEscala,
			Short seqCancelaOverbooking,
			MbcControleEscalaCirurgica controleEscalaCirurgica,
			List<MotivoCancelamentoVO> listaInicialMotivoCancelamentoVO) {
		
		Boolean populaLista = Boolean.FALSE;
		List<MotivoCancelamentoVO> listaMotivoCancelamentoVO = new ArrayList<MotivoCancelamentoVO>();
		
		AghFeriados feriado = getAghuFacade().obterFeriadoSemTurnoDataTruncada(dataCirurgia);
		
		for (MotivoCancelamentoVO motivoCancelamentoVO : listaInicialMotivoCancelamentoVO) {
			if (motivoCancelamentoVO.getSeq().equals(telaListaCirurgiaVO.getDesmarcarAdm())) {
				if (temEscala && controleEscalaCirurgica != null
						&& (controleEscalaCirurgica.getDthrGeracaoEscala().after(cirurgia.getCrgCriadoEm()) || 
								controleEscalaCirurgica.getDthrGeracaoEscala().equals(cirurgia.getCrgCriadoEm()))) {
					populaLista = Boolean.TRUE;
				}

				if (!populaLista) {
					if (feriado != null || DateUtil.isFinalSemana(dataCirurgia)) {
						populaLista = Boolean.TRUE;
						listaMotivoCancelamentoVO.add(motivoCancelamentoVO);
					}
				}
			} else if (motivoCancelamentoVO.getSeq().equals(seqCancelaOverbooking)) {
				// c_crg
				MbcCirurgias crg = getMbcCirurgiasDAO().obterPorChavePrimaria(crgSeq);
				// Verifica se a cirurgia foi agendada como overbooking
				if (crg != null && crg.getOverbooking()) {
					listaMotivoCancelamentoVO.add(motivoCancelamentoVO);
				}
			} else {
				listaMotivoCancelamentoVO.add(motivoCancelamentoVO);
			}
		}
		return listaMotivoCancelamentoVO;
	}
	
	public void popularCancelamentoCirurgia(final TelaListaCirurgiaVO tela, final CirurgiaVO crg) throws ApplicationBusinessException {
		popularListaMotivoCancelamento(tela, crg);
		popularListaValoresQuestaoEComplemento(crg, null);
	}
	
	/** ORADB: MBCP_CIRG_CANCELADA (forms) */
	public void popularListaValoresQuestaoEComplemento(final CirurgiaVO crg, final Short mtcSeq) {
		// C_QES
		List<MbcQuestao> listaQuestao = new ArrayList<MbcQuestao>(); 
				
		if (mtcSeq != null) { // Caso em que é informado um motivo de cancelamento pela tela. 
			listaQuestao = getMbcQuestaoDAO().pesquisarQuestaoAtivaPorMtcSeq(mtcSeq);
		} else if (crg.getQesMtcSeq() != null && crg.getQesSeqp() != null) {
			listaQuestao = getMbcQuestaoDAO().pesquisarQuestaoAtivaPorMtcSeqSeqp(crg.getQesMtcSeq(), crg.getQesSeqp());	
		}

		if (!listaQuestao.isEmpty()) {
			MbcQuestao questao = listaQuestao.get(0); // Deve conter somente 1 elemento
			crg.setQuestao(questao);  // Seta a questão relacionada para a seleção
			
			// C_VVC
			List<MbcValorValidoCanc> listaValorValidoCanc = new ArrayList<MbcValorValidoCanc>();
			listaValorValidoCanc = getMbcValorValidoCancDAO()
					.pesquisarValorValidoCancAtivoPorQesMtcSeqEQesSeqp(questao.getId().getMtcSeq(), questao.getId().getSeqp());
			
			List<MbcValorValidoCanc> listaValorValidoCancSelecao = new ArrayList<MbcValorValidoCanc>();
			if (crg.getVvcQesMtcSeq() != null && crg.getVvcQesSeqp() != null && crg.getVvcSeqp() != null) {
				listaValorValidoCancSelecao = getMbcValorValidoCancDAO()
						.pesquisarValorValidoCancAtivoPorQesMtcSeqQesSeqpESeqp(crg.getVvcQesMtcSeq(), crg.getVvcQesSeqp(), crg.getVvcSeqp());
			}
			
			if (!listaValorValidoCancSelecao.isEmpty()) {
				crg.setValorValido(listaValorValidoCancSelecao.get(0)); // Seta valor selecionado
			}
			
			popularValoresValidos(crg, listaValorValidoCanc);
		} else {
			crg.setQuestao(null);
		}
	}
	
	/** Popula os valores válidos para os valores da questão do cancelamento.
	 * ORADB: MBCP_POPULA_VALORES_VALIDOS (forms) */
	private void popularValoresValidos(final CirurgiaVO crg, List<MbcValorValidoCanc> listaValorValidoCanc) {
		crg.setListaValorValidoCanc(listaValorValidoCanc);
	}
	
	/**
	 * Verifica se existe descrição cirurgica, se tiver não deixa a cirurgia ser cancelada
	 * @param telaVO
	 * @param crgVO
	 * @throws ApplicationBusinessException
	 */
	public void verificarCirurgiaPossuiDescricaoCirurgica(final TelaListaCirurgiaVO telaVO, final CirurgiaVO crgVO)
			throws ApplicationBusinessException {
		String loginUsuarioLogado = telaVO.getLoginUsuarioLogado();
		RapServidores servidorLogado = getRegistroColaboradorFacade().obterServidorPorUsuario(loginUsuarioLogado);
		
		Integer crgSeq = crgVO.getCrgSeq();
		Long countDescricaoCirurgica = getMbcDescricaoCirurgicaDAO().obterCountDescricaoCirurgicaPorCrgSeq(crgSeq);
		Long countPdt = Long.valueOf(0); 

		if (countDescricaoCirurgica == 0) {
			countPdt = getPdtDescricaoDAO().obterQuantidadePdtDescricaoPorCirurgiaSimples(crgSeq); 
		}
		
		if (countDescricaoCirurgica > 0) {
			throw new ApplicationBusinessException(ListarCirurgiasONExceptionCode.MENSAGEM_JA_EXISTE_DESCRICAO_CIRURGICA);
		} else if (countPdt > 0)  { // é PDT
			// Procurar por descricao pendente
			Integer seq = getPdtDescricaoDAO().obterPdtSeqPorCirurgiaEServidorPendente(crgSeq,
							servidorLogado.getId().getMatricula(),
							servidorLogado.getId().getVinCodigo());
			// Procurar por desc concluida
			if (seq == null || seq.intValue() == 0) {
				seq = getPdtDescricaoDAO().obterPdtSeqPorCirurgiaEServidor(crgSeq,
						servidorLogado.getId().getMatricula(),
						servidorLogado.getId().getVinCodigo());
			}	
			if (seq == null || seq.intValue() == 0){
				seq =  getPdtDescricaoDAO().obterPdtDescricaoPorCirurgiaEServidorPendente( crgSeq, 
																	   		 	   servidorLogado.getId().getMatricula(),
																	   		 	   servidorLogado.getId().getVinCodigo());
			}
			if (seq == null || seq.intValue() == 0){
				seq =  getPdtDescricaoDAO().obterPdtDescricaoPorCirurgiaEServidor( crgSeq, 
			   		 	   servidorLogado.getId().getMatricula(),
			   		 	   servidorLogado.getId().getVinCodigo());
			}	
			if (seq == null || seq.intValue() == 0) {											
				return;
			}
			throw new ApplicationBusinessException(ListarCirurgiasONExceptionCode.MENSAGEM_JA_EXISTE_DESCRICAO_CIRURGICA_PDT);
		}
	}
	
	/**
	 * Procedure
	 * 
	 * ORADB: P_CRGP_VER_PERF_CAN (forms)
	 * 
	 * @param telaVO
	 * @param crgVO
	 * @param oldMtcSeq
	 * @throws ApplicationBusinessException
	 */
	public void verificarPermissaoCancelamentoCirurgia(final TelaListaCirurgiaVO telaVO, final CirurgiaVO crgVO)
			throws ApplicationBusinessException {
		Boolean temPermissaoAgendarCirurgiaNaoPrevista = Boolean.FALSE;
		Boolean temPermissaoAlterarCirurgiaAposEscala = Boolean.FALSE;
		Integer diasCancelar = null;
		Integer diasCancelarAdm = null;
		Integer diasCancelarAdmMesOutro = null;
		Date dataAtualTruncada = DateUtil.truncaData(new Date());
		Date dataCirurgia = crgVO.getCrgData();
		Date dataCirurgiaTruncada = DateUtil.truncaData(dataCirurgia);
		
		String loginUsuarioLogado = telaVO.getLoginUsuarioLogado();
		
		MbcCirurgias cirurgia = getMbcCirurgiasDAO().obterPorChavePrimaria(crgVO.getCrgSeq());
		Short mtcSeq = null;
		if (cirurgia.getMotivoCancelamento() != null) {
			mtcSeq = cirurgia.getMotivoCancelamento().getSeq();
		}
		
		/*
		 * Descrição: Se situação da cirurgia for alterada para Cancelada,
		 * verificar se o perfil do usuário tem ação de
		 * "agendar cirurgia não prevista"associado, se tiver permito cancelar,
		 * se não tiver perfil, verificar se ousuário que está cancelando é o
		 * mesmo que agendou a cirurgia, se não foro mesmo usuário dar mensagem
		 * de erro(Não é permitido cancelar cirurgia agendada por outro
		 * funcionário. Entre em contato com a unid. cirúrgicapara cancelar).
		 * Verificar se o usuário não tem permissão para alterar omotivo do
		 * cancelamento Dar mensagem: Não é permitido alterar motivo de
		 * cancelamento para cirurgia cancelada por outro usuário.
		 */
		temPermissaoAgendarCirurgiaNaoPrevista = iCascaFacade.usuarioTemPermissao(
				loginUsuarioLogado, PERMISSAO_AGENDAR_CIRURGIA_NAO_PREVISTA, PERMISSAO_ACAO_AGENDAR);
		temPermissaoAlterarCirurgiaAposEscala = iCascaFacade.usuarioTemPermissao(
				loginUsuarioLogado, PERMISSAO_ALTERAR_CIRURGIA_APOS_ESCALA, PERMISSAO_ACAO_ALTERAR);
		
		RapServidores servidorLogado = getRegistroColaboradorFacade().obterServidorPorUsuario(loginUsuarioLogado);
		RapServidores servidorCirurgia = getRegistroColaboradorFacade().obterServidor(
				new RapServidoresId(crgVO.getServidorMatricula(), crgVO.getServidorVinCodigo()));
		
		DominioSituacaoCirurgia novoSituacaoCirurgia = crgVO.getSituacao();
		if (!novoSituacaoCirurgia.equals(cirurgia.getSituacao()) && DominioSituacaoCirurgia.CANC.equals(novoSituacaoCirurgia) 
				&& !temPermissaoAgendarCirurgiaNaoPrevista && !temPermissaoAlterarCirurgiaAposEscala
				&& !servidorLogado.equals(servidorCirurgia)) {
			// Não é permitido cancelar cirurgia agendada por outro funcionario. Contate a unidade.
			throw new ApplicationBusinessException(ListarCirurgiasONExceptionCode.MBC_00392);
		}
		
		Short novoMtcSeq = crgVO.getMtcSeq();
		if (novoMtcSeq != null && !novoMtcSeq.equals(mtcSeq)
				&& !temPermissaoAgendarCirurgiaNaoPrevista && !temPermissaoAlterarCirurgiaAposEscala
				&& !servidorLogado.equals(servidorCirurgia)) {
			// Não é permitido alterar motivo de cancelamento para cirurgia cancelada por outro funcionario.
			throw new ApplicationBusinessException(ListarCirurgiasONExceptionCode.MBC_00460);
		}

		AghParametros paramDiasCancelar = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_DIAS_CANC);
		AghParametros paramDiasCancelarAdm = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_DIAS_CANC_ADM);
		AghParametros paramDiasCancelarAdmMesOutro = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_DIAS_CANC_ADM_M);
		
		diasCancelar = paramDiasCancelar.getVlrNumerico().intValue();
		diasCancelarAdm = paramDiasCancelarAdm.getVlrNumerico().intValue();
		diasCancelarAdmMesOutro = paramDiasCancelarAdmMesOutro.getVlrNumerico().intValue();
		
		verificarDiasUteisCancelamento(diasCancelar, diasCancelarAdm, diasCancelarAdmMesOutro,
				dataAtualTruncada, dataCirurgia, dataCirurgiaTruncada,
				loginUsuarioLogado);
	}

	private void verificarDiasUteisCancelamento(Integer diasCancelar, Integer diasCancelarAdm,
			Integer diasCancelarAdmMesOutro, Date dataAtualTruncada,
			Date dataCirurgia, Date dataCirurgiaTruncada,
			String loginUsuarioLogado) throws ApplicationBusinessException {
		Date dataCirurgiaMaisDiasCanc = DateUtil.adicionaDias(dataCirurgiaTruncada, diasCancelar);
		
		if (dataAtualTruncada.after(dataCirurgiaMaisDiasCanc)) {
			Calendar calAtual = Calendar.getInstance();
			Calendar calCirg = (Calendar) calAtual.clone(); 
			calCirg.setTime(dataCirurgia);
			
			Integer dias = Math.abs(DateUtil.calcularDiasEntreDatas(dataCirurgiaTruncada, dataAtualTruncada));
			Integer mesCanc = calAtual.get(Calendar.MONTH) + 1;	
			Integer mesCirg = calCirg.get(Calendar.MONTH) + 1;
			Integer diaUtil = 0;
			
			for (int n = 1; n <= dias; n++) {
				// Não é fim de semana e não é feriado
				if (!DateUtil.isFinalSemana(dataCirurgia) && getAghuFacade().obterFeriado(dataCirurgia) == null) {
					diaUtil++;
				}
				dataCirurgia = DateUtil.adicionaDias(dataCirurgia, 1); 
			}

			/*
			 * Somente será permitido cancelar cirurgias ou alterar o motivo de
			 * cancelamento de cirurgias no 1º dia útil seguinte por qualquer
			 * perfil que consta na tabela de
			 * "Motivo de Cancelamento de Cirurgias por Competência". Após o 1º
			 * dia, somente poderá ser cancelada pelo perfil da
			 * "Gerencia Administrativa DO CC". Será permitido Até o 5º dia útil
			 * após a cirurgia, quando FOR dentro DO mês e Até o 2º dia útil
			 * quando FOR de um mês para outro.
			 */
			if (diaUtil <= diasCancelar) {
				return;
			} else if (diaUtil <= diasCancelarAdm && mesCanc.intValue() == mesCirg.intValue()) {
				Boolean possuiPermissaoCancelarForaDoPeriodo = getCascaFacade().usuarioTemPermissao(
						loginUsuarioLogado, PERMISSAO_CANCELAR_CIRURGIA_FORA_DO_PERIODO,
						PERMISSAO_ACAO_EXECUTAR);
				if (!possuiPermissaoCancelarForaDoPeriodo) {
					// Não é permitido cancelar cirurgia após v_dias_canc dia útil da data da cirurgia. Solicite cancelamento à Gerência Administrativa.
					throw new ApplicationBusinessException(ListarCirurgiasONExceptionCode.MBC_01107);
				}
			} else if (diaUtil <= diasCancelarAdmMesOutro && mesCanc.intValue() != mesCirg.intValue()) {
				Boolean possuiPermissaoCancelarForaDoPeriodo = getCascaFacade().usuarioTemPermissao(
						loginUsuarioLogado, PERMISSAO_CANCELAR_CIRURGIA_FORA_DO_PERIODO,
						PERMISSAO_ACAO_EXECUTAR);
				if (!possuiPermissaoCancelarForaDoPeriodo) {
					// Não é permitido cancelar cirurgia após V_DIAS_CANC_ADM_M dia útil da data da cirurgia. Solicite cancelamento à Gerência Administrativa.
					throw new ApplicationBusinessException(ListarCirurgiasONExceptionCode.MBC_01109);
				}
			} else {
				// Não é mais permitido cancelar cirurgia.
				throw new ApplicationBusinessException(ListarCirurgiasONExceptionCode.MBC_01108);
			}
		}
	}
		
	public List<SuggestionListaCirurgiaVO> pesquisarSuggestionProcedimento(final AghUnidadesFuncionais unidade, final Date dtProcedimento, 
			final String filtro, final Short eprEspSeq,  final DominioSituacao situacao, final boolean indPrincipal){

		List<SuggestionListaCirurgiaVO> result = getMbcProcedimentoCirurgicoDAO().pesquisarSuggestionProcedimento(unidade, dtProcedimento, filtro, eprEspSeq, situacao, indPrincipal);
		final List<SuggestionListaCirurgiaVO> resultFinal = new ArrayList<SuggestionListaCirurgiaVO>();
		
		for (SuggestionListaCirurgiaVO vo : result) {
			
			// AND PPC.EPR_PCI_SEQ = MBCC_PROC_PRIN_SEQ(PPC.CRG_SEQ)
			final Integer eprPciSeq = getBlocoCirurgicoPortalPlanejamentoFacade().pesquisarEprPciSeqporCirurgia(vo.getGrcSeq());
			
			if(vo.getEprPciSeq().equals(eprPciSeq)){
				resultFinal.add(vo);
			}
		}
		
		if(!resultFinal.isEmpty()){
			result = new ArrayList<SuggestionListaCirurgiaVO>();
			result.add(resultFinal.get(0));
			
			// Aplica distinct
			POSSUI_VO:
			for (SuggestionListaCirurgiaVO vo : resultFinal) {
				for (SuggestionListaCirurgiaVO voFinal : result) {
					if(vo.getNome().equalsIgnoreCase(voFinal.getNome())){
						continue POSSUI_VO;
					}
				}
				result.add(vo);
			}
			
			CoreUtil.ordenarLista(result,SuggestionListaCirurgiaVO.Fields.NOME.toString(),true);
			
			return result;
		} else {
			return resultFinal;
		}
	}
	
	public Long pesquisarSuggestionProcedimentoCount(final AghUnidadesFuncionais unidade, final Date dtProcedimento, 
			final String filtro, final Short eprEspSeq,  final DominioSituacao situacao, final boolean indPrincipal){

		List<SuggestionListaCirurgiaVO> result = getMbcProcedimentoCirurgicoDAO().pesquisarSuggestionProcedimento(unidade, dtProcedimento, filtro, eprEspSeq, situacao, indPrincipal);
		final List<SuggestionListaCirurgiaVO> resultFinal = new ArrayList<SuggestionListaCirurgiaVO>();
		
		for (SuggestionListaCirurgiaVO vo : result) {
			
			// AND PPC.EPR_PCI_SEQ = MBCC_PROC_PRIN_SEQ(PPC.CRG_SEQ)
			final Integer eprPciSeq = getBlocoCirurgicoPortalPlanejamentoFacade().pesquisarEprPciSeqporCirurgia(vo.getGrcSeq());
			
			if(vo.getEprPciSeq().equals(eprPciSeq)){
				resultFinal.add(vo);
			}
		}
		
		if(!resultFinal.isEmpty()){
			result = new ArrayList<SuggestionListaCirurgiaVO>();
			result.add(resultFinal.get(0));
			
			// Aplica distinct
			POSSUI_VO:
			for (SuggestionListaCirurgiaVO vo : resultFinal) {
				for (SuggestionListaCirurgiaVO voFinal : result) {
					if(vo.getNome().equalsIgnoreCase(voFinal.getNome())){
						continue POSSUI_VO;
					}
				}
				result.add(vo);
			}
				
			return (long) result.size();
		} else {
			return (long) resultFinal.size();
		}
	}
	
	
	/** ORADB MBCP_DEFINE_WHERE */
	@SuppressWarnings("PMD.NPathComplexity")
	public List<CirurgiaVO> pesquisarCirurgias(final TelaListaCirurgiaVO tela, String orderProperty, Integer crgSeq) throws ApplicationBusinessException, ApplicationBusinessException {
		final IParametroFacade  parametroFacade = getParametroFacade();
		
		List<CirurgiaVO> cirurgias = null;
		
		final ControlesEvolucao controlesEvolucao = inicializaControlesEvolucao(tela);
		Date vData = null;
		Integer categoria = null;
		List<Integer> listaSeqTipoItensEvolucao = null;
		
		if(tela.isCheckSalaRecuperacao()){
			vData = new Date();
		} else {
			vData = tela.getDtProcedimento();
		}
		
		if(controlesEvolucao.isvCagEnf()){
			categoria = controlesEvolucao.catefProfEnf;
		} else if(controlesEvolucao.isvCagMed()){
			categoria = controlesEvolucao.catefProfMed;

		} else if(controlesEvolucao.isvCagOutros()){
			categoria = controlesEvolucao.cateOutros;
		}
		
		if (categoria != null){
			listaSeqTipoItensEvolucao = getAmbulatorioFacade().buscaSeqTipoItemEvolucaoPorCategoria(categoria);			
		}
		String strSeqTipoEvolucao = "";
		final StringBuilder bstrTipoEvolucao = new StringBuilder(100);
		if (listaSeqTipoItensEvolucao != null){
			for(Integer seq:listaSeqTipoItensEvolucao){
				 bstrTipoEvolucao.append(seq.toString()).append(',');
			}
			//this.logInfo("FLAVIO Tipos Evolucao = " + strSeqTipoEvolucao.toString());
			strSeqTipoEvolucao = bstrTipoEvolucao.substring(0,bstrTipoEvolucao.length()-1);	
			//this.logInfo("FLAVIO Tipos Evolucao = " + strSeqTipoEvolucao.toString());
		}
		
		Integer diasNotaAdicional = parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_DIAS_NOTA_ADICIONAL);
		
		if (crgSeq != null) {
			cirurgias = getMbcCirurgiasDAO().pesquisarCirurgias(tela, null, null, crgSeq, null, vData, strSeqTipoEvolucao, diasNotaAdicional);
		} else if(tela.isCheckSalaRecuperacao()){			
			final Integer pDataSr = parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_DATA_SR);
			final Date dataInicioFiltro = DateUtil.adicionaDias(new Date(), pDataSr * -1);
			cirurgias = getMbcCirurgiasDAO().pesquisarCirurgias(tela, orderProperty, dataInicioFiltro, null, null, vData, strSeqTipoEvolucao, diasNotaAdicional);
			
		} else {
			List<Integer> crgsProced = null;
			if(tela.getProcedimento() != null){
				tela.setFiltrosPesquisa(tela.getFiltrosPesquisa() + " | Procedimento: " + tela.getProcedimento().getGrcSeq());
				List<SuggestionListaCirurgiaVO> result = getMbcProcedimentoCirurgicoDAO().pesquisarSuggestionProcedimento(tela.getUnidade(), tela.getDtProcedimento(), tela.getProcedimento().getEprPciSeq().toString(), tela.getEspecialidade() != null ? tela.getEspecialidade().getSeq() : null, DominioSituacao.A,true);
				crgsProced = new ArrayList<Integer>();
				if(result==null || result.isEmpty()) {
					crgsProced.add(tela.getProcedimento().getGrcSeq());
				}
				else {
					for (SuggestionListaCirurgiaVO vo : result) {
						crgsProced.add(vo.getGrcSeq());
					}
				}
			}
			//Date data = new Date();
			cirurgias = getMbcCirurgiasDAO().pesquisarCirurgias(tela, orderProperty, null, null, crgsProced, vData, strSeqTipoEvolucao, diasNotaAdicional);
			//this.logInfo("FLAVIO TEMPO SQL: " + ((new Date()).getTime() - data.getTime()));
		}
		//Date data = new Date();
		formataLista(tela, cirurgias);
		//this.logInfo("FLAVIO TEMPO Formata lista: " + ((new Date()).getTime() - data.getTime()));
		return cirurgias;
	}
	
	/**  ORADB: EVT_POST_QUERY */
	public void formataLista(final TelaListaCirurgiaVO tela, final List<CirurgiaVO> cirurgias) throws ApplicationBusinessException, ApplicationBusinessException{
		if(cirurgias != null && !cirurgias.isEmpty()){			
			final IParametroFacade  parametroFacade = getParametroFacade();			
			//final ICascaFacade cascaFacade = getCascaFacade();			
			final IPacienteFacade pacienteFacade = getPacienteFacade();
						
			final AghParametros paramCorExibicaoColunaGmr = parametroFacade.obterAghParametro(AghuParametrosEnum.P_AGHU_COR_NOTIF_GMR);
			final String corExibicaoColunaGmr = paramCorExibicaoColunaGmr.getVlrTexto();
					
			final Date dtTemporariaCriarPRM = DateUtil.obterData(2010, 0, 1);

			//final ControlesEvolucao controlesEvolucao = inicializaControlesEvolucao(tela, parametroFacade, cascaFacade);		
			
			final Boolean tempPermissaoAvisoCERIH = permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), AVISO_CERIH_LISTA_CIRURGIAS, "executar");
			final EscalaCirurgiasON escalaCrgOn = getEscalaCirurgiasON();
			
			final Date dataAtualTruncada = DateUtil.truncaData(new Date());			
			
			for (CirurgiaVO crg : cirurgias) {
				if (DateUtil.validaDataMenor(crg.getCrgData(), tela.getDtProcedimento())){
					crg.setColunaAzul(true);
					crg.setCorExibicao(CirurgiaVO.AZUL);
				}
				
				// Caso seja paciente contaminado com Germe Multirresistente sinaliza com uma cor específica na Lista de Cirurgias
				if (crg.getTemGermeMulti() != null && crg.getTemGermeMulti() > 0){
					crg.setColunaGmr(true);
					crg.setCorExibicaoColunaPaciente(corExibicaoColunaGmr);
				}
				
				
				
				//crg.setLeito(escalaCrgOn.pesquisaQuarto(crg.getPacCodigo()));
				preparaBtSolicitar(pacienteFacade, dataAtualTruncada, crg);
				
				// Verifica se o paciente está em projeto de pesquisa
				if (crg.getProjetoPesquisaPaciente()){
					crg.setDesenho4(CirurgiaVO.PROJ_PESQ);
					crg.setTitleDesenho4(CirurgiaVO.TITLE_PROJ_PESQ);					
				}
				
				
				//  Nara em 11/08/2009
			    //  habilitar botão prescrever p/médico e enfermeiro qdo internação, urg., nascim.
			    //  e ind_pac_atendimento = 'S'
			   // habilitaBotaoPrescrever(tela, crg, pacienteFacade, aghuFacade, identity);
			    
			    if(DominioSituacaoCirurgia.CANC.equals(crg.getSituacao())){
					crg.setDesenho1(CirurgiaVO.CANCELADA);
					crg.setTitleDesenho1(CirurgiaVO.TILE_CANCELADA);
					crg.setSortDesenho1(CirurgiaVO.SORT_POR_CANCELADA); 
			    } else {
			    	switch (crg.getSituacao()) {
						case RZDA: crg.setDesenho1(CirurgiaVO.REALIZADA); 		crg.setTitleDesenho1(CirurgiaVO.TITLE_REALIZADA);	   crg.setSortDesenho1(CirurgiaVO.SORT_POR_REALIZADA); 		break;
						case TRAN: crg.setDesenho1(CirurgiaVO.MEDICO);    		crg.setTitleDesenho1(CirurgiaVO.TITLE_MEDICO);  	   crg.setSortDesenho1(CirurgiaVO.SORT_POR_MEDICO);	        break;
						case CHAM: crg.setDesenho1(CirurgiaVO.TELEFONE); 	   	crg.setTitleDesenho1(CirurgiaVO.TITLE_TELEFONE);	   crg.setSortDesenho1(CirurgiaVO.SORT_POR_TELEFONE);	    break;
						case AGND: crg.setDesenho1(CirurgiaVO.AGENDA); 	   		crg.setTitleDesenho1(CirurgiaVO.TITLE_AGENDA);		   crg.setSortDesenho1(CirurgiaVO.SORT_POR_AGENDA); 	    break;
						case PREP: crg.setDesenho1(CirurgiaVO.PACIENTE_CAMA); 	crg.setTitleDesenho1(CirurgiaVO.TITLE_PACIENTE_CAMA);  crg.setSortDesenho1(CirurgiaVO.SORT_POR_PACIENTE_CAMA);	break;
					}
			    }
			    finalizaFormatacaoLista(tela, crg);
			    verificarExigenciaCERIH(tela, crg, dtTemporariaCriarPRM, tempPermissaoAvisoCERIH);
			    atualizaBotoes(tela, crg);
			    habilitaInabilitaBtsGrid(crg);
			    
			    crg.setInformacaoQuartoLeito(escalaCrgOn.pesquisaQuarto(crg, true));
			}
		}
	}
	
	private void finalizaFormatacaoLista( final TelaListaCirurgiaVO tela, CirurgiaVO crg) {
		
		Integer vTemDesc = crg.getTempDescrCirPendente();
		if(vTemDesc > 0){
			crg.setDesenho2(CirurgiaVO.FOLHA_VERMELHO_PREENCHIDA);
			crg.setTitleDesenho2(CirurgiaVO.TITLE_FOLHA_VERMELHO_PREENCHIDA);
		} else {
			vTemDesc = crg.getTempDescrCir();			
			if(vTemDesc > 1){
				crg.setDesenho2(CirurgiaVO.FOLHA_DUPLA);
				crg.setTitleDesenho2(CirurgiaVO.TITLE_FOLHA_DUPLA);
			} else if(vTemDesc == 1){
				crg.setDesenho2(CirurgiaVO.FOLHA_PREENCHIDA);
				crg.setTitleDesenho2(CirurgiaVO.TITLE_FOLHA_PREENCHIDA);
			} else if(vTemDesc == 0){
				// verifica se tem descrição p PDT pendente
				Integer vTemDescPdt = crg.getTempDescrPdtPendente();
				if(vTemDescPdt > 0 ){
					crg.setDesenho2(CirurgiaVO.FOLHA_VERMELHO_PREENCHIDA);
					crg.setTitleDesenho2(CirurgiaVO.TITLE_FOLHA_VERMELHO_PREENCHIDA);
				} else {
					// verifica se tem descrição p PDT
					vTemDescPdt = crg.getTempDescrPdtSimples();				
					
					if(vTemDescPdt > 0){
						if(vTemDescPdt > 1){
							crg.setDesenho2(CirurgiaVO.FOLHA_DUPLA);
							crg.setTitleDesenho2(CirurgiaVO.TITLE_FOLHA_DUPLA);
							
						} else if(vTemDescPdt == 1){
							crg.setDesenho2(CirurgiaVO.FOLHA_PREENCHIDA);
							crg.setTitleDesenho2(CirurgiaVO.TITLE_FOLHA_PREENCHIDA);
						}
						
					} else {
						crg.setDesenho2(CirurgiaVO.FOLHA_BRANCO);
						crg.setTitleDesenho2(CirurgiaVO.TITLE_FOLHA_BRANCO);
					}
				}
			}
		}
		
		// Popula ícones da ficha de anestesia no desenho5		
		if(crg.getFichaPendente() != null){			
			
			switch (crg.getFichaPendente()) {
				case P:  crg.setDesenho5(CirurgiaVO.PRANCHETA_VERMELHA); 	crg.setTitleDesenho5(CirurgiaVO.TITLE_PRANCHETA_VERMELHA); break;
				case R:  crg.setDesenho5(CirurgiaVO.PRANCHETA_BRANCA);		crg.setTitleDesenho5(CirurgiaVO.TITLE_PRANCHETA_BRANCA);   break;
				case V:  crg.setDesenho5(CirurgiaVO.PRANCHETA_AMARELA);  	crg.setTitleDesenho5(CirurgiaVO.TITLE_PRANCHETA_AMARELA);  break;
				default: crg.setDesenho5(CirurgiaVO.PRANCHETA_VERMELHA); 	crg.setTitleDesenho5(CirurgiaVO.TITLE_PRANCHETA_VERMELHA); break;
			}
		}
		
				
		// Marina 13/07/2009
		// Verifica se tem evolução
		//validaEvolucao(tela, crg, tipoItemEvolucao, controlesEvolucao, ambulatorioFacade);
		if (crg.getTemEvolucao() != null && crg.getTemEvolucao() > 0){
			crg.setDesenho3(CirurgiaVO.FOLHA_DUPLA_EVOLUCAO);
			crg.setTitleDesenho3(CirurgiaVO.TITLE_FOLHA_DUPLA_EVOLUCAO);
		}
		
		if(crg.getTemCertificacaoDigital() != null && crg.getTemCertificacaoDigital() > 0){
			crg.setDesenho6(CirurgiaVO.CHIP);
			crg.setTitleDesenho6(CirurgiaVO.TITLE_CHIP);
		}
	}

	/** ORADB EVT_WHEN_NEW_RECORD_INSTANCE */
	private void habilitaInabilitaBtsGrid(final CirurgiaVO crg) {
		
		Integer vTemDesc = crg.getTempDescrCirPendente();
		Integer vTemDescDdt = crg.getTempDescrPdtPendente();
		
		if(DominioSituacaoCirurgia.CANC.equals(crg.getSituacao())){
			crg.setOutraDescricao(false);
			crg.setDescrever(false);
			crg.setEditarHabilitado(false);
			crg.setVisualizarHabilitado(false);
		} else {
			if((vTemDesc != null && vTemDesc > 0) || (vTemDescDdt != null && vTemDescDdt > 0 )){
				crg.setOutraDescricao(true);
				crg.setDescrever(false);
				crg.setEditarHabilitado(true);
				crg.setVisualizarHabilitado(true);
			} else {
				// pode não ter feito a pesquisa, neste caso efetua a mesma.
				vTemDesc = crg.getTempDescrCir();
				if(vTemDesc > 0){
					crg.setOutraDescricao(true);
					crg.setDescrever(false);
					crg.setEditarHabilitado(true);
					crg.setVisualizarHabilitado(true);
				} else {
					vTemDescDdt = crg.getTempDescrPdtSimples();
					if(vTemDescDdt > 0){
						crg.setOutraDescricao(true);
						crg.setDescrever(false);
						crg.setEditarHabilitado(true);
						crg.setVisualizarHabilitado(true);
					} else {
						crg.setOutraDescricao(false);
						crg.setDescrever(true);
						crg.setEditarHabilitado(true);
						crg.setVisualizarHabilitado(true);
					}
				}
			}
		}
		
		// Caso não tenha descrição, não exibe o botão
		if(CirurgiaVO.FOLHA_BRANCO.equalsIgnoreCase(crg.getDesenho2())){
			crg.setVisualizarHabilitado(false);
		}
	}

	/** ORADB: P_HABILITA_BOTAO_PRESCREVER 
	 * @throws ApplicationBusinessException 
	 * @throws ApplicationBusinessException */
	public boolean habilitaBotaoPrescrever(final CirurgiaVO crg) throws ApplicationBusinessException, ApplicationBusinessException {
		Boolean retorno = false;
		if(permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), PRESCRICAO_MEDICA, RENDER)  ||
				!permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), PRESCRICAO_ENFERMAGEM, RENDER) &&
						!permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), PRESCRICAO_MEDICA, RENDER)){
			retorno = habilitaBotaoPrescreverPrescricaoMedica(crg);
		}else{
			if(crg.getAtdSeq() != null){
				final AghAtendimentos atd = getAghuFacade().obterAghAtendimentoPorChavePrimaria(crg.getAtdSeq());
				if(atd != null){
					List<DominioOrigemAtendimento> listaOrigens = new ArrayList<DominioOrigemAtendimento>();
					listaOrigens.add(DominioOrigemAtendimento.N);
					listaOrigens.add(DominioOrigemAtendimento.I);
					listaOrigens.add(DominioOrigemAtendimento.U);
					if (listaOrigens.contains(atd.getOrigem()) && 
							DominioPacAtendimento.S.equals(atd.getIndPacAtendimento())
							){
						retorno = true;
					}
				}
			} else { // considerar atd não associado à cirurgia p/prescrever - Nara 06/01/2010
				DominioOrigemAtendimento[] origens = new DominioOrigemAtendimento[] { DominioOrigemAtendimento.I, DominioOrigemAtendimento.U}; 
				final List<Integer> atds = getAghuFacade().listarSeqAtdPorOrigem(crg.getPacCodigo(), origens);
				if(!atds.isEmpty()){
					retorno = true;
				}
			}
		}
		return retorno;
	}
	
	public Boolean habilitaBotaoPrescreverPrescricaoMedica(CirurgiaVO crg) throws ApplicationBusinessException, ApplicationBusinessException{
		Boolean retorno = false;
		if(permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), PRESCRICAO_MEDICA, RENDER) || permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), PRESCRICAO_ENFERMAGEM, RENDER)){
			if(crg.getAtdSeq() != null){
				List<DominioOrigemAtendimento> origensAtendimento = Arrays.asList(DominioOrigemAtendimento.U, DominioOrigemAtendimento.I, DominioOrigemAtendimento.N);
				List<AghAtendimentos> atendimentos = getAmbulatorioFacade().
				pesquisarAtendimentoParaPrescricaoMedica(null, crg.getAtdSeq(),
							origensAtendimento ,
							DominioOrigemAtendimento.getOrigensDePrescricaoAmbulatorial());
				if(atendimentos != null && !atendimentos.isEmpty()){
					retorno = true;
				}
			}else {
				DominioOrigemAtendimento[] origens = new DominioOrigemAtendimento[] { DominioOrigemAtendimento.I, DominioOrigemAtendimento.U, DominioOrigemAtendimento.A};
				List<Integer> atds = getAghuFacade().listarSeqAtdPorOrigem(crg.getPacCodigo(), origens);
				if(!atds.isEmpty()){
					for (Integer atd : atds) {
						List<DominioOrigemAtendimento> origensAtendimento = Arrays.asList(DominioOrigemAtendimento.U, DominioOrigemAtendimento.I);
						List<AghAtendimentos> atendimentos = getAmbulatorioFacade().
						pesquisarAtendimentoParaPrescricaoMedica(null,atd,
								origensAtendimento ,
								Arrays.asList(DominioOrigemAtendimento.A));
						if(atendimentos != null && !atendimentos.isEmpty()){
							retorno = true;
						}
					}
				}
			}
		}
		return retorno;
	}
	
	private void preparaBtSolicitar(final IPacienteFacade pacienteFacade, final Date dataAtualTruncada, CirurgiaVO crg) {
		
		if(DominioOrigemPacienteCirurgia.I.equals(crg.getOrigemPacienteCirurgia())){
			if(DateUtil.validaDataMenor(DateUtil.truncaData(crg.getDataInicioCirurgia()), dataAtualTruncada) ||
					DominioSituacaoCirurgia.CANC.equals(crg.getSituacao())){
				crg.setBtSolicitar(false);
			} else {
//				#42044
				AghAtendimentos aghAtendimentos = null;
				if (crg.getAtdSeq() != null) {
					aghAtendimentos = iAghuFacade.obterAghAtendimentosPorSeq(crg.getAtdSeq());
				}
				if(crg.getAtdSeq() == null || /*#42044*/(DominioOrigemAtendimento.C.equals(aghAtendimentos.getOrigem()))){										
					if(crg.getTemPacInternacao() != null && crg.getTemPacInternacao() > 0){
						crg.setBtSolicitar(true);
					} else {
						crg.setBtSolicitar(false);
						crg.setDesenho3(CirurgiaVO.PACIENTE_SEM_INT);
						crg.setTitleDesenho3(CirurgiaVO.TITLE_PACIENTE_SEM_INT);
					}
				} else {
					crg.setBtSolicitar(true);
				}
			}
		}
	}

	/** ORADB: CRG.POST_QUERY */
	private void verificarExigenciaCERIH(final TelaListaCirurgiaVO tela, final CirurgiaVO crg, 
			final Date dataInicioCerih, final boolean tempPermissaoAvisoCERIH){
		// Sinalizar os procedimentos que exigem CERIH -- André Luiz Machado - 08/02/2010
		boolean vExigeCerih = false;		
		// 1) A data de agendamento (MBC_CIRURGIAS.DATA) for maior ou igual a 01/01/2010;
		if(DateUtil.validaDataMaiorIgual(crg.getCrgData(), dataInicioCerih)){
			// 2)	A cirurgia não foi cancelada (MBC_CIRURGIAS.SITUACAO <> 'CANC');
			if(!DominioSituacaoCirurgia.CANC.equals(crg.getSituacao())){
				// 3)	O usuário conectado possuir a ação "AVISO CERIH LISTA CIRURGIAS";
				if(tempPermissaoAvisoCERIH){
					//  4)	Pelo menos um dos procedimentos da cirurgia exigir CERIH.
					// caso a cirurgia foi realizada (MBC_CIRURGIAS.SITUACAO = 'RZDA') 
					// e a nota digitada (MBC_CIRURGIAS.IND_DIGT_NOTA_SALA = 'S'), 
					// considerar apenas os procedimentos da nota (MBC_PROC_ESP_POR_CIRURGIAS.IND_RESP_PROC = 'NOTA'), 
					vExigeCerih = (crg.getExigeCerih() != null && crg.getExigeCerih() > 0);
				} 
			}
		}
		if(vExigeCerih){
			crg.setCorExibicao(CirurgiaVO.LARANJA);
		}
	}
	
	// TODO eSchweigert 27/03/2013 Rever código, Vicente ficou de migrar algumas tabelas referente a permissões
	private ControlesEvolucao inicializaControlesEvolucao(final TelaListaCirurgiaVO tela) throws ApplicationBusinessException{
		// busca qual é a categoria profissional enfermagem
		final Integer catefProfEnf = getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_CATEG_PROF_ENF);
		// busca qual é a categoria profissional médico
		final Integer catefProfMed = getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_CATEG_PROF_MEDICO);
		// categoria outros profissionais
		final Integer cateOutros = getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_CATEG_PROF_OUTROS);
		final List<CseCategoriaProfissional> categorias = getCascaFacade().pesquisarCategoriaProfissional(tela.getServidorLogado());
		CseCategoriaProfissional categoria = null;
		if(!categorias.isEmpty()){
			categoria = categorias.get(0);
		}
		final Integer seq = categoria != null ? categoria.getSeq() : null;
		return new ControlesEvolucao( (catefProfEnf.equals(seq)), 
									  (catefProfMed.equals(seq)), 
									  (cateOutros.equals(seq)),
									  catefProfEnf, catefProfMed, cateOutros
									);
	}
	
	/** ORADB EVT_WHEN_NEW_FORM_INSTANCE */
	public TelaListaCirurgiaVO inicializarTelaListaCirurgiaVO(final String nomeMicrocomputador, boolean carregaUnidadeFuncionalFuncionario) throws ApplicationBusinessException, ApplicationBusinessException{
		final TelaListaCirurgiaVO tela = new TelaListaCirurgiaVO();
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		tela.setDtProcedimento(DateUtil.truncaData(new Date()));
		tela.setPesquisaAtiva(Boolean.FALSE);
		tela.setMatricula(servidorLogadoFacade.obterServidorLogado().getId().getMatricula());
		tela.setVinculo(servidorLogadoFacade.obterServidorLogado().getId().getVinCodigo());
		tela.setDesmarcar(getParametroFacade().buscarValorShort(AghuParametrosEnum.P_MOT_DESMARCAR));
		tela.setDesmarcarAdm(getParametroFacade().buscarValorShort(AghuParametrosEnum.P_MOT_DESMARCAR_ADM));
		tela.setpDiasNotaAdicional(getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_DIAS_NOTA_ADICIONAL));
		tela.setConvenioSusPadrao(getParametroFacade().buscarValorShort(AghuParametrosEnum.P_CONVENIO_SUS_PADRAO));
		tela.setServidorLogado(servidorLogado);
		Long vTemEquipe = this.getPrescricaoMedicaFacade().pesquisarAssociacoesPorServidorCount(servidorLogado);
		if(vTemEquipe > 0L){
			tela.setEquipe(new SuggestionListaCirurgiaVO("EQUIPES DO USUÁRIO", true,  servidorLogado.getId().getMatricula(),  servidorLogado.getId().getVinCodigo()));
		}
		tela.setLoginUsuarioLogado(servidorLogado.getUsuario());
		if(carregaUnidadeFuncionalFuncionario){
			tela.setUnidade(getBlocoCirurgicoFacade().obterUnidadeFuncionalCirurgia(nomeMicrocomputador));
		}
		return tela;
	}
	
	/** ORADB EVT_WHEN_NEW_RECORD_INSTANCE */
	public void atualizaBotoes( final TelaListaCirurgiaVO tela, final CirurgiaVO crg){
		// Habilitar ou desabilitar botão Evolucao da internacao
		// habilitarDesabilitarEvolucaoInterna(crg, aghuFacade, blocoCirurgicoON); eSchweigert 27/03/2013 fora de escopo
		// habilita/desabilita imp aval pre anestesica
		// p_mostra_aval_anes_cirg;	eSchweigert 27/03/2013 fora de escopo
		// habilita/desabilita BOTÃO ANESTESIA
		// p_habilita_botao_anestesia;	eSchweigert 27/03/2013 fora de escopo
		crg.setBtLaudoAih( (crg.getPacCodigo() != null) );
		if(!DominioSituacaoCirurgia.CANC.equals(crg.getSituacao())){
			// alterado em 19/07 se int, se não tem atend crg, vê se o pac está internado
			if(DominioOrigemPacienteCirurgia.I.equals(crg.getOrigemPacienteCirurgia())){
				if(crg.getAtdSeq() == null){					
					if(crg.getTemPacInternacao() != null && crg.getTemPacInternacao() > 0){
						crg.setBtSolicitar(true);
					} else {
						crg.setBtSolicitar(false);
					}
				} else {
					crg.setBtSolicitar(true);
				}
			} else {
				crg.setBtSolicitar(true);
			}
		}
		// Habilita/Desabilita Botão de Impressão de Pulseira -- André Machado - 27/02/2012
		crg.setBtImprimirPulseira( (crg.getPacCodigo() != null) );
	}
	
	/**
	 * Implementação relacionada ao evento dos botões da Lista de Cirurgias. 
	 *  
	 * ORADB: EVT_WHEN_BUTTON_PRESSED
	 * 
	 * @param crgVO
	 */
	public void executarEventoBotaoPressionadoListaCirurgias(CirurgiaVO crgVO) {
		if (crgVO.getAtdSeq() == null) {
			DominioOrigemAtendimento[] origens = new DominioOrigemAtendimento[] { DominioOrigemAtendimento.I, DominioOrigemAtendimento.U};
			List<Integer> listaAtdSeq = getAghuFacade().listarSeqAtdPorOrigem(crgVO.getPacCodigo(), origens);
			if (!listaAtdSeq.isEmpty()) {
				crgVO.setAtdSeq(listaAtdSeq.get(0));
			}
		}
	}
	
	public String mbcImpressao(Integer crgSeq)
			throws ApplicationBusinessException {
		// Verifica se tem descr. cirurgica associada à cirurgia ou descrição de
		// PDT
		Long countDescricaoCirurgica = getMbcDescricaoCirurgicaDAO()
				.obterCountDescricaoCirurgicaPorCrgSeq(crgSeq);
		Long countPdt = Long.valueOf(0);
		if (countDescricaoCirurgica == 0) {
			countPdt = getPdtDescricaoDAO()
					.obterQuantidadePdtDescricaoPorCirurgiaSimples(crgSeq);
			if (countPdt > 0
					&& (getCascaFacade()
							.usuarioTemPermissao(
									servidorLogadoFacade.obterServidorLogado().getUsuario(),
									ListaCirurgiasDescCirurgicaON.PERMISSAO_REALIZAR_DESCRICAO_CIRURGICA,
									ListaCirurgiasDescCirurgicaON.PERMISSAO_ACAO_CONSULTAR) 
							|| getCascaFacade().usuarioTemPermissao(
									servidorLogadoFacade.obterServidorLogado().getUsuario(),
									ListaCirurgiasDescCirurgicaON.PERMISSAO_REALIZAR_DESCRICAO_CIRURGICA_PDT,
									ListaCirurgiasDescCirurgicaON.PERMISSAO_ACAO_CONSULTAR))) {
				return "PDT";
			} else {
				throw new ApplicationBusinessException(
						ListarCirurgiasDescCirurgicaONExceptionCode.ERRO_USUARIO_NAO_PERMITIDO_DESCRICAO_PDT);
			}
		} else {
			if (getCascaFacade()
					.usuarioTemPermissao(
							servidorLogadoFacade.obterServidorLogado().getUsuario(),
							ListaCirurgiasDescCirurgicaON.PERMISSAO_REALIZAR_DESCRICAO_CIRURGICA,
							ListaCirurgiasDescCirurgicaON.PERMISSAO_ACAO_CONSULTAR)
					|| getCascaFacade()
							.usuarioTemPermissao(
									servidorLogadoFacade.obterServidorLogado().getUsuario(),
									ListaCirurgiasDescCirurgicaON.PERMISSAO_REALIZAR_DESCRICAO_CIRURGICA_PDT,
									ListaCirurgiasDescCirurgicaON.PERMISSAO_ACAO_CONSULTAR)) {
				return "CIR";
			} else {
				throw new ApplicationBusinessException(
						ListarCirurgiasDescCirurgicaONExceptionCode.ERRO_USUARIO_ADM_NAO_PERMITIDO_DESCRICAO_CIRURGICA);
			}
		}
	}
	
	private class ControlesEvolucao {
		private boolean vCagEnf;
		private boolean vCagMed;
		private boolean vCagOutros;
		private Integer catefProfEnf;
		private Integer catefProfMed;
		private Integer cateOutros;

		public ControlesEvolucao(boolean vCagEnf, boolean vCagMed, boolean vCagOutros, Integer catefProfEnf, Integer catefProfMed, Integer cateOutros) {
			this.vCagEnf = vCagEnf;			this.vCagMed = vCagMed;			this.vCagOutros = vCagOutros;			this.catefProfEnf = catefProfEnf;
			this.catefProfMed = catefProfMed;			this.cateOutros = cateOutros;
		}
		public boolean isvCagEnf() { 	return vCagEnf; }
		public boolean isvCagMed() { 	return vCagMed; }
		public boolean isvCagOutros() {	return vCagOutros;}
	}
	
	/**
	 * ORADB: EVT_PRE_UPDATE
	 * 
	 * @param mtcSeq
	 * @param questao
	 * @param valorValidoCanc
	 * @param complementoCanc
	 * @throws ApplicationBusinessException 
	 */
	public void validarQuestaoValorValidoEComplemento(MbcQuestao questao, 
			MbcValorValidoCanc valorValidoCanc, String complementoCanc) throws ApplicationBusinessException {
		if (questao != null) {
			if (getMbcValorValidoCancDAO().obterCountValorValidoAtivoPorQesMtcSeq(questao.getId().getMtcSeq()) > 0 && valorValidoCanc == null) {
				// Escolha uma resposta para a questão obrigatoriamente.
				throw new ApplicationBusinessException(
						ListarCirurgiasONExceptionCode.ERRO_LISTA_CIRURGIA_RESPOSTA_CANCELAMENTO_NAO_INFORMADA);
			}
			if (questao.getExigeComplemento() && StringUtils.isEmpty(complementoCanc)) {
				// Complemento deve ser informado obrigatoriamente
				throw new ApplicationBusinessException(
						ListarCirurgiasONExceptionCode.ERRO_LISTA_CIRURGIA_COMPLEMENTO_CANCELAMENTO_NAO_INFORMADO);
			}			
		}
	}
	
	public boolean habilitaBotaoVisualizarRegistros(Integer atdSeqSelecionado, boolean isCheckSalaRecuperacao, Date crgSelecionada) throws ApplicationBusinessException{
		if(atdSeqSelecionado == null){
			return false;
		} 
		
		AghAtendimentos atendimento = this.getAghuFacade().obterAghAtendimentoPorChavePrimaria(atdSeqSelecionado);

		if(atendimento == null || atendimento.getSeq() == null){
			return false;
		}
		
		if(atendimento.getOrigem() == DominioOrigemAtendimento.I &&
			atendimento.getIndPacAtendimento() == DominioPacAtendimento.S && 
			this.getAghuFacade().unidadeFuncionalPossuiCaracteristica(atendimento.getUnidadeFuncional().getSeq(), ConstanteAghCaractUnidFuncionais.CONTROLES_PACIENTE_INFORMATIZADO)){
			
			return true;
		}
		if(atendimento.getOrigem() != DominioOrigemAtendimento.I && isCheckSalaRecuperacao){
			return true;
		}
		if(atendimento.getOrigem() != DominioOrigemAtendimento.I && !isCheckSalaRecuperacao && crgSelecionada != null){
			Calendar dataDiaAnterior = Calendar.getInstance();
			dataDiaAnterior.add(Calendar.DATE, -1);
			Calendar dataCirurgia = Calendar.getInstance();
			dataCirurgia.setTime(crgSelecionada);
			if(dataCirurgia.after(dataDiaAnterior)){
				return true;
			}
		}
		return false;
	}

	public Boolean verificarCancelamentoCirurgiaComDescricao(Integer crgSeq) {
		return getMbcDescricaoCirurgicaDAO().obterQuantidadeMbcDescricaoCirurgicaPorCirurgia(crgSeq) > 0 
					|| getPdtDescricaoDAO().obterQuantidadePdtDescricaoPorCirurgiaSimples(crgSeq) > 0;
	}
	
	// ONs e Rns
	protected EscalaCirurgiasON getEscalaCirurgiasON(){ return escalaCirurgiasON;	}
	protected ListaCirurgiasDescCirurgicaON getListaCirurgiasDescCirurgicaON() { return listaCirurgiasDescCirurgicaON; }
	
	// Daos
	protected MbcProcedimentoCirurgicoDAO getMbcProcedimentoCirurgicoDAO() { return mbcProcedimentoCirurgicoDAO; }
	protected MbcDescricaoCirurgicaDAO getMbcDescricaoCirurgicaDAO(){ return mbcDescricaoCirurgicaDAO;}
	protected MbcCirurgiasDAO getMbcCirurgiasDAO() { return mbcCirurgiasDAO; }
	protected MbcQuestaoDAO getMbcQuestaoDAO() { return mbcQuestaoDAO; }
	protected MbcValorValidoCancDAO getMbcValorValidoCancDAO() { return mbcValorValidoCancDAO;}
	protected MbcFichaAnestesiasDAO getMbcFichaAnestesiasDAO() { return mbcFichaAnestesiasDAO;	}
	protected MbcMotivoCancelamentoDAO getMbcMotivoCancelamentoDAO() { return mbcMotivoCancelamentoDAO;	}
	protected MbcProfAtuaUnidCirgsDAO getMbcProfAtuaUnidCirgsDAO() { return mbcProfAtuaUnidCirgsDAO; 	}
	protected MbcProcEspPorCirurgiasDAO getMbcProcEspPorCirurgiasDAO() {return mbcProcEspPorCirurgiasDAO;	}
	protected MbcControleEscalaCirurgicaDAO getMbcControleEscalaCirurgicaDAO() { return mbcControleEscalaCirurgicaDAO; }
	protected PdtDescricaoDAO getPdtDescricaoDAO() { return pdtDescricaoDAO; }	
	// Facades
	protected IBlocoCirurgicoPortalPlanejamentoFacade getBlocoCirurgicoPortalPlanejamentoFacade() { return  iBlocoCirurgicoPortalPlanejamentoFacade; 	}
	protected IBlocoCirurgicoFacade getBlocoCirurgicoFacade() { return  iBlocoCirurgicoFacade; }
	protected IParametroFacade getParametroFacade(){ return  iParametroFacade; }
	protected IFaturamentoFacade getFaturamentoFacade(){ return  iFaturamentoFacade; }
	protected ICascaFacade getCascaFacade(){ return  iCascaFacade;	}
	protected IPacienteFacade getPacienteFacade(){ return  iPacienteFacade; }
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() { return this.iRegistroColaboradorFacade;	}	
	protected IExamesLaudosFacade getExamesLaudosFacade() { return  iExamesLaudosFacade;	}
	protected IAghuFacade getAghuFacade() { return  iAghuFacade;}
	protected ICertificacaoDigitalFacade getCertificacaoDigitalFacade() { return  iCertificacaoDigitalFacade;	}
	protected IAmbulatorioFacade getAmbulatorioFacade() { return  iAmbulatorioFacade;	}
	protected IPesquisaInternacaoFacade getPesquisaInternacaoFacade() { return iPesquisaInternacaoFacade;	}
	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade() { return iPrescricaoMedicaFacade;	}
}