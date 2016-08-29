package br.gov.mec.aghu.ambulatorio.business;

import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.dao.MamSolicitacaoRetornoDAO;
import br.gov.mec.aghu.dominio.DominioIndPendencia;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MamSolicitacaoRetorno;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * Classe responsável por manter as regras de negócio da entidade MamSolicitacaoRetorno.
 *
 */
@Stateless
public class SolicitacaoRetornoRN extends BaseBusiness {

	private static final long serialVersionUID = -3648045415122945449L;
	
	private static final Log LOG = LogFactory.getLog(SolicitacaoRetornoRN.class);

	private enum SolicitacaoRetornoRNExceptionCode implements BusinessExceptionCode {
		MAM_00576,
		MAM_01142,
		MAM_01135;
	}
	
	@Override
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private MamSolicitacaoRetornoDAO mamSolicitacaoRetornoDAO;
	
	
	/**
	 * Atualiza um registro da entidade MamSolicitacaoRetorno
	 * 
	 * @param elemento - Registro a ser atualizado
	 * @throws ApplicationBusinessException 	
	 */
	public void atualizarSolicitacaoRetorno(MamSolicitacaoRetorno oldMamSolicitacaoRetorno, MamSolicitacaoRetorno newMamSolicitacaoRetorno) throws ApplicationBusinessException {
		verificacaoSolicitacaoRetorno(oldMamSolicitacaoRetorno, newMamSolicitacaoRetorno);
		this.getMamSolicitacaoRetornoDAO().atualizar(newMamSolicitacaoRetorno);
	}

	/** Estória de Usuário #40230 <br/>
	 * ORADB Trigger MAMT_SOR_BRU 
	 * @author marcelo.deus
	 * @throws ApplicationBusinessException 
	 */
	private void verificacaoSolicitacaoRetorno(MamSolicitacaoRetorno oldMamSolicitacaoRetorno, MamSolicitacaoRetorno newMamSolicitacaoRetorno) throws ApplicationBusinessException {
		if (oldMamSolicitacaoRetorno.getIndPendente() != DominioIndPendencia.V.toString()) {
			
			if(modificados(oldMamSolicitacaoRetorno.getAghEquipes(), newMamSolicitacaoRetorno.getAghEquipes()) ||
					modificados(oldMamSolicitacaoRetorno.getIndQtdeMeses(), newMamSolicitacaoRetorno.getIndQtdeMeses()) ||
					modificados(oldMamSolicitacaoRetorno.getQtdeMeses() , newMamSolicitacaoRetorno.getQtdeMeses()) ||
					modificados(oldMamSolicitacaoRetorno.getIndData() , newMamSolicitacaoRetorno.getIndData()) ||
					modificadosDatas(oldMamSolicitacaoRetorno.getDataRetorno() , newMamSolicitacaoRetorno.getDataRetorno()) ||
					modificados(oldMamSolicitacaoRetorno.getIndAposExames() , newMamSolicitacaoRetorno.getIndAposExames())) {
				throw new ApplicationBusinessException(SolicitacaoRetornoRNExceptionCode.MAM_00576);
			}
		}
		
		if (modificados(oldMamSolicitacaoRetorno.getAghEspecialidades() , newMamSolicitacaoRetorno.getAghEspecialidades())) {
			if (newMamSolicitacaoRetorno.getAghEspecialidades().getIndSituacao() == DominioSituacao.I){
				throw new ApplicationBusinessException(SolicitacaoRetornoRNExceptionCode.MAM_01142);
			}
			if (getMamSolicitacaoRetornoDAO().obterEspecialidadeSolicitacaoRetorno(newMamSolicitacaoRetorno) != null) {
				throw new ApplicationBusinessException(SolicitacaoRetornoRNExceptionCode.MAM_01135);
			}
		}
	}
	
	/**
	 * Compara datas sem considerar a hora.
	 * 
	 * @param dataRetorno
	 * @param dataRetorno2
	 * @return
	 */
	private boolean modificadosDatas(Date dataRetorno, Date dataRetorno2) {
		Date d1= DateUtil.truncaData(dataRetorno);
		Date d2= DateUtil.truncaData(dataRetorno2);
		
		return !DateUtil.isDatasIguais(d1, d2);
	}

	/**
	 * A classe dos objetos informados por parâmetro deve implementar o
	 * método equals.
	 * 
	 * @param <T>
	 *            Tipo genérico para indicar que os valores informados por
	 *            parâmetro devem ser do mesmo tipo.
	 * @param newValue
	 *            Valor novo.
	 * @param oldValue
	 *            Valor antigo.
	 * @return Booleano indicando se o valor foi modificado, ou seja, se os
	 *         valores dos parâmetros são diferentes.
	 * @author Marcelo Tocchetto
	 */
	public static <T> Boolean modificados(T newValue, T oldValue) {
		Boolean result = Boolean.FALSE;
	
		if (newValue != null && oldValue != null) {
			result = !newValue.equals(oldValue);
		} else if (newValue != null && oldValue == null) {
			result = Boolean.TRUE;
		} else if (newValue == null && oldValue != null) {
			result = Boolean.TRUE;
		}
	
		return result;
	}

	protected MamSolicitacaoRetornoDAO getMamSolicitacaoRetornoDAO() {
		return mamSolicitacaoRetornoDAO;
	}
	
}