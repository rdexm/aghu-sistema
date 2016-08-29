package br.gov.mec.aghu.prescricaoenfermagem.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.business.prescricaoenfermagem.cadastrosapoio.IPrescricaoEnfermagemApoioFacade;
import br.gov.mec.aghu.dominio.DominioSituacaoMedicamento;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

/**
 * #4961 - Manter medicamentos x cuidados
 * 
 * @author jback
 * 
 */

public class MedicamentosCuidadosPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -3224710692872502904L;
	
	private static final String PAGE_MEDICAMENTOS_CUIDADOS_CRUD = "medicamentosCuidadosCRUD";

	@EJB
	private IPrescricaoEnfermagemApoioFacade prescricaoEnfermagemApoioFacade;

	@Inject @Paginator
	private DynamicDataModel<AfaMedicamento> dataModel;

	private DominioSituacaoMedicamento indSituacao = null;

	private Boolean desativarBotaoPesquisar;
	private AfaMedicamento medicamento;
	private AfaMedicamento medicamentoSelecionado;	

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public List<AfaMedicamento> pesquisarMedicamentos(String filtro) {
		return this.returnSGWithCount(prescricaoEnfermagemApoioFacade.pesquisarMedicamentosParaMedicamentosCuidados((String) filtro),pesquisarMedicamentosCount(filtro));
	}

	public Long pesquisarMedicamentosCount(String filtro) {
		return prescricaoEnfermagemApoioFacade.pesquisarMedicamentosParaMedicamentosCuidadosCount((String) filtro);
	}

	public void pesquisar() {
		dataModel.reiniciarPaginator();
		dataModel.setPesquisaAtiva(Boolean.TRUE);
	}
	
	public String editar(){
		return PAGE_MEDICAMENTOS_CUIDADOS_CRUD;
	}

	public void limparPesquisa() {
		medicamento = null;
		indSituacao = null;
		dataModel.setPesquisaAtiva(Boolean.FALSE);
	}

	@Override
	public List<AfaMedicamento> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		Integer matCodigo = (medicamento != null) ? medicamento.getMatCodigo() : null;
		return prescricaoEnfermagemApoioFacade.pesquisarMedicamentosParaListagemMedicamentosCuidados(matCodigo, indSituacao,
				firstResult, maxResult, orderProperty, asc);
	}

	@Override
	public Long recuperarCount() {
		Integer matCodigo = (medicamento != null) ? medicamento.getMatCodigo() : null;
		return prescricaoEnfermagemApoioFacade.pesquisarMedicamentosParaListagemMedicamentosCuidadosCount(matCodigo, indSituacao);
	}

	public Boolean getDesativarBotaoPesquisar() {
		return desativarBotaoPesquisar;
	}

	public void setDesativarBotaoPesquisar(Boolean desativarBotaoPesquisar) {
		this.desativarBotaoPesquisar = desativarBotaoPesquisar;
	}

	public DominioSituacaoMedicamento getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacaoMedicamento indSituacao) {
		this.indSituacao = indSituacao;
	}

	public AfaMedicamento getMedicamento() {
		return medicamento;
	}

	public void setMedicamento(AfaMedicamento medicamento) {
		this.medicamento = medicamento;
	}

	public DynamicDataModel<AfaMedicamento> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AfaMedicamento> dataModel) {
		this.dataModel = dataModel;
	}

	public AfaMedicamento getMedicamentoSelecionado() {
		return medicamentoSelecionado;
	}

	public void setMedicamentoSelecionado(AfaMedicamento medicamentoSelecionado) {
		this.medicamentoSelecionado = medicamentoSelecionado;
	}
}
