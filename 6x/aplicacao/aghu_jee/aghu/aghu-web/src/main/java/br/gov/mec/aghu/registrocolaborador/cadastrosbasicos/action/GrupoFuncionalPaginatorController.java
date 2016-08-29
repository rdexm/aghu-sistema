package br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.RapGrupoFuncional;
import br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.business.ICadastrosBasicosFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class GrupoFuncionalPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -242851117099768331L;

	@Inject @Paginator
	private DynamicDataModel<RapGrupoFuncional> dataModel;

	private static final Log LOG = LogFactory.getLog(GrupoFuncionalPaginatorController.class);

	private static final String GRUPO_FUNCIONAL_FORM = "cadastrarGrupoFuncional";

	@EJB
	private ICadastrosBasicosFacade cadastrosBasicosFacade;

	private RapGrupoFuncional rapGrupoFuncional;

	private Integer codigoDaTela;
	private String descricao;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	@Override
	public Long recuperarCount() {

		Short codigo = null;

		if (this.codigoDaTela != null) {
			codigo = this.codigoDaTela.shortValue();
		}

		return cadastrosBasicosFacade.montarConsultaCount(codigo, descricao);
	}

	@Override
	public List<RapGrupoFuncional> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {

		Short codigo = null;

		if (this.codigoDaTela != null) {
			codigo = this.codigoDaTela.shortValue();
		}

		List<RapGrupoFuncional> list = cadastrosBasicosFacade.pesquisarGrupoFuncional(codigo, this.descricao, firstResult,
				maxResult, orderProperty, asc);

		if (list == null) {
			list = new ArrayList<RapGrupoFuncional>();
		}

		return list;
	}

	public void pesquisar() {

		this.dataModel.reiniciarPaginator();

		// Ativa o uso de paginação
		this.dataModel.setPesquisaAtiva(true);
	}

	public void limpar() {
		this.dataModel.reiniciarPaginator();
		this.dataModel.setPesquisaAtiva(false);
		this.codigoDaTela = null;
		this.descricao = null;
	}

	public String altera() {
		return GRUPO_FUNCIONAL_FORM;
	}

	public String cadastrar() {
		return GRUPO_FUNCIONAL_FORM;
	}

	public void excluir() {

		try {
			this.cadastrosBasicosFacade.excluir(rapGrupoFuncional.getCodigo());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_EXCLUSAO_GRUPO_FUNCIONAL");

		} catch (ApplicationBusinessException e) {
			LOG.error(e.getMessage(), e);
			apresentarExcecaoNegocio(e);
		}
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {

		if (descricao != null) {
			this.descricao = descricao.toUpperCase();
		} else {
			this.descricao = descricao;
		}
	}

	public RapGrupoFuncional getRapGrupoFuncional() {
		return rapGrupoFuncional;
	}

	public void setRapGrupoFuncional(RapGrupoFuncional rapGrupoFuncional) {
		this.rapGrupoFuncional = rapGrupoFuncional;
	}

	public Integer getCodigoDaTela() {
		return codigoDaTela;
	}

	public void setCodigoDaTela(Integer codigoDaTela) {
		this.codigoDaTela = codigoDaTela;
	}

	public DynamicDataModel<RapGrupoFuncional> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<RapGrupoFuncional> dataModel) {
		this.dataModel = dataModel;
	}
}
