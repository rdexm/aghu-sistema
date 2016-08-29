package br.gov.mec.aghu.compras.pac.action;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.compras.pac.vo.RelatorioQuadroPropostasLicitacaoVO;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;


public class RelatorioQuadroPropostasProvisorioController extends ActionReport {
	
	private static final String PAGE_RELATORIO_QUADRO_PROPOSTAS_PROVISORIO = "compras-relatorioQuadroPropostasProvisorio";

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	private static final long serialVersionUID = -31103426998039476L;

	@EJB
	protected IPacFacade pacFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	private Set<Integer> pacs;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		
	}
	
	/* Dados que serão impressos em PDF. */
	private List<RelatorioQuadroPropostasLicitacaoVO> colecao;

	private String voltarParaUrl;
	private Integer numPac;

	private Short numeroItemInicial;
	private Short numeroItemFinal;
	private String listaItens;
	
	public String printPac(Integer numPac) throws BaseException, JRException, SystemException, IOException{
		this.numPac = numPac;
		this.print();
		return PAGE_RELATORIO_QUADRO_PROPOSTAS_PROVISORIO;
	}
	
	public void print() throws BaseException, JRException, SystemException, IOException {
		
		Set<Integer> listaPacs; 
		
		if (numPac != null){
			listaPacs = new LinkedHashSet<Integer>();
			listaPacs.add(numPac);
		} else {
			listaPacs = pacs;
		}

		colecao = pacFacade.pesquisarQuadroProvisorioItensPropostas(listaPacs, numeroItemInicial, numeroItemFinal, listaItens);
	}

	public StreamedContent getRenderPdf() throws IOException, JRException, SystemException, DocumentException, ApplicationBusinessException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}

	@Override
	public String recuperarArquivoRelatorio() {
	
		return "br/gov/mec/aghu/compras/report/QuadroProvisorioPropostasPAC.jasper";
	}
	
	@Override
	public Collection<RelatorioQuadroPropostasLicitacaoVO> recuperarColecao() throws ApplicationBusinessException {		
		return this.colecao;		
	}

	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		Date dataAtual = new Date();
		SimpleDateFormat sdf_1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		params.put("dataAtual", sdf_1.format(dataAtual));
		params.put("hospitalLocal", hospital);
		params.put("tituloRelatorio", "Quadro de Propostas Provisório");
		params.put("SUBREPORT_DIR", "br/gov/mec/aghu/compras/report/");

		return params;
	}

	public String voltar() {
		return voltarParaUrl;
	}
	
	public List<RelatorioQuadroPropostasLicitacaoVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<RelatorioQuadroPropostasLicitacaoVO> colecao) {
		this.colecao = colecao;
	}

	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}

	public Integer getNumPac() {
		return numPac;
	}

	public void setNumPac(Integer numPac) {
		this.numPac = numPac;
	}

	public Short getNumeroItemInicial() {
		return numeroItemInicial;
	}

	public void setNumeroItemInicial(Short numeroItemInicial) {
		this.numeroItemInicial = numeroItemInicial;
	}

	public Short getNumeroItemFinal() {
		return numeroItemFinal;
	}

	public void setNumeroItemFinal(Short numeroItemFinal) {
		this.numeroItemFinal = numeroItemFinal;
	}

	public String getListaItens() {
		return listaItens;
	}

	public void setListaItens(String listaItens) {
		this.listaItens = listaItens;
	}

	public Set<Integer> getPacs() {
		return pacs;
	}

	public void setPacs(Set<Integer> pacs) {
		this.pacs = pacs;
	}

}
