package br.gov.mec.aghu.exames.business;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class AelSecaoConfExameON extends BaseBusiness {

	
private static final Log LOG = LogFactory.getLog(AelSecaoConfExameON.class);

	@Override
	@Deprecated
	public Log getLogger() {
	return LOG;
	}
	
	private static final long serialVersionUID = -4819028681533768840L;
	
	/**
	 * Método utilizado para habilitar e desabilitar a coluna seção obrigatória da Configuração dos Exames.
	 * Utilizado para liberar o Laudo ou para uma etapa intermediária.
	 * @param ativo
	 * @param obrigatorio
	 * @return
	 */
	public boolean habilitaSecaoObrigatoria(boolean ativo, boolean obrigatorio){
		if(ativo && obrigatorio){
			return true;
		}
		
		if(!ativo || !obrigatorio){
			return false;
		}
		
		return false;
	}
	
	
	
}