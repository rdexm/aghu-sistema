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
import br.gov.mec.aghu.business.exception.AghuCSVBaseException.AghuCSVBaseExceptionExceptionCode;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.vo.ItensRealizadosIndividuaisVO;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.report.DocumentoJasper;

public class RelatorioItensRealizIndvController extends ActionReport {

	private static final String EXCECAO_CAPTURADA = "Exceção capturada: ";

	private static final long serialVersionUID = 621377442632559721L;
	
	private static final Log LOG = LogFactory.getLog(RelatorioItensRealizIndvController.class);

	private Log getLog() {
		return LOG;
	}

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	private FatCompetencia competencia;

	private FatItensProcedHospitalar procedimentoHospitalarInicial;

	private FatItensProcedHospitalar procedimentoHospitalarFinal;

	private List<ItensRealizadosIndividuaisVO> colecao = new LinkedList<ItensRealizadosIndividuaisVO>();

	private Boolean gerouArquivo;

	private Boolean gerouArquivoPDF;

	private Boolean historico = false;

	private String fileName;
	
	@PostConstruct
	protected void init(){
		begin(conversation,true);
	}

	@Override
	public Collection<ItensRealizadosIndividuaisVO> recuperarColecao() {
		if (historico) {
			try {
				this.colecao = faturamentoFacade.listarItensRealizadosIndividuaisHistPorCompetencia(competencia.getId().getDtHrInicio(),
						competencia.getId().getAno(), competencia.getId().getMes(),
						procedimentoHospitalarInicial != null ? procedimentoHospitalarInicial.getCodTabela() : null,
						procedimentoHospitalarFinal != null ? procedimentoHospitalarFinal.getCodTabela() : null);
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		} else {
			try {
				this.colecao = faturamentoFacade.listarItensRealizadosIndividuaisPorCompetencia(competencia.getId().getDtHrInicio(),
						competencia.getId().getAno(), competencia.getId().getMes(),
						procedimentoHospitalarInicial != null ? procedimentoHospitalarInicial.getCodTabela() : null,
						procedimentoHospitalarFinal != null ? procedimentoHospitalarFinal.getCodTabela() : null);
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		}
		return colecao;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/faturamento/report/relatorioItensRealzIndv.jasper";
	}

	public Map<String, Object> recuperarParametros() {
		final Map<String, Object> params = new HashMap<String, Object>();

		final String local = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();

		params.put("nomeHospital", (local != null) ? local.toUpperCase() : local);
		params.put("competencia", String.format("%02d", competencia.getId().getMes()) + "/" + competencia.getId().getAno());

		return params;
	}

	public void gerarCSV() {
		try {
			historico = false;
			fileName = faturamentoFacade.geraCSVRelatorioItensRealzIndv(competencia.getId().getDtHrInicio(), competencia.getId().getAno(),
					competencia.getId().getMes(), procedimentoHospitalarInicial != null ? procedimentoHospitalarInicial.getCodTabela()
							: null, procedimentoHospitalarFinal != null ? procedimentoHospitalarFinal.getCodTabela() : null);
			gerouArquivo = true;

		} catch (IOException e) {
			getLog().error(EXCECAO_CAPTURADA, e);
			apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV,e));
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void gerarCSVHist() {
		try {
			historico = true;
			fileName = faturamentoFacade.geraCSVRelatorioItensRealzIndvHist(competencia.getId().getDtHrInicio(), competencia.getId()
					.getAno(), competencia.getId().getMes(),
					procedimentoHospitalarInicial != null ? procedimentoHospitalarInicial.getCodTabela() : null,
					procedimentoHospitalarFinal != null ? procedimentoHospitalarFinal.getCodTabela() : null);
			gerouArquivo = true;

		} catch (IOException e) {
			getLog().error(EXCECAO_CAPTURADA, e);
			apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV,e));
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void dispararDownload() {
		if (fileName != null) {
			try {
				download(fileName, DominioNomeRelatorio.FATR_ITENS_INDIV.toString() + (historico ? "_HIS" : "") + ".csv");
				gerouArquivo = false;
				fileName = null;

			} catch (IOException e) {
				getLog().error(EXCECAO_CAPTURADA, e);
				apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV,e));
			}
		}
	}

	public void gerarPDF() {

		historico = false;
		DocumentoJasper documento;
		try {
			documento = gerarDocumento();

			fileName = faturamentoFacade.gerarPDFRelatorioItensRealzIndv(documento.getPdfByteArray(false));
			gerouArquivoPDF = true;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			getLog().error(EXCECAO_CAPTURADA, e);
			apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_PDF,e));
		}

	}

	public void gerarPDFHist() {

		historico = true;
		DocumentoJasper documento;
		try {
			documento = gerarDocumento();
			fileName = faturamentoFacade.gerarPDFRelatorioItensRealzIndv(documento.getPdfByteArray(false));
			gerouArquivoPDF = true;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			getLog().error(EXCECAO_CAPTURADA, e);
			apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_PDF,e));
		} 

	}

	public void dispararDownloadPDF() {
		if (fileName != null) {
			try {
				download(fileName);
				gerouArquivoPDF = false;
				fileName = null;
			} catch (IOException e) {
				getLog().error(EXCECAO_CAPTURADA, e);
				apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_PDF,e));
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

	public List<FatItensProcedHospitalar> pesquisarTabela(final String param) {
		return this.returnSGWithCount(faturamentoFacade.listarFatItensProcedHospitalar(param),pesquisarTabelaCount(param));
	}

	public Long pesquisarTabelaCount(final String param) {
		return faturamentoFacade.listarFatItensProcedHospitalarCount(param);
	}

	public void limpar() {
		competencia = null;
		procedimentoHospitalarInicial = null;
		procedimentoHospitalarFinal = null;
		historico = false;
		fileName = null;
		gerouArquivo = false;
		gerouArquivoPDF = false;
	}

	public FatCompetencia getCompetencia() {
		return competencia;
	}

	public void setCompetencia(FatCompetencia competencia) {
		this.competencia = competencia;
	}

	public List<ItensRealizadosIndividuaisVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<ItensRealizadosIndividuaisVO> colecao) {
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

	public Boolean getGerouArquivoPDF() {
		return gerouArquivoPDF;
	}

	public void setGerouArquivoPDF(Boolean gerouArquivoPDF) {
		this.gerouArquivoPDF = gerouArquivoPDF;
	}

	public FatItensProcedHospitalar getProcedimentoHospitalarInicial() {
		return procedimentoHospitalarInicial;
	}

	public void setProcedimentoHospitalarInicial(FatItensProcedHospitalar procedimentoHospitalarInicial) {
		this.procedimentoHospitalarInicial = procedimentoHospitalarInicial;
	}

	public FatItensProcedHospitalar getProcedimentoHospitalarFinal() {
		return procedimentoHospitalarFinal;
	}

	public void setProcedimentoHospitalarFinal(FatItensProcedHospitalar procedimentoHospitalarFinal) {
		this.procedimentoHospitalarFinal = procedimentoHospitalarFinal;
	}
}