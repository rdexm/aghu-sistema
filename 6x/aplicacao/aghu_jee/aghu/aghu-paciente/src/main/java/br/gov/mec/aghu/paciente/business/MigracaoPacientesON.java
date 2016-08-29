package br.gov.mec.aghu.paciente.business;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.NonUniqueObjectException;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.JDBCConnectionException;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.dominio.DominioCor;
import br.gov.mec.aghu.dominio.DominioEstadoCivil;
import br.gov.mec.aghu.dominio.DominioGrauInstrucao;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoEndereco;
import br.gov.mec.aghu.dominio.DominioTipoProntuario;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.AipBairrosCepLogradouro;
import br.gov.mec.aghu.model.AipCidades;
import br.gov.mec.aghu.model.AipEnderecosPacientes;
import br.gov.mec.aghu.model.AipEnderecosPacientesId;
import br.gov.mec.aghu.model.AipFonemaNomeSocialPacientes;
import br.gov.mec.aghu.model.AipFonemaPacientes;
import br.gov.mec.aghu.model.AipFonemasMaePaciente;
import br.gov.mec.aghu.model.AipNacionalidades;
import br.gov.mec.aghu.model.AipPacienteProntuario;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.VAipLocalidadeUc;
import br.gov.mec.aghu.paciente.business.validacaoprontuario.InterfaceValidaProntuario;
import br.gov.mec.aghu.paciente.business.validacaoprontuario.ValidaProntuarioException;
import br.gov.mec.aghu.paciente.business.validacaoprontuario.ValidaProntuarioFactory;
import br.gov.mec.aghu.paciente.cadastro.business.ICadastroPacienteFacade;
import br.gov.mec.aghu.paciente.cadastrosbasicos.business.ICadastrosBasicosPacienteFacade;
import br.gov.mec.aghu.paciente.dao.AipEnderecosPacientesDAO;
import br.gov.mec.aghu.paciente.dao.AipPacienteProntuarioDAO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;

@SuppressWarnings("PMD.ExcessiveClassLength")
@Stateless
public class MigracaoPacientesON extends BaseBusiness{

	private static final Log LOG = LogFactory.getLog(MigracaoPacientesON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private ICadastroPacienteFacade cadastroPacienteFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private ICentroCustoFacade centroCustoFacade;
	
	@EJB
	private ICadastrosBasicosPacienteFacade cadastrosBasicosPacienteFacade;
	
	@Inject
	private ValidaProntuarioFactory validaProntuarioFactory;
	
	@Inject
	private AipEnderecosPacientesDAO aipEnderecosPacientesDAO;

	@Inject
	private AipPacienteProntuarioDAO aipPacienteProntuarioDAO;

	private static final long serialVersionUID = 8485828042929498435L;
	
	private enum MigracaoPacientesONExceptionCode implements BusinessExceptionCode {
		FORMATO_ARQUIVO_INVALIDO_CSV, ERRO_IMPORTAR_ARQUIVO_SISREG, 
		ERRO_INSERIR_CONSULTAS_SISREG, ERRO_CAMPOS_ARQUIVO_MIGRACAO_PACIENTES_1, ERRO_CAMPOS_ARQUIVO_MIGRACAO_PACIENTES_2,
		ERRO_IMPORTAR_ARQUIVO_PACIENTES_MOTIVO, ERRO_MIGRACAO_PACIENTE_CAMPO, ERRO_MIGRACAO_PACIENTE_TAMANHO_NOMES,
		ERRO_MIGRACAO_CAMPO_REQUERIDO, PRONTUARIO_MAIOR_NOVE_MILHOES, ERRO_VALIDA_PRONTUARIO, PRONTUARIO_JA_INCLUIDO,
		ERRO_PERSISTENCIA_MIGRACAO, ERRO_MIGRACAO_CPF_INVALIDO, CARTAO_SAUDE_INVALIDO
	}

	
	public void verificarExtensaoArquivoCSV(final String arquivo) throws ApplicationBusinessException {
		final String nomeArq = ".csv";

		if (!arquivo.endsWith(nomeArq)) {
			throw new ApplicationBusinessException(MigracaoPacientesONExceptionCode.FORMATO_ARQUIVO_INVALIDO_CSV, nomeArq);
		}
	}
	
	
	/**
	 * Carrega arquivo de pacientes
	 * @param caminhoAbsoluto
	 * @return
	 * @throws FileNotFoundException
	 */
	public List<String> carregarArquivoPacientes(String caminhoAbsoluto) throws FileNotFoundException {
		List<String> listaPacientes = new ArrayList<String>(); 
		File f = new File(caminhoAbsoluto);  
        if(!f.exists())  
        {  
        	throw new FileNotFoundException("Não foi possível encontrar o arquivo no caminho: " + caminhoAbsoluto);
        }  
        try {  
            BufferedReader in = new BufferedReader(new FileReader(f));  
            String line;  
            while((line = in.readLine())!=null)  
            {  
            	listaPacientes.add(line);
            }  
      } catch (Exception e) {  
          logError(e);  
      }
		return listaPacientes;
	}
	
	/**
	 * Realiza a importação de cada linha (paciente) do arquivo de migração.
	 * 
	 * @param consulta
	 * @param nomeArquivo
	 * @param numeroLinha
	 * @throws ApplicationBusinessException
	 */
	public void importarArquivo(List<String> listaLinhas, String nomeArquivo,
			String separador, Boolean anularProntuarios, Boolean nomeMaeNaoInformado, Boolean gerarFonemas, Boolean migrarEnderecos,
			Boolean tratarProntuario, String loginUsuarioLogado) 
					throws ApplicationBusinessException {
		
		Integer numeroCampos = 23;
		if (migrarEnderecos){
			numeroCampos = 31;
		}
		
		IPacienteFacade pacienteFacade = this.getPacienteFacade();
		for (int index=0; index <listaLinhas.size(); index++){	
			Integer numeroLinha = index;
			String linhaPaciente = listaLinhas.get(numeroLinha);
			List<String> listaCamposPaciente = new ArrayList<String>();
			String aux = linhaPaciente.replace(separador+separador, separador + " " + separador);
			aux = aux.replace(separador+separador, separador + " " + separador);
			StringTokenizer st = new StringTokenizer(aux, separador);
			while (st.hasMoreTokens()) {
				listaCamposPaciente.add(st.nextToken());
			}
			
			if(listaCamposPaciente.size()<numeroCampos){
				throw new ApplicationBusinessException(MigracaoPacientesONExceptionCode.ERRO_CAMPOS_ARQUIVO_MIGRACAO_PACIENTES_1, nomeArquivo, numeroLinha);
			}
			if(listaCamposPaciente.size()>numeroCampos){
				throw new ApplicationBusinessException(MigracaoPacientesONExceptionCode.ERRO_CAMPOS_ARQUIVO_MIGRACAO_PACIENTES_2, nomeArquivo, numeroLinha);
			}
			AipPacientes paciente = new AipPacientes();
	
			//Popula dados do paciente
			popularPacienteComDadosDoArquivo(paciente, listaCamposPaciente, nomeArquivo, numeroLinha, anularProntuarios, nomeMaeNaoInformado);
			
			//Trata o prontuário do paciente
			if (paciente.getProntuario() != null && !tratarProntuario){
				tratarProntuario(paciente, nomeArquivo, numeroLinha);		
			}
			try {
				//Persiste o paciente
				if (paciente.getProntuario() != null) {
					persistirProntuarioSeNaoCadastrado(paciente, getRegistroColaboradorFacade().obterServidorPorUsuario(loginUsuarioLogado));
				}					
				persistirPaciente(paciente, nomeArquivo, numeroLinha);
				if (migrarEnderecos){
					persistirEndereco(paciente, listaCamposPaciente, nomeArquivo, numeroLinha);
				}
				
				if (gerarFonemas){
					//Refonetiza o paciente
					refonetizarPaciente(paciente);					
				}
				
				listaCamposPaciente.clear();
				//Commitando
				pacienteFacade.commit();
				pacienteFacade.sessionClear();
				
			}
			catch(PersistenceException e) {
				if (e.getCause() instanceof ConstraintViolationException || e.getCause() instanceof NonUniqueObjectException) {
					throw new ApplicationBusinessException(
							MigracaoPacientesONExceptionCode.PRONTUARIO_JA_INCLUIDO,
							paciente.getProntuario(), nomeArquivo, numeroLinha);
				} 
				else{
					throw new ApplicationBusinessException(MigracaoPacientesONExceptionCode.ERRO_PERSISTENCIA_MIGRACAO, nomeArquivo, numeroLinha);
				}
			} 
			catch (JDBCConnectionException e){
				throw new ApplicationBusinessException(MigracaoPacientesONExceptionCode.ERRO_PERSISTENCIA_MIGRACAO, nomeArquivo, numeroLinha);
			}
			catch (HibernateException e){
				throw new ApplicationBusinessException(MigracaoPacientesONExceptionCode.ERRO_PERSISTENCIA_MIGRACAO, nomeArquivo, numeroLinha);
			}
			catch (Exception e) {
				throw new ApplicationBusinessException(MigracaoPacientesONExceptionCode.ERRO_PERSISTENCIA_MIGRACAO, nomeArquivo, numeroLinha);
			} 
		}	
	}

	private void persistirProntuarioSeNaoCadastrado(AipPacientes aipPaciente, RapServidores servidorLogado) {
		AipPacienteProntuario aipPacienteProntuarioJaCadastrado = this.getAipPacienteProntuarioDAO().obterPorChavePrimaria(aipPaciente.getProntuario());
		if (aipPacienteProntuarioJaCadastrado == null) {
			AipPacienteProntuario aipPacienteProntuario = new AipPacienteProntuario();
			aipPacienteProntuario.setProntuario(aipPaciente.getProntuario());
			aipPacienteProntuario.setCriadoEm(new Date());
			aipPacienteProntuario.setServidor(servidorLogado);
			getAipPacienteProntuarioDAO().persistir(aipPacienteProntuario);
		}
	}	
	
	protected AipPacienteProntuarioDAO getAipPacienteProntuarioDAO() {
		return aipPacienteProntuarioDAO;
	}	
	
	/**
	 * Popula e persiste o endereço do paciente quando houver
	 * @param paciente
	 * @param endereco
	 * @param listaCamposPaciente
	 * @param nomeArquivo
	 * @param numeroLinha
	 * @throws ApplicationBusinessException
	 */
	private void persistirEndereco(AipPacientes paciente,
			List<String> listaCamposPaciente,
			String nomeArquivo, Integer numeroLinha) throws ApplicationBusinessException {
		
		ICadastroPacienteFacade cadastroPacienteFacade = getCadastroPacienteFacade();
		AipBairrosCepLogradouro cepCadastrado = null;
		AipEnderecosPacientes endereco = new AipEnderecosPacientes();
		
		//Obtém o cep cadastrado
		String strNumeroCep = listaCamposPaciente.get(15);
		if (!StringUtils.isBlank(strNumeroCep)){
			Integer numeroCep = Integer.parseInt(strNumeroCep);
//			List<AipBairrosCepLogradouro> listaBairrosCepLogradouro = cadastroPacienteFacade.pesquisarCeps(numeroCep, null);
//			if (!listaBairrosCepLogradouro.isEmpty()){
//				cepCadastrado = listaBairrosCepLogradouro.get(0);
//			}
			cepCadastrado = cadastroPacienteFacade.obterBairroCepLogradouroPorCep(numeroCep);
			if (cepCadastrado != null){
				endereco.setAipBairrosCepLogradouro(cepCadastrado);
				
				String strNroLogradouro = listaCamposPaciente.get(16);
				if (!StringUtils.isBlank(strNroLogradouro)){
					Integer nroLogradouro = Integer.parseInt(strNroLogradouro);
					endereco.setNroLogradouro(nroLogradouro);
				}
				
				String strComplementoLogradouro = listaCamposPaciente.get(17);
				if (!StringUtils.isBlank(strComplementoLogradouro)){
					endereco.setComplLogradouro(strComplementoLogradouro);
				}
				
				String strEnderecoCorrespondencia = listaCamposPaciente.get(18);
				if (!StringUtils.isBlank(strEnderecoCorrespondencia)){
					endereco.setIndPadrao(DominioSimNao.valueOf(strEnderecoCorrespondencia));
				}
				else{
					endereco.setIndPadrao(DominioSimNao.N);
				}
				endereco.setTipoEndereco(DominioTipoEndereco.R);
				endereco.setAipPaciente(paciente);
				AipEnderecosPacientesId id = new AipEnderecosPacientesId();
				id.setPacCodigo(paciente.getCodigo());
				Short seqp = 1;
				id.setSeqp(seqp);
				endereco.setId(id);
				getAipEnderecosPacientesDAO().persistir(endereco);
			}
			else{
				//ENDERECO NÃO CADASTRADO COM CEP DO MUNICÍPIO
				persistirEnderecoNaoCadastrado(listaCamposPaciente, endereco, paciente);
			}
		}
		else{
			//ENDERECO NÃO CADASTRADO COM CEP DO MUNICÍPIO
			persistirEnderecoNaoCadastrado(listaCamposPaciente, endereco, paciente);
		}
		
		
	}
	
	/**
	 * Processa a persistência de endereço não cadastrado quando não encontraro cep do município
	 * @param listaCamposPaciente
	 * @param endereco
	 * @param paciente
	 * @throws AGHUNegocioException
	 */
	private void persistirEnderecoNaoCadastrado(List<String> listaCamposPaciente, AipEnderecosPacientes endereco, AipPacientes paciente) throws ApplicationBusinessException {
		String strCodigoMunicipio = listaCamposPaciente.get(24);
		String strCepMunicipio = listaCamposPaciente.get(25);
		String strLogradouro = listaCamposPaciente.get(26);
		if (!StringUtils.isBlank(strCodigoMunicipio) && !StringUtils.isBlank(strCepMunicipio) && !StringUtils.isBlank(strLogradouro)){
			Integer codigoMunicipio = Integer.parseInt(strCodigoMunicipio);
			AipCidades cidade = getCadastrosBasicosPacienteFacade().obterCidade(codigoMunicipio);
			if (cidade != null){
				List<VAipLocalidadeUc> listaVAipLocalidadeUc = getCadastroPacienteFacade().pesquisarCepsPorCidade(strCepMunicipio, codigoMunicipio);
				if (!listaVAipLocalidadeUc.isEmpty()){
					VAipLocalidadeUc vAipLocalidadeUc = listaVAipLocalidadeUc.get(0);
					//Seta os valores
					endereco.setCep(vAipLocalidadeUc.getCep());
					endereco.setAipCidade(cidade);
					endereco.setLogradouro(strLogradouro);
					String strBairro = listaCamposPaciente.get(27);
					if (!StringUtils.isBlank(strBairro)){
						endereco.setBairro(strBairro);
					}
					popularDemaisDadosEndereco(listaCamposPaciente, endereco, paciente);
					getAipEnderecosPacientesDAO().persistir(endereco);
					this.flush();
				}	
			}			
		}
	}
	
	/**
	 * Método que popula os campos número de logradouro, complemento de logradouro,
	 * correspondencia, endereço padrão, tipo endereço
	 * @param listaCamposPaciente
	 * @param endereco
	 * @param paciente
	 */
	private void popularDemaisDadosEndereco(List<String> listaCamposPaciente, AipEnderecosPacientes endereco, AipPacientes paciente){
		String strNroLogradouro = listaCamposPaciente.get(28);
		if (!StringUtils.isBlank(strNroLogradouro)){
			Integer nroLogradouro = Integer.parseInt(strNroLogradouro);
			endereco.setNroLogradouro(nroLogradouro);
		}
		
		String strComplementoLogradouro = listaCamposPaciente.get(29);
		if (!StringUtils.isBlank(strComplementoLogradouro)){
			endereco.setComplLogradouro(strComplementoLogradouro);
		}
		
		String strEnderecoCorrespondencia = listaCamposPaciente.get(30);
		if (!StringUtils.isBlank(strEnderecoCorrespondencia)){
			endereco.setIndPadrao(DominioSimNao.valueOf(strEnderecoCorrespondencia));
		}
		else{
			endereco.setIndPadrao(DominioSimNao.N);
		}
		endereco.setTipoEndereco(DominioTipoEndereco.R);
		endereco.setAipPaciente(paciente);
		AipEnderecosPacientesId id = new AipEnderecosPacientesId();
		id.setPacCodigo(paciente.getCodigo());
		Short seqp = 1;
		id.setSeqp(seqp);
		endereco.setId(id);
	}

	
	/**
	 * Método que persiste o paciente no banco sem passar pelas regras de negócio
	 * do módulo de pacientes
	 */
	public void persistirPaciente(AipPacientes paciente, String nomeArquivo, Integer numeroLinha){
		cadastroPacienteFacade.inserirPacienteMigracao(paciente);
	}
	

	/**
	 * Refonetiza o nome do paciente e da mãe do paciente
	 * @param paciente
	 * @param nomeArquivo
	 * @param numeroLinha
	 * @throws ApplicationBusinessException
	 */
	private void refonetizarPaciente(AipPacientes paciente) throws ApplicationBusinessException {
		cadastroPacienteFacade.salvarFonemas(paciente, paciente.getNome(),AipFonemaPacientes.class);

		if (!StringUtils.isBlank(paciente.getNomeMae())) {
			cadastroPacienteFacade.salvarFonemas(paciente, paciente.getNomeMae(), AipFonemasMaePaciente.class);
		}
		
		if (!StringUtils.isBlank(paciente.getNomeSocial())) {
			cadastroPacienteFacade.salvarFonemas(paciente, paciente.getNomeSocial(), AipFonemaNomeSocialPacientes.class);
		}
	}


	/**
	 * Trata o prontuário do paciente
	 * @param paciente
	 * @param nomeArquivo
	 * @param numeroLinha
	 * @throws ApplicationBusinessException 
	 */
	private void tratarProntuario(AipPacientes paciente, String nomeArquivo, Integer numeroLinha) throws ApplicationBusinessException{
		
		if (paciente.getProntuario() != null && paciente.getProntuario() > 9000000) {
			throw new ApplicationBusinessException(MigracaoPacientesONExceptionCode.PRONTUARIO_MAIOR_NOVE_MILHOES, paciente.getProntuario(), nomeArquivo, numeroLinha);
		}
		
		try{
			//Gera o dígito verificador e concatena ao prontuário informado
			InterfaceValidaProntuario validadorProntuario = validaProntuarioFactory.getValidaProntuario(true);		
			String strValorNovoProntuario = paciente.getProntuario().toString() + validadorProntuario.executaModulo(paciente.getProntuario());
			Integer prontuarioComDigito = Integer.valueOf(strValorNovoProntuario);
			paciente.setProntuario(prontuarioComDigito);
		}
		catch (ValidaProntuarioException e) {
			throw new ApplicationBusinessException(MigracaoPacientesONExceptionCode.ERRO_VALIDA_PRONTUARIO,	nomeArquivo, numeroLinha);
		}
	}


	/**
	 * Popula o objeto paciente com os dados provenientes do arquivo .csv
	 * @param paciente
	 * @param listaCamposPaciente
	 * @param nomeArquivo
	 * @param numeroLinha
	 * @throws ApplicationBusinessException 
	 */
	private void popularPacienteComDadosDoArquivo(AipPacientes paciente,
			List<String> listaCamposPaciente, String nomeArquivo,
			Integer numeroLinha, Boolean anularProntuarios, Boolean nomeMaeNaoInformado) throws ApplicationBusinessException {

		popularProntuarioNomeNomeMae(paciente,listaCamposPaciente,nomeArquivo,numeroLinha, anularProntuarios, nomeMaeNaoInformado);
		
		popularNaturalidadeUfCpf(paciente, listaCamposPaciente, nomeArquivo, numeroLinha);
		
		popularNacionalidadeCorSexo(paciente, listaCamposPaciente, nomeArquivo, numeroLinha);
		
		popularGrauInstrucaoNomePaiEstadoCivil(paciente, listaCamposPaciente, nomeArquivo, numeroLinha);
		
		popularSexoBiologicoObservacao(paciente, listaCamposPaciente, nomeArquivo, numeroLinha);
		
		popularTelefones(paciente, listaCamposPaciente, nomeArquivo, numeroLinha);
		
		popularCartaoSUSRG(paciente, listaCamposPaciente, nomeArquivo, numeroLinha);
		
		populaDemaisDadosPaciente(paciente);
		
	}

	/**
	 * Popula outros dados do paciente
	 * @param paciente
	 */
	private void populaDemaisDadosPaciente(AipPacientes paciente) {
		ICentroCustoFacade centroCustoFacade = getCentroCustoFacade();
		IRegistroColaboradorFacade registroColaboradorFacade = getRegistroColaboradorFacade();
		Date dataAtual = new Date();
		
		//Popula o Centro de custo com o de código 1 (padrão nas implantações)
		FccCentroCustos centroCusto = centroCustoFacade.obterCentroCustoPorChavePrimaria(1);
		paciente.setFccCentroCustosCadastro(centroCusto);
		
		//Popula o Servidor de cadastro com o padrão AGHU
		RapServidoresId id = new RapServidoresId();
		id.setMatricula(9999999);
		Integer vinculo = 955;
		Short vinCodigo = vinculo.shortValue();
		id.setVinCodigo(vinCodigo);
		RapServidores servidor = registroColaboradorFacade.obterRapServidor(id);
		paciente.setRapServidoresCadastro(servidor);
		
		//Popula a data de identificação
		paciente.setDtIdentificacao(dataAtual);
		
		//Popula o indPacienteVip
		paciente.setIndPacienteVip(DominioSimNao.N);
		
		//Popula o criadoEm
		paciente.setCriadoEm(dataAtual);		
		
		//Popula o cadConfirmado (como N porque só muda para S quando paciente apresentar documentos)
		paciente.setCadConfirmado(DominioSimNao.N);
		
		//Popula os campos indGeraProntuario e prntAtivo
		if (paciente.getProntuario() != null){
			paciente.setIndGeraProntuario(DominioSimNao.S);
			paciente.setPrntAtivo(DominioTipoProntuario.A);
		}
		else{
			paciente.setIndGeraProntuario(DominioSimNao.N);
		}
	}

	
	/**
	 * Popula os dados do cartão SUS e RG
	 * @param paciente
	 * @param listaCamposPaciente
	 * @param nomeArquivo
	 * @param numeroLinha
	 */
	private void popularCartaoSUSRG(AipPacientes paciente,
			List<String> listaCamposPaciente, String nomeArquivo,
			Integer numeroLinha) throws ApplicationBusinessException{
		
		//Número do cartão SUS
		String strNroCartaoSUS = listaCamposPaciente.get(19);
		if (!StringUtils.isBlank(strNroCartaoSUS)){
			try{
				BigInteger nroCartaoSUS = new BigInteger(strNroCartaoSUS);
				if (!validarNumeroCartaoSaude(nroCartaoSUS)){
					throw new ApplicationBusinessException(
							MigracaoPacientesONExceptionCode.CARTAO_SAUDE_INVALIDO, nomeArquivo, numeroLinha);
				}
				paciente.setNroCartaoSaude(nroCartaoSUS);
			}
			catch(Exception e){
				throw new ApplicationBusinessException(
						MigracaoPacientesONExceptionCode.ERRO_MIGRACAO_PACIENTE_CAMPO,
						"Número do Cartão Saúde", nomeArquivo, numeroLinha);
			}
			
		}
		
		
		//Número do RG
		String strRG = listaCamposPaciente.get(20);
		if (!StringUtils.isBlank(strRG) && strRG.length() <= 20){
			try{
				paciente.setRg(strRG);
			}
			catch(Exception e){
				throw new ApplicationBusinessException(
						MigracaoPacientesONExceptionCode.ERRO_MIGRACAO_PACIENTE_CAMPO,
						"RG", nomeArquivo, numeroLinha);
			}
			
		}
		
		//Id Sistema Legado
		String strIdSistemaLegado = listaCamposPaciente.get(21);
		if (!StringUtils.isBlank(strIdSistemaLegado)){
			try{
				Long idSistemaLegado = Long.valueOf(strIdSistemaLegado);
				paciente.setIdSistemaLegado(idSistemaLegado);
			}
			catch(Exception e){
				throw new ApplicationBusinessException(
						MigracaoPacientesONExceptionCode.ERRO_MIGRACAO_PACIENTE_CAMPO,
						"Id Sistema Legado", nomeArquivo, numeroLinha);
			}
			
		}
		
	}

	/**
	 * Popula os dados dos telefones do paciente
	 * @param paciente
	 * @param listaCamposPaciente
	 * @param nomeArquivo
	 * @param numeroLinha
	 */
	private void popularTelefones(AipPacientes paciente,
			List<String> listaCamposPaciente, String nomeArquivo,
			Integer numeroLinha) throws ApplicationBusinessException {
		
		//DDD Telefone residencial
		String strDddTelRes = listaCamposPaciente.get(15);
		if (!StringUtils.isBlank(strDddTelRes) && strDddTelRes.length() <= 4){
			try{
				Short dddTelRes = Short.valueOf(strDddTelRes);
				paciente.setDddFoneResidencial(dddTelRes);
			}
			catch(Exception e){
				throw new ApplicationBusinessException(
						MigracaoPacientesONExceptionCode.ERRO_MIGRACAO_PACIENTE_CAMPO,
						"ddd telefone residencial", nomeArquivo, numeroLinha);
			}	
		}
		
		//Número Telefone residencial
		String strNumeroTelRes = listaCamposPaciente.get(16);
		if (!StringUtils.isBlank(strNumeroTelRes) && strNumeroTelRes.length() <= 10){
			try{
				Long numeroTelRes = Long.valueOf(strNumeroTelRes);
				paciente.setFoneResidencial(numeroTelRes);
			}
			catch(Exception e){
				throw new ApplicationBusinessException(
						MigracaoPacientesONExceptionCode.ERRO_MIGRACAO_PACIENTE_CAMPO,
						"número telefone residencial", nomeArquivo, numeroLinha);
			}
			
		}
		
		//DDD Telefone recado
		String strDddTelRecado = listaCamposPaciente.get(17);
		if (!StringUtils.isBlank(strDddTelRecado) && strDddTelRecado.length() <= 4){
			try{
				Short dddTelRecado = Short.valueOf(strDddTelRecado);
				paciente.setDddFoneRecado(dddTelRecado);
			}
			catch(Exception e){
				throw new ApplicationBusinessException(
						MigracaoPacientesONExceptionCode.ERRO_MIGRACAO_PACIENTE_CAMPO,
						"ddd telefone recado", nomeArquivo, numeroLinha);
			}	
		}
		
		//Número Telefone recado
		String strNumeroTelRecado = listaCamposPaciente.get(18);
		if (!StringUtils.isBlank(strNumeroTelRecado) && strNumeroTelRecado.length() <= 15){
			try{
				paciente.setFoneRecado(strNumeroTelRecado);
			}
			catch(Exception e){
				throw new ApplicationBusinessException(
						MigracaoPacientesONExceptionCode.ERRO_MIGRACAO_PACIENTE_CAMPO,
						"número telefone recado", nomeArquivo, numeroLinha);
			}
			
		}
		
	}

	/**
	 * Popula os campos sexo biológico e observação
	 * @param paciente
	 * @param listaCamposPaciente
	 * @param nomeArquivo
	 * @param numeroLinha
	 * @throws ApplicationBusinessException 
	 */
	private void popularSexoBiologicoObservacao(AipPacientes paciente,
			List<String> listaCamposPaciente, String nomeArquivo,
			Integer numeroLinha) throws ApplicationBusinessException {
		
		//Obtém o sexo biológico
		String strSexoBiologico = listaCamposPaciente.get(13);
		if (!StringUtils.isBlank(strSexoBiologico)){
			try{
				DominioSexo sexoBiologico = DominioSexo.getInstance(strSexoBiologico);
				if (sexoBiologico == null){
					throw new ApplicationBusinessException(
							MigracaoPacientesONExceptionCode.ERRO_MIGRACAO_PACIENTE_CAMPO,
							"sexo biologico", nomeArquivo, numeroLinha);
				}
				paciente.setSexoBiologico(sexoBiologico);
			}
			catch(Exception e){
				throw new ApplicationBusinessException(
						MigracaoPacientesONExceptionCode.ERRO_MIGRACAO_PACIENTE_CAMPO,
						"sexo biologico", nomeArquivo, numeroLinha);
			}
		}

		//Obtém a observação
		String observacao = listaCamposPaciente.get(14);
		if (!StringUtils.isBlank(observacao)){
			paciente.setObservacao(observacao);
		}
		
	}


	/**
	 * Popula os campos grau de instrução, nome do pai e estado civil
	 * @param paciente
	 * @param listaCamposPaciente
	 * @param nomeArquivo
	 * @param numeroLinha
	 * @throws ApplicationBusinessException 
	 */
	private void popularGrauInstrucaoNomePaiEstadoCivil(AipPacientes paciente,
			List<String> listaCamposPaciente, String nomeArquivo,
			Integer numeroLinha) throws ApplicationBusinessException {
		
		//Obtem o Grau de Instruçao
		String strGrauInstrucao = listaCamposPaciente.get(10);
		if (!StringUtils.isBlank(strGrauInstrucao)){
			try{
				Integer codGrauInstrucao = Integer.valueOf(strGrauInstrucao);
				DominioGrauInstrucao grauInstrucao = obterGrauInstrucao(codGrauInstrucao);
				if (grauInstrucao == null){
					throw new ApplicationBusinessException(
							MigracaoPacientesONExceptionCode.ERRO_MIGRACAO_PACIENTE_CAMPO,
							"grau de instrução", nomeArquivo, numeroLinha);
				}
				paciente.setGrauInstrucao(grauInstrucao);
			}
			catch(Exception e){
				throw new ApplicationBusinessException(
						MigracaoPacientesONExceptionCode.ERRO_MIGRACAO_PACIENTE_CAMPO,
						"grau de instrução", nomeArquivo, numeroLinha);
			}
			
		}
		
		//Obtém o nome o pai (CAMPO REQUERIDO)
		String nomePai = listaCamposPaciente.get(11);
		nomePai = retirarEspacosExcedentesNomes(nomePai).trim();
		if (nomePai.length() > 50){
			throw new ApplicationBusinessException(MigracaoPacientesONExceptionCode.ERRO_MIGRACAO_PACIENTE_TAMANHO_NOMES, "nomePai", nomeArquivo, numeroLinha);
		}
		if (!StringUtils.isBlank(nomePai)){
			paciente.setNomePai(nomePai.toUpperCase());
		}

		//Obtem o Estado Civil
		String strEstadoCivil = listaCamposPaciente.get(12);
		if (!StringUtils.isBlank(strEstadoCivil)){
			try{
				DominioEstadoCivil estadoCivil = DominioEstadoCivil.valueOf(strEstadoCivil);
				if (estadoCivil == null){
					throw new ApplicationBusinessException(
							MigracaoPacientesONExceptionCode.ERRO_MIGRACAO_PACIENTE_CAMPO,
							"estado civil", nomeArquivo, numeroLinha);
				}
				paciente.setEstadoCivil(estadoCivil);
			}
			catch(Exception e){
				throw new ApplicationBusinessException(
						MigracaoPacientesONExceptionCode.ERRO_MIGRACAO_PACIENTE_CAMPO,
						"estado civil", nomeArquivo, numeroLinha);
			}
			
		}
		
	}


	/**
	 * Obtém o domínio Grau Instrução pelo código
	 * @param codGrauInstrucao
	 * @param nomeArquivo
	 * @param numeroLinha
	 * @return
	 */
	private DominioGrauInstrucao obterGrauInstrucao(Integer codGrauInstrucao) {
		DominioGrauInstrucao retorno = null;
		
		for (DominioGrauInstrucao grau: DominioGrauInstrucao.values()){
			if (codGrauInstrucao.equals(grau.getCodigo())){
				retorno = grau;
				break;
			}
		}
		return retorno;
	}


	/**
	 * Popula os campos nacionalidade, cor e sexo
	 * @param paciente
	 * @param listaCamposPaciente
	 * @param nomeArquivo
	 * @param numeroLinha
	 * @throws ApplicationBusinessException
	 */
	private void popularNacionalidadeCorSexo(AipPacientes paciente,
			List<String> listaCamposPaciente, String nomeArquivo,
			Integer numeroLinha) throws ApplicationBusinessException {
		IPacienteFacade pacienteFacade = getPacienteFacade();
		//Obtém a nacionalidade (CAMPO REQUERIDO)
		String strNacCodigo = listaCamposPaciente.get(7);
		if (!StringUtils.isBlank(strNacCodigo)){
			try{
				Integer nacCodigo = Integer.valueOf(strNacCodigo);
				AipNacionalidades nacionalidade = pacienteFacade.obterNacionalidadePorCodigoAtiva(nacCodigo);
				if (nacionalidade == null){
					throw new ApplicationBusinessException(
							MigracaoPacientesONExceptionCode.ERRO_MIGRACAO_PACIENTE_CAMPO,
							"nacionalidade (nac_codigo)", nomeArquivo, numeroLinha);
				}
				paciente.setAipNacionalidades(nacionalidade);
			}
			catch(Exception e){
				throw new ApplicationBusinessException(
						MigracaoPacientesONExceptionCode.ERRO_MIGRACAO_PACIENTE_CAMPO,
						"nacionalidade (nac_codigo)", nomeArquivo, numeroLinha);
			}
		}
		
		//Obtém a cor
		String strCor = listaCamposPaciente.get(8);
		if (!StringUtils.isBlank(strCor)){
			try{
				DominioCor cor = DominioCor.getInstance(strCor);
				if (cor == null){
					throw new ApplicationBusinessException(
							MigracaoPacientesONExceptionCode.ERRO_MIGRACAO_PACIENTE_CAMPO,
							"cor", nomeArquivo, numeroLinha);
				}
				paciente.setCor(cor);
			}
			catch(Exception e){
				throw new ApplicationBusinessException(
						MigracaoPacientesONExceptionCode.ERRO_MIGRACAO_PACIENTE_CAMPO,
						"cor", nomeArquivo, numeroLinha);
			}
		}
		
		//Obtém o sexo
		String strSexo = listaCamposPaciente.get(9);
		if (!StringUtils.isBlank(strSexo)){
			try{
				DominioSexo sexo = DominioSexo.getInstance(strSexo);
				if (sexo == null){
					throw new ApplicationBusinessException(
							MigracaoPacientesONExceptionCode.ERRO_MIGRACAO_PACIENTE_CAMPO,
							"sexo", nomeArquivo, numeroLinha);
				}
				paciente.setSexo(sexo);
			}
			catch(Exception e){
				throw new ApplicationBusinessException(
						MigracaoPacientesONExceptionCode.ERRO_MIGRACAO_PACIENTE_CAMPO,
						"sexo", nomeArquivo, numeroLinha);
			}
		}
		
	}


	/**
	 * Popula os dados Naturalidade, UF e CPF
	 * @param paciente
	 * @param listaCamposPaciente
	 * @param nomeArquivo
	 * @param numeroLinha
	 * @throws ApplicationBusinessException
	 */
	private void popularNaturalidadeUfCpf(AipPacientes paciente,
			List<String> listaCamposPaciente, String nomeArquivo,
			Integer numeroLinha) throws ApplicationBusinessException {
		
		IPacienteFacade pacienteFacade = getPacienteFacade();
		
		//Obtém a naturalidade
		String strCddCodigo = listaCamposPaciente.get(4);
		if (!StringUtils.isBlank(strCddCodigo)){
			try{
				Integer cddCodigo = Integer.valueOf(strCddCodigo);
				AipCidades cidade = pacienteFacade.obterCidadePorChavePrimaria(cddCodigo);
				//Caso não encontre a cidade pelo código na base do AGHU não popula este campo
				if (cidade != null){
					paciente.setAipCidades(cidade);
					paciente.setAipUfs(cidade.getAipUf());
				}
			}
			catch(Exception e){
				throw new ApplicationBusinessException(
						MigracaoPacientesONExceptionCode.ERRO_MIGRACAO_PACIENTE_CAMPO,
						"naturalidade (cdd_codigo)", nomeArquivo, numeroLinha);
			}
		}
		
		//Obtém o cpf
		String strCpf = listaCamposPaciente.get(6);
		if (!StringUtils.isBlank(strCpf)){	
			if (!CoreUtil.validarCPF(strCpf)){
				throw new ApplicationBusinessException(
						MigracaoPacientesONExceptionCode.ERRO_MIGRACAO_CPF_INVALIDO, nomeArquivo, numeroLinha);
			}
			try{
				Long cpf = Long.valueOf(strCpf);
				if (cpf == null){
					throw new ApplicationBusinessException(
							MigracaoPacientesONExceptionCode.ERRO_MIGRACAO_PACIENTE_CAMPO,
							"CPF", nomeArquivo, numeroLinha);
				}
				paciente.setCpf(cpf);	
			}catch (Exception e){
				throw new ApplicationBusinessException(
						MigracaoPacientesONExceptionCode.ERRO_MIGRACAO_PACIENTE_CAMPO,
						"CPF", nomeArquivo, numeroLinha);
			}
			
		}
		
	}


	/**
	 * Popula o prontuário, o nome e o nome da mãe
	 * @param paciente
	 * @param listaCamposPaciente
	 * @param nomeArquivo
	 * @param numeroLinha
	 * @throws ApplicationBusinessException
	 */
	private void popularProntuarioNomeNomeMae(AipPacientes paciente,
			List<String> listaCamposPaciente, String nomeArquivo,
			Integer numeroLinha, Boolean anularProntuarios, Boolean nomeMaeNaoInformado) throws ApplicationBusinessException {
		
		//Obtém o prontuário
		String strProntuario = listaCamposPaciente.get(0);
		if (!StringUtils.isBlank(strProntuario)){
			Integer prontuario = Integer.parseInt(strProntuario);
			if (prontuario != null && prontuario > 8999999 && anularProntuarios){
				prontuario = null;
			}
			paciente.setProntuario(prontuario);
		}
		//Obtém o nome (CAMPO REQUERIDO)
		String nome = listaCamposPaciente.get(1);
		if (StringUtils.isBlank(nome)){
			throw new ApplicationBusinessException(MigracaoPacientesONExceptionCode.ERRO_MIGRACAO_CAMPO_REQUERIDO, "nome", nomeArquivo, numeroLinha);
		}
		nome = retirarEspacosExcedentesNomes(nome).trim();
		if (nome.length() > 50){
			throw new ApplicationBusinessException(MigracaoPacientesONExceptionCode.ERRO_MIGRACAO_PACIENTE_TAMANHO_NOMES, "nome", nomeArquivo, numeroLinha);
		}
		paciente.setNome(nome.toUpperCase());
		//Obtém o nome da mãe (CAMPO REQUERIDO)
		String nomeMae = listaCamposPaciente.get(2);
		if (StringUtils.isBlank(nomeMae)){
			if (nomeMaeNaoInformado){
				nomeMae = "NÃO INFORMADO";
			}
			else{
				throw new ApplicationBusinessException(MigracaoPacientesONExceptionCode.ERRO_MIGRACAO_CAMPO_REQUERIDO, "nome da mae", nomeArquivo, numeroLinha);				
			}
		}
		nomeMae = retirarEspacosExcedentesNomes(nomeMae).trim();
		if (nomeMae.length() > 50){
			throw new ApplicationBusinessException(MigracaoPacientesONExceptionCode.ERRO_MIGRACAO_PACIENTE_TAMANHO_NOMES, "nomeMae", nomeArquivo, numeroLinha);
		}
		paciente.setNomeMae(nomeMae.toUpperCase());
		//Obtém a nada de nascimento (CAMPO REQUERIDO)
		String strDataNascimento = listaCamposPaciente.get(3);
		if (StringUtils.isBlank(strDataNascimento)){
			throw new ApplicationBusinessException(MigracaoPacientesONExceptionCode.ERRO_MIGRACAO_CAMPO_REQUERIDO, "data de nascimento", nomeArquivo, numeroLinha);
		}
		try{
			final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date dataNascimento = (Date)df.parse(strDataNascimento);
			paciente.setDtNascimento(dataNascimento);
		}catch(Exception e){
			throw new ApplicationBusinessException(MigracaoPacientesONExceptionCode.ERRO_MIGRACAO_PACIENTE_CAMPO, "data de nascimento", nomeArquivo, numeroLinha);
		}
		
	}


	public void tratarErrosImportacaoPacientes(String nomeArquivo, StringBuilder msgErroProcedimento) 
			throws ApplicationBusinessException{
		if (msgErroProcedimento.length() > 0) {
			throw new ApplicationBusinessException(
					MigracaoPacientesONExceptionCode.ERRO_IMPORTAR_ARQUIVO_PACIENTES_MOTIVO, 
					nomeArquivo, msgErroProcedimento);
		}
	}

	
	/**
	 * Método para retirar os espaços excedentes do meio dos nomes
	 * @param nome
	 * @return
	 */
	private String retirarEspacosExcedentesNomes(String nome){ 
	    nome = nome.replaceAll("\\s+"," ");  
	    return nome;
	}
	
	protected ICadastrosBasicosPacienteFacade getCadastrosBasicosPacienteFacade() {
		return cadastrosBasicosPacienteFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}
	
	protected ICadastroPacienteFacade getCadastroPacienteFacade() {
		return cadastroPacienteFacade;
	}
	
	protected ICentroCustoFacade getCentroCustoFacade() {
		return centroCustoFacade;
	}
	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}

	protected AipEnderecosPacientesDAO getAipEnderecosPacientesDAO() {
		return aipEnderecosPacientesDAO;
	}
	
	
	/**
	 * Valida o número do cartão SUS informado
	 * @param numeroCartao
	 * @return
	 */
	public Boolean validarNumeroCartaoSaude(BigInteger numeroCartao)
			throws ApplicationBusinessException {
		boolean passou = false;

		String valor = String.valueOf(numeroCartao);
		if (valor.length() > 15) {
			passou = false;
		} else {
			StringBuffer valorZero = new StringBuffer(valor);
			// Adiciona espaços no início
			for (int i = valor.length(); i < 15; i++) {
				valorZero.insert(0, "0");
			}
			int somat = 0;
			int cont = 15;
			if (valorZero.charAt(0) == '7' || valorZero.charAt(0) == '8' || valorZero.charAt(0) == '9') {
				for (int i = 0; i < 15; i++) {
					String caracter = String.valueOf(valorZero.charAt(i));
					somat += (Integer.valueOf(caracter) * cont);
					cont--;
				}
				if (somat % 11 == 0) {
					passou = true;
				} else {
					passou = false;
				}
			} else {
				String valor11 = valorZero.substring(0, 11);
				for (int i = 0; i < 11; i++) {
					String caracter = String.valueOf(valor11.charAt(i));
					somat += (Integer.valueOf(caracter) * cont);
					cont--;
				}
				int resto = somat % 11;
				int dv = 11 - resto;
				if (dv == 11) {
					dv = 0;
				}
				StringBuffer resultado = new StringBuffer();
				// Retira os espaços do início
				String valor11SemZeros = valor11;
				for (int i = 0; i < valor11.length(); i++) {
					if (valor11.charAt(i) == '0') {
						valor11SemZeros = valor11.substring(i + 1);
					} else {
						break;
					}
				}
				if (dv == 10) {
					somat += 2;
					resto = somat % 11;
					dv = 11 - resto;

					resultado.append(valor11SemZeros).append("001")
							.append(Integer.toString(dv));
				} else {
					resultado.append(valor11SemZeros).append("000")
							.append(Integer.toString(dv));
				}
				if (new BigInteger(resultado.toString()).equals(numeroCartao)) {
					passou = true;
				}
			}
		}
		
		return passou;

	}
}
