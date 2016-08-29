package br.gov.mec.aghu.blococirurgico.procdiagterap.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.PdtTecnicaDAO;
import br.gov.mec.aghu.model.PdtTecnica;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * #24722
 */
@Stateless
public class PdtTecnicaON extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(PdtTecnicaON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private PdtTecnicaDAO pdtTecnicaDAO;


	@EJB
	private IRegistroColaboradorFacade iRegistroColaboradorFacade;

	@EJB
	private PdtTecnicaRN pdtTecnicaRN;

	private static final long serialVersionUID = 5722305668331041985L;

	protected PdtTecnicaRN getTecnicasRN() {
		return pdtTecnicaRN;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return iRegistroColaboradorFacade;
	}
	
	public String persistirPdtTecnica(PdtTecnica tecnica) throws ApplicationBusinessException {
		tecnica.setRapServidores(servidorLogadoFacade.obterServidorLogado());
		if (tecnica.getSeq() == null) {
			tecnica.setCriadoEm(new Date());
			getPdtTecnicaDAO().persistir(tecnica);
			return "MENSAGEM_TECNICAS_INSERCAO_COM_SUCESSO";
		} else {
			getTecnicasRN().preUpdatePdtTecnica(tecnica);
			getPdtTecnicaDAO().atualizar(tecnica);
			getTecnicasRN().posUpdatePdtTecnica(tecnica);
			return "MENSAGEM_TECNICAS_ALTERACAO_COM_SUCESSO";
		}
	}

	protected PdtTecnicaDAO getPdtTecnicaDAO() {
		return pdtTecnicaDAO;
	}
}
