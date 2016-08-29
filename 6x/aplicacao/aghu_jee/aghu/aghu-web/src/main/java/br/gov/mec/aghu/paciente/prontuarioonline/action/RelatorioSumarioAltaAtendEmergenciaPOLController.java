package br.gov.mec.aghu.paciente.prontuarioonline.action;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.paciente.prontuarioonline.business.IProntuarioOnlineFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.RelatorioSumarioAltaAtendEmergenciaPOLVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;

public class RelatorioSumarioAltaAtendEmergenciaPOLController extends ActionReport {

	private static final Log LOG = LogFactory.getLog(RelatorioSumarioAltaAtendEmergenciaPOLController.class); 

	private static final long serialVersionUID = 4478142671265870114L;

	@EJB
	private IProntuarioOnlineFacade prontuarioOnlineFacade;

	@EJB
	private IParametroFacade parametroFacade;
	
	private Integer atdSeq;
	private List<RelatorioSumarioAltaAtendEmergenciaPOLVO> colecao;
	
	@Override
	public Collection<RelatorioSumarioAltaAtendEmergenciaPOLVO> recuperarColecao() {
		try {
			colecao = prontuarioOnlineFacade.recuperarRelatorioSumarioAltaAtendEmergenciaPOLVO(atdSeq);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return colecao;
	}

	
	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/paciente/prontuarioonline/report/mamr_sumarioEmg.jasper";
	}

	@Override
	public Map<String, Object> recuperarParametros() {

		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("logo", recuperarCaminhoLogo());
			params.put("hospitalEndCompletoLinha1", parametroFacade.obterAghParametro(AghuParametrosEnum.P_HOSPITAL_END_COMPLETO_LINHA1).getVlrTexto());
			params.put("hospitalEndCompletoLinha2", parametroFacade.obterAghParametro(AghuParametrosEnum.P_HOSPITAL_END_COMPLETO_LINHA2).getVlrTexto());
			params.put("codigoCidadePadrao", parametroFacade.obterAghParametro(AghuParametrosEnum.P_CODIGO_CIDADE_PADRAO).getVlrNumerico());
			params.put("autorizacaoHospitalPagador", parametroFacade.obterAghParametro(AghuParametrosEnum.P_AUTORIZACAO_HOSPITAL_PAGADOR).getVlrTexto());
			params.put("SUBREPORT_DIR", "/br/gov/mec/aghu/paciente/prontuarioonline/report/");
			return params;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} catch (BaseException e) {
			LOG.error("Erro ao tentar recuparar logotipo para o relatório",e);
		}
		return null;
	}

	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 * 
	 * @throws DocumentException
	 * @throws ApplicationBusinessException 
	 */
	public StreamedContent getRenderPdf() throws IOException,JRException, SystemException, DocumentException, ApplicationBusinessException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(Boolean.TRUE));
	}
	
	// Getters e Setters
	public IProntuarioOnlineFacade getProntuarioOnlineFacade() {
		return prontuarioOnlineFacade;
	}

	public void setProntuarioOnlineFacade(
			IProntuarioOnlineFacade prontuarioOnlineFacade) {
		this.prontuarioOnlineFacade = prontuarioOnlineFacade;
	}

	public IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	public void setParametroFacade(IParametroFacade parametroFacade) {
		this.parametroFacade = parametroFacade;
	}

	public List<RelatorioSumarioAltaAtendEmergenciaPOLVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<RelatorioSumarioAltaAtendEmergenciaPOLVO> colecao) {
		this.colecao = colecao;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}
}
