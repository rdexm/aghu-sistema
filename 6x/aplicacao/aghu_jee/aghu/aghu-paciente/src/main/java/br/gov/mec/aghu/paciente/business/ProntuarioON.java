package br.gov.mec.aghu.paciente.business;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.business.ISchedulerFacade;
import br.gov.mec.aghu.business.bancosangue.IBancoDeSangueFacade;
import br.gov.mec.aghu.business.scheduler.JobEnum;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.solicitacaocompra.business.ISolicitacaoComprasFacade;
import br.gov.mec.aghu.controleinfeccao.business.IControleInfeccaoFacade;
import br.gov.mec.aghu.controlepaciente.business.IControlePacienteFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateFormatUtil;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioCondicaoPaciente;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exameselaudos.business.IExamesLaudosFacade;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.solicitacao.business.ISolicitacaoInternacaoFacade;
import br.gov.mec.aghu.model.AacAtendimentoApacs;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacSisPrenatal;
import br.gov.mec.aghu.model.AacSisPrenatalId;
import br.gov.mec.aghu.model.AbsAmostrasPacientes;
import br.gov.mec.aghu.model.AbsAmostrasPacientesId;
import br.gov.mec.aghu.model.AbsDoacoes;
import br.gov.mec.aghu.model.AbsEstoqueComponentes;
import br.gov.mec.aghu.model.AbsMovimentosComponentes;
import br.gov.mec.aghu.model.AbsRegSanguineoPacientes;
import br.gov.mec.aghu.model.AbsSolicitacoesDoacoes;
import br.gov.mec.aghu.model.AbsSolicitacoesDoacoesId;
import br.gov.mec.aghu.model.AbsSolicitacoesPorAmostra;
import br.gov.mec.aghu.model.AelPacAgrpPesqExames;
import br.gov.mec.aghu.model.AelPacAgrpPesqExamesId;
import br.gov.mec.aghu.model.AelPacUnidFuncionais;
import br.gov.mec.aghu.model.AelPacUnidFuncionaisId;
import br.gov.mec.aghu.model.AelProtocoloInternoUnids;
import br.gov.mec.aghu.model.AelProtocoloInternoUnidsId;
import br.gov.mec.aghu.model.AghAtendimentoPacientes;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghAtendimentosPacExtern;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AhdHospitaisDia;
import br.gov.mec.aghu.model.AinAtendimentosUrgencia;
import br.gov.mec.aghu.model.AinExtratoLeitos;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinSolicitacoesInternacao;
import br.gov.mec.aghu.model.AipAcessoPacientes;
import br.gov.mec.aghu.model.AipAlergiaPacientes;
import br.gov.mec.aghu.model.AipAlergiaPacientesId;
import br.gov.mec.aghu.model.AipAlturaPacientes;
import br.gov.mec.aghu.model.AipAlturaPacientesId;
import br.gov.mec.aghu.model.AipEnderecosPacientes;
import br.gov.mec.aghu.model.AipFonemaPacientes;
import br.gov.mec.aghu.model.AipFonemasMaePaciente;
import br.gov.mec.aghu.model.AipGrupoFamiliarPacientes;
import br.gov.mec.aghu.model.AipLogProntOnlines;
import br.gov.mec.aghu.model.AipMovimentacaoProntuarios;
import br.gov.mec.aghu.model.AipPacHcpaXPacUbs;
import br.gov.mec.aghu.model.AipPacHcpaXPacUbsId;
import br.gov.mec.aghu.model.AipPacienteDadoClinicos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.AipPacientesDadosCns;
import br.gov.mec.aghu.model.AipPesoPacientes;
import br.gov.mec.aghu.model.AipPesoPacientesId;
import br.gov.mec.aghu.model.AipProntuarioLiberados;
import br.gov.mec.aghu.model.EcpHorarioControle;
import br.gov.mec.aghu.model.FatCandidatosApacOtorrino;
import br.gov.mec.aghu.model.FatContaApac;
import br.gov.mec.aghu.model.FatDadosContaSemInt;
import br.gov.mec.aghu.model.FatPacienteTratamentos;
import br.gov.mec.aghu.model.FatPacienteTratamentosId;
import br.gov.mec.aghu.model.FatProcedAmbRealizado;
import br.gov.mec.aghu.model.FatResumoApacs;
import br.gov.mec.aghu.model.FatResumoApacsId;
import br.gov.mec.aghu.model.MamLaudoAih;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcFichaAnestesias;
import br.gov.mec.aghu.model.MciMvtoFatorPredisponentes;
import br.gov.mec.aghu.model.MciMvtoInfeccaoTopografias;
import br.gov.mec.aghu.model.MciMvtoMedidaPreventivas;
import br.gov.mec.aghu.model.MciMvtoProcedimentoRiscos;
import br.gov.mec.aghu.model.McoPartos;
import br.gov.mec.aghu.model.McoPartosId;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmParamCalculoPrescricao;
import br.gov.mec.aghu.model.MptParamCalculoPrescricao;
import br.gov.mec.aghu.model.MptTratamentoTerapeutico;
import br.gov.mec.aghu.model.RapDependentes;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.paciente.business.ProntuarioRN.FlagsSubstituiProntuario;
import br.gov.mec.aghu.paciente.business.ProntuarioRN.ValorFlagsSubstituiProntuario;
import br.gov.mec.aghu.paciente.cadastro.business.ICadastroPacienteFacade;
import br.gov.mec.aghu.paciente.dao.AipAcessoPacientesDAO;
import br.gov.mec.aghu.paciente.dao.AipAlergiaPacientesDAO;
import br.gov.mec.aghu.paciente.dao.AipAlturaPacientesDAO;
import br.gov.mec.aghu.paciente.dao.AipEnderecosPacientesDAO;
import br.gov.mec.aghu.paciente.dao.AipFonemaPacientesDAO;
import br.gov.mec.aghu.paciente.dao.AipFonemasMaePacienteDAO;
import br.gov.mec.aghu.paciente.dao.AipGrupoFamiliarPacientesDAO;
import br.gov.mec.aghu.paciente.dao.AipLogProntOnlinesDAO;
import br.gov.mec.aghu.paciente.dao.AipMovimentacaoProntuarioDAO;
import br.gov.mec.aghu.paciente.dao.AipPacHcpaXPacUbsDAO;
import br.gov.mec.aghu.paciente.dao.AipPacienteDadoClinicosDAO;
import br.gov.mec.aghu.paciente.dao.AipPacientesDAO;
import br.gov.mec.aghu.paciente.dao.AipPacientesDadosCnsDAO;
import br.gov.mec.aghu.paciente.dao.AipPesoPacientesDAO;
import br.gov.mec.aghu.paciente.dao.AipProntuarioLiberadosDAO;
import br.gov.mec.aghu.paciente.vo.AghParametrosVO;
import br.gov.mec.aghu.paciente.vo.ProntuarioIdentificadoVO;
import br.gov.mec.aghu.paciente.vo.VerificaEnderecoVO;
import br.gov.mec.aghu.perinatologia.business.IPerinatologiaFacade;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

/**
 * Classe responsável por prover os métodos de negócio genéricos para as
 * operações de prontuário.
 * 
 * @author jbirk
 * 
 */
@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.ExcessiveClassLength", "PMD.CouplingBetweenObjects"})
@Stateless
public class ProntuarioON extends BaseBusiness {

	@EJB
	private ProntuarioRN prontuarioRN;
	
	private static final Log LOG = LogFactory.getLog(ProntuarioON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@EJB
	private MovimentacaoProntuarioRN movimentacaoProntuarioRN;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade;
	
	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@Inject
	private AipAcessoPacientesDAO aipAcessoPacientesDAO;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@Inject
	private AipPacientesDadosCnsDAO aipPacientesDadosCnsDAO;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@Inject
	private AipEnderecosPacientesDAO aipEnderecosPacientesDAO;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private ISolicitacaoInternacaoFacade solicitacaoInternacaoFacade;
	
	@Inject
	private AipPesoPacientesDAO aipPesoPacientesDAO;
	
	@EJB
	private IControleInfeccaoFacade controleInfeccaoFacade;
	
	@Inject
	private AipPacHcpaXPacUbsDAO aipPacHcpaXPacUbsDAO;
	
	@Inject
	private AipPacientesDAO aipPacientesDAO;
	
	@EJB
	private IExamesLaudosFacade examesLaudosFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@Inject
	private AipLogProntOnlinesDAO aipLogProntOnlinesDAO;
	
	@EJB
	private IPerinatologiaFacade perinatologiaFacade;
	
	@EJB
	private ISolicitacaoComprasFacade solicitacaoComprasFacade;
	
	@EJB
	private IBancoDeSangueFacade bancoDeSangueFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private ISchedulerFacade schedulerFacade;
	
	@Inject
	private AipPacienteDadoClinicosDAO aipPacienteDadoClinicosDAO;
	
	@EJB
	private ICadastroPacienteFacade cadastroPacienteFacade;
	
	@Inject
	private AipMovimentacaoProntuarioDAO aipMovimentacaoProntuarioDAO;
	
	@Inject
	private AipAlturaPacientesDAO aipAlturaPacientesDAO;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@EJB
	private IControlePacienteFacade controlePacienteFacade;
	
	@Inject
	private AipAlergiaPacientesDAO aipAlergiaPacientesDAO;
	
	@Inject
	private AipProntuarioLiberadosDAO aipProntuarioLiberadosDAO;
	
	@Inject
	private AipGrupoFamiliarPacientesDAO aipGrupoFamiliarPacientesDAO;

	@Inject
	private AipFonemaPacientesDAO aipFonemaPacientesDAO;

	@Inject
	private AipFonemasMaePacienteDAO aipFonemasMaePacienteDAO;
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4609340256972398816L;

	public enum ProntuarioONExceptionCode implements BusinessExceptionCode {
		// AIP_00097,
		ERROR_CODE_20001, ERRO_INSERT_APIU, ERRO_DELETE_APIU, ERRO_DELETE_APUF, INFORMAR_DATA_INICIAL, AIP_00098,
		SUBS_PRONT_0066, SUBS_PRONT_0067, SUBS_PRONT_0068,
		SUBS_PRONT_0069, FALHA_SUBSTITUIR_PRONTUARIO, DATA_INICIAL_MAIOR_DATA_FINAL, ERRO_DADOS_CLINICOS_DIFERENTES;

		public void throwException() throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this);
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
	 * <h1>Conversão do cursor s1</h1> Realiza a busca de um registro na tabela
	 * ABS_SOLICITACOES_DOACOES utilizando como filtro de pesquisa o código do
	 * paciente. <br/>
	 * <br/>
	 * <h5>Código original</h5> <code>
	 * CURSOR s1(p_pac NUMBER)  IS
	 * SELECT * FROM ABS_SOLICITACOES_DOACOES
	 * WHERE pac_codigo = p_pac
	 * FOR UPDATE OF pac_codigo;
	 * </code>
	 * 
	 * @param pacCodigo
	 *            Código do paciente.
	 * @return Registro encontrado.
	 * @author mtocchetto
	 */
	protected List<AbsSolicitacoesDoacoes> pesquisarAbsSolicitacoesDoacoes(Integer pacCodigo) {
		return this.getBancoDeSangueFacade().pesquisarAbsSolicitacoesDoacoes(pacCodigo);
	}

	/**
	 * <h1>Conversão do cursor c_apa</h1> Realiza a busca de uma lista de
	 * registros na tabela ABS_AMOSTRAS_PACIENTES utilizando como filtro de
	 * pesquisa o código do paciente. <br/>
	 * <br/>
	 * <h5>Código original</h5> <code>
	 * -- cursor abs_amostra_pacientes
	 * CURSOR  c_apa(c_codigo_origem ABS_AMOSTRAS_PACIENTES. PAC_CODIGO%TYPE) IS
	 * 	SELECT        PAC_CODIGO,
	 * 		 DTHR_AMOSTRA ,
	 * 		 NRO_AMOSTRA,
	 * 		 OBSERVACAO ,
	 * 		 SER_VIN_CODIGO,
	 * 		 SER_MATRICULA,
	 * 		 IND_SITUACAO,
	 * 		 CRIADO_EM,
	 * 		 ALTERADO_EM,
	 * 		 NRO_SOLICITACOES_ATENDIDAS
	 * 	FROM abs_amostras_pacientes
	 * 	WHERE pac_codigo = c_codigo_origem;
	 * </code>
	 * 
	 * @param pacCodigo
	 *            Código do paciente.
	 * @return Lista de registros encontrados.
	 * @author mtocchetto
	 */
	protected List<AbsAmostrasPacientes> pesquisarAmostrasPaciente(
			Integer pacCodigo) {
		return this.getBancoDeSangueFacade().pesquisarAmostrasPaciente(pacCodigo);
	}

	/**
	 * <h1>Conversão do cursor c_spa</h1> Realiza a busca de uma lista de
	 * registros na tabela ABS_SOLICITACOES_POR_AMOSTRA utilizando como filtro
	 * de pesquisa o código do paciente. <br/>
	 * <br/>
	 * <h5>Código original</h5> <code>
	 * -- cursor abs_solicitacoes_por_amostra_
	 * CURSOR  c_spa(c_codigo_origem ABS_SOLICITACOES_POR_AMOSTRA.APA_PAC_CODIGO%TYPE) IS
	 * SELECT  APA_PAC_CODIGO,
	 *           APA_DTHR_AMOSTRA,
	 *           SHE_ATD_SEQ,
	 *           SHE_SEQ,
	 *           SER_MATRICULA,
	 *           CRIADO_EM,
	 *           SER_VIN_CODIGO,
	 *           IND_SITUACAO,
	 *           SEQ
	 * FROM abs_solicitacoes_por_amostra
	 * WHERE apa_pac_codigo = c_codigo_origem;
	 * </code>
	 * 
	 * @param pacCodigo
	 *            Código do paciente.
	 * @return Lista de registros encontrados.
	 * @author mtocchetto
	 **/
	protected List<AbsSolicitacoesPorAmostra> pesquisarAbsSolicitacoesPorAmostra(
			Integer pacCodigo) {
		return this.getBancoDeSangueFacade().pesquisarAbsSolicitacoesPorAmostra(pacCodigo);
	}

	/**
	 * <h1>Conversão do cursor c_pcr_peso</h1> Realiza a busca de uma lista de
	 * registros na tabela MPT_PARAM_CALCULO_PRESCRICOES utilizando como filtro
	 * de pesquisa o código do paciente vinculado a tabela AIP_PESO_PACIENTES. <br/>
	 * <br/>
	 * <h1>Conversão do cursor c_pcr_altura</h1> Realiza a busca de uma lista de
	 * registros na tabela MPT_PARAM_CALCULO_PRESCRICOES utilizando como filtro
	 * de pesquisa o código do paciente vinculado a tabela AIP_ALTURA_PACIENTES. <br/>
	 * <br/>
	 * 
	 * @param pesoPacCodigo
	 *            Código do paciente vinculado a tabela AIP_PESO_PACIENTES.
	 * @param alturaPacCodigo
	 *            Código do paciente vinculado a tabela AIP_ALTURA_PACIENTES.
	 * @return Lista de registros encontrados.
	 * @author mtocchetto
	 **/
	protected List<MptParamCalculoPrescricao> pesquisarMptParamCalculoPrescricoes(
			Integer pesoPacCodigo, Integer alturaPacCodigo) {
		return this.getProcedimentoTerapeuticoFacade().pesquisarMptParamCalculoPrescricoes(pesoPacCodigo, alturaPacCodigo);
	}

	/**
	 * <h1>Conversão do cursor c_pca_peso</h1> Realiza a busca de uma lista de
	 * registros na tabela MPM_PARAM_CALCULO_PRESCRICOES utilizando como filtro
	 * de pesquisa o código do paciente vinculado a tabela AIP_PESO_PACIENTES. <br/>
	 * <br/>
	 * <h1>Conversão do cursor c_pca_altura</h1> Realiza a busca de uma lista de
	 * registros na tabela MPM_PARAM_CALCULO_PRESCRICOES utilizando como filtro
	 * de pesquisa o código do paciente vinculado a tabela AIP_ALTURA_PACIENTES. <br/>
	 * <br/>
	 * 
	 * @param pesoPacCodigo
	 *            Código do paciente vinculado a tabela AIP_PESO_PACIENTES.
	 * @param alturaPacCodigo
	 *            Código do paciente vinculado a tabela AIP_ALTURA_PACIENTES.
	 * @return Lista de registros encontrados.
	 * @author mtocchetto
	 */
	protected List<MpmParamCalculoPrescricao> pesquisarMpmParamCalculoPrescricoes(Integer pesoPacCodigo, Integer alturaPacCodigo) {
		return this.getPrescricaoMedicaFacade().pesquisarMpmParamCalculoPrescricoes(pesoPacCodigo, alturaPacCodigo);
	}

	/**
	 * <h1>Conversão do cursor c_protocolo</h1> Realiza a busca de uma lista de
	 * registros na tabela AEL_PROTOCOLO_INTERNO_UNIDS utilizando como filtro de
	 * pesquisa o código do paciente vinculado a tabela AIP_PACIENTES. <br/>
	 * <br/>
	 * <h5>Código original</h5> <code>
	 * CURSOR c_protocolo(c_codigo_origem NUMBER) IS
	 * SELECT
	 *      UNF_SEQ,
	 *      SER_MATRICULA,
	 *      SER_VIN_CODIGO,
	 *      NRO_PROTOCOLO,
	 *      CRIADO_EM
	 * FROM
	 *      ael_protocolo_interno_unids
	 * WHERE
	 *      pac_codigo  = c_codigo_origem;
	 * </code>
	 * 
	 * @param pacCodigo
	 *            Código do paciente.
	 * @return Lista de registros encontrados.
	 * @author mtocchetto
	 **/
	protected List<AelProtocoloInternoUnids> pesquisarAelProtocoloInternoUnids(Integer pacCodigo) {
		return this.getExamesLaudosFacade().pesquisarAelProtocoloInternoUnids(pacCodigo);
	}

	/**
	 * <h1>Conversão do cursor c_pac_unid_func</h1> Realiza a busca de uma lista
	 * de registros na tabela AEL_PAC_UNID_FUNCIONAIS utilizando como filtro de
	 * pesquisa o código do paciente vinculado a tabela
	 * AEL_PROTOCOLO_INTERNO_UNIDS. <br/>
	 * <br/>
	 * <h5>Código original</h5> <code>
	 * CURSOR c_pac_unid_func(c_codigo_origem NUMBER) IS
	 * SELECT
	 *       PIU_UNF_SEQ
	 *       ,SEQP
	 *       ,UFE_EMA_EXA_SIGLA
	 *       ,UFE_EMA_MAN_SEQ
	 *       ,UFE_UNF_SEQ
	 *       ,CRIADO_EM
	 *       ,SER_MATRICULA
	 *       ,SER_VIN_CODIGO
	 *       ,ISE_SOE_SEQ
	 *       ,ISE_SEQP
	 *       ,DT_EXECUCAO
	 *       ,IDENTIFICADOR_COMPLEMENTAR
	 *       ,CONDICAO_PAC
	 *       ,NRO_FILME
	 *       ,OBSERVACAO
	 *       ,SER_MATRICULA_ALTERADO
	 *       ,SER_VIN_CODIGO_ALTERADO
	 *       ,ALTERADO_EM
	 *       ,EQU_SEQ
	 * FROM
	 *      ael_pac_unid_funcionais
	 * WHERE
	 *      piu_pac_codigo = c_codigo_origem;
	 * </code>
	 * 
	 * @param pacCodigo
	 *            Código do paciente.
	 * @return Lista de registros encontrados.
	 * @author mtocchetto
	 **/
	protected List<AelPacUnidFuncionais> pesquisarAelPacUnidFuncionais(
			Integer pacCodigo) {
		return this.getExamesLaudosFacade().pesquisarAelPacUnidFuncionais(pacCodigo);
	}

	@SuppressWarnings({"PMD.ExcessiveMethodLength", "ucd"})
	protected void procedureInsDelPiuPuf(Integer codigoOrigem,
			Integer codigoDestino, RapServidores servidorLogado) throws ApplicationBusinessException {
		IExamesLaudosFacade examesLaudosFacade = this.getExamesLaudosFacade();
		
		// Carrega as listas dos cursores antes de realizar alterações no banco
		List<AelProtocoloInternoUnids> listaCProtocolo = pesquisarAelProtocoloInternoUnids(codigoOrigem);
		List<AelPacUnidFuncionais> listaCPacUnidFunc = pesquisarAelPacUnidFuncionais(codigoOrigem);

		// Deleta o filho antes de deletar o pai
		try {
			List<AelPacUnidFuncionais> lista = examesLaudosFacade.pesquisarAelPacUnidFuncionais(codigoOrigem);
			for (AelPacUnidFuncionais aelPacUnidFuncionais : lista) {
				examesLaudosFacade.excluirAelPacienteUnidadeFuncional(aelPacUnidFuncionais);
			}
			this.flush();
		} catch (Exception e) {
			ProntuarioONExceptionCode.ERRO_DELETE_APUF.throwException(e);
			// raise_application_error(-20007, 'erro='||sqlerrm);
		}

		// Deleta o pai
		try {
			List<AelProtocoloInternoUnids> lista = examesLaudosFacade.pesquisarAelProtocoloInternoUnids(codigoOrigem);
			for (AelProtocoloInternoUnids aelProtocoloInternoUnids : lista) {
				examesLaudosFacade.removerAelProtocoloInternoUnids(aelProtocoloInternoUnids, false);
			}
			this.flush();
		} catch (Exception e) {
			ProntuarioONExceptionCode.ERRO_DELETE_APIU.throwException(e);
			// raise_application_error(-20006, 'erro='||sqlerrm);
		}

		// Insert nas tabelas com pac_codigo_destino
		for (AelProtocoloInternoUnids cProtocolo : listaCProtocolo) {
			try {
				AghUnidadesFuncionais unidadeFuncional = cProtocolo.getId()
						.getUnidadeFuncional();
				RapServidores servidor = cProtocolo.getServidor();
				Integer nroProtocolo = cProtocolo.getNroProtocolo();
				Date criadoEm = cProtocolo.getCriadoEm();

				AelProtocoloInternoUnidsId id = new AelProtocoloInternoUnidsId(
						codigoDestino, unidadeFuncional);
				AelProtocoloInternoUnids aelProtocoloInternoUnids = new AelProtocoloInternoUnids();
				aelProtocoloInternoUnids.setId(id);

				aelProtocoloInternoUnids.setServidor(servidor);

				aelProtocoloInternoUnids.setNroProtocolo(nroProtocolo);
				// TODO Tocchetto Conferir se o certo é usar V_CRIADO ou
				// V_CRIADO_EM.
				// aelProtocoloInternoUnids.setCriadoEm(V_CRIADO_EM);
				aelProtocoloInternoUnids.setCriadoEm(criadoEm);

				examesLaudosFacade.inserirAelProtocoloInternoUnids(aelProtocoloInternoUnids);
				this.flush();
			} catch (Exception e) {
				boolean uniqueException = false;
				Throwable cause = ExceptionUtils.getCause(e);
				if (cause instanceof ConstraintViolationException) {
					if (StringUtils.containsIgnoreCase(
							((ConstraintViolationException) cause)
									.getConstraintName(), "AEL_PIU_UK1")) {
						uniqueException = true;
					}
				}
				if (!uniqueException) {
					ProntuarioONExceptionCode.ERRO_INSERT_APIU
							.throwException(e);
				}
			}
		}

		Integer seqp = examesLaudosFacade.listarMaxNumeroControleExamePaciente(codigoDestino);
		if (seqp == null) {
			seqp = 0;
		}

		for (AelPacUnidFuncionais cPacUnidFunc : listaCPacUnidFunc) {
			AghUnidadesFuncionais unidadeFuncional = cPacUnidFunc.getId()
					.getUnidadeFuncional();
			seqp = seqp + 1;
			
			Date criadoEm = cPacUnidFunc.getCriadoEm();

			RapServidores servidor = cPacUnidFunc.getServidor();
			
			Date dtExecucao = cPacUnidFunc.getDtExecucao();
			String identificadorComplementar = cPacUnidFunc
					.getIdentificadorComplementar();
			DominioCondicaoPaciente condicaoPac = cPacUnidFunc.getCondicaoPac();
			Integer nroFilme = cPacUnidFunc.getNroFilme();
			String observacao = cPacUnidFunc.getObservacao();

			RapServidores servidorAlterado = cPacUnidFunc.getServidorAlterado();

			Date alteradoEm = cPacUnidFunc.getAlteradoEm();

			AelPacUnidFuncionaisId id = new AelPacUnidFuncionaisId();
			id.setPiuPacCodigo(codigoDestino);
			id.setUnidadeFuncional(unidadeFuncional);
			id.setSeqp(seqp);
			AelPacUnidFuncionais aelPacUnidFuncionais = new AelPacUnidFuncionais();
			aelPacUnidFuncionais.setId(id);
			aelPacUnidFuncionais.setUnfExecutaExames(cPacUnidFunc.getUnfExecutaExames());
			aelPacUnidFuncionais.setCriadoEm(criadoEm);
			aelPacUnidFuncionais.setServidor(servidor);
			if(cPacUnidFunc.getItemSolicitacaoExames() != null) {
				aelPacUnidFuncionais.setItemSolicitacaoExames(cPacUnidFunc.getItemSolicitacaoExames());
			}
			aelPacUnidFuncionais.setDtExecucao(dtExecucao);
			aelPacUnidFuncionais
					.setIdentificadorComplementar(identificadorComplementar);
			aelPacUnidFuncionais.setCondicaoPac(condicaoPac);
			aelPacUnidFuncionais.setNroFilme(nroFilme);
			aelPacUnidFuncionais.setObservacao(observacao);
			aelPacUnidFuncionais.setServidorAlterado(servidorAlterado);
			aelPacUnidFuncionais.setAlteradoEm(alteradoEm);
			aelPacUnidFuncionais.setEquipamento(cPacUnidFunc.getEquipamento());

			examesLaudosFacade.inserirAelPacienteUnidadeFuncional(aelPacUnidFuncionais);
		}
		this.flush();
	}

	// ////////////////JOAO//////////////////////

	/**
	 * Método responsável por implementar a procedure obtem_seq.
	 * 
	 * @param pacCodigo
	 * @return
	 */
	protected Short obterMaxSeqAbsSolicitacoesDoacoes(Integer pacCodigo) {
		return this.getBancoDeSangueFacade().obterMaxSeqAbsSolicitacoesDoacoes(pacCodigo);
	}

	/**
	 * Implementa a procedure insere_sdo.
	 * 
	 * @param absSolicitacoesDoacoes
	 * @param pacCodigo
	 * @param sequencia
	 */
	protected AbsSolicitacoesDoacoes persistirAbsSolicitacoesDoacoes(
			AbsSolicitacoesDoacoes absSolicitacoesDoacoes, Integer pacCodigo,
			Short sequencia) {
		AbsSolicitacoesDoacoesId id = new AbsSolicitacoesDoacoesId();
		id.setPacCodigo(pacCodigo);
		id.setSequencia(sequencia);

		AbsSolicitacoesDoacoes absSolicitacoesDoacoesInsert = new AbsSolicitacoesDoacoes();
		absSolicitacoesDoacoesInsert.setId(id);
		absSolicitacoesDoacoesInsert.setDthrSolicitacao(absSolicitacoesDoacoes
				.getDthrSolicitacao());
		absSolicitacoesDoacoesInsert.setNroDoadores(absSolicitacoesDoacoes
				.getNroDoadores());
		absSolicitacoesDoacoesInsert.setObservacao(absSolicitacoesDoacoes
				.getObservacao());
		absSolicitacoesDoacoesInsert.setDtEncerramento(absSolicitacoesDoacoes
				.getDtEncerramento());
		absSolicitacoesDoacoesInsert.setIndSituacao(absSolicitacoesDoacoes
				.getIndSituacao());
		absSolicitacoesDoacoesInsert.setSerVinCodigo(absSolicitacoesDoacoes
				.getSerVinCodigo());
		absSolicitacoesDoacoesInsert.setSerMatricula(absSolicitacoesDoacoes
				.getSerMatricula());
		absSolicitacoesDoacoesInsert.setIndAutomatica(absSolicitacoesDoacoes
				.getIndAutomatica());

		return this.getBancoDeSangueFacade().inserirAbsSolicitacoesDoacoes(absSolicitacoesDoacoesInsert, true);
	}

	/**
	 * Implementa a procedure dados_abs.
	 * 
	 * @param pacCodigoDestino
	 * @param codigoOrigem
	 */
	@SuppressWarnings("ucd")
	protected void atualizarAbsSolicitacoesDoacoes(Integer codigoOrigem,
			Integer codigoDestino) {
		IBancoDeSangueFacade bancoDeSangueFacade = this.getBancoDeSangueFacade();
		
		Short maxSeq = obterMaxSeqAbsSolicitacoesDoacoes(codigoDestino);
		
		List<AbsSolicitacoesDoacoes> listaAbsSolicitacoes = pesquisarAbsSolicitacoesDoacoes(codigoOrigem);
		for (AbsSolicitacoesDoacoes solicitacaoOrigem : listaAbsSolicitacoes) {
			AbsSolicitacoesDoacoes solicitacaoDestino = null;
			solicitacaoDestino = persistirAbsSolicitacoesDoacoes(
					solicitacaoOrigem, codigoDestino, maxSeq);

			// Equivalente a procedure atualiza_doacao
			for (AbsDoacoes absDoacoes : solicitacaoOrigem.getDoacoes()) {
				// FIXME Tocchetto Chamar triggers de UPDATE da tabela
				// ABS_DOACOES quando implementadas.
				absDoacoes.setAbsSolicitacoesDoacoes(solicitacaoDestino);
				bancoDeSangueFacade.atualizarAbsDoacoes(absDoacoes, true);
			}
			maxSeq++;

			// O refresh permite carregar a lista absDoacoes no objeto
			// solicitacaoDestino.
			// Obs.: Esta lista esta sendo conferida nos testes unitários, não
			// remover o refresh.
			super.refresh(solicitacaoDestino);
			bancoDeSangueFacade.removerAbsSolicitacoesDoacoes(solicitacaoOrigem, true);
		}
	}

	/**
	 * Implementação da procedure verifica_end_origem.
	 */
	@SuppressWarnings("ucd")
	protected void verificarEndOrigem(List<AipEnderecosPacientes> tabEnder,
			Integer pacCodigoOrigem, VerificaEnderecoVO verificaEnderecoVO) {
		Integer ind = verificaEnderecoVO.getInd();

		List<AipEnderecosPacientes> enderecos = this.getCadastroPacienteFacade()
				.obterEndPaciente(pacCodigoOrigem);
		for (AipEnderecosPacientes end : enderecos) {
			ind++;

			tabEnder.add(end);

			if (end.getIndPadrao() == DominioSimNao.S
					&& end.getAipBairrosCepLogradouro() != null) {
				verificaEnderecoVO.setvEndOrigemOk(ind);
				verificaEnderecoVO.setvOrigemOk('S');
			} else if (end.getAipBairrosCepLogradouro() != null) {
				verificaEnderecoVO.setvEndOrigemOk(ind);
			} else if (end.getLogradouro() != null
					&& end.getNroLogradouro() != null
					&& end.getBairro() != null
					&& end.getCep() != null
					&& (end.getAipCidade() != null || (end.getCidade() != null && end
							.getAipUf() != null))) {
				verificaEnderecoVO.setvEndOrigemNcadOk(ind);
			}
		}

		verificaEnderecoVO.setInd(ind);
	}

	/**
	 * Implementação da procedure verifica_end_destino.
	 */
	@SuppressWarnings("ucd")
	protected void verificarEndDestino(List<AipEnderecosPacientes> tabEnder,
			Integer codigoDestino, VerificaEnderecoVO verificaEnderecoVO) {
		Integer ind = verificaEnderecoVO.getInd();

		List<AipEnderecosPacientes> enderecos = this.getCadastroPacienteFacade()
				.obterEndPaciente(codigoDestino);
		for (AipEnderecosPacientes end : enderecos) {
			ind++;

			if (ind == 1) {
				verificaEnderecoVO.setvSeqp(end.getId().getSeqp());
			}

			tabEnder.add(end);

			if (end.getIndPadrao() == DominioSimNao.S
					&& end.getAipBairrosCepLogradouro() != null) {
				verificaEnderecoVO.setvEndDestinoOk(ind);
				verificaEnderecoVO.setvDestinoOk('S');
			} else if (end.getAipBairrosCepLogradouro() != null) {
				verificaEnderecoVO.setvEndDestinoOk(ind);
			} else if (end.getLogradouro() != null
					&& end.getNroLogradouro() != null
					&& end.getBairro() != null
					&& end.getCep() != null
					&& (end.getAipCidade() != null || (end.getCidade() != null && end
							.getAipUf() != null))) {
				verificaEnderecoVO.setvEndDestinoNcadOk(ind);
			}
		}

		verificaEnderecoVO.setInd(ind);
	}

	/**
	 * Implementa a procedure atualiza_end. Atualiza o endereço de destino com
	 * os valores do endereço de origem
	 * 
	 * @param tabEnder
	 * @param ind
	 * @param vSeqp
	 * @param pacCodigoDestino
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws BaseException 
	 */
	protected void atualizarEndereco(AipEnderecosPacientes enderecoSubstituto,
			Integer codigoDestino) throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, BaseException {
		
		List<AipEnderecosPacientes> listaEnderecosDestino = this.getCadastroPacienteFacade().obterEndPaciente(codigoDestino);
		if (!listaEnderecosDestino.isEmpty()){
			AipEnderecosPacientes enderecoAtualDestino = listaEnderecosDestino.get(0);
			
			AipEnderecosPacientes endOld = new AipEnderecosPacientes();
			this.getProntuarioRN().copiarPropriedades(endOld, enderecoAtualDestino);
			
			enderecoAtualDestino.setTipoEndereco(enderecoSubstituto.getTipoEndereco());
			enderecoAtualDestino.setIndPadrao(enderecoSubstituto.getIndPadrao());
			enderecoAtualDestino.setAipCidade(enderecoSubstituto.getAipCidade());
			enderecoAtualDestino.setAipUf(enderecoSubstituto.getAipUf());
			enderecoAtualDestino.setLogradouro(enderecoSubstituto.getLogradouro());
			enderecoAtualDestino.setNroLogradouro(enderecoSubstituto.getNroLogradouro());
			enderecoAtualDestino.setComplLogradouro(enderecoSubstituto.getComplLogradouro());
			enderecoAtualDestino.setBairro(enderecoSubstituto.getBairro());
			enderecoAtualDestino.setCidade(enderecoSubstituto.getCidade());
			enderecoAtualDestino.setCep(enderecoSubstituto.getCep());
			enderecoAtualDestino.setAipBairrosCepLogradouro(enderecoSubstituto.getAipBairrosCepLogradouro());
			this.getCadastroPacienteFacade().atualizarEndereco(endOld, enderecoAtualDestino);
			this.flush();
		}

	}

	/**
	 * Implementação da procedure AIPP_ATUALIZA_ENP.
	 * 
	 * @throws ApplicationBusinessException
	 */
	protected void atualizarEnderecoPaciente(Integer codigoOrigem,
			Integer codigoDestino) throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, BaseException {

		List<AipEnderecosPacientes> listaEnderecosOrigem = this.getCadastroPacienteFacade().obterEndPaciente(codigoOrigem);
		List<AipEnderecosPacientes> listaEnderecosDestino = this.getCadastroPacienteFacade().obterEndPaciente(codigoDestino);
		AipEnderecosPacientes enderecoSubstituir = null;
		
		//Se já houver um endereço cadastrado e de correspondência no destino,
		//fica como está no destino, não precisa substituir
		if (obterEnderecoCadastradoCorrespondencia(listaEnderecosDestino) == null){
			if (obterEnderecoCadastradoCorrespondencia(listaEnderecosOrigem) != null){
				//Se não houver um endereço cadastrado que seja de correspondência no destino mas houver um na
				//na origem, substitui pelo da origem
				enderecoSubstituir = (obterEnderecoCadastradoCorrespondencia(listaEnderecosOrigem));
			}
			else{
				//Se já houver um endereço cadastrado no destino e não houver um cadastrado que 
				//seja de correspondência na origem, fica como está no destino, não precisa substituir
				if (obterEnderecoCadastrado(listaEnderecosDestino) == null){
					if (obterEnderecoCadastrado(listaEnderecosOrigem) != null){
						//Caso contrário, se houver um de origem cadastrado,
						//este substituirá o de destino
						enderecoSubstituir = obterEnderecoCadastrado(listaEnderecosOrigem);
					}
					else{
						//Na não ocorrência das hipóteses acima, se houver um endereço não cadastrado mas completo
						//no destino, fica como está	
						if (obterEnderecoNaoCadastradoCompleto(listaEnderecosDestino) == null){
							if (obterEnderecoNaoCadastradoCompleto(listaEnderecosOrigem) != null){
								//Caso contrário, se houver um endereço não cadastrado na origem, este 
								//será o substituto para o destino
								enderecoSubstituir = obterEnderecoNaoCadastradoCompleto(listaEnderecosOrigem);
							}							
						}
					}
				}
			}
		}

		if (enderecoSubstituir != null){
			atualizarEndereco(enderecoSubstituir, codigoDestino);			
		}

	}
	
	/**
	 * Verifica se existe um endereço não cadastrado completo e retorna o mesmo se existir
	 * @param listaEnderecos
	 * @return
	 */
	private AipEnderecosPacientes obterEnderecoNaoCadastradoCompleto(List<AipEnderecosPacientes> listaEnderecos){
		AipEnderecosPacientes retorno = null;
		for (AipEnderecosPacientes endereco: listaEnderecos){
			if (endereco.getLogradouro() != null
					&& endereco.getNroLogradouro() != null
					&& endereco.getBairro() != null
					&& endereco.getCep() != null
					&& (endereco.getAipCidade() != null || (endereco
							.getCidade() != null && endereco.getAipUf() != null))) {
				retorno = endereco;
				break;					
			}
		}
		return retorno;
	}
	
	/**
	 * Verifica se existe um endereço cadastrado e de correspondência e retorna o mesmo se existir
	 * @param listaEnderecos
	 * @return
	 */
	private AipEnderecosPacientes obterEnderecoCadastradoCorrespondencia(List<AipEnderecosPacientes> listaEnderecos){
		AipEnderecosPacientes retorno = null;
		for (AipEnderecosPacientes endereco: listaEnderecos){
			if (endereco.getAipBairrosCepLogradouro() != null){
				if (endereco.getIndPadrao() != null && endereco.getIndPadrao().equals(DominioSimNao.S)) {
					retorno = endereco;
					break;					
				}
			}
		}
		return retorno;
	}
	
	/**
	 * Verifica se existe um endereço cadastrado e retorna o mesmo se existir
	 * @param listaEnderecos
	 * @return
	 */
	private AipEnderecosPacientes obterEnderecoCadastrado(List<AipEnderecosPacientes> listaEnderecos){
		AipEnderecosPacientes retorno = null;
		for (AipEnderecosPacientes endereco: listaEnderecos){
			if (endereco.getAipBairrosCepLogradouro() != null){
				retorno = endereco;
				break;					
			}
		}
		return retorno;
	}


	/**
	 * Implementa a procedure aipp_atualiza_dados_cns.
	 * 
	 * @param pacienteOrigem
	 * @param pacienteDestino
	 * @throws ApplicationBusinessException
	 */
	protected void atualizarDadosCns(AipPacientes pacienteOrigem,
			AipPacientes pacienteDestino) throws ApplicationBusinessException {
		AipPacientesDadosCnsDAO aipPacientesDadosCnsDAO = this.getAipPacientesDadosCnsDAO();
		
		AipPacientesDadosCns dadosCnsDestino = pacienteDestino
				.getAipPacientesDadosCns();

		if (dadosCnsDestino == null) {
			AipPacientesDadosCns dadosCnsOrigem = pacienteOrigem
					.getAipPacientesDadosCns();

			if (dadosCnsOrigem != null) {
				try {
					pacienteOrigem.setAipPacientesDadosCns(null);
					aipPacientesDadosCnsDAO.remover(dadosCnsOrigem);
					aipPacientesDadosCnsDAO.flush();

					aipPacientesDadosCnsDAO.desatachar(dadosCnsOrigem);

					dadosCnsDestino = dadosCnsOrigem;
					dadosCnsDestino.setPacCodigo(pacienteDestino.getCodigo());
					dadosCnsDestino.setAipPaciente(pacienteDestino);
					dadosCnsOrigem = null;

					pacienteDestino.setAipPacientesDadosCns(dadosCnsDestino);

					this.getCadastroPacienteFacade().validarCartaoSUS(pacienteDestino, pacienteDestino.getAipPacientesDadosCns());
					this.getCadastroPacienteFacade().persistirCartaoSus(pacienteDestino);
					// persistirCartaoSus não grava no BD a AipPacientesDadosCns
					// AipPacientesDadosCns é gravado junto de AipPacientes
					// através de CascadeType.PERSIST
					aipPacientesDadosCnsDAO.persistir(dadosCnsDestino);
					aipPacientesDadosCnsDAO.flush();
				} catch (Exception e) {
					ProntuarioONExceptionCode.SUBS_PRONT_0066.throwException(e);
				}
			}
		}
	}

	/**
	 * Implementa a procedure verifica_outras_informacoes.
	 * 
	 * @param pacCodigoOrigem
	 * @param pacCodigoDestino
	 * @throws BaseException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	protected void verificarOutrasInformacoes(Integer pacCodigoOrigem,
			Integer pacCodigoDestino, String nomeMicrocomputador, RapServidores servidorLogado, 
			final Date dataFimVinculoServidor)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, BaseException {
		AipPacientesDAO aipPacientesDAO = this.getAipPacientesDAO();
		
		AipPacientes pacOrigem = obterPacientePorCodigo(pacCodigoOrigem);
		AipPacientes pacDestino = obterPacientePorCodigo(pacCodigoDestino);

		if (pacOrigem.getDtIdentificacao().before(
				pacDestino.getDtIdentificacao())) {
			pacDestino.setDtIdentificacao(pacOrigem.getDtIdentificacao());
		}
		if (pacDestino.getDtObito() == null) {
			pacDestino.setDtObito(pacOrigem.getDtObito());
		}
		if (pacDestino.getRg() == null) {
			pacDestino.setRg(pacOrigem.getRg());
		}
		if (pacDestino.getOrgaoEmisRg() == null) {
			pacDestino.setOrgaoEmisRg(pacOrigem.getOrgaoEmisRg());
		}
		if (pacDestino.getCpf() == null) {
			pacDestino.setCpf(pacOrigem.getCpf());
		}
		if (pacDestino.getNroCartaoSaude() == null) {
			pacDestino.setNroCartaoSaude(pacOrigem.getNroCartaoSaude());
		}
		if (pacDestino.getRegNascimento() == null) {
			pacDestino.setRegNascimento(pacOrigem.getRegNascimento());
		}
		if (pacDestino.getDddFoneResidencial() == null) {
			pacDestino.setDddFoneResidencial(pacOrigem.getDddFoneResidencial());
		}
		if (pacDestino.getFoneResidencial() == null) {
			pacDestino.setFoneResidencial(pacOrigem.getFoneResidencial());
		}
		if (pacDestino.getDddFoneRecado() == null) {
			pacDestino.setDddFoneRecado(pacOrigem.getDddFoneRecado());
		}
		if (pacDestino.getFoneRecado() == null) {
			pacDestino.setFoneRecado(pacOrigem.getFoneRecado());
		}
		if (pacDestino.getAipOcupacoes() == null) {
			pacDestino.setAipOcupacoes(pacOrigem.getAipOcupacoes());
		}
		if(pacDestino.getAipNacionalidades() == null) {
			pacDestino.setAipNacionalidades(pacOrigem.getAipNacionalidades());
		}

		if (pacDestino.getDtUltProcedimento() == null) {
			pacDestino.setDtUltProcedimento(pacOrigem.getDtUltProcedimento());
		} else if (pacOrigem.getDtUltProcedimento() != null
				&& pacOrigem.getDtUltProcedimento().after(
						pacDestino.getDtUltProcedimento())) {
			pacDestino.setDtUltProcedimento(pacOrigem.getDtUltProcedimento());
		}

		// desliga variavel para não atualizar usuario que recadastrou
		this.getProntuarioRN().setarFlagProntuario(
				FlagsSubstituiProntuario.AIPK_PAC_ATU,
				ValorFlagsSubstituiProntuario.FALSE, servidorLogado != null ? servidorLogado.getUsuario() : null);

		this.getCadastroPacienteFacade().atualizarPaciente(pacDestino, nomeMicrocomputador,servidorLogado, true);
		aipPacientesDAO.flush();

		//Atualiza pacientes filhos com a nova mãe (se for o caso)
		List<AipPacientes> listaPacientesUpdate = aipPacientesDAO.listarPacientesPorCodigoMae(pacCodigoOrigem);
		for (AipPacientes pacienteUpdate : listaPacientesUpdate) {
			// Seta pac_codigo_mae = p_codigo_destino
			pacienteUpdate.setMaePaciente(pacDestino);
			this.getCadastroPacienteFacade().atualizarPaciente(pacienteUpdate, nomeMicrocomputador,servidorLogado, true);
			aipPacientesDAO.flush();
		}

		//Substitui o endereço do paciente (se for o caso) conforme regras explicadas no método abaixo
		atualizarEnderecoPaciente(pacCodigoOrigem, pacCodigoDestino);
		aipPacientesDAO.flush();

//		aipPacientesDAO.refresh(pacOrigem);
//		aipPacientesDAO.refresh(pacDestino);

		//Substitui os dados CNS do paciente no caso de haver um registro na origem e não
		//haver no destino
		atualizarDadosCns(pacOrigem, pacDestino);
		aipPacientesDAO.flush();
	}

	/**
	 * Implementa a procedure atualiza_param_peso_mpt.
	 * 
	 * @param codigoOrigem
	 * @param codigoDestino
	 * @throws ApplicationBusinessException
	 */
	protected void atualizarParamPesoMpt(Integer codigoOrigem,
			Integer codigoDestino) throws ApplicationBusinessException {
		try {
			AipPesoPacientesDAO aipPesoPacientesDAO = this.getAipPesoPacientesDAO();
			RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();

			List<AipPesoPacientes> removeList = new ArrayList<AipPesoPacientes>(0);
			List<MptParamCalculoPrescricao> calculosPacOrigem = pesquisarMptParamCalculoPrescricoes(
					codigoOrigem, null);
			for (MptParamCalculoPrescricao calPacOrigem : calculosPacOrigem) {
				// insere novo peso com o código correto do paciente
				AipPesoPacientes peso = calPacOrigem.getAipPesoPaciente();
				AipPesoPacientesId id = new AipPesoPacientesId(codigoDestino,
						peso.getId().getCriadoEm());
				AipPesoPacientes pesoInsert = aipPesoPacientesDAO.obterPorChavePrimaria(id);
				if (pesoInsert == null) {
					pesoInsert = new AipPesoPacientes();
					this.getProntuarioRN().copiarPropriedades(pesoInsert, peso);
					pesoInsert.setId(id);

					this.getCadastroPacienteFacade().persistirPesoPaciente(pesoInsert, servidorLogado);
					aipPesoPacientesDAO.flush();
				}

				// atualiza o código correto do paciente no FK do peso
				calPacOrigem.setAipPesoPaciente(pesoInsert);
				aipPesoPacientesDAO.flush();

				// exclui o peso registrado com o código errado do paciente
				removeList.add(peso);
			}

			for (AipPesoPacientes peso : removeList) {
				AipPesoPacientes auxPeso = aipPesoPacientesDAO.obterPorChavePrimaria(peso.getId());

				if (auxPeso != null) {
					aipPesoPacientesDAO.remover(peso);
					aipPesoPacientesDAO.flush();
				}
			}
		} catch (Exception e) {
			ProntuarioONExceptionCode.SUBS_PRONT_0067.throwException(e);
		}
	}

	/**
	 * Implementa a procedure atualiza_param_altura_mpt.
	 * 
	 * @param codigoOrigem
	 * @param codigoDestino
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("ucd")
	protected void atualizarParamAlturaMpt(Integer codigoOrigem,
			Integer codigoDestino) throws ApplicationBusinessException {
		try {
			AipAlturaPacientesDAO aipAlturaPacientesDAO = this.getAipAlturaPacientesDAO();
		//	IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade = this.getProcedimentoTerapeuticoFacade();
			
			List<AipAlturaPacientes> removeList = new ArrayList<AipAlturaPacientes>(
					0);
			List<MptParamCalculoPrescricao> calculosPacOrigem = pesquisarMptParamCalculoPrescricoes(
					null, codigoOrigem);
			for (MptParamCalculoPrescricao calPacOrigem : calculosPacOrigem) {
				// insere nova altura com o código correto do paciente
				AipAlturaPacientes altura = calPacOrigem.getAipAlturaPaciente();
				AipAlturaPacientesId id = new AipAlturaPacientesId(
						codigoDestino, altura.getId().getCriadoEm());
				AipAlturaPacientes alturaInsert = aipAlturaPacientesDAO.obterPorChavePrimaria(id);
				if (alturaInsert == null) {
					alturaInsert = new AipAlturaPacientes();
					this.getProntuarioRN().copiarPropriedades(alturaInsert, altura);
					alturaInsert.setId(id);

					aipAlturaPacientesDAO.persistir(alturaInsert);
					aipAlturaPacientesDAO.flush();
				}

				// atualiza o código correto do paciente no FK da altura
				calPacOrigem.setAipAlturaPaciente(alturaInsert);
				this.flush();

				// exclui a altura registrada com o código errado do paciente
				removeList.add(altura);
			}

			for (AipAlturaPacientes altura : removeList) {
				AipAlturaPacientes auxAltura = aipAlturaPacientesDAO.obterPorChavePrimaria(altura.getId());
				if (auxAltura != null) {
					aipAlturaPacientesDAO.remover(altura);
					aipAlturaPacientesDAO.flush();
				}
			}
		} catch (Exception e) {
			ProntuarioONExceptionCode.SUBS_PRONT_0067.throwException(e);
		}
	}

	/**
	 * Implementa a procedure atualiza_param_peso_mpm.
	 * 
	 * @param codigoOrigem
	 * @param codigoDestino
	 * @throws ApplicationBusinessException
	 */
	protected void atualizarParamPesoMpm(Integer codigoOrigem,
			Integer codigoDestino) throws ApplicationBusinessException {
		try {
			AipPesoPacientesDAO aipPesoPacientesDAO = this.getAipPesoPacientesDAO();
		//	IPrescricaoMedicaFacade prescricaoMedicaFacade = this.getPrescricaoMedicaFacade();
			RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();

			List<AipPesoPacientes> removeList = new ArrayList<AipPesoPacientes>(
					0);
			List<MpmParamCalculoPrescricao> calculosPacOrigem = pesquisarMpmParamCalculoPrescricoes(
					codigoOrigem, null);
			for (MpmParamCalculoPrescricao calPacOrigem : calculosPacOrigem) {
				// insere novo peso com o código correto do paciente
				AipPesoPacientes peso = calPacOrigem.getAipPesoPaciente();
				AipPesoPacientesId id = new AipPesoPacientesId(codigoDestino,
						peso.getId().getCriadoEm());
				AipPesoPacientes pesoInsert = aipPesoPacientesDAO.obterPorChavePrimaria(id);
				if (pesoInsert == null) {
					pesoInsert = new AipPesoPacientes();
					this.getProntuarioRN().copiarPropriedades(pesoInsert, peso);
					pesoInsert.setId(id);

					this.getCadastroPacienteFacade().persistirPesoPaciente(pesoInsert, servidorLogado);
					aipPesoPacientesDAO.flush();
				}

				// atualiza o código correto do paciente no FK do peso
				calPacOrigem.setAipPesoPaciente(pesoInsert);
				this.flush();

				// exclui o peso registrado com o código errado do paciente
				removeList.add(peso);
			}

			for (AipPesoPacientes peso : removeList) {
				AipPesoPacientes auxPeso = aipPesoPacientesDAO.obterPorChavePrimaria(peso.getId());
				if (auxPeso != null) {
					aipPesoPacientesDAO.remover(peso);
					aipPesoPacientesDAO.flush();
				}
			}
		} catch (Exception e) {
			ProntuarioONExceptionCode.SUBS_PRONT_0068.throwException(e);
		}
	}

	/**
	 * Implementa a procedure atualiza_param_altura_mpm.
	 * 
	 * @param codigoOrigem
	 * @param codigoDestino
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("ucd")
	protected void atualizarParamAlturaMpm(Integer codigoOrigem,
			Integer codigoDestino) throws ApplicationBusinessException {
		try {
			AipAlturaPacientesDAO aipAlturaPacientesDAO = this.getAipAlturaPacientesDAO();
		//	IPrescricaoMedicaFacade prescricaoMedicaFacade = this.getPrescricaoMedicaFacade();
			
			List<AipAlturaPacientes> removeList = new ArrayList<AipAlturaPacientes>(
					0);
			List<MpmParamCalculoPrescricao> calculosPacOrigem = pesquisarMpmParamCalculoPrescricoes(
					null, codigoOrigem);
			for (MpmParamCalculoPrescricao calPacOrigem : calculosPacOrigem) {
				// insere nova altura com o código correto do paciente
				AipAlturaPacientes altura = calPacOrigem.getAipAlturaPaciente();
				AipAlturaPacientesId id = new AipAlturaPacientesId(
						codigoDestino, altura.getId().getCriadoEm());
				AipAlturaPacientes alturaInsert = aipAlturaPacientesDAO.obterPorChavePrimaria(id);
				if (alturaInsert == null) {
					alturaInsert = new AipAlturaPacientes();
					this.getProntuarioRN().copiarPropriedades(alturaInsert, altura);
					alturaInsert.setId(id);

					aipAlturaPacientesDAO.persistir(alturaInsert);
					aipAlturaPacientesDAO.flush();
				}

				// atualiza o código correto do paciente no FK da altura
				calPacOrigem.setAipAlturaPaciente(alturaInsert);
				this.flush();

				// exclui a altura registrada com o código errado do paciente
				removeList.add(altura);
			}

			for (AipAlturaPacientes altura : removeList) {
				AipAlturaPacientes auxAlturaPaciente = aipAlturaPacientesDAO.obterPorChavePrimaria(altura.getId());
				if (auxAlturaPaciente != null) {
					aipAlturaPacientesDAO.remover(altura);
					aipAlturaPacientesDAO.flush();
				}
			}
		} catch (Exception e) {
			ProntuarioONExceptionCode.SUBS_PRONT_0068.throwException(e);
		}
	}

	protected void excluiEnderecos(Integer codigoOrigem, RapServidores servidorLogado)
			throws ApplicationBusinessException {
		ICadastroPacienteFacade cadastroPacienteFacade = this.getCadastroPacienteFacade();
		AipEnderecosPacientesDAO aipEnderecosPacientesDAO = this.getAipEnderecosPacientesDAO();

		List<AipEnderecosPacientes> enderecos = aipEnderecosPacientesDAO
				.listarEnderecosPacientesPorCodigoPaciente(codigoOrigem);

		for (AipEnderecosPacientes end : enderecos) {
			//#23337 - Ajuste para remover o endereço fisicamente direto ao invés de
			//setar a coluna IND_EXCLUSAO_FORCADA para 'S' (segundo orientação da Milena)
			cadastroPacienteFacade.removerEnderecoPaciente(end);
		}
		aipEnderecosPacientesDAO.flush();
	}

	protected List<AinAtendimentosUrgencia> pesquisarAinAtendimentosUrgencia(
			Integer pacCodigo) {
		return this.getInternacaoFacade().pesquisarAinAtendimentosUrgencia(pacCodigo);
	}

	@SuppressWarnings("ucd")
	protected void atualizarAinAtendimentosUrgencia(Integer codigoOrigem,
			Integer codigoDestino, String nomeMicrocomputador, RapServidores servidorLogado, final Date dataFimVinculoServidor) throws BaseException {
		IInternacaoFacade internacaoFacade = this.getInternacaoFacade();
		AipPacientesDAO aipPacientesDAO = this.getAipPacientesDAO();
		
		// substituindo paciente no atendimento de urgência, caso exista.
		AipPacientes pacOrigem = aipPacientesDAO.obterPorChavePrimaria(codigoOrigem);
		AipPacientes pacDestino = aipPacientesDAO.obterPorChavePrimaria(codigoDestino);
		List<AinAtendimentosUrgencia> atendimentosUrgencia = pesquisarAinAtendimentosUrgencia(codigoOrigem);
		for (AinAtendimentosUrgencia atd : atendimentosUrgencia) {
			internacaoFacade.substituirPacienteAtendimentoUrgencia(pacOrigem,
							pacDestino, atd, nomeMicrocomputador, servidorLogado,dataFimVinculoServidor);
			this.flush();
		}
	}

	protected List<AinInternacao> pesquisarAinInternacao(Integer pacCodigo) {
		return this.getInternacaoFacade().pesquisarAinInternacao(pacCodigo);
	}

	@SuppressWarnings("ucd")
	protected void atualizarAinInternacao(Integer codigoOrigem,
			Integer codigoDestino, String nomeMicrocomputador, RapServidores servidorLogado, final Date dataFimVinculoServidor) throws BaseException {
		IInternacaoFacade internacaoFacade = this.getInternacaoFacade();
		
		AipPacientes pacDestino = this.getAipPacientesDAO().obterPorChavePrimaria(codigoDestino);
		// substituindo paciente na internação, caso exista.
		List<AinInternacao> listaInternacoes = pesquisarAinInternacao(codigoOrigem);
		for (AinInternacao internacao : listaInternacoes) {
			internacao.setPaciente(pacDestino);
			internacaoFacade.atualizarInternacao(internacao, nomeMicrocomputador,servidorLogado, dataFimVinculoServidor, true, false);
			this.flush();
		}
	}
	
	protected void atualizarEcpHorarioControles(Integer codigoOrigem, Integer codigoDestino) {
		AipPacientes pacDestino = this.getAipPacientesDAO().obterPorChavePrimaria(codigoDestino);
		
		List<EcpHorarioControle> listaControles = this.controlePacienteFacade.pesquisarHorarioControlePorPaciente(codigoOrigem);
		for (EcpHorarioControle horarioControle : listaControles) {
			horarioControle.setPaciente(pacDestino);
			controlePacienteFacade.atualizarHorarioControle(horarioControle);
		}
	}
	
	@SuppressWarnings("ucd")
	protected void atualizarPacienteHospDia(Integer codigoOrigem,
			AipPacientes pacDestino) {
		IInternacaoFacade internacaoFacade = this.getInternacaoFacade();
		
		// substituindo paciente no hospital dia, caso exista.
		List<AhdHospitaisDia> hospitais = internacaoFacade.listarHospitaisDiaPorCodigoPaciente(codigoOrigem);

		for (AhdHospitaisDia hosp : hospitais) {
			hosp.setPaciente(pacDestino);
			internacaoFacade.persistirAhdHospitaisDia(hosp);
		}
		this.flush();
	}

	@SuppressWarnings("ucd")
	protected void atualizarPacienteSolicInternacao(Integer codigoOrigem,
			AipPacientes pacDestino, Boolean substituirProntuario) throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException,
			ApplicationBusinessException {
		IInternacaoFacade internacaoFacade = this.getInternacaoFacade();
		ISolicitacaoInternacaoFacade solicitacaoInternacaoFacade = this.getSolicitacaoInternacaoFacade();
		
		// atualizando solicitações de internação
		List<AinSolicitacoesInternacao> solicitacoes = internacaoFacade.listarSolicitacoesInternacaoPorCodigoPaciente(codigoOrigem);

		for (AinSolicitacoesInternacao sol : solicitacoes) {
			sol.setPaciente(pacDestino);
			AinSolicitacoesInternacao solOld = new AinSolicitacoesInternacao();
			this.getProntuarioRN().copiarPropriedades(solOld, sol);
			solicitacaoInternacaoFacade.atualizarSolicitacaoInternacao(sol, solOld, substituirProntuario);
		}
		this.flush();
	}

	@SuppressWarnings("ucd")
	protected void atualizarExtratoLeito(Integer codigoOrigem,
			AipPacientes pacDestino) {
		IInternacaoFacade internacaoFacade = this.getInternacaoFacade();
		
		// atualizando extrato de leito referente a solicitação de internação
		List<AinExtratoLeitos> extratos = internacaoFacade.listarExtratosLeitosPorCodigoPaciente(codigoOrigem);

		for (AinExtratoLeitos ext : extratos) {
			ext.setPaciente(pacDestino);
			internacaoFacade.persistirAinExtratoLeitos(ext);
		}
		this.flush();
	}

	@SuppressWarnings("ucd")
	protected void atualizarCirurgias(Integer codigoOrigem,
			AipPacientes pacDestino, RapServidores servidorLogado) throws BaseException {
		IBlocoCirurgicoFacade blocoCirurgicoFacade = getBlocoCirurgicoFacade();
		
		// substituindo cirurgias, caso exista
		List<MbcCirurgias> cirurgias = blocoCirurgicoFacade.listarCirurgiasPorCodigoPaciente(codigoOrigem);

		for (MbcCirurgias cir : cirurgias) {
			cir.setPaciente(pacDestino);
			blocoCirurgicoFacade.persistirCirurgia(cir, servidorLogado);
		}
		this.flush();
	}

	@SuppressWarnings("ucd")
	protected void atualizarAgendas(Integer codigoOrigem, AipPacientes pacDestino, RapServidores servidorLogado) throws BaseException {
		IBlocoCirurgicoFacade blocoCirurgicoFacade = this.getBlocoCirurgicoFacade();
		
		// substituindo agendas, caso exista
		List<MbcAgendas> agendas = blocoCirurgicoFacade.listarAgendarPorCodigoPaciente(codigoOrigem);

		for (MbcAgendas agd : agendas) {
			agd.setPaciente(pacDestino);
			blocoCirurgicoFacade.persistirAgenda(agd, servidorLogado);
		}
		this.flush();
	}

	private List<AghAtendimentos> pesquisarAghAtendimentos(Integer pacCodigo,
			Boolean origemCirurgia) {
		return this.getAghuFacade().pesquisarAghAtendimentos(pacCodigo, origemCirurgia);
	}

	protected void atualizarAghAtendimentos(Integer codigoOrigem,
			AipPacientes pacDestino, Integer prontuarioDestino,
			Boolean origemCirurgia, String nomeMicrocomputador, RapServidores servidorLogado, Date dataFimVinculoServidor) throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException, BaseException {
		IPacienteFacade pacienteFacade = this.getPacienteFacade();
		//IAghuFacade aghuFacade = this.getAghuFacade();
		
		// substituindo atendimento cirúrgico, caso exista
		List<AghAtendimentos> atendimentos = pesquisarAghAtendimentos(
				codigoOrigem, origemCirurgia);
		for (AghAtendimentos atd : atendimentos) {
			AghAtendimentos atdOld = new AghAtendimentos();
			this.getProntuarioRN().copiarPropriedades(atdOld, atd);
			atd.setPaciente(pacDestino);
			atd.setProntuario(prontuarioDestino);
			pacienteFacade.atualizarAtendimento(atd, atdOld, nomeMicrocomputador, servidorLogado,dataFimVinculoServidor);
		}
		this.flush();
	}

	/**
	 * Método que substitui as consultas do paciente origem para o paciente destino
	 * @param codigoOrigem
	 * @param pacDestino
	 * @param servidorLogado 
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws NumberFormatException
	 * @throws BaseException
	 */
	protected void atualizarConsultas(Integer codigoOrigem,
			AipPacientes pacDestino, String nomeMicrocomputador, RapServidores servidorLogado, Date dataFimVinculoServidor) throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException,
			NumberFormatException, BaseException {
		IAmbulatorioFacade ambulatorioFacade = this.getAmbulatorioFacade();
		// Substitui as consultas caso exista
		List<AacConsultas> consultas = ambulatorioFacade.listarConsultasPorCodigoPaciente(codigoOrigem);
		for (AacConsultas con : consultas) {
			//#23337 - Modificado
			AacConsultas conOld = new AacConsultas();
			this.getProntuarioRN().copiarPropriedades(conOld, con);
			
			con.setPaciente(pacDestino);
			ambulatorioFacade.atualizarConsulta(con, conOld, nomeMicrocomputador, dataFimVinculoServidor,true);
		}
		this.flush();
	}
	
	@SuppressWarnings("ucd")
	protected void atualizarMciMvtoFatorPredisponentes(Integer codigoOrigem,
			AipPacientes pacDestino) {
		IControleInfeccaoFacade controleInfeccaoFacade = this.getControleInfeccaoFacade();
		
		// substituindo MCI_MVTO_FATOR_PREDISPONENTES, caso exista.
		List<MciMvtoFatorPredisponentes> fatores = controleInfeccaoFacade.listarMvtosFatorPredisponentesPorCodigoPaciente(codigoOrigem);

		for (MciMvtoFatorPredisponentes fat : fatores) {
			fat.setPaciente(pacDestino);
			controleInfeccaoFacade.persistirMciMvtoFatorPredisponentes(fat);
		}
		this.flush();
	}

	@SuppressWarnings("ucd")
	protected void atualizarMciMvtoProcedimentoRiscos(Integer codigoOrigem,
			AipPacientes pacDestino) {
		IControleInfeccaoFacade controleInfeccaoFacade = this.getControleInfeccaoFacade();
		
		// substituindo MCI_MVTO_PROCEDIMENTO_RISCOS, caso exista.
		List<MciMvtoProcedimentoRiscos> procedimentosRisco = controleInfeccaoFacade.listarMvtosProcedimentosRiscosPorCodigoPaciente(codigoOrigem);

		for (MciMvtoProcedimentoRiscos prc : procedimentosRisco) {
			prc.setPaciente(pacDestino);
			controleInfeccaoFacade.persistirMciMvtoProcedimentoRiscos(prc);
		}
		this.flush();
	}

	@SuppressWarnings("ucd")
	protected void atualizarMciMvtoMedidaPreventivas(Integer codigoOrigem,
			AipPacientes pacDestino) {
		IControleInfeccaoFacade controleInfeccaoFacade = this.getControleInfeccaoFacade();
		
		// substituindo MCI_MVTO_MEDIDA_PREVENTIVAS, caso exista.
		List<MciMvtoMedidaPreventivas> medidas = controleInfeccaoFacade.listarMvtosMedidasPreventivasPorCodigoPaciente(codigoOrigem);

		for (MciMvtoMedidaPreventivas med : medidas) {
			med.setPaciente(pacDestino);
			controleInfeccaoFacade.persistirMciMvtoMedidaPreventivas(med);
		}
		this.flush();
	}

	@SuppressWarnings("ucd")
	protected void atualizarMciMvtoInfeccaoTopografias(Integer codigoOrigem,
			AipPacientes pacDestino) {
		IControleInfeccaoFacade controleInfeccaoFacade = this.getControleInfeccaoFacade();
		
		// substituindo MCI_MVTO_INFECCAO_TOPOGRAFIAS, caso exista.
		List<MciMvtoInfeccaoTopografias> topografias = controleInfeccaoFacade
				.listarMvtosInfeccoesTopologiasPorCodigoPaciente(codigoOrigem);

		for (MciMvtoInfeccaoTopografias top : topografias) {
			top.setPaciente(pacDestino);
			controleInfeccaoFacade.persistirMciMvtoInfeccaoTopografias(top);
		}
		this.flush();
	}

	@SuppressWarnings("ucd")
	protected void atualizarFatContaApacs(Integer codigoOrigem,
			AipPacientes pacDestino) {
		IFaturamentoFacade faturamentoFacade = this.getFaturamentoFacade();
		
		// substituindo FAT_CONTA_APACS, caso exista.
		List<FatContaApac> contas = faturamentoFacade.listarContasApacsPorCodigoPaciente(codigoOrigem);

		for (FatContaApac con : contas) {
			con.setPaciente(pacDestino);
			faturamentoFacade.persistirFatContaApac(con);
		}
		this.flush();
	}

	@SuppressWarnings("ucd")
	protected void atualizarMpmAltaSumarios(Integer codigoOrigem,
			AipPacientes pacDestino) {
		IPrescricaoMedicaFacade prescricaoMedicaFacade = this.getPrescricaoMedicaFacade();
		
		// substituindo MPM_ALTA_SUMARIOS, caso exista.
		List<MpmAltaSumario> altas = prescricaoMedicaFacade.listarAltasSumarioPorCodigoPaciente(codigoOrigem);

		for (MpmAltaSumario alt : altas) {
			alt.setPaciente(pacDestino);
			prescricaoMedicaFacade.atualizarMpmAltaSumario(alt);
		}
		this.flush();
	}

	@SuppressWarnings("ucd")
	protected void atualizarAghAtendimentoPacientes(Integer codigoOrigem,
			AipPacientes pacDestino) {
		IAghuFacade aghuFacade = this.getAghuFacade();
		
		// substituindo AGH_ATENDIMENTO_PACIENTES, caso exista.
		List<AghAtendimentoPacientes> atendimentosPaciente = aghuFacade.listarAtendimentosPacientesPorCodigoPaciente(codigoOrigem);

		for (AghAtendimentoPacientes atd : atendimentosPaciente) {
			atd.setPaciente(pacDestino);
			aghuFacade.persistirAghAtendimentoPacientes(atd);
		}
		this.flush();
	}

	/**
	 * Atualiza as amostras dos pacientes e as solicitações por amostra que
	 * referenciam as amostras atualizadas.
	 * 
	 * @param codigoOrigem
	 * @param codigoDestino
	 * @author mtocchetto
	 */
	@SuppressWarnings("ucd")
	protected void atualizarAbsAmostrasPacientes(Integer codigoOrigem,
			Integer codigoDestino) {
		/*
		 * A lógica do método foi dividida em 3 métodos auxiliares para permitir
		 * a criação de testes unitários de cada etapa da atualização de
		 * AbsAmostrasPacientes.
		 */

		// Clona os registros de origem setando o paciente de destino na PK
		copiarAbsAmostrasPacientes(codigoOrigem, codigoDestino);

		// Atualiza os filhos (solicitacoes) setando o paciente de destino
		atualizarAbsSolicitacoesPorAmostra(codigoOrigem, codigoDestino);

		// Remove os registros de origem
		removerAbsAmostrasPacientes(codigoOrigem);
	}

	protected void copiarAbsAmostrasPacientes(Integer codigoOrigem,
			Integer codigoDestino) {
		IBancoDeSangueFacade bancoDeSangueFacade = this.getBancoDeSangueFacade();
		
		// Clona os registros de origem setando o paciente de destino na PK
		List<AbsAmostrasPacientes> listaAmostras = pesquisarAmostrasPaciente(codigoOrigem);
		for (AbsAmostrasPacientes amostra : listaAmostras) {
			bancoDeSangueFacade.desatacharAbsAmostrasPacientes(amostra);
			amostra.getId().setPacCodigo(codigoDestino);
			amostra.setSolicitacoesPorAmostras(null);
			bancoDeSangueFacade.persistirAbsAmostrasPacientes(amostra);
		}
		this.flush();
	}

	protected void atualizarAbsSolicitacoesPorAmostra(Integer codigoOrigem,
			Integer codigoDestino) {
		IBancoDeSangueFacade bancoDeSangueFacade = this.getBancoDeSangueFacade();
		
		List<AbsSolicitacoesPorAmostra> solicitacoesAmostra = pesquisarAbsSolicitacoesPorAmostra(codigoOrigem);
		for (AbsSolicitacoesPorAmostra solicitacao : solicitacoesAmostra) {
			AbsAmostrasPacientes amostra = solicitacao.getAmostraPaciente();
			AbsAmostrasPacientesId id = solicitacao.getAmostraPaciente()
					.getId();

			id = new AbsAmostrasPacientesId(codigoDestino, id.getDthrAmostra());
			amostra = bancoDeSangueFacade.obterAbsAmostrasPacientesPorChavePrimaria(id);

			solicitacao.setAmostraPaciente(amostra);
		}
		this.flush();
	}

	protected void removerAbsAmostrasPacientes(Integer pacCodigo) {
		IBancoDeSangueFacade bancoDeSangueFacade = this.getBancoDeSangueFacade();
		
		List<AbsAmostrasPacientes> listaAmostras = pesquisarAmostrasPaciente(pacCodigo);
		for (AbsAmostrasPacientes amostra : listaAmostras) {
			bancoDeSangueFacade.removerAbsAmostrasPacientes(amostra, false);
		}
		this.flush();
	}

	@SuppressWarnings("ucd")
	protected void atualizarAbsEstoqueComponentes(Integer codigoOrigem,
			AipPacientes pacDestino) {
		IBancoDeSangueFacade bancoDeSangueFacade = this.getBancoDeSangueFacade();
		
		// substituindo ABS_ESTOQUE_COMPONENTES, caso exista.
		List<AbsEstoqueComponentes> estoques = bancoDeSangueFacade.listarEstoqueComponentesPorCodigoPaciente(codigoOrigem);

		for (AbsEstoqueComponentes est : estoques) {
			est.setPaciente(pacDestino);
			bancoDeSangueFacade.persistirAbsEstoqueComponentes(est);
		}
		this.flush();
	}

	@SuppressWarnings("ucd")
	protected void atualizarAbsMovimentosComponentes(Integer codigoOrigem,
			AipPacientes pacDestino) {
		IBancoDeSangueFacade bancoDeSangueFacade = this.getBancoDeSangueFacade();
		
		// substituindo ABS_MOVIMENTOS_COMPONENTES, caso exista.
		List<AbsMovimentosComponentes> movimentos = bancoDeSangueFacade.listarMovimentosComponentesPorCodigoPaciente(codigoOrigem);

		for (AbsMovimentosComponentes mov : movimentos) {
			mov.setPaciente(pacDestino);
			bancoDeSangueFacade.persistirAbsMovimentosComponentes(mov);
		}
		this.flush();
	}

	@SuppressWarnings("ucd")
	protected void atualizarAbsRegSanguineoPacientes(Integer codigoOrigem,
			AipPacientes pacDestino) {
		IBancoDeSangueFacade bancoDeSangueFacade = this.getBancoDeSangueFacade();
		
		// substituindo ABS_REG_SANGUINEO_PACIENTES, caso exista.
		List<AbsRegSanguineoPacientes> regSanguineos = bancoDeSangueFacade.listarRegSanguineoPacientesPorCodigoPaciente(codigoOrigem);

		for (AbsRegSanguineoPacientes reg : regSanguineos) {
			reg.getId().setPacCodigo(pacDestino.getCodigo()); // verificar se
			// isso é
			// possível
			bancoDeSangueFacade.persistirAbsRegSanguineoPacientes(reg);
		}
		this.flush();
	}

	protected void atualizarFatProcedAmbRealizados(Integer codigoOrigem,
			AipPacientes pacDestino, String nomeMicrocomputador, RapServidores servidorLogado, Date dataFimVinculoServidor)
			throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, BaseException {
		IFaturamentoFacade faturamentoFacade = this.getFaturamentoFacade();
		// substituindo FAT_PROCED_AMB_REALIZADOS, caso exista.
		List<FatProcedAmbRealizado> procedAmbRealizados = faturamentoFacade
				.listarProcedAmbRealizadoPorCodigoPacientePrhConNumeroNulo(codigoOrigem);

		for (FatProcedAmbRealizado proc : procedAmbRealizados) {
			FatProcedAmbRealizado procOld = new FatProcedAmbRealizado();
			this.getProntuarioRN().copiarPropriedades(procOld, proc);
			
			proc.setPaciente(pacDestino);
			faturamentoFacade.atualizarProcedimentoAmbulatorialRealizado(proc, procOld, nomeMicrocomputador, dataFimVinculoServidor);
		}
		this.flush();
	}

	protected void atualizarAghAtendimentosPacExtern(Integer codigoOrigem,
			AipPacientes pacDestino, String nomeMicrocomputador) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		//IAghuFacade aghuFacade = this.getAghuFacade();
		
		// substituindo AGH_ATENDIMENTOS_PAC_EXTERN, caso exista.
		List<AghAtendimentosPacExtern> atendimentosExt = getExamesFacade()
				.listarAtendimentosPacExternPorCodigoPaciente(codigoOrigem);

		for (AghAtendimentosPacExtern atd : atendimentosExt) {
			atd.setPaciente(pacDestino);
			getExamesFacade().atualizarAghAtendimentosPacExtern(atd, nomeMicrocomputador, servidorLogado);
		}
		this.flush();
	}

	/**
	 * Método que substitui o peso do paciente, se for o caso
	 * @param codigoOrigem
	 * @param codigoDestino
	 * @throws ApplicationBusinessException
	 */
	protected void atualizarAipPesoPacientes(Integer codigoOrigem,
			Integer codigoDestino) throws ApplicationBusinessException {
		AipPesoPacientesDAO aipPesoPacientesDAO = this.getAipPesoPacientesDAO();
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();

		// substituindo AIP_PESO_PACIENTES, caso exista.
		List<AipPesoPacientes> pesosOrigem = aipPesoPacientesDAO
				.listarPesosPacientePorCodigoPacienteOrdenadoPorCriadoEmAsc(codigoOrigem);
		
		List<AipPesoPacientes> pesosDestino = aipPesoPacientesDAO
				.listarPesosPacientePorCodigoPacienteOrdenadoPorCriadoEmAsc(codigoDestino);

		if (pesosOrigem.size() > 0) {
			AipPesoPacientes pesoOrigem = pesosOrigem.get(0);

			if (pesosDestino.size() > 0) {
				AipPesoPacientes pesoDestino = pesosDestino.get(0);

				if (pesoOrigem.getPeso() == pesoDestino.getPeso()
						&& DateUtils.isSameDay(
								pesoOrigem.getId().getCriadoEm(), pesoDestino
										.getId().getCriadoEm())) {
					aipPesoPacientesDAO.remover(pesoOrigem);
					aipPesoPacientesDAO.flush();
				} else {
					if (DateUtils.isSameDay(pesoOrigem.getId().getCriadoEm(),
							pesoDestino.getId().getCriadoEm())) {
						ProntuarioONExceptionCode.ERROR_CODE_20001
								.throwException();
					}
				}
			} else {
				aipPesoPacientesDAO.remover(pesoOrigem);
				aipPesoPacientesDAO.flush();
				aipPesoPacientesDAO.desatachar(pesoOrigem);

				AipPesoPacientesId id = pesoOrigem.getId();
				id.setPacCodigo(codigoDestino); // não sei se isso funciona,
				// talvez tenhamos que fazer
				// utilizando JPQL.

				this.getCadastroPacienteFacade().persistirPesoPaciente(pesoOrigem, servidorLogado);
				aipPesoPacientesDAO.flush();
			}
		}
	}
	
	protected void atualizarDadosClinicosPacientes(AipPacientes pacienteOrigem,
			AipPacientes pacienteDestino) throws ApplicationBusinessException{

		AipPacienteDadoClinicos dadosClinicosOrigem = aipPacienteDadoClinicosDAO
				.obterDadosAdicionaisPaciente(pacienteOrigem);
		AipPacienteDadoClinicos dadosClinicosDestino = aipPacienteDadoClinicosDAO
				.obterDadosAdicionaisPaciente(pacienteDestino);
		if (dadosClinicosOrigem != null){
			if (dadosClinicosDestino != null){
				if ((CoreUtil.modificados(dadosClinicosOrigem.getFatorRh(),
						dadosClinicosDestino.getFatorRh()))
						|| (CoreUtil.modificados(
								dadosClinicosOrigem.getTipagemSanguinea(),
								dadosClinicosDestino.getTipagemSanguinea()))
								|| (CoreUtil.modificados(
										dadosClinicosOrigem.getMesesGestacao(),
										dadosClinicosDestino.getMesesGestacao()))) {
					
					throw new ApplicationBusinessException(
							ProntuarioONExceptionCode.ERRO_DADOS_CLINICOS_DIFERENTES);
					
				}
				else{
					aipPacienteDadoClinicosDAO.remover(dadosClinicosOrigem);
					aipPacienteDadoClinicosDAO.flush();
				}			
			}	
			else{
				//Copia os dados de origem para o destino
				dadosClinicosDestino = new AipPacienteDadoClinicos();
				atualizarAtributosDadosClinicos(dadosClinicosOrigem, dadosClinicosDestino);
				//Exclui os dados de Origem
				aipPacienteDadoClinicosDAO.remover(dadosClinicosOrigem);
				aipPacienteDadoClinicosDAO.flush();
				//Seta o paciente de Destino e persiste
				dadosClinicosDestino.setPacCodigo(pacienteDestino.getCodigo());
				dadosClinicosDestino.setAipPaciente(pacienteDestino);
				aipPacienteDadoClinicosDAO.persistir(dadosClinicosDestino);
				aipPacienteDadoClinicosDAO.flush();
			}
		}
	}
	
	private void atualizarAtributosDadosClinicos(AipPacienteDadoClinicos dadosOrigem, AipPacienteDadoClinicos dadosDestino){
		dadosDestino.setFatorRh(dadosOrigem.getFatorRh());
		dadosDestino.setTipagemSanguinea(dadosOrigem.getTipagemSanguinea());
		dadosDestino.setMesesGestacao(dadosOrigem.getMesesGestacao());
		dadosDestino.setApgar1(dadosOrigem.getApgar1());
		dadosDestino.setApgar5(dadosOrigem.getApgar5());
		dadosDestino.setDthrNascimento(dadosOrigem.getDthrNascimento());
		dadosDestino.setTemperatura(dadosOrigem.getTemperatura());
		dadosDestino.setIgSemanas(dadosOrigem.getIgSemanas());
		dadosDestino.setCriadoEm(dadosOrigem.getCriadoEm());
		dadosDestino.setAlteradoEm(new Date());
		dadosDestino.setIndExterno(dadosOrigem.getIndExterno());
	}

	@SuppressWarnings("ucd")
	protected void atualizarFatPacienteTratamentos(Integer codigoOrigem,
			Integer codigoDestino) {
		IFaturamentoFacade faturamentoFacade = this.getFaturamentoFacade();
		
		// substituindo FAT_PACIENTE_TRATAMENTOS, caso exista.
		List<FatPacienteTratamentos> tratamentos = faturamentoFacade.listarPacientesTratamentosPorCodigoPaciente(codigoOrigem);

		for (FatPacienteTratamentos tratamento : tratamentos) {
			FatPacienteTratamentosId id = tratamento.getId();
			id.setPacCodigo(codigoDestino);

			faturamentoFacade.persistirFatPacienteTratamentos(tratamento); // talvez isso não funcione,
			// teremos que fazer via
			// JPQL.
		}
		this.flush();
	}

	@SuppressWarnings("ucd")
	protected void atualizarAacAtendimentoApacs(Integer codigoOrigem,
			Integer codigoDestino) {
		IAmbulatorioFacade ambulatorioFacade = this.getAmbulatorioFacade();
		
		// substituindo apac ligada diretamente a paciente (fotocoagulação/
		// Otorrino)
		List<AacAtendimentoApacs> atendimentoApacs = ambulatorioFacade.listarAtendimentosApacsPorCodigoPaciente(codigoOrigem);

		for (AacAtendimentoApacs apac : atendimentoApacs) {
			apac.setPacCodigo(codigoDestino);

			ambulatorioFacade.persistirAtendimentoApacs(apac);
		}
		this.flush();
	}

	@SuppressWarnings("ucd")
	protected void atualizarFatResumoApacs(Integer codigoOrigem,
			Integer codigoDestino) {
		IFaturamentoFacade faturamentoFacade = this.getFaturamentoFacade();
		
		// Resumo apacs. Tabela nova incluida para apacs otorrino.
		List<FatResumoApacs> resumos = faturamentoFacade.listarResumosApacsPorCodigoPaciente(codigoOrigem);

		for (FatResumoApacs resumo : resumos) {
			FatResumoApacsId id = resumo.getId();
			id.setPacCodigo(codigoDestino);

			faturamentoFacade.persistirFatResumoApacs(resumo); // talvez isso não funcione,
			// teremos que fazer via JPQL.
		}
		this.flush();
	}

	@SuppressWarnings("ucd")
	protected void atualizarAipLogProntOnlines(Integer codigoOrigem,
			Integer codigoDestino) {
		AipLogProntOnlinesDAO aipLogProntOnlinesDAO = this.getAipLogProntOnlinesDAO();
		AipPacientes pacDestino = this.getAipPacientesDAO().obterPorChavePrimaria(codigoDestino);
		
		// substituindo AIP_LOG_PRONT_ONLINES, caso exista.
		List<AipLogProntOnlines> prontuarios = aipLogProntOnlinesDAO.listarLogsProntOnlinesPorCodigoPaciente(codigoOrigem);

		for (AipLogProntOnlines prontuario : prontuarios) {
			
			prontuario.setPaciente(pacDestino);

			aipLogProntOnlinesDAO.atualizar(prontuario);
		}
		aipLogProntOnlinesDAO.flush();
	}

	@SuppressWarnings("ucd")
	protected void atualizarMptTratamentoTerapeuticos(Integer codigoOrigem,
			AipPacientes pacienteDestino) {
		IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade = this.getProcedimentoTerapeuticoFacade();
		
		List<MptTratamentoTerapeutico> terapeuticos = procedimentoTerapeuticoFacade.listarTratamentosTerapeuticosPorCodigoPaciente(codigoOrigem);

		for (MptTratamentoTerapeutico terapeutico : terapeuticos) {
			terapeutico.setPaciente(pacienteDestino);

			procedimentoTerapeuticoFacade.persistirMptTratamentoTerapeutico(terapeutico);
		}
		this.flush();
	}

	@SuppressWarnings("ucd")
	protected void atualizarMcoPartos(Integer codigoOrigem,
			Integer codigoDestino) {
		IPerinatologiaFacade perinatologiaFacade = this.getPerinatologiaFacade();
		
		List<McoPartos> partos = perinatologiaFacade.listarPartosPorCodigoPaciente(codigoOrigem);

		for (McoPartos parto : partos) {
			McoPartosId id = parto.getId();
			id.setPacCodigo(codigoDestino);

			perinatologiaFacade.persistirMcoPartos(parto); // talvez isso não funcione,
			// teremos que fazer via JPQL.
		}
		this.flush();
	}

	@SuppressWarnings("ucd")
	protected void atualizarAipMovimentacaoProntuarios(Integer codigoOrigem,
			Integer codigoDestino, RapServidores servidorLogado)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		AipMovimentacaoProntuarioDAO aipMovimentacaoProntuarioDAO = this.getAipMovimentacaoProntuarioDAO();
		
		AipPacientes paciente = this.getAipPacientesDAO().obterPorChavePrimaria(codigoDestino);
		
		List<AipMovimentacaoProntuarios> movimentacoes = aipMovimentacaoProntuarioDAO.listarMovimentacoesProntuariosPorCodigoPaciente(codigoOrigem);
		for (AipMovimentacaoProntuarios movimentacao : movimentacoes) {
			movimentacao.setPaciente(paciente);
			
			AipMovimentacaoProntuarios movimentacaoOld = new AipMovimentacaoProntuarios();
			this.getProntuarioRN().copiarPropriedades(movimentacaoOld, movimentacao);
			this.movimentacaoProntuarioRN.atualizarMovimentacaoProntuario(movimentacao, movimentacaoOld, servidorLogado);
		}
		aipMovimentacaoProntuarioDAO.flush();
	}

	@SuppressWarnings("ucd")
	protected void atualizarFatCandidatosApacOtorrino(Integer codigoOrigem,
			Integer codigoDestino) {
		IFaturamentoFacade faturamentoFacade = this.getFaturamentoFacade();
		
		List<FatCandidatosApacOtorrino> candidatos = faturamentoFacade.listarCandidatosApacOtorrinoPorCodigoPaciente(codigoOrigem);

		for (FatCandidatosApacOtorrino candidato : candidatos) {
			candidato.setPacCodigo(codigoDestino);

			faturamentoFacade.persistirFatCandidatosApacOtorrino(candidato);
		}
		this.flush();
	}

	@SuppressWarnings("ucd")
	protected void atualizarFatDadosContaSemInt(Integer codigoOrigem,
			Integer codigoDestino, String nomeComputador, Date dataFimVinculoServidor, RapServidores servidorLogado) throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException, BaseException {
		IFaturamentoFacade faturamentoFacade = this.getFaturamentoFacade();
		
		List<FatDadosContaSemInt> dados = faturamentoFacade.listarDadosContaSemIntPorCodigoPaciente(codigoOrigem);

		for (FatDadosContaSemInt contaSemInt : dados) {
			FatDadosContaSemInt contaSemIntOld = new FatDadosContaSemInt();
			this.getProntuarioRN().copiarPropriedades(contaSemInt, contaSemIntOld);
			contaSemInt.setPacCodigo(codigoDestino);
			getFaturamentoFacade().asuPosAtualizacaoStatement(contaSemIntOld, contaSemInt, nomeComputador, dataFimVinculoServidor, Boolean.TRUE, servidorLogado);
		}
		this.flush();
	}

	@SuppressWarnings("ucd")
	protected void atualizarScoSolicitacoesDeCompras(Integer codigoOrigem,
			Integer codigoDestino)
			throws BaseException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		ISolicitacaoComprasFacade solicitacaoComprasFacade = this.getSolicitacaoComprasFacade();
		IComprasFacade comprasFacade = this.getComprasFacade();
		
		List<ScoSolicitacaoDeCompra> compras = comprasFacade.listarSolicitacoesDeComprasPorCodigoPaciente(codigoOrigem);

		for (ScoSolicitacaoDeCompra compra : compras) {
			AipPacientes paciente = getAipPacientesDAO().obterPorChavePrimaria(codigoDestino);
			ScoSolicitacaoDeCompra compraOld = new ScoSolicitacaoDeCompra();
			this.getProntuarioRN().copiarPropriedades(compra, compraOld);
			compra.setPaciente(paciente);
			solicitacaoComprasFacade.atualizarScoSolicitacaoDeCompra(compra, compraOld);
		}
		this.flush();
	}

	@SuppressWarnings("ucd")
	protected void atualizarAipAlergiaPacientes(Integer codigoOrigem,
			Integer codigoDestino) {
		AipAlergiaPacientesDAO aipAlergiaPacientesDAO = this.getAipAlergiaPacientesDAO();
		
		List<AipAlergiaPacientes> alergias = aipAlergiaPacientesDAO.listarAlergiasPacientesPorCodigoPaciente(codigoOrigem);

		for (AipAlergiaPacientes alergia : alergias) {
			AipAlergiaPacientesId id = alergia.getId();
			id.setPacCodigo(codigoDestino);

			aipAlergiaPacientesDAO.persistir(alergia); // talvez isso não funcione,
			// teremos que fazer via
			// JPQL.
		}
		aipAlergiaPacientesDAO.flush();
	}

	@SuppressWarnings("ucd")
	protected void atualizarAipAcessoPacientes(Integer codigoOrigem,
			Integer codigoDestino) {
		AipAcessoPacientesDAO aipAcessoPacientesDAO = this.getAipAcessoPacientesDAO();
		
		List<AipAcessoPacientes> acessos = aipAcessoPacientesDAO.listarAcessosPacientesPorCodigoPaciente(codigoOrigem);

		for (AipAcessoPacientes acesso : acessos) {
			
			acesso.setPaciente(getAipPacientesDAO().obterOriginal(codigoDestino));

			aipAcessoPacientesDAO.persistir(acesso);
		}
		aipAcessoPacientesDAO.flush();
	}

	protected void atualizarRapPessoasFisicas(Integer codigoOrigem,
			AipPacientes pacDestino, Integer prontuarioDestino, RapServidores servidorLogado) throws ApplicationBusinessException {
		IRegistroColaboradorFacade registroColaboradorFacade = this.getRegistroColaboradorFacade();
		
		List<RapPessoasFisicas> pessoas = registroColaboradorFacade.listarPessoasFisicasPorCodigoPaciente(codigoOrigem);

		for (RapPessoasFisicas pessoa : pessoas) {
			pessoa.setAipPacientes(pacDestino);
			pessoa.setPacProntuario(prontuarioDestino);
			
			registroColaboradorFacade.alterar(pessoa, servidorLogado);
		}
		this.flush();
	}

	@SuppressWarnings("ucd")
	protected void atualizarRapDependentes(Integer codigoOrigem,
			AipPacientes pacDestino, Integer prontuarioDestino) {
		IRegistroColaboradorFacade registroColaboradorFacade = this.getRegistroColaboradorFacade();
		
		List<RapDependentes> dependentes = registroColaboradorFacade.listarDependentesPorCodigoPaciente(codigoOrigem);

		for (RapDependentes dependente : dependentes) {
			dependente.setPaciente(pacDestino);
			dependente.setPacProntuario(prontuarioDestino);
			
			registroColaboradorFacade.atualizarRapDependentes(dependente);
		}
		this.flush();
	}

	protected List<AacSisPrenatal> pesquisarAacSisPrenatal(Integer pacCodigo) {
		return this.getAmbulatorioFacade().pesquisarAacSisPrenatal(pacCodigo);
	}

	@SuppressWarnings("ucd")
	protected void atualizarAacSisPrenatal(Integer codigoOrigem,
			Integer codigoDestino) throws ApplicationBusinessException {
		try {
			IAmbulatorioFacade ambulatorioFacade = this.getAmbulatorioFacade();
			
			// FIXME Tocchetto Validar com a CGTI como tratar qdo já existe
			// outro registro AacSisPrenatal com o mesmo codigoDestino e seq.
			// Incrementar o seq e gravar?
			List<AacSisPrenatal> prenatais = pesquisarAacSisPrenatal(codigoOrigem);
			for (AacSisPrenatal prenatalOrigem : prenatais) {
				AacSisPrenatal prenatalDestino = new AacSisPrenatal();
				this.getProntuarioRN().copiarPropriedades(prenatalDestino,
						prenatalOrigem);
				AacSisPrenatalId id = new AacSisPrenatalId(codigoDestino,
						prenatalOrigem.getId().getSeqp());
				prenatalDestino.setId(id);

				ambulatorioFacade.persistirSisPrenatal(prenatalDestino);
				ambulatorioFacade.removerSisPrenatal(prenatalOrigem);
			}
			this.flush();
		} catch (Exception e) {
			ProntuarioONExceptionCode.SUBS_PRONT_0069.throwException(e);
		}
	}

	@SuppressWarnings("ucd")
	protected void atualizarMbcFichaAnestesias(Integer codigoOrigem, Integer codigoDestino, RapServidores servidorLogado) throws BaseException {
		
		IBlocoCirurgicoFacade blocoCirurgicoFacade = getBlocoCirurgicoFacade();
		
		// substituindo ficha de anestesia, caso exista, para pacientes que não
		// são gestantes (para gestantes ver aipp_subs_pac_gesta)
		List<MbcFichaAnestesias> fichas = blocoCirurgicoFacade.listarFichasAnestesiasPorCodigoPacienteComGsoPacCodigoNulo(codigoOrigem);

		for (MbcFichaAnestesias ficha : fichas) {
			AipPacientes paciente = this.getAipPacientesDAO().obterPorChavePrimaria(codigoDestino);
			ficha.setPaciente(paciente);

			blocoCirurgicoFacade.persistirMbcFichaAnestesias(ficha, servidorLogado);
		}
		
		this.flush();
		
	}

	@SuppressWarnings("ucd")
	protected void atualizarAelPacAgrpPesqExames(Integer codigoOrigem,
			Integer codigoDestino) {
		IExamesLaudosFacade examesLaudosFacade = this.getExamesLaudosFacade();
		
		// Atualizar o novo código apenas para axe_seq diferentes para não
		// violar a primary key
		List<Integer> criteriaResult = examesLaudosFacade.listarAxeSeqsPacAgrpPesq(codigoDestino);
		Integer[] axeSeqs = new Integer[criteriaResult.size()];
		for (int i = 0; i < criteriaResult.size(); i++) {
			axeSeqs[i] = (criteriaResult.get(i));
		}

		List<AelPacAgrpPesqExames> exames = examesLaudosFacade.listarPacAgrpPesqExames(codigoOrigem, axeSeqs);

		for (AelPacAgrpPesqExames exame : exames) {
			AelPacAgrpPesqExamesId id = exame.getId();
			id.setPacCodigo(codigoDestino);

			examesLaudosFacade.persistirAelPacAgrpPesqExames(exame); // talvez isso não funcione,
			// teremos que fazer via JPQL.
		}
		this.flush();

		List<AelPacAgrpPesqExames> examesRemover = examesLaudosFacade.listarPacAgrpPesqExamesPorCodigoPaciente(codigoOrigem);
		for (AelPacAgrpPesqExames exame : examesRemover) {
			examesLaudosFacade.removerAelPacAgrpPesqExames(exame, false);
		}
		this.flush();
	}

	/**
	 * Busca um paciente a partir do código.
	 * 
	 * @param codigo
	 * @return paciente encontrado.
	 */
	protected AipPacientes obterPacientePorCodigo(Integer codigo) {
		return this.getAipPacientesDAO().obterPorChavePrimaria(codigo);
	}

	protected List<AipPacHcpaXPacUbs> pesquisarAipPacHcpaXPacUbs(
			Integer pacCodigoHCPA, Integer pacCodigoUBS) {
		return this.getAipPacHcpaXPacUbsDAO().pesquisarAipPacHcpaXPacUbs(pacCodigoHCPA, pacCodigoUBS);
	}

	@SuppressWarnings("ucd")
	protected void atualizarAipPacHcpaXPacUbsPorPacCodigoHCPA(
			Integer codigoOrigem, Integer codigoDestino) {
		AipPacHcpaXPacUbsDAO aipPacHcpaXPacUbsDAO = this.getAipPacHcpaXPacUbsDAO();
		
		List<AipPacHcpaXPacUbs> lista = pesquisarAipPacHcpaXPacUbs(
				codigoOrigem, null);
		for (AipPacHcpaXPacUbs pac : lista) {
			AipPacHcpaXPacUbsId id = pac.getId();
			id.setPacCodigoHcpa(codigoDestino);
			aipPacHcpaXPacUbsDAO.persistir(pac);
		}
		aipPacHcpaXPacUbsDAO.flush();
	}

	@SuppressWarnings("ucd")
	protected void atualizarAipPacHcpaXPacUbsPorPacCodigoUBS(
			Integer codigoOrigem, Integer codigoDestino) {
		AipPacHcpaXPacUbsDAO aipPacHcpaXPacUbsDAO = this.getAipPacHcpaXPacUbsDAO();
		
		List<AipPacHcpaXPacUbs> lista = pesquisarAipPacHcpaXPacUbs(null,
				codigoOrigem);

		for (AipPacHcpaXPacUbs pac : lista) {
			AipPacHcpaXPacUbsId id = pac.getId();
			id.setPacCodigoUbs(codigoDestino);

			aipPacHcpaXPacUbsDAO.persistir(pac); // talvez isso não
			// funcione, teremos que
			// fazer via JPQL.
		}
		aipPacHcpaXPacUbsDAO.flush();
	}

	/**
	 * Implementação da procedure AIPP_SUBSTITUI_PRONT.
	 * 
	 * ORADB AIPP_SUBSTITUI_PRONT
	 * 
	 * @param prontuarioOrigem
	 * @param prontuarioDestino
	 * @param dtIdentificacaoOrigem
	 * @param dtIdentificacaoDestino
	 * @param codigoOrigem
	 * @param codigoDestino
	 * @throws BaseException 
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public void substituirProntuario(Integer prontuarioOrigem,
			Integer prontuarioDestino, Date dtIdentificacaoOrigem,
			Date dtIdentificacaoDestino, Integer codigoOrigem,
			Integer codigoDestino, String nomeMicrocomputador, RapServidores servidorLogado, final Date dataFimVinculoServidor) throws BaseException {
		try {
			AipPacientesDAO aipPacientesDAO = this.getAipPacientesDAO();
			
			// Incorporada regra de validacao da procedure qms$event em
			// AIPF_SUBST_PRONT.pll
			Boolean prontuariosModificados = CoreUtil.modificados(
					codigoOrigem, codigoDestino);
			if (!prontuariosModificados) {
				ProntuarioONExceptionCode.AIP_00098.throwException();
			}

			if (CoreUtil.modificados(codigoOrigem, codigoDestino)) {

				//#23337 - Verificar qual a relevância da FlagProntuario
				this.getProntuarioRN().setarFlagProntuario(
						FlagsSubstituiProntuario.AACK_CON_3_RN,
						ValorFlagsSubstituiProntuario.TRUE, servidorLogado != null ? servidorLogado.getUsuario() : null);

				//#23337 - Modificado
				//MÓDULO: IDENTIFICACAO DE PACIENTES
				verificarOutrasInformacoes(codigoOrigem, codigoDestino, nomeMicrocomputador, servidorLogado, dataFimVinculoServidor);

				//#23337 - Modificado
				//MÓDULO: IDENTIFICACAO DE PACIENTES
				excluiEnderecos(codigoOrigem, servidorLogado);

				AipPacientes pacOrigem = obterPacientePorCodigo(codigoOrigem);
				AipPacientes pacDestino = obterPacientePorCodigo(codigoDestino);

				//#23337 - Mantido
				atualizarAinAtendimentosUrgencia(codigoOrigem, codigoDestino, nomeMicrocomputador, servidorLogado, dataFimVinculoServidor);

				//#23337 - Mantido
				atualizarAinInternacao(codigoOrigem, codigoDestino, nomeMicrocomputador, servidorLogado, dataFimVinculoServidor);

				//#23337 - Mantido
				atualizarPacienteHospDia(codigoOrigem, pacDestino);

				//#23337 - Modificado (ajustado para chamar triggers de solicitação de internação)
				atualizarPacienteSolicInternacao(codigoOrigem, pacDestino, true);

				//#23337 - Mantido
				atualizarExtratoLeito(codigoOrigem, pacDestino);
				
				//#23337 - Bloco comentado - Não utilizado na versão 4
				atualizarCirurgias(codigoOrigem, pacDestino,servidorLogado);
				
				atualizarEcpHorarioControles(codigoOrigem, codigoDestino);
				
				removerFonemasPacienteSubstituido(pacOrigem, pacDestino);
				
				removerFonemasMaePacienteSubstituido(pacOrigem, pacDestino);
				
				//#23337 - Bloco comentado - Não utilizado na versão 4
				atualizarAgendas(codigoOrigem, pacDestino,servidorLogado);

				//#23337 - Mantido - substitui atendimentos cirúrgicos caso existam
				Boolean origemCirurgia = Boolean.TRUE;
				atualizarAghAtendimentos(codigoOrigem, pacDestino, prontuarioDestino, origemCirurgia, nomeMicrocomputador, servidorLogado, dataFimVinculoServidor);
				

				//#23337 - OK
				//MÓDULO: AGENDAMENTO E MARCACAO DE CONSULTAS
				//Aqui já são tratadas as tabelas MAM_PROC_REALIZADOS,
				//MAM_LOG_EM_USOS e MAM_NOTA_ADICIONAL_ANAMNESES
				atualizarConsultas(codigoOrigem, pacDestino, nomeMicrocomputador, servidorLogado, dataFimVinculoServidor);

				//#23337 - OK
				origemCirurgia = Boolean.FALSE;
				atualizarAghAtendimentos(codigoOrigem, pacDestino, prontuarioDestino, origemCirurgia, nomeMicrocomputador, servidorLogado, dataFimVinculoServidor);

				//#23337 - Bloco comentado - Não utilizado na versão 4
//				atualizarMciMvtoFatorPredisponentes(codigoOrigem,pacDestino);	
//				atualizarMciMvtoProcedimentoRiscos(codigoOrigem, pacDestino);
//				atualizarMciMvtoMedidaPreventivas(codigoOrigem, pacDestino);
//				atualizarMciMvtoInfeccaoTopografias(codigoOrigem,pacDestino);
//				atualizarFatContaApacs(codigoOrigem, pacDestino);

				//#23337 - Modificado (ajustado para chamar triggers de alta sumário)
				atualizarMpmAltaSumarios(codigoOrigem, pacDestino);
				
				//#23337 - Mantido
				atualizarAghAtendimentoPacientes(codigoOrigem, pacDestino);
				
				atualizarLaudoAih(codigoOrigem, pacDestino);
				
				//VERIFICAR

				//#23337 - Bloco comentado - Não utilizado na versão 4
//				atualizarAbsAmostrasPacientes(codigoOrigem, codigoDestino);
//				atualizarAbsEstoqueComponentes(codigoOrigem, pacDestino);
//				atualizarAbsMovimentosComponentes(codigoOrigem, pacDestino);
//				atualizarAbsRegSanguineoPacientes(codigoOrigem, pacDestino);
				// substituindo ABS_SOLICITACOES_DOACOES, caso exista.
//				atualizarAbsSolicitacoesDoacoes(codigoOrigem, codigoDestino);
				
				//#23337 - OK
				//MÓDULO: FATURAMENTO
				atualizarFatProcedAmbRealizados(codigoOrigem, pacDestino, nomeMicrocomputador, servidorLogado, dataFimVinculoServidor);
				
				//#23337 - OK
				atualizarAghAtendimentosPacExtern(codigoOrigem, pacDestino, nomeMicrocomputador);

				// Altera todas as tabelas que têm fk das tabelas de peso e
				// altura
				//#23337 - Mantido
				//MÓDULO: IDENTIFICACAO DE PACIENTES
				atualizarParamPesoMpt(codigoOrigem, codigoDestino);
				//#23337 - Mantido
				atualizarParamAlturaMpt(codigoOrigem, codigoDestino);
				//#23337 - Mantido
				//MÓDULO: IDENTIFICACAO DE PACIENTES
				atualizarParamPesoMpm(codigoOrigem, codigoDestino);
				//#23337 - Mantido
				atualizarParamAlturaMpm(codigoOrigem, codigoDestino);

				// substituindo fks da tabela mam_resposta_anamneses
				//#23337 - Será preciso implementar as triggers de update de MamRespostaAnamneses.
				//Não implementei porque esta tabela não possui dados em UFU. A tabela MamRespostaAnamneses, que é
				//utilizada neste método não possui dados na V1 de Ambulatório.
				//MÓDULO: AMBULATORIO
				this.getProntuarioRN().mampAtuPesoRea(codigoOrigem,
						codigoDestino);
				//this.getProntuarioRN().mampAtuAlturaRea(codigoOrigem,codigoDestino);

				// substituindo fks da tabela mam_resposta_evolucoes
				//#23337 - Será preciso implementar as triggers de update de MamRespostaEvolucoes.
				//Não implementei porque esta tabela não possui dados na V1 de Ambulatório.
				//MÓDULO: AMBULATORIO
				this.getProntuarioRN().mampAtuPesoRev(codigoOrigem,
						codigoDestino, servidorLogado);
				//this.getProntuarioRN().mampAtuAlturaRev(codigoOrigem,codigoDestino);
				//}

				//#23337 - OK
				//MÓDULO: IDENTIFICACAO DE PACIENTES
				atualizarAipPesoPacientes(codigoOrigem, codigoDestino);

				//#23337 - Bloco comentado - Não utilizado na V1 de Ambulatório
				//if (fluxoCompleto) {
				// substituindo AIP_PA_SIST_PACIENTES, caso exista
				//this.getProntuarioRN().aippSubsPacPip(codigoOrigem,codigoDestino);
				// substituindo AIP_PA_DIAST_PACIENTES, caso exista
				//this.getProntuarioRN().aippSubsPacPdp(codigoOrigem,codigoDestino);
				// substituindo AIP_PERIM_CEFALICO_PACIENTES, caso exista
				//this.getProntuarioRN().aippSubsPacPlp(codigoOrigem,codigoDestino);
				
				//#23337 - CRIADO -  substituindo AIP_PACIENTE_DADO_CLINICOS
				//MÓDULO: IDENTIFICACAO DE PACIENTES
				atualizarDadosClinicosPacientes(pacOrigem, pacDestino);
				
				// substituindo temporárias do ambulatório, caso existam
				//#23337 - Mantido
				this.getProntuarioRN().mampAtuPacTmp(codigoOrigem, codigoDestino);
				// substituindo FAT_PACIENTE_TRANSPLANTES, caso exista.
				//#23337 - Não utilizado na versão 4
				//this.getProntuarioRN().fatpSubsApacTrans(codigoOrigem,codigoDestino);
				
				//#23337 -  Bloco não utilizado na versão 4
				//atualizarFatPacienteTratamentos(codigoOrigem, codigoDestino);
				//atualizarAacAtendimentoApacs(codigoOrigem, codigoDestino);
				//atualizarFatResumoApacs(codigoOrigem, codigoDestino);
				atualizarAipLogProntOnlines(codigoOrigem, codigoDestino);
				//atualizarMptTratamentoTerapeuticos(codigoOrigem, pacDestino);
				//atualizarMcoPartos(codigoOrigem, codigoDestino);
				//}

				//#23337 - Modificado
				atualizarAipMovimentacaoProntuarios(codigoOrigem, codigoDestino, servidorLogado);

				//#23337 - Bloco comentado - Não utilizado em UFU
//				if (fluxoCompleto) {
				this.getProntuarioRN().setarFlagProntuario(
						FlagsSubstituiProntuario.FATK_DCS_RN,
						ValorFlagsSubstituiProntuario.S, servidorLogado != null ? servidorLogado.getUsuario() : null);
				//#23337 - Modificado - Chamando implementação das triggers
				atualizarFatCandidatosApacOtorrino(codigoOrigem, codigoDestino);
				//#23337 - Modificado - Chamando implementação das triggers
				atualizarFatDadosContaSemInt(codigoOrigem, codigoDestino, nomeMicrocomputador, dataFimVinculoServidor, servidorLogado);
				//#23337 - Verificar qual a relevância da FlagProntuario
				this.getProntuarioRN().setarFlagProntuario(
						FlagsSubstituiProntuario.FATK_DCS_RN,
						ValorFlagsSubstituiProntuario.N, servidorLogado != null ? servidorLogado.getUsuario() : null);
				//#23337 - Modificado - Chamando implementação das triggers
				atualizarScoSolicitacoesDeCompras(codigoOrigem, codigoDestino);
				//#23337 - Modificado. TODO: A trigger AIPT_ALP_BRU não está implementada, porém
				// sua implementação lançaria exception que impediria a substituição do prontuário.
				//verificar...
				atualizarAipAlergiaPacientes(codigoOrigem, codigoDestino);
				//#23337 - Mantido. TODO: Verificar se a inserção na tabela AIP_ACESSO_PACIENTES_JN
				//já deve ser implementada (trigger AIPT_ACX_ARU)
				atualizarAipAcessoPacientes(codigoOrigem, codigoDestino);
//				}

				//#23337 - TODO: OK, mas avaliar se a trigger RAPT_PES_ASU de RapPessoasFsicas
				//deve ser implementada. Esta trigger chama a procedure rapP_enforce_pes_rules que
				//trata registros da tabela RAP_FONEMAS_PESSOA_FISICA.
				//Não implementei agora porque a tabela RAP_FONEMAS_PESSOA_FISICA não possui dados em nenhum dos
				//HUs implantados
				//MÓDULO: ADMINISTRAÇÃO DE PESSOAL
				atualizarRapPessoasFisicas(codigoOrigem, pacDestino, prontuarioDestino, servidorLogado);

				//#23337 - Bloco comentado - Não utilizado nos HUs implantados
				//atualizarRapDependentes(codigoOrigem, pacDestino, prontuarioDestino);

				//#23337 - Mantido - Sem dados nos HUs implantados mas não possui triggers
				//TODO: verificar dúvida postada pelo Tocchetto no método
				atualizarAacSisPrenatal(codigoOrigem, codigoDestino);
				//#23337 - Comentado, não possui dados nos HUs implantados. TODO: Precisa
				//implementar trigger MBCT_FIC_BRU
				atualizarMbcFichaAnestesias(codigoOrigem, codigoDestino, servidorLogado);
				
				//#23337 - Mantido - não possui triggers
				atualizarAelPacAgrpPesqExames(codigoOrigem, codigoDestino);
				// inclui alteração de prontuario para paciente de triagem
				// de
				// emergencia
				
				//#23337 - Modificado. TODO: Falta implementar ainda triggers de update
				//de MamTrgAlergias e MamTriagens (estas tabelas ainda não possuem dados nos HUs)
				//MÓDULO: AMBULATÓRIO
				this.getProntuarioRN().mampSubstituiPront(codigoOrigem,codigoDestino);
				
				//#23337 - Bloco comentado - Não utilizado nos HUs implantados
				// SUBSTITUI PRONTUARIOS DE PACIENTES DE PROJETO DE PESQUISA
				//this.getProntuarioRN().rnPpjpSubstPront(codigoOrigem,codigoDestino);

				//#23337 - Modificado - triggers implementadas. Ainda não possui dados nos HUs
				procedureInsDelPiuPuf(codigoOrigem, codigoDestino, servidorLogado);
				
				//#23337 - Bloco comentado - Não utilizado nos HUs implantados
				//this.getProntuarioRN().procedureAippSubsPacGesta(codigoOrigem,codigoDestino);

				//#23337 - VERIFICAR SE PRECISA SER MANTIDO
				this.getCadastroPacienteFacade().inserirJournal(prontuarioOrigem, codigoDestino);

				//#23337 - Bloco comentado - Não utilizado nos HUs implantados
				//this.getProntuarioRN().aippSubstProntConv(prontuarioOrigem,prontuarioDestino);

				
				//#23337 - VERIFICAR SE PRECISA SER MANTIDO
				this.getProntuarioRN().setarFlagProntuario(
						FlagsSubstituiProntuario.AACK_CON_3_RN,
						ValorFlagsSubstituiProntuario.FALSE, servidorLogado != null ? servidorLogado.getUsuario() : null);

				// Incorporada regra de validacao do método ver_exclusao_ok em
				// AIPF_SUBST_PRONT.pll
				validarSubstituicaoProntuarios(codigoOrigem);

				aipPacientesDAO.flush();
				this.logInfo("Prontuario substituido com sucesso!");
			}
		} catch (Exception e) {
			this.logInfo("Falha ao substituir prontuario.");
			this.logError("Falha ao substituir prontuario.", e);
//			if (e instanceof BaseException) {
//				throw (BaseException) e;
//			}
			throw new BaseException(ProntuarioONExceptionCode.FALHA_SUBSTITUIR_PRONTUARIO);
		}
	}
	
	private void atualizarLaudoAih(Integer codigoOrigem, AipPacientes pacDestino) {
		List<MamLaudoAih> laudos = ambulatorioFacade.obterLaudosDoPaciente(codigoOrigem);
		for(MamLaudoAih laudo : laudos) {
			laudo.setPacCodigo(pacDestino.getCodigo());
			laudo.setPaciente(pacDestino);
			ambulatorioFacade.gravarMamLaudoAih(laudo);
		}
	}

	private void removerFonemasMaePacienteSubstituido(AipPacientes pacOrigem,
			AipPacientes pacDestino) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		List<AipFonemasMaePaciente> fonemasMaePaciente = getPacienteFacade().pesquisarFonemasMaePaciente(pacOrigem.getCodigo());
		for (AipFonemasMaePaciente fonemaMaePaciente : fonemasMaePaciente){
			getAipFonemasMaePacienteDAO().remover(fonemaMaePaciente);
		}
		getAipFonemaPacientesDAO().flush();
		
	}	
	


	private void removerFonemasPacienteSubstituido(AipPacientes pacOrigem, AipPacientes pacDestino) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		List<AipFonemaPacientes> fonemasPaciente = getPacienteFacade().pesquisarFonemasPaciente(pacOrigem.getCodigo());
		for (AipFonemaPacientes fonemaPaciente : fonemasPaciente){
			getAipFonemaPacientesDAO().remover(fonemaPaciente);
		}
		getAipFonemaPacientesDAO().flush();
	}	

	private AipFonemaPacientesDAO getAipFonemaPacientesDAO() {
		return aipFonemaPacientesDAO;
	}
	
	private AipFonemasMaePacienteDAO getAipFonemasMaePacienteDAO() {
		return aipFonemasMaePacienteDAO;
	}	

	protected void validarSubstituicaoProntuarios(Integer codigoPaciente)
			throws ApplicationBusinessException {
		AipPacientes aipPaciente = this.getAipPacientesDAO().obterPorChavePrimaria(codigoPaciente);
		if (!aipPaciente.getAghAtendimentos().isEmpty()) {
			ProntuarioONExceptionCode.FALHA_SUBSTITUIR_PRONTUARIO
					.throwException();
		}
	}

	/**
	 * METODOS RELATIVOS AO CRUD DE PASSIVAR PRONTUARIOS DE PACIENTES
	 */
	public void passivarProntuario(String secao, Date dthr,
			boolean incluiPassivos, Date ultimaExecucaoPassivarProntuario)
			throws ApplicationBusinessException {
		LOG.debug("LOGPPPP: CHAMADA PARA EXECUCAO SINCRONA");

		Date dataAtual = new Date();
		// Tempo registrado para 60 minutos
		int tempoIntervalo = 60 * 60 * 1000;
		
		Map<String,Object> parametros = new HashMap<>();
		
		parametros.put("SECAO", secao);
		parametros.put("DATA_HORA", dthr);
		parametros.put("INCLUIR_PASSIVOS", incluiPassivos);

		if (ultimaExecucaoPassivarProntuario == null
				|| ((ultimaExecucaoPassivarProntuario.getTime() + tempoIntervalo) < dataAtual.getTime())) {	
			ultimaExecucaoPassivarProntuario = DateUtil.adicionaMinutos(new Date(), 1);
		} else {
			ultimaExecucaoPassivarProntuario.setTime(ultimaExecucaoPassivarProntuario.getTime() + tempoIntervalo);			
		}
		
		LOG.debug("LOGPPPP: DEFINIDA DATA DE EXECUCAO" + ultimaExecucaoPassivarProntuario);
		this.schedulerFacade.agendarTarefa(JobEnum.PASSIVAR_PRONTUARIO,
				null, ultimaExecucaoPassivarProntuario,
				this.registroColaboradorFacade
						.obterServidorAtivoPorUsuario(this
								.obterLoginUsuarioLogado()), parametros);
	}

	/**
	 * Método auxiliar para obter o id do leito no relatório de prontuário
	 * identificados.
	 * 
	 * @param dtUltInternacao
	 * @param dtIdentificacao
	 * @param leitoId
	 * @return String
	 */
	protected static String getLeitoId(Date dtUltInternacao,
			Date dtIdentificacao, String leitoId) {

		Calendar calUltInternacao = null;

		if (dtUltInternacao != null) {
			calUltInternacao = Calendar.getInstance();
			calUltInternacao.setTime(DateUtils.truncate(dtUltInternacao,
					Calendar.DAY_OF_MONTH));
		}

		Calendar calIdentificacao = null;

		if (dtIdentificacao != null) {
			calIdentificacao = Calendar.getInstance();
			calIdentificacao.setTime(DateUtils.truncate(dtIdentificacao,
					Calendar.DAY_OF_MONTH));
		}

		Calendar calTest = Calendar.getInstance();

		if (calIdentificacao != null && calUltInternacao != null) {
			if (calUltInternacao.before(calIdentificacao)) {
				calTest.setTime(calUltInternacao.getTime());
			} else { // Maior ou igual.
				calTest.setTime(calIdentificacao.getTime());
			}
			if (calTest.equals(calIdentificacao)) {
				return leitoId;
			}
		}

		return null;

	}

	/**
	 * Retorna os prontuarios identificados. Método utilizado no relatório
	 * 'Prontuarios Identificados'.
	 */
	public List<Object> obterProntuariosIdentificados(Date dtInicial,
			Date dtFinal, Integer codigoAreaConsiderar,
			Integer codigoAreaDesconsiderar, boolean infAdicionais) {
		AipPacientesDAO aipPacientesDAO = this.getAipPacientesDAO();

		List<Object[]> res = aipPacientesDAO.obterDadosProntuariosIdentificados(dtInicial, dtFinal, codigoAreaConsiderar,
				codigoAreaDesconsiderar);

		// Criando lista de VO.
		List<Object> lista = new ArrayList<Object>(0);

		Iterator<Object[]> it = res.iterator();
		while (it.hasNext()) {
			Object[] obj = it.next();

			ProntuarioIdentificadoVO vo = new ProntuarioIdentificadoVO();

			// Tranforma número de 124567 para 12456/7
			vo.setCodigo(((Integer) obj[0]).toString());

			vo.setDtInicial(dtInicial);
			vo.setDtFinal(dtFinal);
			vo.setDescricao((String) obj[1]);

			// Tranforma número de 124567 para 12456/7
			String prontAux = ((Integer) obj[2]).toString();
			vo.setProntuario(prontAux.substring(0, prontAux.length() - 1) + "/"
					+ prontAux.charAt(prontAux.length() - 1));
			vo.setNome((String) obj[3]);

			Date dtUltInternacao = (Date) obj[4];
			Date dtIdentificacao = (Date) obj[5];
			String leitoId = (String) obj[6];
			vo.setLtoLtoId(getLeitoId(dtUltInternacao, dtIdentificacao, leitoId));

			// Campo aparentemente não utilizado no relatório.
			vo.setProntuario1(((Integer) obj[2]).toString());

			// Campo aparentemente não utilizado no relatório.
			vo.setDesprezar(null);

			vo.setCodigo2(((Integer) obj[7]).toString());
			
			if(infAdicionais){
				vo.setNomeMae((String) obj[8]);
				
				Date dataNascimento = (Date) obj[9];
				vo.setDtNascimento(DateFormatUtil.fomataDiaMesAno(dataNascimento));
			}

			lista.add(vo);
		}

		return lista;
	}

	/**
	 * Método para verificar se existem prontuarios pendentes de liberação.
	 * 
	 * ORADB Function AIPC_LIB_PRONTUARIO
	 *  Modificado para somente inserir o prontuário na tabela de liberados
	 * caso o parâmetro P_AGHU_PERMITE_REUSO_PRONTUARIO tenha valor 'S'
	 * 
	 * @param prontuario
	 * @return String 'T' ou 'F'
	 * @throws ApplicationBusinessException 
	 */
	public String verificarLiberacaoProntuario(Integer prontuario) throws ApplicationBusinessException {
		String retorno = "";
		
		AghParametrosVO aghParametroVO = new AghParametrosVO();
		aghParametroVO.setNome(AghuParametrosEnum.P_AGHU_PERMITE_REUSO_PRONTUARIO.toString());
		this.getParametroFacade().getAghpParametro(aghParametroVO);
		
		boolean permiteReusoProntuario = (aghParametroVO.getVlrTexto() != null && "S"
				.equalsIgnoreCase(aghParametroVO.getVlrTexto()));

		List<AipProntuarioLiberados> prontuarioList = this
				.pesquisarProntuariosLiberados(prontuario);

		if (prontuarioList == null || prontuarioList.size() == 0) {
			// Cria registro em AipProntuarioLiberados
			if (permiteReusoProntuario){
				this.persistirProntuariosLiberados(prontuario);				
			}
			retorno = "T";
		} else {
			retorno = "F";
		}

		return retorno;
	}

	/**
	 * Valida se a data inicial existe e se não é maior que a data final.
	 * 
	 * @param dtInicial
	 * @param dtFinal
	 * @throws ApplicationBusinessException
	 */
	public void validaDadosRelProntuarioIdent(Date dtInicial, Date dtFinal)
			throws ApplicationBusinessException {

		if (dtInicial == null) {
			// Busca em AGH_PARAMETRO
			AghParametrosVO aghParametroVO = new AghParametrosVO();
			aghParametroVO.setNome("P_DATA_REF_IDEN_PAC");

			this.getParametroFacade().getAghpParametro(aghParametroVO);

			Date dtInicialParam = aghParametroVO.getVlrData();

			if (dtInicialParam == null) {
				ProntuarioONExceptionCode.INFORMAR_DATA_INICIAL
						.throwException();
			}
		} else if (dtFinal != null) {
			if (dtInicial.after(dtFinal)) {
				ProntuarioONExceptionCode.DATA_INICIAL_MAIOR_DATA_FINAL
						.throwException();
			}
		}
	}

	/**
	 * Método para pesquisar os prontuários pendentes de liberação existentes,
	 * conforme parametro recebido.
	 * 
	 * @param prontuario
	 * @return
	 */
	protected List<AipProntuarioLiberados> pesquisarProntuariosLiberados(Integer prontuario) {
		return this.getAipProntuarioLiberadosDAO().pesquisarProntuariosLiberados(prontuario);
	}

	/**
	 * Método para remover todos os prontuarios pendentes de liberação
	 * existentes, conforme o parametro recebido.
	 * 
	 * @param prontuario
	 */
	protected void persistirProntuariosLiberados(Integer prontuario) {
		AipProntuarioLiberados prontuarioLiberados = new AipProntuarioLiberados();
		prontuarioLiberados.setProntuario(prontuario);

		this.getAipProntuarioLiberadosDAO().persistir(prontuarioLiberados);
		this.getAipProntuarioLiberadosDAO().flush();
	}
	
	
	
	
	
	
	protected void substituirProntuarioFamilia(AipPacientes pacOrigem,
			AipPacientes pacDestino)throws ApplicationBusinessException {
		
		AipGrupoFamiliarPacientes grupoFamiliarPacientesDestino = aipGrupoFamiliarPacientesDAO.obterPorChavePrimaria(pacDestino.getCodigo());
		AipGrupoFamiliarPacientes grupoFamiliarPacientesOrigem = aipGrupoFamiliarPacientesDAO.obterPorChavePrimaria(pacOrigem.getCodigo());
		// CASO 2
		if (grupoFamiliarPacientesDestino == null && grupoFamiliarPacientesOrigem != null) {
			Integer agfseq = grupoFamiliarPacientesOrigem.getAgfSeq();
				
			aipGrupoFamiliarPacientesDAO.removerPorId(pacOrigem.getCodigo());
			aipGrupoFamiliarPacientesDAO.flush();
			pacOrigem.setGrupoFamiliarPaciente(null);
						
			grupoFamiliarPacientesDestino = new AipGrupoFamiliarPacientes();
			grupoFamiliarPacientesDestino.setSeq(pacDestino.getCodigo());
			grupoFamiliarPacientesDestino.setAgfSeq(agfseq);
	
			aipGrupoFamiliarPacientesDAO.persistir(grupoFamiliarPacientesDestino);
			aipGrupoFamiliarPacientesDAO.flush();
			
		}// CASO 1
		else if (grupoFamiliarPacientesDestino != null && grupoFamiliarPacientesOrigem != null) {
			aipGrupoFamiliarPacientesDAO.removerPorId(pacOrigem.getCodigo());
			aipGrupoFamiliarPacientesDAO.flush();
			pacOrigem.setGrupoFamiliarPaciente(null);
			
		}
		
	}
	
	protected ICadastroPacienteFacade getCadastroPacienteFacade() {
		return (ICadastroPacienteFacade) cadastroPacienteFacade;
	}
	
	protected IInternacaoFacade getInternacaoFacade() {
		return this.internacaoFacade;
	}
	
	protected ISolicitacaoInternacaoFacade getSolicitacaoInternacaoFacade(){
		return this.solicitacaoInternacaoFacade;
	}

	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return (IParametroFacade) parametroFacade;
	}

	protected IComprasFacade getComprasFacade() {
		return (IComprasFacade) comprasFacade;
	}
	
	protected ISolicitacaoComprasFacade getSolicitacaoComprasFacade() {
		return (ISolicitacaoComprasFacade) solicitacaoComprasFacade;
	}
	
	protected IExamesFacade getExamesFacade() {
		return this.examesFacade;
	}

	protected ProntuarioRN getProntuarioRN() {
		return prontuarioRN;
	}
	
	protected IBancoDeSangueFacade getBancoDeSangueFacade() {
		return this.bancoDeSangueFacade;
	}

	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return this.ambulatorioFacade;
	}
	
	protected IExamesLaudosFacade getExamesLaudosFacade() {
		return this.examesLaudosFacade;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected AipAcessoPacientesDAO getAipAcessoPacientesDAO() {
		return aipAcessoPacientesDAO;
	}

	protected AipAlergiaPacientesDAO getAipAlergiaPacientesDAO() {
		return aipAlergiaPacientesDAO;
	}

	protected AipAlturaPacientesDAO getAipAlturaPacientesDAO() {
		return aipAlturaPacientesDAO;
	}

	protected AipEnderecosPacientesDAO getAipEnderecosPacientesDAO() {
		return aipEnderecosPacientesDAO;
	}

	protected AipLogProntOnlinesDAO getAipLogProntOnlinesDAO() {
		return aipLogProntOnlinesDAO;
	}

	protected AipMovimentacaoProntuarioDAO getAipMovimentacaoProntuarioDAO() {
		return aipMovimentacaoProntuarioDAO;
	}
	
	protected AipPacHcpaXPacUbsDAO getAipPacHcpaXPacUbsDAO() {
		return aipPacHcpaXPacUbsDAO;
	}
	
	protected AipPacientesDAO getAipPacientesDAO() {
		return aipPacientesDAO;
	}

	protected AipPacientesDadosCnsDAO getAipPacientesDadosCnsDAO() {
		return aipPacientesDadosCnsDAO;
	}

	protected AipPesoPacientesDAO getAipPesoPacientesDAO() {
		return aipPesoPacientesDAO;
	}

	protected AipProntuarioLiberadosDAO getAipProntuarioLiberadosDAO() {
		return aipProntuarioLiberadosDAO;
	}

	protected IFaturamentoFacade getFaturamentoFacade() {
		return this.faturamentoFacade;
	}

	protected IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}

	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade(){
		return this.prescricaoMedicaFacade;
	}
	
	protected IProcedimentoTerapeuticoFacade getProcedimentoTerapeuticoFacade() {
		return this.procedimentoTerapeuticoFacade;
	}

	protected IPerinatologiaFacade getPerinatologiaFacade() {
		return perinatologiaFacade;
	}
	
	protected IControleInfeccaoFacade getControleInfeccaoFacade() {
		return this.controleInfeccaoFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
