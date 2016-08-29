package br.gov.mec.aghu.paciente.prontuario.action;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.ProntuarioInternacaoVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;

@SessionScoped
@Named
public class RelatorioDesarquivamentoProntuarioInternacaoController extends ActionReport {
	
	private static final long serialVersionUID = 5665948585978396845L;
	private static final Log LOG = LogFactory.getLog(RelatorioDesarquivamentoProntuarioInternacaoController.class);
	private static final String REDIRECT_MONITOR_DESARQUIVAMENTO_PRONTUARIO = "monitorDesarquivamentoProntuario";
	private static final String REDIRECT_RELATORIO_INTERNACAO = "relatorioMonitorDesarquivamentoProntuarioInternacao";

	@Inject
	private MonitoramentoDesarquivamentoProntuarioController monitoramentoDesarquivamentoProntuarioController;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	private Integer abaSelecionada;
	
	private Collection<ProntuarioInternacaoVO> listaProntuarios;

	public String print(){
		return REDIRECT_RELATORIO_INTERNACAO;
	}
	
	public String voltar(){
		LOG.info("voltar"+REDIRECT_MONITOR_DESARQUIVAMENTO_PRONTUARIO);
		monitoramentoDesarquivamentoProntuarioController.setAbaSelecionada(abaSelecionada);
		return REDIRECT_MONITOR_DESARQUIVAMENTO_PRONTUARIO;
	}
	
	@Override
	public Collection<ProntuarioInternacaoVO> recuperarColecao()
			throws ApplicationBusinessException {
		return listaProntuarios;

	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "/br/gov/mec/aghu/paciente/report/relatorioDesarquivamentoProntuarioInternacao.jasper";
	}

	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		Date dataAtual = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		params.put("dataAtual", sdf.format(dataAtual));
		params.put("nomeRelatorio", "AINR_AVISO_SAMIS2");
		params.put("nomeHospital", cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal());

		return params;
	}

	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 * 
	 * @param out
	 * @param data
	 * @throws IOException
	 * @throws SystemException
	 * @throws JRException
	 * @throws ApplicationBusinessException
	 * @throws DocumentException 
	 */
	public StreamedContent getRenderPdf() throws IOException,
			ApplicationBusinessException, JRException, SystemException, DocumentException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}

	// GETs AND SETs

	public Collection<ProntuarioInternacaoVO> getListaProntuarios() {
		return listaProntuarios;
	}

	public void setListaProntuarios(Collection<ProntuarioInternacaoVO> listaProntuarios) {
		this.listaProntuarios = listaProntuarios;
	}

	public Integer getAbaSelecionada() {
		return abaSelecionada;
	}

	public void setAbaSelecionada(Integer abaSelecionada) {
		this.abaSelecionada = abaSelecionada;
	}
}
