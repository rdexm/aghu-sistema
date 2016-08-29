package br.gov.mec.aghu.prescricaomedica.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.DocumentException;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.EmptyReportException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.MpmCidAtendimento;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmPrescricaoMedicaId;
import br.gov.mec.aghu.model.MpmSolicitacaoConsultoria;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.constantes.EnumTipoImpressao;
import br.gov.mec.aghu.prescricaomedica.vo.PrescricaoMedicaVO;
import br.gov.mec.aghu.prescricaomedica.vo.RelatorioConsultoriaVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import net.sf.jasperreports.engine.JRException;

public class RelatorioConsultoriaController extends ActionReport {
	
	private static final String PAGINA_RELATORIO_CONSULTORIA = "relatorioConsultoria";
	private static final String PAGINA_RELATORIO_CONSULTORIA_PDF = "relatorioConsultoriaPdf";
	private static final long serialVersionUID = -5387306989566219975L;
	private static final Log LOG = LogFactory.getLog(RelatorioConsultoriaController.class);

	@Inject
	private SistemaImpressao sistemaImpressao;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;	
	
	@EJB
	private IAghuFacade aghuFacade;

	private List<RelatorioConsultoriaVO> colecao = new ArrayList<RelatorioConsultoriaVO>(0);
	
	private Boolean imprimir = true;
		
	private PrescricaoMedicaVO prescricaoMedicaVO;

	private EnumTipoImpressao tipoImpressao;
	
	private Integer atdSeq;
	
	private Integer scnSeq;
	
	private Boolean validarIndImpSolicConsultoria;

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}

	//@Observer("prescricaoConfirmada")
	public void observarEventoImpressaoPrescricaoConfirmada() throws BaseException, JRException,
			SystemException, IOException {

		print();

		if (getImprimir()) {

			try {
				DocumentoJasper documento = gerarDocumento();

				this.sistemaImpressao.imprimir(documento.getJasperPrint(),
						super.getEnderecoIPv4HostRemoto());

				apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_IMPRESSAO_RELATORIO", "Consultoria");
			} catch (SistemaImpressaoException e) {
				apresentarExcecaoNegocio(e);
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				apresentarMsgNegocio(Severity.ERROR,
						"ERRO_GERAR_RELATORIO");
			}
		
		}
	}
	
	@Override
	public String recuperarArquivoRelatorio() {
		return	"br/gov/mec/aghu/prescricaomedica/report/relatorioConsultoria.jasper";
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Collection recuperarColecao() throws ApplicationBusinessException {
		return this.colecao;
	}

	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();

		params.put("SUBREPORT_DIR", "br/gov/mec/aghu/prescricaomedica/report/");
		try {
			params.put("LOGO_HOSPITAL", recuperarCaminhoLogo());
		} catch (BaseException e) {
			LOG.error("Erro ao tentar recuparar logotipo para o relatório",e);
		}

		return params;
	}

	public String print() {

		try {
			if (tipoImpressao == EnumTipoImpressao.SEM_IMPRESSAO) {
				this.imprimir = false;
			}
			
			Enum[] leftJoin2 = {AghAtendimentos.Fields.CID_ATENDIMENTOS, AghAtendimentos.Fields.PACIENTE, Fields.CID_ATD, AghAtendimentos.Fields.UNIDADE_FUNCIONAL};

			AghAtendimentos atd = aghuFacade.obterAghAtendimentoPorChavePrimaria(prescricaoMedicaVO
					.getId().getAtdSeq(), null, leftJoin2);/*obterAtendimentoPeloSeq(prescricaoMedicaVO.getId().getAtdSeq());*/
			
			RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado(), new Date());

			// colecao = prescricaoMedicaFacade.gerarDadosRelatorioConsultoria(
			// servidor, atd, prescricaoMedicaVO.getId().getAtdSeq(),
			// prescricaoMedicaVO.getId().getSeq());

			colecao = prescricaoMedicaFacade.gerarDadosRelatorioConsultoria(servidorLogado, atd, prescricaoMedicaVO, validarIndImpSolicConsultoria);

			// impede a impressão se a lista não tiver itens gerados.
			if (colecao.size() <= 0) {
				this.imprimir = false;
			}

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return PAGINA_RELATORIO_CONSULTORIA_PDF;
	}
	
	public void directPrint() {

		try {
			MpmPrescricaoMedica pm = new MpmPrescricaoMedica(
					new MpmPrescricaoMedicaId(this.atdSeq, this.scnSeq));
			
			MpmSolicitacaoConsultoria solCons = this.prescricaoMedicaFacade.obterSolicitacaoConsultoriaPorId(atdSeq, scnSeq);
			
			PrescricaoMedicaVO vo = new PrescricaoMedicaVO();
			vo.setPrescricaoMedica(pm);
			if (solCons != null) {
				vo.setDthrInicio(solCons.getDthrSolicitada());
				vo.setDthrFim(solCons.getDthrFim());
			}else if (prescricaoMedicaVO != null) {
				vo.setDthrInicio(prescricaoMedicaVO.getDthrInicio());
				vo.setDthrFim(prescricaoMedicaVO.getDthrFim());
			}
			//this.prescricaoMedicaVO = vo;
			
			AghAtendimentos atd = aghuFacade.obterAtendimentoPeloSeq(vo.getId().getAtdSeq());
			RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado(), new Date());

			colecao = prescricaoMedicaFacade.gerarDadosRelatorioConsultoria(servidorLogado, atd, vo, validarIndImpSolicConsultoria);
			
			DocumentoJasper documento = gerarDocumento();
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		}catch (EmptyReportException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}
	
	public String cancelar(){
		return PAGINA_RELATORIO_CONSULTORIA;
	}

	/**
	 * Renderiza o relatório em formato PDF.
	 * 
	 * @param out
	 * @param data
	 * @throws IOException
	 * @throws BaseException
	 * @throws JRException
	 * @throws SystemException
	 * @throws DocumentException 
	 * @throws ApplicationBusinessException 
	 */
	public StreamedContent getRenderPdf() throws IOException,
		 JRException, SystemException, DocumentException, ApplicationBusinessException {
		
		if(colecao == null || (colecao != null && colecao.isEmpty())){
			AghAtendimentos atd = aghuFacade.obterAtendimentoPeloSeq(prescricaoMedicaVO.getId().getAtdSeq());
			RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado(), new Date());
			colecao = prescricaoMedicaFacade.gerarDadosRelatorioConsultoria(servidorLogado, atd, prescricaoMedicaVO, validarIndImpSolicConsultoria);
		}
		
		DocumentoJasper documento = gerarDocumento(Boolean.TRUE);
		return this.criarStreamedContentPdf(documento.getPdfByteArray(true));
	}

	public String voltar(){
		this.colecao = null;
		return "prescricaomedica-pesquisarFormularioConsultoria";
	}
	
	public Boolean getImprimir() {
		return imprimir;
	}

	public void setImprimir(Boolean imprimir) {
		this.imprimir = imprimir;
	}
	
	public EnumTipoImpressao getTipoImpressao() {
		return tipoImpressao;
	}

	public void setTipoImpressao(EnumTipoImpressao tipoImpressao) {
		this.tipoImpressao = tipoImpressao;
	}
	
	public PrescricaoMedicaVO getPrescricaoMedicaVO() {
		return prescricaoMedicaVO;
	}

	public void setPrescricaoMedicaVO(PrescricaoMedicaVO prescricaoMedicaVO) {
		this.prescricaoMedicaVO = prescricaoMedicaVO;
	}
	enum Fields {
	    CID_ATD("cidAtendimentos." + MpmCidAtendimento.Fields.CID)
		;
		private String fields;
		private Fields(String fields) {
			this.fields = fields;
		}
		public String toString() {
			return this.fields;
		}
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public Integer getScnSeq() {
		return scnSeq;
	}

	public void setScnSeq(Integer scnSeq) {
		this.scnSeq = scnSeq;
	}

	public Boolean getValidarIndImpSolicConsultoria() {
		if (validarIndImpSolicConsultoria == null) {
			validarIndImpSolicConsultoria = Boolean.TRUE;
		}
		return validarIndImpSolicConsultoria;
	}

	public void setValidarIndImpSolicConsultoria(Boolean validarIndImpSolicConsultoria) {
		this.validarIndImpSolicConsultoria = validarIndImpSolicConsultoria;
	}
}
