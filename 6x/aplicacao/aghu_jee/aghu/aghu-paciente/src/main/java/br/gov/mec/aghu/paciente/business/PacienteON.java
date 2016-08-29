package br.gov.mec.aghu.paciente.business;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.ambulatorio.dao.AacConsultasDAO;
import br.gov.mec.aghu.bancosangue.dao.AbsComponenteSanguineoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.configuracao.dao.AghAtendimentoDAO;
import br.gov.mec.aghu.configuracao.dao.AghCaractEspecialidadesDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioCaracEspecialidade;
import br.gov.mec.aghu.dominio.DominioListaOrigensAtendimentos;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.farmacia.dao.AfaMedicamentoDAO;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.dao.AinAtendimentosUrgenciaDAO;
import br.gov.mec.aghu.internacao.dao.AinInternacaoDAO;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AbsComponenteSanguineo;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghCaractEspecialidades;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinAtendimentosUrgencia;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AipAlturaPacientes;
import br.gov.mec.aghu.model.AipConveniosSaudePaciente;
import br.gov.mec.aghu.model.AipGrupoFamiliarPacientes;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.AipPacientesDadosCns;
import br.gov.mec.aghu.model.AipPesoPacientes;
import br.gov.mec.aghu.model.AipSolicitacaoProntuarios;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.McoProcReanimacao;
import br.gov.mec.aghu.model.McoSindrome;
import br.gov.mec.aghu.model.McoTabBallard;
import br.gov.mec.aghu.model.RapConselhosProfissionais;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.VAipEnderecoPaciente;
import br.gov.mec.aghu.paciente.business.exception.PacienteExceptionCode;
import br.gov.mec.aghu.paciente.cadastro.business.ICadastroPacienteFacade;
import br.gov.mec.aghu.paciente.dao.AipAlturaPacientesDAO;
import br.gov.mec.aghu.paciente.dao.AipConveniosSaudePacienteDAO;
import br.gov.mec.aghu.paciente.dao.AipEnderecosPacientesDAO;
import br.gov.mec.aghu.paciente.dao.AipPacientesDAO;
import br.gov.mec.aghu.paciente.dao.AipPaisesDAO;
import br.gov.mec.aghu.paciente.dao.AipPesoPacientesDAO;
import br.gov.mec.aghu.paciente.dao.AipSolicitacaoProntuariosDAO;
import br.gov.mec.aghu.paciente.prontuario.business.exception.ProntuarioExceptionCode;
import br.gov.mec.aghu.paciente.vo.RelatorioPacienteVO;
import br.gov.mec.aghu.perinatologia.dao.McoProcReanimacaoDAO;
import br.gov.mec.aghu.perinatologia.dao.McoSindromeDAO;
import br.gov.mec.aghu.perinatologia.dao.McoTabBallardDAO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.vo.RapServidoresVO;

/**
 * Classe responsável por prover os métodos de negócio genéricos para a entidade
 * de pacientes.
 */
@SuppressWarnings({ "PMD.AghuTooManyMethods", "PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength" })
@Stateless
public class PacienteON extends BaseBusiness {

	@EJB
	private FonemasPacienteRN fonemasPacienteRN;

	@EJB
	private PacienteRN pacienteRN;

	@EJB
	AtendimentosRN atendimentosRN;

	private static final Log LOG = LogFactory.getLog(PacienteON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IInternacaoFacade internacaoFacade;

	@Inject
	private AipPesoPacientesDAO aipPesoPacientesDAO;

	@Inject
	private AipAlturaPacientesDAO aipAlturaPacientesDAO;

	@Inject
	private AipEnderecosPacientesDAO enderecoPacienteDAO;

	@Inject
	private AipPaisesDAO aipPaisesDAO;

	@Inject
	private AipPacientesDAO aipPacientesDAO;

	@Inject
	private AipSolicitacaoProntuariosDAO aipSolicitacaoProntuariosDAO;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private ICadastroPacienteFacade cadastroPacienteFacade;
	
	@Inject
	private AipConveniosSaudePacienteDAO aipConveniosSaudePacienteDAO;

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	@Inject
	private AghAtendimentoDAO aghAtendimentoDAO;

	@Inject
	AghCaractEspecialidadesDAO aghCaractEspecialidadesDAO;

	@Inject
	AinAtendimentosUrgenciaDAO ainAtendimentosUrgenciaDAO;

	@Inject
	AinInternacaoDAO ainInternacaoDAO;
	
	@Inject
	private MbcCirurgiasDAO mbcCirurgiasDAO;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@Inject
	private AacConsultasDAO aacConsultasDAO;
	@Inject
	private McoProcReanimacaoDAO mcoProcReanimacaoDAO;

	@Inject
	private AbsComponenteSanguineoDAO absComponenteSanguineoDAO;

	@Inject
	private AfaMedicamentoDAO afaMedicamentoDAO;
	
	@Inject
	private McoSindromeDAO mcoSindromeDAO;
	
	@Inject
	private McoTabBallardDAO mcoTabBallardDAO;

	@EJB
	private PesquisarPacienteON pesquisarPacienteON;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6425647926603264960L;

	private static final String HORAS = ("HH:mm");

	private enum PacienteONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_INFORMAR_CRITERIOS, MENSAGEM_LEITO_INVALIDO, MENSAGEM_INFORME_DATA_INICIAL, MENSAGEM_INFORME_DATA_FINAL, 
		MENSAGEM_DATA_FINAL_MAIOR_IGUAL_DATA_INICIAL, MENSAGEM_INFORMAR_DADOS_MINIMOS, MENSAGEM_INFORMAR_APENAS_UM_PERIODO, 
		MENSAGEM_INFORMAR_PERIODO_CIRURGIA, MENSAGEM_DIFERENCA_DATAS_PERIODO_ALTA, MENSAGEM_DIFERENCA_DATAS_PERIODO_CONSULTA, 
		MENSAGEM_DIFERENCA_DATAS_PERIODO_CIRURGIA, AIN_00812_1, ERRO_PAC_INT_SO, ERRO_QTD_DIAS_INGRESSO_SO, 
		ERRO_ATENDIMENTO_ESPECIALIDADE_EMERGENCIA, AIN_00200, AIN_00242;
	}

	public AipPacientes obterPacientePelaConsulta(Integer numeroConsulta) {
		AacConsultas consulta = ambulatorioFacade.obterAacConsulta(numeroConsulta);
		return consulta != null ? consulta.getPaciente() : null;
	}
	
	public BigDecimal obterPesoPacientePelaConsulta(Integer numeroConsulta) {
		AacConsultas consulta = ambulatorioFacade.obterAacConsulta(numeroConsulta);
		AipPesoPacientes peso = aipPesoPacientesDAO.obterPesoPacientesPorNumeroConsulta(numeroConsulta);
		Date dataPeso =  peso != null ? peso.getId().getCriadoEm() : null;
		if(dataPeso != null && DateUtil.entre(dataPeso, consulta.getDtConsulta(), DateUtil.adicionaHoras(consulta.getDtConsulta(), 1))) {
			return peso.getPeso();
		}
		return null;
	}

	public BigDecimal obterAlturaPacientePelaConsulta(Integer numeroConsulta) {
		AacConsultas consulta = ambulatorioFacade.obterAacConsulta(numeroConsulta);
		AipAlturaPacientes altura = aipAlturaPacientesDAO.obterAlturaPorNumeroConsulta(numeroConsulta);
		Date dataAltura =  altura != null ? altura.getId().getCriadoEm() : null;
		if(dataAltura != null && DateUtil.entre(dataAltura, consulta.getDtConsulta(), DateUtil.adicionaHoras(consulta.getDtConsulta(), 1))) {
			return altura.getAltura();
		}
		return null;
	}

	public List<AipPacientes> pesquisarPorFonemas(Integer firstResult,
			Integer maxResults, String nome, String nomeMae,
			boolean respeitarOrdem, Date dataNascimento,
			DominioListaOrigensAtendimentos listaOrigensAtendimentos)
			throws ApplicationBusinessException {

		if (StringUtils.isBlank(nome) && StringUtils.isBlank(nomeMae)) {
			throw new ApplicationBusinessException(
					PacienteExceptionCode.MENSAGEM_ERRO_PESQUISA_FONETICA_NOME_OBRIGATORIO);
		}

		Calendar dataInicio = null;
		Calendar dataLimite = null;

		if (dataNascimento != null) {
			dataInicio = Calendar.getInstance();
			dataInicio.setTime(dataNascimento);
			dataInicio.set(Calendar.DAY_OF_MONTH,
					dataInicio.getActualMinimum(Calendar.DAY_OF_MONTH));

			dataLimite = Calendar.getInstance();
			dataLimite.setTime(dataNascimento);
			dataLimite.set(Calendar.DAY_OF_MONTH,
					dataLimite.getActualMaximum(Calendar.DAY_OF_MONTH));
		}

		return this.fonemasPacienteRN.obterPacientes(nome, nomeMae,
				dataInicio, dataLimite, respeitarOrdem, firstResult,
				maxResults, listaOrigensAtendimentos);
	}
	
	public List<AipPacientes> pesquisarPorFonemas(PesquisaFoneticaPaciente parametrosPequisa) throws ApplicationBusinessException {
		return this.fonemasPacienteRN.obterPacientes(parametrosPequisa);
	}
	
	public Long pesquisarPorFonemasCount(PesquisaFoneticaPaciente parametrosPequisa) throws ApplicationBusinessException {
		return this.fonemasPacienteRN.obterPacientesCount(parametrosPequisa);
	}

	/**
	 * Neste caso é melhor fazer a contagem pelo número de código, pois pode
	 * encontrar os mesmos pacientes nos dois casos, senão a contagem não fica
	 * precisa com o número de registros retornados.
	 * 
	 * Na verdade o ideal seria trazer todos os pacientes e passar pelo
	 * manterOrdem, mas ai vai ficar caro fazer isso.
	 * 
	 * @param nome
	 * @param nomeMae
	 * @param respeitarOrdem
	 * @param dataNascimento
	 * @return
	 */
	public Long pesquisarPorFonemasCount(String nome, String nomeMae,
			Boolean respeitarOrdem, Date dtNascimento,
			DominioListaOrigensAtendimentos listaOrigensAtendimentos)
			throws ApplicationBusinessException {
		if (StringUtils.isBlank(nome) && StringUtils.isBlank(nomeMae)) {
			throw new ApplicationBusinessException(
					PacienteExceptionCode.MENSAGEM_ERRO_PESQUISA_FONETICA_NOME_OBRIGATORIO);
		}

		Calendar dataInicio = null;
		Calendar dataLimite = null;

		if (dtNascimento != null) {
			dataInicio = Calendar.getInstance();
			dataInicio.setTime(dtNascimento);
			dataInicio.set(Calendar.DAY_OF_MONTH,
					dataInicio.getActualMinimum(Calendar.DAY_OF_MONTH));

			dataLimite = Calendar.getInstance();
			dataLimite.setTime(dtNascimento);
			dataLimite.set(Calendar.DAY_OF_MONTH,
					dataLimite.getActualMaximum(Calendar.DAY_OF_MONTH));
		}

		return this.fonemasPacienteRN.obterPacientesCount(nome, nomeMae,
				dataInicio, dataLimite, respeitarOrdem,
				listaOrigensAtendimentos);
	}
	
	/**
	 * Obtem o paciente através do cartao de saude.
	 * 
	 * @param aipPacientesNroCartaoSaude
	 * @return
	 */
	public List<AipPacientes> obterPacientePorCartaoSaude(BigInteger aipPacientesNroCartaoSaude) {
		return this.aipPacientesDAO.pesquisarPacientePorCartaoSaude(aipPacientesNroCartaoSaude);
	}
	
	public List<AipPacientes> obterPacientePorCartaoCpfCodigoPronturario(Integer nroProntuario, Integer nroCodigo, Long cpf,BigInteger... nroCartaoSaude) throws ApplicationBusinessException{
		return this.aipPacientesDAO.obterPacientePorCartaoCpfCodigoPronturario(nroProntuario, nroCodigo, cpf, nroCartaoSaude[0]);
		
	}
	
	/**
	 * Busca um paciente na base de dados pelo seu prontuario ou seu codigo.
	 * 
	 * @param nroProntuario
	 * @param nroCodigo
	 * @return
	 * @throws AGHUNegocioException
	 */
	public AipPacientes obterPacientePorCodigoOuProntuario(Integer nroProntuario, Integer nroCodigo, Long cpf, BigInteger... nroCartaoSaude)
			throws ApplicationBusinessException {

		AipPacientes pacienteVolta = null;
		if (nroProntuario == null && nroCodigo == null && cpf == null && (nroCartaoSaude == null || nroCartaoSaude.length == 0)) {
			throw new ApplicationBusinessException(PacienteExceptionCode.AIP_PRONTUARIO_NULO);
		} else if (nroProntuario != null) {
			
			pacienteVolta = pesquisarPacientePorProntuario(nroProntuario);
			
		} else if(nroCodigo != null){
			pacienteVolta = obterHistoricoPaciente(nroCodigo);
		} else if(nroCartaoSaude != null && nroCartaoSaude.length > 0) {
			
			List<AipPacientes> listaNroCartaoSaude = obterPacientePorCartaoSaude(nroCartaoSaude[0]); 
			
			if(!listaNroCartaoSaude.isEmpty() && listaNroCartaoSaude != null){
				pacienteVolta = listaNroCartaoSaude.get(0);
			}
		}

		return pacienteVolta;
	}

	/**
	 * Busca um paciente na base de dados pelo seu prontuario ou seu codigo.
	 * 
	 * @param nroProntuario
	 * @param nroCodigo
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public AipPacientes obterPacientePorCodigoOuProntuario(
			Integer nroProntuario, Long cpf, Integer nroCodigo)
			throws ApplicationBusinessException {

		AipPacientes pacienteVolta = null;
		
		if (nroProntuario == null && nroCodigo == null && cpf == null) {
			return null;
		} else if (nroProntuario != null) {
			pacienteVolta = pesquisarPacientePorProntuario(nroProntuario);
		}  else if (nroCodigo != null){
			pacienteVolta = obterPaciente(nroCodigo);
		} else if (cpf != null) {
			pacienteVolta = obterCpfPaciente(cpf);
		}
		
		this.inicializaPaciente(pacienteVolta);
		
		return pacienteVolta;
	}
	
	public List<AipPacientes> obterPacientePorCodigoOuProntuarioFamiliar(
			Integer nroProntuario, Integer nroCodigo, Integer nroProntuarioFamiliar)
			throws ApplicationBusinessException {

		List<AipPacientes> pacienteVolta = null;

		if(nroProntuarioFamiliar != null){
			pacienteVolta = pesquisarPacientePorProntuarioFamiliar(nroProntuario, nroCodigo, nroProntuarioFamiliar);
		}
		for(AipPacientes pVolta : pacienteVolta){
			this.inicializaPaciente(pVolta);
		}
				
		return pacienteVolta;
	}

	/**
	 * 
	 * @param nroProntuario
	 * @param nroCodigo
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public AipPacientes obterPacientePorCodigoOuProntuarioSemDigito(
			Integer nroProntuario, Integer nroCodigo)
			throws ApplicationBusinessException {

		AipPacientes pacienteVolta = null;

		if (nroProntuario == null && nroCodigo == null) {
			throw new ApplicationBusinessException(
					PacienteExceptionCode.AIP_PRONTUARIO_NULO);
		} else if (nroProntuario != null) {
			pacienteVolta = this.getPesquisarPacienteON().pesquisarPacientePorProntuarioSemDigito(nroProntuario);
		} else {
			pacienteVolta = obterPaciente(nroCodigo);
		}
		
		this.inicializaPaciente(pacienteVolta);
		
		return pacienteVolta;
	}

	private PesquisarPacienteON getPesquisarPacienteON() {
		return pesquisarPacienteON;
	}

	private void inicializaPaciente(AipPacientes pac) {
		
		if(pac == null){
			return;
		}
		
		// Carregando os objetos lazy para exibir na tela
		if (pac.getFccCentroCustosCadastro() != null){
		    pac.getFccCentroCustosCadastro().getDescricao();
		}
		if(pac.getFccCentroCustosRecadastro() != null){
			pac.getFccCentroCustosRecadastro().getDescricao();
		}
		
		RapServidores auxServidor = pac.getRapServidoresCadastro();
		if (auxServidor != null) {
			auxServidor.getPessoaFisica().getNome();
		}
		
		auxServidor = pac.getRapServidoresRecadastro();
		if (auxServidor != null) {
			auxServidor.getPessoaFisica().getNome();
		}

	}

	/**
	 * Busca um paciente na base de dados pelo seu prontuario e seu codigo.
	 * Obs.: Ao menos um dos parâmetros deve ser informado. Todos parâmetros que
	 * forem informados serão utilizados como filtro de consulta.
	 * 
	 * @param nroProntuario
	 * @param nroCodigo
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public AipPacientes obterPacientePorCodigoEProntuario(
			Integer nroProntuario, Integer nroCodigo, Long cpf)
			throws ApplicationBusinessException {
		if (nroProntuario == null && nroCodigo == null && cpf == null) {
			throw new ApplicationBusinessException(
					PacienteExceptionCode.AIP_PRONTUARIO_E_CODIGO_NULOS);
		}

		return this.aipPacientesDAO.obterPacientePorCodigoEProntuario(
				nroProntuario, nroCodigo, cpf);
	}

	/**
	 * Retorna a 0 se não encontrou o paciente e 1 se encontrou.
	 * 
	 * @param nroProntuario
	 * @param nroCodigo
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Long pesquisarPacienteCount(Integer nroProntuario, Integer nroCodigo, Long cpf, BigInteger nroCartaoSaude)
			throws ApplicationBusinessException {
		if (nroProntuario == null && nroCodigo == null && nroCartaoSaude == null && cpf == null) {
			throw new ApplicationBusinessException(
					PacienteExceptionCode.AIP_PRONTUARIO_E_CODIGO_NULOS);
		} else {
			return this.aipPacientesDAO.pesquisarPacienteCount(
					nroProntuario, nroCodigo, cpf, nroCartaoSaude);
		}
	}
	
	public AipPacientes pesquisarPacientePorProntuarioLeito(Integer nroProntuario) {
		return this.aipPacientesDAO.pesquisarPacientePorProntuarioLeito(
				nroProntuario);
	}
	public AipPacientes pesquisarPacientePorProntuario(Integer nroProntuario) {
		return this.aipPacientesDAO.pesquisarPacientePorProntuario(
				nroProntuario);
	}
	
	public List<AipPacientes> pesquisarPacientePorProntuarioFamiliar(Integer nroProntuario, Integer nroCodigo, Integer nroProntuarioFamiliar) {
		return this.aipPacientesDAO.pesquisarPacientePorProntuarioFamiliar(
				nroProntuario, nroCodigo, nroProntuarioFamiliar);
	}
	
	public AipPacientes pesquisarPacienteComponente(Object valor, String tipo) throws ApplicationBusinessException {
		if (valor==null){
			return null;
		}
		Integer numero=null;
		if (valor instanceof String){
			if (((String)valor).isEmpty()){
				return null;
			}
			numero=Integer.valueOf((String)valor);
		}else{
			numero=(Integer) valor;
		}
		
		AipPacientes  paciente = null;
		if (("codigo").equals(tipo)){
			paciente =  this.aipPacientesDAO.obterPorChavePrimaria(numero);
			if (paciente==null){
				throw new ApplicationBusinessException("MENSAGEM_PACIENTE_CODIGO_NAO_ENCONTRADO", Severity.ERROR, numero);
			}
		}else if (("prontuario").equals(tipo)){
			paciente = this.aipPacientesDAO.pesquisarPacientePorProntuario(numero);
			if (paciente==null){
				throw new ApplicationBusinessException("MENSAGEM_PACIENTE_PRONTUARIO_NAO_ENCONTRADO", Severity.ERROR, CoreUtil.formataProntuario(numero));
			}
		}	
		return paciente;
	}	

	/**
	 * 
	 * @param nroProntuario
	 * @return
	 */
	public AipPacientes pesquisarPacientePorProntuarioSemDigito(
			Integer nroProntuario) {
		return this.aipPacientesDAO
				.pesquisarPacientePorProntuarioSemDigito(nroProntuario);
	}

	public List<AipPacientes> pesquisarPacientesPorListaProntuario(
			Collection<Integer> nroProntuarios)
			throws ApplicationBusinessException {
		List<AipPacientes> pacientes = this.aipPacientesDAO
				.pesquisarPacientesPorListaProntuario(nroProntuarios);
		for (AipPacientes pac : pacientes) {
			aipPacientesDAO.refresh(pac);
		}

		/*
		 * TODO: Migrar as procedures e functions da package AIPK_POL_VER_ACESSO
		 * em pacienteRN.temPermissaoVerProntuario
		 */
		// if (!pacienteRN.temPermissaoVerProntuario(nroProntuarios,pacientes))
		// {
		// throw new
		// AuthorizationException("Você não tem permissão de acesso.");
		// }

		return pacientes;
	}

	/**
	 * Obtem o paciente através de seu código.
	 * 
	 * @param aipPacientesCodigo
	 * @return
	 */
	public AipPacientes obterHistoricoPaciente(Integer aipPacientesCodigo) {
		return this.aipPacientesDAO.obterHistoricoPorPacCodigo(aipPacientesCodigo);
		
	}
	
	public AipPacientes obterPaciente(Integer aipPacientesCodigo) {
		return this.aipPacientesDAO.obterPorChavePrimaria(aipPacientesCodigo, true, 
				new Enum[] {AipPacientes.Fields.NACIONALIDADE, AipPacientes.Fields.ENDERECOS, 
				AipPacientes.Fields.ETNIA, AipPacientes.Fields.CIDADE, AipPacientes.Fields.MAE_PACIENTE});
	}
	
	public AipPacientes obterCpfPaciente(Long cpf) {
		return this.aipPacientesDAO.obterPorChavePrimaria(cpf);
	}

	public AipPesoPacientes obterPesoPacienteAtual(AipPacientes aipPaciente) {
		return this.aipPesoPacientesDAO.obterPesoPacienteAtual(aipPaciente);
	}

	/**
	 * METODOS RELATIVOS AO CRUD DE LIBERAR PRONTUARIOS DE PACIENTES
	 */

	/**
	 * Metodo para retornar paginação de prontuários
	 * 
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * 
	 * @param codigo
	 * @param nome
	 * @param prontuario
	 * 
	 * @return List<AipPacientes>
	 */
	public List<AipPacientes> pesquisaProntuarioPaciente(Integer firstResult,
			Integer maxResults, Integer codigo, String nome, Integer prontuario) {
		return this.aipPacientesDAO.pesquisaProntuarioPaciente(
				firstResult, maxResults, codigo, nome, prontuario);
	}

	/**
	 * Método para a pesquisa da situação de prontuário de um paciente
	 * 
	 * @param firstResult
	 * @param maxResults
	 * @param codigo
	 * @param nome
	 * @param prontuario
	 * @param indPacienteVip
	 * @param indPacProtegido
	 * @return
	 */
	public List<AipPacientes> pesquisarSituacaoProntuario(Integer firstResult,
			Integer maxResults, Integer codigo, String nome,
			Integer prontuario, DominioSimNao indPacienteVip,
			DominioSimNao indPacProtegido, Boolean consideraDigito) {
		return this.aipPacientesDAO.pesquisarSituacaoProntuario(
				firstResult, maxResults, codigo, nome, prontuario,
				indPacienteVip, indPacProtegido, consideraDigito);
	}

	public Long pesquisaProntuarioPacienteCount(Integer codigo, String nome,
			Integer prontuario) {
		return this.aipPacientesDAO.pesquisaProntuarioPacienteCount(
				codigo, nome, prontuario);
	}

	/**
	 * Método que retorna o count para a pesquisa da situação de prontuário
	 * 
	 * @param codigo
	 * @param nome
	 * @param prontuario
	 * @param indPacienteVip
	 * @param indPacProtegido
	 * @return
	 */
	public Long pesquisarSituacaoProntuarioCount(Integer codigo, String nome,
			Integer prontuario, DominioSimNao indPacienteVip,
			DominioSimNao indPacProtegido, Boolean consideraDigito) {
		return this.aipPacientesDAO.pesquisarSituacaoProntuarioCount(
				codigo, nome, prontuario, indPacienteVip, indPacProtegido,
				consideraDigito);
	}

	/**
	 * Método pertencente ao crud Liberar Prontuario, que exclui um prontuário.
	 * A exclusão de prontuário, na verdade, consiste em realizar verificações
	 * sobre um determinado paciente para após, excluí-lo.
	 * 
	 * @param paciente
	 * @throws ApplicationBusinessException
	 */
	public void excluirProntuario(AipPacientes paciente, String usuarioLogado)
			throws ApplicationBusinessException {
		this.verificaExclusaoProntuario(paciente.getCodigo());

		this.cadastroPacienteFacade.excluirPaciente(paciente, usuarioLogado);
	}

	/**
	 * Faz verificações para validar se um prontuario pode ser excluído.
	 * 
	 * @param codPaciente
	 * @throws ApplicationBusinessException
	 */
	private void verificaExclusaoProntuario(Integer codPaciente)
			throws ApplicationBusinessException {
		if (this.internacaoFacade.obterUltimaInternacaoPaciente(
				codPaciente) != null) {
			throw new ApplicationBusinessException(
					ProntuarioExceptionCode.AIN_00611);
		}
		if (this.internacaoFacade.obterUltimoAtendimentosUrgencia(
				codPaciente) != null) {
			throw new ApplicationBusinessException(
					ProntuarioExceptionCode.AIN_00613);
		}
		if (this.internacaoFacade.obterUltimoAtendimentoHospitaisDia(
				codPaciente) != null) {
			throw new ApplicationBusinessException(
					ProntuarioExceptionCode.AIN_00615);
		}
		if (this.aghuFacade.obterUltimoAtendimento(codPaciente) != null) {
			throw new ApplicationBusinessException(
					ProntuarioExceptionCode.AIP_00466);
		}
	}

	/**
	 * Obtem o paciente e o endereço padrão através de seu prontuario.
	 * 
	 * @param aipPacientesCodigo
	 * @throws ApplicationBusinessException 
	 * @return
	 */
	public RelatorioPacienteVO obterPacienteComEnderecoPadrao(
			Integer nroProntuario, Long cpf, Integer nroCodigo)
			throws ApplicationBusinessException {
		AipPacientes aipPacientes = obterPacientePorCodigoOuProntuario(
				nroProntuario, nroCodigo, cpf);
		if (aipPacientes == null) {
			throw new ApplicationBusinessException(
					PacienteExceptionCode.AIP_PRONTUARIO_INVALIDO);
		}
		RelatorioPacienteVO pacienteVO = new RelatorioPacienteVO();
		populaDadosPacienteVO(pacienteVO, aipPacientes);
		// PDS
		if (aipPacientes.getAipPacientesDadosCns() != null) {
			populaDadosPacienteVO(pacienteVO,
					aipPacientes.getAipPacientesDadosCns());
		}

		VAipEnderecoPaciente aipEnderecosPacientes = this
				.cadastroPacienteFacade.obterEndecoPaciente(
						aipPacientes.getCodigo());
		if (aipEnderecosPacientes == null
				|| aipEnderecosPacientes.getPacCodigo() == null) {
			throw new ApplicationBusinessException(
					PacienteExceptionCode.ENDERECO_PADRAO_NAO_ENCONTRADO);
		}

		populaDadosPacienteVO(pacienteVO, aipEnderecosPacientes);

		return pacienteVO;
	}

	private RelatorioPacienteVO populaDadosPacienteVO(
			RelatorioPacienteVO pacienteVO,
			AipPacientesDadosCns aipPacientesDadosCns) {

		try {
			ConvertUtilsBean converterUtils = new ConvertUtilsBean();
			converterUtils.register(new DateConverter(null), Date.class);
			BeanUtilsBean beanUtils = new BeanUtilsBean(converterUtils);
			beanUtils.copyProperties(pacienteVO, aipPacientesDadosCns);
		} catch (IllegalAccessException e) {
			this.logError(e.getMessage());
		} catch (InvocationTargetException e) {
			this.logError(e.getMessage());
		}

		if (aipPacientesDadosCns.getMotivoCadastro() != null) {
			pacienteVO.setMotivoCadastro(Integer.toString(aipPacientesDadosCns
					.getMotivoCadastro().getCodigo()));
		}
		if (aipPacientesDadosCns.getAipOrgaosEmissor() != null) {
			pacienteVO.setCodigoOrgaoEmissor(aipPacientesDadosCns
					.getAipOrgaosEmissor().getCodigo());
			pacienteVO.setOrgaoEmissor(aipPacientesDadosCns
					.getAipOrgaosEmissor().getDescricao());
		}
		if (aipPacientesDadosCns.getAipUf() != null) {
			pacienteVO.setUfEmitiuDoc(aipPacientesDadosCns.getAipUf()
					.getSigla());
		}
		if (aipPacientesDadosCns.getDocReferencia() != null) {
			pacienteVO.setDocReferencia(aipPacientesDadosCns.getDocReferencia()
					.getDescricao());
		}
		if (aipPacientesDadosCns.getTipoCertidao() != null) {
			Integer codigoCertidao = aipPacientesDadosCns.getTipoCertidao()
					.getCodigo();
			pacienteVO.setTipoCertidao(codigoCertidao.toString());
		}
		pacienteVO.setDataEmissao(aipPacientesDadosCns.getDataEmissao());
		pacienteVO.setDataEmissaoDocto(aipPacientesDadosCns
				.getDataEmissaoDocto());
		pacienteVO.setCriadoEm(aipPacientesDadosCns.getCriadoEm());

		return pacienteVO;
	}
	
	private void recuperarHorarioCadastro(AipPacientes aipPacientes, RelatorioPacienteVO pacienteVO) {
		if (aipPacientes.getCriadoEm() != null) {
				String horaCadastro = new SimpleDateFormat(HORAS).format(aipPacientes.getCriadoEm());
				pacienteVO.setHoraCadastro(horaCadastro); 
		}
	}

	@SuppressWarnings("PMD.NPathComplexity")
	private RelatorioPacienteVO populaDadosPacienteVO(
			RelatorioPacienteVO pacienteVO, AipPacientes aipPacientes) {
		try {
			ConvertUtils.register(new DateConverter(null), Date.class);
			BeanUtils.copyProperties(pacienteVO, aipPacientes);
			
			recuperarHorarioCadastro(aipPacientes, pacienteVO);

			if (aipPacientes.getCor() != null) {
				pacienteVO.setCor(aipPacientes.getCor().getDescricao());
			}
			
			if (aipPacientes.getCodigo() != null) {
				pacienteVO.setCodigoPaciente(aipPacientes.getCodigo().toString());
			}
			
			if (aipPacientes.getSexo() != null) {
				pacienteVO.setSexo(aipPacientes.getSexo().getDescricao());
			}
			
			if (aipPacientes.getEstadoCivil() != null) {
				pacienteVO.setEstadoCivil(aipPacientes.getEstadoCivil()
						.getDescricao());
			}
			
			if (aipPacientes.getIdSistemaLegado() != null) {
				pacienteVO.setProntFamilia(aipPacientes.getIdSistemaLegado().intValue());
			}
			
			AipGrupoFamiliarPacientes grupoFamiliar = ambulatorioFacade.obterProntuarioFamiliaPaciente(aipPacientes.getCodigo());
			if(grupoFamiliar != null){
				pacienteVO.setProntFamilia(grupoFamiliar.getAgfSeq());
			}
			
			if (aipPacientes.getNomeSocial() != null) {
				pacienteVO.setNomeSocial(aipPacientes.getNomeSocial());
			}
			if (aipPacientes.getAipCidades() != null) {
				pacienteVO.setNaturalidade(aipPacientes.getAipCidades()
						.getNome());
				if (aipPacientes.getAipCidades().getAipUf() != null) {
					pacienteVO.setUfNascimento(aipPacientes.getAipCidades()
							.getAipUf().getSigla());
				}
			}
			if (aipPacientes.getAipNacionalidades() != null) {
				String siglaNac = aipPacientes.getAipNacionalidades()
						.getSigla();
				pacienteVO.setSiglaPaisNacionalidade(siglaNac);
				pacienteVO.setNacionalidade(aipPacientes.getAipNacionalidades()
						.getDescricao());
				if(siglaNac != null){
					pacienteVO.setPaisOrigem(this.aipPaisesDAO.obterPaisPorSigla(siglaNac));
				}
			}
			if (aipPacientes.getAipOcupacoes() != null) {
				pacienteVO.setCodigoProfissao(aipPacientes.getAipOcupacoes()
						.getCodigo());
				pacienteVO.setProfissao(aipPacientes.getAipOcupacoes()
						.getDescricao());
			}

			if (aipPacientes.getGrauInstrucao() != null) {
				pacienteVO.setGrauInstrucao(aipPacientes.getGrauInstrucao()
						.getDescricao());
			}

			pacienteVO.setNomeAnterior(this.cadastroPacienteFacade
					.obterNomeAnteriorPaciente(aipPacientes.getCodigo()));

			pacienteVO.setPostoReferencia(this.pacienteRN.mamcGetPostoPac(
					aipPacientes.getCodigo()));

			if (aipPacientes.getRapServidoresCadastro() != null
					&& aipPacientes.getRapServidoresCadastro()
							.getPessoaFisica() != null) {
				pacienteVO
						.setIdentificador(aipPacientes
								.getRapServidoresCadastro().getPessoaFisica()
								.getNome());
			}
			if (aipPacientes.getFccCentroCustosCadastro() != null) {
				pacienteVO.setAreaCadastradora(aipPacientes
						.getFccCentroCustosCadastro().getDescricao());
			}

		} catch (Exception e) {
			this.logError("Erro: " + e.getMessage(), e);
		}
		return pacienteVO;
	}

	private RelatorioPacienteVO populaDadosPacienteVO(
			RelatorioPacienteVO pacienteVO,
			VAipEnderecoPaciente vAipEnderecoPaciente) {
		if (vAipEnderecoPaciente != null) {
			pacienteVO.setLogradouro(vAipEnderecoPaciente.getLogradouro());
			pacienteVO
					.setNroLogradouro(vAipEnderecoPaciente.getNroLogradouro());
			pacienteVO.setComplementoLogradouro(vAipEnderecoPaciente
					.getComplLogradouro());
			pacienteVO.setBairro(vAipEnderecoPaciente.getBairro());
			pacienteVO.setCidade(vAipEnderecoPaciente.getCidade());
			if (vAipEnderecoPaciente.getCep() != null) {
				pacienteVO.setCep(vAipEnderecoPaciente.getCep().intValue());
			}
			pacienteVO.setUfLogradouro(vAipEnderecoPaciente.getUf());
			pacienteVO.setCodIbge(vAipEnderecoPaciente.getCodIbge());
		}
		return pacienteVO;
	}

	public List<AipSolicitacaoProntuarios> pesquisarSolicitacaoProntuario(
			Integer firstResult, Integer maxResults, String solicitante,
			String responsavel, String observacao) {

		List<AipSolicitacaoProntuarios> lista = this
				.aipSolicitacaoProntuariosDAO
				.pesquisarSolicitacaoProntuario(firstResult, maxResults,
						solicitante, responsavel, observacao);

		for (AipSolicitacaoProntuarios solicitacao : lista) {
			if (solicitacao.getAipPacientes() != null) {
				solicitacao.getAipPacientes().size();// Mesmo fazendo o fetch
														// mode, é necessário
														// chamar a lista
			}
		}
		return lista;
	}

	public AipSolicitacaoProntuarios obterSolicitacaoProntuario(Integer codigo) {
		return this.aipSolicitacaoProntuariosDAO.obterSolicitacaoProntuario(codigo);
	}

	public Long pesquisarSolicitacaoProntuarioCount(String solicitante,
			String responsavel, String observacao) {
		return this.aipSolicitacaoProntuariosDAO.pesquisarSolicitacaoProntuarioCount(solicitante, responsavel,
						observacao);
	}

	public List<AipPacientes> pesquisarPacientesPorProntuario(String strPesquisa) {
		return this.aipPacientesDAO.pesquisarPacientesPorProntuario(
				strPesquisa);
	}

	public List<AipPacientes> pesquisarPacientesPorProntuarioOuCodigo(
			String strPesquisa) {
		return this.aipPacientesDAO
				.pesquisarPacientesPorProntuarioOuCodigo(strPesquisa);
	}

	public List<AipPacientes> pesquisarPacientesComProntuarioPorProntuarioOuCodigo(
			String strPesquisa) {
		return this.aipPacientesDAO
				.pesquisarPacientesComProntuarioPorProntuarioOuCodigo(
						strPesquisa);
	}

	public List<AipPacientes> obterPacientesPorProntuarioOuCodigo(
			String strPesquisa) {
		return this.aipPacientesDAO.obterPacientesPorProntuarioOuCodigo(
				strPesquisa);
	}

	public String pesquisarNomeProfissional(Integer matricula, Short vinculo) {
		StringBuffer nomeProfissional = new StringBuffer();

		RapServidores rapServidores = this.registroColaboradorFacade
				.obterRapServidoresPorChavePrimaria(
						new RapServidoresId(matricula, vinculo));

		List<RapConselhosProfissionais> lista = this
				.registroColaboradorFacade
				.listarRapConselhosProfissionaisPorPessoaFisica(
						rapServidores.getPessoaFisica());
		if (lista != null && !lista.isEmpty()) {
			// Pega o primeiro elemento para processar
			RapConselhosProfissionais conselhoProfissional = lista.get(0);

			if (rapServidores.getPessoaFisica().getSexo() == DominioSexo.M) {
				if (rapServidores.getVinculo().getTituloMasculino() != null) {
					nomeProfissional.append(
							rapServidores.getVinculo().getTituloMasculino())
							.append(' ');
				} else if (conselhoProfissional != null
						&& conselhoProfissional.getTituloMasculino() != null) {
					nomeProfissional.append(
							conselhoProfissional.getTituloMasculino()).append(
							' ');
				}
			} else {
				if (rapServidores.getVinculo().getTituloFeminino() != null) {
					nomeProfissional.append(
							rapServidores.getVinculo().getTituloFeminino())
							.append(' ');
				} else if (conselhoProfissional != null
						&& conselhoProfissional.getTituloFeminino() != null) {
					nomeProfissional.append(
							conselhoProfissional.getTituloFeminino()).append(
							' ');
				}
			}
		}

		nomeProfissional.append(rapServidores.getPessoaFisica().getNome());

		return WordUtils.capitalize(nomeProfissional.toString().toLowerCase());
	}

	public List<RapServidoresVO> obterNomeProfissionalServidores(
			List<RapServidoresId> servidores) {

		List<RapServidoresVO> lista = this.registroColaboradorFacade
				.listarRapConselhosProfissionaisPorServidor(servidores);

		for (RapServidoresVO rapServidoresVO : lista) {
			StringBuffer nomeProfissional = new StringBuffer();
			if (rapServidoresVO.getSexo() == DominioSexo.M) {
				if (rapServidoresVO.getTituloMasculinoVinculo() != null) {
					nomeProfissional.append(
							rapServidoresVO.getTituloMasculinoVinculo())
							.append(' ');
				} else if (rapServidoresVO.getTituloMasculinoConselho() != null) {
					nomeProfissional.append(
							rapServidoresVO.getTituloMasculinoConselho())
							.append(' ');
				}
			} else {
				if (rapServidoresVO.getTituloFemininoVinculo() != null) {
					nomeProfissional.append(
							rapServidoresVO.getTituloFemininoVinculo()).append(
							' ');
				} else if (rapServidoresVO.getTituloFemininoConselho() != null) {
					nomeProfissional.append(
							rapServidoresVO.getTituloFemininoConselho())
							.append(' ');
				}
			}
			nomeProfissional.append(rapServidoresVO.getNome());

			rapServidoresVO.setNomeProfissional(WordUtils
					.capitalize(nomeProfissional.toString().toLowerCase()));
		}

		return lista;
	}

	public List<AipPacientes> pesquisaPacientes(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc,
			Integer numeroProntuario, Date periodoAltaInicio,
			Date periodoAltaFim, Date periodoConsultaInicio,
			Date periodoConsultaFim, Date periodoCirurgiaInicio,
			Date periodoCirurgiaFim, String nomePaciente,
			AghEquipes equipeMedica, AghEspecialidades especialidade,
			FccCentroCustos servico, AghUnidadesFuncionais unidadeFuncional,
			MbcProcedimentoCirurgicos procedimentoCirurgico, String leito)
			throws ApplicationBusinessException {
		this.validaDadosPesquisaPacientes(numeroProntuario, periodoAltaInicio,
				periodoAltaFim, periodoConsultaInicio, periodoConsultaFim,
				periodoCirurgiaInicio, periodoCirurgiaFim, nomePaciente,
				equipeMedica, especialidade, servico, unidadeFuncional,
				procedimentoCirurgico, leito);

		AinLeitos ainLeito = null;

		if (StringUtils.isNotBlank(leito)) {
			ainLeito = this.obterLeito(leito);

			if (ainLeito == null) {
				throw new ApplicationBusinessException(
						PacienteONExceptionCode.MENSAGEM_LEITO_INVALIDO);
			}
		}

		List<String> fonemasPaciente = null;
		if (StringUtils.isNotBlank(nomePaciente)) {
			fonemasPaciente = this.fonemasPacienteRN.obterFonemasNaOrdem(
					nomePaciente);
		}

		return this.aipPacientesDAO.pesquisaPacientes(firstResult,
				maxResults, orderProperty, asc, numeroProntuario,
				periodoAltaInicio, periodoAltaFim, periodoConsultaInicio,
				periodoConsultaFim, periodoCirurgiaInicio, periodoCirurgiaFim,
				nomePaciente, equipeMedica, especialidade, servico,
				unidadeFuncional, procedimentoCirurgico, leito, ainLeito,
				fonemasPaciente);
	}

	public Long pesquisaPacientesCount(Integer numeroProntuario,
			Date periodoAltaInicio, Date periodoAltaFim,
			Date periodoConsultaInicio, Date periodoConsultaFim,
			Date periodoCirurgiaInicio, Date periodoCirurgiaFim,
			String nomePaciente, AghEquipes equipeMedica,
			AghEspecialidades especialidade, FccCentroCustos servico,
			AghUnidadesFuncionais unidadeFuncional,
			MbcProcedimentoCirurgicos procedimentoCirurgico, String leito)
			throws ApplicationBusinessException {
		this.validaDadosPesquisaPacientes(numeroProntuario, periodoAltaInicio,
				periodoAltaFim, periodoConsultaInicio, periodoConsultaFim,
				periodoCirurgiaInicio, periodoCirurgiaFim, nomePaciente,
				equipeMedica, especialidade, servico, unidadeFuncional,
				procedimentoCirurgico, leito);

		AinLeitos ainLeito = null;

		if (StringUtils.isNotBlank(leito)) {
			ainLeito = this.obterLeito(leito);

			if (ainLeito == null) {
				throw new ApplicationBusinessException(
						PacienteONExceptionCode.MENSAGEM_LEITO_INVALIDO);
			}
		}

		List<String> fonemasPaciente = null;
		if (StringUtils.isNotBlank(nomePaciente)) {
			fonemasPaciente = this.fonemasPacienteRN.obterFonemasNaOrdem(
					nomePaciente);
		}

		return this.aipPacientesDAO.pesquisaPacientesCount(
				numeroProntuario, periodoAltaInicio, periodoAltaFim,
				periodoConsultaInicio, periodoConsultaFim,
				periodoCirurgiaInicio, periodoCirurgiaFim, nomePaciente,
				equipeMedica, especialidade, servico, unidadeFuncional,
				procedimentoCirurgico, leito, ainLeito, fonemasPaciente);
	}

	@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
	public void validaDadosPesquisaPacientes(Integer numeroProntuario,
			Date periodoAltaInicio, Date periodoAltaFim,
			Date periodoConsultaInicio, Date periodoConsultaFim,
			Date periodoCirurgiaInicio, Date periodoCirurgiaFim,
			String nomePaciente, AghEquipes equipeMedica,
			AghEspecialidades especialidade, FccCentroCustos servico,
			AghUnidadesFuncionais unidadeFuncional,
			MbcProcedimentoCirurgicos procedimentoCirurgico, String leito)
			throws ApplicationBusinessException {
		if (numeroProntuario == null && periodoAltaInicio == null
				&& periodoAltaFim == null && periodoConsultaInicio == null
				&& periodoConsultaFim == null && periodoCirurgiaInicio == null
				&& periodoCirurgiaFim == null
				&& StringUtils.isBlank(nomePaciente) && equipeMedica == null
				&& especialidade == null && servico == null
				&& unidadeFuncional == null && procedimentoCirurgico == null
				&& StringUtils.isBlank(leito)) {
			throw new ApplicationBusinessException(
					PacienteONExceptionCode.MENSAGEM_INFORMAR_CRITERIOS);
		}

		if (StringUtils.isNotBlank(leito)) {
			AinLeitos ainLeito = this.obterLeito(leito);

			if (ainLeito == null) {
				throw new ApplicationBusinessException(
						PacienteONExceptionCode.MENSAGEM_LEITO_INVALIDO);
			}
		}

		this.datasValidas(periodoAltaInicio, periodoAltaFim);
		this.datasValidas(periodoConsultaInicio, periodoConsultaFim);
		this.datasValidas(periodoCirurgiaInicio, periodoCirurgiaFim);

		boolean periodoAltaInformado = false;
		boolean periodoConsultaInformado = false;
		boolean periodoCirurgiaInformado = false;
		int count = 0;

		if (periodoAltaInicio != null && periodoAltaFim != null) {
			periodoAltaInformado = true;
			count++;
		}

		if (periodoConsultaInicio != null && periodoConsultaFim != null) {
			periodoConsultaInformado = true;
			count++;
		}

		if (periodoCirurgiaInicio != null && periodoCirurgiaFim != null) {
			periodoCirurgiaInformado = true;
			count++;
		}

		if (numeroProntuario == null && StringUtils.isBlank(nomePaciente)
				&& count == 0) {
			throw new ApplicationBusinessException(
					PacienteONExceptionCode.MENSAGEM_INFORMAR_DADOS_MINIMOS);
		}

		if (count > 1) {
			throw new ApplicationBusinessException(
					PacienteONExceptionCode.MENSAGEM_INFORMAR_APENAS_UM_PERIODO);
		}

		if (procedimentoCirurgico != null && !periodoCirurgiaInformado) {
			throw new ApplicationBusinessException(
					PacienteONExceptionCode.MENSAGEM_INFORMAR_PERIODO_CIRURGIA);
		}

		Calendar calPeriodoInicial = Calendar.getInstance();
		Calendar calPeriodoFinal = Calendar.getInstance();

		if (periodoAltaInformado) {
			AghParametros parametroDiasPesquisaInternacao = this
					.parametroFacade.buscarAghParametro(
							AghuParametrosEnum.P_DIAS_PESQ_INTERN);
			Integer quantidadeDiasPesquisaInternacao = parametroDiasPesquisaInternacao
					.getVlrNumerico().intValue();

			calPeriodoInicial.setTime(periodoAltaInicio);
			calPeriodoFinal.setTime(periodoAltaFim);
			calPeriodoInicial.add(Calendar.DAY_OF_MONTH,
					quantidadeDiasPesquisaInternacao);

			if (calPeriodoFinal.after(calPeriodoInicial)) {
				throw new ApplicationBusinessException(
						PacienteONExceptionCode.MENSAGEM_DIFERENCA_DATAS_PERIODO_ALTA,
						quantidadeDiasPesquisaInternacao);
			}
		}

		if (periodoConsultaInformado) {
			AghParametros parametroDiasPesquisaConsulta = this
					.parametroFacade.buscarAghParametro(
							AghuParametrosEnum.P_DIAS_PESQ_CONS);
			Integer quantidadeDiasPesquisaConsulta = parametroDiasPesquisaConsulta
					.getVlrNumerico().intValue();

			calPeriodoInicial.setTime(periodoConsultaInicio);
			calPeriodoFinal.setTime(periodoConsultaFim);
			calPeriodoInicial.add(Calendar.DAY_OF_MONTH,
					quantidadeDiasPesquisaConsulta);

			if (calPeriodoFinal.after(calPeriodoInicial)) {
				throw new ApplicationBusinessException(
						PacienteONExceptionCode.MENSAGEM_DIFERENCA_DATAS_PERIODO_CONSULTA,
						quantidadeDiasPesquisaConsulta);
			}
		}

		if (periodoCirurgiaInformado) {
			AghParametros parametroDiasPesquisaCirurgia = this
					.parametroFacade.buscarAghParametro(
							AghuParametrosEnum.P_DIAS_PESQ_CIRURG);
			Integer quantidadeDiasPesquisaCirurgia = parametroDiasPesquisaCirurgia
					.getVlrNumerico().intValue();

			calPeriodoInicial.setTime(periodoCirurgiaInicio);
			calPeriodoFinal.setTime(periodoCirurgiaFim);
			calPeriodoInicial.add(Calendar.DAY_OF_MONTH,
					quantidadeDiasPesquisaCirurgia);

			if (calPeriodoFinal.after(calPeriodoInicial)) {
				throw new ApplicationBusinessException(
						PacienteONExceptionCode.MENSAGEM_DIFERENCA_DATAS_PERIODO_CIRURGIA,
						quantidadeDiasPesquisaCirurgia);
			}
		}
	}

	public AinLeitos obterLeito(String leito) {
		try {
			return this.internacaoFacade.obterAinLeitosPorChavePrimaria(
					leito);
		} catch (Exception e) {
			logError("Exceção capturada: ", e);
			return null;
		}
	}

	/*
	 * Valida se as datas respeitam as seguintes regras: ou ambas datas são
	 * nulas, ou ambas devem ser informadas e a data inicial deve ser menor que
	 * a data final
	 */
	private void datasValidas(Date dataInicial, Date dataFinal)
			throws ApplicationBusinessException {
		if (dataInicial != null || dataFinal != null) {
			if (dataInicial == null) {
				throw new ApplicationBusinessException(
						PacienteONExceptionCode.MENSAGEM_INFORME_DATA_INICIAL);
			} else if (dataFinal == null) {
				throw new ApplicationBusinessException(
						PacienteONExceptionCode.MENSAGEM_INFORME_DATA_FINAL);
			} else if (dataInicial.after(dataFinal)) {
				throw new ApplicationBusinessException(
						PacienteONExceptionCode.MENSAGEM_DATA_FINAL_MAIOR_IGUAL_DATA_INICIAL);
			}
		}
	}

	public AipPacientes carregarInformacoesEdicao(final AipPacientes paciente) {
		AipPacientes pacienteRetorno =  aipPacientesDAO.obterPorChavePrimaria(paciente.getCodigo(), true, AipPacientes.Fields.MAE_PACIENTE);
		
		aipPacientesDAO.refresh(pacienteRetorno);
		
		Hibernate.initialize(pacienteRetorno.getAipPacientesDadosCns());
		if(pacienteRetorno.getAipPacientesDadosCns()!= null){
			Hibernate.initialize(pacienteRetorno.getAipPacientesDadosCns().getAipOrgaosEmissor());
		}
		
		//Hibernate.initialize(pacienteRetorno.getMaePaciente());
		Hibernate.initialize(pacienteRetorno.getAipOcupacoes());
		Hibernate.initialize(pacienteRetorno.getAipNacionalidades());
		Hibernate.initialize(pacienteRetorno.getAipCidades());
		Hibernate.initialize(pacienteRetorno.getEtnia());
		pacienteRetorno.setEnderecos(new HashSet<>(enderecoPacienteDAO.obterEnderecosCompletosPaciente(pacienteRetorno.getCodigo())));
		return pacienteRetorno;
	}

	/**
	 * Atualizar volume de paciente.
	 */
	public void atualizaVolume(Integer codPaciente, Short volume) {
		AipPacientesDAO aipPacientesDAO = this.aipPacientesDAO;

		AipPacientes paciente = this.obterPaciente(codPaciente);
		if (paciente != null) {
			paciente.setVolumes(volume);

			if (!aipPacientesDAO.contains(paciente)) {
				aipPacientesDAO.merge(paciente);
			}

			aipPacientesDAO.flush();
		}
	}

	/**
	 * Pesquisa o nome do paciente buscando pelo numero do prontuario
	 * 
	 * @param prontuario
	 * @return nomePaciente
	 */
	public String pesquisarNomePaciente(Integer prontuario) {
		return this.aipPacientesDAO.pesquisarNomePaciente(prontuario);
	}

	public Date obterMaxDataCriadoEmPesoPaciente(Integer codigoPaciente) {
		return this.aipPesoPacientesDAO.obterMaxDataCriadoEmPesoPaciente(
				codigoPaciente);
	}

	public boolean verificarUtilizacaoDigitoVerificadorProntuario() {
		return this.isHCPA();
	}

	/**
	 * Método usado para obter todos os convênios (AipConveniosSaudePaciente) de
	 * um paciente.
	 * 
	 * @dbtables AipConveniosSaudePaciente select
	 * 
	 * @param paciente
	 * @return
	 */
	public List<AipConveniosSaudePaciente> pesquisarConveniosPaciente(AipPacientes paciente) {
		return aipConveniosSaudePacienteDAO.pesquisarConveniosPaciente(paciente);
	}

	
	public AinAtendimentosUrgencia ingressarPacienteEmergenciaObstetricaSalaObservacao(Integer numeroConsulta, Integer codigoPaciente, String nomeMicrocomputador)
			throws NumberFormatException, BaseException {

		Integer seqAtendimento = aghAtendimentoDAO.obterAtendimentoPorConNumero(numeroConsulta);

		AghAtendimentos atendimento = aghAtendimentoDAO.obterAtendimentoPeloSeq(seqAtendimento);

		// RN01 #26857
		if (atendimento == null) {
			throw new ApplicationBusinessException(PacienteONExceptionCode.AIN_00812_1);
		}

		// RN01 #26857
		if (possuiAtendimentoUrgencia(atendimento.getAtendimentoUrgencia()) || possuiInternacao(atendimento.getInternacao())) {
			throw new ApplicationBusinessException(PacienteONExceptionCode.ERRO_PAC_INT_SO);
		}

		// RN01 #26857
		Integer quantidadeDiasIngressoSO = parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_DIAS_CONS_INGR_SO);
		if (DateUtil.obterQtdDiasEntreDuasDatas(atendimento.getDthrInicio(), new Date()) > quantidadeDiasIngressoSO) {
			throw new ApplicationBusinessException(PacienteONExceptionCode.ERRO_QTD_DIAS_INGRESSO_SO, quantidadeDiasIngressoSO);
		}

		// RN01 #26857 especialidade possui característica de “Ingresso SO”?
		List<AghCaractEspecialidades> listapPossuiCaracteristicaEspecialidade = aghCaractEspecialidadesDAO.pesquisarCaracteristicaEspecialidade(
				atendimento.getEspecialidade().getSeq(), DominioCaracEspecialidade.INGRESSO_SO);
		if (listapPossuiCaracteristicaEspecialidade != null && listapPossuiCaracteristicaEspecialidade.isEmpty()) {
			throw new ApplicationBusinessException(PacienteONExceptionCode.ERRO_ATENDIMENTO_ESPECIALIDADE_EMERGENCIA);
		}

		AinAtendimentosUrgencia atendimentoUrgencia = this.internacaoFacade.persistirAtendimentoUrgente(numeroConsulta, codigoPaciente, nomeMicrocomputador);

		return atendimentoUrgencia;
	}

	private Boolean possuiAtendimentoUrgencia(AinAtendimentosUrgencia urgencia) {
		if (urgencia == null) {
			return Boolean.FALSE;
		} else if (urgencia != null && urgencia.getSeq() != null) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}

	private Boolean possuiInternacao(AinInternacao internacao) {
		if (internacao == null) {
			return Boolean.FALSE;
		} else if (internacao != null && internacao.getSeq() != null) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}
	
	/**#42803
	 * 
	 * ORADB c_ver_pac_implante / busca_cirurgia
	 * 
	 * @return vCirg
	 */

	public Character verificarPacienteImplante(Integer pacCodigo, Date dtRealizado, CirurgiaVO cirurgiaVO){
		MbcCirurgias mbcCirurgias = mbcCirurgiasDAO.buscarCirurgia(pacCodigo, dtRealizado); 
		if(mbcCirurgias != null){
			cirurgiaVO.setCrgData(mbcCirurgias.getData());
			return 'S';
		}else{
			cirurgiaVO.setCrgData(null);
			return 'N';
		}
	}
	
	/**
	 * Web Service #40707
	 * 
	 * Atualizar a data de nascimento com a Data de Nascimento do Recém-Nascido na tabela AIP_PACIENTES
	 * 
	 * @param pacCodigo
	 * @param dthrNascimento
	 */
	public void atualizarPacienteDthrNascimento(final Integer pacCodigo, final Date dthrNascimento){
		AipPacientes paciente = aipPacientesDAO.obterPorChavePrimaria(pacCodigo);
		if(paciente != null){
			paciente.setDtNascimento(dthrNascimento);
			aipPacientesDAO.atualizar(paciente);
		}
	}

	public String verificarAtualizacaoPaciente(String nomePaciente, Integer numeroConsulta) throws ApplicationBusinessException {
		List<AipPacientes> listaPacientes = this.pesquisarPorFonemas(null, null, nomePaciente, null, false, null, null);
		
		if (listaPacientes == null || listaPacientes.isEmpty()) {
			return "NENHUM_PACIENTE_ENCONTRADO";
		} else if (listaPacientes.size() == 1) {
			AipPacientes pacienteObtido = listaPacientes.get(0);
			if ((pacienteObtido.getProntuario() != null)
					&& (pacienteObtido.getCadConfirmado() != null && pacienteObtido
							.getCadConfirmado().equals(DominioSimNao.S))) {
				//atualizar AacConsulta
				atualizarPacienteConsulta(pacienteObtido, numeroConsulta);
				return null;
			} else {
				return "UM_PACIENTE_ENCONTRADO";
			}
		} else {
			return "VARIOS_PACIENTES_ENCONTRADOS";
		}
	}
	
	public void atualizarPacienteConsulta(AipPacientes paciente, Integer numeroConsulta) {
		AacConsultas aacConsultas = aacConsultasDAO.obterPorChavePrimaria(numeroConsulta);
		aacConsultas.setPaciente(paciente);
		aacConsultasDAO.atualizar(aacConsultas);
	}

	public void persistirProcedimentoReanimacao(Integer seq,
			String componenteSeq,
			Integer matCodigo,
			String descricao,
			DominioSituacao situacao) throws ApplicationBusinessException{
		
		AbsComponenteSanguineo componente = null;
		AfaMedicamento medicamento = null;
		if(componenteSeq != null){
			componente = absComponenteSanguineoDAO.obterPorChavePrimaria(componenteSeq);
		}
		if(matCodigo != null){
			medicamento = afaMedicamentoDAO.obterPorChavePrimaria(matCodigo);
		}
		
		if(seq == null){
			McoProcReanimacao entity = new	McoProcReanimacao();
			entity.setDescricao(descricao);
			entity.setCriadoEm(new Date());
			entity.setIndSituacao(situacao);
			entity.setAbsComponenteSanguineo(componente);
			entity.setAfaMedicamento(medicamento);
			entity.setRapServidores(servidorLogadoFacade.obterServidorLogado());
			mcoProcReanimacaoDAO.persistir(entity);
		}else{
			McoProcReanimacao entity = mcoProcReanimacaoDAO.obterPorChavePrimaria(seq);
			entity.setDescricao(descricao);
			entity.setIndSituacao(situacao);
			entity.setAbsComponenteSanguineo(componente);
			entity.setAfaMedicamento(medicamento);
			entity.setRapServidores(servidorLogadoFacade.obterServidorLogado());
			mcoProcReanimacaoDAO.merge(entity);
		}
	}
	
	public void persistirSindrome(String descricao, String situacao) {
		McoSindrome entity = new McoSindrome();
		entity.setDescricao(descricao);
		entity.setIndSituacao(situacao);
		entity.setRapServidores(servidorLogadoFacade.obterServidorLogado());
		entity.setCriadoEm(new Date());
		mcoSindromeDAO.persistir(entity);
}
	
	public void ativarInativarSindrome(Integer seq){
		McoSindrome entity = mcoSindromeDAO.obterPorChavePrimaria(seq);
		String ativo = "A";
		String inativo = "I";
		if(ativo.equals(entity.getIndSituacao())){
			entity.setIndSituacao(inativo);
		}else{
			entity.setIndSituacao(ativo);
		}
		entity.setRapServidores(servidorLogadoFacade.obterServidorLogado());
		mcoSindromeDAO.merge(entity);
	}
	
	public void persistirBallard(Short seq, Short escore, Short igSemanas) {
		if(seq == null){
			McoTabBallard entity = new McoTabBallard();
			entity.setEscoreBallard(escore);
			entity.setIgSemanas(igSemanas);
			entity.setRapServidores(servidorLogadoFacade.obterServidorLogado());
			entity.setCriadoEm(new Date());
			mcoTabBallardDAO.persistir(entity);
		}else{
			McoTabBallard entity = mcoTabBallardDAO.obterPorChavePrimaria(seq);
			entity.setEscoreBallard(escore);
			entity.setIgSemanas(igSemanas);
			entity.setRapServidores(servidorLogadoFacade.obterServidorLogado());
			mcoTabBallardDAO.merge(entity);
		}
		
	}

	public void atualizarAtendimentoGestante(Integer gsoPacCodigo, Short gsoSeqp, 
			String nomeMicroComputador, Integer atdSeq) throws BaseException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();	
		AghAtendimentos atendimento = this.aghuFacade.obterAtendimentoPeloSeq(atdSeq);
		atendimento.setGsoPacCodigo(gsoPacCodigo);
		atendimento.setGsoSeqp(gsoSeqp);
		
		AghAtendimentos atendimentoOld = this.aghuFacade.obterAtendimentoOriginal(atdSeq);
		
		this.atendimentosRN.atualizarAtendimento(atendimento, atendimentoOld, nomeMicroComputador, servidorLogado, null);
	}
	
}
