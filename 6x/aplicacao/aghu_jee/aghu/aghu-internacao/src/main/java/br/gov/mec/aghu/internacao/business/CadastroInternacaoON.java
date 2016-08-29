package br.gov.mec.aghu.internacao.business;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.OptimisticLockException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.vo.MbcFichaTipoAnestesiaVO;
import br.gov.mec.aghu.blococirurgico.vo.MbcProfAtuaUnidCirgsVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.configuracao.dao.AghCidDAO;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.business.moduleintegration.InactiveModuleException;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.commons.NumberUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.StringUtil;
import br.gov.mec.aghu.dao.ObjetosOracleDAO;
import br.gov.mec.aghu.dominio.DominioFatTipoCaractItem;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioIndRespProc;
import br.gov.mec.aghu.dominio.DominioMomentoAgendamento;
import br.gov.mec.aghu.dominio.DominioNaturezaFichaAnestesia;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioOrigemPacienteCirurgia;
import br.gov.mec.aghu.dominio.DominioPrioridadeCid;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.dominio.DominioSituacaoProjetoPesquisa;
import br.gov.mec.aghu.dominio.DominioTipoPlano;
import br.gov.mec.aghu.exameselaudos.business.IExamesLaudosFacade;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.cadastrosapoio.business.IFaturamentoApoioFacade;
import br.gov.mec.aghu.faturamento.dao.FatContasHospitalaresDAO;
import br.gov.mec.aghu.faturamento.vo.RnCthcVerDatasVO;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.dao.AinAtendimentosUrgenciaDAO;
import br.gov.mec.aghu.internacao.dao.AinCidsInternacaoDAO;
import br.gov.mec.aghu.internacao.dao.AinDiariasAutorizadasDAO;
import br.gov.mec.aghu.internacao.dao.AinInternacaoDAO;
import br.gov.mec.aghu.internacao.dao.AinResponsaveisPacienteDAO;
import br.gov.mec.aghu.internacao.dao.AinTiposCaraterInternacaoDAO;
import br.gov.mec.aghu.internacao.dao.ProcEfetDAO;
import br.gov.mec.aghu.internacao.leitos.exception.AGHULeitoConcurrencyException;
import br.gov.mec.aghu.internacao.responsaveispaciente.business.IResponsaveisPacienteFacade;
import br.gov.mec.aghu.internacao.solicitacao.business.ISolicitacaoInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.ConvenioPlanoVO;
import br.gov.mec.aghu.internacao.vo.ProfessorCrmInternacaoVO;
import br.gov.mec.aghu.internacao.vo.ValidaContaTrocaConvenioVO;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AelProjetoPesquisas;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghInstituicoesHospitalares;
import br.gov.mec.aghu.model.AghOrigemEventos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghProfEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinAtendimentosUrgencia;
import br.gov.mec.aghu.model.AinCidsInternacao;
import br.gov.mec.aghu.model.AinCidsInternacaoId;
import br.gov.mec.aghu.model.AinDiariasAutorizadas;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AinResponsaveisPaciente;
import br.gov.mec.aghu.model.AinTiposCaraterInternacao;
import br.gov.mec.aghu.model.AipConveniosSaudePaciente;
import br.gov.mec.aghu.model.AipEnderecosPacientes;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.CntaConv;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatContasInternacao;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatConvenioSaudePlanoId;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatItensProcedHospitalarId;
import br.gov.mec.aghu.model.FatTipoAih;
import br.gov.mec.aghu.model.MbcAnestesiaCirurgias;
import br.gov.mec.aghu.model.MbcAnestesiaCirurgiasId;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcFichaAnestesias;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgias;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgiasId;
import br.gov.mec.aghu.model.MbcProfCirurgias;
import br.gov.mec.aghu.model.MbcProfCirurgiasId;
import br.gov.mec.aghu.model.MbcSalaCirurgicaId;
import br.gov.mec.aghu.model.PacIntdConv;
import br.gov.mec.aghu.model.ProcEfet;
import br.gov.mec.aghu.model.RapQualificacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.cadastro.business.ICadastroPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;


/**
 * O propósito desta classe é prover os métodos de negócio para o cadastro de
 * internações.
 */
@SuppressWarnings({ "PMD.AghuTooManyMethods", "PMD.ExcessiveClassLength",
		"PMD.JUnit4TestShouldUseTestAnnotation", "PMD.CouplingBetweenObjects" })
@Stateless
public class CadastroInternacaoON extends BaseBusiness {

	private static final String EXCECAO_CAPTURADA = "Exceção capturada: ";

	private static final String ERRO = ". Erro: ";

	private static final String PAC_CODIGO = "PAC_CODIGO = ";

	@Inject
	private ObjetosOracleDAO objetosOracleDAO;
	
	private static final Log LOG = LogFactory.getLog(CadastroInternacaoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private FatContasHospitalaresDAO fatContasHospitalaresDAO;
	
	@Inject
	private AinInternacaoDAO ainInternacaoDAO;
	
	@EJB
	private IResponsaveisPacienteFacade responsaveisPacienteFacade;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@EJB
	private InternacaoRN internacaoRN;
	
	@Inject
	private AinAtendimentosUrgenciaDAO ainAtendimentosUrgenciaDAO;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@EJB
	private ISolicitacaoInternacaoFacade solicitacaoInternacaoFacade;
	
	@Inject
	private AinCidsInternacaoDAO ainCidsInternacaoDAO;
	
	@Inject
	private AinResponsaveisPacienteDAO ainResponsaveisPacienteDAO;
	
	@Inject
	private AghCidDAO aghCidDAO;
	
	@EJB
	private IFaturamentoApoioFacade faturamentoApoioFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IExamesLaudosFacade examesLaudosFacade;
	
	@Inject
	private AinTiposCaraterInternacaoDAO ainTiposCaraterInternacaoDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private ICadastroPacienteFacade cadastroPacienteFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private ProcEfetDAO procEfetDAO;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@Inject
	private AinDiariasAutorizadasDAO ainDiariasAutorizadasDAO;
				
	/**
	 * 
	 */
	private static final long serialVersionUID = 6113712634772861541L;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	/**
	 * Constante que guarda o nome do atributo do contexto referente ao sequence
	 * DAO
	 */
	
	
	/**
	 * Enumeracao com os codigos de mensagens de excecoes negociais do cadastro
	 * internacao.
	 * 
	 * Cada entrada nesta enumeracao deve corresponder a um chave no message
	 * bundle de internacao.
	 * 
	 * Porém se não houver uma este valor será enviado como mensagem de execeção
	 * sem localização alguma.
	 * 
	 * 
	 */
	private enum CadastroInternacaoONExceptionCode implements
			BusinessExceptionCode {
		AIP_USUARIO_NAO_CADASTRADO, INTERNACAO_INVALIDADA_TRIGGER, AIN_ESPECIALIDADE_OBRIGATORIA, AIN_CONVENIO_PLANO_OBRIGATORIO, 
		AIN_CRM_PROFESSOR_OBRIGATORIO, AIN_CARATER_INTERNACAO_OBRIGATORIO, AIN_ORIGEM_INTERNACAO_OBRIGATORIO, 
		AIN_CODIGO_FATURAMENTO_OBRIGATORIO, AIN_DATA_INTERNACAO_FUTURA, AIN_DATA_INTERNACAO_NULA, AIN_00304, AIN_00367, 
		AIN_00369, AIN_00365, CID_PRINCIPAL_EXIGE_SECUNDARIO, AIN_00301, AIN_00303, AIN_PACIENTE_SEM_PRONTUARIO, AIN_VINCULO_PROFESSOR_INVALIDO, 
		AIN_00433, AIN_00868, AIN_RESPONSAVEL_OBRIGATORIO, AIN_00822, AIN_00316, AIN_00317, AIN_00318, AIN_00654, AIN_00893, 
		AIN_EXISTE_PROCEDIMENTO_DT_INTERNACAO, AIN_00662, AIN_00834, AIN_00391, INTERNACAO_INVALIDADA_CONSTRAINT, 
		DATA_HORA_ANTERIOR_DATA_HORA_INTERNACAO_ADMINISTRATIVA, ERRO_ALTERACAO_CONVENIO_PROCEDIMENTO, ERRO_ALTERAR_CONVENIO_INTERNACAO, 
		AIN_DATA_OBRIGATORIA, ERRO_AINP_GERA_NOVA_CONTA, CONTA_CONVENIO_NAO_CADASTRADA, ERRO_CONTA_HOSPITALAR_ATIVA_INEXISTENTE, 
		AIN_PACIENTE_SEM_ENDERECO, LABEL_OPERACAO_INVALIDA_MODULO_FATURAMENTO_INATIVO, MENSAGEM_ERRO_HIBERNATE_VALIDATION, 
		USUARIO_NAO_SERVIDOR, AIN_00147, AIN_00146,PARAMETRO_INCONSISTENTE_PARTO_CESAREA, DATA_ALTA_MENOR_DATA_INTERNACAO_ADMINISTRATIVA
	}

	/**
	 * 
	 * @param codigo
	 * @return
	 */
	@Secure("#{s:hasPermission('tipoCaraterInternacao','pesquisar')}")
	public AinTiposCaraterInternacao obterTipoCaraterInternacao(final Integer codigo) {
		return this.getAinTiposCaraterInternacaoDAO().obterPorChavePrimaria(codigo);
	}

	/**
	 * Método que obtém um evento origem pelo seu id Utilizado na tela de
	 * internação.
	 * 
	 * @param seq
	 * @return AghOrigemEventos
	 */
	@Secure("#{s:hasPermission('origemEvento','pesquisar')}")
	public AghOrigemEventos obterOrigemEvento(final Short seq) {
		
		final AghOrigemEventos retorno = this.getAghuFacade().obterAghOrigemEventosPorChavePrimaria(seq);
		
		return retorno;
	}
	
	/**
	 * Método que obtém uma instituicao hospitalar pelo seu id Utilizado na tela
	 * de internação.
	 * 
	 * @param seq
	 * @return AghInstituicoesHospitalares
	 */
	@Secure("#{s:hasPermission('instituicaoHospitalar','pesquisar')}")
	public AghInstituicoesHospitalares obterInstituicaoHospitalar(final Integer seq) {
		
		final AghInstituicoesHospitalares retorno = this.getAghuFacade().obterAghInstituicoesHospitalaresPorChavePrimaria(seq);
		
		return retorno;
	}
	
	/**
	 * Método que obtém um cid internacao pelo seu id Utilizado na tela de
	 * internação.
	 * 
	 * @param seq
	 * @return AinCidsInternacao
	 */
	@Secure("#{s:hasPermission('internacao','pesquisar')}")
	public AinCidsInternacao obterCidInternacao(final Integer intSeq, final Integer cidSeq) {
		if (intSeq == null || cidSeq == null) {
			return null;
		}
		final AinCidsInternacaoId id = new AinCidsInternacaoId(intSeq, cidSeq);
		return getAinCidsInternacaoDAO().obterPorChavePrimaria(id);
	}
	
	/**
	 * Método responsável pela persistência de uma internação.
	 * 
	 * @dbtables RapQualificacao select
	 * @dbtables RapTipoQualificacao select
	 * @dbtables AghProfEspecialidades select
	 * @dbtables AghEspecialidades select
	 * @dbtables RapServidores select
	 * @dbtables RapPessoasFisicas select
	 * @dbtables RapQualificacao select
	 * @dbtables RapTipoQualificacao select
	 * @dbtables AghProfissionaisEspConvenio select
	 * @dbtables AinEscalasProfissionalInt select 
	 * 
	 * @param internacao
	 *            , listaResponsaveis, listaResponsaveisExcluidos
	 * @throws ApplicationBusinessException
	 */
	
	@Secure("#{s:hasPermission('internacao','alterar')}")
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public AinInternacao persistirInternacao(AinInternacao internacao,
			final List<AinCidsInternacao> cidsInternacao,
			final List<AinCidsInternacao> cidsInternacaoExcluidos,
			final List<AinResponsaveisPaciente> listaResponsaveis,
			final List<AinResponsaveisPaciente> listaResponsaveisExcluidos, 
			final String nomeMicrocomputador, final Date dataFimVinculoServidor,
			final Boolean substituirProntuario)
			throws BaseException {
		
		List<AinResponsaveisPaciente> listResponsavelPacienteOld = new LinkedList<>();
		for (AinResponsaveisPaciente responsavelPaciente: listaResponsaveis) {
			listResponsavelPacienteOld.add(	getAinResponsaveisPacienteDAO().obterOriginal(responsavelPaciente)
			);
		}
		
		LOG.info("Iniciando persistência da internação para paciente c/ código = "+ internacao.getPaciente().getCodigo());
		List<AinCidsInternacao> listaCidsInternacao = getAinCidsInternacaoDAO().pesquisarPorInternacao(internacao);
		for (final AinCidsInternacao cidInternacao : listaCidsInternacao) {
			LOG.info("cid: " + cidInternacao.getCid().getDescricao() + " prioridade: " + cidInternacao.getPrioridadeCid());
		}
		
		// Ajusta CIDs de transientes
		this.ajustarCidsInternacao(cidsInternacao);

		boolean isInclusao = false;		
		
		// TODO: RETIRAR ESTE MERGE ABAIXO QUANDO AS EXCEPTIONS COM ROLLBACK
		// FOREM
		//SUBSTITUÍDAS POR OUTRAS SEM ROLLBACK
		internacao.setPaciente(this.getPacienteFacade().atualizarAipPacientes(internacao.getPaciente(), false));
		
		/*--------MERGE DE PERSISTÊNCIA PARA ATENDIMENTO DE URGÊNCIA----*/
		// Para casos de internação de atendimento de urgência, após a
		// ocorrência de uma
		// exception de negócio, será preciso realizar o merge no objeto
		// atendimentoUrgencia
		AinAtendimentosUrgencia atendimentoUrgencia = internacao.getAtendimentoUrgencia();
		if (atendimentoUrgencia != null) {
			atendimentoUrgencia = this.getAinAtendimentosUrgenciaDAO().atualizar(atendimentoUrgencia);
			internacao.setAtendimentoUrgencia(atendimentoUrgencia);
		}
		/*--------------------------------------------*/

		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (servidorLogado == null) {
			// mudança para lançar exceção sem rollback gmneto 12/02/2010
			throw new ApplicationBusinessException(CadastroInternacaoONExceptionCode.AIP_USUARIO_NAO_CADASTRADO);
		}
		try {
			LOG.info("Validando dthr internação, endereço paciente, campos obrigatórios, professor cônvenio especialidade, aviso SAMIS. PAC_CODIGO = "
							+ internacao.getPaciente().getCodigo());
			validarDataInternacao(internacao.getDthrInternacao());
			this.validarEnderecoPaciente(internacao.getPaciente());
			this.validarCamposObrigatorios(internacao);
			this.validarProfessorConvenioEspecialidade(internacao);
			this.verificarAvisoSamis(internacao);
			
			Integer sizeListaResp = 0;
			if (listaResponsaveis != null){
				sizeListaResp = listaResponsaveis.size();
			}
			
			if (internacao.getSeq() == null) {
				// inclusão
				isInclusao = true;
				LOG.info("Incluindo internação. PAC_CODIGO = "+ internacao.getPaciente().getCodigo());
				internacao = this.internarPaciente(internacao, cidsInternacao,
						listaResponsaveis, sizeListaResp, nomeMicrocomputador, dataFimVinculoServidor, substituirProntuario);
			} else {
				// edição
				LOG.info("Editando internação. INT_SEQ = "	+ internacao.getSeq());
				internacao = this.editarInternacao(internacao, cidsInternacao,
						listaResponsaveis, sizeListaResp, nomeMicrocomputador, dataFimVinculoServidor);
				this.flush();
			}
			
			if (listaResponsaveis != null) {
				LOG.info("Chamando atualização lista de responsáveis. INT_SEQ = "+ internacao.getSeq());
				this.getResponsaveisPacienteFacade().atualizarListaResponsaveisPaciente(
						listaResponsaveis, listaResponsaveisExcluidos,internacao, listResponsavelPacienteOld);
				this.flush();
			}
			
			LOG.info("Chamando atualização do sexo de ocupação do quarto. INT_SEQ = "+ internacao.getSeq());
			
			this.atualizarSexoOcupacaoQuarto(internacao);
			if (cidsInternacao != null){
				LOG.info("Chamando atualização lista de CIDs. INT_SEQ = "+internacao.getSeq());
				atualizarListaCids(internacao, cidsInternacao, cidsInternacaoExcluidos, nomeMicrocomputador);
				this.flush();				
			}
			
		} catch (final AGHULeitoConcurrencyException exLeitoConcurrency) {
			ajustarInternacao(isInclusao, internacao);
			//internacao.setDthrInternacao(new Date());
			throw exLeitoConcurrency;
		} catch(final ApplicationBusinessException e) {
			LOG.error(PAC_CODIGO+internacao.getPaciente().getCodigo()+ERRO+ e.getMessage(), e);
			ajustarInternacao(isInclusao, internacao);
			throw e;
		} catch(final BaseRuntimeException aghume) {
			LOG.error(PAC_CODIGO+internacao.getPaciente().getCodigo()+ERRO+ aghume.getMessage(), aghume);
			ajustarInternacao(isInclusao, internacao);
			String erro = "";
			if (aghume.getCode() != null){
				erro = aghume.getCode().toString();
			}
			throw new ApplicationBusinessException(
					CadastroInternacaoONExceptionCode.INTERNACAO_INVALIDADA_CONSTRAINT,
					erro);
		} catch (final ConstraintViolationException ise) {
//			final BaseListException erros = new BaseListException();
//			for (final InvalidValue message : e.getInvalidValues()) {
//				erros.add(new ApplicationBusinessException(CadastroInternacaoONExceptionCode.MENSAGEM_ERRO_HIBERNATE_VALIDATION, message.getMessage()));
//	        }  			
//			throw erros;
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
					CadastroInternacaoONExceptionCode.MENSAGEM_ERRO_HIBERNATE_VALIDATION,
					mensagem);
		
		}catch(final OptimisticLockException e){
			LOG.error(PAC_CODIGO+internacao.getPaciente().getCodigo()+ERRO+ e.getMessage(), e);
			ajustarInternacao(isInclusao, internacao);
			throw e;
		}catch(final Exception e) {
			LOG.error(PAC_CODIGO+internacao.getPaciente().getCodigo()+ERRO+ e.getMessage(), e);
			ajustarInternacao(isInclusao, internacao);
			throw new ApplicationBusinessException(
					CadastroInternacaoONExceptionCode.INTERNACAO_INVALIDADA_TRIGGER,
					e.getMessage());
		}
			
		return internacao;

	}
	
	/**
	 * Ajusta CIDs de transientes/não persistentes para as validações futuras
	 * @param cidsInternacao
	 */
	private void ajustarCidsInternacao(final List<AinCidsInternacao> cidsInternacao){
		for (int i = 0; i < cidsInternacao.size(); i++) {
			AinCidsInternacao cidInternacao = cidsInternacao.get(i);
			AghCid cid = getAghuFacade().obterAghCidPorChavePrimaria(cidInternacao.getCid().getSeq());
			cidInternacao.setCid(cid);
		}
	}
	
	private void ajustarInternacao(final boolean isInclusao, final AinInternacao internacao) {
		if (isInclusao) {
			internacao.setSeq(null);
			internacao.setIndPacienteInternado(null);
		}
	}
	
	/**
	 * Método que atualiza o sexo de ocupação do quarto
	 * 
	 * @param internacao
	 *  
	 */
	private void atualizarSexoOcupacaoQuarto(final AinInternacao internacao)
			throws ApplicationBusinessException {
		
		AinQuartos quarto = null;
		if (internacao.getLeito() != null){
			AinLeitos leito = cadastrosBasicosInternacaoFacade.obterLeitoPorId(internacao.getLeito().getLeitoID());			
			quarto = leito.getQuarto();
			
		} else if (internacao.getQuarto() != null) {
			quarto = internacao.getQuarto();
		}
		
		if (internacao.getQuarto()!=null){
			quarto = cadastrosBasicosInternacaoFacade.obterQuarto(internacao.getQuarto().getNumero());
		}	
		
		if (quarto != null){
			if (DominioSimNao.S.equals(quarto.getIndConsSexo())
					&& internacao.getPaciente().getSexo() != null) {
				if (quarto.getSexoOcupacao() == null){
					quarto.setSexoOcupacao(internacao.getPaciente().getSexo());
					getCadastrosBasicosInternacaoFacade().persistirQuarto(quarto, null);
				}
			}
			
		}
	}
	
	/**
	 * Método que atualiza a conta hospitalar da internação quando a acomodação
	 * for informada
	 * 
	 * @param contaHospitalar
	 *  
	 */
	@Secure("#{s:hasPermission('contaHospitalar','alterar')}")
	public void atualizarContaHospitalar(final FatContasHospitalares contaHospitalar) {
		if (contaHospitalar.getAcomodacao() != null) {
			this.getFaturamentoFacade().atualizarFatContasHospitalares(contaHospitalar);
		}
	}
	
	/**
	 * Método que valida quando o CNRAC deve ser informado para paciente com
	 * endereço em outro estado
	 * 
	 * @param internacao
	 *  
	 */
	public boolean validarPacienteOutroEstado(final AipPacientes paciente,
			final Integer iphSeq, final Short iphPhoSeq) throws ApplicationBusinessException {
		boolean retorno = true;
		
		final AipEnderecosPacientes enderecoResidencial = this.getCadastroPacienteFacade()
				.obterEnderecoResidencialPaciente(paciente);
		
		final FatItensProcedHospitalarId id = new FatItensProcedHospitalarId();
		id.setSeq(iphSeq);
		id.setPhoSeq(iphPhoSeq);
		
		final FatItensProcedHospitalar procedimento = this.obterFatItemProcedHosp(id);
		//Obtém a UF sede do HU
		final String ufSede = obterUfSedeHU();
		
		if (enderecoResidencial == null
				|| enderecoResidencial.getUfEndereco() == null
				|| !enderecoResidencial.getUfEndereco()
						.equalsIgnoreCase(ufSede)) {
			if (procedimento.getExigeCnrac() != null
					&& procedimento.getExigeCnrac()) {
				if (!buscarCnrac(paciente.getCodigo())){
					retorno = false;
				}
			}	
		}
		
		return retorno;
	}
	
	/**
	 * Método que obtém a UF correspondente ao HU
	 * 
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public String obterUfSedeHU() throws ApplicationBusinessException{
		final AghuParametrosEnum parametroEnum = AghuParametrosEnum.P_AGHU_UF_SEDE_HU;
		final AghParametros aghParametroUfSede = this.getParametroFacade()
				.buscarAghParametro(parametroEnum);
		final String ufSede = aghParametroUfSede.getVlrTexto().trim();
		return ufSede;
	}
	
	
	/**
	 * Método que verifica se deve emitir o aviso ao Samis e atribui a data em
	 * caso afirmativo.
	 * 
	 * @param internacao
	 */
	private void verificarAvisoSamis(final AinInternacao internacao){
		
		if (internacao.getEnvProntUnidInt()){
			if (internacao.getDataHoraAvisoSamis() == null){
				internacao.setDataHoraAvisoSamis(new Date());
			}
		} else {
			if (internacao.getDataHoraAvisoSamis() != null){
				internacao.setDataHoraAvisoSamis(null);
			}
		}
	}
	
	
	/**
	 * Verifica se o paciente tem o endereço residencial
	 */
	public void validarEnderecoPaciente(final AipPacientes paciente)
			throws ApplicationBusinessException {
		final AipEnderecosPacientes enderecoResidencial = this.getCadastroPacienteFacade()
				.obterEnderecoResidencialPaciente(paciente);
		if (enderecoResidencial == null){
			//mudança para lançar exceção sem rollback gmneto 12/02/2010
			throw new ApplicationBusinessException(
					CadastroInternacaoONExceptionCode.AIN_PACIENTE_SEM_ENDERECO);
		}
	}
	
	/**
	 * ORADB ainc_busca_cnrac Método que verifica se uma internação possui CNRAC
	 * 
	 * @return boolean
	 *  
	 */
	private boolean buscarCnrac(final Integer pacCodigo){
		
		boolean retorno = false;
		
		final List<String> listaCnrac = getAinDiariasAutorizadasDAO().pesquisarCnracPorPaciente(pacCodigo);		
		if (listaCnrac.size() > 0){
			final String cnrac = listaCnrac.get(0);
			if (cnrac != null && !cnrac.equals("0")){
				retorno = true;
			}
		}
		return retorno;
	}
	
	/**
	 * Método que valida o CRM Professor associado juntamente com a
	 * especialidade e o convênio
	 * 
	 * @dbtables AghProfEspecialidades select
	 * @dbtables AghEspecialidades select
	 * @dbtables RapServidores select
	 * @dbtables RapPessoasFisicas select
	 * @dbtables RapQualificacao select
	 * @dbtables RapTipoQualificacao select
	 * @dbtables AghProfissionaisEspConvenio select
	 * @dbtables AinEscalasProfissionalInt select
	 * 
	 * @param internacao
	 */
	private void validarProfessorConvenioEspecialidade(final AinInternacao internacao)
			throws ApplicationBusinessException {
		final List<ProfessorCrmInternacaoVO> listaProfessores = this
				.pesquisarProfessoresCrm(
						internacao.getEspecialidade().getSeq(), internacao
								.getEspecialidade().getSigla(),
						internacao.getConvenioSaudePlano().getId()
								.getCnvCodigo(), internacao
								.getConvenioSaudePlano().getConvenioSaude()
								.getVerificaEscalaProfInt(), null, internacao
								.getServidorProfessor().getId().getMatricula(),
						internacao.getServidorProfessor().getId()
								.getVinCodigo());
		
		if (listaProfessores.size() == 0){
			throw new ApplicationBusinessException(
					CadastroInternacaoONExceptionCode.AIN_00433);
		}
	}
	
	/**
	 * Método delegado para a RN de Solicitação Internação
	 * 
	 * @param cidSeq
	 * @return listaCodTabelas
	 */
	public List<Long> pesquisarFatAssociacaoProcedimentos(final Integer cidSeq){
		return this.getSolicitacaoInternacaoFacade()
				.pesquisarFatAssociacaoProcedimentos(cidSeq);
	}

	
	/**	
     * Método responsável por atualizar a lista de CIDs de uma internação.
     * 
	 * @param cidsInternacao
	 *            , cidsInternacaoExcluidos
	 *  
	 */
	private void atualizarListaCids(final AinInternacao internacao, final List<AinCidsInternacao> cidsInternacao, final List<AinCidsInternacao> cidsInternacaoExcluidos, String nomeMicrocomputador) throws ApplicationBusinessException{
		try {
			for (final AinCidsInternacao cidInternacao : cidsInternacaoExcluidos) {
				if (cidInternacao.getId().getIntSeq() != null) {
					excluirCidInternacao(cidInternacao);
				}
			}

			for (final AinCidsInternacao cidInternacao : cidsInternacao) {
				if (cidInternacao.getId().getIntSeq() == null) {
					cidInternacao.getId().setIntSeq(internacao.getSeq());
					//Obtém novamente a internação para não dar erro
					//de objeto transiente caso contenha mais de um CID
					cidInternacao.setInternacao(getInternacaoFacade()
							.obterInternacao(internacao.getSeq()));
					incluirCidInternacao(cidInternacao, nomeMicrocomputador);
				}else{
					excluirCidInternacao(cidInternacao);
					cidInternacao.getId().setIntSeq(internacao.getSeq());
					cidInternacao.setInternacao(getInternacaoFacade()
							.obterInternacao(internacao.getSeq()));
					incluirCidInternacao(cidInternacao, nomeMicrocomputador);
					
				}
			}
		} catch (final InactiveModuleException e) {
			LOG.error("Exceção InactiveModuleException capturada, lançada para cima.");
			throw new InactiveModuleException(e.getMessage());
		} catch (final Exception e) {
			LOG.error(e.getMessage(),e);
			//TODO ver isso aqui depois! não vai funfar!
			for (final AinCidsInternacao cidInternacao : cidsInternacao) {
				if (this.obterCidInternacao(cidInternacao.getId().getIntSeq(),
						cidInternacao.getId().getCidSeq()) == null) {
					cidInternacao.getId().setIntSeq(null);					
				}
			}
		}
	}
	
	 /**	
     * Método responsável por incluir um novo cid na internação
     * 
     * @param cidInternacao
	 * @throws BaseException 
     */
	private void incluirCidInternacao(final AinCidsInternacao cidInternacao, String nomeMicrocomputador)
			throws BaseException {
		AinCidsInternacaoDAO ainCidsInternacaoDAO = this.getAinCidsInternacaoDAO();
		ainCidsInternacaoDAO.persistir(cidInternacao);
		ainCidsInternacaoDAO.flush();
		internacaoRN.executarTriggersCid(cidInternacao,
				DominioOperacoesJournal.INS, nomeMicrocomputador, new Date());
		
	}

	/**
	 * Método responsável por excluir um cid da internação
	 * 
	 * @param cidInternacao
	 */
	private void excluirCidInternacao(AinCidsInternacao cidInternacao) {
		// TODO Aqui deverá ser chamado um método da RN que deverá aplicar as
		// validações de exclusão
		// das triggers de banco da tabela AIN_CIDS_INTERNACAO
		// ...
		AinCidsInternacaoDAO ainCidsInternacaoDAO = this.getAinCidsInternacaoDAO();
		AinCidsInternacao item = ainCidsInternacaoDAO.obterPorChavePrimaria(cidInternacao.getId());
		ainCidsInternacaoDAO.remover(item);
		ainCidsInternacaoDAO.flush();
	}
	
	/**
	 * Método responsável pelas validações dos CIDs associados à internação
	 * 
	 * @param internacao
	 * @throws ApplicationBusinessException
	 */
      @SuppressWarnings("PMD.NPathComplexity")
	private void validarRegrasCids(final DominioSimNao indCaraterCidSeq,
			final List<AinCidsInternacao> cidsInternacao)
			throws ApplicationBusinessException {
		// Mudança para lançar Exceção semm rollback gmneto 12/02/2010
		boolean cidPrincipalInformado = false;
		boolean cidSecundarioInformado = false;
		String cidInicialSecundario = null;
		String cidFinalSecundario = null;
		DominioSimNao vIndCaraterCidSec = null;

		String cidSecundario = null;

		for (AinCidsInternacao cidInternacao: cidsInternacao){
			
			AghCid cid = cidInternacao.getCid();
			
			if (DominioPrioridadeCid.P.equals(cidInternacao.getPrioridadeCid())) {
				
				if (!cidPrincipalInformado){
					cidPrincipalInformado = true;
					//Realiza Merge em caso de Exception
					if (cidInternacao.getId().getIntSeq() != null) {
						cid = aghCidDAO.merge(cid);
					}
					vIndCaraterCidSec = cid.getGrupoCids()
							.getCapituloCid().getIndExigeCidSecundario();
				} else {
					throw new ApplicationBusinessException(
							CadastroInternacaoONExceptionCode.AIN_00367);
				}
				
				if (cid.getGrupoCids().getCapituloCid()
						.getIndDiagPrincipal().equals(DominioSimNao.N)) {
					throw new ApplicationBusinessException(
							CadastroInternacaoONExceptionCode.AIN_00304);
				}

				if (cid.getCidInicialSecundario() != null) {
					cidInicialSecundario = cidInternacao.getCid()
							.getCidInicialSecundario();
				}
				if (cid.getCidFinalSecundario() != null) {
					cidFinalSecundario = cid.getCidFinalSecundario();
				}
			} else if (DominioPrioridadeCid.S.equals(cidInternacao.getPrioridadeCid())) {
				
				if (!cidSecundarioInformado){
					cidSecundarioInformado = true;
				} else {
					throw new ApplicationBusinessException(
							CadastroInternacaoONExceptionCode.AIN_00369);
				}
				cidSecundario = cid.getCodigo();
			}
			this.validarSubCid(cid.getCodigo());
		}
		
		if (!cidPrincipalInformado){
			throw new ApplicationBusinessException(
					CadastroInternacaoONExceptionCode.AIN_00365);
		}
		
		if (cidInicialSecundario != null && cidFinalSecundario != null){
			if ((cidSecundario == null)
					|| (cidSecundario.compareTo(cidInicialSecundario) < 0 || cidSecundario
							.compareTo(cidFinalSecundario) > 0)) {
				throw new ApplicationBusinessException(
						CadastroInternacaoONExceptionCode.CID_PRINCIPAL_EXIGE_SECUNDARIO,
						cidInicialSecundario, cidFinalSecundario);
			}
		}
		
		if (cidSecundarioInformado){
			if (DominioSimNao.N.equals(vIndCaraterCidSec)){
				throw new ApplicationBusinessException(
						CadastroInternacaoONExceptionCode.AIN_00301);
			}
		} else {
			if (DominioSimNao.S.equals(indCaraterCidSeq)){
				throw new ApplicationBusinessException(
						CadastroInternacaoONExceptionCode.AIN_00303);
			}
		}
	}
	
	/**
	 * Método responsável por validações na data de internação
	 * 
	 * @param dtInternacao
	 * @throws ApplicationBusinessException
	 */
	private void validarDataInternacao(final Date dtInternacao)
			throws ApplicationBusinessException {
		
		if (dtInternacao == null){
			// mudança para lançar exceção sem rollback gmneto 12/02/2010
			throw new ApplicationBusinessException(
					CadastroInternacaoONExceptionCode.AIN_DATA_INTERNACAO_NULA);
		}
		
		if (dtInternacao.after(new Date())){
			// mudança para lançar exceção sem rollback gmneto 12/02/2010
			throw new ApplicationBusinessException(
					CadastroInternacaoONExceptionCode.AIN_DATA_INTERNACAO_FUTURA);
		}
	}
	
	/**
	 * Método responsável por validar campos obrigatórios na internação de
	 * paciente.
	 * 
	 * @param internacao
	 * @throws ApplicationBusinessException
	 */
	private void validarCamposObrigatorios(final AinInternacao internacao)
			throws ApplicationBusinessException {
		//mudanças para lançar exceção sem rollback gmneto 12/02/2010
		
		if (internacao.getEspecialidade() == null){
			throw new ApplicationBusinessException(
					CadastroInternacaoONExceptionCode.AIN_ESPECIALIDADE_OBRIGATORIA);
		}
		
		if (internacao.getConvenioSaudePlano() == null){
			throw new ApplicationBusinessException(
					CadastroInternacaoONExceptionCode.AIN_CONVENIO_PLANO_OBRIGATORIO);
		}
		
		if (internacao.getServidorProfessor() == null){
			throw new ApplicationBusinessException(
					CadastroInternacaoONExceptionCode.AIN_CRM_PROFESSOR_OBRIGATORIO);
		}
		
		if (internacao.getTipoCaracterInternacao() == null){
			throw new ApplicationBusinessException(
					CadastroInternacaoONExceptionCode.AIN_CARATER_INTERNACAO_OBRIGATORIO);
		}
		
		if (internacao.getOrigemEvento() == null){
			throw new ApplicationBusinessException(
					CadastroInternacaoONExceptionCode.AIN_ORIGEM_INTERNACAO_OBRIGATORIO);
		}
		
		if (internacao.getIphSeq() == null || internacao.getIphPhoSeq() == null){
			throw new ApplicationBusinessException(
					CadastroInternacaoONExceptionCode.AIN_CODIGO_FATURAMENTO_OBRIGATORIO);
		}
	}

	/**
	 * Método responsável pela geração do Sequencial para o Id de uma internação
	 * de paciente
	 * 
	 * ORADB: AINK_INT_ATU.ATUALIZA_SEQ_INTERNACAO (não foi colocada em uma RN,
	 * pois já estava implementada na ON e é uma operação simples para setar o
	 * Id de Internação)
	 * 
	 * @param paciente
	 */
	public void obterValorSequencialId(final AinInternacao internacao) {
		internacao.setSeq(this.getAinInternacaoDAO().obterSequencialIdInternacao(internacao));
	}
	
	/**
	 * Método que realiza a internação do paciente.
	 * 
	 * @param internacao
	 * @param cidsInternacao
	 * @param sizeListaResp
	 * @throws ApplicationBusinessException
	 */
	private AinInternacao internarPaciente(final AinInternacao internacao,
			final List<AinCidsInternacao> cidsInternacao,
			final List<AinResponsaveisPaciente> listaResponsaveis,
			final Integer sizeListaResp, String nomeMicrocomputador, final Date dataFimVinculoServidor,
			final Boolean substituirProntuario)
			throws BaseException {
		//Mudança para lançar BaseException gmneto 12/02/2010
		LOG.info("Validando especialidade grupo emergência. PAC_CODIGO = "+internacao.getPaciente().getCodigo());
		this.validarEspecialidadeGrupoEmergencia(internacao);
		LOG.info(
				"Consistindo clínica especialidade. PAC_CODIGO = "
						+ internacao.getPaciente().getCodigo());
		this.consistirClinicaEspecialidade(internacao);
		
		if (cidsInternacao != null){
			LOG.info(
					"Validando regras de CIDs. PAC_CODIGO = "
							+ internacao.getPaciente().getCodigo());
			validarRegrasCids(internacao.getTipoCaracterInternacao()
					.getIndCaraterCidSec(), cidsInternacao);
		}
		
		if (listaResponsaveis != null){
			LOG.info(
					"Validando regras dos responsáveis do paciente. PAC_CODIGO = "
							+ internacao.getPaciente().getCodigo());
			this.getResponsaveisPacienteFacade().validarRegrasResponsaveisPaciente(
					listaResponsaveis, internacao);
		}
		
		if (sizeListaResp == 0){
	   		if (internacao.getPaciente().getIdade() < 18){
				throw new ApplicationBusinessException(
						CadastroInternacaoONExceptionCode.AIN_RESPONSAVEL_OBRIGATORIO);
	   		}
   	 	}
		
		if (internacao.getPaciente().getProntuario() == null){
			throw new ApplicationBusinessException(
					CadastroInternacaoONExceptionCode.AIN_PACIENTE_SEM_PRONTUARIO);
		}

		if(internacao.getServidorDigita() == null) {
            RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
            internacao.setServidorDigita(servidorLogado);
        }
		
		this.obterValorSequencialId(internacao);
		
		//CHAMADA DAS TRIGGERS DE INSERT
		this.getInternacaoFacade().inserirInternacao(internacao, nomeMicrocomputador, dataFimVinculoServidor, substituirProntuario);
		
		this.getAinInternacaoDAO().persistir(internacao);
		
		LOG.info("Finalizando internação. INT_SEQ = " + internacao.getSeq());
		
		return internacao;		
	}
	
	/**
	 * Método que realiza a edição de uma internação de paciente.
	 * 
	 * @param internacao
	 * @param cidsInternacao
	 * @param cidsInternacaoExcluidos
	 * @param sizeListaResp
	 * @throws ApplicationBusinessException
	 */
	private AinInternacao editarInternacao(AinInternacao internacao,
			final List<AinCidsInternacao> cidsInternacao,
			final List<AinResponsaveisPaciente> listaResponsaveis,
			final Integer sizeListaResp, 
			final String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException{
		
		final AinInternacao internacaoOld = this.internacaoFacade.obterInternacaoAnterior(internacao.getSeq());
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		internacao = this.getAinInternacaoDAO().merge(internacao);

		/*-------Estas regras precisam ficar aqui, pois na edição devem ser testadas após o merge----*/
		this.validarEspecialidadeGrupoEmergencia(internacao);
		this.verificarInformarNumeroCERIH(internacao.getIphSeq(),internacao.getIphPhoSeq(), internacao.getConvenioSaudePlano().getId().getCnvCodigo(), internacao.getSeq());
		this.verDataInternacao(internacao.getSeq(),	internacao.getDthrInternacao(),	internacao.getAtendimentoUrgencia(), dataFimVinculoServidor);
		this.consistirClinicaEspecialidade(internacao);
		this.verificarRegrasDataInternacao(internacao);
		
		if (cidsInternacao != null){
			validarRegrasCids(internacao.getTipoCaracterInternacao().getIndCaraterCidSec(), cidsInternacao);
		}

		if (listaResponsaveis != null){
			this.getResponsaveisPacienteFacade().validarRegrasResponsaveisPaciente(listaResponsaveis, internacao);
		}

		if (sizeListaResp == 0){
	   		if (internacao.getPaciente().getIdade() < 18){
				throw new ApplicationBusinessException(CadastroInternacaoONExceptionCode.AIN_RESPONSAVEL_OBRIGATORIO);
	   		}
   	 	}

		//CHAMADA DE TRIGGERS DE ATUALIZAÇÃO
		this.getInternacaoFacade().atualizarInternacao(internacao, internacaoOld,  nomeMicrocomputador, servidorLogado ,dataFimVinculoServidor, false);
		/*-----------------------------------------------------------------------*/
		
		return internacao;
	}
	
	/**
	 * Verifica se a data de internação não se encontra maior que as datas
	 * de previsão de alta e de saída do paciente
	 * @param internacao
	 * @throws ApplicationBusinessException 
	 */
	private void verificarRegrasDataInternacao(AinInternacao internacao) throws ApplicationBusinessException {
		if (internacao.getDthrInternacao() != null
				&& internacao.getDtSaidaPaciente() != null
				&& internacao.getDtSaidaPaciente().before(
						internacao.getDthrInternacao())) {
			//AIN-00147 - A data da saída do paciente deve ser maior ou igual a data da internação.
			throw new ApplicationBusinessException(CadastroInternacaoONExceptionCode.AIN_00147);
		}
		if (internacao.getDthrInternacao() != null
				&& internacao.getDtPrevAlta() != null
				&& DateUtil.validaDataTruncadaMaior(
						internacao.getDthrInternacao(),
						internacao.getDtPrevAlta())) {
			//AIN-00146 - A data de previsão de alta deve ser maior ou igual a data da internação.
			throw new ApplicationBusinessException(CadastroInternacaoONExceptionCode.AIN_00146);
		}
		
	}

	/**
	 * Consiste para que seja informada a acomodação autorizada para internações
	 * de convênio (AGUARDANDO MELHOR LOCAL PARA CHAMADA DESTE MÉTODO)
	 * 
	 * @param internacao
	 *  
	 */
	public boolean verificarAcomodacaoInternacaoConvenio(
			final AinInternacao internacao) {
		boolean retorno = false;
		
		final Boolean isConvenioSus = getInternacaoFacade()
				.verificarConvenioSus(internacao.getConvenioSaudePlano()
						.getId().getCnvCodigo());
		//Se Convênio não for SUS
		if (!isConvenioSus){

			final Long listaCount = getFaturamentoFacade().acomodacaoInternacaoConvenioCount(internacao);

			if (listaCount != null && listaCount > 0) {
				retorno = true;
			}	
		}
		
		return retorno;
	}
		
	@Secure("#{s:hasPermission('caraterInternacao','pesquisar')}")
	public List<AinTiposCaraterInternacao> pesquisarCaraterInternacao(
			final String strPesquisa) {

		List<AinTiposCaraterInternacao> retorno;
		
		if (StringUtils.isNotBlank(strPesquisa)) {
			if (StringUtils.isNumeric(strPesquisa)){
				final Integer intPesquisa = Integer.valueOf(strPesquisa);
				retorno = this.getCadastrosBasicosInternacaoFacade()
						.pesquisaTiposCaraterInternacao(0, 200,
								AinTiposCaraterInternacao.Fields.DESCRICAO
										.toString(), true, intPesquisa, null,
								null, null);
			} else {
				retorno = this.getCadastrosBasicosInternacaoFacade()
						.pesquisaTiposCaraterInternacao(0, 200,
								AinTiposCaraterInternacao.Fields.DESCRICAO
										.toString(), true, null, strPesquisa,
								null, null);
			}
		} else {
			retorno = this.getCadastrosBasicosInternacaoFacade()
					.pesquisaTiposCaraterInternacao(0, 200,
							AinTiposCaraterInternacao.Fields.DESCRICAO
									.toString(), true, null, null, null, null);
		}
		
		return retorno;
	}
	
	/**
	 * Pesquisa de projeto de pesquisa por descrição ou código
	 * 
	 * Pesquisa utilizada na tela de internação.
	 * 
	 * @param strPesquisa
	 *            , idadePaciente
	 */
	@Secure("#{s:hasPermission('projetoPesquisa','pesquisar')}")
	public List<AelProjetoPesquisas> pesquisarProjetosPesquisaInternacao(
			final String strPesquisa, final Integer codigoPaciente) {

		//Executa primeira parte da query
		final List<AelProjetoPesquisas> listaProjetosPesquisa = getExamesLaudosFacade().pesquisarProjetosPesquisaInternacao(strPesquisa, codigoPaciente);
	
		
		//Executa segunda parte da query
		final List<ConvenioPlanoVO> listaVAinConvenioPlano = this.getSolicitacaoInternacaoFacade()
				.pesquisarConvenioPlanoVO(0, 0, null);
		final List<AelProjetoPesquisas> listaProjetosPesquisaEmConvenios = new ArrayList<AelProjetoPesquisas>();
		
		for (final AelProjetoPesquisas projetoPesquisa: listaProjetosPesquisa){		
			for (final ConvenioPlanoVO conv: listaVAinConvenioPlano){
				if (testarDadosConvenioPlano(conv)){
					if (conv.getPlano().intValue() == projetoPesquisa
							.getConvenioSaudePlano().getId().getSeq()
							.intValue()
							&& conv.getCnvCodigo().intValue() == projetoPesquisa
									.getConvenioSaudePlano().getId()
									.getCnvCodigo().intValue()) {
						listaProjetosPesquisaEmConvenios.add(projetoPesquisa);
						break;
					}
				}
			}
		}
		return listaProjetosPesquisaEmConvenios;
	}
	
	public List<AelProjetoPesquisas> pesquisarProjetosPesquisaInternacao(
			final String strPesquisa) {
		
		// L_PJQ.situacao in ('01','11','12')
		final DominioSituacaoProjetoPesquisa[] dominios = {
				DominioSituacaoProjetoPesquisa.APROVADO,
				DominioSituacaoProjetoPesquisa.REAPROVADO,
				DominioSituacaoProjetoPesquisa.APROVADO_RES_340_2004 };
		
		
		final List<AelProjetoPesquisas> resultList = getExamesLaudosFacade().pesquisarProjetosPesquisaInternacaoNumero(strPesquisa, dominios);
		if (!resultList.isEmpty() || StringUtils.isBlank(strPesquisa)) {
			return resultList;
		}
		
		return getExamesLaudosFacade().pesquisarProjetosPesquisaInternacaoNomeOuDescricao(strPesquisa, dominios);
	}

	private boolean testarDadosConvenioPlano(final ConvenioPlanoVO conv){
		boolean retorno = false;
		if (conv.getIndPermissaoInternacao()
				&& DominioTipoPlano.I.equals(conv.getIndTipoPlano())
				&& DominioSituacao.A.equals(conv.getIndSituacao())) {
			retorno = true;
		}
		return retorno;
	}
	
	
	
	/**
	 * ORADB VIEW V_AIN_SERV_TRANSF Este método implementa a query antes do
	 * UNION da view V_AIN_SERV_TRANSF em conjunto com a view
	 * V_AIN_SERV_INTERNA.
	 *
	 * @dbtables AghEspecialidades select
	 * @dbtables AghProfEspecialidades select
	 * @dbtables RapServidores select
	 * @dbtables RapPessoasFisicas select
	 * @dbtables RapQualificacao select
	 * @dbtables RapTipoQualificacao select
	 *  
	 * @param especialidade
	 *            , strPesquisa
	 * @return listaObjetos
	 */
	private List<Object[]> obterCriteriaProfessoresInternacaoUnion1(
			final String strPesquisa, final Integer matriculaProfessor,
			final Short vinCodigoProfessor) {
				
		final List<Object[]> listaObjetos = getAghuFacade().obterCriteriaProfessoresInternacaoUnion1(strPesquisa, matriculaProfessor,
				vinCodigoProfessor);

		return listaObjetos;
			
	}
	


	private List<Object[]> obterProfessoresInternacao(final String strPesquisa, final Integer matriculaProfessor, final Short vinCodigoProfessor){

		final List<Object[]> listaObjetos = getAghuFacade().obterProfessoresInternacao(strPesquisa, matriculaProfessor, vinCodigoProfessor);

		return listaObjetos;

	}

	/**
	 * ORADB VIEW V_AIN_SERV_TRANSF Este método implementa a query depois do
	 * UNION da view V_AIN_SERV_TRANSF em conjunto com a view
	 * V_AIN_SERV_INTERNA.
	 * 
	 * @dbtables AghProfEspecialidades select
	 * @dbtables AghEspecialidades select
	 * @dbtables RapServidores select
	 * @dbtables RapPessoasFisicas select
	 * @dbtables RapQualificacao select
	 * @dbtables RapTipoQualificacao select
	 * @dbtables AghProfissionaisEspConvenio select
	 * @dbtables AinEscalasProfissionalInt select
	 * 
	 * @param especialidade
	 *            , strPesquisa
	 * @return listaObjetos
	 */
	private List<Object[]> obterCriteriaProfessoresInternacaoUnion2(
			final String strPesquisa, final Integer matriculaProfessor,
			final Short vinCodigoProfessor) {
		
		final List<Object[]> listaObjetos = getAghuFacade().obterCriteriaProfessoresInternacaoUnion2(strPesquisa, matriculaProfessor,
				vinCodigoProfessor);
		
		return listaObjetos;
			
	}
	
	
	/**
	 * Este método monta e retorna apenas um objeto ProfessorCrmInternacaoVO 
	 * 
	 * @param professorEspecialidade
	 *            , cnvCodigo
	 * @return ProfessorCrmInternacaoVO
	 */
	public ProfessorCrmInternacaoVO obterProfessorCrmInternacaoVO(
			final RapServidores servidorProfessor, final AghEspecialidades especialidade,
			final Short cnvCodigo) {
		
		ProfessorCrmInternacaoVO retorno = null;
		if (servidorProfessor != null && cnvCodigo != null){
			final ProfessorCrmInternacaoVO professorVO = new ProfessorCrmInternacaoVO();
			professorVO.setConsisteEscala(null);
			professorVO.setSerMatricula(servidorProfessor.getId()
					.getMatricula());
			professorVO.setSerVinCodigo(servidorProfessor.getId()
					.getVinCodigo());
			
			AghEspecialidades esp = aghuFacade.obterEspecialidadePorChavePrimaria(especialidade.getSeq());
			professorVO.setEspSeq(esp.getSeq());
			professorVO.setEspSigla(esp.getSigla());
			
			professorVO.setCnvCodigo(cnvCodigo);
			String nomeServidor = registroColaboradorFacade.obterNomePessoaServidor(servidorProfessor.getId()
					.getVinCodigo(), servidorProfessor.getId().getMatricula());
			professorVO.setNome(nomeServidor);	

			List<RapQualificacao> listaQualificacao = this.registroColaboradorFacade.pesquisarQualificacoes(servidorProfessor.getPessoaFisica());		
			
			if (listaQualificacao != null) {
				for (final RapQualificacao qual : listaQualificacao) {
					if(qual.getNroRegConselho() != null) {
						professorVO.setNroRegConselho(qual.getNroRegConselho());
					}
				}
			}
			
			retorno = professorVO;
		} else {
			retorno = new ProfessorCrmInternacaoVO();
		}
		
		return retorno;
	}

	
	/**
	 * Este método realiza a pesquisa de CRM Professores para a tela de
	 * internação
	 * 
	 * @dbtables AghProfEspecialidades select
	 * @dbtables AghEspecialidades select
	 * @dbtables RapServidores select
	 * @dbtables RapPessoasFisicas select
	 * @dbtables RapQualificacao select
	 * @dbtables RapTipoQualificacao select
	 * @dbtables AghProfissionaisEspConvenio select
	 * @dbtables AinEscalasProfissionalInt select
	 * 
	 * @param especialidade
	 *            , convenioPlano, strParam
	 * @return listaLOV
	 */
	@Secure("#{s:hasPermission('colaborador','pesquisar')}")
	public List<ProfessorCrmInternacaoVO> pesquisarProfessoresCrm(final Short espSeq,
			final String espSigla, final Short cnvCodigo, final Boolean indVerfEscalaProfInt,
			final String strParam, final Integer matriculaProfessor,
			final Short vinCodigoProfessor) {
		final String strPesquisa = strParam;
		List<ProfessorCrmInternacaoVO> listaLOV = null;
		
		if (espSeq == null || espSigla == null || cnvCodigo == null || indVerfEscalaProfInt == null) {
			listaLOV = new ArrayList<ProfessorCrmInternacaoVO>();
		} else {
			final Collection<Object[]> listaProfessoresObject1 = this
					.obterCriteriaProfessoresInternacaoUnion1(strPesquisa,
							matriculaProfessor, vinCodigoProfessor);
			final Collection<Object[]> listaProfessoresObject2 = this
					.obterCriteriaProfessoresInternacaoUnion2(strPesquisa,
							matriculaProfessor, vinCodigoProfessor);
			
			final Set<ProfessorCrmInternacaoVO> servidoresTransf = new HashSet<ProfessorCrmInternacaoVO>();
			for (final Object[] vetorObject: listaProfessoresObject1){
				servidoresTransf.add(popularServidorTransfInternacaoVO(
						vetorObject, false));
			}
			for (final Object[] vetorObject: listaProfessoresObject2){
				servidoresTransf.add(popularServidorTransfInternacaoVO(
						vetorObject, true));
			}

			final List<ProfessorCrmInternacaoVO> listaServidoresTransf = new ArrayList<ProfessorCrmInternacaoVO>(
					servidoresTransf);
			this.ordenarCRMProfessor(listaServidoresTransf);

			listaLOV = selecionarProfessoresCrm(listaServidoresTransf, espSeq,
					indVerfEscalaProfInt, cnvCodigo);
		}
		

		return listaLOV;
	}
	
	private List<ProfessorCrmInternacaoVO> selecionarProfessoresCrm(final List<ProfessorCrmInternacaoVO> listaProfessores, final Short espSeq,
			final Boolean indVerfEscalaProfInt, final Short cnvCodigo) {
		final List<ProfessorCrmInternacaoVO> listaLOV = new ArrayList<ProfessorCrmInternacaoVO>();
		for (final ProfessorCrmInternacaoVO professor: listaProfessores){
			if (espSeq.equals(professor.getEspSeq())
					&& indVerfEscalaProfInt.equals(professor
							.getConsisteEscala())) {
				if ((indVerfEscalaProfInt && cnvCodigo.equals(professor.getCnvCodigo())) || (!indVerfEscalaProfInt && professor.getCnvCodigo() == 0)){
						listaLOV.add(professor);
				}
			}
		}
		
		return listaLOV;
	}
	
	
	public List<ProfessorCrmInternacaoVO> pesquisarProfessoresCrm(final Object strParam, final Integer matriculaProfessor, final Short vinCodigoProfessor){
		final String strPesquisa = (String) strParam;

		final Collection<Object[]> listaProfessoresObject1 = this.obterProfessoresInternacao(strPesquisa, matriculaProfessor, vinCodigoProfessor);

		final Set<ProfessorCrmInternacaoVO> servidoresTransf = new HashSet<ProfessorCrmInternacaoVO>();
		for (final Object[] vetorObject: listaProfessoresObject1){
			servidoresTransf.add(popularProfessorCrmInternacaoVO(vetorObject));
		}

		final List<ProfessorCrmInternacaoVO> listaServidoresTransf = new ArrayList<ProfessorCrmInternacaoVO>(servidoresTransf);
		
		final BeanComparator nomeSorter = new BeanComparator("nome", new NullComparator(false));
		Collections.sort(listaServidoresTransf, nomeSorter);

		return listaServidoresTransf;
	}


	/**
	 * Este método realiza a pesquisa de Convênios para a tela de internação
	 * 
	 * @param strParam
	 * @return listaLOV
	 */
	@Secure("#{s:hasPermission('convenioSaude','pesquisar')}")
	public List<ConvenioPlanoVO> pesquisarConvenioPlanoInternacao(
			final Object strParam) {
		final String strPesquisa = (String)strParam;
		
		final List<ConvenioPlanoVO> listaConvenioPlanoView = this.getSolicitacaoInternacaoFacade()
				.pesquisarConvenioPlanoVO(0, 0, strPesquisa);
		this.ordenarConvenioPlano(listaConvenioPlanoView);
		
		final List<ConvenioPlanoVO> listaLOV = new ArrayList<ConvenioPlanoVO>();
		for (final ConvenioPlanoVO convenioPlano: listaConvenioPlanoView){
			if (convenioPlano.getIndPermissaoInternacao()
					&& convenioPlano.getIndTipoPlano() == DominioTipoPlano.I) {
				if (convenioPlano.getIndSituacao() == DominioSituacao.A
						&& convenioPlano.getPlanoIndSituacao() == DominioSituacao.A) {
					listaLOV.add(convenioPlano);
				}
			}
		}	
		return listaLOV;
	}
	
	
	public ConvenioPlanoVO obterConvenioPlanoVO(final Short cnvCodigo, final Byte seq){
		return this.getSolicitacaoInternacaoFacade()
				.obterConvenioPlanoVO(cnvCodigo, seq);
	}
	
	/**
	 * Obtém o convênio e o plano que permite internação
	 * 
	 * @param cnvCodigo
	 * @return
	 */
	@Secure("#{s:hasPermission('convenioSaude','pesquisar')}")
	public FatConvenioSaudePlano obterConvenioPlanoInternacao(final Short cnvCodigo){
		return this.getSolicitacaoInternacaoFacade()
				.obterConvenioPlanoInternacao(cnvCodigo);
	}
	
	/**
	 * método que ordena um List de ConvenioPlanoVO
	 * 
	 */
	private void ordenarConvenioPlano(final List<ConvenioPlanoVO> listaConvenioPlano){
		
		final ComparatorChain chainSorter = new ComparatorChain();
		final BeanComparator convenioPlanoSorter = new BeanComparator(
				"convenioPlano", new NullComparator(false));
		chainSorter.addComparator(convenioPlanoSorter);
		Collections.sort(listaConvenioPlano, chainSorter);
	}
	
	/**
	 * método que ordena um List de ProfessorCrmInternacaoVO
	 * 
	 */
	private void ordenarCRMProfessor(
			final List<ProfessorCrmInternacaoVO> listaServidoresTransf) {
		
		final ComparatorChain chainSorter = new ComparatorChain();
		final BeanComparator quantPacInternadosSorter = new BeanComparator(
				"quantPacInternados", new NullComparator(false));
		final BeanComparator nomeSorter = new BeanComparator("nome",
				new NullComparator(false));
		chainSorter.addComparator(quantPacInternadosSorter);
		chainSorter.addComparator(nomeSorter);
		Collections.sort(listaServidoresTransf, chainSorter);
	}
	

	/**
	 * Este método é responsável por popular o objeto VO que corresponde a um
	 * CRM Professor na tela de internação
	 * 
	 * @param vetorObject
	 *            , consisteEscala
	 * @return servidorTranfInternacaoVO
	 */
	private ProfessorCrmInternacaoVO popularServidorTransfInternacaoVO(
			final Object[] vetorObject, final Boolean consisteEscala) {

		final Integer serMatricula = (Integer)vetorObject[0];
		final Short serVinCodigo = (Short)vetorObject[1];
		final Short espSeq = (Short)vetorObject[2];
		Short cnvCodigo = (Short)vetorObject[3];
		final String espSigla = (String)vetorObject[4];
		final String nome = (String)vetorObject[5];
		final String nroRegConselho = (String)vetorObject[6];
		final Integer capacReferencial = (Integer)vetorObject[7];
		final Integer quantPacInternados = (Integer)vetorObject[8];
		final String atuaCti = (String)vetorObject[9];
		
		final ProfessorCrmInternacaoVO professorCrmInternacaoVO = new ProfessorCrmInternacaoVO();
		professorCrmInternacaoVO.setConsisteEscala(consisteEscala);

		if (!consisteEscala){
			cnvCodigo = 0;
			professorCrmInternacaoVO.setCnvCodigo(cnvCodigo);
			professorCrmInternacaoVO.setAtuaCti(null);
			professorCrmInternacaoVO.setDtInicioEscala(null);
		} else {
			professorCrmInternacaoVO.setCnvCodigo(cnvCodigo);
			professorCrmInternacaoVO.setAtuaCti(atuaCti);
			final Date dtInicioEscala = (Date)vetorObject[10];
			final String strDtInicioEscala = dtInicioEscala.toString();
			professorCrmInternacaoVO.setDtInicioEscala(strDtInicioEscala);
		}
		
		professorCrmInternacaoVO.setSerMatricula(serMatricula);
		professorCrmInternacaoVO.setSerVinCodigo(serVinCodigo);
		professorCrmInternacaoVO.setEspSeq(espSeq);
		
		professorCrmInternacaoVO.setEspSigla(espSigla);
		professorCrmInternacaoVO.setNome(nome);
		professorCrmInternacaoVO.setNroRegConselho(nroRegConselho);
		professorCrmInternacaoVO.setCapacReferencial(capacReferencial);
		professorCrmInternacaoVO.setQuantPacInternados(quantPacInternados);
		
		return professorCrmInternacaoVO;
	}
	
	/**
	 * Este método é responsável por popular o objeto VO que corresponde a um CRM Professor na tela de internação
	 * @param vetorObject, consisteEscala
	 * @return servidorTranfInternacaoVO
	 */
	private ProfessorCrmInternacaoVO popularProfessorCrmInternacaoVO(final Object[] vetorObject){
		final Integer serMatricula = (Integer)vetorObject[0];
		final Short serVinCodigo = (Short)vetorObject[1];
		final String nome = (String)vetorObject[2];
		final String nroRegConselho = (String)vetorObject[3];

		final ProfessorCrmInternacaoVO professorCrmInternacaoVO = new ProfessorCrmInternacaoVO();

		professorCrmInternacaoVO.setSerMatricula(serMatricula);
		professorCrmInternacaoVO.setSerVinCodigo(serVinCodigo);
		professorCrmInternacaoVO.setNome(nome);
		professorCrmInternacaoVO.setNroRegConselho(nroRegConselho);

		return professorCrmInternacaoVO;
	}

	
	@Secure("#{s:hasPermission('convenioSaude','pesquisar')}")
	// FIXME Já existe um metodo similar na ON correta. Remover
	public FatConvenioSaudePlano obterConvenioSaudePlano(
			final FatConvenioSaudePlanoId id) {
		final FatConvenioSaudePlano retorno = this.getFaturamentoFacade().obterFatConvenioSaudePlanoPorChavePrimaria(id);
		
		return retorno;
	}
	
	@Secure("#{s:hasPermission('contaHospitalar','pesquisar')}")
	public FatItensProcedHospitalar obterFatItemProcedHosp(
			final FatItensProcedHospitalarId id) throws ApplicationBusinessException {
		return this.getFaturamentoFacade().obterItemProcedHospitalarPorChavePrimaria(id);
	}
	
	
	@Secure("#{s:hasPermission('atendimentoUrgencia','pesquisar')}")
	public AinAtendimentosUrgencia obterAtendimentoUrgencia(
			final Integer ainAtendimentoUrgenciaSeq) {
		return this.getAinAtendimentosUrgenciaDAO().obterPorChavePrimaria(ainAtendimentoUrgenciaSeq);
	}
	 
	/**
	 * Método que obtém a Origem de internação para quando o paciente já se
	 * encontra em atendimento de emergência.
	 * 
	 * @param unfSeq
	 * @return
	 * @throws ApplicationBusinessException
	 */
	@Secure("#{s:hasPermission('atendimentoUrgencia','pesquisar')}")
	public AghOrigemEventos obterOrigemInternacaoAtendimentoEmergencia(
			final Short unfSeq) throws ApplicationBusinessException {
		
		AghOrigemEventos origemInternacao = null;
		Short vOrigemEvento = null;

		final ConstanteAghCaractUnidFuncionais constanteCO = ConstanteAghCaractUnidFuncionais.CO;
		final ConstanteAghCaractUnidFuncionais constanteUnidadeEmergencia = ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA;
		
		AghuParametrosEnum parametroEnum = null;
		
		if (this.getInternacaoFacade().verificarCaracteristicaUnidadeFuncional(
				unfSeq, constanteCO)) {
			parametroEnum = AghuParametrosEnum.P_COD_EVENTO_CO;
		} else if (this.getInternacaoFacade()
				.verificarCaracteristicaUnidadeFuncional(unfSeq,
						constanteUnidadeEmergencia)) {
			parametroEnum = AghuParametrosEnum.P_ORIGEM_EMERGENCIA;
		}
		
		if (parametroEnum != null){
			AghParametros parametroOrigemEmergencia = this.getParametroFacade()
					.buscarAghParametro(parametroEnum);
			vOrigemEvento = parametroOrigemEmergencia.getVlrNumerico()
					.shortValue();
			if (parametroEnum.equals(parametroEnum.P_COD_EVENTO_CO)){
				parametroEnum = AghuParametrosEnum.P_SEQ_ESP_PRN;
				parametroOrigemEmergencia = this.getParametroFacade()
						.buscarAghParametro(parametroEnum);
				vOrigemEvento = parametroOrigemEmergencia.getVlrNumerico()
						.shortValue();
			}
		}
		
		if (vOrigemEvento != null){
			origemInternacao = this.obterOrigemEvento(vOrigemEvento);			
		}
		
		return origemInternacao;
	}
	
	
	/**
	 * Método que valida e impede que uma especialidade pertença a um grupo de
	 * emergência
	 * 
	 * @param internacao
	 *            , unfSeq
	 * @return
	 *  
	 */
	private void validarEspecialidadeGrupoEmergencia(final AinInternacao internacao) throws BaseException{
		//Mudança para lançar BaseException gmneto 12/02/2010
		//Obtém unfSeq
		Short unfSeq = null;
		if (internacao.getQuarto() != null){
			unfSeq = internacao.getQuarto().getUnidadeFuncional().getSeq();
		} else if (internacao.getLeito() != null) {
			unfSeq = internacao.getLeito().getUnidadeFuncional().getSeq();
		} else {
			unfSeq = internacao.getUnidadesFuncionais().getSeq();
		}
		
		final ConstanteAghCaractUnidFuncionais constanteUnidadeEmergencia = ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA;
		if (!this.getInternacaoFacade()
				.verificarCaracteristicaUnidadeFuncional(unfSeq,
						constanteUnidadeEmergencia)) {
			final AghuParametrosEnum enumGrupoEspEmerg = AghuParametrosEnum.P_GRUPO_ESPEC_EMERG;
			final AghParametros parametroGrupoEspEmerg = this.getParametroFacade()
					.buscarAghParametro(enumGrupoEspEmerg);
			if (internacao.getEspecialidade().getGreSeq() != null
					&& internacao.getEspecialidade().getGreSeq().intValue() == parametroGrupoEspEmerg
							.getVlrNumerico().intValue()) {
				//Mudança para lançar exceção sem rollback gmneto 12/02/2010
				throw new ApplicationBusinessException(
						CadastroInternacaoONExceptionCode.AIN_00822);
				
			}
		}
		
	}
	
	
	/** 
	 * ORA AINP_CONSISTE_CLINICA_ESP Método que valida clínica e especialidade
	 * 
	 * @param internacao
	 * @throws ApplicationBusinessException
	 */
	public void consistirClinicaEspecialidade(final AinInternacao internacao) throws BaseException{
		//Mudança para lança BaseException gmneto 12/02/2010
		
		String vConsClin = "R";
		final AghuParametrosEnum enumPConsClin = AghuParametrosEnum.P_CONS_CLIN;
		final AghParametros parametroPConsClin = this.getParametroFacade()
				.buscarAghParametro(enumPConsClin);
		if (parametroPConsClin.getVlrTexto().equals("I")){
			vConsClin = parametroPConsClin.getVlrTexto();
		}
		
		AinQuartos quarto = null;
		AinLeitos leito = null;
		AghUnidadesFuncionais unidade=null;
		AghEspecialidades especialidade=null;	
		
		if (internacao.getQuarto()!=null){
			quarto = cadastrosBasicosInternacaoFacade.obterQuarto(internacao.getQuarto().getNumero());
		}	
		if (internacao.getLeito()!=null){
			leito = cadastrosBasicosInternacaoFacade.obterLeitoPorId(internacao.getLeito().getLeitoID());
		}
		if (internacao.getUnidadesFuncionais()!=null){
			unidade = aghuFacade.obterUnidadeFuncional(internacao.getUnidadesFuncionais().getSeq());
		}
		if (internacao.getEspecialidade()!=null){
			especialidade = aghuFacade.obterAghEspecialidadesPorChavePrimaria(internacao.getEspecialidade().getSeq());
		}		
		
		if (verificarCondicoesClinica(leito, quarto, unidade, especialidade)) {			
			if (vConsClin.equals("R")){
				if (internacao.getQuarto() != null){
					throw new ApplicationBusinessException(CadastroInternacaoONExceptionCode.AIN_00316);
				} else if (internacao.getLeito() != null) {
					throw new ApplicationBusinessException(CadastroInternacaoONExceptionCode.AIN_00317);
				} else {
					throw new ApplicationBusinessException(CadastroInternacaoONExceptionCode.AIN_00318);
				}
			}
			
		}
		//Neste caso, a outra RN já é chamada daqui mesmo
		this.consistirEspecialidade(internacao);
		
	}
	
	
	/**
	 * Este método é chamado diretamente da Controller para verificar se a
	 * mensagem de confirmação deve ser exibida ao usuário
	 * 
	 * @param leito
	 * @param quarto
	 * @param unidadeFuncional
	 * @param especialidade
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public boolean consistirClinicaMensagemConfirmacao(final AinLeitos leito,
			final AinQuartos quarto, final AghUnidadesFuncionais unidadeFuncional,
			final AghEspecialidades especialidade) throws ApplicationBusinessException {
		boolean retorno = false;
		String vConsClin = "R";
		final AghuParametrosEnum enumPConsClin = AghuParametrosEnum.P_CONS_CLIN;
		final AghParametros parametroPConsClin = this.getParametroFacade()
				.buscarAghParametro(enumPConsClin);
		if (parametroPConsClin.getVlrTexto().equals("I")){
			vConsClin = parametroPConsClin.getVlrTexto();
		}

		if (verificarCondicoesClinica(leito, quarto, unidadeFuncional,
				especialidade)) {
			if (vConsClin.equals("I")){
				retorno = true;
			}
		}
		
		return retorno;
	}
	
	/**
	 * Método que verifica todas as condições da RN de Clínica e Especialidade
	 * 
	 * @param leito
	 * @param quarto
	 * @param unidadeFuncional
	 * @param especialidade
	 * @return
	 */
	private boolean verificarCondicoesClinica(final AinLeitos leito,
			final AinQuartos quarto, final AghUnidadesFuncionais unidadeFuncional,
			final AghEspecialidades especialidade) {
		boolean retorno = false;
		AghClinicas clinica = null;
		boolean deveConsistirClinica = false;
	
		if (leito != null){
			deveConsistirClinica = Objects.equals(leito.getIndConsClinUnidade(),DominioSimNao.S);
			clinica = leito.getQuarto().getClinica();
			
		} else if (quarto != null) {
			deveConsistirClinica = Objects.equals(quarto.getIndConsClin(),DominioSimNao.S);
			clinica = quarto.getClinica();
			
		} else if (unidadeFuncional != null) {
			deveConsistirClinica = Objects.equals(unidadeFuncional.getIndConsClin(), DominioSimNao.S);
			clinica = unidadeFuncional.getClinica();
		}
		
		if (clinica != null){
			if (!clinica.getCodigo().equals(especialidade.getClinica().getCodigo())) {
				if (deveConsistirClinica){
					// Caso não seja "R", apenas pergunta ao usuário se ele quer
					// continuar.
					// Isto, por não ser regra de negócio, está implementado na
					// controller
					retorno = true;
				}
			}
		}
		
		return retorno;
	}

	
	/** 
	 * Este método foi separado do método consistirClinicaEspecialidade apenas
	 * por questões de praticidade, pois na implementação do Oracle Forms, ambos
	 * fazem parte da Unit AINP_CONSISTE_CLINICA_ESP
	 * 
	 * @param internacao
	 * @throws ApplicationBusinessException
	 */
	private void consistirEspecialidade(final AinInternacao internacao) throws BaseException{		
		//Mudança para lançar BaseException gmneto 12/02/2010
		String vConsEsp = "R";
		final AghuParametrosEnum enumPConsEsp = AghuParametrosEnum.P_CONS_ESP;
		final AghParametros parametroPConsEsp = this.getParametroFacade()
				.buscarAghParametro(enumPConsEsp);
		
		if (parametroPConsEsp.getVlrTexto().equals("I")){
			vConsEsp = parametroPConsEsp.getVlrTexto();
		}
		
		if (verificarCondicoesEspecialidade(internacao.getLeito(),
				internacao.getEspecialidade())) {
			// Caso não seja "R", apenas pergunta ao usuário se ele quer
			// continuar.
			// Isto, por não ser regra de negócio, está implementado na
			// controller
			
			//Mudança para lançar exceção sem rollback gmneto 12/02/2010
			if (vConsEsp.equals("R")){
				throw new ApplicationBusinessException(
						CadastroInternacaoONExceptionCode.AIN_00654);
			}
		}
		
	}
	
	/**
	 * Este método é chamado diretamente da Controller para verificar se a
	 * mensagem de confirmação deve ser exibida ao usuário (somente para
	 * especialidade este)
	 * 
	 * @param internacao
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public boolean consistirEspecialidadeMensagemConfirmacao(final AinLeitos leito,
			final AghEspecialidades especialidade) throws ApplicationBusinessException {
		boolean retorno = false;
		String vConsEsp = "R";
		final AghuParametrosEnum enumPConsEsp = AghuParametrosEnum.P_CONS_ESP;
		final AghParametros parametroPConsEsp = this.getParametroFacade()
				.buscarAghParametro(enumPConsEsp);
		
		if (parametroPConsEsp.getVlrTexto().equals("I")){
			vConsEsp = parametroPConsEsp.getVlrTexto();
		}
		
		if (verificarCondicoesEspecialidade(leito, especialidade)){
			// Caso não seja "R", apenas pergunta ao usuário se ele quer
			// continuar.
			// Isto, por não ser regra de negócio, está implementado na
			// controller
			if (vConsEsp.equals("I")){
				retorno = true;
			}
		}
		
		return retorno;
	}
	
	/**
	 * Método que verifica todas as condições da RN de Especialidade
	 * 
	 * @param internacao
	 * @return
	 */
	private boolean verificarCondicoesEspecialidade(final AinLeitos leito,
			final AghEspecialidades especialidade) {
		boolean retorno = false;
		
		if (leito != null && leito.getEspecialidade() != null){
			if (!leito.getEspecialidade().getSeq()
					.equals(especialidade.getSeq())) {
				if (DominioSimNao.S.equals(leito.getIndConsClinUnidade())){
					retorno = true;
				}
			}
		}
		return retorno;
	}

	
	/**
	 * ORADB ainp_verifica_cirurgia Método que obtém o procedimento cirúrgico de
	 * um paciente, caso haja algum no período de até 7 dias antes da data de
	 * internação.
	 * 
	 * @param pacCodigo
	 * @param dtInternacao
	 * @return
	 */
	@Secure("#{s:hasPermission('procedimentoCirurgico','pesquisar')}")
	public MbcCirurgias obterProcedimentoCirurgicoInternacao(final Integer pacCodigo,
			final Date dtInternacao) {
		
		final DominioSituacaoCirurgia situacaoCirurgia = DominioSituacaoCirurgia.CANC;
				
		final MbcCirurgias cirurgia = getBlocoCirurgicoFacade().obterProcedimentoCirurgicoInternacaoUltimaSemana(pacCodigo, dtInternacao, situacaoCirurgia);
		
		return cirurgia;
	}
	
	
	
	
	/**
	 * Regra que verifica se deve ser informado o número CERIH para a internação
	 * 
	 * @param internacao
	 *  
	 */
	public boolean verificarInformarNumeroCERIH(final Integer iphSeq, final Short iphPhoSeq, final Short cnvCodigo, final Integer seqInternacao) throws BaseException{
		//Mudança para exceção sem rollback - no if e na assinatura. gmneto 12/02/2010		
		boolean retorno = false;
		final AghuParametrosEnum enumExigeCerih = AghuParametrosEnum.P_AGHU_CARACT_CERIH;
		final AghParametros parametroExigeCerih = getParametroFacade()
				.buscarAghParametro(enumExigeCerih);

		DominioFatTipoCaractItem dominioFatTipoCaractItem = null;
		for (final DominioFatTipoCaractItem auxDominioFatTipoCaractItem : DominioFatTipoCaractItem
				.values()) {
			if (auxDominioFatTipoCaractItem.getDescricao().equalsIgnoreCase(
					parametroExigeCerih.getVlrTexto())) {
				dominioFatTipoCaractItem = auxDominioFatTipoCaractItem;
			}
		}
		
		final Boolean exigeCerih = getFaturamentoFacade().verificarCaracteristicaExame(iphSeq,
				iphPhoSeq, dominioFatTipoCaractItem);
		final Boolean isConvenioSus = getInternacaoFacade()
				.verificarConvenioSus(cnvCodigo);
		
		boolean possuiCerih = false;
		
		if (exigeCerih && isConvenioSus){
			//Se for na inclusão
			if (seqInternacao != null){
				final List<AinDiariasAutorizadas> listaDiarias = getCadastrosBasicosInternacaoFacade()
						.pesquisarDiariaPorCodigoInternacao(seqInternacao);
				for (final AinDiariasAutorizadas diaria: listaDiarias){
					if (StringUtils.isNotBlank(diaria.getSenha())){
						possuiCerih = true;
					}
				}		
				if (!possuiCerih){
					throw new ApplicationBusinessException(
							CadastroInternacaoONExceptionCode.AIN_00893);
				}
			} else {
				retorno = true;
			}
		}	
		
		return retorno;
	}
	
	
	/**
	 * ORADB AINP_VERIFICA_DATA Verifica se existe procedimento realizado entre
	 * as datas informadas
	 * 
	 * @param internacao
	 * @throws BaseException
	 */
	public void verDataInternacao(final Integer intSeq, final Date dtInternacao,
			final AinAtendimentosUrgencia atendimentoUrgencia, final Date dataFimVinculoServidor)
			throws BaseException {
		
		final AghuParametrosEnum enumTipoDataInt = AghuParametrosEnum.P_AGHU_TIPO_DATA_INTERNACAO;
		final AghParametros parametroTipoDataInt = getParametroFacade().buscarAghParametro(enumTipoDataInt);
		
		final Date dtInternacaoAnterior = obterDtInternacaoAnterior(intSeq);
		
		if (atendimentoUrgencia == null){
			if (dtInternacaoAnterior.compareTo(dtInternacao) != 0){
				final RnCthcVerDatasVO rnCthcVerDatasVO = getFaturamentoFacade().rnCthcVerDatas(intSeq, dtInternacao, 
						dtInternacaoAnterior, parametroTipoDataInt.getVlrTexto(), dataFimVinculoServidor);
				
				if (rnCthcVerDatasVO != null && !rnCthcVerDatasVO.getRetorno()) {
					final SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
					String strDataLimite = null; 
					strDataLimite = df.format(rnCthcVerDatasVO.getDataLimite());  
					
					throw new ApplicationBusinessException(
							CadastroInternacaoONExceptionCode.AIN_EXISTE_PROCEDIMENTO_DT_INTERNACAO, rnCthcVerDatasVO.getPhiSeq(), strDataLimite);
				}
			}
		}				
	}
	
	/**
	 * 
	 * Método que obtem a data de internação anterior da internação
	 * 
	 * @param intSeq
	 * @return
	 */
	public Date obterDtInternacaoAnterior(final Integer intSeq) {

		return getAinInternacaoDAO().obterDtInternacaoAnterior(intSeq);
	}
	
	
	/**
	 * ORADB ainp_consiste_matr_conv Verifica se deve ser informada a matrícula
	 * do convênio do paciente
	 * 
	 * @param internacao
	 * @throws ApplicationBusinessException
	 */
	@Secure("#{s:hasPermission('convenioSaude','pesquisar')}")
	public boolean verificarConvenioSaudePaciente(final AipPacientes paciente,
			final Short cnvCodigo) {
		// Removido throws desnecessário by gmneto 12/02/2011 
		boolean convenioCorreto = false;
		final FatConvenioSaude convenioSaude = getFaturamentoApoioFacade().obterConvenioSaude(cnvCodigo);
		if (convenioSaude.getExigeNumeroMatricula()){
			final List<AipConveniosSaudePaciente> listaConvenios = getPacienteFacade().pesquisarConveniosPaciente(paciente);
			for (final AipConveniosSaudePaciente convenioSaudePaciente: listaConvenios){
				super.refresh(convenioSaudePaciente);
				if (convenioSaudePaciente.getConvenio().getConvenioSaude()
						.getCodigo().equals(cnvCodigo)) {
					if (convenioSaudePaciente.getConvenio().getConvenioSaude()
							.getSituacao().equals(DominioSituacao.A)) {
						if (convenioSaudePaciente.getMatricula() != null){
							convenioCorreto = true;
							break;
						}
					}
				}
			}
		} else {
			convenioCorreto = true;
		}
		return convenioCorreto;
	}
	
	/**
	 * ORADB AINP_VALIDA_SUBCID Método que verifica se o cid informado não
	 * possui um sub-cid
	 * 
	 * @param codigoCid
	 *  
	 */
	public void validarSubCid(final String codigoCid)
			throws ApplicationBusinessException {
		//Mudança para lançar exceção sem rollback gmneto 12/02/2010
		if (!codigoCid.contains(".")){
			
			final List<AghCid> listaCids = this.getAghuFacade().pesquisarSubCid(codigoCid);
			
			if (listaCids != null && !listaCids.isEmpty()){
				throw new ApplicationBusinessException(
						CadastroInternacaoONExceptionCode.AIN_00834);
			}			
		}
	}
		
  	/**
  	 * Método que obtem um procedimentoHospitalar pelo seu id
  	 * @param phoSeq, seq
  	 * @return FatItensProcedHospitalar
  	 */
  	public FatItensProcedHospitalar obterItemProcedimentoHospitalar(final Short phoSeq, final Integer seq){
  		final FatItensProcedHospitalar procedimento = getFaturamentoFacade().obterItemProcedHospitalar(phoSeq, seq);
  		return procedimento;
  	}
  	
  	public AghProfEspecialidades sugereProfessorEspecialidade(final Short espSeq, final Short cnvCodigo){
  		return getInternacaoFacade().verificarEscalaProfessor(espSeq, cnvCodigo);
  	}
	
  	public AinInternacao alterarConvenioInternacao(AinInternacao internacao, final FatContasHospitalares contaHospitalar, final Integer seqContaHospitalarOld, String nomeMicrocomputador, final Date dataFimVinculoServidor, Boolean encerraConta) throws BaseException{
  		
  		this.validarCamposObrigatoriosConvenioInternacao(contaHospitalar);
  		
  		FatContasHospitalares contaHospitalarOld = this.getFatContasHospitalaresDAO().obterPorChavePrimaria(seqContaHospitalarOld, FatContasHospitalares.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO);
  		
  		
  		try {
	  		if (internacao.getConvenioSaudePlano()!=null){
	  			//internacao.setConvenioSaudePlano(this.getFaturamentoFacade().atualizarFatConvenioSaudePlano(internacao.getConvenioSaudePlano()));
				internacao
						.setConvenioSaudePlano(getFaturamentoApoioFacade()
								.persistirConvenioPlano(
										internacao.getConvenioSaude(),
										internacao.getConvenioSaudePlano(),

										faturamentoFacade
												.pesquisarPeriodosEmissaoConvenioSaudePlano(internacao
														.getConvenioSaudePlano()),

										faturamentoFacade
												.pesquisarConvTipoDocumentoConvenioSaudePlano(internacao
														.getConvenioSaudePlano()),

										faturamentoFacade
												.pesquisarConvPlanoAcomodocaoConvenioSaudePlano(internacao
														.getConvenioSaudePlano())));
	   	 	}
	  		
			internacao = this.getAinInternacaoDAO().atualizar(internacao);
	
	  		if(contaHospitalarOld!=null && contaHospitalarOld.getDataInternacaoAdministrativa()==null){
				throw new ApplicationBusinessException(
						CadastroInternacaoONExceptionCode.ERRO_CONTA_HOSPITALAR_ATIVA_INEXISTENTE);
	  	  	}
	  		this.validarDadosConvenioInternacao(contaHospitalar, contaHospitalarOld);	
	  		
	  		Short cnvCodigo = null;
			if (contaHospitalar.getConvenioSaudePlano() != null
					&& contaHospitalar.getConvenioSaudePlano().getId() != null) {
				cnvCodigo = contaHospitalar.getConvenioSaudePlano().getId()
						.getCnvCodigo();
	  		}
	  		Integer phiSeq = null;
	  		if(contaHospitalar.getProcedimentoHospitalarInterno()!=null){
				phiSeq = contaHospitalar.getProcedimentoHospitalarInterno()
						.getSeq();
	  		}
			final ValidaContaTrocaConvenioVO vo = getInternacaoFacade().validarContaTrocaConvenio(
					internacao.getSeq(),
					contaHospitalar.getDataInternacaoAdministrativa(), cnvCodigo,
					phiSeq);
	  		
	  		
	  		if(vo.getRetorno()==1){
	  			this.gerarNovaConta(internacao, contaHospitalar, contaHospitalarOld, nomeMicrocomputador, dataFimVinculoServidor, encerraConta);	
	  		} else {
	  			throw new ApplicationBusinessException(
						CadastroInternacaoONExceptionCode.ERRO_ALTERACAO_CONVENIO_PROCEDIMENTO,
						phiSeq);
			}
	  		
  		}
  		catch (InactiveModuleException e) {

  			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
  			
			getObjetosOracleDAO().executaTrocaConvenios(internacao.getSeq(), 
					   contaHospitalar.getConvenioSaudePlano().getId().getCnvCodigo(), 
					   contaHospitalar.getConvenioSaudePlano().getId().getSeq(),
					   contaHospitalar.getDataInternacaoAdministrativa(),
					   contaHospitalarOld.getSeq(),
					   contaHospitalarOld.getDataInternacaoAdministrativa(),
					   null,
					   contaHospitalarOld.getConvenioSaudePlano().getId().getCnvCodigo(), servidorLogado);

 			//desatacha porque a chamada nativa altera esses dados
			this.evict(internacao);
			this.evict(contaHospitalar);
			this.evict(contaHospitalarOld);
  		}
  		return internacao;
  	}
  	
  	private void validarCamposObrigatoriosConvenioInternacao(
			final FatContasHospitalares contaHospitalar) throws ApplicationBusinessException {
		
		if (contaHospitalar.getConvenioSaudePlano() == null
				|| contaHospitalar.getConvenioSaudePlano().getId() == null) {
			throw new ApplicationBusinessException(
					CadastroInternacaoONExceptionCode.AIN_CONVENIO_PLANO_OBRIGATORIO);
		}
		
		if (contaHospitalar.getDataInternacaoAdministrativa() == null){
			throw new ApplicationBusinessException(
					CadastroInternacaoONExceptionCode.AIN_DATA_OBRIGATORIA);
		}
		
	}
  	
  	/**
	 * ORADB AINP_GERA_NOVA_CONTA Método que verifica se o cid informado não
	 * possui um sub-cid, essa funcao está no FORMS
	 * 
	 * @param codigoCid
	 *  
	 */
	private void gerarNovaConta(final AinInternacao internacao, FatContasHospitalares contaHospitalar, final FatContasHospitalares contaHospitalarOld, String nomeMicrocomputador, final Date dataFimVinculoServidor, Boolean encerraConta) throws BaseException{

		final Integer internacaoSeq = internacao.getSeq();
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		Integer seqContaHospitalarOld = null;
		if(contaHospitalarOld!=null){
			seqContaHospitalarOld = contaHospitalarOld.getSeq();
		}
		final AinInternacao internacaoOld = getInternacaoFacade().obterInternacaoAnterior(internacao.getSeq());
		final Short cspCnvCodigo = contaHospitalar.getConvenioSaudePlano().getId().getCnvCodigo();
		final Byte cspSeq = contaHospitalar.getConvenioSaudePlano().getId().getSeq();
		
		//inicializa_datas
		final AghAtendimentos atendimento = this.inicializarDatas(internacaoSeq, contaHospitalar);
		
		FatContasHospitalares contaHospitalarRetorno = null;
		if(contaHospitalarOld!=null){
			contaHospitalarRetorno = this.obterContaHospitalar(seqContaHospitalarOld);	
		}
		
		contaHospitalarRetorno.setDtAltaAdministrativa(contaHospitalar.getDataInternacaoAdministrativa());
		//encerra
		if (contaHospitalarRetorno != null && encerraConta) {
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			String dataInternacaoOld = dateFormat.format(contaHospitalarRetorno.getDataInternacaoAdministrativa());
			Date dataOld = null;
			try {
				dataOld = dateFormat.parse(dataInternacaoOld);
			} catch (ParseException e) {
				LOG.error(e.getMessage(), e);
			}
			contaHospitalarRetorno.setDataInternacaoAdministrativa(dataOld);
			this.getFaturamentoFacade().
				persistirContaHospitalar(contaHospitalarRetorno, contaHospitalarOld, nomeMicrocomputador, dataFimVinculoServidor);
		}
		else if(encerraConta){
			throw new ApplicationBusinessException(CadastroInternacaoONExceptionCode.AIN_00391);
		}
		
		final AghParametros pCodConv75 = getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_COD_CONV_75);
		final AghParametros pSusPlanoAmbulatorio = getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_SUS_PLANO_AMBULATORIO);
		
		if (cspCnvCodigo != pCodConv75.getVlrNumerico().shortValue()
				&& cspSeq != pSusPlanoAmbulatorio.getVlrNumerico().byteValue()) {
			
			//insere
			contaHospitalar = this.insereDadosContaHospitalarConvenioInternacao(contaHospitalar, contaHospitalarOld);
			this.insere(contaHospitalar, contaHospitalarOld, atendimento.getInternacao().getDthrAltaMedica(), internacao, dataFimVinculoServidor, nomeMicrocomputador);
		}
		//atualiza_int
		internacao.setConvenioSaudePlano(contaHospitalar.getConvenioSaudePlano());
//		ainInternacaoDAO.getAndLock(internacao.getSeq(), LockOptions.UPGRADE);
		
		internacaoOld.setConvenioSaudePlano(contaHospitalarOld.getConvenioSaudePlano());
		getInternacaoFacade().atualizarInternacao(internacao, internacaoOld, true, nomeMicrocomputador,servidorLogado, dataFimVinculoServidor, false);
		if(encerraConta){
			//atualiza lancamentos
			this.atualizarLancamentos(internacaoSeq, atendimento, contaHospitalar,
					contaHospitalarRetorno, nomeMicrocomputador, dataFimVinculoServidor);
			this.verificarContaConvenio(internacaoSeq, contaHospitalarOld,
					contaHospitalar);
		}
	}
	
	/**
	 * 
	 * @param contaHospitalar
	 * @param contaHospitalarOld
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private FatContasHospitalares insereDadosContaHospitalarConvenioInternacao(
			final FatContasHospitalares contaHospitalar,
			final FatContasHospitalares contaHospitalarOld)
			throws ApplicationBusinessException {
		
		contaHospitalar.setProcedimentoHospitalarInterno(contaHospitalarOld
				.getProcedimentoHospitalarInterno());
		
		final AghParametros param = getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_CONVENIO_SUS);
		
		if(contaHospitalar.getConvenioSaudePlano().getId().getCnvCodigo()==1){

			final List<FatTipoAih> listaTipoAih = this.getFaturamentoFacade().pesquisarTipoAihPorSituacaoCodSus(DominioSituacao.A,
					param.getVlrNumerico().shortValue());			
			if(listaTipoAih!=null && listaTipoAih.size()>0){
				contaHospitalar.setTipoAih(listaTipoAih.get(0));
			}
		}
		return contaHospitalar;
	}
	
	private void validarDadosConvenioInternacao(
			final FatContasHospitalares contaHospitalar,
			final FatContasHospitalares contaHospitalarOld)
			throws ApplicationBusinessException {
		final AghParametros pCodConv75 = getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_COD_CONV_75);
		final AghParametros pSusPlanoAmbulatorio = getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_SUS_PLANO_AMBULATORIO);
		
		if (contaHospitalar.getConvenioSaudePlano().getId().getCnvCodigo()
				.equals(pCodConv75.getVlrNumerico().shortValue())
				&& !contaHospitalar
						.getConvenioSaudePlano()
						.getId()
						.getSeq()
						.equals(pSusPlanoAmbulatorio.getVlrNumerico()
								.byteValue())) {
			contaHospitalar.getConvenioSaudePlano().getId()
					.setSeq(Byte.valueOf("2"));
		}
		try{
			this.verificarDataAlta(contaHospitalar);	
		} catch(ParseException e){
			LOG.error(e.getMessage(), e);
		}
	}
	
	private AghAtendimentos inicializarDatas(final Integer internacaoSeq,
			final FatContasHospitalares contaHospitalar) throws ApplicationBusinessException {
		try{
			final AghAtendimentos atendimento = getAghuFacade().obterAtendimentoInternacao(internacaoSeq);
			return atendimento;	
		} catch (final Exception e) {
			LOG.error(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(
					CadastroInternacaoONExceptionCode.ERRO_ALTERAR_CONVENIO_INTERNACAO);
		}
		
	}
	
	private FatContasHospitalares obterContaHospitalar(
			final Integer seqContaHospitalarOld) throws ApplicationBusinessException {
		try{		
			//precisa fazer o evict pq em algumas situações intermitentes está pegando objeto desatualizado do banco.
			FatContasHospitalares contaHospitalar = getFaturamentoFacade().obterContaHospitalar(seqContaHospitalarOld);
			this.evict(contaHospitalar);
			return getFaturamentoFacade().obterContaHospitalar(seqContaHospitalarOld);
		} catch (final Exception e) {
			LOG.error(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(
					CadastroInternacaoONExceptionCode.ERRO_ALTERAR_CONVENIO_INTERNACAO);
		}
	}
	
	private void insere(final FatContasHospitalares contaHospitalar, final FatContasHospitalares contaHospitalarOld,
			final Date dataAltaOriginal, final AinInternacao internacao, final Date dataFimVinculoServidor,
			final String nomeMicrocomputador)
			throws ApplicationBusinessException {
		try{
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			contaHospitalar.setDtAltaAdministrativa(dataAltaOriginal);
			
			Date data = new Date();
			contaHospitalar.setAlteradoEm(data);
			contaHospitalar.setCriadoPor(servidorLogado != null ? servidorLogado.getUsuario() : null);
			contaHospitalar.setCriadoEm(data);
			contaHospitalar.setAlteradoPor(servidorLogado != null ? servidorLogado.getUsuario() : null);

			Integer vSerMatricula = getRegistroColaboradorFacade().obterServidorAtivoPorUsuario(servidorLogado != null ? servidorLogado.getUsuario() : null, dataFimVinculoServidor).getId().getMatricula();

			//if :new.ser_matricula  is null then raise_application_error(-20000, 'Usuário não cadastrado como servidor');
			if(vSerMatricula == null){
				throw new ApplicationBusinessException(CadastroInternacaoONExceptionCode.USUARIO_NAO_SERVIDOR);
			}
			
			/* ATUALIZA CARTAO PONTO DO SERVIDOR */
			contaHospitalar.setServidor(servidorLogado);
			contaHospitalar.setServidorManuseado(servidorLogado);
			
			
			/* INDICADOR DE SITUACAO DA CONTA */
			//if(contaHospitalar.getIndSituacao() == null){Linha nao migrada pois, conforme mapeamento, indSituacao nao pode ser nulo.
			if(contaHospitalar.getDtAltaAdministrativa() == null){
				contaHospitalar.setIndSituacao(DominioSituacaoConta.A);
			}else{
				contaHospitalar.setIndSituacao(DominioSituacaoConta.F);
			}		
			
			this.verificarDataAlta(contaHospitalar);
			
			getFaturamentoFacade().persistirContaHospitalar(contaHospitalar, contaHospitalarOld, true, nomeMicrocomputador, dataFimVinculoServidor);
			
			final FatContasInternacao contaInternacao = new FatContasInternacao();
			contaInternacao.setInternacao(internacao);
			contaInternacao.setContaHospitalar(contaHospitalar);
			
			getFaturamentoFacade().inserirContaInternacaoConvenio(contaInternacao, nomeMicrocomputador, dataFimVinculoServidor);

		} catch (ApplicationBusinessException e) {
			LOG.error("Erro ao gerar a nova conta", e);
			throw new ApplicationBusinessException(e);
		}
		catch (final Exception e) {
			LOG.error("Erro ao gerar a nova conta", e);
			throw new ApplicationBusinessException(
					CadastroInternacaoONExceptionCode.ERRO_AINP_GERA_NOVA_CONTA);
		}
	}
	
	private void verificarDataAlta(FatContasHospitalares contaHospitalar) throws ApplicationBusinessException, ParseException {
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		String dataInternacaoStr = null;
		String dataAltaStr = null;
		Date dataAlta= null;
		Date dataInternacao=null;
		dataInternacaoStr = dateFormat.format(contaHospitalar.getDataInternacaoAdministrativa());
		dataInternacao= dateFormat.parse(dataInternacaoStr);
		if(contaHospitalar.getDtAltaAdministrativa()!=null){
			dataAltaStr = dateFormat.format(contaHospitalar.getDtAltaAdministrativa());
			dataAlta= dateFormat.parse(dataAltaStr);
		}
		
		if (contaHospitalar.getDtAltaAdministrativa()!=null && contaHospitalar.getDtAltaAdministrativa()!=null && dataAlta.before(
				dataInternacao)) {
			throw new ApplicationBusinessException(
					CadastroInternacaoONExceptionCode.DATA_ALTA_MENOR_DATA_INTERNACAO_ADMINISTRATIVA);
		}
	}
	
	@SuppressWarnings("deprecation")
	private void atualizarLancamentos(final Integer internacaoSeq, final AghAtendimentos atendimento, final FatContasHospitalares contaHospitalar, final FatContasHospitalares contaHospitalarOld, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException{
		
		IInternacaoFacade internacaoFacade = getInternacaoFacade();
		
		internacaoFacade.atualizarConvenioPlanoExames(atendimento.getSeq(), contaHospitalar.getConvenioSaudePlano().getId().getCnvCodigo(), contaHospitalar.getConvenioSaudePlano().getId().getSeq(), contaHospitalar.getDataInternacaoAdministrativa(), nomeMicrocomputador);
		
		internacaoFacade.atualizarConvenioPlanoCirurgias(atendimento.getSeq(),contaHospitalar.getConvenioSaudePlano().getId().getCnvCodigo(),contaHospitalar.getConvenioSaudePlano().getId().getSeq(), contaHospitalar.getDataInternacaoAdministrativa());

		getFaturamentoFacade().rnCthpTrocaCnv( internacaoSeq, atendimento.getDthrInicio(), 
											   contaHospitalarOld.getConvenioSaudePlano().getId().getCnvCodigo(),
											   contaHospitalarOld.getConvenioSaudePlano().getId().getSeq(), 
											   contaHospitalar.getConvenioSaudePlano().getId().getCnvCodigo(), 
											   contaHospitalar.getConvenioSaudePlano().getId().getSeq(), 
											   nomeMicrocomputador, dataFimVinculoServidor);
	
	}
	
	/**
	 * ORADB AINP_VERIFICA_CNTA_CONV
	 */
	public void verificarContaConvenio(final Integer seqInternacao,
			final FatContasHospitalares contaHospitalarOld,
			final FatContasHospitalares contaHospitalar) throws ApplicationBusinessException {
		if (grupo(
				contaHospitalar.getConvenioSaudePlano().getId().getCnvCodigo())
				.equals(DominioGrupoConvenio.S)
				&& !grupo(
						contaHospitalarOld.getConvenioSaudePlano().getId()
								.getCnvCodigo()).equals(DominioGrupoConvenio.S)) {
			final List<CntaConv> listaCntaConv = this.obterConta(seqInternacao);
			for(final CntaConv cntaConv: listaCntaConv){
				this.excluirLancamentosConvenio(seqInternacao, cntaConv,
						contaHospitalar, contaHospitalarOld);
				if (!contaHospitalar.getDataInternacaoAdministrativa().equals(
						contaHospitalarOld.getDataInternacaoAdministrativa())) {
					this.atualizarAltaAdministrativa(cntaConv, contaHospitalar);
				}	
			}
		} else {
			final List<CntaConv> listaCntaConv = this.obterConta(seqInternacao);
			for(final CntaConv cntaConv: listaCntaConv){
				if (!grupo(
						contaHospitalar.getConvenioSaudePlano().getId()
								.getCnvCodigo()).equals(DominioGrupoConvenio.S)
						&& contaHospitalar.getConvenioSaudePlano()
								.getIndTipoPlano().equals(DominioTipoPlano.A)) {
					this.atualizarTipoConta(cntaConv);				
				}
			}
			
		}
	}
	
	private DominioGrupoConvenio grupo(final Short cnvCodigo)
			throws ApplicationBusinessException {
		final FatConvenioSaude covenioSaude = getFaturamentoFacade().obterFatConvenioSaudePorId(cnvCodigo);
		final DominioGrupoConvenio grupoConvenio = covenioSaude.getGrupoConvenio();
		return grupoConvenio;	
	}
	
	private List<CntaConv> obterConta(final Integer seqInternacao)
			throws ApplicationBusinessException {
		
		final List<CntaConv> listaCntaConv =  getFaturamentoFacade().pesquisarContaNotEcrtPorInternacao(seqInternacao);
		return listaCntaConv;	
	}
	
	private void excluirLancamentosConvenio(final Integer seqInternacao,
			final CntaConv cntaConv, final FatContasHospitalares contaHospitalar,
			final FatContasHospitalares contaHospitalarOld)
			throws ApplicationBusinessException {
		try{
			if(cntaConv!=null){
				ProcEfetDAO procEfetDAO = this.getProcEfetDAO();
				
				final List<ProcEfet> lista =  procEfetDAO.listarProcEfetPorCtacvNroPosDate(cntaConv.getNro(), contaHospitalar.getDataInternacaoAdministrativa());
				for(final ProcEfet procEfet: lista){
					procEfetDAO.remover(procEfet);
					procEfetDAO.flush();
				}
				
				final Integer retorno = getInternacaoFacade().calcularConta(cntaConv.getNro(),
						contaHospitalarOld.getConvenioSaudePlano().getId()
								.getCnvCodigo(), null);
				if (contaHospitalar.getDataInternacaoAdministrativa().equals(
						contaHospitalarOld.getDataInternacaoAdministrativa())
						&& retorno == 0) {
					final CntaConv cntaConvRetorno = getFaturamentoFacade().obterCntaConvPorChavePrimaria(cntaConv.getNro());
					if(cntaConvRetorno != null){
						this.getFaturamentoFacade().removerCntaConv(cntaConvRetorno, true);
					}

					final List<PacIntdConv> listaPacIntdConv = getPacienteFacade().pesquisarPorCodPrntDataInt(cntaConv.getIntdCodPrnt(), cntaConv.getIntdDataInt());
					
					if(listaPacIntdConv != null && !listaPacIntdConv.isEmpty()){
						final PacIntdConv pacIntdConv = listaPacIntdConv.get(0);
						if(!pacIntdConv.getCntaConvs().isEmpty()){
							throw new ApplicationBusinessException(
									CadastroInternacaoONExceptionCode.CONTA_CONVENIO_NAO_CADASTRADA);
						}
						this.getPacienteFacade().removerPacIntdConv(pacIntdConv, true);
					}
				}
			}
	
		} catch (final Exception e) {
			LOG.error(EXCECAO_CAPTURADA, e);
			if (e.getMessage()
					.equals(getResourceBundleValue(
									CadastroInternacaoONExceptionCode.CONTA_CONVENIO_NAO_CADASTRADA
											.toString()))) {
				throw new ApplicationBusinessException(
						CadastroInternacaoONExceptionCode.CONTA_CONVENIO_NAO_CADASTRADA);
			} else{
				throw new ApplicationBusinessException(
						CadastroInternacaoONExceptionCode.ERRO_ALTERAR_CONVENIO_INTERNACAO);
			}
		}
	}
	
	private void atualizarAltaAdministrativa(final CntaConv cntaConv,
			final FatContasHospitalares contaHospitalar) throws ApplicationBusinessException {
		try{
			final List<PacIntdConv> listaPacIntdConv = getPacienteFacade().pesquisarPorCodPrntDataInt(cntaConv.getIntdCodPrnt(), cntaConv.getIntdDataInt());
			for(final PacIntdConv pacIntdConv: listaPacIntdConv){
				pacIntdConv.setDataAltaMdca(contaHospitalar.getDataInternacaoAdministrativa());
				this.getPacienteFacade().atualizarPacIntdConv(pacIntdConv, true);	
			}	
		} catch (final Exception e) {
			LOG.error(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(
					CadastroInternacaoONExceptionCode.ERRO_ALTERAR_CONVENIO_INTERNACAO);
		}
		}
	
	private void atualizarTipoConta(final CntaConv cntaConv)
			throws ApplicationBusinessException {
		try{
			final List<PacIntdConv> listaPacIntdConv = getPacienteFacade().pesquisarPorCodPrntDataInt(cntaConv.getIntdCodPrnt(), cntaConv.getIntdDataInt());
			for(final PacIntdConv pacIntdConv: listaPacIntdConv){
				pacIntdConv.setDataAltaMdca(null);
				pacIntdConv.setQrtoLto("AMBUL");
				this.getPacienteFacade().atualizarPacIntdConv(pacIntdConv, true);	
			}
			final CntaConv cntaConvRetorno = getFaturamentoFacade().obterCntaConvPorChavePrimaria(cntaConv.getNro());
			cntaConvRetorno.setTipoAtd(DominioTipoPlano.A);
			this.getFaturamentoFacade().atualizarCntaConv(cntaConv, true);	
		} catch (final Exception e) {
			LOG.error(EXCECAO_CAPTURADA, e);
			throw new ApplicationBusinessException(
					CadastroInternacaoONExceptionCode.ERRO_ALTERAR_CONVENIO_INTERNACAO);
		}
		
	}

	protected ISolicitacaoInternacaoFacade getSolicitacaoInternacaoFacade() {
		return solicitacaoInternacaoFacade;
	}

	protected IResponsaveisPacienteFacade getResponsaveisPacienteFacade() {
		return responsaveisPacienteFacade;
	}

	protected ICadastrosBasicosInternacaoFacade getCadastrosBasicosInternacaoFacade() {
		return this.cadastrosBasicosInternacaoFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected ICadastroPacienteFacade getCadastroPacienteFacade() {
		return cadastroPacienteFacade;
	}

	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}

	protected IInternacaoFacade getInternacaoFacade() {
		return this.internacaoFacade;
	}

	protected IFaturamentoApoioFacade getFaturamentoApoioFacade() {
		return this.faturamentoApoioFacade;
	}

	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected IExamesLaudosFacade getExamesLaudosFacade() {
		return this.examesLaudosFacade;
	}
	
	protected IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected AinTiposCaraterInternacaoDAO getAinTiposCaraterInternacaoDAO() {
		return ainTiposCaraterInternacaoDAO;
	}
	
	protected AinCidsInternacaoDAO getAinCidsInternacaoDAO() {
		return ainCidsInternacaoDAO;
	}
	
	protected AinAtendimentosUrgenciaDAO getAinAtendimentosUrgenciaDAO() {
		return ainAtendimentosUrgenciaDAO;
	}
	
	protected AinDiariasAutorizadasDAO getAinDiariasAutorizadasDAO() {
		return ainDiariasAutorizadasDAO;
	}
	
	protected AinInternacaoDAO getAinInternacaoDAO() {
		return ainInternacaoDAO;
	}
	
	protected ProcEfetDAO getProcEfetDAO() {
		return procEfetDAO;
	}

	protected ObjetosOracleDAO getObjetosOracleDAO() {
		return objetosOracleDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
	public FatContasHospitalaresDAO getFatContasHospitalaresDAO() {
		return fatContasHospitalaresDAO;
	}
	
	protected AinResponsaveisPacienteDAO getAinResponsaveisPacienteDAO() {
		return ainResponsaveisPacienteDAO;
	}
	
	public void inserirCirurgiaDoCentroObstetrico(Integer pacCodigo,
			Short gestacaoSeqp, String nivelContaminacao,
			Date dataInicioProcedimento, Short salaCirurgicaSeqp,
			Short tempoDuracaoProcedimento, Short tanSeq,
			Short equipeSeqp, String tipoNascimento) throws BaseException, ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		AghParametros parametroCO = this.parametroFacade
				.buscarAghParametro(AghuParametrosEnum.P_UNIDADE_CO);

		AghAtendimentos atendimendoOrigemUrgencia = this.aghuFacade
				.pesquisarAtendimentosComOrigemUrgencia(pacCodigo, gestacaoSeqp);
		
		if(atendimendoOrigemUrgencia == null){
			atendimendoOrigemUrgencia = this.aghuFacade.pesquisarAtendimentosComOrigemAmbulatorial(pacCodigo, gestacaoSeqp);
		}

		FatConvenioSaudePlano convenioSaudePlano = validarAtendimentoUrgencia(pacCodigo, gestacaoSeqp, atendimendoOrigemUrgencia);
		
		List<MbcProfAtuaUnidCirgsVO> listaProfissionais = this.blocoCirurgicoFacade.obterMbcProfAtuaUnidCirgs(equipeSeqp.intValue(), parametroCO.getVlrNumerico().shortValue());

		AghParametros tipoNascimentoParametro = validarTipoNascimento(tipoNascimento);

		AghParametros tipoAnalgesiaParametro = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_ANALGESIA_PARTO);

		List<MbcFichaTipoAnestesiaVO> listaMbcFichaTipoAnestesiaVO = this.blocoCirurgicoFacade.buscarProcedimentoAgendado(atendimendoOrigemUrgencia.getSeq(),
						pacCodigo, 
						gestacaoSeqp, 
						tipoNascimentoParametro.getVlrNumerico().intValue(),
						tipoAnalgesiaParametro.getVlrNumerico().intValue());

		AghParametros especialidadeGinecoParametro = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_ESP_GINECO);

		MbcCirurgias cirurgia = this.criarCirurgiaAgendamentoCO(
				convenioSaudePlano,
				parametroCO.getVlrNumerico().shortValue(), salaCirurgicaSeqp,
				especialidadeGinecoParametro.getVlrNumerico().shortValue(),
				pacCodigo, dataInicioProcedimento, nivelContaminacao,
				atendimendoOrigemUrgencia, tempoDuracaoProcedimento,
				parametroCO.getVlrNumerico().shortValue());

		this.blocoCirurgicoFacade.persistirCirurgia(cirurgia, servidorLogado);
		
		MbcProcEspPorCirurgias procEspPorCirurgia = this.criarMbcProcEspPorCirurgias(
				cirurgia,//id 
				tipoNascimentoParametro.getVlrNumerico().intValue(), //id
				especialidadeGinecoParametro.getVlrNumerico().shortValue(), //id
				DominioIndRespProc.AGND, //id
				DominioSituacao.A,
				(byte)1,
				Boolean.TRUE);

		this.blocoCirurgicoFacade.persistirProcEspPorCirurgias(procEspPorCirurgia);
		
		MbcProfCirurgias profCirurgias = this.criarMbcProfCirurgias(cirurgia.getSeq(),
				listaProfissionais.get(0).getMatricula(),
				listaProfissionais.get(0).getVinCodigo(),
				listaProfissionais.get(0).getIndFuncao(),
				parametroCO.getVlrNumerico().shortValue(),
				Boolean.TRUE,
				Boolean.FALSE,
				Boolean.FALSE);
		
		this.blocoCirurgicoFacade.persistirProfessorResponsavel(profCirurgias);
		
		if(listaMbcFichaTipoAnestesiaVO != null && !listaMbcFichaTipoAnestesiaVO.isEmpty()){
			
			if(listaMbcFichaTipoAnestesiaVO.get(0).getTanSeq() != null){
				MbcAnestesiaCirurgias anestesiaCirurgias = this.criarMbcAnestesiaCirurgia(listaMbcFichaTipoAnestesiaVO.get(0).getTanSeq(),cirurgia.getSeq());
				this.blocoCirurgicoFacade.persistirAnestesiaCirurgias(anestesiaCirurgias);
			} else {
				MbcAnestesiaCirurgias anestesiaCirurgias = this.criarMbcAnestesiaCirurgia(tanSeq, cirurgia.getSeq());
				this.blocoCirurgicoFacade.persistirAnestesiaCirurgias(anestesiaCirurgias);
			} 
			
			if(listaMbcFichaTipoAnestesiaVO.get(0).getFicSeq() != null){
				MbcFichaAnestesias fichaAnestesia = this.blocoCirurgicoFacade.obterMbcFichaAnestesia(listaMbcFichaTipoAnestesiaVO.get(0).getFicSeq());
				fichaAnestesia.setCirurgia(cirurgia);
				this.blocoCirurgicoFacade.persistirMbcFichaAnestesias(fichaAnestesia, servidorLogado);
			}
		} else {
			MbcAnestesiaCirurgias anestesiaCirurgias = this.criarMbcAnestesiaCirurgia(tanSeq,cirurgia.getSeq());
			this.blocoCirurgicoFacade.persistirAnestesiaCirurgias(anestesiaCirurgias);
		}
	}

	private MbcAnestesiaCirurgias criarMbcAnestesiaCirurgia(Short tanSeq, Integer crgSeq) {
		MbcAnestesiaCirurgias anestesiaCirurgias = new MbcAnestesiaCirurgias();
		MbcAnestesiaCirurgiasId id = new MbcAnestesiaCirurgiasId();
		id.setTanSeq(tanSeq);
		id.setCrgSeq(crgSeq);
		anestesiaCirurgias.setId(id);
		anestesiaCirurgias.setCirurgia(this.blocoCirurgicoFacade.obterCirurgiaPorChavePrimaria(crgSeq));
		anestesiaCirurgias.setMbcTipoAnestesias(this.blocoCirurgicoFacade.obterMbcTipoAnestesiaPorChavePrimaria(tanSeq));
		
		return anestesiaCirurgias;
	}

	private MbcProfCirurgias criarMbcProfCirurgias(Integer crgSeq,
			Integer matricula, 
			Short vinCodigo, 
			String indFuncao, 
			Short unfSeq,
			Boolean indResponsavel, 
			Boolean indRealizou,
			Boolean indInclEscala) {
		
		MbcProfCirurgias profCirurgias = new MbcProfCirurgias();
		MbcProfCirurgiasId idProfCirurgia = new MbcProfCirurgiasId(crgSeq, matricula, vinCodigo, unfSeq, DominioFuncaoProfissional.getInstance(indFuncao));
		profCirurgias.setId(idProfCirurgia);
		profCirurgias.setIndResponsavel(indResponsavel);
		profCirurgias.setIndRealizou(indRealizou);
		profCirurgias.setIndInclEscala(indInclEscala);
		
		return profCirurgias;
	}

	private MbcProcEspPorCirurgias criarMbcProcEspPorCirurgias(
			MbcCirurgias cirurgia, 
			int eprPciSeq, 
			short eprEspSeq,
			DominioIndRespProc agnd, 
			DominioSituacao situacao, 
			byte quantidade, 
			Boolean indPrincipal) {
		
		MbcProcEspPorCirurgias procEspPorCirurgia = new MbcProcEspPorCirurgias();
		MbcProcEspPorCirurgiasId id = new MbcProcEspPorCirurgiasId(cirurgia.getSeq(), eprPciSeq, eprEspSeq, agnd);
		procEspPorCirurgia.setId(id);
		procEspPorCirurgia.setSituacao(situacao);
		procEspPorCirurgia.setQtd(quantidade);
		procEspPorCirurgia.setIndPrincipal(indPrincipal);
		
		procEspPorCirurgia.setMbcEspecialidadeProcCirgs(this.blocoCirurgicoFacade.pesquisarEspecialidadesProcCirgsPorEspSeqEPciSeq(eprEspSeq, eprPciSeq).get(0));
		
		return procEspPorCirurgia;
	}

	private FatConvenioSaudePlano validarAtendimentoUrgencia(Integer pacCodigo,Short gestacaoSeqp, AghAtendimentos atendimendoOrigemUrgencia) {

		if (atendimendoOrigemUrgencia != null) {
			return validarConvenioPorAtendimento(atendimendoOrigemUrgencia);
		}
		
		return null;
	}

	private FatConvenioSaudePlano validarConvenioPorAtendimento(
			AghAtendimentos atendimendoOrigemUrgencia) {

		FatConvenioSaudePlano convenioSaudePlano = null;

		if (DominioOrigemAtendimento.I == atendimendoOrigemUrgencia.getOrigem()) {

			AinInternacao internacao = this.internacaoFacade
					.obterInternacao(atendimendoOrigemUrgencia.getInternacao()
							.getSeq());
			convenioSaudePlano = internacao.getConvenioSaudePlano();
		} else if (DominioOrigemAtendimento.U == atendimendoOrigemUrgencia
				.getOrigem()) {

			AinAtendimentosUrgencia atendimentoUrgencia = this.internacaoFacade
					.obterAinAtendimentosUrgenciaPorChavePrimaria(atendimendoOrigemUrgencia
							.getAtendimentoUrgencia().getSeq());
			convenioSaudePlano = atendimentoUrgencia.getConvenioSaudePlano();
		} else if (DominioOrigemAtendimento.A == atendimendoOrigemUrgencia
				.getOrigem()) {

			AacConsultas consulta = this.ambulatorioFacade.obterAacConsulta(atendimendoOrigemUrgencia.getConsulta().getNumero());
			convenioSaudePlano = consulta.getConvenioSaudePlano();
		}

		return convenioSaudePlano;
	}

	private MbcCirurgias criarCirurgiaAgendamentoCO(
			FatConvenioSaudePlano convenioSaudePlano, Short sciUnfSeq,
			Short salaCirurgicaSeqp, Short espSeq, Integer pacCodigo,
			Date dataInicioProcedimento, String nivelContaminacao,
			AghAtendimentos atendimendoOrigemUrgencia,
			Short tempoDuracaoProcedimento, Short unfSeq) {

		MbcCirurgias cirurgia = new MbcCirurgias();
		cirurgia.setConvenioSaudePlano(convenioSaudePlano);
		cirurgia.setConvenioSaude(convenioSaudePlano.getConvenioSaude());
		cirurgia.setSciUnfSeq(sciUnfSeq);
		cirurgia.setSciSeqp(salaCirurgicaSeqp);
		cirurgia.setSalaCirurgica(this.blocoCirurgicoFacade.obterSalaCirurgicaPorId(new MbcSalaCirurgicaId(unfSeq, salaCirurgicaSeqp)));
		cirurgia.setEspecialidade(this.aghuFacade.obterAghEspecialidadesPorChavePrimaria(espSeq));
		 cirurgia.setPaciente(this.pacienteFacade.obterAipPacientesPorChavePrimaria(pacCodigo));
		cirurgia.setData(dataInicioProcedimento);
		cirurgia.setSituacao(DominioSituacaoCirurgia.RZDA);
		cirurgia.setNaturezaAgenda(DominioNaturezaFichaAnestesia.EMG);
		cirurgia.setContaminacao("L".equals(nivelContaminacao) ? Boolean.FALSE: Boolean.TRUE);
		cirurgia.setDigitaNotaSala(Boolean.FALSE);
		cirurgia.setPrecaucaoEspecial(Boolean.FALSE);
		cirurgia.setOrigemPacienteCirurgia(DominioOrigemPacienteCirurgia.I);
		cirurgia.setAtendimento(atendimendoOrigemUrgencia);
		cirurgia.setDataInicioCirurgia(dataInicioProcedimento);
		cirurgia.setUtilizaProAzot(Boolean.FALSE);
		cirurgia.setUtilizaO2(Boolean.FALSE);
		cirurgia.setTempoPrevistoHoras(getValorTempoMinimoHoras(tempoDuracaoProcedimento));
		cirurgia.setTempoPrevistoMinutos(getValorTempoMinimoMinuto(tempoDuracaoProcedimento));
		cirurgia.setUnidadeFuncional(aghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(unfSeq));
		cirurgia.setTemDescricao(Boolean.FALSE);
		cirurgia.setOverbooking(Boolean.FALSE);
		cirurgia.setMomentoAgenda(DominioMomentoAgendamento.PRV);

		return cirurgia;
	}
	
	public Short getValorTempoMinimoHoras(Short valorTempoMinimo) {
		final Integer valor = this.getValorTempoMinimo(valorTempoMinimo);
		final double divisao = valor / 60;
		BigDecimal trunc = NumberUtil.truncate(new BigDecimal(divisao), 0);
		String lpad = StringUtil.adicionaZerosAEsquerda(trunc, 2);
		return Short.valueOf(lpad);
	}

	public Byte getValorTempoMinimoMinuto(Short valorTempoMinimo) {
		final Integer valor = this.getValorTempoMinimo(valorTempoMinimo);
		final double restoDivisao = valor % 60;
		BigDecimal trunc = NumberUtil.truncate(new BigDecimal(restoDivisao), 0);
		String lpad = StringUtil.adicionaZerosAEsquerda(trunc, 2);
		return Byte.valueOf(lpad);
	}
	
	public Integer getValorTempoMinimo(Short valorTempoMinimo) {
		String lpad = StringUtil.adicionaZerosAEsquerda(valorTempoMinimo, 4);
		String substr1 = lpad.substring(0, 2);
		String substr2 = lpad.substring(2, 4);
		Short valor1 = Short.valueOf(substr1);
		Short valor2 = Short.valueOf(substr2);
		return valor1 * 60 + valor2;
	}
	
	protected AghParametros validarTipoNascimento(String tipoNascimento) throws ApplicationBusinessException {
		
		if("CESAREANA".equals(tipoNascimento)){
			return this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_CESAREANA);
		} else if("PARTO".equals(tipoNascimento)){
			return this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_PARTO);
		}
		
		throw new ApplicationBusinessException(CadastroInternacaoONExceptionCode.PARAMETRO_INCONSISTENTE_PARTO_CESAREA, tipoNascimento);
	}
	
	
}
