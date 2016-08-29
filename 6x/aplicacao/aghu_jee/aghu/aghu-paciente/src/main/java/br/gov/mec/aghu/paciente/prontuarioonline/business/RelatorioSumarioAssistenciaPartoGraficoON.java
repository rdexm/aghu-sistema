package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.text.ChoiceFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.YIntervalSeries;
import org.jfree.data.xy.YIntervalSeriesCollection;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleEdge;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioCardiotocografiaPartos;
import br.gov.mec.aghu.dominio.DominioFormaRupturaBolsaRota;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.McoAtendTrabPartos;
import br.gov.mec.aghu.model.McoBolsaRotas;
import br.gov.mec.aghu.model.McoMedicamentoTrabPartos;
import br.gov.mec.aghu.perinatologia.business.IPerinatologiaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;

 
@Stateless
public class RelatorioSumarioAssistenciaPartoGraficoON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(RelatorioSumarioAssistenciaPartoGraficoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	
	@EJB
	private IPerinatologiaFacade perinatologiaFacade;
	
	@EJB
	private IParametroFacade parametroFacade;

	private static final long serialVersionUID = 4391735951833148931L;

	public BufferedImage getGraficoFrequenciaCardiacaFetalSumAssistParto(Integer pacCodigo,
			Short gsoSeqp) throws ApplicationBusinessException {
		List<LinhaReportVO> listaDados = processarGraficoFreqCardiacaFetal(pacCodigo, gsoSeqp);
		
		DefaultCategoryDataset defaultCategoryDataset = new DefaultCategoryDataset();
		String ultimaData = null;
		for(LinhaReportVO voDado : listaDados){
			if(voDado.getNumero4() != null && voDado.getTexto2() != null){
				defaultCategoryDataset.addValue(voDado.getNumero4(), voDado.getTexto1(), voDado.getTexto2());
				// Verifica a ultima data, para caso o proximo registro não conter valor, 
				// ajustar alinhamento de indexes conforme data do ultimo registro
				if("BCF".equals(voDado.getTexto1())){
					ultimaData = voDado.getTexto2(); 
				}else if("BCF2".equals(voDado.getTexto1())){
					ultimaData = voDado.getTexto2();	
				}
			}else{
				defaultCategoryDataset.addValue(Double.NaN, voDado.getTexto1(), ultimaData);
			}
		}
		
		JFreeChart chart = ChartFactory.createLineChart("Frequência Cardíaca Fetal", null, null, defaultCategoryDataset,
				PlotOrientation.VERTICAL, true, false, false);

		chart.setBorderVisible(false);
		chart.setBorderPaint(Color.black);
		chart.setBackgroundPaint(Color.white);

		final CategoryPlot plot = (CategoryPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.white);
		plot.setRangeGridlinePaint(Color.black);
		plot.setDomainGridlinesVisible(true);
		plot.setDomainGridlinePaint(Color.black);
		plot.setRangeGridlineStroke(new BasicStroke(1));
		//Fundo cinza
		plot.setBackgroundPaint(new Color(200,200,200));

		CategoryAxis categoryaxis = plot.getDomainAxis();
		categoryaxis.setCategoryLabelPositions(CategoryLabelPositions.DOWN_90);
		
		// customise the range axis...
		final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
		rangeAxis.setAutoRange(false);
		rangeAxis.setRange(60, 180);

		processarPropriedadesSeriesFreqCardiacaFetal(plot);
		
		NumberFormat myNumberFormat =  new ChoiceFormat("0#0|10#0|20#0|30#0|40#0|50#0|60#' '|70#'<80'|80#80|90#90|100#100|110#110|120#120|130#130|140#140|150#150|160#160|170#170|180#180");
		rangeAxis.setNumberFormatOverride(myNumberFormat);
		
		return chart.createBufferedImage(480, 360);
	}

	private void processarPropriedadesSeriesFreqCardiacaFetal(
			final CategoryPlot plot) {
		final LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
		renderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator());
		renderer.setBaseItemLabelsVisible(true);
		renderer.setBaseShapesFilled(true);
		renderer.setBaseShapesVisible(true);
		
		//Altera intendidade da COR DA LINHA 
		renderer.setSeriesStroke(0, new BasicStroke(5));
		renderer.setSeriesStroke(1, new BasicStroke(5));
		renderer.setSeriesStroke(2, new BasicStroke(5));
		renderer.setSeriesStroke(3, new BasicStroke(5));
		renderer.setSeriesStroke(4, new BasicStroke(5));
		renderer.setSeriesStroke(5, new BasicStroke(5));
		
		//Altera a INTENSIDADE dos ÍCONES DA SÉRIE
		renderer.setSeriesOutlineStroke(2, new BasicStroke(5));
		renderer.setSeriesOutlineStroke(3, new BasicStroke(5));
		renderer.setSeriesOutlineStroke(4, new BasicStroke(5));
		renderer.setSeriesOutlineStroke(5, new BasicStroke(5));
		
		//Séries 0 e 1, não incluem Shapes dentro do gráfico 
		renderer.setSeriesShapesVisible(0, false);
		renderer.setSeriesShapesVisible(1, false);
		
		//Séries 2 a 5, incluem Shapes do gráfico.
		renderer.setSeriesShapesVisible(2, true);
		renderer.setSeriesShapesVisible(3, true);
		renderer.setSeriesShapesVisible(4, true);
		renderer.setSeriesShapesVisible(5, true);
		
		//Séries 2 a 5, NÃO incluem LINHAS do gráfico.
		renderer.setSeriesLinesVisible(4, false);
		renderer.setSeriesLinesVisible(3, false);
		renderer.setSeriesLinesVisible(2, false);
		renderer.setSeriesLinesVisible(5, false);
		
		//a série 1, inclui legenda(label) nos valores X
		renderer.setSeriesItemLabelGenerator(0, new StandardCategoryItemLabelGenerator());
		renderer.setSeriesItemLabelGenerator(1, new StandardCategoryItemLabelGenerator());
		
		 //linha vermelha (série 1) tracejada
		renderer.setSeriesStroke(1, new BasicStroke(5.0F, 1, 1, 1.0F, new float[] { 6F, 6F }, 0.0F));
		
		//Seta cores das séries
		renderer.setSeriesPaint(1, Color.RED);
		renderer.setSeriesPaint(0, Color.BLUE);
		renderer.setSeriesPaint(2, Color.BLACK);
		renderer.setSeriesPaint(3, Color.BLACK);
		renderer.setSeriesPaint(4, Color.BLACK);
		renderer.setSeriesPaint(5, Color.BLACK);
	}
	
	//Gráfico Freqüência Cardíaca Fetal
	private List<LinhaReportVO> processarGraficoFreqCardiacaFetal(Integer pacCodigo,
			Short gsoSeqp) {
		List<LinhaReportVO> listaCoordenadas = new ArrayList<LinhaReportVO>(); 

		List<McoAtendTrabPartos> listaAtendTrabPartos = getPerinatologiaFacade().listarAtendTrabPartos(pacCodigo, gsoSeqp, McoAtendTrabPartos.Fields.DTHR_ATEND);
		
		if(listaAtendTrabPartos == null || listaAtendTrabPartos.isEmpty()){
			LinhaReportVO bcf = new LinhaReportVO("BCF");
			LinhaReportVO bcf2 = new LinhaReportVO("BCF2");
			LinhaReportVO dip1 = new LinhaReportVO("DIPI");
			LinhaReportVO dip2 = new LinhaReportVO("DIPII");
			LinhaReportVO dip3 = new LinhaReportVO("DIPIII");
			LinhaReportVO brad = new LinhaReportVO("BRAD");
			listaCoordenadas.add(bcf);
			listaCoordenadas.add(bcf2);
			listaCoordenadas.add(dip1);
			listaCoordenadas.add(dip2);
			listaCoordenadas.add(dip3);
			listaCoordenadas.add(brad);
			return listaCoordenadas;
		}
		
		for (McoAtendTrabPartos atendTrabPartos : listaAtendTrabPartos) {
			
			LinhaReportVO bcf = new LinhaReportVO("BCF");
			LinhaReportVO bcf2 = new LinhaReportVO("BCF2");
			LinhaReportVO cat1 = new LinhaReportVO("CAT1");
			LinhaReportVO cat2 = new LinhaReportVO("CAT2");
			LinhaReportVO cat3 = new LinhaReportVO("CAT3");
			LinhaReportVO brad = new LinhaReportVO("BRAD");
			
			//BCF 
			if(atendTrabPartos.getBatimentoCardiacoFetal() != null){
				bcf.setTexto2(DateUtil.dataToString(atendTrabPartos.getDthrAtend(), DateConstants.DATE_PATTERN_HORA_MINUTO));
				bcf.setNumero4(atendTrabPartos.getBatimentoCardiacoFetal());
			}
			
			//BCF2
			if(atendTrabPartos.getBatimentoCardiacoFetal2() != null){
				bcf2.setTexto2(DateUtil.dataToString(atendTrabPartos.getDthrAtend(), DateConstants.DATE_PATTERN_HORA_MINUTO));
				bcf2.setNumero4(atendTrabPartos.getBatimentoCardiacoFetal2());
			}
			
			//DIP1, DIP2, DIP3, BRAD
			//dip1.setData(atendTrabPartos.getDthrAtend());
			cat1.setTexto2(DateUtil.dataToString(atendTrabPartos.getDthrAtend(), DateConstants.DATE_PATTERN_HORA_MINUTO));
			cat2.setTexto2(DateUtil.dataToString(atendTrabPartos.getDthrAtend(), DateConstants.DATE_PATTERN_HORA_MINUTO));
			cat3.setTexto2(DateUtil.dataToString(atendTrabPartos.getDthrAtend(), DateConstants.DATE_PATTERN_HORA_MINUTO));
			brad.setTexto2(DateUtil.dataToString(atendTrabPartos.getDthrAtend(), DateConstants.DATE_PATTERN_HORA_MINUTO));
			if (atendTrabPartos.getCardiotocografia() == null || DominioCardiotocografiaPartos.REATIVO.equals(atendTrabPartos.getCardiotocografia())){
				cat1.setNumero4(Short.valueOf("0"));
				cat2.setNumero4(Short.valueOf("0"));
				cat3.setNumero4(Short.valueOf("0"));
				brad.setNumero4(Short.valueOf("0"));
			}else{
				if(DominioCardiotocografiaPartos.DIPI.equals(atendTrabPartos.getCardiotocografia())){
					cat1.setNumero4(Short.valueOf("70"));
					cat2.setNumero4(Short.valueOf("0"));
					cat3.setNumero4(Short.valueOf("0"));
					brad.setNumero4(Short.valueOf("0"));
				}else if(DominioCardiotocografiaPartos.DIPII.equals(atendTrabPartos.getCardiotocografia())){
					cat1.setNumero4(Short.valueOf("0"));
					cat2.setNumero4(Short.valueOf("70"));
					cat3.setNumero4(Short.valueOf("0"));
					brad.setNumero4(Short.valueOf("0"));
				}else if(DominioCardiotocografiaPartos.DIPIII.equals(atendTrabPartos.getCardiotocografia())){
					cat1.setNumero4(Short.valueOf("0"));
					cat2.setNumero4(Short.valueOf("0"));
					cat3.setNumero4(Short.valueOf("70"));
					brad.setNumero4(Short.valueOf("0"));
				}else if(DominioCardiotocografiaPartos.BRAD.equals(atendTrabPartos.getCardiotocografia())){
					cat1.setNumero4(Short.valueOf("0"));
					cat2.setNumero4(Short.valueOf("0"));
					cat3.setNumero4(Short.valueOf("0"));
					brad.setNumero4(Short.valueOf("70"));
				}
			}
			
			listaCoordenadas.add(bcf);
			listaCoordenadas.add(bcf2);
			listaCoordenadas.add(cat1);
			listaCoordenadas.add(cat2);
			listaCoordenadas.add(cat3);
			listaCoordenadas.add(brad);
		}
		return listaCoordenadas;
	}      
	
	public BufferedImage getGraficoPartogramaSumAssistParto(Integer pacCodigo,
			Short gsoSeqp) throws ApplicationBusinessException{
		
		Map<Long, Double> mapaHoraDilatacao = processarMapaHoraDilatacao(pacCodigo, gsoSeqp);
		
		JFreeChart chart = ChartFactory.createXYLineChart("Partograma", null,
			"Dilatação em cm", 
			getDilatacaoDataSet(pacCodigo, gsoSeqp, mapaHoraDilatacao, Boolean.FALSE),
			PlotOrientation.VERTICAL, true, true,
                false);
		
		Boolean contemAltBcf = verificaExisteAltBcf(pacCodigo, gsoSeqp, mapaHoraDilatacao);

        chart.setBorderVisible(false);
        chart.setBorderPaint(Color.BLACK);
        chart.setBackgroundPaint(Color.WHITE);
        
        //Legenda
        setLegendaPartograma(chart, mapaHoraDilatacao);

        final XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.WHITE);

        plot.setDomainPannable(true);
        plot.setRangePannable(true);

        NumberAxis numberaxis = new NumberAxis("Planos de Dee Lee");
        numberaxis.setAutoRangeIncludesZero(false);
        numberaxis.setRange(-5, 5);
        numberaxis.setInverted(true);
        numberaxis.setTickUnit(new NumberTickUnit(1));
        NumberFormat myNumberFormat =  new ChoiceFormat("'-5'#'-5'|'-4'#'-4'|'-3'#'-3'|'-2'#'-2'|'-1'#'-1'|0#0|1#'+1'|2#'+2'|3#'+3'|4#'+4'|5#'+5'");
        numberaxis.setNumberFormatOverride(myNumberFormat);
        

        plot.setRangeAxis(1, numberaxis);
        plot.setDataset(1, getDilatacaoDataSet(pacCodigo, gsoSeqp, mapaHoraDilatacao, Boolean.TRUE));
        plot.mapDatasetToRangeAxis(1, 1);
        plot.setRenderer(1, new XYLineAndShapeRenderer(true, false));

        plot.setRangeGridlinesVisible(true);
        plot.setDomainGridlinesVisible(true);

        plot.setDomainPannable(true);
        plot.setRangePannable(true);

        //Propriedades das linhas tracejadas
        plot.setDomainGridlinePaint(Color.BLACK);
        plot.setRangeGridlinePaint(Color.BLACK);
        plot.setDomainGridlineStroke(new BasicStroke(1));
        plot.setRangeGridlineStroke(new BasicStroke(1));
        //Fundo cinza
		plot.setBackgroundPaint(new Color(200,200,200));
        
        
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setTickUnit(new NumberTickUnit(1));
        rangeAxis.setAutoRange(false);
        rangeAxis.setRange(0, 10);

        
        NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
        domainAxis.setRange(0,12);
        domainAxis.setVerticalTickLabels(true);
        domainAxis.setAutoRange(false);
        domainAxis.setTickUnit(new NumberTickUnit(1));
        
        processarPropriedadesSeriesPartograma(plot, contemAltBcf);

        Font f = plot.getDomainAxis().getLabelFont();
        numberaxis.setLabelFont(f);
	
        return chart.createBufferedImage(390, 352);
	}


	private void processarPropriedadesSeriesPartograma(final XYPlot plot, Boolean mostraAltBcf) {
		final XYLineAndShapeRenderer rendererDilatacao = (XYLineAndShapeRenderer) plot.getRenderer();
		rendererDilatacao.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
		rendererDilatacao.setBaseItemLabelsVisible(true);
		rendererDilatacao.setBaseShapesFilled(true);
		rendererDilatacao.setBaseShapesVisible(true);
		rendererDilatacao.setBaseLinesVisible(true);

		//rendererDilatacao.setSeriesShape(0, new java.awt.geom.Ellipse2D.Double(-5D, -5D, 10D, 10D));
		rendererDilatacao.setSeriesStroke(0, new BasicStroke(3F));
		rendererDilatacao.setSeriesFillPaint(0, Color.white);
		rendererDilatacao.setUseFillPaint(true);
		
		//Seta cores das séries
		rendererDilatacao.setSeriesPaint(0, Color.BLUE);
		rendererDilatacao.setSeriesPaint(2, Color.BLACK);
		rendererDilatacao.setSeriesPaint(3, Color.BLACK);
		rendererDilatacao.setSeriesPaint(4, Color.BLACK);
		rendererDilatacao.setSeriesPaint(5, Color.BLACK);
		if(mostraAltBcf){
			rendererDilatacao.setSeriesPaint(6, Color.BLACK);			
		}else{
			rendererDilatacao.setSeriesPaint(6, Color.GRAY);
			rendererDilatacao.setSeriesVisibleInLegend(6, false);
			rendererDilatacao.setSeriesShapesVisible(6, false);
		}
		
		//Linha fixa 1
		rendererDilatacao.setSeriesPaint(7, Color.GRAY);
		rendererDilatacao.setSeriesVisibleInLegend(7, false);
		rendererDilatacao.setSeriesShapesVisible(7, false);
		//Linha fixa 2
		rendererDilatacao.setSeriesPaint(8, Color.RED);
		rendererDilatacao.setSeriesVisibleInLegend(8, false);
		rendererDilatacao.setSeriesShapesVisible(8, false);
		
		//Aumenta a intensidade das séries
		rendererDilatacao.setSeriesOutlineStroke(2, new BasicStroke(3));
		rendererDilatacao.setSeriesOutlineStroke(3, new BasicStroke(3));
		rendererDilatacao.setSeriesOutlineStroke(4, new BasicStroke(3));
		rendererDilatacao.setSeriesOutlineStroke(5, new BasicStroke(3));
		if(mostraAltBcf){
			rendererDilatacao.setSeriesOutlineStroke(6, new BasicStroke(3));
		}
		
		//Altera a intendidade da linha da fixa
		rendererDilatacao.setSeriesStroke(7, new BasicStroke(2));
		rendererDilatacao.setSeriesStroke(8, new BasicStroke(2));
		
		//Remove linha das séries que não possuem linha (as únicas que possuem são Dialatação e Delle)
		rendererDilatacao.setSeriesLinesVisible(2, false);
		rendererDilatacao.setSeriesLinesVisible(3, false);
		rendererDilatacao.setSeriesLinesVisible(4, false);
		rendererDilatacao.setSeriesLinesVisible(5, false);
		
		//Altera o conteúdo dentro do shape
		rendererDilatacao.setSeriesFillPaint(2, Color.BLACK);
		rendererDilatacao.setSeriesFillPaint(3, Color.BLACK);
		rendererDilatacao.setSeriesFillPaint(4, Color.BLACK);
		rendererDilatacao.setSeriesFillPaint(5, Color.BLACK);
		if(mostraAltBcf){
			rendererDilatacao.setSeriesFillPaint(6, Color.BLACK);			
		}
		
		final XYLineAndShapeRenderer rendereerDeLee = (XYLineAndShapeRenderer) plot.getRenderer(1);
		rendereerDeLee.setBaseToolTipGenerator(new StandardXYToolTipGenerator());
		rendereerDeLee.setBaseItemLabelsVisible(true);
		rendereerDeLee.setBaseShapesFilled(true);
		rendereerDeLee.setBaseShapesVisible(true);
		rendereerDeLee.setBaseLinesVisible(true);
		
		rendereerDeLee.setSeriesShape(0, new java.awt.geom.Ellipse2D.Double(-5D, -5D, 10D, 10D));
		rendereerDeLee.setSeriesStroke(0, new BasicStroke(3F));
		rendereerDeLee.setSeriesFillPaint(0, Color.white);//Cor dentro do Shape
		rendereerDeLee.setUseFillPaint(true);
		rendereerDeLee.setSeriesPaint(0, Color.RED);
		
		//Faz com que os dois planos DeLee fiquem IGUAIS (o fake e original)
		rendererDilatacao.setSeriesShape(1, new java.awt.geom.Ellipse2D.Double(-5D, -5D, 10D, 10D));
		rendererDilatacao.setSeriesStroke(1, new BasicStroke(3F));
		rendererDilatacao.setSeriesFillPaint(1, Color.white);//Cor dentro do Shape
		rendererDilatacao.setSeriesPaint(1, Color.RED);
		
		//Remove a legenda do plano deLee original
		rendereerDeLee.setSeriesVisibleInLegend(0, false);
	}


	private void setLegendaPartograma(JFreeChart chart,
			Map<Long, Double> mapaHoraDilatacao) {
		String legenda = processarLegendaPartograma(mapaHoraDilatacao);
		TextTitle legendaHorarios = new TextTitle(legenda);
		legendaHorarios.setFont(new Font(Font.MONOSPACED, Font.TRUETYPE_FONT, 12));
		legendaHorarios.setPosition(RectangleEdge.BOTTOM);
		legendaHorarios.setHorizontalAlignment(HorizontalAlignment.CENTER);
		chart.addSubtitle(legendaHorarios);
	}
	
	private YIntervalSeriesCollection getDilatacaoDataSet(Integer pacCodigo,
			Short gsoSeqp, Map<Long, Double> mapaHoraDilatacao,
			Boolean somentePlanoDeLee) throws ApplicationBusinessException {
		
		List<LinhaReportVO> dadosDilatacao = new ArrayList<LinhaReportVO>();
		if(mapaHoraDilatacao != null && !mapaHoraDilatacao.isEmpty()){
			dadosDilatacao = processarPartograma(pacCodigo, gsoSeqp, mapaHoraDilatacao);
		}
		
		YIntervalSeriesCollection dilatacaoDataSet = new YIntervalSeriesCollection();
		
		YIntervalSeries dilatacaoSerie = new YIntervalSeries(DilatacaoSeries.DILATACAO.getDescricao());
		YIntervalSeries deeLeeFake = new YIntervalSeries(DilatacaoSeries.PLANO_DEE_LEE.getDescricao());
		YIntervalSeries ocitocinaSerie = new YIntervalSeries(DilatacaoSeries.OCITOCINA.getDescricao());
		YIntervalSeries amnioRexisSerie = new YIntervalSeries(DilatacaoSeries.AMNIOREXIS.getDescricao());
        YIntervalSeries amniotomiaSerie = new YIntervalSeries(DilatacaoSeries.AMNIOTOMIA.getDescricao());
        YIntervalSeries analgesiaSerie = new YIntervalSeries(DilatacaoSeries.ANALGESIA.getDescricao());
        YIntervalSeries altBcf = new YIntervalSeries(DilatacaoSeries.ALT_BCF.getDescricao());
        YIntervalSeries linhaFixa = new YIntervalSeries("");
        YIntervalSeries linhaFixa2 = new YIntervalSeries("");
        YIntervalSeries deeLee = new YIntervalSeries(DilatacaoSeries.PLANO_DEE_LEE.getDescricao());
        boolean mostraAltBCF = false;
        
       /* YIntervalSeries deeLee2 = new YIntervalSeries(DilatacaoSeries.PLANO_DEE_LEE.getDescricao());
        deeLee2.add(-3, 4, 0, 10);
        dilatacaoDataSet.addSeries(deeLee2);*/
		
        for(LinhaReportVO voDilat: dadosDilatacao){
	        if(somentePlanoDeLee){
	        	if(DilatacaoSeries.PLANO_DEE_LEE.getDescricao().equals(voDilat.getTexto1())){
	        		if(voDilat.getNumero2() != null){
	        			deeLee.add(voDilat.getNumero1().doubleValue(), voDilat.getNumero2().doubleValue(),   -5, 5);
	        		}else{
	        			deeLee.add(voDilat.getNumero1().doubleValue(), Double.NaN,   -5, 5);
	        		}
				}
	        }else{
	        	if(DilatacaoSeries.DILATACAO.getDescricao().equals(voDilat.getTexto1())){
	        		if(voDilat.getNumero2() != null){
	        			dilatacaoSerie.add(voDilat.getNumero1().doubleValue(), voDilat.getNumero2().doubleValue(),   0, 10);
	        		}else{
	        			dilatacaoSerie.add(voDilat.getNumero1().doubleValue(), Double.NaN,   0, 10);
	        		}
				
				}else if(DilatacaoSeries.OCITOCINA.getDescricao().equals(voDilat.getTexto1())){
					ocitocinaSerie.add(voDilat.getNumero1().doubleValue(), voDilat.getNumero2().doubleValue(),   0, 10);
				
				}else if(DilatacaoSeries.AMNIOREXIS.getDescricao().equals(voDilat.getTexto1())){
					amnioRexisSerie.add(voDilat.getNumero1().doubleValue(), voDilat.getNumero2().doubleValue(),   0, 10);
				
				}else if(DilatacaoSeries.AMNIOTOMIA.getDescricao().equals(voDilat.getTexto1())){
					amniotomiaSerie.add(voDilat.getNumero1().doubleValue(), voDilat.getNumero2().doubleValue(),   0, 10);
				
				}else if(DilatacaoSeries.ANALGESIA.getDescricao().equals(voDilat.getTexto1())){
					analgesiaSerie.add(voDilat.getNumero1().doubleValue(), voDilat.getNumero2().doubleValue(),   0, 10);
				}else if(DilatacaoSeries.ALT_BCF.getDescricao().equals(voDilat.getTexto1())){
					altBcf.add(voDilat.getNumero1().doubleValue(), 0.5, 0, 10);
					mostraAltBCF = true;
				}
	        }
		}
        
		 if(somentePlanoDeLee){
			 dilatacaoDataSet.addSeries(deeLee);
		 }else{
			if(mapaHoraDilatacao != null && !mapaHoraDilatacao.isEmpty()){
				//Linha Fixa 1 - CINZA
			    linhaFixa.add(0, 0,     0, 10);
			    linhaFixa.add(10, 10,   0, 10);
			    //Linha Fixa 2 - VERMELHA
		        linhaFixa2.add(4, 0,    0, 10);
		        linhaFixa2.add(12, 8,   0, 10);
			}
	        
	        dilatacaoDataSet.addSeries(dilatacaoSerie);
	        dilatacaoDataSet.addSeries(deeLeeFake);
	        dilatacaoDataSet.addSeries(ocitocinaSerie);
	        dilatacaoDataSet.addSeries(amnioRexisSerie);
	        dilatacaoDataSet.addSeries(amniotomiaSerie);
	        dilatacaoDataSet.addSeries(analgesiaSerie);
	        if(mostraAltBCF){
		    	dilatacaoDataSet.addSeries(altBcf);	        	
		    }else{
		    	dilatacaoDataSet.addSeries(new YIntervalSeries(""));
		    }
	        dilatacaoDataSet.addSeries(linhaFixa);
	        dilatacaoDataSet.addSeries(linhaFixa2);
		 }
       
        
        return dilatacaoDataSet;
	}


	public List<LinhaReportVO> processarPartograma(Integer pacCodigo,
			Short gsoSeqp, Map<Long, Double> mapaHoraDilatacao) throws ApplicationBusinessException{
		
		List<LinhaReportVO> dadosPartograma = new ArrayList<LinhaReportVO>();
		
		processarDilatacaoEPlanoDeLee(dadosPartograma, pacCodigo, gsoSeqp , mapaHoraDilatacao);
		
		processarDadosOcitocina(pacCodigo, gsoSeqp, dadosPartograma, mapaHoraDilatacao);
		
		processarAmniorexisEAmniotomia(pacCodigo, gsoSeqp, dadosPartograma,
				mapaHoraDilatacao);
		
		processarDadosAnalgesia(pacCodigo, gsoSeqp, dadosPartograma,
				mapaHoraDilatacao);
		
		processarDadosAltBcf(pacCodigo, gsoSeqp, dadosPartograma,
				mapaHoraDilatacao);
		
		return dadosPartograma;
	}
	
	private void processarDadosAltBcf(Integer pacCodigo, Short gsoSeqp,
			List<LinhaReportVO> dadosPartograma,
			Map<Long, Double> mapaHoraDilatacao) {
		List<McoAtendTrabPartos> atendTrabPartosAltBcf = getPerinatologiaFacade()
				.listarAtendTrabPartos(pacCodigo, gsoSeqp, McoAtendTrabPartos.Fields.DTHR_ATEND);
		
		if(!atendTrabPartosAltBcf.isEmpty()){
			for(McoAtendTrabPartos item : atendTrabPartosAltBcf){
				if(item.getIndTaquicardia() || item.getSemAceleracaoTransitoria() || item.getVariabilidadeBatidaMenorQueDez()){
					LinhaReportVO voAltBcf = new LinhaReportVO();
					Double valorX = recuperarValorXEmMapaDilatacao(mapaHoraDilatacao, item.getDthrAtend());
					voAltBcf.setNumero1(new BigDecimal(valorX));
					voAltBcf.setTexto1(DilatacaoSeries.ALT_BCF.getDescricao());
					dadosPartograma.add(voAltBcf);
				}
			}
		}
	}
	
	private Boolean verificaExisteAltBcf(Integer pacCodigo, Short gsoSeqp, Map<Long, Double> mapaHoraDilatacao){
		
		Boolean mostraAltBcf = false;
		
		List<McoAtendTrabPartos> atendTrabPartosAltBcf = getPerinatologiaFacade()
				.listarAtendTrabPartos(pacCodigo, gsoSeqp, McoAtendTrabPartos.Fields.DTHR_ATEND);
		if(!atendTrabPartosAltBcf.isEmpty()){
			for(McoAtendTrabPartos item : atendTrabPartosAltBcf){
				if(item.getIndTaquicardia() || item.getSemAceleracaoTransitoria() || item.getVariabilidadeBatidaMenorQueDez()){
					mostraAltBcf = true;
					break;
				}
			}
		}
		return mostraAltBcf;
		
	}


	private void processarDadosAnalgesia(Integer pacCodigo, Short gsoSeqp,
			List<LinhaReportVO> dadosPartograma,
			Map<Long, Double> mapaHoraDilatacao) {
		List<McoAtendTrabPartos> atendTrabPartosAnalgesia = getPerinatologiaFacade()
				.listarAtendTrabPartos(pacCodigo, gsoSeqp,
						McoAtendTrabPartos.Fields.DTHR_ATEND, Boolean.TRUE,
						Boolean.TRUE);
		
		/*//for(McoAtendTrabPartos parto : atendTrabPartosAnalgesia){ 
			LinhaReportVO voAnalgesia = new LinhaReportVO();
			Double valorX = recuperarValorXEmMapaDilatacao(mapaHoraDilatacao, parto.getDthrAtend());
			//o AGH apresenta somente o 1º registro
			 * */
		if(!atendTrabPartosAnalgesia.isEmpty()){
			LinhaReportVO voAnalgesia = new LinhaReportVO();
			Double valorX = recuperarValorXEmMapaDilatacao(mapaHoraDilatacao, atendTrabPartosAnalgesia.get(0).getDthrAtend());
			voAnalgesia.setNumero1(new BigDecimal(valorX));
			voAnalgesia.setNumero2(new BigDecimal(3));
			voAnalgesia.setTexto1(DilatacaoSeries.ANALGESIA.getDescricao());
			dadosPartograma.add(voAnalgesia);
		}
	}


	private void processarAmniorexisEAmniotomia(Integer pacCodigo,
			Short gsoSeqp, List<LinhaReportVO> dadosPartograma,
			Map<Long, Double> mapaHoraDilatacao) {
		List<McoBolsaRotas> bolsasRotas = getPerinatologiaFacade().listarBolsasRotas(pacCodigo, gsoSeqp);
		for(McoBolsaRotas bolsaRota : bolsasRotas){
			if(DominioFormaRupturaBolsaRota.Amniorrexis.equals(bolsaRota.getDominioFormaRuptura()) ||
				DominioFormaRupturaBolsaRota.Amniotomia.equals(bolsaRota.getDominioFormaRuptura())){
				
				LinhaReportVO voAmniorrexis = new LinhaReportVO();
				if(bolsaRota.getDthrRompimento() != null){
					Double valorX = recuperarValorXEmMapaDilatacao(mapaHoraDilatacao, bolsaRota.getDthrRompimento());
					voAmniorrexis.setNumero1(new BigDecimal(valorX));
				}				
				voAmniorrexis.setNumero2(BigDecimal.ONE);
				voAmniorrexis.setTexto1(bolsaRota.getDominioFormaRuptura().getDescricao());
				dadosPartograma.add(voAmniorrexis);
			}
		}
	}


	private void processarDilatacaoEPlanoDeLee(List<LinhaReportVO> dadosPartograma,
			Integer pacCodigo, Short gsoSeqp,
			Map<Long, Double> mapaHoraDilatacao) {
		
		List<McoAtendTrabPartos> trabPartosDilatacao = getPerinatologiaFacade().listarAtendTrabPartos(pacCodigo, gsoSeqp, McoAtendTrabPartos.Fields.DTHR_ATEND);
		
		for(McoAtendTrabPartos parto : trabPartosDilatacao){
			Double valorX = recuperarValorXEmMapaDilatacao(mapaHoraDilatacao, parto.getDthrAtend());

			LinhaReportVO voDilatacao = new LinhaReportVO();
			voDilatacao.setNumero1(new BigDecimal(valorX));
			voDilatacao.setNumero2(parto.getDilatacao() != null ? new BigDecimal(parto.getDilatacao()): null);
			voDilatacao.setTexto1(DilatacaoSeries.DILATACAO.getDescricao());
			dadosPartograma.add(voDilatacao);
			
			//if(parto.getPlanoDelee() != null){
			LinhaReportVO voDeLee = new LinhaReportVO();
			voDeLee.setNumero1(new BigDecimal(valorX));
			voDeLee.setNumero2(parto.getPlanoDelee() != null ? new BigDecimal(parto.getPlanoDelee().getCodigo()): null);
			voDeLee.setTexto1(DilatacaoSeries.PLANO_DEE_LEE.getDescricao());
			dadosPartograma.add(voDeLee);
			//}
		}
	}


	private void processarDadosOcitocina(Integer pacCodigo, Short gsoSeqp,
			List<LinhaReportVO> dadosPartograma,
			Map<Long, Double> mapaHoraDilatacao)
			throws ApplicationBusinessException {
		Integer[] codOcitocinas = getCodigosMedicamentosOcitocina();
		List<McoMedicamentoTrabPartos> mdtoTrabPartos = getPerinatologiaFacade()
				.listarMedicamentosTrabPartos(pacCodigo, gsoSeqp,
						McoMedicamentoTrabPartos.Fields.DTHR_INI,
						McoMedicamentoTrabPartos.Fields.DTHR_INI, Boolean.FALSE,
						codOcitocinas);
		
		//for(McoMedicamentoTrabPartos mtp : mdtoTrabPartos){//Mesmo que o paciente tenha vários, o AGH apresenta somente o 1º registro
		if(!mdtoTrabPartos.isEmpty()){
			Double valorX = recuperarValorXEmMapaDilatacao(mapaHoraDilatacao, mdtoTrabPartos.get(0).getDataHoraInicial());
			LinhaReportVO voOcitocina = new LinhaReportVO();
			voOcitocina.setNumero1(new BigDecimal(valorX));
			voOcitocina.setNumero2(new BigDecimal(2));
			voOcitocina.setTexto1(DilatacaoSeries.OCITOCINA.getDescricao());
			dadosPartograma.add(voOcitocina);
		}
	}
	
	private Integer[] getCodigosMedicamentosOcitocina() throws ApplicationBusinessException {
		AghParametros ocitocina1 = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_OCITOCINA1);
		AghParametros ocitocina2 = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_OCITOCINA2);
		Integer [] codsMdtos = new Integer [2];
		codsMdtos [0] = ocitocina1.getVlrNumerico().intValue();
		codsMdtos [1] = ocitocina2.getVlrNumerico().intValue();
		
		return codsMdtos;
	}
	
	private IParametroFacade getParametroFacade(){
		return parametroFacade;
	}


	private Double recuperarValorXEmMapaDilatacao(
			Map<Long, Double> mapaHoraDilatacao, Date hrDilatacao) {
		Double valorX =  mapaHoraDilatacao.get(mcocCalcHora(hrDilatacao).getTime());
		
		if(valorX == null){
			valorX =  mapaHoraDilatacao.get(mcocCalcHora(DateUtil.adicionaMinutos(mcocCalcHora(hrDilatacao), 1)).getTime());
			if(valorX != null){
				valorX =  valorX - 0.5;
			}else{
				valorX = 0.0;//Esta situação ocorre quando o evento (hrDilatacao) ocorreu antes do primeiro horário da legenda
			}
		}
		return valorX;
	}


	public Map<Long, Double> processarMapaHoraDilatacao(Integer pacCodigo,
			Short gsoSeqp) {
		
		List<McoAtendTrabPartos> trabPartosDilatacao = getPerinatologiaFacade().listarAtendTrabPartos(pacCodigo, gsoSeqp, McoAtendTrabPartos.Fields.DTHR_ATEND);
		Map<Long, Double> mapaDilatacao = new HashMap<Long, Double>();
		
		if(trabPartosDilatacao != null && !trabPartosDilatacao.isEmpty()){
			McoAtendTrabPartos atdParto = trabPartosDilatacao.get(0);
			
			Double nrLegenda = 1.0;
			for(int contDilat = atdParto.getDilatacao(); contDilat > 1; contDilat--){
				Date horaDilatacao = DateUtil.adicionaHoras(mcocCalcHora(atdParto.getDthrAtend()), (contDilat-1) *-1) ;
				mapaDilatacao.put(horaDilatacao.getTime(), nrLegenda);
				nrLegenda++;
			}
			
			Date horaDilatacao = mcocCalcHora(atdParto.getDthrAtend());
			mapaDilatacao.put(horaDilatacao.getTime(), nrLegenda);
			nrLegenda++;
			
			Integer nroRestantes = 1;
			for(int contDilat = atdParto.getDilatacao()+1; contDilat <= 15; contDilat++){
				Date horaDilatacaoN = DateUtil.adicionaHoras(mcocCalcHora(atdParto.getDthrAtend()), nroRestantes) ;
				mapaDilatacao.put(horaDilatacaoN.getTime(), Double.valueOf(contDilat));
				nrLegenda++;
				nroRestantes++;
			}
		}
		
		return mapaDilatacao;
	}

	private String processarLegendaPartograma(Map<Long, Double> mapaDilatacao) {
		
		List<LinhaReportVO> listaLegenda = new ArrayList<LinhaReportVO>();
		for (Map.Entry<Long, Double> entry : mapaDilatacao.entrySet()) {
			if(entry.getValue() <= 10){
				LinhaReportVO vo = new LinhaReportVO();
				vo.setNumero1(new BigDecimal(entry.getValue()));
				vo.setTexto1(DateUtil.dataToString(new Date(entry.getKey()), DateConstants.DATE_PATTERN_HORA_MINUTO));
				listaLegenda.add(vo);
			}
		}
		
		CoreUtil.ordenarLista(listaLegenda, "numero1", true);
		
		StringBuffer legenda = new StringBuffer();
		for(LinhaReportVO vo : listaLegenda){
			legenda.append('(');
			legenda.append(vo.getNumero1());
			legenda.append(')');
			legenda.append(vo.getTexto1());
			legenda.append(' ');
			if(vo.getNumero1().equals(new BigDecimal(5))){//quebra de linha
				legenda.append("\n ");
			}
		}
		
		//#49240 - Estava dando erro ao gerar o gráfico
		//legenda.append(" \n \n");
		
		return legenda.toString();
	}


	/**
	 * MCOC_CALC_HORA
	 * @param pHoraAnt
	 * @return
	 **/
	public Date mcocCalcHora(Date pHoraAnt){
		
		Date vHoraAtu;
		Integer vDiferenca30;
		Integer vDiferenca60;
		
		vHoraAtu = pHoraAnt;
		
		if(DateUtil.getMinutos(pHoraAnt) != 0){
			if(DateUtil.getMinutos(pHoraAnt) < 30){
				vDiferenca30 = new BigDecimal(DateUtil.getMinutos(pHoraAnt).intValue() - 30).abs().intValue();
				vHoraAtu = DateUtil.adicionaMinutos(pHoraAnt, vDiferenca30);
			}else if(DateUtil.getMinutos(pHoraAnt) > 30){
				vDiferenca60 = new BigDecimal(DateUtil.getMinutos(pHoraAnt).intValue() - 60).abs().intValue();
				vHoraAtu = DateUtil.adicionaMinutos(pHoraAnt, vDiferenca60);
			}
		}
		return vHoraAtu;
	}
	
	protected IPerinatologiaFacade getPerinatologiaFacade() {
		return this.perinatologiaFacade;
	}
	
	public enum DilatacaoSeries {
			DILATACAO("Dilatação")
		, 	PLANO_DEE_LEE("PLano Dee Lee")
		,	OCITOCINA("Ocitocina")
		, 	AMNIOREXIS("Amniorexis")
		, 	AMNIOTOMIA("Amniotomia")
		, 	ANALGESIA("Analgesia")
		, 	ALT_BCF("Alt. BCF")
		;

		@SuppressWarnings("PMD.AtributoEmSeamContextManager")	
		private String descricao;

		private DilatacaoSeries(String descricao) {
			this.descricao = descricao;
		}

		public String getDescricao() {
			return descricao;
		}
	}
}