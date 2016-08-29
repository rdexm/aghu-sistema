package br.gov.mec.aghu.compras.solicitacaocompras.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.solicitacaocompra.business.ISolicitacaoComprasFacade;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoAutTempSolicita;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class AutTempSolicitacaoPaginatorController extends ActionController implements ActionPaginator {


	private static final long serialVersionUID = 6461505320953124314L;



	private static final String FORNECER_DIR_SOLICITAR_CRUD = "fornecerDirSolicitarCRUD";



	@EJB
	protected ICentroCustoFacade centroCustoFacade;
	
	@EJB
	private ISolicitacaoComprasFacade solicitacaoComprasFacade;
	
	@Inject
	private SecurityController securityController;	
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	private FccCentroCustos fccCentroCustos;
	
	private RapServidores servidor;
	
	private Date dataPesquisa;
	
	@Inject @Paginator
	private DynamicDataModel<ScoAutTempSolicita> dataModel;
	
	private ScoAutTempSolicita selecionado;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		boolean permissaoGravar = securityController.usuarioTemPermissao("cadastrarPermissoesCompras,gravar") || securityController.usuarioTemPermissao("cadastrarAdmCompras,gravar");
		dataModel.setUserEditPermission(permissaoGravar);
		dataModel.setUserRemovePermission(permissaoGravar); 
	}
	
	
	@Override
	public List<ScoAutTempSolicita> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return solicitacaoComprasFacade.pesquisarAutSolicitacaoTemp(firstResult, maxResult, orderProperty, asc, fccCentroCustos, servidor, dataPesquisa);
	}

	@Override
	public Long recuperarCount() {
		return solicitacaoComprasFacade.pesquisarAutSolicitacaoTempCount(this.fccCentroCustos, this.servidor, this.dataPesquisa);
	}
	
	public List<FccCentroCustos> listarCentroCustosSolic(String objPesquisa) {
		return this.returnSGWithCount(this.centroCustoFacade.pesquisarCentroCustos(objPesquisa),listarCentroCustosSolicCount(objPesquisa));
	}
	
	public Long listarCentroCustosSolicCount(String objPesquisa) {
		return this.centroCustoFacade.pesquisarCentroCustosCount(objPesquisa);
	}
	
	public List<RapServidores> listarServidores(String objPesquisa) {
		if (objPesquisa!=null && !"".equalsIgnoreCase((String) objPesquisa)){
			return this.registroColaboradorFacade.pesquisarServidor(objPesquisa);
			
		}else {
			return this.registroColaboradorFacade.pesquisarRapServidores();
		}
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}
	
	public String editar() {
	    return FORNECER_DIR_SOLICITAR_CRUD;
	}
	
	public String visualizar() {
	    return FORNECER_DIR_SOLICITAR_CRUD;
	}
	
	public String inserir() {
	    return FORNECER_DIR_SOLICITAR_CRUD;
	}
	
	public void limpar() {
		this.servidor = null;
		this.fccCentroCustos = null;
		this.dataPesquisa = null;
		dataModel.limparPesquisa();
	}
	
	
	public void excluir(){
		try{
			this.solicitacaoComprasFacade.excluirAutTempSolicitacao(selecionado.getId());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUIR_FOR_SOL");
		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}

	public FccCentroCustos getFccCentroCustos() {
		return fccCentroCustos;
	}

	public void setFccCentroCustos(FccCentroCustos fccCentroCustos) {
		this.fccCentroCustos = fccCentroCustos;
	}

	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	public Date getDataPesquisa() {
		return dataPesquisa;
	}

	public void setDataPesquisa(Date dataPesquisa) {
		this.dataPesquisa = dataPesquisa;
	}

	public DynamicDataModel<ScoAutTempSolicita> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ScoAutTempSolicita> dataModel) {
		this.dataModel = dataModel;
	}

	public ScoAutTempSolicita getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(ScoAutTempSolicita selecionado) {
		this.selecionado = selecionado;
	}
}