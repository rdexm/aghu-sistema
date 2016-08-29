package br.gov.mec.aghu.paciente.cadastro.action;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipCidades;
import br.gov.mec.aghu.model.AipEnderecosPacientes;
import br.gov.mec.aghu.model.AipEnderecosPacientesId;
import br.gov.mec.aghu.model.VAipCeps;
import br.gov.mec.aghu.paciente.cadastro.business.ICadastroPacienteFacade;
import br.gov.mec.aghu.paciente.cadastrosbasicos.business.ICadastrosBasicosPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe controladora na tela de cadastro/edição de endereco.
 * 
 * @author gmneto
 * 
 */
@SuppressWarnings("PMD.AghuTooManyMethods")
public class EnderecoController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8027740802050518144L;

	private static final Log LOG = LogFactory.getLog(EnderecoController.class);

	private static final String ORIGEM_LOGRADOURO = "ORIGEM_LOGRADOURO";
	private static final String ORIGEM_CEP = "ORIGEM_CEP";

	@EJB
	private ICadastroPacienteFacade cadastroPacienteFacade;
	
	@EJB
	private ICadastrosBasicosPacienteFacade cadastrosBasicosPacienteFacade;
	
	@EJB
	private IParametroFacade parametroFacade;

	/**
	 * Endereco sendo incluído ou editado.
	 */
	private AipEnderecosPacientes endereco;

	private AipEnderecosPacientes enderecoEdicao = new AipEnderecosPacientes();

	private Short sequencialEndereco;

	private VAipCeps cepCadastrado;
	
	private VAipCeps cepNaoCadastrado;

	private VAipCeps cepLogradouro;
	
	private AipCidades cidadeEnderecoCadastrado;
	
	private AipCidades cidadeEnderecoNaoCadastrado;

	private Boolean openedLogradouroCadastrado = true;
	
	private Boolean edicao = false;
	
	private Boolean habilitaCamposLogradouroLivre = false;
	
	private Boolean enderecoNaoLocalizado = Boolean.FALSE;
	
	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	public void inicio() throws Exception {

		this.enderecoEdicao = (AipEnderecosPacientes) BeanUtils.cloneBean(endereco);
		
		if (enderecoEdicaoIsNovo() || enderecoEdicao.isLogradouroCadastrado()) {
			
			
			if (this.enderecoEdicao.getAipBairrosCepLogradouro() != null && !this.enderecoNaoLocalizado){
				this.setCepCadastrado(cadastroPacienteFacade.obterVAipCeps(
						this.enderecoEdicao.getAipBairrosCepLogradouro().getId().getCloCep(),
						this.enderecoEdicao.getAipBairrosCepLogradouro().getId().getCloLgrCodigo(),
						this.enderecoEdicao.getAipBairrosCepLogradouro().getId().getBaiCodigo()));
			}
			
			setOpenedLogradouroCadastrado(true);
			this.habilitaCamposLogradouroLivre = false;
			this.enderecoNaoLocalizado = false;
			
		} else {

			this.setCepCadastrado(cadastroPacienteFacade.obterVAipCeps(
					this.enderecoEdicao.getCep(), null, null));
			this.setCidadeEnderecoCadastrado(this.enderecoEdicao.getAipCidade());
			this.habilitaCamposLogradouroLivre = true;
			this.enderecoNaoLocalizado = Boolean.TRUE;
			setOpenedLogradouroCadastrado(false);
		}
		
		if (this.getCepCadastrado() != null && !this.enderecoNaoLocalizado){
			this.cidadeEnderecoCadastrado = this.getCepCadastrado().getAipCidade();
		}
		
		if (sequencialEndereco == null) {
			this.sequencialEndereco = this.cadastroPacienteFacade.obterSeqEnderecoPacienteAtual(this.endereco.getAipPaciente());
		}
		this.setCepLogradouro(getCepCadastrado());
		this.setEdicao(false);
	}

	/** 
	 * Verifica se o endereço em edição é novo ou não
	 * Para isso verifica se tanto logradouro como cidade estão vazios
	 */
	private boolean enderecoEdicaoIsNovo() {
		return 
			StringUtils.isEmpty(enderecoEdicao.getLogradouro()) &&
			StringUtils.isEmpty(enderecoEdicao.getCidade())
		;
	}
	
	/**
	 * Método usado para incluir/atualizar o endereço na listade endereços do
	 * paciente.
	 */
	public Boolean salvarEnderecoCadastrado() {

		try {

			if (cepCadastrado != null) {
				enderecoEdicao.setCep(this.cepCadastrado.getId().getCep());
				enderecoEdicao.setvAipCep(cepCadastrado);
			}

			this.enderecoEdicao.setCidade(null);
			this.enderecoEdicao.setLogradouro(null);
			this.enderecoEdicao.setBairro(null);
			
			this.enderecoEdicao.setAipBairrosCepLogradouro(this.cepCadastrado.getAipBairrosCepLogradouro());
			this.enderecoEdicao.setvAipCep(this.cepCadastrado);

			this.cadastroPacienteFacade.validarEndereco(this.enderecoEdicao, true);

			this.endereco.setAipBairrosCepLogradouro(enderecoEdicao
					.getAipBairrosCepLogradouro());
			this.endereco.setBclCloLgrCodigo(this.endereco.getAipBairrosCepLogradouro().getId().getCloLgrCodigo());
			this.endereco.setAipCidade(this.enderecoEdicao.getAipCidade());
			this.endereco.setAipLogradouro(this.enderecoEdicao
					.getAipLogradouro());
			this.endereco.setAipUf(this.enderecoEdicao.getAipUf());
			this.endereco.setBairro(this.enderecoEdicao.getBairro());
			this.endereco.setCep(this.enderecoEdicao.getCep());
			this.endereco.setCidade(this.enderecoEdicao.getCidade());
			this.endereco.setComplLogradouro(this.enderecoEdicao
					.getComplLogradouro());
			this.endereco.setLogradouro(this.enderecoEdicao.getLogradouro());
			this.endereco.setPadrao(this.enderecoEdicao.isPadrao());
			this.endereco
					.setTipoEndereco(this.enderecoEdicao.getTipoEndereco());
			this.endereco.setNroLogradouro(this.enderecoEdicao
					.getNroLogradouro());

			if (this.endereco.getId() == null) {
				AipEnderecosPacientesId enderecoPacienteId = new AipEnderecosPacientesId();
				enderecoPacienteId.setPacCodigo(this.endereco.getAipPaciente()
						.getCodigo());
				sequencialEndereco++;
				enderecoPacienteId.setSeqp(sequencialEndereco);
				this.endereco.setId(enderecoPacienteId);

			}

		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);

			return false;
		}

		this.cidadeEnderecoCadastrado = null;
		this.cepCadastrado = null;
		this.cepLogradouro = null;
		this.enderecoEdicao = null;
		return true;

	}

	/**
	 * Método usado para incluir/atualizar o endereço na listade endereços do
	 * paciente.
	 */
	public Boolean salvarEnderecoNaoCadastrado() {

		try {

			if (cepCadastrado != null || this.enderecoEdicao.getCep() != null && renderizaEndereco() && !habilitaCamposLogradouroLivre) {
				//cadastroPacienteFacade.validarCepEnderecoNaoCadastrado(this.enderecoEdicao.getCep());
				enderecoEdicao.setCep(this.enderecoEdicao.getCep());
				//enderecoEdicao.setvAipCep(cepNaoCadastrado);
			} 
			if (cepCadastrado != null && habilitaCamposLogradouroLivre && !renderizaEndereco()){
				cadastroPacienteFacade.validarCepEnderecoNaoCadastrado(this.cepCadastrado.getId().getCep());
				enderecoEdicao.setCep(this.cepCadastrado.getId().getCep());
				enderecoEdicao.setvAipCep(cepCadastrado);
			}
			
			if(cidadeEnderecoCadastrado != null){
				enderecoEdicao.setAipCidade(cidadeEnderecoCadastrado);
				enderecoEdicao.setCidade(null);
			}
			
			this.enderecoEdicao.setAipBairrosCepLogradouro(null);
			
			cadastroPacienteFacade.validarEndereco(this.enderecoEdicao, true);
			
			this.endereco.setAipBairrosCepLogradouro(enderecoEdicao
					.getAipBairrosCepLogradouro());
			this.endereco.setAipCidade(this.enderecoEdicao.getAipCidade());
			this.endereco.setAipLogradouro(this.enderecoEdicao
					.getAipLogradouro());
			this.endereco.setAipUf(this.enderecoEdicao.getAipUf());
			this.endereco.setBairro(this.enderecoEdicao.getBairro());
			this.endereco.setCep(this.enderecoEdicao.getCep());
			this.endereco.setCidade(this.enderecoEdicao.getCidade());
			this.endereco.setComplLogradouro(this.enderecoEdicao
					.getComplLogradouro());
			this.endereco.setLogradouro(this.enderecoEdicao.getLogradouro());

			this.endereco.setPadrao(this.enderecoEdicao.isPadrao());
			this.endereco
					.setTipoEndereco(this.enderecoEdicao.getTipoEndereco());
			this.endereco.setNroLogradouro(this.enderecoEdicao
					.getNroLogradouro());

			if (this.endereco.getId() == null) {
				AipEnderecosPacientesId enderecoPacienteId = new AipEnderecosPacientesId();
				enderecoPacienteId.setPacCodigo(this.endereco.getAipPaciente()
						.getCodigo());
				sequencialEndereco++;
				enderecoPacienteId.setSeqp(sequencialEndereco);
				this.endereco.setId(enderecoPacienteId);

			}

			this.cadastroPacienteFacade.organizarValores(this.endereco);
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);

			return false;
		}

		this.cidadeEnderecoCadastrado = null;
		this.cepCadastrado = null;
		this.cepLogradouro = null;
		this.enderecoEdicao = null;
		return true;

	}

	public List<VAipCeps> buscarEnderecosCep(String param) {
		String auxParam = (String) param;

		auxParam = StringUtils.remove(auxParam, ".");
		auxParam = StringUtils.remove(auxParam, "_");
		auxParam = StringUtils.remove(auxParam, "-");
		List<VAipCeps> listaVAipCeps = new ArrayList<VAipCeps>();

		if (CoreUtil.isNumeroInteger(auxParam)) {
			Integer cep = Integer.valueOf(auxParam);

			try {
				listaVAipCeps = cadastroPacienteFacade.pesquisarVAipCeps(cep,null,null);
			} catch (ApplicationBusinessException e) {
				LOG.error("Erro",e);
				this.apresentarExcecaoNegocio(e);
			}
		}

		return listaVAipCeps;
	}
	
	public Integer obterCountEnderecosCep(String param) {
		String auxParam = (String) param;

		auxParam = StringUtils.remove(auxParam, ".");
		auxParam = StringUtils.remove(auxParam, "_");
		auxParam = StringUtils.remove(auxParam, "-");
		Integer count = 0;

		if (CoreUtil.isNumeroInteger(auxParam)) {
			Integer cep = Integer.valueOf(auxParam);

			try {
				count = cadastroPacienteFacade.obterCountVAipCeps(cep,null, null);
			} catch (ApplicationBusinessException e) {
				LOG.error("Erro",e);
				this.apresentarExcecaoNegocio(e);
			}
		}

		return count;
	}

	private void setarVAipCeps(VAipCeps vAipCeps, String origemBuscaCep) {

		this.cepCadastrado = vAipCeps;

		if (vAipCeps != null) {
			this.cidadeEnderecoCadastrado = vAipCeps.getAipCidade();
			vAipCeps.setAipBairrosCepLogradouro(this.cadastroPacienteFacade.obterBairroCepLogradouroPorCepBairroLogradouro(
					vAipCeps.getId().getCep(), vAipCeps.getCodigoBairroLogradouro(), vAipCeps.getCodigoLogradouro()));
			if(vAipCeps.getAipBairrosCepLogradouro() != null){
				habilitaCamposLogradouroLivre = false;
				this.cepLogradouro = vAipCeps;
			}else {
				this.enderecoEdicao.setAipCidade(cidadeEnderecoCadastrado);
				habilitaCamposLogradouroLivre = ORIGEM_LOGRADOURO.equals(origemBuscaCep);
			}
			
		}
	}
	

	public void refazerPesquisaEndereco() {
		this.enderecoNaoLocalizado = Boolean.FALSE;
	}
		
			
	public boolean parametrizaEnderecoNaoCadastrado(){
		
		boolean retorno = Boolean.FALSE;
	
		try {
			AghParametros aghParamFrn = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_LIBERA_CADASTRO_ENDERECO);
			String paramLiberaLogradouro = aghParamFrn.getVlrTexto();
			retorno = "S".equalsIgnoreCase(paramLiberaLogradouro);

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return retorno;
	}
	
	public boolean renderizaEndereco(){
		
			if (parametrizaEnderecoNaoCadastrado() && !enderecoNaoLocalizado || !parametrizaEnderecoNaoCadastrado() && !enderecoNaoLocalizado){
				return false;
			}
				return true;
	}

	public void setarCepSelecionado(VAipCeps vAipCeps) {

		if (vAipCeps != null) {
			setarVAipCeps(vAipCeps, null);

		} else {
			limparCepSelecionado();
		}

	}

	public void setarCepSelecionado() {

		if (this.cepCadastrado != null) {
			setarVAipCeps(cepCadastrado, ORIGEM_CEP);
		}
	}

	public void limparCepSelecionado() {
		this.cidadeEnderecoCadastrado = null;
		this.cepCadastrado = null;
		this.cepLogradouro = null;
		this.habilitaCamposLogradouroLivre = false;
		this.enderecoEdicao.setCep(null);
		this.enderecoEdicao.setLogradouro(null);
		this.enderecoEdicao.setNroLogradouro(null);
		this.enderecoEdicao.setComplLogradouro(null);
		this.enderecoEdicao.setBairro(null);	
		this.enderecoEdicao.setTipoEndereco(null);
		this.enderecoNaoLocalizado = Boolean.FALSE;
	}
	

	public void setarCepLogradouroSelecionado() {
		if (this.cepLogradouro != null) {
			setarVAipCeps(cepLogradouro, ORIGEM_LOGRADOURO);
		}
	}

	public List<AipCidades> pesquisarCidadePorNome(String nome) {
		AghParametros cidadeHuParametro = null;
		AghParametros siglaUfHuParametro = null;
		Integer cidadeHu = null;
		String siglaUfHu = null;
		try {
			cidadeHuParametro = parametroFacade
					.buscarAghParametro(AghuParametrosEnum.P_CODIGO_CIDADE_PADRAO);
			siglaUfHuParametro = parametroFacade
					.buscarAghParametro(AghuParametrosEnum.P_AGHU_UF_SEDE_HU);
			if (cidadeHuParametro != null){
				cidadeHu = Integer.valueOf(cidadeHuParametro.getValor().toString());
			}
			if (siglaUfHuParametro != null){
				siglaUfHu = siglaUfHuParametro.getVlrTexto();
			}
			this.habilitaCamposLogradouroLivre = false;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}		
		
		return this.cadastroPacienteFacade.pesquisarCidadesOrdenadas(nome, cidadeHu, siglaUfHu);
	}
	
	public Long pesquisarCountCidadePorCodigoNome(String paramPesquisa) {
		return cadastrosBasicosPacienteFacade.pesquisarCountCidadePorCodigoNome((String)paramPesquisa);
	}

	/**
	 * remove o endereco selecionado do contexto.
	 */
	public void limparEndereco() {
		this.endereco = new AipEnderecosPacientes();
		this.cepCadastrado = null;
		this.cepLogradouro = null;
		this.cidadeEnderecoCadastrado = null;
		this.habilitaCamposLogradouroLivre = false;
	}

	/**
	 * 
	 * @param cidadeSelecinoada
	 */
	public void setCidadeSelecionada(AipCidades cidadeSelecinoada) {
		this.cidadeEnderecoCadastrado = cidadeSelecinoada;
	}

	/**
	 * @return the endereco
	 */
	public AipEnderecosPacientes getEndereco() {
		return endereco;
	}

	/**
	 * @param endereco
	 *            the endereco to set
	 */

	public void setEndereco(AipEnderecosPacientes endereco) {
		this.endereco = endereco;
	}

	/**
	 * @return the enderecoEdicao
	 */
	public AipEnderecosPacientes getEnderecoEdicao() {
		return enderecoEdicao;
	}

	/**
	 * @param enderecoEdicao
	 *            the enderecoEdicao to set
	 */
	public void setEnderecoEdicao(AipEnderecosPacientes enderecoEdicao) {
		this.enderecoEdicao = enderecoEdicao;
	}

	public List<VAipCeps> listarLogradourosPorTipoTituloNome(String valor) throws ApplicationBusinessException{

		Integer codigoCidade = null;
		List<VAipCeps> lista = null;
		if (this.cidadeEnderecoCadastrado != null) {
			codigoCidade = this.cidadeEnderecoCadastrado.getCodigo();
			String logradouro=null;
			if (valor!=null){
				logradouro=valor.toUpperCase().trim();
			}
			if(logradouro.length() < 3){
			
				/** TODO Ajuste realizado para o HUB/implantação - #31193
				 * 
				 * */ 
				this.apresentarMsgNegocio(Severity.WARN,"Informar no mínimo 3 caracteres em Logradouro.");
				return null;
			}
			lista = this.cadastroPacienteFacade
					.pesquisarVAipCepsPorLogradouroCidade(logradouro, codigoCidade);
			if (lista == null || lista.isEmpty()){
				lista = this.cadastroPacienteFacade
						.pesquisarVAipCepsPorCidade(codigoCidade);
				if(lista != null && cidadeEnderecoCadastrado != null
						&& cidadeEnderecoCadastrado.getCep() != null
						&& cidadeEnderecoCadastrado.getCep() != 0){
					VAipCeps cepCidade = cadastroPacienteFacade.obterVAipCeps(
							cidadeEnderecoCadastrado.getCep(), null, null);
					lista.add(cepCidade);
				}
				enderecoEdicao.setLogradouro(logradouro);
				this.apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_LOGRADOURO_NAO_ENCONTRADO");
			}
		}else {
			this.apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_ERRO_CIDADE_OBRIGATORIA");
		}
		
		return lista;
	}

	public void buscarCepUnico() {
		if(this.cepCadastrado == null
				&& cidadeEnderecoCadastrado != null
				&& cidadeEnderecoCadastrado.getCep() != null
				&& cidadeEnderecoCadastrado.getCep() != 0){
			try {
				VAipCeps cepUnico = cadastroPacienteFacade.obterVAipCeps(
						cidadeEnderecoCadastrado.getCep(), null, null);
				if (cepUnico != null){
					setarCepSelecionado(cepUnico);
				}
			} catch (ApplicationBusinessException e) {
				LOG.error(e.getMessage(),e);
				this.apresentarExcecaoNegocio(e);
			}
		}
	}
	
	public VAipCeps getCepCadastrado() {
		return cepCadastrado;
	}

	public void setCepCadastrado(VAipCeps cepCadastrado) {
		this.cepCadastrado = cepCadastrado;
	}

	public VAipCeps getCepLogradouro() {
		return cepLogradouro;
	}

	public void setCepLogradouro(VAipCeps cepLogradouro) {
		this.cepLogradouro = cepLogradouro;
	}

	public AipCidades getCidadeEnderecoCadastrado() {
		return cidadeEnderecoCadastrado;
	}

	public void setCidadeEnderecoCadastrado(AipCidades cidadeEnderecoCadastrado) {
		this.cidadeEnderecoCadastrado = cidadeEnderecoCadastrado;
	}

	public String getValorBairroEnderecoCadastrado() {
		if (this.cepCadastrado != null && cepCadastrado.getAipBairrosCepLogradouro() != null
				&& cepCadastrado.getAipBairrosCepLogradouro().getAipBairro() != null) {
			return this.cadastroPacienteFacade.recarregarCepLogradouro(cepCadastrado.getAipBairrosCepLogradouro())
					.getAipBairro().getDescricao();
		} else {
			return "";

		}
	}

	public void setOpenedLogradouroCadastrado(Boolean openedLogradouroCadastrado) {
		this.openedLogradouroCadastrado = openedLogradouroCadastrado;
	}

	public Boolean getOpenedLogradouroCadastrado() {
		return openedLogradouroCadastrado;
	}

	public Boolean getEdicao() {
		return edicao;
	}

	public void setEdicao(Boolean edicao) {
		this.edicao = edicao;
	}

	public Boolean getHabilitaCamposLogradouroLivre() {
		return habilitaCamposLogradouroLivre;
	}

	public void setHabilitaCamposLogradouroLivre(
			Boolean habilitaCamposLogradouroLivre) {
		this.habilitaCamposLogradouroLivre = habilitaCamposLogradouroLivre;
	}
	
	public Boolean getEnderecoNaoLocalizado() {
		return enderecoNaoLocalizado;
	}

	public void setEnderecoNaoLocalizado(Boolean enderecoNaoLocalizado) {
		this.enderecoNaoLocalizado = enderecoNaoLocalizado;
	}

	public AipCidades getCidadeEnderecoNaoCadastrado() {
		return cidadeEnderecoNaoCadastrado;
	}

	public void setCidadeEnderecoNaoCadastrado(
			AipCidades cidadeEnderecoNaoCadastrado) {
		this.cidadeEnderecoNaoCadastrado = cidadeEnderecoNaoCadastrado;
	}

	public VAipCeps getCepNaoCadastrado() {
		return cepNaoCadastrado;
	}

	public void setCepNaoCadastrado(VAipCeps cepNaoCadastrado) {
		this.cepNaoCadastrado = cepNaoCadastrado;
	}

}
