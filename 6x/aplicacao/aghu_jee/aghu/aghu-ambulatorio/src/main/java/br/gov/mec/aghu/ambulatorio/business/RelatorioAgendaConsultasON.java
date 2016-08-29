package br.gov.mec.aghu.ambulatorio.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
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
import br.gov.mec.aghu.ambulatorio.dao.AacConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacGradeAgendamenConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacRetornosDAO;
import br.gov.mec.aghu.ambulatorio.vo.CabecalhoRelatorioAgendaConsultasVO;
import br.gov.mec.aghu.ambulatorio.vo.ConsultasRelatorioAgendaConsultasVO;
import br.gov.mec.aghu.ambulatorio.vo.EncaminhamentosRelatorioAgendaConsultasVO;
import br.gov.mec.aghu.configuracao.dao.AghUnidadesFuncionaisDAO;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.dominio.DominioTipoDataObito;
import br.gov.mec.aghu.dominio.DominioTurno;
import br.gov.mec.aghu.model.AacRetornos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class RelatorioAgendaConsultasON extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5049621454412280942L;

	private static final Log LOG = LogFactory
			.getLog(RelatorioAgendaConsultasON.class);
	
	public enum RelatorioAgendaConsultasONExceptionCode implements BusinessExceptionCode {
		DATA_INICIAL_MAIOR_QUE_DATA_FINAL, CAMPO_OBRIGATORIO, MSG_OBRIGATORIO_GRADE_ESPEC_SETOR
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private AacConsultasDAO aacConsultasDAO;

	@Inject
	private AghUnidadesFuncionaisDAO unidadeFuncionalDAO;

	@Inject
	private AacGradeAgendamenConsultasDAO gradeAgendamentoConsultasDAO;

	@Inject
	private AacRetornosDAO retornosDAO;

	@EJB
	private AmbulatorioConsultaRN ambulatorioConsultaRN;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;

	private static final String ENCODE = "ISO-8859-1";
	private static final String QUEBRA_LINHA = "\n";
	private static final String SEPARADOR = ";";
	
	private void validarCamposTela(Date dataInicio, Date dataFim, Integer seqGrade, Short seqSetor, Short seqUnidadeFuncional) throws BaseListException {
		getLogger().debug("Iniciando validação de campos da tela.");
		BaseListException listaDeErros = new BaseListException();
		if (dataInicio == null){
			listaDeErros.add(new ApplicationBusinessException(RelatorioAgendaConsultasONExceptionCode.CAMPO_OBRIGATORIO, 
					this.getResourceBundleValue("LABEL_DT_INICIAL_CONSULTAS_GRADE_AGENDAMENTO")));
		}
		if (DateUtil.validaDataMaior(dataInicio, dataFim)){
			listaDeErros.add(new ApplicationBusinessException(RelatorioAgendaConsultasONExceptionCode.DATA_INICIAL_MAIOR_QUE_DATA_FINAL));
		}
		if((seqGrade == null) && (seqSetor == null) && (seqUnidadeFuncional == null)){
			listaDeErros.add(new ApplicationBusinessException(RelatorioAgendaConsultasONExceptionCode.MSG_OBRIGATORIO_GRADE_ESPEC_SETOR));
		}
		if (listaDeErros.hasException()) {
			throw listaDeErros;
		}
		getLogger().debug("Fim validação de campos da tela.");
	}
	
	private String obterNome(Date dataObito, DominioTipoDataObito tipoDataObito, Short seqUnidadeFuncional,
			Integer codigoPaciente, Short seqEspecialidade, Integer seqGrade, String nomePaciente) throws ApplicationBusinessException {
		if (codigoPaciente == null){
			return null;
		}
		if (dataObito == null) {
			if (tipoDataObito == null) {
				if (!pacienteFacade.verEnvioPront(seqUnidadeFuncional, codigoPaciente, seqEspecialidade, seqGrade.shortValue(), null, null, null)) {
					return nomePaciente + " - Não Enviar Pasta";
				} else {
					return nomePaciente;
				}
			} else {
				return nomePaciente + " - OBITO";
			}
		} else {
			return nomePaciente + " - OBITO";
		}
	}

	/**
	 * Consulta os dados necessários ao preenchimento da collection para o
	 * relatorio de Agenda de Consultas
	 * 
	 * @return Lista com dados para relatório de Agenda de Consultas
	 * @throws ApplicationBusinessException 
	 * @throws BaseListException 
	 */
	public List<CabecalhoRelatorioAgendaConsultasVO> carregarRelatorioAgendaConsultas(Date dataInicio, Date dataFim,
		 Integer seqGrade, Short seqSetor, Short seqUnidadeFuncional, DominioTurno turno) throws ApplicationBusinessException, BaseListException{
		
		List<CabecalhoRelatorioAgendaConsultasVO> listaCabecalho = new ArrayList<CabecalhoRelatorioAgendaConsultasVO>();
		List<CabecalhoRelatorioAgendaConsultasVO> listaCabecalhoAux = new ArrayList<CabecalhoRelatorioAgendaConsultasVO>();
		List<ConsultasRelatorioAgendaConsultasVO> listaConsultas = new ArrayList<ConsultasRelatorioAgendaConsultasVO>();
		List<EncaminhamentosRelatorioAgendaConsultasVO> lista = new ArrayList<EncaminhamentosRelatorioAgendaConsultasVO>();
		List<EncaminhamentosRelatorioAgendaConsultasVO> listaEncaminhamentos = new ArrayList<EncaminhamentosRelatorioAgendaConsultasVO>();
		
		Integer turnoCodigo = null;
		getLogger().debug("Iniciando carregar relatório de agenda de consultas.");
		
		this.validarCamposTela(dataInicio, dataFim, seqGrade, seqSetor, seqUnidadeFuncional);
		
		if(turno != null){
			turnoCodigo = turno.getCodigo();
		}
		listaCabecalho = this.getAacConsultasDAO().obterDadosCabecalhoRelatorioAgenda(dataInicio, dataFim, seqGrade, seqSetor, seqUnidadeFuncional, turnoCodigo);
				
		if(!listaCabecalho.isEmpty()){
			for (CabecalhoRelatorioAgendaConsultasVO objetoCabecalho : listaCabecalho) {
				if (listaCabecalhoAux.isEmpty() || !listaCabecalhoAux.contains(objetoCabecalho)) {
					listaEncaminhamentos = new ArrayList<EncaminhamentosRelatorioAgendaConsultasVO>();
					
					AghUnidadesFuncionais unidadeFuncional = unidadeFuncionalDAO.obterUnidadeFuncionalComCaracteristica(objetoCabecalho.getSeqUnidadeFuncional());
					
					objetoCabecalho.setPossuiUbs(unidadeFuncional.possuiCaracteristica(ConstanteAghCaractUnidFuncionais.UBS));
					
					listaConsultas = this.getGradeAgendamentoConsultasDAO().obterConsultasGradeAgendamentoRelatorioAgenda(
									objetoCabecalho.getDataConsulta(),objetoCabecalho.getSeqGrade(),objetoCabecalho.getTurno(),
									objetoCabecalho.getSeqEspecialidade(), seqUnidadeFuncional, objetoCabecalho.getSala());
					for (ConsultasRelatorioAgendaConsultasVO objetoConsulta : listaConsultas) {
							objetoConsulta.setNomePaciente(obterNomePaciente(objetoConsulta));
							objetoConsulta.setPossuiUbs(objetoCabecalho.isPossuiUbs());
							if (objetoConsulta.getCodigoPaciente() != null) {
								lista = this.getGradeAgendamentoConsultasDAO().obterEncaminhamentosRelatorioAgenda(
										objetoConsulta.getSeqGrade(),objetoConsulta.getCodigoPaciente(),objetoCabecalho.getDataConsulta());
								for (EncaminhamentosRelatorioAgendaConsultasVO objetoEncaminhamento : lista){
									//Filtro por OBTER_ENVIO_PRONT = S
									if(pacienteFacade.verEnvioPront(objetoEncaminhamento.getSeqUnidadeFuncional(), objetoEncaminhamento.getCodigoPaciente(), 
											objetoEncaminhamento.getSeqEspecialidade(), objetoEncaminhamento.getSeqGrade().shortValue(), null, null, null)){
										listaEncaminhamentos.add(objetoEncaminhamento);
									}
								}
							}
					}
					objetoCabecalho.setInfConsultas(listaConsultas);
					objetoCabecalho.setInfEncaminhamentos(listaEncaminhamentos);
					listaCabecalhoAux.add(objetoCabecalho);
				}
			}
		}
		getLogger().debug("Fim carregar relatório de agenda de consultas.");
		return listaCabecalhoAux;
	}
	/**
	 * Obter nome do paciente
	 * @param objetoConsulta
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private String obterNomePaciente(ConsultasRelatorioAgendaConsultasVO objetoConsulta) throws ApplicationBusinessException {
		String nomePaciente = "";
		if (StringUtils.isNotEmpty(objetoConsulta.getNomePaciente())) {
			nomePaciente = this.obterNome(objetoConsulta.getPacienteDataObito(), objetoConsulta.getPacienteTipoDataObito(), objetoConsulta.getSeqUnidadeFuncional(), objetoConsulta.getCodigoPaciente(), objetoConsulta.getSeqEspecialidade(), objetoConsulta.getSeqGrade(), objetoConsulta.getNomePaciente());
			if (StringUtils.isNotEmpty(nomePaciente)) {
				return nomePaciente.toUpperCase();
			}
		}
		return nomePaciente;
	}

	/**
	 * Método que monta e gera o arquivo CSV do relatório
	 * @param listaLinhas Lista com dados do relatório
	 * @return Caminho absoluto do arquivo gerado
	 * @throws IOException
	 * @throws ApplicationBusinessException
	 */
	public String gerarCSVAgendaConsultas(List<CabecalhoRelatorioAgendaConsultasVO> listaLinhas) throws IOException, ApplicationBusinessException {

		final File file = File.createTempFile(DominioNomeRelatorio.AACR_AGENDA_DT_REF_TITLE.toString(),DominioNomeRelatorio.EXTENSAO_CSV);

		final Writer out = new OutputStreamWriter(new FileOutputStream(file),ENCODE);

		out.write(this.gerarCabecalhoDoRelatorio());
		
		if(!listaLinhas.isEmpty()){
			out.write(this.gerarLinhasDoRelatorio(listaLinhas));
			out.write(this.gerarSecaoFinalDoRelatorio(retornosDAO.obterTodosRetornosRelatorioAgenda(0)));
		}
		
		out.flush();
		out.close();

		return file.getAbsolutePath();
	}

	/**
	 * Método que gera os nomes das colunas do arquivo CSV do relatório
	 * @return String de uma linha com os nomes das colunas
	 */
	private String gerarCabecalhoDoRelatorio() {
		StringBuilder builder = new StringBuilder();
		builder.append(
				getResourceBundleValue("TITLE_CSV_SETOR_C1"))
				.append(SEPARADOR)
				.append(getResourceBundleValue("TITLE_CSV_DATA_CONSULTA"))
				.append(SEPARADOR)
				.append(getResourceBundleValue("TITLE_CSV_DIA"))
				.append(SEPARADOR)
				.append(getResourceBundleValue("TITLE_CSV_TURNO_C1"))
				.append(SEPARADOR)
				.append(getResourceBundleValue("TITLE_CSV_SALA_C1"))
				.append(SEPARADOR)
				.append(getResourceBundleValue("TITLE_CSV_GRADE_C1"))
				.append(SEPARADOR)
				.append(getResourceBundleValue("TITLE_CSV_ESPECIALIDADE"))
				.append(SEPARADOR)
				.append(getResourceBundleValue("TITLE_CSV_EQUIPE"))
				.append(SEPARADOR)
				.append(getResourceBundleValue("TITLE_CSV_NOME_C1"))
				.append(SEPARADOR)
				.append(getResourceBundleValue("TITLE_CSV_PAC_C2"))
				.append(SEPARADOR)
				.append(getResourceBundleValue("TITLE_CSV_CAA_DESC"))
				.append(SEPARADOR)
				.append(getResourceBundleValue("TITLE_CSV_GRADE_C2"))
				.append(SEPARADOR)
				.append(getResourceBundleValue("TITLE_CSV_NUMERO"))
				.append(SEPARADOR)
				.append(getResourceBundleValue("TITLE_CSV_PRONTUARIO_C2"))
				.append(SEPARADOR)
				.append(getResourceBundleValue("TITLE_CSV_NOME_C2"))
				.append(SEPARADOR)
				.append(getResourceBundleValue("TITLE_CSV_HORA_CONSULTA_C2"))
				.append(SEPARADOR)
				.append(getResourceBundleValue("TITLE_CSV_RET_SEQ"))
				.append(SEPARADOR)
				.append(getResourceBundleValue("TITLE_CSV_PAC_CODIGO"))
				.append(SEPARADOR)
				.append(getResourceBundleValue("TITLE_CSV_PRONTUARIO_C3"))
				.append(SEPARADOR)
				.append(getResourceBundleValue("TITLE_CSV_GRADE_C3"))
				.append(SEPARADOR)
				.append(getResourceBundleValue("TITLE_CSV_SETOR_C3"))
				.append(SEPARADOR)
				.append(getResourceBundleValue("TITLE_CSV_SALA_C3"))
				.append(SEPARADOR)
				.append(getResourceBundleValue("TITLE_CSV_HORA_CONSULTA_C3"))
				.append(SEPARADOR)
				.append(getResourceBundleValue("TITLE_CSV_TURNO_C3"))
				.append(SEPARADOR)
				.append(getResourceBundleValue("TITLE_CSV_PAC_C3"))
				.append(SEPARADOR).append(QUEBRA_LINHA);

		return builder.toString().toUpperCase();
	}
	
	private String montarCabecalhoVO(CabecalhoRelatorioAgendaConsultasVO linha){
		StringBuilder resultado = new StringBuilder();
		resultado.append(checarVazio(linha.getDescricaoSetor()))
			.append(SEPARADOR)
			.append(DateUtil.dataToString(linha.getDataConsulta(), "dd/MM/yyyy"))
			.append(SEPARADOR)
			.append(checarVazio(linha.getDia()))
			.append(SEPARADOR)
			.append(checarVazio(linha.getTurno() + 1))
			.append(SEPARADOR)
			.append(checarVazio(linha.getSala()))
			.append(SEPARADOR)
			.append(checarVazio(linha.getSeqGrade()))
			.append(SEPARADOR)
			.append(checarVazio(linha.getNomeEspecialidade()))
			.append(SEPARADOR)
			.append(checarVazio(linha.getNomeEquipe()))
			.append(SEPARADOR)
			.append(checarVazio(linha.getNomeMedico()))
			.append(SEPARADOR);
		return resultado.toString().toUpperCase();
	}
	
	private String montarConsultaVO(ConsultasRelatorioAgendaConsultasVO objetoConsulta, Integer grade){
		StringBuilder resultado = new StringBuilder();
		resultado.append(checarVazio(objetoConsulta.getPacConfirmado()))
			.append(SEPARADOR)
			.append(checarVazio(objetoConsulta.getCondicaoAtendimentoDescricao()))
			.append(SEPARADOR)
			.append(checarVazio(grade))
			.append(SEPARADOR)
			.append(checarVazio(objetoConsulta.getNumeroConsulta()))
			.append(SEPARADOR)
			.append(checarVazio(objetoConsulta.getProntuario()))
			.append(SEPARADOR)
			.append(checarVazio(objetoConsulta.getNomePaciente()))
			.append(SEPARADOR)
			.append(checarVazio(objetoConsulta.getHoraConsulta()))
			.append(SEPARADOR)
			.append(checarVazio(objetoConsulta.getSeqRetorno()))
			.append(SEPARADOR)
			.append(checarVazio(objetoConsulta.getCodigoPaciente()))
			.append(SEPARADOR);
		return resultado.toString().toUpperCase();
	}
	
	private String montarEncaminhamentoVO(EncaminhamentosRelatorioAgendaConsultasVO objetoEncaminhamento, Integer grade){
		StringBuilder resultado = new StringBuilder();
		resultado.append(checarVazio(objetoEncaminhamento.getProntuario()))
			.append(SEPARADOR)
			.append(checarVazio(grade))
			.append(SEPARADOR)
			.append(checarVazio(objetoEncaminhamento.getDescricaoSetor()))
			.append(SEPARADOR)
			.append(checarVazio(objetoEncaminhamento.getSala()))
			.append(SEPARADOR)
			.append(checarVazio(objetoEncaminhamento.getHoraConsulta()))
			.append(SEPARADOR)
			.append(checarVazio(objetoEncaminhamento.getTurno() + 1))
			.append(SEPARADOR)
			.append(checarVazio(objetoEncaminhamento.getCodigoPaciente()))
			.append(SEPARADOR);
		return resultado.toString().toUpperCase();
	}

	/**
	 * Método que preenche o arquivo CSV 
	 * @param listaLinha Lista com os dados do relatório
	 * @return String que representa os dados do relatório formatados para o CSV
	 */
	private String gerarLinhasDoRelatorio(List<CabecalhoRelatorioAgendaConsultasVO> listaCabecalho) {
		StringBuilder builder = new StringBuilder();
		for (CabecalhoRelatorioAgendaConsultasVO cabecalho : listaCabecalho) {
			if (!cabecalho.getInfConsultas().isEmpty()) {
				for (ConsultasRelatorioAgendaConsultasVO objetoConsulta : cabecalho.getInfConsultas()) {
					if (!cabecalho.getInfEncaminhamentos().isEmpty()) {
						boolean achouPaciente = false;
						for (EncaminhamentosRelatorioAgendaConsultasVO objetoEncaminhamento : cabecalho.getInfEncaminhamentos()) {
							if (objetoConsulta.getProntuario() != null && objetoConsulta.getProntuario().equals(objetoEncaminhamento.getProntuario())) {
								// cabeçalhoVO
								builder.append(montarCabecalhoVO(cabecalho));
								
								// consultaVO
								builder.append(montarConsultaVO(objetoConsulta, cabecalho.getSeqGrade()));
								
								// encaminhamentoVO
								builder.append(montarEncaminhamentoVO(objetoEncaminhamento, cabecalho.getSeqGrade()));
								builder.append(QUEBRA_LINHA);
								achouPaciente = true;
							}
						}
						if (!achouPaciente) {
							montarLinhaConsulta(builder, cabecalho, objetoConsulta);
						}
					} else {//Se não há encaminhamentos 
						montarLinhaConsulta(builder, cabecalho, objetoConsulta);
					}
				} 
			} else{//se não há consultas
				// cabeçalhoVO
				builder.append(montarCabecalhoVO(cabecalho));
				builder.append(QUEBRA_LINHA);
			}
		}
		return builder.toString().toUpperCase();
	}

	private void montarLinhaConsulta(StringBuilder builder,
			CabecalhoRelatorioAgendaConsultasVO cabecalho,
			ConsultasRelatorioAgendaConsultasVO objetoConsulta) {
		// cabeçalhoVO
		builder.append(montarCabecalhoVO(cabecalho));
		
		// consultaVO
		builder.append(montarConsultaVO(objetoConsulta, cabecalho.getSeqGrade()));
		builder.append(QUEBRA_LINHA);
	}
	
	
	private String checarVazio(Object valor) {
		String resultado = StringUtils.EMPTY;
		if (valor != null) {
			resultado = String.valueOf(valor);
		}
		return resultado;
	}
	
	/**
	 * Método que gera a seção final do arquivo CSV do relatório onde são apresentados os 
	 * dados de Retornos e nome do hospital
	 * @param listaLinha Lista com os dados do relatório
	 * @return String que apresenta a seção final do relatório formatados para o CSV
	 * @throws ApplicationBusinessException 
	 */
	public String gerarSecaoFinalDoRelatorio(List<AacRetornos> listaLinha) throws ApplicationBusinessException{
		StringBuilder builder = new StringBuilder();
		int qtdRetornos = 0;
		if (listaLinha != null && !listaLinha.isEmpty()) {
			qtdRetornos = listaLinha.size();
			// Montando o cabeçalho da seção de retornos do relatório.
			builder.append(getResourceBundleValue("TITLE_CSV_SEQ"))
			.append(SEPARADOR)
			.append(getResourceBundleValue("TITLE_CSV_DESCRICAO"))
			.append(SEPARADOR)
			.append(QUEBRA_LINHA);

			// Escrevendo as linhas do setor de retornos do relatório
			for (AacRetornos objetoRetorno : listaLinha) {
				builder.append(objetoRetorno.getSeq())
				.append(SEPARADOR)
				.append(objetoRetorno.getDescricao())
				.append(SEPARADOR)
				.append(QUEBRA_LINHA);
			}
		}
		//Montando o cabeçalho da seção final do relatório
		//nome do hospital
		builder.append(getResourceBundleValue("TITLE_CSV_HOSPITAL"))
		.append(SEPARADOR)
		//quantidade de retornos
		.append(getResourceBundleValue("TITLE_CSV_QTD_RETORNOS"))
		.append(SEPARADOR)
		.append(QUEBRA_LINHA);
		
		//Escrevendo as linhas da seção final do relatório
		AghParametros parametroRazaoSocial = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL);
		builder.append(parametroRazaoSocial.getVlrTexto())
		.append(SEPARADOR)
		.append(qtdRetornos)
		.append(SEPARADOR)
		.append(QUEBRA_LINHA);
		return builder.toString().toUpperCase();
	}

	//Getters and Setters
	public AacConsultasDAO getAacConsultasDAO() {
		return aacConsultasDAO;
	}

	public void setAacConsultasDAO(AacConsultasDAO aacConsultasDAO) {
		this.aacConsultasDAO = aacConsultasDAO;
	}

	public AmbulatorioConsultaRN getAmbulatorioConsultaRN() {
		return ambulatorioConsultaRN;
	}

	public void setAmbulatorioConsultaRN(
			AmbulatorioConsultaRN ambulatorioConsultaRN) {
		this.ambulatorioConsultaRN = ambulatorioConsultaRN;
	}

	public AacGradeAgendamenConsultasDAO getGradeAgendamentoConsultasDAO() {
		return gradeAgendamentoConsultasDAO;
	}

	public void setGradeAgendamentoConsultasDAO(
			AacGradeAgendamenConsultasDAO gradeAgendamentoConsultasDAO) {
		this.gradeAgendamentoConsultasDAO = gradeAgendamentoConsultasDAO;
	}

	public AacRetornosDAO getRetornosDAO() {
		return retornosDAO;
	}

	public void setRetornosDAO(AacRetornosDAO retornosDAO) {
		this.retornosDAO = retornosDAO;
	}

}
