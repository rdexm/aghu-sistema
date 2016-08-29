package br.gov.mec.aghu.compras.solicitacaocompras.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.solicitacaocompra.business.ISolicitacaoComprasFacade;
import br.gov.mec.aghu.compras.solicitacaoservico.business.ISolicitacaoServicoFacade;
import br.gov.mec.aghu.model.ScoSolicitacaoCompraServico;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.Severity;


public class SolicitacaoCompraServicoController extends ActionController {
	
	private static final String SOLICITACAO_SERVICO_CRUD = "compras-solicitacaoServicoCRUD";

	private static final Log LOG = LogFactory.getLog(SolicitacaoCompraServicoController.class);

	private static final long serialVersionUID = -3462929396765844271L;

	private Integer numeroSolicitacaoCompra;
	private ScoSolicitacaoDeCompra solicitacaoDeCompra;
	
	@EJB
	private ISolicitacaoComprasFacade solicitacaoComprasFacade;
	
	@EJB
	private ISolicitacaoServicoFacade solicitacaoServicoFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	private ScoSolicitacaoServico solServico;
	
	private Integer numeroSS;
	
	private String voltarPara;
	private boolean showModalExclusao;
		
	private List<ScoSolicitacaoCompraServico> listaSolServicosCompras = new ArrayList<ScoSolicitacaoCompraServico>();
	
	private ScoSolicitacaoCompraServico solCompraServico = new ScoSolicitacaoCompraServico();
	
	private ScoSolicitacaoCompraServico itemScoSolCompra;

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	
	public void iniciar() {
	 

	 

		try {
			carregaDadosSC();
			if (this.getNumeroSS() != null){
				setSolServico(this.solicitacaoServicoFacade.obterSolicitacaoServico(this.getNumeroSS()));
			}
			else {
				setSolServico(null);
			}					
			listaSolServicosCompras = recuperaListaSolServicos();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	
	}
	
	
	private void carregaDadosSC() throws ApplicationBusinessException {
		recuperaScoSolicitacaoDeCompra();
	}
	
	//Método para carregar suggestion Solicitação de Serviços
		public List<ScoSolicitacaoServico> pesquisarSolicitacaoServicoPorNumeroOuDescricao(String solicitacaoServico) {
			return this.solicitacaoServicoFacade.pesquisarSolicitacaoServicoPorNumeroOuDescricao((String)solicitacaoServico);
		}
	
	private void recuperaScoSolicitacaoDeCompra() {
		setSolicitacaoDeCompra(solicitacaoComprasFacade.obterSolicitacaoDeCompra(getNumeroSolicitacaoCompra()));		
	}
	
	private List<ScoSolicitacaoCompraServico> recuperaListaSolServicos(){
		
		return this.solicitacaoServicoFacade.pesquisarSolServicosPorSolCompra(getSolicitacaoDeCompra());
	}
	
	
	public void adicionarSolServico() {

		try {
			
			this.solServico.setServico(this.comprasFacade.obterServicoPorId(this.solServico.getServico().getCodigo()));
			
			
			this.getSolCompraServico().setScoSolicitacaoServico(this.solServico);
			this.getSolCompraServico().setScoSolicitacaoDeCompra(this.getSolicitacaoDeCompra());
		
			if (!this.getListaSolServicosCompras().contains(this.getSolCompraServico())) {
				this.getListaSolServicosCompras().add(this.getSolCompraServico());

			} else {
				this.solicitacaoServicoFacade.mensagemErroDuplicadoScoSolServicoCompras();
	
			}

			this.limpar();
				

		} catch (final ApplicationBusinessException e) {
			LOG.error(e.getMessage(), e);
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void limpar() {
		this.solServico = null;
		this.setSolCompraServico(new ScoSolicitacaoCompraServico());
	}
	
	
	public void gravar() {

		try {

			//Deletar todos os registros de servicos
			List<ScoSolicitacaoCompraServico> listExcluir = this.solicitacaoServicoFacade.listarSolicitacaoCompraServico(getSolicitacaoDeCompra(), null);
			
			if (listExcluir!=null && listExcluir.size()>0){
				
				for (ScoSolicitacaoCompraServico solicitacaoCompraServico : listExcluir ){
					this.solicitacaoServicoFacade.excluirScoSolCompraServico(solicitacaoCompraServico);
				}
			}
	
			for (ScoSolicitacaoCompraServico solicitacaoCompraServico : this.getListaSolServicosCompras()){
				this.solicitacaoServicoFacade.inserirScoSolCompraServico(solicitacaoCompraServico);
			}

				this.setShowModalExclusao(false);
				this.apresentarMsgNegocio(Severity.INFO,
						"MESSAGEM_SUCESSO_ASS_COMPRA_SERVICO_PERSISTENTE");
		

		} catch (final BaseException e) {
			LOG.error(e.getMessage(), e);
			apresentarExcecaoNegocio(e);
		} catch (final BaseRuntimeException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
		
	public void excluir() {

		if (this.listaSolServicosCompras.size()==1){
			this.listaSolServicosCompras = new ArrayList<ScoSolicitacaoCompraServico>();
		}
		else {this.listaSolServicosCompras.remove(itemScoSolCompra);}
		
	}
	
	public String voltar() {
		return this.voltarPara;
	}
	
	public String novaLista(){
		this.listaSolServicosCompras = new ArrayList<ScoSolicitacaoCompraServico>();
		return null;
	}
	
	public String mostrarConfirmacao(){
		this.setShowModalExclusao(true);
		return null;
	}
	
	public String gerarSS(){
		return SOLICITACAO_SERVICO_CRUD;
	}
	
	
	
   public  boolean isDiferencaTelaBanco(){
		
        List<ScoSolicitacaoCompraServico> listaSolicitacoesCompraBanco = this.solicitacaoServicoFacade.listarSolicitacaoCompraServico(getSolicitacaoDeCompra(), null);
		
		/**** Pega todas os registros do banco e compara com os da tela restando apenas os que devem ser apagados
		 *    caso nao exista associações para sc verifica se usuario adicionou algo a lista ***/
        if (listaSolicitacoesCompraBanco.size() > 0){
        	boolean flagLista = (this.getListaSolServicosCompras().size() > listaSolicitacoesCompraBanco.size());
        	listaSolicitacoesCompraBanco.removeAll(getListaSolServicosCompras());
		    return (listaSolicitacoesCompraBanco.size() > 0 || flagLista);        	
        }
        else{
        	return (this.getListaSolServicosCompras().size() > 0);
        }
		
	}
	
	public String validaItensPendentes() {

		this.setShowModalExclusao(isDiferencaTelaBanco());
		
		if (!this.showModalExclusao){
			return this.voltar();
		}
		else
		{
			this.openDialog("modalConfirmacaoExclusaoWG");
			return null;
		}
		    
	}
	
	public void cancelarPendenteItens() {		
		this.setShowModalExclusao(false);
	}	
	
	public ScoSolicitacaoDeCompra getSolicitacaoDeCompra() {
		return solicitacaoDeCompra;
	}

	public void setSolicitacaoDeCompra(ScoSolicitacaoDeCompra solicitacaoDeCompra) {
		this.solicitacaoDeCompra = solicitacaoDeCompra;
	}
	
	public Integer getNumeroSolicitacaoCompra() {
		return numeroSolicitacaoCompra;
	}

	public void setNumeroSolicitacaoCompra(Integer numeroSolicitacaoCompra) {
		this.numeroSolicitacaoCompra = numeroSolicitacaoCompra;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public ScoSolicitacaoServico getSolServico() {
		return solServico;
	}

	public void setSolServico(ScoSolicitacaoServico solServico) {
		this.solServico = solServico;
	}

	public ScoSolicitacaoCompraServico getSolCompraServico() {
		return solCompraServico;
	}

	public void setSolCompraServico(ScoSolicitacaoCompraServico solCompraServico) {
		this.solCompraServico = solCompraServico;
	}

	public Integer getNumeroSS() {
		return numeroSS;
	}

	public void setNumeroSS(Integer numeroSS) {
		this.numeroSS = numeroSS;
	}

	
	public boolean isShowModalExclusao() {
		return showModalExclusao;
	}

	public void setShowModalExclusao(boolean showModalExclusao) {
		this.showModalExclusao = showModalExclusao;
	}

	public List<ScoSolicitacaoCompraServico> getListaSolServicosCompras() {
		return listaSolServicosCompras;
	}

	public void setListaSolServicosCompras(
			List<ScoSolicitacaoCompraServico> listaSolServicosCompras) {
		this.listaSolServicosCompras = listaSolServicosCompras;
	}


	public ScoSolicitacaoCompraServico getItemScoSolCompra() {
		return itemScoSolCompra;
	}


	public void setItemScoSolCompra(ScoSolicitacaoCompraServico itemScoSolCompra) {
		this.itemScoSolCompra = itemScoSolCompra;
	}
}