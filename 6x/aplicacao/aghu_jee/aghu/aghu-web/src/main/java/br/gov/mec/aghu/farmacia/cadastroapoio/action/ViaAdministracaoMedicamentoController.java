package br.gov.mec.aghu.farmacia.cadastroapoio.action;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AfaViaAdministracao;
import br.gov.mec.aghu.model.AfaViaAdministracaoMedicamento;
import br.gov.mec.aghu.model.AfaViaAdministracaoMedicamentoId;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responsável por controlar as ações do criação e edição de
 * vias de administracao de medicamentos.
 * 
 */
@SuppressWarnings("PMD.HierarquiaControllerIncorreta")
public class ViaAdministracaoMedicamentoController extends AbstractCrudMedicamentoController<AfaViaAdministracaoMedicamento> {

	private static final long serialVersionUID = 3069252031265589813L;
	
	@Inject
	private ViaAdministracaoMedicamentoPaginatorController viaAdministracaoMedicamentoPaginatorController;
	
	@Override
	protected boolean efetuarInclusao() {
		return !getEdicao();
	}
	
	@Override
	protected void instanciarEntidade() {
		AfaViaAdministracaoMedicamento result = null;
		AfaViaAdministracaoMedicamentoId id = null;
		
		id = new AfaViaAdministracaoMedicamentoId();		
		result = new AfaViaAdministracaoMedicamento();
		id.setMedMatCodigo(getMedicamento().getMatCodigo());
		id.setVadSigla(null);
		result.setMedicamento(getMedicamento());
		result.setId(id);
		result.setSituacao(DominioSituacao.A);
		result.setPermiteBi(Boolean.FALSE);
		result.setDefaultBi(Boolean.FALSE);			
		
		setEntidade(result);
	}
	
	public List<AfaViaAdministracao> pesquisarViasAdministracao(String strPesquisa) {
		return this.getFarmaciaFacade().getListaViasAdmNaoUtilizadas((String) strPesquisa, getEntidade().getMedicamento().getViasAdministracaoMedicamento());
	}
	
	@Override
	protected void reiniciarPaginatorController() {
		viaAdministracaoMedicamentoPaginatorController.getDataModel().reiniciarPaginator();
	}
	
	@Override
	protected void informarAlteracaoSucesso(AfaViaAdministracaoMedicamento entidade) {
		this.apresentarMsgNegocio(Severity.INFO,
				"MENSAGEM_SUCESSO_ALTERACAO_VIA_MDTO",
				entidade.getViaAdministracao().getDescricao());
	}

	@Override
	protected void informarInclusaoSucesso(AfaViaAdministracaoMedicamento entidade) {
		this.apresentarMsgNegocio(Severity.INFO,
				"MENSAGEM_SUCESSO_CRIACAO_VIA_MDTO",
				entidade.getViaAdministracao().getDescricao());
	}
	
	@Override
	protected void informarExclusaoErro(AfaViaAdministracaoMedicamento entidade) {
		 //via de administração não tem opção de exclusão 
	}

	@Override
	protected void informarExclusaoSucesso(AfaViaAdministracaoMedicamento entidade) {
		// via de administração não tem opção de exclusão 
	}

	@Override
	protected List<String> obterRazoesExcecao() {
		return this.getFarmaciaApoioFacade().obterRazoesExcessaoViaAdministracaoMedicamento();
	}

	@Override
	protected AfaViaAdministracaoMedicamento obterEntidadeOriginalViaEntidade(
			AfaViaAdministracaoMedicamento entidade) {

		return this.getFarmaciaFacade().buscarAfaViaAdministracaoMedimanetoPorChavePrimaria(entidade.getId());
	}

	@Override
	protected void efetuarInclusao(AfaViaAdministracaoMedicamento entidade)
			throws IllegalStateException, BaseException {
		String nomeMicrocomputador = getNomeMicroComputador();
		this.getFarmaciaApoioFacade().efetuarInclusao(entidade, nomeMicrocomputador, new Date());
	}

	@Override
	protected void efetuarAlteracao(AfaViaAdministracaoMedicamento entidade)
			throws IllegalStateException, BaseException {
		String nomeMicrocomputador = getNomeMicroComputador();
		this.getFarmaciaApoioFacade().efetuarAlteracao(entidade, nomeMicrocomputador, new Date());
	}

	@Override
	protected void efetuarRemocao(AfaViaAdministracaoMedicamento entidade)
			throws IllegalStateException, BaseException{
		String nomeMicrocomputador = getNomeMicroComputador();
		this.getFarmaciaApoioFacade().efetuarRemocao(entidade, nomeMicrocomputador, new Date());
	}
	
	public String historico(){
		return RetornoAcaoStrEnum.VISUALIZAR_HISTORICO.toString();		
	}
	
	public static enum RetornoAcaoStrEnum {

		VISUALIZAR_HISTORICO("visualizarHistorico");

		private final String str;

		RetornoAcaoStrEnum(String str) {

			this.str = str;
		}

		@Override
		public String toString() {

			return this.str;
		}
	}
	
	@Override
	public String getPaginaInclusao() {
		return "viaAdministracaoMedicamentoCRUD";
	}

	@Override
	public String getPaginaConfirmado() {
		return "viaAdministracaoMedicamentoList";
	}

	@Override
	public String getPaginaCancelado() {
		return "viaAdministracaoMedicamentoList";
	}

	@Override
	public String getPaginaErro() {
		return null;
	}
	
}