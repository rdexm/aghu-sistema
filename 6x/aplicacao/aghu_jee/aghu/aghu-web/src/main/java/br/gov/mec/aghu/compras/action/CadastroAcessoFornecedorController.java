package br.gov.mec.aghu.compras.action;

import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoProgrCodAcessoForn;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

import com.itextpdf.text.DocumentException;


public class CadastroAcessoFornecedorController extends ActionController {

	private static final String CADASTRAR_CONTATO_FORNECEDOR = "compras-cadastrarContatoFornecedor";
	
//	private static final String MANTER_CADASTRO_FORNECEDOR= "compras-manterCadastroFornecedor";

	private static final String IMPRIMIR_ACESSO_FORNECEDOR_PDF = "imprimirAcessoFornecedorPdf";

	private static final String CADASTRAR_ACESSO_FORNECEDOR = "compras-cadastrarAcessoFornecedorList";
	
	private static final Log LOG = LogFactory.getLog(CadastroAcessoFornecedorController.class);

	private static final long serialVersionUID = -3037020657461566129L;

	@EJB
	private IComprasFacade comprasFacade;
	
	@Inject
	private ImprimirAcessoFornecedorController imprimirAcessoFornecedorController;
	
	private Integer seq;
	private Integer numeroFrn;
	private ScoProgrCodAcessoForn acessoFornecedor;
	private ScoFornecedor fornecedor;
	private boolean visualizar = false;
	private boolean editar = false;
	private boolean novo = false;
	private String pendencia;
	private String situacao;
	private String colorSituacao;
	private Integer seqFornecedor;
	private String voltarParaUrl;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void inicio() {

		if (novo) {
			setAcessoFornecedor(new ScoProgrCodAcessoForn());
			setFornecedor(null);
			setPendencia(null);
			setSeq(null);
			setSituacao(null);
		}
		
		if (getNumeroFrn() != null) {
			
			setFornecedor(comprasFacade.obterFornecedorPorChavePrimaria(getNumeroFrn()));
			
				setSeq(comprasFacade.obterSeqFornecedorPorNumero(getFornecedor()));
					if(getSeq()==null){
						novo=true;
						setAcessoFornecedor(new ScoProgrCodAcessoForn());
						setPendencia(null);
					}else{
						setAcessoFornecedor(comprasFacade.buscarScoProgrCodAcessoForn(getSeq()));
						setPendencia(comprasFacade.buscarPendenciaAcessoFornecedor(getAcessoFornecedor()));
						setSituacao(comprasFacade.buscarSituacaoAcessoFornecedor(getAcessoFornecedor(), getColorSituacao()));
					}
		}else if (getSeq() != null){
			
			setAcessoFornecedor(comprasFacade.buscarScoProgrCodAcessoForn(getSeq()));
			setFornecedor(getAcessoFornecedor().getScoFornecedor());
			setPendencia(comprasFacade.buscarPendenciaAcessoFornecedor(getAcessoFornecedor()));
			setSituacao(comprasFacade.buscarSituacaoAcessoFornecedor(getAcessoFornecedor(), getColorSituacao()));
		}
	
	}
	
	
	public void gravar() throws ApplicationBusinessException, ApplicationBusinessException {
		try {
			getAcessoFornecedor().setScoFornecedor(getFornecedor());
			comprasFacade.persistirAcessoFornecedor(getAcessoFornecedor());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_CADASTRO_SENHA_SUCESSO");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} 
	}
	
	public void editar() throws ApplicationBusinessException, ApplicationBusinessException {
		try {
			getAcessoFornecedor().setScoFornecedor(getFornecedor());
			comprasFacade.atualizarAcessoFornecedor(getAcessoFornecedor());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ATUALIZACAO_SENHA_SUCESSO");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
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

	public String cancelar() {
		return voltarParaUrl;
	}
	
	public String voltar() {
		if (StringUtils.isNotBlank(this.voltarParaUrl)){
			return this.voltarParaUrl;
		}
		return CADASTRAR_ACESSO_FORNECEDOR;
	}
	
	public List<ScoFornecedor> pesquisarFornecedores(String param) {
		return  this.returnSGWithCount(comprasFacade.listarFornecedoresAtivosPorNumeroCnpjRazaoSocial(param),pesquisarFornecedoresCount(param));
	}
	
	public Long pesquisarFornecedoresCount(String param) {
		return comprasFacade.listarFornecedoresAtivosPorNumeroCnpjRazaoSocialCount(param);
	}

	public Integer getSeq() {
		return seq;
	}

	public ScoProgrCodAcessoForn getAcessoFornecedor() {
		return acessoFornecedor;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public void setAcessoFornecedor(ScoProgrCodAcessoForn acessoFornecedor) {
		this.acessoFornecedor = acessoFornecedor;
	}

	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public boolean isVisualizar() {
		return visualizar;
	}

	public boolean isEditar() {
		return editar;
	}

	public void setVisualizar(boolean visualizar) {
		this.visualizar = visualizar;
	}

	public void setEditar(boolean editar) {
		this.editar = editar;
	}
	
	public String getPendencia() {
		return pendencia;
	}

	public void setPendencia(String pendencia) {
		this.pendencia = pendencia;
	}

	public boolean isNovo() {
		return novo;
	}

	public void setNovo(boolean novo) {
		this.novo = novo;
	}

	public String getSituacao() {
		return situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}

	public String getColorSituacao() {
		return colorSituacao;
	}

	public void setColorSituacao(String colorSituacao) {
		this.colorSituacao = colorSituacao;
	}
	
	public Integer getSeqFornecedor() {
		return this.seqFornecedor;
	}

	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}

	public Integer getNumeroFrn() {
		return numeroFrn;
	}

	public void setNumeroFrn(Integer numeroFrn) {
		this.numeroFrn = numeroFrn;
	}
}
