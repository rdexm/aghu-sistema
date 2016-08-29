package br.gov.mec.aghu.paciente.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.prontuario.vo.MatriculaVinculoVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
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
public class McokMcoRN extends BaseBusiness {
	
	private static final Log LOG = LogFactory.getLog(McokMcoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6745746628448627210L;

	private enum McokMcoRNExceptionCode implements BusinessExceptionCode {
		MCO_00003, MCO_00570, MCO_00571
	}	

	public void rnMcopAtuServidor(MatriculaVinculoVO matriculaVinculoVO) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		/* ATUALIZA CARTAO PONTO DO SERVIDOR */
		Short vSerVinCodigo =servidorLogado.getId().getVinCodigo();
		Integer vSerMatricula =servidorLogado.getId().getMatricula();
		
		matriculaVinculoVO.setMatricula(vSerMatricula);
		matriculaVinculoVO.setViniculo(vSerVinCodigo);
		if(matriculaVinculoVO.getMatricula() == null) {
			throw new ApplicationBusinessException(McokMcoRNExceptionCode.MCO_00003);
		}
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
