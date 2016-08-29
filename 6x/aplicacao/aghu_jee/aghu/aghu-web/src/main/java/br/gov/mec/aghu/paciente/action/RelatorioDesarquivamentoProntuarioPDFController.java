package br.gov.mec.aghu.paciente.action;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.context.FacesContext;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.vo.DesarquivamentoProntuarioVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;

public class RelatorioDesarquivamentoProntuarioPDFController extends ActionReport {

	private static final long serialVersionUID = -3146440883903816761L;
	
	private static final String REDIRECIONA_RELATORIO_DESARQUIVAMENTO_PRONTUARIO = "relatorioDesarquivamentoProntuario";	

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IParametroFacade parametroFacade;

	private Date dataMovimento;
	private Integer solicitacao;
	
	private static final Log LOG = LogFactory.getLog(RelatorioDesarquivamentoProntuarioPDFController.class);
	
	@Override
	public Collection<DesarquivamentoProntuarioVO> recuperarColecao() {
		return pacienteFacade.pesquisaDesarquivamentoProntuario(this.solicitacao, this.dataMovimento);
	}

	public void directPrint() {
		DocumentoJasper documento = obterDocumentoJasper();
		
		try {
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
			
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
			
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			this.apresentarMsgNegocio(Severity.ERROR,"ERRO_GERAR_RELATORIO");
		}
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "/br/gov/mec/aghu/paciente/prontuario/report/relatorioDesarquivamentoProntuario.jasper";
	}
	
	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 * 
	 * @param out
	 * @param data
	 * @throws IOException
	 * @throws JRException
	 * @throws MECBaseException
	 * @throws DocumentException
	 */
	public StreamedContent getRenderPdf()  {
		DocumentoJasper documento = obterDocumentoJasper();
			
		try {
			return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
		} catch (IOException e) {
			LOG.error(e.getMessage());
		} catch (JRException e) {
			LOG.error(e.getMessage());
		} catch (DocumentException e) {
			LOG.error(e.getMessage());
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
		return null;
	}
	
	public DocumentoJasper gerarDocumentoJasper() throws ApplicationBusinessException{
		return super.gerarDocumento();
	}
	
	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		Date dataAtual = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		params.put("dataAtual", sdf.format(dataAtual));
		params.put("nomeRelatorio", "AIPR_DESARQ_PRNT");
		String nomeHospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		if(nomeHospital.isEmpty()) {
			try {
				nomeHospital = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL).getVlrTexto();
			} catch (ApplicationBusinessException e) {
				LOG.error(e.getLocalizedMessage());
			}
		}
		
		params.put("nomeHospital", nomeHospital);

		return params;
	}
	
	public String voltar() {
		return REDIRECIONA_RELATORIO_DESARQUIVAMENTO_PRONTUARIO;
	}
	
	private DocumentoJasper obterDocumentoJasper() {
		return (DocumentoJasper) FacesContext
				.getCurrentInstance()
				.getExternalContext()
				.getSessionMap().get("documentoRelatorioDesarquivamentoProntuarioPdf");
	}

	// GETs AND SETs
	public Date getDataMovimento() {
		return dataMovimento;
	}

	public void setDataMovimento(Date dataMovimento) {
		this.dataMovimento = dataMovimento;
	}

	public Integer getSolicitacao() {
		return solicitacao;
	}

	public void setSolicitacao(Integer solicitacao) {
		this.solicitacao = solicitacao;
	}
}
