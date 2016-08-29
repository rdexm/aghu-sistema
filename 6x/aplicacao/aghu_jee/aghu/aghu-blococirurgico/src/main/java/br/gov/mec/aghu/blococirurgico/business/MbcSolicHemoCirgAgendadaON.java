package br.gov.mec.aghu.blococirurgico.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcProcEspPorCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcSolicHemoCirgAgendadaDAO;
import br.gov.mec.aghu.business.bancosangue.IBancoDeSangueFacade;
import br.gov.mec.aghu.dominio.DominioOperacaoBanco;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AbsComponenteSanguineo;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgias;
import br.gov.mec.aghu.model.MbcSolicHemoCirgAgendada;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class MbcSolicHemoCirgAgendadaON extends BaseBusiness{

	private static final Log LOG = LogFactory.getLog(MbcSolicHemoCirgAgendadaON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcProcEspPorCirurgiasDAO mbcProcEspPorCirurgiasDAO;

	@Inject
	private MbcSolicHemoCirgAgendadaDAO mbcSolicHemoCirgAgendadaDAO;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@EJB
	private IBancoDeSangueFacade iBancoDeSangueFacade;

	@EJB
	private IRegistroColaboradorFacade iRegistroColaboradorFacade;

	@EJB
	private MbcSolicHemoCirgAgendadaRN mbcSolicHemoCirgAgendadaRN;

	@EJB
	private IBlocoCirurgicoFacade iBlocoCirurgicoFacade;

	@EJB
	private SolicitacaoHemoterapicaRN solicitacaoHemoterapicaRN;

	/**
	 * 
	 */
	private static final long serialVersionUID = 3137358169555916452L;
	
	public enum MbcSolicHemoCirgAgendadaONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_SOLIC_HEMO_VALIDA_CAMPOS_QUANTIDADE,
		MENSAGEM_SOLIC_HEMO_PREENCHER_CAMPOS_QUANTIDADE;
	}

	public MbcProcEspPorCirurgias pesquisarTipagemSanguinea(Integer pCrgSeq) {
		List<MbcProcEspPorCirurgias> procCirug = getMbcProcEspPorCirurgiasDAO().
			pesquisarProcEspCirurgicoPrincipalAgendamentoPorCrgSeq(pCrgSeq,DominioSituacao.A,Boolean.TRUE);
		
		if (procCirug != null && !procCirug.isEmpty()) {
			return procCirug.get(0);
		} else {
			return null;
		}
		
	}
	
	public String persistirMbcSolicHemoCirgAgendada(MbcSolicHemoCirgAgendada solicHemoCirgAgendada)  throws BaseException{
		
		validarCamposQuantidade(solicHemoCirgAgendada);
		
		if(getMbcSolicHemoCirgAgendadaDAO().obterOriginal(solicHemoCirgAgendada.getId()) == null){
			
			AbsComponenteSanguineo componenteSanguineo = getBancoDeSangueFacade().obterAbsComponenteSanguineoPorId(solicHemoCirgAgendada.getId().getCsaCodigo());
			
			solicHemoCirgAgendada.getId().setCsaCodigo(componenteSanguineo.getCodigo());
			
			// RN4
			getSolicitacaoHemoterapicaRN().rnShapAtuAgenda(DominioOperacaoBanco.INS, // operacao
					solicHemoCirgAgendada.getId().getCrgSeq(), //crgSeq
					solicHemoCirgAgendada.getId().getCsaCodigo(), // csaCodigo 
					solicHemoCirgAgendada.getIndIrradiado(),	// indIrradiado
					solicHemoCirgAgendada.getIndFiltrado(), // indFiltrado
					solicHemoCirgAgendada.getIndLavado(), // indLavado
					solicHemoCirgAgendada.getQuantidade(), //qtdeUnidade
					solicHemoCirgAgendada.getQtdeMl()); //qtdeMl
			
			// RN2
			getMbcSolicHemoCirgAgendadaRN().preInserir(solicHemoCirgAgendada);
			
			// inserir
			solicHemoCirgAgendada.setIndAutoTransfusao(Boolean.FALSE);
			solicHemoCirgAgendada.setIndImprLaudo(Boolean.FALSE);
			solicHemoCirgAgendada.setCriadoEm(new Date());
			getMbcSolicHemoCirgAgendadaDAO().persistir(solicHemoCirgAgendada); 
			
			return "MENSAGEM_SOLIC_HEMO_INSERCAO_COM_SUCESSO";
		}else{
			
			// RN4
			getSolicitacaoHemoterapicaRN().rnShapAtuAgenda(DominioOperacaoBanco.UPD, // operacao
					solicHemoCirgAgendada.getId().getCrgSeq(), //crgSeq
					solicHemoCirgAgendada.getId().getCsaCodigo(), // csaCodigo 
					solicHemoCirgAgendada.getIndIrradiado(),	// indIrradiado
					solicHemoCirgAgendada.getIndFiltrado(), // indFiltrado
					solicHemoCirgAgendada.getIndLavado(), // indLavado
					solicHemoCirgAgendada.getQuantidade(), //qtdeUnidade
					solicHemoCirgAgendada.getQtdeMl()); //qtdeMl
			
			// RN1 -> RN5
			solicHemoCirgAgendada.setCriadoEm(new Date());
			solicHemoCirgAgendada.setServidor(servidorLogadoFacade.obterServidorLogado());
			getSolicitacaoHemoterapicaRN().mbctShaBru(solicHemoCirgAgendada);
			
			// alterar
			getMbcSolicHemoCirgAgendadaDAO().merge(solicHemoCirgAgendada); 
			
			return "MENSAGEM_SOLIC_HEMO_ALTERACAO_COM_SUCESSO";
		}
	}

	private void validarCamposQuantidade(MbcSolicHemoCirgAgendada solicHemoCirgAgendada) throws ApplicationBusinessException {
		if(solicHemoCirgAgendada.getQuantidade() != null && solicHemoCirgAgendada.getQtdeMl() != null){
			 throw new ApplicationBusinessException(MbcSolicHemoCirgAgendadaONExceptionCode.MENSAGEM_SOLIC_HEMO_VALIDA_CAMPOS_QUANTIDADE);
		}
		if(solicHemoCirgAgendada.getQuantidade() == null && solicHemoCirgAgendada.getQtdeMl() == null){
			 throw new ApplicationBusinessException(MbcSolicHemoCirgAgendadaONExceptionCode.MENSAGEM_SOLIC_HEMO_PREENCHER_CAMPOS_QUANTIDADE);
		}
	}

	private MbcSolicHemoCirgAgendadaDAO getMbcSolicHemoCirgAgendadaDAO() {
		return mbcSolicHemoCirgAgendadaDAO;
	}

	public String excluirMbcSolicHemoCirgAgendada(MbcSolicHemoCirgAgendada mbcSolicHemoCirgAgendadaDelecao) 
		throws BaseException {
		
		MbcSolicHemoCirgAgendada original = getMbcSolicHemoCirgAgendadaDAO().obterMbcSolicHemoCirgAgendadaById(mbcSolicHemoCirgAgendadaDelecao.getId().getCrgSeq(), mbcSolicHemoCirgAgendadaDelecao.getId().getCsaCodigo());
		// RN3 -> RN5 e RN6
		getSolicitacaoHemoterapicaRN().mbctShaBrd(original);
		
		// RN4
		getSolicitacaoHemoterapicaRN().rnShapAtuAgenda(DominioOperacaoBanco.DEL, // operacao
				original.getId().getCrgSeq(), //crgSeq
				original.getId().getCsaCodigo(), // csaCodigo 
				original.getIndIrradiado(),	// indIrradiado
				original.getIndFiltrado(), // indFiltrado
				original.getIndLavado(), // indLavado
				original.getQuantidade(), //qtdeUnidade
				original.getQtdeMl()); //qtdeMl
		
		getMbcSolicHemoCirgAgendadaDAO().remover(original);
		
		return "MENSAGEM_SOLIC_HEMO_SUCESSO_EXCLUSAO";
	}
	
	public void refreshListMbcSolicHemoCirgAgendada(List<MbcSolicHemoCirgAgendada> list) {
		for(MbcSolicHemoCirgAgendada item : list){
			if(getBlocoCirurgicoFacade().containsMbcSolicHemoCirgAgendada(item)){
				getBlocoCirurgicoFacade().refreshMbcSolicHemoCirgAgendada(item);
			}
		}
	}
	
	private SolicitacaoHemoterapicaRN getSolicitacaoHemoterapicaRN() {
		return solicitacaoHemoterapicaRN;
	}

	private MbcProcEspPorCirurgiasDAO getMbcProcEspPorCirurgiasDAO() {
		return mbcProcEspPorCirurgiasDAO;
	}
	
	protected IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return iBlocoCirurgicoFacade;
	}

	private MbcSolicHemoCirgAgendadaRN getMbcSolicHemoCirgAgendadaRN() {
		return mbcSolicHemoCirgAgendadaRN;
	}
	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return iRegistroColaboradorFacade;
	}	
	
	protected IBancoDeSangueFacade getBancoDeSangueFacade() {
		return iBancoDeSangueFacade;
	}
}
