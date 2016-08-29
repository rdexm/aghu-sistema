package br.gov.mec.aghu.blococirurgico.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.DiagnosticoDescricaoRN.DiagnosticoDescricaoRNExceptionCode;
import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcDescricaoCirurgicaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcDiagnosticoDescricaoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcDiagnosticoDescricaoJnDAO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioClassificacaoDiagnostico;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSexoDeterminante;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoDescricaoCirurgia;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcDescricaoCirurgica;
import br.gov.mec.aghu.model.MbcDescricaoCirurgicaId;
import br.gov.mec.aghu.model.MbcDiagnosticoDescricao;
import br.gov.mec.aghu.model.MbcDiagnosticoDescricaoId;
import br.gov.mec.aghu.model.MbcDiagnosticoDescricaoJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * Classe responsável pelas regras de negócio relacionadas à entidade MbcDiagnosticoDescricao.
 * 
 * @author dpacheco
 *
 */
@Stateless
public class DiagnosticoDescricaoRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(DiagnosticoDescricaoRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcDiagnosticoDescricaoJnDAO mbcDiagnosticoDescricaoJnDAO;

	@Inject
	private MbcCirurgiasDAO mbcCirurgiasDAO;

	@Inject
	private MbcDiagnosticoDescricaoDAO mbcDiagnosticoDescricaoDAO;

	@Inject
	private MbcDescricaoCirurgicaDAO mbcDescricaoCirurgicaDAO;


	@EJB
	private ProfDescricoesRN profDescricoesRN;

	@EJB
	private IParametroFacade iParametroFacade;

	@EJB
	private IAghuFacade iAghuFacade;
	
	@EJB
	private IRegistroColaboradorFacade iRegistroColaboradorFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -1559161133938369663L;
	
	protected enum DiagnosticoDescricaoRNExceptionCode implements BusinessExceptionCode {
		MBC_01362, MBC_00681, MBC_00668, MBC_00669, MBC_00670;
	}

	@SuppressWarnings("ucd")
	public void alterarDiagnosticoDescricoes( final MbcDiagnosticoDescricao diagnosticoDescricao) throws ApplicationBusinessException, ApplicationBusinessException {
		
		final MbcDiagnosticoDescricao oldDiagnosticoDescricao = getMbcDiagnosticoDescricaoDAO().obterOriginal(diagnosticoDescricao.getId());

		executarAntesDeAlterar(diagnosticoDescricao, oldDiagnosticoDescricao, servidorLogadoFacade.obterServidorLogado());
		getMbcDiagnosticoDescricaoDAO().atualizar(diagnosticoDescricao);	
		executarDepoisDeAlterar(diagnosticoDescricao, oldDiagnosticoDescricao, servidorLogadoFacade.obterServidorLogado());
	}
	
	/**
	 * ORADB: MBCT_DDC_BRU
	 */
	private void executarAntesDeAlterar( final MbcDiagnosticoDescricao diagnosticoDescricao,
										 final MbcDiagnosticoDescricao oldDiagnosticoDescricao,
								 		 final RapServidores servidorLogado ) throws ApplicationBusinessException, ApplicationBusinessException {
		
		verificaUsuarioInclusaoAlteracao(oldDiagnosticoDescricao.getServidor());
		
		verificaDescricaoConcluida(oldDiagnosticoDescricao.getMbcDescricaoCirurgica());
		
		verificarCidAtivo(diagnosticoDescricao.getCid());
		
		// Quando alterado o cid verificar se está compatível com o sexo do cadastro do paciente 
		if(!CoreUtil.igual(diagnosticoDescricao.getCid(), oldDiagnosticoDescricao.getCid())){
			verificarCidCompativelSexoPaciente(diagnosticoDescricao.getCid(), diagnosticoDescricao.getId().getDcgCrgSeq());
		}
		
		// atualiza servidor
		diagnosticoDescricao.setServidor(servidorLogado);
	}

	/**
	 * ORADB: MBCT_DDC_ARU
	 */
	private void executarDepoisDeAlterar(final MbcDiagnosticoDescricao diagnosticoDescricao,
										 final MbcDiagnosticoDescricao oldDiagnosticoDescricao,
								 		 final RapServidores servidorLogado ){
		
		if( !CoreUtil.igual(diagnosticoDescricao.getId().getDcgCrgSeq(), oldDiagnosticoDescricao.getId().getDcgCrgSeq()) ||
				!CoreUtil.igual(diagnosticoDescricao.getId().getDcgSeqp(), oldDiagnosticoDescricao.getId().getDcgSeqp()) ||
				!CoreUtil.igual(diagnosticoDescricao.getId().getCidSeq(), oldDiagnosticoDescricao.getId().getCidSeq()) ||
				!CoreUtil.igual(diagnosticoDescricao.getId().getClassificacao(), oldDiagnosticoDescricao.getId().getClassificacao()) ||
				!CoreUtil.igual(diagnosticoDescricao.getComplemento(), oldDiagnosticoDescricao.getComplemento()) ||
				!CoreUtil.igual(diagnosticoDescricao.getDestacar(), oldDiagnosticoDescricao.getDestacar()) ||
				!CoreUtil.igual(diagnosticoDescricao.getCriadoEm(), oldDiagnosticoDescricao.getCriadoEm()) ||
				!CoreUtil.igual(diagnosticoDescricao.getServidor(), oldDiagnosticoDescricao.getServidor())
		){
			final MbcDiagnosticoDescricaoJn journal = createJournal( oldDiagnosticoDescricao, 
																     DominioOperacoesJournal.UPD);
			getMbcDiagnosticoDescricaoJnDAO().persistir(journal);
		}
	}
	
	
	/**
	 * Verifica se o usuário que está alterando é o mesmo que incluiu
	 * 
	 * ORADB: MBCK_MBC_RN.RN_MBCP_VER_ALTERA
	 */
	public void verificaUsuarioInclusaoAlteracao( final RapServidores oldServidor) throws ApplicationBusinessException{
		if(!CoreUtil.igual(oldServidor, servidorLogadoFacade.obterServidorLogado())){
			throw new ApplicationBusinessException(DiagnosticoDescricaoRNExceptionCode.MBC_00669);
		}
	}

	public void excluirDiagnosticoDescricoes( final MbcDiagnosticoDescricao diagnosticoDescricao) throws ApplicationBusinessException, ApplicationBusinessException{
		
		executarAntesDeExcluir(diagnosticoDescricao);
		getMbcDiagnosticoDescricaoDAO().remover(diagnosticoDescricao);
		executarAposExcluir(diagnosticoDescricao);
	}

	/**
	 * ORADB: MBCT_DDC_BRD
	 * @throws ApplicationBusinessException 
	 */
	protected void executarAntesDeExcluir(final MbcDiagnosticoDescricao diagnosticoDescricao) throws ApplicationBusinessException, ApplicationBusinessException{
		RapServidores servidor = iRegistroColaboradorFacade.obterRapServidor(diagnosticoDescricao.getServidor().getId());
		
		validaServidorExclusao(servidor);
		verificaDescricaoConcluida(diagnosticoDescricao.getMbcDescricaoCirurgica());
	}
	
	/**
	 * Não permite que se exclua o registro desta tabela se a descricao estiver concluida 
	 * 
	 * ORADB: MBCK_MBC_RN.RN_MBCP_VER_CONCLUSA
	 */
	public void verificaDescricaoConcluida(MbcDescricaoCirurgica descricaoCirurgica) throws ApplicationBusinessException, ApplicationBusinessException{
		descricaoCirurgica = mbcDescricaoCirurgicaDAO.obterPorChavePrimaria(descricaoCirurgica.getId());
		
		if(descricaoCirurgica.getDthrConclusao() != null){
			final Integer vValorNumerico = getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_MIN_ALTERA_DESCR);
			
			Integer vSitHoras = DateUtil.diffInDaysInteger(new Date(), descricaoCirurgica.getDthrConclusao());
			vSitHoras *= 24 * 60;
			
			if(DominioSituacaoDescricaoCirurgia.CON.equals(descricaoCirurgica.getSituacao()) &&
					vSitHoras > vValorNumerico){
				throw new ApplicationBusinessException(DiagnosticoDescricaoRNExceptionCode.MBC_00670);
			}
		}
	}
	
	/**
	 * Verifica se o usuario que excluiu foi o mesmo que inclui
	 * 
	 * ORADB: MBCK_MBC_RN.RN_MBCP_VER_EXCLUSAO
	 */
	public void validaServidorExclusao(final RapServidores servidorDiagnosticoDescricao) throws ApplicationBusinessException{
        RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
        RapServidores servidor = iRegistroColaboradorFacade.obterRapServidor(servidorDiagnosticoDescricao.getId());
   
		if(!CoreUtil.igual(servidor, servidorLogado)){
			throw new ApplicationBusinessException(DiagnosticoDescricaoRNExceptionCode.MBC_00668);
		}
	}
	
	
	/**
	 * ORADB: MBCT_DDC_ARD
	 * @param usuarioLogado 
	 */
	protected void executarAposExcluir(final MbcDiagnosticoDescricao diagnosticoDescricao){
		final MbcDiagnosticoDescricaoJn journal = createJournal(diagnosticoDescricao, DominioOperacoesJournal.DEL);
		getMbcDiagnosticoDescricaoJnDAO().persistir(journal);
	}

	private MbcDiagnosticoDescricaoJn createJournal(final MbcDiagnosticoDescricao diagnosticoDescricao, 
													final DominioOperacoesJournal dominio) {
		
		final MbcDiagnosticoDescricaoJn journal =  BaseJournalFactory.getBaseJournal(dominio, MbcDiagnosticoDescricaoJn.class, servidorLogadoFacade.obterServidorLogado().getUsuario());
		
		journal.setCid(diagnosticoDescricao.getCid());
		journal.setClassificacao(diagnosticoDescricao.getId().getClassificacao());
		journal.setComplemento(diagnosticoDescricao.getComplemento());
		journal.setCriadoEm(diagnosticoDescricao.getCriadoEm());
		journal.setDcgCrgSeq(diagnosticoDescricao.getId().getDcgCrgSeq());
		journal.setDcgSeqp(diagnosticoDescricao.getId().getDcgSeqp());
		journal.setDestacar(diagnosticoDescricao.getDestacar());
		journal.setServidor(diagnosticoDescricao.getServidor());
		return journal;
	}
	
	/**
	 * Insere instância de MbcDiagnosticoDescricao.
	 * 
	 * @param diagnosticoDescricao
	 * @param servidorLogado
	 * @throws ApplicationBusinessException
	 */
	public void inserirDiagnosticoDescricoes(
			MbcDiagnosticoDescricao diagnosticoDescricao
			)
			throws ApplicationBusinessException {
		
		executarAntesDeInserir(diagnosticoDescricao);
		getMbcDiagnosticoDescricaoDAO().persistir(diagnosticoDescricao);
		executarEnforce(diagnosticoDescricao); // Enforce é executada somente quando for Insert
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: MBCT_DDC_BRI
	 * 
	 * @param diagnosticoDescricao
	 * @param servidorLogado
	 * @throws ApplicationBusinessException
	 */
	private void executarAntesDeInserir(MbcDiagnosticoDescricao diagnosticoDescricao
			) throws ApplicationBusinessException {
		
		Integer dcgCrgSeq = diagnosticoDescricao.getId().getDcgCrgSeq();
		Short dcgSeqp = diagnosticoDescricao.getId().getDcgSeqp();
		
		ProfDescricoesRN profDescricoesRN = getProfDescricoesRN();
		
		/*
		 * Verifica se o usuario que está fazendo o insert é quem criou a
		 * descricao cirurgica
		 */
		profDescricoesRN.verificarServidorLogadoRealizaDescricaoCirurgica(
				dcgCrgSeq, dcgSeqp);

		
		/*
		 * Código necessário para evitar erros quando o entitymanager não atualiza o objeto inserido....
		 */
		final AghCid cid = getAghuFacade().obterAghCidPorChavePrimaria(diagnosticoDescricao.getId().getCidSeq());
		diagnosticoDescricao.setCid(cid);
		
		final MbcDescricaoCirurgicaDAO dao = getMbcDescricaoCirurgicaDAO();
		final MbcDescricaoCirurgica mdc = dao.obterPorChavePrimaria(new MbcDescricaoCirurgicaId(diagnosticoDescricao.getId().getDcgCrgSeq(), diagnosticoDescricao.getId().getDcgSeqp()));
		diagnosticoDescricao.setMbcDescricaoCirurgica(mdc);
		
		final MbcCirurgias cirurgia = getMbcCirurgiasDAO().obterPorChavePrimaria(diagnosticoDescricao.getId().getDcgCrgSeq());
		diagnosticoDescricao.setCirurgia(cirurgia);
		
		/*
		 * Não permite inserir o registro desta tabela, se a descricao estiver
		 * concluida
		 */
		profDescricoesRN.verificarDescricaoCirurgicaConcluida(dcgCrgSeq,
				dcgSeqp);
		
		/* Verifica se o CID está ativo */
		verificarCidAtivo(cid);

		/*verifica se o CID é compatível com o cadastro do paciente*/
		verificarCidCompativelSexoPaciente(cid, dcgCrgSeq);		
		
		/* Atualiza servidor que incluiu registro */
		diagnosticoDescricao.setServidor(servidorLogadoFacade.obterServidorLogado());
		
		diagnosticoDescricao.setCriadoEm(new Date());
	}
	
	/**
	 * Procedure
	 * 
	 * ORADB: MBCK_DDC_RN.RN_DDCP_VER_CID
	 * 
	 * @param cidSeq
	 * @throws ApplicationBusinessException 
	 */
	protected void verificarCidAtivo(AghCid cid) throws ApplicationBusinessException {
		if (cid != null && DominioSituacao.I.equals(cid.getSituacao())) {
			throw new ApplicationBusinessException(
					DiagnosticoDescricaoRNExceptionCode.MBC_00681);
		}
	}
	
	/**
	 * Procedure
	 * 
	 * ORADB: MBCK_DDC_RN.RN_DDCP_VER_CID_SEX
	 * 
	 * @param cidSeq
	 * @param crgSeq
	 * @throws ApplicationBusinessException
	 */
	protected void verificarCidCompativelSexoPaciente(AghCid cid, Integer crgSeq) 
			throws ApplicationBusinessException {
		DominioSexo sexoPaciente = null;
		DominioSexoDeterminante sexoCid = null;
		
		MbcCirurgias cirurgia = getMbcCirurgiasDAO().obterCirurgiaPacientePorCrgSeq(crgSeq);
		if (cirurgia != null) {
			sexoPaciente = cirurgia.getPaciente().getSexo();
		}
		
		if (cid != null) {
			sexoCid = cid.getRestricaoSexo();
			if(sexoPaciente==null || sexoPaciente.name().equals(sexoCid.name()) || DominioSexoDeterminante.Q.name().equals(sexoCid.name()) ){
				logDebug("Sexo cid compatível com o sexo informado no cadastro do paciente");
			} else {
				// CID informado não é compatível com o sexo informado no cadastro do paciente
				throw new ApplicationBusinessException(DiagnosticoDescricaoRNExceptionCode.MBC_01362);
			}
		}
		
		/*
		 IF    v_pac_sexo = v_cid_sexo or
		       v_pac_sexo is null or
		       v_cid_sexo = 'Q'  THEN
		ELSE
		      --  CID informado não é compatível com o sexo informado no cadastro do paciente
		      raise_application_error  (-20000, 'MBC-01362');
		END IF;
		 */
	}
	
	/**
	 * Procedure
	 * 
	 * ORADB: MBCP_ENFORCE_DDC_RULES
	 * 
	 * @param diagnosticoDescricao
	 * @throws ApplicationBusinessException 
	 */
	private void executarEnforce(MbcDiagnosticoDescricao diagnosticoDescricao) throws ApplicationBusinessException {
		MbcDiagnosticoDescricaoId diagnosticoDescricaoId = diagnosticoDescricao.getId();
		if (DominioClassificacaoDiagnostico.PRE.equals(diagnosticoDescricaoId.getClassificacao())) {
			MbcDiagnosticoDescricaoId diagnosticoDescricaoPosOperId = new MbcDiagnosticoDescricaoId(
					diagnosticoDescricaoId.getDcgCrgSeq(), diagnosticoDescricaoId.getDcgSeqp(), diagnosticoDescricaoId.getCidSeq(),
					DominioClassificacaoDiagnostico.POS);
			
			MbcDiagnosticoDescricao mdc = getMbcDiagnosticoDescricaoDAO().obterPorChavePrimaria(diagnosticoDescricaoPosOperId);
			
			if(mdc == null){
				MbcDiagnosticoDescricao diagnosticoDescricaoPosOper = new MbcDiagnosticoDescricao();
				diagnosticoDescricaoPosOper.setId(diagnosticoDescricaoPosOperId);				
				diagnosticoDescricaoPosOper.setMbcDescricaoCirurgica(diagnosticoDescricao.getMbcDescricaoCirurgica());
				diagnosticoDescricaoPosOper.setComplemento(diagnosticoDescricao.getComplemento());
				diagnosticoDescricaoPosOper.setDestacar(diagnosticoDescricao.getDestacar());
				diagnosticoDescricaoPosOper.setCriadoEm(null);
				diagnosticoDescricaoPosOper.setServidor(null);	
				inserirDiagnosticoDescricoes(diagnosticoDescricaoPosOper);
			}
		}
	}

	
	protected MbcDiagnosticoDescricaoJnDAO getMbcDiagnosticoDescricaoJnDAO() {
		return mbcDiagnosticoDescricaoJnDAO;
	}
	
	protected MbcDiagnosticoDescricaoDAO getMbcDiagnosticoDescricaoDAO() {
		return mbcDiagnosticoDescricaoDAO;
	}
	
	protected MbcCirurgiasDAO getMbcCirurgiasDAO() {
		return mbcCirurgiasDAO;
	}
	
	protected ProfDescricoesRN getProfDescricoesRN() {
		return profDescricoesRN;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.iAghuFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return iParametroFacade;
	}
	
	protected MbcDescricaoCirurgicaDAO getMbcDescricaoCirurgicaDAO(){
		return mbcDescricaoCirurgicaDAO;
	}

}
