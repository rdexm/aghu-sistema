package br.gov.mec.aghu.compras.action;

import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.vo.ScoProgrCodAcessoFornVO;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoProgrCodAcessoForn;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

import com.itextpdf.text.DocumentException;


public class CadastroAcessoFornecedorPaginatorController extends ActionController implements ActionPaginator {

	private static final String CADASTRAR_CONTATO_FORNECEDOR = "compras-cadastrarContatoFornecedor";

	private static final String IMPRIMIR_ACESSO_FORNECEDOR_PDF = "imprimirAcessoFornecedorPdf";

	private static final String CADASTRAR_ACESSO_FORNECEDOR_CRUD = "cadastrarAcessoFornecedorCRUD";

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<ScoProgrCodAcessoFornVO> dataModel;

	private static final Log LOG = LogFactory.getLog(CadastroAcessoFornecedorPaginatorController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -480247529602377287L;

	@EJB
	private IComprasFacade comprasFacade;
	
	@Inject
	private ImprimirAcessoFornecedorController imprimirAcessoFornecedorController;

	
	private ScoFornecedor fornecedor;
	private Integer seq;
	private Integer seqFornecedor;
	private ScoProgrCodAcessoForn acessoFornecedor;
	
	private ScoProgrCodAcessoFornVO selecionado = new ScoProgrCodAcessoFornVO();
	
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}
	
	public void limpar() {
		setFornecedor(null);
		setAtivo(false);
		setSeq(null);
		this.dataModel.limparPesquisa();
	}
	
	public String novo() {
		setSeq(null);
		return redirecionarCadastrarAcessoFornecedorCRUD();
	}
	
	public void selecionarAcessoFornecedor(){
		this.setSeq(this.getSelecionado().getSeq());
		this.setAcessoFornecedor(comprasFacade.obterScoProgrCodAcessoFornPorChavePrimaria(getSeq()));
	}
	
	public void enviarEmailContatos() throws ApplicationBusinessException, ApplicationBusinessException {
		try {
			comprasFacade.enviarEmailContatos(getSeq());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_EMAIL_CONTATO_ENVIADO_SUCESSO");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} 
	}
	
	public void enviarEmailSenha() throws JRException, IOException, DocumentException, BaseException {
		try {
			imprimirAcessoFornecedorController.setCodigo(getSeq());
			byte[] jasper = imprimirAcessoFornecedorController.buscarDocumentoGerado().getPdfByteArray(false);
			comprasFacade.enviarEmailSenha(getSeq(), jasper);
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_EMAIL_SENHA_ENVIADO_SUCESSO");
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (JRException e) {
			LOG.error(e.getMessage(), e);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	public String  imprimir() {
		imprimirAcessoFornecedorController.setCodigo(getSeq());
		
		try {
			imprimirAcessoFornecedorController.print();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (JRException e) {
			LOG.error(e.getMessage(), e);
		} catch (SystemException e) {
			LOG.error(e.getMessage(), e);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}

		return IMPRIMIR_ACESSO_FORNECEDOR_PDF;
	}
	
	public String cadastrarContato() {
		return CADASTRAR_CONTATO_FORNECEDOR;
	}
	
	public String redirecionarCadastrarAcessoFornecedorCRUD(){
		return CADASTRAR_ACESSO_FORNECEDOR_CRUD;
	}

	@Override
	public Long recuperarCount() {
		return comprasFacade.listarFornecedoresCount(this.fornecedor);
	}

	@Override
	public List<ScoProgrCodAcessoFornVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return comprasFacade.listarFornecedores(fornecedor, firstResult, maxResult, orderProperty, asc);
	}
	
	public List<ScoFornecedor> pesquisarFornecedores(String param) {
		setSeq(null);
		return this.returnSGWithCount(comprasFacade.listarFornecedoresPorNumeroCnpjRazaoSocial(param),pesquisarFornecedoresCount(param));
	}
	
	public Long pesquisarFornecedoresCount(String param) {
		return comprasFacade.listarFornecedoresPorNumeroCnpjRazaoSocialCount(param);
	}
	
	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
	public Integer getSeqFornecedor() {
		return seqFornecedor;
	}

	public void setSeqFornecedor(Integer seqFornecedor) {
		this.seqFornecedor = seqFornecedor;
	} 

	public DynamicDataModel<ScoProgrCodAcessoFornVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ScoProgrCodAcessoFornVO> dataModel) {
		this.dataModel = dataModel;
	}
	
	public void setAtivo(boolean ativo){
		this.dataModel.setPesquisaAtiva(ativo);
	}
	
	public boolean isAtivo(){
		return this.dataModel.getPesquisaAtiva();
	}

	public ScoProgrCodAcessoForn getAcessoFornecedor() {
		return acessoFornecedor;
	}

	public void setAcessoFornecedor(ScoProgrCodAcessoForn acessoFornecedor) {
		this.acessoFornecedor = acessoFornecedor;
	}

	public ScoProgrCodAcessoFornVO getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(ScoProgrCodAcessoFornVO selecionado) {
		this.selecionado = selecionado;
	}
}
