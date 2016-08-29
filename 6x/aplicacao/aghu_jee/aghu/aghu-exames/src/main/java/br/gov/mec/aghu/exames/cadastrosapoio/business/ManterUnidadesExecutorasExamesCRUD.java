package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.configuracao.dao.AghUnidadesFuncionaisDAO;
import br.gov.mec.aghu.dominio.DominioApAnterior;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSimNaoRestritoAreaExecutora;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioUnidTempo;
import br.gov.mec.aghu.exames.dao.AelTipoAmostraExameDAO;
import br.gov.mec.aghu.exames.dao.AelUnfExecutaExamesDAO;
import br.gov.mec.aghu.exames.dao.AelUnfExecutaExamesJnDAO;
import br.gov.mec.aghu.model.AbsExameMetodos;
import br.gov.mec.aghu.model.AelConfigMapaExames;
import br.gov.mec.aghu.model.AelExamesEspecialidade;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelGradeAgendaExame;
import br.gov.mec.aghu.model.AelItemPedidoExame;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelPacUnidFuncionais;
import br.gov.mec.aghu.model.AelPermissaoUnidSolic;
import br.gov.mec.aghu.model.AelTipoAmostraExame;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AelUnfExecutaExamesJn;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MpaCadOrdItemExame;
import br.gov.mec.aghu.model.MpmAltaItemPedidoExame;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class ManterUnidadesExecutorasExamesCRUD extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ManterUnidadesExecutorasExamesCRUD.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelUnfExecutaExamesJnDAO aelUnfExecutaExamesJnDAO;
	
	@Inject
	private AelUnfExecutaExamesDAO aelUnfExecutaExamesDAO;
	
	@EJB
	private IAghuFacade iAghuFacade;
	
	@Inject
	private AelTipoAmostraExameDAO aelTipoAmostraExameDAO;
	
	@Inject
	private AghUnidadesFuncionaisDAO aghUnidadesFuncionaisDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -8858897667532860966L;
	public enum ManterUnidadesExecutorasExamesExceptionCode implements BusinessExceptionCode {
		AEL_00371,AEL_00376,AEL_00377,AEL_00375, AEL_UFE_CK23, AEL_UFE_CK26, AEL_UFE_CK27, AEL_UFE_CK19, AEL_UFE_CK11, AEL_UFE_PK, FK_AEL_ITEM_SOLICITACAO_EXAMES, FK_AEL_GRADE_AGENDA_EXAME, FK_MPA_CAD_ORD_ITEM_EXAME, FK_AEL_PAC_UNID_FUNCIONAIS, FK_AEL_CONFIG_MAPA_EXAMES, FK_AEL_ITEM_PEDIDO_EXAME, FK_AEL_ALTA_ITEM_PEDIDO_EXAME, FK_ABS_EXAME_METODOS, FK_AEL_PERMISSAO_UNID_SOLICS, FK_AEL_EXAMES_ESPECIALIDADE;
	}

	public void persistirUnidadeExecutoraExames(
			AelUnfExecutaExames aelUnfExecutaExames, AelExamesMaterialAnalise examesMaterialAnalise) throws BaseException {
		validaConstraints(aelUnfExecutaExames);
		if(aelUnfExecutaExames.getId().getEmaExaSigla() == null && aelUnfExecutaExames.getId().getEmaManSeq() == null && aelUnfExecutaExames.getAelExamesMaterialAnalise() == null){
			// inserir
			preInserirUnidadeExecutoraExames(aelUnfExecutaExames,examesMaterialAnalise);
			getAelUnfExecutaExamesDAO().persistir(aelUnfExecutaExames);
			getAelUnfExecutaExamesDAO().flush();

		}else{
			// atualizar
			preAtualizarUnidadeExecutoraExames(aelUnfExecutaExames);
			getAelUnfExecutaExamesDAO().merge(aelUnfExecutaExames);
			getAelUnfExecutaExamesDAO().flush();
			posAtualizarUnidadeExecutoraExames(aelUnfExecutaExames);
		}

	}



	@SuppressWarnings("PMD.NPathComplexity")
	private void validaConstraints(AelUnfExecutaExames aelUnfExecutaExames) throws ApplicationBusinessException {

		Short tempoLibRotinaIg = aelUnfExecutaExames.getTempoLibRotinaIg();
		DominioUnidTempo unidTempLibRotIg = aelUnfExecutaExames.getUnidTempLibRotIg();
		Short tempoLibUrgIg = aelUnfExecutaExames.getTempoLibUrgenteIg();
		DominioUnidTempo unidTempLibUrgIg = aelUnfExecutaExames.getUnidTempLibUrgIg();
		Short tempoAposLiberacao = aelUnfExecutaExames.getTempoAposLiberacao();
		DominioUnidTempo unidTempoAposLib = aelUnfExecutaExames.getUnidTempoAposLib();
		Boolean indDesativaTemp = aelUnfExecutaExames.getIndDesativaTemp();
		String motivoDesativacao = aelUnfExecutaExames.getMotivoDesativacao();
		Date dthrReativaTemp = aelUnfExecutaExames.getDthrReativaTemp();
		DominioSituacao indSituacao = aelUnfExecutaExames.getIndSituacao();
		DominioSimNao indLaudoUnico = aelUnfExecutaExames.getIndLaudoUnico();
		DominioApAnterior indNumApAnterior = aelUnfExecutaExames.getIndNumApAnterior();

		if (indLaudoUnico != null && indNumApAnterior != null && (indLaudoUnico.equals(DominioSimNao.N) && !indNumApAnterior.equals(DominioApAnterior.N))) {

			throw new ApplicationBusinessException(
					ManterUnidadesExecutorasExamesExceptionCode.AEL_UFE_CK23);

		}

		if(! (
				(
						tempoLibRotinaIg == null && unidTempLibRotIg == null
				)
				||
				(
						tempoLibRotinaIg != null && unidTempLibRotIg != null
				)
		)
		){
			throw new ApplicationBusinessException(
					ManterUnidadesExecutorasExamesExceptionCode.AEL_UFE_CK26);
		}

		if(! (
				(
						tempoLibUrgIg == null && unidTempLibUrgIg == null
				)
				||
				(
						tempoLibUrgIg != null && unidTempLibUrgIg != null
				)
		)
		){
			throw new ApplicationBusinessException(
					ManterUnidadesExecutorasExamesExceptionCode.AEL_UFE_CK27);
		}

		if(! (
				(
						tempoAposLiberacao == null	 &&	 unidTempoAposLib == null
				)
				||
				(
						tempoAposLiberacao != null	 &&	 unidTempoAposLib != null
				)
		)
		){
			throw new ApplicationBusinessException(
					ManterUnidadesExecutorasExamesExceptionCode.AEL_UFE_CK19);
		}		 

		if(!	
				( 
						(
								(
										indDesativaTemp && dthrReativaTemp != null && StringUtils.isNotEmpty(motivoDesativacao)
								)
								||
								(
										indSituacao.equals(DominioSituacao.I) && StringUtils.isNotEmpty(motivoDesativacao)
								)
						)
						||
						(
								(!indDesativaTemp) && indSituacao.equals(DominioSituacao.A) && StringUtils.isEmpty(motivoDesativacao)
						)
				)
		){
			throw new ApplicationBusinessException(
					ManterUnidadesExecutorasExamesExceptionCode.AEL_UFE_CK11);
		}


	}



	private void preInserirUnidadeExecutoraExames(
			AelUnfExecutaExames aelUnfExecutaExames,
			AelExamesMaterialAnalise examesMaterialAnalise) throws BaseException {

		aelUnfExecutaExames.setIndLaudoUnico(DominioSimNao.N);
		aelUnfExecutaExames.setIndNumApAnterior(DominioApAnterior.N);
		
		if(aelUnfExecutaExames.getMotivoDesativacao() == null || aelUnfExecutaExames.getMotivoDesativacao().equals("")){
			aelUnfExecutaExames.setMotivoDesativacao(null);
		}
		aelUnfExecutaExames.getId().setEmaExaSigla(examesMaterialAnalise.getId().getExaSigla());
		aelUnfExecutaExames.getId().setEmaManSeq(examesMaterialAnalise.getId().getManSeq());
		aelUnfExecutaExames.setAelExamesMaterialAnalise(examesMaterialAnalise);

		validacoesPreInserirUnidadeExecutoraExames(aelUnfExecutaExames);


	}

	/**
	 * oradb TRIGGER AELT_UFE_BRI, AELT_UFE_ASI – ENFORCE AELP_ENFORCE_UFE_RULES
	 * @param aelUnfExecutaExames
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	private void validacoesPreInserirUnidadeExecutoraExames(AelUnfExecutaExames aelUnfExecutaExames) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		AelUnfExecutaExames unfExecutaExistente = getAelUnfExecutaExamesDAO().buscaAelUnfExecutaExames(aelUnfExecutaExames.getId().getEmaExaSigla(), aelUnfExecutaExames.getId().getEmaManSeq(), aelUnfExecutaExames.getId().getUnfSeq().getSeq());
		if(unfExecutaExistente != null){
			throw new ApplicationBusinessException(
					ManterUnidadesExecutorasExamesExceptionCode.AEL_UFE_PK); 
		}

		//aelk_ael_rn.rn_aelp_atu_servidor
		aelUnfExecutaExames.setServidor(servidorLogado);

		//aelk_ufe_rn.rn_ufep_ver_unid_fun
		verUnfAtiv(aelUnfExecutaExames.getId().getUnfSeq());

		verificarAgendamentos(aelUnfExecutaExames);

		aelUnfExecutaExames.setCriadoEm(new Date());
	}

	/**
	 * ORADB aelk_ael_rn.rn_aelp_ver_unf_ativ
	 * @param aghUnidadesFuncionais
	 * @throws ApplicationBusinessException
	 */
	public void verUnfAtiv(AghUnidadesFuncionais aghUnidadesFuncionais) throws ApplicationBusinessException {	
				
		aghUnidadesFuncionais = aghUnidadesFuncionaisDAO.obterPeloId(aghUnidadesFuncionais.getSeq());		
		
		if (!aghUnidadesFuncionais.getIndSitUnidFunc().equals(DominioSituacao.A)) {
		
			throw new ApplicationBusinessException(ManterUnidadesExecutorasExamesExceptionCode.AEL_00371);
		
		}
	
	}

	private void verificarAgendamentos(AelUnfExecutaExames aelUnfExecutaExames)
	throws ApplicationBusinessException {
		//aelk_ufe_rn.rn_ufep_ver_agnd_amo

		if( ( !aelUnfExecutaExames.getIndAgendamPrevioInt().equals(DominioSimNaoRestritoAreaExecutora.N) || !aelUnfExecutaExames.getIndAgendamPrevioNaoInt().equals(DominioSimNaoRestritoAreaExecutora.N) ) &&
				(aelUnfExecutaExames.getAelExamesMaterialAnalise().getTempoMinParaAgenda() == null || aelUnfExecutaExames.getAelExamesMaterialAnalise().getTempoMinParaAgenda() == 0) && !aelUnfExecutaExames.getAelExamesMaterialAnalise().getAelMateriaisAnalises().getIndColetavel()){
			throw new ApplicationBusinessException(
					ManterUnidadesExecutorasExamesExceptionCode.AEL_00376);
		}

		List<AelTipoAmostraExame> aelTipoAmostraExameLista = getAelTipoAmostraExameDAO().buscarListaAelTipoAmostraExamePorEmaExaSiglaEmaManSeq(aelUnfExecutaExames.getId().getEmaExaSigla(),aelUnfExecutaExames.getId().getEmaManSeq());
		if (aelUnfExecutaExames.getIndSituacao().equals(DominioSituacao.A)
				&& aelUnfExecutaExames.getAelExamesMaterialAnalise()
				.getAelMateriaisAnalises().getIndColetavel()
				&& (aelTipoAmostraExameLista == null || aelTipoAmostraExameLista
						.isEmpty())) {

			throw new ApplicationBusinessException(
					ManterUnidadesExecutorasExamesExceptionCode.AEL_00377);

		}
	}

	private void preAtualizarUnidadeExecutoraExames(
			AelUnfExecutaExames aelUnfExecutaExames) throws ApplicationBusinessException {
		validacoesPreAtualizarUnidadeExecutoraExames(aelUnfExecutaExames);

		if ("".equals(aelUnfExecutaExames.getMotivoDesativacao())) {
			aelUnfExecutaExames.setMotivoDesativacao(null);
		}

	}

	private void validacoesPreAtualizarUnidadeExecutoraExames(
			AelUnfExecutaExames aelUnfExecutaExames) throws ApplicationBusinessException {
		verificarAgendamentos(aelUnfExecutaExames);
	}

	private void posAtualizarUnidadeExecutoraExames(
			AelUnfExecutaExames aelUnfExecutaExames) throws ApplicationBusinessException {

		persistirAelUnfExecutaExamesJn(aelUnfExecutaExames,DominioOperacoesJournal.UPD);

	}

	private void persistirAelUnfExecutaExamesJn(AelUnfExecutaExames aelUnfExecutaExames, DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AelUnfExecutaExamesJn jn = BaseJournalFactory.getBaseJournal(operacao, AelUnfExecutaExamesJn.class, servidorLogado.getUsuario());

		jn.setEmaExaSigla(aelUnfExecutaExames.getId().getEmaExaSigla());
		jn.setEmaManSeq(aelUnfExecutaExames.getId().getEmaManSeq());
		jn.setUnfSeq(aelUnfExecutaExames.getId().getUnfSeq().getSeq());	
		
		jn.setTempoRealizacaoExame(aelUnfExecutaExames.getTempoRealizacaoExame());
		jn.setUnidadeMedidaTempoRealizaca(aelUnfExecutaExames.getUnidadeMedidaTempoRealizaca());
		jn.setIndExecutaEmPlantao(aelUnfExecutaExames.getIndExecutaEmPlantao());
		jn.setIndLiberaResultAutom(aelUnfExecutaExames.getIndLiberaResultAutom());
		jn.setIndExigeInfoClin(aelUnfExecutaExames.getIndExigeInfoClin());
		jn.setIndAgendamPrevioInt(aelUnfExecutaExames.getIndAgendamPrevioInt());
		jn.setIndAgendamPrevioNaoInt(aelUnfExecutaExames.getIndAgendamPrevioNaoInt());
		jn.setIndAvisaSolicitante(aelUnfExecutaExames.getIndAvisaSolicitante());
		jn.setIndImprimeFicha(aelUnfExecutaExames.getIndImprimeFicha());
		jn.setIndImpNomeExameLaudo(aelUnfExecutaExames.getIndImpNomeExameLaudo());
		jn.setIndMonitorPendencia(aelUnfExecutaExames.getIndMonitorPendencia());
		jn.setIndNroFrascoFornec(aelUnfExecutaExames.getIndNroFrascoFornec());
		jn.setIndLaudoUnico(aelUnfExecutaExames.getIndLaudoUnico());
		jn.setIndNumApAnterior(aelUnfExecutaExames.getIndNumApAnterior());
		jn.setIndImpDuasEtiq(aelUnfExecutaExames.getIndImpDuasEtiq());
		jn.setIndPermVerLaudoExecutando(aelUnfExecutaExames.getIndPermVerLaudoExecutando());
		jn.setIndDesativaTemp(aelUnfExecutaExames.getIndDesativaTemp());
		jn.setIndSituacao(aelUnfExecutaExames.getIndSituacao());
		jn.setTempoMedioOcupSala(aelUnfExecutaExames.getTempoMedioOcupSala());
		jn.setMotivoDesativacao(aelUnfExecutaExames.getMotivoDesativacao());
		jn.setSerMatriculaAlterado(aelUnfExecutaExames.getSerMatriculaAlterado());
		jn.setSerVinCodigoAlterado(aelUnfExecutaExames.getSerVinCodigoAlterado());
		
		jn.setDthrReativaTemp(aelUnfExecutaExames.getDthrReativaTemp());
		jn.setUnfSeqComparece(aelUnfExecutaExames.getUnfSeqComparece());
		jn.setTempoAposLiberacao(aelUnfExecutaExames.getTempoAposLiberacao());
		jn.setUnidTempoAposLib(aelUnfExecutaExames.getUnidTempoAposLib());
		jn.setTempoLibRotinaIg(aelUnfExecutaExames.getTempoLibRotinaIg());
		jn.setUnidTempLibRotIg(aelUnfExecutaExames.getUnidTempLibRotIg());
		jn.setTempoLibUrgenteIg(aelUnfExecutaExames.getTempoLibUrgenteIg());
		jn.setUnidTempLibUrgIg(aelUnfExecutaExames.getUnidTempLibUrgIg());

		getAelUnfExecutaExamesJnDAO().persistir(jn);
		getAelUnfExecutaExamesJnDAO().flush();


	}




	public void removerUnidadeExecutoraExames(String emaExaSigla,
			Integer emaManSeq, Short unfSeq) throws BaseException {
		AelUnfExecutaExames unfExecutaRemover =  getAelUnfExecutaExamesDAO().buscaAelUnfExecutaExames(emaExaSigla, emaManSeq, unfSeq);
		preRemoverUnidadeExecutoraExames(unfExecutaRemover);

		getAelUnfExecutaExamesDAO().remover(unfExecutaRemover);
		getAelUnfExecutaExamesDAO().flush();

		posRemoverUnidadeExecutoraExames(unfExecutaRemover);
	}

	private void preRemoverUnidadeExecutoraExames(
			AelUnfExecutaExames unfExecutaRemover) throws BaseException {
		//CHK_AEL_EXAMES
		validaDelecao(unfExecutaRemover);

	}

	private void validaDelecao(AelUnfExecutaExames unfExecutaRemover) throws BaseException {

		if (unfExecutaRemover == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}

		BaseListException erros = new BaseListException();

		erros.add(this.existeUnidadeExecutora(unfExecutaRemover, AelItemSolicitacaoExames.class, AelItemSolicitacaoExames.Fields.UFE_EMA_EXA_SIGLA,AelItemSolicitacaoExames.Fields.UFE_EMA_MAN_SEQ,AelItemSolicitacaoExames.Fields.UFE_UNF_SEQ, ManterUnidadesExecutorasExamesExceptionCode.FK_AEL_ITEM_SOLICITACAO_EXAMES));
		erros.add(this.existeUnidadeExecutora(unfExecutaRemover, AelGradeAgendaExame.class, AelGradeAgendaExame.Fields.UFE_EMA_EXA_SIGLA,AelGradeAgendaExame.Fields.UFE_EMA_MAN_SEQ,AelGradeAgendaExame.Fields.UFE_UNF_SEQ_SEQ, ManterUnidadesExecutorasExamesExceptionCode.FK_AEL_GRADE_AGENDA_EXAME));
		erros.add(this.existeUnidadeExecutora(unfExecutaRemover, MpaCadOrdItemExame.class, MpaCadOrdItemExame.Fields.UFE_EMA_EXA_SIGLA, MpaCadOrdItemExame.Fields.UFE_EMA_MAN_SEQ, MpaCadOrdItemExame.Fields.UFE_UNF_SEQ, ManterUnidadesExecutorasExamesExceptionCode.FK_MPA_CAD_ORD_ITEM_EXAME ));
		erros.add(this.existeUnidadeExecutora(unfExecutaRemover, AelPacUnidFuncionais.class, AelPacUnidFuncionais.Fields.UFE_EMA_EXA_SIGLA, AelPacUnidFuncionais.Fields.UFE_EMA_MAN_SEQ, AelPacUnidFuncionais.Fields.UFE_UNF_SEQ, ManterUnidadesExecutorasExamesExceptionCode.FK_AEL_PAC_UNID_FUNCIONAIS));
		erros.add(this.existeUnidadeExecutora(unfExecutaRemover, AelConfigMapaExames.class, AelConfigMapaExames.Fields.UFE_EMA_EXA_SIGLA, AelConfigMapaExames.Fields.UFE_EMA_MAN_SEQ, AelConfigMapaExames.Fields.UFE_UNF_SEQ, ManterUnidadesExecutorasExamesExceptionCode.FK_AEL_CONFIG_MAPA_EXAMES));
		erros.add(this.existeUnidadeExecutora(unfExecutaRemover, AelItemPedidoExame.class, AelItemPedidoExame.Fields.UFE_EMA_EXA_SIGLA, AelItemPedidoExame.Fields.UFE_EMA_MAN_SEQ, AelItemPedidoExame.Fields.UFE_UNF_SEQ, ManterUnidadesExecutorasExamesExceptionCode.FK_AEL_ITEM_PEDIDO_EXAME));
		erros.add(this.existeUnidadeExecutora(unfExecutaRemover, MpmAltaItemPedidoExame.class, MpmAltaItemPedidoExame.Fields.UFE_EMA_EXA_SIGLA,MpmAltaItemPedidoExame.Fields.UFE_EMA_MAN_SEQ, MpmAltaItemPedidoExame.Fields.UFE_UNF_SEQ, ManterUnidadesExecutorasExamesExceptionCode.FK_AEL_ALTA_ITEM_PEDIDO_EXAME ));
		erros.add(this.existeUnidadeExecutora(unfExecutaRemover, AbsExameMetodos.class, AbsExameMetodos.Fields.UFE_EMA_EXA_SIGLA, AbsExameMetodos.Fields.UFE_EMA_MAN_SEQ, AbsExameMetodos.Fields.UFE_UNF_SEQ, ManterUnidadesExecutorasExamesExceptionCode.FK_ABS_EXAME_METODOS));
		erros.add(this.existeUnidadeExecutora(unfExecutaRemover, AelPermissaoUnidSolic.class, AelPermissaoUnidSolic.Fields.UFE_EMA_EXA_SIGLA, AelPermissaoUnidSolic.Fields.UFE_EMA_MAN_SEQ, AelPermissaoUnidSolic.Fields.UFE_UNF, ManterUnidadesExecutorasExamesExceptionCode.FK_AEL_PERMISSAO_UNID_SOLICS));
		erros.add(this.existeUnidadeExecutora(unfExecutaRemover, AelExamesEspecialidade.class, AelExamesEspecialidade.Fields.UFE_EMA_EXA_SIGLA, AelExamesEspecialidade.Fields.UFE_EMA_MAN_SEQ, AelExamesEspecialidade.Fields.UFE_UNF_SEQ, ManterUnidadesExecutorasExamesExceptionCode.FK_AEL_EXAMES_ESPECIALIDADE));
		


		if (erros.hasException()) {
			throw erros;
		}

	}

	private ApplicationBusinessException existeUnidadeExecutora(AelUnfExecutaExames unfExecutaRemover,  Class class1, Enum field1, Enum field2, Enum field3, ManterUnidadesExecutorasExamesExceptionCode exceptionCode) {

		if (unfExecutaRemover == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}

		final boolean isExisteItemDieta = getAelUnfExecutaExamesDAO().existeUnfExecutaExames(unfExecutaRemover, class1, field1,field2,field3);

		if(isExisteItemDieta){
			return new ApplicationBusinessException(exceptionCode);
		}

		return null;
	}

	/**
	 * ORADB AELT_UFE_ARD
	 * @param unfExecutaRemover
	 * @throws ApplicationBusinessException 
	 */
	private void posRemoverUnidadeExecutoraExames(
			AelUnfExecutaExames unfExecutaRemover) throws ApplicationBusinessException {
		//aelk_ecp_rn.rn_ecpp_ver_conselho
		persistirAelUnfExecutaExamesJn(unfExecutaRemover,DominioOperacoesJournal.DEL);

	}

	
	protected AelUnfExecutaExamesDAO getAelUnfExecutaExamesDAO(){
		return aelUnfExecutaExamesDAO;
	}
	protected AelUnfExecutaExamesJnDAO getAelUnfExecutaExamesJnDAO(){
		return aelUnfExecutaExamesJnDAO;
	}
	protected IAghuFacade getAghuFacade() {
		return this.iAghuFacade;
	}
	protected AelTipoAmostraExameDAO getAelTipoAmostraExameDAO(){
		return aelTipoAmostraExameDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
