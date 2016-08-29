package br.gov.mec.aghu.blococirurgico.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.cadastro.business.ICadastroPacienteFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * ORADB PROCEDURE RN_CRGP_DEL_PACIENTE
 * 
 * @author aghu
 * 
 */
@Stateless
public class CirurgiaRemoverPacienteRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(CirurgiaRemoverPacienteRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	


	@EJB
	private ICadastroPacienteFacade iCadastroPacienteFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -3078078076266680391L;

	/**
	 * Remove paciente em gerar p
	 * 
	 * @param pacienteAntigo
	 * @param pacienteNovo
	 * @param servidorLogado
	 * @throws BaseException
	 */
	public void removerPaciente(AipPacientes pacienteAntigo, AipPacientes pacienteNovo) throws BaseException {
		// Aqui também foi necessário testar se o paciente antigo existia
		/* FR if (pacienteAntigo != null && !CoreUtil.igual(pacienteAntigo, pacienteNovo)) {
			if (DominioSimNao.N.equals(pacienteAntigo.getCadConfirmado()) && pacienteAntigo.getProntuario() == null) {
				this.getCadastroPacienteFacade().excluirPaciente(pacienteAntigo, servidorLogado.getUsuario());
			}

		}*/

	}

	/*
	 * Getters Facades, RNs e DAOs
	 */

	protected ICadastroPacienteFacade getCadastroPacienteFacade() {
		return this.iCadastroPacienteFacade;
	}

}
