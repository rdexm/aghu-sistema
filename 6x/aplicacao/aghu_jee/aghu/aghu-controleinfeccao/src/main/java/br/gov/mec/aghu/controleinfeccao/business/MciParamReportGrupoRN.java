package br.gov.mec.aghu.controleinfeccao.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.controleinfeccao.dao.MciParamReportGrupoDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciParamReportGrupoJnDAO;
import br.gov.mec.aghu.model.MciParamReportGrupo;
import br.gov.mec.aghu.model.MciParamReportGrupoId;
import br.gov.mec.aghu.model.MciParamReportGrupoJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class MciParamReportGrupoRN extends BaseBusiness {

/**
	 * 
	 */
	private static final long serialVersionUID = 8304115633456871471L;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private MciParamReportGrupoDAO mciParamReportGrupoDAO;
	
	@Inject
	private MciParamReportGrupoJnDAO mciParamReportGrupoJnDAO;
	

	private enum MciParamReportGrupoExceptionCode implements BusinessExceptionCode {
		MENSAGEM_RESTRICAO_INSERCAO_PARAMETRO_REPORT_GRUPO;
	}
	public void gravarParamReportGrupo(MciParamReportGrupo mciParamReportGrupo) throws ApplicationBusinessException{
		if (mciParamReportGrupo.getId() == null) {
			inserirParamReportGrupo(mciParamReportGrupo);
		} else {
			atualizarParamReportGrupo(mciParamReportGrupo);
		}
	}
	private void inserirParamReportGrupo(MciParamReportGrupo mciParamReportGrupo) throws ApplicationBusinessException{
		setarDadosPadraoInsercao(mciParamReportGrupo);
		validarRegistroDuplicado(mciParamReportGrupo);
		this.mciParamReportGrupoDAO.persistir(mciParamReportGrupo);
	}

	private void setarDadosPadraoInsercao(MciParamReportGrupo mciParamReportGrupo) {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		
		MciParamReportGrupoId id = new MciParamReportGrupoId();
		if(mciParamReportGrupo.getMciGrupoReportRotinaCci() != null) {
			id.setGrrSeq(mciParamReportGrupo.getMciGrupoReportRotinaCci().getSeq());
		}
		if(mciParamReportGrupo.getMciParamReportUsuario() != null) {
			id.setPruSeq(mciParamReportGrupo.getMciParamReportUsuario().getSeq());
		}
		mciParamReportGrupo.setId(id);
		mciParamReportGrupo.setCriadoEm(new Date());
		mciParamReportGrupo.setRapServidores(servidorLogado);
		
	}
	private void atualizarParamReportGrupo(MciParamReportGrupo mciParamReportGrupo) throws ApplicationBusinessException{
		MciParamReportGrupo mciParamReportGrupoOriginal = mciParamReportGrupoDAO.obterOriginal(mciParamReportGrupo.getId());
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
//		validarRegistroDuplicado(mciParamReportGrupo);
		setarDadosPadraoAlteracao(mciParamReportGrupo, servidorLogado);
		this.mciParamReportGrupoDAO.merge(mciParamReportGrupo);
		posAtualizarParamReportGrupo(mciParamReportGrupoOriginal, mciParamReportGrupo, servidorLogado);
	}	
	private void validarRegistroDuplicado(MciParamReportGrupo mciParamReportGrupo) throws ApplicationBusinessException {
		MciParamReportGrupoId id = new MciParamReportGrupoId(mciParamReportGrupo.getId().getPruSeq(), mciParamReportGrupo.getId().getGrrSeq()) ;
		MciParamReportGrupo mciParamReportGrupoOriginal = mciParamReportGrupoDAO.obterPorChavePrimaria(id);
		if (mciParamReportGrupoOriginal != null) {
			throw new ApplicationBusinessException(MciParamReportGrupoExceptionCode.MENSAGEM_RESTRICAO_INSERCAO_PARAMETRO_REPORT_GRUPO);
		}
		
	}
	private void setarDadosPadraoAlteracao(MciParamReportGrupo mciParamReportGrupo, RapServidores servidorLogado) {
		if(mciParamReportGrupo.getMciParamReportUsuario() != null) {
			mciParamReportGrupo.getId().setPruSeq(mciParamReportGrupo.getMciParamReportUsuario().getSeq());
		}
		mciParamReportGrupo.setRapServidorMovimentado(servidorLogado);
		mciParamReportGrupo.setAlteradoEm(new Date());
	}
	private void posAtualizarParamReportGrupo(MciParamReportGrupo mciParamReportGrupoOriginal, MciParamReportGrupo mciParamReportGrupo, RapServidores servidorLogado) throws ApplicationBusinessException{
		if (!mciParamReportGrupoOriginal.equals(mciParamReportGrupo)) {
			persistirJournal(mciParamReportGrupoOriginal, DominioOperacoesJournal.UPD, servidorLogado);
		}
	}
	
	private void persistirJournal(MciParamReportGrupo obj, DominioOperacoesJournal operacao, RapServidores servidorLogado) throws ApplicationBusinessException{
		final MciParamReportGrupoJn journal = BaseJournalFactory.getBaseJournal(operacao, MciParamReportGrupoJn.class, servidorLogado.getUsuario());
		journal.setPruSeq(obj.getId().getPruSeq());      
		journal.setGrrSeq(obj.getId().getGrrSeq());      
		journal.setCriadoEm(obj.getCriadoEm());
		journal.setAlteradoEm(obj.getAlteradoEm());    
		journal.setOrdemEmissao(obj.getOrdemEmissao());
		journal.setNroCopias(obj.getNroCopias());   
		journal.setRapServidores(obj.getRapServidores());
		journal.setRapServidorMovimentado(obj.getRapServidorMovimentado());
		if (obj.getMciExportacaoDado() != null) {
			journal.setEdaSeq(obj.getMciExportacaoDado().getSeq());    
		}
		journal.setIndImpressao(obj.getIndImpressao());
		this.mciParamReportGrupoJnDAO.persistir(journal);	
	}
	
	public void excluirParamReportGrupo(Integer pruSeq, Short grrCodigo) throws ApplicationBusinessException {
		MciParamReportGrupoId id = new MciParamReportGrupoId(pruSeq, grrCodigo) ;
		MciParamReportGrupo mciParamReportGrupoOriginal = mciParamReportGrupoDAO.obterPorChavePrimaria(id);
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		mciParamReportGrupoDAO.remover(mciParamReportGrupoOriginal);
		persistirJournal(mciParamReportGrupoOriginal, DominioOperacoesJournal.DEL, servidorLogado);
	}

	protected MciParamReportGrupoDAO getMciParamReportGrupoDAO() {
		return mciParamReportGrupoDAO;
	}
	
	@Override
	protected Log getLogger() {
		return LogFactory.getLog(MciParamReportGrupoRN.class);
	}
}

