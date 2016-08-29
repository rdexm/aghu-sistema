package br.gov.mec.aghu.prescricaomedica.action;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.weld.context.ConversationContext;
import org.jboss.weld.context.http.Http;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.ItemPrescricaoMedica;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmPrescricaoMedicaId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.constantes.EnumTipoImpressao;
import br.gov.mec.aghu.prescricaomedica.vo.PrescricaoMedicaVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.persistence.BaseEntity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;

public class RelatorioPrescricaoMedicaController extends ActionController {

	private static final Log LOG = LogFactory.getLog(RelatorioPrescricaoMedicaController.class);

	private static final long serialVersionUID = -5250443161592672819L;
	
	protected final String TIPO_IMPRESSAO = "TIPO_IMPRESSAO";

	private PrescricaoMedicaVO prescricaoMedicaVO;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	@Inject
	private RelatorioPrescricaoMedicaReportGenerator report;

	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@Inject @Http 
	private ConversationContext conversationContext;

	
	private Date dataMovimento;
	
	private List<ItemPrescricaoMedica> itensDaPrescricaoConfirmacao;

	private List<ItemPrescricaoMedica> itensConfirmados;

	private EnumTipoImpressao tipoImpressao;

	private MpmPrescricaoMedica prescricao;

	protected Boolean imprimirRelatorioItensConfirmados = true;

	private RapServidores servidorValido;
	
	private final String  PAGE_VOLTAR_LIST = "prescricaomedica-verificaPrescricaoMedica";
	
	private String paginaOrigem;
	
	private String voltarPara;
	
	//@In(required = false)
	private List<MpmPrescricaoMedica> prescricoesMedicas;
	//@In(required = false)
	private Integer seqPrescricaoMedicaSelecionada;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		conversationContext.setConcurrentAccessTimeout(900000000000l);
	}
	
	/**  Carregar as informações da prescrição conforme o tipo de impressão selecionado pelo usuário */
	public void init() {

		MpmPrescricaoMedica prescricaoMedica =  prescricaoMedicaFacade.obterPrescricaoComAtendimentoPaciente(prescricaoMedicaVO.getPrescricaoMedica().getId().getAtdSeq(),
				prescricaoMedicaVO.getPrescricaoMedica().getId().getSeq()); //prescricaoMedicaVO.getPrescricaoMedica();

		
		this.setPrescricao(prescricaoMedica);
				
		// Quando tipo for SEM_IMPRESSAO carrega os dados para gerar pendência para certificação eletrônica
		if (tipoImpressao == EnumTipoImpressao.REIMPRESSAO
				|| ((tipoImpressao == EnumTipoImpressao.IMPRESSAO || tipoImpressao == EnumTipoImpressao.SEM_IMPRESSAO) && servidorValido == null)) {

			itensDaPrescricaoConfirmacao = prescricaoMedicaFacade.listarItensPrescricaoMedicaConfirmados(this.prescricao);

		// Quando tipo for SEM_IMPRESSAO carrega os dados para gerar pendência para certificação eletrônica
		} else if ((tipoImpressao == EnumTipoImpressao.IMPRESSAO || tipoImpressao == EnumTipoImpressao.SEM_IMPRESSAO) && servidorValido != null) {
			itensDaPrescricaoConfirmacao = itensConfirmados;
		}

		if (itensDaPrescricaoConfirmacao == null || itensDaPrescricaoConfirmacao.isEmpty()) {
			this.imprimirRelatorioItensConfirmados = false;
		}
		
		report.setPrescricao(prescricaoMedica);
		report.setLogin(obterLoginUsuarioLogado());
		report.setServidorValida(servidorValido);
		report.setTipoImpressao(tipoImpressao);
		report.setImpressaoTotal(true);
		report.setDataMovimento(dataMovimento);
		report.setItensDaPrescricaoConfirmacao(itensDaPrescricaoConfirmacao);
	}

	/**
	 * Método que faz a carga dos dados iniciais para geração do relatório.
	 */
	public String print(Integer idAtendimento, Integer seqPrescricao) throws BaseException, JRException, SystemException, IOException {
		MpmPrescricaoMedica prescricao = this.prescricaoMedicaFacade.obterPrescricaoMedicaPorId( new MpmPrescricaoMedicaId(idAtendimento,seqPrescricao) );
		this.setPrescricao(prescricao);

		return "verEsseRedirectDepois";
	}

//	@Observer("reimpressaoPrescricao")
	public void observarEventoReimpressaoPrescricao() throws BaseException, JRException, SystemException, IOException {
		// impressaoTotal = this.verificarReimpressao(tipoImpressao);
		this.observarEventoImpressaoPrescricaoConfirmada(false);
	}
	
	/**
	 * Método executado através do evento 'prescricaoConfirmada' que realiza a impressão do relatório de prescrição médica
	 */
	public void observarEventoImpressaoPrescricaoConfirmada() throws BaseException, JRException, SystemException, IOException {
		observarEventoImpressaoPrescricaoConfirmada(false);
	}
	
//	@Observer("prescricaoConfirmada")
	public void observarEventoImpressaoPrescricaoConfirmada(boolean contraCheque) throws BaseException, JRException, SystemException, IOException {

		init();
		report.setContraCheque(contraCheque);

		if (itensDaPrescricaoConfirmacao != null && !itensDaPrescricaoConfirmacao.isEmpty()) {

			DocumentoJasper documento = report.observarEventoImpressaoPrescricaoConfirmada(tipoImpressao);

			// Quando tipo for SEM_IMPRESSAO não imprime o relatório fisicamente
			if (!EnumTipoImpressao.SEM_IMPRESSAO.equals(tipoImpressao)) {
				
				try {
					this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());

					apresentarMsgNegocio( Severity.INFO,"MENSAGEM_SUCESSO_IMPRESSAO_RELATORIO","Prescrição Médica");
					
				} catch (SistemaImpressaoException e) {
					apresentarExcecaoNegocio(e);
					
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
					apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
				}
			}			
		}
	}

	public StreamedContent getRenderPdf()  throws IOException, JRException, SystemException, DocumentException, ApplicationBusinessException {
		DocumentoJasper documento = report.gerarDocumento(Boolean.TRUE);
		return ActionReport.criarStreamedContentPdfPorByteArray(documento.getPdfByteArray(true));
	}

	public StreamedContent getRenderPdfMultiPresc() throws IOException, BaseException, JRException, SystemException, DocumentException {
		/*Integer seqPresc = (Integer) data;
		seqPrescricaoMedicaSelecionada = seqPresc;
		
		for(MpmPrescricaoMedica pres : prescricoesMedicas){
			if(pres.getId().getSeq().equals(seqPresc)){
				prescricaoMedicaVO.setPrescricaoMedica(pres);
				break;
			}
		}*/
		init();
		report.getRenderPdfMultiPresc();
		DocumentoJasper documento = report.getDocumentoJasper();
		return ActionReport.criarStreamedContentPdfPorByteArray(documento.getPdfByteArray(false));
	}
	
	/**
	 * Método executado através do evento 'imprimirContraCheque' que realiza a
	 * impressão do contracheque da prescrição médica.
	 */
//	@Observer("imprimirContraCheque")
	public void imprimirContraCheque() throws BaseException, JRException, SystemException, IOException {
		this.observarEventoImpressaoPrescricaoConfirmada(true);
	}
	
	public StreamedContent getRenderPdfContraCheque() throws IOException, BaseException, JRException, SystemException, DocumentException {
		init();
		report.getRenderPdfContraCheque();
		DocumentoJasper documento = report.getDocumentoJasper();
		return ActionReport.criarStreamedContentPdfPorByteArray(documento.getPdfByteArray(false));
	}

	public String voltarList(){
		return PAGE_VOLTAR_LIST;
	}
	
	public String voltar(){
		return voltarPara;
	}

	public MpmPrescricaoMedica getPrescricao() {
		return prescricao;
	}

	public void setPrescricao(MpmPrescricaoMedica prescricao) {
		this.prescricao = prescricao;
	}

	public List<ItemPrescricaoMedica> getItensDaPrescricaoConfirmacao() {
		return itensDaPrescricaoConfirmacao;
	}

	public void setItensDaPrescricaoConfirmacao(List<ItemPrescricaoMedica> itensDaPrescricaoConfirmacao) {
		this.itensDaPrescricaoConfirmacao = itensDaPrescricaoConfirmacao;
	}
	
	protected BaseEntity getEntidadePai() {
		return this.prescricaoMedicaVO.getPrescricaoMedica().getAtendimento();
	} 
	
	public EnumTipoImpressao getTipoImpressao() {
		return tipoImpressao;
	}

	public void setTipoImpressao(EnumTipoImpressao tipoImpressao) {
		this.tipoImpressao = tipoImpressao;
	}

	public PrescricaoMedicaVO getPrescricaoMedicaVO() {
		return prescricaoMedicaVO;
	}

	public void setPrescricaoMedicaVO(PrescricaoMedicaVO prescricaoMedicaVO) {
		this.prescricaoMedicaVO = prescricaoMedicaVO;
	} 

	public RapServidores getServidorValido() {
		return servidorValido;
	}

	public void setServidorValido(RapServidores servidorValido) {
		this.servidorValido = servidorValido;
	}

	public RelatorioPrescricaoMedicaReportGenerator getReport() {
		return report;
	}

	public void setReport(RelatorioPrescricaoMedicaReportGenerator report) {
		this.report = report;
	}

	public void setItensConfirmados(List<ItemPrescricaoMedica> itensConfirmados) {
		this.itensConfirmados = itensConfirmados;
	}
	
	public List<ItemPrescricaoMedica>  getItensConfirmados(){
		return this.itensConfirmados;
	}

	public void setDataMovimento(Date dataMovimento) {
		this.dataMovimento = dataMovimento;
	}

	public Date getDataMovimento() {
		return dataMovimento;
	}
	
	public List<MpmPrescricaoMedica> getPrescricoesMedicas() {
		return prescricoesMedicas;
	}

	public Integer getSeqPrescricaoMedicaSelecionada() {
		return seqPrescricaoMedicaSelecionada;
	}

	public void setPrescricoesMedicas(List<MpmPrescricaoMedica> prescricoesMedicas) {
		this.prescricoesMedicas = prescricoesMedicas;
	}

	public void setSeqPrescricaoMedicaSelecionada(
			Integer seqPrescricaoMedicaSelecionada) {
		this.seqPrescricaoMedicaSelecionada = seqPrescricaoMedicaSelecionada;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public String getPaginaOrigem() {
		return paginaOrigem;
	}

	public void setPaginaOrigem(String paginaOrigem) {
		this.paginaOrigem = paginaOrigem;
	}
}