package br.gov.mec.aghu.blococirurgico.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcAgendaHemoterapiaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcControleEscalaCirurgicaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcSolicHemoCirgAgendadaDAO;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.dominio.DominioNaturezaFichaAnestesia;
import br.gov.mec.aghu.dominio.DominioOperacaoBanco;
import br.gov.mec.aghu.dominio.DominioTipoEscala;
import br.gov.mec.aghu.model.MbcAgendaHemoterapia;
import br.gov.mec.aghu.model.MbcAgendaHemoterapiaId;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcControleEscalaCirurgica;
import br.gov.mec.aghu.model.MbcSolicHemoCirgAgendada;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class SolicitacaoHemoterapicaRN extends BaseBusiness{

	private static final Log LOG = LogFactory.getLog(SolicitacaoHemoterapicaRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcAgendasDAO mbcAgendasDAO;

	@Inject
	private MbcSolicHemoCirgAgendadaDAO mbcSolicHemoCirgAgendadaDAO;

	@Inject
	private MbcCirurgiasDAO mbcCirurgiasDAO;

	@Inject
	private MbcControleEscalaCirurgicaDAO mbcControleEscalaCirurgicaDAO;

	@Inject
	private MbcAgendaHemoterapiaDAO mbcAgendaHemoterapiaDAO;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@EJB
	private ICascaFacade iCascaFacade;

	@EJB
	private MbcAgendaHemoterapiaRN mbcAgendaHemoterapiaRN;

	@EJB
	private MbcSolicHemoCirgAgendadaRN mbcSolicHemoCirgAgendadaRN;

	private static final long serialVersionUID = -3652814879585359159L;
	
	public enum SolicitacaoHemoterapicaRNExceptionCode implements BusinessExceptionCode {
		MBC_00312, MBC_00237, MBC_00451;
	}

	/**
	 * @throws ApplicationBusinessException 
	 * @throws ApplicationBusinessException 
	 * @ORADB MBCT_SHA_BRU
	 */
	public void mbctShaBru(MbcSolicHemoCirgAgendada modificado) throws ApplicationBusinessException, ApplicationBusinessException{
		MbcSolicHemoCirgAgendada original = getMbcSolicHemoCirgAgendadaDAO().obterOriginal(modificado);
		
		/* Verifica se componente sangüíneo está ativo  */
		if(original.getId().getCsaCodigo().equals(modificado.getId().getCsaCodigo())){
			getMbcSolicHemoCirgAgendadaRN().verificarComponenteSanguineo(modificado);
		}
		
		 /* Verifica indicadores irradiado,filtrado,lavado */
		if(modificado.getIndIrradiado() || modificado.getIndFiltrado() || modificado.getIndLavado()){
			getMbcSolicHemoCirgAgendadaRN().verificarIndicadores(modificado);
		}
		
		/* Verifica se já tem escala definitiva para a data da cirurgia */
		rnShapVerEscDef(modificado.getId().getCrgSeq());
		
	}
	/**
	 * @param mbcCirurgias 
	 * Verificar se já existe escala definitiva
	 * @throws ApplicationBusinessException 
	 * @ORADB mbck_sha_rn.rn_shap_ver_esc_def 
	 */
	public void rnShapVerEscDef(Integer crgSeq) throws ApplicationBusinessException {
		 MbcCirurgias cirg = getMbcCirurgiasDAO().obterPorChavePrimaria(crgSeq);
		 if(cirg == null){
			 throw new ApplicationBusinessException(SolicitacaoHemoterapicaRNExceptionCode.MBC_00312);
		 }
	
		 if(DominioNaturezaFichaAnestesia.ELE.equals(cirg.getNaturezaAgenda())){
			MbcControleEscalaCirurgica controleEscalaCirg = getMbcControleEscalaCirurgicaDAO()
					.obterControleEscalaCirgPorUnidadeDataAgendaTruncadaTipoEscala(
							cirg.getUnidadeFuncional().getSeq(), DateUtil.truncaData(cirg.getData()), DominioTipoEscala.D);
			if(controleEscalaCirg != null){
				 throw new ApplicationBusinessException(SolicitacaoHemoterapicaRNExceptionCode.MBC_00237);
			}
		 }
	}
	
	/**
	 * @throws ApplicationBusinessException 
	 * @ORADB MBCT_SHA_BRD
	 */
	public void mbctShaBrd(MbcSolicHemoCirgAgendada modificado) throws ApplicationBusinessException {
		/* Verifica se já tem escala definitiva para a data da cirurgia */
		rnShapVerEscDef(modificado.getId().getCrgSeq());
		/* Não permitir alterar anestesia da cirurgia se natureza do agendamento for  eletiva e usuário não tem perfil de 'agendar cirurgia não prevista*/
		rnShapVerAltElet(modificado.getId().getCrgSeq());
	}
	
	/**
	 *  @ORADB mbck_sha_rn.rn_shap_ver_alt_elet
   	 *	Descrição:  Se a natureza do agendamento for eletiva e o usuário
   	 *	não tem perfil de 'agendar cirurgia  não prevista' e já foi executada a escala definitiva, não permito mais aatualizar  solicitação hemoterápica.
   	 *	Dar mensagem: Não é permitido alterar sol. hemoterápica. Já foi  executada a escala definitiva para esta data.
	 * @throws ApplicationBusinessException 
	 */
	public void rnShapVerAltElet(Integer crgSeq) throws ApplicationBusinessException {
		 MbcCirurgias cirg = getMbcCirurgiasDAO().obterPorChavePrimaria(crgSeq);
		 
		 if(DominioNaturezaFichaAnestesia.ELE.equals(cirg.getNaturezaAgenda())){
			 //Validar perfil
			 Boolean permitirAgendarCirurgiaNaoPrevista = getCascaFacade().usuarioTemPermissao(servidorLogadoFacade.obterServidorLogado().getUsuario(), "permiteAgendarCirurgiaNaoPrevista");
			 if(!permitirAgendarCirurgiaNaoPrevista){//Caso o usuário NÃO tenha permissão, ENTÃO será feita mais uma validação
				 
				 MbcControleEscalaCirurgica controleEscalaCirg = getMbcControleEscalaCirurgicaDAO()
					.obterControleEscalaCirgPorUnidadeDataAgendaTruncadaTipoEscala(
							cirg.getUnidadeFuncional().getSeq(), DateUtil.truncaData(cirg.getData()), DominioTipoEscala.D);
				
				 if(controleEscalaCirg != null){
					 throw new ApplicationBusinessException(SolicitacaoHemoterapicaRNExceptionCode.MBC_00451);
				}
			 }
		 }
	}
	
	/**
	 * @throws BaseException 
	 * @ORADB RN_SHAP_ATU_AGENDA
	 */
	public void rnShapAtuAgenda(DominioOperacaoBanco operacao, Integer crgSeq, 
			String csaCodigo, Boolean indIrradiado,	Boolean indFiltrado, Boolean indLavado, Short qtdeUnidade,	Short qtdeMl) throws BaseException{
		MbcAgendas agenda = getMbcAgendasDAO().obterMbcAgendaGeradaPeloSistemaporCirurgia(crgSeq);
		if(agenda == null){
			return;
		}
		
		if(DominioOperacaoBanco.INS.equals(operacao)){
			MbcAgendaHemoterapia agendaHemot = new MbcAgendaHemoterapia();
			setAtributosMbcAgendaHemoterapia(agendaHemot, crgSeq, agenda.getSeq(), csaCodigo, indIrradiado, indFiltrado, indLavado, qtdeUnidade, qtdeMl);
			getMbcAgendaHemoterapiaRN().persistirAgendaHemoterapia(agendaHemot);
		}
		
		if(DominioOperacaoBanco.UPD.equals(operacao)){
			MbcAgendaHemoterapia agendaHemot = getMbcAgendaHemoterapiaDAO().obterPorChavePrimaria(new MbcAgendaHemoterapiaId(agenda.getSeq(), csaCodigo));
			getMbcAgendaHemoterapiaDAO().refresh(agendaHemot);
			setAtributosMbcAgendaHemoterapia(agendaHemot, crgSeq, agenda.getSeq(), 
					csaCodigo, indIrradiado, indFiltrado, 
					indLavado, 
					qtdeUnidade, qtdeMl);
			getMbcAgendaHemoterapiaRN().persistirAgendaHemoterapia(agendaHemot);
		}
		
		if(DominioOperacaoBanco.DEL.equals(operacao)){
			MbcAgendaHemoterapia agendaHemot = getMbcAgendaHemoterapiaDAO().obterPorChavePrimaria(new MbcAgendaHemoterapiaId(agenda.getSeq(), csaCodigo));
			getMbcAgendaHemoterapiaDAO().refresh(agendaHemot);
			getMbcAgendaHemoterapiaRN().excluirAgendaHemoterapia(agendaHemot);
		}
	}
	
	private void setAtributosMbcAgendaHemoterapia(
			MbcAgendaHemoterapia agendaHemot, Integer crgSeq, Integer seqMbcAgenda, String csaCodigo,
			Boolean indIrradiado, Boolean indFiltrado, Boolean indLavado,
			Short qtdeUnidade, Short qtdeMl) {
		if(agendaHemot.getId() == null){
			agendaHemot.setId(new MbcAgendaHemoterapiaId());
			agendaHemot.getId().setAgdSeq(seqMbcAgenda);
			agendaHemot.getId().setCsaCodigo(csaCodigo);
		}
		
		agendaHemot.setIndIrradiado(indIrradiado);
		agendaHemot.setIndFiltrado(indFiltrado);
		agendaHemot.setIndLavado(indLavado);
		agendaHemot.setQtdeUnidade(qtdeUnidade);
		agendaHemot.setQtdeMl(qtdeMl);
		
	}
	protected MbcSolicHemoCirgAgendadaDAO getMbcSolicHemoCirgAgendadaDAO(){
		return mbcSolicHemoCirgAgendadaDAO;
	}
	
	protected MbcAgendasDAO getMbcAgendasDAO(){
		return mbcAgendasDAO;
	}
	
	protected MbcSolicHemoCirgAgendadaRN getMbcSolicHemoCirgAgendadaRN(){
		return mbcSolicHemoCirgAgendadaRN;
	}
	
	protected MbcAgendaHemoterapiaRN getMbcAgendaHemoterapiaRN(){
		return mbcAgendaHemoterapiaRN;
	}
	
	protected MbcAgendaHemoterapiaDAO getMbcAgendaHemoterapiaDAO(){
		return mbcAgendaHemoterapiaDAO;
	}
	
	protected MbcCirurgiasDAO getMbcCirurgiasDAO(){
		return mbcCirurgiasDAO;
	}
	
	protected MbcControleEscalaCirurgicaDAO getMbcControleEscalaCirurgicaDAO(){
		return mbcControleEscalaCirurgicaDAO;
	}
	
	protected ICascaFacade getCascaFacade(){
		return iCascaFacade;
	}
	
}
