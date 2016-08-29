package br.gov.mec.aghu.paciente.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.McoRecemNascidos;
import br.gov.mec.aghu.perinatologia.business.IPerinatologiaFacade;
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
public class RecemNascidosRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(RecemNascidosRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IPerinatologiaFacade perinatologiaFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8911131309475841283L;

	/**
	 * Método que obtem a data de nascimento do paciente registrado na tabela
	 * MCO_RECEM_NASCIDOS
	 * 
	 * @param codigoPaciente
	 * @return
	 */
	public Date obterDataNascimentoRecemNascidos(Integer codigoPaciente){
		return this.getPerinatologiaFacade().obterDataNascimentoRecemNascidos(codigoPaciente);
	}
	
	/**
	 * Método que obtem a data de nascimento do paciente registrado na tabela
	 * MCO_RECEM_NASCIDOS
	 * 
	 * @param codigoPaciente
	 * @return
	 */
	public McoRecemNascidos obterRecemNascidosPorCodigo(Integer codigoPaciente){
		return this.getPerinatologiaFacade().obterRecemNascidosPorCodigo(codigoPaciente);
	}
	
	/**
	 * Método responsável por persistir no banco um objeto
	 * da tabela MCO_RECEM_NASCIDOS
	 * 
	 * @param recemNascido
	 * @return
	 */
	public void persistirRecemNascido(McoRecemNascidos recemNascido){
		this.getPerinatologiaFacade().inserirMcoRecemNascidos(recemNascido, true);
	}

	protected IPerinatologiaFacade getPerinatologiaFacade() {
		return perinatologiaFacade;
	}

}
