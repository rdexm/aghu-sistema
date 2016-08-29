package br.gov.mec.aghu.exames.action;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoImpressaoLaudo;
import br.gov.mec.aghu.exames.business.IExamesBeanFacade;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.exames.laudos.ResultadoLaudoVO;
import br.gov.mec.aghu.exames.pesquisa.business.IPesquisaExamesFacade;
import br.gov.mec.aghu.exames.solicitacao.vo.DetalharItemSolicitacaoPacienteVO;
import br.gov.mec.aghu.exames.vo.AelItemSolicConsultadoVO;
import br.gov.mec.aghu.exames.vo.DesenhoMascaraExameVO;
import br.gov.mec.aghu.exameselaudos.business.IExamesLaudosFacade;
import br.gov.mec.aghu.model.AelDocResultadoExame;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelNotaAdicional;
import br.gov.mec.aghu.model.AelNotaAdicionalId;
import br.gov.mec.aghu.model.AelParametroCamposLaudo;
import br.gov.mec.aghu.model.AelResultadosPadrao;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.vo.VRapServCrmAelVO;

@SuppressWarnings({"PMD.HierarquiaControllerIncorreta", "PMD.ExcessiveClassLength"})
public class CadastroResultadosNotaAdicionalController extends DesenhoMascaraExamesController {

	private static final Log LOG = LogFactory.getLog(CadastroResultadosNotaAdicionalController.class);
	private static final String INFORMAR_SOLIC_EXAMES = "exames-informarSolicitacaoExameDigitacao";
	private static final String PAGE_CAD_RESULTADO_NOTA_ADICIONAL = "exames-resultadoNotaAdicional";
	private static final String RELATORIO_RESULTADO_EXAMES_PDF = "exames-consultarResultadoNotaAdicional";

	private static final long serialVersionUID = 5910702361309277441L;

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private IExamesLaudosFacade examesLaudosFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	// Instâncias da estória #14255 Anexar Documentos ao Laudo
	@EJB
	private IExamesBeanFacade examesBeanFacade;
	
	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;
	
	@EJB
	private IPesquisaExamesFacade pesquisaExamesFacade;
	
	@Inject
	private SecurityController securityController;
	
	@Inject
	private CalculaValorMascaraExamesComponente calculaValorMascaraExamesComponente;
	
	@Inject
	private CadastroResultadoPadraoLaudoController cadastroResultadoPadraoLaudoController;
	
	// // Integração #14256: Gera O array de bytes com o resultado do exame liberado
	@Inject
	private ConsultarResultadosNotaAdicionalController consultarResultadosNotaAdicionalController;
	
	// // Integração #14256: Gera pendências de assinatura para exames liberados
	@Inject
	private ConsultaResultadosNotaAdicionalGerarPendenciaAssinaturaController notaAdicionalGerarPendenciaAssinaturaController;

	private String voltarPara = INFORMAR_SOLIC_EXAMES;
	private static final String PAGE_VISUALIZACAO_RESULTADO_EXAME = "exames-visualizarResultado";
	private static final String PAGE_PESQUISA_FLUXOGRAMA = "exames-pesquisaFluxograma";

	/** NOVO **/
	private DetalharItemSolicitacaoPacienteVO pacienteVO;
	private String descricaoCompletaExame;
	private List<AelItemSolicConsultadoVO> listaItemSolicConsultado;
	private List<AelResultadosPadrao> listaResultadosPadrao;

	/** Nota adicional **/
	private List<AelNotaAdicional> notasAdicionais;
	private String notaAdicionalTexto;

	private Boolean mostrarMensagemLiberar = false;
	private Boolean mostraNotasAdicionais = false;

	// Instancias pesquisa de servidores responsaveis pela liberacao
	private RapServidores servidorLiberacao;
	private VRapServCrmAelVO servidorLiberacaoVO;
	private Boolean exigeResponsavel = false;
	private Boolean usuarioLiberaExame = null;
	private Boolean usuarioAnulaExame = null;
	private Boolean exibeModalConsultados = Boolean.FALSE;
	private Boolean exameLiberado = Boolean.FALSE;

	private String labelArquivoAnexo = null;

	private Boolean permiteAnexarDocumento = false;
	// Instâncias de arquivo/anexo de upload
	private UploadedFile uploadedFile;
	
	private String situacaoSolicitacao;

	private AelResultadosPadrao resultadoPadrao;
	private Integer resultPadSeq;
	private Boolean erroAnular = Boolean.FALSE;
	private List<DesenhoMascaraExameVO> desenhosMascarasExamesVO;
	
	private Boolean visualizaApenasNotaAdicional;
	
	/** Parâmetro de entrada na tela. Representa o sequencial da solicitação de exames. **/
	private Integer solicitacaoExameSeq;

	/** Parâmetro de entrada na tela. Representa o sequencial do item da solicitação de exame. **/
	private Short itemSolicitacaoExameSeq;

	private String listaErrosValoresNormalidade;
	private String situacaoLiberado;
	
	@Override
	@PostConstruct
	public void init() {
		this.begin(conversation);
	}

	public String inicio() {
		exameLiberado = Boolean.FALSE;
		
		if(getSolicitacaoExameSeq() == null && this.getItemSolicitacaoExameSeq() == null){
			return null;
		}

		try {
			// Solicitacao
			setItemSolicitacao(this.examesFacade.buscaItemSolicitacaoExamePorId(this.getSolicitacaoExameSeq(), this.getItemSolicitacaoExameSeq()));

			this.inicializarControles();
					
			situacaoLiberado = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SITUACAO_LIBERADO).getVlrTexto();	
			inicializarSituacaoExame();

			this.mostraNotasAdicionais = this.getMostrarAbaNotasAdicionais(getItemSolicitacao());

			// Verifica se permite anexar documento
			this.getVerificarPermissaoAnexarDocumento();
			if (permiteAnexarDocumento){
				this.anexarDocumentoLaudoAutomaticamente();
				this.isDocumentoAnexado();
			}

			// Paciente
			pacienteVO = new DetalharItemSolicitacaoPacienteVO();
			pacienteVO.setNomePaciente(examesFacade.buscarLaudoNomePaciente(getItemSolicitacao().getSolicitacaoExame()));

			// Exame
			this.iniciarExame();

			/** Integração com a POC Os parametros já foram setados solicitacaoExameSeq itemSolicitacaoExameSeq **/
			super.setSolicitacaoExameSeq(getSolicitacaoExameSeq());
			super.setItemSolicitacaoExameSeq(getItemSolicitacao().getId().getSeqp());
			super.inicializar();
			desenhosMascarasExamesVO = super.getDesenhosMascarasExamesVO();
			
			if(exigeResponsavel) {
				if (getItemSolicitacao().getServidorResponsabilidade() != null) {
					RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(super.obterLoginUsuarioLogado());
					List<VRapServCrmAelVO> servidores = this.examesFacade
							.obterListaResponsavelLiberacao(getItemSolicitacao().getServidorResponsabilidade().getId().getMatricula().toString(), true);
					if(servidores != null && !servidores.isEmpty() && desenhosMascarasExamesVO.get(0).isPossuiResultados() && servidorLogado.equals(servidores.get(0))) {
						servidorLiberacaoVO = servidores.get(0);
						this.selecionarServidorLiberacao();
						
					} else {
						setarUsuarioPadraoLiberacao();
					}
				} else {
					setarUsuarioPadraoLiberacao();
				}
			}
			
			this.carregarListaResultadosPadrao();
			
			/** Deixa as formulas na sessão **/
			// TODO calculaValorMascaraExamesController.setDesenhosMascarasExamesVO(desenhosMascarasExamesVO);
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
			return voltarPara;
		}
		
		if (voltarPara != null && voltarPara.equals(PAGE_PESQUISA_FLUXOGRAMA)){
			setVisualizaApenasNotaAdicional(true);
		} else {
			setVisualizaApenasNotaAdicional(false);
		}

		return null;
	}
	
	public void inicializarControles() {
		if (getItemSolicitacao().getAelUnfExecutaExames() != null) {
			exigeResponsavel = this.aghuFacade.possuiCaracteristicaPorUnidadeEConstante(getItemSolicitacao().getAelUnfExecutaExames().getUnidadeFuncional().getSeq(), ConstanteAghCaractUnidFuncionais.SOLICITA_RESPONSAVEL); 
			mostrarMensagemLiberar = !getItemSolicitacao().getAelUnfExecutaExames().getIndLiberaResultAutom();
		}
	}
	
	private void inicializarSituacaoExame(){
		AelSitItemSolicitacoes aelSitItemSolicitacao = examesFacade.obteritemSolicitacaoExamesPorChavePrimaria(getItemSolicitacao().getId()).getSituacaoItemSolicitacao();	
		
		if (aelSitItemSolicitacao != null && situacaoLiberado != null){	
			if (situacaoLiberado.equals(aelSitItemSolicitacao.getCodigo())){
				this.exameLiberado = Boolean.TRUE;
			} else {
				exameLiberado = Boolean.FALSE;
			}
		}
	}	
	
	private void setarUsuarioPadraoLiberacao() throws BaseException{
		RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(super.obterLoginUsuarioLogado());
		if(servidorLogado != null) {
			List<VRapServCrmAelVO> servidores = this.examesFacade.obterListaResponsavelLiberacao(servidorLogado.getId().getMatricula().toString(), true);
			if(servidores != null && !servidores.isEmpty()) {
				servidorLiberacaoVO = servidores.get(0);
				this.selecionarServidorLiberacao();
			}
		}
	}
	
	public void selecionarServidorLiberacao() {
		this.servidorLiberacao = this.registroColaboradorFacade.obterRapServidor(new RapServidoresId(servidorLiberacaoVO.getMatricula(), servidorLiberacaoVO.getVinCodigo()));
	}
	
	public void limparServidorLiberacao() {
		this.servidorLiberacao = null;
	}
	
	public String visualizarRelatorio() {
		try {
			this.exibeModalConsultados = Boolean.FALSE;
			geraLaudo(false, PAGE_CAD_RESULTADO_NOTA_ADICIONAL);
			return RELATORIO_RESULTADO_EXAMES_PDF;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	public void imprimirRelatorio(){
		try {
			this.exibeModalConsultados = Boolean.FALSE;
			geraLaudo(true, null);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	private void geraLaudo(Boolean directPrint, String voltarPara) throws ApplicationBusinessException {
		Map<Integer, Vector<Short>> solicitacoes = new HashMap<Integer, Vector<Short>>();
		Integer soeSeq = this.getSolicitacaoExameSeq();
		Short seqp = this.getItemSolicitacaoExameSeq();
		solicitacoes.put(soeSeq, new Vector<Short>());
		solicitacoes.get(soeSeq).add(seqp);
		
		this.pesquisaExamesFacade.validaSituacaoExamesSelecionados(solicitacoes, Boolean.FALSE, Boolean.FALSE);
		
		consultarResultadosNotaAdicionalController.setListaDesenhosMascarasExamesVO(null);
		consultarResultadosNotaAdicionalController.setIsHist(false);
		consultarResultadosNotaAdicionalController.setSolicitacoes(solicitacoes);
		consultarResultadosNotaAdicionalController.setExibeBotoes(false);
		consultarResultadosNotaAdicionalController.setVoltarPara(voltarPara);
		consultarResultadosNotaAdicionalController.setTipoLaudo(DominioTipoImpressaoLaudo.LAUDO_PAC_EXTERNO);
		consultarResultadosNotaAdicionalController.setIsDirectPrint(directPrint);
		consultarResultadosNotaAdicionalController.setExecutouInicio(false);
		consultarResultadosNotaAdicionalController.inicio();
		consultarResultadosNotaAdicionalController.setExecutouInicio(true);
		if (directPrint) {
			consultarResultadosNotaAdicionalController.directPrint();
		}
	}
	
	public void carregarResultadoPadrao() {
		if(resultPadSeq != null) {
			resultadoPadrao = this.examesFacade.obterResultadosPadraoPorSeq(resultPadSeq);
			this.carregarExames(resultadoPadrao);
			ViewHandler viewHandler = FacesContext.getCurrentInstance().getApplication().getViewHandler();
	        UIViewRoot viewRoot = viewHandler.createView(FacesContext.getCurrentInstance(), FacesContext.getCurrentInstance().getViewRoot().getViewId());
	        FacesContext.getCurrentInstance().setViewRoot(viewRoot);
	        FacesContext.getCurrentInstance().renderResponse();
		}
	}

	public Object calcularValor(String idCampo) {
		calculaValorMascaraExamesComponente.setPrevia(false);
		calculaValorMascaraExamesComponente.setDesenhosMascarasExamesVO(super.getDesenhosMascarasExamesVO());
		return calculaValorMascaraExamesComponente.calcularValor(idCampo);
	}

	
	public String visualizarDocumentoAnexo(){
		return PAGE_VISUALIZACAO_RESULTADO_EXAME;
	}
	
	private void iniciarExame(){
		descricaoCompletaExame = getItemSolicitacao().getExame().getSigla() + " - " + getItemSolicitacao().getExame().getDescricaoUsual();
		descricaoCompletaExame += (getItemSolicitacao().getMaterialAnalise() != null) ? " - " + getItemSolicitacao().getMaterialAnalise().getDescricao() : "";
		notasAdicionais = examesFacade.pesquisarNotaAdicionalPorSolicitacaoEItem(this.getSolicitacaoExameSeq(), getItemSolicitacao().getId().getSeqp());
		this.exibeModalConsultados = Boolean.FALSE;
		pesquisaExamesConsultados(this.getSolicitacaoExameSeq(), this.getItemSolicitacaoExameSeq());
	}
	
	/**
	 * @param itemSolicitacao
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private boolean getMostrarAbaNotasAdicionais(AelItemSolicitacaoExames itemSolicitacao) throws ApplicationBusinessException {
		final String situacao = getItemSolicitacao().getSituacaoItemSolicitacao().getCodigo();
		return situacao.equals(this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SITUACAO_LIBERADO).getVlrTexto());
	}

	/** #16838 Anexa documento ao laudo automaticamente **/
	public void anexarDocumentoLaudoAutomaticamente() {
		try {
			examesFacade.anexarDocumentoLaudoAutomatico(getItemSolicitacao());
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}

	public String gravarResultados() {
		try {

			Map<AelParametroCamposLaudo, Object> valoresCampos = new HashMap<AelParametroCamposLaudo, Object>();
			for (DesenhoMascaraExameVO desenhoMascaraExameVO : this.getDesenhosMascarasExamesVO()) {
				preencherValores(desenhoMascaraExameVO.getFormularioDinamicoPanel().getChildren(), valoresCampos);
			}
			
			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e) {
				LOG.error("Exceção caputada:", e);
			}

			this.examesFacade.persistirResultados(valoresCampos, this.getSolicitacaoExameSeq(), getItemSolicitacao().getId().getSeqp(), servidorLiberacao, usuarioLiberaExame, nomeMicrocomputador, getItemSolicitacao());
			this.mostraNotasAdicionais = this.getMostrarAbaNotasAdicionais(getItemSolicitacao());
			
			if (this.usuarioLiberaExame != null && this.usuarioLiberaExame) {
				
				this.apresentarMsgNegocio(Severity.INFO, "LABEL_SUCESSO_LIBERACAO_RESULTADO");
			}
			this.apresentarMsgNegocio(Severity.INFO, "LABEL_SUCESSO_GRAVACAO_RESULTADO");
			
 			// #14256: Integração com a geração da pendência de assinatura digital
			// this.gerarPendenciaAssinaturaDigital(getItemSolicitacao());
			if (securityController.usuarioTemPermissao("liberarResultadoExame", "executar")){
				return voltar();
			}
		} catch (BaseListException e) {
			this.exibeModalConsultados = Boolean.FALSE;
			this.listaErrosValoresNormalidade = null;
			
			int i = 0;
			for (Iterator<BaseException> errors = e.iterator(); errors.hasNext();) {
				BaseException aghuNegocioException = errors.next();
				
				if(this.listaErrosValoresNormalidade == null){
					this.listaErrosValoresNormalidade = aghuNegocioException.getParameters()[i].toString().concat("\n");
				}else{
					this.listaErrosValoresNormalidade.concat(aghuNegocioException.getParameters()[i].toString()).concat("\n");
				}
				i++;
			}
			
			super.openDialog("modalValidacaoValoresNormalidadeWG");
		} catch (ValidatorException e) {
			this.exibeModalConsultados = Boolean.FALSE;
			apresentarMsgNegocio(Severity.ERROR, e.getMessage());
		} catch (BaseException e) {
			this.exibeModalConsultados = Boolean.FALSE;
			super.apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	public void getValidatorOuModalLiberar() {
		try {
			validarNormalidade();
			super.openDialog("modalConfirmacaoWG");
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} catch (BaseListException e) {
			this.exibeModalConsultados = Boolean.FALSE;
			this.listaErrosValoresNormalidade = null;
			
			int i = 0;
			for (Iterator<BaseException> errors = e.iterator(); errors.hasNext();) {
				BaseException aghuNegocioException = errors.next();
				
				if(this.listaErrosValoresNormalidade == null){
					this.listaErrosValoresNormalidade = aghuNegocioException.getParameters()[i].toString().concat("\n");
				}else{
					this.listaErrosValoresNormalidade.concat(aghuNegocioException.getParameters()[i].toString()).concat("\n");
				}
				i++;
			}
			super.openDialog("modalValidacaoValoresNormalidadeWG");
		}
	}

	public void anularResultado() {
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error("Exceção caputada:", e);
		}
		try {
			this.examesFacade.anularResultado(this.getSolicitacaoExameSeq(), getItemSolicitacao().getId().getSeqp(), usuarioAnulaExame, nomeMicrocomputador, getItemSolicitacao());

			this.apresentarMsgNegocio(Severity.INFO, "LABEL_SUCESSO_ANULACAO_RESULTADO");

			this.mostraNotasAdicionais = this.getMostrarAbaNotasAdicionais(getItemSolicitacao());			

		} catch (BaseException e) {
			this.erroAnular = Boolean.TRUE;
			super.apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Pesquisa servidores que visualizaram esse resultado de exame
	 * 
	 * @return
	 */
	private void pesquisaExamesConsultados(final Integer iseSoeSeq, final Short iseSeqp) {
		this.listaItemSolicConsultado = this.examesFacade.pesquisarAelItemSolicConsultadosResultadosExames(iseSoeSeq, iseSeqp);
		if (this.listaItemSolicConsultado != null && !this.listaItemSolicConsultado.isEmpty()) {
			this.exibeModalConsultados = Boolean.TRUE;
		}
	}

	public String gravarResultadosLiberarLiberacaoAutomatica() {
		this.usuarioLiberaExame = true;
		return gravarResultados();
	}

	public String gravarResultadosLiberar() {
		this.usuarioLiberaExame = true;
		return this.gravarResultados();
	}

	public String gravarResultadosNaoLiberar() {
		this.usuarioLiberaExame = false;
		return this.gravarResultados();
	}

	public String anularResultadoExame() {
		this.usuarioAnulaExame = true;
		anularResultado();
		return voltar();
	}

	public void naoAnularResultadoExame() {
		this.usuarioAnulaExame = false;
		anularResultado();
		inicializarSituacaoExame();
	}

	public void gravarNotaAdicional() {
		try {

			if (getItemSolicitacao() != null && notaAdicionalTexto != null) {

				AelNotaAdicionalId id = new AelNotaAdicionalId();
				id.setIseSeqp(getItemSolicitacao().getId().getSeqp());
				id.setIseSoeSeq(this.getSolicitacaoExameSeq());

				AelNotaAdicional notaAdicional = new AelNotaAdicional();
				notaAdicional.setId(id);
				notaAdicional.setItemSolicitacaoExame(getItemSolicitacao());

				notaAdicional.setNotasAdicionais(notaAdicionalTexto);

				this.examesFacade.inserirNotaAdicional(notaAdicional);

				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_INCLUIR_NOTA_ADICIONAL");

				notaAdicionalTexto = null;
				
				this.inicio();
				
				exibeModalConsultados = Boolean.FALSE;

				// #14256: Integração com a geração da pendência de assinatura digital
				// this.gerarPendenciaAssinaturaDigital(getItemSolicitacao());

			}

		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}


	/**
	 * Meodo para Suggestion Box de servidorresponsavel pela liberacao
	 * 
	 * @param object
	 * @return
	 */
	public List<VRapServCrmAelVO> suggestionServidor(String paramPesquisa) {
		try {
			return this.examesFacade.obterListaResponsavelLiberacao(paramPesquisa, false);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	/** Método chamado para o botão voltar **/
	public String voltar() {
		super.limparParametros();
		this.limparParametros();
		return this.voltarPara;
	}

	/** Reseta parâmetros de conversação da tela **/
	@Override
	protected void limparParametros() {
		this.pacienteVO = null;
		this.descricaoCompletaExame = null;
		this.listaItemSolicConsultado = null;
		this.listaResultadosPadrao = null;
		this.notasAdicionais = null;
		this.notaAdicionalTexto = null;
		this.mostrarMensagemLiberar = false;
		this.mostraNotasAdicionais = false;
		this.servidorLiberacao = null;
		this.servidorLiberacaoVO = null;
		this.exigeResponsavel = false;
		this.usuarioLiberaExame = null;
		this.usuarioAnulaExame = null;
		this.exibeModalConsultados = Boolean.FALSE;
		this.permiteAnexarDocumento = false;
		this.uploadedFile = null;
		this.situacaoSolicitacao = null;
		this.resultadoPadrao = null;
		this.resultPadSeq = null;
		this.erroAnular = Boolean.FALSE;
		this.desenhosMascarasExamesVO = null;
	}

	/** Estoria de Usuario #5848 chamado<br> pelo botao de Liberar. **/
	public String liberarExame() {
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error("Exceção caputada:", e);
		}
		try {

			Map<AelParametroCamposLaudo, Object> valoresCampos = new HashMap<AelParametroCamposLaudo, Object>();
			for (DesenhoMascaraExameVO desenhoMascaraExameVO : this.getDesenhosMascarasExamesVO()) {
				preencherValores(desenhoMascaraExameVO.getFormularioDinamicoPanel().getChildren(), valoresCampos);
			}

			this.examesFacade.persistirResultados(valoresCampos, this.getSolicitacaoExameSeq(), getItemSolicitacao().getId().getSeqp(), servidorLiberacao, true, nomeMicrocomputador, getItemSolicitacao());
			this.mostraNotasAdicionais = this.getMostrarAbaNotasAdicionais(getItemSolicitacao());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_LIBERAR_EXAME_CONCLUIDO");

			return voltar();
		} catch (BaseListException e) {
			this.exibeModalConsultados = Boolean.FALSE;
			this.listaErrosValoresNormalidade = null;
			
			int i = 0;
			for (Iterator<BaseException> errors = e.iterator(); errors.hasNext();) {
				BaseException aghuNegocioException = errors.next();
				
				if(this.listaErrosValoresNormalidade == null){
					this.listaErrosValoresNormalidade = aghuNegocioException.getParameters()[i].toString().concat("\n");
				}else{
					this.listaErrosValoresNormalidade.concat(aghuNegocioException.getParameters()[i].toString()).concat("\n");
				}
				i++;
			}
			this.mostrarMensagemLiberar = false;
			super.openDialog("modalValidacaoValoresNormalidadeWG");
		} catch (ValidatorException e) {
			apresentarMsgNegocio(Severity.ERROR, e.getMessage());
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	/**
	 * #14256: Integração com a geração da pendência de assinatura digital
	 * @param solicitacaoExame
	 * @throws BaseException
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyManagementException 
	 */
	public void gerarPendenciaAssinaturaDigital(AelItemSolicitacaoExames itemSolicitacao) throws BaseException, KeyManagementException, NoSuchAlgorithmException {

		ResultadoLaudoVO baos = null;

		Map<Integer, Vector<Short>> solicitacoes = new HashMap<Integer, Vector<Short>>();
		Vector<Short> solSeqs = new Vector<Short>();
		solSeqs.add(getItemSolicitacao().getId().getSeqp());
		solicitacoes.put(this.getSolicitacaoExameSeq(), solSeqs);
		examesLaudosFacade.verificaLaudoPatologia(solicitacoes, false);

		// Informa a coleção a controller
		this.consultarResultadosNotaAdicionalController.setSolicitacoes(solicitacoes);
		// // Informa que o laudo gerado deve ser do tipo Laudo Externo
		this.consultarResultadosNotaAdicionalController.setTipoLaudo(DominioTipoImpressaoLaudo.LAUDO_PAC_EXTERNO);

		try {
			// Gera um array de bytes do arquivo/resultado que será assinado
			baos = this.consultarResultadosNotaAdicionalController.executaLaudo(false);

			this.notaAdicionalGerarPendenciaAssinaturaController.setAtendimento(getItemSolicitacao().getSolicitacaoExame().getAtendimento());
			this.notaAdicionalGerarPendenciaAssinaturaController.setCaminhoArquivo(this.consultarResultadosNotaAdicionalController.getCaminhoArquivo());

			AelSolicitacaoExames solicitacaoExame = getItemSolicitacao().getSolicitacaoExame();

			if (solicitacaoExame.getAtendimento() != null) { // Caso atendimento
				// Seta atendimento utilizada no retorno da entidade pai
				this.notaAdicionalGerarPendenciaAssinaturaController.setAtendimento(solicitacaoExame.getAtendimento());
			} else if (solicitacaoExame.getAtendimentoDiverso() != null) { // Caso atendimento diverso
				// Seta atendimento Diverso utilizada no retorno da entidade pai
				this.notaAdicionalGerarPendenciaAssinaturaController.setAtendimentoDiverso(solicitacaoExame.getAtendimentoDiverso());
			}

			// #14256: Integração com a geração da pendência de assinatura digital
			this.notaAdicionalGerarPendenciaAssinaturaController.gerarPendenciaAssinaturaDigital(baos.getOutputStreamLaudo());
		} catch (IllegalArgumentException e) {
			LOG.error("Exceção capturada: ", e);
			apresentarMsgNegocio("ERRO_GERAR_PENDENCIA_ASSINATURA_EXAME_LIBERADO");
		}
	}

	/* Métodos da estória #14255 Anexar Documentos ao Laudo */
	private void getVerificarPermissaoAnexarDocumento() {
		this.permiteAnexarDocumento = getItemSolicitacao().getExame().getIndPermiteAnexarDoc();
	}

	/**
	 * Listener de upload para o arquivo de laudo
	 * @param event
	 */
	public void listenerUploadArquivoLaudo(FileUploadEvent event) {
		this.uploadedFile = event.getFile();
		anexarDocumentoLaudo();
	}
	

	/**
	 * Anexa arquivo de laudo do upload
	 * @param anexo
	 */
	public void anexarDocumentoLaudo() {

		final Integer tamanhoMaximoUpload = this.examesFacade.obterTamanhoMaximoBytesUploadLaudo();
		// Valida o tamanho do arquivo enviado
		if (this.uploadedFile != null && this.uploadedFile.getSize() < tamanhoMaximoUpload) {
			// Instancia um documento de resultado de exames
			AelDocResultadoExame doc = new AelDocResultadoExame();
			doc.setItemSolicitacaoExame(getItemSolicitacao());
			// Seta anulação como FALSA
			doc.setIndAnulacaoDoc(false);
			try {
				byte[] buffer = new byte[1024 * 1024];
				
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				InputStream in = this.uploadedFile.getInputstream();
				int len;
				while ((len = in.read(buffer)) > 0) {
					baos.write(buffer, 0, len);
				}
				in.close();
				doc.setDocumento(baos.toByteArray());
				baos.close();
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
			}

			// Seta o responsável pela liberação
			doc.getItemSolicitacaoExame().setServidorResponsabilidade(this.servidorLiberacao);
			try {
				this.examesBeanFacade.anexarDocumentoLaudo(doc, getItemSolicitacao().getUnidadeFuncional());			
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_UPLOAD_ARQUIVO_LAUDO", this.uploadedFile.getFileName());
			} catch (BaseException e) {
				super.apresentarExcecaoNegocio(e);
			}
		} else {
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_TAMANHO_ARQUIVO_REJEITADO", this.uploadedFile.getFileName(), tamanhoMaximoUpload);
		}
	}

	/** Exibe mensagem de erro para tipo de arquivo rejeitado durante o upload **/
	public void exibirMensagemUploadTipoArquivoRejeitado() {
		this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_TIPO_ARQUIVO_REJEITADO", "PDF");
	}

	/** Verifica a existencia de arquivo de laudo Existencia: Exibe os icones de DOWNLOAD e REMOCAO de arquivo na listagem Inexistencia: Exibe o icone de UPLOAD/ADICAO de arquivo! **/
	public boolean isDocumentoAnexado() { 
		AelDocResultadoExame docResultadoExame = this.examesFacade.obterDocumentoAnexado(this.getSolicitacaoExameSeq(), getItemSolicitacao().getId().getSeqp());
		if (docResultadoExame != null){
			Integer seqAelDocResultadoExames = docResultadoExame.getSeq();
			String nomeArquivo = this.examesFacade.extrairNomeExtensaoDocumentoLaudoAnexo(getItemSolicitacao());	
			this.setLabelArquivoAnexo(": " + nomeArquivo + "  -  Doc. " +  seqAelDocResultadoExames.toString());
			return true;
		} else {
			this.setLabelArquivoAnexo(null);
			return false;
		}		
	}

	/**
	 * Download do arquivo de laudo
	 * @return
	 */
	public String downloadArquivoLaudo() {
		// Resgata instancia de AelDocResultadoExame
		AelDocResultadoExame doc = this.examesFacade.obterDocumentoAnexado(this.getSolicitacaoExameSeq(), getItemSolicitacao().getId().getSeqp());
		// Inicia download via uma HTTP Response
		return this.downloadViaHttpServletResponse(doc.getDocumento());
	}
	
	/**
	 * Download via uma Http Response
	 * @param dados array de bytes (stream) do arquivo de download
	 * @return
	 */
	private String downloadViaHttpServletResponse(byte[] dados) {
		// Instancia uma HTTP Response
		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
		// Seta o tipo de MIME
		response.setContentType("application/octet-stream");
		// Obtém o nome do arquivo em anexo: SOE_SEQ + SEQP com 3 zeros à esquerda + extensão
		String nomeArquivo = this.examesFacade.extrairNomeExtensaoDocumentoLaudoAnexo(getItemSolicitacao());
		// Seta o arquivo no cabeçalho
		response.addHeader("Content-disposition", "attachment; filename=\"" + nomeArquivo + "\"");
		// Escreve a resposta no HTTP RESPONSE
		ServletOutputStream os = null;
		try {
			os = response.getOutputStream();
			os.write(dados); // Escrevemos o STREAM de resposta/RESPONSE			
			os.flush();
			FacesContext.getCurrentInstance().responseComplete();
		} catch (Exception e) {
			LOG.error(e.getMessage());
		} finally {
			IOUtils.closeQuietly(os);
		}
		return null;
	}

	/** Excluir (Atualiza status) do arquivo de laudo enviado **/
	public void removerDocumentoLaudo() {
		try {
			final AelDocResultadoExame doc = this.examesFacade.obterDocumentoAnexado(this.getSolicitacaoExameSeq(), this.getItemSolicitacaoExameSeq());
			if (doc != null) {
				this.examesBeanFacade.removerDocumentoLaudo(doc, getItemSolicitacao().getUnidadeFuncional());
				this.setLabelArquivoAnexo(null);
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOVER_ARQUIVO_LAUDO");
			}
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Método que realiza a ação do botão atualizar situação
	 */
	public void editarSituacaoResultPadrao(AelResultadosPadrao item) {
		try {
			AelResultadosPadrao resultadoPadrao = this.cadastrosApoioExamesFacade.obterAelResultadoPadraoPorId(item.getSeq()); 
			if (resultadoPadrao != null) {
				DominioSituacao situacao = resultadoPadrao.getSituacao() == DominioSituacao.A ? DominioSituacao.I : DominioSituacao.A;
				resultadoPadrao.setSituacao(situacao);
				// Submete o procedimento para ser atualizado
				this.cadastrosApoioExamesFacade.persistirResultadoPadrao(resultadoPadrao);
			}
			if (this.exibeModalConsultados) {
				this.exibeModalConsultados = Boolean.FALSE;
			}
			this.carregarListaResultadosPadrao();
			// Apresenta as mensagens de acordo
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_RESULT_PADRAO");
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public boolean getActive(AelResultadosPadrao item) {
		return (item.getSituacao() == DominioSituacao.A);
	}

	/**
	 * Lista AelResultadoPadrao
	 * @return
	 */
	public List<AelResultadosPadrao> getListarResultadosPadrao() {
		List<AelResultadosPadrao> retorno = null;
		if (getItemSolicitacao() != null) {
			retorno = this.cadastrosApoioExamesFacade.listarResultadoPadraoCampoPorExameMaterial(getItemSolicitacao().getExame().getSigla(), getItemSolicitacao().getMaterialAnalise().getSeq());
		}
		return retorno;
	}

	/** Carrega uma lista de resultados padrão do item de solicitação de exame selecionado **/
	public void carregarListaResultadosPadrao() {
		if (getItemSolicitacao() != null) {
			this.listaResultadosPadrao = this.cadastrosApoioExamesFacade.listarResultadoPadraoCampoPorExameMaterial(getItemSolicitacao().getExame().getSigla(), getItemSolicitacao()
					.getMaterialAnalise().getSeq());
		}
	}

	public void carregarExames(AelResultadosPadrao resultadoPadrao) {
		try {
			desenhosMascarasExamesVO =
					getCadastroResultadoPadraoLaudoController().montaDesenhosMascarasExamesResultadoPadrao(resultadoPadrao, this.getVelSeqp(), this.getSolicitacaoExameSeq(),
							getItemSolicitacao().getId().getSeqp());
			this.exibeModalConsultados = Boolean.FALSE;
			this.resultPadSeq = null;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	/** GET/SET **/
	public AelResultadosPadrao getResultadoPadrao() {
		return this.resultadoPadrao;
	}
	public void setResultadoPadrao(AelResultadosPadrao resultadoPadrao) {
		this.resultadoPadrao = resultadoPadrao;
	}
	public String getVoltarPara() {
		return voltarPara;
	}
	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}
	public DetalharItemSolicitacaoPacienteVO getPacienteVO() {
		return pacienteVO;
	}
	public void setPacienteVO(DetalharItemSolicitacaoPacienteVO pacienteVO) {
		this.pacienteVO = pacienteVO;
	}
	public String getDescricaoCompletaExame() {
		return descricaoCompletaExame;
	}
	public void setDescricaoCompletaExame(String descricaoCompletaExame) {
		this.descricaoCompletaExame = descricaoCompletaExame;
	}
	public List<AelNotaAdicional> getNotasAdicionais() {
		return notasAdicionais;
	}
	public void setNotasAdicionais(List<AelNotaAdicional> notasAdicionais) {
		this.notasAdicionais = notasAdicionais;
	}
	public String getNotaAdicionalTexto() {
		return notaAdicionalTexto;
	}
	public void setNotaAdicionalTexto(String notaAdicionalTexto) {
		this.notaAdicionalTexto = notaAdicionalTexto;
	}
	public Boolean getExigeResponsavel() {
		return exigeResponsavel;
	}
	public void setExigeResponsavel(Boolean exigeResponsavel) {
		this.exigeResponsavel = exigeResponsavel;
	}
	public RapServidores getServidorLiberacao() {
		return servidorLiberacao;
	}
	public void setServidorLiberacao(RapServidores servidorLiberacao) {
		this.servidorLiberacao = servidorLiberacao;
	}
	public VRapServCrmAelVO getServidorLiberacaoVO() {
		return servidorLiberacaoVO;
	}
	public void setServidorLiberacaoVO(VRapServCrmAelVO servidorLiberacaoVO) {
		this.servidorLiberacaoVO = servidorLiberacaoVO;
	}
	public Boolean getMostrarMensagemLiberar() {
		return mostrarMensagemLiberar;
	}
	public void setMostrarMensagemLiberar(Boolean mostrarMensagemLiberar) {
		this.mostrarMensagemLiberar = mostrarMensagemLiberar;
	}
	public Boolean getUsuarioLiberaExame() {
		return usuarioLiberaExame;
	}
	public void setUsuarioLiberaExame(Boolean usuarioLiberaExame) {
		this.usuarioLiberaExame = usuarioLiberaExame;
	}
	public Boolean getUsuarioAnulaExame() {
		return usuarioAnulaExame;
	}
	public void setUsuarioAnulaExame(Boolean usuarioAnulaExame) {
		this.usuarioAnulaExame = usuarioAnulaExame;
	}
	public Boolean getPermiteAnexarDocumento() {
		return permiteAnexarDocumento;
	}
	public void setPermiteAnexarDocumento(Boolean permiteAnexarDocumento) {
		this.permiteAnexarDocumento = permiteAnexarDocumento;
	}
	public UploadedFile getUploadedFile() {
		return uploadedFile;
	}
	public void setUploadedFile(UploadedFile uploadedFile) {
		this.uploadedFile = uploadedFile;
	}
	public Boolean getMostraNotasAdicionais() {
		return mostraNotasAdicionais;
	}
	public void setMostraNotasAdicionais(Boolean mostraNotasAdicionais) {
		this.mostraNotasAdicionais = mostraNotasAdicionais;
	}
	public List<AelItemSolicConsultadoVO> getListaItemSolicConsultado() {
		return listaItemSolicConsultado;
	}
	public void setListaItemSolicConsultado(List<AelItemSolicConsultadoVO> listaItemSolicConsultado) {
		this.listaItemSolicConsultado = listaItemSolicConsultado;
	}
	public String getSituacaoSolicitacao() {
		return situacaoSolicitacao;
	}
	public void setSituacaoSolicitacao(String situacaoSolicitacao) {
		this.situacaoSolicitacao = situacaoSolicitacao;
	}
	public Boolean getExibeModalConsultados() {
		return exibeModalConsultados;
	}
	public void setExibeModalConsultados(Boolean exibeModalConsultados) {
		this.exibeModalConsultados = exibeModalConsultados;
	}
	public Boolean getExameLiberado() {
		return exameLiberado;
	}
	public void setExameLiberador(Boolean exameLiberado) {
		this.exameLiberado = exameLiberado;
	}
	public Integer getResultPadSeq() {
		return resultPadSeq;
	}
	public void setResultPadSeq(Integer resultPadSeq) {
		this.resultPadSeq = resultPadSeq;
	}
	public List<AelResultadosPadrao> getListaResultadosPadrao() {
		return listaResultadosPadrao;
	}
	public void setListaResultadosPadrao(List<AelResultadosPadrao> listaResultadosPadrao) {
		this.listaResultadosPadrao = listaResultadosPadrao;
	}
	public Boolean getErroAnular() {
		return erroAnular;
	}
	public void setErroAnular(Boolean erroAnular) {
		this.erroAnular = erroAnular;
	}
	@Override
	public List<DesenhoMascaraExameVO> getDesenhosMascarasExamesVO() {
		return desenhosMascarasExamesVO;
	}
	@Override
	public void setDesenhosMascarasExamesVO(List<DesenhoMascaraExameVO> desenhosMascarasExamesVO) {
		this.desenhosMascarasExamesVO = desenhosMascarasExamesVO;
	}
	@Override
	public Short getItemSolicitacaoExameSeq() {
		return itemSolicitacaoExameSeq;
	}
	@Override
	public void setItemSolicitacaoExameSeq(Short itemSolicitacaoExameSeq) {
		this.itemSolicitacaoExameSeq = itemSolicitacaoExameSeq;
	}
	@Override
	public Integer getSolicitacaoExameSeq() {
		return solicitacaoExameSeq;
	}
	@Override
	public void setSolicitacaoExameSeq(Integer solicitacaoExameSeq) {
		this.solicitacaoExameSeq = solicitacaoExameSeq;
	}
	public Boolean getVisualizaApenasNotaAdicional() {
		return visualizaApenasNotaAdicional;
	}
	public void setVisualizaApenasNotaAdicional(Boolean visualizaApenasNotaAdicional) {
		this.visualizaApenasNotaAdicional = visualizaApenasNotaAdicional;
	}
	public String getLabelArquivoAnexo() {
		return labelArquivoAnexo;
	}
	public void setLabelArquivoAnexo(String labelArquivoAnexo) {
		this.labelArquivoAnexo = labelArquivoAnexo;
	}

	public String getListaErrosValoresNormalidade() {
		return listaErrosValoresNormalidade;
	}
	public void setListaErrosValoresNormalidade(
			String listaErrosValoresNormalidade) {
		this.listaErrosValoresNormalidade = listaErrosValoresNormalidade;
	}

	public CadastroResultadoPadraoLaudoController getCadastroResultadoPadraoLaudoController() {
		return cadastroResultadoPadraoLaudoController;
	}

	public void setCadastroResultadoPadraoLaudoController(
			CadastroResultadoPadraoLaudoController cadastroResultadoPadraoLaudoController) {
		this.cadastroResultadoPadraoLaudoController = cadastroResultadoPadraoLaudoController;
	}
}