package br.gov.mec.aghu.transplante.action;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.ambulatorio.action.ConsultaPacientePaginatorController;
import br.gov.mec.aghu.blococirurgico.action.LaudoAIHPaginatorController;
import br.gov.mec.aghu.dominio.DominioSituacaoTmo;
import br.gov.mec.aghu.exames.pesquisa.action.PesquisaExameController;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MtxTransplantes;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.cadastro.action.CadastrarPacienteController;
import br.gov.mec.aghu.transplante.business.ITransplanteFacade;
import br.gov.mec.aghu.transplante.vo.ListarTransplantesVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;

public class ListarTransplantesAba1PaginatorController extends ActionController implements ActionPaginator {
	
	private static final long serialVersionUID = -1622932271194776493L;
	@Inject @Paginator
	private DynamicDataModel<ListarTransplantesVO> dataModel;
	private Boolean mostrarColuna;
	@Inject
	private ListarTransplantesController controllerPrincipal;
	@Inject
	private CadastrarPacienteController cadastrarPacienteController;
	@Inject
	private PesquisaExameController pesquisaExameController;
	@Inject
	private ConsultaPacientePaginatorController consultaPacientePaginatorController;
	@Inject
	private LaudoAIHPaginatorController laudoAIHPaginatorController;
	@Inject
	private IncluirPacienteListaTransplanteTMOController incluirPacienteListaTransplanteTMOController;
	@EJB
	private ITransplanteFacade transplanteFacade;
	@EJB
	private IPacienteFacade pacienteFacade;
	private ListarTransplantesVO filtro; 
	private ListarTransplantesVO itemSelecionado1; 
	private static final String REDIRECT_CADASTRAR_PACIENTE = "paciente-cadastroPaciente";
	private static final String REDIRECT_LISTAR_TRANSPLANTES = "transplante-listarTransplante";
	private static final String REDIRECT_PESQUISA_EXAMES = "exames-pesquisaExames";
	private static final String REDIRECT_PESQUISA_CONSULTAS = "ambulatorio-pesquisaConsultasPaciente";
	private static final String REDIRECT_PESQUISA_LAUDOAIH = "blococirurgico-laudoAIH";
	private static final String REDIRECT_INCLUIR_PACIENTE_TMO = "transplante-incluirPacienteListaTransplanteTMOCRUD";
	
	

	@PostConstruct
	protected void inicializar(){
		begin(conversation);
	}
	
	public void pesquisar(ListarTransplantesVO filtro){
		this.filtro = filtro;
		if(this.filtro == null){
			this.filtro = new ListarTransplantesVO();
		}
		filtro.setSelecioneAba(1);
		try {
			transplanteFacade.validarTipoTransplante(filtro);
			dataModel.reiniciarPaginator();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	@Override
	public Long recuperarCount() {
		return transplanteFacade.obterPacientesAguardandoTransplantePorFiltroCount(filtro);
	}

	@Override
	public List<ListarTransplantesVO> recuperarListaPaginada(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc) {
		//A ideia é só desmarcar o item que foi selecionado na dataTable, 
		//visto que em sua paginação ele seta null no elemento selecionado, mas apenas depois de 'paginar' 2x, assim seto null logo na primeira vez.
		setItemSelecionado1(null);
		try{
			return transplanteFacade.obterPacientesAguardandoTransplantePorFiltro(filtro, firstResult, maxResults, orderProperty, asc);
		}catch(ApplicationBusinessException e){
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	public String editar(){
		if(itemSelecionado1 != null){
			incluirPacienteListaTransplanteTMOController.setPacCodigo(itemSelecionado1.getCodigoPaciente());
			MtxTransplantes transplante = transplanteFacade.obterTransplanteEdicao(itemSelecionado1.getTrpSeq());
			incluirPacienteListaTransplanteTMOController.setTransplante(transplante);
			incluirPacienteListaTransplanteTMOController.setAghCid(transplante.getCid());
			incluirPacienteListaTransplanteTMOController.setMtxOrigens(transplante.getOrigem());
			incluirPacienteListaTransplanteTMOController.setTipoTmo(transplante.getTipoTmo());
			if(transplante.getTipoTmo() != null && transplante.getTipoTmo() == DominioSituacaoTmo.G){
				incluirPacienteListaTransplanteTMOController.setTipoAlogenico(transplante.getTipoAlogenico());
			}
			incluirPacienteListaTransplanteTMOController.setStatusDoenca(transplante.getCriterioPriorizacao());
			incluirPacienteListaTransplanteTMOController.setDoador(transplante.getDoador());
			incluirPacienteListaTransplanteTMOController.setEditandoTransplante(true);
			incluirPacienteListaTransplanteTMOController.setTelaAnterior(REDIRECT_LISTAR_TRANSPLANTES);
			return REDIRECT_INCLUIR_PACIENTE_TMO;
		}
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
	
	public Integer obterPermanenciaPaciente(Date  dataIngresso){
		return DateUtil.calcularDiasEntreDatas(dataIngresso, Calendar.getInstance().getTime());
	}
	public String obterHint(String item, Integer tamanhoMaximo) {
		if (item.length() > tamanhoMaximo) {
			item = StringUtils.abbreviate(item, tamanhoMaximo);
		}
		return item;
    }
	
	public String botaoCadPaciente(){
		if(itemSelecionado1 != null){
			AipPacientes paciente = this.pacienteFacade.obterAipPacientesPorChavePrimaria(itemSelecionado1.getCodigoPaciente());
			cadastrarPacienteController.setCameFrom(REDIRECT_LISTAR_TRANSPLANTES);
			cadastrarPacienteController.prepararEdicaoPaciente(paciente, null);
			return REDIRECT_CADASTRAR_PACIENTE;
		}
		return null;
	}
	
	public String botaoExames(){
		if(itemSelecionado1 != null){
			AipPacientes paciente = this.pacienteFacade.obterAipPacientesPorChavePrimaria(itemSelecionado1.getCodigoPaciente());
			pesquisaExameController.setPaciente(paciente);
			pesquisaExameController.getFiltro().setProntuarioPac(itemSelecionado1.getProntuarioPaciente());
			pesquisaExameController.getFiltro().setNomePacientePac(itemSelecionado1.getNomePaciente());
			pesquisaExameController.setAlterarFiltroPesquisaExames(false);
			pesquisaExameController.setVoltarPara(REDIRECT_LISTAR_TRANSPLANTES);
			pesquisaExameController.pesquisar();
			return REDIRECT_PESQUISA_EXAMES;
		}
		return null;
	}
	
	public String botaoConsultas(){
		if(itemSelecionado1 != null){
			consultaPacientePaginatorController.setVoltarPara(REDIRECT_LISTAR_TRANSPLANTES);
			consultaPacientePaginatorController.setPacCodigo(itemSelecionado1.getCodigoPaciente());
			consultaPacientePaginatorController.pesquisar();
			return REDIRECT_PESQUISA_CONSULTAS;
		}
		return null;
	}
	
	public String botaoLaudoAIH(){
		if(itemSelecionado1 != null){
			laudoAIHPaginatorController.setPacCodigo(itemSelecionado1.getCodigoPaciente());
			laudoAIHPaginatorController.setIniciaPesquisando(true);
			laudoAIHPaginatorController.setVoltarPara(REDIRECT_LISTAR_TRANSPLANTES);
			return REDIRECT_PESQUISA_LAUDOAIH;
		}
		return null;
	}

	public DynamicDataModel<ListarTransplantesVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ListarTransplantesVO> dataModel) {
		this.dataModel = dataModel;
	}

	public Boolean getMostrarColuna() {
		return mostrarColuna;
	}

	public void setMostrarColuna(Boolean mostrarColuna) {
		this.mostrarColuna = mostrarColuna;
	}

	public ListarTransplantesController getControllerPrincipal() {
		return controllerPrincipal;
	}

	public void setControllerPrincipal(
			ListarTransplantesController controllerPrincipal) {
		this.controllerPrincipal = controllerPrincipal;
	}

	public ITransplanteFacade getTransplanteFacade() {
		return transplanteFacade;
	}

	public void setTransplanteFacade(ITransplanteFacade transplanteFacade) {
		this.transplanteFacade = transplanteFacade;
	}

	public ListarTransplantesVO getFiltro() {
		return filtro;
	}

	public void setFiltro(ListarTransplantesVO filtro) {
		this.filtro = filtro;
	}

	public ListarTransplantesVO getItemSelecionado1() {
		return itemSelecionado1;
	}

	public void setItemSelecionado1(ListarTransplantesVO itemSelecionado1) {
		this.itemSelecionado1 = itemSelecionado1;
	}

}
