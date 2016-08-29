package br.gov.mec.aghu.exames.action;

import java.net.UnknownHostException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.exames.business.IExamesBeanFacade;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.action.IdentificarUnidadeExecutoraController;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.exames.vo.AelItemSolicConsultadoVO;
import br.gov.mec.aghu.model.AelDocResultadoExame;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelUnidExecUsuario;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;




public class CarregarArquivoLaudoResultadoExameController extends ActionController  {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(CarregarArquivoLaudoResultadoExameController.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7923279391832066275L;

	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;
	
	@EJB
	private IExamesBeanFacade examesBeanFacade;

	@Inject
	private IdentificarUnidadeExecutoraController unidadeExecutoraController; 

	// Campos de filtro para pesquisa
	private AghUnidadesFuncionais unidadeExecutora;
	private Integer solicitacaoSeq;
	private Short amostraSeqp;
	private Integer soeSeqExclusao;
	private Short seqpExclusao;
	private Short amoSeqpExclusao;

	// Instancias para resultados da pesquisa
	private AelUnidExecUsuario usuarioUnidadeExecutora;
	private List<AelItemSolicitacaoExames> listaItemSolicitacaoExames;
	private List<AelItemSolicConsultadoVO> listaItemSolicConsultado;
	private String nomePasciente;
	private String unidadeSoliciante;
	private String nomeSolicitante;
	
	// Instancias pesquisa de servidores responsaveis pela liberacao
	private RapServidores servidor;
	private Integer matricula;
	private Short vinCodigo;
	private String servidorDesc;
	
	// Intancia de arquivo/anexo de upload
	private UploadedFile UploadedFile;
	private String tiposArquivosAceitos = "pdf"; // Tipos de arquivos aceitos no upload de arquivos

	/**
	 * Chamado no inicio de cada conversacao
	 */
	public void inicio() {

		// Obtem o USUARIO da unidade executora
		try {
			this.usuarioUnidadeExecutora = this.examesFacade.obterUnidExecUsuarioPeloId(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()).getId());
		} catch (ApplicationBusinessException e) {
			LOG.error("Não encontrou o servidor logado!",e);
		}
		
		// Reseta todos os filtros
		this.limparPesquisa();

		// Resgata a unidade executora associada ao usuario
		if(this.usuarioUnidadeExecutora != null){
			this.unidadeExecutora =  this.usuarioUnidadeExecutora.getUnfSeq();
		}
		
	}
	
	
	/**
	 * Persiste identificacao da unidade executora atraves do usuario logado
	 */
	public void persistirIdentificacaoUnidadeExecutora(){
		try {
			
			// Persiste identificacao da unidade executora do usuario
			this.cadastrosApoioExamesFacade.persistirIdentificacaoUnidadeExecutora(this.usuarioUnidadeExecutora, this.unidadeExecutora);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	/**
	 * Pesquisa solicitacoes de exame e seus respectivos itens
	 */
	public void pesquisar() {
		
		this.listaItemSolicitacaoExames = null;
		
		// Limpa instancias relacionados ao servidor responsavel  
		//this.servidor = null;
		//this.servidorDesc = null;
		//this.matricula = null;
		//this.vinCodigo = null;

		if(this.unidadeExecutora == null){
			this.apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_CAMPO_OBRIGATORIO_UNIDADE_EXECUTORA_ARQUIVO_LAUDO");
			return;
		}
		
		if(this.solicitacaoSeq == null){
			this.apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_CAMPO_OBRIGATORIO_SOLICITACAO_ARQUIVO_LAUDO");
			return;
		}

		// Resgata a solicitacao de exame através da id
		AelSolicitacaoExames solicitacaoExame = this.examesFacade.obterAelSolicitacaoExamesPeloId(this.solicitacaoSeq);

		 // Caso a solicitacao de exame exista realiza a consulta de itens de solicitacao de exame
		if(solicitacaoExame != null){
			try {
				
				// Resgata nome do paciente atraves da solicitacao de exame
				if(solicitacaoExame.getAtendimento() != null){
					this.nomePasciente = solicitacaoExame.getAtendimento().getPaciente().getNome();	
				}
	
				// Resgata o nome da unidade solicitante atraves da view VAghUnidFuncional
				this.unidadeSoliciante = this.examesFacade.obterDescricaoVAghUnidFuncional(solicitacaoExame.getUnidadeFuncional().getSeq());
				
				// Resgata nome do solicitante atraves da solicitacao de exame
				if(solicitacaoExame.getServidorResponsabilidade() != null){
					this.nomeSolicitante = solicitacaoExame.getServidorResponsabilidade().getPessoaFisica().getNome();
				}
				
				// Pesquisa itens de solicitacao de exame
				this.listaItemSolicitacaoExames = this.examesFacade.pesquisarCarregarArquivoLaudoResultadoExame(this.unidadeExecutora, solicitacaoExame, this.amostraSeqp);
				
				if(this.listaItemSolicitacaoExames == null  || this.listaItemSolicitacaoExames.isEmpty()){
					this.apresentarMsgNegocio(Severity.WARN,"MENSAGEM_NENHUM_ITEM_SOLICITACAO_EXAME_ARQUIVO_LAUDO");
				}
	
			} catch (BaseException e) {
				super.apresentarExcecaoNegocio(e);	
			}
		} else{
			this.apresentarMsgNegocio(Severity.WARN,"MENSAGEM_NENHUMA_SOLICITACAO_EXAME_ARQUIVO_LAUDO");
		}
	}

	/**
	 * Verifica a existencia de arquivo de laudo
	 * Existencia: Exibe os icones de DOWNLOAD e REMOCAO de arquivo na listagem.
	 * Inexistencia: Exibe o icone de UPLOAD/ADICAO de arquivo!
	 */
	public boolean existeDocumentoAnexado(Integer iseSoeSeq, Short iseSeqp)  {
		return this.examesFacade.existeDocumentoAnexado(iseSoeSeq, iseSeqp);	
	}

	/**
	 * Download do arquivo de laudo
	 * @param iseSoeSeq
	 * @param iseSeqp
	 * @return
	 */
	public String downloadArquivoLaudo(Integer iseSoeSeq, Short iseSeqp)  {
		
		// Resgata instancia de AelDocResultadoExame
		AelDocResultadoExame doc = this.examesFacade.obterDocumentoAnexado(iseSoeSeq, iseSeqp);
		
		// Inicia download via uma HTTP Response
		return this.downloadViaHttpServletResponse(iseSoeSeq, iseSeqp, doc.getDocumento());
	}
	
	/**
	 * Download via uma Http Response
	 * 
	 * @param dados array de bytes (stream) do arquivo de download
	 * @return
	 */
	private String downloadViaHttpServletResponse(Integer iseSoeSeq, Short iseSeqp, byte[] dados) {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		
		// Instancia uma HTTP Response
		HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();

		// Seta o tipo de MIME
		response.setContentType("application/octet-stream");

		// Obtém o nome do arquivo em anexo: SOE_SEQ + SEQP com 3 zeros à esquerda + extensão
		AelItemSolicitacaoExames itemSolicitacao = this.examesFacade.buscaItemSolicitacaoExamePorId(iseSoeSeq, iseSeqp);
		String nomeArquivo = this.examesFacade.extrairNomeExtensaoDocumentoLaudoAnexo(itemSolicitacao);

		// Seta o arquivo no cabeçalho
		response.addHeader("Content-disposition", "attachment; filename=\"" + nomeArquivo + "\"");

		// Escreve a resposta no HTTP RESPONSE
		ServletOutputStream os = null;
		try {
			os = response.getOutputStream();
			os.write(dados); // Escrevemos o STREAM de resposta/RESPONSE
			os.flush();
			facesContext.responseComplete();
		} catch (Exception e) {
			LOG.error(e.getMessage());
		} finally {
			IOUtils.closeQuietly(os);
		}
		return null;
	}
	
	/**
	 * Exibe mensagem de erro para tipo de arquivo rejeitado durante o upload
	 */
	public void exibirMensagemUploadTipoArquivoRejeitado(){
		this.apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_TIPO_ARQUIVO_REJEITADO",this.tiposArquivosAceitos);
	}

	/**
	 * Listener de upload para o arquivo de laudo
	 * @param event
	 */
	public void listenerUploadArquivoLaudo(FileUploadEvent event) {
		this.UploadedFile = event.getFile();
	}
	
	/**
	 * Insere arquivo de laudo do upload
	 * @param anexo
	 */
	public void inserirUploadArquivoLaudo(AelItemSolicitacaoExames itemSolicitacaoExames)  {
		
		final Integer tamanhoMaximoUpload = this.examesFacade.obterTamanhoMaximoBytesUploadLaudo();
	
		// Valida o tamanho do arquivo enviado 
		if(this.UploadedFile != null && this.UploadedFile.getSize() < tamanhoMaximoUpload){

			// Instancia um documento de resultado de exames
			AelDocResultadoExame doc = new AelDocResultadoExame();

			doc.setItemSolicitacaoExame(itemSolicitacaoExames);
			
			// Seta anulação como FALSA
			doc.setIndAnulacaoDoc(false);

			// Converte arquivo em um array de Bytes
			//byte[] blobDocumentoLaudo = CoreUtil.serializarArquivo(CoreUtil.converterFileToArrayBytes(this.UploadedFile.getFile()));
			
			// Seta BLOB no registro de documento de resultado de exames
			//doc.setDocumento(blobDocumentoLaudo);
			
			// Seta o responsável pela liberação
			doc.getItemSolicitacaoExame().setServidorResponsabilidade(this.servidor);
			
			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e) {
				LOG.error("Exceção caputada:", e);
			}
			
			try {
				
				this.examesBeanFacade.inserirAelDocResultadoExame(doc, unidadeExecutora, nomeMicrocomputador);
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_UPLOAD_ARQUIVO_LAUDO", UploadedFile.getFileName());
			} catch (BaseException e) {
				super.apresentarExcecaoNegocio(e);
			}
		} else{
			this.apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_TAMANHO_ARQUIVO_REJEITADO", this.UploadedFile.getFileName(), tamanhoMaximoUpload);
		}
	}
	
	
	/**
	 * Excluir (Atualiza status) do arquivo de laudo enviado 
	 */
	public void removerArquivoLaudo()  {

		try {
			
			final AelDocResultadoExame doc = this.examesFacade.obterDocumentoAnexado(this.soeSeqExclusao, this.seqpExclusao);
			
			if (doc != null) {
	
				String nomeMicrocomputador = null;
				try {
					nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
				} catch (UnknownHostException e) {
					LOG.error("Exceção caputada:", e);
				}
				
				this.examesBeanFacade.removerAelDocResultadoExame(doc, this.unidadeExecutora, nomeMicrocomputador);
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOVER_ARQUIVO_LAUDO");
			}
			
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);			
		} finally {
			// Reseta parametros de exclusao
			this.soeSeqExclusao = null;
			this.seqpExclusao = null;
			this.amoSeqpExclusao = null;
			this.listaItemSolicConsultado = null;
		}

	}
	
	/**
	 * Pesquisa exames consultados durante a exclusao do arquivo (atualizacao de status) de laudo enviado
	 * @return
	 */
	public String pesquisaExamesConsultados()  {
		if(this.soeSeqExclusao != null && this.seqpExclusao != null){
			this.listaItemSolicConsultado = this.examesFacade.pesquisarAelItemSolicConsultadosResultadosExames(this.soeSeqExclusao, this.seqpExclusao);
		}
		return null;
	}
	
	public Boolean verificaExamesConsultados(Integer iseSoeSeq, Short iseSeqp) {
		List<AelItemSolicConsultadoVO> consultado = this.examesFacade.pesquisarAelItemSolicConsultadosResultadosExames(iseSoeSeq, iseSeqp);
		return (consultado != null && !consultado.isEmpty());
	}
	
	/*
	 * Metodos para Suggestion Box
	 */
	
	/**
	 * Metodo para pesquisa na suggestion box de unidade executora
	 */
	public List<AghUnidadesFuncionais> obterAghUnidadesFuncionaisExecutoras(Object objPesquisa){
		return this.aghuFacade.pesquisarUnidadesExecutorasPorCodigoOuDescricao(objPesquisa);
	}	

	/**
	 * Limpa os filtros da consulta
	 */
	public void limparPesquisa() {
		this.listaItemSolicitacaoExames = null;
		this.listaItemSolicConsultado = null;
		// Resgata a unidade executora associada ao usuario
		this.unidadeExecutora =  this.unidadeExecutoraController.getUnidadeExecutora();
		this.amostraSeqp = null;
		this.solicitacaoSeq = null;
		this.nomePasciente = null;
		this.unidadeSoliciante = null;
		this.nomeSolicitante = null;
		this.servidor = null;
		this.servidorDesc = null;
		this.matricula = null;
		this.vinCodigo = null;
		// Limpa instancia de upload
		UploadedFile = null;
	}
	
	/**
	 * Metodos para pesquisa de servidores responsaveis pela liberacao
	 */
	
	/**
	 * Escolher servidor responsavel pela liberacao
	 */
	public void escolherServidor() {
		if (this.matricula != null && this.vinCodigo != null) {

			/*Busca as permissões do servidor informado*/
			RapServidores servidorPermissao = this.registroColaboradorFacade.pesquisarServidorLiberaExames(matricula, vinCodigo);
			RapServidores servidor = this.registroColaboradorFacade.buscaServidor(new RapServidoresId(matricula, vinCodigo));

			if(servidor == null){
				this.apresentarMsgNegocio(Severity.ERROR, "MSG_VINCULO_MATRICULA_NAO_EXISTE");
				this.atribuirServidor(null);

			}else if(servidorPermissao==null){
				this.apresentarMsgNegocio(Severity.ERROR, "MSG_VINCULO_MATRICULA_NAO_HABILITADO");

			}else{
				this.atribuirServidor(servidorPermissao);
			}
		}
	}
	/**
	 * Atribuir servidor responsavel pela liberacao
	 * @param servidor
	 */
	public void atribuirServidor(RapServidores servidor) {
		if (servidor != null) {
			setServidor(servidor);
			setServidorDesc(servidor.getPessoaFisica().getNome());
			setMatricula(servidor.getId().getMatricula());
			setVinCodigo(servidor.getId().getVinCodigo());
		} else {
			setServidor(null);
			setServidorDesc(null);
			setMatricula(null);
			setVinCodigo(null);
		}
	}
	
	/**
	 * Meodo para Suggestion Box de servidorresponsavel pela liberacao
	 * @param object
	 * @return
	 */
	public List<RapServidores> obterServidor(Object paramPesquisa){
		return this.registroColaboradorFacade.pesquisarServidorLiberaExames(paramPesquisa);
	}
	
	/*
	 * Getters e Setters
	 */
	
	public AghUnidadesFuncionais getUnidadeExecutora() {
		return unidadeExecutora;
	}

	public void setUnidadeExecutora(AghUnidadesFuncionais unidadeExecutora) {
		this.unidadeExecutora = unidadeExecutora;
	}

	public Integer getSolicitacaoSeq() {
		return solicitacaoSeq;
	}
	
	public void setSolicitacaoSeq(Integer solicitacaoSeq) {
		this.solicitacaoSeq = solicitacaoSeq;
	}

	public Short getAmostraSeqp() {
		return amostraSeqp;
	}

	public void setAmostraSeqp(Short amostraSeqp) {
		this.amostraSeqp = amostraSeqp;
	}

	public String getNomePasciente() {
		return nomePasciente;
	}

	public void setNomePasciente(String nomePasciente) {
		this.nomePasciente = nomePasciente;
	}

	public String getUnidadeSoliciante() {
		return unidadeSoliciante;
	}

	public void setUnidadeSoliciante(String unidadeSoliciante) {
		this.unidadeSoliciante = unidadeSoliciante;
	}

	public String getNomeSolicitante() {
		return nomeSolicitante;
	}
	
	public void setNomeSolicitante(String nomeSolicitante) {
		this.nomeSolicitante = nomeSolicitante;
	}
	
	public List<AelItemSolicitacaoExames> getListaItemSolicitacaoExames() {
		return listaItemSolicitacaoExames;
	}
	
	public void setListaItemSolicitacaoExames(List<AelItemSolicitacaoExames> listaItemSolicitacaoExames) {
		this.listaItemSolicitacaoExames = listaItemSolicitacaoExames;
	}
	
	public UploadedFile getUploadedFile() {
		return UploadedFile;
	}
	
	public void setUploadedFile(UploadedFile UploadedFile) {
		this.UploadedFile = UploadedFile;
	}

	public List<AelItemSolicConsultadoVO> getListaItemSolicConsultado() {
		return listaItemSolicConsultado;
	}
	
	public void setListaItemSolicConsultado(List<AelItemSolicConsultadoVO> listaItemSolicConsultado) {
		this.listaItemSolicConsultado = listaItemSolicConsultado;
	}

	public AelUnidExecUsuario getUsuarioUnidadeExecutora() {
		return usuarioUnidadeExecutora;
	}
	
	public void setUnidadeExecutoraController(IdentificarUnidadeExecutoraController unidadeExecutoraController) {
		this.unidadeExecutoraController = unidadeExecutoraController;
	}

	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}


	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}


	public Short getVinCodigo() {
		return vinCodigo;
	}


	public void setVinCodigo(Short vinCodigo) {
		this.vinCodigo = vinCodigo;
	}


	public String getServidorDesc() {
		return servidorDesc;
	}


	public void setServidorDesc(String servidorDesc) {
		this.servidorDesc = servidorDesc;
	}

	public String getTiposArquivosAceitos() {
		return tiposArquivosAceitos;
	}
	
	public void setTiposArquivosAceitos(String tiposArquivosAceitos) {
		this.tiposArquivosAceitos = tiposArquivosAceitos;
	}
	
	public Integer getSoeSeqExclusao() {
		return soeSeqExclusao;
	}
	
	public void setSoeSeqExclusao(Integer soeSeqExclusao) {
		this.soeSeqExclusao = soeSeqExclusao;
	}
	
	public Short getSeqpExclusao() {
		return seqpExclusao;
	}
	
	public void setSeqpExclusao(Short seqpExclusao) {
		this.seqpExclusao = seqpExclusao;
	}
	
	public Short getAmoSeqpExclusao() {
		return amoSeqpExclusao;
	}
	
	public void setAmoSeqpExclusao(Short amoSeqpExclusao) {
		this.amoSeqpExclusao = amoSeqpExclusao;
	}
	
}
