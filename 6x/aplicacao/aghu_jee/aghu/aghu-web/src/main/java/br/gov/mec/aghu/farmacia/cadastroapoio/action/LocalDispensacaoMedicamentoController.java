package br.gov.mec.aghu.farmacia.cadastroapoio.action;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import br.gov.mec.aghu.model.AfaLocalDispensacaoMdtos;
import br.gov.mec.aghu.model.AfaLocalDispensacaoMdtosId;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responsável por controlar as ações do criação e edição de
 * local de dispensacao de medicamentos.
 * 
 */
@SuppressWarnings("PMD.HierarquiaControllerIncorreta")
public class LocalDispensacaoMedicamentoController extends AbstractCrudMedicamentoController<AfaLocalDispensacaoMdtos> {
	
	private static final long serialVersionUID = -5581082623859153249L;
	
	@Inject
	private LocalDispensacaoMedicamentoPaginatorController localDispensacaoMedicamentoPaginatorController;

	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisPorCaracteristicas(String strPesquisa) {
		return this.returnSGWithCount(getFarmaciaFacade().pesquisarUnidadesFuncionaisPorCodigoDescricaoAlaAndarECaracteristicas(strPesquisa),pesquisarUnidadesFuncionaisPorCaracteristicasCount(strPesquisa));
	}
	
	public Long pesquisarUnidadesFuncionaisPorCaracteristicasCount(String strParam) {
		return getFarmaciaFacade().pesquisarUnidadesFuncionaisPorCodigoDescricaoAlaAndarECaracteristicasCount(strParam);
	}

	public List<AghUnidadesFuncionais> listarFarmaciasAtivasByPesquisa(String strPesquisa) {
		return this.returnSGWithCount(getFarmaciaFacade().listarFarmaciasAtivasByPesquisa(strPesquisa),listarFarmaciasAtivasByPesquisaCount(strPesquisa));
	}
	
	public Long listarFarmaciasAtivasByPesquisaCount(String strPesquisa) {
		return getFarmaciaFacade().listarFarmaciasAtivasByPesquisaCount(strPesquisa);
	}
	
	public void selecionarTodasUnidadesFuncionaisParaMedicamento(){
		try{
			getFarmaciaApoioFacade().persistirLocalDispensacaoMedicamentoComUnfsDisponiveis(getMedicamento());
			this.apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_SUCESSO_SEL_TODAS_UNFS_LOCAL_DISPENSACAO");
			reiniciarPaginatorController();
		}catch(ApplicationBusinessException e){
			apresentarExcecaoNegocio(e);
		}
	}
	@Override
	protected void instanciarEntidade() {
		AfaLocalDispensacaoMdtos localDispensacao = new AfaLocalDispensacaoMdtos();
		AfaLocalDispensacaoMdtosId localDispensacaoId = new AfaLocalDispensacaoMdtosId();
		localDispensacaoId.setMedMatCodigo(getMedicamento().getCodigo());
		localDispensacao.setId(localDispensacaoId);
		setEntidade(localDispensacao);
	}

	@Override
	protected String getPaginaInclusao() {
		return "localDispensacaoMedicamentoCRUD";
	}

	@Override
	protected String getPaginaConfirmado() {
		return "localDispensacaoMedicamentoList";
	}

	@Override
	protected String getPaginaCancelado() {
		return "localDispensacaoMedicamentoList";
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
	protected AfaLocalDispensacaoMdtos obterEntidadeOriginalViaEntidade(
			AfaLocalDispensacaoMdtos entidade) {
		 return getFarmaciaApoioFacade().obterLocalDispensacaoOriginal(entidade);
	}

	@Override
	protected void efetuarInclusao(AfaLocalDispensacaoMdtos entidade)
			throws IllegalStateException, ApplicationBusinessException,
			BaseException {
		getEntidade().getId().setUnfSeq(getEntidade().getUnidadeFuncional().getSeq());
		String nomeMicrocomputador = getNomeMicroComputador();
		getFarmaciaApoioFacade().persistirLocalDispensacaoMedicamento(getEntidade(), nomeMicrocomputador, new Date());
	}

	@Override
	protected void efetuarAlteracao(AfaLocalDispensacaoMdtos entidade)
			throws IllegalStateException, ApplicationBusinessException,
			BaseException {
		String nomeMicrocomputador = getNomeMicroComputador();
		getFarmaciaApoioFacade().atualizarLocalDispensacaoMedicamento(getEntidade(), nomeMicrocomputador, new Date());
	}

	@Override
	protected void efetuarRemocao(AfaLocalDispensacaoMdtos entidade)
			throws IllegalStateException, ApplicationBusinessException,
			BaseException {
		String nomeMicrocomputador = getNomeMicroComputador();
		getFarmaciaApoioFacade().removerLocalDispensacaoMedicamento(getEntidade(), nomeMicrocomputador, new Date());
	}

	@Override
	protected void informarInclusaoSucesso(AfaLocalDispensacaoMdtos entidade) {
		this.apresentarMsgNegocio(Severity.INFO,
				"MENSAGEM_SUCESSO_CRIACAO_LOCAL_DISPENSACAO",
				getMedicamento().getDescricao());
		
	}

	@Override
	protected void informarAlteracaoSucesso(AfaLocalDispensacaoMdtos entidade) {
		this.apresentarMsgNegocio(Severity.INFO,
				"MENSAGEM_SUCESSO_EDICAO_LOCAL_DISPENSACAO",
				getMedicamento().getDescricao());
	}

	@Override
	protected void informarExclusaoSucesso(AfaLocalDispensacaoMdtos entidade) {
		this.apresentarMsgNegocio(Severity.INFO,
				"MENSAGEM_SUCESSO_REMOCAO_LOCAL_DISPENSACAO",
				getEntidade().getUnidadeFuncional().getDescricao());
	}

	@Override
	protected void informarExclusaoErro(AfaLocalDispensacaoMdtos entidade) {
		this.apresentarMsgNegocio(Severity.ERROR,
				"MENSAGEM_ERRO_REMOCAO_LOCAL_DISPENSACAO_INVALIDO");
	}

	@Override
	protected void reiniciarPaginatorController() {
		localDispensacaoMedicamentoPaginatorController.getDataModel().reiniciarPaginator();
	}
}