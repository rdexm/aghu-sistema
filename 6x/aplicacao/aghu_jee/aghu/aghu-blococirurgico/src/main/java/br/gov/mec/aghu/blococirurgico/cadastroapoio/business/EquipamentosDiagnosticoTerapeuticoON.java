package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.PdtEquipPorProcDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtEquipamentoDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtInstrPorEquipDAO;
import br.gov.mec.aghu.model.PdtEquipPorProc;
import br.gov.mec.aghu.model.PdtEquipPorProcId;
import br.gov.mec.aghu.model.PdtEquipamento;
import br.gov.mec.aghu.model.PdtInstrPorEquip;
import br.gov.mec.aghu.model.PdtInstrPorEquipId;
import br.gov.mec.aghu.model.PdtInstrumental;
import br.gov.mec.aghu.model.PdtProcDiagTerap;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class EquipamentosDiagnosticoTerapeuticoON extends BaseBusiness{

	private static final Log LOG = LogFactory.getLog(EquipamentosDiagnosticoTerapeuticoON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private PdtEquipPorProcDAO pdtEquipPorProcDAO;

	@Inject
	private PdtEquipamentoDAO pdtEquipamentoDAO;

	@Inject
	private PdtInstrPorEquipDAO pdtInstrPorEquipDAO;


	@EJB
	private EquipamentosDiagnosticoTerapeuticoRN equipamentosDiagnosticoTerapeuticoRN;

	@EJB
	private IRegistroColaboradorFacade iRegistroColaboradorFacade;

	private static final long serialVersionUID = 5454601501155607354L;

	protected enum EquipamentoONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_EQUIPAMENTO_CRUD_PROCEDIMENTO_CADASTRADO, MENSAGEM_EQUIPAMENTO_CRUD_INSTRUMENTO_CADASTRADO
		;
	}

	public String persistirPdtEquipamento(PdtEquipamento equipamento) throws ApplicationBusinessException{
		if(equipamento.getSeq() == null){
			equipamento.setCriadoEm(new Date());
			equipamento.setRapServidores(servidorLogadoFacade.obterServidorLogado());
			getEquipamentoRN().validarMotivoInativacaoPdtEquipamento(equipamento);
			getPdtEquipamentoDAO().persistir(equipamento);
			return "MENSAGEM_EQUIPAMENTO_CRUD_CRIACAO_MANTER_EQUIPAMENTO";
		}else{
			getPdtEquipamentoDAO().atualizar(equipamento);
			getEquipamentoRN().validarMotivoInativacaoPdtEquipamento(equipamento);
			getEquipamentoRN().posUpdatePdtEquipamento(equipamento);
			return "MENSAGEM_EQUIPAMENTO_CRUD_EDICAO_MANTER_EQUIPAMENTO";
		}
	}
	
	public String persistirPdtEquipPorProc(PdtEquipamento equipamento, PdtProcDiagTerap procDiagTerap) throws ApplicationBusinessException{
		PdtEquipPorProc equipPorProc = new PdtEquipPorProc();
		PdtEquipPorProcId id = new PdtEquipPorProcId(procDiagTerap.getSeq(), equipamento.getSeq());
		equipPorProc.setId(id);
		equipPorProc.setCriadoEm(new Date());
		equipPorProc.setRapServidores(servidorLogadoFacade.obterServidorLogado());
		
		
		PdtEquipPorProc equipPorProcExiste = getPdtEquipPorProcDAO().obterPorChavePrimaria(id);
		
		// Se já existir um relacionamento, não deve persistir outro
		if(equipPorProcExiste != null){
			throw new ApplicationBusinessException(EquipamentoONExceptionCode.MENSAGEM_EQUIPAMENTO_CRUD_PROCEDIMENTO_CADASTRADO);
		}else {
			getPdtEquipPorProcDAO().persistir(equipPorProc);
			return "MENSAGEM_EQUIPAMENTO_CRUD_CRIACAO_EQUIPAMENTO_PROCEDIMENTO_DIAGNOSTICO_TERAPEUTICO";
		}
	}
	
	public String persistirPdtInstrPorEquip(PdtEquipamento equipamento, PdtInstrumental instrumental) throws ApplicationBusinessException{
		PdtInstrPorEquip instrPorEquip = new PdtInstrPorEquip();
		PdtInstrPorEquipId id = new PdtInstrPorEquipId(equipamento.getSeq(), instrumental.getSeq());
		instrPorEquip.setId(id);
		instrPorEquip.setCriadoEm(new Date());
		instrPorEquip.setRapServidores(servidorLogadoFacade.obterServidorLogado());
		
		PdtInstrPorEquip instrPorEquipExiste = getPdtInstrPorEquipDAO().obterPorChavePrimaria(id);
		
		// Se já existir um relacionamento, não deve persistir outro
		if(instrPorEquipExiste != null){
			throw new ApplicationBusinessException(EquipamentoONExceptionCode.MENSAGEM_EQUIPAMENTO_CRUD_INSTRUMENTO_CADASTRADO);
		}else {
			getPdtInstrPorEquipDAO().persistir(instrPorEquip);
			return "MENSAGEM_EQUIPAMENTO_CRUD_CRIACAO_EQUIPAMENTO_INSTRUMENTO";
		}
	}
	
	public String removerPdtEquipPorProc(PdtEquipPorProc equipPorProc){
		PdtEquipPorProc equip = getPdtEquipPorProcDAO().obterPorChavePrimaria(equipPorProc.getId());
		
		if (equip != null) {
			getPdtEquipPorProcDAO().remover(equip);
			getEquipamentoRN().posDeletePdtEquipPorProc(equip);
			
			return "MENSAGEM_EQUIPAMENTO_CRUD_EXCLUSAO_EQUIPAMENTO_PROCEDIMENTO_DIAGNOSTICO_TERAPEUTICO";
		}
		
		return "";
	}
	
	public String removerPdtInstrPorEquip(PdtInstrPorEquip instrPorEquip){
		PdtInstrPorEquip instr = getPdtInstrPorEquipDAO().obterPorChavePrimaria(instrPorEquip.getId());
		
		if (instr != null) {
			getPdtInstrPorEquipDAO().remover(instr);
			getEquipamentoRN().posDeletePdtInstrPorEquip(instr);
			return "MENSAGEM_EQUIPAMENTO_CRUD_EXCLUSAO_EQUIPAMENTO_INSTRUMENTO";
		}
		
		return "";
	}
	
	public void refreshPdtEquipPorProc(List<PdtEquipPorProc> list) {
		final PdtEquipPorProcDAO dao = getPdtEquipPorProcDAO();
		
		for(PdtEquipPorProc item : list){
			if(dao.contains(item)){
				dao.refresh(item);
			}
		}
	}
	
	public void refreshPdtInstrPorEquip(List<PdtInstrPorEquip> list) {
		final PdtInstrPorEquipDAO dao = getPdtInstrPorEquipDAO();
		
		for(PdtInstrPorEquip item : list){
			if(dao.contains(item)){
				dao.refresh(item);
			}
		}
	}
	
	protected EquipamentosDiagnosticoTerapeuticoRN getEquipamentoRN() {
		return equipamentosDiagnosticoTerapeuticoRN;
	}
	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return iRegistroColaboradorFacade;
	}
	
	protected PdtEquipamentoDAO getPdtEquipamentoDAO() {
		return pdtEquipamentoDAO;
	}

	protected PdtEquipPorProcDAO getPdtEquipPorProcDAO() {
		return pdtEquipPorProcDAO;
	}

	protected PdtInstrPorEquipDAO getPdtInstrPorEquipDAO() {
		return pdtInstrPorEquipDAO;
	}
}