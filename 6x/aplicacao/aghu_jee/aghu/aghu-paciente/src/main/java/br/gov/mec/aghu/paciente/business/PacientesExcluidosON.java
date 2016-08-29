package br.gov.mec.aghu.paciente.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.VAipPacientesExcluidos;
import br.gov.mec.aghu.model.VAipPacientesExcluidosId;
import br.gov.mec.aghu.paciente.dao.VAipPacientesExcluidosDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável por prover os métodos de negócio genéricos para a entidade
 * de pacientes excluidos.
 * 
 * @author lalegre
 * 
 */
@Stateless
public class PacientesExcluidosON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(PacientesExcluidosON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private VAipPacientesExcluidosDAO vAipPacientesExcluidosDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 4278265495608013644L;

	private enum PacientesExcluidosONExceptionCode implements
			BusinessExceptionCode {

		DATA_INICIAL_MAIOR_DATA_FINAL;

		public void throwException() throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this);
		}
	}

	/**
	 * Obtém os dados que serão utilizados no relatório de mesmo nome.
	 * 
	 * @param dataInicial
	 * @param dataFinal
	 * @return List
	 */
	public List<VAipPacientesExcluidos> pesquisa(Date dataInicial,
			Date dataFinal) {
		List<Object[]> res = this.getVAipPacientesExcluidosDAO().pesquisaPacientesExcluidos(dataInicial, dataFinal);
		
		List<VAipPacientesExcluidos> retorno = new ArrayList<VAipPacientesExcluidos>();

		Iterator<Object[]> it = res.iterator();
		while (it.hasNext()) {
			Object[] obj = it.next();

			VAipPacientesExcluidos view = new VAipPacientesExcluidos();
			VAipPacientesExcluidosId id = new VAipPacientesExcluidosId();
			if (obj[0] != null) {
				id.setProntuarioAtual((Integer) obj[0]);
			}
			if (obj[1] != null) {
				id.setProntuarioAntigo((Integer) obj[1]);
			}
			if (obj[2] != null) {
				id.setNome((String) obj[2]);
			}
			if (obj[3] != null) {
				id.setDtNascimento((Date) obj[3]);
			}
			if (obj[4] != null) {
				id.setDtExclusao((Date) obj[4]);
			}
			if (obj[5] != null) {
				id.setCodigoAtual((BigDecimal) obj[5]);
			}
			
			view.setId(id);
			retorno.add(view);
			
		}

		return retorno;
	}
	
	protected VAipPacientesExcluidosDAO getVAipPacientesExcluidosDAO() {
		return vAipPacientesExcluidosDAO;
	}
	
}
