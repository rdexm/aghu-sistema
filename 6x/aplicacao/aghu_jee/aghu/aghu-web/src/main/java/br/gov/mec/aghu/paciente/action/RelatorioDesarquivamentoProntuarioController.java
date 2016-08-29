package br.gov.mec.aghu.paciente.action;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

/**
 * @author lalegre
 */
public class RelatorioDesarquivamentoProntuarioController extends ActionController {

	private static final long serialVersionUID = -7522372679303914891L;
	
	private static final String REDIRECIONA_RELATORIO_DESARQUIVAMENTO_PRONTUARIO_PDF = "relatorioDesarquivamentoProntuarioPdf";

	@Inject
	private RelatorioDesarquivamentoProntuarioPDFController relatorioDesarquivamentoProntuarioPDFController;
	
	private Date dataMovimento;
	private Integer solicitacao;
	
	
	public enum RelatorioDesarquivamentoProntuarioControllerExceptionCode implements BusinessExceptionCode {
		FILTRO_INVALIDO_RELATORIO_DESARQUIVAMENTO_PRONTUARIO
	}
	
	@PostConstruct
	public void init() {
		this.begin(conversation);
	}
	
	public void imprimirRelatorio() {
		if(dataMovimento == null && solicitacao == null){
			this.apresentarMsgNegocio(
					Severity.ERROR,
					RelatorioDesarquivamentoProntuarioControllerExceptionCode.FILTRO_INVALIDO_RELATORIO_DESARQUIVAMENTO_PRONTUARIO
							.toString());
		} else {
			relatorioDesarquivamentoProntuarioPDFController.setDataMovimento(dataMovimento);
			relatorioDesarquivamentoProntuarioPDFController.setSolicitacao(solicitacao);
			relatorioDesarquivamentoProntuarioPDFController.directPrint();
		}
	}
	
	public String visualizarRelatorio() {
		if(dataMovimento == null && solicitacao == null){
			this.apresentarMsgNegocio(
					Severity.ERROR,
					RelatorioDesarquivamentoProntuarioControllerExceptionCode.FILTRO_INVALIDO_RELATORIO_DESARQUIVAMENTO_PRONTUARIO
							.toString());
			return null;
			
		} else {
			relatorioDesarquivamentoProntuarioPDFController.setDataMovimento(dataMovimento);
			relatorioDesarquivamentoProntuarioPDFController.setSolicitacao(solicitacao);
			
			DocumentoJasper documento = null;
			
			try {
				documento = relatorioDesarquivamentoProntuarioPDFController.gerarDocumentoJasper();
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
			
			FacesContext
					.getCurrentInstance()
					.getExternalContext()
					.getSessionMap()
					.put("documentoRelatorioDesarquivamentoProntuarioPdf",
							documento); 

			return REDIRECIONA_RELATORIO_DESARQUIVAMENTO_PRONTUARIO_PDF;
		}
	}

	public void limpar() {
		dataMovimento = null;
		solicitacao = null;
	}
	
	// GETs AND SETs
	public Date getDataMovimento() {
		return dataMovimento;
	}

	public void setDataMovimento(Date dataMovimento) {
		this.dataMovimento = dataMovimento;
	}

	public Integer getSolicitacao() {
		return solicitacao;
	}

	public void setSolicitacao(Integer solicitacao) {
		this.solicitacao = solicitacao;
	}
}
