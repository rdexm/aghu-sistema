	package br.gov.mec.aghu.faturamento.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioTipoIdadeUTI;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.vo.FatBancoCapacidadeVO;
import br.gov.mec.aghu.faturamento.vo.FatSaldoUTIVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.Severity;


public class PesquisaBancoUTIPaginatorController extends ActionController implements ActionPaginator {


	/**
	 * 
	 */
	private static final long serialVersionUID = 5626145373844848030L;

	@Inject @Paginator
	private DynamicDataModel<FatBancoCapacidadeVO> dataModel;

	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IPermissionService permissionService;
	
	// FILTRO
	private Integer mes;
	private Integer ano;
	private DominioTipoIdadeUTI tipoUTI;
	private FatSaldoUTIVO selected;

	@PostConstruct
	public void init() {		
		begin(conversation);
		final Boolean permissao = this.permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "manterCadastrosBasicosFaturamento","executar");
		this.dataModel.setUserEditPermission(permissao);
	}

	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}

	public void limparPesquisa() {
		this.mes = null;
		this.ano = null;
		this.tipoUTI = null;

		this.dataModel.limparPesquisa();
	}

	@Override
	public Long recuperarCount() {		
		return this.faturamentoFacade.pesquisarBancosUTICount(this.mes, this.ano, this.tipoUTI);
	}

	@Override
	public List<FatSaldoUTIVO> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		return this.faturamentoFacade.pesquisarBancosUTI(this.mes, this.ano, this.tipoUTI,firstResult,maxResult, orderProperty, asc);
	}
	
	public void atualizarSituacaoBancoCapacidade(FatBancoCapacidadeVO item) {
		apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_BANCO_CAPACIDADE");
	}
	
	public String cadastroBancoUTI() {
		return "faturamento-cadastroBancoUTI";
	}
	
	public String editar() {
		return "faturamento-cadastroBancoUTI";
	}
	
	public Integer getMes() {
		return mes;
	}

	public void setMes(Integer mes) {
		this.mes = mes;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public DynamicDataModel<FatBancoCapacidadeVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<FatBancoCapacidadeVO> dataModel) {
		this.dataModel = dataModel;
	}

	public DominioTipoIdadeUTI getTipoUTI() {
		return tipoUTI;
	}

	public void setTipoUTI(DominioTipoIdadeUTI tipoUTI) {
		this.tipoUTI = tipoUTI;
	}

	public FatSaldoUTIVO getSelected() {
		return selected;
	}

	public void setSelected(FatSaldoUTIVO selected) {
		this.selected = selected;
	}
	
}
