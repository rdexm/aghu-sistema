package br.gov.mec.aghu.blococirurgico.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.blococirurgico.business.ListaCirurgiasDescCirurgicaON.ListarCirurgiasDescCirurgicaONExceptionCode;
import br.gov.mec.aghu.blococirurgico.dao.MbcAnestesiaDescricoesDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcDescricaoCirurgicaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcDescricaoItensDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcDiagnosticoDescricaoDAO;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade;
import br.gov.mec.aghu.dominio.DominioClassificacaoDiagnostico;
import br.gov.mec.aghu.dominio.DominioIndPendenteDiagnosticos;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoDescricaoCirurgia;
import br.gov.mec.aghu.dominio.DominioSituacaoVersaoDocumento;
import br.gov.mec.aghu.dominio.DominioTipoDocumento;
import br.gov.mec.aghu.model.AghVersaoDocumento;
import br.gov.mec.aghu.model.MamDiagnostico;
import br.gov.mec.aghu.model.MbcAnestesiaDescricoes;
import br.gov.mec.aghu.model.MbcDescricaoCirurgica;
import br.gov.mec.aghu.model.MbcDescricaoCirurgicaId;
import br.gov.mec.aghu.model.MbcDescricaoItens;
import br.gov.mec.aghu.model.MbcDescricaoItensId;
import br.gov.mec.aghu.model.MbcDiagnosticoDescricao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * Classe contendo as regras de negócios (métodos) complementares à classe ListaCirurgiasDescCirurgicaON.
 * 
 * @author dpacheco
 *
 */
@Stateless
public class ListaCirurgiasDescCirurgicaComplON extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(ListaCirurgiasDescCirurgicaComplON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcAnestesiaDescricoesDAO mbcAnestesiaDescricoesDAO;

	@Inject
	private MbcDiagnosticoDescricaoDAO mbcDiagnosticoDescricaoDAO;

	@Inject
	private MbcDescricaoItensDAO mbcDescricaoItensDAO;

	@Inject
	private MbcDescricaoCirurgicaDAO mbcDescricaoCirurgicaDAO;


	@EJB
	private ICascaFacade iCascaFacade;

	@EJB
	private DescricaoCirurgicaRN descricaoCirurgicaRN;

	@EJB
	private IAmbulatorioFacade iAmbulatorioFacade;

	@EJB
	private IPacienteFacade iPacienteFacade;

	@EJB
	private ICertificacaoDigitalFacade iCertificacaoDigitalFacade;
	
	private static final String REALIZAR_DESCRICAO_CIRURGICA = "realizarDescricaoCirurgicaImprimir";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5057790465710137265L;

	/** ORADB: P_FINALIZA_DESCRICAO	 
	 * @param volumePerdaSangue 
	 * @param intercorrenciaClinica 
	 * @param indPerdaSangue 
	 * @param indIntercorrencia */
	public Boolean pFinalizaDescricao(Integer crgSeq, Short dcgSeqp, String nomeMicrocomputador, DominioSimNao indIntercorrencia, DominioSimNao indPerdaSangue, String intercorrenciaClinica, Integer volumePerdaSangue, String achadosOperatorios, String descricaoTecnicaDesc) throws BaseException {
		
		MbcDescricaoCirurgica descricaoCirg = getMbcDescricaoCirurgicaDAO().obterDescricaoCirurgicaEAtendimentoPorId(new MbcDescricaoCirurgicaId(crgSeq,  dcgSeqp));

		final MbcDescricaoItens mdi = getMbcDescricaoItensDAO().obterPorChavePrimaria(new MbcDescricaoItensId(crgSeq, dcgSeqp));
		
		if(mdi.getDthrInicioCirg() == null || mdi.getDthrFimCirg() == null) {
			//Data Inicio e Data Fim da cirurgia devem ser informados
			throw new ApplicationBusinessException(ListarCirurgiasDescCirurgicaONExceptionCode.MBC_00880);
		}
		
		if(CoreUtil.isMaiorDatas(mdi.getDthrFimCirg(), new Date())) {
			//Data Final da cirurgia deve ser menor ou igual a data atual.
			throw new ApplicationBusinessException(ListarCirurgiasDescCirurgicaONExceptionCode.MBC_01088);
		}
		
		if(CoreUtil.isMaiorOuIgualDatas(mdi.getDthrInicioCirg(), mdi.getDthrFimCirg())) {
			//Data inicial da cirurgia deve ser menor que a data final da cirurgia.
			throw new ApplicationBusinessException(ListarCirurgiasDescCirurgicaONExceptionCode.MBC_01147);
		}
		
		List<MbcAnestesiaDescricoes> anestesias = getMbcAnestesiaDescricoesDAO().buscarAnestesiasDescricoes(crgSeq, dcgSeqp);
		if(anestesias == null || anestesias.isEmpty()) {
			throw new ApplicationBusinessException(ListarCirurgiasDescCirurgicaONExceptionCode.MBC_00734);
		}
		
		verificarIntercorrenciaPerdaSangue(indIntercorrencia, indPerdaSangue,
				intercorrenciaClinica, volumePerdaSangue);
		
		if(achadosOperatorios == null){
			//Informe se houve achados operat\u00F3rios durante o procedimento.
			throw new ApplicationBusinessException(ListarCirurgiasDescCirurgicaONExceptionCode.MENSAGEM_ERRO_INFORME_ACHADOS_OPERATORIOS);
		}
		
		if(descricaoTecnicaDesc == null){
			throw new ApplicationBusinessException(ListarCirurgiasDescCirurgicaONExceptionCode.MENSAGEM_ERRO_INFORME_DESCRICAO_TECNICA_DESCRICAO);
		}
		
		if(DominioSituacaoDescricaoCirurgia.PEN.equals(descricaoCirg.getSituacao())){
			getDescricaoCirurgicaRN().alterarDescricaoCirurgica(descricaoCirg, servidorLogadoFacade.obterServidorLogado(), nomeMicrocomputador);
			descricaoCirg.setDthrConclusao(new Date()); 
			descricaoCirg.setSituacao(DominioSituacaoDescricaoCirurgia.CON);
			getMbcDescricaoCirurgicaDAO().atualizar(descricaoCirg);
		}
		
		//CERTIFICAÇÃO DIGITAL
		desbloqDocCertificacao(crgSeq, DominioTipoDocumento.DC);
		
		//MBCP_CARREGA_DIAG
		mbcpCarregaDiag(crgSeq);
		
		
		//MBCP_IMPRIME_DESCR_CIRURGICA
		return mbcpImprimeDescrCirurgica(false);		
	}

	private void verificarIntercorrenciaPerdaSangue(
			DominioSimNao indIntercorrencia, DominioSimNao indPerdaSangue,
			String intercorrenciaClinica, Integer volumePerdaSangue)
			throws ApplicationBusinessException {
		if(indIntercorrencia==null){
			throw new ApplicationBusinessException(ListarCirurgiasDescCirurgicaONExceptionCode.MENSAGEM_ERRO_INFORME_INTERCORRENCIA);
        }else if(indIntercorrencia.equals(DominioSimNao.S) && intercorrenciaClinica == null || "".equals(intercorrenciaClinica)){
        	throw new ApplicationBusinessException(ListarCirurgiasDescCirurgicaONExceptionCode.MBC_01835);
        }
        
        if(indPerdaSangue==null){
        	throw new ApplicationBusinessException(ListarCirurgiasDescCirurgicaONExceptionCode.MENSAGEM_ERRO_INFORME_PERDA_SANG_SIGNIF);
        }else if(indPerdaSangue.equals(DominioSimNao.S) && volumePerdaSangue == null){
        	throw new ApplicationBusinessException(ListarCirurgiasDescCirurgicaONExceptionCode.TITLE_VOLUME_PERDA_SANG_SUGNIF);
        }
	}
	
	protected void mbcpCarregaDiag(Integer crgSeq) throws BaseException {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		MbcDiagnosticoDescricaoDAO diagnosticoDescricaoDAO = getMbcDiagnosticoDescricaoDAO();
		List<MbcDiagnosticoDescricao> diagnosticos = diagnosticoDescricaoDAO.pesquisarDiagnosticoDescricaoPorDcgCrgSeqEClassificacao(crgSeq, DominioClassificacaoDiagnostico.POS);
		
		for(MbcDiagnosticoDescricao diagnostico : diagnosticos) {
			List<MamDiagnostico> diags = (diagnostico.getCid() != null) ? getAmbulatorioFacade().listarDiagnosticosPorPacienteCid(diagnostico.getCirurgia().getPaciente(), diagnostico.getCid()) : null;
			if(diags == null || diags.isEmpty()) {
				//INSERE MAM_DIAGNOSTICO
				MamDiagnostico novoDiagnostico = new MamDiagnostico();
				novoDiagnostico.setData(new Date());
				novoDiagnostico.setDthrCriacao(new Date());
				novoDiagnostico.setDthrValida(new Date());
				novoDiagnostico.setPaciente(diagnostico.getCirurgia().getPaciente());
				novoDiagnostico.setCid(diagnostico.getCid());
				novoDiagnostico.setIndSituacao(DominioSituacao.A);
				novoDiagnostico.setIndPendente(DominioIndPendenteDiagnosticos.V);
				novoDiagnostico.setServidor(servidorLogado);
				novoDiagnostico.setServidorValida(servidorLogado);
				novoDiagnostico.setComplemento(diagnostico.getComplemento());
				novoDiagnostico.setCirurgia(diagnostico.getCirurgia());
				getAmbulatorioFacade().persistirDiagnostico(novoDiagnostico);
			}
		}
	
		for(MamDiagnostico diagnostico : getAmbulatorioFacade().listarDiagnosticosPorCirurgia(crgSeq)) {
			if(diagnostico.getCid() == null || getMbcDiagnosticoDescricaoDAO().obterQuantidadeDiagnosticoDescricaoPorCirurgiaCidClassificacao(crgSeq, diagnostico.getCid().getSeq(), DominioClassificacaoDiagnostico.POS) == 0) {
				//UPDATE MAM_DIAGNOSTICO
				diagnostico.setDthrMvto(new Date());
				diagnostico.setServidorMovimento(servidorLogado);
				diagnostico.setServidorValidaMovimento(servidorLogado);
				diagnostico.setIndSituacao(DominioSituacao.I);
				diagnostico.setIndPendente(DominioIndPendenteDiagnosticos.C);
				getAmbulatorioFacade().atualizarDiagnostico(diagnostico, getAmbulatorioFacade().obterDiagnosticoOriginal(diagnostico.getSeq()));
			}
		}		
	}
	
	public boolean mbcpImprimeDescrCirurgica(final Boolean assinar) throws BaseException {
		return this.getICascaFacade()
				.usuarioTemPermissao(servidorLogadoFacade.obterServidorLogado().getUsuario(), REALIZAR_DESCRICAO_CIRURGICA);

	}
	
	/**
	 * ORADB: P_DESBLOQ_DOC_CERTIF
	 */
	public void desbloqDocCertificacao(final Integer crgSeq, final DominioTipoDocumento tipoDocumento) throws ApplicationBusinessException, ApplicationBusinessException{
		final ICertificacaoDigitalFacade certificacaoDigitalFacade = getCertificacaoDigitalFacade();
		
		// Integração com a Certificação Digital
		// Verifica se está habilitada para uso de certificação digital
		
		// v_habilita_certif := aghk_certif_digital.aghc_habilita_certif('MBCF_DESCR_CIRURGICA');
		final boolean vHabilitaCertif = certificacaoDigitalFacade.verificaAssituraDigitalHabilitada();
		
		// Verifica se o usuário tem permissão para assinatura digital de documentos AGH RN002
		
		if(vHabilitaCertif){ 
			
			// Certificação Digital
			// Verificar se existem documentos de assinatura digital do paciente para a descrição cirurgica.
			final List<AghVersaoDocumento> versoesDocumentos = certificacaoDigitalFacade.obterAghVersaoDocumentoPorCirurgia(crgSeq, tipoDocumento);
			
			for (AghVersaoDocumento aghVersaoDocumento : versoesDocumentos) {
				aghVersaoDocumento.setSituacao(DominioSituacaoVersaoDocumento.I);
				certificacaoDigitalFacade.atualizarAghVersaoDocumento(aghVersaoDocumento);
			}
		}
	}	
	
	protected DescricaoCirurgicaRN getDescricaoCirurgicaRN() {
		return descricaoCirurgicaRN;
	}
	
	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return this.iAmbulatorioFacade;
	}	
	
	protected ICertificacaoDigitalFacade getCertificacaoDigitalFacade() {
		return this.iCertificacaoDigitalFacade;
	}	
	
	protected IPacienteFacade getPacienteFacade() {
		return this.iPacienteFacade;
	}	
		
	protected MbcAnestesiaDescricoesDAO getMbcAnestesiaDescricoesDAO() {
		return mbcAnestesiaDescricoesDAO;
	}
	
	protected MbcDescricaoCirurgicaDAO getMbcDescricaoCirurgicaDAO() {
		return mbcDescricaoCirurgicaDAO;
	}
	
	protected MbcDiagnosticoDescricaoDAO getMbcDiagnosticoDescricaoDAO() {
		return mbcDiagnosticoDescricaoDAO;
	}	

	protected MbcDescricaoItensDAO getMbcDescricaoItensDAO() {
		return mbcDescricaoItensDAO;
	}
	
	protected ICascaFacade getICascaFacade() {
		return this.iCascaFacade;
	}	

}
