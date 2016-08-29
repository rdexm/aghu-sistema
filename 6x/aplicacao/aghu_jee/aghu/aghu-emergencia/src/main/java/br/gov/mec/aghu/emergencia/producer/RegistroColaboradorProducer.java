package br.gov.mec.aghu.emergencia.producer;

import java.io.Serializable;
import java.security.Principal;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import br.gov.mec.aghu.registrocolaborador.service.IRegistroColaboradorService;
import br.gov.mec.aghu.registrocolaborador.vo.Usuario;
import br.gov.mec.aghu.registrocolaborador.vo.UsuarioFiltro;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.service.ServiceException;


/**
 * Fornece a interface(port) para os web services.
 * 
 * @author cvagheti
 * 
 */

@RequestScoped
public class RegistroColaboradorProducer implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID  = -3423984755101825127L;


	@Inject
	private IRegistroColaboradorService registroColaboradorService;
	
	@Inject 
	private Principal principal;

   
    private Usuario usuario = null;
    
    public enum RegistroColaboradorProducerExceptionCode implements BusinessExceptionCode {
	   	 MENSAGEM_EMER_USUARIO_NAO_ENCONTRADO;
	}
    
    @Produces @QualificadorUsuario @RequestScoped
    public Usuario obterUsuarioLogado() throws ApplicationBusinessException {  
	    UsuarioFiltro filtro = new UsuarioFiltro();
	    filtro.setLogin(principal.getName().toLowerCase());	    
	   
		try {
			this.usuario =  registroColaboradorService.buscaUsuario(filtro);
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(RegistroColaboradorProducerExceptionCode.MENSAGEM_EMER_USUARIO_NAO_ENCONTRADO);
		}
    	return usuario;
	}

}
