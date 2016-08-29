package br.gov.mec.aghu.paciente.business;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * 
 * Classe de apoio para as Business Facades. Ela em geral agrupa as
 * funcionalidades encontradas em packages e procedures do AGHU.
 * 
 * Ela poderia ser uma classe com acesso friendly ou default e não ser um
 * componente seam.
 * 
 * Mas fazendo assim facilita, pois ela também pode receber uma referência para
 * o EntityManager.
 * 
 * Outra forma de fazer é instanciar ela diretamente do ON e passar o entity
 * manager para seus parâmetros. Neste caso os metodos desta classe poderiam ser
 * até estaticos e nao necessitar de instanciação. Ai ela seria apenas um
 * particionamento lógico de código e não um componente que possa ser injetado
 * em qualquer outro contexto.
 * 
 * ATENÇÃO, Os metodos desta classe nunca devem ser acessados diretamente por
 * qualquer classe que não a ON correspondente. Por isso sugere-se que todos os
 * métodos desta sejam friendly (default) ou private.
 * 
 */
@Stateless
public class McokIdcRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(McokIdcRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


	/**
	 * 
	 */
	private static final long serialVersionUID = 8537838685583628727L;

	public void setvVeioSubsGestaIdc(Boolean vVeioSubsGestaIdc) {
	//	this.atribuirContextoSessao(VariaveisSessaoEnum.MCOK_IDC_RN_V_VEIO_SUBS_GESTA_IDC, vVeioSubsGestaIdc);
	}

}
