package br.gov.mec.aghu.blococirurgico.business;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.DescricaoCirurgicaRN.DescricaoCirurgicaRNExceptionCode;
import br.gov.mec.aghu.blococirurgico.dao.MbcDescricaoCirurgicaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcDescricaoCirurgicaJnDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcDescricaoItensDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcDescricaoTecnicasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcEspecialidadeProcCirgsDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcDescricoesDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcEspPorCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfDescricoesDAO;
import br.gov.mec.aghu.blococirurgico.vo.CursorCProcDescricaoVO;
import br.gov.mec.aghu.dominio.DominioIndRespProc;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoDescricaoCirurgia;
import br.gov.mec.aghu.dominio.DominioTipoAtuacao;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcDescricaoCirurgica;
import br.gov.mec.aghu.model.MbcDescricaoCirurgicaJn;
import br.gov.mec.aghu.model.MbcDescricaoItens;
import br.gov.mec.aghu.model.MbcEspecialidadeProcCirgs;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgias;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgiasId;
import br.gov.mec.aghu.model.MbcProfDescricoes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

/**
 * Classe responśavel pelas regras de negócio relacionadas à entidade MbcDescricaoCirurgica.
 * 
 * @author dpacheco
 *
 */
@Stateless
public class DescricaoCirurgicaRN extends BaseBusiness {

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(DescricaoCirurgicaRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcDescricaoTecnicasDAO mbcDescricaoTecnicasDAO;

	@Inject
	private MbcDescricaoCirurgicaJnDAO mbcDescricaoCirurgicaJnDAO;

	@Inject
	private MbcProcDescricoesDAO mbcProcDescricoesDAO;

	@Inject
	private MbcProcEspPorCirurgiasDAO mbcProcEspPorCirurgiasDAO;

	@Inject
	private MbcProfDescricoesDAO mbcProfDescricoesDAO;

	@Inject
	private MbcDescricaoCirurgicaDAO mbcDescricaoCirurgicaDAO;

	@Inject
	private MbcDescricaoItensDAO mbcDescricaoItensDAO;

	@Inject
	private MbcEspecialidadeProcCirgsDAO mbcEspecialidadeProcCirgsDAO;


	@EJB
	private IParametroFacade iParametroFacade;

	@EJB
	private MbcProcEspPorCirurgiasRN mbcProcEspPorCirurgiasRN;

	@EJB
	private DiagnosticoDescricaoRN diagnosticoDescricaoRN;

	@EJB
	private MbcCirurgiasRN mbcCirurgiasRN;

	/**
	 * 
	 */
	private static final long serialVersionUID = 9069358823079078006L;
	
	public enum DescricaoCirurgicaRNExceptionCode implements BusinessExceptionCode {
		MBC_00688, MBC_00740, MBC_00675, MBC_00676, MBC_00677, MBC_00678, MBC_00679
	}	
	
	/**
	 * Insere instância de MbcDescricaoCirurgica.
	 * 
	 * @param descricaoCirurgica
	 * @param servidorLogado
	 * @throws ApplicationBusinessException
	 */
	public void inserirDescricaoCirurgica(
			final MbcDescricaoCirurgica descricaoCirurgica)
			throws ApplicationBusinessException {
		
		executarAntesDeInserir(descricaoCirurgica);
		getMbcDescricaoCirurgicaDAO().persistir(descricaoCirurgica);
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: MBCT_DCG_BRI
	 */
	protected void executarAntesDeInserir(
			final MbcDescricaoCirurgica descricaoCirurgica)
			throws ApplicationBusinessException {
		
		verificarEspecialidade(descricaoCirurgica.getDthrConclusao(),
				descricaoCirurgica.getSituacao());
		descricaoCirurgica.setServidor(servidorLogadoFacade.obterServidorLogado());
		descricaoCirurgica.setCriadoEm(new Date());
	}
	
	/**
	 * Procedure
	 * 
	 * ORADB: MBCK_DCG_RN.RN_DCGP_VER_ESP
	 * 
	 * @param dthrConclusao
	 * @param situacao
	 * @throws ApplicationBusinessException
	 */
	protected void verificarEspecialidade(final Date dthrConclusao, final DominioSituacaoDescricaoCirurgia situacao) 
			throws ApplicationBusinessException {
		
		AghParametros paramMinAlteraDescr = null;
		paramMinAlteraDescr = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_MIN_ALTERA_DESCR);
		
		Calendar calAtual = Calendar.getInstance();
		Date dthrAtual = calAtual.getTime();
		Date tempDthrConclusao = dthrConclusao; 
				
		if (tempDthrConclusao == null) {
			tempDthrConclusao = dthrAtual;
		}
		
		// Caso a diferença em minutos entre a data atual e dthrConclusao seja
		// maior que o valor definido no parametro P_MIN_ALTERA_DESCR e
		// situacao da descricao da cirurgia for Concluída
		if (calcularDiferencaMinutosAteDataAtual(tempDthrConclusao, dthrAtual).compareTo(
				paramMinAlteraDescr.getVlrNumerico().doubleValue()) > 0
				&& DominioSituacaoDescricaoCirurgia.CON.equals(situacao)) {
			throw new ApplicationBusinessException(DescricaoCirurgicaRNExceptionCode.MBC_00688);
		}
	}
	
	/**
	 * Calcula a diferença em minutos entre duas datas.
	 * 
	 * @param dthrInicial
	 * @param dthrFinal
	 * @return
	 */
	public Double calcularDiferencaMinutosAteDataAtual(final Date dthrInicial, final Date dthrFinal) {
		Calendar calDthrInicial = Calendar.getInstance();
		calDthrInicial.clear();
		calDthrInicial.setTime(dthrInicial);
		
		Calendar calDthrFinal = Calendar.getInstance();
		calDthrFinal.clear();
		calDthrFinal.setTime(dthrFinal);		

		Double difMinutos = (calDthrFinal.getTimeInMillis() - calDthrInicial.getTimeInMillis()) / (60.0 * 1000.0);
		return difMinutos;
	}
	

	public void excluirDescricaoCirurgica(
			final MbcDescricaoCirurgica descricaoCirurgica) throws ApplicationBusinessException {
		
		executarAntesDeExcluir(descricaoCirurgica);
		getMbcDescricaoCirurgicaDAO().remover(descricaoCirurgica);
		executarDepoisDeExcluir(descricaoCirurgica);
	}
	
	/**
	 * ORADB: MBCT_DCG_ARD
	 */
	protected void executarDepoisDeExcluir(final MbcDescricaoCirurgica descricaoCirurgica) {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		final MbcDescricaoCirurgicaJn journal = createJournal(descricaoCirurgica, servidorLogado.getUsuario(), DominioOperacoesJournal.DEL);
		getMbcDescricaoCirurgicaJnDAO().persistir(journal);
	}

	/**
	 * ORADB: MBCT_DCG_BRD
	 */
	protected void executarAntesDeExcluir(final MbcDescricaoCirurgica descricaoCirurgica) throws ApplicationBusinessException {
		// verifica se o usuário que está excluindo é o mesmo que incluiu
		getDiagnosticoDescricaoRN().validaServidorExclusao(descricaoCirurgica.getServidor());
	}

	public void alterarDescricaoCirurgica(
			final MbcDescricaoCirurgica descricaoCirurgica,
			final RapServidores servidorLogado, String nomeMicrocomputador)
			throws BaseException {
		
		final MbcDescricaoCirurgicaDAO dao = getMbcDescricaoCirurgicaDAO();
		final MbcDescricaoCirurgica oldDescricaoCirurgica = dao.obterOriginal(descricaoCirurgica);
		
		executarAntesDeAlterar(descricaoCirurgica, oldDescricaoCirurgica, servidorLogado, nomeMicrocomputador);
		dao.atualizar(descricaoCirurgica);
		executarDepoisDeAlterar(descricaoCirurgica, oldDescricaoCirurgica);
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: MBCT_DCG_ARU
	 */
	protected void executarDepoisDeAlterar(
			final MbcDescricaoCirurgica descricaoCirurgica,
			final MbcDescricaoCirurgica oldDescricaoCirurgica)
			throws BaseException {
		
		if(!CoreUtil.igual(descricaoCirurgica.getServidor(), oldDescricaoCirurgica.getServidor()) ||
				!CoreUtil.igual(descricaoCirurgica.getId().getCrgSeq(), oldDescricaoCirurgica.getId().getCrgSeq()) ||
				!CoreUtil.igual(descricaoCirurgica.getId().getSeqp(), oldDescricaoCirurgica.getId().getSeqp()) ||
				!CoreUtil.igual(descricaoCirurgica.getDthrConclusao(), oldDescricaoCirurgica.getDthrConclusao()) ||
				!CoreUtil.igual(descricaoCirurgica.getSituacao(), oldDescricaoCirurgica.getSituacao()) ||
				!CoreUtil.igual(descricaoCirurgica.getEspecialidade(), oldDescricaoCirurgica.getEspecialidade()) ||
				!CoreUtil.igual(descricaoCirurgica.getCriadoEm(), oldDescricaoCirurgica.getCriadoEm())
		) {
			final MbcDescricaoCirurgicaJn journal = createJournal(oldDescricaoCirurgica, servidorLogadoFacade.obterServidorLogado().getUsuario(), DominioOperacoesJournal.UPD);
			getMbcDescricaoCirurgicaJnDAO().persistir(journal);
		}
	}
	
	private MbcDescricaoCirurgicaJn createJournal(final MbcDescricaoCirurgica descricaoCirurgica,
			final String usuarioLogado, DominioOperacoesJournal dominio) {
		
		final MbcDescricaoCirurgicaJn journal =  BaseJournalFactory.getBaseJournal(dominio, 
				MbcDescricaoCirurgicaJn.class, usuarioLogado);
		
		journal.setCrgSeq(descricaoCirurgica.getId().getCrgSeq());
		journal.setSeqp(descricaoCirurgica.getId().getSeqp());
		journal.setCriadoEm(descricaoCirurgica.getCriadoEm());
		journal.setDthrConclusao(descricaoCirurgica.getDthrConclusao());
		journal.setEspSeq(descricaoCirurgica.getEspecialidade().getSeq());
		journal.setSerMatricula(descricaoCirurgica.getServidor().getId().getMatricula());
		journal.setSerVinCodigo(descricaoCirurgica.getServidor().getId().getVinCodigo());
		journal.setSituacao(descricaoCirurgica.getSituacao());
		
		return journal;
	}
	
	
	/**
	 * Trigger
	 * 
	 * ORADB: MBCT_DCG_BRU
	 */
	protected void executarAntesDeAlterar(
			final MbcDescricaoCirurgica descricaoCirurgica,
			final MbcDescricaoCirurgica oldDescricaoCirurgica,
			final RapServidores servidorLogado, String nomeMicrocomputador)
			throws BaseException {
		
		DiagnosticoDescricaoRN ddRN = getDiagnosticoDescricaoRN();
		
		// verifica se o usuário que está alterando é o mesmo que incluiu
		ddRN.verificaUsuarioInclusaoAlteracao(oldDescricaoCirurgica.getServidor());
		
		// verifica se a especialidade que está alterando está ativa
		// caso descricao concluida, verifica se o usuário está no prazo para alterá-la
		if(!CoreUtil.igual(oldDescricaoCirurgica.getEspecialidade(), descricaoCirurgica.getEspecialidade())){
			verificarEspecialidade( ((Date)CoreUtil.nvl(descricaoCirurgica.getDthrConclusao(), new Date())), 
									descricaoCirurgica.getSituacao()
								  );
		}

		if(DominioSituacaoDescricaoCirurgia.PEN.equals(descricaoCirurgica.getSituacao())){
			rnCcgpAtuConclusa(descricaoCirurgica, oldDescricaoCirurgica, nomeMicrocomputador);
		}
		
		// atualiza servidor que incluiu registro 
		descricaoCirurgica.setServidor(servidorLogado);
	}
	
	
	/**
	 * ORADB: MBCK_DCG_RN.RN_DCGP_ATU_CONCLUSA
	 * 
	 * Verifica se CARATER e ASA foram informados.Se existe um registro para Descrição Técnica.
	 * Se tem um Responsável Cadastrado.E atualiza a tabela mbc_proc_esp_por_cirurgias			
	 */
	public void rnCcgpAtuConclusa(MbcDescricaoCirurgica descricaoCirurgica, MbcDescricaoCirurgica descricaoCirurgicaOld, String nomeMicrocomputador) throws BaseException{
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		
		Long vExiste = getMbcProcDescricoesDAO().obterProcDescricoesCount( descricaoCirurgicaOld.getId().getCrgSeq(), 
																			  descricaoCirurgicaOld.getId().getSeqp());
		if(vExiste == 0){
			// informe o procedimento cirurgico na pasta cirurgia realizada
			throw new ApplicationBusinessException(DescricaoCirurgicaRNExceptionCode.MBC_00740);
		}
		
		
		final Long vDescTecn = getMbcDescricaoTecnicasDAO().buscarMbcDescricaoTecnicasCount( descricaoCirurgicaOld.getId().getCrgSeq(), 
																								descricaoCirurgicaOld.getId().getSeqp());
		if(vDescTecn == 0){
			throw new ApplicationBusinessException(DescricaoCirurgicaRNExceptionCode.MBC_00675);
		}

		final MbcDescricaoItens vDescItem = getMbcDescricaoItensDAO().buscarDescricaoItens( descricaoCirurgicaOld.getId().getCrgSeq(), 
																					  		descricaoCirurgicaOld.getId().getSeqp());
		if(vDescItem == null){
			throw new ApplicationBusinessException(DescricaoCirurgicaRNExceptionCode.MBC_00676);
			
		} else {
			if(vDescItem.getAsa() == null){
				throw new ApplicationBusinessException(DescricaoCirurgicaRNExceptionCode.MBC_00677);
			}
			
			if(vDescItem.getCarater() == null){
				throw new ApplicationBusinessException(DescricaoCirurgicaRNExceptionCode.MBC_00678);
			}
		}
		
		final List<MbcProfDescricoes> mbcProfDescricoes = getMbcProfDescricoesDAO().
										obterMbcProfDescricoesPorCrgSeqDcgSeqpETipoAtuacao( descricaoCirurgicaOld.getId().getCrgSeq(), 
															  							    descricaoCirurgicaOld.getId().getSeqp(),
															  							    DominioTipoAtuacao.RESP
															  							   );
		if(mbcProfDescricoes.isEmpty()){
			throw new ApplicationBusinessException(DescricaoCirurgicaRNExceptionCode.MBC_00679);
		}
		
		
		final MbcCirurgias cirurgia = descricaoCirurgica.getMbcCirurgias();
		if(cirurgia != null){
			if(Boolean.FALSE.equals(cirurgia.getDigitaNotaSala())){
				cirurgia.setAsa(vDescItem.getAsa());
				cirurgia.setTemDescricao(Boolean.TRUE);
				cirurgia.setDataInicioCirurgia(vDescItem.getDthrInicioCirg());
				cirurgia.setDataFimCirurgia(vDescItem.getDthrFimCirg());
				
			} else {
				cirurgia.setAsa(vDescItem.getAsa());
				cirurgia.setTemDescricao(Boolean.TRUE);
			}
			
			try{
				
				getMbcCirurgiasRN().persistirCirurgia(cirurgia, nomeMicrocomputador, new Date());
				
			}catch (BaseException e) {
				// necessário, pois a persistencia da cirurgia conta com flush, 
				// e caso de algum erro, deve-se efetuar rollback
				throw new ApplicationBusinessException(e);
			}
		}
		
		final MbcProcEspPorCirurgiasDAO procEspPorCirurgiasDAO = getMbcProcEspPorCirurgiasDAO();
		final MbcProcEspPorCirurgiasRN mbcProcEspPorCirurgiasRN = getMbcProcEspPorCirurgiasRN();
		
		inativaMbcProcEspPorCirurgias(descricaoCirurgicaOld, servidorLogado, procEspPorCirurgiasDAO, mbcProcEspPorCirurgiasRN);
		geraOuAtivaMbcProcEspPorCirurgias(descricaoCirurgicaOld, cirurgia, procEspPorCirurgiasDAO, mbcProcEspPorCirurgiasRN);
	}

	/**
	 * Inativa os procedimentos que não são usados por nenhuma
	 * descrição da cirurgia, antes de gravar os que são usados na mbc_proc_esp_por_cirurgias 
	 */
	private void inativaMbcProcEspPorCirurgias( final MbcDescricaoCirurgica descricaoCirurgica,
												final RapServidores servidorLogado, 
												final MbcProcEspPorCirurgiasDAO procEspPorCirurgiasDAO,
												final MbcProcEspPorCirurgiasRN mbcProcEspPorCirurgiasRN) throws BaseException {
		
		final List<MbcProcEspPorCirurgias> procEsps = procEspPorCirurgiasDAO.pesquisarMbcProcEspPorCirurgias( 
																				descricaoCirurgica.getId().getCrgSeq(), 
																				DominioIndRespProc.DESC,
																				DominioSituacao.A);
		
		for (MbcProcEspPorCirurgias procEspPorCirurgia : procEsps) {
			procEspPorCirurgia.setSituacao(DominioSituacao.I);
			mbcProcEspPorCirurgiasRN.persistirProcEspPorCirurgias(procEspPorCirurgia);
		}
	}

	private void geraOuAtivaMbcProcEspPorCirurgias( final MbcDescricaoCirurgica oldDescricaoCirurgica, 
												 final MbcCirurgias cirurgia,
												 final MbcProcEspPorCirurgiasDAO procEspPorCirurgiasDAO,
												 final MbcProcEspPorCirurgiasRN mbcProcEspPorCirurgiasRN) throws BaseException {
		
		final List<CursorCProcDescricaoVO> vPrcs = getMbcProcDescricoesDAO().obterCursorCProcDescricaoVO( 
																			oldDescricaoCirurgica.getId().getCrgSeq(), 
																			DominioSituacao.A);
		
		final MbcEspecialidadeProcCirgsDAO mbcEspecialidadeProcCirgsDAO = getMbcEspecialidadeProcCirgsDAO();
		
		
		final Short vCirEspSeq = cirurgia.getEspecialidade().getSeq();
		
		for (CursorCProcDescricaoVO vPrc : vPrcs) {
			Short vEsp = null;
			List<MbcEspecialidadeProcCirgs> ess = mbcEspecialidadeProcCirgsDAO.
												pesquisarEspecialidadesProcCirgsPorEspSeqEPciSeq(vCirEspSeq, vPrc.getPciSeq());
			
			if(!ess.isEmpty()){
				vEsp = ess.get(0).getId().getEspSeq();
				
			} else {
				ess = mbcEspecialidadeProcCirgsDAO.pesquisarEspecialidadesProcCirgsPorEspSeqEPciSeq2(vCirEspSeq, vPrc.getPciSeq());
				
				if(!ess.isEmpty()){
					vEsp = ess.get(0).getId().getEspSeq();
				}
			}
			
			MbcProcEspPorCirurgias procs = procEspPorCirurgiasDAO.obterPorChavePrimaria(
															new MbcProcEspPorCirurgiasId( cirurgia.getSeq(), 
																						  vPrc.getPciSeq(), 
																						  vEsp, 
																						  DominioIndRespProc.DESC
																						 )
															 						   );
		
			if (procs != null) {
				procs.setCirurgia(cirurgia);
				procs.setSituacao(DominioSituacao.A);
				mbcProcEspPorCirurgiasRN.persistirProcEspPorCirurgias(procs);
			} else {
				procs = new MbcProcEspPorCirurgias();
				
				procs.setId(new MbcProcEspPorCirurgiasId(cirurgia.getSeq(), vPrc.getPciSeq(), vEsp, DominioIndRespProc.DESC ));
				procs.setCirurgia(cirurgia);
				procs.setSituacao(DominioSituacao.A);
				procs.setQtd(Byte.valueOf("1"));
				procs.setIndPrincipal(Boolean.TRUE);
				
				mbcProcEspPorCirurgiasRN.persistirProcEspPorCirurgias(procs);
			}
		}
	}
	

	protected MbcProcEspPorCirurgiasDAO getMbcProcEspPorCirurgiasDAO() {
		return mbcProcEspPorCirurgiasDAO;
	}

	protected MbcEspecialidadeProcCirgsDAO getMbcEspecialidadeProcCirgsDAO() {
		return mbcEspecialidadeProcCirgsDAO;
	}

	protected MbcProfDescricoesDAO getMbcProfDescricoesDAO() {
		return mbcProfDescricoesDAO;
	}
	
	protected MbcDescricaoItensDAO getMbcDescricaoItensDAO() {
		return mbcDescricaoItensDAO;
	}

	protected MbcProcDescricoesDAO getMbcProcDescricoesDAO() {
		return mbcProcDescricoesDAO;
	}

	protected MbcDescricaoTecnicasDAO getMbcDescricaoTecnicasDAO() {
		return mbcDescricaoTecnicasDAO;
	}

	protected MbcDescricaoCirurgicaDAO getMbcDescricaoCirurgicaDAO() {
		return mbcDescricaoCirurgicaDAO;
	}

	protected MbcDescricaoCirurgicaJnDAO getMbcDescricaoCirurgicaJnDAO() {
		return mbcDescricaoCirurgicaJnDAO;
	}

	protected IParametroFacade getParametroFacade() {
		return iParametroFacade;
	}

	protected DiagnosticoDescricaoRN getDiagnosticoDescricaoRN() {
		return diagnosticoDescricaoRN;
	}

	private MbcProcEspPorCirurgiasRN getMbcProcEspPorCirurgiasRN() {
		return mbcProcEspPorCirurgiasRN;
	}
	
	protected MbcCirurgiasRN getMbcCirurgiasRN() {
		return mbcCirurgiasRN;
	}
}
