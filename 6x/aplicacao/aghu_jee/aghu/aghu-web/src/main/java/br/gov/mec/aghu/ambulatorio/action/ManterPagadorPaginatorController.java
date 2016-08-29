package br.gov.mec.aghu.ambulatorio.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AacPagador;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responsável por controlar as ações da listagem de Órgãos pagadores
 * 
 * @author cicero.artifon
 */

public class ManterPagadorPaginatorController extends ActionController
		implements ActionPaginator {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<AacPagador> dataModel;
	private static final long serialVersionUID = -404951988430306467L;

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	/**
	 * Atributos dos campo de filtro da pesquisa do Órgão Pagador
	 */
	private Short codigoOrgaoPagador;
	private Short seqExclusao;
	private String descricaoOrgaoPagador;
	private DominioSituacao situacaoOrgaoPagador;
	private DominioGrupoConvenio convenioOrgaoPagador;

	private AacPagador pagadorSelecionado;

	private static final String REDIRECIONA_CRUD_PAGADOR = "manterPagadorCRUD";

	/**
	 * Atributo utilizado para controlar a exibicao do botao "incluir tipo de
	 * autorização"
	 */
	private boolean exibirBotaoIncluirOrgaoPagador;

	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
		this.setExibirBotaoIncluirOrgaoPagador(true);
	}

	public String iniciarInclusao() {
		this.setPagadorSelecionado(null);
		return REDIRECIONA_CRUD_PAGADOR;
	}

	public String editar() {
		return REDIRECIONA_CRUD_PAGADOR;
	}

	@Override
	public Long recuperarCount() {
		return this.ambulatorioFacade.countPagadorPaginado(codigoOrgaoPagador,
				descricaoOrgaoPagador, situacaoOrgaoPagador,
				convenioOrgaoPagador);
	}

	public void limparPesquisa() {
		this.codigoOrgaoPagador = null;
		this.descricaoOrgaoPagador = null;
		this.situacaoOrgaoPagador = null;
		this.convenioOrgaoPagador = null;
		this.dataModel.setPesquisaAtiva(false);
	}

	@Override
	public List<AacPagador> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {

		List<AacPagador> result = this.ambulatorioFacade
				.pesquisarPagadorPaginado(firstResult, maxResult,
						orderProperty, asc, codigoOrgaoPagador,
						descricaoOrgaoPagador, situacaoOrgaoPagador,
						convenioOrgaoPagador);
		if (result == null) {
			result = new ArrayList<AacPagador>();
		}

		return result;
	}

	public void excluir() {
		try {
			this.ambulatorioFacade.excluirPagador(pagadorSelecionado.getSeq());

			this.dataModel.reiniciarPaginator();
			apresentarMsgNegocio(Severity.INFO, "MSG_PAGADOR_EXCLUIDO_SUCESSO");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public IAmbulatorioFacade getAmbulatorioFacade() {
		return ambulatorioFacade;
	}

	public void setAmbulatorioFacade(IAmbulatorioFacade ambulatorioFacade) {
		this.ambulatorioFacade = ambulatorioFacade;
	}

	public Short getCodigoOrgaoPagador() {
		return codigoOrgaoPagador;
	}

	public void setCodigoOrgaoPagador(Short codigoOrgaoPagador) {
		this.codigoOrgaoPagador = codigoOrgaoPagador;
	}

	public Short getSeqExclusao() {
		return seqExclusao;
	}

	public void setSeqExclusao(Short seqExclusao) {
		this.seqExclusao = seqExclusao;
	}

	public String getDescricaoOrgaoPagador() {
		return descricaoOrgaoPagador;
	}

	public void setDescricaoOrgaoPagador(String descricaoOrgaoPagador) {
		this.descricaoOrgaoPagador = descricaoOrgaoPagador;
	}

	public DominioSituacao getSituacaoOrgaoPagador() {
		return situacaoOrgaoPagador;
	}

	public void setSituacaoOrgaoPagador(DominioSituacao situacaoOrgaoPagador) {
		this.situacaoOrgaoPagador = situacaoOrgaoPagador;
	}

	public DominioGrupoConvenio getConvenioOrgaoPagador() {
		return convenioOrgaoPagador;
	}

	public void setConvenioOrgaoPagador(
			DominioGrupoConvenio convenioOrgaoPagador) {
		this.convenioOrgaoPagador = convenioOrgaoPagador;
	}

	public DynamicDataModel<AacPagador> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AacPagador> dataModel) {
		this.dataModel = dataModel;
	}

	public AacPagador getPagadorSelecionado() {
		return pagadorSelecionado;
	}

	public void setPagadorSelecionado(AacPagador pagadorSelecionado) {
		this.pagadorSelecionado = pagadorSelecionado;
	}

	public boolean isExibirBotaoIncluirOrgaoPagador() {
		return exibirBotaoIncluirOrgaoPagador;
	}

	public void setExibirBotaoIncluirOrgaoPagador(
			boolean exibirBotaoIncluirOrgaoPagador) {
		this.exibirBotaoIncluirOrgaoPagador = exibirBotaoIncluirOrgaoPagador;
	}
}
