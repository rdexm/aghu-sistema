package br.gov.mec.aghu.prescricaomedica.anamneseevolucao.action;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.DocumentException;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.model.MpmEvolucoes;
import br.gov.mec.aghu.prescricaoenfermagem.vo.RelatorioEvolucoesPacienteVO;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import net.sf.jasperreports.engine.JRException;


public class RelatorioEvolucoesPacienteController extends ActionReport {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(RelatorioEvolucoesPacienteController.class);

	private static final long serialVersionUID = 1736471114873L;

	@Inject
	private SistemaImpressao sistemaImpressao;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	private String voltarPara;
	private Long seqAnamnese;

	private List<RelatorioEvolucoesPacienteVO> colecao;
	private List<MpmEvolucoes> evolucoesVisualizacao;

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

	public StreamedContent renderPdf() throws IOException, BaseException, JRException, SystemException, DocumentException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}
	public void imprimirEvolucao(Long seqEvolucao) {
		this.colecao = this.prescricaoMedicaFacade.gerarRelatorioEvolucaoPaciente(seqEvolucao);
		directPrint();
	}

	public void visualizarEvolucoes(List<MpmEvolucoes> listaEvolucoesSelecionadas) {
		for(MpmEvolucoes evolucao : listaEvolucoesSelecionadas){
			this.colecao = this.prescricaoMedicaFacade.gerarRelatorioEvolucaoPaciente(evolucao.getSeq());
			this.colecao.addAll(colecao);
		}
	}
	

	public void imprimirEvolucoesEmVisualizacao() {
		for(MpmEvolucoes evolucao : evolucoesVisualizacao){
			this.colecao = this.prescricaoMedicaFacade.gerarRelatorioEvolucaoPaciente(evolucao.getSeq());
			directPrint();
		}
		this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
	}

	@Override
	public void directPrint() {
		try {

			DocumentoJasper documento = gerarDocumento();
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error("Exceção capturada: ", e);
			apresentarMsgNegocio(Severity.ERROR, e.getLocalizedMessage());
		}
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "/br/gov/mec/aghu/prescricaomedica/report/relatorioEvolucoesPaciente.jasper";
	}

	@Override
	public Collection<?> recuperarColecao() throws ApplicationBusinessException {
		return this.colecao;
	}

	public String voltar() {
		return "paciente-visualizarEvolucoesPOL";
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public Long getSeqAnamnese() {
		return seqAnamnese;
	}

	public void setSeqAnamnese(Long seqAnamnese) {
		this.seqAnamnese = seqAnamnese;
	}

	public List<MpmEvolucoes> getEvolucoesVisualizacao() {
		return evolucoesVisualizacao;
	}

	public void setEvolucoesVisualizacao(List<MpmEvolucoes> evolucoesVisualizacao) {
		this.evolucoesVisualizacao = evolucoesVisualizacao;
	}

}
