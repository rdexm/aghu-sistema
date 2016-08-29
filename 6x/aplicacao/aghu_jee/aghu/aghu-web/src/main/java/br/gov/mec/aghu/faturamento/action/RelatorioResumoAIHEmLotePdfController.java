package br.gov.mec.aghu.faturamento.action;

import java.io.File;
import java.io.FileOutputStream;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.vo.ResumoAIHEmLoteVO;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

public class RelatorioResumoAIHEmLotePdfController extends ActionReport {

	private static final String RELATORIO_RESUMO_AIH_EM_LOTE = "relatorioResumoAIHEmLote";

	private static final long serialVersionUID = -3483259106802867772L;
	
	private static final Log LOG = LogFactory.getLog(RelatorioResumoAIHEmLotePdfController.class);

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;


	private Integer cthSeq;

	private Date dtInicial;

	private Date dtFinal;

	private Boolean autorizada;

	private String iniciaisPaciente;

	private Boolean reapresentada;

	private Boolean directPrint = false;

	public enum RelatorioResumoAIHEmLotePdfExceptionCode implements BusinessExceptionCode {
		ERRO_AO_GERAR_PDF_RELATORIO_RESUMO_AIH_EM_LOTE,
		WARN_SEM_DADOS_CONTA_INFORMADA_PDF_RELATORIO;
	}
	
	@PostConstruct
	protected void init(){
		begin(conversation);
	}
	
	private Log getLog() {
		return LOG;
	}

	public String inicio() {

		if (iniciaisPaciente != null && iniciaisPaciente.isEmpty()) {
			iniciaisPaciente = null;
		}
		
		if (directPrint) {
			try {
				directPrint();
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			} catch (Exception e) {
				getLog().error("Exceção capturada: ", e);
				apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
			}
		}
		return null;
	}

	public String gerarPdf() {
		try {
			DocumentoJasper documento = gerarDocumento();

			final File file = File.createTempFile(DominioNomeRelatorio.FATR_RESUMO_AIH.toString(), ".pdf");
			final FileOutputStream out = new FileOutputStream(file);

			out.write(documento.getPdfByteArray(false));
			out.flush();
			out.close();

			return file.getAbsolutePath();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			getLog().error("Exceção capturada: ", e);
			apresentarExcecaoNegocio(new BaseException(
					RelatorioResumoAIHEmLotePdfExceptionCode.ERRO_AO_GERAR_PDF_RELATORIO_RESUMO_AIH_EM_LOTE));
		}
		return null;
	}

	@Override
	public Collection<ResumoAIHEmLoteVO> recuperarColecao() {
		final List<ResumoAIHEmLoteVO> result = faturamentoFacade.pesquisarResumoAIHEmLote(cthSeq, dtInicial, dtFinal, autorizada,
				iniciaisPaciente, reapresentada);

		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoIPv4HostRemoto().getHostName();
		} catch (UnknownHostException e) {
			getLog().error("Exceção caputada:", e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}

		final Date dataFimVinculoServidor = new Date();

		for (ResumoAIHEmLoteVO vo : result) {

			// Atualiza o campo ind_impressao_espelho de cada conta
			try {
				FatContasHospitalares conta = faturamentoFacade.obterContaHospitalar(vo.getCthSeq());

				if (conta != null && Boolean.FALSE.equals(conta.getIndImpressaoEspelho())) {

					FatContasHospitalares contaClone = faturamentoFacade.clonarContaHospitalar(conta);
					conta.setIndImpressaoEspelho(true);
					faturamentoFacade.persistirContaHospitalar(conta, contaClone, nomeMicrocomputador, dataFimVinculoServidor);
				}
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			} catch (Exception e) {
				getLog().error("Exceção capturada: ", e);
				apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
			}
		}
		return result;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/faturamento/report/relatorioResumoCobrancaAih.jasper";
	}

	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		String subReport = "br/gov/mec/aghu/faturamento/report/relatorioResumoCobrancaAihServicos.jasper";
		Date dataAtual = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		params.put("dataAtual", sdf.format(dataAtual));
		params.put("nomeRelatorio", "FATR_RESUMO_AIH");
		params.put("subRelatorio", Thread.currentThread().getContextClassLoader().getResourceAsStream(subReport));

		String local = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		params.put("nomeHospital", (local != null) ? local.toUpperCase() : local);
		params.put("previa", false);

		return params;
	}

	public String voltar(){
		return RELATORIO_RESUMO_AIH_EM_LOTE;
	}
	
	public Integer getCthSeq() {
		return cthSeq;
	}

	public void setCthSeq(Integer cthSeq) {
		this.cthSeq = cthSeq;
	}

	public Date getDtInicial() {
		return dtInicial;
	}

	public void setDtInicial(Date dtInicial) {
		this.dtInicial = dtInicial;
	}

	public Date getDtFinal() {
		return dtFinal;
	}

	public void setDtFinal(Date dtFinal) {
		this.dtFinal = dtFinal;
	}

	public String getIniciaisPaciente() {
		return iniciaisPaciente;
	}

	public void setIniciaisPaciente(String iniciaisPaciente) {
		this.iniciaisPaciente = iniciaisPaciente;
	}

	public Boolean getReapresentada() {
		return reapresentada;
	}

	public void setReapresentada(Boolean reapresentada) {
		this.reapresentada = reapresentada;
	}

	public Boolean getAutorizada() {
		return autorizada;
	}

	public void setAutorizada(Boolean autorizada) {
		this.autorizada = autorizada;
	}

	public Boolean getDirectPrint() {
		return directPrint;
	}

	public void setDirectPrint(Boolean directPrint) {
		this.directPrint = directPrint;
	}

}
