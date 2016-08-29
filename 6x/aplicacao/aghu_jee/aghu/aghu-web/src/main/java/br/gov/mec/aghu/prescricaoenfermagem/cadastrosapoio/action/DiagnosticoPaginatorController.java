package br.gov.mec.aghu.prescricaoenfermagem.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.business.prescricaoenfermagem.IPrescricaoEnfermagemFacade;
import br.gov.mec.aghu.business.prescricaoenfermagem.cadastrosapoio.IPrescricaoEnfermagemApoioFacade;
import br.gov.mec.aghu.model.EpeDiagnostico;
import br.gov.mec.aghu.model.EpeGrupoNecesBasica;
import br.gov.mec.aghu.model.EpeSubgrupoNecesBasica;
import br.gov.mec.aghu.model.EpeSubgrupoNecesBasicaId;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class DiagnosticoPaginatorController extends ActionController implements ActionPaginator {

	@Inject @Paginator
	private DynamicDataModel<EpeDiagnostico> dataModel;

	private static final long serialVersionUID = 126974068299977702L;

	@EJB
	private IPrescricaoEnfermagemFacade prescricaoEnfermagemFacade;

	@EJB
	private IPrescricaoEnfermagemApoioFacade prescricaoEnfermagemApoioFacade;

	private static final String CANCEL_PAGE = "subGrupoNecessidadesHumanasList";

	private EpeGrupoNecesBasica grupo;
	private EpeSubgrupoNecesBasica subGrupo;
	private Boolean suggestionSubgrupo;
	private Boolean desativarBotaoPesquisar;
	private List<EpeDiagnostico> listaDiagnosticos;
	private Short gnbSeq;
	private Short snbSequencia;
	private Boolean btCancelar;

	private EpeDiagnostico diagnosticoSelecionado;

	private static final String PAGE_CRUD = "diagnosticosCRUD";

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void iniciar() {

		if (suggestionSubgrupo == null) {
			suggestionSubgrupo = true;
		}
		
		if (gnbSeq != null && snbSequencia != null) {
			grupo = prescricaoEnfermagemFacade.obterEpeGrupoNecesBasica(gnbSeq);
			subGrupo = prescricaoEnfermagemFacade.obterEpeSubgrupoNecesBasicaPorChavePrimaria(new EpeSubgrupoNecesBasicaId(gnbSeq, snbSequencia));
			ativarPesquisa();
		}
	}

	public String excluir() {
		try {
			prescricaoEnfermagemApoioFacade.excluirDiagnostico(diagnosticoSelecionado.getId());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_MANTER_DIAGNOSTICO",
					diagnosticoSelecionado.getDescricao());
			dataModel.reiniciarPaginator();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	public String cancelar() {
		return CANCEL_PAGE;
	}

	public String redirecionaNovo() {
		return PAGE_CRUD;
	}

	public String redirecionaEditar() {
		return PAGE_CRUD;
	}

	public void pesquisar() {
		dataModel.reiniciarPaginator();
		dataModel.setPesquisaAtiva(Boolean.TRUE);
		setGnbSeq(subGrupo.getId().getGnbSeq());
		setSnbSequencia(subGrupo.getId().getSequencia());
	}

	public List<EpeGrupoNecesBasica> pesquisarGrupo(String filtro) {
		return this.returnSGWithCount(prescricaoEnfermagemFacade.pesquisarGrupoNecesBasicaAtivoOrderSeq(filtro),pesquisarGrupoCount(filtro));
	}

	public Long pesquisarGrupoCount(String filtro) {
		return prescricaoEnfermagemFacade.pesquisarGrupoNecesBasicaAtivoOrderSeqCount(filtro);
	}

	public List<EpeSubgrupoNecesBasica> pesquisarSubgrupos(String filtro) {
		Short gnbSeq = null;
		if (grupo != null) {
			gnbSeq = grupo.getSeq();
		}
		return this.returnSGWithCount(prescricaoEnfermagemFacade.pesquisarSubgrupoNecessBasicaAtivoOrderSeq(filtro, gnbSeq),pesquisarSubgruposCount(filtro));
	}

	public Long pesquisarSubgruposCount(String filtro) {
		Short gnbSeq = null;
		if (grupo != null) {
			gnbSeq = grupo.getSeq();
		}
		return prescricaoEnfermagemFacade.pesquisarSubgrupoNecessBasicaAtivoOrderSeqCount(filtro, gnbSeq);
	}

	public void desativarSubGrupo() {
		limparPesquisa();
	}

	public void ativarSubgrupo() {
		suggestionSubgrupo = false;
	}

	public String desativarPesquisa() {
		this.setListaDiagnosticos(null);
		subGrupo = null;
		desativarBotaoPesquisar = true;
		dataModel.setPesquisaAtiva(false);
		return null;
	}

	public void ativarPesquisa() {
		desativarBotaoPesquisar = false;
	}

	public void limparPesquisa() {
		listaDiagnosticos = null;
		subGrupo = null;
		grupo = null;
		suggestionSubgrupo = Boolean.TRUE;
		desativarBotaoPesquisar = null;
		gnbSeq = null;
		snbSequencia = null;
		dataModel.setPesquisaAtiva(false);
	}

	@Override
	public Long recuperarCount() {
		return prescricaoEnfermagemFacade.pesquisarDiagnosticosCount(gnbSeq, snbSequencia);
	}

	@Override
	public List<EpeDiagnostico> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return prescricaoEnfermagemFacade.pesquisarDiagnosticos(gnbSeq, snbSequencia, firstResult, maxResult,
				orderProperty, asc);
	}

	public void setSuggestionSubgrupo(Boolean suggestionSubgrupo) {
		this.suggestionSubgrupo = suggestionSubgrupo;
	}

	public Boolean getSuggestionSubgrupo() {
		return suggestionSubgrupo;
	}

	public void setListaDiagnosticos(List<EpeDiagnostico> listaDiagnosticos) {
		this.listaDiagnosticos = listaDiagnosticos;
	}

	public List<EpeDiagnostico> getListaDiagnosticos() {
		return listaDiagnosticos;
	}

	public void setSnbSequencia(Short snbSequencia) {
		this.snbSequencia = snbSequencia;
	}

	public Short getSnbSequencia() {
		return snbSequencia;
	}

	public Short getGnbSeq() {
		return gnbSeq;
	}

	public void setGnbSeq(Short gnbSeq) {
		this.gnbSeq = gnbSeq;
	}

	public void setGrupo(EpeGrupoNecesBasica grupo) {
		this.grupo = grupo;
	}

	public EpeGrupoNecesBasica getGrupo() {
		return grupo;
	}

	public void setSubGrupo(EpeSubgrupoNecesBasica subGrupo) {
		this.subGrupo = subGrupo;
	}

	public EpeSubgrupoNecesBasica getSubGrupo() {
		return subGrupo;
	}

	public void setDesativarBotaoPesquisar(Boolean desativarBotaoPesquisar) {
		this.desativarBotaoPesquisar = desativarBotaoPesquisar;
	}

	public Boolean getDesativarBotaoPesquisar() {
		return desativarBotaoPesquisar;
	}

	public void setBtCancelar(Boolean btCancelar) {
		this.btCancelar = btCancelar;
	}

	public Boolean getBtCancelar() {
		return btCancelar;
	}

	public DynamicDataModel<EpeDiagnostico> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<EpeDiagnostico> dataModel) {
		this.dataModel = dataModel;
	}

	public EpeDiagnostico getDiagnosticoSelecionado() {
		return diagnosticoSelecionado;
	}

	public void setDiagnosticoSelecionado(EpeDiagnostico diagnosticoSelecionado) {
		this.diagnosticoSelecionado = diagnosticoSelecionado;
	}
}
