package br.gov.mec.aghu.certificacaodigital.action;

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.AghCertificadoDigital;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;



public class GerirCertificadoDigitalController extends ActionController{

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(GerirCertificadoDigitalController.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2427754812718530996L;

	@EJB
	private ICertificacaoDigitalFacade certificacaoDigitalFacade;
	
	@Inject
	private GerirCertificadoDigitalPaginatorController gerirCertificadoDigitalPaginatorController;	
	
	private AghCertificadoDigital aghCertificadoDigital;
	
	private Boolean indHabilitado = Boolean.TRUE;
	private Boolean indEmitido = Boolean.FALSE;
	
	
	public String iniciarInclusao() {
		this.preparaInclusao();
		return "gerirCertificadosDigitaisCRUD";
	}
	
	public String editarCertificado(AghCertificadoDigital aghCertificadoDigital) {
		

		try {
			
			//Submete o procedimento para ser persistido
			certificacaoDigitalFacade.persistirAghCertificadoDigital(aghCertificadoDigital);
			

			//Apresenta a mensagen
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EMISSAO_CERTIFICADO", aghCertificadoDigital.getServidorResp().getPessoaFisica().getNome());
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		return null;
	}
	
	public void preparaInclusao(){
		this.setAghCertificadoDigital(new AghCertificadoDigital());
		this.getAghCertificadoDigital().setIndHabilitaCertif(DominioSimNao.S);
		this.getAghCertificadoDigital().setIndEmissaoCertif(DominioSimNao.N);
		this.getAghCertificadoDigital().setServidorResp(gerirCertificadoDigitalPaginatorController.getResponsavel());
		this.getAghCertificadoDigital().setCriadoEm(Calendar.getInstance().getTime());
		this.getAghCertificadoDigital().setQtdEmissoes(Short.valueOf("0"));
		
		setIndEmitido(Boolean.FALSE);
		setIndHabilitado(Boolean.TRUE);
	}
	
	public void limpar(){
		this.preparaInclusao();
	}
	
	public String cancelar(){
		return "gerirCertificadosDigitaisPesquisa";
	}
	
	public void verificaEmissao(){
		if(this.getIndEmitido().equals(Boolean.TRUE)){
			this.getAghCertificadoDigital().setQtdEmissoes(Short.valueOf("1"));
			this.getAghCertificadoDigital().setDtEmissaoCertif(GregorianCalendar.getInstance().getTime());
		}else {
			this.getAghCertificadoDigital().setQtdEmissoes(Short.valueOf("0"));
			this.getAghCertificadoDigital().setDtEmissaoCertif(null);
		}
	}
	
	
	public String gravar() {
		
		
		try {
			
			if(getIndEmitido().equals(Boolean.TRUE)){
				aghCertificadoDigital.setIndEmissaoCertif(DominioSimNao.S);
			}
			
			//Submete o documento para ser persistido
			certificacaoDigitalFacade.persistirAghCertificadoDigital(aghCertificadoDigital);
			
			
			if(aghCertificadoDigital.getIndEmissaoCertif().isSim()){
			  apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_HABILITACAO_EMISSAO_CERTIFICADO", aghCertificadoDigital.getServidorResp().getPessoaFisica().getNome());
			}else{
			  apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_HABILITACAO_SERVIDOR_EMISSAO_CERTIFICADO", aghCertificadoDigital.getServidorResp().getPessoaFisica().getNome());
			}
			
		
		} catch(ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		} catch(PersistenceException e) {
			LOG.error("Exceção capturada: ", e);
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_HABILITACAO_SERVIDOR_EMITISSAO_CERTIFICADO", aghCertificadoDigital.getServidorResp().getPessoaFisica().getNome());
			return null;
		}
		
		return "gerirCertificadosDigitaisPesquisa";
	}

	public AghCertificadoDigital getAghCertificadoDigital() {
		return aghCertificadoDigital;
	}

	public void setAghCertificadoDigital(AghCertificadoDigital aghCertificadoDigital) {
		this.aghCertificadoDigital = aghCertificadoDigital;
	}

	public void setGerirCertificadoDigitalPaginatorController(
			GerirCertificadoDigitalPaginatorController gerirCertificadoDigitalPaginatorController) {
		this.gerirCertificadoDigitalPaginatorController = gerirCertificadoDigitalPaginatorController;
	}

	public Boolean getIndHabilitado() {
		return indHabilitado;
	}

	public void setIndHabilitado(Boolean indHabilitado) {
		this.indHabilitado = indHabilitado;
	}

	public Boolean getIndEmitido() {
		return indEmitido;
	}

	public void setIndEmitido(Boolean indEmitido) {
		this.indEmitido = indEmitido;
	}
	
	
	
}
