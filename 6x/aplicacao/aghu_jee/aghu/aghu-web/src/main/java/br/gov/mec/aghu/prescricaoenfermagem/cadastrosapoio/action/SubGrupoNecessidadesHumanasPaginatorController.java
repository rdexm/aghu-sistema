package br.gov.mec.aghu.prescricaoenfermagem.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.business.prescricaoenfermagem.IPrescricaoEnfermagemFacade;
import br.gov.mec.aghu.business.prescricaoenfermagem.cadastrosapoio.IPrescricaoEnfermagemApoioFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.EpeGrupoNecesBasica;
import br.gov.mec.aghu.model.EpeSubgrupoNecesBasica;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class SubGrupoNecessidadesHumanasPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 8377124220277489320L;

	private static final String PAGE_GRUPO_NECESSIDADES_HUMANAS_LIST = "grupoNecessidadesHumanasList";
	private static final String PAGE_DIAGNOSTICOS_LIST = "diagnosticosList";

	@EJB
	private IPrescricaoEnfermagemFacade prescricaoEnfermagemFacade;

	@EJB
	private IPrescricaoEnfermagemApoioFacade prescricaoEnfermagemApoioFacade;

	@Inject
	private GrupoNecessidadesHumanasPaginatorController grupoNecessidadesHumanasPaginatorController;

	@Inject @Paginator
	private DynamicDataModel<EpeSubgrupoNecesBasica> dataModel;
	private Short seqGrupoNecessidadesHumanas;
	private EpeGrupoNecesBasica epeGrupoNecesBasica;
	List<EpeSubgrupoNecesBasica> listEpeSubgrupoNecesBasica;
	private EpeSubgrupoNecesBasica epeSubgrupoNecesBasica;
	private Boolean checkboxAtivo;
	private Boolean checkboxSubGrupoAtivo;
	private Boolean edicaoSubgrupo;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void iniciar() {
	 

		checkboxSubGrupoAtivo = true;
		epeGrupoNecesBasica = this.prescricaoEnfermagemFacade
				.obterDescricaoGrupoNecessidadesHumanasPorSeq(seqGrupoNecessidadesHumanas);
		if (epeGrupoNecesBasica != null && epeGrupoNecesBasica.getSituacao().equals(DominioSituacao.A)) {
			checkboxAtivo = true;
		}
		edicaoSubgrupo = false;
		listEpeSubgrupoNecesBasica = this.prescricaoEnfermagemApoioFacade
				.pesquisarSubgrupoNecessBasicaOrderSeq(seqGrupoNecessidadesHumanas);
		epeSubgrupoNecesBasica = new EpeSubgrupoNecesBasica();
	
	}

	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}

	public void limparPesquisa() {
		this.seqGrupoNecessidadesHumanas = null;
		dataModel.setPesquisaAtiva(Boolean.FALSE);
	}

	public String confirmar() {
		// Reinicia o paginator da tela anterior
		grupoNecessidadesHumanasPaginatorController.getDataModel().reiniciarPaginator();
		try {
			prescricaoEnfermagemApoioFacade.persistirGrupoNecessidadesHumanas(this.epeGrupoNecesBasica, checkboxAtivo);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_GRUPO_NECESSIDADES_HUMANAS",
					this.epeGrupoNecesBasica.getDescricao());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		return PAGE_GRUPO_NECESSIDADES_HUMANAS_LIST;
	}

	public String excluirSubgrupo() {
		try {
			prescricaoEnfermagemApoioFacade.removerSubgrupoNecessidadesHumanas(epeSubgrupoNecesBasica.getId());
			listEpeSubgrupoNecesBasica = this.prescricaoEnfermagemApoioFacade
					.pesquisarSubgrupoNecessBasicaOrderSeq(seqGrupoNecessidadesHumanas);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_SUBGRUPO_NECESSIDADES_HUMANAS",
					epeSubgrupoNecesBasica.getDescricao());
			cancelarEdicaoSubgrupo();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	public String gravarSubgrupo() {
		try {
			if (epeSubgrupoNecesBasica.getId() != null && epeSubgrupoNecesBasica.getId().getSequencia() != null) {
				prescricaoEnfermagemApoioFacade.persistirSubgrupoNecessidadesHumanas(epeSubgrupoNecesBasica,
						checkboxSubGrupoAtivo, seqGrupoNecessidadesHumanas);
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_SUBGRUPO_NECESSIDADES_HUMANAS",
						this.epeSubgrupoNecesBasica.getDescricao());
			} else {
				prescricaoEnfermagemApoioFacade.persistirSubgrupoNecessidadesHumanas(epeSubgrupoNecesBasica,
						checkboxSubGrupoAtivo, seqGrupoNecessidadesHumanas);
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_SUBGRUPO_NECESSIDADES_HUMANAS",
						this.epeSubgrupoNecesBasica.getDescricao());
			}
			cancelarEdicaoSubgrupo();
			listEpeSubgrupoNecesBasica = this.prescricaoEnfermagemApoioFacade
					.pesquisarSubgrupoNecessBasicaOrderSeq(seqGrupoNecessidadesHumanas);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	public String cancelarEdicaoSubgrupo() {
		edicaoSubgrupo = false;
		epeSubgrupoNecesBasica = new EpeSubgrupoNecesBasica();
		checkboxSubGrupoAtivo = true;
		return null;
	}

	public String iniciarEdicaoSubgrupo(EpeSubgrupoNecesBasica item) {
		edicaoSubgrupo = true;
		epeSubgrupoNecesBasica = item;
		if (item.getSituacao().equals(DominioSituacao.A)) {
			checkboxSubGrupoAtivo = true;
		} else {
			checkboxSubGrupoAtivo = false;
		}
		return null;
	}

	public void iniciarExclusaoSubgrupo(EpeSubgrupoNecesBasica item) {
		epeSubgrupoNecesBasica = item;
	}

	public void cancelarExclusaoSubgrupo() {
		epeSubgrupoNecesBasica = new EpeSubgrupoNecesBasica();
	}

	public String encaminharDiagnosticos() {
		return PAGE_DIAGNOSTICOS_LIST;
	}

	public String cancelar() {
		return PAGE_GRUPO_NECESSIDADES_HUMANAS_LIST;
	}

	public String processaCorLinha(EpeSubgrupoNecesBasica item) {
		if (item.equals(epeSubgrupoNecesBasica)) {
			return "background-color:yellow;";
		} else {
			return "";
		}
	}

	@Override
	public List<EpeSubgrupoNecesBasica> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {
		return null;
	}

	@Override
	public Long recuperarCount() {
		return 0l;
	}

	// ### Getters e Setters ###

	public Short getSeqGrupoNecessidadesHumanas() {
		return seqGrupoNecessidadesHumanas;
	}

	public void setSeqGrupoNecessidadesHumanas(Short seqGrupoNecessidadesHumanas) {
		this.seqGrupoNecessidadesHumanas = seqGrupoNecessidadesHumanas;
	}

	public Boolean getCheckboxAtivo() {
		return checkboxAtivo;
	}

	public void setCheckboxAtivo(Boolean checkboxAtivo) {
		this.checkboxAtivo = checkboxAtivo;
	}

	public List<EpeSubgrupoNecesBasica> getListEpeSubgrupoNecesBasica() {
		return listEpeSubgrupoNecesBasica;
	}

	public void setListEpeSubgrupoNecesBasica(List<EpeSubgrupoNecesBasica> listEpeSubgrupoNecesBasica) {
		this.listEpeSubgrupoNecesBasica = listEpeSubgrupoNecesBasica;
	}

	public EpeSubgrupoNecesBasica getEpeSubgrupoNecesBasica() {
		return epeSubgrupoNecesBasica;
	}

	public void setEpeSubgrupoNecesBasica(EpeSubgrupoNecesBasica epeSubgrupoNecesBasica) {
		this.epeSubgrupoNecesBasica = epeSubgrupoNecesBasica;
	}

	public void setCheckboxSubGrupoAtivo(Boolean checkboxSubGrupoAtivo) {
		this.checkboxSubGrupoAtivo = checkboxSubGrupoAtivo;
	}

	public Boolean getCheckboxSubGrupoAtivo() {
		return checkboxSubGrupoAtivo;
	}

	public void setEdicaoSubgrupo(Boolean edicaoSubgrupo) {
		this.edicaoSubgrupo = edicaoSubgrupo;
	}

	public Boolean getEdicaoSubgrupo() {
		return edicaoSubgrupo;
	}

	public void setEpeGrupoNecesBasica(EpeGrupoNecesBasica epeGrupoNecesBasica) {
		this.epeGrupoNecesBasica = epeGrupoNecesBasica;
	}

	public EpeGrupoNecesBasica getEpeGrupoNecesBasica() {
		return epeGrupoNecesBasica;
	}

	public DynamicDataModel<EpeSubgrupoNecesBasica> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<EpeSubgrupoNecesBasica> dataModel) {
		this.dataModel = dataModel;
	}
}
