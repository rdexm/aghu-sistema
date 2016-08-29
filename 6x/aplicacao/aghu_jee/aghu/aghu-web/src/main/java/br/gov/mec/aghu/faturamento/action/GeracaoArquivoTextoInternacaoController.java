package br.gov.mec.aghu.faturamento.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.exception.AghuCSVBaseException.AghuCSVBaseExceptionExceptionCode;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioSituacaoCompetencia;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.vo.ArquivoURINomeQtdVO;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

public class GeracaoArquivoTextoInternacaoController extends ActionController {

	public enum GeracaoArquivoTextoInternacaoControllerExceptionCode implements BusinessExceptionCode {
		ARQ_ERRO_FISICO

	}
	
	private static final int BUFFER_SIZE_EQ_1M = 1024 * 1024;
	private static final long serialVersionUID = -4037292898432379219L;
	private FatCompetencia competencia;
	private ArquivoURINomeQtdVO arqVo;
	private Boolean gerouArquivoCSV;
	private Boolean gerouArquivo;

	private File fileNutricaoEnteralDigitada;
	private Boolean gerouArquivoNutricaoEnteral;

	private File fileNutricaoContas;
	private Boolean gerouArquivoContas;

	private static final Log LOG = LogFactory.getLog(GeracaoArquivoTextoInternacaoController.class);

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	private static final String MAGIC_MIME_TYPE_EQ_TEXT_CSV = "text/csv";

	@PostConstruct
	public void iniciarPagina() {
		begin(conversation);

		List<FatCompetencia> listaCpe = null;
		FatCompetencia cpe = null;

		listaCpe = this.obterCompetencias();
		if ((listaCpe != null) && !listaCpe.isEmpty()) {
			cpe = listaCpe.get(listaCpe.size() - 1);
			setCompetencia(cpe);
		}
		this.arqVo = null;
		this.gerouArquivoCSV = null;
		this.gerouArquivoNutricaoEnteral = null;
		this.gerouArquivoContas = null;
	}

	protected List<FatCompetencia> obterCompetencias() {

		List<FatCompetencia> result = null;

		result = faturamentoFacade.obterCompetenciasPorModuloESituacoes(DominioModuloCompetencia.INT, DominioSituacaoCompetencia.A);

		return result;
	}

	public List<FatCompetencia> pesquisarCompetencias(String objPesquisa) {
		try {
			return this.returnSGWithCount(
					faturamentoFacade.listarCompetenciaModuloMesAnoDtHoraInicioSemHora(faturamentoFacade.getCompetenciaId(objPesquisa)),
					pesquisarCompetenciasCount(objPesquisa));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return new ArrayList<FatCompetencia>();
	}

	public Long pesquisarCompetenciasCount(String objPesquisa) {
		try {
			return faturamentoFacade.listarCompetenciaModuloMesAnoDtHoraInicioSemHoraCount(faturamentoFacade.getCompetenciaId(objPesquisa));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return 0L;
	}

	public void dispararDownloadArquivo() throws BaseException {

		try {
			if (this.arqVo != null) {
				this.download(new File(this.arqVo.getUri()), MAGIC_MIME_TYPE_EQ_TEXT_CSV);
			}
			this.setGerouArquivoCSV(Boolean.FALSE);
		} catch (IOException e) {
			LOG.error("Erro disparando download de arquivo", e);
			throw new ApplicationBusinessException(GeracaoArquivoTextoInternacaoControllerExceptionCode.ARQ_ERRO_FISICO);
		}
	}

	public String contasPeriodo() {

		try {
			arqVo = faturamentoFacade.gerarArquivoContasPeriodo();
			apresentarMsgNegocio(Severity.INFO, "MSG_INFO_ARQ_GERADO", Integer.valueOf(arqVo.getQtdRegistros()),
					Integer.valueOf(arqVo.getQtdArquivos()));
			dispararDownloadArquivo();
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}

		return null;
	}

	/**
	 * 
	 * #2195
	 */
	public void gerarCSVContasProcedProfissionalVinculoIncorreto() {
		try {
			arqVo = faturamentoFacade.gerarCSVContasProcedProfissionalVinculoIncorreto();
			apresentarMsgNegocio(Severity.INFO, "ARQUIVO_GERADO_COM_SUCESSO_PVI", Integer.valueOf(arqVo.getQtdRegistros()),
					Integer.valueOf(arqVo.getQtdArquivos()));
			dispararDownloadArquivo();
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * 
	 * #2196
	 */
	public void gerarCSVContasNaoReap() {
		try {
			arqVo = faturamentoFacade.gerarCSVContasNaoReapresentadasCPF(competencia);
			apresentarMsgNegocio(Severity.INFO, "ARQUIVO_GERADO_COM_SUCESSO_CNR", Integer.valueOf(arqVo.getQtdRegistros()),
					Integer.valueOf(arqVo.getQtdArquivos()));
			dispararDownloadArquivo();
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * #2197
	 */
	public void gerarCSVNutricaoEnteral() {
		try {
			arqVo = faturamentoFacade.gerarCSVDadosContaNutricaoEnteral();
			apresentarMsgNegocio(Severity.INFO, "MSG_CONTAS_NUTRICAO_ETERAL_M1", Integer.valueOf(arqVo.getQtdRegistros()),
					Integer.valueOf(arqVo.getQtdArquivos()));
			dispararDownloadArquivo();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * #2194
	 */
	public void gerarCSVDadosContasComNPT() {
		try {
			arqVo = faturamentoFacade.gerarCSVDadosContasComNPT(getCompetencia());
			apresentarMsgNegocio(Severity.INFO, "MS01_CONTAS_COM_NPT", Integer.valueOf(arqVo.getQtdRegistros()),
					Integer.valueOf(arqVo.getQtdArquivos()));
			dispararDownloadArquivo();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * #2199
	 */
	public void gerarCSVDadosContaRepresentada() {
		try {
			arqVo = faturamentoFacade.obterDadosContaRepresentada(getCompetencia());
			apresentarMsgNegocio(Severity.INFO, "MS02_CONTA_REPRESENTADA", Integer.valueOf(arqVo.getQtdRegistros()),
					Integer.valueOf(arqVo.getQtdArquivos()));
			dispararDownloadArquivo();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * #41088
	 */
	public String gerarCSVProdutividadeFisiatria() {
		try {
			ArquivoURINomeQtdVO arqVo = null;
			arqVo = faturamentoFacade.obterDadosGeracaoArquivoProdutividadeFisiatria(getCompetencia());
			apresentarMsgNegocio(Severity.INFO, "MSG_INFO_ARQ_GERADO", Integer.valueOf(arqVo.getQtdRegistros()),
					Integer.valueOf(arqVo.getQtdArquivos()));
			this.setArqVo(arqVo);
			this.setGerouArquivoCSV(Boolean.TRUE);

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

		return "faturamento-geracaoArquivoTextoInternacao";
	}

	public void gerarCSVNutricaoEnteralDigitada() {
		if (verificaCompetencia()) {
			try {
				String mes = null;
				if (competencia.getId().getMes().toString().length() == 1) {
					mes = '0' + competencia.getId().getMes().toString();
				} else {
					mes = competencia.getId().getMes().toString();
				}

				String ano = competencia.getId().getAno().toString().substring(2, 4);
				String data1 = mes + ano + "000";
				String data2 = mes + ano + "999";

				fileNutricaoEnteralDigitada = faturamentoFacade.geraCSVRelatorioNutricaoEnteralDigitada(data1, data2, competencia.getId());
				gerouArquivoNutricaoEnteral = true;

			} catch (IOException e) {
				// Log.error("Exceção capturada: ", e);
				apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e,
						e.getLocalizedMessage()));
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}
	}

	public void gerarCSVContas() {
		if (verificaCompetencia()) {
			try {
				String mes = null;
				if (competencia.getId().getMes().toString().length() == 1) {
					mes = '0' + competencia.getId().getMes().toString();
				} else {
					mes = competencia.getId().getMes().toString();
				}

				String ano = competencia.getId().getAno().toString().substring(2, 4);
				String data1 = mes + ano + "000";
				String data2 = mes + ano + "999";

				fileNutricaoContas = faturamentoFacade.geraCSVContas(data1, data2, competencia.getId());
				gerouArquivoContas = true;

			} catch (IOException e) {
				// Log.error("Exceção capturada: ", e);
				apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e,
						e.getLocalizedMessage()));
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}
	}

	public boolean verificaCompetencia() {
		if (competencia == null) {
			this.apresentarMsgNegocio(Severity.ERROR, "COMPETENCIA_NAO_SELECIONADA");
			return false;
		}
		return true;
	}

	public void dispararDownloadNutricaoEnteralDigitada() {
		if (fileNutricaoEnteralDigitada != null) {
			try {
				internDispararDownload(fileNutricaoEnteralDigitada.toURI(), "ENTERAL_DIG_" + competencia.getId().getMes() + "_"
						+ competencia.getId().getAno(), MAGIC_MIME_TYPE_EQ_TEXT_CSV);
				gerouArquivoNutricaoEnteral = false;
				fileNutricaoEnteralDigitada = null;

			} catch (IOException e) {
				// getLog().error("Exceção capturada: ", e);
				apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e,
						e.getLocalizedMessage()));
			}
		}
	}

	public void dispararDownloadContas() {
		if (fileNutricaoContas != null) {
			try {
				internDispararDownload(fileNutricaoContas.toURI(), "NPT_" + competencia.getId().getMes() + "_"
						+ competencia.getId().getAno(), MAGIC_MIME_TYPE_EQ_TEXT_CSV);
				// download(fileNutricaoContas);
				gerouArquivoContas = false;
				fileNutricaoContas = null;

			} catch (IOException e) {
				// getLog().error("Exceção capturada: ", e);
				apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e,
						e.getLocalizedMessage()));
			}
		}
	}

	protected void internDispararDownload(final URI arquivo, final String nomeArq, final String mimeType) throws IOException {
		FacesContext facesContext = null;
		HttpServletResponse response = null;
		ServletOutputStream out = null;
		FileInputStream in = null;
		byte[] buffer = null;
		int len = 0;
		facesContext = FacesContext.getCurrentInstance();
		response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
		response.setContentType(mimeType);
		response.addHeader("Content-disposition", "attachment; filename=\"" + nomeArq + "\"");
		response.addHeader("Cache-Control", "no-cache");
		response.setStatus(200);

		buffer = new byte[BUFFER_SIZE_EQ_1M];
		// committing status and headers
		response.flushBuffer();
		out = response.getOutputStream();
		in = new FileInputStream(new File(arquivo));
		while ((len = in.read(buffer)) >= 0) {
			out.write(buffer, 0, len);
		}

		out.flush();
		out.close();
		in.close();

		facesContext.responseComplete();
	}

	public String voltar() {
		return "voltar";
	}

	public FatCompetencia getCompetencia() {
		return this.competencia;
	}

	public void setCompetencia(final FatCompetencia competencia) {
		this.competencia = competencia;
	}

	public ArquivoURINomeQtdVO getArqVo() {
		return this.arqVo;
	}

	public void setArqVo(final ArquivoURINomeQtdVO arqVo) {
		this.arqVo = arqVo;
	}

	public Boolean getGerouArquivo() {

		return this.gerouArquivo;
	}

	public void setGerouArquivo(final Boolean gerouArquivo) {

		this.gerouArquivo = gerouArquivo;
	}

	public Boolean getGerouArquivoCSV() {
		return gerouArquivoCSV;
	}

	public void setGerouArquivoCSV(Boolean gerouArquivoCSV) {
		this.gerouArquivoCSV = gerouArquivoCSV;
	}

	public Boolean getGerouArquivoNutricaoEnteral() {
		return gerouArquivoNutricaoEnteral;
	}

	public void setGerouArquivoNutricaoEnteral(Boolean gerouArquivoNutricaoEnteral) {
		this.gerouArquivoNutricaoEnteral = gerouArquivoNutricaoEnteral;
	}

	public Boolean getGerouArquivoContas() {
		return gerouArquivoContas;
	}

	public void setGerouArquivoContas(Boolean gerouArquivoContas) {
		this.gerouArquivoContas = gerouArquivoContas;
	}

	public File getFileNutricaoContas() {
		return fileNutricaoContas;
	}

	public void setFileNutricaoContas(File fileNutricaoContas) {
		this.fileNutricaoContas = fileNutricaoContas;
	}

	public File getFileNutricaoEnteralDigitada() {
		return fileNutricaoEnteralDigitada;
	}

	public void setFileNutricaoEnteralDigitada(File fileNutricaoEnteralDigitada) {
		this.fileNutricaoEnteralDigitada = fileNutricaoEnteralDigitada;
	}

}
