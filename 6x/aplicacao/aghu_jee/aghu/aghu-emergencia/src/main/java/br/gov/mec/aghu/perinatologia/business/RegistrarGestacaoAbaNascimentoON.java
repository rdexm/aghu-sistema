package br.gov.mec.aghu.perinatologia.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.ambulatorio.dao.MamTrgEncInternoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTriagensDAO;
import br.gov.mec.aghu.ambulatorio.service.IAmbulatorioService;
import br.gov.mec.aghu.ambulatorio.vo.LaudoAihVO;
import br.gov.mec.aghu.blococirurgico.service.IBlocoCirurgicoService;
import br.gov.mec.aghu.blococirurgico.vo.SalaCirurgicaVO;
import br.gov.mec.aghu.blococirurgico.vo.TipoAnestesiaVO;
import br.gov.mec.aghu.configuracao.service.IConfiguracaoService;
import br.gov.mec.aghu.configuracao.vo.EquipeVO;
import br.gov.mec.aghu.dominio.DominioGravidez;
import br.gov.mec.aghu.dominio.DominioModoNascimento;
import br.gov.mec.aghu.dominio.DominioRNClassificacaoNascimento;
import br.gov.mec.aghu.dominio.DominioTipoNascimento;
import br.gov.mec.aghu.emergencia.business.MarcarConsultaEmergenciaRN;
import br.gov.mec.aghu.emergencia.dao.MamSituacaoEmergenciaDAO;
import br.gov.mec.aghu.emergencia.producer.QualificadorUsuario;
import br.gov.mec.aghu.internacao.service.IInternacaoService;
import br.gov.mec.aghu.internacao.vo.DadosInternacaoUrgenciaVO;
import br.gov.mec.aghu.model.MamSituacaoEmergencia;
import br.gov.mec.aghu.model.MamTrgEncInterno;
import br.gov.mec.aghu.model.MamTriagens;
import br.gov.mec.aghu.model.McoCesarianas;
import br.gov.mec.aghu.model.McoForcipes;
import br.gov.mec.aghu.model.McoGestacoes;
import br.gov.mec.aghu.model.McoNascIndicacoes;
import br.gov.mec.aghu.model.McoNascimentos;
import br.gov.mec.aghu.model.McoNascimentosId;
import br.gov.mec.aghu.model.McoProfNascsId;
import br.gov.mec.aghu.model.McoRecemNascidos;
import br.gov.mec.aghu.model.McoTrabPartos;
import br.gov.mec.aghu.perinatologia.dao.McoAtendTrabPartosDAO;
import br.gov.mec.aghu.perinatologia.dao.McoGestacoesDAO;
import br.gov.mec.aghu.perinatologia.dao.McoNascimentosDAO;
import br.gov.mec.aghu.perinatologia.dao.McoRecemNascidosDAO;
import br.gov.mec.aghu.perinatologia.dao.McoTrabPartosDAO;
import br.gov.mec.aghu.perinatologia.vo.DadosNascimentoSelecionadoVO;
import br.gov.mec.aghu.perinatologia.vo.DadosNascimentoVO;
import br.gov.mec.aghu.perinatologia.vo.IndicacaoPartoVO;
import br.gov.mec.aghu.registrocolaborador.vo.Usuario;
import br.gov.mec.aghu.util.EmergenciaParametrosEnum;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.commons.NumberUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.StringUtil;
import br.gov.mec.aghu.service.ServiceBusinessException;
import br.gov.mec.aghu.service.ServiceException;

/**
 * @author israel.haas
 */
@Stateless
public class RegistrarGestacaoAbaNascimentoON extends BaseBusiness {

	private static final long serialVersionUID = -3423984755101821178L;
	
	@Inject @QualificadorUsuario
	private Usuario usuario;

	@Inject
	private IAmbulatorioService ambulatorioService;
	
	@Inject
	private IConfiguracaoService configuracaoService;
	
	@Inject
	private IInternacaoService internacaoService;
	
	@Inject
	private IBlocoCirurgicoService blocoCirurgicoService;
	
	@Inject
	private McoNascimentosRN mcoNascimentosRN;
	
	@Inject
	private McoNascIndicacoesRN mcoNascIndicacoesRN;
	
	@Inject
	private McoCesarianasRN mcoCesarianasRN;
	
	@Inject
	private McoForcipesRN mcoForcipesRN;
	
	@Inject
	private McoIntercorrenciaNascsRN mcoIntercorrenciaNascsRN;
	
	@Inject
	private McoProfNascsRN mcoProfNascsRN;
	
	@Inject
	private McoNascimentosDAO mcoNascimentosDAO;
	
	@Inject
	private McoRecemNascidosDAO mcoRecemNascidosDAO;
	
	@Inject
	private McoTrabPartosDAO mcoTrabPartosDAO;
	
	@Inject
	private McoAtendTrabPartosDAO mcoAtendTrabPartosDAO;
	
	@Inject
	private McoGestacoesDAO mcoGestacoesDAO;
	
	@Inject
	private MamTrgEncInternoDAO mamTrgEncInternoDAO;
	
	@Inject
	private MamSituacaoEmergenciaDAO mamSituacaoEmergenciaDAO;
	
	@Inject
	private MamTriagensDAO mamTriagensDAO;
	
	@Inject
	private MarcarConsultaEmergenciaRN marcarConsultaEmergenciaRN;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Override
	protected Log getLogger() {
		return null;
	}
	
	private enum RegistrarGestacaoAbaNascimentoONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_SERVICO_INDISPONIVEL, ERRO_INSERIR_DECISAO_TOMADA, ERRO_DATA_NASCIMENTO_OBRIGATORIO, ERRO_DATA_NASCIMENTO_MAIOR_DATA_ATUAL,
		ERRO_EXISTE_DATA_HORA_NASCIMENTO, ERRO_TIPO_OBRIGATORIO, ERRO_MODO_OBRIGATORIO, ERRO_PESO_PLACENTA, ERRO_CORDAO,
		ERRO_CLASSIFICACAO_OBRIGATORIA, ERRO_REGISTRAR_GRAVIDEZ_NAO_CONFIRMADA_NAO_GRAVIDA, ERRO_GESTACAO_GEMELAR,
		ERRO_GESTACAO_GEMELAR_RECEM_NASCIDO, ERRO_ULTRAPASSOU_GEMELAR, ERRO_RECEM_NASCIDO_TEM_APGAR_DIFERENTE_ZERO,
		ERRO_EXCLUIR_ULTIMO_NASCIMENTO, MENSAGEM_TIPO_ANESTESIA_INDISPONIVEL, MENSAGEM_ERRO_OBTER_PARAMETRO, MENSAGEM_ERRO_TIPO_CAT_OBRIGATORIA,
		MENSAGEM_ERRO_INDICACAO_MAIOR_INICIO, MENSAGEM_ERRO_INICIO_MAIOR_INCISAO, MENSAGEM_ERRO_INCISAO_MAIOR_NASCIMENTO,
		MENSAGEM_ERRO_INDICACAO_MAIOR_NASCIMENTO, MENSAGEM_ERRO_INICIO_MAIOR_NASCIMENTO,MENSAGEM_ERRO_UNIDADE_CARACT_CIRG, 
		MCO_00792, MCO_00794, MCO_00795, MCO_00796, MCO_00797,MCO_00806, MCO_00790, MCO_00805, ERRO_DECISAO_TOMADA_NAO_INFORMADA, MENSAGEM_ERRO_TRABALHO_PARTO
		, MCO_00741, MENSAGEM_ERRO_EQUIPE_NAO_INFORMADA, MENSAGEM_ERRO_CONTAMINACAO_NAO_INFORMADA
		, MENSAGEM_ERRO_GESTACAO_GEMELAR_NASC_INCOMPLETO
		, MENSAGEM_ERRO_ANESTESISTA
		, MCO_00737, MENSAGEM_ERRO_PARAMETRO
	}
	
	public List<DadosNascimentoVO> pesquisarMcoNascimentoPorId(Integer pacCodigo, Short gsoSeqp) throws ApplicationBusinessException {
		
		List<McoNascimentos> listMcoNascimentos = this.mcoNascimentosDAO.pesquisarNascimentosPorGestacao(pacCodigo, gsoSeqp);
		
		List<DadosNascimentoVO> listaRetorno = new ArrayList<DadosNascimentoVO>();
		for (McoNascimentos item : listMcoNascimentos) {
			DadosNascimentoVO dadosNascimentoVO = new DadosNascimentoVO();
			dadosNascimentoVO.setDtHrNascimento(item.getDthrNascimento());
			dadosNascimentoVO.setTipoNascimento(item.getTipo());
			dadosNascimentoVO.setModoNascimento(item.getModo());
			dadosNascimentoVO.setApresentacao(item.getApresentacao());
			dadosNascimentoVO.setClassificacao(item.getRnClassificacao());
			dadosNascimentoVO.setPesoPlacenta(item.getPesoPlacenta());
			dadosNascimentoVO.setCordao(item.getCompCordao()==null ? null : Short.valueOf(item.getCompCordao().shortValue()));
			dadosNascimentoVO.setPesoAborto(item.getPesoNamAbo());
			dadosNascimentoVO.setTanSeq(item.getTanSeq());
			dadosNascimentoVO.setGsoPacCodigo(item.getId().getGsoPacCodigo());
			dadosNascimentoVO.setGsoSeqp(item.getId().getGsoSeqp());
			dadosNascimentoVO.setSeqp(item.getId().getSeqp());
			if (item.getIndEpisotomia() != null) {
				dadosNascimentoVO.setIndEpisotomia(item.getIndEpisotomia());
				
			} else {
				dadosNascimentoVO.setIndEpisotomia(Boolean.FALSE);
			}
			if (item.getPeriodoExpulsivo() != null) {
				dadosNascimentoVO.setPeriodoExpulsivo(this.obterTempoHorarioFormatado(item.getPeriodoExpulsivo()));
			}
			if (item.getPeriodoDilatacao() != null) {
				dadosNascimentoVO.setPeriodoDilatacao(this.obterTempoHorarioFormatado(item.getPeriodoDilatacao()));
			}
			TipoAnestesiaVO anestesia = obterAnestesiaAtiva(item.getTanSeq());
			dadosNascimentoVO.setDescricaoAnestesia(anestesia.getDescricao());
			
			listaRetorno.add(dadosNascimentoVO);
		}
		return listaRetorno;
	}
	
	private String obterTempoHorarioFormatado(Short horario) {
		String substr1 = null;
		String substr2 = null;
		if (horario.toString().length() <= 4) {
			String lpad = StringUtil.adicionaZerosAEsquerda(horario, 4);
			substr1 = lpad.substring(0, 2);
			substr2 = lpad.substring(2, 4);
			
		} else {
			substr1 = horario.toString().substring(0, 3);
			substr2 = horario.toString().substring(3, 5);
		}
		return substr1.concat(":").concat(substr2);
	}
	
	public boolean calcularDuracaoTotalParto(Integer gsoPacCodigo, Short gsoSeqp, String periodoExpulsivo, String periodoDilatacao,
			DadosNascimentoSelecionadoVO nascimentoSelecionado, DadosNascimentoVO nascimento) {

		boolean devePintarDuracaoTotal = false;
		boolean isPeriodoExpulsivoPreenchido = isPeriodoExpulsivoDilatacaoPreenchido(periodoExpulsivo);
		boolean isDilatacaoPreenchido = isPeriodoExpulsivoDilatacaoPreenchido(periodoDilatacao);

		String duracaoTotal = null;

		// Se os campos Período expulsivo e Período Dilatação estiverem preenchidos
		if (isPeriodoExpulsivoPreenchido && isDilatacaoPreenchido) {
			// 1. Realizar a soma entre os valores informados nos campos Período expulsivo e Período Dilatação e setar no campo Duração
			// Total. Exemplo: P. Expulsivo = 01:00 P. Dilatação = 00:00 Duração Total = 01:15. Manter a cor original do campo Duração
			// Total.
			duracaoTotal = this.calcularDuracaoTotalSemColorir(periodoExpulsivo, periodoDilatacao);
			nascimentoSelecionado.setDuracaoTotalParto(duracaoTotal);

			// Se um dos campos Período expulsivo e Período Dilatação não estiverem preenchidos
		} else if (!isPeriodoExpulsivoPreenchido || !isDilatacaoPreenchido) {
			// 1. Executa consulta C6
			// 2. Se encontrar resultados e o valor do campo DTHR_ATEND for menor que o valor do campo DTHR_NASCIMENTO para o registro
			// corrente realizar a diferença em horas e minutos de MCO_NASCIMENTOS.DTHR_NASCIMENTO e MCO_ATEND_TRAB_PARTOS.DTHR_ATEND e
			// setar o valor resultante no campo Duração Total.
			duracaoTotal = this.calcularDuracaoTotalColorir(gsoPacCodigo, gsoSeqp, nascimentoSelecionado.getMcoNascimento()
					.getDthrNascimento());
			nascimentoSelecionado.setDuracaoTotalParto(duracaoTotal);
			// 2. Pintar o campo Duração Total de azul.
			devePintarDuracaoTotal = true;
		}
		
		if (isPeriodoExpulsivoPreenchido) {
			// Se a duração total já tiver sido calculada e o valor do campo Período Expulsivo for diferente de vazio
			if (duracaoTotal != null) {
				// 1. Realizar a diferença entre o valor do campo Duração Total e o Período Expulsivo. Setar o valor resultante no campo Período Dilatação.
				nascimento.setPeriodoDilatacao(this.calcularPeriodoDilatacao(periodoExpulsivo, duracaoTotal));
				// Pintar o campo Duração Total de azul.
				devePintarDuracaoTotal = true;
			}

			// Se o MCO_NASCIMENTOS.TIPO for igual a PARTO
			// 1. Realizar a diferença entre MCO.NASCIMENTOS.DTHR_NASCIMENTO corrente e o valor do campo Período Expulsivo e setar no campo
			// Início do fieldSet Agendamento
			nascimentoSelecionado.setDthrInicioProcedimento(this.calcularHoraInicioProcedimento(nascimentoSelecionado.getMcoNascimento()
					.getDthrNascimento(), periodoExpulsivo));

			// 2. Setar o valor do campo Período Expulsivo no campo Tempo do fieldSet Agendamento
			nascimentoSelecionado.setTempoProcedimento(periodoExpulsivo);
		}

		return devePintarDuracaoTotal;
	}
	
	private boolean isPeriodoExpulsivoDilatacaoPreenchido(String periodo) {
		return periodo != null && !periodo.isEmpty()
				&& periodo.length() >= 4;
	}
	
	private String calcularDuracaoTotalSemColorir(String periodoExpulsivo, String periodoDilatacao) {
		String[] arrayHorasMinutosExpulsivo = periodoExpulsivo.split(":");
		Short horasExpulsivo = Short.valueOf(arrayHorasMinutosExpulsivo[0]);
		Short minutosExpulsivo = Short.valueOf(arrayHorasMinutosExpulsivo[1]);
		String[] arrayHorasMinutosDilatacao = periodoDilatacao.split(":");
		Short horasDilatacao = Short.valueOf(arrayHorasMinutosDilatacao[0]);
		Short minutosDilatacao = Short.valueOf(arrayHorasMinutosDilatacao[1]);
		Short horasFinal = (short) (horasExpulsivo + horasDilatacao);
		Short minutosFinal = (short) (minutosExpulsivo + minutosDilatacao);
		final double divisao = minutosFinal / 60;
		BigDecimal truncHoras = NumberUtil.truncate(new BigDecimal(divisao), 0);
		horasFinal = (short) (horasFinal + truncHoras.shortValue());
		String lpadHoras = null;
		if (horasFinal.toString().length() == 1) {
			lpadHoras = StringUtil.adicionaZerosAEsquerda(horasFinal, 2);
		} else {
			lpadHoras = horasFinal.toString();
		}
		
		final double restoDivisao = minutosFinal % 60;
		BigDecimal truncMinutos = NumberUtil.truncate(new BigDecimal(restoDivisao), 0);
		String lpadMinutos = null;
		if (truncMinutos.toString().length() == 1) {
			lpadMinutos = StringUtil.adicionaZerosAEsquerda(truncMinutos, 2);
		} else {
			lpadMinutos = truncMinutos.toString();
		}
		return lpadHoras.concat(":").concat(lpadMinutos);
	}
	
	private String calcularPeriodoDilatacao(String periodoExpulsivo, String duracaoTotalParto) {
		String[] arrayHorasPeriodoExpusivo = periodoExpulsivo.split(":");
		Short horasPeriodoExpusivo = Short.valueOf(arrayHorasPeriodoExpusivo[0]);
		Short minutosPeriodoExpusivo = Short.valueOf(arrayHorasPeriodoExpusivo[1]);
		Integer totalMinutosPeriodoExpulsivo = (horasPeriodoExpusivo * 60) + minutosPeriodoExpusivo;
		String[] arrayHorasMinutosDuracaoTotal = duracaoTotalParto.split(":");
		Short horasDuracaoTotal = Short.valueOf(arrayHorasMinutosDuracaoTotal[0]);
		Short minutosDuracaoTotal = Short.valueOf(arrayHorasMinutosDuracaoTotal[1]);
		Integer totalMinutosDuracaoTotal = (horasDuracaoTotal * 60) + minutosDuracaoTotal;
		Integer diferencaMinutos = totalMinutosDuracaoTotal - totalMinutosPeriodoExpulsivo;
		final double divisao = diferencaMinutos / 60;
		BigDecimal truncHoras = NumberUtil.truncate(new BigDecimal(divisao), 0);
		String lpadHoras = null;
		if (truncHoras.toString().length() == 1) {
			lpadHoras = StringUtil.adicionaZerosAEsquerda(truncHoras, 2);
			
		} else {
			lpadHoras = truncHoras.toString();
		}
		
		final double restoDivisao = diferencaMinutos % 60;
		BigDecimal truncMinutos = NumberUtil.truncate(new BigDecimal(restoDivisao), 0);
		
		String lpadMinutos = null;
		if (truncMinutos.toString().length() == 1) {
			lpadMinutos = StringUtil.adicionaZerosAEsquerda(truncMinutos, 2);
		} else {
			lpadMinutos = truncMinutos.toString();
		}
		return lpadHoras.concat(":").concat(lpadMinutos);
	}
	
	private String calcularDuracaoTotalColorir(Integer gsoPacCodigo, Short gsoSeqp, Date dataHoraNascimento) {
		// 1. Executa consulta C6
		List<Date> listaDthrAtendPorGestacao = this.mcoAtendTrabPartosDAO.listarDthrAtendPorGestacao(gsoSeqp, gsoPacCodigo);
		if (listaDthrAtendPorGestacao != null && !listaDthrAtendPorGestacao.isEmpty()) {
			Date dataHoraAtendimento = listaDthrAtendPorGestacao.get(0);
			if (DateUtil.validaDataMenor(dataHoraAtendimento, dataHoraNascimento)) {
				Integer horas = null;
				Integer minutos = DateUtil.obterQtdMinutosEntreDuasDatas(dataHoraAtendimento, dataHoraNascimento);
				final double divisao = minutos / 60;
				BigDecimal truncHoras = NumberUtil.truncate(new BigDecimal(divisao), 0);
				horas = truncHoras.intValue();
				final double restoDivisao = minutos % 60;
				BigDecimal truncMinutos = NumberUtil.truncate(new BigDecimal(restoDivisao), 0);
				minutos = truncMinutos.intValue();
				Short duracaoTotal = Short.valueOf(horas.toString().concat(minutos.toString()));
				return this.obterTempoHorarioFormatado(duracaoTotal);
			}
		}
		return null;
	}
	
	private Date calcularHoraInicioProcedimento(Date dthrNascimento, String periodoExpulsivo) {
		String[] arrayHorasPeriodoExpusivo = periodoExpulsivo.split(":");
		Short horasPeriodoExpusivo = Short.valueOf(arrayHorasPeriodoExpusivo[0]);
		Short minutosPeriodoExpusivo = Short.valueOf(arrayHorasPeriodoExpusivo[1]);
		Integer totalMinutosPeriodoExpulsivo = (horasPeriodoExpusivo * 60) + minutosPeriodoExpusivo;
		return DateUtil.adicionaMinutos(dthrNascimento, -totalMinutosPeriodoExpulsivo);
	}
	
	public DadosNascimentoSelecionadoVO obterDadosNascimentoSelecionado(Integer seqp, Integer gsoPacCodigo,
			Short gsoSeqp) throws ApplicationBusinessException {
		McoNascimentos nascimento = this.mcoNascimentosDAO.obterDadosNascimentoSelecionado(seqp, gsoPacCodigo, gsoSeqp);
		DadosNascimentoSelecionadoVO dadosNascimento = new DadosNascimentoSelecionadoVO();
		dadosNascimento.setMcoNascimento(nascimento);
		if (nascimento.getMcoCesarianas() != null) {
			dadosNascimento.setMcoCesariana(nascimento.getMcoCesarianas());
		} else {
			dadosNascimento.setMcoCesariana(new McoCesarianas());
		}
		if (nascimento.getMcoForcipes() != null) {
			dadosNascimento.setMcoForcipe(nascimento.getMcoForcipes());
		} else {
			dadosNascimento.setMcoForcipe(new McoForcipes());
		}
		if (dadosNascimento.getMcoCesariana().getHrDuracao() != null) {
			dadosNascimento.setHrDuracaoFormatado(this.obterTempoHorarioFormatado(dadosNascimento.getMcoCesariana().getHrDuracao()));
		}
		return dadosNascimento;
	}
	
	public TipoAnestesiaVO obterAnestesiaAtiva(Short tanSeq) throws ApplicationBusinessException {
		TipoAnestesiaVO retorno = null;
		try {
			retorno = this.blocoCirurgicoService.pequisarTiposAnestesiasAtivas(tanSeq, null).get(0);
			
		} catch (RuntimeException e) {
			throw new ApplicationBusinessException(RegistrarGestacaoAbaNascimentoONExceptionCode.MENSAGEM_TIPO_ANESTESIA_INDISPONIVEL);
		}
		return retorno;
	}
	
	public SalaCirurgicaVO obterDadosSalaCirurgica(Short sciUnfSeq, Short sciSeqp) throws ApplicationBusinessException {
		SalaCirurgicaVO retorno = null;
		try {
			retorno = this.blocoCirurgicoService.obterDadosSalaCirurgica(sciUnfSeq, sciSeqp);
			
		} catch (RuntimeException e) {
			throw new ApplicationBusinessException(RegistrarGestacaoAbaNascimentoONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
		return retorno;
	}
	
	public List<EquipeVO> pesquisarEquipeAtivaCO(Object param) throws ApplicationBusinessException {
		List<EquipeVO> listRetorno = null;
		String strParametro = (String) param;
		try {
			listRetorno = this.configuracaoService.pesquisarEquipeAtivaCO(strParametro);
			
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(RegistrarGestacaoAbaNascimentoONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		} catch (RuntimeException e) {
			throw new ApplicationBusinessException(RegistrarGestacaoAbaNascimentoONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
		return listRetorno;
	}
	
	public Long pesquisarEquipeAtivaCOCount(Object param) throws ApplicationBusinessException {
		Long retorno = null;
		String strPesquisa = (String) param;
		try {
			retorno = this.configuracaoService.pesquisarEquipeAtivaCOCount(strPesquisa);
			
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(RegistrarGestacaoAbaNascimentoONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		} catch (RuntimeException e) {
			throw new ApplicationBusinessException(RegistrarGestacaoAbaNascimentoONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
		return retorno;
	}
	
	public EquipeVO obterEquipePorId(Short eqpSeq) throws ApplicationBusinessException {
		EquipeVO retorno = null;
		try {
			retorno = this.configuracaoService.pesquisarEquipeAtivaCO(eqpSeq.toString()).get(0);
			
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(RegistrarGestacaoAbaNascimentoONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		} catch (RuntimeException e) {
			throw new ApplicationBusinessException(RegistrarGestacaoAbaNascimentoONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
		return retorno;
	}
	
	public Date obterDtHrNascimento(Integer pacCodigo, Short gsoSeqp) {
		List<McoRecemNascidos> listRecemNascidos = this.mcoRecemNascidosDAO.listarRecemNascidosSemRegistro(pacCodigo, gsoSeqp);
		if (!listRecemNascidos.isEmpty()) {
			return listRecemNascidos.get(0).getDthrNascimento();
		}
		return null;
	}
	
	public void validarTipoNascimento(Integer pacCodigo, Short seqp) throws ApplicationBusinessException {
		McoTrabPartos trabPartos = this.mcoTrabPartosDAO.obterMcoTrabPartosPorId(pacCodigo, seqp);
		if (trabPartos == null || trabPartos.getTipoParto() == null) {
			throw new ApplicationBusinessException(RegistrarGestacaoAbaNascimentoONExceptionCode.ERRO_INSERIR_DECISAO_TOMADA);
		}
	}
	
	public void gravarDadosAbaNascimento(Integer conNumero, String hostName, DadosNascimentoVO nascimentoSelecionado,
			DadosNascimentoSelecionadoVO dadosNascimentoSelecionado,
			DadosNascimentoSelecionadoVO dadosNascimentoSelecionadoOriginal) throws ApplicationBusinessException {
		validarPartoInstrumentado(nascimentoSelecionado, dadosNascimentoSelecionado);
		validarCesariana(nascimentoSelecionado, dadosNascimentoSelecionado);
		
		if (nascimentoSelecionado.getModoNascimento().equals(DominioModoNascimento.F)) {
			if (dadosNascimentoSelecionado.getMcoForcipe().getId() == null) {
				incluirMcoForcipe(nascimentoSelecionado, dadosNascimentoSelecionado, dadosNascimentoSelecionadoOriginal);
				
			} else {
				atualizarMcoForcipe(dadosNascimentoSelecionado, dadosNascimentoSelecionadoOriginal);
			}
		}
		
		if (nascimentoSelecionado.getTipoNascimento().equals(DominioTipoNascimento.C)) {
			if (dadosNascimentoSelecionado.getMcoCesariana().getId() == null) {
				incluirMcoCesariana(nascimentoSelecionado, dadosNascimentoSelecionado, dadosNascimentoSelecionadoOriginal);
				
			} else {
				atualizarMcoCesariana(dadosNascimentoSelecionado, dadosNascimentoSelecionadoOriginal);
			}
		}
		this.atualizarMcoNascimento(dadosNascimentoSelecionado, dadosNascimentoSelecionadoOriginal);
		if (dadosNascimentoSelecionado.getMcoNascimento().getEqpSeq() != null) {
			EquipeVO equipe = this.obterEquipePorId(dadosNascimentoSelecionado.getMcoNascimento().getEqpSeq());
			this.atualizarEquipeInternacao(conNumero, hostName, equipe);
		}
	}

	private void incluirMcoForcipe(DadosNascimentoVO nascimentoSelecionado, DadosNascimentoSelecionadoVO dadosNascimentoSelecionado,
			DadosNascimentoSelecionadoVO dadosNascimentoSelecionadoOriginal) throws ApplicationBusinessException {
		
		if (!dadosNascimentoSelecionado.getListaNascIndicacoesForcipeRemover().isEmpty()) {
			for (McoNascIndicacoes item : dadosNascimentoSelecionado.getListaNascIndicacoesForcipeRemover()) {
				this.mcoNascIndicacoesRN.excluirNascIndicacoesForcipe(item);
			}
		}
		if (dadosNascimentoSelecionado.getMcoForcipesExcluir() != null) {
			this.mcoForcipesRN.excluirMcoForcipes(dadosNascimentoSelecionado.getMcoForcipesExcluir(),
					dadosNascimentoSelecionadoOriginal.getMcoForcipe());
		}
		
		dadosNascimentoSelecionado.setMcoForcipe(
				this.mcoForcipesRN.inserirMcoForcipes(dadosNascimentoSelecionado.getMcoForcipe(), nascimentoSelecionado));
		
		for (IndicacaoPartoVO item : dadosNascimentoSelecionado.getListaNascIndicacoesForcipe()) {
			this.mcoNascIndicacoesRN.inserirMcoNascIndicacoesForcipes(
					item.getMcoNascIndicacoes(), dadosNascimentoSelecionado.getMcoForcipe());
		}
	}
	
	private void atualizarMcoForcipe(DadosNascimentoSelecionadoVO dadosNascimentoSelecionado,
			DadosNascimentoSelecionadoVO dadosNascimentoSelecionadoOriginal) throws ApplicationBusinessException {
		
		if (!dadosNascimentoSelecionado.getListaNascIndicacoesForcipeRemover().isEmpty()) {
			for (McoNascIndicacoes item : dadosNascimentoSelecionado.getListaNascIndicacoesForcipeRemover()) {
				this.mcoNascIndicacoesRN.excluirNascIndicacoesForcipe(item);
			}
		}
		for (IndicacaoPartoVO item : dadosNascimentoSelecionado.getListaNascIndicacoesForcipe()) {
			if (item.getMcoNascIndicacoes().getSeq() == null) {
				this.mcoNascIndicacoesRN.inserirMcoNascIndicacoesForcipes(
						item.getMcoNascIndicacoes(), dadosNascimentoSelecionado.getMcoForcipe());
			}
		}
		this.mcoForcipesRN.atualizarMcoForcipes(dadosNascimentoSelecionado.getMcoForcipe(),
				dadosNascimentoSelecionadoOriginal.getMcoForcipe());
	}
	
	private void incluirMcoCesariana(DadosNascimentoVO nascimentoSelecionado, DadosNascimentoSelecionadoVO dadosNascimentoSelecionado,
			DadosNascimentoSelecionadoVO dadosNascimentoSelecionadoOriginal) throws ApplicationBusinessException {
		
		if (!dadosNascimentoSelecionado.getListaNascIndicacoesCesarianaRemover().isEmpty()) {
			for (McoNascIndicacoes item : dadosNascimentoSelecionado.getListaNascIndicacoesCesarianaRemover()) {
				this.mcoNascIndicacoesRN.excluirNascIndicacoesCesariana(item);
			}
		}
		if (dadosNascimentoSelecionado.getMcoCesarianaExcluir() != null) {
			this.mcoCesarianasRN.excluirMcoCesarianas(dadosNascimentoSelecionado.getMcoCesarianaExcluir(),
					dadosNascimentoSelecionadoOriginal.getMcoCesariana());
		}
		
		dadosNascimentoSelecionado.setMcoCesariana(
				this.mcoCesarianasRN.inserirMcoCesarianas(dadosNascimentoSelecionado.getMcoCesariana(),
				dadosNascimentoSelecionado.getMcoNascimento(), nascimentoSelecionado));
		
		for (IndicacaoPartoVO item : dadosNascimentoSelecionado.getListaNascIndicacoesCesariana()) {
			this.mcoNascIndicacoesRN.inserirMcoNascIndicacoesCesarianas(
					item.getMcoNascIndicacoes(), dadosNascimentoSelecionado.getMcoCesariana());
		}
	}
	
	private void atualizarMcoCesariana(DadosNascimentoSelecionadoVO dadosNascimentoSelecionado,
			DadosNascimentoSelecionadoVO dadosNascimentoSelecionadoOriginal) throws ApplicationBusinessException {
		
		if (!dadosNascimentoSelecionado.getListaNascIndicacoesCesarianaRemover().isEmpty()) {
			for (McoNascIndicacoes item : dadosNascimentoSelecionado.getListaNascIndicacoesCesarianaRemover()) {
				this.mcoNascIndicacoesRN.excluirNascIndicacoesCesariana(item);
			}
		}
		for (IndicacaoPartoVO item : dadosNascimentoSelecionado.getListaNascIndicacoesCesariana()) {
			if (item.getMcoNascIndicacoes().getSeq() == null) {
				this.mcoNascIndicacoesRN.inserirMcoNascIndicacoesCesarianas(
						item.getMcoNascIndicacoes(), dadosNascimentoSelecionado.getMcoCesariana());
			}
		}
		this.mcoCesarianasRN.atualizarMcoCesarianas(dadosNascimentoSelecionado.getMcoCesariana(),
				dadosNascimentoSelecionadoOriginal.getMcoCesariana(), dadosNascimentoSelecionado.getMcoNascimento());
	}
	
	private void atualizarMcoNascimento(DadosNascimentoSelecionadoVO dadosNascimentoSelecionado,
			DadosNascimentoSelecionadoVO dadosNascimentoSelecionadoOriginal) {
		this.mcoNascimentosRN.atualizarMcoNascimento(dadosNascimentoSelecionado.getMcoNascimento(),
				dadosNascimentoSelecionadoOriginal.getMcoNascimento());
	}
	
	private void atualizarEquipeInternacao(Integer conNumero, String hostName, EquipeVO equipe)
			throws ApplicationBusinessException {
		try {
			Integer seqAtendimento = this.internacaoService.obterSeqAtendimentoUrgenciaPorConsulta(conNumero);
			if (seqAtendimento != null) {
				DadosInternacaoUrgenciaVO internacaoUrgencia = this.internacaoService.obterInternacaoPorAtendimentoUrgencia(seqAtendimento);
				if (internacaoUrgencia != null) {
					if (!CoreUtil.igual(internacaoUrgencia.getMatriculaProfessor(), equipe.getSerMatricula())
							|| !CoreUtil.igual(internacaoUrgencia.getVinCodigoProfessor(), equipe.getSerVinCodigo())) {
						
						this.internacaoService.atualizarServidorProfessorInternacao(internacaoUrgencia.getSeq(),
								equipe.getSerMatricula(), equipe.getSerVinCodigo(),
								hostName, usuario.getMatricula(), usuario.getVinculo());
					}
				}
			}
			
		} catch (ServiceException e) {
			return;
		} catch (RuntimeException e) {
			return;
		}
	}
	
	private void validarPartoInstrumentado(DadosNascimentoVO nascimentoSelecionado,
			DadosNascimentoSelecionadoVO dadosNascimentoSelecionado) throws ApplicationBusinessException {
		
		if ((nascimentoSelecionado.getTipoNascimento().equals(DominioTipoNascimento.P)
				|| nascimentoSelecionado.getTipoNascimento().equals(DominioTipoNascimento.C))
				&& nascimentoSelecionado.getModoNascimento().equals(DominioModoNascimento.F)) {
			
			if (!dadosNascimentoSelecionado.getListaNascIndicacoesForcipe().isEmpty()
					&& (dadosNascimentoSelecionado.getMcoForcipe().getTipoForcipe() == null
					|| dadosNascimentoSelecionado.getMcoForcipe().getTamanhoForcipe() == null)) {
				
				throw new ApplicationBusinessException(RegistrarGestacaoAbaNascimentoONExceptionCode.MENSAGEM_ERRO_TIPO_CAT_OBRIGATORIA);
			}
		}
	}
	
	private void validarCesariana(DadosNascimentoVO nascimentoSelecionado,
			DadosNascimentoSelecionadoVO dadosNascimentoSelecionado) throws ApplicationBusinessException {
		
		if (nascimentoSelecionado.getTipoNascimento().equals(DominioTipoNascimento.C)) {
			McoCesarianas cesariana = dadosNascimentoSelecionado.getMcoCesariana();
			McoNascimentos nascimento = dadosNascimentoSelecionado.getMcoNascimento();
			
			if (DateUtil.validaDataMenor(cesariana.getDthrIndicacao(), cesariana.getDthrPrevInicio())) {
				throw new ApplicationBusinessException(RegistrarGestacaoAbaNascimentoONExceptionCode.MENSAGEM_ERRO_INDICACAO_MAIOR_INICIO);
			}
			if (DateUtil.validaDataMenorIgual(cesariana.getDthrPrevInicio(), cesariana.getDthrIncisao())) {
				throw new ApplicationBusinessException(RegistrarGestacaoAbaNascimentoONExceptionCode.MENSAGEM_ERRO_INICIO_MAIOR_INCISAO);
			}
			if (DateUtil.validaDataMenor(cesariana.getDthrIncisao(), nascimento.getDthrNascimento())) {
				throw new ApplicationBusinessException(RegistrarGestacaoAbaNascimentoONExceptionCode.MENSAGEM_ERRO_INCISAO_MAIOR_NASCIMENTO);
			}
			if (DateUtil.validaDataMenor(cesariana.getDthrIndicacao(), nascimento.getDthrNascimento())) {
				throw new ApplicationBusinessException(RegistrarGestacaoAbaNascimentoONExceptionCode.MENSAGEM_ERRO_INDICACAO_MAIOR_NASCIMENTO);
			}
			if (DateUtil.validaDataMenor(cesariana.getDthrPrevInicio(), nascimento.getDthrNascimento())) {
				throw new ApplicationBusinessException(RegistrarGestacaoAbaNascimentoONExceptionCode.MENSAGEM_ERRO_INICIO_MAIOR_NASCIMENTO);
			}
		}
	}
	
	public boolean preGravarNascimento(Integer pacCodigo, Short gsoSeqp, DadosNascimentoVO nascimento) throws ApplicationBusinessException {
		
		boolean exibirModalDtHrNascimento = Boolean.FALSE;
		boolean isEdicaoComMesmaDtHrNascimento = Boolean.FALSE;
		
		if (nascimento.getDtHrNascimento() == null) {
			throw new ApplicationBusinessException(RegistrarGestacaoAbaNascimentoONExceptionCode.ERRO_DATA_NASCIMENTO_OBRIGATORIO);
			
		}else if (nascimento.getSeqp() != null) {				
			McoNascimentos nascimentoOriginal  = this.mcoNascimentosDAO.obterMcoNascimento(nascimento.getSeqp(), nascimento.getGsoPacCodigo(), nascimento.getGsoSeqp());			
			if(nascimentoOriginal.getDthrNascimento().getTime() == nascimento.getDtHrNascimento().getTime()){
				isEdicaoComMesmaDtHrNascimento = Boolean.TRUE;
			}
		}
		
		if (nascimento.getDtHrNascimento() == null) {
			throw new ApplicationBusinessException(RegistrarGestacaoAbaNascimentoONExceptionCode.ERRO_DATA_NASCIMENTO_OBRIGATORIO);
			
		} else if (DateUtil.validaDataMaior(nascimento.getDtHrNascimento(), new Date())) {
			throw new ApplicationBusinessException(RegistrarGestacaoAbaNascimentoONExceptionCode.ERRO_DATA_NASCIMENTO_MAIOR_DATA_ATUAL);
			
		}else if (!isEdicaoComMesmaDtHrNascimento && this.mcoNascimentosDAO.verificaExisteNascimentoPorDtHrNascimento(pacCodigo, gsoSeqp, nascimento.getDtHrNascimento(), true)) {
			throw new ApplicationBusinessException(RegistrarGestacaoAbaNascimentoONExceptionCode.ERRO_EXISTE_DATA_HORA_NASCIMENTO);
			
		}  else if (DateUtil.getHoras(nascimento.getDtHrNascimento()) == 0 && DateUtil.getMinutos(nascimento.getDtHrNascimento()) == 0) {
			exibirModalDtHrNascimento = true;
		}
		return exibirModalDtHrNascimento;
	}
	
	public DadosNascimentoVO gravarNascimento(Integer pacCodigo, Short gsoSeqp, DadosNascimentoVO nascimentoVO,
			Short seqAnestesia) throws ApplicationBusinessException {
		
		if (nascimentoVO.getSeqp() != null) {
			McoNascimentosId id = new McoNascimentosId(pacCodigo, gsoSeqp, nascimentoVO.getSeqp());
			McoNascimentos nascimentoOriginal = this.mcoNascimentosDAO.obterOriginal(id);
			
			validarCamposNascimento(pacCodigo, gsoSeqp, nascimentoVO);
			validarPreAlteracao(pacCodigo, gsoSeqp, nascimentoVO);
			validarCamposAlteracao(pacCodigo, gsoSeqp, nascimentoVO, nascimentoOriginal);
			
			this.mcoNascimentosRN.atualizarNascimentos(nascimentoVO, pacCodigo, gsoSeqp, seqAnestesia);
			return nascimentoVO;
			
		} else {
			validarCamposNascimento(pacCodigo, gsoSeqp, nascimentoVO);
			validarPreInclusao(pacCodigo, gsoSeqp, nascimentoVO);
			Integer seqpNascimento = this.obterSeqpNascimento(pacCodigo, gsoSeqp, nascimentoVO);
			
			DadosNascimentoVO nascimentoIncluido = this.mcoNascimentosRN
					.inserirNascimentos(nascimentoVO, pacCodigo, gsoSeqp, seqpNascimento, seqAnestesia);
			return nascimentoIncluido;
		}
	}

	private void validarCamposNascimento(Integer pacCodigo, Short gsoSeqp,
			DadosNascimentoVO nascimento) throws ApplicationBusinessException {
		
		if (nascimento.getTipoNascimento() == null) {
			throw new ApplicationBusinessException(RegistrarGestacaoAbaNascimentoONExceptionCode.ERRO_TIPO_OBRIGATORIO);
		} else {
			validarTipoNascimento(pacCodigo, gsoSeqp);
		}
		if (nascimento.getModoNascimento() == null) {
			throw new ApplicationBusinessException(RegistrarGestacaoAbaNascimentoONExceptionCode.ERRO_MODO_OBRIGATORIO);
		}
		if (nascimento.getPesoPlacenta() != null && (nascimento.getPesoPlacenta() < 100 || nascimento.getPesoPlacenta() > 1000)) {
			throw new ApplicationBusinessException(RegistrarGestacaoAbaNascimentoONExceptionCode.ERRO_PESO_PLACENTA);
		}
		if (nascimento.getCordao() != null && (nascimento.getCordao() < 0 || nascimento.getCordao() > 300)) {
			throw new ApplicationBusinessException(RegistrarGestacaoAbaNascimentoONExceptionCode.ERRO_CORDAO);
		}
		if (nascimento.getClassificacao() == null) {
			throw new ApplicationBusinessException(RegistrarGestacaoAbaNascimentoONExceptionCode.ERRO_CLASSIFICACAO_OBRIGATORIA);
			
		} else if (nascimento.getClassificacao().equals(DominioRNClassificacaoNascimento.NAV)) {
			nascimento.setPesoAborto(null);
		}
		McoGestacoes gestacao = this.mcoGestacoesDAO.pesquisarMcoGestacaoPorId(pacCodigo, gsoSeqp);
		if (!gestacao.getGravidez().equals(DominioGravidez.GCO)) {
			throw new ApplicationBusinessException(RegistrarGestacaoAbaNascimentoONExceptionCode
					.ERRO_REGISTRAR_GRAVIDEZ_NAO_CONFIRMADA_NAO_GRAVIDA);
		}
	}
	
	private void validarPreInclusao(Integer pacCodigo, Short gsoSeqp,
			DadosNascimentoVO nascimento) throws ApplicationBusinessException {
		
		String gemelar = this.mcoGestacoesDAO.obterGemelarPorPaciente(pacCodigo, gsoSeqp);
		
		if (gemelar == null || gemelar.equals("N")) {
			if (this.mcoNascimentosDAO.verificaExisteNascimentoPorDtHrNascimento(pacCodigo, gsoSeqp, nascimento.getDtHrNascimento(), false)) {
				throw new ApplicationBusinessException(RegistrarGestacaoAbaNascimentoONExceptionCode.ERRO_GESTACAO_GEMELAR);
			}
			if (this.mcoRecemNascidosDAO.verificaExisteRecemNascidoPorDtHrNascimento(pacCodigo, gsoSeqp, nascimento.getDtHrNascimento(), false)) {
				throw new ApplicationBusinessException(RegistrarGestacaoAbaNascimentoONExceptionCode.ERRO_GESTACAO_GEMELAR_RECEM_NASCIDO);
				
			}
			
		} else {
			if (!this.mcoRecemNascidosDAO.verificaExisteRecemNascidoPorDtHrNascimento(pacCodigo, gsoSeqp,
					nascimento.getDtHrNascimento(), true)) {
				
				Set<Date> listaNascidosRecemNascidos = this.mcoRecemNascidosDAO
						.obterRegistrosNascidosRecemNascidos(pacCodigo, gsoSeqp);
				
				if (listaNascidosRecemNascidos != null && listaNascidosRecemNascidos.size() >= Integer.valueOf(gemelar)) {
					throw new ApplicationBusinessException(RegistrarGestacaoAbaNascimentoONExceptionCode.ERRO_ULTRAPASSOU_GEMELAR);
				}
			}
		}
	}
	
	private void validarPreAlteracao(Integer pacCodigo, Short gsoSeqp,
			DadosNascimentoVO nascimento) throws ApplicationBusinessException {
		
		if (nascimento.getClassificacao().equals(DominioRNClassificacaoNascimento.NAM)
				|| nascimento.getClassificacao().equals(DominioRNClassificacaoNascimento.ABO)) {
			
			McoRecemNascidos recemNascidos = this.mcoRecemNascidosDAO.obterMcoRecemNascidosPorId(pacCodigo, gsoSeqp, nascimento.getSeqp());
			if (recemNascidos != null && recemNascidos.getApgar1() != null && recemNascidos.getApgar1() > 0) {
				throw new ApplicationBusinessException(RegistrarGestacaoAbaNascimentoONExceptionCode.ERRO_RECEM_NASCIDO_TEM_APGAR_DIFERENTE_ZERO);
			}
		}
	}
	
	private void validarCamposAlteracao(Integer pacCodigo, Short gsoSeqp,
			DadosNascimentoVO nascimento, McoNascimentos nascimentoOriginal) throws ApplicationBusinessException {
		
		if (CoreUtil.modificados(nascimento.getTipoNascimento(), nascimentoOriginal.getTipo())) {
			validarAlteracaoTipoNascimento(pacCodigo, gsoSeqp, nascimento, nascimentoOriginal);
		}
		if (CoreUtil.modificados(nascimento.getModoNascimento(), nascimentoOriginal.getModo())) {
			validarAlteracaoModoNascimento(pacCodigo, gsoSeqp, nascimento, nascimentoOriginal);
		}
	}
	
	private void validarAlteracaoTipoNascimento(Integer pacCodigo, Short gsoSeqp, DadosNascimentoVO nascimento,
			McoNascimentos nascimentoOriginal) throws ApplicationBusinessException {
		
		Integer seqpNascimento = nascimento.getSeqp();
		if (nascimentoOriginal.getTipo().equals(DominioTipoNascimento.C)) {
			this.mcoNascIndicacoesRN.excluirMcoNascIndicacoesCesarea(pacCodigo, gsoSeqp, seqpNascimento);
			this.mcoCesarianasRN.excluirMcoCesarianasPorId(pacCodigo, gsoSeqp, seqpNascimento);
			
		} else {
			McoNascimentosId id = new McoNascimentosId(pacCodigo, gsoSeqp, seqpNascimento);
			McoNascimentos mcoNascimento = this.mcoNascimentosDAO.obterPorChavePrimaria(id);
			mcoNascimento.setPeriodoDilatacao(null);
			mcoNascimento.setPeriodoExpulsivo(null);
			mcoNascimento.setIndEpisotomia(null);
			this.mcoNascimentosRN.atualizarMcoNascimento(mcoNascimento, nascimentoOriginal);
		}
	}
	
	private void validarAlteracaoModoNascimento(Integer pacCodigo, Short gsoSeqp, DadosNascimentoVO nascimento,
			McoNascimentos nascimentoOriginal) throws ApplicationBusinessException {
		
		Integer seqpNascimento = nascimento.getSeqp();
		if (nascimentoOriginal.getModo().equals(DominioModoNascimento.F)) {
			this.mcoNascIndicacoesRN.excluirMcoNascIndicacoesForcipe(pacCodigo, gsoSeqp, seqpNascimento);
			this.mcoForcipesRN.excluirMcoForcipesPorId(pacCodigo, gsoSeqp, seqpNascimento);
		}
	}
	
	private Integer obterSeqpNascimento(Integer pacCodigo, Short gsoSeqp, DadosNascimentoVO nascimento) {
		Integer seqpNascimento = null;
		
		if (this.mcoRecemNascidosDAO.verificaExisteRecemNascido(pacCodigo, gsoSeqp)) {
			McoRecemNascidos recemNascido = this.mcoRecemNascidosDAO
					.obterRecemNascidoPorDtHrNascimento(pacCodigo, gsoSeqp, nascimento.getDtHrNascimento());
			
			if (recemNascido != null) {
				nascimento.setSeqp(recemNascido.getId().getSeqp().intValue());
				
			} else {
				seqpNascimento = this.mcoNascimentosDAO.obterMaxSeqpMcoNascimentos(pacCodigo, gsoSeqp) + 1;
				
				McoRecemNascidos recemNascidoPorId = this.mcoRecemNascidosDAO
						.obterRecemNascidoPorIdEDtHrNascimento(pacCodigo, gsoSeqp, seqpNascimento, nascimento.getDtHrNascimento());
				
				if (recemNascidoPorId == null) {
					// Feito exatamente como está no DOC.
					Byte maxSeqpRecemNascido = this.mcoRecemNascidosDAO.obterMaxSeqpMcoRecemNascidos(pacCodigo, gsoSeqp);
					Integer maxSeqpNascimento = this.mcoNascimentosDAO.obterMaxSeqpMcoNascimentos(pacCodigo, gsoSeqp);
					
					if (maxSeqpRecemNascido > maxSeqpNascimento || maxSeqpRecemNascido < maxSeqpNascimento) {
							seqpNascimento = maxSeqpNascimento + 1;
							nascimento.setSeqp(seqpNascimento);
							
					} else {
						seqpNascimento = maxSeqpNascimento + 1;
						nascimento.setSeqp(seqpNascimento);
					}
				}
			}
		} else {
			seqpNascimento = this.mcoNascimentosDAO.obterMaxSeqpMcoNascimentos(pacCodigo, gsoSeqp) + 1;
			nascimento.setSeqp(seqpNascimento);
		}
		return seqpNascimento;
	}
	
	public void excluirNascimento(DadosNascimentoVO nascimentoExcluir, Integer gsoPacCodigo,
			Short gsoSeqp) throws ApplicationBusinessException {
		
		if (!nascimentoExcluir.getDtHrNascimento().equals(this.mcoNascimentosDAO.obterDtHrUltimoNascimento(gsoPacCodigo, gsoSeqp))) {
			throw new ApplicationBusinessException(RegistrarGestacaoAbaNascimentoONExceptionCode.ERRO_EXCLUIR_ULTIMO_NASCIMENTO);
		}
		Integer seqpNascimento = nascimentoExcluir.getSeqp();
		this.mcoNascIndicacoesRN.excluirMcoNascIndicacoesForcipe(gsoPacCodigo, gsoSeqp, seqpNascimento);
		this.mcoForcipesRN.excluirMcoForcipesPorId(gsoPacCodigo, gsoSeqp, seqpNascimento);
		this.mcoNascIndicacoesRN.excluirMcoNascIndicacoesCesarea(gsoPacCodigo, gsoSeqp, seqpNascimento);
		this.mcoCesarianasRN.excluirMcoCesarianasPorId(gsoPacCodigo, gsoSeqp, seqpNascimento);
		this.mcoIntercorrenciaNascsRN.excluirMcoIntercorrenciaNascs(gsoPacCodigo, gsoSeqp, seqpNascimento);
		this.mcoProfNascsRN.excluirTodosMcoProfNasc(new McoProfNascsId(gsoPacCodigo, gsoSeqp, seqpNascimento, null, null));
		this.mcoNascimentosRN.excluirNascimentos(nascimentoExcluir, gsoPacCodigo, gsoSeqp);
	}
	
	public List<TipoAnestesiaVO> pesquisarTiposAnestesiasAtivas(Object param) {
		String strPesquisa = (String) param;
		Short seq = null;
		String descricao = null;
		if (CoreUtil.isNumeroShort(strPesquisa)) {
			seq = Short.valueOf(strPesquisa);
		} else if (StringUtils.isNotBlank(strPesquisa)) {
			descricao = strPesquisa;
		}
		return this.blocoCirurgicoService.pequisarTiposAnestesiasAtivas(seq, descricao);
	}
	
	public Long pesquisarTiposAnestesiasAtivasCount(Object param) {
		String strPesquisa = (String) param;
		Short seq = null;
		String descricao = null;
		if (CoreUtil.isNumeroShort(strPesquisa)) {
			seq = Short.valueOf(strPesquisa);
		} else if (StringUtils.isNotBlank(strPesquisa)) {
			descricao = strPesquisa;
		}
		return this.blocoCirurgicoService.pequisarTiposAnestesiasAtivasCount(seq, descricao);
	}
	
	public List<SalaCirurgicaVO> obterSalasCirurgicasAtivasPorUnfSeqNome(Object param) throws ApplicationBusinessException {
		Object pUnidadeCO = buscarParametroPorNome("P_UNIDADE_CO", "vlrNumerico");
		Short unfSeq = null;
		if (pUnidadeCO != null) {
			unfSeq = ((BigDecimal) pUnidadeCO).shortValue();
		}
		String strPesquisa = (String) param;
		Short seqp = null;
		String descricao = null;
		if (CoreUtil.isNumeroShort(strPesquisa)) {
			seqp = Short.valueOf(strPesquisa);
		} else if (StringUtils.isNotBlank(strPesquisa)) {
			descricao = strPesquisa;
		}
		return this.blocoCirurgicoService.obterSalasCirurgicasAtivasPorUnfSeqNome(unfSeq, seqp, descricao);
	}
	
	public Long obterSalasCirurgicasAtivasPorUnfSeqNomeCount(Object param) throws ApplicationBusinessException {
		Object pUnidadeCO = buscarParametroPorNome("P_UNIDADE_CO", "vlrNumerico");
		Short unfSeq = null;
		if (pUnidadeCO != null) {
			unfSeq = ((BigDecimal) pUnidadeCO).shortValue();
		}
		String strPesquisa = (String) param;
		Short seqp = null;
		String descricao = null;
		if (CoreUtil.isNumeroShort(strPesquisa)) {
			seqp = Short.valueOf(strPesquisa);
		} else if (StringUtils.isNotBlank(strPesquisa)) {
			descricao = strPesquisa;
		}
		return this.blocoCirurgicoService.obterSalasCirurgicasAtivasPorUnfSeqNomeCount(unfSeq, seqp, descricao);
	}
	
	public EquipeVO buscarEquipeAssociadaLaudoAih(Integer conNumero, Integer pacCodigo) throws ApplicationBusinessException {
		List<LaudoAihVO> listaLaudoAih = this.pesquisarLaudosAihPorConsultaPaciente(conNumero, pacCodigo);
		if (!listaLaudoAih.isEmpty()) {
			List<EquipeVO> listaEquipe = this.pesquisarEquipesPorMatriculaVinculo(listaLaudoAih.get(0).getMatricula(),
					listaLaudoAih.get(0).getVinCodigo());
			
			return listaEquipe.get(0);
		}
		return null;
	}
	
	private List<LaudoAihVO> pesquisarLaudosAihPorConsultaPaciente(Integer conNumero, Integer pacCodigo) throws ApplicationBusinessException {
		List<LaudoAihVO> listaRetorno = null;
		try {
			listaRetorno = ambulatorioService.pesquisarLaudosAihPorConsultaPaciente(conNumero, pacCodigo);
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(RegistrarGestacaoAbaNascimentoONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		} catch (RuntimeException e) {
			throw new ApplicationBusinessException(RegistrarGestacaoAbaNascimentoONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
		return listaRetorno;
	}
	
	private List<EquipeVO> pesquisarEquipesPorMatriculaVinculo(Integer matricula, Short vinCodigo) throws ApplicationBusinessException {
		List<EquipeVO> listaRetorno = null;
		try {
			listaRetorno = configuracaoService.pesquisarEquipesPorMatriculaVinculo(matricula, vinCodigo);
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(RegistrarGestacaoAbaNascimentoONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		} catch (RuntimeException e) {
			throw new ApplicationBusinessException(RegistrarGestacaoAbaNascimentoONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
		return listaRetorno;
	}
	
	private Object buscarParametroPorNome(String nome, String coluna) throws ApplicationBusinessException {
		Object retorno = null;
		retorno = parametroFacade.obterAghParametroPorNome(nome, coluna);
		return retorno;
	}

	public void validarListaUnidadesFuncionais(List<Short> lista) throws ApplicationBusinessException{
		if(lista == null || lista.size() == 0){
			throw new ApplicationBusinessException(RegistrarGestacaoAbaNascimentoONExceptionCode.MENSAGEM_ERRO_UNIDADE_CARACT_CIRG);
		}
	}

	public void validarProsseguirComGravarNascimento(DadosNascimentoVO nascimento, McoCesarianas cesarianas)  throws ApplicationBusinessException{
//		2. Se o MCO_NASCIMENTOS.TIPO for ‘Cesareana’ e o campo MCO_CESARIANAS.CONTAMINACAO for nulo disparar 
//		mensagem “MCO-00792” e cancelar o processamento
		if(nascimento != null && cesarianas!= null){
			if(DominioTipoNascimento.C == nascimento.getTipoNascimento() && cesarianas.getContaminacao() == null){
				throw new ApplicationBusinessException(RegistrarGestacaoAbaNascimentoONExceptionCode.MCO_00792);
			}
		}
	}

	public void validarFieldSetNascimento(
			DadosNascimentoSelecionadoVO dados) throws ApplicationBusinessException{
//		3. Se os campos MCO_NASCIMENTOS.SCI_UNF_SEQ e MCO_NASCIMENTOS.SCI_SEQP para o registro corrente do fieldSet Nascimentos 
//		e os campos MCO_CESARIANAS.SCI_UNF_SEQ e MCO_CESARIANAS.SCI_SEQP forem nulos disparar mensagem “MCO-00794” e cancelar o processamento
		if(dados != null && dados.getMcoNascimento() != null && dados.getMcoCesariana() != null){
			if(dados.getMcoNascimento().getSciUnfSeq() == null && 
					dados.getMcoNascimento().getSciSeqp() == null &&
					dados.getMcoCesariana().getSciUnfSeq() == null &&
					dados.getMcoCesariana().getSciSeqp() == null){
				
				throw new ApplicationBusinessException(RegistrarGestacaoAbaNascimentoONExceptionCode.MCO_00794);
			}
		}
	}

	public void validarAgendamento(DadosNascimentoSelecionadoVO dadosNascimentoSelecionado) throws ApplicationBusinessException {
		if(dadosNascimentoSelecionado != null){
			if(dadosNascimentoSelecionado.getTempoProcedimento() == null){
				// 5. Se o campo MCO_NASCIMENTOS.TAN_SEQ do registro selecionado na aba Nascimento for nulo 
				//disparar mensagem “MCO-00796” e cancelar o processamento
				throw new ApplicationBusinessException(RegistrarGestacaoAbaNascimentoONExceptionCode.MCO_00795);
			}
			
			if(dadosNascimentoSelecionado.getDthrInicioProcedimento() != null){
				if(dadosNascimentoSelecionado.getDthrInicioProcedimento().getTime() > Calendar.getInstance().getTimeInMillis()){
					throw new ApplicationBusinessException(RegistrarGestacaoAbaNascimentoONExceptionCode.MCO_00806);
				}
			}
		}
	}

	public void validarAnestesia(Short tipoAnestesia) throws ApplicationBusinessException {
		if(tipoAnestesia == null){
			throw new ApplicationBusinessException(RegistrarGestacaoAbaNascimentoONExceptionCode.MCO_00796);
		}	
	}

	public void validarEquipe(EquipeVO equipe)  throws ApplicationBusinessException{
		if(equipe == null || equipe.getSeq() == null){
			throw new ApplicationBusinessException(RegistrarGestacaoAbaNascimentoONExceptionCode.MCO_00797);
		}
	}

	public void confirmarProcedimento(Integer pacCodigo, Short gsoSeqp,String contaminacao, Date dthrInicioProcedimento, Short seqp,Short tempoProcedimento, Short tanSeq, Short seq,String tipoParto) throws ServiceException, ServiceBusinessException {		
		this.blocoCirurgicoService.inserirCirurgiaDoCentroObstetrico(pacCodigo,gsoSeqp,contaminacao,dthrInicioProcedimento,seqp,tempoProcedimento,tanSeq,seq,tipoParto);
	}	
	
	public void atualizarSituacaoPaciente(Integer numeroConsulta, String login) throws ApplicationBusinessException{
		BigDecimal vlrNumerico = (BigDecimal) this.parametroFacade.obterAghParametroPorNome(EmergenciaParametrosEnum.P_SIT_PUERPERIO.toString(), "vlrNumerico");
		if(vlrNumerico == null){
			throw new ApplicationBusinessException(RegistrarGestacaoAbaNascimentoONExceptionCode.MENSAGEM_ERRO_PARAMETRO, "P_SIT_PUERPERIO");
		}
		
		List<MamTrgEncInterno>  listaMamTrgEncInterno = mamTrgEncInternoDAO.obterTriagemPorNumeroDaConsulta(numeroConsulta);
		MamSituacaoEmergencia mamSituacaoEmergencia = mamSituacaoEmergenciaDAO.obterPorSeq(vlrNumerico.shortValue());
		
		MamTriagens manMamTriagens = mamTriagensDAO.obterPorChavePrimaria(listaMamTrgEncInterno.get(0).getTriagem().getSeq());
		MamTriagens manMamTriagensOriginal = mamTriagensDAO.obterOriginal(listaMamTrgEncInterno.get(0).getTriagem().getSeq());
		manMamTriagens.setSituacaoEmergencia(mamSituacaoEmergencia);
		
		marcarConsultaEmergenciaRN.atualizarSituacaoTriagem(manMamTriagens, manMamTriagensOriginal, login);
	}
}
