package br.gov.mec.aghu.blococirurgico.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcAgendaSolicEspecialDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendasDAO;
import br.gov.mec.aghu.dominio.DominioOperacaoAgenda;
import br.gov.mec.aghu.dominio.DominioOrigem;
import br.gov.mec.aghu.model.MbcAgendaSolicEspecial;
import br.gov.mec.aghu.model.MbcAgendaSolicEspecialId;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável por prover os métodos de negócio de MbcAgendaSolicEspecial.
 * 
 * @autor fwinck
 * 
 */
@Stateless
public class MbcAgendaSolicEspecialRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(MbcAgendaSolicEspecialRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcAgendasDAO mbcAgendasDAO;

	@Inject
	private MbcAgendaSolicEspecialDAO mbcAgendaSolicEspecialDAO;


	@EJB
	private MbcAgendaHistoricoRN mbcAgendaHistoricoRN;
	private static final long serialVersionUID = -3529490920821934722L;
	
	public enum MbcAgendaSolicEspecialRNExceptionCode implements BusinessExceptionCode {
		MBC_00834, MBC_00889;
	}

	public void persistirAgendaSolicEspecial(MbcAgendaSolicEspecial agendaSolicEspecial) throws BaseException {
		if (agendaSolicEspecial.getId() == null) {
			definirId(agendaSolicEspecial);
			preInserir(agendaSolicEspecial);
			this.getMbcAgendaSolicEspecialDAO().persistir(agendaSolicEspecial);
			//Adicionado flush para evitar que o metodo que busca o proximo seqp traga um seqp ja existente.
			this.getMbcAgendaSolicEspecialDAO().flush();
		} else {
			MbcAgendaSolicEspecial oldAgendaSolicEspecial = getMbcAgendaSolicEspecialDAO().obterOriginal(agendaSolicEspecial.getId());
			preAlterar(agendaSolicEspecial, oldAgendaSolicEspecial);
			this.getMbcAgendaSolicEspecialDAO().merge(agendaSolicEspecial);
		}
	}
	
	private void definirId(MbcAgendaSolicEspecial agendaSolicEspecial){
		MbcAgendaSolicEspecialId id = new MbcAgendaSolicEspecialId();
		id.setAgdSeq(agendaSolicEspecial.getMbcAgendas().getSeq());
		id.setNciSeq(agendaSolicEspecial.getMbcNecessidadeCirurgica().getSeq());
		id.setSeqp(getMbcAgendaSolicEspecialDAO().buscarProximoSeqp(id.getAgdSeq(), id.getNciSeq()));

		
		
		agendaSolicEspecial.setId(id);
	}
	
	public void deletar(MbcAgendaSolicEspecial agendaSolicEspecial) throws BaseException {
		preDeletar(agendaSolicEspecial);
		getMbcAgendaSolicEspecialDAO().remover(agendaSolicEspecial);
	}
	
	private void preInserir(MbcAgendaSolicEspecial agendaSolicEspecial) throws ApplicationBusinessException {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		agendaSolicEspecial.setCriadoEm(new Date());
		agendaSolicEspecial.setRapServidores(servidorLogado);
		validarAgendaComControleEscalaCirurgicaDefinitiva(agendaSolicEspecial.getMbcAgendas());
		validarDescricaoCasoObrigatoria(agendaSolicEspecial);
	}
	
	private void preAlterar(MbcAgendaSolicEspecial newAgendaSolicEspecial, MbcAgendaSolicEspecial oldAgendaSolicEspecial
			) throws BaseException {
		newAgendaSolicEspecial.setRapServidores(servidorLogadoFacade.obterServidorLogado());
		validarAgendaComControleEscalaCirurgicaDefinitiva(newAgendaSolicEspecial.getMbcAgendas());
		validarDescricaoCasoObrigatoria(newAgendaSolicEspecial);
		if(!newAgendaSolicEspecial.getMbcNecessidadeCirurgica().equals(oldAgendaSolicEspecial.getMbcNecessidadeCirurgica())
				|| !newAgendaSolicEspecial.getDescricao().equals(oldAgendaSolicEspecial.getDescricao())) {
			popularSalvarHistoricoAgenda(newAgendaSolicEspecial, oldAgendaSolicEspecial);
		}
	}
	
	private void preDeletar(MbcAgendaSolicEspecial agendaSolicEspecial) throws BaseException {
		validarAgendaComControleEscalaCirurgicaDefinitiva(agendaSolicEspecial.getMbcAgendas());
		popularSalvarHistoricoAgenda(agendaSolicEspecial, null);
	}
	
	/**
	 * @ORADB mbck_ags_rn.rn_agsp_ver_escala
	 */
	public void validarAgendaComControleEscalaCirurgicaDefinitiva(MbcAgendas agenda) throws ApplicationBusinessException {
		MbcAgendas result = getMbcAgendasDAO().pesquisarAgendaComControleEscalaCirurgicaDefinitiva(agenda.getSeq());
		if(result != null && !result.getIndGeradoSistema()) {
			throw new ApplicationBusinessException(MbcAgendaSolicEspecialRNExceptionCode.MBC_00834);
		}
	}
	
	/**
	 * @ORADB mbck_ags_rn.rn_agsp_ver_nec_cirg
	 */
	public void validarDescricaoCasoObrigatoria(MbcAgendaSolicEspecial agendaSolicEspecial) throws ApplicationBusinessException {
		if(agendaSolicEspecial.getMbcNecessidadeCirurgica().getIndExigeDescSolic() && (agendaSolicEspecial.getDescricao() == null
				|| agendaSolicEspecial.getDescricao().isEmpty())) {
			throw new ApplicationBusinessException(MbcAgendaSolicEspecialRNExceptionCode.MBC_00889);
		}
	}
	
	/**
	 * @ORADB mbck_ags_rn.rn_agsp_inc_historic
	 * @param newAgendaAnestesia
	 * @param oldAgendaAnestesia
	 * @throws BaseException 
	 */
	private void popularSalvarHistoricoAgenda(MbcAgendaSolicEspecial newAgendaSolicEspecial, MbcAgendaSolicEspecial oldAgendaSolicEspecial
			) throws BaseException {
		
		StringBuffer descricao = new StringBuffer();
		DominioOperacaoAgenda operacao;
		
		if(oldAgendaSolicEspecial != null) {
			operacao = DominioOperacaoAgenda.A;
			
			
			descricao.append("Necessidade cirúrgica alterada de ").append(oldAgendaSolicEspecial.getMbcNecessidadeCirurgica().getDescricao());
			if(oldAgendaSolicEspecial.getDescricao() != null && !oldAgendaSolicEspecial.getDescricao().isEmpty()) {
				descricao.append(" - ").append(oldAgendaSolicEspecial.getDescricao());
			}
			descricao.append(" para ").append(newAgendaSolicEspecial.getMbcNecessidadeCirurgica().getDescricao());
			if(newAgendaSolicEspecial.getDescricao() != null && !newAgendaSolicEspecial.getDescricao().isEmpty()) {
				descricao.append(" - ").append(newAgendaSolicEspecial.getDescricao()); 
			}
		} else {
			operacao = DominioOperacaoAgenda.E;
			descricao.append("Necessidade cirúrgica excluída: ").append(newAgendaSolicEspecial.getMbcNecessidadeCirurgica().getDescricao())  
					.append(newAgendaSolicEspecial.getDescricao() != null && !newAgendaSolicEspecial.getDescricao().isEmpty() 
							? " - " + newAgendaSolicEspecial.getDescricao() : "");
		}
		getMbcAgendaHistoricoRN().inserir(newAgendaSolicEspecial.getMbcAgendas().getSeq(),
				newAgendaSolicEspecial.getMbcAgendas().getIndSituacao(), DominioOrigem.S, descricao.toString(), operacao);
	}
	
	protected MbcAgendasDAO getMbcAgendasDAO() {
		return mbcAgendasDAO;
	}
	
	protected MbcAgendaHistoricoRN getMbcAgendaHistoricoRN() {
		return mbcAgendaHistoricoRN;
	}

	protected MbcAgendaSolicEspecialDAO getMbcAgendaSolicEspecialDAO(){
		return mbcAgendaSolicEspecialDAO;
	}
}