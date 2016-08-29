package br.gov.mec.aghu.paciente.cadastro.business;

import static br.gov.mec.aghu.model.AipPacientes.VALOR_MAXIMO_PRONTUARIO;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockOptions;

import br.gov.mec.aghu.administracao.business.IAdministracaoFacade;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.business.fonetizador.FonetizadorUtil;
import br.gov.mec.aghu.core.business.moduleintegration.InactiveModuleException;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.dominio.DominioCaracteristicaMicrocomputador;
import br.gov.mec.aghu.dominio.DominioEstadoCivil;
import br.gov.mec.aghu.dominio.DominioGrauInstrucao;
import br.gov.mec.aghu.dominio.DominioMomento;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoSumarioAlta;
import br.gov.mec.aghu.dominio.DominioTipoEndereco;
import br.gov.mec.aghu.dominio.DominioTipoImpressao;
import br.gov.mec.aghu.dominio.DominioTipoProntuario;
import br.gov.mec.aghu.exameselaudos.business.IExamesLaudosFacade;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghCaractMicrocomputador;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipAlturaPacientes;
import br.gov.mec.aghu.model.AipBairrosCepLogradouro;
import br.gov.mec.aghu.model.AipBairrosCepLogradouroId;
import br.gov.mec.aghu.model.AipCidades;
import br.gov.mec.aghu.model.AipCodeControls;
import br.gov.mec.aghu.model.AipConveniosSaudePaciente;
import br.gov.mec.aghu.model.AipConveniosSaudePacienteId;
import br.gov.mec.aghu.model.AipEnderecosPacientes;
import br.gov.mec.aghu.model.AipEnderecosPacientesId;
import br.gov.mec.aghu.model.AipEnderecosPacientesJn;
import br.gov.mec.aghu.model.AipFonemaNomeSocialPacientes;
import br.gov.mec.aghu.model.AipFonemaPacientes;
import br.gov.mec.aghu.model.AipFonemasMaePaciente;
import br.gov.mec.aghu.model.AipNacionalidades;
import br.gov.mec.aghu.model.AipOrgaosEmissores;
import br.gov.mec.aghu.model.AipPacienteDadoClinicos;
import br.gov.mec.aghu.model.AipPacienteProntuario;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.AipPacientesDadosCns;
import br.gov.mec.aghu.model.AipPacientesHist;
import br.gov.mec.aghu.model.AipPacientesJn;
import br.gov.mec.aghu.model.AipPesoPacientes;
import br.gov.mec.aghu.model.AipProntuarioLiberados;
import br.gov.mec.aghu.model.AipSolicitacaoProntuarios;
import br.gov.mec.aghu.model.AipUfs;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.McoExameFisicoRns;
import br.gov.mec.aghu.model.McoGestacoes;
import br.gov.mec.aghu.model.McoNascimentos;
import br.gov.mec.aghu.model.McoRecemNascidos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.business.validacaoprontuario.InterfaceValidaProntuario;
import br.gov.mec.aghu.paciente.business.validacaoprontuario.ValidaProntuarioException;
import br.gov.mec.aghu.paciente.business.validacaoprontuario.ValidaProntuarioFactory;
import br.gov.mec.aghu.paciente.cadastrosbasicos.business.ICadastrosBasicosPacienteFacade;
import br.gov.mec.aghu.paciente.dao.AipCodeControlsDAO;
import br.gov.mec.aghu.paciente.dao.AipConveniosSaudePacienteDAO;
import br.gov.mec.aghu.paciente.dao.AipEnderecosPacientesJnDAO;
import br.gov.mec.aghu.paciente.dao.AipOrgaosEmissoresDAO;
import br.gov.mec.aghu.paciente.dao.AipPacienteDadoClinicosDAO;
import br.gov.mec.aghu.paciente.dao.AipPacienteProntuarioDAO;
import br.gov.mec.aghu.paciente.dao.AipPacientesDAO;
import br.gov.mec.aghu.paciente.dao.AipPacientesDadosCnsDAO;
import br.gov.mec.aghu.paciente.dao.AipPesoPacientesDAO;
import br.gov.mec.aghu.paciente.dao.AipProntuarioLiberadosDAO;
import br.gov.mec.aghu.paciente.dao.AipSolicitacaoProntuariosDAO;
import br.gov.mec.aghu.paciente.historico.business.IHistoricoPacienteFacade;
import br.gov.mec.aghu.perinatologia.business.IPerinatologiaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

/**
 * O propósito desta classe é prover os métodos de negócio para o cadastro de
 * pacientes.
 */

@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.ExcessiveClassLength", "PMD.CyclomaticComplexity"})
@Stateless
public class CadastroPacienteON extends BaseBusiness {

	private static final String PACIENTE_ATUALIZADO = "Paciente Atualizado: ";

	private static final String RECEM_NASCIDO = "Recem Nascido: ";

	private static final String HIFEM = " - ";

	private static final String PACIENTE = "Paciente: ";

	@EJB
	private EnderecoON enderecoON;
	
	@EJB
	private PacienteJournalON pacienteJournalON;
	
	@EJB
	private CadastroPacienteRN cadastroPacienteRN;
	
	@EJB
	private PesoPacienteRN pesoPacienteRN;
	
	private static final Log LOG = LogFactory.getLog(CadastroPacienteON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AipEnderecosPacientesJnDAO aipEnderecosPacientesJnDAO;
	
	@Inject
	private AipPacientesDadosCnsDAO aipPacientesDadosCnsDAO;
	
	@EJB
	private IPerinatologiaFacade perinatologiaFacade;
	
	@Inject
	private AipOrgaosEmissoresDAO aipOrgaosEmissoresDAO;
	
	@Inject
	private AipPesoPacientesDAO aipPesoPacientesDAO;
	
	@Inject
	private AipPacientesDAO aipPacientesDAO;
	
	@EJB
	private IHistoricoPacienteFacade historicoPacienteFacade;
	
	@EJB
	private ICadastrosBasicosPacienteFacade cadastrosBasicosPacienteFacade;
	
	@Inject
	private AipSolicitacaoProntuariosDAO aipSolicitacaoProntuariosDAO;
	
	@EJB
	private IExamesLaudosFacade examesLaudosFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@Inject
	private AipPacienteDadoClinicosDAO aipPacienteDadoClinicosDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private ICentroCustoFacade centroCustoFacade;
	
	@EJB
	private IAdministracaoFacade administracaoFacade;
	
	@Inject
	private AipConveniosSaudePacienteDAO aipConveniosSaudePacienteDAO;
	
	@Inject
	private AipCodeControlsDAO aipCodeControlsDAO;
	
	@Inject
	private AipProntuarioLiberadosDAO aipProntuarioLiberadosDAO;
	
	@Inject	
	private ValidaProntuarioFactory validaProntuarioFactory;

	@Inject
	private AipPacienteProntuarioDAO aipPacienteProntuarioDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -4652536507130136509L;


	/**
	 * Enumeracao com os codigos de mensagens de excecoes negociais do cadastro
	 * paciente.
	 * 
	 * Cada entrada nesta enumeracao deve corresponder a um chave no message
	 * bundle de paciente.
	 * 
	 * Porém se não houver uma este valor será enviado como mensagem de execeção
	 * sem localização alguma.
	 * 
	 * 
	 */
	private enum CadastroPacienteONExceptionCode implements
			BusinessExceptionCode {
		VALIDACAO_SERVIDOR_CENTRO_CUSTO_INCLUSAO_PACIENTE, VALIDACAO_SERVIDOR_CENTRO_CUSTO_EDICAO_PACIENTE,
		VALIDACAO_SERVIDOR_CENTRO_CUSTO_ALTERACAO, VALIDACAO_PACIENTE_COM_PRONTUARIO_SEM_COR,
		PRONTUARIO_MANUAL_MAIOR_MAXIMO, ERRO_NACIONALIDADE_INATIVA, ERRO_INFORMAR_TELEFONE, ERRO_EXCLUIR_PLANO_PACIENTE,
		ERRO_EXCLUIR_SOLICITACAO_PRONTUARIO, ERRO_EXCLUIR_DADOS_CNS, AIP_NOME_PACIENTE_NULO, AIP_NOME_MAE_NULO, AIP_MATRICULA_REQUERIDA,
		AIP_PLANO_ATIVO_DUPLICADO, AIP_REATIVACAO_PLANO, AIP_NOME_DE_UMA_PALAVRA, AIP_DATA_NASCIMENTO_INVALIDA, AIP_ERRO_BUSCA_PRONTUARIO_LIBERADO,
		AIP_PESO_INVALIDO, AIP_PESO_NASCIMENTO_INVALIDO, AIP_PESO_EXISTENTE_NULO, AIP_DATA_NASCIMENTO_NAO_INFORMADA, AIP_USUARIO_NAO_CADASTRADO,
		DADOS_EMISSAO_DOCUMENTOS_INCOMPLETOS, DADOS_CERTIDAO_INCOMPLETOS, AIP_SEXO_BIOLOGICO_NAO_PODE_SER_ALTERADO, AIP_TIPO_CERTIDAO_PREENCHIDO,
		ERRO_RECUPERAR_PACIENTE, ERRO_VALIDACAO_MODELO_PACIENTE, MENSAGEM_ERRO_REMOCAO_SOLICITACAO_PRONTUARIO,
		ERRO_PERSISTIR_SOLICITACAO_PRONTUARIO, ERRO_PERSISTIR_MESMO_PRONTUARIO, MENSAGEM_ERRO_IMPRIMIR_TIPO_IMPRESSAO_NAO_SELECIONADO,
		MENSAGEM_ERRO_IMPRIMIR_VOLUME_PACIENTE_NULO, MENSAGEM_ERRO_IMPRIMIR_VOLUME_INFORMADO_NULO, MENSAGEM_ERRO_IMPRESSAO,
		MENSAGEM_ERRO_IMPRIMIR_VOLUME_INFORMADO_MAIOR_VOLUME_PACIENTE, AIP_PRONTUARIO_JA_GERADO,
		MENSAGEM_ERRO_IMPRIMIR_TODOS_VOLUMES_PACIENTE_SEM_VOLUMES, VALIDACAO_PACIENTE_COM_PRONTUARIO_SEM_SEXO,
		VALIDACAO_PACIENTE_COM_PRONTUARIO_SEM_INSTRUCAO, VALIDACAO_PACIENTE_COM_PRONTUARIO_SEM_PAI,
		VALIDACAO_PACIENTE_COM_PRONTUARIO_SEM_ESTADO_CIVIL, AIP_PRONTUARIO_VALIDADOR_CONFIG_ERROR, ERRO_PRONTUARIO_VIRTUAL_MENOR_9000000,
		AIP_VERIFICAR_PARAMETRO_NACIONALIDADE_PADRAO, AIP_VERIFICAR_PARAMETRO_UF_PADRAO, AIP_VERIFICAR_PARAMETRO_CIDADE_PADRAO,
		SITUACAO_ANTERIOR_NAO_ENCONTRADA, REG_NASCIMENTO_MAE_OBITO, MENSAGEM_ERRO_HIBERNATE_VALIDATION, MSG_MESES_GESTACAO_RANGE,
		MSG_APGAR1_RANGE, MSG_APGAR5_RANGE, MSG_TEMPERATURA_RANGE, MSG_IG_SEMANAS_RANGE,
		ERRO_VIOLACAO_CONSTRAINTS, ERRO_PERSISTENCIA, PACIENTE_SEM_ENDERECO_29045, PACIENTE_DOCUMENTOS_29333, ERRO_CPF_INVALIDO,
		PACIENTE_COM_CPF_DUPLICADO, PACIENTE_COM_CNS_DUPLICADO;
	}

	/**
	 * Método responsável pela persistência de um ou mais pacientes
	 * recém-nascidos. Ele deverá usar dados da mãe para completar o cadastro
	 * dos recém-nascidos
	 * 
	 * @param mae
	 *            , recemNascidos
	 * @throws ValidacaoCadastroPacienteException
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public void persistirRecemNascidos(AghAtendimentos atendimentoMae,
			List<AipPacientes> recemNascidos, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {

		AipPacientes mae = atendimentoMae.getPaciente();
		

		try {
			validaProntuarioFactory.getValidaProntuario(true);
		} catch (ValidaProntuarioException e) {
			throw new ApplicationBusinessException(
					CadastroPacienteONExceptionCode.AIP_PRONTUARIO_VALIDADOR_CONFIG_ERROR,
					e);
		}

		AipCodeControlsDAO aipCodeControlsDAO = this.getAipCodeControlsDAO();

		for (AipPacientes recemNascido : recemNascidos) {

			LOG.info("<<<<<<<<<<<<<<<<<<AIP_PACIENTE_01>>>>>>>>>>>>>>>>");
			LOG.info(RECEM_NASCIDO + recemNascido.getProntuario() + HIFEM + recemNascido.getNome());
			LOG.info("Mãe: " + mae.getProntuario() + HIFEM + mae.getNome());

			// Aqui foi feito uso do lockMode.UPGRADE para fazer um lock
			// pessimista na tabela AIP_CODE_CONTROLS
			AipCodeControls aipCodeControlsComLock = aipCodeControlsDAO.obterCodeControl("AIP_PAC_SQ3", LockOptions.UPGRADE);

			Integer valorNovoProntuario = aipCodeControlsComLock.getNextValue();

			// Não pode ser menor que NOVE MILHÕES o valor do prontuário virtual
			if (valorNovoProntuario < 9000000) {
				throw new BaseException(
						CadastroPacienteONExceptionCode.ERRO_PRONTUARIO_VIRTUAL_MENOR_9000000);
			}

//			Integer valorIncremento = valorNovoProntuario + 1;
//			aipCodeControlsComLock.setNextValue(valorIncremento);
//			String strValorNovoProntuario = valorNovoProntuario.toString()
//					+ validadorProntuario.executaModulo(valorNovoProntuario);
//			recemNascido.setProntuario(Integer.valueOf(strValorNovoProntuario));
			
			recemNascido.setIndGeraProntuario(DominioSimNao.N);
			recemNascido.setGeraProntuarioVirtual(true);
			recemNascido.setDtIdentificacao(new Date());
			recemNascido.setPrntAtivo(DominioTipoProntuario.R);
			recemNascido.setCadConfirmado(DominioSimNao.N);
			recemNascido.setObservacao("Nascido no centro obstétrico da Instituição. Cadastro gerado pelo sistema");
			recemNascido.setGrauInstrucao(DominioGrauInstrucao.NENHUM);
			recemNascido.setNomePai("A informar");
			recemNascido.setEstadoCivil(DominioEstadoCivil.S);
			recemNascido.setNomeMae(mae.getNome());
			recemNascido.setSexo(recemNascido.getSexoBiologico());

			AghParametros param = null;
			AipNacionalidades nacionalidade = null;
			AipUfs uf = null;
			AipCidades naturalidade = null;

			param = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_NACIONALIDADE_PADRAO);
			nacionalidade = this.getCadastrosBasicosPacienteFacade().obterNacionalidade(param.getVlrNumerico().intValue());
			if (nacionalidade == null) {
				throw new ApplicationBusinessException(CadastroPacienteONExceptionCode.AIP_VERIFICAR_PARAMETRO_NACIONALIDADE_PADRAO);
			}
			recemNascido.setAipNacionalidades(nacionalidade);

			param = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_UF_PADRAO);
			uf = this.getCadastrosBasicosPacienteFacade().obterUF(param.getVlrTexto());
			if (uf == null) {
				throw new ApplicationBusinessException(CadastroPacienteONExceptionCode.AIP_VERIFICAR_PARAMETRO_UF_PADRAO);
			}

			param = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_CIDADE_PADRAO);
			List<AipCidades> cidades = this.getCadastrosBasicosPacienteFacade()
					.pesquisarCidades(null, null, param.getVlrTexto(), null,
							null, null, uf);
			if (cidades.isEmpty()) {
				String estado = uf!=null?"/"+uf.getSigla():"";
				String msg = param.getVlrTexto() + estado;
				throw new ApplicationBusinessException(CadastroPacienteONExceptionCode.AIP_VERIFICAR_PARAMETRO_CIDADE_PADRAO,msg);
			}
			naturalidade = cidades.get(0);
			recemNascido.setAipCidades(naturalidade);
			
						
			List<AipEnderecosPacientes> enderecosMae = pacienteFacade.obterEnderecosPacientes(mae.getCodigo()); 
			recemNascido.setEnderecos(new HashSet<AipEnderecosPacientes>());
			
			for (AipEnderecosPacientes enderecoMae : enderecosMae) {
				if (enderecoMae.getTipoEndereco().equals(DominioTipoEndereco.R)) {
					AipEnderecosPacientes enderecoRecemNascido = new AipEnderecosPacientes();
					try {
						enderecoRecemNascido = (AipEnderecosPacientes) BeanUtils
								.cloneBean(enderecoMae);
					} catch (Exception e) {
						LOG.error("Não conseguiu clonar enderecoMae para enderecoRecemNascido.");
					}
					enderecoRecemNascido.setAipPaciente(recemNascido);
					enderecoRecemNascido.setId(new AipEnderecosPacientesId());
					enderecoRecemNascido.setVersion(0);

					recemNascido.getEnderecos().add(enderecoRecemNascido);
				}

			}

			recemNascido = persistirPaciente(recemNascido, nomeMicrocomputador);

			LOG.info("<<<<<<<<<<<<<<<<<<AIP_PACIENTE_2>>>>>>>>>>>>>>>>");
			LOG.info(RECEM_NASCIDO + recemNascido.getProntuario() + HIFEM + recemNascido.getNome());
			LOG.info("Mãe: " + mae.getProntuario() + HIFEM + mae.getNome());

			AghAtendimentos atendimentoRecemNascido = recemNascido.getAghAtendimentos().iterator().next();

			// Popula as colunas de referência à gestação no atendimento da mãe
			// e do RN
			this.preencherChavesGestacaoMae(atendimentoMae);
			atendimentoRecemNascido.setGsoPacCodigo(atendimentoMae.getGsoPacCodigo());
			atendimentoRecemNascido.setGsoSeqp(atendimentoMae.getGsoSeqp());
			
			getPacienteFacade().atualizarAtendimentoGestante(atendimentoMae.getGsoPacCodigo(), atendimentoMae.getGsoSeqp(), nomeMicrocomputador, atendimentoMae.getSeq());
			
			AghAtendimentos atendimentoOld = this.getPacienteFacade().clonarAtendimento(atendimentoRecemNascido);

			atendimentoRecemNascido.setPaciente(recemNascido);
			atendimentoRecemNascido.setProntuario(recemNascido.getProntuario());
			atendimentoRecemNascido.setOrigem(DominioOrigemAtendimento.I);
			atendimentoRecemNascido.setQuarto(atendimentoMae.getQuarto());
			atendimentoRecemNascido.setLeito(atendimentoMae.getLeito());
			atendimentoRecemNascido.setUnidadeFuncional(atendimentoMae.getUnidadeFuncional());
			atendimentoRecemNascido.setDthrInicio(new Date());
			atendimentoRecemNascido.setIndPacPediatrico(true);
			atendimentoRecemNascido.setIndPacPrematuro(false);
			atendimentoRecemNascido.setIndPacAtendimento(DominioPacAtendimento.S);
			atendimentoRecemNascido.setAtendimentoMae(atendimentoMae);
			atendimentoRecemNascido.setIndSitSumarioAlta(DominioSituacaoSumarioAlta.P);
			atendimentoRecemNascido.setOrigem(DominioOrigemAtendimento.N);
			this.getPacienteFacade().persistirAtendimento(atendimentoRecemNascido, atendimentoOld, nomeMicrocomputador, dataFimVinculoServidor);

			LOG.info("<<<<<<<<<<<<<<<<<<AIP_PACIENTE_44 antes do flush final>>>>>>>>>>>>>>>>");
			LOG.info(RECEM_NASCIDO + recemNascido.getProntuario() + HIFEM + recemNascido.getNome());

			aipPacientesDAO.flush();
			LOG.info("<<<<<<<<<<<<<<<<<<AIP_PACIENTE_44 depois do flush final>>>>>>>>>>>>>>>>");
			LOG.info(RECEM_NASCIDO + recemNascido.getProntuario() + HIFEM + recemNascido.getNome());
		}
	}

	private String obterProntuarioVirtualParaRecemNascido(InterfaceValidaProntuario validadorProntuario, AipCodeControlsDAO aipCodeControlsDAO)
			throws ApplicationBusinessException {

		String nome = null;
		String strValorNovoProntuario = null;

		AipCodeControls aipCodeControlsComLock = aipCodeControlsDAO.obterCodeControl("AIP_PAC_SQ3", LockOptions.UPGRADE);

		do {
			// Aqui foi feito uso do lockMode.UPGRADE para fazer um lock
			// pessimista na tabela AIP_CODE_CONTROLS

			Integer valorNovoProntuario = aipCodeControlsComLock.getNextValue();

			// Não pode ser menor que NOVE MILHÕES o valor do prontuário virtual
			if (valorNovoProntuario < 9000000) {
				throw new ApplicationBusinessException(CadastroPacienteONExceptionCode.ERRO_PRONTUARIO_VIRTUAL_MENOR_9000000);
			}

			Integer valorIncremento = valorNovoProntuario + 1;
			aipCodeControlsComLock.setNextValue(valorIncremento);

			strValorNovoProntuario = valorNovoProntuario.toString() + validadorProntuario.executaModulo(valorNovoProntuario);

			nome = aipPacientesDAO.obterNomePacientePorProntuario(Integer.valueOf(strValorNovoProntuario));

		} while (nome != null);

		return strValorNovoProntuario;
	}

	/**
	 * Método que seta as informações de gestação no atendimento da mãe
	 * 
	 * @param atendimentoMae
	 */
	private void preencherChavesGestacaoMae(AghAtendimentos atendimentoMae) {
		if (atendimentoMae.getGsoPacCodigo() == null) {
			AghAtendimentos ultimoAtendimentoMae = getAghuFacade()
					.obterUltimoAtendimentoGestacao(
							atendimentoMae.getPaciente());
			Integer seqp = 1;
			if (ultimoAtendimentoMae != null) {
				seqp = ultimoAtendimentoMae.getGsoSeqp() + 1;
			}
			atendimentoMae.setGsoPacCodigo(atendimentoMae.getPaciente()
					.getCodigo());
			atendimentoMae.setGsoSeqp(seqp.shortValue());
		}
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	/**
	 * Verifica se a mãe não teve óbito
	 * 
	 * @param mae
	 * @throws ApplicationBusinessException
	 */
	public void verificarMaeEmObito(AipPacientes mae)
			throws ApplicationBusinessException {
		if (mae.getDtObito() != null || mae.getDtObitoExterno() != null) {
			throw new ApplicationBusinessException(
					CadastroPacienteONExceptionCode.REG_NASCIMENTO_MAE_OBITO);
		}
	}

	/**
	 * Método responsável pela persistência de um paciente.
	 * 
	 * @param paciente
	 * @throws ValidacaoCadastroPacienteException
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public AipPacientes persistirPaciente(AipPacientes aipPaciente, String nomeMicrocomputador)
			throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AghParametros paramNumeroMaximoProntuarioManual = this
				.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_NUMERO_MAXIMO_PRONTUARIO_MANUAL);

		Long numeroMaximoProntuarioManual = null;
		
		if (aipPaciente.getCpf() != null  && !CoreUtil.validarCPF(aipPaciente.getCpf().toString())) {
			throw new ApplicationBusinessException(CadastroPacienteONExceptionCode.ERRO_CPF_INVALIDO);
		}
		
		if (paramNumeroMaximoProntuarioManual != null && paramNumeroMaximoProntuarioManual.getVlrNumerico() != null) {
			numeroMaximoProntuarioManual = paramNumeroMaximoProntuarioManual.getVlrNumerico().longValue();
		}

		if (aipPaciente.getProntuarioEditado() != null
				&& numeroMaximoProntuarioManual != null
				&& aipPaciente.getProntuarioEditado() > numeroMaximoProntuarioManual) {

			throw new ApplicationBusinessException(
					CadastroPacienteONExceptionCode.PRONTUARIO_MANUAL_MAIOR_MAXIMO,	numeroMaximoProntuarioManual);

		}

		if (servidorLogado == null) {
			throw new ApplicationBusinessException(
					CadastroPacienteONExceptionCode.AIP_USUARIO_NAO_CADASTRADO);
		}

		aipPaciente.setNome(aipPaciente.getNome() == null ? null : aipPaciente.getNome().trim().toUpperCase());
		aipPaciente.setNomeMae(aipPaciente.getNomeMae() == null ? null : aipPaciente.getNomeMae().trim().toUpperCase());
		aipPaciente.setNomeSocial(aipPaciente.getNomeSocial() == null ? null : aipPaciente.getNomeSocial().trim().toUpperCase());

		if (aipPaciente.getAipPacientesDadosCns() != null) {
			persistirCartaoSus(aipPaciente);
		}

		this.validarTelefones(aipPaciente);
	
		this.verficaCepEnderecoCepLogradouro(aipPaciente);

		if (aipPaciente.getCodigo() == null) {
			// inclusão
			try {
				LOG.info("<<<<<<<<<<<<<<<<<<AIP_PACIENTE_3>>>>>>>>>>>>>>>>");
				LOG.info(PACIENTE + aipPaciente.getProntuario() + HIFEM + aipPaciente.getNome());
				
				this.incluirPaciente(aipPaciente, nomeMicrocomputador);

			} catch (BaseException e) {
				// Em caso de erro negocial desfaz a geração da chave e 
				// do prontuário de paciente para evitar erro na gravação
				// dos endereços do paciente
				aipPaciente.setCodigo(null);
				if (aipPaciente.isGerarProntuario()) {
					aipPaciente.setProntuario(null);
				}
				throw e;
			} catch (ConstraintViolationException e){
				aipPaciente.setCodigo(null);
				if (aipPaciente.isGerarProntuario()) {
					aipPaciente.setProntuario(null);
				}
				throw e;
			} catch (Exception e){
				aipPaciente.setCodigo(null);
				if (aipPaciente.isGerarProntuario()) {
					aipPaciente.setProntuario(null);
				}
				LOG.info("<<<<<<<<<<<<<<<<<<AIP_PACIENTE_4>>>>>>>>>>>>>>>>", e);
				LOG.info(PACIENTE + aipPaciente.getProntuario() + HIFEM + aipPaciente.getNome());
				throw e;
			}
		} else {
			// edição
			LOG.info("<<<<<<<<<<<<<<<<<<AIP_PACIENTE_5>>>>>>>>>>>>>>>>");
			LOG.info(PACIENTE + aipPaciente.getProntuario() + HIFEM + aipPaciente.getNome());

			aipPaciente = this.atualizarPaciente(aipPaciente, nomeMicrocomputador, servidorLogado, false);
		}

		List<AipEnderecosPacientes> enderecosPaciente = new ArrayList<AipEnderecosPacientes>(aipPaciente.getEnderecos());
		for (AipEnderecosPacientes enderecoPaciente : enderecosPaciente) {
			AipEnderecosPacientes enderecoPacienteOld = this.getEnderecoON().obterEnderecoBanco(enderecoPaciente.getId());
			enderecoPacienteOld.setId(enderecoPaciente.getId());
			if (this.getCadastroPacienteRN().isEnderecoModificado(enderecoPaciente, enderecoPacienteOld)) {
				this.getEnderecoON().aiptEnpAru(enderecoPacienteOld,enderecoPaciente);
			}
		}
		
		LOG.info("<<<<<<<<<<<<<<<<<<AIP_PACIENTE_6 Antes do flush >>>>>>>>>>>>>>>>");
		LOG.info(PACIENTE + aipPaciente.getProntuario() + HIFEM + aipPaciente.getNome());

		this.getAipPacientesDAO().flush();

		LOG.info("<<<<<<<<<<<<<<<<<<AIP_PACIENTE_7 Após o flush >>>>>>>>>>>>>>>>");
		LOG.info(PACIENTE + aipPaciente.getProntuario() + HIFEM + aipPaciente.getNome());

		return aipPaciente;

	}

	private void verficaCepEnderecoCepLogradouro(AipPacientes aipPaciente) {
		for (AipEnderecosPacientes endereco : aipPaciente.getEnderecos()) { 
		if(endereco.getAipBairrosCepLogradouro()!= null){
			if (endereco.getAipBairrosCepLogradouro().getCepLogradouro().getId().getCep().equals(endereco.getCep())) {
				endereco.setCep(null);
				}
			}
		}
	}

	private void validarNacionalidade(AipPacientes aipPaciente)
			throws ApplicationBusinessException {
		AipNacionalidades nacionalidade = aipPaciente.getAipNacionalidades();

		if (nacionalidade != null && !nacionalidade.isAtivo()) {
			throw new ApplicationBusinessException(
					CadastroPacienteONExceptionCode.ERRO_NACIONALIDADE_INATIVA);
		}

	}

	/**
	 * Método usado para validar entrada de telefones. O número do telefone e o
	 * DDD devem sempre ser informados juntos (se houver um, o outro tb deve ser
	 * informado.)
	 * 
	 * @param aipPaciente
	 * @throws ApplicationBusinessException
	 */
	private void validarTelefones(AipPacientes aipPaciente)
			throws ApplicationBusinessException {

		Short dddFoneResidencial = aipPaciente.getDddFoneResidencial();
		Long foneResidencial = aipPaciente.getFoneResidencial();
		Short dddFoneRecado = aipPaciente.getDddFoneRecado();
		String foneRecado = aipPaciente.getFoneRecado();

		if ((dddFoneResidencial == null && foneResidencial != null)
				|| (dddFoneResidencial != null && foneResidencial == null)) {
			throw new ApplicationBusinessException(
					CadastroPacienteONExceptionCode.ERRO_INFORMAR_TELEFONE);
		}

		if ((dddFoneRecado != null && StringUtils.isBlank(foneRecado))
				|| (dddFoneRecado == null && StringUtils.isNotBlank(foneRecado))) {
			throw new ApplicationBusinessException(
					CadastroPacienteONExceptionCode.ERRO_INFORMAR_TELEFONE);

		}

	}

	/**
	 * Método responsável por incluir um paciente novo.
	 * 
	 * @param paciente
	 * @throws ValidacaoCadastroPacienteException
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	private void incluirPaciente(AipPacientes aipPaciente, String nomeMicrocomputador) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogadoSemCache();

		LOG.info("<<<<<<<<<<<<<<<<<<AIP_PACIENTE_8>>>>>>>>>>>>>>>>");
		LOG.info(PACIENTE + aipPaciente.getProntuario() + HIFEM + aipPaciente.getNome());

		validarDadosPaciente(aipPaciente);
		
		aipPaciente.setRapServidoresCadastro(servidorLogado);

		FccCentroCustos fccCentroCustos = servidorLogado.getCentroCustoLotacao();
		if (fccCentroCustos == null || fccCentroCustos.getCodigo() == null) {
			// #54605
			AghParametros cctPadraoServidor = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_CCT_PADRAO_CAD_PACIENTE);
			if (cctPadraoServidor != null && cctPadraoServidor.getVlrNumerico() != null) {
				fccCentroCustos = centroCustoFacade.obterCentroCusto(cctPadraoServidor.getVlrNumerico().intValue());
				if(fccCentroCustos != null) {
					aipPaciente.setFccCentroCustosCadastro(fccCentroCustos);
				}
				else {
					throw new ApplicationBusinessException(CadastroPacienteONExceptionCode.VALIDACAO_SERVIDOR_CENTRO_CUSTO_INCLUSAO_PACIENTE);
				}
			}

		} else {
			aipPaciente.setFccCentroCustosCadastro(servidorLogado.getCentroCustoLotacao());
		}
		
		aipPaciente.setDtIdentificacao(new Date());
		verificarSexoBiologico(aipPaciente);
		aipPaciente.setCriadoEm(new Date());

		//this.obterValorSequencialId(aipPaciente);
			
		if (aipPaciente.getProntuario() != null) {
			persistirProntuarioSeNaoCadastrado(aipPaciente, servidorLogado);
		}
		this.validarProntuario(aipPaciente);

		LOG.info("<<<<<<<<<<<<<<<<<<AIP_PACIENTE_9>>>>>>>>>>>>>>>>");
		LOG.info(PACIENTE + aipPaciente.getProntuario() + HIFEM + aipPaciente.getNome());
		
		this.getCadastroPacienteRN().atualizarSituacaoProntuarioInclusao(aipPaciente, servidorLogado != null ? servidorLogado.getUsuario() : null);
		
		if (aipPaciente.getAipPacientesDadosCns() != null && aipPaciente.getAipPacientesDadosCns().getPacCodigo() == null) {
			this.getAipPacientesDadosCnsDAO().persistir(aipPaciente.getAipPacientesDadosCns());
			this.getAipPacientesDadosCnsDAO().flush();
		}
		LOG.info("<<<<<<<<<<<<<<<<<<AIP_PACIENTE_24>>>>>>>>>>>>>>>>");
		LOG.info(PACIENTE + aipPaciente.getProntuario() + HIFEM + aipPaciente.getNome());
		
		this.getCadastroPacienteRN().inserirPaciente(aipPaciente);

		LOG.info("<<<<<<<<<<<<<<<<<<AIP_PACIENTE_25>>>>>>>>>>>>>>>>");
		LOG.info(PACIENTE + aipPaciente.getProntuario() + HIFEM + aipPaciente.getNome());
		if (aipPaciente.getAipPacientesDadosCns() != null && aipPaciente.getAipPacientesDadosCns().getPacCodigo() == null) {
			this.getAipPacientesDadosCnsDAO().persistir(aipPaciente.getAipPacientesDadosCns());
		}
		this.getAipPacientesDadosCnsDAO().flush();

		
		Set<AipEnderecosPacientes> enderecos = aipPaciente.getEnderecos();
		if (enderecos!=null && !enderecos.isEmpty()){			
//			aipPaciente.setEnderecos(enderecos);
			this.getEnderecoON().atribuirSequencialEnderecos(aipPaciente);			
			//this.atualizarPaciente(aipPaciente, nomeMicrocomputador);
		}	
		
		//Tentativa de correção do problema #29333
		this.getAipPacientesDAO().flush();
		LOG.info("<<<<<<<<<<<<<<<<<<AIP_PACIENTE_26>>>>>>>>>>>>>>>>");
		LOG.info(PACIENTE + aipPaciente.getProntuario() + HIFEM + aipPaciente.getNome());

		criarFonemasPaciente(aipPaciente);
	}
	
	private void persistirProntuarioSeNaoCadastrado(AipPacientes aipPaciente, RapServidores servidorLogado) {
		AipPacienteProntuario aipPacienteProntuarioJaCadastrado = this.getAipPacienteProntuarioDAO().obterPorChavePrimaria(aipPaciente.getProntuario());
		if (aipPacienteProntuarioJaCadastrado == null) {
			AipPacienteProntuario aipPacienteProntuario = new AipPacienteProntuario();
			aipPacienteProntuario.setProntuario(aipPaciente.getProntuario());
			aipPacienteProntuario.setCriadoEm(new Date());
			aipPacienteProntuario.setServidor(servidorLogado);
			getAipPacienteProntuarioDAO().persistir(aipPacienteProntuario);
		}
	}
	
	private void criarFonemasPaciente(AipPacientes aipPaciente) throws ApplicationBusinessException {
		// TODO: A fonetização poderá passar para a RN
		salvaFonemaPaciente(aipPaciente, aipPaciente.getNome(), AipFonemaPacientes.class);
		salvaFonemaPaciente(aipPaciente, aipPaciente.getNomeMae(), AipFonemasMaePaciente.class);
		salvaFonemaPaciente(aipPaciente, aipPaciente.getNomeSocial(), AipFonemaNomeSocialPacientes.class);
	}
	
	private void salvaFonemaPaciente(AipPacientes paciente, String valor, Class classeFonema) throws ApplicationBusinessException {
		if (!StringUtils.isBlank(valor)) {
			this.salvarFonemas(paciente, valor, classeFonema);
		}
	}
	
	/**
	 * Método responsável por incluir um paciente cirúrgico novo.
	 * 
	 * @param paciente
	 * @throws ValidacaoCadastroPacienteException
	 */
	public AipPacientes persistirPacienteCirurgico(AipPacientes aipPaciente) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogadoSemCache();

		validarNomes(aipPaciente);
		
		if (servidorLogado == null) {
			throw new ApplicationBusinessException(
					CadastroPacienteONExceptionCode.AIP_USUARIO_NAO_CADASTRADO);
		}
		aipPaciente.setRapServidoresCadastro(servidorLogado);

		aipPaciente.setNome(aipPaciente.getNome() == null ? null : aipPaciente.getNome().trim().toUpperCase());
		aipPaciente.setNomeMae(aipPaciente.getNomeMae() == null ? null : aipPaciente.getNomeMae().trim().toUpperCase());
		
		FccCentroCustos fccCentroCustos = servidorLogado.getCentroCustoLotacao();
		if (fccCentroCustos == null || fccCentroCustos.getCodigo() == null) {
			throw new ApplicationBusinessException(CadastroPacienteONExceptionCode.VALIDACAO_SERVIDOR_CENTRO_CUSTO_INCLUSAO_PACIENTE);

		} else {
			aipPaciente.setFccCentroCustosCadastro(servidorLogado.getCentroCustoLotacao());
		}
		aipPaciente.setDtIdentificacao(new Date());
		verificarSexoBiologico(aipPaciente);
		aipPaciente.setCriadoEm(new Date());

		this.getCadastroPacienteRN().inserirPaciente(aipPaciente);

		this.salvarFonemas(aipPaciente, aipPaciente.getNome(),AipFonemaPacientes.class);

		if (!aipPaciente.getNomeMae().trim().equals("")) {
			this.salvarFonemas(aipPaciente, aipPaciente.getNomeMae(),
					AipFonemasMaePaciente.class);
		}
		this.getAipPacientesDAO().flush();
		
		return aipPaciente;
	}

	/**
	 * Método responsável por verificar o atributo Sexo Biológico e atribuir o
	 * mesmo valor de sexo caso esteja null
	 * 
	 * @param aipPaciente
	 * @throws ValidacaoCadastroPacienteException
	 */
	private void verificarSexoBiologico(AipPacientes aipPaciente) {
		if (aipPaciente.getSexoBiologico() == null) {
			aipPaciente.setSexoBiologico(aipPaciente.getSexo());
		}
	}

	/**
	 * Método usado para manter consistência dos valores.
	 * 
	 * @param enderecos
	 */
	public void organizarValores(Set<AipEnderecosPacientes> enderecos) throws ApplicationBusinessException {
		for (AipEnderecosPacientes enderecoPaciente : enderecos) {
			organizarValores(enderecoPaciente);
		}
	}

	public void organizarValores(AipEnderecosPacientes enderecoPaciente) throws ApplicationBusinessException {
		if (enderecoPaciente.getAipBairrosCepLogradouro() == null && enderecoPaciente.getLogradouro() == null) {
			List<AipBairrosCepLogradouro> listAipBairrosCepLogradouro = this
					.getEnderecoON().pesquisarCepExato(
							enderecoPaciente.getCep());
			
			// A partir da melhoria descrita na issue #33502, a troca automática acontecerá apenas se existir apenas 1 CEP exato cadastrado no banco de dados.
			if (!listAipBairrosCepLogradouro.isEmpty() && listAipBairrosCepLogradouro.size() == 1) {
				enderecoPaciente
						.setAipBairrosCepLogradouro(listAipBairrosCepLogradouro
								.get(0));
				generateMessage(Severity.WARN, "TROCA_ENDERECO_NAO_CADASTRADO",
						enderecoPaciente.getCep());
			}
		}
		
		if (enderecoPaciente.getAipBairrosCepLogradouro() != null) {
			enderecoPaciente.setAipCidade(null);
			enderecoPaciente.setBairro(null);
			enderecoPaciente.setLogradouro(null);
			enderecoPaciente.setCidade(null);
			enderecoPaciente.setCep(null);
			enderecoPaciente.setAipLogradouro(null);
			enderecoPaciente.setAipUf(null);
		}
	}

	/**
	 * 
	 * TODO: Este método deve ficar centralizado Método que obtem a data de
	 * óbito anterior do paciente
	 * 
	 * @param codigoPaciente
	 * @return
	 */
	public Date obterDtObitoAnterior(Integer codigoPaciente) {
		return this.getAipPacientesDAO().obterDtObitoAnterior(codigoPaciente);
	}

	/**
	 * Método responsável pela atualização de um paciente.
	 * 
	 * @param paciente
	 * @throws ValidacaoCadastroPacienteException
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity", "PMD.CyclomaticComplexity"})
	public AipPacientes atualizarPaciente(AipPacientes aipPaciente,
			String nomeMicrocomputador, RapServidores servidorLogado,
			Boolean substituirProntuario)
			throws BaseException {
		LOG.info("<<<<<<<<<<<<<<<<<<AIP_PACIENTE_29>>>>>>>>>>>>>>>>");
		LOG.info(PACIENTE + aipPaciente.getProntuario() + HIFEM + aipPaciente.getNome());
		AipPacientesDAO aipPacientesDAO = this.getAipPacientesDAO();
		AipPacientes aipPacienteOriginal = aipPacientesDAO
				.obterOriginal(aipPaciente.getCodigo());
		
		if (aipPacienteOriginal == null){
			aipPacienteOriginal = new AipPacientes();
		}

		AipPacientesDadosCnsDAO aipPacientesDadosCnsDAO = this
				.getAipPacientesDadosCnsDAO();

		this.validarTelefones(aipPaciente);

		if (aipPaciente.getAipPacientesDadosCns() != null
				&& aipPaciente.getAipPacientesDadosCns().getPacCodigo() == null) {
			aipPacientesDadosCnsDAO.persistir(aipPaciente
					.getAipPacientesDadosCns());
		}
		
		//Caso o método tenha sido chamado da substituição de prontuário não
		//deve realizar estas validações
		if (!substituirProntuario){
			this.validarDadosPaciente(aipPaciente);			
			this.organizarValores(aipPaciente.getEnderecos());
		}
		
		this.getEnderecoON().atribuirSequencialEnderecos(aipPaciente);
		
		AipPacientesDadosCns dados = aipPaciente.getAipPacientesDadosCns();
		LOG.info("<<<<<<<<<<<<<<<<<<AIP_PACIENTE_30>>>>>>>>>>>>>>>>");
		LOG.info(PACIENTE + aipPaciente.getProntuario() + HIFEM + aipPaciente.getNome());
		if (!aipPacientesDAO.contains(aipPaciente)) {
			Boolean insereProntuario = aipPaciente.getInsereProntuario();
			aipPaciente = aipPacientesDAO.merge(aipPaciente);
			aipPacientesDAO.flush();
			aipPaciente.setInsereProntuario(insereProntuario);
			LOG.info("<<<<<<<<<<<<<<<<<<AIP_PACIENTE_31>>>>>>>>>>>>>>>>");
			LOG.info(PACIENTE + aipPaciente.getProntuario() + HIFEM + aipPaciente.getNome());
		}
	
		if (dados != null) {
			if (aipPaciente.getAipPacientesDadosCns() == null) {
				// Gato necessário para persistencia do cartão sus. o
				// problema
				// acontece na edição de um paciente que não possui cartão
				// sus.
				// ao informar os dados, sem persisti-los e forçar uma regra
				// de
				// negócio, o cartão sus se torna desatachado mais não é
				// possível fazer seu merge. verificar solução mais elegante
				// posteriormente.
				AipPacientesDadosCns dadosCns = new AipPacientesDadosCns();
				dadosCns.setCartaoNacionalSaudeMae(dados
						.getCartaoNacionalSaudeMae());
				dadosCns.setDocReferencia(dados.getDocReferencia());
				dadosCns.setMotivoCadastro(dados.getMotivoCadastro());
				dadosCns.setAipPaciente(aipPaciente);
				dadosCns.setRapServidor(dados.getRapServidor());
				dadosCns.setCriadoEm(new Date());
				dadosCns.setAlteradoEm(new Date());
				dadosCns.setAipOrgaosEmissor(dados.getAipOrgaosEmissor());
				dadosCns.setAipUf(dados.getAipUf());
				dadosCns.setDataEmissao(dados.getDataEmissao());
				dadosCns.setDataEmissaoDocto(dados.getDataEmissaoDocto());
				dadosCns.setDataEntradaBr(dados.getDataEntradaBr());
				dadosCns.setDataNaturalizacao(dados.getDataNaturalizacao());
				dadosCns.setFolhas(dados.getFolhas());
				dadosCns.setLivro(dados.getLivro());
				dadosCns.setNomeCartorio(dados.getNomeCartorio());
				dadosCns.setNumeroDn(dados.getNumeroDn());
				dadosCns.setPortariaNatural(dados.getPortariaNatural());
				dadosCns.setTermo(dados.getTermo());
				dadosCns.setTipoCertidao(dados.getTipoCertidao());

				aipPacientesDadosCnsDAO.persistir(dadosCns);
				aipPaciente.setAipPacientesDadosCns(dadosCns);
			} else {
				dados.setAipPaciente(aipPaciente);
				aipPaciente.setAipPacientesDadosCns(aipPacientesDadosCnsDAO.merge(dados));
				aipPacientesDadosCnsDAO.flush();
			}
		}

		LOG.info("<<<<<<<<<<<<<<<<<<AIP_PACIENTE_32>>>>>>>>>>>>>>>>");
		LOG.info(PACIENTE_ATUALIZADO + aipPaciente.getProntuario() + HIFEM + aipPaciente.getNome());

		validarProntuario(aipPaciente);

		LOG.info("<<<<<<<<<<<<<<<<<<AIP_PACIENTE_33>>>>>>>>>>>>>>>>");
		LOG.info(PACIENTE_ATUALIZADO + aipPaciente.getProntuario() + HIFEM + aipPaciente.getNome());

		verificarSexoBiologico(aipPaciente);
		validarAlteracaoSexoBiologico(aipPaciente);

		Date dataObitoAnterior = obterDtObitoAnterior(aipPaciente.getCodigo());
		getCadastroPacienteRN().validarDataObitoTrigger(dataObitoAnterior,aipPaciente);

		if (this.getCadastroPacienteRN().pacienteModificado(aipPaciente, aipPacienteOriginal) || 
				(DominioSimNao.N.equals(aipPacienteOriginal.getIndGeraProntuario())) && 
					DominioSimNao.S.equals(aipPaciente.getIndGeraProntuario())) {
			
			aipPaciente.setRapServidoresRecadastro(servidorLogado);
			FccCentroCustos fccCentroCustos = servidorLogado.getCentroCustoLotacao();
			if (fccCentroCustos == null || fccCentroCustos.getCodigo() == null) {
				// #54605
				AghParametros cctPadraoServidor = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_CCT_PADRAO_CAD_PACIENTE);
				if (cctPadraoServidor != null && cctPadraoServidor.getVlrNumerico() != null) {
					fccCentroCustos = centroCustoFacade.obterCentroCusto(cctPadraoServidor.getVlrNumerico().intValue());
					if(fccCentroCustos != null) {
						aipPaciente.setFccCentroCustosRecadastro(fccCentroCustos);
					}
					else {
						throw new ApplicationBusinessException(CadastroPacienteONExceptionCode.VALIDACAO_SERVIDOR_CENTRO_CUSTO_EDICAO_PACIENTE);
					}
				}

			} else {
				aipPaciente.setFccCentroCustosRecadastro(servidorLogado.getCentroCustoLotacao());
			}
			this.getEnderecoON().atualizarDataRecadastro(aipPaciente);
		}

		if (DominioSimNao.N.equals(aipPacienteOriginal
				.getIndGeraProntuario())
			&& DominioSimNao.S.equals(aipPaciente.getIndGeraProntuario())) {
			aipPaciente.setDtIdentificacao(new Date());
			aipPaciente.setRapServidoresCadastro(servidorLogado);

			FccCentroCustos fccCentroCustos = servidorLogado.getCentroCustoLotacao();
			if (fccCentroCustos == null || fccCentroCustos.getCodigo() == null) {
				// #54605
				AghParametros cctPadraoServidor = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_CCT_PADRAO_CAD_PACIENTE);
				if (cctPadraoServidor != null && cctPadraoServidor.getVlrNumerico() != null) {
					fccCentroCustos = centroCustoFacade.obterCentroCusto(cctPadraoServidor.getVlrNumerico().intValue());
					if(fccCentroCustos != null) {
						aipPaciente.setFccCentroCustosCadastro(fccCentroCustos);
					}
					else {
						throw new ApplicationBusinessException(CadastroPacienteONExceptionCode.VALIDACAO_SERVIDOR_CENTRO_CUSTO_EDICAO_PACIENTE);
					}
				}

			} else {
				aipPaciente.setFccCentroCustosCadastro(servidorLogado.getCentroCustoLotacao());
			}
		}

		this.getCadastroPacienteRN().atualizarSituacaoProntuarioEdicao(
				aipPaciente, aipPacienteOriginal);
		LOG.info("<<<<<<<<<<<<<<<<<<AIP_PACIENTE_34>>>>>>>>>>>>>>>>");
		LOG.info(PACIENTE_ATUALIZADO + aipPaciente.getProntuario() + HIFEM + aipPaciente.getNome());

		AipEnderecosPacientesJnDAO aipEnderecosPacientesJnDAO = this
				.getAipEnderecosPacientesJnDAO();
		for (AipEnderecosPacientesId enderecoID : this.getEnderecoON()
				.obterIdEndPaciente(aipPaciente.getCodigo())) {
			AipEnderecosPacientes enderecoPacienteOld = this.getEnderecoON()
					.obterEnderecoBanco(enderecoID);
			enderecoPacienteOld.setId(enderecoID);
			boolean flagAchou = false;
			for (AipEnderecosPacientes enderecoPaciente : aipPaciente
					.getEnderecos()) {
				if (enderecoPaciente.getId().equals(enderecoID)) {
					flagAchou = true;
					this.getEnderecoON().aiptEnpAru(enderecoPacienteOld,
							enderecoPaciente);
				}
			}
			if (!flagAchou) {

				AipEnderecosPacientesJn jn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.DEL,
								AipEnderecosPacientesJn.class, servidorLogado.getUsuario());

				final String nomeCidade = enderecoPacienteOld.getAipCidade() == null ? null
						: enderecoPacienteOld.getAipCidade().getNome();

				jn.setPacCodigo(enderecoPacienteOld.getId().getPacCodigo());
				jn.setSeqp(enderecoPacienteOld.getId().getSeqp());
				jn.setTipoEndereco(enderecoPacienteOld.getTipoEndereco());
				jn.setIndPadrao(enderecoPacienteOld.getIndPadrao());
				jn.setCidade(enderecoPacienteOld.getAipCidade());
				jn.setNomeCidade(nomeCidade);
				jn.setUf(enderecoPacienteOld.getAipUf());
				jn.setLogradouro(enderecoPacienteOld.getLogradouro());
				jn.setNroLogradouro(enderecoPacienteOld.getNroLogradouro());
				jn.setComplLogradouro(enderecoPacienteOld.getComplLogradouro());
				jn.setBairro(enderecoPacienteOld.getBairro());
				jn.setCep(enderecoPacienteOld.getCep());
				if (enderecoPacienteOld.getAipBairrosCepLogradouro() != null
						&& enderecoPacienteOld.getAipBairrosCepLogradouro()
								.getId() != null) {
					final AipBairrosCepLogradouroId aipBCLId = enderecoPacienteOld
							.getAipBairrosCepLogradouro().getId();
					jn.setBclBaiCodigo(aipBCLId.getBaiCodigo());
					jn.setBclCloCep(aipBCLId.getCloCep());
					jn.setBclCloLgrCodigo(aipBCLId.getCloLgrCodigo());
				}

				if (enderecoPacienteOld.getIndExclusaoForcada() != null) {
					jn.setIndExclusaoForcada(DominioSimNao
							.valueOf(enderecoPacienteOld
									.getIndExclusaoForcada()));
				}

				aipEnderecosPacientesJnDAO.persistir(jn);
			}

		}
		
		// #51933
		Integer prontuarioAnterior = null;
		if(aipPacienteOriginal.getProntuario()!=null) {
			prontuarioAnterior = aipPacienteOriginal.getProntuario();
		}

		LOG.info("<<<<<<<<<<<<<<<<<<AIP_PACIENTE_35>>>>>>>>>>>>>>>>");
		LOG.info(PACIENTE_ATUALIZADO + aipPaciente.getProntuario() + HIFEM + aipPaciente.getNome());

		this.getCadastroPacienteRN().atualizarPaciente(aipPaciente, nomeMicrocomputador,servidorLogado, new Date(), prontuarioAnterior);

		LOG.info("<<<<<<<<<<<<<<<<<<AIP_PACIENTE_36>>>>>>>>>>>>>>>>");
		LOG.info(PACIENTE_ATUALIZADO + aipPaciente.getProntuario() + HIFEM + aipPaciente.getNome());

		criarFonemasPaciente(aipPaciente);
		
		if (aipPaciente.getMaePaciente() != null) {
			aipPaciente.getMaePaciente().getProntuario();
		}

		LOG.info("<<<<<<<<<<<<<<<<<<AIP_PACIENTE_42>>>>>>>>>>>>>>>>");
		LOG.info(PACIENTE_ATUALIZADO + aipPaciente.getProntuario() + HIFEM + aipPaciente.getNome());

		return aipPaciente;
	}

	/**
	 * Método responsável por verificar se a alteração do sexo biológico pode
	 * ser realizada TODO Futuramente, ajustar para que a verificação da
	 * possibilidade de alteração seja ligada à permissão do usuário.
	 * 
	 * @param paciente
	 * @throws ValidacaoCadastroPacienteException
	 */
	private void validarAlteracaoSexoBiologico(AipPacientes aipPaciente)
			throws ApplicationBusinessException {
		final Date dataAtual = new Date();
		DominioSexo sexoBiologicoAnterior = obterSexoBiologicoAnterior(aipPaciente
				.getCodigo());
		if (sexoBiologicoAnterior != null
				&& aipPaciente.getSexoBiologico() != sexoBiologicoAnterior) {
			if (aipPaciente.getCriadoEm() != null
					&& !DateUtils.isSameDay(aipPaciente.getCriadoEm(),
							dataAtual)) {
				throw new ApplicationBusinessException(
						CadastroPacienteONExceptionCode.AIP_SEXO_BIOLOGICO_NAO_PODE_SER_ALTERADO);
			}
		}

	}

	/**
	 * Método que obtem o sexo biológico do paciente salvo no banco
	 * 
	 * @param codigoPaciente
	 * @return
	 */
	private DominioSexo obterSexoBiologicoAnterior(Integer codigoPaciente) {
		return this.getAipPacientesDAO().obterSexoBiologicoAnterior(
				codigoPaciente);
	}

	/**
	 * Método de fachada para o método salvarFonemas implementado na
	 * CadastroPacienteRN. Esse método salva os fonemas gerados para o nome
	 * paciente e/ou nome da mãe.
	 * 
	 * @param aipPaciente
	 *            paciente.
	 * @param nome
	 *            nome do paciente a ser fonetizado.
	 * @param tipo
	 *            tipo dos fonemas a serem persistidos (P - nome do paciente, M
	 */
	@SuppressWarnings("rawtypes")
	private void salvarFonemas(AipPacientes aipPaciente, String nome,
			Class clazz) throws ApplicationBusinessException {
		this.getCadastroPacienteRN().salvarFonemas(aipPaciente, nome, clazz);
	}

	/**
	 * Método responsável pelas validações dos dados de paciente. Método
	 * utilizado para inclusão e atualização de Paciente
	 * 
	 * @param paciente
	 * @throws ValidacaoCadastroPacienteException
	 */
	private void validarDadosPaciente(AipPacientes aipPaciente)
		throws BaseException {

		validarNomes(aipPaciente);

		// TODO---------------- Rever Estes Métodos -----------
		// verificarExigenciaProntuario(aipPaciente);
		// atualizaPlacarRegistroNeoNatal(aipPaciente);

		// TODO Ver como deve ficar estes atributos
		// aipPaciente.setDtIdentificacao(new Date());

		this.getCadastroPacienteRN().verificarCamposObrigatoriosPaciente(
				aipPaciente.getIndGeraProntuario(), aipPaciente.getCor(),
				aipPaciente.getSexo(), aipPaciente.getGrauInstrucao(),
				aipPaciente.getNomePai(), aipPaciente.getEstadoCivil(),
				aipPaciente.getAipNacionalidades(),
				aipPaciente.getDddFoneResidencial(), aipPaciente.getFoneResidencial());

		this.getCadastroPacienteRN()
				.validarNaturalidadeNacionalidade(
						aipPaciente.getAipNacionalidades(),
						aipPaciente.getAipCidades());

		this.validarNacionalidade(aipPaciente);

		//#20295
		if (aipPaciente.getNroCartaoSaude() != null) {
			this.getCadastroPacienteRN().validarNumeroCartaoSaude(
					aipPaciente.getNroCartaoSaude());
		}
		
		//#23959
		if (aipPaciente.getAipPacientesDadosCns() != null
				&& aipPaciente.getAipPacientesDadosCns()
						.getCartaoNacionalSaudeMae() != null) {
			this.getCadastroPacienteRN().validarNumeroCartaoSaude(
					BigInteger.valueOf(aipPaciente.getAipPacientesDadosCns()
							.getCartaoNacionalSaudeMae()));
		}		

		this.getEnderecoON().validarEnderecosPaciente(
				aipPaciente.getEnderecos(), false);

		verificarDataNascimento(aipPaciente.getDtNascimento());

	}

	/**
	 * Método responsável por realizar validações sobre a geração de prontuário
	 * além de solicitar a geração do mesmo.
	 * 
	 * @param aipPaciente
	 * @throws ApplicationBusinessException
	 */
	public void validarProntuario(AipPacientes aipPaciente)	throws ApplicationBusinessException {
		// Se alteraram o flag que manda gerar prontuário e o paciente já 
		// tem prontuário que não é virtual (menor ou igual a 90 milhões)
		// lança um erro
		if (aipPaciente.getCodigo() != null && aipPaciente.getIndGeraProntuario().equals(DominioSimNao.N) 
				&& aipPaciente.getProntuario() != null && aipPaciente.getProntuario() <= VALOR_MAXIMO_PRONTUARIO) {
			throw new ApplicationBusinessException(CadastroPacienteONExceptionCode.AIP_PRONTUARIO_JA_GERADO);
		}

		LOG.info("<<<<<<<<<<<<<<<<<<AIP_PACIENTE_10>>>>>>>>>>>>>>>>");
		LOG.info(PACIENTE + aipPaciente.getProntuario() + HIFEM + aipPaciente.getNome());

		// Se o paciente não tem prontuário ou tem um virtual, e o flag indica
		// para gerar prontuário, ou tem flag que indica geração de prontuário
		// virtual diferente de nulo e ligado, ou se indica pra não gerar 
		// prontuário e indica que usuário vai digitar 'na mão' e há um nro
		// de prontuário digitado, então vai chamar rotina geradora 
		if ((aipPaciente.getProntuario() == null || aipPaciente.getProntuario() > VALOR_MAXIMO_PRONTUARIO)
				&& (aipPaciente.getIndGeraProntuario().equals(DominioSimNao.S)
						|| (aipPaciente.getGeraProntuarioVirtual() != null
								&& (aipPaciente.getGeraProntuarioVirtual()!=null && aipPaciente.getGeraProntuarioVirtual())))
				|| (aipPaciente.getIndGeraProntuario().equals(DominioSimNao.S) 
						&& (aipPaciente.getInsereProntuario()!=null && aipPaciente.getInsereProntuario()) && aipPaciente.getProntuarioEditado() != null)) {
			// Guarda prontuário virtual para caso de exceção voltar ele
			// nos catch abaixo
			Integer prontuarioVirtual = null;
			if (aipPaciente.getProntuario()!=null && aipPaciente.getProntuario() > VALOR_MAXIMO_PRONTUARIO) {
				prontuarioVirtual = aipPaciente.getProntuario(); 
			}
			try {
				LOG.info("<<<<<<<<<<<<<<<<<<AIP_PACIENTE_11 Antes de obter prontuário>>>>>>>>>>>>>>>>");
				LOG.info(PACIENTE + aipPaciente.getProntuario() + HIFEM + aipPaciente.getNome());
				
				aipPaciente.setProntuario(obterNumeroProntuario(aipPaciente));
				
				LOG.info("<<<<<<<<<<<<<<<<<<AIP_PACIENTE_12 Após obter prontuário>>>>>>>>>>>>>>>>");
				LOG.info(PACIENTE + aipPaciente.getProntuario() + HIFEM + aipPaciente.getNome());
			} catch (ApplicationBusinessException e) {
				aipPaciente.setProntuario(prontuarioVirtual == null ? null : prontuarioVirtual);
				throw e;
			} catch (ValidaProntuarioException e) {
				aipPaciente.setProntuario(prontuarioVirtual == null ? null : prontuarioVirtual);
				throw new ApplicationBusinessException(	CadastroPacienteONExceptionCode.AIP_PRONTUARIO_VALIDADOR_CONFIG_ERROR,	e);
			}
		}
	}

	/**
	 * Método que verifica se deve ocorrer a geração de um número de prontuário.
	 * 
	 * @param aipPaciente
	 * @throws ApplicationBusinessException
	 * @throws ValidaProntuarioException
	 */
	private Integer obterNumeroProntuario(AipPacientes aipPaciente)	throws ApplicationBusinessException, ValidaProntuarioException {
		LOG.debug("gerando prontuário para o paciente " + aipPaciente.getNome());
		
		AipProntuarioLiberadosDAO aipProntuarioLiberadosDAO = this.getAipProntuarioLiberadosDAO();

		AipProntuarioLiberados prontuarioLiberadoSemLock = null;
		AipProntuarioLiberados prontuarioLiberadoComLock = null;
		boolean reutilizaProntuario = false;

		AghParametros parametroReutilizaProntuarios = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_REUTILIZA_PRONTUARIOS);
		if ("S".equals(parametroReutilizaProntuarios.getVlrTexto())) {

			LOG.info("<<<<<<<<<<<<<<<<<<AIP_PACIENTE_13>>>>>>>>>>>>>>>>");
			LOG.info(PACIENTE + aipPaciente.getProntuario() + HIFEM + aipPaciente.getNome());

			getPacienteFacade().limpaProntuariosLiberados();
			
			reutilizaProntuario = true;
			
			//Testar rotina exclusão

			prontuarioLiberadoSemLock = this.getAipProntuarioLiberadosDAO().obterProntuarioLiberadoComMaiorNumeroProntuario();

			if (prontuarioLiberadoSemLock != null) {
				// Aqui foi feito uso do lockMode.UPGRADE para fazer um lock
				// pessimista na tabela AIP_PRONTUARIO_LIBERADOS
				LOG.info("<<<<<<<<<<<<<<<<<<AIP_PACIENTE_14>>>>>>>>>>>>>>>>");
				LOG.info(PACIENTE + aipPaciente.getProntuario() + HIFEM + aipPaciente.getNome());
				
				prontuarioLiberadoComLock = aipProntuarioLiberadosDAO.obterProntuarioLiberado(
								prontuarioLiberadoSemLock.getProntuario(),LockOptions.UPGRADE);
				LOG.info("Obtendo valor a partir da prontuários liberados. Prontuário: "+ prontuarioLiberadoComLock.getProntuario());
			}
		}

		//ACHOU VALOR NA TABELA DE PRONTUÁRIO LIBERADO
		if (prontuarioLiberadoComLock != null && reutilizaProntuario) {
			LOG.info("<<<<<<<<<<<<<<<<<<AIP_PACIENTE_15>>>>>>>>>>>>>>>>");
			LOG.info(PACIENTE + aipPaciente.getProntuario() + HIFEM + aipPaciente.getNome());

			aipProntuarioLiberadosDAO.remover(prontuarioLiberadoComLock);
			return prontuarioLiberadoComLock.getProntuario(); // / 10;
		}
		
		//PROSEGUE SELECIONANDO O VALOR(SEQUENCE) NA TABELA CODE_CONTROLS
		LOG.info("<<<<<<<<<<<<<<<<<<AIP_PACIENTE_16>>>>>>>>>>>>>>>>");
		LOG.info(PACIENTE + aipPaciente.getProntuario() + HIFEM + aipPaciente.getNome());
		
		Integer novoProntuario = obterProntuarioCodeControls(aipPaciente);		
		aipProntuarioLiberadosDAO.flush();
		if (getAipPacientesDAO().pesquisarPacientePorProntuario(novoProntuario) == null){
			inserirProntuario(novoProntuario);			
		}
		LOG.info("<<<<<<<<<<<<<<<<<<AIP_PACIENTE_21>>>>>>>>>>>>>>>>");
		LOG.info(PACIENTE + aipPaciente.getProntuario() + HIFEM + aipPaciente.getNome());
		return novoProntuario;
	}
	
	private void inserirProntuario(Integer novoProntuario) {
		LOG.info("<<<<<<<<<<<<<<<<<<AIP_PACIENTE_22>>>>>>>>>>>>>>>>");
		LOG.info("INSERIR NOVO PRONTUÁRIO: " + novoProntuario);

		AipPacienteProntuario pacienteProntuario = new AipPacienteProntuario();
		pacienteProntuario.setCriadoEm(new Date());
		pacienteProntuario.setProntuario(novoProntuario);
		pacienteProntuario.setServidor(servidorLogadoFacade.obterServidorLogado());
		getAipPacienteProntuarioDAO().persistir(pacienteProntuario);
		getAipPacienteProntuarioDAO().flush();
		
		LOG.info("<<<<<<<<<<<<<<<<<<AIP_PACIENTE_23>>>>>>>>>>>>>>>>");
		LOG.info("INSERIR NOVO PRONTUÁRIO: " + novoProntuario);
	}

	private Integer obterProntuarioCodeControls(AipPacientes aipPaciente) throws ApplicationBusinessException, ValidaProntuarioException {		
		AipCodeControlsDAO aipCodeControlsDAO = this.getAipCodeControlsDAO();
		String strValorNovoProntuario = null;
		String sequenceProntuario = "";
		
		InterfaceValidaProntuario validadorProntuario = validaProntuarioFactory.getValidaProntuario(true);
		
		if (aipPaciente.getInsereProntuario() != null && aipPaciente.getInsereProntuario() && aipPaciente.getProntuarioEditado() != null) {
			LOG.info("<<<<<<<<<<<<<<<<<<AIP_PACIENTE_17>>>>>>>>>>>>>>>>");
			LOG.info(PACIENTE + aipPaciente.getProntuario() + HIFEM + aipPaciente.getNome());

			// #54094 - Solicitação e Análise da Deise Moura - Restringir a geração do digito verificador quando indicação do prontuário for manual
			AghParametros paramPermiteProntuarioManual = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_PERMITE_PRONTUARIO_MANUAL);
			AghParametros paramDigitoVerificador = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_GERA_DIG_PRONT);
			if(paramPermiteProntuarioManual!= null && paramPermiteProntuarioManual.getVlrTexto()!=null && "true".equals(paramPermiteProntuarioManual.getVlrTexto()) &&
				paramDigitoVerificador!=null && paramDigitoVerificador.getVlrTexto()!=null && "N".equals(paramDigitoVerificador.getVlrTexto())) {
				LOG.info("<<<<<<<<<<<<<<<<<<AIP_PACIENTE_18>>>>>>>>>>>>>>>>");
				LOG.info(PACIENTE + aipPaciente.getProntuario() + HIFEM + aipPaciente.getNome());
				LOG.info("PRONTUARIO EDITADO: " + aipPaciente.getProntuarioEditado().toString());
				return Integer.valueOf(aipPaciente.getProntuarioEditado().toString());
			}
			else {
				strValorNovoProntuario = aipPaciente.getProntuarioEditado().toString()
						+ validadorProntuario.executaModulo(aipPaciente.getProntuarioEditado());

				LOG.info("<<<<<<<<<<<<<<<<<<AIP_PACIENTE_19>>>>>>>>>>>>>>>>");
				LOG.info(PACIENTE + aipPaciente.getProntuario() + HIFEM + aipPaciente.getNome());
				LOG.info("PRONTUARIO GERADO: " + strValorNovoProntuario);

				return Integer.valueOf(strValorNovoProntuario);
			}	
		}
		
		if (aipPaciente.getGeraProntuarioVirtual() != null
				&& aipPaciente.getGeraProntuarioVirtual()
				&& !aipPaciente.getIndGeraProntuario().isSim()) {
			// Se prontuario for virtual.
			sequenceProntuario = "AIP_PAC_SQ3";
		} else {
			// Se prontuario for real.
			sequenceProntuario = "AIP_PAC_SQ2";
		}
		// Aqui foi feito uso do LockMode.UPGRADE para fazer um lock
		// pessimista na tabela AIP_CODE_CONTROLS
		AipCodeControls aipCodeControlsComLock = aipCodeControlsDAO.obterCodeControlLockForce(sequenceProntuario);
		Integer valorNovoProntuario = aipCodeControlsComLock.getNextValue();

		LOG.info("Leu da AIP_CODE_CONTROLS o prontuário: "+ valorNovoProntuario);
		aipCodeControlsComLock.setNextValue(valorNovoProntuario + 1);

		//Teste para validar se executou corretamente
		aipCodeControlsComLock = aipCodeControlsDAO.obterCodeControlLockForce(sequenceProntuario);
		valorNovoProntuario = aipCodeControlsComLock.getNextValue();
		LOG.info("Atualizou AIP_CODE_CONTROLS, novo valor: "	+  valorNovoProntuario);
		strValorNovoProntuario = valorNovoProntuario.toString() + validadorProntuario.executaModulo(valorNovoProntuario);

		LOG.info("<<<<<<<<<<<<<<<<<<AIP_PACIENTE_20>>>>>>>>>>>>>>>>");
		LOG.info(PACIENTE + aipPaciente.getProntuario() + HIFEM + aipPaciente.getNome());
		LOG.info("PRONTUARIO GERADO: " + strValorNovoProntuario);

		return Integer.valueOf(strValorNovoProntuario);
	}

	/**
	 * Método responsável por verificar se a data de nascimento foi informada e
	 * se é anterior à data atual
	 * 
	 * @param dthrNascimento
	 * @throws ValidacaoCadastroPacienteException
	 */
	private void verificarDataNascimento(Date dthrNascimento)
			throws ApplicationBusinessException {
		if (dthrNascimento == null) {
			throw new ApplicationBusinessException(
					CadastroPacienteONExceptionCode.AIP_DATA_NASCIMENTO_NAO_INFORMADA);
		}
		if (dthrNascimento != null && dthrNascimento.after(new Date())) {
			throw new ApplicationBusinessException(
					CadastroPacienteONExceptionCode.AIP_DATA_NASCIMENTO_INVALIDA);
		}

	}

	/**
	 * Método responsável por verificar se algum dado foi informado na tela de
	 * Dados Adicionais
	 * 
	 * @param dadosAdicionais
	 * @throws ValidacaoCadastroPacienteException
	 */
	private boolean verificarExistenciaDadosAdicionais(
			AipPacienteDadoClinicos dadosAdicionais)
			throws ApplicationBusinessException {
		boolean retorno = false;

		if (dadosAdicionais.getDthrNascimento() != null) {
			retorno = true;
		}
		if (dadosAdicionais.getIgSemanas() != null) {
			retorno = true;
		}
		if (dadosAdicionais.getApgar1() != null) {
			retorno = true;
		}
		if (dadosAdicionais.getApgar5() != null) {
			retorno = true;
		}
		if (dadosAdicionais.getTemperatura() != null) {
			retorno = true;
		}
		if (dadosAdicionais.getFatorRh() != null) {
			retorno = true;
		}
		if (dadosAdicionais.getTipagemSanguinea() != null) {
			retorno = true;
		}
		if (dadosAdicionais.getMesesGestacao() != null) {
			retorno = true;
		}

		return retorno;
	}

	/**
	 * Método responsável pela persistência dos dados adicionais de um paciente.
	 * 
	 * @param dadosAdicionais
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public void persistirDadosAdicionais(
			AipPacienteDadoClinicos dadosAdicionais, AipPacientes aipPaciente,
			AipPesoPacientes aipPesoPacientes) throws ApplicationBusinessException,
			BaseListException {
		AipPacienteDadoClinicosDAO aipPacienteDadoClinicosDAO = this
				.getAipPacienteDadoClinicosDAO();
		AipPacientesDAO aipPacientesDAO = this.getAipPacientesDAO();
		AipPesoPacientesDAO aipPesoPacientesDAO = this.getAipPesoPacientesDAO();
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		verificarDataNascimento(dadosAdicionais.getDthrNascimento());
		verificaDadosAdicionais(dadosAdicionais);
		if (verificarExistenciaDadosAdicionais(dadosAdicionais)) {
			if (dadosAdicionais.getPacCodigo() == null) {
				dadosAdicionais.setCriadoEm(new Date());
			}
			dadosAdicionais.setAlteradoEm(new Date());
			aipPaciente.setAipPacienteDadoClinicos(dadosAdicionais);
			dadosAdicionais.setAipPaciente(aipPaciente);
			buscaDadosRn(dadosAdicionais);

			if (dadosAdicionais.getPacCodigo() != null) {
				if (!aipPacienteDadoClinicosDAO.contains(dadosAdicionais)) {

					AipPacientes paciente = aipPacientesDAO.merge(aipPaciente);

					if (paciente.getAipPacienteDadoClinicos() == null) {
						// Gato necessário para persistencia do cartão dos dados
						// adicionais. o problema
						// acontece na edição de um paciente que não possui
						// dados adicionais.
						// ao informar os dados, sem persisti-los e forçar uma
						// regra de
						// negócio, o dados adicionais se torna desatachado mais
						// não é
						// possível fazer seu merge. verificar soluição mais
						// elegante
						// posteriormente.
						AipPacienteDadoClinicos dadosClinicos = new AipPacienteDadoClinicos();
						dadosClinicos.setAipPaciente(paciente);
						dadosClinicos.setAlteradoEm(new Date());
						dadosClinicos.setApgar1(dadosAdicionais.getApgar1());
						dadosClinicos.setApgar5(dadosAdicionais.getApgar5());
						dadosClinicos.setCriadoEm(new Date());
						dadosClinicos.setDthrNascimento(dadosAdicionais
								.getDthrNascimento());
						dadosClinicos.setFatorRh(dadosAdicionais.getFatorRh());
						dadosClinicos.setIgSemanas(dadosAdicionais
								.getIgSemanas());
						dadosClinicos.setIndExterno(dadosAdicionais
								.getIndExterno());
						dadosClinicos.setMesesGestacao(dadosAdicionais
								.getMesesGestacao());
						dadosClinicos.setTemperatura(dadosAdicionais
								.getTemperatura());
						dadosClinicos.setTipagemSanguinea(dadosAdicionais
								.getTipagemSanguinea());

						aipPacienteDadoClinicosDAO.persistir(dadosClinicos);

					} else {
						dadosAdicionais = aipPacienteDadoClinicosDAO
								.merge(dadosAdicionais);
					}
				}
			} else {
				aipPacienteDadoClinicosDAO.persistir(dadosAdicionais);
			}
		}

		verificarPeso(aipPesoPacientes);
		if (aipPesoPacientes.getId().getCriadoEm() == null) {
			if (aipPesoPacientes.getPeso() != null) {
				aipPesoPacientes.setAipPaciente(aipPaciente);
				persistirPesoPaciente(aipPesoPacientes, servidorLogado);
			}
		} else {
			if (!aipPesoPacientesDAO.contains(aipPesoPacientes)) {
				aipPesoPacientes = aipPesoPacientesDAO.merge(aipPesoPacientes);
			}
		}

		try {
			aipPacientesDAO.flush();
		} catch (ConstraintViolationException e) {
			BaseListException erros = new BaseListException();
			for (ConstraintViolation<?> message : e.getConstraintViolations()) {
				erros.add(new ApplicationBusinessException(
						CadastroPacienteONExceptionCode.MENSAGEM_ERRO_HIBERNATE_VALIDATION,
						message.getMessage()));
			}
			throw erros;
		} catch (PersistenceException pe) {
			Throwable cause = pe.getCause();
			if (cause instanceof ConstraintViolationException) {
				ConstraintViolationException cve = (ConstraintViolationException) cause;
				if (cve.getMessage().contains("AIP_PDC_PK")
						|| cve.getMessage().contains("AIP_PEP_PK")) {
					throw new OptimisticLockException();
				}
			}
			LOG.error("Erro ao incluir os Dados Adicionais.", pe);
			throw pe;
		}
	}

	@SuppressWarnings("PMD.NPathComplexity")
	protected void verificaDadosAdicionais(
			AipPacienteDadoClinicos dadosAdicionais)
			throws ApplicationBusinessException {
		if (dadosAdicionais.getMesesGestacao() != null
				&& (CoreUtil.menor(dadosAdicionais.getMesesGestacao(), 0) || CoreUtil
						.maior(dadosAdicionais.getMesesGestacao(), 9))) {
			throw new ApplicationBusinessException(
					CadastroPacienteONExceptionCode.MSG_MESES_GESTACAO_RANGE);
		}

		if (dadosAdicionais.getApgar1() != null
				&& (CoreUtil.menor(dadosAdicionais.getApgar1(), 0) || CoreUtil
						.maior(dadosAdicionais.getApgar1(), 10))) {
			throw new ApplicationBusinessException(
					CadastroPacienteONExceptionCode.MSG_APGAR1_RANGE);
		}

		if (dadosAdicionais.getApgar5() != null
				&& (CoreUtil.menor(dadosAdicionais.getApgar5(), 0) || CoreUtil
						.maior(dadosAdicionais.getApgar5(), 10))) {
			throw new ApplicationBusinessException(
					CadastroPacienteONExceptionCode.MSG_APGAR5_RANGE);
		}

		if (dadosAdicionais.getTemperatura() != null
				&& (CoreUtil.menor(dadosAdicionais.getTemperatura(), 33) || CoreUtil
						.maior(dadosAdicionais.getTemperatura(), 43))) {
			throw new ApplicationBusinessException(
					CadastroPacienteONExceptionCode.MSG_TEMPERATURA_RANGE);
		}

		if (dadosAdicionais.getIgSemanas() != null
				&& (CoreUtil.menor(dadosAdicionais.getIgSemanas(), 1) || CoreUtil
						.maior(dadosAdicionais.getIgSemanas(), 52))) {
			throw new ApplicationBusinessException(
					CadastroPacienteONExceptionCode.MSG_IG_SEMANAS_RANGE);
		}

	}

	public void persistirAlturaPaciente(AipAlturaPacientes alturaPaciente) throws ApplicationBusinessException {
		this.getCadastroPacienteRN().persistir(alturaPaciente);
	}
	
	/**
	 * Método responsável pela persistência do peso do paciente.
	 * 
	 * @param aipPesoPacientes
	 * @param servidorLogado 
	 */
	public void persistirPesoPaciente(AipPesoPacientes aipPesoPacientes, RapServidores servidorLogado)
			throws ApplicationBusinessException {
		this.getPesoPacienteRN().persistirAipPesoPaciente(aipPesoPacientes, servidorLogado);
		this.getAipPesoPacientesDAO().persistir(aipPesoPacientes);
	}

	/**
	 * Método responsável por verificar se o peso informado está correto.
	 * 
	 * @param dadosAdicionais
	 * @throws ValidacaoCadastroPacienteException
	 */
	private void verificarPeso(AipPesoPacientes aipPesoPaciente)
			throws ApplicationBusinessException {

		if (aipPesoPaciente.getId().getCriadoEm() != null
				&& aipPesoPaciente.getPeso() == null) {
			throw new ApplicationBusinessException(
					CadastroPacienteONExceptionCode.AIP_PESO_EXISTENTE_NULO);
		}

		if (aipPesoPaciente.getPeso() != null
				&& aipPesoPaciente.getPeso().floatValue() <= 0) {
			throw new ApplicationBusinessException(
					CadastroPacienteONExceptionCode.AIP_PESO_INVALIDO);
		}

		if (aipPesoPaciente.getPeso() != null
				&& aipPesoPaciente.getIndMomento() != null
				&& aipPesoPaciente.getIndMomento().equals(DominioMomento.N)) {
			if (aipPesoPaciente.getPeso().floatValue() < 0.2
					|| aipPesoPaciente.getPeso().floatValue() > 7) {
				throw new ApplicationBusinessException(
						CadastroPacienteONExceptionCode.AIP_PESO_NASCIMENTO_INVALIDO);
			}
		}

	}

	/**
	 * Método responsável pela persistência do cartão SUS do paciente.
	 * 
	 * @param cartaoSus
	 */
	public void persistirCartaoSus(AipPacientes aipPaciente)
			throws ApplicationBusinessException {

		if (aipPaciente.getAipPacientesDadosCns().getAipPaciente() == null) {
			incluirCartaoSus(aipPaciente);
		} else {
			alterarCartaoSus(aipPaciente);
		}
		if (!verificarExistenciaDadosCartaoSUS(aipPaciente
				.getAipPacientesDadosCns())) {
			if (aipPaciente.getAipPacientesDadosCns().getPacCodigo() != null) {
				removerAipPacientesDadoCns(aipPaciente
						.getAipPacientesDadosCns());
			}
			aipPaciente.setAipPacientesDadosCns(null);
		} else {
			aipPaciente.getAipPacientesDadosCns().setAipPaciente(aipPaciente);
		}

	}

	/**
	 * Método que remove os dados do cartão SUS de um paciente quando todos os
	 * seus dados são alterados para nulo
	 * 
	 * @param cartaoSus
	 */
	private void removerAipPacientesDadoCns(
			AipPacientesDadosCns aipPacientesDadoCns)
			throws ApplicationBusinessException {
		try {
			AipPacientesDadosCnsDAO aipPacientesDadosCnsDAO = this
					.getAipPacientesDadosCnsDAO();
			if (!aipPacientesDadosCnsDAO.contains(aipPacientesDadoCns)) {
				aipPacientesDadoCns = aipPacientesDadosCnsDAO
						.merge(aipPacientesDadoCns);
			}
			aipPacientesDadosCnsDAO.remover(aipPacientesDadoCns);
			aipPacientesDadosCnsDAO.flush();
		} catch (Exception e) {
			LOG.error("Erro ao remover os dados CNS do paciente.",
					e);
			throw new ApplicationBusinessException(
					CadastroPacienteONExceptionCode.ERRO_EXCLUIR_DADOS_CNS);
		}
	}

	/**
	 * Método responsável por verificar se algum dado foi informado na tela de
	 * Cartão SUS
	 * 
	 * @param dadosAdicionais
	 * @throws ValidacaoCadastroPacienteException
	 */
	private boolean verificarExistenciaDadosCartaoSUS(
			AipPacientesDadosCns dadosCartaoSUS) throws ApplicationBusinessException {
		boolean retorno = false;

		if (dadosCartaoSUS.getMotivoCadastro() != null
				|| dadosCartaoSUS.getDocReferencia() != null
				|| dadosCartaoSUS.getCartaoNacionalSaudeMae() != null
				|| dadosCartaoSUS.getDataEntradaBr() != null
				|| dadosCartaoSUS.getDataNaturalizacao() != null
				|| dadosCartaoSUS.getPortariaNatural() != null
				|| dadosCartaoSUS.getTipoCertidao() != null
				|| StringUtils.isNotBlank(dadosCartaoSUS.getNomeCartorio())
				|| StringUtils.isNotBlank(dadosCartaoSUS.getLivro())
				|| dadosCartaoSUS.getFolhas() != null
				|| dadosCartaoSUS.getTermo() != null
				|| dadosCartaoSUS.getDataEmissao() != null
				|| dadosCartaoSUS.getNumeroDn() != null
				|| dadosCartaoSUS.getAipUf() != null
				|| dadosCartaoSUS.getDataEmissaoDocto() != null) {
			retorno = true;
		}

		return retorno;
	}

	/**
	 * Método responsável por incluir um cartão do SUS novo.
	 * 
	 * @param cartaoSus
	 * @throws ApplicationBusinessException
	 */
	private void incluirCartaoSus(AipPacientes aipPaciente)
			throws ApplicationBusinessException {
		this.getCadastroPacienteRN()
				.validarDadosCartaoSUSInsert(
						aipPaciente.getAipPacientesDadosCns());
	}

	/**
	 * Método responsável por alterar um cartão do SUS.
	 * 
	 * @param cartaoSus
	 * @throws ApplicationBusinessException
	 */
	private void alterarCartaoSus(AipPacientes aipPaciente)
			throws ApplicationBusinessException {

		this.getCadastroPacienteRN()
				.validarDadosCartaoSUSUpdate(
						aipPaciente.getAipPacientesDadosCns());

	}

	/**
	 * Método responsável por validar os dados do Cartão SUS
	 * 
	 * @param cartaoSus
	 * @throws ApplicationBusinessException
	 */
	public void validarCartaoSUS(AipPacientes aipPaciente, AipPacientesDadosCns aipPacientesDadosCns)
			throws ApplicationBusinessException {
		if (aipPacientesDadosCns != null && (aipPaciente.getRegNascimento()==null || 
				aipPaciente.getRegNascimento()!=null && aipPaciente.getRegNascimento().length() < 30)){
			validaTipoCertidaoCartaoSus(aipPacientesDadosCns);
		}
		if (aipPaciente.getRg() != null && !aipPaciente.getRg().isEmpty()) {
			if (aipPacientesDadosCns == null 
					|| (aipPacientesDadosCns.getAipOrgaosEmissor() == null
					|| aipPacientesDadosCns.getAipUf() == null 
					|| aipPacientesDadosCns.getDataEmissaoDocto() == null)) {
				
				throw new ApplicationBusinessException(CadastroPacienteONExceptionCode.DADOS_EMISSAO_DOCUMENTOS_INCOMPLETOS);
			}
		} else {
			if (aipPaciente.getRegNascimento() != null && aipPaciente.getRegNascimento().length() < 30) {
				if (aipPacientesDadosCns == null	
						|| (StringUtils.isBlank(aipPacientesDadosCns.getNomeCartorio())
						|| StringUtils.isBlank(aipPacientesDadosCns.getLivro())
						|| aipPacientesDadosCns.getFolhas() == null
						|| aipPacientesDadosCns.getTermo() == null 
						|| aipPacientesDadosCns.getDataEmissao() == null)) {

					throw new ApplicationBusinessException(CadastroPacienteONExceptionCode.DADOS_CERTIDAO_INCOMPLETOS);
				}
			}
		}

		if (aipPacientesDadosCns != null
				&& aipPacientesDadosCns.getTipoCertidao() != null && (aipPaciente.getRegNascimento()==null || 
					aipPaciente.getRegNascimento()!=null && aipPaciente.getRegNascimento().length() < 30)) {
			if (StringUtils.isBlank(aipPacientesDadosCns.getNomeCartorio())
					|| StringUtils.isBlank(aipPacientesDadosCns.getLivro())
					|| aipPacientesDadosCns.getFolhas() == null
					|| aipPacientesDadosCns.getTermo() == null
					|| aipPacientesDadosCns.getDataEmissao() == null) {

				throw new ApplicationBusinessException(CadastroPacienteONExceptionCode.DADOS_CERTIDAO_INCOMPLETOS);
			}
		}
	}

	/**
	 * Método vinculado ao parametro obrigaPreenchimentoRN()
	 * @param cartaoSus
	 * @throws AGHUNegocioException
	 */
	public void validarCartaoSUSRN(AipPacientes aipPaciente, AipPacientesDadosCns aipPacientesDadosCns)
			throws ApplicationBusinessException {
		
		if (aipPaciente.getRg() != null && !aipPaciente.getRg().isEmpty()) {
			if (aipPacientesDadosCns == null 
					|| (aipPacientesDadosCns.getAipOrgaosEmissor() == null
					|| aipPacientesDadosCns.getAipUf() == null 
					|| aipPacientesDadosCns.getDataEmissaoDocto() == null)) {
				
				throw new ApplicationBusinessException(CadastroPacienteONExceptionCode.DADOS_EMISSAO_DOCUMENTOS_INCOMPLETOS);
			}
		} 
		else {
			if (aipPaciente.getRegNascimento() != null && aipPaciente.getRegNascimento().toString().length() < 30) {
				if (aipPacientesDadosCns == null	
						|| (StringUtils.isBlank(aipPacientesDadosCns.getNomeCartorio())
						|| StringUtils.isBlank(aipPacientesDadosCns.getLivro())
						|| aipPacientesDadosCns.getFolhas() == null
						|| aipPacientesDadosCns.getTermo() == null 
						|| aipPacientesDadosCns.getDataEmissao() == null)) {

					throw new ApplicationBusinessException(CadastroPacienteONExceptionCode.DADOS_CERTIDAO_INCOMPLETOS);
				}
			}
		}
	}
	
	/**
	 * Método responsável por validar se existe mais algum cartão sus igual ao número informado
	 * @param aipPaciente
	 * @param aipPacientesDadosCns
	 * @throws AGHUNegocioExceptionSemRollback
	 * @throws AGHUNegocioException 
	 */
	public void validarDuplicidadeCartaoSUS(AipPacientes aipPaciente)
			throws ApplicationBusinessException {
		AghParametros param = this.getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_AGHU_VALIDA_DUPLICIDADE_CNS);
		
		if (aipPaciente.getNroCartaoSaude() != null && param.getVlrNumerico().intValue() > 0) {
			List<AipPacientes> pacientesComCNS = getAipPacientesDAO().pesquisarPacientePorCartaoSaude(aipPaciente.getNroCartaoSaude());
			pacientesComCNS.remove(aipPaciente);
			if (pacientesComCNS.size() > 0){
				StringBuilder cnsDuplicado = new StringBuilder();
				for (AipPacientes aipPacienteCPF : pacientesComCNS) {
					cnsDuplicado.append(' ').append(aipPacienteCPF.getCodigo()).append(',');
				}
				
				throw new ApplicationBusinessException(CadastroPacienteONExceptionCode.PACIENTE_COM_CNS_DUPLICADO,
						cnsDuplicado.toString().substring(0, cnsDuplicado.toString().length()-1));
			}
		}
	}

	/**
	 * Método responsável por validar se existe mais algum paciente cadastrado com o mesmo cpf.
	 * 
	 * @param aipPaciente
	 * @param aipPacientesDadosCns
	 * @throws AGHUNegocioExceptionSemRollback
	 * @throws AGHUNegocioException 
	 */
	public void validarDuplicidadeCPF(AipPacientes aipPaciente)
			throws ApplicationBusinessException {
		AghParametros param = this.getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_AGHU_VALIDA_DUPLICIDADE_CPF);
		
		if (aipPaciente.getCpf() != null && param.getVlrNumerico().intValue() > 0) {
			List<AipPacientes> pacientesComCPF = getAipPacientesDAO().pesquisarPacientePorCPF(aipPaciente.getCpf());
			pacientesComCPF.remove(aipPaciente);
			if (pacientesComCPF.size() > 0){
				StringBuilder cpfDuplicado = new StringBuilder();
				for (AipPacientes aipPacienteCPF : pacientesComCPF) {
					cpfDuplicado.append(' ').append(aipPacienteCPF.getCodigo()).append(',');
				}
				
				throw new ApplicationBusinessException(CadastroPacienteONExceptionCode.PACIENTE_COM_CPF_DUPLICADO,
						cpfDuplicado.toString().substring(0, cpfDuplicado.toString().length()-1));
			}
		}
	}
	
	/**
	 * Método responsável pela validação do nome digitado para um Paciente. Os
	 * nomes do paciente e da mãe do paciente são obrigatórios.
	 * 
	 * @param paciente
	 * @throws ValidacaoCadastroPacienteException
	 */
	private void validarNomes(AipPacientes aipPaciente)
			throws ApplicationBusinessException {

		if (StringUtils.isBlank(aipPaciente.getNome())) {
			throw new ApplicationBusinessException(
					CadastroPacienteONExceptionCode.AIP_NOME_PACIENTE_NULO);
		}
		verificarNome(aipPaciente.getNome(), true);

		if (StringUtils.isBlank(aipPaciente.getNomeMae())) {
			throw new ApplicationBusinessException(
					CadastroPacienteONExceptionCode.AIP_NOME_MAE_NULO);
		}
		verificarNome(aipPaciente.getNomeMae(), false);

		if (!StringUtils.isBlank(aipPaciente.getNomePai())) {
			verificarNome(aipPaciente.getNomePai(), false);
		}
	}

	/**
	 * Método que verifica se um nome qualquer passado como parâmetro não possui
	 * caracteres especiais ou números, se é composto por mais de um nome e se
	 * não inicia com brancos Método responsável por implementar a function
	 * ORADB AGHC_VERIFICA_NOME.
	 * 
	 * @param nome
	 * @return Retorna true para nome válido ou false para nome inválido.
	 */
	private boolean verificarNome(final String nome,
			boolean exigeMultiplasPalavras) throws ApplicationBusinessException {
		String nomeAjustado = FonetizadorUtil.ajustarNome(nome);
		if (exigeMultiplasPalavras && nomeAjustado.indexOf(' ') == -1) {
			throw new ApplicationBusinessException(
					CadastroPacienteONExceptionCode.AIP_NOME_DE_UMA_PALAVRA);
		}
		return true;
	}

	/**
	 * Método usado para persistir as alterações na lista de convênios de um
	 * paciente.
	 * 
	 * @param planosPaciente
	 * @throws ApplicationBusinessException
	 */
	public void persistirPlanoPaciente(
			List<AipConveniosSaudePaciente> planosPaciente)
			throws ApplicationBusinessException {
		AipConveniosSaudePacienteDAO aipConveniosSaudePacienteDAO = this
				.getAipConveniosSaudePacienteDAO();
		AipPacientesDAO aipPacientesDAO = this.getAipPacientesDAO();

		this.validarPlanosPaciente(planosPaciente);

		Short seqPlanoPaciente = null;

		for (AipConveniosSaudePaciente planoPaciente : planosPaciente) {

			this.validarPlanoPaciente(planoPaciente);

			// ATRIBUI SEQUENCIAL E PERSISTE, SE FOR UM NOVO PLANO.
			if (planoPaciente.getId().getSeq() == null) {

				if (seqPlanoPaciente == null) {
					seqPlanoPaciente = this
							.obterValorSeqPlanoPaciente(planoPaciente
									.getPaciente());
				} else {
					seqPlanoPaciente++;
				}

				planoPaciente.getId().setSeq(seqPlanoPaciente);

				aipConveniosSaudePacienteDAO.persistir(planoPaciente);
			} else {
				aipConveniosSaudePacienteDAO.merge(planoPaciente);
				aipConveniosSaudePacienteDAO.flush();
			}

			AipPacientes paciente = aipPacientesDAO.merge(planoPaciente
					.getPaciente());
			paciente.setNome(paciente.getNome());
		}

		aipConveniosSaudePacienteDAO.flush();
	}

	/**
	 * Método para persistir o plano do paciente que está sendo internado.
	 * 
	 * @param planosPaciente
	 * @throws ApplicationBusinessException
	 */
	public void incluirPlanoPacienteInternacao(
			AipConveniosSaudePaciente planoPaciente)
			throws ApplicationBusinessException {
		this.getAipConveniosSaudePacienteDAO().persistir(planoPaciente);
		this.getAipConveniosSaudePacienteDAO().flush();
	}

	/**
	 * Método usado para realizar as velidações necessárias na lista de planos
	 * de uma paciente.
	 */
	private void validarPlanosPaciente(
			List<AipConveniosSaudePaciente> planosPaciente)
			throws ApplicationBusinessException {

		this.validarPlanosAtivos(planosPaciente);

		for (AipConveniosSaudePaciente planoPaciente : planosPaciente) {
			this.validarSituacaoPlanoPaciente(planoPaciente);
		}

	}

	/**
	 * Método que assegura a consistência da situação dos planos. Um plano não
	 * pode ser reativado e, no caso de uma desativação, a data de encerramento
	 * deve ser setada.
	 * 
	 * @param planoPaciente
	 * @throws ApplicationBusinessException
	 */
	private void validarSituacaoPlanoPaciente(
			AipConveniosSaudePaciente planoPaciente)
			throws ApplicationBusinessException {

		DominioSituacao situacaoAnterior = this
				.obterSituacaoAnteriorPlanoPaciente(planoPaciente);

		// verifica se há uma tentativa de reativação de um plano.
		if (situacaoAnterior == DominioSituacao.I
				&& planoPaciente.getSituacao() == DominioSituacao.A) {
			throw new ApplicationBusinessException(
					CadastroPacienteONExceptionCode.AIP_REATIVACAO_PLANO);
		}

		// seta a data de encerramento.
		if (situacaoAnterior == DominioSituacao.A
				&& planoPaciente.getSituacao() == DominioSituacao.I) {
			planoPaciente.setEncerradoEm(new Date());
		}

	}

	/**
	 * Método usado para assegurar que não haverá dois planos ativos para o
	 * mesmo convênio e paciente.
	 * 
	 * @param planosPaciente
	 * @throws ApplicationBusinessException
	 */
	private void validarPlanosAtivos(
			List<AipConveniosSaudePaciente> planosPaciente)
			throws ApplicationBusinessException {
		Set<FatConvenioSaudePlano> codigosConveniosPlanosAtivos = new HashSet<FatConvenioSaudePlano>();

		for (AipConveniosSaudePaciente planoPaciente : planosPaciente) {
			if (planoPaciente.isAtivo()) {
				if (!codigosConveniosPlanosAtivos.contains(planoPaciente
						.getConvenio())) {
					codigosConveniosPlanosAtivos.add(planoPaciente
							.getConvenio());
				} else {
					throw new ApplicationBusinessException(
							CadastroPacienteONExceptionCode.AIP_PLANO_ATIVO_DUPLICADO,
							planoPaciente.getConvenio().getConvenioSaude()
									.getDescricao(), planoPaciente.getConvenio().getDescricao());
				}
			}
		}
	}

	private void validarPlanoPaciente(AipConveniosSaudePaciente planoPaciente)
			throws ApplicationBusinessException {
		FatConvenioSaude convenio = planoPaciente.getConvenio()
				.getConvenioSaude();

		if (convenio.getExigeNumeroMatricula()
				&& planoPaciente.getMatricula() == null) {
			throw new ApplicationBusinessException(
					CadastroPacienteONExceptionCode.AIP_MATRICULA_REQUERIDA,
					convenio.getDescricao());
		}

	}

	/**
	 * Método responsável pela geração do Sequencial para o Id de um cadastro de
	 * paciente.
	 * 
	 * @param paciente
	 */
	private Short obterValorSeqPlanoPaciente(AipPacientes paciente) {
		return this.getAipConveniosSaudePacienteDAO()
				.obterValorSeqPlanoPaciente(paciente);
	}

	/**
	 * Método usado para remover um plano paciente.
	 * 
	 * @param planoPaciente
	 */
	public void removerPlanoPaciente(Integer pacCodigo, Short seq)
			throws ApplicationBusinessException {

		if (seq != null) {// evita tentativa de remoção de planos que
			// ainda não foram persistidos.
			AipPacientesDAO aipPacientesDAO = this.getAipPacientesDAO();
			AipConveniosSaudePacienteDAO aipConveniosSaudePacienteDAO = this
					.getAipConveniosSaudePacienteDAO();

			this.clear();

			AipConveniosSaudePacienteId id = new AipConveniosSaudePacienteId(
					pacCodigo, seq);
			AipConveniosSaudePaciente planoPaciente = aipConveniosSaudePacienteDAO
					.obterPorChavePrimaria(id);

			AipPacientes paciente = planoPaciente.getPaciente();
			paciente.setNome(paciente.getNome());

			try {
				aipPacientesDAO.merge(paciente);
				aipConveniosSaudePacienteDAO.remover(planoPaciente);
				aipPacientesDAO.flush();
			} catch (OptimisticLockException e) {
				LOG.error(e.getMessage(), e);
				throw e;
			} catch (Exception e) {
				LOG.error("Erro ao remover o plano do paciente.",
						e);

				throw new ApplicationBusinessException(
						CadastroPacienteONExceptionCode.ERRO_EXCLUIR_PLANO_PACIENTE);
			}
		}
	}

	/**
	 * Obtem a situação anterior do plano do paciente. Por anterior entenda-se a
	 * situação atual do banco, anterior a alteração do usuário na interface.
	 * CUIDADO AO REUTILIZAR ESTE MÉTODO POIS ELE IGNORA O CASH DE PRIMEIRO
	 * NÍ?VEL DO HIBERNATE.
	 * 
	 * @param planoPaciente
	 * @return
	 */
	private DominioSituacao obterSituacaoAnteriorPlanoPaciente(
			AipConveniosSaudePaciente planoPaciente) {
		return this.getAipConveniosSaudePacienteDAO()
				.obterSituacaoAnteriorPlanoPaciente(planoPaciente);
	}

	/**
	 * Método para listar os Órgãos Emissores em um combo de acordo com o
	 * parâmetro informado. O parâmetro pode ser tanto parte da descrição do
	 * órgão quanto o código do mesmo.
	 * 
	 * @param paramPesquisa
	 * @return li
	 */
	public List<AipOrgaosEmissores> pesquisarOrgaoEmissorPorCodigoDescricao(
			Object paramPesquisa) {
		return this.getAipOrgaosEmissoresDAO()
				.pesquisarOrgaoEmissorPorCodigoDescricao(paramPesquisa);
	}

	/**
	 * Método que conta o total de orgão emissores de acordo com o código ou a
	 * descrição.
	 * 
	 * @param paramPesquisa
	 * @return li
	 */
	public Long obterCountOrgaoEmissorPorCodigoDescricao(Object paramPesquisa) {
		return this.getAipOrgaosEmissoresDAO()
				.obterCountOrgaoEmissorPorCodigoDescricao(paramPesquisa);
	}

	/**
	 * Verifica, no caso de o campo Tipo Certidão estar preenchido, se os demais
	 * campos da mesma seção (com exceção de Número da DN) também estão
	 * preenchidos
	 * 
	 * @param aipPacienteDadosCns
	 * @throws ApplicationBusinessException
	 */
	public void validaTipoCertidaoCartaoSus(
			AipPacientesDadosCns aipPacienteDadosCns)
			throws ApplicationBusinessException {
		if (aipPacienteDadosCns.getTipoCertidao() != null) {
			if (StringUtils.isBlank(aipPacienteDadosCns.getNomeCartorio())
					|| StringUtils.isBlank(aipPacienteDadosCns.getLivro())
					|| aipPacienteDadosCns.getFolhas() == null
					|| aipPacienteDadosCns.getTermo() == null
					|| aipPacienteDadosCns.getDataEmissao() == null) {
				throw new ApplicationBusinessException(
						CadastroPacienteONExceptionCode.AIP_TIPO_CERTIDAO_PREENCHIDO);

			}

		}
	}

	/**
	 * Método para executar toda a lógica de recuperação de um paciente no
	 * sistema através do seu código. O método recupera (insere um paciente que
	 * havia deixado de existir) o paciente com base no seu histórico de
	 * paciente e journal do paciente.
	 * 
	 * @param codigo
	 * @throws ApplicationBusinessException
	 */
	public void recuperarPaciente(Integer codigo) throws ApplicationBusinessException {
		try {
			// Busca registro de histórico de paciente que será base do paciente cadastrado
			AipPacientesHist historicoPaciente = this.getHistoricoPacienteFacade().obterHistoricoPaciente(null, codigo);
			
			// Busca último prntAtivo do paciente através de seu journal		
			AipPacientesJn pacienteJournal = this.getPacienteJournalON().obterPacienteJournal(codigo, DominioOperacoesJournal.UPD); 
	
			// Teoricamente o pacineteJournal nunca será nulo
			if (pacienteJournal != null) {
				historicoPaciente.setPrntAtivo(pacienteJournal.getPrntAtivo());
			} else {
				// Não encontrada situação anterior!
				throw new ApplicationBusinessException(CadastroPacienteONExceptionCode.SITUACAO_ANTERIOR_NAO_ENCONTRADA);
			}
			
			// Atualiza o histórico do paciente com o novo status
			this.getHistoricoPacienteFacade().persistirHistoricoPaciente(historicoPaciente);
			
		} catch (Exception t) {
			LOG.error("Erro ao recuperar Paciente.", t);
			throw new ApplicationBusinessException(CadastroPacienteONExceptionCode.ERRO_RECUPERAR_PACIENTE);
		}
	}
	
	/**
	 * Método para gravar um objeto de paciente sem fazer todas as validações de
	 * dados que estão no objeto. Usado para fazer o UPDATE de objetos que já
	 * tenham a consistencia garantida (Ex.: UPDATE em massa para alterar uma
	 * lista de registros de pacientes para pacientes passivos (prntAtivo=P)).
	 * 
	 * @param paciente
	 */
	public void persistirPacienteSemValidacao(AipPacientes paciente) {
		AipPacientes pacienteAnterior = this.getCadastroPacienteRN()
				.obterPacienteAnterior(paciente.getCodigo());

		aipPacientesDAO.merge(paciente);
		aipPacientesDAO.flush();

		// Chamada para a geração do journal (não faz se novo paciente - Ex.:
		// recuperação de paciente através de seu historico)
		if (pacienteAnterior != null) {
			this.getPacienteJournalON().observarAtualizacaoPaciente(
					pacienteAnterior, paciente, DominioOperacoesJournal.UPD);
			aipPacientesDAO.flush();
		}
	}

	/**
	 * Método para atualizar a situação do paciente. Caso a nova situação seja
	 * 'E' ou 'H', o objeto AipPacientes e AipEnderecosPaciente do paciente
	 * recebido por parametros são movidos para os objetos de histórico
	 * (AipPacientesHist e AipEndPacientesHist).
	 * 
	 * @param paciente
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	public void atualizarSitCadPaciente(AipPacientes paciente, String usuarioLogado) throws BaseException {

		if (paciente == null) {
			return;
		}	
		validarSitCadPaciente(paciente);			
		
		AipPacientes pacienteAnterior = this.getCadastroPacienteRN().obterPacienteAnterior(paciente.getCodigo());

		//verificarBloqueioPacienteUbs(paciente, indBloqueioPacienteUbs, servidorLogado, motivoBloqueio);
		
		// Atualiza paciente para gerar seu journal UPD. Isso é feito somente para manter exatamente o mesmo
		// comportamento que o AGH (gera um registro de UPD no journal de pacientes.
		paciente = getPacienteJournalON().observarAtualizacaoPaciente(pacienteAnterior, paciente, DominioOperacoesJournal.UPD);

		// Se prntAtivo do pacienteAtual for diferente do pacienteAnterior
		// (usado ObjectUtils, pois prntAtivo pode vir nulo)
		if (pacienteAnterior != null && !Objects.equals(pacienteAnterior.getPrntAtivo(), paciente.getPrntAtivo())) {

			// Se prntAtivo do pacienteAtual for H ou E
			if ((DominioTipoProntuario.H.equals(paciente.getPrntAtivo()) || DominioTipoProntuario.E.equals(paciente.getPrntAtivo()))) {

				this.getExamesLaudosFacade().removerProtocolosNeurologia(paciente.getCodigo());

				// Se o prntAtivo do pacienteAnterior não for H ou E
				if (!DominioTipoProntuario.H.equals(pacienteAnterior.getPrntAtivo())
						&& !DominioTipoProntuario.E.equals(pacienteAnterior.getPrntAtivo())) {

					// Insere AipPacientesHist e remove AipPacientes
					this.getCadastroPacienteRN().inserirHistoricoPaciente(paciente, usuarioLogado);
					
					this.getCadastroPacienteRN().excluirPaciente(pacienteAnterior,usuarioLogado != null ? usuarioLogado : null);
				}
			}
		}

		try {
			
			this.getAipPacientesDAO().flush();
			
		}catch (Exception cve){			
			if (cve.getCause() instanceof ConstraintViolationException){
				throw new ApplicationBusinessException(CadastroPacienteONExceptionCode.ERRO_VIOLACAO_CONSTRAINTS, cve);
				
			}else if (cve.getCause() instanceof InactiveModuleException){
				throw new InactiveModuleException(cve.getMessage());
				
			}else{
				throw new ApplicationBusinessException(CadastroPacienteONExceptionCode.ERRO_PERSISTENCIA, cve);
			}

		}
	}
	
	/**
	 * #46172
	 * @param pacCodigo
	 * @param indBloqueioPacienteUbs
	 */
//	private void verificarBloqueioPacienteUbs(AipPacientes paciente, Boolean indBloqueioPacienteUbs, RapServidores servidorLogado, String motivoBloqueio) {
//		AipPacientesBloqueioUbs pacienteBloqueioUbsAnterior = getAipPacientesBloqueioUbsDAO().obterUltimoIndPacienteBloqueioUbs(paciente.getCodigo());
//		if ((pacienteBloqueioUbsAnterior != null && pacienteBloqueioUbsAnterior.getIndPacienteBloqueado() != null && !pacienteBloqueioUbsAnterior
//				.getIndPacienteBloqueado().equals(indBloqueioPacienteUbs)
//				|| ((pacienteBloqueioUbsAnterior == null || pacienteBloqueioUbsAnterior.getIndPacienteBloqueado() == null) && indBloqueioPacienteUbs))) {
//			AipPacientesBloqueioUbs pacienteBloqueioUbs = new AipPacientesBloqueioUbs();
//			pacienteBloqueioUbs.setPaciente(paciente);
//			pacienteBloqueioUbs.setIndPacienteBloqueado(indBloqueioPacienteUbs);
//			pacienteBloqueioUbs.setCriadoEm(new Date());
//			pacienteBloqueioUbs.setServidor(servidorLogado);
//			pacienteBloqueioUbs.setMotivoBloqueio(motivoBloqueio);
//			getCadastroPacienteRN().inserirPacienteBloqueioUbs(pacienteBloqueioUbs);
//		}
//	}
//	
	
	private void validarSitCadPaciente(AipPacientes paciente) throws BaseException {
		if (paciente.getProntuario() != null && paciente.getProntuario() <= VALOR_MAXIMO_PRONTUARIO) {
			if (paciente.getCor() == null) {
				throw new ApplicationBusinessException(
						CadastroPacienteONExceptionCode.VALIDACAO_PACIENTE_COM_PRONTUARIO_SEM_COR);
			}
			if (paciente.getSexo() == null) {
				throw new ApplicationBusinessException(
						CadastroPacienteONExceptionCode.VALIDACAO_PACIENTE_COM_PRONTUARIO_SEM_SEXO);
			}

			if (paciente.getGrauInstrucao() == null) {
				throw new ApplicationBusinessException(
						CadastroPacienteONExceptionCode.VALIDACAO_PACIENTE_COM_PRONTUARIO_SEM_INSTRUCAO);
			}

			if (paciente.getNomePai() == null
					|| paciente.getNomePai().isEmpty()) {
				throw new ApplicationBusinessException(
						CadastroPacienteONExceptionCode.VALIDACAO_PACIENTE_COM_PRONTUARIO_SEM_PAI);
			}

			if (paciente.getEstadoCivil() == null) {
				throw new ApplicationBusinessException(
						CadastroPacienteONExceptionCode.VALIDACAO_PACIENTE_COM_PRONTUARIO_SEM_ESTADO_CIVIL);
			}
		}
		
	}

	private void atualizarSolicitacaoProntuario( AipSolicitacaoProntuarios solicitacaoProntuarios, List<AipPacientes> listaPacientesRemovidos) throws ApplicationBusinessException {
		
		for (AipPacientes paciente : listaPacientesRemovidos) {
			pacienteFacade.validaRemocaoMovimentacaoProntuario(
					solicitacaoProntuarios.getCodigo(), paciente.getCodigo());
		}

		aipSolicitacaoProntuariosDAO.merge(solicitacaoProntuarios);
	}

	public void removerSolicitacaoProntuario(AipSolicitacaoProntuarios solicitacaoProntuarios) throws ApplicationBusinessException {
		try {
			this.getAipSolicitacaoProntuariosDAO().removerPorId(solicitacaoProntuarios.getCodigo());
			this.getAipSolicitacaoProntuariosDAO().flush();
		} catch (Exception e) {
			LOG.error(
					"Erro ao remover a solicitação de prontuário.", e);
			throw new ApplicationBusinessException(
					CadastroPacienteONExceptionCode.ERRO_EXCLUIR_SOLICITACAO_PRONTUARIO);
		}
	}

	/**
	 * ORADB Trigger AIPT_PSP_BRI Implementar SEGURANCA after insert
	 * 
	 */
	public void persistirSolicitacaoProntuario(
			AipSolicitacaoProntuarios solicitacaoProntuarios,
			List<AipPacientes> listaPacientesRemovidos)
			throws ApplicationBusinessException {
		AipSolicitacaoProntuarios aipSolicitacaoProntuarios = this
				.obterSolicitacaoProntuarioAnterior(solicitacaoProntuarios
						.getCodigo());
		if (aipSolicitacaoProntuarios == null) {
			// inclusão
			this.incluirSolicitacaoProntuario(solicitacaoProntuarios);
		} else {
			// edição
			this.atualizarSolicitacaoProntuario(solicitacaoProntuarios,
					listaPacientesRemovidos);
		}
	}

	public AipSolicitacaoProntuarios obterSolicitacaoProntuarioAnterior(
			Integer codigo) {
		return this.getAipSolicitacaoProntuariosDAO()
				.obterSolicitacaoProntuarioAnterior(codigo);
	}

	public void validaProntuariosSolicitacao(List<AipPacientes> lista)
			throws ApplicationBusinessException {
		int valida = 0;
		for (AipPacientes paciente : lista) {
			for (AipPacientes pacienteAux : lista) {
				if (paciente.getProntuario() != null
						&& paciente.getProntuario().equals(
								pacienteAux.getProntuario())) {
					valida = valida + 1;
					if (valida == 2) {
						throw new ApplicationBusinessException(
								CadastroPacienteONExceptionCode.ERRO_PERSISTIR_MESMO_PRONTUARIO,
								paciente.getProntuario().toString());
					}
				}
			}
			valida = 0;
		}
	}

	private void incluirSolicitacaoProntuario(
			AipSolicitacaoProntuarios solicitacaoProntuarios)
			throws ApplicationBusinessException {
		solicitacaoProntuarios.setDtSolicitacao(Calendar.getInstance()
				.getTime());
		this.getAipSolicitacaoProntuariosDAO().persistir(solicitacaoProntuarios);
		this.getAipSolicitacaoProntuariosDAO().flush();
	}

	public void persistirPacienteDeHistoricoPaciente(
			AipPacientesHist pacienteHist) throws ApplicationBusinessException {
		if (pacienteHist != null) {
			this.getCadastroPacienteRN().persistirPacienteDeHistoricoPaciente(
					pacienteHist);
		}
	}

	/**
	 * Método para impressão de etiquetas de paciente.
	 * 
	 * @param DominioTipoImpressao
	 *            tipoImpressao (T - todos, A - a partir de, I - informado)
	 * @param nroVolumeSelecionado
	 *            (seleção do volume na interface)
	 * @param nroVolumesPaciente
	 *            (volume do objeto de paciente corrente)
	 * @param prontuario
	 * @param nome
	 * @return se não for possível fazer impressão, retorna chave da mensagem de
	 *         erro
	 */
	public String imprimirEtiquetasPaciente(DominioTipoImpressao tipoImpressao,
			Integer nroVolumeSelecionado, Integer nroVolumesPaciente,
			Integer prontuario, String nome) throws ApplicationBusinessException {

		StringBuffer sb = new StringBuffer();

		if (DominioTipoImpressao.T.equals(tipoImpressao)) {

			Integer volume = 0;
			// Imprimir etiqueta para todos os volumes do paciente
			for (int i = 0; i <= nroVolumesPaciente; i++) {
				sb.append(this.getPacienteFacade().gerarZpl(prontuario,
						volume.shortValue(), nome));
				volume++;
			}
		} else if (DominioTipoImpressao.I.equals(tipoImpressao)) {
			// Imprimir etiquetas somente para volume informado
			sb.append(this.getPacienteFacade().gerarZpl(prontuario,
					nroVolumeSelecionado.shortValue(), nome));
		} else if (DominioTipoImpressao.A.equals(tipoImpressao)) {
			// Imprimir etiquetas a partir de volume informado
			for (int i = nroVolumeSelecionado; i <= nroVolumesPaciente; i++) {
				// Usada essa variável para facilitar a conversao de
				// int para Short nos parametros da chamada de
				// gerarEtiquetas
				Integer volume = i;

				sb.append(this.getPacienteFacade().gerarZpl(prontuario,
						volume.shortValue(), nome));
			}
		}

		return sb.toString();
	}

	/**
	 * Método para validar se valores informados na tela "Situação Cadastro" são
	 * válidos.
	 * 
	 * @param tipoImpressao
	 * @param nroVolumeSelecionado
	 * @param nroVolumesPaciente
	 * @throws ApplicationBusinessException
	 */
	public void validarImpressaoEtiquetas(DominioTipoImpressao tipoImpressao,
			Integer nroVolumeSelecionado, Integer nroVolumesPaciente)
			throws ApplicationBusinessException {

		if (tipoImpressao == null) {
			// Não foi selecionado tipo de impressao (I, A ou T)
			throw new ApplicationBusinessException(
					CadastroPacienteONExceptionCode.MENSAGEM_ERRO_IMPRIMIR_TIPO_IMPRESSAO_NAO_SELECIONADO);
		} else if ((DominioTipoImpressao.I.equals(tipoImpressao) || DominioTipoImpressao.A
				.equals(tipoImpressao)) && nroVolumeSelecionado == null) {
			// Selecionada opção I ou A, porém não informado o numero de
			// etiquetas
			throw new ApplicationBusinessException(
					CadastroPacienteONExceptionCode.MENSAGEM_ERRO_IMPRIMIR_VOLUME_INFORMADO_NULO);
		}
	}

	/**
	 * Método que verifica atributos necessários para definir o valor do campo
	 * IND_EXTERNO do registro de Dados Adicionais
	 * 
	 * @param dadosAdicionais
	 */
	@SuppressWarnings("all")
	public void buscaDadosRn(AipPacienteDadoClinicos dadosAdicionais) {
		List<McoRecemNascidos> listaRecemNascidos = this
				.getPerinatologiaFacade().listaRecemNascidosPorCodigoPaciente(
						dadosAdicionais.getAipPaciente().getCodigo());

		if (listaRecemNascidos.size() == 0) {
			dadosAdicionais.setIndExterno(DominioSimNao.S);
		} else {
			McoRecemNascidos recemNascidoResultado = null;
			McoNascimentos nascimentoResultado = null;
			AghAtendimentos atendimentoResultado = null;
			McoExameFisicoRns exameResultado = null;
			McoGestacoes gestacaoResultado = null;
			boolean achou = false;
			// LOOP para verificar restrição das datas (não feita na
			// criteria)
			for (McoRecemNascidos recemNascidos : listaRecemNascidos) {
				recemNascidoResultado = recemNascidos;
				exameResultado = recemNascidos.getMcoExameFisicoRns();
				gestacaoResultado = recemNascidos.getMcoGestacoes();

				Set<McoNascimentos> nascimentos = gestacaoResultado
						.getMcoNascimentoses();
				Iterator it = nascimentos.iterator();
				while (it.hasNext()) {
					nascimentoResultado = (McoNascimentos) it.next();
					gestacaoResultado = nascimentoResultado.getMcoGestacoes();
					Date dthrNascimento = nascimentoResultado
							.getDthrNascimento();

					achou = false;

					List<AghAtendimentos> atendimentos = getAghuFacade()
							.pesquisarAtendimentosPorPacienteGestacao(
									recemNascidos.getMcoGestacoes().getId()
											.getPacCodigo(),
									recemNascidos.getMcoGestacoes().getId()
											.getSeqp());

					Iterator itAtend = atendimentos.iterator();
					while (itAtend.hasNext()) {
						atendimentoResultado = (AghAtendimentos) itAtend.next();
						Date dthrInicio = atendimentoResultado.getDthrInicio();
						Date dthrFim = atendimentoResultado.getDthrFim();
						// Restrição das datas
						if (dthrInicio.compareTo(dthrNascimento) <= 0
								&& (dthrFim == null || dthrFim
										.compareTo(dthrNascimento) >= 0)) {
							achou = true;
							break;
						}
					}
					if (achou) {
						break;
					}
				}
				if (achou) {
					break;
				}
			}
			if (!achou) {
				dadosAdicionais.setIndExterno(DominioSimNao.S);
			} else {
				dadosAdicionais.setDthrNascimento(nascimentoResultado
						.getDthrNascimento());
				dadosAdicionais.setApgar1(recemNascidoResultado.getApgar1());
				dadosAdicionais.setApgar5(recemNascidoResultado.getApgar5());
				dadosAdicionais.setTemperatura(exameResultado.getTemperatura());
				Byte igSemanas = null;
				if (exameResultado.getIgFinal() == null) {
					if (gestacaoResultado.getIgAtualSemanas() == null) {
						igSemanas = 99;
					} else {
						igSemanas = gestacaoResultado.getIgAtualSemanas();
					}
				} else {
					igSemanas = exameResultado.getIgFinal();
				}
				dadosAdicionais.setIgSemanas(igSemanas);

				if (atendimentoResultado.getDthrFim() == null) {
					dadosAdicionais.setIndExterno(DominioSimNao.N);
				}
				Date dataCriacao = dadosAdicionais.getCriadoEm();
				if (dataCriacao == null) {
					dataCriacao = new Date();
				}
				if (atendimentoResultado.getDthrFim() != null
						&& atendimentoResultado.getDthrFim().compareTo(
								dataCriacao) < 0) {
					dadosAdicionais.setIndExterno(DominioSimNao.S);
				} else {
					dadosAdicionais.setIndExterno(DominioSimNao.N);
				}

			}

		}

	}

	/**
	 * Método que define o número de meses de gestação dos Dados Adicionais
	 * 
	 * @param igSemanas
	 */
	public Byte definirMesesGestacaoDadosAdicionais(Byte igSemanas) {
		Byte retorno = null;

		if (igSemanas <= 15) {
			byte meses = 3;
			retorno = meses;
		} else if (igSemanas >= 16 && igSemanas <= 19) {
			byte meses = 4;
			retorno = meses;
		} else if (igSemanas >= 20 && igSemanas <= 23) {
			byte meses = 5;
			retorno = meses;
		} else if (igSemanas >= 24 && igSemanas <= 27) {
			byte meses = 6;
			retorno = meses;
		} else if (igSemanas >= 28 && igSemanas <= 31) {
			byte meses = 7;
			retorno = meses;
		} else if (igSemanas >= 32 && igSemanas <= 35) {
			byte meses = 8;
			retorno = meses;
		} else if (igSemanas >= 36) {
			byte meses = 9;
			retorno = meses;
		}
		return retorno;
	}

	public List<AghAtendimentos> pesquisarAtendimentosPorPaciente(
			Integer codigoPaciente) {
		return this.getCadastroPacienteRN().pesquisarAtendimentosPorPaciente(
				codigoPaciente);
	}
	
	public Integer gerarNumeroProntuarioVirtualPacienteEmergencia(AipPacientes paciente, String nomeMicrocomputador) throws ApplicationBusinessException {
		InterfaceValidaProntuario validadorProntuario = null;
		try {
			validadorProntuario = validaProntuarioFactory
					.getValidaProntuario(true);
		} catch (ValidaProntuarioException e) {
			throw new ApplicationBusinessException(
					CadastroPacienteONExceptionCode.AIP_PRONTUARIO_VALIDADOR_CONFIG_ERROR,
					e);
		}

		AipCodeControlsDAO aipCodeControlsDAO = this.getAipCodeControlsDAO();

		String strValorNovoProntuario = obterProntuarioVirtualParaRecemNascido(validadorProntuario, aipCodeControlsDAO);
		
		paciente.setProntuario(Integer.valueOf(strValorNovoProntuario));
		
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		this.getCadastroPacienteRN().atualizarPaciente(paciente, nomeMicrocomputador, servidorLogado, new Date());
		
		return Integer.valueOf(strValorNovoProntuario);
	}
	
	public boolean verificarMicroAlteraProntuarioFamilia(String nomeMicrocomputador) {

		for(AghCaractMicrocomputador caracteristica: getAdministracaoFacade().pesquisaCaractMicrocomputadorPorNome(nomeMicrocomputador, null)) {
			if(caracteristica.getId().getCaracteristica() == DominioCaracteristicaMicrocomputador.PERFIL_UBS) {
				return true;
			}
		}
		return false;
	}	

	protected IAdministracaoFacade getAdministracaoFacade() {
		return administracaoFacade;
	}
	
	protected ICadastrosBasicosPacienteFacade getCadastrosBasicosPacienteFacade() {
		return cadastrosBasicosPacienteFacade;
	}

	protected IExamesLaudosFacade getExamesLaudosFacade() {
		return this.examesLaudosFacade;
	}
	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade(){
		return this.registroColaboradorFacade;
	}

	protected IHistoricoPacienteFacade getHistoricoPacienteFacade() {
		return historicoPacienteFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}

	protected IPerinatologiaFacade getPerinatologiaFacade() {
		return perinatologiaFacade;
	}

	protected PacienteJournalON getPacienteJournalON() {
		return pacienteJournalON;
	}

	protected EnderecoON getEnderecoON() {
		return enderecoON;
	}

	protected CadastroPacienteRN getCadastroPacienteRN() {
		return cadastroPacienteRN;
	}
	
	protected PesoPacienteRN getPesoPacienteRN() {
		return pesoPacienteRN;
	}

	protected AipCodeControlsDAO getAipCodeControlsDAO() {
		return aipCodeControlsDAO;
	}

	protected AipConveniosSaudePacienteDAO getAipConveniosSaudePacienteDAO() {
		return aipConveniosSaudePacienteDAO;
	}

	protected AipEnderecosPacientesJnDAO getAipEnderecosPacientesJnDAO() {
		return aipEnderecosPacientesJnDAO;
	}
	
	protected AipOrgaosEmissoresDAO getAipOrgaosEmissoresDAO() {
		return aipOrgaosEmissoresDAO;
	}

	protected AipPacienteDadoClinicosDAO getAipPacienteDadoClinicosDAO() {
		return aipPacienteDadoClinicosDAO;
	}

	protected AipPacientesDadosCnsDAO getAipPacientesDadosCnsDAO() {
		return aipPacientesDadosCnsDAO;
	}
	
	protected AipPacienteProntuarioDAO getAipPacienteProntuarioDAO() {
		return aipPacienteProntuarioDAO;
	}	

	protected AipPacientesDAO getAipPacientesDAO() {
		return aipPacientesDAO;
	}

	protected AipPesoPacientesDAO getAipPesoPacientesDAO() {
		return aipPesoPacientesDAO;
	}

	protected AipProntuarioLiberadosDAO getAipProntuarioLiberadosDAO() {
		return aipProntuarioLiberadosDAO;
	}

	protected AipSolicitacaoProntuariosDAO getAipSolicitacaoProntuariosDAO() {
		return aipSolicitacaoProntuariosDAO;
	} 
	
	public boolean verificarPacienteModificado(AipPacientes pacienteAtual) {
		AipPacientes pacienteBanco = getCadastroPacienteRN()
				.obterPacienteAnterior(pacienteAtual.getCodigo());
		return getCadastroPacienteRN().pacienteModificado(pacienteAtual,
				pacienteBanco);
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}