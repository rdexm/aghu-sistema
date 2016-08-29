package br.gov.mec.aghu.ambulatorio.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.dao.MamProcedimentoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamProcedimentoRealizadoDAO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioOperacaoBanco;
import br.gov.mec.aghu.dominio.DominioOrigemPacienteAmbulatorio;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MamProcedimento;
import br.gov.mec.aghu.model.MamProcedimentoRealizado;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Regras de négocio para procedimentos realizados
 * em um atendimento médico.
 * 
 * Implementação das regras de negócio da package Oracle: MAMK_POL_RN
 * e de triggers que utilizam estas regras de negócio.
 * 
 * @author diego.pacheco
 * 
 */
@Stateless
public class ProcedimentoAtendimentoConsultaRN extends BaseBusiness {
	
	@EJB
	private MarcacaoConsultaRN marcacaoConsultaRN;
	
	private static final Log LOG = LogFactory.getLog(ProcedimentoAtendimentoConsultaRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@Inject
	private MamProcedimentoRealizadoDAO mamProcedimentoRealizadoDAO;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private MamProcedimentoDAO mamProcedimentoDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3159234050544655260L;

	public enum ProcedimentoAtendimentoConsultaRNExceptionCode implements
			BusinessExceptionCode {
		
	      MAM_00643, MAM_00644, MAM_00645, MAM_00646
		
	}
	
	public ProcedimentoAtendimentoConsultaRN() {
		
	}
	
	
	/**
	 * ORADB: Trigger MAMT_POL_BRI
	 * 
	 * @param newProcedimentoRealizado
	 */
	public void validarProcedimentoAtendimentoBeforeInsert(MamProcedimentoRealizado newProcedimentoRealizado) 
			throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		final DominioOperacaoBanco operacao = DominioOperacaoBanco.INS; 
		
		// verifica se o procedimento atual já foi validado
		if (newProcedimentoRealizado.getPendente().equals(DominioIndPendenteAmbulatorio.V) ) {
			verificarProcedimentoAtendimentoValidado(operacao);
		}
		
		// verifica se o procedimento que está sendo incluído esta ativo
		verificarProcedimentoAtendimentoAtivo(newProcedimentoRealizado.getProcedimento().getSeq());
		
	     if (newProcedimentoRealizado.getServidor() == null || 
	    		 newProcedimentoRealizado.getServidor().getId().getMatricula() == null) {
	    	 newProcedimentoRealizado.setServidor(servidorLogado);
	     }
	     
	     if (newProcedimentoRealizado.getPaciente() == null  ) {
	    	 if (newProcedimentoRealizado.getConsulta() != null) {
	    		Integer codigoPaciente = getMarcacaoConsultaRN().obterCodigoPacienteOrigem(
	    				DominioOrigemPacienteAmbulatorio.CONSULTA.getCodigo(), 
	    				newProcedimentoRealizado.getConsulta().getNumero());
	    		if (codigoPaciente != null && codigoPaciente > 0) {
	    			AipPacientes paciente = getPacienteFacade().obterAipPacientesPorChavePrimaria(codigoPaciente);
	    			newProcedimentoRealizado.setPaciente(paciente);
	    		}
	    	 }
	     }
	}
	
	/**
	 * ORADB: Trigger MAMT_POL_BRU
	 * 
	 * @param oldProcedimentoRealizado
	 * @param newProcedimentoRealizado
	 */
	public void validarProcedimentoAtendimentoBeforeUpdate(MamProcedimentoRealizado oldProcedimentoRealizado, 
			MamProcedimentoRealizado newProcedimentoRealizado) throws ApplicationBusinessException {
		
		final DominioOperacaoBanco operacao = DominioOperacaoBanco.UPD; 
		
		// verifica se o procedimento atual já foi validado
		if (oldProcedimentoRealizado.getPendente().equals(DominioIndPendenteAmbulatorio.V) 
				|| verificarProcedimentoValidadoAnteriormente(oldProcedimentoRealizado)) {
			if (CoreUtil.modificados(oldProcedimentoRealizado.getProcedimento().getSeq(), 
					newProcedimentoRealizado.getProcedimento().getSeq()) 
				|| CoreUtil.modificados(oldProcedimentoRealizado.getSituacao(), 
					newProcedimentoRealizado.getSituacao())) {
				
				verificarProcedimentoAtendimentoValidado(operacao);		
			}
		}
	}
	
	/**
	 * ORADB: Trigger MAMT_POL_BRD
	 * 
	 * @param oldProcedimentoRealizado
	 */
	public void validarProcedimentoAtendimentoBeforeDelete(MamProcedimentoRealizado oldProcedimentoRealizado) 
			throws ApplicationBusinessException {
		
		final DominioOperacaoBanco operacao = DominioOperacaoBanco.DEL;
		
		// verifica se o procedimento atual já foi validado
		if (oldProcedimentoRealizado.getPendente().equals(DominioIndPendenteAmbulatorio.V) 
				|| verificarProcedimentoValidadoAnteriormente(oldProcedimentoRealizado)) {
			verificarProcedimentoAtendimentoValidado(operacao);	
		}
	}	
	
	/**
	 * ORADB: Procedure RN_POLP_VER_PROC_ATI
	 * 
	 * @param prdSeq
	 * @throws ApplicationBusinessException
	 */
	public void verificarProcedimentoAtendimentoAtivo(Integer prdSeq) throws ApplicationBusinessException {
		MamProcedimento procedimento = getMamProcedimentoDAO().obterPorChavePrimaria(prdSeq);
		if (!procedimento.getSituacao().isAtivo()) {
			throw new ApplicationBusinessException(ProcedimentoAtendimentoConsultaRNExceptionCode.MAM_00643);
		}
	}
	
	/**
	 * ORADB: Procedure RN_POLP_VER_VALIDADO
	 * 
	 * @param operacao
	 * @throws ApplicationBusinessException
	 */
	public void verificarProcedimentoAtendimentoValidado(DominioOperacaoBanco operacao) throws ApplicationBusinessException {
		if (operacao.equals(DominioOperacaoBanco.INS)) {
			throw new ApplicationBusinessException(ProcedimentoAtendimentoConsultaRNExceptionCode.MAM_00644);
		}
		else if (operacao.equals(DominioOperacaoBanco.UPD)) {
			throw new ApplicationBusinessException(ProcedimentoAtendimentoConsultaRNExceptionCode.MAM_00645);
		}
		else if (operacao.equals(DominioOperacaoBanco.DEL)) {
			throw new ApplicationBusinessException(ProcedimentoAtendimentoConsultaRNExceptionCode.MAM_00646);
		}
	}
	

	/**
	 * Atualiza um procedimento realizado
	 * 
	 * @param oldProcedimentoRealizado
	 * @param newProcedimentoRealizado
	 * @throws ApplicationBusinessException
	 */
	public MamProcedimentoRealizado atualizarProcedimentoRealizado(MamProcedimentoRealizado oldProcedimentoRealizado, 
			MamProcedimentoRealizado newProcedimentoRealizado, boolean flush) throws ApplicationBusinessException {
		validarProcedimentoAtendimentoBeforeUpdate(oldProcedimentoRealizado, 
				newProcedimentoRealizado);
		MamProcedimentoRealizado retorno = getMamProcedimentoRealizadoDAO()
				.merge(newProcedimentoRealizado);
		if (flush) {
			getMamProcedimentoRealizadoDAO().flush();
		}
		return retorno;
	}
	
	/**
	 * Insere um procedimento realizado
	 * 
	 * @param newProcedimentoRealizado
	 * @throws ApplicationBusinessException
	 */
	public MamProcedimentoRealizado inserirProcedimentoRealizado(
			MamProcedimentoRealizado newProcedimentoRealizado, boolean flush)
			throws BaseException {
		validarProcedimentoAtendimentoBeforeInsert(newProcedimentoRealizado);
		getMamProcedimentoRealizadoDAO().persistir(newProcedimentoRealizado);
		if (flush) {
			getMamProcedimentoRealizadoDAO().flush();
		}
		return newProcedimentoRealizado;
	}
	
	/**
	 * Remove um procedimento realizado
	 * 
	 * @param oldProcedimentoRealizado
	 * @throws ApplicationBusinessException
	 */
	public void removerProcedimentoRealizado(MamProcedimentoRealizado oldProcedimentoRealizado, boolean flush) 
			throws ApplicationBusinessException {
		validarProcedimentoAtendimentoBeforeDelete(oldProcedimentoRealizado);
		getMamProcedimentoRealizadoDAO().remover(oldProcedimentoRealizado);
		if (flush) {
			getMamProcedimentoRealizadoDAO().flush();
		}
	}	
	
	/**
	 * Retorna true caso o procedimento já tenha
	 * sido validado anteriormente, false caso contrário.
	 * 
	 * ORADB: Function RN_POLC_RET_VAR_PACK
	 * 
	 * @param procedimentoRealizado
	 */
	public Boolean verificarProcedimentoValidadoAnteriormente(MamProcedimentoRealizado oldProcedimentoRealizado) {
		return oldProcedimentoRealizado.getValidado();
	}
	
	protected MamProcedimentoDAO getMamProcedimentoDAO() {
		return mamProcedimentoDAO;
	}
	
	protected MamProcedimentoRealizadoDAO getMamProcedimentoRealizadoDAO() {
		return mamProcedimentoRealizadoDAO;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected IPacienteFacade getPacienteFacade() {
		return this.pacienteFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected MarcacaoConsultaRN getMarcacaoConsultaRN() {
		return marcacaoConsultaRN;
	}	

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
