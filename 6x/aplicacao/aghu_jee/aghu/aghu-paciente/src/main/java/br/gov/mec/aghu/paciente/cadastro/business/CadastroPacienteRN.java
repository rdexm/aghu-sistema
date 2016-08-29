package br.gov.mec.aghu.paciente.cadastro.business;

import static br.gov.mec.aghu.model.AipPacientes.VALOR_MAXIMO_PRONTUARIO;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.business.fonetizador.FonetizadorUtil;
import br.gov.mec.aghu.core.business.moduleintegration.InactiveModuleException;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.core.mail.EmailUtil;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.DateValidator;
import br.gov.mec.aghu.dao.ObjetosOracleDAO;
import br.gov.mec.aghu.dominio.DominioCor;
import br.gov.mec.aghu.dominio.DominioEstadoCivil;
import br.gov.mec.aghu.dominio.DominioGrauInstrucao;
import br.gov.mec.aghu.dominio.DominioMomento;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSexoDeterminante;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoProntuario;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AipAlturaPacientes;
import br.gov.mec.aghu.model.AipCidades;
import br.gov.mec.aghu.model.AipConveniosSaudePaciente;
import br.gov.mec.aghu.model.AipEndPacientesHist;
import br.gov.mec.aghu.model.AipEnderecosPacientes;
import br.gov.mec.aghu.model.AipFonemaNomeSocialPacientes;
import br.gov.mec.aghu.model.AipFonemaPacientes;
import br.gov.mec.aghu.model.AipFonemas;
import br.gov.mec.aghu.model.AipFonemasMaePaciente;
import br.gov.mec.aghu.model.AipNacionalidades;
import br.gov.mec.aghu.model.AipPacienteDadoClinicos;
import br.gov.mec.aghu.model.AipPacienteProntuario;
import br.gov.mec.aghu.model.AipPacienteProntuarioJn;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.AipPacientesDadosCns;
import br.gov.mec.aghu.model.AipPacientesHist;
import br.gov.mec.aghu.model.AipPacientesJn;
import br.gov.mec.aghu.model.AipPosicaoFonemasMaePaciente;
import br.gov.mec.aghu.model.AipPosicaoFonemasMaePacienteId;
import br.gov.mec.aghu.model.AipPosicaoFonemasNomeSocialPacientes;
import br.gov.mec.aghu.model.AipPosicaoFonemasNomeSocialPacientesId;
import br.gov.mec.aghu.model.AipPosicaoFonemasPacientes;
import br.gov.mec.aghu.model.AipPosicaoFonemasPacientesId;
import br.gov.mec.aghu.model.AipProntuarioLiberados;
import br.gov.mec.aghu.model.McoRecemNascidos;
import br.gov.mec.aghu.model.PacIntdConv;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.dao.AipAlturaPacientesDAO;
import br.gov.mec.aghu.paciente.dao.AipCidadesDAO;
import br.gov.mec.aghu.paciente.dao.AipConveniosSaudePacienteDAO;
import br.gov.mec.aghu.paciente.dao.AipEnderecosPacientesDAO;
import br.gov.mec.aghu.paciente.dao.AipFonemaNomeSocialPacientesDAO;
import br.gov.mec.aghu.paciente.dao.AipFonemaPacientesDAO;
import br.gov.mec.aghu.paciente.dao.AipFonemasDAO;
import br.gov.mec.aghu.paciente.dao.AipFonemasMaePacienteDAO;
import br.gov.mec.aghu.paciente.dao.AipNacionalidadesDAO;
import br.gov.mec.aghu.paciente.dao.AipOcupacoesDAO;
import br.gov.mec.aghu.paciente.dao.AipPacienteProntuarioDAO;
import br.gov.mec.aghu.paciente.dao.AipPacienteProntuarioJNDAO;
import br.gov.mec.aghu.paciente.dao.AipPacientesDAO;
import br.gov.mec.aghu.paciente.dao.AipPacientesJnDAO;
import br.gov.mec.aghu.paciente.dao.AipPosicaoFonemasMaePacienteDAO;
import br.gov.mec.aghu.paciente.dao.AipPosicaoFonemasNomeSocialPacientesDAO;
import br.gov.mec.aghu.paciente.dao.AipPosicaoFonemasPacientesDAO;
import br.gov.mec.aghu.paciente.dao.AipProntuarioLiberadosDAO;
import br.gov.mec.aghu.paciente.dao.AipUfsDAO;
import br.gov.mec.aghu.paciente.dao.PacIntdConvDAO;
import br.gov.mec.aghu.paciente.historico.business.IHistoricoPacienteFacade;
import br.gov.mec.aghu.paciente.prontuario.business.exception.ProntuarioExceptionCode;
import br.gov.mec.aghu.paciente.vo.AghParametrosVO;
import br.gov.mec.aghu.paciente.vo.DadosAdicionaisVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.util.PesquisaCamposPojoPorAnotacoes;

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
 */
@SuppressWarnings({ "PMD.AghuTooManyMethods", "PMD.CyclomaticComplexity",
		"PMD.ExcessiveClassLength",
		"PMD.CouplingBetweenObjects" })
@Stateless
public class CadastroPacienteRN extends BaseBusiness {

	private static final String HIFEM = " - ";

	private static final String PACIENTE_ATUALIZADO = "Paciente Atualizado: ";

	@Inject
	private ObjetosOracleDAO objetosOracleDAO;
	
	@EJB
	private EnderecoON enderecoON;
	
	@EJB
	private PacienteJournalON pacienteJournalON;
	
	private static final Log LOG = LogFactory.getLog(CadastroPacienteRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AipUfsDAO aipUfsDAO;
	
	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@Inject
	private AipNacionalidadesDAO aipNacionalidadesDAO;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@Inject
	private AipEnderecosPacientesDAO aipEnderecosPacientesDAO;
	
	@Inject
	private EmailUtil emailUtil;
	
	@Inject
	private AipCidadesDAO aipCidadesDAO;
	
	@Inject
	private AipPacientesDAO aipPacientesDAO;
	
	@Inject
	private AipPosicaoFonemasPacientesDAO aipPosicaoFonemasPacientesDAO;
	
	@EJB
	private IHistoricoPacienteFacade historicoPacienteFacade;
	
	@Inject
	private AipPacientesJnDAO aipPacientesJnDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private ICascaFacade cascaFacade;
	
	@Inject
	private PacIntdConvDAO pacIntdConvDAO;
	
	@Inject
	private AipFonemasDAO aipFonemasDAO;
	
	@Inject
	private AipOcupacoesDAO aipOcupacoesDAO;
	
	@EJB
	private ICentroCustoFacade centroCustoFacade;
	
	@Inject
	private AipPosicaoFonemasMaePacienteDAO aipPosicaoFonemasMaePacienteDAO;
	
	@Inject
	private AipFonemasMaePacienteDAO aipFonemasMaePacienteDAO;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@Inject
	private AipFonemaPacientesDAO aipFonemaPacientesDAO;
	
	@Inject
	private AipAlturaPacientesDAO aipAlturaPacientesDAO;
	
	@Inject
	private AipProntuarioLiberadosDAO aipProntuarioLiberadosDAO;
	
	@Inject
	private AipPosicaoFonemasNomeSocialPacientesDAO aipPosicaoFonemasNomeSocialPacientesDAO;

	@Inject
	private AipFonemaNomeSocialPacientesDAO aipFonemaNomeSocialPacientesDAO;

	@Inject
	private AipPacienteProntuarioDAO aipPacienteProntuarioDAO;

	@Inject
	private AipPacienteProntuarioJNDAO aipPacienteProntuarioJNDAO;
	
	@Inject AipConveniosSaudePacienteDAO aipConveniosSaudePacienteDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1925480598894761172L;

	private enum CadastroPacienteRNExceptionCode implements
			BusinessExceptionCode {
		AIP_00211, AIP_00212, AIP_00213, AIP_00214, AIP_00215, AIP_00217, AIP_00207, AIP_00206, AIP_00013, AIP_00120, AIP_00192, 
		SEXO_DETERMINANTE_DIFERENTE, SEXO_OCUPACAO_DIFERENTE, ERRO_NAO_ESPERADO_VERIFICACAO_SEXO_QUARTOS, ERRO_LIBERACAO_PRONTUARIO, 
		VIOLACAO_FK_PACIENTE, PRONTUARIO_EXISTENTE, CODIGO_EXISTENTE, ERRO_EXCLUSAO_PACIENTE, AIP_00351, PRONTUARIO_NULO, MODULO_INATIVO,
		DDD_RESIDENCIAL_REQUIRED, TELEFONE_RESIDENCIAL_REQUIRED;
	}

	// Salva o nome do paciente e de sua mãe fonetizados (sem acentos)
	// Esta regra é utilizada apenas no HCPA.
	private void limpaNomes(AipPacientes paciente) throws ApplicationBusinessException {
		if (isHCPA()) {
			paciente.setNome(FonetizadorUtil.ajustarNome(paciente.getNome()));
			paciente.setNomeMae(FonetizadorUtil.ajustarNome(paciente.getNomeMae()));
			if (paciente.getNomePai() != null) {
				paciente.setNomePai(FonetizadorUtil.ajustarNome(paciente.getNomePai()));
			}
		}
	}

	/**
	 * Método que chama as implementações das triggers responsáveis pela
	 * inserção de um registro da tabela AIP_PACIENTES
	 * 
	 * 
	 * @param paciente
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public void inserirPaciente(AipPacientes paciente)
			throws ApplicationBusinessException {
		// TODO ...
		LOG.info("<<<<<<<<<<<<<<<<<<AIP_PACIENTE_27>>>>>>>>>>>>>>>>");
		LOG.info("Paciente: " + paciente.getProntuario() + HIFEM + paciente.getNome());
		this.limpaNomes(paciente);

		this.getAipPacientesDAO().persistir(paciente);
		this.enforcePacienteInclusao(paciente);
		LOG.info("<<<<<<<<<<<<<<<<<<AIP_PACIENTE_28>>>>>>>>>>>>>>>>");
		LOG.info("Paciente: " + paciente.getProntuario() + HIFEM + paciente.getNome());
		
	}
	
	/**
	 * Método que insere um paciente proveniente da funcionalidade de Migração 
	 * (não chama implementações das regras de negócio)
	 * @param paciente
	 */
	public void inserirPacienteMigracao(AipPacientes paciente){
		this.getAipPacientesDAO().persistir(paciente);
	}

	/**
	 * Método que chama as implementações das triggers responsáveis pela
	 * atualização de um registro da tabela AIP_PACIENTES
	 * 
	 * 
	 * @param paciente
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public void atualizarPaciente(AipPacientes paciente, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws ApplicationBusinessException {
		atualizarPaciente(paciente, nomeMicrocomputador, null,dataFimVinculoServidor, null);
	}
	
	public void atualizarPaciente(AipPacientes paciente, String nomeMicrocomputador, RapServidores servidorLogado,  final Date dataFimVinculoServidor)
			throws ApplicationBusinessException {
		getAipPacientesDAO().desatachar(paciente);
		atualizarPaciente(paciente, nomeMicrocomputador, servidorLogado, dataFimVinculoServidor, null);
	}
	
	public void atualizarPaciente(AipPacientes paciente, String nomeMicrocomputador, RapServidores servidorLogado , final Date dataFimVinculoServidor, Integer prontuarioAnterior)
			throws ApplicationBusinessException {
		LOG.info("<<<<<<<<<<<<<<<<<<AIP_PACIENTE_37>>>>>>>>>>>>>>>>");
		LOG.info(PACIENTE_ATUALIZADO + paciente.getProntuario() + HIFEM + paciente.getNome());


		if (paciente.getProntuario() != null) {
			verificarProntuario(paciente);
		}
		this.limpaNomes(paciente);
		atualizarProntuario(paciente, servidorLogado);
		this.atualizarPacienteTrigger(paciente);
		this.enforcePacienteAtualizacao(paciente, nomeMicrocomputador, servidorLogado, dataFimVinculoServidor, prontuarioAnterior );
		LOG.info("<<<<<<<<<<<<<<<<<<AIP_PACIENTE_40>>>>>>>>>>>>>>>>");
		LOG.info(PACIENTE_ATUALIZADO + paciente.getProntuario() + HIFEM + paciente.getNome());
		
		// Quando da tela que atualiza dt_obito_externo o objeto vindo da tela não estava sendo persistido... 
		aipPacientesDAO.merge(paciente);
		aipPacientesDAO.flush();
		LOG.info("<<<<<<<<<<<<<<<<<<AIP_PACIENTE_41>>>>>>>>>>>>>>>>");
		LOG.info(PACIENTE_ATUALIZADO + paciente.getProntuario() + HIFEM + paciente.getNome());
		
	}
	
	private void atualizarProntuario(AipPacientes paciente, RapServidores servidorLogado) {
		AipPacientes pacienteOld = obterPacienteAnterior(paciente.getCodigo());
		
		if (paciente.getProntuario() != null 
				&& !paciente.getProntuario().equals(pacienteOld.getProntuario())
				) {
			AipPacienteProntuario aipPacienteProntuarioOld = new AipPacienteProntuario();
			if(pacienteOld.getProntuario() != null){
				aipPacienteProntuarioOld = this.getAipPacienteProntuarioDAO().obterPorChavePrimaria(pacienteOld.getProntuario());
			}
			AipPacienteProntuario aipPacienteProntuario = new AipPacienteProntuario();
			aipPacienteProntuario.setCriadoEm(new Date());
			aipPacienteProntuario.setProntuario(paciente.getProntuario());
			aipPacienteProntuario.setSamis(aipPacienteProntuarioOld.getSamis() != null ? aipPacienteProntuarioOld.getSamis() : null);
			aipPacienteProntuario.setServidor(servidorLogado);
			getAipPacienteProntuarioDAO().remover(aipPacienteProntuarioOld);
			getAipPacienteProntuarioDAO().persistir(aipPacienteProntuario);			
		}
	}

	
	private void popularAipPacienteProntuarioJN(
			AipPacienteProntuario aipPacienteProntuarioOld, String usuarioLogado, DominioOperacoesJournal operacaoJournal) {
		AipPacienteProntuarioJn aipPacienteProntuarioJn = new AipPacienteProntuarioJn();
		aipPacienteProntuarioJn.setCriadoEm(aipPacienteProntuarioOld.getCriadoEm());
		aipPacienteProntuarioJn.setProntuario(aipPacienteProntuarioOld.getProntuario());
		aipPacienteProntuarioJn.setNomeUsuario(usuarioLogado);
		aipPacienteProntuarioJn.setOperacao(operacaoJournal);
		aipPacienteProntuarioJn.setSeqSamis(aipPacienteProntuarioOld.getSamis() == null ? null : aipPacienteProntuarioOld.getSamis().getCodigo());
		aipPacienteProntuarioJn.setSerMatricula(aipPacienteProntuarioOld.getServidor().getId().getMatricula());
		aipPacienteProntuarioJn.setSerVinCodigo(aipPacienteProntuarioOld.getServidor().getId().getVinCodigo());
		getAipPacienteProntuarioJNDAO().persistir(aipPacienteProntuarioJn);
		getAipPacienteProntuarioJNDAO().flush();
	}	

	/**
	 * Cadastro de Paciente. Método responsável por implementar a procedure
	 * AIPK_PAC_VER.RN_PACP_VER_CL_OBRIG.
	 * 
	 * @return Retorna se os campos estão preenchidos ou não
	 */
	public boolean verificarCamposObrigatoriosPaciente(
			DominioSimNao geraProntuario, DominioCor cor, DominioSexo sexo,
			DominioGrauInstrucao grauInstrucao, String nomePai,
			DominioEstadoCivil estadoCivil, AipNacionalidades nacionalidade,
			Short dddFoneResidencial, Long foneResidencial)
			throws ApplicationBusinessException {

		this.realizarValidacaoCampo(sexo, CadastroPacienteRNExceptionCode.AIP_00212);
		if (geraProntuario.equals(DominioSimNao.S)) {
			this.realizarValidacaoCampo(cor, CadastroPacienteRNExceptionCode.AIP_00211);
			this.realizarValidacaoCampoParametrizavel(grauInstrucao, AghuParametrosEnum.P_CADASTRO_PACIENTES_GRAU_INSTRUCAO_OBRIGATORIO, 
					true, false, CadastroPacienteRNExceptionCode.AIP_00213);
			this.realizarValidacaoCampoParametrizavel(nomePai, AghuParametrosEnum.P_CADASTRO_PACIENTES_NOME_PAI_OBRIGATORIO, 
					true, false, CadastroPacienteRNExceptionCode.AIP_00214);
			this.realizarValidacaoCampo(estadoCivil, CadastroPacienteRNExceptionCode.AIP_00215);
			this.realizarValidacaoCampo(nacionalidade, CadastroPacienteRNExceptionCode.AIP_00217);
			this.realizarValidacaoCampoParametrizavel(dddFoneResidencial, AghuParametrosEnum.P_CADASTRO_PACIENTES_DDD_RESIDENCIAL_OBRIGATORIO, 
					false, true, CadastroPacienteRNExceptionCode.DDD_RESIDENCIAL_REQUIRED);
			this.realizarValidacaoCampoParametrizavel(foneResidencial, AghuParametrosEnum.P_CADASTRO_PACIENTES_TELEFONE_RESIDENCIAL_OBRIGATORIO, 
					false, true, CadastroPacienteRNExceptionCode.TELEFONE_RESIDENCIAL_REQUIRED);
			
		}
		return true;
	}

	private boolean validarCampo(Object campo) {

		boolean validar = (campo == null);

		if (!validar && campo instanceof String) {

			validar = StringUtils.isBlank((String) campo);

		}

		return validar;

	}

	private void realizarValidacaoCampo(Object campo, CadastroPacienteRNExceptionCode exceptionCode) throws ApplicationBusinessException {
		boolean validar = this.validarCampo(campo);
		if (validar) {
			throw new ApplicationBusinessException(exceptionCode);
		}
	}

	private void realizarValidacaoCampoParametrizavel(Object campo, AghuParametrosEnum parametro, boolean padrao, boolean esperado, CadastroPacienteRNExceptionCode exceptionCode) throws ApplicationBusinessException {
		boolean validar = this.validarCampo(campo);
		if (validar) {
			boolean required = this.getParametroRequired(parametro, padrao, esperado);
			if (required) {
				throw new ApplicationBusinessException(exceptionCode);
			}
		}
	}

	private Boolean getParametroRequired(AghuParametrosEnum parametro, boolean padrao, boolean esperado) throws ApplicationBusinessException {
		Boolean retorno = padrao; // Comportamento padrão
		if (this.getParametroFacade().verificarExisteAghParametro(parametro) && "S".equalsIgnoreCase(this.getParametroFacade().buscarAghParametro(parametro).getVlrTexto())) {
			// Altera o comportamento padrão
			retorno = esperado;
		}
		return retorno;
	}
	

	/**
	 * Cadastro de Paciente. Método responsável por implementar a procedure
	 * AIPK_PAC_VER.RN_PACP_VER_NATURAL.
	 * 
	 * @return Retorna se, no caso da nacionalidade ser brasileira, a
	 *         naturalidade está preenchida ou não.
	 */
	public boolean validarNaturalidadeNacionalidade(
			AipNacionalidades nacionalidade, AipCidades cidade)
			throws ApplicationBusinessException {

		// TODO Verificar se é necessário validar o parâmetro
		// P_NACIONALIDADE_PADRAO
		if (nacionalidade != null && nacionalidade.getSigla() != null
				&& nacionalidade.getSigla().equalsIgnoreCase("BRA")
				&& cidade == null) {
			throw new ApplicationBusinessException(
					CadastroPacienteRNExceptionCode.AIP_00207);
		}
		return true;
	}

	/**
	 * ORADB Function AIPC_VALIDA_CNS Método que realiza a validação do número
	 * do Cartão SUS do Paciente
	 * 
	 * @param numeroCartao
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public void validarNumeroCartaoSaude(BigInteger numeroCartao)
			throws ApplicationBusinessException {
		boolean passou = false;

		String valor = String.valueOf(numeroCartao);
		if (valor.length() > 15) {
			passou = false;
		} else {
			StringBuffer valorZero = new StringBuffer(valor);
			// Adiciona espaços no início
			for (int i = valor.length(); i < 15; i++) {
				valorZero.insert(0, "0");
			}
			int somat = 0;
			int cont = 15;
			if (valorZero.charAt(0) == '7' || valorZero.charAt(0) == '8' || valorZero.charAt(0) == '9') {
				for (int i = 0; i < 15; i++) {
					String caracter = String.valueOf(valorZero.charAt(i));
					somat += (Integer.valueOf(caracter) * cont);
					cont--;
				}
				if (somat % 11 == 0) {
					passou = true;
				} else {
					passou = false;
				}
			} else {
				String valor11 = valorZero.substring(0, 11);
				for (int i = 0; i < 11; i++) {
					String caracter = String.valueOf(valor11.charAt(i));
					somat += (Integer.valueOf(caracter) * cont);
					cont--;
				}
				int resto = somat % 11;
				int dv = 11 - resto;
				if (dv == 11) {
					dv = 0;
				}
				StringBuffer resultado = new StringBuffer();
				// Retira os espaços do início
				String valor11SemZeros = valor11;
				for (int i = 0; i < valor11.length(); i++) {
					if (valor11.charAt(i) == '0') {
						valor11SemZeros = valor11.substring(i + 1);
					} else {
						break;
					}
				}
				if (dv == 10) {
					somat += 2;
					resto = somat % 11;
					dv = 11 - resto;

					resultado.append(valor11SemZeros).append("001")
							.append(Integer.toString(dv));
				} else {
					resultado.append(valor11SemZeros).append("000")
							.append(Integer.toString(dv));
				}
				if (new BigInteger(resultado.toString()).equals(numeroCartao)) {
					passou = true;
				}
			}
		}
		if (!passou) {
			throw new ApplicationBusinessException(
					CadastroPacienteRNExceptionCode.AIP_00351);
		}

	}

	/**
	 * Cadastro de Paciente. Método que atualiza a Situação do Prontuário na Inclusão
	 * @param usuarioLogado 
	 */
	public void atualizarSituacaoProntuarioInclusao(AipPacientes aipPaciente, String usuarioLogado) throws ApplicationBusinessException {

		validarPerfilParaGerarProntuario(aipPaciente);

		if (aipPaciente.getIndGeraProntuario().equals(DominioSimNao.S)
				&& aipPaciente.getPrntAtivo() == null) {
			if (aipPaciente.getCadConfirmado().equals(DominioSimNao.S)) {
				aipPaciente.setPrntAtivo(DominioTipoProntuario.A);
			} else {
				aipPaciente.setPrntAtivo(DominioTipoProntuario.R);
			}
		}
	}

	/**
	 * Cadastro de Paciente. Método que atualiza a Situação do Prontuário na Edição
	 */
	public void atualizarSituacaoProntuarioEdicao(AipPacientes aipPaciente, AipPacientes aipPacienteOriginal) throws ApplicationBusinessException {

		if (aipPacienteOriginal.getProntuario() == null) {
			validarPerfilParaGerarProntuario(aipPaciente);
		}

		if (aipPaciente.getIndGeraProntuario().equals(DominioSimNao.S)) {

			DominioSimNao cadConfirmadoAnterior = aipPacienteOriginal.getCadConfirmado();
			DominioTipoProntuario prntAtivoAnterior = aipPacienteOriginal.getPrntAtivo();
			DominioSimNao indGeraProntuarioAnterior = aipPacienteOriginal.getIndGeraProntuario();
			DominioSimNao indPacienteVipAnterior = aipPacienteOriginal.getIndPacienteVip();

			if (aipPaciente.getCadConfirmado() != cadConfirmadoAnterior) {
				if (DominioSimNao.N.equals(cadConfirmadoAnterior)) {
					if (DominioTipoProntuario.R.equals(prntAtivoAnterior)) {
						aipPaciente.setPrntAtivo(DominioTipoProntuario.A);
					}
				} else {
					if (DominioTipoProntuario.A.equals(prntAtivoAnterior)) {
						aipPaciente.setPrntAtivo(DominioTipoProntuario.R);
					}
				}
			}

			if (DominioSimNao.N.equals(indGeraProntuarioAnterior)) {
				if (DominioSimNao.S.equals(aipPaciente.getCadConfirmado())) {
					aipPaciente.setPrntAtivo(DominioTipoProntuario.A);
				} else {
					aipPaciente.setPrntAtivo(DominioTipoProntuario.R);
				}
			}

			if (((aipPaciente.getPrntAtivo() != prntAtivoAnterior) && 
					(DominioTipoProntuario.P.equals(prntAtivoAnterior) || DominioTipoProntuario.P.equals(aipPaciente.getPrntAtivo()) )
				) || (aipPaciente.getIndPacienteVip() != indPacienteVipAnterior)) {
				
				RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
				
				if (!this.getICascaFacade().usuarioTemPermissao(servidorLogado.getUsuario(), "paciente", "atualizarSituacaoProntuario")) {
					throw new ApplicationBusinessException(CadastroPacienteRNExceptionCode.AIP_00206);
				}
			}
		}
	}

	/**
	 * Verifica se o usuário tem perfil que permita gerar prontuário
	 * 
	 * @param aipPaciente
	 *            Paciente em processo de inclusão ou edição
	 * @throws ApplicationBusinessException
	 *             Caso não tenha perfil, lança uma exceção
	 */
	private void validarPerfilParaGerarProntuario(AipPacientes aipPaciente)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		// Se não tem a role que permite gerar prontuário e marcou para gerar
		// um, gera erro de permissão
		// if (!this.identity.hasRole("AIPG_ATLZ_PACIENTES")
		if (!this.getICascaFacade().usuarioTemPermissao(servidorLogado.getUsuario(), "paciente","gerarProntuario")
				&& DominioSimNao.S.equals(aipPaciente.getIndGeraProntuario())) {
			throw new ApplicationBusinessException(
					ProntuarioExceptionCode.ERRO_PERMISSAO_GERAR_PRONTUARIO);
		}
	}

	/**
	 * Método que realiza as validações necessárias para uma inserção de
	 * informações do Cartão SUS.
	 * 
	 * 
	 * ORADB Trigger AIPT_PDS_BRI.
	 * 
	 * @param pessoa
	 * @return
	 */
	public void validarDadosCartaoSUSInsert(
			AipPacientesDadosCns aipPacienteDadosCns)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		aipPacienteDadosCns.setCriadoEm(new Date());
		aipPacienteDadosCns.setAlteradoEm(new Date());
		aipPacienteDadosCns.setRapServidor(servidorLogado);
		aipPacienteDadosCns.setRapServidorAlterado(servidorLogado);
	}

	/**
	 * Método que realiza as validações necessárias para uma atualização de
	 * informações do Cartão SUS.
	 * 
	 * 
	 * ORADB Trigger AIPT_PDS_BRU.
	 * 
	 * @param pessoa
	 * @return
	 */
	public void validarDadosCartaoSUSUpdate(
			AipPacientesDadosCns aipPacienteDadosCns)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		aipPacienteDadosCns.setAlteradoEm(new Date());
		aipPacienteDadosCns.setRapServidorAlterado(servidorLogado);
	}

	/**
	 * Método que implementa a enforce AIPP_ENFORCE_PAC_RULES para o evento de
	 * 'INSERT' TODO: Rever melhor nome e localização para este método
	 * 
	 * ORADB AIPP_ENFORCE_PAC_RULES
	 * 
	 * @param paciente
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private void enforcePacienteInclusao(AipPacientes paciente)
			throws ApplicationBusinessException {
		if (paciente.getProntuario() != null) {
			verificarProntuario(paciente);
		}
	}

	/**
	 * Método que implementa a enforce AIPP_ENFORCE_PAC_RULES para o evento de
	 * 'UPDATE' TODO: Rever melhor nome e localização para este método
	 * 
	 * ORADB AIPP_ENFORCE_PAC_RULES
	 * 
	 * @param paciente
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private void enforcePacienteAtualizacao(AipPacientes paciente, String nomeMicrocomputador,RapServidores servidorLogado, final Date dataFimVinculoServidor, Integer prontuarioAnterior)
			throws ApplicationBusinessException {

		corrigeSexoInternacao(paciente);

		/* PROCEDURE DE FONETIZAÇÃO JÁ ESTÁ IMPLEMENTADA */

		DominioTipoProntuario prntAtivoAnterior = obterPrntAtivoAnterior(paciente
				.getCodigo());
		if (!Objects.equals(paciente.getPrntAtivo(), prntAtivoAnterior)
				&& (paciente.getPrntAtivo() != null && (paciente.getPrntAtivo()
						.equals(DominioTipoProntuario.E) || paciente
						.getPrntAtivo().equals(DominioTipoProntuario.H)))
				&& (prntAtivoAnterior != null && (!prntAtivoAnterior
						.equals(DominioTipoProntuario.E) || !prntAtivoAnterior
						.equals(DominioTipoProntuario.H)))) {

			AipPacientes pacienteInserir = obterPacienteAnterior(paciente
					.getCodigo());
			inserirHistoricoPaciente(pacienteInserir, servidorLogado != null ? servidorLogado.getUsuario() : null);
			// Exclui o paciente e seus endereços (endereços estão anotados
			// com
			// DELETE_ORPHANS)
			this.excluirPaciente(paciente,servidorLogado != null ? servidorLogado.getUsuario() : null);
		}
		
		/* Atualizar prontuário no atendimento quando gerado */	
		// #51933, #52221 e #52255 - Solução temporária até que o obterOriginal esteja funcionando corretamente.
		atualizarProntuario(paciente, nomeMicrocomputador, dataFimVinculoServidor, prontuarioAnterior);

		/* Data de Nascimento */
		Date dtNascimentoAnterior = DateUtil.truncaData(obterDataNascimentoAnterior(paciente
				.getCodigo()));
		if (verificarDiferencaDatas(paciente.getDtNascimento(),
				dtNascimentoAnterior)) {
			atualizaDataNascimento(paciente.getDtNascimento(),
					paciente.getCodigo());
		}
	}

	private void atualizarProntuario(AipPacientes paciente, String nomeMicrocomputador, final Date dataFimVinculoServidor,
			Integer prontuarioAnterior) throws ApplicationBusinessException {
		if(prontuarioAnterior == null) {
			prontuarioAnterior = obterProntarioAnterior(paciente.getCodigo());
		}
		
		if (prontuarioAnterior == null && paciente.getProntuario() != null) {
			this.getPacienteFacade().atualizarProntuarioNoAtendimento(
					paciente.getCodigo(), paciente.getProntuario(), nomeMicrocomputador, dataFimVinculoServidor);
		}
		if (prontuarioAnterior != null
				&& paciente.getProntuario() != null
				&& !Objects.equals(paciente.getProntuario(),
						prontuarioAnterior)) {
			this.getPacienteFacade().atualizarProntuarioNoAtendimento(
					paciente.getCodigo(), paciente.getProntuario(), nomeMicrocomputador, dataFimVinculoServidor);
		}
	}

	/**
	 * Método que implementa a enforce AIPP_ENFORCE_PAC_RULES para o evento de
	 * 'DELETE' TODO: Rever melhor nome e localização para este método
	 * 
	 * ORADB AIPP_ENFORCE_PAC_RULES
	 * 
	 * @param paciente
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private void enforcePacienteDelecao(AipPacientes paciente)
			throws ApplicationBusinessException {

		/* Chama a procedure eliminar ocorrência da fonetização do nome */
		String nomeAnterior = obterNomeAnterior(paciente.getCodigo());
		removerFonemaExclusaoPaciente(paciente.getCodigo(), nomeAnterior);

		String nomeMaeAnterior = obterNomeMaeAnterior(paciente.getCodigo());
		removerFonemaExclusaoMaePaciente(paciente.getCodigo(), nomeMaeAnterior);

	}

	/**
	 * Método que remove o fonema do nome do paciente no caso da exclusão de
	 * paciente TODO: Rever melhor nome e localização para este método
	 * 
	 * ORADB Procedure aipk_pac_atu.rn_pacp_atu_del_fon
	 * 
	 * @param codigoPaciente
	 *            , nome
	 * @return
	 */
	public void removerFonemaExclusaoPaciente(Integer codigoPaciente,
			String nome) {
		List<AipFonemaPacientes> fonemas = this.getPacienteFacade()
				.pesquisarFonemasPaciente(codigoPaciente);

		for (AipFonemaPacientes s : fonemas) {
			AipFonemas aipFonema = this.getPacienteFacade()
					.obterAipFonemaPorFonema(s.getAipFonemas().getFonema());
			AipFonemaPacientes aipFonemaPaciente = this.getPacienteFacade()
					.obterAipFonemaPaciente(codigoPaciente, aipFonema);

			if (aipFonemaPaciente != null) {
				this.getPacienteFacade().removerAipFonemaPaciente(
						aipFonemaPaciente);
			}

			if (aipFonema != null) {
				if (aipFonema.getContador() == 1
						&& aipFonema.getContadorMae() == 0) {
					this.getPacienteFacade().removerAipFonema(aipFonema);
				} else {
					aipFonema.setContador(aipFonema.getContador() - 1);
					this.getPacienteFacade().persistirAipFonema(aipFonema);
				}
			}
		}
	}

	/**
	 * Método que remove o fonema da mãe do paciente no caso da exclusão de
	 * paciente TODO: Rever melhor nome e localização para este método
	 * 
	 * ORADB Procedure aipk_pac_atu.rn_pacp_atu_del_fmp
	 * 
	 * @param codigoPaciente
	 *            , nome
	 * @return
	 */
	public void removerFonemaExclusaoMaePaciente(Integer codigoPaciente,
			String nomeMae) {
		List<AipFonemasMaePaciente> fonemas = this.getPacienteFacade()
				.pesquisarFonemasMaePaciente(codigoPaciente);

		for (AipFonemasMaePaciente s : fonemas) {
			AipFonemas aipFonema = this.getPacienteFacade()
					.obterAipFonemaPorFonema(s.getAipFonemas().getFonema());
			AipFonemasMaePaciente aipFonemaMaePaciente = this
					.getPacienteFacade().obterAipFonemaMaePaciente(
							codigoPaciente, aipFonema);
			if (aipFonemaMaePaciente != null) {
				this.getPacienteFacade().removerAipFonemaMaePaciente(
						aipFonemaMaePaciente);
			}
			if (aipFonema != null) {
				if (aipFonema.getContadorMae() == 1
						&& aipFonema.getContador() == 0) {
					this.getPacienteFacade().removerAipFonema(aipFonema);
				} else {
					aipFonema.setContadorMae(aipFonema.getContadorMae() - 1);
					this.getPacienteFacade().persistirAipFonema(aipFonema);
				}
			}
		}
	}

	/**
	 * Método que atualiza a data de nascimento. Se a data de nascimento do
	 * paciente for alterada e este tiver nascido aqui no hospital(tem registro
	 * na mco_recem_nascidos com data de nascimento diferente), alterar a data
	 * de nascimento na mco_recem_nascidos.
	 * 
	 * ORADB Procedure aipk_pac_atu.rn_pacp_atu_nasc
	 * 
	 * @param dtNascimento
	 *            , codigoPaciente
	 * @return
	 * @throws ApplicationBusinessException 
	 */
	public void atualizaDataNascimento(Date dtNascimento, Integer codigoPaciente) throws ApplicationBusinessException {
		try {
			Date dthrNascimento = this.getPacienteFacade()
					.obterDataNascimentoRecemNascidos(codigoPaciente);
			if (dthrNascimento != null
					&& verificarDiferencaDatas(dthrNascimento, dtNascimento)) {
				McoRecemNascidos recemNascido = this.getPacienteFacade()
						.obterRecemNascidosPorCodigo(codigoPaciente);
				recemNascido.setDthrNascimento(dtNascimento);
				this.getPacienteFacade().persistirRecemNascido(recemNascido);
			}
		}
		catch (InactiveModuleException e) {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			getObjetosOracleDAO().executaAtualizaDataNascimento(dtNascimento, codigoPaciente, servidorLogado);
		}
	}

	/**
	 * Método que verifica se o número de prontuário gerado já existe para outro
	 * paciente. TODO: Rever melhor nome e localização para este método
	 * 
	 * @param paciente
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private void verificarProntuario(AipPacientes paciente)
			throws ApplicationBusinessException {
		if (paciente.getProntuario() == null) {
			throw new ApplicationBusinessException(
					CadastroPacienteRNExceptionCode.PRONTUARIO_NULO);
		}

		LOG.info("<<<<<<<<<<<<<<<<<<AIP_PACIENTE_38>>>>>>>>>>>>>>>>");
		LOG.info(PACIENTE_ATUALIZADO + paciente.getProntuario() + HIFEM + paciente.getNome());

		List<AipPacientes> listaPacientes = this.getAipPacientesDAO()
				.listaPacientesPorProntuarioIgnorandoCodigo(
						paciente.getProntuario(), paciente.getCodigo());

		if (listaPacientes.size() > 0) {
			int prontuario = paciente.getProntuario();
			paciente.setCodigo(null);
			paciente.setProntuario(null);
			LOG.info("<<<<<<<<<<<<<<<<<<AIP_PACIENTE_39>>>>>>>>>>>>>>>>");
			LOG.info(PACIENTE_ATUALIZADO + paciente.getProntuario() + HIFEM + paciente.getNome());
			
			throw new ApplicationBusinessException(
					CadastroPacienteRNExceptionCode.PRONTUARIO_EXISTENTE,
					prontuario);
		}
	}

	/**
	 * TODO Este método faz a correção do sexo do paciente verificando
	 * internação
	 * 
	 * @param paciente
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private void corrigeSexoInternacao(AipPacientes paciente)
			throws ApplicationBusinessException {
		DominioSexo sexoAnterior = obterSexoAnterior(paciente.getCodigo());
		if (!Objects.equals(sexoAnterior, paciente.getSexo())) {
			if (paciente.getDtUltInternacao() != null
					&& (paciente.getDtUltAlta() == null || paciente
							.getDtUltInternacao()
							.after(paciente.getDtUltAlta()))) {
				atualizarSexoQuartoRN(paciente);
			}
		}
	}

	/**
	 * 
	 * ORADB aipk_pac_atu.rn_pac_atu_sexo_qrt
	 * 
	 * @param paciente
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public void atualizarSexoQuartoRN(AipPacientes paciente)
			throws ApplicationBusinessException {
		atualizarSexoQuartoProcedure(paciente);
	}

	/**
	 * TODO Este método verifica se a troca de sexo do paciente internado torna
	 * inconsistente o sexo de ocupação do quarto. Se o quarto estiver vazio
	 * atualiza o sexo de ocupação se necessário.
	 * 
	 * ORADB procedure AIPP_PAC_ATU_SEX_QRT
	 * 
	 * @param paciente
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public void atualizarSexoQuartoProcedure(AipPacientes paciente)
			throws ApplicationBusinessException {
		DominioSexo vPacSexo = paciente.getSexo();
		Short vQrtNumero = null;

		// Obtem o quarto onde o paciente está internado
		if (paciente.getLeito() != null || paciente.getQuarto() != null) {
			if (paciente.getLeito() == null) {
				vQrtNumero = paciente.getQuarto().getNumero();
			} else {
				vQrtNumero = obterNumeroQuartoPorLeito(paciente.getLeito()
						.getLeitoID());
			}

			DominioSimNao vIndConsSexo = obterIndConSexoQuarto(vQrtNumero);
			DominioSexoDeterminante vSexoDeterminante = obterSexoDeterminanteQuarto(vQrtNumero);
			// DominioSexo vSexoOcupacao = obterSexoOcupacaoQuarto(vQrtNumero);

			if (DominioSimNao.S.equals(vIndConsSexo)) {
				if ((!DominioSexoDeterminante.Q.equals(vSexoDeterminante))
						&& !vSexoDeterminante.toString().equals(
								vPacSexo.toString())) {

					throw new ApplicationBusinessException(
							CadastroPacienteRNExceptionCode.SEXO_DETERMINANTE_DIFERENTE);
				} else {
					this.verificarSexoPacientesQuarto(paciente, vQrtNumero);
				}
			}
		}
	}

	/**
	 * Método que obtem o número do quarto salvo no banco pelo seu Leito
	 * 
	 * @param leitoID
	 * @return
	 */
	public Short obterNumeroQuartoPorLeito(String leitoID) {
		return this.getInternacaoFacade().obterNumeroQuartoPorLeito(leitoID);
	}

	/**
	 * Método que obtem o indConSexo do quarto salvo no banco pelo número do
	 * quarto
	 * 
	 * @param numero
	 * @return DominioSimNao
	 */
	public DominioSimNao obterIndConSexoQuarto(Short numero) {
		return this.getInternacaoFacade().obterIndConSexoQuarto(numero);
	}

	/**
	 * Método que obtem o sexoDeterminante do quarto salvo no banco pelo número
	 * do quarto
	 * 
	 * @param numero
	 * @return DominioSexoDeterminante
	 */
	public DominioSexoDeterminante obterSexoDeterminanteQuarto(Short numero) {
		return this.getInternacaoFacade().obterSexoDeterminanteQuarto(numero);
	}

	/**
	 * Método que verifica o sexo dos pacientes do quarto
	 * 
	 * @param paciente
	 * @return boolean
	 * @throws ApplicationBusinessException
	 */
	private void verificarSexoPacientesQuarto(AipPacientes paciente,
			Short numero) throws ApplicationBusinessException {
		DominioSexo sexoQuarto = obterSexoQuarto(numero);
		DominioSexo sexoQuartoLeito = obterSexoQuartoLeito(numero);
		if (sexoQuarto != null && sexoQuartoLeito != null
				&& !sexoQuarto.equals(sexoQuartoLeito)) {
			throw new ApplicationBusinessException(
					CadastroPacienteRNExceptionCode.SEXO_OCUPACAO_DIFERENTE);
		}

		AinQuartos quarto = obterQuartoPorNumero(numero);
		if (quarto != null) {
			if (sexoQuarto != null) {
				quarto.setSexoOcupacao(sexoQuarto);
			} else {
				quarto.setSexoOcupacao(sexoQuartoLeito);
			}
			// this.entityManager.persist(quarto);
			this.flush();
		}
	}

	/**
	 * Método que obtem o quarto salvo no banco pelo seu número
	 * 
	 * @param numero
	 * @return DominioSexo
	 */
	public AinQuartos obterQuartoPorNumero(Short numero) {
		return this.getInternacaoFacade().pesquisaQuartoPorNumero(numero);
	}

	/**
	 * Método que obtem o sexo do quarto salvo no banco pelo número do quarto
	 * 
	 * @param numero
	 * @return DominioSexo
	 */
	public DominioSexo obterSexoQuarto(Short numero)
			throws ApplicationBusinessException {
		List<DominioSexo> listaSexos = this.getAipPacientesDAO()
				.listarSexosDoQuarto(numero);
		if (listaSexos.size() == 0) {
			return null;
		} else if (listaSexos.size() == 1) {
			return listaSexos.get(0);
		} else {
			throw new ApplicationBusinessException(
					CadastroPacienteRNExceptionCode.SEXO_OCUPACAO_DIFERENTE);
		}
	}

	/**
	 * Método que obtem o sexo do leito salvo no banco pelo número do quarto e
	 * id do leito
	 * 
	 * @param numero
	 * @return DominioSexo
	 */
	public DominioSexo obterSexoQuartoLeito(Short numero)
			throws ApplicationBusinessException {
		List<DominioSexo> listaSexos = this.getAipPacientesDAO()
				.listarSexosDoLeito(numero);

		if (listaSexos.size() == 0) {
			return null;
		} else if (listaSexos.size() == 1) {
			return listaSexos.get(0);
		} else {
			throw new ApplicationBusinessException(
					CadastroPacienteRNExceptionCode.SEXO_OCUPACAO_DIFERENTE);
		}
	}

	/**
	 * TODO Este método deverá ficar centralizado Método que obtem o sexo do
	 * paciente salvo no banco
	 * 
	 * @param codigoPaciente
	 * @return
	 */
	public DominioSexo obterSexoAnterior(Integer codigoPaciente) {
		return this.getAipPacientesDAO().obterSexoAnterior(codigoPaciente);
	}

	/**
	 * Método para verificar se as tabelas AIP_PACIENTES e AIP_PACIENTES_HIST
	 * estão com a estrutura no BD iguais. Caso as duas tabelas não estejam com
	 * a mesma estrutura é enviado um email notificando suas divergencias.
	 * Macado como assíncrono, pois se levantar uma exceção na busca de
	 * parametros ou no envio de email, não pode matar a transação.
	 */
	@Asynchronous
	public void verificarTabelasPacienteIguais() throws ApplicationBusinessException {
		PesquisaCamposPojoPorAnotacoes lista1 = new PesquisaCamposPojoPorAnotacoes();
		PesquisaCamposPojoPorAnotacoes lista2 = new PesquisaCamposPojoPorAnotacoes();

		lista1.pesquisarAnotacoes(AipPacientes.class);
		List<String> campos1List = lista1.getCamposList();

		lista2.pesquisarAnotacoes(AipPacientesHist.class);
		List<String> campos2List = lista2.getCamposList();

		Boolean listasIguais = lista1.compararListas(campos1List, campos2List);

		if (!listasIguais) {
			// Texto usado no email enviado pelo Oracle (titulo)
			StringBuffer msgTitulo = new StringBuffer();
			msgTitulo.append("Tabelas AIP_PACIENTES e AIP_PACIENTES_HIST estão diferentes, pac=")
			.append(String.valueOf(lista1.obterNumeroElementos()))
			.append(" hist=").append(String.valueOf(lista1.obterNumeroElementos()));
		}
	}

	/**
	 * Atualização da Situação do Paciente. Método para aplicar regras da
	 * atualização da situação do paciente, que insere registros de histórico do
	 * paciente. Caso a nova situação do paciente seja 'E' ou 'H' os registros
	 * de paciente/endereço de paciente são removidos.
	 * 
	 * Método responsável por implementar a procedure BD RN_PACP_INS_HISTORIC.
	 * 
	 * ORADB: AIPK_PAC_ATU.RN_PACP_INS_HISTORIC
	 * 
	 * @param paciente
	 * @param usuarioLogado 
	 * @throws ApplicationBusinessException
	 */
	public void inserirHistoricoPaciente(AipPacientes paciente, String usuarioLogado)
			throws ApplicationBusinessException {
		if (paciente == null) {
			return;
		} else {
			this.persistirPacienteHistoricoDePaciente(paciente);
		}
	}

	/**
	 * Apaga um paciente do banco de dados. Deve ser invocado pelo método
	 * excluirProntuario, que realiza todas as validações pertinentes antes da
	 * exclusão.
	 * 
	 * @param paciente
	 *            a ser removido.
	 * @throws ApplicationBusinessException
	 */
	public void excluirPaciente(AipPacientes paciente, String usuarioLogado) throws ApplicationBusinessException {
		try {
			if(paciente == null){
				throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
			}
			
			this.gerarJournalDelete(paciente);

			this.enforcePacienteDelecao(paciente);

			List<AipEnderecosPacientes> listaEnderecos = this.getAipEnderecosPacientesDAO().obterEndPaciente(paciente.getCodigo());
			
			// Gera journal de delete para endereços do paciente
			if (listaEnderecos != null
					&& listaEnderecos.size() > 0) {

				// Somente gera o journal, pois o DELETE de paciente está com
				// anotação
				// DELETE_ORPHANS e removerá todos registros
				for (AipEnderecosPacientes endPac : listaEnderecos) {
					this.getEnderecoON().removerEnderecoPaciente(endPac);
				}
			}
//			this.getAmbulatorioFacade().excluirConsultas(new ArrayList<AacConsultas>(paciente.getAacConsultas()), null,getRegistroColaboradorFacade().obterServidorPorUsuario(usuarioLogado));
			
			/*
			 * TODO:Ativar esta linha em produção! FIXME:TESTAR ESSA LINHA EM
			 * PRODUÇÃO!
			 * 
			 * Não temos base de testes para o banco de dados de histórico
			 * referenciado pelo sinonimo publico AIP_PACIENTES_HISTOR, por isso
			 * esse teste deverá ser feito diretamente em produção.
			 * 
			 * Query q = this.entityManager.
			 * createNativeQuery("DELETE FROM AIP_PACIENTES_HISTOR WHERE CODIGO = "
			 * + paciente.getCodigo());
			 * 
			 * q.executeUpdate();
			 */
			removerConveniosSaudePaciente(paciente.getCodigo());
			// Remove paciente
			paciente = getAipPacientesDAO().obterPorChavePrimaria(paciente.getCodigo());
			Integer prontuario = paciente.getProntuario();
			this.getAipPacientesDAO().remover(paciente);
			if(prontuario != null) {
				AipPacienteProntuario aipPacienteProntuario = getAipPacienteProntuarioDAO().obterPorChavePrimaria(paciente.getProntuario());
				popularAipPacienteProntuarioJN(aipPacienteProntuario, usuarioLogado, DominioOperacoesJournal.DEL);
				getAipPacienteProntuarioDAO().remover(aipPacienteProntuario);			
			}
			this.getAipPacientesDAO().flush();
			
		} catch (InactiveModuleException ime){
			throw new ApplicationBusinessException(CadastroPacienteRNExceptionCode.MODULO_INATIVO);			
			
		} catch (PersistenceException ce) {
			logError("Exceção capturada: ", ce);
			if (ce.getCause() instanceof ConstraintViolationException) {
				/*
				 * ConstraintViolationException cve =
				 * (ConstraintViolationException) ce .getCause();
				 */

				throw new ApplicationBusinessException(CadastroPacienteRNExceptionCode.VIOLACAO_FK_PACIENTE, paciente.getNome());
				
			} else {
				throw new ApplicationBusinessException( CadastroPacienteRNExceptionCode.ERRO_EXCLUSAO_PACIENTE);
			}			
		} catch (Exception e) {
			if (e instanceof ApplicationBusinessException){
				throw new ApplicationBusinessException((ApplicationBusinessException)e);
			}else{
				logError(e.getMessage(), e);
				throw new ApplicationBusinessException(CadastroPacienteRNExceptionCode.ERRO_EXCLUSAO_PACIENTE);
			}	
		}
	}
	
	protected void removerConveniosSaudePaciente(Integer pacCodigo) {
		List<AipConveniosSaudePaciente> listaConvenios = aipConveniosSaudePacienteDAO.pesquisarConveniosPaciente(pacCodigo);
		for (AipConveniosSaudePaciente convenio : listaConvenios) {
			aipConveniosSaudePacienteDAO.remover(convenio);
		}
	}

	/**
	 * Método para efetuar liberação do prontuário e gerar o journal de DELETE
	 * do paciente.
	 * 
	 * ORADB Trigger AIPT_PAC_ARD
	 * 
	 * @param paciente
	 * @throws ApplicationBusinessException
	 */
	private void gerarJournalDelete(AipPacientes paciente)
			throws ApplicationBusinessException {

		if (paciente.getProntuario() != null
				&& paciente.getProntuario() < VALOR_MAXIMO_PRONTUARIO
				&& !DominioTipoProntuario.E.equals(paciente.getPrntAtivo())
				&& !DominioTipoProntuario.H.equals(paciente.getPrntAtivo())) {

			if ("F".equals(this.getPacienteFacade()
					.verificarLiberacaoProntuario(paciente.getProntuario()))) {

				throw new ApplicationBusinessException(
						CadastroPacienteRNExceptionCode.ERRO_LIBERACAO_PRONTUARIO,
						paciente.getProntuario().toString());

			}
		}

		// Gera journal para remoção do paciente
		this.getPacienteJournalON().observarAtualizacaoPaciente(paciente, null,
				DominioOperacoesJournal.DEL);
	}

	/**
	 * Método para receber um objeto AipPacientesHist e retornar um objeto
	 * AipPacientes (fazendo todas equivalencias internamente na ON - de/para)
	 */
	public void persistirPacienteHistoricoDePaciente(AipPacientes paciente)
			throws ApplicationBusinessException {
		// Atribuições para fazer o DE/PARA
		AipPacientesHist historicoPaciente = new AipPacientesHist();
		historicoPaciente.setCidade(paciente.getAipCidades());
		historicoPaciente.setNacionalidade(paciente.getAipNacionalidades());
		historicoPaciente.setOcupacao(paciente.getAipOcupacoes());
		historicoPaciente.setPacienteMae(paciente.getMaePaciente());
		historicoPaciente.setUnidadeFuncional(paciente.getUnidadeFuncional());
		historicoPaciente.setCadConfirmado(paciente.getCadConfirmado());
		historicoPaciente.setCodigo(paciente.getCodigo());
		historicoPaciente.setCor(paciente.getCor());
		historicoPaciente.setCpf(paciente.getCpf());
		historicoPaciente.setCriadoEm(paciente.getCriadoEm());
		historicoPaciente.setDddFoneRecado(paciente.getDddFoneRecado());
		historicoPaciente.setDddFoneResidencial(paciente
				.getDddFoneResidencial());
		historicoPaciente.setDtIdentificacao(paciente.getDtIdentificacao());
		historicoPaciente.setDtNascimento(paciente.getDtNascimento());
		historicoPaciente.setDtObito(paciente.getDtObito());
		historicoPaciente.setDtObitoExterno(paciente.getDtObitoExterno());
		historicoPaciente.setDtRecadastro(paciente.getDtRecadastro());
		historicoPaciente.setDtUltAlta(paciente.getDtUltAlta());
		historicoPaciente.setDtUltAltaHospDia(paciente.getDtUltAltaHospDia());
		historicoPaciente.setDtUltAtendHospDia(paciente.getDtUltAtendHospDia());
		historicoPaciente.setDtUltConsulta(paciente.getDtUltConsulta());
		historicoPaciente.setDtUltInternacao(paciente.getDtUltInternacao());
		historicoPaciente.setDtUltProcedimento(paciente.getDtUltProcedimento());
		historicoPaciente.setEstadoCivil(paciente.getEstadoCivil());
		historicoPaciente.setCentroCusto(paciente.getFccCentroCustosCadastro());
		historicoPaciente.setCentroCustoRecadastro(paciente
				.getFccCentroCustosRecadastro());
		historicoPaciente.setFoneRecado(paciente.getFoneRecado());
		historicoPaciente.setFoneResidencial(paciente.getFoneResidencial());
		historicoPaciente.setGrauInstrucao(paciente.getGrauInstrucao());
		historicoPaciente.setIndGeraProntuario(paciente.getIndGeraProntuario());
		historicoPaciente.setIndPacAgfa(paciente.getIndPacAgfa());
		historicoPaciente.setIndPacienteVip(paciente.getIndPacienteVip());
		historicoPaciente.setIndPacProtegido(paciente.getIndPacProtegido());
		historicoPaciente.setLeito(paciente.getLeito());
		historicoPaciente.setNaturalidade(paciente.getNaturalidade());
		historicoPaciente.setNome(paciente.getNome());
		historicoPaciente.setNomeMae(paciente.getNomeMae());
		historicoPaciente.setNomePai(paciente.getNomePai());
		historicoPaciente.setNroCartaoSaude(paciente.getNroCartaoSaude());
		historicoPaciente.setNumeroPis(paciente.getNumeroPis());
		historicoPaciente.setObservacao(paciente.getObservacao());
		historicoPaciente.setOrgaoEmisRg(paciente.getOrgaoEmisRg());
		historicoPaciente.setPrntAtivo(paciente.getPrntAtivo());
		historicoPaciente.setProntuario(paciente.getProntuario());
		historicoPaciente.setQuarto(paciente.getQuarto());
		historicoPaciente.setServidor(paciente.getRapServidoresCadastro());
		historicoPaciente.setServidorRecadastro(paciente
				.getRapServidoresRecadastro());
		historicoPaciente.setRegNascimento(paciente.getRegNascimento());
		historicoPaciente.setRg(paciente.getRg());
		historicoPaciente.setRecemNascido(paciente.getRecemNascido());
		historicoPaciente.setSexo(paciente.getSexo());

		if (paciente.getSexoBiologico() == null) {
			historicoPaciente.setSexoBiologico(paciente.getSexo());
		} else {
			historicoPaciente.setSexoBiologico(paciente.getSexoBiologico());
		}

		historicoPaciente.setTipoDataObito(paciente.getTipoDataObito());
		historicoPaciente.setUnidadeFuncional(paciente.getUnidadeFuncional());
		historicoPaciente.setVolumes(paciente.getVolumes());

		// Verifica se estrutura de AipPacientes e AipPacientesHist estão
		// iguais. Caso não estejam, envia um email de alerta.
		this.verificarTabelasPacienteIguais();

		this.getHistoricoPacienteFacade()
				.inserirPacienteHist(historicoPaciente);

		// Percorre os endereços do paciente e grava os mesmos no seu
		// histórico
		for (AipEnderecosPacientes enderecoPaciente : paciente.getEnderecos()) {
			AipEndPacientesHist enderecoPacienteHist = this
					.getHistoricoPacienteFacade()
					.converterEnderecoPacienteEmEndPacienteHist(
							enderecoPaciente);
			this.getHistoricoPacienteFacade().inserirEnderecoPacienteHist(
					enderecoPacienteHist);
		}

	}


	/**
	 * @ORADB Function AIPK_RN.RN_AIPC_VER_OBITO
	 */
	public Boolean rnAipcVerObito(Integer pacCodigo) throws ApplicationBusinessException{
		AipPacientes paciente = getPacienteFacade().obterAipPacientesPorChavePrimaria(pacCodigo);
		if(paciente == null) {
			throw new ApplicationBusinessException(CadastroPacienteRNExceptionCode.AIP_00013);	
		}
		if(paciente.getDtObito() == null) {
			return true;
		}
		return false;
	}
	

	/**
	 * @ORADB Function AIPK_ATP_RN.RN_ATPP_VER_PACIENTE
	 */
	public void rnAtppVerPaciente(Integer pacCodigo) throws ApplicationBusinessException {
		//TODO : Implementar ao migrar completamente o SUBSTITUIR PRONTUÁRIO
		//IF NOT aack_con_3_rn.v_subst_prontuario 
			if (!this.rnAipcVerObito(pacCodigo)) {
				throw new ApplicationBusinessException(CadastroPacienteRNExceptionCode.AIP_00120);
			}
	}

	public void persistir(AipAlturaPacientes alturaPaciente) throws ApplicationBusinessException {
		if(alturaPaciente.getId() == null || alturaPaciente.getId().getCriadoEm() == null) {
			this.preInserir(alturaPaciente);
			getAipAlturaPacientesDAO().persistir(alturaPaciente);
		}
		else {
			this.preAtualizar(alturaPaciente);
		}
	}

	
	/**
	 * @throws ApplicationBusinessException 
	 * @ORADB Trigger AIPT_ATP_BRI
	 */
	public void preInserir(AipAlturaPacientes alturaPaciente) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if(alturaPaciente.getId().getCriadoEm() == null) {
			alturaPaciente.getId().setCriadoEm(new Date());
		}
		this.rnAtppVerPaciente(alturaPaciente.getId().getPacCodigo());
		alturaPaciente.setRapServidor(servidorLogado);
	}

	/**
	 * @ORADB Trigger AIPT_ATP_BRU
	 */
	public void preAtualizar(AipAlturaPacientes alturaPaciente) throws ApplicationBusinessException {
		AipAlturaPacientes old = getAipAlturaPacientesDAO().obterOriginal(alturaPaciente);
		/* Não pode sofrer alterações */
		if(DominioMomento.O.equals(old.getIndMomento())) {
			//@ORADB AIPK_ATP_RN.RN_ATPP_VER_ALTERA
			throw new ApplicationBusinessException(CadastroPacienteRNExceptionCode.AIP_00192);
		}
	}


	/**
	 * Método que realiza as ações da trigger
	 * 
	 * ORADB Trigger AIPT_PAC_ARU.
	 * 
	 * @param paciente
	 * @return
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public void atualizarPacienteTrigger(AipPacientes paciente)
			throws ApplicationBusinessException {
		String vNome = null;
		Integer vProntuario = null;
		Date vDtNascimento = null;
		Date vDtIdentificacao = null;

		AipPacientes pacienteAnterior = obterPacienteAnterior(paciente
				.getCodigo());

		if (pacienteAnterior == null) {
			return;
		}

		if (!Objects.equals(paciente.getNome(), pacienteAnterior.getNome())
				|| !Objects.equals(paciente.getProntuario(),
						pacienteAnterior.getProntuario())
				|| (paciente.getDtNascimento().compareTo(
						pacienteAnterior.getDtNascimento()) != 0)
				|| !Objects.equals(paciente.getPrntAtivo(),
						pacienteAnterior.getPrntAtivo())
				|| !Objects.equals(paciente.getVolumes(),
						pacienteAnterior.getVolumes())) {

			if (!Objects.equals(paciente.getNome(),
					pacienteAnterior.getNome())
					&& pacienteAnterior.getProntuario() != null) {
				vNome = pacienteAnterior.getNome();
			} else {
				vNome = null;
			}
			if (!Objects.equals(paciente.getProntuario(),
					pacienteAnterior.getProntuario())
					|| (pacienteAnterior.getProntuario() == null && paciente
							.getProntuario() != null)) {

				vProntuario = pacienteAnterior.getProntuario();
			} else {
				vProntuario = null;
			}
			if (pacienteAnterior.getProntuario() == null
					&& paciente.getProntuario() != null) {
				vDtIdentificacao = pacienteAnterior.getDtIdentificacao();
			} else {
				vDtIdentificacao = null;
			}
			if (verificarDiferencaDatas(paciente.getDtNascimento(),
					pacienteAnterior.getDtNascimento())) {
				vDtNascimento = pacienteAnterior.getDtNascimento();
			} else {
				vDtNascimento = null;
			}

			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			// TODO Refatorar e utilizar este método posteriormente
			// this.pacienteJournalON.observarAtualizacaoPaciente(pacienteAnterior,
			// paciente, DominioOperacoesJournal.UPD);
			AipPacientesJn pacienteJn = BaseJournalFactory
					.getBaseJournal(DominioOperacoesJournal.UPD,
							AipPacientesJn.class, servidorLogado.getUsuario());

			pacienteJn.setCodigoPaciente(paciente.getCodigo());
			pacienteJn.setNomePaciente(vNome);
			pacienteJn.setProntuario(vProntuario);
			pacienteJn.setDtNascimento(vDtNascimento);

			if (pacienteAnterior.getRapServidoresCadastro() != null) {
				pacienteJn.setMatriculaServidorCadastro(pacienteAnterior
						.getRapServidoresCadastro().getId().getMatricula());
				pacienteJn.setVinCodigoServidorCadastro(pacienteAnterior
						.getRapServidoresCadastro().getId().getVinCodigo());
			}

			if (pacienteAnterior.getRapServidoresRecadastro() != null) {
				pacienteJn.setMatriculaServidorRecadastro(pacienteAnterior
						.getRapServidoresRecadastro().getId().getMatricula());
				pacienteJn.setVinCodigoServidorRecadastro(pacienteAnterior
						.getRapServidoresRecadastro().getId().getVinCodigo());
			}

			if (pacienteAnterior.getFccCentroCustosCadastro() != null) {
				pacienteJn.setCodigoCentroCustoCadastro(pacienteAnterior
						.getFccCentroCustosCadastro().getCodigo());
			}

			if (pacienteAnterior.getFccCentroCustosRecadastro() != null) {
				pacienteJn.setCodigoCentroCustoRecadastro(pacienteAnterior
						.getFccCentroCustosRecadastro().getCodigo());
			}

			pacienteJn.setDtIdentificacao(vDtIdentificacao);
			pacienteJn.setPrntAtivo(pacienteAnterior.getPrntAtivo());
			pacienteJn.setVolumes(pacienteAnterior.getVolumes());
			pacienteJn.setSexoBiologico(pacienteAnterior.getSexoBiologico());
			pacienteJn.setIndPacAgfa(pacienteAnterior.getIndPacAgfa());

			this.getAipPacientesJnDAO().persistir(pacienteJn);
		}

		if (pacienteAnterior.getProntuario() != null
				&& paciente.getProntuario() == null
				&& pacienteAnterior.getProntuario() < VALOR_MAXIMO_PRONTUARIO) {

			if (!liberarProntuario(pacienteAnterior.getProntuario())) {
				throw new ApplicationBusinessException(
						CadastroPacienteRNExceptionCode.ERRO_LIBERACAO_PRONTUARIO,
						pacienteAnterior.getProntuario());
			}
		}

		if (pacienteAnterior.getProntuario() == null
				&& paciente.getProntuario() != null
				&& paciente.getProntuario() < VALOR_MAXIMO_PRONTUARIO) {
			atualizarProntuarioPessoasFisicas(paciente);
		}

		if (!Objects.equals(paciente.getNome(), pacienteAnterior.getNome())) {
			alterarNomePaciente(paciente.getProntuario(), paciente.getNome());
		}

	}

	/**
	 * 
	 * Verifica as datas de obito.
	 * 
	 * ORADB Parte de TRIGGER AIPT_PAC_BRU da tabela AIP_PACIENTES.
	 * 
	 * @param dataObito
	 *            , paciente
	 * @throws ApplicationBusinessException
	 */
	public void validarDataObitoTrigger(Date dataObito, AipPacientes paciente) {

		// Se foi INFORMADO O ÓBITO do paciente cancela as interconsultas
		// pendentes.
		if (paciente.getDtObito() == null && dataObito != null) {
			this.atualizarInterconsultas(paciente.getCodigo(), 'O');
		}

		// Se foi RETIRADO O ÓBITO do paciente libera as interconsultas
		// pendentes.
		if (paciente.getDtObito() != null && dataObito == null) {
			this.atualizarInterconsultas(paciente.getCodigo(), 'L');
		}
	}

	/**
	 * Libera ou Cancela as consultas interdependentes.
	 * 
	 * @param pacCodigo
	 * @param tipo
	 */
	public void atualizarInterconsultas(final Integer pacCodigo,
			final Character tipo) {
		this.getAmbulatorioFacade().atualizarInterconsultas(pacCodigo, tipo);
	}

	/**
	 * TODO Este método deverá ficar centralizado Método para fazer comparação
	 * entre valores atuais e anteriores para atributos do tipo Data, evitando
	 * nullpointer.
	 * 
	 * @param data1
	 * @param data2
	 * @return FALSE se parametros recebido forem iguais / TRUE se parametros
	 *         recebidos forem diferentes
	 */
	public boolean verificarDiferencaDatas(Date data1, Date data2) {
		if (data1 == null && data2 == null) {
			return false;
		} else if (data1 == null && data2 != null) {
			return true;
		} else if (data2 == null && data1 != null) {
			return true;
		} else if (data1.getTime() == data2.getTime()) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Método que realiza as ações da trigger
	 * 
	 * ORADB procedure (Schema CONV) ffc_altera_nome_pac
	 * 
	 * @param prontuario
	 *            , nome
	 * @return
	 */
	public void alterarNomePaciente(Integer prontuario, String nome) {
		List<PacIntdConv> listaPacIntdConv = obterListaPacIntdConvPorProntuario(prontuario);
		if (listaPacIntdConv.size() > 0) {
			PacIntdConvDAO pacIntdConvDAO = this.getPacIntdConvDAO();

			for (PacIntdConv pacIntdConv : listaPacIntdConv) {
				pacIntdConv.setNomePac(nome);
				pacIntdConvDAO.persistir(pacIntdConv);
			}

			pacIntdConvDAO.flush();
		}
	}

	/**
	 * Método que obtem a lista de aPacIntdConvs pelo prontuário
	 * 
	 * @param prontuario
	 * @return
	 */
	public List<PacIntdConv> obterListaPacIntdConvPorProntuario(
			Integer prontuario) {
		return this.getPacIntdConvDAO().obterListaPacIntdConvPorProntuario(
				prontuario);
	}

	/**
	 * Método que realiza as ações da trigger
	 * 
	 * ORADB aipk_pac_atu.rn_pacp_atu_prnt_ser
	 * 
	 * @param paciente
	 * @return
	 */
	public void atualizarProntuarioPessoasFisicas(AipPacientes paciente) {
		List<RapPessoasFisicas> listaPessoasFisicas = obterPessoaFisicaPorCodigoPaciente(paciente
				.getCodigo());
		if (listaPessoasFisicas.size() > 0) {
			IRegistroColaboradorFacade registroColaboradorFacade = this
					.getRegistroColaboradorFacade();

			for (RapPessoasFisicas pessoaFisica : listaPessoasFisicas) {
				pessoaFisica.setPacProntuario(paciente.getProntuario());
				registroColaboradorFacade.persistirRapPessoasFisicas(pessoaFisica);
			}
			this.flush();
		}
	}

	/**
	 * Método que obtem a lista de pessoas físicas pelo código do paciente
	 * 
	 * @param pacCodigo
	 * @return
	 */
	public List<RapPessoasFisicas> obterPessoaFisicaPorCodigoPaciente(
			Integer pacCodigo) {
		return this.getRegistroColaboradorFacade()
				.obterPessoaFisicaPorCodigoPaciente(pacCodigo);
	}

	/**
	 * Método que realiza as ações da function
	 * 
	 * ORADB AIPC_LIB_PRONTUARIO.
	 * Modificado para somente inserir o prontuário na tabela de liberados
	 * caso o parâmetro P_AGHU_PERMITE_REUSO_PRONTUARIO tenha valor 'S'
	 * 
	 * @param paciente
	 * @return
	 * @throws ApplicationBusinessException 
	 */
	public boolean liberarProntuario(Integer prontuario) throws ApplicationBusinessException {

		AghParametrosVO aghParametroVO = new AghParametrosVO();
		aghParametroVO.setNome(AghuParametrosEnum.P_AGHU_PERMITE_REUSO_PRONTUARIO.toString());
		this.getParametroFacade().getAghpParametro(aghParametroVO);
		boolean permiteReusoProntuario = (aghParametroVO.getVlrTexto() != null && "S"
				.equalsIgnoreCase(aghParametroVO.getVlrTexto()));
		
		Integer countLiberados = obterNumeroProntuariosLiberados(prontuario);
		if (countLiberados == 0) {
			if (permiteReusoProntuario){
				AipProntuarioLiberados prontuarioLiberado = new AipProntuarioLiberados();
				prontuarioLiberado.setProntuario(prontuario);
				this.getAipProntuarioLiberadosDAO().persistir(prontuarioLiberado);				
			}
			return true;
		} else {
			return false;
		}			
		
	}

	/**
	 * Método que obtem a lista de prontuários liberados pelo número
	 * 
	 * @param prontuario
	 * @return
	 */
	public Integer obterNumeroProntuariosLiberados(Integer prontuario) {
		return this.getAipProntuarioLiberadosDAO()
				.obterNumeroProntuariosLiberados(prontuario);
	}

	/**
	 * Método que obtem o nome do paciente salvo no banco
	 * 
	 * @param codigoPaciente
	 * @return
	 */
	public String obterNomeAnterior(Integer codigoPaciente) {
		return this.getAipPacientesDAO().obterNomeAnterior(codigoPaciente);
	}

	/**
	 * Método que obtem o nome da mãe do paciente salvo no banco
	 * 
	 * @param codigoPaciente
	 * @return
	 */
	public String obterNomeMaeAnterior(Integer codigoPaciente) {
		return this.getAipPacientesDAO().obterNomeMaeAnterior(codigoPaciente);
	}

	/**
	 * Método que obtem a data de nascimento do paciente salvo no banco
	 * 
	 * @param codigoPaciente
	 * @return
	 */
	public Date obterDataNascimentoAnterior(Integer codigoPaciente) {
		return this.getAipPacientesDAO().obterDataNascimentoAnterior(
				codigoPaciente);
	}

	/**
	 * Método que obtem o prontuário do paciente salvo no banco
	 * 
	 * @param codigoPaciente
	 * @return
	 */
	public Integer obterProntarioAnterior(Integer codigoPaciente) {
		return this.getAipPacientesDAO().obterProntarioAnterior(codigoPaciente);
	}

	/**
	 * Método que obtem o prntAtivo do paciente salvo no banco
	 * 
	 * @param codigoPaciente
	 * @return
	 */
	public DominioTipoProntuario obterPrntAtivoAnterior(Integer codigoPaciente) {
		return this.getAipPacientesDAO().obterPrntAtivoAnterior(codigoPaciente);
	}

	/**
	 * Método que obtem uma lista de Atendimentos pelo código do paciente
	 * 
	 * @param codigoPaciente
	 *            , indPacAtendimento
	 * @return
	 */
	public List<AghAtendimentos> pesquisarAtendimentosPorPaciente(
			Integer codigoPaciente) {
		return this.getAghuFacade().pesquisarAtendimentosPorPaciente(
				codigoPaciente);
	}


	/**
	 * Método usado para buscar no BD as informações necessárias para a geração
	 * de journal de pacientes. Essa consulta busca as informações no cache de
	 * 1º nível do hibernate (por causa do uso de projections), buscando os
	 * valores anteriores a qualquer operação de persist sobre o objeto de
	 * paciente.
	 * 
	 * @param codigo
	 * @return
	 */
	public AipPacientes obterPacienteAnterior(Integer codigo) {
		Object[] retornoConsulta = (Object[]) this.getAipPacientesDAO()
				.obterDadosPacienteAnterior(codigo);

		AipPacientes paciente = null;
		if (retornoConsulta != null) {
			paciente = new AipPacientes();

			paciente.setCodigo((Integer) retornoConsulta[0]);
			paciente.setNome((String) retornoConsulta[1]);
			paciente.setProntuario((Integer) retornoConsulta[2]);
			paciente.setDtNascimento((Date) retornoConsulta[3]);
			paciente.setPrntAtivo((DominioTipoProntuario) retornoConsulta[4]);
			paciente.setVolumes((Short) retornoConsulta[5]);
			paciente.setDtIdentificacao((Date) retornoConsulta[6]);

			if (retornoConsulta[7] != null && retornoConsulta[8] != null) {
				RapServidoresId servidorCadastroId = new RapServidoresId();
				RapServidores servidorCadastro = new RapServidores();
				servidorCadastro.setId(servidorCadastroId);
				servidorCadastroId.setMatricula((Integer) retornoConsulta[7]);
				servidorCadastroId.setVinCodigo((Short) retornoConsulta[8]);
				paciente.setRapServidoresCadastro(this
						.getRegistroColaboradorFacade().obterServidor(
								servidorCadastro));
			} else {
				paciente.setRapServidoresCadastro(null);
			}

			if (retornoConsulta[9] != null && retornoConsulta[10] != null) {
				RapServidoresId servidorRecadastroId = new RapServidoresId();
				RapServidores servidorRecadastro = new RapServidores();
				servidorRecadastro.setId(servidorRecadastroId);
				servidorRecadastroId.setMatricula((Integer) retornoConsulta[9]);
				servidorRecadastroId.setVinCodigo((Short) retornoConsulta[10]);
				paciente.setRapServidoresRecadastro(this
						.getRegistroColaboradorFacade().obterServidor(
								servidorRecadastro));
			} else {
				paciente.setRapServidoresRecadastro(null);
			}

			ICentroCustoFacade centroCustoFacade = this.getCentroCustoFacade();

			if (retornoConsulta[11] != null) {
				paciente.setFccCentroCustosCadastro(centroCustoFacade
						.obterCentroCustoPorChavePrimaria((Integer) retornoConsulta[11]));
			}

			if (retornoConsulta[12] != null) {
				paciente.setFccCentroCustosRecadastro(centroCustoFacade
						.obterCentroCustoPorChavePrimaria((Integer) retornoConsulta[12]));
			}

			paciente.setSexoBiologico((DominioSexo) retornoConsulta[13]);
			paciente.setIndPacAgfa((DominioSimNao) retornoConsulta[14]);
			paciente.setNomeMae((String) retornoConsulta[15]);
			if (retornoConsulta[16] != null) {
				paciente.setAipCidades(this.getAipCidadesDAO()
						.obterPorChavePrimaria((Integer) retornoConsulta[16]));
			}
			if (retornoConsulta[17] != null) {
				paciente.setAipNacionalidades(this.getAipNacionalidadesDAO()
						.obterPorChavePrimaria((Integer) retornoConsulta[17]));
			}
			if (retornoConsulta[18] != null) {
				paciente.setAipOcupacoes(this.getAipOcupacoesDAO()
						.obterPorChavePrimaria((Integer) retornoConsulta[18]));
			}
			if (retornoConsulta[19] != null) {
				paciente.setAipUfs(this.getAipUfsDAO().obterPorChavePrimaria(
						(String) retornoConsulta[19]));
			}

			paciente.setCor((DominioCor) retornoConsulta[20]);
			paciente.setSexo((DominioSexo) retornoConsulta[21]);
			paciente.setGrauInstrucao((DominioGrauInstrucao) retornoConsulta[22]);
			paciente.setNomePai((String) retornoConsulta[23]);
			paciente.setNaturalidade((String) retornoConsulta[24]);
			paciente.setDddFoneResidencial((Short) retornoConsulta[25]);
			paciente.setFoneResidencial((Long) retornoConsulta[26]);
			paciente.setDddFoneRecado((Short) retornoConsulta[27]);
			paciente.setFoneRecado((String) retornoConsulta[28]);
			paciente.setEstadoCivil((DominioEstadoCivil) retornoConsulta[29]);
			paciente.setCpf((Long) retornoConsulta[30]);
			paciente.setRg((String) retornoConsulta[31]);
			paciente.setOrgaoEmisRg((String) retornoConsulta[32]);
			paciente.setObservacao((String) retornoConsulta[33]);
			paciente.setRegNascimento((String) retornoConsulta[34]);
			paciente.setNroCartaoSaude((BigInteger) retornoConsulta[35]);
			paciente.setNumeroPis((Long) retornoConsulta[36]);
			paciente.setIndPacAgfa((DominioSimNao) retornoConsulta[37]);
			paciente.setIndGeraProntuario((DominioSimNao) retornoConsulta[38]);
		}

		return paciente;
	}

	/**
	 * Método usado para comparar o paciente atual com o paciente anterior salvo
	 * no banco e indicar se houve alteração ou não em seus atributos.
	 * 
	 * @param pacienteAtual
	 *            , pacienteAnterior
	 * @return boolean
	 */
	public boolean pacienteModificado(AipPacientes pacienteAtual,
			AipPacientes pacienteAnterior) {

		if (pacienteAtual == null || pacienteAnterior == null) {
			return false;
		}

		if (!Objects.equals(pacienteAtual.getNome(),
				pacienteAnterior.getNome())
				|| !Objects.equals(pacienteAtual.getNomeMae(),
						pacienteAnterior.getNomeMae())
				|| verificarDiferencaDatas(pacienteAtual.getDtNascimento(),
						pacienteAnterior.getDtNascimento())
				|| (pacienteAtual.getAipCidades() != null && !pacienteAtual
						.getAipCidades().equals(
								pacienteAnterior.getAipCidades()))
				|| (pacienteAtual.getAipNacionalidades() != null && !pacienteAtual
						.getAipNacionalidades().equals(
								pacienteAnterior.getAipNacionalidades()))
				|| (pacienteAtual.getAipOcupacoes() != null && !pacienteAtual
						.getAipOcupacoes().equals(
								pacienteAnterior.getAipOcupacoes()))
				|| (pacienteAtual.getAipUfs() != null && !pacienteAtual
						.getAipUfs().equals(pacienteAnterior.getAipUfs()))
				|| !Objects.equals(pacienteAtual.getCor(),
						pacienteAnterior.getCor())
				|| !Objects.equals(pacienteAtual.getSexo(),
						pacienteAnterior.getSexo())
				|| !Objects.equals(pacienteAtual.getGrauInstrucao(),
						pacienteAnterior.getGrauInstrucao())
				|| !Objects.equals(pacienteAtual.getNomePai(),
						pacienteAnterior.getNomePai())
				|| !Objects.equals(pacienteAtual.getNaturalidade(),
						pacienteAnterior.getNaturalidade())
				|| !Objects.equals(pacienteAtual.getDddFoneResidencial(),
						pacienteAnterior.getDddFoneResidencial())
				|| !Objects.equals(pacienteAtual.getFoneResidencial(),
						pacienteAnterior.getFoneResidencial())
				|| !Objects.equals(pacienteAtual.getDddFoneRecado(),
						pacienteAnterior.getDddFoneRecado())
				|| !Objects.equals(pacienteAtual.getFoneRecado(),
						pacienteAnterior.getFoneRecado())
				|| !Objects.equals(pacienteAtual.getEstadoCivil(),
						pacienteAnterior.getEstadoCivil())
				|| !Objects.equals(pacienteAtual.getCpf(),
						pacienteAnterior.getCpf())
				|| !Objects.equals(pacienteAtual.getRg(),
						pacienteAnterior.getRg())
				|| !Objects.equals(pacienteAtual.getOrgaoEmisRg(),
						pacienteAnterior.getOrgaoEmisRg())
				|| !Objects.equals(pacienteAtual.getObservacao(),
						pacienteAnterior.getObservacao())
				|| !Objects.equals(pacienteAtual.getRegNascimento(),
						pacienteAnterior.getRegNascimento())
				|| !Objects.equals(pacienteAtual.getNroCartaoSaude(),
						pacienteAnterior.getNroCartaoSaude())
				|| !Objects.equals(pacienteAtual.getNumeroPis(),
						pacienteAnterior.getNumeroPis())
				|| !Objects.equals(pacienteAtual.getSexoBiologico(),
						pacienteAnterior.getSexoBiologico())
				|| !Objects.equals(pacienteAtual.getIndPacAgfa(),
						pacienteAnterior.getIndPacAgfa())) {

			return true;

		}

		if (isEnderecosModificados(pacienteAtual)) {
			return true;
		}

		return false;
	}

	private boolean isEnderecosModificados(AipPacientes paciente) {
		Long quantidadeEnderecosPacienteBanco = aipEnderecosPacientesDAO.buscaQuantidadeEnderecosPaciente(paciente);

		if (quantidadeEnderecosPacienteBanco != Long.valueOf(paciente.getEnderecos().size())) {
			return true;
		}

		boolean retorno = false;
		for (AipEnderecosPacientes enderecoAtual : paciente.getEnderecos()) {
			AipEnderecosPacientes enderecoBanco = enderecoON.obterEnderecoBanco(enderecoAtual.getId());

			retorno = isEnderecoModificado(enderecoAtual, enderecoBanco);
		}

		return retorno;
	}

	public boolean isEnderecoModificado(AipEnderecosPacientes enderecoAtual,
			AipEnderecosPacientes enderecoBanco) {
		boolean retorno = false;
		if (enderecoBanco == null || 
				!Objects.equals(enderecoAtual.getAipBairrosCepLogradouro(),enderecoBanco.getAipBairrosCepLogradouro()) || 
				!Objects.equals(enderecoAtual.getAipCidade(),enderecoBanco.getAipCidade()) || 
				!Objects.equals(enderecoAtual.getAipUf(), enderecoBanco.getAipUf()) || 
				!Objects.equals(enderecoAtual.getBairro(), enderecoBanco.getBairro()) || 
				!Objects.equals(enderecoAtual.getCep(), enderecoBanco.getCep()) || 
				!Objects.equals(enderecoAtual.getCidade(), enderecoBanco.getCidade()) || 
				!Objects.equals(enderecoAtual.getComplLogradouro(),enderecoBanco.getComplLogradouro()) || 
				!Objects.equals(enderecoAtual.getIndPadrao(), enderecoBanco.getIndPadrao()) || 
				!Objects.equals(enderecoAtual.getLogradouro(), enderecoBanco.getLogradouro()) || 
				!Objects.equals(enderecoAtual.getNroLogradouro(), enderecoBanco.getNroLogradouro()) || 
				!Objects.equals(enderecoAtual.getTipoEndereco(), enderecoBanco.getTipoEndereco())) {

			retorno = true;
		}
		return retorno;
	}

	/**
	 * Método para receber um objeto AipPacientesHist e retornar um objeto
	 * AipPacientes (fazendo todas equivalencias internamente na ON - de/para)
	 */
	public void persistirPacienteDeHistoricoPaciente(
			AipPacientesHist historicoPaciente) throws ApplicationBusinessException {

		// Atribuições para fazer o DE/PARA
		AipPacientes paciente = new AipPacientes();
		paciente.setAipCidades(historicoPaciente.getCidade());
		paciente.setAipNacionalidades(historicoPaciente.getNacionalidade());
		paciente.setAipOcupacoes(historicoPaciente.getOcupacao());
		paciente.setMaePaciente(historicoPaciente.getPacienteMae());
		paciente.setAipUfs(historicoPaciente.getUf());
		paciente.setCadConfirmado(historicoPaciente.getCadConfirmado());
		paciente.setCodigo(historicoPaciente.getCodigo());
		paciente.setCor(historicoPaciente.getCor());
		paciente.setCpf(historicoPaciente.getCpf());
		paciente.setCriadoEm(historicoPaciente.getCriadoEm());
		paciente.setDddFoneRecado(historicoPaciente.getDddFoneRecado());
		paciente.setDddFoneResidencial(historicoPaciente
				.getDddFoneResidencial());
		paciente.setDtIdentificacao(historicoPaciente.getDtIdentificacao());
		paciente.setDtNascimento(historicoPaciente.getDtNascimento());
		paciente.setDtObito(historicoPaciente.getDtObito());
		paciente.setDtObitoExterno(historicoPaciente.getDtObitoExterno());
		paciente.setDtRecadastro(historicoPaciente.getDtRecadastro());
		paciente.setDtUltAlta(historicoPaciente.getDtUltAlta());
		paciente.setDtUltAltaHospDia(historicoPaciente.getDtUltAltaHospDia());
		paciente.setDtUltAtendHospDia(historicoPaciente.getDtUltAtendHospDia());
		paciente.setDtUltConsulta(historicoPaciente.getDtUltConsulta());
		paciente.setDtUltInternacao(historicoPaciente.getDtUltInternacao());
		paciente.setDtUltProcedimento(historicoPaciente.getDtUltProcedimento());
		paciente.setEstadoCivil(historicoPaciente.getEstadoCivil());
		paciente.setFccCentroCustosCadastro(historicoPaciente.getCentroCusto());
		paciente.setFccCentroCustosRecadastro(historicoPaciente
				.getCentroCustoRecadastro());
		paciente.setFoneRecado(historicoPaciente.getFoneRecado());
		paciente.setFoneResidencial(historicoPaciente.getFoneResidencial());
		paciente.setGrauInstrucao(historicoPaciente.getGrauInstrucao());
		paciente.setIndGeraProntuario(historicoPaciente.getIndGeraProntuario());
		paciente.setIndPacAgfa(historicoPaciente.getIndPacAgfa());
		paciente.setIndPacienteVip(historicoPaciente.getIndPacienteVip());
		paciente.setIndPacProtegido(historicoPaciente.getIndPacProtegido());
		paciente.setLeito(historicoPaciente.getLeito());
		paciente.setNaturalidade(historicoPaciente.getNaturalidade());
		paciente.setNome(historicoPaciente.getNome());
		paciente.setNomeMae(historicoPaciente.getNomeMae());
		paciente.setNomePai(historicoPaciente.getNomePai());
		paciente.setNroCartaoSaude(historicoPaciente.getNroCartaoSaude());
		paciente.setNumeroPis(historicoPaciente.getNumeroPis());
		paciente.setObservacao(historicoPaciente.getObservacao());
		paciente.setOrgaoEmisRg(historicoPaciente.getOrgaoEmisRg());
		paciente.setPrntAtivo(historicoPaciente.getPrntAtivo());
		paciente.setProntuario(historicoPaciente.getProntuario());
		paciente.setQuarto(historicoPaciente.getQuarto());
		paciente.setRapServidoresCadastro(historicoPaciente.getServidor());
		paciente.setRapServidoresRecadastro(historicoPaciente
				.getServidorRecadastro());
		paciente.setRegNascimento(historicoPaciente.getRegNascimento());
		paciente.setRg(historicoPaciente.getRg());
		paciente.setRecemNascido(historicoPaciente.getRecemNascido());
		paciente.setSexo(historicoPaciente.getSexo());

		if (historicoPaciente.getSexoBiologico() == null) {
			paciente.setSexoBiologico(historicoPaciente.getSexo());
		} else {
			paciente.setSexoBiologico(historicoPaciente.getSexoBiologico());
		}

		paciente.setTipoDataObito(historicoPaciente.getTipoDataObito());
		paciente.setUnidadeFuncional(historicoPaciente.getUnidadeFuncional());
		paciente.setVolumes(historicoPaciente.getVolumes());

		// Verifica se estrutura de AipPacientes e AipPacientesHist estão
		// iguais. Caso não estejam, envia um email de alerta.
		this.verificarTabelasPacienteIguais();

		// Busca todos historicos de endereco do
		List<AipEndPacientesHist> historicoEndPacList = this
				.getHistoricoPacienteFacade()
				.pesquisarHistoricoEnderecoPaciente(
						historicoPaciente.getCodigo());
		for (AipEndPacientesHist aipEndPacientesHist : historicoEndPacList) {
			AipEnderecosPacientes endPac = this.getHistoricoPacienteFacade()
					.converterEnderecoPacienteHistEmEndPaciente(
							aipEndPacientesHist);
			endPac.setAipPaciente(paciente);
			paciente.getEnderecos().add(endPac);
		}

		this.persistirPacienteFonemas(paciente);
	}

	/**
	 * Méotodo para salvar o paciente e incluir seus fonemas.
	 * 
	 * @param paciente
	 */
	public void persistirPacienteFonemas(AipPacientes paciente)
			throws ApplicationBusinessException {
		this.getAipPacientesDAO().persistir(paciente);
		this.getAipPacientesDAO().flush();

		this.salvarFonemas(paciente, paciente.getNome(),
				AipFonemaPacientes.class);

		if (!StringUtils.isBlank(paciente.getNomeMae())) {
			this.salvarFonemas(paciente, paciente.getNomeMae(),AipFonemasMaePaciente.class);
		}
		if (!StringUtils.isBlank(paciente.getNomeSocial())) {
			this.salvarFonemas(paciente, paciente.getNomeSocial(), AipFonemaNomeSocialPacientes.class);
		}
	}

	/**
	 * Salva os fonemas gerados para o nome paciente e/ou nome da mãe.
	 * 
	 * @param aipPaciente  paciente.
	 * @param nome  nome do paciente a ser fonetizado.
	 * @param tipo tipo dos fonemas a serem persistidos (P - nome do paciente, M
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public void salvarFonemas(AipPacientes paciente, String nome, Class clazz) throws ApplicationBusinessException {
		if (paciente == null || nome == null || clazz == null) {
			throw new IllegalArgumentException("Parâmetros inválidos.");
		}

		List<String> fonemas = this.getPacienteFacade().obterFonemasNaOrdem(nome);

		Set<AipFonemas> aipFonemas = salvarAipFonemas(new HashSet<String>(fonemas), clazz);

		if (clazz.equals(AipFonemaPacientes.class)) {
			List<AipPosicaoFonemasPacientes> posicoesFonemas = aipPosicaoFonemasPacientesDAO.listarPosicoesFonemaPaciente(paciente.getCodigo());
			
			for (AipPosicaoFonemasPacientes posicaoFonema : posicoesFonemas) {
				aipPosicaoFonemasPacientesDAO.remover(posicaoFonema);
			}
			aipPosicaoFonemasPacientesDAO.flush();

			for (AipPosicaoFonemasPacientes posicaoFonema : posicoesFonemas) {
				aipPosicaoFonemasPacientesDAO.desatachar(posicaoFonema);
			}

			for (AipFonemas aipFonema : aipFonemas) {
				AipFonemaPacientes aipFonemaPaciente = aipFonemaPacientesDAO
						.obterAipFonemaPacientes(paciente.getCodigo(),
								aipFonema.getFonema());

				if (aipFonemaPaciente == null) {
					aipFonemaPaciente = incluirFonemaPaciente(paciente,
							aipFonema);
				}

				if (aipFonemaPaciente.getPosicaoFonemas() == null) {
					aipFonemaPaciente
							.setPosicaoFonemas(new HashSet<AipPosicaoFonemasPacientes>());
				}

				for (int i = 0; i < fonemas.size(); i++) {
					if (fonemas.get(i).equals(aipFonema.getFonema())) {
						AipPosicaoFonemasPacientes posicaoFonema = new AipPosicaoFonemasPacientes(
								new AipPosicaoFonemasPacientesId(
										aipFonemaPaciente.getSeq(),
										(byte) (i + 1)), aipFonemaPaciente);

						aipPosicaoFonemasPacientesDAO.persistir(posicaoFonema);

						aipFonemaPaciente.getPosicaoFonemas()
								.add(posicaoFonema);
					}
				}
			}
		} else if (clazz.equals(AipFonemaNomeSocialPacientes.class)) {
			List<AipPosicaoFonemasNomeSocialPacientes> posicoesFonemas = aipPosicaoFonemasNomeSocialPacientesDAO.
					listarPosicoesFonemaPaciente(paciente.getCodigo());
			for (AipPosicaoFonemasNomeSocialPacientes posicaoFonema : posicoesFonemas) {
				aipPosicaoFonemasNomeSocialPacientesDAO.remover(posicaoFonema);
				aipPosicaoFonemasNomeSocialPacientesDAO.flush();
			}

			aipPosicaoFonemasMaePacienteDAO.flush();

			for (AipPosicaoFonemasNomeSocialPacientes posicaoFonema : posicoesFonemas) {
				aipPosicaoFonemasNomeSocialPacientesDAO.desatachar(posicaoFonema);
			}

			for (AipFonemas aipFonema : aipFonemas) {
				AipFonemaNomeSocialPacientes posicaoFonemasNomeSocial = 
						aipFonemaNomeSocialPacientesDAO.obterAipFonemaNomeSocialPacientes(paciente.getCodigo(), aipFonema.getFonema());

				if (posicaoFonemasNomeSocial == null) {
					posicaoFonemasNomeSocial = incluirFonemaNomeSocialPaciente(paciente, aipFonema);
				}

				if (posicaoFonemasNomeSocial.getPosicaoFonemas() == null) {
					posicaoFonemasNomeSocial.setPosicaoFonemas(new HashSet<AipPosicaoFonemasNomeSocialPacientes>());
				}

				for (int i = 0; i < fonemas.size(); i++) {
					if (fonemas.get(i).equals(aipFonema.getFonema())) {
						AipPosicaoFonemasNomeSocialPacientes posicaoFonema = new
							AipPosicaoFonemasNomeSocialPacientes( new AipPosicaoFonemasNomeSocialPacientesId(posicaoFonemasNomeSocial.getSeq(), 
									(byte) (i + 1)),  posicaoFonemasNomeSocial);

						aipPosicaoFonemasNomeSocialPacientesDAO.persistir(posicaoFonema);
						posicaoFonemasNomeSocial.getPosicaoFonemas().add(posicaoFonema);
					}
				}
			}
		} else {
			List<AipPosicaoFonemasMaePaciente> posicoesFonemas = aipPosicaoFonemasMaePacienteDAO
					.listarPosicoesFonemaMaePaciente(paciente.getCodigo());
			for (AipPosicaoFonemasMaePaciente posicaoFonema : posicoesFonemas) {
				aipPosicaoFonemasMaePacienteDAO.remover(posicaoFonema);
			}
			aipPosicaoFonemasMaePacienteDAO.flush();

			for (AipPosicaoFonemasMaePaciente posicaoFonema : posicoesFonemas) {
				aipPosicaoFonemasMaePacienteDAO.desatachar(posicaoFonema);
				
			}

			for (AipFonemas aipFonema : aipFonemas) {
				AipFonemasMaePaciente aipFonemaMaePaciente = aipFonemasMaePacienteDAO
						.obterAipFonemasMaePaciente(paciente.getCodigo(),
								aipFonema.getFonema());

				if (aipFonemaMaePaciente == null) {
					aipFonemaMaePaciente = incluirFonemaMaePaciente(paciente,
							aipFonema);
				}

				if (aipFonemaMaePaciente.getPosicaoFonemas() == null) {
					aipFonemaMaePaciente.setPosicaoFonemas(new

					HashSet<AipPosicaoFonemasMaePaciente>());
				}

				for (int i = 0; i < fonemas.size(); i++) {
					if (fonemas.get(i).equals(aipFonema.getFonema())) {
						AipPosicaoFonemasMaePaciente posicaoFonema = new

						AipPosicaoFonemasMaePaciente(
								new AipPosicaoFonemasMaePacienteId(
										aipFonemaMaePaciente.getSeq(),
										(byte) (i + 1)),

								aipFonemaMaePaciente);

						aipPosicaoFonemasMaePacienteDAO
								.persistir(posicaoFonema);

						aipFonemaMaePaciente.getPosicaoFonemas().add(
								posicaoFonema);
					}
				}
			}
		}

		aipPosicaoFonemasNomeSocialPacientesDAO.flush();
	}

	/**
	 * Inclui um fonema novo para o nome paciente.
	 * 
	 * @param aipPaciente
	 *            paciente.
	 * @param aipFonemas
	 *            aipFonemas.
	 */
	private AipFonemaPacientes incluirFonemaPaciente(AipPacientes aipPaciente,
			AipFonemas aipFonemas) {
		AipFonemaPacientes aipFonemaPacientes = new AipFonemaPacientes();

		aipFonemaPacientes.setAipPaciente(aipPaciente);
		aipFonemaPacientes.setAipFonemas(aipFonemas);

		this.getAipFonemaPacientesDAO().persistir(aipFonemaPacientes);

		return aipFonemaPacientes;
	}

	/**
	 * Inclui um fonema novo para o nome da mãe.
	 * 
	 * @param aipPaciente
	 *            paciente.
	 * @param aipFonemas
	 *            aipFonemas.
	 */
	private AipFonemasMaePaciente incluirFonemaMaePaciente(
			AipPacientes aipPaciente, AipFonemas aipFonemas) {
		AipFonemasMaePaciente aipFonemaMaePaciente = new AipFonemasMaePaciente();

		aipFonemaMaePaciente.setAipPaciente(aipPaciente);
		aipFonemaMaePaciente.setAipFonemas(aipFonemas);

		this.getAipFonemasMaePacienteDAO().persistir(aipFonemaMaePaciente);

		return aipFonemaMaePaciente;
	}
	
	private AipFonemaNomeSocialPacientes incluirFonemaNomeSocialPaciente(AipPacientes aipPaciente, AipFonemas aipFonemas) {
		AipFonemaNomeSocialPacientes fonemaNomeSocialPaciente = new AipFonemaNomeSocialPacientes();
		fonemaNomeSocialPaciente.setAipPaciente(aipPaciente);
		fonemaNomeSocialPaciente.setAipFonemas(aipFonemas);
		this.getAipFonemasNomeSocialPacientesDAO().persistir(fonemaNomeSocialPaciente);
		return fonemaNomeSocialPaciente;
	}	

	/**
	 * Salva os fonemas caso eles não existam. Caso existam é incrementado o
	 * campo frequência para cada fonema em questão.
	 * 
	 * @param fonemas
	 *            lista de fonemas a serem persistidas.
	 * @param tipo
	 *            tipo do fonema (P - nome do paciente, M - nome da mãe)
	 * @return retorna a lista de fonemas persistidos.
	 */
	@SuppressWarnings("rawtypes")
	private Set<AipFonemas> salvarAipFonemas(Set<String> fonemas, Class clazz) {
		AipFonemasDAO aipFonemasDAO = this.getAipFonemasDAO();

		Set<AipFonemas> aipFonemas = new HashSet<AipFonemas>();

		for (String f : fonemas) {
			AipFonemas aipFonema = aipFonemasDAO.obterPorChavePrimaria(f);

			if (aipFonema != null) {
				if (clazz.equals(AipFonemaPacientes.class)) {
					aipFonema.setContador(aipFonema.getContador() + 1);
				} else if (clazz.equals(AipFonemaNomeSocialPacientes.class)) {
					aipFonema.setContadorNomeSocial(aipFonema.getContadorNomeSocial() + 1); 
				} else {
					aipFonema.setContadorMae(aipFonema.getContadorMae() + 1);
				}
			} else {
				aipFonema = new AipFonemas();

				if (clazz.equals(AipFonemaPacientes.class)) {
					aipFonema.setContador(1l);
				} else {
					aipFonema.setContador(0l);
					aipFonema.setContadorNomeSocial(1l);
					aipFonema.setContadorMae(1l);
				}

				aipFonema.setFonema(f);
				aipFonemasDAO.persistir(aipFonema);
			}

			aipFonemas.add(aipFonema);
		}
		aipFonemasDAO.flush();

		return aipFonemas;
	}


	/**
	 * Cadastro de Paciente. Método responsável por implementar a procedure
	 * busca_dados_rn
	 * 
	 * @return .
	 */	
	public AipPacienteDadoClinicos buscaDadosAdicionaisClinicos(AipPacientes paciente){
		if (paciente.getAipPacienteDadoClinicos()!=null){
			return paciente.getAipPacienteDadoClinicos();
		}
		List<DadosAdicionaisVO> da = getAipPacientesDAO().obterDadosAdicionais(paciente.getCodigo());
		if (da != null && !da.isEmpty()){
			
			//Seguindo a regra do Oracle, que obtém primeiro registro de uma lista.
			DadosAdicionaisVO dadosVo = da.get(0);
			
			AipPacienteDadoClinicos dadosClinicos = new AipPacienteDadoClinicos();
			
			dadosClinicos.setDthrNascimento(dadosVo.getDtNascimento());			
			dadosClinicos.setApgar1(dadosVo.getApgar1());
			dadosClinicos.setApgar5(dadosVo.getApgar5());
			dadosClinicos.setTemperatura(dadosVo.getTemperatura());
			
			if (dadosVo.getIgFinal()!=null){
				dadosClinicos.setIgSemanas(dadosVo.getIgFinal());
			}else if (dadosVo.getIgAtualSemanas()!=null){
				dadosClinicos.setIgSemanas(dadosVo.getIgAtualSemanas());
			}else{
				dadosClinicos.setIgSemanas(Byte.valueOf("999"));
			}
			if (dadosVo.getDtHrFim()!=null && DateValidator.validaDataMenor(dadosVo.getDtHrFim(), new Date())){
				dadosClinicos.setIndExterno(DominioSimNao.S);
			}else{
				dadosClinicos.setIndExterno(DominioSimNao.N);				
			}
			if (dadosClinicos.getIgSemanas()<=15){
				dadosClinicos.setMesesGestacao(Byte.valueOf("3"));
			}else if (dadosClinicos.getIgSemanas()>=16 && dadosClinicos.getIgSemanas()<=19){
				dadosClinicos.setMesesGestacao(Byte.valueOf("4"));
			}else if (dadosClinicos.getIgSemanas()>=20 && dadosClinicos.getIgSemanas()<=23){
				dadosClinicos.setMesesGestacao(Byte.valueOf("5"));
			}else if (dadosClinicos.getIgSemanas()>=24 && dadosClinicos.getIgSemanas()<=27){
				dadosClinicos.setMesesGestacao(Byte.valueOf("6"));
			}else if (dadosClinicos.getIgSemanas()>=28 && dadosClinicos.getIgSemanas()<=31){
				dadosClinicos.setMesesGestacao(Byte.valueOf("7"));
			}else if (dadosClinicos.getIgSemanas()>=32 && dadosClinicos.getIgSemanas()<=35){
				dadosClinicos.setMesesGestacao(Byte.valueOf("8"));
			}else if (dadosClinicos.getIgSemanas()>=36){
				dadosClinicos.setMesesGestacao(Byte.valueOf("9"));
			}
			return dadosClinicos;
		}
		
		return null;
	}	
	
	
	protected ICascaFacade getICascaFacade() {
		return (ICascaFacade) this.cascaFacade;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	
	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return this.ambulatorioFacade;
	}

	protected ICentroCustoFacade getCentroCustoFacade() {
		return this.centroCustoFacade;
	}

	protected IInternacaoFacade getInternacaoFacade() {
		return this.internacaoFacade;
	}

	protected IHistoricoPacienteFacade getHistoricoPacienteFacade() {
		return (IHistoricoPacienteFacade) historicoPacienteFacade;
	}

	protected IPacienteFacade getPacienteFacade() {
		return (IPacienteFacade) pacienteFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return (IParametroFacade) parametroFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}
	
	protected EmailUtil getEmailUtil() {
		return (EmailUtil) emailUtil;
	}

	protected PacienteJournalON getPacienteJournalON() {
		return pacienteJournalON;
	}

	protected EnderecoON getEnderecoON() {
		return enderecoON;
	}

	protected AipEnderecosPacientesDAO getAipEnderecosPacientesDAO() {
		return aipEnderecosPacientesDAO;
	}

	protected AipFonemasDAO getAipFonemasDAO() {
		return aipFonemasDAO;
	}

	protected AipFonemaPacientesDAO getAipFonemaPacientesDAO() {
		return aipFonemaPacientesDAO;
	}

	protected AipFonemasMaePacienteDAO getAipFonemasMaePacienteDAO() {
		return aipFonemasMaePacienteDAO;
	}

	protected AipPacientesDAO getAipPacientesDAO() {
		return aipPacientesDAO;
	}
	
	protected AipPosicaoFonemasMaePacienteDAO getAipPosicaoFonemasMaePacienteDAO() {
		return aipPosicaoFonemasMaePacienteDAO;
	}

	

	protected AipPosicaoFonemasPacientesDAO getAipPosicaoFonemasPacientesDAO() {
		return aipPosicaoFonemasPacientesDAO;
	}

	

	protected AipPacientesJnDAO getAipPacientesJnDAO() {
		return aipPacientesJnDAO;
	}

	protected AipProntuarioLiberadosDAO getAipProntuarioLiberadosDAO() {
		return aipProntuarioLiberadosDAO;
	}

	protected PacIntdConvDAO getPacIntdConvDAO() {
		return pacIntdConvDAO;
	}

	protected AipCidadesDAO getAipCidadesDAO() {
		return aipCidadesDAO;
	}

	protected AipUfsDAO getAipUfsDAO() {
		return aipUfsDAO;
	}

	protected AipOcupacoesDAO getAipOcupacoesDAO() {
		return aipOcupacoesDAO;
	}

	protected AipNacionalidadesDAO getAipNacionalidadesDAO() {
		return aipNacionalidadesDAO;
	}
	
	protected AipAlturaPacientesDAO getAipAlturaPacientesDAO() {
		return aipAlturaPacientesDAO;
	}

	protected ObjetosOracleDAO getObjetosOracleDAO() {
		return objetosOracleDAO;
	}
	
	protected AipPacienteProntuarioDAO getAipPacienteProntuarioDAO(){
		return aipPacienteProntuarioDAO;
	}
	
	protected AipPacienteProntuarioJNDAO getAipPacienteProntuarioJNDAO(){
		return aipPacienteProntuarioJNDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
	protected AipPosicaoFonemasNomeSocialPacientesDAO getAipPosicaoFonemasNomeSocialPacientes() {
		return aipPosicaoFonemasNomeSocialPacientesDAO;
	}
	
	protected AipFonemaNomeSocialPacientesDAO getAipFonemasNomeSocialPacientesDAO() {
		return aipFonemaNomeSocialPacientesDAO;
	}
	
	public List<AipCidades> ordenarCidades(List<AipCidades> listCidades, Integer cidadeHu, String siglaUfHu){
		List<AipCidades> resultUfHu = new ArrayList<AipCidades>();
		List<AipCidades> result = new ArrayList<AipCidades>();
		List<AipCidades> listCidadesAux = 	new ArrayList<AipCidades>();

		for(AipCidades cidade: listCidades){
			if (cidade.getCodigo().equals(cidadeHu)){
				result.add(cidade);
			}else if (StringUtils.isNotEmpty(siglaUfHu) && cidade.getAipUf().getSigla().equalsIgnoreCase(siglaUfHu)){
				resultUfHu.add(cidade);
			}else {
				listCidadesAux.add(cidade);
			}
		}
		Collections.sort(resultUfHu, new Comparator<AipCidades>() {
			@Override
			public int compare(AipCidades o1, AipCidades o2) {
				return (o1.getAipUf().getSigla() + o1.getNome()).compareTo((o2.getAipUf().getSigla() + o2.getNome()));
			}
		});
		Collections.sort(listCidadesAux, new Comparator<AipCidades>() {
			@Override
			public int compare(AipCidades o1, AipCidades o2) {
				return (o1.getAipUf().getSigla() + o1.getNome()).compareTo((o2.getAipUf().getSigla() + o2.getNome()));
			}
		});

		result.addAll(resultUfHu);
		result.addAll(listCidadesAux);
		return result;
	}
	
}