package br.gov.mec.aghu.perinatologia.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import java.util.Objects;
import org.apache.commons.lang3.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioCor;
import br.gov.mec.aghu.dominio.DominioEventoLogImpressao;
import br.gov.mec.aghu.dominio.DominioGravidez;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.McoApgars;
import br.gov.mec.aghu.model.McoBolsaRotas;
import br.gov.mec.aghu.model.McoGestacoes;
import br.gov.mec.aghu.model.McoGestacoesId;
import br.gov.mec.aghu.model.McoNascimentos;
import br.gov.mec.aghu.model.McoRecemNascidos;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.vo.Paciente;
import br.gov.mec.aghu.perinatologia.dao.McoApgarsDAO;
import br.gov.mec.aghu.perinatologia.dao.McoBolsaRotasDAO;
import br.gov.mec.aghu.perinatologia.dao.McoGestacoesDAO;
import br.gov.mec.aghu.perinatologia.dao.McoLogImpressoesDAO;
import br.gov.mec.aghu.perinatologia.dao.McoNascimentosDAO;
import br.gov.mec.aghu.perinatologia.dao.McoRecemNascidosDAO;
import br.gov.mec.aghu.perinatologia.vo.RecemNascidoFilterVO;
import br.gov.mec.aghu.perinatologia.vo.RecemNascidoVO;
import br.gov.mec.aghu.util.EmergenciaParametrosColunas;
import br.gov.mec.aghu.util.EmergenciaParametrosEnum;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class RecemNascidoON extends BaseBusiness {

	private static final long serialVersionUID = 7082373834407180302L;
	
	private static final Log LOG = LogFactory.getLog(RecemNascidoON.class);
	
	private static final BigDecimal PESO_MIN = new BigDecimal("200");
	private static final BigDecimal PESO_MAX = new BigDecimal("7000");
	private static final Byte LIMITE_QTD_DIAS_RUPTURA_BOLSA = 99;
	private static final Byte LIMITE_QTD_HORAS_RUPTURA_BOLSA = 24;
	private static final Byte LIMITE_QTD_MINUTOS_RUPTURA_BOLSA = 59;
	
	private static final String RN = "RN";
	private static final String ESPACO = " ";
	private static final String NAO_GEMELAR = "N";
	private static final String FORMA_RUPTURA_AMNIOTOMIA = "Amniotomia"; 
	private static final String FORMA_RUPTURA_AMNIORREXIS = "Amniorrexis"; 
	
	@EJB
	private RecemNascidoRN recemNascidoRN;
	
	@Inject
	private McoRecemNascidosDAO mcoRecemNascidosDAO;
	
	@Inject
	private McoGestacoesDAO mcoGestacoesDAO;
	
	@Inject
	private McoApgarsDAO mcoApgarsDAO;
	
	@Inject
	private McoBolsaRotasDAO bolsaRotasDAO;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@Inject
	private McoLogImpressoesDAO mcoLogImpressoesDAO;
	
	@Inject 
	private McoNascimentosDAO mcoNascimentosDAO;

	@EJB
	private IParametroFacade parametroFacade;
	
	@Override
	protected Log getLogger() {
		return LOG;
	}

	private enum RecemNascidoONExceptionCode implements BusinessExceptionCode {
		ERRO_REG_REC_NASC_PERSISTENCIA, MSG_QTDE_RNA_SUPERIOR_GEMELAR, MENSAGEM_SERVICO_INDISPONIVEL, 
		MCO_00671, MCO_00670, MCO_00672, MCO_00639, MCO_00569, MCO_00722,
		MSG_REG_REC_NASC_JA_EXISTE_RNA_PARA_GESTACAO, MSG_REG_REC_NASC_INFORMAR_PESO_RECEM_NASCIDO, 
		MSG_REG_REC_NASC_LIMITE_DIAS, MSG_REG_REC_NASC_LIMITE_HORAS, MSG_REG_REC_NASC_LIMITE_MINUTOS, MSG_ERRO_REG_REC_NASC_EXCLUSAO,
		MENSAGEM_ERRO_PARAMETRO, MENSAGEM_TEMPERTURA_SALA_PARTO, ERRO_CONSULTA_JA_FINALIZADA, INFORMAR_SEXO_RECEM_NASCIDO, MENSAGEM_TEMPO_CLAPEAMENTO;
	}
	
	public List<RecemNascidoVO> listarRecemNascidoVOs(RecemNascidoFilterVO filter) throws ApplicationBusinessException {
		
		List<McoRecemNascidos> recemNascidos = mcoRecemNascidosDAO.listarPorGestante(filter.getPacCodigo(), filter.getSeqp());
		List<RecemNascidoVO> recemNascidoVOs =  new ArrayList<RecemNascidoVO>(6);

		for (McoRecemNascidos recemNascido : recemNascidos) {
			
			Paciente paciente = obterPacienteRecemNascido(recemNascido.getPaciente().getCodigo());
			RecemNascidoVO vo = new RecemNascidoVO();
			
			vo.setGsoPacCodigoPK(recemNascido.getId().getGsoPacCodigo());       // vo.setPacCodigo(filter.getPacCodigo());
			vo.setGsoSeqpPK(recemNascido.getId().getGsoSeqp());					// vo.setSeqp(filter.getSeqp());
			vo.setSeqpPK(recemNascido.getId().getSeqp());
			vo.setIdFake(recemNascido.getId().getSeqp());
			vo.setPacCodigoRecemNascido(recemNascido.getPaciente().getCodigo());
			
			vo.setPacCodigo(filter.getPacCodigo());
			vo.setSeqp(filter.getSeqp());
			vo.setNomePaciente(filter.getNomeGestante());
			
			vo.setDataHora(recemNascido.getDthrNascimento());
			vo.setNome(paciente.getNome());
			vo.setPeso(obterPesoRecemNascido(recemNascido.getPaciente().getCodigo()));
			vo.setSexo(DominioSexo.getInstance(paciente.getSexo()));
			vo.setCor(DominioCor.getInstance(paciente.getCor()));
			vo.setStrProntuario(CoreUtil.formataProntuario(paciente.getProntuario()));
			vo.setProntuario(paciente.getProntuario());
			
			vo.setApagarUmMinuto(recemNascido.getApgar1());
			vo.setApagarCincoMinuto(recemNascido.getApgar5());
			vo.setApagarDezMinuto(recemNascido.getApgar10());
			vo.setQtdDiasRupturaBolsa(recemNascido.getDiasRuptBolsa());
			vo.setQtdHorasRupturaBolsa(recemNascido.getHrsRuptBolsa());
			vo.setQtdMinutosRupturaBolsa(recemNascido.getMinRuptBolsa());
			vo.setColetadoSangueCordao(recemNascido.getIndColetaSangueCordao());
			vo.setAspiracao(recemNascido.getIndAspiracao());
			vo.setAspiracaoTet(recemNascido.getIndAspiracaoTet());
			vo.setIndO2Inalatorio(recemNascido.getIndO2Inalatorio());
			vo.setIndVentilacaoPorMascaraBalao(recemNascido.getIndVentilacaoPorMascaraBalao());
			vo.setIndVentilacaoPorMascaraBabyPuff(recemNascido.getIndVentilacaoPorMascaraBabyPuff());
			vo.setIndVentilacaoTetBalao(recemNascido.getIndVentilacaoPorMascaraBalao());
			vo.setIndVentilacaoTetBabyPuff(recemNascido.getIndVentilacaoTetBabyPuff());
			vo.setIndCateterismoVenoso(recemNascido.getIndCateterismoVenoso());
			vo.setIndMassCardiacaExt(recemNascido.getIndMassCardiacaExt());
			vo.setIndCpap(recemNascido.getIndCpap());
			vo.setIndUrinou(recemNascido.getIndUrinou());
			vo.setIndEvacuou(recemNascido.getIndEvacuou());
			vo.setIndLavadoGastrico(recemNascido.getIndLavadoGastrico());
			vo.setIndAmamentado(recemNascido.getIndAmamentado());
			vo.setDestinoRecemnascido(recemNascido.getDestinoRecemnascido());
			vo.setTempoClampeamentoCordao(recemNascido.getTempoClampeamentoCordao());
			vo.setTemperaturaSalaParto(recemNascido.getTemperaturaSalaParto());
			vo.setIndContatoPele(recemNascido.getIndContatoPele());
			vo.setAspGastrVol(recemNascido.getAspGastrVol());
			vo.setAspGastrAspecto(recemNascido.getAspGastrAspecto());
			vo.setObservacao(recemNascido.getObservacao());
			
			List<McoApgars> apgars = mcoApgarsDAO.obterListaApgarDoRecemNascido(filter.getPacCodigo(), filter.getSeqp(), recemNascido.getId().getSeqp());
			
			if(apgars != null && !apgars.isEmpty()) {
				McoApgars apgar = (McoApgars)CollectionUtils.get(apgars, 0);
				vo.setApagarUmMinuto(apgar.getApgar1());
				vo.setApagarCincoMinuto(apgar.getApgar5());
				vo.setApagarDezMinuto(apgar.getApgar10());
			}
			
			recemNascidoVOs.add(vo);
		}
				
		return recemNascidoVOs;
	}
	
	public boolean temGravidezConfirmada(Integer pacCodigo, Short gsoSeqp) {
		McoGestacoes gestacao = mcoGestacoesDAO.pesquisarMcoGestacaoPorId(pacCodigo, gsoSeqp);
		return (DominioGravidez.GCO == gestacao.getGravidez());
	}
	
	public void adicionarRecemNascido(RecemNascidoVO vo, Integer quantidadeRemcemNascido) throws ApplicationBusinessException {
		notNull(vo, RecemNascidoONExceptionCode.ERRO_REG_REC_NASC_PERSISTENCIA);
		validarDataHora(vo.getDataHora());
		validarCor(vo.getCor());
		validarExistenciaRecemNascido(vo, quantidadeRemcemNascido);
		validarPesoRecemNascido(vo.getPeso());
		vo.setNome(obterNomeRecemNascido(vo, quantidadeRemcemNascido));
		
		vo.setIdFake(obterId(vo, quantidadeRemcemNascido));
	}
	
	private Byte obterId(RecemNascidoVO vo, Integer quantidadeRemcemNascido) {
		
		List<McoNascimentos> listMcoNascimentos = mcoNascimentosDAO.pesquisarNascimentosPorGestacao(vo.getPacCodigo(), vo.getSeqp());

		// 1. Buscar a quantidade de Nascimentos para aquela gestação através da consulta C17
		// 2. Se a quantidade é maior que zero Então – Existe Nascimento cadastrado para aquela gestação
		if (listMcoNascimentos.size() > 0) {
			// Verificar se a data de nascimento (hora e minuto) já existe em Nascimento através da consulta C18
			if (mcoNascimentosDAO.verificaExisteNascimentoPorDtHrNascimento(vo.getPacCodigo(), vo.getSeqp(), 
					listMcoNascimentos.get(listMcoNascimentos.size()-1).getDthrNascimento(), true)) {
				// Se for encontrado registro Então
				// Atribuir o sequencial do Nascimento(C18.NAS.SEQP) ao sequencial do Recém Nascido
				// caso exista mais de um recem nascido no mesmo horário!
				return listMcoNascimentos.get(listMcoNascimentos.size()-1).getId().getSeqp().byteValue();
			
			} else {	
				//Senão buscar o maior sequencial do Recém Nascido para aquela gestação através da consulta C16 e adicionar o valor 1 (um) ao valor encontrado.	
				Byte proximoSeqRecemNascido = mcoRecemNascidosDAO.obterMaxSeqpMcoRecemNascidos(vo.getPacCodigo(), vo.getSeqp());
				++ proximoSeqRecemNascido;
				
				// Verificar se este valor resultante (valor encontrado + 1) já está cadastrado na tabela de Nascimentos através da consulta C19.
				// Se não for encontrado registro Então – não encontrou registro na tabela recém-nascido
				if(! mcoNascimentosDAO.verificaExisteNascimentoPorSequencial(vo.getPacCodigo(), vo.getSeqp(), proximoSeqRecemNascido.intValue())) {
					// Verificar se tem sequencial livre em nascimento entre o maior sequencial do Nascimento na consulta C20
					Byte seqLivreNascimento = verificarPosicaoDisponivelSequencial(obterListaDeSequencial(listMcoNascimentos));		
					// existe sequencial livre entre o menor e o maior sequencial  da tabela nascimento
					if(seqLivreNascimento != null) {
						// verificar se sequecial livre não esta sendo usado na tabela recem nascido
						if(mcoRecemNascidosDAO.obterMcoRecemNascidosPorId(vo.getPacCodigo(), vo.getSeqp(), seqLivreNascimento.intValue()) == null ) {
							// Atribuir o menor sequencial livre ao sequencial do Recém Nascido
							return seqLivreNascimento;
						} else {
							// Buscar o maior sequencial entre o Nascimento e o Recém-Nascido para aquela gestação.
							// adicionar o valor 1 (um) ao maior valor encontrado nas consultas e atribui ao sequencial do Recém Nascidos.
							byte maxSeqpNascimento = mcoNascimentosDAO.obterMaxSeqpMcoNascimentos(vo.getPacCodigo(), vo.getSeqp()).byteValue();
							
							if (proximoSeqRecemNascido > maxSeqpNascimento || proximoSeqRecemNascido == maxSeqpNascimento) {
								return proximoSeqRecemNascido;	
							} else {
								return maxSeqpNascimento ++;
							}
						}
					}
				} 
			}
		}
		
		byte maxSeqpRecemNascido = mcoRecemNascidosDAO.obterMaxSeqpMcoRecemNascidos(vo.getPacCodigo(), vo.getSeqp());
		return maxSeqpRecemNascido ++;
	}
	
	private List<Integer> obterListaDeSequencial(List<McoNascimentos> list){
		List<Integer> listSequencial = new ArrayList<Integer>(6);

		for (McoNascimentos nascimento : list) {
			listSequencial.add(nascimento.getId().getSeqp());
		}
		
		return listSequencial;
	}

	private Byte verificarPosicaoDisponivelSequencial(List<Integer> sequenciais){
		if (sequenciais != null) {
			Collections.sort(sequenciais);
			for (int i = 0; i < sequenciais.size(); i++) {
				int index = Collections.binarySearch(sequenciais, (i + 1));
				if(index < 0){
					return ((byte) i);
				}
			}
		}
		return null;
	}
	
	public void persistirRecemNascido(RecemNascidoVO vo) throws ApplicationBusinessException {
		getLogger().info("Persistir");
	}

	public void validarRecemNascido(RecemNascidoVO vo) throws ApplicationBusinessException {
		notNull(vo, RecemNascidoONExceptionCode.ERRO_REG_REC_NASC_PERSISTENCIA);
		validarDataHora(vo.getDataHora());
		validarSexo(vo);
		validarCor(vo.getCor());
		validarTempoClampeamento(vo.getTempoClampeamentoCordao()); 
		validarTemperaturaSalaParto(vo.getTemperaturaSalaParto());
		validarPesoRecemNascido(vo.getPeso());
		validarBolsaRota(vo);
	}

	private void validarSexo(RecemNascidoVO vo) throws ApplicationBusinessException {
		notNull(vo.getSexo(), RecemNascidoONExceptionCode.INFORMAR_SEXO_RECEM_NASCIDO);
	}
	
	private void validarExistenciaRecemNascido(RecemNascidoVO vo, int quantidadeRemcemNascido) throws ApplicationBusinessException {
		int qtdAux = obterQuantidadeRecemNascido(vo.getPacCodigo(), vo.getSeqp(), quantidadeRemcemNascido);
		
		if(qtdAux > 0){
			validarQuantidadeGemelar(qtdAux, vo.getPacCodigo(), vo.getSeqp());
		}
	}

	private int obterQuantidadeRecemNascido(Integer pacCodigo, Short seqp, int quantidadeRemcemNascido) {
		int qtdRecemNascido = mcoRecemNascidosDAO.obterQuantidadeRecemNascidosPorGestante(pacCodigo, seqp).intValue();
		int qtdAux = 0;
		
		if(quantidadeRemcemNascido > qtdRecemNascido){
			qtdAux = quantidadeRemcemNascido;
		} else {
			qtdAux = qtdRecemNascido;
		}
		
		return qtdAux;
	}
	
	private void validarQuantidadeGemelar(Integer quantidadeGemelar, Integer pacCodigo, Short seqp) throws ApplicationBusinessException {
		McoGestacoes mcoGestacoes = mcoGestacoesDAO.obterPorChavePrimaria(new McoGestacoesId(pacCodigo, seqp));
		
		if(Objects.equals(quantidadeGemelar.toString(), mcoGestacoes.getGemelar())){
			throw new ApplicationBusinessException(RecemNascidoONExceptionCode.MSG_QTDE_RNA_SUPERIOR_GEMELAR, mcoGestacoes != null ? mcoGestacoes.getGemelar() : "");
		}
		
		if(Objects.equals(NAO_GEMELAR, mcoGestacoes.getGemelar())){
			throw new ApplicationBusinessException(RecemNascidoONExceptionCode.MSG_REG_REC_NASC_JA_EXISTE_RNA_PARA_GESTACAO);
		}	
	}

	private void validarPesoRecemNascido(BigDecimal peso) throws ApplicationBusinessException {
		notNull(peso, RecemNascidoONExceptionCode.MSG_REG_REC_NASC_INFORMAR_PESO_RECEM_NASCIDO);
		if(peso.compareTo(PESO_MIN) < 0 || peso.compareTo(PESO_MAX) > 0 ){
			throw new ApplicationBusinessException(RecemNascidoONExceptionCode.MCO_00569);
		}
	}

	private BigDecimal obterPesoRecemNascido(Integer pacCodigoRecemNascido) throws ApplicationBusinessException{
		BigDecimal peso = pacienteFacade.obterPesoPacientesPorCodigoPaciente(pacCodigoRecemNascido);
		if (peso != null) {
			return peso.multiply(BigDecimal.valueOf(1000));
		}
		return null;
	}
	
	public Paciente obterPacienteRecemNascido(Integer pacCodigoRecemNascido) throws ApplicationBusinessException{
		AipPacientes paciente =  pacienteFacade.obterPacientePorCodigoOuProntuario(null, pacCodigoRecemNascido, null);
		if (paciente != null) {
			// transforma para o tipo de retorno
			Paciente result = new Paciente();
			result.setProntuario(paciente.getProntuario());
			result.setCodigo(paciente.getCodigo());
			result.setNome(paciente.getNome());
			result.setNomeMae(paciente.getNomeMae());
			result.setDtNascimento(paciente.getDtNascimento());
			result.setDtObito(paciente.getDtObito());
			result.setCor((paciente.getCor() != null) ? paciente.getCor().toString() : null);
			result.setSexo((paciente.getSexo() != null) ? paciente.getSexo().toString() : null);
			return result;
		}
		return null;
	}
	
	private Boolean isGestcaoDeGemeos(Integer pacCodigo, Short seqp) throws ApplicationBusinessException {
		McoGestacoes mcoGestacoes = mcoGestacoesDAO.obterPorChavePrimaria(new McoGestacoesId(pacCodigo, seqp));

		if(!Objects.equals(NAO_GEMELAR, mcoGestacoes.getGemelar()) && mcoGestacoes.getGemelar() != null){
			return Boolean.TRUE;
		}
		
		return Boolean.FALSE;
	}
	
	public String obterNomeRecemNascido(RecemNascidoVO vo, int quantidadeRemcemNascido) throws ApplicationBusinessException{
		
		StringBuilder nomeRecemNascidoBuilder = new StringBuilder(60);
		
		if(isGestcaoDeGemeos(vo.getPacCodigo(), vo.getSeqp())){
			
			Integer quantidade = obterQuantidadeRecemNascido(vo.getPacCodigo(), vo.getSeqp(), quantidadeRemcemNascido);

			++ quantidade;
			
			nomeRecemNascidoBuilder
				.append(RN)
				.append(conversorDeNumeroRomanosGemelar(quantidade))
				.append(ESPACO)
				.append(vo.getNomePaciente());
		} else {
			nomeRecemNascidoBuilder
				.append(RN)
				.append(ESPACO)
				.append(vo.getNomePaciente());
		}
		
		return nomeRecemNascidoBuilder.toString();
	}
	
	private String conversorDeNumeroRomanosGemelar(int valor){
		
		switch (valor) {
			case 1:
				return "I";
			case 2:
				return "II";
			case 3:
				return "III";
			case 4:
				return "IV";
			case 5:
				return "V";
			case 6:
				return "VI";
			default:
				return "";
		}
	}
	
	/**
	 * 	ON06 ->  1. Validar Data e Hora aplicando a ON07.
 	 * 				1. Se o data e hora (item3) for nulo apresentar a mensagem “MCO-00671”.
	 * 				2. Se a data e hora (item3) for maior que a data atual apresentar a mensagem “MCO-00670” e cancelar o processamento.
	 * 				3. Se a hora (item3) for “00:00” apresentar a mensagem de warning “MCO-00672”.
	 * @param dataHora
	 * @throws ApplicationBusinessException
	 */
	private void validarDataHora(Date dataHora) throws ApplicationBusinessException{
		notNull(dataHora, RecemNascidoONExceptionCode.MCO_00671);
		verifircarDataHoraMaiorDataAtual(dataHora);
		verifircarZeroHora(dataHora);
	}

	/**
	 * Se a data e hora (item3) for maior que a data atual apresentar a mensagem “MCO-00670” e cancelar o processamento.
	 * @param dataHora
	 * @throws ApplicationBusinessException
	 */
	private void verifircarDataHoraMaiorDataAtual(Date dataHora) throws ApplicationBusinessException {
		if(CoreUtil.isMaiorDatas(dataHora, new Date())){
			throw new ApplicationBusinessException(RecemNascidoONExceptionCode.MCO_00670);
		}	
	}
	
	private void verifircarZeroHora(Date dataHora) throws ApplicationBusinessException {
		final Calendar calendar = Calendar.getInstance(); 
		calendar.setTime(dataHora);		
		zeraSecondMillisecond(calendar);

		if(Objects.equals(calendar.getTime(), obterDataAuxComparacao(dataHora))){
			throw new ApplicationBusinessException(RecemNascidoONExceptionCode.MCO_00672);
		}	
	}

	private Date obterDataAuxComparacao(Date dataHora) {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(dataHora);
		DateUtil.zeraHorario(calendar);
		return calendar.getTime();
	}

	/**
	 * Seta o horário para zero segundo e milisegundo
	 * @param calendar
	 */
	public static void zeraSecondMillisecond(Calendar calendar) {
		if(calendar != null){
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
		}
	}
	
	private void notNull(Object object, BusinessExceptionCode exceptionCode) throws ApplicationBusinessException { 
		try {
			Validate.notNull(object);
		} catch (IllegalArgumentException e) {
			throw new ApplicationBusinessException(exceptionCode);
		}
	}

	private void validarBolsaRota(RecemNascidoVO vo) throws ApplicationBusinessException{
		if (vo.getQtdDiasRupturaBolsa() != null) {
			validarDiasBolsaRota(vo.getQtdDiasRupturaBolsa());
		}

		if (vo.getQtdHorasRupturaBolsa() != null) {
			validarHorasBolsaRota(vo.getQtdHorasRupturaBolsa());
		}
		
		if (vo.getQtdMinutosRupturaBolsa() != null) {
			validarMinutosBolsaRota(vo.getQtdMinutosRupturaBolsa());
		}
		
		validarBolsaRotaConformeFormaRuptura(vo);
	}

	/**
	 *  ON09 -> Se o valor do campo dias (item 23) for maior que 99 apresentar a mensagem “MENSAGEM_LIMITE_DIAS” e cancelar o processamento.
	 * @param dias
	 * @throws ApplicationBusinessException
	 */
	public void validarDiasBolsaRota(Byte dias)throws ApplicationBusinessException{
		if(dias > LIMITE_QTD_DIAS_RUPTURA_BOLSA){
			throw new ApplicationBusinessException(RecemNascidoONExceptionCode.MSG_REG_REC_NASC_LIMITE_DIAS);
		}
	}
	
	/**
	 * ON10 -> Se o valor do campo horas (item 24) for maior que 24 apresentar a mensagem “MENSAGEM_LIMITE_HORAS” e cancelar o processamento.
	 * @param horas
	 * @throws ApplicationBusinessException
	 */
	public void validarHorasBolsaRota(Byte horas) throws ApplicationBusinessException{
		if(horas > LIMITE_QTD_HORAS_RUPTURA_BOLSA){
			throw new ApplicationBusinessException(RecemNascidoONExceptionCode.MSG_REG_REC_NASC_LIMITE_HORAS);
		}
	}
	
	/**
	 * ON11 -> Se o valor do campo min (item 25) for maior que 59 apresentar a mensagem “MENSAGEM_LIMITE_MINUTOS” e cancelar o processamento.
	 * @param minutos
	 * @throws ApplicationBusinessException
	 */
	public void validarMinutosBolsaRota(Byte minutos) throws ApplicationBusinessException {
		if(minutos > LIMITE_QTD_MINUTOS_RUPTURA_BOLSA){
			throw new ApplicationBusinessException(RecemNascidoONExceptionCode.MSG_REG_REC_NASC_LIMITE_MINUTOS);
		}
	}
	
	/**
	 * RN08
	 * @param vo
	 * @throws ApplicationBusinessException
	 */
	private void validarBolsaRotaConformeFormaRuptura(RecemNascidoVO vo) throws ApplicationBusinessException {
		
		McoBolsaRotas bolsaRotas = bolsaRotasDAO.buscarBolsaRotas(vo.getPacCodigo(), vo.getSeqp());
		
		/*
		 *  Se a data e hora (item 3) for diferente de nulo 
		 * 	E o campo Dias (item 23) for nulo 
		 * 	E o campo horas (item 24) 
		 *  E o campo min (item 25) for nulo 
		 *  E a forma de ruptura (resultado da consulta C11) for “Amniotomia”, 
		 */
		if (isNullDiaHoraMinutoRupturaBolsa(vo)
				&& Objects.equals(FORMA_RUPTURA_AMNIOTOMIA, bolsaRotas.getFormaRuptura())) {
			atribuirDiaHoraMinutoRupturaBolsa(vo, bolsaRotas);
		}
		
		/*
		 * Se a data e hora (item 3) for diferente de nulo 
		 * E C11.DTHR_ROMPIMENTO é diferente de nulo 
		 * E o campo Dias (item 23) for nulo 
		 * E o campo horas (item 24) 
		 * E o campo min (item 25) for nulo 
		 * E a forma de ruptura (resultado da consulta C11) for “Amniorrexis”, 

		 */
		if (bolsaRotas.getDthrRompimento() != null
				&& isNullDiaHoraMinutoRupturaBolsa(vo)
				&& Objects.equals(FORMA_RUPTURA_AMNIORREXIS, bolsaRotas.getFormaRuptura())) {
			atribuirDiaHoraMinutoRupturaBolsa(vo, bolsaRotas);
		}	
	}

	private void atribuirDiaHoraMinutoRupturaBolsa(RecemNascidoVO vo, McoBolsaRotas mcoBolsaRotas) {
		vo.setQtdDiasRupturaBolsa(DateUtil.obterQtdDiasEntreDuasDatas(vo.getDataHora(), mcoBolsaRotas.getDthrRompimento()).byteValue());
		vo.setQtdHorasRupturaBolsa(DateUtil.obterQtdHorasEntreDuasDatas(vo.getDataHora(), mcoBolsaRotas.getDthrRompimento()).byteValue());
		vo.setQtdMinutosRupturaBolsa(DateUtil.obterQtdMinutosEntreDuasDatas(vo.getDataHora(), mcoBolsaRotas.getDthrRompimento()).byteValue());
	}

	private boolean isNullDiaHoraMinutoRupturaBolsa(RecemNascidoVO vo) {
		return vo.getQtdDiasRupturaBolsa() == null
				&& vo.getQtdHorasRupturaBolsa() == null
				&& vo.getQtdMinutosRupturaBolsa() == null;
	}
	
	/**
	 * ON03
	 * @param vo
	 * @param listRecemNascidoVOs
	 * @throws ApplicationBusinessException
	 */
	public void excluirRecemNascido(RecemNascidoVO vo, List<RecemNascidoVO> listRecemNascidoVOs) throws ApplicationBusinessException {
		notNull(vo, RecemNascidoONExceptionCode.MSG_ERRO_REG_REC_NASC_EXCLUSAO);
		verificarRecemNascidoUltimoRegistro(vo, listRecemNascidoVOs);
		recemNascidoRN.excluirRecemNascido(vo, listRecemNascidoVOs);
	}
	
	/**
	 * Se não for o último registro apresentar a mensagem MCO-00639 e cancelar o processamento
	 * @param vo
	 * @param listRecemNascidoVOs
	 * @throws ApplicationBusinessException
	 */
	private void verificarRecemNascidoUltimoRegistro(RecemNascidoVO vo, List<RecemNascidoVO> listRecemNascidoVOs) throws ApplicationBusinessException {
		if(!Objects.equals(vo.getIdFake(), listRecemNascidoVOs.get(listRecemNascidoVOs.size() -1).getIdFake())){
			throw new ApplicationBusinessException(RecemNascidoONExceptionCode.MCO_00639);
		}
	}

	/**
	 * Se o valor selecionado for “O” apresentar a mensagem “MCO-00722”.
	 * MCO-00722 - A cor OUTROS está desativada!
	 * 
	 * @param DominioCor cor
	 * @throws ApplicationBusinessException
	 */
	private void validarCor(DominioCor cor) throws ApplicationBusinessException{
		if(DominioCor.O.equals(cor)){
			throw new ApplicationBusinessException(RecemNascidoONExceptionCode.MCO_00722);
		}
	}
	
	/**
	 * Validar se o item foi alterado
	 * @param voAlterado
	 * @param voOriginal
	 * @return
	 */
	public boolean validarAlteracaoRecebemNascido(RecemNascidoVO voAlterado, RecemNascidoVO voOriginal){
		if (voAlterado != null && voOriginal != null) {
			boolean validarItensAlterados;
			validarItensAlterados(voAlterado, voOriginal);
			validarItensAlterados = validarItensAlterados(voAlterado, voOriginal);
			if (!validarItensAlterados) {
				validarItensAlterados = validarItensAlteradosContinuacao(voAlterado, voOriginal);
			}
			
			return validarItensAlterados;
		}
		return false;
	 }

	
	
	private boolean validarItensAlterados(RecemNascidoVO voAlterado, RecemNascidoVO voOriginal) {
		if(CoreUtil.igual(voAlterado.getDataHora(), voOriginal.getDataHora()) || 
		    CoreUtil.igual(voAlterado.getNome(), voOriginal.getNome()) || 
		    CoreUtil.igual(voAlterado.getSexo(), voOriginal.getSexo()) || 
		    CoreUtil.igual(voAlterado.getCor(), voOriginal.getCor()) ||
		    CoreUtil.igual(voAlterado.getPeso(), voOriginal.getPeso()) ||
		    CoreUtil.igual(voAlterado.getStrProntuario(), voOriginal.getStrProntuario()) ||
		    CoreUtil.igual(voAlterado.getProntuario(), voOriginal.getProntuario()) ||
		    CoreUtil.igual(voAlterado.getApagarUmMinuto(), voOriginal.getApagarCincoMinuto()) ||
		    CoreUtil.igual(voAlterado.getApagarCincoMinuto(), voOriginal.getApagarCincoMinuto()) ||
		    CoreUtil.igual(voAlterado.getApagarDezMinuto(), voOriginal.getApagarDezMinuto()) ||
		    CoreUtil.igual(voAlterado.getQtdDiasRupturaBolsa(), voOriginal.getQtdDiasRupturaBolsa()) ||
		    CoreUtil.igual(voAlterado.getQtdHorasRupturaBolsa(), voOriginal.getQtdHorasRupturaBolsa()) ||
		    CoreUtil.igual(voAlterado.getQtdMinutosRupturaBolsa(), voOriginal.getQtdMinutosRupturaBolsa()) ||
		    CoreUtil.igual(voAlterado.getColetadoSangueCordao(), voOriginal.getColetadoSangueCordao()) ||
		    CoreUtil.igual(voAlterado.getAspiracao(), voOriginal.getAspiracao())){
			return true;
		}
		return false;
	}
	private boolean validarItensAlteradosContinuacao(RecemNascidoVO voAlterado, RecemNascidoVO voOriginal) {
			if(CoreUtil.igual(voAlterado.getAspiracaoTet(), voOriginal.getAspiracaoTet()) ||
				CoreUtil.igual(voAlterado.getIndO2Inalatorio(), voOriginal.getIndO2Inalatorio()) ||
				CoreUtil.igual(voAlterado.getIndVentilacaoPorMascaraBalao(), voOriginal.getIndVentilacaoPorMascaraBalao()) ||
				CoreUtil.igual(voAlterado.getIndVentilacaoPorMascaraBabyPuff(), voOriginal.getIndVentilacaoPorMascaraBabyPuff()) ||
				CoreUtil.igual(voAlterado.getIndVentilacaoTetBalao(), voOriginal.getIndVentilacaoTetBalao()) ||
				CoreUtil.igual(voAlterado.getIndVentilacaoTetBabyPuff(), voOriginal.getIndVentilacaoTetBabyPuff()) ||
				CoreUtil.igual(voAlterado.getIndCateterismoVenoso(), voOriginal.getIndCateterismoVenoso()) ||
				CoreUtil.igual(voAlterado.getIndMassCardiacaExt(), voOriginal.getIndMassCardiacaExt()) ||
				CoreUtil.igual(voAlterado.getIndCpap(), voOriginal.getIndCpap()) ||
				CoreUtil.igual(voAlterado.getIndUrinou(), voOriginal.getIndUrinou()) ||
				CoreUtil.igual(voAlterado.getIndEvacuou(), voOriginal.getIndEvacuou()) ||
				CoreUtil.igual(voAlterado.getIndLavadoGastrico(), voOriginal.getIndLavadoGastrico()) ||
				CoreUtil.igual(voAlterado.getIndAmamentado(), voOriginal.getIndAmamentado()) ||
				CoreUtil.igual(voAlterado.getDestinoRecemnascido(), voOriginal.getDestinoRecemnascido()) ||
				CoreUtil.igual(voAlterado.getTempoClampeamentoCordao(), voOriginal.getTempoClampeamentoCordao()) ||
				CoreUtil.igual(voAlterado.getTemperaturaSalaParto(), voOriginal.getTemperaturaSalaParto()) ||
				CoreUtil.igual(voAlterado.getIndContatoPele(), voOriginal.getIndContatoPele()) ||
				CoreUtil.igual(voAlterado.getAspGastrVol(), voOriginal.getAspGastrVol()) ||
				CoreUtil.igual(voAlterado.getAspGastrAspecto(), voOriginal.getAspGastrAspecto()) ||
				CoreUtil.igual(voAlterado.getObservacao(), voOriginal.getObservacao())){
				return true;
		}
		return false;
	}
	
	/**
	 * ON14
	 * Buscar o valor do parâmetro P_TEMPO_CLAMPEAMENTO através da RN17.
	 * 2. Se o valor do Tempo de Clampeamento do Cordão (item 63) for diferente
	 * de NULO OU menor que Zero OU maior que o valor do parâmetro
	 * P_TEMPO_CLAMPEAMENTO apresentar a mensagem “MENSAGEM_TEMPO_CLAPEAMENTO” e
	 * cancelar o processamento.
	 */
	private void validarTempoClampeamento(Short tempoClampeamentoCordao) throws ApplicationBusinessException{
		notNull(tempoClampeamentoCordao, RecemNascidoONExceptionCode.MENSAGEM_TEMPO_CLAPEAMENTO);
		
		if (!CoreUtil.isBetweenRange(tempoClampeamentoCordao.intValue(), 0, recemNascidoRN.obterParametroTempoClampeamento())) {
			throw new ApplicationBusinessException(RecemNascidoONExceptionCode.MENSAGEM_TEMPO_CLAPEAMENTO);
		}
		
	}

	/**
	 * ON15 3. Se o valor do Temperatura em Sala de Parto (item 62) for NULO OU
	 * menor que o valor do parâmetro P_TEMPERATURA_SALA_PARTO_MIN OU maior que
	 * o valor do parâmetro P_TEMPERATURA_SALA_PARTO_MAX apresentar a mensagem
	 * “MENSAGEM_TEMPERTURA_SALA_PARTO” e cancelar o processamento.
	 */
	private void validarTemperaturaSalaParto(Short temperaturaSalaParto) throws ApplicationBusinessException{
		notNull(temperaturaSalaParto, RecemNascidoONExceptionCode.MENSAGEM_TEMPERTURA_SALA_PARTO);
		Integer temperaturaMin = recemNascidoRN.obterParametroTemperaturaSalaPartoMin();
		Integer temperaturaMax = recemNascidoRN.obterParametroTemperaturaSalaPartoMax();
		
		if (!CoreUtil.isBetweenRange(temperaturaSalaParto.intValue(), temperaturaMin, temperaturaMax)) {
			throw new ApplicationBusinessException(RecemNascidoONExceptionCode.MENSAGEM_TEMPERTURA_SALA_PARTO, temperaturaMin, temperaturaMax);
		}
	}
	
	/**
	 * #28358
	 * @param vo
	 * @return
	 */
	public boolean verificarExistRegistroRecemNascido(RecemNascidoVO  vo){
		return recemNascidoRN.verificarExistRegistroRecemNascido(vo);
	}
	
	/**
	 * #28358
	 * @param pacCodigo
	 * @param seqp
	 * @param numeroConsulta
	 * @param eventos
	 * @throws ApplicationBusinessException 
	 */
	public void validarConsultaFinalizada(Integer pacCodigo, Short seqp, Integer numeroConsulta, DominioEventoLogImpressao... eventos) throws ApplicationBusinessException {
		if(mcoLogImpressoesDAO.verificarExisteImpressao(pacCodigo, seqp, numeroConsulta, eventos)){
			throw new ApplicationBusinessException(RecemNascidoONExceptionCode.ERRO_CONSULTA_JA_FINALIZADA);
		}
	}
	
	/**
	 * #28358
	 * @param pacCodigoRecemNascido
	 * @return
	 */
	public Integer obterUltimoAtdSeqRecemNascidoPorPacCodigo(Integer pacCodigoRecemNascido) {
		return aghuFacade.obterUltimoAtdSeqRecemNascidoPorPacCodigo(pacCodigoRecemNascido);
	}
	
	/**
	 * Busca um parâmetro pelo nome
	 * 
	 * @param nome
	 * @param coluna
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private Object buscarParametroPorNome(EmergenciaParametrosEnum nome, EmergenciaParametrosColunas coluna) throws ApplicationBusinessException {
		Object retorno = null;
		try {
			retorno = parametroFacade.obterAghParametroPorNome(nome.toString(), coluna.toString());
		} catch (RuntimeException e) {
			throw new ApplicationBusinessException(RecemNascidoONExceptionCode.MENSAGEM_ERRO_PARAMETRO, nome);
		}
		if (retorno == null) {
			throw new ApplicationBusinessException(RecemNascidoONExceptionCode.MENSAGEM_ERRO_PARAMETRO, nome);
		}
		return retorno;
	}
	
	public boolean habilitarBotaoNotasAdicionais(Integer gsoPacCodigo, Short gsoSeqp, Byte rnaSeqp, Integer conNumero) throws ApplicationBusinessException {
		Integer pDiasNotasAdicionais = ((BigDecimal) this.buscarParametroPorNome(EmergenciaParametrosEnum.P_DIAS_NOTAS_ADICIONAIS,
				EmergenciaParametrosColunas.VLR_NUMERICO)).intValue();
		
		Integer cLogCons = mcoLogImpressoesDAO.cLogCons(gsoPacCodigo, gsoSeqp, conNumero);
		Integer cLogRn = mcoLogImpressoesDAO.cLogRn(gsoPacCodigo, gsoSeqp, rnaSeqp);
		Integer cLogEx = mcoLogImpressoesDAO.cLogEx(gsoPacCodigo, gsoSeqp, rnaSeqp);
		Integer cLogNas = mcoLogImpressoesDAO.cLogNas(gsoPacCodigo, gsoSeqp);
		
		if ((cLogCons != null && cLogCons > pDiasNotasAdicionais) ||
			(cLogRn != null && cLogRn > pDiasNotasAdicionais) ||
			(cLogEx != null && cLogEx > pDiasNotasAdicionais) ||
			(cLogNas != null && cLogNas > pDiasNotasAdicionais)){
			return false;
		}
		return true;
	}	

}
