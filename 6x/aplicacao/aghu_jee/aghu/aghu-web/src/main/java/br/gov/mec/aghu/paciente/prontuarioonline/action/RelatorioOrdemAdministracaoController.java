package br.gov.mec.aghu.paciente.prontuarioonline.action;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.PdfUtil;

import com.itextpdf.text.pdf.PdfReader;


public class RelatorioOrdemAdministracaoController extends ActionController {
	private static final String ERRO_GERAR_RELATORIO = "ERRO_GERAR_RELATORIO";
	private static final long serialVersionUID = 1371123685440712182L;
	private static final Log LOG = LogFactory.getLog(RelatorioOrdemAdministracaoController.class);
	
	@Inject
	private SistemaImpressao sistemaImpressao;
	
	private String urlRelatorio;
	
	private String voltarPara;
	private ByteArrayOutputStream output;
	
	private boolean erroAoGerarRelatorio = false;

	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void inicio() {
		this.erroAoGerarRelatorio = false;
		if (StringUtils.isBlank(urlRelatorio)) {
			output = null;
			apresentarMsgNegocio(Severity.WARN, "AVISO_SISTEMA_CHEC_ELETRONICA_INDISPONIVEL");
		} else {
			output = this.buildResultadoPDF();
			if (!isErroAoGerarRelatorio()){
				try {
					PdfReader reader = new PdfReader(output.toByteArray());
					reader.close();
				} catch (IOException e) {
					String msgRetorno;
					this.erroAoGerarRelatorio = true;
					try {
						msgRetorno = output.toString("CP1252");
					} catch (UnsupportedEncodingException e1) {
						msgRetorno = null;
					}
					if (StringUtils.isNotBlank(msgRetorno) && msgRetorno.contains("body")) {
						msgRetorno = msgRetorno.replace("<body>", "").replace("</body>", "").trim();
						LOG.error(msgRetorno, e);
						this.erroAoGerarRelatorio = true;
						apresentarMsgNegocio(Severity.ERROR, "ERRO_USUARIO_NAO_AUTENTICADO_ECE");
					} else {
						LOG.error(e.getMessage(), e);
						this.erroAoGerarRelatorio = true;
						apresentarMsgNegocio(Severity.ERROR, ERRO_GERAR_RELATORIO);
					}
					output = null;
				}
			} else {
				//Caso Tenha ocorrido algum erro na construção do relatório (URL Inválida ou Sistema de Checagem não disponível)
				apresentarMsgNegocio(Severity.WARN, "AVISO_SISTEMA_CHEC_ELETRONICA_INDISPONIVEL");
			}
		}
	}
	
	private ByteArrayOutputStream buildResultadoPDF() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		HttpURLConnection httpConnection = null;
		
		try {
			URL url = new URL(this.urlRelatorio);
			httpConnection = (HttpURLConnection) url.openConnection();

			int responseCode = httpConnection.getResponseCode();

			if (responseCode == HttpURLConnection.HTTP_OK) {
				InputStream inputStream = httpConnection.getInputStream();
				IOUtils.copy(inputStream, out);
			}
		} catch (MalformedURLException me) {
			LOG.error("Exceção encontrada: URL do servidor está incorreta.", me);		
			this.erroAoGerarRelatorio = true;
		} catch (Exception e) {
			LOG.error("Exceção encontrada: Erro ao recuperar o relatório.", e);			
			this.erroAoGerarRelatorio = true;
		} finally {
			if (httpConnection != null) {
				httpConnection.disconnect();
			}
		}
		
		return out;
	}

	public StreamedContent getRenderPdf() {
		try {
			if (output != null) {
				return ActionReport.criarStreamedContentPdfPorByteArray(PdfUtil.protectPdf(output.toByteArray()).toByteArray());
			}
		} catch (IOException e) {
			String msgRetorno;
			try {
				msgRetorno = output.toString("CP1252");
			} catch (UnsupportedEncodingException e1) {
				msgRetorno = null;
			}
			if (StringUtils.isNotBlank(msgRetorno) && msgRetorno.contains("body")) {
				msgRetorno = msgRetorno.replace("<body>", "").replace("</body>", "").trim();
				LOG.error(msgRetorno, e);
				apresentarMsgNegocio(Severity.ERROR, "ERRO_USUARIO_NAO_AUTENTICADO_ECE");
			} else {
				LOG.error(e.getMessage(), e);
				apresentarMsgNegocio(Severity.ERROR, ERRO_GERAR_RELATORIO);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			apresentarMsgNegocio(Severity.ERROR, ERRO_GERAR_RELATORIO);
		}
		return null;
	}
	
	/**
	 * Impressão direta usando o CUPS.
	 * 
	 */
	public void directPrint() {
		try {
			ByteArrayOutputStream output = this.buildResultadoPDF();

			this.sistemaImpressao.imprimir(output, super.getEnderecoIPv4HostRemoto(), "RelatorioOrdemAdministracao");

			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			LOG.error(e.getMessage(), e);
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			this.apresentarMsgNegocio(Severity.ERROR, ERRO_GERAR_RELATORIO);
		}
	}
	
	public String voltar() {
		return voltarPara;
	}

	// Getters e Setters
	
	public SistemaImpressao getSistemaImpressao() {
		return sistemaImpressao;
	}

	public void setSistemaImpressao(SistemaImpressao sistemaImpressao) {
		this.sistemaImpressao = sistemaImpressao;
	}

	public String getUrlRelatorio() {
		return urlRelatorio;
	}

	public void setUrlRelatorio(String urlRelatorio) {
		this.urlRelatorio = urlRelatorio;
	}
	
	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public boolean isErroAoGerarRelatorio() {
		return erroAoGerarRelatorio;
	}


}