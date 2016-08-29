package br.gov.mec.aghu.prescricaomedica.action;

import java.io.IOException;
import java.net.UnknownHostException;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.OptimisticLockException;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.business.SelectionQualifier;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.cups.business.ICupsFacade;
import br.gov.mec.aghu.dominio.DominioIndTipoAltaSumarios;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.action.RelatorioSumarioAltaController.Subtitulo;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;

//@SuppressWarnings({"PMD.HierarquiaControllerIncorreta"})
public class ManterSumarioAltaConclusaoController extends ActionController {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7239933336046223248L;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private ICupsFacade cupsFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	private MpmAltaSumario altaSumario;

	private static final Log LOG = LogFactory.getLog(ManterSumarioAltaConclusaoController.class);
	
	/**
	 * Retorno
	 */
	private String voltarPara;
	
	private final static String ANTECIPAR_SUMARIO = "ANTECIPAR SUMARIO";
	private final static String OBITO = "OBITO";
	private final static String ALTA = "ALTA";
	private final String PAGE_MODAL_JUSTIFICATIVA_LAUDOS = "modalJustificativaLaudos";
//	private final String PAGE_RELATORIO_CONCLUSAO_SUMARIO_ALTA = "relatorioConclusaoSumarioAlta";
    private static final String PAGE_PRESCRICAOMEDICA_MANTER_DIAGNOSTICOS_PACIENTE = "prescricaomedica-manterDiagnosticosPaciente";
    private static final String MANTER_SUMARIO_ALTA = "manterSumarioAlta";
    private static final String ANTECIPAR_SUMARIO_ALTA = "anteciparSumario";
    private static final String MANTER_SUMARIO_OBITO = "manterSumarioObito";

	@Inject
	private ModalJustificativaLaudosController modalJustificativaLaudosController;

	@Inject
	private RelatorioConclusaoSumarioAltaController relatorioConclusaoSumarioAltaController;
	
	@Inject
	@SelectionQualifier
	private RelatorioSumarioAltaController relatorioSumarioAltaController;
	
	@Inject
	private RelatorioConclusaoAbaSumario relatorioConclusaoAbaSumario;

	@Inject
	private ManterSumarioAltaPosAltaController manterSumarioAltaPosAltaController;

 	@Inject
 	private  RelatorioConclusaoAbaReceituario relatorioConclusaoAbaReceituario;

	/**
	 * Verifica se é ANTECIPAR SUMARIO ou ALTA
	 */
	private String altanListaOrigem;
	
	private String  tipoImpressao = "S";
	
	@PostConstruct
	public void init() {
		begin(conversation);
	}
	
	/**
	 * @param altaSumario
	 * @param altaTipoOrigem
	 * @throws BaseException
	 */
	public void renderConclusao(MpmAltaSumario altaSumarioAtual, String altaTipoOrigem, String umVoltarPara, boolean popularReport) throws BaseException {
		this.setAltaSumario(altaSumarioAtual);
		this.setAltanListaOrigem(altaTipoOrigem);
		this.setVoltarPara(umVoltarPara);
		this.inicializaDados();

		if (popularReport) {
			relatorioSumarioAltaController.populaRelatorioAltaObito();
		}
	}
	
	public String voltarRelatorio(){    	
		if (ALTA.equals(this.altanListaOrigem)) {
			return MANTER_SUMARIO_ALTA;
		
		} else if (OBITO.equals(this.altanListaOrigem)) {
			return MANTER_SUMARIO_OBITO;
		
		} else if (ANTECIPAR_SUMARIO.equals(this.altanListaOrigem)) {
			return ANTECIPAR_SUMARIO_ALTA;
		}
		return null;
    }

	/**
	 * Inicializa os dados necessários para a aba de Conclusão.
	 * 
	 */
	private void inicializaDados() {
		Integer seqAtendimento = this.getAltaSumario().getId().getApaAtdSeq();
		relatorioSumarioAltaController.setSeqAtendimento(seqAtendimento);
		relatorioSumarioAltaController.setAtendimento(new AghAtendimentos(seqAtendimento));
		relatorioSumarioAltaController.setPrevia(true);
		relatorioSumarioAltaController.setObito(false);
		relatorioSumarioAltaController.setSubtitulo(Subtitulo.PREVIA.toString());
		
		if(this.altaSumario.getTipo().equals(DominioIndTipoAltaSumarios.OBT)){
			relatorioSumarioAltaController.setAltaSumario(this.altaSumario);
			relatorioSumarioAltaController.setAltaTipoOrigem(this.altanListaOrigem);
			relatorioSumarioAltaController.setTipoImpressao(this.tipoImpressao);
			relatorioSumarioAltaController.setObito(true);
		} 
    }
	
	public Boolean getExibirBotaoConcluir(){
		return !ANTECIPAR_SUMARIO.equals(altanListaOrigem);
	}
		
	/*public StreamedContent getRenderPdf() throws IOException,
	ApplicationBusinessException, JRException, SystemException, DocumentException {
		if(this.altaSumario.getTipo().equals(DominioIndTipoAltaSumarios.OBT)){
			relatorioSumarioAltaController.setAltaSumario(this.altaSumario);
			relatorioSumarioAltaController.setAltaTipoOrigem(this.altanListaOrigem);
			relatorioSumarioAltaController.setTipoImpressao(this.tipoImpressao);
			relatorioSumarioAltaController.setObito(true);
		}

		// Gera o PDF
//		print(false, isObito());
//		return this.criarStreamedContentPdf(getArquivoGerado().toByteArray());
		
		DocumentoJasper documento = relatorioSumarioAltaController.gerarDocumento(Boolean.TRUE);
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}*/
	
	/**
	 * Acao do botao de Concluir na aba de Conclusao do Sumario de Alta.
	 * @throws IOException 
	 * @throws SystemException 
	 * @throws JRException 
	 * 
	 */
	public String concluirSumarioAlta() throws JRException, SystemException, IOException {
		String returnValue = null;
		
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoIPv4HostRemoto().getHostAddress();
		} catch (UnknownHostException e) {
			nomeMicrocomputador = null;
		}
		
		try {
			//Realiza validações de preenchimento de campos
			prescricaoMedicaFacade.validarCamposObrigatoriosSumarioAlta(altaSumario);
			

			//#47088 - Retirado a verificação de computador cadastrado
			//Verifica se o computador está cadastrado, caso o cups esteja ativo
			/*if(cupsFacade.verificarCupsAtivo()) {
				
				String remoteAddress = null;
				try {
					remoteAddress = super.getEnderecoIPv4HostRemoto().getHostAddress();
				} catch (UnknownHostException e) {
					remoteAddress = null;
				}
				
				cupsFacade.verificarComputadorCadastrado(remoteAddress);
			}*/

			RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
			boolean exigeJustificativa = prescricaoMedicaFacade.concluirSumarioAlta(this.getAltaSumario(), nomeMicrocomputador, servidorLogado);
			
			LOG.debug("exigeJustificativa = "+exigeJustificativa);
			if (!exigeJustificativa) {
				//TODO: chamar diretamente os metodos que tem o observer, sem usar esses eventos
//				Boolean redireciona = false;
				
				this.setTipoImpressao("N");
				
				if (ALTA.equals(this.altanListaOrigem)) {
					
					relatorioConclusaoAbaSumario.observarEventoImpressaoSumarioAlta(this.altaSumario, this.altanListaOrigem, servidorLogado);
					imprimirReceituario();

					//					redireciona = 
						/*cupsFacade.raiseCupsEvent("sumarioAltaConcluido",
							this.altaSumario, this.altanListaOrigem, servidorLogado);*/
					//Geração da pendência de assinatura digital é realizada na chamada do método cupsFacade.raiseCupsEvent
				
				} else if (OBITO.equals(this.altanListaOrigem)) {
//					redireciona = 
						//cupsFacade.raiseCupsEvent("sumarioObitoConcluido",
							//this.altaSumario, this.altanListaOrigem, this.tipoImpressao);
					
					relatorioConclusaoAbaSumario.observarEventoImpressaoSumarioObito(this.altaSumario, this.altanListaOrigem, this.tipoImpressao);
					
					//Geração da pendência de assinatura digital é realizada na chamada do método cupsFacade.raiseCupsEvent
				
				} else if (ANTECIPAR_SUMARIO.equals(this.altanListaOrigem)) {
						this.cupsFacade.verificarCupsAtivo();
				}
				
				//TODO:
	   			relatorioConclusaoSumarioAltaController.setAtdSeq(altaSumario.getId().getApaAtdSeq());
	   			relatorioConclusaoSumarioAltaController.setApaSeq(altaSumario.getId().getApaSeq());
	   			relatorioConclusaoSumarioAltaController.setSeqp(altaSumario.getId().getSeqp());
	   			relatorioConclusaoSumarioAltaController.setAltaTipoOrigem(altanListaOrigem);
	   			relatorioConclusaoSumarioAltaController.setVoltarPara(voltarPara);
	   			relatorioConclusaoSumarioAltaController.inicio();
	   		
				returnValue = PAGE_PRESCRICAOMEDICA_MANTER_DIAGNOSTICOS_PACIENTE;
			} else {
				
				modalJustificativaLaudosController.setAtendimentoSeq(altaSumario.getId().getApaAtdSeq());
				modalJustificativaLaudosController.setApaSeq(altaSumario.getId().getApaSeq());
				modalJustificativaLaudosController.setSeqp(altaSumario.getId().getSeqp());
				modalJustificativaLaudosController.setAltaTipoOrigem(altanListaOrigem);
				modalJustificativaLaudosController.setVoltarPara(voltarPara);
				modalJustificativaLaudosController.getManterJustificativaLaudosController().setProntuario(altaSumario.getProntuario());
				modalJustificativaLaudosController.getManterJustificativaLaudosController().setPaciente(altaSumario.getPaciente());
				modalJustificativaLaudosController.getManterJustificativaLaudosController().setAtendimentoSeq(altaSumario.getId().getApaAtdSeq());
				
				returnValue = PAGE_MODAL_JUSTIFICATIVA_LAUDOS;
			}
			//#46129 - Após concluir, limpa os dados da aba pós alta.
			manterSumarioAltaPosAltaController.setAltaSumario(null);
		} catch (BaseListException e) {
			apresentarExcecaoNegocio(e);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch(OptimisticLockException e) {
			apresentarMsgNegocio(Severity.ERROR, "MPM_03735");
			return voltarPara;		
		} catch(IllegalArgumentException e){
			apresentarMsgNegocio(Severity.ERROR, "ERRO_CONCLUIR_SUMARIO_ALTA_OBITO");
		}
		
		return returnValue;
	}
	
	private void imprimirReceituario() throws BaseException {
		
		relatorioConclusaoAbaReceituario.observarEventoImpressaoSumarioAlta(this.altaSumario, this.altanListaOrigem, null);
	}

	/**
	 * Limpa todos os parâmetros/atributos do sumário
	 */
	protected void limparParametros(){
		this.altaSumario = null;
	}
	
	/**
	 * @param altaSumario
	 *            the altaSumario to set
	 */
	public void setAltaSumario(MpmAltaSumario altaSumario) {
		this.altaSumario = altaSumario;
	}

	/**
	 * @return the altaSumario2
	 */
	public MpmAltaSumario getAltaSumario() {
		return altaSumario;
	}

	/**
	 * @param altanListaOrigem
	 *            the altanListaOrigem to set
	 */
	public void setAltanListaOrigem(String altanListaOrigem) {
		this.altanListaOrigem = altanListaOrigem;
	}

	/**
	 * @return the altanListaOrigem
	 */
	public String getAltanListaOrigem() {
		return altanListaOrigem;
	}

	public String getTipoImpressao() {
		return tipoImpressao;
	}

	public void setTipoImpressao(String tipoImpressao) {
		this.tipoImpressao = tipoImpressao;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public RelatorioConclusaoSumarioAltaController getRelatorioConclusaoSumarioAltaController() {
		return relatorioConclusaoSumarioAltaController;
	}

	public void setRelatorioConclusaoSumarioAltaController(
			RelatorioConclusaoSumarioAltaController relatorioConclusaoSumarioAltaController) {
		this.relatorioConclusaoSumarioAltaController = relatorioConclusaoSumarioAltaController;
	}

	public ModalJustificativaLaudosController getModalJustificativaLaudosController() {
		return modalJustificativaLaudosController;
	}

	public void setModalJustificativaLaudosController(
			ModalJustificativaLaudosController modalJustificativaLaudosController) {
		this.modalJustificativaLaudosController = modalJustificativaLaudosController;
	}

	public RelatorioSumarioAltaController getRelatorioSumarioAltaController() {
		return relatorioSumarioAltaController;
	}

	public void setRelatorioSumarioAltaController(
			RelatorioSumarioAltaController relatorioSumarioAltaController) {
		this.relatorioSumarioAltaController = relatorioSumarioAltaController;
	}
}
