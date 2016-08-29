package br.gov.mec.aghu.bancosangue.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.business.bancosangue.IBancoDeSangueFacade;
import br.gov.mec.aghu.model.AbsGrupoJustificativaComponenteSanguineo;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;


public class GrupoJustificativaHemoterapiaPaginatorController extends ActionController implements ActionPaginator{

	private static final long serialVersionUID = -348412252985875085L;

	private static final String MANTER_GRUPO_JUSTIFICATIVA_HEMOTERAPIA = "bancodesangue-manterGrupoJustificativaHemoterapia";
	
	private AbsGrupoJustificativaComponenteSanguineo filtros;
	
	@EJB
	private IBancoDeSangueFacade bancoDeSangueFacade;
	
	
	@Inject @Paginator
	private DynamicDataModel<AbsGrupoJustificativaComponenteSanguineo> dataModel;
	
	private AbsGrupoJustificativaComponenteSanguineo selecionado;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		filtros =new AbsGrupoJustificativaComponenteSanguineo();
	}

	
	@Override
	public Long recuperarCount() {
		return bancoDeSangueFacade.pesquisarAbsGrupoJustificativaComponenteSanguineoCount(filtros.getSeq(), filtros.getDescricao(), filtros.getSituacao(), filtros.getTitulo());
	}

	@Override
	public List<AbsGrupoJustificativaComponenteSanguineo> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return bancoDeSangueFacade.pesquisarAbsGrupoJustificativaComponenteSanguineo(firstResult, maxResult, orderProperty, asc,
																			filtros.getSeq(), filtros.getDescricao(), filtros.getSituacao(), filtros.getTitulo());
	}
	
	public void limpar() {
		dataModel.limparPesquisa();
		filtros = new AbsGrupoJustificativaComponenteSanguineo();
	}

	public String inserir(){
		return MANTER_GRUPO_JUSTIFICATIVA_HEMOTERAPIA;
	}
	
	public String editar(){
		return MANTER_GRUPO_JUSTIFICATIVA_HEMOTERAPIA;
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}

	
//	public void inicio() {
//		if (!ativo) {
//			inicializar();
//		}
//		if (reinicia) {
//			reinicia = false;
//			pesquisar();
//		}
//	}
	
	public DynamicDataModel<AbsGrupoJustificativaComponenteSanguineo> getDataModel() {
		return dataModel;
	}

	public void setDataModel(
			DynamicDataModel<AbsGrupoJustificativaComponenteSanguineo> dataModel) {
		this.dataModel = dataModel;
	}

	public AbsGrupoJustificativaComponenteSanguineo getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(
			AbsGrupoJustificativaComponenteSanguineo selecionado) {
		this.selecionado = selecionado;
	}


	public AbsGrupoJustificativaComponenteSanguineo getFiltros() {
		return filtros;
	}


	public void setFiltros(AbsGrupoJustificativaComponenteSanguineo filtros) {
		this.filtros = filtros;
	}
}