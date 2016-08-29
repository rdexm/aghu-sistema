package br.gov.mec.aghu.paciente.cadastro.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.NoResultException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.dominio.DominioLadoEndereco;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoEndereco;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AipBairrosCepLogradouro;
import br.gov.mec.aghu.model.AipBairrosCepLogradouroId;
import br.gov.mec.aghu.model.AipCaixaPostalComunitarias;
import br.gov.mec.aghu.model.AipCepLogradouros;
import br.gov.mec.aghu.model.AipCidades;
import br.gov.mec.aghu.model.AipEnderecosPacientes;
import br.gov.mec.aghu.model.AipEnderecosPacientesId;
import br.gov.mec.aghu.model.AipEnderecosPacientesJn;
import br.gov.mec.aghu.model.AipGrandesUsuarios;
import br.gov.mec.aghu.model.AipLogradouros;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.AipPacientesHist;
import br.gov.mec.aghu.model.AipTipoLogradouros;
import br.gov.mec.aghu.model.AipUfs;
import br.gov.mec.aghu.model.AipUnidadeOperacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAipCeps;
import br.gov.mec.aghu.model.VAipEnderecoPaciente;
import br.gov.mec.aghu.model.VAipLocalidadeUc;
import br.gov.mec.aghu.paciente.cadastrosbasicos.business.ICadastrosBasicosPacienteFacade;
import br.gov.mec.aghu.paciente.dao.AipBairrosCepLogradouroDAO;
import br.gov.mec.aghu.paciente.dao.AipCidadesDAO;
import br.gov.mec.aghu.paciente.dao.AipEnderecosPacientesDAO;
import br.gov.mec.aghu.paciente.dao.AipEnderecosPacientesJnDAO;
import br.gov.mec.aghu.paciente.dao.AipLogradourosDAO;
import br.gov.mec.aghu.paciente.dao.AipPacientesDAO;
import br.gov.mec.aghu.paciente.dao.AipPacientesHistDAO;
import br.gov.mec.aghu.paciente.dao.AipTipoLogradourosDAO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

/**
 * classe de negócio do módulo de cadastros gerais responsável pelos métodos
 * negociais relativos as classes relacionadas a endereço, tais como cidade,
 * logradouro, UF, etc...
 * 
 * @author gmneto
 * 
 */
@SuppressWarnings({ "PMD.AghuTooManyMethods", "PMD.ExcessiveClassLength" })
@Stateless
public class EnderecoON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(EnderecoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private ICascaFacade cascaFacade;
	
	@Inject
	private AipCidadesDAO aipCidadesDAO;
	
	@Inject
	private AipEnderecosPacientesJnDAO aipEnderecosPacientesJnDAO;
	
	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@Inject
	private AipTipoLogradourosDAO aipTipoLogradourosDAO;
	
	@Inject
	private AipPacientesDAO aipPacientesDAO;
	
	@Inject
	private AipBairrosCepLogradouroDAO aipBairrosCepLogradouroDAO;
	
	@Inject
	private AipEnderecosPacientesDAO aipEnderecosPacientesDAO;
	
	@EJB
	private ICadastrosBasicosPacienteFacade cadastrosBasicosPacienteFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private AipPacientesHistDAO aipPacientesHistDAO;
	
	@Inject
	private AipLogradourosDAO aipLogradourosDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1426311931565366852L;

	private enum EnderecoONExceptionCode implements BusinessExceptionCode {
		CIDADE_OBRIGATORIO, CIDADE_CADASTRADA_E_NAO_CADASTRADA, CEP_JA_CADASTRADO, CEP_OBRIGATORIO, AIP_LOGRADOURO_NAO_INFORMADO, AIP_NUMERO_NAO_INFORMADO, AIP_TIPO_ENDERECO_NAO_INFORMADO, AIP_QUANTIDADE_ENDERECO_RESIDENCIAL, AIP_ENDERECO_NAO_INFORMADO, CIDADE_JA_CADASTRADA, NUMERO_LOGRADOURO_LADO_INCORRETO, LOGRADOURO_OBRIGATORIO, NUMERO_LOGRADOURO_OBRIGATORIO, TIPO_ENDERECO_OBRIGATORIO, AIP_00208, NUMERO_LOGRADOURO_ACIMA_FAIXA_CEP, NUMERO_LOGRADOURO_ABAIXO_FAIXA_CEP, CEP_NAO_CADASTRADO, AIP_00221, AIP_00234, MENSAGEM_ERRO_CEP_INATIVO, BAIRRO_OBRIGATORIO
	}

	/**
	 * Método que lista cidade pelo nome, recebendo parâmetro na forma de
	 * Object. Usado na interface pelo sugestionbox.
	 * 
	 * @param nome
	 * @return
	 */
	public List<AipCidades> pesquisarCidadePorNome(Object nome) {
		return this.getCadastrosBasicosPacienteFacade().pesquisarPorCodigoNome(
				nome, true);
	}

	/**
	 * Método que valida se uma cidade não cadastrada informada pelo usuário já
	 * não existe na base de dados.
	 * 
	 * TODO Avaliar a utilizacao de uma pesquisa fonetica para realizar esta
	 * validacao
	 * 
	 * @param nomeCidade
	 * @throws ApplicationBusinessException
	 */
	public void validarCidade(String nomeCidade) throws ApplicationBusinessException {
		if (nomeCidadeJaExistente(nomeCidade)) {
			throw new ApplicationBusinessException(
					EnderecoONExceptionCode.CIDADE_JA_CADASTRADA);
		}
	}

	public Boolean nomeCidadeJaExistente(String nomeCidade) {
		List<AipCidades> li = this.getAipCidadesDAO().pesquisarCidadesPorNome(
				nomeCidade);

		return !li.isEmpty();
	}
	
	/**
	 * Valida se o cep informado está dentro da numeração estipulada no
	 * logradouro. Reflete a program unit AIPP_VALIDA_NUMERACAO na
	 * AIPF_CAD_PAC.PLL
	 * 
	 * 
	 * 
	 * @param endereco
	 * @throws ApplicationBusinessException
	 */
	private void validarNumeracaoEndereco(AipEnderecosPacientes endereco)
			throws ApplicationBusinessException {
		AipCepLogradouros cepLogradouro = endereco.getAipBairrosCepLogradouro()
				.getCepLogradouro();
		String numInicial = cepLogradouro.getNroInicial();
		String numFinal = cepLogradouro.getNroFinal();
		DominioLadoEndereco lado = cepLogradouro.getLado();
		Integer numLogradouro = endereco.getNroLogradouro();

		if(!StringUtils.isEmpty(numInicial) && numInicial.indexOf(',') == -1){

			if(!StringUtils.isEmpty(numInicial) && numFinal.indexOf(',') == -1){
			
				avaliaSeNroLogradouroMaior(numLogradouro, numInicial.split(","));
				
				avaliaSeNroLogradouroMenor(numLogradouro, numFinal.split(","));
		
				if (lado != null) {
					switch (lado) {
					case I:
						if (endereco.getNroLogradouro() % 2 == 0) {
							throw new ApplicationBusinessException(
									EnderecoONExceptionCode.NUMERO_LOGRADOURO_LADO_INCORRETO);
						}
						break;
					case P:
						if (endereco.getNroLogradouro() % 2 != 0) {
							throw new ApplicationBusinessException(
									EnderecoONExceptionCode.NUMERO_LOGRADOURO_LADO_INCORRETO);
						}
						break;
		
					}
				}
			}
		}

	}

	private void avaliaSeNroLogradouroMenor(Integer nroReferencia, String[] numerosLogradouro) throws ApplicationBusinessException {
		this.avaliaSeNroLogradouroMaiorMenor(nroReferencia, numerosLogradouro, false);
	}
	
	private void avaliaSeNroLogradouroMaior(Integer nroReferencia, String[] numerosLogradouro) throws ApplicationBusinessException {
		this.avaliaSeNroLogradouroMaiorMenor(nroReferencia, numerosLogradouro, true);
	}
	
	private void avaliaSeNroLogradouroMaiorMenor(Integer nroReferencia, String[] numerosLogradouro, boolean consistirParaSerMaiorQue) throws ApplicationBusinessException {
		Integer nro = -1;
		for (int i = 0; i < numerosLogradouro.length; i++) {
			nro = Integer.valueOf(numerosLogradouro[i].trim());
			if (consistirParaSerMaiorQue) {
				if (nroReferencia < nro) {
					throw new ApplicationBusinessException(
							EnderecoONExceptionCode.NUMERO_LOGRADOURO_ABAIXO_FAIXA_CEP, nro);
				}
			} else {
				if (nroReferencia > nro) {
					throw new ApplicationBusinessException(
							EnderecoONExceptionCode.NUMERO_LOGRADOURO_ACIMA_FAIXA_CEP, nro);
				}				
			}
		}
	}	
	
	public List<AipBairrosCepLogradouro> pesquisarCeps(Integer cep,
			Integer codigoCidade) throws ApplicationBusinessException {
		return this.getAipBairrosCepLogradouroDAO().pesquisarCeps(cep,
				codigoCidade);
	}

	public Integer obterCountCeps(Integer cep, Integer codigoCidade)
			throws ApplicationBusinessException {
		return this.getAipBairrosCepLogradouroDAO().obterCountCeps(cep,
				codigoCidade);
	}

	public List<AipBairrosCepLogradouro> pesquisarCepExato(Integer cep)
			throws ApplicationBusinessException {
		return this.getAipBairrosCepLogradouroDAO().pesquisarCepExato(cep);
	}

	/**
	 * Valida o CEP e o logradouro. Reflete a program unit AIPP_VALIDA_ENDERECO
	 * na AIPF_CAD_PAC.PLL
	 * 
	 * @param enderecoPaciente
	 * @param validarMovimentacaoMunicipio
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public void validarEndereco(AipEnderecosPacientes enderecoPaciente, Boolean validarMovimentacaoMunicipio)
			throws ApplicationBusinessException {

		if (enderecoPaciente.getvAipCep() != null && !DominioSituacao.A.equals(enderecoPaciente.getvAipCep().getIndSituacao())){
			String cep = enderecoPaciente.getvAipCep() != null ? enderecoPaciente.getvAipCep().getCepFormatado() : "";
			throw new ApplicationBusinessException(
					EnderecoONExceptionCode.MENSAGEM_ERRO_CEP_INATIVO, cep);
		}
		if (enderecoPaciente.getAipBairrosCepLogradouro() == null
				&& enderecoPaciente.getAipLogradouro() == null
				&& StringUtils.isBlank(enderecoPaciente.getLogradouro())) {
			throw new ApplicationBusinessException(
					EnderecoONExceptionCode.LOGRADOURO_OBRIGATORIO);

		}

		if (enderecoPaciente.getNroLogradouro() == null) {
			throw new ApplicationBusinessException(
					EnderecoONExceptionCode.NUMERO_LOGRADOURO_OBRIGATORIO);
		}

		if (enderecoPaciente.getTipoEndereco() == null) {
			throw new ApplicationBusinessException(
					EnderecoONExceptionCode.TIPO_ENDERECO_OBRIGATORIO);
		}

		if (enderecoPaciente.getAipBairrosCepLogradouro() == null
				&& enderecoPaciente.getAipCidade() == null
				&& StringUtils.isBlank(enderecoPaciente.getCidade())) {
			throw new ApplicationBusinessException(
					EnderecoONExceptionCode.CIDADE_OBRIGATORIO);
		}

		if (Boolean.TRUE.equals(validarMovimentacaoMunicipio) 
				&& enderecoPaciente.getAipBairrosCepLogradouro() == null
				&& enderecoPaciente.getAipCidade() == null) {
			validarCidade(enderecoPaciente.getCidade());
		}

		if (enderecoPaciente.getAipCidade() != null
				&& !StringUtils.isBlank(enderecoPaciente.getCidade())) {
			throw new ApplicationBusinessException(
					EnderecoONExceptionCode.CIDADE_CADASTRADA_E_NAO_CADASTRADA);
		}

		if (enderecoPaciente.getAipBairrosCepLogradouro() != null
				&& enderecoPaciente.getAipBairrosCepLogradouro().getCepLogradouro() != null) {
			this.validarNumeracaoEndereco(enderecoPaciente);
		} else {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

			if (!this.getICascaFacade().usuarioTemPermissao(
					servidorLogado.getUsuario(),
					"gerarExamesIntegracao", "inserir")) {

				if (enderecoPaciente.getCep() == null
						&& !this.getICascaFacade().usuarioTemPermissao(
								servidorLogado.getUsuario(),
								"enderecoPaciente", "validar")) {
					throw new ApplicationBusinessException(
							EnderecoONExceptionCode.CEP_OBRIGATORIO);
				}
				
				if (enderecoPaciente.getCep() == null
						&& enderecoPaciente.getAipCidade() != null
						&& enderecoPaciente.getAipBairrosCepLogradouro() != null) {
					if (this.pesquisarCepsPorCidade(
							String.valueOf(enderecoPaciente
									.getAipBairrosCepLogradouro()),
							enderecoPaciente.getAipCidade().getCodigo())
							.isEmpty()) {
						throw new ApplicationBusinessException(
								EnderecoONExceptionCode.CEP_NAO_CADASTRADO);
					}
				}

			}

		}
	}

	/**
	 * valida o conjunto de endereços do paciente.
	 * 
	 * @param enderecos
	 * @param validarMovimentacaoMunicipio
	 * @param indGeraProntuario
	 * @throws ApplicationBusinessException
	 */
	public void validarEnderecosPaciente(Set<AipEnderecosPacientes> enderecos, Boolean validarMovimentacaoMunicipio)
			throws BaseException {
		this.validarQuantidadeEnderecoResidencial(enderecos);

		this.validarQuantidadeEnderecoCorrespondencia(enderecos);

		for (AipEnderecosPacientes enderecoPaciente : enderecos) {
			this.validarEndereco(enderecoPaciente, validarMovimentacaoMunicipio);
		}
	}

	/**
	 * Método que verifica se, no caso de o prontuário ser gerado, o paciente
	 * possui ao menos um endereço residencial
	 * 
	 * @param paciente
	 * @throws ApplicationBusinessException 
	 */
	// AIPP_ENFORCE_ENP_RULES
	// RN_ENPP_VER_END_RES
	public void validarQuantidadeEnderecoResidencial(
			Set<AipEnderecosPacientes> enderecos) throws ApplicationBusinessException {

		int qtEnderecosResidenciais = 0;

		if (enderecos.size() > 0) {
			for (AipEnderecosPacientes endereco : enderecos) {
				if (endereco.getTipoEndereco().equals(DominioTipoEndereco.R)) {
					qtEnderecosResidenciais++;
				}
			}
		}
		if (qtEnderecosResidenciais != 1) {
			throw new ApplicationBusinessException(
					EnderecoONExceptionCode.AIP_QUANTIDADE_ENDERECO_RESIDENCIAL);
		}

	}

	/**
	 * Método que verifica se o paciente possui mais de um endereço para
	 * correspondência
	 * 
	 * @param código
	 *            do paciente
	 * @throws ApplicationBusinessException
	 */
	// AIPP_ENFORCE_ENP_RULES
	// RN_ENPP_VER_END_CORR
	public void validarQuantidadeEnderecoCorrespondencia(
			Set<AipEnderecosPacientes> enderecos) throws BaseException {

		int quantidadeEnderecosCorr = 0;

		for (AipEnderecosPacientes endereco : enderecos) {
			if (endereco.isPadrao()) {
				quantidadeEnderecosCorr++;
			}
		}

		if (quantidadeEnderecosCorr > 1) {
			throw new ApplicationBusinessException(EnderecoONExceptionCode.AIP_00208);
		}

	}

	/**
	 * Método que atualiza a data de recadastro para data atual TODO Mudar nome
	 * atualizar
	 * 
	 * @param paciente
	 * @throws ApplicationBusinessException
	 */
	// AIPP_ENFORCE_ENP_RULES
	// RN_ENPP_ATU_RECA_PAC
	public void atualizarDataRecadastro(AipPacientes paciente) {
		Date dataAtual = new Date();
		paciente.setDtRecadastro(dataAtual);
	}

	/**
	 * 
	 * @param paciente
	 */
	public void atribuirSequencialEnderecos(AipPacientes paciente) {
		
		Short seqAtual = null;
		AipEnderecosPacientesId idEndereco = null;
		
		for (AipEnderecosPacientes enderecoPaciente : paciente.getEnderecos()) {
			
			if (enderecoPaciente.getId().getSeqp() == null) {
				if (seqAtual == null) {
					seqAtual = this.obterSeqEnderecoPacienteAtual(paciente);
				}
				seqAtual++;

				idEndereco = enderecoPaciente.getId();
				idEndereco.setPacCodigo(paciente.getCodigo());
				idEndereco.setSeqp(seqAtual);

			}
			if (enderecoPaciente.getId().getPacCodigo() == null || !enderecoPaciente.getId().getPacCodigo().equals(paciente.getCodigo())) {
				idEndereco = enderecoPaciente.getId();
				idEndereco.setPacCodigo(paciente.getCodigo());
			}
			
			//INCLUÍDO TESTE PARA NÃO INSERIR ENDEREÇO JÁ EXISTENTE
			if (getAipEnderecosPacientesDAO().obterPorChavePrimaria(enderecoPaciente.getId())!=null){
				getAipEnderecosPacientesDAO().merge(enderecoPaciente);				
			}else{
				getAipEnderecosPacientesDAO().persistir(enderecoPaciente);
			}
			

		}

	}

	/**
	 * Retorna os endereços do paciente.
	 * 
	 * @param pacCodigo
	 *            código do paciente.
	 * @return
	 */
	public List<AipEnderecosPacientes> obterEndPaciente(Integer pacCodigo) {
		return this.getAipEnderecosPacientesDAO().obterEndPaciente(pacCodigo);
	}

	public List<AipEnderecosPacientesId> obterIdEndPaciente(Integer pacCodigo) {
		return this.getAipEnderecosPacientesDAO().obterIdEndPaciente(pacCodigo);
	}

	public short obterSeqEnderecoPacienteAtual(AipPacientes paciente) {
		return this.getAipEnderecosPacientesDAO()
				.obterSeqEnderecoPacienteAtual(paciente);
	}

	/**
	 * ORADB V_AIP_ENDERECO_PACIENTE
	 * 
	 * @param codigoPaciente
	 * @return
	 */
	public VAipEnderecoPaciente obterEndecoPaciente(Integer codigoPaciente) {
		VAipEnderecoPaciente vAipEnderecoPaciente = null;
		if ((vAipEnderecoPaciente = obterUnionEnderecoPaciente1(codigoPaciente)) == null) {
			if ((vAipEnderecoPaciente = obterUnionEnderecoPaciente2(codigoPaciente)) == null) {
				vAipEnderecoPaciente = obterUnionEnderecoPaciente3(codigoPaciente);
			}
		}

		return vAipEnderecoPaciente;
	}

	private VAipEnderecoPaciente obterUnionEnderecoPaciente1(
			Integer codigoPaciente) {
		AipEnderecosPacientes aipEnderecoPaciente = this
				.getAipEnderecosPacientesDAO().obterAipEnderecosPacientes(
						codigoPaciente,
						this.getInternacaoFacade()
								.recuperarEnderecoCorrespondencia(
										codigoPaciente));

		VAipEnderecoPaciente vAipEnderecoPaciente = null;
		if (aipEnderecoPaciente != null) {
			vAipEnderecoPaciente = new VAipEnderecoPaciente();
			vAipEnderecoPaciente.setPacCodigo(aipEnderecoPaciente
					.getAipPaciente().getCodigo());
			vAipEnderecoPaciente.setLogradouro(aipEnderecoPaciente
					.getLogradouroEndereco());
			vAipEnderecoPaciente.setNroLogradouro(aipEnderecoPaciente
					.getNroLogradouro());
			vAipEnderecoPaciente.setComplLogradouro(aipEnderecoPaciente
					.getComplLogradouro());
			vAipEnderecoPaciente.setBairro(aipEnderecoPaciente
					.getBairroEndereco());
			vAipEnderecoPaciente.setCidade(aipEnderecoPaciente
					.getCidadeEndereco());
			Integer cep = aipEnderecoPaciente.getCepEndereco();
			vAipEnderecoPaciente.setCep(cep == null ? null
					: new BigDecimal(cep));
			vAipEnderecoPaciente.setUf(aipEnderecoPaciente.getUfEndereco());
			vAipEnderecoPaciente.setCodIbge(BigDecimal.ZERO);
			vAipEnderecoPaciente.setCddCodigo(aipEnderecoPaciente
					.getAipCidade() == null ? null : aipEnderecoPaciente
					.getAipCidade().getCodigo());
			vAipEnderecoPaciente.setTipCodigo(null);
		}

		return vAipEnderecoPaciente;
	}

	private VAipEnderecoPaciente obterUnionEnderecoPaciente2(
			Integer codigoPaciente) {
		AipEnderecosPacientes aipEnderecoPaciente = this
				.getAipEnderecosPacientesDAO().obterEnderecoPacienteJoin2(
						codigoPaciente,
						this.getInternacaoFacade()
								.recuperarEnderecoCorrespondencia(
										codigoPaciente));

		if (aipEnderecoPaciente == null) {
			return null;
		}

		AipLogradouros aipLogradouro = getAipLogradourosDAO()
				.obterPorChavePrimaria(aipEnderecoPaciente.getBclCloLgrCodigo());
		// Isto teve que ser feito em função de o objeto AipLogradouros não
		// estar sendo
		// carregado no AipEnderecoPaciente (possivelmente por causa da cache do
		// Hibernate)
		aipEnderecoPaciente.setAipLogradouro(aipLogradouro);
		

		VAipEnderecoPaciente vAipEnderecoPaciente = null;
		if (aipEnderecoPaciente != null) {
			vAipEnderecoPaciente = new VAipEnderecoPaciente();
			vAipEnderecoPaciente.setTlgCodigo(aipLogradouro.getAipTipoLogradouro() != null ? aipLogradouro.getAipTipoLogradouro().getCodigo() : null);
			vAipEnderecoPaciente.setPacCodigo(aipEnderecoPaciente
					.getAipPaciente().getCodigo());

			StringBuffer logradouro = new StringBuffer();
			if (aipEnderecoPaciente.getAipLogradouro() != null
					&& aipEnderecoPaciente.getAipLogradouro()
							.getAipTipoLogradouro() != null
					&& aipEnderecoPaciente.getAipLogradouro()
							.getAipTipoLogradouro().getAbreviatura() != null) {
				logradouro.append(
						aipEnderecoPaciente.getAipLogradouro()
								.getAipTipoLogradouro().getAbreviatura())
						.append(' ');
			}
			if (aipEnderecoPaciente.getAipLogradouro() != null) {
				if (aipEnderecoPaciente.getAipLogradouro()
						.getAipTituloLogradouro() != null) {
					logradouro.append(
							aipEnderecoPaciente.getAipLogradouro()
									.getAipTituloLogradouro().getDescricao())
							.append(' ');
				}
				logradouro.append(aipEnderecoPaciente.getAipLogradouro()
						.getNome());
			}
			vAipEnderecoPaciente.setLogradouro(logradouro.toString().trim());

			vAipEnderecoPaciente.setNroLogradouro(aipEnderecoPaciente
					.getNroLogradouro());
			vAipEnderecoPaciente.setComplLogradouro(aipEnderecoPaciente
					.getComplLogradouro());
			vAipEnderecoPaciente.setBairro(aipEnderecoPaciente
					.getBairroEndereco());
			vAipEnderecoPaciente.setCidade(aipEnderecoPaciente
					.getCidadeEndereco());
			if (aipEnderecoPaciente.getCepEndereco() != null) {
				vAipEnderecoPaciente.setCep(new BigDecimal(aipEnderecoPaciente
						.getCepEndereco()));
			}
			vAipEnderecoPaciente.setUf(aipEnderecoPaciente.getUfEndereco());
			if (aipEnderecoPaciente.getAipLogradouro() != null) {
				vAipEnderecoPaciente.setCodIbge(new BigDecimal(
						aipEnderecoPaciente.getAipLogradouro().getAipCidade()
								.getCodIbge()));
				vAipEnderecoPaciente.setCddCodigo(aipEnderecoPaciente
						.getAipLogradouro().getAipCidade().getCodigo());
				vAipEnderecoPaciente.setTipCodigo(aipEnderecoPaciente
						.getAipLogradouro().getAipTipoLogradouro().getCodigo());
			}
		}

		return vAipEnderecoPaciente;
	}

	private VAipEnderecoPaciente obterUnionEnderecoPaciente3(
			Integer codigoPaciente) {
		AipEnderecosPacientes aipEnderecoPaciente = this
				.getAipEnderecosPacientesDAO().obterEnderecoPacienteJoin3(
						codigoPaciente,
						this.getInternacaoFacade()
								.recuperarEnderecoCorrespondencia(
										codigoPaciente));

		VAipEnderecoPaciente vAipEnderecoPaciente = null;
		if (aipEnderecoPaciente != null) {
			vAipEnderecoPaciente = new VAipEnderecoPaciente();
			vAipEnderecoPaciente.setPacCodigo(aipEnderecoPaciente
					.getAipPaciente().getCodigo());
			vAipEnderecoPaciente.setLogradouro(aipEnderecoPaciente
					.getLogradouroEndereco());
			vAipEnderecoPaciente.setNroLogradouro(aipEnderecoPaciente
					.getNroLogradouro());
			vAipEnderecoPaciente.setComplLogradouro(aipEnderecoPaciente
					.getComplLogradouro());
			vAipEnderecoPaciente.setBairro(aipEnderecoPaciente
					.getBairroEndereco());
			vAipEnderecoPaciente.setCidade(aipEnderecoPaciente
					.getCidadeEndereco());

			if (aipEnderecoPaciente.getCep() == null) {
				if (aipEnderecoPaciente.getAipCidade() != null
						&& aipEnderecoPaciente.getAipCidade().getCep() != null) {
					vAipEnderecoPaciente.setCep(new BigDecimal(
							aipEnderecoPaciente.getAipCidade().getCep()));
				}
			} else {
				vAipEnderecoPaciente.setCep(new BigDecimal(aipEnderecoPaciente
						.getCep()));
			}

			vAipEnderecoPaciente.setUf(aipEnderecoPaciente.getUfEndereco());

			if (aipEnderecoPaciente.getAipCidade() != null) {
				if (aipEnderecoPaciente.getAipCidade().getCodIbge() != null) {
					vAipEnderecoPaciente.setCodIbge(new BigDecimal(
							aipEnderecoPaciente.getAipCidade().getCodIbge()));
				}
				vAipEnderecoPaciente.setCddCodigo(aipEnderecoPaciente
						.getAipCidade().getCodigo());
			}
			vAipEnderecoPaciente.setTipCodigo(null);
		}
		return vAipEnderecoPaciente;
	}

	/**
	 * Método para buscar Cidade e Uf do endereço de paciente, caso os mesmos
	 * venham com campos nulos, porém com IDs carregados (problema de FetchMode
	 * na criteria)
	 * 
	 * @param enderecoPaciente
	 */
	private void buscarAtributosEndereco(AipEnderecosPacientes enderecoPaciente) {
		if (enderecoPaciente.getAipCidade() != null
				&& enderecoPaciente.getAipCidade().getNome() == null
				&& enderecoPaciente.getAipCidade().toString() != null) {

			Integer codigoCidade = Integer.valueOf(enderecoPaciente
					.getAipCidade().toString());

			AipCidades cidade = this.getCadastrosBasicosPacienteFacade()
					.obterCidadePorCodigo(codigoCidade, false);
			enderecoPaciente.setAipCidade(cidade);
		}

		if (enderecoPaciente.getAipUf() != null
				&& enderecoPaciente.getAipUf().getNome() == null
				&& enderecoPaciente.getAipUf().toString() != null) {
			AipUfs uf = this.getCadastrosBasicosPacienteFacade().obterUF(
					enderecoPaciente.getAipUf().getSigla());
			enderecoPaciente.setAipUf(uf);
		}
	}
	
	/**
	 * Método que chama as triggers responsáveis pela remoção do endereço do paciente
	 * e em seguida remove o objeto
	 * @param endereco
	 */
	public void removerEnderecoPaciente(AipEnderecosPacientes endereco){
		AipEnderecosPacientesDAO dao = getAipEnderecosPacientesDAO();
		this.aiptEnpArd(endereco);
		endereco = dao.obterPorChavePrimaria(endereco.getId());
		dao.remover(endereco);
	}

	// TABELA : AIP_ENDERECOS_PACIENTES
	// TRIGGER : AIPT_ENP_ARD
	// TYPE : AFTER EACH ROW
	// EVENT : DELETE
	// TODO Usar nome de método não de triger, de preferencia usar excluir no
	// começo
	@SuppressWarnings("PMD.NPathComplexity")
	private void aiptEnpArd(AipEnderecosPacientes delete) {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AipEnderecosPacientesJn jn = BaseJournalFactory.getBaseJournal(
				DominioOperacoesJournal.DEL, AipEnderecosPacientesJn.class, servidorLogado.getUsuario());

		// FIXME encontrar alguma alternativa de na query
		// PacienteON.obterPaciente() trazer todos endereços com
		// cidade/uf/bairroCepLogradouro carregados, assim evitando a chamada do
		// método buscarAtributosEndereco()

		// Método para buscar atributos de objetos mapeados que estão somente
		// com ID (evita LazyInitializationException)
		// Não foi possível evitar o LazyInitializationException através de
		// FetchMode.JOIN na query.
		this.buscarAtributosEndereco(delete);

		jn.setPacCodigo(delete.getId().getPacCodigo());
		jn.setSeqp(delete.getId().getSeqp());
		jn.setTipoEndereco(delete.getTipoEndereco());
		jn.setIndPadrao(delete.getIndPadrao());
		jn.setCidade(delete.getAipCidade());

		jn.setNomeCidade(delete.getCidade());

		jn.setUf(delete.getAipUf());
		jn.setLogradouro(delete.getLogradouro());
		jn.setNroLogradouro(delete.getNroLogradouro());
		jn.setComplLogradouro(delete.getComplLogradouro());
		jn.setBairro(delete.getBairro());
		jn.setCep(delete.getCep());

		// AipBairrosCepLogradouro
		jn.setBclBaiCodigo(delete.getAipBairrosCepLogradouro() == null ? null
				: delete.getAipBairrosCepLogradouro().getId().getBaiCodigo());
		jn.setBclCloCep(delete.getAipBairrosCepLogradouro() == null ? null
				: delete.getAipBairrosCepLogradouro().getId().getCloCep());
		jn.setBclCloLgrCodigo(delete.getAipBairrosCepLogradouro() == null ? null
				: delete.getAipBairrosCepLogradouro().getId().getCloLgrCodigo());

		jn.setIndExclusaoForcada(delete.getIndExclusaoForcada() == null ? null
				: DominioSimNao.valueOf(delete.getIndExclusaoForcada()));

		this.getAipEnderecosPacientesJnDAO().persistir(jn);
	}

	// TABELA : AIP_ENDERECOS_PACIENTES
	// TRIGGER : AIPT_ENP_ARU
	// TYPE : AFTER EACH ROW
	// EVENT : UPDATE
	// TODO Usar nome de método não de triger, de preferencia usar atualizar
	// no
	// começo
	public void aiptEnpAru(AipEnderecosPacientes old,
			AipEnderecosPacientes update) {
		if (verificarDiferencaObject(old.getId(), update.getId())
				|| verificarDiferencaObject(old.getTipoEndereco(),
						update.getTipoEndereco())
				|| (old.isPadrao() != update.isPadrao())
				|| (old.getAipCidade() != null && update.getAipCidade() != null && verificarDiferencaObject(
						old.getAipCidade().getCodigo(), update.getAipCidade()
								.getCodigo()))
				|| (old.getAipUf() != null && update.getAipUf() != null && verificarDiferencaObject(
						old.getAipUf().getSigla(), update.getAipUf().getSigla()))
				|| verificarDiferencaObject(old.getLogradouro(),
						update.getLogradouro())
				|| verificarDiferencaObject(old.getNroLogradouro(),
						update.getNroLogradouro())
				|| verificarDiferencaObject(old.getComplLogradouro(),
						update.getComplLogradouro())
				|| verificarDiferencaObject(old.getBairro(), update.getBairro())
				|| verificarDiferencaObject(old.getCep(), update.getCep())
				|| (old.getAipBairrosCepLogradouro() != null
						&& update.getAipBairrosCepLogradouro() != null && verificarDiferencaObject(
							old.getAipBairrosCepLogradouro().getId(), update
									.getAipBairrosCepLogradouro().getId()))
				|| verificarDiferencaObject(old.getIndExclusaoForcada(),
						update.getIndExclusaoForcada())) {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

			AipEnderecosPacientesJn jn = BaseJournalFactory
					.getBaseJournal(DominioOperacoesJournal.UPD,
							AipEnderecosPacientesJn.class, servidorLogado.getUsuario());

			jn.setPacCodigo(old.getId().getPacCodigo());
			jn.setSeqp(old.getId().getSeqp());
			jn.setTipoEndereco(old.getTipoEndereco());
			jn.setIndPadrao(old.getIndPadrao());
			jn.setCidade(old.getAipCidade());
			jn.setNomeCidade(old.getCidade());
			jn.setUf(old.getAipUf());
			jn.setLogradouro(old.getLogradouro());
			jn.setNroLogradouro(old.getNroLogradouro());
			jn.setComplLogradouro(old.getComplLogradouro());
			jn.setBairro(old.getBairro());
			jn.setCep(old.getCep());
			if (old.getAipBairrosCepLogradouro() != null
					&& old.getAipBairrosCepLogradouro().getId() != null) {
				final AipBairrosCepLogradouroId aipBCLId = old
						.getAipBairrosCepLogradouro().getId();
				jn.setBclBaiCodigo(aipBCLId.getBaiCodigo());
				jn.setBclCloCep(aipBCLId.getCloCep());
				jn.setBclCloLgrCodigo(aipBCLId.getCloLgrCodigo());
			}

			if (old.getIndExclusaoForcada() != null) {
				jn.setIndExclusaoForcada(DominioSimNao.valueOf(old
						.getIndExclusaoForcada()));
			}

			this.getAipEnderecosPacientesJnDAO().persistir(jn);

		}
	}

	/**
	 * TODO Este método deverá ficar centralizado Método para fazer comparação
	 * entre valores atuais e anteriores para atributos do objeto AghGrupoCids,
	 * evitando nullpointer.
	 * 
	 * @param vlrAnterior
	 * @param vlrAtual
	 * @return FALSE se parametros recebido forem iguais / TRUE se parametros
	 *         recebidos forem diferentes
	 */
	private Boolean verificarDiferencaObject(Object vlrAtual, Object vlrAnterior) {

		if (vlrAnterior == null && vlrAtual == null) {
			return false;
		} else if (vlrAnterior != null && !(vlrAnterior.equals(vlrAtual))) {
			return true;
		} else if (vlrAtual != null && !(vlrAtual.equals(vlrAnterior))) {
			return true;
		} else {
			return false;
		}
	}

	public List<AipBairrosCepLogradouro> pesquisarLogradourosCepPorDescricaoCidade(
			String descricao, Integer codigoCidade) throws ApplicationBusinessException {
		List<AipLogradouros> logradourosList = new ArrayList<AipLogradouros>();
		List<AipTipoLogradouros> tipoLogradourosList = new ArrayList<AipTipoLogradouros>();
		if (StringUtils.isNotBlank(descricao)) {
			
			if (descricao.length() >= 3) {
				logradourosList = this.getAipLogradourosDAO().pesquisarLogradouros(
								AipLogradouros.Fields.NOME.toString(),
								AipLogradouros.Fields.NOME_FONETICO.toString(),
								descricao, AipLogradouros.class,
								AipLogradouros.Fields.NOME.toString());
			} else {
				logradourosList = this.getAipLogradourosDAO().pesquisarLogradouros(codigoCidade, descricao);
			}
			
			tipoLogradourosList = this.getAipTipoLogradourosDAO().pesquisarTiposLogradouro(
							AipTipoLogradouros.Fields.DESCRICAO.toString(),
							AipTipoLogradouros.Fields.DESCRICAO_FONETICA.toString(), 
							descricao, AipTipoLogradouros.class,
							AipTipoLogradouros.Fields.DESCRICAO.toString());
		}
		// caso um texto tenha sido digitado e seja encontrado tipo ou
		// logradouro
		if (!StringUtils.isBlank(descricao) && logradourosList.isEmpty()
				&& tipoLogradourosList.isEmpty()) {
			return new ArrayList<AipBairrosCepLogradouro>();
		}

		return this.getAipBairrosCepLogradouroDAO()
				.pesquisarLogradourosCepPorDescricaoCidade(descricao,
						codigoCidade, logradourosList,
						tipoLogradourosList);
	}

	public List<VAipCeps> pesquisarVAipCepsPorLogradouroCidade(
			String descricao, Integer codigoCidade) throws ApplicationBusinessException {
		List<AipLogradouros> logradourosList = new ArrayList<AipLogradouros>();
		List<AipTipoLogradouros> tipoLogradourosList = new ArrayList<AipTipoLogradouros>();
		List<AipUnidadeOperacao> unidadeOperacaoList = new ArrayList<AipUnidadeOperacao>();
		List<AipCaixaPostalComunitarias> cxPostalComunList = new ArrayList<AipCaixaPostalComunitarias>();
		List<AipGrandesUsuarios> grandesUsuariosList = new ArrayList<AipGrandesUsuarios>();
		List<Integer> cepList = new ArrayList<Integer>();
		if (StringUtils.isNotBlank(descricao)) {
			
			if (descricao.length() >= 3) {
				logradourosList = this.getAipLogradourosDAO().pesquisarLogradouros(
								AipLogradouros.Fields.NOME.toString(),
								AipLogradouros.Fields.NOME_FONETICO.toString(),
								descricao, AipLogradouros.class,
								AipLogradouros.Fields.NOME.toString());
			} else {
				logradourosList = this.getAipLogradourosDAO().pesquisarLogradouros(codigoCidade, descricao);
			}
			
			tipoLogradourosList = this.getAipTipoLogradourosDAO().pesquisarTiposLogradouro(
							AipTipoLogradouros.Fields.DESCRICAO.toString(),
							AipTipoLogradouros.Fields.DESCRICAO_FONETICA.toString(), 
							descricao, AipTipoLogradouros.class,
							AipTipoLogradouros.Fields.DESCRICAO.toString());
			
			unidadeOperacaoList = this.getAipBairrosCepLogradouroDAO().pesquisaFoneticaAipUnidadeOperacao(
					AipUnidadeOperacao.Fields.DESCRICAO.toString(),
					AipUnidadeOperacao.Fields.DESCRICAO_FONETICA.toString(),
					descricao, AipUnidadeOperacao.class,
					AipUnidadeOperacao.Fields.DESCRICAO.toString());

			cxPostalComunList = this.getAipBairrosCepLogradouroDAO().pesquisaFoneticaAipCaixaPostalComunitaria(
					AipCaixaPostalComunitarias.Fields.NOME.toString(),
					AipCaixaPostalComunitarias.Fields.NOME_FONETICO.toString(),
					descricao, AipCaixaPostalComunitarias.class,
					AipCaixaPostalComunitarias.Fields.NOME.toString());

			grandesUsuariosList = this.getAipBairrosCepLogradouroDAO().pesquisaFoneticaAipGrandesUsuarios(
					AipGrandesUsuarios.Fields.NOME.toString(),
					AipGrandesUsuarios.Fields.NOME_FONETICO.toString(),
					descricao, AipGrandesUsuarios.class,
					AipGrandesUsuarios.Fields.NOME.toString());

		}

		for(AipUnidadeOperacao unidade:unidadeOperacaoList){
			cepList.add(unidade.getCep());
		}
		for(AipCaixaPostalComunitarias cxPostal:cxPostalComunList){
			cepList.add(cxPostal.getCep());
		}
		for(AipGrandesUsuarios grandesUsuarios:grandesUsuariosList){
			cepList.add(grandesUsuarios.getCep());
		}
		List<VAipCeps> retorno =  this.getAipBairrosCepLogradouroDAO()
				.pesquisarVAipCepsPorLogradouroCidade(descricao,
						codigoCidade, logradourosList,
						tipoLogradourosList, cepList, false);
		if (retorno == null){
			retorno = new ArrayList<VAipCeps>();
		}
		
		if (retorno.size() < 10){
			boolean contem = false;
			for (VAipCeps vAipCeps : retorno) {
				contem = vAipCeps.getLogradouro().contains(descricao);
				if (contem){
					break;
				}
			}
			
			if(!contem) {
			retorno.addAll(getAipBairrosCepLogradouroDAO()
					.pesquisarVAipCepsPorLogradouroCidade(descricao,
							codigoCidade, null,
							null, null, true));
			}
		}
		return retorno;
	}

	/**
	 * Método que retorno o endereco a partir do CEP. TODO Nome de Metodo Camel
	 * Case.
	 * 
	 * @param cep
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<VAipLocalidadeUc> pesquisarCepsPorCidade(String param, Integer codigoCidade)
			throws ApplicationBusinessException {
		return this
				.getAipBairrosCepLogradouroDAO().listarVAipLocalidadeUc(
						param, codigoCidade);
	}

	/**
	 * Método que retorno o endereco a partir do CEP. TODO Nome de Metodo Camel
	 * Case.
	 * 
	 * @param cep
	 * @return
	 * @throws ApplicationBusinessException
	 */

	public Integer obterCountCepsPorCidade(String param, Integer codigoCidade)
			throws ApplicationBusinessException {
		Long count = this.getAipBairrosCepLogradouroDAO()
				.listarVAipLocalidadeUcCount(param, codigoCidade);

		if (codigoCidade != null && count == 0) {
			AipCidades cidade = this.getAipCidadesDAO().obterPorChavePrimaria(
					codigoCidade);
			if (cidade != null && cidade.getCep() > 0) {
				count++;
			}
		}

		return count.intValue();
	}

	public AipBairrosCepLogradouro recarregarCepLogradouro(
			AipBairrosCepLogradouro bairro) {
		return this.getAipBairrosCepLogradouroDAO().merge(bairro);
	}

	public AipEnderecosPacientes obterEnderecoResidencialPaciente(
			AipPacientes paciente) {
		List<AipEnderecosPacientes> listaEnderecos = this
				.getAipEnderecosPacientesDAO()
				.obterEnderecosResidencialPaciente(paciente);

		AipEnderecosPacientes retorno = null;
		for (AipEnderecosPacientes endereco : listaEnderecos) {
			retorno = endereco;
			break;
		}

		return retorno;
	}

	public AipEnderecosPacientes obterEnderecoBanco(
			AipEnderecosPacientesId idEnderecoAnterior) {
		Object[] retornoConsulta = this.getAipEnderecosPacientesDAO()
				.obterInformacoesEnderecoAnterior(idEnderecoAnterior);

		AipEnderecosPacientes enderecoBanco = null;

		if (retornoConsulta != null) {

			AipBairrosCepLogradouro bairroCepLogradouro = null;

			try {
				bairroCepLogradouro = this.getAipEnderecosPacientesDAO()
						.obterBairroCepLogradouro(idEnderecoAnterior);
			} catch (NoResultException e) {
				// engolir esta exceção - o endereço não possui
				// AipBairrosCepLogradouro - não tem problema
				this.logDebug(e);
			}

			enderecoBanco = new AipEnderecosPacientes();

			enderecoBanco.setAipLogradouro((AipLogradouros) retornoConsulta[0]);

			enderecoBanco.setAipBairrosCepLogradouro(bairroCepLogradouro);

			enderecoBanco.setAipCidade((AipCidades) retornoConsulta[1]);

			enderecoBanco.setCidade((String) retornoConsulta[2]);

			enderecoBanco.setIndPadrao((DominioSimNao) retornoConsulta[3]);

			enderecoBanco.setLogradouro((String) retornoConsulta[4]);

			enderecoBanco.setNroLogradouro((Integer) retornoConsulta[5]);

			enderecoBanco
					.setTipoEndereco((DominioTipoEndereco) retornoConsulta[6]);

			enderecoBanco.setAipUf((AipUfs) retornoConsulta[7]);

			enderecoBanco.setComplLogradouro((String) retornoConsulta[8]);

			enderecoBanco.setBairro((String) retornoConsulta[9]);

			enderecoBanco.setCep((Integer) retornoConsulta[10]);

			enderecoBanco.setIndExclusaoForcada((String) retornoConsulta[11]);
		}

		return enderecoBanco;
	}

	/**
	 * ORADB Triggers AIPT_ENP_BSU, AIPT_ENP_BRU, AIPT_ENP_ARU e AIPT_ENP_ASU.
	 * Método de atualização de endereço chamando cada trigger de update para
	 * AIP_ENDERECOS_PACIENTES.
	 * 
	 * @param endOld
	 * @param end
	 * @throws ApplicationBusinessException
	 * @author Marcelo Tocchetto
	 */
	public void atualizarEndereco(AipEnderecosPacientes endOld,
			AipEnderecosPacientes end) throws BaseException {
		// Trigger AIPT_ENP_BSU, BEFORE STATEMENT - Apenas inicia stack, não
		// precisa ser migrada.
		// Trigger AIPT_ENP_BRU, BEFORE EACH ROW
		atualizarEnderecoBRU(endOld, end);
		// Trigger AIPT_ENP_ARU, AFTER EACH ROW - EnderecoON.aiptEnpAru
		aiptEnpAru(endOld, end);
		// Trigger AIPT_ENP_ASU, AFTER STATEMENT - Chama enforce
		// AIPP_ENFORCE_ENP_RULES indicando evento 'UPDATE'
		atualizarEnderecoRegrasEnforce(end, endOld);
	}

	/**
	 * ORADB Trigger AIPT_ENP_BRU, BEFORE EACH ROW - Tabela
	 * AIP_ENDERECOS_PACIENTES.
	 * 
	 * @param endOld
	 * @param end
	 * 
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	protected void atualizarEnderecoBRU(AipEnderecosPacientes endOld,
			AipEnderecosPacientes end) throws ApplicationBusinessException {
		// Valida campos obrigatórios
		Integer cloCepOld = null;
		Integer cloCepNew = null;

		if (endOld.getAipBairrosCepLogradouro() != null
				&& endOld.getAipBairrosCepLogradouro().getId() != null) {
			cloCepOld = endOld.getAipBairrosCepLogradouro().getId().getCloCep();
		}
		if (end.getAipBairrosCepLogradouro() != null
				&& end.getAipBairrosCepLogradouro().getId() != null) {
			cloCepNew = end.getAipBairrosCepLogradouro().getId().getCloCep();
		}

		Integer codigoCidadeOld = endOld.getAipCidade() == null ? null : endOld
				.getAipCidade().getCodigo();
		Integer codigoCidadeNew = end.getAipCidade() == null ? null : end
				.getAipCidade().getCodigo();

		if (CoreUtil.modificados(cloCepOld, cloCepNew)
				|| CoreUtil.modificados(endOld.getLogradouro(),
						end.getLogradouro())
				|| CoreUtil.modificados(endOld.getCidade(), end.getCidade())
				|| CoreUtil.modificados(codigoCidadeOld, codigoCidadeNew)) {
			verificarCamposInclusaoEndereco(cloCepNew, end.getLogradouro(),
					end.getCidade(), codigoCidadeNew, end.getAipPaciente()
							.getCodigo());
		}
	}

	/**
	 * ORADB Procedure AIPK_ENP_RN.RN_ENPP_VER_INCLUSAO.
	 * 
	 * @param cloCep
	 * @param logradouro
	 * @param cidade
	 * @param codigoCidade
	 * @param pacCodigo
	 * @throws ApplicationBusinessException
	 */
	/* Verifica se existe campos de inclusão informado */
	public void verificarCamposInclusaoEndereco(Integer cloCep,
			String logradouro, String cidade, Integer codigoCidade,
			Integer pacCodigo) throws ApplicationBusinessException {
		// verifica a informação de campos obrigatórios
		if (cloCep == null) {
			if (logradouro == null) {
				AipPacientesHist historico = this.getAipPacientesHistDAO()
						.obterPorChavePrimaria(pacCodigo);
				if (historico == null) {
					throw new ApplicationBusinessException(
							EnderecoONExceptionCode.AIP_00221);
				}
			}

			if (cidade == null && codigoCidade == null) {
				AipPacientesHist historico = this.getAipPacientesHistDAO()
						.obterPorChavePrimaria(pacCodigo);
				if (historico == null) {
					throw new ApplicationBusinessException(
							EnderecoONExceptionCode.AIP_00234);
				}
			}
		}
	}

	/**
	 * ORADB Trigger AIPT_ENP_ASU, AFTER STATEMENT - Chama enforce
	 * AIPP_ENFORCE_ENP_RULES indicando evento 'UPDATE'. ORADB Procedure
	 * AIPP_ENFORCE_ENP_RULES - Este método contém apenas as regras do evento
	 * UPDATE.
	 * 
	 * @param end
	 * @param endOld
	 * @throws BaseException
	 */
	public void atualizarEnderecoRegrasEnforce(AipEnderecosPacientes endOld,
			AipEnderecosPacientes end) throws BaseException {
		AipPacientesDAO aipPacientesDAO = this.getAipPacientesDAO();

		// Garante que os dados estão gravados no banco antes de realizar
		// qualquer teste sobre os endereços do paciente do novo registro.
		AipPacientes aipPaciente = end.getAipPaciente();

		aipPacientesDAO.flush();
		aipPacientesDAO.refresh(aipPaciente);

		if (CoreUtil.modificados(endOld.getIndPadrao(), end.getIndPadrao())) {
			validarQuantidadeEnderecoCorrespondencia(aipPaciente.getEnderecos());
		}
		if (CoreUtil.modificados(endOld.getTipoEndereco(),
				end.getTipoEndereco())) {
			validarQuantidadeEnderecoResidencial(aipPaciente.getEnderecos());
		}
		atualizarDataRecadastro(aipPaciente);

		aipPacientesDAO.flush();
	}

	protected ICadastrosBasicosPacienteFacade getCadastrosBasicosPacienteFacade() {
		return this.cadastrosBasicosPacienteFacade;
	}

	protected ICascaFacade getICascaFacade() {
		return (ICascaFacade) this.cascaFacade;
	}

	protected IInternacaoFacade getInternacaoFacade() {
		return internacaoFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected AipEnderecosPacientesJnDAO getAipEnderecosPacientesJnDAO() {
		return aipEnderecosPacientesJnDAO;
	}

	protected AipBairrosCepLogradouroDAO getAipBairrosCepLogradouroDAO() {
		return aipBairrosCepLogradouroDAO;
	}

	protected AipCidadesDAO getAipCidadesDAO() {
		return aipCidadesDAO;
	}

	protected AipEnderecosPacientesDAO getAipEnderecosPacientesDAO() {
		return aipEnderecosPacientesDAO;
	}

	protected AipLogradourosDAO getAipLogradourosDAO() {
		return aipLogradourosDAO;
	}

	protected AipPacientesHistDAO getAipPacientesHistDAO() {
		return aipPacientesHistDAO;
	}

	protected AipPacientesDAO getAipPacientesDAO() {
		return aipPacientesDAO;
	}

	protected AipTipoLogradourosDAO getAipTipoLogradourosDAO() {
		return aipTipoLogradourosDAO;
	}

	public void excluirEndereco(AipEnderecosPacientes endereco) {
		this.getAipEnderecosPacientesDAO().removerPorId(endereco.getId());
	}

	public void validarCepEnderecoNaoCadastrado(Integer cep)
			throws ApplicationBusinessException {
		Long countLocalidadesView = this.getAipBairrosCepLogradouroDAO()
				.listarVAipLocalidadeUcCount(String.valueOf(cep), null);

		if (countLocalidadesView == null || countLocalidadesView == 0) {
			Long countCeps = this.getAipBairrosCepLogradouroDAO()
					.obterCountCepExato(cep);
			
			if (countCeps == null || countCeps == 0) {
				throw new ApplicationBusinessException(
						EnderecoONExceptionCode.CEP_NAO_CADASTRADO);
			}
			
		}
	}
	
	public VAipCeps pesquisarVAipCepUnicoPorCidade(Integer codigoCidade) {
		List<VAipCeps> listCeps = getAipBairrosCepLogradouroDAO().pesquisarVaipCepUnicoPorCidade(codigoCidade);
		
		return listCeps != null && !listCeps.isEmpty() && listCeps.size() == 1 ? listCeps.get(0) : null;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}

	public List<VAipCeps> pesquisarVAipCepsPorCidade(Integer codigoCidade) {
		List<AipUnidadeOperacao> unidadeOperacaoList = new ArrayList<AipUnidadeOperacao>();
		List<AipCaixaPostalComunitarias> cxPostalComunList = new ArrayList<AipCaixaPostalComunitarias>();
		List<AipGrandesUsuarios> grandesUsuariosList = new ArrayList<AipGrandesUsuarios>();
		List<Integer> cepList = new ArrayList<Integer>();

		unidadeOperacaoList = this.getAipBairrosCepLogradouroDAO().pesquisaAipUnidadeOperacaoPorCodigoCidade(codigoCidade);

		cxPostalComunList = this.getAipBairrosCepLogradouroDAO().pesquisaAipCaixaPostalComunitariaPorCodigoCidade(codigoCidade);

		grandesUsuariosList = this.getAipBairrosCepLogradouroDAO().pesquisaAipGrandesUsuariosPorCodigoCidade(codigoCidade);

		for(AipUnidadeOperacao unidade:unidadeOperacaoList){
			cepList.add(unidade.getCep());
		}
		for(AipCaixaPostalComunitarias cxPostal:cxPostalComunList){
			cepList.add(cxPostal.getCep());
		}
		for(AipGrandesUsuarios grandesUsuarios:grandesUsuariosList){
			cepList.add(grandesUsuarios.getCep());
		}
		return this.getAipBairrosCepLogradouroDAO()
				.pesquisarVAipCepsPorLogradouroCidade("",
						codigoCidade, null,
						null, cepList);
	}

}
