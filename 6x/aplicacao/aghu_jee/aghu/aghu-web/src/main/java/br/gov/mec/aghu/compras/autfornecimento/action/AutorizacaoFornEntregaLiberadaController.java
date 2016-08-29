package br.gov.mec.aghu.compras.autfornecimento.action;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.context.RequestContext;

import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.autfornecimento.vo.PesquisaAutorizacaoFornecimentoVO;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.vo.AutorizacaoFornPedidosVO;
import br.gov.mec.aghu.compras.vo.PesquisaAutFornPedidosVO;
import br.gov.mec.aghu.dominio.DominioAfpPublicado;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoGrupoServico;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.VScoFornecedor;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

import com.itextpdf.text.DocumentException;



public class AutorizacaoFornEntregaLiberadaController extends ActionController implements ActionPaginator {
	
	@Inject @Paginator
	private DynamicDataModel<PesquisaAutorizacaoFornecimentoVO> dataModel;

	private static final String AUTORIZACAO_FORNECIMENTO_CRUD = "autorizacaoFornecimentoCRUD";

	private static final String PARCELAS_AUTORIZACAO_FORN_PEDIDO = "parcelasAutorizacaoFornPedido";

	private static final Log LOG = LogFactory.getLog(AutorizacaoFornEntregaLiberadaController.class);

	private static final long serialVersionUID = -1512724787813172347L;
	
	
	private String voltarParaUrl;
	
	private Boolean pesquisou = false;
	private Boolean readOnlyNroComplementoAF;
	private Boolean readOnlyNumeroAFP;
	
	private VScoFornecedor vFornecedor;

	private PesquisaAutFornPedidosVO filtro = new PesquisaAutFornPedidosVO();	
	private List<AutorizacaoFornPedidosVO> listaPedidosAF = new ArrayList<AutorizacaoFornPedidosVO>();
	
	private List<AutorizacaoFornPedidosVO> listaSelecionados = new ArrayList<AutorizacaoFornPedidosVO>();
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private IAutFornecimentoFacade autFornecimentoFacade;
	
	@Inject
	private EnvioEmailAssinaturaAFController envioEmailAssinaturaAFController;
	
	@Inject
	private RelatorioPacientesCUMController relatorioPacientesCUMController;
	
	@Inject
	private RelatorioAutorizacaoFornecimentoController relatorioAutorizacaoFornecimentoController;
	
	private AutorizacaoFornPedidosVO itemSelecionadoPublicacao;
	
	private String mensagemModalPublicar;
	
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void pesquisar() {
		
	
			this.listaSelecionados = null;
			this.listaSelecionados = new ArrayList<AutorizacaoFornPedidosVO>();
			this.setPesquisou(true);
			this.setarCamposFiltro();
			//this.listaPedidosAF = autFornecimentoFacade.pesquisarAutFornPedidosPorFiltro(filtro);
			this.dataModel.reiniciarPaginator();
	
	}
	
	@Override
	public Long recuperarCount() {
		return this.autFornecimentoFacade.pesquisarAutFornPedidosPorFiltroCount(filtro);
	}

	@Override
	public List<AutorizacaoFornPedidosVO> recuperarListaPaginada(Integer first, Integer max,
			String order, boolean asc) {	
		
		try {
			listaPedidosAF = autFornecimentoFacade.pesquisarAutFornPedidosPorFiltro(first, max, filtro);
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		}
			
		return  listaPedidosAF;
	}
	
	public void setarCamposFiltro(){
		this.filtro.setIndImpressa(null);
		this.filtro.setIndEnviada(null);
		this.filtro.setFornecedor(null);

		if (this.filtro.getImpressa() != null) {
			if (this.filtro.getImpressa().equals(DominioSimNao.S)) { this.filtro.setIndImpressa(true); }
			else { this.filtro.setIndImpressa(false); }
		}
		
		if (this.filtro.getEnviada() != null) {
			if (this.filtro.getEnviada().equals(DominioSimNao.S)) { this.filtro.setIndEnviada(true); }
			else { this.filtro.setIndEnviada(false); }
		}
		
		if (this.getvFornecedor() != null) {
			this.filtro.setFornecedor(this.comprasFacade.obterFornecedorPorChavePrimaria(this.getvFornecedor().getNumeroFornecedor()));				
		}
	}
	
	public void limparPesquisa() {
		this.setFiltro(new PesquisaAutFornPedidosVO());
		this.setvFornecedor(null);
		this.setListaPedidosAF(new ArrayList<AutorizacaoFornPedidosVO>());
		this.setPesquisou(false);
	}
	
	public void naoEnviarAFP() {
		
		try {
			if(listaSelecionados.size() == 0){
				this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_SEL_REG_NAO_ENV_AF_ENTREGA_LIB");
				return;
			}
			
			//Atualiza SCO_AF_PEDIDOS e SCO_PROGR_ENTREGA_ITENS_AF
			this.autFornecimentoFacade.alterarAFP(listaSelecionados);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NAO_ENV_AF_ENTREGA_LIB");
			this.pesquisar();
			
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
			LOG.error("Erro",e);
		}
	}
	
	public void selecionarItem(AutorizacaoFornPedidosVO item) {
		for (AutorizacaoFornPedidosVO vo: this.listaPedidosAF) {
			if(vo.equals(item)) {
				vo.setSelecionado(item.getSelecionado());
				if(item.getSelecionado()) {
					this.listaSelecionados.add(item);
				} else {
					this.listaSelecionados.remove(item);
				}
				break;
			}
		}
	}	
	
	
	
	//Suggestion Fornecedor
	public List<VScoFornecedor> pesquisarFornecedoresPorCgcCpfRazaoSocial(String parametro){		
		return this.returnSGWithCount(this.comprasFacade.pesquisarVFornecedorPorCgcCpfRazaoSocial(parametro),pesquisarFornecedoresPorCgcCpfRazaoSocialCount(parametro));
	}
	
	public Long pesquisarFornecedoresPorCgcCpfRazaoSocialCount(String parametro){
		return this.comprasFacade.pesquisarVFornecedorPorCgcCpfRazaoSocialCount(parametro);
	}
	
	//Suggestion Servidor Gestor
	public List<RapServidores> pesquisarGestor(String parametro) {
		return this.returnSGWithCount(this.registroColaboradorFacade.pesquisarResponsaveis(parametro),pesquisarGestorCount(parametro));
	}
	
	public Long pesquisarGestorCount(String parametro){
		return this.registroColaboradorFacade.pesquisarResponsaveisCount(parametro);
	}

	
	//Suggestion Grupo Material
	public List<ScoGrupoMaterial> pesquisarGrupoMaterial(String parametro){
		return this.returnSGWithCount(this.comprasFacade.pesquisarGrupoMaterialPorFiltro(parametro),pesquisarGrupoMaterialCount(parametro));
	}
	
	public Long pesquisarGrupoMaterialCount(String parametro){
		return this.comprasFacade.pesquisarGrupoMaterialPorFiltroCount(parametro);
	}
	
	//Suggestion Material
	public List<ScoMaterial> pesquisarMaterial(String parametro){
		return this.returnSGWithCount(this.comprasFacade.listarScoMateriais(parametro, null, true),pesquisarMaterialCount(parametro));
	}

	public Long pesquisarMaterialCount(String parametro){
		return this.comprasFacade.listarScoMateriaisCount(parametro, null, true);
	}
	
	//Suggestion Grupo Serviço
	public List<ScoGrupoServico> pesquisarGrupoServico(String parametro){
		return this.returnSGWithCount(this.comprasFacade.pesquisarGrupoServico(parametro),pesquisarGrupoServicoCount(parametro));
	}
	
	public Long pesquisarGrupoServicoCount(String parametro){
		return this.comprasFacade.pesquisarGrupoServicoCount(parametro);
	}
	
	//Suggestion Serviço
	public List<ScoServico> pesquisarServicos(String parametro){
		return this.returnSGWithCount(this.comprasFacade.listarServicos(parametro),pesquisarServicosCount(parametro));
	}
	public Long pesquisarServicosCount(String parametro){
		return this.comprasFacade.listarServicosCount(parametro);
	}		
	
	//Gets/Sets
	public Boolean getReadOnlyNroComplementoAF() {
		
		if (this.getFiltro().getNumeroPAC() != null) { readOnlyNroComplementoAF = false; } 
		else 
		{ 
			this.filtro.setNroComplementoAF(null);
			this.filtro.setNumeroAFP(null);
			readOnlyNroComplementoAF = true;
		}

		return readOnlyNroComplementoAF;	
	}
	
	public Boolean getReadOnlyNumeroAFP() {
		
		if (this.getFiltro().getNumeroPAC() != null && this.getFiltro().getNroComplementoAF() != null) {
			readOnlyNumeroAFP = false; } 
		else 
		{ 
			this.filtro.setNumeroAFP(null);
			readOnlyNumeroAFP = true; 
		}

		return readOnlyNumeroAFP;	
	}
	
	public void verificarListaEnviarEmail(){
		if(listaSelecionados.size() == 0){
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_SEL_REG_ENV_AF_ENTREGA_LIB");
		}
		try {			
			this.envioEmailAssinaturaAFController.enviarEmail(null,listaSelecionados);
	//		this.pesquisar();
			
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_ENVIAR_EMAIL");
		}
	}
	
	public void validaPublicarAF(){
		
		//for (AutorizacaoFornPedidosVO itemSelecionados : listaSelecionados ){
		if (listaSelecionados != null &&
				listaSelecionados.size() > 0){ 	
			
			AutorizacaoFornPedidosVO  itemSelecionados = listaSelecionados.get(0);
			
			
			    itemSelecionadoPublicacao = itemSelecionados;
			    if (DominioAfpPublicado.N.equals(itemSelecionados.getIndPublicado())){
			    	mensagemModalPublicar = this.getBundle().getString("MENSAGEM_AFP_NAO_PUBLICADA").replace("{0}", itemSelecionadoPublicacao.getLctNumero().toString());
			    	mensagemModalPublicar = mensagemModalPublicar.replace("{1}", itemSelecionadoPublicacao.getNumeroAFP().toString());
					
			    }
			    else {
			    	mensagemModalPublicar = this.getBundle().getString("MENSAGEM_AFP_PUBLICADA").replace("{0}", itemSelecionadoPublicacao.getLctNumero().toString());
			    	mensagemModalPublicar = mensagemModalPublicar.replace("{1}", itemSelecionadoPublicacao.getNumeroAFP().toString());
			    	if (itemSelecionadoPublicacao.getDtPublicacao() != null){
			    		SimpleDateFormat sdf_dataPublicacao = new SimpleDateFormat("dd/MM/yyyy");
			    		String dataPublicacao = sdf_dataPublicacao.format(itemSelecionadoPublicacao.getDtPublicacao());
			    	    mensagemModalPublicar = mensagemModalPublicar.replace("{2}", dataPublicacao);
			    	}
			    }
			    this.listaSelecionados.remove(itemSelecionados);
			    
			    this.openDialog("modalPublicacaoWG");
			    
		} else {
			this.pesquisar();
			RequestContext requestContext = RequestContext.getCurrentInstance();
			requestContext.update("resultList");
			
		}
			
	}
	
	public void publicarAF() {
		try {
			this.gerarRelatorio();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		this.validaPublicarAF();
	}
	
	public void gerarRelatorio() throws ApplicationBusinessException, JRException, IOException, DocumentException {

		if (itemSelecionadoPublicacao != null){
			relatorioAutorizacaoFornecimentoController.setNumPac(itemSelecionadoPublicacao.getLctNumero());
			relatorioAutorizacaoFornecimentoController.setNroComplemento(itemSelecionadoPublicacao.getNumeroComplemento());
	 	    relatorioAutorizacaoFornecimentoController.setAfpNumero(itemSelecionadoPublicacao.getNumeroAFP());
			relatorioAutorizacaoFornecimentoController.setItemVersaoAf(null);
		    relatorioAutorizacaoFornecimentoController.gerarArquivoPDF();
		    
	        if (autFornecimentoFacade.publicaAfpFornecedorEntrega(itemSelecionadoPublicacao.getAfnNumero(), itemSelecionadoPublicacao.getNumeroAFP())){
	            relatorioPacientesCUMController.setAfeAfnNumero(itemSelecionadoPublicacao.getAfnNumero());
	            relatorioPacientesCUMController.setAfeNumero(itemSelecionadoPublicacao.getNumeroAFP());
	            relatorioPacientesCUMController.gerarArquivoPdf();
			}
		}
	}

	
	
	public String visualizarParcelasAfp(){
		return PARCELAS_AUTORIZACAO_FORN_PEDIDO;
	}
	
	public String verAf(){
		return AUTORIZACAO_FORNECIMENTO_CRUD;
	}
	
	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}
	
	public Boolean getPesquisou() {
		return pesquisou;
	}

	public void setPesquisou(Boolean pesquisou) {
		this.pesquisou = pesquisou;
	}	
	
	public VScoFornecedor getvFornecedor() {
		return vFornecedor;
	}

	public void setvFornecedor(VScoFornecedor vFornecedor) {
		this.vFornecedor = vFornecedor;
	}
	
	public PesquisaAutFornPedidosVO getFiltro() {
		return filtro;
	}

	public void setFiltro(PesquisaAutFornPedidosVO filtro) {
		this.filtro = filtro;
	}

	public List<AutorizacaoFornPedidosVO> getListaPedidosAF() {
		return listaPedidosAF;
	}

	public void setListaPedidosAF(List<AutorizacaoFornPedidosVO> listaPedidosAF) {
		this.listaPedidosAF = listaPedidosAF;
	}

	public DynamicDataModel<PesquisaAutorizacaoFornecimentoVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(
			DynamicDataModel<PesquisaAutorizacaoFornecimentoVO> dataModel) {
		this.dataModel = dataModel;
	}

	public String getMensagemModalPublicar() {
		return mensagemModalPublicar;
	}

	public void setMensagemModalPublicar(String mensagemModalPublicar) {
		this.mensagemModalPublicar = mensagemModalPublicar;
	}

	
}