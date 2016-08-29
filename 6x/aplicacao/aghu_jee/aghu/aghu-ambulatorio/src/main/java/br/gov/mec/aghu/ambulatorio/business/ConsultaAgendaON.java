package br.gov.mec.aghu.ambulatorio.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.Writer;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.vo.RelatorioConsultasAgendaVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.dominio.DominioCor;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.AacGradeProcedHospitalar;
import br.gov.mec.aghu.model.AacRetornos;
import br.gov.mec.aghu.model.AipCidades;
import br.gov.mec.aghu.model.AipEnderecosPacientes;
import br.gov.mec.aghu.model.AipNacionalidades;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.RapPessoaTipoInformacoes;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapQualificacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.cadastro.business.ICadastroPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;

@Stateless
@SuppressWarnings({"PMD.UseStringBufferForStringAppends", "PMD.AddEmptyString", "PMD.ExcessiveClassLength"})
public class ConsultaAgendaON extends BaseBusiness implements Serializable {
	
	private static final Log LOG = LogFactory.getLog(ConsultaAgendaON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private ICadastroPacienteFacade cadastroPacienteFacade;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	private static final long serialVersionUID = 5932661676766248094L;
	private static final String SEPARADOR=";";
	private static final String QUALIFICADOR="\"";
	private static final String ENCODE="ISO-8859-1";
	private static final String PREFIXO="AGDA_";	
	private static final String EXTENSAO=".csv";
	private static final String PREFIXO_RASS="AA_";	
	private static final String EXTENSAO_RASS=".csv";
	private static final String SEPARADOR_DE_LINHA="line.separator";
	private static final String ESPACO_EM_BRANCO_2="  ";
	private static final String ESPACO_EM_BRANCO_3="   ";
	private static final String ESPACO_EM_BRANCO_4="    ";
	private static final String ESPACO_EM_BRANCO_5="     ";
	private static final String ESPACO_EM_BRANCO_6="      ";
	private static final String ESPACO_EM_BRANCO_7="       ";
	private static final String ESPACO_EM_BRANCO_8="        ";
	private static final String ESPACO_EM_BRANCO_10="          ";
	private static final String ESPACO_EM_BRANCO_11="           ";
	private static final String ESPACO_EM_BRANCO_13="             ";
	private static final String ESPACO_EM_BRANCO_15="               ";
	private static final String ESPACO_EM_BRANCO_30="                              ";
	private static final String ESPACO_EM_BRANCO_40="                                        ";
	private static final String CABECALHO_NOME_ORGAO="H. DE CLÍNICAS DE PORTO ALEGRE";
	private static final String CABECALHO_SIGLA_ORGAO="HCPA  ";
	private static final String CABECALHO_CGC_ORGAO="87020517000120";
	private static final String CNES_INFANTIL="7364865";
	private static final String CNES_ADULTO="7364881";
	
	
	public enum ConsultaAgendaONExceptionCode implements BusinessExceptionCode {

		GERACAO_RAAS_RETORNO_DIFERENTE_10, GERACAO_RAAS_UNIDADE_DIFERE_CAPS;

		public void throwException(Object... params)
		throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}

	}
	
	
	public File geraArquivoConsulta(List<AacConsultas> consultas) throws IOException, ApplicationBusinessException{
		File file = File.createTempFile(PREFIXO, EXTENSAO);
		
		Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);
		out.write(geraCabecalho());
		List<Integer> numero = new ArrayList<Integer>(consultas.size());
		for (AacConsultas consulta:consultas){
			if(!numero.contains(consulta.getNumero())){
				numero.add(consulta.getNumero());
				
				out.write(System.getProperty(SEPARADOR_DE_LINHA));
				out.write(geraLinhaConsulta(consulta));
			}
		}
		out.flush();
		out.close();
		
		return file;
	}
	
	
	public File geraArquivoRass(List<AacConsultas> consultas, Date dtInicio,
			Date dtFim, Short filtroUslUnfSeq, AacRetornos filtroRetorno) throws IOException,
			ApplicationBusinessException {
		File file = File.createTempFile(PREFIXO_RASS, EXTENSAO_RASS);
		
		Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);
		out.write(gerarCabecalhoRass(new StringBuffer(100)));
		out.write(System.getProperty(SEPARADOR_DE_LINHA));
		
		verificarUnidadeCapsRetornoAtendido(filtroUslUnfSeq, filtroRetorno);
		List<Integer> numero = new ArrayList<Integer>(consultas.size());
		Integer codigoPacienteAnterior = null;
		for (AacConsultas consulta: consultas){
			if(!numero.contains(consulta.getNumero())){
				numero.add(consulta.getNumero());
				//Gera dados do corpo (15)
				if (!consulta.getPaciente().getCodigo().equals(codigoPacienteAnterior)){
					out.write(gerarDadosPacientePsicosocial(consulta, dtInicio, dtFim));		
					out.write(System.getProperty(SEPARADOR_DE_LINHA));
				}
				//Gera dados do procedimento (16)
				out.write(gerarDadosProcedimentoPaciente(consulta, dtInicio, dtFim));
				codigoPacienteAnterior = consulta.getPaciente().getCodigo();
				out.write(System.getProperty(SEPARADOR_DE_LINHA));	
			}
		}
		out.flush();
		out.close();
		
		return file;
	}
	
	/**
	 * Método que verifica se a unidade informada foi o CAPS e se o retorno foi 10
	 * @param filtroUslUnfSeq
	 * @throws ApplicationBusinessException
	 */
	public void verificarUnidadeCapsRetornoAtendido(Short filtroUslUnfSeq, AacRetornos filtroRetorno) throws ApplicationBusinessException{
		BigDecimal unfSeqCaps = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_UNIDADE_CAPS).getVlrNumerico();
		if (filtroUslUnfSeq == null || !filtroUslUnfSeq.equals(unfSeqCaps.shortValue())){
			throw new ApplicationBusinessException(ConsultaAgendaONExceptionCode.GERACAO_RAAS_UNIDADE_DIFERE_CAPS);
		}
		if (filtroRetorno == null){
			throw new ApplicationBusinessException(ConsultaAgendaONExceptionCode.GERACAO_RAAS_RETORNO_DIFERENTE_10);
		}
	}
	
	
	/**
	 * Gera os dados do procedimento do paciente (consulta)
	 * @param consulta
	 * @param dtInicio
	 * @param dtFim
	 * @param mesMovimentoProducaoRas
	 * @return
	 */
	private String gerarDadosProcedimentoPaciente(AacConsultas consulta, Date dtInicio, Date dtFim) throws ApplicationBusinessException{
		StringBuffer sb = new StringBuffer(100);
		//01 - Codlinha
		sb.append("16").
		//02 - Código da Unidade de Federação
		append("RS").
		//03 - Ano e mês da Produção no formato AAAAMM
		append(obterDataRass(new Date(), false));
		//04 - Código da Unidade Prestadora de Serviço (CNES)
		if (consulta.getPaciente().getIdade() < 18){
			sb.append(CNES_INFANTIL);
		}
		else{
			sb.append(CNES_ADULTO);			
		}
		//05 - Cartão Nacional de Saúde do Paciente
		sb.append(obterCartaoSUSPaciente(consulta.getPaciente()));
		//06 - Data Inicial de Validade
		if (dtInicio != null){
			sb.append(obterDataRass(dtInicio, true));			
		}
		else{
			sb.append(ESPACO_EM_BRANCO_8);
		}
		//07 - Procedimento
		sb.append(obterCodigoProcedimento(consulta))
		//08 - Código CBO do Executante da Ação
		.append(obterCBOExecutante(consulta))
		//09 - Número do CNS do Executante
		.append(obterCNSExecutante(consulta))
		//10 - Data da execução da ação
		.append(obterDataRass(consulta.getDtConsulta(), true))
		//11 - Código do Serviço (3) - Fixo 115, 12 - Código de Classificação (3) - Fixo 002, 13 - Quantidade Realizada (6), 14 - Origem das Informações (3) , 15 - Local de Realização do Exame (1)
		.append("115002000001RASC")
		//16 - Reservado
		.append(ESPACO_EM_BRANCO_4);
		
		return sb.toString();
	}
	
	private String obterCNSExecutante(AacConsultas consulta) throws ApplicationBusinessException{
		String strCns = ESPACO_EM_BRANCO_15;
		RapPessoaTipoInformacoes tipoInformacaoCns = null;
		BigDecimal tipoCns = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TIPO_INF_CNS).getVlrNumerico();
		RapServidores profissionalResponsavel = consulta.getGradeAgendamenConsulta().getEquipe().getProfissionalResponsavel();
		RapPessoasFisicas pessoaFisica = registroColaboradorFacade.obterRapPessoasFisicasPorServidor(profissionalResponsavel.getId());
		tipoInformacaoCns = registroColaboradorFacade.obterTipoInformacao(pessoaFisica.getCodigo(), tipoCns.shortValue());
		if (tipoInformacaoCns != null){
			strCns = tipoInformacaoCns.getValor();
			while (strCns.length() < 15){
				strCns+= " ";
			}
		}
		return strCns;			
	}
	

	private String obterCBOExecutante(AacConsultas consulta) throws ApplicationBusinessException{
		String strCbo = ESPACO_EM_BRANCO_6;
		RapPessoaTipoInformacoes tipoInformacaoCBO = null;
		BigDecimal cboPrincipal = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TIPO_INF_CBO).getVlrNumerico();
		RapServidores profissionalResponsavel = consulta.getGradeAgendamenConsulta().getEquipe().getProfissionalResponsavel();
		RapPessoasFisicas pessoaFisica = registroColaboradorFacade.obterRapPessoasFisicasPorServidor(profissionalResponsavel.getId());
		tipoInformacaoCBO = registroColaboradorFacade.obterTipoInformacao(pessoaFisica.getCodigo(), cboPrincipal.shortValue());
		if (tipoInformacaoCBO != null){
			strCbo = tipoInformacaoCBO.getValor();
			while (strCbo.length() < 6){
				strCbo+= " ";
			}			
		}
		return strCbo;
	}
	
	private String obterCodigoProcedimento(AacConsultas consulta) throws ApplicationBusinessException{
		String strCodTabela = "          ";
		Integer phiSeq = ambulatorioFacade.obterPhiSeqPorNumeroConsulta(consulta.getNumero(), null);
		BigDecimal convenioSus = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_CONVENIO_SUS_PADRAO).getVlrNumerico();
		BigDecimal planoSusAmbulatorio = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SUS_PLANO_AMBULATORIO).getVlrNumerico();
		BigDecimal tabelaFaturamentoUnificada = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TABELA_FATUR_PADRAO).getVlrNumerico();
		if (phiSeq != null){
			Long codigoTabela = faturamentoFacade.obterCodTabelaPorPhi(phiSeq, convenioSus.shortValue(), planoSusAmbulatorio.byteValue(), tabelaFaturamentoUnificada.shortValue());
			if (codigoTabela != null){
				strCodTabela = Long.toString(codigoTabela);
				while (strCodTabela.length() < 10){
					strCodTabela = "0" + strCodTabela;
				}
			}			
		}
		return strCodTabela;
	}


	/**
	 * Gera os dados dos pacientes no arquivo RASS
	 * @param consultas
	 * @return
	 */
	private String gerarDadosPacientePsicosocial(AacConsultas consulta, Date dtInicio, Date dtFim){
		StringBuffer sb = new StringBuffer();
		AipEnderecosPacientes endereco = cadastroPacienteFacade.obterEnderecoResidencialPadraoPaciente(consulta.getPaciente());
		
		gerarCorpoPsicosocial1(sb, consulta, dtInicio, dtFim);
		gerarCorpoPsicosocial2(sb, consulta);
		gerarCorpoPsicosocial3(sb, consulta, endereco);
		gerarCorpoPsicosocial4(sb, consulta, endereco);
		
		
		
		return sb.toString();
	}
	
	private void gerarCorpoPsicosocial1(StringBuffer sb, AacConsultas consulta, Date dtInicio, Date dtFim){
		//01 - Codlinha
		sb.append("15")
		//02 - Código da Unidade de Federação
		.append("RS")
		//03 - Ano e mês da Produção no formato AAAAMM
		.append(obterDataRass(new Date(), false));
		//04 - Código da Unidade Prestadora de Serviço
		if (consulta.getPaciente().getIdade() < 18){
			sb.append(CNES_INFANTIL);
		}
		else{
			sb.append(CNES_ADULTO);			
		}
		//05 - Cartão Nacional de Saúde do Paciente
		sb.append(obterCartaoSUSPaciente(consulta.getPaciente()));
		//06 - Data Inicial de Validade
		if (dtInicio != null){
			sb.append(obterDataRass(dtInicio, true));			
		}
		else{
			sb.append(ESPACO_EM_BRANCO_8);
		}
		//07 - Data Final de Validade
		sb.append(ESPACO_EM_BRANCO_8);
	}
	
	private void gerarCorpoPsicosocial2(StringBuffer sb, AacConsultas consulta){
		//08 - Nome do Paciente
		String nome = consulta.getPaciente().getNome();
		if (nome.length() > 30){
			nome = nome.substring(0, 30);
		}
		else if (nome.length() < 30){
			while (nome.length() < 30){
				nome+=" ";
			}
		}
		sb.append(nome);
		//09 - Prontuário do Paciente
		Integer prontuario = null;
		prontuario = consulta.getPaciente().getProntuario();
		String strProntuario = "          ";
		if (prontuario != null){
			strProntuario = Integer.toString(prontuario);
			if (strProntuario.length() < 10){
				while (strProntuario.length() < 10){
					strProntuario = "0" + strProntuario;
				}
			}
		}
		sb.append(strProntuario);
		//10 - Nome da mãe do paciente
		String nomeMae = consulta.getPaciente().getNomeMae();
		if (nomeMae.length() > 30){
			nomeMae = nomeMae.substring(0, 30);
		}
		else if (nomeMae.length() < 30){
			while (nomeMae.length() < 30){
				nomeMae+=" ";
			}
		}
		sb.append(nomeMae);
	}

	private void gerarCorpoPsicosocial3(StringBuffer sb, AacConsultas consulta, AipEnderecosPacientes endereco){
		//11 - Logradouro de residência do Paciente
		sb.append(obterEnderecoResidencialPaciente(endereco))
		//12 - Número da residência do paciente
		.append(obterNumeroLogradouroEndereco(endereco))
		//13 - Complemento do logradouro do paciente
		.append(obterComplementoLogradouroEndereco(endereco))
		//14 - CEP do logradouro
		.append(obterCEPLogradouro(endereco))
		//15 - Código IBGE do Município do endereço do paciente
		.append(obterCodigoIBGEMunicipio(endereco))
		//16 - Data de nascimento do paciente
		.append(obterDataRass(consulta.getPaciente().getDtNascimento(), true))
		//17 - Sexo do Paciente
		.append(consulta.getPaciente().getSexo());
		//18 - Raça do Paciente
		String strCor = ESPACO_EM_BRANCO_2;
		DominioCor cor = consulta.getPaciente().getCor();
		if (cor != null){
			strCor = "" + cor.getCodigo();
			if (strCor.length() < 2){
				strCor = "0" + strCor;
			}
		}
		sb.append(strCor)
		//19 - Nome do Responsável
		.append(obterResponsavelPaciente(consulta.getPaciente()));
		//20 - Nacionalidade
		String strNacionalidade = ESPACO_EM_BRANCO_3;
		AipNacionalidades nacionalidade = consulta.getPaciente().getAipNacionalidades();
		if (nacionalidade != null){
			strNacionalidade = "" + nacionalidade.getCodigo();
		}
		while (strNacionalidade.length() < 3){
			strNacionalidade = "0" + strNacionalidade;
		}
		sb.append(strNacionalidade)
		//21 - Etnia
		.append(ESPACO_EM_BRANCO_4)
		//22 - Telefone Residencial
		.append(obterTelefoneResidencial(consulta.getPaciente()))
		//23 - Telefone Celular
		.append(obterTelefoneCelular(consulta.getPaciente()));
	}
	
	private void gerarCorpoPsicosocial4(StringBuffer sb, AacConsultas consulta, AipEnderecosPacientes endereco){
		//24 - Código do Motivo de Saída/Permanência. TODO: Camila ficou de verificar
		sb.append("28")
		//25 - Data da ocorrência no caso de alta, transferência ou óbito
		.append(ESPACO_EM_BRANCO_8)
		//26 - CID Principal.  (por enquanto fica vazio)
		.append(ESPACO_EM_BRANCO_4)
		//27 - CID Secundário 1
		.append(ESPACO_EM_BRANCO_4)
		//28 - CID Secundário 2
		.append(ESPACO_EM_BRANCO_4)
		//29 - CID Secundário 3
		.append(ESPACO_EM_BRANCO_4)
		//30 - CID Causas Associadas
		.append(ESPACO_EM_BRANCO_4)
		//31 - Carater do atendimento (deixar em branco)
		.append(ESPACO_EM_BRANCO_2)
		//32 - Código da Origem do Paciente
		.append("02")
		//33 - Cobertura de Estratégia Saúde da Família
		.append('N')
		//34 - Codigo do Estabelecimento de Cobertura (deixar em branco)
		.append(ESPACO_EM_BRANCO_7)
		//35 - Total de ações (procedimentos) realizados na folha
		.append(ESPACO_EM_BRANCO_5)
		//36 - Código de destino do paciente; 37 - Origem das Informações; 38 - Usuário encontra-se em situação de rua; 39 - É usuário de drogas
		.append("00RASNN")
		//40 - Tipos de drogas
		.append(ESPACO_EM_BRANCO_3)
		//41 - Número da autorização para realização do procedimento
		.append(ESPACO_EM_BRANCO_13)
		//42 - Bairro do Paciente
		.append(obterBairroPaciente(endereco))
		//43 - Código do Logradouro (deixar fixo rua - 081)
		.append("081")
		//44 - e-mail do paciente (deixar em branco)
		.append(ESPACO_EM_BRANCO_40)
		//45 - reservado
		.append(ESPACO_EM_BRANCO_4);
	}
	
	
	private String obterCartaoSUSPaciente(AipPacientes paciente){
		String strCartaoSus = ESPACO_EM_BRANCO_15;
		if (paciente.getNroCartaoSaude() != null){
			strCartaoSus = paciente.getNroCartaoSaude().toString();
		}
		return strCartaoSus;
	}
	
	private String obterDataRass(Date data, boolean diaIncluso){
		String strData = ESPACO_EM_BRANCO_6;
		if (data != null){
			GregorianCalendar calendar = new GregorianCalendar();  
			calendar.setTime(data);  
			int dia = calendar.get(Calendar.DAY_OF_MONTH);
			int mes = calendar.get(Calendar.MONTH) + 1;  
			int ano = calendar.get(Calendar.YEAR);  
			String strMes = Integer.toString(mes);
			if (strMes.length() < 2){
				strMes = "0" + strMes;
			}
			String strDia = Integer.toString(dia);
			if (strDia.length() < 2){
				strDia = "0" + strDia;
			}
			if (diaIncluso){
				strData = "" + ano + "" + strMes + "" + strDia;
			}
			else{
				strData = "" + ano + "" + strMes;
			}
		}
		else{
			if (diaIncluso){
				strData = ESPACO_EM_BRANCO_8;
			}
		}
		return strData;
	}
	
	private String obterBairroPaciente(AipEnderecosPacientes endereco){
		String bairro = ESPACO_EM_BRANCO_30;
		
		if (endereco != null){
			if (endereco.getAipBairrosCepLogradouro() != null){
				bairro = endereco.getAipBairrosCepLogradouro().getAipBairro().getDescricao();
			}
			else if (endereco.getBairro() != null){
				bairro = endereco.getBairro();
			}
			if (bairro != null){
				if (bairro.length() > 30){
					bairro = bairro.substring(0, 30);
				}
				else if (bairro.length() < 30){
					while (bairro.length() < 30){
						bairro += " ";
					}
				}			
			}			
		}
		return bairro;
	}
	
	private String obterTelefoneCelular(AipPacientes paciente){
		String strFoneCelular = ESPACO_EM_BRANCO_11;
		String foneCelular = paciente.getFoneRecado();
		if (foneCelular != null){
			strFoneCelular = "" + foneCelular;
		}
		if (strFoneCelular.length() > 11){
			strFoneCelular = ESPACO_EM_BRANCO_11;
		}
		else if (strFoneCelular.length() < 11){
			while (strFoneCelular.length() < 11){
				strFoneCelular = "0" + strFoneCelular;
			}
		}
		
		return strFoneCelular;
	}
	
	private String obterTelefoneResidencial(AipPacientes paciente){
		String strFoneResidencial = ESPACO_EM_BRANCO_11;
		Long foneResidencial = paciente.getFoneResidencial();
		if (foneResidencial != null){
			strFoneResidencial = "" + foneResidencial;
		}
		if (strFoneResidencial.length() > 11){
			strFoneResidencial = ESPACO_EM_BRANCO_11;
		}
		else if (strFoneResidencial.length() < 11){
			while (strFoneResidencial.length() < 11){
				strFoneResidencial = "0" + strFoneResidencial;
			}
		}
		
		return strFoneResidencial;
	}
	
	private String obterResponsavelPaciente(AipPacientes paciente){
		String nomeResponsavel = paciente.getNome();
		if (paciente.getIdade() < 18){
			nomeResponsavel = paciente.getNomeMae();
		}
		if (nomeResponsavel.length() > 30){
			nomeResponsavel = nomeResponsavel.substring(0, 30);
		}
		else if (nomeResponsavel.length() < 30){
			while (nomeResponsavel.length() < 30){
				nomeResponsavel+= " ";
			}
		}
		return nomeResponsavel;
	}
	
	private String obterCodigoIBGEMunicipio(AipEnderecosPacientes endereco){
		String strCodigoIbge = ESPACO_EM_BRANCO_7;
		if (endereco != null){
			if (endereco.getAipBairrosCepLogradouro() != null){
				Integer codigoIbge = endereco.getAipBairrosCepLogradouro().getAipLogradouro().getAipCidade().getCodIbge();
				if (codigoIbge != null){
					strCodigoIbge = Integer.toString(codigoIbge);		
				}
			}
			else{
				AipCidades cidade = endereco.getAipCidade();
				if (cidade != null && cidade.getCodIbge() != null){
					strCodigoIbge = Integer.toString(cidade.getCodIbge());
				}
			}
			if (strCodigoIbge.length() < 7){
				while (strCodigoIbge.length() < 7){
					strCodigoIbge+= " ";
				}
			}			
		}
		return strCodigoIbge;
	}
	
	private String obterCEPLogradouro(AipEnderecosPacientes endereco){
		String cepEndereco = ESPACO_EM_BRANCO_8;
		if (endereco != null){
			if (endereco.getAipBairrosCepLogradouro() != null){
				Integer cep = endereco.getAipBairrosCepLogradouro().getCepLogradouro().getId().getCep();
				if (cep != null){
					cepEndereco = Integer.toString(cep);		
				}
			}
			else{
				Integer cep = endereco.getCep();
				if (cep != null){
					cepEndereco = Integer.toString(cep);		
				}
			}			
		}
		return cepEndereco;
	}
	
	private String obterComplementoLogradouroEndereco(AipEnderecosPacientes endereco){
		String complLogradouro = ESPACO_EM_BRANCO_10;
		if (endereco != null){
			if (endereco.getComplLogradouro() != null){
				complLogradouro = endereco.getComplLogradouro();
			}
			if (complLogradouro.length() > 10){
				complLogradouro = complLogradouro.substring(0, 10);
			}
			else if (complLogradouro.length() < 10){
				while (complLogradouro.length() < 10){
					complLogradouro += " ";
				}
			}			
		}
		return complLogradouro;
	}
	
	private String obterNumeroLogradouroEndereco(AipEnderecosPacientes endereco){
		String nroLogradouro = ESPACO_EM_BRANCO_5;
		if (endereco != null){
			Integer numero = endereco.getNroLogradouro();
			if (numero != null){
				nroLogradouro = Integer.toString(numero);			
			}
			if (nroLogradouro.length() < 5){
				while (nroLogradouro.length() < 5){
					nroLogradouro += " ";
				}
			}			
		}
		return nroLogradouro;
	}
	
	private String obterEnderecoResidencialPaciente(AipEnderecosPacientes endereco){
		String logradouro = ESPACO_EM_BRANCO_30;
		
		if (endereco != null){
			if (endereco.getAipBairrosCepLogradouro() != null){
				logradouro = endereco.getAipBairrosCepLogradouro().getAipLogradouro().getNome();
			}
			else if (endereco.getLogradouro() != null){
				logradouro = endereco.getLogradouro();
			}
			if (logradouro.length() > 30){
				logradouro = logradouro.substring(0, 30);
			}
			else if (logradouro.length() < 30){
				while (logradouro.length() < 30){
					logradouro+= " ";
				}
			}			
		}
		return logradouro;
	}
	
	
	
	public String gerarCabecalhoRass(StringBuffer sb){
		//1 - Codlinha 
		sb.append("01")
		//2 - Indicador de Início do cabeçalho
		.append("#RAS#")
		//3 - Ano e Mês
		.append(obterDataRass(new Date(), false))
		//4 - Quantidade Folhas
		.append(ESPACO_EM_BRANCO_6)
		//5 - Campo de Controle
		.append(ESPACO_EM_BRANCO_4)
		//6 - Nome do órgão
		.append(CABECALHO_NOME_ORGAO)
		//7 - Sigla do órgão
		.append(CABECALHO_SIGLA_ORGAO)
		//8 - CGC do órgão
		.append(CABECALHO_CGC_ORGAO)
		//9 - Nome do órgão de destino
		.append("SMS                                     ")
		//10 - Indicador de órgão de destino estadual ou municipal
		.append('M')
		//11 - Data de geração da remessa
		.append(obterDataRass(new Date(), true));
		//12 - Versão (deixar em branco)
		sb.append(ESPACO_EM_BRANCO_15)
		//13 - Versão do BDSIA
		.append(ESPACO_EM_BRANCO_7);
		
		return sb.toString();
	}
	
	public List<RelatorioConsultasAgendaVO> recuperarInformacoesConsultaParaRelPDF( List<AacConsultas> consultas) {
		List<RelatorioConsultasAgendaVO> listaVO = new ArrayList<RelatorioConsultasAgendaVO>();
		
		for (AacConsultas dadoConsulta : consultas) {
			RelatorioConsultasAgendaVO voConsultas = new RelatorioConsultasAgendaVO();
			recuperaDadosPaciente(dadoConsulta, voConsultas);
			recuperaDadosConsulta(dadoConsulta, voConsultas);
			recuperaDadosEspecialidade(dadoConsulta, voConsultas);

			listaVO.add(voConsultas);
		}
		listaVO = ordenarDadosConsultasByProfissionais(ordenarDadosConsultasByGrade(listaVO));
		return listaVO;
	}
	
	private List<RelatorioConsultasAgendaVO> ordenarDadosConsultasByProfissionais(List<RelatorioConsultasAgendaVO> lista){
		
		class ordenarDadosConsultasByProfissionais implements Comparator<RelatorioConsultasAgendaVO> {

			@Override
			public int compare(RelatorioConsultasAgendaVO lista,
					RelatorioConsultasAgendaVO lista2) {
				return lista.getProfissional().compareTo(lista2.getProfissional());
			}
		}
		List<RelatorioConsultasAgendaVO> listaConsultas = new ArrayList<RelatorioConsultasAgendaVO>();
		if (lista != null){
			listaConsultas.addAll(lista);			
		}
		
		Collections.sort(listaConsultas, new ordenarDadosConsultasByProfissionais());
		return listaConsultas;
		
	}
	
	private List<RelatorioConsultasAgendaVO> ordenarDadosConsultasByGrade(List<RelatorioConsultasAgendaVO> lista){
		
		class ordenarDadosConsultasByGrade implements Comparator<RelatorioConsultasAgendaVO> {

			@Override
			public int compare(RelatorioConsultasAgendaVO lista,
					RelatorioConsultasAgendaVO lista2) {
				return lista.getSeqGradeAgendamento().compareTo(lista2.getSeqGradeAgendamento());
			}
		}
		List<RelatorioConsultasAgendaVO> listaConsultas = new ArrayList<RelatorioConsultasAgendaVO>();
		if (lista != null){
			listaConsultas.addAll(lista);			
		}
		Collections.sort(listaConsultas, new ordenarDadosConsultasByGrade());
		return listaConsultas;
		
	}
	
	
 
	private void recuperaDadosPaciente(AacConsultas dadoConsulta,
			RelatorioConsultasAgendaVO voConsultas) {
		voConsultas.setPaciente(dadoConsulta.getPaciente() == null || dadoConsulta.getPaciente().getNome() == null ? "" : dadoConsulta.getPaciente().getNome());
		voConsultas.setCodigo(dadoConsulta.getPaciente() != null ? dadoConsulta.getPaciente().getCodigo().toString() : "");
		recuperaProntuarioCartaoSusPaciente(dadoConsulta, voConsultas);
		
		voConsultas.setIdade(dadoConsulta.getPaciente() != null && dadoConsulta.getPaciente().getIdade() != null ? dadoConsulta.getPaciente().getIdade().toString(): "");
		
		String dataNascimento = "";
		
		if (dadoConsulta.getPaciente() != null && dadoConsulta.getPaciente().getDtNascimento() != null) {
			dataNascimento = new SimpleDateFormat("dd/MM/yyyy").format(dadoConsulta.getPaciente().getDtNascimento());
		}

		voConsultas.setDataNascimento(dataNascimento);
		
	}
	
		private void recuperaProntuarioCartaoSusPaciente(AacConsultas dadoConsulta, RelatorioConsultasAgendaVO voConsultas) {
		voConsultas.setProntuario(dadoConsulta.getPaciente() != null && dadoConsulta.getPaciente().getProntuario() != null ? dadoConsulta .getPaciente().getProntuario().toString() : "");
		
		voConsultas.setCartaoSus(dadoConsulta.getPaciente() != null && dadoConsulta.getPaciente().getNroCartaoSaude() != null ? dadoConsulta.getPaciente().getNroCartaoSaude().toString() : null);
	}

	private void recuperaDadosConsulta(AacConsultas dadoConsulta,
			RelatorioConsultasAgendaVO voConsultas) {
		voConsultas.setNumeroConsulta(dadoConsulta.getNumero().toString());
	
		voConsultas.setSituacao(dadoConsulta.getCondicaoAtendimento() != null ? dadoConsulta.getCondicaoAtendimento().getDescricao() : null);
		
		String dataConsulta = new SimpleDateFormat("dd/MM/yyyy").format(dadoConsulta.getDtConsulta());
		voConsultas.setDataConsulta(dataConsulta);
		
		String horaConsulta = new SimpleDateFormat("HH:mm").format(dadoConsulta.getDtConsulta());
		voConsultas.setHoraConsulta(horaConsulta);
	}
	
	private void recuperaDadosEspecialidade(AacConsultas dadoConsulta, RelatorioConsultasAgendaVO voConsultas) {
		AacGradeAgendamenConsultas gradeAgendamenConsulta = dadoConsulta.getGradeAgendamenConsulta();
		
		defineProfissional(voConsultas, gradeAgendamenConsulta);
		
		defineEspecialidade(voConsultas, gradeAgendamenConsulta);
		
		voConsultas.setSeqGradeAgendamento(gradeAgendamenConsulta.getSeq().toString());
		
		voConsultas.setAndar(gradeAgendamenConsulta.getUnidadeFuncional().getAndar());
		
		voConsultas.setNomeEquipe(gradeAgendamenConsulta.getEquipe().getNome());
		
	}
	
	private void defineProfissional(RelatorioConsultasAgendaVO voConsultas, AacGradeAgendamenConsultas gradeAgendamenConsulta) {
		voConsultas.setProfissional(gradeAgendamenConsulta.getProfEspecialidade() == null ? StringUtils.EMPTY : gradeAgendamenConsulta.getProfEspecialidade().getRapServidor().getPessoaFisica().getNome());
	}
	
	private void defineEspecialidade(RelatorioConsultasAgendaVO voConsultas, AacGradeAgendamenConsultas gradeAgendamenConsulta) {
		voConsultas.setEspecialidade(gradeAgendamenConsulta.getEspecialidade() != null ? gradeAgendamenConsulta.getEspecialidade().getNomeEspecialidade() : StringUtils.EMPTY);
	}
	
	public String geraCabecalho() throws ApplicationBusinessException{
		String labelZona = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_LABEL_ZONA).getVlrTexto();
		String labelZonaSala = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_LABEL_SALA).getVlrTexto();
		StringBuffer sb = new StringBuffer();
		
		criarStringCabecalho(labelZona, labelZonaSala, sb);
		
		return sb.toString();
	}
		
	private void criarStringCabecalho(String labelZona, String labelZonaSala,
			StringBuffer sb) {
		sb.append("Sigla Especialidade").append(SEPARADOR);
		sb.append("Especialidade/Agenda").append(SEPARADOR);
		sb.append("Grade").append(SEPARADOR);
		sb.append("Equipe").append(SEPARADOR);
		sb.append("Profissional").append(SEPARADOR);
		sb.append("Profissional Responsável Equipe").append(SEPARADOR);
		sb.append("Sigla Especialidade Genérica").append(SEPARADOR);
		sb.append("Nome Especialidade Genérica").append(SEPARADOR);
		sb.append("Marcador").append(SEPARADOR);
		sb.append("Numero Regional do Conselho").append(SEPARADOR);
		sb.append("Consulta").append(SEPARADOR);
		sb.append("Data/Hora").append(SEPARADOR);
		sb.append("Dia").append(SEPARADOR);
		sb.append(labelZona).append(SEPARADOR);
		sb.append(labelZonaSala).append(SEPARADOR);
		sb.append("Pagador").append(SEPARADOR);
		sb.append("Autorização").append(SEPARADOR);
		sb.append("Atendimento").append(SEPARADOR);
		sb.append("Sit").append(SEPARADOR);
		sb.append("Descrição").append(SEPARADOR);
		sb.append("Codigo do Procedimento").append(SEPARADOR);
		sb.append("Dthr Marcação").append(SEPARADOR);
		sb.append("Prontuário").append(SEPARADOR);
		sb.append("Nome Paciente").append(SEPARADOR);
		sb.append("Nome Social").append(SEPARADOR);
		sb.append("Prnt Fam").append(SEPARADOR);
		sb.append("Observação").append(SEPARADOR);
		sb.append("Nome Mãe").append(SEPARADOR);
		sb.append("Código").append(SEPARADOR);
		sb.append("Retorno").append(SEPARADOR);
		sb.append("Logradouro").append(SEPARADOR);
		sb.append("Número").append(SEPARADOR);
		sb.append("Complemento").append(SEPARADOR);
		sb.append("Bairro").append(SEPARADOR);
		sb.append("Cidade").append(SEPARADOR);
		sb.append("Cep").append(SEPARADOR);
		sb.append("UF").append(SEPARADOR);
		sb.append("Telefone").append(SEPARADOR);
		sb.append("Data Nasc").append(SEPARADOR);
		sb.append("Sexo").append(SEPARADOR);
		sb.append("Func").append(SEPARADOR);
		sb.append("Cod Central").append(SEPARADOR);
		sb.append("Posto Saúde").append(SEPARADOR);
		sb.append("Excedente").append(SEPARADOR);
		sb.append("Número do Cartão SUS").append(SEPARADOR);
		sb.append("Procedimento");
	}
	
	
	public String geraLinhaConsulta(AacConsultas consulta){
		StringBuilder texto = new StringBuilder();
		SimpleDateFormat ddMMyyyy = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat ddMMyyHHmm = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		
		//Passado para este método para evitar complexidade ciclomática do PMD
		adicionarEspecialidadeANumeroConsulta(consulta, texto);
		adicionarDataConsultaATipoAgendamento(consulta, texto, ddMMyyHHmm);
		adicionarCondicaoAtendimentoADataMarcacao(consulta, texto, ddMMyyHHmm);
		adicionarObservacaoATelefone(consulta, texto);
		
		if (consulta.getPaciente()!=null){		
			addText(ddMMyyyy.format(consulta.getPaciente().getDtNascimento()), texto);		
			addText(consulta.getPaciente().getSexo()!=null?consulta.getPaciente().getSexo().name():null, texto);
		}else{
			addSeparador(2, texto);
		}
		addText(consulta.getIndFuncionario()!=null?consulta.getIndFuncionario().equals("D")?"Dependente":"Funcionário":null, texto);
		//addText("N", texto); //TODO Ver Unimed
		
		adicionarCodigoCentralACartaoSaude(consulta, texto);
		
//		adicionarProcedimentos(consulta, texto);
		return texto.toString();
	}
	//#43342
//	private void adicionarProcedimentos(AacConsultas consulta, StringBuilder texto){
//		List<AacConsultaProcedHospitalar> procedimentos = new ArrayList<AacConsultaProcedHospitalar>();
//        procedimentos.addAll(consulta.getProcedimentosHospitalares());
//		if(procedimentos != null && !procedimentos.isEmpty()){
//			StringBuilder strProced = new StringBuilder();
//			for (AacConsultaProcedHospitalar proc : procedimentos) {
//                if(proc != null && proc.getProcedHospInterno() != null && proc.getProcedHospInterno().getDescricao() != null){
//                    strProced.append(proc.getProcedHospInterno().getDescricao().trim());
//                }
//            }
//			addText(strProced, texto);
//		}
//	}

	private void adicionarCodigoCentralACartaoSaude(AacConsultas consulta,
			StringBuilder texto) {
		
		addText(consulta.getCodCentral(), texto);		
		addText(consulta.getPostoSaude(), texto);
		addText(consulta.getExcedeProgramacao() != null ? consulta.getExcedeProgramacao() ? "S" : "N" : null, texto);
		addText(consulta.getPaciente()!=null && consulta.getPaciente().getNroCartaoSaude()!=null?
				"#" + consulta.getPaciente().getNroCartaoSaude().toString():null, texto);		
	}

	private void adicionarObservacaoATelefone(AacConsultas consulta, StringBuilder texto){
		if (consulta.getPaciente()!=null){
			addText(consulta.getPaciente().getProntuario(), texto);
			addText(consulta.getPaciente().getNome(), texto);
			addText(consulta.getPaciente().getNomeSocial(), texto);
			
			//Prontuario familia
//			if (consulta.getPaciente().getIdSistemaLegado() != null) {
//				addText(QUALIFICADOR+ consulta.getPaciente().getIdSistemaLegado()+ QUALIFICADOR, texto);
//			} else {
//				addSeparador(1, texto);
//			}
			if(consulta.getPaciente().getGrupoFamiliarPaciente()!=null){
                addText(QUALIFICADOR + consulta.getPaciente().getGrupoFamiliarPaciente().getAgfSeq() + QUALIFICADOR, texto);
            }
			else{
				addSeparador(1,texto);
			}
			// Campo observacao do paciente da consulta
			if (consulta.getPaciente().getObservacao()!=null){
				addText(QUALIFICADOR + consulta.getPaciente().getObservacao()
						.replaceAll(System.getProperty(SEPARADOR_DE_LINHA), " ")
						.replaceAll("\\s", " ") + QUALIFICADOR, texto); //TODO verificar depois o tamanho campo
			}else{
				addSeparador(1,texto);
			}
			
			addText(consulta.getPaciente().getNomeMae(), texto);
			addText(consulta.getPaciente().getCodigo(), texto);
			
		}else{
			addSeparador(5, texto);			
		}
		addText(consulta.getRetorno()!=null?consulta.getRetorno().getDescricao():null, texto);

		AipEnderecosPacientes endereco = null;
		if (consulta.getPaciente()!=null){
			endereco=consulta.getPaciente().getEnderecoPadrao();
		}
		
		tratarEndereco(endereco, texto);
		
		if (consulta.getPaciente()!=null && consulta.getPaciente().getFoneResidencial()!=null){
			if (consulta.getPaciente().getDddFoneResidencial()!=null){
				addText(consulta.getPaciente().getDddFoneResidencial()+"-"+consulta.getPaciente().getFoneResidencial(), texto);
			}else{
				addText(consulta.getPaciente().getFoneResidencial(), texto);				
			}
		}else if (consulta.getPaciente()!=null && consulta.getPaciente().getFoneRecado()!=null){
			if (consulta.getPaciente().getDddFoneRecado()!=null){
				addText(consulta.getPaciente().getDddFoneRecado()+"-"+consulta.getPaciente().getFoneRecado(), texto);
			}else{
				addText(consulta.getPaciente().getFoneRecado(), texto);				
			}
		}else{
			addSeparador(1, texto);
		}
	}
	
	private void adicionarDataConsultaATipoAgendamento(AacConsultas consulta, StringBuilder texto, SimpleDateFormat ddMMyyHHmm){
		addText(ddMMyyHHmm.format(consulta.getDtConsulta()), texto);
		addText(CoreUtil.retornaDiaSemana(consulta.getDtConsulta()).toString().substring(0, 3), texto);
		addText(consulta.getGradeAgendamenConsulta().getUnidadeFuncional().getSigla(), texto);
		addText(consulta.getGradeAgendamenConsulta().getAacUnidFuncionalSala().getId().getSala(), texto);
		addText(consulta.getPagador()!=null?consulta.getPagador().getDescricao():null, texto);	
		addText(consulta.getTipoAgendamento()!=null?consulta.getTipoAgendamento().getDescricao():null, texto);
	}
	
	private void adicionarCondicaoAtendimentoADataMarcacao(AacConsultas consulta, StringBuilder texto, SimpleDateFormat ddMMyyHHmm){
		addText(consulta.getCondicaoAtendimento()!=null?consulta.getCondicaoAtendimento().getDescricao():null, texto);
		addText(consulta.getSituacaoConsulta()!=null?consulta.getSituacaoConsulta().getSituacao():null, texto);
		addText(consulta.getSituacaoConsulta()!=null?consulta.getSituacaoConsulta().getDescricao():null, texto);
		getCodigoProcedimento(texto, consulta);
		addText(consulta.getDthrMarcacao()!=null?ddMMyyHHmm.format(consulta.getDthrMarcacao()):null, texto);	
	}
	
	private void getCodigoProcedimento(StringBuilder texto,AacConsultas consulta){
		Integer Procedimento = null;
		for (AacGradeProcedHospitalar iterable_element : consulta.getGradeAgendamenConsulta().getGradeProcedimentosHospitalar()) {
			Procedimento = iterable_element.getProcedHospInterno().getSeq();
		}
		addText(Procedimento,texto);
 }
	
	/**
	 * Método que adiciona no CVS as colunas desde especialidade até a data de marcação
	 * @param consulta
	 * @param texto
	 */
	private void adicionarEspecialidadeANumeroConsulta(AacConsultas consulta, StringBuilder texto){
		String nomeProfissional="";
		/*-----TRECHO MELHORIA #26987----*/
		String nomeProfissionalResponsavel = "";
		String siglaEspecialidadeGenerica = "";
		String nomeEspecialidadeGenerica = "";
		String NroRegConselho = "";
		String nomeMarcacao = "";
		/*-------------------------------*/
		if(consulta.getGradeAgendamenConsulta().getProfEspecialidade()!=null) {
			nomeProfissional = consulta.getGradeAgendamenConsulta().getProfEspecialidade().getRapServidor().getPessoaFisica().getNome();	
		}
		/*-----TRECHO MELHORIA #26987----*/
		nomeProfissionalResponsavel = consulta.getGradeAgendamenConsulta().getEquipe().getProfissionalResponsavel().getPessoaFisica().getNome();
		if (consulta.getGradeAgendamenConsulta().getEspecialidade().getEspecialidade() != null){
			siglaEspecialidadeGenerica = consulta.getGradeAgendamenConsulta().getEspecialidade().getEspecialidade().getSigla();
			nomeEspecialidadeGenerica = consulta.getGradeAgendamenConsulta().getEspecialidade().getEspecialidade().getNomeEspecialidade();
		}
		/*-------------------------------*/
		addText(consulta.getGradeAgendamenConsulta().getEspecialidade().getSigla(), texto);
		addText(consulta.getGradeAgendamenConsulta().getEspecialidade().getNomeEspecialidade(), texto);	
		addText(consulta.getGradeAgendamenConsulta().getSeq(), texto);	
		addText(consulta.getGradeAgendamenConsulta().getEquipe().getNome(), texto);
		addText(nomeProfissional, texto);
		/*-----TRECHO MELHORIA #26987----*/
		addText(nomeProfissionalResponsavel, texto);
		addText(siglaEspecialidadeGenerica, texto);
		addText(nomeEspecialidadeGenerica, texto);
		
		for (RapQualificacao iterable_element : consulta.getGradeAgendamenConsulta().getEquipe().getProfissionalResponsavel().getPessoaFisica().getQualificacoes()) {
			NroRegConselho = iterable_element.getNroRegConselho();
		}
		
		//#55743 
		nomeMarcacao = consulta.getServidor().getPessoaFisica().getNome();
		addText(nomeMarcacao, texto); 
		
		addText(NroRegConselho,texto);
		/*---54552 ----*/
		/*-------------------------------*/
		addText(consulta.getNumero(), texto);		
	}

	/**
	 * Trata o endereço de correspondência do paciente
	 * @param endereco
	 * @param texto
	 */
	private void tratarEndereco(AipEnderecosPacientes endereco,
			StringBuilder texto) {
		if (endereco!=null){
			AipCidades cidade = null;
			if (endereco.getAipBairrosCepLogradouro() != null){
				addText(endereco.getAipBairrosCepLogradouro().getCepLogradouro().getLogradouro().getNome(), texto);
				addText(endereco.getNroLogradouro(), texto);
				addText(endereco.getComplLogradouro(), texto);
				addText(endereco.getAipBairrosCepLogradouro().getAipBairro().getDescricao(), texto);
				cidade = endereco.getAipBairrosCepLogradouro().getCepLogradouro().getLogradouro().getAipCidade();
				addText(cidade!=null?cidade.getNome():null, texto);
				addText(endereco.getAipBairrosCepLogradouro().getId().getCloCep(), texto);	
				addText(cidade!=null && cidade.getAipUf()!=null?cidade.getAipUf().getSigla():null, texto);	
			}
			else{
				addText(endereco.getLogradouro(), texto);
				addText(endereco.getNroLogradouro(), texto);
				addText(endereco.getComplLogradouro(), texto);
				addText(endereco.getBairro(), texto);
				cidade = endereco.getAipCidade();
				addText(cidade!=null?cidade.getNome():null, texto);
				addText(endereco.getCep(), texto);
				addText(cidade!=null && cidade.getAipUf()!=null?cidade.getAipUf().getSigla():null, texto);	
			}	
		}else{
			addSeparador(7, texto);
		}
		
	}
	
	private void addText(Object texto, StringBuilder sb){
		if (texto != null) {
			sb.append(texto);
		}
		sb.append(SEPARADOR);
	}

	private void addSeparador(int quant, StringBuilder sb){
		sb.append(StringUtils.repeat(SEPARADOR, quant));
	}	
	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}
	
	public IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
}
