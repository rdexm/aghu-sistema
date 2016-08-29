package br.gov.mec.aghu.compras.autfornecimento.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.autfornecimento.vo.FiltroAFPVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.PesquisaItemAFPVO;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;


public class ItensAutorizacaoFornecimentoPedidoController extends ActionController {

	

	private static final String ESTOQUE_ESTATISTICA_CONSUMO = "estoque-estatisticaConsumo";

	private static final String AUTORIZACAO_FORNECIMENTO_CRUD = "autorizacaoFornecimentoCRUD";

	private static final String ASSINAR_AUTORIZACAO_FORNECIMENTO = "assinarAutorizacaoFornecimento";

	private static final long serialVersionUID = 7427334723240933288L;

	public enum ItensAutorizacaoFornecimentoPedidoControllerExceptionCode implements BusinessExceptionCode {
		ERRO_ENVIO_EMAIL_AFP;
	}
	
	@EJB
	protected IComprasFacade comprasFacade;

	@EJB
	protected IAutFornecimentoFacade autFornecimentoFacade;

	@Inject
	protected EnvioEmailAssinaturaAFController envioEmailAssinaturaAFController;
	
	private ScoAutorizacaoForn autorizacaoForn;

	private FiltroAFPVO filtro = new FiltroAFPVO();
	private ScoMaterial material;

	
	private List<PesquisaItemAFPVO> itensAfp;

	private Boolean refazerPesquisa;


	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}

	public void inicio() {
	 

	 

		if (refazerPesquisa == null || !refazerPesquisa) {
			if (this.getAutorizacaoForn() == null && filtro.getNumeroAf() != null) {
				this.setAutorizacaoForn(comprasFacade.obterScoAutorizacaoFornPorChavePrimaria(filtro.getNumeroAf()));
			}
			this.pesquisar();
		}
	
	}
	
    public ScoMaterial obterMaterial(Integer codigoMat){
    	ScoMaterial scoMaterial = this.comprasFacade.obterMaterialPorId(codigoMat);
    	if(scoMaterial != null) {
    		return scoMaterial;
    	}
    	return null;
    }
    
    public ScoServico obterServico(Integer codigoServ){    	
    	ScoServico scoServico = this.comprasFacade.obterServicoPorId(codigoServ);
    	if(scoServico != null) {
    		return scoServico;
    	}
    	return null;
    }
   

	public void pesquisar() {
		if (material != null) {
			this.filtro.setCodigoMaterial(material.getCodigo());
		}
		this.itensAfp = this.autFornecimentoFacade.pesquisarItensAFPedido(this.filtro);
		
	}

	public void limpar() {
		this.filtro = new FiltroAFPVO();
		this.filtro.setNumeroAf(getAutorizacaoForn().getNumero());
		this.pesquisar();
	}
	
	public String voltarAssinarAf(){
		return ASSINAR_AUTORIZACAO_FORNECIMENTO;
	}
	
	
	public String verPedidoAutorizacapFornecimento(){
		return AUTORIZACAO_FORNECIMENTO_CRUD;
	}
	
	public String visualizarEstatisticasConsumo(){
		return ESTOQUE_ESTATISTICA_CONSUMO;
	}

	public void abrirEstatisticaConsumo(final Integer numeroAF) {
		this.pesquisar();
	}

	public List<ScoMaterial> listarMateriais(String filter) {
		return this.returnSGWithCount(this.comprasFacade.listarScoMateriais(filter, null, true),listarMateriaisCount(filter));
	}

	public Long listarMateriaisCount(String filter) {
		return this.comprasFacade.listarScoMateriaisCount(filter, null, true);
	}

	public void setItensAfp(List<PesquisaItemAFPVO> itensAfp) {
		this.itensAfp = itensAfp;
	}

	public List<PesquisaItemAFPVO> getItensAfp() {
		return itensAfp;
	}

	public void setFiltro(FiltroAFPVO filtro) {
		this.filtro = filtro;
	}

	public FiltroAFPVO getFiltro() {
		return filtro;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	public ScoMaterial getMaterial() {
		return material;
	}

	

	public void setAutorizacaoForn(ScoAutorizacaoForn autorizacaoForn) {
		this.autorizacaoForn = autorizacaoForn;
	}

	public ScoAutorizacaoForn getAutorizacaoForn() {
		return autorizacaoForn;
	}

	public void setRefazerPesquisa(Boolean refazerPesquisa) {
		this.refazerPesquisa = refazerPesquisa;
	}

	public Boolean getRefazerPesquisa() {
		return refazerPesquisa;
	}

	

}
