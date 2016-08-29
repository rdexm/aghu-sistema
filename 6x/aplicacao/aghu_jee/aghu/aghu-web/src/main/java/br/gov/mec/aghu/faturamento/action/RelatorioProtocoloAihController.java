package br.gov.mec.aghu.faturamento.action;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJB;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.vo.ProtocoloAihVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;

public class RelatorioProtocoloAihController extends ActionReport {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3886943671957618587L;

	private static final Log LOG = LogFactory.getLog(RelatorioProtocoloAihController.class);
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;

	@EJB
	private IParametroFacade parametroFacade;

	private Date data;
	
	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/faturamento/report/relatorioProtocoloAih.jasper";
	}

	public void print() throws BaseException, JRException, SystemException, IOException {
		recuperarColecao();
	}

	public String visualizarRelatorio() {
		return "relatorioProtocoloAihPdf";
	}
	
	@Override
	public Collection<ProtocoloAihVO> recuperarColecao() throws ApplicationBusinessException {
		return faturamentoFacade.listaProtocolosAih(data);
	}
	
	public void directPrint() {
		try {
			DocumentoJasper documento = gerarDocumento();

			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());

			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}
	
	@Override
	public Map<String, Object> recuperarParametros() {
		Map<String, Object> params = new HashMap<String, Object>();
		AghParametros nomeHospital = null;
		nomeHospital = razaoSocia();
		if (nomeHospital != null) {
			params.put("nomeHospital", nomeHospital.getVlrTexto());
		}
		params.put("dataAtual", new Date());
		params.put("nomeRelatorio", "FATR_PROT_AIH");
		try {
			params.put("caminhoLogo", recuperarCaminhoLogo2());
		} catch (BaseException e) {
			LOG.error("Erro ao tentar recuparar logotipo para o relatório",e);
		}			
		return params;
	}
	
	public AghParametros razaoSocia(){
		AghParametros nomeHospital = null;
		try {
			nomeHospital = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL);	
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return nomeHospital;
	}
	
	public DocumentoJasper buscarDocumentoGerado() throws ApplicationBusinessException{
		return this.gerarDocumento();
	}

	public StreamedContent getRenderPdf() throws IOException, JRException, DocumentException, ApplicationBusinessException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(Boolean.TRUE));
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}
}
