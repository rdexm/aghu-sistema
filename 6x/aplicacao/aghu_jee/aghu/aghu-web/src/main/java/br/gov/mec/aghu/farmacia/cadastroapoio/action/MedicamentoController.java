package br.gov.mec.aghu.farmacia.cadastroapoio.action;

import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaTipoApresentacaoMedicamento;
import br.gov.mec.aghu.model.AfaTipoUsoMdto;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.model.MpmUnidadeMedidaMedica;
import br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.business.ICadastrosBasicosPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.modelobasico.business.IModeloBasicoFacade;

@SuppressWarnings("PMD.HierarquiaControllerIncorreta")
public class MedicamentoController extends AbstractCrudController<AfaMedicamento> {

	private static final long serialVersionUID = 101062103344047178L;

	private static final Log LOG = LogFactory.getLog(MedicamentoController.class);
	
	private static final String PAGE_PRESCRICAOMEDICA_MANTER_PRESCRICAO_SOLUCAO = "prescricaomedica-manterPrescricaoSolucao";
	
	private String origem;
	
	private AfaMedicamento medicamentoSelecionado;
	
	@Inject
	private ICadastrosBasicosPrescricaoMedicaFacade cadastrosBasicosPrescricaoMedicaFacade;
	@Inject
	private IFarmaciaApoioFacade farmaciaApoioFacade;
	@Inject
	private IModeloBasicoFacade modeloBasicoFacade;
	@Inject
	private IFarmaciaFacade farmaciaFacade;
	@Inject
	private MedicamentoPaginatorController medicamentoPaginatorController;
	
	public enum MsgLogStrEnum {

		SUCESSO_INCLUSAO("MENSAGEM_SUCESSO_CRIACAO_MEDICAMENTO"), 
		SUCESSO_ALTERACAO("MENSAGEM_SUCESSO_EDICAO_MEDICAMENTO"),
		SUCESSO_REMOCAO("MENSAGEM_SUCESSO_REMOCAO_MEDICAMENTO"),
		ERRO_REMOCAO("MENSAGEM_ERRO_REMOCAO_MEDICAMENTO");

		private final String str;

		MsgLogStrEnum(String str) {

			this.str = str;
		}

		@Override
		public String toString() {

			return this.str;
		}
	}
	
	@Override
	public void init() {
		begin(conversation);
	}
	
	public void iniciar(){
	 

		if(PAGE_PRESCRICAOMEDICA_MANTER_PRESCRICAO_SOLUCAO.equals(this.origem)){
			// Modo edição através da prescrição
			setEntidade(this.farmaciaApoioFacade.obterMedicamentoEdicao(getEntidade().getMatCodigo()));
		}
	
	}

	public List<MpmUnidadeMedidaMedica> pesquisarUnidadesMedidaMedicaConcentracao(
			String siglaOuDescricao) {
		return cadastrosBasicosPrescricaoMedicaFacade
				.pesquisarUnidadesMedidaMedicaConcentracao(siglaOuDescricao);
	}

	public List<AfaTipoApresentacaoMedicamento> pesquisaTipoApresentacaoMedicamentosAtivos(
			String siglaOuDescricao) {
		return farmaciaApoioFacade
				.pesquisaTipoApresentacaoMedicamentosAtivos(siglaOuDescricao);
	}

	public List<AfaTipoUsoMdto> pesquisaTipoUsoMdtoAtivos(
			String siglaOuDescricao) {
		return farmaciaApoioFacade.pesquisaTipoUsoMdtoAtivos(siglaOuDescricao);
	}

	public List<MpmTipoFrequenciaAprazamento> pesquisaTiposFrequenciaAprazamentoAtivos(
			String strPesquisa) {
		return this.modeloBasicoFacade
				.obterListaTipoFrequenciaAprazamento((String) strPesquisa);
	}
	
	@Override
	protected String getPaginaCancelado() {
		return "medicamentoList";
	}
	
	@Override
	protected String getPaginaConfirmado() {
		String retorno = "medicamentoList";
		
		if (origem != null) {
			retorno = origem;
		}
	
		return retorno;
	}
	
	private void possuiVinculoDiluente() throws ApplicationBusinessException {
		AfaMedicamento medicamento = farmaciaFacade.obterMedicamento(getEntidade().getMatCodigo());
		farmaciaFacade.verificarVinculoDiluenteExistente(medicamento, getEntidade().getIndDiluente(), getEntidade().getIndSituacao());
	}
	
	@Override
	protected boolean efetuarInclusao() {
		//Esta página possui SOMENTE ALTERAÇÂO
		return Boolean.FALSE;
	}
	
	@Override
	protected void efetuarInclusao(AfaMedicamento entidade)
			throws IllegalStateException, BaseException {
		String nomeMicrocomputador = getNomeMicroComputador();
		possuiVinculoDiluente();
		this.farmaciaApoioFacade.efetuarInclusao(entidade, nomeMicrocomputador, new Date());
	}

	@Override
	protected void informarInclusaoSucesso(AfaMedicamento entidade) {
		this.apresentarMsgNegocio(Severity.INFO,
				MsgLogStrEnum.SUCESSO_INCLUSAO.toString(),
				entidade.getDescricao());
	}
	
	private String getNomeMicroComputador() {
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoIPv4HostRemoto().getHostName();
		} catch (UnknownHostException e) {
			LOG.error(e.getMessage(), e);
		}
		return nomeMicrocomputador;
	}
	
	
	@Override
	protected void efetuarAlteracao(AfaMedicamento entidade)
			throws IllegalStateException, BaseException {
		String nomeMicrocomputador = getNomeMicroComputador();
		possuiVinculoDiluente();
		this.farmaciaApoioFacade.efetuarAlteracao(entidade,	nomeMicrocomputador, new Date());
	}
	
	@Override
	protected void informarAlteracaoSucesso(AfaMedicamento entidade) {
		this.apresentarMsgNegocio(Severity.INFO,
				MsgLogStrEnum.SUCESSO_ALTERACAO.toString(),
				entidade.getDescricao());
	}
	
	@Override
	protected void informarExclusaoSucesso(AfaMedicamento entidade) {
		this.apresentarMsgNegocio(Severity.INFO,
				MsgLogStrEnum.SUCESSO_REMOCAO.toString(),
				entidade.getDescricao());
	}

	@Override
	protected void informarExclusaoErro(AfaMedicamento entidade) {
		this.apresentarMsgNegocio(Severity.INFO,
				MsgLogStrEnum.ERRO_REMOCAO.toString(),
				entidade.getDescricao());
	}

	@Override
	protected void reiniciarPaginatorController() {
		medicamentoPaginatorController.getDataModel().reiniciarPaginator();
	}
	
	@Override
	protected void prepararCancelamento() {
		//this.setMatCodigo(null);
	}

	@Override
	protected void procederPreExclusao() {
		//this.iniciarPagina();
	}

	@Override
	protected void procederPosExclusao() {
	//	this.setMatCodigo(null);
	}

	@Override
	protected void prepararInclusao() {
//		this.setMatCodigo(null);
	}

	@Override
	protected List<String> obterRazoesExcecao() {
		return this.farmaciaApoioFacade.obterRazoesExcessaoMedicamento();
	}

	@Override
	protected AfaMedicamento obterEntidadeOriginalViaEntidade(
			AfaMedicamento entidade) {
		return this.farmaciaFacade.obterMedicamentoOriginal(entidade.getMatCodigo());
	}

	@Override
	protected void efetuarRemocao(AfaMedicamento entidade)
			throws IllegalStateException, BaseException {
		String nomeMicrocomputador = getNomeMicroComputador();
		this.farmaciaApoioFacade.efetuarRemocao(entidade, nomeMicrocomputador, new Date());
	}

	@Override
	protected String getPaginaInclusao() {
		return "medicamentoCRUD";
	}
	
	@Override
	protected String getPaginaErro() {
		return null;
	}
	
	//** Métodos que direcionam para outras páginas *//*
	@Inject
	private ViaAdministracaoMedicamentoPaginatorController viaAdministracaoMedicamentoPaginatorController;
	@Inject
	private FormaDosagemPaginatorController formaDosagemPaginatorController;
	@Inject
	private LocalDispensacaoMedicamentoPaginatorController localDispensacaoMedicamentoPaginatorController;
	@Inject
	private SinonimoMedicamentoPaginatorController sinonimoMedicamentoPaginatorController;
	@Inject
	private MedicamentoEquivalentePaginatorController medicamentoEquivalentePaginatorController;
	@Inject
	private HistoricoCadastroMedicamentoPaginatorController historicoCadastroMedicamentoPaginatorController;
	
	public String viaAdministracaoMedicamento(){
		viaAdministracaoMedicamentoPaginatorController.setMedicamento(getEntidade());
		viaAdministracaoMedicamentoPaginatorController.setFromList(Boolean.FALSE);
		return "viaAdministracaoMedicamentoList";
	}
	public String formaDosagem(){
		formaDosagemPaginatorController.setMedicamento(getEntidade());
		formaDosagemPaginatorController.setFromList(Boolean.FALSE);
		return "formaDosagemList";
	}
	public String localDispensacaoMedicamento(){
		localDispensacaoMedicamentoPaginatorController.setMedicamento(getEntidade());
		localDispensacaoMedicamentoPaginatorController.setFromList(Boolean.FALSE);
		return "localDispensacaoMedicamentoList";
	}
	public String sinonimoMedicamento(){
		sinonimoMedicamentoPaginatorController.setMedicamento(getEntidade());
		sinonimoMedicamentoPaginatorController.setFromList(Boolean.FALSE);
		return "sinonimoMedicamentoList";
	}
	public String medicamentoEquivalente(){
		medicamentoEquivalentePaginatorController.setMedicamento(getEntidade());
		medicamentoEquivalentePaginatorController.setFromList(Boolean.FALSE);
		return "medicamentoEquivalenteList";
	}
	
	public String historicoMedicamento(){
		historicoCadastroMedicamentoPaginatorController.setMedicamento(getEntidade());
		historicoCadastroMedicamentoPaginatorController.setPageVoltar("medicamentoCRUD");
		historicoCadastroMedicamentoPaginatorController.getDataModel().reiniciarPaginator();
		return "historicoCadastroMedicamentoList";
	}
	
	
	public String redirecionarManterPrescricaoSolucao(){
		return PAGE_PRESCRICAOMEDICA_MANTER_PRESCRICAO_SOLUCAO;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}
	
	public String cancelar() {
		reiniciarPaginatorController();
		if (origem == null) {		
			return super.cancelar();
		} else {
			prepararCancelamento();
			return origem;
		}
	}

	public AfaMedicamento getMedicamentoSelecionado() {
		return medicamentoSelecionado;
	}

	public void setMedicamentoSelecionado(AfaMedicamento medicamentoSelecionado) {
		this.medicamentoSelecionado = medicamentoSelecionado;
	}
	
	
	
}