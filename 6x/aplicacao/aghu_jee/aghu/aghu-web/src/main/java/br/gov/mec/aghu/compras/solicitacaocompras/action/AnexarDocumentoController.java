package br.gov.mec.aghu.compras.solicitacaocompras.action;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.compras.solicitacaocompra.business.ISolicitacaoComprasFacade;
import br.gov.mec.aghu.compras.solicitacaoservico.business.ISolicitacaoServicoFacade;
import br.gov.mec.aghu.dominio.DominioOrigemSolicitacaoSuprimento;
import br.gov.mec.aghu.model.ScoArquivoAnexo;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class AnexarDocumentoController extends ActionController {

	private static final String ESTOQUE_SERVICO_CRUD = "estoque-servicoCRUD";
	private static final long serialVersionUID = -1793599571786575237L;
	private static final Log LOG = LogFactory.getLog(AnexarDocumentoController.class);
	
	@EJB
	private ISolicitacaoComprasFacade solicitacaoComprasFacade;

	@EJB
	private ISolicitacaoServicoFacade solicitacaoServicoFacade;

	@EJB
	private IPacFacade pacFacade;

	@EJB
	private	ICascaFacade cascaService;
	
	@EJB
	IServidorLogadoFacade servidorLogadoFacade;
	
	private Boolean modoEdit;
	
	private String telaOrigem;
	private DominioOrigemSolicitacaoSuprimento origemSolicitacao;
	private Integer numeroSolicitacao;
	private String descricao;
	private String nomeZip = "";
	private String nomeArquivo;
	private String caminhoAbsolutoZip = "";
	private List<ScoArquivoAnexo> listaDocumentosAnexados;
	private ScoArquivoAnexo documentoEmEdicao;
	private UploadedFile UploadedFile;
	private boolean emEdicao;
	private Long seqArquivo;
	private byte[] anexo;

	@PostConstruct
	protected void inicializar(){		
		this.begin(conversation);
	}
	
	public void iniciar() {
	 

	 

		if (this.modoEdit == null) {
			this.modoEdit = Boolean.TRUE;
		}
		//if (listaDocumentosAnexados == null) {
			pesquisar();
		//}
	
	}
	
	public void pesquisar() {
		listaDocumentosAnexados = solicitacaoComprasFacade.pesquisarArquivosPorNumeroOrigem(origemSolicitacao, numeroSolicitacao);
	}

	/**
	 * Listener de upload para o arquivo
	 * 
	 * @param event
	 * @throws IOException 
	 */
	public void listener(FileUploadEvent event) throws IOException {
		this.UploadedFile = event.getFile();
		anexo = IOUtils.toByteArray(UploadedFile.getInputstream());
		this.gravar();
	}

	public void gravar() {
		try {
			if (emEdicao) {
				
				documentoEmEdicao.setDescricao(this.getDescricao());
				
				solicitacaoComprasFacade.alterarArquivoAnexo(documentoEmEdicao);
	
				documentoEmEdicao.setEmEdicao(false);
				documentoEmEdicao = null;
	
				this.emEdicao = false;
				
				this.apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_DOCUMENTO_ALTERADO_COM_SUCESSO");				
				
			} else {
				if (this.UploadedFile != null) {
					
					if (StringUtils.isEmpty(this.descricao)){
						this.apresentarMsgNegocio(Severity.INFO,
								"MENSAGEM_DESCRICAO_OBRIGATORIO");				
					} else {
					
					ScoArquivoAnexo arquivoAnexo = new ScoArquivoAnexo();
	
					// Converte arquivo em um array de Bytes
					//byte[] Anexo = IOUtils.toByteArray(UploadedFile.getInputstream());
					arquivoAnexo.setAnexo(anexo);
	
					arquivoAnexo.setArquivo(UploadedFile.getFileName());
					arquivoAnexo.setDescricao(this.descricao);
					arquivoAnexo.setDtInclusao(new Date());
					arquivoAnexo.setNumero(new BigInteger(this.numeroSolicitacao.toString()));  
					arquivoAnexo.setTpOrigem(this.origemSolicitacao);
					arquivoAnexo.setUsuario(servidorLogadoFacade.obterServidorLogado());
	
					solicitacaoComprasFacade.incluirArquivoAnexo(arquivoAnexo);
	
					this.apresentarMsgNegocio(Severity.INFO,
								"MENSAGEM_DOCUMENTO_INCLUIDO_COM_SUCESSO");
					}
				}
			}
			setDescricao(null);
			pesquisar();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} /*catch (IOException e) {
			LOG.error("Nao foi possivel ler o arquivo: ", e);
		}*/
	}

	public void editarDocumento(ScoArquivoAnexo arquivo) {
		documentoEmEdicao = arquivo;
		documentoEmEdicao.setEmEdicao(true);

		this.descricao = arquivo.getDescricao();
		this.emEdicao = true;
	}

	public void visualizarDocumento(ScoArquivoAnexo arquivoAnexo) {
		FacesContext context = FacesContext.getCurrentInstance();
		byte[] arquivo = arquivoAnexo.getAnexo();

		// Instancia uma HTTP Response
		HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();

		// Seta o tipo de MIME
		response.setContentType("application/octet-stream");

		// Obtém o nome do arquivo em anexo
		String nomeArquivo = arquivoAnexo.getArquivo();

		// Seta o arquivo no cabeçalho
		response.addHeader("Content-disposition", "attachment; filename=\""	+ nomeArquivo + "\"");

		// Escreve a resposta no HTTP RESPONSE
		ServletOutputStream os = null;
		try {
			os = response.getOutputStream();
			os.write(arquivo);
			os.flush();
			context.responseComplete();
		} catch (Exception e) {
			LOG.error(e.getMessage());
		} finally {
			IOUtils.closeQuietly(os);
		}
	}

	public void excluirDocumento() {
		if (seqArquivo != null) {
			try {
				solicitacaoComprasFacade.excluirArquivoAnexo(seqArquivo);
				
				this.apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_DOCUMENTO_EXCLUIDO_COM_SUCESSO");
				
				pesquisar();
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}
	}

	public void cancelar(){
		documentoEmEdicao.setEmEdicao(false);
		this.setEmEdicao(false);
		this.setDescricao(null);
	}
	
	public Boolean verificarPermissaoPorTipoOrigem(Boolean gravar) {
		if (this.modoEdit == Boolean.FALSE) {
			return this.modoEdit; 
		} else {
			if (this.getOrigemSolicitacao() != null) {
				if (this.getOrigemSolicitacao() == DominioOrigemSolicitacaoSuprimento.SC) {
					return this.solicitacaoComprasFacade.verificarPermissoesSolicitacaoCompras(this.obterLoginUsuarioLogado(), gravar);
				} else if (this.getOrigemSolicitacao() == DominioOrigemSolicitacaoSuprimento.SS) {
					return this.solicitacaoServicoFacade.verificarPermissoesSolicitacaoServico(this.obterLoginUsuarioLogado(), gravar);
				} else if (this.getOrigemSolicitacao() == DominioOrigemSolicitacaoSuprimento.PC) {
					return this.pacFacade.verificarPermissoesPac(this.obterLoginUsuarioLogado(), gravar);
				}
			}
		}
		// para o resto passa tudo
		return Boolean.TRUE;
	}
	
	public Boolean mostrarLinkAlteracaoExclusao(ScoArquivoAnexo item) {
		return (item.getTpOrigem() == this.getOrigemSolicitacao());
	}
	
	public String voltar() {
		if(telaOrigem != null){
			if(telaOrigem.equals("SERVICO_CRUD")){
				return ESTOQUE_SERVICO_CRUD;
			}
		}
		return telaOrigem;
	}
	
	public String validarArquivo() {
		return null;
	}

	public String getTelaOrigem() {
		return telaOrigem;
	}

	public void setTelaOrigem(String telaOrigem) {
		this.telaOrigem = telaOrigem;
	}

	public DominioOrigemSolicitacaoSuprimento getOrigemSolicitacao() {
		return origemSolicitacao;
	}

	public void setOrigemSolicitacao(
			DominioOrigemSolicitacaoSuprimento origemSolicitacao) {
		this.origemSolicitacao = origemSolicitacao;
	}

	public Integer getNumeroSolicitacao() {
		return numeroSolicitacao;
	}

	public void setNumeroSolicitacao(Integer numeroSolicitacao) {
		this.numeroSolicitacao = numeroSolicitacao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getNomeZip() {
		return nomeZip;
	}

	public void setNomeZip(String nomeZip) {
		this.nomeZip = nomeZip;
	}

	public String getNomeArquivo() {
		return nomeArquivo;
	}

	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}

	public String getCaminhoAbsolutoZip() {
		return caminhoAbsolutoZip;
	}

	public void setCaminhoAbsolutoZip(String caminhoAbsolutoZip) {
		this.caminhoAbsolutoZip = caminhoAbsolutoZip;
	}

	public List<ScoArquivoAnexo> getListaDocumentosAnexados() {
		return listaDocumentosAnexados;
	}

	public void setListaDocumentosAnexados(
			List<ScoArquivoAnexo> listaDocumentosAnexados) {
		this.listaDocumentosAnexados = listaDocumentosAnexados;
	}

	public ScoArquivoAnexo getDocumentoEmEdicao() {
		return documentoEmEdicao;
	}

	public void setDocumentoEmEdicao(ScoArquivoAnexo documentoEmEdicao) {
		this.documentoEmEdicao = documentoEmEdicao;
	}

	public UploadedFile getUploadedFile() {
		return UploadedFile;
	}

	public void setUploadedFile(UploadedFile UploadedFile) {
		this.UploadedFile = UploadedFile;
	}

	public boolean isEmEdicao() {
		return emEdicao;
	}

	public void setEmEdicao(boolean emEdicao) {
		this.emEdicao = emEdicao;
	}

	public Long getSeqArquivo() {
		return seqArquivo;
	}

	public void setSeqArquivo(Long seqArquivo) {
		this.seqArquivo = seqArquivo;
	}

	public ICascaFacade getICascaFacade() {
		return cascaService;
	}

	public void setICascaFacade(ICascaFacade cascaService) {
		this.cascaService = cascaService;
	}

	public ISolicitacaoServicoFacade getSolicitacaoServicoFacade() {
		return solicitacaoServicoFacade;
	}

	public void setSolicitacaoServicoFacade(ISolicitacaoServicoFacade solicitacaoServicoFacade) {
		this.solicitacaoServicoFacade = solicitacaoServicoFacade;
	}

	public Boolean getModoEdit() {
		return modoEdit;
	}

	public void setModoEdit(Boolean modoEdit) {
		this.modoEdit = modoEdit;
	}

	public byte[] getAnexo() {
		return anexo;
	}

	public void setAnexo(byte[] anexo) {
		this.anexo = anexo;
	}

}
