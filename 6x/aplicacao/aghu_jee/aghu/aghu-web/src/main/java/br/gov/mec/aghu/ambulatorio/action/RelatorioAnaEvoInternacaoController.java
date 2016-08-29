package br.gov.mec.aghu.ambulatorio.action;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.vo.RelatorioAnaEvoInternacaoVO;
import br.gov.mec.aghu.core.commons.WebUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.mail.ContatoEmail;
import br.gov.mec.aghu.core.mail.EmailUtil;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;

public class RelatorioAnaEvoInternacaoController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {	
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
		 conversation.setTimeout(conversation.getTimeout()*2);
	}

	private static final Log LOG = LogFactory.getLog(RelatorioAnaEvoInternacaoController.class);

	private static final long serialVersionUID = 5772507490229358587L;
	
	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;	
	
	@EJB
	private IParametroFacade parametroFacade;

	@Inject
	private EmailUtil emailUtil;


	
	//dados do relatório de Ajuste de Estoque
	private List<?> dadosRelatorio = null;

	//nome do arquivo do relatório
	private String fileName;

	//indica se houve geração do arquivo do relatório
	private Boolean gerouArquivo;
	
	//tipo de relatorio a ser gerado
	private String tipoRelatorio;
	
	//sequence do atendimento
	private Integer atdSeq;
	
	//origem da chamada da pagina
	private String origem;
	
	private Date dataInicial;
	
	private Date dataFinal;
	
	private String titlePdfView;
	private Map<String, Object> parametrosEspecificos;
	private String nomeRelatorio;
	private String nomeRelatorioRodape;
	private String nomeArquivoRelatorio;
	private Boolean disableButtonCsv = Boolean.FALSE;
	

	private String tipoInternacao;
	private Integer seqInternacao;
	
	private enum EnumTargetRelatorioAnamneseEvolucaoInternacao {
		TITLE_RELATORIO_ANAMNESE_INTERNACAO, TITLE_RELATORIO_EVOLUCAO_INTERNACAO,	
		MENSAGEM_SUCESSO_IMPRESSAO, RELATORIO_ANAMNESE_EVOLUCAO_INTERNACAO_VISUALIZACAO; 
	}
	
	private enum EnumTargetRelatorioAnaEvoInternacao {
		RELATORIO_EVOLUCAO_TODOS, RELATORIO_EVOLUCAO_PERIODO, RELATORIO_ANAMNESES;
	}
	
	private enum RelatorioAnamneseEvolucaoInternacaoControllerExceptionCode implements BusinessExceptionCode {
		MENSAGEM_PESQUISA_SEM_EVOLUCAO_PERIODO, MENSAGEM_PESQUISA_SEM_DADOS; 
	}
	
	public void gerarDados() {
		setDadosRelatorio(null);
		
		// TODO AGHU_MIGRAÇÃO rever isso
		if (isRelatorioAnamnese()) {
			titlePdfView = WebUtil.initLocalizedMessage(EnumTargetRelatorioAnamneseEvolucaoInternacao.TITLE_RELATORIO_ANAMNESE_INTERNACAO.toString(),null);
		} else if (!isRelatorioAnamnese()) {
			titlePdfView =  WebUtil.initLocalizedMessage(EnumTargetRelatorioAnamneseEvolucaoInternacao.TITLE_RELATORIO_EVOLUCAO_INTERNACAO.toString(),null);					
		}
		try {
			setDadosRelatorio(ambulatorioFacade.pesquisarRelatorioAnaEvoInternacao(getAtdSeq(), getTipoRelatorio(), getDataInicial(), getDataFinal()));
			if (getDadosRelatorio() == null || getDadosRelatorio().isEmpty()) {
				String mensagem = "";
				if (EnumTargetRelatorioAnaEvoInternacao.RELATORIO_EVOLUCAO_PERIODO.toString().equals(tipoRelatorio)) {
					mensagem = RelatorioAnamneseEvolucaoInternacaoControllerExceptionCode.MENSAGEM_PESQUISA_SEM_EVOLUCAO_PERIODO.toString();
				} else {
					mensagem =  RelatorioAnamneseEvolucaoInternacaoControllerExceptionCode.MENSAGEM_PESQUISA_SEM_DADOS.toString();
				}					
				apresentarMsgNegocio(Severity.WARN, mensagem, new Object[0]);	
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	/**
	 * Renderiza o PDF
	 */
	public StreamedContent getRenderPdf() throws IOException, ApplicationBusinessException, JRException, SystemException, DocumentException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(true));
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/bancodesangue/report/AnamneseEvolucaoInternacao.jasper";
	}	
	
	public void directPrint(){
		try {
			DocumentoJasper documento = gerarDocumento();

			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());

			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
			
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}
	/**
	 * Recupera a coleção utilizada no relatório
	 */
	@Override
	public Collection<?> recuperarColecao() throws ApplicationBusinessException {
		return dadosRelatorio;
	}
	
	/**
	 * Retorna os parametros utilizados no relatorio
	 */
	@Override
	public Map<String, Object> recuperarParametros() {	
		Map<String, Object> params = new HashMap<String, Object>();
		if (getDadosRelatorioIsNotEmpty()) {
			RelatorioAnaEvoInternacaoVO param = (RelatorioAnaEvoInternacaoVO) getDadosRelatorio().get(0);
			try {
				params.put("caminhoLogo", recuperarCaminhoLogo2());
			} catch (BaseException e) {
				LOG.error("Erro ao tentar recuparar logotipo para o relatório",e);
			}			
			params.put("tipoRelatorio", recuperarTipoRelatorio());
			params.put("rodape", param.getRodape());
			params.put("responsavel", param.getResponsavel());
			params.put("agenda", param.getAgenda());
			params.put("paciente", param.getPaciente());
			params.put("prontuario", param.getProntuario());
			params.put("visto", param.getVisto());
		}
		return params;
	}
	
	private String recuperarTipoRelatorio() {
		String str = "";
		if (isRelatorioAnamnese()) {
			str = "ANAMNESE";
		} else if (!isRelatorioAnamnese()) {
			str = "EVOLUÇÃO";
		}
		return str;
	}
	
	private Boolean isRelatorioAnamnese() {
		Boolean verdadeiro = Boolean.TRUE;
		if (EnumTargetRelatorioAnaEvoInternacao.RELATORIO_ANAMNESES.toString().equals(tipoRelatorio)) {
			verdadeiro = Boolean.TRUE;
		} else if (EnumTargetRelatorioAnaEvoInternacao.RELATORIO_EVOLUCAO_TODOS.toString().equals(tipoRelatorio)
				|| EnumTargetRelatorioAnaEvoInternacao.RELATORIO_EVOLUCAO_PERIODO.toString().equals(tipoRelatorio)) {
			verdadeiro = Boolean.FALSE;
		}
		return verdadeiro;
	}
	
	public Boolean getDadosRelatorioIsNotEmpty() {
		return getDadosRelatorio() != null && !getDadosRelatorio().isEmpty();
	}
	
	public List<?> getDadosRelatorio() {
		return dadosRelatorio;
	}

	public void setDadosRelatorio(List<?> dadosRelatorio) {
		this.dadosRelatorio = dadosRelatorio;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Boolean getGerouArquivo() {
		return gerouArquivo;
	}

	public void setGerouArquivo(Boolean gerouArquivo) {
		this.gerouArquivo = gerouArquivo;
	}

	public String getTitlePdfView() {
		return titlePdfView;
	}

	public void setTitlePdfView(String titlePdfView) {
		this.titlePdfView = titlePdfView;
	}

	public Map<String, Object> getParametrosEspecificos() {
		return parametrosEspecificos;
	}

	public void setParametrosEspecificos(Map<String, Object> parametrosEspecificos) {
		this.parametrosEspecificos = parametrosEspecificos;
	}

	public String getNomeRelatorio() {
		return nomeRelatorio;
	}

	public void setNomeRelatorio(String nomeRelatorio) {
		this.nomeRelatorio = nomeRelatorio;
	}

	public String getNomeRelatorioRodape() {
		return nomeRelatorioRodape;
	}

	public void setNomeRelatorioRodape(String nomeRelatorioRodape) {
		this.nomeRelatorioRodape = nomeRelatorioRodape;
	}

	public String getNomeArquivoRelatorio() {
		return nomeArquivoRelatorio;
	}

	public void setNomeArquivoRelatorio(String nomeArquivoRelatorio) {
		this.nomeArquivoRelatorio = nomeArquivoRelatorio;
	}

	public Boolean getDisableButtonCsv() {
		return disableButtonCsv;
	}

	public void setDisableButtonCsv(Boolean disableButtonCsv) {
		this.disableButtonCsv = disableButtonCsv;
	}

	public String getTipoRelatorio() {
		return tipoRelatorio;
	}

	public void setTipoRelatorio(String tipoRelatorio) {
		this.tipoRelatorio = tipoRelatorio;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public String voltar() {
		return getOrigem();
	}
	
	public String getTipoInternacao() {
		return tipoInternacao;
	}

	public void setTipoInternacao(String tipoInternacao) {
		this.tipoInternacao = tipoInternacao;
	}

	public Integer getSeqInternacao() {
		return seqInternacao;
	}

	public void setSeqInternacao(Integer seqInternacao) {
		this.seqInternacao = seqInternacao;
	}
	
	

	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

	
	public void imprimirRelatorioEvolucaoTodos( Integer atdSeq, RapServidores servidorLogado, InetAddress endereco) {
		this.setTipoRelatorio("RELATORIO_EVOLUCAO_TODOS");
		this.setOrigem("CONSULTA_INTERNACAO_POL");
		this.setAtdSeq(atdSeq); 
		this.gerarDados();	
		
		try {
			DocumentoJasper documento = gerarDocumento();

			this.sistemaImpressao.imprimir(documento.getJasperPrint(), endereco);

		} catch (SistemaImpressaoException e) {
			LOG.error("Erro enviar relatório para impressão" ,e);
			this.enviarEmailErroGeracaoRelatorioAssincrono(e.getMessage(),servidorLogado.getEmail());
		} catch (BaseException e) {
			LOG.error("Erro ao agregar informações para geração do relatório", e);
			this.enviarEmailErroGeracaoRelatorioAssincrono(e.getMessage(),servidorLogado.getEmail());
		} catch (JRException e) {
			LOG.error("Erro ao gerar relatório" , e);
			this.enviarEmailErroGeracaoRelatorioAssincrono(e.getMessage(),servidorLogado.getEmail());
		}

	}

	private void enviarEmailErroGeracaoRelatorioAssincrono(String message, String emailUsuario) {
		
		if (emailUsuario == null){
			LOG.error("Usuario não possui email cadastrado");
			return;
		}

		String emailEnvioAdmAGHU = null;
		try {
			emailEnvioAdmAGHU = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_EMAIL_ENVIO_ADM_AGHU);
		} catch (ApplicationBusinessException e) {
			LOG.error(e.getMessage(), e);
		}
		
		ContatoEmail remetente = new ContatoEmail("sistema AGHU", emailEnvioAdmAGHU);
		ContatoEmail destinatario = new ContatoEmail(emailUsuario);

		emailUtil.enviaEmail(remetente, destinatario, null,
				"Erro na geração do relatório de evoluções",
				"favor contactar suporte do AGHU.  "+ message);
	}
}