package br.gov.mec.aghu.blococirurgico.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcAgendaAnestesiaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcAnestesiaCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcControleEscalaCirurgicaDAO;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.dominio.DominioNaturezaFichaAnestesia;
import br.gov.mec.aghu.dominio.DominioOperacaoBanco;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MbcAgendaAnestesia;
import br.gov.mec.aghu.model.MbcAgendaAnestesiaId;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcAnestesiaCirurgias;
import br.gov.mec.aghu.model.MbcControleEscalaCirurgica;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * 
 * @author aghu
 * 
 */
@Stateless
public class MbcAnestesiaCirurgiasRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(MbcAnestesiaCirurgiasRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcAgendasDAO mbcAgendasDAO;

	@Inject
	private MbcAnestesiaCirurgiasDAO mbcAnestesiaCirurgiasDAO;

	

	@Inject
	private MbcAgendaAnestesiaDAO mbcAgendaAnestesiaDAO;

	@Inject
	private MbcControleEscalaCirurgicaDAO mbcControleEscalaCirurgicaDAO;


	@EJB
	private ICascaFacade iCascaFacade;

	@EJB
	private MbcAgendaAnestesiaRN mbcAgendaAnestesiaRN;

	private static final long serialVersionUID = 7906012157146958668L;

	public enum MbcAnestesiaCirurgiasRNExceptionCode implements BusinessExceptionCode {
		MBC_00854, MBC_00217, MBC_00218;
	}

	/*
	 * Métodos para PERSISTIR
	 */
	public void persistir(MbcAnestesiaCirurgias anestesiaCirurg) throws BaseException {
		MbcAnestesiaCirurgias original = this.getMbcAnestesiaCirurgiasDAO().obterOriginal(anestesiaCirurg);
		if (original == null) { // Inserir
			this.inserirMbcAnestesiaCirurgias(anestesiaCirurg);
		} else { // Atualizar
			this.atualizarMbcAnestesiaCirurgias(anestesiaCirurg);
		}
	}

	public void atualizarMbcAnestesiaCirurgias(MbcAnestesiaCirurgias anestesiaCirurg) throws BaseException{
		MbcAnestesiaCirurgias original = this.getMbcAnestesiaCirurgiasDAO().obterOriginal(anestesiaCirurg);
		
		this.preAtualizar(anestesiaCirurg);
		this.getMbcAnestesiaCirurgiasDAO().atualizar(anestesiaCirurg);
		this.getMbcAnestesiaCirurgiasDAO().flush();
		
		/*
		 * As regras a seguir são da ORADB ENFORCE PROCEDURE MBCP_ENFORCE_ANC_RULES
		 */
		this.atualizarAgenda(DominioOperacaoBanco.UPD, original, anestesiaCirurg);

	}

	/**
	 * ORADB TRIGGER "AGH".MBCT_ANC_BRU
	 * 
	 * @param anestesiaCirurg
	 * @param servidorLogado
	 */
	private void preAtualizar(MbcAnestesiaCirurgias anestesiaCirurg) {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		/* SERVIDOR */
		anestesiaCirurg.setServidor(servidorLogado);
	}

	public void inserirMbcAnestesiaCirurgias(MbcAnestesiaCirurgias anestesiaCirurg) throws BaseException {
		this.preInserir(anestesiaCirurg);
		this.getMbcAnestesiaCirurgiasDAO().persistir(anestesiaCirurg);
		this.getMbcAnestesiaCirurgiasDAO().flush();
		
		/*
		 * As regras a seguir são da ORADB ENFORCE PROCEDURE MBCP_ENFORCE_ANC_RULES
		 */
		this.atualizarAgenda(DominioOperacaoBanco.INS, null, anestesiaCirurg);
	}

	/**
	 * ORADB TRIGGER "AGH".MBCT_ANC_BRI
	 * 
	 * @param anestesiaCirurg
	 * @param servidorLogado
	 * @throws ApplicationBusinessException
	 */
	private void preInserir(MbcAnestesiaCirurgias anestesiaCirurg) throws ApplicationBusinessException {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		/* mbck_anc_rn.rn_ancp_ver_alt_elet(:new.crg_seq); */
		// this.verificarAlteracaoEletiva(anestesiaCirurg);
		/* Tipo de anestesia deve estar ativo */
		this.verificarTipoAnestesia(anestesiaCirurg);
		anestesiaCirurg.setServidor(servidorLogado);
		anestesiaCirurg.setCriadoEm(new Date());
	}

	/**
	 * ORADB mbck_anc_rn.rn_ancp_ver_tipo_ane
	 * 
	 * @param anestesiaCirurg
	 * @throws ApplicationBusinessException
	 */
	private void verificarTipoAnestesia(MbcAnestesiaCirurgias anestesiaCirurg) throws ApplicationBusinessException {
		if (anestesiaCirurg.getMbcTipoAnestesias() == null) {
			/* Não existe tipo de anestesia com este código */
			throw new ApplicationBusinessException(MbcAnestesiaCirurgiasRNExceptionCode.MBC_00217);
		} else if (!anestesiaCirurg.getMbcTipoAnestesias().getSituacao().equals(DominioSituacao.A)) {
			/* Tipo de anestesia deve estar ativo */
			throw new ApplicationBusinessException(MbcAnestesiaCirurgiasRNExceptionCode.MBC_00218);
		}
	}

	public void removerMbcAnestesiaCirurgias(MbcAnestesiaCirurgias anestesiaCirurg) throws BaseException {
		MbcAnestesiaCirurgias original = this.getMbcAnestesiaCirurgiasDAO().obterOriginal(anestesiaCirurg);
		
		this.preDelete(anestesiaCirurg);
		this.getMbcAnestesiaCirurgiasDAO().remover(anestesiaCirurg);
		this.getMbcAnestesiaCirurgiasDAO().flush();
		
		/*
		 * As regras a seguir são da ORADB ENFORCE PROCEDURE MBCP_ENFORCE_ANC_RULES
		 */
		this.atualizarAgenda(DominioOperacaoBanco.DEL, original, anestesiaCirurg);
	}

	/**
	 * ORADB TRIGGER "AGH".MBCT_ANC_BRD
	 * 
	 * @param anestesiaCirurg
	 * @throws ApplicationBusinessException
	 */
	private void preDelete(MbcAnestesiaCirurgias anestesiaCirurg) throws ApplicationBusinessException {
		/*
		 * Não permitir alterar anestesia se natureza do agendamento for eletiva e usuário não tem perfil de 'agendar cirurgia não prevista' e já rodou a escala definitiva (usuário
		 * médico)
		 */
		this.verificarAlteracaoEletiva(anestesiaCirurg);
	}

	/**
	 * ORADB mbck_anc_rn.rn_ancp_ver_alt_elet
	 * 
	 * @param anestesiaCirurg
	 * @throws ApplicationBusinessException
	 */
	private void verificarAlteracaoEletiva(MbcAnestesiaCirurgias anestesiaCirurg) throws ApplicationBusinessException {
		if (anestesiaCirurg.getCirurgia() != null && anestesiaCirurg.getCirurgia().getNaturezaAgenda().equals(DominioNaturezaFichaAnestesia.ELE)) {

			boolean perfil = getICascaFacade().usuarioTemPermissao(obterLoginUsuarioLogado(), "agendarCirurgiaNaoPrevista");

			if (!perfil) {

				List<MbcControleEscalaCirurgica> controle = this.getMbcControleEscalaCirurgicaDAO().pesquisarControleEscalaCirurgicaPorUnfSeqDataPesquisa(
						anestesiaCirurg.getCirurgia().getUnidadeFuncional().getSeq(), anestesiaCirurg.getCirurgia().getData());

				if (controle != null && !controle.isEmpty()) {
					/* Já foi executada a escala definitiva para esta data */
					throw new ApplicationBusinessException(MbcAnestesiaCirurgiasRNExceptionCode.MBC_00854);
				}
			}
		}
	}
	
	/**
	 * ORADB mbck_anc_rn.rn_ancp_atu_agenda
	 * @param operacaoBanco
	 * @param anestesiaCirurg
	 * @throws ApplicationBusinessException
	 */
	public void atualizarAgenda(DominioOperacaoBanco operacaoBanco, MbcAnestesiaCirurgias antigo, MbcAnestesiaCirurgias novo) throws BaseException {
		MbcAgendas agenda = this.getMbcAgendasDAO().obterMbcAgendaGeradaPeloSistemaporCirurgia(novo.getCirurgia().getSeq());
		
		if(agenda == null){
			return;
		}
		
		if(DominioOperacaoBanco.INS.equals(operacaoBanco)){
			
			MbcAgendaAnestesia agendaAnestesia = new MbcAgendaAnestesia();
			agendaAnestesia.setMbcAgendas(agenda);
			agendaAnestesia.setMbcTipoAnestesias(novo.getMbcTipoAnestesias());
			
			this.getMbcAgendaAnestesiaRN().persistirAgendaAnestesia(agendaAnestesia);
			
		} else if (DominioOperacaoBanco.UPD.equals(operacaoBanco)){
			
			MbcAgendaAnestesiaId id = new MbcAgendaAnestesiaId();
			id.setAgdSeq(agenda.getSeq());
			id.setTanSeq(antigo.getMbcTipoAnestesias().getSeq());
			
			MbcAgendaAnestesia agendaAnestesia = this.getMbcAgendaAnestesiaDAO().obterPorChavePrimaria(id);
			
			if(agendaAnestesia != null){
				agendaAnestesia.setMbcTipoAnestesias(novo.getMbcTipoAnestesias());
				this.getMbcAgendaAnestesiaRN().persistirAgendaAnestesia(agendaAnestesia);
			}

		} else if (DominioOperacaoBanco.DEL.equals(operacaoBanco)){
			
			MbcAgendaAnestesiaId id = new MbcAgendaAnestesiaId();
			id.setAgdSeq(agenda.getSeq());
			id.setTanSeq(antigo.getMbcTipoAnestesias().getSeq());
			
			MbcAgendaAnestesia agendaAnestesia = this.getMbcAgendaAnestesiaDAO().obterPorChavePrimaria(id);
			if(agendaAnestesia != null){
				this.getMbcAgendaAnestesiaRN().deletar(agendaAnestesia);
			}
	
		}

	}

	protected MbcAnestesiaCirurgiasDAO getMbcAnestesiaCirurgiasDAO() {
		return mbcAnestesiaCirurgiasDAO;
	}

	protected ICascaFacade getICascaFacade() {
		return iCascaFacade;
	}

	
	protected MbcControleEscalaCirurgicaDAO getMbcControleEscalaCirurgicaDAO() {
		return mbcControleEscalaCirurgicaDAO;
	}
	
	protected MbcAgendasDAO getMbcAgendasDAO() {
		return mbcAgendasDAO;
	}
	
	protected MbcAgendaAnestesiaDAO getMbcAgendaAnestesiaDAO() {
		return mbcAgendaAnestesiaDAO;
	}	
	
	
	protected MbcAgendaAnestesiaRN getMbcAgendaAnestesiaRN() {
		return mbcAgendaAnestesiaRN;
	}

	
	
}
