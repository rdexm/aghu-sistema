package br.gov.mec.aghu.blococirurgico.procdiagterap.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.PdtTecnicaPorProcDAO;
import br.gov.mec.aghu.model.PdtProcDiagTerap;
import br.gov.mec.aghu.model.PdtTecnica;
import br.gov.mec.aghu.model.PdtTecnicaPorProc;
import br.gov.mec.aghu.model.PdtTecnicaPorProcId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class PdtTecnicaPorProcON extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(PdtTecnicaPorProcON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private PdtTecnicaPorProcDAO pdtTecnicaPorProcDAO;


	@EJB
	private IRegistroColaboradorFacade iRegistroColaboradorFacade;

	@EJB
	private PdtTecnicaPorProcRN pdtTecnicaPorProcRN;
	/**
	 * 
	 */
	private static final long serialVersionUID = -8747738157374339830L;

	protected enum TecnicaONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_TECNICA_PROCEDIMENTO_DIAGNOSTICO_TERAPEUTICO_CADASTRADO
		;
	}

	public void refreshPdtTecnicaPorProc(List<PdtTecnicaPorProc> list) {
		final PdtTecnicaPorProcDAO dao = getPdtTecnicaPorProcDAO();
		for(PdtTecnicaPorProc item : list){
			if(dao.contains(item)){
				dao.refresh(item);
			}
		}
	}

	public String persistirPdtTecnicaPorProc(PdtTecnica tecnica, PdtProcDiagTerap procDiagTerap) throws ApplicationBusinessException{
		PdtTecnicaPorProc tecnicaPorProc = new PdtTecnicaPorProc();
		PdtTecnicaPorProcId id = new PdtTecnicaPorProcId(tecnica.getSeq(), procDiagTerap.getSeq());
		tecnicaPorProc.setId(id);
		tecnicaPorProc.setCriadoEm(new Date());
		getTecnicaAssocProcedDiagTerapRN().antesInserirPdtTecnicaPorProc();
		tecnicaPorProc.setRapServidores(servidorLogadoFacade.obterServidorLogado());
		
		PdtTecnicaPorProc tecnicaPorProcExiste = getPdtTecnicaPorProcDAO().obterPorChavePrimaria(id);
		
		// Se já existir um relacionamento, não deve persistir outro
		if(tecnicaPorProcExiste != null){
			throw new ApplicationBusinessException(TecnicaONExceptionCode.MENSAGEM_TECNICA_PROCEDIMENTO_DIAGNOSTICO_TERAPEUTICO_CADASTRADO);
		}else {
			getPdtTecnicaPorProcDAO().persistir(tecnicaPorProc);
			return "MENSAGEM_CRIACAO_TECNICA_PROCEDIMENTO_DIAGNOSTICO_TERAPEUTICO";
		}
	}
	
	public String removerPdtTecnicaPorProc(PdtTecnicaPorProc tecnicaPorProc){
		PdtTecnicaPorProc tecnica = getPdtTecnicaPorProcDAO().obterPorChavePrimaria(tecnicaPorProc.getId());
		
		if (tecnica != null) {
			getPdtTecnicaPorProcDAO().remover(tecnica);
			getTecnicaAssocProcedDiagTerapRN().posDeletePdtTecnicaPorProc(tecnica);
			return "MENSAGEM_EXCLUSAO_TECNICA_PROCEDIMENTO_DIAGNOSTICO_TERAPEUTICO";
		}
		
		return "";
	}
	
	protected PdtTecnicaPorProcRN getTecnicaAssocProcedDiagTerapRN(){
		return pdtTecnicaPorProcRN;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return iRegistroColaboradorFacade;
	}

	protected PdtTecnicaPorProcDAO getPdtTecnicaPorProcDAO() {
		return pdtTecnicaPorProcDAO;
	}

}
