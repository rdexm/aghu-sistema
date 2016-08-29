package br.gov.mec.aghu.paciente.action;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.vo.ReImpressaoEtiquetasVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;

public class ReImpressaoEtiquetasController extends ActionReport {
	
	private static final long serialVersionUID = 4110978662002171606L;
	private static final Log LOG = LogFactory.getLog(ReImpressaoEtiquetasController.class);
	private static final String PACIENTE_RE_IMPRESSAO_ETIQUETAS = "paciente-reImpressaoEtiquetas";
	private static final String PACIENTE_RE_IMPRESAO_ETIQUETAS_PDF = "paciente-reImpressaoEtiquetasPdf";
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@Inject
	private SistemaImpressao sistemaImpressao;
	
	private List<Integer> prontuarios = new ArrayList<Integer>();

	/**
	 * Dados que serão impressos em PDF.
	 */
	private List<ReImpressaoEtiquetasVO> colecao = new ArrayList<ReImpressaoEtiquetasVO>(0);

	private Date dataInicial = new Date();
	private Date dataFinal = new Date();

	private Integer prontuario;
	private Integer prontuario2;
	private Integer prontuario3;
	private Integer prontuario4;
	private Integer prontuario5;
	private Integer prontuario6;
	private Integer prontuario7;
	private Integer prontuario8;
	private Integer prontuario9;
	private Integer prontuario10;
	private Integer prontuario11;
	private Integer prontuario12;
	private Integer prontuario13;
	private Integer prontuario14;
	private Integer prontuario15;
	private Integer prontuario16;
	private Integer prontuario17;
	private Integer prontuario18;
	private Integer prontuario19;
	private Integer prontuario20;
	private Integer prontuario21;
	private Integer prontuario22;

	@PostConstruct
	public void inicio() {
		begin(conversation);
	}
	
	/**
	 * Busca os dados para geração do relatório.
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public String print() throws  JRException, SystemException, IOException {

		if (this.prontuario != null) {
			prontuarios.add(prontuario);
		}
		if (this.prontuario2 != null) {
			prontuarios.add(prontuario2);
		}
		if (this.prontuario3 != null) {
			prontuarios.add(prontuario3);
		}
		if (this.prontuario4 != null) {
			prontuarios.add(prontuario4);
		}
		if (this.prontuario5 != null) {
			prontuarios.add(prontuario5);
		}
		if (this.prontuario6 != null) {
			prontuarios.add(prontuario6);
		}
		if (this.prontuario7 != null) {
			prontuarios.add(prontuario7);
		}
		if (this.prontuario8 != null) {
			prontuarios.add(prontuario8);
		}
		if (this.prontuario9 != null) {
			prontuarios.add(prontuario9);
		}
		if (this.prontuario10 != null) {
			prontuarios.add(prontuario10);
		}
		if (this.prontuario11 != null) {
			prontuarios.add(prontuario11);
		}
		if (this.prontuario12 != null) {
			prontuarios.add(prontuario12);
		}
		if (this.prontuario13 != null) {
			prontuarios.add(prontuario13);
		}
		if (this.prontuario14 != null) {
			prontuarios.add(prontuario14);
		}
		if (this.prontuario15 != null) {
			prontuarios.add(prontuario15);
		}
		if (this.prontuario16 != null) {
			prontuarios.add(prontuario16);
		}
		if (this.prontuario17 != null) {
			prontuarios.add(prontuario17);
		}
		if (this.prontuario18 != null) {
			prontuarios.add(prontuario18);
		}
		if (this.prontuario19 != null) {
			prontuarios.add(prontuario19);
		}
		if (this.prontuario20 != null) {
			prontuarios.add(prontuario20);
		}
		if (this.prontuario21 != null) {
			prontuarios.add(prontuario21);
		}
		if (this.prontuario22 != null) {
			prontuarios.add(prontuario22);
		}

		try {
			this.pacienteFacade.validaDatas(dataInicial, dataFinal);
		} catch (ApplicationBusinessException e) {
			e.getMessage();
			this.apresentarMsgNegocio(Severity.ERROR,
					e.getLocalizedMessage());
			return null;
		}

		this.colecao = pacienteFacade.pesquisa(prontuarios, dataInicial,
				dataFinal);
		
		if (this.colecao == null || this.colecao.isEmpty()) {
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_RELATORIO_REIMPRESSAO_ETIQUETAS_IDENTIFICACAO_VAZIO");
			return null;
		}
		
		return PACIENTE_RE_IMPRESAO_ETIQUETAS_PDF;
	}
	
	public String voltar(){
		return PACIENTE_RE_IMPRESSAO_ETIQUETAS;
	}
	
	/**
	 * Imprime relatório.
	 */
	public void impressaoDireta() throws  JRException,
			SystemException, IOException, ParseException{
		this.print();

		try {
			DocumentoJasper documento = gerarDocumento();

			this.sistemaImpressao.imprimir(documento.getJasperPrint(),
					super.getEnderecoIPv4HostRemoto());

			this.apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			this.apresentarMsgNegocio(Severity.ERROR,
					"ERRO_GERAR_RELATORIO");
		}
		
		
	}

	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 */
	public StreamedContent getRenderPdf() throws IOException, JRException, SystemException, DocumentException, ApplicationBusinessException {
		DocumentoJasper documento = gerarDocumento(false);
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}

	@Override
	public Collection<ReImpressaoEtiquetasVO> recuperarColecao() {
		return this.colecao;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "/br/gov/mec/aghu/paciente/report/etiquetasNome.jasper";
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Integer getProntuario2() {
		return prontuario2;
	}

	public void setProntuario2(Integer prontuario2) {
		this.prontuario2 = prontuario2;
	}

	public Integer getProntuario3() {
		return prontuario3;
	}

	public void setProntuario3(Integer prontuario3) {
		this.prontuario3 = prontuario3;
	}

	public Integer getProntuario4() {
		return prontuario4;
	}

	public void setProntuario4(Integer prontuario4) {
		this.prontuario4 = prontuario4;
	}

	public Integer getProntuario5() {
		return prontuario5;
	}

	public void setProntuario5(Integer prontuario5) {
		this.prontuario5 = prontuario5;
	}

	public Integer getProntuario6() {
		return prontuario6;
	}

	public void setProntuario6(Integer prontuario6) {
		this.prontuario6 = prontuario6;
	}

	public Integer getProntuario7() {
		return prontuario7;
	}

	public void setProntuario7(Integer prontuario7) {
		this.prontuario7 = prontuario7;
	}

	public Integer getProntuario8() {
		return prontuario8;
	}

	public void setProntuario8(Integer prontuario8) {
		this.prontuario8 = prontuario8;
	}

	public Integer getProntuario9() {
		return prontuario9;
	}

	public void setProntuario9(Integer prontuario9) {
		this.prontuario9 = prontuario9;
	}

	public Integer getProntuario10() {
		return prontuario10;
	}

	public void setProntuario10(Integer prontuario10) {
		this.prontuario10 = prontuario10;
	}

	public Integer getProntuario11() {
		return prontuario11;
	}

	public void setProntuario11(Integer prontuario11) {
		this.prontuario11 = prontuario11;
	}

	public Integer getProntuario12() {
		return prontuario12;
	}

	public void setProntuario12(Integer prontuario12) {
		this.prontuario12 = prontuario12;
	}

	public Integer getProntuario13() {
		return prontuario13;
	}

	public void setProntuario13(Integer prontuario13) {
		this.prontuario13 = prontuario13;
	}

	public Integer getProntuario14() {
		return prontuario14;
	}

	public void setProntuario14(Integer prontuario14) {
		this.prontuario14 = prontuario14;
	}

	public Integer getProntuario15() {
		return prontuario15;
	}

	public void setProntuario15(Integer prontuario15) {
		this.prontuario15 = prontuario15;
	}

	public Integer getProntuario16() {
		return prontuario16;
	}

	public void setProntuario16(Integer prontuario16) {
		this.prontuario16 = prontuario16;
	}

	public Integer getProntuario17() {
		return prontuario17;
	}

	public void setProntuario17(Integer prontuario17) {
		this.prontuario17 = prontuario17;
	}

	public Integer getProntuario18() {
		return prontuario18;
	}

	public void setProntuario18(Integer prontuario18) {
		this.prontuario18 = prontuario18;
	}

	public Integer getProntuario19() {
		return prontuario19;
	}

	public void setProntuario19(Integer prontuario19) {
		this.prontuario19 = prontuario19;
	}

	public Integer getProntuario20() {
		return prontuario20;
	}

	public void setProntuario20(Integer prontuario20) {
		this.prontuario20 = prontuario20;
	}

	public Integer getProntuario21() {
		return prontuario21;
	}

	public void setProntuario21(Integer prontuario21) {
		this.prontuario21 = prontuario21;
	}

	public Integer getProntuario22() {
		return prontuario22;
	}

	public void setProntuario22(Integer prontuario22) {
		this.prontuario22 = prontuario22;
	}

	public List<Integer> getProntuarios() {
		return prontuarios;
	}

	public void setProntuarios(List<Integer> prontuarios) {
		this.prontuarios = prontuarios;
	}

	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

	public List<ReImpressaoEtiquetasVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<ReImpressaoEtiquetasVO> colecao) {
		this.colecao = colecao;
	}
}
