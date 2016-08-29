package br.gov.mec.aghu.internacao.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * 
 * @author lalegre
 * 
 */
@Stateless
public class ProcedenciaPacientesInternadosRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ProcedenciaPacientesInternadosRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IPacienteFacade pacienteFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -6651793581989731536L;

	/**
	 * ORADB function aipc_get_city_pac
	 * 
	 * @param pacCodigo
	 * @return
	 */
	public String recuperarCidadePaciente(Integer pacCodigo) {

		Short seqp = recuperarEnderecoCorrespondencia(pacCodigo);
		String cidade = pesquisarCidadeCursor1(pacCodigo, seqp);

		if (cidade == null) {
			cidade = pesquisarCidadeCursor2(pacCodigo, seqp);
			if (cidade == null) {
				cidade = pesquisarCidadeCursor3(pacCodigo, seqp);
				if (cidade == null) {
					return "";
				}
			}
		}

		return cidade;
	}

	/**
	 * ORADB FUNCTION AIPC_ENDERECO_CORRESPONDENCIA
	 * @param pacCodigo
	 * @return
	 */
	public Short recuperarEnderecoCorrespondencia(Integer pacCodigo) {
		Short seqp = getPacienteFacade().obterSeqEnderecoPadraoPaciente(pacCodigo);
		if (seqp == null) {
			seqp = getPacienteFacade().obterSeqEnderecoResidencialPaciente(pacCodigo);
		}

		return seqp;

	}

	/**
	 * Cursor 1 da function aipc_get_city_pac
	 * 
	 * @param pacCodigo
	 * @return
	 */
	private String pesquisarCidadeCursor1(Integer pacCodigo, Short seqp) {
		return getPacienteFacade().pesquisarCidadeCursor1(pacCodigo, seqp);
	}

	/**
	 * Cursor 2 da function aipc_get_city_pac
	 * 
	 * @param pacCodigo
	 * @return
	 */
	private String pesquisarCidadeCursor2(Integer pacCodigo, Short seqp) {
		return getPacienteFacade().pesquisarCidadeCursor2(pacCodigo, seqp);
	}

	/**
	 * Cursor 3 da function aipc_get_city_pac
	 * 
	 * @param pacCodigo
	 * @return
	 */
	private String pesquisarCidadeCursor3(Integer pacCodigo, Short seqp) {
		return getPacienteFacade().pesquisarCidadeCursor3(pacCodigo, seqp);
	}
	
	protected IPacienteFacade getPacienteFacade(){
		return pacienteFacade;
	}
}
