package br.gov.mec.aghu.internacao.pesquisa.business;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.internacao.dao.AinEscalasProfissionalIntDAO;
import br.gov.mec.aghu.internacao.dao.AinInternacaoDAO;
import br.gov.mec.aghu.internacao.vo.EspCrmVO;
import br.gov.mec.aghu.internacao.vo.PacienteProfissionalEspecialidadeVO;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AinEscalasProfissionalInt;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class PesquisaPacientesProfissionalEspecialidadeON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(PesquisaPacientesProfissionalEspecialidadeON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AinInternacaoDAO ainInternacaoDAO;

@EJB
private IRegistroColaboradorFacade registroColaboradorFacade;

@EJB
private IAghuFacade aghuFacade;

@Inject
private AinEscalasProfissionalIntDAO ainEscalasProfissionalIntDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -5675185120745925555L;

	/**
	 * 
	 * Busca especialidades por Nome ou Sigla
	 * 
	 * @return Lista de especialidades
	 */
	@Secure("#{s:hasPermission('especialidade','pesquisar')}")
	public List<AghEspecialidades> pesquisarEspecialidadePorSiglaNome(Object strPesquisa) {
		return getAghuFacade().pesquisarEspecialidadePorSiglaNome(strPesquisa);
	}

	@Secure("#{s:hasPermission('especialidade','pesquisar')}")
	public Long pesquisarEspecialidadePorSiglaNomeCount(Object strPesquisa) {
		return getAghuFacade().pesquisarEspecialidadePorSiglaNomeCount(strPesquisa);
	}
	
	public Integer pesquisaPacientesProfissionalEspecialidadeCount(AghEspecialidades especialidade, EspCrmVO profissional)
			throws ApplicationBusinessException {
		return getAinInternacaoDAO().pesquisaPacientesProfissionalEspecialidadeCount(especialidade, profissional);
	}

	@Secure("#{s:hasPermission('internacao','pesquisar')}")
	public List<PacienteProfissionalEspecialidadeVO> pesquisaPacientesProfissionalEspecialidade(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc, AghEspecialidades especialidade, EspCrmVO profissional)
			throws ApplicationBusinessException {
		List<Object[]> resultList = getAinInternacaoDAO().pesquisaPacientesProfissionalEspecialidade(firstResult, maxResults,
				orderProperty, asc, especialidade, profissional);
		List<PacienteProfissionalEspecialidadeVO> pacientes = new ArrayList<PacienteProfissionalEspecialidadeVO>(0);

		for (Object[] object : resultList) {
			PacienteProfissionalEspecialidadeVO vo = new PacienteProfissionalEspecialidadeVO();
			if (object[0] != null) {
				// Formata numero do prontuário inserindo a /
				String prontAux = object[0].toString();
				prontAux = prontAux.substring(0, prontAux.length() - 1) + "/" + prontAux.charAt(prontAux.length() - 1);
				vo.setProntuario(StringUtils.leftPad(prontAux, 9, '0'));
			}
			if (object[1] != null) {
				vo.setNomePaciente(object[1].toString());
			}
			if (object[2] != null) {
				Timestamp timeStamp = (Timestamp) object[2];
				vo.setInternacao(new Date(timeStamp.getTime()));
			}
			if (object[3] != null) {
				vo.setNomeMedico(object[3].toString());
			}
			if (object[4] != null) {
				vo.setLeito(object[4].toString());
			}
			if (object[5] != null) {
				vo.setQuarto(object[5].toString());
			}
			if (object[6] != null) {
				vo.setUnidadeFuncional(object[6].toString());
			}
			if (object[7] != null) {
				vo.setClinica(object[7].toString());
			}
			pacientes.add(vo);
		}

		return pacientes;
	}

	@Secure("#{s:hasPermission('colaborador','pesquisar')}")
	public List<EspCrmVO> pesquisaProfissionalEspecialidade(AghEspecialidades especialidade, String strPesquisa)
			throws ApplicationBusinessException {
		return getRegistroColaboradorFacade().pesquisaProfissionalEspecialidade(especialidade, strPesquisa);
	}

	@Secure("#{s:hasPermission('colaborador','pesquisar')}")
	public EspCrmVO pesquisarProfissionalPorEspecialidadeCRM(AghEspecialidades especialidade, String strPesquisa)
			throws ApplicationBusinessException {

		List<EspCrmVO> profissionais = pesquisaProfissionalEspecialidade(especialidade, strPesquisa);
		EspCrmVO profissional = profissionais.isEmpty() ? null : profissionais.get(0);

		return profissional;
	}

	/**
	 * Método para pesquisar escalar de profissionais.
	 * 
	 * @param matriculaProfessor
	 * @param codigoProfessor
	 * @param seqEspecialidade
	 * @param codigoConvenioSaude
	 * @return
	 */
	@Secure("#{s:hasPermission('escalaProfissionais','pesquisar')}")
	public List<AinEscalasProfissionalInt> pesquisarEscalaProfissionalInt(Integer matriculaProfessor, Short codigoProfessor,
			Short seqEspecialidade, Short codigoConvenioSaude) {
		return getAinEscalasProfissionalIntDAO().pesquisarEscalaProfissionalInt(matriculaProfessor, codigoProfessor, seqEspecialidade,
				codigoConvenioSaude);
	}

	protected AinInternacaoDAO getAinInternacaoDAO() {
		return ainInternacaoDAO;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected AinEscalasProfissionalIntDAO getAinEscalasProfissionalIntDAO() {
		return ainEscalasProfissionalIntDAO;
	}
	
}
