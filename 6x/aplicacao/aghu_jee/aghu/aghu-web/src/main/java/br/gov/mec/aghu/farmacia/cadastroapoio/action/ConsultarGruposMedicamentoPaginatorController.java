package br.gov.mec.aghu.farmacia.cadastroapoio.action;


import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade;
import br.gov.mec.aghu.model.AfaItemGrupoMedicamento;


@SuppressWarnings({"PMD.HierarquiaControllerIncorreta"})
public class ConsultarGruposMedicamentoPaginatorController extends AbstractCrudMedicamentoPaginatorController<AfaItemGrupoMedicamento> {
	
	private static final long serialVersionUID = -7752035352193544499L;
	
	private static final String PAGE_CONSULTA_MEDICAMENTO_GRUPOS= "consultaMedicamentoGrupos";
	
	@Inject
	private IFarmaciaApoioFacade farmaciaApoioFacade;
	
	@PostConstruct
	public void init(){
		begin(conversation);
	}
	
	@Override
	public void iniciarPagina() {
	 

		super.iniciarPagina();
		super.pesquisar(); 
	
	}
	
	@Override
	public Long recuperarCount() {
		return farmaciaApoioFacade.consultarGruposMedicamentoCount(this.getMedicamento());
	}

	@Override
	public List<AfaItemGrupoMedicamento> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		
		return farmaciaApoioFacade.consultarGruposMedicamento(firstResult, maxResult, 
				AfaItemGrupoMedicamento.Fields.GRUPO_MEDICAMENTO_DESCRICAO.toString(), true, this.getMedicamento());
	}
	
	public String voltar(){
		return PAGE_CONSULTA_MEDICAMENTO_GRUPOS;
	}

	@Override
	public AbstractCrudMedicamentoController getCrudController() {
		return null;
	}

	@Override
	public String paginaCrud() {
		return null;
	}

	@Override
	protected String paginaHist() {
		return null;
	}
}