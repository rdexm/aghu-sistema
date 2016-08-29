package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.PdtEquipPorProcDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtEquipPorProcJnDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtEquipamentoDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtEquipamentoJnDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtInstrPorEquipDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtInstrPorEquipJnDAO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.PdtEquipPorProc;
import br.gov.mec.aghu.model.PdtEquipPorProcJn;
import br.gov.mec.aghu.model.PdtEquipamento;
import br.gov.mec.aghu.model.PdtEquipamentoJn;
import br.gov.mec.aghu.model.PdtInstrPorEquip;
import br.gov.mec.aghu.model.PdtInstrPorEquipJn;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class EquipamentosDiagnosticoTerapeuticoRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(EquipamentosDiagnosticoTerapeuticoRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private PdtInstrPorEquipJnDAO pdtInstrPorEquipJnDAO;

	@Inject
	private PdtEquipPorProcJnDAO pdtEquipPorProcJnDAO;

	@Inject
	private PdtEquipPorProcDAO pdtEquipPorProcDAO;

	@Inject
	private PdtEquipamentoDAO pdtEquipamentoDAO;

	@Inject
	private PdtEquipamentoJnDAO pdtEquipamentoJnDAO;

	@Inject
	private PdtInstrPorEquipDAO pdtInstrPorEquipDAO;


	private static final long serialVersionUID = -1974550544218417288L;
	
	protected enum EquipamentoRNExceptionCode implements BusinessExceptionCode {
		PDT_00112, PDT_00113
		;
	}

	/**
	 * ORADB PROCEDURE RN_DEQP_VER_INAT
	 * @throws ApplicationBusinessException 
	 * 
	 */
	public void validarMotivoInativacaoPdtEquipamento(PdtEquipamento equipamento) throws ApplicationBusinessException{
		if(equipamento.getIndSituacao().equals(DominioSituacao.I) && (equipamento.getMotivoInat() == null || equipamento.getMotivoInat().isEmpty())){
			throw new ApplicationBusinessException(EquipamentoRNExceptionCode.PDT_00112);
		}else if (equipamento.getIndSituacao().equals(DominioSituacao.A) && equipamento.getMotivoInat() != null && !equipamento.getMotivoInat().isEmpty()) {
			throw new ApplicationBusinessException(EquipamentoRNExceptionCode.PDT_00113);
		}
	}
	
	/**
	 * ORADB TRIGGER PDTT_DEQ_ARU
	 */
	public void posUpdatePdtEquipamento(PdtEquipamento equipamento){
		PdtEquipamento original = getPdtEquipamentoDAO().obterOriginal(equipamento);
		
		if(CoreUtil.modificados(equipamento.getSeq(), original.getSeq()) ||
				CoreUtil.modificados(equipamento.getDescricao(), original.getDescricao()) ||
				CoreUtil.modificados(equipamento.getIndSituacao(), original.getIndSituacao()) ||
				CoreUtil.modificados(equipamento.getMotivoInat(), original.getMotivoInat()) ||
				CoreUtil.modificados(equipamento.getCriadoEm(), original.getCriadoEm()) ||
				CoreUtil.modificados(equipamento.getRapServidores().getId().getMatricula(), original.getRapServidores().getId().getMatricula()) ||
				CoreUtil.modificados(equipamento.getRapServidores().getId().getVinCodigo(), original.getRapServidores().getId().getVinCodigo()) ||
				CoreUtil.modificados(equipamento.getPatchImagens(), original.getPatchImagens())){
			
			PdtEquipamentoJn equipamentoJn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, PdtEquipamentoJn.class, servidorLogadoFacade.obterServidorLogado().getUsuario());
			equipamentoJn.setSeq(original.getSeq());
			equipamentoJn.setDescricao(original.getDescricao());
			equipamentoJn.setIndSituacao(original.getIndSituacao().toString());
			equipamentoJn.setMotivoInat(original.getMotivoInat());
			equipamentoJn.setCriadoEm(original.getCriadoEm());
			equipamentoJn.setSerMatricula(original.getRapServidores().getId().getMatricula());
			equipamentoJn.setSerVinCodigo(original.getRapServidores().getId().getVinCodigo());
			equipamentoJn.setPatchImagens(original.getPatchImagens());
			
			getPdtEquipamentoJnDAO().persistir(equipamentoJn);
		}
	}
	
	/**
	 * ORADB TRIGGER PDTT_IEQ_ARD
	 */
	public void posDeletePdtInstrPorEquip(PdtInstrPorEquip instrPorEquip){
		PdtInstrPorEquip original = getPdtInstrPorEquipDAO().obterOriginal(instrPorEquip);
		
		PdtInstrPorEquipJn instrPorEquipJn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.DEL, PdtInstrPorEquipJn.class, servidorLogadoFacade.obterServidorLogado().getUsuario());
		instrPorEquipJn.setDeqSeq(original.getId().getDeqSeq());
		instrPorEquipJn.setPinSeq(original.getId().getPinSeq());
		instrPorEquipJn.setCriadoEm(original.getCriadoEm());
		instrPorEquipJn.setSerMatricula(original.getRapServidores().getId().getMatricula());
		instrPorEquipJn.setSerVinCodigo(original.getRapServidores().getId().getVinCodigo());
		
		getPdtInstrPorEquipJnDAO().persistir(instrPorEquipJn);
	}
	
	/**
	 * ORADB TRIGGER PDTT_EPX_ARD
	 */
	public void posDeletePdtEquipPorProc(PdtEquipPorProc equipPorProc){
		PdtEquipPorProc original = getPdtEquipPorProcDAO().obterOriginal(equipPorProc);
		
		PdtEquipPorProcJn equipPorProcJn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.DEL, PdtEquipPorProcJn.class, servidorLogadoFacade.obterServidorLogado().getUsuario());
		equipPorProcJn.setDeqSeq(original.getId().getDeqSeq());
		equipPorProcJn.setDptSeq(original.getId().getDptSeq());
		equipPorProcJn.setCriadoEm(original.getCriadoEm());
		equipPorProcJn.setSerMatricula(original.getRapServidores().getId().getMatricula());
		equipPorProcJn.setSerVinCodigo(original.getRapServidores().getId().getVinCodigo());
		
		
		getPdtEquipPorProcJnDAO().persistir(equipPorProcJn);
	}

	private PdtEquipamentoJnDAO getPdtEquipamentoJnDAO() {
		return pdtEquipamentoJnDAO;
	}
	
	private PdtEquipamentoDAO getPdtEquipamentoDAO() {
		return pdtEquipamentoDAO;
	}
	


	private PdtEquipPorProcJnDAO getPdtEquipPorProcJnDAO() {
		return pdtEquipPorProcJnDAO;
	}
	
	private PdtEquipPorProcDAO getPdtEquipPorProcDAO() {
		return pdtEquipPorProcDAO;
	}
	


	private PdtInstrPorEquipJnDAO getPdtInstrPorEquipJnDAO() {
		return pdtInstrPorEquipJnDAO;
	}
	
	private PdtInstrPorEquipDAO getPdtInstrPorEquipDAO() {
		return pdtInstrPorEquipDAO;
	}
	
	
	
}
