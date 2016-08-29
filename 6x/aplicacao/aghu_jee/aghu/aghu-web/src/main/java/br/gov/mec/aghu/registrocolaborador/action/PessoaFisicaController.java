package br.gov.mec.aghu.registrocolaborador.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipBairrosCepLogradouro;
import br.gov.mec.aghu.model.AipCidades;
import br.gov.mec.aghu.model.AipNacionalidades;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.AipUfs;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.cadastro.business.ICadastroPacienteFacade;
import br.gov.mec.aghu.paciente.cadastrosbasicos.business.ICadastrosBasicosPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class PessoaFisicaController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(PessoaFisicaController.class);

	private static final long serialVersionUID = 5276855123193650365L;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private IPacienteFacade pacienteFacade;

	@EJB
	private ICadastrosBasicosPacienteFacade cadastrosBasicosPacienteFacade;

	@EJB
	private ICadastroPacienteFacade cadastroPacienteFacade;

	@EJB
	private IParametroFacade parametroFacade;

	private RapPessoasFisicas rapPessoaFisica = new RapPessoasFisicas();

	private AipCidades cidadeCadastrada;

	private Integer codigoPessoa;

	private Integer cloCep;

	private Integer codigoNovaPessoa;

	private Boolean openedLogradouroCadastrado = true;

	/**
	 * Nome sugerido de uma pessoa a qual foi procurada na pesquisa de pessoas
	 */

	private String nomePesquisar;

	@Inject
	private PessoaFisicaPaginatorController pessoaFisicaPaginatorController;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	public String iniciar() {

		if (codigoPessoa != null) {
			try {
				rapPessoaFisica = registroColaboradorFacade.obterPessoaFisicaCRUD(codigoPessoa);

				if (rapPessoaFisica == null) {
					apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
					return cancelarCadastro();
				}

				if (rapPessoaFisica.getAipBairrosCepLogradouro() != null
						&& rapPessoaFisica.getAipBairrosCepLogradouro().getCepLogradouro() != null
						&& rapPessoaFisica.getAipBairrosCepLogradouro().getCepLogradouro().getLogradouro() != null) {
					cidadeCadastrada = rapPessoaFisica.getAipBairrosCepLogradouro().getCepLogradouro().getLogradouro()
							.getAipCidade();
				}

			} catch (ApplicationBusinessException ex) {
				apresentarExcecaoNegocio(ex);
			}
		} else {
			if (StringUtils.isNotBlank(nomePesquisar) && StringUtils.isBlank(rapPessoaFisica.getNome())) {
				rapPessoaFisica.setNome(nomePesquisar);
			}
		}

		return null;

	}

	public List<AipUfs> pesquisarUFs(String parametro) {
		String valor = null;
		if (parametro != null) {
			valor = String.valueOf(parametro);
		}
		return this.cadastrosBasicosPacienteFacade.pesquisarPorSiglaEntaoNome(StringUtils.trimToNull(valor));
	}

	public List<AipNacionalidades> pesquisarNacionalidades(String parametro) {
		String valor = null;
		if (parametro != null) {
			valor = String.valueOf(parametro);
		}
		return cadastrosBasicosPacienteFacade.pesquisarNacionalidadesPessoaFisica(StringUtils.trimToNull(valor));
	}

	public List<AipPacientes> pesquisarPacientes(String objParam) {

		String strPesquisa = (String) objParam;
		// return
		// pacienteON.pesquisarPacientesPorProntuarioOuCodigo(strPesquisa);
		return pacienteFacade.obterPacientesPorProntuarioOuCodigo(strPesquisa);
	}

	public List<AipCidades> pesquisarCidades(String objParam) {
		return cadastrosBasicosPacienteFacade.pesquisarPorCodigoNome(objParam, true);
	}

	public List<AipBairrosCepLogradouro> buscarEnderecosCep(String param) throws ApplicationBusinessException {
		String auxParam = (String) param;
		List<AipBairrosCepLogradouro> listaBairrosCepLogradouro = new ArrayList<AipBairrosCepLogradouro>();

		if (CoreUtil.isNumeroInteger(auxParam)) {
			Integer cep = Integer.valueOf(auxParam);
			Integer codigoCidade = null;

			if (this.cidadeCadastrada != null) {
				codigoCidade = this.cidadeCadastrada.getCodigo();
			}

			try {
				listaBairrosCepLogradouro = cadastroPacienteFacade.pesquisarCeps(cep, codigoCidade);
			} catch (ApplicationBusinessException e) {
				LOG.error(e.getMessage(), e);
				this.apresentarExcecaoNegocio(e);
			}
		}

		return listaBairrosCepLogradouro;
	}

	public List<AipBairrosCepLogradouro> listarLogradourosPorTipoTituloNome(String valor)
			throws ApplicationBusinessException {

		Integer codigoCidade = null;

		if (this.cidadeCadastrada != null) {
			codigoCidade = this.cidadeCadastrada.getCodigo();
		}

		List<AipBairrosCepLogradouro> lista = new ArrayList<AipBairrosCepLogradouro>();
		try {
			lista = this.cadastroPacienteFacade.pesquisarLogradourosCepPorDescricaoCidade(valor.toUpperCase(),
					codigoCidade);
		} catch (ApplicationBusinessException e) {
			LOG.error(e.getMessage(), e);
			this.apresentarExcecaoNegocio(e);
		}
		return lista;
	}

	public void limparCepSelecionado() {

		this.rapPessoaFisica.setAipBairrosCepLogradouro(null);
		this.cidadeCadastrada = null;

	}

	public void setarCepSelecionado() {
		if (rapPessoaFisica.getAipBairrosCepLogradouro() != null) {
			cidadeCadastrada = rapPessoaFisica.getAipBairrosCepLogradouro().getCepLogradouro().getLogradouro()
					.getAipCidade();
		}
	}

	public String cancelarCadastro() {
		nomePesquisar = null;
		this.limparCadastro();
		pessoaFisicaPaginatorController.pesquisar();
		return "pesquisarPessoaFisica";
	}

	private void limparCadastro() {
		registroColaboradorFacade.desatachar(rapPessoaFisica);
		this.rapPessoaFisica = new RapPessoasFisicas();
		this.codigoPessoa = null;
		this.cidadeCadastrada = null;
	}

	public boolean nacionalidadeBrasileira() {
		if (rapPessoaFisica != null && rapPessoaFisica.getAipNacionalidades() != null) {
			Integer codigoNacionalidade = rapPessoaFisica.getAipNacionalidades().getCodigo();

			AghParametros paramCodigoNacionalidade;
			try {
				paramCodigoNacionalidade = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COD_NAC);
				if (codigoNacionalidade == paramCodigoNacionalidade.getVlrNumerico().intValue()) {
					return true;
				}
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		}
		return false;
	}

/*Naturalidade*/
	
	public List<AipCidades> pesquisarCidadePorCodigoNome(String paramPesquisa) {
		if(!pesquisaNaturalidadeOrdemAlfabetica()){
			return cadastrosBasicosPacienteFacade.pesquisarPorCodigoNome(paramPesquisa, true);
		}else{
			return cadastrosBasicosPacienteFacade.pesquisarPorCodigoNomeAlfabetica(paramPesquisa, true);
		}
	}
	
	public boolean pesquisaNaturalidadeOrdemAlfabetica() {
		boolean retorno = false;
		
		try {
			AghParametros aghParamRN = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_PESQUISA_NATURALIDADE_EM_ORDEM_ALFABETICA);
			String paramObrigaRN = aghParamRN.getVlrTexto();
			return "S".equalsIgnoreCase(paramObrigaRN);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return retorno;
	}
	
	public Long pesquisarCountCidadePorCodigoNome(Object paramPesquisa) {
		return cadastrosBasicosPacienteFacade.pesquisarCountCidadePorCodigoNome((String)paramPesquisa);
	}
	
	/*UF*/
	
	public String getDescricaoUFSelecionada() {
		if (this.rapPessoaFisica == null
				|| this.rapPessoaFisica.getAipCidades() == null
				|| StringUtils.isBlank(this.rapPessoaFisica.getAipCidades().getNome())) {
			return "";
		}
		return this.rapPessoaFisica.getAipCidades().getAipUf().getSigla();
	}
	
	/*Naturalidade*/
	public IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	public String salvar() {
		String retorno = null;
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		try {

			associarInfoAuditoria();

			if (rapPessoaFisica.getCodigo() != null) {

				// Necessário para forçar null nos campos que retornaram vazio
				// ("") da tela
				// Não estava conseguindo comparar corretamente no momento de
				// adicionar da journal
				ajustarCampos(rapPessoaFisica);

				registroColaboradorFacade.alterar(rapPessoaFisica, servidorLogado);
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_PESSOA_FISICA_ALTERADA_COM_SUCESSO");
				return cancelarCadastro();

			} else {

				// cidade = cidadeCRUD.obterCidadePorCodigo(3915);
				// rapPessoaFisica.setAipCidades(cidade);

				// chamar o método de persistir
				registroColaboradorFacade.salvar(rapPessoaFisica);
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_PESSOA_FISICA_INCLUIDA_COM_SUCESSO");
				codigoNovaPessoa = rapPessoaFisica.getCodigo();
				return cancelarCadastro();
			}
		} catch (BaseException ex) {
			apresentarExcecaoNegocio(ex);
		}
		return retorno;
	}

	private void ajustarCampos(RapPessoasFisicas pessoaFisica) {

		if (StringUtils.isBlank(pessoaFisica.getNomePai())) {
			pessoaFisica.setNomePai(null);
		}

		if (StringUtils.isBlank(pessoaFisica.getNomeUsual())) {
			pessoaFisica.setNomeUsual(null);
		}

		if (StringUtils.isBlank(pessoaFisica.getCidadeNascimento())) {
			pessoaFisica.setCidadeNascimento(null);
		}

		if (StringUtils.isBlank(pessoaFisica.getEmailParticular())) {
			pessoaFisica.setEmailParticular(null);
		}

		if (StringUtils.isBlank(pessoaFisica.getNomeUsual())) {
			pessoaFisica.setNomeUsual(null);
		}

		if (StringUtils.isBlank(pessoaFisica.getNroIdentidade())) {
			pessoaFisica.setNroIdentidade(null);
		}

		if (StringUtils.isBlank(pessoaFisica.getSerieCartProfissional())) {
			pessoaFisica.setSerieCartProfissional(null);
		}

	}

	private void associarInfoAuditoria() {
		RapServidores servidorLogado;
		try {
			servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado());
			this.rapPessoaFisica.setRapServidores(servidorLogado);
			this.rapPessoaFisica.setCriadoEm(new Date());
		} catch (ApplicationBusinessException e) {
			LOG.error(e.getMessage(), e);
			this.apresentarExcecaoNegocio(e);
		}

	}

	public RapPessoasFisicas getRapPessoaFisica() {
		return rapPessoaFisica;
	}

	public void setRapPessoaFisica(RapPessoasFisicas rapPessoaFisica) {
		this.rapPessoaFisica = rapPessoaFisica;
	}

	public Integer getCodigoPessoa() {
		return codigoPessoa;
	}

	public void setCodigoPessoa(Integer codigoPessoa) {
		this.codigoPessoa = codigoPessoa;
	}

	public Integer getCloCep() {
		return cloCep;
	}

	public void setCloCep(Integer cloCep) {
		this.cloCep = cloCep;
	}

	public AipCidades getCidadeCadastrada() {
		return cidadeCadastrada;
	}

	public void setCidadeCadastrada(AipCidades cidadeCadastrada) {
		this.cidadeCadastrada = cidadeCadastrada;
	}

	public void setCodigoNovaPessoa(Integer codigoNovaPessoa) {
		this.codigoNovaPessoa = codigoNovaPessoa;
	}

	public Integer getCodigoNovaPessoa() {
		return codigoNovaPessoa;
	}

	public void setOpenedLogradouroCadastrado(Boolean openedLogradouroCadastrado) {
		this.openedLogradouroCadastrado = openedLogradouroCadastrado;
	}

	public Boolean getOpenedLogradouroCadastrado() {
		return openedLogradouroCadastrado;
	}

	public String getNomePesquisar() {
		return nomePesquisar;
	}

	public void setNomePesquisar(String nomePesquisar) {
		this.nomePesquisar = nomePesquisar;
	}

}
