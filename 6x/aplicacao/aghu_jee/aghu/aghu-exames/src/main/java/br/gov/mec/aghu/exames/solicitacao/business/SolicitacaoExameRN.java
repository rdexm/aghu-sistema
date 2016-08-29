package br.gov.mec.aghu.exames.solicitacao.business;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.*;
import br.gov.mec.aghu.exames.dao.*;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.contratualizacao.vo.ItemContratualizacaoVO;
import br.gov.mec.aghu.exames.questionario.business.IQuestionarioExamesFacade;
import br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameVO;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelDoadorRedome;
import br.gov.mec.aghu.model.AelExtratoDoador;
import br.gov.mec.aghu.model.AelProjetoPesquisas;
import br.gov.mec.aghu.model.AelRespostaQuestao;
import br.gov.mec.aghu.model.AelSolicitacaoExameJn;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.vo.ConvenioExamesLaudosVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

/**
 * Responsavel pelas regras de negocio da entidade AelSolicitacaoExames.<br>
 * 
 * 
 * Tabela: AEL_SOLICITACAO_EXAMES.
 * 
 * 
 * @author rcorvalao
 * 
 */
@Stateless
public class SolicitacaoExameRN extends BaseBusiness {

	@EJB
	private SolicitacaoExameEnforceRN solicitacaoExameEnforceRN;
	
	@EJB
	private ItemSolicitacaoExameRN itemSolicitacaoExameRN;
	
	private static final Log LOG = LogFactory.getLog(SolicitacaoExameRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IQuestionarioExamesFacade questionarioExamesFacade;
	
	@Inject
	private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@Inject
	private AelSolicitacaoExameDAO aelSolicitacaoExameDAO;

    @Inject
    private AelExtratoDoadorDAO aelExtratoDoadorDAO;

    @Inject
    private AelDoadorRedomeDAO aelDoadorRedomeDAO;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private ISolicitacaoExameFacade solicitacaoExameFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private AelSolicitacaoExameJnDAO aelSolicitacaoExameJnDAO;
	
	@EJB
	private ICascaFacade cascaFacade;	

	private static final long serialVersionUID = 8002962673564504381L;
	
	public enum SolicitacaoExameRNExceptionCode implements BusinessExceptionCode {
		AEL_00395, AEL_00396, AEL_01261, AEL_00407, AEL_00369, AEL_00421, AEL_00422, MENS_SEM_ITENS_SOLICITACAO_EXAME, MENSAGEM_ITEM_NULO;
	}

	public AelSolicitacaoExames inserirSolicitacaoExame(AelSolicitacaoExames solicitacaoExame, String nomeMicrocomputador) throws BaseException {
		// Regras pré-insert
		this.preInsert(solicitacaoExame);
		getAelSolicitacaoExamesDAO().persistir(solicitacaoExame);
		getAelSolicitacaoExamesDAO().flush();
		return solicitacaoExame;
	}

	public AelSolicitacaoExames atualizarSolicitacaoExame(AelSolicitacaoExames solicExame, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		AelSolicitacaoExames returnValue = null;
		AelSolicitacaoExames oldEntity = this.getAelSolicitacaoExamesDAO().obterOriginal(solicExame.getSeq());

		this.preAtualizar(oldEntity, solicExame);
		returnValue = this.getAelSolicitacaoExamesDAO().atualizar(solicExame);
		this.posAtualizacao(oldEntity, returnValue, nomeMicrocomputador, dataFimVinculoServidor);
		this.getAelSolicitacaoExamesDAO().flush();
		return returnValue;
	}

	/**
	 * Insere objeto AelSolicitacaoExames e os itens associados.
	 * @param {AelSolicitacaoExames} solicitacaoExame
	 * @return AelSolicitacaoExames
	 */
	public AelSolicitacaoExames inserir(AelSolicitacaoExames solicitacaoExame, String nomeMicrocomputador, final Date dataFimVinculoServidor
			,RapServidores servidorLogado) throws BaseException{
		if(solicitacaoExame == null || solicitacaoExame.getItensSolicitacaoExame() == null || solicitacaoExame.getItensSolicitacaoExame().isEmpty()) {
			throw new ApplicationBusinessException(SolicitacaoExameRNExceptionCode.MENS_SEM_ITENS_SOLICITACAO_EXAME);
		}		

		// Regras pré-insert
		this.preInsert(solicitacaoExame);

		getAelSolicitacaoExamesDAO().persistir(solicitacaoExame);
		getAelSolicitacaoExamesDAO().flush();

		solicitacaoExame = getAelSolicitacaoExamesDAO().obterPorChavePrimaria(solicitacaoExame.getSeq());

		for (AelItemSolicitacaoExames item : solicitacaoExame.getItensSolicitacaoExame()) {

			AelItemSolicitacaoExames itemSolicExamesClone = null;
			try {
				itemSolicExamesClone = (AelItemSolicitacaoExames) BeanUtils.cloneBean(item);
			} catch (Exception e) {
				LOG.error(e.getMessage(),e);
			}
			
			itemSolicExamesClone.setId(null);
			itemSolicExamesClone.setSolicitacaoExame(solicitacaoExame);
			
			final List<AelRespostaQuestao> respostas = itemSolicExamesClone.getAelRespostasQuestoes();

			this.getItemSolicitacaoExameRN().inserir(itemSolicExamesClone, nomeMicrocomputador, servidorLogado, dataFimVinculoServidor, true);
			
			// Para cada dependente OPCIONAL ou OBRIGATORIO associa ao item pai e grava.
			List<AelItemSolicitacaoExames> dependentes = item.getItemSolicitacaoExames();
			for (AelItemSolicitacaoExames dependente : dependentes) {
				AelItemSolicitacaoExames itemDependente = null;
				try {
				itemDependente = (AelItemSolicitacaoExames) BeanUtils.cloneBean(dependente);
				} catch (Exception e) {
					LOG.error(e.getMessage(),e);					
				}
				
				itemDependente.setId(null);
				itemDependente.setSolicitacaoExame(solicitacaoExame);
				itemDependente.setItemSolicitacaoExame(itemSolicExamesClone);

				this.getItemSolicitacaoExameRN().inserirExameDependente(itemDependente, nomeMicrocomputador, dataFimVinculoServidor, true, servidorLogado);
			}
			// #2257
			if (respostas != null && !respostas.isEmpty()) {
				for (final AelRespostaQuestao resposta : respostas) {
					resposta.setAelItemSolicitacaoExames(itemSolicExamesClone);
					if (resposta.getAelValorValidoQuestao() != null) {
						resposta.setResposta(resposta.getAelValorValidoQuestao().getValorSignificado());
					}
					if (resposta.getAghCid() != null) {
						resposta.setResposta(resposta.getAghCid().getCodigo());
					}
					this.getQuestionarioExamesFacade().persistirAelRespostaQuestao(resposta);
				}
			}
		}
		this.flush();

		if (!StringUtils.isEmpty(solicitacaoExame.getLocalizador())) {
			
			try {
				this.getSolicitacaoExameFacade().inserirStatusInternet(
						solicitacaoExame, null, null,
						DominioSituacaoExameInternet.R,
						DominioStatusExameInternet.NO, null,
						solicitacaoExame.getServidor());
			} catch (Exception e) {
				LOG.error("Exceção capturada: ", e);
			}
		}
		
		solicitacaoExame.setItensSolicitacaoExame(aelItemSolicitacaoExameDAO.pesquisarItemSolicitacaoExamePorSolicitacaoExame(solicitacaoExame.getSeq()));
		return solicitacaoExame;
	}

	/**
	 * Insere objeto AelSolicitacaoExames e os itens associados.
	 * @param {AelSolicitacaoExames} solicitacaoExame
	 * @return AelSolicitacaoExames
	 */
	public List<ItemContratualizacaoVO> inserirContratualizacao(AelSolicitacaoExames solicitacaoExame, 
			List<ItemContratualizacaoVO> listaItensVO, String nomeMicrocomputador, final Date dataFimVinculoServidor,
			RapServidores servidorLogado) throws BaseException {
		if(solicitacaoExame == null || listaItensVO == null || listaItensVO.isEmpty()) {
			throw new ApplicationBusinessException(SolicitacaoExameRNExceptionCode.MENS_SEM_ITENS_SOLICITACAO_EXAME);
		}
		for (ItemContratualizacaoVO itemContratualizacaoVO : listaItensVO) {
			if (itemContratualizacaoVO.getItemSolicitacaoExames() == null) {
				throw new ApplicationBusinessException(SolicitacaoExameRNExceptionCode.MENSAGEM_ITEM_NULO);
			}
		}
		// Regras pré-insert
		this.preInsert(solicitacaoExame);
		getAelSolicitacaoExamesDAO().persistir(solicitacaoExame);
		getAelSolicitacaoExamesDAO().flush();

		for (ItemContratualizacaoVO itemVO : listaItensVO) {
			try {
				if (StringUtils.isEmpty(itemVO.getMensagemErro())) { //Se nao ocorreu um erro anteriormente, tenta inserir no banco o item
					AelItemSolicitacaoExames item = itemVO.getItemSolicitacaoExames();
					item.setId(null);
					item.setSolicitacaoExame(solicitacaoExame);

					AelItemSolicitacaoExames itemPersistido = null;
					itemPersistido = this.getItemSolicitacaoExameRN().inserirContratualizacao(item, nomeMicrocomputador, 
							dataFimVinculoServidor, true, servidorLogado);

					// Para cada dependente OPCIONAL ou OBRIGATORIO associa ao item pai e grava.
					List<AelItemSolicitacaoExames> dependentes = item.getItemSolicitacaoExames();
					for (AelItemSolicitacaoExames dependente : dependentes) {
						dependente.setId(null);
						dependente.setSolicitacaoExame(solicitacaoExame);
						dependente.setItemSolicitacaoExame(itemPersistido);

						this.getItemSolicitacaoExameRN().inserirContratualizacao(dependente, nomeMicrocomputador, 
								dataFimVinculoServidor, true, servidorLogado);
					}
				}
			} catch (BaseException e) {
				itemVO.setMensagemErro(e.getMessage());
				logError(e.getMessage(), e);
			} catch (Exception e) {
				itemVO.setMensagemErro(getResourceBundleValue("ERRO_GENERICO_CONTRATUALIZACAO"));
				logError(e.getMessage(), e);
			}
		}
		return listaItensVO;
	}

	/**
	 * ORADB Trigger AELT_SOE_BRI.<br>
	 * @param {AelSolicitacaoExames} solicitacaoExame
	 */
	public void preInsert(AelSolicitacaoExames solicitacaoExame)
	throws BaseException {

		this.atualizaUsuarioEDataHoraCriacao(solicitacaoExame);

		this.verificaUnidadeFuncional(solicitacaoExame);

		this.verificaAtendimento(solicitacaoExame);

		this.atualizaConvenio(solicitacaoExame);

		this.atualizaProjetoPesquisa(solicitacaoExame);
		
		this.atualizaLocalizador(solicitacaoExame);

	}

	/**
	 * Atualiza AelSolicitacaoExames e<br> 
	 * Remove, insere e atualiza os AelItemSolicitacaoExames associados.<br>
	 * @param servidorLogado 
	 */
	public AelSolicitacaoExames atualizar(AelSolicitacaoExames solicExame, List<AelItemSolicitacaoExames> itemSolicExameExcluidos, String nomeMicrocomputador, RapServidores servidorLogado, final Date dataFimVinculoServidor) throws BaseException {
		AelSolicitacaoExames returnValue = null;
		AelSolicitacaoExames oldEntity = this.getAelSolicitacaoExamesDAO().obterPeloId(solicExame.getSeq());

		this.preAtualizar(oldEntity, solicExame);

		returnValue = this.getAelSolicitacaoExamesDAO().atualizar(solicExame);
		this.getAelSolicitacaoExamesDAO().flush();

		if (itemSolicExameExcluidos != null) {
			for (AelItemSolicitacaoExames item : itemSolicExameExcluidos) {
				this.getItemSolicitacaoExameRN().remover(item);
			}
		}
		for (AelItemSolicitacaoExames item : solicExame.getItensSolicitacaoExame()) {
			if (item.getId() == null) {
				this.getItemSolicitacaoExameRN().inserir(item, nomeMicrocomputador, servidorLogado, dataFimVinculoServidor, true);
			} else {
				this.getItemSolicitacaoExameRN().atualizar(item, null, nomeMicrocomputador, dataFimVinculoServidor,servidorLogado);
			}
		}

		this.posAtualizacao(oldEntity, returnValue, nomeMicrocomputador, new Date());
		return returnValue;
	}

	/**
	 * BEFORE UPDATE ON AEL_SOLICITACAO_EXAMES FOR EACH ROW.<br>
	 * ORADB TRIGGER AELT_SOE_BRU.<br>
	 * ORADB TRIGGER AELT_SOE_BSU.<br>
	 */
	protected void preAtualizar(AelSolicitacaoExames oldEntity, AelSolicitacaoExames newEntity) throws BaseException {
		verificarCamposAlterados(oldEntity, newEntity);
		verificarInformacoesClinicas(oldEntity, newEntity);
	}

	/** Verifica se a informação clínica foi modificada. Se sim e se o ATD_SEQ não for nulo e se a nova informação clínica for nula,
	 * busca os exames daquela solicitação e verifica se algum possui o índice "ind_exige_info_clin"  = 'S'. Se sim, sobe o erro AEL-00422 
	 * Trecho PL:
	 * BEGIN
		   / * RN_SOE006 - Não permite alterar para null as informações clínicas
			se algum exame não cancelado da solicitação exigir. * /
		   IF aghk_util.modificados (:old.informacoes_clinicas,
			:new.informacoes_clinicas) THEN
		       IF :new.atd_seq IS NOT NULL THEN
		           aelk_soe_rn.rn_soep_ver_info_cli (:new.seq,
			:new.informacoes_clinicas);
		       END IF;
		   END IF;
		END;
	 * @param oldEntity
	 * @param newEntity
	 * @throws BaseException
	 */
	protected void verificarInformacoesClinicas(AelSolicitacaoExames oldEntity, AelSolicitacaoExames newEntity) throws BaseException {
		if (CoreUtil.modificados(oldEntity.getInformacoesClinicas(), newEntity.getInformacoesClinicas())
				&& newEntity.getAtendimento() != null && newEntity.getAtendimento().getSeq() != null
				&& newEntity.getInformacoesClinicas() == null) {
			//Busca parâmetro do banco
			AghParametros param = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_CANCELADO);
			if (param == null || param.getVlrTexto() == null) {
				throw new ApplicationBusinessException(SolicitacaoExameRNExceptionCode.AEL_00421);
			}
			if (verificarInfClinicasItensSolicExames(param.getVlrTexto(), newEntity.getSeq())) {
				throw new ApplicationBusinessException(SolicitacaoExameRNExceptionCode.AEL_00422);
			}
		}
	}

	/**
	 * Realiza uma busca de todos os itens de exames da solicitação para verificar se algum possui
	 * a propriedade ind_exige_info_clin igual a 'S'. Se algum possuir retorna true, senão retorna false.
	 */
	protected boolean verificarInfClinicasItensSolicExames(String parametroCA, Integer soeSeq) {
		boolean possuiInfClinica = false;

		List <AelItemSolicitacaoExames> lista = getAelItemSolicitacaoExameDAO().pesquisarItemSolicitacaoExamePorSituacaoSOE(parametroCA, soeSeq);
		if (lista != null) {
			for (AelItemSolicitacaoExames itemSolicitacaoExames : lista) {
				AelUnfExecutaExames unfExecEx = itemSolicitacaoExames.getAelUnfExecutaExames();
				if (unfExecEx != null && unfExecEx.getIndExigeInfoClin()) {
					possuiInfClinica = true;
					break;
				}
			}
		}
		return possuiInfClinica;
	}

	protected AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO() {
		return aelItemSolicitacaoExameDAO;		
	}

	/**
	 * Verifica se os campos CRIADO_EM ou SERVIDOR foram alterados. Se sim estoura Exceção AEL_00369.
	 *
	 */
	protected void verificarCamposAlterados(AelSolicitacaoExames oldEntity, AelSolicitacaoExames newEntity) throws BaseException {
		if (CoreUtil.modificados(oldEntity.getCriadoEm(), newEntity.getCriadoEm()) || CoreUtil.modificados(oldEntity.getServidor(), newEntity.getServidor())) {
			throw new ApplicationBusinessException(SolicitacaoExameRNExceptionCode.AEL_00369);
		}
	}

	/**
	 * AFTER UPDATE ON AEL_SOLICITACAO_EXAMES FOR EACH ROW.<br>
	 * ORABD TRIGGER AELT_SOE_ARU.<br>
	 * ORADB TRIGGER AELT_SOE_ASU.<br>
	 */
	protected void posAtualizacao(AelSolicitacaoExames oldEntity, AelSolicitacaoExames newEntity, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		if (verificarModificacoesInserirJn(oldEntity, newEntity)) {
			AelSolicitacaoExameJn aelSolicitacaoExameJn =  criarAelSolicitacaoExameJn(newEntity, DominioOperacoesJournal.UPD);
			getAelSolicitacaoExameJnDAO().persistir(aelSolicitacaoExameJn);
		}
		getSolicitacaoExameEnforceRN().enforceUpdate(newEntity, nomeMicrocomputador, dataFimVinculoServidor);
	}

	protected AelSolicitacaoExameJn criarAelSolicitacaoExameJn(
			AelSolicitacaoExames newEntity, DominioOperacoesJournal upd) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AelSolicitacaoExameJn aelSolicitacaoExameJn = BaseJournalFactory.getBaseJournal(upd, AelSolicitacaoExameJn.class, servidorLogado.getUsuario());

		aelSolicitacaoExameJn.setAtendimento(newEntity.getAtendimento());
		aelSolicitacaoExameJn.setUnidadeFuncional(newEntity.getUnidadeFuncional());
		aelSolicitacaoExameJn.setRecemNascido(newEntity.getRecemNascido());
		aelSolicitacaoExameJn.setCriadoEm(newEntity.getCriadoEm());
		aelSolicitacaoExameJn.setServidor(newEntity.getServidor());
		aelSolicitacaoExameJn.setInformacoesClinicas(newEntity.getInformacoesClinicas());
		aelSolicitacaoExameJn.setServidorResponsabilidade(newEntity.getServidorResponsabilidade());
		aelSolicitacaoExameJn.setServidorAlterado(newEntity.getServidorAlterado());
		aelSolicitacaoExameJn.setConvenioSaude(newEntity.getConvenioSaude());
		aelSolicitacaoExameJn.setConvenioSaudePlano(newEntity.getConvenioSaudePlano());
		aelSolicitacaoExameJn.setSeq(newEntity.getSeq());
		//		aelSolicitacaoExameJn.setUnidadeFuncionalAreaExecutora(newEntity.getUnidadeFuncionalAreaExecutora());
		//		aelSolicitacaoExameJn.setUsaAntimicrobianos(newEntity.getUsaAntimicrobianos());
		//		aelSolicitacaoExameJn.setIndTransplante(newEntity.getIndTransplante());
		//		aelSolicitacaoExameJn.setProjetoPesquisa(newEntity.getProjetoPesquisa());
		//		aelSolicitacaoExameJn.setAtendimentoDiverso(newEntity.getAtendimentoDiverso());
		//		aelSolicitacaoExameJn.setRegistro(newEntity.getRegistro());
		//		aelSolicitacaoExameJn.setIndObjetivoSolic();
		return aelSolicitacaoExameJn;
	}

	protected AelSolicitacaoExameJnDAO getAelSolicitacaoExameJnDAO() {
		return aelSolicitacaoExameJnDAO;
	}

	protected boolean verificarModificacoesInserirJn(AelSolicitacaoExames oldEntity, AelSolicitacaoExames newEntity) {

		if (CoreUtil.modificados(newEntity.getSeq(), oldEntity.getSeq())
				|| CoreUtil.modificados(newEntity.getAtendimento(), oldEntity.getAtendimento())
				|| CoreUtil.modificados(newEntity.getUnidadeFuncional(), oldEntity.getUnidadeFuncional())
				|| CoreUtil.modificados(newEntity.getRecemNascido(), oldEntity.getRecemNascido())
				|| CoreUtil.modificados(newEntity.getCriadoEm(), oldEntity.getCriadoEm())
				|| CoreUtil.modificados(newEntity.getServidor(), oldEntity.getServidor())
				|| CoreUtil.modificados(newEntity.getInformacoesClinicas(), oldEntity.getInformacoesClinicas())
				|| CoreUtil.modificados(newEntity.getServidorResponsabilidade(), oldEntity.getServidorResponsabilidade())
				|| CoreUtil.modificados(newEntity.getServidorAlterado(), oldEntity.getServidorAlterado())
				|| CoreUtil.modificados(newEntity.getConvenioSaude(), oldEntity.getConvenioSaude())
				|| CoreUtil.modificados(newEntity.getConvenioSaudePlano(), oldEntity.getConvenioSaudePlano())
				|| CoreUtil.modificados(newEntity.getUnidadeFuncionalAreaExecutora(), oldEntity.getUnidadeFuncionalAreaExecutora())
				|| CoreUtil.modificados(newEntity.getUsaAntimicrobianos(), oldEntity.getUsaAntimicrobianos())
				|| CoreUtil.modificados(newEntity.getIndTransplante(), oldEntity.getIndTransplante())
				|| CoreUtil.modificados(newEntity.getProjetoPesquisa(), oldEntity.getProjetoPesquisa())
				|| CoreUtil.modificados(newEntity.getAtendimentoDiverso(), oldEntity.getAtendimentoDiverso())
				|| CoreUtil.modificados(newEntity.getRegistro(), oldEntity.getRegistro())
				|| CoreUtil.modificados(newEntity.getIndObjetivoSolic(), oldEntity.getIndObjetivoSolic())) {
			return true;
		}
		return false;
	}

	/**
	 * Atualiza os dados de usuário com as informações do usuário logado. Pega
	 * informações de login do usuário logado e seta no campo de servidor do
	 * exame. Além disse seta a data CRIADO_EM para data/hora corrente do
	 * sistema.
	 * 18/04/2011
	 * @param {AelSolicitacaoExames} solicitacaoExame
	 * @throws ApplicationBusinessException
	 *             (RAP_00175)
	 */
	protected void atualizaUsuarioEDataHoraCriacao(AelSolicitacaoExames solicitacaoExame) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		// Seta campos de usuário com os dados do usuário logado
		solicitacaoExame.setServidor(servidorLogado);

		// Seta data CRIADO_EM para data/hora corrente do sistema
		solicitacaoExame.setCriadoEm(new Date());
	}

	/**
	 * ORADB PACKAGE/PROCEDURE AELK_SOE_RN.RN_SOEP_VER_UNF_RN
	 * 
	 * A unidade funcional deve estar ativa. Se não informada, deve ser a
	 * unidade funcional do atendimento. Se a unidade solicitante for obstétrica
	 * ou CO, deve exigir a informação de recém nascido. Caso constrário, deve
	 * ser 'N'.
	 * 18/04/2011
	 * 
	 * @param {AelSolicitacaoExames} solicitacaoExame
	 * @throws ApplicationBusinessException
	 *             (AEL_00395, AEL_00396 ou AEL_01261)
	 */
	protected void verificaUnidadeFuncional(
			AelSolicitacaoExames solicitacaoExame)
	throws ApplicationBusinessException {

		// Se unidade for nula, pega unidade funcional do atendimento
		if (solicitacaoExame.getUnidadeFuncional() == null
				&& solicitacaoExame.getAtendimento() != null) {

			solicitacaoExame.setUnidadeFuncional(solicitacaoExame
					.getAtendimento().getUnidadeFuncional());
		}

		// Verifica unidade funcional
		AghUnidadesFuncionais unidadeFuncional = solicitacaoExame
		.getUnidadeFuncional();
		if (unidadeFuncional != null) {

			DominioSituacao situacao = unidadeFuncional.getIndSitUnidFunc();
			if (DominioSituacao.I.equals(situacao)) {

				throw new ApplicationBusinessException(
						SolicitacaoExameRNExceptionCode.AEL_00395);
			}

			DominioSimNao ehUnidObstetrica = this.getAghuFacade()
			.verificarCaracteristicaDaUnidadeFuncional(
					unidadeFuncional.getSeq(),
					ConstanteAghCaractUnidFuncionais.UNID_OBSTETRICA);
			DominioSimNao ehUnidCO = this.getAghuFacade()
			.verificarCaracteristicaDaUnidadeFuncional(
					unidadeFuncional.getSeq(),
					ConstanteAghCaractUnidFuncionais.CO);
			if (DominioSimNao.S.equals(ehUnidObstetrica)
					|| DominioSimNao.S.equals(ehUnidCO)) {

				if (solicitacaoExame.getRecemNascido() == null) {

					throw new ApplicationBusinessException(
							SolicitacaoExameRNExceptionCode.AEL_00396);
				}

			} else {

				if (solicitacaoExame.getRecemNascido() != null
						&& solicitacaoExame.getRecemNascido()) {

					throw new ApplicationBusinessException(
							SolicitacaoExameRNExceptionCode.AEL_01261);
				}

			}

		}

	}

	/**
	 * ORADB PACKAGE/PROCEDURE AELK_SOE_RN.RN_SOEP_VER_ATENDIME
	 * 
	 * Se a origem do atendimento do paciente NÃO for Externa (‘X’) o
	 * responsável deve ser preenchido (SER_MATRICULA_EH_RESPONSABILID,
	 * SER_VIN_CODIGO_EH_RESPONSABILI), caso o responsável NÃO tenha sido
	 * preenchido a aplicação deve exibir o erro “Para atendimento de origem
	 * diferente de Paciente Externo, responsável deve ser preenchido.”,
	 * AEL_00407;
	 * 
	 * 18/04/2011
	 * 
	 * @param {AelSolicitacaoExames} solicitacaoExame
	 * @throws ApplicationBusinessException
	 *             (AEL_00407)
	 */
	protected void verificaAtendimento(AelSolicitacaoExames solicitacaoExame)
	throws ApplicationBusinessException {

		AghAtendimentos atendimento = solicitacaoExame.getAtendimento();
		if (atendimento != null && !DominioOrigemAtendimento.X.equals(atendimento.getOrigem())
				&& solicitacaoExame.getServidorResponsabilidade() == null) {
			throw new ApplicationBusinessException(
					SolicitacaoExameRNExceptionCode.AEL_00407);
		}
	}

	/**
	 * ORADB PACKAGE/PROCEDURE AELK_SOE_RN.RN_SOEP_ATU_CONVENIO
	 * 
	 * Atualiza do valor do convênio plano (CSP_CNV_CODIGO, CSP_SEQ). Se ATD_SEQ
	 * NÃO for nulo então busca o convênio
	 * plano de um determinado atendimento, caso a ATD_SEQ estiver nula então
	 * busca o convênio plano de um
	 * determinado atendimento diverso.
	 * 
	 * @param {AelSolicitacaoExames} solicitacaoExame
	 */
	protected void atualizaConvenio(AelSolicitacaoExames solicitacaoExame) {

		ConvenioExamesLaudosVO vo = null;
		if (solicitacaoExame.getAtendimento() != null) {
			vo = this.getPacienteFacade().buscarConvenioExamesLaudos(solicitacaoExame.getAtendimento().getSeq());
		} 
		else {			
			vo = getExamesFacade().rnAelpBusConvAtv(solicitacaoExame.getAtendimentoDiverso().getSeq());
		}

		if (vo != null) {
			FatConvenioSaudePlano convenioSaudePlano = this.getFaturamentoFacade().obterConvenioSaudePlano(vo.getCodigoConvenioSaude(), vo.getCodigoConvenioSaudePlano());
			solicitacaoExame.setConvenioSaudePlano(convenioSaudePlano);
		}

	}

	/**
	 * ORADB PACKAGE/PROCEDURE AELK_SOE_RN.RN_SOEP_ATU_PJQ_SEQ
	 * 
	 * Atualiza o valor do projeto de pesquisa (PJQ_SEQ).
	 * 
	 * 18/04/2011
	 * 
	 * @param {AelSolicitacaoExames} solicitacaoExame
	 */
	protected void atualizaProjetoPesquisa(AelSolicitacaoExames solicitacaoExame) {

		// TODO REGRA DEVE SER MIGRADA QUANDO O MODULO DE
		// PROJETO DE PESQUISA FOR MIGRADO

	}

	/**
	 * ORADB FUNCTION AELC_LOCAL_PAC
	 * 
	 * @param seqAtd
	 * @return
	 */
	public String recuperarLocalPaciente(AghAtendimentos atendimento) {

		String local = "   ";

        if (atendimento.getOrigem().equals(DominioOrigemAtendimento.A)) {

            if (getAghuFacade()
                    .verificarCaracteristicaDaUnidadeFuncional(
                            atendimento.getUnidadeFuncional().getSeq(),
                            ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA)
                    .equals(DominioSimNao.S)) {

                local = "U:EMG";

            } else {

                local = "U:AMB";

            }

        } else if (atendimento.getOrigem().equals(DominioOrigemAtendimento.X)) {

            local = "U:EXT";

        } else if (atendimento.getOrigem().equals(DominioOrigemAtendimento.D)) {

            local = "U:DOA";

        } else if (atendimento.getOrigem().equals(DominioOrigemAtendimento.C)) {

            local = "U:CIR";

        } else if (atendimento.getOrigem().equals(DominioOrigemAtendimento.U)) {

            if (atendimento.getUnidadeFuncional().getSigla() != null) {

                local = "U:" + atendimento.getUnidadeFuncional().getSigla();

            } else {

                local = "   ";

            }

        } else if (atendimento.getLeito() != null) {

            local = atendimento.getLeito().getLeitoID();

        } else if (atendimento.getQuarto() != null) {

            local = atendimento.getQuarto().getDescricao();

        } else if (atendimento.getUnidadeFuncional() != null) {

            local = "U:"
                    + atendimento.getUnidadeFuncional().getAndar().toString()
                    + " "
                    + atendimento.getUnidadeFuncional().getIndAla()
                    .getDescricao();

        }

        return local;
    }

	/**
	 * Remove todas as solicitações de exames de um dado atendimento.
	 * 
	 * @param atendimento
	 */
	public void excluirSolicitacaoExamesPorAtendimento(
			AghAtendimentos atendimento) {
		AelSolicitacaoExameDAO dao = this.getAelSolicitacaoExamesDAO();

		List<AelSolicitacaoExames> solicitacaoExamesDoAtendimento = dao
		.buscarSolicitacaoExamesPorAtendimento(atendimento);

		for (AelSolicitacaoExames solicitacaoExame : solicitacaoExamesDoAtendimento) {
			dao.remover(solicitacaoExame);
		}

	}

	public void atualizarIndImpressaoSolicitacaoExames(Integer soeSeq, String nomeMicrocomputador, final Date dataFimVinculoServidor,
			RapServidores servidorLogado) throws BaseException {
		AelSolicitacaoExameDAO dao = this.getAelSolicitacaoExamesDAO();

		AelSolicitacaoExames exame = dao.obterPorChavePrimaria(soeSeq);

		for (AelItemSolicitacaoExames item : exame.getItensSolicitacaoExame()) {
			item.setIndImprimiuTicket(true);
			getItemSolicitacaoExameRN().atualizar(item, null, nomeMicrocomputador, dataFimVinculoServidor, servidorLogado);
		}
	}

	protected IFaturamentoFacade getFaturamentoFacade() {
		return this.faturamentoFacade;
	}

	protected AelSolicitacaoExameDAO getAelSolicitacaoExamesDAO() {
		return aelSolicitacaoExameDAO;
	}

    protected AelExtratoDoadorDAO getAelExtratoDoadorDAO() {
        return aelExtratoDoadorDAO;
    }

	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}

    protected AelDoadorRedomeDAO getAelDoadorRedomeDAO() {
        return aelDoadorRedomeDAO;
    }

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	private ItemSolicitacaoExameRN getItemSolicitacaoExameRN() {
		return itemSolicitacaoExameRN;
	}

	protected SolicitacaoExameEnforceRN getSolicitacaoExameEnforceRN() {
		return solicitacaoExameEnforceRN;
	}

	protected IExamesFacade getExamesFacade() {
		return this.examesFacade;
	}

	protected IQuestionarioExamesFacade getQuestionarioExamesFacade() {
		return this.questionarioExamesFacade;
	}

	protected ISolicitacaoExameFacade getSolicitacaoExameFacade() {
		return this.solicitacaoExameFacade;
	}

	/**
	 * @ORADB AELC_GET_PROJ_RESP
	 * Busca o responsável do projeto de pesquisa de uma determinada solicitação
	 * @param seq
	 * @return
	 */
	public String buscarResponsavelProjetoPesquisaPorSolicitacaoExame(
			Integer seq) {
		AelSolicitacaoExames solicitacaoExames = getAelSolicitacaoExamesDAO().obterPeloId(seq);
		AelProjetoPesquisas projetoPesquisa = null;

		if(solicitacaoExames.getAtendimento() != null){
			if(DominioOrigemAtendimento.I.equals(solicitacaoExames.getAtendimento().getOrigem())){
				projetoPesquisa = solicitacaoExames.getAtendimento().getInternacao().getProjetoPesquisa();
			}else if(DominioOrigemAtendimento.A.equals(solicitacaoExames.getAtendimento().getOrigem())){
				if(solicitacaoExames.getAtendimento().getConsulta().getGradeAgendamenConsulta() != null){
					projetoPesquisa = solicitacaoExames.getAtendimento().getConsulta().getProjetoPesquisa();
				}
			}else if(DominioOrigemAtendimento.C.equals(solicitacaoExames.getAtendimento().getOrigem())){
				Iterator<MbcCirurgias> it = solicitacaoExames.getAtendimento().getCirurgias().iterator();
				MbcCirurgias cirurgias = it.next();
				projetoPesquisa = cirurgias.getProjetoPesquisa();
			}
		}else if(solicitacaoExames.getAtendimentoDiverso() != null){
			projetoPesquisa = solicitacaoExames.getAtendimentoDiverso().getAelProjetoPesquisas();
		}

		if(projetoPesquisa != null){
			return projetoPesquisa.getNomeResponsavel();
		}else{
			return null;
		}
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	/**
	 * ORADB AELP_BUSCA_CONSULTA
	 * @param solicitacaoExameVO
	 * @return
	 * @throws BaseException
	 */
	public RapServidores buscarConsulta(SolicitacaoExameVO solicitacaoExameVO) throws BaseException{

		RapServidores servidorOut = new RapServidores();
		//RapServidores aelk_variaveis_vk_ser__consulta;

		/**
		 * Acessa as consultas para buscar o profissional responsável
		 */

		AacConsultas consulta = getAmbulatorioFacade().obterAacConsulta(solicitacaoExameVO.getNumeroConsulta());

		if(consulta!=null && consulta.getGradeAgendamenConsulta()!=null && consulta.getGradeAgendamenConsulta().getProfServidor() == null){

			if(consulta.getGradeAgendamenConsulta().getEquipe()!=null){
				//aelp_verifica_servidor
				//aelk_variaveis_vk_ser__consulta = servidorOut;
				return this.verificarServidor(consulta.getGradeAgendamenConsulta().getEquipe().getProfissionalResponsavel());
			}

		}else if(consulta!=null && consulta.getGradeAgendamenConsulta().getProfServidor()!=null){

			//	aelk_variaveis_vk_ser__consulta = servidorOut;
			return this.verificarServidor(consulta.getGradeAgendamenConsulta().getProfServidor());

		}

		/**
		 * Acessa consultas no Bull
		 */

		if(consulta!=null){

			if(consulta.getGradeAgendamenConsulta().getProfServidor()!=null
					&& consulta.getGradeAgendamenConsulta().getProfServidor().getId().getMatricula() == 999
					&& consulta.getGradeAgendamenConsulta().getProfServidor().getId().getVinCodigo() == 99999){

				if(consulta.getGradeAgendamenConsulta().getEquipe()!=null){
					servidorOut = this.verificarServidor(consulta.getGradeAgendamenConsulta().getEquipe().getProfissionalResponsavel());
					//	aelk_variaveis_vk_ser__consulta = servidorOut;
				}
			}else{
				servidorOut = this.verificarServidor(consulta.getGradeAgendamenConsulta().getProfServidor());
				//	aelk_variaveis_vk_ser__consulta = servidorOut;
			}
		}

		return servidorOut;

	}

	/**
	 * ORADB aelp_verifica_servidor
	 */
	protected RapServidores verificarServidor(RapServidores servidorIn)	throws ApplicationBusinessException {

		RapServidores servidorOut = servidorIn;

		Integer matriculaIn = servidorIn.getId().getMatricula(); //p_matricula_in
		Short vinCodigoIn = servidorIn.getId().getVinCodigo();//p_vin_codigo_in

		List<RapServidores>  listServidores = getRegistroColaboradorFacade().pesquisarServidoresExameView(vinCodigoIn, matriculaIn, null);

		if(listServidores!=null && !listServidores.isEmpty()){
			return listServidores.get(0);
		}

		Integer pesCodigo = servidorIn.getPessoaFisica().getCodigo();

		List<RapServidores> listRapServidores = getRegistroColaboradorFacade().pesquisarRapServidoresPorCodigoPessoa(pesCodigo);

		//for r_ser_busca 
		for(RapServidores servidor: listRapServidores){

			if(!servidor.getId().getVinCodigo().equals(vinCodigoIn) && !servidor.getId().getMatricula().equals(matriculaIn)){
				servidorOut = getRegistroColaboradorFacade().buscaVRapServSolExme(servidor.getId().getVinCodigo(), servidor.getId().getMatricula(), null,null, null);
			}
		}


		return servidorOut;
	}

	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return this.ambulatorioFacade;
	}

	protected void atualizaLocalizador(AelSolicitacaoExames solicitacaoExame) throws ApplicationBusinessException {

		boolean isAtivarRotina = false;
		final AghParametros parametroAtivaExameInternet = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_LIGA_EXAMES_INTERNET);
		if (parametroAtivaExameInternet != null) {
			Object paramSimOuNao = parametroAtivaExameInternet.getValor();
			if (paramSimOuNao != null) {
				isAtivarRotina = "S".equalsIgnoreCase(paramSimOuNao.toString());
			}
		}	

		if(!isAtivarRotina) {
			return;
		}
		
		String localizador = RandomStringUtils.randomAlphabetic(2).toUpperCase()
				+ RandomStringUtils.randomNumeric(2)
				+ RandomStringUtils.randomAlphabetic(2).toUpperCase()
				+ RandomStringUtils.randomNumeric(2);

		while (!getAelSolicitacaoExamesDAO().localizadorValido(localizador)) {
			localizador = RandomStringUtils.randomAlphabetic(2).toUpperCase()
					+ RandomStringUtils.randomNumeric(2)
					+ RandomStringUtils.randomAlphabetic(2).toUpperCase()
					+ RandomStringUtils.randomNumeric(2);
		}

		solicitacaoExame.setLocalizador(localizador);
	}	

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
	protected ICascaFacade getCascaFacade(){
		return this.cascaFacade;
	}

	/**
	 * #39003 - Serviço que busca ultima solicitacao de exames
	 * @param atdSeq
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public AelSolicitacaoExames buscarUltimaSolicitacaoExames(Integer atdSeq) throws ApplicationBusinessException{
		Boolean existeServidorCategoriaProfMedico = Boolean.FALSE;
		AelSolicitacaoExames solicitacaoExames = null;
		solicitacaoExames = getAelSolicitacaoExamesDAO().obterAelSolicitacaoExamePorAtdSeq(atdSeq);
		if (solicitacaoExames != null && solicitacaoExames.getServidorResponsabilidade() != null) {
			existeServidorCategoriaProfMedico = getCascaFacade().existeServidorCategoriaProfMedico(solicitacaoExames.getServidorResponsabilidade().getId().getMatricula(), solicitacaoExames.getServidorResponsabilidade().getId().getVinCodigo());	
		}
		if (existeServidorCategoriaProfMedico) {
			return solicitacaoExames;
		}else{
			return null;
		}
	}

    public Integer buscarDoadorRedome(Integer soeSeq) {
        return getAelSolicitacaoExamesDAO().listarDoadorRedome(soeSeq);
    }

    /**
     * Procedure aelp_atlz_sit_doador_redome
     * #48566
     */
    public void preparaDadosinsercaoExtratoDoadores(Integer soeSeq) throws BaseException {

        AelExtratoDoador aelExtratoDoador = new AelExtratoDoador();

        aelExtratoDoador.setData(new Date());
        String soeSeqConvertido = soeSeq.toString();
        aelExtratoDoador.setObservacao(soeSeqConvertido);
        aelExtratoDoador.setIndSistema(DominioSimNao.S.toString());
        aelExtratoDoador.setSituacao(DominioSituacaoExtratoDoador.SOL.toString());

        RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

        if (servidorLogado != null) {
            aelExtratoDoador.setSerMatricula(servidorLogado.getId().getMatricula());
            aelExtratoDoador.setSerVinCodigo(servidorLogado.getId().getVinCodigo());
        }

        Integer listaDorSeq = buscarDoadorRedome(soeSeq);

        if (listaDorSeq != null) {

            AelDoadorRedome aelDoadorRedome = this.getAelSolicitacaoExamesDAO().obterDoadorRedomePeloSeq(listaDorSeq);
            aelExtratoDoador.setAelDoadorRedome(aelDoadorRedome);
            aelDoadorRedome.setSituacao(aelExtratoDoador.getSituacao());
            preInsertExtratoDoares(aelDoadorRedome);
            gravaExtratoDoadores(aelExtratoDoador);
        }
    }

    /**
     * ORADB Trigger AELT_ETD_BRI.<br>
     */
    public void preInsertExtratoDoares(AelDoadorRedome aelDoadorRedome) throws BaseException {

           if(aelDoadorRedome.getSituacao() != null){
               AelDoadorRedome recebeDoadorRedome =  getAelDoadorRedomeDAO().buscarAelDoadorRedomePorSeq(aelDoadorRedome.getSeq());
               recebeDoadorRedome.setSituacao(aelDoadorRedome.getSituacao());
               getAelDoadorRedomeDAO().merge(recebeDoadorRedome);
               getAelDoadorRedomeDAO().flush();
           }
    }

    public void gravaExtratoDoadores(AelExtratoDoador aelExtratoDoador) throws BaseException {

        if (aelExtratoDoador != null) {
            getAelExtratoDoadorDAO().persistir(aelExtratoDoador);
            getAelExtratoDoadorDAO().flush();

        }
    }
}
