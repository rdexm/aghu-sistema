package br.gov.mec.aghu.core.report;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.EmptyReportException;
import br.gov.mec.aghu.core.exception.EmptyReportException.EmptyReportExceptionCode;

import com.itextpdf.text.DocumentException;

/**
 * Classe base especializada em gerar relatórios, encapsulando todos os métodos
 * necessários para esta tarefa.
 * 
 * Esta classe deve ser usada na camada de apresentação ou na camada de negócio
 * para obtenção de relatórios através da engine do jasper reports, seja para
 * apresentação em tela seja para envio direto para serviço de impressão ou
 * qualquer outro destino que se faça necessário.
 * 
 * @author geraldo
 * 
 */
public abstract class ReportGenerator implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3597510135169267755L;

	protected final String PARAMETRO_CAMINHO_RELATORIO = "PARAMETRO_CAMINHO_RELATORIO";

	protected DocumentoJasper documentoJasper;

	/**
	 * 
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 * 
	 * 
	 * @param out
	 * @param data
	 * @throws IOException
	 * @throws ApplicationBusinessException
	 * @throws JRException
	 * @throws SystemException
	 * @throws DocumentException
	 */
	public void renderPdf(OutputStream out, Object data) throws IOException,
			ApplicationBusinessException, JRException, SystemException,
			DocumentException {
		documentoJasper = gerarDocumento();

		out.write(documentoJasper.getPdfByteArray(false));
	}

	/**
	 * Retorna um DocumentoJasper preenchido com os dados fornecidos pelos
	 * métodos sobrescritos
	 * {@link MECRelatorioController#recuperarArquivoRelatorio()},
	 * {@link MECRelatorioController#recuperarParametros()} e
	 * {@link MECRelatorioController#recuperarColecao()}. Também executa o
	 * método {@link MECRelatorioController#executarPosGeracaoRelatorio(String)}
	 * .<br />
	 * Deste objeto pode ser obtidos JasperPrint, PDFs(protegidos ou não) e
	 * XLSs.
	 * 
	 * @see DocumentoJasper
	 * 
	 * @return
	 * @throws MECBaseException
	 */
	public final DocumentoJasper gerarDocumento(Map<String, Object> parametros)
			throws ApplicationBusinessException, EmptyReportException {
		String jasperPath = recuperarArquivoRelatorio();
		Collection<?> colecao = recuperarColecao();
		
		this.documentoJasper = new DocumentoJasper(jasperPath);
		this.documentoJasper.setParametros(recuperarParametrosComLocale());
		this.documentoJasper.setDados(colecao);

		if (colecao == null || colecao.isEmpty()) {
			throw new EmptyReportException(EmptyReportExceptionCode.MENSAGEM_RELATORIO_VAZIO);
		}
		
		if (parametros == null) {
			parametros = new HashMap<String, Object>();
		}
		parametros.put(PARAMETRO_CAMINHO_RELATORIO, jasperPath);

		this.executarPosGeracaoRelatorio(parametros);

		return this.documentoJasper;
	}

	/**
	 * Retorna um DocumentoJasper preenchido com os dados fornecidos pelos
	 * métodos sobrescritos
	 * {@link MECRelatorioController#recuperarArquivoRelatorio()},
	 * {@link MECRelatorioController#recuperarParametros()} e
	 * {@link MECRelatorioController#recuperarColecao()}. Também executa o
	 * método {@link MECRelatorioController#executarPosGeracaoRelatorio(String)}
	 * .<br />
	 * Deste objeto pode ser obtidos JasperPrint, PDFs(protegidos ou não) e
	 * XLSs.
	 * 
	 * @see DocumentoJasper
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public final DocumentoJasper gerarDocumento()
			throws ApplicationBusinessException, EmptyReportException {
		return gerarDocumento(new HashMap<String, Object>());
	}

	/**
	 * Método responsável por retornar os parâmetros utilizados no relatório e
	 * setar o locale.</br> Obtém os paramtros executando recuperarParametros()
	 * e, caso não venha um locale nos parâmetros, adiciona o locale ptBR.
	 * 
	 * @return o relatorio Jasper.
	 * @throws SystemException
	 */
	private Map<String, Object> recuperarParametrosComLocale() {
		Map<String, Object> params = recuperarParametros();
		if (params == null) {
			params = new HashMap<String, Object>();
		}
		Locale locale = null;
		try {
			locale = (Locale) params.get(JRParameter.REPORT_LOCALE);
		} catch (Exception e) {
			locale = null;
		}
		if (locale == null) {
			locale = new Locale("pt", "BR");
		}
		params.put(JRParameter.REPORT_LOCALE, locale);
		return params;
	}

	/**
	 * Método responsável por retornar os parâmetros utilizados no relatório.
	 * Esta implementação padrão naum inclui nenhum parâmetro e retorna null
	 * 
	 * @return o relatorio Jasper.
	 * @throws SystemException
	 */
	protected Map<String, Object> recuperarParametros() {
		return null;
	}

	/**
	 * 
	 * Método responsavel por definir a colecao utilizada nos relatórios.
	 * 
	 * @return
	 * @throws ApplicationBusinessException
	 */
	protected abstract Collection<?> recuperarColecao()
			throws ApplicationBusinessException;

	/**
	 * Método que executa após a geração de um relatório.
	 */
	protected abstract void executarPosGeracaoRelatorio(
			Map<String, Object> parametros) throws ApplicationBusinessException;

	/**
	 * Método responsável por retornar o caminho do relatório compilado do Jasper. Este
	 * metodo NÃO pode retornar NULL.
	 * 
	 * @return o caminho para o arquivo do relatorio Jasper
	 * @throws SystemException
	 */
	protected abstract String recuperarArquivoRelatorio();

}
