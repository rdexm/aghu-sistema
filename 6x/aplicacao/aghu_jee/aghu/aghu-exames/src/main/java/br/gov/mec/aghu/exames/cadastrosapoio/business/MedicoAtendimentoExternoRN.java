package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.exames.dao.AghAtendimentosPacExternDAO;
import br.gov.mec.aghu.exames.dao.AghMedicoExternoDAO;
import br.gov.mec.aghu.exames.dao.AghMedicoMatriculaConveniosDAO;
import br.gov.mec.aghu.model.AghMedicoExterno;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class MedicoAtendimentoExternoRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MedicoAtendimentoExternoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AghMedicoMatriculaConveniosDAO aghMedicoMatriculaConveniosDAO;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private AghMedicoExternoDAO aghMedicoExternoDAO;
	
	@Inject
	private AghAtendimentosPacExternDAO aghAtendimentosPacExternDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -8058855735355590734L;

	public enum MedicoAtendimentoExternoRNExceptionCode implements BusinessExceptionCode {
		CRM_INVALIDO, //Numero de CRM Invalido.
		CPF_INVALIDO_MEDICO_EXTERNO, // CPF Inválido
		APE_MEX_FK1, //CONSTRAINT da tabela agh_atendimentos_pac_extern.
		MMC_MEX_FK1, //CONSTRAINT da tabela agh_medico_matricula_convenios.
		CRM_JA_CADASTRADO;
		
		public void throwException(Object... params) throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}
	}
	
		
	public void inserir(AghMedicoExterno elemento) throws BaseException {
		this.preInserir(elemento);
		this.getAghMedicoExternoDAO().persistir(elemento);
	}
	
	public void atualizar(AghMedicoExterno elemento) throws ApplicationBusinessException, BaseException {	
		// Devido a existência de médicos cadastrados mais de uma vez com mesmo CRM
		// optou-se por validar CRM único apenas na inclusao de novos médicos, permitindo
		// a edição dos outros dados de médicos já cadastrados
		//validaCRMUnico(elemento);
		
		if(elemento.getCpf() != null) {
			if (!CoreUtil.validarCPF(elemento.getCpf().toString())) {
				throw new ApplicationBusinessException(MedicoAtendimentoExternoRNExceptionCode.CPF_INVALIDO_MEDICO_EXTERNO);
			}
		}
		this.getAghMedicoExternoDAO().merge(elemento);
	}

	public void validaCRMUnico(AghMedicoExterno elemento) throws BaseException{
		if (elemento.getCrm() != null && aghMedicoExternoDAO.verificarExistenciaCRM(elemento)){
			throw new ApplicationBusinessException(MedicoAtendimentoExternoRNExceptionCode.CRM_JA_CADASTRADO);
		}
	}
	
	public void remover(Integer seqExclusao) throws ApplicationBusinessException, BaseException {
		AghMedicoExterno medicoExterno = this.getAghMedicoExternoDAO().obterPorChavePrimaria(seqExclusao);
		
		if (medicoExterno == null) {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}
		
		this.preRemover(medicoExterno);
		this.getAghMedicoExternoDAO().remover(medicoExterno);
	}
	
	
	protected void preInserir(AghMedicoExterno elemento) throws BaseException {
		validaCRMUnico(elemento);
		
		if(elemento.getCpf() != null) {
			if (!CoreUtil.validarCPF(elemento.getCpf().toString())) {
				throw new ApplicationBusinessException(MedicoAtendimentoExternoRNExceptionCode.CPF_INVALIDO_MEDICO_EXTERNO);
			}
		}
		
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		elemento.setServidor(servidorLogado);
		elemento.setCriadoEm(new Date());
	}
	
	
	protected void preRemover(AghMedicoExterno medicoExterno) throws BaseException {
		BaseListException erros = new BaseListException();
		
		erros.add(verificaConstraintMatriculaConvenioMedico(medicoExterno));
		erros.add(verificaConstraintPacientesExternos(medicoExterno));
		
		if (erros.hasException()) {
			throw erros;
		}
	}
	
	/**
	 * @ORADB Constraint agh_ape_mex_fk1 de AghAtendimentosPacExtern
	 */
	protected ApplicationBusinessException verificaConstraintPacientesExternos(AghMedicoExterno medicoExterno) {
		ApplicationBusinessException returnException = null;
		
		//verificar se o registro a ser removido em questao se nao tem referencia em AghAtendimentosPacExtern
		Long countPacExt = 
			this.getAghAtendimentosPacExternDAO().countPacientesExternosPorMedicoExterno(medicoExterno.getSeq());
			//this.getAghuFacade().countPacientesExternosPorMedicoExterno(medicoExterno.getSeq());
		
		
		if(countPacExt > 0) {
			returnException = new ApplicationBusinessException(MedicoAtendimentoExternoRNExceptionCode.APE_MEX_FK1);
		}
		
		return returnException;
	}
	
	protected AghAtendimentosPacExternDAO getAghAtendimentosPacExternDAO() {
		return aghAtendimentosPacExternDAO;
	}
	
	/**
	 * @ORADB Constraint agh_mmc_mex_fk1 de AghMedicoMatriculaConvenios
	 */
	protected ApplicationBusinessException verificaConstraintMatriculaConvenioMedico(AghMedicoExterno medicoExterno) {
		ApplicationBusinessException returnException = null;
		
		//verificar se o registro a ser removido em questao se nao tem referencia em AghMedicoMatriculaConvenios
		Long countMedMatCnv = this.getAghMedicoMatriculaConveniosDAO().countMatriculaConvenioPorMedicoExterno(medicoExterno.getSeq());
				
		if(countMedMatCnv > 0) {
			returnException = new ApplicationBusinessException(MedicoAtendimentoExternoRNExceptionCode.MMC_MEX_FK1);
		}
	
		return returnException;
	}
	
	
	//getters
	protected AghMedicoExternoDAO getAghMedicoExternoDAO() {
		return aghMedicoExternoDAO;
	}
		
	protected AghMedicoMatriculaConveniosDAO getAghMedicoMatriculaConveniosDAO() {
		return aghMedicoMatriculaConveniosDAO;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
}