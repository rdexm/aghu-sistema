package br.gov.mec.aghu.certificacaodigital.action;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade;
import br.gov.mec.aghu.certificacaodigital.vo.DocumentosPendentesVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.model.AghVersaoDocumento;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;

public class ListarPendenciasAssinaturaPaginatorController extends
		ActionController implements ActionPaginator {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final String PAGE_PESQUISAR_PACIENTES_AGENDADOS = "ambulatorio-pesquisarPacientesAgendados";

	private static final String PAGE_PESQUISAR_PACIENTES_INTERNADOS = "prescricaomedica-pesquisarListaPacientesInternados";

	@Inject
	@Paginator
	private DynamicDataModel<DocumentosPendentesVO> dataModel;
	private static final long serialVersionUID = 7970766099924249927L;

	@EJB
	private ICertificacaoDigitalFacade certificacaoDigitalFacade;

	@EJB
	private IPacienteFacade pacienteFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	private RapServidores responsavel;
	private AipPacientes paciente;
	private Integer pacCodigo;
	private Integer vinCodigo;
	private Integer matricula;
	private String tipo;

	private List<DocumentosPendentesVO> documentosPendentesVO;

	private Boolean todosDocumentosSelecionados;
	private Boolean clicouCheckboxTodos = false;

	private boolean inicializado;
	private String voltarPara;

	private String tituloVisao = "";
	private String contextPath;
	private Integer seqAghVersaoDocumento;

	private String selectedTab;
	private final String TAB_LISTA_PENDENCIAS = "0";
	private final String TAB_DOCUMENTO_ORIGINAL = "1";

	public static final String CONTENT_TYPE = "application/pdf;";

	private enum ListarPendenciasAssinaturaPaginatorExceptionCode implements
			BusinessExceptionCode {
		MENSAGEM_RESPONSAVEL_NULO, MENSAGEM_SEQ_AGH_VERSAO_DOCUMENTO_NULO;
	}

	@SuppressWarnings("PMD.NPathComplexity")
	public void iniciar() {

		String s = (String) FacesContext.getCurrentInstance()
				.getExternalContext().getRequestParameterMap()
				.get("centralPendencia");

		setContextPath(FacesContext.getCurrentInstance().getExternalContext()
				.getRequestContextPath());

		if (Boolean.valueOf(s)) {
			RapServidores rap;
			try {
				rap = registroColaboradorFacade
						.obterServidorPorUsuario(obterLoginUsuarioLogado());
				vinCodigo = rap.getId().getVinCodigo().intValue();
				matricula = rap.getId().getMatricula().intValue();
			} catch (Exception e) {
				rap = null;
			}

			// Resgate do TIPO: Necessário quando a pendência é gerada através
			// da pesquisa de pacientes agendados de AMBULATÓRIO
			if (PAGE_PESQUISAR_PACIENTES_AGENDADOS
					.equalsIgnoreCase(this.voltarPara)) {
				Long count = this.certificacaoDigitalFacade
						.listarPendentesResponsavelPacienteCount(rap, paciente);
				if (count != null && count.intValue() > 0) {
					this.setTipo("1");
				} else {
					this.setTipo("3");
				}
			}
		}

		if (matricula != null && vinCodigo != null
				&& (this.responsavel == null)) {
			RapServidoresId servidorId = new RapServidoresId(matricula,
					vinCodigo.shortValue());
			this.responsavel = this.registroColaboradorFacade
					.buscaServidor(servidorId);
		}

		if (pacCodigo != null) {
			this.paciente = new AipPacientes();
			this.paciente = this.pacienteFacade.buscaPaciente(pacCodigo);

			if (this.responsavel == null) {
				try {
					this.responsavel = registroColaboradorFacade
							.obterServidorPorUsuario(obterLoginUsuarioLogado());
				} catch (Exception e) {
					this.responsavel = null;
				}
			}
			// vem da lista de pacientes do ambulatorio
			if (this.voltarPara != null
					&& (!this.voltarPara.equals("pesquisarPacientesAgendados"))
					&& (!this.voltarPara
							.equals(PAGE_PESQUISAR_PACIENTES_INTERNADOS))) { // vem
																				// da
																				// lista
																				// de
																				// pacientes
																				// da
																				// prescricao
				this.definirTipo();
			}
			
			inicializado = true;
		}

		if ((tipo != null)
				&& ((tipo.equals("1")) || (tipo.equals("2")) || (tipo
						.equals("")))) {

			if (this.responsavel == null) {
				try {
					this.responsavel = registroColaboradorFacade
							.obterServidorPorUsuario(obterLoginUsuarioLogado());
				} catch (Exception e) {
					this.responsavel = null;
				}
			}

			if (inicializado) {
				recuperarListaPaginada(0, 100, null, true);
			} else {
				dataModel.reiniciarPaginator();
			}
		}

		if (this.responsavel != null) {
			this.setVinCodigo(this.responsavel.getVinculo().getCodigo()
					.intValue());
		}

		//if (s != null && s.equals("true")) {
			this.pesquisar();
		//}

		this.mostrarVisao();
	}
	
	public void onTabChange(TabChangeEvent event) {
		
		if (event.getTab().getId().equals("tab1")) {
		  this.limparDocumentoOriginal();
		}
	}

	@Override
	public Long recuperarCount() {

		Long count = null;

		if (this.responsavel != null) {

			if (this.tipo == null || (tipo.equals(""))) { // samis
				count = this.certificacaoDigitalFacade
						.listarPendentesResponsavelCount(this.responsavel);
			} else if (this.tipo.equals("1")) { // meus documentos do paciente
				count = this.certificacaoDigitalFacade
						.listarPendentesResponsavelPacienteCount(
								this.responsavel, this.paciente);

			} else if (this.tipo.equals("2")) { // meus documentos
				count = (this.certificacaoDigitalFacade.listarPendentesCount(
						this.responsavel, this.paciente) + this.certificacaoDigitalFacade
						.listarPendentesResponsavelPacienteCount(
								this.responsavel, this.paciente));

			} else if (this.tipo.equals("3")) { // documentos do paciente
				count = (this.certificacaoDigitalFacade
						.listarPendentesPacienteCount(this.responsavel,
								this.paciente) + this.certificacaoDigitalFacade
						.listarPendentesResponsavelPacienteCount(
								this.responsavel, this.paciente));
			}
		}

		return count;
	}

	@Override
	public List<DocumentosPendentesVO> recuperarListaPaginada(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {
		
		documentosPendentesVO = new ArrayList<DocumentosPendentesVO>();
		
		if(this.responsavel != null){
			
			if (this.getMatricula() != null && this.responsavel == null) {
				RapServidoresId servidorId = new RapServidoresId(this.matricula,
						vinCodigo.shortValue());

				this.responsavel = this.registroColaboradorFacade
						.buscaServidor(servidorId);
			}

			if (pacCodigo != null && this.paciente == null) {
				this.paciente = new AipPacientes();
				this.paciente = this.pacienteFacade.buscaPaciente(pacCodigo);
				this.definirTipo();
			}

			if (this.tipo == null || (tipo.equals(""))) { // samis
				documentosPendentesVO = this.certificacaoDigitalFacade
						.pesquisarPendentesResponsavel(firstResult, maxResult,
								orderProperty, asc, this.responsavel);

			} else if (this.tipo.equals("1")) { // meus documentos do paciente
				documentosPendentesVO = this.certificacaoDigitalFacade
						.pesquisarPendentesResponsavelPaciente(firstResult,
								maxResult, orderProperty, asc, this.responsavel,
								this.paciente);

			} else if (this.tipo.equals("2")) { // meus documentos
				documentosPendentesVO = this.certificacaoDigitalFacade
						.pesquisarPendentesePendentesResponsavelPaciente(
								firstResult, maxResult, orderProperty, asc,
								this.responsavel, this.paciente);

			} else if (this.tipo.equals("3")) { // documentos do paciente
				documentosPendentesVO = this.certificacaoDigitalFacade
						.pesquisarPendentesPacienteePendentesResponsavelPaciente(
								firstResult, maxResult, orderProperty, asc,
								this.responsavel, this.paciente);
			}

			if (documentosPendentesVO == null) {
				documentosPendentesVO = new ArrayList<DocumentosPendentesVO>();
			}

			for (DocumentosPendentesVO doc : this.documentosPendentesVO) {
				doc.setSelecionado(this.getClicouCheckboxTodos());
			}

		}
		return documentosPendentesVO;
		
	}

	/**
	 * 
	 * Define a variável de classe "tipo", responsável pela atualização dos
	 * radiobuttons da tela de pesquisa de pendências, situando o usuário na
	 * opção correta de pesquisa.
	 * 
	 */
	public void definirTipo() {

		if (this.matricula != null && this.tipo != null) {

			if (this.vinCodigo != null) {

				RapServidoresId id = new RapServidoresId(this.matricula,
						this.vinCodigo.shortValue());
				this.responsavel = this.registroColaboradorFacade
						.buscaServidor(id);
			}
		}

		if (pacCodigo != null) {
			this.paciente = new AipPacientes();
			this.paciente = this.pacienteFacade.buscaPaciente(pacCodigo);
		}

		if (this.responsavel != null && this.paciente != null) {

			if (this.certificacaoDigitalFacade
					.pesquisarPendentesResponsavelPaciente(0, 1, null, false,
							this.responsavel, this.paciente) != null
					&& !this.certificacaoDigitalFacade
							.pesquisarPendentesResponsavelPaciente(0, 1, null,
									false, this.responsavel, this.paciente)
							.isEmpty()) {

				this.tipo = "1";

			} else {

				if (this.certificacaoDigitalFacade.pesquisarPendentes(0, 1,
						null, false, this.responsavel, this.paciente) != null
						&& !this.certificacaoDigitalFacade
								.pesquisarPendentesePendentesResponsavelPaciente(
										0, 1, null, false, this.responsavel,
										this.paciente).isEmpty()) {

					this.tipo = "2";

				} else {

					if (this.certificacaoDigitalFacade
							.pesquisarPendentesPaciente(0, 1, null, false,
									this.responsavel, this.paciente) != null
							&& !this.certificacaoDigitalFacade
									.pesquisarPendentesPacienteePendentesResponsavelPaciente(
											0, 1, null, false,
											this.responsavel, this.paciente)
									.isEmpty()) {

						this.tipo = "2";
					} else {

						if (this.certificacaoDigitalFacade
								.pesquisarPendentesPaciente(0, 1, null, false,
										this.responsavel, this.paciente) != null
								&& !this.certificacaoDigitalFacade
										.pesquisarPendentesPacienteePendentesResponsavelPaciente(
												0, 1, null, false,
												this.responsavel, this.paciente)
										.isEmpty()) {

							this.tipo = "2";
						}
					}
				}
			}
		}
	}

	/**
	 * Método da suggestion box para pesquisa de servidores
	 * 
	 * @param parametro
	 * @return
	 * @throws BaseException
	 */
	public List<RapServidores> pesquisarServidores(Object parametro)
			throws BaseException {
		List<RapServidores> listResult = new ArrayList<RapServidores>();

		if (CoreUtil.isNumeroInteger(parametro.toString())) {
			this.matricula = Integer.valueOf(parametro.toString());
		}

		listResult = this.registroColaboradorFacade
				.pesquisarServidor(parametro);
		return listResult;
	}

	/**
	 * 
	 * Método da suggestion box para pesquisa de servidores com certificação
	 * digital
	 * 
	 * @param parametro
	 * @return
	 * @throws BaseException
	 */
	public List<RapServidores> pesquisarServidorComCertificacaoDigital(
			String parametro) throws BaseException {

		if (CoreUtil.isNumeroInteger(parametro)) {
			this.matricula = Integer.valueOf(parametro);
		}

		return this.returnSGWithCount(this.certificacaoDigitalFacade
				.pesquisarServidorComCertificacaoDigital(parametro), this
				.pesquisarServidorComCertificacaoDigitalCount(parametro));
		
		/*return this.returnSGWithCount(this.certificacaoDigitalFacade
				.pesquisarServidorComCertificacaoDigital(parametro), this
				.recuperarCount());*/
	}

	public Long pesquisarServidorComCertificacaoDigitalCount(String parametro) {
		return this.certificacaoDigitalFacade
				.pesquisarServidorComCertificacaoDigitalCount(parametro);
	}

	public void limparDadosServidor() {

		this.responsavel = null;
		this.tituloVisao="";
		this.todosDocumentosSelecionados = Boolean.FALSE;
		this.setVinCodigo(null);
		this.setSeqAghVersaoDocumento(null); 
		this.setSelectedTab(TAB_LISTA_PENDENCIAS);
		this.getDataModel().limparPesquisa();
		this.getDataModel().setPesquisaAtiva(false);
	}

	/**
	 * Setar o servidor selecionado pelo usuário
	 */
	public void selecionouServidor() {
		this.setVinCodigo(this.responsavel.getVinculo().getCodigo().intValue());
		this.definirTipo();
		this.mostrarVisao();
		this.dataModel.reiniciarPaginator();
	}

	/**
	 * Método para limpar o formulário da tela.
	 */
	public void limparCampos() {
		this.todosDocumentosSelecionados = Boolean.FALSE;
		this.setResponsavel(null);
		this.setVinCodigo(null);
		this.setDocumentosPendentesVO(null);
		this.setPaciente(null);
		this.setInicializado(false);
		if (this.getVoltarPara() != null
				&& !this.getVoltarPara().equals(
						PAGE_PESQUISAR_PACIENTES_INTERNADOS)) {
			this.setPacCodigo(null);
		}
		this.pacCodigo = null;
		this.tipo = null;
		this.getDataModel().limparPesquisa();
		this.getDataModel().setPesquisaAtiva(false);
		this.tituloVisao = "";
		this.setSelectedTab(TAB_LISTA_PENDENCIAS);
		//return "limpar";
	}

	public void limparDocumentoOriginal() {
		this.selectedTab = this.TAB_LISTA_PENDENCIAS;
		this.setSeqAghVersaoDocumento(null);
	}

	public void pesquisarComDefinicaoDeTipo() throws BaseException {
		this.definirTipo();
		this.pesquisar();
	}

	public void pesquisar() {
		
		if(this.responsavel == null && this.matricula != null && this.vinCodigo != null){
			if (matricula != null && vinCodigo != null
					&& (this.responsavel == null)) {
				RapServidoresId servidorId = new RapServidoresId(matricula,
						vinCodigo.shortValue());
				this.responsavel = this.registroColaboradorFacade
						.buscaServidor(servidorId);
			}
		}

		if (this.responsavel == null) {
			apresentarMsgNegocio(
					Severity.ERROR,
					ListarPendenciasAssinaturaPaginatorExceptionCode.MENSAGEM_RESPONSAVEL_NULO
							.toString());
			return;
		}

		if (this.matricula != null) {
			if (CoreUtil.isNumeroInteger(this.matricula)) {
				this.matricula = Integer.valueOf(this.matricula.toString());
			}

			if (this.vinCodigo != null && this.responsavel == null) {

				RapServidoresId id = new RapServidoresId(this.matricula,
						this.vinCodigo.shortValue());
				this.responsavel = this.registroColaboradorFacade
						.buscaServidor(id);
			}

			this.vinCodigo = this.responsavel.getId().getVinCodigo().intValue();

			if (this.pacCodigo == null) {
				this.setTipo(null); // samis
			}
		}

		try {
			this.certificacaoDigitalFacade
					.verificaConfiguracaoCertificacaoDigital();
		} catch (ApplicationBusinessException e) {
			apresentarMsgNegocio(Severity.WARN, e.getCode().toString());
		}

		this.mostrarVisao();
		this.dataModel.reiniciarPaginator();
	}

	public void mostrarVisao() {

		StringBuffer temp = new StringBuffer();
		
		String espaco = " ";
		String legenda = getBundle().getString(
				"LEGENDA_DOCUMENTOS_PENDENTES_PROFISSIONAL");

		String legendaPaciente = getBundle().getString("LEGENDA_PACIENTE");
		String legendaDocsPendentesProfissional = getBundle().getString(
				"LEGENDA_DOCUMENTOS_PENDENTES_PROFISSIONAL");

		String legendaDocsPendentesPaciente = getBundle().getString(
				"LEGENDA_DOCUMENTOS_PENDENTES_PACIENTE");

		if (this.responsavel != null) {
			String nomeResponsavel = this.responsavel.getPessoaFisica()
					.getNome();
			String nomePessoaFisica = this.responsavel.getPessoaFisica()
					.getNome();

			if (this.tipo != null && this.tipo.equals("1")) {
				temp.append(legenda);
				temp.append(espaco);
				temp.append(nomeResponsavel);
				temp.append(espaco);
				temp.append(legendaPaciente);
				temp.append(espaco);
				temp.append(this.paciente.getNome());

			} else if (this.tipo == null || this.tipo.equals("2")) {
				temp.append(legendaDocsPendentesProfissional);
				temp.append(espaco);

				if (this.responsavel != null
						&& this.responsavel.getPessoaFisica() != null
						&& this.responsavel.getPessoaFisica().getNome() != null) {
					temp.append(nomePessoaFisica);
				}

			} else if (this.tipo != null && this.tipo.equals("3")) {
				if (this.paciente != null) {
					temp.append(legendaDocsPendentesPaciente);
					temp.append(espaco);
					temp.append(this.paciente.getNome());
				}
			}
		}
		this.tituloVisao = temp.toString();
	}

	/**
	 * Refaz pesquisa sem reiniciar o paginator, ou seja, pesquisando na mesma
	 * pagina
	 */
	public void refazerPesquisa() {
		getDataModel();
	}

	public String voltarPesquisa() {
		limparCampos();
		String returnValue = null;
		if (this.getVoltarPara() != null) {
			returnValue = this.getVoltarPara();
		}
		return returnValue;
	}

	public String cancelarPesquisa() {
		limparCampos();
		return "cancelarPesquisa";
	}

	public void selecionaTodos() {
		for (DocumentosPendentesVO doc : this.documentosPendentesVO) {
			doc.setSelecionado(this.getClicouCheckboxTodos());
		}
	}

	public List<String> getListSeqDocsMarcados(){
		List<String> retorno= new ArrayList<String>();
		for (DocumentosPendentesVO doc : this.documentosPendentesVO) {
			if(doc.getSelecionado()){
				retorno.add(doc.getSeq().toString());
			}
		}
		return retorno;
	}
	
	public RapServidores getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(RapServidores responsavel) {
		this.responsavel = responsavel;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public Integer getVinCodigo() {
		return vinCodigo;
	}

	public void setVinCodigo(Integer vinCodigo) {
		this.vinCodigo = vinCodigo;
	}

	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public List<DocumentosPendentesVO> getDocumentosPendentesVO() {
		return documentosPendentesVO;
	}

	public void setDocumentosPendentesVO(
			List<DocumentosPendentesVO> documentosPendentesVO) {
		this.documentosPendentesVO = documentosPendentesVO;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public Boolean getTodosDocumentosSelecionados() {
		return todosDocumentosSelecionados;
	}

	public void exibirMensagemSucessoAssinatura() {
		dataModel.reiniciarPaginator();
		apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ASSINATURA");
	}

	public void setTodosDocumentosSelecionados(Boolean selecionaTodos) {

		// Se desmarcou a seleção de todas as consultas
		if (this.todosDocumentosSelecionados && !selecionaTodos) {
			this.documentosPendentesVO = null;
		}

		// Se clicou no checkbox (mudou o valor boolean)
		if (this.todosDocumentosSelecionados != selecionaTodos) {
			this.todosDocumentosSelecionados = selecionaTodos;
			clicouCheckboxTodos = true;
		}
		refazerPesquisa();
	}

	public Boolean getClicouCheckboxTodos() {
		return clicouCheckboxTodos;
	}

	public void setClicouCheckboxTodos(Boolean clicouCheckboxTodos) {
		this.clicouCheckboxTodos = clicouCheckboxTodos;
	}

	public boolean isInicializado() {
		return inicializado;
	}

	public void setInicializado(boolean inicializado) {
		this.inicializado = inicializado;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public String getTituloVisao() {
		return tituloVisao;
	}

	public void setTituloVisao(String tituloVisao) {
		this.tituloVisao = tituloVisao;
	}

	public Integer getSeqAghVersaoDocumento() {
		return seqAghVersaoDocumento;
	}

	public void setSeqAghVersaoDocumento(Integer seqAghVersaoDocumento) {
		this.seqAghVersaoDocumento = seqAghVersaoDocumento;
	}

	public String getContextPath() {
		return contextPath;
	}

	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	public StreamedContent getRenderPdf() {

		try {

			if (this.seqAghVersaoDocumento == null) {

				apresentarMsgNegocio(
						Severity.ERROR,
						ListarPendenciasAssinaturaPaginatorExceptionCode.MENSAGEM_SEQ_AGH_VERSAO_DOCUMENTO_NULO
								.toString());
				this.selectedTab = this.TAB_LISTA_PENDENCIAS;
				return null;
			}
			
			this.selectedTab = this.TAB_DOCUMENTO_ORIGINAL;
			AghVersaoDocumento arquivo = this.certificacaoDigitalFacade
					.visualizarDocumentoOriginal(this.seqAghVersaoDocumento);

			return new DefaultStreamedContent(new ByteArrayInputStream(
					arquivo.getOriginal()), "application/pdf", "relatorio.pdf");

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	public String obterUserLogado(){
		return super.obterLoginUsuarioLogado();
	}

	public DynamicDataModel<DocumentosPendentesVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<DocumentosPendentesVO> dataModel) {
		this.dataModel = dataModel;
	}

	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}

	public String getTABLISTA() {
		return TAB_LISTA_PENDENCIAS;
	}
}
