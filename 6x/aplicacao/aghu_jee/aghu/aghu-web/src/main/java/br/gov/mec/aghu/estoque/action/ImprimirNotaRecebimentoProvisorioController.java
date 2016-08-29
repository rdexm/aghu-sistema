package br.gov.mec.aghu.estoque.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
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
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.DocumentException;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.vo.ItemRecebimentoProvisorioRelVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.AghInstituicoesHospitalares;
import net.sf.jasperreports.engine.JRException;


public class ImprimirNotaRecebimentoProvisorioController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	private static final Log LOG = LogFactory.getLog(ImprimirNotaRecebimentoProvisorioController.class);
	
	private static final long serialVersionUID = 3099300600546744615L;

	@Inject
	private SistemaImpressao sistemaImpressao;
	 
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	
	private List<Integer> listaNumerosRecebimentos;
	
	
	/*	Dados que serão impressos em PDF. */
	private List<ItemRecebimentoProvisorioRelVO> colecao = new ArrayList<ItemRecebimentoProvisorioRelVO>();
	
	// indica para onde o botao voltar deve redirecionar
	private String voltarParaUrl;
	
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	
	public String voltar() {
		return voltarParaUrl;
	}
	
	/**
	 * Método responsável por gerar a coleção de dados.
	 * @throws DocumentException 
	 */
	public String print(Integer numRecebimento) throws BaseException, JRException, SystemException, IOException, DocumentException {
		
		
		setListaNumerosRecebimentos(new ArrayList<Integer>());
				
		if (numRecebimento !=null) { 
			getListaNumerosRecebimentos().add(numRecebimento); 
		}
			
		colecao = this.estoqueFacade.pesquisarRelatorioItemRecProv(getListaNumerosRecebimentos());
		
		if (colecao.isEmpty()) {
			apresentarMsgNegocio(Severity.INFO,"MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA");
		
		DocumentoJasper documento = gerarDocumento();
		media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(false)));
		return null;
		}
	
		return "imprimirRelatorio";
		
	}
	
    public String printAll(Integer numRecebimento) throws BaseException, JRException, SystemException, IOException {
		
		if (getListaNumerosRecebimentos() == null ) {
			setListaNumerosRecebimentos(new ArrayList<Integer>());
		}		
		if (numRecebimento !=null) { getListaNumerosRecebimentos().add(numRecebimento); }
			
		colecao = this.estoqueFacade.pesquisarRelatorioItemRecProv(getListaNumerosRecebimentos());
		
		
		if (colecao.isEmpty()) {
			apresentarMsgNegocio(Severity.INFO,"MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA");
			return null;
		}
	
		return "imprimirRelatorio";
		
	}	
    
    public String printConfirmacaoRecebimento(Integer numRecebimento) throws BaseException, JRException, SystemException, IOException {
		
		
		setListaNumerosRecebimentos(new ArrayList<Integer>());
				
		if (numRecebimento !=null) { 
			getListaNumerosRecebimentos().add(numRecebimento); 
		}
			
		colecao = this.estoqueFacade.pesquisarRelatorioItemRecProv(getListaNumerosRecebimentos());
		
		if (colecao.isEmpty()) {
			apresentarMsgNegocio(Severity.INFO,"MENSAGEM_REL_CONFIR_REC_PROTOCOLO_RECEB");
			return null;
		}
	
		return "imprimirRelatorio";
//		muito estranho isso aqui	TODO
	}
	
	
	

	/**
	 * Impressão direta usando o CUPS.
	 */
	public void directPrint() throws SistemaImpressaoException, ApplicationBusinessException {

		colecao = this.estoqueFacade.pesquisarRelatorioItemRecProv(getListaNumerosRecebimentos());
			
		if (colecao.isEmpty()) {
			apresentarMsgNegocio(Severity.INFO,"MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA");
			return;
		}

		try {
			DocumentoJasper documento = gerarDocumento();

			this.sistemaImpressao.imprimir(documento.getJasperPrint(),super.getEnderecoIPv4HostRemoto());

			apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			apresentarMsgNegocio(Severity.ERROR,"ERRO_GERAR_RELATORIO");
		}
	}
	
	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();		
		AghInstituicoesHospitalares inst = aghuFacade.recuperarInstituicaoHospitalarLocal();
		String hospital = inst.getNome();		
		params.put("nomeHospital", hospital);
	

		return params;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return	"br/gov/mec/aghu/estoque/report/imprimirNotaRecebimentoProvisorio.jasper";
	}

	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 */
	
	@Override
	public Collection<ItemRecebimentoProvisorioRelVO> recuperarColecao() throws ApplicationBusinessException {
		return colecao;
	}
	
	public StreamedContent getRenderPdf() throws IOException, ApplicationBusinessException, JRException, 
																SystemException, DocumentException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}
	
	
	/*	Getters and Setters */
	public SistemaImpressao getSistemaImpressao() {
		return sistemaImpressao;
	}

	public void setSistemaImpressao(SistemaImpressao sistemaImpressao) {
		this.sistemaImpressao = sistemaImpressao;
	}

		
	public List<ItemRecebimentoProvisorioRelVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<ItemRecebimentoProvisorioRelVO> colecao) {
		this.colecao = colecao;
	}

	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}
	

	public List<Integer> getListaNumerosRecebimentos() {
		return listaNumerosRecebimentos;
	}

	public void setListaNumerosRecebimentos(List<Integer> listaNumerosRecebimentos) {
		this.listaNumerosRecebimentos = listaNumerosRecebimentos;
	}
}