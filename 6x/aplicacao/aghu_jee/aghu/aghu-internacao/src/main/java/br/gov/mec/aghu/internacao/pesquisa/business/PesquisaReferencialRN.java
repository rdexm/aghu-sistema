package br.gov.mec.aghu.internacao.pesquisa.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.internacao.dao.AinInternacaoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class PesquisaReferencialRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(PesquisaReferencialRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AinInternacaoDAO ainInternacaoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -4213844622731654755L;

	/**
	 * ORADB Procedure AINK_PES_REF.CONTA_INTERNADOS
	 * 
	 * Conta internações para especialidade por origem para leitos do
	 * referencial.
	 */
	public Long[] contaInternados(Short seqEspecialidade) {
		return getAinInternacaoDAO().contaInternados(seqEspecialidade);
	}

	/**
	 * ORADB Procedure AINK_PES_REF.CONTA_BLOQUEIOS
	 * 
	 * Conta leitos bloqueados em função do paciente do quarto considerando a
	 * especialidade do mesmo.
	 */
	public Long contaBloqueios(Short seqEspecialidade) {
		return getAinInternacaoDAO().contaBloqueios(seqEspecialidade);
	}

	/**
	 * ORADB Procedure AINK_PES_REF.CONTA_CTI
	 * 
	 * Conta internações em CTI considerando também os pacientes extras(sem
	 * leito).
	 */
	public Long[] contaCti(Short seqEspecialidade) {
		return getAinInternacaoDAO().contaCti(seqEspecialidade);
	}

	/**
	 * ORADB Procedure AINK_PES_REF.CONTA_APTOS
	 * 
	 * Conta internações em acomodação apartamentos.
	 */
	public Long[] contaAptos(Short seqEspecialidade) {
		return getAinInternacaoDAO().contaAptos(seqEspecialidade);
	}

	/**
	 * ORADB Procedure AINK_PES_REF.CONTA_OUTRAS_UNIDADES
	 * 
	 * Conta internações em leitos que não fazem parte do referencial, não são
	 * apartamentos e nem cti mas a clínica da internação é a mesma do leito.
	 */
	public Long[] contaOutrasUnidades(Short seqEspecialidade) {
		return getAinInternacaoDAO().contaOutrasUnidades(seqEspecialidade);
	}

	/**
	 * ORADB Procedure AINK_PES_REF.CONTA_OUTRAS_CLINICAS
	 * 
	 * Conta internações para a clínica em questão onde os leitos ocupados são
	 * de clínica diferente, não são apartamentos e nem cti.
	 */
	public Long[] contaOutrasClinicas(Short seqEspecialidade) {
		return getAinInternacaoDAO().contaOutrasClinicas(seqEspecialidade);
	}

	protected AinInternacaoDAO getAinInternacaoDAO() {
		return ainInternacaoDAO;
	}
}
