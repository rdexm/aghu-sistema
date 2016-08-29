package br.gov.mec.aghu.prescricaomedica.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dao.SequenceID;
import br.gov.mec.aghu.dominio.DominioCaracEspecialidade;
import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.dominio.DominioOperacaoBanco;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoPim2;
import br.gov.mec.aghu.dominio.DominioSituacaoPrescricao;
import br.gov.mec.aghu.dominio.DominioSituacaoSaps3;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghCaractEspecialidades;
import br.gov.mec.aghu.model.AghCaractEspecialidadesId;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MamAnamneses;
import br.gov.mec.aghu.model.McoNascimentos;
import br.gov.mec.aghu.model.MpaUsoOrdCuidado;
import br.gov.mec.aghu.model.MpaUsoOrdMdto;
import br.gov.mec.aghu.model.MpaUsoOrdNutricao;
import br.gov.mec.aghu.model.MpaUsoOrdProcedimento;
import br.gov.mec.aghu.model.MpmComposicaoPrescricaoNpt;
import br.gov.mec.aghu.model.MpmComposicaoPrescricaoNptId;
import br.gov.mec.aghu.model.MpmEscoreSaps3;
import br.gov.mec.aghu.model.MpmItemPrescParecerMdto;
import br.gov.mec.aghu.model.MpmItemPrescParecerMdtoId;
import br.gov.mec.aghu.model.MpmItemPrescricaoDieta;
import br.gov.mec.aghu.model.MpmItemPrescricaoDietaId;
import br.gov.mec.aghu.model.MpmItemPrescricaoMdto;
import br.gov.mec.aghu.model.MpmItemPrescricaoMdtoId;
import br.gov.mec.aghu.model.MpmItemPrescricaoNpt;
import br.gov.mec.aghu.model.MpmItemPrescricaoNptId;
import br.gov.mec.aghu.model.MpmModoUsoPrescProced;
import br.gov.mec.aghu.model.MpmMotivoIngressoCti;
import br.gov.mec.aghu.model.MpmParecerUsoMdto;
import br.gov.mec.aghu.model.MpmPim2;
import br.gov.mec.aghu.model.MpmPrescricaoCuidado;
import br.gov.mec.aghu.model.MpmPrescricaoCuidadoId;
import br.gov.mec.aghu.model.MpmPrescricaoDieta;
import br.gov.mec.aghu.model.MpmPrescricaoDietaId;
import br.gov.mec.aghu.model.MpmPrescricaoMdto;
import br.gov.mec.aghu.model.MpmPrescricaoMdtoId;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmPrescricaoMedicaId;
import br.gov.mec.aghu.model.MpmPrescricaoNpt;
import br.gov.mec.aghu.model.MpmPrescricaoNptId;
import br.gov.mec.aghu.model.MpmPrescricaoProcedimento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.perinatologia.business.IPerinatologiaFacade;
import br.gov.mec.aghu.prescricaomedica.dao.MpmComposicaoPrescricaoNptDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmEscoreSaps3DAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemPrescParecerMdtoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemPrescricaoDietaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemPrescricaoMdtoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemPrescricaoNptDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmModoUsoPrescProcedDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmMotivoIngressoCtiDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmParecerUsoMdtosDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPim2DAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoCuidadoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoDietaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoMdtoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoMedicaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoNptDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoProcedimentoDAO;
import br.gov.mec.aghu.prescricaomedica.vo.RedirectVO;
import br.gov.mec.aghu.protocolo.business.IProtocoloFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.ExcessiveClassLength"})
@Stateless
public class VerificarPrescricaoON extends BaseBusiness {

	@EJB
	private ManterModoUsoPrescProcedRN manterModoUsoPrescProcedRN;
	
	@EJB
	private ManterPrescricaoCuidadoON manterPrescricaoCuidadoON;
	
	@EJB
	private ManterPrescricaoDietaRN manterPrescricaoDietaRN;
	
	@EJB
	private PrescricaoMedicaRN prescricaoMedicaRN;
	
	@EJB
	private PrescreverProcedimentoEspecialRN prescreverProcedimentoEspecialRN;
	
	private static final Log LOG = LogFactory.getLog(VerificarPrescricaoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IParametroFacade parametroFacade; 
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IPerinatologiaFacade perinatologiaFacade;
	
	@Inject
	private MpmItemPrescricaoDietaDAO mpmItemPrescricaoDietaDAO;
	
	@Inject
	private MpmModoUsoPrescProcedDAO mpmModoUsoPrescProcedDAO;
	
	@EJB
	private IProtocoloFacade protocoloFacade;
	
	@Inject
	private MpmPrescricaoMedicaDAO mpmPrescricaoMedicaDAO;
	
	@Inject
	private MpmPrescricaoDietaDAO mpmPrescricaoDietaDAO;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@Inject
	private MpmItemPrescParecerMdtoDAO mpmItemPrescParecerMdtoDAO;
	
	@Inject
	private MpmPrescricaoProcedimentoDAO mpmPrescricaoProcedimentoDAO;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private ICascaFacade cascaFacade;
	
	@EJB
	private IFarmaciaFacade farmaciaFacade;
	
	@Inject
	private MpmPrescricaoCuidadoDAO mpmPrescricaoCuidadoDAO;
	
	@Inject
	private MpmItemPrescricaoMdtoDAO mpmItemPrescricaoMdtoDAO;
	
	@Inject
	private MpmPrescricaoMdtoDAO mpmPrescricaoMdtoDAO;

	@Inject
	private MpmPim2DAO mpmPim2DAO;
	
	@Inject
	private MpmMotivoIngressoCtiDAO mpmMotivoIngressoCtiDAO;
	
	@Inject
	private MpmEscoreSaps3DAO mpmEscoreSaps3DAO;
	
	@Inject
	private MpmPrescricaoNptDAO mpmPrescricaoNptDAO;
	
	@Inject
	private MpmComposicaoPrescricaoNptDAO mpmComposicaoPrescricaoNptDAO;
	
	@Inject
	private MpmItemPrescricaoNptDAO mpmItemPrescricaoNptDAO;
	
	@Inject
	private MpmParecerUsoMdtosDAO mpmParecerUsoMdtosDAO;
	/**
	 * 
	 */
	private static final long serialVersionUID = -5204012412727427651L;

	public enum ExceptionCode implements BusinessExceptionCode {
		OUTRA_PRESCRICAO_EM_USO, // MPM-02290
		PRESCRICOES_SOBREPOSTAS, //MPM-01218
		PRESCRICAO_NO_PASSADO, //MPM-01222 
		PRESCRICAO_NAO_INFORMATIZADA, //MPM-01980
		TIPO_ATENDIMENTO_NAO_PERMITE_PRESCRICAO, // MPM-01340
		ATENDIMENTO_NAO_VIGENTE_PARA_PERIODO, // AGH-00199
		PRESCRICAO_CONSECUTIVA_NAO_ENCONTRADA, // MPM-01191
		NAO_EH_POSSIVEL_ADIANTAR_PRESCRICAO, NAO_EH_PERMITIDO_ADIANTAR_PRESCRICAO, // MPM-01194
		ATENDIMENTO_NAO_ESTA_EM_ANDAMENTO, ATENDIMENTO_PRESCRICAO_NULO, DT_REFERENCIA_PRESCRICAO_NULA, DT_INICIO_PRESCRICAO_NULA, ERRO_COPIAR_PRESCRICAO_CUIDADOS, ERRO_COPIAR_PRESCRICAO_DIETAS, ERRO_COPIAR_PRESCRICAO_MEDICAMENTOS, ERRO_COPIAR_PRESCRICAO_PROCEDIMENTOS, ERRO_COPIAR_USO_ORD_NUTRICOES, ERRO_COPIAR_USO_ORD_CUIDADOS, 
		MAIS_DE_UM_ATENDIMENTO_ENCONTRADO, NENHUM_ATENDIMENTO_ENCONTRADO, PRESCRICAO_ATUAL_EM_USO,
		SNAPPE_II, RN_EXAME_FISICO, REL_RN, REL_EXAME_FISICO, PRESCRICAO_GESTANTE_I, PRESCRICAO_GESTANTE_II,
		ERRO_ATUALIZACAO_PRES_NPT, ERRO_INCLUSAO_ITEM_NPT, ERRO_INCLUSAO_COMPOSICAO_NPT, ERRO_INCLUSAO_PRESCRICAO_NPT, MPM_04174;
	}

	private static final String PREENCHER_PMI_2 = "prescricaomedica-preencherPim2"; 
	private static final String PREENCHER_ESCORE_GRAVIDADE = "prescricaomedica-preencherEscoreGravidade";
	private static final String COMPONENTE_PRESCRICAO_MEDICA = "prescricaoMedica";
    private static final String METODO_PRESCRICAO_MEDICA =	"confirmar";	
	

	/**
	 * Busca atendimento atual do paciente através do número do prontuário
	 * 
	 * @param prontuario
	 * @return atendimento
	 */
	public AghAtendimentos obterAtendimentoAtualPorProntuario(
			final Integer prontuario) {
		AipPacientes paciente = getPacienteFacade().obterPacientePorProntuario(
				prontuario);
		// Antes do Merge/ migracao
		//return getAghuFacade().obterAtendimentoVigenteDetalhado(paciente);
		// codigo do merge / migracao
		return getAghuFacade().obterAtendimentoAtual(paciente);
	}
	
	public AghAtendimentos obterAtendimentoAtualPorProntuarioLeito(
			final Integer prontuario) {
		AipPacientes paciente = getPacienteFacade().obterPacientePorProntuarioLeito(
				prontuario);
		
		return getAghuFacade().obterAtendimentoAtual(paciente);
	}
	/**
	 * Busca atendimento atual do paciente através do id do leito em que está
	 * internado. No caso de inconsistência do banco, mais de um registro 
	 * serão retornados.
	 * 
	 * @param leitoId
	 * @return atendimento
	 * @throws ApplicationBusinessException 
	 */
	public AghAtendimentos obterAtendimentoAtualPorLeitoId(final String leitoId) throws ApplicationBusinessException {
		AinLeitos leito = getCadastrosBasicosInternacaoFacade().obterLeitoPorId(leitoId);
		List<AghAtendimentos> listaAtendimentos = getAghuFacade().pesquisarAtendimentoVigente(leito);
		if (listaAtendimentos.size() == 1){
			return listaAtendimentos.get(0);
		}
		
		if (listaAtendimentos.size() > 1){
			throw new ApplicationBusinessException(ExceptionCode.MAIS_DE_UM_ATENDIMENTO_ENCONTRADO, leitoId);
		}
		
		throw new ApplicationBusinessException(ExceptionCode.NENHUM_ATENDIMENTO_ENCONTRADO, leitoId);		
	}

	/**
	 * Busca prescrições médicas de um atendimento ainda não encerradas (data e
	 * hora fim maior que data e hora atual) a partir do id do atendimento
	 * 
	 * @param id
	 *            do atendimento
	 * @return lista de prescrições médicas
	 */
	public List<MpmPrescricaoMedica> pesquisarPrescricoesMedicasNaoEncerradasPorAtendimentoSeq(
			final Integer seq) {
		AghAtendimentos atendimento = getPacienteFacade().obterAtendimento(seq);
		return pesquisarPrescricoesMedicasNaoEncerradasPorAtendimento(atendimento);
	}

	/**
	 * Busca prescrições médicas de um atendimento ainda não encerradas (data e
	 * hora fim maior que data e hora atual)
	 * 
	 * @param atendimento
	 * @return lista de prescrições médicas
	 */
	public List<MpmPrescricaoMedica> pesquisarPrescricoesMedicasNaoEncerradasPorAtendimento(
			final AghAtendimentos atendimento) {
		Date dataAtual = getDataAtual();
		return getMpmPrescricaoMedicaDAO().prescricoesAtendimentoDataFim(
				atendimento, dataAtual);
	}

	/**
	 * Verifica se é possível editar a prescrição médica. Não pode haver outra
	 * prescrição em uso para o mesmo atendimento. Marca a prescrição como em
	 * uso para o usuário atual.
	 * 
	 * @param prescricao
	 * @param cienteEmUso
	 *            indica se o usuário já está ciente no caso da prescrição está
	 *            em uso.
	 * @throws ApplicationBusinessException 
	 * @exception OUTRA_PRESCRICAO_EM_USO
	 */
	public void editarPrescricao(final MpmPrescricaoMedica prescricao,
			final boolean cienteEmUso) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AghAtendimentos atendimento = prescricao.getAtendimento();

		getAghuFacade().refreshAndLock(atendimento);
		
		getMpmPrescricaoMedicaDAO().refreshAndLock(prescricao);		

		// utiliza a mesma data durante toda a transação
		Date dataAtual = getDataAtual();
		
		if (!DominioSituacaoPrescricao.U.equals(prescricao.getSituacao())){
			validarOutraPrescricaoEmUso(atendimento, prescricao, dataAtual);			
		}

		if (!cienteEmUso) {
			validarPrescricaoEmUso(prescricao);
		}

		// Se está 'L'ivre e vai para em 'U'so, atualiza o movimento
		if (DominioSituacaoPrescricao.L.equals(prescricao.getSituacao())) {
			prescricao.setDthrMovimento(dataAtual);
		}
		prescricao.setSituacao(DominioSituacaoPrescricao.U);
		
		prescricao.setServidorAtualizada(servidorLogado);

		//getAghuFacade().flush();
		
		gerarLogPrescricaoEmUso(prescricao, "Edição");
	}

	/**
	 * Verifica se a prescrição está em uso. Se for o caso, lança uma exceção de
	 * forma a alertar o usuário. Após o usuários tomar ciencia da condição da
	 * prescrição, esta validação não é mais realizada.
	 * 
	 * ORADB function MPMC_CONFIRMA_ATU_PME_USO da MPMF_ELAB_PRESCRICAO.pll
	 * 
	 * @param prescricao
	 * @exception PRESCRICAO_ATUAL_EM_USO
	 *            
	 */
	private void validarPrescricaoEmUso(final MpmPrescricaoMedica prescricao)
			throws BaseException {
		if (DominioSituacaoPrescricao.U.equals(prescricao.getSituacao())) {
			RapServidores servidor;
			if (prescricao.getServidorAtualizada() == null) {
				servidor = prescricao.getServidor(); 
			} else {
				servidor = prescricao.getServidorAtualizada();
			}
			throw new ApplicationBusinessException(ExceptionCode.PRESCRICAO_ATUAL_EM_USO, 
					new SimpleDateFormat("dd/MM/yyyy HH:mm").format(
							prescricao.getCriadoEm()
					),
					servidor.getPessoaFisica().getNome() 
			);
		}
	}

	/**
	 * Obtém data de referência para a próxima prescrição
	 * 
	 * @throws ApplicationBusinessException
	 * 
	 */
	public Date obterDataReferenciaProximaPrescricao(
			final AghAtendimentos atendimento) throws ApplicationBusinessException {
		return obterDataReferenciaProximaPrescricao(atendimento, getDataAtual());
	}

	/**
	 * Obtém data de referência para a próxima prescrição
	 * 
	 * @throws ApplicationBusinessException
	 * 
	 */
	private Date obterDataReferenciaProximaPrescricao(
			final AghAtendimentos atendimento, final Date dataAtual)
			throws ApplicationBusinessException {
		if (atendimento == null) {
			throw new IllegalArgumentException("Atendimento não informado.");
		}
		
		if (dataAtual == null) {
			throw new IllegalArgumentException("Data não informada.");
		}
		
		// Prescricao informatizada
		if (! aghuFacade.possuiCaracteristicaPorUnidadeEConstante(atendimento.getUnidadeFuncional().getSeq(), ConstanteAghCaractUnidFuncionais.PME_INFORMATIZADA)) {
			throw new ApplicationBusinessException(
					ExceptionCode.PRESCRICAO_NAO_INFORMATIZADA);
		}

		Date dataReferencia;
		List<MpmPrescricaoMedica> prescricoes;
		prescricoes = getMpmPrescricaoMedicaDAO().prescricoesAtendimentoDataFim(
				atendimento, dataAtual);
		if (prescricoes.size() > 0) {
			dataReferencia = prescricoes.get(prescricoes.size()-1).getDthrFim();
		} else {
			Date horaValidadePME = atendimento.getUnidadeFuncional()
					.getHrioValidadePme();
			Date dataTrocaPrescricao = DateUtil.comporDiaHora(dataAtual,
					horaValidadePME);
			if (dataAtual.compareTo(dataTrocaPrescricao) < 0) {
				dataReferencia = DateUtil.adicionaDias(dataAtual, -1);
			} else {
				dataReferencia = dataAtual;
			}
		}
		return DateUtils.truncate(dataReferencia, Calendar.DAY_OF_MONTH);
	}

	/**
	 * Verifica se o atendimento está em andamento e permite internação
	 * 
	 * @param atendimento
	 * @throws ApplicationBusinessException
	 *             ATENDIMENTO_NAO_ESTA_EM_ANDAMENTO,
	 *             TIPO_ATENDIMENTO_NAO_PERMITE_PRESCRICAO
	 */
	public void verificaAtendimento(AghAtendimentos atendimento)
			throws ApplicationBusinessException {
		if (!DominioPacAtendimento.S.equals(atendimento.getIndPacAtendimento())
				&& getAmbulatorioFacade().pesquisarAtendimentoParaPrescricaoMedica(null, atendimento.getSeq()).isEmpty()) {
			throw new ApplicationBusinessException(
					ExceptionCode.ATENDIMENTO_NAO_ESTA_EM_ANDAMENTO);
		}
		if (!atendimento.getOrigem().permitePrescricao()) {
			throw new ApplicationBusinessException(
					ExceptionCode.TIPO_ATENDIMENTO_NAO_PERMITE_PRESCRICAO);
		}
	}

	/**
	 * Verifica se a data fim do atendimento é vigente para o período informado
	 * 
	 * @param atendimento
	 * @param dthrFim
	 * @param dthrInicio
	 * @throws ApplicationBusinessException
	 *             ATENDIMENTO_NAO_VIGENTE_PARA_PERIODO
	 */
	public void validaDataFimAtendimento(
			final AghAtendimentos atendimento, Date dthrFim, Date dthrInicio)
			throws ApplicationBusinessException {
		if (atendimento.getDthrFim() != null) {
			throw new ApplicationBusinessException(
					ExceptionCode.ATENDIMENTO_NAO_VIGENTE_PARA_PERIODO);
		}
	}

	/**
	 * Obtem a última prescrição que encerra no dia informado
	 * @param prescricoes é a lista de prescrições retornadas por  
	 *        getPrescricaoMedicaDAO().prescricoesAtendimentoDataFim
	 *        Considera-se que a lista esteja ordenada por dataFim
	 * @param data considera apenas o dia, ignorando a hora
	 * @return prescricao com maior dataFim dentro do dia informado  
	 *         null caso não exista prescrição encerrando neste dia
	 */
	private MpmPrescricaoMedica obterUltimaPrescricaoDoDia(
			List<MpmPrescricaoMedica> prescricoes, Date data
	) {
		MpmPrescricaoMedica ultimaPrescricao = null;
		for (MpmPrescricaoMedica prescricao : prescricoes) {
			//if (DateUtil.validarMesmoDia(prescricao.getDthrFim(), data)) {
			if (DateUtil.calcularDiasEntreDatas(prescricao.getDthrFim(), data) <= 0) {
				ultimaPrescricao = prescricao;
			}
		}
		return ultimaPrescricao;
	}
	
	// ORADB: Procedure MPMP_VER_FUTURA_PRESCRICAO (MPMF_ELAB_PRESCRICAO.dll)
	private boolean possuiPrescricaoFutura(Date dataReferencia,
			List<MpmPrescricaoMedica> prescricoes) {
		for (MpmPrescricaoMedica prescricao : prescricoes) {
			if (prescricao.getDtReferencia().compareTo(dataReferencia) > 0) {
				return true;
			}
		}
		return false;
	}
	

	public void verificarCriarPrescricao(
			final AghAtendimentos atendimento
		) throws ApplicationBusinessException {
		verificarCriarPrescricao(atendimento, getDataAtual());
	}
	
	
	private void verificarCriarPrescricao (
		final AghAtendimentos atendimento,
		Date dataAtual
	) throws ApplicationBusinessException {
		verificaAtendimento(atendimento);
		
		// Prescricao informatizada
		if (!aghuFacade.possuiCaracteristicaPorUnidadeEConstante(atendimento.getUnidadeFuncional().getSeq(), ConstanteAghCaractUnidFuncionais.PME_INFORMATIZADA)) {
			throw new ApplicationBusinessException(ExceptionCode.PRESCRICAO_NAO_INFORMATIZADA);
		}
		
		validarOutraPrescricaoEmUso(atendimento, null, dataAtual);
	}

	private RedirectVO validarPrescricaoUTIP(final AghAtendimentos atendimento) throws ApplicationBusinessException {
		//UTI – PEDIÁTRICA – CARACTERISTICA = Unid UTIP
		if (aghuFacade.possuiCaracteristicaPorUnidadeEConstante(atendimento.getUnidadeFuncional().getSeq(), ConstanteAghCaractUnidFuncionais.UNID_UTIP)) {
			//vERIFICAR PIM2
			List<MpmPim2> lista = mpmPim2DAO.pesquisarPim2PorAtendimentoSituacao(atendimento.getSeq(), DominioSituacaoPim2.P);
			if(lista != null && !lista.isEmpty()) {
				Integer limitePIM2 = parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_LIMITE_HRS_PIM2);
				Integer tempoEspera = DateUtil.obterQtdHorasEntreDuasDatas(atendimento.getDthrIngressoUnidade(), new Date());
				if(limitePIM2 < tempoEspera) {
					//REDIRECIONA #3542
					return new RedirectVO(PREENCHER_PMI_2, new Object[] {atendimento.getSeq()});
				}
			}
		}
		return null;
	}
	
	private void validarPrescricaoUTIN(final AghAtendimentos atendimento) throws ApplicationBusinessException {
		//UTI – PEDIÁTRICA – CARACTERISTICA = Unid UTIN
		if (aghuFacade.possuiCaracteristicaPorUnidadeEConstante(atendimento.getUnidadeFuncional().getSeq(), ConstanteAghCaractUnidFuncionais.UNID_UTIN)) {
			if(farmaciaFacade.existeDispensacaoAnteriorPacienteUTI(atendimento.getSeq(), atendimento.getUnidadeFuncional().getSeq())) {
				if(!perinatologiaFacade.isSnappesPreenchido(atendimento.getPaciente().getCodigo(), atendimento.getDthrInicio())) {
					throw new ApplicationBusinessException(ExceptionCode.SNAPPE_II);
				}
				if(!perinatologiaFacade.isExameFisicoRealizado(atendimento.getPaciente().getCodigo())) {
					throw new ApplicationBusinessException(ExceptionCode.RN_EXAME_FISICO);
				}
			}
		}
	}
	
	private void validarPrescricaoNeoNatal(final AghAtendimentos atendimento) throws ApplicationBusinessException {
		//INTERNACAO NEONATOLOGICA / NEO – ALOJAMENTO CONJUNTO / UTIN – NEONATAL – CARACTERISTICA = Unid Neonatologia
		if (aghuFacade.possuiCaracteristicaPorUnidadeEConstante(atendimento.getUnidadeFuncional().getSeq(), ConstanteAghCaractUnidFuncionais.UNID_NEONATOLOGIA)) {
			if(DominioOrigemAtendimento.I.equals(atendimento.getOrigem()) || DominioOrigemAtendimento.N.equals(atendimento.getOrigem())) {
				if(mpmPrescricaoMedicaDAO.existePrescricaoAnteriorPaciente(atendimento.getSeq())) {
					if(!perinatologiaFacade.isImpressaoLogNecessaria(atendimento.getPaciente().getCodigo())) {
						throw new ApplicationBusinessException(ExceptionCode.REL_RN);
					}
					if(!perinatologiaFacade.isImpressaoRelatorioFisicoNecessaria(atendimento.getPaciente().getCodigo())) {
						throw new ApplicationBusinessException(ExceptionCode.REL_EXAME_FISICO);
					}
				}
			}
		}
	}
	
	private RedirectVO validarPrescricaoObstetricia(final AghAtendimentos atendimento) throws ApplicationBusinessException {
		//EMERGÊNCIA OBSTETRICA – CARACTERISTICA = Emergencia Obstetrica 
		if (aghuFacade.possuiCaracteristicaPorUnidadeEConstante(atendimento.getUnidadeFuncional().getSeq(), ConstanteAghCaractUnidFuncionais.EMERGENCIA_OBSTETRICA)) {
			if(mpmPrescricaoMedicaDAO.existePrescricaoAnteriorPaciente(atendimento.getSeq())) {
				if(atendimento.getGsoPacCodigo() != null && atendimento.getGsoPacCodigo() > 0) {
					List<McoNascimentos> listaNascimentos = perinatologiaFacade.listarNascimentosPorCodigoPaciente(atendimento.getPaciente().getCodigo());
					if(listaNascimentos == null || listaNascimentos.isEmpty()) {
						listaNascimentos = perinatologiaFacade.pesquisarNascimentosPorGestacao(atendimento.getGsoPacCodigo(), atendimento.getGsoSeqp());
						if(listaNascimentos != null && !listaNascimentos.isEmpty()) {
							throw new ApplicationBusinessException(ExceptionCode.PRESCRICAO_GESTANTE_I);	
						}
					}
				}
			}
			
			if (aghuFacade.possuiCaracteristicaPorUnidadeEConstante(atendimento.getUnidadeFuncional().getSeq(), ConstanteAghCaractUnidFuncionais.UNID_CTI)) {
				List<MpmMotivoIngressoCti> motivoIngressoCti = mpmMotivoIngressoCtiDAO.pesquisarMotivoIngressoCtisPorAtendimento(atendimento);
				if(!aghuFacade.possuiCaracteristicaPorUnidadeEConstante(atendimento.getUnidadeFuncional().getSeq(), ConstanteAghCaractUnidFuncionais.UNID_CTI) 
						|| !DominioPacAtendimento.S.equals(atendimento.getIndPacAtendimento())
						|| motivoIngressoCti == null
						|| motivoIngressoCti.isEmpty()) {
					//REDIRECIONA #44136 (IMPEDIDA)
					return null;
				}				
			}
		}
		return null;
	}
	
	private RedirectVO validarPrescricaoCTI(final AghAtendimentos atendimento) throws ApplicationBusinessException {
		//SAPS 3
		if (aghuFacade.possuiCaracteristicaPorUnidadeEConstante(atendimento.getUnidadeFuncional().getSeq(), ConstanteAghCaractUnidFuncionais.UNID_CTI)) {
			List<MpmEscoreSaps3> listaEscore = mpmEscoreSaps3DAO.pesquisarEscorePorAtendimento(atendimento.getSeq());
			if(listaEscore != null && !listaEscore.isEmpty()) {
				MpmEscoreSaps3 escore = (MpmEscoreSaps3)CollectionUtils.get(listaEscore, 0);
				if(DominioSituacaoSaps3.P.equals(escore.getIndSituacao())) {
					Integer limiteSAPS3 = parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_AGHU_LIMITE_HRS_SAPS3);
					Integer tempoEspera = DateUtil.obterQtdHorasEntreDuasDatas(escore.getDthrIngressoUnidade(), new Date());
					if(limiteSAPS3 < tempoEspera) {
						//REDIRECIONA #44084
						return new RedirectVO(PREENCHER_ESCORE_GRAVIDADE, new Object[] {escore.getSeq()});
					}
				}
			}
		}
		return null;
	}
	
	private RedirectVO validarPrescricaoMedica(final AghAtendimentos atendimento) throws ApplicationBusinessException {

		RedirectVO redirectVO = validarPrescricaoUTIP(atendimento);
		if(redirectVO != null) {
			return redirectVO;
		}
		
		validarPrescricaoUTIN(atendimento);
		
		validarPrescricaoNeoNatal(atendimento);
		
		redirectVO =  validarPrescricaoObstetricia(atendimento);
		if(redirectVO != null) {
			return redirectVO;
		}
		
		redirectVO =  validarPrescricaoCTI(atendimento);
		return redirectVO;
	}
	
	/**
	 * @throws BaseException 
	 * Cria uma nova prescrição médica para um atendimento para a data informada
	 * (opcional)
	 * 
	 * @param atendimento
	 * @param data
	 *            de referência
	 * @exception
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public Object criarPrescricao(
			final AghAtendimentos atendimento, Date dataReferencia, String nomeMicrocomputador)
			throws BaseException {
		
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		
		if (atendimento == null) {
			throw new IllegalArgumentException("Atendimento não informado.");
		}
		
		// Serializa criação de prescrições sobre o mesmo atendimento
		getAghuFacade().refreshAndLock(atendimento);
		
		AghUnidadesFuncionais unidadeFuncional = atendimento.getUnidadeFuncional();
		getAghuFacade().refreshUnidadesFuncionais(unidadeFuncional);

		// utiliza a mesma data durante toda a transação
		Date dataAtual = getDataAtual();
		
		RedirectVO redirectVO = validarPrescricaoMedica(atendimento);
		if(redirectVO != null) {
			return redirectVO;
		}
		
		verificarCriarPrescricao(atendimento, dataAtual);
		
		// Calcula data de início e data de fim

		if (dataReferencia == null) {
			dataReferencia = obterDataReferenciaProximaPrescricao(atendimento,
					dataAtual);
		}

		Date horaValidadePME = unidadeFuncional.getHrioValidadePme();

		Date dthrFim = DateUtil.comporDiaHora(DateUtil.adicionaDias(
				dataReferencia, +1), horaValidadePME);

		Date dthrInicio = (Date) ObjectUtils.max(DateUtil.adicionaDias(dthrFim,
				-1), dataAtual);
		
		List<MpmPrescricaoMedica> prescricoes;
		prescricoes = getMpmPrescricaoMedicaDAO().prescricoesAtendimentoDataFim(
				atendimento, dataAtual
		);
		
		MpmPrescricaoMedica ultimaPrescricaoDia = obterUltimaPrescricaoDoDia(
				prescricoes, dataReferencia
		);
		
		if (ultimaPrescricaoDia != null && ultimaPrescricaoDia.getDthrFim().compareTo(dthrInicio) < 0) {
			// Isto ocorre quando há alteração na hora de validade da prescição (geralmente por troca de unidade funcional)
			// Neste caso, deve-se criar uma prescrição para preencher o horário entre o fim da prescrição anterior e o que 
			// seria o início da nova prescrição.
			dthrFim = dthrInicio;
			dthrInicio = ultimaPrescricaoDia.getDthrFim();
		}
		
		// ORADB MPMK_PME_RN.RN_PMEP_VER_DTHR_FIM
		if (dthrFim.compareTo(dataAtual) < 0) {
			throw new ApplicationBusinessException(ExceptionCode.PRESCRICAO_NO_PASSADO);
		}

		validarAdiantamento(unidadeFuncional, dataAtual, dthrInicio);

		// Validar se já existe prescrição neste período

		for (MpmPrescricaoMedica prescricao : prescricoes) {
			if (prescricao.getDthrFim().compareTo(dthrFim) < 0
					&& prescricao.getDthrFim().compareTo(dthrInicio) > 0) {
				dthrInicio = prescricao.getDthrFim();
			}
			if (prescricao.getDthrInicio().compareTo(dthrFim) < 0
					&& prescricao.getDthrInicio().compareTo(dthrInicio) > 0) {
				dthrFim = prescricao.getDthrInicio();
			}
			if (prescricao.getDthrInicio().compareTo(dthrInicio) >= 0
					&& prescricao.getDthrFim().compareTo(dthrFim) <= 0) {
				throw new ApplicationBusinessException(
						ExceptionCode.PRESCRICOES_SOBREPOSTAS);
			}
		}
		if (dthrInicio.compareTo(dthrFim) >= 0) {
			throw new ApplicationBusinessException(
					ExceptionCode.PRESCRICOES_SOBREPOSTAS);
		}
		
		// Verificar Prescrição Consecutiva
		if (aghuFacade.possuiCaracteristicaPorUnidadeEConstante(unidadeFuncional.getSeq(), ConstanteAghCaractUnidFuncionais.PME_CONSECUTIVA) && ultimaPrescricaoDia == null
				&& dthrInicio.compareTo(dataAtual) > 0) {
			throw new ApplicationBusinessException(ExceptionCode.PRESCRICAO_CONSECUTIVA_NAO_ENCONTRADA);
		}
		
		validaDataFimAtendimento(atendimento, dthrFim, dthrInicio);
			
		// Tudo ok. Cria nova prescrição.

		MpmPrescricaoMedica novaPrescricao = new MpmPrescricaoMedica();
		novaPrescricao.setId(new MpmPrescricaoMedicaId(atendimento.getSeq(),
				getMpmPrescricaoMedicaDAO().obterValorSequencialId()));

		novaPrescricao.setCriadoEm(dataAtual);
		novaPrescricao.setDthrMovimento(dataAtual); // mpmk_pme_rn.rn_pmep_ver_dthr_ini
		novaPrescricao.setSituacao(DominioSituacaoPrescricao.U);
		novaPrescricao.setServidor(servidorLogado);
		novaPrescricao.setServidorAtualizada(servidorLogado);
		novaPrescricao.setDthrInicio(dthrInicio);
		novaPrescricao.setDthrFim(dthrFim);
		novaPrescricao.setDtReferencia(dataReferencia);
		novaPrescricao.setAtendimento(atendimento);

		getMpmPrescricaoMedicaDAO().persistir(novaPrescricao);
		getMpmPrescricaoMedicaDAO().flush();

		// Copiar itens da prescrição anterior
		copiarPrescricao(
				novaPrescricao, 
				possuiPrescricaoFutura(dataReferencia, prescricoes), nomeMicrocomputador
		);
		
		//getAghuFacade().flush();

		gerarLogPrescricaoEmUso(novaPrescricao, "Inclusão");	
				
		getPrescricaoMedicaRN().atualizarFaturamento( novaPrescricao, null, DominioOperacaoBanco.INS, nomeMicrocomputador, new Date());

		return novaPrescricao;

	}

	/**
	 * Valida adiantamento da prescrição. A prescrição não pode ser adiantada
	 * mais do que o informado no parâmetro em sua unidade funcional. ORADB
	 * MPMK_PME_RN.RN_PMEP_VER_PRCR_FUT
	 * 
	 * @param unidadeFuncional
	 * @param dataAtual
	 * @param dthrInicio
	 * @throws ApplicationBusinessException
	 */
	private void validarAdiantamento(AghUnidadesFuncionais unidadeFuncional,
			Date dataAtual, Date dthrInicio) throws ApplicationBusinessException {
		Short quantidade = unidadeFuncional.getNroUnidTempoPmeAdiantadas();
		if (quantidade == null || quantidade == 0) {
			if (dthrInicio.compareTo(dataAtual) > 0) {
				throw new ApplicationBusinessException(
						ExceptionCode.NAO_EH_PERMITIDO_ADIANTAR_PRESCRICAO);
			}
		} else {
			Calendar dataInicioMaxima = Calendar.getInstance();
			dataInicioMaxima.setTime(dataAtual);
			int field = 0;
			switch (unidadeFuncional.getIndUnidTempoPmeAdiantada()) {
			case D:
				field = Calendar.DAY_OF_MONTH;
				break;
			case H:
				field = Calendar.HOUR_OF_DAY;
				break;
			}
			dataInicioMaxima.add(field, quantidade);
			if (dthrInicio.compareTo(dataInicioMaxima.getTime()) > 0) {
				throw new ApplicationBusinessException(
						ExceptionCode.NAO_EH_POSSIVEL_ADIANTAR_PRESCRICAO,
						quantidade, field == Calendar.DAY_OF_MONTH ? "dias"
								: "horas");
			}
		}
	}

	/**
	 * Valida se não há uma outra prescrição em uso para o mesmo atendimento, o
	 * que inviabiliza criação ou edição de outras prescrições.
	 * 
	 * @param atendimento
	 * @param prescricaoDesconsiderar
	 * @param dataAtual
	 * @throws ApplicationBusinessException
	 */
	private void validarOutraPrescricaoEmUso(final AghAtendimentos atendimento,
			MpmPrescricaoMedica prescricaoDesconsiderar, Date dataAtual)
			throws ApplicationBusinessException {
		MpmPrescricaoMedica prescricaoEmUso = obterPrescricaoEmUso(atendimento,
				prescricaoDesconsiderar, dataAtual);
		if (prescricaoEmUso != null) {
			throw new ApplicationBusinessException(
					ExceptionCode.OUTRA_PRESCRICAO_EM_USO
			);
		}
	}

	/**
	 * Verifica se existe alguma prescrição com situação "em uso"
	 * 
	 * @param atendimento
	 * @param prescricaoDesconsiderar
	 * @param dataAtual
	 */
	private MpmPrescricaoMedica obterPrescricaoEmUso(
			final AghAtendimentos atendimento,
			MpmPrescricaoMedica prescricaoDesconsiderar, Date dataAtual) {
		List<MpmPrescricaoMedica> prescricoes;
		prescricoes = getMpmPrescricaoMedicaDAO().prescricoesAtendimentoDataFim(
				atendimento, dataAtual);
		for (MpmPrescricaoMedica prescricao : prescricoes) {
			if (prescricao.equals(prescricaoDesconsiderar)) {
				continue;
			}
			if (DominioSituacaoPrescricao.U.equals(prescricao.getSituacao())) {
				return prescricao;
			}
		}
		return null;
	}

	/**
	 * Método que atualiza a prescrição recém criada com as informações da
	 * prescrição anterior mais recente
	 * 
	 * @param atdSeq
	 * @param dtReferencia
	 * @param dataInicio
	 * @throws ApplicationBusinessException
	 * @throws BaseException
	 */
	public void copiarPrescricao(MpmPrescricaoMedica prescricaoMedicaNova,
			boolean temPrescricaoFutura, String nomeMicrocomputador) throws BaseException {
		// Obtém dados da prescrição nova (ESTES ATRIBUTOS PRECISAM ESTAR
		// SETADOS NO OBJETO)
		Integer pmeAtdSeq = prescricaoMedicaNova.getId().getAtdSeq();
		Date dtReferencia = prescricaoMedicaNova.getDtReferencia();
		Date dataInicio = prescricaoMedicaNova.getDthrInicio();
		Date dataFim = prescricaoMedicaNova.getDthrFim();

		if (pmeAtdSeq == null) {
			throw new ApplicationBusinessException(
					ExceptionCode.ATENDIMENTO_PRESCRICAO_NULO);
		}
		if (dtReferencia == null) {
			throw new ApplicationBusinessException(
					ExceptionCode.DT_REFERENCIA_PRESCRICAO_NULA);
		}
		if (dataInicio == null) {
			throw new ApplicationBusinessException(
					ExceptionCode.DT_INICIO_PRESCRICAO_NULA);
		}

		Long nroDias = Long.valueOf(0);
		MpmPrescricaoMedica prescricaoMedicaAnterior =
			getMpmPrescricaoMedicaDAO().obterUltimaPrescricaoAtendimento(pmeAtdSeq, dtReferencia, dataInicio, prescricaoMedicaNova);

		if (prescricaoMedicaAnterior != null
				&& prescricaoMedicaAnterior.getDthrInicio() != null) {
			Date dtFimLida = prescricaoMedicaAnterior.getDthrFim();
			Date dtInicioLida = prescricaoMedicaAnterior.getDthrInicio();
			
			if(DominioOrigemAtendimento.getOrigensDePrescricaoAmbulatorial().
					contains(prescricaoMedicaNova.getAtendimento().getOrigem())){
				nroDias = Long.valueOf(1);
				this.copiarItensPrescricao(prescricaoMedicaNova,
						prescricaoMedicaAnterior, dataInicio, dataFim,
						dtFimLida, nroDias, temPrescricaoFutura, nomeMicrocomputador);
			}else if (dataInicio.compareTo(dtInicioLida) != 0) {
					nroDias = (dtReferencia.getTime() - dtInicioLida.getTime()) / 86400000;
					if (nroDias == 0) {
						nroDias = Long.valueOf(1);
					}
					this.copiarItensPrescricao(prescricaoMedicaNova,
							prescricaoMedicaAnterior, dataInicio, dataFim,
							dtFimLida, nroDias, temPrescricaoFutura, nomeMicrocomputador);
				} else {
					if (temPrescricaoFutura) {
						nroDias = Long.valueOf(1);
						this.copiarItensPrescricao(prescricaoMedicaNova,
								prescricaoMedicaAnterior, dataInicio, dataFim,
								dtFimLida, nroDias, temPrescricaoFutura, nomeMicrocomputador);
					}
				}
		}
	}

	/**
	 * Método que atualiza os itens da prescrição
	 * 
	 * @param dtFimLida
	 * @param nroDias
	 * @param dtInicioLida
	 * @throws ApplicationBusinessException
	 * @throws BaseException
	 */
	public void copiarItensPrescricao(MpmPrescricaoMedica prescricaoMedicaNova,
			MpmPrescricaoMedica prescricaoMedicaAnterior, Date dataInicio,
			Date dataFim, Date dtFimLida, Long nroDias,
			boolean temPrescricaoFutura, String nomeMicrocomputador) throws BaseException {
		
		final Integer pmeAtdSeq = prescricaoMedicaAnterior.getId().getAtdSeq();
		
//		if (!temPrescricaoFutura){
//			dataFim = null;
//		}
		
		//Copia os cuidados da prescrição
		this.copiarCuidadosPrescricao(dataFim, dataInicio, dtFimLida, prescricaoMedicaNova, prescricaoMedicaAnterior, nomeMicrocomputador);
		//Copia as dietas médicas da prescrição
		this.copiarDietasMedicas(dataFim, dataInicio, dtFimLida, pmeAtdSeq, prescricaoMedicaNova, prescricaoMedicaAnterior, nomeMicrocomputador);
		//Copia os medicamentos da prescrição
		this.copiarMedicamentosPrescricao(dataInicio, dataFim, dtFimLida,
				pmeAtdSeq, prescricaoMedicaNova, prescricaoMedicaAnterior,
				temPrescricaoFutura, nomeMicrocomputador);
		//Copia os procedimentos da prescrição
		this.copiarProcedimentosPrescricao(dataInicio, dataFim, dtFimLida, pmeAtdSeq, prescricaoMedicaNova, prescricaoMedicaAnterior, nomeMicrocomputador);
		//Copia nutrição parental total
		this.copiarNutricaoParentalTotalPrescricao(dataInicio, dataFim, dtFimLida, pmeAtdSeq, prescricaoMedicaNova, prescricaoMedicaAnterior);
		
	}
	

/*********************************************************************************************************
 * COPIA CUIDADOS DA PRESCRIÇÃO
 *********************************************************************************************************/
	
	/**
	 * Método que copia e atualiza a lista de cuidados da prescrição
	 * @param dataFim
	 * @param dtInicio
	 * @param dtFimLida
	 * @param pmeAtdSeq
	 * @param prescricaoMedicaNova
	 * @throws BaseException
	 */
	protected void copiarCuidadosPrescricao(Date dataFim, Date dtInicio,
			Date dtFimLida, 
			MpmPrescricaoMedica prescricaoMedicaNova, MpmPrescricaoMedica prescricaoMedicaAnterior, String nomeMicrocomputador)
			throws BaseException {
		MpmPrescricaoCuidadoDAO mpmPrescricaoCuidadosDAO = getPrescricaoCuidadosDAO();
		List<MpmPrescricaoCuidado> listaCuidados = mpmPrescricaoCuidadosDAO
				.pesquisarCuidadosMedicosUltimaPrescricao(prescricaoMedicaNova
						.getId().getAtdSeq(), dtFimLida, prescricaoMedicaAnterior);
		
		Integer pcuSeqAnt = null;
		Integer pcuSeq = null;
		
		try{
			for (MpmPrescricaoCuidado cuidado: listaCuidados){
				if (cuidado.getDthrFim() == null){
					pcuSeqAnt = cuidado.getId().getSeq().intValue();
					//Atualiza o cuidado
					//this.atualizarPrescricaoCuidado(prescricaoMedicaNova, cuidado, pmeAtdSeq, dtFimLida);
				}
				//Obtém o próximo sequencial
				pcuSeq = mpmPrescricaoCuidadosDAO.obterValorSequencialId();
				//Insere um novo cuidado de prescrição
				this.inserirPrescricaoCuidado(prescricaoMedicaNova, cuidado, dtInicio, dataFim, pcuSeq, true, nomeMicrocomputador);
				//Atualiza chaves da tabela mpa_uso_ord_cuidados
				atualizarUsoOrdCuidados(pcuSeq, pcuSeqAnt, prescricaoMedicaNova.getId().getAtdSeq());
			}
		} catch (BaseException e) {
			logError("Exceção BaseException capturada, lançada para cima.");
			throw e;
		} catch (Exception e) {
			//logError("ERRO COPIAR PRESCRIÇÃO CUIDADOS", e);
			logError(e.getMessage(),e);
			throw new ApplicationBusinessException(
					ExceptionCode.ERRO_COPIAR_PRESCRICAO_CUIDADOS, e);
		}
		
	}

	
/*********************************************************************************************************
 * COPIA DIETAS DA PRESCRIÇÃO
 *********************************************************************************************************/
	
	/**
	 * Método que copia e atualiza a lista de dietas
	 * @param dataFim
	 * @param dtInicio
	 * @param dtFimLida
	 * @param pmeAtdSeq
	 * @param prescricaoMedicaNova
	 * @throws ApplicationBusinessException
	 */
	protected void copiarDietasMedicas(Date dataFim, Date dtInicio,
			Date dtFimLida, Integer pmeAtdSeq,
			MpmPrescricaoMedica prescricaoMedicaNova, MpmPrescricaoMedica prescricaoMedicaAnterior, String nomeMicrocomputador)
			throws ApplicationBusinessException {
		
		MpmPrescricaoDietaDAO mpmPrescricaoDietaDAO = getPrescricaoDietaDAO();
		//Cursor de MpmPrescricaoDieta
		List<MpmPrescricaoDieta> listaDietas = mpmPrescricaoDietaDAO
				.pesquisarDietasUltimaPrescricao(dtFimLida,
						prescricaoMedicaAnterior);
		Integer pdtSeqAnt = null;
		Integer pdtSeq = null;
		
		try{
			for (MpmPrescricaoDieta dieta: listaDietas){
				
				if (dieta.getDthrFim() == null){
					pdtSeqAnt = dieta.getId().getSeq().intValue();
					//Atualiza a prescrição dieta
					//this.atualizarPrescricaoDieta(prescricaoMedicaNova, dieta, pmeAtdSeq, dtFimLida);
				}
				//Obtém o próximo sequencial
				pdtSeq = mpmPrescricaoDietaDAO.obterValorSequencialId();
				//Insere uma nova prescrição dieta
				MpmPrescricaoDieta dietaNova = this.inserirPrescricaoDieta(prescricaoMedicaNova, dieta, dtInicio, dataFim, pdtSeq, true, nomeMicrocomputador);
				//Persiste os objetos usoOrdNutricao
				atualizarUsoOrdNutricoes(pdtSeq, pdtSeqAnt, pmeAtdSeq);
				//Persiste itens da dieta
				inserirItensPrescricaoDieta(pmeAtdSeq, dieta, pdtSeq, dietaNova);
			}

		} catch (ApplicationBusinessException e) {
			logError("Exceção ApplicationBusinessException capturada, lançada para cima.");
			throw e;
		} catch (Exception e) {
			//logError("ERRO COPIAR PRESCRIÇÃO DIETAS", e);
			logError(e);
			throw new ApplicationBusinessException(
					ExceptionCode.ERRO_COPIAR_PRESCRICAO_DIETAS, e);
		}
	}
	
	

/*********************************************************************************************************
 * COPIA MEDICAMENTOS DA PRESCRIÇÃO
 *********************************************************************************************************/
	
	/**
	 * Método que copia e atualiza a lista de medicamentos da prescrição
	 * @param dataFim
	 * @param dtInicio
	 * @param dtFimLida
	 * @param pmeAtdSeq
	 *  
	 */
	protected void copiarMedicamentosPrescricao(Date dataInicio, Date dataFim,
			Date dtFimLida, Integer pmeAtdSeq, MpmPrescricaoMedica prescricaoMedicaNova, MpmPrescricaoMedica prescricaoMedicaAnterior,
			boolean temPrescricaoFutura, String nomeMicrocomputador) throws ApplicationBusinessException {
		
		try{
			MpmPrescricaoMdtoDAO mpmPrescricaoMdtoDAO = getPrescricaoMdtosDAO();
			List<MpmPrescricaoMdto> listaMedicamentos = mpmPrescricaoMdtoDAO
					.pesquisarMedicamentosUltimaPrescricao(dtFimLida, prescricaoMedicaAnterior);
			
			for (MpmPrescricaoMdto medicamento: listaMedicamentos){
				Boolean represcrever = false;
				Long pmdSeqLido = medicamento.getId().getSeq();
//				Long pmdSeqAnt = medicamento.getId().getSeq();
				
				represcrever = this.verificarReprescreverItemMedicamento(dataInicio, medicamento, pmeAtdSeq, pmdSeqLido);
				
//				if (!represcrever){
//					//atualizarMedicamento(prescricaoMedicaNova, pmeAtdSeq, dtFimLida, medicamento);
//				}
//				else{
//					if (!mpmPrescricaoMdtosDAO.verificarMedicamentoJaPrescrito(
//							pmeAtdSeq, medicamento.getId().getSeq(),
//							dataInicio, dataFim)) {
//						
//						//atualizarMedicamento(prescricaoMedicaNova, pmeAtdSeq, dtFimLida, medicamento);
//					}
//				}
				if (!represcrever){
					if (DominioIndPendenteItemPrescricao.P.equals(medicamento.getIndPendente()) ||
							DominioIndPendenteItemPrescricao.B.equals(medicamento.getIndPendente())){
						
						//Copia medicamento pendente
						this.copiarPendentes(prescricaoMedicaNova, pmeAtdSeq, medicamento.getId().getSeq(), dataInicio, dataFim, nomeMicrocomputador);
					}
					else{
						//Copia medicamento não pendente
						this.copiarNaoPendentes(prescricaoMedicaNova, pmeAtdSeq, medicamento.getId().getSeq(), dataInicio, dataFim, nomeMicrocomputador);
					}
				}
				else{
					if (!mpmPrescricaoMdtoDAO.verificarMedicamentoJaPrescrito(
							pmeAtdSeq, medicamento.getId().getSeq(),
							dataInicio, dataFim)) {
						
						this.represcrever(prescricaoMedicaNova, pmeAtdSeq,
								medicamento.getId().getSeq(), dataInicio, dataFim,
								dtFimLida, temPrescricaoFutura, nomeMicrocomputador);
					}
				}
			}
		}catch (ApplicationBusinessException e) {
			logError("Exceção ApplicationBusinessException capturada, lançada para cima.");
			throw e;
		} 
		catch (Exception e) {
			//logError("ERRO COPIAR PRESCRIÇÃO MEDICAMENTOS", e);			
			logError(e);
			int pos = 1;
			if(e.getStackTrace() != null && e.getStackTrace().length > 0) {
				for (StackTraceElement stackTraceElement : e.getStackTrace()) {
					if(stackTraceElement.getClassName().contains("br.gov.mec")){
						logError("Erro " + pos++ + ") Classe: " + stackTraceElement.getClassName() + " - na Linha: " + stackTraceElement.getLineNumber() + " - no Método: " + stackTraceElement.getMethodName());
					}
				}	
			}
			throw new ApplicationBusinessException(
					ExceptionCode.ERRO_COPIAR_PRESCRICAO_MEDICAMENTOS, e);

		}
	}
	

/*********************************************************************************************************
 * COPIA PROCEDIMENTOS DA PRESCRIÇÃO
 *********************************************************************************************************/
	
	/**
	 * Método que copia e atualiza a lista de procedimentos da prescrição
	 */
	protected void copiarProcedimentosPrescricao(Date dataInicio, Date dataFim,
			Date dtFimLida, Integer pmeAtdSeq,
			MpmPrescricaoMedica prescricaoMedicaNova,
			MpmPrescricaoMedica prescricaoMedicaAnterior, 
			String nomeMicrocomputador)
			throws ApplicationBusinessException {
		
		try{
			MpmPrescricaoProcedimentoDAO mpmPrescricaoProcedimentoDAO = getMpmPrescricaoProcedimentoDAO();
			List<MpmPrescricaoProcedimento> listaProcedimentos = mpmPrescricaoProcedimentoDAO
					.pesquisarProcedimentosUltimaPrescricao(dtFimLida,
							prescricaoMedicaAnterior);
			Long pprSeqAnt = null;
			
			for (MpmPrescricaoProcedimento procedimento: listaProcedimentos){
				if (procedimento.getDthrFim() == null){
					pprSeqAnt = procedimento.getId().getSeq();
//					this.atualizarProcedimento(prescricaoMedicaNova, pmeAtdSeq, dtFimLida, procedimento);
				}
				
				//TODO: Não utilizar mais a linha abaixo for implementado
				//o método contendo triggers, procedures e sequence
				//Long pprSeqNovo = Long.valueOf(mpmPrescricaoProcedimentoDAO.obterValorSequencialId());
				
				MpmPrescricaoProcedimento novoProcedimento = 
				this.inserirProcedimento(prescricaoMedicaNova, null,
						pmeAtdSeq, dataInicio, dataFim, procedimento, true, nomeMicrocomputador);
				
				// Insere na tabela Mpm_Modo_Uso_Prescricao_Procedimento
				this.inserirModoUsoPrescricaoProcedimento(novoProcedimento,
						pmeAtdSeq, procedimento.getId().getSeq().longValue());
				// Atualiza a chave da tabela mpa_uso_ord_procedimentos
				this.atualizarMpaUsoOrdProcedimento(pprSeqAnt, pmeAtdSeq,
						novoProcedimento.getId().getSeq());
			}
		} catch (ApplicationBusinessException e) {
			logError("Exceção ApplicationBusinessException capturada, lançada para cima.");
			throw e;
		}
		catch (Exception e) {
			//logError("ERRO COPIAR PRESCRIÇÃO PROCEDIMENTOS", e);
			logError(e);
			throw new ApplicationBusinessException(
					ExceptionCode.ERRO_COPIAR_PRESCRICAO_PROCEDIMENTOS, e);
		}
	}
	

/*********************************************************************************************************
 * COPIA NPTS DA PRESCRIÇÃO
 *********************************************************************************************************/
	private void copiarNutricaoParentalTotalPrescricao(Date dataInicio, Date dataFim, Date dtFimLida, Integer pmeAtdSeq, MpmPrescricaoMedica prescricaoMedicaNova, MpmPrescricaoMedica prescricaoMedicaAnterior) throws ApplicationBusinessException {
		//public void criarPrescricaoNpts(MpmPrescricaoMedica ultimaPrescricao, MpmPrescricaoMedica novaPrescricao) throws ApplicationBusinessException {
			//executa c1 
			List <MpmPrescricaoNpt> listaNpt = mpmPrescricaoNptDAO.informacoesPrescricaoNPTAntiga(prescricaoMedicaAnterior);
			Integer nptSeq = null;
			//Integer nptSeqAnt = null;
			Boolean inserirComposicao = Boolean.FALSE;
			for (MpmPrescricaoNpt itemNpt : listaNpt) {
				nptSeq = mpmPrescricaoNptDAO.getNextVal(SequenceID.MPM_PNP_SQ1);
				//nptSeqAnt = itemNpt.getId().getSeq();
	
				//#45296 RF01.2 inclui nova prescrição na tabela MPM_PRESCRICAO_NPTS 
				inserirComposicao = inserirNutricaoParenteral(itemNpt, prescricaoMedicaNova, prescricaoMedicaAnterior, nptSeq, dataInicio, dataFim, inserirComposicao);
				
				MpmPrescricaoNpt novaNpt = mpmPrescricaoNptDAO.obterOriginal(new MpmPrescricaoNptId(prescricaoMedicaNova.getAtendimento().getSeq(), nptSeq));
				
				//#45296 RF01.3 RF01.4
				inserirComposicoesNpt(novaNpt, prescricaoMedicaNova, itemNpt, inserirComposicao);
				//RF01.1 se dthrFim == null, atualiza a prescrição na tabela MPM_PRESCRICAO_NPTS
				inserirComposicao = Boolean.FALSE;
				atualizarNutricaoParenteral(itemNpt, prescricaoMedicaAnterior);
			}
		//}
	}
	private Boolean inserirNutricaoParenteral(MpmPrescricaoNpt nptAnterior, MpmPrescricaoMedica prescricaoMedicaNova, 
			MpmPrescricaoMedica prescricaoMedicaAnterior, Integer nptSeqNovo, Date dataInicio, Date dataFim, Boolean inserirComposicao) throws ApplicationBusinessException{
		try {
			//Monta a nova npt 
			MpmPrescricaoNpt novaNpt = new MpmPrescricaoNpt();
			//Monta o id
			MpmPrescricaoNptId id = new MpmPrescricaoNptId();
			id.setAtdSeq(prescricaoMedicaNova.getId().getAtdSeq());
			id.setSeq(nptSeqNovo);
			//seta id
			novaNpt.setId(id);
			//Seta a referência para a Prescrição Médica criada
			novaNpt.setPrescricaoMedica(prescricaoMedicaNova);
			//Seta os demais campos da npt anterior
			setarCamposNptAnterior(nptAnterior, dataInicio, dataFim, novaNpt);
			//CAMPOS PRCR ANTERIOR
			novaNpt.setMpmPrescricaoNptAnterior(nptAnterior);
			//Persiste a prescricao npt
			mpmPrescricaoNptDAO.persistir(novaNpt);
			mpmPrescricaoNptDAO.flush();
			return Boolean.TRUE;
		} catch (Exception e) {
			throw new ApplicationBusinessException(ExceptionCode.ERRO_INCLUSAO_PRESCRICAO_NPT);
		}
	}

	private void inserirComposicoesNpt(MpmPrescricaoNpt novaNpt, MpmPrescricaoMedica novaPrescricao, MpmPrescricaoNpt  nptAnterior, Boolean inserirComposicao)throws ApplicationBusinessException {
		if(inserirComposicao){
			//#45296 RF01.3 insere composições vinculadas na tabela MPM_COMPOSICAO_PRESCRICAO_NPTS 
			try {
				List <MpmComposicaoPrescricaoNpt> composicoes = mpmComposicaoPrescricaoNptDAO.pesquisarComposicoesPrescricaoNpt(novaPrescricao.getAtendimento().getSeq(), Integer.valueOf(nptAnterior.getId().getSeq()));
				if(composicoes != null && !composicoes.isEmpty()){
					for (MpmComposicaoPrescricaoNpt cnp : composicoes) {
						//monta id
						MpmComposicaoPrescricaoNptId id = new MpmComposicaoPrescricaoNptId();
						id.setPnpAtdSeq(novaPrescricao.getAtendimento().getSeq());
						id.setPnpSeq(novaNpt.getId().getSeq());
						id.setSeqp(cnp.getId().getSeqp());
						//nova composicao
						MpmComposicaoPrescricaoNpt novaCompo = new MpmComposicaoPrescricaoNpt();
						//seta id
						novaCompo.setId(id);
						//seta outros campos
						novaCompo.setAfaTipoVelocAdministracoes(cnp.getAfaTipoVelocAdministracoes());
						novaCompo.setAfaTipoComposicoes(cnp.getAfaTipoComposicoes());
						novaCompo.setVelocidadeAdministracao(cnp.getVelocidadeAdministracao());
						novaCompo.setQtdeHorasCorrer(cnp.getQtdeHorasCorrer());
						novaNpt.getComposicoesPrescricaoNpt().add(novaCompo);
						mpmComposicaoPrescricaoNptDAO.persistir(novaCompo);
						mpmComposicaoPrescricaoNptDAO.flush();
					}
				}
			} catch (Exception e) {
				throw new ApplicationBusinessException(ExceptionCode.ERRO_INCLUSAO_COMPOSICAO_NPT);
			}
			
			//#45296 RF01.4 inclusão de itens vinculados na tabela MPM_ITEM_PRESCRICAO_NPTS
			List <MpmComposicaoPrescricaoNpt> composi = mpmComposicaoPrescricaoNptDAO.pesquisarComposicoesPrescricaoNpt(novaPrescricao.getAtendimento().getSeq(),
								Integer.valueOf(novaNpt.getId().getSeq()));
			try {
				List<MpmItemPrescricaoNpt> listaItemNpt = mpmItemPrescricaoNptDAO.listarItensPrescricaoNpt(novaPrescricao.getAtendimento().getSeq(), nptAnterior.getId().getSeq());
				if(listaItemNpt!= null && !listaItemNpt.isEmpty()){
					for (MpmItemPrescricaoNpt mpmItemPnp : listaItemNpt) {
						//monta id
						MpmItemPrescricaoNptId id = new MpmItemPrescricaoNptId();
						id.setCptPnpAtdSeq(novaPrescricao.getAtendimento().getSeq());
						id.setCptPnpSeq(novaNpt.getId().getSeq());
						id.setCptSeqp(mpmItemPnp.getId().getCptSeqp());
						id.setSeqp(mpmItemPnp.getId().getSeqp());
						
						MpmItemPrescricaoNpt novoItem = new MpmItemPrescricaoNpt();
						//seta id
						novoItem.setId(id);
						
						//seta outros campos
						novoItem.setAfaComponenteNpts(mpmItemPnp.getAfaComponenteNpts());
						novoItem.setAfaFormaDosagens(mpmItemPnp.getAfaFormaDosagens());
						novoItem.setQtdePrescrita(mpmItemPnp.getQtdePrescrita());
						novoItem.setQtdeBaseCalculo(mpmItemPnp.getQtdeBaseCalculo());
						novoItem.setQtdeCalculada(mpmItemPnp.getQtdeCalculada());
						novoItem.setTipoParamCalculo(mpmItemPnp.getTipoParamCalculo());
						novoItem.setTotParamCalculo(mpmItemPnp.getTotParamCalculo());
						novoItem.setUnidadeMedidaMedicas(mpmItemPnp.getUnidadeMedidaMedicas());
						novoItem.setPcnCnpMedMatCodigo(mpmItemPnp.getPcnCnpMedMatCodigo());
						novoItem.setPcnSeqp(mpmItemPnp.getPcnSeqp());
						novoItem.setPercParamCalculo(mpmItemPnp.getPercParamCalculo());
						
						//adiciona item em composicao
						for (MpmComposicaoPrescricaoNpt itemComposi : composi) {
							if(novoItem.getId().getCptSeqp().equals(itemComposi.getId().getSeqp())){
								itemComposi.getMpmItemPrescricaoNptses().add(novoItem);
							}
						}
						
						mpmItemPrescricaoNptDAO.persistir(novoItem);
						mpmItemPrescricaoNptDAO.flush();
					}
				}
			} catch (Exception e) {
				throw new ApplicationBusinessException(ExceptionCode.ERRO_INCLUSAO_ITEM_NPT);
			}
		}
	}
	
	private void atualizarNutricaoParenteral(MpmPrescricaoNpt nptAnterior, MpmPrescricaoMedica pmeAntiga) throws ApplicationBusinessException {
		if(nptAnterior != null && nptAnterior.getDthrFim() == null){
			try {
				nptAnterior.setDthrFim(pmeAntiga.getDthrFim());
				nptAnterior.setServidorValidaMovimentacao(nptAnterior.getServidorValidacao());
				mpmPrescricaoNptDAO.atualizar(nptAnterior);
				mpmPrescricaoNptDAO.flush();
			} catch (Exception e) {
				throw new ApplicationBusinessException(ExceptionCode.ERRO_ATUALIZACAO_PRES_NPT);
			}
		}
	}
	
	private void setarCamposNptAnterior(MpmPrescricaoNpt nptAnterior,
			Date dataInicio, Date dataFim, MpmPrescricaoNpt novaNpt) {
		novaNpt.setAfaFormulaNptPadrao(nptAnterior.getAfaFormulaNptPadrao());
		novaNpt.setDthrInicio(dataInicio);
		novaNpt.setSegueGotejoPadrao(nptAnterior.getSegueGotejoPadrao());
		novaNpt.setIndPendente(DominioIndPendenteItemPrescricao.P);
		novaNpt.setProcedEspecialDiversos(nptAnterior.getProcedEspecialDiversos());
		novaNpt.setJustificativa(nptAnterior.getJustificativa());
		novaNpt.setObservacao(nptAnterior.getObservacao());
		novaNpt.setVolumeMlKgDia(nptAnterior.getVolumeMlKgDia());
		novaNpt.setDthrFim(dataFim);
		novaNpt.setJnpSeq(nptAnterior.getJnpSeq());
		novaNpt.setParamVolumeMl(nptAnterior.getParamVolumeMl());
		novaNpt.setTipoParamVolume(nptAnterior.getTipoParamVolume());
		novaNpt.setVolumeCalculado(nptAnterior.getVolumeCalculado());
		novaNpt.setVolumeDesejado(nptAnterior.getVolumeDesejado());
		novaNpt.setTempoHInfusaoSolucao(nptAnterior.getTempoHInfusaoSolucao());
		novaNpt.setTempoHInfusaoLipidios(nptAnterior.getTempoHInfusaoLipidios());
		novaNpt.setCaloriasDia(nptAnterior.getCaloriasDia());
		novaNpt.setCaloriasKgDia(nptAnterior.getCaloriasKgDia());
		novaNpt.setRelCalNProtNitrogenio(nptAnterior.getRelCalNProtNitrogenio());
		novaNpt.setPercCalAminoacidos(nptAnterior.getPercCalAminoacidos());
		novaNpt.setPercCalLipidios(nptAnterior.getPercCalLipidios());
		novaNpt.setPercCalGlicose(nptAnterior.getPercCalGlicose());
		novaNpt.setGlicoseRelGlicLipid(nptAnterior.getGlicoseRelGlicLipid());
		novaNpt.setLipidiosRelGlicLipid(nptAnterior.getLipidiosRelGlicLipid());
		novaNpt.setRelacaoCalcioFosforo(nptAnterior.getRelacaoCalcioFosforo());
		novaNpt.setConcGlicComLipidios(nptAnterior.getConcGlicComLipidios());
		novaNpt.setConcGlicSemLipidios(nptAnterior.getConcGlicSemLipidios());
		novaNpt.setTaxaInfusaoLipidios(nptAnterior.getTaxaInfusaoLipidios());
		novaNpt.setOsmolaridadeComLipidios(nptAnterior.getOsmolaridadeComLipidios());
		novaNpt.setOsmolaridadeSemLipidios(nptAnterior.getOsmolaridadeSemLipidios());
		novaNpt.setPcaAtdSeq(nptAnterior.getPcaAtdSeq());
		novaNpt.setPcaCriadoEm(nptAnterior.getPcaCriadoEm());
		novaNpt.setParamTipoLipidio(nptAnterior.getParamTipoLipidio());
		novaNpt.setDuracaoTratSolicitado(nptAnterior.getDuracaoTratSolicitado());
		novaNpt.setBombaInfusao(nptAnterior.getBombaInfusao());
		novaNpt.setServidor(nptAnterior.getServidor());
		novaNpt.setCriadoEm(new Date());
	}

	
	
/*******************************************************************************************************
 * 
 *******************************************************************************************************/
	
//	
//	/**
//	 * Método que atualiza os cuidados de uma prescrição
//	 * @param cuidado
//	 * @param pmeAtdSeq
//	 * @param dtFimLida
//	 */
//	private void atualizarPrescricaoCuidado(
//			MpmPrescricaoMedica prescricaoMedicaNova,
//			MpmPrescricaoCuidado cuidado, Integer pmeAtdSeq, Date dtFimLida) {
//		MpmPrescricaoCuidadoDAO mpmPrescricaoCuidadosDAO = getPrescricaoCuidadosDAO();
//		MpmPrescricaoCuidado cuidadoAtualizar = mpmPrescricaoCuidadosDAO
//		.obterPrescricaoCuidadoPorId(pmeAtdSeq, cuidado.getId()
//				.getSeq());
//		
//		cuidadoAtualizar.setDthrFim(dtFimLida);	
//		cuidadoAtualizar.setServidorValidaMovimentacao(cuidado.getServidorValidaMovimentacao());
//	
//		//TODO: Não utilizar mais este método quando for implementado
//		//o método contendo triggers e procedures
//		mpmPrescricaoCuidadosDAO.flush();
//	}
	
	/**
	 * Método que insere cuidados em uma nova prescrição
	 * @param prescricaoMedicaNova
	 * @param cuidadoAntigo
	 * @param dtInicio
	 * @param dataFim
	 * @param pcuSeqNovo
	 * @throws BaseException 
	 */
	private void inserirPrescricaoCuidado(
			MpmPrescricaoMedica prescricaoMedicaNova,
			MpmPrescricaoCuidado cuidadoAntigo, Date dtInicio, Date dataFim,
			Integer pcuSeqNovo, Boolean isCopiado, String nomeMicrocomputador) throws BaseException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		/*----- Monta o novo cuidado de prescrição -----*/
		MpmPrescricaoCuidado prescricaoCuidadoNovo = new MpmPrescricaoCuidado();
		//Monta id
		MpmPrescricaoCuidadoId mpmPrescricaoCuidadosId = new MpmPrescricaoCuidadoId();
		mpmPrescricaoCuidadosId.setAtdSeq(prescricaoMedicaNova.getId().getAtdSeq());
		mpmPrescricaoCuidadosId.setSeq(pcuSeqNovo.longValue());
		//Seta o id
		prescricaoCuidadoNovo.setId(mpmPrescricaoCuidadosId);
		//Seta a referência para a Prescrição Médica criada
		prescricaoCuidadoNovo.setPrescricaoMedica(prescricaoMedicaNova);
		//Seta os demais campos do cuidado anterior
		prescricaoCuidadoNovo.setMpmTipoFreqAprazamentos(cuidadoAntigo.getMpmTipoFreqAprazamentos());
		prescricaoCuidadoNovo.setMpmCuidadoUsuais(cuidadoAntigo.getMpmCuidadoUsuais());
		prescricaoCuidadoNovo.setIndPendente(DominioIndPendenteItemPrescricao.P);
		prescricaoCuidadoNovo.setIndItemRecomendadoAlta(cuidadoAntigo.getIndItemRecomendadoAlta());
		prescricaoCuidadoNovo.setDthrInicio(dtInicio);
		prescricaoCuidadoNovo.setFrequencia(cuidadoAntigo.getFrequencia());
		prescricaoCuidadoNovo.setDescricao(cuidadoAntigo.getDescricao());
		prescricaoCuidadoNovo.setDthrFim(dataFim);
		//46021 - ATUALIZAR CAMPOS PRCR ANTERIOR
		prescricaoCuidadoNovo.setPcuSeqPrcrAnt(cuidadoAntigo.getId().getSeq());
		prescricaoCuidadoNovo.setPcuAtdSeqPrcrAnt(cuidadoAntigo.getId().getAtdSeq());
		
		/*--------------------------------------------------------------------------- */
		//TODO: As três linhas abaixo pertencem à trigger ORADB MPMT_PCU_BRI e deverá
		// ser retirado daqui quando esta for implementada
		
		prescricaoCuidadoNovo.setServidor(servidorLogado);
		prescricaoCuidadoNovo.setCriadoEm(new Date());
		prescricaoCuidadoNovo.setIndItemRecTransferencia(false);
		/*--------------------------------------------------------------------------- */
		
		//Persiste a prescrição de cuidado
		getManterPrescricaoCuidadoON().incluir(prescricaoCuidadoNovo, isCopiado, nomeMicrocomputador, 
											   new Date());
	}
	
	
//	/**
//	 * Método que atualiza as dietas de uma prescrição
//	 * @param prescricaoMedicaNova
//	 * @param dieta
//	 * @param pmeAtdSeq
//	 * @param dtFimLida
//	 */
//	private void atualizarPrescricaoDieta(
//			MpmPrescricaoMedica prescricaoMedicaNova,
//			MpmPrescricaoDieta dieta, Integer pmeAtdSeq, Date dtFimLida) {
//		
//		MpmPrescricaoDietaDAO mpmPrescricaoDietaDAO = getPrescricaoDietaDAO();
//		
//		MpmPrescricaoDieta dietaAtualizar = mpmPrescricaoDietaDAO
//		.obterPrescricaoDietaPorId(pmeAtdSeq, dieta.getId().getSeq());
//		
//		dietaAtualizar.setDthrFim(dtFimLida);	
//		dietaAtualizar.setServidorMovimentado(dieta.getServidorValidacao());
//		
//		//TODO: Não utilizar mais este método quando for implementado
//		//o método contendo triggers e procedures
//		mpmPrescricaoDietaDAO.flush();
//	}
	
	/**
	 * Método que insere uma nova prescrição dieta
	 * @param prescricaoMedicaNova
	 * @param dietaAntiga
	 * @param dtInicio
	 * @param dataFim
	 * @param pdtSeq
	 * @throws BaseException 
	 */
	private MpmPrescricaoDieta inserirPrescricaoDieta(MpmPrescricaoMedica prescricaoMedicaNova, MpmPrescricaoDieta dietaAntiga, 
			Date dtInicio, Date dataFim, Integer pdtSeq, Boolean isCopiado, String nomeMicrocomputador) throws BaseException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		/*----- Monta a nova dieta de prescrição -----*/
		MpmPrescricaoDieta prescricaoDietaNova = new MpmPrescricaoDieta();
		//Monta id
		MpmPrescricaoDietaId mpmPrescricaoDietaId = new MpmPrescricaoDietaId();
		mpmPrescricaoDietaId.setAtdSeq(prescricaoMedicaNova.getId().getAtdSeq());
		mpmPrescricaoDietaId.setSeq(pdtSeq.longValue());
		//Seta o id
		prescricaoDietaNova.setId(mpmPrescricaoDietaId);
		//Seta a referência para a Prescrição Médica
		prescricaoDietaNova.setPrescricaoMedica(prescricaoMedicaNova);
		//Seta os demais campos da dieta anterior
		prescricaoDietaNova.setIndPendente(DominioIndPendenteItemPrescricao.P);
		prescricaoDietaNova.setIndAvalNutricionista(dietaAntiga.getIndAvalNutricionista());
		prescricaoDietaNova.setIndItemRecomendadoAlta(dietaAntiga.getIndItemRecomendadoAlta());
		prescricaoDietaNova.setDthrInicio(dtInicio);
		prescricaoDietaNova.setObservacao(dietaAntiga.getObservacao());
		prescricaoDietaNova.setDthrFim(dataFim);
		prescricaoDietaNova.setIndBombaInfusao(dietaAntiga.getIndBombaInfusao());
		//46021 - ATUALIZAR CAMPOS PRCR ANTERIOR
		prescricaoDietaNova.setPdtSeqPrcrAnt(dietaAntiga.getId().getSeq());
		prescricaoDietaNova.setPdtAtdSeqPrcrAnt(dietaAntiga.getId().getAtdSeq());
		/*--------------------------------------------------------------------------- */
		//TODO: As três linhas abaixo pertencem à trigger ORADB MPMT_PDT_BRI e deverá
		// ser retirado daqui quando esta for implementada
		
		prescricaoDietaNova.setRapServidores(servidorLogado);
		prescricaoDietaNova.setCriadoEm(new Date());
		prescricaoDietaNova.setIndItemRecTransferencia(false);
		/*--------------------------------------------------------------------------- */
		
		//Persiste a dieta da prescrição
		getManterPrescricaoDietaRN().inserirPrescricaoDieta(prescricaoDietaNova, isCopiado, nomeMicrocomputador);
		
		return prescricaoDietaNova;
	}
	
	/**
	 * Método que insere um novo procedimento da prescrição
	 * @param pprSeqNovo
	 * @param pmeAtdSeq
	 * @param dataInicio
	 * @param dataFim
	 * @param procedimento
	 *  
	 */
	private MpmPrescricaoProcedimento inserirProcedimento(MpmPrescricaoMedica prescricaoMedicaNova, Long pprSeqNovo, Integer pmeAtdSeq,
			Date dataInicio, Date dataFim,
			MpmPrescricaoProcedimento procedimento, Boolean isCopiado, String nomeMicrocomputador) throws BaseException {
		
		MpmPrescricaoProcedimentoDAO mpmPrescricaoProcedimentoDAO = getMpmPrescricaoProcedimentoDAO();
		MpmPrescricaoProcedimento procedimentoAntigo = mpmPrescricaoProcedimentoDAO
		.obterProcedimentoPeloId(pmeAtdSeq, procedimento
				.getId().getSeq());
		
		MpmPrescricaoProcedimento procedimentoNovo = new MpmPrescricaoProcedimento();
		
		//TODO: Não setar o seq abaixo quando for implementado
		//o método contendo triggers, procedures e sequence
		//MpmPrescricaoProcedimentoId id = new MpmPrescricaoProcedimentoId();
		//id.setAtdSeq(pmeAtdSeq);
		//id.setSeq(pprSeqNovo);
		//procedimentoNovo.setId(id);
		
		//Seta a referência para a Prescrição Médica criada
		procedimentoNovo.setPrescricaoMedica(prescricaoMedicaNova);
		
		procedimentoNovo.setProcedimentoCirurgico(procedimentoAntigo.getProcedimentoCirurgico());
		procedimentoNovo.setMatCodigo(procedimentoAntigo.getMatCodigo());
		procedimentoNovo.setProcedimentoEspecialDiverso(procedimentoAntigo.getProcedimentoEspecialDiverso());
		procedimentoNovo.setDthrInicio(dataInicio);
		procedimentoNovo.setIndPendente(DominioIndPendenteItemPrescricao.P);
		procedimentoNovo.setJustificativa(procedimentoAntigo.getJustificativa());
		procedimentoNovo.setQuantidade(procedimentoAntigo.getQuantidade());
		procedimentoNovo.setInformacaoComplementar(procedimentoAntigo.getInformacaoComplementar());
		procedimentoNovo.setDthrFim(dataFim);
		procedimentoNovo.setDuracaoTratamentoSolicitado(procedimentoAntigo.getDuracaoTratamentoSolicitado());
		procedimentoNovo.setDigitacaoSolicitante(procedimentoAntigo.getDigitacaoSolicitante());
		//46021 - ATUALIZAR CAMPOS PRCR ANTERIOR
		procedimentoNovo.setPprSeqPrcrAnt(procedimentoAntigo.getId().getSeq());
		procedimentoNovo.setPprAtdSeqPrcrAnt(procedimentoAntigo.getId().getAtdSeq());
		/*--------------------------------------------------------------------------- */
		//TODO: As três linhas abaixo pertencem à trigger ORADB MPMT_PPR_BRI e deverá
		// ser retirado daqui quando esta for implementada
		//procedimentoNovo.setServidor(usuarioLogado);
		//procedimentoNovo.setCriadoEm(new Date());
		/*--------------------------------------------------------------------------- */
		//TODO: Não chamar o método abaixo quando for implementado
		//o método contendo triggers, procedures e sequence
		//mpmPrescricaoProcedimentoDAO.inserir(procedimentoNovo);
				
		this.getPrescreverProcedimentoEspecialRN().inserirPrescricaoProcedimento(procedimentoNovo, isCopiado, nomeMicrocomputador);
		
		return procedimentoNovo;
	}
	
	/**
	 * Método que insere objeto MpmModoUsoPrescricaoProcedimento
	 * @param pprSeqNovo
	 * @param pmeAtdSeq
	 * @param seq
	 *  
	 */
	private void inserirModoUsoPrescricaoProcedimento(
			MpmPrescricaoProcedimento novoProcedimento, Integer pmeAtdSeq,
			Long seq) throws ApplicationBusinessException {
		
		MpmModoUsoPrescProcedDAO mpmModoUsoPrescProcedsDAO = getMpmModoUsoPrescProcedDAO();
		List<MpmModoUsoPrescProced> listaModosUsoPresc = mpmModoUsoPrescProcedsDAO
				.pesquisarModoUsoPrescProcedimentosPorAtdSeq(pmeAtdSeq, seq);
		
		for (MpmModoUsoPrescProced usoPrescAntigo: listaModosUsoPresc){
			MpmModoUsoPrescProced usoPrescNovo = new MpmModoUsoPrescProced();
			
//			MpmModoUsoPrescProcedId id = new MpmModoUsoPrescProcedId();
//			id.setPprAtdSeq(pmeAtdSeq);
//			id.setPprSeq(pprSeqNovo.longValue());
//			id.setSeqp(usoPrescAntigo.getId().getSeqp());
//			usoPrescNovo.setId(id);
			
			usoPrescNovo.setTipoModUsoProcedimento(usoPrescAntigo.getTipoModUsoProcedimento());
			usoPrescNovo.setQuantidade(usoPrescAntigo.getQuantidade());
			novoProcedimento.addModoUso(usoPrescNovo);
			
			//TODO: Não chamar o método abaixo quando for implementado
			//o método contendo triggers, procedures e sequence
			//mpmModoUsoPrescProcedsDAO.inserir(usoPrescNovo);
			
			this.getManterModoUsoPrescProcedRN().inserirModoUsoPrescProced(usoPrescNovo);
		}
	}
	
	/**
	 * Método que atualiza a chave da tabela mpa_uso_ord_procedimentos
	 * @param pprSeqNovo
	 * @param pmeAtdSeq
	 * @param seq
	 */
	private void atualizarMpaUsoOrdProcedimento(Long pprSeqAnt, Integer pmeAtdSeq, Long pprSeqNovo){
		MpmPrescricaoMdtoDAO mpmPrescricaoMdtoDAO = getPrescricaoMdtosDAO();
		if (pprSeqAnt != null){
			List<MpaUsoOrdProcedimento> listaUsoOrdProcedimentos = getProtocoloFacade()
			.pesquisarUsoOrdProcedimentosPorPrescricaoMedicamento(pprSeqAnt.longValue(), pmeAtdSeq);
			MpmPrescricaoMdto medicamentoNovo = mpmPrescricaoMdtoDAO.obterMedicamentoPeloId(pmeAtdSeq,pprSeqNovo);
			MpmPrescricaoMdto medicamentoAnterior = mpmPrescricaoMdtoDAO.obterMedicamentoPeloId(pmeAtdSeq,pprSeqAnt.longValue());
			for (MpaUsoOrdProcedimento procedAtualizar: listaUsoOrdProcedimentos){
				procedAtualizar.setPrescricaoMedicamento(medicamentoNovo);
				procedAtualizar.setPrescricaoMedicamentoAnt(medicamentoAnterior);	
				
				//TODO: Não chamar o método abaixo quando for implementado
				//o método contendo triggers, procedures e sequence
				//getProtocoloFacade().flush();
			}
			
		}
		
	}
	

	private Boolean validarParecerNaoVerificado(MpmItemPrescricaoMdto itemMedicamento) {
		if(itemMedicamento.getJustificativaUsoMedicamento() != null) {
			List<MpmParecerUsoMdto> parecerUso = mpmParecerUsoMdtosDAO.listaParecerPelaJustificativaUso(itemMedicamento.getJustificativaUsoMedicamento().getSeq());
			if(parecerUso != null && !parecerUso.isEmpty()) {
				for(MpmParecerUsoMdto parecer : parecerUso) {
					if(DominioSimNao.N.equals(parecer.getIndParecerVerificado())) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * Verifica se deve represcrever itens de medicamento
	 * @param dtInicio
	 * @param medicamento
	 * @param pmeAtdSeq
	 * @param pmdSeqLido
	 * @return
	 */
	protected boolean verificarReprescreverItemMedicamento(Date dtInicio,
			MpmPrescricaoMdto medicamento, Integer pmeAtdSeq, Long pmdSeqLido) throws ApplicationBusinessException{
		
		MpmPrescricaoMdtoDAO mpmPrescricaoMdtoDAO = getPrescricaoMdtosDAO();
		MpmItemPrescricaoMdtoDAO mpmItemPrescricaoMdtosDAO = getItemPrescricaoMdtosDAO();
		List<MpmItemPrescricaoMdto> listaItensMedicamentos = mpmItemPrescricaoMdtosDAO.pesquisarItensPrescricaoMdtos(pmeAtdSeq, pmdSeqLido, null, null);
		Date dthrInicio = null;
		Boolean represcrever = false;
		Boolean parecerNaoVerificado = false;
		
		for (MpmItemPrescricaoMdto itemMedicamento: listaItensMedicamentos){
			parecerNaoVerificado = validarParecerNaoVerificado(itemMedicamento);

			if(parecerNaoVerificado) {
				if(itemMedicamento.getDuracaoTratAprovado() == null 
						&& Boolean.TRUE.equals(itemMedicamento.getMedicamento().getAfaTipoUsoMdtos().getIndAntimicrobiano())) {
					Integer horasSuspProf = 0; 
					horasSuspProf = parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_HORAS_SUSP_PROFILAXIA);
					dthrInicio = dtInicio;
					Long nroDias = (dthrInicio.getTime()-medicamento.getDthrInicioTratamento().getTime())/86400000;
					if ((nroDias.intValue() + 1) > medicamento.getDuracaoTratSolicitado().intValue() || ( (nroDias*24) > horasSuspProf )){
						represcrever = true;
						continue;
					}
				}
			}	
				
			if (medicamento.getDuracaoTratSolicitado() == null ||
					(itemMedicamento.getDuracaoTratAprovado() == null &&
							itemMedicamento.getMedicamento().getAfaTipoUsoMdtos().getIndAvaliacao())){
				continue;
			}
			
			if (itemMedicamento.getDuracaoTratAprovado() != null && medicamento.getDthrInicioTratamento()!=null){
				dthrInicio = dtInicio;
				Long nroDias = (dthrInicio.getTime()-medicamento.getDthrInicioTratamento().getTime())/86400000;
				if ((nroDias.intValue() + 1) > itemMedicamento.getDuracaoTratAprovado().intValue()){
					represcrever = true;
				}
				
			}
			else if (medicamento.getDuracaoTratSolicitado() != null){
				Integer pmdAtdSeqPesq = medicamento.getId().getAtdSeq();
				Long pmdSeqPesq = medicamento.getId().getSeq();
				MpmPrescricaoMdto medicamentoAvaliado = mpmPrescricaoMdtoDAO.obterMedicamentoAvaliacao(pmdAtdSeqPesq, pmdSeqPesq);
				if (medicamentoAvaliado != null){
					pmdAtdSeqPesq = medicamentoAvaliado.getId().getAtdSeq();
				}
				else{
					dthrInicio = dtInicio;
					if(medicamento.getDthrInicioTratamento() == null) {
						return false;
					}
					Long nroDias = (dthrInicio.getTime()- (medicamento.getDthrInicioTratamento() != null ? medicamento.getDthrInicioTratamento().getTime() : 0))/86400000;
					if ((nroDias.intValue() + 1) > medicamento.getDuracaoTratSolicitado().intValue()){
						represcrever = true;
						
					}
				}
			}
		}
		return represcrever;
	}
	
	/**
	 * Método que represcreve os medicamentos
	 * @param pmdAtdSeq
	 * @param pmdSeq
	 *  
	 */
	public void represcrever(MpmPrescricaoMedica prescricaoMedicaNova, Integer pmdAtdSeq, Long pmdSeq, Date dtInicio, Date dthrFim,
			Date dtFimLida, boolean temPrescricaoFutura, String nomeMicrocomputador)
			throws BaseException {
		IPrescricaoMedicaFacade prescricaoMedicaFacade = getPrescricaoMedicaFacade();
		
		//O atributo pendente é setado para utilizar o mesmo comportamento da cópia de pendentes para os ítens de medicamento
		final boolean pendente = true;
		
		if (temPrescricaoFutura){
			dthrFim = dtFimLida;
		}
		//Obtém o medicamento para represcrever
		MpmPrescricaoMdto medicamentoReprescrever = this
				.obterMedicamentoReprescrever(prescricaoMedicaNova, pmdAtdSeq,
						pmdSeq, dtInicio, dthrFim);
		//Obtém o conjunto de ítens de medicamentos copiados
		List<MpmItemPrescricaoMdto> itensReprescrever = this
				.pesquisarItensMedicamentosCopiar(pmdAtdSeq, pmdSeq,
						medicamentoReprescrever, pendente);
		
		medicamentoReprescrever.setItensPrescricaoMdtos(itensReprescrever);
		//Persiste o medicamento e seus ítens
		prescricaoMedicaFacade.persistirPrescricaoMedicamento(medicamentoReprescrever, nomeMicrocomputador, null);	
		//Atualiza usoOrdMedicamentos
		atualizarUsoOrdMdtos(pmdAtdSeq, pmdSeq, medicamentoReprescrever.getId().getSeq());
	}
	
	/**
	 * Método que providencia a cópia dos medicamentos e ítens de medicamentos pendentes
	 * @param pmeAtdSeq
	 * @param pmdSeq
	 * @param seqGerado
	 * @param dtInicio
	 * @param dataFim
	 *  
	 */
	private void copiarPendentes(MpmPrescricaoMedica prescricaoMedicaNova,
			Integer pmeAtdSeq, Long pmdSeqAntigo, Date dtInicio, Date dataFim, String nomeMicrocomputador)
			throws BaseException {
		IPrescricaoMedicaFacade prescricaoMedicaFacade = getPrescricaoMedicaFacade();
		final boolean pendente = true;
		
		//Obtém o medicamento pendente copiado
		MpmPrescricaoMdto medicamentoCopiado = this.obterMedicamentoCopiar(prescricaoMedicaNova, pmeAtdSeq, pmdSeqAntigo, dtInicio, dataFim, pendente);
		//Obtém o conjunto de ítens de medicamentos copiados
		List<MpmItemPrescricaoMdto> itensCopiados = this
				.pesquisarItensMedicamentosCopiar(pmeAtdSeq, pmdSeqAntigo,
						medicamentoCopiado, pendente);
		
		//Seta os itens copiados
		medicamentoCopiado.setItensPrescricaoMdtos(itensCopiados);
		//Persiste o medicamento e seus ítens
		prescricaoMedicaFacade.persistirPrescricaoMedicamento(medicamentoCopiado, true, nomeMicrocomputador,null);
		//Atualiza usoOrdMedicamentos
		atualizarUsoOrdMdtos(pmeAtdSeq, pmdSeqAntigo, medicamentoCopiado.getId().getSeq());
	}
	
	/**
	 * Método que providencia a cópia dos medicamentos e ítens de medicamentos não pendentes
	 * @param pmeAtdSeq
	 * @param pmdSeq
	 * @param seqGerado
	 * @param dtInicio
	 * @param dataFim
	 *  
	 */
	private void copiarNaoPendentes(MpmPrescricaoMedica prescricaoMedicaNova,
			Integer pmeAtdSeq, Long pmdSeqAntigo, Date dtInicio, Date dataFim, String nomeMicrocomputador)
			throws BaseException {
		IPrescricaoMedicaFacade prescricaoMedicaFacade = getPrescricaoMedicaFacade();
		final boolean pendente = false;
		
		//Obtém o medicamento pendente copiado
		MpmPrescricaoMdto medicamentoCopiado = this.obterMedicamentoCopiar(
				prescricaoMedicaNova, pmeAtdSeq, pmdSeqAntigo, dtInicio,
				dataFim, pendente);
		//Obtém o conjunto de ítens de medicamentos copiados
		List<MpmItemPrescricaoMdto> itensCopiados = this
				.pesquisarItensMedicamentosCopiar(pmeAtdSeq, pmdSeqAntigo,
						medicamentoCopiado, pendente);
		
		//Seta os itens copiados
		medicamentoCopiado.setItensPrescricaoMdtos(itensCopiados);
		//Persiste o medicamento e seus ítens
		prescricaoMedicaFacade.persistirPrescricaoMedicamento(medicamentoCopiado, true, nomeMicrocomputador,null);
		//Copia o parecer de ítens de medicamentos
		this.copiarParecerItensMedicamentos(pmeAtdSeq, pmdSeqAntigo);
		//Atualiza usoOrdMedicamentos
		atualizarUsoOrdMdtos(pmeAtdSeq, pmdSeqAntigo, medicamentoCopiado.getId().getSeq());
	}
	
	/**
	 * Método que copia o parecer de ítens de medicamentos
	 * @param pmeAtdSeq
	 * @param pmdSeqAntigo
	 * @throws ApplicationBusinessException
	 */
	private void copiarParecerItensMedicamentos(Integer pmeAtdSeq,Long pmdSeqAntigo) throws ApplicationBusinessException {
		IPrescricaoMedicaFacade prescricaoMedicaFacade = getPrescricaoMedicaFacade();
		MpmItemPrescParecerMdtoDAO mpmItemPrescParecerMdtoDAO = getMpmItemPrescParecerMdtoDAO();
		
		List<MpmItemPrescParecerMdto> listaItensParecerMedicamento = mpmItemPrescParecerMdtoDAO
				.pesquisarItensParecerMedicamentos(pmeAtdSeq, pmdSeqAntigo);
		
		for (MpmItemPrescParecerMdto itemParecerAntigo: listaItensParecerMedicamento){
			MpmItemPrescParecerMdto itemParecerNovo = new MpmItemPrescParecerMdto();
			MpmItemPrescParecerMdtoId id = new MpmItemPrescParecerMdtoId();
			id.setImePmdAtdSeq(pmeAtdSeq);
			id.setImePmdSeq(pmdSeqAntigo);
			id.setImeMedMatCodigo(itemParecerAntigo.getId().getImeMedMatCodigo());
			id.setImeSeqp(itemParecerAntigo.getId().getImeSeqp());
			itemParecerNovo.setId(id);
			itemParecerNovo.setMpmParecerUsoMdtos(itemParecerAntigo.getMpmParecerUsoMdtos());
			
			prescricaoMedicaFacade.persistirParecerItemPrescricaoMedicamento(itemParecerNovo);
		}
	}
	
	/**
	 * Atualiza registros da tabela MpaUsoOrdMdtos
	 * @param pmeAtdSeq
	 * @param pmdSeqAntigo
	 * @param pmdSeqNovo
	 */
	private void atualizarUsoOrdMdtos(Integer pmeAtdSeq, Long pmdSeqAntigo, Long pmdSeqNovo){
		MpmPrescricaoMdtoDAO mpmPrescricaoMdtoDAO = getPrescricaoMdtosDAO();
		
		MpmPrescricaoMdto prescricaoMedicamentoNovo = mpmPrescricaoMdtoDAO.obterMedicamentoPeloId(pmeAtdSeq, pmdSeqNovo);
		MpmPrescricaoMdto prescricaoMedicamentoAnterior = mpmPrescricaoMdtoDAO.obterMedicamentoPeloId(pmeAtdSeq, pmdSeqAntigo);
		
		List<MpaUsoOrdMdto> listaUsoOrds = getProtocoloFacade().pesquisarUsosOrdMdtos(pmeAtdSeq, pmdSeqAntigo);
		for (MpaUsoOrdMdto usoOrd: listaUsoOrds){
			usoOrd.setPrescricaoMedicamento(prescricaoMedicamentoNovo);
			usoOrd.setPrescricaoMedicamentoAnt(prescricaoMedicamentoAnterior);
		}
		//TODO: Não utilizar mais este método quando for implementado
		//o método contendo triggers e procedures
		//getProtocoloFacade().flush();
	}
	
	
	/**
	 * Método que retorna a lista de ítens de medicamentos a copiar
	 * @param pmeAtdSeq
	 * @param pmdSeqAntigo
	 * @param medicamentoCopiado
	 * @return
	 */
	private List<MpmItemPrescricaoMdto> pesquisarItensMedicamentosCopiar(
			Integer pmeAtdSeq, Long pmdSeqAntigo,
			MpmPrescricaoMdto medicamentoCopiado, Boolean pendente) {
		
		MpmItemPrescricaoMdtoDAO mpmItemPrescricaoMdtosDAO = getItemPrescricaoMdtosDAO();
		List<MpmItemPrescricaoMdto> listaItensAntigos = mpmItemPrescricaoMdtosDAO
				.pesquisarItensPrescricaoMdtos(pmeAtdSeq, pmdSeqAntigo, null,
						null);
		
		List<MpmItemPrescricaoMdto> itensNovos = new ArrayList<MpmItemPrescricaoMdto>();
		
		for (MpmItemPrescricaoMdto itemAntigo: listaItensAntigos){
			
			MpmItemPrescricaoMdto itemNovo = new MpmItemPrescricaoMdto();
			MpmItemPrescricaoMdtoId id = new MpmItemPrescricaoMdtoId();
			id.setPmdAtdSeq(pmeAtdSeq);
			id.setMedMatCodigo(itemAntigo.getId().getMedMatCodigo());
			id.setSeqp(itemAntigo.getId().getSeqp());
			itemNovo.setId(id);
			
			itemNovo.setPrescricaoMedicamento(medicamentoCopiado);
			itemNovo.setMedicamento(itemAntigo.getMedicamento());
			if (pendente){
				itemNovo.setJustificativaUsoMedicamento(null);
				itemNovo.setUsoMdtoAntimicrobia(null);
				itemNovo.setDuracaoTratAprovado(null);
			}
			else{
				itemNovo.setJustificativaUsoMedicamento(itemAntigo.getJustificativaUsoMedicamento());
				itemNovo.setUsoMdtoAntimicrobia(itemAntigo.getUsoMdtoAntimicrobia());
				itemNovo.setDuracaoTratAprovado(itemAntigo.getDuracaoTratAprovado());
			}
			
			itemNovo.setFormaDosagem(itemAntigo.getFormaDosagem());
			itemNovo.setDose(itemAntigo.getDose());
			itemNovo.setMdtoAguardaEntrega(itemAntigo.getMdtoAguardaEntrega());
			itemNovo.setQtdeCalcSist24h(itemAntigo.getQtdeCalcSist24h());
			
			itemNovo.setDoseCalculada(itemAntigo.getDoseCalculada());
			itemNovo.setQtdeMgKg(itemAntigo.getQtdeMgKg());
			itemNovo.setQtdeMgSuperfCorporal(itemAntigo.getQtdeMgSuperfCorporal());
			itemNovo.setTipoCalculoDose(itemAntigo.getTipoCalculoDose());
			itemNovo.setObservacao(itemAntigo.getObservacao());
			itemNovo.setQtdeParamCalculo(itemAntigo.getQtdeParamCalculo());
			itemNovo.setBaseParamCalculo(itemAntigo.getBaseParamCalculo());
			itemNovo.setUnidadeMedidaMedica(itemAntigo.getUnidadeMedidaMedica());
			itemNovo.setDuracaoParamCalculo(itemAntigo.getDuracaoParamCalculo());
			itemNovo.setUnidDuracaoCalculo(itemAntigo.getUnidDuracaoCalculo());
			itemNovo.setParamCalculoPrescricao(itemAntigo.getParamCalculoPrescricao());
			
			itensNovos.add(itemNovo);
			
		}
		
		return itensNovos;
	}
	
	/**
	 * Método que obtém o medicamento pendente a copiar
	 * @param pmeAtdSeq
	 * @param pmdSeqAntigo
	 * @param dtInicio
	 * @param dataFim
	 * @throws ApplicationBusinessException
	 */
	private MpmPrescricaoMdto obterMedicamentoCopiar(MpmPrescricaoMedica prescricaoMedicaNova, Integer pmeAtdSeq,
			Long pmdSeqAntigo, Date dtInicio, Date dataFim, Boolean pendente)
			throws ApplicationBusinessException {

		MpmPrescricaoMdtoDAO mpmPrescricaoMdtoDAO = getPrescricaoMdtosDAO();
		MpmPrescricaoMdto medicamentoAntigo = mpmPrescricaoMdtoDAO.obterMedicamentoPeloId(pmeAtdSeq, pmdSeqAntigo);
		
		MpmPrescricaoMdto medicamentoNovo = new MpmPrescricaoMdto();
		MpmPrescricaoMdtoId id = new MpmPrescricaoMdtoId();
		id.setAtdSeq(pmeAtdSeq);
		medicamentoNovo.setId(id);
		//Seta a referência para a Prescrição Médica criada
		medicamentoNovo.setPrescricaoMedica(prescricaoMedicaNova);
		medicamentoNovo.setTipoVelocAdministracao(medicamentoAntigo.getTipoVelocAdministracao());
		medicamentoNovo.setTipoFreqAprazamento(medicamentoAntigo.getTipoFreqAprazamento());
		medicamentoNovo.setViaAdministracao(medicamentoAntigo.getViaAdministracao());
		medicamentoNovo.setDthrInicio(dtInicio);
		medicamentoNovo.setIndSeNecessario(medicamentoAntigo.getIndSeNecessario());
		medicamentoNovo.setIndBombaInfusao(medicamentoAntigo.getIndBombaInfusao());
		medicamentoNovo.setIndItemRecomendadoAlta(medicamentoAntigo.getIndItemRecomendadoAlta());
		medicamentoNovo.setIndPendente(DominioIndPendenteItemPrescricao.P);
		medicamentoNovo.setFrequencia(medicamentoAntigo.getFrequencia());
		medicamentoNovo.setHoraInicioAdministracao(medicamentoAntigo.getHoraInicioAdministracao());
		medicamentoNovo.setObservacao(medicamentoAntigo.getObservacao());
		medicamentoNovo.setGotejo(medicamentoAntigo.getGotejo());
		medicamentoNovo.setQtdeHorasCorrer(medicamentoAntigo.getQtdeHorasCorrer());
		if (pendente){
			medicamentoNovo.setDuracaoTratSolicitado(null);
			medicamentoNovo.setDthrInicioTratamento(null);			
		}
		else{
			medicamentoNovo.setDuracaoTratSolicitado(medicamentoAntigo.getDuracaoTratSolicitado());
			medicamentoNovo.setDthrInicioTratamento(medicamentoAntigo.getDthrInicioTratamento());		
		}
		medicamentoNovo.setIndSolucao(medicamentoAntigo.getIndSolucao());
		medicamentoNovo.setDthrFim(dataFim);
		medicamentoNovo.setUnidHorasCorrer(medicamentoAntigo.getUnidHorasCorrer());
		medicamentoNovo.setDiluente(medicamentoAntigo.getDiluente());
		medicamentoNovo.setVolumeDiluenteMl(medicamentoAntigo.getVolumeDiluenteMl());
		//46021 - ATUALIZAR CAMPOS PRCR ANTERIOR 
		medicamentoNovo.setPmdSeqPrcrAnt(medicamentoAntigo.getId().getSeq());
		medicamentoNovo.setPmdAtdSeqPrcrAnt(medicamentoAntigo.getId().getAtdSeq());		
		
		return medicamentoNovo;
	}
	
	/**
	 * Método que obtém o medicamento para represcrever
	 * @param pmeAtdSeq
	 * @param pmdSeq
	 * @param dtInicio
	 * @param dataFim
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private MpmPrescricaoMdto obterMedicamentoReprescrever(MpmPrescricaoMedica prescricaoMedicaNova, Integer pmeAtdSeq,
			Long pmdSeq, Date dtInicio, Date dataFim)
			throws ApplicationBusinessException {

		MpmPrescricaoMdtoDAO mpmPrescricaoMdtoDAO = getPrescricaoMdtosDAO();
		MpmPrescricaoMdto medicamentoAntigo = mpmPrescricaoMdtoDAO.obterMedicamentoPeloId(pmeAtdSeq, pmdSeq);
		MpmPrescricaoMdto medicamentoNovo = new MpmPrescricaoMdto();
		MpmPrescricaoMdtoId id = new MpmPrescricaoMdtoId();
		id.setAtdSeq(pmeAtdSeq);
		medicamentoNovo.setId(id);
		
		//Seta a referência para a Prescrição Médica criada
		medicamentoNovo.setPrescricaoMedica(prescricaoMedicaNova);
		medicamentoNovo.setTipoVelocAdministracao(medicamentoAntigo.getTipoVelocAdministracao());
		medicamentoNovo.setTipoFreqAprazamento(medicamentoAntigo.getTipoFreqAprazamento());
		medicamentoNovo.setViaAdministracao(medicamentoAntigo.getViaAdministracao());
		medicamentoNovo.setDthrInicio(dtInicio);
		medicamentoNovo.setIndSeNecessario(medicamentoAntigo.getIndSeNecessario());
		medicamentoNovo.setIndBombaInfusao(medicamentoAntigo.getIndBombaInfusao());
		medicamentoNovo.setIndItemRecomendadoAlta(medicamentoAntigo.getIndItemRecomendadoAlta());
		medicamentoNovo.setIndPendente(DominioIndPendenteItemPrescricao.R);
		medicamentoNovo.setFrequencia(medicamentoAntigo.getFrequencia());
		medicamentoNovo.setHoraInicioAdministracao(medicamentoAntigo.getHoraInicioAdministracao());
		medicamentoNovo.setObservacao(medicamentoAntigo.getObservacao());
		medicamentoNovo.setGotejo(medicamentoAntigo.getGotejo());
		medicamentoNovo.setQtdeHorasCorrer(medicamentoAntigo.getQtdeHorasCorrer());
		medicamentoNovo.setDuracaoTratSolicitado(null);
		medicamentoNovo.setDthrInicioTratamento(null);
		medicamentoNovo.setIndSolucao(medicamentoAntigo.getIndSolucao());
		medicamentoNovo.setDthrFim(dataFim);
		medicamentoNovo.setPrescricaoMdtoReprescritoOrigem(medicamentoAntigo);
		medicamentoNovo.setUnidHorasCorrer(medicamentoAntigo.getUnidHorasCorrer());
		medicamentoNovo.setDiluente(medicamentoAntigo.getDiluente());
		medicamentoNovo.setVolumeDiluenteMl(medicamentoAntigo.getVolumeDiluenteMl());
		//46021 - ATUALIZAR CAMPOS PRCR ANTERIOR 
		medicamentoNovo.setPmdSeqPrcrAnt(medicamentoAntigo.getId().getSeq());
		medicamentoNovo.setPmdAtdSeqPrcrAnt(medicamentoAntigo.getId().getAtdSeq());		
		return medicamentoNovo;
	}
	

//	/**
//	 * Atualiza um determinado medicamento
//	 * @param pmeAtdSeq
//	 * @param dtFimLida
//	 * @param medicamento
//	 * @throws ApplicationBusinessException 
//	 */
//	private void atualizarMedicamento(MpmPrescricaoMedica prescricaoMedicaNova,
//			Integer pmeAtdSeq, Date dtFimLida, MpmPrescricaoMdto medicamento)
//			throws BaseException {
//		MpmPrescricaoMdtoDAO mpmPrescricaoMdtoDAO = getPrescricaoMdtosDAO();
//		
//		MpmPrescricaoMdto medicamentoAtualizar = mpmPrescricaoMdtoDAO
//		.obterMedicamentoPeloId(pmeAtdSeq, medicamento.getId().getSeq());
//		//Seta a referencia para a Prescrição Médica criada.
//		//medicamentoAtualizar.setPrescricaoMedica(prescricaoMedicaNova);
//		
//		medicamentoAtualizar.setDthrFim(dtFimLida);
//		medicamentoAtualizar.setServidorMovimentado(medicamento.getServidorValidacao());
//		getPrescricaoMedicaFacade().persistirPrescricaoMedicamento(medicamentoAtualizar);	
//	}

	
	
	
	/**
	 * Método que insere novos itens de dieta
	 * @param pmeAtdSeq
	 * @param dieta
	 * @param pdtSeq
	 * @throws BaseException 
	 * @throws Exception 
	 */
	private void inserirItensPrescricaoDieta(Integer pmeAtdSeq,
			MpmPrescricaoDieta dieta, Integer pdtSeq,
			MpmPrescricaoDieta dietaNova) throws BaseException {
		
		MpmItemPrescricaoDietaDAO mpmItemPrescricaoDietasDAO = getItemPrescricaoDietaDAO();
		List<MpmItemPrescricaoDieta> listaItensPrescricaoDieta = mpmItemPrescricaoDietasDAO
		.pesquisarItensDieta(pmeAtdSeq, dieta.getId().getSeq());
		
		for (MpmItemPrescricaoDieta itemDieta: listaItensPrescricaoDieta){
			
			MpmItemPrescricaoDieta itemDietaNovo = new MpmItemPrescricaoDieta();
			//Monta id
			MpmItemPrescricaoDietaId mpmItemPrescricaoDietaId = new MpmItemPrescricaoDietaId();
			mpmItemPrescricaoDietaId.setPdtAtdSeq(pmeAtdSeq);
			mpmItemPrescricaoDietaId.setPdtSeq(pdtSeq.longValue());
			mpmItemPrescricaoDietaId.setTidSeq(itemDieta.getId().getTidSeq());
			itemDietaNovo.setId(mpmItemPrescricaoDietaId);
			
			itemDietaNovo.setTipoFreqAprazamento(itemDieta.getTipoFreqAprazamento());
			itemDietaNovo.setQuantidade(itemDieta.getQuantidade());
			itemDietaNovo.setFrequencia(itemDieta.getFrequencia());
			itemDietaNovo.setDuracaoSolicitada(itemDieta.getDuracaoSolicitada());
			itemDietaNovo.setNumVezes(itemDieta.getNumVezes());
			itemDietaNovo.setTipoItemDieta(itemDieta.getTipoItemDieta());
			
			itemDietaNovo.setPrescricaoDieta(dietaNova);
			dietaNova.getItemPrescricaoDieta().add(itemDietaNovo);
			
			//Persiste o item
			getManterPrescricaoDietaRN().inserirItemPrescricaoDieta(itemDietaNovo);
		}
	}
	
	/**
	 * Método que atualiza objetos da tabela MPA_USO_ORD_CUIDADOS
	 * @param pcuSeq
	 * @param pcuSeqAnt
	 * @param pmeAtdSeq
	 *  
	 */
	private void atualizarUsoOrdCuidados(Integer pcuSeq, Integer pcuSeqAnt, Integer pmeAtdSeq) throws ApplicationBusinessException{
		List<MpaUsoOrdCuidado> listaUsoOrdCuidados = null;
		Long longPcuSeqAnt = null;
		
		try{
			if (pcuSeqAnt != null){
				longPcuSeqAnt = pcuSeqAnt.longValue();
				listaUsoOrdCuidados = getProtocoloFacade().pesquisarUsoOrdCuidadosPorPrescricaoCuidado(longPcuSeqAnt, pmeAtdSeq);
			}
			else{
				listaUsoOrdCuidados = new ArrayList<MpaUsoOrdCuidado>();
			}
			
			for (MpaUsoOrdCuidado usoOrdCuidado: listaUsoOrdCuidados){
				//Atualiza a prescricaoCuidado
				MpmPrescricaoCuidado prescricaoCuidado = usoOrdCuidado.getPrescricaoCuidado();
				MpmPrescricaoCuidado prescricaoCuidadoAtualizar = getMpmPrescricaoCuidadoDAO()
				.obterPrescricaoCuidadoPorId(prescricaoCuidado.getId()
						.getAtdSeq(), pcuSeq.longValue());
				usoOrdCuidado.setPrescricaoCuidado(prescricaoCuidadoAtualizar);
				
				//Atualiza a prescricaoCuidadoAnt
				MpmPrescricaoCuidado prescricaoCuidadoAnt = usoOrdCuidado.getPrescricaoCuidadoAnt();
				MpmPrescricaoCuidado prescricaoCuidadoAntAtualizar = getMpmPrescricaoCuidadoDAO()
				.obterPrescricaoCuidadoPorId(prescricaoCuidadoAnt.getId()
						.getAtdSeq(), pcuSeqAnt.longValue());
				usoOrdCuidado.setPrescricaoCuidadoAnt(prescricaoCuidadoAntAtualizar);
			}
			
			//TODO: Não utilizar mais este método quando for implementado
			//o método contendo triggers e procedures
			//getProtocoloFacade().flush();	
		} catch (Exception e) {
			//logError("ERRO COPIAR USO ORD CUIDADOS", e);
			logError(e);
			throw new ApplicationBusinessException(
					ExceptionCode.ERRO_COPIAR_USO_ORD_CUIDADOS, e);
		}
	}
	
	
	/**
	 * Método que atualiza objetos da tabela MPA_USO_ORD_NUTRICOES
	 * @param pcuSeq
	 * @param pcuSeqAnt
	 * @param pmeAtdSeq
	 *  
	 */
	private void atualizarUsoOrdNutricoes(Integer pdtSeq, Integer pdtSeqAnt, Integer pmeAtdSeq) throws ApplicationBusinessException{
		List<MpaUsoOrdNutricao> listaUsoOrdNutricoes = null;
		Long longPdtSeqAnt = null;
		try{
			if (pdtSeqAnt != null){
				longPdtSeqAnt = pdtSeqAnt.longValue();
				listaUsoOrdNutricoes = getProtocoloFacade().pesquisarUsoOrdNutricoesPorPrescricaoDieta(longPdtSeqAnt, pmeAtdSeq);
			}
			else{
				listaUsoOrdNutricoes = new ArrayList<MpaUsoOrdNutricao>();
			}
			
			for (MpaUsoOrdNutricao usoOrdNutricao: listaUsoOrdNutricoes){
				//Atualiza a prescricaoCuidado
				MpmPrescricaoDieta prescricaoDieta = usoOrdNutricao.getPrescricaoDieta();
				MpmPrescricaoDieta prescricaoDietaAtualizar = mpmPrescricaoDietaDAO
				.obterPrescricaoDietaPorId(prescricaoDieta.getId()
						.getAtdSeq(), pdtSeq.longValue());
				usoOrdNutricao.setPrescricaoDieta(prescricaoDietaAtualizar);
				
				//Atualiza a prescricaoCuidadoAnt
				MpmPrescricaoDieta prescricaoDietaAnt = usoOrdNutricao.getPrescricaoDietaAnt();
				MpmPrescricaoDieta prescricaoDietaAntAtualizar = mpmPrescricaoDietaDAO
				.obterPrescricaoDietaPorId(prescricaoDietaAnt.getId()
						.getAtdSeq(), pdtSeqAnt.longValue());
				usoOrdNutricao.setPrescricaoDietaAnt(prescricaoDietaAntAtualizar);
			}
			
			//TODO: Não utilizar mais este método quando for implementado
			//o método contendo triggers e procedures'
			//getProtocoloFacade().flush();
			
		} catch (Exception e) {
			//logError("ERRO COPIAR USO ORD NUTRIÇÕES", e);
			logError(e);
			throw new ApplicationBusinessException(
					ExceptionCode.ERRO_COPIAR_USO_ORD_NUTRICOES, e);
		}
	}
	
	/**
	 * Método que faz a lógica da procedure
	 * ORADB MPMP_ATUALIZA_LOG_PME_USO
	 * conforme definido na pll MPMF_ELAB_PRESCRICAO.PLL
	 */
	private void gerarLogPrescricaoEmUso(MpmPrescricaoMedica prescricaoMedica, String operacao) {
		if (prescricaoMedica == null || operacao == null || operacao.isEmpty()) {
			throw new IllegalArgumentException("Parâmetro 'prescricaoMedica' ou 'operacao' não informado.");
		}
		
		StringBuilder msgLog = new StringBuilder(operacao);
		msgLog.append(" da Prescrição Médica");
		if (prescricaoMedica.getId() != null) {
			msgLog.append(" [SEQ = "+prescricaoMedica.getId().getSeq()+"]");
			msgLog.append(" [ATD_SEQ = "+prescricaoMedica.getId().getAtdSeq()+"]");
		}
		
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");        
		msgLog.append(" [CRIADO_EM = ").append(df.format(prescricaoMedica.getCriadoEm())).append(']')
		.append(" [DT_REFERENCIA = ").append(df.format(prescricaoMedica.getDtReferencia())).append(']')	
		.append(" [IND_SITUACAO = ").append(prescricaoMedica.getSituacao()).append(']')
		.append(" [SER_MATRICULA = ").append(prescricaoMedica.getServidor().getId().getMatricula()).append(']')
		.append(" [SER_VIN_CODIGO = ").append(prescricaoMedica.getServidor().getId().getVinCodigo()).append(']');
		
			
		logInfo(msgLog);		
	}
	
	/**
	 * Melhoria #40184 Parte de Anamnese em EVT_WHEN_NEW_RECORD_INSTANCE
	 * 
	 * @param atd
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Boolean verificarAnamneseCriarPrescricao(AghAtendimentos atd) throws ApplicationBusinessException {
		// CURSOR C_BLOQUEIO
		AghCaractEspecialidades bloqueio = null;
		if (atd.getEspecialidade() != null) {
			bloqueio = this.aghuFacade.obterCaracteristicaEspecialidadePorChavePrimaria(new AghCaractEspecialidadesId(atd.getEspecialidade().getSeq(), DominioCaracEspecialidade.BLOQUEIO_PRESCRICAO_MEDICA));
		}

		// Se NÃO possuir a característica "Bloqueio prescricao medica"
		if (bloqueio == null) { // n_existe
			return Boolean.TRUE; // O botão "Nova Prescrição" deve ficar habilitado
		} else {
			AghParametros pDiasAnamnesePrescr = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_DIAS_ANAMNESE_PRESCR);
			final int dias = pDiasAnamnesePrescr.getVlrNumerico().intValue();

			AghParametros pNroPrescrSemBloq = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_NRO_PRESCR_SEM_BLOQ);
			final Long nPrescr = pNroPrescrSemBloq.getVlrNumerico().longValue();

			// CURSOR ANAMNESE: Verificar se existe anamnese médica registrada
			List<MamAnamneses> listAnamnese = this.ambulatorioFacade.pesquisarAnamnesesVerificarPrescricao(dias, atd.getProntuario());
			if(!listAnamnese.isEmpty()){
				for (MamAnamneses anamnese : listAnamnese) {
					// Validação do CURSOR ANAMNESE: csec_ver_acao_qua_ma('VALIDAR PRESCRICAO MEDICA', ANA.SER_VIN_CODIGO_VALIDA, ANA.SER_MATRICULA_VALIDA) = 'S'
					boolean servidorQualificacaoValidarPrescricaoMedica = cascaFacade.usuarioTemPermissao(anamnese.getServidorValida().getUsuario(), 
							COMPONENTE_PRESCRICAO_MEDICA, METODO_PRESCRICAO_MEDICA);
					if(servidorQualificacaoValidarPrescricaoMedica){
						return Boolean.TRUE;
					}
				}
			}
			
			// CURSOR Q_PRESC
			final Long quantPresc = this.prescricaoMedicaFacade.obterQuantidadePrescricoesVerificarAnamnese(atd.getProntuario());

			if (CoreUtil.igual(quantPresc, nPrescr) || CoreUtil.menor(quantPresc, nPrescr)) {
				throw new ApplicationBusinessException(ExceptionCode.MPM_04174);
			} else if (CoreUtil.maior(quantPresc, nPrescr)) {
				return Boolean.FALSE;
			}
			
		}
		return Boolean.TRUE; // Valor padrão
	}

	/**
	 * Método que retorna a prescrição mais recente de um atendimento
	 * @param atdSeq
	 * @param dtReferencia
	 * @param dataInicio
	 * @return
	 */
	public MpmPrescricaoMedica obterUltimaPrescricaoAtendimento(Integer atdSeq, Date dtReferencia, Date dataInicio){
		MpmPrescricaoMedicaDAO mpmPrescricaoMedicaDAO = getMpmPrescricaoMedicaDAO();
		return mpmPrescricaoMedicaDAO.obterUltimaPrescricaoAtendimento(atdSeq, dtReferencia, dataInicio);
	}
	
	protected MpmItemPrescricaoMdtoDAO getItemPrescricaoMdtosDAO(){
		return mpmItemPrescricaoMdtoDAO;
	}
	
	protected MpmPrescricaoMdtoDAO getPrescricaoMdtosDAO(){
		return mpmPrescricaoMdtoDAO;
	}
	
	protected MpmPrescricaoDietaDAO getPrescricaoDietaDAO(){
		return mpmPrescricaoDietaDAO;
	}
	
	protected MpmItemPrescricaoDietaDAO getItemPrescricaoDietaDAO(){
		return mpmItemPrescricaoDietaDAO;
	}
	
	protected MpmPrescricaoCuidadoDAO getPrescricaoCuidadosDAO(){
		return mpmPrescricaoCuidadoDAO;
	}
	
	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade(){
		return prescricaoMedicaFacade;
	}
	
	protected MpmItemPrescParecerMdtoDAO getMpmItemPrescParecerMdtoDAO(){
		return mpmItemPrescParecerMdtoDAO;
	}
	
	protected MpmPrescricaoProcedimentoDAO getMpmPrescricaoProcedimentoDAO(){
		return mpmPrescricaoProcedimentoDAO;
	}
	
	protected MpmModoUsoPrescProcedDAO getMpmModoUsoPrescProcedDAO(){
		return mpmModoUsoPrescProcedDAO;
	}
	
	protected PrescricaoMedicaRN getPrescricaoMedicaRN() {
		return prescricaoMedicaRN;
	}
	
	protected IProtocoloFacade getProtocoloFacade(){
		return protocoloFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
	protected IAmbulatorioFacade getAmbulatorioFacade(){
		return this.ambulatorioFacade;
	}
	
	protected Date getDataAtual() {
		return new Date();
	}

	/* getters de Facades */

	private IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}

	protected ICadastrosBasicosInternacaoFacade getCadastrosBasicosInternacaoFacade() {
		return cadastrosBasicosInternacaoFacade;
	}
	
	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

	protected MpmPrescricaoMedicaDAO getMpmPrescricaoMedicaDAO() {
		return mpmPrescricaoMedicaDAO;
	}
	
	protected MpmPrescricaoDietaDAO getMpmPrescricaoDietaDAO(){
		return mpmPrescricaoDietaDAO; 
	}
	
	protected MpmPrescricaoCuidadoDAO getMpmPrescricaoCuidadoDAO(){
		return mpmPrescricaoCuidadoDAO;
	}
	
	private ManterModoUsoPrescProcedRN getManterModoUsoPrescProcedRN() {
		return manterModoUsoPrescProcedRN;
	}
	
	private PrescreverProcedimentoEspecialRN getPrescreverProcedimentoEspecialRN() {
		return prescreverProcedimentoEspecialRN;
	}
	
	private ManterPrescricaoDietaRN getManterPrescricaoDietaRN(){
		return manterPrescricaoDietaRN;
	}
	
	private ManterPrescricaoCuidadoON getManterPrescricaoCuidadoON(){
		return manterPrescricaoCuidadoON;
	}
}
