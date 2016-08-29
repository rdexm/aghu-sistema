package br.gov.mec.aghu.internacao.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.NonUniqueResultException;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.business.moduleintegration.InactiveModuleException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.dominio.DominioLocalPaciente;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSexoDeterminante;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoSolicitacaoInternacao;
import br.gov.mec.aghu.dominio.DominioTransacaoAltaPaciente;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.internacao.business.vo.AtualizarEventosVO;
import br.gov.mec.aghu.internacao.business.vo.AtualizarIndicadoresInternacaoVO;
import br.gov.mec.aghu.internacao.business.vo.BuscarLocalInternacaoVO;
import br.gov.mec.aghu.internacao.business.vo.RegistraExtratoLeitoVO;
import br.gov.mec.aghu.internacao.business.vo.RegistrarMovimentoInternacoesVO;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.dao.AinAtendimentosUrgenciaDAO;
import br.gov.mec.aghu.internacao.dao.AinDocsInternacaoDAO;
import br.gov.mec.aghu.internacao.dao.AinExtratoLeitosDAO;
import br.gov.mec.aghu.internacao.dao.AinInternacaoDAO;
import br.gov.mec.aghu.internacao.dao.AinLeitosDAO;
import br.gov.mec.aghu.internacao.dao.AinMovimentoInternacaoDAO;
import br.gov.mec.aghu.internacao.dao.AinQuartosDAO;
import br.gov.mec.aghu.internacao.dao.AinSolicitacoesInternacaoDAO;
import br.gov.mec.aghu.internacao.dao.AinTiposAltaMedicaDAO;
import br.gov.mec.aghu.internacao.dao.AinTiposMovimentoLeitoDAO;
import br.gov.mec.aghu.internacao.dao.AinTiposMvtoInternacaoDAO;
import br.gov.mec.aghu.internacao.leitos.business.ILeitosInternacaoFacade;
import br.gov.mec.aghu.internacao.solicitacao.business.ISolicitacaoInternacaoFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghProfEspecialidades;
import br.gov.mec.aghu.model.AghProfEspecialidadesId;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinAtendimentosUrgencia;
import br.gov.mec.aghu.model.AinDocsInternacao;
import br.gov.mec.aghu.model.AinDocsInternacaoId;
import br.gov.mec.aghu.model.AinExtratoLeitos;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinMovimentosInternacao;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AinSolicitacoesInternacao;
import br.gov.mec.aghu.model.AinTiposAltaMedica;
import br.gov.mec.aghu.model.AinTiposMovimentoLeito;
import br.gov.mec.aghu.model.AipConveniosSaudePaciente;
import br.gov.mec.aghu.model.AipConveniosSaudePacienteId;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatConvTipoDocumentos;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatConvenioSaudePlanoId;
import br.gov.mec.aghu.model.McoEscalaLeitoRns;
import br.gov.mec.aghu.model.McoPlacar;
import br.gov.mec.aghu.model.PacIntdConv;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.cadastro.business.ICadastroPacienteFacade;
import br.gov.mec.aghu.paciente.vo.AghParametrosVO;
import br.gov.mec.aghu.perinatologia.business.IPerinatologiaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

/**
 * 
 * Classe de apoio para as Business Facades. Ela em geral agrupa as
 * funcionalidades encontradas em packages e procedures do AGHU.
 * 
 * Ela poderia ser uma classe com acesso friendly ou default e não ser um
 * componente seam.
 * 
 * Mas fazendo assim facilita, pois ela também pode receber uma referência para
 * o EntityManager.
 * 
 * Outra forma de fazer é instanciar ela diretamente do ON e passar o entity
 * manager para seus parâmetros. Neste caso os metodos desta classe poderiam ser
 * até estaticos e nao necessitar de instanciação. Ai ela seria apenas um
 * particionamento lógico de código e não um componente que possa ser injetado
 * em qualquer outro contexto.
 * 
 * ATENÇÃO, Os metodos desta classe nunca devem ser acessados diretamente por
 * qualquer classe que não a ON correspondente. Por isso sugere-se que todos os
 * métodos desta sejam friendly (default) ou private.
 * 
 * <b><br>
 * ORADB Package AINK_INT_ATU. <br>
 * Obs.: AINK_INT_ATU.EXCLUI_LOCALIZA_PACIENTES e
 * AINK_INT_ATU.INSERE_LOCALIZA_PACIENTES não foram migradas, pois não continham
 * nenhum código, existia apenas a sua assinatura na package oracle original.
 * </b>
 * 
 * @author Marcelo Tocchetto
 */
@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.ExcessiveClassLength","PMD.AtributoEmSeamContextManager"})
@Stateless
public class AtualizaInternacaoRN extends BaseBusiness {

	@EJB
	private InternacaoON internacaoON;
	
	@EJB
	private InternacaoRN internacaoRN;
	
	@EJB
	private InternacaoUtilRN internacaoUtilRN;
	
	@EJB
	private MovimentosInternacaoON movimentosInternacaoON;
	
	@EJB
	private CadastroInternacaoON cadastroInternacaoON;
	
	private static final Log LOG = LogFactory.getLog(AtualizaInternacaoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AinInternacaoDAO ainInternacaoDAO;
	
	@Inject
	private AinTiposMovimentoLeitoDAO ainTiposMovimentoLeitoDAO;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@Inject
	private AinAtendimentosUrgenciaDAO ainAtendimentosUrgenciaDAO;
	
	@Inject
	private AinLeitosDAO ainLeitosDAO;
	
	@EJB
	private IPerinatologiaFacade perinatologiaFacade;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@EJB
	private ISolicitacaoInternacaoFacade solicitacaoInternacaoFacade;
	
	@Inject
	private AinTiposAltaMedicaDAO ainTiposAltaMedicaDAO;
	
	@Inject
	private AinSolicitacoesInternacaoDAO ainSolicitacoesInternacaoDAO;
	
	@Inject
	private AinExtratoLeitosDAO ainExtratoLeitosDAO;
	
	@Inject
	private AinQuartosDAO ainQuartosDAO;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private ICadastroPacienteFacade cadastroPacienteFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private AinDocsInternacaoDAO ainDocsInternacaoDAO;
	
	@Inject
	private AinMovimentoInternacaoDAO ainMovimentoInternacaoDAO;
	
	@Inject
	private AinTiposMvtoInternacaoDAO ainTiposMvtoInternacaoDAO;
	
	@EJB
	private ILeitosInternacaoFacade leitosInternacaoFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -6617999094764107245L;
	private static final String P_COD_TIPO_ALTA_OBITO = "P_COD_TIPO_ALTA_OBITO";
	private static final String P_COD_TIPO_ALTA_OBITO_MAIS_48H = "P_COD_TIPO_ALTA_OBITO_MAIS_48H";

	private enum AtualizaInternacaoRNExceptionCode implements
			BusinessExceptionCode {
		AIN_00000, AIN_00338, AIN_00339, AIN_00341, AIN_00342, AIN_00343, AIN_00344, AIN_00346, AIN_00347, AIN_00348, AIN_00352, AIN_00353, AIN_00355, AIN_00356, AIN_00357, AIN_00400, AIN_00401, AIN_00406, AIN_00408, AIN_00422, AIN_00610, AIN_00659, AIN_00660, AIN_00663, AIN_00664, AIN_00670, AIN_00671, AIN_00672, AIN_00689, AIN_00690, AIN_00691, AIN_00692, AIN_00693, AIN_00694, AIN_00695, AIN_00696, AIN_00697, AIN_00698, AIN_00699, AIN_00700, AIN_00701, AIN_00702, AIN_00703, AIN_00706, AIN_00734, AIN_00739, AIN_00794, AIN_00795, AIN_00840, AIN_00842, AIN_00870, AIN_00896, AIN_00282, AIN_00351, PACIENTES_DE_AMBOS_OS_SEXOS_INTERNADOS_NO_MESMO_QUARTO, ERRO_CLONAR_CONTA_HOSPITALAR,
		LABEL_OPERACAO_INVALIDA_MODULO_INATIVO, MENSAGEM_ERRO_HIBERNATE_VALIDATION;

		public void throwException() throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this);
		}

		public void throwException(final Throwable cause, final Object... params)
				throws ApplicationBusinessException {
			// Tratamento adicional para não esconder a excecao de negocio
			// original
			if (cause instanceof ApplicationBusinessException) {
				throw (ApplicationBusinessException) cause;
			}
			throw new ApplicationBusinessException(this, cause, params);
		}

	}

	/** ##### INICIO DAS REFERENCIAS PARA VARIAVEIS GLOBAIS ##### */

	private String codigoTipoAlta; // v_cod_tipo_alta

	private String justificativaleitoBloqueioLimpeza; // v_justif_lto_bloq_limpeza

	private String justificativaExtratoLeito; // v_justif_extrato_leito

	private String idLeitoAntigo; // v_lto_id_antigo

	private Short codigoMovimentoLeitoAlta; // v_cod_mvto_lto_alta

	private Short codigoMovimentoLeitoDesocupado; // v_cod_mvto_lto_desocupado

	private Short codigoMovimentoLeitoInternacao; // v_cod_mvto_lto_internacao

	private Short codigoMovimentoLeitoLimpeza; // v_cod_mvto_lto_limpeza

	private Short codigoMovimentoLeitoPertences; // v_cod_mvto_lto_pertences

	private Short tipoMovimentoLeitoCodigoAlta; // v_tml_codigo_alta

	private String codigoTipoAltaObito; // v_cod_tp_alta_obito

	private Integer codigoMovimentoInternacao; // v_cod_mvto_int

	private Integer codigoMovimentoInternacaoAlta; // v_cod_mvto_int_alta

	private Byte codigoMovimentoInternacaoInternado; // v_cod_mvto_int_internacao

	private DominioSituacaoSolicitacaoInternacao situacaoSolicitacaoEfetuada; // v_ind_sit_solicitacao_efetuada

	private DominioSituacaoSolicitacaoInternacao situacaoSolicitacaoPendente; // v_ind_sit_solicitacao_pendente

	private DominioSituacaoSolicitacaoInternacao situacaoSolicitacaoAtendida; // v_ind_sit_solicitacao_atendida

	private DominioSituacaoSolicitacaoInternacao situacaoSolicitacaoCancelada; // v_ind_sit_solicit_cancelada

	private Short codigoMovimentoLeitoBloqueioAcompanhamento; // v_cod_mvto_lto_bloq_acomp

	private Short codigoMovimentoLeitoInfeccao; // v_cod_mvto_lto_infeccao

	private Short codigoMovimentoLeitoOcupado; // v_cod_mvto_lto_ocupado

	private Short codigoMovimentoLeitoPatologia; // v_cod_mvto_lto_patologia

	private Short codigoMovimentoLeitoTecnico; // v_cod_mvto_lto_tecnico

	private String codigoEventoAlta; // v_cod_evento_alta

	private String codigoEventoSaida; // v_cod_evento_saida

	/** ##### FIM DAS REFERENCIAS PARA VARIAVEIS GLOBAIS ##### */
	
	/**
	 * ORADB Procedure AINK_INT_ATU.ATUALIZA_DT_LCTO_EXL_LTO
	 * 
	 * Atualização da data lançamento do extrato do leito.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void atualizarDataLanctoExtratoLeito(final Integer newIntSeq,
			final Date newDthrInternacao, final Date oldDthrInternacao)
			throws ApplicationBusinessException {
		try {
			// ------------------------------------------------------------------------
			// -- Atualiza Dt Lcto Internacao
			// -- 1. Atualizar as datas de dthr_lancamento para os leitos
			// ocupados,
			// -- e (bloqueio técnico, acompanhante, patologia, infecção)
			// utlizados
			// -- pelo paciente com mesma data hora internação
			// -- [UPD17]
			// -- extrato leitos
			// -- [>UPD] altera data da internação -> new e old
			// -- [M] sim
			// ------------------------------------------------------------------------

			// Atualiza a data/hora do lançamento nos leitos			
			AinExtratoLeitosDAO ainExtratoLeitosDAO = this.getAinExtratoLeitosDAO();
			
			final List<AinExtratoLeitos> listaExtratoLeitos = ainExtratoLeitosDAO.pesquisarPorSeqDataInternacao(newIntSeq, oldDthrInternacao);
			for (final AinExtratoLeitos ainExtratoLeitos : listaExtratoLeitos) {
				ainExtratoLeitos.setDthrLancamento(newDthrInternacao);
				ainExtratoLeitosDAO.atualizar(ainExtratoLeitos);
			}
			ainExtratoLeitosDAO.flush();
		} catch (final Exception e) {
			logError(e.getMessage(), e);
			AtualizaInternacaoRNExceptionCode.AIN_00691.throwException(e);
		}
	}

	/**
	 * ORADB Procedure AINK_INT_ATU.RN_INTP_ATU_PLACAR
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void rnIntpAtuPlacar(final Integer codigoMae, final Short espSeq)
			throws ApplicationBusinessException {
		final AghParametrosVO aghParametrosVO = new AghParametrosVO();
		aghParametrosVO.setNome("P_CLINICA_OBSTETRICA");

		this.getParametroFacade().getAghpParametro(aghParametrosVO);

		if (aghParametrosVO.getMsg() != null) {
			AtualizaInternacaoRNExceptionCode.AIN_00840.throwException();
		}

		final BigDecimal valorClinicaObstetrica = aghParametrosVO.getVlrNumerico();
		BigDecimal codigoEspecialidade = null;

		if (espSeq != null) {
			final AghEspecialidades especialidade = this.getAghuFacade().obterAghEspecialidadesPorChavePrimaria(espSeq);
			if (especialidade != null) {
				codigoEspecialidade = new BigDecimal(especialidade.getClinica()
						.getCodigo().intValue());
			}
		}

		if (codigoMae != null && valorClinicaObstetrica != null
				&& valorClinicaObstetrica.equals(codigoEspecialidade)) {

			McoPlacar mcoPlacars = getPerinatologiaFacade().buscarPlacar(
					codigoMae);
			
			if(mcoPlacars != null){
				mcoPlacars.setIndSituacao(DominioSituacao.I);
				try {
					getPerinatologiaFacade().persistirPlacar(mcoPlacars);
				} catch (final InactiveModuleException e) {
					logWarn(e.getMessage());
				}
			}
		}
	}

	/**
	 * ORADB Procedure AINK_INT_ATU.VERIFICA_SEXO_PACIENTE
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificarSexoPaciente(final String ltoLtoId, final Short qrtNumero,
			final Integer pacCodigo) throws ApplicationBusinessException {
		// ------------------------------------------------------------------------
		// -- Verifica Atualiza Sexo Paciente
		// -- 1. No caso do quarto do paciente possuir indicador de consistencia
		// -- de sexo setado, verifica se o sexo determinante do quarto e
		// -- do paciente conferem, se não houver sexo determinante, verifica
		// -- se o sexo de ocupação do quarto e do paciente conferem.
		// -- 2. Atualiza o sexo de ocupacao do quarto
		// -- [IER41] [UPD20]
		// -- [>INS] [DEL] [>UPD] lto_lto_id, qrt_numero, unf_seq
		// -- , pac_codigo -> new
		// -- [T] leitos, quartos
		// -- [M] sim
		// ------------------------------------------------------------------------
		try {
			Short vQrtNumero = null;

			if (ltoLtoId != null || qrtNumero != null) {
				// Obtem sexo do paciente

				final DominioSexo sexoPaciente = getPacienteFacade().obterSexoPaciente(pacCodigo);

				// Obtem o quarto onde o paciente está internado
				if (ltoLtoId == null) {
					vQrtNumero = qrtNumero;
				} else {
					vQrtNumero = this.getAinLeitosDAO().obterNumeroQuartoPorLeito(ltoLtoId);
				}

				// Obtem o indicadores de sexo do quarto
				final AinQuartos quarto = this.getAinQuartosDAO().obterPorChavePrimaria(vQrtNumero);
				final DominioSimNao indConsSexo = quarto.getIndConsSexo();
				final DominioSexoDeterminante sexoDeterminante = quarto
						.getSexoDeterminante();

				if (DominioSimNao.S.equals(indConsSexo)) {
					if (!DominioSexoDeterminante.Q.equals(sexoDeterminante)
							&& !sexoDeterminante.toString().equals(
									sexoPaciente == null ? null : sexoPaciente
											.toString())) {
						AtualizaInternacaoRNExceptionCode.AIN_00400
								.throwException();
					} else {
						DominioSexo sexoQuarto = null;
						DominioSexo sexoLeito = null;
						try {
							// sexoQuarto ---------------------
							sexoQuarto = this.getAinInternacaoDAO().obterSexoQuarto(vQrtNumero);
							
							// sexoLeito ---------------------
							sexoLeito = this.getAinInternacaoDAO().obterSexoLeito(vQrtNumero);

							if (sexoQuarto != null && sexoLeito != null
									&& !sexoQuarto.equals(sexoLeito)) {
								AtualizaInternacaoRNExceptionCode.AIN_00401
										.throwException();
							}
						} catch (final NonUniqueResultException e) {
							logError(e.getMessage(), e);
							// Significa que existem pacientes de ambos os sexos
							// internados neste quarto.
							// No AGH era lançado AIN-00401 (sexo pac. diferente
							// do sexo ocupação) para este caso
							// Porém foi criado uma nova mensagem para esta
							// situação. Ver Defeito #4243.
							AtualizaInternacaoRNExceptionCode.PACIENTES_DE_AMBOS_OS_SEXOS_INTERNADOS_NO_MESMO_QUARTO
									.throwException(e);
						}

						quarto.setSexoOcupacao(sexoQuarto != null ? sexoQuarto
								: sexoLeito);
						this.getCadastrosBasicosInternacaoFacade().persistirQuarto(quarto, null);
						this.flush();
					}
				}
			}
		} catch (final Exception e) {
			logError(e.getMessage(), e);
			AtualizaInternacaoRNExceptionCode.AIN_00689.throwException(e);
		}
	}

	/**
	 * ORADB Procedure AINK_INT_ATU.ATUALIZA_SEXO_QUARTO
	 * 
	 * Atualiza Sexo Quarto
	 * 
	 * Coloca null no sexo de ocupação do quarto quando este se tornar liberado
	 * após a saída de um paciente.
	 */
	public void atualizarSexoQuarto(final String paramLeitoId, final Short paramNumeroQuarto)
			throws ApplicationBusinessException {
		
		Short numeroQuarto = null;

		if (paramLeitoId != null || paramNumeroQuarto != null) {
			// Obtem o quarto onde o paciente estava internado
			if (paramLeitoId == null) {
				numeroQuarto = paramNumeroQuarto;
			} else {
				numeroQuarto = this.getAinLeitosDAO().obterNumeroQuartoPorLeito(paramLeitoId);
			}

			// Verifica se quarto ficou livre
			final List<AinLeitos> leitosOcupados = getAinLeitosDAO().pesquisarLeitosOcupadosOuReservadosPorNumeroQuartoLeitoId(numeroQuarto, paramLeitoId);

			final List<AinInternacao> quartosOcupados = this.getAinInternacaoDAO().pesquisarPorNumeroQuarto(numeroQuarto);

			if (leitosOcupados.isEmpty() && quartosOcupados.isEmpty()) {
				final AinQuartos quarto = this.getAinQuartosDAO().obterPorChavePrimaria(numeroQuarto);
				quarto.setSexoOcupacao(null);
				this.getCadastrosBasicosInternacaoFacade().persistirQuarto(quarto, null);
				this.flush();
			}
		}
	}

	/**
	 * ORADB Procedure AINK_INT_ATU.ATUALIZA_EVENTOS
	 * 
	 * @throws ApplicationBusinessException
	 */
	public AtualizarEventosVO atualizarEventos(final Date dthrPrimeiroEvento,
			final Date dthrUltimoEvento) throws ApplicationBusinessException {
		// ----------------------------------------------------------------------
		// -- Atualiza Eventos
		// -- E. Ao modificar o último evento:
		// -- 1. O o primeiro evento recebe o último
		// -- se o o primeiro evento for nulo
		// -- ou o o primeiro for maior que o último
		// -- 2. O o último evento recebe o primeiro
		// -- se o o último evento for nulo
		// -- ou o último for menor que o primeiro
		// -- [INS] [UPD] dthr_ultimo_evento, dthr_primeiro_evento -> new
		// -- [T]
		// -- [M] não
		// ----------------------------------------------------------------------
		Date primeiroEvento = dthrPrimeiroEvento;
		Date ultimoEvento = dthrUltimoEvento;

		try {
			if (primeiroEvento == null
					|| (ultimoEvento != null && primeiroEvento
							.after(ultimoEvento))) {
				primeiroEvento = ultimoEvento;
			}

			if (ultimoEvento == null
					|| (primeiroEvento != null && ultimoEvento
							.before(primeiroEvento))) {
				ultimoEvento = primeiroEvento;
			}
		} catch (final Exception e) {
			logError(e.getMessage(), e);
			AtualizaInternacaoRNExceptionCode.AIN_00795.throwException(e);
		}

		return new AtualizarEventosVO(primeiroEvento, ultimoEvento);
	}

	/**
	 * ORADB Procedure AINK_INT_ATU.VERIFICA_SOLIC_INTERNACAO
	 * 
	 * Verifica Atualiza Solicitacao Internacao. A solicitação de internação
	 * será devidamente encerrada.
	 * 
	 * Obs.: O parâmatro p_lto_lto_id foi removido ao transcrever este método,
	 * era utilizado em um trecho de código que não é mais necessário (validado
	 * com CGTI->Milena, 02/06/2010).
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificarSolicitacaoInternacao(final Integer codigoPaciente, Boolean substituirProntuario)
			throws ApplicationBusinessException {
		try {			
			final AinSolicitacoesInternacao solicitacaoInternacao = this.getAinSolicitacoesInternacaoDAO().obterPrimeiraSolicitacoesAtendidasPorPaciente(codigoPaciente);
			if (solicitacaoInternacao != null) {
				// o paciente possui solicitação e esta será encerrada
				final AinSolicitacoesInternacao solicitacaoInternacaoOriginal = this.getAinSolicitacoesInternacaoDAO().obterPorChavePrimaria(solicitacaoInternacao.getSeq());
				this.getAinSolicitacoesInternacaoDAO().desatachar(solicitacaoInternacaoOriginal);

				solicitacaoInternacao
						.setIndSitSolicInternacao(DominioSituacaoSolicitacaoInternacao.E);

				this.getSolicitacaoInternacaoFacade().persistirSolicitacaoInternacao(
						solicitacaoInternacao, solicitacaoInternacaoOriginal, substituirProntuario);
			}
		} catch (final Exception e) {
			logError(e.getMessage(), e);
			AtualizaInternacaoRNExceptionCode.AIN_00671.throwException(e);
		}
	}

	/**
	 * ORADB Procedure AINK_INT_ATU.RN_INT_ATU_SOL_TRANS.
	 * 
	 * Chama procedure para atualização da solicitação de transferência.
	 */
	public void atualizarSolicitacaoTransferencia(final Integer seq,
			final Date dthrAltaMedica) throws ApplicationBusinessException {
		// ------------------------------------------------------------------------
		// -- RN_INT_ATU_SOL_TRANS
		// -- Chama a procedure AINP_INT_ATU_SOL_TRANS para
		// -- atualizar a situação da solicitação de transferência quando da
		// -- alta do paciente. Torna cancelada a solicitação pendente ou
		// atendida
		// -- solicitaçãoes de transferência
		// -- [>UPD]PROCESSA_ALTA -> :new.seq
		// -- [M] não
		// ------------------------------------------------------------------------
		this.getInternacaoRN().atualizarSituacaoSolicitacaoTransferencia(seq,
				dthrAltaMedica);
	}

	/**
	 * ORADB Procedure AINK_INT_ATU.RN_INT_ATU_SOL_ESTOR
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void atualizarSolicitacaoTransferenciaPacienteEstornoAlta(
			final Integer seq, final Date dthrAltaMedica) throws ApplicationBusinessException {
		// ------------------------------------------------------------------------
		// -- RN_INT_ATU_SOL_ESTOR
		// -- Chama a procedure AINP_INT_ATU_SOL_EST para
		// -- atualizar a situação da solicitação de transferência quando do
		// -- estorno da alta do paciente. Torna pendente ou atendida
		// -- a solicitação cancelada.
		// -- solicitaçãoes de transferência
		// -- [>UPD]ESTORNA_ALTA -> :new.seq, :old.dthr_alta_medica
		// -- [M] não
		// --------------------------------------------------------------------------------
		this.getInternacaoRN().ainpIntAtuSolEstorno(seq, dthrAltaMedica);
	}

	/**
	 * ORADB Procedure AINK_INT_ATU.ATUALIZA_SERVIDOR
	 * 
	 * Atualiza Servidor
	 * 
	 * Se o usuario é um servidor, guarda o servidor. Caso contrário, mantém o
	 * servidor anterior, se não existir servidor anterior, dispara erro.
	 * 
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	public void atualizarServidor(final RapServidoresId idServidor)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final Integer serMatricula = servidorLogado.getId().getMatricula();
		final Short serVinCodigo = servidorLogado.getId().getVinCodigo();

		if (idServidor.getMatricula() == null
				|| idServidor.getVinCodigo() == null) {
			if (serMatricula != null && serVinCodigo != null) {
				idServidor.setMatricula(serMatricula);
				idServidor.setVinCodigo(serVinCodigo);
			} else {
				AtualizaInternacaoRNExceptionCode.AIN_00400.throwException();
			}
		}
	}

	/**
	 * ORADB Procedure/Function AINK_INT_ATU.ATUALIZA_ATENDIMENTOS
	 */
	public void atualizaAtendimentos(final Integer seqAtendimentoUrgencia,
			final Integer codigoPaciente, final Date dataHoraInternacao,
			final AinInternacao internacao) throws ApplicationBusinessException {
		// ------------------------------------------------------------------------
		// -- Atualiza Atendimentos
		// -- 1. Atualiza atendimentos de urgencia
		// -- [CRE31]
		// -- atendimentos_urgencia
		// -- [>DEL] passar a dthr_internacao = NULL -> old
		// -- [>INS] [>UPD] pac_codigo, dthr_internacao -> new
		// -- [M] sim
		// ------------------------------------------------------------------------
		if (seqAtendimentoUrgencia != null) {
			final AghParametros aghParametros = this.getParametroFacade()
					.buscarAghParametro(AghuParametrosEnum.P_COD_TIPO_ALTA_NORMAL);
			if (aghParametros != null && aghParametros.getVlrTexto() != null) {
				this.codigoTipoAlta = aghParametros.getVlrTexto();
			} else {
				throw new ApplicationBusinessException(
						AtualizaInternacaoRNExceptionCode.AIN_00406);
			}

			final List<AinAtendimentosUrgencia> atendimentosUrgencia = this.getAinAtendimentosUrgenciaDAO().pesquisarAtendimentoUrgencia(
					seqAtendimentoUrgencia, codigoPaciente);
			if (atendimentosUrgencia != null && !atendimentosUrgencia.isEmpty()) {
				for (final AinAtendimentosUrgencia atendimentoUrgencia : atendimentosUrgencia) {
					atendimentoUrgencia
							.setIndPacienteEmAtendimento(dataHoraInternacao == null);
					atendimentoUrgencia
							.setTipoAltaMedica(dataHoraInternacao == null ? null
									: this.getAinTiposAltaMedicaDAO().obterPorChavePrimaria(this.codigoTipoAlta));
					atendimentoUrgencia
							.setDtAltaAtendimento(dataHoraInternacao);

					this.getInternacaoON()
							.persistirAtendimentoUrgencia(atendimentoUrgencia);

					/*-------------------------------------------------------------------------*/
					// TODO : Retirar a chamada deste método após a
					// implementação das triggers de AIN_ATENDIMENTOS_URGENCIA.
					// Este código é chamado da enforce de
					// ain_atendimentos_urgencia ainda não implementada.
					atualizarLeitoAtendimentoUrgencia(atendimentoUrgencia,
							internacao);
					/*----------------------------------------------------------------------- */
				}
			}
		}
	}

	/**
	 * Método que providencia a atualização do leito do atendimento de urgência
	 * 
	 * @param atendimentoUrgencia
	 * @param internacao
	 * @throws ApplicationBusinessException
	 */
	private void atualizarLeitoAtendimentoUrgencia(
			final AinAtendimentosUrgencia atendimentoUrgencia,
			final AinInternacao internacao) throws ApplicationBusinessException {

		final AghParametros parametroLeitoDesocupado = this.getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_COD_MVTO_LTO_DESOCUPADO);
		final Short codigoMovimentoLeito = parametroLeitoDesocupado.getVlrNumerico()
				.shortValue();
		final AinTiposMovimentoLeito tipoMovimentoLeito = obterTipoMovimentoLeito(codigoMovimentoLeito);
		getLeitosInternacaoFacade().atualizarLeito(atendimentoUrgencia.getLeito(),
				tipoMovimentoLeito, internacao);
	}

	/**
	 * Obtém o tipo de movimento do leito
	 * 
	 * @param codigo
	 * @return
	 */
	private AinTiposMovimentoLeito obterTipoMovimentoLeito(final Short codigo) {
		return this.getAinTiposMovimentoLeitoDAO().obterPorChavePrimaria(codigo);
	}

	/**
	 * ORADB Procedure/Function AINK_INT_ATU.ATUALIZA_DT_LCTO_MVTO_ALTA
	 */
	public void atualizaDataLancamentoMovimentoAlta(final Integer seqInternacao,
			final Date dataHoraAltaMedica) throws ApplicationBusinessException {
		// ------------------------------------------------------------------------
		// -- Atualiza Dt Lcto Mvto Alta
		// -- 1. Atualiza na tabela de movimentos de internação a data de alta
		// -- [UPD16]
		// -- movimentos internacao
		// -- [>UPD] dthr_alta_medica -> new
		// -- [M] sim
		// ------------------------------------------------------------------------
		try {
			if (dataHoraAltaMedica != null) {
				final AghParametros aghParametros = this.getParametroFacade()
						.buscarAghParametro(AghuParametrosEnum.P_COD_MVTO_INT_ALTA);
				if (aghParametros != null
						&& aghParametros.getVlrNumerico() != null) {
					this.codigoMovimentoInternacaoAlta = aghParametros
							.getVlrNumerico().intValue();
				} else {
					throw new ApplicationBusinessException(
							AtualizaInternacaoRNExceptionCode.AIN_00348);
				}
				
				AinMovimentoInternacaoDAO ainMovimentoInternacaoDAO = this.getAinMovimentoInternacaoDAO();
				
				final List<AinMovimentosInternacao> movimentosInternacao = ainMovimentoInternacaoDAO
						.pesquisarAinMovimentosInternacaoPorInternacaoTipoMvto(seqInternacao, this.codigoMovimentoInternacaoAlta);

				if (movimentosInternacao != null
						&& !movimentosInternacao.isEmpty()) {
					for (final AinMovimentosInternacao movimentoInternacao : movimentosInternacao) {
						movimentoInternacao
								.setDthrLancamento(dataHoraAltaMedica);
						ainMovimentoInternacaoDAO.atualizar(movimentoInternacao);
					}

					ainMovimentoInternacaoDAO.flush();
				}
			}
		} catch (final Exception e) {
			logError(e.getMessage(), e);
			throw new ApplicationBusinessException(
					AtualizaInternacaoRNExceptionCode.AIN_00692);
		}
	}

	/**
	 * ORADB Procedure/Function AINK_INT_ATU.ATUALIZA_DT_LCTO_MVTO_INT
	 */
	public void atualizaDataInternacaoMovimentosInternacao(
			final Integer seqInternacao, final Date dataHoraInternacao)
			throws ApplicationBusinessException {
		// ------------------------------------------------------------------------
		// -- Atualiza Dt Lcto Mvto Int
		// -- 1. Atualiza na tabela de movimentos de internação a data
		// -- de internacao
		// -- [IER52]
		// -- movimentos internacao
		// -- [>UPD] dthr_internacao -> new
		// -- [M] sim
		// ------------------------------------------------------------------------
		try {
			if (dataHoraInternacao != null) {
				final AghParametros aghParametros = this.getParametroFacade()
						.buscarAghParametro(AghuParametrosEnum.P_COD_MVTO_INT_INTERNACAO);
				if (aghParametros != null
						&& aghParametros.getVlrNumerico() != null) {
					this.codigoMovimentoInternacao = aghParametros
							.getVlrNumerico().intValue();
				} else {
					throw new ApplicationBusinessException(
							AtualizaInternacaoRNExceptionCode.AIN_00348);
				}
				
				AinMovimentoInternacaoDAO ainMovimentoInternacaoDAO = this.getAinMovimentoInternacaoDAO();
				
				final List<AinMovimentosInternacao> movimentosInternacao = ainMovimentoInternacaoDAO
						.pesquisarAinMovimentosInternacaoPorInternacaoTipoMvto(seqInternacao, this.codigoMovimentoInternacao);

				if (movimentosInternacao != null
						&& !movimentosInternacao.isEmpty()) {
					for (final AinMovimentosInternacao movimentoInternacao : movimentosInternacao) {
						movimentoInternacao
								.setDthrLancamento(dataHoraInternacao);
						ainMovimentoInternacaoDAO.atualizar(movimentoInternacao);						
					}
					ainMovimentoInternacaoDAO.flush();
				}
			}
		} catch (final ConstraintViolationException ise) {
			String mensagem = "";
			Set<ConstraintViolation<?>> arr = ise.getConstraintViolations();
			for (ConstraintViolation item : arr) {
				if (!"".equals(item)) {
					mensagem = item.getMessage();
					if (mensagem.isEmpty()) {
						mensagem = " Valor inválido para o campo "
								+ item.getPropertyPath();
					}
				}
			}
			throw new ApplicationBusinessException(
					AtualizaInternacaoRNExceptionCode.MENSAGEM_ERRO_HIBERNATE_VALIDATION,
					mensagem);

		} catch (final Exception e) {
			logError(e.getMessage(), e);
			throw new ApplicationBusinessException(
					AtualizaInternacaoRNExceptionCode.AIN_00693);
		}
	}

	/**
	 * ORADB Procedure AINK_INT_ATU.ATUALIZA_IND_INTERNACAO
	 * 
	 * Atualiza indicadores de internacao.
	 * 
	 * Obs.: Transcrição alterada. 1 - Parâmetro P_NEW_IND_PACIENTE_INTERNADO
	 * removido da assinatura do método, pois não havia leitura do valor, apenas
	 * atribuição na implementação do método. Esta mapeado no retorno como
	 * AtualizarIndicadoresInternacaoVO.indPacienteInternado. 2 - Parâmetro
	 * P_NEW_IND_SAIDA_PAC mapeado no retorno como
	 * AtualizarIndicadoresInternacaoVO.indSaidaPaciente.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public AtualizarIndicadoresInternacaoVO atualizarIndicadoresInternacao(
			final Date dthrAltaMedica, final String tamCodigo, final Boolean newIndSaidaPac)
			throws ApplicationBusinessException {
		Boolean indPacienteInternado = null;
		Boolean indSaidaPaciente = newIndSaidaPac;
		try {
			if (dthrAltaMedica != null && tamCodigo != null) {
				indPacienteInternado = Boolean.FALSE;
			} else {
				indPacienteInternado = Boolean.TRUE;
				indSaidaPaciente = Boolean.FALSE;
			}
		} catch (final Exception e) {
			logError(e.getMessage(), e);
			AtualizaInternacaoRNExceptionCode.AIN_00694.throwException(e);
		}
		return new AtualizarIndicadoresInternacaoVO(indPacienteInternado,
				indSaidaPaciente);
	}

	/**
	 * ORADB Procedure AINK_INT_ATU.ATUALIZA_IND_LOCALIZACAO
	 * 
	 * Atualiza indicador de localização.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public DominioLocalPaciente atualizarIndicadorLocalizacao(final String leitoID,
			final Short qrtNumero, final Short unfSeq) throws ApplicationBusinessException {
		DominioLocalPaciente indLocalPaciente = null;
		try {
			if (leitoID != null) {
				indLocalPaciente = DominioLocalPaciente.L;
			} else if (qrtNumero != null) {
				indLocalPaciente = DominioLocalPaciente.Q;
			} else if (unfSeq != null) {
				indLocalPaciente = DominioLocalPaciente.U;
			}
		} catch (final Exception e) {
			logError(e.getMessage(), e);
			AtualizaInternacaoRNExceptionCode.AIN_00695.throwException(e);
		}
		return indLocalPaciente;
	}

	/**
	 * ORADB Procedure AINK_INT_ATU.ATUALIZA_IND_SAIDA_PAC
	 * 
	 * Atualiza indicador de saida do paciente.
	 */
	public Boolean atualizarIndicadorSaidaPaciente(final Date dtSaidaPaciente) {
		return dtSaidaPaciente == null ? false : true;
	}

	/**
	 * ORADB Procedure AINK_INT_ATU.ATUALIZA_PROF_ESPECIALIDADES
	 * 
	 * 1. Atualiza somatório dos pacientes internados [IER47]
	 * 
	 */
	public void atualizaProfissionaisEspecialidades(
			final AghEspecialidades especialidade, final RapServidores professor,
			final Integer nroPacientes) {
		final AghProfEspecialidadesId id = new AghProfEspecialidadesId(professor
				.getId().getMatricula(), professor.getId().getVinCodigo(),
				especialidade.getSeq());
		final AghProfEspecialidades profEsp = this.getAghuFacade().obterAghProfEspecialidadesPorChavePrimaria(id);
		if (profEsp != null){
			profEsp.setQuantPacInternados(profEsp.getQuantPacInternados()
					+ nroPacientes);
		}
	}
	
	/**
	 * 
	 * 
	 * 1. deleta do somatório dos pacientes internados ao fazer estorno 
	 * 
	 */
	public void deletaProfissionaisEspecialidades(
			final AghEspecialidades especialidade, final RapServidores professor,
			final Integer nroPacientes) {
		final AghProfEspecialidadesId id = new AghProfEspecialidadesId(professor
				.getId().getMatricula(), professor.getId().getVinCodigo(),
				especialidade.getSeq());
		final AghProfEspecialidades profEsp = this.getAghuFacade().obterAghProfEspecialidadesPorChavePrimaria(id);
		if (profEsp != null){
			if(profEsp.getQuantPacInternados() > 0){
				profEsp.setQuantPacInternados(profEsp.getQuantPacInternados()
						+ nroPacientes);
			}
		}
	}

	/**
	 * ORADB Procedure AINK_INT_ATU.ATUALIZA_SEQ_INTERNACAO
	 * 
	 * Atualiza sequencia de internacao.
	 * 
	 * Obs.: Transcrição alterada, ao invés de receber o parâmetro original
	 * p_new_seq deve ser passada uma instância da classe AinInternacao.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void atualizarSeqInternacao(final AinInternacao internacao)
			throws ApplicationBusinessException {
		try {
			if (internacao != null && internacao.getSeq() == null) {
				this.getCadastroInternacaoON().obterValorSequencialId(internacao);
			}
		} catch (final Exception e) {
			logError(e.getMessage(), e);
			AtualizaInternacaoRNExceptionCode.AIN_00697.throwException(e);
		}
	}

	/**
	 * ORADB Procedure/Function AINK_INT_ATU.ATU_SOLICITACOES_PENDENTES
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public void atualizaSolicitacoesPendentes(final Integer codigoPaciente, Boolean substituirProntuario)
			throws ApplicationBusinessException {
		// ------------------------------------------------------------------------
		// -- Atualiza Solicitacoes Pendentes
		// -- 1. Atualiza a solicitação de internação como efetuado
		// -- [IER55.1]
		// -- [>INS]
		// -- solicitacoes_internacao
		// -- [M] não
		// ------------------------------------------------------------------------
		try {
			AghParametros aghParametros = this.getParametroFacade()
					.buscarAghParametro(AghuParametrosEnum.P_IND_SIT_SOLIC_EFETUADA);
			if (aghParametros != null && aghParametros.getVlrTexto() != null) {
				this.situacaoSolicitacaoEfetuada = DominioSituacaoSolicitacaoInternacao
						.valueOf(aghParametros.getVlrTexto());
			} else {
				throw new ApplicationBusinessException(
						AtualizaInternacaoRNExceptionCode.AIN_00659);
			}

			aghParametros = this.getParametroFacade()
					.buscarAghParametro(AghuParametrosEnum.P_IND_SIT_SOLIC_ATENDIDA);
			if (aghParametros != null && aghParametros.getVlrTexto() != null) {
				this.situacaoSolicitacaoAtendida = DominioSituacaoSolicitacaoInternacao
						.valueOf(aghParametros.getVlrTexto());
			} else {
				throw new ApplicationBusinessException(
						AtualizaInternacaoRNExceptionCode.AIN_00659);
			}

			aghParametros = this.getParametroFacade()
					.buscarAghParametro(AghuParametrosEnum.P_IND_SIT_SOLIC_PENDENTE);
			if (aghParametros != null && aghParametros.getVlrTexto() != null) {
				this.situacaoSolicitacaoPendente = DominioSituacaoSolicitacaoInternacao
						.valueOf(aghParametros.getVlrTexto());
			} else {
				throw new ApplicationBusinessException(
						AtualizaInternacaoRNExceptionCode.AIN_00660);
			}

			aghParametros = this.getParametroFacade()
					.buscarAghParametro(AghuParametrosEnum.P_IND_SIT_SOLIC_CANCELADA);
			if (aghParametros != null && aghParametros.getVlrTexto() != null) {
				this.situacaoSolicitacaoCancelada = DominioSituacaoSolicitacaoInternacao
						.valueOf(aghParametros.getVlrTexto());
			} else {
				throw new ApplicationBusinessException(
						AtualizaInternacaoRNExceptionCode.AIN_00739);
			}


			List<AinSolicitacoesInternacao> solicitacoesInternacao = this.getAinSolicitacoesInternacaoDAO()
					.obterSolicitacoesAtendidasPorPacienteSituacao(codigoPaciente, this.situacaoSolicitacaoAtendida);
			if (solicitacoesInternacao != null
					&& !solicitacoesInternacao.isEmpty()) {
				for (final AinSolicitacoesInternacao solicitacaoInternacao : solicitacoesInternacao) {
					final AinSolicitacoesInternacao solicitacaoInternacaoOriginal = this.getAinSolicitacoesInternacaoDAO()
							.obterPorChavePrimaria(solicitacaoInternacao.getSeq());
					this.getAinSolicitacoesInternacaoDAO().desatachar(solicitacaoInternacaoOriginal);

					solicitacaoInternacao
							.setIndSitSolicInternacao(this.situacaoSolicitacaoEfetuada);

					this.getSolicitacaoInternacaoFacade()
							.persistirSolicitacaoInternacao(
									solicitacaoInternacao,
									solicitacaoInternacaoOriginal, substituirProntuario);
				}
			}
			
			solicitacoesInternacao = this.getAinSolicitacoesInternacaoDAO().obterSolicitacoesAtendidasPorPacienteSituacao(
					codigoPaciente, this.situacaoSolicitacaoPendente);
			if (solicitacoesInternacao != null
					&& !solicitacoesInternacao.isEmpty()) {
				for (final AinSolicitacoesInternacao solicitacaoInternacao : solicitacoesInternacao) {
					final AinSolicitacoesInternacao solicitacaoInternacaoOriginal = this.getAinSolicitacoesInternacaoDAO().obterPorChavePrimaria(solicitacaoInternacao.getSeq());
					this.getAinSolicitacoesInternacaoDAO().desatachar(solicitacaoInternacaoOriginal);

					solicitacaoInternacao
							.setIndSitSolicInternacao(this.situacaoSolicitacaoCancelada);

					this.getSolicitacaoInternacaoFacade()
							.persistirSolicitacaoInternacao(
									solicitacaoInternacao,
									solicitacaoInternacaoOriginal, substituirProntuario);
				}
			}

		} catch (final Exception e) {
			logError(e.getMessage(), e);
			throw new ApplicationBusinessException(
					AtualizaInternacaoRNExceptionCode.AIN_00356);
		}
	}

	/**
	 * ORADB Procedure/Function AINK_INT_ATU.ATU_SOLICITACOES_EFETUADAS
	 * 
	 * Atualiza Solicitacoes Efetuadas.
	 */
	public void atualizaSolicitacoesEfetuadas(final Integer codigoPaciente, Boolean substituirProntuario)
			throws ApplicationBusinessException {
		// ------------------------------------------------------------------------
		// -- Atualiza Solicitacoes Efetuadas
		// -- 1. Atualiza a solicitação de internação como pendente
		// -- [IER55.2]
		// -- [DEL]
		// -- solicitacoes_internacao
		// -- [M] não
		// ------------------------------------------------------------------------
		try {
			final AghParametros aghParametros = this.getParametroFacade()
					.buscarAghParametro(AghuParametrosEnum.P_IND_SIT_SOLIC_PENDENTE);
			if (aghParametros != null && aghParametros.getVlrTexto() != null) {
				this.situacaoSolicitacaoPendente = DominioSituacaoSolicitacaoInternacao
						.valueOf(aghParametros.getVlrTexto());
			} else {
				throw new ApplicationBusinessException(
						AtualizaInternacaoRNExceptionCode.AIN_00660);
			}

			final List<AinSolicitacoesInternacao> solicitacoesInternacao = this.getAinSolicitacoesInternacaoDAO()
					.listarSolicitacoesInternacaoPorCodigoPaciente(codigoPaciente);

			if (solicitacoesInternacao != null
					&& !solicitacoesInternacao.isEmpty()) {
				for (final AinSolicitacoesInternacao solicitacaoInternacao : solicitacoesInternacao) {
					final AinSolicitacoesInternacao solicitacaoInternacaoOriginal = this.getAinSolicitacoesInternacaoDAO().obterPorChavePrimaria(solicitacaoInternacao.getSeq());
					this.getAinSolicitacoesInternacaoDAO().desatachar(solicitacaoInternacaoOriginal);

					solicitacaoInternacao
							.setIndSitSolicInternacao(this.situacaoSolicitacaoPendente);
					solicitacaoInternacao.setLeito(null);
					solicitacaoInternacao.setQuarto(null);
					solicitacaoInternacao.setUnidadeFuncional(null);

					this.getSolicitacaoInternacaoFacade()
							.persistirSolicitacaoInternacao(
									solicitacaoInternacao,
									solicitacaoInternacaoOriginal, substituirProntuario);
				}
			}

		} catch (final Exception e) {
			logError(e.getMessage(), e);
			throw new ApplicationBusinessException(
					AtualizaInternacaoRNExceptionCode.AIN_00794);
		}
	}


	private String getCodTipoAltaObito() throws ApplicationBusinessException {
		return this.getInternacaoUtilRN().pegaParametro(P_COD_TIPO_ALTA_OBITO,
				(String) null,
				InternacaoUtilRN.InternacaoUtilRNExceptionCode.AIN_00347);
	}

	private String getCodTipoAltaObitoMais48h() throws ApplicationBusinessException {
		return this.getInternacaoUtilRN().pegaParametro(
				P_COD_TIPO_ALTA_OBITO_MAIS_48H, (String) null,
				InternacaoUtilRN.InternacaoUtilRNExceptionCode.AIN_00347);
	}

	/**
	 * ORADB Procedure AINK_INT_ATU.ATUALIZA_ALTA_OBITO Evento PA = Processo
	 * Alta
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void atualizaAltaObitoProcessaAlta(
			final AinTiposAltaMedica tipoAltaMedica, final AipPacientes paciente,
			final Date dataAltaMedica, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws ApplicationBusinessException {
		final String codTipoAltaMedica = tipoAltaMedica == null ? null
				: tipoAltaMedica.getCodigo();
		if (Objects.equals(codTipoAltaMedica, getCodTipoAltaObito())
				|| Objects.equals(codTipoAltaMedica,
						getCodTipoAltaObitoMais48h())) {
			paciente.setDtObito(dataAltaMedica);
			this.getCadastroPacienteFacade().atualizarPacienteParcial(paciente, nomeMicrocomputador, dataFimVinculoServidor);
		}
	}

	/**
	 * ORADB Procedure AINK_INT_ATU.ATUALIZA_ALTA_OBITO Evento EA = Estorno Alga
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void atualizaAltaObitoEstornoAlta(final AinTiposAltaMedica tipoAltaMedica,
			final AipPacientes paciente, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws ApplicationBusinessException {
		final String codTipoAltaMedica = tipoAltaMedica == null ? null
				: tipoAltaMedica.getCodigo();
		if (Objects.equals(codTipoAltaMedica, getCodTipoAltaObito())
				|| Objects.equals(codTipoAltaMedica,
						getCodTipoAltaObitoMais48h())) {
			paciente.setDtObito(null);
			this.getCadastroPacienteFacade().atualizarPacienteParcial(paciente, nomeMicrocomputador, dataFimVinculoServidor);
		}
	}

	/**
	 * ORADB Procedure AINK_INT_ATU.ATUALIZA_ALTA_OBITO Evento AA = Alteração
	 * Alta
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void atualizaAltaObitoAlteracaoAlta(
			final AinTiposAltaMedica tipoAltaMedica, final AipPacientes paciente,
			final Date dataAltaMedica, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws ApplicationBusinessException {
		final String codTipoAltaMedica = tipoAltaMedica == null ? null
				: tipoAltaMedica.getCodigo();
		if (Objects.equals(codTipoAltaMedica, getCodTipoAltaObito())
				|| Objects.equals(codTipoAltaMedica,
						getCodTipoAltaObitoMais48h())) {
			paciente.setDtObito(dataAltaMedica);
		} else {
			paciente.setDtObito(null);
		}

		this.getCadastroPacienteFacade().atualizarPacienteParcial(paciente, nomeMicrocomputador, dataFimVinculoServidor);
	}

	/**
	 * ORADB Procedure/Function AINK_INT_ATU.EXCLUI_LCTO_ALTA
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public void excluiLctoAlta(final AinInternacao internacao, final AinLeitos leito)
			throws ApplicationBusinessException {
		// ------------------------------------------------------------------------
		// -- Exclui Lcto Alta
		// -- Ao desfazer a alta da internacao
		// -- 1. Excluir todos extratos gerados pela alta
		// -- desde que não hajam extratos posteriores
		// -- 2. Atualiza todos os leitos retornando sua situação
		// -- para a situação anterior à alta
		// -- desde que não hajam extratos posteriores
		// -- [DEL] [>UPD] estorna_alta -> new
		// -- extratos leitos, leitos
		// -- [M] sim
		// ------------------------------------------------------------------------

		AghParametros aghParametros = this.getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_COD_MVTO_LTO_ALTA);
		if (aghParametros != null && aghParametros.getVlrNumerico() != null) {
			this.codigoMovimentoLeitoAlta = aghParametros.getVlrNumerico()
					.shortValue();
		} else {
			throw new ApplicationBusinessException(
					AtualizaInternacaoRNExceptionCode.AIN_00344);
		}

		aghParametros = this.getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_COD_MVTO_LTO_DESOCUPADO);
		if (aghParametros != null && aghParametros.getVlrNumerico() != null) {
			this.codigoMovimentoLeitoDesocupado = aghParametros
					.getVlrNumerico().shortValue();
		} else {
			throw new ApplicationBusinessException(
					AtualizaInternacaoRNExceptionCode.AIN_00422);
		}

		aghParametros = this.getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_COD_MVTO_LTO_BLOQUEIO_LIMPEZA);
		if (aghParametros != null && aghParametros.getVlrNumerico() != null) {
			this.codigoMovimentoLeitoLimpeza = aghParametros.getVlrNumerico()
					.shortValue();
		} else {
			throw new ApplicationBusinessException(
					AtualizaInternacaoRNExceptionCode.AIN_00343);
		}

		aghParametros = this.getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_COD_MVTO_LTO_PERTENCES_PAC);
		if (aghParametros != null && aghParametros.getVlrNumerico() != null) {
			this.codigoMovimentoLeitoPertences = aghParametros.getVlrNumerico()
					.shortValue();
		} else {
			throw new ApplicationBusinessException(
					AtualizaInternacaoRNExceptionCode.AIN_00870);
		}

		aghParametros = this.getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_COD_MVTO_LTO_OCUPADO);
		if (aghParametros != null && aghParametros.getVlrNumerico() != null) {
			this.codigoMovimentoLeitoOcupado = aghParametros.getVlrNumerico()
					.shortValue();
		} else {
			throw new ApplicationBusinessException(
					AtualizaInternacaoRNExceptionCode.AIN_00346);
		}

		aghParametros = this.getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_COD_MVTO_LTO_DESOCUPADO);
		if (aghParametros != null && aghParametros.getVlrNumerico() != null) {
			this.codigoMovimentoLeitoDesocupado = aghParametros
					.getVlrNumerico().shortValue();
		} else {
			throw new ApplicationBusinessException(
					AtualizaInternacaoRNExceptionCode.AIN_00342);
		}

		final List<Short> listaMovimentosLeito = new ArrayList<Short>();
		listaMovimentosLeito.add(this.codigoMovimentoLeitoAlta);
		listaMovimentosLeito.add(this.codigoMovimentoLeitoDesocupado);
		listaMovimentosLeito.add(this.codigoMovimentoLeitoLimpeza);
		listaMovimentosLeito.add(this.codigoMovimentoLeitoPertences);
		
		AinExtratoLeitosDAO ainExtratoLeitosDAO = this.getAinExtratoLeitosDAO();
		
		final List<AinExtratoLeitos> extratosLeitos = ainExtratoLeitosDAO.pesquisarPorLeitoInternacaoMovimentos(leito, internacao,
				listaMovimentosLeito, codigoMovimentoLeitoDesocupado);
		
		if (extratosLeitos != null && !extratosLeitos.isEmpty()) {
			for (final AinExtratoLeitos extratoLeito : extratosLeitos) {
				ainExtratoLeitosDAO.remover(extratoLeito);
			}
			ainExtratoLeitosDAO.flush();
		}

		/*
		 * UPDATE ain_leitos lto SET lto.tml_codigo = 16, --NVL(
		 * v_tml_codigo,0), lto.int_seq = p_new_int_seq WHERE lto.lto_id =
		 * p_new_lto_lto_id;
		 */
		if (leito != null) {
			final AinTiposMovimentoLeito tipoMovimentoLeito = this.getAinTiposMovimentoLeitoDAO().obterPorChavePrimaria(
					this.codigoMovimentoLeitoOcupado);

			leito.setInternacao(internacao);
			leito.setTipoMovimentoLeito(tipoMovimentoLeito);

			getCadastrosBasicosInternacaoFacade().validarAtualizacaoLeito(leito);
			
			AinLeitosDAO ainLeitosDAO = this.getAinLeitosDAO();
			ainLeitosDAO.atualizar(leito);
			ainLeitosDAO.flush();
		}

	}

	/**
	 * ORADB Procedure/Function AINK_INT_ATU.EXCLUI_REGISTRO_ALTA
	 */
	public void excluiRegistroAlta(final AinInternacao internacao,
			final Date dataSaidaPaciente) throws ApplicationBusinessException {
		// ------------------------------------------------------------------------
		// -- Exclui Registro Alta
		// -- 1. Restaura tipo movimento leito
		// -- [UPD19] [DEL03]
		// -- [>UPD] 'estorna_alta' -> new
		// -- movimentos internacao
		// -- [M] sim
		// ------------------------------------------------------------------------

		final AghParametros aghParametros = this.getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_COD_MVTO_INT_ALTA);
		if (aghParametros != null && aghParametros.getVlrNumerico() != null) {
			this.codigoMovimentoInternacaoAlta = aghParametros.getVlrNumerico()
					.intValue();
		} else {
			throw new ApplicationBusinessException(
					AtualizaInternacaoRNExceptionCode.AIN_00348);
		}

		if (dataSaidaPaciente == null) {
			AinMovimentoInternacaoDAO ainMovimentoInternacaoDAO = this.getAinMovimentoInternacaoDAO();
			
			final List<AinMovimentosInternacao> movimentosInternacao = ainMovimentoInternacaoDAO
					.pesquisarAinMovimentosInternacaoPorInternacaoTipoMvto(internacao == null ? null : internacao.getSeq(),
							this.codigoMovimentoInternacaoAlta);

			if (movimentosInternacao != null && !movimentosInternacao.isEmpty()) {
				for (final AinMovimentosInternacao movimentoInternacao : movimentosInternacao) {
					ainMovimentoInternacaoDAO.remover(movimentoInternacao);
				}
				ainMovimentoInternacaoDAO.flush();
			}
		}
	}

	/**
	 * ORADB Procedure/Function AINK_INT_ATU.INSERE_CONVENIOS
	 */
	public void insereConvenios(final Integer codigoPaciente,
			final Short codigoConvenioSaudePlano, final Byte seqConvenioSaudePlano)
			throws ApplicationBusinessException {
		// ------------------------------------------------------------------------
		// -- Insere Convenios
		// -- 1. Registra inserção na "convênios saúde"
		// -- [CRE30]
		// -- [>INS] [>UPD] pac_codigo, csp_cnv_codigo, csp_seq -> new
		// -- convenios_saude_paciente
		// -- [M] não
		// ------------------------------------------------------------------------
		try {

			final List<AipConveniosSaudePaciente> lista = this.getPacienteFacade()
					.pesquisarAtivosPorPacienteCodigoSeqConvenio(codigoPaciente, codigoConvenioSaudePlano, seqConvenioSaudePlano);
			if (lista == null || lista.isEmpty()) {
				final Short seq = this.getPacienteFacade().obterValorSeqPlanoCodPaciente(codigoPaciente);
				
				final AipConveniosSaudePaciente aipConveniosSaudePaciente = new AipConveniosSaudePaciente();

				aipConveniosSaudePaciente
						.setId(new AipConveniosSaudePacienteId(
								codigoPaciente, seq));
				aipConveniosSaudePaciente.setCriadoEm(new Date());
				aipConveniosSaudePaciente.setSituacao(DominioSituacao.A);
				aipConveniosSaudePaciente.setMatricula(null);
				aipConveniosSaudePaciente.setConvenio(this.getFaturamentoFacade().obterFatConvenioSaudePlanoPorChavePrimaria(
						new FatConvenioSaudePlanoId(codigoConvenioSaudePlano, seqConvenioSaudePlano)));

				this.getCadastroPacienteFacade()
						.incluirPlanoPacienteInternacao(aipConveniosSaudePaciente);
			}
		} catch (final ApplicationBusinessException e) {
			logError(e.getMessage(), e);
			throw e;
		} catch (final Exception e) {
			logError(e.getMessage(), e);
			throw new ApplicationBusinessException(
					AtualizaInternacaoRNExceptionCode.AIN_00702);
		}
	}

	/**
	 * ORADB Procedure AINK_INT_ATU.INSERE_DOCS_INTERNACAO
	 * 
	 * Insere Docs Internacao 1. Insere docs do convênio.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void inserirDocumentosInternacao(final Integer novoIntSeq,
			final Short novoCspCnvCodigo, final Byte novoCspSeq)
			throws ApplicationBusinessException {
		try {
			AinDocsInternacaoDAO ainDocsInternacaoDAO = this.getAinDocsInternacaoDAO();
			
			final List<FatConvTipoDocumentos> listaFatConvTipoDocumentos = getFaturamentoFacade()
					.pesquisarObrigatoriosPorFatConvenioSaudePlano(novoCspCnvCodigo, novoCspSeq);

			AinDocsInternacao ainDocsInternacao = null;
			AinDocsInternacaoId idAinDocsInternacao = null;
			for (final FatConvTipoDocumentos fatConvTipoDocumentos : listaFatConvTipoDocumentos) {
				final FatConvenioSaudePlano convenioSaudePlano = fatConvTipoDocumentos
						.getConvenioSaudePlano();
				final Short cspCnvCodigo = convenioSaudePlano.getId().getCnvCodigo();
				final Byte cspSeq = convenioSaudePlano.getId().getSeq();
				final Byte ctdTipoDocSeq = fatConvTipoDocumentos.getId().getTpdSeq();
				final DominioSimNao indDocEntregue = DominioSimNao.S;

				idAinDocsInternacao = new AinDocsInternacaoId(novoIntSeq,
						cspCnvCodigo, cspSeq, ctdTipoDocSeq);
				ainDocsInternacao = new AinDocsInternacao(idAinDocsInternacao,
						indDocEntregue);
				
				ainDocsInternacaoDAO.persistir(ainDocsInternacao);
			}
			ainDocsInternacaoDAO.flush();

		} catch (final Exception e) {
			logError(e.getMessage(), e);
			AtualizaInternacaoRNExceptionCode.AIN_00663.throwException(e);
		}
	}

	/**
	 * ORADB Procedure AINK_INT_ATU.EXCLUI_DOCS_INTERNACAO
	 * 
	 * Exclui Docs Internacao 1. Exclui docs do convênio.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void excluirDocumentosInternacao(final Integer seqInternacao,
			final Short codigoConvenioSaudePaciente, final Byte seqConvenioSaudePaciente)
			throws ApplicationBusinessException {
		try {
			AinDocsInternacaoDAO ainDocsInternacaoDAO = this.getAinDocsInternacaoDAO();
			
			final List<AinDocsInternacao> listaDocsInternacao = ainDocsInternacaoDAO.pesquisarPorInternacaoConvenio(seqInternacao,
					codigoConvenioSaudePaciente, seqConvenioSaudePaciente);
			for (final AinDocsInternacao ainDocsInternacao : listaDocsInternacao) {
				ainDocsInternacaoDAO.remover(ainDocsInternacao);
			}
			ainDocsInternacaoDAO.flush();
		} catch (final Exception e) {
			logError(e.getMessage(), e);
			AtualizaInternacaoRNExceptionCode.AIN_00664.throwException(e);
		}
	}

	/**
	 * ORADB Procedure/Function AINK_INT_ATU.INSERE_EXTRATO_LEITO
	 */
	public void insereExtratoLeito(final AinLeitos leito, final RapServidores digita,
			final RapServidores professor, final Date dataHoraInternacao,
			final Date dataHoraUltimoEvento, final AinInternacao internacao, final boolean ocupa)
			throws ApplicationBusinessException {
		// ------------------------------------------------------------------------
		// -- Insere Extrato Leito
		// -- 1. Registra internação na extratos do leito
		// -- 2. Essa regra não esta sendo utilizada em todos os eventos
		// -- pois os Forms de Internação, Estorno Internação já
		// -- possuem essa regra embutida
		// -- 3. A rotina Exclui_Lcto_Alta chamda no Estorno da Alta desfaz
		// -- as ações de Insere_Extrato_Leito e a Registra_Extrato_Leito
		// -- [IER53]
		// -- [INS] [>UPD] lto_lto_id -> new, 'OCUPA'
		// -- [?UPD] lto_lto_id -> old, 'DESOCUPA'
		// -- extratos_leito
		// -- [M] sim
		// ------------------------------------------------------------------------
		AghParametros aghParametros;
		Short codigoMovimentoLeito;

		if (ocupa) {
			aghParametros = this.getParametroFacade()
					.buscarAghParametro(AghuParametrosEnum.P_COD_MVTO_LTO_OCUPADO);
			if (aghParametros != null && aghParametros.getVlrNumerico() != null) {
				this.codigoMovimentoLeitoOcupado = aghParametros
						.getVlrNumerico().shortValue();
			} else {
				throw new ApplicationBusinessException(
						AtualizaInternacaoRNExceptionCode.AIN_00346);
			}
			codigoMovimentoLeito = this.codigoMovimentoLeitoOcupado;
		} else {
			aghParametros = this.getParametroFacade()
					.buscarAghParametro(AghuParametrosEnum.P_COD_MVTO_LTO_DESOCUPADO);
			if (aghParametros != null && aghParametros.getVlrNumerico() != null) {
				this.codigoMovimentoLeitoDesocupado = aghParametros
						.getVlrNumerico().shortValue();
			} else {
				throw new ApplicationBusinessException(
						AtualizaInternacaoRNExceptionCode.AIN_00342);
			}
			codigoMovimentoLeito = this.codigoMovimentoLeitoDesocupado;
		}

		if (leito != null) {
			final AinTiposMovimentoLeito tipoMovimento = this.getAinTiposMovimentoLeitoDAO().obterPorChavePrimaria(codigoMovimentoLeito);

			this.getLeitosInternacaoFacade().inserirExtrato(leito, tipoMovimento, digita,
					professor, null,
					dataHoraUltimoEvento != null ? dataHoraUltimoEvento
							: dataHoraInternacao, new Date(), null, internacao,
					null, null, null);
		}
	}

	/**
	 * ORADB Procedure/Function AINK_INT_ATU.REGISTRA_EXTRATO_LEITO
	 * 
	 * O atributo dataHoraAltaMedica não é utilizado nesta procedure, mas foi
	 * mantida na assinatura para evitar problemas.
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public void registraExtratoLeito(final String leitoID, final Integer seqInternacao,
			final Date dataHoraAltaMedica, final Date dataHoraUltimoEvento,
			final DominioTransacaoAltaPaciente transacao) throws ApplicationBusinessException {
		// ------------------------------------------------------------------------
		// -- Registra Extrato Leito
		// -- 1. Registra internação na extratos do leito
		// -- 2. Desocupa todos os leitos do quarto caso
		// -- o leito desocupado estiver com bloqueio refente ao paciente
		// -- [IER43] [IER42]
		// -- extrato_leitos
		// -- [?DEL] [>UPD] lto_id -> old (mas o dthr_ultimo evento é new)
		// -- [>UPD] "processa_alta" -> new
		// -- [M] sim
		// ------------------------------------------------------------------------
		if (StringUtils.isNotBlank(leitoID)) {
			final Calendar cal = Calendar.getInstance();
			final Date dataHoraAtual = cal.getTime();

			cal.add(Calendar.SECOND, 1);
			final Date dataHoraAtualMaisUmSegundo = cal.getTime();

			AghParametros aghParametros = this.getParametroFacade()
					.buscarAghParametro(AghuParametrosEnum.P_COD_MVTO_LTO_ALTA);
			if (aghParametros != null && aghParametros.getVlrNumerico() != null) {
				this.codigoMovimentoLeitoAlta = aghParametros.getVlrNumerico()
						.shortValue();
			} else {
				throw new ApplicationBusinessException(
						AtualizaInternacaoRNExceptionCode.AIN_00344);
			}

			aghParametros = this.getParametroFacade()
					.buscarAghParametro(AghuParametrosEnum.P_COD_MVTO_LTO_DESOCUPADO);
			if (aghParametros != null && aghParametros.getVlrNumerico() != null) {
				this.codigoMovimentoLeitoDesocupado = aghParametros
						.getVlrNumerico().shortValue();
			} else {
				throw new ApplicationBusinessException(
						AtualizaInternacaoRNExceptionCode.AIN_00342);
			}

			aghParametros = this.getParametroFacade()
					.buscarAghParametro(AghuParametrosEnum.P_COD_MVTO_LTO_INFECCAO);
			if (aghParametros != null && aghParametros.getVlrNumerico() != null) {
				this.codigoMovimentoLeitoInfeccao = aghParametros
						.getVlrNumerico().shortValue();
			} else {
				throw new ApplicationBusinessException(
						AtualizaInternacaoRNExceptionCode.AIN_00339);
			}

			aghParametros = this.getParametroFacade()
					.buscarAghParametro(AghuParametrosEnum.P_COD_MVTO_LTO_TECNICO);
			if (aghParametros != null && aghParametros.getVlrNumerico() != null) {
				this.codigoMovimentoLeitoTecnico = aghParametros
						.getVlrNumerico().shortValue();
			} else {
				throw new ApplicationBusinessException(
						AtualizaInternacaoRNExceptionCode.AIN_00610);
			}

			aghParametros = this.getParametroFacade()
					.buscarAghParametro(AghuParametrosEnum.P_COD_MVTO_LTO_PATOLOGIA);
			if (aghParametros != null && aghParametros.getVlrNumerico() != null) {
				this.codigoMovimentoLeitoPatologia = aghParametros
						.getVlrNumerico().shortValue();
			} else {
				throw new ApplicationBusinessException(
						AtualizaInternacaoRNExceptionCode.AIN_00341);
			}

			aghParametros = this.getParametroFacade()
					.buscarAghParametro(AghuParametrosEnum.P_COD_MVTO_LTO_BLOQ_ACOMP);
			if (aghParametros != null && aghParametros.getVlrNumerico() != null) {
				this.codigoMovimentoLeitoBloqueioAcompanhamento = aghParametros
						.getVlrNumerico().shortValue();
			} else {
				throw new ApplicationBusinessException(
						AtualizaInternacaoRNExceptionCode.AIN_00338);
			}

			aghParametros = this.getParametroFacade()
					.buscarAghParametro(AghuParametrosEnum.P_COD_MVTO_LTO_BLOQUEIO_LIMPEZA);
			if (aghParametros != null && aghParametros.getVlrNumerico() != null) {
				this.codigoMovimentoLeitoLimpeza = aghParametros
						.getVlrNumerico().shortValue();
			} else {
				throw new ApplicationBusinessException(
						AtualizaInternacaoRNExceptionCode.AIN_00000);
			}

			aghParametros = this.getParametroFacade()
					.buscarAghParametro(AghuParametrosEnum.P_JUSTIF_LTO_BLOQ_LIMPEZA);
			if (aghParametros != null && aghParametros.getVlrTexto() != null) {
				this.justificativaExtratoLeito = aghParametros.getVlrTexto();
			} else {
				throw new ApplicationBusinessException(
						AtualizaInternacaoRNExceptionCode.AIN_00000);
			}

			final List<Short> codigosMovimento = new ArrayList<Short>();
			codigosMovimento.add(this.codigoMovimentoLeitoBloqueioAcompanhamento);
			codigosMovimento.add(this.codigoMovimentoLeitoInfeccao);
			codigosMovimento.add(this.codigoMovimentoLeitoTecnico);
			codigosMovimento.add(this.codigoMovimentoLeitoPatologia);

			final List<RegistraExtratoLeitoVO> lista = this.getAinLeitosDAO().pesquisarRegistraExtratoLeitoVO(leitoID, codigosMovimento);

			if (lista != null && !lista.isEmpty()) {
				for (final RegistraExtratoLeitoVO registraExtratoLeitoVO : lista) {
					final AinLeitos leito = this.getAinLeitosDAO().obterPorChavePrimaria(registraExtratoLeitoVO.getLeitoID());
					final AinInternacao internacao = this.getAinInternacaoDAO().obterPorChavePrimaria(seqInternacao);
					Date dataLancamento;

					if (transacao != null
							&& transacao
									.equals(DominioTransacaoAltaPaciente.PROCESSA_ALTA)) {
						dataLancamento = dataHoraUltimoEvento;
						final AinTiposMovimentoLeito tipoMovimentoLeitoAlta = this.getAinTiposMovimentoLeitoDAO().obterPorChavePrimaria(this.codigoMovimentoLeitoAlta);

						final String justificativa = "CRIADO A PARTIR DA ALTA DO LEITO "
								+ leitoID;
						this.getLeitosInternacaoFacade().inserirExtrato(leito,
								tipoMovimentoLeitoAlta, null, null,
								justificativa, dataHoraUltimoEvento,
								dataHoraAtual, null, internacao, null, null,
								null);
					} else {
						dataLancamento = dataHoraUltimoEvento;
					}

					String justificativa = null;
					AinTiposMovimentoLeito tipoMovimentoLeito;

					if (Boolean.TRUE.equals(registraExtratoLeitoVO
							.getIndBloqueioLimpeza())) {
						justificativa = this.justificativaExtratoLeito;
						tipoMovimentoLeito = this.getAinTiposMovimentoLeitoDAO().obterPorChavePrimaria(this.codigoMovimentoLeitoLimpeza);
					} else {
						tipoMovimentoLeito = this.getAinTiposMovimentoLeitoDAO().obterPorChavePrimaria(this.codigoMovimentoLeitoDesocupado);
					}

					this.getLeitosInternacaoFacade().inserirExtrato(leito,
							tipoMovimentoLeito, null, null, justificativa,
							dataLancamento, dataHoraAtualMaisUmSegundo, null,
							internacao, null, null, null);
				}
			}
		}
	}

	/**
	 * ORADB Package AINK_INT_ATU.RN_INT_REG_MOV_INT ORADB AINP_INT_REG_MOV_INT
	 * 
	 * A package AINK_INT_ATU.RN_INT_REG_MOV_INT simplesmente chama a procedure
	 * AINP_INT_REG_MOV_INT
	 */
	public RegistrarMovimentoInternacoesVO registrarMovimentoInternacoes(
			final Integer seqInternacao, final Integer matriculaServidorDigita,
			final Short vinCodigoServidorDigita, final Date dthrInternacao,
			final Short numeroQuarto, final Integer matriculaServidorProfessor,
			final Short vinCodigoServidorProfessor, final Short seqUnidadeFuncional,
			final Short seqEspecialidade, final String leitoID, final Date dthrAltaMedica,
			final Date dthrUltimoEvento, final Integer tmiSeq,
			final DominioTransacaoAltaPaciente transacao, final Date dtSaidaPaciente)
			throws ApplicationBusinessException {
		try {
			Integer codigoMovimentoAlta = null;
			Integer codigoMovimentoInternacao = null;

			AghParametros aghParametros = this.getParametroFacade()
					.buscarAghParametro(AghuParametrosEnum.P_COD_MVTO_INT_ALTA);
			if (aghParametros != null && aghParametros.getVlrNumerico() != null) {
				codigoMovimentoAlta = aghParametros.getVlrNumerico().intValue();
			} else {
				throw new ApplicationBusinessException(
						AtualizaInternacaoRNExceptionCode.AIN_00344);
			}

			aghParametros = this.getParametroFacade()
					.buscarAghParametro(AghuParametrosEnum.P_COD_MVTO_INT_INTERNACAO);
			if (aghParametros != null && aghParametros.getVlrNumerico() != null) {
				codigoMovimentoInternacao = aghParametros.getVlrNumerico().intValue();
			} else {
				throw new ApplicationBusinessException(
						AtualizaInternacaoRNExceptionCode.AIN_00282);
			}

			Integer tmi;
			Date dataHoraLancamento;
			if (DominioTransacaoAltaPaciente.PROCESSA_ALTA.equals(transacao)) {
				tmi = codigoMovimentoAlta;
				dataHoraLancamento = dtSaidaPaciente;
			} else if (DominioTransacaoAltaPaciente.INTERNACAO
					.equals(transacao)) {
				tmi = codigoMovimentoInternacao;
				dataHoraLancamento = dthrInternacao;
			} else {
				tmi = tmiSeq;
				dataHoraLancamento = dthrUltimoEvento;
			}

			final RapServidoresId idRapServidor = new RapServidoresId();
			idRapServidor.setMatricula(matriculaServidorProfessor);
			idRapServidor.setVinCodigo(vinCodigoServidorProfessor);
			final RapServidores rapServidor = this.getRegistroColaboradorFacade()
					.obterRapServidoresPorChavePrimaria(idRapServidor);

			final BuscarLocalInternacaoVO buscarLocalInternacaoVO = buscarLocalInternacao(
					leitoID, numeroQuarto, seqUnidadeFuncional);

			final AinMovimentosInternacao movimentoInternacao = new AinMovimentosInternacao();
			movimentoInternacao.setInternacao(this.getAinInternacaoDAO().obterPorChavePrimaria(seqInternacao));
			movimentoInternacao.setTipoMovimentoInternacao(this.getAinTiposMvtoInternacaoDAO().obterPorChavePrimaria(tmi));
			movimentoInternacao.setDthrLancamento(dataHoraLancamento);
			if (buscarLocalInternacaoVO.getNumeroQuarto() != null) {
				movimentoInternacao
						.setQuarto(this.getAinQuartosDAO().obterPorChavePrimaria(buscarLocalInternacaoVO.getNumeroQuarto()));
			}
			movimentoInternacao.setServidor(rapServidor);
			if (buscarLocalInternacaoVO.getSeqUnidadeFuncional() != null) {
				movimentoInternacao.setUnidadeFuncional(this.getAghuFacade().obterAghUnidadesFuncionaisPorChavePrimaria(
						buscarLocalInternacaoVO.getSeqUnidadeFuncional()));
			}
			movimentoInternacao.setEspecialidade(this.getAghuFacade().obterAghEspecialidadesPorChavePrimaria(seqEspecialidade));
			if (buscarLocalInternacaoVO.getLeitoID() != null) {
				movimentoInternacao.setLeito(this.getAinLeitosDAO().obterPorChavePrimaria(buscarLocalInternacaoVO.getLeitoID()));
			}

			this.getMovimentosInternacaoON()
					.persistirMovimentoInternacao(movimentoInternacao);

			final RegistrarMovimentoInternacoesVO registrarMovimentoInternacoesVO = new RegistrarMovimentoInternacoesVO();
			registrarMovimentoInternacoesVO
					.setNumeroQuarto(buscarLocalInternacaoVO.getNumeroQuarto());
			registrarMovimentoInternacoesVO
					.setSeqUnidadeFuncional(buscarLocalInternacaoVO
							.getSeqUnidadeFuncional());
			registrarMovimentoInternacoesVO.setLeitoID(buscarLocalInternacaoVO
					.getLeitoID());

			return registrarMovimentoInternacoesVO;
		} catch (final BaseRuntimeException e) {
			logError(e.getMessage(), e);
			throw new ApplicationBusinessException(e.getCode());
		} catch (final ApplicationBusinessException e) {
			logError(e.getMessage(), e);
			throw e;		
		} catch (final Exception e) {
			logError(e.getMessage(), e);
			throw new ApplicationBusinessException(
					AtualizaInternacaoRNExceptionCode.AIN_00351);
		}
	}

	/**
	 * ORADB AINK_INT_RN.RN_INTP_ATU_LOCAL
	 */
	public BuscarLocalInternacaoVO buscarLocalInternacao(final String leitoID,
			final Short numeroQuarto, final Short seqUnidadeFuncional) {
		final BuscarLocalInternacaoVO buscarLocalInternacaoVO = new BuscarLocalInternacaoVO();
		buscarLocalInternacaoVO.setLeitoID(leitoID);
		buscarLocalInternacaoVO.setNumeroQuarto(numeroQuarto);
		buscarLocalInternacaoVO.setSeqUnidadeFuncional(seqUnidadeFuncional);

		if (StringUtils.isNotBlank(leitoID)) {
			final AinLeitos leito = this.getAinLeitosDAO().obterPorChavePrimaria(leitoID);
			buscarLocalInternacaoVO.setNumeroQuarto(leito.getQuarto()
					.getNumero());
			buscarLocalInternacaoVO.setSeqUnidadeFuncional(leito
					.getUnidadeFuncional().getSeq());
		} else if (numeroQuarto != null) {
			final AinQuartos quarto = this.getAinQuartosDAO().obterPorChavePrimaria(numeroQuarto);
			buscarLocalInternacaoVO.setSeqUnidadeFuncional(quarto
					.getUnidadeFuncional().getSeq());
		}

		return buscarLocalInternacaoVO;
	}

	/**
	 * ORADB Function AINK_INT_ATU.DEFINE_TRANSACAO
	 */
	public DominioTransacaoAltaPaciente defineTransacao(final String oldTamCodigo,
			final String newTamCodigo, final Date oldDthrAltaMedica,
			final Date newDthrAltaMedica, final Date oldDtSaidaPaciente,
			final Date newDtSaidaPaciente) throws ApplicationBusinessException {
		DominioTransacaoAltaPaciente transacao = null;

		try {
			// Rotina de saída do paciente
			if (newDthrAltaMedica != null && oldDtSaidaPaciente == null
					&& newDtSaidaPaciente != null) {
				transacao = DominioTransacaoAltaPaciente.PROCESSA_ALTA;
			} else
			// Rotina de estorno da alta/data de saída do paciente
			if ((oldTamCodigo != null && newTamCodigo == null
					&& oldDthrAltaMedica != null && newDthrAltaMedica == null)
					|| (oldTamCodigo != null && newTamCodigo != null
							&& oldDthrAltaMedica != null
							&& newDthrAltaMedica != null
							&& oldDtSaidaPaciente != null && newDtSaidaPaciente == null)) {
				transacao = DominioTransacaoAltaPaciente.ESTORNA_ALTA;
			} else
			// Rotina de Alteração dos dados da alta do paciente
			if ((oldDthrAltaMedica != null && newDthrAltaMedica != null && !oldDthrAltaMedica
					.equals(newDthrAltaMedica))
					|| (oldTamCodigo != null && newTamCodigo != null && !oldTamCodigo
							.equals(newTamCodigo))
					|| (oldDtSaidaPaciente != null
							&& newDtSaidaPaciente != null && !oldDtSaidaPaciente
							.equals(newDtSaidaPaciente))) {
				transacao = DominioTransacaoAltaPaciente.ALTERA_ALTA;
			}
		} catch (final Exception e) {
			logError(e.getMessage(), e);
			AtualizaInternacaoRNExceptionCode.AIN_00706.throwException(e);
		}

		return transacao;
	}

	/**
	 * ORADB Procedure/Function AINK_INT_ATU.DEFINE_TMI
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public String defineTipoMovimentoInternacao(final RapServidores professorNovo,
			final RapServidores professorAntigo, final AinLeitos leitoNovo,
			final AinLeitos leitoAntigo, final AinQuartos quartoNovo,
			final AinQuartos quartoAntigo,
			final AghUnidadesFuncionais unidadeFuncionalNova,
			final AghUnidadesFuncionais unidadeFuncionalAntiga,
			final AghEspecialidades especialidadeNova,
			final AghEspecialidades especialidadeAntiga) throws ApplicationBusinessException {
		// ------------------------------------------------------------------------
		// -- Define Tipo Movimento Internacao
		// -- 1. Avalia qual o tipo de movimento paciente através da comparação
		// -- dos valores old e new da internacao e retorna um inteiro
		// -- de acordo com a tipo de transferência do paciente
		// --
		// -- Código Transferência entre
		// -- [02] Uma unidade de emergência para outra unidade
		// -- [15] Clínica(Especialidade)
		// -- [14] Unidades
		// -- [13] Leito/Quarto
		// -- [12] Especialidade
		// -- [11] Profissional
		// ------------------------------------------------------------------------
		try {
			// Obtém dados para comparação
			final Short seqUnidadeFuncionalAntiga = this.getInternacaoUtilRN().obtemUnf(
					leitoAntigo != null ? leitoAntigo.getLeitoID() : null,
					quartoAntigo != null ? quartoAntigo.getNumero() : null,
					unidadeFuncionalAntiga != null ? unidadeFuncionalAntiga
							.getSeq() : null);
			final Short seqUnidadeFuncionalNova = this.getInternacaoUtilRN().obtemUnf(
					leitoNovo != null ? leitoNovo.getLeitoID() : null,
					quartoNovo != null ? quartoNovo.getNumero() : null,
					unidadeFuncionalNova != null ? unidadeFuncionalNova
							.getSeq() : null);

			final DominioSimNao indUnidadeEmergenciaAntiga = this.getInternacaoUtilRN()
					.obtemIndUnidEmergencia(seqUnidadeFuncionalAntiga);
			final DominioSimNao indUnidadeEmergenciaNova = this.getInternacaoUtilRN()
					.obtemIndUnidEmergencia(seqUnidadeFuncionalNova);
			
			/*-------------------------------------------------------------------------*/
			//twickert: Essa parte estava com problema apos o refactory
			//de forma intermitente ocorre entity is not managed pq pela fachada pega
			//o entity manualmente, sendo que em determinados casos deve estar criando ele
			//o que nao associa ele a atual conversação e ao usar lança a exceção:
			//08:17:19,365 ERROR [AtualizaInternacaoRN] Entity not managed
			//java.lang.IllegalArgumentException: Entity not managed
			
			//Analisando o codigo abaixo, aparentemente está incorreto e não é necessário
			//por isso foi comentado.
			//Caso tenha problema no futuro usar o obter por chave primaria e o obter original.
			
			//TODO: REVER ESTES REFRESH. CRUDS DOS ITENS DA PRESCRIÇÃO NÃO DEVERIAM
			//LANÇAR EXCEPTIONS COM ROLLBACK
//			if (especialidadeNova != null){
//				aghuFacade.refresh(especialidadeNova);				
//			}
//			if (especialidadeAntiga != null){
//				aghuFacade.refresh(especialidadeAntiga);				
//			}
			/*-------------------------------------------------------------------------*/

			String param = null;
			// Início das comparações
			if (this.getInternacaoUtilRN().modificados(indUnidadeEmergenciaAntiga,
					indUnidadeEmergenciaNova)) {
				param = AghuParametrosEnum.P_COD_MVTO_INT_TRSF_SO.toString();
			} else {
				final Integer codigoEspecialidadeAntiga = this.getInternacaoUtilRN()
						.obtemClinicaEspecialidade(especialidadeAntiga.getSeq());
				final Integer codigoEspecialidadeNova = this.getInternacaoUtilRN()
						.obtemClinicaEspecialidade(especialidadeNova.getSeq());

				if (this.getInternacaoUtilRN().modificados(
						codigoEspecialidadeAntiga, codigoEspecialidadeNova)) {
					param = AghuParametrosEnum.P_COD_MVTO_INT_TRSF_CLIN
							.toString();
				} else if (this.getInternacaoUtilRN().modificados(
						seqUnidadeFuncionalAntiga, seqUnidadeFuncionalNova)) {
					param = AghuParametrosEnum.P_COD_MVTO_INT_TRSF_UNIDADE
							.toString();
				} else if (this.getInternacaoUtilRN().modificados(leitoAntigo,
						leitoNovo)
						|| this.getInternacaoUtilRN().modificados(quartoAntigo,
								quartoNovo)) {
					param = AghuParametrosEnum.P_COD_MVTO_INT_TRSF_LTO
							.toString();
				} else if (this.getInternacaoUtilRN().modificados(
						especialidadeAntiga, especialidadeNova)) {
					param = AghuParametrosEnum.P_COD_MVTO_INT_TRSF_ESP
							.toString();
				} else if (this.getInternacaoUtilRN().modificados(professorAntigo,
						professorNovo)) {
					param = AghuParametrosEnum.P_COD_MVTO_INT_TRSF_EQPE
							.toString();
				}
			}

			if (param != null) {
				return getAinTiposMvtoInternacaoDAO().buscarPorParametroNumerico(param);
			} else {
				String retValue = null;
				final AghParametros aghParametros = this.getParametroFacade()
						.buscarAghParametro(AghuParametrosEnum.P_COD_MVTO_TRSF_INEXISTENTE);
				if (aghParametros != null
						&& aghParametros.getVlrTexto() != null) {
					retValue = aghParametros.getVlrTexto();
				}

				return retValue != null ? retValue
						: "TRANSFERENCIA INEXISTENTE";
			}
		} catch (final Exception e) {
			logError(e.getMessage(), e);
			throw new ApplicationBusinessException(
					AtualizaInternacaoRNExceptionCode.AIN_00672);
		}
	}

	/**
	 * ORADB Procedure/Function AINK_INT_ATU.RN_INTP_ATU_ATD_RN
	 * 
	 * Atualiza informações de leito do RN.
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public void atualizaInformacoesLeito(final Integer gsoPacCodigo, final Short gsoSeqp,
			final AinLeitos leito, final AinQuartos quarto,
			final AghUnidadesFuncionais unidadeFuncional, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws ApplicationBusinessException {

		final BuscarLocalInternacaoVO buscarLocalInternacaoVO = buscarLocalInternacao(
				(leito != null) ? leito.getLeitoID() : null,
				(quarto != null) ? quarto.getNumero() : null,
				(unidadeFuncional != null) ? unidadeFuncional.getSeq() : null);

		final McoEscalaLeitoRns escalaLeitoRns = this.getPerinatologiaFacade().obterMcoEscalaLeitoRnsPorLeito(
				buscarLocalInternacaoVO.getLeitoID());

		RapServidores rapServidorResponsavel = null;
		if (escalaLeitoRns != null) {
			rapServidorResponsavel = escalaLeitoRns.getServidorResponsavel();
		}
		
		final List<AghAtendimentos> atendimentos = this.getAghuFacade().pesquisarAtendimentosGestacoesEmAtendimento(gsoPacCodigo,gsoSeqp);

		if (atendimentos != null && !atendimentos.isEmpty()) {
			try {
				// Atualizadas as informações de Leito do recem-nascido.
				for (final AghAtendimentos atendimento : atendimentos) {
					if (StringUtils.isNotBlank(buscarLocalInternacaoVO.getLeitoID())){
						atendimento.setLeito(this.getAinLeitosDAO().obterPorChavePrimaria(buscarLocalInternacaoVO.getLeitoID()));						
					}
					else{
						atendimento.setLeito(null);
					}
					if (buscarLocalInternacaoVO.getNumeroQuarto() != null){
						atendimento.setQuarto(getCadastrosBasicosInternacaoFacade()
								.obterQuarto(buscarLocalInternacaoVO
										.getNumeroQuarto()));						
					}
					else{
						atendimento.setQuarto(null);
					}
					atendimento.setUnidadeFuncional(this.getAghuFacade().obterAghUnidadesFuncionaisPorChavePrimaria(
							buscarLocalInternacaoVO.getSeqUnidadeFuncional()));

					if (rapServidorResponsavel != null) {
						atendimento.setServidor(rapServidorResponsavel);
					}

					final AghAtendimentos atendimentoOld = getPacienteFacade().clonarAtendimento(atendimento);
					this.getPacienteFacade().persistirAtendimento(atendimento, atendimentoOld, nomeMicrocomputador, dataFimVinculoServidor);
				}
			} catch (final Exception e) {
				logError(e.getMessage(), e);
				throw new ApplicationBusinessException(
						AtualizaInternacaoRNExceptionCode.AIN_00842);
			}
		}
	}

	/**
	 * ORADB Procedure/Function AINK_INT_ATU.RN_INTP_ATU_DTFIM_RN
	 */
	public void atualizaDataFim(final Integer gsoPacCodigo, final Short gsoSeqp, final Date dataHoraFim, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws ApplicationBusinessException {
		try {
			
			final List<AghAtendimentos> atendimentos = this.getAghuFacade().pesquisarAtendimentosGestacoesEmAtendimento(gsoPacCodigo,gsoSeqp);

			if (atendimentos != null && !atendimentos.isEmpty()) {
				AghAtendimentos atendimentoOld = null;
				for (final AghAtendimentos atendimento : atendimentos) {
					atendimentoOld = getPacienteFacade().clonarAtendimento(atendimento);
					atendimento.setDthrFim(dataHoraFim);
					atendimento.setIndPacAtendimento(DominioPacAtendimento.N);

					this.getPacienteFacade().persistirAtendimento(atendimento, atendimentoOld, nomeMicrocomputador, dataFimVinculoServidor);
				}
			}
		} catch (final Exception e) {
			logError(e.getMessage(), e);
			throw new ApplicationBusinessException(
					AtualizaInternacaoRNExceptionCode.AIN_00842);
		}
	}

	/**
	 * ORADB Procedure/Function AINK_INT_ATU.RN_INTP_ATU_EST_RN
	 */
	public void atualizaEstadoAtendimento(final Integer gsoPacCodigo, final Short gsoSeqp, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws ApplicationBusinessException {
		try {
			final List<AghAtendimentos> atendimentos = getAghuFacade().pesquisarAtendimentosGestacoesFinalizados(gsoPacCodigo,gsoSeqp);

			if (atendimentos != null && !atendimentos.isEmpty()) {
				AghAtendimentos atendimentoOld = null;
				for (final AghAtendimentos atendimento : atendimentos) {
					atendimentoOld = getPacienteFacade().clonarAtendimento(atendimento);
					atendimento.setDthrFim(null);
					atendimento.setIndPacAtendimento(DominioPacAtendimento.S);

					this.getPacienteFacade().persistirAtendimento(atendimento, atendimentoOld, nomeMicrocomputador, dataFimVinculoServidor);
				}
			}
		} catch (final Exception e) {
			logError(e.getMessage(), e);
			throw new ApplicationBusinessException(
					AtualizaInternacaoRNExceptionCode.AIN_00842);
		}
	}

	/**
	 * ORADB Procedure/Function AINK_INT_ATU.RN_INTP_ATU_DT_CONV
	 * 
	 * Atualiza a Data da Alta na tabela de Convênios. Obs.: O parametro
	 * dataSaidaPacienteAntiga é ignorado, mas foi mantido na assinatura para
	 * evitar problemas de compatibilidade.
	 */
	public void atualizaDataAltaConvenio(final Integer seqInternacao,
			final Date dataSaidaPacienteNova, final Date dataSaidaPacienteAntiga)
			throws ApplicationBusinessException {
		try {
			final AinInternacao internacao = this.getAinInternacaoDAO().obterPorChavePrimaria(seqInternacao);
			final List<PacIntdConv> lista = getPacienteFacade().obterListaPacIntdConvPorAtendimento(internacao.getAtendimento().getSeq());

			if (lista != null && !lista.isEmpty()) {
				for (final PacIntdConv pacIntdConv : lista) {
					pacIntdConv.setDataAltaMdca(dataSaidaPacienteNova);
					this.getInternacaoON().persistirInternacaoConvenio(pacIntdConv);
				}
			}

		} catch (final Exception e) {
			logError(e.getMessage(), e);
			throw new ApplicationBusinessException(
					AtualizaInternacaoRNExceptionCode.AIN_00896);
		}
	}
	
	/**
	 * Método temporário que atualiza atendimento do recem-nascido na alta da mãe
	 */
	public void atualizaDataFimRecemNascido(final AghAtendimentos atendimentoMae, final Date dataHoraFim, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws ApplicationBusinessException {
		try {
			
			final List<AghAtendimentos> atendimentos = this.getAghuFacade().pesquisarAtendimentosEmAtendimentoPeloAtendimentoMae(
					atendimentoMae);

			if (atendimentos != null && !atendimentos.isEmpty()) {
				AghAtendimentos atendimentoOld = null;
				for (final AghAtendimentos atendimento : atendimentos) {
					atendimentoOld = getPacienteFacade().clonarAtendimento(atendimento);
					atendimento.setDthrFim(dataHoraFim);
					atendimento.setIndPacAtendimento(DominioPacAtendimento.N);

					this.getPacienteFacade().persistirAtendimento(atendimento, atendimentoOld, nomeMicrocomputador, dataFimVinculoServidor);
				}
			}
		} catch (final Exception e) {
			logError(e.getMessage(), e);
			throw new ApplicationBusinessException(
					AtualizaInternacaoRNExceptionCode.AIN_00842);
		}
	}

	public String getCodigoTipoAlta() {
		return this.codigoTipoAlta;
	}

	public void setCodigoTipoAlta(final String codigoTipoAlta) {
		this.codigoTipoAlta = codigoTipoAlta;
	}

	public String getJustificativaleitoBloqueioLimpeza() {
		return this.justificativaleitoBloqueioLimpeza;
	}

	public void setJustificativaleitoBloqueioLimpeza(
			final String justificativaleitoBloqueioLimpeza) {
		this.justificativaleitoBloqueioLimpeza = justificativaleitoBloqueioLimpeza;
	}

	public String getJustificativaExtratoLeito() {
		return this.justificativaExtratoLeito;
	}

	public void setJustificativaExtratoLeito(final String justificativaExtratoLeito) {
		this.justificativaExtratoLeito = justificativaExtratoLeito;
	}

	public String getIdLeitoAntigo() {
		return this.idLeitoAntigo;
	}

	public void setIdLeitoAntigo(final String idLeitoAntigo) {
		this.idLeitoAntigo = idLeitoAntigo;
	}

	public Short getCodigoMovimentoLeitoAlta() {
		return this.codigoMovimentoLeitoAlta;
	}

	public void setCodigoMovimentoLeitoAlta(final Short codigoMovimentoLeitoAlta) {
		this.codigoMovimentoLeitoAlta = codigoMovimentoLeitoAlta;
	}

	public Short getCodigoMovimentoLeitoDesocupado() {
		return this.codigoMovimentoLeitoDesocupado;
	}

	public void setCodigoMovimentoLeitoDesocupado(
			final Short codigoMovimentoLeitoDesocupado) {
		this.codigoMovimentoLeitoDesocupado = codigoMovimentoLeitoDesocupado;
	}

	public Short getCodigoMovimentoLeitoInternacao() {
		return this.codigoMovimentoLeitoInternacao;
	}

	public void setCodigoMovimentoLeitoInternacao(
			final Short codigoMovimentoLeitoInternacao) {
		this.codigoMovimentoLeitoInternacao = codigoMovimentoLeitoInternacao;
	}

	public Short getCodigoMovimentoLeitoLimpeza() {
		return this.codigoMovimentoLeitoLimpeza;
	}

	public void setCodigoMovimentoLeitoLimpeza(final Short codigoMovimentoLeitoLimpeza) {
		this.codigoMovimentoLeitoLimpeza = codigoMovimentoLeitoLimpeza;
	}

	public Short getCodigoMovimentoLeitoPertences() {
		return this.codigoMovimentoLeitoPertences;
	}

	public void setCodigoMovimentoLeitoPertences(
			final Short codigoMovimentoLeitoPertences) {
		this.codigoMovimentoLeitoPertences = codigoMovimentoLeitoPertences;
	}

	public Short getTipoMovimentoLeitoCodigoAlta() {
		return this.tipoMovimentoLeitoCodigoAlta;
	}

	public void setTipoMovimentoLeitoCodigoAlta(
			final Short tipoMovimentoLeitoCodigoAlta) {
		this.tipoMovimentoLeitoCodigoAlta = tipoMovimentoLeitoCodigoAlta;
	}

	public String getCodigoTipoAltaObito() {
		return this.codigoTipoAltaObito;
	}

	public void setCodigoTipoAltaObito(final String codigoTipoAltaObito) {
		this.codigoTipoAltaObito = codigoTipoAltaObito;
	}

	public Integer getCodigoMovimentoInternacao() {
		return this.codigoMovimentoInternacao;
	}

	public void setCodigoMovimentoInternacao(final Integer codigoMovimentoInternacao) {
		this.codigoMovimentoInternacao = codigoMovimentoInternacao;
	}

	public Integer getCodigoMovimentoInternacaoAlta() {
		return this.codigoMovimentoInternacaoAlta;
	}

	public void setCodigoMovimentoInternacaoAlta(
			final Integer codigoMovimentoInternacaoAlta) {
		this.codigoMovimentoInternacaoAlta = codigoMovimentoInternacaoAlta;
	}

	public Byte getCodigoMovimentoInternacaoInternado() {
		return this.codigoMovimentoInternacaoInternado;
	}

	public void setCodigoMovimentoInternacaoInternado(
			final Byte codigoMovimentoInternacaoInternado) {
		this.codigoMovimentoInternacaoInternado = codigoMovimentoInternacaoInternado;
	}

	public DominioSituacaoSolicitacaoInternacao getSituacaoSolicitacaoEfetuada() {
		return this.situacaoSolicitacaoEfetuada;
	}

	public void setSituacaoSolicitacaoEfetuada(
			final DominioSituacaoSolicitacaoInternacao situacaoSolicitacaoEfetuada) {
		this.situacaoSolicitacaoEfetuada = situacaoSolicitacaoEfetuada;
	}

	public DominioSituacaoSolicitacaoInternacao getSituacaoSolicitacaoPendente() {
		return this.situacaoSolicitacaoPendente;
	}

	public void setSituacaoSolicitacaoPendente(
			final DominioSituacaoSolicitacaoInternacao situacaoSolicitacaoPendente) {
		this.situacaoSolicitacaoPendente = situacaoSolicitacaoPendente;
	}

	public DominioSituacaoSolicitacaoInternacao getSituacaoSolicitacaoAtendida() {
		return this.situacaoSolicitacaoAtendida;
	}

	public void setSituacaoSolicitacaoAtendida(
			final DominioSituacaoSolicitacaoInternacao situacaoSolicitacaoAtendida) {
		this.situacaoSolicitacaoAtendida = situacaoSolicitacaoAtendida;
	}

	public DominioSituacaoSolicitacaoInternacao getSituacaoSolicitacaoCancelada() {
		return this.situacaoSolicitacaoCancelada;
	}

	public void setSituacaoSolicitacaoCancelada(
			final DominioSituacaoSolicitacaoInternacao situacaoSolicitacaoCancelada) {
		this.situacaoSolicitacaoCancelada = situacaoSolicitacaoCancelada;
	}

	public Short getCodigoMovimentoLeitoBloqueioAcompanhamento() {
		return this.codigoMovimentoLeitoBloqueioAcompanhamento;
	}

	public void setCodigoMovimentoLeitoBloqueioAcompanhamento(
			final Short codigoMovimentoLeitoBloqueioAcompanhamento) {
		this.codigoMovimentoLeitoBloqueioAcompanhamento = codigoMovimentoLeitoBloqueioAcompanhamento;
	}

	public Short getCodigoMovimentoLeitoInfeccao() {
		return this.codigoMovimentoLeitoInfeccao;
	}

	public void setCodigoMovimentoLeitoInfeccao(
			final Short codigoMovimentoLeitoInfeccao) {
		this.codigoMovimentoLeitoInfeccao = codigoMovimentoLeitoInfeccao;
	}

	public Short getCodigoMovimentoLeitoOcupado() {
		return this.codigoMovimentoLeitoOcupado;
	}

	public void setCodigoMovimentoLeitoOcupado(final Short codigoMovimentoLeitoOcupado) {
		this.codigoMovimentoLeitoOcupado = codigoMovimentoLeitoOcupado;
	}

	public Short getCodigoMovimentoLeitoPatologia() {
		return this.codigoMovimentoLeitoPatologia;
	}

	public void setCodigoMovimentoLeitoPatologia(
			final Short codigoMovimentoLeitoPatologia) {
		this.codigoMovimentoLeitoPatologia = codigoMovimentoLeitoPatologia;
	}

	public Short getCodigoMovimentoLeitoTecnico() {
		return this.codigoMovimentoLeitoTecnico;
	}

	public void setCodigoMovimentoLeitoTecnico(final Short codigoMovimentoLeitoTecnico) {
		this.codigoMovimentoLeitoTecnico = codigoMovimentoLeitoTecnico;
	}

	public String getCodigoEventoAlta() {
		return this.codigoEventoAlta;
	}

	public void setCodigoEventoAlta(final String codigoEventoAlta) {
		this.codigoEventoAlta = codigoEventoAlta;
	}

	public String getCodigoEventoSaida() {
		return this.codigoEventoSaida;
	}

	public void setCodigoEventoSaida(final String codigoEventoSaida) {
		this.codigoEventoSaida = codigoEventoSaida;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected ICadastrosBasicosInternacaoFacade getCadastrosBasicosInternacaoFacade() {
		return this.cadastrosBasicosInternacaoFacade;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	
	protected ILeitosInternacaoFacade getLeitosInternacaoFacade() {
		return this.leitosInternacaoFacade;
	}

	protected ISolicitacaoInternacaoFacade getSolicitacaoInternacaoFacade() {
		return solicitacaoInternacaoFacade;
	}

	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}

	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}

	protected IPerinatologiaFacade getPerinatologiaFacade() {
		return perinatologiaFacade;
	}

	protected ICadastroPacienteFacade getCadastroPacienteFacade() {
		return cadastroPacienteFacade;
	}
	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected MovimentosInternacaoON getMovimentosInternacaoON() {
		return movimentosInternacaoON;
	}

	protected InternacaoRN getInternacaoRN() {
		return internacaoRN;
	}

	protected InternacaoON getInternacaoON() {
		return internacaoON;
	}

	protected InternacaoUtilRN getInternacaoUtilRN() {
		return internacaoUtilRN;
	}

	protected CadastroInternacaoON getCadastroInternacaoON() {
		return cadastroInternacaoON;
	}
	
	protected AinExtratoLeitosDAO getAinExtratoLeitosDAO() {
		return ainExtratoLeitosDAO;
	}
		
	protected AinLeitosDAO getAinLeitosDAO() {
		return ainLeitosDAO;
	}
	
	protected AinQuartosDAO getAinQuartosDAO() {
		return ainQuartosDAO;
	}
	
	protected AinInternacaoDAO getAinInternacaoDAO() {
		return ainInternacaoDAO;
	}
	
	protected AinSolicitacoesInternacaoDAO getAinSolicitacoesInternacaoDAO() {
		return ainSolicitacoesInternacaoDAO;
	}
	
	protected AinAtendimentosUrgenciaDAO getAinAtendimentosUrgenciaDAO() {
		return ainAtendimentosUrgenciaDAO;
	}
	
	protected AinTiposAltaMedicaDAO getAinTiposAltaMedicaDAO() {
		return ainTiposAltaMedicaDAO;
	}
	
	protected AinTiposMovimentoLeitoDAO getAinTiposMovimentoLeitoDAO() {
		return ainTiposMovimentoLeitoDAO;
	}
	
	protected AinMovimentoInternacaoDAO getAinMovimentoInternacaoDAO() {
		return ainMovimentoInternacaoDAO;
	}
	
	protected AinDocsInternacaoDAO getAinDocsInternacaoDAO() {
		return ainDocsInternacaoDAO;
	}
	
	protected AinTiposMvtoInternacaoDAO getAinTiposMvtoInternacaoDAO() {
		return ainTiposMvtoInternacaoDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
