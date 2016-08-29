package br.gov.mec.aghu.suprimentos.action;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.cups.business.ICupsFacade;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.suprimentos.vo.RelAutorizacaoFornecimentoSaldoEntregaVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateFormatUtil;

import com.itextpdf.text.DocumentException;

//Estória #6795
public class RelatorioAutorizacaoFornecimentoSaldoEntregaController extends ActionReport{

	private static final long serialVersionUID = -1779393346831045335L;
	private static final Log LOG = LogFactory.getLog(RelatorioAutorizacaoFornecimentoSaldoEntregaController.class);
	//private static final String RELATORIO_AUTORIZACAO_FORNECIMENTO_SALDO_ENTREGA_PDF = "relatorioAutorizacaoFornecimentoSaldoEntregaPdf";
	private static final String RELATORIO_AUTORIZACAO_FORNECIMENTO_SALDO_ENTREGA = "relatorioAutorizacaoFornecimentoSaldoEntrega";

	private Integer lctNumero;              //p_pfr_lct_numero
	private Short nroComplemento;           //p_nro_complemento  
	private Integer sequenciaAlteracao;     //p_sequencia_alteracao
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	@EJB
	private ICupsFacade cupsFacade;

	@EJB
	private IComprasFacade comprasFacade;
	
	
	@PostConstruct
	protected void inicializar(){
		LOG.debug("begin conversation");
		this.begin(conversation);
	}
	
	//Criar método de pesquisa na sco_af_jn
	public List<RelAutorizacaoFornecimentoSaldoEntregaVO>obterListaAutorizacaoFornecimentoSaldoEentrega(Integer lctNumero, Integer sequenciaAlteracao, Short nroComplemento){
		return this.comprasFacade.obterListaAutorizacaoFornecimentoSaldoEntrega(lctNumero, sequenciaAlteracao, nroComplemento);
	}

	/**
	 * Dados que serão impressos 
	 */
	private List<RelAutorizacaoFornecimentoSaldoEntregaVO>  colecao = new ArrayList<RelAutorizacaoFornecimentoSaldoEntregaVO>();

	public void limparPesquisa() {
		this.lctNumero = null;
		this.sequenciaAlteracao = null;
		this.nroComplemento=null;

	}

	/**
	 * Impressão direta usando o CUPS.
	 * 
	 * @return
	 * @throws JRException
	 */

	public void directPrint() {

		try {			

			colecao = this.comprasFacade.obterListaAutorizacaoFornecimentoSaldoEntrega(
					getLctNumero(), 
					getsequenciaAlteracao(),
					getNroComplemento());


			if(colecao.isEmpty()){
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA");
				return;
			}

//			print(false);
//			getCupsFacade().enviarPdfCupsPorClasse(
//				getArquivoGerado(),
//				DominioNomeRelatorio.AUTORIZACAO_FORNECIMENTO_SALDO_ENTREGA,
//				super.getEnderecoRedeHostRemoto()
//			);
			DocumentoJasper documento = gerarDocumento();
			
			this.sistemaImpressao.imprimir(documento.getJasperPrint(),
					super.getEnderecoIPv4HostRemoto());
			this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (UnknownHostException e) {
			apresentarMsgNegocio(Severity.ERROR, e.getLocalizedMessage());
		} catch (JRException e) {
			LOG.error(e.getMessage(),e);
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		} 
	}

	/**CadastrosBasicosInternacaoFacade
	 * Apresenta PDF na tela do navegador.
	 * 
	 * @return
	 * @throws BaseException
	 * @throws JRException	
	 * @throws IOException
	 */
	/*
	public String print(){
		try {

			colecao = this.comprasFacade.obterListaAutorizacaoFornecimentoSaldoEentrega(
					getLctNumero(), 
					getsequenciaAlteracao(),
					getNroComplemento());this.comprasFacade.obterListaAutorizacaoFornecimentoSaldoEentrega


			if(colecao.isEmpty()){
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA");
				return null;
			}		
			return RELATORIO_AUTORIZACAO_FORNECIMENTO_SALDO_ENTREGA_PDF;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}*/
	 

	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 * 
	 * @param out
	 * @param data
	 * @throws com.itextpdf.text.DocumentException 
	 * @throws IOException	
	 * @throws JRException
	 */
	public StreamedContent getRenderPdf() throws IOException, JRException, DocumentException {

		try {
			// Gera o PDF		
			DocumentoJasper documento = gerarDocumento();
			return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
//		} catch (DocumentException e) {
//			apresentarMsgNegocio(Severity.ERROR, e.getLocalizedMessage());
		}
	}
	
	public String voltar(){
		return RELATORIO_AUTORIZACAO_FORNECIMENTO_SALDO_ENTREGA;
	}


	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/farmacia/report/relatorioAutorizacaoFornecimentoSaldoEntrega.jasper";
	}

	@Override
	public Collection<RelAutorizacaoFornecimentoSaldoEntregaVO> recuperarColecao() throws ApplicationBusinessException {
		return colecao;
	}

	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();

		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		params.put("hospitalLocal", hospital);
		params.put("dataAtual", DateFormatUtil.obterDataAtualHoraMinutoSegundo());
		params.put("lctNumero", getLctNumero());
		params.put("nroComplemento", getNroComplemento());
		params.put("sequenciaAlteracao", getsequenciaAlteracao());
		params.put("nomeRelatorio", "SCO_AF_ENTREGA");		

		return params;			
	}


	//Getters & Setters

	public ICupsFacade getCupsFacade() {
		return cupsFacade;
	}

	public void setCupsFacade(ICupsFacade cupsFacade) {
		this.cupsFacade = cupsFacade;
	}

	public List<RelAutorizacaoFornecimentoSaldoEntregaVO> getColecao() {
		return colecao;
	}

	public Integer getSequenciaAlteracao() {
		return sequenciaAlteracao;
	}


	public void setSequenciaAlteracao(Integer sequenciaAlteracao) {
		this.sequenciaAlteracao = sequenciaAlteracao;
	}

	public void setColecao(List<RelAutorizacaoFornecimentoSaldoEntregaVO> colecao) {
		this.colecao = colecao;
	}


	public Integer getLctNumero() {
		return this.lctNumero;
	}

	public void setLctNumero(Integer lctNumero) {
		this.lctNumero = lctNumero;
	}

	public Integer getsequenciaAlteracao() {
		return this.sequenciaAlteracao;
	}

	public void setsequenciaAlteracao(Integer sequenciaAlteracao) {
		this.sequenciaAlteracao = sequenciaAlteracao;
	}

	public Short getNroComplemento() {
		return nroComplemento;
	}

	public void setNroComplemento(Short nroComplemento) {
		this.nroComplemento = nroComplemento;
	}

}
