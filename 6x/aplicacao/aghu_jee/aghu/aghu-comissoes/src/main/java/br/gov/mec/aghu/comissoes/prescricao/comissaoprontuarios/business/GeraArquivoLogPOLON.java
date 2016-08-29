package br.gov.mec.aghu.comissoes.prescricao.comissaoprontuarios.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.AipLogProntOnlines;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class GeraArquivoLogPOLON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(GeraArquivoLogPOLON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 7748120085457948105L;
	private static final String SEPARADOR = ";";
	private static final String ENCODE = "ISO-8859-1";
	private static final String EXTENSAO_CSV = ".csv";

	public String gerarCSVLogProntuarioOnline(List<AipLogProntOnlines> logs)
			throws IOException {

		File file = null;
		Writer out = null;
		String fileName = null;

		if (logs != null && !logs.isEmpty()) {

			fileName = "Log_POL_"
					+ new SimpleDateFormat("MM_yyyy").format(new Date());
			file = File.createTempFile(fileName, EXTENSAO_CSV);
			out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);

			// cabecalho
			out.write("DATA;SERVIDOR;M\u00C1QUINA;PRONTUARIO;OCORRENCIA\n");
			for (AipLogProntOnlines log : logs) {
				StringBuilder criadoEm = new StringBuilder("");
				StringBuilder servidor = new StringBuilder("");
				StringBuilder machine = new StringBuilder("");
				StringBuilder codigo = new StringBuilder("");
				StringBuilder ocorrencia = new StringBuilder("");

				if (log.getCriadoEm() != null) {
					criadoEm.append(DateUtil.dataToString(log.getCriadoEm(),
							DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
				}

				if (log.getServidor().getPessoaFisica().getNome() != null
						&& !log.getServidor().getPessoaFisica().getNome()
								.isEmpty()) {
					servidor.append(log.getServidor().getPessoaFisica()
							.getNome());
				}

				if (log.getMachine() != null && !log.getMachine().isEmpty()) {
					machine.append(log.getMachine());
				}

				if (log.getPaciente().getProntuario() != null) {
					codigo.append(CoreUtil.formataProntuario(log.getPaciente()
							.getProntuario()));
				}

				if (log.getOcorrencia().getDescricao() != null
						&& !log.getOcorrencia().getDescricao().isEmpty()) {
					ocorrencia.append(log.getOcorrencia().getDescricao());
				}

				out.write(criadoEm.toString() + SEPARADOR + servidor.toString()
						+ SEPARADOR + machine.toString() + SEPARADOR
						+ codigo.toString() + SEPARADOR + ocorrencia.toString()
						+ "\n");
			}

			fileName = file.getAbsolutePath();
			out.flush();
			out.close();
		}

		return fileName;
	}

	public String nameHeaderEfetuarDownloadCSVLogProntuarioOnline(){
		 return "Log_POL_"  + new SimpleDateFormat("MM_yyyy").format(new Date());
	}

}
