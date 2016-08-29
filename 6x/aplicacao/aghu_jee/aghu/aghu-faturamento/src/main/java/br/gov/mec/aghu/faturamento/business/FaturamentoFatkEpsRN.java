package br.gov.mec.aghu.faturamento.business;

import java.io.Serializable;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * <p>
 * TODO Implementar metodos <br/>
 * Linhas: 976 <br/>
 * Cursores: 0 <br/>
 * Forks de transacao: 1 <br/>
 * Consultas: 12 tabelas <br/>
 * Alteracoes: 14 tabelas <br/>
 * Metodos: 2 <br/>
 * Metodos externos: 4 <br/>
 * </p>
 * <p>
 * ORADB: <code>FATK_EPS_RN</code>
 * </p>
 * @author gandriotti
 *
 */
@Stateless
public class FaturamentoFatkEpsRN extends BaseBusiness implements Serializable {

	private static final Log LOG = LogFactory.getLog(FaturamentoFatkEpsRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7160846974499003001L;

	public Boolean obterAtributoEpsEncerramento() {
//		Object obj = obterContextoSessao("FATK_EPS_RN_V_EPS_ENCERRAMENTO");
//		if (obj == null) {
			return Boolean.FALSE;
//		}
//		return (Boolean) obj;
	}
}
