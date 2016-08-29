package br.gov.mec.aghu.paciente.prontuarioonline.action;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade;
import br.gov.mec.aghu.dominio.DominioTipoDocumento;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.MamNotaAdicionalEvolucoes;
import br.gov.mec.aghu.paciente.prontuarioonline.business.IProntuarioOnlineFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.NotasPolVO;
import br.gov.mec.aghu.core.commons.BaseBean;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;



public class RelatorioNotasController extends ActionReport {

	private static final Log LOG = LogFactory.getLog(RelatorioNotasController.class);
	private static final long serialVersionUID = -3949012343792385390L;

	@EJB
	IProntuarioOnlineFacade prontuarioOnlineFacade;
	
	@EJB
	IAmbulatorioFacade ambulatorioFacade;

	@EJB
	private ICertificacaoDigitalFacade certificacaoDigitalFacade;
	
	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	

	private MamNotaAdicionalEvolucoes nota;
	private Collection<NotasPolVO> colecao;
	
	private Integer seqVersaoDoc;
	private Integer seqMamNotaAdicionalEvolucoes;
	private String paginaVoltar;
	
	public String imprimir(MamNotaAdicionalEvolucoes notaParam) {
		this.nota = notaParam;
		if(getProntuarioOnlineFacade().visualizarRelatorio(nota.getSeq())){
			print();
			seqVersaoDoc = getProntuarioOnlineFacade().recuperarVersaoDoc(nota.getSeq());
			return "visualizaRelatorio";
		}else{
			directPrint();
			return "imprimirRelatorio";
		}
	}

	@Override
	public Collection<NotasPolVO> recuperarColecao() {
		try {
			return getProntuarioOnlineFacade().pesquisarNotasPol(nota.getSeq());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	/**
	 * Impressão direta usando o CUPS.
	 * 
	 * @param pacienteProntuario
	 * @return
	 * @throws BaseException
	 * @throws JRException
	 * @throws SystemException
	 * @throws IOException
	 */
	public void directPrint() {
		try {
			DocumentoJasper documento = gerarDocumento(Boolean.TRUE);

			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());

			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}

	}
	
	@Override
	protected BaseBean getEntidadePai() {
		return nota;
	}
	/**
	 * Apresenta PDF na tela do navegador.
	 * 
	 * @return
	 */
	public void print() {
		colecao = recuperarColecao();
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/paciente/prontuarioonline/report/relatorioNotasPOL.jasper";
		
	}

	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		try {
			params.put("caminhoLogo", recuperarCaminhoLogo());
		} catch (BaseException e) {
			LOG.error("Erro ao tentar recuparar logotipo para o relatório",e);
		}		

		return params;
	}

	
	public String voltar() {
		return paginaVoltar;
	}
	
	
	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 * 
	 * @param out
	 * @param data
	 * @throws IOException
	 * @throws SystemException
	 * @throws JRException
	 * @throws DocumentException 
	 * @throws ApplicationBusinessException 
	 */
	public StreamedContent getRenderPdf() throws IOException,
			JRException, SystemException, DocumentException, ApplicationBusinessException {
		nota = getAmbulatorioFacade().buscarNotaParaRelatorio(seqMamNotaAdicionalEvolucoes);
		DocumentoJasper documento = gerarDocumento(Boolean.TRUE);
		return this.criarStreamedContentPdf(documento.getPdfByteArray(true)); // Protegido? = TRUE
	}

	// Getters e Setters

	public void setNota(MamNotaAdicionalEvolucoes nota) {
		this.nota = nota;
	}

	public MamNotaAdicionalEvolucoes getNota() {
		return nota;
	}

	public IProntuarioOnlineFacade getProntuarioOnlineFacade() {
		return prontuarioOnlineFacade;
	}

	public void setProntuarioOnlineFacade(
			IProntuarioOnlineFacade prontuarioOnlineFacade) {
		this.prontuarioOnlineFacade = prontuarioOnlineFacade;
	}

	public Collection<NotasPolVO> getColecao() {
		return colecao;
	}

	public void setColecao(Collection<NotasPolVO> colecao) {
		this.colecao = colecao;
	}

	public SistemaImpressao getSistemaImpressao() {
		return sistemaImpressao;
	}

	public void setSistemaImpressao(SistemaImpressao sistemaImpressao) {
		this.sistemaImpressao = sistemaImpressao;
	}

	public void setSeqVersaoDoc(Integer seqVersaoDoc) {
		this.seqVersaoDoc = seqVersaoDoc;
	}

	public Integer getSeqVersaoDoc() {
		return seqVersaoDoc;
	}

	public Integer getSeqMamNotaAdicionalEvolucoes() {
		return seqMamNotaAdicionalEvolucoes;
	}

	public void setSeqMamNotaAdicionalEvolucoes(Integer seqMamNotaAdicionalEvolucoes) {
		this.seqMamNotaAdicionalEvolucoes = seqMamNotaAdicionalEvolucoes;
	}

	public IAmbulatorioFacade getAmbulatorioFacade() {
		return ambulatorioFacade;
	}

	public void setAmbulatorioFacade(IAmbulatorioFacade ambulatorioFacade) {
		this.ambulatorioFacade = ambulatorioFacade;
	}

	public String getPaginaVoltar() {
		return paginaVoltar;
	}

	public void setPaginaVoltar(String paginaVoltar) {
		this.paginaVoltar = paginaVoltar;
	}

	public void gerarDocumentoCertificado(MamNotaAdicionalEvolucoes nota) throws BaseException {
		if (getCertificacaoDigitalFacade().verificarServidorHabilitadoCertificacaoDigitalUsuarioLogado()) {
			this.nota = nota;
			gerarDocumento(DominioTipoDocumento.NPO);
		}
		
	}
		
	public ICertificacaoDigitalFacade getCertificacaoDigitalFacade() {
		return certificacaoDigitalFacade;
	}
}