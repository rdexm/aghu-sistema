package br.gov.mec.aghu.prescricaomedica.business;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioControleSumarioAltaPendente;
import br.gov.mec.aghu.dominio.DominioIndConcluido;
import br.gov.mec.aghu.dominio.DominioIndTipoAltaSumarios;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoSumarioAlta;
import br.gov.mec.aghu.dominio.DominioUnidTempo;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AghAtendimentoPacientes;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MpmAltaMotivo;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.model.MpmCidAtendimento;
import br.gov.mec.aghu.model.MpmMotivoAltaMedica;
import br.gov.mec.aghu.model.MpmSolicitacaoConsultoria;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.business.ManterAltaOtrProcedimentoRN.ManterAltaOtrProcedimentoRNExceptionCode;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaMotivoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaSumarioDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmCidAtendimentoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmMotivoAltaMedicaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmSolicitacaoConsultoriaDAO;
import br.gov.mec.aghu.prescricaomedica.vo.MpmAltaSumarioVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.DateValidator;

@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.ExcessiveClassLength"})
@Stateless
public class ManterAltaSumarioRN extends BaseBusiness {

	@EJB
	private ManterAltaEvolucaoON manterAltaEvolucaoON;
	
	@EJB
	private ManterAltaCirgRealizadaON manterAltaCirgRealizadaON;
	
	@EJB
	private ManterObitoNecropsiaON manterObitoNecropsiaON;
	
	@EJB
	private ManterAltaDiagSecundarioON manterAltaDiagSecundarioON;
	
	@EJB
	private ManterObtCausaAntecedenteON manterObtCausaAntecedenteON;
	
	@EJB
	private ManterAltaReinternacaoON manterAltaReinternacaoON;
	
	@EJB
	private ManterAltaConsultoriaON manterAltaConsultoriaON;
	
	@EJB
	private ManterAltaMotivoON manterAltaMotivoON;
	
	@EJB
	private ManterAltaItemPedidoExameON manterAltaItemPedidoExameON;
	
	@EJB
	private ManterAltaComplFarmacoON manterAltaComplFarmacoON;
	
	@EJB
	private ManterAltaOtrProcedimentoON manterAltaOtrProcedimentoON;
	
	@EJB
	private ManterObtGravidezAnteriorON manterObtGravidezAnteriorON;
	
	@EJB
	private ManterAltaEstadoPacienteON manterAltaEstadoPacienteON;
	
	@EJB
	private ManterAltaPrincFarmacoON manterAltaPrincFarmacoON;
	
	@EJB
	private ManterAltaRecomendacaoON manterAltaRecomendacaoON;
	
	@EJB
	private ManterObtCausaDiretaON manterObtCausaDiretaON;
	
	@EJB
	private ManterAltaPlanoON manterAltaPlanoON;
	
	@EJB
	private ManterObtOutraCausaON manterObtOutraCausaON;
	
	@EJB
	private ManterAltaDiagMtvoInternacaoON manterAltaDiagMtvoInternacaoON;
	
	@EJB
	private ManterAltaOutraEquipeSumrON manterAltaOutraEquipeSumrON;
	
	@EJB
	private ManterAltaPedidoExameON manterAltaPedidoExameON;
	
	@EJB
	private ManterAltaDiagPrincipalON manterAltaDiagPrincipalON;
	
	@EJB
	private MpmAltaPrincReceitasON mpmAltaPrincReceitasON;
	
	private static final Log LOG = LogFactory.getLog(ManterAltaSumarioRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private MpmAltaSumarioDAO mpmAltaSumarioDAO;
	
	@Inject
	private MpmMotivoAltaMedicaDAO mpmMotivoAltaMedicaDAO;
	
	@Inject
	private MpmAltaMotivoDAO mpmAltaMotivoDAO;
	
	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private MpmCidAtendimentoDAO mpmCidAtendimentoDAO;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private MpmSolicitacaoConsultoriaDAO mpmSolicitacaoConsultoriaDAO;

	//private static final Log log = Logging.getLog(ManterAltaSumarioRN.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 4009956799262289412L;

	public enum ManterAltaSumarioRNExceptionCode implements
	BusinessExceptionCode {

		ERRO_INSERIR_ALTA_SUMARIO, ERRO_ATUALIZAR_ALTA_SUMARIO, //
		ERRO_ADIANTAR_DATA_ALTA_N_DIAS, //
		ERRO_ADIANTAR_DATA_ALTA_N_HORAS, //
		ATENDIMENTO_NAO_ENCONTRADO, //
		LOCAL_PACIENTE_NAO_ENCONTRADO, //
		MPM_02709, //
		MPM_02711, //
		MPM_02712, //
		MPM_02712_1, //
		MPM_02713, //
		MPM_03009, //
		MPM_03010, //
		MPM_03010_1, //
		MPM_03011, //
		MPM_03012, //
		MPM_03711, //
		MPM_03735, //
		RAP_00175, MPM_02642, MPM_00153, MPM_00355, ERRO_ALTERAR_REGISTRO_OBITO_CAUSA_ANTECEDENTE, MPM_03322;

		public void throwException(Object... params)
		throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}

		public void throwException(Throwable cause, Object... params)
		throws ApplicationBusinessException {
			// Tratamento adicional para não esconder a excecao de negocio
			// original
			if (cause instanceof ApplicationBusinessException) {
				throw (ApplicationBusinessException) cause;
			}
			throw new ApplicationBusinessException(this, cause, params);
		}

	}

	/**
	 * Insere objeto MpmAltaSumario.
	 * 
	 * @param altaSumario
	 * @throws ApplicationBusinessException
	 */
	public void inserirAltaSumario(MpmAltaSumario altaSumario)
	throws ApplicationBusinessException {

		try {
			this.preInserirAltaSumario(altaSumario);
			this.getMpmAltaSumarioDAO().persistir(altaSumario);

			this.posInserirAltaSumario(altaSumario);

		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			ManterAltaSumarioRNExceptionCode.ERRO_INSERIR_ALTA_SUMARIO.throwException(e);
		}

	}

	/**
	 * MPMP_ATU_MOTIVO_ALTA_OBITO
	 * Insere objeto MpmAltaMotivos para sumário de óbito, caso tenha cadastrado algum de tipo óbito 
	 * na tabela mpm_motivo_alta_medicas.
	 * 
	 * @param altaSumario
	 * @throws ApplicationBusinessException
	 */
	private void posInserirAltaSumario(MpmAltaSumario altaSumario) {
		if (altaSumario.getTipo() == DominioIndTipoAltaSumarios.OBT) {
			List<MpmMotivoAltaMedica> listMotivoAltaMedica = getMpmMotivoAltaMedicaDAO().listMotivoAltaMedicaObito();
			if (listMotivoAltaMedica != null && !listMotivoAltaMedica.isEmpty()) {
				MpmMotivoAltaMedica motivoAltaMedica = listMotivoAltaMedica.get(0);
				MpmAltaMotivo pojo = new MpmAltaMotivo();

				pojo.setId(altaSumario.getId());
				pojo.setAltaSumario(altaSumario);
				pojo.setMotivoAltaMedicas(motivoAltaMedica);
				pojo.setDescMotivo(motivoAltaMedica.getDescricao());

				getMpmAltaMotivoDAO().persistir(pojo);
				getMpmAltaMotivoDAO().flush();
			}
		}
	}

	protected MpmAltaMotivoDAO getMpmAltaMotivoDAO() {
		return mpmAltaMotivoDAO; 
	}

	protected MpmMotivoAltaMedicaDAO getMpmMotivoAltaMedicaDAO() {
		return mpmMotivoAltaMedicaDAO;
	}

	/**
	 * @ORADB Trigger MPMT_ASU_BRI
	 * 
	 * EXECUTA ANTES DE FAZER O INSERT DO OBJETO.
	 * 
	 * @param altaSumario
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	protected void preInserirAltaSumario(MpmAltaSumario altaSumario) throws ApplicationBusinessException {

		MpmAltaSumario altaSumarioOriginal = null;

		if (altaSumario.getId() != null) {

			altaSumarioOriginal = this.getMpmAltaSumarioDAO().obterAltaSumarioPeloId(altaSumario.getId().getApaAtdSeq(), altaSumario.getId().getApaSeq(), altaSumario.getId().getSeqp());

		}

		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		altaSumario.setCriadoEm(new Date());
		altaSumario.setServidor(servidorLogado);

		if (!DominioIndTipoAltaSumarios.TRF.equals(altaSumario.getTipo())) {

			// testa data de alta com o fim do atendimento
			this.verificarAltaFutura(altaSumario.getAtendimento().getSeq(),
					altaSumario.getDthrAlta(), altaSumario.getTipo());

			// Se o agh.atendimentos.ind_pac_atendimento = 'N' a
			// dthr_alta do mpm_alta_sumarios não pode ser maior
			// que o agh.atendimentos.dthr_fim
			if (altaSumario.getDthrAlta() != null) {

				Date dataAltaAntiga = altaSumario.getDthrAlta();

				if (altaSumarioOriginal != null && altaSumarioOriginal.getDthrAlta() != null) {

					dataAltaAntiga = altaSumarioOriginal.getDthrAlta();

				}

				this.verificarDataAlta(altaSumario.getAtendimento().getSeq(),
						altaSumario.getDthrAlta(), dataAltaAntiga,
						DominioOperacoesJournal.INS, altaSumario.getTipo());

			}

		} else {

			// consite a data da alta quando tipo = transferencia
			this.verificarDataTRF(altaSumario.getAtendimento().getSeq(),
					altaSumario.getDthrAlta());

		}

		altaSumario.setServidorAltera(this.validaServidorLogado());

		if (altaSumario.getTransfConcluida() == null) {
			altaSumario.setTransfConcluida(Boolean.FALSE);
		}

		if (DominioIndConcluido.S.equals(altaSumario.getConcluido())) {

			altaSumario.setServidorAltera(servidorLogado);
			altaSumario.setDthrElaboracaoAlta(new Date());

		} else {

			altaSumario.setServidorAltera(null);
			altaSumario.setDthrElaboracaoAlta(null);

		}

		if (altaSumario.getTransfConcluida() != null
				&& altaSumario.getTransfConcluida().booleanValue()) {

			altaSumario.setServidorAltera(servidorLogado);
			altaSumario.setDthrElaboracaoTransf(new Date());

		} else {

			altaSumario.setServidorAltera(null);
			altaSumario.setDthrElaboracaoTransf(null);

		}

	}

	/**
	 * @ORADB Trigger MPMT_ASU_BRU
	 * 
	 * @param altaSumario
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	public void atualizarAltaSumario(MpmAltaSumario altaSumario, String nomeMicrocomputador) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		MpmAltaSumarioVO altaSumarioOriginal = this.getMpmAltaSumarioDAO().obterAltaSumarioOriginal(altaSumario);

		AghAtendimentos atendimento = altaSumario.getAtendimento();

		DominioIndConcluido novoIndConcluido = altaSumario.getConcluido();
		DominioIndConcluido antigoIndConcluido = altaSumarioOriginal.getConcluido();

		LOG.info("atendimento.getSeq()= "+atendimento.getSeq());
		// atualiza indicadores do atendimento
		this.atualizarAtendimento(atendimento.getSeq(), novoIndConcluido, antigoIndConcluido, nomeMicrocomputador);

		if (!DominioIndTipoAltaSumarios.TRF.equals(altaSumario.getTipo())) {
			/*
			 * testa data de alta com o fim do atendimento Verifica
			 * data/hora alta futura
			 */
			if (CoreUtil.modificados(altaSumarioOriginal.getDthrAlta(),altaSumario.getDthrAltaAux()) && altaSumario.getDthrAltaAux() != null) {
				this.verificarAltaFutura(atendimento.getSeq(),
						altaSumario.getDthrAltaAux(), altaSumario.getTipo());

				/*
				 * Se o agh.atendimentos.ind_pac_atendimento = 'N' a
				 * dthr_alta do mpm_alta_sumarios não pode ser maior que o
				 * agh.atendimentos.dthr_fim
				 */
				this.verificarDataAlta(atendimento.getSeq(),
						altaSumario.getDthrAltaAux(),
						altaSumarioOriginal.getDthrAlta(),
						DominioOperacoesJournal.UPD, altaSumario.getTipo());
			}

		} else {

			// consite a data da alta quando tipo = transferencia
			this.verificarDataTRF(atendimento.getSeq(), altaSumario.getDthrAltaAux());

		}

		altaSumario.setServidorAltera(this.validaServidorLogado());

		if (CoreUtil.modificados(novoIndConcluido, antigoIndConcluido)) {
			DominioSituacao novaSituacao = altaSumario.getSituacao();

			if (DominioIndConcluido.S.equals(novoIndConcluido)) {
				if (DominioSituacao.I.equals(novaSituacao)) {
					ManterAltaSumarioRNExceptionCode.MPM_03735.throwException();
				}
				if (altaSumario.getServidorValida() == null) {
					altaSumario.setServidorValida(servidorLogado);
				}
				altaSumario.setDthrElaboracaoAlta(new Date());
			} else {
				altaSumario.setServidorValida(null);
				altaSumario.setDthrElaboracaoAlta(null);
			}
		}

		if (CoreUtil.modificados(altaSumario.getTransfConcluida(), altaSumarioOriginal.getTransfConcluida())) {
			if (altaSumario.getTransfConcluida() != null && altaSumario.getTransfConcluida()) {
				altaSumario.setServidorValida(servidorLogado);
				altaSumario.setDthrElaboracaoTransf(new Date());
			} else {
				altaSumario.setServidorValida(null);
				altaSumario.setDthrElaboracaoTransf(null);
			}
		}

		if (altaSumario.getDthrAltaAux()!=null) {
			altaSumario.setDthrAlta(altaSumario.getDthrAltaAux());
		}
		
		try {
			this.getMpmAltaSumarioDAO().merge(altaSumario);
			this.getMpmAltaSumarioDAO().flush();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new ApplicationBusinessException(ManterAltaSumarioRNExceptionCode.ERRO_ATUALIZAR_ALTA_SUMARIO);
		}
		
	}

	/**
	 * @ORADB Procedure MPMK_ASU_RN.RN_ASUP_ATU_ATEND.
	 * 
	 * Operação: UPD Descrição: quando for atualizada a data da alta e o
	 * atendimento estiver com ind_pac_atendimento = 'N', atualizar este
	 * atendimento colocando ind_sit_sumario_alta = 'P' (pendente) e
	 * ctrl_sumv_alta_pendente = 'E' (elaborado e não entregue)
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void atualizarAtendimento(Integer novoAtdSeq,
			DominioIndConcluido novoIndConcluido,
			DominioIndConcluido antigoIndConcluido, String nomeMicrocomputador) throws ApplicationBusinessException {
		DominioIndConcluido novoInd = novoIndConcluido == null ? DominioIndConcluido.N
				: novoIndConcluido;
		DominioIndConcluido antigoInd = antigoIndConcluido == null ? DominioIndConcluido.N
				: antigoIndConcluido;
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();

		if (novoAtdSeq != null && DominioIndConcluido.S.equals(novoInd)
				&& DominioIndConcluido.N.equals(antigoInd)) {

			List<DominioOrigemAtendimento> listaOrigemAtendimento = Arrays.asList(
			DominioOrigemAtendimento.I
			,DominioOrigemAtendimento.U
			,DominioOrigemAtendimento.H
			,DominioOrigemAtendimento.N);

			AghAtendimentos atendimento = getAghuFacade().obterAghAtendimentoPorChavePrimaria(novoAtdSeq);

			if (DominioPacAtendimento.N.equals(atendimento.getIndPacAtendimento())
					&& listaOrigemAtendimento.contains(atendimento.getOrigem())) {
				atendimento.setIndSitSumarioAlta(DominioSituacaoSumarioAlta.P);
				atendimento.setCtrlSumrAltaPendente(DominioControleSumarioAltaPendente.E);

				try {

					AghAtendimentos atendimentoOld = getAghuFacade().obterAtendimentoOriginal(novoAtdSeq);
					this.getPacienteFacade().atualizarAtendimento(atendimento, atendimentoOld, nomeMicrocomputador, servidorLogado, new Date());

				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
					ManterAltaSumarioRNExceptionCode.MPM_02709.throwException(e);
				}
			}
		}
	}

	/**
	 * @ORADB Procedure MPMK_ASU_RN.RN_ASUP_VER_ALTA_FUT.
	 * 
	 * Operação: INS, UPD Descrição: quando a data da alta for informada, só
	 * deve permitir que seja futura para paciente internado em unidade
	 * funcional com limite de dias de adiantamento > 0 (pode utilizar como
	 * modelo a regra rn_salp_ver_alta_fut).
	 * 
	 * @param novoAtdSeq
	 *            Seq de AghAtendimento.
	 * @param novaDataAlta
	 *            DataAlta de alta sumário.
	 * @param novoTipo
	 *            Tipo de alta sumário.
	 * @throws ApplicationBusinessException
	 */
	public void verificarAltaFutura(Integer novoAtdSeq, Date novaDataAlta,
			DominioIndTipoAltaSumarios novoTipo) throws ApplicationBusinessException {
		if (DominioIndTipoAltaSumarios.OBT.equals(novoTipo)
				&& DateValidator.validaDataMaiorQueAtual(novaDataAlta)) {
			ManterAltaSumarioRNExceptionCode.MPM_03711.throwException();
		}

		AghAtendimentos atendimento = getAghuFacade()
		.obterAghAtendimentoPorChavePrimaria(novoAtdSeq);

		if (atendimento != null) {
			AghUnidadesFuncionais unidadeFuncional = atendimento
			.getUnidadeFuncional();
			Short nroUnidTempoPmeAdiantadas = unidadeFuncional
			.getNroUnidTempoPmeAdiantadas();
			DominioUnidTempo indUnidTempoPmeAdiantada = unidadeFuncional
			.getIndUnidTempoPmeAdiantada();

			if (nroUnidTempoPmeAdiantadas != null
					&& nroUnidTempoPmeAdiantadas > 0
					&& indUnidTempoPmeAdiantada != null) {
				//DateTime dthrAtual = new DateTime();
				//DateTime dthrNovaDataAlta = new DateTime(novaDataAlta);

				switch (indUnidTempoPmeAdiantada) {
				case D:
					//Days days = Days.daysBetween(dthrAtual, dthrNovaDataAlta);
					//int intervaloDias = days.getDays() + 1;
					int intervaloDias = DateUtil.obterQtdDiasEntreDuasDatas(new Date(), novaDataAlta) + 1;

					// if (TRUNC ( p_new_dthr_alta - SYSDATE ) + 1) >
					// v_nro_unid_tempo_sal_adiant) {
					if (intervaloDias > nroUnidTempoPmeAdiantadas.intValue()) {
						// raise_application_error (-20000, 'Só é possível
						// adiantar a alta com até ' ||
						// TO_CHAR(v_nro_unid_tempo_sal_adiant) || ' dias de
						// antecedência');
						ManterAltaSumarioRNExceptionCode.ERRO_ADIANTAR_DATA_ALTA_N_DIAS
						.throwException(nroUnidTempoPmeAdiantadas
								.intValue());
					}
					break;
				case H:
					//Hours hours = Hours.hoursBetween(dthrAtual,	dthrNovaDataAlta);
					//int intervaloHoras = hours.getHours();
					int intervaloHoras = DateUtil.obterQtdHorasEntreDuasDatas(new Date(), novaDataAlta);

					// if ( p_new_dthr_alta - SYSDATE ) * 24 >
					// v_nro_unid_tempo_sal_adiant) {
					if (intervaloHoras > nroUnidTempoPmeAdiantadas.intValue()) {
						// raise_application_error (-20000,'Só é possível
						// adiantar a alta com até ' ||
						// TO_CHAR(v_nro_unid_tempo_sal_adiant) || ' horas de
						// antecedência');
						ManterAltaSumarioRNExceptionCode.ERRO_ADIANTAR_DATA_ALTA_N_HORAS
						.throwException(nroUnidTempoPmeAdiantadas
								.intValue());
					}
					break;
				default:
					break;
				}
			}

			if ((nroUnidTempoPmeAdiantadas == null || nroUnidTempoPmeAdiantadas
					.intValue() == 0)
					&& DateValidator.validaDataMaiorQueAtual(novaDataAlta)) {
				ManterAltaSumarioRNExceptionCode.MPM_02711.throwException();
			}
		}
	}

	/*
	 * ORADB Procedure MPMK_ASU_RN.RN_ASUP_VER_DT_ALTA.
	 * 
	 * Operação: INS,UPD Descrição: Quando a data da alta for informada e o
	 * paciente não estiver mais em atendimento (ind_pac_atendimento = 'N') esta
	 * deve ser >= a dthr_fim - 1 e <= dthr_fim . Se o paciente estiver em
	 * atendimento a data da alta deve ser > = a sysdate.
	 */
	public void verificarDataAlta(Integer novoAtdSeq, Date novaDataAlta,
			Date antigaDataAlta, DominioOperacoesJournal operacao, DominioIndTipoAltaSumarios tipo)
	throws ApplicationBusinessException {
		final Integer MINUTOS_DIA = 1440;
		Date today = new Date();

		List<DominioOperacoesJournal> operacoesValidas = Arrays.asList(
				DominioOperacoesJournal.INS, DominioOperacoesJournal.UPD);

		boolean executaValidacao = operacoesValidas.contains(operacao)
		&& novaDataAlta != null && novoAtdSeq != null;
		if (executaValidacao && DominioOperacoesJournal.UPD.equals(operacao)) {
			executaValidacao = CoreUtil.modificados(antigaDataAlta,
					novaDataAlta);
		}

		if (executaValidacao) {
			AghAtendimentos atendimento = getAghuFacade()
			.obterAghAtendimentoPorChavePrimaria(novoAtdSeq);

			ManterAltaSumarioRNExceptionCode errorCode = null;

			if (!DominioPacAtendimento.N.equals(atendimento
					.getIndPacAtendimento())) {
				
				long diff = today.getTime() - novaDataAlta.getTime();
				long difMinutos = (diff / (1000 * 60));
				
				// if trunc(p_new_dthr_alta) < trunc(sysdate - 1) then
				if (difMinutos > MINUTOS_DIA) {
					// MPM-03010 = Data da alta/óbito deve ser igual a data
					// corrente ou um dia antes
					// MPM-02712 = Data da alta/óbito deve ser igual a data
					// corrente ou um dia antes
					// raise_application_error(-20000,'MPM-03010');
					
					
					if (DominioIndTipoAltaSumarios.OBT.equals(tipo)) {
						errorCode = this.getBusinessExceptionCode(operacao,
								ManterAltaSumarioRNExceptionCode.MPM_03010_1, // INSERT
								ManterAltaSumarioRNExceptionCode.MPM_02712_1, // UPDATE
								null);
					} else {
						errorCode = this.getBusinessExceptionCode(operacao,
								ManterAltaSumarioRNExceptionCode.MPM_03010, // INSERT
								ManterAltaSumarioRNExceptionCode.MPM_02712, // UPDATE
								null);
					}
					
					errorCode.throwException();
				}
			} else if (atendimento.getDthrFim() != null) {
				Date dthrFim = atendimento.getDthrFim();

				SimpleDateFormat sdfHora = new SimpleDateFormat(
				"dd/MM/yyyy HH:mm");
				String strDthrFim = sdfHora.format(atendimento.getDthrFim());
				
				long diff = dthrFim.getTime() - novaDataAlta.getTime();
				long difMinutos = (diff / (1000 * 60));

				// IF TRUNC(p_new_dthr_alta) < TRUNC(v_dthr_fim - 1) THEN
				if (difMinutos > MINUTOS_DIA){
					// MPM-03011 = Data da alta/óbito deve ser igual a data do
					// fim do atendimento (#1) ou menos um dia
					// MPM-02713 = Data da alta/óbito deve ser igual a data do
					// fim do atendimento (#1) ou um dia antes.
					// raise_application_error(-20000,'MPM-03011 #1'||
					// TO_CHAR(v_dthr_fim,'dd/mm/yyyy hh24:mi'));
					errorCode = this.getBusinessExceptionCode(operacao,
							ManterAltaSumarioRNExceptionCode.MPM_03011, // INSERT
							ManterAltaSumarioRNExceptionCode.MPM_02713, // UPDATE
							null);
					errorCode.throwException(strDthrFim);
					// IF p_new_dthr_alta > v_dthr_fim THEN
				} else if (novaDataAlta.after(dthrFim)) {
					// MPM-03012 = Data da alta/óbito não pode ser maior que a
					// data do fim do atendimento --> #1.
					// MPM-03009 = Data da alta/óbito não pode ser maior que a
					// data do fim do atendimento --> #1.
					// raise_application_error(-20000,'MPM-03012 #1'||
					// TO_CHAR(v_dthr_fim,'dd/mm/yyyy hh24:mi'));
					errorCode = this.getBusinessExceptionCode(operacao,
							ManterAltaSumarioRNExceptionCode.MPM_03012, // INSERT
							ManterAltaSumarioRNExceptionCode.MPM_03009, // UPDATE
							null);
					errorCode.throwException(strDthrFim);
				}
			}
		}
	}

	private ManterAltaSumarioRNExceptionCode getBusinessExceptionCode(
			DominioOperacoesJournal operacao,
			ManterAltaSumarioRNExceptionCode codeInsert,
			ManterAltaSumarioRNExceptionCode codeUpdate,
			ManterAltaSumarioRNExceptionCode codeDelete) {
		ManterAltaSumarioRNExceptionCode codeRetorno = null;
		switch (operacao) {
		case INS:
			codeRetorno = codeInsert;
			break;
		case UPD:
			codeRetorno = codeUpdate;
			break;
		case DEL:
			codeRetorno = codeDelete;
			break;
		default:
			break;
		}
		return codeRetorno;
	}

	/**
	 * @ORADB FUNCTION MPMC_VER_DT_INI_ATD2 Recupera Data Internação
	 * 
	 * @param seq
	 * @return
	 */
	public Date obterDataInternacao2(Integer seq) throws ApplicationBusinessException {
		AghAtendimentos atendimento = this.getAghuFacade()
				.obterAghAtendimentoPorChavePrimaria(seq);
		Integer int_seq = null;
		Integer atu_seq = null;
		Integer hod_seq = null;

		if (atendimento == null) {
			throw new ApplicationBusinessException(
					ManterAltaSumarioRNExceptionCode.ATENDIMENTO_NAO_ENCONTRADO);
		} else {
			if (atendimento.getOrigem() != null
					&& atendimento.getOrigem().equals(
							DominioOrigemAtendimento.N)) {
				return atendimento.getDthrInicio();
			} else {
				if (atendimento.getAtendimentoUrgencia() != null) {
					atu_seq = atendimento.getAtendimentoUrgencia().getSeq();
				}
				if (atendimento.getInternacao() != null) {
					int_seq = atendimento.getInternacao().getSeq();
				}
				if (atendimento.getHospitalDia() != null) {
					hod_seq = atendimento.getHospitalDia().getSeq();
				}
				return this.obterDataInternacao(int_seq, atu_seq, hod_seq);
			}
		}
	}

	/**
	 * @ORADB FUNCTION MPMC_VER_DT_INI_ATD Recupera Data Internação
	 * 
	 * @return
	 */
	public Date obterDataInternacao(Integer int_seq, Integer atu_seq,
			Integer hod_seq) throws ApplicationBusinessException {
		Date dataInternacao = null;

		if (atu_seq != null) {
			dataInternacao = this.getInternacaoFacade()
			.obterDataAtendimento(atu_seq);
		} else if (int_seq != null) {
			dataInternacao = this.getInternacaoFacade().obterDataInternacao(
					int_seq);
		} else if (hod_seq != null) {
			dataInternacao = this.getInternacaoFacade().obterDataInternacao(
					hod_seq);
		}

		return dataInternacao;
	}

	/**
	 * @ORADB FUNCTION MPMC_LOCAL_PAC_ATD Retorna local atendimento
	 * 
	 * @param apaAtdSeq
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public String obterLocalPaciente(Integer apaAtdSeq)
	throws ApplicationBusinessException {
		String local = null;
		AghAtendimentos atendimento = this.getAghuFacade()
		.obterAghAtendimentoPorChavePrimaria(apaAtdSeq);

		if (atendimento != null) {
			if (atendimento.getLeito() != null
					&& atendimento.getLeito().getLeitoID() != null) {
				local = "L:" + atendimento.getLeito().getLeitoID();
			} else if (atendimento.getQuarto() != null
					&& atendimento.getQuarto().getDescricao() != null) {
				local = "Q:" + atendimento.getQuarto().getDescricao().toString();
			} else if (atendimento.getUnidadeFuncional() != null) {
				local = atendimento.getUnidadeFuncional()
				.getLPADAndarAlaDescricao();
			}
		} else {
			throw new ApplicationBusinessException(
					ManterAltaSumarioRNExceptionCode.LOCAL_PACIENTE_NAO_ENCONTRADO);
		}
		return local;
	}

	/**
	 * @ORADB Procedure MPMK_ASU_RN.RN_ASUP_VER_DT_TRF
	 * 
	 * bsoliveira - 19/10/2010
	 * 
	 * @param {Integer} novoAtdSeq
	 * @param {Date} novoDtHrTransf
	 * @throws ApplicationBusinessException
	 */
	public void verificarDataTRF(Integer novoAtdSeq, Date novoDtHrTransf)
	throws ApplicationBusinessException {

		Date dtHrInicioAtd = this.obterDataInternacao2(novoAtdSeq);

		if (DateUtil.validaDataTruncadaMaior(dtHrInicioAtd, novoDtHrTransf)) {

			throw new ApplicationBusinessException(
					ManterAltaSumarioRNExceptionCode.MPM_03322);

		}

	}

	/**
	 * @ORADB Procedure MPMK_OPC_RN.RN_OPCP_VER_ALTA_SUMARIO. 
	 * @ORADB Procedure MPMK_OES_RN.RN_OESP_VER_ALTA_SUM. 
	 * @ORADB Procedure MPMK_ARC_RN.RN_ARCP_VER_SUM_ATIV. 
	 * @ORADB Procedure MPMK_ADP_RN.RN_ADPP_VER_ALTA_SUM. 
	 * @ORADB Procedure MPMK_PFA_RN.RN_PFAP_VER_ALTA_SUM. 
	 * @ORADB Procedure mpmk_amt_rn.rn_amtp_ver_alta_sum 
	 * @ORADB Procedure mpmk_aev_rn.rn_aevp_ver_alta_sum 
	 * @ORADB Procedure mpmk_aex_rn.rn_aexp_ver_sum_ativ 
	 * @ORADB Procedure mpmk_aip_rn.rn_aipp_ver_sum_ativ 
	 * @ORADB Procedure mpmk_alr_rn.rn_alrp_ver_sum_ativ 
	 * @ORADB Procedure mpmk_aep_rn.rn_aepp_ver_sum_ativ 
	 * @ORADB Procedure mpmk_cfa_rn.rn_cfap_ver_alta_sum 
	 * @ORADB Procedure mpmk_dmi_rn.rn_dmip_ver_alta_sum 
	 * @ORADB Procedure mpmk_ads_rn.rn_adsp_ver_alta_sum
	 * @ORADB Procedure mpmk_amt_rn.rn_amtp_ver_alta_sum
	 * @ORADB Procedure mpmk_arc_rn.rn_arcp_ver_sum_ativ
	 * 
	 * 
	 * Operação: INS, UPD e DEL Descrição: Só pode incluir, alterar e deletar
	 * registros quando o pai alta sumário estiver ativo
	 * 
	 * bsoliveira - 27/10/2010
	 * 
	 * @param novoAsuApaAtdSeq
	 *            asuApaAtdSeq de MpmAltaOutraEquipeSumr.
	 * @param novoAsuApaSeq
	 *            asuApaSeq de MpmAltaOutraEquipeSumr.
	 * @param novoAsuSeqp
	 *            asuSeqp de MpmAltaOutraEquipeSumr.
	 * @throws ApplicationBusinessException
	 */
	public void verificarAltaSumarioAtivo(MpmAltaSumario altaSumario) throws ApplicationBusinessException {

		if (altaSumario != null
				&& !DominioSituacao.A.equals(altaSumario.getSituacao())) {

			ManterAltaSumarioRNExceptionCode.MPM_03735.throwException();

		}

	}

	/**
	 * @ORADB Procedure MPMK_ARC_RN.RN_ARCP_VER_TIPO_ALT.<br>
	 * @ORADB Procedure MPMK_APL_RN.RN_APLP_VER_TIPO_ALT.<br>
	 * Mensagem:<br> 
	 * MPM-02832:Registro de alta para tipo óbito não pode ser alterado pelo usuário.<br>
	 * MPM-02856:Sumário de alta está inativo.<br>
	 * 
	 * Operação: INS, UPD Descrição: Só podem ser inclídos registros<br> 
	 * se o pai alta sumário NÃO for do tipo ÓBITO.<br>
	 * 
	 * bsoliveira - 29/10/2010
	 * 
	 * @param {MpmAltaSumarioId} novoAltaSumarioId
	 * @throws ApplicationBusinessException
	 */
	public void verificarTipoAlteracao(MpmAltaSumarioId novoAltaSumarioId) throws ApplicationBusinessException {
		MpmAltaSumarioDAO altaSumarioDAO = this.getMpmAltaSumarioDAO();
		MpmAltaSumario altaSumario = altaSumarioDAO.obterAltaSumarioPeloIdESituacao(novoAltaSumarioId, DominioSituacao.A);

		if (altaSumario != null && DominioIndTipoAltaSumarios.OBT.equals(altaSumario.getTipo())) {
			ManterAltaSumarioRNExceptionCode.MPM_02642.throwException();
		} else if (altaSumario == null) {
			ManterAltaSumarioRNExceptionCode.MPM_03735.throwException();
		}
	}

	/**
	 * @ORADB Procedure MPMP_VER_CID_ATENDIM.
	 * 
	 * VERIFICA INTEGRIDADE COM MPM_CID_ATENDIMENTOS
	 * 
	 * bsoliveira - 29/10/2010
	 * 
	 * @param {Integer} novoCiaSeq
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificarCidAtendimento(Integer novoCiaSeq)
	throws ApplicationBusinessException{

		if (novoCiaSeq != null) {

			MpmCidAtendimentoDAO cidAtendimentoDAO = this.getCidAtendimentoDAO();
			MpmCidAtendimento cidAtendimento = cidAtendimentoDAO
			.obterCidAtendimentoPeloId(novoCiaSeq);

			if (cidAtendimento == null) {

				ManterAltaSumarioRNExceptionCode.MPM_00153.throwException();

			}

		}

	}

	/**
	 * @ORADB FUNCTION MPMC_EDIT_NOME_SERV Retorna nome do servidor já editado
	 * 
	 * @param vinCodigo
	 * @param matricula
	 * @return
	 */
	public String obtemNomeServidorEditado(Short vinCodigo, Integer matricula) {
		Object[] obj = this.getRegistroColaboradorFacade().obtemDadosServidor(vinCodigo, matricula);
		String nomeEditado = null;
		String nomePessoa = null;
		if (obj != null) {
			if (obj[0] != null) {
				nomePessoa = (String) obj[0];
			}
			if (obj[1] != null && ((DominioSexo) obj[1]).equals(DominioSexo.M)) {
				if (obj[2] != null) {
					nomeEditado = (String) obj[2] + " " + nomePessoa;
				} else if (obj[4] != null) {
					nomeEditado = (String) obj[4] + " " + nomePessoa;
				} else {
					nomeEditado = nomePessoa;
				}
			} else if (obj[1] != null && ((DominioSexo) obj[1]).equals(DominioSexo.F)) {
				if (obj[3] != null) {
					nomeEditado = (String) obj[3] + " " + nomePessoa;
				} else if (obj[5] != null) {
					nomeEditado = (String) obj[5] + " " + nomePessoa;
				} else {
					nomeEditado = nomePessoa;
				}
			}
			if (nomeEditado != null) {
				return WordUtils.capitalizeFully(nomeEditado).replaceAll(" Da ", " da ").replaceAll(" De ", " de ").replaceAll(" Do ", " do ");
			} else {
				return WordUtils.capitalizeFully(nomePessoa).replaceAll(" Da ", " da ").replaceAll(" De ", " de ").replaceAll(" Do ", " do ");
			}
		}
		return null;
	}

	/**
	 * @ORADB Procedure MPMP_VER_SOLIC_CONSU.
	 * 
	 * VERIFICA INTEGRIDADE COM MPM_SOLICITACAO_CONSULTORIAS
	 * 
	 * bsoliveira - 29/10/2010
	 * 
	 * @param {Integer} novoScnSeq
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificarSolicitacaoConsultoria(Integer novoScnSeq)
	throws ApplicationBusinessException {

		if (novoScnSeq != null) {

			MpmSolicitacaoConsultoriaDAO solicitacaoConsultoriaDAO = this.getSolicitacaoConsultoriaDAO();
			MpmSolicitacaoConsultoria solicitacaoConsultoria = solicitacaoConsultoriaDAO
			.obterSolicitacaoConsultoriaPeloSeq(novoScnSeq);

			if (solicitacaoConsultoria == null) {

				ManterAltaSumarioRNExceptionCode.MPM_00355.throwException();

			}

		}

	}

	/**
	 * @ORADB Procedure MPMK_OPC_RN.RN_OPCP_VER_DTHR_PRO. @ORADB Procedure
	 * MPMK_ACN_RN.RN_ACNP_VER_DTHR_CON.
	 * mpmk_acn_rn.rn_acnp_ver_dthr_con
	 * 
	 * bsoliveira - 29/10/2010
	 * 
	 * @param {Integer} novoAsuApaAtdSeq
	 * @param {Date} novoDthr
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificarDtHr(Integer novoAsuApaAtdSeq, Date novoDthrOut)
	throws ApplicationBusinessException {

		Date dtHrInicio = this.obterDataInternacao2(novoAsuApaAtdSeq);
		Date dtHrFim = new Date();

		AghAtendimentos atendimento = this.getAghuFacade()
		.obterAtendimentoPeloSeq(novoAsuApaAtdSeq);
		if (atendimento != null) {

			if (atendimento.getDthrFim() != null) {

				dtHrFim = atendimento.getDthrFim();

			}

			if (!DateUtil.entreTruncado(novoDthrOut, dtHrInicio, dtHrFim)) {

				if (atendimento.getDthrFim() != null) {
					ManterAltaOtrProcedimentoRNExceptionCode.MPM_VALIDA_DT_OUTROS_PROCED 
					.throwException();
				} else {
					ManterAltaOtrProcedimentoRNExceptionCode.MPM_02874
					.throwException();
				}

			}

		} else {

			ManterAltaOtrProcedimentoRNExceptionCode.MPM_02875.throwException();

		}

	}

	/**
	 * @ORADB Procedure RN_DMIP_VER_SITUACAO
	 * @ORADB Procedure mpmk_ads_rn.rn_adsp_ver_situacao
	 * @ORADB Procedure mpmk_pfa_rn.rn_pfap_ver_situacao
	 * @ORADB Procedure mpmk_acn_rn.rn_acnp_ver_situacao
	 * 
	 * Se a nova situação (IND_SITUACAO) for igual a “A” e a antiga situação for
	 * igual a “I”. Não deve permitir ativar o ind_situacao. Deve ser passado
	 * por parâmetro o código de erro a ser apresentado.
	 * 
	 * @param {DominioSituacao} novoIndSituacao
	 * @param {DominioSituacao} antigoIndSituacao
	 * @throws ApplicationBusinessException
	 */
	public void verificarSituacao(DominioSituacao novoIndSituacao,
			DominioSituacao antigoIndSituacao,
			BusinessExceptionCode msgCodigoErro) throws ApplicationBusinessException {

		if (DominioSituacao.A.equals(novoIndSituacao)
				&& DominioSituacao.I.equals(antigoIndSituacao)) {

			throw new ApplicationBusinessException(msgCodigoErro);

		}

	}

	/**
	 * @ORADB Procedure RN_DMIP_VER_IND_CARG
	 * @ORADB Procedure mpmk_pfa_rn.rn_pfap_ver_ind_carg
	 * @ORADB Procedure mpmk_acn_rn.rn_acnp_ver_ind_carg
	 * 
	 * Não deve permitir a alterar ind_carga. Se foi modificado o valor de
	 * ind_carga deve subir uma exceção com o código de erro passado por
	 * parâmetro.
	 * 
	 * @param {Boolean} novoIndCarga
	 * @param {Boolean} antigoIndCarga
	 * @param {BusinessExceptionCode} msgCodigoErro
	 * @throws ApplicationBusinessException
	 */
	public void verificarIndCarga(Boolean novoIndCarga, Boolean antigoIndCarga, BusinessExceptionCode msgCodigoErro) throws ApplicationBusinessException {

		if (CoreUtil.modificados(novoIndCarga, antigoIndCarga)) {

			throw new ApplicationBusinessException(msgCodigoErro);

		}

	}

	/**
	 * Valida se existe informações do servidor logado. Caso não exista retorna
	 * uma exceção, senão retorna dados do servidor logado.
	 * 
	 * bsoliveira - 27/10/2010
	 * 
	 * @return {RapServidores}
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	// TODO Verificar possibilidade de se remover este método
	public RapServidores validaServidorLogado() throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		if (servidorLogado == null) {
			ManterAltaSumarioRNExceptionCode.RAP_00175.throwException();
		}

		return servidorLogado;

	}	

	/**
	 * @ORADB PROCEDURE MPMP_CANCELA_SUMARIO
	 * Tem por finalidade excluir os filhos e o mpm_alta_sumario
	 * @param altaSumario
	 * @throws BaseException
	 */
	public void cancelarSumario(MpmAltaSumario altaSumario, String nomeMicrocomputador) throws BaseException {
		
		MpmAltaSumario mpmAltaSumario = this.getMpmAltaSumarioDAO().obterPorChavePrimaria(altaSumario.getId());
		
		MpmAltaSumario altaSumarioAtivo = this.getMpmAltaSumarioDAO().obterAltaSumarioAtivo(
				altaSumario.getId().getApaAtdSeq(), 
				altaSumario.getId().getApaSeq(), 
				altaSumario.getId().getSeqp()
		);

		if (altaSumarioAtivo == null) {
			ManterAltaSumarioRNExceptionCode.MPM_03735.throwException();
		}
		
		DominioIndTipoAltaSumarios tipo = altaSumario.getTipo();
		Integer apaAtdSeq = altaSumario.getId().getApaAtdSeq();
		Integer apaSeq = altaSumario.getId().getApaSeq();
		Short seqp = altaSumario.getId().getSeqp();

		//delete from mpm_alta_recomendacoes
		getManterAltaRecomendacaoON().removerAltaRecomendacao(mpmAltaSumario);

		//delete from mpm_alta_estado_pacientes
		getManterAltaEstadoPacienteON().removerAltaEstadoPaciente(mpmAltaSumario);

		//delete from mpm_alta_item_pedido_exames
		getManterAltaItemPedidoExameON().removerAltaItemPedidoExame(mpmAltaSumario);

		//delete from mpm_alta_reinternacoes
		getManterAltaReinternacaoON().removerAltaReinternacao(mpmAltaSumario);

		//delete from mpm_alta_pedido_exames
		getManterAltaPedidoExameON().removerAltaPedidoExame(mpmAltaSumario);

		//delete from mpm_alta_planos
		getManterAltaPlanoON().removerAltaPlano(mpmAltaSumario);

		//delete from mpm_alta_motivos
		getManterAltaMotivoON().removerAltaMotivo(mpmAltaSumario);

		//delete from mpm_alta_evolucoes
		getManterAltaEvolucaoON().removerAltaEvolucao(mpmAltaSumario);

		//delete from mpm_alta_compl_farmacos
		getManterAltaComplFarmacoON().removerAltaComplFarmaco(mpmAltaSumario);

		//delete from mpm_alta_princ_farmacos
		getManterAltaPrincFarmacoON().removerAltaPrincFarmaco(mpmAltaSumario);

		//delete from mpm_alta_consultorias
		getManterAltaConsultoriaON().removerAltaConsultoria(mpmAltaSumario);

		//delete from mpm_alta_otr_procedimentos
		getManterAltaOtrProcedimentoON().removerAltaOtrProcedimento(mpmAltaSumario);

		//delete from mpm_alta_cirg_realizadas
		getManterAltaCirgRealizadaON().removerAltaCirgRealizada(mpmAltaSumario);

		//delete from mpm_alta_diag_secundarios
		getManterAltaDiagSecundarioON().removerMpmAltaDiagSecundario(mpmAltaSumario);

		//delete from mpm_alta_diag_principais
		getManterAltaDiagPrincipalON().removerAltaDiagPrincipal(mpmAltaSumario);

		//delete from mpm_alta_diag_mtvo_internacoes
		getManterAltaDiagMtvoInternacaoON().removerAltaDiagMtvoInternacao(mpmAltaSumario);

		//delete from mpm_alta_outra_equipe_sumrs
		getManterAltaOutraEquipeSumrON().removerAltaOutraEquipeSumr(mpmAltaSumario);

		//delete from mpm_obt_causa_diretas
		getManterObtCausaDiretaON().removerObtCausaDireta(mpmAltaSumario);

		//delete from mpm_obt_causa_antecedentes
		getManterObtCausaAntecedenteON().removerObtCausaAntecedente(mpmAltaSumario);

		//delete from mpm_obt_outra_causas
		getManterObtOutraCausaON().removerObtOutraCausa(mpmAltaSumario);

		//delete from mpm_obt_necropsias
		getManterObitoNecropsiaON().removerObitoNecropsia(mpmAltaSumario);

		//delete from mpm_obt_gravidez_anteriores
		getManterObtGravidezAnteriorON().removerObtGravidezAnterior(mpmAltaSumario);

		//delete from mam_receituarios
		getAmbulatorioFacade().removerReceituario(mpmAltaSumario);
		
		getMpmAltaPrincReceitasON().removerAltaPrincReceitas(mpmAltaSumario);
		
//		mpmAltaSumario = this.getMpmAltaSumarioDAO().obterPorChavePrimaria(altaSumario.getId());
		//delete from mpm_alta_sumarios
		getMpmAltaSumarioDAO().remover(mpmAltaSumario);
		getMpmAltaSumarioDAO().flush();
		
		//Atualiza a versão anterior
		LOG.info("Atualizando a versão anterior do sumário...");
		atualizarVersaoAnteriorSumario(tipo, apaAtdSeq, apaSeq, seqp, nomeMicrocomputador);
		LOG.info("Atualizada a versão anterior do sumário!");
	}

	/**
	 * Atualiza a versão anterior do sumário no cancelamento da versão corrente
	 * @param apaAtdSeq
	 * @param apaSeq
	 * @param seqp
	 * @throws BaseException 
	 */
	private void atualizarVersaoAnteriorSumario(DominioIndTipoAltaSumarios tipo, Integer apaAtdSeq, Integer apaSeq, Short seqp, String nomeMicrocomputador) throws BaseException {

		Integer seqpAnterior = seqp.intValue() - 1;

		if (seqpAnterior > 0) {

			MpmAltaSumario altaSumario = getMpmAltaSumarioDAO().obterAltaSumarioPeloId(apaAtdSeq, apaSeq, seqpAnterior.shortValue());

			if (altaSumario != null) {

				if (tipo.equals(DominioIndTipoAltaSumarios.OBT)) {

					altaSumario.setSituacao(DominioSituacao.A);
					atualizarAltaSumario(altaSumario, nomeMicrocomputador);

				} else {

					altaSumario.setSituacao(DominioSituacao.A);
					altaSumario.setConcluido(DominioIndConcluido.N);
					altaSumario.setDthrFim(new Date());
					atualizarAltaSumario(altaSumario, nomeMicrocomputador);

				}

			}

		} else if (seqpAnterior == 0) {

			AghAtendimentoPacientes atendimentoPacientes = this.getAghuFacade().obterAtendimentoPaciente(apaAtdSeq, apaSeq);

			if (atendimentoPacientes != null) {

				this.getAghuFacade().removerAghAtendimentoPacientes(atendimentoPacientes, true);

			}

		}

	}
	/**
	 * #39013 - Serviço que estorna alta sumario
	 * @param seqp
	 * @param atdSeq
	 * @param apaSeq
	 * @param nomeMicrocomputador
	 * @throws ApplicationBusinessException
	 */
	public void atualizarAltaSumarioEstorno(Short seqp, Integer atdSeq, Integer apaSeq, String nomeMicrocomputador) throws ApplicationBusinessException{
		MpmAltaSumario altaSumario = this.getMpmAltaSumarioDAO().obterAltaSumarioAtivo(atdSeq, apaSeq, seqp);
		if (altaSumario != null) {
			altaSumario.setDthrAlta(null);
			altaSumario.setConcluido(DominioIndConcluido.N);
			altaSumario.setEstorno(true);
			altaSumario.setRecuperaVersao(true);
			altaSumario.setDthrEstorno(new Date());
			altaSumario.setServidorEstorno(validaServidorLogado());
			atualizarAltaSumario(altaSumario, nomeMicrocomputador);
		}
	}

	protected IInternacaoFacade getInternacaoFacade() {
		return this.internacaoFacade;
	}
	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}
	

	protected MpmCidAtendimentoDAO getCidAtendimentoDAO() {
		return mpmCidAtendimentoDAO;
	}

	protected MpmSolicitacaoConsultoriaDAO getSolicitacaoConsultoriaDAO() {
		return mpmSolicitacaoConsultoriaDAO;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	

	protected ManterAltaRecomendacaoON getManterAltaRecomendacaoON() {
		return manterAltaRecomendacaoON;
	}

	protected ManterAltaEstadoPacienteON getManterAltaEstadoPacienteON() {
		return manterAltaEstadoPacienteON;
	}

	protected ManterAltaReinternacaoON getManterAltaReinternacaoON() {
		return manterAltaReinternacaoON;
	}

	protected ManterAltaItemPedidoExameON getManterAltaItemPedidoExameON() {
		return manterAltaItemPedidoExameON;
	}

	protected ManterAltaPedidoExameON getManterAltaPedidoExameON() {
		return manterAltaPedidoExameON;
	}

	protected ManterAltaPlanoON getManterAltaPlanoON() {
		return manterAltaPlanoON;
	}

	protected ManterAltaMotivoON getManterAltaMotivoON() {
		return manterAltaMotivoON;
	}

	protected ManterAltaEvolucaoON getManterAltaEvolucaoON() {
		return manterAltaEvolucaoON;
	}

	protected ManterAltaComplFarmacoON getManterAltaComplFarmacoON() {
		return manterAltaComplFarmacoON;
	}

	protected ManterAltaPrincFarmacoON getManterAltaPrincFarmacoON() {
		return manterAltaPrincFarmacoON;
	}

	protected ManterAltaConsultoriaON getManterAltaConsultoriaON() {
		return manterAltaConsultoriaON;
	}

	protected ManterAltaOtrProcedimentoON getManterAltaOtrProcedimentoON() {
		return manterAltaOtrProcedimentoON;
	}

	protected ManterAltaCirgRealizadaON getManterAltaCirgRealizadaON() {
		return manterAltaCirgRealizadaON;
	}

	protected ManterAltaDiagSecundarioON getManterAltaDiagSecundarioON() {
		return manterAltaDiagSecundarioON;
	}

	protected ManterAltaDiagPrincipalON getManterAltaDiagPrincipalON() {
		return manterAltaDiagPrincipalON;
	}

	protected ManterAltaDiagMtvoInternacaoON getManterAltaDiagMtvoInternacaoON() {
		return manterAltaDiagMtvoInternacaoON;
	}

	protected ManterAltaOutraEquipeSumrON getManterAltaOutraEquipeSumrON() {
		return manterAltaOutraEquipeSumrON;
	}

	protected ManterObtCausaDiretaON getManterObtCausaDiretaON() {
		return manterObtCausaDiretaON;
	}

	protected ManterObtCausaAntecedenteON getManterObtCausaAntecedenteON() {
		return manterObtCausaAntecedenteON;
	}

	protected ManterObtOutraCausaON getManterObtOutraCausaON() {
		return manterObtOutraCausaON;
	}

	protected ManterObitoNecropsiaON getManterObitoNecropsiaON() {
		return manterObitoNecropsiaON;
	}

	protected MpmAltaSumarioDAO getMpmAltaSumarioDAO(){
		return mpmAltaSumarioDAO;
	}

	protected ManterObtGravidezAnteriorON getManterObtGravidezAnteriorON() {
		return manterObtGravidezAnteriorON;
	}

	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return this.ambulatorioFacade;
	}

	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}

	protected MpmAltaPrincReceitasON getMpmAltaPrincReceitasON() {
		return mpmAltaPrincReceitasON;
	}

	protected void setMpmAltaPrincReceitasON(MpmAltaPrincReceitasON mpmAltaPrincReceitasON) {
		this.mpmAltaPrincReceitasON = mpmAltaPrincReceitasON;
	}
	
}