package br.gov.mec.aghu.exames.contratualizacao.business;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.business.fonetizador.FonetizadorUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.dominio.DominioCor;
import br.gov.mec.aghu.dominio.DominioFatTipoCaractItem;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoEndereco;
import br.gov.mec.aghu.exames.contratualizacao.util.Header;
import br.gov.mec.aghu.exames.contratualizacao.util.Item;
import br.gov.mec.aghu.exames.contratualizacao.util.Itens;
import br.gov.mec.aghu.exames.contratualizacao.util.Paciente;
import br.gov.mec.aghu.exames.contratualizacao.util.SolicitacaoExame;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipCepLogradouros;
import br.gov.mec.aghu.model.AipCidades;
import br.gov.mec.aghu.model.AipEnderecosPacientes;
import br.gov.mec.aghu.model.AipEnderecosPacientesId;
import br.gov.mec.aghu.model.AipEtnia;
import br.gov.mec.aghu.model.AipFonemaNomeSocialPacientes;
import br.gov.mec.aghu.model.AipFonemaPacientes;
import br.gov.mec.aghu.model.AipFonemasMaePaciente;
import br.gov.mec.aghu.model.AipNacionalidades;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.AipUfs;
import br.gov.mec.aghu.model.FatConvGrupoItemProced;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.cadastro.business.ICadastroPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;


public class PacienteContratualizacaoCommand extends ContratualizacaoCommand {

	private static final long serialVersionUID = -4007537779282839689L;

	private static final Log LOG = LogFactory.getLog(PacienteContratualizacaoCommand.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@Inject
	private IParametroFacade parametroFacade;

	@Inject
	private IFaturamentoFacade faturamentoFacade;

	@Inject
	private IPacienteFacade pacienteFacade;

	@Inject
	private IAghuFacade aghuFacade;

	@Inject
	private ICadastroPacienteFacade cadastroPacienteFacade;

	private final String MENSAGEM_OBSERVACAO_PACIENTE = "Paciente gerado através de integração - contratualização exames";
	private final Integer TAMANHO_CAMPO_NOME = 50;
	private final Integer TAMANHO_CAMPO_NOME_MAE = 50;
	private final Integer TAMANHO_CAMPO_LOGRADOURO = 60;
	private final Integer TAMANHO_CAMPO_COMPLEMENTO = 20;
	private final Integer TAMANHO_CAMPO_BAIRRO = 20;	
	
	private Itens itens;
	private Byte planoConvenio;
	private Short convenio;
	
	public enum PacienteContratualizacaoActionExceptionCode implements BusinessExceptionCode {
		MENSAGEM_DADOS_OBRIGATORIOS_PACIENTE, MENSAGEM_DATA_NASCIMENTO_INVALIDA_CONTRATUALIZACAO, MENSAGEM_SEXO_PACIENTE_INVALIDO, MENSAGEM_PACIENTE_DATA_OBITO_REGISTRADA, MENSAGEM_DADOS_OBRIGATORIOS_ENDERECO, MENSAGEM_NOME_PACIENTE_MAIOR_PERMITIDO, MENSAGEM_CEP_CIDADE_UF_INVALIDO, MENSAGEM_NACIONALIDADE_INVALIDA, MENSAGEM_RACA_INVALIDA, MENSAGEM_RACA_INDIGENA_EXIGE_ETNIA, MENSAGEM_CARTAO_SAUDE_INVALIDO;
	}

	/**
	 * Localizar o paciente recebido no xml de integração da contratualização de
	 * exames. Caso o paciente não seja localizado, o mesmo será inserido
	 * automaticamente na base de dados
	 * 
	 * @param nome
	 * @param nomeMae
	 * @param dataNascimento
	 * @param sexo
	 * @param nroCartaoSaude
	 * @param logradouro
	 * @param nroLogradouro
	 * @param complLogradouro
	 * @param bairro
	 * @param cidade
	 * @param cep
	 * @return
	 * @throws BaseException
	 * @throws ParseException
	 */
	public AipPacientes localizarPaciente(String nomeParam, String nomeMaeParam,
			String dataNascimentoParam, String sexoParam,
			String nroCartaoSaudeParam, String logradouro, String nroLogradouro,
			String complLogradouro, String bairro, Integer cep, String cidade, String siglaUf,
			Integer nacionalidadeParam, String raca, Integer etniaParam, String nomeMicrocomputador) throws BaseException,
			ParseException {

		if (StringUtils.isEmpty(nomeParam) || StringUtils.isEmpty(nomeMaeParam)
				|| StringUtils.isEmpty(dataNascimentoParam)
				|| StringUtils.isEmpty(sexoParam)) {
			throw new ApplicationBusinessException(
					PacienteContratualizacaoActionExceptionCode.MENSAGEM_DADOS_OBRIGATORIOS_PACIENTE);
		}

		if (StringUtils.isEmpty(logradouro) || StringUtils.isEmpty(bairro)) {
			throw new ApplicationBusinessException(
					PacienteContratualizacaoActionExceptionCode.MENSAGEM_DADOS_OBRIGATORIOS_ENDERECO);
		}
				
		Date dataNascimento = this.carregarDataNascimento(dataNascimentoParam);
		DominioSexo sexo = this.carregarSexoPaciente(sexoParam);
		BigInteger nroCartaoSaude =  this.carregarCartaoSaude(nroCartaoSaudeParam);
		String nomeAjustado = this.ajustarNome(nomeParam, true);
		String nomeMaeAjustado = this.ajustarNome(nomeMaeParam, false);
		AipNacionalidades nacionalidade = this.getPacienteFacade()
				.obterNacionalidadePorCodigoAtiva(nacionalidadeParam);
		DominioCor cor = DominioCor.getInstance(raca);
		AipEtnia etnia = null;
		if(DominioCor.I.equals(cor) && etniaParam != null){
			etnia = (this.getPacienteFacade().obterAipEtniaPorChavePrimaria(etniaParam));
		}
		AipCidades aipCidade = this.carregarCidade(cep, cidade, siglaUf);
		AipUfs aipUf = siglaUf!= null?this.getPacienteFacade().obterAipUfsPorChavePrimaria(siglaUf):null;
		
		AipPacientes paciente = this.buscarPaciente(nomeAjustado,
				nomeMaeAjustado, dataNascimento, nroCartaoSaude);

		if (paciente != null) {
			this.validarDataObito(paciente);
			return this.atualizarPaciente(paciente, nacionalidade, cor, etnia,
					aipCidade, nroCartaoSaude, nomeMicrocomputador, new Date());
		} else {
			return this.incluirPaciente(nomeAjustado, nomeMaeAjustado,
					dataNascimento, sexo, nroCartaoSaude, logradouro,
					nroLogradouro, complLogradouro, bairro, aipCidade, cep,
					aipUf, nacionalidade, cor, etnia);
		}
		
	}

	/**
	 * Ajustar o nro do logradouro, caso seja maior que 5 assumir zero 
	 * @param nroLogradouro
	 * @return
	 */
	protected Integer ajustarNroLogradouro(String nroLogradouro) {

		nroLogradouro = StringUtils.trimToNull(nroLogradouro);
		Integer numLogradouro = Integer.valueOf(0);
		if (StringUtils.isNotEmpty(nroLogradouro)
				&& StringUtils.isNumeric(nroLogradouro)
				&& nroLogradouro.length() <= 5) {
			numLogradouro = Integer.parseInt(nroLogradouro);
		}
		return numLogradouro;

	}
	
	/**
	 * Validar a regra da data de óbito
	 * @param paciente
	 * @throws BaseException
	 */
	protected void validarDataObito(AipPacientes paciente) throws BaseException{
		if (paciente.getDtObito() != null) {
			throw new ApplicationBusinessException(
					PacienteContratualizacaoActionExceptionCode.MENSAGEM_PACIENTE_DATA_OBITO_REGISTRADA);
		}
	}
	
	/**
	 * Realizar a atualização do paciente caso uma das informações enviadas no
	 * xml não estejam na base de dados
	 * 
	 * @param paciente
	 * @param nacionalidade
	 * @param cor
	 * @param etnia
	 * @param cidade
	 * @param nroCartaoSaude
	 * @return
	 * @throws BaseException
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	private AipPacientes atualizarPaciente(AipPacientes paciente,
			AipNacionalidades nacionalidade, DominioCor cor, AipEtnia etnia,
			AipCidades cidade, BigInteger nroCartaoSaude, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		
		boolean atualizouPaciente = false;
		boolean atualizouEndereco = false;
		
		if(paciente.getNroCartaoSaude() == null && nroCartaoSaude != null){
			paciente.setNroCartaoSaude(nroCartaoSaude);
			atualizouPaciente = true;
		}
		
		if (paciente.getProntuario() == null) {

			if (paciente.getAipNacionalidades() == null
					&& nacionalidade != null) {
				paciente.setAipNacionalidades(nacionalidade);
				atualizouPaciente = true;
			}

			if (paciente.getCor() == null && cor != null) {
				paciente.setCor(cor);
				atualizouPaciente = true;
				if (DominioCor.I.equals(paciente.getCor())
						&& paciente.getEtnia() == null && etnia != null) {
					paciente.setEtnia(etnia);
				}
			}

			if (cidade != null) {
				boolean atualizaEndereco = true;
				Set<AipEnderecosPacientes> setEnderecos = paciente
						.getEnderecos();
				for (AipEnderecosPacientes endereco : setEnderecos) {
					if (endereco.getAipCidade() != null || !StringUtils.isEmpty(endereco.getCidade())) {
						atualizaEndereco = false;
						break;
					}
				}

				if (atualizaEndereco) {
					paciente.getEnderecoPadrao().setAipCidade(cidade);
					atualizouEndereco = true;
				}
			}
		}		
		
		this.realizarValidacoesFaturamento(paciente);

		if (atualizouPaciente || atualizouEndereco) {
			paciente = this.atualizarDadosRecadastroPaciente(paciente);
			if (atualizouEndereco) {
				this.atualizarJournalEnderecoPaciente(paciente);
			}
			this.getCadastroPacienteFacade().atualizarPacienteParcial(paciente, nomeMicrocomputador, dataFimVinculoServidor);
		}
		
		return paciente;
	}

	/**
	 * Atualizar a journal do paciente
	 * @param paciente
	 */
	private void atualizarJournalEnderecoPaciente(AipPacientes paciente){
		AipEnderecosPacientes enderecoPaciente = paciente
				.getEnderecoPadrao();
		AipEnderecosPacientes enderecoPacienteOld = this
				.getCadastroPacienteFacade().obterEnderecoBanco(
						enderecoPaciente.getId());
		enderecoPacienteOld.setId(enderecoPaciente.getId());
		this.getCadastroPacienteFacade().aiptEnpAru(
				enderecoPacienteOld, enderecoPaciente);		
	}
	
	/**
	 * Atualizar informações referente a atualização do paciente
	 * @param paciente
	 * @return
	 * @throws ApplicationBusinessException  
	 */
	private AipPacientes atualizarDadosRecadastroPaciente(AipPacientes paciente) throws ApplicationBusinessException{
		
		//this.getCadastroPacienteFacade().organizarValores(paciente.getEnderecos());
		//this.getCadastroPacienteFacade().atribuirSequencialEnderecos(paciente);
		
		AipPacientes pacienteAnterior = this.getCadastroPacienteFacade()
				.obterPacienteAnterior(paciente.getCodigo());

		if (this.getCadastroPacienteFacade().pacienteModificado(paciente,
				pacienteAnterior)) {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogadoSemCache();
			
			paciente.setRapServidoresRecadastro(
					servidorLogado);
			paciente.setFccCentroCustosRecadastro(
					servidorLogado.getCentroCustoLotacao());				
			paciente.setDtRecadastro(Calendar.getInstance().getTime());
		}
		
		return paciente;
	}
	
	/**
	 * 
	 * @param cep
	 * @param cidade
	 * @param siglaUf
	 * @return
	 */
	protected AipCidades carregarCidade(Integer cep, String cidade, String siglaUf){
		
		AipCidades aipCidade = null;
		
		if (cep != null && cep != 0) {
			List<AipCepLogradouros> listCepLogradouro = this
					.getPacienteFacade().listarCepLogradourosPorCEP(cep);

			if(listCepLogradouro!= null && !listCepLogradouro.isEmpty()){
				aipCidade = listCepLogradouro.get(0).getLogradouro()
					.getAipCidade();
			}
		}
		if (aipCidade == null && cidade != null && siglaUf != null) {			
			List<AipCidades> listaCidade = this.getPacienteFacade().pesquisarCidadesPorNomeSiglaUf(cidade, siglaUf);
			aipCidade = listaCidade.get(0);
		}
		
		return aipCidade;
	}
	
	/**
	 * Ajustar o nome do paciente e o nome da mãe, retirando os caracteres
	 * especiais e consistir o tamanho quando o nome for do paciente
	 * 
	 * @param nome
	 * @param isPaciente
	 * @return
	 * @throws BaseException
	 */
	protected String ajustarNome(String nome, boolean isPaciente)
			throws BaseException {

		if (isPaciente && nome.length() > TAMANHO_CAMPO_NOME) {
			throw new ApplicationBusinessException(
					PacienteContratualizacaoActionExceptionCode.MENSAGEM_NOME_PACIENTE_MAIOR_PERMITIDO,
					TAMANHO_CAMPO_NOME);
		}
		return FonetizadorUtil.ajustarNome(nome);

	}
	
	/**
	 * Carregar o numero do cartão nacional de saúde caso o mesmo seja válido
	 * @param nroCartaoSaudeArquivo
	 * @return
	 */
	private BigInteger carregarCartaoSaude(String nroCartaoSaudeArquivo) {

		if (!StringUtils.isEmpty(nroCartaoSaudeArquivo)) {
			BigInteger nroCartaoSaude = new BigInteger(nroCartaoSaudeArquivo);
			return nroCartaoSaude;
		} else {
			return null;
		}

	}
	
	/**
	 * Carregar o sexo do paciente caso o mesmo seja válido
	 * @param sexoArquivo
	 * @return
	 * @throws BaseException
	 */
	protected DominioSexo carregarSexoPaciente(String sexoArquivo) throws BaseException{
		
		DominioSexo sexo = DominioSexo.getInstance(sexoArquivo);

		if (sexo == null || StringUtils.isBlank(sexo.getDescricao())) {
			throw new ApplicationBusinessException(
					PacienteContratualizacaoActionExceptionCode.MENSAGEM_SEXO_PACIENTE_INVALIDO);
		}

		return sexo;
	}
	
	/**
	 * Carregar a data de nascimento do paciente caso a mesma seja válida
	 * @param dataNascimentoArquivo
	 * @return
	 * @throws BaseException
	 */
	private Date carregarDataNascimento(String dataNascimentoArquivo) throws BaseException{
		
		Date dataNascimento;
		
		Pattern p = Pattern.compile(RegexUtil.DATE_PATTERN);
		Matcher m = p.matcher(dataNascimentoArquivo);
		if (!m.matches() || !RegexUtil.validarDias(dataNascimentoArquivo)) {
			throw new ApplicationBusinessException(PacienteContratualizacaoActionExceptionCode.MENSAGEM_DATA_NASCIMENTO_INVALIDA_CONTRATUALIZACAO);
		} else {		
			DateFormat sdformat = new SimpleDateFormat("ddMMyyyy");
			try {
				dataNascimento = sdformat.parse(dataNascimentoArquivo);
			} catch (ParseException e) {
				throw new ApplicationBusinessException(PacienteContratualizacaoActionExceptionCode.MENSAGEM_DATA_NASCIMENTO_INVALIDA_CONTRATUALIZACAO);
			}
		}
		
		if (dataNascimento.after(Calendar.getInstance().getTime())) {
			throw new ApplicationBusinessException(
					PacienteContratualizacaoActionExceptionCode.MENSAGEM_DATA_NASCIMENTO_INVALIDA_CONTRATUALIZACAO);
		}
		
		return dataNascimento;
	}
	
	/**
	 * Incluir novo paciente e endereço a partir da solicitação externa de
	 * exames
	 * 
	 * @param nome
	 * @param nomeMae
	 * @param dataNascimento
	 * @param sexo
	 * @param nroCartaoSaude
	 * @param logradouro
	 * @param nroLogradouro
	 * @param complLogradouro
	 * @param bairro
	 * @param cidade
	 * @param cep
	 * @return
	 * @throws BaseException
	 */
	protected AipPacientes incluirPaciente(String nome, String nomeMae,
			Date dataNascimento, DominioSexo sexo, BigInteger nroCartaoSaude,
			String logradouro, String nroLogradouro, String complLogradouro,
			String bairro, AipCidades aipCidade, Integer cep, AipUfs aipUfs, AipNacionalidades nacionalidade, DominioCor cor, AipEtnia etnia) throws BaseException {

		AipPacientes paciente = this.carregarPaciente(nome, nomeMae,
				dataNascimento, sexo, nroCartaoSaude, nacionalidade, cor, etnia, aipCidade);
		AipEnderecosPacientes enderecoPaciente = this
				.carregarEnderecoPaciente(logradouro, nroLogradouro,
						complLogradouro, bairro, aipCidade, cep, aipUfs);
		enderecoPaciente.setAipPaciente(paciente);
		paciente.getEnderecos().add(enderecoPaciente);
		
		this.realizarValidacoesFaturamento(paciente);		
		this.inserirPacienteEEnderecoSemValidacao(paciente);
		return paciente;
	}

	/**
	 * Inserir um novo paciente e o endereço sem realizar validações de tela
	 * @param paciente
	 * @return
	 * @throws BaseException
	 */
	private AipPacientes inserirPacienteEEnderecoSemValidacao(AipPacientes paciente) throws BaseException{
		Set<AipEnderecosPacientes> enderecos = paciente.getEnderecos();
		paciente.setEnderecos(null);	
		this.getCadastroPacienteFacade().inserirPaciente(paciente);
		if (enderecos!=null && !enderecos.isEmpty()){
			paciente.setEnderecos(enderecos);
			this.getCadastroPacienteFacade().atribuirSequencialEnderecos(paciente);	
			this.getCadastroPacienteFacade().persistirPacienteSemValidacao(paciente);
		}		
		this.getCadastroPacienteFacade().salvarFonemas(paciente, paciente.getNome(), AipFonemaPacientes.class);
		this.getCadastroPacienteFacade().salvarFonemas(paciente, paciente.getNomeMae(), AipFonemasMaePaciente.class);
		this.getCadastroPacienteFacade().salvarFonemas(paciente, paciente.getNomeSocial(), AipFonemaNomeSocialPacientes.class);
		this.flush();		
		
		return paciente;
	}

	/**
	 * Carregar o paciente
	 * 
	 * @param nome
	 * @param nomeMae
	 * @param dataNascimento
	 * @param sexo
	 * @param nroCartaoSaude
	 * @throws ApplicationBusinessException  
	 */
	protected AipPacientes carregarPaciente(String nome, String nomeMae,
			Date dataNascimento, DominioSexo sexo, BigInteger nroCartaoSaude,
			AipNacionalidades nacionalidade, DominioCor cor, AipEtnia etnia, AipCidades aipCidade) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogadoSemCache();

		AipPacientes paciente = new AipPacientes();
		paciente.setFccCentroCustosCadastro(servidorLogado.getCentroCustoLotacao());
		paciente.setRapServidoresCadastro(servidorLogado);
		paciente.setNome(nome);
		paciente.setNomeMae(nomeMae.length() > TAMANHO_CAMPO_NOME_MAE ? StringUtils
				.substring(nomeMae, 0, TAMANHO_CAMPO_NOME_MAE) : nomeMae);
		paciente.setDtNascimento(dataNascimento);
		paciente.setDtIdentificacao(Calendar.getInstance().getTime());
		paciente.setSexo(sexo);
		paciente.setObservacao(MENSAGEM_OBSERVACAO_PACIENTE);
		paciente.setCadConfirmado(DominioSimNao.N);
		paciente.setIndGeraProntuario(DominioSimNao.N);
		paciente.setNroCartaoSaude(nroCartaoSaude);
		paciente.setIndPacienteVip(DominioSimNao.N);
		paciente.setCriadoEm(Calendar.getInstance().getTime());
		paciente.setSexoBiologico(sexo);		
		paciente.setAipNacionalidades(nacionalidade);
		paciente.setCor(cor);
		if(paciente.getCor() != null && DominioCor.I.equals(paciente.getCor())){
			paciente.setEtnia(etnia);
		}
		
		return paciente;

	}

	/**
	 * Carregar o endereço do paciente
	 * 
	 * @param logradouro
	 * @param nroLogradouro
	 * @param complLogradouro
	 * @param ,bairro
	 * @param cidade
	 * @param cep
	 * @return
	 * @throws ApplicationBusinessException 
	 *  
	 */
	public AipEnderecosPacientes carregarEnderecoPaciente(String logradouro,
			String nroLogradouro, String complLogradouro, String bairro,
			AipCidades aipCidade, Integer cep, AipUfs aipUf)
			throws BaseException {
		AipEnderecosPacientes enderecoPaciente = new AipEnderecosPacientes();

		enderecoPaciente.setId(new AipEnderecosPacientesId());
		enderecoPaciente.setTipoEndereco(DominioTipoEndereco.R);
		enderecoPaciente.setIndPadrao(DominioSimNao.S);
		enderecoPaciente
				.setLogradouro(logradouro.length() > TAMANHO_CAMPO_LOGRADOURO ? StringUtils
						.substring(logradouro, 0, TAMANHO_CAMPO_LOGRADOURO)
						: logradouro);
		enderecoPaciente.setCep(cep);
		enderecoPaciente.setNroLogradouro(this.ajustarNroLogradouro(nroLogradouro));
		if (complLogradouro != null) {
			enderecoPaciente
					.setComplLogradouro(complLogradouro.length() > TAMANHO_CAMPO_COMPLEMENTO ? StringUtils
							.substring(complLogradouro, 0,
									TAMANHO_CAMPO_COMPLEMENTO)
							: complLogradouro);
		}
		enderecoPaciente
				.setBairro(bairro.length() > TAMANHO_CAMPO_BAIRRO ? StringUtils
						.substring(bairro, 0, TAMANHO_CAMPO_BAIRRO) : bairro);
		enderecoPaciente.setAipCidade(aipCidade);
		enderecoPaciente.setAipUf(aipUf);

		return enderecoPaciente;
	}
	
	/**
	 * Buscar o paciente conforme regra estabelecida na geração de solicitação
	 * externa de exames 1. Número cartão saúde enviado: 1.1 Localizar o
	 * paciente pelo número do cartão + nome 2. Número cartão saúde não enviado:
	 * 2.1 Localizar o paciente pelo nome + nome mãe + data nascimento
	 * 
	 * @param nome
	 * @param nomeMae
	 * @param dataNascimento
	 * @param numCartaoSaude
	 * @return
	 */
	protected AipPacientes buscarPaciente(String nome, String nomeMae,
			Date dataNascimento, BigInteger nroCartaoSaude) {

		AipPacientes paciente = null;
		
		if (nroCartaoSaude != null) {
			List<AipPacientes> listaPac = getPacienteFacade()
					.pesquisarPacientePorNomeENumeroCartaoSaude(nome,
							nroCartaoSaude);
			if (listaPac != null && !listaPac.isEmpty()) {
				paciente = listaPac.get(0);
			}
		} else {
			List<AipPacientes> listaPac = getPacienteFacade()
					.pesquisarPacientePorNomeDtNacimentoComHoraNomeMae(nome,
							dataNascimento, nomeMae);
			if (listaPac != null && !listaPac.isEmpty()) {
				paciente = listaPac.get(0);
			}
		}

		return paciente;
	}

	
	private void realizarValidacoesFaturamento(AipPacientes paciente)
			throws BaseException {
		
		AghParametros exigeQuandoBPI = this.getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_EXIGE_QUANDO_BPI);
		
		if ("S".equals(exigeQuandoBPI.getVlrTexto()) && !this.exameCobraBpi()) {
			return;
		}
		
		if (paciente.getEnderecoPadrao().getAipCidade() == null) {
			throw new ApplicationBusinessException(
					PacienteContratualizacaoActionExceptionCode.MENSAGEM_CEP_CIDADE_UF_INVALIDO);
		}

		if (paciente.getAipNacionalidades() == null) {
			throw new ApplicationBusinessException(
					PacienteContratualizacaoActionExceptionCode.MENSAGEM_NACIONALIDADE_INVALIDA);
		}

		if (paciente.getCor() == null) {
			throw new ApplicationBusinessException(
					PacienteContratualizacaoActionExceptionCode.MENSAGEM_RACA_INVALIDA);
		}

		if (DominioCor.I.equals(paciente.getCor())
				&& paciente.getEtnia() == null) {
			throw new ApplicationBusinessException(
					PacienteContratualizacaoActionExceptionCode.MENSAGEM_RACA_INDIGENA_EXIGE_ETNIA);
		}

		if (paciente.getNroCartaoSaude() == null) {
			throw new ApplicationBusinessException(
					PacienteContratualizacaoActionExceptionCode.MENSAGEM_CARTAO_SAUDE_INVALIDO);
		}

	}
	
	/**
	 * Verificar a necessidade de realizar as validações de faturamento. Apenas
	 * realiza as validações para exames com característica 'COBRA BPI'.
	 * 
	 * @param listItens
	 * @param seqPlano
	 * @param codigoConvenio
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private boolean exameCobraBpi() throws ApplicationBusinessException {
		
		if (this.itens == null || this.planoConvenio == null || this.convenio == null) {
			return false;
		}
		
		AghParametros tabelaFaturadaPadrao = this.getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_TABELA_FATUR_PADRAO);
						
		for (Item item : this.itens.getItem()) {
			
			if (!StringUtils.isEmpty(item.getSiglaExame())
					&& !StringUtils.isEmpty(item.getMaterialAnalise())
					&& !StringUtils.isEmpty(item.getUnidadeExecutora())
					&& tabelaFaturadaPadrao.getVlrNumerico() != null) {
				
				List<FatConvGrupoItemProced> listConvGrupoItem = 
						this.getFaturamentoFacade().obterListaFatConvGrupoItensProcedPorExame(
								this.planoConvenio,
								this.convenio,
								Short.parseShort(tabelaFaturadaPadrao
										.getVlrNumerico().toString()),
								item.getSiglaExame(),
								Integer.parseInt(item.getMaterialAnalise()),
								Short.parseShort(item.getUnidadeExecutora()));
				
				for(FatConvGrupoItemProced convGrupoItemProced : listConvGrupoItem){
					final Boolean cobraBpi = this.getFaturamentoFacade()
							.verificarCaracteristicaExame(
									convGrupoItemProced.getId().getIphSeq(),
									convGrupoItemProced.getId().getIphPhoSeq(),
									DominioFatTipoCaractItem.COBRA_BPI);
					if (cobraBpi) {
						return true;
					}
				}
			}
		}

		return false;
	}
	
	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}
	
	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}
	
	protected ICadastroPacienteFacade getCadastroPacienteFacade() {
		return cadastroPacienteFacade;
	}

	@Override
	@SuppressWarnings("PMD.NPathComplexity")
	Map<String, Object> executar(Map<String, Object> parametros)
			throws NumberFormatException, BaseException, ParseException {
		
		if( parametros == null){
			throw new IllegalArgumentException("Argumento inválido.");
		}
		
		Paciente pacienteIntegracao = null;
		if (parametros.containsKey(PACIENTE_INTEGRACAO)) {
			pacienteIntegracao = (Paciente) parametros.get(PACIENTE_INTEGRACAO);
		}
		
		if (parametros.containsKey(SOLICITACAO_INTEGRACAO)) {
			SolicitacaoExame solicitacao = (SolicitacaoExame) parametros.get(ContratualizacaoCommand.SOLICITACAO_INTEGRACAO);
			this.setItens(solicitacao.getItens());
		}		
		
		Header headerIntegracao = null;
		if (parametros.containsKey(HEADER_INTEGRACAO)) {
			headerIntegracao = (Header) parametros.get(HEADER_INTEGRACAO);
			this.setPlanoConvenio(headerIntegracao.getPlanoConvenio());
			this.setConvenio((short)headerIntegracao.getConvenio());
		}
		
		String nomeMicrocomputador = (String) parametros.get(ContratualizacaoCommand.NOME_MICROCOMPUTADOR);
		
		if (pacienteIntegracao != null) {
			AipPacientes paciente = this.localizarPaciente(
					pacienteIntegracao.getNome(),
					pacienteIntegracao.getNomeMae(),
					pacienteIntegracao.getDataNascimento(),
					pacienteIntegracao.getSexo(),
					pacienteIntegracao
							.getNumeroCartaoSaude(),
					pacienteIntegracao.getLogradouro().getRua(),
					pacienteIntegracao.getLogradouro().getNumero(),
					pacienteIntegracao.getLogradouro().getComplemento(),
					pacienteIntegracao.getLogradouro().getBairro(),
					!StringUtils.isEmpty(pacienteIntegracao.getLogradouro()
							.getCep()) ? Integer.parseInt(pacienteIntegracao
							.getLogradouro().getCep()) : null,
					pacienteIntegracao.getLogradouro().getCidade(),		
					pacienteIntegracao.getLogradouro().getSiglaUf(),
					!StringUtils.isEmpty(pacienteIntegracao.getNacionalidade()) ? Integer.parseInt(pacienteIntegracao.getNacionalidade()):null,
							pacienteIntegracao.getRaca(),
							!StringUtils.isEmpty(pacienteIntegracao.getEtnia())?Integer.parseInt(pacienteIntegracao.getEtnia()):null, nomeMicrocomputador);

			parametros.put(PACIENTE_AGHU, paciente);
			return parametros;
		} else {
			return null;
		}

	}

	@Override
	boolean comitar() {
		return true;
	}

	public Itens getItens() {
		return itens;
	}

	public void setItens(Itens itens) {
		this.itens = itens;
	}

	public Byte getPlanoConvenio() {
		return planoConvenio;
	}

	public void setPlanoConvenio(Byte planoConvenio) {
		this.planoConvenio = planoConvenio;
	}

	public Short getConvenio() {
		return convenio;
	}

	public void setConvenio(Short convenio) {
		this.convenio = convenio;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
