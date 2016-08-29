package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.controlepaciente.vo.DescricaoControlePacienteVO;
import br.gov.mec.aghu.dominio.DominioFormaRupturaBolsaRota;
import br.gov.mec.aghu.dominio.DominioMomento;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AipAlturaPacientes;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.AipPesoPacientes;
import br.gov.mec.aghu.model.McoAnamneseEfs;
import br.gov.mec.aghu.model.McoAtendTrabPartos;
import br.gov.mec.aghu.model.McoCesarianas;
import br.gov.mec.aghu.model.McoForcipes;
import br.gov.mec.aghu.model.McoIndicacaoNascimento;
import br.gov.mec.aghu.model.McoIntercorrencia;
import br.gov.mec.aghu.model.McoIntercorrenciaNascs;
import br.gov.mec.aghu.model.McoLogImpressoes;
import br.gov.mec.aghu.model.McoMedicamentoTrabPartos;
import br.gov.mec.aghu.model.McoNascIndicacoes;
import br.gov.mec.aghu.model.McoNascimentos;
import br.gov.mec.aghu.model.McoNotaAdicional;
import br.gov.mec.aghu.model.McoProcedimentoObstetrico;
import br.gov.mec.aghu.model.McoProfNascs;
import br.gov.mec.aghu.model.McoRecemNascidos;
import br.gov.mec.aghu.model.McoTrabPartos;
import br.gov.mec.aghu.model.MpmUnidadeMedidaMedica;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.dao.AipPacientesDAO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.PartoCesarianaVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.SumarioAssistenciaPartoVO;
import br.gov.mec.aghu.perinatologia.business.IPerinatologiaFacade;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.BuscaConselhoProfissionalServidorVO;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.AghuNumberFormat;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.StringUtil;

@Stateless
public class RelatorioSumarioAssistenciaPartoON extends BaseBusiness {

	@EJB
	private RelatorioSumarioAssistenciaPartoGraficoON relatorioSumarioAssistenciaPartoGraficoON;
	
	@EJB
	private RelExameFisicoRecemNascidoPOLON relExameFisicoRecemNascidoPOLON;
	
	private static final Log LOG = LogFactory.getLog(RelatorioSumarioAssistenciaPartoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IProntuarioOnlineFacade prontuarioOnlineFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IPerinatologiaFacade perinatologiaFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IFarmaciaFacade farmaciaFacade;
	
	@Inject
	private AipPacientesDAO pacienteDAO;

	private static final long serialVersionUID = -5044966756575908140L;

	public List<SumarioAssistenciaPartoVO> pesquisarRelatorioSumarioAssistenciaParto(
			Integer pacCodigo, Short gsoSeqp, Integer conNumero)
			throws ApplicationBusinessException {

		SumarioAssistenciaPartoVO voRelatorio = new SumarioAssistenciaPartoVO();

		// QPAC (1/2/"11 a 15"/"65 a 67")
		McoAnamneseEfs anamnese = getPerinatologiaFacade()
				.obterAnamnesePorGestacaoEConsulta(pacCodigo, gsoSeqp,
						conNumero);
		if (anamnese != null) {
			processarAnamnese(anamnese, voRelatorio);
		}

		voRelatorio.setfAltura(preencherDadosAltura(conNumero));// 4
		voRelatorio.setfConvenio(getProntuarioOnlineFacade()
				.obterDescricaoConvenio(pacCodigo, conNumero));// 5
		voRelatorio.setfPeso(preencherDadosPeso(conNumero));// 6

		// QNASCIMENTO (3/7/"27 a 57")
		voRelatorio.setListaEquipe(new ArrayList<LinhaReportVO>());
		voRelatorio.setListaPartoCesarianaVO(new ArrayList<PartoCesarianaVO>());

		List<McoNascimentos> listaNascimentos = getPerinatologiaFacade()
				.pesquisarNascimentosPorGestacao(pacCodigo, gsoSeqp);
		for (McoNascimentos nascimento : listaNascimentos) {
			processarAnamneseNascimento(anamnese, nascimento, voRelatorio);
		}

		// QTRABPARTOS, QINDNASCIMENTO, QATENDTRABPARTOS/QPOSICAO
		// (10/16/"21 a 26")
		processarTrabalhoParto(voRelatorio, pacCodigo, gsoSeqp);

		// listaMedicamentos (17 a 20)
		voRelatorio.setListaMedicamentos(processarMedicamentos(pacCodigo,
				gsoSeqp));

		// voRelatorio.setfGrafico1(processarGrafico1(pacCodigo, gsoSeqp));//8
		voRelatorio.setGrafico1(getRelatorioSumarioAssistenciaPartoGraficoON()
				.getGraficoFrequenciaCardiacaFetalSumAssistParto(pacCodigo,
						gsoSeqp));
		voRelatorio.setGrafico2(getRelatorioSumarioAssistenciaPartoGraficoON()
				.getGraficoPartogramaSumAssistParto(pacCodigo, gsoSeqp));

		// listaProfissionais (58 a 59)
		voRelatorio.setListaProfissionais(processarProfissionais(pacCodigo,
				gsoSeqp));

		// PARAM2 (60/61)
		processarParam2(voRelatorio, pacCodigo, gsoSeqp);

		// listaNotasAdicionais (62 a 64)
		voRelatorio.setListaNotasAdicionais(processarNotasAdicionais(pacCodigo,
				gsoSeqp));

		voRelatorio.setfCurrentDate(DateUtil.dataToString(new Date(),
				"dd/MM/yyyy HH:mm:ss"));// 68
		voRelatorio.setfPacCodigo(pacCodigo);// 69
		voRelatorio.setfConNumero(conNumero);// 70

		voRelatorio
				.setListaIntercorrencias(processarIntercorrenciasEmFilhos(voRelatorio));// (55
																						// a
																						// 57)

		return Arrays.asList(voRelatorio);
	}

	// (55 a 57)
	private List<LinhaReportVO> processarIntercorrenciasEmFilhos(
			SumarioAssistenciaPartoVO voRelatorio) {

		List<LinhaReportVO> intercorrencias = new ArrayList<LinhaReportVO>();

		for (PartoCesarianaVO parto : voRelatorio.getListaPartoCesarianaVO()) {
			intercorrencias.addAll(parto.getListaIntercorrencias());
		}
		return intercorrencias;
	}

	// PARAM2 (60/61)
	private void processarParam2(SumarioAssistenciaPartoVO voRelatorio,
			Integer pacCodigo, Short gsoSeqp) throws ApplicationBusinessException {
		List<McoLogImpressoes> listaLogImpressoes = getPerinatologiaFacade()
				.pesquisarLogImpressoesEventoMcorNascimento(pacCodigo, gsoSeqp);
		StringBuilder data = new StringBuilder();
		if (!listaLogImpressoes.isEmpty() && listaLogImpressoes.get(0).getCriadoEm() != null) {
			data.append(
					DateUtil.dataToString(listaLogImpressoes.get(0)
							.getCriadoEm(), "dd/MM/yyyy, HH:mm")).append('h');
		} else {
			data.append(DateUtil.dataToString(new Date(), "dd/MM/yyyy, HH:mm"))
					.append('h');
		}
		voRelatorio.setF8(data.toString());// 60

		if (!listaLogImpressoes.isEmpty() && listaLogImpressoes.get(0).getServidor() != null) {
			/*Object[] buscaConsProf = getPrescricaoMedicaFacade().buscaConsProf(
			listaLogImpressoes.get(0).getServidor());*/
			BuscaConselhoProfissionalServidorVO servidorProf = getPrescricaoMedicaFacade().buscaConselhoProfissionalServidorVO(listaLogImpressoes.get(0).getServidor().getId().getMatricula(), listaLogImpressoes.get(0).getServidor().getId().getVinCodigo(), false);
			/*String nomeServidor = (String) buscaConsProf[1];
			String siglaConselho = (String) buscaConsProf[2];
			String regConselho = (String) buscaConsProf[3];*/
			StringBuilder responsavel = new StringBuilder();
			responsavel.append(servidorProf.getNome()).append(' ').append(servidorProf.getSiglaConselho())
				.append(' ').append(servidorProf.getNumeroRegistroConselho());
			if (StringUtils.isBlank(responsavel.toString())) {
				String nome = getProntuarioOnlineFacade()
						.obterNomeProfissional(
								listaLogImpressoes.get(0).getServidor().getId()
										.getMatricula(),
								listaLogImpressoes.get(0).getServidor().getId()
										.getVinCodigo());
				responsavel.append(nome);
			}
			voRelatorio.setF9(responsavel.toString());// 61
		}
	}

	// QTRABPARTOS, QINDNASCIMENTO, QATENDTRABPARTOS/QPOSICAO (10/16/"21 a 26")
	private void processarTrabalhoParto(SumarioAssistenciaPartoVO voRelatorio,
			Integer pacCodigo, Short gsoSeqp) {
		// QTRABPARTOS
		List<McoTrabPartos> trabPartos = getPerinatologiaFacade()
				.listarTrabPartos(pacCodigo, gsoSeqp);
		if (trabPartos != null && !trabPartos.isEmpty()) {
			McoTrabPartos parto = trabPartos.get(0);

			if (parto.getJustificativa() != null) {
				voRelatorio.setfJustificativa(parto.getJustificativa());// 10
			}
			if (parto.getDthriniCtg() != null) {
				voRelatorio.setfDthrIniCtg(DateUtil.dataToString(
						parto.getDthriniCtg(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));// 21
			}
			if (parto.getIndicacoesCtg() != null) {
				voRelatorio.setfIndicacoes(parto.getIndicacoesCtg());// 22
			}
			if (parto.getTipoParto() != null) {
				voRelatorio.setfTipoParto(parto.getTipoParto().getDescricao());// 23
			}
			if (parto.getObservacao() != null) {
				voRelatorio.setfObservacao(parto.getObservacao());// 26
			}
			// QINDNASCIMENTO
			if (parto.getIndicacaoNascimento() != null) {
				McoIndicacaoNascimento indicacaoNascimento = getPerinatologiaFacade()
						.obterIndicacaoNascimentoPorChavePrimaria(
								parto.getIndicacaoNascimento().getSeq());
				if (indicacaoNascimento != null) {
					voRelatorio
							.setfDescrInd(indicacaoNascimento.getDescricao());// 24
				}
			}
		}
		// QATENDTRABPARTOS - QPOSICAO
		McoAtendTrabPartos atendTrabPartos = getPerinatologiaFacade()
				.obterAtendTrabPartosMaxSeqp(pacCodigo, gsoSeqp);
		if (atendTrabPartos != null) {
			if (atendTrabPartos.getCardiotocografia() != null) {
				voRelatorio.setfCardiotocografia(atendTrabPartos
						.getCardiotocografia().getDescricao());// 16
			}
			if (atendTrabPartos.getPosicao() != null) {
				voRelatorio.setfTbpPosicao(atendTrabPartos.getPosicao()
						.getDescricao());// 25
			}
		}
	}

	// QNASCIMENTO (3/7/"27 a 57")
	private void processarAnamneseNascimento(McoAnamneseEfs anamnese,
			McoNascimentos nascimento, SumarioAssistenciaPartoVO voRelatorio) {
		if (anamnese != null && anamnese.getConsulta().getPaciente() != null
				&& nascimento.getCriadoEm() != null) {
			// 3
			voRelatorio.setfIdade(getProntuarioOnlineFacade()
					.obterIdadeRecemNascido(
							anamnese.getConsulta().getPaciente()
									.getDtNascimento(),
							nascimento.getCriadoEm()));
		}
		// listaEquipe (7)
		if (voRelatorio.getListaEquipe().isEmpty()
				&& (nascimento.getEqpSeq() != null)) {
			voRelatorio.getListaEquipe().add(processarListaEquipe(nascimento));
		}
		// listaPartoCesariana (27 a 57)
		PartoCesarianaVO partoCesarianaVO = processarListaPartoCesariana(nascimento);
		partoCesarianaVO.setfIndiceNascimento(voRelatorio
				.getListaPartoCesarianaVO().size() + 1);
		voRelatorio.getListaPartoCesarianaVO().add(partoCesarianaVO);
	}

	// QPAC (1/2/"11 a 15"/"65 a 67")
	private void processarAnamnese(McoAnamneseEfs anamnese,
			SumarioAssistenciaPartoVO voRelatorio) {
		if (anamnese.getConsulta().getPaciente() != null) {
			voRelatorio
					.setfNome(anamnese.getConsulta().getPaciente().getNome());// 1
		}
		if (anamnese.getConsulta().getPaciente() != null
				&& anamnese.getConsulta().getPaciente().getProntuario() != null) {
			voRelatorio.setfProntuario(CoreUtil.formataProntuario(anamnese.getConsulta().getPaciente()
					.getProntuario()));// 2
		}
		if (anamnese.getMcoGestacoes().getBolsaRota() != null) {
			processarBolsaRota(anamnese, voRelatorio);
		}
		// 66
		voRelatorio.setfLeito(null);
		if (anamnese.getConsulta().getPaciente() != null) {
			if (anamnese.getConsulta().getPaciente().getDtUltAlta() == null) {
				voRelatorio.setfLeito(obterLeito(anamnese));
			} else {
				if (anamnese.getConsulta().getPaciente().getDtUltInternacao() != null) {
					if ((DateUtil.truncaData(anamnese.getConsulta()
							.getPaciente().getDtUltAlta())).before(DateUtil
							.truncaData(anamnese.getConsulta().getPaciente()
									.getDtUltInternacao()))) {
						voRelatorio.setfLeito(obterLeito(anamnese));
					}
				}
			}
		}
	}

	// bolsaRota (11.1/11.2/"12 a 15")
	private void processarBolsaRota(McoAnamneseEfs anamnese,
			SumarioAssistenciaPartoVO voRelatorio) {
		voRelatorio.setfLabel(null);
		if (anamnese.getMcoGestacoes().getBolsaRota() != null) {
			if (anamnese.getMcoGestacoes().getBolsaRota()
					.getDominioFormaRuptura() != null) {
				// StringBuilder formaRupturaDthrRompimento = new
				// StringBuilder();
				voRelatorio
						.setfFormaRuptura(anamnese.getMcoGestacoes()
								.getBolsaRota().getDominioFormaRuptura()
								.getDescricao());

				if (anamnese.getMcoGestacoes().getBolsaRota()
						.getDthrRompimento() != null) {
					voRelatorio.setfDthrRompimento(DateUtil.dataToString(
							anamnese.getMcoGestacoes().getBolsaRota()
									.getDthrRompimento(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
				} else {
					if ((anamnese.getMcoGestacoes().getBolsaRota()
							.getDominioFormaRuptura()
							.equals(DominioFormaRupturaBolsaRota.Amniorrexis))) {
						voRelatorio.setfLabel("Data de rompimento IGNORADA");// 12
					}
				}
			}
			// 13
			if (anamnese.getMcoGestacoes().getBolsaRota().getLiquidoAmniotico() != null) {
				voRelatorio.setfLiquidoAmniotico(anamnese.getMcoGestacoes()
						.getBolsaRota().getLiquidoAmniotico().getDescricao());
			}
			// 14
			if (anamnese.getMcoGestacoes().getBolsaRota().getIndOdorFetido() != null) {
				voRelatorio
						.setfOdor(DominioSimNao.S.equals(anamnese
								.getMcoGestacoes().getBolsaRota()
								.getIndOdorFetido()) ? "odor fétido" : null);
			}
			// 15
			if (anamnese.getMcoGestacoes().getBolsaRota().getIndAmnioscopia() != null) {
				voRelatorio
						.setfAmnioscopia(DominioSimNao.S.equals(anamnese
								.getMcoGestacoes().getBolsaRota()
								.getIndAmnioscopia()) ? "Amnioscopia" : null);
			}
		}
	}

	private String obterLeito(McoAnamneseEfs anamnese) {
			return anamnese.getPaciente().getLtoLtoId();
	}

	// listaEquipe (7)
	private LinhaReportVO processarListaEquipe(McoNascimentos nascimento) {
		LinhaReportVO linhaReportEquipe = new LinhaReportVO();
		// QEQUIPE
		if (nascimento.getEqpSeq() != null) {
			AghEquipes equipe = getAghuFacade().obterEquipeNotRestrict(
					nascimento.getEqpSeq().intValue());
			if (equipe != null) {
				linhaReportEquipe.setTexto1(equipe.getNome());// 7
			}
		}
		return linhaReportEquipe;
	}

	// listaPartoCesariana (27 a 57)
	private PartoCesarianaVO processarListaPartoCesariana(
			McoNascimentos nascimento) {

		PartoCesarianaVO partoCesarianaVO = new PartoCesarianaVO();

		// QNASCIMENTO (27 a 33)
		processarPartoCesarianaNascimento(nascimento, partoCesarianaVO);

		// QFORCIPE (34 a 36)
		processarPartoCesarianaForcipe(nascimento, partoCesarianaVO);

		// QCESARIANAS 37/38/39/40/41/42/43/44/45/46
		processarPartoCesarianaCesarianas(nascimento, partoCesarianaVO);

		// QRECEMNASCIDO 47/48/49/50/51/52/53/54/55/56
		processarPartoCesarianaRecemNascido(nascimento, partoCesarianaVO);

		// QINTERCORRENCIAS (55 a 57)
		processarPartoCesarianaIntercorrencias(nascimento, partoCesarianaVO);

		return partoCesarianaVO;
	}

	// QINTERCORRENCIAS (55 a 57)
	private void processarPartoCesarianaIntercorrencias(
			McoNascimentos nascimento, PartoCesarianaVO partoCesarianaVO) {
		List<LinhaReportVO> listaIntercorrencias = new ArrayList<LinhaReportVO>();
		List<McoIntercorrenciaNascs> listaIntercorrenciaNascs = getPerinatologiaFacade()
				.listarIntercorrenciaNascPorCodSequenceSeq(
						nascimento.getId().getGsoPacCodigo(),
						nascimento.getId().getGsoSeqp(),
						nascimento.getId().getSeqp());
		for (McoIntercorrenciaNascs intercorrenciaNascs : listaIntercorrenciaNascs) {
			if (intercorrenciaNascs.getDthrIntercorrencia() != null) {
				LinhaReportVO linhaReportIntercorrencias = new LinhaReportVO();
				linhaReportIntercorrencias.setTexto1(DateUtil.dataToString(
						intercorrenciaNascs.getDthrIntercorrencia(),
						DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));// 55
				// QINTERCORR
				McoIntercorrencia intercorrencia = getPerinatologiaFacade()
						.obterIntercorrenciaoPorChavePrimaria(
								intercorrenciaNascs.getMcoIntercorrencia().getSeq());
				if (intercorrencia != null) {
					linhaReportIntercorrencias.setTexto2(intercorrencia
							.getDescricao());// 56
				}
				// QPROCED
				if (intercorrenciaNascs.getMcoProcedimentoObstetricos() != null) {
					McoProcedimentoObstetrico procedimentoObstetrico = getPerinatologiaFacade()
							.obterProcedimentoObstetricoPorChavePrimaria(
									intercorrenciaNascs.getMcoProcedimentoObstetricos().getSeq());
					if (procedimentoObstetrico != null) {
						linhaReportIntercorrencias
								.setTexto3(procedimentoObstetrico
										.getDescricao());// 57
					}
				}
				listaIntercorrencias.add(linhaReportIntercorrencias);
			}
		}
		partoCesarianaVO.setListaIntercorrencias(listaIntercorrencias);
	}

	// QRECEMNASCIDO 47/48/49/50/51/52/53/54/55/56
	private void processarPartoCesarianaRecemNascido(McoNascimentos nascimento,
			PartoCesarianaVO partoCesarianaVO) {
		McoRecemNascidos recemNascidos = getPerinatologiaFacade()
				.obterRecemNascidoGestacoesPaciente(
						nascimento.getId().getGsoPacCodigo(),
						nascimento.getId().getGsoSeqp(),
						nascimento.getId().getSeqp());
		
		StringBuffer peso = new StringBuffer();
		
		if (nascimento.getPesoNamAbo() != null) {
			peso.append(nascimento.getPesoNamAbo()).append(" g");// 48
		} else if (recemNascidos != null) {
			AipPesoPacientes aipPeso = getPacienteFacade().obterPesoPaciente(recemNascidos.getPaciente().getCodigo(), DominioMomento.N);
			if (aipPeso != null) {
				peso.append(aipPeso.getPeso().multiply(new BigDecimal(1000)).setScale(0, RoundingMode.HALF_DOWN)).append(" g");// 48
			}
		}
		
		partoCesarianaVO.setfPesoRn(peso.toString());

		if (nascimento.getRnClassificacao() != null) {
			partoCesarianaVO.setfRnClassificacao(nascimento.getRnClassificacao().getDescricao());// 51
		}

		setDadosPlacenta(nascimento, partoCesarianaVO); // 52 e 53

		if (nascimento.getObservacao() != null) {
			partoCesarianaVO.setfObsNas(nascimento.getObservacao());// 54
		}
			
		if (recemNascidos != null) {
			
			if (recemNascidos.getApgar1() != null) {
				partoCesarianaVO.setfApgar1(recemNascidos.getApgar1()
						.toString());// 49
			}
			if (recemNascidos.getApgar5() != null) {
				partoCesarianaVO.setfApgar5(recemNascidos.getApgar5()
						.toString());// 50
			}
			
			// QAIPPACNAS
			AipPacientes pacientes =  pacienteDAO.obterPorChavePrimaria(recemNascidos.getPaciente().getCodigo());
			if (pacientes != null) {
					partoCesarianaVO.setfSexoRn(
								pacientes.getSexo() != null ? 
										pacientes.getSexo().getDescricao() : 
											DominioSexo.M.getDescricao());// 47
			}
		}
	}

	private void setDadosPlacenta(McoNascimentos nascimento,
			PartoCesarianaVO partoCesarianaVO) {
		StringBuffer pesoPlacenta = new StringBuffer();
		partoCesarianaVO
				.setfPesoPlac(nascimento.getPesoPlacenta() != null ? pesoPlacenta
						.append(nascimento.getPesoPlacenta()).append(" g")
						.toString()
						: "g"); // 52

		StringBuffer compCordao = new StringBuffer();
		partoCesarianaVO
				.setfCompCordao(nascimento.getCompCordao() != null ? " " + compCordao
						.append(nascimento.getCompCordao()).append(" cm")
						.toString() : "cm"); // 53

	}

	// QCESARIANAS 37/38/39/40/41/42/43/44/45/46
	private void processarPartoCesarianaCesarianas(McoNascimentos nascimento,
			PartoCesarianaVO partoCesarianaVO) {
		McoCesarianas cesarianas = getPerinatologiaFacade()
				.obterCesarianaPorChavePrimaria(
						nascimento.getId().getGsoPacCodigo(),
						nascimento.getId().getGsoSeqp(),
						nascimento.getId().getSeqp());
		if (cesarianas != null) {

			setarListaIndicacaoUsoCesareana(cesarianas, partoCesarianaVO);// 37

			tranformMcoNascimentoParaPartoCesariana(partoCesarianaVO,
					cesarianas);// 38 a 42

			partoCesarianaVO.setfLaqueadura(DominioSimNao.S.equals(cesarianas
					.getIndLaqueaduraTubaria()) ? "Laqueadura Tubaria" : null);// 43
			partoCesarianaVO.setfRafia(DominioSimNao.S.equals(cesarianas
					.getIndRafiaPeritonial()) ? "Rafia Peritonial" : null);// 44
			partoCesarianaVO.setfLavagem(DominioSimNao.S.equals(cesarianas
					.getIndLavagemCavidade()) ? "Lavagem Cavidade" : null);// 45
			partoCesarianaVO.setfDreno(DominioSimNao.S.equals(cesarianas
					.getIndDrenos()) ? "Drenos" : null);// 46
		}
	}

	// 37
	private void setarListaIndicacaoUsoCesareana(McoCesarianas cesarianas,
			PartoCesarianaVO partoCesarianaVO) {
		// Q6
		List<LinhaReportVO> listaIndUsoCesareana = new ArrayList<LinhaReportVO>();
		List<McoNascIndicacoes> listaNascIndicacoes = getPerinatologiaFacade()
				.listarNascIndicacoesPorCesariana(
						cesarianas.getMcoNascimentos().getId()
								.getGsoPacCodigo(),
						cesarianas.getMcoNascimentos().getId().getSeqp(),
						cesarianas.getMcoNascimentos().getId().getGsoSeqp());

		for (McoNascIndicacoes nascIndicacoes : listaNascIndicacoes) {
			if (nascIndicacoes.getIndicacaoNascimento().getSeq() != null) {
				LinhaReportVO linhaReportIndUsoCesareana = new LinhaReportVO();
				// Q2
				McoIndicacaoNascimento indicacaoNascimento = getPerinatologiaFacade()
						.obterIndicacaoNascimentoPorChavePrimaria(
								nascIndicacoes.getIndicacaoNascimento()
										.getSeq());
				if (indicacaoNascimento != null) {
					/*if (DominioTipoIndicacaoNascimento.C
							.equals(indicacaoNascimento.getTipoIndicacao())) {*/ //Defeito #21895
						linhaReportIndUsoCesareana
								.setTexto1(indicacaoNascimento.getDescricao());// 37
					//}
				}
				listaIndUsoCesareana.add(linhaReportIndUsoCesareana);
			}
		}
		partoCesarianaVO.setListaIndUsoCesareana(listaIndUsoCesareana);
	}

	private String editarTempo(Short tempo) {
		String duracao = StringUtil.adicionaZerosAEsquerda(tempo, 4);
		StringBuilder hrDuracao = new StringBuilder();
		hrDuracao.append(duracao.substring(0, 2)).append(':')
				.append(duracao.substring(2, 4)).append('h');
		return hrDuracao.toString();
	}

	// 39 a 42
	private void tranformMcoNascimentoParaPartoCesariana(
			PartoCesarianaVO partoCesarianaVO, McoCesarianas cesarianas) {
		if (cesarianas.getHrDuracao() != null) {
			partoCesarianaVO.setfHoraDuracao(editarTempo(cesarianas
					.getHrDuracao()));// 38
		}
		if (cesarianas.getLaparotomia() != null) {
			partoCesarianaVO.setfLaparotomia(cesarianas.getLaparotomia()
					.getDescricao());// 39
		}
		if (cesarianas.getHisterotomia() != null) {
			partoCesarianaVO.setfHisterotomia(cesarianas.getHisterotomia()
					.getDescricao());// 40
		}
		if (cesarianas.getHisterorrafia() != null) {
			partoCesarianaVO.setfHisterorrafia(cesarianas.getHisterorrafia()
					.getDescricao());// 41
		}
		if (cesarianas.getContaminacao() != null) {
			partoCesarianaVO.setfContaminacao(cesarianas.getContaminacao()
					.getDescricao());// 42
		}
	}

	// QFORCIPE (34 a 36)
	private void processarPartoCesarianaForcipe(McoNascimentos nascimento,
			PartoCesarianaVO partoCesarianaVO) {
		McoForcipes forcipes = getPerinatologiaFacade().obterForcipe(
				nascimento.getId().getGsoPacCodigo(),
				nascimento.getId().getGsoSeqp(), nascimento.getId().getSeqp());
		if (forcipes != null) {
			// QNASCINDIC
			List<LinhaReportVO> listaForcipe = new ArrayList<LinhaReportVO>();
			List<McoNascIndicacoes> listaNascIndicacoes = getPerinatologiaFacade()
					.pesquisarNascIndicacoesPorForcipes(
							forcipes.getMcoNascimentos().getId()
									.getGsoPacCodigo(),
							forcipes.getMcoNascimentos().getId().getGsoSeqp(),
							forcipes.getMcoNascimentos().getId().getSeqp());

			for (McoNascIndicacoes nascIndicacoes : listaNascIndicacoes) {
				if (nascIndicacoes.getIndicacaoNascimento().getSeq() != null) {
					LinhaReportVO linhaReportForcipe = new LinhaReportVO();
					// QINDNASC
					McoIndicacaoNascimento indicacaoNascimento = getPerinatologiaFacade()
							.obterIndicacaoNascimentoPorChavePrimaria(
									nascIndicacoes.getIndicacaoNascimento()
											.getSeq());
					if (indicacaoNascimento != null) {
						/*if (DominioTipoIndicacaoNascimento.F
								.equals(indicacaoNascimento.getTipoIndicacao())) {*/
							linhaReportForcipe.setTexto1(indicacaoNascimento
									.getDescricao());// 34
						//}
					}
					linhaReportForcipe.setTexto2(forcipes.getTipoForcipe()
							.getDescricao()
							+ "  "
							+ forcipes.getTamanhoForcipe().getDescricao());// 35
					linhaReportForcipe
							.setTexto3(DominioSimNao.S.equals(forcipes
									.getIndForcipeComRotacao()) ? "Com rotação"
									: null);// 36

					listaForcipe.add(linhaReportForcipe);
				}
			}
			partoCesarianaVO.setListaForcipe(listaForcipe);
		}
	}

	// QNASCIMENTO (27 a 33)
	private void processarPartoCesarianaNascimento(McoNascimentos nascimento,
			PartoCesarianaVO partoCesarianaVO) {
		if (nascimento.getDthrNascimento() != null) {
			partoCesarianaVO.setfDtNascimento(DateUtil.dataToString(
					nascimento.getDthrNascimento(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));// 27
		}
		partoCesarianaVO.setfTipo(nascimento.getTipo().getDescricao());// 28
		if (nascimento.getModo() != null) {
			partoCesarianaVO.setfModo(nascimento.getModo().getDescricao());// 29
		}
		if (nascimento.getIndEpisotomia() != null) {
			partoCesarianaVO.setfEpisiotomia(DominioSimNao.S.equals(nascimento
					.getIndEpisotomia()) ? "Episiotomia realizada" : null);// 30
		}
		if (nascimento.getPeriodoDilatacao() != null) {
			partoCesarianaVO.setfPeriodoDilatacao(editarTempo(nascimento
					.getPeriodoDilatacao()));// 31
		}
		if (nascimento.getPeriodoExpulsivo() != null) {
			partoCesarianaVO.setfPeriodoExpulsivo(editarTempo(nascimento
					.getPeriodoExpulsivo()));// 32
		}
		// 33
		partoCesarianaVO.setfDuracao(getProntuarioOnlineFacade()
				.obterDuracaoDoParto(nascimento.getId().getGsoPacCodigo(),
						nascimento.getId().getGsoSeqp(),
						nascimento.getPeriodoDilatacao(),
						nascimento.getPeriodoExpulsivo(),
						nascimento.getDthrNascimento()));
	}

	// QALTURA
	private String preencherDadosAltura(Integer conNumero) {
		String alturaMae = null;
		AipAlturaPacientes aipAltura = getPacienteFacade()
				.obterAlturaPorNumeroConsulta(conNumero);
		if (aipAltura != null && aipAltura.getAltura() != null
				&& aipAltura.getAltura().compareTo(BigDecimal.ZERO) > 0) {
			StringBuilder altura = new StringBuilder();
			altura.append(
					AghuNumberFormat.formatarNumeroMoeda(aipAltura
							.getAltura()
							.divide(new BigDecimal(100), 2,
									BigDecimal.ROUND_CEILING).doubleValue()))
					.append(" m");
			alturaMae = altura.toString();
		}
		return alturaMae;
	}

	// QPESO
	private String preencherDadosPeso(Integer conNumero) {
		String pesoMae = null;
		AipPesoPacientes aipPeso = getPacienteFacade()
				.obterPesoPacientesPorNumeroConsulta(conNumero);
		if (aipPeso != null && aipPeso.getPeso() != null) {
			StringBuilder peso = new StringBuilder();
			if (aipPeso.getPeso().scale() > 0) {
				peso.append(
						AghuNumberFormat.formatarNumeroMoeda(aipPeso.getPeso()
								.setScale(2, BigDecimal.ROUND_UP).doubleValue()))
						.append(" kg");
			} else {
				peso.append(aipPeso.getPeso().toString()).append(" kg");
			}
			pesoMae = peso.toString();
		}
		return pesoMae;
	}

	// listaMedicamentos (17 a 20)
	private List<LinhaReportVO> processarMedicamentos(Integer pacCodigo,
			Short gsoSeqp) {
		List<LinhaReportVO> listaMedicamentos = new ArrayList<LinhaReportVO>();
		// QMEDICAMUTIL
		List<McoMedicamentoTrabPartos> listaMedTrabPartos = getPerinatologiaFacade()
				.listarMedicamentosTrabPartos(pacCodigo, gsoSeqp);
		for (McoMedicamentoTrabPartos medTrabPartos : listaMedTrabPartos) {
			LinhaReportVO linhaReportMedicamento = new LinhaReportVO();
			if (medTrabPartos.getDataHoraInicial() != null) {
				linhaReportMedicamento.setTexto1(DateUtil.dataToString(
						medTrabPartos.getDataHoraInicial(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));// 17
			}
			// QCADASTROMED
			AfaMedicamento medicamento = getFarmaciaFacade().obterMedicamento(
					medTrabPartos.getId().getMedMatCodigo());
			if (medicamento != null) {
				linhaReportMedicamento.setTexto2(medicamento.getDescricao());// 18
				if (medicamento.getMpmUnidadeMedidaMedicas() != null) {
					// QUMM
					MpmUnidadeMedidaMedica umm = getPrescricaoMedicaFacade()
							.obterUnidadeMedicaPorId(
									medicamento.getMpmUnidadeMedidaMedicas()
											.getSeq());
					if (umm != null) {
						linhaReportMedicamento.setTexto4(umm.getDescricao());// 20
					}
				}
			}
			if (medTrabPartos.getDose() != null) {
				linhaReportMedicamento.setTexto3(medTrabPartos.getDose()
						.toString());// 19
			}
			listaMedicamentos.add(linhaReportMedicamento);
		}
		return listaMedicamentos;
	}

	// listaProfissionais (58 a 59)
	private List<LinhaReportVO> processarProfissionais(Integer pacCodigo,
			Short gsoSeqp) {
		List<LinhaReportVO> listaProfissionais = new ArrayList<LinhaReportVO>();
		// QPROFISSENVOLV
		Integer nasSeqp = 1;
		List<McoProfNascs> listaProfNascs = getPerinatologiaFacade()
				.listarProfNascsPorNascimento(pacCodigo, gsoSeqp, nasSeqp);
		for (McoProfNascs profNascs : listaProfNascs) {
			LinhaReportVO linhaReportProfissional = new LinhaReportVO();
			// 58
			linhaReportProfissional.setTexto1(getProntuarioOnlineFacade()
					.obterNomeProfissional(profNascs.getId().getSerMatriculaNasc(),
							profNascs.getId().getSerVinCodigoNasc()));
			// 59
			DescricaoControlePacienteVO descricao = getRegistroColaboradorFacade().buscarDescricaoAnotacaoControlePaciente(
					profNascs.getId().getSerVinCodigoNasc(),
					profNascs.getId().getSerMatriculaNasc());

			linhaReportProfissional.setTexto2(descricao != null ? descricao.getSiglaNumeroConselho(): null);

			listaProfissionais.add(linhaReportProfissional);
		}

		return listaProfissionais;
	}

	private IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}

	// listaNotasAdicionais (62 a 64)
	private List<LinhaReportVO> processarNotasAdicionais(Integer pacCodigo,
			Short gsoSeqp) throws ApplicationBusinessException {
		List<LinhaReportVO> listaNotasAdicionais = new ArrayList<LinhaReportVO>();
		// Q1
		List<McoNotaAdicional> listanotaAdicional = getPerinatologiaFacade()
				.pesquisarNotaAdicionalPorPacienteGestacaoEvento(pacCodigo,
						gsoSeqp);
		for (McoNotaAdicional notaAdicional : listanotaAdicional) {
			LinhaReportVO linhaReportProfissional = new LinhaReportVO();
			// 62
			if (notaAdicional.getNotaAdicional() != null) {
				linhaReportProfissional.setTexto1(notaAdicional
						.getNotaAdicional());
			}
			// 63
			linhaReportProfissional.setTexto2(DateUtil.dataToString(
					notaAdicional.getCriadoEm(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO) + "h");
			// 64
			linhaReportProfissional
					.setTexto3(getRelExameFisicoRecemNascidoPOLON()
							.formataNomeProf(notaAdicional.getSerMatricula(),
									notaAdicional.getSerVinCodigo()));
			/*RapServidores rapServidor = getRegistroColaboradorFacade()
					.buscarServidor(notaAdicional.getSerVinCodigo(),
							notaAdicional.getSerMatricula());
			if (rapServidor != null) {
				Object[] buscaConsProf = getPrescricaoMedicaFacade()
						.buscaConsProf(rapServidor);
				String nomeServidor = (String) buscaConsProf[1];
				String siglaConselho = (String) buscaConsProf[2];
				String regConselho = (String) buscaConsProf[3];
				StringBuilder responsavel = new StringBuilder();
				responsavel.append(nomeServidor).append(' ')
						.append(siglaConselho).append(' ').append(regConselho);
				linhaReportProfissional.setTexto3(responsavel.toString());
			}*/

			listaNotasAdicionais.add(linhaReportProfissional);
		}
		return listaNotasAdicionais;
	}

	protected IFarmaciaFacade getFarmaciaFacade() {
		return this.farmaciaFacade;
	}
	
	protected RelExameFisicoRecemNascidoPOLON getRelExameFisicoRecemNascidoPOLON(){
		return relExameFisicoRecemNascidoPOLON;
	}

	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return this.prescricaoMedicaFacade;
	}

	protected IPerinatologiaFacade getPerinatologiaFacade() {
		return this.perinatologiaFacade;
	}

	protected IPacienteFacade getPacienteFacade() {
		return this.pacienteFacade;
	}

	protected IProntuarioOnlineFacade getProntuarioOnlineFacade() {
		return prontuarioOnlineFacade;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	

	protected RelatorioSumarioAssistenciaPartoGraficoON getRelatorioSumarioAssistenciaPartoGraficoON() {
		return relatorioSumarioAssistenciaPartoGraficoON;
	}
}
