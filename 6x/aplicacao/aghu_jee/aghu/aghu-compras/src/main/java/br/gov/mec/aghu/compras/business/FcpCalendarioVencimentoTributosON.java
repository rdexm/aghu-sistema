package br.gov.mec.aghu.compras.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.vo.FcpCalendarioVencimentoTributosVO;
import br.gov.mec.aghu.dominio.DominioTipoTributo;
import br.gov.mec.aghu.model.FcpCalendarioVencimentoTributos;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class FcpCalendarioVencimentoTributosON extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5543037090454483485L;

	/**
	 * 
	 */

	private static final Log LOG = LogFactory.getLog(FcpCalendarioVencimentoTributosON.class);
	
	/**
	 * Objeto com as regras de negócio da rotina de calendário vencimento tributo
	 * */
	@EJB
	private FcpCalendarioVencimentoTributosRN fcpCalendarioVencimentoTributosRN;
	

	/**
	 * 
	 */
	public FcpCalendarioVencimentoTributos pesquisarFcpCalendarioVencimentoTributoPorCodigo(Integer numeroCalendarioVencimento){
		return fcpCalendarioVencimentoTributosRN.pesquisarFcpCalendarioVencimentoTributoPorCodigo(numeroCalendarioVencimento); 
	}
	
	/**
	 * Métoque que recebe o objeto com os parâmetros de pesquisa e chama o método on correspondente a pesquisa de calendários de vencimentos
	 * 
	 * @param inicioApuracao Data de início de apuração dos calendários
	 * @param fimApuracao    Data do fim da apuração dos calendários
	 * @param tipoTributo    Tipo do tributo do calendárdio(s)
	 * @return Lista com os calendários de vencimentos de tributo
	 * */
	public List<FcpCalendarioVencimentoTributosVO> pesquisarFcpCalendarioVencimentoTributo( Date dataApuracao, DominioTipoTributo tipoTributo ) {
		return fcpCalendarioVencimentoTributosRN.pesquisarFcpCalendarioVencimentoTributo(dataApuracao, tipoTributo);
	}

	/**
	 * Insere um registro na tabela da entidade
	 * Lança exceção se os períodos da apuração se sobrepõem
	 * 
	 * @param inicioApuracao
	 * @param fimApuracao
	 * @param tipoTributo
	 * @return
	 * @throws ApplicationBusinessException 
	 */	
	public FcpCalendarioVencimentoTributos persistirCalendarioVencimento( FcpCalendarioVencimentoTributos fcpCalendarioVencimento )
			throws ApplicationBusinessException {
		return fcpCalendarioVencimentoTributosRN.persistirCalendarioVencimento(fcpCalendarioVencimento);
	}
	
	/**
	 * Método para remover um registro de um calendário de vencimento do tributo.
	 * @param calendarioVencimentoTributo Calendário do vencimento do tributo a ser removido
	 * */
	public void remover(FcpCalendarioVencimentoTributos calendarioVencimentoTributo) {
		fcpCalendarioVencimentoTributosRN.remover(calendarioVencimentoTributo);
	}
	
	@Override
	protected Log getLogger() {
		return FcpCalendarioVencimentoTributosON.LOG;
	}

}
