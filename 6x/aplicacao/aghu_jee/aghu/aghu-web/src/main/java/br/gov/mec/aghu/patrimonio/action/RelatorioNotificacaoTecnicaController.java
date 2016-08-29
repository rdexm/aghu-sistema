package br.gov.mec.aghu.patrimonio.action;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioStatusNotificacaoTecnica;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.patrimonio.business.IPatrimonioFacade;
import br.gov.mec.aghu.patrimonio.vo.NotificacaoTecnicaItemRecebimentoProvisorioVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;

public class RelatorioNotificacaoTecnicaController extends ActionReport {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9072899076425788260L;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IPatrimonioFacade patrimonioFacade;
	
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@Inject
	private SistemaImpressao sistemaImpressao;

	private static final Log LOG = LogFactory.getLog(RelatorioNotificacaoTecnicaController.class);
	
	private StreamedContent media;
		
	private Long pntSeq;
	
	private String voltarPara;
	
	@PostConstruct
	protected void inicializar() {
		begin(conversation);	
	}
	
	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/patrimonio/report/notificacaoTecnicaItemBemPermanente.jasper";
	}

	/**
	 * Ação do botão Imprimir.
	 */
	public String imprimir() throws ApplicationBusinessException, JRException, SistemaImpressaoException, UnknownHostException {
		this.directPrint();
		return this.voltar();
	}
	
	/**
	 * Ação do botão Voltar
	 */
	public String voltar() {
		return voltarPara;
	}
	
	public void directPrint(){
		try {
			DocumentoJasper documento = gerarDocumento();
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
			this.apresentarMsgNegocio(Severity.INFO, "DOC_ENVIADO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}		
	}

	@Override
	protected List<NotificacaoTecnicaItemRecebimentoProvisorioVO> recuperarColecao() throws ApplicationBusinessException {
		List<NotificacaoTecnicaItemRecebimentoProvisorioVO> listaVO = new ArrayList<NotificacaoTecnicaItemRecebimentoProvisorioVO>();
		NotificacaoTecnicaItemRecebimentoProvisorioVO itemVO = patrimonioFacade.obterNotificacaoTecnicaItemRecebProvisorio(pntSeq);
		itemVO.setNotaFiscal(estoqueFacade.obterNotaFiscalItemRecebimento(itemVO.getReceb()));
		itemVO.setTipoNotificacao(DominioStatusNotificacaoTecnica.obterDominioStatusNotificacaoTecnica(itemVO.getStatus()));
		listaVO.add(itemVO);
		return listaVO;
	}
	
	@Override
	public Map<String, Object> recuperarParametros() {
		Map<String, Object> parametros = new HashMap<String, Object>();
		try {
			parametros.put("caminhoLogo", parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_LOGO_HOSPITAL_JEE7));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return parametros;		
	}
	
	public StreamedContent getRenderPdf() throws ApplicationBusinessException, JRException, IOException, DocumentException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(Boolean.TRUE));
	}
	
	public Long getPntSeq() {
		return pntSeq;
	}

	public void setPntSeq(Long pntSeq) {
		this.pntSeq = pntSeq;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}
}
