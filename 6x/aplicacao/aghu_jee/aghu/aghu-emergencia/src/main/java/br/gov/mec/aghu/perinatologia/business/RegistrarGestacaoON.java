package br.gov.mec.aghu.perinatologia.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.dominio.DominioGravidez;
import br.gov.mec.aghu.exames.service.IExamesService;
import br.gov.mec.aghu.exames.vo.InformacaoExame;
import br.gov.mec.aghu.exames.vo.ItemSolicitacaoExame;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.vo.MedicamentoVO;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AipRegSanguineos;
import br.gov.mec.aghu.model.McoExameExterno;
import br.gov.mec.aghu.model.McoGestacoes;
import br.gov.mec.aghu.model.McoIndicacaoNascimento;
import br.gov.mec.aghu.model.McoResultadoExameSignifs;
import br.gov.mec.aghu.model.McoResultadoExameSignifsId;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.vo.DadosSanguineos;
import br.gov.mec.aghu.perinatologia.dao.McoGestacoesDAO;
import br.gov.mec.aghu.perinatologia.dao.McoIndicacaoNascimentoDAO;
import br.gov.mec.aghu.perinatologia.dao.McoLogImpressoesDAO;
import br.gov.mec.aghu.perinatologia.dao.McoNascimentosDAO;
import br.gov.mec.aghu.perinatologia.dao.McoRecemNascidosDAO;
import br.gov.mec.aghu.perinatologia.vo.DadosGestacaoVO;
import br.gov.mec.aghu.perinatologia.vo.ExameResultados;
import br.gov.mec.aghu.perinatologia.vo.ResultadoExameSignificativoPerinatologiaVO;
import br.gov.mec.aghu.registrocolaborador.service.IRegistroColaboradorService;
import br.gov.mec.aghu.registrocolaborador.vo.RapServidorConselhoVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.service.ServiceException;
/**
 * @author israel.haas
 */
@Stateless
public class RegistrarGestacaoON extends BaseBusiness {

	private static final String VLR_TEXTO = "vlrTexto";
	
	private static final String PARAMETRO_OBRIGATORIO = "Parâmetro obrigatório";

	private static final long serialVersionUID = -3423984755101821178L;

	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private IExamesService exameService;
	
	@Inject
	private McoGestacoesDAO mcoGestacoesDAO;
	
	@Inject
	private McoNascimentosDAO mcoNascimentosDAO;
	
	@Inject
	private McoRecemNascidosDAO mcoRecemNascidosDAO;
	
	@Inject
	private McoLogImpressoesDAO mcoLogImpressoesDAO;

	@Inject
	private McoIndicacaoNascimentoDAO mcoIndicacaoNascimentoDAO ;
	
	@Inject
	private RegistrarGestacaoRN registrarGestacaoRN;
	
	@EJB
	private McoResultadoExameSignifsRN mcoResultadoExameSignifsRN;
	
	@Inject
	private IRegistroColaboradorService registroColaboradorService;

	@EJB
	private IFarmaciaFacade farmaciaFacade;

	@Override
	protected Log getLogger() {
		return null;
	}
	
	private enum RegistrarGestacaoONExceptionCode implements BusinessExceptionCode {
		ERROR_SEM_COMUNICACAO_OBSTETRICO, MENSAGEM_SERVICO_INDISPONIVEL, ERRO_GRAVIDEZ_CONFIRMADA_GESTA_MAIOR_ZERO,
		ERRO_GESTA_SOMA_PARA_CESAREA_ABORTO_ECTOPICA, ERRO_GESTA_PACIENTE_NAO_GRAVIDA, ERRO_GRAVIDEZCONFIRMADA_PACIENTENAOGRAVIDA,
		ERRO_ALTERACAO_NUMERO_GESTACAO, MENSAGEM_ERRO_OBTER_PARAMETRO, ERRO_IDADE_GESTACIONAL, ERRO_IDADE_GESTACIONAL_DIAS,
		ERRO_DATA_ECO_MENOR_365DIAS, ERRO_GESTA_HUM_GRAVIDEZ_CONFIRMADA
	}
	
	public String preencherInformacoesPaciente(String nomePaciente, String idadeFormatada, Integer prontuario, Integer numeroConsulta) {
		StringBuffer retorno = new StringBuffer(nomePaciente)
		.append(" - ")
		.append(idadeFormatada)
		.append(" - Prontuário: ")
		.append(CoreUtil.formataProntuario(prontuario))
		.append(" - Consulta: ")
		.append(numeroConsulta != null ? numeroConsulta : "");
		return retorno.toString();
	}
	
	public DadosGestacaoVO pesquisarMcoGestacaoPorId(Integer pacCodigo, Short seqp, Integer numeroConsulta) throws ApplicationBusinessException {
		
		McoGestacoes mcoGestacoes = this.mcoGestacoesDAO.pesquisarMcoGestacaoPorId(pacCodigo, seqp);
		
		DadosGestacaoVO dadosGestacaoVO = new DadosGestacaoVO();
		dadosGestacaoVO.setGestacao(mcoGestacoes.getGesta());
		dadosGestacaoVO.setParto(mcoGestacoes.getPara());
		dadosGestacaoVO.setCesariana(mcoGestacoes.getCesarea());
		dadosGestacaoVO.setAborto(mcoGestacoes.getAborto());
		dadosGestacaoVO.setEctopica(mcoGestacoes.getEctopica());
		dadosGestacaoVO.setGravidez(mcoGestacoes.getGravidez());
		dadosGestacaoVO.setGemelar(mcoGestacoes.getGemelar());
		dadosGestacaoVO.setDtUltimaMenstruacao(mcoGestacoes.getDtUltMenstruacao());
		dadosGestacaoVO.setDtPrimeiraEco(mcoGestacoes.getDtPrimEco());
		dadosGestacaoVO.setEcoSemanas(mcoGestacoes.getIgPrimEco());
		dadosGestacaoVO.setEcoDias(mcoGestacoes.getIgPrimEcoDias());
		dadosGestacaoVO.setIgAtualSemanas(mcoGestacoes.getIgAtualSemanas());
		dadosGestacaoVO.setIgAtualDias(mcoGestacoes.getIgAtualDias());
		dadosGestacaoVO.setDtProvavelParto(mcoGestacoes.getDtProvavelParto());
		dadosGestacaoVO.setNroConsultas(mcoGestacoes.getNumConsPrn());
		dadosGestacaoVO.setDtPrimeiraConsulta(mcoGestacoes.getDtPrimConsPrn());
		dadosGestacaoVO.setVatCompleta(mcoGestacoes.getVatCompleta());
		dadosGestacaoVO.setMesmoPai(mcoGestacoes.getIndMesmoPai());
		dadosGestacaoVO.setTipoSanguineoPai(mcoGestacoes.getGrupoSanguineoPai());
		dadosGestacaoVO.setFatorRHPai(mcoGestacoes.getFatorRhPai());
		dadosGestacaoVO.setJustificativa(mcoGestacoes.getJustificativa());
		dadosGestacaoVO.setSerMatricula(mcoGestacoes.getSerMatricula());
		dadosGestacaoVO.setSerVinCodigo(mcoGestacoes.getSerVinCodigo());
		dadosGestacaoVO.setSeqp(mcoGestacoes.getId().getSeqp());
		dadosGestacaoVO.setPacCodigo(mcoGestacoes.getId().getPacCodigo());
		dadosGestacaoVO.setCriadoEm(mcoGestacoes.getCriadoEm());
		dadosGestacaoVO.setUsoMedicamentos(mcoGestacoes.getUsoMedicamentos());
		dadosGestacaoVO.setDthrSumarioAltaMae(mcoGestacoes.getDthrSumarioAltaMae());
		
		dadosGestacaoVO.setInformacoesIg(obterInformacoesIg(mcoGestacoes, pacCodigo, seqp, numeroConsulta));
		
		dadosGestacaoVO.setObservacaoExame(mcoGestacoes.getObsExames());
		
		return dadosGestacaoVO;
	}
	
	public DadosSanguineos obterRegSanguineosPorCodigoPaciente(Integer pacCodigo, Byte seqp) throws ApplicationBusinessException {
		
		Byte seqpByte = seqp.byteValue();
		if (pacCodigo == null || seqpByte == null) {
			throw new ApplicationBusinessException("Parâmetros obrigatórios", Severity.ERROR);
		}
		try {
			AipRegSanguineos grupo = pacienteFacade.obterRegSanguineosPorCodigoPaciente(pacCodigo, seqpByte);
			if (grupo != null) {
				DadosSanguineos dadosSanguineos = new DadosSanguineos();
				dadosSanguineos.setCoombs(grupo.getCoombs() != null ? grupo.getCoombs().toString() : null);
				dadosSanguineos.setFatorRh(grupo.getFatorRh());
				dadosSanguineos.setGrupoSanguineo(grupo.getGrupoSanguineo());
				return dadosSanguineos;
			}
			return null;
		} catch (RuntimeException e) {
			throw new ApplicationBusinessException(e.getMessage(), Severity.ERROR);
		}
	}
	
	private String obterInformacoesIg(McoGestacoes mcoGestacoes, Integer pacCodigo, Short seqp, Integer numeroConsulta) throws ApplicationBusinessException {
		
		String retorno = null;
		
		Boolean existeNascimento = this.mcoNascimentosDAO.verificaExisteNascimento(pacCodigo, seqp);
		Boolean existeRecemNascido = this.mcoRecemNascidosDAO.verificaExisteRecemNascido(pacCodigo, seqp);
		
		if (existeNascimento || existeRecemNascido) {
			Integer igSem = mcoGestacoes.getIgAtualSemanas() != null ? mcoGestacoes.getIgAtualSemanas().intValue() : 0;
			Integer igDias = mcoGestacoes.getIgAtualDias() != null ? mcoGestacoes.getIgAtualDias().intValue() : 0;
			Integer igSemanas = igSem * 7;
			igSem = igSemanas + igDias;
			Integer igSem7 = igSem / 7;
			igDias = igSem - (igSem7 * 7);
			igSem = igSem / 7;
			
			if (igSem <= 42) {
				retorno = "Idade Gestacional de hoje calculada conforme última consulta ".concat(igSem.toString()).concat(" sem./ ")
						.concat(igDias.toString()).concat(" dias.");
			} else {
				retorno = "";
			}
		} else {
			try {
				
				if (pacCodigo == null || numeroConsulta == null || seqp == null) {
					throw new ApplicationBusinessException(PARAMETRO_OBRIGATORIO, Severity.ERROR);
				}
				
				List<Date> listaDatas = this.ambulatorioFacade.pesquisarPorPacienteConsultaGestacao(pacCodigo, numeroConsulta, seqp);
				if (listaDatas.isEmpty()) {
					retorno = "";
				} else {
					Date dtRetorno = DateUtil.truncaData(listaDatas.get(0));
					Date dtAtual = DateUtil.truncaData(new Date());
					Integer igSem = mcoGestacoes.getIgAtualSemanas() != null ? mcoGestacoes.getIgAtualSemanas().intValue() : 0;
					Integer igDias = mcoGestacoes.getIgAtualDias() != null ? mcoGestacoes.getIgAtualDias().intValue() : 0;
					Integer vDias = DateUtil.calcularDiasEntreDatas(dtRetorno, dtAtual);
					Integer igSemanas = igSem * 7;
					igSem = igSemanas + vDias + igDias;
					Integer igSem7 = igSem / 7;
					igDias = igSem - (igSem7 * 7);
					igSem = igSem / 7;
					
					if (igSem <= 42) {
						retorno = "Idade Gestacional de hoje calculada conforme última consulta ".concat(igSem.toString()).concat(" sem./ ")
								.concat(igDias.toString()).concat(" dias.");
					} else {
						retorno = "";
					}
				}
			} catch (RuntimeException e) {
				retorno = "";
			}
		}
		return retorno;
	}
	
	
	/**
	 * 
	 * @param dadosGestacaoVO
	 * @param dadosGestacaoVOOriginal
	 * @param pacCodigo
	 * @param seqp
	 * @return true se deve exibir a modal de justificativa
	 * @throws ApplicationBusinessException
	 */
	public boolean preGravar(DadosGestacaoVO dadosGestacaoVO, DadosGestacaoVO dadosGestacaoVOOriginal,
			Integer pacCodigo, Short seqp) throws ApplicationBusinessException {
		
		boolean exibirModalJustificativa = false;
		
		validarGestacao(dadosGestacaoVO, dadosGestacaoVOOriginal, pacCodigo, seqp);
		validarDataPrimEco(dadosGestacaoVO, dadosGestacaoVOOriginal);
		validarEcoSemanasDias(dadosGestacaoVO, true, false, false);
		validarEcoSemanasDias(dadosGestacaoVO, false, false, false);
		//insert
		if(dadosGestacaoVOOriginal.getGestacao()==null){
			exibirModalJustificativa = verificarJustificativa(
					dadosGestacaoVO, pacCodigo, exibirModalJustificativa);
		}else{
			//update
			if(dadosGestacaoVO.getGestacao()!=null && 
					!dadosGestacaoVO.getGestacao().equals(dadosGestacaoVOOriginal.getGestacao())){
				exibirModalJustificativa = verificarJustificativa(
						dadosGestacaoVO, pacCodigo, exibirModalJustificativa);
			}
		}	
		return exibirModalJustificativa;
	}

	private boolean verificarJustificativa(DadosGestacaoVO dadosGestacaoVO,
			Integer pacCodigo, boolean exibirModalJustificativa)
			throws ApplicationBusinessException {
		
		if (!this.possuiJustificativa(pacCodigo, dadosGestacaoVO)) {
			// Estória #28045
			exibirModalJustificativa = true;
		}
		return exibirModalJustificativa;
	}
	
	private McoResultadoExameSignifs montarMcoResultadoExameSignifs(ResultadoExameSignificativoPerinatologiaVO resultadoExameSignificativoPerinatologiaVO) {
		McoResultadoExameSignifs resultado = new McoResultadoExameSignifs(); 
		McoResultadoExameSignifsId id = this.montarIdResultadoExame(resultadoExameSignificativoPerinatologiaVO);
		resultado.setId(id);
		resultado.setDataRealizacao(resultadoExameSignificativoPerinatologiaVO.getDataRealizacao());
		resultado.setEmaExaSigla(resultadoExameSignificativoPerinatologiaVO.getEmaExaSigla());
		resultado.setEmaManSeq(resultadoExameSignificativoPerinatologiaVO.getEmaManSeq());
		if(resultadoExameSignificativoPerinatologiaVO.getEexSeq() != null){
			McoExameExterno exameExterno = new McoExameExterno();
			exameExterno.setSeq(resultadoExameSignificativoPerinatologiaVO.getEexSeq());
			resultado.setExameExterno(exameExterno);
		}
		resultado.setIseSeqp(resultadoExameSignificativoPerinatologiaVO.getIseSeqp());
		resultado.setIseSoeSeq(resultadoExameSignificativoPerinatologiaVO.getIseSoeSeq());
		resultado.setResultado(resultadoExameSignificativoPerinatologiaVO.getResultado());		
		return resultado;
	}

	private McoResultadoExameSignifsId montarIdResultadoExame(
			ResultadoExameSignificativoPerinatologiaVO resultadoExameSignificativoPerinatologiaVO) {
		McoResultadoExameSignifsId id = new McoResultadoExameSignifsId();
		id.setGsoPacCodigo(resultadoExameSignificativoPerinatologiaVO.getGsoPacCodigo());
		id.setGsoSeqp(resultadoExameSignificativoPerinatologiaVO.getGsoSeqp());
		id.setSeqp(resultadoExameSignificativoPerinatologiaVO.getSeqp());
		return id;
	}
	
	public void gravar(DadosGestacaoVO dadosGestacaoVO, DadosGestacaoVO dadosGestacaoVOOriginal, Integer pacCodigo, Short seqp,
			List<ExameResultados> examesResultados, List<ResultadoExameSignificativoPerinatologiaVO> resultadosExamesExcluidos)
			throws ApplicationBusinessException {
		
		this.gravar(dadosGestacaoVO, dadosGestacaoVOOriginal, pacCodigo, seqp);
				 
		for (int i = 0; i < examesResultados.size(); i++) {
			ExameResultados exame = examesResultados.get(i);
			for (int j = 0; j < exame.getResultados().length; j++) {
				ResultadoExameSignificativoPerinatologiaVO resultadoExame = exame.getResultados()[j];
				if (resultadoExame.getSeqp() == null) {
					resultadoExame.setGsoPacCodigo(pacCodigo);
					resultadoExame.setGsoSeqp(dadosGestacaoVOOriginal.getSeqp());
					McoResultadoExameSignifs mcoResultadoExameSignifs = this.montarMcoResultadoExameSignifs(resultadoExame);
					this.mcoResultadoExameSignifsRN.incluir(mcoResultadoExameSignifs);
					resultadoExame.setSeqp(mcoResultadoExameSignifs.getId().getSeqp());
				} else {
					McoResultadoExameSignifsId id = this.montarIdResultadoExame(resultadoExame);
					this.mcoResultadoExameSignifsRN.atualizarDescricaoDataRealizacao(id, resultadoExame.getResultado(), resultadoExame.getDataRealizacao());
				}
			}
		}

		for (ResultadoExameSignificativoPerinatologiaVO resultadoExame : resultadosExamesExcluidos) {
			McoResultadoExameSignifsId id = this.montarIdResultadoExame(resultadoExame);
			this.mcoResultadoExameSignifsRN.excluir(id);
		}
		resultadosExamesExcluidos.clear();
	}
	
	public void gravar(DadosGestacaoVO dadosGestacaoVO, DadosGestacaoVO dadosGestacaoVOOriginal,
			Integer pacCodigo, Short seqp) throws ApplicationBusinessException {
		
		if (seqp != null) {
			this.registrarGestacaoRN.atualizarMcoGestacoes(dadosGestacaoVO, dadosGestacaoVOOriginal, pacCodigo, seqp);
			this.removerRegSanguineosSemDadosPorPaciente(pacCodigo);
			
			if (dadosGestacaoVO.getTipoSanguineoMae() != null || dadosGestacaoVO.getFatorRHMae() != null
					|| dadosGestacaoVO.getCoombs() != null) {
				
				DadosSanguineos dadosSanguineos = this.obterRegSanguineosPorCodigoPaciente(pacCodigo, seqp.byteValue());
				if (dadosSanguineos != null) {
					this.registrarGestacaoRN.atualizarRegSanguineo(pacCodigo, seqp, dadosGestacaoVO);
					
				} else {
					this.registrarGestacaoRN.inserirRegSanguineo(pacCodigo, dadosGestacaoVO);
				}
			}
		} else {
			this.setarValoresDefault(dadosGestacaoVO);
			this.registrarGestacaoRN.inserirMcoGestacoes(dadosGestacaoVO, dadosGestacaoVOOriginal, pacCodigo);
			if (dadosGestacaoVO.getTipoSanguineoMae() != null || dadosGestacaoVO.getFatorRHMae() != null
					|| dadosGestacaoVO.getCoombs() != null) {
				this.registrarGestacaoRN.inserirRegSanguineo(pacCodigo, dadosGestacaoVO);
			}
		}
	}
	
	public void removerRegSanguineosSemDadosPorPaciente(Integer pacCodigo) throws ApplicationBusinessException {

		if (pacCodigo == null) {
			throw new ApplicationBusinessException(PARAMETRO_OBRIGATORIO, Severity.ERROR);
		}
		try {
			List<AipRegSanguineos> dados = pacienteFacade.listarRegSanguineosSemDadosPorPaciente(pacCodigo);
			if (dados != null && !dados.isEmpty()) {
				for (AipRegSanguineos aipRegSanguineos : dados) {
					pacienteFacade.removerAipRegSanguineos(aipRegSanguineos);
				}
			}
		} catch (RuntimeException e) {
			throw new ApplicationBusinessException(e.getMessage(),Severity.ERROR);
		}
	}
	
	private void validarGestacao(DadosGestacaoVO dadosGestacaoVO, DadosGestacaoVO dadosGestacaoVOOriginal,
			Integer pacCodigo, Short seqp) throws ApplicationBusinessException {
		
		setarValoresDefault(dadosGestacaoVO);
		Byte gestacao = dadosGestacaoVO.getGestacao();
		Byte parto = dadosGestacaoVO.getParto();
		Byte cesariana = dadosGestacaoVO.getCesariana();
		Byte aborto = dadosGestacaoVO.getAborto();
		Byte ectopica = dadosGestacaoVO.getEctopica();
		
		if (dadosGestacaoVO.getGravidez().equals(DominioGravidez.GCO)) {
			validaGestacaoGCO(dadosGestacaoVO, gestacao, parto, cesariana, aborto, ectopica);
			
		} else if (dadosGestacaoVO.getGravidez().equals(DominioGravidez.PNG)
				&& gestacao.compareTo((byte) (parto + cesariana + aborto + ectopica)) != 0) {
			throw new ApplicationBusinessException(RegistrarGestacaoONExceptionCode.ERRO_GESTA_PACIENTE_NAO_GRAVIDA);
			
		} else if (gestacao == 0 && (dadosGestacaoVO.getParto() != 0 || cesariana != 0
					|| aborto != 0 || ectopica != 0)) {
			throw new ApplicationBusinessException(RegistrarGestacaoONExceptionCode.ERRO_GRAVIDEZCONFIRMADA_PACIENTENAOGRAVIDA);
		}
		if (seqp != null && CoreUtil.modificados(dadosGestacaoVO.getGestacao(), dadosGestacaoVOOriginal.getGestacao())) {
			if (this.mcoLogImpressoesDAO.verificaExisteLogImpressao(pacCodigo, seqp)) {
				throw new ApplicationBusinessException(RegistrarGestacaoONExceptionCode.ERRO_ALTERACAO_NUMERO_GESTACAO);
			}
		}
	}

	private void setarValoresDefault(DadosGestacaoVO dadosGestacaoVO) {
		if (dadosGestacaoVO.getCesariana() == null) {
			dadosGestacaoVO.setCesariana(Byte.valueOf("0"));
		}
		if (dadosGestacaoVO.getAborto() == null) {
			dadosGestacaoVO.setAborto(Byte.valueOf("0"));
		}
		if (dadosGestacaoVO.getEctopica() == null) {
			dadosGestacaoVO.setEctopica(Byte.valueOf("0"));
		}
		if (dadosGestacaoVO.getParto() == null) {
			dadosGestacaoVO.setParto(Byte.valueOf("0"));
		}
	}
	
	private void validaGestacaoGCO(DadosGestacaoVO dadosGestacaoVO, Byte gestacao, Byte parto, Byte cesariana,
			Byte aborto, Byte ectopica) throws ApplicationBusinessException {
		if (gestacao <= 0) {
			throw new ApplicationBusinessException(RegistrarGestacaoONExceptionCode.ERRO_GRAVIDEZ_CONFIRMADA_GESTA_MAIOR_ZERO);
			
		} else if (gestacao > 1 && gestacao > (parto + cesariana + aborto + ectopica + 1)) {
			throw new ApplicationBusinessException(RegistrarGestacaoONExceptionCode.ERRO_GESTA_SOMA_PARA_CESAREA_ABORTO_ECTOPICA);
			
		} else if (gestacao == 1 && (parto != 0 || cesariana != 0
				|| aborto != 0 || ectopica != 0)) {
			throw new ApplicationBusinessException(RegistrarGestacaoONExceptionCode.ERRO_GESTA_HUM_GRAVIDEZ_CONFIRMADA);
		}
	}
	
	private void validarDataPrimEco(DadosGestacaoVO dadosGestacaoVO,
			DadosGestacaoVO dadosGestacaoVOOriginal) throws ApplicationBusinessException {
		
		Date dtMenos1Ano = DateUtil.adicionaDias(DateUtil.truncaData(new Date()), -365);
		
		if (CoreUtil.modificados(dadosGestacaoVO.getDtPrimeiraEco(), dadosGestacaoVOOriginal.getDtPrimeiraEco())
				&& DateUtil.validaDataMenor(dadosGestacaoVO.getDtPrimeiraEco(), dtMenos1Ano)) {
			
			throw new ApplicationBusinessException(RegistrarGestacaoONExceptionCode.ERRO_DATA_ECO_MENOR_365DIAS);
		}
	}
	
	private void validarDataPrimEcoCompararDataAtual(DadosGestacaoVO dadosGestacaoVO) throws ApplicationBusinessException {
		
		Date dtMenos1Ano = DateUtil.adicionaDias(DateUtil.truncaData(new Date()), -365);
		
		if (DateUtil.validaDataMenor(dadosGestacaoVO.getDtPrimeiraEco(), dtMenos1Ano)) {
			throw new ApplicationBusinessException(RegistrarGestacaoONExceptionCode.ERRO_DATA_ECO_MENOR_365DIAS);
		}
	}
	
	public void validarEcoSemanasDias(DadosGestacaoVO dadosGestacaoVO, boolean isSemanas,
			boolean atualizar, boolean calcularIg) throws ApplicationBusinessException {
		Byte primEcoSemanas = dadosGestacaoVO.getEcoSemanas();
		Byte primEcoDias = dadosGestacaoVO.getEcoDias();
		if (isSemanas) {
			if (primEcoSemanas != null) {
				if (primEcoSemanas < 1 || primEcoSemanas > 44) {
					throw new ApplicationBusinessException(RegistrarGestacaoONExceptionCode.ERRO_IDADE_GESTACIONAL);
				}
			}
		} else if (primEcoDias != null) {
			if (primEcoDias < 0 || primEcoDias > 7) {
				throw new ApplicationBusinessException(RegistrarGestacaoONExceptionCode.ERRO_IDADE_GESTACIONAL_DIAS);
			}
		}
		if (calcularIg) {
			calcularIgAtualSemanasDias(dadosGestacaoVO, primEcoSemanas, primEcoDias, atualizar);
		}
		
		//RN14 - APLICAR NO ITEM 12 DO CONFORME SOLICITA NO QUADRO DESCRITIVO
		validarDataPrimEcoCompararDataAtual(dadosGestacaoVO);
			
	}
	
	private void calcularIgAtualSemanasDias(DadosGestacaoVO dadosGestacaoVO, Byte primEcoSemanas, Byte primEcoDias, boolean atualizar) {
		Integer igSem = 0, igDias = 0;
		
		if (primEcoSemanas != null && primEcoSemanas <= 20) {
			Date dtPrimEco = DateUtil.truncaData(dadosGestacaoVO.getDtPrimeiraEco());
			Date dtAtual = DateUtil.truncaData(new Date());
			igSem = dadosGestacaoVO.getEcoSemanas() != null ? dadosGestacaoVO.getEcoSemanas().intValue() : 0;
			igDias = dadosGestacaoVO.getEcoDias() != null ? dadosGestacaoVO.getEcoDias().intValue() : 0;
			Integer vDias = DateUtil.calcularDiasEntreDatas(dtPrimEco, dtAtual);
			Integer igSemanas = igSem * 7;
			igSem = igSemanas + vDias + igDias;
			Integer igSem7 = igSem / 7;
			igDias = igSem - (igSem7 * 7);
			igSem = igSem / 7;
			
			dadosGestacaoVO.setIgAtualSemanas(igSem.byteValue());
			dadosGestacaoVO.setIgAtualDias(igDias.byteValue());
			dadosGestacaoVO.setDtInformadaIg(new Date());
		} else {
			dadosGestacaoVO.setIgAtualSemanas(null);
			dadosGestacaoVO.setIgAtualDias(null);
			dadosGestacaoVO.setDtProvavelParto(null);
		}	
//		} else if (atualizar) {
//			dadosGestacaoVO.setIgAtualSemanas(null);
//			dadosGestacaoVO.setIgAtualDias(null);
//			dadosGestacaoVO.setDtProvavelParto(null);
//		}
	}
	
	public void calcularDtProvavelParto(DadosGestacaoVO dadosGestacaoVO) {
		Byte igAtualSemanas = dadosGestacaoVO.getIgAtualSemanas();
		Byte igAtualDias = dadosGestacaoVO.getIgAtualDias();
		
		if (igAtualSemanas != null || igAtualDias != null) {
			
			igAtualSemanas = (igAtualSemanas != null ? igAtualSemanas : 0);
			igAtualDias = (igAtualDias != null ? igAtualDias : 0);
			
			dadosGestacaoVO.setDtInformadaIg(new Date());
			Integer igSemanas = igAtualSemanas * 7;
			Integer resultado = -igSemanas - igAtualDias + 280;
			
			dadosGestacaoVO.setDtProvavelParto(DateUtil.adicionaDias(new Date(), resultado));
		}
	}
	
	private boolean possuiJustificativa(Integer pacCodigo, DadosGestacaoVO dadosGestacaoVO) throws ApplicationBusinessException {
		List<McoGestacoes> listGestas = this.mcoGestacoesDAO.listaGestacaoPorPaciente(pacCodigo, dadosGestacaoVO.getGestacao());
//		for (McoGestacoes gesta : listGestas) {
//			if (gesta.getJustificativa() != null) {
//				return false;
//			}
//		}
		// Já seta a mensagem da modal
		Object pConsisteGesta;
		pConsisteGesta = buscarParametroPorNome("P_CONSISTE_GESTA", VLR_TEXTO);
		String consisteGesta = null;
		if (pConsisteGesta != null && !listGestas.isEmpty()) {
			consisteGesta = ((String) pConsisteGesta);
			if (consisteGesta.equalsIgnoreCase("S")) {
				dadosGestacaoVO.setMensagemModalJustificativa(this.montaMensagemJustificativa(listGestas.get(listGestas.size() - 1)));
			} else {
				return true;
			}
		} else {
			return true;
		}
		return false;
	}
	
	private String montaMensagemJustificativa(McoGestacoes gestacao) {
		String retorno = "A informação gestação ".concat(gestacao.getGesta().toString()).concat(" já existe para o paciente em ")
				.concat(DateUtil.obterDataFormatada(gestacao.getCriadoEm(), "dd/MM/yyyy")).concat(".")
				.concat(" Deseja manter a informação atual mediante justificativa?");
		
		return retorno;
	}
	
	public void validarParto(DadosGestacaoVO dadosGestacaoVO, DadosGestacaoVO dadosGestacaoVOOriginal,
			Integer pacCodigo, Short seqp) throws ApplicationBusinessException {
		
		if (pacCodigo == null) {
			throw new ApplicationBusinessException(PARAMETRO_OBRIGATORIO, Severity.ERROR);
		}		
		
		try {
			if (!this.pacienteFacade.existsRegSanguineosPorCodigoPaciente(pacCodigo)) {
				Object pCodSangue = buscarParametroPorNome("P_COD_SANGUE", "vlrNumerico");
				Integer vMaterial = null;
				if (pCodSangue != null) {
					vMaterial = ((BigDecimal) pCodSangue).intValue();
				}
				if (dadosGestacaoVO.getTipoSanguineoMae() == null) {
					preencherGrupoSanguineoMae(dadosGestacaoVO, vMaterial, pacCodigo);
				}
				if (dadosGestacaoVO.getFatorRHMae() == null) {
					preencherFatorRHMae(dadosGestacaoVO, vMaterial, pacCodigo);
				}
				if (dadosGestacaoVO.getCoombs() == null) {
					preencherCoombs(dadosGestacaoVOOriginal, vMaterial, pacCodigo);
				}
			}
		} catch (RuntimeException e) {
			throw new ApplicationBusinessException(RegistrarGestacaoONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
	}
	
	private Object buscarParametroPorNome(String nome, String coluna) throws ApplicationBusinessException {
		Object retorno = null;
		try {
			retorno = parametroFacade.obterAghParametroPorNome(nome, coluna);
		} catch (RuntimeException e) {
			throw new ApplicationBusinessException(RegistrarGestacaoONExceptionCode.MENSAGEM_ERRO_OBTER_PARAMETRO, nome);
		}
		return retorno;
	}
	
	private void preencherGrupoSanguineoMae(DadosGestacaoVO dadosGestacaoVO, Integer vMaterial,
			Integer pacCodigo) throws ApplicationBusinessException {
		try {
			Object pExameGrupoSang = buscarParametroPorNome("P_EXAME_GRUPO_SANGUINEO", VLR_TEXTO);
			String vExame = null;
			if (pExameGrupoSang != null) {
				vExame = ((String) pExameGrupoSang);
			}
			List<ItemSolicitacaoExame> listaItemSolExame = this.exameService
					.listarItemSolicitacaoExamePorSiglaMaterialPaciente(vExame, vMaterial, pacCodigo);
			
			for (ItemSolicitacaoExame item : listaItemSolExame) {
				InformacaoExame informacaoExame = this.exameService.buscarInformacaoExamePorItem(item.getSoeSeq(), item.getSeqp());
				if (informacaoExame.getDescricao() != null && !informacaoExame.getDescricao().isEmpty()) {
					String descricao = informacaoExame.getDescricao();
					if (descricao.equalsIgnoreCase("INDETERMINADO")) {
						dadosGestacaoVO.setTipoSanguineoMae(null);
						
					} else {
						dadosGestacaoVO.setTipoSanguineoMae(descricao);
					}
				}
			}
			
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(RegistrarGestacaoONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		} catch (RuntimeException e) {
			throw new ApplicationBusinessException(RegistrarGestacaoONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
	}
	
	private void preencherFatorRHMae(DadosGestacaoVO dadosGestacaoVO, Integer vMaterial,
			Integer pacCodigo) throws ApplicationBusinessException {
		try {
			Object pExameRH = buscarParametroPorNome("P_EXAME_RH", VLR_TEXTO);
			String vExame = null;
			if (pExameRH != null) {
				vExame = ((String) pExameRH);
			}
			List<ItemSolicitacaoExame> listaItemSolExame = this.exameService
					.listarItemSolicitacaoExamePorSiglaMaterialPaciente(vExame, vMaterial, pacCodigo);
			
			for (ItemSolicitacaoExame item : listaItemSolExame) {
				InformacaoExame informacaoExame = this.exameService.buscarInformacaoExamePorItem(item.getSoeSeq(), item.getSeqp());
				if (informacaoExame.getDescricao() != null && !informacaoExame.getDescricao().isEmpty()) {
					String descricao = informacaoExame.getDescricao();
					if (descricao.equalsIgnoreCase("POSITIVO")) {
						dadosGestacaoVO.setFatorRHMae("+");
						
					} else if (descricao.equalsIgnoreCase("NEGATIVO")) {
						dadosGestacaoVO.setFatorRHMae("-");
						
					} else if (descricao.equalsIgnoreCase("INDETERMINADO")) {
						dadosGestacaoVO.setFatorRHMae(null);
					}
				}
			}
			
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(RegistrarGestacaoONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		} catch (RuntimeException e) {
			throw new ApplicationBusinessException(RegistrarGestacaoONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
	}
	
	private void preencherCoombs(DadosGestacaoVO dadosGestacaoVO, Integer vMaterial,
			Integer pacCodigo) throws ApplicationBusinessException {
		try {
			Object pExameCoombs = buscarParametroPorNome("P_EXAME_COOMBS", VLR_TEXTO);
			String vExame = null;
			if (pExameCoombs != null) {
				vExame = ((String) pExameCoombs);
			}
			List<ItemSolicitacaoExame> listaItemSolExame = this.exameService
					.listarItemSolicitacaoExamePorSiglaMaterialPaciente(vExame, vMaterial, pacCodigo);
			
			for (ItemSolicitacaoExame item : listaItemSolExame) {
				InformacaoExame informacaoExame = this.exameService.buscarInformacaoExamePorItem(item.getSoeSeq(), item.getSeqp());
				if (informacaoExame.getDescricao() != null && !informacaoExame.getDescricao().isEmpty()) {
					String descricao = informacaoExame.getDescricao();
					if (descricao.equals("NÃO REALIZADO") || descricao.equals("TRAÇOS")) {
						dadosGestacaoVO.setCoombs(null);
						
					} else if (descricao.equalsIgnoreCase("NEGATIVO")) {
						dadosGestacaoVO.setCoombs("N");
						
					} else if (descricao.equalsIgnoreCase("POSITIVO") || descricao.equalsIgnoreCase("POSITIVO +")
							|| descricao.equalsIgnoreCase("POSITIVO ++") || descricao.equalsIgnoreCase("POSITIVO +++")) {
						dadosGestacaoVO.setCoombs("P");
					}
				}
			}
			
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(RegistrarGestacaoONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		} catch (RuntimeException e) {
			throw new ApplicationBusinessException(RegistrarGestacaoONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
	}
	
	public List<RapServidorConselhoVO> pesquisarServidoresConselho(String strPesquisa) throws ApplicationBusinessException{
		List<RapServidorConselhoVO> listaPesquisarService;
		try {
			listaPesquisarService = registroColaboradorService.pesquisarServidoresConselho(strPesquisa, obterCentroCustos());
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(RegistrarGestacaoONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
		return listaPesquisarService;
	}
	public Long pesquisarServidoresConselhoCount(String strPesquisa) throws ApplicationBusinessException{
		Long count;
		try {
			count= registroColaboradorService.pesquisarServidoresConselhoCount(strPesquisa, obterCentroCustos());
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(RegistrarGestacaoONExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
		return count;
	}

	public List<McoIndicacaoNascimento> pesquisarIndicacoesNascimentoPorSeqNome(
			String objPesquisa) {
		return mcoIndicacaoNascimentoDAO.pesquisarIndicacoesNascimentoPorSeqNome(objPesquisa);
	}
	public Long pesquisarIndicacoesNascimentoPorSeqNomeCount(
			String objPesquisa) {
		return mcoIndicacaoNascimentoDAO.pesquisarIndicacoesNascimentoPorSeqNomeCount(objPesquisa);
	}

	public Long obterMcoMedicamentoPorSeqOuDescricaoCount(Object objPesquisa) throws ServiceException{
		return farmaciaFacade.pesquisarMedicamentoAtivoPorCodigoOuDescricaoCount((String) objPesquisa);
	}

	public List<MedicamentoVO> obterMcoMedicamentoPorSeqOuDescricao(Object objPesquisa) throws ServiceException {
		
		List<MedicamentoVO> result = new ArrayList<MedicamentoVO>();
		List<AfaMedicamento> lista = farmaciaFacade.pesquisarMedicamentoAtivoPorCodigoOuDescricao((String) objPesquisa, 100);
		if (lista != null && !lista.isEmpty()) {
			for (AfaMedicamento afaMedicamento : lista) {
				result.add(new MedicamentoVO(afaMedicamento.getMatCodigo(), afaMedicamento.getDescricao()));
			}
		}
		return result;

	}
	
	private List<Integer> obterCentroCustos() throws ApplicationBusinessException{

		try {
			Object param = buscarParametroPorNome("P_CENTRO_CUSTO_PERINATOLOGIA", VLR_TEXTO);
			if (param != null) {
				String[] centroCustos = param.toString().split(",");
				List<Integer> listaCentroCustos = new ArrayList<Integer>();
				for (String centroCusto : centroCustos) {
					if (StringUtils.isNotBlank(centroCusto)) {
						if (CoreUtil.isNumeroInteger(StringUtils.strip(centroCusto))) {
							listaCentroCustos.add(Integer.valueOf(StringUtils.strip(centroCusto)));
						}
						
					}
				}
				return listaCentroCustos;
			}
		} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException("Parâmetro obrigatório P_CENTRO_CUSTO_PERINATOLOGIA  não existe no banco");
		}
		return null;
		
	}
	
	


}