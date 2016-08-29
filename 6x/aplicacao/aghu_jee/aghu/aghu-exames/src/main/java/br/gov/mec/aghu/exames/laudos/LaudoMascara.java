package br.gov.mec.aghu.exames.laudos;

import static net.sf.dynamicreports.report.builder.DynamicReports.cmp;
import static net.sf.dynamicreports.report.builder.DynamicReports.stl;

import java.awt.Font;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.sf.dynamicreports.report.base.style.DRFont;
import net.sf.dynamicreports.report.builder.DynamicReports;
import net.sf.dynamicreports.report.builder.style.FontBuilder;
import net.sf.dynamicreports.report.builder.style.StyleBuilder;
import net.sf.dynamicreports.report.constant.HorizontalAlignment;
import net.sf.dynamicreports.report.constant.Markup;
import net.sf.dynamicreports.report.constant.SplitType;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;

import br.gov.mec.aghu.core.commons.ConfigurationUtil;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class LaudoMascara extends AbstractLaudo {
	
	private static final Log LOG = LogFactory.getLog(LaudoMascara.class);

	/**
	 *  Ajuste necessario no GAP quando o tamanho do componente eh reajustado para o tamanho visual do seu valor
	 */
	private static final int AJUSTE_GAP = 3;

	/**
	 *  Espaco em branco que sobre em um campo da mascara, podendo ser desprezado para melhor funcionamento da montagem do laudo
	 */
	private static final int LACUNA_MASCARA = 50;

	/**
	 *  Espaco adicionada juntamente com cada componente da mascara, para evitar aglomeracoes
	 */
	private static final int ESPACO = 1;

	/**
	 *  Tamanho maximo de uma linha, sem ajuste de pixels
	 */
	public static final int TAMANHO_MAX = 735;
	
	/**
	 * Altura minima texto longo
	 */
	public static final Short ALTURA_MIN_TEXTO_LONGO = 15;

	private static final String TEMPLATE_LOG = "%1$s Processa mascara para solicitacao=%2$s, item=%3$s, %4$s-%5$s";
	
	protected ExamesListaVO examesLista;

	protected String caminhoLogo;

	public LaudoMascara() {
		this.report = DynamicReports.report();
		this.detail = cmp.horizontalList();
		this.report.detail(this.detail);
		this.report.setDetailSplitType(SplitType.IMMEDIATE);
		this.report.setDataSource(new JRBeanCollectionDataSource(Arrays
				.asList(new Object())));
		this.reportPageConfig();
	}

	@SuppressWarnings("PMD.NPathComplexity")
	protected void processaMascaras(ExameVO exame)  {

		LOG.debug(String.format(TEMPLATE_LOG, "BEGIN", exame.getSolicitacao(),
				exame.getItem(), exame.getSigla(), exame.getDescricao()));

		List<MascaraVO> mascaras = removeCamposVazios(exame.getMascaras());

		this.calcularQuebraLinha(mascaras);

		// gap é o espaço entre os campos na mesma linha
		int gap = 0;
		// somatorio do tamanho da linha, acrescentado a cada campo
		int tamanhoLinha = 0;
		// controle se o tamanho do componente foi ajustado de acordo com o tamanho da string do valor
		boolean ajustado = false;
		MascaraVO mAnterior = null;
		String valorSemTags = "";
		
		for (MascaraVO m : mascaras) {
			LOG.debug("\t BEGIN Campo");
			if (!m.isExibeRelatorio() || m.getValor() == null) {
				LOG.debug("\t END Campo");
				continue;
			}

			LOG.debug("Linha=" + m.getLinha());
			LOG.debug("LarguraObjetoVisual=" + m.getLarguraObjetoVisual());

			if (m.isQuebraLinha()) {
				// calculo com ultimo campo da linha define tamanho da linha
				int tamanhoLinhaAnterior = (mAnterior != null ? (mAnterior
						.getPosicaoColunaImpressao() + mAnterior
						.getLarguraObjetoVisual()) : 0);
				LOG.debug("Tamanho da linha anterior = " + tamanhoLinhaAnterior);
				if (tamanhoLinhaAnterior > 740) {
					LOG.debug("Provavelmente ocorrerá erro na geração do relatório "
							+ "devido ao tamanho de linha excedido. Verifique "
							+ "campo na mascara do laudo com tamanho >= 740"
							+ "(aproximadamente) " + m.getId());

				}
				tamanhoLinha = 0;
				gap = 0;
				this.getDetail().newRow();
			}

			StyleBuilder style2 = this.montaEstiloCampo(m);

			// colocar atributo para facilitar debig
			// style2.setBorder(stl.pen1Point());

			// LOG.debug(m.isQuebraLinha() + " # " + m.getValor() + " # "
			// + m.getLarguraObjetoVisual());

			// calculo do gap
			if (m.isQuebraLinha()) {
				gap = m.getPosicaoColunaImpressao();
				LOG.debug("Calculo do gap " + m.getPosicaoColunaImpressao()
						+ " = " + gap);
				// se quebrar a linha pode ignorar o ajuste de gap
				ajustado = false;
			} else if (mAnterior != null && !m.isQuebraLinha()) {
				gap = m.getPosicaoColunaImpressao()
						- mAnterior.getPosicaoColunaImpressao()
						- mAnterior.getLarguraObjetoVisual();
				LOG.debug("Calculo do gap " + m.getPosicaoColunaImpressao()
						+ " - " + mAnterior.getPosicaoColunaImpressao() + " -"
						+ mAnterior.getLarguraObjetoVisual() + " = " + gap);
				// Ajusta o gap caso o componente anterior tenha sido ajustado
				if(ajustado) {
					gap += AJUSTE_GAP;
					ajustado = false;
				}
			}

			LOG.debug("gap=" + gap);
			if (gap < 0){
				gap = 0;
				LOG.debug("ATENÇÃO: Campo do laudo se sobrepoe a outro, não será adicionado ao relatório. " + m);
			}
			
			
			if(m.getValor() != null){
				valorSemTags = retornaValorSemTag(m.getValor());
			}
			
			// Calcula o tamanho visual da string do valor, se for muito menor (?50?) que o valor da mascara então o valor calculado é utilizado
			DRFont df = style2.getStyle().getFont();
			FontRenderContext context = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB).createGraphics().getFontRenderContext();
			Font fnt  = new Font(df.getFontName(), Font.PLAIN, df.getFontSize());
			double tamanhoVisualDoValor = valorSemTags.length() * fnt.getMaxCharBounds(context).getWidth();//fnt.getStringBounds(m.getValor(), context).getWidth();
			int tamanhoAjustado = (int) Math.round(tamanhoVisualDoValor/PIXEL_AJUST);
			LOG.debug("Tamanho original "+m.getLarguraObjetoVisual()+" Tamanho calculado " + tamanhoAjustado);
			
			//TODO verificar se este numero (50) é muito grande para ser parametro de espaco sobrando
			if (m.getLarguraObjetoVisual()-tamanhoAjustado > LACUNA_MASCARA){
				LOG.debug("Alterando tamanho original pelo calculado para evitar falsos positivos no calculo de gaps.");
				m.setLarguraObjetoVisual((short)(tamanhoAjustado));
				ajustado = true;
			} else if ((m.getLarguraObjetoVisual()-tamanhoAjustado < LACUNA_MASCARA) && (tamanhoAjustado < TAMANHO_MAX)) {
				LOG.debug("Alterando tamanho original pelo calculado para evitar quebra de linha de strings com LarguraObjetoVisual menor que o tamanho visual.");
				m.setLarguraObjetoVisual((short)(tamanhoAjustado));
				ajustado = true;
			}
			
			// calculo de overflow das linhas, caso ocorra serah gerada uma nova linha
			int overflow = ((m.getLarguraObjetoVisual() + gap + tamanhoLinha + ESPACO) - TAMANHO_MAX);
			LOG.debug("Calculo do overflow da linha:\n"
					+ " ( "+ m.getLarguraObjetoVisual() +"+"+ gap +"+" +tamanhoLinha+ "+" + ESPACO + " ) -" + TAMANHO_MAX);
			LOG.debug("overflow=" + overflow);
			if (overflow > 0){
				LOG.debug("Tratamento de overflow");
				if (gap > overflow) {
					LOG.debug("Overflow serah retirado do GAP");
					gap = gap - overflow;
				} else {
					LOG.debug("Criada nova linha para nao causar erro por overflow");
					if ((m.getPosicaoColunaImpressao() + m.getLarguraObjetoVisual()) < TAMANHO_MAX){
						gap = m.getPosicaoColunaImpressao();
					}  else {
						gap = m.getPosicaoColunaImpressao() - ((m.getPosicaoColunaImpressao() + m.getLarguraObjetoVisual()) - TAMANHO_MAX);
					}
					tamanhoLinha = 0;
					this.getDetail().newRow();
				}
			}
			this.addCell(valorSemTags, m.getLarguraObjetoVisual(), gap, style2);
			tamanhoLinha += gap + m.getLarguraObjetoVisual() + ESPACO;
			mAnterior = m;
			LOG.debug("\t END Campo");
		}

		// espaço sempre após blocos
		this.getDetail().add(cmp.verticalGap(20));

		LOG.debug(String.format(TEMPLATE_LOG, "END", exame.getSolicitacao(),
				exame.getItem(), exame.getSigla(), exame.getDescricao()));
		
		processarVlrRef(exame);
		
	}

	private String retornaValorSemTag(String textoLivre) {
		StringBuffer retorno = null;
		try {
			if (textoLivre != null && StringUtils.isNotBlank(textoLivre)) {
				textoLivre = "<root>" + CoreUtil.converterRTF2Text(textoLivre.trim()) + "</root>";
				textoLivre = textoLivre.replaceAll("&", "#");
				
				textoLivre = Jsoup.parse(textoLivre).text();
				retorno = new StringBuffer();
				
				retorno.append(textoLivre);
			}
		} catch (Exception e) {
			return textoLivre;
		}

		return (retorno != null) ? StringEscapeUtils.unescapeHtml4(retorno.toString().replaceAll("#", "&")).replace("<root>", "").replace("</root>", "") : "";
	}
	
	
	private List<MascaraVO> removeCamposVazios(List<MascaraVO> mascaras){
		List<MascaraVO> lista = new ArrayList<MascaraVO>();
		for (MascaraVO m : mascaras) {
			if (m.isExibeRelatorio() && m.getValor() != null) {
				lista.add(m);				
			}
		}
		return lista;
	}

	private void calcularQuebraLinha(List<MascaraVO> mascaras) {
		primeiraOrdenacao(mascaras);
		// primeiraOrdenacao2();
		// for (MascaraVO v : mascaras) {
		// System.out.println(v);
		// }

		// TODO FALTA VERIFICAR QDO A QUEBRA FOR MAIS DE UMA LINHA

		// normaliza posicao da linha
		// se a diferenca da posicao y entre os campos for < ENTRE_LINHAS
		// significa que estão na mesma linha
		int linha = 1;
		for (MascaraVO mascara : mascaras) {
			int min = mascara.getPosicaoLinhaImpressao()
					- MascaraVO.ENTRE_LINHAS;
			int max = mascara.getPosicaoLinhaImpressao()
					+ MascaraVO.ENTRE_LINHAS;
			for (MascaraVO m : mascaras) {
				if (m.getLinha() == null && m.getPosicaoLinhaImpressao() >= min
						&& m.getPosicaoLinhaImpressao() <= max) {
					m.setLinha(linha);
				}
			}

			linha++;
		}

		// ordena para calcular quebra
		Collections.sort(mascaras);
		Integer linhaAnterior = Integer.valueOf(0);
		for (MascaraVO mascara : mascaras) {
			mascara.setQuebraLinha(linhaAnterior != mascara.getLinha());

			linhaAnterior = mascara.getLinha();
		}

	}

	/**
	 * Ordena a lista por posicao linha e posicao coluna.
	 * 
	 * @param mascaras
	 */
	private void primeiraOrdenacao(List<MascaraVO> mascaras) {
		// ordena para normalizar numero da linha
		Collections.sort(mascaras, new Comparator<MascaraVO>() {

			@Override
			public int compare(MascaraVO o1, MascaraVO o2) {
				if (o1.getPosicaoLinhaImpressao().equals(
						o2.getPosicaoLinhaImpressao())) {
					return o1.getPosicaoColunaImpressao().compareTo(
							o2.getPosicaoColunaImpressao());
				}

				return o1.getPosicaoLinhaImpressao().compareTo(
						o2.getPosicaoLinhaImpressao());
			}
		});
	}

	@Override
	public void setExamesLista(ExamesListaVO examesLista) {
		this.examesLista = examesLista;
	}

	@Override
	public ExamesListaVO getExamesLista() {
		return this.examesLista;
	}

	public void executar() throws FileNotFoundException, IOException{

		for (ExameVO exame : this.getExamesLista().getExames()) {
			this.criarCabecalhoExame(exame);
			processaMascaras(exame);
			super.criarLinha();
		}
	}
	
	private StyleBuilder montaEstiloCampo(MascaraVO m){
		FontBuilder font = stl.font().setBold(m.isNegrito()) //
				.setItalic(m.isItalico()) //
				.setUnderline(m.isSublinhado()) //
				.setStrikeThrough(m.isRiscado()) //
				.setFontSize(m.getTamanhoFonte() == 0 ? 10 : m.getTamanhoFonte());
		font.setFontName("Courier New");
		HorizontalAlignment horizontalAlignment = null;
		switch (m.getAlinhamento()) {
		case 'C':
			horizontalAlignment = HorizontalAlignment.CENTER;
			break;
		case 'E':
			horizontalAlignment = HorizontalAlignment.LEFT;
			break;
		case 'D':
			horizontalAlignment = HorizontalAlignment.RIGHT;
			break;
		default:
			break;
		}
		StyleBuilder style = stl.style(font);

		String[] tags = {"</html>", "</div>", "</span>", "</p>", "</body>", 
				"</head>", "</table>", "</li>", "</a>", "</em>","</fieldset>", "<br>", "<br />", "<br/>"};
		for (String tag : tags) {
			if (m.getValor().contains(tag)){
				style.setMarkup(Markup.HTML);
			}
			
		}
		
		
		return style.setHorizontalAlignment(
				horizontalAlignment);
	}
	
	protected void processarVlrRef(ExameVO exame) {
		if (StringUtils.isBlank(exame.getVlrRef())) {
			return;
		}

		int gap = (int) Math.round(10 * PIXEL_AJUST);

		this.getDetail().newRow();
		this.getDetail().add(
				cmp.text("Valores de Referência:")
						.setHorizontalAlignment(HorizontalAlignment.LEFT)
						.setStyle(AbstractLaudo.STYLE_COURIER_08_NORMAL));
		this.getDetail().newRow();
		this.getDetail().add(cmp.verticalGap(10));
		this.getDetail().add(
				gap,
				cmp.text(exame.getVlrRef())
						.setHorizontalAlignment(HorizontalAlignment.LEFT)
						.setStyle(AbstractLaudo.STYLE_COURIER_08_NORMAL)
						.setMarkup(Markup.HTML));
		this.getDetail().newRow();

	}
	
	protected FileInputStream defineLogoHospital() throws IOException {
		return ConfigurationUtil.carregarImagem(this.getCaminhoLogo());
	}

	@Override
	public void setCaminhoLogo(String caminho) {
		this.caminhoLogo = caminho;		
	}
	
	@Override
	public String getCaminhoLogo(){
		return this.caminhoLogo;
	}
}
