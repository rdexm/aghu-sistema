package br.gov.mec.aghu.compras.action;


import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.contaspagar.action.ImprimeMovimentacaoFornecedorController;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.ScoContatoFornecedor;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class CadastroContatoFornecedorController extends ActionController{

	private static final long serialVersionUID = 631026886886170255L;
	
	private ScoFornecedor fornecedor;
	private ScoContatoFornecedor contatoFornecedor;
	
	private Boolean indEnvioEmailSupTec;
	private Integer numeroFrn;
	private List<ScoContatoFornecedor> listaContatos;
	private Boolean confirmaExclusao;
	private Boolean confirmaAlteracao;	
	private String voltarParaUrl;
	private boolean update;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@Inject
	private ImprimeMovimentacaoFornecedorController imprimeMovimentacaoFornecedorController; 
	
	private static final Enum[] fetchArgsInnerJoin = {ScoContatoFornecedor.Fields.SCO_FORNECEDOR};
	private static final String PAGE_CADASTRAR_CONTATO_FORNECEDOR = "compras-imprimeMovimentacaoFornecedor";
		
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void iniciar(){
		this.confirmaExclusao = false;
		this.confirmaAlteracao = false;
		this.update = false;
		if (numeroFrn!=null){
			fornecedor = comprasFacade.obterFornecedorPorNumero(numeroFrn);
			listaContatos = comprasFacade.pesquisarContatosPorFornecedor(numeroFrn);
		}
		if (this.contatoFornecedor==null){
			this.contatoFornecedor = new ScoContatoFornecedor();
		}	
	}

	public void editar(ScoContatoFornecedor contatoFornecedor){
		
		if(contatoFornecedor.getIndEnvioEmailSupTec() != null && contatoFornecedor.getIndEnvioEmailSupTec().equals(DominioSimNao.S)){
			setIndEnvioEmailSupTec(Boolean.TRUE);
		}
		else if (contatoFornecedor.getIndEnvioEmailSupTec() != null){
			setIndEnvioEmailSupTec(Boolean.FALSE);
		}
		if(contatoFornecedor!=null && contatoFornecedor.getId()!=null){
			this.contatoFornecedor = comprasFacade.obterContatoFornecedor(contatoFornecedor.getId(), fetchArgsInnerJoin , null);

			if(this.contatoFornecedor == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				listaContatos = comprasFacade.pesquisarContatosPorFornecedor(numeroFrn);
				cancelar();
				return;
			}
			
			update = true;
		}
	}
	
	public void prepararExclusao(ScoContatoFornecedor contatoFornecedor) {			
		this.contatoFornecedor = contatoFornecedor;
		this.setConfirmaExclusao(true);		
	}
	
	public String excluir() {
		if (this.getContatoFornecedor() != null && this.contatoFornecedor.getId()!=null) {
			
			try {
				comprasFacade.removerContatoFornecedor(this.getContatoFornecedor().getId());
				
				apresentarMsgNegocio(Severity.INFO, "SUCESSO_EXCLUSAO_CONTATO_FORNECEDOR");
				
				this.setConfirmaExclusao(false);
				this.contatoFornecedor = new ScoContatoFornecedor();
				listaContatos = comprasFacade.pesquisarContatosPorFornecedor(numeroFrn);
				
			} catch (final ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
				this.setConfirmaExclusao(false);
			}
		}
		return null;
	}
	
	public String gravar(){
		try {

			boolean isInsert = false;
			if(indEnvioEmailSupTec!=null){
				if(indEnvioEmailSupTec.equals(Boolean.TRUE)){
					contatoFornecedor.setIndEnvioEmailSupTec(DominioSimNao.S);
				}
				else if(indEnvioEmailSupTec != null){
					contatoFornecedor.setIndEnvioEmailSupTec(DominioSimNao.N);
				}
					
			}
			if (contatoFornecedor!=null && contatoFornecedor.getScoFornecedor()==null){
				contatoFornecedor.setScoFornecedor(fornecedor);
				isInsert = true;
			}
		
			comprasFacade.persistirContatoFornecedor(this.numeroFrn, contatoFornecedor);
			apresentarMsgNegocio(Severity.INFO, isInsert? "SUCESSO_INCLUSAO_CONTATO_FORNECEDOR" : "SUCESSO_ALTERADO_CONTATO_FORNECEDOR");
			setIndEnvioEmailSupTec(null);
			this.contatoFornecedor = new ScoContatoFornecedor();
			listaContatos = comprasFacade.pesquisarContatosPorFornecedor(numeroFrn);
			this.update = false;
			
			if(StringUtils.equalsIgnoreCase(voltarParaUrl, PAGE_CADASTRAR_CONTATO_FORNECEDOR)){
				imprimeMovimentacaoFornecedorController.pesquisar();
				return voltarParaUrl;
			}			
			
		} catch (final ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			this.setConfirmaExclusao(false);
		}
		
		return null;
	}
	
	public String voltar() {
		if(comprasFacade.verificarAlteracaoContatoFornecedor(contatoFornecedor)){
			this.setConfirmaAlteracao(true);
			return null;
		} 
		
		this.contatoFornecedor = null;
		
		return voltarParaUrl;
	}
	
	public String voltarDefinido() {
		this.contatoFornecedor = new ScoContatoFornecedor();
		return voltar();
	}

	public void cancelar(){
		update = false;
		setIndEnvioEmailSupTec(null);
		this.contatoFornecedor = new ScoContatoFornecedor();
	}


	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public ScoContatoFornecedor getContatoFornecedor() {
		return contatoFornecedor;
	}

	public void setContatoFornecedor(ScoContatoFornecedor contatoFornecedor) {
		this.contatoFornecedor = contatoFornecedor;
	}

	public Integer getNumeroFrn() {
		return numeroFrn;
	}

	public void setNumeroFrn(Integer numeroFrn) {
		this.numeroFrn = numeroFrn;
	}

	public List<ScoContatoFornecedor> getListaContatos() {
		return listaContatos;
	}

	public void setListaContatos(List<ScoContatoFornecedor> listaContatos) {
		this.listaContatos = listaContatos;
	}

	public Boolean getConfirmaExclusao() {
		return confirmaExclusao;
	}

	public void setConfirmaExclusao(Boolean confirmaExclusao) {
		this.confirmaExclusao = confirmaExclusao;
	}

	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}

	public boolean isUpdate() {
		return update;
	}

	public void setUpdate(boolean update) {
		this.update = update;
	} 

	public Boolean getConfirmaAlteracao() {
		return confirmaAlteracao;
	}

	public void setConfirmaAlteracao(Boolean confirmaAlteracao) {
		this.confirmaAlteracao = confirmaAlteracao;
	}
	
	public ImprimeMovimentacaoFornecedorController getImprimeMovimentacaoFornecedorController() {
		return imprimeMovimentacaoFornecedorController;
	}

	public void setImprimeMovimentacaoFornecedorController(
			ImprimeMovimentacaoFornecedorController imprimeMovimentacaoFornecedorController) {
		this.imprimeMovimentacaoFornecedorController = imprimeMovimentacaoFornecedorController;
	}
	
	public Boolean getindEnvioEmailSupTec() {
		return indEnvioEmailSupTec;
	}

	public void setIndEnvioEmailSupTec(Boolean indEnvioEmailSupTec) {
		this.indEnvioEmailSupTec = indEnvioEmailSupTec;
	}
}