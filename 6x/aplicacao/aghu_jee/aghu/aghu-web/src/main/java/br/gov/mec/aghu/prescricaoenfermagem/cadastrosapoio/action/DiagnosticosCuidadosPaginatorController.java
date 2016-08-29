package br.gov.mec.aghu.prescricaoenfermagem.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.business.prescricaoenfermagem.IPrescricaoEnfermagemFacade;
import br.gov.mec.aghu.business.prescricaoenfermagem.cadastrosapoio.IPrescricaoEnfermagemApoioFacade;
import br.gov.mec.aghu.model.EpeDiagnostico;
import br.gov.mec.aghu.model.EpeFatRelacionado;
import br.gov.mec.aghu.model.EpeGrupoNecesBasica;
import br.gov.mec.aghu.model.EpeSubgrupoNecesBasica;
import br.gov.mec.aghu.model.EpeSubgrupoNecesBasicaId;
import br.gov.mec.aghu.prescricaoenfermagem.vo.DiagnosticoCuidadoVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

public class DiagnosticosCuidadosPaginatorController extends ActionController implements ActionPaginator {

	@Inject @Paginator
	private DynamicDataModel<DiagnosticoCuidadoVO> dataModel;

	private static final long serialVersionUID = 126974068299977703L;
	
	private static final String PAGE_DIAGNOSTICOS_CUIDADOS_CRUD = "diagnosticosCuidadosCRUD";

	@EJB
	private IPrescricaoEnfermagemFacade prescricaoEnfermagemFacade;

	@EJB
	private IPrescricaoEnfermagemApoioFacade prescricaoEnfermagemApoioFacade;

	private EpeGrupoNecesBasica grupo;
	private EpeSubgrupoNecesBasica subGrupo;
	private EpeDiagnostico diagnostico;
	private EpeFatRelacionado etiologia;
	private Boolean suggestionSubgrupo;
	private Boolean desativarBotaoPesquisar;
	private Boolean suggestionDiagnosticos;
	private Boolean exibirBotaoNovo;
	private List<EpeDiagnostico> listaDiagnosticos;
	private Short gnbSeq;
	private Short snbSequencia;
	private Boolean btCancelar;
	private DiagnosticoCuidadoVO diagnosticoCuidadoSelecionado;

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

	public String cancelar() {
		return "cancelar";
	}
	
	public String editar(){
		return PAGE_DIAGNOSTICOS_CUIDADOS_CRUD;
	}

	public void pesquisar() {
		dataModel.reiniciarPaginator();
		exibirBotaoNovo = Boolean.TRUE;
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

	public List<EpeFatRelacionado> pesquisarEtiologia(String filtro) {
		return this.returnSGWithCount(prescricaoEnfermagemApoioFacade.pesquisarEtiologiasTodas((String) filtro),pesquisarEtiologiaCount(filtro));
	}

	public Long pesquisarEtiologiaCount(String filtro) {
		return prescricaoEnfermagemApoioFacade.pesquisarEtiologiasCountTodas((String) filtro);
	}

	public void ativarSubgrupo() {
		suggestionSubgrupo = false;
	}

	public void desativarSubGrupo() {
		listaDiagnosticos = null;
		subGrupo = null;
		grupo = null;
		gnbSeq = null;
		snbSequencia = null;
		suggestionSubgrupo = Boolean.TRUE;
		diagnostico = null;
		suggestionDiagnosticos = Boolean.TRUE;
		desativarBotaoPesquisar = null;
		exibirBotaoNovo = Boolean.FALSE;
	}

	public void ativarDiagnosticos() {
		suggestionDiagnosticos = false;
	}

	public void desativarDiagnosticos() {
		listaDiagnosticos = null;
		diagnostico = null;
		suggestionDiagnosticos = Boolean.TRUE;
		exibirBotaoNovo = Boolean.FALSE;
	}

	public void limparDiagnosticos() {
		listaDiagnosticos = null;
		exibirBotaoNovo = Boolean.FALSE;
	}

	public void limparPesquisa() {		
		desativarSubGrupo();
		etiologia = null;
		dataModel.setPesquisaAtiva(Boolean.FALSE);
	}

	@Override
	public List<DiagnosticoCuidadoVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {
		Short snbGnbSeq, snbSequencia, dgnSequencia, freSeq;
		snbGnbSeq = (grupo != null) ? grupo.getSeq() : null;
		snbSequencia = (subGrupo != null) ? subGrupo.getId().getSequencia() : null;
		dgnSequencia = (diagnostico != null) ? diagnostico.getId().getSequencia() : null;
		freSeq = (etiologia != null) ? etiologia.getSeq() : null;
		return prescricaoEnfermagemFacade.pesquisarDiagnosticosLista(snbGnbSeq, snbSequencia, dgnSequencia, freSeq, firstResult,
				maxResult, orderProperty, asc);
	}

	@Override
	public Long recuperarCount() {
		Short snbGnbSeq, snbSequencia, dgnSequencia, freSeq;
		snbGnbSeq = (grupo != null) ? grupo.getSeq() : null;
		snbSequencia = (subGrupo != null) ? subGrupo.getId().getSequencia() : null;
		dgnSequencia = (diagnostico != null) ? diagnostico.getId().getSequencia() : null;
		freSeq = (etiologia != null) ? etiologia.getSeq() : null;
		return prescricaoEnfermagemFacade.pesquisarDiagnosticosListaCount(snbGnbSeq, snbSequencia, dgnSequencia, freSeq);
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

	public EpeFatRelacionado getEtiologia() {
		return etiologia;
	}

	public void setEtiologia(EpeFatRelacionado etiologia) {
		this.etiologia = etiologia;
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

	public DynamicDataModel<DiagnosticoCuidadoVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<DiagnosticoCuidadoVO> dataModel) {
		this.dataModel = dataModel;
	}

	public DiagnosticoCuidadoVO getDiagnosticoCuidadoSelecionado() {
		return diagnosticoCuidadoSelecionado;
	}

	public void setDiagnosticoCuidadoSelecionado(DiagnosticoCuidadoVO diagnosticoCuidadoSelecionado) {
		this.diagnosticoCuidadoSelecionado = diagnosticoCuidadoSelecionado;
	}
}
