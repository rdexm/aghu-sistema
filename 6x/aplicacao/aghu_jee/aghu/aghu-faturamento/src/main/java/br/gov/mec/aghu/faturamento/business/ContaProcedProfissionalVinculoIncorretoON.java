package br.gov.mec.aghu.faturamento.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.exception.AghuCSVBaseException.AghuCSVBaseExceptionExceptionCode;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.faturamento.vo.ArquivoURINomeQtdVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class ContaProcedProfissionalVinculoIncorretoON extends BaseBusiness {

	private static final long serialVersionUID = -1268443613036585895L;
	private static final Log LOG = LogFactory.getLog(ContaProcedProfissionalVinculoIncorretoON.class);
	
	@EJB
	private ContaProcedProfissionalVinculoIncorretoRN contaProcedProfVinculoIncorretoRN;
	
	private static final String QUEBRA_LINHA = "\n";
	private static final String SEPARADOR=";";
	private static final String DATE_PATTERN_DDMMYYYYHHMM = "ddMMyyyyHHmm";
	private static final String ENCODE="ISO-8859-1";

	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	public ArquivoURINomeQtdVO gerarCSVContasProcedProfissionalVinculoIncorreto() throws ApplicationBusinessException {
		ArquivoURINomeQtdVO resultado = null;
		Writer out = null;
		try {
			List<Integer>  lista = contaProcedProfVinculoIncorretoRN.buscarContasProcedProfissionalVinculoIncorreto();
			
			String filename = criarNomeArquivo();
			final File file = File.createTempFile(filename, DominioNomeRelatorio.EXTENSAO_CSV);
			
			out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);
			out.write(gerarCabecalhoDoRelatorio());

			if (lista != null && !lista.isEmpty()) {
				for(Integer linha : lista){
					out.write(gerarLinhasDoRelatorio(linha));
				}
				resultado = new ArquivoURINomeQtdVO(file.toURI(), filename, lista.size(), 1);
			}
			out.flush();
		} catch (IOException e) {
			 throw new ApplicationBusinessException( AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage());
		} finally {
			IOUtils.closeQuietly(out);
		}
		return resultado;
	}
	
	private String criarNomeArquivo() {
		return "VINCULO_INCORRETO_" + DateUtil.obterDataFormatada(new Date(System.currentTimeMillis()), DATE_PATTERN_DDMMYYYYHHMM) + DominioNomeRelatorio.EXTENSAO_CSV;
	}
	
	private String gerarCabecalhoDoRelatorio() {
		StringBuilder builder = new StringBuilder();
		builder.append(getResourceBundleValue("LABEL_PVI"))
			.append(SEPARADOR)
			.append(QUEBRA_LINHA);
		return builder.toString();
	}
	
	private String gerarLinhasDoRelatorio(Integer linha) {
		StringBuilder builder = new StringBuilder();
		builder.append(linha.intValue())
			.append(SEPARADOR)
			.append(QUEBRA_LINHA);
		return builder.toString();
	}	
}
