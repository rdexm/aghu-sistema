package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.dominio.DominioGrupoMbcPosicionamento;
import br.gov.mec.aghu.dominio.DominioOcorrenciaFichaEvento;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSituacaoExame;
import br.gov.mec.aghu.dominio.DominioTipoEventoMbcPrincipal;
import br.gov.mec.aghu.dominio.DominioTipoFluidoAdministrado;
import br.gov.mec.aghu.dominio.DominioTipoFluidoPerdido;
import br.gov.mec.aghu.dominio.DominioTipoInducaoManutencao;
import br.gov.mec.aghu.dominio.DominioTipoOcorrenciaFichaFarmaco;
import br.gov.mec.aghu.model.MbcAnestRegionalNeuroeixos;
import br.gov.mec.aghu.model.MbcFichaAnestesiaRegional;
import br.gov.mec.aghu.model.MbcFichaAnestesias;
import br.gov.mec.aghu.model.MbcFichaBloqNervoPlexos;
import br.gov.mec.aghu.model.MbcFichaEquipeAnestesia;
import br.gov.mec.aghu.model.MbcFichaEspecifIntubacoes;
import br.gov.mec.aghu.model.MbcFichaExame;
import br.gov.mec.aghu.model.MbcFichaFinal;
import br.gov.mec.aghu.model.MbcFichaGrandePorte;
import br.gov.mec.aghu.model.MbcFichaInducaoManutencao;
import br.gov.mec.aghu.model.MbcFichaInicial;
import br.gov.mec.aghu.model.MbcFichaMaterial;
import br.gov.mec.aghu.model.MbcFichaMedMonitorizacao;
import br.gov.mec.aghu.model.MbcFichaNeonatologia;
import br.gov.mec.aghu.model.MbcFichaOrgaoTransplante;
import br.gov.mec.aghu.model.MbcFichaPaciente;
import br.gov.mec.aghu.model.MbcFichaPosicionamento;
import br.gov.mec.aghu.model.MbcFichaProcedimento;
import br.gov.mec.aghu.model.MbcFichaTipoAnestesia;
import br.gov.mec.aghu.model.MbcFichaVentilacao;
import br.gov.mec.aghu.model.MbcFichaViaAerea;
import br.gov.mec.aghu.model.MbcNeuroeixoNvlPuncionados;
import br.gov.mec.aghu.model.MbcOcorrenciaFichaEvento;
import br.gov.mec.aghu.model.MbcTmpFichaFarmaco;
import br.gov.mec.aghu.model.MbcVentilacao;
import br.gov.mec.aghu.model.MbcViaAerea;
import br.gov.mec.aghu.model.McoRecemNascidos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.dao.AipPacientesDAO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.AtosAnestesicosPolVO;
import br.gov.mec.aghu.perinatologia.business.IPerinatologiaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.util.CapitalizeEnum;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;

@SuppressWarnings("PMD.ExcessiveClassLength")
@Stateless
public class RelatorioAtosAnestesicosPOLON extends BaseBusiness {


private static final String _ML = " mL";

private static final String _HIFEN_ = " - ";

private static final String PONTO_VIRGULA_ = ";  ";

@EJB
private RelatorioAtosAnestesicosPOLRN relatorioAtosAnestesicosPOLRN;

private static final Log LOG = LogFactory.getLog(RelatorioAtosAnestesicosPOLON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IAmbulatorioFacade ambulatorioFacade;

@EJB
private IRegistroColaboradorFacade registroColaboradorFacade;

@EJB
private IPerinatologiaFacade perinatologiaFacade;

@EJB
private IBlocoCirurgicoFacade blocoCirurgicoFacade;

@Inject
private AipPacientesDAO pacienteDAO;

	private static final long serialVersionUID = -8100452693663797692L;

	@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
	public List<AtosAnestesicosPolVO> pesquisarRelatorioAtosAnestesicos(
			Long seqMbcFichaAnestesia, String vSessao)
			throws ApplicationBusinessException {
		if ("-1".equals(vSessao)) {
			vSessao = null;
		}
		MbcFichaAnestesias ffa = getBlocoCirurgicoFacade()
				.obterMbcFichaAnestesia(seqMbcFichaAnestesia);
		AtosAnestesicosPolVO vo = new AtosAnestesicosPolVO();

		vo.setFicSeq(toBig(ffa.getSeq()));
		vo.setOrigem(vo.getOrigem());
		vo.setLocal(ffa.getUnidadeFuncional().getDescricao());
		vo.setPacProntuario(CoreUtil.formataProntuarioRelatorio(ffa
				.getPaciente().getProntuario()));
		vo.setPacNome(ffa.getPaciente().getNome());
		vo.setPacIdade(getRelatorioAtosAnestesicosPOLRN().mbccIdaAnoMesDia(
				ffa.getPaciente().getCodigo()));
		vo.setPacSexo(ffa.getPaciente().getSexo().getDescricao());
		vo.setPacCor(ffa.getPaciente().getCor().getDescricao());
		vo.setLeito(ffa.getLeito());
		vo.setCarater(ffa.getNatureza() != null ? ffa.getNatureza().getDescricao(): null);
		vo.setSala(toBig(ffa.getSala()));

		MbcFichaFinal fichaFinal = recuperaMbcFichaFinal(ffa.getSeq());
		if (fichaFinal != null) {
			vo.setPorte(fichaFinal.getPorte().getDescricao());
		}
		Date inicioAnestesia = getRelatorioAtosAnestesicosPOLRN()
				.mbccGetDthrEvento(ffa.getSeq(),
						DominioOcorrenciaFichaEvento.I, "ANESTESIA");
		Date fimAnestesia = getRelatorioAtosAnestesicosPOLRN()
				.mbccGetDthrEvento(ffa.getSeq(),
						DominioOcorrenciaFichaEvento.F, "ANESTESIA");
		Date inicioCirurgia = getRelatorioAtosAnestesicosPOLRN()
				.mbccGetDthrEvento(ffa.getSeq(),
						DominioOcorrenciaFichaEvento.I, "CIRURGIA");
		Date fimCirurgia = getRelatorioAtosAnestesicosPOLRN()
				.mbccGetDthrEvento(ffa.getSeq(),
						DominioOcorrenciaFichaEvento.F, "CIRURGIA");
		vo.setInicioAnestesia(inicioAnestesia);
		vo.setFimAnestesia(fimAnestesia);
		vo.setInicioCirurgia(DateUtil.dataToString(inicioCirurgia,
				DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
		vo.setFimCirurgia(DateUtil.dataToString(fimCirurgia,
				DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
		String duracao = diferencaEmHorasEMinutosFormatado(
				inicioAnestesia, fimAnestesia);
		vo.setDuracaoAnestesia(duracao != null ? duracao : "");
		duracao = diferencaEmHorasEMinutosFormatado(inicioCirurgia,
				fimCirurgia);
		vo.setDuracaoCirurgia(duracao != null ? duracao : "");

		MbcFichaPaciente fichaPaciente = recuperaMbcFichaPaciente(ffa.getSeq());
		if (fichaPaciente != null) {
			vo.setAsa("ASA: " + fichaPaciente.getAsa().getDescricao());
			if (fichaPaciente.getJustificativaAsa() != null) {
				vo.setJustAsa("Justificativa ASA>1: "
						+ fichaPaciente.getJustificativaAsa());
			}
			if (fichaPaciente.getTempoJejum() == null) {
				if (fichaPaciente.getJejumIndeterminado()) {
					vo.setTempoJejum("Tempo Jejum Indeterminado ");
				}
			} else {
				vo.setTempoJejum(fichaPaciente.getTempoJejum() + " horas");
			}

			if (fichaPaciente.getPeso() != null) {
				vo.setPeso(new DecimalFormat("###.##").format(fichaPaciente
						.getPeso()) + " Kg");
			}

			if (fichaPaciente.getAltura() != null) {
				vo.setAltura((fichaPaciente.getAltura() + " m").replace(".",
						","));
			}

			if (fichaPaciente.getAltura() != null
					&& fichaPaciente.getAltura() != 0
					&& fichaPaciente.getPeso() != null) {
				BigDecimal imc = CoreUtil.calculaImc(new BigDecimal(
						fichaPaciente.getPeso()),
						new BigDecimal(fichaPaciente.getAltura()));
				vo.setImc(imc);
				vo.setSuperficieCorporal((getBlocoCirurgicoFacade()
						.calcularSuperficieCorporalDoPaciente(
								ffa.getPaciente().getCodigo()).setScale(2,
								RoundingMode.FLOOR) + " m2").replace(".", ","));
			} else {
				vo.setSuperficieCorporal("   m2");
			}

			if (fichaPaciente.getMbcCondPsicoSensoriais() != null) {
				vo.setCondPsicoSensorial("Cond.Psico-sensoriais na Admissão: "
						+ fichaPaciente.getMbcCondPsicoSensoriais()
								.getDescricao());
			}
		}

		MbcFichaInicial fichaInicial = recuperaMbcFichaInicial(ffa.getSeq());
		if (fichaInicial != null) {
			vo.setViaArea("Via Aérea/Ventilação na Admissão: "
					+ concatenarViaAerea(fichaInicial.getMbcViaAereas(),
							fichaInicial.getO2(), fichaInicial.getRegime()));
		}

		vo.setObservacao(ffa.getObservacao());

		MbcFichaTipoAnestesia fichaTipoAnestesia = recuperaMbcFichaTipoAnestesia(ffa
				.getSeq());
		if (fichaTipoAnestesia != null) {
			vo.setTipoAnestesia(fichaTipoAnestesia.getTipoAnestesia()
					.getDescricao());
		}

		vo.setDefinicaoMonitorizacao(getRelatorioAtosAnestesicosPOLRN()
				.mbccGetDefMonit(ffa.getSeq()));
		vo.setFarmacos(getRelatorioAtosAnestesicosPOLRN().mbccGetFarmaco(
				Integer.valueOf(ffa.getSeq().toString())));
		vo.setTecnicas(getRelatorioAtosAnestesicosPOLRN().mbccGetTecnica(
				ffa.getSeq()));

		if (fichaFinal != null) {
			vo.setNumLinhasVenosas(fichaFinal.getNumLinhasVenosas());
			vo.setCondPsicoRecuperacao(fichaFinal.getMbcCondPsicoSensoriais()
					.getDescricao());
			vo.setViaAereaRecuperacao(concatenarViaAerea(
					fichaFinal.getMbcViaAereas(), fichaFinal.getO2(),
					fichaFinal.getRegime()));
			if (fichaFinal.getDestinoPaciente() != null) {
				vo.setDestinoPaciente(fichaFinal.getDestinoPaciente()
						.getDescricao());
			}
		}

		vo.setDthrValida(ffa.getDthrValida());

		vo.setAssinatura(processarAssinaturaServidorValida(ffa
				.getServidorValida()));

		vo.setGsoPacCodigo(ffa.getGsoPacCodigo() != null ? new BigDecimal(ffa
				.getGsoPacCodigo()) : null);
		vo.setGsoSeqp(ffa.getGsoSeqp() != null ? new BigDecimal(ffa
				.getGsoSeqp()) : null);
		vo.setTotNeonato(new BigDecimal(getBlocoCirurgicoFacade()
				.getCountMbcFichaNeonatologiaByMbcFichaAnestesia(ffa.getSeq())));
		vo.setTotGrandePorte(new BigDecimal(getBlocoCirurgicoFacade()
				.getCountMbcFichaGrandePorteByMbcFichaAnestesia(ffa.getSeq())));

		vo.setMbcrFichaEquipe(getMbcrFichaEquipe(ffa));
		vo.setMbcrFichaProcedimento(getMbcrFichaProcedimento(ffa));
		vo.setMbcrFichaExame(getMbcrFichaExame(ffa));
		vo.setMbcrFichaMedicamentoPre(getMbcrFichaMedicamentoPre(ffa));
		vo.setMbcrFichaInducao(getMbcrFichaInducao(ffa));
		vo.setMbcrFichaGrafico(getMbcrFichaGrafico(ffa));
		vo.setMbcrFichaMatrizFarmaco(getMbcrFichaMatrizFarmaco(ffa, vSessao));
		vo.setMbcrFichaNeonatologia(getMbcrFichaNeonatologia(ffa));
		vo.setMbcrFichaRegional(getMbcrFichaRegional(ffa));
		vo.setMbcrFicha5viaVentPos(getMbcrFicha5viaVentPos(ffa));
		vo.setMbcrFichaFluido(getMbcrFichaFluido(ffa));
		vo.setMbcrFichaEvento(getMbcrFichaEvento(ffa));
		vo.setMbcrFichaGrandePorte(getMbcrFichaGrandePorte(ffa));
		vo.setMbcrFichaObstetricia(getMbcrFichaObstetricia(ffa));
		vo.setMbcrFichaManutencao(getMbcrFichaManutencao(ffa));
		vo.setDataAtual(new Date());

		return Arrays.asList(vo);
	}

	private List<LinhaReportVO> getMbcrFichaProcedimento(MbcFichaAnestesias ffa) {
		List<MbcFichaProcedimento> fichaProc = getBlocoCirurgicoFacade()
				.obterFichaProcedimentoComProcedimentoCirurgicoByFichaAnestesia(
						ffa.getSeq(), DominioSituacaoExame.R);

		List<LinhaReportVO> results = new ArrayList<LinhaReportVO>();
		for (MbcFichaProcedimento proc : fichaProc) {
			results.add(new LinhaReportVO(capitalizaTodas(proc
					.getMbcProcedimentoCirurgicos().getDescricao())));
		}

		return results;
	}

	private List<LinhaReportVO> getMbcrFichaManutencao(MbcFichaAnestesias ffa) {
		List<MbcFichaInducaoManutencao> fichasInducoes = getBlocoCirurgicoFacade()
				.pesquisarMbcInducaoManutencaoByFichaAnestesia(ffa.getSeq(),
						DominioTipoInducaoManutencao.M, Boolean.TRUE);
		List<LinhaReportVO> exames = new ArrayList<LinhaReportVO>();

		for (MbcFichaInducaoManutencao ex : fichasInducoes) {
			LinhaReportVO vo = new LinhaReportVO();
			vo.setTexto1(ex.getMbcInducaoManutencoes().getDescricao());
			exames.add(vo);
		}

		return exames;
	}

	private List<LinhaReportVO> getMbcrFichaObstetricia(
			MbcFichaAnestesias ffa) {
		List<LinhaReportVO> vos = new ArrayList<LinhaReportVO>();

		if(ffa.getGsoPacCodigo() != null && ffa.getGsoSeqp() != null){
			List<String> descricaoIndNascimento = getPerinatologiaFacade()
				.obterDescricaoMcoIndicacaoNascimentoByFichaAnestesia(
						ffa.getGsoPacCodigo(), ffa.getGsoSeqp());
	
			for(String desc : descricaoIndNascimento){
				LinhaReportVO vo = new LinhaReportVO();
				vo.setTexto1(desc);
				vo.setSubReport1(getMbcrFichaObstetriciaRns(ffa.getSeq()));
		
				vos.add(vo);
			}
		}
		return vos;
	}

	private List<LinhaReportVO> getMbcrFichaObstetriciaRns(
			Long seqMbcFichaAnestesia) {

		List<McoRecemNascidos> rns = getPerinatologiaFacade().pesquisarMcoRecemNascidoByMbcFichaAnestesia(seqMbcFichaAnestesia);

		List<LinhaReportVO> vos = new ArrayList<LinhaReportVO>();
		for(McoRecemNascidos rn : rns){
			LinhaReportVO vo = new LinhaReportVO();

			StringBuilder txt = new StringBuilder(50);
			txt.append("Sexo: ")
			.append( pacienteDAO.obterPorChavePrimaria(rn.getPaciente()).getSexo().getDescricao())
			.append("  Peso: ")
			.append(getRelatorioAtosAnestesicosPOLRN().mcocPesoRn(rn.getPaciente().getCodigo()).getPeso()
			    .setScale(2, RoundingMode.FLOOR)).append( "g  Apgar 1 min: ")
			.append(rn.getApgar1() ).append("  Apgar 5 min: ").append(rn.getApgar5());

			vo.setTexto1(txt.toString());
			vos.add(vo);
		}
		return vos;
	}
	private IPerinatologiaFacade getPerinatologiaFacade(){
		return perinatologiaFacade;
	}

	@SuppressWarnings("PMD.NPathComplexity")
	private List<LinhaReportVO> getMbcrFichaGrandePorte(MbcFichaAnestesias ffa) {
		List<MbcFichaGrandePorte> fichasPortes = getBlocoCirurgicoFacade()
				.pesquisarMbcFichaGrandePorteByMbcFichaAnestesia(ffa.getSeq());

		List<LinhaReportVO> vos = new ArrayList<LinhaReportVO>();
		for (MbcFichaGrandePorte ex : fichasPortes) {

			LinhaReportVO vo = new LinhaReportVO();
			vo.setTexto1(ex.getCecCanulArterial() != null ? ex
					.getCecCanulArterial().getDescricao() : null);
			vo.setTexto2(ex.getCecCanulAtrial() != null ? ex
					.getCecCanulAtrial().getDescricao() : null);
			vo.setTexto3(ex.getCecCanulAtrial() != null ? ex
					.getCecCanulAtrial().getDescricao() : null);
			vo.setTexto4(ex.getCecFluxo() != null ? ex.getCecFluxo()
					.getDescricao() : null);

			StringBuilder card = new StringBuilder();
			card.append(ex.getCardEspessura() != null ? ex.getCardEspessura()
					.getDescricao() + PONTO_VIRGULA_ : null)
			.append(ex.getCardTemperatura() != null ? ex
					.getCardTemperatura().getDescricao() + PONTO_VIRGULA_ : null)
			.append(ex.getCardMomento() != null ? ex.getCardMomento()
					.getDescricao() + PONTO_VIRGULA_ : null)
			.append(ex.getCardFluxo() != null ? ex.getCardFluxo()
					.getDescricao() + PONTO_VIRGULA_ : null);
			vo.setTexto5(card.toString());

			StringBuilder trans = new StringBuilder(30);
			trans.append(Boolean.TRUE.equals(ex.getRetransplante()) ? "Retransplante;  "
					: "")
			.append(Boolean.TRUE.equals(ex.getIntervivos()) ? "Intervivos;  "
					: "")
			.append(ex.getChild() != null ? "CHILD:"
					+ ex.getChild().name() + PONTO_VIRGULA_ : "")
			.append(ex.getMeldPeld() != null ? "MELD/PELD:"
					+ ex.getMeldPeld() + PONTO_VIRGULA_ : "");
			vo.setTexto6(trans.toString());

			vo.setSubReport1(getMbcrFichaGrandePorteOrgao(ffa.getSeq()));
			vos.add(vo);
		}

		return vos;
	}

	private List<LinhaReportVO> getMbcrFichaGrandePorteOrgao(
			Long seqMbcFichaAnestesia) {
		List<MbcFichaOrgaoTransplante> fichasOrgaosTransp = getBlocoCirurgicoFacade()
				.pesquisarMbcFichaOrgaoTransplanteByFichaAnestesia(
						seqMbcFichaAnestesia);

		List<LinhaReportVO> vos = new ArrayList<LinhaReportVO>();
		for (MbcFichaOrgaoTransplante ex : fichasOrgaosTransp) {

			LinhaReportVO vo = new LinhaReportVO();
			StringBuilder txt = new StringBuilder();
			txt.append(ex.getMbcOrgaoTransplantados() != null ? ex
					.getMbcOrgaoTransplantados().getDescricao() : "")
			.append(",  Dt Retirada: ")
			.append(DateUtil.dataToString(ex.getDthrRetirada(),
					DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
			vo.setTexto1(capitalizaTodas(txt.toString()));

			String isquemiaFr = getRelatorioAtosAnestesicosPOLRN()
					.mbccGetIsquemiaFr(
							seqMbcFichaAnestesia,
							ex.getMbcOrgaoTransplantados() != null ? ex
									.getMbcOrgaoTransplantados().getSeq()
									: null);
			vo.setTexto2(isquemiaFr);

			String isquemiaGt = getRelatorioAtosAnestesicosPOLRN()
					.mbccGetIsquemiaGt(
							seqMbcFichaAnestesia,
							ex.getMbcOrgaoTransplantados() != null ? ex
									.getMbcOrgaoTransplantados().getSeq()
									: null);
			vo.setTexto3(isquemiaGt);

			vos.add(vo);
		}

		return vos;
	}

	private List<LinhaReportVO> getMbcrFichaEvento(MbcFichaAnestesias ffa) {
		LinhaReportVO vo = new LinhaReportVO();
		vo.setSubReport1(getMbcrFichaEventoPrincipal(ffa.getSeq()));
		vo.setSubReport2(getMbcrFichaEventoAdverso(ffa.getSeq()));

		return Arrays.asList(vo);
	}

	private List<LinhaReportVO> getMbcrFichaEventoAdverso(
			Long seqMbcFichaAnestesia) {
		List<MbcOcorrenciaFichaEvento> fichasEvs = getBlocoCirurgicoFacade()
				.pesquisarMbcOcorrenciaFichaEventoComMbcEventoAdverso(
						seqMbcFichaAnestesia);

		List<LinhaReportVO> vos = new ArrayList<LinhaReportVO>();
		for (MbcOcorrenciaFichaEvento ex : fichasEvs) {

			LinhaReportVO vo = new LinhaReportVO();
			vo.setTexto1(ex.getMbcFichaEventos().getMbcEventoAdversos()
					.getNome());

			vo.setTexto2(ex.getGravidade() != null ? ex.getGravidade()
					.getDescricao() : "");

			vo.setTexto3(DateUtil.dataToString(ex.getDthrOcorrencia(), DateConstants.DATE_PATTERN_HORA_MINUTO));

			vo.setTexto4(DateUtil.dataToString(ex.getDthrResolucao(), DateConstants.DATE_PATTERN_HORA_MINUTO));

			vos.add(vo);
		}

		return vos;
	}

	@SuppressWarnings("PMD.NPathComplexity")
	private List<LinhaReportVO> getMbcrFichaEventoPrincipal(
			Long seqMbcFichaAnestesia) {
		List<MbcOcorrenciaFichaEvento> fichasEvs = getBlocoCirurgicoFacade()
				.pesquisarMbcOcorrenciaFichaEvento(
						seqMbcFichaAnestesia,
						Arrays.asList(DominioTipoEventoMbcPrincipal.A,
								DominioTipoEventoMbcPrincipal.C),
						Arrays.asList(DominioTipoOcorrenciaFichaFarmaco.S,
								DominioTipoOcorrenciaFichaFarmaco.I));

		List<LinhaReportVO> vos = new ArrayList<LinhaReportVO>();
		for (MbcOcorrenciaFichaEvento ex : fichasEvs) {

			LinhaReportVO vo = new LinhaReportVO();
			StringBuilder txt = new StringBuilder();
			txt.append(ex.getMbcFichaEventos().getMbcEventoPrincipais() != null ? ex
					.getMbcFichaEventos().getMbcEventoPrincipais().getNome()
					: "")
			.append(ex.getMbcFichaEventos().getComplemento() != null ? ex
					.getMbcFichaEventos().getComplemento() + _HIFEN_ : "")
			.append(ex.getMbcFichaEventos().getMbcOrgaoTransplantados() != null ? ex
					.getMbcFichaEventos().getMbcOrgaoTransplantados()
					.getDescricao()
					+ _HIFEN_
					: "");
			vo.setTexto1(txt.toString());

			vo.setTexto2(DateUtil.dataToString(ex.getDthrOcorrencia(), DateConstants.DATE_PATTERN_HORA_MINUTO));

			Date dthrOcorrenciaFichaEvento = getBlocoCirurgicoFacade()
					.obterDtOcorrenciaMaxMbcFichaOcorrenciaEvento(
							ex.getMbcFichaEventos().getSeq(),
							DominioTipoOcorrenciaFichaFarmaco.F);
			vo.setTexto3(DateUtil.dataToString(dthrOcorrenciaFichaEvento,
					DateConstants.DATE_PATTERN_HORA_MINUTO));

			vo.setNumero1(ex.getMbcFichaEventos().getMbcEventoPrincipais() != null ? new BigDecimal(
					ex.getMbcFichaEventos().getMbcEventoPrincipais().getOrdem())
					: null);
			vos.add(vo);
		}

		List<MbcFichaFinal> fichasFinais = getBlocoCirurgicoFacade()
				.pesquisarMbcFichasFinais(seqMbcFichaAnestesia, Boolean.TRUE);
		for (MbcFichaFinal ex : fichasFinais) {
			LinhaReportVO vo = new LinhaReportVO();
			vo.setTexto1(Boolean.TRUE.equals(ex.getNenhumEventoAdverso()) ? "Nenhum Evento Adverso constatado"
					: null);
			vo.setTexto2(null);
			vo.setTexto3(null);
			vo.setNumero1(BigDecimal.ZERO);
			vos.add(vo);
		}

		CoreUtil.ordenarLista(vos, "texto1", Boolean.TRUE);

		return vos;
	}

	private List<LinhaReportVO> getMbcrFichaFluido(MbcFichaAnestesias ffa) {

		List<MbcFichaFinal> fichasFinais = getBlocoCirurgicoFacade()
				.pesquisarMbcFichasFinais(ffa.getSeq());
		
		if(fichasFinais == null || fichasFinais.isEmpty()){
			return new ArrayList<LinhaReportVO>();
		}
		
		List<LinhaReportVO> vos = new ArrayList<LinhaReportVO>();
		MbcFichaFinal ex = fichasFinais.get(0);
		LinhaReportVO vo = new LinhaReportVO();

		vo.setTexto1(Boolean.TRUE.equals(ex != null
				&& ex.getNenhumFluidoAdministrado()) ? "Nenhum Fluido Administrado"
				: "");
		vo.setTexto2(getRelatorioAtosAnestesicosPOLRN().mbccGetFluidoAdm(
				ffa.getSeq(), DominioTipoFluidoAdministrado.CR));
		vo.setTexto3(getRelatorioAtosAnestesicosPOLRN().mbccGetFluidoAdm(
				ffa.getSeq(), DominioTipoFluidoAdministrado.CO));
		vo.setTexto4(getRelatorioAtosAnestesicosPOLRN().mbccGetFluidoAdm(
				ffa.getSeq(), DominioTipoFluidoAdministrado.HE));
		vo.setTexto5(Boolean.TRUE.equals(ex != null
				&& ex.getProvaCruzadaEmSala()) ? "**Realizada Prova Cruzada em Sala"
				: "");
		vo.setTexto6(getRelatorioAtosAnestesicosPOLRN().mbccGetFluidoPerd(
				ffa.getSeq(), DominioTipoFluidoPerdido.F));
		vo.setTexto7(getRelatorioAtosAnestesicosPOLRN().mbccGetFluidoPerd(
				ffa.getSeq(), DominioTipoFluidoPerdido.S));

		Long totF = getRelatorioAtosAnestesicosPOLRN().mbccGetTotPerdido(ffa.getSeq(), DominioTipoFluidoPerdido.F);
		Long totS = getRelatorioAtosAnestesicosPOLRN().mbccGetTotPerdido(ffa.getSeq(), DominioTipoFluidoPerdido.S);
		totF = zeroSeNull(totF);
		totS = zeroSeNull(totS);

		vo.setNumero1(new BigDecimal(totF + totS));
		vo.setSubReport1(getMbcrFichaBalanco(ffa.getSeq()));
		vos.add(vo);

		return vos;
	}

	private List<LinhaReportVO> getMbcrFichaBalanco(Long seqMbcFichaAnest) {
		List<LinhaReportVO> vos = new ArrayList<LinhaReportVO>();

		LinhaReportVO vo = new LinhaReportVO();

		Long totAdm = getRelatorioAtosAnestesicosPOLRN().mbccGetTotAdm(
				seqMbcFichaAnest, Boolean.TRUE);
		totAdm = zeroSeNull(totAdm);
		vo.setTexto1(totAdm + _ML);

		Long totPerd = getRelatorioAtosAnestesicosPOLRN().mbccGetTotPerdido(seqMbcFichaAnest, DominioTipoFluidoPerdido.F);
		totPerd = zeroSeNull(totPerd);
		vo.setTexto2(totPerd + _ML);

		vo.setTexto3(totAdm - totPerd + _ML);

		totAdm = getRelatorioAtosAnestesicosPOLRN().mbccGetTotAdm(seqMbcFichaAnest, Boolean.FALSE);
		totAdm = zeroSeNull(totAdm);
		vo.setTexto4(totAdm + _ML);

		totPerd = getRelatorioAtosAnestesicosPOLRN().mbccGetTotPerdido(seqMbcFichaAnest, DominioTipoFluidoPerdido.S);
		totPerd = zeroSeNull(totPerd);
		vo.setTexto5(totPerd + _ML);

		vo.setTexto6(totAdm - totPerd + _ML);

		vos.add(vo);

		return vos;
	}
	
	private Long zeroSeNull(Long num) {
		if (num == null) {
			return 0l;
		} else {
			return num;
		}
	}

	private List<LinhaReportVO> getMbcrFicha5viaVentPos(MbcFichaAnestesias ffa) {
		Long countMbcViaArea = getBlocoCirurgicoFacade()
				.getCountMbcFichaViaAereaByFichaAnestesia(ffa.getSeq());
		List<LinhaReportVO> vos = new ArrayList<LinhaReportVO>();
		LinhaReportVO vo = new LinhaReportVO();
		vo.setNumero1(new BigDecimal(countMbcViaArea));
		vo.setNumero2(new BigDecimal(ffa.getSeq()));
		vo.setTexto1(ffa.getEspecificaVentilacaoSistema());

		vo.setSubReport1(getMbcrFichaViaAerea(ffa.getSeq()));
		vo.setSubReport2(getMbcrFichaEspecificacaoIntubacao(ffa.getSeq()));
		vo.setSubReport3(getMbcrFichaEspecificacaoOutra(ffa.getSeq()));
		vo.setSubReport4(getMbcrFichaMaterialUtilizado(ffa.getSeq()));
		vo.setSubReport5(getMbcrFichaVentilacao(ffa.getSeq()));
		vo.setSubReport6(getMbcrFichaPosicao(ffa.getSeq()));
		vo.setSubReport7(getMbcrFichaInclinacao(ffa.getSeq()));

		vos.add(vo);
		return vos;
	}

	private List<LinhaReportVO> getMbcrFichaInclinacao(Long seqMbcFichaAnestesia) {
		List<MbcFichaPosicionamento> fichasPos = getBlocoCirurgicoFacade()
				.pesquisarMbcFichaPosicionamentosByFichaAnestesia(
						seqMbcFichaAnestesia, Boolean.TRUE,
						DominioGrupoMbcPosicionamento.I);

		List<LinhaReportVO> vos = new ArrayList<LinhaReportVO>();
		for (MbcFichaPosicionamento ex : fichasPos) {

			LinhaReportVO vo = new LinhaReportVO();
			vo.setTexto1(ex.getMbcPosicionamentos().getDescricao());
			vos.add(vo);
		}

		return vos;
	}

	private List<LinhaReportVO> getMbcrFichaPosicao(Long seqMbcFichaAnestesia) {
		List<MbcFichaPosicionamento> fichasPos = getBlocoCirurgicoFacade()
				.pesquisarMbcFichaPosicionamentosByFichaAnestesia(
						seqMbcFichaAnestesia, Boolean.TRUE,
						DominioGrupoMbcPosicionamento.P);

		List<LinhaReportVO> vos = new ArrayList<LinhaReportVO>();
		for (MbcFichaPosicionamento ex : fichasPos) {

			LinhaReportVO vo = new LinhaReportVO();
			vo.setTexto1(ex.getMbcPosicionamentos().getDescricao());
			vos.add(vo);
		}

		return vos;
	}

	private List<LinhaReportVO> getMbcrFichaVentilacao(Long seqMbcFichaAnestesia) {
		List<MbcFichaVentilacao> fichasVents = getBlocoCirurgicoFacade()
				.pesquisarMbcFichaVentilacaoByFichaAnestesia(
						seqMbcFichaAnestesia);
		List<LinhaReportVO> vos = new ArrayList<LinhaReportVO>();
		CoreUtil.ordenarLista(
				fichasVents,
				MbcFichaVentilacao.Fields.MBC_VENTILACOES
						.toString()
						.concat(".")
						.concat(MbcVentilacao.Fields.GRUPO.toString().concat(
								".codigo")), Boolean.TRUE);

		for (MbcFichaVentilacao ex : fichasVents) {

			LinhaReportVO vo = new LinhaReportVO();
			vo.setTexto1(ex.getMbcVentilacoes().getDescricao());
			vos.add(vo);
		}

		return vos;
	}

	private List<LinhaReportVO> getMbcrFichaMaterialUtilizado(
			Long seqMbcFichaAnestesia) {
		List<MbcFichaMaterial> fichasMats = getBlocoCirurgicoFacade()
				.pesquisarMbcFichasMateriaisComScoMaterialByFichaAnestesia(
						seqMbcFichaAnestesia, null, Boolean.TRUE);
		List<LinhaReportVO> vos = new ArrayList<LinhaReportVO>();

		for (MbcFichaMaterial ex : fichasMats) {

			LinhaReportVO vo = new LinhaReportVO();
			StringBuilder txt = new StringBuilder(20);

			txt.append(capitalizaTodas(ex.getScoMaterial().getNome()))
			.append(ex.getScoMaterial().getNumero() != null ? "  Número: "
					+ ex.getScoMaterial().getNumero() : "")
			.append(ex.getQuantidade() != null ? "  Qtd: "
					+ ex.getQuantidade() : "");

			vo.setTexto1(txt.toString());

			vos.add(vo);
		}

		return vos;
	}

	private List<LinhaReportVO> getMbcrFichaEspecificacaoOutra(
			Long seqMbcFichaAnestesia) {
		List<MbcFichaEspecifIntubacoes> espsInt = getBlocoCirurgicoFacade()
				.pesquisarMbcEspecifIntubacaoByFichaAnestesiaAndTipo(
						seqMbcFichaAnestesia, "O");
		List<LinhaReportVO> vos = new ArrayList<LinhaReportVO>();

		for (MbcFichaEspecifIntubacoes ex : espsInt) {
			LinhaReportVO vo = new LinhaReportVO();
			vo.setTexto1(ex.getMbcEspecificacaoIntubacoes().getDescricao());
			vos.add(vo);
		}

		return vos;
	}

	private List<LinhaReportVO> getMbcrFichaEspecificacaoIntubacao(
			Long seqMbcFichaAnestesia) {
		List<MbcFichaEspecifIntubacoes> espsInt = getBlocoCirurgicoFacade()
				.pesquisarMbcEspecifIntubacaoByFichaAnestesiaAndTipo(
						seqMbcFichaAnestesia, "T");
		List<LinhaReportVO> vos = new ArrayList<LinhaReportVO>();

		for (MbcFichaEspecifIntubacoes ex : espsInt) {
			LinhaReportVO vo = new LinhaReportVO();
			vo.setTexto1(ex.getMbcEspecificacaoIntubacoes().getDescricao());
			vos.add(vo);
		}

		return vos;
	}

	private List<LinhaReportVO> getMbcrFichaViaAerea(Long seqMbcFichaAnestesia) {

		List<MbcFichaViaAerea> viasAereas = getBlocoCirurgicoFacade()
				.pesquisarMbcFichaViaAereaByFichaAnestesia(seqMbcFichaAnestesia);
		List<LinhaReportVO> vos = new ArrayList<LinhaReportVO>();

		for (MbcFichaViaAerea ex : viasAereas) {
			LinhaReportVO vo = new LinhaReportVO();
			vo.setTexto1(ex.getMbcViaAereas().getDescricao());
			vos.add(vo);
		}

		return vos;
	}

	@SuppressWarnings("PMD.NPathComplexity")
	private List<LinhaReportVO> getMbcrFichaRegional(MbcFichaAnestesias ffa) {
		List<MbcFichaAnestesiaRegional> fichasRegionais = getBlocoCirurgicoFacade()
				.pesquisarMbcFichaAnestesiaRegionalByFichaAnestesia(
						ffa.getSeq(), Boolean.TRUE, Boolean.TRUE, Boolean.TRUE,
						Boolean.TRUE);
		List<LinhaReportVO> vos = new ArrayList<LinhaReportVO>();
		LinhaReportVO vo = new LinhaReportVO();
		Boolean encontrou = Boolean.FALSE;

		if (fichasRegionais != null && !fichasRegionais.isEmpty()) {
			encontrou = Boolean.TRUE;
			MbcFichaAnestesiaRegional ex = fichasRegionais.get(0);

			// Não foi adicionado 'Boolean.TRUE' no lugar do '"S"' porque estas
			// colunas não tem constraint
			vo.setTexto1("S".equals(ex.getIvRegional()) ? "IV Regional" : null);
			vo.setTexto2("S".equals(ex.getIntercostais()) ? "Intercostais"
					: null);

			StringBuilder especificaIntercostais = new StringBuilder();
			especificaIntercostais.append(ex.getIntercostaisDe() != null ? "De "
							+ ex.getIntercostaisDe() + "º" : null)
			.append(ex.getIntercostaisAte() != null ? " a "
							+ ex.getIntercostaisAte() + "º" : null)
			.append("S".equals(ex
					.getIntercostaisBilateral()) ? " Bilateral" : null);
			vo.setTexto3(especificaIntercostais.toString());

			vo.setTexto4("S".equals(ex.getCateterPrevio()) ? "Cateter Prévio"
					: null);
			vo.setTexto5("S".equals(ex.getUsoCateter()) ? "Uso de Cateter"
					: null);
			vo.setTexto5(ex.getNumeroCateter() != null ? vo.getTexto5()
					+ " Número " + ex.getNumeroCateter() : null);

			vo.setTexto6("S".equals(ex.getInfusaoContinua()) ? "Infusão Contínua"
					: null);
			vo.setTexto7(ex.getLatencia() != null ? "Latência: "
					+ ex.getLatencia() + " min" : null);
			vo.setTexto8(ex.getDuracao() != null ? "Duração: "
					+ ex.getDuracao() + " min." : null);

			vo.setTexto9("S".equals(ex.getBloqueioExecutadoEqpCirurgi()) ? "Bloqueio executado pela equipe cirúrgica"
					: null);
		}
		if (encontrou) {
			vo.setSubReport1(getMbcrFichaNervoPlexo(ffa));
			vo.setSubReport2(getMbcrFichaNeuroeixo(ffa));
			vo.setSubReport3(getMbcrFichaAgulhas(ffa));
			vo.setSubReport4(getMbcrFichaRegionalExecutor(ffa));

			vos.add(vo);
		}
		return vos;
	}

	private List<LinhaReportVO> getMbcrFichaRegionalExecutor(
			MbcFichaAnestesias ffa) {
		List<MbcFichaEquipeAnestesia> fichasEqsAnests = getBlocoCirurgicoFacade()
				.pesquisarMbcFichaEquipeAnestesiasByFichaAnestesia(
						ffa.getSeq(), Boolean.TRUE);
		List<LinhaReportVO> vos = new ArrayList<LinhaReportVO>();

		for (MbcFichaEquipeAnestesia ex : fichasEqsAnests) {
			LinhaReportVO vo = new LinhaReportVO();

			if (ex.getServidorAnest() != null) {
				String vPes = getRegistroColaboradorFacade()
						.obterNomePessoaServidor(
								ex.getServidorAnest().getId().getVinCodigo(),
								ex.getServidorAnest().getId().getMatricula());
				if (vPes != null) {
					vo.setTexto1(capitalizaTodas(vPes));
					vos.add(vo);
				}
			}
		}

		return vos;
	}

	private List<LinhaReportVO> getMbcrFichaAgulhas(MbcFichaAnestesias ffa) {
		List<MbcFichaMaterial> fichasMats = getBlocoCirurgicoFacade()
				.pesquisarMbcFichasMateriaisComScoMaterialByFichaAnestesia(
						ffa.getSeq(), Boolean.TRUE, null);
		List<LinhaReportVO> vos = new ArrayList<LinhaReportVO>();

		for (MbcFichaMaterial ex : fichasMats) {

			LinhaReportVO vo = new LinhaReportVO();
			StringBuilder txt = new StringBuilder();

			txt.append(capitalizaTodas(ex.getScoMaterial().getNome()))
			.append(ex.getQuantidade() != null ? "  Qtd: "
					+ ex.getQuantidade() : "");

			vo.setTexto1(txt.toString());

			vos.add(vo);
		}

		return vos;
	}

	private List<LinhaReportVO> getMbcrFichaNeuroeixo(MbcFichaAnestesias ffa) {
		List<MbcAnestRegionalNeuroeixos> fichasAnests = getBlocoCirurgicoFacade()
				.pesquisarMbcAnestRegNeuroeixoByFichaAnestesia(ffa.getSeq());
		List<LinhaReportVO> vos = new ArrayList<LinhaReportVO>();
		LinhaReportVO vo = new LinhaReportVO();

		if (fichasAnests != null && !fichasAnests.isEmpty()) {
			MbcAnestRegionalNeuroeixos ex = fichasAnests.get(0);
			vo.setNumero1(new BigDecimal(ex.getSeq()));
			StringBuilder txt = new StringBuilder();

			txt.append("Volume Inicial Injetado: ")
			.append(new DecimalFormat("###.##").format(ex
					.getVolumeInicialInjetado().doubleValue()))
			.append(_ML)
			.append(ex.getMbcNivelBloqueiosByNblSeqInicial() != null ? "   Nível Bloqueio Inicial: "
					+ ex.getMbcNivelBloqueiosByNblSeqInicial().getDescricao()
					: "")
			.append(ex.getMbcNivelBloqueiosByNblSeqFinal() != null ? "   Final: "
					+ ex.getMbcNivelBloqueiosByNblSeqFinal().getDescricao()
					: "");

			vo.setTexto1(txt.toString());

			vo.setSubReport1(getMbcrFichaNeuroeixoNiveis(vo.getNumero1()
					.intValue()));

			vos.add(vo);
		}

		return vos;
	}

	private List<LinhaReportVO> getMbcrFichaNeuroeixoNiveis(
			Integer seqMbcNeuroEixoNvlPuncionados) {
		List<MbcNeuroeixoNvlPuncionados> fichasNeuros = getBlocoCirurgicoFacade()
				.pesquisarMbcNeuroNvlPuncionadosByMbcAnestRegionalNeuroeixos(
						seqMbcNeuroEixoNvlPuncionados);
		List<LinhaReportVO> vos = new ArrayList<LinhaReportVO>();

		for (MbcNeuroeixoNvlPuncionados ex : fichasNeuros) {
			LinhaReportVO vo = new LinhaReportVO();
			vo.setTexto1(ex.getBloqueio() != null ? ex.getBloqueio()
					.getDescricao() : null);
			vo.setTexto2(ex.getAbordagem() != null ? "Abord.: "
					+ ex.getAbordagem().getDescricao() : null);
			vo.setTexto3(ex.getMbcNivelPuncionados() != null ? "Niveis Punc.: "
					+ ex.getMbcNivelPuncionados().getDescricao() : null);
			vo.setTexto4(ex.getMbcPosicionamentos() != null ? "Pos.: "
					+ ex.getMbcPosicionamentos().getDescricao() : null);

			vos.add(vo);
		}

		return vos;
	}

	@SuppressWarnings("PMD.NPathComplexity")
	private List<LinhaReportVO> getMbcrFichaNervoPlexo(MbcFichaAnestesias ffa) {
		List<MbcFichaBloqNervoPlexos> fichasNervosPlexos = getBlocoCirurgicoFacade()
				.pesquisarMbcFichaBloqNervoPlexosByFichaAnestesia(ffa.getSeq());
		List<LinhaReportVO> vos = new ArrayList<LinhaReportVO>();

		for (MbcFichaBloqNervoPlexos ex : fichasNervosPlexos) {
			LinhaReportVO vo = new LinhaReportVO();
			StringBuilder txt = new StringBuilder();
			txt.append(ex.getMbcNervoPlexos() != null ? ex.getMbcNervoPlexos()
					.getDescricao() : ex.getOutroNervo())
			.append(" - Técnica " + ex.getMbcTecnBloqNervoPlexos() != null ? ex
					.getMbcTecnBloqNervoPlexos().getDescricao() : ex
					.getOutraTecnica())
			.append(Boolean.TRUE.equals(ex.getNeuroestimulacao()) ? ", Neuroestimulação"
					: "")
			.append(Boolean.TRUE.equals(ex.getParestesias()) ? ", Parestesias"
					: "")
			.append(Boolean.TRUE.equals(ex.getUltraSom()) ? ", Ultra-Som"
					: "");
			vo.setTexto1(txt.toString());
			vos.add(vo);
		}

		return vos;
	}

	@SuppressWarnings("PMD.NPathComplexity")
	private List<LinhaReportVO> getMbcrFichaNeonatologia(MbcFichaAnestesias ffa) {
		List<MbcFichaNeonatologia> fichasNeonatologias = getBlocoCirurgicoFacade()
				.pesquisarMbcNeonatologiasByFichaAnestesia(ffa.getSeq());
		List<LinhaReportVO> vos = new ArrayList<LinhaReportVO>();

		for (MbcFichaNeonatologia ex : fichasNeonatologias) {
			LinhaReportVO vo = new LinhaReportVO();

			vo.setTexto1(ex.getIgSemanas() != null ? "Capurro/Balard: "
					+ ex.getIgSemanas() + " semanas" : null);

			vo.setTexto2(ex.getRacaoHidrica() != null ? "Ração Hídrica Diária/Manutenção: "
					+ ex.getRacaoHidrica() + " ml/Kg/dia"
					: null);

			vo.setTexto3(ex.getTxInfusaoGlicose() != null ? "Taxa de infusão de glicose (TIG): "
					+ ex.getTxInfusaoGlicose() + " mg/Kg/min"
					: null);

			StringBuilder volume = new StringBuilder(80);
			volume.append("Volume nas últimas 12hs:  ")
			.append(ex.getVolumeSoro() != null ? "Soro Fisiológico ('"
					+ ex.getVolumeSoro() + "');" : "")
			.append(ex.getVolumeSangue() != null ? "Sangue ('"
					+ ex.getVolumeSangue() + "');" : "")
			.append(ex.getVolumePlasma() != null ? "Plasma ('"
					+ ex.getVolumeSoro() + "');" : "");
			vo.setTexto4(volume.toString());

			vo.setNumero1(ex.getVolumeSoro() != null ? new BigDecimal(ex
					.getVolumeSoro()) : null);

			vo.setNumero2(ex.getVolumeSangue() != null ? new BigDecimal(ex
					.getVolumeSangue()) : null);

			vo.setNumero3(ex.getVolumePlasma() != null ? new BigDecimal(ex
					.getVolumePlasma()) : null);

			vo.setTexto5(ex.getDiurese() != null ? "Diurese nas últimas 6hs: "
					+ ex.getDiurese() : null);

			StringBuilder diurese = new StringBuilder(30);
			diurese.append("Diurese nas últimas 6hs: ")
			.append(ex.getAcessoCentralPic() == null
					|| !ex.getAcessoCentralPic() ? ""
					: "Acesso central PIC (cateterização periférica);  ")
			.append(ex.getPuncaoUmbilical() == null
					|| !ex.getPuncaoUmbilical() ? ""
					: "Punção de artéria umbilical;  ");
			vo.setTexto6(diurese.toString());

			vos.add(vo);
		}

		return vos;
	}

	private List<LinhaReportVO> getMbcrFichaMatrizFarmaco(
			MbcFichaAnestesias ffa, String vSessao) {
		List<MbcTmpFichaFarmaco> fichasTmps = getBlocoCirurgicoFacade()
				.pesquisarMbcTmpFichaFarmacoByFichaAnestesiaESessao(
						ffa.getSeq().intValue(), vSessao);
		List<LinhaReportVO> vos = new ArrayList<LinhaReportVO>();

		for (MbcTmpFichaFarmaco ex : fichasTmps) {
			LinhaReportVO vo = new LinhaReportVO();
			/* ORDEM */vo.setNumero1(new BigDecimal(ex.getOrdem()));
			/* MEDICAMENTO */vo.setTexto1(ex.getDescMedicamento());
			/* DOSE_TOTAL */vo.setTexto2(ex.getDoseTotal());
			/* DTHR_OCORRENCIA */vo.setData(ex.getDthrOcorrencia());
			/* TEMPO_DECORRIDO */vo
					.setNumero2(ex.getTempoDecorrido() != null ? new BigDecimal(
							ex.getTempoDecorrido()) : null);
			/* CELULA */vo.setTexto3(ex.getCelula());

			vos.add(vo);
		}

		return vos;
	}

	private List<LinhaReportVO> getMbcrFichaGrafico(MbcFichaAnestesias ffa) {
		List<MbcFichaMedMonitorizacao> fichasMedMonitorizacoes = getBlocoCirurgicoFacade()
				.pesquisarMbcFichaMedMonitorizacaoComMbcTipoItemMonit(
						ffa.getSeq());
		List<LinhaReportVO> vos = new ArrayList<LinhaReportVO>();

		for (MbcFichaMedMonitorizacao ex : fichasMedMonitorizacoes) {
			LinhaReportVO vo = new LinhaReportVO();

			vo.setTexto1(ex.getMbcFichaMonitorizacao()
					.getMbcItemMonitorizacao().getMbcTipoItemMonitorizacao()
					.getNomeReduzido());
			vo.setData(ex.getDthrMedicao());
			vo.setNumero1(new BigDecimal(ex.getMedicao()).setScale(0,
					RoundingMode.UP));

			vos.add(vo);
		}

		return vos;
	}

	private List<LinhaReportVO> getMbcrFichaInducao(MbcFichaAnestesias ffa) {
		List<MbcFichaInducaoManutencao> fichasInducoes = getBlocoCirurgicoFacade()
				.pesquisarMbcInducaoManutencaoByFichaAnestesia(ffa.getSeq(),
						DominioTipoInducaoManutencao.I, Boolean.TRUE);
		List<LinhaReportVO> exames = new ArrayList<LinhaReportVO>();

		for (MbcFichaInducaoManutencao ex : fichasInducoes) {
			LinhaReportVO vo = new LinhaReportVO();
			vo.setTexto1(ex.getMbcInducaoManutencoes().getDescricao());
			exames.add(vo);
		}

		return exames;
	}

	private List<LinhaReportVO> getMbcrFichaMedicamentoPre(
			MbcFichaAnestesias ffa) {
		String retorno = getRelatorioAtosAnestesicosPOLRN().mbccGetMedicPre(
				ffa.getSeq());
		if (retorno != null) {
			LinhaReportVO vo = new LinhaReportVO();
			vo.setTexto1(retorno);
			return Arrays.asList(vo);
		} else {
			return null;
		}
	}

	private List<LinhaReportVO> getMbcrFichaExame(MbcFichaAnestesias ffa) {
		List<MbcFichaExame> fichasExames = getBlocoCirurgicoFacade()
				.pesquisarMbcFichasExamesComItemSolicitacaoExame(ffa.getSeq());
		List<LinhaReportVO> exames = new ArrayList<LinhaReportVO>();

		for (MbcFichaExame ex : fichasExames) {
			LinhaReportVO vo = new LinhaReportVO();
			vo.setTexto1(capitalizaTodas(ex.getAelItemSolicitacaoExames()
					.getExame().getDescricaoUsual()));
			vo.setTexto2("Sol: "
					+ ex.getAelItemSolicitacaoExames().getId().getSoeSeq()
					+ "-" + ex.getAelItemSolicitacaoExames().getId().getSeqp());
			exames.add(vo);
		}

		return exames;
	}

	private List<LinhaReportVO> getMbcrFichaEquipe(MbcFichaAnestesias ffa) {
		List<MbcFichaEquipeAnestesia> equipesAnestesias = getBlocoCirurgicoFacade()
				.pesquisarMbcFichaEquipeAnestesiasComServidorAnestesia(
						ffa.getSeq());
		List<LinhaReportVO> equipes = new ArrayList<LinhaReportVO>();

		for (MbcFichaEquipeAnestesia eq : equipesAnestesias) {
			LinhaReportVO vo = new LinhaReportVO();
			vo.setTexto1(capitalizaTodas(eq.getServidorAnest()
					.getPessoaFisica().getNome()));
			vo.setTexto2(getRelatorioAtosAnestesicosPOLRN().mbccGetConselho(
					eq.getServidorAnest().getId().getMatricula(),
					eq.getServidorAnest().getId().getVinCodigo()));
			vo.setTexto3(getRelatorioAtosAnestesicosPOLRN().mbccGetNroConselh(
					eq.getServidorAnest().getId().getMatricula(),
					eq.getServidorAnest().getId().getVinCodigo()));
			vo.setTexto4(DateUtil.dataToString(eq.getDthrEntrada(), DateConstants.DATE_PATTERN_HORA_MINUTO));
			vo.setTexto5(DateUtil.dataToString(eq.getDthrSaida(), DateConstants.DATE_PATTERN_HORA_MINUTO));
			vo.setTexto6(eq.getAnoResidencia());

			equipes.add(vo);
		}
		return equipes;
	}

	private String concatenarViaAerea(MbcViaAerea mbcViaAereas, Short o2,
			String regime) {
		StringBuilder viaAerea = new StringBuilder(25);
		viaAerea.append(mbcViaAereas != null ? mbcViaAereas.getDescricao() : "")
		.append(o2 != null ? ("   O2:" + o2 + " L/min") : "")
		.append(regime != null ? ("   Regime:" + regime) : "");
		return viaAerea.toString();
	}

	private String processarAssinaturaServidorValida(
			RapServidores servidorValida) {
		if(servidorValida == null){
			return null;
		}
		
		StringBuilder txt = new StringBuilder();
		if (DominioSexo.F.equals(servidorValida.getPessoaFisica().getSexo())) {
			txt.append("Dra.");
		} else {
			txt.append("Dr.");
		}
		txt.append(' ')

		.append(capitalizaTodas(servidorValida.getPessoaFisica().getNome()))
		.append("   ")

		.append(getRelatorioAtosAnestesicosPOLRN().mbccGetConselho(
				servidorValida.getId().getMatricula(),
				servidorValida.getId().getVinCodigo()))
		.append(' ');

		String nroRegConselho = getRelatorioAtosAnestesicosPOLRN()
				.mbccGetNroConselh(servidorValida.getId().getMatricula(),
						servidorValida.getId().getVinCodigo());
		txt.append(nroRegConselho != null ? nroRegConselho : "");

		return txt.toString();
	}

	private IAmbulatorioFacade getAmbulatorioFacade() {
		return this.ambulatorioFacade;
	}

	private String capitalizaTodas(String texto) {
		return getAmbulatorioFacade().obterDescricaoCidCapitalizada(texto,
				CapitalizeEnum.TODAS);
	}

	private MbcFichaTipoAnestesia recuperaMbcFichaTipoAnestesia(Long seq) {
		List<MbcFichaTipoAnestesia> fichasTipoAnest = getBlocoCirurgicoFacade()
				.pesquisarMbcFichasTipoAnestesias(seq);
		if (fichasTipoAnest != null && !fichasTipoAnest.isEmpty()) {
			return fichasTipoAnest.get(0);
		}
		return null;
	}

	private MbcFichaInicial recuperaMbcFichaInicial(Long seq) {
		List<MbcFichaInicial> fichasIni = getBlocoCirurgicoFacade()
				.pesquisarMbcFichasIniciais(seq);
		if (fichasIni != null && !fichasIni.isEmpty()) {
			return fichasIni.get(0);
		}
		return null;
	}

	private MbcFichaPaciente recuperaMbcFichaPaciente(Long seq) {
		List<MbcFichaPaciente> fichasPac = getBlocoCirurgicoFacade()
				.pesquisarMbcFichasPacientes(seq);
		if (fichasPac != null && !fichasPac.isEmpty()) {
			return fichasPac.get(0);
		}
		return null;
	}

	public String diferencaEmHorasEMinutosFormatado(Date dataInicial, Date dataFinal){
		if(dataInicial != null && dataFinal != null){
			Long diferenca = dataFinal.getTime() - dataInicial.getTime();
			Long diferencaEmHoras = (diferenca / 1000) / 60 / 60;
			Long minutosRestantes = (diferenca / 1000) / 60 % 60;
	
			return String.format("%02d", diferencaEmHoras) + ":"
					+ String.format("%02d", minutosRestantes);
		}
		return null;
	}

	private MbcFichaFinal recuperaMbcFichaFinal(Long seq) {
		List<MbcFichaFinal> fichasFinais = getBlocoCirurgicoFacade()
				.pesquisarMbcFichasFinais(seq);
		if (fichasFinais != null && !fichasFinais.isEmpty()) {
			return fichasFinais.get(0);
		}
		return null;
	}

	private BigDecimal toBig(Short seq) {
		return seq != null ? new BigDecimal(seq) : null;
	}

	private BigDecimal toBig(Long seq) {
		return seq != null ? new BigDecimal(seq) : null;
	}

	private IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	private RelatorioAtosAnestesicosPOLRN getRelatorioAtosAnestesicosPOLRN() {
		return relatorioAtosAnestesicosPOLRN;
	}

}
