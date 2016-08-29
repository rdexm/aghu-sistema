package br.gov.mec.aghu.ambulatorio.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.dao.AacGradeAgendamenConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacProcedHospEspecialidadesDAO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.AacProcedHospEspecialidades;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ConsultasRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ConsultasRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	@Inject
	private AacProcedHospEspecialidadesDAO aacProcedHospEspecialidadesDAO;	
	@EJB
	private IFaturamentoFacade faturamentoFacade;	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;	
	@EJB
	private IAghuFacade aghuFacade;	
	@Inject
	private AacGradeAgendamenConsultasDAO aacGradeAgendamenConsultasDAO;	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2724100926484500460L;

	public enum ConsultasRNExceptionCode implements	BusinessExceptionCode {
		AAC_00070, AAC_00065, AAC_00133, PROCED_NAO_IDENT_CONSULTA;
	}

	public void inserirProcedimentoEspecialidade(AacProcedHospEspecialidades procEsp) throws BaseException {
		preInserirProcedimentoEspecialidade(procEsp);
		aacProcedHospEspecialidadesDAO.persistir(procEsp);
		aposInserirProcedimentoEspecialidade(procEsp);
	}
	
	public void atualizarProcedimentoEspecialidade(AacProcedHospEspecialidades procEsp) throws BaseException {
		preAtualizarProcedimentoEspecialidade(procEsp);
		aacProcedHospEspecialidadesDAO.atualizar(procEsp);
		aposAtualizarProcedimentoEspecialidade(procEsp);
	}

	
	/**
	 * Trigger
	 * ORADB: AACT_PHP_BRU
	 */
	public void preAtualizarProcedimentoEspecialidade(AacProcedHospEspecialidades procEsp) throws BaseException {
		// verifica se procedimento é identificador de consulta
		if(procEsp.getConsulta()) {
			isProcedimentoConsulta(procEsp.getId().getPhiSeq());
		}
	}

	/**
	 * Trigger
	 * ORADB: AACT_PHP_BRI
	 */
	public void preInserirProcedimentoEspecialidade(AacProcedHospEspecialidades procEsp) throws BaseException {
		// verfica proced ativo
		isProcedimentoHospitalarInternoAtivo(procEsp.getId().getPhiSeq());
		// verfica esp ativa
		isEspecialidadeAtiva(procEsp.getId().getEspSeq());
		
		if(procEsp.getConsulta()) {
			isProcedimentoConsulta(procEsp.getId().getPhiSeq());
		}
	}
	
	/**
	 * Trigger
	 * ORADB: AACT_PHP_ASU
	 */
	public void aposAtualizarProcedimentoEspecialidade(AacProcedHospEspecialidades procEsp) throws BaseException {
		enforceProcedimentoEspecialidade(procEsp);
	}

	public Boolean validaGradeAgendamento(AacProcedHospEspecialidades procEsp) throws BaseException {
		if(!procEsp.getConsulta()) {
			Long nroProcCons = getAacProcedHospEspecialidadesDAO().quantidadeProcedHospEspecialidadesConsultaPorEspSeq(procEsp.getId().getEspSeq());
			List<AacProcedHospEspecialidades> lista = getAacProcedHospEspecialidadesDAO().listarProcedimentosEspecialidadeComConsultaPorEspSeq(procEsp.getId().getEspSeq());
			if(!lista.isEmpty() && lista.contains(procEsp)) {
				nroProcCons--;
			}

			if(nroProcCons.equals(0)) {
				Integer count = 0;
				List<AacGradeAgendamenConsultas> consultas = getAacGradeAgendamenConsultasDAO().quantidadeGradeAgendamentosConsultaPorEspSeq(procEsp.getId().getEspSeq());
				for(AacGradeAgendamenConsultas cons : consultas) {
					if(DominioSituacao.A.equals(this.getAmbulatorioFacade().verificarSituacaoGrade(cons.getSeq(), new Date()))) {
						count++;
					}
				}
				if(count > 0) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Trigger
	 * ORADB: AACT_PHP_ARI e AACT_PHP_ASI
	 */
	public void aposInserirProcedimentoEspecialidade(AacProcedHospEspecialidades procEsp) throws BaseException {
		if(!procEsp.getConsulta()) {
			getAmbulatorioFacade().integraProcedimento(procEsp.getId().getPhiSeq());
		}
		enforceProcedimentoEspecialidade(procEsp);
	}
	
	/**
	 * Function
	 * ORADB: AACP_ENFORCE_PHP_RULES
	 */
	public void enforceProcedimentoEspecialidade(AacProcedHospEspecialidades procEsp) throws BaseException {
		// verifica se existe mais de um item com
		// indicador marcado como Consulta
		isEspecialidadeConsultaUnica(procEsp);
	}
	
	/**
	 * Function
	 * ORADB: AACK_AAC_RN.RN_AACP_VER_ESP_ATI
	 * ORADB: AACK_PHP_RN.RN_PHPP_VER_ESP_ATI
	 * Métodos idênticos
	 */
	public void isEspecialidadeAtiva(final Short seq) throws ApplicationBusinessException {
		AghEspecialidades especialidade = getAghuFacade().obterAghEspecialidadesPorChavePrimaria(seq);
		if(especialidade == null || !especialidade.isAtivo()) {
			throw new ApplicationBusinessException(ConsultasRNExceptionCode.AAC_00070);
		}		
	}

	/**
	 * Function
	 * ORADB: AACK_AAC_RN.RN_AACP_VER_PHI_ATI
	 * ORADB: AACK_PHP_RN.RN_PHPP_VER_PROC_ATI
	 * Métodos idênticos
	 */
	public void isProcedimentoHospitalarInternoAtivo(final Integer seq) throws ApplicationBusinessException {
		FatProcedHospInternos procedimento = getFaturamentoFacade().obterProcedimentoHospitalarInterno(seq);
		if(procedimento == null || !DominioSituacao.A.equals(procedimento.getSituacao())) {
			throw new ApplicationBusinessException(ConsultasRNExceptionCode.AAC_00065);
		}		
	}
	
	/**
	 * Function
	 * ORADB: AACK_PHP_RN.RN_PHPP_VER_IDENT_CO
	 */
	public void  isProcedimentoConsulta(final Integer seq) throws BaseException {
		if(getFaturamentoFacade().consultaQuantidadeProcedimentosConsultaPorPhiSeq(seq) <= 0) {
			throw new ApplicationBusinessException(ConsultasRNExceptionCode.PROCED_NAO_IDENT_CONSULTA);
		}
	}

	/**
	 * Function
	 * ORADB: AACK_PHP_RN.RN_PHPP_VER_IND_CONS
	 */
	public void isEspecialidadeConsultaUnica(final AacProcedHospEspecialidades procEsp) throws BaseException {		
		if(procEsp.getConsulta()) {
			Long nroEsp = getAacProcedHospEspecialidadesDAO().quantidadeProcedHospEspecialidadesConsultaPorEspSeq(procEsp.getId().getEspSeq());
			List<AacProcedHospEspecialidades> lista = getAacProcedHospEspecialidadesDAO().listarProcedimentosEspecialidadeComConsultaPorEspSeq(procEsp.getId().getEspSeq());
			if(!lista.isEmpty() && lista.contains(procEsp)) {
				return;
			}
			else {
				nroEsp++;
			}
			if(nroEsp > 1) {
		        // Somente um Item_especialidade pode  ter o indicador
		        // Consulta
				throw new ApplicationBusinessException(ConsultasRNExceptionCode.AAC_00133);
			}
		}
	}
	
	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return this.ambulatorioFacade;
	}

	protected AacProcedHospEspecialidadesDAO getAacProcedHospEspecialidadesDAO() {
		return aacProcedHospEspecialidadesDAO;
	}

	protected AacGradeAgendamenConsultasDAO getAacGradeAgendamenConsultasDAO() {
		return aacGradeAgendamenConsultasDAO;
	}
}
