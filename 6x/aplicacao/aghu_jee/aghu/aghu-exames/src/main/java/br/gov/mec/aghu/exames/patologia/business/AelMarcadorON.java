package br.gov.mec.aghu.exames.patologia.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.dao.AelMarcadorDAO;
import br.gov.mec.aghu.model.AelMarcador;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * @author rafael.fonseca
 *
 */

@Stateless
public class AelMarcadorON extends BaseBusiness {

	@Inject
	private AelMarcadorDAO aelMarcadorDAO;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;	

	private static final long serialVersionUID = -3014763071874726520L;
	
	private static final Log LOG = LogFactory.getLog(AelMarcadorON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	public String ativarInativarAelMarcador(AelMarcador aelMarcadorEdicao) {
		String mensagem = "";
		
		if (DominioSituacao.A.equals(aelMarcadorEdicao.getIndSituacao())) {
			aelMarcadorEdicao.setIndSituacao(DominioSituacao.I);
			mensagem = "MENSAGEM_MARCADOR_DESATIVADO_SUCESSO";
		} else {
			aelMarcadorEdicao.setIndSituacao(DominioSituacao.A);
			mensagem = "MENSAGEM_MARCADOR_ATIVADO_SUCESSO";
		}
		
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		aelMarcadorEdicao.setServidorInclusao(servidorLogado);
		aelMarcadorEdicao.setAlteradoEm(new Date());
		
		getAelMarcadorDAO().atualizar(aelMarcadorEdicao);
		getAelMarcadorDAO().flush();
		
		return mensagem;
	}

	protected AelMarcadorDAO getAelMarcadorDAO() {
		return this.aelMarcadorDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}	
}
