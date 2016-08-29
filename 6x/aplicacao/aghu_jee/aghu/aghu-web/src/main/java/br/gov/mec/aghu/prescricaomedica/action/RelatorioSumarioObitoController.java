package br.gov.mec.aghu.prescricaomedica.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.RelatorioSumarioObitoVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;



public class RelatorioSumarioObitoController extends ActionReport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3669158547534554607L;

	private Integer seqAtendimento;
	
	private String  tipoImpressao;
	

	private List<MpmAltaSumario> listaAltasSumarios;


	
	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	IPrescricaoMedicaFacade prescricaoMedicaFacade;

	
	
	private static final Log LOG = LogFactory.getLog(RelatorioSumarioObitoController.class);
	
	

	/**
	 * Dados que serão impressos em PDF.
	 */
	private List<RelatorioSumarioObitoVO> colecao = new ArrayList<RelatorioSumarioObitoVO>(
			0);
	
	
	@PostConstruct
	public void init() {		
		begin(conversation);			
		
	}
	
	@Override
	public Collection<RelatorioSumarioObitoVO> recuperarColecao()  {

		AghAtendimentos atendimento = this.aghuFacade
				.obterAghAtendimentoPorChavePrimaria(seqAtendimento);

		listaAltasSumarios = atendimento.getAltasSumario();
		if (listaAltasSumarios.isEmpty()) {
			return null;
		}

		MpmAltaSumarioId id = this.listaAltasSumarios.get(
				this.listaAltasSumarios.size() - 1).getId();

		
		try {
			this.colecao.add(this.buscaColecao(id));
		} catch (BaseException e) {
			LOG.error("Erro ao tentar recuparar colecao ",e);
		}

		return this.colecao;
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
	public StreamedContent getRenderPdf() throws IOException,
	ApplicationBusinessException, JRException, SystemException, DocumentException {
		DocumentoJasper documento = gerarDocumento();

		return this.criarStreamedContentPdf(documento.getPdfByteArray(Boolean.TRUE));
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/prescricaomedica/report/relatorioSumarioObito.jasper";

		// alterar a chamada para o metodo da controller.(recuperarColecao())
	}

	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("SUBREPORT_DIR", "br/gov/mec/aghu/prescricaomedica/report/");
		params.put("previaUrlImagem", recuperaCaminhoImgBackground());
		try {
			params.put("caminhoLogo", recuperarCaminhoLogo());
		} catch (BaseException e)  {
			LOG.error("Erro ao tentar recuparar logotipo para o relatório",e);
		}		

		return params;
	}

	private String recuperaCaminhoImgBackground() {
		ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		String path = servletContext.getRealPath("/resources/img/report_previa.png");
		return path;
	}

	private RelatorioSumarioObitoVO buscaColecao(final MpmAltaSumarioId id) throws BaseException	 {
		return this.prescricaoMedicaFacade.criaRelatorioSumarioAlta(id, this.tipoImpressao);
	}

	public Integer getSeqAtendimento() {
		return seqAtendimento;
	}

	public void setSeqAtendimento(Integer seqAtendimento) {
		this.seqAtendimento = seqAtendimento;
	}

	public String getTipoImpressao() {
		return tipoImpressao;
	}

	public void setTipoImpressao(String tipoImpressao) {
		this.tipoImpressao = tipoImpressao;
	}

}
