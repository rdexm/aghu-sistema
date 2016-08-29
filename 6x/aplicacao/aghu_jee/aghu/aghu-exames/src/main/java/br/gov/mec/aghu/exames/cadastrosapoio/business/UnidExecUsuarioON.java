package br.gov.mec.aghu.exames.cadastrosapoio.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelUnidExecUsuarioDAO;
import br.gov.mec.aghu.model.AelUnidExecUsuario;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class UnidExecUsuarioON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(UnidExecUsuarioON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelUnidExecUsuarioDAO aelUnidExecUsuarioDAO;

	private static final long serialVersionUID = 8801565685123065356L;

	public AelUnidExecUsuario atualizar(AelUnidExecUsuario elemento, AghUnidadesFuncionais unidFuncional) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (elemento != null) {
			getAelUnidExecUsuarioDAO().removerPorId(elemento.getId());
		}
		
		elemento = new AelUnidExecUsuario();
		elemento.setId(servidorLogado.getId());
		elemento.setServidor(servidorLogado);
		elemento.setUnfSeq(unidFuncional);
		getAelUnidExecUsuarioDAO().persistir(elemento);
		
		return elemento;
	}
	
	private AelUnidExecUsuarioDAO getAelUnidExecUsuarioDAO() {
		return aelUnidExecUsuarioDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
