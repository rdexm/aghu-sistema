package br.gov.mec.aghu.faturamento.business;

import java.io.Serializable;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.faturamento.dao.FatConvGrupoItemProcedJnDAO;
import br.gov.mec.aghu.model.FatConvGrupoItemProced;
import br.gov.mec.aghu.model.FatConvGrupoItemProcedJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;


@Stateless
public class ConvenioGrupoItemProcedimentoRN extends BaseBusiness implements
	Serializable {

	@EJB
	private FaturamentoFatkCgiRN faturamentoFatkCgiRN;
	
	private static final Log LOG = LogFactory.getLog(ConvenioGrupoItemProcedimentoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private FatConvGrupoItemProcedJnDAO fatConvGrupoItemProcedJnDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 4177094287716041803L;

	/**
	 * ORADB Trigger FATT_CGI_ARD
	 * 
	 * @param oldFat
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void executarAposExcluirItemConvenioGrupoItemProcedimento(FatConvGrupoItemProced oldFat)
			throws ApplicationBusinessException {

		executarInsertJournal(oldFat, DominioOperacoesJournal.DEL);
		
	}
	
	/**
	 * ORADB Trigger FATT_CGI_ARU
	 * 
	 * @param oldFat
	 * @param newFat
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void executarAposAtualizarConvenioGrupoItemProcedimento(FatConvGrupoItemProced oldFat, FatConvGrupoItemProced newFat)
			throws ApplicationBusinessException {

		if (CoreUtil.modificados(oldFat.getId().getCpgCphCspCnvCodigo(), newFat.getId().getCpgCphCspCnvCodigo()) ||
			CoreUtil.modificados(oldFat.getId().getCpgCphCspSeq(), newFat.getId().getCpgCphCspSeq()) ||
			CoreUtil.modificados(oldFat.getId().getCpgCphPhoSeq(), newFat.getId().getCpgCphPhoSeq()) ||
			CoreUtil.modificados(oldFat.getId().getCpgGrcSeq(), newFat.getId().getCpgGrcSeq()) ||
			CoreUtil.modificados(oldFat.getId().getIphPhoSeq(), newFat.getId().getIphPhoSeq()) ||
			CoreUtil.modificados(oldFat.getId().getIphSeq(), newFat.getId().getIphSeq()) ||
			CoreUtil.modificados(oldFat.getId().getPhiSeq(), newFat.getId().getPhiSeq()) ||
			CoreUtil.modificados(oldFat.getIndExigeJustificativa(), newFat.getIndExigeJustificativa()) ||
			CoreUtil.modificados(oldFat.getIndImprimeLaudo(), newFat.getIndImprimeLaudo()) ||
			CoreUtil.modificados(oldFat.getIndCobrancaFracionada(), newFat.getIndCobrancaFracionada()) ||
			CoreUtil.modificados(oldFat.getCriadoPor(), newFat.getCriadoPor()) ||
			CoreUtil.modificados(oldFat.getAlteradoPor(), newFat.getAlteradoPor()) ||
			CoreUtil.modificados(oldFat.getCriadoEm(), newFat.getCriadoEm()) ||
			CoreUtil.modificados(oldFat.getAlteradoEm(), newFat.getAlteradoEm()) ||
			CoreUtil.modificados(oldFat.getIndExigeAutorPrevia(), newFat.getIndExigeAutorPrevia()) ||
			CoreUtil.modificados(oldFat.getIndPaga(), newFat.getIndPaga()) ||
			CoreUtil.modificados(oldFat.getIndExigeNotaFiscal(), newFat.getIndExigeNotaFiscal()) ||
			CoreUtil.modificados(oldFat.getTempoValidade(), newFat.getTempoValidade()) ||
			CoreUtil.modificados(oldFat.getIndInformaTempoTrat(), newFat.getIndInformaTempoTrat())) {
			executarInsertJournal(newFat, DominioOperacoesJournal.UPD);
		}
	}
	
	/**
	 * ORADB Trigger FATT_CGI_BRI
	 * 
	 * @param newFat
	 * @throws ApplicationBusinessException
	 */
	public void executarAntesInserirConvenioGrupoItemProcedimento(FatConvGrupoItemProced newFat)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		getFaturamentoFatkCgiRN().rnCgipVerUntabhos(newFat.getId().getCpgCphPhoSeq(), newFat.getId().getIphPhoSeq());
				
		newFat.setCriadoEm(new Date());
		newFat.setCriadoPor(servidorLogado.getUsuario());
		newFat.setAlteradoEm(new Date());
		newFat.setAlteradoPor(servidorLogado.getUsuario());
	}
	
	/**
	 * ORADB Trigger FATT_CGI_BRU
	 * 
	 * @param newFat
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void executarAntesAtualizarConvenioGrupoItemProcedimento(FatConvGrupoItemProced newFat)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		getFaturamentoFatkCgiRN().rnCgipVerUntabhos(newFat.getId().getCpgCphPhoSeq(), newFat.getId().getIphPhoSeq());
		
		newFat.setAlteradoEm(new Date());
		newFat.setAlteradoPor(servidorLogado.getUsuario());
		
	}

	
	
	/** Executa o Insert na entidade FatConvGrupoItemProcedJn 
	 * 
	 *  @param  fat
	 *  @param  operacao = DominioOperacoesJournal.DEL, DominioOperacoesJournal.INS, DominioOperacoesJournal.UPD
	 */
	private void executarInsertJournal(FatConvGrupoItemProced fat, DominioOperacoesJournal operacao)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		FatConvGrupoItemProcedJnDAO fatConvGrupoItemProcedJnDAO = getFatConvGrupoItemProcedJnDAO();
		
		FatConvGrupoItemProcedJn jn = new FatConvGrupoItemProcedJn();
		
		jn.setNomeUsuario(servidorLogado.getUsuario()); //pega o usuario logado
		jn.setOperacao(operacao);
		jn.setCpgCphCspCnvCodigo(fat.getId().getCpgCphCspCnvCodigo());
		jn.setCpgCphCspSeq(fat.getId().getCpgCphCspSeq());
		jn.setCpgCphPhoSeq(fat.getId().getCpgCphPhoSeq());
		jn.setCpgGrcSeq(fat.getId().getCpgGrcSeq());
		jn.setIphPhoSeq(fat.getId().getIphPhoSeq());
		jn.setIphSeq(fat.getId().getIphSeq());
		jn.setPhiSeq(fat.getId().getPhiSeq());
		jn.setIndExigeJustificativa(fat.getIndExigeJustificativa());
		jn.setIndImprimeLaudo(fat.getIndImprimeLaudo());
		jn.setIndCobrancaFracionada(fat.getIndCobrancaFracionada());
		jn.setCriadoPor(fat.getCriadoPor());
		jn.setAlteradoPor(fat.getAlteradoPor());
		jn.setCriadoEm(fat.getCriadoEm());
		jn.setAlteradoEm(fat.getAlteradoEm());
		jn.setIndExigeAutorPrevia(fat.getIndExigeAutorPrevia());
		jn.setIndPaga(fat.getIndPaga());
		jn.setIndExigeNotaFiscal(fat.getIndExigeNotaFiscal());
		jn.setTempoValidade(fat.getTempoValidade());
		jn.setIndInformaTempoTrat((fat.getIndInformaTempoTrat() != null)?DominioSimNao.getInstance(fat.getIndInformaTempoTrat()).toString():null);
		
		fatConvGrupoItemProcedJnDAO.persistir(jn); //persiste a entidade
		fatConvGrupoItemProcedJnDAO.flush();
	}
	
	protected FatConvGrupoItemProcedJnDAO getFatConvGrupoItemProcedJnDAO() {
		return fatConvGrupoItemProcedJnDAO;
	}

	protected FaturamentoFatkCgiRN getFaturamentoFatkCgiRN() {
		return faturamentoFatkCgiRN;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
