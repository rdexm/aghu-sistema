package br.gov.mec.aghu.blococirurgico.business;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.dao.MbcDescricaoItensDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcedimentoCirurgicoDAO;
import br.gov.mec.aghu.blococirurgico.dao.PdtDadoDescDAO;
import br.gov.mec.aghu.blococirurgico.vo.MbcCirurgiasCursorCirVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioIndContaminacao;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioOrigemPacienteCirurgia;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.model.MbcDescricaoItens;
import br.gov.mec.aghu.model.MbcDescricaoItensId;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class DescricaoItensON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(DescricaoItensON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcProcedimentoCirurgicoDAO mbcProcedimentoCirurgicoDAO;

	@Inject
	private PdtDadoDescDAO pdtDadoDescDAO;

	@Inject
	private MbcDescricaoItensDAO mbcDescricaoItensDAO;


	@EJB
	private IPacienteFacade iPacienteFacade;

	@EJB
	private DescricaoItensRN descricaoItensRN;

	@EJB
	private IParametroFacade iParametroFacade;

	@EJB
	private IExamesFacade iExamesFacade;

	@EJB
	private IAghuFacade iAghuFacade;

	private static final long serialVersionUID = 4924241063366848841L;

	protected enum MbcCirurgiasONExceptionCode implements BusinessExceptionCode {
		MBC_01332, MBC_01333, MBC_00877, MBC_00877B, MBC_00878, MBC_00879,
		COLISAO_HORARIOS_CIRURGIA, COLISAO_HORARIOS_CIRURGIA_PROCED_TERAPEUTICO,
		MBC_01384, MBC_01385, MBC_01386;
	}

	/**
	 * ORADB: PROCEDURE EVT_WHEN_VALIDATE_ITEM2 
	 * Estória: #24894
	 * 
	 * @param dcgCrgSeq				Código da cirurgia
	 * @param dcgSeqp				Código da descrição do item
	 * @param lVdcDataCirurgia		l_vdc_data_cirurgia
	 * @param dtiDthrInicioCir		dti.dthr_inicio_cirg
	 * @param dtiDthrFimCirg		dti.dthr_fim_cirg
	 * @param dtHrEntradaSala		dcg.l_vdc_dthr_entrada_sala
	 * @param dtHrSaidaSala			dcg.l_vdc_dthr_saida_sala
	 * @param grcUnfSeq				dcg.l_vdc_crg_unf_seq
	 * @param sciSeqp				l_vdc_sci_seqp
	 * @param alterandoDtInicio		informa de qual campo partiu ação
	 * @param lPciTempoMinimo		MBC_PROCEDIMENTO_CIRURGICOS.TEMPO_MINIMO
	 * @param servidorLogado
	 */
	public boolean atualizarDatasDescricaoCirurgica(final Integer dcgCrgSeq, final Short dcgSeqp,
												 final Date lVdcDataCirurgia, final Date dtiDthrInicioCir, final Date dtiDthrFimCirg,  
							 					 final Date dtHrEntradaSala,  final Date dtHrSaidaSala,    final Short grcUnfSeq, 
							 					 final Short sciSeqp, final boolean alterandoDtInicio, 	   final Short lPciTempoMinimo) throws BaseException{
		
		boolean estourouTempoMimCirur = false;
		final MbcDescricaoItens mdi = getMbcDescricaoItensDAO().obterPorChavePrimaria(new MbcDescricaoItensId(dcgCrgSeq, dcgSeqp));
		
		
		
		/* TRECHO DE CÓDIGO QUE SÓ É EXECUTADO QUANDO:
		 * 
		 * (name_in('system.cursor_block') = 'DTI' AND  --pasta cirurgia realizada
	 	   name_in('system.trigger_item') = 'DTI.DTHR_INICIO_CIRG')
		 */
		
//		if(alterandoDtInicio){
			if(DateUtil.validaDataMenor(dtiDthrInicioCir, lVdcDataCirurgia)){
				// Data de início da cirurgia não pode ser menor que a data da cirurgia realizada.
				throw new ApplicationBusinessException(MbcCirurgiasONExceptionCode.MBC_00878);
			}
			
			final Integer vDiasLimite = getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_DIAS_LIM_INICIO_CIRG);
			final Date dataComp = DateUtil.adicionaDias(lVdcDataCirurgia, vDiasLimite);
			
			if(DateUtil.validaDataMaior( DateUtil.truncaData(dtiDthrInicioCir), DateUtil.truncaData(dataComp))){
				// Data de início da cirurgia não pode ser maior que 1 dia da data de agendamento
				throw new ApplicationBusinessException(MbcCirurgiasONExceptionCode.MBC_00879, vDiasLimite);
			}
			
			if(dtiDthrFimCirg != null && DateUtil.validaDataMaior(dtiDthrInicioCir, dtiDthrFimCirg)){
				// Data Inicial da cirurgia deve ser Menor que a Data Final.
				throw new ApplicationBusinessException(MbcCirurgiasONExceptionCode.MBC_00877B);
			}

			if(dtHrEntradaSala != null && 
					DateUtil.validaDataMaior(dtHrEntradaSala, dtiDthrInicioCir)){
				
				// Data Inicio da cirurgia deve ser Maior ou igual a Data Entrada na sala.
				throw new ApplicationBusinessException(MbcCirurgiasONExceptionCode.MBC_01332);
			}
				
			final IPacienteFacade pacienteFacade = getPacienteFacade();
			
			// Verifica colisão de horários com outra descrição cirurgica
			validaColisoesOutrasCirurgias(dcgCrgSeq, grcUnfSeq, lVdcDataCirurgia, sciSeqp, dtiDthrInicioCir, dtiDthrFimCirg, true, pacienteFacade);
				
			// Verifica colisão de horários com descrição de PDT
			validaColisoesProcedimentosTerapeutico(dcgCrgSeq, grcUnfSeq, lVdcDataCirurgia, sciSeqp, dtiDthrInicioCir, dtiDthrFimCirg, true, pacienteFacade);
//		}
		
		
		if(dtiDthrInicioCir != null && dtiDthrFimCirg != null){
			estourouTempoMimCirur = validarTempoMinimoCirurgia(dtiDthrInicioCir, dtiDthrFimCirg, lPciTempoMinimo);
		}
		
		if(!alterandoDtInicio){
			if(DateUtil.validaDataMenor(dtiDthrFimCirg, dtiDthrInicioCir)){
				// Data Final da cirurgia deve ser Maior que a Data Inicial.
				throw new ApplicationBusinessException(MbcCirurgiasONExceptionCode.MBC_00877);
			}
			
			if(dtHrSaidaSala != null){
				if(DateUtil.validaDataMenor(dtHrSaidaSala, dtiDthrFimCirg)){
					// Data fim da cirurgia deve ser Menor ou igual a Data saída da sala.
					throw new ApplicationBusinessException(MbcCirurgiasONExceptionCode.MBC_01333);
				}
			}

	//		final IPacienteFacade pacienteFacade = getPacienteFacade();
			
			// Verifica colisão de horários com outra descrição cirurgica - dthr fim
			validaColisoesOutrasCirurgias(dcgCrgSeq, grcUnfSeq, lVdcDataCirurgia, sciSeqp, dtiDthrInicioCir, dtiDthrFimCirg, false, pacienteFacade);

			
			// Verifica colisão de horários com descrição de PDT
			validaColisoesProcedimentosTerapeutico(dcgCrgSeq, grcUnfSeq, lVdcDataCirurgia, sciSeqp, dtiDthrInicioCir, dtiDthrFimCirg, false, pacienteFacade);
		}
		
		mdi.setDthrInicioCirg(dtiDthrInicioCir);
		mdi.setDthrFimCirg(dtiDthrFimCirg);
		
		getDescricaoItensRN().alterarMbcDescricaoItens(mdi);
		return estourouTempoMimCirur;
	}

	public boolean validarTempoMinimoCirurgia(final Date dtiDthrInicioCir, final Date dtiDthrFimCirg, final Short lPciTempoMinimo) {
		/*
		 * EXECUTA PARA AMBAS AS DATAS:
		 * 
		 * IF name_in('system.trigger_item')           = 'DTI.DTHR_FIM_CIRG' OR name_in('system.trigger_item') = 'DTI.DTHR_INICIO_CIRG' THEN
		 */
	
		if(lPciTempoMinimo != null){
			if(lPciTempoMinimo > 0){
				int escalaPrecisaoQuociente = 10;
				if(lPciTempoMinimo >= 100){
					// v_duracao :=TRUNC((((to_date(name_in('dti.dthr_fim_cirg')) - to_date(name_in('dti.dthr_inicio_cirg'))) * 24)* 100),0);
					int vDuracao = DateUtil.calcularDiasEntreDatasComPrecisao(
							dtiDthrInicioCir, dtiDthrFimCirg, escalaPrecisaoQuociente).multiply(new BigDecimal(24)).multiply(new BigDecimal(100)).intValue();
							
					//v_tempo_minimo := (substr(lpad(name_in('CIR.L_PCI_TEMPO_MINIMO'),4,0),1,2)*100) + (substr(lpad(name_in('CIR.L_PCI_TEMPO_MINIMO'),4,0),3,2))
					int vTempoMinimo = calculoTempoMinimo(lPciTempoMinimo.toString(), 100);
					
					// A duração desta cirurgia ultrapassa o dobro de tempo considerado mínimo para realização. Verifique!
					if (vDuracao > (vTempoMinimo * 2)){
						return true;
					}
					
				} else {
				
					// v_duracao :=TRUNC((((to_date(name_in('dti.dthr_fim_cirg')) - to_date(name_in('dti.dthr_inicio_cirg'))) * 24)* 60),0);
					int vDuracao = DateUtil.calcularDiasEntreDatasComPrecisao(
							dtiDthrInicioCir, dtiDthrFimCirg, escalaPrecisaoQuociente).multiply(new BigDecimal(24)).multiply(new BigDecimal(60)).intValue();
					
					// v_tempo_minimo := (SUBSTR(lpad(name_in('CIR.L_PCI_TEMPO_MINIMO'),4,0),1,2)*60) + (SUBSTR(lpad(name_in('CIR.L_PCI_TEMPO_MINIMO'),4,0),3,2));
					int vTempoMinimo = calculoTempoMinimo(lPciTempoMinimo.toString(), 60);
					
					// A duração desta cirurgia ultrapassa o dobro de tempo considerado mínimo para realização. Verifique!
					if (vDuracao > (vTempoMinimo * 2)){
						return true;
					}
				}
			}
		}
		
		return false;
	}

	private void validaColisoesOutrasCirurgias(final Integer crgSeq, final Short grcUnfSeq, final Date lVdcDataCirurgia, final Short sciSeqp,
			final Date dtiDthrInicioCir, final Date dtiDthrFimCirg, final boolean alterandoDtInicial, final IPacienteFacade pacienteFacade) throws ApplicationBusinessException {
		
		final List<MbcCirurgiasCursorCirVO> fetCir =  getMbcDescricaoItensDAO().pesquisarOutrasDescricoesCirurgicasPorSala( crgSeq, 	
																													lVdcDataCirurgia, 
																													grcUnfSeq, 
																													grcUnfSeq, 
																													sciSeqp);
		if(alterandoDtInicial){
			for (MbcCirurgiasCursorCirVO vo : fetCir) {
				if((DateUtil.validaDataMaiorIgual(dtiDthrInicioCir, vo.getDthrInicioCirg()) &&
						DateUtil.validaDataMenor(dtiDthrInicioCir, vo.getDthrFimCirg())) ||
						( DateUtil.validaDataMenor(dtiDthrInicioCir, vo.getDthrInicioCirg()) &&
						  DateUtil.validaDataMaior(dtiDthrFimCirg, vo.getDthrFimCirg())
						)
						
				){
					String nome = pacienteFacade.obterNomeDoPacientePorCodigo(vo.getPacCodigo());
	
					throw new ApplicationBusinessException( MbcCirurgiasONExceptionCode.COLISAO_HORARIOS_CIRURGIA, 
												    nome, 
												    vo.getSala(),
												    DateUtil.obterDataFormatada(vo.getDthrInicioCirg(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO) ,
												    DateUtil.obterDataFormatada(vo.getDthrFimCirg(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO) 
												    );
				}
			}
			
		} else {
			
			for (MbcCirurgiasCursorCirVO vo : fetCir) {
				if(DateUtil.validaDataMaior(dtiDthrFimCirg, vo.getDthrInicioCirg()) &&
						DateUtil.validaDataMenorIgual(dtiDthrFimCirg, vo.getDthrFimCirg())  
				){
					String nome = pacienteFacade.obterNomeDoPacientePorCodigo(vo.getPacCodigo());
	
					throw new ApplicationBusinessException( MbcCirurgiasONExceptionCode.COLISAO_HORARIOS_CIRURGIA, 
												    nome, 
												    vo.getSala(),
												    DateUtil.obterDataFormatada(vo.getDthrInicioCirg(),DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO) ,
												    DateUtil.obterDataFormatada(vo.getDthrFimCirg(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO) 
												    );
				}
				
				if(DateUtil.validaDataMenor(dtiDthrInicioCir, vo.getDthrInicioCirg()) &&
						DateUtil.validaDataMaior(dtiDthrFimCirg, vo.getDthrFimCirg())  
				){
					String nome = pacienteFacade.obterNomeDoPacientePorCodigo(vo.getPacCodigo());
					
					throw new ApplicationBusinessException( MbcCirurgiasONExceptionCode.COLISAO_HORARIOS_CIRURGIA, 
												    nome, 
												    vo.getSala(),
												    DateUtil.obterDataFormatada(vo.getDthrInicioCirg(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO) ,
												    DateUtil.obterDataFormatada(vo.getDthrFimCirg(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO) 
												    );
				} 
			}
		}
	}

	private void validaColisoesProcedimentosTerapeutico(final Integer crgSeq, final Short grcUnfSeq, final Date lVdcDataCirurgia, final Short sciSeqp,
			final Date dtiDthrInicioCir, final Date dtiDthrFimCirg, final boolean alterandoDtInicial, final IPacienteFacade pacienteFacade) throws ApplicationBusinessException {
		
		final List<MbcCirurgiasCursorCirVO> cPdt =  getPdtDadoDescDAO().pesquisarOutrasDescricoesPdtPorSala( crgSeq, 	
																												  lVdcDataCirurgia, 
																												  grcUnfSeq, grcUnfSeq,
																												  sciSeqp );
		if(alterandoDtInicial){
			for (MbcCirurgiasCursorCirVO vo : cPdt) {
				if(DateUtil.validaDataMaiorIgual(dtiDthrInicioCir, vo.getDthrInicioCirg()) &&
						DateUtil.validaDataMenor(dtiDthrInicioCir, vo.getDthrFimCirg()) ||
						( DateUtil.validaDataMenor(dtiDthrInicioCir, vo.getDthrInicioCirg()) &&
						  DateUtil.validaDataMaior(dtiDthrFimCirg, vo.getDthrFimCirg())
						)
						
				){
					String nome = pacienteFacade.obterNomeDoPacientePorCodigo(vo.getPacCodigo());
	
					throw new ApplicationBusinessException( MbcCirurgiasONExceptionCode.COLISAO_HORARIOS_CIRURGIA_PROCED_TERAPEUTICO, 
												    nome, 
												    vo.getSala(),
												    DateUtil.obterDataFormatada(vo.getDthrInicioCirg(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO) ,
												    DateUtil.obterDataFormatada(vo.getDthrFimCirg(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO) 
												    );
				}
			}
			
		} else {

			for (MbcCirurgiasCursorCirVO vo : cPdt) {
				if(DateUtil.validaDataMaior(dtiDthrFimCirg, vo.getDthrInicioCirg()) &&
						DateUtil.validaDataMenorIgual(dtiDthrFimCirg, vo.getDthrFimCirg()) 
						
				){
					String nome = pacienteFacade.obterNomeDoPacientePorCodigo(vo.getPacCodigo());
	
					throw new ApplicationBusinessException( MbcCirurgiasONExceptionCode.COLISAO_HORARIOS_CIRURGIA_PROCED_TERAPEUTICO, 
												    nome, 
												    vo.getSala(),
												    DateUtil.obterDataFormatada(vo.getDthrInicioCirg(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO) ,
												    DateUtil.obterDataFormatada(vo.getDthrFimCirg(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO) 
												    );
				}
	
				if(DateUtil.validaDataMenor(dtiDthrInicioCir, vo.getDthrInicioCirg()) &&
						DateUtil.validaDataMaior(dtiDthrFimCirg, vo.getDthrFimCirg()) 
						
				){
					String nome = pacienteFacade.obterNomeDoPacientePorCodigo(vo.getPacCodigo());
	
					throw new ApplicationBusinessException( MbcCirurgiasONExceptionCode.COLISAO_HORARIOS_CIRURGIA_PROCED_TERAPEUTICO, 
												    nome, 
												    vo.getSala(),
												    DateUtil.obterDataFormatada(vo.getDthrInicioCirg(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO) ,
												    DateUtil.obterDataFormatada(vo.getDthrFimCirg(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO) 
												    );
				}
			}
		}
	}
	
	public static int calculoTempoMinimo(String tempo, int multiplicador) {
		//   Select (Substr(Lpad(4859,4,0),1,2)*100) + (Substr(Lpad(4859,4,0),3,2)) from dual
		int val1 = Integer.parseInt(StringUtils.leftPad(tempo, 4, '0').substring(0,2))*multiplicador;
		int val2 = Integer.parseInt(StringUtils.leftPad(tempo, 4, '0').substring(2));
		
		return (val1+val2);
	}

	/**
	 * ORADB: C_ATD_PAC ESTÓRIA #24894
	 * @param pacCodigo 
	 */
	public Integer obterSeqAghAtendimentoPorPaciente(final Integer pacCodigo){
		final DominioOrigemAtendimento[] origens = {DominioOrigemAtendimento.I, DominioOrigemAtendimento.U};
		
		return getAghuFacade().obterSeqAghAtendimentoPorPaciente(pacCodigo, origens, DominioPacAtendimento.S);
	}
	
	/**
	 * ORADB: PROCEDURE P_BUSCA_MATERIAL_EXAME ESTÓRIA: #24894
	 */
	public String buscaDescricaoMaterialExame(final DominioOrigemPacienteCirurgia origemPaciente, final Integer pacCodigo, final Integer atdSeq){
		
		final Integer vAtdSeq = atdSeq != null ? atdSeq : obterSeqAghAtendimentoPorPaciente(pacCodigo);
		
		if(vAtdSeq != null){
			final List<String> descricaoMaterialExame = getExamesFacade().obterDescricaoMaterialAnalise(vAtdSeq);
			
			final StringBuilder result = new StringBuilder();
			for (String dsMat : descricaoMaterialExame) {
				result.append(dsMat).append(", ");
			}
			
			if(result.length() > 0){
				result.delete(result.length() - 2, result.length() );
				return result.toString();
			}
		}
		
		return null;
	}
	
	/**
	 * ORADB: FUNCTION C_HABILITA_ENC_EXAME ESTÓRIA: #24894
	 */
	public boolean habilitaEncaminhamentoExame(final DominioOrigemPacienteCirurgia origemPaciente, final Integer pacCodigo, final Integer atdSeq){
		
		if(DominioOrigemPacienteCirurgia.I.equals(origemPaciente)){
			final Integer vAtdSeq = atdSeq != null ? atdSeq : obterSeqAghAtendimentoPorPaciente(pacCodigo);
			
			if(vAtdSeq == null){
				final List<Integer> pacs = getPacienteFacade().obtemCodPacienteComInternacaoNaoNulaEOutrasDatas(pacCodigo);
				
				if(pacs == null || pacs.isEmpty()){
//						copy('N', 'DCG.IND_ENC_MAT_EXAME');
//						SET_ITEM_PROPERTY ('DCG.IND_ENC_MAT_EXAME', ENABLED, PROPERTY_FALSE);
//						SET_ITEM_PROPERTY ('DCG.IND_ENC_MAT_EXAME', NAVIGABLE, PROPERTY_FALSE);

					return false;
				}
			}
		}
		
		return true;
	}
	
	/**
	 * Procedure
	 * 
	 * ORADB: EVT_WHEN_LIST_CHANGED (form MBCF_DESCR_CIRURGICA) 
	 * 
	 * @param pciSeq
	 * @param novoIndContaminacao
	 * @throws ApplicationBusinessException
	 */
	public void validarContaminacaoProcedimentoCirurgico(Integer pciSeq, DominioIndContaminacao novoIndContaminacao) throws ApplicationBusinessException {
		
		MbcProcedimentoCirurgicos procedimentoCirurgico = getMbcProcedimentoCirurgicoDAO().obterPorChavePrimaria(pciSeq);
		DominioIndContaminacao indContaminacao = procedimentoCirurgico.getIndContaminacao();
		
		if (DominioIndContaminacao.P.equals(indContaminacao)
				&& DominioIndContaminacao.L.equals(novoIndContaminacao)) {
			/* Procedimento cadastrado com contaminação Procedimento Contaminada, 
			só pode ser alterado para Contaminada ou Infectada.*/
			throw new ApplicationBusinessException(MbcCirurgiasONExceptionCode.MBC_01384);

		} else if (DominioIndContaminacao.C.equals(indContaminacao)
				&& (DominioIndContaminacao.L.equals(novoIndContaminacao) || DominioIndContaminacao.P
						.equals(novoIndContaminacao))) {
			/* Procedimento cadastrado com contaminação  Contaminada,
		    só pode ser alterado para Infectado.*/			
			throw new ApplicationBusinessException(MbcCirurgiasONExceptionCode.MBC_01385);

		} else if (DominioIndContaminacao.I.equals(indContaminacao)
				&& !DominioIndContaminacao.I.equals(novoIndContaminacao)) {
			/* Procedimento cadastrado com contaminação Infectada, não pode ser alterado. */	
			throw new ApplicationBusinessException(MbcCirurgiasONExceptionCode.MBC_01386);
		}
	}
	
	protected DescricaoItensRN getDescricaoItensRN() {
		return descricaoItensRN;
	}
	
	protected MbcDescricaoItensDAO getMbcDescricaoItensDAO() {
		return mbcDescricaoItensDAO;
	}
	
	protected MbcProcedimentoCirurgicoDAO getMbcProcedimentoCirurgicoDAO() {
		return mbcProcedimentoCirurgicoDAO;
	}
	
	protected PdtDadoDescDAO getPdtDadoDescDAO() {
		return pdtDadoDescDAO;
	}
	
	protected IPacienteFacade getPacienteFacade() {
		return iPacienteFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return iParametroFacade;
	}
	
	protected IAghuFacade getAghuFacade() {
		return iAghuFacade;
	}
	
	protected IExamesFacade getExamesFacade() {
		return iExamesFacade;
	}
}