package br.gov.mec.aghu.sig.custos.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoCentroProducaoCustos;
import br.gov.mec.aghu.model.SigCentroProducao;
import br.gov.mec.aghu.sig.custos.business.ICustosSigCadastrosBasicosFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class PesquisarCentroProducaoPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -1036600425549438959L;
	private static final String MANTER_CENTRO_PRODUCAO = "manterCentroProducao";
	private static final Log LOG = LogFactory.getLog(PesquisarCentroProducaoPaginatorController.class);
	
	@Inject @Paginator
	private DynamicDataModel<SigCentroProducao> dataModel;
	private SigCentroProducao parametroSelecionado;
	private String nomeCentroProducao;
	private DominioTipoCentroProducaoCustos tipoCentroProducao;
	private DominioSituacao situacao;
	private Integer seq;
	private Boolean exibirBtnNovo = false;
	private Boolean recarregarLista = false;
	private SigCentroProducao centroProducao;
	//private 
	

	@EJB
	private ICustosSigCadastrosBasicosFacade custosSigCadastrosBasicosFacade;
	
	@EJB
	private IPermissionService permissionService;

	
	@PostConstruct
	protected void inicializar(){
		this.dataModel.setUserEditPermission(this.permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "editarCentroProducao", "editar"));
		this.dataModel.setUserRemovePermission(this.permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "excluirCentroProducao", "excluir"));
		this.begin(conversation);
		LOG.debug("begin conversation");
	}
	
	public void iniciar() {
	 

		if (this.centroProducao == null) {
			this.centroProducao = new SigCentroProducao();
		}
		this.setSituacao(DominioSituacao.A);
		if (this.getRecarregarLista()) {
			this.reiniciarPaginator();
			this.setRecarregarLista(false);
		}
	
	}
	
	@Override
	public List<SigCentroProducao> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {

		List<SigCentroProducao> result = this.custosSigCadastrosBasicosFacade.pesquisarCentroProducaoCentroTipoSituacao(firstResult, maxResult, orderProperty,
				asc, this.nomeCentroProducao, this.tipoCentroProducao, this.situacao);

		if (result == null) {
			result = new ArrayList<SigCentroProducao>();
		}
		return result;
	}

	@Override
	public Long recuperarCount() {
		return this.custosSigCadastrosBasicosFacade.listarCentroProducaoCount(this.nomeCentroProducao, this.tipoCentroProducao, this.situacao);
	}
	
	public void reiniciarPaginator(){
		this.dataModel.reiniciarPaginator();
	}	

	public void pesquisar() {
		this.reiniciarPaginator();
		this.setExibirBtnNovo(true);
		this.setAtivo(true);
	}

	public void limpar() {
		this.reiniciarPaginator();
		this.setCentroProducao(null);
		this.setNomeCentroProducao(null);
		this.setTipoCentroProducao(null);
		this.setSituacao(DominioSituacao.A);
		this.setAtivo(false);
		this.setExibirBtnNovo(false);
	}

	public void excluirCentroProducao() {
		try {
			centroProducao = this.custosSigCadastrosBasicosFacade.obterSigCentroProducao(seq);
			if (centroProducao != null) {
				this.custosSigCadastrosBasicosFacade.excluirCentroProducao(centroProducao.getSeq());
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_CENTRO_PRODUCAO", centroProducao.getNome());
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		this.setCentroProducao(null);
		this.reiniciarPaginator();
	}

	public String cadastrarCentroProducao() {
		this.seq = null;
		return MANTER_CENTRO_PRODUCAO;
	}

	public String editar() {
		return MANTER_CENTRO_PRODUCAO;
	}

	public String visualizar() {
		return MANTER_CENTRO_PRODUCAO;
	}

	// GETTERS E SETTERS-----------------------------------------------
	
	public Integer getseq() {
		return seq;
	}

	public void setseq(Integer seq) {
		this.seq = seq;
	}

	public SigCentroProducao getCentroProducao() {
		return centroProducao;
	}

	public void setCentroProducao(SigCentroProducao centroProducao) {
		this.centroProducao = centroProducao;
	}

	// NOVO
	public Boolean getExibirBtnNovo() {
		return exibirBtnNovo;
	}

	public void setExibirBtnNovo(Boolean exibirBtnNovo) {
		this.exibirBtnNovo = exibirBtnNovo;
	}

	public Integer getSeq() {
		return seq;
	}

	public String getNomeCentroProducao() {
		return nomeCentroProducao;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public void setNomeCentroProducao(String nomeCentroProducao) {
		this.nomeCentroProducao = nomeCentroProducao;
	}

	public DominioTipoCentroProducaoCustos getTipoCentroProducao() {
		return tipoCentroProducao;
	}

	public void setTipoCentroProducao(DominioTipoCentroProducaoCustos tipoCentroProducao) {
		this.tipoCentroProducao = tipoCentroProducao;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public Boolean getRecarregarLista() {
		return recarregarLista;
	}

	public void setRecarregarLista(Boolean recarregarLista) {
		this.recarregarLista = recarregarLista;
	} 

	public DynamicDataModel<SigCentroProducao> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<SigCentroProducao> dataModel) {
	 this.dataModel = dataModel;
	}

	public SigCentroProducao getParametroSelecionado() {
		return parametroSelecionado;
	}

	public void setParametroSelecionado(SigCentroProducao parametroSelecionado) {
		this.parametroSelecionado = parametroSelecionado;
		if(parametroSelecionado!=null){
			this.setSeq(parametroSelecionado.getSeq());
		}
	}
	
	public void setAtivo(boolean ativo){
		this.dataModel.setPesquisaAtiva(ativo);
	}
	
	public boolean isAtivo(){
		return this.dataModel.getPesquisaAtiva();
	}
	
}