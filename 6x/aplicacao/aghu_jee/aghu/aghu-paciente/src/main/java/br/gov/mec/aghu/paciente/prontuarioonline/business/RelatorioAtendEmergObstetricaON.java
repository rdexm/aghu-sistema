/**
 * 
 */
package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioEventoNotaAdicional;
import br.gov.mec.aghu.dominio.DominioOcorrenciaIntercorrenciaGestacao;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AipAlturaPacientes;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.AipPesoPacientes;
import br.gov.mec.aghu.model.AipRegSanguineos;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.McoAnamneseEfs;
import br.gov.mec.aghu.model.McoBolsaRotas;
import br.gov.mec.aghu.model.McoConduta;
import br.gov.mec.aghu.model.McoGestacoes;
import br.gov.mec.aghu.model.McoGestacoesId;
import br.gov.mec.aghu.model.McoIntercorPasatus;
import br.gov.mec.aghu.model.McoIntercorrenciaGestacoes;
import br.gov.mec.aghu.model.McoNotaAdicional;
import br.gov.mec.aghu.model.McoPlanoIniciais;
import br.gov.mec.aghu.paciente.dao.AipAlturaPacientesDAO;
import br.gov.mec.aghu.paciente.dao.AipPacientesDAO;
import br.gov.mec.aghu.paciente.dao.AipPesoPacientesDAO;
import br.gov.mec.aghu.paciente.dao.AipRegSanguineosDAO;
import br.gov.mec.aghu.paciente.vo.RelatorioAtendEmergObstetricaCondutaVO;
import br.gov.mec.aghu.paciente.vo.RelatorioAtendEmergObstetricaIntercorrenciasVO;
import br.gov.mec.aghu.paciente.vo.RelatorioAtendEmergObstetricaVO;
import br.gov.mec.aghu.perinatologia.business.IPerinatologiaFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.Dominio;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.AghuNumberFormat;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.DateValidator;

/**
 * @author aghu
 *
 */
@Stateless
public class RelatorioAtendEmergObstetricaON extends BaseBusiness {


@EJB
private SumarioAtendimentoRecemNascidoRN sumarioAtendimentoRecemNascidoRN;

@EJB
private RelExameFisicoRecemNascidoPOLON relExameFisicoRecemNascidoPOLON;

private static final Log LOG = LogFactory.getLog(RelatorioAtendEmergObstetricaON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AipPesoPacientesDAO aipPesoPacientesDAO;

@Inject
private AipPacientesDAO aipPacientesDAO;

@Inject
private AipRegSanguineosDAO aipRegSanguineosDAO;

@EJB
private IAmbulatorioFacade ambulatorioFacade;

@EJB
private IPerinatologiaFacade perinatologiaFacade;

@EJB
private IAghuFacade aghuFacade;

@Inject
private AipAlturaPacientesDAO aipAlturaPacientesDAO;

	private static final long serialVersionUID = 1549933814821430909L;
	private final static String DATE_PATTERN_DDMMYYYY = "dd/MM/yyyy";
	
	/**
	 * Monta o relatório da estória #17321
	 * @author daniel.silva
	 * @since 24/08/2012
	 * @param dtHrMovimento
	 * @param matricula
	 * @param vinculo
	 * @param gsoPacCodigo
	 * @param gsoSeqp
	 * @param conNumero
	 * @return
	 * @throws ApplicationBusinessException  
	 */
	public RelatorioAtendEmergObstetricaVO montarRelatorio(Date dthrMovimento, Integer matricula, Short vinculo, Integer gsoPacCodigo, Short gsoSeqp, Integer conNumero) throws ApplicationBusinessException {
		RelatorioAtendEmergObstetricaVO vo = new RelatorioAtendEmergObstetricaVO();
		
		//parametros para geração dos dados
		vo.setPacCodigo(gsoPacCodigo);
		vo.setGsoSeqp(gsoSeqp);
		vo.setConNumero(conNumero);
		
		//Inicia a alimentação do vo.
		vo.setDthrMovimento(obterDataHoraMovimento(dthrMovimento));
		vo.setResponsavel(obterResponsavel(matricula, vinculo));
		obterDadosPaciente(vo);				//QPAC
		obterDadosAltura(vo);				//QALTURA
		obterDadosPesoPaciente(vo);			//QPESO
		obterDadosGestacao(vo);				//QGESTACAO
		obterIntercorrenciasGestacoes(vo);  //QING e QOPA
		obterDadosCondutas(vo);				//QPLI e QCDT
		obterDadosDiagnostico(vo);			//QCID
		obterDadosNotasAdicionais(vo);		//Q1
		
		return vo;
	}
	
	/**
	 * Retorna o Responsavel 
	 * @param matricula
	 * @param vinculo
	 * @return
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	private String obterResponsavel(Integer matricula, Short vinculo) throws ApplicationBusinessException {
		if(matricula != null && vinculo != null){
			String sb = getRelExameFisicoRecemNascidoPOLON().formataNomeProf(matricula, vinculo);
			if (sb != null){
				return sb;
			}
			return (getSumarioAtendimentoRecemNascidoRN().obterNomeProfissional(matricula, vinculo));
		}
		return null;
	}

	/**
	 * Retorna a dthrMovimento
	 * @param dthrMovimento
	 * @return
	 */
	private String obterDataHoraMovimento(Date dthrMovimento) {
		String dthr = DateUtil.dataToString(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO);
		if (dthrMovimento != null) {
			dthr = DateUtil.dataToString(dthrMovimento, DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO);
		}
		return dthr.replace(" ", ", ").concat(" h.");
	}

	/**
	 * Estória #17321 - Busca e preenche os seguintes campos do relatório (Q_PAC)
	 * 
	 * @author daniel.silva
	 * @since  24/08/12
	 */
	private void obterDadosPaciente(RelatorioAtendEmergObstetricaVO vo) {		 
		AipPacientes pac = getAipPacientesDAO().obterPorChavePrimaria(vo.getPacCodigo());
		if (pac != null) {
			preencherDadosPaciente(vo, pac);
		}
		McoAnamneseEfs efi = getPerinatologiaFacade().obterAnamnesePorPacienteSequenceGestacaoConsulta(vo.getPacCodigo(), vo.getGsoSeqp(), vo.getConNumero());
		if (efi != null) {
			preencherDadosAnamneses(vo, efi);
		}
		AacConsultas con = getAmbulatorioFacade().obterConsulta(vo.getConNumero());
		if (con != null && con.getConvenioSaudePlano() != null && con.getConvenioSaudePlano().getConvenioSaude() != null) {
			preencherDadosConvenio(vo, con.getConvenioSaudePlano().getConvenioSaude());
		}
		McoBolsaRotas bsr = getPerinatologiaFacade().obterMcoBolsaRotasProId(new McoGestacoesId(vo.getPacCodigo(), vo.getGsoSeqp()));
		if (bsr != null) {
			preencherDadosBolsaRoda(vo, bsr);
		}
	}
	
	/**
	 * Alimenta a vo com os dados de diagnosticos (QCID)
	 * @param vo
	 * @author daniel.silva
	 * @since  27/08/12
	 */
	private void obterDadosDiagnostico(RelatorioAtendEmergObstetricaVO vo) {
		AghCid aghCid = getAghuFacade().obterCid(vo.getEfiCidSeq());
		if (aghCid != null) {
			vo.setCidCodigo(obterConcatenarIsNotEmpty(aghCid.getCodigo(), "(", ")"));
			vo.setCidDescricao(aghCid.getDescricao());
		}
	}
	
	/**
	 * Alimenta a vo com os dados de notas adicionais (Q1)
	 * @param vo
	 * @author daniel.silva
	 * @throws ApplicationBusinessException  
	 * @since  27/08/12
	 */
	private void obterDadosNotasAdicionais(RelatorioAtendEmergObstetricaVO vo) throws ApplicationBusinessException {
		McoNotaAdicional notaAdicional = getPerinatologiaFacade().obterMcoNotaAdicional(vo.getPacCodigo(), vo.getGsoSeqp(), null, vo.getConNumero(), DominioEventoNotaAdicional.MCOR_CONSULTA_OBS);
		if (notaAdicional != null) {
			vo.setNadCriadoEm(DateUtil.dataToString(notaAdicional.getCriadoEm(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO).replace(" ", ", ").concat(" h"));
			vo.setNadNotaAdicional(notaAdicional.getNotaAdicional());
			vo.setNadNomeProf(getRelExameFisicoRecemNascidoPOLON().formataNomeProf(notaAdicional.getSerMatricula(), notaAdicional.getSerVinCodigo()));
		}
	}
	
	private RelExameFisicoRecemNascidoPOLON getRelExameFisicoRecemNascidoPOLON() {
		return relExameFisicoRecemNascidoPOLON;
	}
	
	/**
	 * alimenta a vo com uma listagem de RelatorioAtendEmergObstetricaCondutaVO
	 * Essa listagem será alimentada por (QPLI) e (QCDT)
	 * @param vo
	 * @author daniel.silva
	 * @since  27/08/12
	 */
	private void obterDadosCondutas(RelatorioAtendEmergObstetricaVO vo) {
		List<McoPlanoIniciais> planoIniciais = getPerinatologiaFacade().listarPlanosIniciaisPorPacienteSequenceNumeroConsulta(vo.getPacCodigo(), vo.getGsoSeqp(), vo.getConNumero());
		for (McoPlanoIniciais pli : planoIniciais) {
			RelatorioAtendEmergObstetricaCondutaVO condutaVO = new RelatorioAtendEmergObstetricaCondutaVO();
			condutaVO.setComplementoConduta(pli.getComplemento());
			McoConduta cdt = getPerinatologiaFacade().obterMcoCondutaPorChavePrimaria(pli.getId().getCdtSeq());
			if (cdt != null) {
				condutaVO.setCodConduta(obterConcatenarIsNotEmpty(cdt.getCod(), "(", ")"));
				condutaVO.setDescrConduta(cdt.getDescricao());
			}
			vo.getCondutas().add(condutaVO);
		}
	}
	
	/**
	 * Alimenta a vo com uma listagem de RelatorioAtendEmergObstetricaIntercorrenciasVO.
	 * Essa listagem será alimentada por (QING) e (QOPA)
	 * 
	 * @author daniel.silva
	 * @since 27/08/12
	 * @param vo
	 */
	private void obterIntercorrenciasGestacoes(RelatorioAtendEmergObstetricaVO vo) {
		List<McoIntercorrenciaGestacoes> intercorrenciaGestacoes = getPerinatologiaFacade().listarIntercorrenciasGestacoesPorCodGestCodPaciente(vo.getGsoSeqp(), vo.getPacCodigo(), DominioOcorrenciaIntercorrenciaGestacao.A);
		for (McoIntercorrenciaGestacoes ing : intercorrenciaGestacoes) {
			RelatorioAtendEmergObstetricaIntercorrenciasVO intercorrenciasVO = new RelatorioAtendEmergObstetricaIntercorrenciasVO();
			intercorrenciasVO.setIngComplemento(ing.getComplemento());
			McoIntercorPasatus opa = getPerinatologiaFacade().obterMcoIntercorPasatusPorChavePrimaria(ing.getId().getOpaSeq().intValue());
			if (opa != null) {
				intercorrenciasVO.setOpaDescricao(opa.getDescricao());
			}
			vo.getIntercorrencias().add(intercorrenciasVO);
		}
	}
	
	/**
	 * Alimenta a vo com os dados da tabela MCO_BOLSA_ROTAS
	 * @author daniel.silva
	 * @since  24/08/12
	 * @param vo
	 * @param bsr
	 */
	private void preencherDadosBolsaRoda(RelatorioAtendEmergObstetricaVO vo, McoBolsaRotas bsr) {
		vo.setFormaRuptura(bsr.getFormaRuptura());
		if(bsr.getDthrRompimento() != null){
			vo.setDthrRompimento(DateUtil.dataToString(bsr.getDthrRompimento(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
		}
		else{
			vo.setDthrRompimento(null);
		}
		vo.setLiquidoAmniotico(obterDescricaoIsDiferenteNull(bsr.getLiquidoAmniotico()));
		vo.setOdor(bsr.getIndOdorFetido() ? "odor fétido" : null);	
		vo.setIndAmnioscopia(bsr.getIndAmnioscopia() ? "Amnioscopia" : null);
	}

	/**
	 * Alimenta a vo com os dados da tabela FAT_CONVENIOS_SAUDE
	 * @author daniel.silva
	 * @since  24/08/12
	 * @param vo
	 * @param cnv
	 */
	private void preencherDadosConvenio(RelatorioAtendEmergObstetricaVO vo, FatConvenioSaude cnv) {
		vo.setConvenio(cnv.getDescricao());
	}

	/**
	 * Concatena dois valores e retorna uma String no valor "v1/v2 mmHg"
	 * @author daniel.silva
	 * @since  24/08/12
	 * @param sistolica
	 * @param diastolica
	 * @return
	 */
	private String obterPressaoFormatada(Short sistolica, Short diastolica) {
		String retorno = null;
		if (sistolica != null) {
			StringBuilder sb = new StringBuilder();
			sb.append(sistolica).append('/').append(diastolica).append(" mmHg");
			retorno = sb.toString();
		}
		return retorno;
	}

	/**
	 * Alimenta a vo com os dados da tabela MCO_ANAMNESE_EFS
	 * @author daniel.silva
	 * @since  24/08/12
	 * @param vo
	 * @param efi
	 */
	private void preencherDadosAnamneses(RelatorioAtendEmergObstetricaVO vo, McoAnamneseEfs efi) {
		vo.setDthrAtendimento(DateUtil.dataToString(efi.getDthrConsulta(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
		vo.setMotivoConsulta(efi.getMotivo());
		vo.setPressaoArt(obterPressaoFormatada(efi.getPressaoArtSistolica(), efi.getPressaoArtDiastolica()));
		vo.setPressaoRepouso(obterPressaoFormatada(efi.getPressaoSistRepouso(), efi.getPressaoDiastRepouso()));
 		
		if(efi.getPlanoDelee() != null){
			vo.setPlanoDelee(Byte.valueOf(efi.getPlanoDelee().toString()));
		}
		
 		vo.setObservacao(efi.getObservacao());
 		vo.setExFisicoGeral(obterConcatenarIsNotEmpty(efi.getExFisicoGeral(), "Ex Fís. Geral: ", null));
 		vo.setEfiCidSeq(efi.getCidSeq());
 		vo.setExameEspecular(efi.getExameEspecular());
 		if (efi.getDinamicaUterina() != null) {
 			vo.setDinamUterEIntens(efi.getDinamicaUterina());
 			if (obterDescricaoIsDiferenteNull(efi.getIntensidadeDinUterina()) != null) {
 				vo.setDinamUterEIntens(vo.getDinamUterEIntens().concat(" ").concat(efi.getIntensidadeDinUterina().getDescricao()));
			}
 		} else if(obterDescricaoIsDiferenteNull(efi.getIntensidadeDinUterina()) != null) {
 			vo.setDinamUterEIntens(obterDescricaoIsDiferenteNull(efi.getIntensidadeDinUterina()));
 		}
 		vo.setFreqCardiaca(obterConcatenarIsDiferenteNull(efi.getFreqCardiaca(), " bpm"));
 		
 		if (efi.getTemperatura() != null) {
 			vo.setTemperatura(obterConcatenarIsDiferenteNull(efi.getTemperatura(), " ºC").replace(".", ","));
 		}
 		
 		vo.setAlturaUterina(obterConcatenarIsDiferenteNull(efi.getAlturaUterina(), " cm"));
 		vo.setBatimentoCardiacoFetal(obterConcatenarIsDiferenteNull(efi.getBatimentoCardiacoFetal(), " bpm"));
 		vo.setBatimentoCardiacoFetalDois(obterConcatenarIsDiferenteNull(efi.getBatimentoCardiacoFetal2(), " bpm"));
		vo.setEdema(obterDescricaoIsDiferenteNull(efi.getEdema()));
		vo.setSitFeral(obterDescricaoIsDiferenteNull(efi.getSitFetal()));
		vo.setPosicaoCervice(obterDescricaoIsDiferenteNull(efi.getPosicaoCervice()));
		vo.setApresentacao(obterDescricaoIsDiferenteNull(efi.getApresentacao()));
		vo.setEspessuraCervice(obterConcatenarIsDiferenteNull(obterDescricaoIsDiferenteNull(efi.getEspessuraCervice()), "/"));
		vo.setApagamento(obterConcatenarIsNotEmpty(efi.getApagamento(), null, "%"));
		vo.setDilatacao(obterConcatenarIsNotEmpty(efi.getDilatacao(), null, " cm"));
		vo.setAr(obterConcatenarIsNotEmpty(efi.getAr(), "AR : ", null));
		vo.setAcv(obterConcatenarIsNotEmpty(efi.getAcv(), "ACV : ", null));
		vo.setAcelTrans(obterStringDominioSim(efi.getIndAcelTrans(), "Acel. transitória presente"));
		vo.setMovFetal(obterStringDominioSim(efi.getIndMovFetal(), "Movim. fetal presente"));
		vo.setFreqRespiratoria(obterConcatenarIsDiferenteNull(efi.getFreqRespiratoria(), " mpm"));
	}
	
	private String obterStringDominioSim(DominioSimNao dom, String str) {
		String retorno = null;
		if (dom != null && dom.isSim()) {
			retorno = str;
		}
		return retorno;
	}
	
	private String obterConcatenarIsNotEmpty(Object obj, String inicio, String fim) {
		StringBuilder sb = new StringBuilder();
		String retorno = null;
		if (obj != null) {
			if (StringUtils.isNotEmpty(inicio)) {
				sb.append(inicio);
			}
			sb.append(obj);
			if (StringUtils.isNotEmpty(fim)) {
				sb.append(fim);
			}
		}
		if (sb.length() > 0) {
			retorno = sb.toString();
		}
		return retorno;
	}
	
	private String obterDescricaoIsDiferenteNull(Dominio dom) {
		String str = null;
		if (dom != null) {
			str = dom.getDescricao();
		}
		return str;
	}
	
	private String obterConcatenarIsDiferenteNull(Object obj, String str) {
		StringBuilder sb = new StringBuilder();
		if (obj != null) {
			sb.append(obj).append(str);
		}
		String retorno = null;
		if (sb.length() > 0) {
			retorno = sb.toString();
		}
		return retorno;
	}
	
	/**
	 * Alimenta a vo com os dados da tabela AIP_PACIENTES
	 * @author daniel.silva
	 * @since  24/08/12
	 * @param vo
	 * @param pac
	 */
	private void preencherDadosPaciente(RelatorioAtendEmergObstetricaVO vo, AipPacientes pac) {
		vo.setNomePac(pac.getNome());
		vo.setProntuario(CoreUtil.formataProntuario(pac.getProntuario()));
		vo.setIdade(getAmbulatorioFacade().obterIdadeMesDias(pac.getDtNascimento(), new Date()));
		vo.setLeito(obterLeito(pac.getDtUltAlta(), pac.getDtUltInternacao(), pac.getLtoLtoId())); //CF_LEITOFormula
	}
	
	/**
	 * Retorna o Leito (CF_LEITOFormula)
	 * @param dtUltAlta
	 * @param dtUltInternacao
	 * @param ltoLtoId
	 * @return
	 */
	private String obterLeito(Date dtUltAlta, Date dtUltInternacao, String ltoLtoId) {
		String leito = null;
		if (dtUltAlta == null || DateValidator.validaDataMenor(dtUltAlta, dtUltInternacao)) {
			leito = ltoLtoId;
		}
		return leito;

	}

	/**
	 * Estória #17321 - Busca e preenche dados da altura (QALTURA)
	 * @author daniel.silva
	 * @since  24/08/12
	 */
	private void obterDadosAltura(RelatorioAtendEmergObstetricaVO vo) {
		AipAlturaPacientes alturaMae = getAipAlturaPacientesDAO().obterAlturaPorNumeroConsulta(vo.getConNumero());
		if(alturaMae != null && alturaMae.getAltura() != null){
			StringBuilder sb = new StringBuilder();
			sb.append(AghuNumberFormat.formatarNumeroMoeda(alturaMae.getAltura().divide(new BigDecimal(100),2,BigDecimal.ROUND_CEILING).doubleValue())).append(" m");
			vo.setAltura(sb.toString());
		}
		
	}

	/**
	 * Estoria #17321 - Busca e preeche os dados de peso do paciente - (QPESO)
	 * @author daniel.silva
	 * @since  24/08/12
	 */
	private void obterDadosPesoPaciente(RelatorioAtendEmergObstetricaVO vo) {
		AipPesoPacientes pesoMae = getAipPesoPacientesDAO().obterPesoPacientesPorNumeroConsulta(vo.getConNumero());
		if(pesoMae != null && pesoMae.getPeso() != null){
			vo.setPeso(pesoMae.getPeso().toString().replace(".", ",").concat((" kg")));
		} else {
			vo.setPeso("0 kg");
		}
	}	

	
	/**
	 * Estoria #17321 - busca e preenche os dados de gestação (QGESTACAO)
	 * @author daniel.silva
	 * @since  24/08/12
	 * @param  vo
	 */
	private void obterDadosGestacao(RelatorioAtendEmergObstetricaVO vo) {
		
		McoGestacoes gso = getPerinatologiaFacade().obterMcoGestacoes(new McoGestacoesId(vo.getPacCodigo(), vo.getGsoSeqp()));
		
		if(gso != null) {
			preencherDadosGestacao(vo, gso);
			AipRegSanguineos regSanguineo = this.aipRegSanguineosDAO.obterRegSanguineosPorCodigoPaciente(vo.getPacCodigo(), vo.getGsoSeqp().byteValue());
			if (regSanguineo != null) {
				preencherDadosSanguineos(vo, regSanguineo);
			}
		}
	}
	
	/**
	 * Alimenta o vo com os dados da tabela AIP_REG_SANGUINEOS
	 * @author daniel.silva
	 * @since  24/08/12
	 * @param vo
	 * @param registrosSanguineos
	 */
	private void preencherDadosSanguineos(RelatorioAtendEmergObstetricaVO vo, AipRegSanguineos registrosSanguineos) {
		Object[] array = {registrosSanguineos.getGrupoSanguineo(), registrosSanguineos.getFatorRh()};
		vo.setTipoSangueMae(StringUtils.join(array));
		if(StringUtils.equals(registrosSanguineos.getFatorRh(), "-") && registrosSanguineos.getCoombs() != null) {
			vo.setCoombs(registrosSanguineos.getCoombs().getDescricao());
		}
	}

	/**
	 * Função que formata os dias da eco (CF_DIAS_ECOFormula)
	 * @author daniel.silva
	 * @since  24/08/12
	 * @param dias
	 * @return
	 */
	private String obterDiasEcoFormatada(Byte dias) {
		StringBuilder sb = new StringBuilder();
		if (dias != null) {
			sb.append("/ ").append(dias).append(" dias");
		}
		return sb.toString();
	}

	/**
	 * Alimenta o vo com os dados da tabela MCO_GESTACOES
	 * @author daniel.silva
	 * @since  24/08/12
	 * @param vo
	 * @param gso
	 */
	private void preencherDadosGestacao(RelatorioAtendEmergObstetricaVO vo, McoGestacoes gso) {
		vo.setGesta(gso.getGesta());
		vo.setPara(gso.getPara());
		vo.setCesarea(gso.getCesarea());
		vo.setAborto(gso.getAborto());
		vo.setDum(DateUtil.dataToString(gso.getDtUltMenstruacao(), DATE_PATTERN_DDMMYYYY));
		vo.setDtPrimEco(DateUtil.dataToString(gso.getDtPrimEco(), DATE_PATTERN_DDMMYYYY));
		vo.setIgPrimEco(obterConcatenarIsDiferenteNull(gso.getIgPrimEco(), " semanas"));
		vo.setDtInformadaIg(DateUtil.dataToString(gso.getDtInformadaIg(), DATE_PATTERN_DDMMYYYY));
		vo.setDiasEco(obterDiasEcoFormatada(gso.getIgPrimEcoDias()));
		vo.setIdadeGestacional(obterIdadeGestacional(gso));
		vo.setDtProvavelParto(DateUtil.dataToString(gso.getDtProvavelParto(), DATE_PATTERN_DDMMYYYY));
		vo.setNumConsPrn(gso.getNumConsPrn());
		vo.setDtPrimConsPrn(DateUtil.dataToString(gso.getDtPrimConsPrn(), DATE_PATTERN_DDMMYYYY));
		vo.setGemelar(gso.getGemelar());
		vo.setVatCompleta(gso.getVatCompleta() ? "Vacina anti-tetânica completa" : null);
		vo.setUsoMedicamentos(obterConcatenarIsNotEmpty(gso.getUsoMedicamentos(), "Medicamentos: ", null));
		vo.setEctopica(gso.getEctopica());
		vo.setGravidez(obterDescricaoIsDiferenteNull(gso.getGravidez()));
	}

	/**
	 * Retorna uma String contendo a idade gestacional.
	 * @author daniel.silva
	 * @since  24/08/12
	 * @param gso
	 * @return
	 */
	private String obterIdadeGestacional(McoGestacoes gso) {
		StringBuilder sb = new StringBuilder();
		if(gso.getIgAtualSemanas() != null) {
			sb.append(gso.getIgAtualSemanas().toString()).append(" semanas /");
		}
		if (gso.getIgAtualDias() != null) {				
			sb.append(gso.getIgAtualDias()).append(" dias");
		}else{
			sb.append("0 dias");
		}
		
		return sb.toString();
	}
	
	/**
	 * @author daniel.silva
	 * @since  24/08/12
	 * @return
	 */
	protected AipPacientesDAO getAipPacientesDAO() {
		return aipPacientesDAO;
	}
	
	/**
	 * Acesso ao modulo perinatologia
	 * @author daniel.silva
	 * @since  24/08/12
	 * @return
	 */
	protected IPerinatologiaFacade getPerinatologiaFacade() {
		return perinatologiaFacade;
	}
	
	/**
	 * @author daniel.silva
	 * @since  24/08/12
	 * @return
	 */
	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return ambulatorioFacade;
	}
	
	/**
	 * @author daniel.silva
	 * @since  24/08/12
	 * @return
	 */
	protected AipAlturaPacientesDAO getAipAlturaPacientesDAO() {
		return aipAlturaPacientesDAO;
	}
	
	/**
	 * @author daniel.silva
	 * @since  24/08/12
	 * @return
	 */
	protected AipPesoPacientesDAO getAipPesoPacientesDAO() {
		return aipPesoPacientesDAO;
	}
	
	/**
	 * @author daniel.silva
	 * @since  24/08/12
	 * @return
	 */
	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}
	
	/**
	 * @author daniel.silva
	 * @since  29/08/12
	 * @return
	 */
	protected SumarioAtendimentoRecemNascidoRN getSumarioAtendimentoRecemNascidoRN() {
		return sumarioAtendimentoRecemNascidoRN;
	}
}
