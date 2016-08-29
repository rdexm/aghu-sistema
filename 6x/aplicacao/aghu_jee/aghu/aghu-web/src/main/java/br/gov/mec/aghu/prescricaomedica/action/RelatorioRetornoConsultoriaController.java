package br.gov.mec.aghu.prescricaomedica.action;

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

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.RetornoConsultoriaVO;

public class RelatorioRetornoConsultoriaController extends ActionReport {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2951815345259295261L;

	private static final Log LOG = LogFactory.getLog(RelatorioRetornoConsultoriaController.class);

	@Inject
	private SistemaImpressao sistemaImpressao;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	private List<RetornoConsultoriaVO> colecao = new ArrayList<RetornoConsultoriaVO>(
			0);

	private Integer scnSeq;

	private Integer atdSeq;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	@Override
	public Map<String, Object> recuperarParametros() {
		Map<String, Object> params = new HashMap<String, Object>();

		params.put("SUBREPORT_DIR", "br/gov/mec/aghu/prescricaomedica/report/");
		try {
			params.put("LOGO_HOSPITAL", recuperarCaminhoLogo2());
		} catch (BaseException e) {
			LOG.error("Erro ao tentar recuparar logotipo para o relatório", e);
		}

		return params;
	}

	public void directPrint() {

		try {
			colecao = this.prescricaoMedicaFacade.pesquisarRetornoConsultorias(atdSeq, scnSeq);
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
	
	public void downloadPdf() {
		try {
			colecao = this.prescricaoMedicaFacade.pesquisarRetornoConsultorias(atdSeq, scnSeq);
			
			DocumentoJasper documento = gerarDocumento();
			super.download(documento.getPdfByteArray(false), "DANFE.pdf", "text/pdf");
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_GERACAO_DANFE");
		} catch(ApplicationBusinessException e) { 
			apresentarExcecaoNegocio(e);
		} catch(BaseException e) { 
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_EMISSAO_DANFE_FORMATO");
		} catch(Exception e) {
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_EMISSAO_DANFE_FORMATO");
		}
	}
	
	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/prescricaomedica/report/relatorioRetornoConsultoriaPrescricaoMedica.jasper";
	}

	@Override
	public Collection recuperarColecao() throws ApplicationBusinessException {
		return this.colecao;
	}

	public List<RetornoConsultoriaVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<RetornoConsultoriaVO> colecao) {
		this.colecao = colecao;
	}

	public Integer getScnSeq() {
		return scnSeq;
	}

	public void setScnSeq(Integer scnSeq) {
		this.scnSeq = scnSeq;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}
}