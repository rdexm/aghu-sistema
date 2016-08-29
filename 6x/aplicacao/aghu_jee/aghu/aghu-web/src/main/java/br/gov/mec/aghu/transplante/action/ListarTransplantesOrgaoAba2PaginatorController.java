package br.gov.mec.aghu.transplante.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.ambulatorio.action.ConsultaPacientePaginatorController;
import br.gov.mec.aghu.blococirurgico.action.LaudoAIHPaginatorController;
import br.gov.mec.aghu.exames.pesquisa.action.PesquisaExameController;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.cadastro.action.CadastrarPacienteController;
import br.gov.mec.aghu.transplante.business.ITransplanteFacade;
import br.gov.mec.aghu.transplante.vo.FiltroTransplanteOrgaoVO;
import br.gov.mec.aghu.transplante.vo.PacienteTransplantadosOrgaoVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

public class ListarTransplantesOrgaoAba2PaginatorController extends ActionController implements ActionPaginator {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2398888263580851843L;
	@Inject @Paginator
	private DynamicDataModel<PacienteTransplantadosOrgaoVO> dataModel2;
	@Inject
	private ListarTransplantesOrgaoController listarTransplantesOrgaoController;
	@Inject
	private CadastrarPacienteController cadastrarPacienteController;
	@Inject
	private PesquisaExameController pesquisaExameController;
	@Inject
	private ConsultaPacientePaginatorController consultaPacientePaginatorController;
	@Inject
	private LaudoAIHPaginatorController laudoAIHPaginatorController;
	@Inject
	private IncluirPacienteListaTransplanteController incluirPacienteListaTransplanteController;
	@EJB
	private ITransplanteFacade transplanteFacade;
	@EJB
	private IPacienteFacade pacienteFacade;
	
	private FiltroTransplanteOrgaoVO filtro; 
	private PacienteTransplantadosOrgaoVO itemSelecionado; 
	private static final String REDIRECT_CADASTRAR_PACIENTE = "paciente-cadastroPaciente";
	private static final String REDIRECT_LISTAR_TRANSPLANTES_ORGAO = "transplante-listarTransplanteOrgao";
	private static final String REDIRECT_PESQUISA_EXAMES = "exames-pesquisaExames";
	private static final String REDIRECT_PESQUISA_CONSULTAS = "ambulatorio-pesquisaConsultasPaciente";
	private static final String REDIRECT_PESQUISA_LAUDOAIH = "blococirurgico-laudoAIH";
	private static final String REDIRECT_EDITAR_TRANSPLANTE = "transplante-incluirPacienteListaTransplanteCRUD";
	
	
	@PostConstruct
	protected void inicializar(){
		begin(conversation);
	}
	
	public void pesquisar(FiltroTransplanteOrgaoVO filtro){
		this.filtro = filtro;
		dataModel2.reiniciarPaginator();
	}
	
	@Override
	public Long recuperarCount() {
		return transplanteFacade.obterListaPacientesTransplantadosOrgaoCount(filtro);
	}

	@Override
	public List<PacienteTransplantadosOrgaoVO> recuperarListaPaginada(Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {
		itemSelecionado = null;
		return transplanteFacade.obterListaPacientesTransplantadosOrgao(filtro, firstResult, maxResults, orderProperty, asc);
	}
	
	public String editar(){
		if(itemSelecionado != null){
			incluirPacienteListaTransplanteController.setPacCodigo(itemSelecionado.getCodigoReceptor());
			incluirPacienteListaTransplanteController.setItemSelecionadoAba2(itemSelecionado);
			incluirPacienteListaTransplanteController.setTelaAnterior(REDIRECT_LISTAR_TRANSPLANTES_ORGAO);
			return REDIRECT_EDITAR_TRANSPLANTE; 
		}
		return null;
	}
	public String alterarSituacao(){
		// Encaminha para tela de alteração de situação, estória #41799, se já desenvolvida, caso contrario deixar ícone desabilitado.
		return null;
	}
	
	public String obterProntuarioFormatado(String prontuario){
		if (StringUtils.isEmpty(prontuario)) {
			return StringUtils.EMPTY;
		}
		return String.valueOf(Long.valueOf(prontuario));
	}
	
	public String obterHint(String item, Integer tamanhoMaximo) {
		if (item.length() > tamanhoMaximo) {
			item = StringUtils.abbreviate(item, tamanhoMaximo);
		}
		return item;
    }
	
	public String obterIdadePaciente(Date dataNascimento){
		return transplanteFacade.obterIdadeFormatada(dataNascimento);
	}
	
	public String botaoCadPaciente(){
		if(itemSelecionado != null){
			AipPacientes paciente = this.pacienteFacade.obterAipPacientesPorChavePrimaria(itemSelecionado.getCodigoReceptor());
			cadastrarPacienteController.setCameFrom(REDIRECT_LISTAR_TRANSPLANTES_ORGAO);
			cadastrarPacienteController.prepararEdicaoPaciente(paciente, null);
			return REDIRECT_CADASTRAR_PACIENTE;
		}
		return null;
	}
	
	public String botaoExames(){
		if(itemSelecionado != null){
			AipPacientes paciente = this.pacienteFacade.obterAipPacientesPorChavePrimaria(itemSelecionado.getCodigoReceptor());
			pesquisaExameController.setPaciente(paciente);
			pesquisaExameController.getFiltro().setProntuarioPac(itemSelecionado.getPacProntuario());
			pesquisaExameController.getFiltro().setNomePacientePac(itemSelecionado.getPacNome());
			pesquisaExameController.setAlterarFiltroPesquisaExames(false);
			pesquisaExameController.setVoltarPara(REDIRECT_LISTAR_TRANSPLANTES_ORGAO);
			pesquisaExameController.pesquisar();
			return REDIRECT_PESQUISA_EXAMES;
		}
		return null;
	}
	
	public String botaoConsultas(){
		if(itemSelecionado != null){
			consultaPacientePaginatorController.setPacCodigo(itemSelecionado.getCodigoReceptor());
			consultaPacientePaginatorController.pesquisar();
			return REDIRECT_PESQUISA_CONSULTAS;
		}
		return null;
	}
	
	public String botaoLaudoAIH(){
		if(itemSelecionado != null){
			laudoAIHPaginatorController.setPacCodigo(itemSelecionado.getCodigoReceptor());
			laudoAIHPaginatorController.setIniciaPesquisando(true);
			laudoAIHPaginatorController.setVoltarPara(REDIRECT_LISTAR_TRANSPLANTES_ORGAO);
			return REDIRECT_PESQUISA_LAUDOAIH;
		}
		return null;
	}
	
	public DynamicDataModel<PacienteTransplantadosOrgaoVO> getDataModel2() {
		return dataModel2;
	}

	public void setDataModel2(DynamicDataModel<PacienteTransplantadosOrgaoVO> dataModel2) {
		this.dataModel2 = dataModel2;
	}

	public FiltroTransplanteOrgaoVO getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroTransplanteOrgaoVO filtro) {
		this.filtro = filtro;
	}

	public PacienteTransplantadosOrgaoVO getItemSelecionado() {
		return itemSelecionado;
	}

	public void setItemSelecionado(PacienteTransplantadosOrgaoVO itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}

	public ListarTransplantesOrgaoController getListarTransplantesOrgaoController() {
		return listarTransplantesOrgaoController;
	}

	public void setListarTransplantesOrgaoController(ListarTransplantesOrgaoController listarTransplantesOrgaoController) {
		this.listarTransplantesOrgaoController = listarTransplantesOrgaoController;
	}
}
