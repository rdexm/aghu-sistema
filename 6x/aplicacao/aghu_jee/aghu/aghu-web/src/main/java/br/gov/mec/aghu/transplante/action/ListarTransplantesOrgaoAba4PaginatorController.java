package br.gov.mec.aghu.transplante.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.action.ConsultaPacientePaginatorController;
import br.gov.mec.aghu.blococirurgico.action.LaudoAIHPaginatorController;
import br.gov.mec.aghu.exames.pesquisa.action.PesquisaExameController;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.cadastro.action.CadastrarPacienteController;
import br.gov.mec.aghu.transplante.business.ITransplanteFacade;
import br.gov.mec.aghu.transplante.vo.FiltroTransplanteOrgaoVO;
import br.gov.mec.aghu.transplante.vo.PacienteAguardandoTransplanteOrgaoVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

public class ListarTransplantesOrgaoAba4PaginatorController extends ActionController implements ActionPaginator {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2860962460607845102L;
	@Inject @Paginator
	private DynamicDataModel<FiltroTransplanteOrgaoVO> dataModel4;
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
	
	private static final Log LOG = LogFactory.getLog(ListarTransplantesOrgaoAba4PaginatorController.class);
	
	
	private FiltroTransplanteOrgaoVO filtro; 
	private PacienteAguardandoTransplanteOrgaoVO itemSelecionado; 
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
		if(this.filtro == null){
			this.filtro = new FiltroTransplanteOrgaoVO();
		}
		filtro.setSelectTab(4);
		dataModel4.reiniciarPaginator();
	}
	
	@Override
	public Long recuperarCount() {
		
		try {
			List<PacienteAguardandoTransplanteOrgaoVO> listaRetorno = transplanteFacade.obterListaPacientesRetiradosOrgao(filtro, null, null, null, true, false);
			return Long.valueOf(listaRetorno.size());
		} catch (ApplicationBusinessException | CloneNotSupportedException e) {
			LOG.error(e.getMessage());
			apresentarExcecaoNegocio((BaseException) e);
		}
		return null;
	}

	@Override
	public List<PacienteAguardandoTransplanteOrgaoVO> recuperarListaPaginada(Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc) {
		itemSelecionado = null;
		try {
			return transplanteFacade.obterListaPacientesRetiradosOrgao(filtro, firstResult, maxResults, orderProperty, asc, true);
		} catch (ApplicationBusinessException | CloneNotSupportedException e) {
			LOG.error(e.getMessage());
		}
		
		return new ArrayList<PacienteAguardandoTransplanteOrgaoVO>();
		
	}
	
	public String editar(){
		if(itemSelecionado != null){
		incluirPacienteListaTransplanteController.setPacCodigo(itemSelecionado.getCodigoReceptor());
		incluirPacienteListaTransplanteController.setItemSelecionadoaba1(itemSelecionado);
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
	
	public String obterIdadePaciente(Date  dataNascimento){
		return transplanteFacade.obterIdadeFormatada(dataNascimento);
	}
	
	public String obterHint(String item, Integer tamanhoMaximo) {
		if (item.length() > tamanhoMaximo) {
			item = StringUtils.abbreviate(item, tamanhoMaximo);
		}
		return item;
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
	
	public DynamicDataModel<FiltroTransplanteOrgaoVO> getDataModel4() {
		return dataModel4;
	}

	public void setDataModel4(DynamicDataModel<FiltroTransplanteOrgaoVO> dataModel4) {
		this.dataModel4 = dataModel4;
	}

	public FiltroTransplanteOrgaoVO getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroTransplanteOrgaoVO filtro) {
		this.filtro = filtro;
	}

	public PacienteAguardandoTransplanteOrgaoVO getItemSelecionado() {
		return itemSelecionado;
	}

	public void setItemSelecionado(PacienteAguardandoTransplanteOrgaoVO itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}

	public ListarTransplantesOrgaoController getListarTransplantesOrgaoController() {
		return listarTransplantesOrgaoController;
	}

	public void setListarTransplantesOrgaoController(ListarTransplantesOrgaoController listarTransplantesOrgaoController) {
		this.listarTransplantesOrgaoController = listarTransplantesOrgaoController;
	}
}
