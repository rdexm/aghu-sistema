package br.gov.mec.aghu.blococirurgico.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcAgendaAnestesiaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendasDAO;
import br.gov.mec.aghu.dominio.DominioOperacaoAgenda;
import br.gov.mec.aghu.dominio.DominioOrigem;
import br.gov.mec.aghu.model.MbcAgendaAnestesia;
import br.gov.mec.aghu.model.MbcAgendaAnestesiaId;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável por prover os métodos de negócio de MbcAgendaAnestesia.
 * 
 * @autor fwinck
 * 
 */
@Stateless
public class MbcAgendaAnestesiaRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(MbcAgendaAnestesiaRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcAgendasDAO mbcAgendasDAO;

	@Inject
	private MbcAgendaAnestesiaDAO mbcAgendaAnestesiaDAO;


	@EJB
	private MbcAgendaHistoricoRN mbcAgendaHistoricoRN;

	private static final long serialVersionUID = -3458622371469364380L;
	
	public enum MbcAgendaAnestesiaRNExceptionCode implements BusinessExceptionCode {
		MBC_00895; //Agenda Anestesias não pode ser incluída, alterada ou excluída - Já foi executada escala do dia
	}

	public void persistirAgendaAnestesia(MbcAgendaAnestesia agendaAnestesia) throws BaseException {
		if (agendaAnestesia.getId() == null) {
			definirId(agendaAnestesia);
			preInserir(agendaAnestesia);
			this.getMbcAgendaAnestesiaDAO().persistir(agendaAnestesia);
		} else {
			MbcAgendaAnestesia oldAgendaAnestesia = getMbcAgendaAnestesiaDAO().obterOriginal(agendaAnestesia.getId());
			preAtualizar(agendaAnestesia, oldAgendaAnestesia);
			this.getMbcAgendaAnestesiaDAO().merge(agendaAnestesia);
		}
	}

	private void definirId(MbcAgendaAnestesia agendaAnestesia){
		MbcAgendaAnestesiaId id = new MbcAgendaAnestesiaId();
		id.setAgdSeq(agendaAnestesia.getMbcAgendas().getSeq());
		id.setTanSeq(agendaAnestesia.getMbcTipoAnestesias().getSeq());
		agendaAnestesia.setId(id);
	}
	
	public void deletar(MbcAgendaAnestesia agendaAnestesia) throws BaseException {
		preDeletar(agendaAnestesia);
		this.getMbcAgendaAnestesiaDAO().remover(agendaAnestesia);
	}
	
	/**
	 * @ORADB MBCT_AGA_BRD
	 * @param agendaAnestesia
	 * @param servidorLogado
	 * @throws BaseException 
	 */
	private void preDeletar(MbcAgendaAnestesia agendaAnestesia) throws BaseException {
		validarAgendaComControleEscalaCirurgicaDefinitiva(agendaAnestesia.getMbcAgendas());
		popularSalvarHistoricoAgenda(agendaAnestesia, null);
	}
	
	/**
	 * @throws BaseException 
	 * @ORADB MBCT_AGA_BRU
	 */
	private void preAtualizar(MbcAgendaAnestesia newAgendaAnestesia, MbcAgendaAnestesia oldAgendaAnestesia) throws BaseException {
		validarAgendaComControleEscalaCirurgicaDefinitiva(newAgendaAnestesia.getMbcAgendas());
		if(!newAgendaAnestesia.getMbcTipoAnestesias().equals(newAgendaAnestesia.getMbcTipoAnestesias())) {
			popularSalvarHistoricoAgenda(newAgendaAnestesia, oldAgendaAnestesia);
		}
	}
	
	/**
	 * @ORADB MBCT_AGA_BRI
	 * 
	 * 
	 * @throws ApplicationBusinessException 
	 */
	private void preInserir(MbcAgendaAnestesia mbcAgendaAnestesia) throws ApplicationBusinessException {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		mbcAgendaAnestesia.setCriadoEm(new Date());
		mbcAgendaAnestesia.setRapServidores(servidorLogado);
		validarAgendaComControleEscalaCirurgicaDefinitiva(mbcAgendaAnestesia.getMbcAgendas());
	}
	
	/**
	 * @ORADB mbck_aga_rn.rn_agap_ver_escala
	 * 
	 * 
	 * @throws ApplicationBusinessException 
	 */
	public void validarAgendaComControleEscalaCirurgicaDefinitiva(MbcAgendas agenda) throws ApplicationBusinessException {
		MbcAgendas result = getMbcAgendasDAO().pesquisarAgendaComControleEscalaCirurgicaDefinitiva(agenda.getSeq());
		if(result != null && !result.getIndGeradoSistema()) {
			throw new ApplicationBusinessException(MbcAgendaAnestesiaRNExceptionCode.MBC_00895);
		}
	}
	
	/**
	 * @ORADB mbck_aga_rn.rn_agap_inc_historic
	 * @param newAgendaAnestesia
	 * @param oldAgendaAnestesia
	 * @throws BaseException 
	 */
	private void popularSalvarHistoricoAgenda(MbcAgendaAnestesia newAgendaAnestesia, MbcAgendaAnestesia oldAgendaAnestesia) throws BaseException {
		StringBuffer descricaoHistorico = new StringBuffer();
		DominioOperacaoAgenda operacaoHistorico;
		
		if(oldAgendaAnestesia != null) {
			operacaoHistorico = DominioOperacaoAgenda.A;
			descricaoHistorico.append("Tipo de anestesia alterado de ").append(oldAgendaAnestesia.getMbcTipoAnestesias().getDescricao())
					.append(" para ").append(newAgendaAnestesia.getMbcTipoAnestesias().getDescricao());
		} else {
			operacaoHistorico = DominioOperacaoAgenda.E;
			descricaoHistorico.append("Tipo de anestesia excluída: ").append(newAgendaAnestesia.getMbcTipoAnestesias().getDescricao());
		}
		
		getMbcAgendaHistoricoRN().inserir(newAgendaAnestesia.getMbcAgendas().getSeq(),
				newAgendaAnestesia.getMbcAgendas().getIndSituacao(), DominioOrigem.N, descricaoHistorico.toString(),
				operacaoHistorico);
	}
	
	protected MbcAgendaAnestesiaDAO getMbcAgendaAnestesiaDAO() {
		return mbcAgendaAnestesiaDAO;
	}
	
	protected MbcAgendasDAO getMbcAgendasDAO() {
		return mbcAgendasDAO;
	}
	
	protected MbcAgendaHistoricoRN getMbcAgendaHistoricoRN() {
		return mbcAgendaHistoricoRN;
	}
}