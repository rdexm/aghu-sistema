package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcEspecialidadeProcCirgsDAO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MbcEspecialidadeProcCirgs;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class EspecialidadeProcCirgRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(EspecialidadeProcCirgRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcCirurgiasDAO mbcCirurgiasDAO;

	@Inject
	private MbcEspecialidadeProcCirgsDAO mbcEspecialidadeProcCirgsDAO;


	/**
	 * 
	 */
	private static final long serialVersionUID = 3222503847490874528L;

	public enum EspecialidadeProcCirgRNExceptionCode implements BusinessExceptionCode {
		MBC_00258, MBC_00067;
	}

	public void persistir(MbcEspecialidadeProcCirgs especialidade, Boolean inserir) throws BaseException {
		if(inserir) {
			this.inserir(especialidade);
		}
		else {
			this.atualizar(especialidade);
		}
	}

	
	public void inserir(MbcEspecialidadeProcCirgs especialidade) throws BaseException {
		//VALIDACAO FORMS - PK - JA EXISTENTE
		this.validarRegistroJaExistente(especialidade);
		this.preInserir(especialidade);
		this.getMbcEspecialidadeProcCirgsDAO().persistir(especialidade);
	}
	
	public void atualizar(MbcEspecialidadeProcCirgs especialidade) throws BaseException {
		this.preAtualizar(especialidade);
		this.getMbcEspecialidadeProcCirgsDAO().atualizar(especialidade);
	}

	private void validarRegistroJaExistente(MbcEspecialidadeProcCirgs especialidade) throws BaseException {
		if(getMbcEspecialidadeProcCirgsDAO().obterOriginal(especialidade) != null) {
			throw new ApplicationBusinessException(EspecialidadeProcCirgRNExceptionCode.MBC_00067);
		}
	}

	/**
	 * ORADB: MBCT_EPR_BRI
	 * 
	 * @param especialidade
	 * @param servidorLogado
	 */
	private void preInserir(MbcEspecialidadeProcCirgs especialidade) {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		especialidade.setCriadoEm(new Date());
		especialidade.setServidor(servidorLogado);
	}

	
	/**
	 * ORADB: MBCT_EPR_BRU
	 * 
	 * @param especialidade
	 * @param servidorLogado
	 * @throws BaseException
	 */
	private void preAtualizar(MbcEspecialidadeProcCirgs especialidade) throws BaseException {
		MbcEspecialidadeProcCirgs original = getMbcEspecialidadeProcCirgsDAO().obterOriginal(especialidade);
		
		if(CoreUtil.modificados(original.getSituacao(), especialidade.getSituacao()) && DominioSituacao.I.equals(especialidade.getSituacao())) {
			 /*Não é permitido alterar a descrição  deste sinônimo de  procedimento*/
			 this.rnEprpVerSituacao(especialidade.getId().getPciSeq(), especialidade.getId().getEspSeq());
		}
	}
	
	/**
	 * @ORADB MBCK_SNP_RN : RN_EPRP_VER_SITUACAO
	 */
	protected void rnEprpVerSituacao(Integer pciSeq, Short espSeq) throws BaseException {
		  /*Não pode ter cirurgia agendada para o procedimento/especialidade
	      Teti   04/05/00*/
		if(getMbcCirurgiasDAO().verificarSeCirurgiaAgendadaParaEspecialidadeProc(pciSeq, espSeq)) {
			//Inativação inválida. Existe cirurgia agendada para este procedimento
			throw new ApplicationBusinessException(EspecialidadeProcCirgRNExceptionCode.MBC_00258);
		}
	}

	
	private MbcEspecialidadeProcCirgsDAO getMbcEspecialidadeProcCirgsDAO() {
		return mbcEspecialidadeProcCirgsDAO;
	}
	
	private MbcCirurgiasDAO getMbcCirurgiasDAO() {
		return mbcCirurgiasDAO;
	}

}
