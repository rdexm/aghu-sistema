package br.gov.mec.aghu.farmacia.cadastroapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaTipoUsoMdto;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

/**
 * Controller da tela de pesquisa de medicamento
 * 
 * @author lcmoura
 * 
 */

public class MedicamentoPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -5771378424461964824L;

	@Inject
	private IFarmaciaApoioFacade farmaciaApoioFacade;
	
	@EJB
	private IPermissionService permissionService;
	
	private AfaMedicamento medicamentoPesquisa;
	private AfaMedicamento medicamentoSelecionado;
	
	@Inject @Paginator
	private DynamicDataModel<AfaMedicamento> dataModel;
	
	private DominioSimNao indRevisaoCadastro;
	
	@Inject
	private MedicamentoController medicamentoController;
	
	@PostConstruct
	public void init(){
		dataModel.setUserEditPermission(permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "medicamento", "alterar"));
		begin(conversation);
		medicamentoPesquisa = new AfaMedicamento();
	}
	
	public static enum RetornoAcaoStrEnum {
		
		SINONIMO_MEDICAMENTO("sinonimoMedicamentoList"),
		FORMA_DOSAGEM("formaDosagemList"),
		VIA_ADM_MDTO("viaAdministracaoMedicamentoList"),
		LOCAL_DISPENSACAO_MDTO("localDispensacaoMedicamentoList"),
		INICIAR_INCLUSAO("iniciarInclusao"),
		CONFIRMADO("confirmado"),
		CANCELADO("cancelado"),	
		ERRO("erro"),
		SUBSTITUTO_MEDICAMENTO("medicamentoEquivalenteList"),
		HISTORICO_MDTO("historicoCadastroMedicamentoList"),
		MEDICAMENTO_CRUD("medicamentoCRUD"),
		CADASTRAR_DILUENTES("farmacia-cadastrarDiluentes");
		
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
	public Long recuperarCount() {
		return farmaciaApoioFacade.pesquisarListaAfaMedicamentoCount(medicamentoPesquisa);
	}

	@Override
	public List<AfaMedicamento> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String order, boolean asc) {
		return farmaciaApoioFacade.pesquisarListaAfaMedicamento(
				firstResult,maxResult, AfaTipoUsoMdto.Fields.DESCRICAO.toString(),asc,medicamentoPesquisa);
	}
	
	/**
	 * Pesquisa as tipos de uso de medicamentos ativos
	 * 
	 * @param siglaOuDescricao
	 * @return
	 */
	public List<AfaTipoUsoMdto> pesquisaTipoUsoMdtoAtivos(
			String siglaOuDescricao) {
		return farmaciaApoioFacade.pesquisaTipoUsoMdtoAtivos(siglaOuDescricao);
	}

	/**
	 * Método executado ao clicar no botão pesquisar
	 */
	public void pesquisar() {
		setDominioSimNaoToBoolean();
		this.medicamentoSelecionado = null;
		getDataModel().reiniciarPaginator();
	}
	
	
	public String realizarChamadaViaAdministracaoMedicamento() {
		String retorno = null;
		if (this.medicamentoSelecionado != null) {
			retorno = RetornoAcaoStrEnum.VIA_ADM_MDTO.toString();
		}
		return retorno;
	}
	
	public String realizarChamadaFormaDosagem() {
		String retorno = null;
		if (this.medicamentoSelecionado != null) {
			retorno = RetornoAcaoStrEnum.FORMA_DOSAGEM.toString();
		}
		return retorno;
	}
	
	public String realizarChamadaLocalDispensacaoMedicamento() {
		String retorno = null;
		if (this.medicamentoSelecionado != null) {
			retorno = RetornoAcaoStrEnum.LOCAL_DISPENSACAO_MDTO.toString();
		}
		return retorno;
	}
	
	public String realizarChamadaDiluentes(){
		String retorno = RetornoAcaoStrEnum.ERRO.toString();
		if (this.medicamentoSelecionado != null) {
			retorno = RetornoAcaoStrEnum.CADASTRAR_DILUENTES.toString();
		}
		return retorno;
	}
	
	/**
	 * Chama a tela de Historico de Medicamento
	 * 
	 * @return
	 */
	public String realizarChamadaHistoricoMedicamento() {
		String retorno = null;
		if (this.medicamentoSelecionado != null) {
			retorno = RetornoAcaoStrEnum.HISTORICO_MDTO.toString();
		}
		return retorno;
	}
	
	public String realizarChamadaSinonimoMedicamento() {
		String retorno = null;
		if (this.medicamentoSelecionado != null) {
			retorno = RetornoAcaoStrEnum.SINONIMO_MEDICAMENTO.toString();
		}
		return retorno;
	}
	
	public String realizarChamadaSubstitutosMedicamentos() {
		String retorno = null;
		if (this.medicamentoSelecionado != null) {
			retorno = RetornoAcaoStrEnum.SUBSTITUTO_MEDICAMENTO.toString();
		}
		return retorno;
	}
	

	private void setDominioSimNaoToBoolean() {
		getMedicamentoPesquisa().setIndRevisaoCadastro(null);
		
		if(indRevisaoCadastro != null){
			getMedicamentoPesquisa().setIndRevisaoCadastro(getIndRevisaoCadastro().isSim());
		}
		
	}

	public void limpar() {
		medicamentoPesquisa = new AfaMedicamento();
		indRevisaoCadastro = null;
		getDataModel().limparPesquisa();
	}
	
	public String editar(){
		medicamentoController.setEntidade(medicamentoSelecionado);
		return RetornoAcaoStrEnum.MEDICAMENTO_CRUD.toString();
	}
	
	public AfaMedicamento getMedicamentoPesquisa() {
		return medicamentoPesquisa;
	}

	public void setMedicamentoPesquisa(AfaMedicamento medicamentoPesquisa) {
		this.medicamentoPesquisa = medicamentoPesquisa;
	}

	public AfaMedicamento getMedicamentoSelecionado() {
		return medicamentoSelecionado;
	}

	public void setMedicamentoSelecionado(AfaMedicamento medicamentoSelecionado) {
		this.medicamentoSelecionado = medicamentoSelecionado;
	}

	public DynamicDataModel<AfaMedicamento> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AfaMedicamento> dataModel) {
		this.dataModel = dataModel;
	}

	public DominioSimNao getIndRevisaoCadastro() {
		return indRevisaoCadastro;
	}

	public void setIndRevisaoCadastro(DominioSimNao indRevisaoCadastro) {
		this.indRevisaoCadastro = indRevisaoCadastro;
	}

}