package br.gov.mec.aghu.farmacia.cadastroapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MpmUnidadeTempo;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class PesquisarUnidadeTempoPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -4014911948472790077L;
	
	private final String REDIRECIONA_MANTER_UNIDADE_TEMPO = "manterUnidadeTempo";
	
	@Inject
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@Inject @Paginator
	private DynamicDataModel<MpmUnidadeTempo> dataModel;
	
	private MpmUnidadeTempo mpmUnidadeTempoPesquisa;
	private MpmUnidadeTempo mpmUnidadeTempoSelecionado;
	
	@Inject
	private ManterUnidadeTempoController manterUnidadeTempoController;
	
	@PostConstruct
	public void init(){
		begin(conversation);
		mpmUnidadeTempoPesquisa = new MpmUnidadeTempo();
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}
	
	public void limparPesquisa() {
		mpmUnidadeTempoPesquisa = new MpmUnidadeTempo();
		this.dataModel.limparPesquisa();
	}
	
	public String inserirNovo(){
		return "manterUnidadeTempo";
	}
	
	public void excluir(){
		try {
			this.prescricaoMedicaFacade.excluirUnidadeTempo(mpmUnidadeTempoSelecionado.getSeq());
			this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_EXCLUIR_UNIDADE_TEMPO");
			dataModel.reiniciarPaginator();
		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
		}
		
	}
	
	public String editar(){
		manterUnidadeTempoController.setUnidadeTempo(mpmUnidadeTempoSelecionado);
		return REDIRECIONA_MANTER_UNIDADE_TEMPO;
	}
	public String novo() {
		MpmUnidadeTempo mut = new MpmUnidadeTempo();
		mut.setIndSituacao(DominioSituacao.A);
		manterUnidadeTempoController.setUnidadeTempo(mut);
		return REDIRECIONA_MANTER_UNIDADE_TEMPO;
	}
	
	@Override
	public Long recuperarCount() {
		return prescricaoMedicaFacade.pesquisarUnidadeTempoCount(this.mpmUnidadeTempoPesquisa);
	}

	@Override
	public List<MpmUnidadeTempo> recuperarListaPaginada(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {
		return this.prescricaoMedicaFacade.listaUnidadeTempo(firstResult, maxResult, "seq", true, this.mpmUnidadeTempoPesquisa);
	}

	public DynamicDataModel<MpmUnidadeTempo> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<MpmUnidadeTempo> dataModel) {
		this.dataModel = dataModel;
	}

	public MpmUnidadeTempo getMpmUnidadeTempoPesquisa() {
		return mpmUnidadeTempoPesquisa;
	}

	public void setMpmUnidadeTempoPesquisa(MpmUnidadeTempo mpmUnidadeTempoPesquisa) {
		this.mpmUnidadeTempoPesquisa = mpmUnidadeTempoPesquisa;
	}

	public MpmUnidadeTempo getMpmUnidadeTempoSelecionado() {
		return mpmUnidadeTempoSelecionado;
	}

	public void setMpmUnidadeTempoSelecionado(
			MpmUnidadeTempo mpmUnidadeTempoSelecionado) {
		this.mpmUnidadeTempoSelecionado = mpmUnidadeTempoSelecionado;
	}
}
