package br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AfaViaAdministracao;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.business.ICadastrosBasicosPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.Severity;

public class ViasAdministracaoPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 6498823838159585925L;

	private static final String PAGE_VIAS_ADMINISTRACAO_CRUD = "viasAdministracaoCrud";

	@EJB
	private ICadastrosBasicosPrescricaoMedicaFacade cadastrosBasicosPrescricaoMedicaFacade;

	@Inject
	private IParametroFacade parametroFacade;

	@Inject @Paginator
	private DynamicDataModel<AfaViaAdministracao> dataModel;

	private AfaViaAdministracao parametroSelecionado;

	private boolean exibirBotaoNovo;
	private String siglaViasAdministracao;
	private String descricaoViasAdministracao;
	private DominioSituacao indSituacao;

	@PostConstruct
	public void init() {
		begin(conversation, true);
	}

	public void pesquisar() {
		this.getDataModel().reiniciarPaginator();
		this.setExibirBotaoNovo(true);
	}

	public void limparPesquisa() {
		this.setSiglaViasAdministracao(null);
		this.setDescricaoViasAdministracao(null);
		this.setExibirBotaoNovo(false);
		this.setIndSituacao(null);
		this.getDataModel().limparPesquisa();
	}

	@Override
	public Long recuperarCount() {
		return cadastrosBasicosPrescricaoMedicaFacade.pesquisarViasAdministracaoCount(siglaViasAdministracao, descricaoViasAdministracao, indSituacao);
	}

	@Override
	public List<AfaViaAdministracao> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {

		List<AfaViaAdministracao> result = cadastrosBasicosPrescricaoMedicaFacade.pesquisarViasAdministracao(firstResult, maxResult, orderProperty, asc, siglaViasAdministracao,
				descricaoViasAdministracao, indSituacao);

		if (result == null) {
			result = new ArrayList<AfaViaAdministracao>();
		}

		return result;
	}

	public void excluir() throws BaseException {
		try {
			AghParametros parametro = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_DIAS_PERM_DEL_MPM);

			Integer vlrParam = 0;

			if (parametro.getVlrNumerico() != null) {
				vlrParam = parametro.getVlrNumerico().intValue();
			}

			cadastrosBasicosPrescricaoMedicaFacade.removerViaAdministracao(this.parametroSelecionado.getSigla(), vlrParam);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_VIAS_ADMINISTRACAO", parametroSelecionado.getDescricao());

			this.setExibirBotaoNovo(false);
		} catch (BaseListException e) {
			apresentarExcecaoNegocio(e);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public String inserirEditar() {
		return PAGE_VIAS_ADMINISTRACAO_CRUD;
	}

	/*
	 * Getters and Setters
	 */
	/**
	 * @return the exibirBotaoNovo
	 */
	public boolean isExibirBotaoNovo() {
		return exibirBotaoNovo;
	}

	/**
	 * @param exibirBotaoNovo
	 *            the exibirBotaoNovo to set
	 */
	public void setExibirBotaoNovo(boolean exibirBotaoNovo) {
		this.exibirBotaoNovo = exibirBotaoNovo;
	}

	/**
	 * @return the siglaViasAdministracao
	 */
	public String getSiglaViasAdministracao() {
		return siglaViasAdministracao;
	}

	/**
	 * @param siglaViasAdministracao
	 *            the siglaViasAdministracao to set
	 */
	public void setSiglaViasAdministracao(String siglaViasAdministracao) {
		this.siglaViasAdministracao = siglaViasAdministracao;
	}

	/**
	 * @return the descricaoViasAdministracao
	 */
	public String getDescricaoViasAdministracao() {
		return descricaoViasAdministracao;
	}

	/**
	 * @param descricaoViasAdministracao
	 *            the descricaoViasAdministracao to set
	 */
	public void setDescricaoViasAdministracao(String descricaoViasAdministracao) {
		this.descricaoViasAdministracao = descricaoViasAdministracao;
	}

	/**
	 * @return the indSituacao
	 */
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	/**
	 * @param indSituacao
	 *            the indSituacao to set
	 */
	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public DynamicDataModel<AfaViaAdministracao> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AfaViaAdministracao> dataModel) {
		this.dataModel = dataModel;
	}

	public AfaViaAdministracao getParametroSelecionado() {
		return parametroSelecionado;
	}

	public void setParametroSelecionado(AfaViaAdministracao parametroSelecionado) {
		this.parametroSelecionado = parametroSelecionado;
	}

}