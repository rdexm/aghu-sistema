package br.gov.mec.aghu.internacao.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.controleinfeccao.business.IControleInfeccaoFacade;
import br.gov.mec.aghu.model.MciMvtoFatorPredisponentes;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

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
public class MvtoFatorPredisponentesRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(MvtoFatorPredisponentesRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IControleInfeccaoFacade controleInfeccaoFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4593419578940371524L;

	private enum MvtoFatorPredisponentesRNExceptionCode implements BusinessExceptionCode {
		ERRO_EXCLUIR_MVTO_FATOR_PREDISPONENTE
	}

	
	/**
	 * Método que chama as implementações das triggers responsáveis pela
	 * remoção de um registro da tabela MCI_MVTO_FATOR_PREDISPONENTES
	 * 
	 * 
	 * @param mvto
	 * @return
	 *  
	 */
	public void removerMvtoFatorPredisponente(MciMvtoFatorPredisponentes mvto) throws ApplicationBusinessException {
		//TODO ...
		mcitMfpArd(mvto);
		mcitMfpBsd(mvto);
		mcitMfpAsd(mvto);
	}
	
	
	/**
	 * Método que implementa a trigger
	 * 
	 * ORADB Trigger MCIT_MFP_ARD
	 * 
	 * @param mvto
	 * @return
	 *  
	 */
	//TODO Alterar nome deste método e avaliar se precisa mesmo ser implementado.
	public void mcitMfpArd(MciMvtoFatorPredisponentes mvto) throws ApplicationBusinessException {
		try {
			this.getControleInfeccaoFacade().removerMciMvtoFatorPredisponentes(mvto, true);
		} catch (Exception e) {
			logError("Erro ao remover o mvto fator predisponente.", e);
			throw new ApplicationBusinessException(MvtoFatorPredisponentesRNExceptionCode.ERRO_EXCLUIR_MVTO_FATOR_PREDISPONENTE);
		}
	}
	
	
	/**
	 * Método que implementa a trigger
	 * 
	 * ORADB trigger MCIT_MFP_ASD
	 * 
	 * @param mvto
	 * @return
	 *  
	 */
	//TODO Alterar nome deste método e avaliar se precisa mesmo ser implementado.
	public void mcitMfpAsd(MciMvtoFatorPredisponentes mvto){
		//TODO ...
	}
	
	
	/**
	 * Método que implementa a trigger
	 * 
	 * ORADB trigger MCIT_MFP_BSD
	 * 
	 * @param mvto
	 * @return
	 *  
	 */
	//TODO Alterar nome deste método e avaliar se precisa mesmo ser implementado.
	public void mcitMfpBsd(MciMvtoFatorPredisponentes mvto){
		//TODO ...
	}
	
	
	/**
	 * Método que chama as implementações das triggers responsáveis pela
	 * inserção de um registro da tabela MCI_MVTO_FATOR_PREDISPONENTES
	 * 
	 * 
	 * @param mvto
	 * @return
	 *  
	 */
	public void inserirMvtoFatorPredisponente(MciMvtoFatorPredisponentes mvto){
		this.mcitMfpBri(mvto);
		this.mcitMfpAri(mvto);
		this.mcitMfpBsi(mvto);
		this.getControleInfeccaoFacade().inserirMciMvtoFatorPredisponentes(mvto, true);
	}
	
	/**
	 * Método que implementa a trigger
	 * 
	 * ORADB Trigger MCIT_MFP_ARI
	 * 
	 * @param mvto
	 * @return
	 *  
	 */
	//TODO Alterar nome deste método e avaliar se precisa mesmo ser implementado.
	public void mcitMfpAri(MciMvtoFatorPredisponentes mvto){
		//TODO ...
	}
	
	/**
	 * Método que implementa a trigger
	 * 
	 * ORADB Trigger MCIT_MFP_BRI
	 * 
	 * @param mvto
	 * @return
	 *  
	 */
	//TODO Alterar nome deste método e avaliar se precisa mesmo ser implementado.
	public void mcitMfpBri(MciMvtoFatorPredisponentes mvto){
		//TODO ...
	}
	
	/**
	 * Método que implementa a trigger
	 * 
	 * ORADB trigger MCIT_MFP_BSI
	 * 
	 * @param mvto
	 * @return
	 *  
	 */
	//TODO Alterar nome deste método e avaliar se precisa mesmo ser implementado.
	public void mcitMfpBsi(MciMvtoFatorPredisponentes mvto){
		//TODO ...
	}
	
	protected IControleInfeccaoFacade getControleInfeccaoFacade() {
		return this.controleInfeccaoFacade;
	}
	
}
