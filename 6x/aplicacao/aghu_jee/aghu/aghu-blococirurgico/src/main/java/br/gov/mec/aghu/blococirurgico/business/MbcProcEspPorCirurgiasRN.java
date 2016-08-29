package br.gov.mec.aghu.blococirurgico.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendaProcedimentoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCompSangProcCirgDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcControleEscalaCirurgicaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcEquipamentoCirgPorUnidDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcEspecialidadeProcCirgsDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcEspPorCirurgiaJnDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcEspPorCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcedimentoCirurgicoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcedimentoPorGrupoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfCirurgiasDAO;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.dao.ObjetosOracleDAO;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioIndRespProc;
import br.gov.mec.aghu.dominio.DominioNaturezaFichaAnestesia;
import br.gov.mec.aghu.dominio.DominioOperacaoBanco;
import br.gov.mec.aghu.dominio.DominioOrigemPacienteCirurgia;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioRegimeProcedimentoCirurgicoSus;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AelProjetoIntercProc;
import br.gov.mec.aghu.model.AelProjetoPesquisas;
import br.gov.mec.aghu.model.AelProjetoProcedimento;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.MbcAgendaProcedimento;
import br.gov.mec.aghu.model.MbcAgendaProcedimentoId;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcControleEscalaCirurgica;
import br.gov.mec.aghu.model.MbcEspecialidadeProcCirgs;
import br.gov.mec.aghu.model.MbcEspecialidadeProcCirgsId;
import br.gov.mec.aghu.model.MbcGrupoProcedCirurgico;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgiaJn;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgias;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MbcProcedimentoPorGrupo;
import br.gov.mec.aghu.model.MbcProfCirurgias;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.business.moduleintegration.InactiveModuleException;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.core.mail.EmailUtil;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * Classe responsável pelas regras de BANCO para MBC_PROC_ESP_POR_CIRURGIAS
 * 
 * @author aghu
 * 
 */
@SuppressWarnings({ "PMD.ExcessiveClassLength" })
@Stateless
public class MbcProcEspPorCirurgiasRN extends BaseBusiness {
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(MbcProcEspPorCirurgiasRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcProcedimentoCirurgicoDAO mbcProcedimentoCirurgicoDAO;

	@Inject
	private MbcProcEspPorCirurgiasDAO mbcProcEspPorCirurgiasDAO;

	@Inject
	private MbcAgendaProcedimentoDAO mbcAgendaProcedimentoDAO;

	@Inject
	private MbcCirurgiasDAO mbcCirurgiasDAO;

	@Inject
	private EmailUtil emailUtil;

	@Inject
	private MbcProcedimentoPorGrupoDAO mbcProcedimentoPorGrupoDAO;

	@Inject
	private ObjetosOracleDAO objetosOracleDAO;

	@Inject
	private MbcEquipamentoCirgPorUnidDAO mbcEquipamentoCirgPorUnidDAO;

	@Inject
	private MbcProfCirurgiasDAO mbcProfCirurgiasDAO;

	@Inject
	private MbcProcEspPorCirurgiaJnDAO mbcProcEspPorCirurgiaJnDAO;

	@Inject
	private MbcCompSangProcCirgDAO mbcCompSangProcCirgDAO;

	@Inject
	private MbcEspecialidadeProcCirgsDAO mbcEspecialidadeProcCirgsDAO;

	@Inject
	private MbcControleEscalaCirurgicaDAO mbcControleEscalaCirurgicaDAO;


	@EJB
	private ICascaFacade iCascaFacade;

	@EJB
	private IFaturamentoFacade iFaturamentoFacade;

	@EJB
	private ISolicitacaoExameFacade iSolicitacaoExameFacade;

	@EJB
	private IParametroFacade iParametroFacade;

	@EJB
	private IExamesFacade iExamesFacade;

	@EJB
	private MbcAgendaProcedimentoRN mbcAgendaProcedimentoRN;

	/**
	 * 
	 */
	private static final long serialVersionUID = -98189791661301175L;

	public enum MbcProcEspPorCirurgiasRNExceptionCode implements BusinessExceptionCode {
		MBC_00353, MBC_00354, MBC_00360, MBC_00452, MBC_00361, MBC_01826, MBC_01374, MBC_01375, MBC_00340, MBC_00459, MBC_00535;
	}

	/*
	 * Constantes para o e-mail com o aviso de cotas de procedimentos em projeto de pesquisa
	 */
	private static final String VALOR_ASSUNTO = "AVISO DE COTAS DE PROCEDIMENTOS EM PROJETO DE PESQUISA";
	private static final String VALOR_MENSAGEM = "Há procedimentos com as cotas próximo do fim. Projeto de pesquisa: ";

	/*
	 * Métodos para PERSISTIR
	 */

	/**
	 * Persistir MbcProcEspPorCirurgias
	 * 
	 * @param procEspPorCirurgia
	 * @param servidorLogado
	 * @throws BaseException
	 */
	public void persistirProcEspPorCirurgias(MbcProcEspPorCirurgias procEspPorCirurgia) throws BaseException {
		MbcProcEspPorCirurgias original = getMbcProcEspPorCirurgiasDAO().obterOriginal(procEspPorCirurgia.getId());
		if (original == null) { // Inserir
			this.inserirMbcProcEspPorCirurgias(procEspPorCirurgia);
		} else { // Atualizar
			this.atualizarMbcProcEspPorCirurgias(procEspPorCirurgia);
		}
	}

	/*
	 * Métodos INSERIR
	 */

	/**
	 * ORADB TRIGGER MBCT_PPC_BRI (INSERT)
	 * 
	 * @param procEspPorCirurgia
	 * @param servidorLogado
	 * @throws BaseException
	 */
	public void preInserirMbcProcEspPorCirurgias(MbcProcEspPorCirurgias procEspPorCirurgia) throws BaseException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();

		//******
		//AJUSTE TEMPORÁRIO FEITO NA IMPLEMENTAÇÃO DAS TRIGGERS
		// TODO - CORRIGIR IMPLEMENTAÇÃO 
		//******
		MbcEspecialidadeProcCirgsId especProcCirgId = new MbcEspecialidadeProcCirgsId();
		especProcCirgId.setPciSeq(procEspPorCirurgia.getId().getEprPciSeq());
		especProcCirgId.setEspSeq(procEspPorCirurgia.getId().getEprEspSeq());
		procEspPorCirurgia.setMbcEspecialidadeProcCirgs(
				getMbcEspecialidadeProcCirgsDAO().obterPorChavePrimaria(especProcCirgId)
		);
		procEspPorCirurgia.setCirurgia(
				getMbcCirurgiasDAO().obterPorChavePrimaria(procEspPorCirurgia.getId().getCrgSeq())
		);
		//procEspPorCirurgia.setIndRespProc(procEspPorCirurgia.getId().getIndRespProc());
		//******
		
		procEspPorCirurgia.setCriadoEm(new Date());
		
		// Caso o procedimento não é descrição cirúrgica
		if (!DominioIndRespProc.DESC.equals(procEspPorCirurgia.getId().getIndRespProc())) {
			this.verificarAlteracaoNaturezaAgendamentoEletiva(procEspPorCirurgia.getCirurgia()); // RN1
		}

		procEspPorCirurgia.setServidor(servidorLogado); // RN2 Atualiza servidor (MBCK_MBC_RN.RN_MBCP_ATU_SERVIDOR)

		this.verificarSituacao(procEspPorCirurgia.getMbcEspecialidadeProcCirgs()); // RN3

		// Verifica a quantidade
		if (CoreUtil.maior(procEspPorCirurgia.getQtd(), 1)) {
			this.verificarQuantidadeProcedimento(procEspPorCirurgia.getProcedimentoCirurgico()); // RN5
		}

		// Verifica a cota de procedimentos no cadastro de projetos
		this.verificarCotaProcedimentosCadastroProjetos(procEspPorCirurgia); // RN5

		// Atualiza a cota de procedimentos no cadastro de projetos
		this.atualizarCotaProcedimentosCadastroProjetos(procEspPorCirurgia); // RN6

		// Verifica se o regime sus do procedimento cirugico está de acordo com a origem pac da cirurgia
		this.verificarRegimeSusProcedimentoCirurgico(procEspPorCirurgia); // RN7
		
		this.verificarProfissionalResponsavel(procEspPorCirurgia);
	}

	private void verificarProfissionalResponsavel(MbcProcEspPorCirurgias procEspPorCirurgia) throws BaseException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		MbcCirurgias cirurgia = procEspPorCirurgia.getCirurgia();
		MbcProfCirurgias responsavel = getMbcProfCirurgiasDAO().retornaResponsavelCirurgia(cirurgia);		
		AghAtendimentos atendimento = procEspPorCirurgia.getCirurgia().getAtendimento();
		Boolean possuiCompSang = this.getMbcCompSangProcCirgDAO().buscarMbcCompSangProcCirg(
																	procEspPorCirurgia.getId().getEprPciSeq(),
																	procEspPorCirurgia.getId().getEprEspSeq()).size() > 0;
		
		if(responsavel != null && possuiCompSang && atendimento != null
			&& DominioPacAtendimento.S.equals(cirurgia.getAtendimento().getIndPacAtendimento())
			&& !cirurgia.getSituacao().equals(DominioSituacaoCirurgia.RZDA)
			&& !cirurgia.getSituacao().equals(DominioSituacaoCirurgia.CANC)) {
			getMbcProcEspPorCirurgiasDAO().flush();
			try {
				getSolicitacaoExameFacade().gerarExameProvaCruzadaTransfusional(
						atendimento, procEspPorCirurgia.getCirurgia(), null,
						responsavel.getServidor(), Boolean.FALSE);
			} catch(InactiveModuleException e) {
				logWarn(e.getMessage());
				this.getObjetosOracleDAO()
						.gerarExameProvaCruzadaTransfusional(
								atendimento.getSeq(), cirurgia.getSeq(), servidorLogado, responsavel.getServidor(), DominioSimNao.N.toString());
			}
		}
	}
	
	private ObjetosOracleDAO getObjetosOracleDAO() {
		return objetosOracleDAO;
	}
	
	/**
	 * Inserir MbcProcEspPorCirurgias
	 * 
	 * @param procEspPorCirurgia
	 * @param servidorLogado
	 * @throws BaseException
	 */
	public void inserirMbcProcEspPorCirurgias(MbcProcEspPorCirurgias procEspPorCirurgia) throws BaseException {
		this.preInserirMbcProcEspPorCirurgias(procEspPorCirurgia);
		
		if (getMbcProcEspPorCirurgiasDAO().contains(procEspPorCirurgia)) {
			// Se o pojo - procEspPorCirurgia - jah estiver contido no session do hibernate, o persistir não vai funcionar.
			// O registro não vai ser inserido, vai ocorrer erro em outros pojos que tenham o ProcEspPorCirurgias como dependencia (FK).
			LOG.warn("MbcProcEspPorCirurgiasRN.inserirMbcProcEspPorCirurgias - metodo de insert encontrou pojo MbcProcEspPorCirurgias na session do hibernate.");
		}
		getMbcProcEspPorCirurgiasDAO().persistir(procEspPorCirurgia);
		
		/*
		 * A ORADB TRIGGER MBCT_PPC_ARI_AGFA (INSERT) não foi migrada pois é integração com AGFA. AGFA é realizada pelo próprio ORACLE
		 */
		/*
		 * ORADB TRIGGER MBCT_PPC_ASI: As regras a seguir são da ENFORCE ORADB PROCEDURE MBCK_PPC.MBCP_ENFORCE_PPC_RULES
		 */
		this.atualizarFatura(procEspPorCirurgia); // RN1 ENFORCE
		// Verifica equipamento de video utilizados
		this.verificarUtilizacaoEquipamento(procEspPorCirurgia); // RN2 ENFORCE
		// Insere o procedimento no portal de agendamento de cirurgias
		this.atualizarAgenda(DominioOperacaoBanco.INS, procEspPorCirurgia.getCirurgia(), procEspPorCirurgia, null); // RN3 ENFORCE
	}

	/*
	 * Métodos ATUALIZAR
	 */

	/**
	 * ORADB TRIGGER MBCT_PPC_BRU (UPDATE)
	 * 
	 * @param original
	 * @param novo
	 * @param servidorLogado
	 * @throws BaseException
	 */
	public void preAtualizarMbcProcEspPorCirurgias(MbcProcEspPorCirurgias original, MbcProcEspPorCirurgias novo) throws BaseException {

		if (CoreUtil.modificados(original.getPacOruAccNumber(), novo.getPacOruAccNumber())) {
			return;
		}

		if (!DominioIndRespProc.DESC.equals(novo.getId().getIndRespProc())) {
			// Não é descrição cirúrgica
			this.verificarAlteracaoNaturezaAgendamentoEletiva(novo.getCirurgia());
		}

		this.verificarSituacao(novo.getMbcEspecialidadeProcCirgs());

		if (CoreUtil.maior(novo.getQtd(), 1)) {
			this.verificarQuantidadeProcedimento(novo.getProcedimentoCirurgico());
		}

		// Verifica a cota de procedimentos no cadastro de projetos
		this.verificarCotaProcedimentosCadastroProjetos(novo);

		// Atualiza a cota de procedimentos no cadastro de projetos
		this.atualizarCotaProcedimentosCadastroProjetos(novo);

	}

	/**
	 * Atualizar MbcProcEspPorCirurgias
	 * 
	 * @param procEspPorCirurgia
	 * @param servidorLogado
	 * @throws BaseException
	 */
	public void atualizarMbcProcEspPorCirurgias(MbcProcEspPorCirurgias procEspPorCirurgia) throws BaseException {

		MbcProcEspPorCirurgias original = getMbcProcEspPorCirurgiasDAO().obterOriginal(procEspPorCirurgia.getId());

		this.preAtualizarMbcProcEspPorCirurgias(original, procEspPorCirurgia);
		this.getMbcProcEspPorCirurgiasDAO().merge(procEspPorCirurgia);
		this.posAtualizarMbcProcEspPorCirurgias(original, procEspPorCirurgia);

		/*
		 * ORADB TRIGGER MBCT_PPC_ASU: As regras a seguir são da ENFORCE ORADB PROCEDURE MBCK_PPC.MBCP_ENFORCE_PPC_RULES
		 */
		if (CoreUtil.modificados(original.getSituacao(), procEspPorCirurgia.getSituacao()) || CoreUtil.modificados(original.getQtd(), procEspPorCirurgia.getQtd())
				|| CoreUtil.modificados(original.getProcedHospInterno(), procEspPorCirurgia.getProcedHospInterno())
				|| CoreUtil.modificados(original.getCid(), procEspPorCirurgia.getCid())) { // RN1 ENFORCE

			// Se a situação ou a quantidade do procedimento da cirurgia forem alterados
			this.atualizarFatura(procEspPorCirurgia);
		}

		// Atualiza o procedimento no portal de agendamento de cirurgias
		this.atualizarAgenda(DominioOperacaoBanco.UPD, procEspPorCirurgia.getCirurgia(), procEspPorCirurgia, original); // RN2 ENFORCE
	}

	/**
	 * ORADB TRIGGER MBCT_PPC_ARU (UPDATE)
	 * 
	 * @param original
	 * @param novo
	 * @param servidorLogado
	 * @throws BaseException
	 */
	public void posAtualizarMbcProcEspPorCirurgias(MbcProcEspPorCirurgias original, MbcProcEspPorCirurgias novo) throws BaseException {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();

		if (CoreUtil.modificados(original.getPacOruAccNumber(), novo.getPacOruAccNumber())) {
			return;
		}

		// Verifica se o registro foi modificado
		final boolean isModificado = CoreUtil.modificados(original.getCirurgia(), novo.getCirurgia())
				|| CoreUtil.modificados(original.getMbcEspecialidadeProcCirgs(), novo.getMbcEspecialidadeProcCirgs())
				|| CoreUtil.modificados(original.getId().getIndRespProc(), novo.getId().getIndRespProc()) || CoreUtil.modificados(original.getServidor(), novo.getServidor())
				|| CoreUtil.modificados(original.getSituacao(), novo.getSituacao()) || CoreUtil.modificados(original.getCriadoEm(), novo.getCriadoEm())
				|| CoreUtil.modificados(original.getQtd(), novo.getQtd()) || CoreUtil.modificados(original.getIndPrincipal(), novo.getIndPrincipal())
				|| CoreUtil.modificados(original.getProcedHospInterno(), novo.getProcedHospInterno()) || CoreUtil.modificados(original.getCid(), novo.getCid());

		if (isModificado) {

			MbcProcEspPorCirurgiaJn jn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, MbcProcEspPorCirurgiaJn.class, servidorLogado.getUsuario());

			jn.setCrgSeq(original.getCirurgia().getSeq());
			jn.setEprEspSeq(original.getMbcEspecialidadeProcCirgs().getId().getEspSeq());
			jn.setEprPciSeq(original.getMbcEspecialidadeProcCirgs().getId().getPciSeq());
			jn.setIndRespProc(original.getId().getIndRespProc());
			jn.setSerMatricula(original.getServidor().getId().getMatricula());
			jn.setSerVinCodigo(original.getServidor().getId().getVinCodigo());
			jn.setSituacao(original.getSituacao());
			jn.setCriadoEm(original.getCriadoEm());
			jn.setQtd(original.getQtd());
			jn.setIndPrincipal(original.getIndPrincipal());
			jn.setPhiSeq(original.getProcedHospInterno() != null ? original.getProcedHospInterno().getSeq() : null);
			jn.setCidSeq(original.getCid() != null ? original.getCid().getSeq() : null);

			// Insere na JOURNAL
			getMbcProcEspPorCirurgiaJnDAO().persistir(jn);

		}
	}

	/*
	 * Métodos REMOVER
	 */

	/**
	 * ORADB TRIGGER MBCT_PPC_BRD (DELETE)
	 * 
	 * @param procEspPorCirurgia
	 * @param servidorLogado
	 * @throws BaseException
	 */
	public void preRemoverMbcProcEspPorCirurgias(MbcProcEspPorCirurgias procEspPorCirurgia) throws BaseException {
		this.verificarAlteracaoNaturezaAgendamentoEletiva(procEspPorCirurgia.getCirurgia());
	}

	/**
	 * Remover MbcProcEspPorCirurgias
	 * 
	 * @param procEspPorCirurgia
	 * @param servidorLogado
	 * @throws BaseException
	 */
	public void removerMbcProcEspPorCirurgias(MbcProcEspPorCirurgias procEspPorCirurgia) throws BaseException {
		this.preRemoverMbcProcEspPorCirurgias(procEspPorCirurgia);
		this.getMbcProcEspPorCirurgiasDAO().remover(procEspPorCirurgia);

		/*
		 * ORADB TRIGGER MBCT_PPC_ASD: As regras a seguir são da ENFORCE ORADB PROCEDURE MBCK_PPC.MBCP_ENFORCE_PPC_RULES
		 */
		// Excluir o procedimento no portal de agendamento de cirurgias
		this.atualizarAgenda(DominioOperacaoBanco.DEL, procEspPorCirurgia.getCirurgia(), null, procEspPorCirurgia); // RN1 ENFORCE
	}

	/*
	 * PROCEDURES
	 */

	/**
	 * ORADB PROCEDURE MBCK_PPC_RN.RN_PPCP_VER_ALT_ELET
	 * <p>
	 * Verifica se a natureza do agendamento é eletiva e usuário NÃO TEM PERFIL de 'agendar não prevista' e já foi executada a escala definitiva: Não permito mais atualizar
	 * procedimento da cirurgia(somente os que o IND_RESP_PROC = 'AGND').
	 * </p>
	 * 
	 * @param cirurgia
	 * @throws BaseException
	 */
	public void verificarAlteracaoNaturezaAgendamentoEletiva(MbcCirurgias cirurgia) throws BaseException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();

		if (cirurgia != null && DominioNaturezaFichaAnestesia.ELE.equals(cirurgia.getNaturezaAgenda())) {

			// Verificar se o usuário tem a permissão: AGENDAR CIRURGIA NAO PREVISTA
			final boolean isPerfilAgendarCirurgiaNaoPrevista = this.getICascaFacade().usuarioTemPermissao(servidorLogado.getUsuario(), "agendarCirurgiaNaoPrevista");

			if (!isPerfilAgendarCirurgiaNaoPrevista) {

				// Pesquisa o controle da escala cirúrgica
				List<MbcControleEscalaCirurgica> listaControleEscala = this.getMbcControleEscalaCirurgicaDAO().pesquisarControleEscalaCirurgicaPorUnfSeqDataPesquisa(
						cirurgia.getUnidadeFuncional().getSeq(), cirurgia.getData());

				if (!listaControleEscala.isEmpty()) { // Quando o controle de escala existir

					// Pesquisa a lista de procedimentos
					List<MbcProcEspPorCirurgias> listaProcedimentos = this.getMbcProcEspPorCirurgiasDAO().pesquisarMbcProcEspCirurgicoAgendamentoPorCirurgia(cirurgia.getSeq());

					if (listaProcedimentos != null) {
						// Já foi executada a escala definitiva para esta data
						throw new ApplicationBusinessException(MbcProcEspPorCirurgiasRNExceptionCode.MBC_00452);
					}

				}
			}

		}

	}

	/**
	 * ORADB PROCEDURE MBCK_PPC_RN.RN_PPCP_VER_SITUACAO
	 * <p>
	 * Verifica a situação da especialidade cirurgica
	 * </p>
	 * 
	 * @param especialidadeProcCirgs
	 * @throws BaseException
	 */
	public void verificarSituacao(MbcEspecialidadeProcCirgs especialidadeProcCirgs) throws BaseException {

		if (especialidadeProcCirgs == null) {
			// Especialidade do procedimento cirurgico não cadastrado
			throw new ApplicationBusinessException(MbcProcEspPorCirurgiasRNExceptionCode.MBC_00353);
		}

		if (DominioSituacao.I.equals(especialidadeProcCirgs.getSituacao())) {
			// Especialidade por procedimento cirúrgico inativo
			throw new ApplicationBusinessException(MbcProcEspPorCirurgiasRNExceptionCode.MBC_00354);
		}

	}

	/**
	 * ORADB PROCEDURE MBCK_PPC_RN.RN_PPCP_VER_QUANT
	 * <p>
	 * Verifica a quantidade do procedimento cirurgico
	 * </p>
	 * 
	 * @param procEspPorCirurgia
	 * @throws BaseException
	 */
	public void verificarQuantidadeProcedimento(MbcProcedimentoCirurgicos procedimentoCirurgicos) throws BaseException {

		if (procedimentoCirurgicos == null) {
			// Procedimento cirúrgico não está cadastrado
			throw new ApplicationBusinessException(MbcProcEspPorCirurgiasRNExceptionCode.MBC_00360);
		}

		if (Boolean.FALSE.equals(procedimentoCirurgicos.getIndProcMultiplo())) {
			// Procedimento cirúrgico não suporta multiplicidade
			throw new ApplicationBusinessException(MbcProcEspPorCirurgiasRNExceptionCode.MBC_00361);
		}

	}

	/**
	 * ORADB PROCEDURE MBCK_PPC_RN.RN_PPCP_VER_COTA_PJQ
	 * <p>
	 * Verifica a cota de procedimentos no cadastro de projeto
	 * </p>
	 * 
	 * @param procEspPorCirurgia
	 * @throws BaseException
	 */
	public void verificarCotaProcedimentosCadastroProjetos(MbcProcEspPorCirurgias procEspPorCirurgia) throws BaseException {
		// Verifica se a situação é inativa
		if (DominioSituacao.I.equals(procEspPorCirurgia.getSituacao()) || procEspPorCirurgia.getCirurgia() == null) {
			return;
		}
		MbcCirurgias cirurgia = this.getMbcCirurgiasDAO().obterCirurgiaPorSeq(procEspPorCirurgia.getCirurgia().getSeq());
		
		final AelProjetoPesquisas projetoPesquisa = cirurgia.getProjetoPesquisa(); // Obtém o projeto de pesquisa
		final AipPacientes paciente = cirurgia.getPaciente(); // Obtém o paciente

		if (projetoPesquisa != null) {

			boolean isVoucher = projetoPesquisa.getVoucherEletronico();

			if (!isVoucher) {
				// Retorna caso não contenha voucher
				return;
			}

			// Pesquisa procedimento do projeto para obter as quantidades
			AelProjetoProcedimento projetoProcedimento = this.getExamesFacade().obterProjetoProcedimentoPorChavePrimaria(projetoPesquisa.getSeq(),
					procEspPorCirurgia.getProcedimentoCirurgico().getSeq());
			Short quantidadePermitida = null;
			Short quantidadeSolicitada = null;

			if (projetoProcedimento == null) {

				// Verifica se o procedimento está liberado nas intercorrências. Vide: AELP_VER_INTERCORRENCIA(V_RETORNO)
				final boolean retorno = this.verificarIntercorrencias(projetoPesquisa, paciente, procEspPorCirurgia.getProcedimentoCirurgico());

				if (!retorno) {
					// O procedimento não está previsto no projeto.
					throw new ApplicationBusinessException(MbcProcEspPorCirurgiasRNExceptionCode.MBC_01375, procEspPorCirurgia.getProcedimentoCirurgico().getDescricao());
				} else {
					return;
				}

			} else {
				quantidadePermitida = projetoProcedimento.getQtdePermitido() != null ? projetoProcedimento.getQtdePermitido() : 0;
				quantidadeSolicitada = projetoProcedimento.getQtdeSolicitado() != null ? projetoProcedimento.getQtdeSolicitado() : 0;
			}

			if (CoreUtil.menorOuIgual(quantidadePermitida - quantidadeSolicitada, 0)) {

				// Verifica se o procedimento está liberado nas intercorrências. Vide: AELP_VER_INTERCORRENCIA(V_RETORNO)
				final boolean retorno = this.verificarIntercorrencias(projetoPesquisa, paciente, procEspPorCirurgia.getProcedimentoCirurgico());

				if (!retorno) {
					// O procedimento deste projeto não contém mais saldo.
					throw new ApplicationBusinessException(MbcProcEspPorCirurgiasRNExceptionCode.MBC_01374, procEspPorCirurgia.getProcedimentoCirurgico().getDescricao());
				}
			}

		}

	}

	/**
	 * ORADB PROCEDURE MBCK_PPC_RN.RN_PPCP_VER_COTA_PJQ.AELP_VER_INTERCORRENCIA
	 * <p>
	 * Verifica se o procedimento está liberado nas intercorrências
	 * </p>
	 * 
	 * @param procEspPorCirurgia
	 * @throws BaseException
	 */
	private boolean verificarIntercorrencias(AelProjetoPesquisas projetoPesquisa, AipPacientes paciente, MbcProcedimentoCirurgicos procedimentoCirurgico
			) throws BaseException {

		AelProjetoIntercProc projetoIntercorrencia = this.getExamesFacade().obterProjetoIntercProcProjetoPacienteQuantidadeEfetivado(projetoPesquisa.getSeq(),
				paciente.getCodigo(), procedimentoCirurgico.getSeq());

		if (projetoIntercorrencia == null) {
			return false;
		}

		Integer quantidadeSubtraida = projetoIntercorrencia.getQtde() - 1;
		projetoIntercorrencia.setQtde(quantidadeSubtraida.shortValue());
		projetoIntercorrencia.setEfetivado(true);

		// ATUALIZA projeto intercorrencia com a aquanidade subtraída e indicação de efetivado
		this.getExamesFacade().persistirProjetoIntercProc(projetoIntercorrencia);

		return true;
	}

	/**
	 * ORADB PROCEDURE MBCK_PPC_RN.RN_PPCP_ATU_COTA_PJQ
	 * <p>
	 * Atualiza a cota de procedimentos no cadastro de projetos
	 * </p>
	 * 
	 * @param procEspPorCirurgia
	 * @throws BaseException
	 */
	public void atualizarCotaProcedimentosCadastroProjetos(MbcProcEspPorCirurgias procEspPorCirurgias) throws BaseException {
		MbcCirurgias cirurgia = this.getMbcCirurgiasDAO().obterCirurgiaPorSeq(procEspPorCirurgias.getCirurgia().getSeq());

		AelProjetoPesquisas projetoPesquisa = cirurgia.getProjetoPesquisa();

		DominioSituacaoCirurgia situacaoCirurgia = getMbcCirurgiasDAO().obterSituacaoCirurgia(cirurgia.getSeq());

		if (DominioSituacaoCirurgia.CANC.equals(situacaoCirurgia)) {
			return;
		}

		if (projetoPesquisa != null) { // Encontrou projeto de pesquisa

			// Resgata o contador que será utilizado no percentual das quantidades. Vide: Inativo = -1. Ativo = +1
			final Integer valorCount = DominioSituacao.I.equals(procEspPorCirurgias.getSituacao()) ? -1 : 1;

			boolean isVoucher = projetoPesquisa.getVoucherEletronico();
			if (!isVoucher) {
				// Retorna caso não contenha voucher
				return;
			}

			// Obtém o projeto procedimento
			AelProjetoProcedimento projetoProcedimento = this.getExamesFacade().obterProjetoProcedimentoAtivoPorId(projetoPesquisa.getSeq(),
					procEspPorCirurgias.getProcedimentoCirurgico().getSeq());

			if (projetoProcedimento == null) {
				return;
			}

			// Obtém as quantidades
			final Short quantidadePermitida = (Short) CoreUtil.nvl(projetoProcedimento.getQtdePermitido(), 0);
			final Short quantidadeSolicitada = (Short) CoreUtil.nvl(projetoProcedimento.getQtdeSolicitado(), 0);

			// Calcula a NOVA QUANTIDADE SOLICITADA
			Integer qtdeSolicitado = ((Short) CoreUtil.nvl(quantidadeSolicitada, 0)) + valorCount;

			// Seta no procedimento a NOVA QUANTIDADE SOLICITADA
			projetoProcedimento.setQtdeSolicitado(qtdeSolicitado.shortValue());

			/*
			 * TODO ATENÇÃO! AS REGRAS DE AEL_PROJETO_PROCEDIMENTOS NÃO FORAM MIGRADAS NO SPRINT 69
			 */
			// Atualiza projeto procedimento com a NOVA QUANTIDADE SOLICITADA
			this.getExamesFacade().persistirProjetoProcedimento(projetoProcedimento);

			// Obtém o percentual para avisar cotas de exames nos projetos de pesquisa
			AghParametros parametroPercAvisoCotaProjeto = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_PERC_AVISO_COTA_PROJETO);
			Double valorPercentualCota = parametroPercAvisoCotaProjeto.getVlrNumerico().doubleValue();

			// Calcula o saldo
			Integer valorSaldo = quantidadePermitida - (quantidadeSolicitada + valorCount);

			// Calcula o percentual do valor perto do fim
			Double valorPercentual = quantidadePermitida * (valorPercentualCota / 100);

			if (CoreUtil.menorOuIgual(valorSaldo, valorPercentual)) {
				// Quando o saldo for menor que o valor percentual ENVIA O E-MAIL com o aviso de cotas de procedimentos em projeto de pesquisa
				String valorMensagem = VALOR_MENSAGEM + projetoPesquisa.getNome();
				this.enviaEmail(projetoPesquisa.getMailResp(), VALOR_ASSUNTO, valorMensagem);
			}

		}

	}

	/**
	 * Envia email em MBCK_PPC_RN.RN_PPCP_ATU_COTA_PJQ
	 * 
	 * @param servidorLogado
	 * @param destinatario
	 * @param assunto
	 * @param mensagem
	 * @throws BaseException
	 */
	private void enviaEmail(String destinatario, String assunto, String mensagem) throws BaseException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();

		if (servidorLogado != null && destinatario != null) {

			// Obtém remetente do AGHU
			String remetente = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_EMAIL_ENVIO).getVlrTexto();

			// Instancia destinatarios
			List<String> destinatarios = new ArrayList<String>();

			// Acrescenta o servidor logado na lista de destinatários
			if (servidorLogado != null && servidorLogado.getEmail() != null) {
				destinatarios.add(servidorLogado.getEmail().trim().toLowerCase());
			}

			// Acrescenta destinatários
			StringTokenizer emailPara = new StringTokenizer(destinatario, ";");
			while (emailPara.hasMoreTokens()) {
				destinatarios.add(emailPara.nextToken().trim().toLowerCase());
			}

			// Envia e-mail
			this.getEmailUtil().enviaEmail(remetente, destinatarios, null, assunto, mensagem);
		}

	}

	/**
	 * ORADB PROCEDURE MBCK_PPC_RN.RN_PPCP_REGIM_CIRG
	 * <p>
	 * Verifica se o regime sus do procedimento cirugico está de acordo com a ORIGEM PAC da cirurgia
	 * </p>
	 * 
	 * @param procEspPorCirurgia
	 * @throws BaseException
	 */
	public void verificarRegimeSusProcedimentoCirurgico(MbcProcEspPorCirurgias procEspPorCirurgias) throws BaseException {

		/*
		 * Verifica se os regimes dos procedimentos estão de acordo com o ORIGEM_PAC_CIRG da Cirurgia: Se no procedimento for AMBULATÓRIO permite Na Cirurgia AMBULATÓRIO e
		 * INTERNAÇÃO. Se no procedimento for HOSPITAL DIA, INTERNAÇÃO até 72h e INTERNAÇÃO permite na Cirurgia INTERNAÇÃO Não faz verificação se o Regime do procedimento OU se a
		 * Origem da cirurgia forem nulos.
		 */

		if (!DominioIndRespProc.AGND.equals(procEspPorCirurgias.getId().getIndRespProc())) {
			// Retorna quando for DIFERENTE DE AGENDAMENTO
			return;
		}

		boolean convenioSus = false;
		DominioRegimeProcedimentoCirurgicoSus regimeProcedSus = null;
		DominioOrigemPacienteCirurgia origemPacCirg = null;
		FatConvenioSaude convenioSaude = null;

		MbcProcedimentoCirurgicos procedimentoCirurgico = procEspPorCirurgias.getProcedimentoCirurgico();
		if (procedimentoCirurgico != null) {
			regimeProcedSus = procedimentoCirurgico.getRegimeProcedSus();
		}

		MbcCirurgias cirurgia = procEspPorCirurgias.getCirurgia();
		if (procEspPorCirurgias.getCirurgia() != null) {
			origemPacCirg = cirurgia.getOrigemPacienteCirurgia();
			convenioSaude = cirurgia.getConvenioSaude();
		}

		DominioGrupoConvenio grupoConvenio = convenioSaude.getGrupoConvenio();

		if (grupoConvenio != null && grupoConvenio == DominioGrupoConvenio.S) {
			convenioSus = true;
		}

		if (!convenioSus) {
			return;
		}

		// Regimes da lista: 'H','9','I'
		List<DominioRegimeProcedimentoCirurgicoSus> listaRegimesNaoPermitidos = new LinkedList<DominioRegimeProcedimentoCirurgicoSus>();
		listaRegimesNaoPermitidos.add(DominioRegimeProcedimentoCirurgicoSus.HOSPITAL_DIA);
		listaRegimesNaoPermitidos.add(DominioRegimeProcedimentoCirurgicoSus.INTERNACAO_ATE_72H);
		listaRegimesNaoPermitidos.add(DominioRegimeProcedimentoCirurgicoSus.INTERNACAO);

		if (regimeProcedSus != null && origemPacCirg != null && listaRegimesNaoPermitidos.contains(regimeProcedSus) && DominioOrigemPacienteCirurgia.A.equals(origemPacCirg)) {
			// Para o regime deste procedimento o tipo de origem permitido é a Internação.
			throw new ApplicationBusinessException(MbcProcEspPorCirurgiasRNExceptionCode.MBC_01826);
		}

	}

	/**
	 * ORADB PROCEDURE MBCK_PPC.RN_PPCP_ATU_FATURA
	 * <p>
	 * Garantir que sejam atualizadas as tabelas do faturamento (ITEM_CONTA_HOSPITALAR) quando: IND_RESP_PROC for igual a nota(na digitação da nota de sala) ou for alterada a
	 * situação do procedimento tipo nota.
	 * </p>
	 * 
	 * @param procEspPorCirurgia
	 * @throws BaseException
	 */
	public void atualizarFatura(MbcProcEspPorCirurgias procEspPorCirurgia) throws BaseException {
		// Quando a resposta for DIGITAÇÃO NOTA DE SALA
		if (DominioIndRespProc.NOTA.equals(procEspPorCirurgia.getId().getIndRespProc())) {
			this.getFaturamentoFacade().atualizarFaturamentoBlocoCirurgico(procEspPorCirurgia, null, null);
		}
	}

	/**
	 * ORADB PROCEDURE MBCK_PPC.RN_PPCP_VER_UTLZ_EQU
	 * <p>
	 * Verifica equipamento de video utilizado
	 * </p>
	 * 
	 * @param procEspPorCirurgia
	 * @throws BaseException
	 */
	public void verificarUtilizacaoEquipamento(MbcProcEspPorCirurgias procEspPorCirurgia) throws BaseException {

		// Quando a resposta for AGENDAMENTO
		if (DominioIndRespProc.AGND.equals(procEspPorCirurgia.getId().getIndRespProc())) {

			// Obtém o percentual para avisar cotas de exames nos projetos de pesquisa
			AghParametros parametroGrupoVideoaparoscopia = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_GRUPO_VIDEOLAPAROSCOPIA);
			Short valorGrupoEquipamento = parametroGrupoVideoaparoscopia.getVlrNumerico().shortValue();

			// Obtém o percentual para avisar cotas de exames nos projetos de pesquisa
			AghParametros parametroEquipamentoVideolap = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_EQUIPAMENTO_VIDEOLAP);
			Short valorEquipamentoVideo = parametroEquipamentoVideolap.getVlrNumerico().shortValue();

			// Pesquisa grupo do procedimento			
			MbcProcedimentoCirurgicos procedimentoCirurgico = getMbcProcedimentoCirurgicoDAO().obterPorChavePrimaria(procEspPorCirurgia.getId().getEprPciSeq());
			
			List<MbcProcedimentoPorGrupo> listaProcedimentoPorGrupo = this.getMbcProcedimentoPorGrupoDAO().pesquisarGrupoPorProcedimento(
					procedimentoCirurgico.getSeq());

			// Obtém o PRIMEIRO grupo do procedimento da pesquisa grupo do procedimento
			MbcProcedimentoPorGrupo procedimentoPorGrupo = listaProcedimentoPorGrupo != null && !listaProcedimentoPorGrupo.isEmpty() ? listaProcedimentoPorGrupo.get(0) : null;

			if (procedimentoPorGrupo != null) {

				MbcGrupoProcedCirurgico grupoProcedCirurgico = procedimentoPorGrupo.getMbcGrupoProcedCirurgico();

				// Verifica se o valor do grupo do procedimento cirurgico equivale ao valor do grupo do equipamento informado no parâmetro
				if (grupoProcedCirurgico != null && grupoProcedCirurgico.getSeq().equals(valorGrupoEquipamento)) {

					// Obtém a data de previsão para a cirurgia
					MbcCirurgias cirurgia = procEspPorCirurgia.getCirurgia();
					Date valorDataHoraInicio = cirurgia.getDataPrevisaoInicio();
					if (valorDataHoraInicio == null) {
						return;
					}

					// Obtém a quantidade de equipamento na unidade
					final Short valorQuantidade = this.getMbcEquipamentoCirgPorUnidDAO().obterQuantidadePorId(valorEquipamentoVideo, cirurgia.getUnidadeFuncional().getSeq());

					// Obtém a quantidade de cirurgias com det proc. no dia e unidade
					List<Integer> listaPciSeq = this.getMbcProcedimentoPorGrupoDAO().pesquisarVerificarUtilizacaoEquipamentoPorGrupo(grupoProcedCirurgico.getSeq());
					List<MbcProcEspPorCirurgias> listaProcEspPorCirurgias = this.getMbcProcEspPorCirurgiasDAO().pesquisarVerificarUtilizacaoEquipamento(cirurgia.getSeq(),
							cirurgia.getUnidadeFuncional().getSeq(), valorDataHoraInicio, listaPciSeq);
					final Integer valorQuantidadePpc = listaProcEspPorCirurgias.size();

					// Verifica se a quantidade de cirurgias com o procedimento é maior que o número de equipamentos disponíveis na unidade
					if (CoreUtil.maiorOuIgual(valorQuantidadePpc, valorQuantidade)) {

						// Pesquisa cirurgias agendadas com proc. na colisão de horário
						List<MbcProcEspPorCirurgias> listaCirurgias = this.getMbcProcEspPorCirurgiasDAO().pesquisarCirurgiasAgendadasColisaoHorario(cirurgia.getSeq(),
								cirurgia.getUnidadeFuncional().getSeq(), valorDataHoraInicio, listaPciSeq);

						// Contador da quantidade de cirurgias encontradas
						Short valorQuantidadeCirurgiasColisao = 0;

						// Percorre a lista de cirurgias
						for (MbcProcEspPorCirurgias pcc : listaCirurgias) {

							MbcCirurgias cirurgiaItem = pcc.getCirurgia();

							// Verifica colisão de horários das cirurgias com o procedimento
							if (!(DateUtil.validaDataMenor(cirurgiaItem.getDataPrevisaoInicio(), valorDataHoraInicio) && DateUtil.validaDataMenorIgual(
									cirurgiaItem.getDataPrevisaoInicio(), valorDataHoraInicio))
									&& !DateUtil.validaDataMaiorIgual(cirurgiaItem.getDataPrevisaoInicio(), valorDataHoraInicio)) {

								valorQuantidadeCirurgiasColisao++;
							}

						}

						if (CoreUtil.maiorOuIgual(valorQuantidadeCirurgiasColisao, valorQuantidade)) {
							// Todos os equipamentos de vídeo utilizados para este tipo de procedimento já se encontram reservados neste horário. Verifique e altere o agendamento
							throw new ApplicationBusinessException(MbcProcEspPorCirurgiasRNExceptionCode.MBC_00535);
						}
					}

				}

			}
		}
	}

	/**
	 * ORADB PROCEDURE MBCK_PPC.RN_PPCP_ATU_AGENDA
	 * <p>
	 * Persiste ou remove o procedimento no portal de agendamento de cirurgias
	 * </p>
	 * 
	 * @param operacaoBanco
	 *            tipo de operação no banco
	 * @param cirurgia
	 * @param novo
	 *            registro novo (Inserido ou atualizado)
	 * @param antigo
	 *            registro antigo (antes de ser atualizado ou removido)
	 * @throws BaseException
	 */
	public void atualizarAgenda(DominioOperacaoBanco operacaoBanco, MbcCirurgias cirurgia, MbcProcEspPorCirurgias novo, MbcProcEspPorCirurgias antigo
			) throws BaseException {

		if ((DominioOperacaoBanco.INS.equals(operacaoBanco) || DominioOperacaoBanco.UPD.equals(operacaoBanco)) && !DominioIndRespProc.AGND.equals(novo.getId().getIndRespProc())) {
			// Retorna quando a resposta for diferente de AGENDAMENTO
			return;
		}

		// Obtém uma cirurgia com agenda gerada pelo sistema
		MbcCirurgias cirurgiasAgendaSistema = this.getMbcCirurgiasDAO().obterCirurgiaAgendadaGeradaSistema(cirurgia.getSeq());

		if (cirurgiasAgendaSistema == null) {
			return;
		}

		if (DominioOperacaoBanco.INS.equals(operacaoBanco) && !novo.getIndPrincipal()) { // INSERIR

			MbcAgendaProcedimento agendaProcedimento = new MbcAgendaProcedimento();

			agendaProcedimento.setMbcAgendas(cirurgiasAgendaSistema.getAgenda());
			agendaProcedimento.setMbcEspecialidadeProcCirgs(novo.getMbcEspecialidadeProcCirgs());
			agendaProcedimento.setCriadoEm(new Date());
			agendaProcedimento.setRapServidores(servidorLogadoFacade.obterServidorLogado());
			agendaProcedimento.setQtde(novo.getQtd().shortValue());

			// Insere na agenda de procedimentos
			getMbcAgendaProcedimentoRN().persistirAgendaProcedimentos(null, agendaProcedimento);

		} else if (DominioOperacaoBanco.UPD.equals(operacaoBanco)) { // ATUALIZAR

			if (Boolean.FALSE.equals(antigo.getIndPrincipal()) && Boolean.TRUE.equals(novo.getIndPrincipal())) {

				MbcAgendaProcedimentoId id = new MbcAgendaProcedimentoId();
				id.setAgdSeq(cirurgiasAgendaSistema.getAgenda().getSeq());
				id.setEprEspSeq(antigo.getMbcEspecialidadeProcCirgs().getId().getEspSeq());
				id.setEprPciSeq(antigo.getMbcEspecialidadeProcCirgs().getId().getPciSeq());

				MbcAgendaProcedimento agendaProcedimento = getMbcAgendaProcedimentoDAO().obterOriginal(id);

				// REMOVE da agenda de procedimentos
				getMbcAgendaProcedimentoRN().deletar(agendaProcedimento);

			} else if (Boolean.TRUE.equals(antigo.getIndPrincipal()) && Boolean.FALSE.equals(novo.getIndPrincipal())) {

				MbcAgendaProcedimento agendaProcedimento = new MbcAgendaProcedimento();

				agendaProcedimento.setMbcAgendas(cirurgiasAgendaSistema.getAgenda());
				agendaProcedimento.setMbcEspecialidadeProcCirgs(novo.getMbcEspecialidadeProcCirgs());
				agendaProcedimento.setCriadoEm(new Date());
				agendaProcedimento.setRapServidores(servidorLogadoFacade.obterServidorLogado());
				agendaProcedimento.setQtde(novo.getQtd().shortValue());

				// INSERE na agenda de procedimentos
				getMbcAgendaProcedimentoRN().persistirAgendaProcedimentos(null,agendaProcedimento);

			} else if (!novo.getIndPrincipal()) {

				MbcAgendaProcedimentoId id = new MbcAgendaProcedimentoId();
				id.setAgdSeq(cirurgiasAgendaSistema.getAgenda().getSeq());
				id.setEprEspSeq(antigo.getMbcEspecialidadeProcCirgs().getId().getEspSeq());
				id.setEprPciSeq(antigo.getMbcEspecialidadeProcCirgs().getId().getPciSeq());

				MbcAgendaProcedimento agendaProcedimentoOld = getMbcAgendaProcedimentoDAO().obterOriginal(id);
				MbcAgendaProcedimento agendaProcedimento = getMbcAgendaProcedimentoDAO().obterOriginal(id);

				agendaProcedimento.setMbcAgendas(cirurgiasAgendaSistema.getAgenda());
				agendaProcedimento.setMbcEspecialidadeProcCirgs(novo.getMbcEspecialidadeProcCirgs());
				agendaProcedimento.setQtde(novo.getQtd().shortValue());

				// ATUALIZA na agenda de procedimentos
				getMbcAgendaProcedimentoRN().persistirAgendaProcedimentos(agendaProcedimentoOld, agendaProcedimento);

			}

			// Atualiza da agenda de procedimentos

		} else if (DominioOperacaoBanco.DEL.equals(operacaoBanco) && !antigo.getIndPrincipal()) { // REMOVER

			MbcAgendaProcedimentoId id = new MbcAgendaProcedimentoId();
			id.setAgdSeq(cirurgiasAgendaSistema.getAgenda().getSeq());
			id.setEprEspSeq(antigo.getMbcEspecialidadeProcCirgs().getId().getEspSeq());
			id.setEprPciSeq(antigo.getMbcEspecialidadeProcCirgs().getId().getPciSeq());

			MbcAgendaProcedimento agendaProcedimento = getMbcAgendaProcedimentoDAO().obterOriginal(id);

			// REMOVE da agenda de procedimentos
			getMbcAgendaProcedimentoRN().deletar(agendaProcedimento);

		}

	}

	/**
	 * ORADB PROCEDURE RN_CRGP_VER_PROC_ESP
	 * @param procEspPorCirurgia
	 * @throws BaseException
	 */
	@SuppressWarnings("ucd")
	public void verificarProcedimentoEspecialidade(Integer crgSeq, DominioIndRespProc indRespProc) throws BaseException {
		
		List<MbcProcEspPorCirurgias> listProcEspPorCirurgias = this.getMbcProcEspPorCirurgiasDAO().pesquisarMbcProcEspPorCirurgiasCadastrados(crgSeq, indRespProc);
		
		if(listProcEspPorCirurgias == null || listProcEspPorCirurgias.isEmpty()){
			/* Procedimento principal  não cadastrado para esta cirurgia */
			throw new ApplicationBusinessException(MbcProcEspPorCirurgiasRNExceptionCode.MBC_00340);
			
		}else if(listProcEspPorCirurgias.size() > 1){
			/* Só pode existir um procedimento principal cadastrado por cirurgia */
			throw new ApplicationBusinessException(MbcProcEspPorCirurgiasRNExceptionCode.MBC_00459);
		}
	}

	/*
	 * Getters Facades, RNs e DAOs
	 */
	protected ICascaFacade getICascaFacade() {
		return this.iCascaFacade;
	}

	protected IExamesFacade getExamesFacade() {
		return this.iExamesFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return iParametroFacade;
	}

	protected IFaturamentoFacade getFaturamentoFacade() {
		return iFaturamentoFacade;
	}

	protected ISolicitacaoExameFacade getSolicitacaoExameFacade() {
		return iSolicitacaoExameFacade;
	}
	
	protected MbcProcEspPorCirurgiasDAO getMbcProcEspPorCirurgiasDAO() {
		return mbcProcEspPorCirurgiasDAO;
	}

	protected MbcEspecialidadeProcCirgsDAO getMbcEspecialidadeProcCirgsDAO() {
		return mbcEspecialidadeProcCirgsDAO;
	}

	protected MbcControleEscalaCirurgicaDAO getMbcControleEscalaCirurgicaDAO() {
		return mbcControleEscalaCirurgicaDAO;
	}

	protected MbcCirurgiasDAO getMbcCirurgiasDAO() {
		return mbcCirurgiasDAO;
	}
	
	protected MbcProcedimentoCirurgicoDAO getMbcProcedimentoCirurgicoDAO(){
		return mbcProcedimentoCirurgicoDAO;
	}

	protected MbcProcEspPorCirurgiaJnDAO getMbcProcEspPorCirurgiaJnDAO() {
		return mbcProcEspPorCirurgiaJnDAO;
	}

	protected MbcEquipamentoCirgPorUnidDAO getMbcEquipamentoCirgPorUnidDAO() {
		return mbcEquipamentoCirgPorUnidDAO;
	}

	protected MbcProcedimentoPorGrupoDAO getMbcProcedimentoPorGrupoDAO() {
		return mbcProcedimentoPorGrupoDAO;
	}

	protected MbcAgendaProcedimentoRN getMbcAgendaProcedimentoRN() {
		return mbcAgendaProcedimentoRN;
	}

	protected MbcAgendaProcedimentoDAO getMbcAgendaProcedimentoDAO() {
		return mbcAgendaProcedimentoDAO;
	}
	
	protected MbcCompSangProcCirgDAO getMbcCompSangProcCirgDAO() {
		return mbcCompSangProcCirgDAO;
	}
	
	protected MbcProfCirurgiasDAO getMbcProfCirurgiasDAO() {
		return mbcProfCirurgiasDAO;
	}

	public EmailUtil getEmailUtil() {
		return emailUtil;
	}

}
