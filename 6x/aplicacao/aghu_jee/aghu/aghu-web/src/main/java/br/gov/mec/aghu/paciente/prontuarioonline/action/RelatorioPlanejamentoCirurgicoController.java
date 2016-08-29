package br.gov.mec.aghu.paciente.prontuarioonline.action;

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

import org.primefaces.model.StreamedContent;

import com.itextpdf.text.DocumentException;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.core.commons.BaseBean;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.dominio.DominioTipoDocumento;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.paciente.prontuarioonline.business.IProntuarioOnlineFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.PlanejamentoCirurgicoVO;
import net.sf.jasperreports.engine.JRException;


public class RelatorioPlanejamentoCirurgicoController extends ActionReport {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final long serialVersionUID = 6685084953597868114L;

	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@EJB
	private IProntuarioOnlineFacade prontuarioOnlineFacade;

	@EJB
	private IParametroFacade parametroFacade;
	
	private List<PlanejamentoCirurgicoVO> colecao = new ArrayList<PlanejamentoCirurgicoVO>(0);

	private String voltarRelatorioPlanejamento;
	
	private Integer seqMbcAgenda;
	private Integer seqMbcCirurgia;
	


	/**
	 * Impressão direta usando o CUPS.
	 */
	public void directPrint() throws ApplicationBusinessException {

		colecao = this.prontuarioOnlineFacade.recuperarPlanejamentoCirurgicoVO(getSeqMbcAgenda(), getSeqMbcCirurgia());

		try {
			DocumentoJasper documento = gerarDocumento();

			this.sistemaImpressao.imprimir(documento.getJasperPrint(),
					super.getEnderecoIPv4HostRemoto());

			this.apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			this.apresentarMsgNegocio(Severity.ERROR,
					"ERRO_GERAR_RELATORIO");
		}
	} 

	@Override
	public Collection<PlanejamentoCirurgicoVO> recuperarColecao() throws ApplicationBusinessException {
		
		return colecao = this.prontuarioOnlineFacade.recuperarPlanejamentoCirurgicoVO(getSeqMbcAgenda(), getSeqMbcCirurgia());
	
	}
	
	@Override
	protected BaseBean getEntidadePai() {
		if(this.colecao != null) {
			return this.colecao.get(0);
		}
		return null;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "/br/gov/mec/aghu/paciente/prontuarioonline/report/planejamentoCirurgico/mbcr_planej_cirurg.jasper";
	}

	@Override
	public Map<String, Object> recuperarParametros() {
		
			Map<String, Object> params = new HashMap<String, Object>();
			
			params.put("SUBREPORT_DIR", "br/gov/mec/aghu/paciente/prontuarioonline/report/planejamentoCirurgico/");
			params.put("titulo", "Planejamento Cirúrgico");
			try {
				params.put("caminhoLogo", recuperarCaminhoLogo());
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
					
			return params;		
	}

	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 * 
	 * @throws DocumentException
	 * @throws ApplicationBusinessException 
	 */
	public StreamedContent getRenderPdf() throws IOException, JRException, SystemException, DocumentException, ApplicationBusinessException {
		DocumentoJasper documento = gerarDocumento();
		//if (getPermiteImprimirPlanejamentoCirurgicoPOL()) {
		//	return this.criarStreamedContentPdf(documento.getPdfByteArray(false));			
		//} else {
			return this.criarStreamedContentPdf(documento.getPdfByteArray(true));			
		//}
	}		

	public void geraPendenciasCertificacaoDigital() throws BaseException {
		this.gerarDocumento(DominioTipoDocumento.PC);		
	}
	
	// Getters e Setters

	public SistemaImpressao getSistemaImpressao() {
		return sistemaImpressao;
	}

	public void setSistemaImpressao(SistemaImpressao sistemaImpressao) {
		this.sistemaImpressao = sistemaImpressao;
	}
	
	public IProntuarioOnlineFacade getProntuarioOnlineFacade() {
		return prontuarioOnlineFacade;
	}

	public void setProntuarioOnlineFacade(
			IProntuarioOnlineFacade prontuarioOnlineFacade) {
		this.prontuarioOnlineFacade = prontuarioOnlineFacade;
	}

	public IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	public void setParametroFacade(IParametroFacade parametroFacade) {
		this.parametroFacade = parametroFacade;
	}

	public List<PlanejamentoCirurgicoVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<PlanejamentoCirurgicoVO> colecao) {
		this.colecao = colecao;
	}

	public void setVoltarRelatorioPlanejamento(
			String voltarRelatorioPlanejamento) {
		this.voltarRelatorioPlanejamento = voltarRelatorioPlanejamento;
	}

	public String getVoltarRelatorioPlanejamento() {
		return voltarRelatorioPlanejamento;
	}

	public Integer getSeqMbcCirurgia() {
		return seqMbcCirurgia;
	}

	public void setSeqMbcAgenda(Integer seqMbcAgenda) {
		this.seqMbcAgenda = seqMbcAgenda;
	}

	public void setSeqMbcCirurgia(Integer seqMbcCirurgia) {
		this.seqMbcCirurgia = seqMbcCirurgia;
	}

	public Integer getSeqMbcAgenda() {
		return seqMbcAgenda;
	}	
}