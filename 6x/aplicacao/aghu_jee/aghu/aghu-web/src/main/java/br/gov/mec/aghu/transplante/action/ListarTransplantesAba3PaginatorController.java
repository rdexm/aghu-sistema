package br.gov.mec.aghu.transplante.action;

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

public class ListarTransplantesAba3PaginatorController extends ActionController implements ActionPaginator {


	/**
	 * 
	 */
	private static final long serialVersionUID = -6023871410957502034L;

	@Inject @Paginator
	private DynamicDataModel<ListarTransplantesVO> dataModel3;
	
	private Boolean mostrarColuna;
	
	@Inject
	private ListarTransplantesController controllerPrincipal;
	
	@Inject
	private CadastrarPacienteController cadastrarPacienteController;
	
	@Inject
	private PesquisaExameController pesquisaExameController;
	
	@Inject
	private LaudoAIHPaginatorController laudoAIHPaginatorController;
	
	@Inject
	private ConsultaPacientePaginatorController consultaPacientePaginatorController;
	
	@EJB
	private ITransplanteFacade transplanteFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@Inject
	private IncluirPacienteListaTransplanteTMOController incluirPacienteListaTransplanteTMOController;
	
	private ListarTransplantesVO filtro; 
	private ListarTransplantesVO itemSelecionado; 
	private static final String REDIRECT_CADASTRAR_PACIENTE = "paciente-cadastroPaciente";
	private static final String REDIRECT_PESQUISA_EXAMES = "exames-pesquisaExames";
	private static final String REDIRECT_LISTAR_TRANSPLANTES = "transplante-listarTransplante";
	private static final String MATERIAL_RECEBIDO  = "informarDadosMaterialRecebidoTmoCRUD";
	private static final String INCLUIR_PACIENTE_LISTA_TRANSPLANTE_TMOCRUD = "transplante-incluirPacienteListaTransplanteTMOCRUD";
	private static final String REDIRECT_PESQUISA_LAUDOAIH = "blococirurgico-laudoAIH";
	private static final String REDIRECT_PESQUISA_CONSULTAS = "ambulatorio-pesquisaConsultasPaciente";
	
	
	@PostConstruct
	protected void inicializar(){
		begin(conversation);
	}
	
	public void pesquisar(ListarTransplantesVO filtro){
		this.filtro = filtro;
		if(this.filtro == null){
			this.filtro = new ListarTransplantesVO();
		}
		filtro.setSelecioneAba(3);
		try {
			transplanteFacade.validarTipoTransplante(filtro);
			dataModel3.reiniciarPaginator();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	@Override
	public Long recuperarCount() {
		return transplanteFacade.obterPacientesTransplantadosPorFiltroCount(filtro);
	}

	@Override
	public List<ListarTransplantesVO> recuperarListaPaginada(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc) {
		//A expliciação sobre porque seto null encontra-se ListarTransplantesAba1, neste mesmo método.
		setItemSelecionado(null);
		try{
			return transplanteFacade.obterPacientesTransplantadosPorFiltro(filtro, firstResult, maxResults, orderProperty, asc);
		}catch(ApplicationBusinessException e){
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	public String editar(){
		if(itemSelecionado != null){
			incluirPacienteListaTransplanteTMOController.setPacCodigo(itemSelecionado.getCodigoPaciente());
			MtxTransplantes transplante = transplanteFacade.obterTransplanteEdicao(itemSelecionado.getTrpSeq());
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
			return INCLUIR_PACIENTE_LISTA_TRANSPLANTE_TMOCRUD;
		}
		return null;
	}
	public String coletarMaterialBiologico(){
		return MATERIAL_RECEBIDO;
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
	public String obterPermanenciaPaciente(ListarTransplantesVO itemSelecionado){
		return DateUtil.calcularDiasEntreDatas(itemSelecionado.getDataSituacao(), itemSelecionado.getDataSituacaoAtual()) + " dias";
	}
	
	public String obterHint(String item, Integer tamanhoMaximo) {
		if (item.length() > tamanhoMaximo) {
			item = StringUtils.abbreviate(item, tamanhoMaximo);
		}
		return item;
    }
	
	public String botaoCadPaciente(){
		if(itemSelecionado != null){
			AipPacientes paciente = this.pacienteFacade.obterAipPacientesPorChavePrimaria(itemSelecionado.getCodigoPaciente());
			cadastrarPacienteController.setCameFrom(REDIRECT_LISTAR_TRANSPLANTES);
			cadastrarPacienteController.prepararEdicaoPaciente(paciente, null);
			return REDIRECT_CADASTRAR_PACIENTE;
		}
		return null;
	}
	
	public String botaoExames(){
		if(itemSelecionado != null){
			pesquisaExameController.getFiltro().setProntuarioPac(itemSelecionado.getProntuarioPaciente());
			pesquisaExameController.getFiltro().setNomePacientePac(itemSelecionado.getNomePaciente());
			pesquisaExameController.setAlterarFiltroPesquisaExames(false);
			pesquisaExameController.setVoltarPara(REDIRECT_LISTAR_TRANSPLANTES);
			pesquisaExameController.pesquisar();
			return REDIRECT_PESQUISA_EXAMES;
		}
		return null;
	}
	
	public String botaoConsultas(){
		if(itemSelecionado != null){
			consultaPacientePaginatorController.setPacCodigo(itemSelecionado.getCodigoPaciente());
			consultaPacientePaginatorController.pesquisar();
			return REDIRECT_PESQUISA_CONSULTAS;
		}
		return null;
	}
	
	public String botaoLaudoAIH(){
		if(itemSelecionado != null){
			laudoAIHPaginatorController.setPacCodigo(itemSelecionado.getCodigoPaciente());
			laudoAIHPaginatorController.setIniciaPesquisando(true);
			laudoAIHPaginatorController.setVoltarPara(REDIRECT_LISTAR_TRANSPLANTES);
			return REDIRECT_PESQUISA_LAUDOAIH;
		}
		return null;
	}
	
	public DynamicDataModel<ListarTransplantesVO> getDataModel3() {
		return dataModel3;
	}

	public void setDataModel(DynamicDataModel<ListarTransplantesVO> dataModel) {
		this.dataModel3 = dataModel;
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

	public ListarTransplantesVO getItemSelecionado() {
		return itemSelecionado;
	}

	public void setItemSelecionado(ListarTransplantesVO itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}
}