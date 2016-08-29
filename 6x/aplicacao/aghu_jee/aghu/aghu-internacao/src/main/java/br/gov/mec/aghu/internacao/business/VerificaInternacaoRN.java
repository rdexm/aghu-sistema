package br.gov.mec.aghu.internacao.business;

import static br.gov.mec.aghu.model.AipPacientes.VALOR_MAXIMO_PRONTUARIO;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.exceptioncode.AGHUBaseBusinessExceptionCode;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.controleinfeccao.business.IControleInfeccaoFacade;
import br.gov.mec.aghu.dao.AghWork;
import br.gov.mec.aghu.dao.EsquemasOracleEnum;
import br.gov.mec.aghu.dao.ObjetosBancoOracleEnum;
import br.gov.mec.aghu.dominio.DominioSexoDeterminante;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoDataObito;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.cadastrosapoio.business.IFaturamentoApoioFacade;
import br.gov.mec.aghu.internacao.business.vo.VerificarContaPacienteVO;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.dao.AinInternacaoDAO;
import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinAtendimentosUrgencia;
import br.gov.mec.aghu.model.AinEscalasProfissionalInt;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Essa classe deve implementar todas procedures e functions referentes a
 * package AINK_INT_VER do banco de dados Oracle. Essa package está separada
 * nesta classe, pois é muito extensa.
 */

@SuppressWarnings({"deprecation","PMD.AghuTooManyMethods", "PMD.ExcessiveClassLength"})
@Stateless
public class VerificaInternacaoRN extends BaseBusiness {


@EJB
private AltaPacienteObservacaoON altaPacienteObservacaoON;

	private static final Log LOG = LogFactory.getLog(VerificaInternacaoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@Inject
	private AinInternacaoDAO ainInternacaoDAO;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IControleInfeccaoFacade controleInfeccaoFacade;
	
	@EJB
	private IFaturamentoApoioFacade faturamentoApoioFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@EJB
	private IPesquisaInternacaoFacade pesquisaInternacaoFacade;
	
	@EJB
	private InternacaoRN internacaoRN;

	/**
	 * 
	 */
	private static final long serialVersionUID = 9027792392969025816L;

	//private static final String ENTITY_MANAGER = "entityManager";

	/**
	 * Constante que guarda o nome do atributo do contexto referente ao sequence
	 * DAO
	 */
	

	private enum VerificaInternacaoRNExceptionCode implements
			BusinessExceptionCode {
		AIP_00120, AIN_00385, AIN_00395, AIN_00394, AIN_00593, AIN_00595, AIN_00597, AIN_00605, AIN_00607, AIN_00300, 
		AIN_00410, AIN_00814, AIN_00841, AIN_00848, AIN_00849, AIN_00802, AIN_00315, AIN_00599, AIN_00600, AIN_00601, 
		AIN_00603, AIN_00839, AIN_00501, AIN_00342, AIN_00399, AIN_00526, AIN_00589, AIN_00587, AIN_00588, AIN_00728, 
		AIN_00729, AIN_00591, AIN_00680, AIN_00683, RN_CTHC_VER_DATAS, SUBST_PRONT_2_INTERNACOES_VIGENTES;
	}

	/**
	 * Método para validar data de óbito do paciente.
	 * 
	 * ORADB: Procedure AINK_INT_VER.RN_INT_VER_OBITO_PAC
	 * 
	 * @param codigoPaciente
	 * @param dataInternacao
	 * @param operacao
	 * @throws ApplicationBusinessException
	 */
	public void verificarObitoPaciente(Integer codigoPaciente,
			Date dataInternacao, DominioOperacoesJournal operacao)
			throws ApplicationBusinessException {

		if (codigoPaciente == null || dataInternacao == null) {
			return;
		}

		boolean subirExcecao = false;
		AipPacientes paciente = this.getPacienteFacade().obterPaciente(codigoPaciente);

		if (paciente != null && (paciente.getDtObito() != null
					|| paciente.getTipoDataObito() != null)) {
			Calendar data = Calendar.getInstance();
			data.setTime(dataInternacao);
			data = DateUtils.truncate(data, Calendar.DAY_OF_MONTH);

			// Condição 1 (trunc(pac.dt_obito) < trunc(c_data)
			if (paciente.getDtObito() != null) {
				Calendar dataObito = Calendar.getInstance();
				dataObito.setTime(paciente.getDtObito());

				// Trunca datasObito para comparações
				dataObito = DateUtils.truncate(dataObito,
						Calendar.DAY_OF_MONTH);
				if (dataObito.before(data)) {
					subirExcecao = true;
				}
			}

			// Condição 2 (pac.dt_obito is null and pac.tipo_data_obito =
			// 'IGN' and c_operacao='I')
			if (paciente.getDtObito() == null
					&& DominioTipoDataObito.IGN.equals(paciente
							.getTipoDataObito())
					&& DominioOperacoesJournal.INS.equals(operacao)) {
				subirExcecao = true;
			}

			// Condição 3 (dataObito é nula - usa dataObitoExterno)
			if (paciente.getDtObito() == null
					&& paciente.getDtObitoExterno() != null) {
				Calendar dataObitoExterno = Calendar.getInstance();

				if (DominioTipoDataObito.DMA.equals(paciente
						.getTipoDataObito())) {
					dataObitoExterno = DateUtils.truncate(dataObitoExterno,
							Calendar.DAY_OF_MONTH);
				} else if (DominioTipoDataObito.MES.equals(paciente
						.getTipoDataObito())) {
					data = DateUtils.truncate(data, Calendar.MONTH);
					dataObitoExterno = DateUtils.truncate(dataObitoExterno,
							Calendar.MONTH);
				} else if (DominioTipoDataObito.ANO.equals(paciente
						.getTipoDataObito())) {
					data = DateUtils.truncate(data, Calendar.YEAR);
					dataObitoExterno = DateUtils.truncate(dataObitoExterno,
							Calendar.YEAR);
				}

				if (dataObitoExterno.before(data)) {
					subirExcecao = true;
				}
			}
		}

		if (subirExcecao) {
			throw new ApplicationBusinessException(
					VerificaInternacaoRNExceptionCode.AIP_00120);
		}
	}

	/**
	 * Método para validar se o tipo de alta do paciente é igual ao parametro
	 * P_COD_TIPO_ALTA_OBITO. Caso seja, é levantada uma exception.
	 * 
	 * ORADB: Procedure AINK_INT_VER.VERIFICA_ALTA
	 * 
	 * @param tamCodigo
	 * @param dataSaidaPaciente
	 * @throws ApplicationBusinessException
	 */
	public void verificarAlta(String tamCodigo, Date dataSaidaPaciente)
			throws ApplicationBusinessException {
		AghParametros parametro = this.getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_COD_TIPO_ALTA_OBITO);

		if (parametro != null && parametro.getVlrTexto().equals(tamCodigo)
				&& dataSaidaPaciente == null) {
			throw new ApplicationBusinessException(
					VerificaInternacaoRNExceptionCode.AIN_00385);
		}
	}

	/**
	 * Método para validar se data de saída do paciente é menor que a data
	 * atual.
	 * 
	 * ORADB: Procedure AINK_INT_VER.VERIFICA_DATA_SAIDA_PACIENTE
	 * 
	 * @param dataSaidaPaciente
	 * @throws ApplicationBusinessException
	 */
	public void verificarDataSaidaPaciente(Date dataSaidaPaciente)
			throws ApplicationBusinessException {

		if (dataSaidaPaciente != null && dataSaidaPaciente.after(new Date())) {
			throw new ApplicationBusinessException(
					VerificaInternacaoRNExceptionCode.AIN_00395);
		}
	}

	/**
	 * Método para validar a data de alta do paciente da internação.
	 * 
	 * ORADB: Procedure AINK_INT_VER.VERIFICA_DATA_ALTA (na procedure do BD são
	 * recebidos 2 parâmetros, porém como um deles não era utilizado, foi
	 * migrado para o Java somente com o recebimento do parâmetro usado na
	 * implementação)
	 * 
	 * @param dataAltaMedica
	 * @throws ApplicationBusinessException
	 */
	public void verificarDataAlta(Date dataAltaMedica)
			throws ApplicationBusinessException {

		if (dataAltaMedica != null && dataAltaMedica.after(new Date())) {
			throw new ApplicationBusinessException(VerificaInternacaoRNExceptionCode.AIN_00394);
		}
	}

	/**
	 * Método para validar se o leito está ativo.
	 * 
	 * ORADB: Procedure AINK_INT_VER.VERIFICA_LEITO_ATIVO
	 * 
	 * @param leitoId
	 * @throws ApplicationBusinessException
	 */
	public void verificarLeitoAtivo(String leitoId) throws ApplicationBusinessException {
		if (leitoId != null) {
			AinLeitos leito = this.getCadastrosBasicosInternacaoFacade().obterLeitoPorId(leitoId);
	
			// Se o leito for nulo ou não tiver situação = 'A', sobe exception
			if (leito == null || !DominioSituacao.A.equals(leito.getIndSituacao())) {
				throw new ApplicationBusinessException(
						VerificaInternacaoRNExceptionCode.AIN_00593);
			}
		}
	}
	
	public void verificarLeito(String leitoId) throws ApplicationBusinessException {
		if (leitoId != null) {
			this.getCadastrosBasicosInternacaoFacade().verificarInternar(leitoId);
		}
	}

	/**
	 * Método para validar se o quarto está ativo.
	 * 
	 * ORADB: Procedure AINK_INT_VER.VERIFICA_QUARTO_ATIVO
	 * 
	 * @param numeroQuarto
	 * @throws ApplicationBusinessException
	 */
	public void verificarQuartoAtivo(Short numeroQuarto)
			throws ApplicationBusinessException {
		if(numeroQuarto != null) {
			AinQuartos quarto = this.getCadastrosBasicosInternacaoFacade().obterQuarto(numeroQuarto);
	
			if (quarto == null
					|| quarto.getUnidadeFuncional() == null
					|| !DominioSituacao.A.equals(quarto.getUnidadeFuncional()
							.getIndSitUnidFunc())) {
				throw new ApplicationBusinessException(
						VerificaInternacaoRNExceptionCode.AIN_00595);
			}
		}
	}

	/**
	 * Método para validar se a unidade funcional está ativa.
	 * 
	 * ORADB: AINK_INT_VER.VERIFICA_UNID_FUNC_ATIVA
	 * 
	 * @param seqUnidadeFuncional
	 * @throws ApplicationBusinessException
	 */
	public void verificarUnidadeFuncionalAtivo(Short seqUnidadeFuncional)
			throws ApplicationBusinessException {

		if(seqUnidadeFuncional != null) {
			AghUnidadesFuncionais unidadeFuncional = this.aghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(seqUnidadeFuncional);
	
			if (unidadeFuncional == null
					|| !DominioSituacao.A.equals(unidadeFuncional
							.getIndSitUnidFunc())) {
				throw new ApplicationBusinessException(
						VerificaInternacaoRNExceptionCode.AIN_00597);
			}
		}
	}

	/**
	 * Método para validar se a idade do paciente está dentro da faixa de idade
	 * permitida para a internação.
	 * 
	 * ORADB: Procedure AINK_INT_VER.VERIFICA_FAIXA_IDADE
	 * 
	 * @param seqEspecialidade
	 * @param codigoPaciente
	 * @param dataInternacao
	 * @throws ApplicationBusinessException
	 */
	public void verificarFaixaIdade(Short seqEspecialidade,
			Integer codigoPaciente, Date dataInternacao)
			throws ApplicationBusinessException {

		AipPacientes paciente = this.getPacienteFacade().obterPaciente(codigoPaciente);
		AghEspecialidades especialidade = this.getCadastrosBasicosInternacaoFacade()
				.obterEspecialidade(seqEspecialidade);

		if (paciente != null && especialidade != null && dataInternacao != null) {
			Long timeMilliseconds = dataInternacao.getTime()
					- paciente.getDtNascimento().getTime();
			Long anos = timeMilliseconds / 1000 / 60 / 60 / 24 / 365;

			if (anos.shortValue() > especialidade.getIdadeMaxPacInternacao()
					|| anos.shortValue() < especialidade
							.getIdadeMinPacInternacao()) {
				throw new ApplicationBusinessException(
						VerificaInternacaoRNExceptionCode.AIN_00605);
			}
		} else {
			throw new ApplicationBusinessException(
					VerificaInternacaoRNExceptionCode.AIN_00605);
		}
	}

	/**
	 * Método para validar se o convênio da internação permite internar o
	 * paciente.
	 * 
	 * ORADB: Procedure AINK_INT_VER.VERIFICA_PERMISSAO_CONVENIO
	 * 
	 * @param codigoConvenio
	 * @throws ApplicationBusinessException
	 */
	public void verificarPermissaoConvenio(Short codigoConvenio)
			throws ApplicationBusinessException {

		FatConvenioSaude convenioSaude = this.getFaturamentoApoioFacade()
				.obterConvenioSaude(codigoConvenio);

		if (convenioSaude == null || !convenioSaude.getPermissaoInternacao()) {
			throw new ApplicationBusinessException(
					VerificaInternacaoRNExceptionCode.AIN_00607);
		}
	}

	/**
	 * Método para validar se o período que o paciente está internado está
	 * dentro do período estipulado no parâmetro 'P_QTD_DIAS_VOLTA_INT'.
	 * 
	 * ORADB: Procedure AINK_INT_VER.VERIFICA_DATA_INT_DIAS_VOLTA
	 * 
	 * @param dataInternacao
	 * @throws ApplicationBusinessException
	 */
	public void verificarDataInternacaoDiasVolta(Date dataInternacao)
			throws ApplicationBusinessException {

		if (dataInternacao != null) {
			AghParametros parametro = this.getParametroFacade()
					.buscarAghParametro(AghuParametrosEnum.P_QTD_DIAS_VOLTA_INT);

			Long dias = parametro.getVlrNumerico().longValue();
			Long millisegundosDias = dias * 1000 * 60 * 60 * 24;

			Calendar dataAtual = Calendar.getInstance();
			dataAtual = DateUtils.truncate(dataAtual, Calendar.DAY_OF_MONTH);

			Calendar dataLimiteInternacao = Calendar.getInstance();
			dataLimiteInternacao.setTimeInMillis(dataLimiteInternacao
					.getTimeInMillis()
					- millisegundosDias);

			Calendar dataInt = Calendar.getInstance();
			dataInt.setTime(dataInternacao);
			dataInt = DateUtils.truncate(dataInt, Calendar.DAY_OF_MONTH);

			if (dataInt.before(dataLimiteInternacao)
					|| dataInt.compareTo(dataLimiteInternacao) == 0
					|| dataInt.after(dataAtual)
					) {
				throw new ApplicationBusinessException(
						VerificaInternacaoRNExceptionCode.AIN_00300);
			}
		}
	}

	/**
	 * Método para validar intervalo de datas em internações existentes do
	 * paciente que não seja a recebida por parâmetro.
	 * 
	 * ORADB: Procedure AINK_INT_VER.RN_INT_VER_SOBRE_IN2
	 * 
	 * @param seqInternacao
	 * @param codigoPaciente
	 * @param datainternacao
	 * @param dataAltaMedica
	 */
	public void verificarIntervaloValidoInternacao(Integer seqInternacao,
			Integer codigoPaciente, Date dataInternacao, Date dataAltaMedica)
			throws ApplicationBusinessException {

		if (codigoPaciente != null && dataInternacao != null
				) {
			List<AinInternacao> internacaoList = this.getPesquisaInternacaoFacade()
					.pesquisarInternacaoPorPaciente(codigoPaciente);

			// Percorre lista de internação para fazer validações
			boolean subirException = false;
			for (AinInternacao internacao : internacaoList) {
				subirException = this.verificarIntervaloInternacao(
						seqInternacao, internacao, dataInternacao,
						dataAltaMedica);
				if (subirException) {
					throw new ApplicationBusinessException(
							VerificaInternacaoRNExceptionCode.AIN_00410);
				}
			}
		}
	}

	/**
	 * Método para fazer validações referentes a data dew internação e data de
	 * alta médica em um determinado intervalo. Esse método faz as validações
	 * que a query da procedure AINK_INT_VER.RN_INT_VER_SOBRE_IN2 tem nas suas
	 * restrições.
	 * 
	 * @param seqInternacao
	 * @param internacao
	 * @param dataInternacao
	 * @param dataAltaMedica
	 * @return
	 */
	public boolean verificarIntervaloInternacao(Integer seqInternacao,
			AinInternacao internacao, Date dataInternacao, Date dataAltaMedica) {

		if (!seqInternacao.equals(internacao.getSeq())) {

			Date dataInternacaoBD = internacao.getDthrInternacao();
			Date dataAltaMedicaBD = internacao.getDthrAltaMedica();

			if (dataAltaMedicaBD == null
					&& (dataInternacaoBD.before(dataInternacao) 
							|| (dataAltaMedica != null && dataInternacaoBD.before(dataAltaMedica)))
				) {
				return true;
			}

			if (dataInternacaoBD != null
					&& dataAltaMedicaBD != null
					&& dataInternacao != null
					&& dataAltaMedica != null
					&& (CoreUtil.isBetweenDatas(dataInternacaoBD, dataInternacao, dataAltaMedica)
							|| CoreUtil.isBetweenDatas(dataAltaMedicaBD, dataInternacao, dataAltaMedica) || (CoreUtil
							.isMenorOuIgualDatas(dataAltaMedicaBD, dataInternacao) && CoreUtil.isMaiorOuIgualDatas(dataAltaMedicaBD,
							dataAltaMedica)))) {
				return true;
			}

			if (dataAltaMedica == null
					&& (CoreUtil.isMenorOuIgualDatas(dataInternacaoBD, dataInternacao) 
							&& CoreUtil.isMaiorOuIgualDatas(dataAltaMedicaBD, dataInternacao))
				) {
				return true;
			}

			if (dataAltaMedica == null && dataAltaMedicaBD == null) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Método para validar se o quarto do paciente precisa solicitação de medida
	 * preventiva de infecção.
	 * 
	 * ORADB: Procedure AINK_INT_VER.RN_INTP_VER_MED_PREV
	 * 
	 * @param leitoId
	 * @param codigoPaciente
	 * @throws ApplicationBusinessException
	 */
	public void verificarMedidaPreventiva(String leitoId, Integer codigoPaciente)
			throws ApplicationBusinessException {		
		/*
		  Retirando validação enquanto o modulo de CCIH não está disponivel do AGHU
		  if(leitoId != null) {
			AinLeitos leito = this.getCadastrosBasicosInternacaoFacade().obterLeitoPorId(leitoId);
	
			// Se o leito tem indicador de exclusividade de controle de infecção e o paciente não possuir medida preventiva então AIN_00814
			if (leito != null && leito.getQuarto() != null && this.getControleInfeccaoFacade().verificaLeitoExclusivoControleInfeccao(leito)) {
				if(!this.getControleInfeccaoFacade().verificarNecessidadeMedidaPreventivaInfeccao(codigoPaciente)) {
					throw new ApplicationBusinessException(VerificaInternacaoRNExceptionCode.AIN_00814);
				}
			}
		}*/
	}

	/**
	 * Método para validar se o prontuário do paciente é menor que VALOR_MAXIMO_PRONTUARIO.
	 * 
	 * ORADB: Procedure AINK_INT_VER.RN_INTP_VER_RN_SPRNT
	 * 
	 * @param codigoPaciente
	 */
	public void verificarProntuarioPaciente(Integer codigoPaciente)
			throws ApplicationBusinessException {

		AipPacientes paciente = this.getPacienteFacade().obterPaciente(codigoPaciente);

		if (paciente != null && paciente.getProntuario() > VALOR_MAXIMO_PRONTUARIO) {
			throw new ApplicationBusinessException(
					VerificaInternacaoRNExceptionCode.AIN_00841);
		}
	}

	/**
	 * Método para verificar se o convênio do paciente é igual ao parêmtro
	 * 'P_COD_CONV_75' após sua alta.
	 * 
	 * ORADB: Procedure AINK_INT_VER.RN_INT_VER_CONV_75
	 * 
	 * @param codigoConvenioSaude
	 * @param dataAlta
	 * @param motivo
	 * @throws ApplicationBusinessException
	 */
	public void verificarConvenioAposAlta(Short codigoConvenioSaude,
			Date dataAlta, String motivo) throws ApplicationBusinessException {
		final String textoMotivo = "CNV";

		AghParametros parametro = this.getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_COD_CONV_75);

		if (parametro != null
				&& codigoConvenioSaude.equals(parametro.getVlrNumerico())
				&& dataAlta == null) {
			if (textoMotivo.equalsIgnoreCase(motivo)) {
				throw new ApplicationBusinessException(
						VerificaInternacaoRNExceptionCode.AIN_00848);
			} else {
				throw new ApplicationBusinessException(
						VerificaInternacaoRNExceptionCode.AIN_00849);
			}
		}
	}

	/**
	 * Método para verificar se existe prontuário para que o paciente seja
	 * internado.
	 * 
	 * ORADB: Procedure AINK_INT_VER.RN_INT_VER_PRONT
	 * 
	 * @param codigoPaciente
	 * @throws ApplicationBusinessException
	 */
	public void verificarProntuario(Integer codigoPaciente)
			throws ApplicationBusinessException {

		this.getInternacaoRN()
				.verificarExistenciaProntuarioParaInternarPaciente(codigoPaciente);
	}

	/**
	 * Método para verificar se existem lançamentos que fiquem fora do período
	 * de internação.
	 * 
	 * ORADB: Procedure AINK_INT_VER.VERIFICA_CONTA_PAC
	 * 
	 * @param seqInternacao
	 * @param dataAtualizacao
	 * @param dataAnterior
	 * @param tipo
	 * @param dataLimite
	 * @param seqPhi
	 * @return
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public VerificarContaPacienteVO verificarContaPaciente(
			final Integer seqInternacao, final Date dataAtualizacao,
			final Date dataAnterior, final String tipoAltaMedica,
			final Date dataLimite, final Integer seqPhi)
			throws ApplicationBusinessException {

		if (!ainInternacaoDAO.isOracle()) {
			return null;
		}
		
		// Usado MutableBoolean, pois dentro do excopo da subclasse criada para
		// a chamada da procedure não havia como atribuir um valor a uma
		// variável boolean ou Boolen, pois são imutáveis.
		final MutableBoolean retorno = new MutableBoolean(false);
		final VerificarContaPacienteVO vo = new VerificarContaPacienteVO();
		
		final Date dNova = dataAtualizacao == null ? null : new java.sql.Timestamp(dataAtualizacao.getTime());
		final Date dAnterior = dataAnterior == null ? null : new java.sql.Timestamp(dataAnterior.getTime());
		
		final String nomeObjeto = EsquemasOracleEnum.AGH + "." + ObjetosBancoOracleEnum.AGHU_RN_CTHC_VER_DATAS;
		
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AghWork work = new AghWork(servidorLogado.getUsuario()) {
			public void executeAghProcedure(Connection connection) throws SQLException {
				
				CallableStatement cs = null;
				try {
					StringBuilder sbCall = new StringBuilder("{? = call ");
					sbCall.append(nomeObjeto).append("(?,?,?,?,?,?)}");

					cs = connection.prepareCall(sbCall.toString());

					CoreUtil.configurarParametroCallableStatement(cs, 2, Types.INTEGER, seqInternacao);
					CoreUtil.configurarParametroCallableStatement(cs, 3, Types.TIMESTAMP, dNova);
					CoreUtil.configurarParametroCallableStatement(cs, 4, Types.TIMESTAMP, dAnterior);
					CoreUtil.configurarParametroCallableStatement(cs, 5, Types.VARCHAR, tipoAltaMedica);

					// Registra parâmetros OUT
					cs.registerOutParameter(1, Types.INTEGER);
					cs.registerOutParameter(6, Types.DATE);
					cs.registerOutParameter(7, Types.INTEGER);

					cs.execute();

					// Obtém valores dos parâmetros OUT
					if (cs.getInt(1)== 0) {
						retorno.setValue(true);
					} else {
						retorno.setValue(false);
					}

					Date dataLimite = cs.getDate(6);
					Integer seqPhi = cs.getInt(7);

					vo.setDataLimite(dataLimite);
					vo.setSeqPhi(seqPhi);

				} finally {
					if (cs != null) {
						cs.close();
					}
				}
			}
		};
		
		
		Exception erro = null;
		
		try {
			ainInternacaoDAO.doWork(work);
		} catch (Exception e) {			
			erro = e;
		}
		
		if (work.getException() != null || erro != null){
			
			Exception exception = work.getException() != null ? work.getException() : erro ;
			
			String valores = CoreUtil
					.configurarValoresParametrosCallableStatement(
							seqInternacao, dNova, dAnterior, tipoAltaMedica,
							null, null);
			this.logError(CoreUtil
					.configurarMensagemUsuarioLogCallableStatement(nomeObjeto,
							exception, true, valores));
			throw new ApplicationBusinessException(
					AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD,
					nomeObjeto, valores, CoreUtil
							.configurarMensagemUsuarioLogCallableStatement(
									nomeObjeto, exception, false, valores));
		}
		
		
		if (retorno.equals(Boolean.TRUE)) {
			throw new ApplicationBusinessException(
					VerificaInternacaoRNExceptionCode.AIN_00839);
		}
		return vo;
	}
	
	
	
	/**
	 * Método para verificar se o faturamento trabalha com o faturamento de
	 * internação.
	 * 
	 * ORADB: Procedure AINK_INT_VER.VERIFICA_CONVENIO
	 * 
	 * @param codigoConvenio
	 * @param seqConvenio
	 * @throws ApplicationBusinessException
	 */
	public void verificarConvenio(Short codigoConvenio, Byte seqConvenio)
			throws ApplicationBusinessException {

		FatConvenioSaudePlano convenioSaudePlano = this.getFaturamentoApoioFacade()
				.obterConvenioSaudePlanoParaInternacao(codigoConvenio,
						seqConvenio);

		if (convenioSaudePlano == null) {
			throw new ApplicationBusinessException(
					VerificaInternacaoRNExceptionCode.AIN_00501);
		}
	}

	/**
	 * Método para verificar se profissional está na escala. Essa verificação é
	 * feita somente se a unidade funcional possuirem o indicador de escala.
	 * 
	 * ORADB: Procedure AINK_INT_VER.VERIFICA_ESCALA_PROF
	 * 
	 * @param matriculaProfessor
	 * @param codigoProfessor
	 * @param leitoId
	 * @param numeroQuarto
	 * @param seqUnidadeFuncional
	 * @param seqEspecialidade
	 * @param codigoConvenioSaude
	 * @param dataInternacao
	 * @throws ApplicationBusinessException
	 */
	public void verificarEscalaProfessor(Integer matriculaProfessor,
			Short codigoProfessor, String leitoId, Short numeroQuarto,
			Short seqUnidadeFuncional, Short seqEspecialidade,
			Short codigoConvenioSaude, Date dataInternacao)
			throws ApplicationBusinessException {

		if (leitoId != null && !"".equals(leitoId)) {
			AinLeitos leito = this.getCadastrosBasicosInternacaoFacade().obterLeitoPorId(leitoId);

			if (leito == null
					|| leito.getQuarto().getUnidadeFuncional() == null
					|| leito.getQuarto().getUnidadeFuncional().getSeq() == null) {
				throw new ApplicationBusinessException(
						VerificaInternacaoRNExceptionCode.AIN_00728);
			}
		} else if (numeroQuarto != null) {
			AinQuartos quarto = this.getCadastrosBasicosInternacaoFacade().obterQuarto(numeroQuarto);

			if (quarto == null || quarto.getUnidadeFuncional() == null
					|| quarto.getUnidadeFuncional().getSeq() == null) {
				throw new ApplicationBusinessException(
						VerificaInternacaoRNExceptionCode.AIN_00729);
			}
		}

		FatConvenioSaude convenioSaude = this.getFaturamentoApoioFacade()
				.obterConvenioSaude(codigoConvenioSaude);
		AghUnidadesFuncionais unidadeFuncional = null;
		if(seqUnidadeFuncional!=null){
			unidadeFuncional = this.aghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(seqUnidadeFuncional);
		}
	
		if (convenioSaude != null
				&& convenioSaude.getVerificaEscalaProfInt()
				&& unidadeFuncional != null
				&& DominioSimNao.S.equals(unidadeFuncional
						.getIndVerfEscalaProfInt())) {

			List<AinEscalasProfissionalInt> escalaProfissionalIntList = this.getPesquisaInternacaoFacade()
					.pesquisarEscalaProfissionalInt(matriculaProfessor,
							codigoProfessor, seqEspecialidade,
							codigoConvenioSaude);

			// Iteração para aplicar restrição de "dataInicio <= dataInternacao
			// and nvl(dtFim, sysdate) >= dataInternacao"
			List<AinEscalasProfissionalInt> resultados = new ArrayList<AinEscalasProfissionalInt>();
			Date dataFim = null;
			for (AinEscalasProfissionalInt escalasProfissionalInt : escalaProfissionalIntList) {

				if (CoreUtil.isMenorOuIgualDatas(escalasProfissionalInt.getId()
						.getDtInicio(), dataInternacao)) {
					// Código equivalente ao NVL(dtFim, sysdate)
					dataFim = escalasProfissionalInt.getDtFim() == null ? new Date()
							: escalasProfissionalInt.getDtFim();
					if (CoreUtil.isMaiorOuIgualDatas(dataFim, dataInternacao)) {
						resultados.add(escalasProfissionalInt);
					}
				}

			}

			// Se não encontrar registros com a restrição, sobe exceção.
			// Se encontrar MAIS DE UM, significa que o profissional está
			// sobreposto na escala e nenhuma providencia é tomada.
			if (resultados.size() == 0) {
				throw new ApplicationBusinessException(
						VerificaInternacaoRNExceptionCode.AIN_00591);
			}
		}
	}

	/**
	 * Método para verificar item do procedimento hospitalar (se iph_pho_seq e
	 * iph_seq não estão com valor nulo).
	 * 
	 * ORADB: Procedure AINK_INT_VER.VERIFICA_ITEM_PROC_HOSPITALAR
	 * 
	 * @param iphPhoSeq
	 * @param iphSeq
	 * @throws ApplicationBusinessException
	 */
	public void verificarItemProcedimentoHospitalar(Short iphPhoSeq,
			Integer iphSeq) throws ApplicationBusinessException {

		if (iphPhoSeq == null || iphSeq == null) {
			throw new ApplicationBusinessException(
					VerificaInternacaoRNExceptionCode.AIN_00526);
		}
	}

	/**
	 * Método para verificar se sexo e idade do paciente são compatíveis com o
	 * procedimento.
	 * 
	 * ORADB: Procedure AINK_INT_VER.VERIFICA_SEXO_PROCED
	 * 
	 * @param codigoPaciente
	 * @param iphPhoSeq
	 * @param iphSeq
	 * @param dataInternacao
	 * @throws ApplicationBusinessException
	 */
	public void verificarSexoIdadePacienteProcedimento(Integer codigoPaciente,
			Short iphPhoSeq, Integer iphSeq, Date dataInternacao)
			throws ApplicationBusinessException {

		if (codigoPaciente != null && iphPhoSeq != null && iphSeq != null
				&& dataInternacao != null) {

			AipPacientes paciente = this.getPacienteFacade().obterPaciente(codigoPaciente);
			if (paciente != null) {
				FatItensProcedHospitalar itemProcedimentoHospitalar = this.getFaturamentoFacade()
						.obterItemProcedHospitalar(iphPhoSeq, iphSeq);

				if (itemProcedimentoHospitalar != null
						&& itemProcedimentoHospitalar.getSexo().equals(
								DominioSexoDeterminante.Q)) {

					Long idadeMinima = itemProcedimentoHospitalar.getIdadeMin()
							.longValue();
					Long idadeMaxima = itemProcedimentoHospitalar.getIdadeMax()
							.longValue();

					Long timeMilliseconds = (dataInternacao.getTime() - paciente
							.getDtNascimento().getTime());
					Long anos = timeMilliseconds / 1000 / 60 / 60 / 24 / 365;

					if (!(anos >= idadeMinima && anos <= idadeMaxima)) {
						throw new ApplicationBusinessException(
								VerificaInternacaoRNExceptionCode.AIN_00680);
					}
				}
			}
		}
	}

	/**
	 * Método para verificar a situação do cadastro do leito.
	 * 
	 * ORADB: Procedure AINK_INT_VER.VERIFICA_SIT_LEITO
	 * 
	 * @param leitoId
	 * @throws ApplicationBusinessException
	 */
	public void verificarSituacaoLeito(String leitoId)
			throws ApplicationBusinessException {

		if(leitoId != null) {
			AghParametros parametro1 = this.getParametroFacade()
					.buscarAghParametro(AghuParametrosEnum.P_COD_MVTO_LTO_DESOCUPADO);
			if (parametro1 == null || "".equals(parametro1.getVlrNumerico())) {
				throw new ApplicationBusinessException(
						VerificaInternacaoRNExceptionCode.AIN_00342);
			}
	
			AghParametros parametro2 = this.getParametroFacade()
					.buscarAghParametro(AghuParametrosEnum.P_COD_MVTO_LTO_BLOQUEIO_LIMPEZA);
			if (parametro2 == null || "".equals(parametro2.getVlrNumerico())) {
				throw new ApplicationBusinessException(
						VerificaInternacaoRNExceptionCode.AIN_00342);
			}
	
			AghParametros parametro3 = this.getParametroFacade()
					.buscarAghParametro(AghuParametrosEnum.P_COD_MVTO_LTO_RESERVADO);
			if (parametro3 == null || "".equals(parametro3.getVlrNumerico())) {
				throw new ApplicationBusinessException(
						VerificaInternacaoRNExceptionCode.AIN_00342);
			}
	
			AinLeitos leito = this.getCadastrosBasicosInternacaoFacade().obterLeitoPorId(leitoId);
	
			if (leito != null && leito.getTipoMovimentoLeito() != null) {
				// Verifica se tipoMovimentoLeito é igual a pelo menos um dos
				// parametros
				if (!leito.getTipoMovimentoLeito().getCodigo().equals(
						parametro1.getVlrNumerico().shortValue())
						&& !leito.getTipoMovimentoLeito().getCodigo().equals(
								parametro2.getVlrNumerico().shortValue())
						&& !leito.getTipoMovimentoLeito().getCodigo().equals(
								parametro3.getVlrNumerico().shortValue())) {
					throw new ApplicationBusinessException(
							VerificaInternacaoRNExceptionCode.AIN_00399);
				}
			} else {
				throw new ApplicationBusinessException(
						VerificaInternacaoRNExceptionCode.AIN_00399);
			}
		}
	}

	/**
	 * Método para verificar se existe sobreposição entre os períodos de
	 * internação do paciente e atendimento de urgência.
	 * 
	 * ORADB: Procedure AINK_INT_VER.VERIFICA_SOBREPOSICAO_ATU
	 * 
	 * @param seqAtendimentoUrgencia
	 * @param codigoPaciente
	 * @param dataInternacao
	 * @param dataAltaMedica
	 * @throws ApplicationBusinessException
	 */
	public void verificarSobreposicaoPeriodoInternacao(
			Integer seqAtendimentoUrgencia, Integer codigoPaciente,
			Date dataInternacao, Date dataAltaMedica)
			throws ApplicationBusinessException {

		List<AinAtendimentosUrgencia> atendimentoUrgenciaList = this.getAltaPacienteObservacaoON()
				.pesquisarAtendimentoUrgencia(codigoPaciente);
		
		// As restrições implementadas abaixo através de IFs são referentes as
		// restrições da query existente na procedure original
		// (VERIFICA_SOBREPOSICAO_ATU).
		if (atendimentoUrgenciaList.size() > 0) {
			dataAltaMedica = dataAltaMedica == null ? new Date()
					: dataAltaMedica;

			for (AinAtendimentosUrgencia atendimentoUrgencia : atendimentoUrgenciaList) {
				if (!atendimentoUrgencia.getSeq()
						.equals(seqAtendimentoUrgencia)) {

					if (CoreUtil
							.isBetweenDatas(atendimentoUrgencia
									.getDtAtendimento(), dataInternacao,
									dataAltaMedica)) {
						throw new ApplicationBusinessException(
								VerificaInternacaoRNExceptionCode.AIN_00683);
					}

					if (CoreUtil.isBetweenDatas(atendimentoUrgencia
							.getDtAltaAtendimento(), dataInternacao,
							dataAltaMedica)) {
						throw new ApplicationBusinessException(
								VerificaInternacaoRNExceptionCode.AIN_00683);
					}

					if (atendimentoUrgencia.getDtAtendimento().before(
							dataInternacao)) {
						Date dataAltaAtendimento = atendimentoUrgencia
								.getDtAltaAtendimento() == null ? new Date()
								: atendimentoUrgencia.getDtAltaAtendimento();

						if (dataAltaAtendimento.after(dataAltaMedica)) {
							throw new ApplicationBusinessException(
									VerificaInternacaoRNExceptionCode.AIN_00683);
						}
					}
				}
			}
		}
	}

	public ICadastrosBasicosInternacaoFacade getCadastrosBasicosInternacaoFacade() {
		return this.cadastrosBasicosInternacaoFacade;
	}

	protected IControleInfeccaoFacade getControleInfeccaoFacade() {
		return controleInfeccaoFacade;
	}

	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}

	protected IFaturamentoApoioFacade getFaturamentoApoioFacade() {
		return this.faturamentoApoioFacade;
	}

	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}

	protected IPesquisaInternacaoFacade getPesquisaInternacaoFacade() {
		return pesquisaInternacaoFacade;
	}

	protected AltaPacienteObservacaoON getAltaPacienteObservacaoON() {
		return altaPacienteObservacaoON;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected InternacaoRN getInternacaoRN() {
		return internacaoRN;
	}
	
	protected AinInternacaoDAO getAinInternacaoDAO() {
		return ainInternacaoDAO;
	}

	/**
	 * Método que garante que na substituição de prontuário o paciente
	 * destino não fique com duas internações com ind_paciente_internado = 'S'
	 * @param indPacienteInternado
	 * @param codigo
	 * @throws ApplicationBusinessException 
	 */
	public void validarInternacoesSubstituirProntuario(
			Boolean indPacienteOrigemInternado, Integer codigo, Integer intSeq) throws ApplicationBusinessException {

		if (indPacienteOrigemInternado){
			Integer seqInternacaoVigente = getAinInternacaoDAO().obterIntSeqPorPacienteInternado(codigo, intSeq);
			if (seqInternacaoVigente != null){
				throw new ApplicationBusinessException(VerificaInternacaoRNExceptionCode.SUBST_PRONT_2_INTERNACOES_VIGENTES);
			}
		}
		
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
