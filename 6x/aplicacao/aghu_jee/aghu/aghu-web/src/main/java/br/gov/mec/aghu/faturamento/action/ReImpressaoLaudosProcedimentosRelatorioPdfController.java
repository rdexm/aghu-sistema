package br.gov.mec.aghu.faturamento.action;

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

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.RelatorioLaudosProcSusVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;

public class ReImpressaoLaudosProcedimentosRelatorioPdfController extends ActionReport {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2468794975650191683L;

	private static final Log LOG = LogFactory.getLog(ReImpressaoLaudosProcedimentosRelatorioPdfController.class);

	private Log getLog() {
		return LOG;
	}

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	private Integer seqAtendimento;

	private Integer apaSeq;

	private Short seqp;

	private Boolean imprimir = true;
	
	private boolean isDirectPrint = false;

	@PostConstruct
	protected void init() {
		begin(conversation);
	}
	
	public String inicio() {
		if (isDirectPrint) {
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

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/prescricaomedica/report/relatorioLaudoProcSus.jasper";
	}

	@Override
	public Collection<RelatorioLaudosProcSusVO> recuperarColecao() {
		List<RelatorioLaudosProcSusVO> colecao = new ArrayList<RelatorioLaudosProcSusVO>();
		List<RelatorioLaudosProcSusVO> retorno = new ArrayList<RelatorioLaudosProcSusVO>();
		try {
			RapServidores servidorValidaMpmAltaSumario = prescricaoMedicaFacade.obterServidorCriacaoAltaSumario(this.seqAtendimento);

			retorno.addAll(prescricaoMedicaFacade.pesquisaLaudoProcedimentoSus(this.seqAtendimento, this.apaSeq, this.seqp, servidorValidaMpmAltaSumario));
			colecao = listarSemRedundancia(retorno);
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return colecao;
	}
	public List<RelatorioLaudosProcSusVO> listarSemRedundancia(List<RelatorioLaudosProcSusVO> retorno){
		List<RelatorioLaudosProcSusVO> colecao = new ArrayList<RelatorioLaudosProcSusVO>();
		for (RelatorioLaudosProcSusVO relatorioLaudosProcSusVO : retorno) {
			if(!colecao.contains(relatorioLaudosProcSusVO)){
				colecao.add(relatorioLaudosProcSusVO);
			}
		}
		return colecao;
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
	
	 /**
	* Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	*/
	@Override
	public StreamedContent getRenderPdf() throws IOException,
		JRException, DocumentException, ApplicationBusinessException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}
	
	public String voltar(){
		return "reimpressaoLaudosProcedimentos";
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

	public Boolean getImprimir() {
		return imprimir;
	}

	public void setImprimir(Boolean imprimir) {
		this.imprimir = imprimir;
	}

	public boolean isDirectPrint() {
		return isDirectPrint;
	}

	public void setDirectPrint(boolean isDirectPrint) {
		this.isDirectPrint = isDirectPrint;
	}

}
