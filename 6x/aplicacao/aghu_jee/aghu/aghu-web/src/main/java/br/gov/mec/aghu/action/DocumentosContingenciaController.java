package br.gov.mec.aghu.action;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.dominio.DominioMimeType;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.TipoDocumentoImpressao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.model.AelUnidExecUsuario;
import br.gov.mec.aghu.model.AghDocumentoContingencia;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;


public class DocumentosContingenciaController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<RapServidores> dataModel;

	private static final Log LOG = LogFactory.getLog(DocumentosContingenciaController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -8889787940716097214L;


	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private IExamesFacade examesFacade;
	
	@Inject
	private SistemaImpressao sistemaImpressao;

	private String nomeDocumento;

	private DominioMimeType tipoDocumento;

	private RapServidores servidorSelecionado;

	private AghUnidadesFuncionais unidadeExecutora;

	private AelUnidExecUsuario usuarioUnidadeExecutora;

	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}

	public void inicio() {
	 

	 

		

		// Obtem o usuario da unidade executora
		try {
			this.setServidorSelecionado(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()));
			this.usuarioUnidadeExecutora = this.examesFacade.obterUnidExecUsuarioPeloId(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()).getId());
		} catch (ApplicationBusinessException e) {
			usuarioUnidadeExecutora = null;
		}

		// Resgata a unidade executora associada ao usuario
		if (this.usuarioUnidadeExecutora != null) {
			this.unidadeExecutora = this.usuarioUnidadeExecutora.getUnfSeq();
		}

	
	}
	

	@Override
	public List<AghDocumentoContingencia> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return aghuFacade.pesquisarDocumentoContingenciaPorUsuarioNomeTipo(servidorSelecionado.getUsuario(), nomeDocumento, this.getTipoDocumento(), firstResult, maxResult,
				orderProperty, asc);
	}

	@Override
	public Long recuperarCount() {
		return aghuFacade.obterCountDocumentoContingenciaPorUsuarioNomeTipo(servidorSelecionado.getUsuario(), nomeDocumento, this.getTipoDocumento());
	}

	public List<RapServidores> pesquisarUsuarios(String param) {
		return  this.registroColaboradorFacade.pesquisarRapServidoresPorCodigoDescricao(param);
	}

	public void downloadRelatorio(AghDocumentoContingencia documento) {
		final FacesContext fc = FacesContext.getCurrentInstance();
		final HttpServletResponse response = (HttpServletResponse) fc.getExternalContext().getResponse();

		response.setContentType(documento.getFormato().getContentType());
		response.setHeader("Content-Disposition", "attachment;filename=" + documento.getNome());

		OutputStream out;
		try {
			out = response.getOutputStream();

			out.write(documento.getArquivo());
			out.flush();
			out.close();

		} catch (IOException e) {
			LOG.error("Exceção capturada: ", e);
			this.apresentarMsgNegocio("ERRO_DOWNLOAD_RELATORIO");			
		}
		fc.responseComplete();

	}

	/**
	 * Verifica se o tipo de MIME do documento de contingência é TXT
	 * 
	 * @param documentoContingencia
	 * @return
	 */
	public boolean verificarDocumentoContingenciaTxt(AghDocumentoContingencia documentoContingencia) {
		return DominioMimeType.TXT.equals(documentoContingencia.getFormato());
	}

	/**
	 * Imprime cópia de de segurança de etiquetas
	 * 
	 * @param documentoContingencia
	 */
	public void imprimirCopiaSegurancaEtiquetas(AghDocumentoContingencia documentoContingencia) {
		try {
			byte[] bytes = documentoContingencia.getArquivo();
			this.sistemaImpressao.imprimir(new String(bytes), this.unidadeExecutora, TipoDocumentoImpressao.ETIQUETAS_BARRAS);
//			this.solicitacaoExameFacade.imprimirCopiaSegurancaEtiquetas(documentoContingencia, this.unidadeExecutora, null);
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_COPIA_CONTINGENCIA_IMPRESSAS_SUCESSO");
		} catch (final BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public String getNomeDocumento() {
		return nomeDocumento;
	}

	public void setNomeDocumento(String nomeDocumento) {
		this.nomeDocumento = nomeDocumento;
	}



	public DominioMimeType getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(DominioMimeType tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public RapServidores getServidorSelecionado() {
		return servidorSelecionado;
	}

	public void setServidorSelecionado(RapServidores servidorSelecionado) {
		this.servidorSelecionado = servidorSelecionado;
	}

	public AghUnidadesFuncionais getUnidadeExecutora() {
		return unidadeExecutora;
	}

	public void setUnidadeExecutora(AghUnidadesFuncionais unidadeExecutora) {
		this.unidadeExecutora = unidadeExecutora;
	}

	public AelUnidExecUsuario getUsuarioUnidadeExecutora() {
		return usuarioUnidadeExecutora;
	}

	public void setUsuarioUnidadeExecutora(AelUnidExecUsuario usuarioUnidadeExecutora) {
		this.usuarioUnidadeExecutora = usuarioUnidadeExecutora;
	}
 


	public DynamicDataModel<RapServidores> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<RapServidores> dataModel) {
	 this.dataModel = dataModel;
	}
}
