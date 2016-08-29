package br.gov.mec.aghu.prescricaomedica.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.ProfessorCrmInternacaoVO;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MpmListaPacCpa;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class ConfigurarListaPacientesController extends ActionController {

	private static final long serialVersionUID = 2829009439047251334L;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;

    @Inject
    private ListaPacientesInternadosController listaPacienteInternadoController;
	

	private RapServidores servidor;

	private List<AghEspecialidades> listaServEspecialidades;
	private AghEspecialidades especialidade, especialidadeSelecionada;
	private static EspecialidadeComparator especialidadeComparator = new EspecialidadeComparator();
	private static EspecialidadeNameComparator especialidadeNameComparator = new EspecialidadeNameComparator();

	private List<AghEquipes> listaServEquipes;
	private AghEquipes equipe, equipeSelecionada;
	private static EquipeComparator equipeComparator = new EquipeComparator();
	
	private static ResponsavelComparator responsavelComparator = new ResponsavelComparator();

	private List<AghUnidadesFuncionais> listaServUnFuncionais;
	private AghUnidadesFuncionais unidadeFuncional,
	unidadeFuncionalSelecionada;
	private static UnidadeFuncionalComparator unidadeComparator = new UnidadeFuncionalComparator();

	private List<AghAtendimentos> listaServAtendimentos;
	private List<AghAtendimentos> listaAtendimentosPacientesPesquisa = new ArrayList<AghAtendimentos>();
	private AghAtendimentos atendimentoSelecionado;
	private Integer numeroProntuario;
	private static AtendimentoComparator atendimentoComparator = new AtendimentoComparator();
	private String nomePesquisaFonetica;
	private String leitoPesquisaFonetica;
	private String quartoPesquisaFonetica;
	private AghUnidadesFuncionais unidadeFuncionalPesquisaFonetica,	unidadeFuncionalPesquisaFoneticaSelecionada;
	private AghAtendimentos atendimentoTeste;

	private boolean incluirPacientesCuidadosPosAnestesicos = false;
	private boolean incluirPacientesCuidadosPosAnestesicosCarregado = false;
	private final String PAGE_LISTAR_PACIENTES_INTERNADOS = "pesquisarListaPacientesInternados";
	private final String PAGE_LISTAR_PACIENTES_ENFERMAGEM = "prescricaoenfermagem-listaPacientesEnfermagem";
	private final String PAGE_PESQUISA_FONETICA_PRESCRICAO = "pesquisaFoneticaPrescricao";
	
	/* CRM PROFESSOR */
	private ProfessorCrmInternacaoVO responsavel, responsavelSelecionado;
	
	private List<ProfessorCrmInternacaoVO> listaProfessorCrmInternacaoVO;

	private boolean confirmaVoltar = false;
	
	private String cameFrom;
		
	private String leitoID;
	
	@Inject
    private PesquisaFoneticaPrescricaoController pesquisaFoneticaPrescricaoController;
	
	private final String PAGE_CONFIGURAR_LISTA_PACIENTES= "prescricaomedica-configurarListaPacientes";

	@PostConstruct
	public void init() {
		begin(conversation);
		this.carregar();
	}
		
	public void carregar() {
		setServidor(servidorLogadoFacade.obterServidorLogadoSemCache());
		
		// método para carregar a lista de especialidades
		try {
			listaServEspecialidades = getPrescricaoMedicaFacade()
			.getListaEspecialidades(servidor);
			Collections.sort(listaServEspecialidades, especialidadeComparator);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		// método para carregar a lista de equipes
		try {
			listaServEquipes = getPrescricaoMedicaFacade().getListaEquipes(
					servidor);
			Collections.sort(listaServEquipes, equipeComparator);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		// método para carregar a lista de responsáveis
		try {
			listaProfessorCrmInternacaoVO = getPrescricaoMedicaFacade().getListaResponsaveis(
					servidor);
			Collections.sort(listaProfessorCrmInternacaoVO, responsavelComparator);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

		// método para carregar a lista de unidades funcionais
		try {
			listaServUnFuncionais = getPrescricaoMedicaFacade()
			.getListaUnidadesFuncionais(servidor);
			Collections.sort(listaServUnFuncionais, unidadeComparator);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

		// método para carregar as listas de pacientes atendimento
		try {
			setListaServAtendimentos(getPrescricaoMedicaFacade()
					.getListaAtendimentos(servidor));
			Collections.sort(listaServAtendimentos, atendimentoComparator);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

		// carrega valor de pacientes em cuidados pos anestesicos
		try {
			MpmListaPacCpa cpas = getPrescricaoMedicaFacade()
			.getPacienteCuidadosPosAnestesicos(servidor);
			if (cpas != null) {
				incluirPacientesCuidadosPosAnestesicos = cpas.getIndPacCpa();
				incluirPacientesCuidadosPosAnestesicosCarregado = incluirPacientesCuidadosPosAnestesicos;
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		setConfirmaVoltar(false);
	}

	/**
	 * Salva as alterações
	 * 
	 * @return
	 */
	public void salvar() {
		try {
			// salva lista de especialidades
			getPrescricaoMedicaFacade().salvarListaEspecialidades(
					getListaServEspecialidades(), getServidor());
			// salva lista de equipes
			getPrescricaoMedicaFacade().salvarListaEquipes(
					getListaServEquipes(), getServidor());
			// salva lista de responsáveis
			getPrescricaoMedicaFacade().salvarListaResponsaveis(
					getListaProfessorCrmInternacaoVO(), getServidor());
			
			// salva lista de un funcionais
			getPrescricaoMedicaFacade().salvarListaUnidadesFuncionais(
					getListaServUnFuncionais(), getServidor());
			// salva lista de pacientes atendimento
			getPrescricaoMedicaFacade().salvarListaAtendimentos(
					getListaServAtendimentos(), getServidor());

			// salva indicador lista pacientes atendimento
			getPrescricaoMedicaFacade().salvarIndicadorPacientesAtendimento(
					isIncluirPacientesCuidadosPosAnestesicos(), getServidor());

			limpar();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		apresentarMsgNegocio(Severity.INFO,	"MENSAGEM_LISTA_PACIENTES_CONFIGURADA_SUCESSO");
	}

	/**
	 * Retorna os valores da tela ao conteúdo do banco de dados
	 */
	public void limpar() {
		carregar();
		setEspecialidade(null);
		setConfirmaVoltar(false);
		setEquipe(null);
		setResponsavel(null);
		setUnidadeFuncional(null);
		setNumeroProntuario(null);
		setAtendimentoSelecionado(null);
		setLeitoID(null);
		listaAtendimentosPacientesPesquisa = new ArrayList<AghAtendimentos>();
	}

	/**
	 * Método da suggestion box para pesquisa de especialidades a incluir na
	 * lista Exclui da listagem os itens que já estão na tela Ignora a pesquisa
	 * caso o parametro seja o próprio valor selecionado anteriormente (contorna
	 * falha de pesquisa múltipla na suggestion box)
	 * 
	 * @param parametro
	 * @return
	 */
	public List<AghEspecialidades> pesquisarEspecialidades(String parametro) {
		String paramString = (String) parametro;
//		//debug("ConfigurarListaPacientesController.pesquisarEspecialidades(): parametro = ["
	//			+ parametro + "].");
		Set<AghEspecialidades> result = new HashSet<AghEspecialidades>();
		if ((especialidadeSelecionada == null)
				|| !(StringUtils.equalsIgnoreCase(paramString,
						especialidadeSelecionada.getSigla()) || StringUtils
						.equalsIgnoreCase(paramString, especialidadeSelecionada
								.getNomeEspecialidade()))) {
			try {
				result = new HashSet<AghEspecialidades>(
						getPrescricaoMedicaFacade().getListaEspecialidades(
								paramString));
				// result.removeAll(getListaServEspecialidades());
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		} else {
			// adiciona a especialidade selecionada para nao mostrar mensagens
			// erradas na tela
			result.add(especialidadeSelecionada);
		}
		List<AghEspecialidades> resultReturn = new ArrayList<AghEspecialidades>(
				result);
		Collections.sort(resultReturn, especialidadeNameComparator);
		return resultReturn;
	}

	/**
	 * Método que exclui uma especialidade da lista em memória Ignora nulos
	 * 
	 * @param espParaExcluir
	 */
	public void excluirEspecialidade(AghEspecialidades espParaExcluir) {
//		//debug("ConfigurarListaPacientesController.excluirEspecialidade(): Entrando.");
		if (espParaExcluir != null) {
	//		//debug("ConfigurarListaPacientesController.excluirEspecialidade(): Especialidade = ["
		//			+ (espParaExcluir != null ? espParaExcluir.getSigla() : "")
			//		+ "].");
			listaServEspecialidades.remove(espParaExcluir);
			setConfirmaVoltar(true);
		}
		////debug("ConfigurarListaPacientesController.excluirEspecialidade(): Saindo.");
	}

	/**
	 * Adiciona uma especialidade na lista em memória.<br>
	 * Caso a especialidade já esteja na lista mostra mensagem.
	 */
	public void adicionarEspecialidade() {
		////debug("ConfigurarListaPacientesController.adicionarEspecialidade(): Entrando.");
		if (getEspecialidadeSelecionada() != null) {
			if (listaServEspecialidades == null) {
				listaServEspecialidades = new ArrayList<AghEspecialidades>();
			}
			if (!listaServEspecialidades
					.contains(getEspecialidadeSelecionada())) {
			//	//debug("ConfigurarListaPacientesController.adicionarEspecialidade(): Especialidade = ["
				//		+ (getEspecialidadeSelecionada() != null ? getEspecialidadeSelecionada()
					//			.getSigla()
						//		: "") + "].");
				listaServEspecialidades.add(getEspecialidadeSelecionada());
				Collections.sort(listaServEspecialidades,
						especialidadeComparator);
				setEspecialidade(null);
				setEspecialidadeSelecionada(null);
				setConfirmaVoltar(true);
			} else {
				apresentarMsgNegocio(Severity.WARN,
				"MENSAGEM_CONFIG_LISTA_ESPECIALIDADE_JA_ADICIONADA");
			}
		}
		////debug("ConfigurarListaPacientesController.adicionarEspecialidade(): Saindo.");
	}

	public void setSelecionouEspecialidade() {
		setEspecialidadeSelecionada(getEspecialidade());
	}

	/**
	 * Método da suggestion box para pesquisa de equipes a incluir na lista
	 * Exclui da listagem os itens que já estão na tela Ignora a pesquisa caso o
	 * parametro seja o próprio valor selecionado anteriormente (contorna falha
	 * de pesquisa múltipla na suggestion box)
	 * 
	 * @param parametro
	 * @return
	 */
	public List<AghEquipes> pesquisarEquipes(String parametro) {
		String paramString = (String) parametro;
		//getLog().info("ConfigurarListaPacientesController.pesquisarEquipes(): parametro = ["+ parametro + "].");
		Set<AghEquipes> result = new HashSet<AghEquipes>();
		if ((equipeSelecionada == null)
				|| !(StringUtils.equalsIgnoreCase(paramString, String
						.valueOf(equipeSelecionada.getSeq())) || StringUtils
						.equalsIgnoreCase(paramString, String
								.valueOf(equipeSelecionada.getNome())))) {
			try {
				result = new HashSet<AghEquipes>(getPrescricaoMedicaFacade()
						.getListaEquipes(paramString));
				// result.removeAll(getListaServEquipes());
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		} else {
			// adiciona a selecionada para nao mostrar mensagens erradas na tela
			result.add(equipeSelecionada);
		}
		List<AghEquipes> resultReturn = new ArrayList<AghEquipes>(result);
		Collections.sort(resultReturn, equipeComparator);
		return resultReturn;
	}

	/**
	 * Método que exclui uma equipe da lista em memória Ignora nulos
	 * 
	 * @param espParaExcluir
	 */
	public void excluirEquipe(AghEquipes eqParaExcluir) {
//		//debug("ConfigurarListaPacientesController.excluirEquipe(): Entrando.");
		if (eqParaExcluir != null) {
	//		//debug("ConfigurarListaPacientesController.excluirEquipe(): Equipe = ["
		//			+ (eqParaExcluir != null ? eqParaExcluir.getNome() : "")
			//		+ "].");
			listaServEquipes.remove(eqParaExcluir);
			setConfirmaVoltar(true);
		}
		////debug("ConfigurarListaPacientesController.excluirEquipe(): Saindo.");
	}
	
	/**
	 * Método que exclui um responsável da lista em memória Ignora nulos
	 * 
	 * @param espParaExcluir
	 */
	public void excluirResponsavel(ProfessorCrmInternacaoVO professorCrmInternacaoVO) {
		if (professorCrmInternacaoVO != null) {
			listaProfessorCrmInternacaoVO.remove(professorCrmInternacaoVO);
			setConfirmaVoltar(true);
		}
	}

	/**
	 * Adiciona uma equipe na lista em memória Caso a especialidade já esteja na
	 * lista, ou seja nula, ignora
	 */
	public void adicionarEquipe() {
		//debug("ConfigurarListaPacientesController.adicionarEquipe(): Entrando.");
		if (getEquipeSelecionada() != null) {
			if (listaServEquipes == null) {
				listaServEquipes = new ArrayList<AghEquipes>();
			}
			if (!listaServEquipes.contains(getEquipeSelecionada())) {
				//debug("ConfigurarListaPacientesController.adicionarEquipe(): Equipe = ["
						//+ (getEquipeSelecionada() != null ? getEquipeSelecionada()
							//	.getNome()
								//: "") + "].");
				listaServEquipes.add(getEquipeSelecionada());
				Collections.sort(listaServEquipes, equipeComparator);
				setEquipe(null);
				setEquipeSelecionada(null);
				setConfirmaVoltar(true);
			} else {
				apresentarMsgNegocio(Severity.WARN,
				"MENSAGEM_CONFIG_LISTA_EQUIPE_JA_ADICIONADA");
			}

		}
		//debug("ConfigurarListaPacientesController.adicionarEquipe(): Saindo.");
	}
	
	/**
	 * Adiciona um responsável na lista em memória Caso a especialidade já esteja na
	 * lista, ou seja nula, ignora
	 */
	public void adicionarResponsavel() {
		if (getResponsavelSelecionado() != null) {
			if (listaProfessorCrmInternacaoVO == null) {
				listaProfessorCrmInternacaoVO = new ArrayList<ProfessorCrmInternacaoVO>();
			}
			if (!listaProfessorCrmInternacaoVO.contains(getResponsavelSelecionado())) {
				listaProfessorCrmInternacaoVO.add(getResponsavelSelecionado());
				Collections.sort(listaProfessorCrmInternacaoVO, responsavelComparator);
				setResponsavel(null);
				setResponsavelSelecionado(null);
				setConfirmaVoltar(true);
			} else {
				apresentarMsgNegocio(Severity.WARN,
				"MENSAGEM_CONFIG_LISTA_RESPONS_JA_ADICIONADO");
			}

		}
	}

	public void setSelecionouEquipe() {
		setEquipeSelecionada(getEquipe());
	}
	
	public void setSelecionouResponsavel() {
		setResponsavelSelecionado(getResponsavel());
	}

	/**
	 * Método da suggestion box para pesquisa de unidades funcionais a incluir
	 * na lista Exclui da listagem os itens que já estão na tela Ignora a
	 * pesquisa caso o parametro seja o próprio valor selecionado anteriormente
	 * (contorna falha de pesquisa múltipla na suggestion box)
	 * 
	 * @param parametro
	 * @return
	 */
	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionais(
			String parametro) {
		String paramString = (String) parametro;
		//debug("ConfigurarListaPacientesController.pesquisarUnidadesFuncionais(): parametro = ["
				//+ parametro + "].");
		Set<AghUnidadesFuncionais> result = new HashSet<AghUnidadesFuncionais>();
		if ((unidadeFuncionalSelecionada == null)
				|| !(StringUtils.equalsIgnoreCase(paramString, String
						.valueOf(unidadeFuncionalSelecionada.getSeq())) || StringUtils
						.equalsIgnoreCase(paramString,
								unidadeFuncionalSelecionada
								.getLPADAndarAlaDescricao()))) {
			try {
				result = new HashSet<AghUnidadesFuncionais>(
						getPrescricaoMedicaFacade().getListaUnidadesFuncionais(
								paramString));
				// result.removeAll(getListaServUnFuncionais());
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		} else {
			// adiciona a selecionada para nao mostrar mensagens erradas na tela
			result.add(unidadeFuncionalSelecionada);
		}
		List<AghUnidadesFuncionais> resultReturn = new ArrayList<AghUnidadesFuncionais>(
				result);
		Collections.sort(resultReturn, unidadeComparator);
		return resultReturn;
	}

	/**
	 * Método que exclui uma unidade funcional da lista em memória Ignora nulos
	 * 
	 * @param espParaExcluir
	 */
	public void excluirUnidadeFuncional(AghUnidadesFuncionais unidade) {
		//debug("ConfigurarListaPacientesController.excluirUnidadeFuncional(): Entrando.");
		if (unidade != null) {
			//debug("ConfigurarListaPacientesController.excluirUnidadeFuncional(): UnidadeFuncional = ["
					//+ (unidade != null ? unidade.getDescricao() : "") + "].");
			listaServUnFuncionais.remove(unidade);
			setConfirmaVoltar(true);
		}
		//debug("ConfigurarListaPacientesController.excluirUnidadeFuncional(): Saindo.");
	}

	/**
	 * Adiciona uma equipe na lista em memória Caso a especialidade já esteja na
	 * lista, ou seja nula, ignora
	 */
	public void adicionarUnidadeFuncional() {
		//debug("ConfigurarListaPacientesController.adicionarUnidadeFuncional(): Entrando.");
		if (getUnidadeFuncionalSelecionada() != null) {
			if (listaServUnFuncionais == null) {
				listaServUnFuncionais = new ArrayList<AghUnidadesFuncionais>();
			}
			if (!listaServUnFuncionais
					.contains(getUnidadeFuncionalSelecionada())) {
				//debug("ConfigurarListaPacientesController.adicionarUnidadeFuncional(): UnidadeFuncional = ["
						//+ (getUnidadeFuncionalSelecionada() != null ? getUnidadeFuncionalSelecionada()
							//	.getDescricao()
								//: "") + "].");
				listaServUnFuncionais.add(getUnidadeFuncionalSelecionada());
				Collections.sort(listaServUnFuncionais, unidadeComparator);
				setUnidadeFuncional(null);
				setUnidadeFuncionalSelecionada(null);
				setConfirmaVoltar(true);
			} else {
				apresentarMsgNegocio(Severity.WARN,
						"MENSAGEM_CONFIG_LISTA_UNIDADE_FUNCIONAL_JA_ADICIONADA");
			}
		}
		//debug("ConfigurarListaPacientesController.adicionarUnidadeFuncional(): Saindo.");
	}

	public void setSelecionouUnidadeFuncional() {
		setUnidadeFuncionalSelecionada(getUnidadeFuncional());
	}

	public void obterPacienteAtendimento() {
		AghAtendimentos atendimento=null;
		try {
			if (numeroProntuario!=null){
				AipPacientes pac = pacienteFacade.obterPacientePorProntuario(numeroProntuario);
				if(pac == null){
					apresentarMsgNegocio(Severity.ERROR, "MESSAGEM_PRONTUARIO_INVALIDO", CoreUtil.formataProntuario(numeroProntuario));
					numeroProntuario = null;
					return;
				}
				atendimento=getPrescricaoMedicaFacade().obterAtendimentoPorProntuario(numeroProntuario);
				this.leitoID = null;
				
				if (atendimento == null) {
					List<AghAtendimentos> result = getPrescricaoMedicaFacade().getListaAtendimentos(
							getNumeroProntuario());
					if (result.isEmpty()) {
						apresentarMsgNegocio(Severity.WARN,	"MENSAGEM_PACIENTE_NAO_EXISTE_CODIGO");
						return;
					} else {
						setAtendimentoSelecionado(result.get(0));
					}
				}
				
			}else if(StringUtils.isNotBlank(leitoID)){
				AinLeitos leito = internacaoFacade.obterAinLeitosPorChavePrimaria(leitoID.toUpperCase());
				if(leito == null){
					apresentarMsgNegocio(Severity.ERROR, "MESSAGEM_LEITO_INVALIDO", leitoID);
					leitoID = null;
					return;
				}
				atendimento=getPrescricaoMedicaFacade().obterAtendimentoPorLeito(leitoID);
				//this.setLeitoID(null);
				this.numeroProntuario = null;
			}
			
		} catch (ApplicationBusinessException e) {
			this.setNumeroProntuario(null);
			this.setLeitoID(null);
			apresentarExcecaoNegocio(e);
		}
		setAtendimentoSelecionado(atendimento);
	}
	
	
	
	/**
	 * Método da suggestion box para pesquisa de unidades funcionais a incluir
	 * na lista Exclui da listagem os itens que já estão na tela Ignora a
	 * pesquisa caso o parametro seja o próprio valor selecionado anteriormente
	 * (contorna falha de pesquisa múltipla na suggestion box)
	 * 
	 * @param parametro
	 * @return
	 */
	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisFonetica(
			Object parametro) {
		String paramString = (String) parametro;
		//debug("ConfigurarListaPacientesController.pesquisarUnidadesFuncionaisFonetica(): parametro = ["
				//+ parametro + "].");
		List<AghUnidadesFuncionais> result = new ArrayList<AghUnidadesFuncionais>();
		if ((unidadeFuncionalPesquisaFoneticaSelecionada == null)
				|| !(StringUtils.equalsIgnoreCase(paramString, String
						.valueOf(unidadeFuncionalPesquisaFoneticaSelecionada
								.getSeq())) || StringUtils.equalsIgnoreCase(
										paramString,
										unidadeFuncionalPesquisaFoneticaSelecionada
										.getLPADAndarAlaDescricao()))) {
			try {
				result = getPrescricaoMedicaFacade()
				.getListaUnidadesFuncionais(paramString);
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		} else {
			// adiciona a selecionada para nao mostrar mensagens erradas na tela
			result.add(unidadeFuncionalPesquisaFoneticaSelecionada);
		}
		return result;
	}

	public void setSelecionouUnidadeFuncionalFonetica() {
		setUnidadeFuncionalPesquisaFoneticaSelecionada(getUnidadeFuncionalPesquisaFonetica());
	}

	public void pesquisarAtendimentoFonetica() {
		// TODO implementar este metodo
//		getLog().info("ConfigurarListaPacientesController.pesquisarAtendimentoFonetica(): Entrando.");
		List<AghAtendimentos> resultadoPesquisaFonetica = new ArrayList<AghAtendimentos>();
		try {
			resultadoPesquisaFonetica = getPrescricaoMedicaFacade()
			.pesquisaFoneticaAtendimentos(nomePesquisaFonetica,
					leitoPesquisaFonetica, quartoPesquisaFonetica,
					unidadeFuncionalPesquisaFoneticaSelecionada);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		setListaAtendimentosPacientesPesquisa(resultadoPesquisaFonetica);
	//	getLog().info("ConfigurarListaPacientesController.pesquisarAtendimentoFonetica(): Saindo.");
	}

	/**
	 * Método que exclui um paciente em atendimento da lista em memória Ignora
	 * nulos
	 * 
	 * @param espParaExcluir
	 */
	public void excluirAtendimento(AghAtendimentos aParaExcluir) {
		//debug("ConfigurarListaPacientesController.excluirAtendimento(): Entrando.");
		if (aParaExcluir != null) {
			//debug("ConfigurarListaPacientesController.excluirAtendimento(): Atendimento = ["
		//			+ (aParaExcluir != null ? aParaExcluir.getPaciente()
			//				.getNome() : "") + "].");
			listaServAtendimentos.remove(aParaExcluir);
			setConfirmaVoltar(true);
		}
		//debug("ConfigurarListaPacientesController.excluirAtendimento(): Saindo.");
	}

	/**
	 * Adiciona uma equipe na lista em memória Caso a especialidade já esteja na
	 * lista, ou seja nula, ignora
	 */
	public void adicionarAtendimento() {
		//debug("ConfigurarListaPacientesController.adicionarAtendimento(): Entrando.");
		//setAtendimentoSelecionado(obterPacienteAtendimento(getNumeroProntuario(), getLeitoID()));
		
		/*if (getNumeroProntuario() != null
				&& getAtendimentoSelecionado() == null) {
			pesquisarAtendimento();
		}*/
		if (getAtendimentoSelecionado() != null) {
			if (listaServAtendimentos == null) {
				listaServAtendimentos = new ArrayList<AghAtendimentos>();
			}
			if (!listaServAtendimentos.contains(getAtendimentoSelecionado())) {
				//debug("ConfigurarListaPacientesController.adicionarAtendimento(): Atendimento = ["
				//		+ (getAtendimentoSelecionado() != null ? getAtendimentoSelecionado()
					//			.getPaciente().getNome()
						//		: "") + "].");
				listaServAtendimentos.add(getAtendimentoSelecionado());
				Collections.sort(listaServAtendimentos, atendimentoComparator);
				setAtendimentoSelecionado(null);
				setNumeroProntuario(null);
				setConfirmaVoltar(true);
			} else {
				apresentarMsgNegocio(Severity.WARN, "MENSAGEM_CONFIG_LISTA_ATENDIMENTO_JA_ADICIONADA");
			}
		}
		//debug("ConfigurarListaPacientesController.adicionarAtendimento(): Saindo.");
	}

	public String voltar() {
		limpar();
		if(cameFrom.equals("listaPacientesEnfermagem")){
			return PAGE_LISTAR_PACIENTES_ENFERMAGEM;	
		} else if(cameFrom.equals("listaPacientesPrescricaoMedica")){
            listaPacienteInternadoController.reiniciaBotoes();
			return PAGE_LISTAR_PACIENTES_INTERNADOS;
		}
		return "";
	}

	public String pesquisarFonetica() {
		this.pesquisaFoneticaPrescricaoController.setCameFrom(PAGE_CONFIGURAR_LISTA_PACIENTES);
		return PAGE_PESQUISA_FONETICA_PRESCRICAO;
	}
	
	public String getVerificaPendenciasVoltar() {
		this.verificarRetornoPesquisaFonetica();
		if (!isConfirmaVoltar()
				&& (incluirPacientesCuidadosPosAnestesicosCarregado != incluirPacientesCuidadosPosAnestesicos)) {
			setConfirmaVoltar(true);
		}
		if (isConfirmaVoltar()) {
			return null;
		}
		return voltar();
	}

	private void verificarRetornoPesquisaFonetica(){
		Integer numeroAtendimentosBanco = null;
		Integer numeroAtendimentosMemoria = null;
		try {
			if(getPrescricaoMedicaFacade().getListaAtendimentos(servidor)!=null){
				numeroAtendimentosBanco = getPrescricaoMedicaFacade().getListaAtendimentos(servidor).size();
			}
			if(listaServAtendimentos!=null){
				numeroAtendimentosMemoria = listaServAtendimentos.size();
			}
			if(numeroAtendimentosBanco!=null&& numeroAtendimentosMemoria!=null && !confirmaVoltar && !numeroAtendimentosBanco.equals(numeroAtendimentosMemoria)){
				setConfirmaVoltar(true);
			}
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		}
	
	}
	
	public void setIncluirPacientesCuidadosPosAnestesicos(
			boolean incluirPacientesCuidadosPosAnestesicos) {
		this.incluirPacientesCuidadosPosAnestesicos = incluirPacientesCuidadosPosAnestesicos;
	}

	public boolean isIncluirPacientesCuidadosPosAnestesicos() {
		return incluirPacientesCuidadosPosAnestesicos;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	public RapServidores getServidor() {
		return servidor;
	}

	public void setListaServEspecialidades(
			List<AghEspecialidades> listaServEspecialidades) {
		this.listaServEspecialidades = listaServEspecialidades;
	}

	public List<AghEspecialidades> getListaServEspecialidades() {
		return listaServEspecialidades;
	}

	public void setPrescricaoMedicaFacade(
			IPrescricaoMedicaFacade prescricaoMedicaFacade) {
		this.prescricaoMedicaFacade = prescricaoMedicaFacade;
	}

	public IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidadeSelecionada(
			AghEspecialidades especialidadeSelecionada) {
		this.especialidadeSelecionada = especialidadeSelecionada;
	}

	public AghEspecialidades getEspecialidadeSelecionada() {
		return especialidadeSelecionada;
	}

	public void setListaServEquipes(List<AghEquipes> listaServEquipes) {
		this.listaServEquipes = listaServEquipes;
	}

	public List<AghEquipes> getListaServEquipes() {
		return listaServEquipes;
	}

	public void setEquipe(AghEquipes equipe) {
		this.equipe = equipe;
	}

	public AghEquipes getEquipe() {
		return equipe;
	}

	public void setEquipeSelecionada(AghEquipes equipeSelecionada) {
		this.equipeSelecionada = equipeSelecionada;
	}

	public AghEquipes getEquipeSelecionada() {
		return equipeSelecionada;
	}

	public void setListaServUnFuncionais(
			List<AghUnidadesFuncionais> listaServUnFuncionais) {
		this.listaServUnFuncionais = listaServUnFuncionais;
	}

	public List<AghUnidadesFuncionais> getListaServUnFuncionais() {
		return listaServUnFuncionais;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncionalSelecionada(
			AghUnidadesFuncionais unidadeFuncionalSelecionada) {
		this.unidadeFuncionalSelecionada = unidadeFuncionalSelecionada;
	}

	public AghUnidadesFuncionais getUnidadeFuncionalSelecionada() {
		return unidadeFuncionalSelecionada;
	}

	public void setListaServAtendimentos(
			List<AghAtendimentos> listaServAtendimentos) {
		this.listaServAtendimentos = listaServAtendimentos;
	}

	public List<AghAtendimentos> getListaServAtendimentos() {
		return listaServAtendimentos;
	}

	public void setAtendimentoSelecionado(AghAtendimentos atendimentoSelecionado) {
		this.atendimentoSelecionado = atendimentoSelecionado;
	}

	public AghAtendimentos getAtendimentoSelecionado() {
		return atendimentoSelecionado;
	}

	public void setNumeroProntuario(Integer numeroProntuario) {
		this.numeroProntuario = numeroProntuario;
	}

	public Integer getNumeroProntuario() {
		return numeroProntuario;
	}

	public void setListaAtendimentosPacientesPesquisa(
			List<AghAtendimentos> listaAtendimentosPacientesPesquisa) {
		this.listaAtendimentosPacientesPesquisa = listaAtendimentosPacientesPesquisa;
	}

	public List<AghAtendimentos> getListaAtendimentosPacientesPesquisa() {
		return listaAtendimentosPacientesPesquisa;
	}

	public void setNomePesquisaFonetica(String nomePesquisaFonetica) {
		this.nomePesquisaFonetica = nomePesquisaFonetica;
	}

	public String getNomePesquisaFonetica() {
		return nomePesquisaFonetica;
	}

	public void setLeitoPesquisaFonetica(String leitoPesquisaFonetica) {
		this.leitoPesquisaFonetica = leitoPesquisaFonetica;
	}

	public String getLeitoPesquisaFonetica() {
		return leitoPesquisaFonetica;
	}

	public void setQuartoPesquisaFonetica(String quartoPesquisaFonetica) {
		this.quartoPesquisaFonetica = quartoPesquisaFonetica;
	}

	public String getQuartoPesquisaFonetica() {
		return quartoPesquisaFonetica;
	}

	public void setUnidadeFuncionalPesquisaFonetica(
			AghUnidadesFuncionais unidadeFuncionalPesquisaFonetica) {
		this.unidadeFuncionalPesquisaFonetica = unidadeFuncionalPesquisaFonetica;
	}

	public AghUnidadesFuncionais getUnidadeFuncionalPesquisaFonetica() {
		return unidadeFuncionalPesquisaFonetica;
	}

	public void setUnidadeFuncionalPesquisaFoneticaSelecionada(
			AghUnidadesFuncionais unidadeFuncionalPesquisaFoneticaSelecionada) {
		this.unidadeFuncionalPesquisaFoneticaSelecionada = unidadeFuncionalPesquisaFoneticaSelecionada;
	}

	public AghUnidadesFuncionais getUnidadeFuncionalPesquisaFoneticaSelecionada() {
		return unidadeFuncionalPesquisaFoneticaSelecionada;
	}

	public void setConfirmaVoltar(boolean confirmaVoltar) {
		this.confirmaVoltar = confirmaVoltar;
	}

	public boolean isConfirmaVoltar() {
		return confirmaVoltar;
	}

	private static class EspecialidadeComparator implements
	Comparator<AghEspecialidades> {
		@Override
		public int compare(AghEspecialidades esp1, AghEspecialidades esp2) {
			return esp1.getSigla().compareToIgnoreCase(esp2.getSigla());
		}
	}

	private static class EspecialidadeNameComparator implements
	Comparator<AghEspecialidades> {
		@Override
		public int compare(AghEspecialidades esp1, AghEspecialidades esp2) {
			return esp1.getNomeEspecialidade().compareToIgnoreCase(
					esp2.getNomeEspecialidade());
		}
	}

	private static class EquipeComparator implements Comparator<AghEquipes> {
		@Override
		public int compare(AghEquipes e1, AghEquipes e2) {
			return e1.getNome().compareToIgnoreCase(e2.getNome());
		}
	}
	
	private static class ResponsavelComparator implements Comparator<ProfessorCrmInternacaoVO> {
		@Override
		public int compare(ProfessorCrmInternacaoVO e1, ProfessorCrmInternacaoVO e2) {
			return e1.getNome().compareToIgnoreCase(e2.getNome());
		}
	}

	private static class UnidadeFuncionalComparator implements
	Comparator<AghUnidadesFuncionais> {
		@Override
		public int compare(AghUnidadesFuncionais u1, AghUnidadesFuncionais u2) {
			int result = u1.getLPADAndarAlaDescricao().compareToIgnoreCase(
					u2.getLPADAndarAlaDescricao());
			return result;
		}
	}

	private static class AtendimentoComparator implements
	Comparator<AghAtendimentos> {
		@Override
		public int compare(AghAtendimentos a1, AghAtendimentos a2) {
			int result = a1.getPaciente().getNome().compareToIgnoreCase(
					a2.getPaciente().getNome());
			return result;
		}
	}

	public List<ProfessorCrmInternacaoVO> pesquisarProfessor(String strParam) {
		return internacaoFacade.pesquisarProfessoresCrm(strParam, null, null);
	}

	public ProfessorCrmInternacaoVO getResponsavel() {
		return responsavel;
	}

	public List<ProfessorCrmInternacaoVO> getListaProfessorCrmInternacaoVO() {
		return listaProfessorCrmInternacaoVO;
	}

	public void setListaProfessorCrmInternacaoVO(
			List<ProfessorCrmInternacaoVO> listaProfessorCrmInternacaoVO) {
		this.listaProfessorCrmInternacaoVO = listaProfessorCrmInternacaoVO;
	}

	public ProfessorCrmInternacaoVO getResponsavelSelecionado() {
		return responsavelSelecionado;
	}

	public void setResponsavelSelecionado(
			ProfessorCrmInternacaoVO responsavelSelecionado) {
		this.responsavelSelecionado = responsavelSelecionado;
	}

	public void setResponsavel(ProfessorCrmInternacaoVO responsavel) {
		this.responsavel = responsavel;
	}

	public String getCameFrom() {
		return cameFrom;
	}

	public void setCameFrom(String cameFrom) {
		this.cameFrom = cameFrom;
	}

	public AghAtendimentos getAtendimentoTeste() {
		return atendimentoTeste;
	}

	public void setAtendimentoTeste(AghAtendimentos atendimentoTeste) {
		this.atendimentoTeste = atendimentoTeste;
	}	

	public String getLeitoID() {
		return leitoID;
	}

	public void setLeitoID(String leitoID) {
		this.leitoID = leitoID;
	}
	
	
}