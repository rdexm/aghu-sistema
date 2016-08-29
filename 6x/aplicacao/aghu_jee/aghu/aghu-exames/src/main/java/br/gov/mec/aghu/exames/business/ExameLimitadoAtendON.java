package br.gov.mec.aghu.exames.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.exames.dao.AelExamesLimitadoAtendDAO;
import br.gov.mec.aghu.exames.dao.VAelExameMatAnaliseDAO;
import br.gov.mec.aghu.exames.vo.LiberacaoLimitacaoExameVO;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AelExamesLimitadoAtend;
import br.gov.mec.aghu.model.AelExamesMaterialAnaliseId;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.VAelExameMatAnalise;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ExameLimitadoAtendON extends BaseBusiness {


@EJB
private ExameLimitadoAtendRN exameLimitadoAtendRN;

private static final Log LOG = LogFactory.getLog(ExameLimitadoAtendON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelExamesLimitadoAtendDAO aelExamesLimitadoAtendDAO;

@Inject
private VAelExameMatAnaliseDAO vAelExameMatAnaliseDAO;

@EJB
private IAghuFacade aghuFacade;

@EJB
private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 9113569008795997302L;
	
	public enum ExameLimitadoAtendONExceptionCode implements BusinessExceptionCode {
		ERRO_EXAME_INCLUIDO, MAIS_DE_UM_ATENDIMENTO_ENCONTRADO, MAIS_DE_UM_ATENDIMENTO_ENCONTRADO_PACIENTE
	}

	public List<LiberacaoLimitacaoExameVO> pesquisarLiberacaoLimitacaoExames(Integer atdSeq){
		List<LiberacaoLimitacaoExameVO> lista =  this.getAelExameLimitadoAtendDAO().pesquisarLiberacaoLimitacaoExames(atdSeq);
		for(LiberacaoLimitacaoExameVO liberaLimitacaoExameVO: lista){
			AelExamesMaterialAnaliseId id = new AelExamesMaterialAnaliseId();
			id.setManSeq(liberaLimitacaoExameVO.getManSeq());
			id.setExaSigla(liberaLimitacaoExameVO.getSigla());
			VAelExameMatAnalise exameMatAnalise = this.getVAelExameMatAnaliseDAO().obterPorChavePrimaria(id);
			liberaLimitacaoExameVO.setNomeUsualMaterial(exameMatAnalise.getNomeUsualMaterial());
		}
		return lista;
	}
	
	
	public void persistir(AelExamesLimitadoAtend exameLimitadoAtend) throws ApplicationBusinessException {
		AelExamesLimitadoAtend exameLimitadoAtendOld = this.getAelExameLimitadoAtendDAO().obterPeloId(exameLimitadoAtend.getId());
		if(exameLimitadoAtendOld!=null){
			throw new ApplicationBusinessException(ExameLimitadoAtendONExceptionCode.ERRO_EXAME_INCLUIDO);
		} else {
			this.getExameLimitadoAtendRN().persistir(exameLimitadoAtend);	
		}
	}
	
	public AghAtendimentos obterAtendimentoAtualPorLeitoId(final String leitoId) throws ApplicationBusinessException {
		AinLeitos leito = getCadastrosBasicosInternacaoFacade().obterLeitoPorId(leitoId);
		List<AghAtendimentos> listaAtendimentos = getAghuFacade().pesquisarAtendimentoVigenteExameLimitadoPorLeito(leito);
		if (listaAtendimentos.size() == 1){
			return listaAtendimentos.get(0);
		} 
		
		if (listaAtendimentos.size() > 1){
			throw new ApplicationBusinessException(
					ExameLimitadoAtendONExceptionCode.MAIS_DE_UM_ATENDIMENTO_ENCONTRADO);
		}
		return null;		
	}
	
	public AghAtendimentos obterAtendimentoAtualPorProntuario(final Integer prontuario) throws ApplicationBusinessException {
		List<AghAtendimentos> listaAtendimentos = getAghuFacade().pesquisarAtendimentoVigenteExameLimitadoPorProntuario(prontuario);
		if (listaAtendimentos.size() == 1){
			return listaAtendimentos.get(0);
		} 
		
		if (listaAtendimentos.size() > 1){
			throw new ApplicationBusinessException(
					ExameLimitadoAtendONExceptionCode.MAIS_DE_UM_ATENDIMENTO_ENCONTRADO_PACIENTE);
		}
		return null;
	}
	
	protected ExameLimitadoAtendRN getExameLimitadoAtendRN() {
		return exameLimitadoAtendRN;
	}
	
	protected AelExamesLimitadoAtendDAO getAelExameLimitadoAtendDAO() {
		return aelExamesLimitadoAtendDAO;
	}
	
	protected VAelExameMatAnaliseDAO getVAelExameMatAnaliseDAO() {
		return vAelExameMatAnaliseDAO;
	}
	
	protected ICadastrosBasicosInternacaoFacade getCadastrosBasicosInternacaoFacade() {
		return this.cadastrosBasicosInternacaoFacade;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
}
