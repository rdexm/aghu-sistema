package br.gov.mec.aghu.exames.laudos;

import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;
import static net.sf.dynamicreports.report.builder.DynamicReports.concatenatedReport;
import static net.sf.dynamicreports.report.builder.DynamicReports.report;
import static net.sf.dynamicreports.report.builder.DynamicReports.stl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.builder.component.HorizontalListBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;
import net.sf.dynamicreports.report.constant.HorizontalAlignment;
import net.sf.dynamicreports.report.constant.LineStyle;
import net.sf.dynamicreports.report.constant.SplitType;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.commons.CoreUtil;

public class LaudoExterno extends LaudoMascara {

	private static final Log LOG = LogFactory.getLog(LaudoExterno.class);

	private static final String CONSTANTE_TODO_TESTE_LABORATORIAL = "\"Todo teste laboratorial"
			+ " deve ser correlacionado com o quadro clínico do paciente,"
			+ " sem o qual a interpretação do resultado é apenas relativa.\"";

	private static final String TEMPLATE_CONTATO = "CNPJ: %1$s - Telefone %2$s - Telefax %3$s - Caixa Postal %4$s";

	private List<JasperReportBuilder> reports = new ArrayList<JasperReportBuilder>();

	public void reportFooterConfig(ExamesListaVO exames, ExameVO exame) {

		HorizontalListBuilder horizontalList = cmp.horizontalList();

		// espaço entre detalhes e rodape
		horizontalList.add(cmp.verticalGap(10));
		horizontalList.newRow();

		horizontalList.add(cmp.text(CONSTANTE_TODO_TESTE_LABORATORIAL)
				.setHorizontalAlignment(HorizontalAlignment.CENTER)
				.setStyle(AbstractLaudo.STYLE_ARIAL_08_ITALIC));
		horizontalList.newRow();

		// Linha simples no topo do rodapé
		horizontalList.add(cmp.line());
		horizontalList.newRow();

		// GAP entre a margem superior
		horizontalList.add(cmp.verticalGap(20));

		// Acrescenta campo de texto na lista
		VerticalListBuilder verticalList = cmp.verticalList();
		verticalList.add(cmp.text(exame.getEnderecoContatos().getEndereco())
				.setHorizontalAlignment(HorizontalAlignment.CENTER)
				.setStyle(AbstractLaudo.STYLE_ARIAL_08_NORMAL));
		verticalList.add(cmp
				.text(String.format(TEMPLATE_CONTATO, exame
						.getEnderecoContatos().getCnpj(), exame
						.getEnderecoContatos().getFone(), exame
						.getEnderecoContatos().getFax(), exame
						.getEnderecoContatos().getCaixaPostal()))
				.setHorizontalAlignment(HorizontalAlignment.CENTER)
				.setStyle(AbstractLaudo.STYLE_ARIAL_08_NORMAL));
		verticalList.add(cmp
				.text("email: " + exame.getEnderecoContatos().getEmail()
						+ " - homepage: "
						+ exame.getEnderecoContatos().getHomepage())
				.setHorizontalAlignment(HorizontalAlignment.CENTER)
				.setStyle(AbstractLaudo.STYLE_ARIAL_08_NORMAL));

		horizontalList.add(verticalList);
		horizontalList.newRow(); // Nova linha

		// espaço sempre apos blocos
		horizontalList.add(cmp.verticalGap(20));

		// Seta rodapé
		getReport().pageFooter(horizontalList);

	}

	public void executar() throws IOException {

		Collections.sort(this.getExamesLista().getExames(),
				new Comparator<ExameVO>() {

					@Override
					public int compare(ExameVO e1, ExameVO e2) {
						String concat1 = e1.getUnidade()
								+ e1.getNomeMedicoSolicitante()
								+ e1.getConvenio();
						String concat2 = e2.getUnidade()
								+ e2.getNomeMedicoSolicitante()
								+ e2.getConvenio();
						return concat1.compareTo(concat2);
					}
				});

		ExameVO exameAnterior = null;
		for (ExameVO exame : this.getExamesLista().getExames()) {
			if ((exameAnterior != null && !this.grupoEquals(exame, exameAnterior))
					|| (exameAnterior != null && exame.getProntuario() != null && !exame.getProntuario().equals(exameAnterior.getProntuario()))
					|| (exameAnterior != null && exame.getOrigem() != null && !exame.getOrigem().equals(exameAnterior.getOrigem()))) {
				this.reports.add(this.createReport());
				this.reportHeaderConfig(this.getExamesLista(), exame);
				this.reportFooterConfig(this.getExamesLista(), exame);
			}
			if (exameAnterior == null) {
				this.reports.add(this.createReport());
				this.reportHeaderConfig(this.getExamesLista(), exame);
				this.reportFooterConfig(this.getExamesLista(), exame);
			}
			super.criarCabecalhoExame(exame);
			super.criarMensagemResultadoNaoLiberado(exame);
			super.processaMascaras(exame);
			super.criarRecebimentoLiberacao(exame);
			super.criarAssinaturaMedico(exame);
			super.criarInformacoesColeta(exame);
			super.criarInformacoesRespiracao(exame);
			super.criarNotasAdicionais(exame);
			super.criarAssinaturaEletronica(exame);
			super.criarLinha();

			exameAnterior = exame;
		}
	}

	/**
	 * Retorna true se o grupo unidade, medico solicitante e convenio forem
	 * iguais.
	 * 
	 * @param e1
	 * @param e2
	 * @return
	 */
	private boolean grupoEquals(ExameVO e1, ExameVO e2) {
		String concat1 = e1.getUnidade() + e1.getNomeMedicoSolicitante()
				+ e1.getConvenio();
		String concat2 = e2.getUnidade() + e2.getNomeMedicoSolicitante()
				+ e2.getConvenio();
		return concat1.equals(concat2);
	}
	
	private void reportHeaderConfig(ExamesListaVO lista, ExameVO exame) throws IOException {
		HorizontalListBuilder horizontalList = cmp.horizontalList();

		FileInputStream imagemLogo = defineLogoHospital();
		
		// Logotipo na direita do cabeçalho
		if (imagemLogo != null) {
			horizontalList.add(cmp.image(imagemLogo).setFixedDimension(
					96, 35));
		}

		// identificacao hospital, serviço, chefe do serviço e unidade
		VerticalListBuilder verticalList = cmp.verticalList();

		verticalList.add(cmp
				.text(exame.getServico()
						+ StringUtils.defaultString(exame.getRegistroConselhoServico() == null ? "" : " - " + exame.getRegistroConselhoServico()))
				.setHorizontalAlignment(HorizontalAlignment.CENTER)
				.setStyle(AbstractLaudo.STYLE_COURIER_10_NORMAL));

		verticalList.add(cmp.text(exame.getChefeServico())
				.setHorizontalAlignment(HorizontalAlignment.CENTER)
				.setStyle(AbstractLaudo.STYLE_COURIER_10_NORMAL));

		verticalList.add(cmp.text(exame.getUnidade())
				.setHorizontalAlignment(HorizontalAlignment.CENTER)
				.setStyle(AbstractLaudo.STYLE_COURIER_10_BOLD));

		horizontalList.add(verticalList);
		horizontalList.newRow();
		horizontalList.add(cmp.verticalGap(5));
		horizontalList.newRow();

		// Linha dupla no meio do cabeçalho
		horizontalList.add(cmp.line().setPen(
				stl.pen().setLineStyle(LineStyle.DOUBLE).setLineWidth(3f)));
		horizontalList.newRow();
//		horizontalList.add(cmp.verticalGap(15));

		// identificacao do paciente, medico e convenio
		VerticalListBuilder verticalListLeft = cmp.verticalList();
		verticalListLeft.add(cmp.verticalGap(5));
		verticalListLeft.add(cmp.text("Paciente: " + exame.getNomePaciente())
				.setHorizontalAlignment(HorizontalAlignment.LEFT)
				.setStyle(AbstractLaudo.STYLE_COURIER_10_BOLD));
		verticalListLeft.add(cmp
				.text("Emissão do laudo: "
						+ DateFormatUtils.format(Calendar.getInstance(),
								"dd/MM/yyyy"))
				.setHorizontalAlignment(HorizontalAlignment.LEFT)
				.setStyle(AbstractLaudo.STYLE_COURIER_10_NORMAL));
		// medico solicitante
		verticalListLeft.add(cmp
				.text("Dr(a) " + exame.getNomeMedicoSolicitante())
				.setHorizontalAlignment(HorizontalAlignment.LEFT)
				.setStyle(AbstractLaudo.STYLE_COURIER_10_NORMAL));

		horizontalList.add(verticalListLeft);
		//
		VerticalListBuilder verticalListRight = cmp.verticalList().setFixedWidth(180);
		verticalListRight.add(cmp.verticalGap(5));
		
		if (exame.getOrigem() != null) {
			verticalListRight
					.add(cmp.text(
							"Origem: "
									+ StringUtils.defaultString(exame.getOrigem()))
							.setHorizontalAlignment(HorizontalAlignment.LEFT)
							.setStyle(AbstractLaudo.STYLE_COURIER_10_NORMAL));
		} 
		if (exame.getProntuario() != null && exame.getProntuario() != 0){
			verticalListRight.add(cmp.text(
					"Prontuário: " + CoreUtil.formataProntuario(exame.getProntuario().toString()))
					.setHorizontalAlignment(HorizontalAlignment.LEFT)
					.setStyle(AbstractLaudo.STYLE_COURIER_10_NORMAL));
		} 
		
		verticalListRight.add(cmp
				.text("Convênio: "
						+ StringUtils.defaultString(exame.getConvenio()))
				.setHorizontalAlignment(HorizontalAlignment.LEFT)
				.setStyle(AbstractLaudo.STYLE_COURIER_10_NORMAL));
		horizontalList.add(verticalListRight);
		horizontalList.newRow();

		// numero da pagina
		horizontalList.add(cmp.pageNumber()
				.setFormatExpression(PATTERN_PAGINADOR)
				.setHorizontalAlignment(HorizontalAlignment.RIGHT)
				.setStyle(AbstractLaudo.STYLE_COURIER_10_NORMAL));

		// linha dupla no final do cabeçalho
		horizontalList.newRow();
		horizontalList.add(cmp.line().setPen(
				stl.pen().setLineStyle(LineStyle.DOUBLE).setLineWidth(3f)));
		horizontalList.newRow();

		// espaço sempre apos blocos
		horizontalList.add(cmp.verticalGap(10));

		this.report.pageHeader(horizontalList);
	}

	private JasperReportBuilder createReport() {
		this.detail = cmp.horizontalList(); // Instancia a lista horizontal
		// para o desenho dos resultados
		this.report = report();
		this.reportPageConfig();
		return this.report
				.detail(this.detail)
				.setDetailSplitType(SplitType.IMMEDIATE)
				.setDataSource(
						new JRBeanCollectionDataSource(Arrays
								.asList(new Object())));
	}

	@Override
	public void toPdf(OutputStream outputStream) throws DRException {
		JasperReportBuilder[] array = new JasperReportBuilder[this.reports
				.size()];
		array = this.reports.toArray(array);
		try {
			concatenatedReport().continuousPageNumbering().concatenate(array)
					.toPdf(outputStream);
		} catch (DRException e) {
			LOG.error("Erro ao exportar laudo para pdf", e);
		}

	}

}
