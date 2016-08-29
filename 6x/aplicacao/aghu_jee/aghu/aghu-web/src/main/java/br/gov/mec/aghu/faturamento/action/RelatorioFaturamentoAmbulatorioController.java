package br.gov.mec.aghu.faturamento.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.business.exception.AghuCSVBaseException.AghuCSVBaseExceptionExceptionCode;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.vo.FaturaAmbulatorioVO;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.FatCompetencia;

public class RelatorioFaturamentoAmbulatorioController extends ActionReport {

	private static final long serialVersionUID = 621377442632559721L;
	
	private static final Log LOG = LogFactory.getLog(RelatorioFaturamentoAmbulatorioController.class);

	private Log getLog() {
		return LOG;
	}

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	@EJB
	private IAghuFacade aghuFacade;

	private FatCompetencia competencia;

	private List<FaturaAmbulatorioVO> colecao = new LinkedList<FaturaAmbulatorioVO>();

	private Boolean gerouArquivo;

	private String fileName;

	@PostConstruct
	protected void init() {
		begin(conversation, true);
	}

	public String visualizarRelatorio() {
		return "relatorioFaturamentoAmbulatorioPdf";
	}

	@Override
	public Collection<FaturaAmbulatorioVO> recuperarColecao() {
		colecao = faturamentoFacade.listarFaturamentoAmbulatorioPorCompetencia(competencia.getId().getMes(), competencia.getId()
				.getAno(), competencia.getId().getDtHrInicio());

		return colecao;
	}

	public void directPrint() {
		try {
			super.directPrint();
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			getLog().error("Exceção capturada: ", e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/faturamento/report/relatorioFaturamentoAmb.jasper";
	}

	public Map<String, Object> recuperarParametros() {
		final Map<String, Object> params = new HashMap<String, Object>();

		final String local = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();

		params.put("nomeHospital", (local != null) ? local.toUpperCase() : local);
		params.put("competencia", String.format("%02d", competencia.getId().getMes()) + "/" + competencia.getId().getAno());
		params.put("cgcCpf", aghuFacade.obterCgcHospital());
		params.put("endereco", aghuFacade.getEndereco() != null ? aghuFacade.getEndereco().toUpperCase() : null);
		params.put("teto", "TOTAL");
		params.put("dtCompetencia",
				DateUtil.obterDataFormatada(competencia.getId().getDtHrInicio(), "dd/MM/yyyy")
						+ (competencia.getDtHrFim() != null ? " À " + DateUtil.obterDataFormatada(competencia.getDtHrFim(), "dd/MM/yyyy")
								: ""));

		return params;
	}

	public void gerarCSV() {
		try {
			fileName = faturamentoFacade.geraCSVRelatorioFaturaAmbulatorio(competencia);
			gerouArquivo = true;
		} catch (IOException e) {
			getLog().error("Exceção capturada: ", e);
			apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e));
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void dispararDownload() {
		if (fileName != null) {
			try {
				download(fileName,
						DominioNomeRelatorio.FATR_FATURA_AMB.toString() + "_" + String.format("%02d", competencia.getId().getMes()) + "_"
								+ competencia.getId().getAno() + ".csv");
				gerouArquivo = false;
				fileName = null;
			} catch (IOException e) {
				getLog().error("Exceção capturada: ", e);
				apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e));
			}
		}
	}

	public List<FatCompetencia> pesquisarCompetencias(String objPesquisa) {
		try {
			return this.returnSGWithCount(faturamentoFacade.listarCompetenciaModuloMesAnoDtHoraInicioSemHora(faturamentoFacade.getCompetenciaId(objPesquisa,
					DominioModuloCompetencia.AMB)),pesquisarCompetenciasCount(objPesquisa));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return new ArrayList<FatCompetencia>();
	}

	public Long pesquisarCompetenciasCount(String objPesquisa) {
		try {
			return faturamentoFacade.listarCompetenciaModuloMesAnoDtHoraInicioSemHoraCount(faturamentoFacade.getCompetenciaId(objPesquisa,
					DominioModuloCompetencia.AMB));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return 0L;
	}

	public void limpar() {
		competencia = null;
		fileName = null;
		gerouArquivo = false;
	}

	public String voltar() {
		return "relatorioFaturamentoAmbulatorio";
	}

	public FatCompetencia getCompetencia() {
		return competencia;
	}

	public void setCompetencia(FatCompetencia competencia) {
		this.competencia = competencia;
	}

	public List<FaturaAmbulatorioVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<FaturaAmbulatorioVO> colecao) {
		this.colecao = colecao;
	}

	public Boolean getGerouArquivo() {
		return gerouArquivo;
	}

	public void setGerouArquivo(Boolean gerouArquivo) {
		this.gerouArquivo = gerouArquivo;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
