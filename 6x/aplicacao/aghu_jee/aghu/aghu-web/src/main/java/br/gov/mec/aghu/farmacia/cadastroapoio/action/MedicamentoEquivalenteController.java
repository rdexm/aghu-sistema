package br.gov.mec.aghu.farmacia.cadastroapoio.action;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaMedicamentoEquivalente;
import br.gov.mec.aghu.model.AfaMedicamentoEquivalenteId;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responsável por controlar as ações do criação e edição de
 * medicamentos equivalentes de medicamentos.
 * 
 */
@SuppressWarnings("PMD.HierarquiaControllerIncorreta")
public class MedicamentoEquivalenteController extends AbstractCrudMedicamentoController<AfaMedicamentoEquivalente> {
	
	private static final long serialVersionUID = -1663621972708992475L;
	
	@Inject
	private MedicamentoEquivalentePaginatorController medicamentoEquivalentePaginatorController;
	
	public List<AfaMedicamento> pesquisarMedicamentos(String strPesquisa){
		return this.returnSGWithCount(getFarmaciaApoioFacade().pesquisarTodosMedicamentos(strPesquisa),pesquisarMedicamentosCount(strPesquisa));
	}
	
	public Long pesquisarMedicamentosCount(String strObject) {
		return getFarmaciaApoioFacade().pesquisarTodosMedicamentosCount(strObject);
	}

	@Override
	protected void instanciarEntidade() {
		AfaMedicamentoEquivalente ent = new AfaMedicamentoEquivalente();
		ent.setId(new AfaMedicamentoEquivalenteId());
		ent.getId().setMedMatCodigo(getMedicamento().getMatCodigo());
		setEntidade(ent);
	}

	@Override
	protected String getPaginaInclusao() {
		return "medicamentoEquivalenteCrud";
	}

	@Override
	protected String getPaginaConfirmado() {
		return "medicamentoEquivalenteList";
	}

	@Override
	protected String getPaginaCancelado() {
		return "medicamentoEquivalenteList";
	}

	@Override
	protected String getPaginaErro() {
		return null;
	}

	@Override
	protected List<String> obterRazoesExcecao() {
		return getFarmaciaApoioFacade().obterRazoesExcessaoMedicamentoEquivalente();
	}

	@Override
	protected AfaMedicamentoEquivalente obterEntidadeOriginalViaEntidade(
			AfaMedicamentoEquivalente entidade) {
		return null;
	}

	@Override
	protected void efetuarInclusao(AfaMedicamentoEquivalente entidade)
			throws IllegalStateException, ApplicationBusinessException,
			BaseException {
		getEntidade().getId().setMedMatCodigoEquivalente(getEntidade().getMedicamentoEquivalente().getCodigo());
		String nomeMicrocomputador = getNomeMicroComputador();
		getFarmaciaApoioFacade().efetuarInclusao(entidade, nomeMicrocomputador, new Date());
	}

	@Override
	protected void efetuarAlteracao(AfaMedicamentoEquivalente entidade)
			throws IllegalStateException, ApplicationBusinessException,
			BaseException {
		String nomeMicrocomputador = getNomeMicroComputador();
		getFarmaciaApoioFacade().efetuarAlteracao(entidade, nomeMicrocomputador, new Date());
	}

	@Override
	protected void efetuarRemocao(AfaMedicamentoEquivalente entidade)
			throws IllegalStateException, ApplicationBusinessException,
			BaseException {
		//Medicamento Equivalente não possui remoção
	}

	@Override
	protected void informarInclusaoSucesso(AfaMedicamentoEquivalente entidade) {
		this.apresentarMsgNegocio(Severity.INFO,
				"MENSAGEM_SUCESSO_CRIACAO_MDTO_EQUIVALENTE",
				entidade.getMedicamentoEquivalente().getDescricao());
	}

	@Override
	protected void informarAlteracaoSucesso(AfaMedicamentoEquivalente entidade) {
		this.apresentarMsgNegocio(Severity.INFO,
				"MENSAGEM_SUCESSO_ALTERACAO_MDTO_EQUIVALENTE",
				entidade.getMedicamentoEquivalente().getDescricao());
	}

	@Override
	protected void informarExclusaoSucesso(AfaMedicamentoEquivalente entidade) {
		//Medicamento Equivalente não possui remoção
	}

	@Override
	protected void informarExclusaoErro(AfaMedicamentoEquivalente entidade) {
		//Medicamento Equivalente não possui remoção
	}

	@Override
	protected void reiniciarPaginatorController() {
		medicamentoEquivalentePaginatorController.getDataModel().reiniciarPaginator();
	}
}