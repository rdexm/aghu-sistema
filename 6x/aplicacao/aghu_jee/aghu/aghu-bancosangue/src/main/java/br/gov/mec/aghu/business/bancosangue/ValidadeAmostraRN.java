package br.gov.mec.aghu.business.bancosangue;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.bancosangue.dao.ValidadeAmostraDAO;
import br.gov.mec.aghu.bancosangue.dao.ValidadeAmostraJnDAO;
import br.gov.mec.aghu.model.AbsValidAmostraComponentesJn;
import br.gov.mec.aghu.model.AbsValidAmostrasComponentes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;


@Stateless
public class ValidadeAmostraRN extends BaseBusiness {
	
	
	private static final Log LOG = LogFactory.getLog(ValidadeAmostraRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private ValidadeAmostraDAO validadeAmostraDAO;
	
	@Inject
	private ValidadeAmostraJnDAO validadeAmostraJnDAO;

	private static final long serialVersionUID = -2556912541807614067L;

	public void persistir(final AbsValidAmostrasComponentes amostraComp) throws ApplicationBusinessException{
		if(isEdicao(amostraComp)){
			atualizaValidAmostrasComponentes(amostraComp);
		}else{
			insereValidAmostrasComponentes(amostraComp);
		}
	}
	
	private void insereValidAmostrasComponentes(final AbsValidAmostrasComponentes amostraComp) throws ApplicationBusinessException {
		preInsert(amostraComp);
		getValidadeAmostraDAO().persistir(amostraComp);
		getValidadeAmostraDAO().flush();
		
	}
	
	private void atualizaValidAmostrasComponentes(final AbsValidAmostrasComponentes amostraComp) throws ApplicationBusinessException {
		AbsValidAmostrasComponentes original = getValidadeAmostraDAO().obterOriginal(amostraComp);
		preAtualiza(amostraComp);
		getValidadeAmostraDAO().merge(amostraComp);
		getValidadeAmostraDAO().flush();
		posAtualizar(amostraComp, original);	
	}
	

	/**
	 * ORADB:ABST_VAC_BRI (PRE-INSERT) 
	 * @param AbsValidAmostrasComponentes
	 * @author fausto.trindade@squadra.com.br
	 * RN1: Seta ser_vin_codigo e ser_matricula de acordo com o usuário logado.
	 * RN2: Seta CRIADO_EM e ALTERADO_EM com a data atual.
	 */
	private void preInsert(final AbsValidAmostrasComponentes amostraComp)throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		amostraComp.setSerVinCodigo(Integer.valueOf(servidorLogado.getId().getVinCodigo()));
		amostraComp.setSerMatricula(servidorLogado.getId().getMatricula());
		amostraComp.setCriadoEm(new Date());
		Long nid = getValidadeAmostraDAO().obterProximoCodigoSeqp(amostraComp.getId().getCsaCodigo());
		amostraComp.getId().setSeqp(nid.intValue());
	}

	/**
	 * ORADB:ABST_VAC_BRU (PRE-UPDATE) 
	 * @param AbsValidAmostrasComponentes
	 * @author fausto.trindade@squadra.com.br
	 * RN1: Seta ser_vin_codigo e ser_matricula de acordo com o usuário logado.
	 */
	private void preAtualiza(final AbsValidAmostrasComponentes amostraComp) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		amostraComp.setSerVinCodigo(Integer.valueOf(servidorLogado.getId().getVinCodigo()));
		amostraComp.setSerMatricula(servidorLogado.getId().getMatricula());
	}
	

	/**
	 * ORADB:ABST_VAC_ARU (POS-UPDATE) 
	 * @param AbsValidAmostrasComponentes
	 * @author fausto.trindade@squadra.com.br
	 * RN1: Se foi alterado algum campo, realizar insert em abs_valid_amostras_componen_jn.
	 */
	private void posAtualizar(final AbsValidAmostrasComponentes amostraComp, final AbsValidAmostrasComponentes original) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if(isObjetoModificado(amostraComp,original)){
			AbsValidAmostraComponentesJn amostraCompJn = new AbsValidAmostraComponentesJn();
			amostraCompJn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, AbsValidAmostraComponentesJn.class, servidorLogado.getUsuario());
			amostraCompJn.setCriadoEm(amostraComp.getCriadoEm());
			amostraCompJn.setCsaCodigo(amostraComp.getId().getCsaCodigo());
			amostraCompJn.setIdadeMesFinal(amostraComp.getIdadeMesFinal());
			amostraCompJn.setIdadeMesInicial(amostraComp.getIdadeMesFinal());
			amostraCompJn.setNroMaximoSolicitacoes(amostraComp.getNroMaximoSolicitacoes());
			amostraCompJn.setSeqp(amostraComp.getId().getSeqp());
			amostraCompJn.setSerMatricula(amostraComp.getSerMatricula());
			amostraCompJn.setSerVinCodigo(amostraComp.getSerVinCodigo());
			amostraCompJn.setTipoValidade(amostraComp.getTipoValidade());
			amostraCompJn.setUnidValidAmostra(amostraComp.getUnidValidAmostra());
			amostraCompJn.setValidade(amostraComp.getValidade());
			
			getValidadeAmostraJnDAO().persistir(amostraCompJn);
			getValidadeAmostraJnDAO().flush();
		}
	}
	
	
	private boolean isEdicao(final AbsValidAmostrasComponentes amostraComp){
		if(amostraComp.getId() != null && amostraComp.getId().getCsaCodigo() != null && amostraComp.getId().getSeqp() != null){
			return true;
		}else{
			return false;
		}
	}

	
	private boolean isObjetoModificado(final AbsValidAmostrasComponentes amostraComp, final AbsValidAmostrasComponentes original){
		
		if(amostraComp.getIdadeMesInicial().intValue() != original.getIdadeMesInicial().intValue()){
			return true;
		}
		if(amostraComp.getIdadeMesFinal().intValue() != original.getIdadeMesFinal().intValue()){
			return true;
		}
		
		if(amostraComp.getValidade().intValue() != original.getValidade().intValue()){
			return true;
		}
		if(amostraComp.getNroMaximoSolicitacoes().intValue() != original.getNroMaximoSolicitacoes().intValue()){
			return true;
		}
		
		if(amostraComp.getUnidValidAmostra().getCodigo() != original.getUnidValidAmostra().getCodigo()){
			return true;
		}
		
		if(amostraComp.getTipoValidade().getCodigo() != original.getTipoValidade().getCodigo()){
			return true;
		}
		
		return false;
	}
	
	protected ValidadeAmostraDAO getValidadeAmostraDAO() {
		return validadeAmostraDAO;
	}
	
	protected ValidadeAmostraJnDAO getValidadeAmostraJnDAO(){
		return validadeAmostraJnDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
