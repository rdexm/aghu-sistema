package br.gov.mec.aghu.certificacaodigital.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.certificacaodigital.vo.RelatorioControlePendenciasVO;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.dominio.DominioOrdenacaoRelatorioControlePendencias;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class RelatorioCSVCertificacaoDigitalON extends BaseBusiness {
	private static final Log LOG = LogFactory.getLog(RelatorioCSVCertificacaoDigitalON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private ICertificacaoDigitalFacade certificacaoDigitalFacade;

	private static final long serialVersionUID = 9091267120235415086L;
	private static final String SEPARADOR = ";";
	private static final String ENCODE = "ISO-8859-1";
	private static final String EXTENSAO = ".csv";

	@SuppressWarnings("PMD.NPathComplexity")
	public String geraCSVRelatorioControlePendencias(
			final RapServidores rapServidores,
			final FccCentroCustos fccCentroCustos,
			final DominioOrdenacaoRelatorioControlePendencias ordenacao) throws BaseException, IOException {

		List<RelatorioControlePendenciasVO> colecao = new ArrayList<RelatorioControlePendenciasVO>(
				0);
		
		colecao = getCertificacaoDigitalFacade()
				.pesquisaPendenciaAssinaturaDigital(rapServidores,
						fccCentroCustos, ordenacao);

		final File file = File.createTempFile(
				DominioNomeRelatorio.PENDENCIAS_ASSINATURA_DIGITAL.toString(),
				EXTENSAO);
		final Writer out = new OutputStreamWriter(new FileOutputStream(file),
				ENCODE);

		out.write(("PROFISSIONAL" + SEPARADOR + "MATRÍCULA" + SEPARADOR
				+ "VÍNCULO" + SEPARADOR + "SERVIÇO" + SEPARADOR + "TOTAL" + this
				.getLineSeparator()));

		for (final RelatorioControlePendenciasVO vo : colecao) {
			out.write((vo.getNomePessoaFisica() != null ? vo
					.getNomePessoaFisica() : "")
					+ SEPARADOR
					+ (vo.getMatriculaId() != null ? vo.getMatriculaId() : "")
					+ SEPARADOR
					+ (vo.getCodigoVinculo() != null ? vo.getCodigoVinculo()
							: "")
					+ SEPARADOR
					+ (vo.getDescricaoCentroCusto() != null ? vo
							.getDescricaoCentroCusto() : "")
					+ SEPARADOR
					+ (vo.getCountDocumentos() != null ? vo
							.getCountDocumentos() : "")
					+ SEPARADOR
					+ getLineSeparator());
		}

		out.flush();
		out.close();

		return file.getAbsolutePath();
	}

	protected ICertificacaoDigitalFacade getCertificacaoDigitalFacade() {
		return this.certificacaoDigitalFacade;
	}

	private String getLineSeparator() {
		String lineSeparator = System.getProperty("line.separator");
		if (lineSeparator == null) {
			lineSeparator = "\n";
		}
		return lineSeparator;
	}

	public String nameHeaderDownloadedCSVRelatorioControlePendencias(){
		return DominioNomeRelatorio.PENDENCIAS_ASSINATURA_DIGITAL + "_" + new SimpleDateFormat("MM_yyyy").format(new Date());
	}
}
