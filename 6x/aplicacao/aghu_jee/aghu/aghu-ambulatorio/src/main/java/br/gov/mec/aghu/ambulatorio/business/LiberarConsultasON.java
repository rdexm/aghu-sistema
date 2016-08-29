package br.gov.mec.aghu.ambulatorio.business;

import java.math.BigDecimal;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.dao.AacCaracteristicaGradeDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacCondicaoAtendimentoDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacConsultaProcedHospitalarDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacConsultasJnDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacGradeAgendamenConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacPagadorDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacSituacaoConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacTipoAgendamentoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamAnamnesesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamControlesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamEvolucoesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamExtratoControlesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamLogEmUsosDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamNotaAdicionalAnamnesesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamNotaAdicionalEvolucoesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamSituacaoAtendimentosDAO;
import br.gov.mec.aghu.ambulatorio.vo.BuscaFormaAnteriorVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioCaracEspecialidade;
import br.gov.mec.aghu.dominio.DominioEstornoConsulta;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoControle;
import br.gov.mec.aghu.dominio.DominioSituacaoProcedimentoAmbulatorio;
import br.gov.mec.aghu.exameselaudos.business.IExamesLaudosFacade;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AacCaracteristicaGrade;
import br.gov.mec.aghu.model.AacCaracteristicaGradeId;
import br.gov.mec.aghu.model.AacConsultaProcedHospitalar;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacConsultasJn;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.AacSituacaoConsultas;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FatProcedAmbRealizado;
import br.gov.mec.aghu.model.MamControles;
import br.gov.mec.aghu.model.MamExtratoControles;
import br.gov.mec.aghu.model.MamExtratoControlesId;
import br.gov.mec.aghu.model.MamLogEmUsos;
import br.gov.mec.aghu.model.MamSituacaoAtendimentos;
import br.gov.mec.aghu.model.MptAgendaPrescricao;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.TipoOperacaoEnum;

@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength"})
@Stateless
public class LiberarConsultasON extends BaseBusiness {

    private static final long serialVersionUID = -5719622838001686394L;

    private static final String EXCECAO_LANCADA = "Exceção lançada: ";

    @EJB
    private ProcedimentoConsultaRN procedimentoConsultaRN;

    @EJB
    private AmbulatorioRN ambulatorioRN;

    @EJB
    private AmbulatorioConsultaRN ambulatorioConsultaRN;

    @EJB
    private MarcacaoConsultaRN marcacaoConsultaRN;

    private static final Log LOG = LogFactory.getLog(LiberarConsultasON.class);

    @Override
    @Deprecated
    protected Log getLogger() {
	return LOG;
    }

    @EJB
    private IPermissionService permissionService;

    @Inject
    private MamEvolucoesDAO mamEvolucoesDAO;

    @EJB
    private IFaturamentoFacade faturamentoFacade;

    @EJB
    private IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade;

    @Inject
    private AacSituacaoConsultasDAO aacSituacaoConsultasDAO;

    @EJB
    private IAmbulatorioFacade ambulatorioFacade;

    @Inject
    private AacCaracteristicaGradeDAO aacCaracteristicaGradeDAO;

    @Inject
    private MamNotaAdicionalEvolucoesDAO mamNotaAdicionalEvolucoesDAO;

    @Inject
    private MamControlesDAO mamControlesDAO;

    @Inject
    private MamExtratoControlesDAO mamExtratoControlesDAO;

    @Inject
    private MamLogEmUsosDAO mamLogEmUsosDAO;

    @Inject
    private AacCondicaoAtendimentoDAO aacCondicaoAtendimentoDAO;

    @Inject
    private AacPagadorDAO aacPagadorDAO;

    @Inject
    private MamAnamnesesDAO mamAnamnesesDAO;

    @Inject
    private AacTipoAgendamentoDAO aacTipoAgendamentoDAO;

    @Inject
    private MamSituacaoAtendimentosDAO mamSituacaoAtendimentosDAO;

    @Inject
    private AacConsultaProcedHospitalarDAO aacConsultaProcedHospitalarDAO;

    @Inject
    private AacConsultasDAO aacConsultasDAO;

    @EJB
    private IExamesLaudosFacade examesLaudosFacade;

    @EJB
    private IPacienteFacade pacienteFacade;

    @Inject
    private MamNotaAdicionalAnamnesesDAO mamNotaAdicionalAnamnesesDAO;

    @EJB
    private IParametroFacade parametroFacade;

    @EJB
    private IAghuFacade aghuFacade;

    @Inject
    private AacConsultasJnDAO aacConsultasJnDAO;

    @Inject
    private AacGradeAgendamenConsultasDAO aacGradeAgendamenConsultasDAO;

    public enum LiberarConsultasONExceptionCode implements BusinessExceptionCode {
	ERRO_LIBERACAO_CONSULTA, ERRO_CARREGAR_CONSULTA, ERRO_AO_REMOVER_CONSULTA, AAC_00124, AAC_00669, AEL_00510, FAT_00512, AGH_00455, MAM_01973, MAM_01975, MAM_01976, MAM_01977, MAM_03668, MAM_03671, MPT_00605, MAM_01527, MAM_03667, MAM_01528, MAM_01529, MAM_01981, MAM_01776, MAM_03670, PARAMETRO_INVALIDO;
    }

    /**
     * Libera a consulta cujo código é passado por parâmetro.
     * 
     * @param numeroConsulta
     * @throws BaseException
     */
    @SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
    public void liberarConsulta(Integer numeroConsulta, String nomeMicrocomputador, final Date dataFimVinculoServidor, Boolean possuiReconsulta) throws BaseException {
	if (numeroConsulta == null) {
	    throw new IllegalArgumentException("O número da consulta é obrigatório");
	}

	// IAmbulatorioFacade ambulatorioFacade = getAmbulatorioFacade();
	AmbulatorioConsultaRN ambulatorioConsultaRN = getAmbulatorioConsultaRN();
	ProcedimentoConsultaRN procedimentoConsultaRN = getProcedimentoConsultaRN();
	AacConsultasDAO aacConsultasDAO = getAacConsultasDAO();
	AacConsultaProcedHospitalarDAO aacConsultaProcedHospitalarDAO = getAacConsultaProcedHospitalarDAO();
	Short vPgdSeqOld = null;
	Short vTagSeqOld = null;
	Short vCaaSeqOld = null;
	Integer vPjqSeq = null;
	AacConsultas consulta = aacConsultasDAO.obterPorChavePrimaria(numeroConsulta);
	if (consulta == null) {
	    throw new ApplicationBusinessException(LiberarConsultasONExceptionCode.ERRO_CARREGAR_CONSULTA, String.valueOf(numeroConsulta));
	}

	// Verifica se existem regitros ambulatoriais (no MAM) para esta consulta, se existe e situação é agendado exclui, senão dá mensagem de erro
	// Se unidade de emergencia, volta a situação de aguardando para agendado
	this.verificaRegitrosAmbulatoriais(numeroConsulta, consulta.getPaciente() != null ? consulta.getPaciente().getCodigo() : null,
		consulta.getGradeAgendamenConsulta() != null ? consulta.getGradeAgendamenConsulta().getSeq() : null, nomeMicrocomputador);

	// String liberarPrimeiraConsultaParam = this.buscarVlrTextoAghParametro(AghuParametrosEnum.P_ACAO_LIBERAR_PRIM_CONSULTA);
	Short vPgd = this.buscarVlrShortAghParametro(AghuParametrosEnum.P_PAGADOR_SUS);
	Short vCaa = this.buscarVlrShortAghParametro(AghuParametrosEnum.P_CAA_CONSULTA);
	Short vTag = this.buscarVlrShortAghParametro(AghuParametrosEnum.P_TAG_SMS);

	if (CoreUtil.igual(vPgd, consulta.getPagador() != null ? consulta.getPagador().getSeq() : null)
		&& CoreUtil.igual(vTag, consulta.getTipoAgendamento() != null ? consulta.getTipoAgendamento().getSeq() : null)
		&& CoreUtil.igual(vCaa, consulta.getCondicaoAtendimento() != null ? consulta.getCondicaoAtendimento().getSeq() : null)
		&& !this.getPermissionService().usuarioTemPermissao(obterLoginUsuarioLogado(), "liberaConsulta", "liberar")) {
	    throw new ApplicationBusinessException(LiberarConsultasONExceptionCode.AAC_00669);
	}

	// Verifica competência do faturamento
	ambulatorioConsultaRN.verificarConsultaFat(numeroConsulta);
	// Ver se na atendimentos há relação com internação, doação, hospital dia, atend. urgência. Neste caso não será deletado o atendimento
	ambulatorioConsultaRN.verificarExclusaoAtendimento(numeroConsulta);
	// Verifica se existem exames solicitados para a consulta (não cancelados)
	TipoOperacaoEnum operacaoAtendimento = this.verificaExames(numeroConsulta);
	this.deletaFaturamento(numeroConsulta, nomeMicrocomputador, dataFimVinculoServidor);
	try {
	    List<AacConsultaProcedHospitalar> consultasProcedimentosHospitalares = aacConsultaProcedHospitalarDAO
		    .buscarConsultaProcedHospPorNumeroConsulta(numeroConsulta);
	    if (consultasProcedimentosHospitalares != null && !consultasProcedimentosHospitalares.isEmpty()) {
		for (AacConsultaProcedHospitalar consultaProcedimentoHospitalar : consultasProcedimentosHospitalares) {
					procedimentoConsultaRN.removerProcedimentoConsulta(consultaProcedimentoHospitalar, true, nomeMicrocomputador);
		}
	    }
	} catch (Exception e) {
	    this.logError("A seguinte exceção deve ser ignorada, seguindo a lógica migrada do PL/SQL: ", e);
	}
	if (TipoOperacaoEnum.DELETE.equals(operacaoAtendimento)) {
	    this.deletaAtendimento(numeroConsulta, nomeMicrocomputador, dataFimVinculoServidor);
	} else if (TipoOperacaoEnum.UPDATE.equals(operacaoAtendimento)) {
	    this.updateAtendimento(numeroConsulta, nomeMicrocomputador, dataFimVinculoServidor);
	}
	// AACP_VER_ANTERIOR (#52563)
	if(possuiReconsulta) {
		List<AacConsultas> reconsultas = aacConsultasDAO.pesquisarConsultacPorConsultaNumeroAnterior(numeroConsulta);
		for(AacConsultas reconsulta : reconsultas) {
			Integer numeroConsultaAnterior = ambulatorioConsultaRN.obterConsultaAnteriorPaciente(consulta);
			if (numeroConsultaAnterior != null) {
				AacConsultas consultaAnterior = aacConsultasDAO.obterConsulta(numeroConsultaAnterior);
				reconsulta.setConsulta(consultaAnterior);
			}
			else {
				reconsulta.setConsulta(null);
			}
		}
	}

	/*
	 * ATENÇÃO: Este trecho foi retirado da linha 115 e trazido pra cá em função do delete que é feito no objeto MamControle. Por haver um
	 * relacionamento de one to one deste objeto com AacConsultas estava dando erro ao remover o MamControle daquela posição quando chegava no
	 * primeiro flush (devido à remoção da AaacConsulta se encontrar só neste ponto). Agora por não haver nenhum flush entre a exclusão do
	 * controle e a exclusão da consulta o problema não ocorre mais. Esta alteração foi feita para resolver a issue #18608
	 */
	LiberarConsultasONExceptionCode retornoExcluiConsulta = this.excluiConsulta(numeroConsulta);
	if (retornoExcluiConsulta != null) {
	    throw new ApplicationBusinessException(retornoExcluiConsulta);
	}/* -------------------------------------------------------------------- */
	String caracteristicaAgendaPrescricaoQuimio = this.buscarVlrTextoAghParametro(AghuParametrosEnum.P_CARACT_PRESC_QUIMIO_AAC);

	if (consulta.getExcedeProgramacao() != null && consulta.getExcedeProgramacao()) {
	    if (Boolean.TRUE.equals(verificaCaracteristicaGrade(consulta.getGradeAgendamenConsulta() != null ? consulta
		    .getGradeAgendamenConsulta().getSeq() : null, caracteristicaAgendaPrescricaoQuimio))) {
		this.liberaAgenda(numeroConsulta, TipoOperacaoEnum.DELETE);
	    }  	
	    try {
			ambulatorioConsultaRN.excluirConsulta(consulta, nomeMicrocomputador, new Date(), false);
			this.flush();
	    } catch (Exception e) {
			logError(EXCECAO_LANCADA, e);
			String pkEstourada = verificarConstraintViolationException(e);
			if(pkEstourada != null){
				throw new ApplicationBusinessException(LiberarConsultasONExceptionCode.ERRO_AO_REMOVER_CONSULTA, pkEstourada);
			} else {
				throw new ApplicationBusinessException(LiberarConsultasONExceptionCode.ERRO_AO_REMOVER_CONSULTA, e.getMessage());
			}			
	    }
	} else {
	    if (Boolean.TRUE.equals(verificaCaracteristicaGrade(consulta.getGradeAgendamenConsulta() != null ? consulta
		    .getGradeAgendamenConsulta().getSeq() : null, caracteristicaAgendaPrescricaoQuimio))) {
		this.liberaAgenda(numeroConsulta, TipoOperacaoEnum.DELETE);
	    }

	    // Busca a forma original
	    BuscaFormaAnteriorVO vo = this.buscaFormaAnterior(numeroConsulta, vPgdSeqOld, vTagSeqOld, vCaaSeqOld);
	    if (vo != null) {
		vPgdSeqOld = vo.getPgdSeq();
		vTagSeqOld = vo.getTagSeq();
		vCaaSeqOld = vo.getCaaSeq();
	    }
	    vPjqSeq = consulta.getProjetoPesquisa() != null ? consulta.getProjetoPesquisa().getSeq() : null;
	    Short pagadorPesquisa = this.buscarVlrShortAghParametro(AghuParametrosEnum.P_PAGADOR_PESQUISA);
	    if (vPgdSeqOld != null && !vPgdSeqOld.equals(pagadorPesquisa)) {
		// Pesquisa
		vPjqSeq = null;
	    }
	    consulta = aacConsultasDAO.obterPorChavePrimaria(numeroConsulta);
	    if (consulta != null) {
		try {
		    aacConsultasDAO.desatachar(consulta);
		    AacConsultas consultaAnterior = aacConsultasDAO.obterPorChavePrimaria(numeroConsulta);
		    aacConsultasDAO.desatachar(consultaAnterior);
		    String codigoIndSituacaoConsultaLivre = this.buscarVlrTextoAghParametro(AghuParametrosEnum.P_SITUACAO_CONS_LIVRE);
		    AacSituacaoConsultas situacaoConsultaLivre = StringUtils.isNotBlank(codigoIndSituacaoConsultaLivre) ? this
			    .getAacSituacaoConsultasDAO().obterPorChavePrimaria(codigoIndSituacaoConsultaLivre) : null;
		    consulta.setSituacaoConsulta(situacaoConsultaLivre);
		    consulta.setConvenioSaudePlano(null);
		    consulta.setRetorno(null);
		    consulta.setAtendimento(null);
		    consulta.setPaciente(null);
		    consulta.setMotivo(null);
		    consulta.setDthrInicio(null);
		    consulta.setDthrFim(null);
		    consulta.setServidorConsultado(null);
		    consulta.setServidorAtendido(null);
		    consulta.setServidorMarcacao(null);
		    consulta.setIndPendenciaFat(null);
		    consulta.setIndFuncionario(null);
		    consulta.setIndImpressaoBa(null);
		    consulta.setCodCentral(null);
		    consulta.setPostoSaude(null);
		    consulta.setConsulta(null);
		    consulta.setDthrMarcacao(null);
		    consulta.setProjetoPesquisa(vPjqSeq != null ? this.getExamesLaudosFacade().obterAelProjetoPesquisasPorChavePrimaria(
			    vPjqSeq) : null);
		    consulta.setGrupoAtendAmb(null);
		    if (vPgdSeqOld != null) {
			consulta.setPagador(this.getAacPagadorDAO().obterPorChavePrimaria(vPgdSeqOld));
		    }
		    if (vTagSeqOld != null) {
			consulta.setTipoAgendamento(this.getAacTipoAgendamentoDAO().obterPorChavePrimaria(vTagSeqOld));
		    }
		    if (vCaaSeqOld != null) {
			consulta.setCondicaoAtendimento(this.getAacCondicaoAtendimentoDAO().obterPorChavePrimaria(vCaaSeqOld));
		    }
		    ambulatorioConsultaRN.atualizarConsulta(consultaAnterior, consulta, false, nomeMicrocomputador, new Date(), false, true);
		    this.flush();
		} catch (ApplicationBusinessException e) {
		    LOG.error(e.getMessage(), e);
		    throw e;
		} catch (Exception e) {
		    logError(EXCECAO_LANCADA, e);
		    throw new ApplicationBusinessException(LiberarConsultasONExceptionCode.ERRO_LIBERACAO_CONSULTA, e.getMessage());
		}
	    } else {
		throw new ApplicationBusinessException(LiberarConsultasONExceptionCode.ERRO_LIBERACAO_CONSULTA, "Código " + numeroConsulta
			+ " não existe.");
	    }
	}
    }
    
    /**
     * MEtodo criado para verificar chaves estouradas, e mostrar as mesmas, essa funcionalidade foi adicionada para o 
     * AGHUse ficar com as mesmas mensagens do AGHWeb 
     * @param ex
     * @return Chave estourada se houver, null se a exeção não for de chave primaria
     */
	private String verificarConstraintViolationException(Exception ex) {
		String retorno = null;
		Throwable e = ex.getCause();
		if (e instanceof PersistenceException) {
			e = e.getCause();
			if (e instanceof ConstraintViolationException) {
				e = e.getCause();
				if(e instanceof SQLIntegrityConstraintViolationException){
					retorno = e.getMessage();
				}
			}
		}
		return retorno;
	}

    /**
     * ORADB AACP_DELETA_ATD
     * 
     * @param pNewConNumero
     */
    public void deletaAtendimento(Integer pNewConNumero, String nomeMicrocomputador, final Date dataFimVinculoServidor)
	    throws BaseException {
		IAghuFacade aghuFacade = this.getAghuFacade();
		IPacienteFacade pacienteFacade = this.getPacienteFacade();
	
		try {
		    List<AghAtendimentos> atendimentos = aghuFacade
			    .listarAtendimentosPorConsultaComInternacaoAtendumentoUrgenciaAtendumentoPacienteExternoNulo(pNewConNumero);	
		    if (atendimentos != null && !atendimentos.isEmpty()) {
			for (AghAtendimentos atendimento : atendimentos) {
			    pacienteFacade.removerAtendimento(atendimento, nomeMicrocomputador);
			}
		    }
		} catch (Exception e) {
		    logError(EXCECAO_LANCADA, e);
		    List<AghAtendimentos> atendimentos = aghuFacade.listarAtendimentosPorNumeroConsulta(pNewConNumero);	
		    if (atendimentos != null && !atendimentos.isEmpty()) {
			for (AghAtendimentos atendimento : atendimentos) {
			    AghAtendimentos atendimentoAnterior = pacienteFacade.clonarAtendimento(atendimento);
			    pacienteFacade.persistirAtendimento(atendimento, atendimentoAnterior, nomeMicrocomputador, dataFimVinculoServidor);
			}
		    }
		}
    }

    /**
     * ORADB AACC_VER_CARACT_GRD
     * 
     * Verifica se uma grade possui determinada caracteristica. Se tiver retorna
     * true, se não tiver retorna false.
     * 
     * @param pGrdSeq
     * @param pCaracteristica
     * @return
     */
    public Boolean verificaCaracteristicaGrade(Integer pGrdSeq, String pCaracteristica) {
	AacCaracteristicaGrade caracteristicaGrade = getAacCaracteristicaGradeDAO().obterPorChavePrimaria(
		new AacCaracteristicaGradeId(pGrdSeq, pCaracteristica));
	return caracteristicaGrade != null;
    }

    /**
     * ORADB MPTK_AGENDA.LIBERA_AGENDA
     * 
     * @param pConNumero
     * @param acao
     * @throws BaseException
     */
    public void liberaAgenda(Integer pConNumero, TipoOperacaoEnum acao) throws BaseException {
	try {
	    IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade = getProcedimentoTerapeuticoFacade();
	    List<MptAgendaPrescricao> agendasPrescricoes = this.getProcedimentoTerapeuticoFacade()
		    .listarAgendasPrescricaoPorNumeroConsulta(pConNumero);
	    if (agendasPrescricoes != null && !agendasPrescricoes.isEmpty()) {
		for (MptAgendaPrescricao agendaPrescricao : agendasPrescricoes) {
		    agendaPrescricao.setConNumero(TipoOperacaoEnum.DELETE.equals(acao) ? null : pConNumero);
		    agendaPrescricao.setSituacao(DominioSituacao.I);
		    agendaPrescricao.setIndEstornoConsulta(DominioEstornoConsulta.S);
		    procedimentoTerapeuticoFacade.atualizaMptAgendaPrescricao(agendaPrescricao, true);
		}
		this.flush();
	    }
	} catch (Exception e) {
	    logError(EXCECAO_LANCADA, e);
	    throw new ApplicationBusinessException(LiberarConsultasONExceptionCode.MPT_00605);
	}
    }
    /**
     * ORADB MAMC_VER_EXISTE_AMB
     * 
     * @param pConNumero
     * @return
     */
    public Boolean verificaExisteAmbulatorio(Integer pConNumero) {
    	Long count = getMamControlesDAO().listaControlesPorNumeroConsultaCount(pConNumero);
	return count != null && count > 0;
    }

    /**
     * ORADB P_DELETA_FATURAMENTO
     * 
     * @param pConsulta
     * @throws BaseException
     */
    public void deletaFaturamento(Integer pConsulta, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
	IFaturamentoFacade faturamentoFacade = this.getFaturamentoFacade();

	try {
	    List<FatProcedAmbRealizado> listaProcedimentosAmbulatoriaisRealizados = faturamentoFacade
		    .listarProcedimentosAmbulatoriaisRealizados(pConsulta);
	    if (listaProcedimentosAmbulatoriaisRealizados != null && !listaProcedimentosAmbulatoriaisRealizados.isEmpty()) {
		for (FatProcedAmbRealizado procedimentoAmbulatorialRealizado : listaProcedimentosAmbulatoriaisRealizados) {
		    faturamentoFacade.removerProcedimentosAmbulatoriaisRealizados(procedimentoAmbulatorialRealizado, nomeMicrocomputador,
			    dataFimVinculoServidor);
		}
	    }
	} catch (Exception e) {
	    logError(EXCECAO_LANCADA, e);
	    DominioSituacaoProcedimentoAmbulatorio[] situacoesIgnoradas = new DominioSituacaoProcedimentoAmbulatorio[] {
		    DominioSituacaoProcedimentoAmbulatorio.APRESENTADO, DominioSituacaoProcedimentoAmbulatorio.ENCERRADO,
		    DominioSituacaoProcedimentoAmbulatorio.TRANSFERIDO };

	    List<FatProcedAmbRealizado> listaProcedimentosAmbulatoriaisRealizados = faturamentoFacade
		    .listarProcedimentosAmbulatoriaisRealizados(pConsulta, situacoesIgnoradas);

	    if (listaProcedimentosAmbulatoriaisRealizados != null && !listaProcedimentosAmbulatoriaisRealizados.isEmpty()) {
		for (FatProcedAmbRealizado procedimentoAmbulatorialRealizado : listaProcedimentosAmbulatoriaisRealizados) {
		    FatProcedAmbRealizado oldAmbRealizado = faturamentoFacade
			    .clonarFatProcedAmbRealizado(procedimentoAmbulatorialRealizado);
		    procedimentoAmbulatorialRealizado.setConsultaProcedHospitalar(null);
		    procedimentoAmbulatorialRealizado.setSituacao(DominioSituacaoProcedimentoAmbulatorio.CANCELADO);
		    procedimentoAmbulatorialRealizado.setAtendimento(null);
		    faturamentoFacade.atualizarProcedimentosAmbulatoriaisRealizados(procedimentoAmbulatorialRealizado, oldAmbRealizado,
			    nomeMicrocomputador, dataFimVinculoServidor);
		}
	    } else {
		throw new ApplicationBusinessException(LiberarConsultasONExceptionCode.FAT_00512);
	    }
	}
    }

    /**
     * ORADB P_UPDATE_ATD
     * 
     * @param pNroConsulta
     * @throws BaseException
     */
    public void updateAtendimento(Integer pNroConsulta, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
	try {
	    IAghuFacade aghuFacade = getAghuFacade();
	    IPacienteFacade pacienteFacade = this.getPacienteFacade();
	    List<AghAtendimentos> atendimentos = aghuFacade.listarAtendimentosPorNumeroConsulta(pNroConsulta);

	    if (atendimentos != null && !atendimentos.isEmpty()) {
		for (AghAtendimentos atendimento : atendimentos) {
		    AghAtendimentos atendimentoOld = pacienteFacade.clonarAtendimento(atendimento);
		    atendimento.setConsulta(null);
		    pacienteFacade.persistirAtendimento(atendimento, atendimentoOld, nomeMicrocomputador, dataFimVinculoServidor);
		}
		this.flush();
	    }
	} catch (Exception e) {
	    logError(EXCECAO_LANCADA, e);
	    throw new ApplicationBusinessException(LiberarConsultasONExceptionCode.AGH_00455, e.getMessage());
	}
    }

    /**
     * ORADB P_VERIFICA_EXAMES
     * 
     * @param pConNumero
     * @return
     * @throws BaseException
     */
    public TipoOperacaoEnum verificaExames(Integer pConNumero) throws BaseException {
	IExamesLaudosFacade examesLaudosFacade = getExamesLaudosFacade();
	TipoOperacaoEnum operacaoAtendimento = null;

	Long count = examesLaudosFacade.listarSolicitacoesExamesCount(pConNumero);
	if (count == null || count == 0) {
	    operacaoAtendimento = TipoOperacaoEnum.DELETE;
	} else {
	    String codigoSituacaoItemSolicitacaoExameCancelado = this.buscarVlrTextoAghParametro(AghuParametrosEnum.P_SITUACAO_CANCELADO);
	    if (StringUtils.isBlank(codigoSituacaoItemSolicitacaoExameCancelado)) {
		throw new ApplicationBusinessException(LiberarConsultasONExceptionCode.AEL_00510);
	    }
	    count = examesLaudosFacade.listarSolicitacoesExamesCount(pConNumero, codigoSituacaoItemSolicitacaoExameCancelado);
	    if (count == null || count == 0) {
		operacaoAtendimento = TipoOperacaoEnum.UPDATE;
	    } else {
		throw new ApplicationBusinessException(LiberarConsultasONExceptionCode.AAC_00124);
	    }
	}
	return operacaoAtendimento;
    }

    /**
     * ORADB MAMP_AMBU_AGENDADO
     * 
     * @param pConNumero
     * @param codigoPaciente
     * @param grdSeq
     * @throws BaseException
     */
    @SuppressWarnings("PMD.ExcessiveMethodLength")
    public void verificaRegitrosAmbulatoriais(Integer pConNumero, Integer codigoPaciente, Integer grdSeq, String nomeMicrocomputador)
	    throws BaseException {
	IAghuFacade aghuFacade = getAghuFacade();
	IPacienteFacade pacienteFacade = getPacienteFacade();
	MarcacaoConsultaRN marcacaoConsultaRN = getMarcacaoConsultaRN();

	AacConsultasDAO aacConsultasDAO = getAacConsultasDAO();
	MamControlesDAO mamControlesDAO = getMamControlesDAO();
	MamSituacaoAtendimentosDAO mamSituacaoAtendimentosDAO = getMamSituacaoAtendimentosDAO();
	Short vSeqSitAgendado = null;
	Long vCtlSeq = null;
	Short vExcSeqp = null;
	Short vEspSeq = null;
	Boolean vCaract = null;

	AacGradeAgendamenConsultas gradeAgendamentoConsulta = getAacGradeAgendamenConsultasDAO().obterPorChavePrimaria(grdSeq);
	if (gradeAgendamentoConsulta != null) {
	    vEspSeq = gradeAgendamentoConsulta.getEspecialidade().getSeq();
	}

	if (vEspSeq != null) {
	    Long count = aghuFacade.listarCaracteristicasEspecialidadesCount(vEspSeq, DominioCaracEspecialidade.EMERGENCIA);
	    vCaract = count != null && count > 0;
	}

	if (Boolean.TRUE.equals(vCaract)) {
	    // Busca parâmetro do "Paciente agendado"
	    vSeqSitAgendado = this.buscarVlrShortAghParametro(AghuParametrosEnum.P_SEQ_SIT_AGENDADO);
	    if (vSeqSitAgendado == null) {
		throw new ApplicationBusinessException(LiberarConsultasONExceptionCode.MAM_01973);
	    }
	    MamSituacaoAtendimentos situacaoAgendado = mamSituacaoAtendimentosDAO.obterPorChavePrimaria(vSeqSitAgendado);
	    List<MamControles> listaControles = mamControlesDAO.listaControlesPorNumeroConsulta(pConNumero);
	    if (listaControles != null && !listaControles.isEmpty()) {
		vCtlSeq = listaControles.get(0).getSeq();
	    }

	    if (vCtlSeq != null) {
		// Já existia controle
		try {
		    MamControles controle = mamControlesDAO.obterPorChavePrimaria(vCtlSeq);

		    controle.setDthrMovimento(new Date());
		    controle.setSituacaoAtendimento(situacaoAgendado);
		    marcacaoConsultaRN.atualizarControles(controle, nomeMicrocomputador);
		    this.flush();
		} catch (BaseException e) {
		    logError(EXCECAO_LANCADA, e);
		    throw e;
		} catch (Exception e) {
		    logError(EXCECAO_LANCADA, e);
		    throw new ApplicationBusinessException(LiberarConsultasONExceptionCode.MAM_01977, e.getMessage());
		}
		vExcSeqp = this.getMamExtratoControlesDAO().buscaProximoSeqp(vCtlSeq);
		try {
		    MamExtratoControles extrato = new MamExtratoControles();
		    extrato.setId(new MamExtratoControlesId(vCtlSeq, vExcSeqp));
		    extrato.setDthrMovimento(new Date());
		    extrato.setSituacaoAtendimento(situacaoAgendado);
		    marcacaoConsultaRN.inserirExtratoControles(extrato, nomeMicrocomputador);
		    this.flush();
		} catch (BaseException e) {
		    logError(EXCECAO_LANCADA, e);
		    throw e;
		} catch (Exception e) {
		    logError(EXCECAO_LANCADA, e);
		    throw new ApplicationBusinessException(LiberarConsultasONExceptionCode.MAM_01977, e.getMessage());
		}
	    } else {
		try {
		    MamControles controle = new MamControles();
		    controle.setDthrMovimento(new Date());
		    controle.setSituacao(DominioSituacaoControle.L);
		    controle.setPaciente(pacienteFacade.obterPacientePorCodigo(codigoPaciente));
		    controle.setConsulta(aacConsultasDAO.obterPorChavePrimaria(pConNumero));
		    controle.setSituacaoAtendimento(situacaoAgendado);
		    marcacaoConsultaRN.inserirControles(controle, nomeMicrocomputador);
		    this.flush();
		    vCtlSeq = controle.getSeq();
		} catch (BaseException e) {
		    logError(EXCECAO_LANCADA, e);
		    throw e;
		} catch (Exception e) {
		    logError(EXCECAO_LANCADA, e);
		    throw new ApplicationBusinessException(LiberarConsultasONExceptionCode.MAM_01975, e.getMessage());
		}
		try {
		    MamExtratoControles extrato = new MamExtratoControles();
		    extrato.setId(new MamExtratoControlesId(vCtlSeq, (short) 1));
		    extrato.setDthrMovimento(new Date());
		    extrato.setSituacaoAtendimento(situacaoAgendado);
		    marcacaoConsultaRN.inserirExtratoControles(extrato, nomeMicrocomputador);
		    this.flush();
		} catch (BaseException e) {
		    logError(EXCECAO_LANCADA, e);
		    throw e;
		} catch (Exception e) {
		    logError(EXCECAO_LANCADA, e);
		    throw new ApplicationBusinessException(LiberarConsultasONExceptionCode.MAM_01976, e.getMessage());
		}

	    }
	}
    }
    /**
     * ORADB MAMP_EXCLUI_CONSULTA
     * 
     * @param pConNumero
     * @return
     * @throws BaseException
     */
    @SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
    public LiberarConsultasONExceptionCode excluiConsulta(Integer pConNumero) throws BaseException {
	AmbulatorioRN ambulatorioRN = getAmbulatorioRN();
	MamEvolucoesDAO mamEvolucoesDAO = getMamEvolucoesDAO();
	MamAnamnesesDAO mamAnamnesesDAO = getMamAnamnesesDAO();
	MamNotaAdicionalAnamnesesDAO mamNotaAdicionalAnamnesesDAO = getMamNotaAdicionalAnamnesesDAO();
	MamNotaAdicionalEvolucoesDAO mamNotaAdicionalEvolucoesDAO = getMamNotaAdicionalEvolucoesDAO();
	LiberarConsultasONExceptionCode msgRetorno = null;
	Boolean vAchouEvolucao = null;
	Boolean vAchouInformacao = null;
	Long count = null;

	// Se existe registro no ambulatório
	if (Boolean.TRUE.equals(this.verificaExisteAmbulatorio(pConNumero))) {
	    // Se ultima situação é agendado
	    if (Boolean.TRUE.equals(this.buscaAgendado(pConNumero))) {
		// Apaga os registros existentes
		try {
		    List<MamExtratoControles> extratosControle = getMamExtratoControlesDAO().listarExtratosControlesPorNumeroConsulta(
			    pConNumero);
		    if (extratosControle != null && !extratosControle.isEmpty()) {
			for (MamExtratoControles extratoControle : extratosControle) {
				this.getMamExtratoControlesDAO().remover(extratoControle);
			}
		    }
		} catch (Exception e) {
		    logError(EXCECAO_LANCADA, e);
		    msgRetorno = LiberarConsultasONExceptionCode.MAM_01528;
		}

		try {
		    List<MamControles> controles = getMamControlesDAO().listaControlesPorNumeroConsulta(pConNumero);
		    if (controles != null && !controles.isEmpty()) {
			for (MamControles controle : controles) {
				this.getMamControlesDAO().remover(controle);
			    controle.getConsulta().setControle(null);
			}
		    }
		} catch (Exception e) {
		    logError(EXCECAO_LANCADA, e);
		    msgRetorno = LiberarConsultasONExceptionCode.MAM_01529;
		}

		try {
		    // Deleta log em usos caso alguém tenha entrado no atend.e cancelado
		    List<MamLogEmUsos> logsEmUso = getMamLogEmUsosDAO().listaLogsEmUsoPorNumeroConsulta(pConNumero);
		    if (logsEmUso != null && !logsEmUso.isEmpty()) {
			for (MamLogEmUsos logEmUso : logsEmUso) {
			    ambulatorioRN.removerLogEmUso(logEmUso, true);
			}
		    }
		} catch (Exception e) {
		    logError(EXCECAO_LANCADA, e);
		    msgRetorno = LiberarConsultasONExceptionCode.MAM_01981;
		}

	    } else {
		// Não deixa excluir e dá mensagem de erro
		msgRetorno = LiberarConsultasONExceptionCode.MAM_01527;
	    }
	} else {
	    // Verifica se existe alguma evolucao
	    count = mamEvolucoesDAO.listarEvolucoesPorNumeroConsultaCount(pConNumero);
	    vAchouEvolucao = count != null && count > 0;
	    if (Boolean.TRUE.equals(vAchouEvolucao)) {
		// Se existe evolucao relacionada da mensagem de erro e não deixa excluir
		msgRetorno = LiberarConsultasONExceptionCode.MAM_01776;
		// Não é possível excluir a consulta pois já existe evolucao.
	    }
	}

	// Se já tem um erro detectado deve-se sair da rotina
	if (msgRetorno != null) {
	    return msgRetorno;
	}
	// Testar informações vinculadas a consultas sem atendimento ambulatorial e atendimento na emergencia verifica se tem alguma evolucção já feita para esta consulta
	vAchouInformacao = false;
	// Verifica se existe alguma evolucao
	count = mamEvolucoesDAO.listarEvolucoesPorNumeroConsultaCount(pConNumero);
	vAchouInformacao = count != null && count > 0;
	if (Boolean.TRUE.equals(vAchouInformacao)) {
	    msgRetorno = LiberarConsultasONExceptionCode.MAM_03667;
	    // Para esta consulta já foi feito um registro
	    return msgRetorno;
	}

	// Verifica se tem alguma anamnese já feita para esta consulta
	count = mamAnamnesesDAO.listarAnamnesesPorNumeroConsultaCount(pConNumero);
	vAchouInformacao = count != null && count > 0;
	if (Boolean.TRUE.equals(vAchouInformacao)) {
	    // Para esta consulta já foi feito um registro
	    return LiberarConsultasONExceptionCode.MAM_03670;
	}

	// Verifica se tem alguma nota adicional evolucoes já feita para esta consulta
	count = mamNotaAdicionalEvolucoesDAO.listarNotasAdicionaisEvolucoesPorNumeroConsultaCount(pConNumero);
	vAchouInformacao = count != null && count > 0;

	if (Boolean.TRUE.equals(vAchouInformacao)) {
	    // Para esta consulta já foi feito um registro
	    return LiberarConsultasONExceptionCode.MAM_03668;	}
	// Verifica se tem alguma nota adicional anamnese já feita para esta consulta
	count = mamNotaAdicionalAnamnesesDAO.listarNotasAdicionaisAnamnesesPorNumeroConsultaCount(pConNumero);
	vAchouInformacao = count != null && count > 0;
	if (Boolean.TRUE.equals(vAchouInformacao)) {
	    // Para esta consulta já foi feito um registro
	    return LiberarConsultasONExceptionCode.MAM_03671;
	}

	return msgRetorno;
    }

    /**
     * ORADB MAMC_GET_AGENDADO
     * 
     * @param pConNumero
     * @return
     */
    private Boolean buscaAgendado(Integer pConNumero) {
	List<MamSituacaoAtendimentos> listaSituacoes = getMamSituacaoAtendimentosDAO()
		.pesquisarSituacoesAtendimentosPorConsulta(pConNumero);
	Boolean retorno = null;

	if (listaSituacoes.isEmpty()) {
	    retorno = true;
	} else {
	    MamSituacaoAtendimentos situacaoAtendimento = listaSituacoes.get(0);
	    retorno = situacaoAtendimento.getAgendado();
	}

	return retorno;
    }

    /**
     * ORADB AACC_BUSCA_FORMA_ANT
     * 
     * @param pNumero
     * @param fagPgdSeqOld
     * @param fagTagSeqOld
     * @param fagCaaSeqOld
     * @return
     * @throws BaseException
     */
    public BuscaFormaAnteriorVO buscaFormaAnterior(Integer pNumero, Short fagPgdSeqOld, Short fagTagSeqOld, Short fagCaaSeqOld)
	    throws BaseException {
	BuscaFormaAnteriorVO vo = new BuscaFormaAnteriorVO();
	vo.setPgdSeq(fagPgdSeqOld);
	vo.setTagSeq(fagTagSeqOld);
	vo.setCaaSeq(fagCaaSeqOld);

	Short vPgdSeqOld = null;
	Short vTagSeqOld = null;
	Short vCaaSeqOld = null;

	String codigoIndSituacaoConsultaLivre = this.buscarVlrTextoAghParametro(AghuParametrosEnum.P_SITUACAO_CONS_LIVRE);

	List<AacConsultasJn> consultasJn = getAacConsultasJnDAO().listarConsultasJnOrdenadoPorJnDateTimeDesc(pNumero,
		codigoIndSituacaoConsultaLivre);

	if (consultasJn == null || consultasJn.isEmpty()) {
	    vPgdSeqOld = null;
	    vTagSeqOld = null;
	    vCaaSeqOld = null;
	} else {
	    AacConsultasJn consultaJn = consultasJn.get(0);

	    if (consultaJn.getFagPgdSeq() == null) {
		vPgdSeqOld = null;
		vTagSeqOld = null;
		vCaaSeqOld = null;
	    } else {
		vPgdSeqOld = consultaJn.getFagPgdSeq();
		vTagSeqOld = consultaJn.getFagTagSeq();
		vCaaSeqOld = consultaJn.getFagCaaSeq();
	    }
	}

	vo.setPgdSeq(vPgdSeqOld);
	vo.setTagSeq(vTagSeqOld);
	vo.setCaaSeq(vCaaSeqOld);
	vo.setRetorno(true);

	return vo;
    }

    protected BigDecimal buscarVlrNumericoAghParametro(AghuParametrosEnum parametrosEnum) throws BaseException {
	AghParametros param = getParametroFacade().buscarAghParametro(parametrosEnum);
	if (param == null || param.getVlrNumerico() == null) {
	    throw new ApplicationBusinessException(LiberarConsultasONExceptionCode.PARAMETRO_INVALIDO, parametrosEnum.toString());
	}
	return param.getVlrNumerico();
    }

    protected Short buscarVlrShortAghParametro(AghuParametrosEnum parametrosEnum) throws BaseException {
	BigDecimal ret = buscarVlrNumericoAghParametro(parametrosEnum);
	if (ret == null) {
	    throw new ApplicationBusinessException(LiberarConsultasONExceptionCode.PARAMETRO_INVALIDO, parametrosEnum.toString());
	}
	return ret.shortValue();
    }

    protected String buscarVlrTextoAghParametro(AghuParametrosEnum parametrosEnum) throws BaseException {
	AghParametros param = this.getParametroFacade().buscarAghParametro(parametrosEnum);
	if (param == null || param.getVlrTexto() == null) {
	    throw new ApplicationBusinessException(LiberarConsultasONExceptionCode.PARAMETRO_INVALIDO, parametrosEnum.toString());
	}
	return param.getVlrTexto();
    }
    
    //  #52563
    public Integer verificarConsultaPossuiReconsultasVinculadas(Integer numero) {
    	List<AacConsultas> lista = aacConsultasDAO.pesquisarConsultacPorConsultaNumeroAnterior(numero);
    	if(lista!=null) {
    		return lista.size();
    	}
    	else {
    		return null;
    	}
    }

    protected IAghuFacade getAghuFacade() {
	return this.aghuFacade;
    }

    protected IParametroFacade getParametroFacade() {
	return parametroFacade;
    }

    protected IExamesLaudosFacade getExamesLaudosFacade() {
	return this.examesLaudosFacade;
    }

    protected IFaturamentoFacade getFaturamentoFacade() {
	return faturamentoFacade;
    }

    protected IPacienteFacade getPacienteFacade() {
	return pacienteFacade;
    }

    protected IAmbulatorioFacade getAmbulatorioFacade() {
	return this.ambulatorioFacade;
    }

    protected IProcedimentoTerapeuticoFacade getProcedimentoTerapeuticoFacade() {
	return this.procedimentoTerapeuticoFacade;
    }

    protected AmbulatorioConsultaRN getAmbulatorioConsultaRN() {
	return ambulatorioConsultaRN;
    }

    protected AmbulatorioRN getAmbulatorioRN() {
	return ambulatorioRN;
    }

    protected ProcedimentoConsultaRN getProcedimentoConsultaRN() {
	return procedimentoConsultaRN;
    }

    protected MarcacaoConsultaRN getMarcacaoConsultaRN() {
	return marcacaoConsultaRN;
    }

    protected AacConsultasDAO getAacConsultasDAO() {
	return aacConsultasDAO;
    }

    protected AacConsultaProcedHospitalarDAO getAacConsultaProcedHospitalarDAO() {
	return aacConsultaProcedHospitalarDAO;
    }

    protected AacConsultasJnDAO getAacConsultasJnDAO() {
	return aacConsultasJnDAO;
    }

    protected AacCaracteristicaGradeDAO getAacCaracteristicaGradeDAO() {
	return aacCaracteristicaGradeDAO;
    }

    protected AacSituacaoConsultasDAO getAacSituacaoConsultasDAO() {
	return aacSituacaoConsultasDAO;
    }

    private AacPagadorDAO getAacPagadorDAO() {
	return aacPagadorDAO;
    }

    private AacTipoAgendamentoDAO getAacTipoAgendamentoDAO() {
	return aacTipoAgendamentoDAO;
    }

    private AacCondicaoAtendimentoDAO getAacCondicaoAtendimentoDAO() {
	return aacCondicaoAtendimentoDAO;
    }

    protected MamControlesDAO getMamControlesDAO() {
	return mamControlesDAO;
    }

    protected MamSituacaoAtendimentosDAO getMamSituacaoAtendimentosDAO() {
	return mamSituacaoAtendimentosDAO;
    }

    protected MamExtratoControlesDAO getMamExtratoControlesDAO() {
	return mamExtratoControlesDAO;
    }

    protected MamLogEmUsosDAO getMamLogEmUsosDAO() {
	return mamLogEmUsosDAO;
    }

    protected MamEvolucoesDAO getMamEvolucoesDAO() {
	return mamEvolucoesDAO;
    }

    protected MamAnamnesesDAO getMamAnamnesesDAO() {
	return mamAnamnesesDAO;
    }

    protected MamNotaAdicionalAnamnesesDAO getMamNotaAdicionalAnamnesesDAO() {
	return mamNotaAdicionalAnamnesesDAO;
    }

    protected MamNotaAdicionalEvolucoesDAO getMamNotaAdicionalEvolucoesDAO() {
	return mamNotaAdicionalEvolucoesDAO;
    }

    protected AacGradeAgendamenConsultasDAO getAacGradeAgendamenConsultasDAO() {
	return aacGradeAgendamenConsultasDAO;
    }

    public IPermissionService getPermissionService() {
	return permissionService;
    }

}