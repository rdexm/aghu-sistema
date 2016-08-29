package br.gov.mec.aghu.patrimonio.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.PtmBemPermanentes;
import br.gov.mec.aghu.patrimonio.business.IPatrimonioFacade;
import br.gov.mec.aghu.patrimonio.vo.ImprimirFichaAceiteTecnicoBemPermanenteVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;

public class RelatorioImprimirFichaAceiteTecnicoBemPermanente extends ActionReport{
	




	/**
	 * 
	 */
	private static final long serialVersionUID = 345681235678L;

	@EJB
	private IPatrimonioFacade patrimonioFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	private static final Log LOG = LogFactory.getLog(RelatorioImprimirFichaAceiteTecnicoBemPermanente.class);
	
	private Integer avtSeq;
	private List<PtmBemPermanentes> listaBens;
	private StreamedContent media;
	private static final String PAGINA_VISUALIZAR = "patrimonio-visualizarRelatorioFichaAceiteTecnico";
	private static final String VOLTAR_PARA = "patrimonio-registrarAceiteTecnicoCRUD";
	
	@PostConstruct
	public void init(){
		begin(conversation);
	}
	
	public String visualizar(Integer avtSeq) throws ApplicationBusinessException, JRException, IOException, DocumentException{
		this.avtSeq = avtSeq;
		inicializar();
		DocumentoJasper documento = gerarDocumento();
		this.media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(true)));
        return PAGINA_VISUALIZAR;
	}

	public void imprimir(){
		inicializar();
		try {
			DocumentoJasper documento = gerarDocumento();
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
			apresentarMsgNegocio(Severity.INFO, "DOC_ENVIADO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);

		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}
	
	private void inicializar(){
		listaBens = patrimonioFacade.recuperarCamposVariaveisRelatorio(avtSeq);
	}
	
	@Override
	protected List<ImprimirFichaAceiteTecnicoBemPermanenteVO> recuperarColecao() throws ApplicationBusinessException {
		List<ImprimirFichaAceiteTecnicoBemPermanenteVO> lista = new ArrayList<ImprimirFichaAceiteTecnicoBemPermanenteVO>();
		ImprimirFichaAceiteTecnicoBemPermanenteVO vo = patrimonioFacade.recuperarCamposFixosRelatorio(avtSeq);
		lista.add(vo);
		return lista;
	}
	
	@Override
	protected String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/patrimonio/report/aceiteTecnicoBensPermanentes.jasper";
	}
	
	@Override
	protected Map<String, Object> recuperarParametros() {
		Map<String, Object> parametros  = new HashMap<String, Object>();
		try {
			parametros.put("listaBens", listaBens);
			parametros.put("caminhoLogo", parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_LOGO_HOSPITAL_JEE7));
			parametros.put("SUBREPORT_DIR", "br/gov/mec/aghu/patrimonio/report/");
		} catch (ApplicationBusinessException e) {
			LOG.error(e.getMessage(), e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
		return parametros;
	}

	public String voltar(){
		return VOLTAR_PARA;
	}
	
	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	public List<PtmBemPermanentes> getListaBens() {
		return listaBens;
	}

	public void setListaBens(List<PtmBemPermanentes> listaBens) {
		this.listaBens = listaBens;
	}
}
