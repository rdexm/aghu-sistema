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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.sf.dynamicreports.jasper.builder.JasperReportBuilder;
import net.sf.dynamicreports.report.builder.component.HorizontalListBuilder;
import net.sf.dynamicreports.report.builder.component.RectangleBuilder;
import net.sf.dynamicreports.report.builder.component.VerticalListBuilder;
import net.sf.dynamicreports.report.builder.style.ReportStyleBuilder;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.ComponentPositionType;
import net.sf.dynamicreports.report.constant.HorizontalAlignment;
import net.sf.dynamicreports.report.constant.LineStyle;
import net.sf.dynamicreports.report.constant.SplitType;
import net.sf.dynamicreports.report.constant.VerticalAlignment;
import net.sf.dynamicreports.report.exception.DRException;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.commons.CoreUtil;


public class LaudoSamis extends LaudoMascara {

	private static final Log LOG = LogFactory.getLog(LaudoSamis.class);
	
	private static final String TEMPLATE_CONTATO = "CNPJ: %1$s - Telefone %2$s - Telefax %3$s - Caixa Postal %4$s";

	private List<JasperReportBuilder> reports = new ArrayList<JasperReportBuilder>();

	public void reportFooterConfig(ExamesListaVO exames, ExameVO exame) throws IOException {

		HorizontalListBuilder horizontalList = cmp.horizontalList();

		// espaço entre detalhes e rodape
		horizontalList.add(cmp.verticalGap(10));
		horizontalList.newRow();

		// TODO incluir dados do paciente
		VerticalListBuilder verticalListLeft = cmp.verticalList();
		verticalListLeft.setFixedWidth(190);
		FileInputStream imagemLogo = defineLogoHospital();

		StyleBuilder style2 = stl.style().setRadius(5).setLeftPadding(5)
				.setTopPadding(5).setBottomPadding(5).setRightPadding(5);

		RectangleBuilder background2 = cmp.rectangle().setStyle(style2);
		verticalListLeft.setBackgroundComponent(background2);
		if (imagemLogo != null) {
			ReportStyleBuilder centralizado = stl.style().setAlignment(HorizontalAlignment.CENTER, VerticalAlignment.TOP).setTopPadding(5);
			verticalListLeft.add(cmp.image(imagemLogo)
					.setMinDimension(96, 34)
					.setHorizontalAlignment(HorizontalAlignment.CENTER).setPositionType(ComponentPositionType.FIX_RELATIVE_TO_TOP).setStyle(centralizado));
		}
		// verticalListLeft.add(cmp.roundRectangle());
		verticalListLeft.add(cmp.verticalGap(10));
		verticalListLeft.add(cmp.text("LAUDO DE EXAMES")
				.setHorizontalAlignment(HorizontalAlignment.CENTER)
				.setStyle(AbstractLaudo.STYLE_ARIAL_12_BOLD));
		horizontalList.add(verticalListLeft);

		// lado direito
		
		VerticalListBuilder verticalListRight = cmp.verticalList();
		verticalListRight.setFixedWidth(355);
		verticalListRight.add(cmp.verticalGap(5));
		verticalListRight.add(cmp.text(exame.getNomePaciente()).setStyle(stl.style(stl.font().bold()).setLeftPadding(2)).setFixedWidth(345));
		verticalListRight.add(cmp.text("Dr.(a) " + exame.getNomeMedicoSolicitante()).setFixedWidth(345));
		verticalListRight.add(cmp.text("Convênio: " + exame.getConvenio()).setFixedWidth(345));
		String prnt = null;
		if (exame.getProntuario() != null && exame.getProntuario() != 0) {
			prnt = CoreUtil.formataProntuario(exame.getProntuario().toString());
		} 
		
		HorizontalListBuilder linha = cmp.horizontalList();
		linha.setFixedWidth(355);
		linha.add(0,cmp.text(exame.getOrigem()).setFixedWidth(135).setHorizontalAlignment(HorizontalAlignment.LEFT));

		if (exames.getProntuarioMae() != null){
			linha.add(0,cmp.text(StringUtils.defaultString(prnt)).setFixedWidth(90).setHorizontalAlignment(HorizontalAlignment.CENTER));
			linha.add(0,cmp.text("Mãe: " + CoreUtil.formataProntuario(exames.getProntuarioMae().toString())).setFixedWidth(120).setHorizontalAlignment(HorizontalAlignment.RIGHT));
		} else {
			linha.add(0,cmp.text(StringUtils.defaultString(prnt)).setFixedWidth(90).setHorizontalAlignment(HorizontalAlignment.RIGHT));
		}
		
		
		
		verticalListRight.add(0,linha);

		
		verticalListRight.setBackgroundComponent(background2);

		horizontalList.add(cmp.gap(10, 10));
		horizontalList.add(verticalListRight);

		// espaço sempre apos blocos
		horizontalList.add(cmp.verticalGap(20));

		// Seta rodapé
		getReport().pageFooter(horizontalList);

	}

	private void adicionarEnderecoContatos(ExameVO exame,
			HorizontalListBuilder horizontalList) {
		
		String email = "";
		String homepage = "";
		String endereco = "";
		String cnpj = "";
		String fone = "";
		String fax = "";
		String caixaPostal = "";
		
		if(exame.getEnderecoContatos() != null){
			fone = exame.getEnderecoContatos().getFone();
			email =  exame.getEnderecoContatos().getEmail();
			homepage = exame.getEnderecoContatos().getHomepage();
			endereco = exame.getEnderecoContatos().getEndereco();
			cnpj = exame.getEnderecoContatos().getCnpj();
			fax = exame.getEnderecoContatos().getFax();
			caixaPostal = exame.getEnderecoContatos().getCaixaPostal();
		}
		
		if(cnpj == null) {
			cnpj = StringUtils.EMPTY;
		}
		if(fone == null) {
			fone = StringUtils.EMPTY;
		}
		if(fax == null) {
			fax = StringUtils.EMPTY;
		}
		if(caixaPostal == null) {
			caixaPostal = StringUtils.EMPTY;
		}
		if(email == null) {
			email = StringUtils.EMPTY;
		}
		if(homepage == null) {
			homepage = StringUtils.EMPTY;
		}
		
		// Acrescenta campo de texto na lista
		VerticalListBuilder verticalList = cmp.verticalList();
		verticalList.add(cmp.text(endereco)
				.setHorizontalAlignment(HorizontalAlignment.CENTER)
				.setStyle(AbstractLaudo.STYLE_ARIAL_05_NORMAL));
		verticalList.add(cmp
				.text(String.format(TEMPLATE_CONTATO, cnpj, fone, fax, caixaPostal))
				.setHorizontalAlignment(HorizontalAlignment.CENTER)
				.setStyle(AbstractLaudo.STYLE_ARIAL_05_NORMAL));
		verticalList.add(cmp
				.text("email: " + email+ " - homepage: "+ homepage)
				.setHorizontalAlignment(HorizontalAlignment.CENTER)
				.setStyle(AbstractLaudo.STYLE_ARIAL_05_NORMAL));

		horizontalList.add(verticalList);
		horizontalList.newRow(); // Nova linha
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
			if ( (exameAnterior != null && !this.grupoEquals(exame, exameAnterior))
					|| (exameAnterior != null && exame.getProntuario() != null && !exame.getProntuario().equals(exameAnterior.getProntuario()))
					|| (exameAnterior != null && exame.getOrigem() != null &&  !exame.getOrigem().equals(exameAnterior.getOrigem()))) {
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

	private void reportHeaderConfig(ExamesListaVO lista, ExameVO exame) {
		HorizontalListBuilder horizontalList = cmp.horizontalList();

		// identificacao hospital, serviço, chefe do serviço e unidade
		VerticalListBuilder verticalList = cmp.verticalList();

		verticalList.add(cmp
				.text(exame.getServico() == null ? "" : exame.getServico() 
						+ StringUtils.defaultString(exame.getRegistroConselhoServico() == null ? "" : " - " + exame.getRegistroConselhoServico()))
				.setHorizontalAlignment(HorizontalAlignment.CENTER)
				.setStyle(AbstractLaudo.STYLE_COURIER_12_NORMAL));

		verticalList.add(cmp.text(exame.getChefeServico() == null ? "" : exame.getChefeServico())
				.setHorizontalAlignment(HorizontalAlignment.CENTER)
				.setStyle(AbstractLaudo.STYLE_COURIER_12_NORMAL));

		verticalList.add(cmp.text(exame.getUnidade())
				.setHorizontalAlignment(HorizontalAlignment.CENTER)
				.setStyle(AbstractLaudo.STYLE_COURIER_12_BOLD));

		horizontalList.add(verticalList);
		horizontalList.newRow();
		this.adicionarEnderecoContatos(exame, horizontalList);
		horizontalList.add(cmp.verticalGap(10));
		horizontalList.newRow();

		// numero da pagina
		horizontalList.add(cmp.pageNumber().setFormatExpression(PATTERN_PAGINADOR)
				.setHorizontalAlignment(HorizontalAlignment.RIGHT)
				.setStyle(AbstractLaudo.STYLE_COURIER_10_NORMAL));
		horizontalList.newRow();

		horizontalList.add(cmp.line().setPen(
				stl.pen().setLineStyle(LineStyle.SOLID).setLineWidth(1f)));
		horizontalList.newRow();
		horizontalList.add(cmp.verticalGap(15));

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
			concatenatedReport().concatenate(array).continuousPageNumbering()
					.toPdf(outputStream);
		} catch (DRException e) {
			LOG.error("Erro ao exportar laudo para pdf.", e);
		}

	}

}
