package br.gov.mec.aghu.blococirurgico.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcAgendaHemoterapiaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcControleEscalaCirurgicaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcSolicHemoCirgAgendadaDAO;
import br.gov.mec.aghu.business.bancosangue.IBancoDeSangueFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.dao.ObjetosOracleDAO;
import br.gov.mec.aghu.dominio.DominioNaturezaFichaAnestesia;
import br.gov.mec.aghu.dominio.DominioOperacaoBanco;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.model.AbsComponenteSanguineo;
import br.gov.mec.aghu.model.MbcAgendaHemoterapia;
import br.gov.mec.aghu.model.MbcAgendaHemoterapiaId;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcControleEscalaCirurgica;
import br.gov.mec.aghu.model.MbcSolicHemoCirgAgendada;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.business.moduleintegration.InactiveModuleException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;


@Stateless
public class MbcSolicHemoCirgAgendadaRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(MbcSolicHemoCirgAgendadaRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcSolicHemoCirgAgendadaDAO mbcSolicHemoCirgAgendadaDAO;

	@Inject
	private ObjetosOracleDAO objetosOracleDAO;

	@Inject
	private MbcAgendaHemoterapiaDAO mbcAgendaHemoterapiaDAO;

	@Inject
	private MbcControleEscalaCirurgicaDAO mbcControleEscalaCirurgicaDAO;


	@EJB
	private ICascaFacade iCascaFacade;

	@EJB
	private IBancoDeSangueFacade iBancoDeSangueFacade;

	@EJB
	private ISolicitacaoExameFacade iSolicitacaoExameFacade;

	@EJB
	private MbcAgendaHemoterapiaRN mbcAgendaHemoterapiaRN;

	@EJB
	private IBlocoCirurgicoFacade iBlocoCirurgicoFacade;

	@EJB
	private SolicitacaoHemoterapicaRN solicitacaoHemoterapicaRN;


	private static final long serialVersionUID = 5493342328674998011L;

	public enum MbcSolicHemoCirgAgendadaRNExceptionCode implements BusinessExceptionCode {
		MBC_00224, MBC_00230, MBC_00225, MBC_00226, MBC_00227, MBC_00312, MBC_00237, MBC_00451;
	}
	
	/**
	 * @ORADB MBCT_SHA_BRD
	 * @param elemento
	 * @param servidorLogado
	 * @throws BaseException
	 */
	public void deletar(MbcSolicHemoCirgAgendada elemento) throws BaseException{
		preDeletar(elemento);		
		this.getMbcSolicHemoCirgAgendadaDAO().remover(elemento);
		posDeletar(elemento);
	}

	private void posDeletar(MbcSolicHemoCirgAgendada elemento) throws BaseException {
		MbcAgendaHemoterapia mbcAgendaHemoterapia = getMbcAgendaHemoterapiaDAO().obterPorChavePrimaria(new MbcAgendaHemoterapiaId(elemento.getMbcCirurgias().getAgenda().getSeq(),elemento.getId().getCsaCodigo()));
		getMbcAgendaHemoterapiaRN().excluirAgendaHemoterapia(mbcAgendaHemoterapia);
	}

	private void preDeletar(MbcSolicHemoCirgAgendada elemento
			) throws BaseException{
		verificarEscalaDefinitivaDataCirurgia(elemento);
		verificarAlteraAnestesia(elemento);
	}

	/**
	 * @ORADB mbck_sha_rn.rn_shap_ver_alt_elet
	 * @param elemento
	 * @param servidorLogado 
	 * @throws ApplicationBusinessException 
	 */
	private void verificarAlteraAnestesia(MbcSolicHemoCirgAgendada elemento) throws ApplicationBusinessException {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		if(DominioNaturezaFichaAnestesia.ELE.equals(elemento.getMbcCirurgias().getNaturezaAgenda())){
			//TODO: configurar permissão correta
			if(getICascaFacade().usuarioTemPermissao(servidorLogado.getUsuario(), "permissao")){
				List<MbcControleEscalaCirurgica> escalaCirurgicas = getMbcControleEscalaCirurgicaDAO().pesquisarControleEscalaCirurgicaPorUnfSeqDataPesquisa(elemento.getMbcCirurgias().getUnidadeFuncional().getSeq(),elemento.getMbcCirurgias().getData());
				if(!escalaCirurgicas.isEmpty()){
					throw new ApplicationBusinessException(MbcSolicHemoCirgAgendadaRNExceptionCode.MBC_00451);
				}
			}
		}
	}

	/**
	 * @ORADB mbck_sha_rn.rn_shap_ver_esc_def
	 * @param elemento
	 * @throws ApplicationBusinessException
	 */
	public void verificarEscalaDefinitivaDataCirurgia(
			MbcSolicHemoCirgAgendada elemento) throws ApplicationBusinessException {
		if(elemento.getMbcCirurgias() == null){
			throw new ApplicationBusinessException(MbcSolicHemoCirgAgendadaRNExceptionCode.MBC_00312);
		}
		
		if(DominioNaturezaFichaAnestesia.ELE.equals(elemento.getMbcCirurgias().getNaturezaAgenda())){
			List<MbcControleEscalaCirurgica> escalaCirurgicas = getMbcControleEscalaCirurgicaDAO().pesquisarControleEscalaCirurgicaPorUnfSeqDataPesquisa(elemento.getMbcCirurgias().getUnidadeFuncional().getSeq(),elemento.getMbcCirurgias().getData());
			if(!escalaCirurgicas.isEmpty()){
				throw new ApplicationBusinessException(MbcSolicHemoCirgAgendadaRNExceptionCode.MBC_00237);
			}
		}
	}

	/**
	 * 
	 * @param elemento
	 * @param servidorLogado
	 * @throws BaseException 
	 */
	public void inserir(MbcSolicHemoCirgAgendada elemento) throws BaseException{

		this.preInserir(elemento);
		this.getMbcSolicHemoCirgAgendadaDAO().persistir(elemento);
		this.getMbcSolicHemoCirgAgendadaDAO().flush();
		this.posInserir(elemento);

	}
	
	private void posInserir(MbcSolicHemoCirgAgendada elemento) throws BaseException {
		this.getSolicitacaoHemoterapicaRN().rnShapAtuAgenda(DominioOperacaoBanco.INS, // operacao
				elemento.getId().getCrgSeq(), //crgSeq
				elemento.getId().getCsaCodigo(), // csaCodigo 
				elemento.getIndIrradiado(), // indIrradiado
				elemento.getIndFiltrado(), // indFiltrado
				elemento.getIndLavado(), // indLavado
				elemento.getQuantidade(), //qtdUnidade
				elemento.getQtdeMl()); //qtdeMl		
	}

	/**
	 * ORADB TRIGGER "AGH".MBCT_SHA_BRI
	 * @param elemento
	 * @param servidorLogado 
	 * @throws BaseException 
	 */
	public void preInserir(MbcSolicHemoCirgAgendada elemento) throws BaseException {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		/* Verifica se componente sangüíneo está ativo  */
		this.verificarComponenteSanguineo(elemento);

		if(elemento.getIndIrradiado() || elemento.getIndFiltrado() || elemento.getIndLavado()){
			this.verificarIndicadores(elemento);
		}
		/* Não permitir alterar cirurgia se natureza do agendamento for eletiva 
		 * e usuário não tem perfil de 'agendar cirurgia não prevista'*/
		getSolicitacaoHemoterapicaRN().rnShapVerAltElet(elemento.getId().getCrgSeq());

		/* atualiza o servidor que está inserindo o registro*/
		elemento.setServidor(servidorLogado);
		
		verificarProfissionalResponsavel(elemento, null);
	}

	/**
	 * mbck_sha_rn.rn_shap_ver_indicad
	 * @param elemento
	 * @throws ApplicationBusinessException 
	 */
	public void verificarIndicadores(MbcSolicHemoCirgAgendada elemento) throws ApplicationBusinessException {

		AbsComponenteSanguineo originalAbsComp;
		if(elemento.getAbsComponenteSanguineo()==null){
			/* Não existe componente sangüíneo com este código */
			throw new ApplicationBusinessException(MbcSolicHemoCirgAgendadaRNExceptionCode.MBC_00224);
		}else {
			originalAbsComp = getBancoDeSangueFacade().obterAbsComponenteSanguineoOriginal(elemento.getAbsComponenteSanguineo().getCodigo());
			if(!originalAbsComp.getIndIrradiado() && elemento.getIndIrradiado()){
				/* Este componente não pode ser irradiado. Entre em contato com Banco de Sangue */
				throw new ApplicationBusinessException(MbcSolicHemoCirgAgendadaRNExceptionCode.MBC_00225);
			}else if(!originalAbsComp.getIndFiltrado() && elemento.getIndFiltrado()){
				/* Este componente não pode ser filtrado. Entre em contato com Banco de Sangue */
				throw new ApplicationBusinessException(MbcSolicHemoCirgAgendadaRNExceptionCode.MBC_00226);
			}else if (!originalAbsComp.getIndLavado() && elemento.getIndLavado()){
				/* Este componente não pode ser lavado. Entre em contato com Banco de Sangue */
				throw new ApplicationBusinessException(MbcSolicHemoCirgAgendadaRNExceptionCode.MBC_00227);
			}
		}

	}


	/**
	 *  ORADB mbck_sha_rn.rn_shap_ver_componen
	 * @param elemento
	 * @throws ApplicationBusinessException
	 */
	public void verificarComponenteSanguineo(MbcSolicHemoCirgAgendada elemento)
	throws ApplicationBusinessException {

		AbsComponenteSanguineo componenteSanguineo = getBancoDeSangueFacade().obterAbsComponenteSanguineoPorId(elemento.getId().getCsaCodigo());
		
		if(componenteSanguineo==null){
			/* Não existe componente sangüíneo com este código */
			throw new ApplicationBusinessException(MbcSolicHemoCirgAgendadaRNExceptionCode.MBC_00224);

		}else{

			if(!componenteSanguineo.getIndSituacao().equals(DominioSituacao.A)){
				/* Componente sangüíneo está inativo */
				throw new ApplicationBusinessException(MbcSolicHemoCirgAgendadaRNExceptionCode.MBC_00230);
			}
		}
	}
	
	private void verificarProfissionalResponsavel(MbcSolicHemoCirgAgendada elemento, String nomeMicrocomputador) throws BaseException {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		RapServidores responsavel = getBlocoCirurgicoFacade().buscaRapServidorDeMbcProfCirurgias(elemento.getId().getCrgSeq(), DominioSimNao.S);		
		MbcCirurgias cirurgia = getBlocoCirurgicoFacade().obterCirurgiaPorSeqInnerJoinAtendimento(elemento.getId().getCrgSeq());
		
		if(responsavel != null && cirurgia != null && cirurgia.getAtendimento() != null
			&& DominioPacAtendimento.S.equals(cirurgia.getAtendimento().getIndPacAtendimento())
			&& !cirurgia.getSituacao().equals(DominioSituacaoCirurgia.RZDA)
			&& !cirurgia.getSituacao().equals(DominioSituacaoCirurgia.CANC)) {
			try {
				getSolicitacaoExameFacade()
						.gerarExameProvaCruzadaTransfusional(
								cirurgia.getAtendimento(), cirurgia,
								nomeMicrocomputador, 
								responsavel, Boolean.FALSE);
			} catch(InactiveModuleException e) {
				logWarn(e.getMessage());
				this.getObjetosOracleDAO()
						.gerarExameProvaCruzadaTransfusional(
								cirurgia.getAtendimento().getSeq(), cirurgia.getSeq(), servidorLogado, responsavel, DominioSimNao.N.toString());
			}
		}
	}

	private ObjetosOracleDAO getObjetosOracleDAO() {
		return objetosOracleDAO;
	}
	
	protected MbcSolicHemoCirgAgendadaDAO getMbcSolicHemoCirgAgendadaDAO(){
		return mbcSolicHemoCirgAgendadaDAO;
	}

	private SolicitacaoHemoterapicaRN getSolicitacaoHemoterapicaRN() {
		return solicitacaoHemoterapicaRN;
	}
	
	protected MbcControleEscalaCirurgicaDAO getMbcControleEscalaCirurgicaDAO(){
		return mbcControleEscalaCirurgicaDAO;
	}
	
	protected ICascaFacade getICascaFacade() {
		return iCascaFacade;
	}
	
	protected MbcAgendaHemoterapiaDAO getMbcAgendaHemoterapiaDAO(){
		return mbcAgendaHemoterapiaDAO;
	}
	
	protected MbcAgendaHemoterapiaRN getMbcAgendaHemoterapiaRN(){
		return mbcAgendaHemoterapiaRN;
	}
	
	protected IBancoDeSangueFacade getBancoDeSangueFacade(){
		return iBancoDeSangueFacade;
	}

	protected IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return iBlocoCirurgicoFacade;
	}
	
	protected ISolicitacaoExameFacade getSolicitacaoExameFacade() {
		return iSolicitacaoExameFacade;
	}
}