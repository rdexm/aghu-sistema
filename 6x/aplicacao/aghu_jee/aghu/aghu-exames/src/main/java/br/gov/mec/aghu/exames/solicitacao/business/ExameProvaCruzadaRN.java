package br.gov.mec.aghu.exames.solicitacao.business;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.business.bancosangue.IBancoDeSangueFacade;
import br.gov.mec.aghu.dominio.DominioNaturezaFichaAnestesia;
import br.gov.mec.aghu.dominio.DominioOrigemPacienteCirurgia;
import br.gov.mec.aghu.dominio.DominioSituacaoItemSolicitacaoExame;
import br.gov.mec.aghu.dominio.DominioTipoColeta;
import br.gov.mec.aghu.dominio.DominioTipoEscala;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.dao.AelExtratoItemSolicitacaoDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelMotivoCancelaExames;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcControleEscalaCirurgica;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.DateValidator;

/**
 * ORADB Package AELK_EXAME_PCT
 * 
 * @author amribeiro
 */
@Stateless
public class ExameProvaCruzadaRN extends BaseBusiness {
	
	private static final long serialVersionUID = 8002574673564504381L;

	private static final Log LOG = LogFactory.getLog(ExameProvaCruzadaRN.class);
	
	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private ISolicitacaoExameFacade solicitacaoExameFacade;
	
	@EJB
	private IBancoDeSangueFacade bancoDeSangueFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;
	
	@EJB
	private IPesquisaInternacaoFacade pesquisaInternacaoFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private AelExtratoItemSolicitacaoDAO aelExtratoItemSolicitacaoDAO;
	
	@Inject
	private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@EJB
	private SolicitacaoExameRN solicitacaoExameRN;
	
	public enum ExameProvaCruzadaRNExceptionCode implements BusinessExceptionCode {
		UFE_UNF_SEQ_INEXISTENTE;
	}
	
	/**
	 * ORADB AELK_EXAME_PCT.AELP_INCLUI_SOL_ITEM
	 *
	 * @throws MECBaseException 
	 */
	public void inserir(String exaSigla, Integer manSeq, Short ufeUnfSeq,
			AghAtendimentos atendimento, MbcCirurgias cirurgia,
			DominioSituacaoItemSolicitacaoExame situacao,
			DominioTipoColeta tipoColeta, String nomeMicrocomputador,
			RapServidores servidorLogado, RapServidores responsavel) throws BaseException {
		
		AelSolicitacaoExames soe = new AelSolicitacaoExames();
		soe.setAtendimento(atendimento);
		soe.setUnidadeFuncional(atendimento.getUnidadeFuncional());
		soe.setRecemNascido(Boolean.FALSE);
		soe.setCriadoEm(null);
		soe.setServidor(servidorLogado);
		soe.setInformacoesClinicas("Gerado automaticamente para exame de prova cruzada transfusional para reserva de hemocomponentes.");
		soe.setServidorResponsabilidade(responsavel);
		
		AelSitItemSolicitacoes situacaoItem = this.getExamesFacade().obterSituacaoExamePeloId(situacao.getCodigo());
		
		AelUnfExecutaExames unfExec = this.getExamesFacade()
				.obterAelUnidadeExecutoraExamesPorID(exaSigla, manSeq, ufeUnfSeq);
		AelExames exame = this.getExamesFacade().obterAelExamesPeloId(exaSigla);
		AelMateriaisAnalises material = this.getExamesFacade().obterMaterialAnalisePeloId(manSeq);
		AghUnidadesFuncionais unfFunc = this.getAghuFacade().obterAghUnidadesFuncionaisPorChavePrimaria(ufeUnfSeq);		
		if(unfFunc  == null){
			throw new ApplicationBusinessException(ExameProvaCruzadaRNExceptionCode.UFE_UNF_SEQ_INEXISTENTE,ufeUnfSeq);
		}
		AelItemSolicitacaoExames ise = new AelItemSolicitacaoExames();
		ise.setSituacaoItemSolicitacao(situacaoItem);
		ise.setAelUnfExecutaExames(unfExec);
		ise.setTipoColeta(tipoColeta);
		ise.setIndUsoO2(Boolean.FALSE);
		ise.setIndGeradoAutomatico(Boolean.TRUE);
		ise.setDthrProgramada(new Date());
		ise.setIndImprimiuTicket(Boolean.FALSE);
		ise.setIndCargaContador(Boolean.FALSE);
		ise.setCirurgia(cirurgia);
		ise.setIndPossuiImagem(Boolean.FALSE);
		ise.setIndUsoO2Un(Boolean.FALSE);
		ise.setExame(exame);
		ise.setMaterialAnalise(material);
		ise.setUnidadeFuncional(unfFunc);
		
		soe.addItemSolicitacaoExame(ise);
		
		this.getSolicitacaoExameRN().inserir(soe, nomeMicrocomputador, new Date(), servidorLogado);
	}
	
	/**
	 * ORADB AELK_EXAME_PCT.AELP_GERA_EXAME_PCT
	 * 
	 * @return
	 * @throws MECBaseException 
	 */
	public Boolean gerarExamePCT(AghAtendimentos atendimento,
			MbcCirurgias cirurgia, String nomeMicrocomputador,
			RapServidores responsavel, Boolean validaHemocomponente)
			throws BaseException {
		
		DominioTipoColeta tipoColeta = DominioTipoColeta.U;
		DominioSituacaoItemSolicitacaoExame situacaoIse = DominioSituacaoItemSolicitacaoExame.CS;	
		
		RapServidores servidorLogado = this.getServidorLogadoFacade().obterServidorLogado();
		
		String exaSigla = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_EXAME_PROVA_CRUZADA).getVlrTexto();
		String sitCancelado = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_CANCELADO).getVlrTexto();
		Integer manSeq = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_MATERIAL_PROVA_CRUZADA).getVlrNumerico().intValue();
		Integer validade = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_VALIDADE_AMOSTRA_PCT).getVlrNumerico().intValue();
		Integer mesesNeo = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_MESES_PCT_NEO).getVlrNumerico().intValue();
		Short ufeUnfSeq = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_UNID_EXEC_PROVA_CRUZADA).getVlrNumerico().shortValue();	
		
		Boolean possuiHemocomponente = Boolean.TRUE;
		
		if(validaHemocomponente) {
			possuiHemocomponente = this.verificarCirurgiaPossuiHemocomponentes(cirurgia);
		}
		
		if(possuiHemocomponente) {
			//verificar se paciente menor de <P_MESES_PCT_NEO> meses (neonatal)já possui um exame PCT
			if(this.verificarPacienteNeonatalPossuiPCT(atendimento,
					exaSigla, manSeq, ufeUnfSeq,
					sitCancelado, mesesNeo)) {
				return Boolean.FALSE;
			}
		} else {
			return Boolean.FALSE;
		}
		
		//verificar se existe um exame PCT pendente de realização 
		if(verificarExamePendente(atendimento, exaSigla, sitCancelado, manSeq,
				validade, ufeUnfSeq)) {
			return Boolean.FALSE;
		}
		
		//verificar se o paciente possui amostra válida no banco de sangue
		if(verificarAmostraPendente(atendimento, cirurgia, validade)) {
			return Boolean.FALSE;
		}
		
		situacaoIse = this.retornaSituacaoColeta(atendimento, cirurgia);
		tipoColeta = this.retornaTipoColeta(atendimento, cirurgia);
		
		this.inserir(exaSigla, manSeq, ufeUnfSeq, atendimento, cirurgia, situacaoIse, tipoColeta, nomeMicrocomputador, servidorLogado, responsavel);		
		
		return Boolean.TRUE;
	}

	private Boolean verificarAmostraPendente(AghAtendimentos atendimento,
			MbcCirurgias cirurgia, Integer validade) {
		return getBancoDeSangueFacade().existeAmostraPendente(atendimento.getSeq(), cirurgia.getSeq(), validade);
	}

	private Boolean verificarExamePendente(AghAtendimentos atendimento,
			String exaSigla, String sitCancelado, Integer manSeq,
			Integer validade, Short ufeUnfSeq) {
		return getAelItemSolicitacaoExameDAO().existeExamePendente(atendimento.getSeq(), exaSigla, manSeq, ufeUnfSeq, sitCancelado, validade);
	}
	
	private DominioTipoColeta retornaTipoColeta(AghAtendimentos atendimento, MbcCirurgias cirurgia) throws ApplicationBusinessException {
		DominioTipoColeta tipoColeta = DominioTipoColeta.U;
				
		if (cirurgia.getNaturezaAgenda().equals(
				DominioNaturezaFichaAnestesia.APR)
				|| cirurgia.getNaturezaAgenda().equals(
						DominioNaturezaFichaAnestesia.ELE)
				|| cirurgia.getNaturezaAgenda().equals(
						DominioNaturezaFichaAnestesia.ESP)) {
			AinInternacao internacao = getPesquisaInternacaoFacade().obterInternacaoPacientePorCodPac(cirurgia.getPaciente().getCodigo());			
			
			Date dthrInternacao = new GregorianCalendar(2001, 1, 1).getTime();
			
			if(internacao != null) {
				dthrInternacao = internacao.getDthrInternacao();
			}
				
			if (!DateValidator.validarMesmoDia(cirurgia.getData(), dthrInternacao)) {
				MbcControleEscalaCirurgica controleEscala = getBlocoCirurgicoFacade()
						.obterControleEscalaCirgPorUnidadeDataAgendaTruncadaTipoEscala( 
								cirurgia.getUnidadeFuncional().getSeq(), cirurgia.getData(), DominioTipoEscala.D);
				
				if(controleEscala == null || controleEscala.getDthrGeracaoEscala() == null) { //escala aberta
					tipoColeta = DominioTipoColeta.N;
				}
			}
		}
			
		return tipoColeta;
	}
	
	private DominioSituacaoItemSolicitacaoExame retornaSituacaoColeta(AghAtendimentos atendimento, MbcCirurgias cirurgia) throws ApplicationBusinessException {
		DominioSituacaoItemSolicitacaoExame situacaoIse = DominioSituacaoItemSolicitacaoExame.CS;
		
		if(cirurgia.getOrigemPacienteCirurgia().equals(DominioOrigemPacienteCirurgia.A)) {
			return situacaoIse;
		}
		if (cirurgia.getNaturezaAgenda().equals(
				DominioNaturezaFichaAnestesia.EMG)
				|| cirurgia.getNaturezaAgenda().equals(
						DominioNaturezaFichaAnestesia.URG)) {
			return situacaoIse;
		} else {
			if (cirurgia.getNaturezaAgenda().equals(
					DominioNaturezaFichaAnestesia.APR)
					|| cirurgia.getNaturezaAgenda().equals(
							DominioNaturezaFichaAnestesia.ELE)
					|| cirurgia.getNaturezaAgenda().equals(
							DominioNaturezaFichaAnestesia.ESP)) {
				AinInternacao internacao = getPesquisaInternacaoFacade().obterInternacaoPacientePorCodPac(cirurgia.getPaciente().getCodigo());

				Date dthrInternacao = new GregorianCalendar(2001, 1, 1).getTime();
				
				if(internacao != null) {				
					dthrInternacao = internacao.getDthrInternacao();
				}
				
				if(internacao != null && internacao.getDthrInternacao() == null) {		//Melhoria #42042 - Modificado cfe email com Silvia Gralha 13/02/15			
					internacao.setDthrInternacao(new GregorianCalendar(2001, 1, 1).getTime());
				}
				
			if (DateValidator.validarMesmoDia(cirurgia.getData(), dthrInternacao)) {
				SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
				sdf.setLenient(false);
					
				try {
					AghParametros dtParam = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_HORARIO_LIMITE_COLETA_AMOSTRAS_PCT_INTERNADOS);
					
					Date dtAtual = sdf.parse(sdf.format((internacao == null || internacao.getDthrInternacao() == null) ? dthrInternacao : internacao.getDthrInternacao()));	//Melhoria #42042 - Modificado cfe email com Silvia Gralha 13/02/15					
					Date dtInicial = sdf.parse(sdf.format(DateUtil.obterDataComHoraInical(dthrInternacao)));						
					Date dtFinal = sdf.parse(dtParam.getVlrTexto());
					
					if (DateUtil.entre(dtAtual, dtInicial, dtFinal)){	
						situacaoIse = DominioSituacaoItemSolicitacaoExame.AC;
					}
				}catch (ParseException e) {
					this.logError("Ocorreu erro na formatação de datas PCT: " + e.getMessage());
				}
			} else {					
				situacaoIse = DominioSituacaoItemSolicitacaoExame.AC;
				}
			}
		}
		return situacaoIse;
	}	

	/**
	 * Regra para verificar se a cirurgia possui hemocomponentes associados ao agendamento
	 */
	private Boolean verificarCirurgiaPossuiHemocomponentes(MbcCirurgias cirurgia) {		
		Boolean possuiHemocomp = Boolean.FALSE;
		
		if(getBlocoCirurgicoFacade().verificarCirurgiaPossuiHemocomponentes(cirurgia.getSeq())) {
			possuiHemocomp = Boolean.TRUE;
		} else {
			if(getBlocoCirurgicoCadastroApoioFacade().verificarCirurgiaPossuiComponenteSanguineo(cirurgia.getSeq())) {
				possuiHemocomp = Boolean.TRUE;
			}
		}
		return possuiHemocomp;
	}
	
	private Boolean verificarPacienteNeonatalPossuiPCT(AghAtendimentos atendimento,
			String exaSigla, Integer manSeq, Short ufeUnfSeq, String situacaoCancelado, Integer meses) {
		
		return getPacienteFacade().verificarPacienteNeonatalPossuiExamePCT(atendimento.getSeq(), exaSigla, manSeq, ufeUnfSeq, situacaoCancelado, meses);
	}
	
	/**
	 * ORADB AELK_EXAME_PCT.AELP_CANCELA_CIRURGIA
	 * 
	 * @return
	 * @throws MECBaseException 
	 */

	public void cancelarItemSolicitacao(AghAtendimentos atendimento,
			MbcCirurgias cirurgia, String nomeMicrocomputador) throws BaseException {
		final AghParametros parametroExamePCT = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_EXAME_PROVA_CRUZADA);
		final AghParametros parametroSitColetadoSolic = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_COLETADO_SOLIC);
		final AghParametros parametroSitAColetar = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_A_COLETAR);
		final AghParametros parametroSitCancelado = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_CANCELADO);
		final AghParametros parametroMocPct = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_MOC_CANCELA_PCT);

		String[] situacaoIse = {parametroSitAColetar.getVlrTexto(), parametroSitColetadoSolic.getVlrTexto()};

		AelItemSolicitacaoExames ise = getAelItemSolicitacaoExameDAO()
				.obterItemSolicitacaoExamePorAtendimentoCirurgico(
						atendimento.getSeq(), cirurgia.getSeq(), parametroExamePCT.getVlrTexto(),
						situacaoIse, Boolean.TRUE);

		if(ise != null) {
			Short totalExtrato = getAelExtratoItemSolicitacaoDAO().buscarMaiorSeqp(ise.getId().getSoeSeq(), ise.getId().getSeqp());

			if(totalExtrato == 1) {
				AelSitItemSolicitacoes situacao = this.getExamesFacade().pesquisaSituacaoItemExame(parametroSitCancelado.getVlrTexto());
				AelMotivoCancelaExames motivo = this.getExamesFacade().obterMotivoCancelamentoPeloId(parametroMocPct.getVlrNumerico().shortValue());
				
				ise.setSituacaoItemSolicitacao(situacao);
				ise.setAelMotivoCancelaExames(motivo);

				getSolicitacaoExameFacade().atualizar(ise, nomeMicrocomputador);
			}
		}
	}
	
	protected AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO() {
		return aelItemSolicitacaoExameDAO;
	}
	
	protected AelExtratoItemSolicitacaoDAO getAelExtratoItemSolicitacaoDAO() {
		return aelExtratoItemSolicitacaoDAO;
	}
	
	protected SolicitacaoExameRN getSolicitacaoExameRN() {
		return solicitacaoExameRN;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}
	
	protected IPesquisaInternacaoFacade getPesquisaInternacaoFacade() {
		return pesquisaInternacaoFacade;
	}
	
	protected IBlocoCirurgicoCadastroApoioFacade getBlocoCirurgicoCadastroApoioFacade() {
		return blocoCirurgicoCadastroApoioFacade;
	}
	
	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}
	
	protected IBancoDeSangueFacade getBancoDeSangueFacade() {
		return bancoDeSangueFacade;
	}
	
	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}
	
	protected IExamesFacade getExamesFacade() {
		return examesFacade;
	}
	
	protected ISolicitacaoExameFacade getSolicitacaoExameFacade() {
		return solicitacaoExameFacade;
	}

	public IServidorLogadoFacade getServidorLogadoFacade() {
		return servidorLogadoFacade;
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}
}