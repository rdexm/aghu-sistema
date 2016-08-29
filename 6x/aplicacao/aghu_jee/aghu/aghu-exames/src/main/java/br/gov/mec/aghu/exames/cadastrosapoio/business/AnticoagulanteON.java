package br.gov.mec.aghu.exames.cadastrosapoio.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelAnticoagulante;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class AnticoagulanteON extends BaseBusiness {

	@EJB
	private AnticoagulanteRN anticoagulanteRN;
	
	private static final Log LOG = LogFactory.getLog(AnticoagulanteON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	private static final long serialVersionUID = -5960817732553706002L;
	
	public AelAnticoagulante persistirAnticoagulante(AelAnticoagulante elemento) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if(elemento.getSeq() == null) {
			elemento.setDescricao(elemento.getDescricao().toUpperCase());
			//Preenche com o usuário logado
			elemento.setServidor(servidorLogado);
			//Realiza inserção
			return getAnticoagulanteRN().inserir(elemento);
			
		} else {
			//Realiza atualização
			return getAnticoagulanteRN().atualizar(elemento);
		}
	}
	
	public AelAnticoagulante ativarInativarAnticoagulante(AelAnticoagulante aelAnticoagulante) throws ApplicationBusinessException {
		aelAnticoagulante.setIndSituacao(aelAnticoagulante.getIndSituacao() == DominioSituacao.A ? DominioSituacao.I : DominioSituacao.A);
		return getAnticoagulanteRN().atualizar(aelAnticoagulante);
	}

	protected AnticoagulanteRN getAnticoagulanteRN() {
		return anticoagulanteRN;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
}