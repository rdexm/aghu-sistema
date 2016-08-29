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

public class DiagnosticosEtiologiasPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 126974068299977703L;

	private static final String PAGE_DIAGNOSTICOS_ETIOLOGIAS_CRUD = "diagnosticosEtiologiasCRUD";

	@EJB
	private IPrescricaoEnfermagemFacade prescricaoEnfermagemFacade;

	@EJB
	private IPrescricaoEnfermagemApoioFacade prescricaoEnfermagemApoioFacade;

	@Inject @Paginator
	private DynamicDataModel<EpeDiagnostico> dataModel;

	private EpeGrupoNecesBasica grupo;
	private EpeSubgrupoNecesBasica subGrupo;
	private EpeDiagnostico diagnostico;
	private EpeDiagnostico diagnosticoSelecionado;
	private Boolean suggestionSubgrupo;
	private Boolean desativarBotaoPesquisar;
	private Boolean suggestionDiagnosticos;
	private Boolean exibirBotaoNovo;
	private List<EpeDiagnostico> listaDiagnosticos;
	private Short gnbSeq;
	private Short snbSequencia;
	private Boolean btCancelar;
	private String cameFrom;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void iniciar() {
	 

		if (suggestionSubgrupo == null) {
			suggestionSubgrupo = true;
		}
		if (suggestionDiagnosticos == null) {
			suggestionDiagnosticos = true;
		}
		if (desativarBotaoPesquisar == null) {
			desativarBotaoPesquisar = false;
		} else {
			dataModel.reiniciarPaginator();
		}
		if (gnbSeq != null && snbSequencia != null) {
			grupo = prescricaoEnfermagemFacade.obterEpeGrupoNecesBasica(gnbSeq);
			EpeSubgrupoNecesBasicaId idSubgrupo = new EpeSubgrupoNecesBasicaId(gnbSeq, snbSequencia);
			subGrupo = prescricaoEnfermagemFacade.obterEpeSubgrupoNecesBasicaPorChavePrimaria(idSubgrupo);
		}
	
	}

	public String editar() {
		return PAGE_DIAGNOSTICOS_ETIOLOGIAS_CRUD;
	}

	public String cancelar() {
		return "cancelar";
	}

	public void pesquisar() {
		dataModel.reiniciarPaginator();
		dataModel.setPesquisaAtiva(Boolean.TRUE);
		if (subGrupo != null) {
			gnbSeq = subGrupo.getId().getGnbSeq();
			snbSequencia = subGrupo.getId().getSequencia();
		}
	}

	public List<EpeGrupoNecesBasica> pesquisarGrupo(String filtro) {
		return this.returnSGWithCount(prescricaoEnfermagemFacade.pesquisarGruposNecesBasicaTodos(filtro),pesquisarGrupoCount(filtro));
	}

	public Long pesquisarGrupoCount(String filtro) {
		return prescricaoEnfermagemFacade.pesquisarGruposNecesBasicaTodosCount(filtro);
	}

	public IPrescricaoEnfermagemApoioFacade getPrescricaoEnfermagemApoioFacade() {
		return prescricaoEnfermagemApoioFacade;
	}

	public void setPrescricaoEnfermagemApoioFacade(IPrescricaoEnfermagemApoioFacade prescricaoEnfermagemApoioFacade) {
		this.prescricaoEnfermagemApoioFacade = prescricaoEnfermagemApoioFacade;
	}

	public List<EpeSubgrupoNecesBasica> pesquisarSubgrupos(String filtro) {
		Short gnbSeq = (grupo != null) ? grupo.getSeq() : null;
		return this.returnSGWithCount(prescricaoEnfermagemFacade.pesquisarSubgrupoNecessBasicaTodos(filtro, gnbSeq),pesquisarSubgruposCount(filtro));
	}

	public Long pesquisarSubgruposCount(String filtro) {
		Short gnbSeq = (grupo != null) ? grupo.getSeq() : null;
		return prescricaoEnfermagemFacade.pesquisarSubgrupoNecessBasicaTodosCount(filtro, gnbSeq);
	}

	public List<EpeDiagnostico> pesquisarDiagnosticos(String filtro) {
		Short gnbSeq, snbSequencia;
		gnbSeq = (grupo != null) ? grupo.getSeq() : null;
		snbSequencia = (subGrupo != null) ? subGrupo.getId().getSequencia() : null;
		return this.returnSGWithCount(prescricaoEnfermagemFacade.pesquisarDiagnosticosTodos(filtro, gnbSeq, snbSequencia),pesquisarDiagnosticosCount(filtro));
	}

	public Long pesquisarDiagnosticosCount(String filtro) {
		Short gnbSeq, snbSequencia;
		gnbSeq = (grupo != null) ? grupo.getSeq() : null;
		snbSequencia = (subGrupo != null) ? subGrupo.getId().getSequencia() : null;
		return prescricaoEnfermagemFacade.pesquisarDiagnosticosTodosCount(filtro, gnbSeq, snbSequencia);
	}

	public void ativarSubgrupo() {
		suggestionSubgrupo = false;
		dataModel.setPesquisaAtiva(Boolean.FALSE);
	}

	public void desativarSubGrupo() {
		listaDiagnosticos = null;
		subGrupo = null;
		grupo = null;
		suggestionSubgrupo = true;
		diagnostico = null;
		suggestionDiagnosticos = null;
		suggestionSubgrupo = null;
		desativarBotaoPesquisar = null;
		gnbSeq = null;
		snbSequencia = null;
		dataModel.setPesquisaAtiva(Boolean.FALSE);
	}
	
	

	public void ativarDiagnosticos() {
		suggestionDiagnosticos = false;
		dataModel.setPesquisaAtiva(Boolean.FALSE);
	}
	
	public void desativarPesquisar(){
		dataModel.setPesquisaAtiva(Boolean.FALSE);
	}

	public void desativarDiagnosticos() {
		snbSequencia = null;
		listaDiagnosticos = null;
		subGrupo = null;
		diagnostico = null;
		suggestionDiagnosticos = Boolean.TRUE;
		dataModel.setPesquisaAtiva(Boolean.FALSE);
	}

	public void limparDiagnosticos() {
		listaDiagnosticos = null;
		dataModel.setPesquisaAtiva(Boolean.FALSE);
	}

	public void limparPesquisa() {
		dataModel.limparPesquisa();
		desativarSubGrupo();
	}

	@Override
	public List<EpeDiagnostico> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		Short snbGnbSeq, snbSequencia, dgnSequencia;
		snbGnbSeq = (grupo != null) ? grupo.getSeq() : null;
		snbSequencia = (subGrupo != null) ? subGrupo.getId().getSequencia() : null;
		dgnSequencia = (diagnostico != null) ? diagnostico.getId().getSequencia() : null;
		return prescricaoEnfermagemFacade.pesquisarDiagnosticosPorGrpSgrpDiag(snbGnbSeq, snbSequencia, dgnSequencia, firstResult,
				maxResult, orderProperty, asc);
	}

	@Override
	public Long recuperarCount() {
		Short snbGnbSeq, snbSequencia, dgnSequencia;
		snbGnbSeq = (grupo != null) ? grupo.getSeq() : null;
		snbSequencia = (subGrupo != null) ? subGrupo.getId().getSequencia() : null;
		dgnSequencia = (diagnostico != null) ? diagnostico.getId().getSequencia() : null;
		return prescricaoEnfermagemFacade.pesquisarDiagnosticosPorGrpSgrpDiagCount(snbGnbSeq, snbSequencia, dgnSequencia);
	}

	public EpeGrupoNecesBasica getGrupo() {
		return grupo;
	}

	public void setGrupo(EpeGrupoNecesBasica grupo) {
		this.grupo = grupo;
	}

	public EpeSubgrupoNecesBasica getSubGrupo() {
		return subGrupo;
	}

	public void setSubGrupo(EpeSubgrupoNecesBasica subGrupo) {
		this.subGrupo = subGrupo;
	}

	public EpeDiagnostico getDiagnostico() {
		return diagnostico;
	}

	public void setDiagnostico(EpeDiagnostico diagnostico) {
		this.diagnostico = diagnostico;
	}

	public Boolean getSuggestionSubgrupo() {
		return suggestionSubgrupo;
	}

	public void setSuggestionSubgrupo(Boolean suggestionSubgrupo) {
		this.suggestionSubgrupo = suggestionSubgrupo;
	}

	public Boolean getDesativarBotaoPesquisar() {
		return desativarBotaoPesquisar;
	}

	public void setDesativarBotaoPesquisar(Boolean desativarBotaoPesquisar) {
		this.desativarBotaoPesquisar = desativarBotaoPesquisar;
	}

	public Boolean getSuggestionDiagnosticos() {
		return suggestionDiagnosticos;
	}

	public void setSuggestionDiagnosticos(Boolean suggestionDiagnosticos) {
		this.suggestionDiagnosticos = suggestionDiagnosticos;
	}

	public Boolean getExibirBotaoNovo() {
		return exibirBotaoNovo;
	}

	public void setExibirBotaoNovo(Boolean exibirBotaoNovo) {
		this.exibirBotaoNovo = exibirBotaoNovo;
	}

	public List<EpeDiagnostico> getListaDiagnosticos() {
		return listaDiagnosticos;
	}

	public void setListaDiagnosticos(List<EpeDiagnostico> listaDiagnosticos) {
		this.listaDiagnosticos = listaDiagnosticos;
	}

	public Short getGnbSeq() {
		return gnbSeq;
	}

	public void setGnbSeq(Short gnbSeq) {
		this.gnbSeq = gnbSeq;
	}

	public Short getSnbSequencia() {
		return snbSequencia;
	}

	public void setSnbSequencia(Short snbSequencia) {
		this.snbSequencia = snbSequencia;
	}

	public Boolean getBtCancelar() {
		return btCancelar;
	}

	public void setBtCancelar(Boolean btCancelar) {
		this.btCancelar = btCancelar;
	}

	public void setCameFrom(String cameFrom) {
		this.cameFrom = cameFrom;
	}

	public String getCameFrom() {
		return cameFrom;
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
