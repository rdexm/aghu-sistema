package br.gov.mec.aghu.controleinfeccao.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.DocumentException;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.controleinfeccao.business.IControleInfeccaoFacade;
import br.gov.mec.aghu.controleinfeccao.vo.FiltroListaPacienteCCIHVO;
import br.gov.mec.aghu.controleinfeccao.vo.RelatorioBuscaAtivaPacientesCCIHVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import net.sf.jasperreports.engine.JRException;

public class RelatorioBuscaAtivaPacientesController extends ActionReport {

	private static final long serialVersionUID = 727526009060617267L;

	private static final Log LOG = LogFactory.getLog(RelatorioBuscaAtivaPacientesController.class);

	@Inject
	private SistemaImpressao sistemaImpressao;

	@EJB
	private IControleInfeccaoFacade controleInfeccaoFacade;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;	

	private List<RelatorioBuscaAtivaPacientesCCIHVO> colecao = new ArrayList<RelatorioBuscaAtivaPacientesCCIHVO>(0);
	private FiltroListaPacienteCCIHVO filtro;
	private String voltarPara;
	private static final String IMPRIMIR_BUSCA_ATIVA = "controleinfeccao-relatorioBuscaAtivaPacientesPdf";
	
	private StreamedContent media;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	@Override
	public Map<String, Object> recuperarParametros() {
		Map<String, Object> params = new HashMap<String, Object>();

		params.put("SUBREPORT_DIR", "br/gov/mec/aghu/controleinfeccao/report/");
		params.put("nomeHospital", this.cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal());
		
		return params;
	}

	public void directPrint() {

		try {
			colecao = this.controleInfeccaoFacade.gerarRelatorioBuscaAtivaPacientes(filtro);
			DocumentoJasper documento = gerarDocumento();
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}
	
	public StreamedContent getRenderPdf() throws IOException, JRException, DocumentException, ApplicationBusinessException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(Boolean.FALSE));			
	}
	
	public String print() throws JRException, IOException, DocumentException, ApplicationBusinessException {
		carregarColecao();
		String retorno = null;
		
		if(colecao.isEmpty()) {
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA");
		} else {
			DocumentoJasper documento = gerarDocumento(); 
			media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(Boolean.TRUE)));	
			retorno = IMPRIMIR_BUSCA_ATIVA;
		}
		return retorno;
	}
	
	public void carregarColecao() {
		colecao = this.controleInfeccaoFacade.gerarRelatorioBuscaAtivaPacientes(filtro);
	}
	
	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/controleinfeccao/report/relatorioBuscaAtivaPacientesDetalhamento.jasper";
	}

	@Override
	public Collection<RelatorioBuscaAtivaPacientesCCIHVO> recuperarColecao() throws ApplicationBusinessException {
		return colecao;
	}
	
	public String voltar() {
		return voltarPara;
	}

	public List<RelatorioBuscaAtivaPacientesCCIHVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<RelatorioBuscaAtivaPacientesCCIHVO	> colecao) {
		this.colecao = colecao;
	}

	public FiltroListaPacienteCCIHVO getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroListaPacienteCCIHVO filtro) {
		this.filtro = filtro;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}
	
	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}
}