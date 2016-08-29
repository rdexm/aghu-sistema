package br.gov.mec.aghu.compras.cadastrosbasicos.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.model.ScoLocalizacaoProcesso;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;
import br.gov.mec.aghu.model.ScoTempoAndtPac;
import br.gov.mec.aghu.model.ScoTemposAndtPacsId;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class TempoLocalizacaoPacPaginatorController extends ActionController implements ActionPaginator {

	private static final String ETAPA_PAC_MODALIDADE_CRUD = "etapaPacModalidadeCRUD";

	private static final long serialVersionUID = -4243919536767889238L;

	private static final String TEMPO_LOCALIZACAO_PAC_CRUD = "tempoLocalizacaoPacCRUD";

	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	
	@Inject
	private SecurityController securityController;	

	private ScoTempoAndtPac tempoLocalizacaoPac = new ScoTempoAndtPac();

    private ScoLocalizacaoProcesso localizacaoProcesso;
	private ScoModalidadeLicitacao modalidadeLicitacao;
	
	private Short maxDiasPermanencia;
	
	@Inject @Paginator
	private DynamicDataModel<ScoTempoAndtPac> dataModel;
	
	private ScoTempoAndtPac selecionado;
	

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		boolean permissaoGravar = securityController.usuarioTemPermissao("cadastrarApoioPAC,gravar") || securityController.usuarioTemPermissao("cadastrarTempoLocalPac,gravar");
		dataModel.setUserEditPermission(permissaoGravar);
		dataModel.setUserRemovePermission(permissaoGravar); 
	}

	public void inicio(){
	 

	 

		
		if(this.dataModel.getPesquisaAtiva()){
			this.dataModel.reiniciarPaginator();
		}
		//this.//setIgnoreInitPageConfig(true);
		
	
	}
	
	
	@Override
	public Long recuperarCount() {
		if (this.getMaxDiasPermanencia() != null) {
			tempoLocalizacaoPac.setMaxDiasPermanencia(maxDiasPermanencia.shortValue());
		} else {
			tempoLocalizacaoPac.setMaxDiasPermanencia(null);
		}
		return comprasCadastrosBasicosFacade.listarTempoAndtPacCount(modalidadeLicitacao, localizacaoProcesso, tempoLocalizacaoPac);
	}

	@Override
	public List<ScoTempoAndtPac> recuperarListaPaginada(final Integer firstResult, final Integer maxResult, final String orderProperty, final boolean asc) {
		List<ScoTempoAndtPac> result = comprasCadastrosBasicosFacade.listarTempoAndtPac(
				firstResult, maxResult,	orderProperty, asc, 
				modalidadeLicitacao, localizacaoProcesso, tempoLocalizacaoPac);

		if (result == null) {
			result = new ArrayList<ScoTempoAndtPac>();
		}

		return result;
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}

	public String visualizar(ScoTemposAndtPacsId tempoLocPacId) {
		this.tempoLocalizacaoPac = comprasCadastrosBasicosFacade.obterTempoAndtPac(tempoLocPacId);
		return TEMPO_LOCALIZACAO_PAC_CRUD;
	}
	
	public String editar() {
		this.tempoLocalizacaoPac = selecionado;
	    return TEMPO_LOCALIZACAO_PAC_CRUD;
	}
	
	public String iniciarCadastroEtapaPacModalidade(ScoTemposAndtPacsId tempoLocPacId) {
		this.tempoLocalizacaoPac = comprasCadastrosBasicosFacade.obterTempoAndtPac(tempoLocPacId);
		return ETAPA_PAC_MODALIDADE_CRUD;
	}
	
	public String inserir() {
	    return TEMPO_LOCALIZACAO_PAC_CRUD;
	}
	
	public void limparPesquisa() {
		dataModel.limparPesquisa(); 
		this.setTempoLocalizacaoPac(new ScoTempoAndtPac());
		this.modalidadeLicitacao = null;
		this.localizacaoProcesso = null;
		this.maxDiasPermanencia = null;
	}

	public List<ScoModalidadeLicitacao> pesquisarModalidadeLicitacao(String modalidade) {
		return this.comprasCadastrosBasicosFacade.pesquisarModalidadeLicitacaoPorCodigoDescricao(modalidade);
	}
	
	public List<ScoLocalizacaoProcesso> pesquisarLocalizacaoProcesso(String localizacao) {
		return this.comprasCadastrosBasicosFacade.pesquisarLocalizacaoProcessoPorCodigoOuDescricao(localizacao, false);
	}

	public void excluir(){
		try{
			this.comprasCadastrosBasicosFacade.excluirTempoAndtPac(selecionado.getId());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_TEMPO_LOC_PAC_DELET_SUCESSO");		
		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}

	public ScoTempoAndtPac getTempoLocalizacaoPac() {
		return tempoLocalizacaoPac;
	}

	public void setTempoLocalizacaoPac(ScoTempoAndtPac tempoLocalizacaoPac) {
		this.tempoLocalizacaoPac = tempoLocalizacaoPac;
	}

	public ScoModalidadeLicitacao getModalidadeLicitacao() {
		return modalidadeLicitacao;
	}

	public void setModalidadeLicitacao(
			ScoModalidadeLicitacao modalidadeLicitacao) {
		this.modalidadeLicitacao = modalidadeLicitacao;
	}

	public ScoLocalizacaoProcesso getLocalizacaoProcesso() {
		return localizacaoProcesso;
	}

	public void setLocalizacaoProcesso(
			ScoLocalizacaoProcesso localizacaoProcesso) {
		this.localizacaoProcesso = localizacaoProcesso;
	} 

	public void setMaxDiasPermanencia(Short maxDiasPermanencia) {
		this.maxDiasPermanencia = maxDiasPermanencia;
	}

	public Short getMaxDiasPermanencia() {
		return maxDiasPermanencia;
	}

	public DynamicDataModel<ScoTempoAndtPac> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ScoTempoAndtPac> dataModel) {
		this.dataModel = dataModel;
	}

	public ScoTempoAndtPac getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(ScoTempoAndtPac selecionado) {
		this.selecionado = selecionado;
	}
}