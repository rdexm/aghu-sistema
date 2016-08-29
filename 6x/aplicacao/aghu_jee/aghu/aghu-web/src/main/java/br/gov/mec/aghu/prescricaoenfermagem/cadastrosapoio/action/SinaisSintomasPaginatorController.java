package br.gov.mec.aghu.prescricaoenfermagem.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.business.prescricaoenfermagem.cadastrosapoio.IPrescricaoEnfermagemApoioFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.EpeCaractDefinidora;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class SinaisSintomasPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 8377124220277489320L;

	private static final String PAGE_SINAIS_SINTOMAS_CRUD = "sinaisSintomasCRUD";

	@EJB
	private IPrescricaoEnfermagemApoioFacade prescricaoEnfermagemApoioFacade;

	@EJB
	private IPermissionService permissionService;
	
	@Inject @Paginator
	private DynamicDataModel<EpeCaractDefinidora> dataModel;

	private Integer codigo;
	private String descricao;
	private DominioSituacao situacao;
	private Integer codigoExclusao;
	private Boolean exibirBotaoNovo;
	private EpeCaractDefinidora sinaisSintomasSelecionado;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void inicio(){
		this.dataModel.setUserRemovePermission(this.permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "manterSinalSintoma", "alterar"));
		this.dataModel.setUserEditPermission(this.permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "manterSinalSintoma", "alterar"));
		
	}

	public void pesquisar() {
		dataModel.reiniciarPaginator();
		exibirBotaoNovo = true;
	}

	public String redirecionaNovo() {
		return PAGE_SINAIS_SINTOMAS_CRUD;
	}

	public String redirecionaEditar() {
		return PAGE_SINAIS_SINTOMAS_CRUD;
	}

	public void limpar() {
		codigo = null;
		descricao = null;
		situacao = null;
		exibirBotaoNovo = false;
		dataModel.setPesquisaAtiva(Boolean.FALSE);
	}

	@Override
	public List<EpeCaractDefinidora> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {
		return prescricaoEnfermagemApoioFacade.listarSinaisSintomasPorCodigoDescricaoSituacao(firstResult, maxResult,
				orderProperty, asc, codigo, descricao, situacao);
	}

	@Override
	public Long recuperarCount() {
		return prescricaoEnfermagemApoioFacade.listarSinaisSintomasPorCodigoDescricaoSituacaoCount(codigo, descricao, situacao);
	}

	public void excluir() {
		dataModel.reiniciarPaginator();

		try {
			if (sinaisSintomasSelecionado != null) {
				prescricaoEnfermagemApoioFacade.verificaExclusaoEpeCaractDefinidora(sinaisSintomasSelecionado);
				prescricaoEnfermagemApoioFacade.removerSinaisSintomas(sinaisSintomasSelecionado.getCodigo());
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_SINAIS_SINTOMAS",
						sinaisSintomasSelecionado.getDescricao());
			} else {
				this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_REMOCAO_SINAIS_SINTOMAS_INVALIDO");
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		codigo = null;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public Integer getCodigoExclusao() {
		return codigoExclusao;
	}

	public void setCodigoExclusao(Integer codigoExclusao) {
		this.codigoExclusao = codigoExclusao;
	}

	public Boolean getExibirBotaoNovo() {
		return exibirBotaoNovo;
	}

	public void setExibirBotaoNovo(Boolean exibirBotaoNovo) {
		this.exibirBotaoNovo = exibirBotaoNovo;
	}

	public DynamicDataModel<EpeCaractDefinidora> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<EpeCaractDefinidora> dataModel) {
		this.dataModel = dataModel;
	}

	public EpeCaractDefinidora getSinaisSintomasSelecionado() {
		return sinaisSintomasSelecionado;
	}

	public void setSinaisSintomasSelecionado(EpeCaractDefinidora sinaisSintomasSelecionado) {
		this.sinaisSintomasSelecionado = sinaisSintomasSelecionado;
	}
}
