package br.gov.mec.aghu.registrocolaborador.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dao.ObjetosOracleDAO;
import br.gov.mec.aghu.dominio.DominioLadoEndereco;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipBairrosCepLogradouro;
import br.gov.mec.aghu.model.AipCidades;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapPessoasFisicasJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapVinculos;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.cadastro.business.ICadastroPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.dao.RapDependentesDAO;
import br.gov.mec.aghu.registrocolaborador.dao.RapPessoasFisicasDAO;
import br.gov.mec.aghu.registrocolaborador.dao.RapPessoasFisicasJnDAO;
import br.gov.mec.aghu.registrocolaborador.dao.RapServidoresDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.business.fonetizador.FonetizadorUtil;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@SuppressWarnings("PMD.CyclomaticComplexity")
@Stateless
public class PessoaFisicaON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(PessoaFisicaON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private RapDependentesDAO rapDependentesDAO;
	
	@Inject
	private RapPessoasFisicasJnDAO rapPessoasFisicasJnDAO;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@Inject
	private RapPessoasFisicasDAO rapPessoasFisicasDAO;
	
	@Inject
	private RapServidoresDAO rapServidoresDAO;
	
	@EJB
	private ICadastroPacienteFacade cadastroPacienteFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private ObjetosOracleDAO objetosOracleDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 777698867421543528L;

	public enum PessoaFisicaONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_PARAMETRO_NAO_INFORMADO, MENSAGEM_PESSOA_FISICA_NAO_INFORMADA, ERRO_INFORMAR_DDD_TELEFONE_RESIDENCIAL, ERRO_INFORMAR_RAMAL_TELEFONE_RESIDENCIAL, ERRO_INFORMAR_DDD_TELEFONE_CELULAR, ERRO_INFORMAR_DDD_TELEFONE_PAGER, ERRO_RG_CPF_OBRIGATORIOS, ERRO_DATA_NASCIMENTO_FUTURA, ERRO_CPF_JA_CADASTRADO, ERRO_PAC_CODIGO_JA_CADASTRADO, ERRO_USUARIO_SEM_PRIVILEGIO, ERRO_ENDERECO_NAO_INFORMADO, ERRO_CIDADE_ENDERECO_NAO_CADASTRADO, ERRO_NUMERO_LOGRADOURO_PAR, ERRO_NUMERO_LOGRADOURO_IMPAR, ERRO_NUMERO_LOGRADOURO_ACIMA_FAIXA_CEP, ERRO_NUMERO_LOGRADOURO_ABAIXO_FAIXA_CEP, ERRO_CEP_CADASTRADO, ERRO_SERVIDOR_ATIVO_STARH, ERRO_INFORMAR_TELEFONE_RESIDENCIAL, ERRO_INFORMAR_TELEFONE_CELULAR, ERRO_INFORMAR_TELEFONE_PAGER, ERRO_CARTEIRA_SERIE_OBRIGATORIOS, ERRO_TITULO_SECAO_ZONA_ELEITOR_OBRIGATORIOS, ERRO_NACIONALIDADE_OBRIGATORIA, ERRO_CARACTERES_ESPECIAIS_NOME, ERRO_CARACTERES_ESPECIAIS_NOME_MAE, MESSAGE_INVALID_STATE_PESSOA;
	}

	public RapPessoasFisicas obterPessoaFisica(Integer codigo)
			throws ApplicationBusinessException {

		if (codigo == null) {
			throw new ApplicationBusinessException(
					PessoaFisicaONExceptionCode.MENSAGEM_PARAMETRO_NAO_INFORMADO);
		}

		return getRapPessoasFisicasDAO().obterPorChavePrimaria(codigo);
	}
	
	
	public RapPessoasFisicas obterPessoaFisicaCRUD(Integer codigo)
			throws ApplicationBusinessException {
		
		if (codigo == null) {
			throw new ApplicationBusinessException(
					PessoaFisicaONExceptionCode.MENSAGEM_PARAMETRO_NAO_INFORMADO);
		}

		return getRapPessoasFisicasDAO().obterPorChavePrimaria(codigo, true, RapPessoasFisicas.Fields.CIDADE, RapPessoasFisicas.Fields.BAIRRO_CEP_LOGRADOURO, 
						RapPessoasFisicas.Fields.NACIONALIDADE, RapPessoasFisicas.Fields.PACIENTE);
	
	}

	
	public void alterar(RapPessoasFisicas pessoaFisica, RapServidores servidorLogado)
			throws ApplicationBusinessException {

		RapPessoasFisicasDAO pessoasFisicasDAO = getRapPessoasFisicasDAO();
		RapPessoasFisicas pesOriginal = pessoasFisicasDAO.obterOld(pessoaFisica, true);			

		this.validarAtualizacaoPessoa(pesOriginal, pessoaFisica);
		this.validarTelefones(pessoaFisica);
		this.verificarCamposObrigatorios(pessoaFisica);
		this.validarDtNascimento(pessoaFisica.getDtNascimento());
		this.ajustarNomes(pessoaFisica);
		this.validarEndereco(pessoaFisica);

		if (pessoaFisica.getCpf() != null) {			
			if (pessoasFisicasDAO.pesquisarPessoaFisicaCount(pessoaFisica.getCpf(), pessoaFisica.getCodigo()) > 0) {
				throw new ApplicationBusinessException(
						PessoaFisicaONExceptionCode.ERRO_CPF_JA_CADASTRADO);
			}
		}

		if (pessoaFisica.getAipPacientes() != null) {
			if (pessoasFisicasDAO.pesquisarPessoaFisicaCount(pessoaFisica.getAipPacientes().getCodigo(), pessoaFisica.getCodigo()) > 0) {
				throw new ApplicationBusinessException(
						PessoaFisicaONExceptionCode.ERRO_PAC_CODIGO_JA_CADASTRADO);
			}
		}

		this.journalUpdate(pesOriginal, pessoaFisica);
		try {
			pessoasFisicasDAO.merge(pessoaFisica);
			pessoasFisicasDAO.flush();
		}
		catch (ConstraintViolationException e) {
			logError(e);
			for (ConstraintViolation violation : e.getConstraintViolations()) {
				String mensagem = violation.getMessage();
				if (violation.getMessage() != null && violation.getMessage().equalsIgnoreCase("not a well-formed email address")){
					mensagem = "Email inválido!";
				}
				throw new ApplicationBusinessException(
						PessoaFisicaONExceptionCode.MESSAGE_INVALID_STATE_PESSOA, mensagem);	
			}
		}
	}
		
	
	
	public void salvar(RapPessoasFisicas pessoaFisica)
			throws ApplicationBusinessException {

		if (pessoaFisica == null) {
			throw new ApplicationBusinessException(
					PessoaFisicaONExceptionCode.MENSAGEM_PESSOA_FISICA_NAO_INFORMADA);
		}

		this.validarTelefones(pessoaFisica);
		this.verificarCamposObrigatorios(pessoaFisica);
		this.validarDtNascimento(pessoaFisica.getDtNascimento());
		this.ajustarNomes(pessoaFisica);
		this.validarEndereco(pessoaFisica);

		RapPessoasFisicasDAO pessoasFisicasDAO = getRapPessoasFisicasDAO();
		
		if (pessoaFisica.getCpf() != null) {
			if (pessoasFisicasDAO.pesquisarPessoaFisicaCount(pessoaFisica.getCpf()) > 0) {
				throw new ApplicationBusinessException(
						PessoaFisicaONExceptionCode.ERRO_CPF_JA_CADASTRADO);
			}
		}

		if (pessoaFisica.getAipPacientes() != null) {
			if (pessoasFisicasDAO.pesquisarPessoaFisicaCount(pessoaFisica.getAipPacientes().getCodigo()) > 0) {
				throw new ApplicationBusinessException(
						PessoaFisicaONExceptionCode.ERRO_PAC_CODIGO_JA_CADASTRADO);
			}
		}

		if (pessoaFisica.getAipCidades() != null) {
			AipCidades cidade = this.getPacienteFacade().obterCidadePorChavePrimaria(pessoaFisica.getAipCidades().getCodigo());
			pessoaFisica.setAipCidades(cidade);
		}

		try{
			pessoasFisicasDAO.persistir(pessoaFisica);
			pessoasFisicasDAO.flush();
		}catch (ConstraintViolationException e) {
			pessoaFisica.setCodigo(null);
			logError(e);	
			for (ConstraintViolation violation : e.getConstraintViolations()) {
				String mensagem = violation.getMessage();
				if (violation.getMessage() != null && violation.getMessage().equalsIgnoreCase("not a well-formed email address")){
					mensagem = "Email inválido!";
				}
				throw new ApplicationBusinessException(
						PessoaFisicaONExceptionCode.MESSAGE_INVALID_STATE_PESSOA, mensagem);	
			}
		}
	}	

	@SuppressWarnings("PMD.NPathComplexity")
	private void validarTelefones(RapPessoasFisicas rapPessoasFisicas)
			throws ApplicationBusinessException {

		Short dddResidencial = rapPessoasFisicas.getDddFoneResidencial();
		Long foneResidencial = rapPessoasFisicas.getFoneResidencial();
		Integer ramalResidencial = rapPessoasFisicas.getRamalFoneResidencial();

		Short dddCelular = rapPessoasFisicas.getDddFoneCelular();
		Long foneCelular = rapPessoasFisicas.getFoneCelular();

		Short dddPager = rapPessoasFisicas.getDddFonePagerBip();
		Long fonePager = rapPessoasFisicas.getFonePagerBip();

		if (dddResidencial != null && foneResidencial == null) {
			throw new ApplicationBusinessException(
					PessoaFisicaONExceptionCode.ERRO_INFORMAR_DDD_TELEFONE_RESIDENCIAL);
		}

		if (dddResidencial == null && foneResidencial != null) {
			throw new ApplicationBusinessException(
					PessoaFisicaONExceptionCode.ERRO_INFORMAR_TELEFONE_RESIDENCIAL);
		}

		if (ramalResidencial != null && foneResidencial == null) {
			throw new ApplicationBusinessException(
					PessoaFisicaONExceptionCode.ERRO_INFORMAR_RAMAL_TELEFONE_RESIDENCIAL);
		}

		if (dddCelular != null && foneCelular == null) {
			throw new ApplicationBusinessException(
					PessoaFisicaONExceptionCode.ERRO_INFORMAR_DDD_TELEFONE_CELULAR);
		}

		if (dddCelular == null && foneCelular != null) {
			throw new ApplicationBusinessException(
					PessoaFisicaONExceptionCode.ERRO_INFORMAR_TELEFONE_CELULAR);
		}

		if (dddPager != null && fonePager == null) {
			throw new ApplicationBusinessException(
					PessoaFisicaONExceptionCode.ERRO_INFORMAR_DDD_TELEFONE_PAGER);
		}

		if (dddPager == null && fonePager != null) {
			throw new ApplicationBusinessException(
					PessoaFisicaONExceptionCode.ERRO_INFORMAR_TELEFONE_PAGER);
		}

	}

	private void verificarCamposObrigatorios(RapPessoasFisicas pessoaFisica)
			throws ApplicationBusinessException {

		if(pessoaFisica.getAipNacionalidades() == null){
			throw new ApplicationBusinessException(
					PessoaFisicaONExceptionCode.ERRO_NACIONALIDADE_OBRIGATORIA);
		}
		
		Integer codigoNacionalidade = pessoaFisica.getAipNacionalidades()
				.getCodigo();

		if (codigoNacionalidade != null) {

			AghParametros paramCodigoNacionalidade = getParametroFacade()
					.buscarAghParametro(AghuParametrosEnum.P_COD_NAC);

			if (codigoNacionalidade == paramCodigoNacionalidade
					.getVlrNumerico().intValue()
					&& (pessoaFisica.getCpf() == null || StringUtils
							.isBlank(pessoaFisica.getNroIdentidade()))) {
				throw new ApplicationBusinessException(
						PessoaFisicaONExceptionCode.ERRO_RG_CPF_OBRIGATORIOS);
			}

		}

		Integer nroCartProfissional = pessoaFisica.getNroCartProfissional();
		String serieCartProfissional = pessoaFisica.getSerieCartProfissional();

		if ((nroCartProfissional != null && StringUtils
				.isBlank(serieCartProfissional))
				|| (nroCartProfissional == null && !StringUtils
						.isBlank(serieCartProfissional))) {
			throw new ApplicationBusinessException(
					PessoaFisicaONExceptionCode.ERRO_CARTEIRA_SERIE_OBRIGATORIOS);
		}

		Long nroTitEleitor = pessoaFisica.getNroTitEleitor();
		Short secaoTitEleitor = pessoaFisica.getSecaoTitEleitor();
		Short zonaTitEleitor = pessoaFisica.getZonaTitEleitor();

		if ((nroTitEleitor != null && (secaoTitEleitor == null || zonaTitEleitor == null))
				|| (secaoTitEleitor != null && (nroTitEleitor == null || zonaTitEleitor == null))
				|| (zonaTitEleitor != null && (nroTitEleitor == null || secaoTitEleitor == null))) {
			throw new ApplicationBusinessException(
					PessoaFisicaONExceptionCode.ERRO_TITULO_SECAO_ZONA_ELEITOR_OBRIGATORIOS);
		}

	}

	private void validarDtNascimento(Date dataNascimento)
			throws ApplicationBusinessException {

		Date dataAtual = new Date();

		if (dataNascimento.after(dataAtual)) {
			throw new ApplicationBusinessException(
					PessoaFisicaONExceptionCode.ERRO_DATA_NASCIMENTO_FUTURA);
		}

	}

	private void ajustarNomes(RapPessoasFisicas rapPessoaFisica)
			throws ApplicationBusinessException {

		String nomeAjustado = FonetizadorUtil.ajustarNome(rapPessoaFisica
				.getNome());
		if (StringUtils.isBlank(nomeAjustado)) {
			throw new ApplicationBusinessException(
					PessoaFisicaONExceptionCode.ERRO_CARACTERES_ESPECIAIS_NOME);
		}
		rapPessoaFisica.setNome(nomeAjustado);

		nomeAjustado = FonetizadorUtil
				.ajustarNome(rapPessoaFisica.getNomeMae());
		if (StringUtils.isBlank(nomeAjustado)) {
			throw new ApplicationBusinessException(
					PessoaFisicaONExceptionCode.ERRO_CARACTERES_ESPECIAIS_NOME_MAE);
		}
		rapPessoaFisica.setNomeMae(nomeAjustado);

		if (!StringUtils.isBlank(rapPessoaFisica.getNomePai())) {
			nomeAjustado = FonetizadorUtil.ajustarNome(rapPessoaFisica
					.getNomePai());
			rapPessoaFisica.setNomePai(nomeAjustado);
		}

	}

	@SuppressWarnings("PMD.NPathComplexity")
	private void validarEndereco(RapPessoasFisicas pessoaFisica)
			throws ApplicationBusinessException {

		if (pessoaFisica.getAipBairrosCepLogradouro() == null
				&& pessoaFisica.getAipCidades() == null
				&& StringUtils.isBlank(pessoaFisica.getLogradouro())
				&& pessoaFisica.getCep() == null
				&& StringUtils.isBlank(pessoaFisica.getBairro())) {
			throw new ApplicationBusinessException(
					PessoaFisicaONExceptionCode.ERRO_ENDERECO_NAO_INFORMADO);
		}

		/*
		 * if (pessoaFisica.getAipBairrosCepLogradouro() != null &&
		 * (pessoaFisica.getAipCidades() != null ||
		 * !StringUtils.isBlank(pessoaFisica.getLogradouro()) ||
		 * pessoaFisica.getCep() != null ||
		 * !StringUtils.isBlank(pessoaFisica.getBairro()))) { throw new
		 * ApplicationBusinessException(
		 * PessoaFisicaONExceptionCode.ERRO_ENDERECO_CADASTRADO_NAO_CADASTRADO);
		 * }
		 */

		if (pessoaFisica.getAipCidades() == null
				&& (!StringUtils.isBlank(pessoaFisica.getLogradouro())
						|| pessoaFisica.getCep() != null || !StringUtils
						.isBlank(pessoaFisica.getBairro()))) {
			throw new ApplicationBusinessException(
					PessoaFisicaONExceptionCode.ERRO_CIDADE_ENDERECO_NAO_CADASTRADO);
		}

		if (pessoaFisica.getAipBairrosCepLogradouro() != null
				&& pessoaFisica.getAipBairrosCepLogradouro().getCepLogradouro() != null) {

			DominioLadoEndereco ladoEndereco = pessoaFisica
					.getAipBairrosCepLogradouro().getCepLogradouro().getLado();
			Integer nroLogradouro = pessoaFisica.getNroLogradouro();
			String nroInicial = pessoaFisica.getAipBairrosCepLogradouro().getCepLogradouro().getNroInicial();
			String nroFinal = pessoaFisica.getAipBairrosCepLogradouro().getCepLogradouro().getNroFinal();

			if (nroLogradouro != null) {

				if (ladoEndereco != null) {
					if (ladoEndereco == DominioLadoEndereco.I
							&& (nroLogradouro % 2 == 0)) {
						throw new ApplicationBusinessException(
								PessoaFisicaONExceptionCode.ERRO_NUMERO_LOGRADOURO_PAR);
					}
					if (ladoEndereco == DominioLadoEndereco.P
							&& (nroLogradouro % 2 != 0)) {
						throw new ApplicationBusinessException(
								PessoaFisicaONExceptionCode.ERRO_NUMERO_LOGRADOURO_IMPAR);
					}

				}

				if (nroInicial != null) {
					nroInicial = nroInicial.replaceAll("\\.", ",");
					avaliaSeNroLogradouroMaior(nroLogradouro, nroInicial.split(","));
				}
				
				if (nroFinal != null) {
					nroFinal = nroFinal.replaceAll("\\.", ",");
					avaliaSeNroLogradouroMenor(nroLogradouro, nroFinal.split(","));
				}

			}

			pessoaFisica.setLogradouro(null);
			pessoaFisica.setBairro(null);
			pessoaFisica.setCep(null);

		} else {

			Integer cepNaoCadastrado = pessoaFisica.getCep();
			if (cepNaoCadastrado != null) {
				List<AipBairrosCepLogradouro> lista = getCadastroPacienteFacade().pesquisarCeps(
						cepNaoCadastrado, null);
				if (!lista.isEmpty()) {

					// limpar as informações do logradouro não cadastrado quando
					// houver esta crítica
					pessoaFisica.setLogradouro(null);
					pessoaFisica.setBairro(null);
					pessoaFisica.setCep(null);

					throw new ApplicationBusinessException(
							PessoaFisicaONExceptionCode.ERRO_CEP_CADASTRADO);

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
							PessoaFisicaONExceptionCode.ERRO_NUMERO_LOGRADOURO_ABAIXO_FAIXA_CEP, nro);
				}
			} else {
				if (nroReferencia > nro) {
					throw new ApplicationBusinessException(
							PessoaFisicaONExceptionCode.ERRO_NUMERO_LOGRADOURO_ACIMA_FAIXA_CEP, nro);
				}				
			}
		}
	}
	
	/**
	 * ORADB: Trigger RAPT_PES_ARU
	 * 
	 * @param velho
	 * @param novo
	 */
	// @Transactional(TransactionPropagationType.REQUIRED)
	private void journalUpdate(RapPessoasFisicas velho, RapPessoasFisicas novo) {

		if (dadosAlterados(velho, novo)) {

			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			RapPessoasFisicasJn pessoaFisicaJn = BaseJournalFactory
					.getBaseJournal(DominioOperacoesJournal.UPD,
							RapPessoasFisicasJn.class, servidorLogado != null ? servidorLogado.getUsuario() : null);

			pessoaFisicaJn.setCodigo(velho.getCodigo());
			pessoaFisicaJn.setCpf(velho.getCpf());
			pessoaFisicaJn.setCriadoEm(velho.getCriadoEm());
			pessoaFisicaJn.setNome(velho.getNome());
			pessoaFisicaJn.setNomeMae(velho.getNomeMae());
			if (velho.getAipPacientes() != null) {
				pessoaFisicaJn.setPacProntuario(velho.getAipPacientes()
						.getProntuario());
			}
			pessoaFisicaJn.setNomePai(velho.getNomePai());
			pessoaFisicaJn.setDtNascimento(velho.getDtNascimento());
			pessoaFisicaJn.setSexo(velho.getSexo());
			pessoaFisicaJn.setNomeUsual(velho.getNomeUsual());
			pessoaFisicaJn.setGrauInstrucao(velho.getGrauInstrucao());
			pessoaFisicaJn.setEstadoCivil(velho.getEstadoCivil());
			if (velho.getAipBairrosCepLogradouro() != null) {
				pessoaFisicaJn.setBclBaiCodigo(velho
						.getAipBairrosCepLogradouro().getAipBairro()
						.getCodigo());
				pessoaFisicaJn.setBclCloCep(velho.getAipBairrosCepLogradouro()
						.getCepLogradouro().getId().getCep());
				pessoaFisicaJn.setBclCloLgrCodigo(velho
						.getAipBairrosCepLogradouro().getCepLogradouro()
						.getId().getLgrCodigo());
			}
			if (velho.getAipCidades() != null) {
				pessoaFisicaJn.setCddCodigo(velho.getAipCidades().getCodigo());
			}
			if (velho.getAipNacionalidades() != null) {
				pessoaFisicaJn.setNacCodigo(velho.getAipNacionalidades()
						.getCodigo());
			}
			pessoaFisicaJn.setLogradouro(velho.getLogradouro());
			pessoaFisicaJn.setComplLogradouro(velho.getComplLogradouro());
			pessoaFisicaJn.setNroLogradouro(velho.getNroLogradouro());
			pessoaFisicaJn.setCep(velho.getCep());
			pessoaFisicaJn.setBairro(velho.getBairro());
			pessoaFisicaJn.setCidadeNascimento(velho.getCidadeNascimento());
			if (velho.getAipUfs() != null) {
				pessoaFisicaJn.setUfSigla(velho.getAipUfs().getSigla());
			}
			pessoaFisicaJn.setNroIdentidade(velho.getNroIdentidade());
			pessoaFisicaJn.setNroCartProfissional(velho
					.getNroCartProfissional());
			pessoaFisicaJn.setSerieCartProfissional(velho
					.getSerieCartProfissional());
			pessoaFisicaJn.setPisPasep(velho.getPisPasep());
			pessoaFisicaJn.setNroTitEleitor(velho.getNroTitEleitor());
			pessoaFisicaJn.setZonaTitEleitor(velho.getZonaTitEleitor());
			pessoaFisicaJn.setSecaoTitEleitor(velho.getSecaoTitEleitor());
			pessoaFisicaJn.setDddFoneResidencial(velho.getDddFoneResidencial());
			pessoaFisicaJn.setFoneResidencial(velho.getFoneResidencial());
			pessoaFisicaJn.setRamalFoneResidencial(velho
					.getRamalFoneResidencial());
			pessoaFisicaJn.setDddFoneCelular(velho.getDddFoneCelular());
			pessoaFisicaJn.setFoneCelular(velho.getFoneCelular());
			pessoaFisicaJn.setDddFonePagerBip(velho.getDddFonePagerBip());
			pessoaFisicaJn.setFonePagerBip(velho.getFonePagerBip());

			RapPessoasFisicasJnDAO rapPessoasFisicasJnDAO = this.getRapPessoasFisicasJnDAO();
			rapPessoasFisicasJnDAO.persistir(pessoaFisicaJn);
		}
	}

	/**
	 * ORADB: Trigger RAPT_PES_BRU
	 * 
	 * @param pessoaOrigem
	 * @param pessoaFisica
	 */

	private void validarAtualizacaoPessoa(final RapPessoasFisicas pessoaOrigem,
			final RapPessoasFisicas pessoaFisica) throws ApplicationBusinessException {
		
		boolean permiteAlterar = false;

		if (pessoaFisica == null) {
			return;
		}


		// TODO Verificar se o banco é de Produção
		// para realizar as consistências do método
		// IF AGHC_AMBIENTE = 'P'

		if (this.dadosAlterados(pessoaOrigem, pessoaFisica)) {

			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			

			// Verifica se algum vínculo da pessoa não pode ser alterado pelo
			// usuário corrente
			List<RapServidores> lista = getRapServidoresDAO().pesquisarRapServidoresPorCodigoPessoa(pessoaFisica.getCodigo());

			if (lista.isEmpty()) {
				permiteAlterar = true;
			} else {
				for (RapServidores servidores : lista) {
					permiteAlterar = getObjetosOracleDAO().perfilVinculo(servidores.getVinculo().getCodigo(), servidorLogado != null ? servidorLogado.getUsuario() : null);
				}
			}

			if (!permiteAlterar) {
				throw new ApplicationBusinessException(
						PessoaFisicaONExceptionCode.ERRO_USUARIO_SEM_PRIVILEGIO);
			}
		}
	}

	private boolean dadosAlterados(RapPessoasFisicas velho,
			RapPessoasFisicas novo) {

		boolean alterouDados = false;

		if (!CoreUtil.igual(velho.getCodigo(), novo.getCodigo())
				|| !CoreUtil.igual(velho.getCpf(), novo.getCpf())
				|| !CoreUtil.igual(velho.getNome(), novo.getNome())
				|| !CoreUtil.igual(velho.getNomeMae(), novo.getNomeMae())
				|| !CoreUtil.igual(velho.getAipPacientes(),
						novo.getAipPacientes())
				|| !CoreUtil.igual(velho.getNomePai(), novo.getNomePai())
				|| !CoreUtil.igual(velho.getDtNascimento(),
						novo.getDtNascimento())
				|| !CoreUtil.igual(velho.getSexo(), novo.getSexo())
				|| !CoreUtil.igual(velho.getNomeUsual(), novo.getNomeUsual())
				|| !CoreUtil.igual(velho.getGrauInstrucao(),
						novo.getGrauInstrucao())
				|| !CoreUtil.igual(velho.getEstadoCivil(),
						novo.getEstadoCivil())
				|| !CoreUtil.igual(velho.getAipBairrosCepLogradouro(),
						novo.getAipBairrosCepLogradouro())
				|| !CoreUtil.igual(velho.getAipCidades(), novo.getAipCidades())
				|| !CoreUtil.igual(velho.getAipNacionalidades().getCodigo(),
						novo.getAipNacionalidades().getCodigo())
				|| !CoreUtil.igual(velho.getLogradouro(), novo.getLogradouro())
				|| !CoreUtil.igual(velho.getComplLogradouro(),
						novo.getComplLogradouro())
				|| !CoreUtil.igual(velho.getNroLogradouro(),
						novo.getNroLogradouro())
				|| !CoreUtil.igual(velho.getCep(), novo.getCep())
				|| !CoreUtil.igual(velho.getBairro(), novo.getBairro())
				|| !CoreUtil.igual(velho.getCidadeNascimento(),
						novo.getCidadeNascimento())
				|| !CoreUtil.igual(velho.getAipUfs(), novo.getAipUfs())
				|| !CoreUtil.igual(velho.getNroIdentidade(),
						novo.getNroIdentidade())
				|| !CoreUtil.igual(velho.getNroCartProfissional(),
						novo.getNroCartProfissional())
				|| !CoreUtil.igual(velho.getSerieCartProfissional(),
						novo.getSerieCartProfissional())
				|| !CoreUtil.igual(velho.getPisPasep(), novo.getPisPasep())
				|| !CoreUtil.igual(velho.getNroTitEleitor(),
						novo.getNroTitEleitor())
				|| !CoreUtil.igual(velho.getZonaTitEleitor(),
						novo.getZonaTitEleitor())
				|| !CoreUtil.igual(velho.getSecaoTitEleitor(),
						novo.getSecaoTitEleitor())
				|| !CoreUtil.igual(velho.getDddFoneResidencial(),
						novo.getDddFoneResidencial())
				|| !CoreUtil.igual(velho.getFoneResidencial(),
						novo.getFoneResidencial())
				|| !CoreUtil.igual(velho.getRamalFoneResidencial(),
						novo.getRamalFoneResidencial())
				|| !CoreUtil.igual(velho.getDddFoneCelular(),
						novo.getDddFoneCelular())
				|| !CoreUtil.igual(velho.getFoneCelular(),
						novo.getFoneCelular())
				|| !CoreUtil.igual(velho.getDddFonePagerBip(),
						novo.getDddFonePagerBip())
				|| !CoreUtil.igual(velho.getFonePagerBip(),
						novo.getFonePagerBip())
				|| !CoreUtil.igual(velho.getNroPagerBip(),
						novo.getNroPagerBip())
				|| !CoreUtil.igual(velho.getEmailParticular(),
						novo.getEmailParticular())) {

			alterouDados = true;

		}
		return alterouDados;
	}
	
	protected RapServidoresDAO getRapServidoresDAO() {
		return rapServidoresDAO;
	}
	
	public boolean existeVinculoDependentesPessoa(Integer codigo) throws ApplicationBusinessException {
		if (codigo == null) {
			throw new ApplicationBusinessException(
					PessoaFisicaONExceptionCode.MENSAGEM_PARAMETRO_NAO_INFORMADO);
		}
		List<RapVinculos> listaVinculos = getRapServidoresDAO().obterVinculosPessoa(codigo);
		for (RapVinculos rapVinculos : listaVinculos) {
			if (rapVinculos.getIndDependente().isSim()) {
				return true;
			}
		}
		return false;
	}
	
	protected ObjetosOracleDAO getObjetosOracleDAO() {
		return objetosOracleDAO;
	}
	
	protected RapPessoasFisicasDAO getRapPessoasFisicasDAO() {
		return rapPessoasFisicasDAO;
	}

	protected RapPessoasFisicasJnDAO getRapPessoasFisicasJnDAO() {
		return rapPessoasFisicasJnDAO;
	}

	protected RapDependentesDAO getRapDependentesDAO() {
		return rapDependentesDAO;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected ICadastroPacienteFacade getCadastroPacienteFacade() {
		return cadastroPacienteFacade;
	}

	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
