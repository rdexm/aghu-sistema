package br.gov.mec.aghu.exames.laudos;

import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;
import static net.sf.dynamicreports.report.builder.DynamicReports.margin;
import static net.sf.dynamicreports.report.builder.DynamicReports.stl;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.jasper.builder.export.JasperHtmlExporterBuilder;
import net.sf.dynamicreports.report.base.expression.AbstractSimpleExpression;
import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.builder.component.FillerBuilder;
import net.sf.dynamicreports.report.builder.component.HorizontalListBuilder;
import net.sf.dynamicreports.report.builder.component.TextFieldBuilder;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.Constants;
import net.sf.dynamicreports.report.constant.HorizontalAlignment;
import net.sf.dynamicreports.report.constant.LineStyle;
import net.sf.dynamicreports.report.constant.Markup;
import net.sf.dynamicreports.report.constant.PageType;
import net.sf.dynamicreports.report.constant.SplitType;
import net.sf.dynamicreports.report.definition.ReportParameters;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.utils.DateConstants;

/**
 * Abstração do laudo
 * 
 * @author aghu
 * 
 */
public abstract class AbstractLaudo implements ILaudoReport {

	private static final int LIMITE_RETRIES_TROCA_FONTE = 50;

	private static final Log LOG = LogFactory.getLog(AbstractLaudo.class);
	
	private static final String TITULO_NOTAS_ADICIONAIS = "Notas Adicionais";
	
	private static final String TITULO_INFORMACOES_COLETA = "OBSERVAÇÕES DO MOMENTO DA COLETA:";
	
	private static final String TITULO_RESULTADO_NAO_LIBERADO = "*** RESULTADO NÃO LIBERADO ***";

	/**
	 * TODO Transformar em ENUM. Estilos para fontes
	 */
	public static final StyleBuilder TEXT_STYLE;
	public static final StyleBuilder STYLE_COURIER_05_BOLD;
	public static final StyleBuilder STYLE_COURIER_05_NORMAL;
	public static final StyleBuilder STYLE_COURIER_08_BOLD;
	public static final StyleBuilder STYLE_COURIER_08_NORMAL;
	public static final StyleBuilder STYLE_COURIER_10_BOLD;
	public static final StyleBuilder STYLE_COURIER_10_NORMAL;
	public static final StyleBuilder STYLE_COURIER_12_BOLD;
	public static final StyleBuilder STYLE_COURIER_12_NORMAL;
	public static final StyleBuilder STYLE_COURIER_12_ITALIC;
	public static final StyleBuilder STYLE_COURIER_15_BOLD;
	public static final StyleBuilder STYLE_COURIER_15_NORMAL;

	public static final StyleBuilder STYLE_ARIAL_05_BOLD;
	public static final StyleBuilder STYLE_ARIAL_05_NORMAL;
	public static final StyleBuilder STYLE_ARIAL_08_BOLD;
	public static final StyleBuilder STYLE_ARIAL_08_NORMAL;
	public static final StyleBuilder STYLE_ARIAL_08_ITALIC;
	public static final StyleBuilder STYLE_ARIAL_10_BOLD;
	public static final StyleBuilder STYLE_ARIAL_10_NORMAL;
	public static final StyleBuilder STYLE_ARIAL_12_BOLD;
	public static final StyleBuilder STYLE_ARIAL_12_NORMAL;

	/**
	 * Padrão de paginação
	 */
	public static final String PATTERN_PAGINADOR = "Página {0}";

	/**
	 * Constantes utilizadas nas correções métricas do laudo
	 */
	public static final int CELL_HEIGHT = 12;
	public static final double PIXEL_AJUST = 0.7503152659;

	private List<String> fontsAlteradas = new ArrayList<String>();

	/**
	 * Inicializa constantes
	 */
	static {
		TEXT_STYLE = null;

		STYLE_COURIER_05_BOLD = stl.style().setFont(stl.fontCourierNew())
				.setFontSize(5).setBold(true);
		STYLE_COURIER_05_NORMAL = stl.style().setFont(stl.fontCourierNew())
				.setFontSize(5);
		STYLE_COURIER_08_BOLD = stl.style().setFont(stl.fontCourierNew())
				.setFontSize(8).setBold(true);
		STYLE_COURIER_08_NORMAL = stl.style().setFont(stl.fontCourierNew())
				.setFontSize(8);
		STYLE_COURIER_10_BOLD = stl.style().setFont(stl.fontCourierNew())
				.setFontSize(10).setBold(true);
		STYLE_COURIER_10_NORMAL = stl.style().setFont(stl.fontCourierNew())
				.setFontSize(10);
		STYLE_COURIER_12_BOLD = stl.style().setFont(stl.fontCourierNew())
				.setFontSize(12).setBold(true);
		STYLE_COURIER_12_ITALIC = stl.style().setFont(stl.fontCourierNew())
				.setFontSize(12).setItalic(true);
		STYLE_COURIER_12_NORMAL = stl.style().setFont(stl.fontCourierNew())
				.setFontSize(12);
		STYLE_COURIER_15_BOLD = stl.style().setFont(stl.fontCourierNew())
				.setFontSize(15).setBold(true);
		STYLE_COURIER_15_NORMAL = stl.style().setFont(stl.fontCourierNew())
				.setFontSize(15);

		STYLE_ARIAL_05_BOLD = stl.style().setFont(stl.fontArial())
				.setFontSize(5).setBold(true);
		STYLE_ARIAL_05_NORMAL = stl.style().setFont(stl.fontArial())
				.setFontSize(5);
		STYLE_ARIAL_08_BOLD = stl.style().setFont(stl.fontArial())
				.setFontSize(8).setBold(true);
		STYLE_ARIAL_08_NORMAL = stl.style().setFont(stl.fontArial())
				.setFontSize(8);
		STYLE_ARIAL_08_ITALIC = stl.style().setFont(stl.fontArial())
				.setFontSize(8).italic();
		STYLE_ARIAL_10_BOLD = stl.style().setFont(stl.fontArial())
				.setFontSize(10).setBold(true);
		STYLE_ARIAL_10_NORMAL = stl.style().setFont(stl.fontArial())
				.setFontSize(10);
		STYLE_ARIAL_12_BOLD = stl.style().setFont(stl.fontArial())
				.setFontSize(12).setBold(true);
		STYLE_ARIAL_12_NORMAL = stl.style().setFont(stl.fontArial())
				.setFontSize(12);

	}

	protected JasperReportBuilder report;
	protected HorizontalListBuilder detail;

	/**
	 * Construtor padrão
	 */
	public AbstractLaudo() {
	}

	public void reportPageConfig() {
		this.getReport().setLocale(new Locale("pt", "BR")); // Localidade
		this.getReport().setPageFormat(PageType.A4); // Tamanho A4
		this.getReport().setPageMargin(margin(20)); // Margem 20
	}

	public class DontBreakOnFirstPage extends AbstractSimpleExpression<Boolean> {
		private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;

		@Override
		public Boolean evaluate(ReportParameters reportParameters) {
			// return reportParameters.getPageRowNumber().intValue() == 1;
			return false;
		}
	}

	@Override
	public void toJrXml(OutputStream outputStream) throws DRException {
		this.getReport().toJrXml(outputStream);

	}

	@Override
	public void toPdf(OutputStream outputStream) throws DRException {
		this.getReport().toPdf(outputStream);

	}
	
	@Override
	public void toHtml(OutputStream outputStream) throws DRException {
		this.getReport().toHtml(outputStream);

	}
	
	@Override
	public void toHtml(JasperHtmlExporterBuilder exporter) throws DRException {
		this.getReport().toHtml(exporter);
		
	}

	@Override
	public void show() throws DRException {
		this.getReport().show();
	}

	/**
	 * Célula em branco
	 * 
	 * @param size
	 * @return
	 */
	public FillerBuilder addEmptyCell(int size) {
		FillerBuilder fillerBuilder = cmp.gap(
				(int) Math.round(size == 0 ? 1 : size * PIXEL_AJUST),
				CELL_HEIGHT);
		this.getDetail().add(fillerBuilder);
		return fillerBuilder;
	}

	/**
	 * Célula RTF
	 * 
	 * @param text
	 * @param size
	 * @return
	 */
	public TextFieldBuilder<String> addRtfCell(final String text, final int size) {
		StyleBuilder rtfStyle = stl.style().setMarkup(Markup.RTF);
		return addCell(text, size, 0, rtfStyle);
	}

	/**
	 * Célula HTML
	 * 
	 * @param text
	 * @param size
	 * @return
	 */
	public TextFieldBuilder<String> addXhtmlCell(final String text,
			final int size) {
		StyleBuilder xhtmlStyle = stl.style().setMarkup(Markup.HTML);
		return addCell(text, size, 0, xhtmlStyle);
	}

	/**
	 * Célula em texto puro
	 * 
	 * @param text
	 * @param size
	 * @return
	 */
	public TextFieldBuilder<String> addCell(final String text, final int size) {
		return addCell(text, size, 0, null);
	}

	/**
	 * Label estilizado
	 * 
	 * @param text
	 * @param size
	 * @param style
	 * @return
	 */
	public TextFieldBuilder<String> addCell(String text, int size,
			final int gap, StyleBuilder style) {

		size += 1;
		int ajustedSize = (int) Math.round(size == 0 ? 1 : size * PIXEL_AJUST);
		int ajustedGap = (int) Math.round(size == 0 ? 1 : gap * PIXEL_AJUST);
		
		//Caso o tamanho seja superior a 555, ocorre erro ao gerar o pdf.
		if (ajustedSize > 555){
			ajustedSize = 555;
		}
		
		if (text.startsWith("{\\rtf")) {
			if (style != null) {
				style.setMarkup(Markup.RTF);
			} else {
				style = stl.style().setMarkup(Markup.RTF);
			}
		}
		
		text = ajustaFonts(text,style.getStyle().getMarkup());

		TextFieldBuilder<String> label = cmp.text(text) //
				.setFixedWidth(ajustedSize);//
		label.setStyle(style);

		if (ajustedGap < 0) {
			LOG.warn("Gap menor que zero " + ajustedGap + " # " + text);
			ajustedGap = 0;
		}
		this.getDetail().add(ajustedGap, label);
		return label;
	}
	
	private String ajustaFonts(String text, Markup markup) {
		if (Markup.HTML.equals(markup) || Markup.RTF.equals(markup)){
			boolean retry = true;
			int retryCount = 0;
			while (retry && retryCount < LIMITE_RETRIES_TROCA_FONTE) {
				retry = false;
				JasperReportBuilder reporte = DynamicReports.report();
				HorizontalListBuilder detalhe = cmp.horizontalList();
				reporte.addDetail(detalhe);
				reporte.setDetailSplitType(SplitType.IMMEDIATE);
				reporte.setDataSource(new JRBeanCollectionDataSource(Arrays
						.asList(new Object())));
				TextFieldBuilder<String> label = cmp.text(text).setMarkup(markup);
				
				detalhe.add(label);
				
				try {
					OutputStream outputStream = new ByteArrayOutputStream();
					reporte.toPdf(outputStream);
				} catch (net.sf.jasperreports.engine.util.JRFontNotFoundException e) {
					LOG.warn("Erro na Font do relatorio "+e.getMessage());
					String regexErro = "Font '([\\w ]*)' is not available to the JVM. See the Javadoc for more details\\.";
					Pattern regex = Pattern.compile(regexErro);
					Matcher m = regex.matcher(e.getMessage());
					if (m.matches()){
						String font = m.group(1);
						String textAlterado = text.replace(font, stl.fontCourierNew().getFont().getFontName());
						if (textAlterado.equalsIgnoreCase(text)){
							String regexSubfont = "\\{\\\\f(\\d*)[\\\\|A-z|\\d]*\\{[\\s|A-z|\\\\|\\*]*;\\}[A-z|\\s]*;\\}";
							Pattern regexSub = Pattern.compile(regexSubfont);
							Matcher mSub = regexSub.matcher(text);
							if (mSub.find() || mSub.matches()){
								text = text.replaceFirst("\\{\\\\f(\\d*)[\\\\|A-z|\\d]*\\{[\\s|A-z|\\\\|\\*]*;\\}[A-z|\\s]*;\\}", "{\\\\f$1\\\\fnil\\\\fcharset0 Courier New;}");
							}
						} else {
							text= textAlterado;
						}
						fontsAlteradas.add(font);
						retry = true;
						retryCount++;
					}
				} catch (DRException e) {
					//possui outros erros de construcao do relatorio
					retry = false;
				}
			}
		}
		return text;
	}

	protected void criarLinha(){
		this.getDetail().newRow();
		this.getDetail().add(
				cmp.line().setPen(
						stl.pen().setLineStyle(LineStyle.SOLID)
								.setLineWidth(1f)));
		this.getDetail().newRow();
		this.getDetail().add(cmp.verticalGap(5));
		this.getDetail().newRow();
	}

	protected void criarCabecalhoExame(ExameVO exame) {
		Integer width = 400;
		if (exame.getListaRecebLiberacao() != null) {
			width = 300;
		}
		if (exame.isMostraDescricao()) {
			this.getDetail().add(
					cmp.text(exame.getNomeExame() != null ? exame.getNomeExame(): "")
							.setHorizontalAlignment(HorizontalAlignment.LEFT)
							.setStyle(AbstractLaudo.STYLE_COURIER_10_BOLD)
							.setFixedWidth(width)
							);
		}

		String text = "Solicitação: %1$s";
		
		if (exame.getListaRecebLiberacao() == null) {
			this.getDetail().add(
					cmp.text(String.format(text, exame.getSolicitacao()))
							.setHorizontalAlignment(HorizontalAlignment.RIGHT)
							.setStyle(AbstractLaudo.STYLE_COURIER_10_BOLD));
		}
		else {
			StringBuffer sb = new StringBuffer();
			for (String recebLiberacao : exame.getListaRecebLiberacao()) {
				String match = "Solicitação:";
				Integer start = recebLiberacao.indexOf(match);
				Integer end = (start + match.length() - 1);
				
				if (start != -1) {
					if (sb.length() != 0) {
						sb.append(" / ");
					}
					sb.append(recebLiberacao.substring(end+1, recebLiberacao.length()-1));
				}
			}
			
			this.getDetail().add(
					cmp.text(String.format(text, sb.toString()))
							.setHorizontalAlignment(HorizontalAlignment.RIGHT)
							.setStyle(AbstractLaudo.STYLE_COURIER_10_BOLD)
							.setFixedWidth(200));
		}

		this.getDetail().newRow();
		this.getDetail().add(cmp.verticalGap(10));

		this.getDetail().newRow();
	}

	protected void criarRecebimentoLiberacao(ExameVO exame) {
		this.getDetail().newRow();
		this.getDetail().add(cmp.verticalGap(10));
		this.getDetail().newRow();
		if (exame.getListaRecebLiberacao() == null) {
			this.getDetail().add(
					cmp.text(exame.getRecebimentoLiberacao())
							.setHorizontalAlignment(HorizontalAlignment.LEFT)
							.setStyle(AbstractLaudo.STYLE_COURIER_08_NORMAL));
			this.getDetail().newRow();
		}
		else {
			for (String recebLiberacao : exame.getListaRecebLiberacao()) {
				this.getDetail().add(
						cmp.text(recebLiberacao)
								.setHorizontalAlignment(HorizontalAlignment.LEFT)
								.setStyle(AbstractLaudo.STYLE_COURIER_08_NORMAL));
				this.getDetail().newRow();
			}
		}
		this.getDetail().add(cmp.verticalGap(10));
	}
	
	protected void criarAssinaturaMedico(ExameVO exame) {
		if (StringUtils.isNotEmpty(exame.getAssinaturaMedico())) {
			this.getDetail().newRow();
			this.getDetail().add(
					cmp.text(exame.getAssinaturaMedico())
							.setHorizontalAlignment(HorizontalAlignment.RIGHT)
							.setStyle(AbstractLaudo.STYLE_COURIER_08_NORMAL));
			this.getDetail().newRow();
			this.getDetail().add(cmp.verticalGap(10));
			this.getDetail().newRow();
		}
	}
	
	protected void criarAssinaturaEletronica(ExameVO exame) {
		if (StringUtils.isNotEmpty(exame.getAssinaturaEletronica())){
			this.getDetail().newRow();
			this.getDetail().add(
					cmp.text(exame.getAssinaturaEletronica())
							.setHorizontalAlignment(HorizontalAlignment.RIGHT)
							.setStyle(AbstractLaudo.STYLE_COURIER_12_ITALIC));
			this.getDetail().newRow();
			String conferencia = "Conferência por Vídeo";
			this.getDetail().add(
					cmp.text(conferencia)
							.setHorizontalAlignment(HorizontalAlignment.RIGHT)
							.setStyle(AbstractLaudo.STYLE_COURIER_08_NORMAL));
			this.getDetail().newRow();
			this.getDetail().add(cmp.verticalGap(10));
			this.getDetail().newRow();
		}
	}
	
	protected void criarNotasAdicionais(ExameVO exame) {
		if (exame.getNotas() != null && !exame.getNotas().isEmpty()) {
			this.detail.newRow();
			this.addCell(TITULO_NOTAS_ADICIONAIS, 300, 0,
					AbstractLaudo.STYLE_COURIER_10_BOLD);
		}

		for (NotaAdicionalVO nota : exame.getNotas()) {
			detail.newRow();
			addCell(DateFormatUtils.format(nota.getCriadoEm(),
					DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO), 130, 0,
					AbstractLaudo.STYLE_COURIER_10_NORMAL);
			addCell(nota.getCriadoPor(), 270, 10,
					AbstractLaudo.STYLE_COURIER_10_NORMAL);
			getDetail().newRow();
			addCell(nota.getNota(), 720, 10,
					AbstractLaudo.STYLE_COURIER_10_NORMAL);

		}
		this.detail.newRow();
		this.detail.add(cmp.verticalGap(10));
		this.detail.newRow();
	}
	
	protected void criarInformacoesColeta(ExameVO exame) {
		if (exame.getInformacoesColeta() != null && !exame.getInformacoesColeta().isEmpty()) {
			this.detail.newRow();
			this.addCell(TITULO_INFORMACOES_COLETA, 300, 0,
					AbstractLaudo.STYLE_COURIER_10_NORMAL);

			for (String infColeta : exame.getInformacoesColeta()) {
				getDetail().newRow();
				addCell(infColeta, 720, 10,
						AbstractLaudo.STYLE_COURIER_08_NORMAL);

			}
			this.detail.newRow();
			this.detail.add(cmp.verticalGap(10));
			this.detail.newRow();
		}
	}

	protected void criarInformacoesRespiracao(ExameVO exame) {
		if (exame.getInformacoesRespiracao() != null) {

			getDetail().newRow();
			addCell(exame.getInformacoesRespiracao() + ".", 720, 0,
					AbstractLaudo.STYLE_COURIER_08_BOLD);

			this.detail.newRow();
			this.detail.add(cmp.verticalGap(10));
			this.detail.newRow();
		}
	}
	
	protected void criarMensagemResultadoNaoLiberado(ExameVO exame) {

		if (exame.getLiberacao() == null) {

			getDetail().newRow();
			
			this.getDetail().add(
					cmp.text(TITULO_RESULTADO_NAO_LIBERADO)
							.setHorizontalAlignment(HorizontalAlignment.CENTER)
							.setStyle(AbstractLaudo.STYLE_COURIER_08_BOLD));
			
			this.detail.newRow();
			this.detail.add(cmp.verticalGap(10));
			this.detail.newRow();			
		}
	}


	public JasperReportBuilder getReport() {
		return report;
	}

	public void setReport(JasperReportBuilder report) {
		this.report = report;
	}

	public HorizontalListBuilder getDetail() {
		return detail;
	}

	public void setDetail(HorizontalListBuilder detail) {
		this.detail = detail;
	}

	@Override
	public List<String> getFontsAlteradas() {
		return fontsAlteradas;
	}

}
