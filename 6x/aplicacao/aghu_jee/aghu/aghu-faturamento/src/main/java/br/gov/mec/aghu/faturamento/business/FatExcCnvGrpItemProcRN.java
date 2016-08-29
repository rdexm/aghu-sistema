package br.gov.mec.aghu.faturamento.business;

import java.io.Serializable;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.faturamento.dao.FatExcCnvGrpItemProcJnDAO;
import br.gov.mec.aghu.model.FatExcCnvGrpItemProc;
import br.gov.mec.aghu.model.FatExcCnvGrpItemProcJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;

/**
 * Implementacao das triggers da FAT_EXC_CNV_GRP_ITENS_PROC
 */

@Stateless
public class FatExcCnvGrpItemProcRN extends BaseBusiness implements	Serializable {
	
	private static final Log LOG = LogFactory.getLog(FatExcCnvGrpItemProcRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private FatExcCnvGrpItemProcJnDAO fatExcCnvGrpItemProcJnDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -7564822461261616023L;

	/**
	 * ORADB FATT_EGI_BRI
	 * 
	 * @param excCnvGrpItemProc
	 */
	public void executarAntesDeInserir(FatExcCnvGrpItemProc excCnvGrpItemProc){
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		Date data = new Date();		
		excCnvGrpItemProc.setAlteradoEm(data);
		excCnvGrpItemProc.setCriadoEm(data);
		excCnvGrpItemProc.setAlteradoPor(servidorLogado.getUsuario());
		excCnvGrpItemProc.setCriadoPor(servidorLogado.getUsuario());
	}
	
	/**
	 * ORADB FATT_EGI_BRU
	 * 
	 * @param excCnvGrpItemProc
	 */
	public void executarAntesDeAtualizar(FatExcCnvGrpItemProc excCnvGrpItemProc){
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		Date data = new Date();		
		excCnvGrpItemProc.setAlteradoEm(data);
		excCnvGrpItemProc.setAlteradoPor(servidorLogado.getUsuario());
	}
	
	
	/**
	 * ORADB FATT_EGI_ARU
	 * 
	 * @param excCnvGrpItemProcNew
	 * @param excCnvGrpItemProcOld
	 */
	public void executarDepoisDeAtualizar(FatExcCnvGrpItemProc excCnvGrpItemProcNew, FatExcCnvGrpItemProc excCnvGrpItemProcOld){
		
		if(CoreUtil.modificados(excCnvGrpItemProcNew.getIphSeq(), excCnvGrpItemProcOld.getIphSeq())
				||CoreUtil.modificados(excCnvGrpItemProcNew.getIphPhoSeq(), excCnvGrpItemProcOld.getIphPhoSeq())
				||CoreUtil.modificados(excCnvGrpItemProcNew.getIphSeqRealizado(), excCnvGrpItemProcOld.getIphSeqRealizado())
				||CoreUtil.modificados(excCnvGrpItemProcNew.getIphPhoSeqRealizado(), excCnvGrpItemProcOld.getIphPhoSeqRealizado())
				||CoreUtil.modificados(excCnvGrpItemProcNew.getCpgCphCspSeq(), excCnvGrpItemProcOld.getCpgCphCspSeq())
				||CoreUtil.modificados(excCnvGrpItemProcNew.getCpgCphCspCnvCodigo(), excCnvGrpItemProcOld.getCpgCphCspCnvCodigo())
				||CoreUtil.modificados(excCnvGrpItemProcNew.getCpgCphPhoSeq(), excCnvGrpItemProcOld.getCpgCphPhoSeq())
				||CoreUtil.modificados(excCnvGrpItemProcNew.getCpgGrcSeq(), excCnvGrpItemProcOld.getCpgGrcSeq())
				||CoreUtil.modificados(excCnvGrpItemProcNew.getCriadoPor(), excCnvGrpItemProcOld.getCriadoPor())
				||CoreUtil.modificados(excCnvGrpItemProcNew.getCriadoEm(), excCnvGrpItemProcOld.getCriadoEm())
				||CoreUtil.modificados(excCnvGrpItemProcNew.getAlteradoPor(), excCnvGrpItemProcOld.getAlteradoPor())
				||CoreUtil.modificados(excCnvGrpItemProcNew.getAlteradoEm(), excCnvGrpItemProcOld.getAlteradoEm())
				){
			FatExcCnvGrpItemProcJn jn = construirObjetoJournal(excCnvGrpItemProcOld, DominioOperacoesJournal.UPD);
			this.getFatExcCnvGrpItemProcJnDAO().persistir(jn);
		}
	}
	
	
	/**
	 *  ORADB FATT_EGI_ARD
	 *  
	 * @param excCnvGrpItemProc
	 */
	public void executarDepoisDeRemover(FatExcCnvGrpItemProc excCnvGrpItemProc){
		
		FatExcCnvGrpItemProcJn jn = construirObjetoJournal(excCnvGrpItemProc, DominioOperacoesJournal.DEL);
		this.getFatExcCnvGrpItemProcJnDAO().persistir(jn);
	}
	
	/**
	 * Metodo para 'montar' um objeto Jn (setando os devidos atributos) para posterior persistência.
	 * @param excCnvGrpItemProc
	 * @param operacao
	 * @return
	 */
	private FatExcCnvGrpItemProcJn construirObjetoJournal(FatExcCnvGrpItemProc excCnvGrpItemProc, DominioOperacoesJournal operacao){
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
				
		FatExcCnvGrpItemProcJn jn = new FatExcCnvGrpItemProcJn();
		
		jn.setNomeUsuario(servidorLogado.getUsuario());
		jn.setOperacao(operacao);
		
		jn.setPhiSeq(excCnvGrpItemProc.getPhiSeq());
		jn.setIphPhoSeq(excCnvGrpItemProc.getIphPhoSeq());
		jn.setIphSeq(excCnvGrpItemProc.getIphSeq());
		jn.setIphPhoSeqRealizado(excCnvGrpItemProc.getIphPhoSeqRealizado());
		jn.setIphSeqRealizado(excCnvGrpItemProc.getIphSeqRealizado());
		jn.setCpgCphCspSeq(excCnvGrpItemProc.getCpgCphCspSeq());
		jn.setCpgCphCspCnvCodigo(excCnvGrpItemProc.getCpgCphCspCnvCodigo());
		jn.setCpgCphPhoSeq(excCnvGrpItemProc.getCpgCphPhoSeq());
		jn.setCpgGrcSeq(excCnvGrpItemProc.getCpgGrcSeq());
		jn.setCriadoPor(excCnvGrpItemProc.getCriadoPor());
		jn.setCriadoEm(excCnvGrpItemProc.getCriadoEm());
		jn.setAlteradoPor(excCnvGrpItemProc.getAlteradoPor());
		jn.setAlteradoEm(excCnvGrpItemProc.getAlteradoEm());
		
		return jn;
	}
	
	protected FatExcCnvGrpItemProcJnDAO getFatExcCnvGrpItemProcJnDAO() {
		return fatExcCnvGrpItemProcJnDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
