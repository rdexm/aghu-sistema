package br.gov.mec.aghu.farmacia.cadastroapoio.action;

import java.util.List;

import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AfaSinonimoMedicamento;
import br.gov.mec.aghu.model.AfaSinonimoMedicamentoId;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

@SuppressWarnings("PMD.HierarquiaControllerIncorreta")
public class SinonimoMedicamentoController extends AbstractCrudMedicamentoController<AfaSinonimoMedicamento> {

	private static final long serialVersionUID = -1100801624191636789L;
	
	@Inject
	private SinonimoMedicamentoPaginatorController sinonimoMedicamentoPaginatorController;

	@Override
	protected void instanciarEntidade() {
		AfaSinonimoMedicamento sinonimoMedicamento = new AfaSinonimoMedicamento();
		AfaSinonimoMedicamentoId id = new AfaSinonimoMedicamentoId();
		id.setMedMatCodigo(getMedicamento().getCodigo());
		sinonimoMedicamento.setId(id);
		sinonimoMedicamento.setMedicamento(getMedicamento());
		sinonimoMedicamento.setIndSituacao(DominioSituacao.A);
		setEntidade(sinonimoMedicamento);
	}

	@Override
	protected String getPaginaInclusao() {
		return "sinonimoMedicamentoCRUD";
	}

	@Override
	protected String getPaginaConfirmado() {
		return "sinonimoMedicamentoList";
	}

	@Override
	protected String getPaginaCancelado() {
		return "sinonimoMedicamentoList";
	}

	@Override
	protected String getPaginaErro() {
		return null;
	}

	@Override
	protected List<String> obterRazoesExcecao() {
		return null;
	}

	@Override
	protected AfaSinonimoMedicamento obterEntidadeOriginalViaEntidade(
			AfaSinonimoMedicamento entidade) {
		return null;
	}

	@Override
	protected void efetuarInclusao(AfaSinonimoMedicamento entidade)
			throws IllegalStateException, ApplicationBusinessException,
			BaseException {
		getFarmaciaApoioFacade().inserirAtualizarSinonimoMedicamento(entidade);
	}

	@Override
	protected void efetuarAlteracao(AfaSinonimoMedicamento entidade)
			throws IllegalStateException, ApplicationBusinessException,
			BaseException {
		getFarmaciaApoioFacade().inserirAtualizarSinonimoMedicamento(entidade);
	}

	@Override
	protected void efetuarRemocao(AfaSinonimoMedicamento entidade)
			throws IllegalStateException, ApplicationBusinessException,
			BaseException {
		//getFarmaciaApoioFacade().removerSinonimoMedicamento(entidade);
	}

	@Override
	protected void informarInclusaoSucesso(
			AfaSinonimoMedicamento entidade) {
		this.apresentarMsgNegocio(Severity.INFO,
				"MENSAGEM_SUCESSO_CRIACAO_SINONIMO",
				entidade.getDescricao());
	}

	@Override
	protected void informarAlteracaoSucesso(
			AfaSinonimoMedicamento entidade) {
		this.apresentarMsgNegocio(Severity.INFO,
				"MENSAGEM_SUCESSO_EDICAO_SINONIMO",
				entidade.getDescricao());
	}

	@Override
	protected void informarExclusaoSucesso(
			AfaSinonimoMedicamento entidade) {
		this.apresentarMsgNegocio(Severity.INFO,
				"MENSAGEM_SUCESSO_REMOCAO_SINONIMO",
				entidade.getDescricao());
	}

	@Override
	protected void informarExclusaoErro(AfaSinonimoMedicamento entidade) {
		this.apresentarMsgNegocio(Severity.INFO,
				"MENSAGEM_ERRO_REMOCAO_SINONIMO_INVALIDA");
	}

	@Override
	protected void reiniciarPaginatorController() {
		sinonimoMedicamentoPaginatorController.getDataModel().reiniciarPaginator();
	}

}