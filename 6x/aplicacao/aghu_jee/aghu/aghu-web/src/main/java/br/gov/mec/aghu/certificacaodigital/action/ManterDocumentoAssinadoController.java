package br.gov.mec.aghu.certificacaodigital.action;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.persistence.PersistenceException;

import br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade;
import br.gov.mec.aghu.dominio.DominioIdentificadorDocumentoAssinado;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoDocumento;
import br.gov.mec.aghu.model.AghDocumentoCertificado;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterDocumentoAssinadoController extends ActionController{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2714576648083623143L;

	private static final String PAGE_MANTER_DOCUMENTOS_ASSINADOS_PESQUISA = "manterDocumentosAssinadosPesquisa";
	private static final String PAGE_MANTER_DOCUMENTOS_ASSINADOS_CRUD = "manterDocumentosAssinadosCRUD";

	@EJB
	private ICertificacaoDigitalFacade certificacaoDigitalFacade;
	
	//Variáveis para controle de edição
	private Integer codigo;
	private AghDocumentoCertificado documento;

	public DominioIdentificadorDocumentoAssinado[] getIdentificadorDocumentoAssinadoItens(){
		return DominioIdentificadorDocumentoAssinado.values();
	}
	
	public DominioTipoDocumento[] getTipoDocumentoItens(){
		return DominioTipoDocumento.values();
	}
	
	public DominioSituacao[] getAtivoInativoItens(){
		return DominioSituacao.values();
	}
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public String iniciarInclusao() {
		codigo = null;
		documento = new AghDocumentoCertificado();
		documento.setIndSituacao(DominioSituacao.A);
		return PAGE_MANTER_DOCUMENTOS_ASSINADOS_CRUD;
	}
	
	/**
	 * Método chamado ao carregar a tela de cadastro de documentos
	 */
	public void iniciar() {
		if(codigo != null) {
			documento = certificacaoDigitalFacade.obterAghDocumentoCertificadoPeloId(codigo);
		} else {
			resetarDocumento();
		}
	}
	
	private void resetarDocumento() {
		documento = new AghDocumentoCertificado();
		documento.setIndSituacao(DominioSituacao.A);
	}
	
	/**
	 * Método que realiza a ação do botão limpar
	 */
	public void limpar() {
		resetarDocumento();
	}
	
	/**
	 * Método que realiza a ação do botão gravar
	 */
	public String gravar() {
		//Reinicia o paginator
		//reiniciarPaginator(ManterDocumentoAssinadoPaginatorController.class);
		
		//Verifica se a ação é de criação ou edição
		boolean criando = documento.getSeq() == null;
		
		try {
			
			documento.setDthrEdicao(new Date());
			
			//Submete o documento para ser persistido
			certificacaoDigitalFacade.persistirAghDocumentoCertificado(documento);
			
			//Realiza o flush
			//certificacaoDigitalFacade.flush();
			
			//Apresenta as mensagens de acordo
			if(criando) {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_DOCUMENTO_ASSINADO", documento.getNome());
			} else {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_DOCUMENTO_ASSINADO", documento.getNome());
			}
		
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		} catch(PersistenceException e) {
			//getLog().error("Exceção capturada: ", e);
			if(criando) {
				apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_CRIACAO_DOCUMENTO_ASSINADO", documento.getNome());
			} else {
				apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_EDICAO_DOCUMENTO_ASSINADO", documento.getNome());
			}
			return null;
		}
		
		return PAGE_MANTER_DOCUMENTOS_ASSINADOS_PESQUISA;
	}
	
	/**
	 * Método que realiza a ação do botão cancelar
	 */
	public String cancelar() {
		documento = new AghDocumentoCertificado();
		codigo = null;
		return PAGE_MANTER_DOCUMENTOS_ASSINADOS_PESQUISA;
	}
	
	//Gets e Sets
	public Integer getCodigo() {
		return codigo;
	}
	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}
	public AghDocumentoCertificado getDocumento() {
		return documento;
	}
	public void setDocumento(AghDocumentoCertificado documento) {
		this.documento = documento;
	}
}
