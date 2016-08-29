package br.gov.mec.aghu.prescricaoenfermagem.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.business.prescricaoenfermagem.IPrescricaoEnfermagemFacade;
import br.gov.mec.aghu.model.EpeDiagnostico;
import br.gov.mec.aghu.model.EpeGrupoNecesBasica;
import br.gov.mec.aghu.model.EpeSubgrupoNecesBasica;
import br.gov.mec.aghu.model.EpeSubgrupoNecesBasicaId;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

public class DiagnosticosSinaisSintomasListController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 126974068299977703L;
	
	private static final String PAGE_DIAGNOSTICOS_SINAIS_SINTOMAS_CRUD = "diagnosticosSinaisSintomasCRUD";

	@EJB
	private IPrescricaoEnfermagemFacade prescricaoEnfermagemFacade;
	
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
		if (gnbSeq != null) {
			grupo = prescricaoEnfermagemFacade.obterEpeGrupoNecesBasica(gnbSeq);
		} else {
			grupo = null;
		}
		if (gnbSeq != null && snbSequencia != null) {
			EpeSubgrupoNecesBasicaId idSubgrupo = new EpeSubgrupoNecesBasicaId(gnbSeq, snbSequencia);
			subGrupo = prescricaoEnfermagemFacade.obterEpeSubgrupoNecesBasicaPorChavePrimaria(idSubgrupo);
		} else {
			subGrupo = null;
		}
	
	}
	
	public String editar(){
		return PAGE_DIAGNOSTICOS_SINAIS_SINTOMAS_CRUD;
	}

	public String cancelar() {
		return "cancelar";
	}

	public void pesquisar() {
		dataModel.reiniciarPaginator();
		dataModel.setPesquisaAtiva(Boolean.TRUE);
		exibirBotaoNovo = Boolean.TRUE;
		if (grupo != null) {
			gnbSeq = grupo.getSeq();
		} else {
			gnbSeq = null;
		}
		if (subGrupo != null) {
			snbSequencia = subGrupo.getId().getSequencia();
		} else {
			snbSequencia = null;
		}
	}

	public List<EpeGrupoNecesBasica> pesquisarGrupo(String filtro) {
		return this.returnSGWithCount(prescricaoEnfermagemFacade.pesquisarGruposNecesBasicaTodos(filtro),pesquisarGrupoCount(filtro));
	}

	public Long pesquisarGrupoCount(String filtro) {
		return prescricaoEnfermagemFacade.pesquisarGruposNecesBasicaTodosCount(filtro);
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
	}

	public void desativarSubGrupo() {
		listaDiagnosticos = null;
		subGrupo = null;
		snbSequencia = null;
		grupo = null;
		gnbSeq = null;
		suggestionSubgrupo = Boolean.TRUE;
		diagnostico = null;
		suggestionDiagnosticos = Boolean.TRUE;
		dataModel.setPesquisaAtiva(Boolean.FALSE);
		exibirBotaoNovo = Boolean.FALSE;
	}

	public void ativarDiagnosticos() {
		suggestionDiagnosticos = false;
	}

	public void desativarDiagnosticos() {
		listaDiagnosticos = null;
		diagnostico = null;
		suggestionDiagnosticos = Boolean.TRUE;
		dataModel.setPesquisaAtiva(Boolean.FALSE);
		exibirBotaoNovo = Boolean.FALSE;
		snbSequencia = 0;
	}

	public void limparDiagnosticos() {
		listaDiagnosticos = null;
		dataModel.setPesquisaAtiva(Boolean.FALSE);
		exibirBotaoNovo = Boolean.FALSE;
	}

	public void limparPesquisa() {
		desativarSubGrupo();
	}

	@Override
	public List<EpeDiagnostico> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		Short snbGnbSeq = (grupo != null) ? grupo.getSeq() : null;
		Short snbSequencia = (subGrupo != null) ? subGrupo.getId().getSequencia() : null;
		Short dgnSequencia = (diagnostico != null) ? diagnostico.getId().getSequencia() : null;
		return prescricaoEnfermagemFacade.pesquisarDiagnosticosPorGrpSgrpDiag(snbGnbSeq, snbSequencia, dgnSequencia, firstResult,
				maxResult, orderProperty, asc);
	}

	@Override
	public Long recuperarCount() {
		Short snbGnbSeq = (grupo != null) ? grupo.getSeq() : null;
		Short snbSequencia = (subGrupo != null) ? subGrupo.getId().getSequencia() : null;
		Short dgnSequencia = (diagnostico != null) ? diagnostico.getId().getSequencia() : null;
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
