package br.gov.mec.aghu.farmacia.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.DocumentException;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.vo.QuantidadePrescricoesDispensacaoVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import net.sf.jasperreports.engine.JRException;

public class RelatorioQuantidadePrescricoesDispensacaoController extends ActionReport{

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	private static final long serialVersionUID = -7361089821760716242L;
	
	private static final String PAGE_QUANTIDADE_PRESCRICOES_DISPENSADAS= "quantidadePrescricoesDispensadas";
	private static final String PAGE_QUANTIDADE_PRESCRICOES_DISPENSADAS_PDF= "quantidadePrescricoesDispensadasPdf";
	
	@Inject
	private SistemaImpressao sistemaImpressao;

	@EJB
	private IFarmaciaFacade farmaciaFacade;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	private Date dataEmissaoInicio;
	private Date dataEmissaoFim;
	private List<QuantidadePrescricoesDispensacaoVO> colecao = new ArrayList<QuantidadePrescricoesDispensacaoVO>(0);
	
	@PostConstruct
	public void init() {
		begin(conversation);
	}
	
	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/farmacia/report/quantidadePrescricoesDispensadas.jasper";
	}
	
	public void limparPesquisa() {
		this.dataEmissaoInicio = null;
		this.dataEmissaoFim = null;
	}
	
	public void directPrint() throws SistemaImpressaoException, ApplicationBusinessException{
		try{
			colecao = this.farmaciaFacade.pesquisarRelatorioQuantidadePrescricoesDispensacao(getDataEmissaoInicio(), getDataEmissaoFim());
			DocumentoJasper documento = gerarDocumento();
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}
	
	public String print()throws JRException, IOException, DocumentException{
		try{
			colecao = this.farmaciaFacade.pesquisarRelatorioQuantidadePrescricoesDispensacao(getDataEmissaoInicio(), getDataEmissaoFim());
		
		DocumentoJasper documento = gerarDocumento();

		media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(false)));
		return PAGE_QUANTIDADE_PRESCRICOES_DISPENSADAS_PDF;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	public String voltar(){
		return PAGE_QUANTIDADE_PRESCRICOES_DISPENSADAS;
	}
	
	@Override
	public List<QuantidadePrescricoesDispensacaoVO> recuperarColecao() throws ApplicationBusinessException {
		return colecao;
	}
	
	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();

		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		params.put("hospitalLocal", hospital);
		params.put("dataAtual", DateUtil.obterDataFormatadaHoraMinutoSegundo(new Date()));
		params.put("funcionalidade", "Quantidade de Prescrições Dispensadas/Triadas");
		params.put("nomeRelatorio", "AFAR_QUANT_PRCR_DISP");		

		return params;			
	}
	
	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 * 
	 */
	public StreamedContent getRenderPdf() throws IOException,
	ApplicationBusinessException, JRException, SystemException, DocumentException {
		DocumentoJasper documento = gerarDocumento();

		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}
	
	public List<QuantidadePrescricoesDispensacaoVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<QuantidadePrescricoesDispensacaoVO> colecao) {
		this.colecao = colecao;
	}

	public Date getDataEmissaoInicio() {
		return dataEmissaoInicio;
	}

	public void setDataEmissaoInicio(Date dataEmissaoInicio) {
		this.dataEmissaoInicio = dataEmissaoInicio;
	}

	public Date getDataEmissaoFim() {
		return dataEmissaoFim;
	}

	public void setDataEmissaoFim(Date dataEmissaoFim) {
		this.dataEmissaoFim = dataEmissaoFim;
	}
}
