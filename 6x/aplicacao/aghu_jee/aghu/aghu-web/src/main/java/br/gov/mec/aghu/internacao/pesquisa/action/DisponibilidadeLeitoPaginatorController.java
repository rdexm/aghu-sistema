package br.gov.mec.aghu.internacao.pesquisa.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.internacao.action.CadastroInternacaoController;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.LeitoDisponibilidadeVO;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinAcomodacoes;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.cadastro.action.CadastrarPacienteController;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;


public class DisponibilidadeLeitoPaginatorController extends ActionController implements ActionPaginator{

	private static final long serialVersionUID = 640590549921992846L;

	private final String PAGE_PESQUISAR_DISPONIBILIDADE_UNIDADE = "internacao-pesquisarDisponibilidadeUnidade";
	private final String PAGE_PESQUISAR_DISPONIBILIDADE_QUARTO = "internacao-pesquisarDisponibilidadeQuarto";
	private final String PAGE_PESQUISAR_PACIENTE = "paciente-pesquisaPaciente";
	private final String PAGE_SOLICITACOES_INTERNACOES = "internacao-pesquisaSolicitacaoInternacao";
	private final String PAGE_QUARTO_PACIENTE= "internacao-quartoCRUD";
	private final String PAGE_BLOQUEIO_LEITO= "internacao-bloqueiaLeito";
	private final String PAGE_TRANSFERIR_PACIENTE= "internacao-transferirPacienteCRUD";
	private static final String PAGE_RESERVA_LEITO= "internacao-reservaLeito";
	private static final String PAGE_CADASTRO_INTERNACAO = "internacao-cadastroInternacao";
	private static final String PAGE_INTERNACAO_TRANSFERIR_PACIENTE_LIST = "internacao-transferirPacienteList";
	private static final String  PAGE_INTERNACAO_PESQUISAR_PACIENTE_INTERNADO = "internacao-pesquisarPacienteInternado";
	private static final String  PAGE_INTERNACAO_PESQUISAR_PACIENTE_INTERNADO_DETALHES = "internacao-pesquisarPacienteInternadoDetalhes";
	private static final String  PESQUISAR_PACIENTE_INTERNADO = "pesquisarPacienteInternado";
	private static final String  PESQUISAR_PACIENTE_INTERNADO_DETALHES = "pesquisarPacienteInternadoDetalhes";
	private static final String LISTAR_CIRURGIAS = "blococirurgico-listaCirurgias";
	
	private static final String INTERNACAO = "Internacao";

	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	@EJB
	private IPesquisaInternacaoFacade pesquisaInternacaoFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@Inject
	private CadastrarPacienteController  cadastrarPacienteController;
	
	@Inject @Paginator
	private DynamicDataModel<LeitoDisponibilidadeVO> dataModel;
	private LeitoDisponibilidadeVO leitoSelecionado;
	private AipPacientes pacienteInternacao;
	private Integer pacCodigo; //param da tela pesquisar disponibilidade unidade
	
	private AghUnidadesFuncionais unidadeFuncional;
	private Short unfSeq; //param da tela pesquisar disponibilidade unidade
	private AinQuartos quarto;
	private AghClinicas clinica;
	private AinAcomodacoes acomodacao;
	private AinLeitos leito;
	private AinLeitos leitoAtribuido;
	private String cameFrom;
	private Integer internacaoSeq;
	private Boolean exibirAcaoTransferir;

	@Inject
	private CadastroInternacaoController cadastroInternacaoController;

	@PostConstruct
	public void init() {
		this.begin(conversation);
		this.exibirAcaoTransferir=false;
	}
	
	public void inicio(){
	 

		
		if(LISTAR_CIRURGIAS.equalsIgnoreCase(this.cameFrom) || INTERNACAO.equalsIgnoreCase(this.cadastrarPacienteController.getCameFrom())){
			this.pacCodigo = this.cadastrarPacienteController.getPacCodigo();
			this.unfSeq = this.cadastrarPacienteController.getSeqUnidadeFuncional();	
			
			if(!LISTAR_CIRURGIAS.equalsIgnoreCase(this.cameFrom)){
				this.cameFrom = this.cadastrarPacienteController.getCameFrom();
			}
		} else if(PESQUISAR_PACIENTE_INTERNADO.equals(this.cameFrom) || PESQUISAR_PACIENTE_INTERNADO_DETALHES.equals(this.cameFrom) || PAGE_INTERNACAO_PESQUISAR_PACIENTE_INTERNADO.equalsIgnoreCase(this.cameFrom) || PAGE_INTERNACAO_PESQUISAR_PACIENTE_INTERNADO_DETALHES.equalsIgnoreCase(this.cameFrom)){
			this.exibirAcaoTransferir=true;
		} else {
			cameFrom = "internacao-pesquisarDisponibilidadeLeito";
		}
		
		if(unfSeq != null){
			this.unidadeFuncional = this.aghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(unfSeq);
		}
		
		if(pacCodigo != null){
			this.pacienteInternacao = pacienteFacade.obterAipPacientesPorChavePrimaria(pacCodigo);
		}
	
	}

	
	public String internarPaciente(LeitoDisponibilidadeVO leitoVO){
		String idLeito = leitoVO.getLeitoId();
		try {
			cadastrosBasicosInternacaoFacade.verificarInternar(idLeito);
			if (pacienteInternacao != null) {
				this.leitoAtribuido = cadastrosBasicosInternacaoFacade.obterLeitoPorId(idLeito); // idLeitoAtribuido
				pesquisaInternacaoFacade.consistirSexoQuarto(pacienteInternacao, leitoAtribuido.getQuarto());
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		if (pacienteInternacao != null) {
			if (LISTAR_CIRURGIAS.equalsIgnoreCase(this.cameFrom)){
				cadastroInternacaoController.setCameFrom(this.cameFrom);
				cadastroInternacaoController.setAinLeitoId(idLeito);
			}
			return PAGE_CADASTRO_INTERNACAO;
		} else {
			return PAGE_PESQUISAR_PACIENTE;
		}		
	}
	
	public String pesquisarDisponibilidadeUnidade(){
		return PAGE_PESQUISAR_DISPONIBILIDADE_UNIDADE;
	}
	
	public String pesquisarDisponibilidadeQuarto(){
		return PAGE_PESQUISAR_DISPONIBILIDADE_QUARTO;
	}
	
	public String detalharQuarto(){
		return PAGE_QUARTO_PACIENTE;
	}	
	
	
	public String reservarLeito(){
		return PAGE_RESERVA_LEITO;		
	}
	
	
	public String bloquearLeito(){
		return PAGE_BLOQUEIO_LEITO;		
	}
	

	public String transferirPaciente(){
		return PAGE_TRANSFERIR_PACIENTE;		
	}

	public String transferir(){
		return PAGE_INTERNACAO_TRANSFERIR_PACIENTE_LIST;		
	}
	
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncional(String strPesquisa) {
		return cadastrosBasicosInternacaoFacade.pesquisarUnidadeFuncional(strPesquisa == null ? null : strPesquisa);
	}
	
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalInternacaoAtiva(String strPesquisa) {
		return cadastrosBasicosInternacaoFacade.pesquisarUnidadeFuncionalInternacaoAtiva(strPesquisa == null ? null : strPesquisa);
	}

	/**
	 * Método que retorna uma coleções de acomodações p/ preencher a suggestion
	 * box, de acordo com filtro informado.
	 * 
	 * @param parametro
	 * @return
	 */
	public List<AinAcomodacoes> pesquisarAcomodacoes(String parametro) {
		return this.cadastrosBasicosInternacaoFacade.pesquisarAcomodacoesPorCodigoOuDescricao(parametro);
	}

	/**
	 * Método que retorna uma coleções de leitos p/ preencher a suggestion box,
	 * de acordo com filtro informado.
	 * 
	 * @param parametro
	 * @return
	 */
	public List<AinLeitos> pesquisarLeitos(String parametro) {
		return this.cadastrosBasicosInternacaoFacade.pesquisarLeitos(parametro);
	}

	/**
	 * Método que retorna uma coleções de quartos p/ preencher a suggestion box,
	 * de acordo com filtro informado.
	 * 
	 * @param parametro
	 * @return
	 */
	public List<AinQuartos> pesquisarQuartos(String paramPesquisa) {
		return this.returnSGWithCount(this.cadastrosBasicosInternacaoFacade.pesquisarQuartos(paramPesquisa == null ? null : paramPesquisa),pesquisarQuartosCount(paramPesquisa));
	}
	
	
	public Integer pesquisarQuartosCount(String paramPesquisa) {
		return this.cadastrosBasicosInternacaoFacade.pesquisarQuartos(paramPesquisa == null ? null : paramPesquisa).size();
	}

	/**
	 * 
	 */
	public void limparPesquisa() {
		this.acomodacao = null;
		this.unidadeFuncional = null;
		this.quarto = null;
		this.clinica = null;
		this.leito = null;
		dataModel.limparPesquisa();
	}

	/**
	 * Método que realiza a ação do botão pesquisar.
	 */
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}

	/**
	 * Método para atribuir quarto selecionado na suggestion ao atributo
	 * this.quarto da controller.
	 * 
	 * @param quarto
	 */
	public void atribuirQuarto(AinQuartos quarto) {
		this.quarto = quarto;
	}

	/**
	 * Método para atribuir unidade funcional selecionada na suggestion ao
	 * atributo unidadeFuncional da controller.
	 * 
	 * @param unidadeFuncional
	 */

	public void atribuirAndar(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	/**
	 * Método que retorna uma coleções de clínicas p/ preencher a suggestion
	 * box, de acordo com filtro informado.
	 * 
	 * @param parametro
	 * @return
	 */
	public List<AghClinicas> pesquisarClinicas(String paramPesquisa) {
		return this.aghuFacade.pesquisarClinicas(paramPesquisa == null ? null : paramPesquisa);
	}

	/**
	 * Método para atribuir clinica selecionado na suggestion ao atributo
	 * this.clinica da controller.
	 * 
	 * @param clinica
	 */
	public void atribuirClinica(AghClinicas clinica) {
		this.clinica = clinica;
	}

	/**
	 * Método para atribuir clinica selecionado na suggestion ao atributo
	 * this.clinica da controller.
	 * 
	 * @param clinica
	 */
	public void atribuirAcomodacao(AinAcomodacoes acomodacao) {
		this.acomodacao = acomodacao;
	}

	/**
	 * Método para atribuir clinica selecionado na suggestion ao atributo
	 * this.clinica da controller.
	 * 
	 * @param clinica
	 */
	public void atribuirLeito(AinLeitos leito) {
		this.leito = leito;
	}

	@Override
	@SuppressWarnings("PMD.NPathComplexity")
	public List<LeitoDisponibilidadeVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		if (this.leito != null && StringUtils.isBlank(this.leito.getLeitoID())) {
			this.leito.setLeitoID(null);
		}
		return this.pesquisaInternacaoFacade.pesquisarDisponibilidadeLeitos(firstResult, maxResult, 
				acomodacao == null ? null : acomodacao.getSeq(),
				clinica == null ? null : clinica.getCodigo(), 
				unidadeFuncional == null ? null : unidadeFuncional.getSeq(),
				leito == null ? null : leito.getLeitoID(), 
				quarto == null ? null : quarto.getNumero());
	}

	@Override
	public Long recuperarCount() {
		if (leito != null && StringUtils.isBlank(leito.getLeitoID())) {
			leito.setLeitoID(null);
		}
		return this.pesquisaInternacaoFacade.pesquisarDisponibilidadeLeitosCount(
				acomodacao == null ? null : acomodacao.getSeq(),
				clinica == null ? null : clinica.getCodigo(), 
				unidadeFuncional == null ? null : unidadeFuncional.getSeq(),
				leito == null ? null : leito.getLeitoID(), 
				quarto == null ? null : quarto.getNumero());
	}	
	
	public boolean exibirAcaoInternar(LeitoDisponibilidadeVO leitoVO) {
		return cadastrosBasicosInternacaoFacade.permitirInternar(leitoVO.getLeitoId());
	}

	public boolean consistirSexo(LeitoDisponibilidadeVO leitoVO) {
		if (pacienteInternacao != null) {
			AinLeitos leito = cadastrosBasicosInternacaoFacade.obterLeitoPorId(leitoVO.getLeitoId());
			try {
				pesquisaInternacaoFacade.consistirSexoLeito(pacienteInternacao, leito);
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
				return false;
			}
		}
		return true;
	}
	
	
	public String  cancelar(){
		return this.cameFrom;
	}
	
	public String buscarSolicitacoesInternacoesPendentes(){
		return PAGE_SOLICITACOES_INTERNACOES;
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public AinQuartos getQuarto() {
		return quarto;
	}

	public void setQuarto(AinQuartos quarto) {
		this.quarto = quarto;
	}

	public AghClinicas getClinica() {
		return clinica;
	}

	public void setClinica(AghClinicas clinica) {
		this.clinica = clinica;
	}

	/**
	 * @return the acomodacao
	 */
	public AinAcomodacoes getAcomodacao() {
		return acomodacao;
	}

	/**
	 * @param acomodacao
	 *            the acomodacao to set
	 */
	public void setAcomodacao(AinAcomodacoes acomodacao) {
		this.acomodacao = acomodacao;
	}

	/**
	 * @return the leito
	 */
	public AinLeitos getLeito() {
		return leito;
	}

	/**
	 * @param leito
	 *            the leito to set
	 */
	public void setLeito(AinLeitos leito) {
		this.leito = leito;
	}

	/**
	 * Método get para montar a descrição completa de um leito, composta pelo
	 * leitoID, andar, ala e descrição que o mesmo se encontra.
	 * 
	 * @return String com descrição completa
	 */
	public String getDescricaoCompletaLeito() {
		if (leito==null){
			return "";
		}
		return cadastrosBasicosInternacaoFacade.obterDescricaoCompletaLeito(leito.getLeitoID());
	}

	/**
	 * Método get para montar a descrição completa de um quarto, composta pelo
	 * andar, ala e descrição que o mesmo se encontra.
	 * 
	 * @return String com descrição completa
	 */
	public String getDescricaoCompletaQuarto() {
		if (quarto==null){
			return "";
		}
		return cadastrosBasicosInternacaoFacade.obterDescricaoCompletaQuarto(quarto.getNumero());
	}

	public boolean isExibirBotaoVagasQuarto() {
		return this.quarto != null && this.quarto.getNumero() != null;
	}

	public AipPacientes getPacienteInternacao() {
		return pacienteInternacao;
	}

	public void setPacienteInternacao(AipPacientes pacienteInternacao) {
		this.pacienteInternacao = pacienteInternacao;
	}

	public DynamicDataModel<LeitoDisponibilidadeVO> getDataModel() {
		return dataModel;
	}


	public void setDataModel(DynamicDataModel<LeitoDisponibilidadeVO> dataModel) {
		this.dataModel = dataModel;
	}


	public LeitoDisponibilidadeVO getLeitoSelecionado() {
		return leitoSelecionado;
	}


	public void setLeitoSelecionado(LeitoDisponibilidadeVO leitoSelecionado) {
		this.leitoSelecionado = leitoSelecionado;
	}


	public String getCameFrom() {
		return cameFrom;
	}


	public void setCameFrom(String cameFrom) {
		this.cameFrom = cameFrom;
	}


	public Integer getInternacaoSeq() {
		return internacaoSeq;
	}


	public void setInternacaoSeq(Integer internacaoSeq) {
		this.internacaoSeq = internacaoSeq;
	}
	
	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public Boolean getExibirAcaoTransferir() {
		return exibirAcaoTransferir;
	}

	public void setExibirAcaoTransferir(Boolean exibirAcaoTransferir) {
		this.exibirAcaoTransferir = exibirAcaoTransferir;
	}
	
	public AinLeitos getLeitoAtribuido() {
		return leitoAtribuido;
	}
	
	public void setLeitoAtribuido(AinLeitos leitoAtribuido) {
		this.leitoAtribuido = leitoAtribuido;
	}

}