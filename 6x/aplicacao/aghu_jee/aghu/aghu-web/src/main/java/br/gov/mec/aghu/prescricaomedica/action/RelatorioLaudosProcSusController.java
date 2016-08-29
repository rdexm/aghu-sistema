package br.gov.mec.aghu.prescricaomedica.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.RelatorioLaudosProcSusVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;


public class RelatorioLaudosProcSusController extends ActionReport {

	private static final long serialVersionUID = 1207219417234165626L;

	private Integer seqAtendimento;
	
	private Integer apaSeq;
	
	private Short seqp;
	
	private Boolean imprimir = true;
	
	private String voltarPara;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	List<RelatorioLaudosProcSusVO> listaLaudoProcedimento;

	/**
	 * Dados que serão impressos em PDF.
	 */
	private List<RelatorioLaudosProcSusVO> colecao = new ArrayList<RelatorioLaudosProcSusVO>();

	public String print() throws BaseException, JRException, SystemException, IOException {
		
		try {
			this.listaLaudoProcedimento = prescricaoMedicaFacade.pesquisaLaudoProcedimentoSus(this.seqAtendimento,this.apaSeq, this.seqp);
		} catch (ApplicationBusinessException e) {
			this.imprimir = false;
			apresentarExcecaoNegocio(e);
		}
		this.colecao.clear();
		return "RelatorioLaudosProcSusPdf";
	}
	
	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/prescricaomedica/report/relatorioLaudoProcSus.jasper";
	}
	
	@Override
	public Collection<RelatorioLaudosProcSusVO> recuperarColecao() {
		this.colecao.clear();
		this.colecao.addAll(listaLaudoProcedimento);
		return this.colecao;
	}

	@PostConstruct
	public void init() {
		begin(conversation);
	}
	
	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 */
	public StreamedContent getRenderPdf() throws IOException,
			JRException, SystemException, DocumentException, ApplicationBusinessException {
		
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}

	@Override
	public Map<String, Object> recuperarParametros() {
		Map<String, Object> params = new HashMap<String, Object>();
		ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();

		params.put("nomeRelatorio", "MPMR_LAUDO_SUS_PROC");
		params.put("logoSusPath", servletContext.getRealPath("/resources/img/logoSus.jpg"));
		params.put("subRelatorio", "br/gov/mec/aghu/prescricaomedica/report/");

		return params;
	}
	
	public String voltar(){
		return voltarPara;
	}

	public Integer getSeqAtendimento() {
		return seqAtendimento;
	}

	public void setSeqAtendimento(Integer seqAtendimento) {
		this.seqAtendimento = seqAtendimento;
	}

	public Integer getApaSeq() {
		return apaSeq;
	}

	public void setApaSeq(Integer apaSeq) {
		this.apaSeq = apaSeq;
	}

	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	public List<RelatorioLaudosProcSusVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<RelatorioLaudosProcSusVO> colecao) {
		this.colecao = colecao;
	}

	public Boolean getImprimir() {
		return imprimir;
	}

	public void setImprimir(Boolean imprimir) {
		this.imprimir = imprimir;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}
}