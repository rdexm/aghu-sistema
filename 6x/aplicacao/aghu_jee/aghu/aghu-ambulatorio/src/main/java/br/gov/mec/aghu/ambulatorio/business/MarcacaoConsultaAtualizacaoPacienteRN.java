package br.gov.mec.aghu.ambulatorio.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.MarcacaoConsultaRN.MarcacaoConsultaRNExceptionCode;
import br.gov.mec.aghu.ambulatorio.dao.MamAlergiasDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamAltasSumarioDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamAtestadosDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamDestinosDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamDiagnosticoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamFiguraAnamnesesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamFiguraEvolucaoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamImpDiagnosticaDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamInterconsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamLaudoAihDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamLembreteDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamMedicamentoAtivoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamMotivoAtendimentosDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamPacienteMinhaListaDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamPacienteMinhaListaJnDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamPistaNotifInfeccaoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamPistaNotifInfecccaoJnDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamReceituarioCuidadoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamReceituariosDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamRelatorioDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamSolicHemoterapicasDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamSolicProcedimentoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamSolicitacaoRetornoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTrgAlergiasDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTrgAlergiasJnDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTrgEncInternoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTriagensDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MamAlergias;
import br.gov.mec.aghu.model.MamAltaSumario;
import br.gov.mec.aghu.model.MamAtestados;
import br.gov.mec.aghu.model.MamDestinos;
import br.gov.mec.aghu.model.MamDiagnostico;
import br.gov.mec.aghu.model.MamFiguraAnamnese;
import br.gov.mec.aghu.model.MamFiguraEvolucao;
import br.gov.mec.aghu.model.MamImpDiagnostica;
import br.gov.mec.aghu.model.MamInterconsultas;
import br.gov.mec.aghu.model.MamLaudoAih;
import br.gov.mec.aghu.model.MamLembrete;
import br.gov.mec.aghu.model.MamMedicamentoAtivo;
import br.gov.mec.aghu.model.MamMotivoAtendimento;
import br.gov.mec.aghu.model.MamPacienteMinhaLista;
import br.gov.mec.aghu.model.MamPacienteMinhaListaJn;
import br.gov.mec.aghu.model.MamPistaNotifInfeccao;
import br.gov.mec.aghu.model.MamPistaNotifInfeccaoJn;
import br.gov.mec.aghu.model.MamReceituarioCuidado;
import br.gov.mec.aghu.model.MamReceituarios;
import br.gov.mec.aghu.model.MamRelatorio;
import br.gov.mec.aghu.model.MamSolicHemoterapica;
import br.gov.mec.aghu.model.MamSolicProcedimento;
import br.gov.mec.aghu.model.MamSolicitacaoRetorno;
import br.gov.mec.aghu.model.MamTrgAlergiaJn;
import br.gov.mec.aghu.model.MamTrgAlergias;
import br.gov.mec.aghu.model.MamTrgEncInterno;
import br.gov.mec.aghu.model.MamTriagens;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;

@Stateless
public class MarcacaoConsultaAtualizacaoPacienteRN extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5519127500536961028L;

	private static final Log LOG = LogFactory.getLog(MarcacaoConsultaAtualizacaoPacienteRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private AmbulatorioTriagemRN ambulatorioTriagemRN;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	
	@Inject
	private MamInterconsultasDAO mamInterconsultasDAO;
	
	@Inject
	private MamPistaNotifInfecccaoJnDAO mamPistaNotifInfecccaoJnDAO;
	
	@Inject
	private MamPistaNotifInfeccaoDAO mamPistaNotifInfeccaoDAO;

	@Inject
	private MamPacienteMinhaListaJnDAO mamPacienteMinhaListaJnDAO;

	@Inject
	private MamTrgAlergiasJnDAO mamTrgAlergiasJnDAO;
	
	@Inject
	private MamTrgEncInternoDAO mamTrgEncInternoDAO;
	
	@Inject
	private MamTriagensDAO mamTriagensDAO;
	
	@Inject
	private MamAltasSumarioDAO mamAltasSumarioDAO;

	@Inject
	private MamAtestadosDAO mamAtestadosDAO;

	@Inject
	private MamDiagnosticoDAO mamDiagnosticoDAO;

	@Inject
	private MamFiguraAnamnesesDAO mamFiguraAnamnesesDAO;

	@Inject
	private MamFiguraEvolucaoDAO mamFiguraEvolucaoDAO;

	@Inject
	private MamLaudoAihDAO mamLaudoAihDAO;

	@Inject
	private MamMedicamentoAtivoDAO mamMedicamentoAtivoDAO;

	@Inject
	private MamMotivoAtendimentosDAO mamMotivoAtendimentosDAO;

	@Inject
	private MamReceituariosDAO mamReceituariosDAO;

	@Inject
	private MamReceituarioCuidadoDAO mamReceituarioCuidadoDAO;

	@Inject
	private MamRelatorioDAO mamRelatorioDAO;

	@Inject
	private MamSolicitacaoRetornoDAO mamSolicitacaoRetornoDAO;

	@Inject
	private MamLembreteDAO mamLembreteDAO;

	@Inject
	private MamSolicHemoterapicasDAO mamSolicHemoterapicasDAO;

	@Inject
	private MamSolicProcedimentoDAO mamSolicProcedimentoDAO;

	@Inject
	private MamAlergiasDAO mamAlergiasDAO;

	@Inject
	private MamDestinosDAO mamDestinosDAO;

	@Inject
	private MamImpDiagnosticaDAO mamImpDiagnosticaDAO;
	
	@Inject
	private MamTrgAlergiasDAO mamTrgAlergiasDAO;

	@Inject
	private MamPacienteMinhaListaDAO mamPacienteMinhaListaDAO;

	
	/**
	 * #42360 RN-1.2 Atualiza MAM_ALTA_SUMARIOS
	 * 
	 * @param conNumero
	 * @param paciente
	 * @throws ApplicationBusinessException 
	 */
	public void atualizarMamAltaSumarios(Integer conNumero, AipPacientes paciente) throws ApplicationBusinessException {
		try {
			MamAltaSumario mamAltaSumario = this.mamAltasSumarioDAO.buscarAltaSumarioPorNumeroConsulta(conNumero);
			if (mamAltaSumario != null) {
				mamAltaSumario.setPaciente(paciente);
	
				this.mamAltasSumarioDAO.atualizar(mamAltaSumario);
				this.mamAltasSumarioDAO.flush();
			}
			
		} catch (Exception e) {
			throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.MAM_01226);
		}
	}
	
	public void gerarProntuarioVirtualAtualizarDependencias(Integer conNumero, Integer pacCodigo, AipPacientes paciente, String nomeMicrocomputador)
			throws ApplicationBusinessException {
		//Integer vProntuario = null;

		List<MamTrgEncInterno> listTrgSeq = this.getMamTrgEncInternoDAO().obterCursorTei(conNumero);

		for (MamTrgEncInterno trgSeq : listTrgSeq) {

			MamTriagens mamTriagens = this.mamTriagensDAO.buscarTriagensPorPacCodigoSeq(pacCodigo, trgSeq.getId().getTrgSeq());
			if (mamTriagens != null) {
				mamTriagens.setPaciente(paciente);

				try {
					this.mamTriagensDAO.atualizar(mamTriagens);
					this.mamTriagensDAO.flush();
				} catch (Exception e) {
					throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.MAM_03371);
				}
			}

			MamTrgAlergias mamTrgAlergias = this.mamTrgAlergiasDAO.buscarTrgAlergiasPorPacCodigoSeq(pacCodigo, trgSeq.getId().getTrgSeq());
			if (mamTrgAlergias != null) {
				mamTrgAlergias.setPacCodigo(pacCodigo);

				try {
					this.mamTrgAlergiasDAO.atualizar(mamTrgAlergias);
					this.mamTrgAlergiasDAO.flush();
					/**
					 * @ORADB: MAMT_TAI_ARU
					 */
					gravarJournalMamTrgAlergias(mamTrgAlergias, DominioOperacoesJournal.UPD);
				} catch (Exception e) {
					throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.MAM_03372);
				}

			}

			MamPacienteMinhaLista mamPacienteMinhaLista = this.mamPacienteMinhaListaDAO.buscarPacMinhaListaPorPacCodigoSeq(pacCodigo, trgSeq.getId().getTrgSeq());
			if (mamPacienteMinhaLista != null) {
				mamPacienteMinhaLista.setAipPacientes(paciente);

				try {
					this.mamPacienteMinhaListaDAO.atualizar(mamPacienteMinhaLista);
					this.mamPacienteMinhaListaDAO.flush();
					/**
					 * ORADB: MAMT_PIL_ARU
					 */
					gravarJournalPacientesMinhaLista(mamPacienteMinhaLista, DominioOperacoesJournal.UPD);
				} catch (Exception e) {
					throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.MAM_03659);
				}
			}
			//vProntuario = 
			ambulatorioTriagemRN.inserirPontuarioVirtual(pacCodigo, nomeMicrocomputador);
		}
	}
	
	
	/**
	 * #42360 RN-1.21 Atualiza MAM_IMP_DIAGNOSTICAS
	 * @param conNumero
	 * @param paciente
	 * @throws ApplicationBusinessException 
	 */
	public void atualizarMamImpDiagnosticas(Integer conNumero, AipPacientes paciente) throws ApplicationBusinessException {
		try{
			MamImpDiagnostica mamImpDiagnostica = this.mamImpDiagnosticaDAO.buscarMamImpDiagnosticaPorNumeroConsulta(conNumero);
			if (mamImpDiagnostica != null) {
				mamImpDiagnostica.setAipPacientes(paciente);
	
				this.mamImpDiagnosticaDAO.atualizar(mamImpDiagnostica);
				this.mamImpDiagnosticaDAO.flush();
			}
		} catch (Exception e) {
			throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.MAM_04122);
		}
	}
	
	/**
	 * #42360 RN-1.20 Atualiza MAM_DESTINOS
	 * @param conNumero
	 * @param paciente
	 * @throws ApplicationBusinessException 
	 */
	public void atualizarMamDestinos(Integer conNumero, Integer pacCodigo) throws ApplicationBusinessException {
		try{
			MamDestinos mamDestinos = this.mamDestinosDAO.buscarDestinosPorNumeroConsulta(conNumero);
			if (mamDestinos != null) {
				mamDestinos.setPacCodigo(pacCodigo);
	
				this.mamDestinosDAO.atualizar(mamDestinos);
				this.mamDestinosDAO.flush();
			}
		} catch (Exception e) {
			throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.MAM_03427);
		}
	}
	
	/**
	 * #42360 RN-1.19 Atualiza MAM_ALERGIAS
	 * @param conNumero
	 * @param paciente
	 * @throws ApplicationBusinessException 
	 */
	public void atualizarMamAlergias(Integer conNumero, AipPacientes paciente) throws ApplicationBusinessException {
		try{
			MamAlergias MamAlergias = this.mamAlergiasDAO.buscarAlergiasPorNumeroConsulta(conNumero);
			if (MamAlergias != null) {
				MamAlergias.setPaciente(paciente);
	
				this.mamAlergiasDAO.atualizar(MamAlergias);
				this.mamAlergiasDAO.flush();
			}
		} catch (Exception e) {
			throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.MAM_03426);
		}
	}
	
	/**
	 * #42360 RN-1.18 Atualiza MAM_SOLIC_PROCEDIMENTOS
	 * @param conNumero
	 * @param paciente
	 * @throws ApplicationBusinessException 
	 */
	public void atualizarMamSolicProcedimentos(Integer conNumero, AipPacientes paciente) throws ApplicationBusinessException {
		try{
			MamSolicProcedimento mamSolicProcedimento = this.mamSolicProcedimentoDAO.buscarSolicProcedimentoPorNumeroConsulta(conNumero);
			if (mamSolicProcedimento != null) {
				mamSolicProcedimento.setAipPacientes(paciente);
	
				this.mamSolicProcedimentoDAO.atualizar(mamSolicProcedimento);
				this.mamSolicProcedimentoDAO.flush();
			}
		} catch (Exception e) {
			throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.MAM_02435);
		}
	}
	
	/**
	 * #42360 RN-1.17 Atualiza MAM_SOLIC_HEMOTERAPICAS
	 * @param conNumero
	 * @param paciente
	 * @throws ApplicationBusinessException 
	 */
	public void atualizarMamSolicHemoterapicas(Integer conNumero, AipPacientes paciente) throws ApplicationBusinessException {
		try{
			MamSolicHemoterapica mamSolicHemoterapica = this.mamSolicHemoterapicasDAO.buscarSolicHemoterapicasPorNumeroConsulta(conNumero);
			if (mamSolicHemoterapica != null) {
				mamSolicHemoterapica.setAipPacientes(paciente);
	
				this.mamSolicHemoterapicasDAO.atualizar(mamSolicHemoterapica);
				this.mamSolicHemoterapicasDAO.flush();
			}
		} catch (Exception e) {
			throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.MAM_02434);
		}
	}
	
	/**
	 * #42360 RN-1.16 Atualiza MAM_LEMBRETES
	 * @param conNumero
	 * @param paciente
	 * @throws ApplicationBusinessException 
	 */
	public void atualizarMamLembretes(Integer conNumero, AipPacientes paciente) throws ApplicationBusinessException {
		try{
			MamLembrete mamLembrete = this.mamLembreteDAO.buscarLembretePorNumeroConsulta(conNumero);
			if (mamLembrete != null) {
				mamLembrete.setAipPacientes(paciente);
	
				this.mamLembreteDAO.atualizar(mamLembrete);
				this.mamLembreteDAO.flush();
			}
		} catch (Exception e) {
			throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.MAM_02332);
		}
	}
	
	/**
	 * #42360 RN-1.15 Atualiza MAM_SOLICITACAO_RETORNO
	 * @param conNumero
	 * @param paciente
	 * @throws ApplicationBusinessException 
	 */
	public void atualizarMamSolicitacaoRetorno(Integer conNumero, AipPacientes paciente) throws ApplicationBusinessException {
		try{
			MamSolicitacaoRetorno mamSolicitacaoRetorno = this.mamSolicitacaoRetornoDAO.buscarSolicitacaoRetornoPorNumeroConsulta(conNumero);
			if (mamSolicitacaoRetorno != null) {
				mamSolicitacaoRetorno.setAipPacientes(paciente);
	
				this.mamSolicitacaoRetornoDAO.atualizar(mamSolicitacaoRetorno);
				this.mamSolicitacaoRetornoDAO.flush();
			}
		} catch (Exception e) {
			throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.MAM_01245);
		}
	}
	
	/**
	 * #42360 RN-1.14 Atualiza MAM_RELATORIOS
	 * @param conNumero
	 * @param paciente
	 * @throws ApplicationBusinessException 
	 */
	public void atualizarMamRelatorios(Integer conNumero, AipPacientes paciente) throws ApplicationBusinessException {
		try{
			MamRelatorio mamRelatorio = this.mamRelatorioDAO.buscarRelatorioPorNumeroConsulta(conNumero);
			if (mamRelatorio != null) {
				mamRelatorio.setAipPacientes(paciente);
	
				this.mamRelatorioDAO.atualizar(mamRelatorio);
				this.mamRelatorioDAO.flush();
			}
		} catch (Exception e) {
			throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.MAM_01244);
		}
	}
	
	/**
	 * #42360 RN-1.3 Atualiza MAM_PISTA_NOTIF_INFECCOES
	 * @throws ApplicationBusinessException
	 */
	public void atualizarMamPistaNotifInfeccao(Integer conNumero, AipPacientes paciente, Integer pacCodigo) throws ApplicationBusinessException {
		try {
			MamPistaNotifInfeccao mamPista = mamPistaNotifInfeccaoDAO.obterMamPistaNotifInfeccao(conNumero);
	
			if (mamPista != null) {
				mamPista.setPacCodigo(pacCodigo);
	
				this.mamPistaNotifInfeccaoDAO.atualizar(mamPista);
				this.mamPistaNotifInfeccaoDAO.flush();
				/**
				 * @ORADB: MAMT_PNN_ARU
				 */
				gravarJournalMamPistaNotifInfeccao(mamPista, DominioOperacoesJournal.UPD);
			}
			
		} catch (ApplicationBusinessException e) {
			LOG.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.MAM_01651);
		}
	}
	
	private void gravarJournalMamPistaNotifInfeccao(MamPistaNotifInfeccao pistaNotifInfeccao, DominioOperacoesJournal operacao)
			throws ApplicationBusinessException {
		RapServidores servidor = this.registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
		MamPistaNotifInfeccaoJn journal = new MamPistaNotifInfeccaoJn();

		journal.setSeq(pistaNotifInfeccao.getSeq());
		journal.setIndTipoPista(pistaNotifInfeccao.getIndTipoPista());
		journal.setContaminacao(pistaNotifInfeccao.getContaminacao());
		journal.setDthrEvento(pistaNotifInfeccao.getDthrEvento());
		journal.setDtInicPergunta(pistaNotifInfeccao.getDtInicPergunta());
		journal.setDtFimPerguntaRotina(pistaNotifInfeccao.getDtFimPerguntaRotina());
		journal.setDtFimPergunta(pistaNotifInfeccao.getDtFimPergunta());
		journal.setCriadoEm(pistaNotifInfeccao.getCriadoEm());
		journal.setPacCodigo(pistaNotifInfeccao.getPacCodigo());
		journal.setCrgSeq(pistaNotifInfeccao.getCrgSeq());
		journal.setAtdSeq(pistaNotifInfeccao.getAtdSeq());
		journal.setSerMatricula(pistaNotifInfeccao.getSerMatricula());
		journal.setSerVinCodigo(pistaNotifInfeccao.getSerVinCodigo());

		if (pistaNotifInfeccao.getConsulta() != null) {
			journal.setConNumero(pistaNotifInfeccao.getConsulta().getNumero());
		}

		journal.setNomeUsuario(servidor.getUsuario());
		journal.setOperacao(operacao);

		mamPistaNotifInfecccaoJnDAO.persistir(journal);
	}


	/**
	 * #42360 RN-1.4 Atualiza MAM_ATESTADOS
	 * @param conNumero
	 * @param paciente
	 * @throws ApplicationBusinessException 
	 */
	public void atualizarMamAtestados(Integer conNumero, AipPacientes paciente) throws ApplicationBusinessException {
		try {
			MamAtestados mamAtestados = this.mamAtestadosDAO.buscarAtestadoPorNumeroConsulta(conNumero);
	
			if (mamAtestados != null) {
				mamAtestados.setAipPacientes(paciente);
	
				this.mamAtestadosDAO.atualizar(mamAtestados);
				this.mamAtestadosDAO.flush();
			}
		} catch (Exception e) {
			throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.MAM_01229);
		}
	}

	/**
	 * #42360 RN-1.5 Atualiza MAM_DIAGNOSTICOS
	 * @param conNumero
	 * @param paciente
	 * @throws ApplicationBusinessException 
	 */
	public void atualizarMamDiagnosticos(Integer conNumero, AipPacientes paciente) throws ApplicationBusinessException {
		try{
			MamDiagnostico mamDiagnostico = this.mamDiagnosticoDAO.buscarDiagnosticoPorNumeroConsulta(conNumero);
			if (mamDiagnostico != null) {
				mamDiagnostico.setPaciente(paciente);
	
				this.mamDiagnosticoDAO.atualizar(mamDiagnostico);
				this.mamDiagnosticoDAO.flush();
			}
		} catch (Exception e) {
			throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.MAM_01230);
		}
	}

	/**
	 * #42360 RN-1.6 Atualiza MAM_FIGURAS_ANAMNESES
	 * @param conNumero
	 * @param paciente
	 * @throws ApplicationBusinessException 
	 */
	public void atualizarMamFigurasAnamnese(Integer conNumero, AipPacientes paciente) throws ApplicationBusinessException {
		try{
			MamFiguraAnamnese mamFiguraAnamnese = this.mamFiguraAnamnesesDAO.buscarFiguraAnamnesesPorNumeroConsulta(conNumero);
			if (mamFiguraAnamnese != null) {
				mamFiguraAnamnese.setAipPacientes(paciente);
	
				this.mamFiguraAnamnesesDAO.atualizar(mamFiguraAnamnese);
				this.mamFiguraAnamnesesDAO.flush();
			}
		} catch (Exception e) {
			throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.MAM_01232);
		}
	}

	/**
	 * #42360 RN-1.7 Atualiza MAM_FIGURAS_EVOLUCOES
	 * @param conNumero
	 * @param paciente
	 * @throws ApplicationBusinessException 
	 */
	public void atualizarMamFigurasEvolucoes(Integer conNumero, AipPacientes paciente) throws ApplicationBusinessException {
		try{
		MamFiguraEvolucao mamFiguraEvolucao = this.mamFiguraEvolucaoDAO.buscarFiguraEvolucaoPorNumeroConsulta(conNumero);
			if (mamFiguraEvolucao != null) {
				mamFiguraEvolucao.setAipPacientes(paciente);
	
				this.mamFiguraEvolucaoDAO.atualizar(mamFiguraEvolucao);
				this.mamFiguraEvolucaoDAO.flush();
			}
		} catch (Exception e) {
			throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.MAM_01233);
		}
	}

	/**
	 * #42360 RN-1.8 Atualiza MAM_INTERCONSULTAS
	 * @param conNumero
	 * @param paciente
	 * @throws ApplicationBusinessException 
	 */
	public void atualizarMamInterconsultas(Integer conNumero, AipPacientes paciente) throws ApplicationBusinessException {
		try{
			MamInterconsultas mamInterconsultas = this.mamInterconsultasDAO.buscarInterconsultasPorNumeroConsulta(conNumero);
			if (mamInterconsultas != null) {
				mamInterconsultas.setPaciente(paciente);
	
				this.mamInterconsultasDAO.atualizar(mamInterconsultas);
				this.mamInterconsultasDAO.flush();
			}
		} catch (Exception e) {
			throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.MAM_01234);
		}
	}

	/**
	 * #42360 RN-1.9 Atualiza MAM_LAUDO_AIHS
	 * @param conNumero
	 * @param paciente
	 * @throws ApplicationBusinessException 
	 */
	public void atualizarMamLaudoAihs(Integer conNumero, AipPacientes paciente) throws ApplicationBusinessException {
		try{
			MamLaudoAih mamLaudoAih = this.mamLaudoAihDAO.buscarLaudoAihPorNumeroConsulta(conNumero);
			if (mamLaudoAih != null) {
				mamLaudoAih.setPaciente(paciente);
	
				this.mamLaudoAihDAO.atualizar(mamLaudoAih);
				this.mamLaudoAihDAO.flush();
			}
		} catch (Exception e) {
			throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.MAM_01235);
		}
	}

	/**
	 * #42360 RN-1.10 Atualiza MAM_MEDICAMENTOS_ATIVOS
	 * @param conNumero
	 * @param paciente
	 * @throws ApplicationBusinessException 
	 */
	public void atualizarMamMedicamentosAtivos(Integer conNumero, AipPacientes paciente) throws ApplicationBusinessException {
		try{
			MamMedicamentoAtivo mamMedicamentoAtivo = this.mamMedicamentoAtivoDAO.buscarMedicamentoAtivoPorNumeroConsulta(conNumero);
			if (mamMedicamentoAtivo != null) {
				mamMedicamentoAtivo.setAipPacientes(paciente);
	
				this.mamMedicamentoAtivoDAO.atualizar(mamMedicamentoAtivo);
				this.mamMedicamentoAtivoDAO.flush();
			}
		} catch (Exception e) {
			throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.MAM_01237);
		}
	}

	/**
	 * #42360 RN-1.11 Atualiza MAM_MOTIVO_ATENDIMENTOS
	 * @param conNumero
	 * @param paciente
	 * @throws ApplicationBusinessException 
	 */
	public void atualizarMamMotivoAtendimentos(Integer conNumero, AipPacientes paciente) throws ApplicationBusinessException {
		try{
			MamMotivoAtendimento mamMotivoAtendimento = this.mamMotivoAtendimentosDAO.buscarMovitoAtendimentoPorNumeroConsulta(conNumero);
			if (mamMotivoAtendimento != null) {
				mamMotivoAtendimento.setPaciente(paciente);
	
				this.mamMotivoAtendimentosDAO.atualizar(mamMotivoAtendimento);
				this.mamMotivoAtendimentosDAO.flush();
			}
		} catch (Exception e) {
			throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.MAM_01238);
		}
	}

	/**
	 * #42360 RN-1.12 Atualiza MAM_RECEITUARIOS
	 * @param conNumero
	 * @param paciente
	 * @throws ApplicationBusinessException 
	 */
	public void atualizarMamReceituarios(Integer conNumero, AipPacientes paciente) throws ApplicationBusinessException {
		try{
			MamReceituarios mamReceituarios = this.mamReceituariosDAO.buscarReceituariosPorNumeroConsulta(conNumero);
			if (mamReceituarios != null) {
				mamReceituarios.setPaciente(paciente);
	
				this.mamReceituariosDAO.atualizar(mamReceituarios);
				this.mamReceituariosDAO.flush();
			}
		} catch (Exception e) {
			throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.MAM_01242);
		}

	}

	/**
	 * #42360 RN-1.13 Atualiza MAM_RECEITUARIO_CUIDADOS
	 * @param conNumero
	 * @param paciente
	 * @throws ApplicationBusinessException 
	 */
	public void atualizarMamReceituarioCuidados(Integer conNumero, AipPacientes paciente) throws ApplicationBusinessException {
		try{
			MamReceituarioCuidado mamReceituarioCuidado = this.mamReceituarioCuidadoDAO.buscarMamReceituarioCuidadoPorNumeroConsulta(conNumero);
			if (mamReceituarioCuidado != null) {
				mamReceituarioCuidado.setPaciente(paciente);
	
				this.mamReceituarioCuidadoDAO.atualizar(mamReceituarioCuidado);
				this.mamReceituarioCuidadoDAO.flush();
			}
		} catch (Exception e) {
			throw new ApplicationBusinessException(MarcacaoConsultaRNExceptionCode.MAM_01243);
		}
	}
	
	
	private void gravarJournalMamTrgAlergias(MamTrgAlergias trgAlergias, DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidor = this.registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
		MamTrgAlergiaJn journal = new MamTrgAlergiaJn();

		journal.setTrgSeq(trgAlergias.getId().getTrgSeq());
		journal.setSeqp(trgAlergias.getId().getSeqp());
		journal.setDescricao(trgAlergias.getDescricao());
		journal.setPacCodigo(trgAlergias.getPacCodigo());
		journal.setMicNome(trgAlergias.getMicNome());
		journal.setCriadoEm(trgAlergias.getCriadoEm());
		journal.setSerMatricula(trgAlergias.getSerMatricula());
		journal.setSerVinCodigo(trgAlergias.getSerVinCodigo());

		journal.setNomeUsuario(servidor.getUsuario());
		journal.setOperacao(operacao);

		mamTrgAlergiasJnDAO.persistir(journal);
	}
	
	private void gravarJournalPacientesMinhaLista(MamPacienteMinhaLista pacienteMinhaLista, DominioOperacoesJournal operacao)
			throws ApplicationBusinessException {
		RapServidores servidor = this.registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
		MamPacienteMinhaListaJn journal = new MamPacienteMinhaListaJn();

		journal.setCmlSeq(pacienteMinhaLista.getId().getCmlSeq());
		journal.setSeqp(pacienteMinhaLista.getId().getSeqp());
		journal.setCriadoEm(pacienteMinhaLista.getCriadoEm());
		journal.setIndMostrar(pacienteMinhaLista.getIndMostrar());
		journal.setIndSituacao(pacienteMinhaLista.getIndSituacao());
		journal.setTrgSeq(pacienteMinhaLista.getTrgSeq());
		journal.setPacCodigo(pacienteMinhaLista.getAipPacientes().getCodigo());

		journal.setNomeUsuario(servidor.getUsuario());
		journal.setOperacao(operacao);

		mamPacienteMinhaListaJnDAO.persistir(journal);
	}
	
	protected MamTrgEncInternoDAO getMamTrgEncInternoDAO() {
		return mamTrgEncInternoDAO;
	}


}
