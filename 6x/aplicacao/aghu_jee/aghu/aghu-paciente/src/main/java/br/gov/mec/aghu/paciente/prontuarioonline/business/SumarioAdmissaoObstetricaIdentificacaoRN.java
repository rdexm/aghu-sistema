package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.internacao.vo.SumarioAdmissaoObstetricaInternacaoVO;
import br.gov.mec.aghu.internacao.vo.SumarioAdmissaoObstetricaInternacaoVO.ParametrosReportEnum;
import br.gov.mec.aghu.model.AipAlturaPacientes;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.AipPesoPacientes;
import br.gov.mec.aghu.model.McoAnamneseEfs;
import br.gov.mec.aghu.model.McoBolsaRotas;
import br.gov.mec.aghu.model.McoGestacoes;
import br.gov.mec.aghu.model.McoGestacoesId;
import br.gov.mec.aghu.paciente.dao.AipAlturaPacientesDAO;
import br.gov.mec.aghu.paciente.dao.AipPacientesDAO;
import br.gov.mec.aghu.paciente.dao.AipPesoPacientesDAO;
import br.gov.mec.aghu.perinatologia.business.IPerinatologiaFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.commons.NumberUtil;
import br.gov.mec.aghu.core.utils.AghuNumberFormat;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class SumarioAdmissaoObstetricaIdentificacaoRN extends BaseBusiness {


@EJB
private SumarioAtendimentoRecemNascidoRN sumarioAtendimentoRecemNascidoRN;

@EJB
private SumarioAdmissaoObstetricaExameFisicoRN sumarioAdmissaoObstetricaExameFisicoRN;

//@EJB
//private ConsultaDescricaoCirurgicaPolRN consultaDescricaoCirurgicaPolRN;

@EJB
private SumarioAdmissaoObstetricaGestAtualRN sumarioAdmissaoObstetricaGestAtualRN;

private static final Log LOG = LogFactory.getLog(SumarioAdmissaoObstetricaIdentificacaoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AipPesoPacientesDAO aipPesoPacientesDAO;

@Inject
private AipPacientesDAO aipPacientesDAO;

@EJB
private IAmbulatorioFacade ambulatorioFacade;

@EJB
private IPerinatologiaFacade perinatologiaFacade;

@Inject
private AipAlturaPacientesDAO aipAlturaPacientesDAO;

	private static final long serialVersionUID = -3870579087457060905L;


	/**
	 * Acesso ao modulo perinatologia
	 * @return
	 */
	private IPerinatologiaFacade getPerinatologiaFacade() {
		return perinatologiaFacade;
	}
	
	/**
	 * Acesso ao modulo ambulatorio
	 * @return
	 */
	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return ambulatorioFacade;
	}
	
	/**
	 * 
	 * @return
	 */
	private AipAlturaPacientesDAO getAipAlturaPacientesDAO() {
		return aipAlturaPacientesDAO;
	}
	
	/**
	 * 
	 * @return
	 */
	private AipPesoPacientesDAO getAipPesoPacientesDAO() {
		return aipPesoPacientesDAO;
	}		
	
	/**
	 * 
	 * @return
	 */
	private AipPacientesDAO getAipPacientesDAO() {
		return aipPacientesDAO;
	}
	
	/**
	 * 
	 * @return
	 */
	/*private ConsultaDescricaoCirurgicaPolRN getConsultaDescricaoCirurgicaPolRN() {
		return consultaDescricaoCirurgicaPolRN;
	}*/
	
	/**
	 * 
	 * @return
	 */
	private SumarioAtendimentoRecemNascidoRN getSumarioAtendimentoRecemNascidoRN() {
		return sumarioAtendimentoRecemNascidoRN;
	}
	
	/**
	 * 
	 * @return
	 */
	private SumarioAdmissaoObstetricaGestAtualRN getSumarioAdmissaoObstetricaGestAtualRN() {
		return sumarioAdmissaoObstetricaGestAtualRN;
	}
	
	/**
	 * 
	 * @return
	 */
	private SumarioAdmissaoObstetricaExameFisicoRN getSumarioAdmissaoObstetricaExameFisicoRN() {
		return sumarioAdmissaoObstetricaExameFisicoRN;
	}
	
	/**
	 * Q_PAC
	 * @param vo
	 */
	@SuppressWarnings("unchecked")
	public void executarQPac(SumarioAdmissaoObstetricaInternacaoVO vo) {
		
		// obtem gestacao
		Integer pacCodigo = (Integer) vo.getParametrosHQL().get(ParametrosReportEnum.P_PAC_CODIGO);
		Short seqp = (Short) vo.getParametrosHQL().get(ParametrosReportEnum.P_GSO_SEQP);
		Integer numeroConsulta = (Integer) vo.getParametrosHQL().get(ParametrosReportEnum.P_CON_NUMERO);

		McoGestacoesId gestacaoId = new McoGestacoesId();
		gestacaoId.setPacCodigo(pacCodigo);
		gestacaoId.setSeqp(seqp);
		McoGestacoes gestacao = getPerinatologiaFacade().obterMcoGestacoes(gestacaoId);
		McoAnamneseEfs anamnese = null;
		McoBolsaRotas bolsaRota = null;
		
		if(gestacao != null) {
			
			AipPacientes paciente =  getAipPacientesDAO().obterPorChavePrimaria(pacCodigo);
			anamnese = getPerinatologiaFacade().obterAnamnesePorGestacaoEConsulta(pacCodigo, seqp, numeroConsulta);
			bolsaRota = gestacao.getBolsaRota();
			
			if(paciente != null) {
				vo.setNome(paciente.getNome()); // 1 e 88
				vo.setProntuario(CoreUtil.formataProntuario(paciente.getProntuario().toString())); // 2 e 90
				vo.setIdade(obterIdadePaciente(anamnese)); // 3
				vo.getParametrosHQL().put(ParametrosReportEnum.QPAC_PAC_CODIGO, paciente.getCodigo());
				vo.getParametrosHQL().put(ParametrosReportEnum.QPAC_DT_ULT_ALTA, paciente.getDtUltAlta());
				vo.getParametrosHQL().put(ParametrosReportEnum.QPAC_DT_ULT_INTERNACAO, paciente.getDtUltInternacao());
				
			}
			
			preencherDadosAnamnese(anamnese, vo);
			preencherDadosBolsaRota(bolsaRota, vo);
			
			vo.setConvenio(getSumarioAtendimentoRecemNascidoRN().obterDescricaoConvenio(pacCodigo, numeroConsulta)); // 7			
			vo.setJustificativa(getSumarioAdmissaoObstetricaGestAtualRN().executarCFJustificativaFormula(vo)); // 22
			vo.setAcvAr(getSumarioAdmissaoObstetricaExameFisicoRN().executarCF1Formula(vo)); //69
			vo.setExFisGeral(obterExameFisicoGeral(anamnese)); // 70
			if(executarCFLeitoFormula(vo)) {
				vo.setLeito(paciente.getLtoLtoId()); // 89
			}
		}
	}


	/**
	 * Q_ALTURA
	 * @param vo
	 */
	public void executarQAltura(SumarioAdmissaoObstetricaInternacaoVO vo) {
		Integer numeroConsulta = (Integer) vo.getParametrosHQL().get(ParametrosReportEnum.P_CON_NUMERO);
		AipAlturaPacientes alturaPaciente = getAipAlturaPacientesDAO().obterAlturaPorNumeroConsulta(numeroConsulta);
		if(alturaPaciente != null && alturaPaciente.getAltura() != null && alturaPaciente.getAltura().compareTo(BigDecimal.ZERO) > 0){
			StringBuilder sb = new StringBuilder();
			sb.append(AghuNumberFormat.formatarNumeroMoeda(alturaPaciente.getAltura().divide(new BigDecimal(100),2,BigDecimal.ROUND_CEILING).doubleValue())).append(" m");
			vo.setAltura(sb.toString());
		}
	}
	
	/**
	 * Q_PESO
	 * @param vo
	 */
	public void executarQPeso(SumarioAdmissaoObstetricaInternacaoVO vo) {
		Integer numeroConsulta = (Integer) vo.getParametrosHQL().get(ParametrosReportEnum.P_CON_NUMERO);
		AipPesoPacientes pesoPaciente = getAipPesoPacientesDAO().obterPesoPacientesPorNumeroConsulta(numeroConsulta);
		if(pesoPaciente != null && pesoPaciente.getPeso() != null){
			StringBuilder sb = new StringBuilder();
			sb.append(NumberUtil.truncate(pesoPaciente.getPeso().doubleValue(), 1)).append(" kg");
			vo.setPesoMae(sb.toString().replace(".", ","));
		}
	}
	
	
	/**
	 * CF_EX_FIS_GERALFormula
	 * @param anamnese
	 * @return
	 */
	private String obterExameFisicoGeral(McoAnamneseEfs anamnese) {
		if(anamnese != null) {
			StringBuilder exameFisicoGeral = new StringBuilder();
			if(StringUtils.isNotBlank(anamnese.getExFisicoGeral())) {
				exameFisicoGeral.append("Ex Fís. Geral:  ");
				exameFisicoGeral.append(anamnese.getExFisicoGeral());
				return exameFisicoGeral.toString();
			}
		}
		return null;
	}
	
	/**
	 * CF_LEITO_FORMULA
	 * @param vo
	 */
	public boolean executarCFLeitoFormula(SumarioAdmissaoObstetricaInternacaoVO vo) {
		Date dtUltAlta = (Date) vo.getParametrosHQL().get(ParametrosReportEnum.QPAC_DT_ULT_ALTA);
		Date dtUltInternacao = (Date) vo.getParametrosHQL().get(ParametrosReportEnum.QPAC_DT_ULT_INTERNACAO);
		if(dtUltAlta == null || dtUltAlta.before(dtUltInternacao)) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	
	/**
	 * Preenche o campo 3 do relatorio
	 * @param anamnese
	 * @return
	 */
	private String obterIdadePaciente(McoAnamneseEfs anamnese) {
		String idade = "";
		if(anamnese != null && anamnese.getPaciente() != null) {
			idade = getAmbulatorioFacade().obterIdadeMesDias(anamnese.getPaciente().getDtNascimento(), anamnese.getDthrConsulta());
		}
		if(StringUtils.isNotBlank(idade)) {
			idade = idade.replace("0 mês", "");
		}
		return idade;
	}

	
	/**
	 * Retorna pressao arterial formatada
	 * @param sistolica
	 * @param diastolica
	 * @return
	 */
	private String obterPressaoArterial(Short sistolica, Short diastolica) {
		String retorno = null;
		if (sistolica != null) {
			StringBuilder sb = new StringBuilder();
			sb.append(sistolica).append('/').append(diastolica).append(" mmHg");
			retorno = sb.toString();
		}
		return retorno;
	}
	
	
	/**
	 * Retorna pressao arterial em repouso formatada
	 * @param sistolica
	 * @param diastolica
	 * @return
	 */
	private String obterPressaoArterialRepouso(Short sistolica, Short diastolica) {
		String retorno = null;
		if (sistolica != null) {
			StringBuilder sb = new StringBuilder();
			sb.append(sistolica).append('/').append(diastolica).append(" mmHg");
			retorno = sb.toString();
		}
		return retorno;
	}
	
	
	/**
	 * Preenche os dados relacionados a anamnese (QPAC)
	 * )
	 * @param anamnese
	 * @param vo
	 */
	@SuppressWarnings("unchecked")
	private void preencherDadosAnamnese(McoAnamneseEfs anamnese, SumarioAdmissaoObstetricaInternacaoVO vo) {
		
		if(anamnese != null) {
			
			vo.setDataAtendimento(anamnese.getDthrConsulta()); // 5
			vo.setMotivoConsulta(anamnese.getMotivo()); // 8
			
			vo.setPa(obterPressaoArterial(anamnese.getPressaoArtSistolica(), anamnese.getPressaoArtDiastolica())); // 44
			
			vo.setPaRepouso(obterPressaoArterialRepouso(anamnese.getPressaoSistRepouso(), anamnese.getPressaoDiastRepouso())); // 45
			
			validaPAFreqTemp(anamnese, vo);
			
			//tratamento campo 50
			vo.setDinamUterina(anamnese.getDinamicaUterina());
			
			if(anamnese.getIntensidadeDinUterina() != null) {
				vo.setDinamUterina(vo.getDinamUterina().concat(" ").concat(anamnese.getIntensidadeDinUterina().getDescricao()));
			}
			
			if(anamnese.getFreqRespiratoria() != null) {
				vo.setFreqResp(anamnese.getFreqRespiratoria().toString().concat(" mpm")); // 51
			}
			
			if(anamnese.getBatimentoCardiacoFetal2() != null) {
				vo.setBcf2(anamnese.getBatimentoCardiacoFetal().toString().concat(" bpm")); // 52
			}
			
			if(anamnese.getEdema() != null) {
				vo.setEdema(anamnese.getEdema().getDescricao()); // 53
			}
			if(anamnese.getSitFetal() != null) {
				vo.setSitFetal(anamnese.getSitFetal().getDescricao()); // 54		
			}
			
			vo.setExameEspecular(anamnese.getExameEspecular()); // 55

			if(DominioSimNao.S.equals(anamnese.getIndAcelTrans())) {
				vo.setAcelTrans("Acel. transitória presente"); // 56
			}
			if(DominioSimNao.S.equals(anamnese.getIndMovFetal())) {
				vo.setMovFetal("Movim. fetal presente"); // 57
			}
			if(anamnese.getEspessuraCervice() != null) {
				vo.setCervice(anamnese.getEspessuraCervice().getDescricao().concat("/").concat(anamnese.getPosicaoCervice().getDescricao())); // 58 e 59
			}
			vo.setApag(anamnese.getApagamento()); // 60
			vo.setDilatacao(anamnese.getDilatacao() ); // 61
			
			if(anamnese.getApresentacao() != null) {
				vo.setApres(anamnese.getApresentacao().getDescricao()); // 62
			}
			
			preencherPlanoDelee(anamnese, vo);
			
			vo.setObservacaoDiagnostico(anamnese.getObservacao()); // 81
		
			vo.getParametrosHQL().put(ParametrosReportEnum.QPAC_BA, anamnese.getConsulta().getNumero());
			vo.getParametrosHQL().put(ParametrosReportEnum.QPAC_EFI_CID_SEQ, anamnese.getCidSeq());	
			
			preencherDadosCF1Formula(anamnese, vo);
		}
	}

	private void preencherPlanoDelee(McoAnamneseEfs anamnese, SumarioAdmissaoObstetricaInternacaoVO vo) {
		if(anamnese.getPlanoDelee() != null){
			vo.setPlDeLee(Byte.valueOf(anamnese.getPlanoDelee().toString())); // 63
		}
	}
	
	/**
	 * Preenche os dados relacionados a CF_1_FORMULA
	 * @param anamnese
	 * @param vo
	 */
	@SuppressWarnings("unchecked")
	private void preencherDadosCF1Formula(McoAnamneseEfs anamnese, SumarioAdmissaoObstetricaInternacaoVO vo){
		if (anamnese.getAcv() != null){
			vo.getParametrosHQL().put(ParametrosReportEnum.QPAC_ACV, CoreUtil.concatenarIsNotEmpty(anamnese.getAcv(), "ACV : ", null));
		}
		
		if (anamnese.getAr() != null){
			vo.getParametrosHQL().put(ParametrosReportEnum.QPAC_AR, CoreUtil.concatenarIsNotEmpty(anamnese.getAr(), "AR : ", null));
		}
	}
	
	/**
	 * Preenche os dados relacionados a bolsa rotas (QPAC)
	 * 
	 * @param bolsaRota
	 * @param vo
	 */
	private void preencherDadosBolsaRota(McoBolsaRotas bolsaRota, SumarioAdmissaoObstetricaInternacaoVO vo) {
		if(bolsaRota != null) {
			vo.setFormaRuptura(bolsaRota.getFormaRuptura()); // 64
			if(bolsaRota.getDthrRompimento() != null) {
				vo.setDthrRompimento(DateUtil.dataToString(bolsaRota.getDthrRompimento(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO)); // 65
			}
			if(bolsaRota.getLiquidoAmniotico() != null) {
				vo.setLiqAminiotico(bolsaRota.getLiquidoAmniotico().getDescricao()); // 66
			}
			if(DominioSimNao.S.equals(bolsaRota.getIndOdorFetido())) {
				vo.setOdor("odor fétido"); // 67
			}
			if(DominioSimNao.S.equals(bolsaRota.getIndAmnioscopia())) {
				vo.setOdor("Amnioscopia"); // 68
			}
		}		
	}
	
	// PMD :(
	private void validaPAFreqTemp(McoAnamneseEfs anamnese, SumarioAdmissaoObstetricaInternacaoVO vo) {
		
		if(anamnese.getFreqCardiaca() != null) {
			vo.setFreqCardiaca(anamnese.getFreqCardiaca().toString()); // 46
		}
		if(anamnese.getBatimentoCardiacoFetal() != null) {
			vo.setBcf(anamnese.getBatimentoCardiacoFetal().toString()); // 47
		}
		if(anamnese.getTemperatura() != null) {
			vo.setTemp(anamnese.getTemperatura().toString()); // 48
		}
		if(anamnese.getAlturaUterina() != null) {
			vo.setAltUterina(anamnese.getAlturaUterina().toString()); // 49
		}
	}
	
}
