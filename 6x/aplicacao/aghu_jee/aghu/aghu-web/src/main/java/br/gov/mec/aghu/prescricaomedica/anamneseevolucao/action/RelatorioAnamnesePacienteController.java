package br.gov.mec.aghu.prescricaomedica.anamneseevolucao.action;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.RelatorioAnamnesePacienteVO;

import com.itextpdf.text.DocumentException;


public class RelatorioAnamnesePacienteController extends ActionReport {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(RelatorioAnamnesePacienteController.class);

	private static final long serialVersionUID = 1736471114873L;

	@Inject
	private SistemaImpressao sistemaImpressao;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	private Long seqAnamnese;

	private String voltarPara;

	private List<RelatorioAnamnesePacienteVO> gerarDados() throws ApplicationBusinessException {
		return this.prescricaoMedicaFacade.gerarRelatorioAnamnesePaciente(this.seqAnamnese);
	}

	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();

		try {
			params.put("caminhoLogo", recuperarCaminhoLogo());
		} catch (BaseException e) {
			LOG.error("Erro ao tentar recuparar logotipo para o relatório", e);
		}

		return params;
	}

	public void renderPdf(OutputStream out, Object data) throws IOException, BaseException, JRException, SystemException, DocumentException {
		gerarDados();
		DocumentoJasper documento = gerarDocumento();
		out.write(documento.getPdfByteArray(Boolean.TRUE));
	}

	@Override
	public void directPrint() {
		try {

			DocumentoJasper documento = gerarDocumento();
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(e.getMessage());
			apresentarMsgNegocio(Severity.ERROR, e.getLocalizedMessage());
		}
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "/br/gov/mec/aghu/prescricaomedica/report/relatorioAnamnesePaciente.jasper";
	}

	@Override
	public Collection<?> recuperarColecao() throws ApplicationBusinessException {
		return gerarDados();
	}

	public String voltar() {
		return this.voltarPara;
	}

	public Long getSeqAnamnese() {
		return seqAnamnese;
	}

	public void setSeqAnamnese(Long seqAnamnese) {
		this.seqAnamnese = seqAnamnese;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

}
