package br.gov.mec.aghu.registrocolaborador.business;

import java.security.Principal;
import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.security.SecurityContextAssociation;

import br.gov.mec.aghu.casca.autenticacao.AghuQuartzPrincipal;
import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.dao.RapServidoresDAO;


@Modulo(ModuloEnum.REGISTRO_COLABORADOR)
@Stateless
public class ServidorLogadoFacade extends BaseFacade implements IServidorLogadoFacade {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7545173695403634127L;
	
	private static final Log LOG = LogFactory.getLog(ServidorLogadoFacade.class);
	
	@Inject
	private RapServidoresDAO rapServidoresDAO;
	
	@Override
	public RapServidores obterServidorLogado() {
		RapServidores servidor = null;
		final String msgErro = "Erro ao buscar Servidor Logado:" ;
		if (this.getSessionContext() != null
				&& this.getSessionContext().getCallerPrincipal() != null) {
			String login = this.getSessionContext().getCallerPrincipal().getName();
			if (!"anonymous".equalsIgnoreCase(login)) {
				try {
					servidor = rapServidoresDAO.obterServidorAtivoPorUsuario(login);
				} catch (ApplicationBusinessException e) {
					LOG.warn(msgErro + e.getMessage());
				}
			} else {
				//@InjetarLogin
				// pode ser o caso de metodos disparados pelo Quartz - alguma filha de AghuJob.
				Principal usuario = SecurityContextAssociation.getPrincipal();
				if (usuario instanceof AghuQuartzPrincipal) {
					//Se for instancia de AghuQuartzPrincipal significa que é do Quartz.
					login = usuario.getName();
					try {
						servidor = rapServidoresDAO.obterServidorAtivoPorUsuario(login);
					} catch (ApplicationBusinessException e) {
						LOG.warn(msgErro + e.getMessage());
					}
				}			
			}
		}
		
		return servidor;
	}

	@Override
	public RapServidores obterServidorLogadoSemCache() {
		final String msgErro = "Erro ao buscar Servidor Logado:" ;
		RapServidores servidor = null;

		if (this.getSessionContext() != null
				&& this.getSessionContext().getCallerPrincipal() != null) {
			String login = this.getSessionContext().getCallerPrincipal().getName();
			if (!"anonymous".equalsIgnoreCase(login)) {
				try {
					servidor = rapServidoresDAO.obterServidorAtivoPorUsuarioSemCache(login, new Date());
				} catch (ApplicationBusinessException e) {
					LOG.warn(msgErro + e.getMessage());
				}
			} else {
				//@InjetarLogin
				// pode ser o caso de metodos disparados pelo Quartz - alguma filha de AghuJob.
				Principal usuario = SecurityContextAssociation.getPrincipal();
				if (usuario instanceof AghuQuartzPrincipal) {
					//Se for instancia de AghuQuartzPrincipal significa que é do Quartz.
					login = usuario.getName();
					try {
						servidor = rapServidoresDAO.obterServidorAtivoPorUsuarioSemCache(login, new Date());
					} catch (ApplicationBusinessException e) {
						LOG.warn(msgErro + e.getMessage());
					}
				}			
			}
		}
		
		return servidor;
	}

	@Override
	public RapServidores obterServidorPorChavePrimaria(Integer matricula, Short vinCodigo) {
		return rapServidoresDAO.obter(new RapServidoresId(matricula, vinCodigo));	
	}
}