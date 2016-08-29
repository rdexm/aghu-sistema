package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioCpfCgcResponsavel;
import br.gov.mec.aghu.dominio.DominioPaisResponsavelConta;
import br.gov.mec.aghu.dominio.DominioResponsavelConta;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.model.AghPaisBcb;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghResponsavel;
import br.gov.mec.aghu.model.AipCidades;
import br.gov.mec.aghu.model.AipEnderecosPacientes;
import br.gov.mec.aghu.model.AipOrgaosEmissores;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.VAipCeps;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.cadastro.business.ICadastroPacienteFacade;
import br.gov.mec.aghu.paciente.cadastrosbasicos.business.ICadastrosBasicosPacienteFacade;


/**
 * Classe responsável por controlar as ações do criação e edição de
 * Responsaveis.
 */



public class ResponsavelContaController  extends ActionController {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1408451175566950412L;

	

	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private ICadastroPacienteFacade cadastroPacienteFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private ICadastrosBasicosPacienteFacade cadastrosBasicosPacienteFacade;
	
	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;

	private AghResponsavel aghResponsavel;
	private AipPacientes paciente;
	private AipPacientes pacienteResponsavel;
	
	private DominioPaisResponsavelConta dominioPaisResponsavel = DominioPaisResponsavelConta.B;
	private DominioResponsavelConta selRespConta;
	
	private String orgaoEmisRg;

	private String voltarPara;
	private Integer seq;
	
	private Boolean isSus;
	
	/*** ENDERECO ***/
	private VAipCeps cepCadastrado;
	private VAipCeps cepLogradouro;	
	private AipCidades cidadeEnderecoCadastrado;
	private Boolean habilitaCamposLogradouroLivre = false;
	private Boolean flagPaciente = false;
	
	private Integer seqAinRep;	
	
	private AghPaisBcb paisBrasil = new AghPaisBcb();
	

	@PostConstruct
	public void init() {
		begin(conversation);		
	}
	
	public void inicializarParametros(){
		
		String  respPrm = getRequestParameter("resp");
		String  pac_codigoStr = getRequestParameter("pac_codigo");
		Integer pac_codigoPrm = null;
		if(!StringUtils.isEmpty(pac_codigoStr)){
		   pac_codigoPrm = Integer.parseInt(pac_codigoStr);
		}
		
		String  seq_ain_repStr = getRequestParameter("seq_ain_rep");
		
		if(!StringUtils.isEmpty(seq_ain_repStr)){
		   this.setSeqAinRep(Integer.parseInt(seq_ain_repStr));
		}
		else {
		   this.setSeqAinRep(null);
		}
		
		
		if(!StringUtils.isEmpty(respPrm)){
			if (respPrm.equalsIgnoreCase("P")){
				this.setSelRespConta(DominioResponsavelConta.P);
			} else {
				this.setSelRespConta(DominioResponsavelConta.O);
			}
		}
		if(pac_codigoPrm != null){
			this.setPaciente(this.pacienteFacade.obterPacientePorCodigo(pac_codigoPrm));
		}		
	}
	
	@SuppressWarnings("PMD.NPathComplexity")
	public void iniciar(){
		
		inicializarParametros();
		
		this.dominioPaisResponsavel = DominioPaisResponsavelConta.B;
		
		
		try {
			paisBrasil = this.examesFacade.obterAghPaisBcb(this.parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_SEQ_PAIS_BRASIL));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		
		if (this.seq == null ){
			this.setAghResponsavel(new AghResponsavel());		
			this.getAghResponsavel().setAghPaisBcb(null);		
			this.getAghResponsavel().setDominioCpfCgc(DominioCpfCgcResponsavel.F);
			this.setOrgaoEmisRg(orgaoEmisRg);
			if(this.getAghResponsavel().getAghPaisBcb() != null && !this.getAghResponsavel().getAghPaisBcb().equals(paisBrasil)){
				this.dominioPaisResponsavel = DominioPaisResponsavelConta.E;
			}
			this.limparEndereco();
			
		} 
		else {
			this.setAghResponsavel(this.cadastrosApoioExamesFacade.obterResponsavelPorSeq(this.getSeq()));		
			this.setSelRespConta(this.getAghResponsavel().getAipPaciente() != null ? DominioResponsavelConta.P : DominioResponsavelConta.O);
		}
		
		this.setFlagPaciente(DominioResponsavelConta.P.equals(this.getSelRespConta()));
		
		if (DominioResponsavelConta.O.equals(this.getSelRespConta()) &&
			this.getSeq() != null) {
			
			this.setAghResponsavel(this.cadastrosApoioExamesFacade.obterResponsavelPorSeq(this.getSeq()));
						
			
			if(this.getAghResponsavel().getAghPaisBcb() != null && !this.getAghResponsavel().getAghPaisBcb().equals(paisBrasil)){
				this.dominioPaisResponsavel = DominioPaisResponsavelConta.E;
			}
			
			if (this.getAghResponsavel().getAipBairrosCepLogradouro() != null) {
				try {
					this.setCepCadastrado(cadastroPacienteFacade.obterVAipCeps(
							this.getAghResponsavel().getAipBairrosCepLogradouro().getId().getCloCep(),
							this.getAghResponsavel().getAipBairrosCepLogradouro().getId().getCloLgrCodigo(),
							this.getAghResponsavel().getAipBairrosCepLogradouro().getId().getBaiCodigo()));
				} catch (ApplicationBusinessException e) {
					apresentarExcecaoNegocio(e);
				}
			}
			else {
			    try {
					this.setCepCadastrado(cadastroPacienteFacade.obterVAipCeps(
							this.getAghResponsavel().getCep(), null, null));
				} catch (ApplicationBusinessException e) {
					apresentarExcecaoNegocio(e);
				}
			}
			
			if (this.getCepCadastrado() != null){
				this.cidadeEnderecoCadastrado = this.getCepCadastrado().getAipCidade();
				if (this.getCepCadastrado().getUfSigla() != null && this.cidadeEnderecoCadastrado.getAipUf() == null){
					this.cidadeEnderecoCadastrado.setAipUf(cadastrosBasicosPacienteFacade.obterUF(this.getCepCadastrado().getUfSigla()));
				}
			}			
			
			this.setCepLogradouro(getCepCadastrado());		
			
		}
		else if (DominioResponsavelConta.P.equals(this.getSelRespConta()) && this.getPaciente() != null){
			
			 AghResponsavel resp;
			 AipPacientes pacienteInit;
			 
			if(this.getSeq() == null){
				resp =  cadastrosApoioExamesFacade.obterResponsavelPorPaciente(paciente);
				pacienteInit = this.paciente;
				this.setPacienteResponsavel(null);
			}
			else {
				resp = this.cadastrosApoioExamesFacade.obterResponsavelPorSeq(this.getSeq());
				this.setPacienteResponsavel(this.pacienteFacade.obterPacientePorCodigo(resp.getAipPaciente().getCodigo()));
				pacienteInit = this.getPacienteResponsavel();
			}
			
			if (resp != null){
				this.setAghResponsavel(resp);
			}
			
			obterDadosPaciente(pacienteInit);
		}
			
	
	}
	
	public void obterDadosPaciente(AipPacientes pacienteParametro){
		this.getAghResponsavel().setNome(pacienteParametro.getNome());
		this.getAghResponsavel().setDtNascimento(pacienteParametro.getDtNascimento());
		this.getAghResponsavel().setNomeMae(pacienteParametro.getNomeMae());
		this.getAghResponsavel().setDddFone(pacienteParametro.getDddFoneResidencial());
		this.getAghResponsavel().setFone(pacienteParametro.getFoneResidencial());
		this.setDominioPaisResponsavel(DominioPaisResponsavelConta.B);
		this.getAghResponsavel().setCpfCgc(pacienteParametro.getCpf());
		this.getAghResponsavel().setRg(pacienteParametro.getRg());
		
		if (pacienteParametro.getCpf() != null){
			this.getAghResponsavel().setDominioCpfCgc(DominioCpfCgcResponsavel.F);
		}
					
		if (pacienteParametro.getAipPacientesDadosCns() != null){
			if(pacienteParametro.getAipPacientesDadosCns().getAipOrgaosEmissor() != null){
		       this.getAghResponsavel().setAipOrgaosEmissor(this.cadastroPacienteFacade.obterOrgaoEmissorPorCodigo(pacienteParametro.getAipPacientesDadosCns().getAipOrgaosEmissor().getCodigo()));
			}
		}
		
		this.setOrgaoEmisRg(pacienteParametro.getOrgaoEmisRg());
				
		this.getAghResponsavel().setRegNascimento(pacienteParametro.getRegNascimento());
		this.getAghResponsavel().setPisPasep(pacienteParametro.getNumeroPis());
		
		AipEnderecosPacientes endereco = this.cadastroPacienteFacade.obterEnderecoResidencialPadraoPaciente(pacienteParametro);
		
		this.getAghResponsavel().setAipBairrosCepLogradouro(endereco.getAipBairrosCepLogradouro());
		this.getAghResponsavel().setAipCidade(endereco.getAipCidade());
		this.getAghResponsavel().setAipLogradouro(endereco.getAipLogradouro());
		this.getAghResponsavel().setNroLogradouro(endereco.getNroLogradouro());
		this.getAghResponsavel().setComplLogradouro(endereco.getComplLogradouro());
		this.getAghResponsavel().setCidade(endereco.getCidade());
		this.getAghResponsavel().setAipUf(endereco.getAipUf());
		this.getAghResponsavel().setCep(endereco.getCep());
		this.getAghResponsavel().setLogradouro(endereco.getLogradouro());
		this.getAghResponsavel().setBairro(endereco.getBairro());
		
		if (endereco.getAipBairrosCepLogradouro() != null) {
			try {
				this.setCepCadastrado(cadastroPacienteFacade.obterVAipCeps(
						endereco.getAipBairrosCepLogradouro().getId().getCloCep(),
						endereco.getAipBairrosCepLogradouro().getId().getCloLgrCodigo(),
						endereco.getAipBairrosCepLogradouro().getId().getBaiCodigo()));
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}
		else {
		    try {
				this.setCepCadastrado(cadastroPacienteFacade.obterVAipCeps(
					endereco.getCep(), null, null));
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}
		
		if (this.getCepCadastrado() != null){
			this.cidadeEnderecoCadastrado = this.getCepCadastrado().getAipCidade();
			if (this.getCepCadastrado().getUfSigla() != null && this.cidadeEnderecoCadastrado.getAipUf() == null){
				this.cidadeEnderecoCadastrado.setAipUf(cadastrosBasicosPacienteFacade.obterUF(this.getCepCadastrado().getUfSigla()));
			}
		}			
		
		this.setCepLogradouro(getCepCadastrado());	
	}
	
	
	/**
	 * Método que realiza a ação do botão confirmar na tela de cadastro de
	 * responsavel
	 */
	public String confirmar() {
		
		try {
			boolean create = this.aghResponsavel.getSeq() == null;
			
 			if(this.cepCadastrado != null) {
				this.cepCadastrado.setAipBairrosCepLogradouro(
						this.cadastroPacienteFacade.obterBairroCepLogradouroPorCepBairroLogradouro(
								this.cepCadastrado.getId().getCep(), 
								this.cepCadastrado.getCodigoBairroLogradouro(), 
								this.cepCadastrado.getCodigoLogradouro()));
				
				if(this.cepCadastrado.getAipBairrosCepLogradouro() != null){
					this.aghResponsavel.setAipBairrosCepLogradouro(this.cepCadastrado.getAipBairrosCepLogradouro());
					this.aghResponsavel.setCep(null);					
				}else{
					this.aghResponsavel.setCep(this.getCepCadastrado().getId().getCep());
					this.aghResponsavel.setAipBairrosCepLogradouro(null);
					
				}
			}
			if(this.cidadeEnderecoCadastrado != null ){
				cidadeEnderecoCadastrado = this.cadastrosBasicosPacienteFacade.obterCidade(cidadeEnderecoCadastrado.getCodigo());
				this.aghResponsavel.setAipCidade(cidadeEnderecoCadastrado);
				this.aghResponsavel.setAipUf(cidadeEnderecoCadastrado.getAipUf());
			}
			if(this.cepLogradouro != null && this.cepLogradouro.getAipLogradouro() != null) {
				this.cepLogradouro.setAipLogradouro(this.cadastrosBasicosPacienteFacade.obterLogradouroPorCodigo(this.cepLogradouro.getAipLogradouro().getCodigo()));
				this.aghResponsavel.setAipLogradouro(this.cepLogradouro.getAipLogradouro());
			}
			if (DominioPaisResponsavelConta.B.equals(this.dominioPaisResponsavel)){
				this.aghResponsavel.setAghPaisBcb(this.examesFacade.obterAghPaisBcb(this.parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_SEQ_PAIS_BRASIL)));
			}
			
			if (this.flagPaciente){				
				AghResponsavel responsavelTemp = new AghResponsavel();				
				if (this.aghResponsavel.getSeq() != null){
					responsavelTemp = this.cadastrosApoioExamesFacade.obterResponsavelPorSeq(this.aghResponsavel.getSeq());
				}
				responsavelTemp.setEmail(this.aghResponsavel.getEmail());
				if (this.getPacienteResponsavel() == null){
				    responsavelTemp.setAipPaciente(this.paciente);
				    this.getAghResponsavel().setAipPaciente(this.paciente);
				}
				this.cadastrosApoioExamesFacade.salvarResponsavel(responsavelTemp, seqAinRep);
				this.getAghResponsavel().setSeq(responsavelTemp.getSeq());
			}
			else {
			    this.cadastrosApoioExamesFacade.salvarResponsavel(this.aghResponsavel, seqAinRep);
			}
			
			if (create) {
				apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_CRIACAO_RESPONSAVEL_PAC");
			} else {
				apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_EDICAO_RESPONSAVEL_PAC");
			}
			
			if (seqAinRep == null){
				return this.voltarPara;
			}
		
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}  
		
		return null;
	}

	/**
	 * Método que realiza a ação do botão cancelar na tela de cadastro de
	 * tipo de unidade funcional
	 */
	public String cancelar() {		
		this.setAghResponsavel(new AghResponsavel());
		return this.voltarPara;
	}

	
	public Boolean habilitarSuggPaises(){
		return DominioPaisResponsavelConta.B.equals(dominioPaisResponsavel);
	}
	
	public Boolean mostrarCpfCgc(){
		return DominioCpfCgcResponsavel.F.equals(this.aghResponsavel.getDominioCpfCgc());
	}
	
	public void limparDadosPais(){
		if(DominioPaisResponsavelConta.E.equals(dominioPaisResponsavel)){
			this.aghResponsavel.setCpfCgc(null);
			this.aghResponsavel.setDominioCpfCgc(null);
			this.aghResponsavel.setRg(null);
			this.aghResponsavel.setAipOrgaosEmissor(null);
			this.aghResponsavel.setPisPasep(null);
			this.aghResponsavel.setRegNascimento(null);
		}
		else {
			this.aghResponsavel.setNroDocExterior(null);
			this.aghResponsavel.setAghPaisBcb(null);
			
		}
		this.limparEndereco();
		
	}
	
	public List<AipOrgaosEmissores> pesquisarOrgaoEmissorPorCodigoDescricao(String paramPesquisa) {
		return this.cadastroPacienteFacade.pesquisarOrgaoEmissorPorCodigoDescricao(paramPesquisa);
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
				this.aghResponsavel.setLogradouro(logradouro);
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
				this.apresentarExcecaoNegocio(e);
			}
		}
	}
	
	// METODOS DE ENDEREÇO
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
				this.apresentarExcecaoNegocio(e);
			}
		}

		return count;
	}

	private void setarVAipCeps(VAipCeps vAipCeps) {

		this.cepCadastrado = vAipCeps;

		if (vAipCeps != null) {
			this.cidadeEnderecoCadastrado = vAipCeps.getAipCidade();
			vAipCeps.setAipBairrosCepLogradouro(this.cadastroPacienteFacade.obterBairroCepLogradouroPorCepBairroLogradouro(
					vAipCeps.getId().getCep(), vAipCeps.getCodigoBairroLogradouro(), vAipCeps.getCodigoLogradouro()));
			
			if(this.aghResponsavel != null){
			   this.aghResponsavel.setUfSiglaExterior(vAipCeps.getUfSigla());
			}
			
			if(vAipCeps.getAipBairrosCepLogradouro() != null){
				habilitaCamposLogradouroLivre = !DominioPaisResponsavelConta.B.equals(dominioPaisResponsavel);
				this.cepLogradouro = vAipCeps;
				
			}else {				
				habilitaCamposLogradouroLivre = !vAipCeps.getTipo().equalsIgnoreCase("Logradouro");
			}
			
		}
	}

	public void setarCepSelecionado(VAipCeps vAipCeps) {

		if (vAipCeps != null) {
			setarVAipCeps(vAipCeps);

		} else {
			limparCepSelecionado();
		}

	}

	public void setarCepSelecionado() {

		if (this.cepCadastrado != null) {
			setarVAipCeps(cepCadastrado);
		}
	}

	public void limparCepSelecionado() {
		this.cidadeEnderecoCadastrado = null;
		this.cepCadastrado = null;
		this.cepLogradouro = null;
		this.habilitaCamposLogradouroLivre = !DominioPaisResponsavelConta.B.equals(dominioPaisResponsavel);
		this.aghResponsavel.setAipUf(null);
		this.aghResponsavel.setUfSiglaExterior(null);
	}

	public void setarCepLogradouroSelecionado() {
		if (this.cepLogradouro != null) {
			setarVAipCeps(cepLogradouro);
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
			this.habilitaCamposLogradouroLivre = !DominioPaisResponsavelConta.B.equals(dominioPaisResponsavel);
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
		this.cepCadastrado = null;		
		this.cepLogradouro = null;
		this.cidadeEnderecoCadastrado = null;
		this.habilitaCamposLogradouroLivre = !DominioPaisResponsavelConta.B.equals(dominioPaisResponsavel);
		this.aghResponsavel.setAipUf(null);
		this.aghResponsavel.setUfSiglaExterior(null);
		this.aghResponsavel.setLogradouro(null);
		this.aghResponsavel.setCep(null);
		this.aghResponsavel.setBairro(null);
		this.aghResponsavel.setComplLogradouro(null);
		this.aghResponsavel.setNroLogradouro(null);
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
	
	// ### GETs e SETs ###	

	
	public AghResponsavel getAghResponsavel() {
		return aghResponsavel;
	}

	public void setAghResponsavel(AghResponsavel aghResponsavel) {
		this.aghResponsavel = aghResponsavel;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public List<AghPaisBcb> listarPaisesBcb(String parametro){
		return this.examesFacade.listarPaisesBcb(parametro, this.paisBrasil);
	}

	public DominioPaisResponsavelConta getDominioPaisResponsavel() {
		return dominioPaisResponsavel;
	}

	public void setDominioPaisResponsavel(
			DominioPaisResponsavelConta dominioPaisResponsavel) {
		this.dominioPaisResponsavel = dominioPaisResponsavel;
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

	public Boolean getHabilitaCamposLogradouroLivre() {
		return habilitaCamposLogradouroLivre;
	}

	public void setHabilitaCamposLogradouroLivre(
			Boolean habilitaCamposLogradouroLivre) {
		this.habilitaCamposLogradouroLivre = habilitaCamposLogradouroLivre;
	}
	
	public DominioResponsavelConta getSelRespConta() {
		return selRespConta;
	}

	public void setSelRespConta(DominioResponsavelConta selRespConta) {
		this.selRespConta = selRespConta;
	}

	public String getOrgaoEmisRg() {
		return orgaoEmisRg;
	}

	public void setOrgaoEmisRg(String orgaoEmisRg) {
		this.orgaoEmisRg = orgaoEmisRg;
	}

	public Boolean getFlagPaciente() {
		return flagPaciente;
	}

	public void setFlagPaciente(Boolean flagPaciente) {
		this.flagPaciente = flagPaciente;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
	public Integer getSeqAinRep() {
		return seqAinRep;
	}

	public void setSeqAinRep(Integer seqAinRep) {
		this.seqAinRep = seqAinRep;
	}

	public AipPacientes getPacienteResponsavel() {
		return pacienteResponsavel;
	}

	public void setPacienteResponsavel(AipPacientes pacienteResponsavel) {
		this.pacienteResponsavel = pacienteResponsavel;
	}

	public AghPaisBcb getPaisBrasil() {
		return paisBrasil;
	}

	public void setPaisBrasil(AghPaisBcb paisBrasil) {
		this.paisBrasil = paisBrasil;
	}

	public Boolean getIsSus() {
		return isSus;
	}

	public void setIsSus(Boolean isSus) {
		this.isSus = isSus;
	}
}
