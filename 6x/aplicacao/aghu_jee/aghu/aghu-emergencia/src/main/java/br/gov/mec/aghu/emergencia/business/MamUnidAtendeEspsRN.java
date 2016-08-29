package br.gov.mec.aghu.emergencia.business;

import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.emergencia.business.MamUnidAtendemRN.MamUnidAtendemRNExceptionCode;
import br.gov.mec.aghu.emergencia.dao.MamUnidAtendeEspDAO;
import br.gov.mec.aghu.emergencia.dao.MamUnidAtendeEspJnDAO;
import br.gov.mec.aghu.emergencia.dao.MamUnidAtendemDAO;
import br.gov.mec.aghu.emergencia.producer.QualificadorUsuario;
import br.gov.mec.aghu.model.MamUnidAtendeEsp;
import br.gov.mec.aghu.model.MamUnidAtendeEspId;
import br.gov.mec.aghu.model.MamUnidAtendeEspJn;
import br.gov.mec.aghu.model.MamUnidAtendem;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.dao.RapServidoresDAO;
import br.gov.mec.aghu.registrocolaborador.vo.Usuario;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseOptimisticLockException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class MamUnidAtendeEspsRN extends BaseBusiness {

	private static final long serialVersionUID = -3638234673604268619L;


	public enum MamUnidAtendeEspsRNExceptionCode implements BusinessExceptionCode {
		
		MENSAGEM_EMERG_ERRO_ESPECIALIDADE_JA_CADASTRADA,
		MENSAGEM_EMERG_ERRO_MAM_02923,
		OPTIMISTIC_LOCK;

	}
	
	@Inject
	private MamUnidAtendemDAO mamUnidAtendemDAO;
	
	@Inject
	private MamUnidAtendeEspDAO mamUnidAtendeEspDAO;
	
	@Inject
	private MamUnidAtendeEspJnDAO mamUnidAtendeEspJnDAO;
	
	@Inject
	private RapServidoresDAO servidorDAO;
	
	@Inject @QualificadorUsuario
	private Usuario usuario;
	
	
	@Override
	protected Log getLogger() {
		// TODO Auto-generated method stub
		return null;
	}

	/***
	 * @ORADB MAM_UNID_ATENDE_ESPS.MAMT_UAE_BRI	
	 * @param mamUnidAtendeEsp
	 * @throws ApplicationBusinessException
	 */
	public void inserirUnidAtendeEsp(MamUnidAtendeEsp mamUnidAtendeEsp, String hostName) throws ApplicationBusinessException {

		try {
			MamUnidAtendeEspId id = new MamUnidAtendeEspId();
			id.setUanUnfSeq(mamUnidAtendeEsp.getMamUnidAtendem().getSeq());
			id.setEspSeq(mamUnidAtendeEsp.getAghEspecialidades().getSeq());

			if (mamUnidAtendeEsp.getMamUnidAtendem().getSeq() == null) {
				throw new ApplicationBusinessException(MamUnidAtendeEspsRNExceptionCode.OPTIMISTIC_LOCK);
			} else {
				MamUnidAtendem mamUnidAtendemOriginal = this.mamUnidAtendemDAO.obterPorChavePrimaria(mamUnidAtendeEsp.getMamUnidAtendem().getSeq());

				if (mamUnidAtendemOriginal == null) {
					throw new ApplicationBusinessException(MamUnidAtendeEspsRNExceptionCode.OPTIMISTIC_LOCK);
				}
			}
			validaDuplicidade(mamUnidAtendeEsp.getMamUnidAtendem().getUnfSeq(), mamUnidAtendeEsp.getAghEspecialidades().getSeq() );
			validaHoraInicioFim(mamUnidAtendeEsp.getHoraInicioMarcaCons(), mamUnidAtendeEsp.getHoraFimMarcaCons());

			mamUnidAtendeEsp.setId(id);
			mamUnidAtendeEsp.setCriadoEm(new Date());
			mamUnidAtendeEsp.setRapServidores(servidorDAO.obter(new RapServidoresId(usuario.getMatricula(),usuario.getVinculo())));
			
			mamUnidAtendeEsp.setMicNome(hostName);

			mamUnidAtendeEspDAO.persistir(mamUnidAtendeEsp);
		} catch (BaseOptimisticLockException e) {
			throw new ApplicationBusinessException(MamUnidAtendemRNExceptionCode.OPTIMISTIC_LOCK);
		}

	}

	/**
	 * @ORADB MAM_UNID_ATENDE_ESPS.MAMT_UAE_BRU
	 * @param mamUnidAtendeEsp
	 * @param hostName
	 * @throws ApplicationBusinessException
	 */
	public void alterarUnidAtendeEsp(MamUnidAtendeEsp mamUnidAtendeEsp, String hostName) throws ApplicationBusinessException {

		try {
			MamUnidAtendeEsp mamUnidAtendeEspOriginal = this.mamUnidAtendeEspDAO.obterPorChavePrimaria(mamUnidAtendeEsp.getId());

			if (mamUnidAtendeEspOriginal.getMamUnidAtendem().getSeq() == null) {
				throw new ApplicationBusinessException(MamUnidAtendeEspsRNExceptionCode.OPTIMISTIC_LOCK);
			} else {
				MamUnidAtendem mamUnidAtendemOriginal = this.mamUnidAtendemDAO.obterPorChavePrimaria(mamUnidAtendeEspOriginal.getMamUnidAtendem().getSeq());

				if (mamUnidAtendemOriginal == null) {
					throw new ApplicationBusinessException(MamUnidAtendeEspsRNExceptionCode.OPTIMISTIC_LOCK);
				}
			}

			validaHoraInicioFim(mamUnidAtendeEsp.getHoraInicioMarcaCons(), mamUnidAtendeEsp.getHoraFimMarcaCons());

			mamUnidAtendeEsp.setRapServidores(servidorDAO.obter(new RapServidoresId(usuario.getMatricula(),usuario.getVinculo())));
			
			
			mamUnidAtendeEsp.setMicNome(hostName);

			if (CoreUtil.modificados(mamUnidAtendeEsp.getIndSituacao(), mamUnidAtendeEspOriginal.getIndSituacao())
					|| CoreUtil.modificados(mamUnidAtendeEsp.getIndMarcaExtra(), mamUnidAtendeEspOriginal.getIndMarcaExtra())
					|| CoreUtil.modificados(mamUnidAtendeEsp.getIndSoMarcaConsDia(), mamUnidAtendeEspOriginal.getIndSoMarcaConsDia())
					|| CoreUtil.modificados(mamUnidAtendeEsp.getMicNome(), mamUnidAtendeEspOriginal.getMicNome())
					|| CoreUtil.modificados(mamUnidAtendeEsp.getRapServidores() , mamUnidAtendeEspOriginal.getRapServidores())
					|| CoreUtil.modificados(mamUnidAtendeEsp.getHoraInicioMarcaCons(), mamUnidAtendeEspOriginal.getHoraInicioMarcaCons())
					|| CoreUtil.modificados(mamUnidAtendeEsp.getHoraFimMarcaCons(), mamUnidAtendeEspOriginal.getHoraFimMarcaCons())) {

				this.inserirJournalUnidAtendeEsp(mamUnidAtendeEspOriginal, DominioOperacoesJournal.UPD);

			}

			this.mamUnidAtendeEspDAO.desatachar(mamUnidAtendeEspOriginal);
			// this.mamUnidAtendeEspDAO.merge(mamUnidAtendeEsp);
			this.mamUnidAtendeEspDAO.atualizar(mamUnidAtendeEsp);
		} catch (BaseOptimisticLockException e) {
			throw new ApplicationBusinessException(MamUnidAtendemRNExceptionCode.OPTIMISTIC_LOCK);
		}

	}
	
	public void validaDuplicidade(Short unfSeq, Short espSeq)
			throws ApplicationBusinessException {
		if (mamUnidAtendeEspDAO.existeUnidAtendeEspPorUnidadeEspecialidade(
				unfSeq, espSeq)) {
			throw new ApplicationBusinessException(
					MamUnidAtendeEspsRNExceptionCode.MENSAGEM_EMERG_ERRO_ESPECIALIDADE_JA_CADASTRADA);
		}
	}
	
	
	public void validaHoraInicioFim(Date horaInicio, Date horaFim) throws ApplicationBusinessException {
		
		if (horaFim.before(horaInicio)){
			 throw new ApplicationBusinessException(MamUnidAtendeEspsRNExceptionCode.MENSAGEM_EMERG_ERRO_MAM_02923);
		}
	}
	
	
	/**
	 * ORADB MAM_UNID_ATENDE_ESPS.MAMT_UAE_ARD
	 * @param mamUnidAtendeEsp	 * 
	 */	 
	public void excluirUnidAtendeEsp(MamUnidAtendeEspId id) {	  	
	  		
		MamUnidAtendeEsp mamUnidAtendeEspOriginal = this.mamUnidAtendeEspDAO.obterPorChavePrimaria(id);  			
		this.inserirJournalUnidAtendeEsp(mamUnidAtendeEspOriginal,DominioOperacoesJournal.UPD);
		this.mamUnidAtendeEspDAO.remover(mamUnidAtendeEspOriginal);		  		
	  		
	 }
	 
	
	/***	 
	 * @param mamUnidAtendeEspJnOriginal
	 * @param operacao
	 */
	
	public void inserirJournalUnidAtendeEsp(MamUnidAtendeEsp mamUnidAtendeEspOriginal, DominioOperacoesJournal operacao) {
		
		  MamUnidAtendeEspJn mamUnidAtendeEspJn = new MamUnidAtendeEspJn();
		
		  mamUnidAtendeEspJn.setNomeUsuario(usuario.getLogin());
		  mamUnidAtendeEspJn.setOperacao(operacao);
		  mamUnidAtendeEspJn.setUanUnfSeq(mamUnidAtendeEspOriginal.getMamUnidAtendem().getUnfSeq());
		  mamUnidAtendeEspJn.setEspSeq(mamUnidAtendeEspOriginal.getAghEspecialidades().getSeq() );
		  mamUnidAtendeEspJn.setCriadoEm(mamUnidAtendeEspOriginal.getCriadoEm());
		  mamUnidAtendeEspJn.setIndSituacao(mamUnidAtendeEspOriginal.getIndSituacao());
		  mamUnidAtendeEspJn.setIndMarcaExtra(mamUnidAtendeEspOriginal.getIndMarcaExtra());
		  mamUnidAtendeEspJn.setMicNome(mamUnidAtendeEspOriginal.getMicNome());
		  mamUnidAtendeEspJn.setSerVinCodigo(mamUnidAtendeEspOriginal.getRapServidores().getId().getVinCodigo());
		  mamUnidAtendeEspJn.setSerMatricula(mamUnidAtendeEspOriginal.getRapServidores().getId().getMatricula());
		  mamUnidAtendeEspJn.setIndSoMarcaConsDia(mamUnidAtendeEspOriginal.getIndSoMarcaConsDia());
		  mamUnidAtendeEspJn.setHoraInicioMarcaCons(mamUnidAtendeEspOriginal.getHoraInicioMarcaCons());
		  mamUnidAtendeEspJn.setHoraFimMarcaCons(mamUnidAtendeEspOriginal.getHoraFimMarcaCons());
			 
		  mamUnidAtendeEspJnDAO.persistir(mamUnidAtendeEspJn);	
		
	} 
	
	

}
