package br.gov.mec.aghu.blococirurgico.procdiagterap.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.dao.PdtDadoDescDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtDescObjetivaDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtDescTecnicaDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtDescricaoDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtDescricaoJnDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtProcDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtProfDAO;
import br.gov.mec.aghu.blococirurgico.procdiagterap.business.PdtDescricaoRN.DescricaoRNExceptionCode;
import br.gov.mec.aghu.blococirurgico.vo.ProcEspPorCirurgiaVO;
import br.gov.mec.aghu.dominio.DominioAsa;
import br.gov.mec.aghu.dominio.DominioIndRespProc;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcEspecialidadeProcCirgs;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgias;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgiasId;
import br.gov.mec.aghu.model.PdtDadoDesc;
import br.gov.mec.aghu.model.PdtDescricao;
import br.gov.mec.aghu.model.PdtDescricaoJn;
import br.gov.mec.aghu.model.PdtProf;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

/**
 * Classe responsável pelas regras de negócio relacionadas à entidade PdtDescricao.
 * 
 * @author dpacheco
 *
 */
@Stateless
public class PdtDescricaoRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(PdtDescricaoRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private PdtProcDAO pdtProcDAO;

	@Inject
	private PdtDescricaoJnDAO pdtDescricaoJnDAO;

	@Inject
	private PdtDescricaoDAO pdtDescricaoDAO;

	@Inject
	private PdtDescObjetivaDAO pdtDescObjetivaDAO;

	@Inject
	private PdtProfDAO pdtProfDAO;

	@Inject
	private PdtDadoDescDAO pdtDadoDescDAO;

	@Inject
	private PdtDescTecnicaDAO pdtDescTecnicaDAO;


	@EJB
	private IBlocoCirurgicoFacade iBlocoCirurgicoFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -5188167759497723953L;
	
	public enum DescricaoRNExceptionCode implements BusinessExceptionCode {
		PDT_00141, PDT_00142, PDT_00143, PDT_00144, PDT_00145, PDT_00146, MSG_FIM_CRG_MENOR_DATA_ATUAL
	}
	
	/**
	 * Insere instância de PdtDescricao.
	 * 
	 * @param descricao
	 * @param servidorLogado
	 * @throws ApplicationBusinessException
	 */
	public void inserirDescricao(final PdtDescricao descricao)
			throws ApplicationBusinessException {
		executarAntesInserir(descricao);
		getPdtDescricaoDAO().persistir(descricao);
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: PDTT_DDT_BRI
	 * 
	 * @param descricao
	 * @param servidorLogado
	 */
	private void executarAntesInserir(final PdtDescricao descricao) {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		descricao.setCriadoEm(new Date());
		descricao.setServidor(servidorLogado);
	}
	
	/**
	 * Atualiza instância de PdtDescricao.
	 * 
	 * @param newDescricao
	 * @param servidorLogado
	 * @throws BaseException
	 */
	public void atualizarDescricao(PdtDescricao newDescricao, boolean isLaudoPreliminar) throws BaseException {
		final PdtDescricaoDAO dao = getPdtDescricaoDAO();
		PdtDescricao oldDescricao = dao.obterOriginal(newDescricao);
		executarAntesAtualizar(oldDescricao, newDescricao, isLaudoPreliminar);
		newDescricao = dao.merge(newDescricao);
		dao.atualizar(newDescricao);
		executarAposAtualizar(oldDescricao, newDescricao);
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: PDTT_DDT_BRU
	 * 
	 * @param oldDescricao
	 * @param newDescricao
	 * @param servidorLogado
	 * @throws BaseException
	 */
	private void executarAntesAtualizar(final PdtDescricao oldDescricao, 
			final PdtDescricao newDescricao, final boolean isLaudoPreliminar) throws BaseException {
		
		// Atualiza servidor que atualizou o registro
		newDescricao.setServidor(servidorLogadoFacade.obterServidorLogado());
		
		// Qdo a situacao for atualizada p PRE ou DEF, fazer a atualização
		atualizarConclusao(newDescricao, oldDescricao, isLaudoPreliminar);
	}
	
	/**
	 * Procedure
	 * 
	 * ORADB: PDTK_DDT_RN.RN_DDTP_ATU_CONCLUI
	 * 
	 * @param descricaoOld
	 * @param isLaudoPreliminar 
	 * @param servidorLogado
	 * @throws BaseException 
	 */
	private void atualizarConclusao(PdtDescricao descricao, PdtDescricao descricaoOld, boolean isLaudoPreliminar) throws BaseException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		Integer ddtSeq = descricaoOld.getSeq();
		Integer ddtCrgSeq = null;
		Short ddtEspSeq = descricaoOld.getEspecialidade().getSeq();
		DominioAsa asa = null;
		Date dthrInicioCirurgia = null;
		Date dthrFimCirurgia = null;
		MbcCirurgias cirurgia = descricao.getMbcCirurgias(); 
		Short espSeqCirurgia = null;
		
		if (cirurgia != null) {
			ddtCrgSeq = cirurgia.getSeq();
			espSeqCirurgia = cirurgia.getEspecialidade().getSeq();
		}
		
		Long countProc = getPdtProcDAO().obterCountProcPorDdtSeq(ddtSeq);
		if (countProc == 0) {
			// Informe o Procedimento Diag. Terapêutico na pasta Cirurgia !
			throw new ApplicationBusinessException(DescricaoRNExceptionCode.PDT_00141);
		}
		
		//#53347 - Ao liberar laudo preliminar, não é necessário inserir descrição
		if(!isLaudoPreliminar) {
			verificarDescricao(ddtSeq);
		}
		
		PdtDadoDesc dadoDesc = getPdtDadoDescDAO().obterDadoDescPorDdtSeq(ddtSeq);
		if (dadoDesc != null) {
			if (dadoDesc.getDthrInicio() == null || dadoDesc.getDthrFim() == null) {
				// Informe data/hora de inicio e fim do procedimento na pasta P Diag Terap!
				throw new ApplicationBusinessException(DescricaoRNExceptionCode.PDT_00143);
			} else {
				asa = dadoDesc.getAsa();
				dthrInicioCirurgia = dadoDesc.getDthrInicio();
				dthrFimCirurgia = dadoDesc.getDthrFim();
				if (dadoDesc.getCarater() == null) {
					// O  Caráter na pasta P Diag Terap, deve ser informado !
					throw new ApplicationBusinessException(DescricaoRNExceptionCode.PDT_00144);
				}
			}
			
		}
		
		List<PdtProf> listaProf = getPdtProfDAO().pesquisarProfResponsavelPorDdtSeq(ddtSeq);
		if (listaProf.isEmpty()) {
			// Informe a equipe responsável por este procedimento !
			throw new ApplicationBusinessException(DescricaoRNExceptionCode.PDT_00145);
		}
		
		IBlocoCirurgicoFacade blocoCirurgicoFacade = getBlocoCirurgicoFacade();
		
		if (cirurgia != null) {
			if (!cirurgia.getDigitaNotaSala()) {
				cirurgia.setTemDescricao(Boolean.TRUE);
				cirurgia.setAsa(asa);
				cirurgia.setDataInicioCirurgia(dthrInicioCirurgia);
				cirurgia.setDataFimCirurgia(dthrFimCirurgia);
				try {
					blocoCirurgicoFacade.persistirCirurgia(cirurgia, servidorLogado);
				} catch (BaseException e) {
					logError(e);
					throw e;
				} catch (PersistenceException e) {
					logError(e);
					throw new ApplicationBusinessException(DescricaoRNExceptionCode.PDT_00146, e);
				}
			} else {
				cirurgia.setTemDescricao(Boolean.TRUE);
				try {
					blocoCirurgicoFacade.persistirCirurgia(cirurgia, servidorLogado);
				} catch (BaseException e) {
					logError(e);
					throw e;
				} catch (PersistenceException e) {
					logError(e);
					throw new ApplicationBusinessException(DescricaoRNExceptionCode.PDT_00146, e);
				}
			}
		}
		
		persistirProcEspPorCirurgia(servidorLogado, ddtSeq, ddtCrgSeq,
				ddtEspSeq, cirurgia,espSeqCirurgia, blocoCirurgicoFacade);
	}
	
	private void verificarDescricao(Integer ddtSeq) throws ApplicationBusinessException {
			Long countDescTecnica = getPdtDescTecnicaDAO().obterCountDescricaoNaoNulaPorDdtSeq(ddtSeq);
			if (countDescTecnica == 0) {
				Long countDescObjetiva = getPdtDescObjetivaDAO().obterCountDescricaoObjetivaPorDdtSeq(ddtSeq);
				if (countDescObjetiva == 0) {
					// Informe a descrição técnica na pasta Descrição e/ou informe a descrição objetiva na pasta Desc Objetiva !
					throw new ApplicationBusinessException(DescricaoRNExceptionCode.PDT_00142);
				}
			}
	}

	private void persistirProcEspPorCirurgia(
			final RapServidores servidorLogado,
			Integer ddtSeq,
			Integer ddtCrgSeq,
			Short ddtEspSeq,
			MbcCirurgias cirurgia, Short espSeqCirurgia,
			IBlocoCirurgicoFacade blocoCirurgicoFacade) throws BaseException {
		/*
		 * Inativa os procedimentos que não são usados por nenhuma
		 * proced diag terap, antes de gravar os que são usados
		 * na mbc_proc_esp_por_cirurgias
		 */
		if (ddtCrgSeq != null) {
			List<MbcProcEspPorCirurgias> listaProcEspPorCirurgia = blocoCirurgicoFacade
					.pesquisarProcEspSemProcDiagTerapPorDdtSeqEDdtCrgSeq(
							ddtSeq, ddtCrgSeq);
			
			for (MbcProcEspPorCirurgias procEspPorCirurgia : listaProcEspPorCirurgia) {
				procEspPorCirurgia.setSituacao(DominioSituacao.I);
				blocoCirurgicoFacade.persistirProcEspPorCirurgias(procEspPorCirurgia);
			}
		}
		
		List<ProcEspPorCirurgiaVO> listaProcEspPorCirurgiaVO = 
				getPdtProcDAO().pesquisarProcEspNaoPrincipalPorDdtSeqEEspSeq(ddtSeq, ddtEspSeq);
		listaProcEspPorCirurgiaVO.addAll(getPdtProcDAO().pesquisarProcEspPrincipalPorDdtSeqEEspSeq(ddtSeq, ddtEspSeq));
		
		for (ProcEspPorCirurgiaVO procEspPorCirurgiaVO : listaProcEspPorCirurgiaVO) {
			AghEspecialidades especialidade = null;
			
			List<MbcEspecialidadeProcCirgs> listaEspProcCirg = 
					blocoCirurgicoFacade.pesquisarEspecialidadesProcCirgsPorEspSeqEPciSeq(espSeqCirurgia, procEspPorCirurgiaVO.getPciSeq());
			 
			if (!listaEspProcCirg.isEmpty()) {
				especialidade = listaEspProcCirg.get(0).getEspecialidade();
			} else {
				List<MbcEspecialidadeProcCirgs> listaEspProcCirgNaoPrincipal = 
						blocoCirurgicoFacade.pesquisarEspecialidadesProcCirgsPorEspSeqEPciSeq2(espSeqCirurgia, procEspPorCirurgiaVO.getPciSeq());
				especialidade = listaEspProcCirgNaoPrincipal.get(0).getEspecialidade();
			}
			
			MbcProcEspPorCirurgiasId procEspPorCirurgiaId = 
					new MbcProcEspPorCirurgiasId(cirurgia.getSeq(), procEspPorCirurgiaVO.getPciSeq(), especialidade.getSeq(), DominioIndRespProc.DESC);
			MbcProcEspPorCirurgias procEspPorCirurgia = blocoCirurgicoFacade.obterMbcProcEspPorCirurgiasPorChavePrimaria(procEspPorCirurgiaId);
			
			if (procEspPorCirurgia != null) {
				procEspPorCirurgia.setSituacao(DominioSituacao.A);
				blocoCirurgicoFacade.persistirProcEspPorCirurgias(procEspPorCirurgia);
			} else {
				try {
					procEspPorCirurgia = new MbcProcEspPorCirurgias();
					procEspPorCirurgia.setId(procEspPorCirurgiaId);
					procEspPorCirurgia.setServidor(null);
					procEspPorCirurgia.setSituacao(DominioSituacao.A);
					procEspPorCirurgia.setCriadoEm(null);
					procEspPorCirurgia.setQtd(Byte.valueOf("1"));
					procEspPorCirurgia.setIndPrincipal(Boolean.TRUE);
					procEspPorCirurgia.setProcedHospInterno(null);
					blocoCirurgicoFacade.persistirProcEspPorCirurgias(procEspPorCirurgia);							
				} catch (PersistenceException e) {
					logError(e);
				}
			}
		}
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: PDTT_DDT_ARU
	 * 
	 * @param oldDescricao
	 * @param newDescricao
	 * @param servidorLogado
	 */
	private void executarAposAtualizar(final PdtDescricao oldDescricao, 
			final PdtDescricao newDescricao) {
		
		if (CoreUtil.modificados(newDescricao.getComplemento(), oldDescricao.getComplemento())
				|| CoreUtil.modificados(newDescricao.getItemSolicitacaoExame(), oldDescricao.getItemSolicitacaoExame())
				|| CoreUtil.modificados(newDescricao.getCriadoEm(), oldDescricao.getCriadoEm())		
				|| CoreUtil.modificados(newDescricao.getServidor(), oldDescricao.getServidor())
				|| CoreUtil.modificados(newDescricao.getDthrConclusao(), oldDescricao.getDthrConclusao())		
				|| CoreUtil.modificados(newDescricao.getSeq(), oldDescricao.getSeq())
				|| CoreUtil.modificados(newDescricao.getSituacao(), oldDescricao.getSituacao())
				|| CoreUtil.modificados(newDescricao.getResultadoNormal(), oldDescricao.getResultadoNormal())) {
			
			inserirJournal(oldDescricao, servidorLogadoFacade.obterServidorLogado() ,DominioOperacoesJournal.UPD);
		}
	}
	
	/**
	 * Remove instância de PdtDescricao.
	 * 
	 * @param oldDescricao
	 * @param servidorLogado
	 */
	public void removerDescricao(PdtDescricao oldDescricao) {
		oldDescricao = getPdtDescricaoDAO().obterPorChavePrimaria(oldDescricao.getSeq());
		getPdtDescricaoDAO().remover(oldDescricao);
		executarAposRemover(oldDescricao);
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: PDTT_DDT_ARD
	 * 
	 * @param oldDescricao
	 * @param servidorLogado
	 */
	private void executarAposRemover(final PdtDescricao oldDescricao) {
		inserirJournal(oldDescricao, servidorLogadoFacade.obterServidorLogado(), DominioOperacoesJournal.DEL);
	}
	
	private void inserirJournal(final PdtDescricao oldDescricao, 
			final RapServidores servidorLogado, final DominioOperacoesJournal operacaoJournal) {
		
		PdtDescricaoJn jn = BaseJournalFactory.getBaseJournal(
				operacaoJournal, PdtDescricaoJn.class, servidorLogado.getUsuario());
		jn.setComplemento(oldDescricao.getComplemento());
		
		MbcCirurgias cirurgia = oldDescricao.getMbcCirurgias(); 
		if (cirurgia != null) {
			jn.setCrgSeq(cirurgia.getSeq());	
		}
		
		AelItemSolicitacaoExames itemSolicitacaoExame = oldDescricao.getItemSolicitacaoExame();
		if (itemSolicitacaoExame != null) {
			jn.setIseSeqp(itemSolicitacaoExame.getId().getSeqp());
			jn.setIseSoeSeq(itemSolicitacaoExame.getId().getSoeSeq());
		}
		
		jn.setEspSeq(oldDescricao.getEspecialidade().getSeq());
		jn.setCriadoEm(oldDescricao.getCriadoEm());
		
		RapServidoresId servidorId = oldDescricao.getServidor().getId();
		jn.setSerVinCodigo(servidorId.getVinCodigo());
		jn.setSerMatricula(servidorId.getMatricula());
		
		jn.setDthrConclusao(oldDescricao.getDthrConclusao());
		jn.setSeq(oldDescricao.getSeq());
		jn.setSituacao(oldDescricao.getSituacao());
		jn.setResultadoNormal(oldDescricao.getResultadoNormal());
		getPdtDescricaoJnDAO().persistir(jn);
	}
	
	protected IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return iBlocoCirurgicoFacade;
	}

	protected PdtDescObjetivaDAO getPdtDescObjetivaDAO() {
		return pdtDescObjetivaDAO;
	}
	
	protected PdtDescTecnicaDAO getPdtDescTecnicaDAO() {
		return pdtDescTecnicaDAO;
	}

	protected PdtDadoDescDAO getPdtDadoDescDAO() {
		return pdtDadoDescDAO;
	}
	
	protected PdtProfDAO getPdtProfDAO() {
		return pdtProfDAO;
	}

	protected PdtProcDAO getPdtProcDAO() {
		return pdtProcDAO;
	}

	protected PdtDescricaoDAO getPdtDescricaoDAO() {
		return pdtDescricaoDAO;
	}

	protected PdtDescricaoJnDAO getPdtDescricaoJnDAO() {
		return pdtDescricaoJnDAO;
	}	
	
}
