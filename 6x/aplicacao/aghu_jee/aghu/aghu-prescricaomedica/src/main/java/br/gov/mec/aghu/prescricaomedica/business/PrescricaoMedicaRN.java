package br.gov.mec.aghu.prescricaomedica.business;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dao.ObjetosOracleDAO;
import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.dominio.DominioOperacaoBanco;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoMedicamento;
import br.gov.mec.aghu.dominio.DominioSituacaoPrescricao;
import br.gov.mec.aghu.exameselaudos.BuscarResultadosExamesVO;
import br.gov.mec.aghu.exameselaudos.business.IExamesLaudosFacade;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.dao.AfaComponenteNptDAO;
import br.gov.mec.aghu.farmacia.dao.AfaComponenteNptJnDAO;
import br.gov.mec.aghu.farmacia.dao.AfaDecimalComponenteNptDAO;
import br.gov.mec.aghu.farmacia.dao.AfaGrupoComponenteNptDAO;
import br.gov.mec.aghu.farmacia.dao.AfaItemNptPadraoDAO;
import br.gov.mec.aghu.farmacia.dao.AfaMedicamentoDAO;
import br.gov.mec.aghu.farmacia.dao.AfaParamComponenteNptDAO;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AfaComponenteNpt;
import br.gov.mec.aghu.model.AfaComponenteNptJn;
import br.gov.mec.aghu.model.AfaDecimalComponenteNpt;
import br.gov.mec.aghu.model.AfaFormulaNptPadrao;
import br.gov.mec.aghu.model.AfaGrupoComponenteNpt;
import br.gov.mec.aghu.model.AfaItemNptPadrao;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaMensCalculoNpt;
import br.gov.mec.aghu.model.AfaParamComponenteNpt;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghProfEspecialidades;
import br.gov.mec.aghu.model.AhdHospitaisDia;
import br.gov.mec.aghu.model.AinAtendimentosUrgencia;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.FatConvGrupoItemProced;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatConvenioSaudePlanoId;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.MpmControlPrevAltas;
import br.gov.mec.aghu.model.MpmEscalaGlasgow;
import br.gov.mec.aghu.model.MpmInformacaoPrescribente;
import br.gov.mec.aghu.model.MpmItemPrescricaoNpt;
import br.gov.mec.aghu.model.MpmJustificativaNpt;
import br.gov.mec.aghu.model.MpmListaServSumrAlta;
import br.gov.mec.aghu.model.MpmMotivoIngressoCti;
import br.gov.mec.aghu.model.MpmPacAtendProfissional;
import br.gov.mec.aghu.model.MpmParecerUsoMdto;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmPrescricaoMedicaId;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VMpmPrescrMdtos;
import br.gov.mec.aghu.model.VMpmPrescrMdtosId;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.dao.AfaMensCalculoNptDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmControlPrevAltasDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmEscalaGlasglowDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmInformacaoPrescribenteDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemPrescricaoNptDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmJustificativaNptDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmLaudoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmListaServSumrAltaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPacAtendProfissionalDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmParecerUsoMdtosDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoMedicaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmSumarioAltaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmTipoFrequenciaAprazamentoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.VMpmPrescrMdtosDAO;
import br.gov.mec.aghu.prescricaomedica.vo.BuscaDadosAtendimentoUrgenciaVO;
import br.gov.mec.aghu.prescricaomedica.vo.BuscaDadosHospitalDiaVO;
import br.gov.mec.aghu.prescricaomedica.vo.BuscaDadosInternacaoVO;
import br.gov.mec.aghu.prescricaomedica.vo.BuscaPrescricaoMedicaVO;
import br.gov.mec.aghu.prescricaomedica.vo.ComponenteNPTVO;
import br.gov.mec.aghu.prescricaomedica.vo.ParecerPendenteVO;
import br.gov.mec.aghu.prescricaomedica.vo.VerificaAtendimentoVO;
import br.gov.mec.aghu.prescricaomedica.vo.VerificaIndicadoresConvenioInternacaoVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

/**
 * Implementação da package MPMK_RN.
 */

@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength", "PMD.JUnit4TestShouldUseTestAnnotation"})
@Stateless
public class PrescricaoMedicaRN extends BaseBusiness implements
Serializable {

	private static final Log LOG = LogFactory.getLog(PrescricaoMedicaRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@Inject
	private MpmPacAtendProfissionalDAO mpmPacAtendProfissionalDAO;
	
	@Inject
	private MpmListaServSumrAltaDAO mpmListaServSumrAltaDAO;
	
	@Inject
	private MpmTipoFrequenciaAprazamentoDAO mpmTipoFrequenciaAprazamentoDAO;
	
	@Inject
	private VMpmPrescrMdtosDAO vMpmPrescrMdtosDAO;
	
	@Inject
	private MpmPrescricaoMedicaDAO mpmPrescricaoMedicaDAO;
	
	@Inject
	private MpmControlPrevAltasDAO mpmControlPrevAltasDAO;
	
	@Inject
	private MpmEscalaGlasglowDAO mpmEscalaGlasglowDAO;
	
	@Inject
	private MpmJustificativaNptDAO mpmJustificativaNptDAO;
	
	@Inject
	private AfaMensCalculoNptDAO afaMensCalculoNptDAO;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IExamesLaudosFacade examesLaudosFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private MpmSumarioAltaDAO mpmSumarioAltaDAO;
	
	@Inject
	private MpmLaudoDAO mpmLaudoDAO;
	
	@EJB
	private IFarmaciaFacade farmaciaFacade;
	
	@Inject
	private ObjetosOracleDAO objetosOracleDAO;
	
	@Inject 
	private MpmInformacaoPrescribenteDAO mpmInformacaoPrescribenteDAO;
	
	@Inject
	private MpmParecerUsoMdtosDAO mpmParecerUsoMdtosDAO;
	
	@EJB
	private ListaPacientesInternadosON listaPacientesInternadosON;
		@Inject
	private AfaComponenteNptDAO afaComponenteNptDAO;
	
	@Inject
	private AfaMedicamentoDAO afaMedicamentoDAO;
	
	@Inject
	private AfaGrupoComponenteNptDAO afaGrupoComponenteNptDAO;
	
	@Inject
	private AfaParamComponenteNptDAO afaParamComponenteNptDAO;
	
	@Inject
	private AfaDecimalComponenteNptDAO afaDecimalComponenteNptDAO;
	
	@Inject
	private AfaComponenteNptJnDAO afaComponenteNptJnDAO;
	
	@Inject
	private MpmItemPrescricaoNptDAO mpmItemPrescricaoNptDAO;
	
	@Inject
	private AfaItemNptPadraoDAO afaItemNptPadraoDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6809803090442476157L;

	/**
	 * Constante que guarda o nome do atributo do contexto referente ao sequence
	 * DAO
	 */
	

//	private static final String ENTITY_MANAGER = "entityManager";

	private static final DateFormat DIA_MES_DATE_FORMAT = new SimpleDateFormat(
	"dd/MM");

	private static final NumberFormat NUMBER_FORMAT = NumberFormat
	.getInstance(new Locale("pt", "BR"));

	protected enum PrescricaoMedicamentoExceptionCode implements
	BusinessExceptionCode {
		AGH_00183, AGH_00199, AHD_00090, AIN_00268, //
		MPM_00748, MPM_00749, MPM_00750, MPM_01067, MPM_02290,//
		/** Tipo de freqüência exige a informação da freqüência. */
		MPM_00751, // 
		MPM_00752, MPM_01104, //
		MPM_01175, MPM_01311, MPM_01312, MPM_01340, MPM_01376, MPM_02012, //
		MPM_02424, MPM_02425, MPM_02426, MPM_02427, MPM_00885, MPM_00886, MPM_01567, //
		MPM_01568, FREQUENCIA_MENOR_IGUAL_ZERO, PREENCHER_CAMPOS_OBRIGATORIOS, AFA_00199, MSG_COMPONENTE_EXISTE,
		AFA_01208,AFA_00172,AFA_00173,MESSAGE_DEPENDENTE1,MESSAGE_DEPENDENTE2, MESSAGE_DEPENDENTE3, AFA_00197,
		MSG_CONSTRAINT1_PARAM;

		public void throwException() throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this);
		}

	}

	public void atualizaPrescricaoMedica(MpmPrescricaoMedica prescricaoMedica, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		
		MpmPrescricaoMedica prescricaoMedicaOld = getMpmPrescricaoMedicaDAO().obterPrescricaoPorId(prescricaoMedica.getId());

		getMpmPrescricaoMedicaDAO().merge(prescricaoMedica);
		
		atualizarFaturamento(prescricaoMedica, prescricaoMedicaOld, DominioOperacaoBanco.UPD, nomeMicrocomputador, dataFimVinculoServidor);

	}


	/**
	 * Usado para a suggestion Box.
	 * Se não retornar um resultado na primeira consulta, que busca apenas pelo nome exato da sigla
	 * Recorre á segunda consulta que busca por pedaços do nome ou da sigla.
	 * @return List<AghEspecialidades>Lista de especialidades para a suggestion
	 */
	public List<AghEspecialidades> getEspecialidades(Object objPesquisa) {
		List <AghEspecialidades> especialidades = this.getAghuFacade().pesquisarPorIdSigla(objPesquisa);
		if (especialidades == null || especialidades.isEmpty()) {
			especialidades = this.getAghuFacade().pesquisarPorNomeIdSiglaIndices(objPesquisa);
		}
		return especialidades; 
	}
	
	public Long getEspecialidadesCount(Object objPesquisa) {
		Long especialidadesCount = this.getAghuFacade().pesquisarPorIdSiglaCount(objPesquisa);
		if (especialidadesCount == null || especialidadesCount < 1) {
			especialidadesCount = this.getAghuFacade().pesquisarPorNomeIdSiglaIndicesCount(objPesquisa);
		}
		return especialidadesCount; 
	}	
	
	public List<AghEspecialidades> getEspecialidadesPorServidor(RapServidores servidor) {
		List<AghEspecialidades> especialidades = this.getAghuFacade().pesquisarEspecialidadesPorServidor(servidor);
		return especialidades;
	}

	public List<AghProfEspecialidades> getConsultoresPorEspecialidade(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, final AghEspecialidades especialidade) {
		List<AghProfEspecialidades> profEspecialidades = this.getAghuFacade()
				.pesquisarConsultoresPorEspecialidade(firstResult, maxResult,
						orderProperty, asc, especialidade);
		return profEspecialidades;
	}
	
	public Long getConsultoresPorEspecialidadeCount(AghEspecialidades especialidade) {
		return this.getAghuFacade().pesquisarConsultoresPorEspecialidadeCount(especialidade);
	}	

	/**
	 * Usado para a suggestion Box que consulta somente especialidades ativas sem indicação de consultoria
	 * Se não retornar um resultado na primeira consulta, que busca apenas pelo nome exato da sigla
	 * Recorre á segunda consulta que busca por pedaços do nome ou da sigla.
	 * @return List<AghEspecialidades>Lista de especialidades para a suggestion
	 */
	public List<AghEspecialidades> getEspecialidadesAtivas(Object objPesquisa) {
		List <AghEspecialidades> especialidades = this.getAghuFacade().pesquisarPorSiglaAtivas(objPesquisa);
		if (especialidades == null || especialidades.isEmpty()) {
			especialidades = this.getAghuFacade().pesquisarPorSiglaAtivasIndices(objPesquisa);
		}
		return especialidades; 
	}

	/**
	 * Valida a frequencia de acordo com o tipo de frequencia fornecida.
	 * Dependendo do tipo a frequencia pode ser obrigatória ou não.<br>
	 * 
	 * ORADB Procedure MPMK_RN.RN_MPMP_VER_DIG_FREQ
	 * 
	 * Se Tipo Frequencia Aprazamento possuir ind_digita_frequencia = 'S'
	 * frequencia não pode ser nula e Se Tipo Frequencia Aprazamento possuir
	 * ind_digita_frequencia = 'N' frequencia deve ser nula.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificaDigitacaoFrequencia(Short seqTipoFrequenciaAprazamento,
			Short frequencia) throws ApplicationBusinessException {
		if (frequencia != null && seqTipoFrequenciaAprazamento == null) {
			throw new ApplicationBusinessException(
					PrescricaoMedicamentoExceptionCode.MPM_01376);
		}

		if (seqTipoFrequenciaAprazamento != null) {
			MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento = getMpmTipoFrequenciaAprazamentoDAO()
			.obterPorChavePrimaria(seqTipoFrequenciaAprazamento);

			if (tipoFrequenciaAprazamento != null) {
				if (tipoFrequenciaAprazamento.getIndDigitaFrequencia()
						&& (frequencia == null || frequencia == 0)) {
					throw new ApplicationBusinessException(
							PrescricaoMedicamentoExceptionCode.MPM_00751);
				} else if (!tipoFrequenciaAprazamento.getIndDigitaFrequencia()
						&& (frequencia != null && frequencia > 0)) {
					throw new ApplicationBusinessException(
							PrescricaoMedicamentoExceptionCode.MPM_00752);
				}
			}
		}
	}

	public void verificaDigitacaoFrequencia(MpmTipoFrequenciaAprazamento tipo,
			Short frequencia) throws ApplicationBusinessException {
		this.verificaDigitacaoFrequencia(tipo == null ? null : tipo.getSeq(),
				frequencia);
	}

	/**
	 * ORADB Procedure MPMK_RN.RN_MPMP_VER_PROC_HOS
	 * 
	 * @author mtocchetto
	 * @throws ApplicationBusinessException
	 */
	public Integer verificaProcedimentoHospitalar(Integer codigoMaterial,
			Integer seqProcedimentoCirurgico,
			Short seqProcedimentoEspecialDiverso) throws ApplicationBusinessException {

		List<FatProcedHospInternos> listaProcedHospInterno = new ArrayList<FatProcedHospInternos>();
		Integer phiSeq = null;

		if (codigoMaterial != null) {
			listaProcedHospInterno = getFaturamentoFacade()
			.pesquisarFatProcedHospInternos(codigoMaterial, null, null);
		} else if (seqProcedimentoCirurgico != null) {
			listaProcedHospInterno = getFaturamentoFacade()
			.pesquisarFatProcedHospInternos(null,
					seqProcedimentoCirurgico, null);
		} else if (seqProcedimentoEspecialDiverso != null) {
			listaProcedHospInterno = getFaturamentoFacade()
			.pesquisarFatProcedHospInternos(null, null,
					seqProcedimentoEspecialDiverso);
		}

		phiSeq = listaProcedHospInterno.isEmpty() ? null
				: listaProcedHospInterno.get(0).getSeq();

		return phiSeq;
	}

	/**
	 * ORADB Procedure MPMK_RN.RN_MPMP_VER_ATD_PRCR
	 * 
	 * Verifica se o atendimento é valido.
	 * 
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public VerificaAtendimentoVO verificaAtendimento(Date dataHoraInicio,
			Date dataHoraFim, Integer seqAtendimento, Integer seqHospitalDia,
			Integer seqInternacao, Integer seqAtendimentoUrgencia)
	throws ApplicationBusinessException {
		// Verifica se o atendimento está vigente e se é válido para prescrição.
		// Pode ser acessada a partir do atendimento (atd), hospital dia (hod),
		// internação (int) ou atendimento urgência (atu).
		IPacienteFacade pacienteFacade = getPacienteFacade();

		AghAtendimentos atendimento = null;
		if (seqAtendimento != null) {
			atendimento = aghuFacade.obterAghAtendimentoPorChavePrimaria(seqAtendimento);

			super.refresh(atendimento);

			if (atendimento != null) {
				this.validaInformacoesAtendimento(atendimento, dataHoraInicio,
						dataHoraFim);
			} else {
				throw new ApplicationBusinessException(
						PrescricaoMedicamentoExceptionCode.AGH_00183);
			}
		}

		if (seqHospitalDia != null) {
			List<AghAtendimentos> atendimentos = pacienteFacade
			.obterAtendimentoPorSeqHospitalDia(seqHospitalDia);

			if (atendimentos != null && !atendimentos.isEmpty()) {
				// Pega apenas o primeiro atendimento, conforme procedure
				// migrada.
				atendimento = atendimentos.get(0);

				this.validaInformacoesAtendimento(atendimento, dataHoraInicio,
						dataHoraFim);
			} else {
				throw new ApplicationBusinessException(
						PrescricaoMedicamentoExceptionCode.AGH_00183);
			}
		}

		if (seqInternacao != null) {
			List<AghAtendimentos> atendimentos = pacienteFacade
			.obterAtendimentoPorSeqInternacao(seqInternacao);

			if (atendimentos != null && !atendimentos.isEmpty()) {
				// Pega apenas o primeiro atendimento, conforme procedure
				// migrada.
				atendimento = atendimentos.get(0);

				this.validaInformacoesAtendimento(atendimento, dataHoraInicio,
						dataHoraFim);
			} else {
				throw new ApplicationBusinessException(
						PrescricaoMedicamentoExceptionCode.AGH_00183);
			}
		}

		if (seqAtendimentoUrgencia != null) {
			List<AghAtendimentos> atendimentos = pacienteFacade
			.obterAtendimentoPorSeqAtendimentoUrgencia(seqAtendimentoUrgencia);

			if (atendimentos != null && !atendimentos.isEmpty()) {
				// Pega apenas o primeiro atendimento, conforme procedure
				// migrada.
				atendimento = atendimentos.get(0);

				this.validaInformacoesAtendimento(atendimento, dataHoraInicio,
						dataHoraFim);
			} else {
				throw new ApplicationBusinessException(
						PrescricaoMedicamentoExceptionCode.AGH_00183);
			}
		}

		VerificaAtendimentoVO verificaAtendimentoVO = null;
		if (atendimento != null) {
			verificaAtendimentoVO = new VerificaAtendimentoVO();
			verificaAtendimentoVO.setDthrInicio(atendimento.getDthrInicio());
			verificaAtendimentoVO.setDthrFim(atendimento.getDthrFim());
			verificaAtendimentoVO.setSeqAtendimento(atendimento.getSeq());
			verificaAtendimentoVO.setSeqHospitalDia(atendimento
					.getHospitalDia() != null ? atendimento.getHospitalDia()
							.getSeq() : null);
			verificaAtendimentoVO
			.setSeqInternacao(atendimento.getInternacao() != null ? atendimento
					.getInternacao().getSeq()
					: null);
			verificaAtendimentoVO.setSeqAtendimentoUrgencia(atendimento
					.getAtendimentoUrgencia() != null ? atendimento
							.getAtendimentoUrgencia().getSeq() : null);
			verificaAtendimentoVO.setSeqUnidadeFuncional(atendimento
					.getUnidadeFuncional() != null ? atendimento
							.getUnidadeFuncional().getSeq() : null);
		}

		return verificaAtendimentoVO;
	}

	private void validaInformacoesAtendimento(AghAtendimentos atendimento,
			Date dataHoraInicio, Date dataHoraFim) throws ApplicationBusinessException {
		if (dataHoraInicio != null) {
			if (!DominioOrigemAtendimento.
					getOrigensPermitemPrescricaoMedica().contains(atendimento.getOrigem())) {
				// Tipo de atendimento não permite prescrição
				throw new ApplicationBusinessException(
						PrescricaoMedicamentoExceptionCode.MPM_01340);
			}
			
			if (!DominioOrigemAtendimento.A.equals(atendimento.getOrigem())) {

				if (!CoreUtil.isBetweenDatas(dataHoraInicio, atendimento
						.getDthrInicio(),
						atendimento.getDthrFim() != null ? atendimento.getDthrFim()
								: dataHoraInicio)) {
					throw new ApplicationBusinessException(
							PrescricaoMedicamentoExceptionCode.AGH_00199);
				}
	
				if (dataHoraFim != null
						&& !CoreUtil.isBetweenDatas(dataHoraFim, atendimento
								.getDthrInicio(),
								atendimento.getDthrFim() != null ? atendimento
										.getDthrFim() : dataHoraFim)) {
					throw new ApplicationBusinessException(
							PrescricaoMedicamentoExceptionCode.AGH_00199);
				}
			}
		}
	}

	/**
	 * Valida as informações de aprazamento como frequencia, tipo de frequencia
	 * de aprazamento e compatibilidades.<br>
	 * 
	 * ORADB Procedure MPMK_RN.RN_MPMP_TIP_FREQ_APRA
	 * 
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public void validaAprazamento(Short seqTipoFrequenciaAprazamento,
			Short frequencia) throws ApplicationBusinessException {

		// se não foi informado apraz, não há validação
		if (seqTipoFrequenciaAprazamento == null && frequencia == null) {
			return;
		}

		// se informada a frequencia um tipo de apraz deve ser informado
		if (frequencia != null && seqTipoFrequenciaAprazamento == null) {
			throw new ApplicationBusinessException(
					PrescricaoMedicamentoExceptionCode.MPM_01376);
		}

		if (frequencia != null && frequencia.shortValue() <= 0) {
			throw new ApplicationBusinessException(
					PrescricaoMedicamentoExceptionCode.FREQUENCIA_MENOR_IGUAL_ZERO);
		}

		MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento = this
		.getMpmTipoFrequenciaAprazamentoDAO().obterPorChavePrimaria(
				seqTipoFrequenciaAprazamento);

		// Tipo de freqüência aprazamento não cadastrado.
		if (tipoFrequenciaAprazamento == null) {
			throw new ApplicationBusinessException(
					PrescricaoMedicamentoExceptionCode.MPM_00749);
		}

		// Tipo de freqüência aprazamento deve estar ativo.
		if (!DominioSituacao.A.equals(tipoFrequenciaAprazamento
				.getIndSituacao())) {
			throw new ApplicationBusinessException(
					PrescricaoMedicamentoExceptionCode.MPM_00750);
		}

		// Tipo de freqüência exige a informação da freqüência.
		if (tipoFrequenciaAprazamento.getIndDigitaFrequencia()
				&& (frequencia == null || frequencia.equals(0))) {
			throw new ApplicationBusinessException(
					PrescricaoMedicamentoExceptionCode.MPM_00751);
		}

		// Tipo de freqüência não permite a informação de freqüência.
		if (!tipoFrequenciaAprazamento.getIndDigitaFrequencia()
				&& frequencia != null && frequencia > 0) {
			throw new ApplicationBusinessException(
					PrescricaoMedicamentoExceptionCode.MPM_00752);
		}
	}

	public void validaAprazamento(
			MpmTipoFrequenciaAprazamento tipoFreqAprazamento, Short frequencia)
	throws ApplicationBusinessException {
		if (tipoFreqAprazamento == null) {
			this.validaAprazamento((Short) null, frequencia);
		} else {
			this.validaAprazamento(tipoFreqAprazamento.getSeq(), frequencia);
		}

	}

	/**
	 * ORADB Procedure MPMK_RN.RN_MPMP_VER_PR_MD_UP
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificaPrescricaoMedicaUpdate(Integer seqAtendimento,
			Date dataHoraInicio, Date dataHoraFim,
			Date dataHoraMovimentoPendente,
			DominioIndPendenteItemPrescricao pendente, String operacao, 
			String nomeMicrocomputador, 
			final Date dataFimVinculoServidor)
	throws ApplicationBusinessException {
		BuscaPrescricaoMedicaVO buscaPrescricaoMedicaVO = buscaPrescricaoMedica(
				seqAtendimento, dataHoraInicio, dataHoraFim, operacao, pendente);

		if (buscaPrescricaoMedicaVO.getDataHoraInicio() == null
				&& buscaPrescricaoMedicaVO.getDataHoraFim() == null
				&& buscaPrescricaoMedicaVO.getSituacao() == null) {
			throw new ApplicationBusinessException(
					PrescricaoMedicamentoExceptionCode.MPM_01104);
		}

		if (buscaPrescricaoMedicaVO.getDataHoraMovimentoPendente() == null
				&& !DominioIndPendenteItemPrescricao.N.equals(pendente)) {
			try {
				MpmPrescricaoMedicaId id = new MpmPrescricaoMedicaId();
				id.setSeq(buscaPrescricaoMedicaVO.getSeqPrescricaoMedica());
				id.setAtdSeq(seqAtendimento);

				MpmPrescricaoMedica prescricaoMedica = getMpmPrescricaoMedicaDAO()
				.obterPorChavePrimaria(id);

				if (prescricaoMedica != null) {
					prescricaoMedica
					.setDthrInicioMvtoPendente(buscaPrescricaoMedicaVO
							.getDataHoraMovimento());
					atualizaPrescricaoMedica(prescricaoMedica, nomeMicrocomputador, dataFimVinculoServidor);
				}
			} catch (Exception e) {
				logError("Exceção capturada: ", e);
				throw new ApplicationBusinessException(
						PrescricaoMedicamentoExceptionCode.MPM_01311);
			}
		}
	}

	/**
	 * ORADB Procedure MPMK_RN.RN_MPMP_VER_PRCR_MDC ORADB Procedure
	 * MPMK_PPR_RN.RN_PPRP_VER_PRCR_MED
	 * 
	 * Regra genérica para obter prescrição.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificaPrescricaoMedica(Integer seqAtendimento,
			Date dataHoraInicio, Date dataHoraFim,
			Date dataHoraMovimentoPendente,
			DominioIndPendenteItemPrescricao pendente, String operacao, 
			String nomeMicrocomputador, final Date dataFimVinculoServidor)
	throws ApplicationBusinessException {
		BuscaPrescricaoMedicaVO buscaPrescricaoMedicaVO = buscaPrescricaoMedica(
				seqAtendimento, dataHoraInicio, dataHoraFim, operacao, pendente);

		if (buscaPrescricaoMedicaVO.getDataHoraInicio() == null
				&& buscaPrescricaoMedicaVO.getDataHoraFim() == null
				&& buscaPrescricaoMedicaVO.getSituacao() == null) {
			throw new ApplicationBusinessException(
					PrescricaoMedicamentoExceptionCode.MPM_00748);
		}

		BuscaPrescricaoMedicaVO buscaPrescricaoMedicaVO2 = null;
		if (dataHoraFim != null
				&& dataHoraFim.after(buscaPrescricaoMedicaVO.getDataHoraFim())) {
			// Buscando prescrição válida para a data final informada
			buscaPrescricaoMedicaVO2 = buscaPrescricaoMedica(seqAtendimento,
					dataHoraInicio, dataHoraFim, operacao, pendente);

			if (buscaPrescricaoMedicaVO2.getDataHoraInicio() == null
					&& buscaPrescricaoMedicaVO2.getDataHoraFim() == null
					&& buscaPrescricaoMedicaVO2.getSituacao() == null) {
				throw new ApplicationBusinessException(
						PrescricaoMedicamentoExceptionCode.MPM_02012);
			}
		}

		if (buscaPrescricaoMedicaVO.getDataHoraMovimentoPendente() == null
				&& !DominioIndPendenteItemPrescricao.N.equals(pendente)) {
			try {
				MpmPrescricaoMedicaId id = new MpmPrescricaoMedicaId();
				id.setSeq(buscaPrescricaoMedicaVO.getSeqPrescricaoMedica());
				id.setAtdSeq(seqAtendimento);

				MpmPrescricaoMedica prescricaoMedica = getMpmPrescricaoMedicaDAO()
				.obterPorChavePrimaria(id);

				if (prescricaoMedica != null) {
					prescricaoMedica
					.setDthrInicioMvtoPendente(buscaPrescricaoMedicaVO
							.getDataHoraMovimento());
					atualizaPrescricaoMedica(prescricaoMedica, nomeMicrocomputador, dataFimVinculoServidor);
				}
			} catch (Exception e) {
				logError("Exceção capturada: ", e);
				throw new ApplicationBusinessException(
						PrescricaoMedicamentoExceptionCode.MPM_01312);
			}
		}
	}

	/**
	 * ORADB Procedure MPMK_RN.MPMP_GET_PRCR_MED2
	 * 
	 * Busca a prescrição médica conforme atendimento e data informado,
	 * retornando os valores dos campos dthr_inicio, dthr_fim, ind_situacao, seq
	 * e dthr_inicio_mvto_pendente.
	 * 
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public BuscaPrescricaoMedicaVO buscaPrescricaoMedica(
			Integer seqAtendimento, Date dataHoraInicio, Date dataHoraFim,
			String operacao, DominioIndPendenteItemPrescricao indPendente)
	throws ApplicationBusinessException {
		BuscaPrescricaoMedicaVO buscaPrescricaoMedicaVO = new BuscaPrescricaoMedicaVO();

		if (dataHoraInicio == null) {
			throw new ApplicationBusinessException(
					PrescricaoMedicamentoExceptionCode.MPM_02427);
		}

		if (dataHoraFim == null || dataHoraInicio.equals(dataHoraFim)) {
			List<MpmPrescricaoMedica> prescricoesMedicamentos = getMpmPrescricaoMedicaDAO()
			.listarPrescricoesMedicasInicio(seqAtendimento,
					dataHoraInicio);

			if (prescricoesMedicamentos != null
					&& !prescricoesMedicamentos.isEmpty()) {
				// Pega o primeiro elemento, conforme lógica do cursor do oracle
				MpmPrescricaoMedica prescricaoMedica = prescricoesMedicamentos
				.get(0);
				this.testaEmUso(indPendente, dataHoraFim, operacao,
						prescricaoMedica.getSituacao(), prescricaoMedica
						.getDthrMovimento(), prescricaoMedica
						.getDthrFim(), 1);

				buscaPrescricaoMedicaVO.setDataHoraInicio(prescricaoMedica
						.getDthrInicio());
				buscaPrescricaoMedicaVO.setDataHoraFim(prescricaoMedica
						.getDthrFim());
				buscaPrescricaoMedicaVO.setSituacao(prescricaoMedica
						.getSituacao());
				buscaPrescricaoMedicaVO.setSeqPrescricaoMedica(prescricaoMedica
						.getId().getSeq());
				buscaPrescricaoMedicaVO
				.setDataHoraMovimentoPendente(prescricaoMedica
						.getDthrInicioMvtoPendente());
				buscaPrescricaoMedicaVO.setDataHoraMovimento(prescricaoMedica
						.getDthrMovimento());
				buscaPrescricaoMedicaVO.setDataReferencia(prescricaoMedica
						.getDtReferencia());
			}
		} else {
			Date auxDataFim = null;
			if ("I".equals(operacao)) {
				List<MpmPrescricaoMedica> prescricoesMedicamentos = getMpmPrescricaoMedicaDAO()
				.listarPrescricoesMedicasInicio(seqAtendimento,
						dataHoraInicio);

				if (prescricoesMedicamentos != null
						&& !prescricoesMedicamentos.isEmpty()) {
					// Pega o primeiro elemento, conforme lógica do cursor do
					// oracle
					MpmPrescricaoMedica prescricaoMedica = prescricoesMedicamentos
					.get(0);
					auxDataFim = prescricaoMedica.getDthrFim();

					this.testaEmUso(indPendente, dataHoraFim, operacao,
							prescricaoMedica.getSituacao(), prescricaoMedica
							.getDthrMovimento(), prescricaoMedica
							.getDthrFim(), 2);

					buscaPrescricaoMedicaVO.setDataHoraInicio(prescricaoMedica
							.getDthrInicio());
					buscaPrescricaoMedicaVO.setDataHoraFim(prescricaoMedica
							.getDthrFim());
					buscaPrescricaoMedicaVO.setSituacao(prescricaoMedica
							.getSituacao());
					buscaPrescricaoMedicaVO
					.setSeqPrescricaoMedica(prescricaoMedica.getId()
							.getSeq());
					buscaPrescricaoMedicaVO
					.setDataHoraMovimentoPendente(prescricaoMedica
							.getDthrInicioMvtoPendente());
					buscaPrescricaoMedicaVO
					.setDataHoraMovimento(prescricaoMedica
							.getDthrMovimento());
					buscaPrescricaoMedicaVO.setDataReferencia(prescricaoMedica
							.getDtReferencia());
				}
			}

			if ("U".equals(operacao)
					|| (auxDataFim != null && dataHoraFim.after(auxDataFim))) {
				List<MpmPrescricaoMedica> prescricoesMedicamentos = getMpmPrescricaoMedicaDAO()
				.listarPrescricoesMedicasFim(seqAtendimento,
						dataHoraFim);

				if (prescricoesMedicamentos != null
						&& !prescricoesMedicamentos.isEmpty()) {
					// Pega o primeiro elemento, conforme lógica do cursor do
					// oracle
					MpmPrescricaoMedica prescricaoMedica = prescricoesMedicamentos
					.get(0);

					this.testaEmUso(indPendente, dataHoraFim, operacao,
							prescricaoMedica.getSituacao(), prescricaoMedica
							.getDthrMovimento(), prescricaoMedica
							.getDthrFim(), 3);

					buscaPrescricaoMedicaVO.setDataHoraInicio(prescricaoMedica
							.getDthrInicio());
					buscaPrescricaoMedicaVO.setDataHoraFim(prescricaoMedica
							.getDthrFim());
					buscaPrescricaoMedicaVO.setSituacao(prescricaoMedica
							.getSituacao());
					buscaPrescricaoMedicaVO
					.setSeqPrescricaoMedica(prescricaoMedica.getId()
							.getSeq());
					buscaPrescricaoMedicaVO
					.setDataHoraMovimentoPendente(prescricaoMedica
							.getDthrInicioMvtoPendente());
					buscaPrescricaoMedicaVO
					.setDataHoraMovimento(prescricaoMedica
							.getDthrMovimento());
					buscaPrescricaoMedicaVO.setDataReferencia(prescricaoMedica
							.getDtReferencia());
				}
			}
		}

		return buscaPrescricaoMedicaVO;
	}
	
	//verifica se é a primeira prescrição do paciente após entrada na unidade funcional CTI
	public boolean verificarPrimeiraEntradaUnidadeFuncional(Integer atdSeq) throws ApplicationBusinessException {
		boolean existeCID = false;

		List<Short> listaUnfSeq = this.aghuFacade
				.verificaPrimeiraPrescricaoMedicaPacienteUnidFuncional(atdSeq, "P_AGHU_MPM_CARACTERISTICA");
		
		if (!listaUnfSeq.isEmpty()) {
			List<MpmMotivoIngressoCti> listaCIDS = this
					.prescricaoMedicaFacade.pesquisarMotivoIngressoCtiPorAtdSeq(atdSeq);
				
			if (!listaCIDS.isEmpty() && listaCIDS.get(0).getCid() != null) {
				existeCID = true;
			}
		} else {
			// Seta para true pois não pertence à uma UF CTI
			existeCID = true;
		}
		return existeCID;
	}
	/**
	 * ORADB Procedure MPMK_RN.MPMP_TESTA_EM_USO
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void testaEmUso(DominioIndPendenteItemPrescricao pendente,
			Date dataHoraFim, String operacao,
			DominioSituacaoPrescricao situacao, Date dataHoraMovimento,
			Date dataHoraFimPrescricaoMedica, int tSeq)
	throws ApplicationBusinessException {
		boolean testar = true;

		if ((pendente == null
				|| DominioIndPendenteItemPrescricao.D.equals(pendente) || DominioIndPendenteItemPrescricao.N
				.equals(pendente))
				|| (dataHoraFim != null && new Date().after(dataHoraFim))
				|| ("U".equals(operacao)
						&& DominioIndPendenteItemPrescricao.P.equals(pendente) && dataHoraFim != null)
						|| ("U".equals(operacao)
								&& (DominioIndPendenteItemPrescricao.E.equals(pendente) || DominioIndPendenteItemPrescricao.A
										.equals(pendente)) && (dataHoraFim != null && dataHoraFim
												.equals(dataHoraFimPrescricaoMedica)))) {
			testar = false;
		}

		if (testar) {
			if (!DominioSituacaoPrescricao.U.equals(situacao)
					|| dataHoraMovimento == null) {
				if (tSeq == 1) {
					throw new ApplicationBusinessException(
							PrescricaoMedicamentoExceptionCode.MPM_02424);
				} else if (tSeq == 2) {
					throw new ApplicationBusinessException(
							PrescricaoMedicamentoExceptionCode.MPM_02425);
				} else if (tSeq == 3) {
					throw new ApplicationBusinessException(
							PrescricaoMedicamentoExceptionCode.MPM_02426);
				}
			}
		}
	}

	/**
	 * ORADB Procedure MPMK_RN.RN_MPMC_VER_ATD_EGW
	 * 
	 * Verifica atendimento da escala glasgow.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public Integer verificaAtendimentoEescalaGlasgow(Integer seqEscalaGlasgow)
	throws ApplicationBusinessException {

		MpmEscalaGlasgow escalaGlasglow = this.getEscalaGlasglowDAO().obterPorChavePrimaria(seqEscalaGlasgow);

		if (escalaGlasglow == null || escalaGlasglow.getAtendimento() == null) {
			throw new ApplicationBusinessException(PrescricaoMedicamentoExceptionCode.MPM_00885);
		}

		return escalaGlasglow.getAtendimento().getSeq();
	}

	/**
	 * ORADB Procedure MPMK_RN.RN_MPMP_VER_PHI
	 * 
	 * Verifica o Procedimento Hospitalar Interno.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public Integer verificaProcedimentoHospitalarInterno(
			Integer codigoMaterial, Integer seqProcedimentoCirurgico,
			Short seqProcedimentoEspecialDiverso) throws ApplicationBusinessException {

		// Identico ao método ORADB Procedure MPMK_RN.RN_MPMP_VER_PROC_HOS
		return verificaProcedimentoHospitalar(codigoMaterial,
				seqProcedimentoCirurgico, seqProcedimentoEspecialDiverso);
	}

	/**
	 * ORADB Procedure MPMK_RN.RN_MPMP_VER_CONVENIO
	 * 
	 * Verifica indicadores convênio da internação para o atendimento associado.
	 * 
	 * bsoliveira 26/10/2010
	 * 
	 * @throws ApplicationBusinessException
	 */
	public VerificaIndicadoresConvenioInternacaoVO verificaIndicadoresConvenioInternacao(
			Short cspCnvCodigo, Byte cspSeq, Integer phiSeq)
	throws ApplicationBusinessException {

		VerificaIndicadoresConvenioInternacaoVO verificaIndicadoresConvenioInternacaoVO = null;

		FatConvGrupoItemProced fatConvGrupoItemProced = this
		.getFaturamentoFacade()
		.obterFatConvGrupoItensProcedPeloId(cspCnvCodigo, cspSeq,
				phiSeq);
		if (fatConvGrupoItemProced != null) {

			verificaIndicadoresConvenioInternacaoVO = new VerificaIndicadoresConvenioInternacaoVO();
			verificaIndicadoresConvenioInternacaoVO
			.setPhiSeq(fatConvGrupoItemProced.getId().getPhiSeq());
			verificaIndicadoresConvenioInternacaoVO
			.setIndExigeJustificativa(fatConvGrupoItemProced
					.getIndExigeJustificativa());
			verificaIndicadoresConvenioInternacaoVO
			.setIndImprimeLaudo(fatConvGrupoItemProced
					.getIndImprimeLaudo());
			verificaIndicadoresConvenioInternacaoVO
			.setIndCobrancaFracionada(fatConvGrupoItemProced
					.getIndCobrancaFracionada());

		}

		return verificaIndicadoresConvenioInternacaoVO;
	}

	/**
	 * ORADB Procedure MPMK_RN.RN_MPMP_VER_HOD
	 * 
	 * Busca dados do hospital dia.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public BuscaDadosHospitalDiaVO buscaDadosHospitalDia(Integer seqHospitalDia)
	throws ApplicationBusinessException {
		IInternacaoFacade internacaoFacade = getInternacaoFacade();
		AhdHospitaisDia hospitalDia = internacaoFacade.obterAhdHospitaisDiaPorChavePrimaria(seqHospitalDia);

		if (hospitalDia == null) {
			PrescricaoMedicamentoExceptionCode.AHD_00090.throwException();
		}

		BuscaDadosHospitalDiaVO buscaDadosHospitalDiaVO = new BuscaDadosHospitalDiaVO();
		buscaDadosHospitalDiaVO.setCodigoConvenioSaude(hospitalDia
				.getConvenioSaudePlano().getId().getCnvCodigo());
		buscaDadosHospitalDiaVO.setCspSeq(hospitalDia.getConvenioSaudePlano()
				.getId().getSeq());
		buscaDadosHospitalDiaVO.setDataHoraPrimeiroEvento(hospitalDia
				.getDthrPrimeiroEvento());
		buscaDadosHospitalDiaVO.setDataHoraUltimoEvento(hospitalDia
				.getDthrUltimoEvento());

		return buscaDadosHospitalDiaVO;
	}

	/**
	 * ORADB Procedure MPMK_RN.RN_MPMP_VER_INT
	 * 
	 * Busca dados da internação.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public BuscaDadosInternacaoVO buscaDadosInternacao(Integer seqInternacao)
	throws ApplicationBusinessException {
		IInternacaoFacade internacaoFacade = getInternacaoFacade();
		AinInternacao internacao = internacaoFacade .obterAinInternacaoPorChavePrimaria(seqInternacao);

		if (internacao == null) {
			PrescricaoMedicamentoExceptionCode.AIN_00268.throwException();
		}

		FatConvenioSaudePlano convenioSaudePlano = internacao
		.getConvenioSaudePlano();
		FatConvenioSaudePlanoId convenioSaudePlanoId = convenioSaudePlano
		.getId();

		BuscaDadosInternacaoVO buscaDadosInternacaoVO = new BuscaDadosInternacaoVO();
		buscaDadosInternacaoVO.setCodigoConvenioSaude(convenioSaudePlanoId
				.getCnvCodigo());
		buscaDadosInternacaoVO.setSeqConvenioSaudePlano(convenioSaudePlanoId
				.getSeq());
		buscaDadosInternacaoVO.setDataHoraPrimeiroEvento(internacao
				.getDthrPrimeiroEvento());
		buscaDadosInternacaoVO.setDataHoraUltimoEvento(internacao
				.getDthrUltimoEvento());

		return buscaDadosInternacaoVO;
	}

	/**
	 * ORADB Procedure MPMK_RN.RN_MPMP_VER_ATU
	 * 
	 * Busca dados do atendimento urgência
	 * 
	 * @throws ApplicationBusinessException
	 */
	public BuscaDadosAtendimentoUrgenciaVO buscaDadosAtendimentoUrgencia(
			Integer seqAtendimentoUrgencia) throws ApplicationBusinessException {
		IInternacaoFacade internacaoFacade = getInternacaoFacade();
		AinAtendimentosUrgencia atendimentoUrgencia = internacaoFacade
				.obterAinAtendimentosUrgenciaPorChavePrimaria(seqAtendimentoUrgencia);

		if (atendimentoUrgencia == null) {
			PrescricaoMedicamentoExceptionCode.MPM_01175.throwException();
		}

		BuscaDadosAtendimentoUrgenciaVO buscaDadosAtendimentoUrgenciaVO = new BuscaDadosAtendimentoUrgenciaVO();
		buscaDadosAtendimentoUrgenciaVO
		.setCodigoConvenioSaude(atendimentoUrgencia.getConvenioSaude()
				.getCodigo());
		buscaDadosAtendimentoUrgenciaVO.setCspSeq(atendimentoUrgencia
				.getCspSeq());

		return buscaDadosAtendimentoUrgenciaVO;
	}

	/**
	 * ORADB FATC_BUSCA_IPH_AGRUPA
	 * 
	 * Busca o cod_tabela para um IPH.
	 * 
	 * @param phiSeq
	 * @param cnvCodigo
	 * @param cspSeq
	 * @param phoSeq
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public String buscaProcedimentoHospitalarInternoAgrupa(Integer phiSeq,
			Short cnvCodigo, Byte cspSeq, Short phoSeq)
	throws ApplicationBusinessException {
		Short tipoGrupoContaSUS = null;

		AghParametros aghParametros = getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS);
		if (aghParametros != null && aghParametros.getVlrNumerico() != null) {
			tipoGrupoContaSUS = aghParametros.getVlrNumerico().shortValue();
		}

		List<String> descricoes = getFaturamentoFacade()
		.buscaProcedimentoHospitalarInternoAgrupa(phiSeq, cnvCodigo,
				cspSeq, phoSeq, tipoGrupoContaSUS);

		String descricao = null;
		if (descricoes != null && !descricoes.isEmpty()) {
			descricao = descricoes.get(descricoes.size() - 1);// Pega o último
			// elemento da
			// lista
		}

		return descricao;
	}

	/**
	 * ORADB FATC_BUSCA_DESCR_IPH
	 * 
	 * Busca a descrição do código SUS para um phi.
	 * 
	 * @param phiSeq
	 * @param cnvCodigo
	 * @param cspSeq
	 * @param phoSeq
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Long buscaDescricaoProcedimentoHospitalarInterno(Integer phiSeq,
			Short cnvCodigo, Byte cspSeq, Short phoSeq)
	throws ApplicationBusinessException {
		Short tipoGrupoContaSUS = null;

		AghParametros aghParametros = getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS);
		if (aghParametros != null && aghParametros.getVlrNumerico() != null) {
			tipoGrupoContaSUS = aghParametros.getVlrNumerico().shortValue();
		}

		List<Long> codigos = getFaturamentoFacade()
		.buscaDescricaoProcedimentoHospitalarInterno(phiSeq, cnvCodigo,
				cspSeq, phoSeq, tipoGrupoContaSUS);

		Long codigoTabela = null;
		if (codigos != null && !codigos.isEmpty() && codigos.size() == 1) {
			codigoTabela = codigos.get(0);
		}

		return codigoTabela;
	}

	/**
	 * ORADB MPMC_GET_JUS_LD_ITEM
	 * 
	 * @param atdSeq
	 * @param phiSeq
	 * @return
	 */
	public String buscaJustificativaItemLaudo(Integer atdSeq, Integer phiSeq) {
		return getMpmLaudoDAO().buscaJustificativaItemLaudo(atdSeq, phiSeq);
	}

	/**
	 * TODO: SERA MIGRADO NA V2 (SEGUNDA VERSAO) DO MODULO DE PRESCRICAO.
	 * 
	 * Retorna, para um determinado paciente e campo laudo, o resultado do exame
	 * mais próximo da data informada baseado na data de liberação do resultado,
	 * que esteja dentro do período desta data menos o número de dias informado.
	 * 
	 * ORADB MPMC_GET_RESUL_EXAME
	 * 
	 * @param pacCodigo
	 * @param dataLiberacao
	 * @param nomeCampo
	 * @param numeroDias
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public String buscarResultadoExame(Integer pacCodigo, Date dataLiberacao,
			String nomeCampo, Integer numeroDias) throws ApplicationBusinessException {
		StringBuffer retorno = null;

		List<BuscarResultadosExamesVO> listaVO = getExamesLaudosFacade()
		.buscarResultadosExames(pacCodigo, dataLiberacao, nomeCampo);

		if (listaVO != null && !listaVO.isEmpty()) {
			BuscarResultadosExamesVO vo = listaVO.get(0);

			retorno = new StringBuffer(NUMBER_FORMAT.format(vo.getResultado())).append('(');
			if (vo.getData() != null) {
				retorno.append(DIA_MES_DATE_FORMAT.format(vo.getData()));
			}
			retorno.append(')');
		}

		return retorno != null ? retorno.toString() : null;
	}

	/**
	 * ORADB Procedure MPMK_FIA_RN.RN_FIAP_VER_GLASGOW
	 * 
	 * @param seqEscalaGlasglow
	 * @param seqAtendimento
	 */
	public void verificarEscalaGlasglow(Integer seqEscalaGlasglow,
			Integer seqAtendimento) throws ApplicationBusinessException {

		Integer seqAtd = this.verificaAtendimentoEescalaGlasgow(seqEscalaGlasglow);

		if (seqAtd != seqAtendimento) {
			throw new ApplicationBusinessException(
					PrescricaoMedicamentoExceptionCode.MPM_00886);
		}
	}

	/**
	 * ORADB: Procedure MPMK_PAR_RN.RN_PARP_ATU_DELETE
	 * 
	 * Excluir ocorrências da tabela atendimento profissional.
	 * 
	 * @param atdSeq
	 */
	public void removerAtendProf(Integer atdSeq) {

		List<MpmPacAtendProfissional> atdProfs = this.getMpmPacAtendProfissionalDAO().pesquisarPacienteAtendimentoProfissional(atdSeq);

		if( atdProfs != null && !atdProfs.isEmpty()) {
			for (MpmPacAtendProfissional atdProf : atdProfs) {
				this.getMpmPacAtendProfissionalDAO().remover(atdProf);
				this.getMpmPacAtendProfissionalDAO().flush();
			}
		}
	}

	/**
	 * ORADB: Procedure MPMK_LSA_RN.RN_LSAP_ATU_DELETE
	 * 
	 * Exclui ocorrências de lista sumários pendentes.
	 * 
	 * @param atdSeq
	 */
	public void removerSumariosPendentes(Integer atdSeq) {

		List<MpmListaServSumrAlta> sumPends = this.getMpmListaServSumrAltaDAO().pesquisarSumariosPendentesPorAtendimento(atdSeq);

		if( sumPends != null && !sumPends.isEmpty()) {
			for (MpmListaServSumrAlta sumPend : sumPends) {
				this.getMpmListaServSumrAltaDAO().remover(sumPend);
				this.getMpmListaServSumrAltaDAO().flush();
			}
		}
	}

	/**
	 * ORADB Procedure MPMK_FIA_RN.RN_FIAP_VER_ATEND
	 * @param dataHoraInicio
	 * @param seqAtendimento
	 * @param dataRealizacao
	 * @throws ApplicationBusinessException
	 */
	public void verificarAtendimento(Date dataHoraInicio, Integer seqAtendimento,
			Date dataRealizacao) throws ApplicationBusinessException {

		VerificaAtendimentoVO vo = verificaAtendimento(dataHoraInicio, null,
				seqAtendimento, null, null, null);

		if (dataRealizacao != null && vo != null && vo.getDthrInicio() != null) {
			if (dataRealizacao.compareTo(vo.getDthrInicio()) < 0) {
				throw new ApplicationBusinessException(
						PrescricaoMedicamentoExceptionCode.MPM_01567);
			}
			if (dataRealizacao.compareTo(new Date()) > 0) {
				throw new ApplicationBusinessException(
						PrescricaoMedicamentoExceptionCode.MPM_01568);
			}
		}
	}

	/**
	 * ORADB MPMP_ENFORCE_PME_RULES
	 * 
	 * TODO: MPMP_ENFORCE_PME_RULES (implementa parcialmente)
	 * 
	 * @param prescricaoMedica
	 * @throws BaseException 
	 */
	public void atualizarFaturamento(MpmPrescricaoMedica prescricaoMedica, MpmPrescricaoMedica prescricaoMedicaOld, DominioOperacaoBanco operacao, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		String usuarioLogado = null;
		if (servidorLogado != null) {
			usuarioLogado = servidorLogado.getUsuario();
		}
		
		if (operacao.equals(DominioOperacaoBanco.UPD)) {
			/* Verifica se já existe prescrição com alta */
			if (prescricaoMedicaOld != null && prescricaoMedica != null 
					&& CoreUtil.modificados(prescricaoMedicaOld.getSituacao(),prescricaoMedica.getSituacao())) {
				if (DominioSituacaoPrescricao.U.equals(prescricaoMedica.getSituacao())) {
					this.rnPmepVerPosAlta(prescricaoMedica.getId().getAtdSeq());
				}
			}

			/* verifica duas prescricoes em uso */
			if (DominioSituacaoPrescricao.U.equals(prescricaoMedica.getSituacao())) {
				this.rnPmepVerEmUso(prescricaoMedica.getId());
			}

			/* gera dispensação de medicamento */
			if (prescricaoMedicaOld != null && prescricaoMedica != null 
					&& DominioSituacaoPrescricao.U.equals(prescricaoMedicaOld.getSituacao()) 
					&& DominioSituacaoPrescricao.L.equals(prescricaoMedica.getSituacao())) {
						if (prescricaoMedicaOld.getServidorValida() == null && prescricaoMedica.getServidorValida() != null) {
							 //mpmp_gera_disp_tot (l_pme_row_new.atd_seq, l_pme_row_new.seq, l_pme_row_new.dthr_inicio, l_pme_row_new.dthr_fim);
							getFarmaciaFacade().mpmpGeraDispTot(prescricaoMedica.getId().getAtdSeq(), prescricaoMedica.getId().getSeq(), prescricaoMedica.getDthrInicio(), prescricaoMedica.getDthrFim(), nomeMicrocomputador);
							//fatk_interface_mpm.rn_fatp_atu_interface(l_pme_row_new.atd_seq,l_pme_row_new.seq, l_pme_row_new.dthr_inicio, l_pme_row_new.dthr_fim,NULL, 'I');
							getFaturamentoFacade().atualizaContaHospitalar(prescricaoMedica.getId().getAtdSeq(), prescricaoMedica.getId().getSeq(), prescricaoMedica.getDthrInicio(), prescricaoMedica.getDthrFim(), null, DominioOperacaoBanco.INS, dataFimVinculoServidor);
							
							//mpmp_gera_pista_mci (l_pme_row_new.atd_seq, l_pme_row_new.seq,l_pme_row_new.dthr_inicio, l_pme_row_new.dthr_fim);
							getObjetosOracleDAO().geraPistaMci(prescricaoMedica.getId().getAtdSeq(), prescricaoMedica.getId().getSeq(), prescricaoMedica.getDthrInicio(), prescricaoMedica.getDthrFim(), usuarioLogado);
							
							if(getPacienteFacade().verificarUnidadeChecagem(prescricaoMedica.getId().getAtdSeq())){
								 //mpmp_gera_ordem_adm (l_pme_row_new.atd_seq, l_pme_row_new.seq, l_pme_row_new.dthr_inicio,  l_pme_row_new.dthr_fim,  l_pme_row_new.dt_referencia);
								getObjetosOracleDAO().geraOrdemAdm(prescricaoMedica.getId().getAtdSeq(), prescricaoMedica.getId().getSeq(), prescricaoMedica.getDthrInicio(), prescricaoMedica.getDthrFim(), prescricaoMedica.getDtReferencia(), usuarioLogado);
								 //mpmp_inclui_prcr_ece (l_pme_row_new.atd_seq, l_pme_row_new.seq, l_pme_row_new.dthr_inicio, l_pme_row_new.dthr_fim, l_pme_row_new.dt_referencia);
								getObjetosOracleDAO().incluiPrcrEce(prescricaoMedica.getId().getAtdSeq(), prescricaoMedica.getId().getSeq(), prescricaoMedica.getDthrInicio(), prescricaoMedica.getDthrFim(), prescricaoMedica.getDtReferencia(), usuarioLogado);
							}
						}
						if (prescricaoMedicaOld.getServidorValida() != null 
								&& prescricaoMedica.getServidorValida() != null
								&& prescricaoMedicaOld.getDthrInicioMvtoPendente() != null 
								&& prescricaoMedica.getDthrInicioMvtoPendente() == null) {
								//	mpmp_gera_disp_mvto (l_pme_row_new.atd_seq, l_pme_row_new.seq, l_pme_saved_row.dthr_inicio_mvto_pendente, l_pme_row_new.dthr_inicio, l_pme_row_new.dthr_fim);
								getFarmaciaFacade().mpmpGeraDispMVto(prescricaoMedica.getId().getAtdSeq(), prescricaoMedica.getId().getSeq(), prescricaoMedicaOld.getDthrInicioMvtoPendente(), prescricaoMedica.getDthrInicio(), prescricaoMedica.getDthrFim(), nomeMicrocomputador);
								//fatk_interface_mpm.rn_fatp_atu_interface(l_pme_row_new.atd_seq, l_pme_row_new.seq, l_pme_row_new.dthr_inicio, l_pme_row_new.dthr_fim, l_pme_saved_row.dthr_inicio_mvto_pendente, 'A');
								getFaturamentoFacade().atualizaContaHospitalar(prescricaoMedica.getId().getAtdSeq(), prescricaoMedica.getId().getSeq(), prescricaoMedica.getDthrInicio(), prescricaoMedica.getDthrFim(), prescricaoMedicaOld.getDthrInicioMvtoPendente(), DominioOperacaoBanco.UPD, dataFimVinculoServidor);
								//	mpmp_gera_pista_mci (l_pme_row_new.atd_seq,l_pme_row_new.seq, l_pme_row_new.dthr_inicio, l_pme_row_new.dthr_fim);
								getObjetosOracleDAO().geraPistaMci(prescricaoMedica.getId().getAtdSeq(), prescricaoMedica.getId().getSeq(), prescricaoMedica.getDthrInicio(), prescricaoMedica.getDthrFim(), usuarioLogado);
							
								if(getPacienteFacade().verificarUnidadeChecagem(prescricaoMedica.getId().getAtdSeq())){
									//mpmp_gera_mvto_ece (l_pme_row_new.atd_seq, l_pme_row_new.seq, l_pme_saved_row.dthr_inicio_mvto_pendente, l_pme_saved_row.dthr_movimento, l_pme_row_new.dthr_inicio, l_pme_row_new.dthr_fim, l_pme_row_new.dt_referencia);
									getObjetosOracleDAO().geraMvtoEce(prescricaoMedica.getId().getAtdSeq(), prescricaoMedica.getId().getSeq(), prescricaoMedicaOld.getDthrInicioMvtoPendente(), prescricaoMedicaOld.getDthrMovimento(), prescricaoMedica.getDthrInicio(), prescricaoMedica.getDthrFim(), prescricaoMedica.getDtReferencia(), usuarioLogado);
								}
						}
			}

		} else if (operacao.equals(DominioOperacaoBanco.INS)) {
			/* verifica duas prescricoes em uso */
			if(DominioSituacaoPrescricao.U.equals(prescricaoMedica.getSituacao())){
				this.rnPmepVerEmUso(prescricaoMedica.getId());
			}
			
			/* gera dispensação de medicamento */
			if(DominioSituacaoPrescricao.L.equals(prescricaoMedica.getSituacao())){
	            //mpmp_gera_disp_tot (l_pme_row_new.atd_seq, l_pme_row_new.seq, l_pme_row_new.dthr_inicio,l_pme_row_new.dthr_fim);
				getFarmaciaFacade().mpmpGeraDispTot(prescricaoMedica.getId().getAtdSeq(), prescricaoMedica.getId().getSeq(), prescricaoMedica.getDthrInicio(), prescricaoMedica.getDthrFim(), nomeMicrocomputador);
				
				//fatk_interface_mpm.rn_fatp_atu_interface(l_pme_row_new.atd_seq, l_pme_row_new.seq,l_pme_row_new.dthr_inicio,l_pme_row_new.dthr_fim,NULL, 'I');
				getFaturamentoFacade().atualizaContaHospitalar(prescricaoMedica.getId().getAtdSeq(), prescricaoMedica.getId().getSeq(), prescricaoMedica.getDthrInicio(), prescricaoMedica.getDthrFim(), null, DominioOperacaoBanco.INS, dataFimVinculoServidor);
				//mpmp_gera_pista_mci (l_pme_row_new.atd_seq,l_pme_row_new.seq, l_pme_row_new.dthr_inicio, l_pme_row_new.dthr_fim);
				getObjetosOracleDAO().geraPistaMci(prescricaoMedica.getId().getAtdSeq(), prescricaoMedica.getId().getSeq(), prescricaoMedica.getDthrInicio(), prescricaoMedica.getDthrFim(), usuarioLogado);
				
				if(getPacienteFacade().verificarUnidadeChecagem(prescricaoMedica.getId().getAtdSeq())){
				 	//mpmp_gera_ordem_adm (l_pme_row_new.atd_seq, l_pme_row_new.seq,l_pme_row_new.dthr_inicio,l_pme_row_new.dthr_fim,l_pme_row_new.dt_referencia);
					getObjetosOracleDAO().geraOrdemAdm(prescricaoMedica.getId().getAtdSeq(), prescricaoMedica.getId().getSeq(), prescricaoMedica.getDthrInicio(), prescricaoMedica.getDthrFim(), prescricaoMedica.getDtReferencia(), usuarioLogado);
				 	//mpmp_inclui_prcr_ece (l_pme_row_new.atd_seq, l_pme_row_new.seq,l_pme_row_new.dthr_inicio,l_pme_row_new.dthr_fim,l_pme_row_new.dt_referencia);
					getObjetosOracleDAO().incluiPrcrEce(prescricaoMedica.getId().getAtdSeq(), prescricaoMedica.getId().getSeq(), prescricaoMedica.getDthrInicio(), prescricaoMedica.getDthrFim(), prescricaoMedica.getDtReferencia(), usuarioLogado);
			    }
			}
		}

	}

	


	@SuppressWarnings("PMD.NPathComplexity")
	private VMpmPrescrMdtos transformObjectToVMpmPrescrMdtos(Object[] result) {
		VMpmPrescrMdtosId id = new VMpmPrescrMdtosId();
		id.setIndItemRecomendadoAlta(result[0]!=null?(String) 	result[0]:null);
		id.setAtdSeq				(result[1]!=null?(Integer) 	result[1]:null);
		id.setSeq					(result[2]!=null?(Long) 	result[2]:null);
		id.setTpFrequencia			(result[3]!=null?(Short) 	result[3]:null);
		id.setViaAdministracao		(result[4]!=null?(String) 	result[4]:null);
		id.setTpVelocid				(result[5]!=null?(Short) 	result[5]:null);
		id.setIndSeNecessario		(result[6]!=null?(String) 	result[6]:null);
		id.setIndPendente 			(result[7]!=null?(String) 	result[7]:null);
		id.setFrequencia 			(result[8]!=null?(Short) 	result[8]:null);
		id.setHrInicioAdm 			(result[9]!=null?(Date) 	result[9]:null);
		id.setDthrInicio 			(result[10]!=null?(Date) 	result[10]:null);
		id.setDthrFim 				(result[11]!=null?(Date) 	result[11]:null);
		id.setGotejo 				(result[12]!=null?(BigDecimal) result[12]:null);
		id.setDuracaoTrat			(result[13]!=null?(Short) 	result[13]:null); 
		id.setDuracaoTratAprov		(result[14]!=null?(Short) 	result[14]:null); 
		id.setDthrInicioTratamento 	(result[15]!=null?(Date) 	result[15]:null);
		id.setQtdeCorrer 			(result[16]!=null?(Byte) 	result[16]:null);
		id.setObservacao 			(result[17]!=null?(String) 	result[17]:null);
		id.setMatCodigo 			(result[18]!=null?(Integer) 	result[18]:null);
		id.setSeqItem 				(result[19]!=null?(Short) 	result[19]:null);
		id.setDoseCalculada			(result[20]!=null?(BigDecimal) result[20]:null); 
		id.setIndUsoMdto 			(result[21]!=null?(String) 	result[21]:null);
		id.setDescrComplementar		(result[22]!=null?(String) 	result[22]:null); 
		id.setFdsDose				(result[23]!=null?(Integer) 	result[23]:null);
		id.setDose 					(result[24]!=null?(BigDecimal) result[24]:null);
		id.setQtdeCalc24h			(result[25]!=null?(Short) 	result[25]:null); 
		id.setIndSolucao			(result[26]!=null?(String) 	result[26]:null);
		id.setMedDescricaoEdit		(result[27]!=null?(String) 	result[27]:null); 
		id.setUnidDose 				(result[28]!=null?(String) 	result[28]:null);
		id.setUnidDoseEdit			(result[29]!=null?(String) 	result[29]:null); 
		id.setFreqEdit				(result[30]!=null?(String) 	result[30]:null);
		id.setJumSeq 				(result[31]!=null?(Integer) 	result[31]:null);
		id.setIndMdtoAguardaEntrega	(result[32]!=null?(String) 	result[32]:null); 
		id.setIndUsoMdtoAntimicrob 	(result[33]!=null?(String) 	result[33]:null);
		id.setQtdeMgKg 				(result[34]!=null?(BigDecimal) result[34]:null);
		id.setQtdeMgSuperfCorporal	(result[35]!=null?(BigDecimal) result[35]:null);
		id.setTipoCalcDose 			(result[36]!=null?(String) 	result[36]:null);
		id.setObservacaoItem		(result[37]!=null?(String) 	result[37]:null); 
		id.setCriadoEm				(result[38]!=null?(Date) 	result[38]:null);
		id.setIndControlado			(result[39]!=null?(String) 	result[39]:null); 
		id.setIndOrigemJustif		(result[40]!=null?(String) 	result[40]:null); 
		id.setPmdAtdSeq				(result[41]!=null?(Integer) 	result[41]:null);
		id.setPmdSeq 				(result[42]!=null?(Long) 	result[42]:null);
		id.setAlteradoEm 			(result[43]!=null?(Date) 	result[43]:null);
		id.setSerMatriculaValida	(result[44]!=null?(Integer) 	result[44]:null);
		id.setSerVinCodigoValida 	(result[45]!=null?(Short) 	result[45]:null);
		id.setIndAntimicrobiano		(result[46]!=null?(String) 	result[46]:null);
		id.setIndQuimioterapico 	(result[47]!=null?(String) 	result[47]:null);
		id.setMedPrcrDescCompleta	(result[48]!=null?(String) 	result[48]:null);
		id.setUnidHorasCorrer		(result[49]!=null?(String) 	result[49]:null);
		id.setVolumeDiluenteMl		(result[50]!=null?(BigDecimal) result[50]:null);
		id.setMedMatCodigoDiluente 	(result[51]!=null?(Integer) 	result[51]:null);
		id.setQtdeParamCalculo 		(result[52]!=null?(BigDecimal) result[52]:null);
		id.setBaseParamCalculo 		(result[53]!=null?(String) 	result[53]:null);
		id.setUmmSeqCalculo 		(result[54]!=null?(Integer) 	result[54]:null);
		id.setDuracaoParamCalculo	(result[55]!=null?(Integer) 	result[55]:null);
		id.setUnidDuracaoCalculo	(result[56]!=null?(String) 	result[56]:null);
		id.setPcaAtdSeq				(result[57]!=null?(Integer) 	result[57]:null);
		id.setPcaCriadoEm			(result[58]!=null?(Date) 	result[58]:null);
		id.setIndBombaInfusao		(result[59]!=null?(String) 	result[59]:null);
		
		VMpmPrescrMdtos presc = new VMpmPrescrMdtos();
		presc.setId(id);
		
		if(id.getTpFrequencia() !=null){
			presc.setTipoFrequenciaAprazamento(getMpmTipoFrequenciaAprazamentoDAO().obterTipoFrequenciaAprazamentoPeloId(id.getTpFrequencia()));
		}
		if(id.getTpVelocid() !=null){
			presc.setTipoVelocidadeAdministracao(getFarmaciaFacade().obterAfaTipoVelocAdministracoesDAO(id.getTpVelocid()));
		}
		return presc;
	}
	
	/**
	 * @param imePmdAtdSeq
	 *            afa_dispensacao_mdtos.ime_pmd_atd_seq
	 * @param vPmdSeq
	 *            afa_dispensacao_mdtos.ime_pmd_seq
	 * @param imeMedMatCodigo
	 *            afa_dispensacao_mdtos.ime_med_mat_codigo
	 * @param imeSeqp
	 *            afa_dispensacao_mdtos.ime_seqp
	 * @return
	 */
	public VMpmPrescrMdtos obtemPrescMdto(Integer imePmdAtdSeq, Long vPmdSeq,
			Integer imeMedMatCodigo, Short imeSeqp) {
		
		Object[] result = getVMpmPrescrMdtosDAO().obtemPrescMdto(imePmdAtdSeq,
				vPmdSeq, imeMedMatCodigo, imeSeqp);
		
		return transformObjectToVMpmPrescrMdtos(result);
	}

    /**
	 * ORADB MPMC_PAC_PRV_ALTA_48H
	 * 
	 * Verifica se o paciente está com previsão de alta nas próximas XX hors (48);
	 *  
	 * Implementação da função  MPMC_PAC_PRV_ALTA_48H.
	 * 
	 * @author andremachado - 09/03/2012
     * @param aghAtendimentos
     * @return
     */
	public boolean verificaPrevisaoAltaProxima (AghAtendimentos aghAtendimentos ) {
		return listaPacientesInternadosON.verificarPossuiPlanoAlta(aghAtendimentos);
	}
	
	/**
	 * @ORADB mpmk_pme_rn.rn_pmep_ver_em_uso - PACKAGE  mpmk_pme_rn - FUNCTION rn_pmep_ver_em_uso
	 * 
	 */
	public void rnPmepVerEmUso(MpmPrescricaoMedicaId id) throws BaseException {
		if(getMpmPrescricaoMedicaDAO().obterQuantidadePrescricoesMedicasEmUsoPeloId(id) > 0) {
			throw new ApplicationBusinessException(
					PrescricaoMedicamentoExceptionCode.MPM_02290);			
		}
	}
	
	/**
	 * @ORADB mpmk_pme_rn.rn_pmep_ver_pos_alta - PACKAGE  mpmk_pme_rn - FUNCTION rn_pmep_ver_pos_alta
	 * 
	 */
	public void rnPmepVerPosAlta(Integer atdSeq) throws BaseException {
		if(getMpmSumarioAltaDAO().obterQuantidadeSumarioAltaComMotivoAlta(atdSeq) > 0) {
			throw new ApplicationBusinessException(
					PrescricaoMedicamentoExceptionCode.MPM_01067);
		}
	}
	
	
	/**
	 * ORADB MPMC_PAC_PRV_ALTA_48H - FUNCTION c_analisa_atend
	 * 
	 * Para um Atendimento verifica se existe previsão de alta nas próximas horas.
	 *  
	 * Implementação da função  MPMC_PAC_PRV_ALTA_48H - FUNCTION c_analisa_atend
	 * 
	 * @author andremachado - 09/03/2012
	 * @param aghAtendimentos
	 * @return
	 */
	public boolean analisaAtendimentoPrevisaoAltaProxima(
			AghAtendimentos aghAtendimentos) {

		boolean retorno = false;
		MpmControlPrevAltas mpmControlPrevAltas = new MpmControlPrevAltas();


		mpmControlPrevAltas = this
				.getMpmControlPrevAltasDAO()
				.obterUltimoControlePrevisaoAltasPorAtendimento(aghAtendimentos);

		if (mpmControlPrevAltas != null) {

			if  ("S".equalsIgnoreCase(mpmControlPrevAltas.getResposta())) {

				// pega o dia de hoje
				Date dataDia  = DateUtils.truncate(new Date(), Calendar.DATE);
				Date DtInicio = DateUtils.truncate(mpmControlPrevAltas.getDtInicio(), Calendar.DATE);
				Date DtFim    = DateUtils.truncate(mpmControlPrevAltas.getDtFim(),Calendar.DATE);
				
				if (CoreUtil.isBetweenDatas(dataDia, DtInicio, DtFim )) {
					retorno = true;
				} else {
					retorno = false;
				}
			} else {
				retorno = false;
			}
		}
		return retorno;
	}
	
	public void inserirJustificativaNPT(MpmJustificativaNpt justificativaNpt) throws ApplicationBusinessException {
		justificativaNpt.setCodSus(Short.valueOf("1"));
		
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		if (servidorLogado != null) {
			justificativaNpt.setServidor(servidorLogado);
			justificativaNpt.setServidorAlterado(servidorLogado);
		} 
		
		justificativaNpt.setCriadoEm(new Date());
		justificativaNpt.setAlteradoEm(new Date());
		
		this.getMpmJustificativaNptDAO().persistir(justificativaNpt);
	}
		
	//#3509 INICIO
	public void excluirMensagemCalculoNpt(AfaMensCalculoNpt item){
		item = afaMensCalculoNptDAO.obterPorChavePrimaria(item.getSeq());
		afaMensCalculoNptDAO.remover(item);
		afaMensCalculoNptDAO.flush();
	}
	
	public void salvarMensagemCalculoNpt(AfaMensCalculoNpt item, RapServidores servidorLogado) throws BaseException, ApplicationBusinessException {
		validarDescricao(item.getDescricao());
		if(item != null && item.getSeq() != null){
			item.setAlteradoEm(new Date());
			item.setSerMatriculaAlteradoPor(servidorLogado.getId().getMatricula());
			item.setSerVinCodigoAlteradoPor(servidorLogado.getVinculo().getCodigo());
			afaMensCalculoNptDAO.atualizar(item);
		}else{
			item.setCriadoEm(new Date());
			item.setSerMatricula(servidorLogado.getId().getMatricula());
			item.setSerVinCodigo(servidorLogado.getVinculo().getCodigo());
			afaMensCalculoNptDAO.persistir(item);
		}
	}
	
	private void validarDescricao(String descricao) throws ApplicationBusinessException{
		if(descricao == null || descricao.equals("")){
			throw new ApplicationBusinessException(PrescricaoMedicamentoExceptionCode.PREENCHER_CAMPOS_OBRIGATORIOS);
		}
	}

	public boolean verificarPacienteInternadoCaracteristicaControlePrevisao(Integer atendimentoSeq) throws ApplicationBusinessException{
		String parametro = null;
		AghParametros aghParametros = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_CONTROLE_ALTA);		
		
		if (aghParametros != null && aghParametros.getNome() != null) {
			parametro = aghParametros.getNome();
		}
	
		List<Short> unidadesFuncionaisSeq = this.aghuFacade.pesquisarUnidadesFuncionaisPorCaracteristica(parametro);
		
		if(unidadesFuncionaisSeq != null && !unidadesFuncionaisSeq.isEmpty()){
			return this.getAghuFacade().verificarPacienteInternadoCaracteristicaControlePrevisao(unidadesFuncionaisSeq, atendimentoSeq);
		}
		return false;
	}

	
	public void atualizarVisualizacaoParecer(Integer atdSeq, ParecerPendenteVO parecerPendenteVO) throws BaseException {
		
		List<MpmParecerUsoMdto> listaPareceres = mpmParecerUsoMdtosDAO.listarParecerVisualizacao(atdSeq);
		if (listaPareceres != null && !listaPareceres.isEmpty()) {
			for (MpmParecerUsoMdto mpmParecerUsoMdto : listaPareceres) {
				if(mpmParecerUsoMdto.getSeq().intValue() == parecerPendenteVO.getSeq().intValue()){
					mpmParecerUsoMdto.setIndParecerVerificado(DominioSimNao.S);
					mpmParecerUsoMdtosDAO.atualizar(mpmParecerUsoMdto);
				}
			}
		}
	}
	
	
	public void atualizarVisualizacaoInformacaoPrescribente(MpmInformacaoPrescribente informacaoPrescribente, RapServidores servidorLogado) throws BaseException {
		MpmInformacaoPrescribente informacaoPrescribenteOriginal = mpmInformacaoPrescribenteDAO.obterOriginal(informacaoPrescribente);
		
		if (informacaoPrescribenteOriginal != null) {
			informacaoPrescribenteOriginal.setIndInfVerificada(true);
			informacaoPrescribenteOriginal.setDthrInfVerificada(new Date());
			informacaoPrescribenteOriginal.setServidorVerificado(servidorLogado);
			informacaoPrescribenteOriginal.setSerMatriculaVerificada(servidorLogado.getId().getMatricula());
			informacaoPrescribenteOriginal.setSerVinCodigoVerificada(servidorLogado.getId().getVinCodigo());
			mpmInformacaoPrescribenteDAO.atualizar(informacaoPrescribenteOriginal);
			mpmInformacaoPrescribenteDAO.flush();
		}
		
		
		
	}
	
	public MpmJustificativaNptDAO getMpmJustificativaNptDAO() {
		return mpmJustificativaNptDAO;
	}
	
	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}

	protected IFarmaciaFacade getFarmaciaFacade() {
		return this.farmaciaFacade;
	}

	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}

	protected MpmTipoFrequenciaAprazamentoDAO getMpmTipoFrequenciaAprazamentoDAO() {
		return mpmTipoFrequenciaAprazamentoDAO;
	}

	protected MpmPrescricaoMedicaDAO getMpmPrescricaoMedicaDAO() {
		return mpmPrescricaoMedicaDAO;
	}

	protected MpmPacAtendProfissionalDAO getMpmPacAtendProfissionalDAO() {
		return mpmPacAtendProfissionalDAO;
	}

	private MpmListaServSumrAltaDAO getMpmListaServSumrAltaDAO() {
		return mpmListaServSumrAltaDAO;
	}

	protected IInternacaoFacade getInternacaoFacade() {
		return this.internacaoFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected MpmLaudoDAO getMpmLaudoDAO() {
		return mpmLaudoDAO;
	}

	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}
	
	protected IExamesLaudosFacade getExamesLaudosFacade() {
		return this.examesLaudosFacade;
	}

	private MpmEscalaGlasglowDAO getEscalaGlasglowDAO() {
		return mpmEscalaGlasglowDAO;
	}

	private VMpmPrescrMdtosDAO getVMpmPrescrMdtosDAO() {
		return vMpmPrescrMdtosDAO;
	}
	
	protected MpmControlPrevAltasDAO getMpmControlPrevAltasDAO() {
		return mpmControlPrevAltasDAO;
	}
	
	protected MpmSumarioAltaDAO getMpmSumarioAltaDAO() {
		return mpmSumarioAltaDAO;
	}

	protected ObjetosOracleDAO getObjetosOracleDAO() {
		return objetosOracleDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
	public void atualizarJustificativaNPT(MpmJustificativaNpt justificativaNpt) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		justificativaNpt.setServidorAlterado(servidorLogado);
		this.getMpmJustificativaNptDAO().merge(justificativaNpt);
	}
	
	public void setMpmJustificativaNptDAO(MpmJustificativaNptDAO mpmJustificativaNptDAO) {
		this.mpmJustificativaNptDAO = mpmJustificativaNptDAO;
	}
	
	public void gravarComponenteNpt(AfaComponenteNpt entity, Integer matCodigo, Short gcnSeq) throws ApplicationBusinessException{
		// Regra que o analista esqueceu
		AfaComponenteNpt compo = afaComponenteNptDAO.obterPorChavePrimaria(matCodigo);
		if(compo != null){
			if(compo.getMedMatCodigo() != null){
				throw new ApplicationBusinessException(PrescricaoMedicamentoExceptionCode.MSG_COMPONENTE_EXISTE);
}
		}
		
		// AFAT_CNP_BRI
		AfaMedicamento medicamento = afaMedicamentoDAO.obterPorChavePrimaria(matCodigo);
		if(!(DominioSituacaoMedicamento.A.equals(medicamento.getIndSituacao()))){
			throw new ApplicationBusinessException(PrescricaoMedicamentoExceptionCode.AFA_00197);
		}
		
		entity.setAfaMedicamentos(medicamento);
		entity.setMedMatCodigo(medicamento.getMatCodigo());
		if(gcnSeq != null){
			AfaGrupoComponenteNpt grupo = afaGrupoComponenteNptDAO.obterPorChavePrimaria(gcnSeq);
			entity.setAfaGrupoComponenteNpt(grupo);
			entity.setGcnSeq(grupo.getSeq());
		}

		entity.setCriadoEm(new Date());
		entity.setIndImpDoseSumario(true);
		
		RapServidores servidor = servidorLogadoFacade.obterServidorLogado();
		entity.setServidor(servidor);
		
		afaComponenteNptDAO.persistir(entity);
	}

	
	public void gravarParamComponenteNpt(AfaParamComponenteNpt entity) throws ApplicationBusinessException{
		boolean grava = true;
		if(DominioSituacao.A.equals(entity.getIndSituacao())){
		List<AfaParamComponenteNpt> lista = afaParamComponenteNptDAO.obterAtivosPorMatCodigo(entity.getId().getCnpMedMatCodigo(),null);
		if(lista != null){
			if(lista.size() > 0){
				grava = false;
				throw new ApplicationBusinessException(PrescricaoMedicamentoExceptionCode.AFA_01208);
			}
		}
		}
		
		if(grava){
			entity.setCriadoEm(new Date());
			
			RapServidores servidor = servidorLogadoFacade.obterServidorLogado();
			entity.setSerMatricula(servidor.getId().getMatricula());
			entity.setSerVinCodigo(servidor.getId().getVinCodigo());
			entity.getId().setSeqp(geraSequenceParam(entity.getId().getCnpMedMatCodigo()));
			afaParamComponenteNptDAO.persistir(entity);
		}
	}
	
	public void gravarCasaComponenteNpt(AfaDecimalComponenteNpt entity) throws ApplicationBusinessException{
		entity.setCriadoEm(new Date());
		
		RapServidores servidor = servidorLogadoFacade.obterServidorLogado();
		entity.setSerMatricula(servidor.getId().getMatricula());
		entity.setSerVinCodigo(servidor.getId().getVinCodigo());

		entity.getId().setSeqp(geraSequenceCasa(entity.getId().getCnpMedMatCodigo()));
		afaDecimalComponenteNptDAO.persistir(entity);
	}
	
	public void atualizarCasaComponenteNpt(AfaDecimalComponenteNpt entity) throws ApplicationBusinessException{
		
		AfaDecimalComponenteNpt item = afaDecimalComponenteNptDAO.obterPorChavePrimaria(entity.getId());

		item.setIndSituacao(entity.getIndSituacao());
		item.setNroCasasDecimais(entity.getNroCasasDecimais());
		item.setPesoFinal(entity.getPesoFinal());
		item.setPesoInicial(entity.getPesoInicial());

		item.setAlteradoEm(new Date());
		
		RapServidores servidor = servidorLogadoFacade.obterServidorLogado();
		item.setSerMatriculaAlteradoPor(servidor.getId().getMatricula());
		item.setSerVinCodigoAlteradoPor(servidor.getId().getVinCodigo());

		afaDecimalComponenteNptDAO.merge(item);
	}
	
	public void removerParamComponenteNpt(AfaParamComponenteNpt entity) throws ApplicationBusinessException{
		AfaParamComponenteNpt item = afaParamComponenteNptDAO.obterPorChavePrimaria(entity.getId());
		List<MpmItemPrescricaoNpt> itemP = mpmItemPrescricaoNptDAO.listarItensPorMatCodigoSeqp(entity.getId().getCnpMedMatCodigo(),entity.getId().getSeqp());
		if(itemP != null){
			if(itemP.size() > 0){
				throw new ApplicationBusinessException(PrescricaoMedicamentoExceptionCode.MSG_CONSTRAINT1_PARAM);
			}
		}
		if(item != null){
			afaParamComponenteNptDAO.remover(item);
		}
	}
	
	public void removerCasaComponenteNpt(AfaDecimalComponenteNpt entity) throws ApplicationBusinessException{
		AfaDecimalComponenteNpt item = afaDecimalComponenteNptDAO.obterPorChavePrimaria(entity.getId());
		if(item != null){
			afaDecimalComponenteNptDAO.remover(item);
		}
	}
	
	public void atualizarParamComponenteNpt(AfaParamComponenteNpt entity) throws ApplicationBusinessException{
		boolean atualiza = true;
		if(DominioSituacao.A.equals(entity.getIndSituacao())){
		List<AfaParamComponenteNpt> lista = afaParamComponenteNptDAO.obterAtivosPorMatCodigo(entity.getId().getCnpMedMatCodigo(),entity.getId().getSeqp());
		if(lista != null){
			if(lista.size() > 0){
				atualiza = false;
				throw new ApplicationBusinessException(PrescricaoMedicamentoExceptionCode.AFA_01208);
			}
		}
		}

		if(atualiza){
			AfaParamComponenteNpt item = afaParamComponenteNptDAO.obterPorChavePrimaria(entity.getId());

			item.setFatorConversaoMl(entity.getFatorConversaoMl());
			item.setFatorConvMlFosforo(entity.getFatorConvMlFosforo());
			item.setFatorConvMlMosm(entity.getFatorConvMlMosm());
			item.setFatorConvUnidCalorias(entity.getFatorConvUnidCalorias());
			item.setFatorConvUnidNitrogenio(entity.getFatorConvUnidNitrogenio());
			item.setIndCalculaVolume(entity.getIndCalculaVolume());
			item.setIndSituacao(entity.getIndSituacao());
			item.setUmmSeq(entity.getUmmSeq());
			item.setTipoCaloria(entity.getTipoCaloria());
			item.setTipoParamCalculo(entity.getTipoParamCalculo());
			item.setVolumeMaximoMl(entity.getVolumeMaximoMl());

			item.setAlteradoEm(new Date());
			
			RapServidores servidor = servidorLogadoFacade.obterServidorLogado();
			item.setSerMatriculaAlteradoPor(servidor.getId().getMatricula());
			item.setSerVinCodigoAlteradoPor(servidor.getId().getVinCodigo());
			
			afaParamComponenteNptDAO.merge(item);
		}
	}
	
	
	
	public void removerComponenteNpt(ComponenteNPTVO selecionado) throws ApplicationBusinessException{
		// #3504
		// PC8
		
//		List<AfaItemProducaoNpt> listaItem1 = afaItemProducaoNptDAO.listarPorMatCodigo(selecionado.getMedMatCodigo());
//		if(listaItem1 != null){
//			if(listaItem1.size() > 0){
//				throw new ApplicationBusinessException(PrescricaoMedicamentoExceptionCode.MESSAGE_DEPENDENTE1);
//			}
//		}
		List<MpmItemPrescricaoNpt> listaItem2 = mpmItemPrescricaoNptDAO.listarItensPorMatCodigo(selecionado.getMedMatCodigo());
		if(listaItem2 != null){
			if(listaItem2.size() > 0){
				throw new ApplicationBusinessException(PrescricaoMedicamentoExceptionCode.MESSAGE_DEPENDENTE2);
			}
		}
		List<AfaItemNptPadrao> listaItem3 = afaItemNptPadraoDAO.listarPorMatCodigo(selecionado.getMedMatCodigo());
		if(listaItem3 != null){
			if(listaItem3.size() > 0){
				throw new ApplicationBusinessException(PrescricaoMedicamentoExceptionCode.MESSAGE_DEPENDENTE3);
			}
		}

		// #3504
		// TRIGGER
		AghParametros aghParametro = getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_DIAS_PERM_DEL_AFA);
		
		if (aghParametro != null && aghParametro.getVlrNumerico() != null) {
			float diff = CoreUtil.diferencaEntreDatasEmDias(Calendar
					.getInstance().getTime(), selecionado.getCriadoEm());
			if (diff > aghParametro.getVlrNumerico().floatValue()) {
				throw new ApplicationBusinessException(PrescricaoMedicamentoExceptionCode.AFA_00172);
			}
		} else {
			throw new ApplicationBusinessException(PrescricaoMedicamentoExceptionCode.AFA_00173);
		}
		
		
		
		AfaComponenteNpt compo = afaComponenteNptDAO.obterPorChavePrimaria(selecionado.getMedMatCodigo());
		if(compo != null){
			RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
			final AfaComponenteNptJn journal = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.DEL, AfaComponenteNptJn.class, servidorLogado.getUsuario());
			journal.setAfaGrupoComponenteNpt(compo.getAfaGrupoComponenteNpt());
			journal.setCriadoEm(compo.getCriadoEm());
			journal.setDescricao(compo.getDescricao());
			journal.setDescricaoMedicamento(compo.getDescricaoMedicamento());
			journal.setGcnSeq(compo.getGcnSeq());
			
			if(compo.getIdentifComponente() != null){
				journal.setIdentifComponente(compo.getIdentifComponente());
			}
			journal.setIndAdulto(compo.getIndAdulto());
			if(compo.getIndImpDoseSumario() != null){
				if(compo.getIndImpDoseSumario()){
					journal.setIndImpDoseSumario("S");
				}else{
					journal.setIndImpDoseSumario("N");
				}
				
			}
			
			journal.setIndPediatria(compo.getIndPediatria());
			journal.setIndSituacao(compo.getIndSituacao());
			journal.setMedMatCodigo(compo.getMedMatCodigo());
			journal.setObservacao(compo.getObservacao());
			journal.setOrdem(compo.getOrdem());
			journal.setServidor(compo.getServidor());

			afaComponenteNptJnDAO.persistir(journal);		
			
			afaComponenteNptDAO.remover(compo);
		}
	}
	
	public void atualizarComponenteNpt(AfaComponenteNpt entity, Integer matCodigo, Short gcnSeq) throws ApplicationBusinessException{
		AfaComponenteNpt componente = afaComponenteNptDAO.obterPorChavePrimaria(matCodigo);
		
		if(DominioSituacao.I.equals(entity.getIndSituacao()) && DominioSituacao.A.equals(componente.getIndSituacao())){
			List<AfaFormulaNptPadrao> listaFormula = afaComponenteNptDAO.verificaDelecaoComponente(matCodigo);
			if(listaFormula != null){
				if(listaFormula.size() > 0){
					throw new ApplicationBusinessException(PrescricaoMedicamentoExceptionCode.AFA_00199);		 
				}
			}
		}
		
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		final AfaComponenteNptJn journal = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.DEL, AfaComponenteNptJn.class, servidorLogado.getUsuario());
		journal.setAfaGrupoComponenteNpt(componente.getAfaGrupoComponenteNpt());
		journal.setCriadoEm(componente.getCriadoEm());
		journal.setDescricao(componente.getDescricao());
		journal.setDescricaoMedicamento(componente.getDescricaoMedicamento());
		journal.setGcnSeq(componente.getGcnSeq());
		if(componente.getIdentifComponente() != null){
			journal.setIdentifComponente(componente.getIdentifComponente());
		}
		if(componente.getIndImpDoseSumario() != null){
			if(componente.getIndImpDoseSumario()){
				journal.setIndImpDoseSumario("S");
			}else{
				journal.setIndImpDoseSumario("N");
			}
		}
		journal.setIndAdulto(componente.getIndAdulto());
		journal.setIndPediatria(componente.getIndPediatria());
		journal.setIndSituacao(componente.getIndSituacao());
		journal.setMedMatCodigo(componente.getMedMatCodigo());
		journal.setObservacao(componente.getObservacao());
		journal.setOrdem(componente.getOrdem());
		journal.setServidor(componente.getServidor());

		afaComponenteNptJnDAO.persistir(journal);		
		
		
		AfaGrupoComponenteNpt grupo = afaGrupoComponenteNptDAO.obterPorChavePrimaria(gcnSeq);
		componente.setAfaGrupoComponenteNpt(grupo);
		componente.setGcnSeq(grupo.getSeq());

		componente.setOrdem(entity.getOrdem());
		componente.setObservacao(entity.getObservacao());
		componente.setIdentifComponente(entity.getIdentifComponente());
		componente.setIndAdulto(entity.getIndAdulto());
		componente.setIndSituacao(entity.getIndSituacao());
		componente.setIndPediatria(entity.getIndPediatria());
		if(entity.getIndImpDoseSumario() == null){
			componente.setIndImpDoseSumario(true);
		}else{
		componente.setIndImpDoseSumario(entity.getIndImpDoseSumario());
		}

		RapServidores servidor = servidorLogadoFacade.obterServidorLogado();
		componente.setServidor(servidor);
		
		afaComponenteNptDAO.merge(componente);
	}
	
	public void verificarDelecao(Date data) throws ApplicationBusinessException {
		AghParametros aghParametro = getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_DIAS_PERM_DEL_AFA);

		if (aghParametro != null && aghParametro.getVlrNumerico() != null) {
			float diff = CoreUtil.diferencaEntreDatasEmDias(Calendar
					.getInstance().getTime(), data);
			if (diff > aghParametro.getVlrNumerico().floatValue()) {
				throw new ApplicationBusinessException(PrescricaoMedicamentoExceptionCode.AFA_00172);
			}
		} else {
			throw new ApplicationBusinessException(PrescricaoMedicamentoExceptionCode.AFA_00173);
		}
	}
	
	public Short geraSequenceParam(Integer matCodigo){
		Short id = prescricaoMedicaFacade.obterParamComponenteCount(matCodigo);
		id++;
		return id;
}
	
	public Short geraSequenceCasa(Integer matCodigo){
		Short id= prescricaoMedicaFacade.obterCasaComponenteCount(matCodigo);
		id++;
		return id;
	}
}
