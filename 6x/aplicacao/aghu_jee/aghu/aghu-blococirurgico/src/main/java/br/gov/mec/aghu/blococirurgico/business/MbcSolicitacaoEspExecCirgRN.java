package br.gov.mec.aghu.blococirurgico.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcAgendaSolicEspecialDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcNecessidadeCirurgicaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcSolicitacaoEspExecCirgDAO;
import br.gov.mec.aghu.dominio.DominioOperacaoBanco;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.model.MbcAgendaSolicEspecial;
import br.gov.mec.aghu.model.MbcAgendaSolicEspecialId;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcSolicitacaoEspExecCirg;
import br.gov.mec.aghu.model.MbcSolicitacaoEspExecCirgId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável pelas regras de negócio relacionadas à entidade
 * MbcSolicitacaoEspExecCirg.
 * 
 * @author ihaas
 * 
 */
@Stateless
public class MbcSolicitacaoEspExecCirgRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(MbcSolicitacaoEspExecCirgRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcAgendaSolicEspecialDAO mbcAgendaSolicEspecialDAO;
	
		
	@Inject
	private MbcNecessidadeCirurgicaDAO mbcNecessidadeCirurgicaDAO;

	@Inject
	private MbcSolicitacaoEspExecCirgDAO mbcSolicitacaoEspExecCirgDAO;

	@Inject
	private MbcAgendasDAO mbcAgendasDAO; 

	@EJB
	private IRegistroColaboradorFacade iRegistroColaboradorFacade;

	@EJB
	private MbcAgendaSolicEspecialRN mbcAgendaSolicEspecialRN;

	/**
	 * 
	 */
	private static final long serialVersionUID = -639550022022971803L;
	
	private enum MbcSolicitacaoEspExecCirgRNExceptionCode implements BusinessExceptionCode {
		MBC_00313,
		MBC_00315
	}

	/**
	 * Insere instância de MbcSolicitacaoEspExecCirg.
	 * 
	 * @param solicitacaoEspecial
	 * @throws BaseException 
	 */
	public void persistirSolicitacaoEspecial(MbcSolicitacaoEspExecCirg solicitacaoEspecial)
			throws BaseException {

		executarAntesDeInserir(solicitacaoEspecial);
		
		// cria o id se não vir informado
				if (solicitacaoEspecial.getId() == null || solicitacaoEspecial.getId().getSeqp() == null){
					criarIdSolicitacaoEspecial(solicitacaoEspecial);
				}
		
		getMbcSolicitacaoEspExecCirgDAO().persistir(solicitacaoEspecial);
		
				
		if (solicitacaoEspecial.getMbcCirurgias().getAgenda() != null){
			MbcAgendas agenda = this.mbcAgendasDAO.obterPorChavePrimaria(solicitacaoEspecial.getMbcCirurgias().getAgenda().getSeq());
			
			solicitacaoEspecial.getMbcCirurgias().setAgenda(agenda);
			solicitacaoEspecial.setMbcNecessidadeCirurgica(this.mbcNecessidadeCirurgicaDAO.obterPorChavePrimaria(solicitacaoEspecial.getMbcNecessidadeCirurgica().getSeq()));
			
			if(agenda.getIndGeradoSistema().equals(Boolean.TRUE)) {			
			   executaAposInserirAtualizarExcluir(solicitacaoEspecial, DominioOperacaoBanco.INS);
			}
		}
	}
	
	private void criarIdSolicitacaoEspecial(MbcSolicitacaoEspExecCirg solicitacaoEspecial) {
		MbcSolicitacaoEspExecCirgId id = new MbcSolicitacaoEspExecCirgId();
		id.setCrgSeq(solicitacaoEspecial.getMbcCirurgias().getSeq());
		id.setNciSeq(solicitacaoEspecial.getMbcNecessidadeCirurgica().getSeq());
		Short seqp = getMbcSolicitacaoEspExecCirgDAO()
				.obterSolicEspProximoSeqp(
						solicitacaoEspecial.getMbcCirurgias().getSeq(),
						solicitacaoEspecial.getMbcNecessidadeCirurgica()
								.getSeq());
		id.setSeqp(seqp);
		solicitacaoEspecial.setId(id);
	}

	/**
	 * Trigger
	 * 
	 * ORADB: MBCT_SEC_BRI
	 * 
	 * @param solicitacaoEspecial
	 * @param obterLoginUsuarioLogado
	 * @throws ApplicationBusinessException
	 */
	private void executarAntesDeInserir(MbcSolicitacaoEspExecCirg solicitacaoEspecial)
			throws ApplicationBusinessException {
		
		//RN1
		solicitacaoEspecial.setCriadoEm(new Date());
		//RN4
		solicitacaoEspecial.setRapServidores(servidorLogadoFacade.obterServidorLogado());
		
		validarDadosSolicitacaoEspecial(solicitacaoEspecial);
		
	}

	/**
	 * 
	 * Valida situação da cirurgia e a obrigatoriedade da descrição
	 * 
	 * @param solicitacaoEspecial
	 * @throws ApplicationBusinessException
	 */
	private void validarDadosSolicitacaoEspecial(
			MbcSolicitacaoEspExecCirg solicitacaoEspecial) throws ApplicationBusinessException {
		//RN2 ORADB MBCK_SEC_RN.RN_SECP_VER_SIT_CIRG
		if (!solicitacaoEspecial.getMbcCirurgias().getSituacao().equals(DominioSituacaoCirurgia.AGND)) {
			throw new ApplicationBusinessException(MbcSolicitacaoEspExecCirgRNExceptionCode.MBC_00313);
		}
		//RN3 ORADB MBCK_SEC_RN.RN_SECP_VER_NEC_CIRG
		if(solicitacaoEspecial.getDescricao() == null 
				&& solicitacaoEspecial.getMbcNecessidadeCirurgica().getIndExigeDescSolic().equals(Boolean.TRUE)) {
			
			throw new ApplicationBusinessException(MbcSolicitacaoEspExecCirgRNExceptionCode.MBC_00315);
		}
	}
	
	/**
	 * 
	 * Atualiza instância de MbcSolicitacaoEspExecCirg
	 * 
	 * @param solicitacaoEspecial
	 * @throws BaseException 
	 * 
	 */
	public void atualizarSolicitacaoEspecial(MbcSolicitacaoEspExecCirg solicitacaoEspecial) throws BaseException {
		
		validarDadosSolicitacaoEspecial(solicitacaoEspecial);
		
		getMbcSolicitacaoEspExecCirgDAO().atualizar(solicitacaoEspecial);
		
		if (solicitacaoEspecial.getMbcCirurgias().getAgenda() != null){
			MbcAgendas agenda = this.mbcAgendasDAO.obterPorChavePrimaria(solicitacaoEspecial.getMbcCirurgias().getAgenda().getSeq());
			
			solicitacaoEspecial.getMbcCirurgias().setAgenda(agenda);
			solicitacaoEspecial.setMbcNecessidadeCirurgica(this.mbcNecessidadeCirurgicaDAO.obterPorChavePrimaria(solicitacaoEspecial.getMbcNecessidadeCirurgica().getSeq()));
			
			if(agenda.getIndGeradoSistema().equals(Boolean.TRUE)) {			
			   executaAposInserirAtualizarExcluir(solicitacaoEspecial, DominioOperacaoBanco.UPD);
			}
		}		
	}
	
	
	
	public void excluirSolicitacaoEspecial(Integer crgSeqExc, Short nciSeqExc, Short seqpExc) throws BaseException {
		MbcSolicitacaoEspExecCirg solicitacaoEspecial = 
				mbcSolicitacaoEspExecCirgDAO.obterPorChavePrimaria(new MbcSolicitacaoEspExecCirgId(crgSeqExc, nciSeqExc, seqpExc));

		MbcSolicitacaoEspExecCirg sol = getMbcSolicitacaoEspExecCirgDAO().
				obterPorChavePrimaria(solicitacaoEspecial.getId(), new Enum[] {MbcSolicitacaoEspExecCirg.Fields.MBC_CIRURGIAS }, null);
		
		getMbcSolicitacaoEspExecCirgDAO().remover(sol);
		
		if (solicitacaoEspecial.getMbcCirurgias().getAgenda() != null){
			MbcAgendas agenda = this.mbcAgendasDAO.obterPorChavePrimaria(solicitacaoEspecial.getMbcCirurgias().getAgenda().getSeq());
			
			solicitacaoEspecial.getMbcCirurgias().setAgenda(agenda);
			solicitacaoEspecial.setMbcNecessidadeCirurgica(this.mbcNecessidadeCirurgicaDAO.obterPorChavePrimaria(solicitacaoEspecial.getMbcNecessidadeCirurgica().getSeq()));
			
			if(agenda.getIndGeradoSistema().equals(Boolean.TRUE)) {			
			   executaAposInserirAtualizarExcluir(solicitacaoEspecial, DominioOperacaoBanco.DEL);
			}
		}		
		
	}
	
	/**
	 * 
	 * ORADB MBCP_ENFORCE_SEC_RULES
	 * @throws BaseException 
	 * 
	 */
	private void executaAposInserirAtualizarExcluir(MbcSolicitacaoEspExecCirg solicEspecial,
			DominioOperacaoBanco operacao) throws BaseException {
		
		if (operacao.equals(DominioOperacaoBanco.INS)) {
			MbcAgendaSolicEspecial agendaSolicEsp = new MbcAgendaSolicEspecial();
			agendaSolicEsp.setDescricao(solicEspecial.getDescricao());
			agendaSolicEsp.setMbcAgendas(solicEspecial.getMbcCirurgias().getAgenda());
			agendaSolicEsp.setMbcNecessidadeCirurgica(solicEspecial.getMbcNecessidadeCirurgica());
			
			this.getMbcAgendaSolicEspecialRN().persistirAgendaSolicEspecial(agendaSolicEsp);
			
		} else if (operacao.equals(DominioOperacaoBanco.UPD)) {
			MbcAgendaSolicEspecialId id = new MbcAgendaSolicEspecialId();
			id.setAgdSeq(solicEspecial.getMbcCirurgias().getAgenda().getSeq());
			id.setNciSeq(solicEspecial.getId().getNciSeq());
			id.setSeqp(solicEspecial.getId().getSeqp());
			
			MbcAgendaSolicEspecial agendaSolicEsp = this.getMbcAgendaSolicEspecialDAO().obterPorChavePrimaria(id);
			if (agendaSolicEsp != null) {
				agendaSolicEsp.setDescricao(solicEspecial.getDescricao());
				
				this.getMbcAgendaSolicEspecialRN().persistirAgendaSolicEspecial(agendaSolicEsp);
			}
			
		} else if (operacao.equals(DominioOperacaoBanco.DEL)) {
			MbcAgendas ag = mbcAgendasDAO.obterPorChavePrimaria(solicEspecial.getMbcCirurgias().getAgenda().getSeq());
			MbcAgendaSolicEspecialId id = new MbcAgendaSolicEspecialId();
			id.setAgdSeq(ag.getSeq());
			id.setNciSeq(solicEspecial.getId().getNciSeq());
			id.setSeqp(solicEspecial.getId().getSeqp());
			
			MbcAgendaSolicEspecial agendaSolicEsp = this.getMbcAgendaSolicEspecialDAO().obterPorChavePrimaria(id);
			if(agendaSolicEsp != null) {
				this.getMbcAgendaSolicEspecialRN().deletar(agendaSolicEsp);
			}
		}
	}

	private MbcSolicitacaoEspExecCirgDAO getMbcSolicitacaoEspExecCirgDAO() {
		return mbcSolicitacaoEspExecCirgDAO;
	}
	
	private MbcAgendaSolicEspecialRN getMbcAgendaSolicEspecialRN() {
		return mbcAgendaSolicEspecialRN;
	}
	
	private MbcAgendaSolicEspecialDAO getMbcAgendaSolicEspecialDAO() {
		return mbcAgendaSolicEspecialDAO;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return iRegistroColaboradorFacade;
	}

}
