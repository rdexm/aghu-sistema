package br.gov.mec.aghu.blococirurgico.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcAgendaOrtProteseDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcMatOrteseProtCirgDAO;
import br.gov.mec.aghu.dominio.DominioOperacaoBanco;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MbcAgendaOrtProtese;
import br.gov.mec.aghu.model.MbcAgendaOrtProteseId;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcMatOrteseProtCirg;
import br.gov.mec.aghu.model.MbcMatOrteseProtCirgId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável pelas regras de negócio relacionadas à entidade
 * MbcMatOrteseProtCirgRN.
 * 
 * @author fbraganca
 * 
 */

@Stateless
public class MbcMatOrteseProtCirgRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MbcMatOrteseProtCirgRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcMatOrteseProtCirgDAO mbcMatOrteseProtCirgDAO;

	@Inject
	private MbcAgendaOrtProteseDAO mbcAgendaOrtProteseDAO;

	@Inject
	private MbcAgendasDAO mbcAgendaDao;

	@EJB
	private MbcAgendaOrtProteseRN mbcAgendaOrtProteseRN;

	@EJB
	private IRegistroColaboradorFacade iRegistroColaboradorFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 8208672656577693843L;

	private enum MbcMatOrteseProtCirgRNExceptionCode implements BusinessExceptionCode {
		MBC_00255
	}
	
	/**
	 * Insere instância de MbcMatOrteseProtCirg
	 * 
	 * @param materialOrteseProtese
	 * @param obterLoginUsuarioLogado
	 * @throws BaseException 
	 */
	
	public void persistirMatOrteseProtese(MbcMatOrteseProtCirg materialOrteseProtese) throws BaseException{
		
		this.executarAntesDeInserir(materialOrteseProtese);
		
		MbcMatOrteseProtCirgId id = new MbcMatOrteseProtCirgId();
		id.setCrgSeq(materialOrteseProtese.getMbcCirurgias().getSeq());
		id.setMatCodigo(materialOrteseProtese.getScoMaterial().getCodigo());
		
		materialOrteseProtese.setId(id);
		
		this.getMbcMatOrteseProtCirgDAO().persistir(materialOrteseProtese);
		
		MbcMatOrteseProtCirg mbcOpme = this.getMbcMatOrteseProtCirgDAO().obterPorChavePrimaria(materialOrteseProtese.getId(), new Enum[] {MbcMatOrteseProtCirg.Fields.MBC_CIRURGIAS}, null);
		MbcAgendas agenda = mbcAgendaDao.obterPorChavePrimaria(mbcOpme.getMbcCirurgias().getAgenda().getSeq());
		if (agenda != null && agenda.getIndGeradoSistema().equals(Boolean.TRUE)) {
			this.executaAposInserirExcluir(materialOrteseProtese, DominioOperacaoBanco.INS, agenda);
		}
	}
	
	/**
	 * Exclui instância de MbcMatOrteseProtCirg
	 * 
	 * @param materialOrteseProtese
	 * @param obterLoginUsuarioLogado
	 * @throws BaseException 
	 */
	
	public void excluirMatOrteseProtese(MbcMatOrteseProtCirg materialOrteseProtese) throws BaseException{
		
		MbcMatOrteseProtCirg mbcOpme = this.getMbcMatOrteseProtCirgDAO().obterPorChavePrimaria(materialOrteseProtese.getId(), new Enum[] {MbcMatOrteseProtCirg.Fields.MBC_CIRURGIAS}, null);
		
		MbcAgendas agenda = mbcAgendaDao.obterPorChavePrimaria(mbcOpme.getMbcCirurgias().getAgenda().getSeq());
		
		if (mbcOpme != null) {
			this.getMbcMatOrteseProtCirgDAO().remover(mbcOpme);	
		}
		
		if(agenda != null
				&& agenda.getIndGeradoSistema().equals(Boolean.TRUE)){
			this.executaAposInserirExcluir(materialOrteseProtese, DominioOperacaoBanco.DEL, agenda);
		}
	}
	
	
	
	/**
	 * Trigger
	 * 
	 * ORADB: MBCT_MOP_BRI
	 * 
	 * @param MbcMatOrteseProtCirg
	 * @throws ApplicationBusinessException
	 */
	private void executarAntesDeInserir(MbcMatOrteseProtCirg materialOrteseProtese) throws ApplicationBusinessException{
		if (!materialOrteseProtese.getScoMaterial().getIndSituacao().equals(DominioSituacao.A)){
			throw new ApplicationBusinessException(MbcMatOrteseProtCirgRNExceptionCode.MBC_00255);
		}
			
	}
	
	/**
	 * 
	 * ORADB MBCP_ENFORCE_MOP_RULES
	 * @throws BaseException 
	 * 
	 */
	
	private void executaAposInserirExcluir(MbcMatOrteseProtCirg materialOrteseProtese,
			 DominioOperacaoBanco operacao, MbcAgendas agenda) throws BaseException{
		
		if(operacao.equals(DominioOperacaoBanco.INS)){
			MbcAgendaOrtProtese agendaOrtProtese = new MbcAgendaOrtProtese();
			
			agendaOrtProtese.setMbcAgendas(agenda);
			agendaOrtProtese.setScoMaterial(materialOrteseProtese.getScoMaterial());
			agendaOrtProtese.setQtde(materialOrteseProtese.getQtdSolic());
			
			this.getMbcAgendaOrtProteseRN().inserirAgendasOrteseProtese(agendaOrtProtese);
			
		} else if (operacao.equals(DominioOperacaoBanco.DEL)){
			MbcAgendaOrtProteseId id = new MbcAgendaOrtProteseId();
			id.setAgdSeq(agenda.getSeq());
			id.setMatCodigo(materialOrteseProtese.getId().getMatCodigo());
			
			MbcAgendaOrtProtese agendaOrtProtese = this.getMbcAgendaOrtProteseDAO().obterPorChavePrimaria(id);
			if(agendaOrtProtese != null){
				this.getMbcAgendaOrtProteseRN().removerAgendasOrteseProtese(agendaOrtProtese);
			}
		}
	}
	
	private MbcAgendaOrtProteseRN getMbcAgendaOrtProteseRN(){
		return mbcAgendaOrtProteseRN;
	}
	
	private MbcAgendaOrtProteseDAO getMbcAgendaOrtProteseDAO() {
		return mbcAgendaOrtProteseDAO;
	}
	
	private MbcMatOrteseProtCirgDAO getMbcMatOrteseProtCirgDAO(){
		return mbcMatOrteseProtCirgDAO;
	}
	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return iRegistroColaboradorFacade;
	}

}
