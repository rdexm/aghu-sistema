package br.gov.mec.aghu.exames.ejb;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioAbrangenciaGrupoRecomendacao;
import br.gov.mec.aghu.dominio.DominioResponsavelGrupoRecomendacao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoTransporteUnidade;
import br.gov.mec.aghu.exames.contratualizacao.vo.ItemContratualizacaoVO;
import br.gov.mec.aghu.exames.dao.AelGrupoRecomendacaoDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelUnfExecutaExamesDAO;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameVO;
import br.gov.mec.aghu.exames.solicitacao.vo.SolicitacaoExameVO;
import br.gov.mec.aghu.model.AelGrupoQuestao;
import br.gov.mec.aghu.model.AelGrupoRecomendacao;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelRespostaQuestao;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;


@Stateless
@SuppressWarnings({"PMD.JUnit4TestShouldUseTestAnnotation","PMD.PackagePrivateBaseBusiness","PMD.AtributoEmSeamContextManager"})
public class SolicitacaoExameBean extends BaseBusiness implements SolicitacaoExameBeanLocal {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8626514787620255047L;

	private static final Log LOG = LogFactory.getLog(SolicitacaoExameBean.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Resource
	protected SessionContext ctx;

	@EJB
	private ISolicitacaoExameFacade solicitacaoExameFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private AelGrupoRecomendacaoDAO aelGrupoRecomendacaoDAO;

	@Inject
	private AelUnfExecutaExamesDAO aelUnfExecutaExamesDAO;

	@EJB
	private IAghuFacade aghuFacade;

	@Inject
	private AelSolicitacaoExameDAO aelSolicitacaoExameDAO;
	
	@Inject
	private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;
	

	public enum SolicitacaoExameBeanExceptionCode implements BusinessExceptionCode {
		ERRO_PARA_TESTAR_ROLLBACK
		, ERRO_AO_TENTAR_PERSISTIR_SOLICITACAO_EXAME
		, ERRO_AO_TENTAR_VOLTAR_SOLICITACAO_EXAME
		, ERRO_AO_TENTAR_RECEBER_SOLICITACAO_EXAME
		, ERRO_AO_TENTAR_ATUALIZAR_SOLICITACAO_EXAME
		;
	}



	// Finalizar Solicitacao de Exames - Inicio
	// ##############################################################

	/**
	 * 
	 */
	@Override
	public SolicitacaoExameVO gravaSolicitacaoExame(SolicitacaoExameVO vo, String nomeMicrocomputador) throws BaseException {
		boolean ehInsercao = vo.isInsercao();

		try {

			// Cria um clone da VO para ser usado na gravação para, em caso de erro, 
			// descartar alterações feitas na VO passada como argumento
			// SolicitacaoExameVO voClone = (SolicitacaoExameVO) vo.clone();

			SolicitacaoExameVO result = this.getSolicitacaoExameFacade().gravar(vo, nomeMicrocomputador);

			return result;
		} catch (BaseException e) {
			logError(e.getMessage(), e);
			this.reAtacharSolicitacaoExame(vo, ehInsercao);
			this.recuperarNumeroDeAmostra(vo);
			this.limparRespostas(vo, ehInsercao);
			this.ctx.setRollbackOnly();
			throw e;
		} catch (Exception e) {
			logError(e.getMessage(), e);
			this.reAtacharSolicitacaoExame(vo, ehInsercao);
			this.recuperarNumeroDeAmostra(vo);
			this.limparRespostas(vo, ehInsercao);
			this.ctx.setRollbackOnly();
			throw new ApplicationBusinessException(SolicitacaoExameBeanExceptionCode.ERRO_AO_TENTAR_PERSISTIR_SOLICITACAO_EXAME);
		}
	}

	private void recuperarNumeroDeAmostra(final SolicitacaoExameVO vo) {
		for (final ItemSolicitacaoExameVO item: vo.getItemSolicitacaoExameVos()){
			
			if (item.getNumeroAmostraAnterior() != null) {
				item.setNumeroAmostra(item.getNumeroAmostraAnterior());
			}
		}
	}

	private void limparRespostas(final SolicitacaoExameVO vo, boolean ehInsercao) {
		if(ehInsercao){
			for (final ItemSolicitacaoExameVO item: vo.getItemSolicitacaoExameVos()){
				if (item.getGruposQuestao() != null) {			
					for(final AelGrupoQuestao grupo: item.getGruposQuestao()){
						for(final AelRespostaQuestao resposta : grupo.getAelRespostaQuestaos()){
							resposta.setId(null);
						}
					}					
				}
			}
		}
	}

	@Override
	public List<ItemContratualizacaoVO> gravaSolicitacaoExameContratualizacao(
			AelSolicitacaoExames aelSolicitacaoExames, List<ItemContratualizacaoVO> listaItensVO, String nomeMicrocomputador) throws BaseException {
		List<ItemContratualizacaoVO> retorno = listaItensVO;
		try {
			retorno = this.getSolicitacaoExameFacade().gravarContratualizacao(aelSolicitacaoExames, listaItensVO, nomeMicrocomputador);
			boolean ocorreuErro = false;
			for (ItemContratualizacaoVO itemContratualizacaoVO : retorno) {
				if (StringUtils.isNotEmpty(itemContratualizacaoVO.getMensagemErro())) {
					ocorreuErro = true;
					break;
				}
			}
			if (ocorreuErro) {
				this.ctx.setRollbackOnly();
			} else {
				getAelSolicitacaoExamesDAO().flush();	
			}
		} catch (BaseException e) {
			logError(e.getMessage(), e);
			this.ctx.setRollbackOnly();
			throw e;
		} catch (Exception e) {
			logError(e.getMessage(), e);
			this.ctx.setRollbackOnly();
			throw new ApplicationBusinessException(SolicitacaoExameBeanExceptionCode.ERRO_AO_TENTAR_PERSISTIR_SOLICITACAO_EXAME);
		}
		return listaItensVO;
	}
	
	private void reAtacharSolicitacaoExame(SolicitacaoExameVO vo, boolean ehInsercao) {
		if (ehInsercao) {
			vo.doSetSolicitacaoExame(null);
		} else {
			AelSolicitacaoExames solicEx = getAelSolicitacaoExamesDAO().obterPorChavePrimaria(vo.getSolicitacaoExameSeq());
			vo.doSetSolicitacaoExame(solicEx);
		}
		//this.reAtacharItens(vo.getItemSolicitacaoExameVos());
	}

	protected void reAtacharItens(List<ItemSolicitacaoExameVO> itemSolicitacaoExameVos) {
		if (itemSolicitacaoExameVos != null) {
			for (ItemSolicitacaoExameVO itemVO : itemSolicitacaoExameVos) {
				if (itemVO.getUnfExecutaExame() != null && itemVO.getUnfExecutaExame().getUnfExecutaExame() != null) {
					AelUnfExecutaExames unfExame = itemVO.getUnfExecutaExame().getUnfExecutaExame();
					getAelUnfExecutaExamesDAO().desatachar(unfExame);
					
					AelUnfExecutaExames unfExame1 = getAelUnfExecutaExamesDAO().obterPorChavePrimaria(unfExame.getId());
					itemVO.getUnfExecutaExame().setUnfExecutaExame(unfExame1);
				}//IF
			}//FOR da lista de itens.
		}//IF da lista de itens.
	}

	/**
	 * 
	 */
	@Override
	public void receberItemSolicitacaoExame(AelItemSolicitacaoExames aelItemSolicitacaoExames, String nomeMicrocomputador) throws BaseException {

		try {

			getSolicitacaoExameFacade().receberItemSolicitacaoExame(aelItemSolicitacaoExames, nomeMicrocomputador);
			getAelSolicitacaoExamesDAO().flush();

		} catch (BaseException e) {
			this.ctx.setRollbackOnly();
			logError(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			this.ctx.setRollbackOnly();
			logError(e.getMessage(), e);
			throw new ApplicationBusinessException(SolicitacaoExameBeanExceptionCode.ERRO_AO_TENTAR_RECEBER_SOLICITACAO_EXAME);
		}

	}

	/**
	 * 
	 */
	@Override
	public void voltarItemSolicitacaoExame(AelItemSolicitacaoExames aelItemSolicitacaoExames, String nomeMicrocomputador) throws BaseException {

		try {

			getSolicitacaoExameFacade().voltarItemSolicitacaoExame(aelItemSolicitacaoExames, nomeMicrocomputador, new Date());
			getAelSolicitacaoExamesDAO().flush();

		} catch (BaseException e) {
			this.ctx.setRollbackOnly();
			logError(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			this.ctx.setRollbackOnly();
			logError(e.getMessage(), e);
			throw new ApplicationBusinessException(SolicitacaoExameBeanExceptionCode.ERRO_AO_TENTAR_VOLTAR_SOLICITACAO_EXAME);
		}

	}
	
	/**
	 * 
	 */
	@Override
	public void atualizarItemSolicitacaoExame(AelItemSolicitacaoExames aelItemSolicitacaoExames, String nomeMicrocomputador)throws BaseException {

		try {			
			getSolicitacaoExameFacade().atualizar(aelItemSolicitacaoExames, nomeMicrocomputador);
			getAelSolicitacaoExamesDAO().flush();

		} catch (BaseException e) {
			this.ctx.setRollbackOnly();
			logError(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			this.ctx.setRollbackOnly();
			logError(e.getMessage(), e);
			throw new ApplicationBusinessException(SolicitacaoExameBeanExceptionCode.ERRO_AO_TENTAR_ATUALIZAR_SOLICITACAO_EXAME);
		}

	}
	
	@Override
	public void atualizarItemSolicitacaoExame(AelItemSolicitacaoExames aelItemSolicitacaoExames, AelItemSolicitacaoExames itemSolicitacaoExameOriginal, String nomeMicrocomputador)throws BaseException {

		try {
			getSolicitacaoExameFacade().atualizar(aelItemSolicitacaoExames, itemSolicitacaoExameOriginal, nomeMicrocomputador);
			getAelSolicitacaoExamesDAO().flush();

		} catch (BaseException e) {
			this.ctx.setRollbackOnly();
			logError(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			this.ctx.setRollbackOnly();
			logError(e.getMessage(), e);
			throw new ApplicationBusinessException(SolicitacaoExameBeanExceptionCode.ERRO_AO_TENTAR_ATUALIZAR_SOLICITACAO_EXAME);
		}

	}	
	
	@Override
	public void recepcionarPaciente(AelItemSolicitacaoExames itemSolicitacaoExames, final DominioTipoTransporteUnidade transporte, final Boolean indUsoO2Un, String nomeMicrocomputador) throws BaseException {
		
		try {
			
			itemSolicitacaoExames = getAelItemSolicitacaoExameDAO().obterPorChavePrimaria(itemSolicitacaoExames.getId());
			
//			if(transporte != null){
				itemSolicitacaoExames.setTipoTransporteUn(transporte);	
//			}
			
			itemSolicitacaoExames.setIndUsoO2Un(indUsoO2Un);

			getSolicitacaoExameFacade().atualizar(itemSolicitacaoExames, nomeMicrocomputador);
			getAelSolicitacaoExamesDAO().flush();

		} catch (BaseException e) {
			this.ctx.setRollbackOnly();
			logError(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			this.ctx.setRollbackOnly();
			logError(e.getMessage(), e);
			throw new ApplicationBusinessException(SolicitacaoExameBeanExceptionCode.ERRO_AO_TENTAR_ATUALIZAR_SOLICITACAO_EXAME);
		}
		
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void insert1() throws BaseException {
		try {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

			AelGrupoRecomendacaoDAO grupoDao = getAelGrupoRecomendacaoDAO();

			AelGrupoRecomendacao grupo = new AelGrupoRecomendacao();
			grupo.setDescricao("Teste 272.1");
			grupo.setAbrangencia(DominioAbrangenciaGrupoRecomendacao.A);
			grupo.setResponsavel(DominioResponsavelGrupoRecomendacao.C);
			grupo.setIndSituacao(DominioSituacao.A);
			grupo.setCriadoEm(new Date());
			grupo.setServidor(servidorLogado);

			grupoDao.persistir(grupo);

			grupoDao.flush();
		}catch (Exception e) {
			this.ctx.setRollbackOnly();
			logError("Exceção capturada: ", e);
			throw new ApplicationBusinessException(SolicitacaoExameBeanExceptionCode.ERRO_PARA_TESTAR_ROLLBACK);
		}
	}


	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void insert2() throws BaseException {

		try {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			AelGrupoRecomendacaoDAO grupoDao = getAelGrupoRecomendacaoDAO();

			AelGrupoRecomendacao grupo1 = new AelGrupoRecomendacao();
			grupo1.setDescricao("Teste 272.2");
			grupo1.setAbrangencia(DominioAbrangenciaGrupoRecomendacao.A);
			grupo1.setResponsavel(DominioResponsavelGrupoRecomendacao.C);
			grupo1.setIndSituacao(DominioSituacao.A);
			grupo1.setCriadoEm(new Date());
			grupo1.setServidor(servidorLogado);

			grupoDao.persistir(grupo1);
			grupoDao.flush();

			AelGrupoRecomendacao grupo12 = new AelGrupoRecomendacao();
			grupo12.setDescricao("Teste 272.3");
			grupo12.setAbrangencia(DominioAbrangenciaGrupoRecomendacao.A);
			grupo12.setResponsavel(DominioResponsavelGrupoRecomendacao.C);
			grupo12.setIndSituacao(DominioSituacao.A);
			grupo12.setCriadoEm(new Date());
			grupo12.setServidor(servidorLogado);

			grupoDao.persistir(grupo12);
			grupoDao.flush();

			throw new ApplicationBusinessException(SolicitacaoExameBeanExceptionCode.ERRO_PARA_TESTAR_ROLLBACK);

		}catch (BaseException e) {
			this.ctx.setRollbackOnly();
			throw e;
		}

	}
	
	@Override
	public AelSolicitacaoExames atualizarSolicitacaoExame(AelSolicitacaoExames solicExame, List<AelItemSolicitacaoExames> itemSolicExameExcluidos, String nomeMicrocomputador) throws BaseException {
		try {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			return this.getSolicitacaoExameFacade().atualizar(solicExame, itemSolicExameExcluidos, nomeMicrocomputador, servidorLogado);
		} catch (BaseException e) {
			this.ctx.setRollbackOnly();
			throw e;
		}
	}
	
	protected AelSolicitacaoExameDAO getAelSolicitacaoExamesDAO() {
		return aelSolicitacaoExameDAO;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected AelUnfExecutaExamesDAO getAelUnfExecutaExamesDAO() {
		return aelUnfExecutaExamesDAO;
	}

	protected ISolicitacaoExameFacade getSolicitacaoExameFacade() {
		return this.solicitacaoExameFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}
	
	protected AelGrupoRecomendacaoDAO getAelGrupoRecomendacaoDAO() {
		return this.aelGrupoRecomendacaoDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
	public AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO() {
		return aelItemSolicitacaoExameDAO;
	}
		
}
