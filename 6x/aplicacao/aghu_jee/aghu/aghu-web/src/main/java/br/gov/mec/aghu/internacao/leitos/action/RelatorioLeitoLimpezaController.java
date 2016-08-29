package br.gov.mec.aghu.internacao.leitos.action;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.leitos.business.ILeitosInternacaoFacade;
import br.gov.mec.aghu.internacao.leitos.vo.LiberaLeitoLimpezaVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;

/**
 * @author lalegre
 */
public class RelatorioLeitoLimpezaController extends ActionReport {

	private static final long serialVersionUID = 2594998638655437890L;

	private static final String RELATORIO_LEITO_LIMPEZA_PDF = "relatorioLeitosAlimparPdf";
	private static final String RELATORIO_LEITO_LIMPEZA = "liberaLeitoLimpeza";
	private static final String RELATORIO_LEITO_LIMPEZA_CANCELAR = "retornoAGH";

	/**
	 * Responsável pela pesquisa de Leitos Limpeza
	 */
	@EJB
	private ILeitosInternacaoFacade leitosInternacaoFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	/**
	 * Dados que serão impressos em PDF.
	 */
	private List<LiberaLeitoLimpezaVO> colecao = new ArrayList<LiberaLeitoLimpezaVO>(0);

	@PostConstruct
	public void inicio() {
		begin(conversation);
	}

	/**
	 * Método responsável por gerar a coleção de dados.
	 * 
	 * @return
	 * @throws MECBaseException
	 * @throws JRException
	 * @throws SystemException
	 * @throws IOException
	 */
	public String print() throws IOException, ApplicationBusinessException, JRException, SystemException, DocumentException {
		return RELATORIO_LEITO_LIMPEZA_PDF;
	}

	@Override
	public Collection<LiberaLeitoLimpezaVO> recuperarColecao() throws ApplicationBusinessException {
		return leitosInternacaoFacade.pesquisarLeitosLimpeza();
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "/br/gov/mec/aghu/internacao/leitos/report/relatorioLeitosAlimpar.jasper";
	}

	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 * 
	 * @param out
	 * @param data
	 * @throws IOException
	 * @throws SystemException
	 * @throws JRException
	 * @throws MECBaseException
	 * @throws DocumentException
	 */
	public StreamedContent getRenderPdf() throws IOException, ApplicationBusinessException, JRException, SystemException, DocumentException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}

	@Override
	public Map<String, Object> recuperarParametros() {
		Map<String, Object> params = new HashMap<String, Object>();
		Date dataAtual = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		params.put("dataAtual", sdf.format(dataAtual));
		params.put("nomeRelatorio", "AINR_LEITOS_ALIMPAR");
		params.put("hospital", cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal());
		params.put("titulo", "Leitos em Limpeza");

		return params;
	}

	public String voltar() {
		return RELATORIO_LEITO_LIMPEZA;
	}

	public String cancelar() {
		return RELATORIO_LEITO_LIMPEZA_CANCELAR;
	}

	public List<LiberaLeitoLimpezaVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<LiberaLeitoLimpezaVO> colecao) {
		this.colecao = colecao;
	}

}
