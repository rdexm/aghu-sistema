package br.gov.mec.aghu.faturamento.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.faturamento.vo.RateioValoresSadtPorPontosVO;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.AghuNumberFormat;

@Stateless
public class RelatorioRateioValoresSadtPorPontosON extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1845266767749000229L;
	
	private static final Log LOG = LogFactory.getLog(RelatorioRateioValoresSadtPorPontosON.class);
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	private static final String SEPARADOR=";";
	private static final String ENCODE="ISO-8859-1";
	private static final String EXTENSAO=".csv";

	@Override
	protected Log getLogger() {
		return LOG;
	}

	/**
	 * Gera o arquivo CSV do relatório de Rateio de Valores Sadt por Pontos.
	 * 
	 * @param competencia
	 * @return
	 * @throws IOException
	 */
	public String exportarArquivoCSV(final FatCompetencia competencia) throws IOException, ApplicationBusinessException {

		final List<RateioValoresSadtPorPontosVO> colecao = 
				this.faturamentoFacade.obterRateioValoresSadtPorPontos(
						competencia.getId().getDtHrInicio(),competencia.getId().getAno(),competencia.getId().getMes());
		
		if (colecao == null || colecao.isEmpty()) {
			return null;
		}

		final BigDecimal fatorMultiplicacao = 
				this.faturamentoFacade.obterFatorMultiplicacaoParaValorRateado(
						competencia.getId().getDtHrInicio(),competencia.getId().getAno(),competencia.getId().getMes());
		
		final File file = File.createTempFile(DominioNomeRelatorio.REL_RATEIO_VALORES_SADT_POR_PONTOS.toString(), EXTENSAO);
		
		final Writer writer = new OutputStreamWriter(new FileOutputStream(file), ENCODE);
		
		writer.write("Cód" + SEPARADOR + "Unidade" + SEPARADOR + "Pontos SADT" + SEPARADOR + "Valor Rateado" + "\n");

		long somaPontosSadt = 0;
		double somaValoresRateados = 0;
		
		for (final RateioValoresSadtPorPontosVO item : colecao) {

			double valorRateado = 0;
			
			if (item.getPontosSadt() != null) {
				
				somaPontosSadt += item.getPontosSadt();
				
				if (fatorMultiplicacao != null) {
					valorRateado = item.getPontosSadt() * fatorMultiplicacao.doubleValue();
				}
			}
			
			somaValoresRateados += valorRateado;
			
			imprimirLinha(writer, item, valorRateado);
		}
		
		writer.write(SEPARADOR + SEPARADOR + somaPontosSadt + SEPARADOR + AghuNumberFormat.formatarNumeroMoeda(somaValoresRateados) + "\n");

		imprimirRodape(fatorMultiplicacao, writer, somaPontosSadt);

		writer.flush();
		writer.close();
		
		return file.getAbsolutePath();
	}

	private void imprimirLinha(final Writer writer,	final RateioValoresSadtPorPontosVO item, double valorRateado) throws IOException {
		
		writer.write((item.getSeq() != null ? item.getSeq() : "") + SEPARADOR +
				   (item.getDescricao() != null ? item.getDescricao() : "") + SEPARADOR + 
				   (item.getPontosSadt() != null ? item.getPontosSadt() : "") + SEPARADOR + 
				   AghuNumberFormat.formatarNumeroMoeda(valorRateado) + "\n");
	}

	private void imprimirRodape(final BigDecimal fatorMultiplicacao, final Writer writer, long somaPontosSadt) throws IOException {

		if (fatorMultiplicacao != null) {
			writer.write("Valor Ponto:" + SEPARADOR + AghuNumberFormat.formatarNumeroMoeda(fatorMultiplicacao.doubleValue() / somaPontosSadt) + SEPARADOR + SEPARADOR + "\n");
		} else {
			writer.write("Valor Ponto:" + SEPARADOR + "0,0000" + SEPARADOR + SEPARADOR + "\n");
		}
	}
}
