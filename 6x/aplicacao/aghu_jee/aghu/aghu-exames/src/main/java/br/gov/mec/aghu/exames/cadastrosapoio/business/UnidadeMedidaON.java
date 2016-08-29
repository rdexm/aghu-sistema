package br.gov.mec.aghu.exames.cadastrosapoio.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelUnidMedValorNormal;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class UnidadeMedidaON extends BaseBusiness {

	@EJB
	private UnidadeMedidaRN unidadeMedidaRN;
	
	private static final Log LOG = LogFactory.getLog(UnidadeMedidaON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	private static final long serialVersionUID = 9034654402039423399L;

	public AelUnidMedValorNormal persistirUnidadeMedida(AelUnidMedValorNormal elemento) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AelUnidMedValorNormal retorno = null;
		
		if(elemento.getSeq() == null) {
			//Preenche com o usuário logado
			elemento.setServidor(servidorLogado);
			//Realiza inserção
			retorno = getUnidadeMedidaRN().inserir(elemento);
		} else {
			//Realiza atualização
			retorno = getUnidadeMedidaRN().atualizar(elemento);
		}
		
		return retorno;
	}
	
	public AelUnidMedValorNormal ativarInativarAelUnidMedValorNormal(AelUnidMedValorNormal aumvn) throws ApplicationBusinessException{
		aumvn.setIndSituacao(aumvn.getIndSituacao() == DominioSituacao.A ? DominioSituacao.I : DominioSituacao.A);
		return getUnidadeMedidaRN().atualizar(aumvn);
	}
	
	

	protected UnidadeMedidaRN getUnidadeMedidaRN() {
		return unidadeMedidaRN;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
			
}
