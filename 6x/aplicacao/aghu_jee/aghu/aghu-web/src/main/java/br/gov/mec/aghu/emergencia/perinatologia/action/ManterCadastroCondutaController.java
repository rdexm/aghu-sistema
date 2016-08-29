package br.gov.mec.aghu.emergencia.perinatologia.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.model.McoConduta;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.locator.ServiceLocator;

public class ManterCadastroCondutaController extends ActionController implements ActionPaginator {
	private static final long serialVersionUID = -3993243944737162606L;

	@EJB
	IEmergenciaFacade emergenciaFacade;

	private boolean pesquisarCondutas;
	private boolean manterCondutas;
	private Boolean pesquisaAtiva;
	private Boolean novaSitucao;
	private Long filtroCodigo;
	private String filtroDescricao;
	private Integer filtroFaturamento;
	private DominioSituacao filtroSituacao;
	private Boolean editar;
	private String novaDescricao;
	private Integer novoFaturamento;
	private McoConduta mcoConduta;
	@Inject @Paginator
	private DynamicDataModel<McoConduta> dataModel;

	public void pesquisar() {
		setPesquisaAtiva(Boolean.TRUE);
		this.getDataModel().reiniciarPaginator();
	}

	public void limparPesquisa() {
		setPesquisaAtiva(Boolean.FALSE);
		setFiltroCodigo(null);
		setFiltroDescricao(null);
		setFiltroFaturamento(null);
		setFiltroSituacao(null);
		this.limparDadosInclucaoEdicao();
		this.getDataModel().limparPesquisa();
		this.cancelarEdicao();
	}

	public void persistirConduta() {
		if(editar){
			try {
				mcoConduta.setDescricao(getNovaDescricao());
				mcoConduta.setIndSituacao(DominioSituacao.getInstance(getNovaSitucao()));
				mcoConduta.setCod(getNovoFaturamento());
				
				this.emergenciaFacade.persistirConduta(mcoConduta);
				getDataModel().reiniciarPaginator();
				this.cancelarEdicao();
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_CONDUTAS");
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}
		else {
			McoConduta mcoConduta = new McoConduta();
			mcoConduta.setDescricao(getNovaDescricao());
			mcoConduta.setIndSituacao(DominioSituacao.getInstance(getNovaSitucao()));
			mcoConduta.setCod(getNovoFaturamento());
			
			try {
				this.emergenciaFacade.persistirConduta(mcoConduta);
				this.getDataModel().reiniciarPaginator();
				this.limparDadosInclucaoEdicao();
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_CONDUTA_INSERIDA_SUCESSO");
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}	
	}
	
	public void cancelarEdicao() {
		this.limparDadosInclucaoEdicao();
		this.editar = Boolean.FALSE;
	}

	public void ativarInativar(){
		this.emergenciaFacade.ativarInativarConduta(mcoConduta);
		this.getDataModel().reiniciarPaginator();
		apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_CONDUTA_SITUACAO");
	}
	
	public void editar() {
		this.editar = Boolean.TRUE; 
		setNovaDescricao(mcoConduta.getDescricao());
		setNovaSitucao(mcoConduta.getIndSituacao() != null ? mcoConduta.getIndSituacao().isAtivo() : Boolean.FALSE);
		setNovoFaturamento(mcoConduta.getCod());
	}

	private void limparDadosInclucaoEdicao() {
		setNovaDescricao(null);
		setNovaSitucao(Boolean.TRUE);
		setNovoFaturamento(null);
	}
	
	public Boolean convertDominioSituacaoToBoolean(DominioSituacao situacao){
		return situacao.isAtivo();
	}
	
	@PostConstruct
	public void init() {
		begin(conversation, true);

		this.manterCondutas = getPermissionService().usuarioTemPermissao(obterLoginUsuarioLogado(), "manterCondutas", "executar");
		this.pesquisarCondutas = getPermissionService().usuarioTemPermissao(obterLoginUsuarioLogado(), "pesquisarCondutas", "visualizar");
		this.novaSitucao = Boolean.TRUE;
		this.editar = Boolean.FALSE;
	}

	@Override
	public List<McoConduta> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.emergenciaFacade.listarCondutas(firstResult, maxResult, McoConduta.Fields.DESCRICAO.toString(), true, getFiltroCodigo(), getFiltroDescricao(),
				getFiltroFaturamento(), getFiltroSituacao());
	}

	@Override
	public Long recuperarCount() {
		return this.emergenciaFacade.listarCondutasCount(getFiltroCodigo(), getFiltroDescricao(), getFiltroFaturamento(), getFiltroSituacao());
	}

	private IPermissionService getPermissionService() {
		return ServiceLocator.getBeanRemote(IPermissionService.class, "aghu-casca");
	}

	public Long getFiltroCodigo() {
		return filtroCodigo;
	}

	public void setFiltroCodigo(Long filtroCodigo) {
		this.filtroCodigo = filtroCodigo;
	}

	public String getFiltroDescricao() {
		return filtroDescricao;
	}

	public void setFiltroDescricao(String filtroDescricao) {
		this.filtroDescricao = filtroDescricao;
	}

	public Integer getFiltroFaturamento() {
		return filtroFaturamento;
	}

	public void setFiltroFaturamento(Integer filtroFaturamento) {
		this.filtroFaturamento = filtroFaturamento;
	}

	public DominioSituacao getFiltroSituacao() {
		return filtroSituacao;
	}

	public void setFiltroSituacao(DominioSituacao filtroSituacao) {
		this.filtroSituacao = filtroSituacao;
	}

	public Boolean getPesquisaAtiva() {
		return pesquisaAtiva;
	}

	public void setPesquisaAtiva(Boolean pesquisaAtiva) {
		this.pesquisaAtiva = pesquisaAtiva;
	}

	public Boolean getEditar() {
		return editar;
	}

	public void setEditar(Boolean editar) {
		this.editar = editar;
	}

	public boolean isPesquisarCondutas() {
		return pesquisarCondutas;
	}

	public void setPesquisarCondutas(boolean pesquisarCondutas) {
		this.pesquisarCondutas = pesquisarCondutas;
	}

	public boolean isManterCondutas() {
		return manterCondutas;
	}

	public void setManterCondutas(boolean manterCondutas) {
		this.manterCondutas = manterCondutas;
	}

	public String getNovaDescricao() {
		return novaDescricao;
	}

	public void setNovaDescricao(String novaDescricao) {
		this.novaDescricao = novaDescricao;
	}

	public Integer getNovoFaturamento() {
		return novoFaturamento;
	}

	public void setNovoFaturamento(Integer novoFaturamento) {
		this.novoFaturamento = novoFaturamento;
	}

	public Boolean getNovaSitucao() {
		return novaSitucao;
	}

	public void setNovaSitucao(Boolean novaSitucao) {
		this.novaSitucao = novaSitucao;
	}

	public DynamicDataModel<McoConduta> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<McoConduta> dataModel) {
		this.dataModel = dataModel;
	}

	public McoConduta getMcoConduta() {
		return mcoConduta;
	}

	public void setMcoConduta(McoConduta mcoConduta) {
		this.mcoConduta = mcoConduta;
	}

}