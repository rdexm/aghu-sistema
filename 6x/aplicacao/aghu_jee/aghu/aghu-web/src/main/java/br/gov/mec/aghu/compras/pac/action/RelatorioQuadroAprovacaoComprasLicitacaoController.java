package br.gov.mec.aghu.compras.pac.action;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;

import org.apache.commons.lang3.text.WordUtils;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.compras.pac.vo.ItemLicitacaoQuadroAprovacaoVO;
import br.gov.mec.aghu.dominio.DominioMes;
import br.gov.mec.aghu.model.AghInstituicoesHospitalares;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;

public class RelatorioQuadroAprovacaoComprasLicitacaoController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	private static final long serialVersionUID = -71687538831270143L;
	
	private static final String ARQUIVO_RELATORIO = "br/gov/mec/aghu/compras/report/quadroAprovacaoComprasLicitacao.jasper";
	
	private static final String PAGE_RELATORIO_QUADRO_APROVACAO_COMPRAS_LICITACAO = "relatorioQuadroAprovacaoComprasLicitacao";

	
	/** URL de Origem */
	private String origem;
	
	
	@EJB
	protected IPacFacade pacFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	private Set<Integer> pacs;
	private Boolean assinaturas;
	private Integer numPac;
	private String voltarParaUrl;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public String printPac(Integer numPac, Boolean assinaturas) throws BaseException, JRException, SystemException, IOException{
		this.numPac = numPac;
		this.assinaturas = assinaturas;
		pacs =  new LinkedHashSet<Integer>();
		pacs.add(numPac);
		this.recuperarColecao();		 
		return PAGE_RELATORIO_QUADRO_APROVACAO_COMPRAS_LICITACAO;
	}
	
		
	@Override
	public Collection<ItemLicitacaoQuadroAprovacaoVO> recuperarColecao() throws ApplicationBusinessException {
		return pacFacade.pesquisarPacsQuadroAprovacao(pacs, assinaturas);
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return ARQUIVO_RELATORIO;
	}
	
	@Override
	public Map<String, Object> recuperarParametros() {
		Calendar now = Calendar.getInstance();
		Map<String, Object> params = new HashMap<String, Object>();
	
		
		AghInstituicoesHospitalares inst = aghuFacade.recuperarInstituicaoHospitalarLocal();
		
		params.put("EMPRESA_CONEXAO_ATIVA", inst.getNome());
		
		// Cidade
		String cidade = inst.getCddCodigo() != null ? inst.getCddCodigo().getNome() : inst.getCidade();
		
		if (cidade != null) {
			params.put("CIDADE_CONEXAO_ATIVA", WordUtils
					.capitalizeFully(cidade).replaceAll(" Da ", " da ")
					.replaceAll(" De ", " de ").replaceAll(" Do ", " do "));
		}
		
		params.put(JRParameter.REPORT_RESOURCE_BUNDLE, super.getBundle());
		params.put("DATA_EMISSAO_RELATORIO", now.getTime());
		params.put("MES_EMISSAO_RELATORIO", DominioMes.obterDominioMes(now.get(Calendar.MONTH)).getDescricao());
		params.put("EXIBIR_ASSINATURAS", assinaturas);
		
		return params;
	}
	
	/**
	 * Imprime relatório.
	 */
	public void imprimir(OutputStream out, Object data) throws BaseException, JRException, SystemException, IOException, DocumentException {
		DocumentoJasper documento = gerarDocumento();
		this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}	

	public String voltar(){
		return origem;
	}
	
	
	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public Set<Integer> getPacs() {
		return pacs;
	}

	public void setPacs(Set<Integer> pacs) {
		this.pacs = pacs;
	}

	public Boolean getAssinaturas() {
		return assinaturas;
	}

	public void setAssinaturas(Boolean assinaturas) {
		this.assinaturas = assinaturas;
	}

	public Integer getNumPac() {
		return numPac;
	}

	public void setNumPac(Integer numPac) {
		this.numPac = numPac;
	}

	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}
}