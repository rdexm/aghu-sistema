package br.gov.mec.aghu.exames.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.DocumentException;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.dominio.DominioViewMonitorPendenciasExames;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.vo.MonitorPendenciasExamesFiltrosPesquisaVO;
import br.gov.mec.aghu.exames.vo.MonitorPendenciasExamesVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import net.sf.jasperreports.engine.JRException;

//@Scope(ScopeType.SESSION)
public class RelatorioMonitorPendenciasExamesController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	private static final Log LOG = LogFactory.getLog(RelatorioMonitorPendenciasExamesController.class);
	
	private static final String PAGE_EXAMES_RELATORIO_MONITOR_PENDENCIA_PDF = "exames-relatorioMonitorPendenciaPdf";

	private static final long serialVersionUID = -1383354508251853544L;

	private static final String MONITOR_PENDENCIAS_EXAMES = "exames-monitorPendenciasExames";

	@Inject
	private SistemaImpressao sistemaImpressao;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	@EJB
	private IExamesFacade examesFacade;

	private MonitorPendenciasExamesFiltrosPesquisaVO filtrosPesquisa;
	private List<MonitorPendenciasExamesVO> colecao;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public String print()throws JRException, IOException, DocumentException , BaseException {
		this.pesquisarMonitorPendenciasExames();
	
		DocumentoJasper documento = this.gerarDocumento();
		media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(false)));
		return PAGE_EXAMES_RELATORIO_MONITOR_PENDENCIA_PDF;
	}

	public void directPrint() throws SistemaImpressaoException, ApplicationBusinessException {

		this.pesquisarMonitorPendenciasExames();

		try {
			DocumentoJasper documento = gerarDocumento();
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");

		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
			
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}

	@Override
	public Collection<MonitorPendenciasExamesVO> recuperarColecao() throws ApplicationBusinessException {
		return this.colecao;
	}

	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		Date dataAtual = new Date();
		SimpleDateFormat sdf_1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		SimpleDateFormat sdf_2 = new SimpleDateFormat("dd/MM/yyyy");
		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		params.put("dataAtual", sdf_1.format(dataAtual));
		params.put("hospitalLocal", hospital);
		params.put("nomeRelatorio", "AELR_MON_PENDENCIA");
		if(filtrosPesquisa.getDataReferenciaIni()!=null){
			params.put("periodo", "Período de "+sdf_2.format(filtrosPesquisa.getDataReferenciaIni()) + " a "+ sdf_2.format(filtrosPesquisa.getDataReferenciaFim()));
		}
		params.put("totalRegistros", colecao.size() - 1);

		StringBuffer tituloRelatorio = new StringBuffer(50);
		tituloRelatorio.append("MONITOR DE PENDÊNCIAS");
		params.put("tituloRelatorio", tituloRelatorio.toString() +" - "+filtrosPesquisa.getViewMonitorPendenciasExames().getDescricao());

		return params;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		if(DominioViewMonitorPendenciasExames.AREA_EXECUTORA.equals(filtrosPesquisa.getViewMonitorPendenciasExames())){
			return "/br/gov/mec/aghu/exames/report/relatorioMonitorPendenciaAreaExecutora.jasper";
		}
		return "/br/gov/mec/aghu/exames/report/relatorioMonitorPendencia.jasper";	
	}
	
	public StreamedContent getRenderPdf() throws IOException, ApplicationBusinessException, JRException, SystemException, DocumentException {
		DocumentoJasper documento = this.gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}

	private void pesquisarMonitorPendenciasExames() {
		if (this.filtrosPesquisa != null) {
			try {
				this.colecao = this.examesFacade.pesquisarMonitorPendenciasExames(this.filtrosPesquisa);
			} catch (SistemaImpressaoException e) {
				apresentarExcecaoNegocio(e);
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
			}
		}
	}
	
	public String voltar(){
		return MONITOR_PENDENCIAS_EXAMES;
	}

	public MonitorPendenciasExamesFiltrosPesquisaVO getFiltrosPesquisa() {
		return filtrosPesquisa;
	}

	public void setFiltrosPesquisa(MonitorPendenciasExamesFiltrosPesquisaVO filtrosPesquisa) {
		this.filtrosPesquisa = filtrosPesquisa;
	}

	public List<MonitorPendenciasExamesVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<MonitorPendenciasExamesVO> colecao) {
		this.colecao = colecao;
	}
}