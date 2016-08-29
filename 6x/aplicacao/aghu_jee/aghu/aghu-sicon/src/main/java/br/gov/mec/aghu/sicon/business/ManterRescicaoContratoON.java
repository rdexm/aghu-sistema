package br.gov.mec.aghu.sicon.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacaoEnvioContrato;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoContrato;
import br.gov.mec.aghu.model.ScoResContrato;
import br.gov.mec.aghu.model.ScoResContratoJn;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.sicon.dao.ScoResContratoDAO;
import br.gov.mec.aghu.sicon.dao.ScoResContratoJnDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

/**
 * The Class ManterRescicaoContratoON.
 */
@Stateless
public class ManterRescicaoContratoON extends BaseBusiness {
	
	private static final Log LOG = LogFactory.getLog(ManterRescicaoContratoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private ScoResContratoDAO scoResContratoDAO;
	
	@Inject
	private ScoResContratoJnDAO scoResContratoJnDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5621669966495042710L;

	private enum ManterRescicaoContratoONExceptionCode implements
	BusinessExceptionCode { 
		JUSTIFICATIVA_OBRIGATORIO, TIPO_RESCISAO_OBRIGATORIO, RESCISAO_JA_ENVIADA;
	}
	
	/**
	 * Gets the rescicao contrato.
	 *
	 * @param input the input
	 * @return the rescicao contrato
	 * @throws BaseException the mEC base exception
	 */
	public ScoResContrato getRescicaoContrato(ScoContrato input)throws BaseException{
		return getResContratoDAO().getRescicaoByContrato(input);
	}
	
	/**
	 * Persiste rescicao contrato.
	 *
	 * @param input the input
	 * @return the sco res contrato
	 * @throws ApplicationBusinessException 
	 * @throws BaseException the mEC base exception
	 */
	public ScoResContrato persisteRescicaoContrato(final ScoResContrato resContrato)throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		ScoResContratoDAO scoResContratoDAO = this.getResContratoDAO();
		ScoResContrato input = resContrato;
		
		propriedadesObrigatoriasPreenchidas(input);
        if (isAtualizacao(input)){
        	//RN01
        	//Conforme orientado pela Deise Moura, updates devem ser permitidos em caso de atualizacao de campo OBSERVACAO
        	//if(input.getContrato().getSituacao() == DominioSituacaoEnvioContrato.E)
        	//	throw new ApplicationBusinessException(ManterRescicaoContratoONExceptionCode.RESCISAO_JA_ENVIADA);
        	//else{
        		//RN02
        		input.setAlteradoEm(new Date());
        		input.setServidor(servidorLogado);
        		atualizarRescisaoContratoJn(input);
        		
        		input = scoResContratoDAO.merge(input);
        		
        		return input;
        	//}
        }
        else{
            Date d =new Date();
    		//RN02
            input.setCriadoEm(d);        	
            input.setIndSituacao(DominioSituacaoEnvioContrato.A);
            input.setServidor(servidorLogado);            
            
            scoResContratoDAO.persistir(input);
            scoResContratoDAO.flush();
            
            ScoResContrato rescisao = input;
            inserirRescisaoContratoJn(input);
            return rescisao;
        }          
    }
	
	private void inserirRescisaoContratoJn(ScoResContrato rescisao){	
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		ScoResContratoJn rescisaoJn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.INS, ScoResContratoJn.class, servidorLogado.getUsuario());
	    
		preencherRescisaoContratoJn(rescisaoJn, rescisao);
		
		getScoResContratoJnDAO().persistir(rescisaoJn);
		getScoResContratoJnDAO().flush();
	}
	
	private void atualizarRescisaoContratoJn(ScoResContrato rescisao){
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		Integer seqRescisaoOriginal = rescisao.getSeq();		
		this.getResContratoDAO().desatachar(rescisao);		
		ScoResContrato rescisaoOriginal = this.getResContratoDAO().obterPorChavePrimaria(seqRescisaoOriginal);

		ScoResContratoJn rescisaoJn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, ScoResContratoJn.class, servidorLogado.getUsuario());
		
		preencherRescisaoContratoJn(rescisaoJn, rescisaoOriginal);
		
		getScoResContratoJnDAO().persistir(rescisaoJn);
		getScoResContratoJnDAO().flush();
	}
	
	private void preencherRescisaoContratoJn(ScoResContratoJn rescisaoJn, ScoResContrato rescisao){
		rescisaoJn.setSeq(rescisao.getSeq());
		rescisaoJn.setScoContrato(rescisao.getContrato());
		rescisaoJn.setJustificativa(rescisao.getJustificativa());
		rescisaoJn.setIndTipoRescisao(rescisao.getIndTipoRescisao());
		rescisaoJn.setDtAssinatura(rescisao.getDtAssinatura());
		rescisaoJn.setDtPublicacao(rescisao.getDtPublicacao());
		rescisaoJn.setIndSituacao(rescisao.getIndSituacao());
	}
	

	/**
	 * Checks if is atualizacao.
	 *
	 * @param input the input
	 * @return true, if is atualizacao
	 */
	private boolean isAtualizacao(ScoResContrato input){
		return (input.getSeq()==null) ? false : true;
	}

	
    /**
     * Propriedades obrigatorias preenchidas.
     *
     * @param input the input
     */
    private void propriedadesObrigatoriasPreenchidas(ScoResContrato input) throws ApplicationBusinessException {
    	if(input.getJustificativa()==null || input.getJustificativa().trim().equals("")) {
    		throw new ApplicationBusinessException(ManterRescicaoContratoONExceptionCode.JUSTIFICATIVA_OBRIGATORIO);
    	}
    	
    	if(input.getIndTipoRescisao()==null || input.getIndTipoRescisao().toString().equals("")) {
    		throw new ApplicationBusinessException(ManterRescicaoContratoONExceptionCode.TIPO_RESCISAO_OBRIGATORIO);
    	}
    }
	
	/**
	 * Gets the dao.
	 *
	 * @return the dao
	 */
	protected ScoResContratoDAO getResContratoDAO() {		
		return scoResContratoDAO;
	}
	
	protected ScoResContratoJnDAO getScoResContratoJnDAO(){
		return scoResContratoJnDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}