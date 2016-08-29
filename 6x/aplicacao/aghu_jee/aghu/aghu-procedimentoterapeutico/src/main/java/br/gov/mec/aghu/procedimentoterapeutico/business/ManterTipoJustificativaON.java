package br.gov.mec.aghu.procedimentoterapeutico.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import br.gov.mec.aghu.model.MptJustificativa;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptBloqueioDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptExtratoSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptJustificativaDAO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ManterTipoJustificativaON extends BaseBusiness {



	private static final long serialVersionUID = 5619526016150274770L;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	private static final Log LOG = LogFactory.getLog(ManterTipoJustificativaON.class);

	@Inject
	private MptJustificativaDAO mptJustificativaDAO;
	
	@Inject
	private MptBloqueioDAO mptBloqueioDAO;
	
	@Inject
	private MptExtratoSessaoDAO mptExtratoSessaoDAO;
		
	public enum ManterTipoJustificativaONException implements BusinessExceptionCode {
		REGISTRO_JUSTIFICATIVA_JA_EXISTENTE, POSSUI_VINCULO_BLOQUEIO, POSSUI_VINCULO_EXTRATO;
	}
	
	@Deprecated
	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	
	
	public void adicionarJustificativa(MptJustificativa mptJustificativa) throws ApplicationBusinessException{
		 MptJustificativa entity = this.mptJustificativaDAO.obterMptJustificativa(mptJustificativa);
		if((mptJustificativa != null)&&(entity == null)){
			mptJustificativa.setServidor(this.servidorLogadoFacade.obterServidorLogado());
			mptJustificativa.setCriadoEm(new Date());
			this.mptJustificativaDAO.persistir(mptJustificativa);
		}else{
			throw new ApplicationBusinessException(ManterTipoJustificativaONException.REGISTRO_JUSTIFICATIVA_JA_EXISTENTE);
			}
	}
	
	
	public void editarJustificativa(MptJustificativa mptJustificativa) throws ApplicationBusinessException{
		if(mptJustificativa != null){
			
			MptJustificativa entity = this.mptJustificativaDAO.obterPorChavePrimaria(mptJustificativa.getSeq());
			entity.setDescricao(mptJustificativa.getDescricao());
			entity.setIndSituacao(mptJustificativa.getIndSituacao());
			entity.setMptTipoJustificativa(mptJustificativa.getMptTipoJustificativa());
			entity.setMptTipoSessao(mptJustificativa.getMptTipoSessao());
			this.mptJustificativaDAO.atualizar(entity);
		}else{
			throw new ApplicationBusinessException(ManterTipoJustificativaONException.REGISTRO_JUSTIFICATIVA_JA_EXISTENTE);
			}
	}
	
	
	
	public void excluirJustificativa(MptJustificativa mptJustificativa) throws ApplicationBusinessException{
		MptJustificativa entity = this.mptJustificativaDAO.obterPorChavePrimaria(mptJustificativa.getSeq());
		if(entity != null){
			if(this.mptBloqueioDAO.existeVinculoBloqueio(entity)){
				throw new ApplicationBusinessException(ManterTipoJustificativaONException.POSSUI_VINCULO_BLOQUEIO);
			}else if(this.mptExtratoSessaoDAO.existeVinculoExtratoSessao(entity)){
				throw new ApplicationBusinessException(ManterTipoJustificativaONException.POSSUI_VINCULO_EXTRATO);
			}
			else{
				this.mptJustificativaDAO.remover(entity);
				this.mptJustificativaDAO.flush();
			}
		}
	
	}
	
	
	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}


}
