package br.gov.mec.aghu.paciente.prontuarioonline.business;


import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.internacao.vo.SumarioAdmissaoObstetricaInternacaoVO;
import br.gov.mec.aghu.internacao.vo.SumarioAdmissaoObstetricaInternacaoVO.ParametrosReportEnum;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * Utilizado pela estoria #15836
 * 
 * @author Guilherme Finotti Carvalho
 *
 */
@Stateless
public class SumarioAdmissaoObstetricaInternacaoON extends BaseBusiness {


@EJB
private SumarioAdmissaoObstetricaExameFisicoRN sumarioAdmissaoObstetricaExameFisicoRN;

@EJB
private SumarioAdmissaoObstetricaExamesRN sumarioAdmissaoObstetricaExamesRN;

@EJB
private SumarioAdmissaoObstetricaNotasAdRN sumarioAdmissaoObstetricaNotasAdRN;

@EJB
private SumarioAdmissaoObstetricaDiagPrincipalRN sumarioAdmissaoObstetricaDiagPrincipalRN;

@EJB
private SumarioAdmissaoObstetricaCondutaRN sumarioAdmissaoObstetricaCondutaRN;

@EJB
private SumarioAdmissaoObstetricaHistFamiliarRN sumarioAdmissaoObstetricaHistFamiliarRN;

@EJB
private SumarioAdmissaoObstetricaGestAtualRN sumarioAdmissaoObstetricaGestAtualRN;

@EJB
private SumarioAdmissaoObstetricaIdentificacaoRN sumarioAdmissaoObstetricaIdentificacaoRN;

@EJB
private SumarioAdmissaoObstetricaObservacaoRN sumarioAdmissaoObstetricaObservacaoRN;

private static final Log LOG = LogFactory.getLog(SumarioAdmissaoObstetricaInternacaoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


	private static final long serialVersionUID = -7054742869300754108L;

	/**
	 * Monta o relatorio da estoria #15836
	 * 
	 * @param serMatricula
	 * @param serVinCodigo
	 * @param atdSeq
	 * @param pacCodigo
	 * @param conNumero
	 * @param gsoSeqp
	 * @param dthrMovimento
	 * @return
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	@SuppressWarnings("unchecked")
	public SumarioAdmissaoObstetricaInternacaoVO montarRelatorio(Integer serMatricula, Short serVinCodigo,  Integer pacCodigo, Integer conNumero, Short gsoSeqp, Date dthrMovimento) throws ApplicationBusinessException {
		
		SumarioAdmissaoObstetricaInternacaoVO vo = new SumarioAdmissaoObstetricaInternacaoVO();
		
		// parametros recebidos
		vo.getParametrosHQL().put(ParametrosReportEnum.P_MATRICULA, serMatricula);
		vo.getParametrosHQL().put(ParametrosReportEnum.P_VIN_CODIGO, serVinCodigo);
		vo.getParametrosHQL().put(ParametrosReportEnum.P_PAC_CODIGO, pacCodigo);
		vo.getParametrosHQL().put(ParametrosReportEnum.P_CON_NUMERO, conNumero);
		vo.getParametrosHQL().put(ParametrosReportEnum.P_GSO_SEQP, gsoSeqp);
		vo.getParametrosHQL().put(ParametrosReportEnum.P_DTHR_MOVIMENTO, dthrMovimento);
		
		//preencher campo 82
		String strDataHoraObservacao = DateUtil.obterDataFormatada(dthrMovimento, "dd/MM/yyyy, HH:mm");
		if(StringUtils.isNotBlank(strDataHoraObservacao)) {
			strDataHoraObservacao = strDataHoraObservacao.concat(" h");
		}
		vo.setDataHoraObservacao(strDataHoraObservacao);
		
		preencherDadosIdentificacao(vo);
		preencherDadosGestacaoAtual(vo);
		preencherDadosHistoricoFamiliar(vo);
		preencherDadosExameFisico(vo);
		preencherDadosExamesRealizados(vo);
		preencherDadosConduta(vo);
		preencherDadosDiagnosticoPrincipalInternacao(vo);
		preencherDadosObservacao(vo);
		preencherDadosNotasAdicionais(vo);
 		
		return vo;
	}
	
	

	/**
	 * 1 - Identificacao
	 * @param vo
	 */
	private void preencherDadosIdentificacao(SumarioAdmissaoObstetricaInternacaoVO vo) {		
		getSumarioAdmissaoObstetricaIdentificacaoRN().executarQPac(vo);
		getSumarioAdmissaoObstetricaIdentificacaoRN().executarQAltura(vo);
		getSumarioAdmissaoObstetricaIdentificacaoRN().executarQPeso(vo);
		getSumarioAdmissaoObstetricaIdentificacaoRN().executarCFLeitoFormula(vo);
	}
	
	
	/**
	 * 3 - Gestacao Atual 
	 * @param vo
	 */
	private void preencherDadosGestacaoAtual(SumarioAdmissaoObstetricaInternacaoVO vo) {
		getSumarioAdmissaoObstetricaGestAtualRN().executarQGestacao(vo);
		getSumarioAdmissaoObstetricaGestAtualRN().executarCFJustificativaFormula(vo); 
		getSumarioAdmissaoObstetricaGestAtualRN().executarQPassadas(vo);
		getSumarioAdmissaoObstetricaGestAtualRN().executarQIng(vo);
	//	getSumarioAdmissaoObstetricaGestAtualRN().executarQOpa1(vo);
	}
	
	/**
	 * 4 - Gestacoes Anteriores / Antecedentes Familiares
	 * @param vo
	 */
	private void preencherDadosHistoricoFamiliar(SumarioAdmissaoObstetricaInternacaoVO vo) {
		getSumarioAdmissaoObstetricaHistFamiliarRN().executarQGpa(vo);
		getSumarioAdmissaoObstetricaHistFamiliarRN().executarQHif(vo);
	}
	
	/**
	 * 5 - Exame Fisico
	 * @param vo
	 */
	private void preencherDadosExameFisico(SumarioAdmissaoObstetricaInternacaoVO vo) {
		getSumarioAdmissaoObstetricaExameFisicoRN().executarCF1Formula(vo);
	}
	
	/**
	 * 6 - Exames Realizados
	 * @param vo
	 */
	private void preencherDadosExamesRealizados(SumarioAdmissaoObstetricaInternacaoVO vo) {
		getSumarioAdmissaoObstetricaExamesRN().executarQRxs(vo);
		getSumarioAdmissaoObstetricaExamesRN().executarQVex(vo);
	}
	
	/**
	 * 7 - Conduta
	 * @param vo
	 */
	private void preencherDadosConduta(SumarioAdmissaoObstetricaInternacaoVO vo) {
		getSumarioAdmissaoObstetricaCondutaRN().executarQPli(vo);
	}
	
	/**
	 * 8 - Diagnostico Principal da Internacao
	 * @param vo
	 */
	private void preencherDadosDiagnosticoPrincipalInternacao(SumarioAdmissaoObstetricaInternacaoVO vo) {
		getSumarioAdmissaoObstetricaDiagPrincipalRN().executarQCid(vo);
	}
	
	/**
	 * 9 - Observacao / Diagnosticos Secundarios
	 * @param vo
	 * @throws ApplicationBusinessException 
	 */
	private void preencherDadosObservacao(SumarioAdmissaoObstetricaInternacaoVO vo) throws ApplicationBusinessException {
		getSumarioAdmissaoObstetricaObservacaoRN().executarResponsavel(vo);
	}
	
	/**
	 * 10 - Notas Adicionais
	 * @param vo
	 * @throws ApplicationBusinessException  
	 */
	private void preencherDadosNotasAdicionais(SumarioAdmissaoObstetricaInternacaoVO vo) throws ApplicationBusinessException {
		getSumarioAdmissaoObstetricaNotasAdRN().executarQ1(vo);
	}
	
	/**
	 * Retorna RN responsavel por preencher os dados de identificacao e motivo consulta
	 * @return
	 */
	private SumarioAdmissaoObstetricaIdentificacaoRN getSumarioAdmissaoObstetricaIdentificacaoRN() {
		return sumarioAdmissaoObstetricaIdentificacaoRN;
	}
	
	/**
	 * Retorna RN responsavel por preencher os dados de gestacao atual
	 * @return
	 */
	private SumarioAdmissaoObstetricaGestAtualRN getSumarioAdmissaoObstetricaGestAtualRN() {
		return sumarioAdmissaoObstetricaGestAtualRN;
	}
	
	/**
	 * Retorna RN responsavel por preencher os dados de historico familiar
	 * @return
	 */
	private SumarioAdmissaoObstetricaHistFamiliarRN getSumarioAdmissaoObstetricaHistFamiliarRN() {
		return sumarioAdmissaoObstetricaHistFamiliarRN;
	}
	
	/**
	 * Retorna RN responsavel por preencher os dados de exame fisico
	 * @return
	 */
	private SumarioAdmissaoObstetricaExameFisicoRN getSumarioAdmissaoObstetricaExameFisicoRN() {
		return sumarioAdmissaoObstetricaExameFisicoRN;
	}
	
	/**
	 * Retorna RN responsavel por preencher os dados de exames realizados
	 * @return
	 */
	private SumarioAdmissaoObstetricaExamesRN getSumarioAdmissaoObstetricaExamesRN() {
		return sumarioAdmissaoObstetricaExamesRN;
	}
	
	/**
	 * Retorna RN responsavel por preencher os dados de conduta
	 * @return
	 */
	private SumarioAdmissaoObstetricaCondutaRN getSumarioAdmissaoObstetricaCondutaRN() {
		return sumarioAdmissaoObstetricaCondutaRN;
	}
	
	/**
	 * Retorna RN responsavel por preencher os dados de diagnostico principal internacao
	 * @return
	 */
	private SumarioAdmissaoObstetricaDiagPrincipalRN getSumarioAdmissaoObstetricaDiagPrincipalRN() {
		return sumarioAdmissaoObstetricaDiagPrincipalRN;
	}
	
	/**
	 * Retorna RN responsavel por preencher os dados de responsavel
	 * @return
	 */
	private SumarioAdmissaoObstetricaObservacaoRN getSumarioAdmissaoObstetricaObservacaoRN() {
		return sumarioAdmissaoObstetricaObservacaoRN;
	}
	
	/**
	 * Retorna RN responsavel por preencher os dados de notas adicionais
	 * @return
	 */
	private SumarioAdmissaoObstetricaNotasAdRN getSumarioAdmissaoObstetricaNotasAdRN() {
		return sumarioAdmissaoObstetricaNotasAdRN;
	}	
	
}
